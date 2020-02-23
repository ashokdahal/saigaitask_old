<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/lang_resource.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<link rel="stylesheet" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<link rel="stylesheet" href="${f:url('/css/jquery-ui-timepicker-addon.css')}" />
<link rel="stylesheet" href="${f:url('/css/screen.css')}"  type="text/css" media="screen" title="default" />
<%--
<link rel="stylesheet" type="text/css" href="${f:url('/css/base.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/layout.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/header.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/btn.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery-ui-button.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/extjs/resources/css/ext-all.css')}"/>
<link rel="stylesheet" type="text/css" href="${f:url('/css/thickbox.css')}"/>
<link rel="stylesheet" type="text/css" href="${f:url('/css/print.css')}" media="print" />
 --%>
<script src="${f:url('/js/jquery-1.10.2.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}" type="text/javascript"></script>
<%--
<script src="${f:url('/js/jquery.pngFix.pack.js')}" type="text/javascript"></script>
 --%>
<script type="text/javascript">
$(document).ready(function() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
	$(".menuebutton").click(function(){
		if($(this).hasClass("show")){
			//非表示にする
			var ids = $(this).attr("ids");
			$(this).removeClass("show");
			$(".menueContent[ids="+ids+"]").removeClass("show");
		}else{
			//表示にする
			var ids = $(this).attr("ids");
			$(this).addClass("show");
			$(".menueContent[ids="+ids+"]").addClass("show");
		}
	});
	$(".pagelink button").click(function(){
		window.parent.location.href = "${f:url('/page/?menuid=')}"+$(this).attr('pageid');
	});
	$("table").tablesorter({
		widgets: ['zebra']
	});
});
</script>
<style>
.menuebutton {
	background: #ffffff;
	border: 1px solid #000;
	padding: 2px 10px;
	cursor: pointer;
	border-radius: 8px;
	font-size: 15px;
}
.menuebutton.show {
	background: #ffff00;
}
.menueContent {
	display:none;
}
.menueContent.show {
	display:block;
}
.buttons {
	margin-right: auto;
	margin-left: auto;
	align: center;
	border-collapse : separate;
	border: none;
}
table {
	border: 1px solid #000;
	border-collapse : collapse;
	font-size: 12px;
}
th, td {
	border: 1px solid #000;
	padding: 2px 5px;
}
table th {
	color: #fff;
	background: #4f81bd;
}
table td {
}
.pagelink{
	font-size: 12px;
}
.pagelink button{

}
</style>
</head>
<body>
<div style="font-size: 0.8em;">
<%=lang.__("Display important response status after disaster as timeline.")%>
</div>
<hr>
<%--ボタン --%>
<div style="text-align:center;">
<table class="buttons">
<c:forEach var="e" varStatus="s" items="${menuelist }">
	<c:if test="${s.index%3==0 }"><tr></c:if>
	<td class="menuebutton" ids="${e['id']}">${f:h(e["name"])}</td>
	<c:if test="${s.index%3==2 }"></tr></c:if>
</c:forEach>
</table>
</div>
<hr>
<%-- 各データの表示 --%>
<c:forEach var="e" varStatus="s" items="${menuelist }">
	<div class="menueContent" ids="${e['id']}">
		<h4>${f:h(e["name"])}</h4>
		<table class="tablesorter">
		<%-- ヘッダの構成 --%>
		<thead>
		<tr>
		<c:if test="${subths[s.index] == null}">
		<c:forEach var="thse" varStatus="thss" items="${ths[s.index] }">
			<th>${f:h(thse)}</th>
		</c:forEach>
		</c:if>
		<c:if test="${subths[s.index] != null}">
		<c:forEach var="thse" varStatus="thss" items="${ths[s.index] }">
			<c:if test="${!thss.last || subths[s.index] == null}">
				<th rowspan="2">${f:h(thse)}</th>
			</c:if>
			<c:if test="${thss.last && subths[s.index] != null}">
				<th colspan="${ fn:length( subths[s.index] ) }">${f:h(thse)}</th>
			</c:if>
		</c:forEach>
		</c:if>
		</tr>
		<%-- 時間・状態 --%>
		<tr>
		<c:if test="${subths[s.index] != null}">
			<c:forEach var="subthse" varStatus="subthss" items="${subths[s.index] }">
				<th>${fal:h(subthse) }</th>
			</c:forEach>
		</c:if>
		</tr>
		</thead>
		<tbody>
		<%-- データの出力 --%>
		<c:forEach var="de" varStatus="ds" items="${datas[s.index] }">
			<tr>
			<c:forEach var="dde" varStatus="dds" items="${datas[s.index][ds.index] }">
				<c:if test="${colors[s.index][ds.index][dds.index] != null}">
				<td style="background: ${colors[s.index][ds.index][dds.index] }"><c:out value="${f:h(dde)}"></c:out></td>
				</c:if>
				<c:if test="${colors[s.index][ds.index][dds.index] == null}">
				<td><c:out value="${f:h(dde)}"></c:out></td>
				</c:if>
			</c:forEach>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		<div class="error_message">${errorMessages[s.index] }</div>
		<!--<div class="pagelink">
		<c:forEach var="pagenamese" varStatus="pagenamess" items="${pagenames[s.index] }">
		${pagetasknames[s.index][pagenamess.index] }:
		<button pageid="${pageids[s.index][pagenamess.index] }">
			${f:h(pagenames[s.index][pagenamess.index])}
		</button>
		</c:forEach>
		</div>-->
	</div>
</c:forEach>
</body>
</html>
