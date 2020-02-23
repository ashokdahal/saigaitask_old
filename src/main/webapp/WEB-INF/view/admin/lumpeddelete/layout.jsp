<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
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
	localgovinfoid: ${loginDataDto.localgovinfoid}
	,groupid: ${loginDataDto.groupid}
};
$(function(){
	// レイアウト初期化
	SaigaiTask.Layout.init();
});
</script>
</head>
<body style="visibility: hidden;">
	<div id="content" class="ui-layout-center">
		<tiles:insertAttribute name="content" />
	</div>
</body>
</html>