/**
 * 描画レイヤ
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/DrawLayerSelectDrag.js
 */
SaigaiTask.Map.Layer.DrawLayer = new OpenLayers.Class(SaigaiTask.Map.Layer.DrawLayerSelectDrag, {

	stmap: null,

	/** 地図click時のハンドラ */
	singlePointMoveClickHandler: null,

	/** 描画用のレイヤ */
	layer: null,
	drawFeatures: null,
	activeDrawKey: null,
	featureIdSerial: 0,

	/**
	 * コントロールの連想配列
	 * @type {Object.<String, OpenLayers.Control>}
	 */
	controls: null,

	/**
	 * @type {SaigaiTask.Map.Control.SnapControl}
	 */
	snapControl: null,

	/**
	 * ユーザが設定したスタイル
	 * @type {Object}
	 */
	userStyle: null,

	/**
	 *
	 */
	initialize: function(stmap) {
		var me = this;
		me.stmap = stmap;

		// スナップレイヤを初期化
		me.snapControl = new SaigaiTask.Map.control.SnapControl(stmap);

		// 描画レイヤを初期化
		me.initDraw();
		me.userStyle = {};

		// スナップの編集レイヤに描画レイヤを設定
		me.snapControl.setLayer(me.layer);

		// 点を描画して位置を選ぶ
		me.singlePointMoveClickHandler = new OpenLayers.Handler.Click(
			stmap, {
				click: function(evt) {
					var lonlat = stmap.map.getLonLatFromPixel(new OpenLayers.Pixel(evt.xy.x, evt.xy.y));
					var ret = me.moveSinglePoint(lonlat);
					var feature = ret.feature;
					$("."+feature.fid).change();
					return ret;
				}
			}, {
				single: true
			}
		);
		me.singlePointMoveClickHandler.deactivate();

		// 描画の履歴
		me.historyControl = new SaigaiTask.Map.control.DrawLayerHistoryControl(me);
		stmap.controls.kmlSelectControl.deactivate();

		// テキストメモ用の選択ボタンの初期化
		SaigaiTask.Map.Layer.DrawLayerSelectDrag.prototype.initialize.apply(this, [me.layer]);
	},

	getFeatureIDPrefix: function(){
		return this.stmap.div+"Feature";
	},

	createFeatureID: function(drawKey){
		// TODO: DrawLayerを識別できるようにする？
		var num = this.layer.features.length-1;
		return this.getFeatureIDPrefix()+num;
	},

	getFeatureIndexFromID: function(featureId){
		var prefix = this.getFeatureIDPrefix();
		var id = featureId.substring(prefix.length);
		return id;
	},

	initDraw: function(){
		var me = this;
		var stmap = me.stmap;
		// 描画レイヤを作成
		var layer = me.layer = new OpenLayers.Layer.Vector("Draw Layer");
		layer.events.on({
			"featureadded": function(evt){
				var feature = evt.feature;
				// 追加したフィーチャを取得する
				var f = feature;
				// フィーチャIDを設定する
				f.fid = me.createFeatureID(me.activeDrawKey);
				// 追加したフィーチャにスタイル設定がなければデフォルトスタイルを保存する
				if(!f.style) {
					f.style = new Object();
					var style = me.layer.styleMap.styles['default'].defaultStyle;
					for(var key in style){
						f.style[key] = style[key];
					}
				}
				// マウスカーソルをポインタにする
				f.style.cursor="pointer";
				// フィーチャを保存する
				if(!me.drawFeatures[me.activeDrawKey]){
					me.drawFeatures[me.activeDrawKey] = new Array();
				}
				me.drawFeatures[me.activeDrawKey].push(f);
				me.writeFeatureGeometoryValue(feature);
			},
			"featureremoved": function(evt){
				var feature = evt.feature;
				$("."+feature.fid).val("");
			},
			"featuremodified": function(evt){
				var feature = evt.feature;
				me.writeFeatureGeometoryValue(feature);
			}

		});
		stmap.addLayer(layer);

		// 描画スタイルを設定
		layer.styleMap = new OpenLayers.StyleMap();
		// スタイル複製しておく
		for(var key in layer.styleMap.styles) {
			var obj = layer.styleMap.styles[key];
			if(obj instanceof OpenLayers.Style) {
				layer.styleMap.styles[key] = obj.clone();
			}
		}
		layer.styleMap.styles['default'].defaultStyle = me.getDrawControlStyle();

		// 描画コントローラを作成
		var dragFeatureStatus = null;
		var drawControls = {
			text: new OpenLayers.Control.DrawFeature(layer, OpenLayers.Handler.Point, {
				featureAdded:function(feature){ me._startTextEdit(feature); }
			}),
			point:   new OpenLayers.Control.DrawFeature(layer,OpenLayers.Handler.Point),
			line:    new OpenLayers.Control.DrawFeature(layer,OpenLayers.Handler.Path),
			freeline:    new OpenLayers.Control.DrawFeature(layer,OpenLayers.Handler.Path, {
				handlerOptions: {
					freehand: true
				}
			}),
			polygon: new OpenLayers.Control.DrawFeature(layer,OpenLayers.Handler.Polygon),
			circle: new OpenLayers.Control.DrawFeature(layer,OpenLayers.Handler.RegularPolygon, {
				handlerOptions: {sides: 40}, // 頂点数
				eventListeners: {
					"deactivate": function() {
						// 前の半径ポップアップがあったら閉じる
						if(!!stmap.popupManager) {
							stmap.popupManager.closeAll();
						}
					}
				},
				callbacks: {
					// dragging
					"move": function(geometry) {
						// 円の半径を求める
						// 単位がメートルの座標系に変換
						if(map.map.units!="m") {
							geometry = geometry.clone().transform(map.map.getProjectionObject(), new OpenLayers.Projection("EPSG:900913"));
						}
						// 円の面積 A = Pi * r^2
						// r = Sqrt(A / Pi) = 0.56419 * Sqrt(A)
						var area = geometry.getArea();
						var radius = 0.565352 * Math.sqrt(area);

						// 前の半径ポップアップがあったら閉じる
						if(!!stmap.popupManager) {
							stmap.popupManager.closeAll();
						}

						// 円の半径を表示する
						var centroid = geometry.getCentroid();
						var center = new OpenLayers.LonLat(centroid.x, centroid.y).transform(stmap.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));
						popup = new SaigaiTask.Map.view.Popup();
						var radiusText = parseInt(radius)+"m";
						if(10000 < radius) radiusText =(radius/1000).toFixed(2)+"km";
						popup.showExtPopup({
							map: stmap,
							olmap: stmap.map,
							center: center,
							size: new OpenLayers.Size(150,10),
							title: radiusText
						});
					}
				}
			}),
			box:     new OpenLayers.Control.DrawFeature(layer,OpenLayers.Handler.RegularPolygon, {
				handlerOptions: {
					sides: 4,
					irregular: true
				}
			}),
			dragFeature: new OpenLayers.Control.DragFeature(layer, {
				onStart: function(feature,pixel){
					dragFeatureStatus = {
						beforeFeature: feature.clone()
					};
				},
				onComplete: function(feature,pixel){
					me.writeFeatureGeometoryValue(feature);
					if(!!dragFeatureStatus) {
						// 地物の移動イベントをトリガーする
						var event = {
							type: "featuremoved",
							feature: feature,
							afterFeature: feature.clone(),
							beforeFeature: dragFeatureStatus.beforeFeature
						};
						me.layer.events.triggerEvent(event.type, event);
						dragFeatureStatus = null;
					}
				}
			}),
			modifyFeature: new OpenLayers.Control.ModifyFeature(layer),
			selectFeature: new OpenLayers.Control.SelectFeature(layer),
			selectDragFeature: new OpenLayers.Control.SelectDragFeature(layer, {
				onSelect:function(feature){ me._featureSelected(feature); },
				onUnselect:function(feature){ me._featureUnselected(feature); },
				onDblClick:function(feature){ me.checkTextEdit(feature); },
				onKeyDown:function(evt){ /*if (me.eMap.isFocus())*/ if(document.activeElement.tagName!="TEXTAREA") me._onKeyDown(evt); },
				//onStart:function(feature, pixel, toggled){ if (!toggled) me.selectFeature(feature); },
				onStart: function(feature,pixel){
					// SelectDragControlでは複数選択して移動ができるためその対応
					var features = me.memoLayer.selectedFeatures;
					for(var idx in features) {
						var feature = features[idx];
						delete feature.beforeFeature;
						var beforeFeature = me.cloneTextFeature(feature);
						feature.beforeFeature = beforeFeature;
					}
					dragFeatureStatus = {
					};
				},
				onDrag:function(pixel){ me.memoSelectLayer.setVisibility(false); },
				onComplete:function(pixel){
					me.memoSelectLayer.setVisibility(true);
					me.resetFeatureSelection(layer);
					me._selectedFeaturesModified();

					if(!!dragFeatureStatus) {
						var features = me.memoLayer.selectedFeatures;
						for(var idx in features) {
							//var feature = me.controls.selectDragFeature.mouseFeature;
							var feature = features[idx];
							beforeFeature = feature.beforeFeature;

							// 地物の移動イベントをトリガーする
							var event = {
								type: "featuremoved",
								feature: feature,
								afterFeature: me.cloneTextFeature(feature),
								beforeFeature: beforeFeature
							};
							me.layer.events.triggerEvent(event.type, event);
						};
						dragFeatureStatus = null;							
					}
				},
				clickout:true, toggle:false, multiple:false, hover:false, toggleKey:"ctrlKey", multipleKey:"shiftKey", box:false
			}),
			selectDragFeatureBox: new OpenLayers.Control.SelectDragFeature(layer, {
				onSelect:function(feature){ me._featureSelected(feature); },
				onUnselect:function(feature){ me._featureUnselected(feature); },
				onDblClick:function(feature){ me.checkTextEdit(feature); },
				onKeyDown:function(evt){ /*if (me.eMap.isFocus())*/ if(document.activeElement.tagName!="TEXTAREA") me._onKeyDown(evt); },
				//onStart:function(feature, pixel, toggled){ if (!toggled) self.selectFeature(feature); },
				onStart: function(feature,pixel){
					// SelectDragControlでは複数選択して移動ができるためその対応
					var features = me.memoLayer.selectedFeatures;
					for(var idx in features) {
						var feature = features[idx];
						delete feature.beforeFeature;
						var beforeFeature = me.cloneTextFeature(feature);
						feature.beforeFeature = beforeFeature;
					}
					dragFeatureStatus = {
					};
				},
				onDrag:function(pixel){ me.memoSelectLayer.setVisibility(false); },
				onComplete:function(pixel){
					me.memoSelectLayer.setVisibility(true);
					me.resetFeatureSelection(layer);
					me._selectedFeaturesModified();

					if(!!dragFeatureStatus) {
						var features = me.memoLayer.selectedFeatures;
						for(var idx in features) {
							//var feature = me.controls.selectDragFeature.mouseFeature;
							var feature = features[idx];
							beforeFeature = feature.beforeFeature;

							// 地物の移動イベントをトリガーする
							var event = {
								type: "featuremoved",
								feature: feature,
								afterFeature: me.cloneTextFeature(feature),
								beforeFeature: beforeFeature
							};
							me.layer.events.triggerEvent(event.type, event);
						};
						dragFeatureStatus = null;							
					}
				},
				clickout:true, toggle:false, multiple:false, hover:false, toggleKey:"ctrlKey", multipleKey:"shiftKey", box:true})
			
		};

		// 描画コントローラを追加
		me.controls = {};
		for(var key in drawControls){
			var control = drawControls[key];
			me.addControl(control, key);
		}

		// フィーチャ編集コントローラ設定
		var virtualStyle = drawControls.modifyFeature.virtualStyle;
		virtualStyle.strokeColor = "#FF0000";
		virtualStyle.strokeWidth = 2;
		virtualStyle.strokeDashstyle = 'dash';
		virtualStyle.externalGraphic = stmap.icon.getURL("verticeIconURL");
		virtualStyle.graphicWidth = 9;
		virtualStyle.graphicHeight = 9;
		virtualStyle.graphicOpacity = 0.5;
		virtualStyle.graphicXOffset = -5;
		virtualStyle.graphicYOffset = -5;

		// 描画フィーチャを保存するオブジェクトを生成
		me.drawFeatures = new Object();
		for(var key in drawControls){
			me.drawFeatures[key] = new Array();
		}
	},

	// 描画スタイル
	/**
	 * 描画コントロールのデフォルトスタイルを取得する
	 * KMLStyle のために、色名を使用せず、カラーコードを用いる.
	 * @param {String} 描画コントローラキー
	 */
	getDrawControlDefaultStyle: function(key){
		var me = this;
		var stmap  = me.stmap;
		console.log("getDrawControlDefaultStyle");
		var style = {
			// 塗りつぶしの設定
			fillColor: "#FFFFFF",
			fillOpacity: 0.4,
			// ラインの設定
			strokeColor: "#000000",//"#ee9900",
			strokeOpacity: 1,
			strokeWidth: 2,
			strokeLinecap: "round", // butt, round, square
			strokeDashstyle: "solid", // dot, dash, dashdot, longdash, longdashdot, solid
			// フォント設定
			fontSize: 14, // @see OpenLayers.Format.KMLStyle.FONTSIZE
			labelAlign:"lt",
			// 図の設定
			externalGraphic: stmap.icon.getURL("editingIconURL"),
			graphicWidth: 19,
			graphicHeight: 32,
			graphicXOffset: -9,
			graphicYOffset: -32,
			graphicOpacity: 1
		};
		switch(key){
		case "point":
		case "line":
		case "polygon":
		case "box":
			break;
		case "modifyFeature":
			style.externalGraphic = stmap.icon.getURL("pointnodeIconURL");
			style.graphicWidth = 9;
			style.graphicHeight = 9;
			style.graphicXOffset = -5;
			style.graphicYOffset = -5;
			style.graphicOpacity = 1;
			break;
		case "selectDragFeature":
		case "text":
			style = {};
			Ext.apply(style, me.modifyStyleMap.styles.default.defaultStyle);
			break;
		}
		return style;
	},
	/**
	 * 描画コントロールに対応したトスタイルを取得する
	 * @param {String} 描画コントローラキー
	 */
	getDrawControlStyle: function(key){
		var me = this;
		var stmap  = me.stmap;
		var style = me.getDrawControlDefaultStyle(key);
		// ユーザが設定したスタイルがあれば上書き
		Ext.apply(style, me.userStyle);
		return style;
	},

	/**
	 * フィーチャの値を指定クラスに書き込む
	 * @param {OpenLayers.Feature} feature
	 */
	writeFeatureGeometoryValue: function(feature){
		if(!feature) return;
		var me = this;
		var stmap = me.stmap;
		var wkt = stmap.getWKT(feature);
		var featureId = feature.fid;
		switch(feature.geometry.CLASS_NAME){
		case "OpenLayers.Geometry.Point":
		case "OpenLayers.Geometry.LineString":
		case "OpenLayers.Geometry.Polygon": // 四角形も含む
		default:
			// 値を書き込む
			var c = "."+featureId;
			var jqueryObj = jQuery(c);
			jqueryObj.val(wkt);
			break;
		}
	},

	/**
	 * 指定の座標に座標選択用の点を移動する.
	 * 点がない場合は描画する.
	 * @param {OpenLayers.LonLat} lonlat
	 * @returns {Object} ret
	 */
	moveSinglePoint: function(lonlat){
		var me = this;
		// point draw control
		var drawKey = 'point';
		var idx = 0;
		var feature = me.drawFeatures[drawKey][idx];

		me.layer.setVisibility(true);

		if(lonlat!=null) {
			if(feature){
				feature.move(lonlat);
			}
			else {
				var lon = lonlat.lon;
				var lat = lonlat.lat;
				me.drawPointFeature(lon,lat);
				feature = me.drawFeatures[drawKey][idx];
			}
		}

		if(feature!=null) {
			me.writeFeatureGeometoryValue(feature);
		}

		var ret = {
			feature: feature
		};

		return ret;
	},

	/**
	 * 座標選択用の点を削除する.
	 */
	removeSinglePoint: function() {
		return me.removePointFeature(0);
	},


	/**
	 * 地図の中心にポイントを描画する
	 */
	drawCenterPoint: function(){
		var center = this.map.getCenter();
		return this.drawPointFeature(center.lon, center.lat);
	},

	/**
	 * ポイントを描画する
	 * @param {Number} lon
	 * @param {Number} lat
	 */
	drawPointFeature: function(lon,lat){
		var geometry = new OpenLayers.Geometry.Point(lon,lat);
		return this.drawFeature("point",geometry);
	},

	/**
	 * ジオメトリを描画する
	 * @param {String} controlKey 描画コントローラキー
	 * @param {OpenLayers.Geometry} geometry ジオメトリ
	 */
	drawFeature: function(controlKey, geometry){
		if(!geometry||!controlKey) return;

		var drawControl = this.getDrawControl(controlKey);
		var savedActiveDrawKey = this.activeDrawKey;
		this.activeDrawKey = controlKey;
		var ret = drawControl.drawFeature(geometry);
		this.activeDrawKey = savedActiveDrawKey;
		return ret;
	},

	/**
	 * ジオメトリを描画する
	 * @param {OpenLayers.Geometry} geometry (EPSG:4326)
	 */
	drawFeatureByGeometry: function(geometry) {
		var me = this;
		if(geometry.CLASS_NAME.match(/^OpenLayers.Geometry.Multi/)!=null) {
			for(var idx in geometry.components) {
				me.drawFeatureByGeometry(geometry.components[idx]);
			}
		}
		else {
			var controlKey = null;
			switch(geometry.CLASS_NAME) {
			case "OpenLayers.Geometry.Point":
				controlKey = "point";
				break;
			case "OpenLayers.Geometry.LineString":
				controlKey = "line";
				break;
			case "OpenLayers.Geometry.Polygon":
				controlKey = "polygon";
				break;
			}
			geometry.transform(new OpenLayers.Projection("EPSG:4326"), me.stmap.map.getProjectionObject());
			me.drawFeature(controlKey, geometry);
		}
	},

	getDrawLayerFeatureByIndex: function(idx) {
		return this.layer.features[idx];
	},

	removePointFeature: function(idx){
		return this.removeFeature("point",idx);
	},

	removeFeature: function(drawKey,idx){
		var feature = this.drawFeatures[drawKey][idx];
		this.layer.removeFeatures([feature]);
		this.drawFeatures[drawKey][idx] = null;
	},

	// コントローラ
	/**
	 * コントローラをOpenLayersに追加する
	 * キーがあれば連想配列に保存する
	 * @param {OpenLayers.Control} control コントローラ
	 * @param {String} key 連想配列のキー
	 */
	addControl: function(control, key) {
		if(typeof key!='undefined'){
			this.controls[key]=control;
		}
		return this.stmap.map.addControl(control);
	},

	setControlActivation: function(control,activation) {
		var me = this;
		if(activation)return control.activate();
		else {
			if(control==me.getDrawControl(me.activeDrawKey)) {
				me.activeDrawKey = null;
			}
			return control.deactivate();
		}
	},

	// 描画コントローラ
	/**
	 * 描画コントローラを取得する
	 * @param {String} key 描画コントローラキー
	 */
	getDrawControl: function(key){
		return this.controls[key];
	},

	/**
	 * 描画コントローラの有効フラグを取得する
	 * @param {String} key 描画コントローラキー
	 */
	getDrawActivation: function(key){
		var control=this.getDrawControl(key);
		return control.active;
	},

	/**
	 * 描画コントローラの有効フラグを設定する
	 * @param {String} key 描画コントローラキー
	 * @param {Boolean} activate 有効フラグ
	 */
	setDrawActivation: function(key,activate){
		var control=this.getDrawControl(key);
		return this.setControlActivation(control,activate);
	},

	/**
	 * 描画コントローラを無効にする
	 * @param {String} 描画コントローラキー
	 */
	deactivateDrawControl: function(key){
		return this.setDrawActivation(key,false);
	},

	/**
	 * 現在有効になっているモードがあるならOFFにする
	 */
	deactivateCurrentDrawControl: function(){
		var activeDrawKey = this.activeDrawKey;
		if(activeDrawKey) {
			return this.deactivateDrawControl(activeDrawKey);
		}
	},

	/**
	 * 描画コントローラを有効にする
	 * 一度に有効にできるコントローラは１つだけ。
	 * @param {String} 描画コントローラキー
	 */
	activateDrawControl: function(key){
		// マウスコントロールをすべて解除する
		//this.deactivateMouseControl();
		// 有効にする
		if(key){
			console.log("activate "+key);
			this.setDrawActivation(key,true);
			this.activeDrawKey=key;
			var style = this.getDrawControlStyle(key);
			this.layer.styleMap.styles['default'].defaultStyle = style;
			return true;
		}
		return false;
	},

	/**
	 * フィーチャ移動コントローラのactivationを取得します.
	 */
	getDragFeatureControl: function() {
		var me = this;
		var key = "dragFeature";
		var control = me.controls[key];
		if(!control) return null;
		return control;
	},

	/**
	 * フィーチャ移動コントローラのactivationを取得します.
	 */
	getDragFeatureControlActivation: function() {
		var me = this;
		var control = me.getDragFeatureControl();
		if(!control) return false;
		return control.active;
	},

	/**
	 * フィーチャ移動コントローラの有効無効を指定します.
	 * @type {Boolean} activation 指定がなければ反転
	 */
	setDragFeatureControlActivation: function(activation) {
		var me = this;
		var stmap = me.stmap;
		var key = "dragFeature";
		var control = me.getDragFeatureControl()
		if(!control) return;
		if(typeof activation=="undefined"){
			// 指定がなければ反転する
			activation = !control.active;
		}

		if(activation){
			stmap.deactivateMouseControl();
			me.deactivateCurrentDrawControl(); // 描画
			me.setDragFeatureControlActivation(false); // フィーチャ移動
			me.setModifyFeatureControlActivation(false); // フィーチャ編集
			stmap.setNavigationControlActivation(true);
			return me.activateDrawControl(key);
		}
		return me.setControlActivation(control, activation);
	},


	/**
	 * フィーチャ選択コントローラのactivationを取得します.
	 */
	getSelectDragFeatureControl: function() {
		var me = this;
		var key = "selectDragFeature";
		var control = me.controls[key];
		if(!control) return null;
		return control;
	},
	/**
	 * フィーチャ選択コントローラのactivationを取得します.
	 */
	getBoxSelectDragFeatureControl: function() {
		var me = this;
		var key = "selectDragFeatureBox";
		var control = me.controls[key];
		if(!control) return null;
		return control;
	},

	/**
	 * フィーチャ選択コントローラのactivationを取得します.
	 */
	getSelectDragFeatureControlActivation: function() {
		var me = this;
		var control = me.getSelectDragFeatureControl();
		if(!control) return false;
		return control.active;
	},
	/**
	 * フィーチャ選択コントローラのactivationを取得します.
	 */
	getBoxSelectDragFeatureControlActivation: function() {
		var me = this;
		var control = me.getBoxSelectDragFeatureControl();
		if(!control) return false;
		return control.active;
	},

	/**
	 * フィーチャ選択コントローラの有効無効を指定します.
	 * @type {Boolean} activation 指定がなければ反転
	 */
	setSelectDragFeatureControlActivation: function(activation) {
		var me = this;
		var stmap = me.stmap;
		var key = "selectDragFeature";
		var control = me.getSelectDragFeatureControl()
		if(!control) return;
		if(typeof activation=="undefined"){
			// 指定がなければ反転する
			activation = !control.active;
		}

		if(activation){
			stmap.deactivateMouseControl();
			me.deactivateCurrentDrawControl(); // 描画
			me.setDragFeatureControlActivation(false); // フィーチャ移動
			me.setModifyFeatureControlActivation(false); // フィーチャ編集
			stmap.setNavigationControlActivation(true);
			stmap.toFront(me.layer);
			return me.activateDrawControl(key);
		}
		return me.setControlActivation(control, activation);
	},
	/**
	 * フィーチャ選択コントローラの有効無効を指定します.
	 * @type {Boolean} activation 指定がなければ反転
	 */
	setBoxSelectDragFeatureControlActivation: function(activation) {
		var me = this;
		var stmap = me.stmap;
		var key = "selectDragFeatureBox";
		var control = me.getBoxSelectDragFeatureControl()
		if(!control) return;
		if(typeof activation=="undefined"){
			// 指定がなければ反転する
			activation = !control.active;
		}

		if(activation){
			stmap.deactivateMouseControl();
			me.deactivateCurrentDrawControl(); // 描画
			me.setDragFeatureControlActivation(false); // フィーチャ移動
			me.setModifyFeatureControlActivation(false); // フィーチャ編集
			stmap.setNavigationControlActivation(true);
			stmap.toFront(me.layer);
			return me.activateDrawControl(key);
		}
		return me.setControlActivation(control, activation);
	},

	/**
	 * フィーチャ編集
	 */
	setModifyFeatureControlActivation: function(activation) {
		var me = this;
		var stmap = me.stmap;
		var key = "modifyFeature";
		var control = this.controls[key];
		if(!control)return;
		if(typeof activation=="undefined"){
			// 指定がなければ反転する
			activation = !control.active;
		}

		if(activation){
			stmap.deactivateMouseControl();
			me.deactivateCurrentDrawControl(); // 描画
			me.setDragFeatureControlActivation(false); // フィーチャ移動
			me.setModifyFeatureControlActivation(false); // フィーチャ編集
			stmap.setNavigationControlActivation(true);

			// フィーチャ編集モード
			control.mode = OpenLayers.Control.ModifyFeature.RESHAPE;

			// 編集コントローラを有効にする
			var ret = me.activateDrawControl(key);

			// フィーチャがあれば選択状態にする
			if(0<me.layer.features.length) {
				control.selectFeature(me.layer.features[0]);
			}

			return ret;
		}

		return me.setControlActivation(control, activation);
	},

	/**
	 * 終了処理
	 */
	destroy: function() {
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;
		var drawLayer = me;
		drawLayer.deactivateCurrentDrawControl();
		drawLayer.setDragFeatureControlActivation(false);
		// SelectFeature を無効、取り除く
		drawLayer.controls.selectFeature.deactivate();
		olmap.removeControl(drawLayer.controls.selectFeature);
		drawLayer.layer.setVisibility(false);
		drawLayer.layer.removeAllFeatures();
		drawLayer.singlePointMoveClickHandler.deactivate();
		olmap.removeLayer(drawLayer.layer);
		drawLayer.layer.destroy();
		// スナップ終了処理
		me.snapControl.destroy();
		// kmlSelectFeature を有効化
		stmap.controls.kmlSelectControl.activate();
	}

});
