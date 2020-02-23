/**
 * レイヤグループ
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.Group = new OpenLayers.Class({

	/**
	 * レイヤ情報
	 * @type {SaigaiTask.Map.Layer.LayerInfo}
	 */
	layerInfo: null,

	/**
	 *
	 * @param {SaigaiTask.Map.Layer.LayerInfo} layerInfo レイヤ情報
	 */
	initialize: function(layerInfo) {

		// 引数を処理
		var me = this;
		me.layerInfo = layerInfo;

		// レイヤはなし
		layerInfo.layer = null;
	}
});

SaigaiTask.Map.Layer.Group.type = SaigaiTask.Map.Layer.Type.GROUP;