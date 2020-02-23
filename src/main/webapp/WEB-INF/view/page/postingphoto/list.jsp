<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>
<%-- リロードする必要の無い JavaScript, CSS は head.jsp に記載する. --%>
<script type="text/javascript">

var addline = false;
var idsArr = new Array();
var typesArr = new Array();
var groupingArr = new Array();
var currentSort;
var currentSortAttr;
//# sourceURL=listdebug.js

// 最終更新日を更新
SaigaiTask.setUpdateTime("${f:h(pageDto.updateTime)}");

$(function() {

	SaigaiTask.Page.List({
		/**
		 * メタデータ
		 * @type {Object<String, String>} メタデータID, タイトル
		 */
		metadatas: JSON.parse('${fal:json(metadataMap)}')
	});

	var filterMap = ${fal:json(filterIds)};
	// フィルタ件数を取得
	var sum = 0;
	var filterNum = 0;
	for(var gid in filterMap) {
		var isFilter = (filterMap[gid]==false);
		if(isFilter) filterNum++;
		sum++;
	}
	// フィルタ件数の更新
	SaigaiTask.Page.filter.getFilterNumEl().text(MessageFormat.format(" <%=lang.__("({0} items / {1} items)")%>", filterNum, sum));
	$("#data-num").text(" [ "+addFigure(${count})+"<%=lang.__("Items")%> ]");

	//テーブルのカラムが増えた場合のoffset
	<%-- 合計セル出力用 --%>
	<fmt:parseNumber var="offset" value="0" integerOnly="true" />
	<fmt:parseNumber var="offset_tmp" value="0" integerOnly="true" />
	<c:if test="${!deletable && key != 'gid'}">
		<c:set var="offset" value="${offset_tmp+1}" />
		<c:set var="offset_tmp" value="${offset}" />
	</c:if>
	<%-- 削除フラグがある場合 --%>
	<c:if test="${deletable}">
		<c:set var="offset" value="${offset_tmp+1}" />
		<c:set var="offset_tmp" value="${offset}" />
	</c:if>
	<%--mapの場合 --%>
	<c:if test="${key == 'gid'}">
		<c:set var="offset" value="${offset_tmp+1}" />
	</c:if>

	// テーブルヘッダー
	var headers = {};
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
	<c:if test="${!e.sortable}">
	headers[${offset+s.index}] = {sorter:false};
	</c:if>
	</c:forEach>
	var offset = ${offset};
	for(var idx=0; idx<offset; idx++) {
		headers[idx] = {sorter:false};
	}

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

	// TableSorter 初期化
	try {
		$("#editTable").tablesorter({
			widgets: ['zebra'],
			widthFixed : true,
			headers: headers,
			// FIXME: リストのレコードが0件の場合に、デフォルトソート(sortList)を指定すると、
			//  “table.config.parsers[c] is undefined” のエラーが発生する
			//  2.15.5 へのアップデートで fix する予定
			sortList: sortList,
		<c:if test="${paging}">
			serverSideSorting:true
		</c:if>
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
				$('#content').mask("Loading...");
				$('#tablebody').load('${f:url('/page/postingphoto/tablepagebodysort')}/${listForm.menutaskid}/${listForm.menuid}/'+page+'/'+sorttmp+','+currentSortAttr+'?'+d.getTime(), function() {
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
					var cs = currentSort;
					if (cs=='') cs = '-1';
					reloadContent("list/list/${listForm.menutaskid}/${listForm.menuid}/"+cs);
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
		//対象外データのグレー表示/非表示の切り替え
		if($("#filter-gray-ck") && filtering){
			//$("#filter-gray-ck").removeAttr("disabled");
			$("#filter-gray-ck-div").css("display","block");
			filterFunc = function(){
				//グレー表示
				if($("#filter-gray-ck").prop("checked")){
					$("tr.gray").css("display", "table-row");
					//grayの行を非表示にすると、tablesorterの色分けが崩れるので、以下で制御
					//一旦、oddとevenクラスを全削除
					$("table#editTable tr").each(function(){
						$(this).removeClass('even');
						$(this).removeClass('odd');
					});
					//振り直し
					var idx=0;
					$("table#editTable tr").each(function(){
						if (!$(this).hasClass('sum')) {
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
						if(!$(this).hasClass('sum') && !$(this).hasClass("gray")){//grayクラスは飛ばす
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
		var filtergray = "${filtergray}";
		if(filtergray == "false"){
			$("#filter-gray-ck").attr("checked", false );
		}else{
			$("#filter-gray-ck").attr("checked", true );
		}
		filterFunc();
	}

	//ボタンのスタイル設定
	$(".dialog-button").button();
	$("#dialog-button-list li").css("margin", "5px").css("float", "left").css("list-style", "none");

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
			td.attr("style","${f.style}");
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
					"${e.key}",
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

	var url = "./postingphoto/update/";
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
//		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}','Content-Type': 'application/json'},
		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		data: SaigaiTask.Edit.toSAStrutsParam(data),
		success: function(msg) {
			$('#editTable').clearSaveData();
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("<%=lang.__("Error : Failed to register")%>");
		}
	});

	if (addline) {//追加の場合
		addline = false;
	}

}

function addLine() {
	if (addline) return;

	var line = "<tr>";
	<%-- 合計セル出力用 --%>
	<c:if test="${!deletable && key != 'gid'}">
		line += "<td></td>";
	</c:if>
	<%-- 削除フラグがある場合 --%>
	<c:if test="${deletable}">
		line += "<td></td>";
	</c:if>
	<%--mapの場合 --%>
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
    addline = true;
}

//指定した「レイヤ名.gid」のフィーチャをポップアップして地図表示
function openMap(gid){
	SaigaiTask.PageURL.override({pagetype : "map", popup : gid }).move();
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

//CSVファイル出力
function outputCSV(form){

	var pageall = false;
	if (form.pageall != undefined) pageall = form.pageall.checked;
	//ここでテーブル表示の内容と同じループを回して、CSVの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	var datas = new Array();
	if (${!paging} || !pageall) {
	//ヘッダ
	datas[0] = new Array();
	datas[0][0] = "";//合計値用対策
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		datas[0][${s.index+1}] = "${f:h(e.name)}";
	</c:forEach>
	//合計
	datas[1] = new Array();
	<c:if test="${totalable}">
		datas[1][0] = "<%=lang.__("Total<!--2-->")%>";//合計値用対策
		<c:forEach var="e" varStatus="s" items="${colinfoItems}">
			datas[1][${s.index+1}] = "${sumItems[e.name]}";
		</c:forEach>
	</c:if>
	//データ、フィルタリング対象のもののみ出力
	var cnt = 1;
	var accordionInfo = SaigaiTask.Page.List.accordionHeaderInfo();
	<c:forEach var="e" varStatus="s" items="${result}">
		<c:if test="${!empty mtbl.accordionattrid}">
			<c:set var="val" value="${fn:toLowerCase(fn:trim(e[mtbl.accordionattrid==null?'':mtbl.accordionattrid]))}" />
			//アコーディオン対象属性の値が変化したところにヘッダを挿入する
			<c:if test="${s.first || val != lastVal}">
				datas[cnt+1] = [[ "${fal:nolf(f:h(val))}" + '' + accordionInfo["${val}"].count ]];
				<c:set var="lastVal" value="${val}" />
			cnt++;
			</c:if>
		</c:if>
	datas[cnt+1] = new Array();
		<c:if test="${filterIds[f:h(e[key])]}">
			datas[cnt+1][0] = "";//合計値用対策
			<c:forEach var="f" varStatus="t" items="${colinfoItems}">
				<c:set var="selMap" value="${selectValView[f.attrid]}" />
				<c:choose>
					<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
						datas[cnt+1][${t.index+1}] = "${fal:nolf(selMap[f:h(e[f.attrid])])}";
					</c:when>
					<c:when test="${editClass[t.index] == 'Upload'}">
						<c:if test="${fn:length(e[f.attrid]) > 0}">
							datas[cnt+1][${t.index+1}] = "${fal:nolf(f:h(e[f.attrid]))}";
						</c:if>
					</c:when>
					<c:otherwise>
						datas[cnt+1][${t.index+1}] = "${fal:nolf(f:h(e[f.attrid]))}";
					</c:otherwise>
				</c:choose>
			</c:forEach>
			cnt++;
		</c:if>
	</c:forEach>

		//二重配列に格納したデータをカンマ区切りで整形、改行は「<br>」で代用して、パラメータ文字列に整形
		var csvStr = "";
		for(var i=0; i<datas.length; i++){
			for(var j=0; j<datas[i].length; j++){
				//alert(datas[i].length);
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
		form.submit();
	}while(0);
	$("#csv-dialog").dialog("close");
}

//PDFファイル出力
function outputPDF(form){

	var pageall = false;
	if (form.pageall != undefined) pageall = form.pageall.checked;
	//ここでテーブル表示の内容と同じループを回して、PDFの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	var datas = new Array();
	if (${!paging} || !pageall) {
	//ヘッダ
	datas[0] = new Array();
	datas[0][0] = "";//合計値用対策
	//アコーディオンの開閉表示（三角マーク）用のスペースを確保する
	<c:if test="${!empty mtbl.accordionattrid}">
		datas[0][0] = "   ";
	</c:if>
	<c:forEach var="e" varStatus="s" items="${colinfoItems}">
		datas[0][${s.index+1}] = "${f:h(e.name)}";
	</c:forEach>
	//合計
	datas[1] = new Array();
	<c:if test="${totalable}">
		datas[1][0] = "<%=lang.__("Total<!--2-->")%>";//合計値用対策
		<c:forEach var="e" varStatus="s" items="${colinfoItems}">
			datas[1][${s.index+1}] = "${sumItems[e.name]}";
		</c:forEach>
	</c:if>
	//データ、フィルタリング対象のもののみ出力
	<c:set var="lastVal" value="" />;
	var cnt = 1;
	//アコーディオンの開閉状態を取得する
	var accordionInfo = SaigaiTask.Page.List.accordionHeaderInfo();
	<c:forEach var="e" varStatus="s" items="${result}">
		var open = true;
		<c:if test="${!empty mtbl.accordionattrid}">
			<c:set var="val" value="${fn:toLowerCase(fn:trim(e[mtbl.accordionattrid==null?'':mtbl.accordionattrid]))}" />
			var open = accordionInfo["${val}"].open;
			//アコーディオン対象属性の値が変化したところにヘッダを挿入する
			<c:if test="${s.first || val != lastVal}">
				datas[cnt+1] = [{
					open: open,
					value: "${fal:nolf(f:h(val))}",
					count: accordionInfo["${val}"].count
				}];
				<c:set var="lastVal" value="${val}" />
				cnt++;
			</c:if>
		</c:if>
		datas[cnt+1] = new Array();
		<c:if test="${filterIds[f:h(e[key])]}">
		//アコーディオンが開いているもののみ出力
		if (open) {
			datas[cnt+1][0] = "";//合計値用対策
			<c:forEach var="f" varStatus="t" items="${colinfoItems}">
				<c:set var="selMap" value="${selectValView[f.attrid]}" />
				<c:choose>
					<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
						datas[cnt+1][${t.index+1}] = "${fal:nolf(selMap[f:h(e[f.attrid])])}";
					</c:when>
					<c:when test="${editClass[t.index] == 'Upload'}">
						<c:if test="${fn:length(e[f.attrid]) > 0}">
							datas[cnt+1][${t.index+1}] = "${fal:nolf(f:h(e[f.attrid]))}";
						</c:if>
					</c:when>
					<c:otherwise>
						datas[cnt+1][${t.index+1}] = "${fal:nolf(f:h(e[f.attrid]))}";
					</c:otherwise>
				</c:choose>
				<c:if test="${f.attrid == mtbl.accordionattrid}">
				</c:if>
			</c:forEach>
			cnt++;
		}
		</c:if>
	</c:forEach>
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
		form.submit();
	}while(0);
	$("#pdf-dialog").dialog("close");
}


</script>
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
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
		<c:if test="${!deletable && key != 'gid'}">
			<th class=""></th>
		</c:if>
		<%-- 削除フラグがある場合 --%>
		<c:if test="${deletable}">
			<th><%=lang.__("Delete")%></th>
		</c:if>
		<%--mapの場合 --%>
		<c:if test="${key == 'gid' && pageDto.pagetoggleButton.first=='map'}">
			<th></th>
		</c:if>
		<%-- テーブルヘッダ --%>
		<c:forEach var="e" varStatus="s" items="${colinfoItems}">
			<c:set var="hi" value="" />
			<c:if test="${e.highlight}"><c:set var="hi" value="highlight" /></c:if>
			<th class="${hi}"  id="${e.attrid}" }>
				${f:h(e.name)}<c:if test="${colMap[e.attrid] && e.editable}"><font style="color:FF0000"> ※</font></c:if>
			</th>
		</c:forEach>
	</tr>
	</thead>

	<tbody id="tablebody">
<c:forEach var="e" varStatus="s" items="${result}">
	<c:if test="${!filterIds[f:h(e[key])]}">
		<tr class="gray" style="display:table-row;" >
	</c:if>
	<c:if test="${filterIds[f:h(e[key])]}">
		<tr>
	</c:if>

		<c:if test="${!deletable && key != 'gid'}">
			<td></td>
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
				<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${editClass[t.index]} ${hi} ${tip}" style="${st} ${st2}">
					<c:if test="${editClass[t.index] == 'String' || editClass[t.index] == 'TextArea' || editClass[t.index] == 'Number' || editClass[t.index] == 'Float' || editClass[t.index] == 'Date' || editClass[t.index] == 'DateTime'}">
						${f:h(colVal)}
					</c:if>
					<c:if test="${editClass[t.index] == 'Select'}">
						<span id="${f.attrid}:${e[key]}" style="display:none;">${colVal}</span>
						<select name="${f.attrid}:${e[key]}" onChange="onSelStyleChange(${f.id}, this, '#${f.attrid}:${e[key]}')">
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
						<span id="${f.attrid}:${e[key]}" style="display:none;">${colVal}</span>
						<input type="checkbox" name="${f.attrid}:${e[key]}" value="${f:h(checkStr[f.attrid])}" ${(checkStr[f.attrid]==colVal)?"checked":""} onClick="onCheckStyleChange(${f.id}, this, '#${f.attrid}:${e[key]}')">
					</c:if>
				</td>
			</c:when>
			<c:otherwise><!-- not edit -->
				<c:set var="selMap" value="${selectValView[f.attrid]}" />
				<c:choose>
					<c:when test="${ (fn:length(selMap) > 0) && (editClass[t.index] == 'Select')}">
						<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${hi}" style="${st} text-align:${pageDto.text_align};">${selMap[f:h(colVal)]}</td>
					</c:when>
					<c:when test="${editClass[t.index] == 'Upload'}">
						<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${hi}" style="${st} ${st2}">
						<c:if test="${fn:length(colVal) > 0}">
							<a href="${f:url(colVal)}" target="_blank" style="color:blue; text-decoration: underline;">${f:h(colVal)}</a>
						</c:if>
						</td>
					</c:when>
					<c:when test="${editClass[t.index] == 'Image'}">
						<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${hi}"><img src="${f:h(colVal)}" style="max-width:${photolayerInfo.maximagewidth}px; max-height:${photolayerInfo.maximagewidth}px;"></td>
					</c:when>
					<c:otherwise>
						<td id="${table}:${f.attrid}:${key}:${e[key]}" class="${hi} ${tip}" style="${st} ${st2}">${f:h(colVal)}</td>
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
			<input type="hidden" name="pageall" value="true">
			</c:if>
			<a href='javascript:outputCSV(document.getElementById("outputcsv"));' class="dialog-button" ><%=lang.__("CSV output")%></a>
		</form:form>
	</div>
</div>

<%-- PDFファイル出力 yaama --%>
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
			<input type="hidden" name="pageall" value="true">
			</c:if>
			<a href='javascript:outputPDF(document.getElementById("outputpdf"));' class="dialog-button" ><%=lang.__("PDF output")%></a>
		</form:form>
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
	if (currentSort == undefined || currentSort == -1) {
		if (currentSort == undefined || currentSort == -1 || currentSort.length == 0) {
		$("#editTable").trigger("sorton",-1);
	}
	} else {
		var sorting = $("#editTable")[0].config.sortList;
		//var sorting = [[2,1],[0,0]];
<c:if test="${!paging}">
		$("#editTable").trigger("sorton", [sorting]);
</c:if>
		//$("#editTable").trigger("sorton",-1);
	}


	SaigaiTask.Page.List.initOnClickMapButton();
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
	if (currentSort == undefined || currentSort == -1 || (Array.isArray(currentSort) && currentSort.length == 0)) {
		$('#tablebody').load('${f:url('/page')}/postingphoto/tablepagebody/${listForm.menutaskid}/${listForm.menuid}/'+page+"?"+d.getTime(), function() {
			editTable();
			listupdate();
			$('#content').unmask();
		});
	}
	else {
		var sorttmp = currentSort.join(",")+","+currentSortAttr;
		$('#tablebody').load('${f:url('/page')}/postingphoto/tablepagebodysort/${listForm.menutaskid}/${listForm.menuid}/'+page+'/'+sorttmp+"?"+d.getTime(), function() {
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

</script>

<div id="tooltip"></div>
