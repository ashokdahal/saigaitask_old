<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>

<link href="${f:url('/css/popup.css')}" rel="stylesheet" type="text/css" />
<link href="${f:url('/css/jquery.tablesorter/style.css')}" rel="stylesheet" type="text/css" />
<link href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" rel="stylesheet" type="text/css" />
<link href="${f:url('/css/jquery-ui-timepicker-addon.css')}" rel="stylesheet" type="text/css" />
<script src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}" type=" text/javascript"></script>
<script src="${f:url('/js/jquery-ui-timepicker-addon.js')}" type="text/javascript"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script src="${f:url('/js/jquery-ui-timepicker-ja.js')}" type="text/javascript"></script>
</c:if>
<style type="text/css">
<!--
.listgrp {
	border: 2px solid #ccc;
	width: 300px;
	height: 150px;
	overflow-y: scroll;
}
.todoufuken {
	border: 2px solid #ccc;
	width: 650px;
	height: 200px;
	overflow-y: scroll;
}
.chikugrp {
	width: 80px;
	height: 100px;
	float: left;
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
.tbl {
	height: 80px;
}
.tbl div{
	margin-right: 15px;
	float: left;
}
.tbl td{
	padding: 1px;
	background-color: white;
}
div.clear{clear: both;}
span.bold{font-weight: bold;}
span.red{color: red;}
span.blue{color: blue;}
span.green{color: green;}
span.gray{color: gray;}
-->
</style>
<script type="text/javascript" src="${f:url('/js/msg.js')}"></script>
<!-- CKEditor BEGIN  -->
<script type="text/javascript" src="${f:url('/js/ckeditor-4.6.2/ckeditor.js')}"></script>
<script type="text/javascript" src="${f:url('/js/ckeditor-4.6.2/adapters/jquery.js')}"></script>
<!-- CKEditor END  -->
<link href="${f:url('/css/popup.css')}"  rel="stylesheet" type="text/css" />
<link href="${f:url('/css/jquery.tablesorter/style.css')}"  rel="stylesheet" type="text/css" />
<script type="text/javascript">
function setTemplateId(id) {
	$('#templatemsg').load(SaigaiTask.contextPath+"/template/content/"+id, null,
		function(responseText, status, XMLHttpRequest) {
			<c:choose>
				<c:when test="${pcommonsmediaForm.isGeneral()}">
					// 全置換関数を用意
					var replaceAll = function (expression, org, dest){
					    return expression.split(org).join(dest);
					}
					var text = responseText;
					text = replaceAll(text, "\r\n", "\n");
					text = replaceAll(text, "\n", "<br />\r\n");
					var ckData = CKEDITOR.instances.pcommonsmediaContent.getData();
					var r = null;
					do {
						r = Math.random() + "";
					} while (ckData.indexOf(r) != -1);

					CKEDITOR.instances.pcommonsmediaContent.insertHtml(r);

					ckData = CKEDITOR.instances.pcommonsmediaContent.getData();

					ckData = ckData.replace(r, text);
					CKEDITOR.instances.pcommonsmediaContent.setData(ckData);
				</c:when>
				<c:when test="${pcommonsmediaForm.isEvent()}">
					var pos = getCaretPosition($('#pcommonsmediaContentEvent').get(0));
					insertTextAtPosision($('#pcommonsmediaContentEvent').get(0), pos, responseText);
					setTemplateTitle(id);
				</c:when>
			</c:choose>
		});
	$('#templatemsg').load(SaigaiTask.contextPath+"/template/title/"+id, null,
		function(responseText, status, XMLHttpRequest) {
			$('#title').val(responseText);
			$('#emailTitle').val(responseText);
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
$(function() {
	var dt = new Date();

	// 公式発表日時ピッカー生成
	$("#reporttime").datetimepicker({
		controlType : 'select',
		timeFormat : 'HH:mm:ss',
		dateFormat : 'yy/mm/dd'
	});

	// 希望公開終了日時
	<c:choose>
		<c:when test="${isAllClose == false}">
			// 全解除or全閉鎖でない場合、希望公開終了日時は表示しない
			document.getElementById("validdatetime").value = "";
			document.getElementById("validdatetime").style.display = "none";
			document.getElementById("validdatetimeterm").style.display = "none";
			document.getElementById("validdatetimemsg").style.display = "block";
		</c:when>
		<c:otherwise>
			// 48時間後をデフォルト表示
			var now = new Date();
			dt.setDate(now.getDate() + 2);
			var dtformat = [ dt.getFullYear(), dt.getMonth() + 1, dt.getDate() ].join('/') + ' ' + dt.toLocaleTimeString();

			<c:choose>
				<c:when test="${validationCheckflg == false}">
					document.getElementById("validdatetime").value = dtformat;
				</c:when>
			</c:choose>

			// 希望公開終了日時ピッカー生成
			$("#validdatetime").datetimepicker({
				controlType : 'select',
				timeFormat : 'HH:mm:ss',
				dateFormat : 'yy/mm/dd',
				hour : dt.getHours(),
				minute : dt.getMinutes()
			});

			<c:choose>
				<c:when test="${pcommonsmediaForm.isGeneral()}">
					// CKEditor
					//$("#pcommonsmediaContent").ckeditor();
					CKEDITOR.replace( 'pcommonsmediaContent', {
						customConfig: SaigaiTask.contextPath+'/js/saigaitask.page.pcommonsmedia_gi.ckeditor.config.js'
					});
					// 24時間～90日後の日時を表示
					var valid_from = 1;
					var valid_to = 90;
				</c:when>
				<c:when test="${pcommonsmediaForm.isEvent()}">
					// イベント開始時間用ピッカー生成
					$("#eventfrom").datetimepicker({
						controlType : 'select',
						timeFormat : 'HH:mm:ss',
						dateFormat : 'yy/mm/dd'
					});
					// イベント終了時間用ピッカー生成
					$("#eventto").datetimepicker({
						controlType : 'select',
						timeFormat : 'HH:mm:ss',
						dateFormat : 'yy/mm/dd'
					});
					// 24時間～90日後の日時を表示
					var valid_from = 1;
					var valid_to = 90;
				</c:when>
				<c:when test="${pcommonsmediaForm.isShelter()}">
					// 24時間～72時間後の日時を表示
					var valid_from = 1;
					var valid_to = 3;
				</c:when>
				<c:when test="${pcommonsmediaForm.isRefuge()}">
					// 24時間～72時間後の日時を表示
					var valid_from = 1;
					var valid_to = 3;
				</c:when>
				<c:when test="${pcommonsmediaForm.isDamage()}">
				// 24時間～72時間後の日時を表示
				var valid_from = 1;
				var valid_to = 3;
				</c:when>
			</c:choose>

			var dt2 = new Date();
			dt2.setDate(now.getDate() + valid_from);
			var dtformat_from = [ dt2.getFullYear(), dt2.getMonth() + 1, dt2.getDate() ].join('/') + ' ' + dt2.toLocaleTimeString();

			var dt3 = new Date();
			dt3.setDate(now.getDate() + valid_to);
			var dtformat_to = [ dt3.getFullYear(), dt3.getMonth() + 1, dt3.getDate() ].join('/') + ' ' + dt3.toLocaleTimeString();

			document.getElementById("validdatetimeterm").innerHTML = MessageFormat.format('<%=lang.__("You can be set between {0} to {1}")%>', dtformat_from, dtformat_to);
			document.getElementById("validdatetimetermfrom").value = dtformat_from;
			document.getElementById("validdatetimetermto").value = dtformat_to;

			document.getElementById("validdatetime").style.display = "block";
			document.getElementById("validdatetimeterm").style.display = "block";
			document.getElementById("validdatetimemsg").style.display = "none";
		</c:otherwise>
	</c:choose>
});
function changeDocumentId(){
	var element = document.getElementById("documentId");
	var sindex;
	for (var i=0;i<element.options.length;i++) {
		if (element.options[i].selected) {
			sindex=i;break;
		}
	}

	// 先頭行(新規発信)は訂正、取消のラジオを選択不可に
	if (sindex == 0) {
		$("*[name=distributiontype]")[0].checked = true;
		$("*[name=distributiontype]")[1].disabled = true;
		$("*[name=distributiontype]")[2].disabled = true;
	// 選択行は該当の過去発信データを取得
	} else {
		$("*[name=distributiontype]")[0].checked = false;
		$("*[name=distributiontype]")[1].checked = false;
		$("*[name=distributiontype]")[2].checked = false;
		$("*[name=distributiontype]")[1].disabled = false;
		$("*[name=distributiontype]")[2].disabled = false;
<%--	document.page_pcommonsmediaActionForm.submit(); 2017.07.28 kawada 修正 --%>
		var form = document.getElementById("the_form");
		form.submit();
	}
}
function showCancelConfirm(){
	if (!confirm('<%=lang._E("You can not undo. Cancel?\n Use correct delivery in case of correcting false report.")%>')) {
		$("*[name=distributiontype]")[2].checked = false;
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
	var form = document.getElementById("the_form");
	// old
	form.action = "${f:url('/page/pcommonsmedia/send')}";
	<c:choose>
		<c:when test="${pcommonsmediaForm.isGeneral()}">
			form.pcommonsmediaContent.value = CKEDITOR.instances.pcommonsmediaContent.getData();
		</c:when>
	</c:choose>
	form.submit();
}
</script>

<font color="red"><ul><c:forEach var="msg" items="${sessionScope['action_errors']}"><li><c:out value="${msg}" escapeXml="false"/></li></c:forEach><c:remove var="action_errors" scope="session" /></ul></font>
<form:form id="the_form" modelAttribute="pcommonsmediaForm">
<% FormUtils.printToken(out, request); %>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<div class="headmessage"><%=lang.__("Send the status report to L-Alert (media), and send e-mail for confirmation with following contents.")%></div>
<div class="statusValues">
	<c:choose>
	<c:when test="${pcommonsmediaForm.statusValue == 'ACTUAL'}"><%=lang.__("L-Alert performance mode")%></c:when>
	<c:when test="${pcommonsmediaForm.statusValue == 'EXERCISE'}"><%=lang.__("L-Alert training mode")%></c:when>
	<c:when test="${pcommonsmediaForm.statusValue == 'TEST'}"><%=lang.__("L-Alert test mode")%></c:when>
	<c:otherwise><%=lang.__("Mode unknown")%></c:otherwise>
	</c:choose>
</div>
<div class="clear"></div>
		<c:choose>
<%-- 避難所 --%>
			<c:when test="${pcommonsmediaForm.isShelter()}">
				<span class="page_title"><%=lang.__("･Opening info")%></span>
				<table border="0" style="font-size:12px;border:0px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;">XML：</td>
						<td colspan="2">
							<table border="1" style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Type<!--2-->")%><br /><%=lang.__("The system")%></td>
									<td width="180px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Type<!--2-->")%><br /><%=lang.__("L-Alert")%></td>
									<td width="180px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Shelter name")%></td>
									<td width="250px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Address")%><br /><%=lang.__("Coordinates<!--2-->")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Opening")%><br /><%=lang.__("Status")%></td>
									<td width="140px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Opening date and time")%><br /><%=lang.__("Closing date and time")%></td>
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Number of evacuees")%><br /><%=lang.__("(Include voluntary evacuation)")%></td>
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Number of evacuated households")%><br /><%=lang.__("(Include voluntary evacuation)")%></td>
<!-- 										<td width="70px" style="background-color: #F0F0F6; padding: 4px;">テーブル名</td> -->
								</tr>
								<c:forEach var="shelter" varStatus="s" items="${shelterInformationList}">
									<tr align="center" height="40px">
										<td>${f:h(shelter.shelterLayerName)}</td>
										<td>
											${f:h(shelter.type)}
											<c:if test="${shelter.typeDetail != ''}">
												<br />(${f:h(shelter.typeDetail)})
											</c:if>
										</td>
										<td>
											${f:h(shelter.shelterName)}
										</td>
										<td>
											<c:choose>
												<c:when test="${shelter.shelterAddress == ''}">-<br /></c:when>
												<c:otherwise>${f:h(shelter.shelterAddress)}<br /></c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test="${shelter.circle == ''}">-</c:when>
												<c:otherwise>(${f:h(shelter.circle)})</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${shelter.shelterStatus == lang.__('Closing')}"><span class="red">${f:h(shelter.shelterStatus)}</span></c:when>
												<c:when test="${shelter.shelterStatus == lang.__('Opening')}"><span class="blue">${f:h(shelter.shelterStatus)}</span></c:when>
												<c:when test="${shelter.shelterStatus == lang.__('Permanent status')}"><span class="green">${f:h(shelter.shelterStatus)}</span></c:when>
												<c:when test="${shelter.shelterStatus == lang.__('Not opening')}"><span class="gray">${f:h(shelter.shelterStatus)}</span></c:when>
												<c:otherwise>${shelter.shelterStatus}</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${shelter.setupTime == ''}">-</c:when>
												<c:otherwise>${f:h(shelter.setupTime)}</c:otherwise>
											</c:choose>
											<br />
											<c:choose>
												<c:when test="${shelter.closeTime == ''}">-</c:when>
												<c:otherwise>${f:h(shelter.closeTime)}</c:otherwise>
											</c:choose>
										</td>
										<td>
											<fmt:formatNumber value="${f:h(shelter.headCount)}" pattern="###,###" />
											<c:if test="${shelter.headCountVoluntary != ''}">
												<br />(<fmt:formatNumber value="${f:h(shelter.headCountVoluntary)}" pattern="###,###" />)
											</c:if>
										</td>
										<td>
											<fmt:formatNumber value="${f:h(shelter.houseHolds)}" pattern="###,###" />
											<c:if test="${shelter.houseHoldsVoluntary != ''}">
													<br />(<fmt:formatNumber value="${f:h(shelter.houseHoldsVoluntary)}" pattern="###,###" />)
											</c:if>
										</td>
<%-- 											<td>${f:h(shelter.tablename)}</td> --%>
									</tr>
								</c:forEach>
							</table>
							<div class="comment" style="height:140px;">
								<span class="bold"><%=lang.__("Notes of opening situation")%></span>
								<br /><%=lang.__("1. Type system is a type of shelter. It will not send to L-Alert.")%>
								<br /><%=lang.__("2. L-Alert type will send to L-Alert.")%>
								<br /><%=lang.__("3. Open (or close) date and time will be send to L-Alert only at the open (or close) date and time.")%>
								<br /><%=lang.__("4. Shelter status is changed and sent to an expression corresponding to L-Alert.")%>
								<br /><%=lang.__("5. The coordinates, in addition to the latitude and longitude, 0 as radius will be transferred to the L-Alert.")%>
								<br /><%=lang.__("6. The number of evacuees and evacuated households are sent to L-Alert only when the shelter is open or permanent.")%>
							</div>
							<br />
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Supplementary info")%>：</td>
									<td width="540px" colspan="2"><form:input path="complementaryInfo" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of supplementary info")%></span>
								<br /><%=lang.__("Enter message you want to send to residents, such as notes, reason to be evacuated, etc.")%>
							</div>
							<br />
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Disaster name<!--2-->")%>：</td>
									<td width="540px" colspan="2"><form:input path="trackdataname" readonly="false" cssStyle="resize: none; width:90%" /></td>
								</tr>
							</table>
							<div class="comment" style="height:70px;">
								<span class="bold"><%=lang.__("Notes of disaster name")%></span>
								<br /><%=lang.__("1. Disaster name will be communicated to the L-Alert as the subject of evacuation.")%>
								<br /><%=lang.__("2. It is no problem in the tentative name until determined formal disaster name.")%>
							</div>
						</td>
					</tr>
				</table>
			</c:when>
<%-- 避難勧告/避難指示 --%>
			<c:when test="${pcommonsmediaForm.isRefuge()}">
				<span class="page_title"><%=lang.__("･Evacuation advisory/order info")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;">XML：</td>
						<td colspan="2">
							<table border="1" style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="150px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("District name<!--2-->")%></td>
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Announcement type")%></td>
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Last time")%><br /><%=lang.__("Announcement type")%></td>
									<td width="140px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Announcement time<!--2-->")%></td>
									<td width="140px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Number of affected households/persons")%></td>
