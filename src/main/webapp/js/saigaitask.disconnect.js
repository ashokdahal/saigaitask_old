/** disconnect パッケージ */
SaigaiTask.disconnect = {};

/**
 * content部分だけ再読み込みします.
 * @param {String} url
 */
SaigaiTask.disconnect.loadContent = function(url) {
	var content = $('#content');
	try {
		// content 部分のレイアウトを破棄
		if(!!SaigaiTask.Layout.contentLayout) SaigaiTask.Layout.contentLayout.destroy();
		SaigaiTask.Layout.contentLayout = null;
		if(!!SaigaiTask.Layout.content) SaigaiTask.Layout.content.html("");
		SaigaiTask.Layout.content = null;

		// 開いているダイアログをすべて閉じる
		$(".ui-dialog-content").each(function(){
			var dialog = $(this);
			dialog.dialog("destroy");
		});

		// すでにリクエストを出していたらアボートする
		if(!!SaigaiTask.disconnect.loadContentXHR) SaigaiTask.disconnect.loadContentXHR.abort();
		// パラメータ
		var params = {};
		content.mask("Loading...");
		// リクエスト開始
		SaigaiTask.disconnect.loadContentXHR = $.ajax({
			url: url,
			success: function(html, dataType) {
				content.html(html);
				SaigaiTask.Layout.content=content;
				SaigaiTask.Layout.initContentLayout();
			},
			error: function(xhr, textStatus, errorThrown) {
				if(textStatus=="error") {
					if(!!xhr.responseText) {
						content.html(xhr.responseText);
						$("input[type='button']", content).hide();
					}
					else {
						content.html(lang.__("An error occurred."));
					}
				}
				else if(textStatus=="timeout") {
					content.html(lang.__("Timeout occurred."));
				}
			},
			complete: function(xhr, textStatus) {
				content.unmask();
				// ボタンの初期化
				btn.init();
			}
		});
	} catch(e) {
		 console.error(e);
		 alert(lang.__("Can not read<!--2-->"));
	}
}

/**
 * Form を送信します.
 * @param {String} form の styleId
 * @param {String} confirmMsg 確認ダイアログのメッセージ
 */
SaigaiTask.disconnect.submitForm = function(formId, confirmMsg, statusMsg) {
	var form = document.getElementById(formId);
	if(confirmMsg === ''){
		$("body").mask(statusMsg);
		form.submit();
	}else{
		Ext.MessageBox.confirm(lang.__("Sending confirmation"), confirmMsg, function(btn) {
			if (btn == "yes") {
				$("body").mask(statusMsg);
				form.submit();
			} else if (btn == "no") {
				// do nothing
			}
		}, window);
	}
	return false;
}


/**
 * 自治体情報のエクスポート。
 */
SaigaiTask.disconnect.exporTrackdata = function(formId) {
	var form = document.getElementById(formId);

	var selectBox = $("#contents_setup_importform_select")[0];
	if(selectBox){
	    var select = document.getElementById('contents_setup_importform_select');
	    var options = document.getElementById('contents_setup_importform_select').options;
	    var value = options.item(select.selectedIndex).value;
	    $("#exportLocalgovinfoid").val(value);
	}
	var msg = lang.__("Are you sure to export following data?");
	msg += "<br/>" + lang.__("･Disaster data");
	msg += "<br/>" + lang.__("･Disaster map");

	Ext.MessageBox.confirm(lang.__("Export"), msg, function(btn) {
		if (btn == "yes") {
			form.submit();
		} else if (btn == "no") {
			// do nothing
		}
	}, window);


}
//SaigaiTask.disconnect.exporTrackdata = function(localgovinfoid,token) {
//	SaigaiTask.disconnect.exporTrackdata = function(localgovinfoid,token) {
//	if(localgovinfoid == 0){
//	    var select = document.getElementById('contents_setup_importform_select');
//	    var options = document.getElementById('contents_setup_importform_select').options;
//	    var value = options.item(select.selectedIndex).value;
//	    localgovinfoid = value;
//	}
//
//	var msg = lang.__("Are you sure to export following data?");
//	msg += "<br/>" + lang.__("･Disaster data");
//	msg += "<br/>" + lang.__("･Disaster map");
//
//	Ext.MessageBox.confirm(lang.__("Export"), msg, function(btn) {
//		if (btn == "yes") {
//			location.href=SaigaiTask.contextPath+"/admin/disconnect/export/download?localgovinfoid="+localgovinfoid+"&"+token;
//		} else if (btn == "no") {
//			// do nothing
//		}
//	}, window);
//}


/**
 * 通信途絶ツールトップに戻る
 * @param {Number} localgovinfoid 自治体ID
 */
SaigaiTask.disconnect.backToTop = function(localgovinfoid) {

	location.href=SaigaiTask.contextPath+"/admin/disconnect/";
}


