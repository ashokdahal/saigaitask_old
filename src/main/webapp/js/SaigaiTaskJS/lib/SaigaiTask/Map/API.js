/**
 * SaigaiTaskのAPIインタフェースを定義します.
 * @requires SaigaiTask/Map.js
 */
SaigaiTask.Map.API = OpenLayers.Class({

	contextPath: null,

	/**
	 * SaigaiTask.Mapオブジェクト
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	initialize: function(contextPath) {
		var me = this;

		me.contextPath = contextPath;

		// SaigaiTask.Map.API.[サブAPI名] のクラスをロードする
		// サブAPIの変数名は小文字とする
		for(var subApiName in SaigaiTask.Map.API) {
			var subApi = SaigaiTask.Map.API[subApiName];
			if(typeof subApi=="function") {
				var subApiVarName = subApiName.toLowerCase()
				me[subApiVarName] = new subApi(me);
			}
		}
	},

	errorMsg: function(jqXHR) {
		return "\n\n[HTTP status code "+jqXHR.status+"]";
	},

	/**
	 * エラーメッセージを表示します.
	 */
	showError: function(jqXHR) {
		alert(lang.__("Failed to get data.<!--2-->")+this.errorMsg(jqXHR));
	},

	/**
	 * eコミマップの地図初期化データを取得します.
	 * @param {Number} mapId 地図ID
	 * @param success コールバック関数
	 */
	getEcommapInfo: function(mapId, success) {
		var me = this;
		var url = me.url.ecommapInfoURL;
		jQuery.ajax(url, {
			async: true,
			dataType: "json",
			data: {
				mapId: mapId
			},
			success: function(data, textStatus, jqXHR) {
				if(jQuery.isFunction(success)) {
					success(data);
				}
			},
			error: function(jqXHR, status, errorThrown) {
				if(jqXHR.status!=0) {
					alert(lang.__("Failed to get initialization map data. (mapId=")+mapId+")"+me.errorMsg(jqXHR));
				}
				console.error(errorThrown);
			}
		});
	},

	/**
	 * 登録情報を1件取得します.
	 * @param mid 地図ID
	 * @param layer レイヤID
	 * @param fid フィーチャID
	 * @param center 中心位置
	 * @param bbox 矩形検索で使ったbbox
	 * @param success コールバック関数
	 */
	getContent: function(mid, layer, fid, center, bbox, success) {
		try {
			var me = this;
			var layerInfo = me.stmap.getLayerInfo(layer);
			if(!layerInfo) return; // layer not found
			var url = this.url.contentsGetURL;
			url += "?"+"layer="+layer+"&fid="+fid;
			if(mid!=null) {
				url += "&mid="+mid;
			}
			var time = layerInfo.getTime();
			if(time==null) time = SaigaiTask.PageURL.getTime();
			if(!!time) {
				var iso8601Time = time.toISOString();
				// It corresponds to the e-com map, and the time zone is plus (it is actually added by division because getTimezonOffset becomes negative number-540).
				if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
					iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
				}
				if(!!time) url += "&time="+iso8601Time;
			}

			jQuery.ajax(url, {
				async: true,
				dataType: "json",
				cache: false,
				//headers: {
				//	"X-CSRF-Token": SaigaiTask.csrfToken
				//},
				success: function(data, textStatus, jqXHR) {
					if(jQuery.isFunction(success)) {
						success(data);
					}
				},
				error: function(jqXHR, status, errorThrown) {
					var errorMsg = "\n\n[HTTP status code "+jqXHR.status+"]";
					alert(lang.__("Failed to get data.<!--2-->")+errorMsg);
					console.error(errorThrown);
				}
			});
		} catch (e) { console.error(e); }
	},

	/**
	 * 登録情報を1件削除します.
	 * @param layer レイヤID
	 * @param fid フィーチャID
	 * @param success コールバック関数
	 */
	deleteContent: function(layer, fid, success) {
		try {
			var url = this.url.contentsDeleteURL;

			jQuery.ajax(url, {
				async: true,
				data: {
					layer: layer,
					fid: fid
				},
				cache: false,
				//headers: {
				//	"X-CSRF-Token": SaigaiTask.csrfToken
				//},
				success: function(data, textStatus, jqXHR) {
					if(jQuery.isFunction(success)) {
						success(data);
					}
				},
				error: function(jqXHR, status, errorThrown) {
					var errorMsg = "\n\n[HTTP status code "+jqXHR.status+"]";
					alert(lang.__("Failed to delete data.")+errorMsg);
					console.error(errorThrown);
				}
			});
		} catch (e) { console.error(e); }
	},

	/**
	 * 矩形で登録情報を検索します.
	 * @param bbox 矩形
	 * @param cid サイトID
	 * @param mid 地図ID
	 * @param layerIds 検索レイヤID
	 * @param rule SLD表示ルール
	 * @param limit 取得数
	 * @param async 非同期通信フラグ
	 * @param success コールバック関数
	 * @returns
	 */
	searchContentsByBbox: function(bbox, cid, mid, layerIds, rule, limit, async, success) {
		var me = this;
		// オプションでURLを組み立てる
		var url = me.url.contentsBboxURL+"?";
		url += "bbox="+(bbox.join(","));
		url += "&cid="+cid;
		url += "&mid="+mid;
		url += "&layers="+layerIds.join(',');
		url += "&rule="+rule;
		url += "&limit="+limit;
		url += "&noname=true";

		// 時間パラメータを取得
		var me = this;
		var layertimes = "";
		for(var idx in layerIds) {
			var layerId = layerIds[idx];
			var layerInfo = me.stmap.getLayerInfo(layerId);
			if(layerInfo.time!=null) {
				if(layertimes.length!=0) layertimes += ",";
				var time = layerInfo.getTime();
				var iso8601Time = time.toISOString();
				// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
				if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
					iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
				}
				layertimes += layerId +","+ iso8601Time;
			}
		}
		if(0<layertimes.length) {
			url += "&layertimes="+layertimes;
		}
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			if(!!time) url += "&time="+iso8601Time;
		}

		// 検索する
		var result = null;
		jQuery.ajax(url, {
			async: async,
			dataType: "json",
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: function(data, textStatus, jqXHR) {
				var result = data;
				if(jQuery.isFunction(success)) {
					success(result);
				}
			},
			error: function(jqXHR, status, errorThrown) {
				var errorMsg = "\n\n[HTTP status code "+jqXHR.status+"]";
				alert(lang.__("Failed to get data.<!--2-->")+errorMsg);
				console.error(errorThrown);
			}
		});

		if(async==false) {
			return result;
		}
	},

	/**
	 * コンテンツ情報を属性・空間検索します.
	 * TODO: ExtJSのFormになっているところをこっちに移す？
	 */
	searchContents: function() {},

	/**
	 * サーバから検索範囲をWKTで取得します.
	 * 検索範囲はバッファ計算、結合したジオメトリです.
	 * @param params HTTPリクエストパラメータ
	 */
	getContentsSearchRangeWKT: function(params) {
		var me = this;
		var wkt = null;

		var url = me.url.contentsSearchRangeWKTURL;

		jQuery.ajax(url, {
			dataType: "json",
			data: params,
			async: false,
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: function(data) {
				wkt = data.items.wkt;
			}
		});
		return wkt;
	},

	/**
	 * レイヤの属性情報配列を取得します.
	 * @param layerId レイヤID
	 * @return layerInfo, attrInfos を持つJSONオブジェクト
	 */
	getAttrInfos: function(layerId) {
		var url = this.url.attrInfoURL;
		var json = null;
		jQuery.ajax({
			url: url,
			type: "GET",
			async: false,
			cache: false,
			dataType: "json",
			data: {
				layer: layerId
			},
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: function(data) {
				json = data;
			}
		});
		return json;
	},

	/**
	 * レイヤの最終更新日時を取得します.
	 */
	getLayerLastUpdateTime: function(layerInfo) {
		var url = this.url.layerLastUpdateTimeURL;
		var time = layerInfo.getTimeParam();
		var json = null;
		jQuery.ajax({
			url: url,
			type: "GET",
			async: false,
			cache: false,
			dataType: "json",
			data: {
				layer: layerInfo.layerId,
				attrIds: layerInfo.updatecolumn,
				time: time
			},
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: function(data) {
				json = data;
			}
		});
		return json;
	},

	/**
	 * SLDファイルを文字列で取得します.
	 * @param mid 地図ID
	 * @param layerId レイヤID
	 * @returns SLD文字列
	 */
	getSld: function(mid, layerId) {
		var url = this.url.sldURL;
		url += "?mid="+mid+"&layer="+layerId;

		var result = null;
		jQuery.ajax(url, {
			dataType: "text",
			async: false,
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: function(data) {
				result = data;
			}
		});
		return result;
	},

	/**
	 * 印刷処理を実行します.
	 */
	createPdf: function(params, success) {
		var url = SaigaiTask.contextPath+"/PdfServlet";
		var defaultParams = {
			// プレビュー表示ページ
			prev_row: 0,
			prev_col: 0,

			// 印刷種別
			printmap: 1,
			printlegend: 1,

			// タイトル
			maptitle: "",
			// タイトルの配置
			titlealign: 0,
			// タイトルの文字サイズ自動フラグ
			titlefontauto: 1,

			// 説明・注釈
			description: "",
			// 説明・注釈の配置
			descalign: 2,
			// 説明・注釈の文字サイズ自動フラグ
			descfontauto: 1,

			//bboxtype: "on",

			// 用紙サイズ
			pagesize: "a4",

			// 用紙向き 0: 縦, 1: 横
			rotate: 1,

			// 地図分割数
			rows: 1,
			cols: 1,

			// 地図ページマージン
			mapmt: 5,
			mapmb: 5,
			mapml: 5,
			mapmr: 5,

			// アイコンの倍率
			iconrate: 1.0,

			// 線の太さ
			linerate: 1.0,

			// スケール
			scalealign: 4,

			// 凡例の列幅の自動フラグ
			legendcolauto: 1,
			// 凡例サイズ
			legendrate: 0.8,
			// 凡例表示位置 1: 左上、 2: 右上、 3: 左下、 4: 右下
			legendpos: 3,

			legendcontents: 10,
			legendref: 10,
			legendbase: 10,

			// 凡例のマップ内余白
			legendmapmt: 3,
			legendmapmb: 3,
			legendmapmr: 3,
			legendmapml: 3,

			// 凡例の別ページ余白
			legendmt: 10,
			legendmb: 10,
			legendml: 10,
			legendmr: 10,

			// 一覧のマージン
			listmt: 10,
			listmb: 10,
			listml: 10,
			listmr: 10,
			// 一覧のマージンの自動フラグ
			listcolauto: 1,

			// 帳票表示
			listfile: 1,
			listlayers: ["c11", "c13"],

			// ヘッダ
			header_l: null,
			header_l_text: "",
			header_c: null,
			header_c_text: "",
			header_r: null,
			header_r_text: "",

			// フッタ
			fpage: 1,
			fmappage: 1,
			memoVisible: true,

			// 外枠索引
			index_enabled: 0,
			index_h: 2,
			index_cols: 6,
			index_v: 1,
			index_rows: 6,
			
			//cid: 1,
			//mid: 11,
			//sid: "ah12w8vvw20q",
			//epsg: 900913,
			//bbox: "15452148.098818863,4494180.914057751,15511294.545987,4536016.207544248",

			//登録情報の時間パラメータ
			time: null,

			//レイヤ個別の時間パラメータ 登録情報はレイヤ単位、主題図はサーバ単位、主題図画像はレイヤ単位
			//カンマ区切り "レイヤID1,時間1,レイヤID2,時間2"
			layertimes: null,

			// 登録情報のSLDルールごとの表示パラメータ
			// 例 c42:0:1:2,c50:1
			// c42はルール0番目と1番目のみ表示、c50はルール1番目のみを表示
			rule: null,

			// layers の指定がないと、eコミ地図設定で表示されたものが印刷される
			contentslayers: "",
			reflayers: "",
			kmllayers: "",
			overlaylayers: "",
			baselayer: ""
		};


		var data = {};
		Ext.applyIf(data, params);
		Ext.applyIf(data, defaultParams);
		$.ajax({
			url: url,
			type: "post",
			data: data,
			success: function(data, textStatus, jqXHR) {
				if(jQuery.isFunction(success)) {
					success(data);
				}
			}
		});
	},

	/**
	 * レイヤIDに対応するメタデータを更新します.
	 */
	updateMetadata: function(layerId) {
		var url = this.url.metadataUpdateURL;
		url += "?layer="+layerId;

		jQuery.ajax(url, {
			dataType: "text",
			async: true,
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: function(result) {
				console.log("updateMetadata: "+result)
			}
		});
	},

	/**
	 * ジオメトリを切り出します.
	 * @param {String} WKT 切り出されるジオメトリのWKT
	 * @param {String} layerId 切り出すレイヤのレイヤID
	 * @return {Object} 切り出し結果オブジェクト
	 */
	intersection: function(wkt, layerId) {
		var url = this.url.intersectionURL;

		var result = null;
		jQuery.ajax(url, {
			type: "post",
			dataType: "json",
			async: false,
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			data: {
				wkt: wkt,
				layer: layerId
			},
			success: function(data) {
				result = data;
			},
			error: function(data) {
				throw lang.__("An error occurred in cropping.");
			}
		});
		return result;
	},

	/**
	 * MGRSコードから経緯度へ変換します。
	 * @param {String} mgrs MGRSコード
	 * @return {JSONObject} 結果オブジェクト
	 */
	mgrs2lonlat: function(mgrs) {
		var url = this.url.mgrs2lonlatURL;

		var result = null;
		jQuery.ajax(url, {
			type: "get",
			dataType: "json",
			async: false,
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			data: {
				mgrs: mgrs
			},
			success: function(data) {
				result = data;
			},
			error: function(data) {
				throw lang.__("An error occurred during the conversion MGRS code into coordinates.");
			}
		});
		return result;
	},

	/**
	 * ランドマーク検索機能が有効か無効かをチェックします。
	 * @return {JSONObject} 結果オブジェクト（valid）
	 */
	landmarkValid: function() {
		var url = this.url.landmarkValidURL;
		var defer = jQuery.Deferred();
		jQuery.ajax(url, {
			type: "post",
			dataType: "json",
			async: true,
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	},

	/**
	 * 1件のランドマークデータを登録します。
	 * @param {String} landmark ランドマーク名
	 * @param {double} lon 10進経度
	 * @param {double} lat 10進緯度
	 * @return {JSONObject} 結果オブジェクト（landmark）
	 */
	landmarkRegist: function(landmark, lon, lat) {
		var url = this.url.landmarkRegistURL;
		var defer = jQuery.Deferred();
		jQuery.ajax(url, {
			type: "post",
			//params: 'landmark='+landmark+'&lon='+lon+'&lat='+lat,
			dataType: "json",
			async: true,
			data: {
				landmarkData: JSON.stringify([{
					landmark : landmark,
					lon : lon,
					lat : lat
				}])
			},
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	},

	/**
	 * ランドマーク名からランドマークデータを検索します。
	 * @param {String} landmark ランドマーク名
	 * @return {JSONObject} 結果オブジェクト（landmark,lon,lat）
	 */
	landmarkSearch: function(landmark) {
		var url = this.url.landmarkSearchURL;
		var defer = jQuery.Deferred();
		jQuery.ajax(url, {
			type: "post",
			dataType: "json",
			async: false,//取得結果を待つ必要があるので
			data: {
				landmarkData: JSON.stringify([{
					landmark : landmark
				}])
			},
			//headers: {
			//	"X-CSRF-Token": SaigaiTask.csrfToken
			//},
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	}

});

/**
 * Add memo API
 * @requires SaigaiTask/Map/API.js
 */
SaigaiTask.Map.API.Rakugaki = OpenLayers.Class({

	/**
	 * API オブジェクト
	 * @type {SaigaiTask.Map.API}
	 */
	api: null,

	initialize: function(api) {
		var me = this;
		me.api = api;
	},

	/**
	 * 保存
	 */
	save: function(kml) {
		var me = this;
		var url = me.api.contextPath+"/page/map/rakugaki/save/";

		var result = null;
		jQuery.ajax(url, {
			type: "post",
			dataType: "json",
			async: false,
			data: {
				kml: kml
			},
			success: function(data) {
				result = data;
			},
			error: function(data) {
				throw lang.__("Unable to save memo KML.");
			}
		});
		return result;
	},

	download: function(option) {

		var me = this;

		var defaultOption = {
			url: me.api.contextPath+"/page/map/rakugaki/download/",
			type: "get",
			dataType: "xml",
			async: false,
			success: function(data) {
				result = data;
			},
			error: function(data) {
				throw lang.__("Unable to download memo KML.");
			}
		}
		Ext.applyIf(option, defaultOption);

		var result = null;
		jQuery.ajax(option);
		return result;
	},

	lock: function() {
		var me = this;
		var defer = jQuery.Deferred();
		jQuery.ajax({
			url: me.api.contextPath+"/page/map/rakugaki/lock",
			type: "get",
			dataType: "json",
			async: true,
			cache: false,
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	},

	unlock: function() {
		var me = this;
		var defer = jQuery.Deferred();
		jQuery.ajax({
			url: me.api.contextPath+"/page/map/rakugaki/unlock",
			type: "get",
			dataType: "json",
			async: true,
			cache: false,
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	}
});


/**
 * Call AJAX API of disaster response system.
 * TODO: I will move to another file later.
 * @requires SaigaiTask/Map/API.js
 */
SaigaiTask.Map.SaigaiTaskAPI = OpenLayers.Class(SaigaiTask.Map.API, {

	/**
	 * コンテキストパス
	 * @type {String}
	 */
	contextPath: null,

	/**
	 * AJAXでデータを取得するためのURLの連想配列
	 * @type {Object.<String, String>}
	 */
	url: {
		//===============================================================
		// eコミマップのデータを取得するためのURL
		//===============================================================
		/**
		 * eコミマップ情報を取得するURL
		 * @type {String}
		 */
		ecommapInfoURL: "/map/ecommap/info",

		/**
		 * コンテンツ情報を取得するURL
		 * @type {String}
		 */
		contentsGetURL: "/map/ecommap/contents/get",

		/**
		 * コンテンツ情報を作成するURL
		 * @type {String}
		 */
		contentsCreateURL: "/map/contents/create/",

		/**
		 * コンテンツ情報を更新するURL
		 * @type {String}
		 */
		contentsUpdateURL: "/map/ecommap/contents/update",

		/**
		 * コンテンツ情報を削除するURL
		 * @type {String}
		 */
		contentsDeleteURL: "/map/ecommap/contents/delete",

		/**
		 * コンテンツ情報を矩形で検索するURL
		 * @type {String}
		 */
		contentsBboxURL: "/map/ecommap/contents/bbox",

		/**
		 * コンテンツ情報を属性・空間検索するURL
		 * @type {String}
		 */
		contentsSearchURL: "/map/ecommap/contents/search",

		/**
		 * コンテンツ情報の空間検索の検索範囲をWKTで取得するURL
		 * @type {String}
		 */
		contentsSearchRangeWKTURL: "/map/ecommap/contents/search/range/wkt",

		/**
		 * 属性情報を取得するURL
		 * @type {String}
		 */
		attrInfoURL: "/map/ecommap/attrInfo",

		/**
		 * SLDを取得するためのURL
		 * @type {String}
		 */
		sldURL: "/map/ecommap/sld",

		/**
		 * eコミにWFSリクエストをProxyするURL
		 * @type {String}
		 */
		wfsProxyURL: "/map/ecommap/wfsProxy?&url=",

		//===============================================================
		// 災害対応システムのデータを取得するためのURL
		//===============================================================
		/**
		 * 地図の表示状態（レイアウト）を読込・保存するURL
		 */
		layoutURL: "/map/layout/",

		/**
		 * この場所についてURL
		 */
		pointInfoURL: "/map/pointinfo/"
	},

	/**
	 * API初期化
	 */
	initialize: function(contextPath) {
		var me = this;
		me.contextPath = contextPath;
		// URL初期化
		var url = me.url;
		me.url = {};
		for(var idx in url) {
			me.url[idx] = contextPath+url[idx];
		}
	},

	/**
	 * Get the layout.
	 * @param data
	 * @param success
	 */
	loadLayout: function(data, success) {
		var url = this.url.layoutURL;
		jQuery.ajax(url, {
			dataType: "json",
			data: data,
			cache: false,
			async: false,
			success: function(layout){
				if(jQuery.isFunction(success)) {
					success(layout);
				}
			},
			error: function(data){
				// do nothing
			}
		});
	},

	/**
	 * レイアウトを保存します.
	 * @param data
	 * @returns jqXHR
	 */
	saveLayout: function(data) {
		// リクエストを送信する
		var url = this.url.layoutURL;
		return jQuery.ajax(url, {
			dataType: "json",
			cache: false,
			data: SaigaiTask.Map.param(data),
			success: function(data){
				// do nothing
			},
			error: function(data){
				// do nothing
			}
		});
	}
});


/**
 * Call the crisis management cloud system's AJAX API.
 * TODO: I will move to another file later.
 * @requires SaigaiTask/Map/API.js
 */
SaigaiTask.Map.SaigaiTask2API = OpenLayers.Class(SaigaiTask.Map.API, {

	/**
	 * Context path
	 * @type {String}
	 */
	contextPath: null,

	/**
	 * Associative array of URLs for acquiring data by AJAX
	 * @type {Object.<String, String>}
	 */
	url: {
		//===============================================================
		// URL for acquiring data of e-com map
		//===============================================================
//		/**
//		 * URL to acquire e-com map information
//		 * @type {String}
//		 */
//		ecommapInfoURL: "/map/ecommap/info",



		/**
		 * URL to acquire content information
		 * @type {String}
		 */
		contentsGetURL: "/page/map/ecommap/contents/get",

		/**
		 * URL to register content information
		 * @type {String}
		 */
		contentsCreateURL: "/page/map/ecommap/contents/create",

		/**
		 * URL to update content information
		 * @type {String}
		 */
		contentsUpdateURL: "/page/map/ecommap/contents/update",

		/**
		 * コンテンツ情報を削除するURL
		 * @type {String}
		 */
		contentsDeleteURL: "/page/map/ecommap/contents/delete",

		/**
		 * コンテンツ情報を矩形で検索するURL
		 * @type {String}
		 */
		contentsBboxURL: "/page/map/ecommap/contents/bbox",

//		/**
//		 * コンテンツ情報を属性・空間検索するURL
//		 * @type {String}
//		 */
//		contentsSearchURL: "/map/ecommap/contents/search",

		/**
		 * URL for acquiring search range of content information space search with WKT
		 * @type {String}
		 */
		contentsSearchRangeWKTURL: "/page/map/ecommap/contents/search/range/wkt",

		/**
		 * URL to acquire attribute information
		 */
		attrInfoURL: "/page/map/ecommap/attrInfo",

		/**
		 * URL to get the last update date of the layer
		 * @type {String}
		 */
		layerLastUpdateTimeURL: "/page/map/ecommap/updatetime",

		/**
		 * URL for acquiring SLD
		 * @type {String}
		 */
		sldURL: "/page/map/ecommap/sld",

		/**
		 * URL to proxy WFS request to e-mail
		 * @type {String}
		 */
		wfsProxyURL: "/page/map/ecommap/wfsProxy?session_token="+SaigaiTask.csrfToken+"&_csrf="+$("meta[name='_csrf']").attr("content")+"&url=",

		/**
		 * クリアリングハウスのメタデータを更新するURL
		 */
		metadataUpdateURL: "/page/map/updatemetadata",

		/**
		 * URL for space operation of geometry extraction
		 */
		intersectionURL: "/page/map/intersection",

		/**
		 * About this place URL
		 */
		pointInfoURL: "/page/map/pointinfo/",

		/**
		 * Convert URL from MGRS to latitude and longitude
		 */
		mgrs2lonlatURL: "/page/map/mgrs2lonlat",

		/**
		 * Save memo layer
		 */
		rakugakiSaveURL: "/map/rakugaki/save/",

		/**
		 * ランドマーク検索機能が有効か無効かのチェック
		 */
		landmarkValidURL: "/page/map/landmark/valid/",

		/**
		 * ランドマークデータの登録
		 */
		landmarkRegistURL: "/page/map/landmark/regist/",

		/**
		 * Landmark data search
		 */
		landmarkSearchURL: "/page/map/landmark/search/"

	},

	/**
	 * API initialization
	 */
	initialize: function(contextPath) {
		var me = this;
		me.contextPath = contextPath;
		// URL初期化
		var url = me.url;
		me.url = {};
		for(var idx in url) {
			me.url[idx] = contextPath+url[idx];
		}

		// SaigaiTask.Map.APIの初期化
		SaigaiTask.Map.API.prototype.initialize.apply(this, [contextPath]);
	}
});
