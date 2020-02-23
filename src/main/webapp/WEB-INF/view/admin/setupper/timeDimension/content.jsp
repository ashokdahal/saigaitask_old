<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>
<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("History Transformation")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
	<div>
		<span style="margin-left: 15px;"><%=lang.__("Run all maps and layers history transformation.")%></span><br/>
		<br/>
		<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
	    <c:forEach var="msg" items="${messages}">
			<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
		</c:forEach>
		<c:remove var="messages" scope="session"/>
	</div>

	<br/>

	<form:form  id="upgrade_form" servletRelativeAction="/admin/setupper/timeDimension/upgrade">
		<% FormUtils.printToken(out, request); %>
		<%--
		<span>時系列マップを試したい場合は<a href="#" onclick="SaigaiTask.setupper.loadContent('${f:url('/admin/setupper/timeDimension/convertForm')}');">こちら</a>にアクセスしてください。</span><br/>
		 --%>

		<!-- マスターマップレイヤ一覧 -->
		<c:set var="listDto" value="${mapListDto}"></c:set>
		<%@ include file="inc_list_dto.jsp" %>

		<!-- 統合する災害マップを選択 -->
		<br/>
		<div style="margin-left: 10px; display:none;" id="merge-description">
			<span><%=lang.__("Disaster map may be integrated into master map. Check disasters to be integrated.")%></span><br/>
			<span><%=lang.__("* Disaster data will be inserted as history data of master map after integration process. And integrated disaster map layer will be deleted.")%></span><br/>
			<span><%=lang.__("Transfer disaster maps, not being run integration process, into maps with history.")%></span><br/>
		</div>
		<script type="text/javascript">
			var enableMerge = 0<$("[name='trackmapinfoIds']").length;
			if(enableMerge) {
				$("#merge-description").css("display", "block");
			}
			else {
				// 統合カラムはマージの時のみ表示
				$("#ecommap_map_list th#column-3").css("display", "none");
				$("#ecommap_map_list td[headers='column-3']").css("display", "none");
			}
		</script>

		<br/>

		<!-- マスターマップレイヤ一覧 -->
		<c:set var="listDto" value="${historyLayerListDto}"></c:set>
		<%@ include file="inc_list_dto.jsp" %>
	</form:form>



</div>

<div id="content_footer" class="ui-layout-south">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.submitForm('upgrade_form', '<%=lang._E("Run history transformation.<br/>Proceed?")%>');"><%=lang.__("History Transformation")%></a>
</div>

