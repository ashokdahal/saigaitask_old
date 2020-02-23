/**
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.SessionLayout = new OpenLayers.Class(OpenLayers.Control, {

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
	 * AJAXリクエストのアボート用
	 */
	xhr: null,

	initialize: function(stmap, options) {
		var me = this;
		me.stmap = stmap;

		// 表示状態をサーバに保存するイベントの初期化.
		var handler = function(evt){
			me.saveLayout();
		};
		stmap.events.on({
			"legenditemexpanded": handler,
			"legendcheckchange": handler
		});
		stmap.map.events.on({
			"zoomend": handler,
			"moveend": handler
		});

		OpenLayers.Control.prototype.initialize.apply(this, [options]);
	},

	/**
	 * 表示状態をサーバから取得する.
	 */
	loadLayout: function(callback, blayer){
		if(this.active) {
			var sessionLayout = this;
			var me = this.stmap;
			var map = me.map;
			var data = {
				mapDivId: me.div
			};
			me.api.loadLayout(data, function(layout) {
				if(layout!=null){
					// FIXME: eコミのサイト投影法をメルカトルからWGS84に変更した際にセッションに保存したレイアウトをロードすると地図が真っ白になる不具合を修正する
					// ズーム
					var zoom = layout.zoom;
					if(typeof zoom!="undefined") {
						map.zoomTo(zoom);
					}
					// center
					if(typeof layout.center!="undefined") {
						var center = new OpenLayers.LonLat.fromString(layout.center);
						me.setCenter(center);
					}
					// 凡例
					if(typeof layout.layers!="undefined" && blayer) {
						// 読み込んだプロパティを設定
						var layerMap = me.getLayerMap();
						var idx = null;
						for(idx in layout.layers) {
							// TODO* 現状の構造に合わせて変更する
							var layerLayout = layout.layers[idx];
							var layerId = layerLayout.id;
							var layer = layerMap[layerId];
							if(layer!=null) {
								layer.expanded = layerLayout.expanded;
								layer.visibility = layerLayout.visibility;
								layer.searchable = layerLayout.visibility;
							}
						}
						me.redrawContentsLayer(0);
					}
				}
				sessionLayout.events.triggerEvent("layoutloaded");
				if(typeof callback=="function") callback();
			});
		}
		else {
			if(typeof callback=="function") callback();
		}
	},

	/**
	 * 表示状態をサーバに保存する.
	 */
	saveLayout: function(){
		if(this.active!=true) return;
		var me = this.stmap;
		var map = me.map;
		// 送信中のリクエストがあれば中断する
		if(this.xhr!=null) this.xhr.abort();
		var allLayers = me.getAllLayers();
		var layers = [];
		for(var allLayersIdx in allLayers) {
			var layer = allLayers[allLayersIdx];
			layers[layers.length] = {
				id: layer.layerId,
				expanded: layer.expanded==true,
				visibility: layer.visibility
			};
		}
		// リクエストを送信する
		var data = {
			mapDivId: me.div,
			"zoom": map.zoom,
			"center": map.getCenter().clone().transform(map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326")).toShortString(),
			"layers": layers
		};
		this.xhr = me.api.saveLayout(data);
	}
});