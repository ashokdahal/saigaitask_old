<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<%-- リロードする必要の無い JavaScript, CSS は head.jsp に記載する. --%>

<script type="text/javascript">
var addline = false;
var idsArr = new Array();
var typesArr = new Array();
var currentSort;
var currentSortAttr;
//# sourceURL=listdebug.js

// 最終更新日を更新
SaigaiTask.setUpdateTime("${f:h(pageDto.updateTime)}");

$("body").on({
	"onsavegeom": function(e, wkt) {
		$('#map-dialog').dialog('close')
		$('<input>').attr({
			type: 'hidden',
			id: 'the_geom',
			name: '${table}:theGeom:${key}:0',
			value: wkt
		}).appendTo(document.addlineform);
	}
})

$(function() {

	SaigaiTask.Page.List({
		/**
		 * メタデータ
		 * @type {Object<String, String>} メタデータID, タイトル
		 */
		metadatas: JSON.parse('${fal:json(metadataMap)}')
	});

	var filterMap = ${fal:json(filterIds)};
	SaigaiTask.Page.Filter.size = ${fn:length(pageDto.menuInfo.filterInfoList)};
	SaigaiTask.Page.Filter.nofilter  = ${filterDto==null && 0<fn:length(pageDto.menuInfo.filterInfoList)};
	SaigaiTask.Page.Filter.nofilter |= ${filterDto!=null && filterDto.filterInfoId==0};
	SaigaiTask.Page.Filter.nofilter |= ${filterDto!=null && filterDto.conditionValue.has("nofilter") && filterDto.conditionValue.get("nofilter")};
	// フィルタ件数を取得
	var sum = 0;
	var filterNum = 0;
	for(var gid in filterMap) {
		var isFilter = (filterMap[gid]==false);
		if(isFilter) filterNum++;
		sum++;
	}

	// フィルタ件数の更新
	if(SaigaiTask.Page.Filter.nofilter) {
		SaigaiTask.Page.filter.getFilterNameEl().text("<%=lang.__("解除")%>");
		SaigaiTask.Page.filter.getFilterNumEl().text(" [ "+addFigure(${count})+"<%=lang.__("Items")%> ]");
		$("#filter-gray-ck,label[for=filter-gray-ck]").hide();
	}
	else {
		//SaigaiTask.Page.filter.getFilterNumEl().text(MessageFormat.format(" <%=lang.__("({0} items / {1} items)")%>", filterNum, sum));
		SaigaiTask.Page.filter.getFilterNumEl().text(" ( "+addFigure(${fn:length(filterDto.filteredFeatureIds)})+"<%=lang.__("Items")%> / "+addFigure(${filterDto.total})+"<%=lang.__("Items")%> )");
		$("#filter-gray-ck,label[for=filter-gray-ck]").show();
	}

		$("#data-num").text(" [ "+addFigure(${count})+"<%=lang.__("Items")%> ]");
	<c:if test="${filterDto.searchText != null}">
	$("#search-text").text("${filterDto.searchText}");
	$("#search-text").addClass("ui-widget-content");
	$("#search-text").css({'padding':'2px','margin-left':'3px'});
	</c:if>
	<c:if test="${filterDto.searchText == null}">
	$("#search-text").text("");
	$("#search-text").removeClass("ui-widget-content");
	$("#search-text").css({'padding':'0px','margin-left':'0px'});
	</c:if>
	$("#content_head").trigger("resize");

	//テーブルのカラムが増えた場合のoffset
	<%-- 合計セル出力用 --%>
	<fmt:parseNumber var="offset" value="0" integerOnly="true" />
	<fmt:parseNumber var="offset_tmp" value="0" integerOnly="true" />
	<c:if test="${!deletable && (key != 'gid' && key != '_orgid')}">
		<c:set var="offset" value="${offset_tmp+1}" />
		<c:set var="offset_tmp" value="${offset}" />
	</c:if>
	<%-- 削除フラグがある場合 --%>
	<c:if test="${deletable}">
		<c:set var="offset" value="${offset_tmp+1}" />
		<c:set var="offset_tmp" value="${offset}" />
	</c:if>
	<%--mapの場合 --%>
	<c:if test="${key == 'gid' || key == '_orgid'}">
		<c:set var="offset" value="${offset_tmp+1}" />
	</c:if>

	// テーブルヘッダー
	var headers = {};
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
	<c:if test="${!e.sortable}">
	headers[${offset+s.index}] = {sorter:false};
	</c:if>
	// 日付時刻がうまくソートされないため text を指定する
	<c:if test="${e.sortable && (editClass[s.index] == 'Date' || editClass[s.index] == 'DateTime')}">
	headers[${offset+s.index}] = {sorter:'text'};
	</c:if>
	</c:forEach>
	var offset = ${offset};
	for(var idx=0; idx<offset; idx++) {
		headers[idx] = {sorter:false};
	}

	// 固定のソートカラム（アコーディオン対象カラム）
	var sortForce = [];
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		<c:if test="${e.attrid == mtbl.accordionattrid}">
			sortForce.push([${s.index + offset}, 0]);
		</c:if>
	</c:forEach>

	// デフォルトソートリスト
	var sortList = [];
	var defsort = "${f:h(defsort)}";
	while(true) {
		if(0<defsort.length) {
			var elems = defsort.split(",");
			var row = elems[0];
			var order = elems[1]; // 0: asc, 1: desc
			var sort = [row, order];
			// ソート不可ならはずす
			if(!!headers[row]) {
				if(headers[row].sorter==false) {
					break;
				}
			}
			sortList.push(sort);
			currentSortAttr = elems[2];
		}
		break;
	}

	if (sortForce.length > 0) {
		// デフォルトソート項目にアコーディオン対象項目が指定されている場合はそちらを優先する
		if (sortList.length > 0 && sortList[0][0] == sortForce[0][0]) {
			sortForce[0][1] = Number(sortList[0][1]);
		}
		// 先頭にアコーディオン対象カラムを追加する
		else {
			sortList.unshift(sortForce[0]);
		}
	}

	// sortForce に何か指定しないと class="tablesorter-headerAsc/Desc" が設定されない？
	if (sortForce.length == 0 && 0<sortList.length) {
		sortForce = [[-1, 0]];
	}

	// TableSorter 初期化
	try {
		$("#editTable").tablesorter({
			widgets: [
				'zebra', 'stickyHeaders'
	<c:if test="${!empty mtbl.accordionattrid}">
				, 'group'
	</c:if>
			],
			widthFixed : true,
			headers: headers,
			// FIXME: リストのレコードが0件の場合に、デフォルトソート(sortList)を指定すると、
			//  “table.config.parsers[c] is undefined” のエラーが発生する
			//  2.15.5 へのアップデートで fix する予定
			sortList: sortList,
			sortForce: sortForce,
	<c:if test="${paging}">
			serverSideSorting:true,
	</c:if>
			widgetOptions: {
	<c:if test="${!empty mtbl.accordionattrid}">
				group_collapsible : true,
				group_count : ' ({num})',
	</c:if>
				stickyHeaders_attachTo: "#content_main"
			}
	    }).bind("sortEnd",
	            function(sorter) {
	        currentSort = sorter.target.config.sortList;
	        if (currentSort != -1 && Array.isArray(currentSort) && currentSort.length > 0) {
	        	if (currentSort.length > 1)
		        	currentSort = currentSort[1];
	        	else
	        		currentSort = currentSort[0];
	            currentSortAttr = sorter.target.config.headerList[currentSort[0]].id;
		        SaigaiTask.PageURL.override({
		        	sort: currentSort.join(",")
		        });
     		}
	        <c:if test="${paging}">
	        if (sortclick) {
	        	sortclick = false;
				var sorttmp = currentSort.join(",");
				var d = new Date();
				// 時間パラメータの付与
				var timeParam = SaigaiTask.PageURL.timeParam();
				$('#content').mask("Loading...");
				$('#tablebody').load('${f:url('/page/list/tablepagebodysort')}/${listForm.menutaskid}/${listForm.menuid}/'+page+'/'+sorttmp+','+currentSortAttr+'?'+d.getTime()+timeParam, function() {
					editTable();
					listupdate();
					$('#content').unmask();
				});
	        }
	        </c:if>
	        return false;
	    });
	}catch(e) {
		console.error("tablesorter init error", e);
	}

	var sortclick = false;
	$(".tablesorter-header").on("click", function(e) {
		sortclick = true;
	});

	// StickyHeader のスタイルを調整する
	$('#editTable-sticky').css('margin', '0 0 0 10px');

	// グルーピングヘッダの開閉状態を設定する
//	SaigaiTask.Page.List.setAccordionHeaderState();

});
var ismobile = false;
//for フィルタリング
var filterFunc;
$(function() {
	var agent = navigator.userAgent;
	if(agent.search(/iPhone/) != -1 || agent.search(/iPad/) != -1 || agent.search(/iPod/) != -1 || agent.search(/Android/) != -1){
        ismobile = true;
    }

	$('#editTable').editTable({
		callback : updateTable,
		comma : false,
		mobile : ismobile
	});

	//for 一括変更
	slimerDto = JSON.parse('${fal:json(slimerDto)}');
	// リロード時に前回の一括変更ダイアログがあった場合は破棄する
	if(!!window.slimers) {
		for(var idx in window.slimers) {
			var slimer = window.slimers[idx];
			slimer.destroy();
		}
	}
	//一括変更の初期化
	window.slimers = [];
	if(!!slimerDto.slimerInfos) {
		var first = true;
		for(var idx in slimerDto.slimerInfos) {
			var slimerInfo = slimerDto.slimerInfos[idx];
			slimer = new SaigaiTask.Edit.Slimer(slimerInfo);
			if(first) {
				first = false;
				SaigaiTask.Edit.slimer = slimer;
				SaigaiTask.Edit.slimer.dialog.attr("id", "slimer-dialog");
				SaigaiTask.Edit.slimer.dialog.bind("slimersuccess", function(ev) {
					//リストの更新
					SaigaiTask.Page.List.reloadlist();
				});
			}
			window.slimers.push(slimer);
		}
	}

	//for フィルタリング
	//var filterFunc;

	if($("tr.gray")){
		//$("tr.gray").find('select').attr("disabled", "disabled");
		//$("tr.gray").find('a').removeAttr("onclick");//onclickを不可に
		//$("tr.gray").find('a').css("cursor", "default");//カーソルをpointerにしない

		var filtering = ("${filtering}"=="true");
		// フィルター解除の場合にも表示させる（条件変更ボタンを押せるようにする）
		filtering |= 0<SaigaiTask.Page.Filter.size;
		//対象外データのグレー表示/非表示の切り替え
		if($("#filter-gray-ck") && filtering){
			//$("#filter-gray-ck").removeAttr("disabled");
			$("#filter-gray-ck-div").css("display","block");
			filterFunc = function(){
				//グレー表示
				if($("#filter-gray-ck").prop("checked")){
					//$("tr.gray").css("display", "table-row");
					$("tr.gray").css("display", "");
					//grayの行を非表示にすると、tablesorterの色分けが崩れるので、以下で制御
					//一旦、oddとevenクラスを全削除
					$("table#editTable tr").each(function(){
						$(this).removeClass('even');
						$(this).removeClass('odd');
					});
					//振り直し
					var idx=0;
					$("table#editTable tr").each(function(){
						if (!$(this).hasClass('sum') &&
							!$(this).hasClass('group-header')) { //グルーピングヘッダを飛ばす
							if(idx%2==1){
								$(this).addClass('odd');
							}else{
								$(this).addClass('even');
							}
							idx++;
						}
					});
					SaigaiTask.PageURL.override({filtergray : "true"});
				//非表示
				}else{
					$("tr.gray").css("display", "none");
					//grayの行を非表示にすると、tablesorterの色分けが崩れるので、以下で制御
					//一旦、oddとevenクラスを全削除
					$("table#editTable tr").each(function(){
						$(this).removeClass('even');
						$(this).removeClass('odd');
					});
					//振り直し
					var idx=0;
					$("table#editTable tr").each(function(){
						if (!$(this).hasClass('sum') && !$(this).hasClass("gray") && //grayクラスは飛ばす
							!$(this).hasClass('group-header')) { //グルーピングヘッダを飛ばす
							if(idx%2==1){
								$(this).addClass('odd');
							}else{
								$(this).addClass('even');
							}
							idx++;
						}
					});
					SaigaiTask.PageURL.override({filtergray : "false"});
				}
			//});
			}
			$("#filter-gray-ck").change(function(){
				filterFunc();
			});
		}else{
			//$("#filter-gray-ck").attr("disabled", "disabled");
			$("#filter-gray-ck-div").css("display", "none");
			if (${!paging})
				$("#data-num").css("display", "inline");
		}
	}

	//情報の登録ボタン表示のOn/Off
	var addable = "${addable}"=="true";
	if(!addable){
		disableButton("#add-button",BUTTON_MODE_ONCLICK);
	}

	//一括変更ボタン表示のOn/Off
	if(idsArr.length==0 || ${pageDto.viewMode}){
		disableButton("#slimer-button",BUTTON_MODE_CLICK);
	}

	//グレー表示On/Off
	if($("#filter-gray-ck") && filterFunc!=undefined){
		var filtergray = "${listForm.filtergray}";
		if (${filterDto.filterstateid!=FilterState.FILTEROFF}) {
		if(filtergray == "false"){
			$("#filter-gray-ck").attr("checked", false );
		}else{
			$("#filter-gray-ck").attr("checked", true );
		}
		}
		if (${filterDto.filterstateid==FilterState.FILTEROFF}) {
			$("#filter-gray-ck").attr("checked", false );
			$("#filter-gray-ck").attr("disabled", true );
		}
		else {
			$("#filter-gray-ck").attr("disabled", false );
		}
		filterFunc();
	}

	//ボタンのスタイル設定
	$(".dialog-button").button();
	$("#dialog-button-list li").css("margin", "5px").css("float", "left").css("list-style", "none");

	// 意思決定支援モードなら推定避難者数に応じてリスト表示を行う
	if(SaigaiTask.PageURL.initParams.decisionsupport){
		SaigaiTask.Page.decisionSupport = new SaigaiTask.DecisionSupport("list");
	}

});

