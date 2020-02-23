<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>
<script type="text/javascript">
var currentSort;
var currentSortAttr;
//# sourceURL=listdebug.js

//最終更新日を更新
SaigaiTask.setUpdateTime("${f:h(pageDto.updateTime)}");

$(function() {
	SaigaiTask.Page.List({
		/**
		 * メタデータ
		 * @type {Object<String, String>} メタデータID, タイトル
		 */
		metadatas: JSON.parse('${fal:json(metadataMap)}')
	});

	//ページングOFF時の件数表示
	$("#data-num").text(" [ "+addFigure(${count})+"<%=lang.__("Items")%> ]");
	//ボタンのスタイル設定
	$(".dialog-button").button();
});

</script>
<c:choose>
<c:when test="${fn:length(listDtos)==0}">
<%=lang.__("There are no columns that can be displayed.")%>
</c:when>
<c:otherwise>
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<style type="text/css">
<!--
.tablesorter TD.sum {
	background-color: #1E4B78;
	color: white;
	text-align: center;
}
//-->
</style>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.list.js')}"></script>


<script type="text/javascript">
//CSVファイル出力
function outputCSV(form, tableId) {

	var pageall = false;
	if (form.pageall != undefined) pageall = form.pageall.checked;
	//ここでテーブル表示の内容と同じループを回して、CSVの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	var datas = new Array();
	if (${!paging} || !pageall) {

		var datas = SaigaiTask.Page.List.getDatas({
			tableId: tableId
		});

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
		form.action = "${f:url('/page/externallist/outputcsv/')}";
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
	SaigaiTask.Page.csvDialog.dialog("close");
}

//PDFファイル出力
function outputPDF(form, tableId) {

	var pageall = false;
	if (form.pageall != undefined) pageall = form.pageall.checked;
	//ここでテーブル表示の内容と同じループを回して、PDFの元になるテキストを出力して二重配列の変数にセットする
	//DBから取得する文字列内に、改行コードがあると「""」でくくられずにエラーになるので、fal:nolf()で改行コードを削除する
	var datas = new Array();
	if (${!paging} || !pageall) {
		var datas = SaigaiTask.Page.List.getDatas({
			tableId: tableId
		});
	}

	//formのPOSTで文字列をJSON形式で送信
	do{
		if(form == null || form == undefined){
			alert("<%=lang.__("Form has not been set correctly!")%>");
			break;
		}
		form.action = "${f:url('/page/externallist/outputpdf/')}";
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
				$('<input>').attr({"type":"hidden","name":"time","value":printTimeStr}).appendTo(form);
			}
			else timeInput.val(printTimeStr);
		}		form.dataList.value = JSON.stringify(datas);
		form.submit();
	}while(0);
	SaigaiTask.Page.pdfDialog.dialog("close");
}
//履歴保存
function saveHistoryData(listId) {

	var datas = SaigaiTask.Page.List.getDatas({
		tableId: listId
	});

	// 合計行の表示
	var totalable = $("table#"+listId+" #total-header").length==1;

	var data = {
		dataList: JSON.stringify(datas),
		pagetype: "${externalListForm.pagetype}",
		listid: listId,
		totalable: totalable,
		menuid: "${externalListForm.menuid}",
		menutaskid: "${externalListForm.menutaskid}",
		menutypeid: "${pageDto.menuInfo.menutypeid}",
		filename: "${externalListForm.pagetype}-"+listId
	};

	console.log("post.data");
	console.log(data);

	SaigaiTask.Page.saveDialog.mask("<%=lang.__("Now saving..<!--2-->")%>");
	$.ajax({
		url: SaigaiTask.contextPath+"/page/generalizationHistory/save",
		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
//		contentType: 'application/x-javascript',
		method: "post",
		data: data,
		complete: function(xhr, textStatus) {
			SaigaiTask.Page.saveDialog.unmask();
			SaigaiTask.Page.saveDialog.dialog("close");
			var msg = "<div><%=lang.__("Saved.")%></div>";
			if(textStatus=="error") {
				msg = "<div><%=lang.__("Unable to save.")%></div>";
			}
			$(msg).dialog({
				title: "<%=lang.__("Save history")%>",
				buttons: { "OK": function() { $(this).dialog("close"); } }
			});
		}
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
	if(${listDto.historybtn}) historybutton();
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
		$("#history-dialog").load(this.href+"&"+date.getTime()+(iso8601Time!=null?"&time="+iso8601Time:""), function() {
			$(this).dialog("option", "position", "{ my: 'center', at: 'center', of: 'window'}");
		});
	});
}

