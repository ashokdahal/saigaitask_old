<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../common/lang_resource.jsp" %>
<!DOCTYPE HTML>
<html>
<head><jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<meta charset="utf-8">
<title><%=lang.__("Disaster name: register window")%></title>
<script type="text/javascript">
// iframe を使っている場合
if(window!=parent){
	parent.location.reload();
	self.parent.tb_remove();
}
</script>
</head>
<body>
success
</body>
</html>