function onSelStyleChange(id, obj, tag) {
	var val = obj.options[obj.selectedIndex].value;
	onStyleChange(id, obj, tag, val);
}
function onCheckStyleChange(id, obj, tag) {
	var val = obj.value;
	if (obj.checked==false) val = '';
	onStyleChange(id, obj, tag, val);
}
function onStyleChange(id, obj, tag, val) {
	var td = $(obj).parent();
<c:forEach var="e" varStatus="s" items="${colinfoItems}">
	<c:if test="${e.editable && e.id!=null}">
	if (id == ${e.id}) {
	<c:if test="${styleMap[e.id] != null}">
	<c:forEach var="f" varStatus="t" items="${styleMap[e.id]}">
		if (val == "${f.val}")
			td.attr("style","${f:h(f.style)}");
		else
	</c:forEach>
			td.removeAttr('style');
	</c:if>
		var tag2 = tag;
		if (tag.indexOf('#') == 0)
			var tag2 = tag.substring(1);
		$(document.getElementById(tag2)).text(val);
		//$(tag).text(val);
	}
	</c:if>
</c:forEach>
}

function updateTable() {
	$('#editTable').trigger("update");
}

//nullチェック用関数、jquery.edit-table.js内でcallされる
function ckNull(id, val){
	if(id!=undefined){
		var idData = id.split(":");
		var ckCols = new Array(
			<c:forEach var="e" varStatus="s" items="${colMap}">
				<c:if test="${e.value}">
					"${f:h(e.key)}",
				</c:if>
			</c:forEach>
			""//ダミーのnullデータ
		);
		for(var i=0; i<ckCols.length-1; i++){
			if(ckCols[i]==idData[1] && (val==null || val.length<1 || val=="null")){
				return false;
			}
		}
	}
	return true;
}

