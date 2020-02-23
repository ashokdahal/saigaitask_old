<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<link href="${f:url('/css/jquery.tablesorter/style.css')}" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery-ui-timepicker-addon.css')}" />
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script src="${f:url('/js/jquery.edit-table.js')}" type="text/javascript"></script>
<script src="${f:url('/js/jquery-ui-timepicker-addon.js')}" type="text/javascript"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script src="${f:url('/js/jquery-ui-timepicker-ja.js')}" type="text/javascript"></script>
</c:if>
<script src="${f:url('/js/jquery.upload-1.0.2.min.js')}" type="text/javascript"></script>
<jsp:include page="/WEB-INF/view/page/map/head.jsp"/>
<script type="text/javascript">
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
});
</script>

<style type="text/css">
th.trackdataname{color:white; background-color:#4F81BD; text-align:center; font-weight: bold;}
th.sendtime{color:white; background-color:#4F81BD; text-align:center; font-weight: bold;}
th.title{color:white; background-color:#4F81BD; text-align:center; font-weight: bold;}
th.content{color:white; background-color:#4F81BD; text-align:center; font-weight: bold;}
th.mailto{color:white; background-color:#4F81BD; text-align:center; font-weight: bold;}
th.send{color:white; background-color:#4F81BD; text-align:center; font-weight: bold;}

td.trackdataname{padding:5px; width:70px;/*word-break:break-all;*/}
td.sendtime{padding:5px; width:10px;/*word-break:break-all;*/}
td.title{padding:5px; width:100px;/*word-break:break-all;*/}
td.content{padding:5px; width:650px; word-break:break-all;}
td.mailto{padding:5px; width:150px;/*word-break:break-all;*/}
td.send{padding:5px; width:50px;/*word-break:break-all;*/}

.pager{margin-top:20px;]}
.pager_fl{font-weight:bold; padding:5px; margin:1px; border:solid 1px #4F81BD; color:#4F81BD;}
.pager_fl:hover{font-weight:bold; padding:5px; margin:1px; border:solid 1px #4F81BD; background-color:#4F81BD ;color:white;}
.pager_link{font-weight:bold; padding:5px; margin:1px; border:solid 1px #4F81BD; color:#4F81BD;}
.pager_link:hover{font-weight:bold; padding:5px; margin:1px; border:solid 1px #4F81BD; background-color:#4F81BD ;color:white;}
.pager_current{font-weight:bold; padding:5px; margin:1px; border:solid 1px gray; background-color:gray; color:white;}
.pager_omission{font-weight:bold; padding:5px; margin:1px; color:gray;}
.pager
</style>

<form:form method="get" modelAttribute="mailhistoryForm">
	<form:select path="noticetypeid" onchange="submit()">
		<c:forEach var="f" varStatus="s" items="${noticetypeMasterItems}">
		<form:option value="${f:h(f.id)}">${f:h(f.name)}</form:option>
		</c:forEach>
	</form:select>
	<form:hidden path="menutaskid" value="${menutaskid}" />
	<form:hidden path="menuid" value="${menuid}" />
</form:form></br>

<table border="1" bordercolor="#CDCDCD">
<thead>
<tr>
<th class="trackdataname"><%=lang.__("Disaster name<!--2-->")%></th>
<th class="sendtime"><%=lang.__("Sent date and time")%></th>
<th class="title"><%=lang.__("Title<!--2-->")%></th>
<th class="content"><%=lang.__("Text")%></th>
<th class="mailto"><%=lang.__("Send to<!--2-->")%></th>
<th class="send"><%=lang.__("Sending result")%></th>
</tr>
</thead>
<tbody>
<% pageContext.setAttribute("newline", "\n"); %>
	<c:forEach var="e" varStatus="s" items="${noticemailDataItems}">
		<tr>
		<td class="trackdataname">${f:h(e.trackdataname)}</td>
	    <td class="sendtime">${f:h(e.sendtime)}</td>
	    <td class="title">${f:h(e.title)}</td>
	<%--     <td class="content">${f:h(e.content)}</td> --%>
	    <td class="content">${fn:replace(e.content, newline, "<br/>")}</td>
	    <td class="mailto">${f:h(e.mailto)}</td>
	    <td class="send">
		    <c:choose>
		    	<c:when test="${f:h(e.send) == 'true'}">
		    		<%=lang.__("Success")%>
		    	</c:when>
		    	<c:otherwise>
		    		<font color = red><%=lang.__("Failure")%></font>
		    	</c:otherwise>
		    </c:choose>
	    </td>
	 	</tr>
 	</c:forEach>
</tbody>
</table>

<div class="pager">
	<c:forEach var="l" varStatus="s" items="${links}">
		<c:choose>
			<c:when test="${l.type=='fst'}">
				<a class="pager_fl" title="<%=lang.__("First page")%>" href="${f:url('')}?menuid=${menuid}&menutaskid=${menutaskid}&pageno=1&noticetypeid=${noticetypeid}"><%=lang.__("First page")%></a>
			</c:when>
			<c:when test="${l.type=='current'}">
				<span class="pager_current"><b>${l.numPage}</b></span>
			</c:when>
			<c:when test="${l.type=='omission'}">
				<span class="pager_omission"><b>${l.numPage}</b></span>
			</c:when>
			<c:when test="${l.type=='link'}">
				<a class="pager_link" title="${l.numPage}<%=lang.__("Page")%>" href="${f:url('')}?menuid=${menuid}&menutaskid=${menutaskid}&pageno=${l.numPage}&noticetypeid=${noticetypeid}">${l.numPage}</a>
			</c:when>
			<c:when test="${l.type=='fnl'}">
				<a class="pager_fl" title="<%=lang.__("Last page")%>" href="${f:url('')}?menuid=${menuid}&menutaskid=${menutaskid}&pageno=${pageNum}&noticetypeid=${noticetypeid}"><%=lang.__("Last page")%></a>
			</c:when>
		</c:choose>
	</c:forEach>
</div>
