<%@include file="../../common/lang_resource.jsp" %>
<link rel="stylesheet" type="text/css" href="${f:url('/css/form4_style.css')}" />
<script>

//ファイル出力
function saveData(){
	if (!confirm('<%=lang._E("Save No.4 format data to file. \n Are you sure ?")%>')) return;
	do{
		document.forms[0].action = "${f:url('/page/report4form/insert')}";
		document.forms[0].submit();
	}while(0);
}

//ファイル出力後の読み込みであればダイアログを表示する
window.onload = function(){
	var flag = "${outputDialogFlag}";
	if(flag == "true"){
		alert("<%=lang.__("File saved.")%>");
	}
}

function showData() {
	do{
		document.forms[0].action = "${f:url('/page/report4form/showdata')}";
		document.forms[0].submit();
	}while(0);
}

</script>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<form:form modelAttribute="report4formForm">

<form:hidden path="trackdataid" />
<form:hidden path="reporttypeinfoid" />
<form:hidden path="userinfoid" />
<form:hidden path="menuid" />
<form:hidden path="fileurl" />
<form:hidden path="reportdataid" />
<form:hidden path="deleted" />
<form:hidden path="note" />
<form:hidden path="subtotal" />
<form:hidden path="atotal" />

<div class="container">
  <div class="header">
    <!-- end .header --></div>
  <div class="content">
  <table width="900" border="0">
  <tr>
    <td><span class="head">
		<form:select path="showtime_yy" cssStyle="width:70px;">
			<c:forEach var="e" varStatus="s" items="${report4formForm.showtime_yy_list}">
				<form:option value="${f:h(report4formForm.showtime_yy_list[s.index])}">${f:h(showtime_yy_list[s.index])}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__("Year")%>
		<form:select path="showtime_mm" cssStyle="width:45px;">
			<c:forEach var="i" begin="1" end="12" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__("Month")%>
		<form:select path="showtime_dd" cssStyle="width:45px;">
			<c:forEach var="i" begin="1" end="31" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__("Day")%>
		<form:select path="showtime_hh" cssStyle="width:45px;">
			<c:forEach var="i" begin="0" end="23" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
        <%=lang.__("Hour")%>
		<form:select path="showtime_mm2" cssStyle="width:45px;">
			<c:forEach var="i" begin="0" end="59" step="1">
			<form:option value="${i}">${i}</form:option>
			</c:forEach>
		</form:select>
		<%=lang.__(" minutes point in time info")%></span>
    <input type="button" name="showdata" value="<%=lang.__("Display")%>" onclick="javascript:showData()"></td>
    <td>&nbsp;</td>
  </tr>
</table>

	<jsp:include page="form.jsp" />

<!-- end .content --></div>
<!-- end .container --></div>
</form:form>
