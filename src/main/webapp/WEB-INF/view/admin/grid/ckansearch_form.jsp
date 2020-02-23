<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%@include file="../../common/jsp_lang.jsp" %>
<%-- 地図画面からも利用します. --%>
<%-- 地図画面からも利用します. --%>

ClearingHouseLayerNameElm 	= '';
ClearingHouseLayerIdElm 	= '';
ClearingHouseWmsCapsUrlElm 	= '';
ClearingHouseWmsUrlElm 		= '';
ClearingHouseWmsLegendUrlElm = '';
ClearingHouseWmsFeatureUrlElm= '';
ClearingHouseMapTypeElm		= '';
ClearingHouseAttributionElm	= '';
ClearingHouseLayerOpacElm	= '';
ClearingHouseWmsFormatElm	= '';
ClearingHouseLayerDescElm	= '';
ClearingHouseLegendHeightElm= '';
ClearingHouseSelectedLayerIdsElm= '';
ClearingHouseSelectedLayerNamesElm= '';

CkanGeneralError	= "<%=lang.__("An error occurred.")%>";
CkanItemPerPage = 15;

// ボタンデフォルト
Ckan_insertMap = true;
Ckan_addMapPage = false;
Ckan_addAllMapPage = false;


// 詳細表示
function showMapDetail(data) {
	//検索条件をサーバへ送信して検索結果を取得し、表示する。
/*
	$.ajax({
		type: "POST",
		url: SaigaiTask.contextPath + '/admin/clearinghouse/getMapDetail',
		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		data: {"metaDataId": metaDataId, "training": !!SaigaiTask.Page && !!SaigaiTask.Page.training},
		//dataType: "text",
		success: function(data) {
*/
			var html = "";
			var abstract = "";
			if (data.notes)
				abstract = br2breakline(data.notes);

			html += "<a id='linkReturn' href='#' style='float:right;'><%=lang.__("Return to list")%></a><br/>";
			html += "<%=lang.__("Map info")%> <br/>";
			html += "<table class='wmsCapsFormTable' width='100%' cellspacing=1 cellpadding=1>";
			html += "<tr width='120px'>";
			html += "<th><%=lang.__("Map name")%></th>";
			html += "<td><input type='input' class='mapinputbox' id='mapTitle' value='" + data.title + "' style='width:450px;'></td>";
			html += "</tr>";

			html += "<tr>";
			html += "<th><%=lang.__("Explanation of map")%> </th>";
			html += "<td><textarea id='mapAbstract' class='mapinputbox' style='width:450px;height:80px;'>" + abstract + "</textarea></td>";
			html += "</tr>";

			html += "<tr>";
			html += "<th><%=lang.__("Thumbnail")%> </th>";
			html += "<td>";
			if (data.thumbnail_url && data.thumbnail_url != ""){
				html += "<img src='" + data.thumbnail_url + "' width='300' class='detail_preview'>";
			}else{
				html += "<img src='" + SaigaiTask.contextPath + "/images/common/noimage.gif' width='128' class='detail_preview'>";
			}
			html += "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<th><%=lang.__("Organization")%> </th>";
			html += "<td>" + (!!data["organization"] ? data.organization.title : "") + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<th><%=lang.__("Update date and time")%> </th>";
			html += "<td>" + new Date(data.metadata_modified).toLocaleString() + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<th><%=lang.__("Resource")%> </th>";
			html += "<td><table style='border-spacing:4px 0px; border-collapse: separate; border:1px solid #808080;'><tr>";
			var checked = ' checked="checked"';
			for (var j = 0; j < data.resources.length; j++) {
				if (data.wmsCount > 1) {
					html += '<td>'
					if (data.resources[j].format == "OGC WMS") {
						html += '<input type="radio" class="radioWmsUrl" name="radio_wms_url" value="'
						html += data.resources[j].url;
						html += '"';
						html += ' data-ckanresourceid="' + data.resources[j].id + '"'
						html += checked;
						html += '>';
						checked = '';
					}
					html += '</td>'
				}
				html += "<td><a id='showResource-" + j + "' href='javascript:void(0)'>" + data.resources[j].name;
				html += "</td><td>" + data.resources[j].description;
				html += "</td></tr>";
			}
			html += "</table></td>";
			html += "</tr>";

			if(Ckan_addMapPage) {
			html += "<tr>";
			html += "<th><%=lang.__("Transmittance")%> </th>";
			html += "<td><select id='layeropacity'>";
			html += "<option value='1.0'><%=lang.__("0%(nontransparent)")%></option>";
			html += "<option value='0.9'>10%</option>";
			html += "<option value='0.8'>20%</option>";
			html += "<option value='0.7'>30%</option>";
			html += "<option value='0.6'>40%</option>";
			html += "<option value='0.5' selected>50%</option>";
			html += "<option value='0.4'>60%</option>";
			html += "<option value='0.3'>70%</option>";
			html += "<option value='0.2'>80%</option>";
			html += "<option value='0.1'>90%</option>";
			html += "</select></td>";
			}

			html += "</table>";

			if (data.wmsCount > 0) {
				if(Ckan_insertMap) {
					html += 	"<a id='insertMap' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Add map")%>";
					html += 	"<span class='ui-icon ui-icon-disk'></span></a>";
				}
				if(Ckan_addMapPage) {
					html += 	"<a id='addMapPage' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Add this map")%>";
					html += 	"<span class='ui-icon ui-icon-disk'></span></a>";
				}
				if(Ckan_addAllMapPage) {
					html += 	"<a id='addAllMapPage' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Add to all map")%>";
					html += 	"<span class='ui-icon ui-icon-disk'></span></a>";
				}
			}

			document.getElementById("ckanDetail").innerHTML = html;
			showCkanDetailPane();

			$("#linkReturn").click(function() {showCkanListPane();});

			$("#insertMap").click(function() {insertMap(data, document.getElementById("mapTitle").value, document.getElementById("mapAbstract").value);});
			$("#addMapPage").click(function() {addMapPage(data, document.getElementById("mapTitle").value, document.getElementById("mapAbstract").value, "add");});
			$("#addAllMapPage").click(function() {addMapPage(data, document.getElementById("mapTitle").value, document.getElementById("mapAbstract").value, "add-all");});
			for (var j = 0; j < data.resources.length; j++) {
				(function(data) {
					$("#showResource-" + j).click(function() {showResource(data);});
				})(data.resources[j]);
			}
			$(".radioWmsUrl").change(function() { data.wmsUrl = $(this).val(); });