$(function() {
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
});

function reload(){
	SaigaiTask.PageURL.updateSummary();
}
</script>

<c:forEach var="listDto" items="${listDtos}">
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


<table border="0" cellpadding="3" cellspacing="2" id="${listDto.styleId}" class="tablesorter shortline" style="margin-left:10px;">
	<c:if test="${!empty listDto.title }">
	<caption style="font-weight: bold; font-size: large;">${f:h(listDto.title)}</caption>
	</c:if>
	<thead>
		<tr id="column-header">
			<c:if test="${listDto.totalable}"><th class=""></th></c:if>
			<c:forEach varStatus="s" items="${listDto.columnNames}">
			<th id="column-${s.index}">${s.current}</th>
			</c:forEach>
		</tr>
		<c:if test="${listDto.totalable}">
		<tr id="total-header">
			<td class="sum"><%=lang.__("Total<!--2-->")%></td>
			<c:forEach var="name" varStatus="s" items="${listDto.columnNames}">
			<td class="sum" headers="column-${s.index}" id="column-${s.index}-sum">${listDto.sumItems[name]}</td>
			</c:forEach>
		</tr>
		</c:if>
	</thead>
	<tbody id="tablebody">
		<c:forEach varStatus="s" items="${listDto.columnValues}">
		<tr>
			<c:if test="${listDto.totalable || listDto.historybtn}"><td class="">
			<c:if test="${listDto.historybtn}"><a id="historybtn" href="${f:url('/page/externallist/history')}?menutaskid=${externalListForm.menutaskid}&menuid=${externalListForm.menuid}&metaid=${externalListForm.metaid}&${listDto.historybtnUrlParams[s.index]}" class="btn_icon blue historybtn" style="font-weight:normal;">🕒</a></c:if>
			</td></c:if>
			<c:forEach varStatus="s2" items="${s.current}">
			<c:set var="st2" value="text-align:${listDto.typeItems[listDto.columnNames[s2.index]]=='number'?pageDto.number_align:pageDto.text_align}" />
			<td class="showtip" headers="column-${s2.index}" style="${st2}">${listDto.typeItems[name]}<c:if test="${s2.current!='null'}">${s2.current}</c:if></td>
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

<script type="text/javascript">
$(function() {
    $("#${listDto.styleId}").tablesorter({
    	widgets: ['zebra'],
		headers: {
//<c:forEach var="e" varStatus="s" items="${listDto.columnNames}">
//				${s.index}:{sorter:false},
//</c:forEach>
		},
		<c:if test="${!empty listDto.defsort}">
		sortList: [[${f:h(listDto.defsort)}]],
			<c:if test="${paging}">
			serverSideSorting:true
			</c:if>
		</c:if>
    }).bind("sortEnd", function(sorter) {
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
			$('#tablebody').load('${f:url('/page/externallist/tablepagebodysort')}/${externalListForm.menutaskid}/${externalListForm.menuid}/${externalListForm.metaid}/'+page+'/'+sorttmp+','+currentSortAttr+'?'+d.getTime(), function() {
				editTable();
				listupdate();
				$('#content').unmask();
			});
        }
        </c:if>
        return false;

	});

	//ボタンのスタイル設定
	$(".dialog-button").button();

	var sortclick = false;
	$(".tablesorter-header").on("click", function(e) {
		sortclick = true;
	});
});

function reloadlist() {
	//リストの更新
	SaigaiTask.Page.List.reloadlist();
}

var page = ${externalListForm.npage};
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
}

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

var nextpage = ${nextpage};

