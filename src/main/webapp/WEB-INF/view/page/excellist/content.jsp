<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>

<script type="text/javascript" src="${f:url('/js/msg.js')}"></script>
<link rel="stylesheet" type="text/css" href="${f:url('/css/form4_style.css')}" />

<style type="text/css">
<!--
	.listgrp { border:2px solid #ccc; width:300px; height: 150px; overflow-y: scroll; }
-->
#excelTabs{
	width:1050px;
}
#excelTabsContent{
	width:1050px;
}
.excellist_previewdev{
	width:1050px;
	border-style:none;
	border-color:black;
	border-width:1px;
	padding-top:5px;
	padding-bottom:5px;
	padding-left:5px;
	padding-right:5px;
}

.excellist_previewdev table {
	width: 1000px;
	table-layout: fixed;
}
.userinputs{
	width:100%;
	height:90%;
	box-sizing:border-box;
}

</style>
<script type="text/javascript">
$(document).ready(function(){
	$( "#excelTabs" ).tabs();
});

//OVERRIDE
function pdfDialog(){
	executePrint(0);
}

	// OVERRIDE
function executePrint(mode){
	var menuinfoid = $("#hiddenMenuid").val();
	var url1;
	if(mode === 0){
		url1 = SaigaiTask.contextPath+"/page/excellist/createexcellist";
	}else{
		url1 = SaigaiTask.contextPath+"/page/excellist/saveexcellist";
	}

	var url2 = SaigaiTask.contextPath+"/page/excellist/downloadexcellist?<%=FormUtils.getTokenParam(request)%>&menuinfoid="+menuinfoid;

	var menuinfoid = $("#hiddenMenuid").val();
	var showtime_yy = $("#showtime_yy").val();
	var showtime_mm = $("#showtime_mm").val();
	var showtime_dd = $("#showtime_dd").val();
	var showtime_hh = $("#showtime_hh").val();
	var showtime_mm2 = $("#showtime_mm2").val();

	var userinputs = "{";
	$(".userinputs").each(function(index, element){
		var elementId = $(element).attr('id');
		var elementValue = $(element).val();
		// 改行を置換しておく
		elementValue = elementValue.replace(/\r?\n/g, "\\n");

		userinputs += "\"" + elementId + "\":\"" + elementValue + "\","
 	});
	userinputs = userinputs.substr( 0, userinputs.length-1 ) ;
	userinputs = userinputs + "}";
	if(userinputs.length <= 1){
		userinputs = "";
	}

	$.ajax({
		type: "POST",
//		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		url : url1,
		data: {
			menuinfoid:menuinfoid,
			showtime_yy:showtime_yy,
			showtime_mm:showtime_mm,
			showtime_dd:showtime_dd,
			showtime_hh:showtime_hh,
			showtime_mm2:showtime_mm2,
			userinputs:userinputs
		},
		async : true,
		cache : false,
		success : function(data, dataType) {
			if(data.success){
				if(mode === 0){
					url2 = url2 + "&excelfile=" + data.excelfile
					window.location = url2;
				}else{
					alert("<%=lang.__("File saved.")%>");
				}
			}else{
				alert(data.message);
			}
		}
	});
//	alert("asynchronous");
}

function loadExcellistContent() {


	var url = SaigaiTask.contextPath+"/page/excellist/loadexcellist";
	var menuinfoid = $("#hiddenMenuid").val();
	var showtime_yy = $("#showtime_yy").val();
	var showtime_mm = $("#showtime_mm").val();
	var showtime_dd = $("#showtime_dd").val();
	var showtime_hh = $("#showtime_hh").val();
	var showtime_mm2 = $("#showtime_mm2").val();

	$.ajax({
		dataType: "json",
		type: "POST",
		headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		cache: false,
		url: url,
		data: {
			menuinfoid:menuinfoid,
			showtime_yy:showtime_yy,
			showtime_mm:showtime_mm,
			showtime_dd:showtime_dd,
			showtime_hh:showtime_hh,
			showtime_mm2:showtime_mm2
		},
		success: function(response) {
			if(response.error){
				alert(response.error);
			}
			else{
				$("#excelTabs").html( response.content );
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("<%=lang.__("Failed to create HTML from the excel template file.")%>");
		}
	});

}

function saveData(){
	if (!confirm('<%=lang._E("Save Excel file to file. \n Are you sure ?")%>')) return;
	do{
		executePrint(1);
	}while(0);
}


</script>

<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<form:form method="POST" id="the_form" modelAttribute="excellistForm">
	<% FormUtils.printToken(out, request); %>

<div class="container">
  <div class="header">
    <!-- end .header --></div>
  <div class="content">
  <table width="900" border="0">
  <tr>
    <td><span class="head">
		<form:select path="showtime_yy" id="showtime_yy" cssStyle="width:70px;">
			<c:forEach var="e" varStatus="s" items="${excellistForm.showtime_yy_list}">
				<form:option value="${f:h(excellistForm.showtime_yy_list[s.index])}">${f:h(excellistForm.showtime_yy_list[s.index])}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__("Year")%>
		<form:select path="showtime_mm" styleId = "showtime_mm" cssStyle="width:45px;">
			<c:forEach var="i" begin="1" end="12" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__("Month")%>
		<form:select path="showtime_dd" styleId = "showtime_dd" cssStyle="width:45px;">
			<c:forEach var="i" begin="1" end="31" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__("Day")%>
		<form:select path="showtime_hh" styleId = "showtime_hh" cssStyle="width:45px;">
			<c:forEach var="i" begin="0" end="23" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
        <%=lang.__("Hour")%>
		<form:select path="showtime_mm2" styleId = "showtime_mm2" cssStyle="width:45px;">
			<c:forEach var="i" begin="0" end="59" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__(" minutes point in time info")%></span>
		<input type="submit" value="<%=lang.__("Display")%>" />
    </td>
    <td>&nbsp;</td>
  </tr>
  </table>
<!--
  <span class="page_title"><%=lang.__("Excel file preview")%></span>
 -->
  <div  class="excellist_previewdev" id = "loadExcellistContent">
    <div id="excelTabs">
      <ul>
        <c:forEach var="i" items="${excelPages}" varStatus="status">
          <li><a href="#exceltab-${i}">Page${i}</a></li>
        </c:forEach>
      </ul>
      <div id="excelTabsContent">
      	${excelPagesContent}
      </div>

    </div>
  </div>
  <br />
  <form:hidden path="menuid" id="hiddenMenuid"/>
  <form:hidden path="menutaskid" id="hiddenMenutaskid"/>
</div>
</div>
</form:form>
<div id="templatemsg" style="display:none">
</div>

<%-- 履歴保存 --%>
<div id="save-dialog" title="<%=lang.__("Save history")%>" style="display:none;">
	<div>
		<%=lang.__("Save data displayed on the list as history in CSV and PDF file.")%><br>
	</div>
	<br>
	<div align="center">
		<a href="javascript:saveHistoryData('${listId}');" class="dialog-button" ><%=lang.__("History saving of summary table")%></a>
	</div>
</div>
