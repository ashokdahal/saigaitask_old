<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="../../common/lang_resource.jsp" %>
<html>
	<head><jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
		<title><%=lang.__("Error page")%></title>
	</head>
	<body>
		<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
	</body>
</html>
