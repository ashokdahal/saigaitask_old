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

	// event handler
	$("#disaster-select").on("change", function() {
		var select = $(this);
		var disasterid = select.val();
		var url = SaigaiTask.contextPath+"/admin/setupper/groupMenu/content?disasterid="+disasterid;
		SaigaiTask.setupper.loadContent(url);
	});
});
</script>

<c:set var="showTable" value="${0<fn:length(menuprocessNames)&&0<fn:length(menutaskNames)&&0<fn:length(menuNames)}"/>

<div id="Newgroup"><c:if test="${!usual}"><%=lang.__("New group")%></c:if><c:if test="${usual}"><%=lang.__("New unit")%></c:if></div>
<div id="Editgroupinfo"><c:if test="${!usual}"><%=lang.__("Edit group info")%></c:if><c:if test="${usual}"><%=lang.__("Edit unit info")%></c:if></div>
<div id="Groupname"><c:if test="${!usual}"><%=lang.__("<span>Group name:</span>")%></c:if><c:if test="${usual}"><span><%=lang.__("Unit name:")%></span></c:if></div>
<div id="content_head" class="ui-layout-north">
	<h1>
		<span><%=lang.__("Management login user and settings menu hierarchy")%></span>
		<c:if test="${!disasterCombined}">
		<c:if test="${0<fn:length(disasterItems)}">
		<span style="float: right"><span><%=lang.__("Disaster type")%>:</span>
		<c:choose>
		<c:when test="${fn:length(disasterItems)==1}">${disasterItems[0].name}</c:when>
		<c:when test="${0<fn:length(disasterItems)}">
			<select id="disaster-select" style="position: relative; top: -2px; font-size: 11px;">
			<c:forEach var="disaster" items="${disasterItems}">
			<c:set var="selected" value="${disaster.id==groupMenuForm.disasterid}"/>
			<option value="${disaster.id}" ${selected?'selected':''}>${f:h(disaster.name)}</option>
			</c:forEach>
			</select>
		</c:when>
		</c:choose>
		</span>
		</c:if>
		</c:if>
	</h1>
	<c:if test="${showTable}">
	<span><%=lang.__("Check and input display order in menu to use.")%></span>
	<span style="float: right;"><a href="#" <%--class="btn blue"--%> id="add-groupcol" onclick="return SaigaiTask.setupper.GroupMenu.addGroupColumn();"><c:if test="${!usual}"><%=lang.__("Add group")%></c:if><c:if test="${usual}"><%=lang.__("Add unit")%></c:if></a></span>
	</c:if>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left; padding: 0px;">
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<c:if test="${showTable}">
<form:form id="groupMenuForm" servletRelativeAction="/admin/setupper/groupMenu/update" modelAttribute="groupMenuForm">
	<%-- group の入力フォームは form 直下のここに定義する. thead だと、sticky-header で複製されてダブる. --%>
	<c:if test="${disasterCombined}">
		<input type="hidden" name="disasterid" value="${usual?0:1}"/>
	</c:if>
	<c:forEach var="e" varStatus="s" items="${groupInfoItems}">
	<%-- <input type="hidden" name="disasterid" value="${gropuMenuForm.disasterid}"/> --%>
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
			<th class="sorter-false filter-false link group-col" style="background-color: burlywood;">
				<span class="groupname" onclick="">${f:h(e.name)}</span>
			</th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="e" varStatus="s" items="${menuprocessNames}">
			<c:forEach var="f" varStatus="t" items="${menutaskNames[e]}">
				<c:forEach var="g" varStatus="u" items="${menuNames[menutasktypeNames[f]]}">
				<c:set var="firstSubtaskRow" value="${u.index==0}"/>
				<c:set var="firstTaskRow"   value="${t.index==0&&u.index==0}"/>
				<tr>
					<td class="task-col ${firstTaskRow?'first':''}">${f:h(e)}</td>
					<td class="subtask-col ${firstSubtaskRow?'first':''}">${f:h(f)}</td>
					<td class="menu-col" style="text-align: left;">${f:h(g.name)}</td>
					<c:forEach var="h" varStatus="v" items="${groupInfoItems}">
						<td class="group-col">
						<c:set var="menumap" value="${groupMenuMap[h.id]}" />
						<c:set var="checked" value="" />
						<c:set var="disporder" value="" />
						<c:if test="${menumap != null}">
							<c:if test="${menumap[g.id] != null}">
								<c:set var="checked" value="checked" />
								<c:set var="disporder" value="${menumap[g.id].disporder}" />
							</c:if>
						</c:if>
						<c:if test="${menumap == null}"></c:if>
						<input type="checkbox" name="menu"  value="c:${h.id}:${g.id}" ${checked} />
						<input type="text" size="1" name="d:${h.id}:${g.id}" value="${disporder}" />
						</td>
					</c:forEach>
				</tr>
				</c:forEach>
			</c:forEach>
		</c:forEach>
	</tbody>
</table>
</form:form>
</c:if>
</div>

<div id="content_footer" class="ui-layout-south">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.submitForm('groupMenuForm', '<%=lang._E("Are you sure to save?")%>');"><%=lang.__("Save")%></a>
</div>


