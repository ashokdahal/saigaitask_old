<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<%@include file="../common/adminjs-header.jsp" %>
<script type="text/javascript">
<c:forEach var="f" varStatus="t" items="${tablelist}">
$(document).ready(function(){
	jQuery("#${f}list").jqGrid({
   		url:'${f}',
		datatype: "json",
		colNames:[
<c:forEach var="e" varStatus="s" items="${fieldmap[f]}">'${f:h(e)}',</c:forEach>
		          ],
		colModel:[
<c:forEach var="e" varStatus="s" items="${fieldmap[f]}">{name:'${f:h(e)}',index:'${f:h(e)}',width:55},</c:forEach>
		     	],
   		rowNum:10,
   		rowList:[10,20,30],
		height: 'auto',
   		pager: '#${f}pager',
   		sortname: 'id',
    	viewrecords: true,
    	sortorder: "desc",
    	caption:"${f}",
		editurl:'${f:url("jqgridedit/")}${f}',
	});
	jQuery("#${f}list").jqGrid('navGrid','#${f}pager',{edit:false,add:false,del:true});
});
</c:forEach>
</script>
</head>
<body>
<c:forEach var="f" varStatus="t" items="${tablelist}">
<table id="${f}list"></table>
<div id="${f}pager"></div>
</c:forEach>
</body>
</html>