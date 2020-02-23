<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<tiles:insertTemplate template="/WEB-INF/view/common/layout.jsp" flush="true">
<tiles:putAttribute name="content" type="string">
<!-- 65535問題を回避するためにJSPを二つに分ける -->
	<jsp:include page="content.jsp"/>
	<%-- <jsp:include page="content2.jsp"/> 2017.07.28 kawada 移動：同フォームが２つ存在していたため --%>
</tiles:putAttribute>
</tiles:insertTemplate>

