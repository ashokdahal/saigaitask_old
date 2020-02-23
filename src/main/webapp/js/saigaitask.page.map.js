/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
if(typeof SaigaiTask.Page=="undefined") {
	SaigaiTask.Page = {};
}

//グローバル設定
SaigaiTask.Map.view.Popup.searchButton = false;

/** レイアウト初期化 */
SaigaiTask.Map.initLayout = function(map) {
	$('.accordion_head').bind("click", function() {
		setTimeout(function() {
			//alert("resize");
			map.resize();
		}, 1000);
	});
	$("body").bind("onvisible", function(){map.resize();});
	$("body").bind("onopen", function(){map.resize();});
	$("body").bind("onclose", function(){map.resize();});
	$("body").bind("onresize", function(){
		// リサイズ直後だとうまくいかないので遅延させる
		setTimeout(function() {
			map.resize();
		}, 100);
	});
	$("#content .ui-layout-center").css("overflow", "hidden");

	/**
	 * Override.
	 */
	SaigaiTask.Map.view.MainPanel.prototype.getExpandSize = function(target) {
		target = $("#content .ui-layout-center").get(0);
		var padding = 0;
		var width = $(target).width()-padding;
		var height = $(target).height()-padding;
		var size = {width: width, height: height};
		return size;
	};
};

SaigaiTask.Map.showWindow = function(map) {
	content.attrs = []
	if(SaigaiTask.PageURL.params.insertFeature){
		if(SaigaiTask.PageURL.params.drawGeometryfid > 0){
			var fid = SaigaiTask.PageURL.params.drawGeometryfid;
			var layerId = SaigaiTask.PageURL.params.layerId;
			var layerInfo = map.ecommaps[0].layerInfoStore[layerId];
			SaigaiTask.Map.showContentsFormWindow(map, layerInfo, fid);
		}else{
			addLine();
		}
	}
}

