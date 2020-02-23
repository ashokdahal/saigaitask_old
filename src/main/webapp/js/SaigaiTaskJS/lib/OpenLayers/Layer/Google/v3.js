/* Copyright (c) 2006-2013 by OpenLayers Contributors (see authors.txt for
 * full list of contributors). Published under the 2-clause BSD license.
 * See license.txt in the OpenLayers distribution or repository for the
 * full text of the license. */

/**
 * Google Maps Javascript API の APIバージョン 3.20 が削除された影響で、
 * Googleマップを初期表示の背景地図とすると表示されない不具合の対応。
 * @see https://github.com/openlayers/openlayers/issues/1450
 */
OpenLayers.Layer.Google.v3.setGMapVisibility = function(visible) {
	var cache = OpenLayers.Layer.Google.cache[this.map.id];
	var map = this.map;
	if (cache) {
		var type = this.type;
		var layers = map.layers;
		var layer;
		for (var i=layers.length-1; i>=0; --i) {
			layer = layers[i];
			if (layer instanceof OpenLayers.Layer.Google &&
						layer.visibility === true && layer.inRange === true) {
				type = layer.type;
				visible = true;
				break;
			}
		}
		var container = this.mapObject.getDiv();
		if (visible === true) {
			if (container.parentNode !== map.div) {
				// begin of issues 1450 
				if (!cache.rendered) {
					var me = this;
					google.maps.event.addListenerOnce(this.mapObject, 'tilesloaded', function() {
						cache.rendered = true;
						me.setGMapVisibility(me.getVisibility());
						me.moveTo(me.map.getCenter());
						cache.googleControl.appendChild(map.viewPortDiv);
					});
				} else {
					cache.googleControl.appendChild(map.viewPortDiv);
				}
				map.div.appendChild(container);
				google.maps.event.trigger(this.mapObject, 'resize');
				// end of issues 1450 
			}
			this.mapObject.setMapTypeId(type);
		} else if (cache.googleControl.hasChildNodes()) {
			map.div.appendChild(map.viewPortDiv);
			map.div.removeChild(container);
		}
	}
};
