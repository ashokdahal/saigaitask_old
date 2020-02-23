<%/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<script type="text/javascript" src="${f:url('/js/saigaitask.setupper.js')}"></script>
<link class="theme blue" rel="stylesheet" href="${f:url('/js/tablesorter-2.28.5/css/theme.blue.css')}">
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.widgets.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.balloon/jquery.balloon.min.js')}"></script>

<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />
<style type="text/css">
html body #content .link {
	color: blue;
	cursor: pointer;
}
/* jquery-ui font reset */
.ui-widget,.ui-helper-reset {
	font-size: 16px;
}
.ui-tabs .ui-tabs-nav li a {
	padding: 2px 4px;
}
/* track css */
table.table02 {
  margin-top: 0.2em;  margin-bottom: 0em;
  margin-left: auto;  margin-right: auto; /* centering */
/*  width:440px;*/
  width: 95%;
  border-collapse: collapse;
  border: solid 1px #999;
  font-size: 12px;
}

table.table02 caption {
  margin-top: 1em;
  text-align: left;
}

table.table02 th,
table.table02 td {
  border: solid 1px #999;
  padding: 4px 6px;
}

table.table02 th {
  background: #E6E6E6;
  text-align: center;
  white-space: nowrap;
  color: #666;
    font-weight: bolder;
}

table.table02 td.head {
	  background: #E6E6E6;
  text-align: center;
  font-size: 100%;
  font-weight: bolder;
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
		<jsp:include page="/WEB-INF/view/admin/setupper/header.jsp" />
	</div>
	<div id="menu" class="ui-layout-west"><jsp:include page="/WEB-INF/view/admin/setupper/menu.jsp" /></div>
	<div id="content" class="ui-layout-center">
		<tiles:insertAttribute name="content" />
	</div>
</body>
</html>
