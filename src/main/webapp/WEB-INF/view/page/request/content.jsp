<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<script type="text/javascript" src="${f:url('/js/msg.js')}"></script>
<style type="text/css">
<!--
	.listgrp { border:2px solid #ccc; width:300px; height: 100px; overflow-y: scroll; }
-->
</style>
<script type="text/javascript">
var send = false;
function sendMessage() {
	if (send) {
		alert('<%=lang.__("Please send it again after you move to another window once.")%>');
		return;
	}
	if (!confirm('<%=lang._E("Send displayed contents.\n Are you sure?")%>')) return;
	do{
        var $form = $('#the_form');
		$form.attr('action', '${f:url('/page/request/send')}');

        // 送信
        $.ajax({
            url: $form.attr('action'),
            type: $form.attr('method'),
            data: $form.serialize(),
            timeout: 30000,

            // 通信成功時の処理
            success: function(result, textStatus, xhr) {
            	if (result.result == "OK") {
	            	alert("<%=lang.__("Sent.")%>");
	            	send = true;
            	}
            	else if (result == "double")
            		alert("<%=lang.__("Double delivery! Reload page.")%>");
            	else if (result == "notitle")
            		alert("<%=lang.__("There is no title.")%>");
            	else if (result == "nocontent")
            		alert("<%=lang.__("There is no text body")%>");
            	else if (result == "errormail")
            		alert("<%=lang.__("E-mail address of the additional destination is invalid.")%>");
            	else if (result == "nosend")
            		alert("<%=lang.__("Please select the destination.")%>");
            	else if (result == "nopopup")
            		alert("<%=lang.__("Specify pop-up notification destination.")%>");
            	else
                	alert("<%=lang.__("An error occurred in sending.")%>");
            },

            // 通信失敗時の処理
            error: function(xhr, textStatus, error) {
            	alert("<%=lang.__("Unable to send.")%>");
            }
        });
	}while(0);
}

function setTemplateId(id) {
	$('#templatemsg').load(SaigaiTask.contextPath+"/template/content/"+id, null,
		function(responseText, status, XMLHttpRequest) {
			var pos = getCaretPosition($('#mailcontent').get(0));
			insertTextAtPosision($('#mailcontent').get(0), pos, responseText);
			setTemplateTitleId(id);
		});
}

function setTemplateTitleId(id) {
	$('#templatemsg').load(SaigaiTask.contextPath+"/template/title/"+id, null,
		function(responseText, status, XMLHttpRequest) {
			//var pos = getCaretPosition($('#mailcontent').get(0));
			//insertTextAtPosision($('#mailcontent').get(0), pos, responseText);
			$('#mailtitle').val(responseText);
		});
}
</script>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<%=lang.__("Send after additional write and check contents.")%><br>
<br>
<form:form id="the_form" modelAttribute="requestForm">
<form:hidden path="trackdataid"/>
<form:hidden path="menuid" />

<table border="1" style="font-size:12px;border:1px; padding:4px;">
	<tr>
		<td style="background-color:#F0F0F6; padding:4px;"><span style="font-weight:bold"><%=lang.__("Destination")%>：</span></td>
		<td style="padding:4px;">
			<div class="listgrp">
<c:forEach var="e" varStatus="s" items="${noticegroupInfoItems}">
				<input type="checkbox" name="noticegroupinfoid" value="${e.id}"/>${f:h(e.name)}<br />
</c:forEach>
			</div><br />
			<%=lang.__("Add delivery target")%>：<form:input path="additionalReceiver" readonly="false" cssStyle="resize: none; width:100%" />
		</td>
		<td style="padding:4px;">
			<div style="overflow:auto;width:80%;height:100px;border:1px black solid;margin:0px; background-color:white;">
				<c:forEach var="obj" items="${groupInfoItems}" >
					<label><input type="checkbox" name="checkedAlertList" value="${obj.id}" /><c:out value="${f:h(obj.name)}" /></label><br />
				</c:forEach>
			</div><br />
			<form:checkbox path="enablePopup" /><%=lang.__("Make Pop-up the request alert also on the system")%><br /><br />
		</td>
	</tr>
	<tr>
		<td style="background-color:#F0F0F6; padding:4px;"><span style="font-weight:bold"><%=lang.__("Title<!--3-->")%>：</span></td>
		<td colspan="2" style="padding:4px;"><form:input path="mailtitle" id="mailtitle" readonly="false" cssStyle="resize: none; width:100%" /></td>
	</tr>
	<tr>
		<td style="background-color:#F0F0F6; padding:4px;"><span style="font-weight:bold"><%=lang.__("Text body")%>：</span></td>
		<td colspan="2" style="padding:4px;">
			<form:textarea path="mailcontent" id="mailcontent" readonly="false" rows="8" cols="60" cssStyle="resize: none;"></form:textarea>
			<a class="thickbox ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" href="${f:url('/template/')}${f:h(pageDto.menuInfo.id)}/${NoticeType.MAIL}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&height=450&width=920"><span class="ui-button-text"><%=lang.__("Template")%></span></a>
		</td>
	</tr>
</table>
<div id="templatemsg" style="display:none"></div>
</form:form>