/** RasterDataDownload パッケージ */
SaigaiTask.disconnect.RasterDataDownload = {};

/**
 * ダウンロード範囲指定用の地図ウインドウを開く
 */
SaigaiTask.disconnect.RasterDataDownload.openMapWindow = function() {
	var url = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/showMap";
	window.open(url, "RasterDataDownload-Map-Window", "width=640,height=480,scrollbars=yes,resizable=yes,status=yes");
}

/**
 * 表示する地図の初期化
 */
SaigaiTask.disconnect.RasterDataDownload.loadMap = function(divId) {
	var projection3857 = new OpenLayers.Projection("EPSG:3857");
	var projection4326 =  new OpenLayers.Projection("EPSG:4326");

    var search = new SaigaiTask.disconnect.RasterDataDownload.SearchBox({
        afterSearchBox: function(b) {
        	SaigaiTask.disconnect.RasterDataDownload.addPolygonFromBounds(b, this.map);
            }
        });

    var map = new OpenLayers.Map({
	  div: divId,
	  projection: projection3857,
	  displayProjection: projection4326,
    controls: [
               new OpenLayers.Control.Navigation({
                   dragPanOptions: {
                       enableKinetic: true
                   },
                   zoomWheelEnabled:true
               }),
               new OpenLayers.Control.Attribution(),
               search,
               new OpenLayers.Control.Zoom()
           ]
	});

	map.addLayer(new OpenLayers.Layer.XYZ(lang.__("Standard map"),
		"http://cyberjapandata.gsi.go.jp/xyz/std/${z}/${x}/${y}.png", {
		attribution: "<a href='http://www.gsi.go.jp/kikakuchousei/kikakuchousei40182.html' target='_blank'>" + lang.__("Geographic Survey Institute") + "</a>",
		maxZoomLevel: 17
	}));

	map.setCenter(new OpenLayers.LonLat(138.7313889, 35.3622222)
		.transform(projection4326, projection3857), 5);

	return map;
}

/**
 * 地図の矩形選択コントロールオブジェクト
 */
SaigaiTask.disconnect.RasterDataDownload.SearchBox = OpenLayers.Class(OpenLayers.Control, {
    type: OpenLayers.Control.TYPE_TOOL,
    out: false,
    keyMask: null,
    alwaysSearch: false,
    draw: function() {
        this.handler = new OpenLayers.Handler.Box(this,{done: this.searchBox}, {keyMask: this.keyMask} );
        this.events.on({
            activate: this.iconOn,
            deactivate: this.iconOff
        });
    },
    searchBox: function (position) {
        if (position instanceof OpenLayers.Bounds) {
            var bounds;
            if (!this.out) {
                var minXY = this.map.getLonLatFromPixel({
                    x: position.left,
                    y: position.bottom
                });
                var maxXY = this.map.getLonLatFromPixel({
                    x: position.right,
                    y: position.top
                });
                bounds = new OpenLayers.Bounds(minXY.lon, minXY.lat,
                                               maxXY.lon, maxXY.lat);
            } else {
                var pixWidth = Math.abs(position.right-position.left);
                var pixHeight = Math.abs(position.top-position.bottom);
                var searchFactor = Math.min((this.map.size.h / pixHeight),
                    (this.map.size.w / pixWidth));
                var extent = this.map.getExtent();
                var center = this.map.getLonLatFromPixel(
                    position.getCenterPixel());
                var xmin = center.lon - (extent.getWidth()/2)*searchFactor;
                var xmax = center.lon + (extent.getWidth()/2)*searchFactor;
                var ymin = center.lat - (extent.getHeight()/2)*searchFactor;
                var ymax = center.lat + (extent.getHeight()/2)*searchFactor;
                bounds = new OpenLayers.Bounds(xmin, ymin, xmax, ymax);
            }
			var org = bounds.clone();
			this.afterSearchBox(org);

        } else { // it's a pixel
        }
    },
	beforeSearchBox: function() {},
	afterSearchBox: function() {},
	iconOn: function(evt) {},
	iconOff: function(evt) {},

    CLASS_NAME: "SaigaiTask.disconnect.RasterDataDownload.SearchBox"
});
SaigaiTask.disconnect.RasterDataDownload.initVectorLayer = function(map) {
    var vector = SaigaiTask.disconnect.RasterDataDownload.getVectorLayer(map);
    if (!vector || vector.length == 0) {
        vector = new OpenLayers.Layer.Vector("vector",{
              isBaseLayer: false,
              wrapDataLine: false,
              vibility: true
            }
        );
        map.addLayer(vector);
    }
    return vector;
};
SaigaiTask.disconnect.RasterDataDownload.addPolygonFromRect = function(left, bottom, right, top, map) {
    var b = new OpenLayers.Bounds(left, bottom, right, top);
    b = b.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:3857")),
    SaigaiTask.disconnect.RasterDataDownload.addPolygonFromBounds(b, map);
};
SaigaiTask.disconnect.RasterDataDownload.addPolygonFromBounds = function(bounds, map) {
	SaigaiTask.disconnect.RasterDataDownload.addPolygon(new OpenLayers.Feature.Vector(bounds.toGeometry(), "polygon"), map);
};
SaigaiTask.disconnect.RasterDataDownload.addPolygon = function(feature, map) {
	SaigaiTask.disconnect.RasterDataDownload.initVectorLayer(map);
	SaigaiTask.disconnect.RasterDataDownload.removePolygon(map);
    var vector = SaigaiTask.disconnect.RasterDataDownload.getVectorLayer(map);
    if (vector && vector.length == 1) {
        vector[0].addFeatures([feature]);
    }
};
SaigaiTask.disconnect.RasterDataDownload.removePolygon = function(map) {
    var vector = SaigaiTask.disconnect.RasterDataDownload.getVectorLayer(map);
    if (vector && vector.length == 1) {
        vector[0].removeAllFeatures();
    }
};
SaigaiTask.disconnect.RasterDataDownload.getSearchBoxControl = function(map) {
    return map.getControlsBy("CLASS_NAME", "SaigaiTask.disconnect.RasterDataDownload.SearchBox");
};
SaigaiTask.disconnect.RasterDataDownload.getVectorLayer = function(map) {
    return map.getLayersBy("CLASS_NAME", "OpenLayers.Layer.Vector");
};


