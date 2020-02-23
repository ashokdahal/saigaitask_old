<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../common/lang_resource.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />

<title>TableData Reset Window</title>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/tablesorter-2.28.5/css/theme.blue.css')}" />
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.widgets.js')}"></script>

<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.datetimepicker-2.3.7.css')}" />
<script type="text/javascript" src="${f:url('/js/jquery.datetimepicker.min-2.3.7.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.datetimepicker.full.min-2.3.7.js')}"></script>


<style type="text/css">

#resetTable1 thead tr th, #resetTable2 thead tr th,
#resetTable1 tr td, #resetTable2 tr td{
	font-size: 14px;
	text-align: center;
}
.resetTableCells{
	display: table;
	table-layout: fixed;
	width: 100%;
	border-collapse: separate;
	border-spacing: 6px 6px;
	border-color: #FFFFFF;
}
.tablesorter-header-inner{
	text-align: center;
	font-size: 14px;
}
.reset_btn {
	width:60px;
	text-decoration: none;
	cursor: pointer;
	margin:0 auto;
	border-radius : 6px;
	border: 1px solid rgba(0, 0, 0, 0.2);
	background-image:
		none,
		linear-gradient(
			rgba(255, 255, 255, 0.6)   0%,
			rgba(255, 255, 255, 0.4)  50%,
			rgba(255, 255, 255, 0.2)  50%,
			rgba(255, 255, 255,   0) 100%
		);
	}
}
.reset_active { background-color:#26b; }
.reset_gray { background-color: #808080; }

.label_orange{
	width:40%;
	height:24px;
	cursor: pointer;
	border-radius : 6px;
	border: 1px solid rgba(0, 0, 0, 0.2);
	background-color:rgba(255,153,0,0.8);
	background-image:
		none,
		linear-gradient(
			rgba(255, 255, 255, 0.6)   0%,
			rgba(255, 255, 255, 0.4)  50%,
			rgba(255, 255, 255, 0.2)  50%,
			rgba(255, 255, 255,   0) 100%
		);
	color:#000000;
}
.label_white{
	width:40%;
	height:24px;
	visibility: visible;
	cursor: pointer;
	border-radius : 6px;
	border: 1px solid rgba(0, 0, 0, 0.2);
	background-color:rgba(176,176,176,0.8);
	background-image:
		none,
		linear-gradient(
			rgba(255, 255, 255, 0.6)   0%,
			rgba(255, 255, 255, 0.4)  50%,
			rgba(255, 255, 255, 0.2)  50%,
			rgba(255, 255, 255,   0) 100%
		);
	color:#000000;
}
.label_hidden{
	width:40%;
	height:24px;
	visibility: hidden;
	cursor: cursor;
}
</style>

<script type="text/javascript">

// DateTimePickerの言語設定
var lang = "${lang.getLangCode()}";

// ページ読み込み時に、リセット情報を格納するObject変数
var resetLayers = null;
var restoreTime = null;
// 今開いてるlayerのattr情報を格納する変数
var nowResetLayer = null;

window.SaigaiTask = {
		contextPath: "<%=request.getContextPath()%>",
		csrfToken: "<%= session.getId() %>"
	};

$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
	SaigaiTask.ajaxcsrfToken = token;
});


