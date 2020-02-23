<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<%-- <jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include> --%>
<%@include file="../common/adminjs-header.jsp" %>
<meta charset="utf-8">
<meta name="description" content="<%=lang.__("It is admin window of NIED disaster information sharing system.")%>">
<meta name=" keywords" content="<%=lang.__("NIED disaster information sharing system, admin window")%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />
<title><%=lang.__("Admin window : NIED disaster information sharing system")%></title>

<link type="text/css" media="screen" href="${f:url('/js/OpenLayers/theme/default/style.css')}" rel="stylesheet" />
<script type="text/javascript" src="${f:url('/js/OpenLayers/lib/OpenLayers.js')}"></script>
<script type="text/javascript" src="${f:url('/admin-js/js/map2.js')}"></script>
<script type="text/javascript" language="javascript">

function confirm_insert() {

	// 組織名称をセット
	$("#organizationTitle").val($("#organization option:selected").html());

	if (document.getElementById("registered").value == "true") {
		return confirm("<%=lang.__("Selected layer has been registered already. Are you sure to update?")%>");
	}
	else {
		return confirm("<%=lang.__("Are you sure to register?")%>");
	}
}

$(function() {
  $("#lyid").change(function() {
	  if($(this).val() != ""){
		var mapIdLayerId = $(this).val().split(':');
		var _mapId = mapIdLayerId[0];
		var _layerId = mapIdLayerId[1];
		var _id = mapIdLayerId[2];
		$.ajax({
			async:    false, // 同期通信
			url:      SaigaiTask.contextPath+'/admin/uploadckanData/changevalue',
			type:     'GET',
			dataType: 'json',
			cache:    false, // ブラウザにキャッシュさせません。
//			data:     {layerId :$(this).val()},
			data:     {mapId :_mapId, layerId : _layerId, id : _id},
			error:    function(){
				alert("<%=lang.__("Failed to read json file.")%>");
			},
			success:  function(data){ // サーバからJSON形式のデータを受け取る
				// JSON 形式をHTMLへ変換する
				document.getElementById("registered").value = data.registered;
				document.getElementById("spanExTmp").innerHTML = (data.exTemp[0]==null&&data.exTemp[1]==null) ? "<%=lang.__("None")%>" : data.exTemp;

				////////////////////////////////////////////////////////////
				//メタデータに関する情報
				////////////////////////////////////////////////////////////
				// メタデータの公開/非公開
				$("#isOpen").val(data.isOpen);
				// 情報種別ID
				$("#infoType").val(data.infoType);
				// 依存関係のある情報種別ID
				$("#depInfoType").val(data.depInfoType);
				// システム名
				$("#systemName").val(data.systemName);
				// システムURL
				$("#systemUrl").val(data.systemUrl);
				// 言語
				$("#language").val(data.language);
				// 地図情報
				$("#minx").val(data.minx);
				$("#miny").val(data.miny);
				$("#maxx").val(data.maxx);
				$("#maxy").val(data.maxy);
				$("#spatial").val(data.spatial);

				////////////////////////////////////////////////////////////
				// 地図データに関する情報
				////////////////////////////////////////////////////////////
				// 地図データのタイトル
				$("#title").val(data.title);
				// 地図データの内容の要約
				$("#abstr").val(data.abstr);
				// 検索タグ
				$("#tags").val(data.tags);
				// ライセンス情報
				if (data.licenseInfo)
					$("#licenseInfo").val(data.licenseInfo);
				else
					$("#licenseInfo").prop("selectedIndex", 0);

				////////////////////////////////////////////////////////////
				// メタデータ/地図データに関する問い合わせ先情報
				////////////////////////////////////////////////////////////
				// 組織
				if (data.organization)
					$("#organization").val(data.organization);
				else
					$("#organization").prop("selectedIndex", 0);
				// 作成者
				$("#author").val(data.author);
				// 作成者のメールアドレス
				$("#authorEmail").val(data.authorEmail);
				// 作成日
				$("#metadataCreated").val(data.metadataCreated);
				$("#createdFormatted").val(data.createdFormatted);
				// メンテナー
				$("#maintainer").val(data.maintainer);
				// メンテナーのメールアドレス
				$("#maintainerEmail").val(data.maintainerEmail);
				// 修正日
				$("#metadataModified").val(data.metadataModified);
				$("#modifiedFormatted").val(data.modifiedFormatted);
				// バージョン番号
				$("#version").val(data.version);

				////////////////////////////////////////////////////////////
				// 地図データのアクセスに関する情報
				////////////////////////////////////////////////////////////
				// WMS URL
		      	$("#wms").val(data.wms);
				// WFS URL
		      	$("#wfs").val(data.wfs);

/*
				var information = "";
				if(data.registered) {
					information +=MessageFormat.format("<%=lang.__("meta data ID {0} registered.")%>", data["fileIdentifier"]);
					information +="<br/><%=lang.__("Update date and time")%>："+data["updateTime"];
				}
				$("#registration_information").html(information);
*/
				// Map ID
		      	$("#mapId").val(data.mapId);
				// Layer ID
		      	$("#layerId").val(data.layerId);

				// 登録ボタン
				$("#insert").val(data.registered ? "<%=lang.__("Update<!--2-->")%>" : "<%=lang.__("Registration")%>");
			}
		});
	  }else{
			$("#title").text("");
			$("#minx").val("");
			$("#miny").val("");
			$("#maxx").val("");
			$("#maxy").val("");
			$("#spatial").val("");
			$("#spanExTmp").text("");
	  }
	});

	// 地図ボタン押下イベント
	$(document).on(
		'click',
		'.ckan-callrangemap',
		function(){
			// 関数呼び出し
			toggleMapArea();
		}
	);

	$(document).on(
		'click',
		'#btnSetMapArea',
		function(){
			// 関数呼び出し
			setMapArea();
		}
	);

<c:if test="${!uploadckanDataForm.authorized}">
	  $("#insert").prop("disabled", true);
</c:if>

});