function ckVal(id, val) {
	if (id!=undefined) {

	}
}

//変更登録実行時のnullチェック
function ckNullAdd(){
	var ckIdStr = new Array(
		//jQueryのidとして使う場合、idにピリオド(.)やコロン(:)を用いる場合は、バックスラッシュ(\\)でエスケープする！
		<c:forEach var="e" varStatus="s" items="${colMap}">
			<c:if test="${e.value}">
				"${table}\\:${e.key}\\:${key}\\:0",
			</c:if>
		</c:forEach>
		""//ダミーのnullデータ
	);
	for( i=0; i<ckIdStr.length-1; i++){
		var val = $("#"+ckIdStr[i]).html();
		//alert(val);
		if(val=="" || val=="&nbsp;"){
			alert("<%=lang._E("There is no value in the required fields!\n Please enter a value.")%>");
			return false;
		}
	}
	return true;
}

//変更時のnullチェック
function ckNullEdit(dataArray){
	for(var idx in dataArray) {
		var saveData = dataArray[idx];
		if (!ckNull(saveData[0], saveData[1])) {
			alert("<%=lang._E("There is no value in the required fields!\n Please enter a value.")%>");
			return false;
		}
	}
	return true;
}

function saveData() {
	// 時間指定中なら更新禁止とする
	if(!!SaigaiTask.PageURL.getTime()) {
		alert("<%=lang.__("Can't update during time set.")%>");
		return;
	}

	$('body').trigger("click");
	var dataArray = $('#editTable').getSaveData();
	if (dataArray.length == 0) {
		if (addline) alert("<%=lang.__("Enter value.")%>");
		else alert("<%=lang.__("Target update data not found.")%>");
		return;
	}

	//新規追加データのnullチェック
	if(!(ckNullAdd()))return;
	if (!ckNullEdit(dataArray)) return;

	if (!confirm('<%=lang.__("Are you sure to save?<!--2-->")%>')) return;

	var url = "${f:url('/page/list/update/')}";
	// 送信データを生成
	var data = {
		saveDatas: []
	};
	for(var idx in dataArray) {
		var saveData = dataArray[idx];
		data.saveDatas.push({
			id: saveData[0],
			value: saveData[1]
		});
	}

	// 変更登録
	$.ajax({
		dataType: "json",
		type: "POST",
		cache: false,
		url: url,
		data: SaigaiTask.Edit.toSAStrutsParam(data),
		success: function(result) {
			if(!result.success) {
				alert("ERROR: "+(!!result.msg?result.msg:""));
				return;
			}

			$('#editTable').clearSaveData();

			var features = result.features;

			//ファイルがあればアップロード
			if ($('#editTable').countSaveFiles() > 0) {
				// 会議録等のシステムテーブルへのアップロード処理
				saveFiles(features[0].id);

			}
			else {
				//document.location.reload();
				//$('#editTable').clearEvent();
				// リストの更新
				SaigaiTask.Page.List.reloadlist();
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("<%=lang.__("Error : Failed to register")%>");
		}
	});

	if (addline) {//追加の場合
		/*var tr = $("#editTable tr");
		var cells = tr.eq(1).children();
		for( var j=0,m=cells.length;j<m;j++ ){
			var cell = cells.eq(j);
			cell.removeClass("highlight");
		}
		$('#editTable').trigger("update");
		*/
		addline = false;
	}

}

/**
 * 会議録等のシステムテーブルへのファイルアップロード処理
 * @param fid : gidの事
 */
function saveFiles(fid) {
	var $fdata, id;
	var fileArray = $('#editTable').getSaveFiles();
	for(var idx in fileArray) {
		var file = fileArray[idx];
		$fdata = file[1];
		id = file[0];
	}
	// IE8はFormDataを利用出来ないので自作して送信
	var actionurl = "${f:url('/page/list/upload/')}";

	// ファイルアップロード用のformを作成
	/*var forms = $("#fileupload");
	forms.attr('action',  actionurl);
//	$("<input>", {type: 'hidden',name:'session_token', id: 'session_token',value:'${cookie.JSESSIONID.value}'}).appendTo('#fileupload');
	$('<input>').attr({"type":"hidden","name":"session_token","value":'${cookie.JSESSIONID.value}'}).appendTo(forms);

	// アップロード予定のファイルをformFileにリネームして登録(1ファイルのみ)
	$fdata.attr('name','formFile');
	forms.append($fdata);
	// その他必要なパラメータを追加
	$('<input>').attr({"type":"hidden","name":"id","value":id}).appendTo(forms);
	$('<input>').attr({"type":"hidden","name":"value","value":fid}).appendTo(forms);

	forms.submit();
	*/


	//var url = "./list/upload/";

	var $fdata, id;
	var fileArray = $('#editTable').getSaveFiles();
	for(var idx in fileArray) {
		var file = fileArray[idx];
		$fdata = file[1];
		id = file[0];
	}

	//TODO:現状１ファイルにしか対応してない。
	var fd = new FormData();
	fd.append('formFile', $fdata.prop("files")[0]);
	fd.append('id', id);
	fd.append('value', fid);
	$.ajax({
		type: 'post',
		processData: false,
		contentType: false,
		url: actionurl,
		data: fd,
		dataType: 'html',
		success: function(data){
			$('#editTable').clearSaveData();
			$('#editTable').clearSaveFiles();
			//document.location.reload();
			var cs = currentSort;
			if (currentSort == undefined || currentSort == -1 || currentSort.length == 0)
			//if (cs=='') 
				cs = '-1';
			reloadContent("list/listpage/${listForm.menutaskid}/${listForm.menuid}/0/"+cs);
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("upload error : "+textStatus);
		}
	});
}

function saveFilesAfter(){
	$("#fileupload").empty();

	$('#editTable').clearSaveData();
	$('#editTable').clearSaveFiles();
	//document.location.reload();
	// リストの更新
	SaigaiTask.Page.List.reloadlist();
}

<c:choose>
<c:when test="${key == 'id'}">
/**
 * システムテーブルの追加登録
 */
function addLine() {
	if (addline) return;
	// 時間指定中なら登録禁止とする
	if(!!SaigaiTask.PageURL.getTime()) {
		alert("<%=lang.__("Can't register during time set.")%>");
		return;
	}

	var line = "<tr id='newline'>";
	//合計セル出力用
	<c:if test="${!deletable && key != 'gid'}">
		line += "<td></td>";
	</c:if>
	//削除フラグがある場合
	<c:if test="${deletable}">
		line += "<td></td>";
	</c:if>
	//mapの場合
	<c:if test="${key == 'gid'}">
		line += "<td></td>";
	</c:if>
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
	line += "<td id=\"${table}:${e.attrid}:${key}:0\" class=\"${editClass[s.index]} highlight\">";
	<c:if test="${editClass[s.index] == 'String' || editClass[s.index] == 'TextArea' || editClass[s.index] == 'Number' || editClass[s.index] == 'Float' || editClass[s.index] == 'Date' || editClass[s.index] == 'DateTime'}">
	line += "&nbsp;";
	</c:if>
	<c:if test="${editClass[s.index] == 'Select'}">
	line += "<select>";
		<c:if test="${!colMap[e.attrid] && selectVal[e.attrid][0]!=''}">
		line += '<option value=""></option>';
		</c:if>
		<c:forEach var="g" varStatus="u" items="${selectStr[e.attrid]}">
		<c:set var="g2" value="${selectVal[e.attrid]}" />
		line += "<option value=\"${f:h(g2[u.index])}\">${f:h(g)}</option>";
		</c:forEach>
	line += "</select>";
	</c:if>
	<c:if test="${editClass[s.index] == 'Checkbox'}">
	line += "<input type=\"checkbox\" value=\"${f:h(checkStr[e.attrid])}\">";
	</c:if>
	<c:if test="${editClass[s.index] == 'Upload'}">
	//line += "<form id=\"upform\" enctype=\"multipart/form-data\"><input id=\"newfile\" name=\"formFiles\" type=\"file\" ></form>";
	line += "<input id=\"newfile\" name=\"formFiles\" type=\"file\" >";
	</c:if>
	line += "</td>";
</c:forEach>
	line += "</tr>";
	$('#editTable').clearEvent();
	$('#editTable').prepend(line);
    $('#editTable').editTable({
    	callback : updateTable,
    	comma : false,
    	mobile : ismobile
    });
	//コンボボックスの選択
<c:forEach var="e" varStatus="s" items="${colinfoItems}">
	<c:if test="${editClass[s.index] == 'Select'}">
	$('#editTable').saveData('${table}:${e.attrid}:${key}:0', '${f:h(selectVal[e.attrid][0])}');
	</c:if>
</c:forEach>
	// 会議録で編集するたびに新規登録行がずれるので、addLine後に trigger("update") するとずれなくなる。
	// table=$("#editTable"); newline=$("#newline", table); table.prepend(newline); var resort = false; $("table").trigger("update", [resort]);
	var resort = false; $('#editTable').trigger("update", [resort]);

    addline = true;
}
</c:when>
<c:when test="${key == 'gid' || key == '_orgid'}">
/**
 * mapの追加登録
 */
function addLine() {
	// 時間指定中なら登録禁止とする
	if(!!SaigaiTask.PageURL.getTime()) {
		alert("<%=lang.__("Can't register during time set.")%>");
		return;
	}

	$("#addline-dialog").load("${f:url('/page/list/detail/')}${listForm.menutaskid}/${listForm.menuid}/-1", /*data*/null, /*callback*/function() {
		var addlineDialog = $(this);
		addlineDialog.dialog({
			modal: true,
			autoOpen: false,
			width: "auto",
			maxWidth: 800,
			maxHeight: 600,//for 720px
			buttons: {
				"<%=lang.__("Save")%>": function () {
					if (!confirm("<%=lang.__("Are you sure to save?<!--2-->")%>")) return;
					SaigaiTask.Detail.saveDetailData({
						menuid: ${listForm.menuid},
						form: document.addlineform,
						token: '${cookie.JSESSIONID.value}',
						dialog: addlineDialog
					});
				},
				<c:if test="${key=='gid' || key=='_orgid'}">
				"<%=lang.__("Map display")%>": function () {
					$("#the_geom").remove();
					// 登録フォームの有無
					var insertFeature = '';
					// 住所
					var address = '';
					// 中心位置
					var center = '';
					// ポップアップ
					var popup = '';
					// wkt
					var wkt = '';
					var layerId = '';
					// 編集可であれば登録フォームを表示
					<c:if test="${editable}">
						insertFeature = '&insertFeature=true';
					</c:if>
					// 編集可でなければポップアップを表示
					<c:if test="${!editable}">
						if(typeof document.detailform == "undefined" || !(!!document.detailform) || document.detailform.gid != 0)
							insertFeature = '&insertFeature=true';
						else popup = '&popup=${table}.'+document.detailform.gid.value;
					</c:if>
					<c:if test="${e.theGeom != null}">
						var theGeom = "${e.theGeom}";
						wkt = "&drawGeometrywkt=${e.wkt}";
						center = '&center='+theGeom.slice(theGeom.indexOf("(")+1,theGeom.indexOf(")"));
					</c:if>
					// 住所項目が登録されている場合addressに住所を格納
					<c:if test="${!empty master.addresscolumn}">
						var readaddress = $("[name*=':${master.addresscolumn}:']");
						if(readaddress.length == 1){
							address = '&address=' + readaddress[0].value;
						}
					</c:if>
					layerId = '&layerId=${table}';
					//var url = 'map/content/?menuid=${menuid}&menutaskid=${menutaskid}&legendCollapsed=true&drawGeometry=true&drawGeometryfid=${gid}'+insertFeature+center+wkt+popup+address+layerId;
					var url = '${f:url('/page/map/content/')}?menuid=${listForm.menuid}&menutaskid=${listForm.menutaskid}&legendCollapsed=true&drawGeometry=true&drawGeometryfid=${gid}'+insertFeature+center+wkt+popup+address+layerId;
					// 時間パラメータの付与
					var time = SaigaiTask.PageURL.getTime();
					if(!!time) {
						var iso8601Time = time.toISOString();
						// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
						if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
							iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
						}
						url += "&time="+iso8601Time;
					}
					SaigaiTask.Detail.showMap(url);
				},
				</c:if>
				"<%=lang.__("Close")%>": function () {
					addlineDialog.dialog("close");
				}
			}
		});
		$("#addline-dialog").dialog("open");
	});
}
</c:when>
</c:choose>
//指定した「レイヤ名.gid」のフィーチャをポップアップして地図表示
function openMap(gid){
	SaigaiTask.PageURL.override({pagetype : "map", popup : gid }).move();
}

$(function() {
	//ファイルアップロード
	$('#file-dialog') . dialog( {
		width: 460,
        autoOpen: false
    } );
});

// ecommap用のファイルアップロードダイアログ
function openFileDialog(id, orgid) {
	$('#upfile').val('');
	$('#file-dialog').dialog( 'open' );
	$('#upgid').val(id);
	$('#orgid').val(orgid);

	var layerid = $('[name=layerid]').val();
	var gid = $('#upgid').val();
	var session_token = $('[name=session_token]').val();
	var url = "./map/ecommap/contents/get?layer="+layerid+"&fid="+orgid+"&session_token=" + session_token;
	// 時間パラメータの付与
	var time = SaigaiTask.PageURL.getTime();
	if(!!time) {
		var iso8601Time = time.toISOString();
		// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
		if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
			iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
		}
		url += "&time="+iso8601Time;
	}
	$.ajax({
		dataType: "json",
		type: "GET",
		cache: false,
		url: url,
		success: function(msg) {
			var files = msg.files;
			$('#photoarea').empty();

			for(var i = 0;i < files.length;i+=2){
				(function(fileURL, title){
					// 画像URLのサーバ名を動的に取得する
					var server = ecommapURL+'/..';
					// 画像URL
					if(fileURL.indexOf('http')==-1) fileURL = server + fileURL;

					// サムネイル画像
					var url = fileURL;
					var isImage = true;
					var ext = FalUtil.getFileExt(url);
					var thumbnail = url;
					// 画像以外のファイルの場合はアイコン表示
					if (! ext.match(/jpg|gif|png|jpeg|file/)) {
						thumbnail = ecommapURL+"/map/fileicons/"+ext+".png";
						isImage = false;
					}

					if(isImage) {
						var photostyle = '<div style="float:left; margin: 5px; width:120px; height:150px"><a href="'+fileURL+'" data-lightbox="photo" data-title="'+title+'" ><img src="'+fileURL+'" style="max-widtg:80px;max-height:80px"/></a><p style="word-wrap:break-word">'+files[i+1]+'</p>';
						if(${editable}) photostyle += '<input type="button" value="<%=lang.__("Delete")%>" onClick="photodelete(this)" id=delete'+i+' /></div>';
						$("#photoarea").append(photostyle);
					}
					else {
						var div = '<div style="float:left; margin: 5px; width:120px; height:150px" id=file'+i+'>';
						var img = '<img src="'+thumbnail+'" style="max-widtg:150px;max-height:150px; cursor:pointer;"/><p>'+title+'<p>';
						img = $(img);
						img.click(function(){
							//img.src = "/map/map/fileicons/"+ext+".png";
							FalUtil.downloadFile({
								url: url
							});
						});
						$("#photoarea").append(div);
						$("#file"+i).append(img);
						if(${editable}){
							var deletebutton = '<input type="button" value="<%=lang.__("Delete")%>" onClick="photodelete(this)" id=delete'+i+' /></div>';
							$("#file"+i).append(deletebutton);
						}
					}
				})(files[i], files[i+1]);

			}
		}
	});

}

$(function() {
	$('#detail-dialog').dialog({
		modal: true,
		autoOpen: false,
		width: "auto",
		maxWidth: 800,
		maxHeight: 600,//for 720px
		buttons: {
		}
	});
	listeditbutton();
});

function listeditbutton() {
	$(".listphotobtn").on("click", function(e) {
		e.preventDefault();
		var date = new Date();
		$("#detail-dialog").html("");
		$("#detail-dialog").dialog("option", "title", "<%=lang.__("Detail information")%>").dialog("open");
		var url = this.href+"?"+date.getTime();
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			url+="&time="+iso8601Time;
			var table = "${table}";
			url+="&layerid="+table;
		}
		$("#detail-dialog").load(url, function() {
			$(this).dialog("option", "position", "{ my: 'center', at: 'center', of: 'window'}");
		});
	});
}

//CSVファイル出力
function outputCSV(form){

	var filter = false;
	if (form.filtergray != undefined) filter = !form.filtergray.checked;
	var pageall = false;
	if (form.pageall != undefined) pageall = form.pageall.checked;
	var csvStr = "";
	//ここでテーブル表示の内容と同じループを回して、CSVの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	if (${!paging} || !pageall) {
		var datas = SaigaiTask.Page.List.getDatas({doublequote:true, filter:true});
		//二重配列に格納したデータをカンマ区切りで整形、改行は「<br>」で代用して、パラメータ文字列に整形
		var csvStr = "";
		for(var i=0; i<datas.length; i++){
			for(var j=0; j<datas[i].length; j++){
				//alert(datas[i].length);
				if (datas[i][j] instanceof Object) {
					//今のところアコーディオンだけ
					csvStr += datas[i][j].value+" "+datas[i][j].count;
				}
				else
					csvStr += datas[i][j];
				if(j<datas[i].length-1){
					csvStr += ",";
				}
			}
			csvStr += "<br>";
		}
	}
	//formのPOSTで文字列を送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('./list/outputcsv/')}";
		form.method = "POST";
		$('<input>').attr({"type":"hidden","name":"session_token","value":'${cookie.JSESSIONID.value}'}).appendTo(form);
		// 表示時間パラメータを追加
		var time = SaigaiTask.PageURL.getTime();
		var timeInput = $("input[name='time']", form);
		if(time!=null) {
			time.setMilliseconds(0); // ミリ秒切捨て
			//var timeStr = time.toLocaleDateString()+" "+time.toLocaleTimeString();
			var timeStr = SaigaiTask.Page.Timeslider.formatDate(time, {
				dateSplit: "",
				datetimeSplit: "",
				timeSplit: ""
			});
			if(timeInput.length==0) {
				$('<input>').attr({"type":"hidden","name":"time","value":timeStr}).appendTo(form);
			}
			else timeInput.val(timeStr);
		}
		else {
			timeInput.remove();
		}
		form.value.value = csvStr;
		SaigaiTask.PageURL.download(function(){form.submit()});
	}while(0);
	$("#csv-dialog").dialog("close");
}

