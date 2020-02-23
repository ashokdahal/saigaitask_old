<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../common/jsp_lang.jsp" %>
<html>
<head>
<meta charset="utf-8" />
<jsp:include page="/WEB-INF/view/common/mobhead.jsp"></jsp:include>
</head>
<body>
<div data-role="page">

	<div data-role="header">
		<h1><%=lang.__("NIED disaster information sharing system")%> </h1>
	</div><!-- /header -->

	<div role="main" class="ui-content">
		<!-- <div id="tabs" data-role="tabs">
			<div data-role="navbar">
			<ul>
			<c:forEach var="e" varStatus="s" items="${menuprocessInfoItems}">
				<li><a href="${f:url('/mob/process/')}${e.id}">${f:h(e.name) }</a></li>
			</c:forEach>
			</ul>
			</div>
		</div>-->

		<div data-role="content">
			<ul data-role="listview" data-inset="true" data-filter="false" data-divider-theme="e" class="ui-listview ui-listview-inset ui-corner-all ui-shadow">
			<li data-role="list-divider" role="heading" class="ui-li-divider ui-bar-e ui-first-child"><%=lang.__("Menu<!--2-->")%></li>
			<c:forEach var="e" varStatus="s" items="${menuprocessInfoItems}">
				<c:if test="${e.visible}">
					<li class="ui-last-child"><a href="${f:url('/mob/task/')}${e.id}">${f:h(e.name) }</a></li>
				</c:if>
			</c:forEach>
			</ul>
		</div><!-- /content -->
	</div>

</div><!-- /page -->
</body>
</html>