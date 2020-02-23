<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html>
<%@include file="../common/jsp_lang.jsp" %>
<html>
<head>
<meta charset="utf-8" />
<title>${f:h(systemname)}</title>
<link rel="stylesheet" href="${f:url('/css/screen.css')}"  type="text/css" media="screen" title="default" />
<script src="${f:url('/js/jquery-1.10.2.min.js')}" type="text/javascript"></script>
<%--
<script src="${f:url('/js/jquery.pngFix.pack.js')}" type="text/javascript"></script>
 --%>
<script type="text/javascript">
$(document).ready(function(){
//$(document).pngFix( );
	reloadindex();
	setInterval(reloadindex, 20000);
});
function reloadindex() {
	//document.location.reload();
	$("#login-info").load('${f:url('triggerinfo/')}${localgovinfoid}');
}
</script>
</head>
<body id="login-bg">

<!-- Start: login-info -->
<div id="login-info">
	<!-- start logininfo_box -->
	<div id="logininfo_box_1" class="logininfo_box">
	</div>
	<!-- end locgininfof_box -->
</div>
<!-- end: login-info -->

<!-- Start: login-holder -->
<div id="login-holder">


	<!-- start logo -->
	<div id="logo-login">
		<!--<a href="index.html"><img src="images/shared/logo.png" width="156" height="40" alt="" /></a>-->
	</div>
	<!-- end logo -->

	<div class="clear"></div>

	<!--  start loginbox ..............-->
	<div id="loginbox">

			<div id="loginbox-title">${f:h(systemname)} <%=lang.__("Login")%></div>
			<p></p>
			<br />
	<form:form>
		<form:hidden path="returnpath"/>
	<!--  start login-inner -->
	<div id="login-inner">
		<table border="0" cellpadding="0" cellspacing="0">
<c:if test="${disasterItems != null }">
		<tr>
			<th><%=lang.__("Disaster type")%></th>
			<c:if test="${(disasterid == 0)}"><c:set var="dis" value="false" /></c:if>
			<c:if test="${(disasterid != 0)}">
				<c:set var="dis" value="true" />
				<form:hidden path="disasterid"/>
			</c:if>
			<td><form:select path="disasterid" cssClass="styledselect" disabled="${dis }">
			<form:option value=""> <%=lang.__("Select.")%> </form:option>
<c:forEach var="e" varStatus="s" items="${disasterItems}">
			<form:option value="${e.id}"> ${f:h(e.name)} </form:option>
</c:forEach>
		  </form:select> </td>
		</tr>
</c:if>
<c:if test="${groupInfoItems != null }">
		<tr>
			<th><%=lang.__("Group name<!--2-->")%></th>
			<td><form:select path="groupid" cssClass="styledselect">
<c:forEach var="e" varStatus="s" items="${groupInfoItems}">
			<form:option value="${e.id}"> ${f:h(e.name)} </form:option>
</c:forEach>
		  </form:select> </td>
		</tr>
</c:if>
		<tr>
			<th><%=lang.__("Password")%></th>
			<td><form:password path="password" value=""  onfocus="this.value=''" cssClass="login-inp" /></td>
		</tr>
		<tr>
			<th></th>
			<td>
				<c:if test="${(!bmaster)}"><input type="submit" name="logintask" class="submit-login" /></c:if>
				<!--<c:if test="${(disasterid == 0)}"><input type="submit" name="loginview" value="閲覧ログイン" /></c:if>-->
				<c:if test="${(bmaster)}"><input type="submit" name="loginmaster" value="<%=lang.__("Master login")%>" /></c:if>
			</td>
		</tr>
		</table>
	</div>
 	<!--  end login-inner -->
 	</form:form>
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
 </div>
 <!--  end loginbox -->

	</div>
<!-- End: login-holder -->
</body>
</html>