var uploadckanRangemap = null;
var uploadckanRangemapWmsLayer = null;
var uploadckanRangeDialogPos = null;

function setMapArea(base) {
	//alert("setMapArea");
	var vector = getVectorLayer(uploadckanRangemap);
	if (!vector || vector.length == 0)
		return;

	var bounds = vector[0].features[0].geometry.bounds;
	bounds = bounds.transform(new OpenLayers.Projection("EPSG:900913"), new OpenLayers.Projection("EPSG:4326"));

	// 選択された範囲を設定する
	var x1 = bounds.left.toFixed(6);
	var x2 = bounds.right.toFixed(6);
	var y1 = bounds.top.toFixed(6);
	var y2 = bounds.bottom.toFixed(6);

	$("#minx").val(x1);
	$("#maxx").val(x2);
	$("#miny").val(y1);
	$("#maxy").val(y2);
	$("#spatial").val(
		'{"type":"Polygon","coordinates":[[['
			+ x1 + ',' + y1 + '], ['
			+ x2 + ',' + y1 + '], ['
			+ x2 + ',' + y2 + '], ['
			+ x1 + ',' + y2 + '], ['
			+ x1 + ',' + y1 + ']]]}');

	$.jgrid.hideModal('#rangeMapModalDialog',{jqm:true});
}

function uploadckanInitMyMap() {
	uploadckanRangemap = initMyMap("mapPanel_header");

	// attribution の上で矩形描画を終了したときの不具合に対応
	$('#rangeMapModalDialog .olControlAttribution').on("mouseup", function(evt) {
		var event = document.createEvent("MouseEvents");
		event.initMouseEvent('mouseup', true, true, window,
							 0, evt.screenX, evt.screenY, evt.clientX, evt.clientY,
							 false, false, false, false,
							 0, null);
		uploadckanRangemap.viewPortDiv.dispatchEvent(event);
	});
}

