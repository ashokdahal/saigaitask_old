<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript">if(!!console && !!console.time) console.time("loadHeadTime");</script>
<jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<c:if test="${0<fn:length(pageDto.includeHeadJSP)}">
	<jsp:include page="${pageDto.includeHeadJSP}"></jsp:include>
</c:if>

<script type="text/javascript">
console.time("layoutTime");
SaigaiTask.PageURL.setInitParams(${pageDto.formJSON});
if(typeof SaigaiTask.Page=="undefined") {
	SaigaiTask.Page = {};
}
Ext.apply(SaigaiTask.Page, {
	trackData: {
		id: "${f:h(pageDto.trackData.id)}",
		starttime: ${empty pageDto.trackData.starttime.time ? 0 : pageDto.trackData.starttime.time},
		endtime: ${empty pageDto.trackData.endtime.time ? 0 : pageDto.trackData.endtime.time}
	},
	menuprocessInfo: {
		id: "${f:h(pageDto.menuprocessInfo.id)}",
		name: "${f:h(pageDto.menuprocessInfo.name)}"
	},
	menutaskInfo: {
		id: "${f:h(pageDto.menutaskInfo.id)}",
		name: "${f:h(pageDto.menutaskInfo.name)}"
	},
	menuInfo: {
		id: "${f:h(pageDto.menuInfo.id)}",
		name: "${f:h(pageDto.menuInfo.name)}"
	},
	localgovInfo: {
		pref: "${f:h(pageDto.localgovInfo.pref)}",
		city: "${f:h(pageDto.localgovInfo.city)}"
	},
	saigaiName: "${f:h(pageDto.saigaiName)}",
	/**
	 * 自分以外のメニューフラグ
	 * @type {Boolean}
	 */
	othermenu: ${f:h(pageDto.othermenu)},
	/**
	 * 全画面表示フラグ
	 * @type {Boolean}
	 */
	fullscreen: ${f:h(pageDto.fullscreen)},
	/**
	 * 訓練モードフラグ
	 * @type {Boolean}
	 */
	training: ${pageDto.trackData!=null && pageDto.trackData.trainingplandataid!=null && pageDto.trackData.trainingplandataid > 0},
	/**
	 * 意思決定支援フラグ
	 * @type {Boolean}
	 */
	decisionsupport: ${f:h(pageDto.isDecisionSupport())}
});
$(function(){

	//FIXME!
	//IE9でjQueryのdialogが正常に表示できず、クローズすらできない

	// URL表示の実装
	$("#url-button").on('click' ,function(){
		// URL表示用テキスト
		var text = $("<input type='text' style='width: 100%;' value='"+SaigaiTask.PageURL.getUrl()+"' readonly/>");
		// ダイアログ表示
		var div = $("<div>");
		text.appendTo(div);
		div.dialog({
			title: "<%=lang.__("URL display")%>",
			modal: true
		});
	});

	$("a#menubox").click(function(event) {
		var url = SaigaiTask.PageURL.getUrl();
		//alert(url);
		url = encodeURIComponent(url);

	    $.ajax({
	        type: "GET",
	        async: false,
	        url: "seturl",
	        data: "url="+url,
	        success: function(){}
	    });

	});
});

/** ボタンの無効化
 * @param id		無効化するボタンのID
 * @param mode		無効化するアクションの種類（定数で定義）
 * */
var BUTTON_MODE_ONCLICK = 0;
var BUTTON_MODE_CLICK = 1;
function disableButton(id, mode){
	//ボタンの無効化
	$(id).addClass("lgray");
	$(id).removeClass("blue");

	//モードによって無効化するアクションを変更する
	switch(mode){
		case this.BUTTON_MODE_ONCLICK:
			$(id).removeAttr('onclick');
			break;
		case this.BUTTON_MODE_CLICK:
			$(id).off('click');
			break;
		default:
			break;
	}
}

