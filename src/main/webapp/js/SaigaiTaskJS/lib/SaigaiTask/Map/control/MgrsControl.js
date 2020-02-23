/**
 * UTMグリッドレイヤを管理するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.MgrsControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * MGRSグリッドのレイヤ情報
	 * @type {SaigaiTask.Map.Layer.LayerInfo}
	 */
	layerInfo: null,

	/**
	 * MGRSグリッドのレイヤ
	 * @type {OpenLayers.Layer}
	 */
	layer: null,

	/**
	 * MGRSコードの桁数の自動決定モード
	 * @type {Boolean}
	 */
	autoPrecision: true,

	/**
	 * 桁数からズームレベルの相互変換のときに、
	 * 納まるグリッド数のパラメータ
	 * @type {Number}
	 */
	// 地図表示領域に１マスが収まらなくなったら次のケタ
	//AUTO_PRECISION_GRID_NUM: 1.0,
	// 地図表示領域に１マスの６割が収まらなくなったら次のケタ
	AUTO_PRECISION_GRID_NUM: 0.6,

	initialize: function(stmap) {
		var me = this;
		me.stmap = stmap;

		// レイヤ情報
		var layerInfo = me.layerInfo = new SaigaiTask.Map.Layer.LayerInfo({
			layerId: "mgrs",
			type: SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS, // 印刷用
			name: lang.__("UTM grid"),
			wmsURL: window.location.protocol+"//"+window.location.host+SaigaiTask.contextPath+"/page/map/mgrs/?",
			wmsFormat: "image/png",
			visibility: false, // 初期表示
			params: {
				precision: 0
			}
		});
		layerInfo.children = [];
		for(var p=0; p<=5; p++) {
			layerInfo.children.push(new SaigaiTask.Map.Layer.LayerInfo({
				layerId: layerInfo.layerId+"_"+p,
				featuretypeId: "mgrs"+p,
				mgrsPrecision: p,
				visibility: false,
				params: {
					LAYERS: "mgrs"+p
				}
			}));
		}

		// UTMグリッドレイヤの初期化
		var layer = me.layer = new SaigaiTask.Map.Layer.WMSLayer(layerInfo);

		// 一番上に表示させるため、ロード後に追加
		stmap.events.on({
			"loadendecommap": function(ecommap) {
				// 地図未追加なら追加する
				if(!layer.map) {
					stmap.addLayer(layer);
					layer.setVisibility(layerInfo.visibility);
				}

			},
			// 凡例初期化
			"legendinitialize": function(args) {
				// 凡例パネルのツールバーに追加
				var legend = args.legend;
				// ボタン
				var btn = Ext.create("Ext.Button", {
					text: lang.__('UTM grid'),
					tooltip: lang.__("UTM grid is shown or hided."),
					icon: stmap.icon.getURL("mgrsIconURL"),
					enableToggle: true,
					handler: function() {
						layerInfo.visibility = !layerInfo.visibility;
						// 表示の場合
						if(layerInfo.visibility) {
							// MGRS桁数の自動決定
							if(me.autoPrecision) {
								var autoPrecision = me.getPrecisionForZoom(stmap.map.getZoom());
								me.setPrecision(autoPrecision);
							}
						}
						layer.refreshParams();
					}
				});

				legend.addTbarItems([btn]);
			}
		});

		stmap.map.events.on({
			"zoomend": function() {
				// MGRS桁数の自動決定
				if(me.autoPrecision) {
					var autoPrecision = me.getPrecisionForZoom(stmap.map.getZoom());
					var current = layerInfo.params.precision;
					if(current!=autoPrecision) {
						me.setPrecision(autoPrecision);
						layer.refreshParams();
					}
				}
			}
		});
	},

	setPrecision: function(precision) {
		var me = this;
		var layerInfo = me.layerInfo;
		layerInfo.params.precision = precision;
		// LAYERS パラメータも指定する
		for(var idx in layerInfo.children) {
			var child = layerInfo.children[idx];
			child.visibility = child.mgrsPrecision<=precision;
		}
	},

	/**
	 * MGRSコードの桁数からズームレベルを取得する
	 * @param {Number} precision MGRSコード桁数
	 */
	getZoomForPrecision: function(precision) {
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;
		var zoom = olmap.getZoom();
		if($.isNumeric(precision) && olmap.getUnits()=="m") {
			// 位置は格子の交差点なので、2倍の大きさが表示範囲に入るように計算
			for(var zoom=olmap.getMinZoom(); zoom<olmap.getNumZoomLevels(); zoom++) {
				if(!me.isViewportAvailable(zoom, precision)) {
					// 1つ前のズームレベルにする
					if(0<zoom) { zoom--;}
					break;
				}
			}
			if(olmap.getNumZoomLevels() <= zoom) zoom=olmap.getNumZoomLevels()-1;
		}
		return zoom;
	},

	/**
	 * ズームレベルに合うMGRSコードの桁数を取得する
	 * @param {Number} zoom ズームレベル
	 */
	getPrecisionForZoom: function(zoom) {
		var precision = 0;
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;

		var precisions = [5, 4, 3, 2, 1, 0];
		for(var idx in precisions) {
			var p = precisions[idx];
			if(!me.isViewportAvailable(zoom, p)) {
				if(0<idx) { idx--; }
				precision = precisions[idx];
				break;
			}
		}


		return precision;
	},

	/**
	 * ズームレベルに対して、適切なMGRSグリッドかどうかを判定します.
	 * (逆も可)
	 * @param {Number} zoom ズームレベル
	 * @param {Number} precision MGRSコード桁数
	 */
	isViewportAvailable: function(zoom, precision) {
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;

		// 桁数に応じて、MGRSコードの格子の大きさを取得
		var maxMeter = null;
		switch(precision) {
		case 5: maxMeter=1; break;      //    1m
		case 4: maxMeter=10; break;     //   10m
		case 3: maxMeter=100; break;    //  100m
		case 2: maxMeter=1000; break;   //   1km
		case 1: maxMeter=10000; break;  //  10km
		case 0: maxMeter=100000; break; // 100km
		}
		if(maxMeter==null) return false;

		// 表示領域の小さいほうのピクセル数を取得(マージンを両端で 30px とっておく)
		var pixel = Math.min($(olmap.getViewport()).width(), $(olmap.getViewport()).height()) - 30*2;
		// 表示領域の最大メートル
		var viewportMeter = olmap.getResolutionForZoom(zoom) * pixel;
		// 最大メートルを超えたらやめる
		return maxMeter*me.AUTO_PRECISION_GRID_NUM <= viewportMeter;
	}
});
