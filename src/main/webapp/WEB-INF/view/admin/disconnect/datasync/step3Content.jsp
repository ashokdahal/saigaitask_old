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
	var form = document.getElementById("contents_datasync_step3form");

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
//alert("step3Content.jsp");
	return SaigaiTask.disconnect.submitForm('contents_datasync_step3form', '<%=lang._E("Are you sure to run import?")%>','<%=lang._E("Data merging...")%>');
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data synchronization")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_datasync_step3form" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/datasync/trackdatacheck" modelAttribute="datasyncForm">
	<% FormUtils.printToken(out, request); %>
	<form:hidden path="uploadTrackDataDirName" value="${uploadTrackDataDirName}"/>
	<form:hidden path="uploadTrackDataFileName" value="${uploadTrackDataFileName}"/>
	<%-- <form:hidden path="localgovinfoid" value= "${loginDataDto.localgovinfoid}" /> --%>
	<form:hidden id="contents_confirm_form_haserror" path="hasError" value= "${hasError}" />
	<form:hidden path="syncAll" />
	<form:hidden path="syncAttachedFile" />

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
					<th><%=lang.__("Disaster of import origin")%></th>
					<th><%=lang.__("Disaster of import destination")%></th>
				</tr>
			<c:forEach var="fileTrackData" varStatus="s" items="${disconnectImportConfirmDto.fileTrackDataList}">
				<tr>
					<td>
						<form:checkbox cssClass="selectFileTrackMultibox"  path="selectFileTrackMultibox" value="${fileTrackData.id}"/>
						${fileTrackData.id}:${fileTrackData.name}
					</td>
					<td>
						<c:forEach var="targetTrackData" varStatus="s" items="${disconnectImportConfirmDto.targetTrackDataList}">
							<form:checkbox cssClass="selectDbTrackMultibox"  path="selectDbTrackMultibox" value="${fileTrackData.id}:${targetTrackData.id}"/>
							${targetTrackData.id}:${targetTrackData.name}<BR>
						</c:forEach>
				</td>
				</tr>
			</c:forEach>
		</table>




</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