function toggleMapArea() {
	var mapctrl = $('#rangeMapModalDialog');
	if(mapctrl.css('display') == 'block'){
		// 表示中は閉じて終わり
		$.jgrid.hideModal('#rangeMapModalDialog',{jqm:true});
		return;
	}

	popupRangMapDialog();

	if (uploadckanRangemap == null || document.getElementById("mapPanel_header").children.length == 0) {
		uploadckanInitMyMap();
	}

	// レイヤが追加されている場合は削除する
	if (uploadckanRangemapWmsLayer) {
		uploadckanRangemap.removeLayer(uploadckanRangemapWmsLayer);
		uploadckanRangemapWmsLayer = null;
	}

	removePolygon(uploadckanRangemap);
	document.getElementById("rdoScroll").checked = true;
	getSearchBoxControl(uploadckanRangemap)[0].deactivate();

	// 設定された範囲と適切な縮尺で表示
	var l = $("#minx").val();
	var r = $("#maxx").val();
	var b = $("#miny").val();
	var t = $("#maxy").val();
	var bounds = null;

	// 範囲が設定されている場合
	if (l && r && b && t) {

		// サイズがゼロの場合
		if (r == l && b == t)
			bounds = new OpenLayers.Bounds(l - 0.5, b - 0.5, r + 0.5, t + 0.5);
		else
			bounds = new OpenLayers.Bounds(l, b, r, t);

		// 地物を表示するレイヤを追加する
		if ($("#lyid").val()) {
			var mapIdLayerId = $("#lyid").val().split(':');
			//var mapId = mapIdLayerId[0];
			//var layerId = mapIdLayerId[1];
			var mapId = $('#mapId').val();
			var layerId = $('#layerId').val();
			var cid = mapIdLayerId[3];

			// WMS レイヤを作成する
			uploadckanRangemapWmsLayer = new OpenLayers.Layer.WMS(
				"wms layer",
				SaigaiTask.contextPath + "/page/map/wmsAuth/?cid=" + cid + "&mid=" + mapId + "&", {
					layers: layerId,
					transparent: true
				}, {
					opacity: 1.0,
					singleTile: true,
				});
			uploadckanRangemap.addLayer(uploadckanRangemapWmsLayer);
			uploadckanRangemap.raiseLayer(uploadckanRangemapWmsLayer, -1);
		}
	}
	else {
		bounds = new OpenLayers.Bounds(-180, -90, 180, 90);
	}

	// 座標変換
	var bounds = bounds.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
	bounds = bounds.scale(1.25);
	uploadckanRangemap.zoomToExtent(bounds);

	//設定範囲を表示
	if (l && r && b && t) {
		addPolygonFromRect(l, b, r, t, uploadckanRangemap);
	}

}

