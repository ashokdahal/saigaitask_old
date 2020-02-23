/**
 * メモ編集ウィンドウ
 * @require SaigaiTask/Map/view.js
 * @returns {SaigaiTask.Map.view.ContentsFormWindow}
 */
SaigaiTask.Map.view.RakugakiWindow = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.RakugakiWindow.prototype = {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * @type {SaigaiTask.Map.control.RakugakiControl}
	 */
	rakugakiControl: null,

	/**
	 * @type {Ext.window.Window}
	 */
	window: null,

	/**
	 * @type {SaigaiTask.Map.view.DrawToolbar}
	 */
	drawToolbar: null,

	/**
	 * メモ編集ウィンドウを表示します.
	 * @param option.stmap 地図オブジェクト
	 * @param option.layerInfo レイヤ情報
	 * @param option.lonlat 経緯度(地図の投影法で)
	 * @param option.fid フィーチャID(更新時の場合指定)
	 * @param option.feature フィーチャ(EPSG:4326)(更新時の場合指定)
	 * @param option.content 登録情報(更新時の場合指定)
	 * @param option.drawGeometryOnly ジオメトリ描画モード（描画のみで登録処理しない）
	 */
	initialize: function(option) {
		var me = this;
		me.stmap = option.stmap;
		me.rakugakiControl = option.rakugakiControl;

		me.initDrawToolbar();
	},
	
	/**
	 * 描画用ツールバーを初期化する
	 */
	initDrawToolbar: function() {
		var me = this;
		var stmap = me.stmap;

		// 描画ツールバーを２段目に表示する
		var headerToolbar = null;
		if(!!me.headerToolbar) {
			headerToolbar = me.headerToolbar;
		}
		else {
			// メモ描画用ツールバーを新規作成
			// ヘッダツルーバー下に非表示状態で追加する
			headerToolbar = me.headerToolbar = {};
			var headerTbar = Ext.create("Ext.toolbar.Toolbar", {
				docked: "top",
				align: "right"
				//items: headerTbarItems
			});
			headerToolbar.tbar = headerTbar;
			headerTbar.hide();

			// メインパネルにツールバー追加
			var headerToolbar = me.headerToolbar;
			map.components.mainpanel.mapPanel.panel.addDocked(headerToolbar.tbar);
		}

		// 描画ツールバーを初期化
		var drawToolbar = me.drawToolbar = new SaigaiTask.Map.view.DrawToolbar(map, {
			init: false,
			//geomType: "MULTILINESTRING",
			//drawPoint: true,
			//drawPolygon: true,
			//drawLine: true,
			drawFreeLine: true,
			drawText: true,
			// 線の色
			strokeColor: true,
			// 線の太さ
			strokeWidth: true,
			fillColor: true,
			fontColor: true,
			fontSize: true,
			modifyFeature: false,
			dragFeature: false,
			selectFeature: false,
			selectDragFeature: true,
			selectRangeFeature: true,
			removeSelectedFeature: true,
			removeFeature: true,
			// 操作を元に戻す
			undo: true,
			// 操作をやり直す
			redo: true,
			maxDrawNum: Number.MAX_VALUE
		});

		// snap無効
		drawToolbar.drawLayer.snapControl.snap.deactivate();

		// 描画ツールバーを地図画面のヘッダーに追加
		//drawToolbar.tbar.hide();
		//headerToolbar.tbar.add(drawToolbar.tbar);
	},

	initEditWindow: function() {
		var me = this;
		var stmap = me.stmap;
		var drawToolbar = me.drawToolbar;

		// Toolbar
		var dockedItems = [];
		{
			var toolbarLayouts = [
				["drawFreeLine", "strokeColor", "strokeWidth"],
				["drawText", "fontColor", "fontSize", "fillColor"],
				["selectDragFeature", "selectRangeFeature", "removeSelectedFeature", "removeFeature", "undo", "redo"]
			];
			
			for(var toolbarLayoutsIdx in toolbarLayouts) {
				var toolbarItems = [];
				var drawToolbarTypes = toolbarLayouts[toolbarLayoutsIdx];
				for(var key in drawToolbarTypes) {
					var drawToolbarType = drawToolbarTypes[key];
					
					if(drawToolbarType=="-") {
						toolbarItems.push("-");
						continue;
					}
					
					// find
					var tbarItems = drawToolbar.tbarItems;
					for(var idx in tbarItems) {
						var item = tbarItems[idx];
						// 区切り文字の場合
						if(item=="-") continue;
						else {
							if(item.drawToolbarType==drawToolbarType) {
								toolbarItems.push(item);
								break;
							}
						}
					}
				}

				dockedItems.push({
					xtype: "toolbar",
					dock: "top",
					items: toolbarItems
				});
			}
		}

		// フォームのウィンドウを生成
		var win = null;
		win = me.window = Ext.create('Ext.window.Window', {
			title: lang.__("Memo")+lang.__("Edit"),
			width: 420, //height: 600,
			maxWidth: document.body.clientWidth,
			maxHeight: document.body.clientHeight,
			collapsible: true,
			//tbar: tbar,
			dockedItems: dockedItems,
			layout: 'fit',
			//items: drawGeometryOnly ? null : formPanel,
			closeAction: 'hide',
			buttons: [{
				text: lang.__('Registration'),
				textAlign: "left",
				icon: stmap.icon.getURL("editIconURL"),
				handler: function(){
					me.rakugakiControl.save();
				}
			}, {
				text: lang.__('Cancel'),
				handler: function(){
					//me.rakugakiControl.cancel();
					win.close();
				}
			}],
			listeners: {
				hide: function() {
					me.rakugakiControl.cancel();
				}
			}
		});
	},

	showEditWindow: function() {
		var me = this;
		var stmap = me.stmap;

		var win = me.window;
		if(!win) {
			me.initEditWindow();
			win = me.window;
		}
		
		// ウィンドウ表示
		win.show();
		// 右上に表示
		//win.alignTo(document, "tr", [-win.getWidth(), 0]);
		// トップ中央に表示
		win.alignTo(document, "t", [-win.getWidth()/2, 0]);
		// 初期状態は選択ボタンにする
		me.drawToolbar.buttons.selectDragFeatureButton.toggle(true);
		me.drawToolbar.drawLayer.setSelectDragFeatureControlActivation(true);
	}
}