<!-- 										<td width="140px" style="background-color: #F0F0F6; padding: 4px;">テーブル名</td> -->
								</tr>
								<c:forEach var="refuge" varStatus="s" items="${refugeInformationList}">
									<tr align="center" height="27px">
										<td>${f:h(refuge.chikuName)}</td>
										<td>${f:h(refuge.hatureiKbn)}</td>
										<td>
											<c:choose>
												<c:when test="${refuge.hatureiKbn == lang.__('Cancel<!--4-->')}">
													<c:choose>
														<c:when test="${lasthatureiKbnMap[refuge.chikuName] == lang.__('Evacuation preparation')}">
															<select name="lasthatureiKbnMap(<c:out value="${f:h(refuge.chikuName)}"></c:out>)">
																<option value=""><%=lang.__("Select<!--2-->")%></option>
																<option selected value="<%=lang.__("Evacuation preparation")%>"><%=lang.__("Evacuation preparation")%></option>
																<option value="<%=lang.__("Evacuation advisory")%>"><%=lang.__("Evacuation advisory")%></option>
																<option value="<%=lang.__("Evacuation order")%>"><%=lang.__("Evacuation order")%></option>
															</select>
														</c:when>
														<c:when test="${lasthatureiKbnMap[refuge.chikuName] == lang.__('Evacuation advisory')}">
															<select name="lasthatureiKbnMap(<c:out value="${f:h(refuge.chikuName)}"></c:out>)">
																<option value=""><%=lang.__("Select<!--2-->")%></option>
																<option value="<%=lang.__("Evacuation preparation")%>"><%=lang.__("Evacuation preparation")%></option>
																<option selected value="<%=lang.__("Evacuation advisory")%>"><%=lang.__("Evacuation advisory")%></option>
																<option value="<%=lang.__("Evacuation order")%>"><%=lang.__("Evacuation order")%></option>
															</select>
														</c:when>
														<c:when test="${lasthatureiKbnMap[refuge.chikuName] == lang.__('Evacuation order')}">
															<select name="lasthatureiKbnMap(<c:out value="${f:h(refuge.chikuName)}"></c:out>)">
																<option value=""><%=lang.__("Select<!--2-->")%></option>
																<option value="<%=lang.__("Evacuation preparation")%>"><%=lang.__("Evacuation preparation")%></option>
																<option value="<%=lang.__("Evacuation advisory")%>"><%=lang.__("Evacuation advisory")%></option>
																<option selected value="<%=lang.__("Evacuation order")%>"><%=lang.__("Evacuation order")%></option>
															</select>
														</c:when>
														<c:otherwise>
															<select name="lasthatureiKbnMap(<c:out value="${f:h(refuge.chikuName)}"></c:out>)">
																<option selected value=""><%=lang.__("Select<!--2-->")%></option>
																<option value="<%=lang.__("Evacuation preparation")%>"><%=lang.__("Evacuation preparation")%></option>
																<option value="<%=lang.__("Evacuation advisory")%>"><%=lang.__("Evacuation advisory")%></option>
																<option value="<%=lang.__("Evacuation order")%>"><%=lang.__("Evacuation order")%></option>
															</select>
														</c:otherwise>
													</c:choose>
												</c:when>
											<c:otherwise>${f:h(refuge.lasthatureiKbn)}</c:otherwise>
										</c:choose>
										<td>${f:h(refuge.hatureiDateTime)}</td>
										<td>${f:h(refuge.targetHouseholds)} / ${f:h(refuge.people)}</td>
