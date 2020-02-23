/**
 * 登録情報の空間検索のコントローラです.
 * @class SaigaiTask.Map.control.SpatialSearch
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.SpatialSearch = function() {};
SaigaiTask.Map.control.SpatialSearch.prototype = {

	/**
	 * 検索結果のJSONを SaigaiTask.Map.model.SearchResult オブジェクトに変換して取得します.
	 * @param result 検索結果JSONオブジェクト
	 * @returns {SaigaiTask.Map.model.SearchResult}
	 */
	getSearchResult: function(result) {
		if(result) {
			var searchResult = new SaigaiTask.Map.model.SearchResult();
			searchResult.raw = result;
			var _result = result;
			if(_result) {
				searchResult.success = _result.success;

				// カウント情報を取得する
				var counts = _result.items[0];
				var resultCount = new SaigaiTask.Map.model.ResultCount();
				resultCount.total = counts[0];
				resultCount.limit = counts[1];
				resultCount.offset = counts[2];
				searchResult.counts = resultCount;

				// フィーチャ情報を取得する
				searchResult.features = [];
				var features = _result.items[1];
				for(var featuresIdx in features) {
					var feature = features[featuresIdx];
					var f = new SaigaiTask.Map.model.Feature();
					f.layerId = feature[0];
					f.featureId = feature[1];
					// ジオメトリ
					f.geometry = new SaigaiTask.Map.model.Geometry();
					var geometryResult = feature[2];
					f.geometry.wkt = geometryResult[0];
					// 属性値
					f.attributes = [];
					var attributeResults = feature[3];
					for(var attributeResultsIdx=0; attributeResultsIdx<attributeResults.length/2; attributeResultsIdx++) {
						var idx = attributeResultsIdx*2;
						var attr = new SaigaiTask.Map.model.Attribute();
						attr.id =  attributeResultsIdx;
						attr.name = attributeResults[idx];
						attr.value = attributeResults[idx+1];
						f.attributes.push(attr);
					}
					f.files = feature[4];
					f.metadata = feature[5];
					f.raw = feature;
					searchResult.features.push(f);
				}
				return searchResult;
			}
		}
		return null;
	}
};
