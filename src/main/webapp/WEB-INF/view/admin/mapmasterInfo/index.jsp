<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<%@include file="../common/adminjs-header.jsp" %>

	<link type="text/css" media="screen" href="${f:url('/css/gldp.css')}" rel="stylesheet" />
	<link type="text/css" media="screen" href="${f:url('/js/OpenLayers/theme/default/style.css')}" rel="stylesheet" />

	<script type="text/javascript" src="${f:url('/js/gldp.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/OpenLayers/lib/OpenLayers.js')}"></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/map2.js')}"></script>
	<link type="text/css" media="screen" href="${f:url('/css/getRecordsResponseMap.css')}" rel="stylesheet" />
</head>
<body>
<div id="wrapper">
	<header>
		<div id="h_main">
			<h2><%=lang.__("Map master info")%></h2>
			<hr>
		</div>
	</header>

	<!-- データ一覧 -->
	<script type="text/javascript">
		$(document).ready(function(){
			//使用するグリッドJSPをインクルードする。
			<jsp:include page="../grid/tablemaster_info.jsp" flush="true" />
			<jsp:include page="../grid/timelinetable_info.jsp" flush="true" />
			<jsp:include page="../grid/mapmaster_info.jsp" flush="true" />
			<jsp:include page="../grid/tablecalculatecolumn_info.jsp" flush="true" />
			<jsp:include page="../grid/tablecalculate_info.jsp" flush="true" />
			<jsp:include page="../grid/tableresetcolumn_data.jsp" flush="true" />
			//自グリッド設定
			mapmaster_info();
		});
	</script>

	<div id="main">
	</div>
<!-- /#wrapper --></div>
</body>
</html>
