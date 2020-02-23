/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
if(typeof SaigaiTask.Page=="undefined") {
	SaigaiTask.Page = {};
}

/**
 *Initialize JavaScript of list page
 */
SaigaiTask.Page.List = function(option) {
	// 地図ボタンのクリックイベントハンドラを初期化する
	SaigaiTask.Page.List.initOnClickMapButton();
	// 外部地図切り替えSelectを表示する
	SaigaiTask.Page.List.createMetadataSelect(option);
	// 時間パラメータ変更時のイベントハンドラを初期化
	if (SaigaiTask.Page.timeslider != undefined) {
		SaigaiTask.Page.timeslider.onchangetime = function() {
			// リスト再読み込み
			SaigaiTask.PageURL.reloadContent();
		};
	}
	
	// 最初の一度だけ初期化する
	if(!SaigaiTask.Page.List.initialized) {
		// 初期化フラグを立てる
		SaigaiTask.Page.List.initialized = true;
		// フィルタリング初期化
		if(!!SaigaiTask && !!SaigaiTask.Page && !!SaigaiTask.Page.Filter) {
			SaigaiTask.Page.filter = new SaigaiTask.Page.Filter();
			// 検索イベントハンドラを定義
			SaigaiTask.Page.filter.events.on("search", function(event, filterInfo, searchResult){
				SaigaiTask.PageURL.reloadContent();
			});
		}
	}
};

/** 初回の初期化フラグ */
SaigaiTask.Page.List.initialized = false;

/**
 * 地図ボタンのクリックイベントハンドラを初期化する
 */
SaigaiTask.Page.List.initOnClickMapButton = function() {
	// 地図ボタンのクリックイベントハンドラ
	var mapWindow = null;
	var map = null;
	$(".map-btn").on("click", function() {
		var mapBtn = $(this);
		var tr = mapBtn.parents("tr");
		var layerid = $("input[name='layerid']", tr).val();
		var gid = parseInt($("input[name='gid']", tr).val());
		var theGeom = $("input[name='the_geom']", tr).val();

		var move = true;
		if(move) {
			// アコーディオンの開閉状態を保存
			SaigaiTask.Page.List.getAccordionHeaderState();
			// 地図画面に移動
			SaigaiTask.PageURL.override({pagetype : 'map', popup : layerid+"."+gid }).move();
		}
		else {
			var showPopup = function() {
				// ポップアップ表示
				if(gid!=NaN && 0<theGeom.length) {
					map.getContent(layerid, gid);
				}
			}

			// 地図ウィンドウを生成して表示
			if(mapWindow==null) {
				mapWindow = SaigaiTask.Page.List.createMapWindow(function() {
					showPopup();
				});
				map = mapWindow.map;
			}
			// 地図ウィンドウを表示
			else {
				mapWindow.show();
				showPopup();
			}
		}

		return false;
	});
};

/**
 * 地図ウィンドウを生成します.
 * @return {Ext.Window}
 */
SaigaiTask.Page.List.createMapWindow = function(callback) {
	// OpenLayers.Mapのdivを生成する
	var newDiv = new Ext.Element(document.createElement('div'));
	newDiv.setStyle("width", "100%");
	newDiv.setStyle("height", "100%");

	// 地図ウィンドウを生成
	var mapWindow = Ext.create("Ext.Window", {
		contentEl: newDiv,
		layout: "fit",
		width: 500,
		height: 500,
		resizable: false,
		closeAction: "hide"
	});
	window.mapWindow = mapWindow;
	// 地図ウィンドウを表示
	mapWindow.show();
	mapWindow.alignTo(document, "br", [-mapWindow.getWidth(), -mapWindow.getHeight()]);

	// Generate map object
	var contextPath = SaigaiTask.contextPath;
	map = new SaigaiTask.Map(newDiv.id, {
		contextPath: contextPath,
		api: new SaigaiTask.Map.SaigaiTask2API(contextPath),
		icon: new SaigaiTask.Map.Icon(contextPath+"/js/SaigaiTaskJS/css"),
		showLegend: false
	});
	mapWindow.map = map; // 保存しておく

	// 読込中マスク
	loadMask = new Ext.LoadMask(mapWindow, {msg: lang.__("Now loading..")});
	loadMask.show();

	// eコミ情報をロード
	var menuid = SaigaiTask.PageURL.params.menuid;
	if(jQuery.isNumeric(menuid)==false) return;
	var url = SaigaiTask.contextPath + "/page/map/ecommapInfo?menuid="+menuid;
	var success = function(data) {
		var ecommapInfo = new SaigaiTask.Map.EcommapInfo(data);
		map.registEcommapInfo(ecommapInfo);
		map.mapId = ecommapInfo.mapInfo.mapId;
		if(jQuery.isFunction(callback)) {
			callback();
		}
	};
	jQuery.ajax(url, {
		async: true,
		dataType: "json",
		data: null,
		success: function(data, textStatus, jqXHR) {
			if(jQuery.isFunction(success)) {
				success(data);
			}
		},
		error: function(jqXHR, status, errorThrown) {
			if(jqXHR.status!=0) {
				alert(lang.__("Failed to get initialization map data. (mapId=")+mapId+")"+me.errorMsg(jqXHR));
			}
			console.error(errorThrown);
		},
		complete: function() {
			loadMask.hide();
		}
	});

	return mapWindow;
};

