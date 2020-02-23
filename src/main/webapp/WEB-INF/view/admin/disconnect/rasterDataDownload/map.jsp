<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%=lang.__("Range selection on map")%></title>
<jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<!-- OpelLayesのライブラリを読み込み -->
<script type="text/javascript" src="${f:url('/js/OpenLayers/OpenLayers.js')}"></script>
<link class="theme blue" rel="stylesheet" href="${f:url('/js/tablesorter-2.28.5/css/theme.blue.css')}">
<script type="text/javascript" src="${f:url('/js/saigaitask.disconnect.js')}"></script>

<script type="text/javascript" >
var mapobj;
$(document).ready(function(){
	mapobj = SaigaiTask.disconnect.RasterDataDownload.loadMap("rasterDataDownload-map");
});

function setMapArea(base){
    var vector = SaigaiTask.disconnect.RasterDataDownload.getVectorLayer(mapobj);
    if (!vector || vector.length == 0)
        return;

    var bounds = vector[0].features[0].geometry.bounds;
    bounds = bounds.transform(new OpenLayers.Projection("EPSG:3857"), new OpenLayers.Projection("EPSG:4326"));
    // 選択された範囲を設定する

	var x1 = bounds.left.toFixed(6);
	var x2 = bounds.right.toFixed(6);
	var y1 = bounds.top.toFixed(6);
	var y2 = bounds.bottom.toFixed(6);

	window.opener.document.getElementById('contentsRasterdatadownloadFormStartLat').value = y1;
	window.opener.document.getElementById('contentsRasterdatadownloadFormStartLon').value = x1;
	window.opener.document.getElementById('contentsRasterdatadownloadFormEndLat').value = y2;
	window.opener.document.getElementById('contentsRasterdatadownloadFormEndLon').value = x2;
	window.close();
	return false;
}

</script>

<style>
  body {padding: 0; margin: 0}
  html, body, #rasterDataDownload-map {height: 100%; width: 100%;}
  div.olControlAttribution {
    padding: 3px;
    color:#000000;
    background-color:#ffffff;
    background-color:rgba(230,255,255,0.7);
    font-size:12px;
    line-height:14px;
    bottom:5px;
    vertical-align: middle;
  }
</style>
</head>
<body>
	<div id="rasterDataDownload-map">
	<table width='100%' border='0' cellspacing='0' cellpadding='0'>
		<tr>
			<td style='border: 0 !important; background-color: #CBCFE9 !important;'>
				<input type='radio' id='rdoScroll' name='mapmoderadio' value='none1' checked='checked' onchange='SaigaiTask.disconnect.RasterDataDownload.getSearchBoxControl(mapobj)[0].deactivate()'><%=lang.__("Scroll")%>
				<input type='radio' id='rdoRange' name='mapmoderadio' value='none2' onchange='SaigaiTask.disconnect.RasterDataDownload.getSearchBoxControl(mapobj)[0].activate()'><%=lang.__("Range specification")%>
			</td>
			<td align='right' style='border: 0 !important; background-color: #CBCFE9 !important;'>
				<input id='btnSetMapArea' type='button' value='<%=lang.__("Proceed")%>' onclick='setMapArea();'>
				<input type='button' value='<%=lang.__("Cancel")%>' onclick='window.close(); return false;'>
			</td>
		</tr>
	</table>
	</div>
</body>
</html>
