/**
 * TileCacheレイヤ
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.TileCacheLayer = new OpenLayers.Class(OpenLayers.Layer.TileCache, {
	/**
	 * OSMレイヤ情報
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

		var options = {
			visibility: layerInfo.visibility,
			buffer: 0,
			alpha: true,
			opacity: 1.0,
			isBaseLayer: true
		};

		options.format = layerInfo.wmsFormat;
		OpenLayers.Layer.TileCache.prototype.initialize.apply(this, [layerInfo.name, layerInfo.wmsURL, layerInfo.featuretypeId, options]);
	}
});

SaigaiTask.Map.Layer.TileCacheLayer.type = SaigaiTask.Map.Layer.Type.TILECACHE;
