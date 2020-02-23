/**
 * XYZ Layer
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.XYZLayer = new OpenLayers.Class(OpenLayers.Layer.XYZZoom, {
	layerInfo: null,
	initialize: function(layerInfo) {
		this.layerInfo = layerInfo;
		var options = {
			reprojection: true,
			isBaseLayer: layerInfo.type==SaigaiTask.Map.Layer.Type.BASE_XYZ,
			buffer: 0,
			opacity: layerInfo.opacity,
			alpha: true,
			visibility: layerInfo.visibility,
			attribution: layerInfo.attribution
		};

		options.maxZoomLevel = layerInfo.maxZoomLevel;

		OpenLayers.Layer.XYZZoom.prototype.initialize.apply(this, [layerInfo.name, layerInfo.wmsURL, options]);

		layerInfo.layer = this;
	}
});

SaigaiTask.Map.Layer.XYZLayer.type = [SaigaiTask.Map.Layer.Type.BASE_XYZ, SaigaiTask.Map.Layer.Type.OVERLAY_XYZ, SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_XYZ]