$(function() {
	if (${!paging})
		$("#data-num").css("display", "inline");

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
	$(".paging-num").text(" ["+pst+"-"+pend+"/"+addFigure(${count})+"<%=lang.__("Items")%>　"+(page+1)+"/${externalListForm.pagesize}<%=lang.__("display page")%>]");


</c:if>

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

function loadNextpage(page) {
	var d = new Date();
	$('#content').mask("Loading...");
	if (currentSort == undefined || currentSort == -1 || (Array.isArray(currentSort) && currentSort.length == 0)) {
		$('#tablebody').load('${f:url('/page')}/externallist/tablepagebody/${externalListForm.menutaskid}/${externalListForm.menuid}/${externalListForm.metaid}/'+page+"?"+d.getTime(), function() {
			editTable();
			listupdate();
			$('#content').unmask();
		});

	}
	else {
		var sorttmp = currentSort.join(",")+","+currentSortAttr;
		$('#tablebody').load('${f:url('/page')}/externallist/tablepagebodysort/${externalListForm.menutaskid}/${externalListForm.menuid}/${externalListForm.metaid}/'+page+'/'+sorttmp+"?"+d.getTime(), function() {
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

function updateTable() {
	$('#editTable').trigger("update");
}

var ismobile = false;
</script>
</c:forEach>
<div id="tooltip"></div>


<%-- CSVファイル出力 --%>
<div id="csv-dialog" title="<%=lang.__("CSV file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as CSV file.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputcsv" modelAttribute="externalListForm">
			<% FormUtils.printToken(out, request); %>
			<input type="hidden" name="value" value="" />
			<input type="hidden" name="totalable" value="${listDto.totalable}">
			<input type="hidden" name="menuid" value="${externalListForm.menuid}">
			<input type="hidden" name="menutaskid" value="${externalListForm.menutaskid}">
			<input type="hidden" name="metaid" value="${externalListForm.metaid}">
			<input type="hidden" name="paging" value="${paging}">
			<c:if test="${paging}">
			<input type="checkbox" name="pageall" ><%=lang.__("output all pages")%><br>※<%=lang.__("It may take a very long time for much data.")%>
			</c:if>
		<c:forEach var="listDto" items="${listDtos}">
			<a href='javascript:outputCSV(document.getElementById("outputcsv"), "${listDto.styleId}");' class="dialog-button" >${!empty listDto.title ? f:h(listDto.title) : lang.__('CSV output')}</a>
		</c:forEach>
		</form:form>
	</div>
</div>

<%-- PDFファイル出力 --%>
<div id="pdf-dialog" title="<%=lang.__("PDF file output")%>" style="display:none;">
	<div>
		<%=lang.__("Output data displayed in the list as PDF file.")%><br>
	</div>
	<br>
	<div align="center">
		<form:form id="outputpdf" modelAttribute="externalListForm">
			<% FormUtils.printToken(out, request); %>
			<input type="hidden" name="dataList" value="">
			<input type="hidden" name="totalable" value="${listDto.totalable}">
			<input type="hidden" name="menuid" value="${externalListForm.menuid}">
			<input type="hidden" name="menutaskid" value="${externalListForm.menutaskid}">
			<input type="hidden" name="metaid" value="${externalListForm.metaid}">
			<input type="hidden" name="paging" value="${paging}">
			<c:if test="${paging}">
			<input type="checkbox" name="pageall" ><%=lang.__("output all pages")%><br>※<%=lang.__("It may take a very long time for much data.")%>
			</c:if>

		<c:forEach var="listDto" items="${listDtos}">
			<a href='javascript:outputPDF(document.getElementById("outputpdf"), "${listDto.styleId}");' class="dialog-button" >${!empty listDto.title ? listDto.title : lang.__('PDF output')}</a>
		</c:forEach>
		</form:form>
	</div>
</div>

<%-- 履歴保存 --%>
<div id="save-dialog" title="<%=lang.__("Save history")%>" style="display:none;">
	<div>
		<%=lang.__("Save data displayed on the list as history in CSV and PDF file.")%><br>
	</div>
	<br>
	<div align="center">
		<c:forEach var="listDto" items="${listDtos}">
			<a href="javascript:saveHistoryData('${listDto.styleId}');" class="dialog-button" >
				<c:if test="${!empty listDto.title}">${lang.__("History saving of {0}", f:h(listDto.title))}</c:if>
				<c:if test="${empty listDto.title}"><%=lang.__("Save history")%></c:if>
			</a>
		</c:forEach>
	</div>
</div>

<%-- 履歴表示 --%>
<div id="history-dialog" title="<%=lang.__("History display")%>" style="display:none;">
	<div>
		<%=lang.__("Please choose format to download you want.")%><br>
	</div>
	<br>
	<div align="center">
		<c:forEach var="listDto" items="${listDtos}">
			<a href="javascript:showHistoryData('${listDto.styleId}');" class="dialog-button" >
				<c:if test="${!empty listDto.title}">${lang.__("History display of {0}", f:h(listDto.title))}</c:if>
				<c:if test="${empty listDto.title}"><%=lang.__("History display")%></c:if>
			</a>
		</c:forEach>
	</div>
</div>
</c:otherwise>
</c:choose>
