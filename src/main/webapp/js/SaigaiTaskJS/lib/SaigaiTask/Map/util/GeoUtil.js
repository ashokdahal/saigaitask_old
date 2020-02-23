/**
 * @class SaigaiTask.Map.util.GeoUtil
 * GIS関連のユーティリティクラスです.
 * @static
 * @requires SaigaiTask/Map/util.js
 */
SaigaiTask.Map.util.GeoUtil = {

	/**
	 * 投影法一覧
	 * @type {Object.<String, OpenLayers.Projection>}
	 */
	projection: {

		WGS84: new OpenLayers.Projection("EPSG:4326"),

		GOOGLE: new OpenLayers.Projection("EPSG:900913"),

		/**
		 * UTM zone 52N
		 * 九州と沖縄などを含みます.
		 * http://www.spatialreference.org/ref/epsg/32652/
		 */
		UTM_ZONE_52N: new OpenLayers.Projection("EPSG:32652"),

		/**
		 * UTM zone 53N
		 * 四国、中国地方、近畿、愛知県などを含みます。
		 * http://www.spatialreference.org/ref/epsg/32653/
		 */
		UTM_ZONE_53N: new OpenLayers.Projection("EPSG:32653"),

		/**
		 * UTM zone 54N
		 * 静岡、長野、関東、東北、北海道などを含みます。
		 * http://www.spatialreference.org/ref/epsg/32654/
		 */
		UTM_ZONE_54N: new OpenLayers.Projection("EPSG:32654"),

		/**
		 * UTM zone 55N
		 * 北海道の釧路や択捉島などを含みます。
		 * http://www.spatialreference.org/ref/epsg/32655/
		 */
		UTM_ZONE_55N: new OpenLayers.Projection("EPSG:32655")
	},

	/**
	 * 投影法の境界線一覧(WGS84)
	 * @type {Object.<String, OpenLayers.Bounds>}
	 */
	projectionBounds: {

		WGS84: null,

		GOOGLE: null,

		/**
		 * UTM zone 52N
		 */
		UTM_ZONE_52N: new OpenLayers.Bounds(126.0000, 0.0000, 132.0000, 84.0000),

		/**
		 * UTM zone 53N
		 */
		UTM_ZONE_53N: new OpenLayers.Bounds(132.0000, 0.0000, 138.0000, 84.0000),

		/**
		 * UTM zone 54N
		 */
		UTM_ZONE_54N: new OpenLayers.Bounds(138.0000, 0.0000, 144.0000, 84.0000),

		/**
		 * UTM zone 55N
		 */
		UTM_ZONE_55N: new OpenLayers.Bounds(144.0000, 0.0000, 150.0000, 84.0000)
	},

	/**
	 * OpenLayers.Geometry を指定の投影法に変換します.
	 * 変換結果はgeometry自身に反映されます.
	 * Proj4jsライブラリが必要です.
	 * @param {OpenLayers.Geometry} geometry 変換対象のジオメトリ
	 * @param {OpenLayers.Projection} sourceProj 変換対象の投影法
	 * @param {OpenLayers.Projection} destProj 変換したい投影法
	 */
	transform: function(geometry, sourceProj, destProj) {
		switch(geometry.CLASS_NAME) {
			case "OpenLayers.Geometry.Curve" :
				console.error('transform '+geometry.CLASS_NAME+' is not supported.');
				break;
			case "OpenLayers.Geometry.Point" :
			case "OpenLayers.Geometry.Collection" :
			case "OpenLayers.Geometry.LinearRing" :
				geometry = geometry.transform(sourceProj, destProj);
				break;
			case "OpenLayers.Geometry.LineString" :
			case "OpenLayers.Geometry.Polygon" :
			case "OpenLayers.Geometry.MultiPoint" :
			case "OpenLayers.Geometry.MultiLineString" :
			case "OpenLayers.Geometry.MultiPolygon" :
				var components= geometry.components;
				for(var componentsIdx in components) {
					var component = components[componentsIdx];
					SaigaiTask.Map.util.GeoUtil.transform(component, sourceProj, destProj);
				}
				break;
		}
		return geometry;
	},

	/**
	 * OpenLayers.Geometry をUTMに変換します.
	 * UTMは、52から55までのゾーンに含まれるものまたは近いもののどれかに変換されます.
	 * 変換結果はgeometry自身に反映されます.
	 * @param {OpenLayers.Geometry} geometry 変換対象のジオメトリ
	 * @param {OpenLayers.Projection} sourceProj 変換対象の投影法(optional, default WGS84)
	 * @return {OpenLayers.Projection} 変換後のUTM投影法
	 */
	transform2UTM: function(geometry, sourceProj) {
		var startTime = new Date();

		if(typeof sourceProj=='undefined') {
			sourceProj = SaigaiTask.Map.util.GeoUtil.projection.WGS84;
		}

		// UTM ゾーン設定
		var zones = [{
			proj: SaigaiTask.Map.util.GeoUtil.projection.UTM_ZONE_52N,
			bounds: SaigaiTask.Map.util.GeoUtil.projectionBounds.UTM_ZONE_52N
		}, {
			proj: SaigaiTask.Map.util.GeoUtil.projection.UTM_ZONE_53N,
			bounds: SaigaiTask.Map.util.GeoUtil.projectionBounds.UTM_ZONE_53N
		}, {
			proj: SaigaiTask.Map.util.GeoUtil.projection.UTM_ZONE_54N,
			bounds: SaigaiTask.Map.util.GeoUtil.projectionBounds.UTM_ZONE_54N
		}, {
			proj: SaigaiTask.Map.util.GeoUtil.projection.UTM_ZONE_55N,
			bounds: SaigaiTask.Map.util.GeoUtil.projectionBounds.UTM_ZONE_55N
		}];

		// 重心を含むUTMゾーンを調べる
		// なければ距離の近いものを取得する
		var destProj = null;
		var mindistance = null;
		var centroid = geometry.getCentroid(); // 重心
		for(var zonesIdx in zones) {
			var zone = zones[zonesIdx];
			var contain = zone.bounds.contains(centroid.x, centroid.y);
			if(contain) {
				destProj = zone.proj;
				break;
			}
			var distance = centroid.distanceTo(zone.bounds.toGeometry());
			if(mindistance==null||mindistance>distance) {
				mindistance = distance;
				destProj = zone.proj;
			}
		}

		SaigaiTask.Map.util.GeoUtil.transform(geometry, sourceProj, destProj);
		var endTime = new Date();
		console.log("transform2UTM: "+(endTime-startTime)/1000+lang.__("Second"));
		return destProj;
	},

	/**
	 * WKT配列のジオメトリを結合してフィーチャを取得します.
	 * JSTSライブラリが必要です.
	 * @param {Array<String>} wkts WKT文字列配列
	 * @return {OpenLayers.Feature.Vector} ベクタフィーチャ. 無い場合は null
	 */
	union: function(wkts) {
		var wktFormat = new OpenLayers.Format.WKT();
		var parser = new jsts.io.OpenLayersParser();
		var unionGeom = null;
		var utmProj = null;
		for(var wktsIdx in wkts) {
			var wkt = wkts[wktsIdx];
			var feature = wktFormat.read(wkt);

			utmProj = SaigaiTask.Map.util.GeoUtil.transform2UTM(feature.geometry);
			var jstsGeom = parser.read(feature.geometry);
			if(unionGeom==null) unionGeom = jstsGeom;
			else unionGeom = unionGeom.union(jstsGeom);
		}
		if(unionGeom!=null) {
			var olGeom = parser.write(unionGeom);
			var feature = new OpenLayers.Feature.Vector(olGeom);
			SaigaiTask.Map.util.GeoUtil.transform(feature.geometry, utmProj, SaigaiTask.Map.util.GeoUtil.projection.WGS84);
			return feature;
		}
		return null;
	},

	/**
	 * WKT配列をバッファ計算してフィーチャ配列で取得します.
	 * JSTSライブラリが必要です.
	 * @param {Array<String>} wkts WKT文字列配列
	 * @param {Number} buffer バッファ（メートル）
	 * @return {Array.<OpenLayers.Feature.Vector>} ベクタフィーチャ配列.無い場合は要素数0の配列を返す.
	 */
	buffer: function(wkts, buffer) {
		var wktFormat = new OpenLayers.Format.WKT();
		var parser = new jsts.io.OpenLayersParser();
		var features = [];
		for(var wktsIdx in wkts) {
			var wkt = wkts[wktsIdx];
			var feature = wktFormat.read(wkt);

			var utmProj = SaigaiTask.Map.util.GeoUtil.transform2UTM(feature.geometry);
			var jstsGeom = parser.read(feature.geometry);
			jstsGeom = jstsGeom.buffer(buffer); // buffered by meter unit.
			console.log("utmProj");
			console.log(utmProj);

			var olGeom = parser.write(jstsGeom);
			var bufferedFeature = new OpenLayers.Feature.Vector(olGeom);
			SaigaiTask.Map.util.GeoUtil.transform(bufferedFeature.geometry, utmProj, SaigaiTask.Map.util.GeoUtil.projection.WGS84);
			features.push(bufferedFeature);
		}
		return features;
	}
};
