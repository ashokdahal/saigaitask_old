/**
 * レイヤー種別
 * @requires SaigaiTask/Map/Layer.js
 */
SaigaiTask.Map.Layer.Type = {

		/**
		 * レイヤグループ（汎用）
		 * SaigaiTask オリジナルタイプ
		 */
		GROUP: 0,

		// 登録情報レイヤ
		/**
		 * eコミのローカルGeoServerで持っているコンテンツレイヤ
		 */
		LOCAL: 1,

		/**
		 * eコミのグループ化用のコンテンツを持たないレイヤ
		 */
		LOCAL_GROUP: 10,

		// 主題図項目レイヤ初期化
		/**
		 * eコミの外部WMSの参照用コンテンツレイヤ
		 */
		REFERENCE: 51,

		/**
		 * eコミの外部参照地図のWMSのレイヤを子に持つレイヤ LAYERパラメータは付けない
		 */
		REFERENCE_WMS: 59,

		// ベースレイヤ
		/**
		 * WMSベースレイヤ
		 */
		BASE_WMS: 1100,

		/**
		 * eコミのタイルキャッシュベースレイヤ
		 */
		TILED: 1150,

		/**
		 * eコミのOpenLayers.Layer.TileCache ベースレイヤ
		 */
		TILECACHE: 1152,

		/**
		 * eコミのOpenStreetMapレイヤ
		 */
		OSM: 1153,

		/**
		 * eコミのOpenLayers.Layer.GOOGLE ベースレイヤ
		 */
		GOOGLE: 1155,

		/**
		 * eコミのwebtis.Layer.BaseMap（電子国土）
		 */
		WEBTIS: 1160,
		/** OpenLayers.Layer.XYZTile ベースレイヤ */
		BASE_XYZ: 1170,

		/**
		 * eコミのXYZレイヤの背景地図
		 */
		BASE_XYZ: 1170,

		/** KMLレイヤ */
		KML: 130,

		// 主題図（画像）項目レイヤ初期化
		OVERLAY_WMS: 100,
		OVERLAY_WMS_SINGLE: 101,
		OVERLAY_TILED: 150,
		OVERLAY_TILECACHE: 151,
		OVERLAY_OSM: 152,
		OVERLAY_XYZ: 170,

		/**
		 * 外部地図レイヤ
		 */
		EXTERNAL_MAP_WMS: 900,
		EXTERNAL_MAP_WMS_LAYERS: 901,
		EXTERNAL_MAP_XYZ: 910,

		/**
		 * ArcGISレイヤ
		 */
		EXTERNAL_MAP_ARCGIS_LAYERS: 903,

		/**
		 * ベースレイヤのレイヤ種別を配列で取得します.
		 * @returns {Array<Number>}
		 */
		getBaseLayerTypes: function() {
			return [
					SaigaiTask.Map.Layer.Type.BASE_WMS,
					SaigaiTask.Map.Layer.Type.TILED,
					SaigaiTask.Map.Layer.Type.TILECACHE,
					SaigaiTask.Map.Layer.Type.OSM,
					SaigaiTask.Map.Layer.Type.GOOGLE,
					SaigaiTask.Map.Layer.Type.WEBTIS,
					SaigaiTask.Map.Layer.Type.BASE_XYZ
				];
		},

		isBaseLayerType: function(type) {
			return jQuery.inArray(type, SaigaiTask.Map.Layer.Type.getBaseLayerTypes())!=-1;
		},

		getOverlayLayerTypes: function() {
			return [
					SaigaiTask.Map.Layer.Type.OVERLAY_WMS,
					SaigaiTask.Map.Layer.Type.OVERLAY_WMS_SINGLE,
					SaigaiTask.Map.Layer.Type.OVERLAY_TILED,
					SaigaiTask.Map.Layer.Type.OVERLAY_TILECACHE,
					SaigaiTask.Map.Layer.Type.OVERLAY_OSM,
					SaigaiTask.Map.Layer.Type.OVERLAY_XYZ
				];
		},

		isOverlayLayerType: function(type) {
			return jQuery.inArray(type, SaigaiTask.Map.Layer.Type.getOverlayLayerTypes())!=-1;
		},

		getExternalmapLayerTypes: function() {
			return [
					SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS,
					SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS_LAYERS,
					SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_XYZ,
					SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_ARCGIS_LAYERS
				];
		},

		isExternalmapLayerType: function(type) {
			return jQuery.inArray(type, SaigaiTask.Map.Layer.Type.getExternalmapLayerTypes())!=-1;
		},

		getArcGISLayerTypes: function() {
			return [
					SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_ARCGIS_LAYERS
				];
		},

		isArcGISLayerType: function(type) {
			return jQuery.inArray(type, SaigaiTask.Map.Layer.Type.getArcGISLayerTypes())!=-1;
		}
};
