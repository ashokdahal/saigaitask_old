/**
 * 登録情報ウィンドウ
 * @require SaigaiTask/Map/view.js
 * @returns {SaigaiTask.Map.view.ContentsFormWindow}
 */
SaigaiTask.Map.view.ContentsFormWindow = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.ContentsFormWindow.prototype = {

	stmap: null,

	/**
	 * @type {Ext.window.Window}
	 */
	window: null,

	/**
	 * @type {SaigaiTask.Map.view.ContentsFormPanel}
	 */
	contentsFormPanel: null,

	/**
	 * @type {SaigaiTask.Map.view.DrawToolbar}
	 */
	drawToolbar: null,

	/**
	 * @type {Map.view.Measure}
	 */
	measure: null,

	/**
	 * 登録情報ウィンドウを表示します.
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
		var stmap = option.stmap;
		var layerInfo = option.layerInfo;
		var fid = typeof option.fid!="undefined" ? option.fid : null;
		var feature = typeof option.feature!="undefined" ? option.feature : null;
		var content = typeof option.content!="undefined" ? option.content : null;
		var lonlat = option.lonlat;
		var drawGeometryOnly = !!option.drawGeometryOnly;

		// 登録情報ウィンドウは一度に１つしか表示させない
		if(stmap.components.contentsFormWindow!=null) {
			stmap.components.contentsFormWindow.window.close();
		}
		stmap.components.contentsFormWindow = me;

		// TODO: レイヤが非表示だったら表示する

		// Toolbar
		var tbar = null;
		var dockedItems = [];

		// 描画ツールバー
		var geomType = layerInfo.geomType;
		var isMulti = geomType.indexOf("MULTI")==0;
		var isPoint = geomType.match(/POINT$/)!=null;
		var isPolygon = geomType.match(/POLYGON$/)!=null;
		var isLinestring = geomType.match(/LINESTRING$/)!=null;
		var pseudo = /*擬似ポリゴン*/isPolygon; // 擬似ジオメトリフラグ
		var drawToolbar = me.drawToolbar = new SaigaiTask.Map.view.DrawToolbar(stmap, {
			init: false,
			geomType: geomType,
			renderTo: Ext.getBody(),
			drawPoint: isPoint || isPolygon,
			drawPolygon: isPolygon,
			drawCircle: isPolygon,
			drawLine: isLinestring || isPolygon,
			modifyFeature: isPoint==false,
			dragFeature: true,
			removeFeature: true,
			intersectLayerId: layerInfo.intersectionlayerid,
			intersectLayerName: layerInfo.intersectionlayername,
			maxDrawNum: (isMulti ? Number.MAX_VALUE : 1)
		});

		// set Toolbar option
		if(isPolygon) {
			// 区切り文字で分割
			var split = function(tbarItems) {
				var array = [];
				var items = [];
				for(var idx in tbarItems) {
					var item = tbarItems[idx];
					// 区切り文字の場合
					if(item=="-") {
						// アイテムあればtoolbarとして追加
						if(0<items.length) {
							array.push({
								xtype: "toolbar",
								dock: "top",
								items: items
							});
						}
						// 空にする
						items = [];
					}
					else {
						items.push(item);
					}
				}
				return array;
			}
			// 分割する
			var tbarItems = drawToolbar.tbarItems;
			dockedItems = dockedItems.concat(split(tbarItems));
		}
		// Polygon以外は１つのToobarでまとめる
		else {
			tbar = drawToolbar.getSimpleToolbar();
		}

		// 描画レイヤ設定
		var drawLayer = drawToolbar.drawLayer;
		drawLayer.layer.setVisibility(true);

		// 最初のフィーチャを描画
		if(feature!=null) {
			// 擬似ジオメトリ可の場合、元のジオメトリに戻す
			if(pseudo) {
				var geometry = feature.geometry;
				var isMulti = geometry.CLASS_NAME.match(/^OpenLayers.Geometry.Multi/)!=null;
				var geoms = isMulti ? geometry.components : [geometry];
				for(var idx in geoms) {
					// 擬似ジオメトリからもとのジオメトリに戻す
					var geom = geoms[idx];
					orgGeom = drawToolbar.convertPseudoGeometry(geomType, geom);
					drawLayer.drawFeatureByGeometry(orgGeom!=null?orgGeom:geom);
				}
			}
			else {
				// そのまま描画する
				drawLayer.drawFeatureByGeometry(feature.geometry);
			}
		}
		else {
			if(isPoint) {
				// クリックした位置に点を描画する
				// フィーチャがあればフィーチャの位置に点を描画する
				if(feature!=null) {
					var point = feature.geometry.getCentroid().transform(new OpenLayers.Projection("EPSG:4326"), stmap.map.getProjectionObject());
					lonlat = new OpenLayers.LonLat(point.x, point.y);
				}
				// 位置の指定がなければ中心位置に点を描画する
				if(lonlat==null) {
					lonlat = stmap.map.getCenter();
				}
				var ret = drawLayer.moveSinglePoint(lonlat);
				//drawLayer.singlePointMoveClickHandler.activate();
				drawLayer.setDragFeatureControlActivation(true);
				feature = ret.feature;
			}
		}

		// 登録情報ウィンドウを初期化
		var layerId = layerInfo.layerId;
		var contentsFormPanel = me.contentsFormPanel = new SaigaiTask.Map.view.ContentsFormPanel(stmap, layerId, feature, fid, content);
		var formPanel = contentsFormPanel.formPanel;
		var layerName = contentsFormPanel.json.layerInfo.layerName;

		// フォームのウィンドウを生成
		var win = null;
		win = me.window = Ext.create('Ext.window.Window', {
			title: layerName + ' ' + lang.__('Registration form'),
			width: 300, //height: 600,
			maxWidth: document.body.clientWidth,
			maxHeight: document.body.clientHeight,
			collapsible: true,
			tbar: tbar,
			dockedItems: dockedItems,
			layout: 'fit',
			items: drawGeometryOnly ? null : formPanel,
			buttons: [{
				text: lang.__('Registration'),
				textAlign: "left",
				icon: stmap.icon.getURL("editIconURL"),
				handler: function(){
					var form = formPanel.form;
					if(form.isValid()) {
						try{
							// ONになっている描画コントロールがあれば解除する.
							// ポリゴンレイヤで編集モードだと、編集用のPOINTも形状チェック対象になり、Invalid Geometryになるので解除する
							drawLayer.deactivateCurrentDrawControl();
							var feature = drawToolbar.getDrawFeature(geomType, {
								pseudo: pseudo// 擬似ジオメトリ
							});

							if(confirm(lang.__('Are you sure to register?<!--2-->'))){
								// ジオメトリ描画モード
								if(drawGeometryOnly) {
									var wkt = stmap.getWKT(feature);
									// この処理は汎用的でないので、イベントトリガーして実行させるようにする
									/*window.parent.frames.addlineform.the_geom.value(wkt);
									window.parent.$('#map-dialog').dialog('close');*/
									if(fid == 0 || fid==null){
										parent.$('body').trigger('onsavegeom',[wkt]);
									}else{
										parent.$('body').trigger('oneditgeom',[wkt]);
									}
								}
								else {
									var wkt = stmap.getWKT(feature);
									contentsFormPanel.submit({
										wkt: wkt==null ? "" : wkt
									});
									win.close();
								}
							}
						} catch(e) {
							console.error(e);
							if(e=="Invalid Geometry") {
								alert(lang.__("You can not register this shape. \n shape might be overlapped"));
							}
							else {
								alert(e);
							}
						}
					}
					else {
						alert(lang.__("Input data not appropriate."));
					}
				}
			}, {
				text: lang.__('Cancel'),
				handler: function(){ win.close(); }
			}],
			listeners: {
				destroy: function() {
					drawLayer.destroy();
				}
			}
		});

		// 住所ボタンのクリックイベントを定義
		formPanel.addListener("clickaddressbtn", function(args) {
			var btn = args.addressBtn;
			var field = args.textField;
			// 最初のフィーチャの中心位置を使って住所を求める
			var features = drawToolbar.drawLayer.layer.features;
			if(0<features.length) {
				var feature = features[0];
				var center = feature.geometry.getCentroid();
				center = center.transform(stmap.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));

				stmap.geocode(center)
				.done(function(results) {
					var result = results[0];
					var address = stmap.getFormattedAddress(result);
					field.setValue(address);
				});
			}
		});

		// ウィンドウ表示
		win.show();
		win.alignTo(document, "tr", [-win.getWidth(), 0]);

		var measure = me.measure = new SaigaiTask.Map.view.Measure(stmap);
		measure.stopMeasure();
	}
};
