<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<style type="text/css">
  td{
  	width:100%;
  }
  .result_number{
  	font-weight:bold;
  	color:red;
  }
</style>

<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Language resources import results")%></h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
	<c:if test="${loginDataDto.localgovinfoid==0}">
		<div style="width: 800px;">

			<h2><%=lang.__("Import completed")%></h2>
			<table border="1" class="form">
				<tr>
					<td>
					    <BR>
						<span class="result_number">${totalcount}</span><%=lang.__("Stored messages in the database.")%>
						<BR>
						<%=lang.__("Details :")%>
						<BR>
						&nbsp;<%=lang.__("･")%><span class="result_number">${updatecount}</span><%=lang.__("Messages updated .")%>
						<BR>
						&nbsp;<%=lang.__("･")%><span class="result_number">${insertcount}</span><%=lang.__("Messages created.")%>
						<BR>
						<BR>
					</td>
				</tr>
			</table>
		</div>
	</c:if>
</div>



