<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<head>
<style>
table{
	border: 1px solid gray;
}
table tr td,th {
	border: 1px solid gray;
	padding: 0 10px;
	vertical-align: top;
}
</style>
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript">
var contextPath = "<%=request.getContextPath()%>";
var jsessionid = "${cookie.JSESSIONID.value}";
var QueryString = function () {
	// This function is anonymous, is executed immediately and
	// the return value is assigned to QueryString!
	var query_string = {};
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		// If first entry with this name
		if (typeof query_string[pair[0]] === "undefined") {
			query_string[pair[0]] = decodeURIComponent(pair[1]);
		// If second entry with this name
		} else if (typeof query_string[pair[0]] === "string") {
			var arr = [ query_string[pair[0]],decodeURIComponent(pair[1]) ];
			query_string[pair[0]] = arr;
		// If third or later entry with this name
		} else {
			query_string[pair[0]].push(decodeURIComponent(pair[1]));
		}
	}
	return query_string;
	}();
var getClazz = function(jobj) {
	return jobj.hasClass("saigaitask") ? "saigaitask" : jobj.hasClass("ecommap") ? "ecommap" : null;
}
$(function() {

	var showResponse = function(clazz, ajax, jqXHR, textStatus, errorThrown) {

		var response = $("div.response."+clazz);

		// Request URL
		$("#request_url", response).html(ajax.url);

		// Status code
		$("#status_code", response).html(jqXHR.status+" "+jqXHR.statusText);

		// Response Headers
		$("textarea[name='response_header']", response)
		.text(jqXHR.getAllResponseHeaders());

		var responseTextOuter = $("#response_text_outer", response);
		var responseHtmlOuter = $("#response_html_outer", response);

		// Response
		responseTextOuter.css("display", "none");
		responseHtmlOuter.css("display", "none");
		if(jqXHR.responseText) {
			var contentType = jqXHR.getResponseHeader("Content-Type");
			if(contentType.indexOf("text/html")==0) {
				// remove old irame
				$("iframe", responseHtmlOuter).remove();

				var iframeSrc = "data:"+contentType+","+(jqXHR.responseText);
				var iframe = $("<iframe style='width: 400px; height: 300px;''>");
				iframe.attr("src", iframeSrc);
				responseHtmlOuter.append(iframe);

				responseHtmlOuter.css("display", "block");
			}
			else if(contentType.indexOf("json")!=-1) {
				var data = JSON.parse(jqXHR.responseText);
				$("textarea[name='response']", response).text(JSON.stringify(data, null, 4));
				responseTextOuter.css("display", "block");
			}
			else {
				$("textarea[name='response']", response).text(jqXHR.responseText);
				responseTextOuter.css("display", "block");
			}
		}
	}

	//
	// API 初期化
	//
	$("select[name='method']").on("change", function() {
		var method = $(this).val();
		var request = $(this).parents(".request");

		var requestDataOuter = $("#request_data_outer", request);
		requestDataOuter.css("display", "none");
		$("select[name='contentType']", request).val("");
		if(method=="POST" || method=="PUT" || method=="PATCH") {
			requestDataOuter.css("display", "block");
			$("select[name='contentType']", request).val("application/json");
		}
	});
	$("input[name='submit']").on("click", function() {
		var request = $(this).parents(".request");
		var clazz = getClazz(request);

		// build URL
		var method = $("select[name='method']", request).val();
		var version = $("select[name='version']", request).val();
		var resource = $("select[name='resource']", request).val();
		var path = $("input[name='path']", request).val();
		var baseurl = $("input[name='baseurl']", request).val();
		var url = baseurl+"/"+version+"/"+resource+"/"+path;

		// post data
		var contentType = null;
		var processData = true;
		var data = null;
		if(method=="POST" || method=="PUT" || method=="PATCH") {
			data = $("textarea[name='request_data']", request).val();
			//contentType = "application/json";
			contentType = $("select[name='contentType']", request).val();
		}
		// ファイル指定があればリクエストデータを捨てて
		// ファイル送信処理を行う
		var formData = null;
		{
			var files = [];
			$("input[type='file']", request).each(function() {
				var _files=$(this).prop("files");
				if(0<_files.length) {
					files.push(_files[0]);
				}
			});
			if(0<files.length) {
				formData = new FormData();
				for(var index in files) {
					formData.append("file", files[index]);
				}
				// contentType, processData set false
				data = formData;
				contentType = false;
				processData = false;
			}
		}
		if(data==null) data = {};

		// build Header
		var headers = {};
		if(!!jsessionid) headers["X-CSRF-Token"] = jsessionid;
		
		// add Authentication
		var useAuthenticationHeader = true;
		var mode = $("input[name='mode']:checked").val();
		if(mode=="oauth") {
			var oauthToken = $("input[name='oauth_token']", request).val();
			if(!!oauthToken) {
				if(useAuthenticationHeader) headers["Authorization"] = "Bearer "+oauthToken;
				else data.oauth_token = oauthToken;
			}
		}
		else if(mode=="apikey") {
			var apikey = $("input[name='apikey']", request).val();
			if(!!apikey) {
				if(useAuthenticationHeader) headers["Authorization"] = apikey;
				else data.api_key = apikey;
			}
		}

		$.ajax({
			url: url,
			method: method,
			headers: headers,
			contentType: contentType,
			dataType: "json",
			data: data,
			processData: processData
		}).done(function(data, textStatus, jqXHR) {
			showResponse(clazz, this, jqXHR, textStatus);
		}).fail(function(jqXHR, textStatus, errorThrown) {
			showResponse(clazz, this, jqXHR, textStatus);
		});
	});

	//
	// OAuth 初期化
	//
	// onchange
	var grantTypeSelect = $("select[name='grant_type']");
	grantTypeSelect.on("change", function() {
		var grantType = $(this).val();
		var inputs = {
			"consumer_key": "Consumer Key",
			"consumer_secret": "Consumer Secret",
			"authorization_code": "Authorization Code",
			"username": "Username",
			"password": "Password"
		};

		// Claer all input
		$("#oauth_field").empty();

		var enableds = [];
		// GrantType: Authorization Code Grant
		if("authorization_code"==grantType) {
			enableds = ["consumer_key", "consumer_secret", "authorization_code"];
		}
		// GrantType: Implicit Grant
		else if("implicit"==grantType) {
			enableds = ["consumer_key"];
		}
		// GrantType: Resource Owner Password Credentials
		else if("password"==grantType) {
			enableds = ["consumer_key", "consumer_secret", "username", "password"];
		}
		for(var idx in enableds) {
			var field = enableds[idx];
			var name = inputs[field];
			var span = $('<span>'+name+': <input type="text" name="'+field+'" value=""/></span><br/>');
			$("#oauth_field").append(span);
		}
	});
	grantTypeSelect.trigger("change");
	// initialize
	if(!!QueryString.ecommap_baseurl) {
		$(".ecommap input[name='baseurl']").val(QueryString.ecommap_baseurl);
	}
	if(QueryString.grant_type=="authorization_code"
			&& !!QueryString.code) {
		$(".saigaitask input[name='authorization_code']").val(QueryString.code);
	}
	else if(QueryString.grant_type=="implicit") {
		if(location.hash.indexOf("#access_token=")==0) {
			var accessToken = location.hash.substring("#access_token=".length);
			$(".saigaitask input[name='oauth_token']").val(accessToken);
			$('.saigaitask #oauth_get_access_token').css('display', "none");
		}
	}
	// onsubmit
	$("input[name='oauth_submit']").on("click", function() {
		var grantType = $("select[name='grant_type']").val();
		var consumerKey = $("input[name='consumer_key']").val();
		var consumerSecret = $("input[name='consumer_secret']").val();
		var request = $(this).parents(".request");
		var clazz = getClazz(request);
		// GrantType: Authorization Code Grant
		if("authorization_code"==grantType) {
			var redirectUri = "/api/tools/explorer/?grant_type=authorization_code";
			var code = $("input[name='authorization_code']", request).val();
			// Step 1: Get authorization_code
			if(code.length==0) {
				var url = contextPath+"/oauth2/authorize/?response_type=code&client_id="+consumerKey+"&redirect_uri="+redirectUri;
				location.href = url;
			}
			// Step 2: Exchange authorization_code for access_token
			else {
				// POST
				var url = contextPath+"/oauth2/token/";
				$.ajax({
					url: url,
					method: "POST",
					data: {
						"grant_type": "authorization_code",
						"code": code,
						"client_id": consumerKey,
						"client_secret": consumerSecret,
						"redirect_uri": redirectUri
					}
				}).done(function(data, textStatus, jqXHR) {
					showResponse(clazz, this, jqXHR, textStatus);
					if(!!data.access_token) {
						$("input[name='oauth_token']", request).val(data.access_token);
						$('#oauth_get_access_token', request).css('display', "none");
					}
				}).fail(function(jqXHR, textStatus, errorThrown) {
					showResponse(clazz, this, jqXHR, textStatus);
				});
			}
		}
		// GrantType: Implicit Grant
		else if("implicit"==grantType) {
			var redirectUri = "/api/tools/explorer/?grant_type=implicit";
			var url = contextPath+"/oauth2/authorize/?response_type=token&client_id="+consumerKey+"&redirect_uri="+redirectUri;
			location.href = url;
		}
		// GrantType: Resource Owner Password Credentials
		else if("password"==grantType) {
			var redirectUri = "/api/tools/explorer/?grant_type=password";
			var username = $("input[name='username']", request).val();
			var password = $("input[name='password']", request).val();
			// POST
			var url = contextPath+"/oauth2/token/";
			$.ajax({
				url: url,
				method: "POST",
				data: {
					"grant_type": "password",
					"code": code,
					"client_id": consumerKey,
					"client_secret": consumerSecret,
					"username": username,
					"password": password,
					"redirect_uri": redirectUri
				}
			}).done(function(data, textStatus, jqXHR) {
				showResponse(clazz, this, jqXHR, textStatus);
				if(!!data.access_token) {
					$("input[name='oauth_token']", request).val(data.access_token);
					$('#oauth_get_access_token', request).css('display', "none");
				}
			}).fail(function(jqXHR, textStatus, errorThrown) {
				showResponse(clazz, this, jqXHR, textStatus);
			});
		}
		else {
			alert("not support grant_type: "+grantType);
		}
	});
	
	
	// mode radio
	$("input[name='mode']").on("change", function() {
		var radio = $("input[name='mode']:checked");
		var mode = radio.val();
		
		var oauthConfigDiv = $("#oauth_config");
		var apikeyConfigDiv = $("#apikey_config");
		
		if(mode=="oauth") {
			oauthConfigDiv.css("display", "block");
			apikeyConfigDiv.css("display", "none");
		}
		else if(mode=="apikey") {
			oauthConfigDiv.css("display", "none");
			apikeyConfigDiv.css("display", "block");
		}
	});
	// init mode radio checked
	var initMode = "oauth";
	//var initMode = "apikey";
	$("input[name='mode']").each(function() {
		var radio = $(this);
		if(radio.prop("value")==initMode) {
			radio.prop("checked", true);
			radio.change();
		}
	});
});
function toggleOAuthDiv(clazz) {
	var oauthDiv = $('.'+clazz+' #oauth_get_access_token');
	oauthDiv.css('display', oauthDiv.css('display')=="none" ? "block" : "none");
}
</script>
</head>
<body>
<table>
	<tr>
		<th>SaigaiTask</th>
		<th>ecommap</th>
	</tr>
	<tr>
		<td>
			<div class="saigaitask request">
				<div id="request_outer">
					<span>Request: </span>
					<label><input type="radio" name="mode" value="oauth"/>OAuth</label><label>
					<input type="radio" name="mode" value="apikey"/>API KEY</label>
					<div id="apikey_config">
						<span>API KEY: <input type="text" name="apikey" /></span>
					</div>
					<div id="oauth_config">
					<span>Access Token: <input
					type="text" name="oauth_token" /> <a href="javascript:void(0)" onclick="toggleOAuthDiv('saigaitask');">GET</a></span><br />
					<div id="oauth_get_access_token" style="border: 1px solid gray; padding: 5px; width: 360px;">
						<h2 style="margin: 0">Get Access Token</h2><br/>
						<span>Grant Type: <select name="grant_type">
							<option value="authorization_code">Authorization Code</option>
							<option value="implicit">Implicit</option>
							<option value="password">Resource Owner Password Credentials</option>
						</select></span><br/>
						<div id="oauth_field">
						</div>
						<input type="button" name="oauth_submit" value="<%=lang.__("Send")%>"/>
					</div>
					</div>
					<div>
					<select name="saigataskAPIList">
						<option></option>
					</select>
					<br/>
					<script type="text/javascript">
					apiInfos = [];
					/*
					// API v1
					{
						apiInfos.push({
							group: "/api/v1 eコミマップ情報API",
							path: "GET /api/v1/ecommapInfo/"
						});
						apiInfos.push({
							group: "/api/v1 班情報API",
							path: "GET /api/v1/groupInfos/[localgovinfoid]"
						});
						apiInfos.push({
							group: "/api/v1 ユニット情報API",
							path: "GET /api/v1/unitInfos/[groupid]"
						});
						apiInfos.push({
							group: "/api/v1 ユーザ情報API",
							path: "GET /api/v1/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v1 ユーザ情報API",
							path: "POST /api/v1/userInfo/"
						});
						apiInfos.push({
							group: "/api/v1 ユーザ情報API",
							path: "PUT /api/v1/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v1 ユーザ情報API",
							path: "PATCH /api/v1/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v1 ユーザ情報API",
							path: "DELETE /api/v1/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v1 災害データ同期API",
							path: "POST /api/v1/trackdatasync/confirmtrackmapping"
						});
						apiInfos.push({
							group: "/api/v1 災害データ同期API",
							path: "POST /api/v1/trackdatasync/confirmdatasync"
						});
						apiInfos.push({
							group: "/api/v1 災害データ同期API",
							path: "POST /api/v1/trackdatasync/execdatasync"
						});
						apiInfos.push({
							group: "/api/v1 アラームAPI",
							path: "POST /api/v1/alarm/"
						});
						apiInfos.push({
							group: "/api/v1 インシデント管理API",
							path: "GET /api/v1/trackDatas/"
						});
						apiInfos.push({
							group: "/api/v1 インシデント管理API",
							path: "GET /api/v1/trackData/[trackdataid]"
						});
						apiInfos.push({
							group: "/api/v1 インシデント管理API",
							path: "POST /api/v1/trackData/"
						});
						apiInfos.push({
							group: "/api/v1 インシデント管理API",
							path: "PUT /api/v1/trackData/[trackdataid]"
						});
						apiInfos.push({
							group: "/api/v1 インシデント管理API",
							path: "PATCH /api/v1/trackData/[trackdataid]"
						});
						apiInfos.push({
							group: "/api/v1 インシデント管理API",
							path: "DELETE /api/v1/trackData/[trackdataid]"
						});
						apiInfos.push({
							group: "/api/v1 認証API",
							path: "GET /api/v1/login/;JSESSIONID=[JSESSIONID]"
						});
						apiInfos.push({
							group: "/api/v1 認証API",
							path: "POST /api/v1/login/",
							requestData: {
								trackdataid: "[trackdataid]",
								langCode: "[langCode]",
								disasterid: "[disasterid]"
							}
						});
						apiInfos.push({
							group: "/api/v1 認証API",
							path: "POST /api/v1/logout/;JSESSIONID=[JSESSIONID]"
						});
					}
					*/

					// API v2
					{
						apiInfos.push({
							group: "/api/v2 ログイン情報取得API",
							path: "GET /api/v2/me/"
						});
						apiInfos.push({
							group: "/api/v2 eコミマップ情報API",
							path: "GET /api/v2/ecommapInfo/"
						});
						apiInfos.push({
							group: "/api/v2 班情報API",
							path: "GET /api/v2/groupInfos/[localgovinfoid]"
						});
						apiInfos.push({
							group: "/api/v2 ユニット情報API",
							path: "GET /api/v2/unitInfos/[localgovinfoid]"
						});
						apiInfos.push({
							group: "/api/v2 ユーザ情報API",
							path: "GET /api/v2/userInfos/[groupid]"
						});
						apiInfos.push({
							group: "/api/v2 ユーザ情報API",
							path: "GET /api/v2/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v2 ユーザ情報API",
							path: "POST /api/v2/userInfo/"
						});
						apiInfos.push({
							group: "/api/v2 ユーザ情報API",
							path: "PUT /api/v2/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v2 ユーザ情報API",
							path: "PATCH /api/v2/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v2 ユーザ情報API",
							path: "DELETE /api/v2/userInfo/[userid]"
						});
						apiInfos.push({
							group: "/api/v2 災害データ同期API",
							path: "POST /api/v2/trackdatasync/confirmtrackmapping"
						});
						apiInfos.push({
							group: "/api/v2 災害データ同期API",
							path: "POST /api/v2/trackdatasync/confirmdatasync"
						});
						apiInfos.push({
							group: "/api/v2 災害データ同期API",
							path: "POST /api/v2/trackdatasync/execdatasync"
						});
						apiInfos.push({
							group: "/api/v2 アラームAPI",
							path: "POST /api/v2/alarm/"
						});
						apiInfos.push({
							group: "/api/v2 写真投稿用レイヤ情報API",
							path: "POST /api/v2/sendlayerInfos/"
						});
						apiInfos.push({
							group: "/api/v2 認証API",
							path: "GET /api/v2/login/;JSESSIONID=[JSESSIONID]"
						});
						apiInfos.push({
							group: "/api/v2 認証API",
							path: "POST /api/v2/login/",
							requestData: {
								trackdataid: "[trackdataid]",
								langCode: "[langCode]"
							}
						});
						apiInfos.push({
							group: "/api/v2 認証API",
							path: "POST /api/v2/logout/;JSESSIONID=[JSESSIONID]"
						});
						apiInfos.push({
							group: "/api/v2 投稿アプリ認証API",
							path: "POST /api/v2/mobileqrcodeInfo/auth",
							requestData: {
								id: "[id]",
								clientKeyEncryption: "[clientKeyEncryption]"
							}
						});
					}

					// 利用者画面API
					{
						apiInfos.push({
							group: "/page 地図画面API",
							path: "GET /page/map/?menutaskid=[menutaskid]&menuid=[menuid]&insertFeature=true&insertFeatureData=[insertFeatureData]&oninsertFeature=close"
						});
						apiInfos.push({
							group: "/page 地図画面API",
							path: "POST /page/map/"
						});
						apiInfos.push({
							group: "/page 地図画面 eコミマップ情報API",
							path: "GET /page/map/ecommapInfo?menutaskid=[menutaskid]&menuid=[menuid]"
						});

					}

					$(function(){
						var select = $(".saigaitask select[name=saigataskAPIList]");
						select.change(function() {
							// 例： GET /api/v1/trackData/[trackdataid]
							//var api = select.val();
							var apiInfosIdx = select.val();
							var apiInfo = apiInfos[apiInfosIdx];
							var api = apiInfo.path;
							var elems = api.split(" ");

							// methodセレクトボックスを変更
							var method = elems[0];
							var methodSel = $(".saigaitask select[name=method]");
							methodSel.val(method);
							methodSel.change();

							var path = elems[1]; // 例： /api/v1/trackData/[trackdataid]
							// REST API の場合
							if(path.startsWith("/api")) {
								path = path.substring("/api/".length); // 例： v1/trackData/[trackdataid]
								// versionセレクトボックスを変更
								var version = path.substring(0, path.indexOf("/"));
								$(".saigaitask select[name=version]").val("api/"+version);
								path = path.substring(path.indexOf("/"));// 例： /trackData/[trackdataid]
							}
							// 利用者画面APIの場合
							if(path.startsWith("/page")) { // 例： /page/map/
								// versionセレクトボックスを変更
								$(".saigaitask select[name=version]").val("page");
								path = path.substring("/page".length); // 例： /map/
							}

							// resourceセレクトボックスを変更
							var resource = $(".saigaitask select[name=resource]");
							var maxMatchLength = 0;
							$("option", resource).each(function() {
								var opt = $(this);
								if(path.startsWith("/"+opt.val())) {
									// 今回マッチしたAPIの方が長い場合に更新
									var matchLength = opt.val().length;
									if(maxMatchLength<matchLength) {
										maxMatchLength = matchLength;
										resource.val(opt.val());
										// pathテキストボックスにデフォルト値を設定
										$(".saigaitask input[name=path]").val(path.substring(opt.val().length+2));
									}
								}
							});

							// request_dataテキストエリアにデフォルト値を追加
							if(!!apiInfo.requestData) {
								var requestData = $(".saigaitask textarea[name='request_data']");
								if(typeof apiInfo.requestData=="object") {
									requestData.text(JSON.stringify(apiInfo.requestData, null, 4));
								}
							}
						});
						var appendOptgroupIfNotExists = function(select, optgroupLabel) {
							//var optgroup = $("optgroup[label='"+optgroupLabel+"']", select);
							var optgroup = $("optgroup", select);
							var find = null;
							optgroup.each(function(){
								if($(this).attr("label").trim()==optgroupLabel.replace(/&nbsp;/g, " ").trim()) {
									find = $(this);
								}
							});
							if(find!=null) return find;
							// まだなければ追加
							var optgroup = $("<optgroup label='"+optgroupLabel+"'></optgroup>'");
							select.append(optgroup);
							return optgroup;

						};
						for(var apiInfosIdx in apiInfos) {
							var apiInfo = apiInfos[apiInfosIdx];
							// METHOD を除いたパス: /api/v1/trackData/[trackdataid]
							var path = apiInfo.path.split(" ")[1];

							// /api/v1 /api/v2 /page のいずれかの optgroup を追加
							var optgroupLabel =
								path.startsWith("/api") ? path.substring(0, path.indexOf("/", "/api/".length)) :
								path.startsWith("/page") ? "/page" : "other";
							optgroupLabel += " ===================================";
							appendOptgroupIfNotExists(select, optgroupLabel);

							// APIグループの optgroup を追加
							optgroupLabel = /*"&nbsp;&nbsp;"+*/apiInfo.group;
							optgroup = appendOptgroupIfNotExists(select, optgroupLabel);

							// このAPIの option を追加
							var option = $("<option value='"+apiInfosIdx+"'>"+apiInfo.path+"</option>");
							optgroup.append(option);
						}
					});
					</script>
					<select name="method">
						<option>GET</option>
						<option>POST</option>
						<option>PUT</option>
						<option>PATCH</option>
						<option>DELETE</option>
					</select>
					<input type="text" name="baseurl" value="<%=request.getContextPath()%>" />
					<select name="version">
						<option>api/v2</option>
						<option>api/v1</option>
						<option>page</option>
					</select> <select name="resource">
							<option>me</option>
							<option>ecommapInfo</option>
							<option>groupInfos</option>
							<option>unitInfos</option>
							<option>userInfo</option>
							<option>userInfos</option>
							<option>trackdatasync/confirmtrackmapping</option>
							<option>trackdatasync/confirmdatasync</option>
							<option>trackdatasync/execdatasync</option>
							<option>alarm</option>
							<option>trackDatas</option>
							<option>trackData</option>
							<option>login</option>
							<option>logout</option>
							<option>sendlayerInfos</option>
							<option>map</option>
							<option>menutaskInfos</option>
							<option>taskmenuInfos</option>
							<option>listFeatures</option>
							<option>listFeaturesKarte</option>
							<option>mobileqrcodeInfo/auth</option>
					</select> <input type="text" name="path" /> <input type="button"
						name="submit" value="submit" /> <br /> <br />
						</div>
				</div>

				<div id="request_data_outer" style="display: none;">
					<span>Content-Type: </span><select name="contentType">
						<option></option>
						<option>application/json</option>
						<option>application/x-www-form-urlencoded</option>
					</select><br/>
					<span>Request Data: </span><br />
					<textarea name="request_data" rows="8" cols="50"></textarea>
					<br />
				</div>

			</div>
		</td>
		<td>
			<div class="ecommap request">
				<div id="request_outer">
					<span>Request:</span><br /> <span>Access Token: <input
					type="text" name="oauth_token" /> <!-- <a href="javascript:void(0)" onclick="toggleOAuthDiv('ecommap');">GET</a> --> </span><br />
					<!--
					<div id="oauth" style="border: 1px solid gray; padding: 5px; width: 360px;">
						<h2 style="margin: 0">Get Access Token</h2><br/>
						<span>Grant Type: <select name="grant_type">
							<option value="authorization_code">Authorization Code</option>
							<option value="implicit">Implicit</option>
							<option value="password">Resource Owner Password Credentials</option>
						</select></span><br/>
						<div id="oauth_field">
						</div>
						<input type="button" name="oauth_submit" value="送信"/>
					</div> -->
					<select name="method">
						<option>GET</option>
						<option>POST</option>
						<option>PUT</option>
						<option>PATCH</option>
						<option>DELETE</option>
					</select>
					<input type="text" name="baseurl" value="" />
					<select name="version">
						<option>rest</option>
					</select> <select name="resource">
						<option>user</option>
						<option>group</option>
						<option>map</option>
						<option>layer</option>
						<option>maplayer</option>
						<option>sld</option>
						<option>sldicon</option>
						<option>feature</option>
						<option>featurefile</option>
						<option>upload/contents</option>
						<option>upload/icon</option>
					</select> <input type="text" name="path" /> <input type="button"
						name="submit" value="submit" /> <br /> <br />
				</div>

				<div id="request_data_outer" style="display: none;">
					<span>Content-Type: </span><select name="contentType">
						<option></option>
						<option>application/json</option>
						<option>application/x-www-form-urlencoded</option>
					</select><br/>
					<span>Request Data: </span><br />
					<textarea name="request_data" rows="8" cols="50"></textarea>
					<!-- とりあえず5個まで -->
					<br/><input type="file"/>
					<br/><input type="file"/>
					<br/><input type="file"/>
					<br/><input type="file"/>
					<br/><input type="file"/>
					<br />
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			<div class="saigaitask response">
				<div id="request_url_outer">
					<span>Request URL: </span><span id="request_url"></span><br />
				</div>

				<div id="response_header_outer">
					<span>Response Header: </span><span id="status_code"></span><br />
					<textarea name="response_header" rows="8" cols="50"></textarea>
					<br />
				</div>

				<div id="response_text_outer">
					<span>Response: </span><br />
					<textarea name="response" rows="20" cols="50"></textarea>
					<br />
				</div>
				<div id="response_html_outer" style="display: none;">
					<span>Response(HTML):</span><br />
				</div>
			</div>
		</td>
		<td>
			<div class="ecommap response">
				<div id="request_url_outer">
					<span>Request URL: </span><span id="request_url"></span><br />
				</div>

				<div id="response_header_outer">
					<span>Response Header: </span><span id="status_code"></span><br />
					<textarea name="response_header" rows="8" cols="50"></textarea>
					<br />
				</div>

				<div id="response_text_outer">
					<span>Response: </span><br />
					<textarea name="response" rows="20" cols="50"></textarea>
					<br />
				</div>
				<div id="response_html_outer" style="display: none;">
					<span>Response(HTML):</span><br />
				</div>
			</div>
		</td>
	</tr>
</table>
</body>
