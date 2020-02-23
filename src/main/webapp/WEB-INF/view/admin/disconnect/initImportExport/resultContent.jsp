<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data import / export window")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_confirm_form" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/initImportExport/importTrackdata" modelAttribute="initImportExportForm">
	<form:hidden path="importExecStatus" value="${f:h(constantDisconnectImportstatusStep3)}"/>
	<form:hidden path="uploadTrackDataDirName" value="${uploadTrackDataDirName}"/>
	<form:hidden path="uploadTrackDataFileName" value="${uploadTrackDataFileName}"/>
	<form:hidden path="localgovinfoid" value= "${loginDataDto.localgovinfoid}" />


	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<h1 style="margin: 0px;"><%=lang.__("Result of import")%></h1>

	<table  class="form">
		<tr>
			<td><a href="#" class="btn blue" onclick="return SaigaiTask.disconnect.backToTop(${loginDataDto.localgovinfoid});"><%=lang.__("Return")%></a></td>
		</tr>
	</table>

	<c:if test="${endMessage==''}">
	</c:if>
	<c:if test="${endMessage!=''}">
		<p>${f:h(endMessage)}</p>
	</c:if>

	<c:forEach var="importTrackData" varStatus="s" items="${disconnectImportConfirmDto.importTrackDataList}">
		<!--c:if test="${!s.first}"-->
		<h2 style="margin: 0px;"><%=lang.__("Disaster name")%>:${importTrackData.trackDataName}</h2>
		<table border="1" class="form">
			<tr>
				<th><%=lang.__("Import method")%></th>
				<th><%=lang.__("Record data ID of import source")%></th>
				<th><%=lang.__("Record data ID of import destination")%></th>
				<th><%=lang.__("Map ID of import source")%></th>
				<th><%=lang.__("Map ID of import destination")%></th>
			</tr>
			<tr>
				<c:if test="${importTrackData.isUpdate==false}">
					<td><%=lang.__("Register")%></td>
				</c:if>
				<c:if test="${importTrackData.isUpdate==true}">
					<td><%=lang.__("Update<!--2-->")%></td>
				</c:if>
				<td>${f:h(importTrackData.fileTrackDataId)}:${f:h(importTrackData.fileTrackDataName)}</td>
				<td>${f:h(importTrackData.dbTrackDataId)}:${f:h(importTrackData.dbTrackDataName)}</td>
				<td>${f:h(importTrackData.fileMapId)}</td>
				<td>${f:h(importTrackData.dbMapId)}</td>
			</tr>
		</table>
		<!--/c:if-->
	</c:forEach>



</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