/*
		},
		error: function(request, text, error) {
			alert(ClearingHouseGeneralError+":"+text);
		}
	});
*/
}

function insertMap(data, title, abstract) {
	var fileIdentifier = data.name;

	if (ClearingHouseLayerNameElm)
		$("#" + ClearingHouseLayerNameElm).val(title);
	if (ClearingHouseLayerIdElm)
		$("#" + ClearingHouseLayerIdElm).val(fileIdentifier);
	if (ClearingHouseWmsCapsUrlElm)
		$("#" + ClearingHouseWmsCapsUrlElm).val(data.wmsUrl);
	if (ClearingHouseMapTypeElm) {
		var radios = document.getElementsByName(ClearingHouseMapTypeElm);
		if (radios)
			radios[0].checked = true;
	}
	closeCkanDialog();
}

/**
 * 地図画面に地図を追加する.
 */
function addMapPage(data, title, abstract, state) {
	var fileIdentifier = data.fileIdentifier;
	var layeropacity = document.getElementById("layeropacity").value;
	var params = [];
	var hasAdd = true;
		var metadataid = data.name;
		var updtm = data.metadata_modified;
		params.push({
			/** メニューID */
			menuinfoid: SaigaiTask.Page.menuInfo.id,
			/** メタデータID */
			metadataid: metadataid,
			/** データに付けられた名前 */
			name: title,
			/** フィルタID(外部地図のフィルタは未実装なので使用しない) */
			//filterid: null,
			/** メタデータ更新日時 */
			updateTime: updtm,
			/** 初期表示フラグ */
			visible: true,
			/** 凡例折りたたみ */
			closed: false,
			/** 追加状態 */
			state: state,
			/** 透明度 */
			layeropacity: layeropacity,
			/** 表示順 */
			// サーバ側で自動付与
			//disporder: 0
			/** WMS URL */
			wmscapsurl: data.wmsUrl,
			// 複数リソースの場合のリソースID
			ckanresourceid: $(".radioWmsUrl:checked").attr("data-ckanresourceid")
		});
		// 複数リソースの場合は WMS URL を保存
		// リソースが１つの場合はメタデータIDだけ保存
		if(data.wmsCount==1) delete params[0].wmscapsurl;

	// サーバのセッションに保存
	var dialog = $("body"); // dialog全体の要素
	dialog.mask("<%=lang.__("Now saving..")%>");
	jQuery.ajax(SaigaiTask.contextPath+"/page/map/sessionMetadata", {
		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		async: false,
		type: "post",
		//dataType: "json",
		data: {
			records: JSON.stringify(params), "training": !!SaigaiTask.Page && !!SaigaiTask.Page.training
		},
		success: function(data, textStatus, jqXHR) {
			// 追加処理の場合はページ再読み込み
			if(hasAdd) {
				closeCkanDialog()
				dialog.unmask();
				dialog.mask("<%=lang.__("Now reloading..")%>");
				SaigaiTask.PageURL.move();
			}
			else {
				// 閉じる
				dialog.unmask();
				//mdlist.dialog("close");
			}
		},
		error: function(jqXHR, status, errorThrown) {
			alert("<%=lang.__("An error occurred. (")%>"+status+")"+":"+text+"\n"+errorThrown);
			// 閉じる
			dialog.unmask();
			//mdlist.dialog("close");
		},
		complete: function(jqXHR, status) {
		}
	});
}

function fillZero(str, length) {
	while (str.toString().length < length)
		str = "0".concat(str);
	return str;
}


/**
 * 地図画面に地図を追加する.
 */
function insertWms(data, form) {
	var dialog = $("body"); // dialog全体の要素
	dialog.mask("<%=lang.__("Now saving..")%>");

	//処理
	var i;

	//秒までの日時を生成
	var d = new Date();
	var time = d.getFullYear().toString() + fillZero(d.getMonth() + 1, 2) + fillZero(d.getDate(), 2) + fillZero(d.getHours(), 2) + fillZero(d.getMinutes(), 2) + fillZero(d.getSeconds(), 2);

	var fileIdentifier = "WMSCapabilities_" + time;
	var wmsCapsUrl = data.wmsCapsUrl;
	var wmsUrl = data.wmsUrl;
	var wmsLegendUrl = data.wmsLegendUrl;
	var wmsFeatureUrl = data.wmsFeatureUrl;
	var mapTitle = form.mapTitle.value;
	var attribution = form.ckan_attr.value;
	var opacity = parseFloat(form.opacity.value);
	var wmsFormat = form.format.value;
	var wmsLayerDescUrl = form.mapAbstract.value;
	var legendHeight = 0;
	if (form.legheight.checked) {
		legendHeight = form.legheight.value;
	}

	var selectedLayerIds = "";
	var selectedLayerNames = "";
	for (i = 0; i < data.layers.length; i++) {
		data.layers[i].isGroup = parseInt(data.layers[i].isGroup);
		data.layers[i].level = parseInt(data.layers[i].level);

		if (!data.layers[i].isGroup && data.layers[i].level > 0) {
			var chkid = 'chk_' + data.layers[i].layerId;
			var textid = 'layername_' + data.layers[i].layerId;
			//alert("chkid:" + chkid + ", textid:" + textid);
			if (document.getElementById(chkid).checked) {
				if (selectedLayerIds != "")
					selectedLayerIds += "$,$";
				if (selectedLayerNames != "")
					selectedLayerNames += "$,$";
				selectedLayerIds += data.layers[i].name;
				selectedLayerNames += document.getElementById(textid).value;
			}
		}
	}

	if (ClearingHouseLayerNameElm)
		$("#" + ClearingHouseLayerNameElm).val(mapTitle);
	if (ClearingHouseLayerIdElm)
		$("#" + ClearingHouseLayerIdElm).val(fileIdentifier);
	if (ClearingHouseWmsCapsUrlElm)
		$("#" + ClearingHouseWmsCapsUrlElm).val(wmsCapsUrl);
	if (ClearingHouseWmsUrlElm)
		$("#" + ClearingHouseWmsUrlElm).val(wmsUrl);
	if (ClearingHouseWmsLegendUrlElm)
		$("#" + ClearingHouseWmsLegendUrlElm).val(wmsLegendUrl);
	if (ClearingHouseWmsFeatureUrlElm)
		$("#" + ClearingHouseWmsFeatureUrlElm).val(wmsFeatureUrl);
	if (ClearingHouseMapTypeElm) {
		var radios = document.getElementsByName(ClearingHouseMapTypeElm);
		if (radios)
			radios[1].checked = true;
	}


	if (ClearingHouseAttributionElm)
		$("#" + ClearingHouseAttributionElm).val(attribution);
	if (ClearingHouseLayerOpacElm)
		$("#" + ClearingHouseLayerOpacElm).val(opacity);
	//alert("wmsFormat:" + wmsFormat);
	if (ClearingHouseWmsFormatElm)
		$("#" + ClearingHouseWmsFormatElm).val(wmsFormat);
	if (ClearingHouseLayerDescElm)
		$("#" + ClearingHouseLayerDescElm).val(wmsLayerDescUrl);
	if (ClearingHouseLegendHeightElm)
		$("#" + ClearingHouseLegendHeightElm).val(legendHeight);
	if (ClearingHouseSelectedLayerIdsElm)
		$("#" + ClearingHouseSelectedLayerIdsElm).val(selectedLayerIds);
	if (ClearingHouseSelectedLayerNamesElm)
		$("#" + ClearingHouseSelectedLayerNamesElm).val(selectedLayerNames);




	closeCkanDialog()
	dialog.unmask();
	return 1;
}

// CKAN検索
function searchCkan(startPosition) {
	if (!startPosition)
		startPosition = 1;

	var keyword = document.getElementById("keyword").value;
	var orderby = document.getElementById("orderby").value;
	var maxRecords = CkanItemPerPage;

	// 利用者画面からの呼び出し時はWMSの直接検索はしない。
	var isMapPage = false;
	try{ isMapPage = SaigaiTask.PageURL.params.pagetype=="map" } catch(e){};
	var isWmsSearch = !isMapPage;

	if (keyword.match(/^http.?:/) && isWmsSearch) {
		document.getElementById("ckanList").innerHTML = keyword;

		//検索条件をサーバへ送信して検索結果を取得し、表示する。
		$.ajax({
			type: "POST",
			url: SaigaiTask.contextPath + '/admin/clearinghouse/getMapDetailByWMSCapabilities',
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			data: {"wmsCapsUrl": keyword, "training": !!SaigaiTask.Page && !!SaigaiTask.Page.training},
			success: function(data) {
				var i;
				var html = "";
				var abstract = "";
				if (data.wmsAbstract)
					abstract = br2breakline(data.wmsAbstract);

				html += "<a id='linkDetailReturn' href='#' style='float:right;'><%=lang.__("Return to list")%></a><br/>";
				html += "<%=lang.__("Map info")%> <br/>";
				html += "<table class='wmsCapsFormTable' width='100%' cellspacing=1 cellpadding=1>";
				html += "<tr width='120px'>";
				html += "<th><%=lang.__("Map name")%></th>";
				html += "<td><input type='input' class='mapinputbox' id='mapTitle' value='" + data.wmsTitle + "' style='width:450px;'></td>";
				html += "</tr>";

				html += "<tr>";
				html += "<th><%=lang.__("Explanation of map")%> </th>";
				html += "<td><textarea id='mapAbstract' class='mapinputbox' style='width:450px;height:80px;'>" + abstract + "</textarea></td>";
				html += "</tr>";

				html += "<tr>";
				html += "<th><%=lang.__("Author info")%></th>";
				html += "<td><input type='text' id='ckan_attr' name='attr' style='width:450px;' value='" + data.attribution + "'></td>";
				html += "</tr>";

				html += "<tr>";
				html += "<th><%=lang.__("Transparency")%></th>";
				html += "<td>";
				html += 	"<input type='radio' name='opacity' value='1.0' style='position:relative;top:2px;' checked='checked'/><%=lang.__("Opacity")%>";
				html += 	"<input type='radio' name='opacity' value='0.75' style='position:relative;top:2px;'/><%=lang.__("Dense")%>";
				html += 	"<input type='radio' name='opacity' value='0.5' style='position:relative;top:2px;'/><%=lang.__("Translucent")%>";
				html += 	"<input type='radio' name='opacity' value='0.25' style='position:relative;top:2px;'/><%=lang.__("Light")%>";
				html += "</td>";
				html += "</tr>";

				html += "<tr>";
				html += "<th><%=lang.__("Map image format")%></th>";
				html += "<td>";
				html += "<select name='format' id='format'>";
				for (i = 0; i < data.formats.length; i++)
					html += "<option value='" + data.formats[i] + "'>" + data.formats[i] + "</option>";
				html += "</select>";
				html += "</td>";
				html += "</tr>";

				html += "<tr>";
				html += "<th><%=lang.__("Legend image")%></th>";
				html += "<td>";
				html += "<input type='checkbox' id='ckan_legheight' name='legheight' value='18' style='position:relative;top:2px;'/><%=lang.__("Display narrow (separate coloring display, etc.)")%>";
				html += "</td>";
				html += "</tr>";

				html += "</table>";


				html += "<%=lang.__("Display item")%> <br/>";
				html += "<table class='wmsCapsFormTable' width='100%' cellspacing=1 cellpadding=1>";
				html += "<tr>";
				html += "<td>";
				html += "<input type='checkbox' id='ckanCheckAll' checked='checked'/>";
				html += "</td>";
				html += "<td colspan='2'><%=lang.__("Select all")%>";
				html += "</td>";
				html += "</tr>";

				for (i = 0; i < data.layers.length; i++) {
					data.layers[i].isGroup = parseInt(data.layers[i].isGroup);
					data.layers[i].level = parseInt(data.layers[i].level);
					if (!data.layers[i].isGroup && data.layers[i].level > 0) {
						html += "<tr>";
						html += "<td width='20px'>";
						html += "<input type='checkbox' id='chk_" + data.layers[i].layerId + "' name='layers[]' checked='checked' value='" + data.layers[i].layerName + "," + data.layers[i].level + (data.layers[i].isGroup ? ",1" : "") + "'/>";
						html += "</td>";
						html += "<td width='300px'>";
						html += "<input type='text' name='layername_" + data.layers[i].layerId + "' id='layername_" + data.layers[i].layerId + "' value='" + data.layers[i].title + "' style='width:300px;' onclick='this.focus();this.select();'/>";
						if (data.layers[i].minScale)
							html += "<input type='hidden' name='minscale_" + data.layers[i].layerId + "' value='" + data.layers[i].minScale + "'>";
						if (data.layers[i].maxScale)
							html += "<input type='hidden' name='maxscale_" + data.layers[i].layerId + "' value='" + data.layers[i].maxScale + "'>";
						html += "</td>";
						html += "<td align='left'>";
						html += data.layers[i].name;
						html += "</td>";
						html += "</tr>";
					}
				}

				html += "</table>";

				html += 	"<a id='wmsInsertMap' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Add map")%>";
				html += 	"<span class='ui-icon ui-icon-disk'></span></a>";

				document.getElementById("ckanDetail").innerHTML = html;
				showCkanDetailPane();

				$("#linkDetailReturn").click(function() {
					document.getElementById("keyword").value = "";
					searchClearingHouse();
				});

				$("#ckanCheckAll").click(function() {
					var checkboxes = document.getElementsByName('layers[]');
					var bOn = this.checked;
					if (!checkboxes) return;
					var size = checkboxes.length;
					for (var i=0; i<size; i++) {
						checkboxes[i].checked = bOn;
					}
				});

				$("#wmsInsertMap").click(function() {insertWms(data, document.ckanForm);});
			},
			error: function(request, text, error) {
				alert(ClearingHouseGeneralError+":"+text);
			}
		});

	}
	else {
		//検索条件をサーバへ送信して検索結果を取得し、表示する。
		$.ajax({
			type: "POST",
			url: SaigaiTask.contextPath + '/admin/ckan/search',
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			data: {"keyword": keyword, "orderby": orderby, "startPosition": startPosition, "maxRecords": maxRecords, "training": !!SaigaiTask.Page && !!SaigaiTask.Page.training},
			success: function(data) {
				if (!data.success) {
					alert(CkanGeneralError + ":" + JSON.stringify(data.error));
					return;
				}

				var i;
				var start, end, total, prev, next;
				var linkNames = [];
				var dataFromId = {};

				total 	= data.result.count;
				start 	= startPosition;
				end 	= start + data.result.results.length - 1;


				var html = "";
				$('#ckanpaging').html("");

				if (data.result.results.length > 0) {

					var addPaging = function() {
						var html = "";
						html += MessageFormat.format("<%=lang.__("Search result: {0} / {1}")%>", start + " - " + end, data.result.count + "\n");
	
						html += "&nbsp;&nbsp;&nbsp;&nbsp;";
						if (start > 1) {
							prev = start - CkanItemPerPage;
							if (prev < 1) prev = 1;
							html += "<a class='linkPrev' href='#'><%=lang.__("Forward")%></a>";
							linkNames.push("linkPrev");
						}
						else
							html += "<span style='color:#888888;'><%=lang.__("Forward")%></span>";
	
						html += "&nbsp;&nbsp;&nbsp;&nbsp;";
	
						if (end < total) {
							next = end + 1;
							html += "<a class='linkNext' href='#'><%=lang.__("Next")%></a>";
							linkNames.push("linkNext");
						}
						else {
							html += "<span style='color:#888888;'><%=lang.__("Next")%></span>";
						}
						$('#ckanpaging').html(html);
					}
					
					addPaging();

					html += "<table class='csw_get_records' width='100%' cellspacing='0' cellpadding='0'>";
					for (i = 0; i < data.result.results.length; i++) {
						dataFromId[data.result.results[i].id] = data.result.results[i];
						linkNames.push("linkDetailImage_" + data.result.results[i].id);
						linkNames.push("linkDetail_" + data.result.results[i].id);
						linkNames.push("linkMeta_" + data.result.results[i].id);

						// サムネイル URL, WMS URL 取得
						var wmsCount = 0;
						for (var j = 0; j < data.result.results[i].resources.length; j++) {
							var resource = data.result.results[i].resources[j];
							if (resource.name == "thumbnail.png")
								data.result.results[i].thumbnail_url = resource.url;
							else if (resource.format == "OGC WMS") {
								if (wmsCount == 0)
								   data.result.results[i].wmsUrl = resource.url;
								wmsCount++;
							}
						}
						data.result.results[i].wmsCount = wmsCount;

						var abstract = "";
						if (data.result.results[i].notes)
							abstract = data.result.results[i].notes;

						html += "<tr>";
						html += 	"<td>";
						if(data.result.results[i].thumbnail_url){
							html += 		"<img id='linkDetailImage_" + data.result.results[i].id + "' class='preview' width='128' src='" + data.result.results[i].thumbnail_url + "'>";
						}else{
							html += 		"<img id='linkDetailImage_" + data.result.results[i].id + "' class='preview' width='128' src='"+SaigaiTask.contextPath+"/images/common/noimage.gif'>";
						}
						if(data.result.results[i].originalSource){ /*???*/
							html += 		"<span>"+data.result.results[i].originalSource+"</span>";
						}

						html += 	"</td>";

						html += 	"<td>";
						html += 		"<table class='csw_records' width='100%'>";
						html += 			"<tr>";
						html += 				"<td class='title'>";
						html += 					"<a id='linkDetail_" + data.result.results[i].id + "' href='#'>" + data.result.results[i].title + "</a>";
						html += 					"<br/>" + new Date(data.result.results[i].metadata_modified).toLocaleString();
						html += 					"<br/>" + "<%=lang.__("Organization")%>"+" : "+data.result.results[i].organization.title;
						html += 				"</td>";
						html += 			"<tr>";
						html += 			"</tr>";
						html += 				"<td class='abstract'>";
						html += 					abstract;
						html += 				"</td>";
						html += 			"</tr>";

						//html += 			"</tr>";
						//html += 				"<td>";
						//html += 					"<a id='linkMeta_" + data.record[i].metadataId + "' href='javascript:showMetadata(" + data.record[i].metadataId + ")'>メタデータ表示</a>";
						//html += 				"</td>";
						//html += 			"</tr>";

						html += 		"</table>";
						html += 	"</td>";
						html += "</tr>";
					}

					html += "</table>";

					//addPaging();
				}
				else {
					html = "<%=lang.__("Corresponding map is not found.")%>";
				}

				document.getElementById("ckanList").innerHTML = html;
				showCkanListPane();

				for (i = 0; i < linkNames.length; i++) {
					var linkName = linkNames[i];
					if (linkName == "linkPrev")
						$(".linkPrev").click(function() {searchCkan(prev);});
					else if (linkName == "linkNext")
						$(".linkNext").click(function() {searchCkan(next);});
					else if (linkName.indexOf("linkDetail_") >= 0 || linkName.indexOf("linkDetailImage_") >= 0) {
						// クリアリングハウスでカスケード検索時は dc:identifierの値が 1:176322 のように「サーバID:identifier」の形式になるが、
						// jQueryのセレクタでコロンはfilterで扱われてしまうので、コロンをエスケープする
						$("#" + linkName.replace(/:/g,'\\:')).click(function() {var tokens = this.id.split("_"); showMapDetail(dataFromId[tokens[1]]);});
					}
					else if (linkName.indexOf("linkMeta_") >= 0) {
						$("#" + linkName).click(function() {var tokens = this.id.split("_"); showMetadata(tokens[1]);});
					}

				}
			},
			error: function(request, text, error) {
				alert(CkanGeneralError + ":" + text);
			}
		});
	}
}