/** フッターのページボタンの初期化 */
SaigaiTask.Map.initPageButton = function(map) {
	/** 「データの追加」ボタンハンドラの上書き */
	window.addLine = function() {
		// 時間指定中なら登録禁止とする
		if(!!SaigaiTask.PageURL.getTime()) {
			alert(lang.__("Can't register during time set."));
			return;
		}

		// 編集可能な登録情報レイヤ情報を取得する
		var ecommap = map.ecommaps[0];
		var layerInfos = ecommap.getLayerInfosByTypes([SaigaiTask.Map.Layer.Type.LOCAL]);
		// 地物登録フォームのプリセットデータの取得
		if(SaigaiTask.PageURL.params.insertFeatureData!=undefined){
			var count = 0;
			for(var idx in layerInfos[0].attrInfos){
				var attrId = layerInfos[0].attrInfos[idx].attrId;
				var data = SaigaiTask.PageURL.params.insertFeatureData[attrId];
				if(data != undefined){
					content.attrs[count] = {attrId: attrId, attrValue: data};
					count++;
				}
			}
		}
		var addableLayerInfos = [];
		for(var idx in layerInfos) {
			var layerInfo = layerInfos[idx];
			if(layerInfo.addable==true) addableLayerInfos.push(layerInfo);
		}
		// ない場合
		if(addableLayerInfos.length==0) {
			alert(lang.__("There is no registration info to which data add"));
		}
		// 1つの場合は登録情報登録ウィンドウを表示
		else if(addableLayerInfos.length==1) {
			var layerInfo = addableLayerInfos[0];
			SaigaiTask.Map.showContentsFormWindow(map, layerInfo, /*fid*/null);
		}
		// 複数ある場合は新規登録メニューを表示する
		else {
			var contextmenu = map.components.contextmenu;
			contextmenu.lonlat = null;
			var position = SaigaiTask.Page.getMousePosition();
			contextmenu.showMenu(position.x, position.y, {
				addContentsMenu: true,
				addContentsMenuLayerInfos: addableLayerInfos
			});
		}
	};
	/** 「変更登録」ボタンハンドラの上書き */
	window.saveData = function() {
		// do nothing.
	};
	/** 「印刷」ボタンハンドラの上書き */
	var initPrintContent = true;
	window.pdfDialog = function() {
		var pdfControl = map.controls.pdfControl;
		// 印刷設定の初期化
		if(initPrintContent) {
			var form = pdfControl.printWindow.formPanel.form;
			form.setValues({
				maptitle: SaigaiTask.Page.saigaiName,
				description: SaigaiTask.Page.menuprocessInfo.name + "\n" + SaigaiTask.Page.menutaskInfo.name + "\n" + SaigaiTask.Page.menuInfo.name
			});
			initPrintContent = false;
		}
		pdfControl.show();
	}
	/** 「検索」ボタンハンドラの上書き */
	window.filterDialog = function() {
		var filtering = new SaigaiTask.Filtering();
		// 検索時のイベントハンドラ
		filtering.events.on("filter", function(event, data) {
			filtering.dialog.mask(lang.__("Searching..."));

			// 検索条件をGETパラメータに保存
			if(typeof data=="object" && data!=null) {
				var conditionValue = null;
				if(typeof data.conditionValue!="undefined") {
					conditionValue = data.conditionValue;
				}
				SaigaiTask.PageURL.params.conditionValue = JSON.stringify(conditionValue);
			}

			// 登録情報レイヤをリロード
			for(var idx in map.ecommaps) {
				var ecommap = map.ecommaps[idx];
				for(var contentslayerIdx in ecommap.contentsLayers) {
					var layer = ecommap.contentsLayers[contentslayerIdx].getLayer();
					if(typeof layer.refreshParams=="function") {
						layer.refreshParams({
							nocache: true
						});
					}
				}
			}

			// 空間検索範囲の条件を更新
			// 空間検索の場合は、設定用の条件ではなく、実際に使用するレイヤIDを指定する
			map.ecommaps[0].updateSpatialLayerCondition(data.conditionValueActual);

			// マスクは1秒後に外す
			setTimeout(function(){
				filtering.dialog.unmask();
			}, 1000);
			console.log(data);
			console.log("===============");
		});
		var conditionValue = SaigaiTask.PageURL.params.conditionValue;
		if(typeof conditionValue=="string") conditionValue = JSON.parse(conditionValue);
		// ダイアログを表示
		filtering.show({
			taskId: SaigaiTask.Page.menutaskInfo.id,
			conditionValue: conditionValue,
			modal: false,
			loadButton: false,
			searchButton: true
		});
		console.log("filtering");
		console.log( filtering );

		// オーバーレイを非表示にする
		filtering.dialog.prev(".ui-widget-overlay").hide();
	}
	$("#filter-button").click(function() {
		filterDialog();
	});
};

SaigaiTask.Map.showContentsFormWindow = function(map, layerInfo, fid) {
	var lonlat = map.map.getCenter(); // 地図の投影法で取得
	var feature = null;
	var drawGeometryOnly = !!SaigaiTask.PageURL.params.drawGeometry;

	try{
		if(SaigaiTask.PageURL.params.drawGeometrywkt != ""){
			var wkt = SaigaiTask.PageURL.params.drawGeometrywkt;
			var wktFormat = new OpenLayers.Format.WKT();
			feature = wktFormat.read(wkt);
			var centroid = feature.geometry.getCentroid();
			center = new OpenLayers.LonLat(centroid.x, centroid.y);
			// センターに設定
			map.setCenter(center.clone());
		}
	}catch(e) {
		alert(lang.__("Unable to get position info."));
		console.error(lang.__("Failed to get position info :")+layerId+"."+fid);
		console.error(e);
		return;
	}
	new SaigaiTask.Map.view.ContentsFormWindow({
		stmap: map,
		layerInfo: layerInfo,
		fid: fid,
		feature: feature,
		lonlat: lonlat,
		content: content,
		drawGeometryOnly: drawGeometryOnly
	});
}

/**
 * PageURLを地図ページ用に拡張します.
 */
