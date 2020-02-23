<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>

<%@include file="../../common/lang_resource.jsp" %>
<script type="text/javascript">
$(function() {
	$("#historyTable").tablesorter({
		theme : 'jui',
		widgets: ['uitheme','zebra'],
		headers: {
			0:{sorter:false},
			1:{sorter:false},
		}
	});	
	if (${fn:length(result)} == 1) {
		$("table#historyTable tr").addClass("odd");
		$("table#historyTable tr").addClass("ui-state-default");
	}
	var myButtons = {
			"<%=lang.__("Close")%>": function () {
				$('#history-dialog').dialog("close");
			}
	};
	$('#history-dialog').dialog('option', 'buttons', myButtons);
});
</script>
<div>

	<div data-role="content">

<form name="historyform">


<font color="red"><ul><form:errors path="*" element="li"/></ul></font>


<c:if test="${fn:length(result)==0}"><span><%=lang.__("No history")%></span></c:if>

<table border="0" cellpadding="3" cellspacing="2" id="historyTable" class="tablesorter" style="margin-left:10px;width:auto;${fn:length(result)==0?'display:none;':''}">
	<thead>
		<tr>
			<th><%=lang.__("Update date and time")%></th>
			<th><%=lang.__("Editor")%></th>
		<%-- テーブルヘッダ --%>
		<c:forEach var="e" varStatus="s" items="${attridItems}">
			<th>${f:h(e.name)}</th>
		</c:forEach>
		</tr>
	</thead>
	
	<tbody>
<c:forEach var="e" varStatus="s" items="${result}">
		<tr>
			<fmt:timeZone value="Etc/UTC">
			<fmt:formatDate value="${e['timeFrom']}" var="d" pattern="yyyy/MM/dd HH:mm"/></fmt:timeZone>
			<td>${d}</td>
			<td><c:catch>${f:h(e['stEdituser'])}</c:catch></td>
		<c:forEach var="f" varStatus="t" items="${attridItems}">
			<%-- データ変換 --%>
			<c:set var="colVal" value="${e[f.attrid]}"/>

			<td>${f:h(colVal)}</td>
		</c:forEach>
		</tr>
</c:forEach>
	</tbody>
</table>

</form>

	</div>
</div>
