<%/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<script type="text/javascript">
$(function(){
	// レイアウト初期化
	Ext.onReady(function(){
		SaigaiTask.Layout.content=$("#contents_setup");
		SaigaiTask.Layout.initContentLayout();
	});

	// テーブル初期化
	SaigaiTask.setupper.GroupMenu.initTable();
	$("#add-groupcol").button()
	.children(".ui-button-text").css("font-size", "8px");
});
</script>

<c:set var="showTable" value="${0<fn:length(disasterprocessNames)&&0<fn:length(disastertaskNames)&&0<fn:length(disastermenuNames)}"/>

<div id="content_head" class="ui-layout-north">
	<h1>
		<span><%=lang.__("Management login user and settings menu hierarchy")%></span>
	</h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left; padding: 0px;">
<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>
<c:if test="${showTable}">
<form:form id="disasterCombineForm" servletRelativeAction="/admin/setupper/disasterCombine/update">
	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
	<%-- group の入力フォームは form 直下のここに定義する. thead だと、sticky-header で複製されてダブる. --%>
	<c:forEach var="e" varStatus="s" items="${groupInfoItems}">
	<input type="hidden" name="groupid" value="${f:h(e.id)}"/>
	<input type="hidden" name="groupname" value="${f:h(e.name)}"/>
	</c:forEach>
<table id="groupMenu-table" style="margin-top:0px; margin-left:0px;">
	<thead>
		<tr>
			<th class="sorter-false task-col"><%=lang.__("Task")%></th>
			<th class="sorter-false subtask-col"><%=lang.__("Sub task")%></th>
			<th class="sorter-false menu-col"><%=lang.__("Menu<!--2-->")%></th>
			<c:forEach var="e" varStatus="s" items="${groupInfoItems}">
			<th class="sorter-false filter-false group-col" style="background-color: burlywood;">
				<span>${f:h(e.name)}</span>
			</th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="a" varStatus="b" items="${disasterItems}">
			<c:forEach var="e" varStatus="s" items="${disasterprocessNames[a.id]}">
				<c:forEach var="f" varStatus="t" items="${disastertaskNames[a.id][e]}">
					<c:forEach var="g" varStatus="u" items="${disastermenuNames[a.id][disastertasktypeNames[a.id][f]]}">
					<c:set var="firstSubtaskRow" value="${u.index==0}"/>
					<c:set var="firstTaskRow"   value="${t.index==0&&u.index==0}"/>
					<tr>
						<td class="task-col ${firstTaskRow?'first':''}">${f:h(e)}
						</td>
						<td class="subtask-col ${firstSubtaskRow?'first':''}">${f:h(f)}</td>
						<td class="menu-col" style="text-align: left;">${f:h(g.name)}</td>
						<c:forEach var="h" varStatus="v" items="${groupInfoItems}">
							<td class="group-col">
							<c:set var="menumap" value="${disasterCombineForm.groupMenuMap[h.id]}" />
							<c:set var="checked" value="" />
							<c:set var="disporder" value="" />
							<c:if test="${menumap != null}">
								<c:if test="${menumap[g.id] != null}">
									<c:set var="checked" value="checked" />
									<c:set var="disporder" value="${menumap[g.id].disporder}" />
								</c:if>
							</c:if>
							<c:if test="${menumap == null}"></c:if>
							${disporder}
							</td>
						</c:forEach>
					</tr>
					</c:forEach>
				</c:forEach>
			</c:forEach>
		</c:forEach>
	</tbody>
</table>
</form:form>
</c:if>
</div>

<div id="content_footer" class="ui-layout-south">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.submitForm('disasterCombineForm', '<%=lang._E("Are you sure to save?")%>');"><%=lang.__("Save")%></a>

</div>


