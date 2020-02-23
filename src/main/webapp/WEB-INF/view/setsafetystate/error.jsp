<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../common/lang_resource.jsp" %>
<html>
<head>
<title><%=lang.__("Error page")%></title>
</head>
<body>

${f:h(errorMessage)}

</body>
</html>
