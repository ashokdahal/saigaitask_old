<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/lang_resource.jsp" %>

<!DOCTYPE HTML>
<html>
<head>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta charset="utf-8">
<title><%=lang.__("Select template")%></title>
	<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/jquery-ui.js')}"></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery.jqGrid.min.js')}" ></script>
	<link href="${f:url('/css/template.css')}"  rel="stylesheet" type="text/css" />
	<style type="text/css">
	  .jqgfirstrow {
		  display: none;
	  }
	  .ui-state-default {
		  display: none;
	  }
	  .ui-jqgrid tr.jqgrow td {
		  white-space: pre-wrap;
	  }
	  .ui-jqgrid tr.jqgrow td textarea {
		  font-size: 12px
	  }
	</style>
<script type="text/javascript">
$(document).ready(function(){
	$("form").submit(function() {
		var tid = $("input[name='templateid']:checked").val();
		if (!tid) {
			alert('<%=lang.__("Please choose a template.")%>');
			return false;
		}
		self.parent.setTemplateId(tid);
		self.parent.tb_remove();
	});

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});
});
</script>
</head>
<body>

<script type="text/javascript">
	$(document).ready(function() {
		<%--使用するグリッドJSPをインクルードする。--%>
		<jsp:include page="template.jsp" flush="true" />
		<c:if test="${!noticeDefaultFound}">
			<jsp:include page="notice_default.jsp" flush="true" />
			init_notice_default();
			alert('<%=lang._E("Relate template to menu.\n Select an item related to the menu shown the list below.")%>');
		</c:if>
		<c:if test="${noticeDefaultFound}">
			<c:if test="${disableAddButton}">
				$("#template_add").attr("disabled", "disabled");
			</c:if>
			init_template();
			$("#main").show();
		</c:if>
		
		$('#lui_template').hide();
	});
</script>

<div class="container">
	<div class="dlghead">
		<h3 class="dlgtitle"><%=lang.__("Select template")%></h3>
	</div>

	<c:if test="${!noticeDefaultFound}">
		<div id="default">
			<div id="default_grid">
			</div>
			<table width="100%" border="0">
				<tr>
					<td align="right"><input type="button" name="registdef" id="registdef" value="<%=lang.__("Registration")%>" alt="<%=lang.__("Registration")%>" /></td>
				</tr>
			</table>
		</div>
	</c:if>

	<div id="main" style="display: none">
		<div id="main_grid">
		</div>
		<form:form modelAttribute="templateForm">
			<% FormUtils.printToken(out, request); %>
			<table width="100%" border="0">
				<tr>
					<td align="left"><input type="button" name="template_add" id="template_add" value="<%=lang.__("Add<!--2-->")%>" /></td>
					<td align="left"><input type="button" name="template_copy" id="template_copy" value="<%=lang.__("Copy")%>" /></td>
					<td align="left"><input type="button" name="template_del" id="template_del" value="<%=lang.__("Delete")%>" /></td>
					<td width="680" />
					<td align="right"><input type="submit" name="index" id="regist" value="<%=lang.__("Registration / instruction")%>" alt="<%=lang.__("Registration / instruction")%>" /></td>
				</tr>
			</table>
		</form:form>
	</div>
</div>
<!-- end .container -->
</body>
</html>
