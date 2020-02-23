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
			<h2><%=lang.__("Page button info")%></h2>
			<hr>
		</div>
	</header>

	<!-- データ一覧 -->
	<script type="text/javascript">
		$(document).ready(function(){
			//使用するグリッドJSPをインクルードする。
			<jsp:include page="../grid/pagemenubutton_info.jsp" flush="true" />

			//自グリッド設定
			pagemenubutton_info();
		});
	</script>
	<div id="main">
	</div>
<!-- /#wrapper --></div>
</body>
</html>