function popupRangMapDialog() {

	if (!document.getElementById("rangeMapModalDialog")) {	//ダイヤログが2回作成されることを避ける。
		var dialogHtml = '';
		dialogHtml += "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" ;
		dialogHtml += "<tr>" ;

		dialogHtml += "<td style='border: 0 !important; background-color: #CBCFE9 !important;'>" ;
		dialogHtml += "<%=lang.__("Base map")%>:"
		dialogHtml += "<select id='baselayer' name='baselayer'>" ;
		dialogHtml += "<option value = '0' ><%=lang.__("Geographical Survey Institute Tile")%></option>"
		dialogHtml += "<option value = '1' >OpenStreetMap</option>"
		dialogHtml += "</select>"
		dialogHtml += "</td>" ;

		dialogHtml += "</tr>" ;
		dialogHtml += "</table>" ;
		dialogHtml += "<div id='mapctrl_header' style='display:none; position:absolute; width: 300px; height: 300px; background-color:white;z-index:1000;'>" ;
		dialogHtml += "<div id='mapctrlinner_header'>" ;
		dialogHtml += "<div id='mapimgsizer_header'>" ;
		dialogHtml += "<div id='mapPanel_header' style='width: 300px; height: 300px;'></div>" ;
		dialogHtml += "</div>" ;
		dialogHtml += "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" ;
		dialogHtml += "<tr>" ;

		dialogHtml += "<td style='border: 0 !important; background-color: #CBCFE9 !important;'>" ;
		dialogHtml += "<input type='radio' id='rdoScroll' name='mapmoderadio' value='none1' checked='checked' onchange='getSearchBoxControl(uploadckanRangemap)[0].deactivate()'><%=lang.__("Scroll")%>" ;
		dialogHtml += "<input type='radio' id='rdoRange' name='mapmoderadio' value='none2' onchange='getSearchBoxControl(uploadckanRangemap)[0].activate()'><%=lang.__("Range specification")%>" ;
		dialogHtml += "</td>" ;

		dialogHtml += "<td align='right' style='border: 0 !important; background-color: #CBCFE9 !important;'>" ;
		dialogHtml += "<input id='btnSetMapArea' type='button' value='<%=lang.__("Proceed")%>' >" ;
		dialogHtml += "<input type='button' value='<%=lang.__("Cancel")%>' onclick='$.jgrid.hideModal(\"#rangeMapModalDialog\",{jqm:true})'>" ;
		dialogHtml += "</td>" ;

		dialogHtml += "</tr>" ;
		dialogHtml += "</table>" ;
		dialogHtml += "</div>" ;
		dialogHtml += "</div>" ;

		var openDiv = $("[id^='ckanWrapper']");
		var position = openDiv.position();

		//ダイアログ作成
		$.jgrid.createModal(
			IDs = {
				themodal:'rangeMapModalDialog',
				modalhead:'rangeMapModalDialogHd',
				modalcontent:'rangeMapModalDialogCnt',
				scrollelm : ''
			},
			dialogHtml,
			{
				gbox:"#gbox_rangeMapModalDialog",
				jqModal:true,
				drag:true,
				resize:false,
				caption:'<%=lang.__("Range specification")%>',
				top: position.top,
				left: position.left + 400,
				width:304,
				height: 370,
				closeOnEscape:true,
				zIndex:1000
			},
			'',
			'',
			true
		);
	}

	// タッチパネルでダイアログを移動できるように、タッチイベントをマウスイベントに変換する
	var toMouseEvent = function(type, evt) {
		evt = evt.originalEvent;
		if (evt.touches && evt.touches[0])
			evt = evt.touches[0];
		var event = document.createEvent("MouseEvents");
		event.initMouseEvent(type, true, true, window,
							 0, evt.screenX, evt.screenY, evt.clientX, evt.clientY,
							 false, false, false, false,
							 0, null);
		evt.target.dispatchEvent(event);
	};
	$('#rangeMapModalDialogHd').on("touchstart", function(evt) {
		toMouseEvent('mousedown', evt);
	});
	$('#rangeMapModalDialogHd').on("touchmove", function(evt) {
		toMouseEvent('mousemove', evt);
		evt.preventDefault();
	});
	$('#rangeMapModalDialogHd').on("touchend", function(evt) {
		toMouseEvent('mouseup', evt);
	});

	// ダイアログが移動したときの処理
	$('#rangeMapModalDialog').on("mouseup pointerup touchend", function(event) {
		var position = $('#rangeMapModalDialog').position();
		if (uploadckanRangeDialogPos && (
			uploadckanRangeDialogPos.left != position.left || uploadckanRangeDialogPos.top != position.top)) {
			uploadckanRangeDialogPos = position;
			uploadckanRangemap.updateSize();
		}
	});

	// 地理院タイルとOpenStreetMapの切替
	$('#baselayer').change(function() {
//		uploadckanRangemap.setBaseLayer(uploadckanRangemap.layers[this.value]);
		uploadckanRangemap.setBaseLayer(uploadckanRangemap.getLayersBy('isBaseLayer', true)[this.value]);
	});

	// ダイアログの表示
	$.jgrid.viewModal("#rangeMapModalDialog",{modal:true});

	$('#mapctrl_header').show();

	$("#rangeMapModalDialog").focus();

	uploadckanRangeDialogPos = $('#rangeMapModalDialog').position();
}
</script>

