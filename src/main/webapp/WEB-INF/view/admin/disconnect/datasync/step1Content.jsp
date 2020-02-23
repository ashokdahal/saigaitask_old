<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>
<script type="text/javascript" src="${f:url('/admin-js/js/ajaxfileupload.js')}" ></script>

<script type="text/javascript">
$(function() {
});

/**
 * アップロードを実行する
 */
function doUrlcheck() {
//alert("step1Content.jsp");
	return SaigaiTask.disconnect.submitForm('contents_datasync_checkurl_form','','<%=lang._E("URL checking...")%>');
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data synchronization")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_datasync_checkurl_form" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/datasync/checkUrl" modelAttribute="datasyncForm">
	<% FormUtils.printToken(out, request); %>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<div style="margin-bottom:1em;">
		<c:if test="${loginDataDto.localgovinfoid==0}">
			<h2 style="margin: 0px;"><%=lang.__("Specify local gov. ID")%></h2>
			<table border="1" class="form">
			<tr>
			<th width="150"><%=lang.__("Select local gov.")%></th>
			<td width="650">
				<form:select path="selectLocalgov" id="contentsDatasyncChekurlFormSelectLocalgov">
	    			<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
	        			<form:option value="${localgovSelectOption.key}">${localgovSelectOption.key}: ${f:h(localgovSelectOption.value)}</form:option>
	    			</c:forEach>
				</form:select>
			</td>
			</tr>
			</table>
		</c:if>
	</div>

	<h2 style="margin: 0px;"><%=lang.__("Confirmation of data synchronization")%></h2>
	<table border="1" class="form">
		<tr>
		</tr>
		<tr>
			<th rowspan="2"><%=lang.__("Please input URL of the NIED disaster information sharing system which does data synchronization.")%></th>
			<td><form:input path="cloudurl" /></td>
		</tr>
		<tr>
			<td>
				<a href="#" class="btn blue" onclick="return doUrlcheck();"><%=lang.__("Next")%></a>
			</td>
		</tr>
	</table>
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>

