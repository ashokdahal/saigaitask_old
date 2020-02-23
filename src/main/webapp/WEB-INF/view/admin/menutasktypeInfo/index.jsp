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

	<script type="text/javascript" src="${f:url('/admin-js/js/utils.js')}"></script>

	<script type="text/javascript" src="${f:url('/js/gldp.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/OpenLayers/lib/OpenLayers.js')}"></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/map2.js')}"></script>
	<link type="text/css" media="screen" href="${f:url('/css/getRecordsResponseMap.css')}" rel="stylesheet" />
</head>
<body>
<div id="wrapper">
	<header>
		<div id="h_main">
			<h2><%=lang.__("Task type info")%></h2>
			<hr>
		</div>
	</header>

	<!-- データ一覧 -->
	<script type="text/javascript">
		//alert("--- Hello from menutasktypeInfo/index.jsp ---");

		$(document).ready(function(){
			//使用するグリッドJSPをインクルードする。
			<jsp:include page="../grid/menutasktype_info.jsp" flush="true" />
			<jsp:include page="../grid/menu_info.jsp" flush="true" />
			<jsp:include page="../grid/filtering.jsp" flush="true" />
			<jsp:include page="../grid/menumap_info.jsp" flush="true" />
			<%-- 外部地図データ情報と外部リストデータ情報は、メニュー情報の「外部地図表示レイヤ設定」ボタンから操作すべきなので非表示に変更。 --%>
			<%--jsp:include page="../grid/externaltabledata_info.jsp" flush="true" /--%>
			<%--jsp:include page="../grid/externalmapdata_info.jsp" flush="true" /--%>

			<jsp:include page="../grid/menuaction_info.jsp" flush="true" />
			<jsp:include page="../grid/mapbaselayer_info.jsp" flush="true" />
			<jsp:include page="../grid/mapreferencelayer_info.jsp" flush="true" />
			<%--
			    震度レイヤがテーブルマスタ化したため、maplayer_info で表示設定を行うようになったため、
			    一旦、非表示に変更する
			<jsp:include page="../grid/meteolayer_info.jsp" flush="true" />
			--%>
			<jsp:include page="../grid/mapkmllayer_info.jsp" flush="true" />
			<jsp:include page="../grid/maplayerattr_info.jsp" flush="true" />
			<jsp:include page="../grid/maplayer_info.jsp" flush="true" />
			<jsp:include page="../grid/pagemenubutton_info.jsp" flush="true" />
			<jsp:include page="../grid/menutable_info.jsp" flush="true" />
			<jsp:include page="../grid/tablelistcolumn_info.jsp" flush="true" />
			<jsp:include page="../grid/tablelistkarte_info.jsp" flush="true" />
			<jsp:include page="../grid/menu_info.jsp" flush="true" />
			<jsp:include page="../grid/innermap_info.jsp" flush="true" />
			<jsp:include page="../grid/outermap_info.jsp" flush="true" />
			<jsp:include page="../grid/noticedefault_info.jsp" flush="true" />
			<jsp:include page="../grid/tablerowstyle_info.jsp" flush="true" />
<c:if test="${!loginDataDto.useCkan}">
			<jsp:include page="../grid/clearinghousesearch_form.jsp" flush="true" />
</c:if>
<c:if test="${loginDataDto.useCkan}">
			<jsp:include page="../grid/ckansearch_form.jsp" flush="true" />
</c:if>
			<jsp:include page="../grid/noticedefaultgroup_info.jsp" flush="true" />
			<jsp:include page="../grid/alarmdefaultgroup_info.jsp" flush="true" />
			<jsp:include page="../grid/filter_info.jsp" flush="true" />
			//自グリッド設定
			menutasktype_info();
		});
	</script>
	<div id="main">
	</div>
<!-- /#wrapper --></div>
</body>
</html>
