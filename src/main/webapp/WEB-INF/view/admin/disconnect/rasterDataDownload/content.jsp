<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>
<script type="text/javascript">

function addtokenhref() {
	var token = "<%=FormUtils.getTokenParam(request)%>";
	// 今の内容を保存しておく
	var nowContent = $("#contentsRasterdatadownloadFormDownloadLink").html();

	// トークンを追加
	var nowHref = $("#contentsRasterdatadownloadFormDownloadLinkHref").attr("href");
	$("#contentsRasterdatadownloadFormDownloadLinkHref").attr("href",nowHref+"&"+token)

	// ダウンロードリンクを削除
	$("#contentsRasterdatadownloadFormDownloadLink").html("<%=lang.__("Download has been performed.")%>");

	return false;
}
function addtokencalc() {
	var token = "${cookie.JSESSIONID.value}";
	return SaigaiTask.disconnect.RasterDataDownload.calc(token);
}
function addtokendownload() {
	var token = "${cookie.JSESSIONID.value}";
	return SaigaiTask.disconnect.RasterDataDownload.download(token);
}


</script>


<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Download raster data")%></h1>
</div>


<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contentsRasterdatadownloadForm" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/rasterDataDownload/download" modelAttribute="rasterDataDownloadForm">

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<div style="margin-bottom:1em;">
		<c:if test="${loginDataDto.localgovinfoid==0}">
			<h2 style="margin: 0px;"><%=lang.__("Specify local gov. ID")%></h2>
			<table border="1" class="form">
			<tr>
			<th width="150"><%=lang.__("Select local gov.")%></th>
			<td width="650">
				<form:select path="selectLocalgov"  id="contentsRasterdatadownloadFormSelectLocalgov"  onchange="return SaigaiTask.disconnect.RasterDataDownload.changeLocolgov()">
	    			<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
	        			<form:option value="${localgovSelectOption.key}">${localgovSelectOption.key}: ${f:h(localgovSelectOption.value)}</form:option>
	    			</c:forEach>
				</form:select>
			</td>
			</tr>
			</table>
		</c:if>
	</div>

	<div style="margin-bottom:1em;">
		<table border="1" class="form">
			<tr>
			</tr>
			<tr>
				<th width="150"><%=lang.__("System name<!--2-->")%></th>
				<td width="650"><span id="contentsRasterdatadownloadFormSystemname">${f:h(rasterDataDownloadForm.systemname)}</span></td>
			</tr>
		</table>
	</div>

	<div style="margin-bottom:1em;">
		<table border="1" class="form">
			<tr>
				<th width="150"><%=lang.__("Target map")%></th>
				<td width="650">
					<form:select path="selectMapUrl"  id="contentsRasterdatadownloadFormSelectMapUrl" >
		    			<c:forEach var="mapUrlSelectOptions" items="${mapUrlSelectOptions}">
		        			<form:option value="${mapUrlSelectOptions.value}">${mapUrlSelectOptions.key}: ${f:h(mapUrlSelectOptions.value)}</form:option>
		    			</c:forEach>
					</form:select>
				</td>
			</tr>
			<tr>
				<th width="150"><%=lang.__("Target zoom level")%></th>
				<td width="650"><span id="contentsRasterdatadownloadFormZoomLevelLabel">${f:h(rasterDataDownloadForm.startZoomLevel)} - ${f:h(endZoomLevel)}</span></td>
				<form:hidden id="contentsRasterdatadownloadFormLocalgovType" path="localgovType" value="${f:h(rasterDataDownloadForm.localgovType)}"></form:hidden>
				<form:hidden id="contentsRasterdatadownloadFormStartZoomLevel" path="startZoomLevel" value="${f:h(rasterDataDownloadForm.startZoomLevel)}"></form:hidden>
				<form:hidden id="contentsRasterdatadownloadFormEndZoomLevel" path="endZoomLevel" value="${f:h(rasterDataDownloadForm.endZoomLevel)}"></form:hidden>
			</tr>
		</table>
	</div>

	<div style="margin-bottom:1em;">
		<div style="float:left;width:600px;">
			<table border="1" class="form">
				<tr>
					<th  rowspan="2"><%=lang.__("Target district")%></th>
					<td ><%=lang.__("Start latitude")%>&nbsp;<form:input id="contentsRasterdatadownloadFormStartLat" path="startLat" size="15"></form:input></td>
					<td ><%=lang.__("Start longitude")%>&nbsp;<form:input id="contentsRasterdatadownloadFormStartLon" path="startLon" size="15"></form:input></td>
				</tr>
				<tr>
					<td ><%=lang.__("End latitude")%>&nbsp;<form:input id="contentsRasterdatadownloadFormEndLat" path="endLat" size="15"></form:input></td>
					<td ><%=lang.__("End longitude")%>&nbsp;<form:input id="contentsRasterdatadownloadFormEndLon" path="endLon" size="15"></form:input></td>
				</tr>
		</table>
		</div>
		<div style="margin-left:10px;float:right;width: 190px;">
			<table>
			<tr>
				<td>
					<a href="#" class="btn blue" onclick="return SaigaiTask.disconnect.RasterDataDownload.openMapWindow();"><%=lang.__("Specify range on map")%></a>
				</td>
			</tr>
			</table>
		</div>
	</div>

	<div style="clear:both;"></div>

	<div style="margin-bottom:1em;">
		<div style="float:left;width:600px;">
			<table border="1" class="form">
				<tr>
					<th width="150" rowspan="2"><%=lang.__("Approximation")%></th>
					<td width="100"><%=lang.__("Data size")%>:</td>
					<td ><span id="contentsRasterdatadownloadFormDataSize">${f:h(rasterDataDownloadForm.dataSize)}</span></td>
				</tr>
				<tr>
					<td width="100"><%=lang.__("Download time")%>:</td>
					<td ><span id="contentsRasterdatadownloadFormDownloadTime">${f:h(rasterDataDownloadForm.downloadTime)}</span></td>
				</tr>
			</table>
		</div>
		<div style="margin-left:10px;float:right;width:190px;">
			<table>
			<tr>
				<td>
					<a href="#" class="btn blue" onclick="addtokencalc();"><%=lang.__("Recalculation")%></a>
				</td>
			</tr>
			</table>
		</div>
	</div>

	<div style="clear:both;"></div>

	<div style="margin-bottom:1em;">
		<table border="1" class="form">
			<tr>
				<th width="150"><%=lang.__("Alert info")%></th>
				<td width="650">
					<span id="contentsRasterdatadownloadFormAlertMessage" style="color:#ff0000;">${f:h(rasterDataDownloadForm.alertMessage)}</span>
					<span id="contentsRasterdatadownloadFormDownloadLink" style="color:#000000;"></span>
				</td>
			</tr>
		</table>
	</div>

	<div style="clear:both;"></div>

	<h1><%=lang.__("Download")%><%=lang.__("List")%></h1>
	<table border="1" class="form" style="width:100%">
		<tr>
			<th>No.</th>
			<th><%=lang.__("Download")%><%=lang.__("Link")%></th>
		</tr>
		<c:forEach var="rasterDownloadStatus" items="${rasterDownloadStatusList}" varStatus="s">
		<tr>
			<td>${s.index+1}</td>
			<td>
				<c:choose>
					<%-- ダウンロード済みの場合 --%>
					<c:when test="${! empty rasterDownloadStatus.zipfilename}">
						<a href="${f:url('/admin/disconnect/rasterDataDownload/downloadfile?zipfile=')}${rasterDownloadStatus.zipfilename}&<%=FormUtils.getTokenParam(request)%>" type='application/x-compress' onclick='setTimeout(function(){var url = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/content";SaigaiTask.disconnect.loadContent(url);}, 1000);'>${rasterDownloadStatus.zipfilename}</a>
					</c:when>
					<c:otherwise>
						<c:set var="progress" value="${rasterDownloadStatus.downloadedPixCalcSum/rasterDownloadStatus.pixCalcSum*100}"/>
						<%=lang.__("Download")%><%=lang.__("Status")%>:<fmt:formatNumber value="${progress}" maxFractionDigits="1"/>%
						<c:if test="${progress==100}"><%=lang.__("Zip file in process of creation")%></c:if>
						<c:if test="${rasterDownloadStatus.cancel}"><%=lang.__("Canceled")%></c:if>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		</c:forEach>
	</table>
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
	<div style="float:left;">
		<a href="#" class="btn blue" onclick="addtokendownload();"><%=lang.__("Execution")%></a>
	</div>
	<div style="float:right;">
		<a href="#" class="btn blue" onclick="return SaigaiTask.disconnect.backToTop(${loginDataDto.localgovinfoid});"><%=lang.__("Return")%></a>
	</div>
</div>


