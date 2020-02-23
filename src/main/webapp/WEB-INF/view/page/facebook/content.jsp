<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<script type="text/javascript" src="${f:url('/js/msg.js')}"></script>
<link href="${f:url('/css/popup.css')}"  rel="stylesheet" type="text/css" />
<link href="${f:url('/css/jquery.tablesorter/style.css')}"  rel="stylesheet" type="text/css" />
<style type="text/css">
<!--
	.listgrp { border:2px solid #ccc; width:300px; height: 150px; overflow-y: scroll; }
-->
</style>
<script type="text/javascript">
window.onload = function(){
	<%-- 訓練モードによる本文追加 --%>
	<c:if test="${ pageDto.trackData.trainingplandataid > 0 }">
		<c:if test="${ pageDto.trackData.trainingplanData.facebookflag }">
	var facebookContent = $("#facebookContent").val();
	$("#facebookContent").val("<%=lang.__("[Training]")%>" + "\n" + facebookContent);
		</c:if>
	</c:if>

	viewLength();
}
function viewLength() {
//	タイトル削除
// 	var title = document.getElementsByName("facebookTitle")[0].value.length;
	var content = document.getElementsByName("facebookContent")[0].value.length;
//	document.getElementById("lengthnow").innerHTML = title + content + 1 + "文字";
	document.getElementById("lengthnow").innerHTML = content + "<%=lang.__("Character")%>";
	if (content > 5000) {
		document.getElementById("lengthnow").style.color = "red";
	} else {
		document.getElementById("lengthnow").style.color = "black";
	}
}

function setTemplateId(id) {
	$('#templatemsg').load(SaigaiTask.contextPath+"/template/title/"+id, null,
		function(responseText, status, XMLHttpRequest) {
			$('#emailTitle').val(responseText);
			$('#templatemsg').load(SaigaiTask.contextPath+"/template/content/"+id, null,
				function(responseText, status, XMLHttpRequest) {
					var pos = getCaretPosition($('#facebookContent').get(0));
					insertTextAtPosision($('#facebookContent').get(0), pos, responseText);
					//setTemplateTitle(id);
				});
			viewLength();
		});
}

function sendMessage() {
	<%-- 訓練モードによる制御チェック --%>
	<c:if test="${ pageDto.trackData.trainingplandataid > 0 }">
		<c:if test="${ !pageDto.trackData.trainingplanData.facebookflag }">
		alert("<%=lang.__("For undergoing training, we limit Facebook send function ")%>");
		return;
		</c:if>
	</c:if>

	if (!confirm('<%=lang._E("Send displayed contents.\n Are you sure?")%>')) {
		return;
	}
	// old
	document.forms[0].action = "${f:url('/page/facebook/send')}";
	document.forms[0].submit();

// 		do {
// 			var $form = $('#the_form');
// 			$form.attr('action', '${f:url('send')}');

// 	        // 送信
// 	        $.ajax({
// 	            url: $form.attr('action'),
// 	            type: $form.attr('method'),
// 	            data: $form.serialize(),
// 	            timeout: 30000,

// 	            // 通信成功時の処理
// 	            success: function(result, textStatus, xhr) {
// 	            	if (result.result == "OK")
// 		            	alert("送信しました。");
// 	            	else
// 	                	alert("送信時にエラーが発生しました。");
// 	            },

// 	            // 通信失敗時の処理
// 	            error: function(xhr, textStatus, error) {
// 	            	alert("送信できませんでした。");
// 	            }
// 	        });
// 		} while(0);
}
</script>

<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>
<form:form id="the_form" modelAttribute="facebookForm">
	<% FormUtils.printToken(out, request); %>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>

	<%=lang.__("Send confirmation e-mail content describing below and status report to SNS(Facebook) ")%><br /><br />
	<span class="page_title"><%=lang.__("･Text for Facebook")%></span>
	<table class="table_email">
		<tr>
			<td valign="top"><%=lang.__("Post destination")%>：</td>
			<td width="200px">
			<div class="listgrp" style="width:500px;  height:80px">
				<c:choose>
					<c:when test="${facebookForm.facebookHomeinfo != null}">
						<input type="checkbox" name="facebookHomeid" value="${f:h(facebookForm.facebookHomeinfo)}"/><%=lang.__("[Home]")%>${f:h(facebookForm.facebookUsername.name)}<br />
					</c:when>
				</c:choose>

				<c:forEach var="e" varStatus="s" items="${facebookaccountInfo}">
					<input type="checkbox" name="facebookaccountinfo" value="${f:h(e.id)}"/><%=lang.__("[Facebook page]")%>${f:h(e.name)}<br />
				</c:forEach>

				<c:forEach var="e" varStatus="s" items="${facebookgroupInfo}">
					<input type="checkbox" name="facebookgroupinfo" value="${f:h(e.id)}"/><%=lang.__("[Group]")%>${f:h(e.name)}<br />
				</c:forEach>

			</div>
			</td>
		</tr>
<!-- タイトル削除 -->
<!-- 		<tr> -->
<!-- 			<td width="70px">タイトル：</td> -->
<%-- 			<td width="200px"><form:input path="facebookTitle" id="facebookTitle" readonly="false" cssStyle="resize: none; width:100%" onblur="viewLength();" onkeyup="viewLength();" /></td> --%>
<%-- 			<td width="200px"><a class="thickbox " href="${f:url('/template/')}${pageDto.menuInfo.id}/?TB_iframe=true&width=920&height=450"><input type="button" value="定型文の参照" class="ui-button ui-widget ui-state-default ui-corner-all" /></a></td> --%>
<!-- 		</tr> -->
		<tr>
			<td width="70px"><%=lang.__("Template")%>：</td>
			<td width="200px"><a class="thickbox " href="${f:url('/template/')}${f:h(pageDto.menuInfo.id)}/${noticeType}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&width=920&height=450"><input type="button" value="<%=lang.__("Reference to template")%>" class="ui-button ui-widget ui-state-default ui-corner-all" /></a></td>
		</tr>
		<tr>
			<td valign="top"><%=lang.__("Text")%>：</td>
			<td colspan="2">
				<form:textarea path="facebookContent" id="facebookContent" readonly="false" rows="8" cols="60" cssStyle="resize: none;" onblur="viewLength();" onkeyup="viewLength();" ></form:textarea>
				<div><span id="lengthnow"><%=lang.__("One character")%></span><span><%=lang.__("/5000 characters")%></span></div>
			</td>
		</tr>
	</table>
	<br />
	<span class="page_title"><%=lang.__("･Confirmation e-mail")%></span>
	<table class="table_email">
		<tr>
			<td width="70px" rowspan="2" valign="top"><%=lang.__("Title<!--2-->")%>：</td>
			<td width="200px" colspan="2"><form:input path="emailTitle" id="emailTitle" readonly="false" cssStyle="resize: none; width:100%" /></td>
		</tr>
		<tr>
			<td width="200px">
			<div class="listgrp">
				<c:forEach var="e" varStatus="s" items="${noticegroupInfoItems}">
					<input type="checkbox" name="noticegroupinfoid" value="${f:h(e.id)}"/>${f:h(e.name)}<br />
				</c:forEach>
			</div>
			</td>
			<td valign="bottom">
				<%=lang.__("* E-mail body (text) equals to the above.")%><br /><br />
				<%=lang.__("Add delivery target")%>：<form:input path="additionalReceiver" readonly="false" cssStyle="resize: none; width:100%" />
			</td>
		</tr>
	</table>
	<form:hidden path="menuid" />
	<form:hidden path="menutaskid" />
</form:form>
<div id="templatemsg" style="display:none"></div>