</head>

<style type="text/css">
input:read-only {
	background-color: rgb(235, 235, 228);
	border-style: solid;
	border-width: 1px;
	border-color: rgb(169, 169, 169);
	padding: 1px;
}
.fm-button {
	padding: .0em .5em;
}
</style>

<body>
<div id="ckanWrapper">
	<h1><%=lang.__("Registration window to the clearinghouse")%></h1>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<form:form modelAttribute="uploadckanDataForm" action="/SaigaiTask/admin/uploadckanData">

<c:if test="${!uploadckanDataForm.authorized}">
	<div style="color:red;">
		 <%=lang.__("Auth key for registration/update can't be acquired. Check the CKAN auth key info table settings and whether the organization info links to CKAN server organization or not.")%>
	</div>
</c:if>

	<table border="1" class="form" style="float:left;">
		<tr>
			<th><%=lang.__("Target layer<!--2-->")%><font color="#ff0000">※</font></th>
			<td>
				<form:select  id="lyid" path="layerid" cssClass="styledselect">
					<form:option value=""><%=lang.__("Select<!--2-->")%></form:option>
					<c:forEach var="e" varStatus="s" items="${tablemasterInfos}">
						<c:if test="${!empty e.layerid}">
						<form:option value="${f:h(e.mapmasterInfo.mapid)}:${f:h(e.layerid)}:${f:h(e.id)}:${f:h(e.mapmasterInfo.communityid)}">${f:h(e.name)} </form:option>
						</c:if>
					</c:forEach>
				</form:select>
				<c:if test="${isRequiredLayerId}">
					<font color="#ff0000"><%=lang.__("Select layer.")%></font>
				</c:if>
			</td>
		</tr>
	</table>

	<br/>

	<table border="1" class="form" style="float:left;">

<%--
		<tr>
			<td colspan=2 id="registration_information" style="background-color: none;"></td>
		</tr>