SaigaiTask.Map.initPageURL = function() {
	/**
	 * URLのcenterパラメータを設定します.
	 * @param {OpenLayers.LonLat} center(WGS84)
	 */
	SaigaiTask.PageURL.setCenter = function(center) {
		SaigaiTask.PageURL.override({"center": center.lon+","+center.lat});
	};
	/**
	 * URLのcenterパラメータを取得します.
	 * @return {OpenLayers.LonLat} WGS84の経緯度
	 */
	SaigaiTask.PageURL.getCenter = function() {
		var text = SaigaiTask.PageURL.params["center"];
		return SaigaiTask.PageURL._getCenter(text);
	};

	SaigaiTask.PageURL.getInitCenter = function() {
		var text = SaigaiTask.PageURL.initParams["center"];
		return SaigaiTask.PageURL._getCenter(text);
	};
	SaigaiTask.PageURL._getCenter = function(text) {
		if(typeof text=="string") {
			var elems = text.split(",");
			if(elems.length==2) {
				var lon = elems[0];
				var lat = elems[1];
				if(isFinite(lon) && isFinite(lat)) {
					return new OpenLayers.LonLat(Number(lon), Number(lat));
				}
			}
		}
		return null;
	};

	SaigaiTask.PageURL.setZoom = function(zoom) {
		SaigaiTask.PageURL.override({"zoom": zoom});
	};
	SaigaiTask.PageURL.getZoom = function(zoom) {
		return SaigaiTask.PageURL.params["zoom"];
	};
	SaigaiTask.PageURL.getInitZoom = function(zoom) {
		return SaigaiTask.PageURL.initParams["zoom"];
	};

	SaigaiTask.PageURL.setHidden = function(hidden) {
		SaigaiTask.PageURL.override({"hidden": hidden});
	}

	// オーバーライド
	SaigaiTask.PageURL._getUrl = SaigaiTask.PageURL.getUrl;
	SaigaiTask.PageURL.getUrl = function() {
		var stmap = window.map;

		// ポップアップの再現パラメータ
		if(!!stmap) {
			var popupManager = stmap.popupManager;
			var popupParams = [];
			if(!!popupManager) {
				for(var idx in popupManager.popups) {
					var popup = popupManager.popups[idx];
					if(!!popup.popupParam) {
						popupParams.push(popup.popupParam);
					}
				}
			}
			SaigaiTask.PageURL.override({"popup": popupParams.join(",")});
		}

		return this._getUrl.apply(this, arguments);
	}

};
SaigaiTask.Map.initPageURL();

