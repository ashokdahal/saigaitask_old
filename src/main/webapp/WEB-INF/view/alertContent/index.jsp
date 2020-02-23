<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../common/lang_resource.jsp" %>
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" />
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript">
var page = 0;
var count = ${count};
$(function() {
	$("#pageprev").on('click' ,function(){
		if (page == 0) return;
		page = page-1;
		$('#alertdata').load('${f:url('alertContent/page/')}'+page);
		pagestate();
	});
	$("#pagenext").on('click' ,function(){
		if (count-1 == page) return;
		page = page+1;
		$('#alertdata').load('${f:url('alertContent/page/')}'+page);
		pagestate();
	});
	pagestate();
});

function pagestate() {
	if (page == 0) {
		$("#pageprev").addClass('ui-state-disabled');
		$("#pageprev").removeClass('ui-state-default');
	}
	else if ($("#pageprev").hasClass('ui-state-disabled')){
		$("#pageprev").removeClass('ui-state-disabled');
		$("#pageprev").addClass('ui-state-default');
	}
	if (count-1 == page) {
		$("#pagenext").addClass('ui-state-disabled');
		$("#pagenext").removeClass('ui-state-default');
	}
	else if ($("#pagenext").hasClass('ui-state-disabled')) {
		$("#pagenext").removeClass('ui-state-disabled');
		$("#pagenext").addClass('ui-state-default');
	}
}
</script>
<table border="0" cellpadding="3" cellspacing="2" id="alertTable" class="tablesorter" style="margin-left:10px;">
	<thead>
	<tr>
		<th class="header">No.</th>
		<th><%=lang.__("Type<!--2-->")%></th>
		<th><%=lang.__("Receive date and time")%></th>
		<th><%=lang.__("Title<!--2-->")%></th>
		<th><%=lang.__("Content")%></th>
	</tr>
	</thead>
	<tbody id="alertdata">
	<c:forEach var="e" varStatus="s" items="${alertcontentItems}">
	<tr>
		<td>${f:h(e.id)}</td>
		<td>${f:h(e.teloptypeid) }</td>
		<td>${f:h(e.receivetime) }</td>
		<td>${f:h(e.title) }</td>
		<td>${f:h(e.content)}</td>
	</tr>
	</c:forEach>
	</tbody>
	<tr>
		<td colspan="5" align="right">
			<div style="text-align:right;">
			<a id="pageprev" class="ui-button ui-widget ui-state-default ui-corner-all">
				<span class="ui-icon ui-icon-arrowthick-1-w" title="<%=lang.__("Return")%>"></span></a>
			&nbsp;
			<a id="pagenext" class="ui-button ui-widget ui-state-default ui-corner-all">
				<span class="ui-icon ui-icon-arrowthick-1-e" title="<%=lang.__("Next")%>"></span></a>
			</div>
		</td>
	</tr>
</table>

