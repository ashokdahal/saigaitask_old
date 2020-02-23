<%@include file="../../common/lang_resource.jsp" %>
<link rel="stylesheet" type="text/css" href="../css/jquery.tablesorter/style.css" />


<table border="0" cellpadding="3" cellspacing="2" id="observTable" class="tablesorter" style="margin-left:10px;">
	<thead>
	<tr>
		<th></th>
<c:forEach var="e" varStatus="s" items="${observItems}">
		<c:if test="${e.typecol > 0 }">
		<th colspan="${e.typecol}">${f:h(e.typename)}</th>
		</c:if>
</c:forEach>
	</tr>
	<tr>
		<th></th>
<c:forEach var="e" varStatus="s" items="${observItems}">
		<c:if test="${e.namecol > 0 }">
		<th colspan="${e.namecol}">
		<c:if test="${e.observid==Observ.RAIN}">
			<a href="${f:url('/observ/rain/')}${e.id}" target="_blank" style="color:white;text-decoration:underline;">${f:h(e.name)}</a>
		</c:if>
		<c:if test="${e.observid==Observ.RIVER}">
			<a href="${f:url('/observ/river/')}${e.id}" target="_blank" style="color:white;text-decoration:underline;">${f:h(e.name)}</a>
		</c:if>
		<c:if test="${e.observid==Observ.DAM}">
			<a href="${f:url('/observ/dam/')}${e.id}" target="_blank" style="color:white;text-decoration:underline;">${f:h(e.name)}</a>
		</c:if>
		</th>
		</c:if>
</c:forEach>
	</tr>
	<tr>
		<th></th>
<c:forEach var="e" varStatus="s" items="${observItems}">
		<th>${f:h(e.itemname)}</th>
</c:forEach>
	</tr>
	</thead>
	<tbody id="realdata">
<c:forEach var="e" varStatus="s" items="${timeItems}">
	<c:if test="${s.index < omenuInfo.count}">
	<tr>
		<td><fmt:formatDate value="${e}" pattern="<%=lang.__(\"dd 'day'  HH:mm\")%>" /></td>
		<c:forEach var="f" varStatus="t" items="${datamapItems}">
		<td>${f:h(f[e].val)}
		<c:set var="ob" value="${observItems[t.index]}"/>
		<c:if test="${arrowmapItems[ob.no] != null}">
			&nbsp;${arrowmapItems[ob.no][e]}
		</c:if>
		</td>
		</c:forEach>
	</tr>
	</c:if>
</c:forEach>

	</tbody>
</table>

<script type="text/javascript">
//var telemin = ${Observ.MIN};
var telemin = 10;
var loadvalue = function(){
	var d = new Date();
	$('#realdata').load('${f:url('observe/telemeterdata/')}${omenuInfo.menuinfoid}/'+telemin+'?'+d.getTime());
};
$(document).ready(function() {
    setInterval(loadvalue, 120000);
});
loadvalue();

$("#tele-button").on('click' ,function(){
	var str = $("#tele-button").html();
	if (telemin == ${Observ.MIN}) {
		telemin = ${Observ.HOUR};
		str = str.replace("<%=lang.__("On the hour every hour")%>", "<%=lang.__("10 minutes each")%>");
	}
	else {
		telemin = ${Observ.MIN};
		str = str.replace("<%=lang.__("10 minutes each")%>", "<%=lang.__("On the hour every hour")%>");
	}
	$("#tele-button").html(str);
	loadvalue();
});
</script>
