<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>
<script type="text/javascript">

function exportButtonClick(){
	var langCode = $("#langCodeSelect").val();
	var url = SaigaiTask.contextPath + "/admin/multilang/export/clickExportButton?<%=FormUtils.getTokenParam(request)%>&langCode="+langCode;
	location.href = url;
}
</script>

<style type="text/css">
  th{
  	width:40%;
  }
  td{
  	width:60%;
  }
</style>


<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Language resource export")%></h1>
	<p><%=lang.__("Export installed language data to CSV file with UTF-8 character code and tab delimiter.")%></p>
</div>


<div id="content_main" class="ui-layout-center" style="text-align: left;">
<c:if test="${loginDataDto.localgovinfoid==0}">
<div style="width: 800px;">
	<form:form id="multilang-export-form" method="post" enctype="multipart/form-data" servletRelativeAction="/admin/multilang/export/clickExportButton" modelAttribute="importExportForm">
		<% FormUtils.printToken(out, request); %>

	    <c:forEach var="msg" items="${messages}">
			<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
		</c:forEach>
		<c:remove var="messages" scope="session"/>

		<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

		<h2><%=lang.__("Select export language")%></h1>
		<table border="1" class="form">
			<tr>
			<th>
				<span><%=lang.__("Select export language.")%></span>
			</th>
			<td>
				<form:select path="langCode" id="langCodeSelect">
	    			<c:forEach var="langCodeList" items="${langCodeList}">
	        			<form:option value="${langCodeList.key}">${langCodeList.value}</form:option>
	    			</c:forEach>
				</form:select>
			</td>
			</tr>
		</table>


		<div style="float:left;">
			<a href="#" class="btn blue" onclick="exportButtonClick();"><%=lang.__("Export")%></a>
		</div>
	</form:form>
</div>
</c:if>
</div>



