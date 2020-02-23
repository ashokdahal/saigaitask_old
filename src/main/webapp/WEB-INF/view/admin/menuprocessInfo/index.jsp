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
</head>
<body>
<div id="wrapper">
	<header>
		<div id="h_main">
			<h2><%=lang.__("Menu hierarchy")%></h2>
			<hr>
		</div>
	</header>

	<!-- データ一覧 -->
	<script type="text/javascript">
		$(document).ready(function(){
			//使用するグリッドJSPをインクルードする。

			<jsp:include page="../grid/menumap_info.jsp" flush="true" />
			<jsp:include page="../grid/externaltabledata_info.jsp" flush="true" />
			<jsp:include page="../grid/externalmapdata_info.jsp" flush="true" />
			<jsp:include page="../grid/menuaction_info.jsp" flush="true" />
			<jsp:include page="../grid/mapbaselayer_info.jsp" flush="true" />
			<jsp:include page="../grid/mapreferencelayer_info.jsp" flush="true" />
			<jsp:include page="../grid/maplayerattr_info.jsp" flush="true" />
			<jsp:include page="../grid/maplayer_info.jsp" flush="true" />
			<jsp:include page="../grid/pagemenubutton_info.jsp" flush="true" />
			<jsp:include page="../grid/tablelistcolumn_info.jsp" flush="true" />
			<jsp:include page="../grid/menutable_info.jsp" flush="true" />
			<jsp:include page="../grid/menu_info.jsp" flush="true" />
			<jsp:include page="../grid/menutask_info.jsp" flush="true" />
			<jsp:include page="../grid/menuprocess_info.jsp" flush="true" />
			<jsp:include page="../grid/innermap_info.jsp" flush="true" />
			<jsp:include page="../grid/outermap_info.jsp" flush="true" />

			//自グリッド設定
			menuprocess_info();
		});
	</script>
	<div id="main">
	</div>
<!-- /#wrapper --></div>
</body>
</html>
