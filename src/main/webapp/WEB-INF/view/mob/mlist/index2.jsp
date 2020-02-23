<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>

<!DOCTYPE HTML>
<%@include file="../../common/jsp_lang.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<mata name="viewport" content="width=device-width,initial-scale=1">
<jsp:include page="/WEB-INF/view/common/mobhead.jsp"></jsp:include>


<script>
/**
 * @author
 */
var gMap = null;
var gMarkerCenter;
/*$(document).on('pagebeforecreate', '#pgPlanList', function(e){
	console.log('pagebeforecreate');
	//$('#aaa').append('<button>a</button>');
});*/

//$('#pgSpotMap').live('pageshow', drawMap);
$(document).on('pageshow','#pgSpotMap', function(){
	console.log('#pgSpotMap:pageshow')
	drawMap();
});


//// Map
function drawMap(){
	if (gMap == null){
		initMapM(); // 地図の初期化
	}
}
function initMapM(){
	function drawMarkerCenterInit(myMap, pos){
         var markerCenter = new google.maps.Marker({
             position: pos,
             map: myMap,
             title: 'map center:' + pos, // アイコンのタイトル (中心の経緯度を設定)
             icon: '/images/icon/cross2_red.ico', // アイコン画像を指定
             //shadow: '', // 影のアイコン画像
             draggable: true // ドラッグ可能にする
         });
         return markerCenter;
     }
	 console.debug('initMap()');
	var bodyHeight = $('body').height();
	//var pageHeight = $(document).height();
	$("#mMap").css('height',bodyHeight);
	var latlng = new google.maps.LatLng(35.709984, 139.810703);
    var opts = {
        //zoom: gOptions.map.zoom ?  parseInt(gOptions.map.zoom) : 15,
        zoom: 15,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    gMap = new google.maps.Map(document.getElementById('mMap'), opts);
	// センターマーカーを表示
    gMarkerCenter = drawMarkerCenterInit(gMap, latlng);
    // リスナーを追加：中心移動時にセンターマーカーを再描画(位置とタイトル)、情報パネル更新
    google.maps.event.addListener(gMap, 'center_changed', function(){
        var pos = gMap.getCenter();
        gMarkerCenter.setPosition(pos);
        //gMarkerCenter.setTitle('map center: ' + pos);
        // 情報パネル(Grid)の情報更新
        //if (gOptions.map.isUpdateGridRealtime == true){ setGridMapValue('center'); } //	setGridMapValue('center');
    });
}
function showMapSpot(){
	$.mobile.changePage($('#pgSpotMap'));
	drawMap();
	displaySpotOnMap(curPlan, '/images/icon/mm_20_purple.png', '/images/icon/mm_20_shadow.png');

}
function displaySpotOnMap(spot, icon, icon_shadow){
    var pos = new google.maps.LatLng(spot.spot_lat, spot.spot_lng);
    //var _icon = '/images/icon/mm_20_blue.png';
    //if (spots[i].icon.small == undefined) { _icon = '/images/icon/mm_20_blue.png'; } else { _icon = spots[i].icon.small; }
    var marker = new google.maps.Marker({
        position: pos,
        icon: icon,
		shadow : icon_shadow,
		animation: google.maps.Animation.DROP,
        map: gMap
    });
	return marker;
}
////


</script>
	</head>
	<body>

<div data-role="page">

	<div data-role="header">
		<h1><%=lang.__("NIED disaster information sharing system")%> </h1>
	</div><!-- /header -->

	<div data-role="content">

	<form>
	    <input id="filterTable-input" data-type="search" />
	</form>
<c:if test="${addable}">
	<a href="${f:url('create/')}${menutaskid}/${menuid}/${e[key]}" data-role="button" data-inline="true" data-theme="d" data-rel="dialog" data-transition="pop"><%=lang.__("Add new")%></a>
</c:if>
    <table id="tbl" data-role="table" data-mode="columntoggle" data-filter="true" data-input="#filterTable-input" class="tablesorter ui-responsive ui-shadow">
      <!--ヘッダー行で列選択の優先順位を設定-->
      <thead>
        <tr>
<c:if test="${editable}">
        <th><%=lang.__("Edit")%></th>
</c:if>
<c:if test="${key == 'gid'}">
        <th>Map</th>
</c:if>
        <c:forEach var="e" varStatus="s" items="${colinfoItems}">
        <c:set var="pri" value="${s.index+1}"/>
        <c:if test="${pri <= 5}"><c:set var="pri" value="1"/></c:if>
        <c:if test="${pri > 5}"><c:set var="pri" value="6"/></c:if>
          <th data-priority="${pri}">${f:h(e.name)}</th>
        </c:forEach>
        </tr>
      </thead>
      <tbody>
<c:forEach var="e" varStatus="s" items="${result}">
	<c:if test="${!filterIds[f:h(e[key])]}">
		<tr class="gray">
	</c:if>
	<c:if test="${filterIds[f:h(e[key])]}">
		<tr>
	</c:if>

<c:if test="${editable}">
	<td><a href="${f:url('detail/')}${menutaskid}/${menuid}/${e[key]}" data-role="button" data-icon="edit" data-iconpos="notext" data-rel="dialog" data-transition="pop">&nbsp;</a></td>
</c:if>
<c:if test="${key == 'gid'}">
	<c:if test="${e.theGeom!=null}">
	<td><a href="#pgSpotMap" data-ajax="false" data-role="button" data-icon="arrow-r" data-iconpos="notext" ></a></td>
	</c:if>
	<c:if test="${e.theGeom==null}">
	<td></td>
	</c:if>
</c:if>
	<c:forEach var="f" varStatus="t" items="${colinfoItems}">
		<c:set var="hi" value="" />
		<c:if test="${f.highlight}"><c:set var="hi" value=" highlight" /></c:if>
		<c:set var="st" value="" />
		<c:if test="${styleMap[f.id] != null}">
			<c:forEach var="g" varStatus="u" items="${styleMap[f.id]}">
				<c:if test="${g.val == e[f.attrid]}">
					<c:set var="st" value="${f:h(g.style)}" />
				</c:if>
			</c:forEach>
		</c:if>

		<%-- データ変換 --%>
		<c:set var="colVal" value="${e[f.attrid]}"/>

			<td style="${st}">${f:h(colVal)}</td>

	</c:forEach>
	</tr>
</c:forEach>
      </tbody>
    </table>

	</div><!-- /content -->
</div><!-- /page -->

<div data-role="page" id="pgSpotMap">
	<div data-role="header" ><h2>Map</h2></div>
	<div data-role="content">
		<!--<div id="mMap"></div>-->
		<div id="mMap" class="ui-shadow"></div>
	</div>
</div>

	</body>
</html>
