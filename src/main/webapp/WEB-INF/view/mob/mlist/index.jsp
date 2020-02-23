<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/jsp_lang.jsp" %>
<html>
<head>
<meta charset="utf-8" />
<mata name="viewport" content="width=device-width,initial-scale=1">
<jsp:include page="/WEB-INF/view/common/mobhead.jsp"></jsp:include>
<style type="text/css">
<!--
.tablesorter TD.sum {
	background-color: #1E4B78;
	color: white;
	text-align: center;
}
//-->
</style>

</head>
<body>
<div data-role="page">

	<div data-role="header">
		<h1><%=lang.__("NIED disaster information sharing system")%> </h1>
	</div><!-- /header -->

	<div data-role="content">


	<form>
		<% FormUtils.printToken(out, request); %>
	    <input id="filterTable-input" data-type="search" />
	</form>
<c:if test="${addable}">
	<a href="${f:url('/mob/mlist/create/')}${listForm.menutaskid}/${listForm.menuid}/${e[key]}" data-role="button" data-inline="true" data-theme="d" data-rel="dialog" data-transition="pop"><%=lang.__("Add new")%></a>
</c:if>
    <table id="tbl" data-role="table" data-mode="columntoggle" data-filter="true" data-input="#filterTable-input" class="tablesorter ui-responsive ui-shadow">
      <!--ヘッダー行で列選択の優先順位を設定-->
      <thead>
        <tr>
<c:if test="${editable}">
        <th><%=lang.__("Edit")%></th>
</c:if>
<c:if test="${key == 'gid' || key == '_orgid'}">
        <th>Map</th>
</c:if>
        <c:forEach var="e" varStatus="s" items="${colinfoItems}">
        <c:set var="pri" value="${s.index+1}"/>
        <c:if test="${pri <= 5}"><c:set var="pri" value="1"/></c:if>
        <c:if test="${pri > 5}"><c:set var="pri" value="6"/></c:if>
          <th data-priority="${pri}">${f:h(e.name)}</th>
        </c:forEach>
        </tr>
      </thead>
      <tbody>
<c:forEach var="e" varStatus="s" items="${result}">
	<c:if test="${!filterIds[f:h(e[key])]}">
		<tr class="gray">
	</c:if>
	<c:if test="${filterIds[f:h(e[key])]}">
		<tr>
	</c:if>

<c:if test="${editable}">
	<td><a href="${f:url('/mob/mlist/detail/')}${listForm.menutaskid}/${listForm.menuid}/${e[key]}" data-role="button" data-icon="edit" data-iconpos="notext" data-rel="dialog" data-transition="pop">&nbsp;</a></td>
</c:if>
<c:if test="${key == 'gid' || key == '_orgid'}">
	<c:if test="${e.theGeom!=null}">
	<td><a data-ajax="false" data-role="button" data-icon="arrow-r" data-iconpos="notext" onClick="openMap('${e.theGeom}')"></a></td>
	</c:if>
	<c:if test="${e.theGeom==null}">
	<td></td>
	</c:if>
</c:if>
	<c:forEach var="f" varStatus="t" items="${colinfoItems}">
		<c:set var="hi" value="" />
		<c:if test="${f.highlight}"><c:set var="hi" value=" highlight" /></c:if>
		<c:set var="st" value="" />
		<c:if test="${styleMap[f.id] != null}">
			<c:forEach var="g" varStatus="u" items="${styleMap[f.id]}">
				<c:if test="${g.val == e[f.attrid]}">
					<c:set var="st" value="${f:h(g.style)}" />
				</c:if>
			</c:forEach>
		</c:if>

		<%-- データ変換 --%>
		<c:set var="colVal" value="${e[f.attrid]}"/>

			<td style="${st}">${f:h(colVal)}</td>

	</c:forEach>
	</tr>
</c:forEach>
      </tbody>
    </table>

	</div><!-- /content -->
</div><!-- /page -->

<div data-role="page" id="pgSpotMap">
  <div data-role="header" ><h2>Map</h2></div>
  <div data-role="content">
    <div id="mMap" class="ui-shadow"></div>
    <!-- class="ui-shadow"で要素に影をつける。<div id="mMap"> でも地図表示はOK-->
  </div>
</div>
</body>
</html>
