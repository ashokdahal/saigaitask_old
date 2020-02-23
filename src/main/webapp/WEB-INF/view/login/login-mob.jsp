<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/jsp_lang.jsp" %>
<html>
<head>
<meta charset="utf-8" />
<jsp:include page="/WEB-INF/view/common/mobhead.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	document.forms[0].setAttribute('data-ajax', 'false');
});
</script>
</head>
<body>
<div data-role="page">

	<div data-role="header">
		<h1>${f:h(loginForm.systemname)} <%=lang.__("Login")%></h1>
	</div><!-- /header -->

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<form:form modelAttribute="loginForm">
	<% FormUtils.printToken(out, request); %>
	<div data-role="content">
	<form:hidden path="localgovinfoid"/>
	<form:hidden path="returnpath"/>
	<form:hidden path="type"/>

<c:if test="${0<loginForm.localgovinfoid}">
	<%-- 記録データが無い場合は、災害種別を選択してログインする --%>
	<div id="tabs" data-role="tabs">
      <div data-role="navbar">
        <ul>
          <li><a href="#login-tabs-1" class="ui-btn-active"><%=lang.__("Disaster")%></a></li>
          <li><a href="#login-tabs-2"><%=lang.__("Normal")%></a></li>
        </ul>
      </div>
<div id="login-tabs-1">
	<c:choose>
	<c:when test="${fn:length(trackDatas) == 0}">
		<c:if test="${!disasterCombined}">
				<label for="disastertype"><strong><%=lang.__("Disaster type")%></strong></label>
				<c:if test="${(loginForm.disasterid != 0)}">
					<form:hidden path="disasterid"/>
				</c:if>
				<form:select path="disasterid" cssClass="styledselect">
				<c:if test="${fn:length(disasterItems)!=1}">
					<form:option value=""> <%=lang.__("Select.")%> </form:option>
				</c:if>
	<c:forEach var="e" varStatus="s" items="${disasterItems}">
				<form:option value="${e.id}"> ${f:h(e.name)} </form:option>
	</c:forEach>
			  </form:select>
		</c:if>
		<c:if test="${disasterCombined}">
			<input type="hidden" name="disasterid" value="1" >
		</c:if>
	</c:when>
	<%-- 記録データがある場合は、災害データを選択してログインする --%>
	<c:otherwise>
				<label for="disastername"><strong><%=lang.__("Disaster name")%></strong></label>
				<c:set var="dis" value="false" />
				<%-- 1つしかないなら、選ばせない --%>
				<c:if test="${fn:length(trackDatas) == 1}">
					<c:set var="dis" value="true" />
					<form:hidden path="trackdataid"/>
				</c:if>
					<form:select path="trackdataid" cssClass="styledselect" disabled="${dis}">
						<c:forEach var="e" varStatus="s" items="${trackDatas}">
							<form:option value="${e.id}"> ${f:h(e.name)} </form:option>
						</c:forEach>
					</form:select>
	</c:otherwise>
	</c:choose>

		<label for="groupid"><strong><%=lang.__("Group name<!--2-->")%></strong></label>
		<form:input path="groupid" value="" />
		<label for="basic"><strong><%=lang.__("Password")%></strong></label>
		<form:password path="password" value="" />

		<label for="langCode" class="select"><strong><%=lang.__("Language code")%></strong></label>
		<form:select path="langCode">
			<c:forEach var="e" varStatus="s" items="${multilangInfoItems}">
					<form:option value="${e.code}" > ${f:h(e.name)} </form:option>
			</c:forEach>
		</form:select>

		<input name="logintask" type="submit" value="<%=lang.__("Login")%>" />
</div>

<!--  平常時モードログイン -->
<div id="login-tabs-2">
<!-- Start: login-holder -->
<c:if test="${0<loginForm.localgovinfoid}">
				<label for="unitid"><strong><%=lang.__("Unit name<!--2-->")%></strong></label>
				<form:input path="unitid" value=""  onfocus="this.value=''" cssClass="login-inp" />
</c:if>
			<label for="password"><strong><%=lang.__("Password")%></strong></label>
			<input type="password" name="password2" value=""  onfocus="this.value=''" class="login-inp" />
			<label for="language"><strong><%=lang.__("Language code")%></strong></label>
				<form:select path="langCode" cssClass="styledselect">
					<c:forEach var="e" varStatus="s" items="${multilangInfoItems}">
						<form:option value="${e.code}" > ${f:h(e.name)} </form:option>
					</c:forEach>
				</form:select>
				<c:if test="${(!bmaster)}"><input type="submit" name="loginusual" class="submit-login" value="<%=lang.__("Login")%>" /></c:if>
				<c:if test="${(bmaster)}"><input type="submit" name="loginmasterusual" value="<%=lang.__("Master login")%>" /></c:if>
</div>
	</div><!-- End: tabs -->
</c:if>
	</div><!-- /content -->

</form:form>
<font color="red"><ul><c:forEach var="msg" items="${errors}"><li><c:out value="${f:h(msg)}" escapeXml="false"/></li></c:forEach></ul></font>
</div><!-- /page -->
</body>
</html>
