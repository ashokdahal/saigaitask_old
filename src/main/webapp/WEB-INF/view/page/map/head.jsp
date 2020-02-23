<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>

	<!-- 地図機能のcss -->
	<link rel='stylesheet' type='text/css' href="${f:url('/js/SaigaiTaskJS/css/map.css')}"/>

	<script type="text/javascript" src="${f:url('/js/OpenLayers/OpenLayers.js')}"></script>

	<c:if test="${loginDataDto.geocoder=='GOOGLE'}">
	<!-- Google Maps API -->
	<!-- version 指定なしなら最新versionとなる. 指定したものがない場合は一番近いージョンとなる. -->
	<!-- 15/11/18 support version: 3.21.12, 3.22.12a, 3.23.0 -->
	<script type="text/javascript" src="https://maps.google.com/maps/api/js?v=3.23&sensor=false&region=IN&key=AIzaSyDdr1tIkkjTrzUqmdp--Mxoyh7LB9mG21I"></script>
	<script type="text/javascript">
		if(google.maps.version.match("^3.23")==null) {
			console.error("Google Maps API version: "+google.maps.version+" not support!"
				+"\nSupport version: 3.23");
		}
	</script>
	</c:if>

	<!-- 地図機能のJavascript -->
	<script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/falutil.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/SaigaiTask.js')}"></script>
	<!-- <script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/SaigaiTask-debug-all.js')}"></script> -->
	<!-- <script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/SaigaiTask-min.js')}"></script> -->
	<script type="text/javascript" src="${f:url('/js/saigaitask.page.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/saigaitask.page.map.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/saigaitask.page.filter.js')}"></script>
	<!-- 意志決定支援のJavascript -->
	<script type="text/javascript" src="${f:url('/js/saigaitask.page.decisionSupport.js')}"></script>
	<script type="text/javascript">SaigaiTask.timesliderConf = ${!empty pageDto.timesliderConf ? pageDto.timesliderConf : 'null'}</script>
	<script type="text/javascript" src="${f:url('/js/saigaitask.page.timeslider.js')}"></script>

	<!-- TableSorter -->
	<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
	<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>

	<!-- Clearinghouse Search Dialog -->
	<link rel="stylesheet" type="text/css" href="${f:url('/css/getRecordsResponseMap.css')}" />
	<script type="text/javascript">
<c:if test="${!loginDataDto.useCkan}">
	<jsp:include page="/WEB-INF/view/admin/grid/clearinghousesearch_form.jsp"></jsp:include>
	// ボタンの設定
	ClearingHouse_cswInsertMap = false;
	ClearingHouse_addMapPage = true;
	ClearingHouse_addAllMapPage = true;
</c:if>
<c:if test="${loginDataDto.useCkan}">
	<jsp:include page="/WEB-INF/view/admin/grid/ckansearch_form.jsp"></jsp:include>
	// ボタンの設定
	Ckan_insertMap = false;
	Ckan_addMapPage = true;
	Ckan_addAllMapPage = true;
</c:if>
	</script>
