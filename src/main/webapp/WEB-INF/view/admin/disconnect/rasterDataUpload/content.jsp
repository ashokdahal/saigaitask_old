<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>
<script type="text/javascript" src="${f:url('/admin-js/js/ajaxfileupload.js')}" ></script>

<script type="text/javascript">
$(function() {
});

/**
 * アップロードを実行する
 */
function doUploadRasterData() {
	/*
	$('#contents_setup_form').attr({ action: '${f:url("upload")}?<%=FormUtils.getTokenParam(request)%>' })
	SaigaiTask.disconnect.submitForm('contents_setup_form', 'confirmMsg','<%=lang._E("Now registering..")%>');
	return;
	*/

	if (!$("[name=rasterDataFile]").val()) {
		$("#contentsRasterdatauploadFormAlertMessage").text(lang.__('<%=lang.__("Select the raster data file.")%>'));
		return false;
	}
	var token = "${cookie.JSESSIONID.value}";
	var url = '${f:url("/admin/disconnect/rasterDataUpload/upload")}';
	SaigaiTask.disconnect.RasterDataUpload.uploadForm('contents_setup_form', url, token);
	return true;
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Upload raster data")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_setup_form" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" modelAttribute="rasterDataUploadForm">

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<div style="margin-bottom:1em;">
		<c:if test="${loginDataDto.localgovinfoid==0}">
			<h2 style="margin: 0px;"><%=lang.__("Specify local gov. ID")%></h2>
			<table border="1" class="form">
			<tr>
			<th width="150"><%=lang.__("Select local gov.")%></th>
			<td width="650">
				<form:select path="selectLocalgov" id="contentsRasterdatadownloadFormSelectLocalgov">
	    			<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
	        			<form:option value="${localgovSelectOption.key}">${f:h(localgovSelectOption.key)}: ${f:h(localgovSelectOption.value)}</form:option>
	    			</c:forEach>
				</form:select>
			</td>
			</tr>
			</table>
		</c:if>
	</div>

	<h2 style="margin: 0px;"><%=lang.__("Upload raster data")%></h2>
	<table border="1" class="form">
		<tr>
		</tr>
		<tr>
			<th rowspan="2"><%=lang.__("Specify the raster data file, please click the upload button.")%></th>
			<td><input type="file" name="rasterDataFile"/></td>
		</tr>
		<tr>
			<td>
				<a href="#" class="btn blue" onclick="return doUploadRasterData();"><%=lang.__("Upload")%></a>
			</td>
		</tr>
	</table>

	<div style="margin-bottom:1em;">
		<table border="1" class="form">
			<tr>
				<th width="150"><%=lang.__("Alert info")%></th>
				<td width="650">
					<span id="contentsRasterdatauploadFormAlertMessage" style="color:#ff0000;"></span>
					<span id="contentsRasterdatauploadFormSuccessMessage" style="color:#000000;"></span>
				</td>
			</tr>
		</table>
	</div>

 	<form:hidden path="mapId" />
	<form:hidden path="ecomUser" />
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