/**
 * メタデータ切り替えSelect を表示する
 */
SaigaiTask.Page.List.createMetadataSelect = function(option) {
	var metadatas = option.metadatas;

	var container = document.getElementById("externallistselect-container");
	if (!container || container.hasChildNodes()) return;

	 // メタデータがあればリスト切り替え用のセレクトボックスを生成する
	 var select = document.createElement("select");
	 var selected = null;
	 if(typeof SaigaiTask.PageURL.params.metaid!="undefined") {
		 selected = SaigaiTask.PageURL.params.metaid;
	 }

	 // 内部地図のoptionを作成
	var option = document.createElement("option");
	//option.innerText = SaigaiTask.Page.localgovInfo.pref;
	option.text = SaigaiTask.Page.localgovInfo.pref;
	var city = SaigaiTask.Page.localgovInfo.city;
	if(typeof city!="undefined" && 0<city.length) {
		if (typeof(option.innerText) != 'undefined')
			option.innerText = city;
		else
			option.text = city;
	}
	option.value = "";
	select.appendChild(option);

	// 内部リスト、外部リスト以外のそのほかのリスト
	var isOther = function(metadataId) {
		if(metadataId.indexOf("list")==0){
			var optgroup = document.createElement("optgroup");
			optgroup.label = "―――――";
			select.appendChild(optgroup);
			// 内部地図のoptionを作成
			var option = document.createElement("option");
			option.text = metadatas[metadataId];
			option.value = metadataId;
			select.appendChild(option);
			//return true;
		}
		return false;
	};

	// 外部地図のoptionを作成
	var otherMetadataIds = [];
	var metadataNum = 0;
	for(var metadataId in metadatas) {
		if(isOther(metadataId)) {
			 otherMetadataIds.push(metadataId);
			 continue;
		}
		if(metadataId.indexOf("list")!=0){
			metadataNum++;
			var metadataName = metadatas[metadataId];
			var option = document.createElement("option");
			var optionText = metadataName + lang.__("(External list)");
			//var optionText = metadataName;
			if (typeof(option.innerText) != 'undefined') option.innerText = optionText;
			else option.text = optionText;
			option.value = metadataId;
			option.selected = metadataId==selected;
			select.appendChild(option);
		}
	}

	// そのほかのリストの option を作成
/*
	if(0<otherMetadataIds.length) {
		var optgroup = document.createElement("optgroup");
		optgroup.label = "―――――";
		select.appendChild(optgroup);
		for(var otherMetadataIdsIdx in otherMetadataIds) {
			var metadataId = otherMetadataIds[otherMetadataIdsIdx];
			metadataNum++;
			var metadataName = metadatas[metadataId];
			var option = document.createElement("option");
			var optionText = metadataName;
			if (typeof(option.innerText) != 'undefined') option.innerText = optionText;
			else option.text = optionText;
			option.value = metadataId;
			option.selected = metadataId==selected;
			optgroup.appendChild(option);
		}
	}
*/
	 // 外部地図があればリスト切り替え用のセレクトボックスを表示する
	 if(0<metadataNum) {
		 var container = document.getElementById("externallistselect-container");
		 var span = document.createElement("span");
		 span.innerHTML = lang.__("Data switch:");
		 container.appendChild(span);
		 container.appendChild(select);

		// メタデータIDを指定して外部地図リストページへ移動する
		 $(select).change(function() {
			var metadataId = $(this).val();
			//var pagetype = metadataId=="" ? "list" : "externallist";
			if(metadataId == "" || metadataId.indexOf("list")==0){
				var pagetype = "list";
			}else{
				var pagetype = "externallist";
			}
			SaigaiTask.PageURL.override({
				pagetype: pagetype,
				metaid: metadataId
			}).reloadContent();
		 });
	 }
};

