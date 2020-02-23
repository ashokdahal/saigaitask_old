///**
// * WEBTIS Layer (電子国土)
// * 
// * 以下の読込が必要.
// * <script type="text/javascript" src="http://portal.cyberjapan.jp/sys/v4/webtis/webtis_v4.js" charset="UTF-8"></script>
// * <link rel="stylesheet" type="text/css" href="http://portal.cyberjapan.jp/sys/v4/css/webtis.css">
// * 
// * @requires SaigaiTask/Map/Layer.js
// * @requires SaigaiTask/Map/Layer/Type.js
// */
//SaigaiTask.Map.Layer.WebtisLayer = new OpenLayers.Class(webtis.Layer.BaseMap, {
//	layerInfo: null,
//	initialize: function(layerInfo) {
//		this.layerInfo = layerInfo;
//		var layerID1 = "JAIS", layerID2 = "BAFD1000K", layerID3 = "BAFD200K", layerID4 = "DJBMM", layerID5 = "FGD";
//		var featureIds = layerInfo.featuretypeId.split(",");
//		if(featureIds.length>=5){
//			layerID1 = featureIds[0];
//			layerID2 = featureIds[1];
//			layerID3 = featureIds[2];
//			layerID4 = featureIds[3];
//			layerID5 = featureIds[4];
//		}
//		var dataset = {0 : { dataId : "" }, 1 : { dataId : "" }, 2 : { dataId : "" }, 3 : { dataId : "" }, 4 : { dataId : "" },
//			5 : { dataId : layerID1 }, 6 : { dataId : layerID1 }, 7 : { dataId : layerID1 }, 8 : { dataId : layerID1 }, 9 : { dataId : layerID2 },
//			10 : { dataId : layerID2 }, 11 : { dataId : layerID2 }, 12 : { dataId : layerID3 }, 13 : { dataId : layerID3 }, 14 : { dataId : layerID3 },
//			15 : { dataId : layerID4 }, 16 : { dataId : layerID4 }, 17 : { dataId : layerID4 }, 18 : { dataId : layerID5 },
//			19 : { dataId : "" }, 20 : { dataId : "" } , 21 : { dataId : "" } };
//
//		//著作権情報
//		var name = layerInfo.name;
//		webtis.Layer.BaseMap.prototype.initialize.apply(this, [name, {dataSet: dataset, isBaseLayer: true}]);
//		if (layerInfo.attribution) {
//			//hack
//			this.updateAttribution = this.updateAttributionIE7;
//			this.attributionTemplate = "<br/>"+layerInfo.attribution+"<br/>"+this.attributionTemplate+"<br/>";
//		}
//	}
//});

/**
 * WEBTIS Layer (電子国土)
 * 標準的なXYZ座標系の地理院タイルと呼ばれる仕様ができた.
 * webtis_v4.js を使わずに OpenLayers の標準のクラスで取得できるようになった。
 * @requires OpenLayers/Layer/XYZZoom.js
 * @requires OpenLayers/Layer/GSITile.js
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.WebtisLayer = new OpenLayers.Class(OpenLayers.Layer.GSITile, {
	layerInfo:null,
	initialize: function(layerInfo) {
		this.layerInfo = layerInfo;
		OpenLayers.Layer.GSITile.prototype.initialize.apply(this, [layerInfo.name, layerInfo.featuretypeId]);
		
	}
});

SaigaiTask.Map.Layer.WebtisLayer.type = SaigaiTask.Map.Layer.Type.WEBTIS;
