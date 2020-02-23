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
		SaigaiTask.training.loadContent(anchor.get(0).href+"/content");
		// cancel anchor href
		return false;
	});
});
</script>

<!-- 左手訓練プランリスト -->
<div class="leftList">
	<a href="${f:url('/admin/training/')}"><span id="leftListTitle"><%=lang.__("Training plan list")%></span></a><br />
	<ul id="leftPlanList">
		<c:choose>
			<c:when test="${fn:length(trainingPlanList) > 0}">
				<c:forEach var="e" varStatus="s" items="${trainingPlanList}">
					<li id="planid_${e.id}"> ${f:h(e.name)} </li>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<li id="noPlanLi"><%=lang.__("Training plan has not been created")%></li>
			</c:otherwise>
		</c:choose>
	</ul>
</div>

<div>
	<ul id="menulist" style="border:0px;">
	<!-- 気象庁防災情報ＸＭＬ編集 -->
		<li><a href="${f:url('/')}admin/training/xmlEditor"><%=lang.__("Disaster info xml editing")%></a></li>
	</ul>
</div>

