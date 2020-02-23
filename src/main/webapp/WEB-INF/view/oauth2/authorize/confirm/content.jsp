<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>

<script type="text/javascript">
function submitOAuthConfirmForm() {
	var form = document.getElementById("oauth_confirm_form");
	form.submit();
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1>${lang.__('OAuth authentication window')}</h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<div>
	<p>${lang.__("Application {0} attempts to access with your account privilege.", f:h(consumerDescription))}<br/>
	<%=lang.__("Permit?")%></p>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
    <c:forEach var="msg" items="${messages}">
		<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
	</c:forEach>
	<c:remove var="messages" scope="session"/>
</div>
<br/>
<div>
	<a href="#" class="btn blue" onclick="submitOAuthConfirmForm();"><%=lang.__("Allowed")%></a>
	<a href="#" class="btn blue" onclick="window.close();"><%=lang.__("Not allowed")%></a>
</div>

<form:form id="oauth_confirm_form" method="post" enctype="application/x-www-form-urlencoded" cssStyle="width: 500px;" servletRelativeAction="/oauth2/authorize/allow" modelAttribute="authorizeForm">
	<% FormUtils.printToken(out, request); %>
	<form:hidden path="oauthCallback"/>
	<form:hidden path="consumerKey"/>
</form:form>
