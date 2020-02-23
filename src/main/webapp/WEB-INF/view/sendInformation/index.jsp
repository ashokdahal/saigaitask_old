<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/lang_resource.jsp" %>

<html>
<head>
	<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/jquery-ui.css')}" />
	<link href="${f:url('/css/popup.css')}"  rel="stylesheet" type="text/css" />
	<link href="${f:url('/css/template.css')}"  rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${f:url('/js/msg.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/jquery.MultiFile.js')}" language="javascript"></script>

<script type="text/javascript">

	$(document).ready(function(){
		$("#seltemplate").click(function() {
			var tid = $("input[name='templateid']:checked").val();
			//alert(tid);
			setTemplateId(tid);
			//self.parent.tb_remove();

			showTemplate(false);
		});
	});

	function showTemplate(b) {
		if (b) {
			$('#content').hide();
			$('#template').show();
		}
		else {
			$('#content').show();
			$('#template').hide();
		}
	}

	function setTemplateId(id) {
		if (!id) return;
		$('#templatemsg').load(SaigaiTask.contextPath+"/template/content/"+id, null,
			function(responseText, status, XMLHttpRequest) {
				var pos = getCaretPosition($('#sendInformationContent').get(0));
				insertTextAtPosision($('#sendInformationContent').get(0), pos, responseText);
				setTemplateTitleId(id);
			});
	}

	function setTemplateTitleId(id) {
		$('#templatemsg').load(SaigaiTask.contextPath+"/template/title/"+id, null,
			function(responseText, status, XMLHttpRequest) {
				//var pos = getCaretPosition($('#mailcontent').get(0));
				//insertTextAtPosision($('#mailcontent').get(0), pos, responseText);
				$('#emailTitle').val(responseText);
			});
	}

	function sendMessage() {
		var title = document.getElementsByName("emailTitle")[0].value;
		var content = document.getElementsByName("sendInformationContent")[0].value;
		var to = document.getElementsByName("noticegroupinfoid");
		var toText = document.getElementsByName("checkedText");
		var mail = '';
		var upfiles = '';

		for (var i=0; to.length>i; i++){
			//if (to[i].checked) mail += toText[i].innerHTML + '\n';// + ' <' + to[i].value + '>\n';
			if (to[i].checked) {
				if (toText[i]) mail += toText[i].innerHTML + '\n';
			}
		}

		if (document.getElementsByName("additionalReceiver")[0].value != "")
			mail += '<%=lang.__("Add delivery target")%> <' + document.getElementsByName("additionalReceiver")[0].value + ">\n";
		/*//Firefox does't work!
		var len = document.all.length;
		for(var i=0; i<len; i++) {
			if(document.all(i).className.indexOf("MultiFile-title") > -1) {
				upfiles += document.all(i).innerHTML + '\n';
			}
		}*/
		$('.MultiFile-title').each(function(i, elem) {
			upfiles += elem.innerHTML + '\n';
		});
		// 1000文字程度あるとダイアログのOKがクリックできない
		if (mail.length < 1000 && title.length < 1000 && content.length < 1000) {
			if (!confirm('<%=lang._E("Following content will be sent. Are you sure?")%>' + '<%=lang.__("･Send to")%>' + '\n' + mail + '\n' + '<%=lang.__("･Title")%>' + '\n' + title + '\n\n' + '<%=lang.__("･Body text")%>' + '\n' + content + '\n\n' + '<%=lang.__("･Attachment")%>' + '\n' + upfiles)) {
				return;
			}
		}
		do {
			document.forms[0].action = "${f:url('/sendInformation/send')}";
			document.forms[0].submit();
		} while(0);
	}