<%-- 											<td>${f:h(refuge.tablename)}</td> --%>
									</tr>
								</c:forEach>
							</table>
							<div class="comment">
								<span class="bold"><%=lang.__("Notes of evacuation advisory/order")%></span>
								<br /><%=lang.__("1. Only city allowed to send. In case that local prefectural gov. announce, send info on city window.")%>
								<br /><%=lang.__("2. In case of cancel, the number of affected households and persons will not send to L-Alert.")%>
								<br /><%=lang.__("3. Announce type and previous announce type are changed and sent to an expression corresponding to L-Alert.")%>
								<br /><%=lang.__("4. If the entire district and a separate district name is mixed, both will be communicated to the L-Alert.")%>
								<br /><%=lang.__("5. In case of announcement type blank, whole district not displayed and not sent to L-Alert.")%>
								<br /><%=lang.__("6. In case to send district name separated, send canceled district info with selected previous announcement type.")%>
								<br /><%=lang.__("7. You selected incorrect previous announcement type and canceled. Then, in case to send district name separated, send corrected info.")%>
								<br /><%=lang.__("8. In case to send district name separated, send info after deleting it.")%>
								<br /><%=lang.__("(Example) In case that A district is separated to be 1 Chome and 2 Chome  and canceled direction of 2 Chome after A section is issued to be evacuation order.")%>
								<div class="tbl">
									<div><%=lang.__("(T)")%>
										<table border="1">
											<tr><td><%=lang.__("A district 1")%></td><td><%=lang.__("Evacuation order")%></td></tr>
											<tr><td><%=lang.__("A district 2")%></td><td><%=lang.__("Cancel<!--4-->")%></td></tr>
										</table>
									</div>
									<div><%=lang.__("(F)")%>
										<table border="1">
											<tr><td><%=lang.__("A district 1")%></td><td><%=lang.__("Evacuation order")%></td></tr>
											<tr><td><%=lang.__("A district 2")%></td><td><%=lang.__("Cancel<!--4-->")%></td></tr>
											<tr><td><%=lang.__("A district")%></td><td><%=lang.__("Evacuation order")%></td></tr>
										</table>
									</div>
									<div class="clear"></div>
								</div>
								<div class="clear"></div>
							</div>
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Supplementary info")%>：</td>
									<td width="540px" colspan="2"><form:input path="complementaryInfo" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of supplementary info")%></span>
								<br /><%=lang.__("Enter message you want to send to residents, such as notes, reason to be evacuated, etc.")%>
							</div>
							<br />
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Disaster name<!--2-->")%>：</td>
									<td width="540px" colspan="2"><form:input path="trackdataname" readonly="false" cssStyle="resize: none; width:90%" /></td>
								</tr>
							</table>
							<div class="comment" style="height:70px;">
								<span class="bold"><%=lang.__("Notes of disaster name")%></span>
								<br /><%=lang.__("1. Disaster name will be communicated to the L-Alert as the subject of evacuation.")%>
								<br /><%=lang.__("2. It is no problem in the tentative name until determined formal disaster name.")%>
							</div>
						</td>
					</tr>
				</table>
			</c:when>
