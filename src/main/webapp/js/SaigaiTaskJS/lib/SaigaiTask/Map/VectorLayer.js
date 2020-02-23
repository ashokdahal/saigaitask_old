/**
 * 
 * @requires SaigaiTask/Map.js
 */
SaigaiTask.Map.VectorLayer = function(){
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.VectorLayer.prototype = {

	/**
	 * @type {SaigaiTask.Map}
	 */
	map: null,

	/**
	 * eコミマップの地図ID
	 * @type {Number}
	 */
	mapId: null,

	/**
	 * eコミマップでのレイヤID
	 * @type {String}
	 */
	layerId: null,

	/**
	 * 避難所ベクタレイヤ
	 * @type {OpenLayers.Layer.Vector}
	 */
	layer: null,

	/**
	 * 選択フィーチャの配列
	 * @type {Array<Number>}
	 */
	selectedFeatureIds: [],

	/**
	 * コンストラクタに渡したオプション
	 */
	options: null,

	/**
	 * 検索実行時のjQuery.ajaxのオプション
	 * 前回の検索Ajaxのオプションを保存.
	 * @type {Object}
	 */
	option: null,

	/**
	 * OpenLayersのイベントクラス
	 * @type {OpenLayers.Events}
	 */
	events: null,

	/**
	 * Mapクラスのイベントタイプ
	 * @type {Object<String, String>}
	 */
	EventType: {
		/** 選択前 */
		beforeselected: "beforeselected"
	},

	/**
	 * コンストラクタ
	 * @param {SaigaiTask.Map} map
	 * @param {Object<String, *>} options オプション
	 * @param {String} options.sld スタイルオプション
	 */
	initialize: function(map, options) {
		var me = this;
		me.map = map;
		var olmap = map.map;

		// オプションの保存
		me.options = options;
		me.mapId = options.mapId;
		var layerId = me.layerId = options.layerId;

		// WMSの方は非表示にする
		map.switchContentsLayer(layerId, false);
		map.setLayerSearchable(layerId, true);

		// レイヤの準備
		var layer = me.layer = new OpenLayers.Layer.Vector(me.layerId + " Vector Layer", {
			projection: olmap.displayProjection,
			preFeatureInsert: function(feature) {
				feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:"+map.epsg));
			}
		});
		olmap.addLayer(layer);

		// SLD
		var sldXML = options.sld;
		if(sldXML!=null) {
			var sldFormat = new OpenLayers.Format.SLD();
			var sld = sldFormat.read(sldXML);
			for(var l in sld.namedLayers){
				var styles = sld.namedLayers[l].userStyles;
				me.layer.styleMap = styles[0];
			}
		}

		// styleMap
		var styleMap = options.styleMap;
		if(styleMap!=null) {
			me.layer.styleMap = styleMap;
		}

		// フィーチャをhoverしたときのスタイル
		var highlightFeature = me.highlightFeatureControl = new OpenLayers.Control.SelectFeature(layer, {
			hover: true,
			highlightOnly: true,
			renderIntent: "temporary",
			eventListeners: {
				featurehighlighted: function(e) {
				},
				featureunhighlighted: function(e) {
				}
			}
		});
		map.addControl(highlightFeature);

		// フィーチャをクリックしたときのスタイル
		var selectFeature = me.selectFeatureControl = new OpenLayers.Control.SelectFeature(layer, {
			clickout: false,
			toggle: true,
			multiple: true,
			toggleKey: "ctrlKey",
			multipleKey: "shiftKey",
			renderIntent: "select",
			eventListeners: {
				featurehighlighted: function(e) {
					var feature = e.feature;
					var attributes = feature.attributes;
					me.select(attributes);
				},
				featureunhighlighted: function(e) {
					var feature = e.feature;
					var attributes = feature.attributes;
					var id = attributes.id;
					// テーブルから削除する
					me.remove(id);
				}
			}
		});
		map.addControl(selectFeature);

		me.events = new OpenLayers.Events(me);
	},

	load: function(json) {
		var me = this;
		var map = me.map;
		var layer = me.layer;
		var layerId = me.layerId;

		var count = json.count;
		layer.removeAllFeatures();
		if(0<count) {
			// フィーチャの追加
			var records = json.records;
			var recordsIdx = null, record, wkt;
			var wktFormat = new OpenLayers.Format.WKT(), features = [], feature;
			var filter = [];
			for(recordsIdx in records) {
				record = records[recordsIdx];
				// フィーチャ
				wkt = record.wkt;
				if(wkt!=null) {
					feature = wktFormat.read(wkt);
					feature.attributes = record;
					features.push(feature);
					// フィルタするフィーチャID
					filter.push(record.featureid);
				}
			}
			layer.addFeatures(features);
			// 選択済みフィーチャは選択済みで表示する
			for(var selectedFeatureIdsIdx in me.selectedFeatureIds) {
				var selectedFeatureId = me.selectedFeatureIds[selectedFeatureIdsIdx];
				var feature = me.getFeature(selectedFeatureId);
				if(feature!=null) {
					me.selectFeatureControl.select(feature);
				}
			}
			// フィルタの更新
			if(map.searchFilter==null) map.searchFilter={};
			map.searchFilter[layerId] = filter;
			// レイヤのZ-Indexを更新する
			map.toFront(layer);
		}
	},

	search: function(formSelector) {
		var me = this;
		var form = $(formSelector);
		var option = {};
		option.url = form.attr("action");
		option.type = form.attr("method");
		option.data = SaigaiTask.Map.util.jQueryUtil.getFormParams(formSelector).join("&"); // パラメータ文字列 name=value&name=value
		option.async = true;
		option.success = function(json) {
			me.load(json);
		};
		me.option = option;
		$.ajax(option);
	},

	research: function() {
		var me = this;
		$.ajax(me.option);
	},

	/**
	 * 属性情報で選択済みにします.
	 */
	select: function(attributes) {
		var me = this;

		var cont = me.events.triggerEvent(me.EventType.beforeselected, {
			attributes: attributes
		});
		if(cont==false) return;

		// 選択済みフィーチャの配列に登録
		var id = attributes.id;
		if(jQuery.inArray(id, me.selectedFeatureIds)==-1) {
			me.selectedFeatureIds.push(id);
		}

		// フィーチャがあれば選択状態にする
		var feature = me.getFeature(id);
		if(feature!=null) {
			if(me.isSelected(feature)==false) {
				me.selectFeatureControl.select(feature);
			}
		}
	},

	selectByGeometry: function(geometry) {
		var me = this;
		if(geometry!=null) {
			var features = me.layer.features;
			for(var featuresIdx in features) {
				var feature = features[featuresIdx];
				var point = feature.geometry.getCentroid();
				if(point.intersects(geometry)) {
					var attributes = feature.attributes;
					me.select(attributes);
				}
			}
		}
	},

	unselect: function(id) {
		var me = this;

		// 選択済みID配列から削除する
		var index = jQuery.inArray(id, me.selectedFeatureIds);
		if(index!=-1) {
			me.selectedFeatureIds.splice(index, 1);
		}

		// フィーチャがあれば未選択にする
		var feature = me.getFeature(id);
		if(feature!=null) {
			me.selectFeatureControl.unselect(feature);
		}
	},

	/**
	 * 選択済みから外します.
	 * @param {Number} id ID
	 */
	remove: function(id) {
		var me = this;

		// 選択済みID配列から削除する
		var index = jQuery.inArray(id, me.selectedFeatureIds);
		if(index!=-1) {
			me.selectedFeatureIds.splice(index, 1);
		}
	},

	/**
	 * レイヤからID指定でフィーチャを取得します.
	 * @param {Number} id ID
	 * @return {OpenLayers.Feature.Vector}
	 */
	getFeature: function(id) {
		var me = this;
		var features = me.layer.getFeaturesByAttribute("id", id);
		var feature = null;
		if(0<features.length) {
			feature = features[0];
		}
		return feature;
	},

	/**
	 * 指定フィーチャが選択済みかどうかチェックします.
	 * @return {Boolean}
	 */
	isSelected: function(feature) {
		var me = this;
		var selectedFeatures = me.layer.selectedFeatures;
		return jQuery.inArray(feature, selectedFeatures)!=-1;
	}
};