//PDFファイル出力
function outputPDF(form){

	var filter = false;
	if (form.filtergray != undefined) filter = !form.filtergray.checked;
	var pageall = false;
	if (form.pageall != undefined) pageall = form.pageall.checked;
	//ここでテーブル表示の内容と同じループを回して、PDFの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	var datas = [], frows = [];
	if (${!paging} || !pageall) {
		datas = SaigaiTask.Page.List.getDatas({filter:true});
		//frows = SaigaiTask.Page.List.getFilterRow();
		//if (filter)
		//	frows = [];
	}

	//formのPOSTで文字列をJSON形式で送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('./list/outputpdf/')}";
		form.method = "POST";
		$('<input>').attr({"type":"hidden","name":"session_token","value":'${cookie.JSESSIONID.value}'}).appendTo(form);
		// 時間パラメータを追加
		{
			// 印刷日時パラメータを追加
			// [例] 2016/10/11 10:37 印刷
			var printTime = new Date();
			printTime.setMilliseconds(0); // ミリ秒切捨て
			printTime.setSeconds(0); // 秒切捨て
			var printTimeStr = SaigaiTask.Page.Timeslider.formatDate(printTime, {
				dateSplit: "/",
				datetimeSplit: " ",
				timeSplit: ":"
			});
			printTimeStr += " <%=lang.__("Print")%>";

			// 表示時間パラメータを追加
			// [例] 2016/10/01 00:00 時点
			var time = SaigaiTask.PageURL.getTime();
			if(time!=null) {
				time.setMilliseconds(0); // ミリ秒切捨て
				var timeStr = SaigaiTask.Page.Timeslider.formatDate(time, {
					dateSplit: "/",
					datetimeSplit: " ",
					timeSplit: ":"
				});
				timeStr += " <%=lang.__("As of")%>"
				printTimeStr += "    "+timeStr;
			}

			// 時間パラメータを追加
			var timeInput = $("input[name='time']", form);
			if(timeInput.length==0) {
				$('<input>').attr({"type":"hidden","name":"printtime","value":printTimeStr}).appendTo(form);
			}
			else timeInput.val(printTimeStr);
		}
		form.dataList.value = JSON.stringify(datas);
		//form.filterrowList.value = JSON.stringify(frows);
		SaigaiTask.PageURL.download(function(){form.submit()});
	}while(0);
	$("#pdf-dialog").dialog("close");
}

