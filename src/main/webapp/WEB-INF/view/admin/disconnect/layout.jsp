<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../../common/lang_resource.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<script type="text/javascript" src="${f:url('/js/OpenLayers/OpenLayers.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.disconnect.js')}"></script>
<link class="theme blue" rel="stylesheet" href="${f:url('/js/tablesorter-2.28.5/css/theme.blue.css')}">
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.widgets.js')}"></script>

<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />
<style type="text/css">
html body #content .link {
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">
SaigaiTask.loginDataDto = {
	localgovinfoid: ${f:h(loginDataDto.localgovinfoid)}
	,groupid: ${f:h(loginDataDto.groupid)}
};
$(function(){
	// レイアウト初期化
	SaigaiTask.Layout.init();
});
</script>
</head>
<body style="visibility: hidden;">
	<div id="header" class="ui-layout-north">
		<jsp:include page="/WEB-INF/view/admin/disconnect/header.jsp" />
	</div>
	<div id="menu" class="ui-layout-west"><jsp:include page="/WEB-INF/view/admin/disconnect/menu.jsp" /></div>
	<div id="content" class="ui-layout-center">
		<tiles:insertAttribute name="content" />
	</div>
</body>
</html>