function redrawList() {
	console.log($("#content_main").height());
	console.log($(".tablesorter").height());
	var th = $("#content_main").height()-$(".tablesorter").height()-20;
	var nh = $(".tablesorter-scroller-table").height();
	console.log("table h="+$(".tablesorter-scroller-table").height()+" new h="+th);
	if (Math.abs(th - nh) > 15) {
		$(".tablesorter-scroller-table").height(th);
		console.log("re height");
	}
	//th = $("#content_main").height()-$(".tablesorter").height();
	//$(".tablesorter-scroller-table").height(th);
	//$("#mapth").width("100px");
}

/**
 * table を上から順に走査して TR(TH, TD) を配列で取得する.
 * オプションなしの場合は表示されているデータをすべて取得する.
 */
SaigaiTask.Page.List.getDatas = function(options) {
	options = $.extend({
		tableId: "editTable",
		excludeColumnHeader: false,
		excludeTotalHeader: false,
		doublequote: false,
		filter: false
	}, options);
	var datas = [];
	var accordionInfo = SaigaiTask.Page.List.accordionHeaderInfo();
	var curAccrodionInfo = null;
	var table = $("[id='"+options.tableId+"']:visible");
	var trs = $("tr", table);
	trs.each(function(trsIdx) {
		var tr = trs.eq(trsIdx);
		var data = [];
		
		// TODO: フィルタ行を含めない？

		// 判定用の変数を定義
		isColumnHeader = tr.attr("id")=="column-header";
		isTotalHeader = tr.attr("id")=="total-header";
		isData = tr.hasClass("odd")||tr.hasClass("even");
		isFilter = tr.hasClass("gray");
		isGroupHeader = tr.hasClass('group-header');
		if (isData && curAccrodionInfo!=null && !curAccrodionInfo.open) return;

		// オプション指定があれば、ヘッダ は含めない
		if(isColumnHeader && options.excludeColumnHeader) return;
		// オプション指定があれば、合計行 は含めない
		if(isTotalHeader && options.excludeTotalHeader) return;
		// オプション指定があれば、フィルタリング行は含めない
		if (isFilter && options.filter) return;

		// td, th の値を取得
		tr.children("td,th").each(function() {
			if ($(this).hasClass("noout")) return;
			var text = "";
			if (this.tagName=="TD") {
				if (isGroupHeader) {//アコーディオン機能
					var name = "";
					$(this).children("span").each(function() {
						if ($(this).hasClass("group-name"))
							name = trimtext($(this).text());
					});
					curAccrodionInfo = accordionInfo[name];
					text = {
						open: accordionInfo[name].open,
						value: name,
						count: accordionInfo[name].count
					};
					//text = $(this).find("span").text();
				}
				//selectの場合
				else if ($(this).children("span").length != 0)
					text = $(this).find("span").text();
				else if ($(this).attr("id") == "mapbtn")
					text = " ";
				else
					text = trimtext($(this).text());
			}
			else if (this.tagName=="TH")
				text = $(this).text().trim();

			if (options.doublequote && !isGroupHeader)
				text = "\""+text+"\"";
			data.push(text);
		});

		if(0<data.length) datas.push(data);
	});

	return datas;
}

function trimtext(text) {
	if (text.length == 0) return text;
	var str = text;
	while (true) {
		var org = str;
		var str = trimdata(str);
		if (str.length == org.length)
			return str;
	}
}

function trimdata(text) {
	var str = text.trim();

	if (str.indexOf('\n') == 0)
		str = str.substring(1);

	if (str.lastIndexOf('\n') == str.length-1)
		str = str.substring(0, str.length-2);

	return str;
}

/**
 * リストの更新
 */
window.reloadlist = SaigaiTask.Page.List.reloadlist = function() {
	$("#map-dialog").remove();
	$("#detail-dialog").remove();
	$("#addline-dialog").remove();
	// カルテのthe_geom
	$("#the_geom").remove();
	$('body').unbind("onsavegeom");
	$("body").unbind("oneditgeom");
	var cs = currentSort;
	if (cs=='') cs = '-1';
	else if (cs != undefined)
		cs = cs + ","+ currentSortAttr;
	// 時間パラメータの付与
	var time = SaigaiTask.PageURL.getTime();
	var timeParam = "";
	if(!!time) {
		var iso8601Time = time.toISOString();
		// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
		if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
			iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
		}
		timeParam = "time="+iso8601Time;
	}
	var d = new Date();
	reloadContent("list/listpage/"+SaigaiTask.Page.menutaskInfo.id+ "/"+SaigaiTask.Page.menuInfo.id+"/"+page+"/"+cs+"?"+d.getTime()+"&"+timeParam);
}


