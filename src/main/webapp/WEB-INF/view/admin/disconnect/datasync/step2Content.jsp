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
	<c:if test="${oAuthCode==null}">
		$("#step2_content_viewarea").css("display", "none");
		var oauthAuthorozeUrlBase = "${f:h(cloudurl)}";
		var oauthAuthorozeUrl = "";
		if(oauthAuthorozeUrlBase.endsWith("/")){
			oauthAuthorozeUrl = oauthAuthorozeUrlBase + "oauth2/authorize/";
		}else{
			oauthAuthorozeUrl = oauthAuthorozeUrlBase + "/oauth2/authorize/";
		}
	 	var step2Url = "${f:h(step2Url)}";

		var strval = oauthAuthorozeUrl + "?cid=${oAuthCommunityId}&response_type=code&client_id=${oAuthConsumerKey}&redirect_uri="
			+ step2Url+"?<%=FormUtils.getTokenParam(request)%>";
		document.location.href = oauthAuthorozeUrl
				+ "?cid=${oAuthCommunityId}&response_type=code&client_id=${oAuthConsumerKey}&redirect_uri="
				+ step2Url+"?<%=FormUtils.getTokenParam(request)%>";
	</c:if>
});

function doDataSend() {
//alert("step2Content.jsp");
	return SaigaiTask.disconnect.submitForm('contents_datasync_step2form', '','<%=lang._E("Data making/sending...")%>');
}
</script>

<div id="step2_content_viewarea">
<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Disaster data synchronization")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_datasync_step2form" method="post" enctype="multipart/form-data" cssStyle="width: 800px;" servletRelativeAction="/admin/disconnect/datasync/trackmapping" modelAttribute="datasyncForm">
	<% FormUtils.printToken(out, request); %>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<form:hidden path="cloudurlChecked" />
	<form:hidden path="targetLocalgovinfoid" />

	<div style="margin-bottom:1em;">
	</div>

	<h2 style="margin: 0px;"><%=lang.__("Starting of data synchronization")%></h2>
	<table border="1" class="form">
		<tr>
		</tr>
		<tr>
			<th rowspan="2"><%=lang.__("This data synchronization with a NIED disaster information sharing system of URL is begun.")%></th>
			<td>${f:h(cloudurl)}</td>
		</tr>
		<tr>
			<td>
				<a href="#" id="datasyncBottun" class="btn blue" onclick="return doDataSend();"><%=lang.__("Next")%></a>
			</td>
		</tr>
	</table>

	<table border="1" class="form">
		<tr>
			<th rowspan="2"><%=lang.__("Synchro method")%></th>
			<td>
				<form:radiobutton path="syncAll" value="1" />
				<%=lang.__("All history")%>
				<form:radiobutton path="syncAll" value="0" />
				<%=lang.__("Only latest data")%>
			</td>
		</tr>
		<tr>
			<td>
				<form:radiobutton path="syncAttachedFile" value="1" />
				<%=lang.__("Include attached file")%>
				<form:radiobutton path="syncAttachedFile" value="0" />
				<%=lang.__("Not include attached file")%>
			</td>
		</tr>
	</table>
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
</div>
</div>
