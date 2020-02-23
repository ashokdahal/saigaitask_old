<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/lang_resource.jsp" %>
<head>
<meta charset="utf-8" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/jquery-ui.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.css" />
<style type="text/css">
html {
 display: table;
 width: 100%;
 height: 100%;
}
body {
 display: table-cell;
 vertical-align: middle;
    background-color: #444;
}
table {
	background-color: #ffffff;
	border-top:#ffffff 3px double;
	border-collapse: collapse;
	font-size: 11px;
	width: 50%;
	color:#333333;
	margin-left: auto;
	margin-right: auto;
}
table th.t_top {
	border: #dcdddd 1px solid;
	background-color: #efefef;
	text-align: left;
	padding: 10px;
}
tr:nth-child(even) td {
    border: #dcdddd 1px solid;
	background-color: #fff;
	text-align: left;
	padding: 10px;
	vertical-align: top;
}
tr:nth-child(odd) td {
    border: #dcdddd 1px solid;
	background-color: #f7f8f8;
	text-align: left;
	padding: 10px;
	vertical-align: top;
}</style>
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.min.js"></script>
<script type="text/javascript">
function regist(msg) {
	if (!confirm(msg))
		return false;

	$('#container').mask("Loading...");
	return true;
}
</script>
</head>
<body >



<form:form modelAttribute="demoForm" servletRelativeAction="/demo/start">
<% FormUtils.printToken(out, request); %>
<div id="container">
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<table>
  <tr>
  	<th class="t_top"></th>
    <%--<th class="t_top"><%=lang.__("Disaster type")%></th> --%>
    <th class="t_top"><%=lang.__("Name")%></th>
    <th class="t_top"><%=lang.__("Notes")%></th>
  </tr>
<c:forEach var="e" varStatus="s" items="${demoInfoItems }">
  <tr >
  	<td cssStyle="width:10px;"><form:radiobutton path="id" value="${e.id}"/></td>
    <%--<td >${f:h(disasterMap[e.disasterid])}</td>--%>
    <td >${f:h(e.name)}</td>
    <td >${f:h(e.note)}</td>
  </tr>
</c:forEach>
	<tr>
		<td colspan="4" style="text-align:right;"><input type="submit" name="start" value="<%=lang.__("Start demo")%>"  onclick="return regist('<%=lang.__("Are you sure to start demo?")%>');"></td>
	</tr>
</table>
</div>
</form:form>
</body>
</html>