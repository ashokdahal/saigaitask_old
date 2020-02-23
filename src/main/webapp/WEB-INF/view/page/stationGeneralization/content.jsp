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
var contentTabPanel = null;
$(function() {
	// 外部リストの切り替えSelectは非表示にする
	$("#content_head #externallistselect-container").remove();

 	var contentTabs = $("#content-tabs");
 	// タブ化
 	contentTabs.tabs({
 		heightStyle: "content",
 		beforeLoad: function(event, ui) {
 			// マスク
 			contentTabs.mask("Loading...");

 			// Ajax 完了時の挙動を初期化
 			ui.ajaxSettings.complete = function(jqXHR, textStatus) {

 				window.textStatus = textStatus;
 				window.jqXHR = jqXHR;
 				// エラーなら
 				if(["error", "timeout", "abort", "parsererror"].indexOf(textStatus)!=-1) {
 					ui.panel.html("<%=lang.__("Can not read")%>");
 				}

 				if(!!jqXHR.responseText) {
 					ui.panel.html(jqXHR.responseText);
 				}

 				// ロード後にマスク解除
 	 			setTimeout(function() {
 	 				contentTabs.unmask();
 	 			}, 200);
 			}
 		},
 		load: function(event, ui) {
 			contentTabPanel = ui.panel;
 			// 自治体名のリンクを削除
 			$("#editTable tbody td[headers='column-0']").each(function() {
 				var localgovTd = $(this);
 				var localgovAnchor = $("a", localgovTd);
 				if(0<localgovAnchor.length) {
 	 				var localgovName = localgovAnchor.text();
 	 				localgovTd.text(localgovName);
 				}
 			});
 			// タブとタブの中身の高さを調整
 			contentTabs.css("overflow", "hidden");
 			var contentTabsPanel = $(".ui-tabs-panel", contentTabs);
 			var contentHeadHeight = $("#content_head").height();
 			var tabNavHeight = $(".ui-tabs-nav", contentTabs).height();
 			contentTabsPanel.height($("#content_main").height()-contentHeadHeight-tabNavHeight-10); // タブの高さ分は除く
 			contentTabsPanel.css("overflow", "auto");
// 			// 総括表テーブルの数値を右寄せ
// 			$(".generalization-table td").each(function(){
// 				if($.isNumeric($(this).text())) {
// 					$(this).css("text-align", "right");
// 				}
// 			});
 		}
 	});
	//ボタンのスタイル設定
	$(".dialog-button").button();
});

//CSVファイル出力
function csvDialog(){
	var csvDialog = $("#csv-dialog", contentTabPanel);
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
	var pdfDialog = $("#pdf-dialog", contentTabPanel);
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

</script>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<c:if test="${0<fn:length(pageDto.tabs)}">
<div id="content-tabs">
  <ul>
  <c:forEach var="tab" items="${pageDto.tabs}">
    <li><a href="${tab.url}">${f:h(tab.title)}</a></li>
  </c:forEach>
  </ul>
</div>
</c:if>