$(document).ready(function() {
	// テーブルソーターの初期化
	$("#resetTable1").tablesorter({ widgets: ['zebra','stickyHeaders'] });
	$("#resetTable2").tablesorter({ widgets: ['zebra','stickyHeaders'], cssInfoBlock:"ctl_row" });

	// イベント関連
	$(document).on('click', '.resetAttr', function(){

		var layerid = $(this).attr("layerid");
		var attrid = $(this).attr("attrid");
		var reset = false;
		if( $(this).val() == "ON"){
			reset = false;
		}else{
			reset = true;
		}

		if('attrs' in nowResetLayer){
			var attrs = nowResetLayer.attrs;
			for(var i = 0; i < attrs.length; i++){
				if(attrs[i].attrid == attrid){
					attrs[i].reset = reset;
				}
			}
		}
		// ボタンスタイルを切り替え
		if(reset){
			$("#" + layerid + "_" + attrid + "_on").removeClass("label_hidden").addClass("label_white");
			$("#" + layerid + "_" + attrid + "_off").removeClass("label_white").addClass("label_hidden");
		}else{
			$("#" + layerid + "_" + attrid + "_on").removeClass("label_white").addClass("label_hidden");
			$("#" + layerid + "_" + attrid + "_off").removeClass("label_hidden").addClass("label_white");
		}
	});
	$(document).on('click', '.resetlayer', function(){
		var layerid = $(this).attr("id");
		var tid = $(this).attr("tid");
		// 初期に取得したレイヤ情報から解析
		if(resetLayers.length > 0){
			for(var i = 0; i < resetLayers.length; i++){
				if(resetLayers[i].layerid == layerid){
					setAttrList(resetLayers[i]);
					nowResetLayer = resetLayers[i];
					break;
				}
			}
		}
	});
	// 属性の全選択
	$(document).on('click', '#attrALLON', function(){
		$.each($('.resetAttr'), function(){
			if($(this).val() == "ON" && $(this).hasClass("label_hidden")){
				var layerid = $(this).attr("layerid");
				var attrid = $(this).attr("attrid");
				// ボタンの入れ替え
				$(this).removeClass("label_hidden").addClass("label_white");
				$("#" + layerid + "_" + attrid + "_off").removeClass("label_white").addClass("label_hidden");
			}
		});
		// 配列も全てtrueにしておく
		var attrs = nowResetLayer.attrs;
		for(var i = 0; i < attrs.length; i++){
			attrs[i].reset = true;
		}
	});
	// 属性の全解除
	$(document).on('click', '#attrALLOFF', function(){
		$.each($('.resetAttr'), function(){
			// OFFのボタンで、今未選択のボタンを対象に処理
			if($(this).val() == "OFF" && $(this).hasClass("label_hidden")){
				var layerid = $(this).attr("layerid");
				var attrid = $(this).attr("attrid");
				// ボタンの入れ替え
				$(this).removeClass("label_hidden").addClass("label_white");
				$("#" + layerid + "_" + attrid + "_on").removeClass("label_white").addClass("label_hidden");
			}
		});
		// 配列も全てfalseにしておく
		var attrs = nowResetLayer.attrs;
		for(var i = 0; i < attrs.length; i++){
			attrs[i].reset = false;
		}
	});
	// リセット時間の表示
	$(document).on('click', '#datePicerBtn', function(){
		dispDatePicer();
	});


	// 現在のリセット設定を初期で読み出しておく
	getAttrData();

	// 言語設定
	$.datetimepicker.setLocale(lang);
	$("#restoreTimes").datetimepicker({format:'Y/m/d H:i:s'});

});

/**
 * 親フレームからの呼び出し用
 */
function dispDatePicer(){
	$("#restoreTimes").datetimepicker("show");
}

/**
 * 現在のリセット情報を取得し、グローバル変数に格納する
 */
function getAttrData(){

	$.ajax({
		url : "${f:url('/track/')}outputResetLayers",
		type: "POST",
		//headers: {"X-CSRF-Token":"${cookie.JSESSIONID.value}"},
		dataType : "json",
		data : "",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8"
	}).success(function(data, statusText, jqXHR){
		if(data != null){
			if('resetlayers' in data){
				resetLayers = data.resetlayers;
				restoreTime = data.restoretime;
				$("#restoreTimes").val(restoreTime);
				// 一番上のレイヤの属性情報がデフォルトで表示できるようにする
				if(resetLayers.length > 0){
					setAttrList(resetLayers[0]);
					nowResetLayer = resetLayers[0];
				}
			}
		}
	}).fail(function(){
		console.log("error--!");
	});
}

/**
 * グローバル変数から、選択されたレイヤの属性一覧テーブルを作成する
 */
function setAttrList(layerData){
	// テーブルクリア
	$("#resetAttrList").empty();
	// リセット属性一覧の上部にレイヤ名称出す
	$("#resetLayerName").html(layerData.layername);

	if('attrs' in layerData){
		var attrs = layerData.attrs;

		for(var i = 0; i < attrs.length; i++){
			var trs = $("<tr>").appendTo("#resetAttrList");
			$("<td>").css({"padding-top":"8px"}).html(attrs[i].attrname).appendTo(trs);

			var td2 = $("<td>").css({"text-align":"center"}).appendTo(trs);
			var buttonON  = $("<input>").attr({"type":"button","layerid":layerData.layerid,"attrid":attrs[i].attrid}).addClass("resetAttr").css({"margin-right":"10px"}).val("ON");
			var buttonOFF = $("<input>").attr({"type":"button","layerid":layerData.layerid,"attrid":attrs[i].attrid}).addClass("resetAttr").val("OFF");

			var ids = layerData.layerid + "_" + attrs[i].attrid;
			if(attrs[i].reset == true){
				buttonON.attr({"id": ids + "_on"}).addClass("label_white").appendTo(td2);
				buttonOFF.attr({"id":ids + "_off"}).addClass("label_hidden").appendTo(td2);
			}else{
				buttonON.attr({"id": ids + "_on"}).addClass("label_hidden").appendTo(td2);
				buttonOFF.attr({"id":ids + "_off"}).addClass("label_white").appendTo(td2);
			}
		}
	}
	$("#resetAttrList").trigger("update");
}

/**
 * リセット処理実行関数
 */