<%-- 被害情報 --%>
			<c:when test="${pcommonsmediaForm.isDamage()}">
				<span class="page_title"><%=lang.__("･Human suffering")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Deaths")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Missing")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Seriously injured<!--2-->")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Slightly injured")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.deadPeople)}</td>
										<td>${f:h(damageInformationDto.missingPeople)}</td>
										<td>${f:h(damageInformationDto.seriouslyInjuredPeople)}</td>
										<td>${f:h(damageInformationDto.slightlyInjuredPeople)}</td>
									</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･House damage")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">

							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Fully destroyed (buildings)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Fully destroyed (householdings)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Fully destroyed (people)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Partially destroyed (buildings)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Partially destroyed (householdings)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Partially destroyed (people)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Partial damage (buildings)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Partial damage (households)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Partial damage (people)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Inundation above floor (houses)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Inundation above floor (households)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Inundation above floor (people)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Inundation under floor (houses)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Inundation under floor (households)")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Inundation under floor (people)")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.totalCollapseBuilding)}</td>
										<td>${f:h(damageInformationDto.totalCollapseHousehold)}</td>
										<td>${f:h(damageInformationDto.totalCollapseHuman)}</td>
										<td>${f:h(damageInformationDto.halfCollapseBuilding)}</td>
										<td>${f:h(damageInformationDto.halfCollapseHousehold)}</td>
										<td>${f:h(damageInformationDto.halfCollapseHuman)}</td>

										<td>${f:h(damageInformationDto.someCollapseBuilding)}</td>
										<td>${f:h(damageInformationDto.someCollapseHousehold)}</td>
										<td>${f:h(damageInformationDto.someCollapseHuman)}</td>

										<td>${f:h(damageInformationDto.overInundationBuilding)}</td>
										<td>${f:h(damageInformationDto.overInundationHousehold)}</td>
										<td>${f:h(damageInformationDto.overInundationHuman)}</td>
										<td>${f:h(damageInformationDto.underInundationBuilding)}</td>
										<td>${f:h(damageInformationDto.underInundationHousehold)}</td>
										<td>${f:h(damageInformationDto.underInundationHuman)}</td>
									</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Ruinous house damage")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">


							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Public facility")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Other")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.publicBuilding)}</td>
										<td>${f:h(damageInformationDto.otherBuilding)}</td>
									</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Other damage")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Outflow, burying rice field")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Flooded rice fields")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Outflow, burying farm")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Flooded farms")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Educational facilities")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Hospital")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Road")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Bridge")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("River")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Port")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Erosion control")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Cleaning facilities")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Cliff failure")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Train service interruptions")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Damaged ship")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Water supply<!--2-->")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Telephone")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Electricity")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Gas")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Block wall, etc.")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.ricefieldOutflowBuried)}</td>
										<td>${f:h(damageInformationDto.ricefieldFlood)}</td>
										<td>${f:h(damageInformationDto.fieldOutflowBuried)}</td>
										<td>${f:h(damageInformationDto.fieldFlood)}</td>
										<td>${f:h(damageInformationDto.educationalFacilities)}</td>
										<td>${f:h(damageInformationDto.hospital)}</td>

										<td>${f:h(damageInformationDto.road)}</td>
										<td>${f:h(damageInformationDto.bridge)}</td>
										<td>${f:h(damageInformationDto.river)}</td>
										<td>${f:h(damageInformationDto.port)}</td>
										<td>${f:h(damageInformationDto.sedimentControl)}</td>

										<td>${f:h(damageInformationDto.cleaningFacility)}</td>
										<td>${f:h(damageInformationDto.cliffCollapse)}</td>
										<td>${f:h(damageInformationDto.railwayInterruption)}</td>
										<td>${f:h(damageInformationDto.ship)}</td>
										<td>${f:h(damageInformationDto.water)}</td>
										<td>${f:h(damageInformationDto.phone)}</td>
										<td>${f:h(damageInformationDto.electric)}</td>
										<td>${f:h(damageInformationDto.gas)}</td>
										<td>${f:h(damageInformationDto.blockWalls_Etc)}</td>
									</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Suffering")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Number of afflicted households")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Number of victims")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.suffererHousehold)}</td>
										<td>${f:h(damageInformationDto.suffererHuman)}</td>
									</tr>
							</table>
							<br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Fire")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Building")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Hazardous material")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Other")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.fireBuilding)}</td>
										<td>${f:h(damageInformationDto.fireDangerousGoods)}</td>
										<td>${f:h(damageInformationDto.otherFire)}</td>
									</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Cost of damage (facilities)")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1" style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Public educational facilities")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Agriculture, forestry and fisheries facilities")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Public works facilities")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Subtotal")%></td>
								</tr>
								<tr>
									<td>${f:h(damageInformationDto.publicScoolFacillities)}</td>
									<td>${f:h(damageInformationDto.agricultureFacilities)}</td>
									<td>${f:h(damageInformationDto.publicEngineeringFacilities)}</td>
									<td>${f:h(damageInformationDto.subtotalDamageFacilities)}</td>
								</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Cost of damage (other)")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1" style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Damaged agriculture")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Damaged forestry")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Livestock damage")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Damaged fisheries")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Commerce and industry damage")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Other")%></td>
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Subtotal")%></td>
								</tr>
								<tr>
									<td>${f:h(damageInformationDto.farmingDamage)}</td>
									<td>${f:h(damageInformationDto.forestryDamage)}</td>
									<td>${f:h(damageInformationDto.animalDamage)}</td>
									<td>${f:h(damageInformationDto.fisheriesDamage)}</td>
									<td>${f:h(damageInformationDto.commerceAndIndustryDamage)}</td>
									<td>${f:h(damageInformationDto.otherDamageOther)}</td>
									<td>${f:h(damageInformationDto.subtotalOtherDamage)}</td>
								</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Cost of damage (total amount)")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1" style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Total amount")%></td>
								</tr>
								<tr>
									<td>${f:h(damageInformationDto.totalDamage)}</td>
								</tr>
							</table><br />
						</td>
					</tr>
				</table>
				<span class="page_title"><%=lang.__("･Fire fighting")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table border="1"
								style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Accumulated number of firefighter going into action")%></td>
									<td width="70px"
										style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Accumulated number of volunteer fire corps going into action")%></td>
								</tr>
									<tr>
										<td>${f:h(damageInformationDto.fireman1)}</td>
										<td>${f:h(damageInformationDto.fireman2)}</td>
									</tr>
							</table><br />
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Supplementary info")%>：</td>
									<td width="540px" colspan="2"><form:input path="complementaryInfo" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<div class="comment" style="height:100px;">
								<span class="bold"><%=lang.__("Notes of supplementary info")%></span>
								<br /><%=lang.__("1. Please enter matters, such as contents which are typed in the No.4 format note district, to be transmitted to residents and media institutions.")%>
								<br /><%=lang.__("2. If contents typed in the note district of the No.4 format, it will be initially displayed.")%>
								<br /><%=lang.__("3. In case that you have no other choice than to send all disaster numbers unconfirmed, enter [send blank due to the numbers unconfirmed].")%>
							</div>
							<br />
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Disaster name<!--2-->")%>：</td>
									<td width="540px" colspan="2"><form:input path="trackdataname" readonly="false" cssStyle="resize: none; width:90%" /></td>
								</tr>
							</table>
							<div class="comment" style="height:70px;">
								<span class="bold"><%=lang.__("Notes of disaster name")%></span>
								<br /><%=lang.__("1. Disaster name will be communicated to the L-Alert as the subject of evacuation.")%>
								<br /><%=lang.__("2. It is no problem in the tentative name until determined formal disaster name.")%>
							</div>
						</td>
					</tr>
				</table>
			</c:when>
