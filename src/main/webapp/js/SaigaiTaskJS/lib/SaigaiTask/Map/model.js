/**
 * データモデルパッケージです.
 * @requires SaigaiTask/Map.js
 */
SaigaiTask.Map.model = {};


/**
 * 地図の初期化オプションのデータモデルです.
 */
SaigaiTask.Map.model.InitOptions = function(){};
SaigaiTask.Map.model.InitOptions.prototype = {
	/**
	 * 官民協働危機管理クラウドシステムのコンテキストパス
	 * @type {String}
	 */
	contextPath: null,

	/**
	 * AJAXのAPIクラスを指定します.
	 * デフォルトは {SaigaiTask.Map.SaigaiTaskAPI} です.
	 * @type {SaigaiTask.Map.API}
	 */
	api: null,

	/**
	 * 地図アイコンのクラスを指定します.
	 * @type {SaigaiTask.Map.Icon}
	 */
	icon: null,

	/**
	 * 凡例パネルの表示
	 * @type {Boolean}
	 */
	showLegend: false,

	/**
	 * 右クリックメニューのオプション
	 * @type {SaigaiTask.Map.model.ShowMenuOptions}
	 */
	showMenuOptions: null,

	/**
	 * OpenLayersの投影法
	 * @type {Number}
	 */
	epsg: 4326
};

/**
 * コンテキストメニューの表示オプションのデータモデルです.
 */
SaigaiTask.Map.model.ShowMenuOptions = function(){};
SaigaiTask.Map.model.ShowMenuOptions.prototype = {
	/**
	 * 新規登録メニュー
	 */
	addContentsMenu: false,
	/**
	 * レイヤ削除メニュー
	 * @type {Boolean}
	 */
	deleteLayerMenu: false,
	/**
	 * 透明度変更メニュー
	 */
	transparencyLayerMenu: false
};

/**
 * 避難所検索オプションのデータモデルです.
 */
SaigaiTask.Map.model.ShelterSearchOptions = function(){};
SaigaiTask.Map.model.ShelterSearchOptions.prototype = {
	/**
	 * レイヤID
	 * @type {String}
	 */
	layerId: null,

	/**
	 * フィーチャID
	 * @type {Array<Number>}
	 */
	featureId: null
};

/**
 * eコミマップ情報のデータモデルです.
 */
SaigaiTask.Map.model.Ecommap = function(){};
SaigaiTask.Map.model.Ecommap.prototype = {

	/**
	 * ベースレイヤ配列
	 * @type {Array.<SaigaiTask.Map.model.Layer>}
	 */
	baseLayers: null,

	/**
	 * 登録情報レイヤ配列
	 * @type {Array.<SaigaiTask.Map.model.Layer>}
	 */
	contentsLayers: null,

	/**
	 * オーバレイレイヤ配列
	 * @type {Array.<SaigaiTask.Map.model.Layer>}
	 */
	overlayLayers: null,

	/**
	 * 参照レイヤ配列
	 * @type {Array.<SaigaiTask.Map.model.Layer>}
	 */
	referenceLayers: null
};

/**
 * レイヤのデータモデルです.
 * @class SaigaiTask.Map.model.Layer
 */
SaigaiTask.Map.model.Layer = function(){};
SaigaiTask.Map.model.Layer.prototype = {

	/**
	 * レイヤID
	 * @type {String}
	 */
	id: null,

	/**
	 * レイヤ名称
	 * @type {String}
	 */
	name: null,

	/**
	 * レイヤ説明
	 */
	description: null,

	/**
	 * 属性配列
	 * @type {Array.<SaigaiTask.Map.model.Attribute>}
	 */
	attributes: null
};

/**
 * フィーチャのデータモデルです.
 * @class SaigaiTask.Map.model.Feature
 */
