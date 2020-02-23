<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<div id="global_header_admin">
	<div id="global_header_title" style="float: left;">
		<a href="${f:url('/page/')}" style="color: black; text-decoration: none;"><span class="shadow">${f:h(siteName)}</span></a>
		<a href="${f:url('/admin/training')}" style="text-decoration: none;"><span style="color: white;"><%=lang.__("Training panel")%></span></a>
		<c:if test="${1<fn:length(localgovSelectOptions)}">
		<select id="loginlocalgov-select" style="position: relative; top: -1px; font-size:12px;"
			onchange="location.href='${f:url('/admin/training/?localgovinfoid=')}'+$(this).val()">
			<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
			<c:set var="govid" value="${JsonGetCurLocalGovID}"/>
			<c:set var="selected" value="${localgovSelectOption.key==govid}"/>
			<c:if test="${localgovSelectOption.key==0}">
			<option value="${localgovSelectOption.key}" ${selected?"selected":""}></option>
			</c:if>
			<c:if test="${localgovSelectOption.key!=0}">
			<option value="${localgovSelectOption.key}" ${selected?"selected":""}>${localgovSelectOption.key}: ${f:h(localgovSelectOption.value)}</option>
			</c:if>

			</c:forEach>
		</select>
		</c:if>
	</div>
	<div id="globalnav" style="float: right; padding-top:5px; width:auto; height:23px; line-height:23px; font-size:12px; ">
		<a href="${f:url('/admin/mainFrame')}"  style="color:white; margin-left: 5px;"><%=lang.__("Admin window")%></a>
		<c:if test="${! loginDataDto.usual}">
			<a href="#"                             style="color:white; margin-left: 5px; text-decoration:none; cursor: default;">[${f:h(loginDataDto.groupInfo.name)}]</a>
		</c:if>
		<c:if test="${loginDataDto.usual}">
			<a href="#"                             style="color:white; margin-left: 5px; text-decoration:none; cursor: default;">[${f:h(loginDataDto.unitInfo.name)}]</a>
		</c:if>
		<a href="${f:url('/logout?type=admin')}"    style="color:white; margin-left: 5px;"><%=lang.__("Logout")%></a>
	</div>
	<div style="clear: both;"></div>
</div>
