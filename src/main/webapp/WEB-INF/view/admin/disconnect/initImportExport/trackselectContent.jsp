<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>

<script type="text/javascript">
$(function() {
	var form = document.getElementById("contents_trackselect_form");
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

	if($("#contents_confirm_form_haserror").val()){
		$("#contents_confirm_form_runbutton").addClass('lgray');
		$("#contents_confirm_form_runbutton").removeClass('blue');
		$("#contents_confirm_form_runbutton").removeAttr("onclick");
	}

	$("#contents_confirm_form_runbutton").click(function(event){
		if($("#contents_confirm_form_haserror").val()){
			event.preventDefault();
		}
	});

//	$(".selectFileTrackMultibox").each(function(i, elem) {
//		if($(elem).prop('checked')){
//			$(elem).prop('disabled',true);
//		}
//	});

//	$(".selectDbTrackMultibox").each(function(i, elem) {
//		if($(elem).prop('checked')){
//			$(elem).prop('disabled',true);
//		}
//	});

});
function doTrackSelectExec() {
	return SaigaiTask.disconnect.submitForm('contents_trackselect_form', '<%=lang._E("Are you sure to run import?")%>','<%=lang._E("Now registering..")%>');
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data import / export window")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_trackselect_form" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/initImportExport/importTrackdata" modelAttribute="initImportExportForm">
	<% FormUtils.printToken(out, request); %>
	<form:hidden path="importExecStatus" value="${f:h(constantDisconnectImportstatusStep2)}"/>
	<form:hidden path="uploadTrackDataDirName" value="${uploadTrackDataDirName}"/>
	<form:hidden path="uploadTrackDataFileName" value="${uploadTrackDataFileName}"/>
	<form:hidden path="localgovinfoid" value= "${loginDataDto.localgovinfoid}" />
	<form:hidden id="contents_confirm_form_haserror" path="hasError" value= "${hasError}" />
	<form:hidden path="importAll" />
	<form:hidden path="importAttachedFile" />

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<h1 style="margin: 0px;"><%=lang.__("Import confirmation")%></h1>

	<table  class="form">
		<tr>
			<td><a href="#" class="btn blue" onclick="return SaigaiTask.disconnect.backToTop(${loginDataDto.localgovinfoid});"><%=lang.__("Return")%></a></td>
			<td><a href="#" id="contents_confirm_form_runbutton" class="btn blue" onclick="return doTrackSelectExec();"><%=lang.__("Run import")%></td>

		</tr>
	</table>

		<table border="1" class="form">
				<tr>
					<th><%=lang.__("Import-from disaster")%></th>
					<th><%=lang.__("Import-to disaster")%></th>
				</tr>
			<c:forEach var="fileTrackData" varStatus="s" items="${disconnectImportConfirmDto.fileTrackDataList}">
				<tr>
					<td>
						<form:checkbox cssClass="selectFileTrackMultibox"  path="selectFileTrackMultibox" value="${fileTrackData.id}"/>
						${f:h(fileTrackData.id)}:${f:h(fileTrackData.name)}
					</td>
					<td>
						<c:forEach var="targetTrackData" varStatus="s" items="${disconnectImportConfirmDto.targetTrackDataList}">
							<form:checkbox cssClass="selectDbTrackMultibox"  path="selectDbTrackMultibox" value="${fileTrackData.id}:${targetTrackData.id}"/>
							${f:h(targetTrackData.id)}:${f:h(targetTrackData.name)}<BR>
						</c:forEach>
				</td>
				</tr>
			</c:forEach>
		</table>




</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