SaigaiTask.Map.model.Feature = function(){};
SaigaiTask.Map.model.Feature.prototype = {

	/**
	 * レイヤID
	 * @type {String}
	 */
	layerId: null,

	/**
	 * フィーチャID
	 * レイヤID.フィーチャIDで取得するには{@link #getFeatureId()}メソッドを利用してください.
	 * @type {number}
	 */
	featureId: null,

	/**
	 * ジオメトリ
	 * TODO: @type {SaigaiTask.Map.model.Geometry}
	 */
	geometry: null,

	/**
	 * 属性の配列
	 * @type {Array.<SaigaiTask.Map.model.Attribute>}
	 */
	attributes: null,

	/**
	 * ファイル情報の配列
	 * TODO: @type {Object}
	 */
	files: null,

	/**
	 * メタデータ情報の配列
	 * TODO: @type {Object}
	 */
	metadata: null,

	/**
	 * "レイヤID.フィーチャID" の形式でフィーチャIDを取得します.
	 * @return {String} なければ null
	 */
	getFeatureId: function() {

		var fid = null;
		if(layerId&&featureId) {
			fid = layerId+"."+featureId;
		}
		return fid;
	},

	/**
	 * 属性名を配列で取得します.
	 * @return {Array.<String>}
	 */
	getAttributeNames: function() {
		var names = [];
		if(this.attributes) {
			for(var idx in this.attributes) {
				names.push(this.attributes[idx].name);
			}
		}
		return names;
	},

	/**
	 * 属性値を配列で取得します.
	 * @return {Array.<*>}
	 */
	getAttributeValues: function() {
		var values = [];
		if(this.attributes) {
			for(var idx in this.attributes) {
				values.push(this.attributes[idx].value);
			}
		}
		return values;
	}
};

/**
 * ジオメトリのデータモデルです.
 * @class SaigaiTask.Map.model.Geometry
 */
SaigaiTask.Map.model.Geometry = function(){};
SaigaiTask.Map.model.Geometry.prototype = {
	/**
	 * WKT
	 * @type {String}
	 */
	wkt: null
};

/**
 * フィーチャの属性のデータモデルです.
 * @class SaigaiTask.Map.model.Attribute
 */
SaigaiTask.Map.model.Attribute = function(){};
SaigaiTask.Map.model.Attribute.prototype = {

	/**
	 * 属性ID
	 * @type {String}
	 */
	id: null,

	/**
	 * 属性名
	 * @type {String}
	 */
	name: null,

	/**
	 * 属性値
	 * @type {*}
	 */
	value: null
};

/**
 * 空間検索の検索パラメータのデータモデルです.
 * @class SaigaiTask.Map.model.SearchParams 空間検索パラメータ
 */
SaigaiTask.Map.model.SearchParams = function(){};
SaigaiTask.Map.model.SearchParams.prototype = {

	/**
	 * 検索範囲のバッファ
	 * @type {Number}
	 */
	buffer: 0,

	/**
	 *  検索範囲のフィーチャの配列
	 * フィーチャは "[レイヤID].[フィーチャID]" の書式とします.
	 * @type {Array.<String>}
	 */
	features: ["c127.3"],

	/**
	 * 検索対象のレイヤID
	 * @type {String}
	 */
	layerId: "c127",

	/**
	 * 検索対象の地図ID
	 * @type {Number}
	 */
	mapId: 42,

	/**
	 * 検索範囲の検索方法ID
	 * @type {Number}
	 */
	spatialType: 1
};

/**
 * 空間検索結果のデータモデルです.
 * @class SaigaiTask.Map.model.SearchResult 空間検索結果
 */
SaigaiTask.Map.model.SearchResult = function(){};
SaigaiTask.Map.model.SearchResult.prototype = {

	/**
	 * 検索が成功したか
	 * @type {boolean}
	 */
	success: false,

	/**
	 * 検索結果数情報のオブジェクト
	 * @type {SaigaiTask.Map.model.ResultCount}
	 */
	counts: null,

	/**
	 * 検索されたフィーチャの配列
	 * @type {Array.<SaigaiTask.Map.model.Feature>}
	 */
	features: null,

	/**
	 * 検索結果のJSONの生データ
	 * @type {Object.<*, *>}
	 */
	raw: null
};

/**
 * 検索結果の結果数のデータモデルです.
 * @class SaigaiTask.Map.model.ResultCount 空間検索結果の結果数
 */
SaigaiTask.Map.model.ResultCount = function(){};
SaigaiTask.Map.model.ResultCount.prototype = {

	/**
	 * 検索条件に対応するデータ全件数
	 * @type {Number}
	 */
	total: 0,

	/**
	 * 検索結果一覧を取得したときのLimit
	 * @type {Number}
	 */
	limit: 0,

	/**
	 * 検索結果一覧を取得したときのOffset
	 * @type {Number}
	 */
	offset: 0
};