<%-- イベント --%>
			<c:when test="${pcommonsmediaForm.isEvent()}">
				<span class="page_title"><%=lang.__("･Event content")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Past events")%>：</td>
									<td width="560px" colspan="2">
										<form:select path="documentId" id="documentId" onchange="changeDocumentId();">
											<form:option value= "0"><%=lang.__("* When sending a new event, select this line.")%></form:option>
											<c:forEach var="event" varStatus="s" items="${eventInformationList}">
												<form:option value= "${f:h(event.documentid)}"><c:out value="${f:h(event.title)}" /></form:option>
											</c:forEach>
										</form:select>
									</td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of past events")%></span>
								<br /><%=lang.__("Please specify the target info to update, correct or cancel events sent before.")%>
							</div>
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Title<!--2-->")%>：</td>
									<td width="200px" colspan="2"><form:input path="title" id="title" readonly="false" cssStyle="resize: none; width:100%" /></td>
									<td width="360px"><a class="thickbox " href="${f:url('/template/')}${f:h(pageDto.menuInfo.id)}/${NoticeType.COMMONSMADIA}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&width=920&height=450"><input type="button" value="<%=lang.__("Reference to template")%>" class="ui-button ui-widget ui-state-default ui-corner-all" /></a></td>
								</tr>
							</table>
							<br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Text")%>：</td>
									<td width="560px" colspan="2"><form:textarea cols="48" id="pcommonsmediaContentEvent" rows="8" path="text" cssStyle="resize: none;"></form:textarea>

									</td>
								</tr>
							</table> <br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Venue")%>：</td>
									<td width="560px" colspan="2"><form:input path="area" readonly="false" cssStyle="resize: none; width:100%" /></td>
								</tr>
							</table>
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"></td>
									<td width="560px" rowspan="2" valign="top"><%=lang.__("In case of existing multiple items, input comma-separated.")%></td>

								</tr>
							</table> <br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("The start date and time")%>：</td>
									<td  width="200px" colspan="2"><form:input id="eventfrom" path="eventFrom" cssStyle="resize: none; width:100%" /></td>
									<td width="110px" rowspan="2" valign="top" Align="right"><%=lang.__("The end date and time")%>：</td>
									<td  width="200px" colspan="2"><form:input id="eventto" path="eventTo" cssStyle="resize: none; width:100%" /></td>
								</tr>
							</table>
							<br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Entry fee")%>：</td>
									<td width="560px" colspan="2"><form:input path="eventFee" readonly="false" cssStyle="resize: none; width:150px;" />&nbsp;&nbsp;<%=lang.__("(Example) Free, Adults 1000 yen/Child 500 yen")%></td>
								</tr>
							</table>
							<br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Announcement URI")%>：</td>
									<td width="560px" colspan="2"><form:input path="notificationUri" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("File URI")%>：</td>
									<td width="560px" colspan="2"><form:input path="fileUri" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("File title")%>：</td>
									<td width="560px" colspan="2"><form:input path="fileCaption" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of announcement URI / file URL")%></span>
								<br /><%=lang.__("Specify URI of images, videos, audios, PDF file, etc. which are in public on Internet by creator.")%>
							</div>
						</td>
					</tr>
				</table>
			</c:when>
