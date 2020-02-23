/**
 * 地物の複数選択コントロール.
 * 検索可能な登録情報レイヤが１つだけの場合、
 * 地図をSHIFTキーを押しながらクリックすると
 * 検索結果の地物を選択状態にします.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.SelectFeatureControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * APIProperty: events {<OpenLayers.Events>}
	 *
	 * Supported map event types (in addition to those from
	 * <OpenLayers.Layer.events>): layoutloaded- レイアウト読み込み後にトリガーする.
	 */

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * 選択状態の地物の強調表示レイヤ
	 * @type {OpenLayaers.Layer.Vector}
	 */
	layer: null,

	/**
	 * 選択対象のレイヤ情報
	 * @type {SaigaiTask.Map.LayerInfo}
	 */
	layerInfo: null,

	/**
	 * 選択済みフィーチャデータ
	 * @type {Array<SaigaiTask.Map.control.SelectFeatureControl.FeatureData>}
	 */
	selectedFeatureDatas: null,

	/**
	 * 選択リストウィンドウ
	 * @type {SaigaiTask.Map.view.SelectFeatureWindow}
	 */
	window: null,

	/**
	 * 一括変更クラス
	 * @type {SaigaiTask.Edit.Slimer}
	 */
	slimer: null,

	initialize: function(stmap, options) {
		var me = this;
		me.stmap = stmap;
		me.selectedFeatureDatas = [];

		// 表示状態をサーバに保存するイベントの初期化.
		var handler = function(evt){
			me.saveLayout();
		};
		stmap.events.on({
			"clicksearch": function(args, evt) {
				me.onClickSearch(args);
			}
		});

		// 強調表示レイヤの登録
		me.layer = new OpenLayers.Layer.Vector("Selected Feature Layer", {
			style: {
				strokeColor: "#FFFF00",
				strokeWidth: 2,
				//strokeDashstyle: 'dash',
				fillColor: "white",
				fillOpacity: 0.5,
				// 図の設定
				externalGraphic: stmap.icon.getURL("selectingIconURL"),
				graphicWidth: 19,
				graphicHeight: 32,
				graphicXOffset: -9,
				graphicYOffset: -32,
				graphicOpacity: 1
			}
		});
		stmap.addLayer(me.layer);

		OpenLayers.Control.prototype.initialize.apply(this, [options]);
	},

	/** 
	 * 地図画面クリックによる検索で
	 * 結果が得られた時のハンドラ
	 */
	onClickSearch: function(args) {
		var me = this;
		var stmap = me.stmap;

		var layerIds = args.layerIds;
		var center = args.center;
		var bbox = args.bbox;
		var option = args.option;
		var evt = !!option ? option.evt : null;
		var result = args.result;

		// 結果が 0 ならなにもしない
		if(result[0][0]==0) return;

		// 検索可能なレイヤが１つのみでそれが登録情報レイヤの場合、
		// SHIFTキーを押しながら地物をクリックしたら複数選択する
		if(layerIds.length==1) {
			var ecommap = stmap.ecommaps[0];
			var layerInfo = ecommap.layerInfoStore[layerIds[0]];
			if(layerInfo!=null && layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL) {
				if(!!evt && evt.shiftKey) {
					// 選択中のレイヤ情報として保持
					if(me.layerInfo==null) me.layerInfo = layerInfo;

					// ポップアップさせない
					args.executePopup = false;

					//console.debug("select features...");
					// 複数の場合でも最も近距離の地物を選択するため
					// 最初の地物を選択状態にする
					var layer = result[1][0][0];
					var fid = result[1][0][1];
					// 名称属性
					var nameAttr = result[1][0][3][0];
					var nameAttrId = nameAttr[0];
					// 名称属性の別名があれば設定する
					nameAttrInfo = me.layerInfo.getAttrInfo(nameAttrId);
					if(!!nameAttrInfo && !!nameAttrInfo.name) nameAttr[1] = nameAttrInfo.name;

					// 選択中かチェックする
					var selected = me.isSelected(layer, fid);

					// 選択中なら解除する
					if(selected) {
						var selectedFeatureData = me.get(layer, fid);
						me.deselect(selectedFeatureData);
					}
					// 未選択なら選択する
					else {
						stmap.api.getContent(stmap.mapId, layer, fid, center, bbox, function(data) {
							if(data==null) {
								return;
							}
							else if(typeof data=="error"){
								console.warn("error!",args);
							}
							else{
								// レスポンスが返ってくるのが遅いこともあるので
								// 選択にする前に、もう一度チェックする
								// 選択中かチェックする
								var selected = me.isSelected(layer, fid);

								// 選択中なら解除する
								if(selected) {
									me.deselect(layer, fid);
								}
								else {
									var featureData = new SaigaiTask.Map.control.SelectFeatureControl.FeatureData(data);
									featureData.nameAttr = nameAttr;
									me.select(featureData);
								}
							}
						});
					}
				}
			}
		}
	},

	/**
	 * 地物を選択状態にします.
	 * @param {SaigaiTask.Map.control.SelectFeatureControl.FeatureData} featureData 
	 */
	select: function(featureData) {
		var me = this;
		var stmap = me.stmap;

		// 選択済みフィーチャデータに追加
		me.selectedFeatureDatas.push(featureData);

		// 選択リストに追加
		if(me.window==null) {
			me.window = new SaigaiTask.Map.view.SelectFeatureWindow({
				stmap: stmap,
				featureData: featureData
			});

			// ウィンドウを閉じたら、選択状態をすべて解除する
			me.window.window.on("destroy", function() {
				me.clear();
			});

			// グリッドでアイテムを選択したら、ポップアップを表示する
			me.window.grid.on("select", function(grid, record, index, eOpts) {
				var data = record.getData();
				var layerId = data.layerId;
				var featureId = Number(data.featureId);
				var featureData = me.get(layerId, featureId);
				// 中心位置を地物に合わされないように、center引数を渡す
				var centroid = featureData.feature.geometry.getCentroid();
				var center = new OpenLayers.LonLat(centroid.x, centroid.y);
				center.transform(stmap.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));
				stmap.getContent(layerId, featureId, center);
			});

			// 解除ボタンハンドラを定義する
			me.window.removeActionColumnItem.handler = function(grid, rowIndex, colIndex) {
				var record = grid.getStore().getAt(rowIndex)
				var data = record.getData();
				var selectedFeatureData = me.get(data.layerId, data.featureId);
				if(selectedFeatureData!=null) {
					me.deselect(selectedFeatureData);
				}
			};

			// 一括変更ボタンハンドラを定義する
			me.window.slimerButton.setHandler(function() {
				me.onClickSlimerButton();
			});

			// 選択対象のレイヤの表示・非表示の連動
			var layerInfo = stmap.getLayerInfo(featureData.layerId);
			var layer = layerInfo.getLayer();
			layer.events.on({
				"refreshParams": function() {
					me.layer.display(layerInfo.visibility);
					if(layerInfo.visibility) {
						me.window.window.show();
					}
					else {
						me.window.window.hide();
					}
				}
			});

			me.window.show();
		}
		else {
			me.window.add(featureData);
		}

		// レイヤにフィーチャを追加
		try{
			var wkt = featureData.geom[0];
			var wktFormat = new OpenLayers.Format.WKT();
			var feature = wktFormat.read(wkt);
			feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), stmap.map.getProjectionObject());
			featureData.feature = feature;
			me.layer.addFeatures([feature]);
			me.layer.refresh();
			stmap.toFront(me.layer);
		}catch(e) {
			alert(lang.__("Unable to get position info."));
			console.error(lang.__("Failed to get position info :")+layerId+"."+fid);
			console.error(e);
			return;
		}
	},

	/**
	 * 一括変更ボタンが押されたら呼ばれる
	 */
	onClickSlimerButton: function() {
		var me = this;
		var stmap = me.stmap;
		if(!me.selectedFeatureDatas || me.selectedFeatureDatas.length==0) {
			alert(lang.__("Please select one or more."));
		}
		else {
			// カラム情報を生成
			var columns = [];
			for(var idx in me.layerInfo.attrInfos) {
				var attrInfo = me.layerInfo.attrInfos[idx];
				var editable = attrInfo.status==0;
				if(editable) {
					columns.push({
						name: attrInfo.name,
						field: attrInfo.attrId,
						dataType: attrInfo.dataTypeId,
						editable: editable,
						nullable: attrInfo.nullable
					});
				}
			}

			// 選択中のフィーチャだけ変更対象とする
			var targetIds = [];
			var values = {};
			for(var idx in me.selectedFeatureDatas) {
				// id を保持
				var selectedFeatureData = me.selectedFeatureDatas[idx];
				var attrs = selectedFeatureData.attrs;
				targetIds.push(selectedFeatureData.featureId);
				
				for(var attrsIdx in attrs) {
					var attr = attrs[attrsIdx];
					// 最初の地物の属性値を基準にする
					if(idx==0) {
						values[attr.attrId] = attr.attrValue;
					}
					else {
						// 属性値が異なった場合は空値にする
						if(values[attr.attrId]!=attr.attrValue) {
							values[attr.attrId] = null;
						}
					}
				}
			}

			// 一括変更を取得
			var slimer = me.slimer;
			if(!slimer) {
				// 一括変更情報がないか検索する
				var key = "gid";
				var slimers = SaigaiTask.Edit.slimers;
				for(var slimersIdx in slimers) {
					var _slimer = slimers[slimersIdx]
					if(me.layerInfo.layerId==_slimer.table) {
						slimer = me.slimer = _slimer;
						// ダイアログを閉じたときのイベント
						slimer.dialog.bind("slimersuccess", function(evt) {
							// 選択リストも閉じる(選択のクリア)
							//me.window.window.close();
							// 地図のリロード
							stmap.reload();
							// 一括変更対象をクリア
							slimer.targetIds = null;
						});
						break;
					}
				}
			}

			if(!slimer) {
				alert(lang.__("Bulk changes dialog is not found."));
			}
			else {
				// 一括変更対象を設定
				slimer.targetIds = targetIds;

				// 共通の値をフォームにセットする
				slimer.setValues(values);

				// ダイアログを表示
				slimer.dialog.dialog({
					title: lang.__("Bulk change"),
					minWidth: 400,
					maxHeight: 500,
					modal: true
				});
			}
		}
	},

	/**
	 * 地物の選択状態を解除します.
	 * @param {SaigaiTask.Map.control.SelectFeatureControl.FeatureData} selectedFeatureData 
	 */
	deselect: function(selectedFeatureData) {
		var me = this;

		// 選択済みフィーチャデータから削除する
		var index = jQuery.inArray(selectedFeatureData, me.selectedFeatureDatas);
		me.selectedFeatureDatas.splice(index, 1);

		// 選択リストから削除
		me.window.remove(selectedFeatureData);

		// レイヤからフィーチャを削除する
		var feature = selectedFeatureData.feature;
		if(feature!=null) {
			me.layer.removeFeatures([feature]);
			me.layer.refresh();
		}
	},

	/**
	 * すべての選択状態を解除します.
	 */
	clear: function() {
		var me = this;

		while(0<me.selectedFeatureDatas.length) {
			var selectedFeatureData = me.selectedFeatureDatas[0];
			me.deselect(selectedFeatureData);
		}

		me.layerInfo = null;
		me.window = null;
	},

	/**
	 * 選択済みフィーチャデータのインデックスを取得します.
	 * 存在しなければ -1 を返します.
	 */
	indexOf: function(layer, fid) {
		var me = this;
		for(var idx in me.selectedFeatureDatas) {
			var selectedFeatureData = me.selectedFeatureDatas[idx];
			if(selectedFeatureData.layerId==layer && selectedFeatureData.featureId==fid) {
				return idx;
			}
		}
		return -1;
	},

	/**
	 * レイヤID, フィーチャID を指定して地物を取得します.
	 */
	get: function(layer, fid) {
		var me = this;
		var index = me.indexOf(layer, fid);
		return index!=-1 ? me.selectedFeatureDatas[index] : null;
	},

	/**
	 * フィーチャが選択状態かどうかチェックします.
	 * @param {String} layer レイヤID
	 * @param {Number} fid フィーチャID
	 */
	isSelected: function(layer, fid) {
		return this.indexOf(layer, fid)!=-1 ? true : false;
	}
});


/**
 * 選択フィーチャデータ
 */
SaigaiTask.Map.control.SelectFeatureControl.FeatureData = new OpenLayers.Class({

	/**
	 * レイヤID
	 * @type {String}
	 */
	layerId: null,

	/**
	 * フィーチャID
	 * @type {Number}
	 */
	featureId: null,

	/**
	 * 属性データの配列
	 * @type {Array<Object>}
	 */
	attrs: null,

	geom: null,

	files: null,

	meta: null,

	/**
	 * ベクタフィーチャ
	 * @type {OpenLayers.Feature.Vector}
	 */
	feature: null,

	/**
	 * 生のデータ
	 * @type {Object}
	 */
	raw: null,

	initialize: function(data) {
		var me = this;

		// データをコピー
		OpenLayers.Util.extend(me, data);
	}
});