$(function() {
	$('#search-dialog').dialog({
		modal: true,
		autoOpen: false,
		width: 600,
		maxHeight: $(document).height()-20,
		buttons: {
			/*"閉じる": function() {
				$(this).dialog("close");
				return false;
			}*/
		}
	});
});

function openSearchDialog() {
	$("#search-dialog").html("<%=lang.__("reading")%>");
	$('#search-dialog').dialog( 'open' );
	var opensearchURL = '${f:url('/page/list/opensearch')}/${listForm.menutaskid}/${listForm.menuid}/list?';
	if(typeof window.opensearchall!="undefined") {
		opensearchURL += "opensearchall="+window.opensearchall;
	}
	$('#search-dialog').load(opensearchURL, function() {
		$(this).dialog("option", "position", "{ my: 'center', at: 'center', of: 'window'}");
	});
}

$(function() {
	$('#history-dialog').dialog({
		modal: true,
		autoOpen: false,
		width: 1000,
		maxWidth: 1200,
		maxHeight: $(window).height()*0.9,//for 720px
		buttons: {
			"<%=lang.__("Close")%>": function() {
				$(this).dialog("close");
				return false;
			}
		}
	});
	historybutton();
});

function historybutton() {
	$(".historybtn").on("click", function(e) {
		e.preventDefault();
		$("#history-dialog").html("");
		$("#history-dialog").dialog("option", "title", "<%=lang.__("History display")%>").dialog("open");
		// 時間パラメータの付与
		var time = SaigaiTask.PageURL.getTime();
		var iso8601Time = null;
		if(!!time) {
			iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
		}
		var date = new Date();
		$("#history-dialog").load(this.href+"?"+date.getTime()+(iso8601Time!=null?"&time="+iso8601Time:""), function() {
			$(this).dialog("option", "position", "{ my: 'center', at: 'center', of: 'window'}");
		});
	});
}

function setTags(oids, val) {
	//オートコンプリート
	var idx = oids.indexOf(':');
	var idx2 = oids.indexOf(':', idx+1);
	var layerid = oids.substring(0, idx);
	var attrid = oids.substring(idx+1, idx2);
	//var availableTags = "${autocompleteDataMap[attrid]}";
	//$( "#inputNow" ).autocomplete({source: availableTags,minLength: 0});
	$( "#inputNow" ).autocomplete({source: "tags/"+layerid+"/"+attrid,minLength: 0});
	//$( "#inputNow" ).autocomplete( "search", val );
}

