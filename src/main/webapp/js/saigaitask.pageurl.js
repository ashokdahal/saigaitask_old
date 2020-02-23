/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/**
 * ページのURLリクエストパラメータを管理します.
 * とりあえずSingleton.
 */
SaigaiTask.PageURL = {

	/**
	 * 初期化時のリクエストパラメータオブジェクト
	 * @type {Object}
	 */
	initParams: null,

	/**
	 * リクエストパラメータオブジェクト
	 * @type {Object}
	 * params.time {String} 表示データ時間(ISO8601)
	 */
	params: null,

	/**
	 * 基底URL
	 */
	baseurl: SaigaiTask.contextPath + "/page/",

	/**
	 * @param params リクエストパラメータオブジェクト
	 */
	set: function(params) {
		var me = this;
		me.params = params;
	},

	/**
	 * @param initParams リクエストパラメータオブジェクト
	 */
	setInitParams: function(initParams) {
		var me = this;
		me.initParams = initParams;
		// paramsには値をコピーする
		me.params = {};
		me.override(initParams);
	},

	/**
	 * 指定したリクエストパラメータのみ上書きします.
	 * @returns {SaigaiTask.PageURL}
	 */
	override: function(params) {
		this.params = $.extend(this.params, params);
		// 検索条件JSON形式、Objectになってしまっていたら、Stringに直す
		if(typeof params.conditionValue=="object") {
			this.params.conditionValue = JSON.stringify(params.conditionValue);
		}
		return this;
	},

	/**
	 * params に保存されたリクエストパラメータを、
	 * /page/ にGETで移動します.
	 */
	move: function() {
		var method = "GET";
		if(method=="GET") {
			location.href = this.getUrl();
		}
		else {
			$.form(this.baseurl, this.params, "GET").submit();
		}
	},

	/**
	 * 設定したパラメータで、コンテンツ部分だけを読み込み
	 */
	reloadContent: function() {
		var params = this.params;
		// 意思決定支援モードの場合はパラメータを渡す
		var decisionsupport = SaigaiTask.PageURL.initParams.decisionsupport ? true : false;
		this.baseurl+params.pagetype+"/content?"+this.getQuery() + "&decisionsupport=" + decisionsupport
		reloadContent(this.baseurl+params.pagetype+"/content?"+this.getQuery() + "&decisionsupport=" + decisionsupport);
	},

	/**
	 * 集計表を更新
	 */
	updateSummary: function(){
		var params = this.params;
		// 意思決定支援モードの場合はパラメータを渡す
		var decisionsupport = SaigaiTask.PageURL.initParams.decisionsupport ? true : false;
		this.baseurl+params.pagetype+"/content?"+this.getQuery() + "&decisionsupport=" + decisionsupport
		reloadContent(this.baseurl+params.pagetype+"/updateSummary?"+this.getQuery() + "&decisionsupport=" + decisionsupport);
	},

	getQuery: function(added) {
		var params = {};
		var paramsList= [this.params];
		if(typeof added=="object") paramsList.push(added);
		for(var idx in paramsList) {
			var _params = paramsList[idx];
			for(var key in _params) {
				var value = _params[key];
				if(value!=null&&value!="") {
					params[key] = value;
				}
			}
		}
		return toSAStrutsParam(params);
	},

	/**
	 * パラメータ付きURL文字列を取得する.
	 * @param {Object} 追加パラメータ
	 */
	getUrl: function(added) {
		var origin = location.origin;
		if(!location.origin) {
			origin = location.protocol + "//" + location.host;
		}

		return origin+this.baseurl+"?"+this.getQuery(added);
	},

	/**
	 * データ表示時間パラメータを取得
	 * @return ISO8601日付文字列（指定がなければ null)
	 */
	getTime: function() {
		var timeStr = SaigaiTask.PageURL.params.time;
		return (!!timeStr ? new Date(timeStr) : null);
	},

	/**
	 * レイヤ個別の時間パラメータ(layertimes)から時間パラメータを取得
	 * @return ISO8601日付文字列（指定がなければ null)
	 */
	getLayerTime: function(_layerId) {
		var layertimesStr = SaigaiTask.PageURL.params.layertimes;
		if(typeof layertimesStr=="string") {
			var layerId = null;
			var layerTime = null;
			var arr = layertimesStr.split(",");
			for(var idx in arr) {
				if(layerId==null) layerId = arr[idx];
				else {
					layerTime = arr[idx];
					if(_layerId==layerId) return layerTime;
					// clear
					layerId = layerTime = null;
				}
			}
		}
		return null;
	},

	timeParam: function() {
		var time = this.getTime();
		var timeParam = "";
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			timeParam = "&time="+iso8601Time;
		}
		return timeParam;
	},

	/**
	 * 時間パラメータでタイムゾーン分時間をシフトするかどうかの設定
	 * eコミマップがタイムゾーンをDBでもっていないための対策
	 * @type Boolean
	 */
	CONFIG_SHIFT_TIMEZONE_OFFSET: false,

	/**
	 * 画面を移動する前に行う処理
	 */
	beforeMove: function(pageType) {
		if (pageType == 'map') {
			SaigaiTask.Page.List.getAccordionHeaderState();
		}
	},

	
	isDownload: false,

	/**
	 * ロードマスクを表示させずにダウンロードする。
	 * 
	 * @param opt URLまたはダウンロード関数またはjQuery.ajaxのようなパラメータ
	 * 
	 * 利用例）URLを直接渡す
	 *         SaigaiTask.PageURL.download("http://xxx");
	 *         
	 * 利用例）form.submit()のダウンロードの場合は関数を渡す
	 *         SaigaiTask.PageURL.download(function(){
	 *           form.submit();
	 *         });
	 */
	download: function(opt) {
		
		var me = this;
		
		if(typeof opt=="string") {
			var url = opt;

			// location.href方式
			// beforeunloadイベントが発火されるので、isDownloadフラグでダウンロード判定する。
			// フラグをfalseにする場合は少し遅延させないといけない
			SaigaiTask.PageURL.download(function(){
				location.href = url;
			});

			// window.open方式
			// beforeunloadイベントが発火されないのでフラグ管理は不要。
			// ダウンロードが始まるまではブランクページが表示される。
			// ダウンロードファイルの生成に時間がかかる場合はブランクページで待たされるので、
			// 生成に時間がかかるものは進捗状況を出せるようにサーバ側も含めて修正した方がよいかもしれない。
			//window.open(url, "_blank");
			
			// iframe方式
			// beforeunloadイベントが発火されないのでフラグ管理は不要。
			// ※chromeでは onload でダウンロード完了を検知できないので、ダウンロード状況を出せない
			/*
			// ダウンロード状況ダイアログ
			var dialog = $("<div id='download-dialog' title='ダウンロード状況'></div>");
			dialog.html("<span>ダウンロードを開始しています。<br/>しばらくお待ちください。</span>");
			dialog.dialog();
			var iframes = '<iframe id="downloader" width="0" height="0" frameborder="0" src="' + url + '"/>';
			$(iframes).appendTo('body').load(function(){
			    //エラーがあった場合、サーバー側からエラーメッセージが返されるので、それを表示
			    var body = $(this).contents().find('body').text();
			    if(body != ''){
					dialog.html(body);
			    }
			    // ダウンロード完了の場合
			    else {
					dialog.html("<span>ダウンロードが完了しました。</span>");
			    }
			    //iframeは自動的に閉じる
			    setTimeout(function() {
			        $('#downloader').remove();
			    },300);
			});
			*/
		}
		else if(typeof opt=="function") {
			var execDownload = opt;

			// ダウンロードフラグをたてる
			me.isDownload = true;

			// ダウンロード関数実行
			execDownload();

			// ダウンロードフラグを少し遅延させて戻す
			setTimeout(function(){
				me.isDownload = false;
			}, 300);
		}
		else if(typeof opt=="object") {
			// form.submit方式
			// POSTで送信することも可能
			
			var form = $('<form class="download-form" width="0" height="0"/>');
			form.attr({
				action: opt.url,
				method: opt.method || "GET",
				target: opt.target || "_self" // _blank
			});

			// データがあれば input hidden で追加
			var data = opt.data || {};
			for(var name in data) {
				var value = data[name];
				$('<input>').attr({"type":"hidden","name":name,"value":value}).appendTo(form);
			}

			// session_token を追加
			$('<input>').attr({"type":"hidden","name":"session_token","value":SaigaiTask.csrfToken}).appendTo(form);
			
			// _csrf を追加
			var _csrf = $("meta[name='_csrf']").attr("content");
			$('<input>').attr({"type":"hidden","name":"_csrf","value":_csrf}).appendTo(form);
			
			form.appendTo('body');
			
			SaigaiTask.PageURL.download(function(){
				form.submit();
			});
		}
	}
};

