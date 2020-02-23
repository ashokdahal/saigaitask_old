<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<script type="text/javascript" src="${f:url('/js/msg.js')}"></script>
<link href="${f:url('/css/popup.css')}"  rel="stylesheet" type="text/css" />
<link href="${f:url('/css/jquery.tablesorter/style.css')}"  rel="stylesheet" type="text/css" />
<link href="${f:url('/css/jquery-ui-timepicker-addon.css')}" rel="stylesheet" type="text/css" />
<script src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}" type=" text/javascript"></script>
<script src="${f:url('/js/jquery-ui-timepicker-addon.js')}" type="text/javascript"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script src="${f:url('/js/jquery-ui-timepicker-ja.js')}" type="text/javascript"></script>
</c:if>
<style type="text/css">
<!--
.listgrp {
	border:2px solid #ccc;
	width:300px;
	height:150px;
	overflow-y:scroll;
}
.comment {
	width: 670px;
	border: solid 1px red;
	background-color: #FFFCDB;
	padding: 5px;
	margin: 8px 0px;
	line-height: 19px;
}
.headmessage {
	float: left;
}
.statusValues {
	border: solid 1px black;
	float: right;
	font-weight: bold;
	color: red;
	background-color: white;
	padding: 5px;
	margin-right: 10px;
}
div.clear{clear: both;}
span.bold{font-weight: bold;}
span.red{color: red;}
-->
</style>
<script type="text/javascript">
window.onload = function(){
	<%-- 訓練モードによる本文追加 --%>
	<c:if test="${ pageDto.trackData.trainingplandataid > 0 }">
		<c:if test="${ pageDto.trackData.trainingplanData.publiccommonsflag }">
	var pcommonsmailTitle = $("#pcommonsmailTitle").val();
	var pcommonsmailContent = $("#pcommonsmailContent").val();
	$("#pcommonsmailTitle").val("<%=lang.__("[Training]")%>" + pcommonsmailTitle);
	$("#pcommonsmailContent").val("<%=lang.__("[Training]")%>" + pcommonsmailContent);
		</c:if>
	</c:if>

	viewLength();
	viewLengthTitle();

	// 公式発表日時ピッカー生成
	$("#reporttime").datetimepicker({
		controlType : 'select',
		timeFormat : 'HH:mm:ss',
		dateFormat : 'yy/mm/dd'
	});
}
function viewLength() {
	var title = document.getElementsByName("pcommonsmailTitle")[0].value.length;
	var content = document.getElementsByName("pcommonsmailContent")[0].value.length;
	var n = document.getElementsByName("pcommonsmailContent")[0].value.split("\n").length - 1;

	document.getElementById("lengthnow").innerHTML = title + content + n + "<%=lang.__("Character")%>";

	if (title + content + n > 200) {
		document.getElementById("lengthnow").style.color = "red";
	} else {
		document.getElementById("lengthnow").style.color = "black";
	}
}
function viewLengthTitle() {
	var title = document.getElementsByName("pcommonsmailTitle")[0].value.length;
	document.getElementById("lengthnowtitle").innerHTML = title + "<%=lang.__("Character")%>";
	if (title > 15) {
		document.getElementById("lengthnowtitle").style.color = "red";
	} else {
		document.getElementById("lengthnowtitle").style.color = "black";
	}
}
function setTemplateId(id) {
	$('#templatemsg').load(SaigaiTask.contextPath+"/template/title/"+id, null,
		function(responseText, status, XMLHttpRequest) {
			//var pos = getCaretPosition($('#mailcontent').get(0));
			//insertTextAtPosision($('#mailcontent').get(0), pos, responseText);
			$('#pcommonsmailTitle').val(responseText);
			$('#emailTitle').val(responseText);
			$('#templatemsg').load(SaigaiTask.contextPath+"/template/content/"+id, null,
			function(responseText, status, XMLHttpRequest) {
				var pos = getCaretPosition($('#pcommonsmailContent').get(0));
				insertTextAtPosision($('#pcommonsmailContent').get(0), pos, responseText);
				viewLength();
			});

			viewLengthTitle();
		});
}
function setOrganization(arr) {
	if (arr["organizationType"] == "1") {
		$('#organizationName').val(arr["organizationName"]);
		$('#organizationCode').val(arr["organizationCode"]);
		$('#organizationDomainName').val(arr["organizationDomainName"]);
		$('#officeName').val(arr["officeName"]);
		$('#officeNameKana').val(arr["officeNameKana"]);
		$('#officeLocationArea').val(arr["officeLocationArea"]);
		$('#phone').val(arr["phone"]);
		$('#fax').val(arr["fax"]);
		$('#email').val(arr["email"]);
		$('#officeDomainName').val(arr["officeDomainName"]);
	} else if (arr["organizationType"] == "2") {
		$('#organizationNameEditorial').val(arr["organizationName"]);
		$('#organizationCodeEditorial').val(arr["organizationCode"]);
		$('#organizationDomainNameEditorial').val(arr["organizationDomainName"]);
		$('#officeNameEditorial').val(arr["officeName"]);
		$('#officeNameKanaEditorial').val(arr["officeNameKana"]);
		$('#officeLocationAreaEditorial').val(arr["officeLocationArea"]);
		$('#phoneEditorial').val(arr["phone"]);
		$('#faxEditorial').val(arr["fax"]);
		$('#emailEditorial').val(arr["email"]);
		$('#officeDomainNameEditorial').val(arr["officeDomainName"]);
	}
}
function sendMessage() {
	<%-- 訓練モードによる制御チェック --%>
	<c:if test="${ pageDto.trackData.trainingplandataid > 0 }">
		<c:if test="${ !pageDto.trackData.trainingplanData.publiccommonsflag }">
		alert("<%=lang.__("For undergoing training, we limit L-Alert send function ")%>");
		return;
		</c:if>
	</c:if>

	if (!confirm('<%=lang._E("Send displayed contents.\n Are you sure?")%>')) {
		return;
	}
	// old
 	document.forms[0].action = "${f:url('/page/pcommonsmail/send')}";
 	document.forms[0].submit();
// 	do {
// 		// new
// 		var $form = $('#the_form');
// 		$form.attr('action', '${f:url('send')}');

//         // 送信
//         $.ajax({
//             url: $form.attr('action'),
//             type: $form.attr('method'),
//             data: $form.serialize(),
//             timeout: 30000,

//             // 通信成功時の処理
//             success: function(result, textStatus, xhr) {
//             	if (result.result == "OK")
// 	            	alert("送信しました。");
//             	else
//                 	alert("送信時にエラーが発生しました。");
//             },

//             // 通信失敗時の処理
//             error: function(xhr, textStatus, error) {
//             	alert("送信できませんでした。");
//             }
//         });
// 	} while(0);
}
</script>
<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>
<form:form id="the_form" modelAttribute="pcommonsmailForm">
<% FormUtils.printToken(out, request); %>
	<div class="headmessage"><%=lang.__("Send the status report to L-Alert originating (emergency e-mail), and send e-mail for confirmation with the following contents.")%></div>
	<div class="statusValues">
		<c:choose>
		<c:when test="${statusValue == 'ACTUAL'}"><%=lang.__("L-Alert performance mode")%></c:when>
		<c:when test="${statusValue == 'EXERCISE'}"><%=lang.__("L-Alert training mode")%></c:when>
		<c:when test="${statusValue == 'TEST'}"><%=lang.__("L-Alert test mode")%></c:when>
		<c:otherwise><%=lang.__("Mode unknown")%></c:otherwise>
		</c:choose>
	</div>
	<div class="clear"></div>
	<span class="page_title"><%=lang.__("･The contents of the emergency e-mail")%></span>
	<table class="table_email">
		<tr>
			<td width="70px" valign="top"><%=lang.__("Title<!--2-->")%>：</td>
			<td width="200px">
				<form:input path="pcommonsmailTitle" id="pcommonsmailTitle" readonly="false" cssStyle="resize: none; width:100%" onblur="viewLengthTitle();viewLength();" onkeyup="viewLengthTitle();viewLength();" />
				<div><span id="lengthnowtitle"><%=lang.__("0 character")%></span><span><%=lang.__("/15 characters")%></span></div>
			</td>
			<td width="200px" valign="top">
				<a class="thickbox" href="${f:url('/template/')}${f:h(pageDto.menuInfo.id)}/${NoticeType.COMMONSMAIL}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&width=920&height=450"><input type="button" value="<%=lang.__("Reference to template")%>" class="ui-button ui-widget ui-state-default ui-corner-all" /></a>
			</td>
		</tr>
		<tr>
			<td valign="top"><%=lang.__("Text")%>：</td>
			<td colspan="2">
				<form:textarea path="pcommonsmailContent" id="pcommonsmailContent" readonly="false" rows="8" cols="60" cssStyle="resize: none;" onblur="viewLength();" onkeyup="viewLength();" ></form:textarea>
				<div><span id="lengthnow"><%=lang.__("0 character")%></span><span><%=lang.__("/200 characters (include title)")%></span></div>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<div class="comment" style="height:295px;">
					<span class="bold"><%=lang.__("Notes of text")%></span>
					<br /><%=lang.__("1. You must write local gov. name.( ex. This is xx prefecture or xx city.)")%>
					<br /><%=lang.__("2. Line feed equals to two characters.")%>
					<br /><%=lang.__("3. Tel, e-mail address, URI, mailto are not specified. You are not allowed to enter only blank.")%>
					<br /><%=lang.__("4. platform dependent characters can not be specified.")%>
					<br /><img src="../images/common/kisyuizonmoji.png" alt="<%=lang.__("Platform dependent characters example")%>" />
					<br /><%=lang.__("5. Don't send items not typed the below. (Only info complying with cell phone company's regulation can be sent)")%>
					<c:if test="${isPrefecture == false}">
						<br />&nbsp;<%=lang.__("･Evacuation preparation info, evacuation advisory, evacuation order")%>
					</c:if>
					<br />&nbsp;<%=lang.__("･Warning district info")%>
					<br />&nbsp;<%=lang.__("･Tsunami advisory, tsunami warning, major tsunami warning")%>
					<br />&nbsp;<%=lang.__("･Eruption warning(Except summit crater warning less than level 3)")%>
					<br />&nbsp;<%=lang.__("･Designated river flood warning (Excluding flooding attention info)")%>
					<br />&nbsp;<%=lang.__("･Landslide disaster warning info")%>
					<br />&nbsp;<%=lang.__("･Tokai quake prediction info")%>
					<br />&nbsp;<%=lang.__("･Ballistic missile, attacking info to air plain, guerilla attack info, major terror attack")%>
				</div>
			</td>
		</tr>
	</table>
	<br />
	<span class="page_title"><%=lang.__("･Announcement / creating organization")%></span>
	<table border="0" style="font-size:12px;border:1px;padding:4px;">
		<tr>
			<td valign="top" style="width: 40px;"></td>
			<td colspan="2">
				<table>
					<tr>
						<td width="140px"></td>
						<td width="210px"><span class="bold"><%=lang.__("Organization holding accountability")%></span></td>
						<td width="10px"></td>
						<td width="210px"><span class="bold"><%=lang.__("Creating organization (Organizations with responsibility)")%></span></td>
						<td width="200px"></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Organization name")%>：</td>
						<td width="210px">
							<form:input path="organizationName" id="organizationName" readonly="false" cssStyle="resize:none;width:135px;" />
							<a class="thickbox" href="${f:url('/organization/1/')}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&width=920&height=450">
								<input type="button" value="<%=lang.__("Select")%>" class="ui-button ui-widget ui-state-default ui-corner-all"/>
							</a>
						</td>
						<td width="10px"></td>
						<td width="210px">
							<form:input path="organizationNameEditorial" id="organizationNameEditorial" readonly="false" cssStyle="resize:none;width:135px;" />
							<a class="thickbox" href="${f:url('/organization/2/')}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&width=920&height=450">
								<input type="button" value="<%=lang.__("Select")%>" class="ui-button ui-widget ui-state-default ui-corner-all"/>
							</a>
						</td>
						<td width="200px"></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Local gov. code")%>：</td>
						<td width="210px"><form:input path="organizationCode" id="organizationCode" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="organizationCodeEditorial" id="organizationCodeEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"><%=lang.__("* 6 digits with check digits")%></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Organization domain")%>：</td>
						<td width="210px"><form:input path="organizationDomainName" id="organizationDomainName" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="organizationDomainNameEditorial" id="organizationDomainNameEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department name")%>：</td>
						<td width="210px"><form:input path="officeName" id="officeName" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="officeNameEditorial" id="officeNameEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department name (kana)")%>：</td>
						<td width="210px"><form:input path="officeNameKana" id="officeNameKana" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="officeNameKanaEditorial" id="officeNameKanaEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"><%=lang.__("* Two-byte kana character")%></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department address")%>：</td>
						<td width="210px"><form:input path="officeLocationArea" id="officeLocationArea" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="officeLocationAreaEditorial" id="officeLocationAreaEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department phone number")%>：</td>
						<td width="210px"><form:input path="phone" id="phone" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="phoneEditorial" id="phoneEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"><%=lang.__("* Need hyphen and district code")%></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department FAX number")%>：</td>
						<td width="210px"><form:input path="fax" id="fax" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="faxEditorial" id="faxEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"><%=lang.__("* Need hyphen and district code")%></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department  e-mail address")%>：</td>
						<td width="210px"><form:input path="email" id="email" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="emailEditorial" id="emailEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"></td>
					</tr>
					<tr>
						<td width="140px" valign="top"><%=lang.__("Department domain")%>：</td>
						<td width="210px"><form:input path="officeDomainName" id="officeDomainName" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="10px"></td>
						<td width="210px"><form:input path="officeDomainNameEditorial" id="officeDomainNameEditorial" readonly="false" cssStyle="resize: none; width:100%" /></td>
						<td width="200px"></td>
					</tr>
				</table>
				<div class="comment" style="height:125px;">
					<span class="bold"><%=lang.__("Attention of creator")%></span>
					<br/><%=lang.__("1. Initially show info of local prefectural gov. or city logging in to this system.")%>
					<br/><%=lang.__("2. Confirm that the info initially displayed is no errors, and send info after correct errors.")%>
					<br/><%=lang.__("3. In the announce organization district, type the name of city which accept accountability.")%>
					<br/><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.<!--2-->")%>
					<br/><%=lang.__("5. In case that division with no domain, specify the same content of department domain.")%>
				</div>
			</td>
		</tr>
		<tr>
			<td valign="top" style="width: 40px;"></td>
			<td colspan="2">
				<table>
					<tr>
						<td width="140px"><%=lang.__("Official announcement date and time")%>：</td>
						<td width="210px"><form:input id="reporttime" path="reporttime" cssStyle="resize: none; width: 150px;" /></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
				<div class="comment" style="height:65px;">
					<span class="bold"><%=lang.__("Notes of official announcement date and time")%></span>
					<br /><%=lang.__("1. Specify acknowledged disclosure date and time of the info.")%>
					<br /><%=lang.__("2. Keep in mind that the date and time is not when the info will send.")%>
				</div>
			</td>
		</tr>
	</table>
	<br />
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