//ダイヤログを閉じる
function closeCkanDialog() {
	$.jgrid.hideModal('#ckanSearchModalDialog',{jqm:true});
}

//画面を切り換える
function showCkanListPane() {
	document.getElementById("ckanList").style.display 	= "block";
	document.getElementById("ckanDetail").style.display = "none";
	document.getElementById("ckanResource").style.display = "none";
}

function showCkanDetailPane() {
	document.getElementById("ckanDetail").style.display = "block";
	document.getElementById("ckanList").style.display 	= "none";
	document.getElementById("ckanResource").style.display = "none";
}

function showCkanResourcePane() {
	document.getElementById("ckanResource").style.display = "block";
	document.getElementById("ckanDetail").style.display = "none";
}


//クリアリングハウス検索ダイアログで地図を選択
function outermap_select(layerNameElm, layerIdElm, layerWmsCapsUrlElm, layerWmsUrlElm, layerWmsLegendUrlElm, layerWmsFeatureUrlElm, radioMapTypeElm, attributionElm, layerOpacityElm, wmsFormatElm, layerDescriptionElm, legendHeightElm, selectedLayerIdsElm, selectedLayerNamesElm) {
	ClearingHouseLayerNameElm 	= layerNameElm;
	ClearingHouseLayerIdElm 	= layerIdElm;
	ClearingHouseWmsCapsUrlElm	= layerWmsCapsUrlElm;
	ClearingHouseWmsUrlElm		= layerWmsUrlElm;
	ClearingHouseWmsLegendUrlElm= layerWmsLegendUrlElm;
	ClearingHouseWmsFeatureUrlElm= layerWmsFeatureUrlElm;
	ClearingHouseMapTypeElm		= radioMapTypeElm;
	ClearingHouseAttributionElm	= attributionElm;
	ClearingHouseLayerOpacElm	= layerOpacityElm;
	ClearingHouseWmsFormatElm	= wmsFormatElm;
	ClearingHouseLayerDescElm	= layerDescriptionElm;
	ClearingHouseLegendHeightElm= legendHeightElm;
	ClearingHouseSelectedLayerIdsElm = selectedLayerIdsElm;
	ClearingHouseSelectedLayerNamesElm = selectedLayerNamesElm;


	// 利用者画面からの呼び出し時はWMSの直接検索はしないのでメッセージを変更
	var isMapPage = false;
	try{ isMapPage = SaigaiTask.PageURL.params.pagetype=="map" } catch(e){};
	if(isMapPage){
			noticeMessage ='<%=lang.__("Enter keyword.")%>';
	}else{
		noticeMessage ='<%=lang.__("Enter WMSCapabilities url beginning with keyword or http:// (However, this function is valid only on admin window).")%>';
	}

	if (!document.getElementById("ckanSearchModalDialog")) {	//ダイヤログが2回作成されることを避ける。
		var dialogHtml = '';
		dialogHtml += "<form id='ckanForm' name='ckanForm' style='width:100%;' onsubmit='return false;'>";
		dialogHtml += "<div style='padding:3px;margin-bottom:2px;border:1px solid #DDDDDD;'>";
		dialogHtml += "<%=lang.__("Search map from clearinghouse")%>";

		dialogHtml += 	"<table border='0' cellpadding='0' cellspacing='0' width='100%'>";
		dialogHtml += 		"<tr><td colspan='2' style='padding:4px;' nowrap='nowrap'>"+noticeMessage+"</td></tr>";
		dialogHtml += 		"<tr><td width='90%'><input type='text' id='keyword' name='keyword' style='width:95%' value=''/></td>";
		dialogHtml += 			"<td>";
		dialogHtml += 					"<a id='ckanSubmit' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Search")%>";
	    dialogHtml += 					"<span class='ui-icon ui-icon-search'></span></a>";
		dialogHtml += 			"</td></tr>";
		dialogHtml += 		"<tr><td colspan='2' style='padding:4px;text-align:left;width:60px;' nowrap='nowrap'><%=lang.__("Sort")%>:";
		dialogHtml += 				"<select id='orderby' name='orderby'><option value='5'><%=lang.__("Automatic")%></option><option value='11'><%=lang.__("Newer registration")%></option><option value='10'><%=lang.__("Older registration")%></option></select>";
		dialogHtml += 				"&nbsp;<span id='ckanpaging'></span>";
		dialogHtml += 			"</td></tr>";

		//dialogHtml += 		"<tr><td colspan='2'>";
		//dialogHtml += 			"<table width='100%'>";
		//dialogHtml += 				"<tr><td>";
		//dialogHtml += 					"<a id='ckanSubmit' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Search")%>";
	    //dialogHtml += 					"<span class='ui-icon ui-icon-search'></span></a>";
		//dialogHtml += 				"</td><td align='right'>";
		// 2018.07.18 閉じるボタンは不要とのこと
		//dialogHtml += 					"<a id='ckanCancel' class='fm-button ui-state-default ui-corner-all fm-button-icon-left' onmouseover='jqGridButton_hover(this)' onmouseout='jqGridButton_out(this)' href='javascript:void(0)'><%=lang.__("Close")%>";
	    //dialogHtml += 					"<span class='ui-icon ui-icon-close'></span></a>";
		//dialogHtml += 				"</td></tr>";
		//dialogHtml += 			"</table>";
		//dialogHtml += 		"</td></tr>";

		dialogHtml += 	"</table>";

		dialogHtml += 	"<div id='ckanList' style='overflow:auto;height:480px; border:1px solid black;padding:4px;margin-top:3px;'></div>";
		dialogHtml += 	"<div id='ckanDetail' style='overflow:auto;height:480px; border:1px solid black;padding:4px;display:none;'></div>";
		dialogHtml += 	"<div id='ckanResource' style='overflow:auto;height:480px; border:1px solid black;padding:4px;display:none;'></div>";

		dialogHtml += "</div>";
		dialogHtml += "</form>";

		//ダイアログ作成
		$.jgrid.createModal(
			IDs = {
				themodal:'ckanSearchModalDialog',
				modalhead:'ckanSearchModalDialogHd',
				modalcontent:'ckanSearchModalDialogCnt',
				scrollelm : ''
			},
			dialogHtml,
			{
				gbox:"#gbox_clearinghousesearch",
				jqModal:true,
				drag:true,
				resize:false,
				caption:'<%=lang.__("Search for and add map.")%>',
				top:100,
				left:100,
				width:610,
				//height: 'auto',
				height: 650,
				closeOnEscape:true,
				zIndex:1000
			},
			'',
			'',
			true
		);


		//----------- ボタン設定 ----------
		//検索実行
		$("#ckanSubmit").click(function() {searchCkan(1);});
		// 並び替えを変更したら検索実行
		$("#orderby").change(function() {searchCkan(1);});
		//ダイアログを閉じる
		$("#ckanCancel").click(closeCkanDialog);

		$('#keyword').keypress(function (e) {
			if (e.which == 13) {
				searchCkan(1);
			}
		});
	}
	else {
		document.getElementById("keyword").value = "";
		searchCkan(1);
	}

	//初期の検索
	searchCkan(1);

	//ダイアログの表示
	$.jgrid.viewModal("#ckanSearchModalDialog",
		{modal:true}
	);
	$("#ckanSearchModalDialog").focus();
}

// リソース表示
function showResource(data) {
	var html = "";
	html += "<a id='linkReturnToDetail' href='#' style='float:right;'><%=lang.__("return to map info")%></a><br/>";
	html += "<%=lang.__("Resource info")%><br/>";
	html += "<table class='wmsCapsFormTable' width='100%' cellspacing=1 cellpadding=1>";
	for (var key in data) {
		var value = data[key];
		if (value) {
			html += "<tr>";
			html += "<th>" + key + "</th><td>";
			if (key == "url") {
				html += "<a href='" + value + "' target='_blank'";
				var mimetype = data["mimetype"];
				if (mimetype) {
					html += " type='" + mimetype + "'";
				}
				html += ">" + value + "</a>";
				if (mimetype && mimetype.lastIndexOf('image/', 0) === 0) {
					html += "<br><img src='" + value + "' width='300' class='detail_preview'>";
				}
			}
			else {
				html += value;
			}
			html += "</td></tr>";
		}
	}
	html += "</table></td>";
	html += "</tr>";

	html += "</table>";

	document.getElementById("ckanResource").innerHTML = html;
	showCkanResourcePane();

	$("#linkReturnToDetail").click(function() {showCkanDetailPane();});
}
