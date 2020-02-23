<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>

<script type="text/javascript">
var isValid = false;

$(function() {
	// var form = document.getElementById("contents_setup_form");
	// 新規作成の場合
	if(SaigaiTask.loginDataDto.localgovinfoid==0) {
		// do nothing
	}
	// 自治体指定の場合
	else {
		// 編集は未実装のため、新規作成でない場合は入力フォームを無効にする。
		// とりあえず何もしない
//		$("input,select,textarea", form).attr("disabled", true);
	}

	var fileForm = $('#contents_setup_importform_file')[0];
	if(fileForm.files[0]){
		$("#contents_setup_importform_runbutton").addClass('blue');
		$("#contents_setup_importform_runbutton").removeClass('lgray');
	}else{
		$("#contents_setup_importform_runbutton").addClass('lgray');
		$("#contents_setup_importform_runbutton").removeClass('blue');
	}

	$("#contents_setup_importform_runbutton").click(function(event){
		if(! isValid){
			event.preventDefault();
		}
	});

	$('#contents_setup_importform_file').on("change", function() {
        var file = this.files[0];
        if(file != null) {
            isValid = true;
        	$("#contents_setup_importform_runbutton").addClass('blue');
        	$("#contents_setup_importform_runbutton").removeClass('lgray');
        }
    });
});

function doImportTrackDataCheck() {
	if(isValid){
		return SaigaiTask.disconnect.submitForm('contents_setup_importform','','<%=lang._E("Now registering..")%>');
	}else{
		return false;
	}
}
function doExportTrackData() {
	// return SaigaiTask.disconnect.submitForm('contents_setup_exportform','','<%=lang._E("Now registering..")%>');
	// var token = "<%=FormUtils.getTokenParam(request)%>";
	// return SaigaiTask.disconnect.exporTrackdata(${loginDataDto.localgovinfoid}, token);
	return SaigaiTask.disconnect.exporTrackdata('contents_setup_exportform');
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data import / export window")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_setup_importform" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/initImportExport/importTrackdata" modelAttribute="initImportExportForm">
	<% FormUtils.printToken(out, request); %>
	<form:hidden path="importExecStatus" value="${f:h(constantDisconnectImportstatusStep1)}"/>
	<form:hidden path="localgovinfoid" value="${initImportExportForm.localgovinfoid}" />

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<c:if test="${loginDataDto.localgovinfoid==0}">
		<h2 style="margin: 0px;"><%=lang.__("Specify local gov. ID")%></h2>
		<table border="1" class="form">
		<tr>
		<th><%=lang.__("Select local gov.")%></th>
		<td>
			<form:select path="selectLocalgov"  id="contents_setup_importform_select" >
    			<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
        			<form:option value="${localgovSelectOption.key}">${localgovSelectOption.key}: ${f:h(localgovSelectOption.value)}</form:option>
    			</c:forEach>
			</form:select>
		</td>
		</tr>
		</table>
	</c:if>

	<h2 style="margin: 0px;"><%=lang.__("Import disaster data")%></h2>
	<table border="1" class="form">
		<tr>
		</tr>
		<tr>
			<th rowspan="2"><%=lang.__("Specify the disaster data file, please click the import confirmation button.")%></th>
			<td><input type="file" id="contents_setup_importform_file" name="trackDataFile"/></td>
		</tr>
		<tr>
			<td>
				<a href="#" class="btn blue" id="contents_setup_importform_runbutton" onclick="return doImportTrackDataCheck();"><%=lang.__("Import confirmation")%></a>
			</td>
		</tr>
	</table>

	<table border="1" class="form">
		<tr>
			<th rowspan="2"><%=lang.__("Import method")%></th>
			<td>
				<form:radiobutton path="importAll" value="1" />
				<%=lang.__("All history")%>
				<form:radiobutton path="importAll" value="0" />
				<%=lang.__("Only latest data")%>
			</td>
		</tr>
		<tr>
			<td>
				<form:radiobutton path="importAttachedFile" value="1" />
				<%=lang.__("Include attached file")%>
				<form:radiobutton path="importAttachedFile" value="0" />
				<%=lang.__("Not include attached file")%>
			</td>
		</tr>
	</table>
</form:form>

<form:form id="contents_setup_exportform" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/export/download" modelAttribute="initImportExportForm">
	<% FormUtils.printToken(out, request); %>
	<form:hidden id="exportLocalgovinfoid" path="localgovinfoid" value="${loginDataDto.localgovinfoid}" />

	<h2 style="margin: 0px;"><%=lang.__("Export disaster data")%></h2>
	<table border="1" class="form">
		<tr>
			<th><%=lang.__("Download the disaster data file .")%></th>
			<td>
			<a href="#" class="btn blue"  onclick="return doExportTrackData();"><%=lang.__("Export")%></a>
			</td>
		</tr>
	</table>

	<table border="1" class="form">
		<tr>
			<th><%=lang.__("Export method")%></th>
			<td>
				<form:radiobutton path="exportAttachedFile" value="1" />
				<%=lang.__("Include attached file")%>
				<form:radiobutton path="exportAttachedFile" value="0" />
				<%=lang.__("Not include attached file")%>
			</td>
		</tr>
	</table>
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

