/**
 * Google Layer
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.GoogleLayer = new OpenLayers.Class(OpenLayers.Layer.Google, {
	layerInfo: null,
	initialize: function(layerInfo) {
		this.layerInfo = layerInfo;
		var options = {
			reprojection: true,
			isBaseLayer: true,
			buffer: 0,
			opacity: layerInfo.opacity,
			alpha: true,
			visibility: true,
			attribution: layerInfo.attribution
		};

		if("SATELLITE"==layerInfo.featuretypeId){
			options.type = google.maps.MapTypeId.SATELLITE;
		} else if("HYBRID"==layerInfo.featuretypeId){
			options.type = google.maps.MapTypeId.HYBRID;
		} else if("PHYSICAL"==layerInfo.featuretypeId || "TERRAIN"==layerInfo.featuretypeId){
			options.type = google.maps.MapTypeId.TERRAIN;
		}
		options.sphericalMercator = true;

		var name = "Google_"+layerInfo.featuretypeId;
		OpenLayers.Layer.Google.prototype.initialize.apply(this, [name, options]);
	}
});

SaigaiTask.Map.Layer.GoogleLayer.type = SaigaiTask.Map.Layer.Type.GOOGLE;