function updateResetLayer(){
	// 体制の解除チェック
	var isStationOFF = "${f:h(isStationOFF) == 'false'}"=="false";
	var warnStation = "<%=lang.__("[Alert] Not yet released scheme!")%>";
	if(isStationOFF==false) {
		alert(warnStation);
		return false;
	}

	if(!confirm("<%=lang.__("Complete disaster treatment after reset info setting completion?")%>")){
		return false;
	}
	if(resetLayers == null) return false;

	restoreTime = $("#restoreTimes").val();
	var data = {"resetLayers": resetLayers, "restoretime" : restoreTime};
	// JSON文字列にする
	var ser = JSON.stringify(data);
	// formで投げる
	var forms = $("<form>").attr({"action":"${f:url('/track/')}resetExecute", "method":"POST", "target":"_self"}).appendTo($("body"));
	$("<input>").attr({"name":"resetParam"}).val(ser).appendTo(forms);
	var token = $("meta[name='_csrf']").attr("content");
	$("<input>").attr({"name":"_csrf"}).val(token).appendTo(forms);
	forms.submit();
	return true;
}

</script>

</head>
<body>

<!-- リセット対象レイヤダイアログ -->
<div id="resetDialog" style="font-size:14px; letter-spacing:1.2;">
	<div class="resetTableCells">
		<div style="display:table-cell;">
			<p style="line-height:150%;">
				<%=lang.__("Show data listed below, putting back to the status of pre-disaster.")%><br />
				<%=lang.__("Change every item setting on Item Setting.")%>
				<c:choose>
					<c:when test="${f:h(isStationOFF) == 'false'}">
						<br /><span style="color:#FF0000; font-weight:700;"><%=lang.__("[Alert] Not yet released scheme!")%></span>
					</c:when>
				</c:choose>
			</p>
		</div>
		<div style="display:table-cell;">
			<p style="margin:12px 0 6px 0;">
				<span style="margin:0 10px 0 0; color:#FF0000; font-weight:700;"><%=lang.__("Restoration point")%>：</span><input type="text" id="restoreTimes" value="">
				<input type="button" class="label_white" id="datePicerBtn" style="margin-left:10px; width:15%;" value="<%=lang.__("Change")%>">
			</p>
			<p style="color:#FF0000;"><%=lang.__("* It shows date and time before disaster starts as default.")%></p>
		</div>
	</div>
	<div class="resetTableCells">
		<div style="display:table-cell;">
			<p style="margin:0;"><%=lang.__("Reset target layer list")%></p>
		</div>
		<div style="display:table-cell;">
			<div class="resetTableCells">
				<p style="margin:0;"><%=lang.__("Reset target attribution list")%>
					＜<span id="resetLayerName"></span>＞
				</p>
			</div>
		</div>
	</div>
	<div class="resetTableCells">
		<div style="display:table-cell;">

			<table id="resetTable1" class="tablesorter-blue" style="width:90%;">
				<thead>
					<!--  <th class="th_layerid">レイヤID</th> -->
					<th><%=lang.__("Layer name<!--2-->")%></th>
					<th class="th_reset"><%=lang.__("Item Setting")%></th>
				</thead>
				<tbody id="resetLayerList"></tbody>

				<c:choose>
					<c:when test="${!empty resetTablemasterInfos}">
						<c:forEach var="resetTablemasterInfo" varStatus="s1" items="${resetTablemasterInfos}">
						<tr>
							<td style="padding-top:8px;">${f:h(resetTablemasterInfo.tablemasterInfo.name)}</td>
							<td style="text-align:center;">
								<input type="button" id="${f:h(resetTablemasterInfo.layerid)}" tid="${f:h(resetTablemasterInfo.tablemasterinfoid)}" class="label_white resetlayer" style="margin-right:10px;" value="<%=lang.__("Attribution setting")%>">
							</td>
						</tr>
						</c:forEach>
					</c:when>
				</c:choose>
			</table>
		</div>
		<div style="display:table-cell;">
			<table id="resetTable2" class="tablesorter-blue" style="width:90%;">
				<thead>
					<th><%=lang.__("Attribution name")%></th>
					<th class="th_reset" data-sorter="false"><%=lang.__("Reset")%></th>
				</thead>
				<tbody class="ctl_row">
					<tr>
						<td style="background-color:#2a6aab;"></td>
						<td style="background-color:#2a6aab;">
							<input type="button" class="label_white" id="attrALLON" style="margin-right:10px;" value="<%=lang.__("All selected")%>">
							<input type="button" class="label_white" id="attrALLOFF" value="<%=lang.__("All cleared")%>">
						</td>
					</tr>
				</tbody>
				<tbody id="resetAttrList"></tbody>
			</table>
		</div>
	</div>
</div>
</body>
</html>