/**
 * メモレイヤを管理するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.RakugakiControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	mainpanel: null,

	kmlFormat: null,

	/**
	 * 編集移行用ツールバー
	 */
	tbar: null,

	/**
	 * 描画ツールバー
	 * @type {SaigaiTask.Map.view.DrawToolbar}
	 */
	drawToolbar: null,

	/**
	 * メモボタン
	 */
	rakugakiButton: null,

	/**
	 * ボタン
	 */
	buttons: null,
	
	/**
	 * メモ編集ウィンドウ
	 * @type {SaigaiTask.Map.view.RakugakiWindow}
	 */
	rakugakiWindow: null,

	initialize: function(stmap) {
		var me = this;
		me.stmap = stmap;

		// KMLフォーマットの初期化
		var wgs84 = new OpenLayers.Projection("EPSG:4326");
		var internalProjection = wgs84;
		if(!!stmap.epsg) internalProjection = new OpenLayers.Projection("EPSG:"+stmap.epsg);
		me.kmlFormat = new OpenLayers.Format.KMLStyle({
			extractStyles: true,
			internalProjection: internalProjection
		});

		// メモボタン
		me.rakugakiButton = Ext.create("Ext.Button", {
			text: lang.__("Memo"),
			icon: stmap.icon.getURL("mapEditIconURL"),
			tooltip: lang.__("Memo layer will be displayed or hided."),
			enableToggle: true,
			handler: function(button, evt) {
				if(button.pressed) {
					// メモを表示
					me.show();
				}
				else {
					if(me.hasUnsaveEdit()) {
						Ext.MessageBox.confirm(lang.__("Hide memo"), lang.__("There is editing memo.<br/>Are you sure to discard the changes?"), function(btn, text){
							if(btn=='yes') {
								// メモを非表示
								me.hide();
							}
							else {
								// キャンセルの場合はボタンを戻す
								button.toggle(true);
								return;
							}
						});
					}
					else {
						// メモを非表示
						me.hide();
					}
				}
			}
		});

		//
		// 編集移行用ボタン
		//
		me.buttons = {};
		var buttonHandler = function(type) {
			me.reload(function() {
				me.startEdit(type);
			});
		};
//		me.buttons.register = Ext.create("Ext.Button", {
//			text: "新規",
//			icon: stmap.icon.getURL("addIconURL"),
//			tooltip: "新しくメモします。",
//			enableToggle: true
//		});
		me.buttons.edit = Ext.create("Ext.Button", {
			text: lang.__("Edit"),
			icon: stmap.icon.getURL("editIconURL"),
			tooltip: lang.__("Edit memo"),
			enableToggle: true
		});
		var types = ["register", "edit"];
		for(var idx in types) {
			var type = types[idx];
			(function(type){
				var button = me.buttons[type];
				if(!!button) {
					button.on("click", function(button, status){
						if(button.pressed) {
							buttonHandler(type);
						}
						// ボタン押下を戻す場合
						else {
							// 編集中の場合は確認する
							if(me.hasUnsaveEdit()) {
								Ext.MessageBox.confirm(lang.__("Cancel memo {0}", button.text), lang.__("There is editing memo.<br/>Are you sure to discard the changes?"), function(btn, text){
									if(btn=='yes') {
										me.cancelEdit();
									}
									else {
										// キャンセルの場合はボタンを戻す
										button.toggle(true);
										return;
									}
								});
							}
							else {
								me.stopEdit();
							}
						}
					});
				}
			})(type);
		}
		me.tbar = Ext.create("Ext.toolbar.Toolbar", {
			hidden: true,
			items: [me.buttons.register, me.buttons.edit]
		});

		// SaigaiTask.Map.view.MainPanel の初期化時にメモボタンを追加する
		stmap.events.on({
			"epsgchanged": function(args) {
				// 地図の投影法が変わったら、KMLFormatも連動して変更する
				var internalProjection = wgs84;
				if(!!stmap.epsg) internalProjection = new OpenLayers.Projection("EPSG:"+stmap.epsg);
				me.kmlFormat.internalProjection = internalProjection;
			},
			"initmainpanel": function(args) {
				me.mainpanel = args.mainpanel;
				// メモボタンを追加
				me.mainpanel.headerToolbar.tbar.add("->", me.rakugakiButton, me.tbar);
			}
		});
	},

	/**
	 * メモの表示
	 */
	show: function() {
		var me = this;
		me.tbar.show();
		// 描画ツールバーがなければ初期化
		if(me.drawToolbar==null) {
			me.rakugakiWindow = new SaigaiTask.Map.view.RakugakiWindow({
				stmap: me.stmap,
				rakugakiControl: me
			});
			me.drawToolbar = me.rakugakiWindow.drawToolbar;
		}
		// レイヤ表示
		me.drawToolbar.drawLayer.layer.setVisibility(true);
		me.reload();
	},
	/**
	 * メモの非表示
	 */
	hide: function() {
		var me = this;
		// 編集中なら
		if(me.hasUnsaveEdit()) {
			// 編集を中止
			me.cancelEdit();
		}
		else {
			// もし、編集していれば終了する
			me.stopEdit();
		}
		// レイヤ非表示
		me.drawToolbar.drawLayer.layer.setVisibility(false);
		// 編集移行用ツールバーを非表示
		me.tbar.hide();
	},


	/**
	 * 編集の開始
	 */
	startEdit: function(type) {
		console.debug("startEdit: "+type);
		var me = this;
		var stmap = me.stmap;

		var onlockfail = function(msg) {
			alert(msg);
			me.buttons[type].toggle();
		}

		// lock
		stmap.api.rakugaki.lock()
		.fail(function() {
			onlockfail(lang.__("Failed to edit lock."));
		})
		.done(function(result) {
			// ロックフラグを取得
			var lock = false;
			if(!!result) lock = !!result.lock;

			// ロック失敗処理
			if(lock==false) {
				var lockInfo = result.lockInfo;
				var username ="";
				if(!!lockInfo.groupInfo && !!lockInfo.groupInfo.name) username="("+lockInfo.groupInfo.name+")";
				if(!!lockInfo.unitInfo && !!lockInfo.unitInfo.name) username="("+lockInfo.unitInfo.name+")";
				onlockfail(lang.__("User {0} is editing.", username));
				return;
			}

			// lockを定期的にリクエストし、ロック時間を延長する
			me.lockTimer = setInterval(function() {
				stmap.api.rakugaki.lock();
			}, 10*1000)

			// 制御ボタンの更新
			for(var btnType in me.buttons) {
				var btn = me.buttons[btnType];
				switch(btnType) {
				// 保存・中止ボタンは表示する
				case "save":
				case "cancel":
					btn.show();
					break;
				default:
					// 押してないボタンは無効
					if(type!=btnType) {
						btn.setDisabled(true);
					}
					break;
				}
			}

			// 描画
			var drawToolbar = me.drawToolbar;
			if(drawToolbar!=null) {
				// メモ編集ウィンドウを表示
				me.rakugakiWindow.showEditWindow();
				
				// 履歴の記録を開始
				drawToolbar.drawLayer.historyControl.clear();
				drawToolbar.drawLayer.historyControl.startHistory();
			}
		});

	},
	/**
	 * 編集の終了
	 */
	stopEdit: function() {
		var me = this;
		var stmap = me.stmap;

		// 編集ロックの延長リクエストを停止
		clearInterval(me.lockTimer);
		// ロック解除
		stmap.api.rakugaki.unlock();

		// 制御ボタンの更新
		for(var btnType in me.buttons) {
			var btn = me.buttons[btnType];
			switch(btnType) {
			// 保存・中止ボタンは表示する
			case "save":
			case "cancel":
				btn.hide();
				break;
			default:
				// すべてのボタンを表示
				btn.show();
				btn.setDisabled(false);
				btn.toggle(false);
				break;
			}
		}

		// 描画
		var drawToolbar = me.drawToolbar;
		if(drawToolbar!=null) {
			// 履歴の記録を停止
			drawToolbar.drawLayer.historyControl.clear();
			drawToolbar.drawLayer.historyControl.stopHistory();

			// メモ編集ウィンドウを非表示
			if(me.rakugakiWindow.window!=null) {
				me.rakugakiWindow.window.close();
			}

			// 地物の削除
			//drawToolbar.drawLayer.layer.removeAllFeatures();
			// 描画コントロール解除
			drawToolbar.drawLayer.deactivateCurrentDrawControl();
			stmap.setNavigationControlActivation(true);
		}
	},
	/**
	 * 編集途中かどうか
	 */
	hasUnsaveEdit: function() {
		try {
			return 0<this.drawToolbar.drawLayer.historyControl.stack.length;
		} catch(e) {
			return false;
		}
	},
	/**
	 * 編集のキャンセル
	 */
	cancelEdit: function() {
		var me = this;

		me.stopEdit();

		// 編集中止
		me.reload();
	},
	/**
	 * 編集の保存
	 */
	saveEdit: function(callback) {
		var me = this;
		var stmap = me.stmap;
		var kml = me.getEditKML();
		stmap.api.rakugaki.save(kml);
		me.stopEdit();
		if(typeof callback=="function") {
			callback();
		}
	},
	
	save: function() {
		var me = this;
		var stmap = me.stmap;
		// 編集していない場合
		if(!me.hasUnsaveEdit()) {
			Ext.MessageBox.alert(lang.__("Save memo"), lang.__("Nothing is changed."));
		}
		else {
			// KMLの取得
			Ext.MessageBox.confirm(lang.__("Save memo"), lang.__("Are you sure to save memo?"), function(btn, text){
				if(btn=='yes') {
					// 保存実行
					me.saveEdit(function() {
						Ext.MessageBox.alert(lang.__("Save memo"), lang.__("Saved."));
					});
				}
			});
		}
	},
	cancel: function() {
		var me = this;
		var stmap = me.stmap;
		// 編集中の場合は確認してから編集モードを終了する
		if(me.hasUnsaveEdit()) {
			Ext.MessageBox.confirm(lang.__("Cancel memo"), lang.__("There is memo that has not been saved.<br/>It will discard the changes and cancel editing memo. <br/> Are you sure?"), function(btn, text){
				if(btn=='yes') {
					me.cancelEdit();
				}
			});
		}
		else {
			me.cancelEdit();
		}
	},
	
	/**
	 * 編集KMLを取得する
	 */
	getEditKML: function() {
		var me = this;
		var drawToolbar = me.drawToolbar;
		var features = drawToolbar.drawLayer.layer.features;
		var kml = me.kmlFormat.write(features);
		return kml;
	},

	/**
	 * 再読み込み処理
	 */
	reload: function(callback) {
		var me = this;
		var stmap = me.stmap;
		var drawToolbar = me.drawToolbar;

		// KMLダウンロード
		stmap.api.rakugaki.download({
			async: true,
			success: function(kml) {
				drawToolbar.drawLayer.layer.removeAllFeatures();
				if(kml!=null) {
					var kmlString = new XMLSerializer().serializeToString(kml);
					var features = me.kmlFormat.read(kmlString);
					drawToolbar.drawLayer.layer.addFeatures(features);
				}
				if(typeof callback=="function") {
					callback();
				}
			},
			error: function() {
				ExtmMessageBox.alert(lang.__("Reload memo"), lang.__("Unable to get memo from server."));
			}
		});
	}
});