/**
 * オブジェクトをSAStrutsのクエリ文字列に変換する.
 * @param {Object} obj
 * @return {String} クエリ文字列
 */
SaigaiTask.toSAStrutsParam = window.toSAStrutsParam = function(obj) {
	var prefix =null;
	var params = [];
	var add = function(key, value) {
		value = value == null ? "" : value;
		var doEncode = key!="popup";
		params[params.length] = encodeURIComponent(key) + "=" + (doEncode ? encodeURIComponent(value) : value);
	};
	/**
	 * @param prefix HTTPリクエストパラメータ名
	 * @param obj HTTPリクエストパラメータ値
	 */
	var buildParams = function(prefix, obj) {
		if(jQuery.isArray(obj)) {
			jQuery.each(obj, function(idx, value) {
				buildParams(prefix+"["+idx+"]", value);
			});
		}
		else if(jQuery.type(obj)==="object") {
			var name = null;
			for(name in obj) {
				buildParams(prefix+"."+name, obj[name]);
			}
		}
		else {
			add(prefix, obj);
		}
	};

	for(prefix in obj) {
		buildParams(prefix, obj[prefix]);
	}

	return params.join("&").replace(/%20/g, "+");
};


SaigaiTask.parseURL = function(url) {

	var parser = document.createElement('a'),
	searchObject = {},
	queries, split, i;

	// Let the browser do the work
	parser.href = url;

	// Convert query string to object
	queries = parser.search.replace(/^\?/, '').split('&');
	for( i = 0; i < queries.length; i++ ) {
		split = queries[i].split('=');
		searchObject[split[0]] = split[1];
	}

	return {
		protocol: parser.protocol,
		host: parser.host,
		hostname: parser.hostname,
		port: parser.port,
		pathname: parser.pathname,
		search: parser.search,
		searchObject: searchObject,
		hash: parser.hash,
		toURL: function() {
			var obj = this;
			var url = obj.protocol + "//" + obj.host + obj.pathname;
			var params = [];
			for(var name in obj.searchObject) {
				var value = obj.searchObject[name];
				params.push(name+"="+value);
			}
			url += "?"+params.join("&");
			return url;
		}
	};

}