//一括変更ダイアログ表示
function slimerDialog(){

	// 時間指定中なら一括変更禁止とする
	if(!!SaigaiTask.PageURL.getTime()) {
		alert("<%=lang.__("Can't change all during time set.")%>");
		return;
	}

	if(SaigaiTask.Edit.proceed) {
		alert("<%=lang._E("Bulk change in processing. \n Wait for completion.")%>");
		return;
	}
	var slimerDialog = $("#slimer-dialog");
	if (slimerDialog.length == 0)
		alert("<%=lang.__("In this page, you can not change in a batch.")%>");
	slimerDialog.dialog({
		title: "<%=lang.__("Bulk change")%>",
		minWidth: 400,
		maxHeight: 500,
		modal: true
	});
}

//CSVファイル出力
function csvDialog(){
	var csvDialog = $("#csv-dialog");
	if (csvDialog.length == 0)
		alert("<%=lang.__("In this page, you can not output CSV.")%>");
	csvDialog.dialog({
		title: "<%=lang.__("CSV output")%>",
		minWidth: 450,
		maxHeight: 500,
		modal: true
	});
	SaigaiTask.Page.csvDialog = csvDialog;
}
//PDFファイル出力
function pdfDialog(){
	var pdfDialog = $("#pdf-dialog");
	if (pdfDialog.length == 0)
		alert("<%=lang.__("In this page, you can not output PDF.")%>");
	pdfDialog.dialog({
		title: "<%=lang.__("PDF output")%>",
		minWidth: 450,
		maxHeight: 500,
		modal: true
	});
	SaigaiTask.Page.pdfDialog = pdfDialog;
}
//保存
function saveData(){
	var saveDialog = $("#save-dialog");
	if (saveDialog.length == 0)
		alert("<%=lang.__("In this page, you can not save.")%>");
	saveDialog.dialog({
		title: "<%=lang.__("Save")%>",
		minWidth: 450,
		maxHeight: 500,
		modal: true
	});
	SaigaiTask.Page.saveDialog = saveDialog;
}
//履歴表示
function showHistory(){
	var historyDialog = $("#history-dialog");
	if (historyDialog.length == 0)
		alert("<%=lang.__("In this page, you can not indicate history.")%>");
	historyDialog.dialog({
		title: "<%=lang.__("History display")%>",
		minWidth: 450,
		maxHeight: 500,
		modal: true
	});
	SaigaiTask.Page.historyDialog = historyDialog;
}

function openManualWindow(url) {
	if(url!=null) {
		var params = [];
		var dx = screen.width;
		var dy = screen.height;
		var width = Math.min(dx/2, 500); // 画面サイズの半分の幅（MAX500px）
		var height = dy; // 高さ100%
		// サイズ
		params.push("width="+width);
		params.push("height="+height);
		// 表示位置 右上寄せ
		// Chrome は left,topの位置指定ができないため、左上になる.
		params.push("left="+(dx-width));
		params.push("screenX="+(dx-width));
		params.push("top="+(dy-height));
		params.push("screenY="+(dy-height));
		// その他
		params.push("menubar=no");
		params.push("toolbar=no");
		params.push("scrollbars=yes");
		console.log(params.join(","));
		window.open(url, "manual", params.join(","));
	}
	return false;
}

function loadEarthquakeLayers(){
	var extLayers = map.map.getLayersByName("EarthquakeLayer");
	if(extLayers.length>0){
		for(var i = 0; i<extLayers.length; i++){
			var extLayer = extLayers[i];
			map.map.removeLayer(extLayer);
		}
	}
	$('#content').mask("Loading...");
	var lon = $('#txtLon').val();
	var lat = $('#txtLat').val();
	var depth = $('#txtDepth').val();
	var magnitude = $('#ddMagnitude').val();
	if(lon=="" || lat ==""){
		alert("Please click on the map to select a point");
		unmaskContent();
		return false;
		
	}
	if(depth==""){
		depth=0;
	}
	$.ajax({
		
        type: "GET",
        async: false,
        url: SaigaiTask.contextPath+"/earthquakelayers/getearthquakelayerdata",
        data: "latitude="+lat +"&longitude="+lon+"&depth="+depth+"&magnitude="+magnitude,
        dataType : "JSON",
        success: function(d){
        	var layers = d;
        	wmsLayers = new OpenLayers.Layer.WMS( "EarthquakeLayer","${pageDto.earthQuakeGeoServer}", 
        			{layers: layers.join(), transparent: true}, {isBaseLayer:false});
        	map.addLayer(wmsLayers);
        	unmaskContent();
        }
    });
	// "Loading..."
}

//情報発信
function sendData() {
	var ref = "${f:url('/sendInformation/')}${pageDto.menuInfo.id}/?TB_iframe&width=800&height=450";
	tb_show('', ref, null);
}

/**
 * 被災者推計ボタン
 * 最大化機能をベースに別ウインドウで表示する
 */
function decisionSupport(){
	// 被災者推計ページでの演算実行処理
	if(SaigaiTask.PageURL.initParams.decisionsupport){
		var decisionURL = "${f:url('/page/decisionsupport')}";
		// 地震リストを取得する
		SaigaiTask.Page.decisionSupport.getEarthQuakeLayer(decisionURL, '${cookie.JSESSIONID.value}', '${pageDto.menuInfo.id}')
		.done(function(divs){
			var me = divs;
			divs.dialog({
				title: "<%=lang.__("Sum up victims.")%>",
				modal: true,
				width : 460,
				height : 330,
				buttons:
					[{
						 text:"<%=lang.__("Execution")%>",
						 click:function(){
							$(".ui-dialog-buttonpane button:contains('<%=lang.__("Execution")%>')").button("disable");
							var layerid = $("input[name=earthQuakeLayers]:checked").val();
							if(layerid == null){
								if(!window.confirm('<%=lang.__("Not to execute calculation of the number of persons difficult to go back home due to no configurations of quake layer.")%>')){
									$(".ui-dialog-buttonpane button:contains('<%=lang.__("Execution")%>')").button("enable");
									return;
								}else{
									layerid = "";
								}
							}
							var decisionText = $("<p>").attr({"id":"decisionText"}).html("<%=lang.__("Analyzing in process. Please wait.")%>").appendTo(divs);
							// ロック処理
							$.when(SaigaiTask.Page.decisionSupport.doLock(decisionURL, true))
							.then(function(){
								return SaigaiTask.Page.decisionSupport.calculation(decisionURL, '${cookie.JSESSIONID.value}', layerid)
							}).done(function(data){
								if(data != null){
									// エラー表示
									if('error' in data){
										var errArr = data.error;
										if(errArr.length == 0){
											$("#decisionText").html("<%=lang.__("Calculation completed!")%>");
											me.dialog("close");
										}
										for(var i = 0; i < errArr.length; i++){
											$("<p>").css({"color":"#FF0000"}).html(errArr[i].msg).appendTo(me);
										}
										decisionText.html("<%=lang.__("Calculation completed!<!--2-->")%>");
										$("#dialog_message").html("");
									}
								}
								if(SaigaiTask.Page.decisionSupport.mode == "map"){
									// 地図モード
									SaigaiTask.Page.decisionSupport.displayDecisionLayer("${f:url('/page/decisionsupport')}", '${cookie.JSESSIONID.value}');
								}else{
									// リストモード
									SaigaiTask.PageURL.reloadContent();
								}
								SaigaiTask.Page.decisionSupport.doLock(decisionURL, false);
							}).fail(function(){
								$("#decisionText").html("<%=lang.__("Failed to calculate!")%>");
							}).always(function(){
								//me.dialog("close"); reloadContent
							});
						 }
					 },
					 {
						 text:"<%=lang.__("Cancel<!--2-->")%>",
						 click:function(){
							 console.log("<%=lang.__("Canceled.")%>");
							 $(this).dialog("close");
						 }
					}]
			});
			divs.dialog("open");
		}).fail(function(){
		});
	}
	// 被災者推計ボタン処理
	else{
		window.open(SaigaiTask.PageURL.getUrl({fullscreen: true, decisionsupport: true}), 'SaigaiTaskFullscreen');
	}
}

/**
 * 最終更新日の表示を変更します.
 * @param {String} text 表示文字列
 */
SaigaiTask.setUpdateTime = function(text) {
	if (text != null && text != "") {
		$("#updateTime").show();
	}
	else {
		$("#updateTime").hide();
	}
	$("#updateTime span").text(text);
};

function addFigure(str) {
	var num = new String(str).replace(/,/g, "");
	while(num != (num = num.replace(/^(-?\d+)(\d{3})/, "$1,$2")));
	return num;
}

function maskBeforeUnload(event) {
	// ダウンロード中の場合はマスクしない
	if(SaigaiTask.PageURL.isDownload)
		return;
	
	$('#content').mask("Loading...");
	$('body').on("click", unmaskContent);
}

function unmaskContent() {
	if($('div').hasClass('loadmask')) {
		$('#content').unmask();
	}
}

$(function() {
	$(window).on("beforeunload", maskBeforeUnload);
});

</script>
<script type="text/javascript">console.timeEnd("loadHeadTime");console.time("loadBodyTime");</script>
</head>
<body style="overflow: hidden;">
	<c:if test="${pageDto.contentOnly == false }">
	<div id="header" class="ui-layout-north" style="padding:0px; border: 1px solid #BBB;overflow:visible; ${pageDto.fullscreen ? 'display:none;':''}">
		<%--
		 --%>
		<jsp:include page="/WEB-INF/view/common/header.jsp" />
		<jsp:include page="/WEB-INF/view/common/tab.jsp" />
	</div>
	<div id="menu" class="ui-layout-west" style="visibility: hidden;">
		<jsp:include page="/WEB-INF/view/common/menu.jsp" />
	</div>
	</c:if>

	<div id="content" class="ui-layout-center" style="visibility: hidden; overflow: hidden;">
		<script type="text/javascript">
		SaigaiTask.Layout.init({
			initAll: false
		});
		SaigaiTask.Layout.initBodyLayout();
		</script>
		<c:if test="${pageDto.contentOnly == false }">
		<div id="content_head" class="ui-layout-north" style="visibility: hidden;">
			<c:if test="${pageDto.menuInfo.helphref!=null && pageDto.menuInfo.helphref!=''}">
			<div class="toolbox_box">
				<a href="#" onclick="javascript:openManualWindow('${f:h(pageDto.menuInfo.helphref)}')" class="btn_icon blue" style="">&#x2753;</a>
				<br style="clear:both;">
				<span><%=lang.__("Tool_Help")%></span>
			</div>
			</c:if>
			<c:if test="${pageDto.menuInfo.helphref==null || pageDto.menuInfo.helphref==''}">
			<div class="toolbox_box">
				<a href="#" onclick="javascript:openManualWindow('${f:h(pageDto.menuInfo.helphref)}')" class="btn_icon gray" style="">&#x2753;</a>
				<br style="clear:both;">
				<span><%=lang.__("Tool_Help")%></span>
			</div>
			</c:if>
			<c:if test="${(pagetype=='list'||pagetype=='map'||pagetype=='externallist'||pagetype=='postingphoto') && pageDto.othermenu==false}">
			<div class="toolbox_box" id="url-button">
				<a href="#" class="btn_icon blue">&#x1F517;</a>
				<br style="clear:both;">
				<span><%=lang.__("Tool_URL")%></span>
			</div>
			</c:if>
			<c:if test="${(pagetype=='map') && pageDto.othermenu==false}">
			<div class="toolbox_box" id="measure-button">
				<a href="#" class="btn_icon blue">&#x1F4D0;</a>
				<br style="clear:both;">
				<span><%=lang.__("Tool_Measure")%></span>
			</div>
			</c:if>
			<c:if test="${(pagetype=='list') && pageDto.othermenu==false}">
			<div class="toolbox_box">
				<a href="#" class="btn_icon lgray">&#x1F4D0;</a>
				<br style="clear:both;">
				<span><%=lang.__("Tool_Measure")%></span>
			</div>
			</c:if>
			<c:if test="${(pagetype=='list'||pagetype=='map'||pagetype=='externallist')}">
			<div class="toolbox_box" id="timeslider-button" onclick="SaigaiTask.Page.timeslider.show();" style="cursor: pointer;">
				<a href="#" class="btn_icon lgray">🕒</a>
				<br style="clear:both;">
				<span><%=lang.__("History")%></span>
			</div>
			</c:if>
			<div style="float:left;"><!-- float left div -->
			<span style="color:black;display:none;" id="data-num">[${count} <%=lang.__("Items")%>]</span>
			<span id="externallistselect-container" style="color:black;"></span>
			</div>
			<ul class="buttons">
			<c:if test="${pageDto.menuInfo.menutypeid==Menutype.OBSERVE}">
					<li><a href="#" class="btn blue" id="tele-button"><%=lang.__("On the hour every hour")%></a></li>
			</c:if>
			<c:if test="${pageDto.fullscreen}">
				<li><a href="#" class="btn blue" onclick="window.close();" id="fullscreen-close-button"><%=lang.__("Close")%></a></li>
			</c:if>
			<c:if test="${pageDto.enableFullscreen}">
				<c:if test="${pageDto.fullscreen==false}">
					<li><a href="#" class="btn blue" onclick="window.open(SaigaiTask.PageURL.getUrl({fullscreen: true}), 'SaigaiTaskFullscreen');"><%=lang.__("Maximize")%></a></li>
				</c:if>
			</c:if>
			<c:if test="${pageDto.pagetoggleButton!=null}">
				<li><a href="#" class="btn blue" onclick="SaigaiTask.PageURL.beforeMove('${pageDto.pagetoggleButton.first}');SaigaiTask.PageURL.override({pagetype: '${f:h(pageDto.pagetoggleButton.first)}'}).move();">${f:h(lang.__(pageDto.pagetoggleButton.second))}</a></li>
			</c:if>

			<c:if test="${pageDto.isDecisionSupport()}">
				<li>
				<div id="updateTime" style="float:left; padding: 3px;${pageDto.updateTime==null?' display: none;':''}">
					<%=lang.__("Last updated")%><br />
					<span style="border: solid 1px gray; padding: 1px;">${f:h(pageDto.updateTime)}</span> &nbsp;
				</div>
				<div style="float:left; padding: 3px;${decisionUpdateTime==null?' display: none;':''}">
					<%=lang.__("Refugee estimation update time")%><br />
					<span style="border: solid 1px gray; padding: 1px;">${f:h(decisionUpdateTime)}</span> &nbsp;
				</div>
				</li>
			</c:if>
			<c:if test="${!pageDto.isDecisionSupport()}">
				<li id="updateTime" style="padding: 3px;${pageDto.updateTime==null?' display: none;':''}">
					<%=lang.__("Last updated")%><span style="border: solid 1px gray; padding: 1px;">${f:h(pageDto.updateTime)}</span> &nbsp;
				</li>
			</c:if>
			</ul>
			<c:if test="${pagetype=='list'||pagetype=='map'||pagetype=='history'||pagetype=='postingphoto'}">
			<div id="filter-gray-ck-div" style="display:none;/*初期非表示*/">
				<span><%=lang.__("Filter")%>: <span id="filter-name"></span><span id="filter-num"></span></span>&nbsp;<input type="button" value="<%=lang.__("Change conditions")%>" id="filter-switch-btn" onclick="SaigaiTask.Page.filter.showDialog();"/><span id="search-text" style="font-weight:bold;background:none;background-color:yellow;color:#222;">${f:h(searchText) }</span><br/>
				<input type="checkbox" id="filter-gray-ck" checked><label for="filter-gray-ck"><%=lang.__("Filter results in gray color")%></label>
			</div>
			<%--
			途中まで作ったけど、保留となった。
			<!-- 管理画面の検索条件設定ダイアログを表示する -->
<!--			<a href="#" class="btn blue" id="filter-button">検索</a> -->
			 --%>
			</c:if>
			<div id="externallistselect-container"></div>
			<c:if test="${pageDto.menuInfo.name=='VI. Rapid Loss Assessment'}">
			<div>
				<input type="text" id="txtLat" name="txtLat" placeholder="Lat"style="margin-left: 40px"/>
				<input type="text" id="txtLon" name="txtLon" placeholder="Lon"/>
				<select  id="ddMagnitude" name="ddMagnitude" placeholder="Magnitude">
					<option>4.5</option>
					<option>5.0</option>
					<option>5.5</option>
					<option>6.0</option>
					<option>6.5</option>
					<option>7.0</option>
					<option>7.5</option>
					<option>8.0</option>
					<option>8.5</option>
					<%--<option>${pageDto.menuprocessInfo.name}</option> --%>
				</select>
				<input type="text" id="txtDepth" name="depth" placeholder="Depth"/>
				<input type="submit" value="Load Data" class="ui-button-text" onclick="$('#content').mask('Loading...');loadEarthquakeLayers();"/>
			</div>
			</c:if>
		</div>
		<script type="text/javascript">
		$("#content_head").css("visibility", "visible");
		btn.init(); // コンテンツヘッダのボタンを先に初期化する
		</script>
		</c:if>
		<div id="content_main" class="ui-layout-center" style="visibility: hidden;">
			<tiles:insertAttribute name="content" />
		</div>
		<c:if test="${pageDto.contentOnly == false }">
			<c:if test="${0<fn:length(bbuttonItems)}"><%-- ボタンがなければフッター非表示 --%>
			<div id="content_footer" class="ui-layout-south" style="visibility: hidden; padding:5px;"><jsp:include page="/WEB-INF/view/common/footer.jsp" /></div>
			</c:if>
		</c:if>
		<script type="text/javascript">
		SaigaiTask.Layout.body.on("oninitcontentlayout", function() {
			$("#content_footer").css("visibility", "visible");
		});
		SaigaiTask.Layout.initContentLayout();
		SaigaiTask.Layout.completeLayout();
		</script>
	</div>
	<script type="text/javascript">console.timeEnd("loadBodyTime");</script>
</body>
</html>