//履歴表示
function showHistoryData(listId) {
	// 表のタイトル
	var caption = $("caption", $("#"+listId));

	$.ajax({
		url: SaigaiTask.contextPath+"/page/generalizationHistory/list",
		method: "post",
		data: {
			pagetype: SaigaiTask.PageURL.params.pagetype,
			listid: listId
		},
		success: function(data, dataType) {
			if(!!data) {

				SaigaiTask.Page.historyDialog.dialog("close");

				//array = JSON.parse(data);
				var dialog = $("<div>");
				array = data;
				var num = array.length;

				if(num==0) {
					dialog.html(lang.__("Not yet save history"));
				}
				else {
					// table
					var table = $("<table>");
					table.attr("class", "tablesorter");

					// table header
					var thead = $("<thead>");
					thead.css("text-align", "center");
					table.append(thead);
					var theadTR = $("<tr>");
					thead.append(theadTR);
					//theadTR.append($("<td>ID</td>"));
					theadTR.append($("<td>" + lang.__("Registered date and time") + "</td>"));
					theadTR.append($("<td>CSV</td>"));
					theadTR.append($("<td>PDF</td>"));

					// table content
					for(var idx in array) {
						var info = array[idx];

						// URL構築
						var url = SaigaiTask.contextPath+"/page/generalizationHistory/download"
						+"?pagetype="+SaigaiTask.PageURL.params.pagetype
						+"&listid="+listId
						+"&id="+info.id;

						var tr = $("<tr>");
						table.append(tr);

						// id td
						//var td = $("<td>").html(info.id);
						//tr.append(td);

						// registtime td
						var td = $("<td>").html(info.registtime);
						tr.append(td);

						// csv td
						var td = $("<td>");
						if(!!info.csv) {
							var anchor = $("<a>CSV</a>");
							anchor.css("color", "blue");
							anchor.attr("href", url+"&suffix=csv");
							td.append(anchor);
						}
						else {
							td.append("CSV");
						}
						tr.append(td);

						// pdf td
						var td = $("<td>");
						if(!!info.pdf) {
							var anchor = $("<a>PDF</a>");
							anchor.css("color", "blue");
							anchor.attr("href", url+"&suffix=pdf");
							td.append(anchor);
						}
						else {
							td.append("PDF");
						}
						tr.append(td);
					}

					dialog.append(table);
				}

				var title = lang.__("History");
				if(0<caption.text().length) {
					title = lang.__("History of {0}.", caption.text());
				}

				dialog.dialog({
					title: title,
					minWidth: 450,
					maxHeight: 500,
					modal: true
				});
			}
		}
	});
}

/**
 * リスト画面よりアコーディオンの開閉状態を取得し SaigaiTask.PageURL に設定する
 */
SaigaiTask.Page.List.getAccordionHeaderState = function() {
	var headers = $('#editTable .group-header');

	if (headers.length > 0) {
		var initialOpen = $('#accordionopen').val() != 'false';
		var headerState = { open: [], close: [] };
		headers.each(function(i, header) {
			var value = $(header).find('span.group-name').text();
			// 初期状態と異るもののみ追加する
			if ($(header).hasClass('collapsed')) {
				if (initialOpen)
					headerState.close.push(value);
			}
			else {
				if (!initialOpen)
					headerState.open.push(value);
			}
		});
		SaigaiTask.PageURL.override({accordion: JSON.stringify(headerState)});
	}
};

/**
 * SaigaiTask.PageURL よりアコーディオンの開閉状態を取得し、リスト画面に設定する
 */
SaigaiTask.Page.List.setAccordionHeaderState = function() {
	var headers = $('#editTable .group-header');

	if (headers) {
		var headerState = SaigaiTask.PageURL.params.accordion;
		var initialOpen = $('#accordionopen').val() != 'false';
		var states = {};

		// 初期状態と異るものをハッシュ表にする
		if (headerState) {
			if (initialOpen)
				for (var i = 0; i < headerState.close.length; i++)
					states[headerState.close[i]] = true;
			else
				for (var i = 0; i < headerState.open.length; i++)
					states[headerState.open[i]] = true;
		}

		headers.each(function(i, header) {
			var open = initialOpen;
			if (states[$(header).find('span.group-name').text()]) {
				open = !initialOpen;
			}
			// 現在の状態と異っている場合に toggle する
			current = !$(header).hasClass('collapsed');
			if (open != current) {
				$(header).trigger('toggleGroup');
			}
		});
	}
};

/**
 * リスト画面よりアコーディオンの開閉状態を取得する
 */
SaigaiTask.Page.List.accordionHeaderInfo = function() {
	var headers = $('#editTable .group-header');
	var result = {};
	if (headers.length > 0) {
		headers.each(function(i, header) {
			var info = {
				open: !$(header).hasClass('collapsed'),
				count: $(header).find('span.group-count').text()
			};
			result[$(header).find('span.group-name').text()] = info;
		});
	}
	return result;
};
