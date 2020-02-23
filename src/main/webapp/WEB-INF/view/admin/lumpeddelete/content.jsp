<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>
<script type="text/javascript">
function submitForm(formId, confirmMsg) {
	var form = document.getElementById(formId);

	if(confirmMsg === ''){
		$("body").mask("<%=lang.__("Now getting..")%>");
		form.submit();
	}else{
		Ext.MessageBox.confirm("<%=lang.__("Bulk deletion confirmation")%>", confirmMsg, function(btn) {
			if (btn == "yes") {
				$("body").mask("<%=lang.__("Now deleting..")%>");
				form.submit();
			} else if (btn == "no") {
				// do nothing
			}
		}, window);
	}
	return false;
}

function doLumpedeleteExec() {
	return submitForm('lumpeddelete_submit','<%=lang._E("Are you sure to run the bulk deletion?")%>');
}

function changeLocolgov() {
	return submitForm('lumpeddelete_selectLocalgov','');
}
function checkAllDleteTableListIdsExec(){
	if($("#checkAllDleteTableListIds:checked").val() === "1"){
		checkAll("deleteTableListIds",true);
	}else{
		checkAll("deleteTableListIds",false);
	}
}
function checkAllDeleteMapLayerAttrIdsExec(){
	if($("#checkAllDeleteMapLayerAttrIds:checked").val() === "1"){
		checkAll("deleteMapLayerAttrIds",true);
	}else{
		checkAll("deleteMapLayerAttrIds",false);
	}
}
function checkAll(targetName,mode){
	var target = $("input[name='" + targetName +  "']");
	target.each(function(){
		$(this).prop("checked",mode);
	});

}
$(function(){
	$("#checkAllDleteTableListIds").prop("checked",false);
	$("#checkAllDeleteMapLayerAttrIds").prop("checked",false);
	checkAll("deleteTableListIds",false);
	checkAll("deleteMapLayerAttrIds",false);
});
</script>


<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Bulk deletion")%></h1>
</div>


<div id="content_main" class="ui-layout-center" style="text-align: left;">
<c:if test="${loginDataDto.localgovinfoid==0}">
	<form:form id="lumpeddelete_selectLocalgov" method="post" enctype="multipart/form-data" servletRelativeAction="/admin/lumpeddelete/changeLocalgov" modelAttribute="lumpeddeleteForm">
	<% FormUtils.printToken(out, request); %>
		<h2 style="margin: 0px;"><%=lang.__("Specify local gov. ID")%></h2>
			<table border="1" class="form">
			<tr>
			<th width="150"><%=lang.__("Select local gov.")%></th>
			<td width="650">
				<form:select path="selectLocalgov"  id="lumpeddeleteSelectLocalgov"  onchange="return changeLocolgov();">
	    			<c:forEach var="localgovSelectOption" items="${localgovSelectOptions}">
	        			<form:option value="${localgovSelectOption.key}">${localgovSelectOption.key}: ${f:h(localgovSelectOption.value)}</form:option>
	    			</c:forEach>
				</form:select>
			</td>
			</tr>
			</table>
	</form:form>