/**
 * サーバに、ダウンロードするラスターデータのファイルサイズと所要時間の概算を算出させる
 */
SaigaiTask.disconnect.RasterDataDownload.calc = function(token) {
	var url = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/calc";

	var selectMapUrl = $("#contentsRasterdatadownloadFormSelectMapUrl").val();
	var startLat = $("#contentsRasterdatadownloadFormStartLat").val();
	var startLon = $("#contentsRasterdatadownloadFormStartLon").val();
	var endLat = $("#contentsRasterdatadownloadFormEndLat").val();
	var endLon = $("#contentsRasterdatadownloadFormEndLon").val();
	var startZoomLevel = $("#contentsRasterdatadownloadFormStartZoomLevel").val();
	var endZoomLevel = $("#contentsRasterdatadownloadFormEndZoomLevel").val();

	// 範囲情報をサーバに送信し、概数を返してもらう
	$.ajax({
		dataType: "json",
		type: "POST",
//		headers: {"X-CSRF-Token":token},
		cache: false,
		url: url,
		data: {
			selectMapUrl:selectMapUrl,
			startLat:startLat,
			startLon:startLon,
			endLat:endLat,
			endLon:endLon,
			startZoomLevel:startZoomLevel,
			endZoomLevel:endZoomLevel
		},
		success: function(response) {
			if(response.errors){
				var errorHtml = "";
				for(var i = 0; i < response.errors.length; i++){
					errorHtml += response.errors[i] + "<BR>";
				}
				$("#contentsRasterdatadownloadFormAlertMessage").html(errorHtml);
			}
			else{
				$("#contentsRasterdatadownloadFormAlertMessage").html("");
			}
			$("#contentsRasterdatadownloadFormDownloadLink").html("");
			$("#contentsRasterdatadownloadFormDataSize").html(response.datasize);
			$("#contentsRasterdatadownloadFormDownloadTime").html(response.downloadtime);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
//			alert("error : 登録できませんでした");
		}
	});

	// 画面にセット
}

/**
 * 画面上のセレクトボックスで選択した自治体情報でフォームの内容を更新
 */
SaigaiTask.disconnect.RasterDataDownload.changeLocolgov = function() {

	var url = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/changeLocalgov";
	var selectLocalgov = $("#contentsRasterdatadownloadFormSelectLocalgov").val();

	// 選択した自治体を送信し、結果で画面を再描画
	$.ajax({
		dataType: "json",
		type: "POST",
		cache: false,
		url: url,
		data: {
			selectLocalgov:selectLocalgov
		},
		success: function(response) {
			$("#contentsRasterdatadownloadFormSystemname").html(response.systemname);
			$("#contentsRasterdatadownloadFormZoomLevelLabel").html(response.startZoomLevel +" - "+ response.endZoomLevel);
			$("#contentsRasterdatadownloadFormStartZoomLevel").val(response.startZoomLevel);
			$("#contentsRasterdatadownloadFormEndZoomLevel").val(response.endZoomLevel);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
		}
	});
}

/**
 * サーバに地図サーバへのラスターデータダウンロードを依頼する
 */
