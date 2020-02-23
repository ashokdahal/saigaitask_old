<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<script type="text/javascript">
$(function() {
	// メニューの初期化
	$("#menulist").menu();
	$("#menulist li a").on("click", function(e){
		var anchor = $(this);
		$("#menulist li a").css("background-Color", "transparent");
		anchor.css("background-Color", "yellow");
		SaigaiTask.setupper.loadContent(anchor.get(0).href+"content");
		// cancel anchor href
		return false;
	});
});

</script>
<div>
	<ul id="menulist" style="border:0px;">
	<c:if test="${loginDataDto.admin }">
		<li><a href="${f:url('/')}admin/setupper/initSetup/"><%=lang.__("Local gov. info")%></a></li>
		<li><a href="${f:url('/')}admin/setupper/localgovGroup/"><%=lang.__("自治体グループ")%></a></li>
		<li><a href="${f:url('/')}admin/setupper/groupMenu/"><%=lang.__("User/menu hierarchy")%></a></li>
		<li><a href="${f:url('/')}admin/setupper/timeDimension/"><%=lang.__("History Transformation")%></a></li>
		<li><a href="${f:url('/')}admin/setupper/disasterCombine/"><%=lang.__("Disaster type integration")%></a></li>
		<li><a href="${f:url('/')}admin/setupper/menuWizard/"><%=lang.__("Wizard")%></a></li>
		<li><a href="${f:url('/')}admin/setupper/importPopulation/"><%=lang.__("Population import")%></a></li>
	</c:if>
	<c:if test="${! loginDataDto.admin }">
		<li><a href="${f:url('/')}admin/setupper/menuWizard/"><%=lang.__("Wizard")%></a></li>
	</c:if>
	</ul>
</div>
