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
<title><%=lang.__("Staff request email input.")%></title>
</head>
<body>
<script type="text/javascript">
</script>
<h2><%=lang.__("Staff request email input.")%></h2>
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<form:form id="the_form" method="post" enctype="multipart/form-data" servletRelativeAction="/setsafetystate/save" modelAttribute="setsafetystateForm">
<% FormUtils.printToken(out, request); %>

<form:hidden path="encryptparam" value="${hiddenEncryptparam}"/>

<table border="1" style="font-size:12px;border:1px; padding:4px;">
	<tr>
		<td>
			<%=lang.__("Response selection")%>：<BR>
			&nbsp;&nbsp;
			<form:select path="selectedSafetystate"  id="safetystateSelect">
				<c:forEach var="safetystateList" items="${safetyStateListMap}">
	        		<form:option value="${safetystateList.key}">${f:h(safetystateList.key)}: ${f:h(safetystateList.value)}</form:option>
	    		</c:forEach>
			</form:select><BR>
			<%=lang.__("Comment insert")%>：<BR>
			&nbsp;&nbsp;<form:input path="comment" readonly="false" cssStyle="resize: none; width:100%" value="${comment}"/><BR>
			<input type="submit" value="<%=lang.__("Send")%>" />
		</td>
	</tr>
</table>

</form:form>

</body>
</html>
