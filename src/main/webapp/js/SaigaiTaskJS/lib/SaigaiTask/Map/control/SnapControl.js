/**
 * This is a class to control the snap layer.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.SnapControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * @type {OpenLayers.Control.Snapping}
	 */
	snap: null,

	wfsLayers: null,

	initialize: function(stmap) {
		var me = this;
		me.stmap = stmap;

		// eコミ情報がなければ初期化終了する
		if(stmap.ecommaps.length==0) return;
		var ecommap = stmap.ecommaps[0];
		var contentsLayerInfos = ecommap.contentsLayerInfos;
		var wfsLayers = [];
		var wfsLayer = null;

		// スナップ用スタイル
		// 塗りつぶしなしで線の色もなし
		var styleMap = new OpenLayers.StyleMap({
			fillOpacity: 0,
			strokeOpacity: 0
		});

		// スナップ対象レイヤがあればWFSレイヤとして追加する.
		for(var idx in contentsLayerInfos) {
			var layerInfo = contentsLayerInfos[idx];
			var mapInfo = ecommap.mapInfo;
			var isSnappableLayer = layerInfo.snappable;
			if(isSnappableLayer) {
				// WFSレイヤを作成
				wfsLayer = stmap.addContentsWFSLayer(mapInfo.communityId, mapInfo.mapId, layerInfo.layerId, null, {
					styleMap: styleMap
				});
				wfsLayers.push(wfsLayer);
			}
		}
		me.wfsLayers = wfsLayers;

		// configure the snapping agent
		var targets = [];
		for(var wfsLayersIdx in wfsLayers) {
			// スナップ対象レイヤに追加
			wfsLayer = wfsLayers[wfsLayersIdx];
			targets.push({
				layer: wfsLayer,
				tolerance: 10
			});
		}
		snap = me.snap = new OpenLayers.Control.Snapping({
			layer : null,
			targets: targets
		});
		stmap.addControl(snap);
		snap.activate();
	},

	/**
	 * スナップ編集用レイヤを設定する
	 * @param layer
	 */
	setLayer: function(layer) {
		var me = this;
		var snap = me.snap;
		if(snap!=null) {
			snap.setLayer(layer);
		}
	},

	/**
	 * 終了処理
	 */
	destroy: function() {
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;
		// スナップWFSレイヤをすべて取り除く
		var wfsLayers = me.wfsLayers;
		for(var idx in wfsLayers) {
			var wfsLayer = wfsLayers[idx];
			olmap.removeLayer(wfsLayer);
			wfsLayer.destroy();
		}
	}
});