SaigaiTask.Map.initCheckNewMetadata = function(intval, excludeMetadataIds){
	var checknewmetadata = function(){
		// <%-- クリアリングハウスから取得 --%>
		jQuery.ajax(SaigaiTask.contextPath+'/page/newmetadata?'+(new Date()).getTime(),{
			async: true,
			dataType: "json",
			success: function(data, textStatus, jqXHR) {
				// レコードのフィルタ
				var records = [];
				if(parseInt(data.numMatch) > 0){
					for(var i = 0;i < data.record.length;i++){
						var record = data.record[i];
						// record.status は、メタデータがあ
						var updateStatus = !!!record.status;
						if(updateStatus) record.status = lang.__("New");
						var metadataid = record.fileIdentifier;
						var updateTime = excludeMetadataIds[metadataid];
						if(typeof updateTime!="undefined") {
							// 期限なしの削除（すでに追加済みの場合とか）
							if(updateTime==true) continue;
							// Chrome は "2014-03-19 19:41:55" でもOKだが、他のブラウザでは invalid date となる.
							// 正しくは  "2014-03-19T19:41:55" とする.
							// 削除レコードによるフィルタ、削除されていても更新日時が新しければ通知する
							var recordUpdateTime = new Date(record["updateTime"].replace(" ", "T"));
							if(isNaN(recordUpdateTime.getTime())) {
								// IE8はこの形式では扱えないので修正する(ブラウザ判定はしない)
								var recordUpdateTimeIE = record["updateTime"].replace(/[-]/g, "/");
								// yyyy/mm/ddTHH:mm:ddの形に整形
								recordUpdateTimeIE = recordUpdateTimeIE.substring(0, recordUpdateTimeIE.lastindexOf("."));
								recordUpdateTime = new Date(recordUpdateTimeIE.replace(" ", "T"));
								if(isNaN(recordUpdateTime.getTime())){
									console.error(lang.__("Update date and time of the deleted record is invalid date.")+record["updateTime"]);
								}
							}
							var updateTimeDate = new Date(updateTime.replace(" ", "T"));
							if(isNaN(updateTimeDate)) {
								// IE8はこの形式では扱えないので修正する
								var updateTimeIE = updateTime.replace(/[-]/g, "/");
								updateTimeIE = updateTimeIE.substring(0, updateTimeIE.lastIndexOf("."));
								updateTimeDate = new Date(updateTimeIE.replace(" ", "T"));
								if(isNaN(updateTimeDate)) {
									console.error(lang.__("Update date and time of metadata is invalid date.")+updateTime);
								}
							}
							if(recordUpdateTime <= updateTimeDate) continue;
							else if(updateStatus) data.record[i].status=lang.__("Update<!--2-->");
						}
						records.push(data.record[i]);
					}
				}

				if(0<records.length) {
					// <%-- メタデータ一覧をテーブルにしてダイアログ表示 --%>
					var mdlist = $("#newmetadatalist");
					var html = lang.__("<p>Newly registered/updated metadata found.<br>Select methods to be added and press [Proceed] button.</p>");
					html += "<table id='metadatalisttable' class='tablesorter'>";
					if (!data.wmsMoreThanOne)
						html += "<thead><tr>"
						//+"<th><input type='checkbox' class='all-cb'/></th>"
						+lang.__("<th><!--status--></th><th>Title</th><th>Creator</th><th>Registration, updated date and time</th><th width='290px;'>Method</th></tr></thead>");
					else
						html += "<thead><tr>"
						+lang.__("<th><!--ステータス--></th><th>タイトル</th><th>作成者</th><th>登録・更新日時</th><th>WMS</th><th width='290px;'>追加方法</th></tr></thead>");
					html += "<tbody>";
					for(var i = 0;i < records.length; i++) {
						var record = records[i];
						for (j = 0; j < (!record.wmsurls ? 1 : record.wmsurls.length); j++) {
							var metadataid = record.fileIdentifier;
							var wmsurl = !record.wmsurls ? record.wmsurl : record.wmsurls[j].url;
							var title = record.title;
							var cntname = (record.contactname!=null?record.contactname:"");
							var updtm = record.updateTime;
							var idx = updtm.indexOf("T");
							if(idx != -1)
								updtm = updtm.substring(0,idx)+" "+updtm.substring(idx+1);
							html += "<tr"+(i%2==1?" class='odd'":"")+">";
							//html += "<td style='text-align: center !important;'><input type='checkbox' value='"+i+"' class='record-cb'></td>"
							html += "<td style='text-align: left !important;'>"+record.status+"</td>";
							html += "<td style='text-align: left !important;'>"+title+"</td>";
							html += "<td style='text-align: left !important;'>"+cntname+"</td>";
							html += "<td style='text-align: left !important;'>"+new Date(updtm).toLocaleString()+"</td>";
							if (data.wmsMoreThanOne) {
								name = record.wmsurls[j].name;
								html += "<td style='text-align: left !important;'>"+name+"</td>";
							}
							// 追加方法
							{
								html += "<td style='text-align: left !important;'> ";
								var name = "state-"+i+"-"+j;
								var states = [{
									name: name,
									value: "add",
									label: lang.__("Add this map")
								}, {
									name: name,
									value: "add-all",
									label: lang.__("Add to all map")
								}, {
									name: name,
									value: "remove",
									label: lang.__("No add"),
									checked: true
								}];
								for(var statesIdx=0; statesIdx<states.length; statesIdx++) {
									// スペースで区切る
									if(0<statesIdx) html += "&nbsp;&nbsp;";
									// 変数の準備
									var state = states[statesIdx];
									var id = state.name+"-"+statesIdx;
									var checked = typeof state.checked=="boolean" ? state.checked : false;
									// HTML要素をjQueryで組み立てる.
									var wrapper = $("<div>"); // HTML文字列を取得するためのDIV
									var radio = $("<input type='radio'>").attr("name", state.name).attr("value", state.value).attr("id", id).attr("checked", checked);
									var label = $("<label>").attr("for", id).html(state.label);
									// wrpperを使ってHTML文字列を取得
									wrapper.append(radio).append(label);
									html += wrapper.html();
								}
								html += "</td>";
							}
							html += "</tr>";
						}
					}
					html += "</tbody>";
					html += "</table>";
					mdlist.html(html);

					// すでに何かのダイアログが表示されていれば、メタデータ更新通知ダイアログは表示しない
					if(SaigaiTask.Page.anyDialogIsOpen()) {
						setTimeout(checknewmetadata,intval);
					}
					else {
						// ダイアログ表示
						mdlist.dialog({
							title: lang.__("Registered / update metadata found."),
							modal: true,
							maxHeight: 500,
							minWidth: 700,
							buttons: [
								{
									text: lang.__("Proceed"),
									click: function(event){
										var params = [];
										var hasAdd = false;
										for(var i = 0;i < records.length; i++) {
											var record = records[i];
											var title = record.title;
											var metadataid = record.fileIdentifier;
											var updtm = record.updateTime;
											for (j = 0; j < (!record.wmsurls ? 1 : record.wmsurls.length); j++) {
												var name = "state-"+i+"-"+j;
												var state = $("#metadatalisttable [name='"+name+"']:checked").val();
												var opacity = record.opacity;
												if(state.indexOf("add")==0) hasAdd = true;
												else if(state.indexOf("remove")==0) {
													// 除外メタデータIDに指定する
													excludeMetadataIds[metadataid] = updtm.replace("T", " ");
												}
												var wmsUrl = record.wmsurls ? record.wmsurls[j].url : null;
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
													layeropacity: opacity,
													/** 表示順 */
													// サーバ側で自動付与
													//disporder: 0
													/** WMS URL */
													wmscapsurl: wmsUrl
												});
											}
										}

										// サーバのセッションに保存
										var dialog = mdlist.parent(".ui-dialog"); // dialog全体の要素
										dialog.mask(lang.__("Now saving.."));

										jQuery.ajax(SaigaiTask.contextPath+"/page/map/sessionMetadata", {
											async: false,
											type: "POST",
											//dataType: "json",
											data: {
												records: JSON.stringify(params)
											},
											success: function(data, textStatus, jqXHR) {
												// 追加処理の場合はページ再読み込み
												if(hasAdd) {
													dialog.unmask();
													dialog.mask(lang.__("Now reloading.."));
													SaigaiTask.PageURL.move();
												}
												else {
													// 閉じる
													dialog.unmask();
													mdlist.dialog("close");
												}
											},
											error: function(jqXHR, status, errorThrown) {
												alert(lang.__("An error occurred. ({0})", status) + "\n"+errorThrown);
												// 閉じる
												dialog.unmask();
												mdlist.dialog("close");
											},
											complete: function(jqXHR, status) {
											}
										});

									}
								}/*, 閉じるボタンがあると誤操作になるので非表示に変更
								{
									text: lang.__("Close"),
									click: function(event){
										mdlist.dialog("close");
									}
								}*/
								],
								beforeClose : function(event){
									setTimeout(checknewmetadata,intval);
									return true;
								}
						});

						// テーブル初期化
						$("#metadatalisttable").tablesorter({
							widgets: ['zebra'],
							headers: {
								4: {sorter: false}
							},
							sortList: [
							           [0, 0] // ステータスを「新規」「更新」の順で並び替え
							           ]
						});
					}
				}else{
					// <%-- メタデータが検索されなかった場合は次のタイマを起動 --%>
					setTimeout(checknewmetadata,intval);
				}
			},
			error: function(jqXHR, status, errorThrown) {
				// <%-- エラーの場合も次のタイマ起動--%>
				setTimeout(checknewmetadata,intval);
			}
		});
	};
	// <%-- 初回のタイマ起動 --%>
	if(0<intval) {
		setTimeout(checknewmetadata,intval);
	}
	//SaigaiTask.Page.Map.checknewmetadata = checknewmetadata;
	//SaigaiTask.Page.Map.excludeMetadataIds = excludeMetadataIds;
}

