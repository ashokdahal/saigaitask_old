<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<tiles:insertTemplate template="/WEB-INF/view/admin/multilang/layout.jsp" flush="true">
	<tiles:putAttribute name="content" type="string">
	<jsp:include page="importresultcontent.jsp"/>
	</tiles:putAttribute>
</tiles:insertTemplate>