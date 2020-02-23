(function() {

	var scriptName = "lib/SaigaiTask.js";

	if (typeof window.SaigaiTask == "undefined") {
		window.SaigaiTask = {};
	}
	window.SaigaiTask._getScriptLocation = (function() {
		var r = new RegExp("(^|(.*?\\/))(" + scriptName + ")(\\?|$)"), s = document
				.getElementsByTagName('script'), src, m, l = "";
		for ( var i = 0, len = s.length; i < len; i++) {
			src = s[i].getAttribute('src');
			if (src) {
				m = src.match(r);
				if (m) {
					l = m[1];
					break;
				}
			}
		}
		return (function() {
			return l;
		});
	})();

    jsFiles = [
               "SaigaiTask/Map.js",
               "SaigaiTask/Map/API.js",
               "SaigaiTask/Map/Icon.js",
               "SaigaiTask/Map/EcommapInfo.js",
               "SaigaiTask/Map/Layer.js",
               "SaigaiTask/Map/Layer/Type.js",
               "SaigaiTask/Map/Layer/TimeSeriesType.js",
               "SaigaiTask/Map/Layer/Group.js",
               "SaigaiTask/Map/Layer/LayerInfo.js",
               "SaigaiTask/Map/Layer/AttrInfo.js",
               "SaigaiTask/Map/Layer/WMSLayer.js",
               "SaigaiTask/Map/Layer/TileCacheLayer.js",
               "SaigaiTask/Map/Layer/OSMLayer.js",
               "SaigaiTask/Map/Layer/GoogleLayer.js",
               "SaigaiTask/Map/Layer/ArcGIS93Rest.js",
               "OpenLayers/Layer/XYZZoom.js",
               "OpenLayers/Layer/GSITile.js",
               "OpenLayers/Layer/Google/v3.js",
               "SaigaiTask/Map/Layer/WebtisLayer.js",
               "SaigaiTask/Map/Layer/XYZLayer.js",
               "SaigaiTask/Map/Layer/DrawLayerSelectDrag.js",
               "SaigaiTask/Map/Layer/DrawLayer.js",
               "SaigaiTask/Map/Layer/KMLLayer.js",
               "SaigaiTask/Map/Layer/PdfRangeLayer.js",
               "SaigaiTask/Map/VectorLayer.js",
               "SaigaiTask/Map/view.js",
               "SaigaiTask/Map/view/ContextMenu.js",
               "SaigaiTask/Map/view/ContentsFileFormPanel.js",
               "SaigaiTask/Map/view/ContentsFormPanel.js",
               "SaigaiTask/Map/view/ContentsFormWindow.js",
               "SaigaiTask/Map/view/Popup.js",
               "SaigaiTask/Map/view/ContentsPopup.js",
               "SaigaiTask/Map/view/RefContentsPopup.js",
               "SaigaiTask/Map/view/KMLPopup.js",
               "SaigaiTask/Map/view/ListPopup.js",
               "SaigaiTask/Map/view/HeaderToolbar.js",
               "SaigaiTask/Map/view/DrawToolbar.js",
               "SaigaiTask/Map/view/GeocodingToolbar.js",
               "SaigaiTask/Map/view/MapPanel.js",
               "SaigaiTask/Map/view/MainPanel.js",
               "SaigaiTask/Map/view/LegendPanel.js",
               "SaigaiTask/Map/view/SpatialSearchForm.js",
               "SaigaiTask/Map/view/SpatialSearchResult.js",
               "SaigaiTask/Map/view/ContentsSearch.js",
               "SaigaiTask/Map/view/PrintWindow.js",
               "SaigaiTask/Map/view/PrintPreviewWindow.js",
               "SaigaiTask/Map/view/SelectFeatureWindow.js",
               "SaigaiTask/Map/view/Measure.js",
               "SaigaiTask/Map/view/RakugakiWindow.js",
               "SaigaiTask/Map/control.js",
               "SaigaiTask/Map/control/PopupManager.js",
               "SaigaiTask/Map/control/PdfControl.js",
               "SaigaiTask/Map/control/SnapControl.js",
               "SaigaiTask/Map/control/SessionLayout.js",
               "SaigaiTask/Map/control/SpatialSearch.js",
               "SaigaiTask/Map/control/SyncControl.js",
               "SaigaiTask/Map/control/SelectFeatureControl.js",
               "SaigaiTask/Map/control/MousePosition60.js",
               "SaigaiTask/Map/control/MgrsControl.js",
               "SaigaiTask/Map/control/RakugakiControl.js",
               "SaigaiTask/Map/control/DrawLayerHistoryControl.js",
               "SaigaiTask/Map/model.js",
               "SaigaiTask/Map/util.js",
               "SaigaiTask/Map/util/CommonUtil.js",
               "SaigaiTask/Map/util/jQueryUtil.js",
               "SaigaiTask/Map/util/GeoUtil.js",
               "OpenLayers/Control/CenterCursor.js",
               "OpenLayers/Control/SelectDragFeature.js",
               "OpenLayers/Control/ScaleBar.js",
               "OpenLayers/Control/DragMarker.js",
               "OpenLayers/Format/KMLStyleUtil.js",
               "OpenLayers/Format/KMLStyle.js",
               "dojo.js",
               "OpenLayers/RendererPatch.js"
    ];

    // use "parser-inserted scripts" for guaranteed execution order
    // http://hsivonen.iki.fi/script-execution/
    var scriptTags = new Array(jsFiles.length);
    var host = SaigaiTask._getScriptLocation() + "lib/";
    for (var i=0, len=jsFiles.length; i<len; i++) {
        scriptTags[i] = "<script src='" + host + jsFiles[i] +
                               "' charset='UTF-8'></script>";
    }
    if (scriptTags.length > 0) {
        document.write(scriptTags.join(""));
    }
})();

SaigaiTask.VERSION_NUMBER="$Revision: 12434 $";
