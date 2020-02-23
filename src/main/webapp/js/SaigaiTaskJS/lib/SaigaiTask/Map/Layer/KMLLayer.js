/**
 * KMLレイヤ
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 * requires OpenLayers.Format.KMLStyle.js
 */
SaigaiTask.Map.Layer.KMLLayer = new OpenLayers.Class(OpenLayers.Layer.Vector, {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * KMLレイヤ情報
	 * @type {SaigaiTask.Map.Layer.LayerInfo}
	 */
	layerInfo: null,

	/**
	 * @type {OpenLayers.Format.KMLStyle}
	 */
	kmlFormat: null,

	/**
	 * @type {OpenLayers.Control.SelectFeature}
	 */
	kmlSelectControl: null,

	/**
	 * リロード秒数
	 * default 0
	 * @type {Number}
	 */
	reloadSec: 0,

	/**
	 * リロードのタイマーID
	 * @type {Number}
	 */
	reloadTimerId: null,

	/**
	 *
	 * @param layerInfo kmlLayerInfo
	 */
	initialize: function(layerInfo) {
		// 引数を処理
		var me = this;
		me.layerInfo = layerInfo;
		var ecommapInfo = layerInfo.ecommap;
		var stmap = me.stmap = ecommapInfo.stmap;
		me.kmlSelectControl = stmap.controls["kmlSelectControl"];

		var options = {
			visibility: layerInfo.visibility,
			attribution: layerInfo.attribution
		};

		// KMLフォーマットの初期化
		var wgs84 = new OpenLayers.Projection("EPSG:4326");
		var internalProjection = wgs84;
		if(!!stmap.epsg) internalProjection = new OpenLayers.Projection("EPSG:"+stmap.epsg);
		me.kmlFormat = new OpenLayers.Format.KMLStyle({
			extractStyles: true,
			internalProjection: internalProjection
		});

		// Vectorレイヤの初期化
		OpenLayers.Layer.Vector.prototype.initialize.apply(this, [layerInfo.name, options]);

		// リロード秒数の取得
		var layerInfo = me.layerInfo;
		if(layerInfo.reload!=null) {
			try {
				var reloadJson = JSON.parse(layerInfo.reload);
				if($.isNumeric(reloadJson.sec)) {
					me.reloadSec = reloadJson.sec;
				}
			} catch (e) {
				logger.warn(e);
			}
		}

		//<span class="ja">初回読み込み</span><span class="en">Load for the first time</span>
		// 初期表示で、キャッシュを読まないようにする
		if (me.visibility) {
			me.loadKMLFeature({
				data: {
					reload: true
				}
			});
		}

		// イベントハンドラの初期化
		me.events.on({
			"visibilitychanged": function() {
				if(me.getVisibility()) {
					// 強制リロード
					me.loadKMLFeature({
						data: {
							reload: true
						}
					});
				}
			},
			"featureselected": function(e) {
				me.kmlPopup = new SaigaiTask.Map.view.KMLPopup(me.stmap);
				me.kmlPopup.show(e.feature, layerInfo);
				me.kmlSelectControl.unselectAll();
			}
		});

		layerInfo.layer = this;
		me.stmap.map.addLayer(me);
		// クリック可能なら SelectControl に追加
		if(layerInfo.searchable) {
			var layers = [me].concat(me.kmlSelectControl.layers);
			me.kmlSelectControl.setLayer(layers)
			if(!me.kmlSelectControl.active) me.kmlSelectControl.activate();
		}
	},

	loadKMLFeature: function(option) {
		//<span class="ja">URLからKML読み込み</span><span class="en">Load KML from URL</span>
		//var url = "/map/kml?mid="+this.mapId+"&kml="+layerInfo.layerId+(reloadSec>0?"&sec="+reloadSec:"")+"&"+(Math.floor(new Date().getTime()/1000));
		//var url = this.kmlURL+"mid="+this.mapId+"&url="+escape(kmlUrl)+(reloadSec>0?"&sec="+reloadSec:"")+"&"+(Math.floor(new Date().getTime()/1000));

		var me = this;
		var kmlLayer = me;

		var data = {
			url: me.layerInfo.wfsURL
		};
		if(0<me.reloadSec) {
			data.reloadSec = me.reloadSec;
		}

		// 引数の値を優先してセット
		if(!!option) {
			if(!!option.data) {
				$.extend(data, option.data);
			}
		}

		jQuery.ajax({
			url: SaigaiTask.contextPath+"/page/map/kml/",
			async: true,
			dataType: "text",
			data: data,
			cache: false
		}).done(function(data) {
			if (data) {
				var features = me.kmlFormat.read(data);
				kmlLayer.removeAllFeatures();
				kmlLayer.addFeatures(features);
				for (var i=0; i<features.length; i++) {
					var feature = features[i];
					var style = feature.style;
					if(!!style) {
						style.cursor = "pointer";
						//<span class="ja">テキストの場合はfontColorが設定されている</span><span class="en">Set fontColor in case of text</span>
						if (style.fontColor) {
							style.label = feature.attributes.name;
							style.fontWeight = "bold";
							style.labelAlign = "lt";
						}
					}
				}
				kmlLayer.redraw();
			}
		}).fail(function(xhr, textStatus, errorThrown) {
			alert(lang.__("Failed to get {0}", me.name));
		}).complete(function(xhr, textStatus) {
		//
		//<span class="ja">リロード指定時</span><span class="en">When identifying reload</span>
			if (me.reloadTimerId==null) {
			// リロードインターバルの起動
			if (me.reloadSec > 0) {
					me.reloadTimerId = setTimeout(function() {
						me.reloadTimerId = null;
					if (me.visibility) me.loadKMLFeature();
				}, me.reloadSec*1000);
			}
		}
		});

	},

	/**
	 * @see eMapBase.openKMLPopup
	 */
	openKMLPopup: function(feature, layerInfo) {
		var me = this;
		var stmap = me.stmap;
		if(stmap.popupManager!=null) {
			stmap.popupManager.closeAll();
		}

		try {
			var self = this;
			var lonlat = feature.geometry.getBounds().getCenterLonLat().clone();
			//<span class="ja">Featureは地図座標なので一旦緯度経度に戻す</span>
			//<span class="en">Because feature is map coordinate, return to longtitude-latitude once </span>
			if (me.stmap.epsg!=4326) lonlat.transform(me.stmap.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));

			var popupDiv = document.createElement('div');
			var div, span;

			//Attrs
			div = document.createElement('div');
			div.className = "popup_attr";
			//<span class="ja">aタグはtargetとhrefのみにする</span><span class="en">Set only 'target' and 'href' for 'A' tag</span>
			if (feature.attributes.description) {
				div.innerHTML = (feature.attributes.name ? "<h3>"+SaigaiTask.Map.util.CommonUtil.escapeXml(feature.attributes.name)+"</h3>" : "")
					+feature.attributes.description.replace(/<\s*a\s+/ig, '<a target="_blank" ').replace(/onclick\s*=\s*"[^"]*"/ig,'');
				//attrDiv.style.width = w+"px";
			} else if (feature.attributes.name) {
				div.innerHTML = "<h3>"+SaigaiTask.Map.util.CommonUtil.escapeXml(feature.attributes.name)+"</h3>";
			}

			try {
				var tbody = null;
				var td;
				for (var key in feature.attributes) {
					if (key != "name" && key != "description" && key != "styleUrl") {
						if (feature.attributes[key].value) {
							if (!tbody) tbody = document.createElement('tbody');
							var attrTr = document.createElement('tr');
							td = document.createElement('td');
							attrTr.appendChild(td);
							td.innerHTML = key;
							td = document.createElement('td');
							td.innerHTML = feature.attributes[key].value;
							attrTr.appendChild(td);
							tbody.appendChild(attrTr);
						}
					}
				}
				if (tbody) {
					table = document.createElement('table');
					table.cellSpacing = 0;
					table.cellPadding = 0;
					table.appendChild(tbody);
					div.appendChild(table);
				}
			} catch (e) {}

			popupDiv.appendChild(div);

			// DIV用のパネル（ヘッダーなし）
			var panel = Ext.create('Ext.panel.Panel', {
				width: 200,
				contentEl: popupDiv
			});

			// ExtPopupのオプション
			var option = {
				map: me.stmap,
				olmap: me.stmap.map,
				center: lonlat,
				items: [panel],
				title: layerInfo.name,
				pinned: false
			};
			var popup = SaigaiTask.Map.view.Popup.showExtPopup(option);
		} catch (e) { console.error(e); }
	},

	CLASS_NAME: "SaigaiTask.Map.Layer.KMLLayer"
});

SaigaiTask.Map.Layer.KMLLayer.type = SaigaiTask.Map.Layer.Type.KML;