</c:if>
<form:form id="lumpeddelete_submit" method="post" enctype="multipart/form-data" servletRelativeAction="/admin/lumpeddelete/doLumpeddelete" modelAttribute="lumpeddeleteForm">
	<% FormUtils.printToken(out, request); %>

    <c:forEach var="msg" items="${messages}">
		<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
	</c:forEach>
	<c:remove var="messages" scope="session"/>
	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<c:forEach var="localgovInfoItem" varStatus="s" items="${lumpeddeleteDto.localgovInfos}">
		<form:hidden path="currentLocalgovinfoid" value= "${lumpeddeleteDto.currentLocalgovinfoid}" />
		<c:if test="${lumpeddeleteDto.viewLocalgovInfoLabel==true}">
			<h2>${localgovInfoItem.value}</h2>
		</c:if>

		<c:if test="${lumpeddeleteDto.viewDataCount <= 0}">
			<h3><%=lang.__("Menu info, the target of bulk deletion, does not exist.")%></h3>
		</c:if>

		<c:if test="${lumpeddeleteDto.viewDataCount > 0}">
		<table border="1" class="form">
			<tr>
				<th><%=lang.__("Disaster type")%><BR><%=lang.__("Group")%></th>
				<th><%=lang.__("Menu process")%></th>
				<th><%=lang.__("Menu task")%></th>
				<th><%=lang.__("Menu<!--2-->")%></th>
				<th style="text-align: center;">
					<%=lang.__("Table list item info")%>
					<BR>
					<%=lang.__("Select all<!--2-->")%>
					<input type="checkbox" name="checkAllDleteTableListIds" id = "checkAllDleteTableListIds" value="1"  onchange="checkAllDleteTableListIdsExec();">
				</th>
				<th style="text-align: center;">
					<%=lang.__("Map layer attribute info")%>
					<BR>
					<%=lang.__("Select all<!--2-->")%>
					<input type="checkbox" name="checkAllDeleteMapLayerAttrIds"  id = "checkAllDeleteMapLayerAttrIds" value="1" onchange="checkAllDeleteMapLayerAttrIdsExec();">
				</th>
			</tr>
			<c:forEach var="menuloginInfoItem" varStatus="s1" items="${lumpeddeleteDto.menuloginInfos[localgovInfoItem.key]}">
			<c:forEach var="menuprocessInfoItem" varStatus="s2" items="${lumpeddeleteDto.menuprocessInfos[menuloginInfoItem.key]}">
			<c:forEach var="menutaskInfoItem" varStatus="s3" items="${lumpeddeleteDto.menutaskInfos[menuprocessInfoItem.key]}">
			<c:forEach var="menutaskmenuInfoItem" varStatus="s4" items="${lumpeddeleteDto.menutaskmenuInfos[menutaskInfoItem.key]}">
				<tr>
					<c:if test="${s2.first == true}">
					<c:if test="${s3.first == true}">
					<c:if test="${s4.first == true}">
						<td rowspan="${lumpeddeleteDto.localgovInfoMenuloginInfosMenuCount[localgovInfoItem.key][menuloginInfoItem.key]}">
							${menuloginInfoItem.value}
						</td>
					</c:if>
					</c:if>
					</c:if>
					<c:if test="${s3.first == true}">
					<c:if test="${s4.first == true}">
						<td rowspan="${lumpeddeleteDto.localgovInfoMenuprocessInfosMenuCount[localgovInfoItem.key][menuprocessInfoItem.key]}">
							${menuprocessInfoItem.value}
						</td>
					</c:if>
					</c:if>
					<c:if test="${s4.first == true}">
						<td rowspan="${lumpeddeleteDto.localgovInfoMenutaskInfosMenuCount[localgovInfoItem.key][menutaskInfoItem.key]}">
							${menutaskInfoItem.value}
						</td>
					</c:if>
					<td>
							${menutaskmenuInfoItem.value}
					</td>
					<td style="text-align: center;">
						<form:checkbox id="deleteTableListIds-${menutaskmenuInfoItem.key}" path="deleteTableListIds" value="${menutaskmenuInfoItem.key}" />
					</td>
					<td style="text-align: center;">
						<form:checkbox id="deleteMapLayerAttrIds-${menutaskmenuInfoItem.key}" path="deleteMapLayerAttrIds" value="${menutaskmenuInfoItem.key}" />
					</td>
				</tr>
			</c:forEach>
			</c:forEach>
			</c:forEach>
			</c:forEach>
		</table>
		</c:if>
	</c:forEach>
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
	<div style="float:left;">
		<a href="#" class="btn blue" onclick="doLumpedeleteExec();"><%=lang.__("Run the bulk deletion")%></a>
	</div>
</div>


