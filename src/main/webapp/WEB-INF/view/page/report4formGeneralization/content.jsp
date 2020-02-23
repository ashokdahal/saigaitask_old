<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@include file="../../common/lang_resource.jsp" %>
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/form4_style.css')}" />
<style type="text/css">
.headline {
	border-left: 5px solid #48832C;
	padding: 3px 10px;
}

.table02 .head {
	text-align: center;
	background-Color: #e6e6e6;
}
</style>
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.list.js')}"></script>
<script>
var tabPanel = null;
$(function() {
	var reportTabs = $("#report-tabs");
	// タブ化
	reportTabs.tabs({
		heightStyle: "content",
		beforeLoad: function(event, ui) {
			reportTabs.mask("Loading...");
		},
		load: function(event, ui) {
			activeGeneralizationTab = ui.tab.hasClass("generalization-tab");
			activeSummaryTab = ui.tab.hasClass("summary-tab");
			activeDetailTab = ui.tab.hasClass("detail-tab");
			window.ui = ui;
			console.log("ui.tab");
			console.log(ui.tab);
			tabPanel = ui.panel;
			// 詳細タブの form を編集不可
			// とりあえず、全体で
			reportTabs.find("input,select,textarea").attr("readonly", true);
			// 自治体名のリンクを削除
			$("#summarylist td[headers='column-0']").each(function() {
				var localgovTd = $(this);
				var localgovName = $("a", localgovTd).text();
				localgovTd.text(localgovName);
			});
			// タブとタブの中身の高さを調整
			reportTabs.css("overflow", "hidden");
			var reportTabsPanel = $(".ui-tabs-panel", reportTabs);
			reportTabsPanel.height($("#content_main").height()-60); // タブの高さ分は除く
			reportTabsPanel.css("overflow", "auto");
			// 総括表テーブルの数値を右寄せ
			$(".generalization-table td").each(function(){
				if($.isNumeric($(this).text())) {
					$(this).css("text-align", "right");
				}
			});
			setTimeout(function() {
				reportTabs.unmask();
			}, 200);
		}
	});
	// データ選択Select
	var selects = $("select[name='reportdataids']");
	var refreshGeneralizationTab = function() {
		// 選択中の４号様式データIDを取得
		reportdataidsParam = "";
		selects.each(function() {
			var reportdataid = $(this).val();
			if(reportdataid!=null && $.isNumeric(reportdataid)) {
				reportdataidsParam += "&reportdataids="+reportdataid;
			}
		});
		// 集計表タブ
		var summaryUrl = "${f:url('/page/report4formGeneralization/summaryTab?')}";
		summaryUrl += "menuid="+SaigaiTask.PageURL.params.menuid;
		summaryUrl += "&menutaskid="+SaigaiTask.PageURL.params.menutaskid;
		summaryUrl += reportdataidsParam;
		var summaryAnchor = $(".summary-tab a", reportTabs).eq(0);
		summaryAnchor.attr("href", summaryUrl);
		var tabIdx = $("li a", reportTabs).index(summaryAnchor);
		//reportTabs.tabs("load", tabIdx);

		// 総括表タブ
		var generalizationUrl = "${f:url('/page/report4formGeneralization/generalizationTab?')}";
		generalizationUrl += "menuid="+SaigaiTask.PageURL.params.menuid;
		generalizationUrl += reportdataidsParam;
		var generalizationAnchor = $(".generalization-tab a", reportTabs).eq(0);
		generalizationAnchor.attr("href", generalizationUrl);
		var tabIdx = $("li a", reportTabs).index(generalizationAnchor);
		//reportTabs.tabs("load", tabIdx);
	};
	// 「報告日時から４号様式を選択」Select.onChange を初期化
	for(var idx in selects) {
		var select = selects.eq(idx);
		var initOnChangeSelect = function(select, idx) {
			select.on("change", function() {
				// 詳細タブ
				var reportUrl = "${f:url('/page/report4formGeneralization/detailTab?')}";
				var reportdataid = $(this).val();
				var anchor = $(".detail-tab a", reportTabs).eq(idx);
				anchor.attr("href", reportUrl + "reportdataid=" + reportdataid);
				var tabIdx = $("li a", reportTabs).index(anchor);
				//reportTabs.tabs("load", tabIdx);

				// 総括表タブの更新
				refreshGeneralizationTab();

				// 開いているタブをリロード
				var activeTabIdx = reportTabs.tabs("option", "active");
				reportTabs.tabs("load", activeTabIdx);
			});
		};
		initOnChangeSelect(select, idx);
	}
	// 総括表タブの更新
	refreshGeneralizationTab();
	// 開いているタブをリロード
	var activeTabIdx = reportTabs.tabs("option", "active");
	reportTabs.tabs("load", activeTabIdx);
	//ボタンのスタイル設定
	$(".dialog-button").button();
});