$(function() {
	showtips();

	//ページングボタンの処理
	var nav = $('#pagingnav');
	var offset = nav.offset();
	$("#content_main").scroll(function () {
		if($("#content_main").scrollTop() > /*offset.top*/0) {
			nav.addClass('fixed');
			nav.addClass('ui-widget-content');
			var btm = $("#content_main").css('bottom');
			nav.css({bottom:btm});
		} else {
			nav.removeClass('fixed');
			nav.removeClass('ui-widget-content');
			nav.css({bottom:'auto'});
		}
	});
});

function showtips() {
	$(".showtip").hover(
		function(){
			var $this = $(this);
			if (this.offsetWidth < this.scrollWidth) {
				var tabletop = $("#content_main").scrollTop();
				var tableleft = $("#content_main").scrollLeft();
			    var abc = $(this).text();
			    if (abc != "") {
			        var pos = $(this).position();
			        var postop = pos.top+tabletop;
			        var posleft = pos.left+tableleft;
			        $("#tooltip").text(abc).css({"display":"block","top":postop,"left":posleft});
			    	$("#tooltip").dblclick(function(){
			    		$(this).css("display","none");
			    		$this.dblclick();
			    	});
			    }
			    else {
			    }
			}
			else
		        $("#tooltip").text(abc).css("display","none");
			}
		);
}

function reloadlist() {
	//リストの更新
	SaigaiTask.Page.List.reloadlist();
}

function reload(){
	SaigaiTask.PageURL.updateSummary();
}
</script>
<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>
<c:if test="${paging}">
	<div id="pagingnav" style="text-align:left;">
	<a id="pagestart" class="pagestart ui-button ui-widget ui-state-disabled ui-corner-all">
		<span class="ui-icon ui-icon-arrowthickstop-1-w" title="<%=lang.__("First")%>"></span></a>
	<a id="pageprev" class="pageprev ui-button ui-widget ui-state-disabled ui-corner-all">
		<span class="ui-icon ui-icon-arrowthick-1-w" title="<%=lang.__("Return")%>"></span></a>
	<a id="pagenext" class="pagenext ui-button ui-widget ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-arrowthick-1-e" title="<%=lang.__("Next")%>"></span></a>
	<a id="pageend" class="pageend ui-button ui-widget ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-arrowthickstop-1-e" title="<%=lang.__("Last")%>"></span></a>
	&nbsp;
	<span style="color:gray;" class="paging-num">(${count} <%=lang.__("Items")%>)</span>
	</div>
</c:if>
<table border="0" cellpadding="3" cellspacing="2" id="editTable" class="tablesorter shortline" style="margin-left:10px;">
	<thead>
	<tr>
		<%-- 合計セル出力用 --%>
		<c:if test="${!deletable && (key != 'gid' && key != '_orgid')}">
			<th class=""></th>
		</c:if>
		<%-- 削除フラグがある場合 --%>
		<c:if test="${deletable}">
			<th class="noout"><%=lang.__("Delete")%></th>
		</c:if>
		<%--mapの場合 --%>
		<c:if test="${key == 'gid' || key == '_orgid'}">
			<th></th>
		</c:if>
		<%-- テーブルヘッダ --%>
		<c:forEach var="e" varStatus="s" items="${colinfoItems}">
			<c:set var="hi" value="" />
			<c:if test="${e.highlight}"><c:set var="hi" value="highlight" /></c:if>
			<c:set var="gr" value="" />
			<c:if test="${e.attrid == mtbl.accordionattrid}">
				<%--
					グルーピングはいくつか指定可能。
					もともと group-number で数字でグルーピングになっていたが、
					17/10/20要望により、 group-text に変更
					@see https://mottie.github.io/tablesorter/docs/example-widget-grouping.html#header_class_names
					--%>
				<%-- <c:set var="gr" value=" group-number" />--%>
				<c:set var="gr" value=" group-text" />
			</c:if>
			<th class="${hi}${gr}"  id="${e.attrid}">
				${f:h(e.name)}<c:if test="${colMap[e.attrid] && e.editable}"><font style="color:FF0000"> ※</font></c:if>
			</th>
		</c:forEach>
	</tr>
	<c:if test="${totalable}">
	<tr>
		<%-- 値の合計 --%>
		<c:if test="${!deletable && (key != 'gid' && key != '_orgid')}">
			<td class="sum"><%=lang.__("Total<!--2-->")%></td>
		</c:if>
		<c:if test="${deletable}">
			<td class="sum noout"></td>
		</c:if>

		<c:if test="${key == 'gid' || key == '_orgid'}">
			<c:choose>
			<c:when test="${deletable}">
				<td class="sum"><%=lang.__("Total<!--2-->")%></td>
			</c:when>
			<c:otherwise>
				<td class="sum"><c:if test="${!deletable}"><%=lang.__("Total<!--2-->")%></c:if></td>
			</c:otherwise>
			</c:choose>
		</c:if>

	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		<c:set var="gr" value="" />
		<c:if test="${e.attrid == mtbl.accordionattrid}">
			<%-- <c:set var="gr" value=" group-number" />--%>
			<c:set var="gr" value=" group-text" />
		</c:if>
		<td class="sum${gr}">${f:h(sumItems[e.name])}</td>
	</c:forEach>
	</tr>
	</c:if>
	</thead>

	<tbody id="tablebody">
