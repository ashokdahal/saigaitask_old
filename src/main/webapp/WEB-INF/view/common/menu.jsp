<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<div id="tasklist-container">
	<div style="text-align: center;">
		<input type="button" value="<%=lang.__("Task list")%>" id="tasklist-button" style="width: 90%; margin: 3px 0px;" class="ui-orange-button"/>
	</div>
	<div id="tasklist-dialog" style="display:none;">
	<c:forEach var="menuprocessInfo" items="${pageDto.menuprocessInfos}">
		<c:if test="${menuprocessInfo.visible}">
		<h3><c:if test="${menuprocessInfo.important }"><font color='orange'>★</font></c:if>${f:h(menuprocessInfo.name)}</h3>
		<div>
			<ul class="menu" style="border:0px;">
			<c:forEach var="menutaskInfo" items="${menuprocessInfo.menutaskInfos}">
				<c:if test="${menutaskInfo.visible}">
				<li style="<c:if test="${menutaskInfo.id==menutaskid}">background-color:yellow;</c:if>border: 1px solid gray;margin: 3px; border-radius: 5px;">
					<a href="${f:url('/')}page/?menutaskid=${f:h(menutaskInfo.id)}&fullscreen=${f:h(pageDto.fullscreen)}">
						<%-- 現在のタスクがわかるようにする？ --%>
						<c:if test="${menutaskInfo.important }"><font color='orange'>★</font></c:if>${f:h(menutaskInfo.name)}
					</a>
				</li>
				</c:if>
			</c:forEach>
			</ul>
		</div>
		</c:if>
	</c:forEach>
	</div>
</div>
<div>
	<div>
		<span><%=lang.__("･Menu")%></span>
	</div>
	<ul id="menulist" style="border:0px;">
	<c:forEach var="tmenuInfo" items="${pageDto.menutaskInfo.menutaskmenuInfos}">
		<c:if test="${tmenuInfo.menuInfo.visible}">
		<li style="<c:if test="${tmenuInfo.menuInfo.id==pageDto.menuInfo.id}">background-color:yellow;</c:if>border: 1px solid gray;margin: 3px; border-radius: 5px;">
			<a id="menubox" onclick="menuClick(${f:h(pageDto.menutaskInfo.id)}, ${f:h(tmenuInfo.menuInfo.id)},${f:h(pageDto.fullscreen)})" >
				<%-- 現在のメニューがわかるようにする？ --%>
				<c:if test="${tmenuInfo.important }"><font color='orange'>★</font></c:if>${f:h(tmenuInfo.menuInfo.name)}
			</a>
		</li>
		</c:if>
	</c:forEach>
	</ul>
</div>

<c:if test="${empty pageDto.localgovInfo.logoimagefile}">
	<c:set var="logoimagefileurl" value="/images/common/ban_nied.png" />
</c:if>
<c:if test="${! empty pageDto.localgovInfo.logoimagefile}">
	<c:set var="logoimagefileurl" value="${pageDto.localgovInfo.logoimagefile}" />
</c:if>
<img src="${f:url(logoimagefileurl)}" style="position: fixed; bottom: 3px; width: 150px;"/>

<script type="text/javascript">
//$(function() {
	// タスクリストの初期化
	var tasklistDialog = $("#tasklist-dialog");
	tasklistDialog.hide();
	$(".menu", tasklistDialog).menu();

	// タスクリストボタンの初期化
	$("#tasklist-button").button()
	.click(function(){
		tasklistDialog.dialog({
			title: "<%=lang.__("Task list")%>",
			maxHeight: 500
		});
	});

	// メニューの初期化
	$("#menulist").menu();
//});

function menuClick(menutaskinfoid, menuinfoid, fullscreen) {
	location.href="${f:url('/page/')}?menutaskid="+menutaskinfoid+"&menuid="+menuinfoid+"&fullscreen="+fullscreen;
}

</script>