/**
 * 地図ページのJavaScriptを初期化
 */
SaigaiTask.Page.Map = function(option) {

	var ecommapInfoOption = option.ecommapInfoOption;
	var updateTimeFormat = option.updateTimeFormat;
	var filterlayer = option.filterlayer;

	if(ecommapInfoOption==null) {
		alert(lang.__("Map not set."));
		return;
	}

	// 地図オブジェクトを生成
	var contextPath = SaigaiTask.contextPath;
	var map = window.map = new SaigaiTask.Map("map", {
		contextPath: contextPath,
		api: new SaigaiTask.Map.SaigaiTask2API(contextPath),
		icon: new SaigaiTask.Map.Icon(contextPath+"/js/SaigaiTaskJS/css"),
		showLegend: true,
		geocoder: option.geocoder,
		coordinateDecimal: option.coordinateDecimal,
		collapsed: option.collapsed
	});

	// ズームボタンをズームバーに変更する.
	var panZoom = map.controls.panZoom;
	if(!!panZoom) {
		map.map.removeControl(panZoom);
		panZoom.destroy();
	}
	if(false) {
		// 拡大縮小ボタン、十字スクロール、ホーム
		var panZoomBar = new OpenLayers.Control.PanZoomBar();
		SaigaiTask.Map.extendZoomWorld	(panZoomBar);
		map.addControl(panZoomBar, "panZoomBar");
	}
	else if(true) {
		// 拡大縮小ボタンだけ
		var zoom = new OpenLayers.Control.Zoom();
		// コントロール追加
		map.addControl(zoom, "zoom");
		// ホームボタンを有効
		SaigaiTask.Map.extendZoomWorld(zoom);
	}

	// eコミマップ情報を初期化
	ecommapInfoOption.stmap = map;
	var ecommapInfo = new SaigaiTask.Map.EcommapInfo(ecommapInfoOption);
	var time = SaigaiTask.PageURL.getTime();
	if(!!time && time!=null) {
		// 登録情報レイヤの時系列対応
		if(!!ecommapInfo.contentsLayerInfo) {
			try {
				var iso8601 = new Date(time).toISOString();
				ecommapInfo.contentsLayerInfo.params.time = iso8601;
			} catch(e) {
				alert(lang.__("Time parameter invalid.")+" "+time);
			}
		}
	}
	//$("body").bind("onvisible", function(){
	map.registEcommapInfo(ecommapInfo);
	map.mapId = ecommapInfo.mapInfo.mapId;
	//});

	// loadendecommap
	{
		// 意思決定支援機能の実行
		if(SaigaiTask.PageURL.initParams.decisionsupport){
			SaigaiTask.Page.decisionSupport.displayDecisionLayer(SaigaiTask.contextPath+"/page/decisionsupport", SaigaiTask.csrfToken);
		}

	}

	// loadmask
	$('#content').mask("Loading...");
	
	// 各種イベントを設定
	map.events.on({
		"loadendecommap": function(ecommap) {
		},
		"alllayerloadend": function() {
			$('#content').unmask();
		},
		"beforeaddlegend": function(evt) {
		},
		"afterredrawlegendpanel": function(evt) {
		},
		"successcontentssubmit": function(eOpts) {
			// 登録/更新後に最終更新日カラムの表示を更新する
			// 登録/更新が可能なレイヤはメニューに１つしか存在しないという前提.
			var contentsFormPanel = eOpts.contentsFormPanel;
			var params = contentsFormPanel.params;
			var attrInfos = contentsFormPanel.layerInfo.attrInfos;
			for(var idx in attrInfos) {
				var attrInfo = attrInfos[idx];
				var attrId = attrInfo.attrId;
				if( // 登録時にアップデート
						(contentsFormPanel.update==false && attrInfo.updateInserted)
						// 更新時にアップデート
						|| (contentsFormPanel.update && attrInfo.updateModified) ) {
					var val = params[attrId];
					if(val!=null) {
						jQuery.ajax({
							url: SaigaiTask.contextPath+"/page/getUpdateTime",
							data: {
								timestamp: val
							},
							success: function(result) {
								if(typeof result.text=="string") {
									SaigaiTask.setUpdateTime(result.text);
								}
							}
						});
					}
				}
			}
			// クリアリングハウスのメタデータを更新する
			var layerId = contentsFormPanel.layerInfo.layerId;
			map.api.updateMetadata(layerId);
			// フィルターがあるなら再検索する。
			SaigaiTask.Page.filter.search();
		},
		"successcontentsdelete": function(eOpts) {
			// TODO: 削除後に最終更新日カラムの表示を更新する
			// クリアリングハウスのメタデータを更新する
			//var layerId = contentsFormPanel.layerInfo.layerId;
			var layerId = eOpts.layerInfo.layerId;
			map.api.updateMetadata(layerId);
			// フィルターがあるなら再検索する。
			SaigaiTask.Page.filter.search();
		}
	});
	for(var idx in SaigaiTask.Edit.slimers) {
		var slimer = SaigaiTask.Edit.slimers[idx];
		// 一括変更時のイベントハンドラ
		slimer.dialog.on("slimersuccess", function() {
			// フィルターがあるなら再検索する。
			SaigaiTask.Page.filter.search();
		});
	}
	// OpenLayersのイベントハンドラ
	map.map.events.on({
		"move": function() {
			// 地図移動時に中心位置をパラメータに保存する
			var center = map.map.getCenter();
			center.transform(map.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));
			SaigaiTask.PageURL.setCenter(center);
		},
		"zoomend": function() {
			// ズーム時にパラメータに保存する
			var zoom = map.map.getZoom();
			SaigaiTask.PageURL.setZoom(zoom);
		},
		"changelayer": function() {
			// レイヤ表示/非表示でパラメータを更新
			var hidden = "none"; // ダミーのレイヤID
			var hiddenLayerIds = map.ecommaps[0].getHiddenLayerIds();
			if(0<hiddenLayerIds.length) hidden = hiddenLayerIds.join(",");
			SaigaiTask.PageURL.setHidden(hidden);

			// SLDルールのパラメータを初期化
			SaigaiTask.PageURL.params.rule = null;

			// SLDルールのチェック状態をパラメータに設定
			// 未チェックの場合は レイヤID だけ指定するものとする
			var ecommap = map.ecommaps[0];
			var layerInfos = ecommap.getLayerInfosByTypes([SaigaiTask.Map.Layer.Type.LOCAL]);
			var clearrule = "";
			for(var idx in layerInfos) {
				var layerInfo = layerInfos[idx];
				var legendrules = layerInfo.legendrules;
				if(legendrules!=null) {
					var layerRule = layerInfo.layerId
					var allON = true;
					var allOFF = true;
					for(var legendrulesIdx in legendrules) {
						var legendrule = legendrules[legendrulesIdx];
						if(legendrule.visibility) {
							 layerRule += ":"+legendrule.ruleId;
							 allOFF = false;
						}
						else allON = false;
					}

					// 全ONまたはレイヤ非表示全OFFならパラメータで指定する必要なし
					if(allON || (allOFF&&layerInfo.visibility==false)) {
						clearrule += (clearrule!=""?",":"")+layerInfo.layerId;
					}
					else {
						// パラメータに追加
						if(SaigaiTask.PageURL.params.rule==null) {
							SaigaiTask.PageURL.params.rule = layerRule;
						}
						else {
							SaigaiTask.PageURL.params.rule += ","+layerRule;
						}
					}
				}
			}

			// SLDルールのチェック状態をセッションに保存
			if(!!SaigaiTask.Page.Map.sessionSLDRuleXHR) {
				var xhr = SaigaiTask.Page.Map.sessionSLDRuleXHR;
				if(xhr && xhr.readyState != 4){
					xhr.abort();
				}
			}
			SaigaiTask.Page.Map.sessionSLDRuleXHR = jQuery.ajax(SaigaiTask.contextPath+"/page/map/sessionSLDRule", {
				async: true,
				type: "post",
				dataType: "json",
				//headers: {"X-CSRF-Token":SaigaiTask.csrfToken},
				data: {
					rule: SaigaiTask.PageURL.params.rule,
					clearrule: clearrule
				}
			});
		}
	});
	$(window).on("beforeunload", function() {
		// ページ遷移前に Cookie に最後の表示位置を記録する

		// Home位置なら記憶している位置情報をリセットする
		var location = {
				center: null,
				zoom: 0
		}
		// Home位置でなければ位置情報をセットする
		if(!map.isHome()) {
			// 地図移動時に中心位置をCookieに保存する
			var center = map.getCenter();
			location.center = center.lon+","+center.lat;
			// ズーム時にCookie に保存する
			location.zoom = map.map.getZoom();
		}
		Ext.util.Cookies.set("location", JSON.stringify(location));
	});

	// レイアウト初期化
	SaigaiTask.Map.initLayout(map);
	SaigaiTask.Map.initPageButton(map);
	SaigaiTask.Map.showWindow(map);

	// フィルタ対象レイヤがあれば
	var filterLayerInfo = ecommapInfo.layerInfoStore[filterlayer];
	if(typeof filterLayerInfo!="undefined" && filterLayerInfo!=null) {
		// グレー表示チェックボックスのイベント定義
		var graycb = $("#filter-gray-ck");
		graycb.change(function() {
			var checked = graycb.is(":checked");
			filterLayerInfo.setGrayout(checked ? filterLayerInfo.grayout : 0);
			SaigaiTask.PageURL.override({filtergray: new String(checked)}); // Boolean to String (for false)
		});
		var filtergray = SaigaiTask.PageURL.params.filtergray;
		SaigaiTask.PageURL.override({filtergray: new String(filtergray)}); // Boolean to String (for false)
		graycb.attr("checked", filtergray);
		graycb.change();
		// 空間検索範囲レイヤの表示チェックボックスを追加する
		if(filterLayerInfo.hasSpatialLayer()) {
			var filterspatial = SaigaiTask.PageURL.params.filterspatial;
			SaigaiTask.PageURL.override({filterspatial: new String(filterspatial)}); // Boolean to String (for false)
			var cb = $('<input type="checkbox" id="filter-spatial-ck" '+(filterspatial?'checked':'')+'/>');
			var label = $(lang.__('<label for="filter-spatial-ck">Display search range</label>'));
			graycb.parent().append(cb).append(label);
			cb.change(function() {
				var checked = cb.is(":checked");
				filterLayerInfo.spatialLayer.setVisibility(checked);
				SaigaiTask.PageURL.override({filterspatial: new String(checked)});
			});
			if(filterspatial) {
				// 地図表示前にSingleTileのレイヤを表示するとエラーになるため、
				// 遅延させて表示する.(ほかに良い方法ある？)
				setTimeout(function() {
					filterLayerInfo.spatialLayer.setVisibility(true);
				}, 500);
			}
			
			// フィルター解除の場合
			if(SaigaiTask.Page.Filter.nofilter) {
				$("#filter-spatial-ck,label[for=filter-spatial-ck]").hide();
			}
		}
	}

	// 時間パラメータ変更時のイベントハンドラを初期化
	SaigaiTask.Page.timeslider.onchangetime = function() {
		// フィルターがあるなら再検索する。
		SaigaiTask.Page.filter.search();

		// 再描画すると、TIMEパラメータが自動更新されるため、
		// 登録情報レイヤなど、すべてのWMSレイヤを再描画する
		//map.redrawContentsLayer(0);
		for(var idx in map.map.layers) {
			var layer = map.map.layers[idx];
			map.redrawLayer(layer);
		}
		// ２画面表示中の場合は再描画
		if(typeof submap!="undefined") {
			for(var idx in submap.map.layers) {
				var layer = submap.map.layers[idx];
				submap.redrawLayer(layer);
			}
		}
		// 最終更新日時を取得
		{
			// 最終更新日時属性IDを持つレイヤ情報を検索
			var layerInfo = null;
			var ecommap = map.ecommaps[0];
			var layerInfos = ecommap.getLayerInfosByTypes([SaigaiTask.Map.Layer.Type.LOCAL]);
			for(var idx in layerInfos) {
				var layerInfo = layerInfos[idx];
				if(layerInfo.updatecolumn!=null) {
					 var json = map.api.getLayerLastUpdateTime(layerInfo);
					var updateTime = json.updateTime;
					SaigaiTask.setUpdateTime(updateTime);
					break;
				}
			}
		}
	};

	// 計測機能の実装
	var measure = new SaigaiTask.Map.view.Measure(map);
	$("#measure-button").on('click' ,function(){
		measure.showMenu();
	});
};


