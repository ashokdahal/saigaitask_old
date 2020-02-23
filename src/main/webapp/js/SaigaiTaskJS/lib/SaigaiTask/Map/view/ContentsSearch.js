
/**
 * 登録情報を検索するビューです.
 * @class SaigaiTask.Map.view.ContentsSearch
 * @param {SaigaiTask.Map} map
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.ContentsSearch = function(map) {
	this.map = map;
};
SaigaiTask.Map.view.ContentsSearch.prototype = {

	/**
	 * SaigaiTask.Mapオブジェクト
	 * @type {SaigaiTask.Map}
	 */
	map: null,

	/**
	 * 登録情報検索ウィジェットのコンテナです.
	 * @type {Ext.container.Container}
	 */
	container: null,

	/**
	 * 登録情報検索フォームのビュー
	 * @type {SaigaiTask.Map.view.SpatialSearchForm}
	 */
	spatialSearchFrom: null,

	/**
	 * 登録情報検索結果のビュー
	 * @type {SaigaiTask.Map.view.SpatialSearchResult}
	 */
	spatialSearchResult: null,

	/**
	 * 検索範囲レイヤー
	 * @type {OpenLayers.Layer.Vector}
	 */
	rangeLayer: null,

	/**
	 * 検索結果レイヤー
	 * @type {OpenLayers.Layer.Vector}
	 */
	resultLayer: null,

	/**
	 * 全検索結果
	 * @type {SaigaiTask.Map.model.SearchResult}
	 */
	searchResult: null,

	/**
	 * 登録情報検索ウィジェットを取得します
	 * @param {Object<String, *>} options
	 * @param {Object<String, *>} options.searchFormOptions {@link SaigaiTask.Map.view.SpatialSearchForm #get(options)} の引数options
	 */
	get: function(options) {
		var me = this;
		var searchFormOptions = options.searchForm;

		// 検索フォームのパネルを定義
		var spatialSearchForm = new SaigaiTask.Map.view.SpatialSearchForm();
		me.spatialSearchFrom = spatialSearchForm;
		var form = spatialSearchForm.get(searchFormOptions);

		// 検索結果のグリッドを定義
		var spatialSearchResult = new SaigaiTask.Map.view.SpatialSearchResult();
		me.spatialSearchResult = spatialSearchResult;
		var resultView = spatialSearchResult.grid;
		resultView.title = lang.__('Search result');
		resultView.collapsible = false;
		resultView.margin = '5 0 0 0';
		resultView.autoSize = true;
		var store = spatialSearchResult.grid.store;

		// 検索範囲レイヤを更新する関数を定義
		var reloadRangeLayer = function(){
			var wkts = [];
			var values = spatialSearchForm.getValues();
			var wkt = me.map.api.getContentsSearchRangeWKT(values);
			if(typeof wkt!="undefined" && wkt!=null) {
				wkts.push(wkt);
			}
			me.loadRangeLayer(wkts);
		};
		reloadRangeLayer();

		// 検索結果レイヤの検索メソッドを定義
		var searchForMap = function() {
			// 検索範囲を更新する
			reloadRangeLayer();

			// 検索リクエストを送信
			me.searchResult = null;
			var limit = 0;
			var offset = 0;
			form.submit({
				params: {
					mid: searchFormOptions.searchButtonInfo.mid,
					limit: limit,
					offset: offset
				}, success: function(form, action) {
					var result = action.result;
					var spatialSearch = new SaigaiTask.Map.control.SpatialSearch();
					var searchResult = spatialSearch.getSearchResult(result);

					// 地図に検索結果レイヤを追加する
					me.loadResultLayer(searchResult);
					me.searchResult = searchResult;
				}
			});
		};

		// 検索結果グリッドの検索メソッドを定義
		var searchForGrid = function(limit, offset) {
			// ロード中の表示
			resultView.setLoading(true);

			// collapsed の場合は表示する
			if(resultView.collapsed) {
				resultView.toggleCollapse();
			}

			// ページサイズを更新
			store.pageSize = limit;

			// 検索リクエストを送信
			form.submit({
				params: {
					mid: searchFormOptions.searchButtonInfo.mid,
					limit: limit,
					offset: offset
				}, success: function(form, action) {
					var result = action.result;
					var spatialSearch = new SaigaiTask.Map.control.SpatialSearch();
					var searchResult = spatialSearch.getSearchResult(result);
					// 検索結果をグリッドにロードする
					spatialSearchResult.load(searchResult);

					// ロード中の非表示
					resultView.setLoading(false);
				}
			});
		};

		// 検索ボタンイベントを定義
		form.on('search', function(){
			// 検索範囲、検索結果をレイヤに更新
			searchForMap();

			// 取得数、オフセット指定で検索
			var limit = spatialSearchResult.limitCombo.value;
			var offset = 0;
			searchForGrid(limit, offset);
		});

		// ページングのイベントを定義
		var pagingTbar = spatialSearchResult.pagingTbar;
		pagingTbar.on('movefirst', function() {
			store.currentPage = 1;
			var limit = spatialSearchResult.limitCombo.value;
			var offset = (store.currentPage-1)*store.pageSize;
			searchForGrid(limit, offset);
		});
		pagingTbar.on('movelast', function() {
			var total = pagingTbar.store.totalCount;
			var limit = pagingTbar.store.pageSize;
			store.currentPage = Math.ceil(total/limit);
			var offset = (store.currentPage-1)*store.pageSize;
			searchForGrid(limit, offset);
		});
		pagingTbar.on('movenext', function() {
			store.currentPage++;
			var limit = spatialSearchResult.limitCombo.value;
			var offset = (store.currentPage-1)*store.pageSize;
			searchForGrid(limit, offset);
		});
		pagingTbar.on('moveprevious', function() {
			store.currentPage--;
			var limit = spatialSearchResult.limitCombo.value;
			var offset = (store.currentPage-1)*store.pageSize;
			searchForGrid(limit, offset);
		});

		// グリッドのイベントを定義
		spatialSearchResult.grid.on('popup', function(layerId, featureId){
			if(me.map) {
				me.map.getContent(layerId, featureId);
			}
		});

		// 組み合わせる
		form.region = "north";
		resultView.region = "center";
		var panel = Ext.create("Ext.container.Container", {
			width: 500,
			height: 500,
			autoScroll: true,
			layout: {
				type: 'border'
				//align: 'stretch'
			},
			items: [form, resultView]
		});

		// 検索を実行する
		//form.doSearch();

		this.container = panel;

		return panel;
	},

	/**
	 * 登録情報検索ウィンドウを表示します.
	 * @param {Object<String, *>} options
	 */
	show: function(options) {
		var me = this;
		var map = me.map;
		var container = me.get(options);

		// フッターツールバー
		var fbar = null;
		fbar = map.events.triggerEvent(map.EventType.beforeshowcontentsearchview, {
			contentSearchView: me
		});

		// ウィンドウを定義する
		var win = Ext.create("Ext.window.Window", {
			title: lang.__('Registration info Search'),
			collapsible: true,
			resizable: true,
			width: 500,
			maxWidth: document.body.clientWidth,
			maxHeight: document.body.clientHeight,
			layout: 'fit',
			items: [container],
			fbar: fbar,
			buttonAlign: "center"
		});

		win.on('destroy', function(){
			me.onDestroy();
		});

		window.win = win;

		win.show();
		// ウィンドウ左下に表示
		win.alignTo(document, "bl", [0, -win.getHeight()-20]);
	},

	/**
	 * 検索範囲を検索範囲レイヤに反映します.
	 * @param {Array<String>} wkts 検索範囲のWKT文字列配列
	 */
	loadRangeLayer: function(wkts) {
		var me = this;
		var olmap = me.map.map;

		// 定義されていなければ生成する
		if(!me.rangeLayer) {
			me.rangeLayer = new OpenLayers.Layer.Vector(lang.__("Search range"));
		}

		var rangeLayer = me.rangeLayer;

		// 一度、非表示にする
		if(olmap.getLayer(rangeLayer.id)) {
			olmap.removeLayer(rangeLayer);
		}

		// WKTからフィーチャを作成する
		features = [];
		var wktFormat = new OpenLayers.Format.WKT();
		for(var wktsIdx in wkts) {
			var wkt = wkts[wktsIdx];
			var feature = wktFormat.read(wkt);
			// 投影法を変換
			feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), olmap.getProjectionObject());
			features.push(feature);
		}

		// フィーチャを更新する
		rangeLayer.removeAllFeatures();
		rangeLayer.addFeatures(features);

		// データがあれば表示する
		if(0<features.length) {
			olmap.addLayer(rangeLayer);
		}
	},

	/**
	 * 検索結果を検索結果レイヤに反映します.
	 * @param {SaigaiTask.Map.model.SearchResult} searchResult 検索結果オブジェクト
	 * @param {Array<SaigaiTask.Map.model.Feature>} searchResult.features 検索結果のフィーチャ配列
	 */
	loadResultLayer: function(searchResult) {
		var me = this;
		var map = me.map;
		var olmap = me.map.map;

		// 定義されていなければ生成する
		if(!me.resultLayer) {
			me.resultLayer = new OpenLayers.Layer.Vector(lang.__("Search result"));
		}

		var resultLayer = me.resultLayer;

		// スタイルを更新
		var style = {
			// 塗りつぶしの設定
			fillColor: "white",
			fillOpacity: 0.4,
			// ラインの設定
			strokeColor: "#0099ee",
			strokeOpacity: 1,
			strokeWidth: 5,
			strokeLinecap: "round", // butt, round, square
			strokeDashstyle: "solid", // dot, dash, dashdot, longdash, longdashdot, solid
			// 図の設定
			externalGraphic: map.icon.getURL("editingIconURL"),
			graphicWidth: 19,
			graphicHeight: 32,
			graphicXOffset: -9,
			graphicYOffset: -32,
			graphicOpacity: 1
		};
		resultLayer.styleMap.styles['default'].defaultStyle = style;


		// 一度、非表示にする
		if(olmap.getLayer(resultLayer.id)) {
			olmap.removeLayer(resultLayer);
		}

		// フィーチャを更新する
		resultLayer.removeAllFeatures();
		var features = [];
		for(var searchResultFeaturesIdx in searchResult.features) {
			var searchResultFeature = searchResult.features[searchResultFeaturesIdx];
			var geometry = OpenLayers.Geometry.fromWKT(searchResultFeature.geometry.wkt);
			var feature = new OpenLayers.Feature.Vector(geometry);
			features.push(feature);
		}
		resultLayer.addFeatures(features);

		// データがあれば表示する
		if(0<features.length) {
			olmap.addLayer(resultLayer);
		}
	},

	/**
	 * 登録情報検索の終了処理です.
	 */
	onDestroy: function() {
		var me = this;
		var olmap = me.map.map;

		// 検索範囲レイヤーを消去する
		var rangeLayer = me.rangeLayer;
		if(rangeLayer) {
			if(olmap.getLayer(rangeLayer.id)) {
				olmap.removeLayer(rangeLayer);
			}
		}

		// 検索結果レイヤーを消去する
		var resultLayer = me.resultLayer;
		if(resultLayer) {
			if(olmap.getLayer(resultLayer.id)) {
				olmap.removeLayer(resultLayer);
			}
		}
	}
};
