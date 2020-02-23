<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>
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
		$form.attr('action', '${f:url('/page/assemble/send')}');

        // 送信
        $.ajax({
            url: $form.attr('action'),
            type: $form.attr('method'),
            data: $form.serialize(),
            timeout: 300000,

            // 通信成功時の処理
            success: function(result, textStatus, xhr) {
            	if (result.result == "OK") {
	            	send = true;
	            	alert("<%=lang.__("Sent.")%>");
            	}
            	else if (result == "double")
            		alert("<%=lang.__("Double delivery! Reload page.")%>");
            	else if (result == "notitle")
            		alert("<%=lang.__("There is no title.")%>");
            	else if (result == "nocontent")
            		alert("<%=lang.__("There is no text body")%>");
            	else if (result == "errormail")
            		alert("<%=lang.__("E-mail address of the additional destination is invalid.")%>");
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
</script>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

<c:if test="${assembleForm.message != null }">
<span style="color:red;font-weight:bold;">${f:h(assembleForm.message) }</span><br>
</c:if>
<c:if test="${assembleForm.message == null || assembleForm.message == ''}">
<%=lang.__("Do Assembling staff. Send it after confirm the content and add if necessary.")%><br>
</c:if>
<br>
<form:form id="the_form" modelAttribute="assembleForm">
<% FormUtils.printToken(out, request); %>

<form:hidden path="trackdataid"/>
<form:hidden path="assembleinfoid"/>
<form:hidden path="noticegroupinfoid"/>

<table border="1" style="font-size:12px;border:1px; padding:4px;">
	<tr>
		<td rowspan="2" style="background-color:#F0F0F6; padding:4px;"><span style="font-weight:bold"><%=lang.__("Destination")%>：</span></td>
		<td rowspan="2" cssStyle="padding:4px;">${f:h(assembleForm.noticegroupinfoname) }<form:hidden path="noticegroupinfoname" /></td>
		<td cssStyle="padding:4px;"><form:checkbox path="enablePopup" /><%=lang.__("Make Pop-up the request alert also on the system")%></td>
	</tr>
	<tr>
		<td cssStyle="padding:4px;"><%=lang.__("Add delivery target")%>：<form:input path="additionalReceiver" readonly="false" cssStyle="resize: none; width:100%" /></td>
	</tr>
	<tr>
		<td style="background-color:#F0F0F6; padding:4px;"><span style="font-weight:bold"><%=lang.__("Title<!--3-->")%>：</span></td>
		<td colspan="2" cssStyle="padding:4px;"><form:input path="mailtitle" readonly="false" cssStyle="resize: none; width:100%" /></td>
	</tr>
	<tr>
		<td style="background-color:#F0F0F6; padding:4px;"><span style="font-weight:bold"><%=lang.__("Text body")%>：</span></td>
		<td colspan="2" style="padding:4px;">
			<!-- <input type="button" value="定型文の参照"><br>-->
			<form:textarea path="mailcontent" readonly="false" rows="8" cols="60" cssStyle="resize: none;"></form:textarea>
		</td>
	</tr>
</table>

</form:form>
