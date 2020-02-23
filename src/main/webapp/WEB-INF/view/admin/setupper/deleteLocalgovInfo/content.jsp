<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Municipality Info > Municipality Deletion")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<div>
<c:if test="${deleteLocalgovInfoForm.success==false}">
<span><%=lang.__("After municipality deletion completed, municipality setting, disaster data shown below will be all deleted.")%></span>
</c:if>
<c:if test="${deleteLocalgovInfoForm.success==true}">
<span>${lang.__('Municipality ID={0} deleted.', deleteLocalgovInfoForm.localgovinfoid)}</span>
</c:if>

<form:form>
	<font color="red"><ul>
    <c:forEach var="error" items="${action_errors}">
		<li><c:out value="${f:h(error)}" escapeXml="false"/></li>
	</c:forEach>
	</ul></font>
	<c:remove var="action_errors" scope="session"/>

    <c:forEach var="msg" items="${messages}">
		<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
	</c:forEach>
	<c:remove var="messages" scope="session"/>
</form:form>
</div>
<br/>

<c:forEach var="listDto" items="${listDtos}">
<table border="1" cellpadding="3" cellspacing="2" id="${listDto.styleId}" class="tablesorter shortline" style="margin-left:10px;">
	<c:if test="${!empty listDto.title }">
	<caption style="font-weight: bold; font-size: large;">${listDto.title}</caption>
	</c:if>
	<thead>
		<tr id="column-header">
			<c:if test="${listDto.totalable}"><th class=""></th></c:if>
			<c:forEach varStatus="s" items="${listDto.columnNames}">
			<th id="column-${s.index}">${s.current}</th>
			</c:forEach>
		</tr>
		<c:if test="${listDto.totalable}">
		<tr id="total-header">
			<td class="sum"><%=lang.__("Total<!--2-->")%></td>
			<c:forEach var="name" varStatus="s" items="${listDto.columnNames}">
			<td class="sum" headers="column-${s.index}">${listDto.sumItems[name]}</td>
			</c:forEach>
		</tr>
		</c:if>
	</thead>
	<tbody>
		<c:forEach varStatus="s" items="${listDto.columnValues}">
		<tr>
			<c:if test="${listDto.totalable}"><td class=""></td></c:if>
			<c:forEach varStatus="s2" items="${s.current}">
			<c:set var="st2" value="text-align:${listDto.typeItems[listDto.columnNames[s2.index]]=='number'?pageDto.number_align:pageDto.text_align}" />
			<td class="showtip" headers="column-${s2.index}" style="${st2}">${listDto.typeItems[name]}<c:if test="${s2.current!='null'}">${s2.current}</c:if></td>
			</c:forEach>
		</tr>
		</c:forEach>
	</tbody>
</table>
<script type="text/javascript">
$(function() {
    $("#${listDto.styleId}").tablesorter({
    	//widgets: ['zebra'],
		headers: {
//<c:forEach var="e" varStatus="s" items="${listDto.columnNames}">
//				${s.index}:{sorter:false},
//</c:forEach>
		},
		<c:if test="${!empty listDto.defsort}">
		sortList: [[${f:h(listDto.defsort)}]]
		</c:if>
    }).bind("sortEnd", function(sorter) {
		currentSort = sorter.target.config.sortList;
		SaigaiTask.PageURL.override({
			sort: currentSort.join(",")
		});
	});

    // テーブルの各セルに対して、パディング設定
	$("#${listDto.styleId} td, th").css("padding", "0 10px");
	// テーブルヘッダの背景色をグレーに変更
	$("#${listDto.styleId} thead tr").css("background-color", "#ddd");

	//ボタンのスタイル設定
	$(".dialog-button").button();

	// 削除リクエストがあれば進捗チェック
	var requestid = "${deleteLocalgovInfoForm.requestid}";
	var success = ${deleteLocalgovInfoForm.success};
	var fail = ${deleteLocalgovInfoForm.fail};
	if(!success && !fail && 0<requestid.length) {
		SaigaiTask.setupper.Deletelocalgov.startCheckProgress(requestid);
	}
});
</script>
<br/>
</c:forEach>
</div>

<form:form id="localgovinfo_delete_form" method="post" cssStyle="width: 500px;" servletRelativeAction="/admin/setupper/deleteLocalgovInfo/delete">
<input type="hidden" name="requestid" value="${deleteLocalgovInfoForm.requestid}"/>
<input type="hidden" name="localgovinfoid" value="${loginDataDto.localgovinfoid}" />
<input type="hidden" name="success" value="${deleteLocalgovInfoForm.success}"/>
<input type="hidden" name="fail" value="false"/><%-- false固定（もう一度削除実行時にfalseにならないように） --%>
</form:form>

<div id="content_footer" class="ui-layout-south">
<c:if test="${deleteLocalgovInfoForm.success==false}">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.submitForm('localgovinfo_delete_form', '<%=lang.__("Delete all data, such as its local government info or disaster info.")%><br/><%=lang.__("Restoration impossible after deletion. Continue?")%>');"><%=lang.__("Delete")%></a>
</c:if>
</div>