</script>
</head>
<body style="background-color:#DCDCDC;" id="sendInformationbody">
	<div id="headerArea"><strong><%=lang.__("Send info")%></strong></div>
	<br /><br />
	<form:form id="the_form" enctype="multipart/form-data" modelAttribute="sendInformationForm">
	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
	<% FormUtils.printToken(out, request); %>

	<form:hidden path="menuinfoid"/>
	<div id="content">
		<a class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="return confirm('<%=lang.__("Do you want to end the disaster response?")%>');" href="#" role="button" aria-disabled="false"></a>
		<table class="info_table" rules="cols">
			<tr>
				<td width="45%"><strong><%=lang.__("･Choose alert destination")%></strong></td>
				<td width="55%" style="padding-left: 10px;"><strong><%=lang.__("･Choose e-mail delivery destination")%></strong></td>
			</tr>
			<tr>
				<td><%=lang.__("Following user is preset.")%><br /><%=lang.__("Re-select as required.")%></td>
				<td style="padding-left: 10px;"><%=lang.__("Following e-mail address is preset.")%><br /><%=lang.__("Re-select as required.")%></td>
			</tr>
			<tr>
				<td>
					<div style="overflow:auto;width:80%;height:100px;border:1px black solid;margin:0px; background-color:white;">
						<c:forEach var="obj" items="${groupInfoItems}" >
							<label><form:checkbox path="checkedAlertList" value="${obj.id}" /><c:out value="${f:h(obj.name)}" /></label><br />
						</c:forEach>
					</div>
				</td>
				<td style="padding-left: 10px;">
					<div style="overflow:auto;width:90%;height:100px;border:1px black solid;margin:0px;  background-color:white;" >
						<c:forEach var="e" varStatus="s" items="${noticegroupInfoItems}">
							<label><form:checkbox path="noticegroupinfoid" value="${e.id}"/><span name="checkedText">${f:h(e.name)}</span></label><br />
						</c:forEach>
					</div>
				</td>
			</tr>
			<tr>
				<td><label><form:checkbox path="popup" /><%=lang.__("To pop-up")%></label></td>
				<td cssStyle="padding-left: 10px;"><%=lang.__("Add delivery target")%>：<form:input path="additionalReceiver" readonly="false" cssStyle="resize: none; width:69%" /></td>
			</tr>
			<tr>
				<td><c:if test="${enablePush}"><label><form:checkbox path="push" /><%=lang.__("PUSH notice action")%></label></c:if></td>
				<td style="padding-left: 10px;"><%=lang.__("If you send to multiple destinations, enter them by comma-separated.")%></td>
			</tr>
			<tr>
				<td rowspan="3"><c:if test="${enablePush}"><div><%=lang.__("PUSH notice text")%>：</div><div><form:textarea path="pushContent" id="pushContent" readonly="false" rows="3" cols="45" cssStyle="resize: none;"></form:textarea></div></c:if></td>
				<td cssStyle="padding-left: 10px;"><label><form:checkbox path="notmail" /><%=lang.__("Not e-mail delivery.")%></label></td>
			</tr>
			<tr>
				<th style="padding-left: 10px;" class="info_th"><%=lang.__("･Choose fixed phrase for e-mail")%></th>
			</tr>
			<tr>
				<td style="padding-left: 10px;">
					<div style="float:left;"><%=lang.__("Title<!--3-->")%>：</div>
					<div cssStyle="float:left;"></div><form:input path="emailTitle" id="emailTitle" readonly="false" cssStyle="resize: none; width:55%" />
					&nbsp;<!-- <input type="button" class="thickbox ui-button ui-widget ui-state-default ui-corner-all" value="定型文の参照" onclick="location.href='${f:url('/template/')}?<%=FormUtils.getTokenParam(request)%>&TB2_iframe&width=800&height=450&modal=false'" />-->
					<input type="button" class="thickbox ui-button ui-widget ui-state-default ui-corner-all" value="<%=lang.__("Reference to template")%>" onclick="showTemplate(true)" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td style="padding-left: 10px;">
					<div style="float:left;"><%=lang.__("Text")%>：</div>
					<div cssStyle="float:left;"></div><form:textarea path="sendInformationContent" id="sendInformationContent" readonly="false" rows="6" cols="45" cssStyle="resize: none;"></form:textarea>
				</td>
			</tr>
			<tr>
				<td></td>
				<td style="padding-left: 10px;">
					<input type="file" name="formFiles"  class="multi" /><br />
				</td>
			</tr>
		</table>
		<Hr Color= gray width="90%" Size="1">
		<div align="right"  style="padding-right: 70px;">
			<input type="submit" value="<%=lang.__("Cancel")%>" onClick="self.parent.tb_remove(); return false;" class="ui-button ui-widget ui-state-default ui-corner-all" />
			<input type="button" value="<%=lang.__("Execution")%>" onclick="sendMessage()" class="ui-button ui-widget ui-state-default ui-corner-all" />
		</div>
	</div>
	<div id="template" style="display:none;background-color:white;">
		<table class="table01">
		<c:forEach var="e" varStatus="s" items="${noticetemplatetypeItems}">
			<c:set var="templist" value="${noticeTemplateMap[e.id]}" />
		  <c:forEach var="f" varStatus="t" items="${templist}">
		  <tr>
		    <c:if test="${t.index==0}">
		    	<td rowspan="${fn:length(templist)}">${fal:h(e.name)}</td>
		    </c:if>
		    <td>${fal:h(f.templateclass)}</td>
		    <td width="50"><div align="center">
		      <form:radiobutton path="templateid" value="${f.id}" id="templateid" />
		    </div></td>
		    <td>${fal:h(f.title)}</td>
		    <td width="500">${fal:h(f.content)}</td>
		  </tr>
		  </c:forEach>
		</c:forEach>
		</table>
		<table width="100%" border="0">
		  <tr>
		    <td align="right"><input type="button" id="seltemplate" value="<%=lang.__("Registration / instruction")%>" alt="<%=lang.__("Registration / instruction")%>" /></td>
		  </tr>
		</table>
	</div>
	</form:form>
    <!-- <div class="close"><input type="submit" value="×" onClick="self.parent.tb_remove(); return false;" /></div>-->
	<div id="templatemsg" style="display:none"></div>
</body>
</html>
