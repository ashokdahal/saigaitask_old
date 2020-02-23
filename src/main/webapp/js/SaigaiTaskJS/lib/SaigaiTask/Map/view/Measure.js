/**
 *
 *  @requires SaigaiTask/Map/view.js
 *  author: nakano
 *  距離面積計測機能
 */

SaigaiTask.Map.view.Measure = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.Measure.prototype = {

		map: null,
		measure: null,
		/** 距離計測コントロール */
		DIST : null,
		/** 面積計測コントロール */
		AREA : null,
		/** 円計測コントロール */
		CIRCLE : null,

		/** 計測結果表示用 */
		measureLayer : null,

		/** 描画予定の図形種別 */
		featureMode : "LINE",

		/** 初期化フラグ */
		initdrawlayer : true,
		/**
		 * コンストラクタ
		 */
		initialize: function(map) {
			var me = this;
			me.map = map;
			distresult = 0;
			arearesult = 0;
			// イベントの設定
			me.onEvent();
/*
			me.measure = Ext.create("Ext.button.Button", {
				//text : '距離面積計測',
				icon: map.icon.getURL("rulerIconURL"),
				//margin : "0 5 0 5",
				tooltip: '距離面積計測',
				listeners: {
					click: function() {
						me.showMenu();
						//me.onEvent();
					}
				}
			});
*/
		},

		/** 各種イベントの設定**/
		onEvent : function(){
			var me = this;
			/* 計測モードで描画したものを計測 */
			if(typeof map.components.contentsFormWindow == "undefined" || map.components.contentsFormWindow == null || !(!!map.components.contentsFormWindow && map.components.contentsFormWindow.drawToolbar.drawLayer.layer.visibility)){
				// 距離を計測
				$(document).on('click','#measure_type_dist',function(){
					me.startMeasureDist();
				});
				// 面積を計測
				$(document).on('click','#measure_type_area',function(){
					me.startMeasureArea();
				});
				// 人口と世帯数表示
				$(document).on('click','#measure_type_people',function(){
					me.startMeasurePeople();
				});
			}
			/* 新規登録で描画したものを計測 */
			else{
				me.startMeasure();
			}

		},

		offEvent : function(){
			var me = this;
			$(document).off('click','#measure_type_dist');
			$(document).off('click','#measure_type_area');
			$(document).off('click','#measure_type_people');
		},

		/** 距離計測開始 */
		startMeasureDist : function()
		{
			var me = this;
			me.featureMode = "DIST";
			me.initMeasureLayer();
			me.setPanelVisible('DIST');
			$("#measure_dist").attr('value', "0");
			$("#measure_dist_units").attr('value', "");
			me._startMeasure(false);
		},
		/** 面積計測開始 */
		startMeasureArea : function()
		{
			var me = this;
			// 面積と人口を切り替える時は初期化しないようにする
			if(me.featureMode != "PEOPLE"){
				me.initMeasureLayer();
				me._startMeasure(true);
				$("#measure_area").attr('value', "0");
				$("#measure_area_units").attr('value', "");
			}
			me.featureMode = "AREA";
			me.setPanelVisible('AREA');
			//this.setPanelVisible('OUTER');
		},
		/** 人口数計測開始 */
		startMeasurePeople : function()
		{
			var me = this;
			// 面積と人口を切り替える時は初期化しないようにする
			if(me.featureMode != "AREA"){
				me.initMeasureLayer();
				me._startMeasure(true);
				$("#measure_people").attr('value', "0");
				$("#measure_house").attr('value', "0");
			}
			me.featureMode = "PEOPLE";
			me.setPanelVisible('PEOPLE');

		},
		/** 登録レイヤの計測開始 */
		startMeasure : function()
		{
			$("#measure_dist_span").css('display',"");
			$("#measure_area_span").css('display',"");
			$("#measure_people_span").css('display',"")
			$("#buttons").hide();

			this._startMeasure(true);
		},
		/** 計測結果レイヤを初期化して地図に追加 */
		initMeasureLayer : function()
		{
			if (this.measureLayer) {
				//形状削除
				this.measureLayer.removeAllFeatures(this.measureLayer.features);
			} else {
				//生成
				this.measureLayer = new OpenLayers.Layer.Vector(lang.__("Measurement results"));
			}
			//スタイル設定

			this.map.map.addLayer(this.measureLayer);
		},

		stopMeasure : function()
		{
			//ラジオを未選択に
			//$("#measure_type_dist").attr("checked", false);
			//$("#measure_type_area").attr("checked", false);
			//this.setPanelVisible();
			//$("#measure_widget").toggle();
			this._deactivateMeasure();
			if(this.measureLayer){
				this.map.map.removeLayer(this.measureLayer);
				this.measureLayer = null;
				distresult = 0;
				arearesult = 0;
			}
			$('#measure').dialog('close');
		},

		setPanelVisible : function(type)
		{
			var distDisplay = type=="DIST" ? "block" : "none";
			var areaDisplay = type=="AREA" ? "block" : "none";
			//var outerDisplay = type=="OUTER" ? "" : "none";
			var peopleDisplay = type=="PEOPLE" ? "block" : "none";
			var controlDisplay = type ? "block" : "none";
			$("#measure_dist_span").css('display',distDisplay);
			$("#measure_area_span").css('display',areaDisplay);
			//$("#measure_outer_span").css('display',outerDisplay);
			$("#measure_people_span").css('display',peopleDisplay);
			$("#measure_control_span").css('display',controlDisplay);
		},

		addCenterMeasure : function()
		{
			var lonlat = this.map.map.getCenter();
			this._addMeasurePoint(lonlat.lon, lonlat.lat);
		},
		undoMeasurePoint : function()
		{
			this._undoMeasurePoint();
		},

		////////////////////////////////
		//	計測
		/** 計測開始 */
		_startMeasure : function(areadraw)
		{
			var me = this;
			//初期化
			if (!me.DIST) {
				var style = new OpenLayers.Style();
				style.addRules([new OpenLayers.Rule({symbolizer:{
					"Point": {pointRadius: 4,graphicName: "square",fillColor: "#FFFFFF",fillOpacity: 1,strokeWidth: 1,strokeOpacity: 1,strokeColor: "#333333"},
					"Line": {strokeWidth: 3,strokeOpacity: 1,strokeColor: "#FF0000",strokeDashstyle: "dash"},
					"Polygon": {strokeWidth: 2,strokeOpacity: 1,strokeColor: "#FF0000",fillColor: "#FFFFFF",fillOpacity: 0.3}
				}})]);

				var measureOptions = {
						handlerOptions: {
							style:"default",
							layerOptions:{
								styleMap:new OpenLayers.StyleMap({"default":style})
							},
							persist:true/*, move: function(){if(!!map.components.contentsFormWindow && map.components.contentsFormWindow.drawToolbar.drawLayer.layer.visibility){me.stopMeasure();}}*/
						}
				};
				me.DIST = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, measureOptions);
				me.DIST.geodesic = true;
				me.map.map.addControl(me.DIST);

				me.AREA = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon, measureOptions);
				me.AREA.geodesic = true;
				me.map.map.addControl(me.AREA);

				me.CIRCLE = new OpenLayers.Control.Measure(OpenLayers.Handler.RegularPolygon, {
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
				}, measureOptions);
				me.CIRCLE.geodesic = true;
				me.map.map.addControl(me.CIRCLE);

				//ダブルクリック無効
				//this.DIST.handler.dblclick = this.mapControls['MEASURE'].handler.click;
				//this.AREA.handler.dblclick = this.mapControls['MEASUREAREA'].handler.click;

				this.DIST.partialDelay = 0;
				this.AREA.partialDelay = 0;
				this.CIRCLE.partialDelay = 0;
			}

			var contentsFormWindow = map.components.contentsFormWindow;
			// 登録情報編集ウィンドウが表示されているかチェック
			if(!!contentsFormWindow && contentsFormWindow.drawToolbar.drawLayer.layer.visibility) {
				var drawToolbar = contentsFormWindow.drawToolbar;
				var features = drawToolbar.drawLayer.drawFeatures;
				var linebtn, polygonbtn, circlebtn, modifybtn;

				for(i = 0;i < drawToolbar.tbarItems.length; i++){
					if(drawToolbar.tbarItems[i].iconCls == "draw-line-icon")
						linebtn = drawToolbar.tbarItems[i];
					if(drawToolbar.tbarItems[i].iconCls == "draw-polygon-icon")
						polygonbtn = drawToolbar.tbarItems[i];
					if(drawToolbar.tbarItems[i].iconCls == "draw-circle-icon")
						circlebtn = drawToolbar.tbarItems[i];
				}

				var drawLayer =  contentsFormWindow.drawToolbar.drawLayer;
				if(me.initdrawlayer){
					drawLayer.layer.events.on({
						"featureadded": function(evt) {
							var distunit = $("#measure_dist_units").html() == 'm';
							if(distunit){
								var dist = parseFloat($("#measure_dist").val());
							}else{
								var dist = parseFloat($("#measure_dist").val()) * 1000;
							}
							var areaunit = $("#measure_area_units").html() == 'm';
							if(areaunit){
								var area = parseFloat($("#measure_area").val());
							}else{
								var area = parseFloat($("#measure_area").val()) * 1000000;
							}
							var peopleval = $("#measure_people").val();
							if (typeof peopleval == "undefined") var people = 0;
							else {
								if (peopleval.indexOf(",") != -1) var people = parseInt(peopleval.split(',').join('').trim());
								else var people = parseInt(peopleval);
							}
							var houseval = $("#measure_house").val();
							if (typeof houseval == "undefined") var house = 0;
							else{
								if (houseval.indexOf(",") != -1) var house = parseInt(houseval.split(',').join('').trim());
								else var house = parseInt(houseval);
							}
							if(linebtn.pressed){
								var line = Math.floor(me.DIST.getBestLength(features.line[features.line.length-1].geometry)[0]*1000)/1000.0;
								if(me.DIST.getBestLength(features.line[features.line.length-1].geometry)[1] == 'km')
									line *= 1000;
								dist += line;
								dist = dist.toPrecision(4);
								if(dist < 1000){
									$("#measure_dist").attr('value', dist);
									$("#measure_dist_units").html('m');
								}else{
									$("#measure_dist").attr('value', dist/1000);
									$("#measure_dist_units").html('km');
								}
							}
							else if(polygonbtn.pressed){
								var polygon = Math.floor(me.AREA.getBestArea(features.polygon[features.polygon.length-1].geometry)[0]*1000)/1000.0;
								if(me.AREA.getBestArea(features.polygon[features.polygon.length-1].geometry)[1] == 'km')
									polygon *= 1000000;
								area = parseFloat(area);
								area += polygon;

								var polygons = features.polygon[features.polygon.length-1].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
								var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+polygons, SaigaiTask.csrfToken);
								ajaxData.doAjax()
								.done(function(data, statusText, jqXHR){
									if('people' in data){
										people += data.people;
									}
									if('house' in data){
										house += data.house;
									}
									$("#measure_people").attr('value', separateComma(people));
									$("#measure_house").attr('value', separateComma(house));
								}).fail(function(jqXHR, statusText, errorThrown){
									console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
								});

							}
							else if (circlebtn.pressed){
								var circle = Math.floor(me.CIRCLE.getBestArea(features.circle[features.circle.length-1].geometry)[0]*1000)/1000.0;
								if(me.CIRCLE.getBestArea(features.circle[features.circle.length-1].geometry)[1] == 'km')
									circle *= 1000000;
								area = parseFloat(area);
								area += circle;

								var circles = features.circle[features.circle.length-1].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
								var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+circles, SaigaiTask.csrfToken);
								ajaxData.doAjax()
								.done(function(data, statusText, jqXHR){
									if('people' in data){
										people += data.people
									}
									if('house' in data){
										house += data.house;
									}
									$("#measure_people").attr('value', separateComma(people));
									$("#measure_house").attr('value', separateComma(house));
								}).fail(function(jqXHR, statusText, errorThrown){
									console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
								});
							}
							area = area.toPrecision(6);
							if(area < 1000000){
								$("#measure_area").attr('value', area);
								$("#measure_area_units").html('m');
							}else{
								$("#measure_area").attr('value', area/1000000);
								$("#measure_area_units").html('km');
							}
							/*$("#measure_people").attr('value', people);
							$("#measure_house").attr('value', house);*/
						},
						"featureremoved" : function(evt){
							dist = 0;
							dist = dist.toPrecision(4);
							area = 0;
							area = area.toPrecision(6);
							$("#measure_dist").attr('value', dist);
							$("#measure_area").attr('value', area);
							$("#measure_people").attr('value', "0");
							$("#measure_house").attr('value', "0");
							features.polygon = [];
							features.circle = [];
							features.line = [];
						},
						"featuremoved" : function(evt) {
							var people = 0;
							var house = 0;
							for(i = 0;i < features.polygon.length;i++){
								var polygons = features.polygon[i].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
								var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+polygons, SaigaiTask.csrfToken);
								ajaxData.doAjax()
								.done(function(data, statusText, jqXHR){
									if('people' in data){
										people += data.people;
									}
									if('house' in data){
										house += data.house;
									}
									$("#measure_people").attr('value', separateComma(people));
									$("#measure_house").attr('value', separateComma(house));
								}).fail(function(jqXHR, statusText, errorThrown){
									console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
								});
							}
							for(i = 0;i < features.circle.length;i++){
								var circles = features.circle[i].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
								var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+circles, SaigaiTask.csrfToken);
								ajaxData.doAjax()
								.done(function(data, statusText, jqXHR){
									if('people' in data){
										people += data.people;
									}
									if('house' in data){
										house += data.house;
									}
									$("#measure_people").attr('value', separateComma(people));
									$("#measure_house").attr('value', separateComma(house));
								}).fail(function(jqXHR, statusText, errorThrown){
									console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
								});
							}
						},
						"featuremodified" : function(evt){
							if(evt.feature.geometry.id.match("LineString")){
								dist = 0;
								for(i = 0;i < features.line.length; i++){
									var line = Math.floor(me.DIST.getBestLength(features.line[i].geometry)[0]*1000)/1000.0;
									if(me.DIST.getBestLength(features.line[i].geometry)[1] == 'km')
										line *= 1000;
									dist = parseFloat(dist);
									dist += line;
									dist = dist.toPrecision(4);
									if(dist < 1000){
										$("#measure_dist").attr('value', dist);
										$("#measure_dist_units").html('m');
									}else{
										$("#measure_dist").attr('value', dist/1000);
										$("#measure_dist_units").html('km');
									}
								}
							}else if(evt.feature.geometry.id.match("Polygon")){
								area = 0;
								for(i = 0;i < features.polygon.length; i++){
									var polygon = Math.floor(me.AREA.getBestArea(features.polygon[i].geometry)[0]*1000)/1000.0;
									if(me.AREA.getBestArea(features.polygon[i].geometry)[1] == 'km')
										polygon *= 1000000;
									area = parseFloat(area);
									area += polygon;
								}
								for(i = 0;i < features.circle.length; i++){
									var circle = Math.floor(me.CIRCLE.getBestArea(features.circle[i].geometry)[0]*1000)/1000.0;
									if(me.CIRCLE.getBestArea(features.circle[i].geometry)[1] == 'km')
										circle *= 1000000;
									area = parseFloat(area);
									area += circle;
								}
								area = area.toPrecision(6);
								if(area < 1000000){
									$("#measure_area").attr('value', area);
									$("#measure_area_units").html('m');
								}else{
									$("#measure_area").attr('value', area/1000000);
									$("#measure_area_units").html('km');
								}
								var people = 0;
								var house = 0;
								for(i = 0;i < features.polygon.length;i++){
									var polygons = features.polygon[i].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
									var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+polygons, SaigaiTask.csrfToken);
									ajaxData.doAjax()
									.done(function(data, statusText, jqXHR){
										if('people' in data){
											people += data.people;
										}
										if('house' in data){
											house += data.house;
										}
										$("#measure_people").attr('value', separateComma(people));
										$("#measure_house").attr('value', separateComma(house));
									}).fail(function(jqXHR, statusText, errorThrown){
										console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
									});
								}
								for(i = 0;i < features.circle.length;i++){
									var circles = features.circle[i].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
									var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+circles, SaigaiTask.csrfToken);
									ajaxData.doAjax()
									.done(function(data, statusText, jqXHR){
										if('people' in data){
											people += data.people;
										}
										if('house' in data){
											house += data.house;
										}
										$("#measure_people").attr('value', separateComma(people));
										$("#measure_house").attr('value', separateComma(house));
									}).fail(function(jqXHR, statusText, errorThrown){
										console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
									});
								}
							}
						}
					});
					me.initdrawlayer = false;
				}
			}else{
				var control = areadraw?this.AREA:this.DIST;
				this._deactivateMeasure();
				var self = this;
				control.activate();
				control.events.on({
					"measurepartial": this.measureCallback,
					"measure": function(event){
						self.measureComplete(event);
					}
				});
			}
		},
		/** ダブルクリックで計測完了時のCallback */
		measureComplete : function(event)
		{

			//計測結果Vectorレイヤにコピー
			this.measureLayer.addFeatures([new OpenLayers.Feature.Vector(event.geometry)]);

			this._deactivateMeasure();
			//ラジオを未選択に
			/*
			$("#measure_type_dist").attr("checked", false);
			$("#measure_type_area").attr("checked", false);
			*/

			var me = this;
			if(!event.geometry.toString().search('LINESTRING')){
				if($("#measure_dist_units").text() == 'km'){
					if(event.units == 'km'){
						//distresult = distresult + Math.floor(event.measure * 1000) / 1000.0;
						distresult = Math.floor(event.measure * 1000) / 1000.0;
					}else{
						//distresult = distresult + Math.floor(event.measure * 1000) / 1000.0 * 0.001;
						distresult = Math.floor(event.measure * 1000) / 1000.0 * 0.001;
					}
					$("#measure_dist_units").html('km');
				}else{
					if(event.units == 'km'){
						//distresult = distresult + Math.floor(event.measure * 1000) / 1000.0 * 1000;
						distresult = Math.floor(event.measure * 1000) / 1000.0 * 1000;
					}else{
						//distresult = distresult + Math.floor(event.measure * 1000) / 1000.0;
						distresult = Math.floor(event.measure * 1000) / 1000.0;
					}
					if(distresult < 1000){
						$("#measure_dist_units").html('m');
					}else{
						distresult = distresult * 0.001;
						$("#measure_dist_units").html('km');
					}
				}
				// 有効桁数4桁にする
				distresult = distresult.toPrecision(4);
				$("#measure_dist").attr('value', distresult);
			}

			if(!event.geometry.toString().search('POLYGON')){

				if(me.featureMode == "AREA" || me.featureMode == "PEOPLE"){
					// 描画完了時に演算する
					var polygons = event.geometry.transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
					var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+polygons, SaigaiTask.csrfToken);
					ajaxData.doAjax()
					.done(function(data, statusText, jqXHR){
						if('people' in data){
							$("#measure_people").attr('value', separateComma(data.people));
						}else{
							$("#measure_people").attr('value', "0");
						}
						if('house' in data){
							$("#measure_house").attr('value', separateComma(data.house));
						}else{
							$("#measure_house").attr('value', "0");
						}
					}).fail(function(jqXHR, statusText, errorThrown){
						console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
					});
				}

				if($("#measure_area_units").text() == 'km'){
					if(event.units == 'km'){
						arearesult = Math.floor(event.measure * 1000000) / 1000000.0;
					}else{
						arearesult = Math.floor(event.measure * 1000000) / 1000000.0 * 1.0e-6;
					}
					$("#measure_area_units").html('km');
				}else{
					if(event.units == 'km'){
						arearesult = Math.floor(event.measure * 1000000) / 1000000.0 * 1.0e+6;
					}else{
						arearesult = Math.floor(event.measure * 1000000) / 1000000.0;
					}
					if(arearesult < 1.0e+6){
						$("#measure_area_units").html('m');
					}else{
						arearesult = arearesult * 1.0e-6;
						$("#measure_area_units").html('km');
					}
				}
				// 有効桁数6桁にする
				arearesult = arearesult.toPrecision(6);
				$("#measure_area").attr('value', arearesult);
			}
		},

		/** 計測を終了 */
		_deactivateMeasure : function()
		{
			var control = this.DIST;
			if (control) {
				control.events.remove("measure");
				control.events.remove("measurepartial");
				control.deactivate();
			}
			var control = this.AREA;
			if (control) {
				control.events.remove("measure");
				control.events.remove("measurepartial");
				control.deactivate();
			}
		},

		/**
		 * 距離・面積計測を行うためのメニューを表示します
		 */
		showMenu: function() {
			var self = this;
			$('#measure').remove();
			$('#content_main').append('<div id="measure" style="display:none;">');
			// 登録フォームが表示されていなければ、計測用のジオメトリを描画し計測
			if(map.components.contentsFormWindow == null || !(!!map.components.contentsFormWindow && map.components.contentsFormWindow.drawToolbar.drawLayer.layer.visibility)){
				$('#measure').load(SaigaiTask.PageURL.baseurl + 'measure/measure/', function(responseText, textStatus, jqXHR){
					if(!(!!map.components.contentsFormWindow && map.components.contentsFormWindow.drawToolbar.drawLayer.layer.visibility)){
						self.onEvent();
					}
				});
			// 登録フォームが表示されていれば登録するジオメトリの計測
			}else{
				$('#measure').load(SaigaiTask.PageURL.baseurl + 'measure/measure/', function(responseText, textStatus, jqXHR) {
					//self.onEvent();
					// ラジオを消す
					$("#buttons").attr("style","display:none")
					// 距離INPUT表示
					$("#measure_dist_span").css('display',"block");
					// 面積INPUT表示
					$("#measure_area_span").css('display',"block");
					// 人口INPUT表示
					$("#measure_people_span").css('display',"block");
					// 計測結果の初期化(距離は有効数字4桁、面積は有効数字6桁)
					dist = 0;
					dist = dist.toPrecision(4);
					area = 0;
					area = area.toPrecision(6);
					$("#measure_dist").attr('value', dist);
					$("#measure_area").attr('value', area);
					$("#measure_people").attr('value', "0");
					$("#measure_house").attr('value', "0");

					if(!this.DIST){

						var style = new OpenLayers.Style();
						style.addRules([new OpenLayers.Rule({symbolizer:{
							"Point": {pointRadius: 4,graphicName: "square",fillColor: "#FFFFFF",fillOpacity: 1,strokeWidth: 1,strokeOpacity: 1,strokeColor: "#333333"},
							"Line": {strokeWidth: 3,strokeOpacity: 1,strokeColor: "#FF0000",strokeDashstyle: "dash"},
							"Polygon": {strokeWidth: 2,strokeOpacity: 1,strokeColor: "#FF0000",fillColor: "#FFFFFF",fillOpacity: 0.3}
						}})]);
						var measureOptions = {handlerOptions: {style:"default", layerOptions:{styleMap:new OpenLayers.StyleMap({"default":style})}, persist:true }};
						var me = this;
						me.DIST = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, measureOptions);
						me.DIST.geodesic = true;
						map.map.addControl(me.DIST);
						me.AREA = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon, measureOptions);
						me.AREA.geodesic = true;
						map.map.addControl(me.AREA);
						me.CIRCLE = new OpenLayers.Control.Measure(OpenLayers.Handler.RegularPolygon, {
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
						});
						me.CIRCLE.geodesic = true;
						map.map.addControl(me.CIRCLE);
					}
					var contentsFormWindow = map.components.contentsFormWindow;
					var drawToolbar = contentsFormWindow.drawToolbar;
					var features = drawToolbar.drawLayer.drawFeatures;
					var dist = 0;
					var area = 0;
					var people = 0;
					var house = 0;
					for(var i=0; i<features.line.length;i++){
						var line = Math.floor(me.DIST.getBestLength(features.line[i].geometry)[0]*1000)/1000.0;
						if(me.DIST.getBestLength(features.line[i].geometry)[1] == 'km')
							line *= 1000;
						dist += line;
					}
					dist = dist.toPrecision(4);
					if(dist < 1000){
						$("#measure_dist").attr('value', dist);
						$("#measure_dist_units").html('m');
					}else{
						$("#measure_dist").attr('value', dist/1000);
						$("#measure_dist_units").html('km');
					}
					for(var i=0; i< features.polygon.length;i++){
						var polygon = Math.floor(me.AREA.getBestArea(features.polygon[i].geometry)[0]*1000000)/1000000.0;
						if(me.AREA.getBestArea(features.polygon[i].geometry)[1] == 'km')
							polygon *= 1000000;
						area += polygon;

						var polygons = features.polygon[i].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
						var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+polygons, SaigaiTask.csrfToken);
						ajaxData.doAjax()
						.done(function(data, statusText, jqXHR){
							if('people' in data){
								people += data.people
							}
							if('house' in data){
								house += data.house;
							}
							$("#measure_people").attr('value', separateComma(people));
							$("#measure_house").attr('value', separateComma(house));
						}).fail(function(jqXHR, statusText, errorThrown){
							console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
						});
					}
					for(var i=0; i< features.circle.length;i++){
						var circle = Math.floor(me.CIRCLE.getBestArea(features.circle[i].geometry)[0]*1000000)/1000000.0;
						if(me.CIRCLE.getBestArea(features.circle[i].geometry)[1] == 'km')
							circle *= 1000000;
						area += circle;

						var circles = features.circle[i].geometry.clone().transform(new OpenLayers.Projection("EPSG:"+map.epsg), new OpenLayers.Projection("EPSG:4326")).toString();
						var ajaxData = new GetDatas(SaigaiTask.contextPath+"/page/decisionsupport/JSON_WKTAreaPeople", "wkts="+circles, SaigaiTask.csrfToken);
						ajaxData.doAjax()
						.done(function(data, statusText, jqXHR){
							if('people' in data){
								people += data.people
							}
							if('house' in data){
								house += data.house;
							}
							$("#measure_people").attr('value', separateComma(people));
							$("#measure_house").attr('value', separateComma(house));
						}).fail(function(jqXHR, statusText, errorThrown){
							console.log("JSON_BBOXAreaPeople is error! StatusCode=" + jqXHR.status);
						});
					}
					area = area.toPrecision(6);
					if(area < 1000000){
						$("#measure_area").attr('value', area);
						$("#measure_area_units").html('m');
					}else{
						$("#measure_area").attr('value', area/1000000);
						$("#measure_area_units").html('km');
					}
					// ここだけ対応すれば良い？？
					$("#measure_people").attr('value',separateComma(people));
					$("#measure_house").attr('value',separateComma(house));

				});
			}

			$('#buttons').css('display', 'none');
			$('#measure').dialog({
				title: lang.__("Measure distance and area"),
				position: {
				    my: "top",
				    at: "top",
				    of: window
				},
				buttons: [{
					text: lang.__("Close"),
					click: function() {
						self.stopMeasure();
						self.offEvent();
						$('#measure').dialog('destroy');
						$('#measure').remove();
						return false;
					}
				}],
				close: function(){
					self.stopMeasure();
					self.offEvent();
					$('#measure').dialog('destroy');
					$('#measure').remove();
				}
			});
		}
}

// Ajax用クラス
function GetDatas(){
	this.initialize.apply(this, arguments);
}
GetDatas.prototype = {
	initialize : function(url, param, token){
		this.url = url;
		this.param = param;
		this.token = token;
	},
	doAjax : function(){
		var jqXHR = $.ajax({
			url : this.url,
			headers: {"X-CSRF-Token":this.token},
			dataType : "json",
			data : this.param,
			contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
		return jqXHR.promise();
	}
}

/*
 * 数値のカンマ区切り(正規表現)
 */
function separateComma(num){
	return String(num).replace( /(\d)(?=(\d\d\d)+(?!\d))/g, '$1,');
}