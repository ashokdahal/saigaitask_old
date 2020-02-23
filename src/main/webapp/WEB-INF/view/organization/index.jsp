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
<meta charset="utf-8">
<title><%=lang.__("Select organization")%></title>
	<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/jquery-ui.js')}"></script>
	<script type="text/javascript" src="${f:url('/admin-js/js/jquery.jqGrid.min.js')}" ></script>
	<link type="text/css" media="screen" href="${f:url('/ui.jqgrid.css')}" rel="stylesheet" />
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
			var rownum = $("input[name='rownum']:checked").val();
			if (!rownum) {
				alert('<%=lang.__("Select organization.")%>');
				return false;
			}
			var arr = {};
			var tblrow = document.getElementById('organizationList').rows[rownum];
			arr['organizationType'] = $("#organizationType").val();
 			arr['organizationName'] = tblrow.cells[1].innerHTML;
 			arr['organizationCode'] = tblrow.cells[2].innerHTML;
 			arr['organizationDomainName'] = tblrow.cells[3].innerHTML;
 			arr['officeName'] = tblrow.cells[4].innerHTML;
 			arr['officeNameKana'] = tblrow.cells[5].innerHTML;
 			arr['officeLocationArea'] = tblrow.cells[6].innerHTML;
 			arr['phone'] = tblrow.cells[7].innerHTML;
 			arr['fax'] = tblrow.cells[8].innerHTML;
 			arr['email'] = tblrow.cells[9].innerHTML;
 			arr['officeDomainName'] = tblrow.cells[10].innerHTML;
			self.parent.setOrganization(arr);
			self.parent.tb_remove();
		});
	});
	</script>
</head>
<body>

<div class="container">
	<div class="dlghead">
		<h3 class="dlgtitle"><%=lang.__("Select organization")%></h3>
	</div>

	<div id="default_grid">
		<input type="hidden" id="organizationType" value="${organizationType}" />
		<table class="table01" id="organizationList">
			<tr align="center" height="27px">
				<th></th>
				<th><%=lang.__("Organization name")%></th>
				<th><%=lang.__("Local public")%><br /><%=lang.__("Organization code")%></th>
				<th><%=lang.__("Organization")%><br /><%=lang.__("Domain")%></th>
				<th><%=lang.__("Department name")%></th>
				<th><%=lang.__("Department name")%><br /><%=lang.__("Kana")%></th>
				<th><%=lang.__("Department")%><br /><%=lang.__("Address")%></th>
				<th><%=lang.__("Department")%><br /><%=lang.__("Phone number")%></th>
				<th><%=lang.__("Department")%><br /><%=lang.__("FAX number")%></th>
				<th><%=lang.__("Department")%><br /><%=lang.__("E-mail address")%></th>
				<th><%=lang.__("Department")%><br /><%=lang.__("Domain")%></th>
			</tr>
			<c:forEach var="gov" varStatus="s" items="${organizationFormList}">
				<tr align="center" height="27px">
					<td><input type="radio" value="${f:h(gov.rownum)}" name="rownum" id="rownum" /></td>
					<td>${f:h(gov.organizationName)}</td>
					<td>${f:h(gov.organizationCode)}</td>
					<td>${f:h(gov.organizationDomainName)}</td>
					<td>${f:h(gov.officeName)}</td>
					<td>${f:h(gov.officeNameKana)}</td>
					<td>${f:h(gov.officeLocationArea)}</td>
					<td>${f:h(gov.phone)}</td>
					<td>${f:h(gov.fax)}</td>
					<td>${f:h(gov.email)}</td>
					<td>${f:h(gov.officeDomainName)}</td>
				</tr>
			</c:forEach>
		</table>
	</div>

	<c:if test="${organizationFound}">
	<div id="main">
		<form:form modelAttribute="organizationForm">
			<% FormUtils.printToken(out, request); %>
			<table width="100%" border="0">
				<tr>
					<td align="right"><input type="submit" name="index" id="regist" value="<%=lang.__("Select")%>" alt="<%=lang.__("Select")%>" /></td>
				</tr>
			</table>
		</form:form>
	</div>
	</c:if>

</div>
<!-- end .container -->
</body>
</html>