<c:forEach var="e" varStatus="s" items="${result}">
	<c:catch var="_orgidException"><c:set var="orgid" value="${e._orgid}"/></c:catch> <%-- _orgid があるかチェック（プロパティがないと例外発生なので一旦キャッチする） --%>
	<%-- 意思決定支援 --%>
	<c:if test="${pageDto.isDecisionSupport()}">
		<c:if test="${!decisionIds[f:h(e[key])]}">
			<tr class="${filterClasses[f:h(e[key])]}" style="display:table-row;" >
		</c:if>
		<c:if test="${decisionIds[f:h(e[key])]}">
			<tr>
		</c:if>
	</c:if>
	<c:if test="${!pageDto.isDecisionSupport()}">
		<c:if test="${!filterIds[f:h(e[key])]}">
			<tr class="${filterClasses[f:h(e[key])]}" style="display:table-row;" >
		</c:if>
		<c:if test="${filterIds[f:h(e[key])]}">
			<tr>
		</c:if>
	</c:if>

		<c:if test="${!deletable && (key != 'gid' && key != '_orgid')}">
			<td></td>
		</c:if>

		<c:if test="${deletable}">
			<td width="30px" id="${table}:delete:${key}:${not empty _orgidException ? e[key] : e._orgid}" class="Checkbox noout" >
				<input type="checkbox" name="delete:${not empty _orgidException ? e[key] : e._orgid}" value="${not empty _orgidException ? e[key] : e._orgid}" />
			</td>
		</c:if>

		<%--mapの場合 --%>
		<c:if test="${key == 'gid' || key == '_orgid'}">
			<c:choose>
				<c:when test="${listForm.summarylist}">
					<td>
				</c:when>
			<c:otherwise>
			<td id="mapbtn" width="${pageDto.pagetoggleButton!=null && pageDto.pagetoggleButton.first=='map'?'120px':'80px'}"><%--ボタン１つで 40px; 常に２つ表示 --%>
				<input type="hidden" name="layerid" value="${table}"/>
				<input type="hidden" name="gid" value="${not empty _orgidException ? e.gid : e._orgid}"/>
				<c:if test="${firsttable}"><%-- 最初のテーブルじゃないと動作しないボタンのため１件目のテーブルかチェックする --%>
				<%-- <input type="hidden" name="the_geom" id="thegeom" value="${e.theGeom}"/> --%>
				<c:if test="${pageDto.pagetoggleButton!=null && pageDto.pagetoggleButton.first=='map'}">
				<a id="listmapbtn" href="#" class="btn_icon ${not empty e.theGeom ? 'map-btn blue' : 'icon-gray'}" style="font-weight:normal;">🌏</a>
				</c:if>
				<%-- <c:if test="${editable}"> --%>
				<%-- <a id="listphotobtn" href="#" onclick="openFileDialog(${e[key]});" class="btn_icon ${true ? 'blue' : 'lgray'}" style="font-weight:normal;">📎</a> --%>
				<a id="listphotobtn" href="${f:url('/page/list/detail/')}${listForm.menutaskid}/${listForm.menuid}/${e[key]}" class="btn_icon ${true ? 'listphotobtn blue' : 'lgray'} " style="font-weight:normal;">&#x2710<!-- U+2710 UPPER RIGHT PENCIL--></a>
				<%-- </c:if> --%>
				<a id="historybtn" href="${f:url('/page/list/history/')}${listForm.menutaskid}/${listForm.menuid}/${e[key]}" class="btn_icon blue historybtn" style="font-weight:normal;">🕒</a>
				</c:if>
			</td>
			</c:otherwise>
			</c:choose>
		</c:if>

		<c:forEach var="f" varStatus="t" items="${colinfoItems}">
		<c:set var="hi" value="" />
		<c:if test="${f.highlight}"><c:set var="hi" value="highlight" /></c:if>
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
		<%-- 日付形式は yy/mm/dd HH:MM:SS になるように変換する --%>
		<c:if test="${editClass[t.index] == 'Date' || editClass[t.index] == 'DateTime'}">
			<c:set var="colVal" value="${fn:replace(colVal, '-', '/')}"/>
			<c:set var="colVal" value="${fn:replace(colVal, '.0', '')}"/>
		</c:if>

		<c:set var="st2" value="" />
		<c:set var="tip" value="" />
		<c:if test="${editClass[t.index] == 'String' || editClass[t.index] == 'TextArea' || editClass[t.index] == 'Date' || editClass[t.index] == 'DateTime' || editClass[t.index] == 'Upload'}">
			<c:set var="st2" value="text-align:${pageDto.text_align};" />
			<c:set var="tip" value=" showtip" />
		</c:if>
		<c:if test="${editClass[t.index] == 'Number' || editClass[t.index] == 'Float'}">
			<c:set var="st2" value="text-align:${pageDto.number_align};" />
		</c:if>
		<c:choose>
			<c:when test="${f.editable && pageDto.viewMode==false}"><!-- edit -->
				<td id="${table}:${f.attrid}:${key}:${not empty _orgidException ? e[key] : e._orgid}" class="${editClass[t.index]} ${hi} ${tip}" style="${f:h(st)} ${f:h(st2)}">
					<c:if test="${editClass[t.index] == 'String' || editClass[t.index] == 'TextArea' || editClass[t.index] == 'Number' || editClass[t.index] == 'Float' || editClass[t.index] == 'Date' || editClass[t.index] == 'DateTime'}">
						${f:h(colVal)}
					</c:if>
					<c:if test="${editClass[t.index] == 'Select'}">
						<span id="${f.attrid}:${not empty _orgidException ? e[key] : e._orgid}" style="display:none;">${f:h(colVal)}</span>
						<select name="${f.attrid}:${not empty _orgidException ? e[key] : e._orgid}" onChange="onSelStyleChange(${f.id}, this, '#${f.attrid}:${not empty _orgidException ? e[key] : e._orgid}')">
						<c:if test="${!colMap[f.attrid] && f.editable && selectVal[f.attrid][0]!=''}"><option value=""></option></c:if>
						<c:forEach var="g" varStatus="u" items="${selectStr[f.attrid]}">
							<c:set var="g2" value="${selectVal[f.attrid]}" />
							<c:if test="${g2[u.index] != colVal}">
								<option value="${f:h(g2[u.index])}">${f:h(g)}</option>
							</c:if>
							<c:if test="${g2[u.index] == colVal}">
								<option value="${f:h(g2[u.index])}" selected>${f:h(g)}</option>
							</c:if>
						</c:forEach>
						</select>
					</c:if>
					<c:if test="${editClass[t.index] == 'Checkbox'}">
						<span id="${f.attrid}:${not empty _orgidException ? e[key] : e._orgid}" style="display:none;">${f:h(colVal)}</span>
						<input type="checkbox" name="${f.attrid}:${not empty _orgidException ? e[key] : e._orgid}" value="${f:h(checkStr[f.attrid])}" ${(checkStr[f.attrid]==colVal)?"checked":""} onClick="onCheckStyleChange(${f.id}, this, '#${f.attrid}:${not empty _orgidException ? e[key] : e._orgid}')">
					</c:if>
					<c:if test="${editClass[t.index] == 'Upload' || editClass[t.index] == 'Link'}">
						<c:if test="${fn:length(colVal) > 0}">
							<a href="${f:url(colVal)}" target="_blank" style="color:blue; text-decoration: underline;">${f:h(colVal)}</a>
						</c:if>
						<!-- <input type="button" name="${f.attrid}:${e[key]}" value="更新">-->
					</c:if>
				</td>
			</c:when>
			<c:otherwise><!-- not edit -->
				<c:set var="selMap" value="${selectValView[f.attrid]}" />
				<c:choose>
					<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
						<td id="${table}:${f.attrid}:${key}:${not empty _orgidException ? e[key] : e._orgid}" class="${hi}" style="${f:h(st)} text-align:${f:h(pageDto.text_align)};">${f:h(selMap[f:h(colVal)])}</td>
					</c:when>
					<c:when test="${editClass[t.index] == 'Upload' || editClass[t.index] == 'Link'}">
						<td id="${table}:${f.attrid}:${key}:${not empty _orgidException ? e[key] : e._orgid}" class="${hi}" style="${st} ${st2}">
						<c:if test="${fn:length(colVal) > 0}">
							<a href="${f:url(colVal)}" target="_blank" style="color:blue; text-decoration: underline;">${f:h(colVal)}</a>
						</c:if>
						</td>
					</c:when>
					<c:otherwise>
						<td id="${table}:${f.attrid}:${key}:${not empty _orgidException ? e[key] : e._orgid}" class="${hi} ${tip}" style="${st} ${st2}">${f:h(colVal)}</td>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</tr>
</c:forEach>
	</tbody>
</table>
<c:if test="${paging}">
	<div style="text-align:left;">
	<a id="pagestart" class="pagestart ui-button ui-widget ui-state-disabled ui-corner-all">
		<span class="ui-icon ui-icon-arrowthickstop-1-w" title="<%=lang.__("First")%>"></span></a>
	<a id="pageprev" class="pageprev ui-button ui-widget ui-state-disabled ui-corner-all">
		<span class="ui-icon ui-icon-arrowthick-1-w" title="<%=lang.__("Return")%>"></span></a>
	<a id="pagenext" class="pagenext ui-button ui-widget ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-arrowthick-1-e" title="<%=lang.__("Next")%>"></span></a>
	<a id="pageend" class="pageend ui-button ui-widget ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-arrowthickstop-1-e" title="<%=lang.__("Last")%>"></span></a>
	&nbsp;
	<span style="color:gray;" class="paging-num">(${count} <%=lang.__("Items")%>)</span>
	</div>
</c:if>
<input type="hidden" id="accordionopen" value="${mtbl.accordionopen}">
<div id="tooltip"></div>
<div id="file-dialog" title="<%=lang.__("File upload")%>" style="display:none; width:450px; height:auto;">
	<form:form id="fileupload" method="POST" enctype='multipart/form-data' target="fileframe" modelAttribute="listForm"></form:form>
	<% FormUtils.printToken(out, request); %>
	<c:if test="${editable}">
	<p style="text-aling:center; margin-top:5px; margin-bottom:10px;"><%=lang.__("Select upload file.")%></p>
	<input type="file" name="formFile" id="upfile">
	</c:if>
	<input type="hidden" id="upgid">
	<input type="hidden" id="orgid">
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<!-- <div id="photoarea" style="width:450px; height:auto"></div> -->
</div>

