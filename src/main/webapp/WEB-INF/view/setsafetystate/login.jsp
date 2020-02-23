<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../common/lang_resource.jsp" %>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<html>
<head>
<title><%=lang.__("Staff request email log in.")%></title>
</head>
<body>
<script type="text/javascript">
</script>
<h2><%=lang.__("Staff request email log in.")%></h2>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<form:form id="the_form" method="post" enctype="multipart/form-data" servletRelativeAction="/setsafetystate/dologin" modelAttribute="setsafetystateForm">
<% FormUtils.printToken(out, request); %>

<form:hidden path="encryptparam" value="${hiddenEncryptparam}"/>

<table border="1" style="font-size:12px;border:1px; padding:4px;">
	<tr>
		<td>
			<%=lang.__("Disaster name")%>：<BR>
			&nbsp;&nbsp;${f:h(trackName)}<BR>
			<%=lang.__("Group name<!--2-->")%>：<BR>
			&nbsp;&nbsp;${f:h(groupName)}<BR>
			<%=lang.__("Password")%>：<BR>
			&nbsp;&nbsp;<form:password path="password" /><BR>
			<input type="submit" value="<%=lang.__("Login")%>" />
		</td>
	</tr>
</table>

</form:form>

</body>
</html>
