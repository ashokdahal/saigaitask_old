<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data synchronization")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
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
		<p>${endMessage}</p>
	</c:if>

	<c:forEach var="importTrackData" varStatus="s" items="${disconnectImportConfirmDto.importTrackDataList}">
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
				<td>${importTrackData.fileTrackDataId}:${importTrackData.fileTrackDataName}</td>
				<td>${importTrackData.dbTrackDataId}:${importTrackData.dbTrackDataName}</td>
				<td>${importTrackData.fileMapId}</td>
				<td>${importTrackData.dbMapId}</td>
			</tr>
		</table>
	</c:forEach>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