//CSVファイル出力
function csvDialog(){
	var csvDialog = $("#csv-dialog", tabPanel);
	if (csvDialog.length == 0) {
		// ダイアログを２回目開いたときに、#csv-dialog が body 直下に移動しているため
		if(SaigaiTask.Page.csvDialog!=null) {
			SaigaiTask.Page.csvDialog.dialog();
		}
		else {
			alert("<%=lang.__("In this tab, you can not output CSV file.")%>");
		}
	}
	else {
		csvDialog.dialog({
			title: "<%=lang.__("CSV output")%>",
			minWidth: 450,
			maxHeight: 500,
			modal: true
		});
		SaigaiTask.Page.csvDialog = csvDialog;
	}
}

//PDFファイル出力
function pdfDialog(){
	var pdfDialog = $("#pdf-dialog", tabPanel);
	if (pdfDialog.length == 0) {
		// ダイアログを２回目開いたときに、#pdf-dialog が body 直下に移動しているため
		if(SaigaiTask.Page.pdfDialog!=null) {
			SaigaiTask.Page.pdfDialog.dialog();
		}
		else {
			alert("<%=lang.__("In this tab, you can not output PDF file.")%>");
		}
	}
	else {
		pdfDialog.dialog({
			title: "<%=lang.__("PDF output")%>",
			minWidth: 450,
			maxHeight: 500,
			modal: true
		});
		SaigaiTask.Page.pdfDialog = pdfDialog;
	}
}
//保存
function saveData(){
	var saveDialog = $("#save-dialog", tabPanel);
	if (saveDialog.length == 0) {
		// ダイアログを２回目開いたときに、#save-dialog が body 直下に移動しているため
		if(SaigaiTask.Page.saveDialog!=null) {
			SaigaiTask.Page.saveDialog.dialog();
		}
		else {
			alert("<%=lang.__("In this page, you can not save.")%>");
		}
	}
	else {
		saveDialog.dialog({
			title: "<%=lang.__("Save")%>",
			minWidth: 450,
			maxHeight: 500,
			modal: true
		});
		SaigaiTask.Page.saveDialog = saveDialog;
	}
}
//履歴表示
function showHistory(){
	var historyDialog = $("#history-dialog", tabPanel);
	if (historyDialog.length == 0) {
		// ダイアログを２回目開いたときに、#save-dialog が body 直下に移動しているため
		if(SaigaiTask.Page.historyDialog!=null) {
			SaigaiTask.Page.historyDialog.dialog();
		}
		else {
			alert("<%=lang.__("In this page, you can not indicate history.")%>");
		}

	}
	else {
		historyDialog.dialog({
			title: "<%=lang.__("History display")%>",
			minWidth: 450,
			maxHeight: 500,
			modal: true
		});
		SaigaiTask.Page.historyDialog = historyDialog;
	}
}
</script>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<span class="headline"><%=lang.__("Select No.4 format from reporting date and time")%></span>
<table class="table02">
  <thead>
    <tr>
      <td class="head"><%=lang.__("Local gov. name")%></td>
      <td class="head"><%=lang.__("Reporting date and time")%></td>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="trackgroupData" items="${trackgroupDatas}">
    <c:set var="cityTrackData" value="${trackgroupData.cityTrackData}"/>
    <tr>
      <td>${f:h(cityTrackData.localgovInfo.city)}</td>
      <td>
        <select name="reportdataids" >
          <c:set var="reportDatas" value="${trackdataid2reportDatas[cityTrackData.id]}" />
          <c:choose>
            <c:when test="${0<fn:length(reportDatas)}">
              <c:forEach var="reportData" items="${reportDatas}">
              <option value="${reportData.id}">${reportData.reportcontentDatas[0].reporttime}</option>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <option><%=lang.__("Data is not found.")%></option>
            </c:otherwise>
          </c:choose>
        </select>
      </td>
    </tr>
    </c:forEach>
  </tbody>
</table>

<br/>

<div id="report-tabs">
  <ul>
    <li class="generalization-tab"><a href="${f:url('/page/report4formGeneralization/tabInitialize')}"><%=lang.__("Summary table")%></a></li>
    <c:if test="${showSummaryTab}">
    <li class="summary-tab"><a href="${f:url('/page/report4formGeneralization/tabInitialize')}"><%=lang.__("Aggregation table")%></a></li>
    </c:if>
    <%-- 市町村の詳細タブ --%>
    <c:forEach var="trackgroupData" items="${trackgroupDatas}">
      <c:set var="cityTrackData" value="${trackgroupData.cityTrackData}"/>
      <li class="detail-tab"><a href="${f:url('/page/report4formGeneralization/detailTab?reportdataid=')}${trackdataid2reportDatas[cityTrackData.id][0].id}">${cityTrackData.localgovInfo.city}</a></li>
    </c:forEach>
  </ul>
</div>

