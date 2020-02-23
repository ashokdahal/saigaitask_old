/**
 * OpenStreetMap base layer
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.OSMLayer = new OpenLayers.Class(OpenLayers.Layer.OSM, {
	/**
	 * OSM layer information
	 */
	layerInfo: null,

	/**
	 *
	 * @param layerInfo referenceLayerInfo
	 */
	initialize: function(layerInfo) {
		// 引数を処理
		var me = this;
		me.layerInfo = layerInfo;

		var url = layerInfo.wmsURL;
		if(!url.match(".*\\$\{z\}\/\\$\{x\}\/\\$\{y\}\.png$")){
			if(!url.match(".*/$")) url += "/";
			url += "${z}/${x}/${y}.png";
		}
		var options = {
			visibility: layerInfo.visibility,
			buffer: 0,
			alpha: true,
			opacity: 1.0,
			isBaseLayer: true
		};

		// WMS layer initialization
		OpenLayers.Layer.OSM.prototype.initialize.apply(this, [layerInfo.name, url, options]);
	}
});

SaigaiTask.Map.Layer.OSMLayer.type = SaigaiTask.Map.Layer.Type.OSM;