<%-- CSVファイル出力 --%>
<div id="csv-dialog" title="<%=lang.__("CSV file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as CSV file.")%><br>
		<%=lang.__("* In case of filtering settings configured, only searched data (not in gray color)  are displayed.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputcsv" modelAttribute="listForm">
			<% FormUtils.printToken(out, request); %>
			<input type="hidden" name="value" value="" >
			<input type="hidden" name="menuid" value="${listForm.menuid}">
			<input type="hidden" name="menutaskid" value="${listForm.menutaskid}">
			<input type="hidden" name="paging" value="${paging}">
			<c:if test="${paging}">
			<input type="checkbox" name="pageall" ><%=lang.__("output all pages")%><br>※<%=lang.__("It may take a very long time for much data.")%>
			</c:if>
			<a href='javascript:outputCSV(document.getElementById("outputcsv"));' class="dialog-button" ><%=lang.__("CSV output")%></a>
		</form:form>
	</div>
</div>

<%-- 履歴表示 --%>
<div id="history-dialog" title="<%=lang.__("History display")%>" style="display:none;">
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<div style="float: right;">
	</div>
</div>

<%-- PDFファイル出力 --%>
<div id="pdf-dialog" title="<%=lang.__("PDF file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as PDF file.")%><br>
		<%=lang.__("* In case of filtering settings configured, only searched data (not in gray color)  are displayed.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputpdf" modelAttribute="listForm">
			<% FormUtils.printToken(out, request); %>
			<input type="hidden" name="dataList" value="">
			<input type="hidden" name="menuid" value="${listForm.menuid}">
			<input type="hidden" name="menutaskid" value="${listForm.menutaskid}">
			<input type="hidden" name="paging" value="${paging}">
			<c:if test="${paging}">
			<input type="checkbox" name="pageall" ><%=lang.__("output all pages")%><br>※<%=lang.__("It may take a very long time for much data.")%>
			</c:if>
			<a href='javascript:outputPDF(document.getElementById("outputpdf"));' class="dialog-button" ><%=lang.__("PDF output")%></a>
		</form:form>
	</div>
</div>

<div id="addline-dialog" title="<%=lang.__("Register")%>" style="display:none;">
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<div style="float: right;">
	</div>
</div>

<div id="detail-dialog" title="<%=lang.__("Detail display")%>" style="display:none;">
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<div style="float: right;">
	</div>
</div>

<div id="map-dialog" style="display:none; height:80%; width:85%;"></div>

<div id="search-dialog" title="<%=lang.__("Attribute search")%>" style="display:none;">
	<input type="hidden" id="menuid" name="menuid" value="${listForm.menuid}">
	<input type="hidden" id="menutaskid" name="menutaskid" value="${listForm.menutaskid}">
	<div style="float: right;">
	</div>
</div>

<script type="text/javascript">
var page = ${listForm.npage};

function loadedbody() {
	location.href = "${f:url('/logout')}"
}

</script>
<script type="text/javascript">
//# sourceURL=pagingdebug.js
function listupdate() {
	sortclick = false;
	//$('#tablebody').empty();
	$("#editTable").trigger("update");
	var sortforce = $("#editTable")[0].config.sortForce;
	//alert("test");
	if (sortforce == undefined || sortforce.length == 0 || sortforce[0][0] == -1) {
	if (currentSort == undefined || currentSort == -1 || currentSort.length == 0) {
		$("#editTable").trigger("sorton",-1);
	} else {
		var sorting = $("#editTable")[0].config.sortList;
		//var sorting = [[2,1],[0,0]];
<c:if test="${!paging}">
		$("#editTable").trigger("sorton", [sorting]);
</c:if>
		//$("#editTable").trigger("sorton",-1);
	}
	}

	SaigaiTask.Page.List.initOnClickMapButton();
	listeditbutton();
	historybutton();
	showtips();
	if (filterFunc!=undefined)
		filterFunc();
}

var nextpage = ${nextpage};
$(function() {
<c:if test="${paging}">
	if (!nextpage) {
		$('.pagenext').removeClass('ui-state-default');
		$('.pagenext').addClass('ui-state-disabled');
		$('.pageend').removeClass('ui-state-default');
		$('.pageend').addClass('ui-state-disabled');
	}
	if (nextpage) {
		$('.pagenext').removeClass('ui-state-disabled');
		$('.pagenext').addClass('ui-state-default');
		$('.pageend').removeClass('ui-state-disabled');
		$('.pageend').addClass('ui-state-default');
	}
	if (page > 0) {
		$('.pageprev').removeClass('ui-state-disabled');
		$('.pageprev').addClass('ui-state-default');
		$('.pagestart').removeClass('ui-state-disabled');
		$('.pagestart').addClass('ui-state-default');
	}
	if (page == 0) {
		$('.pageprev').removeClass('ui-state-default');
		$('.pageprev').addClass('ui-state-disabled');
		$('.pagestart').removeClass('ui-state-default');
		$('.pagestart').addClass('ui-state-disabled');
	}
	$(".pagestart").on('click' ,function(){
		if (page == 0) return;
		page = 0;
		loadNextpage(page);
	});
	$(".pageprev").on('click' ,function(){
		if (page == 0) return;
		page = page-1;
		loadNextpage(page);
	});
	$(".pagenext").on('click' ,function(){
		if (!nextpage) return;
		page = page+1;
		loadNextpage(page);
	});
	$(".pageend").on('click' ,function(){
		if (!nextpage) return;
		nextpage = false;
		loadNextpage(-1);
	});
	var prow = ${pageDto.pagerow+1};
	var pst = (prow-1)*page+1;
	var pend = (prow-1)*(page+1)+1;
	$(".paging-num").text(" ["+pst+"-"+pend+"/"+addFigure(${count})+"<%=lang.__("Items")%>　"+(page+1)+"/${listForm.pagesize}<%=lang.__("display page")%>]");
</c:if>
});

function loadNextpage(page) {
	var d = new Date();
	$('#content').mask("Loading...");
	// 時間パラメータの付与
	var timeParam = SaigaiTask.PageURL.timeParam();
	if (currentSort == undefined || currentSort == -1 || (Array.isArray(currentSort) && currentSort.length == 0)) {
		$('#tablebody').load('${f:url('/page')}/list/tablepagebody/${listForm.menutaskid}/${listForm.menuid}/'+page+"?"+d.getTime()+timeParam, function() {
			editTable();
			listupdate();
			$('#content').unmask();
		});
	}
	else {
		var sorttmp = currentSort.join(",")+","+currentSortAttr;
		$('#tablebody').load('${f:url('/page')}/list/tablepagebodysort/${listForm.menutaskid}/${listForm.menuid}/'+page+'/'+sorttmp+"?"+d.getTime()+timeParam, function() {
			editTable();
			listupdate();
			$('#content').unmask();
		});
	}
	SaigaiTask.PageURL.override({npage: page});
}

function editTable() {
	$('#editTable').editTable({
		callback : updateTable,
		comma : false,
		mobile : ismobile
	});
}
function checkBeforeUnload(event) {
	var dataArray = $('#editTable').getSaveData();
	var num = dataArray.length;
	for (var i = 0; i < dataArray.length; i++) {
		if (dataArray[i].length > 1 && dataArray[i][0].indexOf(":select:") > 0)
			num--;
	}
	if (num > 0) {
		return '保存していません。このまま移動しますか？';
	}
}

$(function() {
	$(window).on("beforeunload", checkBeforeUnload);
});

</script>

<%-- ファイルUpload用 --%>
<iframe style="width:0; height:0; border:none;" id="fileframe" name="fileframe"></iframe>
