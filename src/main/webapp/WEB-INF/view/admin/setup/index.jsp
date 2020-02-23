<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html>
<%@include file="../../common/lang_resource.jsp" %>
<html lang="ja">
<head><jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<meta charset="utf-8">
<meta name="description" content="<%=lang.__("It is monitoring/observation window of NIED disaster information sharing system.")%>">
<meta name=" keywords" content="<%=lang.__("NIED disaster information sharing system, monitoring/observation")%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />
<title><%=lang.__("Edit window : NIED disaster information sharing system")%></title>
</head>

<body>
<div id="wrapper">
	<div id="contents_setup">
	<h1><%=lang.__("Administrator setup window")%></h1>
	<!-- 
	<h2>eコミマップ 情報</h2>
    -->
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<form:form>
	<h2><%=lang.__("Management group info")%></h2>
		<form:hidden path="returnpath"/>
	<table border="1" class="form">
		<tr>
			<th><%=lang.__("Group name (NIED disaster information sharing system management group name)")%> </th>
			<td><form:input path="name" size="30"/>
				<br>
				<%=lang.__("(NIED disaster information sharing system group name)")%> </td>
		</tr>
		<tr>
			<th><%=lang.__("Password")%></th>
			<td><form:input path="password" size="30" value=""/><br>
			<%=lang.__("(Password to login to the NIED disaster information sharing system)")%></td>
		</tr>
		<tr>
			<th><%=lang.__("e-Com map user account")%> </th>
			<td><form:input path="ecomuser" size="30"/></td>
		</tr>
		<tr>
			<th><%=lang.__("e-Com map password")%> </th>
			<td><form:input path="ecompass" size="30" value=""/></td>
		</tr>
	</table>
		<div class="txtC">
			<input type="submit" name="insert" value="<%=lang.__("Registration")%>" />
		</div>
		</form:form>
	</div><!-- /#contents_setup -->
</div><!-- /#wrapper -->
</body>
</html>