--%>
		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Info for meta data")%></th>
		</tr>

		<tr>
			<th><%=lang.__("public/private")%></th>
			<td>
				<form:select id="isOpen" path="isOpen" cssClass="styledselect">
					<option value="1"><%=lang.__("in pubic")%></option>
					<option value="2"><%=lang.__("private")%></option>
				</form:select>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Info type ID")%></th>
			<td>
				<span><form:input path="infoType" size="40" readonly="true"/></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Dependent info type ID")%></th>
			<td>
				<span><form:input path="depInfoType" size="40"/></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("System name")%></th>
			<td>
				<span><form:input path="systemName" size="40" readonly="true" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("System URL")%></th>
			<td>
				<span><form:input path="systemUrl" size="40" readonly="true" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Language")%></th>
			<td>
				<span><form:input path="language" size="20"/></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("map info（maxx）")%></th>
			<td>
				<span><form:input path="maxx" size="20" readonly="true" /></span>
				<a class="ckan-callrangemap fm-button ui-state-default ui-corner-all" href="javascript:void(0)"><%=lang.__("Map")%></a>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("map info（maxy）")%></th>
			<td>
				<span><form:input path="maxy" size="20" readonly="true" /></span>
				<a class="ckan-callrangemap fm-button ui-state-default ui-corner-all" href="javascript:void(0)"><%=lang.__("Map")%></a>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("map info（minx）")%></th>
			<td>
				<span><form:input path="minx" size="20" readonly="true" /></span>
				<a class="ckan-callrangemap fm-button ui-state-default ui-corner-all" href="javascript:void(0)"><%=lang.__("Map")%></a>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("map info（miny）")%></th>
			<td>
				<span><form:input path="miny" size="20" readonly="true" /></span>
				<a class="ckan-callrangemap fm-button ui-state-default ui-corner-all" href="javascript:void(0)"><%=lang.__("Map")%></a>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("map info（spatial）")%></th>
			<td>
				<span><form:input path="spatial" cssStyle="width:99%;" readonly="true" /></span>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Info for map data")%></th>
		</tr>

		<tr>
			<th><%=lang.__("Title<!--2-->")%><font style="font-size:80%;"></font></th>
			<td>
				<span><form:input path="title" size="30" readonly="true" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("description")%></th>
			<td><form:textarea path="abstr" cols="60" rows="3"/></td>
		</tr>

		<tr>
			<th><%=lang.__("Search tags")%></th>
			<td>
				<span><form:input path="tags" size="30"/></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("License info")%></th>
			<td>
				<form:select id="licenseInfo" path="licenseInfo" cssClass="styledselect">
					<c:forEach var="e" varStatus="s" items="${licenseList}">
						<form:option value="${e.getString('id')}">${f:h(e.getString('title'))}</form:option>
					</c:forEach>
				</form:select>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Contact info for meta/map data")%><font style="font-size:80%;">(*1)</font></th>
		</tr>

		<tr>
			<th><%=lang.__("Organization")%></th>
			<td>
				<form:select id="organization" path="organization" cssClass="styledselect">
					<c:forEach var="e" varStatus="s" items="${organizationList}">
						<form:option value="${e.getString('id')}">${f:h(e.getString('title'))}</form:option>
					</c:forEach>
				</form:select>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Author")%></th>
			<td>
				<span><form:input path="author" size="30" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Creator's mail address")%></th>
			<td>
				<span><form:input path="authorEmail" size="30" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Creation date")%></th>
			<td>
				<span><input id="createdFormatted" size="30" readonly="true" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Maintainer")%></th>
			<td>
				<span><form:input path="maintainer" size="30" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Maintainer's mail address")%></th>
			<td>
				<span><form:input path="maintainerEmail" size="30" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("Modified date")%></th>
			<td>
				<span><input id="modifiedFormatted" size="30" readonly="true" /></span>
			</td>
		</tr>

		<tr>
			<th><%=lang.__("version No.")%></th>
			<td>
				<span><form:input path="version" size="20" /></span>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Time coverage info for map data")%></th>
		</tr>

		<tr>
			<th><%=lang.__("Info about time coverage")%></th>
			<td>
				<span id="spanExTmp"></span>
			</td>
		</tr>

		<tr>
			<th colspan=2 style="background-color: darksalmon;"><%=lang.__("Access info for map data")%></th>
		</tr>

		<tr>
			<th>WMS URL</th>
			<td><form:input id="wms" path="wms" readonly="true" cssStyle="width:99%;"/></td>
		</tr>

		<tr>
			<th>WFS URL</th>
			<td><form:input id="wfs" path="wfs" readonly="true" cssStyle="width:99%;"/></td>
		</tr>

		<tr>
			<td colspan=2 style="background-color: white;"><input type="checkbox" id="updateDefault" name="updateDefault" value="true"/><label for="updateDefault"><%=lang.__("Update meta data default info.")%></label><font style="font-size:80%;">(*1)</font></td>
		</tr>
	</table>

	<form:hidden id="registered" path="registered" value="false" />
	<form:hidden id="organizationTitle" path="organizationTitle" />
	<form:hidden  path="metadataCreated" />
	<form:hidden  path="metadataModified" />
	<input type="hidden"  id="mapId" />
	<input type="hidden"  id="layerId" />

	<div class="txtC" style="clear: left;">
		<input type="submit" name="insert" id="insert" value="<%=lang.__("Registration")%>" onclick="return confirm_insert();" style="width:120px;"/><br/>
	</div>
</form:form>
</div><!-- /#ckanWrapper -->
</body>
</html>
