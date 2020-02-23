<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<script type="text/javascript">
$(function(){

	initLangCodeSelect();

	$(".importTypeRadio").click(function () {
		initLangCodeSelect();
	});

	$("#langCodeSelect").change(function () {
		var selectLangCode = $("#langCodeSelect").val();
		for(var i = 0; i < langItems.length ; i++){
			if(langItems[i].code === selectLangCode){
				if(langItems[i].name){
					$("#langNameText").val(langItems[i].name);
					break;
				}
			}
		}
	});
});

function initLangCodeSelect(){
	var importTypeValue = $("input:radio[name='importType']:checked").val();
	var url =  SaigaiTask.contextPath + "/admin/multilang/import/reloadlangCodeSelect";

	var nowSelectLangCode = $("#langCodeSelect").val();
	var nowInputLangName = $("#langNameText").val();

	$.ajax({
		dataType: "json",
		type: "POST",
//		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		cache: false,
		url: url,
		data: {
			selectImportType:importTypeValue
		},

		success: function(data) {
			$("#langCodeSelect").empty();
			$("#langCodeSelect").prop('disabled',true);
			langItems = data.langCodeList;

			var keepLangCode = false;
			var optionItems = new Array();
			for(var i = 0; i < data.langCodeList.length ; i++){
				optionItems.push(new Option(data.langCodeList[i].code, data.langCodeList[i].code));
				if(data.langCodeList[i].code === nowSelectLangCode){
					keepLangCode = true;
				}
			}
	        $("#langCodeSelect").append(optionItems);
			$("#langCodeSelect").prop('disabled',false);

			if(keepLangCode){
				$("#langCodeSelect").val(nowSelectLangCode);
			}else{
				$("#langNameText").val(langItems[0].name);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("<%=lang.__("Error : Failed to register")%>");
		}
	});
}


function importButtonClick(){
	$("#multilang-import-form").submit();
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
	<h1><%=lang.__("Language resource import")%></h1>
	<p><%=lang.__("Import language data of CSV file format.")%></p>
	<p><%=lang.__("File character code is UTF-8 and delimiter is tab character.")%></p>
</div>


<div id="content_main" class="ui-layout-center" style="text-align: left;">
	<c:if test="${loginDataDto.localgovinfoid==0}">
		<form:form id="multilang-import-form" method="post" enctype="multipart/form-data" servletRelativeAction="/admin/multilang/import/clickImportButton" modelAttribute="importExportForm">
			<div style="width: 800px;">
				<% FormUtils.printToken(out, request); %>

			    <c:forEach var="msg" items="${messages}">
					<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
				</c:forEach>
				<c:remove var="messages" scope="session"/>

				<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

				<h2><%=lang.__("Select import method")%></h2>
				<table border="1" class="form">
					<tr>
					<th>
						<span><%=lang.__("Select import method.")%></span>
					</th>
					<td>
						<form:radiobutton cssClass="importTypeRadio" id="importType-new" path="importType" value="1"/><%=lang.__("Create new language set")%>
						<BR>
						<form:radiobutton cssClass="importTypeRadio" id="importType-renew" path="importType" value="2"/><%=lang.__("Replace existing language set")%>
						<span>&nbsp;</span>
						<form:radiobutton cssClass="importTypeRadio" id="importType-update" path="importType" value="3"/><%=lang.__("Update existing language set")%>
					</td>
					</tr>
				</table>

				<h2><%=lang.__("Select language")%></h2>
				<table border="1" class="form">
					<tr>
					<th>
						<span><%=lang.__("Specify language to be imported.")%></span>
					</th>
					<td>
						<%=lang.__("Language code :")%>
						<form:select id="langCodeSelect" path="langCode">
			    			<c:forEach var="langCodeList" items="${langCodeList}">
			        			<form:option value="${langCodeList.code}">${langCodeList.code}</form:option>
			    			</c:forEach>
						</form:select>
						<span>&nbsp;</span>
						<%=lang.__("Language name :")%>
						<form:input styleId = "langNameText" path="langName" value="" />
					</td>
					</tr>
				</table>

				<h2><%=lang.__("Specify file")%></h2>
				<table border="1" class="form">
					<tr>
					<th>
						<span><%=lang.__("Specify import file.<!--2-->")%></span>
					</th>
					<td>
						<input type="file" name="langDataFile"/>
					</td>
					</tr>
				</table>

				<div style="float:left;">
					<a href="#" class="btn blue" onclick="importButtonClick();"><%=lang.__("Import")%></a>
				</div>

			</div>
		</form:form>
	</c:if>
</div>