SaigaiTask.disconnect.RasterDataDownload.download = function(token) {
	var url = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/download";
	var selectLocalgov = $("#contentsRasterdatadownloadFormSelectLocalgov").val();
	var selectMapUrl = $("#contentsRasterdatadownloadFormSelectMapUrl").val();
	var startLat = $("#contentsRasterdatadownloadFormStartLat").val();
	var startLon = $("#contentsRasterdatadownloadFormStartLon").val();
	var endLat = $("#contentsRasterdatadownloadFormEndLat").val();
	var endLon = $("#contentsRasterdatadownloadFormEndLon").val();
	var startZoomLevel = $("#contentsRasterdatadownloadFormStartZoomLevel").val();
	var endZoomLevel = $("#contentsRasterdatadownloadFormEndZoomLevel").val();

	$("#contentsRasterdatadownloadFormDownloadLink").html("");
	$("body").mask(lang.__("Raster data downloading.."));
	// 範囲情報をサーバに送信し、概数を返してもらう
	$.ajax({
		dataType: "json",
		type: "POST",
		headers: {"X-CSRF-Token":token},
		cache: false,
		url: url,
		data: {
			selectLocalgov:selectLocalgov,
			selectMapUrl:selectMapUrl,
			startLat:startLat,
			startLon:startLon,
			endLat:endLat,
			endLon:endLon,
			startZoomLevel:startZoomLevel,
			endZoomLevel:endZoomLevel
		},
		success: function(response) {
			/*
			if(response.errors){
				var errorHtml = "";
				for(var i = 0; i < response.errors.length; i++){
					errorHtml += response.errors[i] + "<BR>";
				}
				$("#contentsRasterdatadownloadFormAlertMessage").html(errorHtml);
			}else{
				$("#contentsRasterdatadownloadFormAlertMessage").html("");
				$("#contentsRasterdatadownloadFormDataSize").html(response.datasize);
				$("#contentsRasterdatadownloadFormDownloadTime").html(response.downloadtime);

				var downLoadUrl = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/downloadfile?zipfile=";
				downLoadUrl += response.zipfilename;
				var aTagStr = "<a id='contentsRasterdatadownloadFormDownloadLinkHref' href='"+downLoadUrl+"' type='application/x-compress' onclick='addtokenhref();'>" + response.zipfilename +"</a>";
				$("#contentsRasterdatadownloadFormDownloadLink").html(lang.__("Ready to download. Click following link and download.") + "<BR> → " + aTagStr + "<BR>" + lang.__("Click \"Run\" button in case that download again after clicking link."));
			}
			*/
			// 1秒まってからリロード
			setTimeout(function(){
				var url = SaigaiTask.contextPath+"/admin/disconnect/rasterDataDownload/content";
				SaigaiTask.disconnect.loadContent(url);
			}, 1000);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
		},
		complete: function(xhr, textStatus) {
			$("body").unmask();
		}
	});

};


SaigaiTask.disconnect.RasterDataUpload = {};

/**
 * form の内容をアップロードする
 */
SaigaiTask.disconnect.RasterDataUpload.uploadForm = function(formId, url, token, callback) {
	var formData = new FormData($('#' + formId)[0]);
	$("#contentsRasterdatauploadFormAlertMessage").empty();
	$("#contentsRasterdatauploadFormSuccessMessage").empty();
	$("body").mask(lang.__("Uploading file.."));

	$.ajax({
		dataType: "text",
		type: "POST",
		cache: false,
		headers: { "X-CSRF-Token":token },
		url: url,
		data: formData,
        contentType: false,
        processData: false,
		success: function(data) {
			data = JSON.parse(data);
			$("body").unmask();
			if (data.error) {
				$("#contentsRasterdatauploadFormAlertMessage").html(lang.__('Failed to upload file.') + '<br/>' + data.error);
			}
			else {
				$("#contentsRasterdatauploadFormSuccessMessage").html(lang.__('Succeed at upload file.'));
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			$("body").unmask();
			$("#contentsRasterdatauploadFormAlertMessage").html(lang.__('Failed to upload file.') + '<br/>' + textStatus + "/" + errorThrown);
		}
	});
};

SaigaiTask.disconnect.datasync = {};

/**
 * form の内容をアップロードする
 */
SaigaiTask.disconnect.datasync.uploadForm = function(formId, url, token, callback) {
	var formData = new FormData($('#' + formId)[0]);
	$("body").mask(lang.__("Check.."));

	$.ajax({
		dataType: "text",
		type: "POST",
		cache: false,
		headers: { "X-CSRF-Token":token },
		url: url,
		data: formData,
        contentType: false,
        processData: false,
		success: function(data) {
			data = JSON.parse(data);
			$("body").unmask();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			$("body").unmask();
			$("#contentsRasterdatauploadFormAlertMessage").html(lang.__('Failed to upload file.') + '<br/>' + textStatus + "/" + errorThrown);
		}
	});
};