<%-- 2017.07.28 kawada 同フォームが２つ存在していたので修正 --%>
		<%@ include file="content2.jsp" %>
	</c:choose>
		<br />
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
						<c:choose>
							<c:when test="${pcommonsmediaForm.isEvent()}">
							<tr>
								<td width="140px" valign="top"><%=lang.__("Name of the person in charge")%>：</td>
								<td width="210px"><form:input path="personResponsible" readonly="false" cssStyle="resize: none; width:100%" /></td>
								<td width="10px"></td>
								<td width="210px"></td>
								<td width="200px"></td>
							</tr>
							</c:when>
						</c:choose>
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
					<div class="comment">
						<span class="bold"><%=lang.__("Attention of creator")%></span>
						<br /><%=lang.__("1. Initially show info of local prefectural gov. or city logging in to this system.")%>
						<br /><%=lang.__("2. Confirm that the info initially displayed is no errors, and send info after correct errors.")%>
						<br />
						<c:choose>
							<c:when test="${pcommonsmediaForm.isShelter()}"><%=lang.__("3. In the announce organization district, type the name of city which accept accountability or local prefectural gov.")%></c:when>
							<c:when test="${pcommonsmediaForm.isRefuge()}"><%=lang.__("3. In the announce organization district, type the name of city which accept accountability or local prefectural gov.<!--3-->")%></c:when>
							<c:when test="${pcommonsmediaForm.isDamage()}"><%=lang.__("3. In the announce organization district, type the name of city which accept accountability or local prefectural gov.<!--2-->")%></c:when>
							<c:when test="${pcommonsmediaForm.isEvent()}"><%=lang.__("3. In the announce organization district, type the name of city which accept accountability.")%></c:when>
							<c:when test="${pcommonsmediaForm.isGeneral()}"><%=lang.__("3. In the announce organization district, type the name of city which accept accountability.")%></c:when>
							<c:when test="${pcommonsmediaForm.isAntidisaster()}"><%=lang.__("3. In the announce organization district, type the name of city which accept accountability.<!--2-->")%></c:when>
						</c:choose>
						<br />
						<c:choose>
							<c:when test="${pcommonsmediaForm.isShelter()}"><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.")%></c:when>
							<c:when test="${pcommonsmediaForm.isRefuge()}"><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.<!--4-->")%></c:when>
							<c:when test="${pcommonsmediaForm.isDamage()}"><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.<!--2-->")%></c:when>
							<c:when test="${pcommonsmediaForm.isEvent()}"><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.<!--2-->")%></c:when>
							<c:when test="${pcommonsmediaForm.isGeneral()}"><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.<!--2-->")%></c:when>
							<c:when test="${pcommonsmediaForm.isAntidisaster()}"><%=lang.__("4. Specify the name of local gov. creating the info with responsibility as the creator.<!--3-->")%></c:when>
						</c:choose>
						<br /><%=lang.__("5. In case that division with no domain, specify the same content of department domain.")%>
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
		<span class="page_title"><%=lang.__("･Transmission type")%></span>
		<table border="0" class="table_pcommonsoption" style="margin-left: 40px;">
			<tr>
				<td colspan="2">
					<form:radiobutton path="distributiontype" value="normalSend" /><%=lang.__("Deliver in normal")%><br />
					<form:radiobutton path="distributiontype" value="correctionSend" /><%=lang.__("Deliver correction")%><br />
					<form:radiobutton path="distributiontype" value="cancelSend" onclick="showCancelConfirm();" /><%=lang.__("Deliver cancel")%>
					<div class="comment" style="height:85px;">
						<span class="bold"><%=lang.__("Attention of delivery type")%></span>
						<c:choose>
							<c:when test="${pcommonsmediaForm.isShelter()}"><br /><%=lang.__("Normal sending - Select this to change the last message sent or to send new messages.")%><br /><%=lang.__("Correction delivery : select this to correct the last message sent in which there are some errors.")%><br /><%=lang.__("Cancel delivery : you cancel the last delivery which is incorrect and select it.")%></c:when>
							<c:when test="${pcommonsmediaForm.isRefuge()}"><br /><%=lang.__("Normal sending - Select this to change the last message sent or to send new messages.")%><br /><%=lang.__("Correction delivery : select this to correct the last message sent in which there are some errors.")%><br /><%=lang.__("Cancel delivery : you cancel the last delivery which is incorrect and select it.")%></c:when>
							<c:when test="${pcommonsmediaForm.isDamage()}"><br /><%=lang.__("Normal sending - Select this to change the last message sent or to send new messages.")%><br /><%=lang.__("Correction delivery : select this to correct the last message sent in which there are some errors.")%><br /><%=lang.__("Cancel delivery : you cancel the last delivery which is incorrect and select it.")%></c:when>
							<c:when test="${pcommonsmediaForm.isEvent()}"><br /><%=lang.__("Normal sending - Select this to change message sent in the past or to send new messages.")%><br /><%=lang.__("Correction delivery : select this to correct message sent in the past in which there are some errors.")%><br /><%=lang.__("Cancel delivery : you cancel delivery in the past which is incorrect and select it.")%></c:when>
							<c:when test="${pcommonsmediaForm.isGeneral()}"><br /><%=lang.__("Normal sending - Select this to change message sent in the past or to send new messages.")%><br /><%=lang.__("Correction delivery : select this to correct message sent in the past in which there are some errors.")%><br /><%=lang.__("Cancel delivery : you cancel delivery in the past which is incorrect and select it.")%></c:when>
							<c:when test="${pcommonsmediaForm.isAntidisaster()}"><br /><%=lang.__("Normal sending - Select this to change the last message sent or to send new messages.")%><br /><%=lang.__("Correction delivery : select this to correct the last message sent in which there are some errors.")%><br /><%=lang.__("Cancel delivery : you cancel the last delivery which is incorrect and select it.")%></c:when>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<td><%=lang.__("Contents and reason to be corrected / canceled")%>：</td>
				<td>
					<form:input path="description" readonly="false" cssStyle="resize:none;width:500px;" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="comment" style="height:65px;">
						<span class="bold"><%=lang.__("Attention of contents and reason to be corrected / canceled")%></span>
						<br /><%=lang.__("1. To correct or delete, enter its content and reason briefly.")%>
						<br /><%=lang.__("2. This statement directly sent to residents, enter apology statement in case of correct or cancel.")%>
					</div>
				</td>
			</tr>
			<tr>
				<td><%=lang.__("Expected expiration date and time")%>：</td>
				<td><form:input id="validdatetime" path="validdatetime" cssStyle="resize: none; width: 150px;" />
					<input type="hidden" value="" id="validdatetimetermfrom" name="validdatetimetermfrom" style="display: none;" />
					<input type="hidden" value="" id="validdatetimetermto" name="validdatetimetermto" style="display: none;" />
					<span id="validdatetimeterm" style="display: none;"></span>
					<span id="validdatetimemsg" style="display: none;">
					<c:choose>
							<c:when test="${pcommonsmediaForm.isShelter()}"><%=lang.__("Only if you have closed all shelter, you can set desired opening end date and time.")%></c:when>
							<c:when test="${pcommonsmediaForm.isRefuge()}"><%=lang.__("Only if you have canceled all evacuation advisory/order, you can set desired opening end date and time.")%></c:when>
							<c:when test="${pcommonsmediaForm.isAntidisaster()}"><%=lang.__("Only if you have dissolved the HQ, you can set desired opening end date and time.")%></c:when>
					</c:choose>
					</span>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:choose>
							<c:when test="${pcommonsmediaForm.isShelter()}">
								<div class="comment" style="height:50px;">
									<span class="bold"><%=lang.__("Attention regarding expected expiration date and time")%></span>
									<br /><%=lang.__("Specify expected expiration date and time of this info.")%>
								</div>
							</c:when>
							<c:when test="${pcommonsmediaForm.isRefuge()}">
								<div class="comment" style="height:50px;">
									<span class="bold"><%=lang.__("Attention regarding expected expiration date and time")%></span>
									<br /><%=lang.__("Specify expected expiration date and time of this info.")%>
								</div>
							</c:when>
							<c:otherwise>
								<div class="comment" style="height:65px;">
									<span class="bold"><%=lang.__("Attention regarding expected expiration date and time")%></span>
									<br /><%=lang.__("1. Specify expiration date and time of the info to be in public. ")%>
									<br /><%=lang.__("2. If no setting, it is specified 48 hour after")%>
								</div>
							</c:otherwise>
					</c:choose>
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
							<form:checkbox path="noticegroupinfoid" value="${f:h(e.id)}"/>${f:h(e.name)}<br />
						</c:forEach>
					</div>
				</td>
				<td valign="bottom"><%=lang.__("* E-mail body (text) equals to the above.")%><br />
				<br /> <%=lang.__("Add delivery target")%>：<form:input path="additionalReceiver" readonly="false" cssStyle="resize: none; width:100%" />
				</td>
			</tr>
		</table>
		<form:hidden path="menuid" />
		<form:hidden path="menutaskid" />
		<form:hidden path="shelter" />
		<form:hidden path="refuge" />
		<form:hidden path="event" />
		<form:hidden path="sendType" />
		<form:hidden path="documentRevision" />
	</form:form>
	<div id="templatemsg" style="display:none"></div>
