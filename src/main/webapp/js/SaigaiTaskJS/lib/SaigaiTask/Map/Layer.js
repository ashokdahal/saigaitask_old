/**
 * SaigaiTask.Map.Layerパッケージ
 * @requires SaigaiTask/Map.js
 */
SaigaiTask.Map.Layer = {
	/**
	 * Search for a layer class that matches the layer type of the layer information and generate a layer.
	 * If there is no corresponding layer, generate it in the WMS layer
	 */
	newLayerFromLayerInfo: function(layerInfo) {
		var layer = null;
		if(layerInfo) {
			// Generate a class corresponding to the layer type if there is one
			var type = layerInfo.type;
			for(var key in SaigaiTask.Map.Layer) {
				// Layer class check
				if(typeof SaigaiTask.Map.Layer[key]=="function" && typeof SaigaiTask.Map.Layer[key].type!="undefined") {
					// Array for multiple correspondence
					var types = SaigaiTask.Map.Layer[key].type;
					if($.isArray(SaigaiTask.Map.Layer[key].type)==false) types = [SaigaiTask.Map.Layer[key].type];
					for(var typesIdx in types) {
						// Check layer type
						if(type==types[typesIdx]) {
							// Create a layer by passing layerInfo as an argument
							layer = new SaigaiTask.Map.Layer[key](layerInfo);
							break;
						}
					}
				}
			}
			// If you cannot find the layer type
			// Generate WMS layer
			if(layer==null) {
				layer = new SaigaiTask.Map.Layer.WMSLayer(layerInfo);
			}
		}
		return layer;
	}
};