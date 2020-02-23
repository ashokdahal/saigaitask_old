<%-- 2017.07.28 kawada ＷＨＥＮ句のみを抽出 --%>
<%-- お知らせ --%>
			<c:when test="${pcommonsmediaForm.isGeneral()}">
				<span class="page_title"><%=lang.__("･Info")%></span>
				<table border="0" style="font-size: 12px; border: 1px; padding: 4px; width:100%">
					<tr>
						<td valign="top" style="width: 40px;"></td>
						<td colspan="2">
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Past info")%>：</td>
									<td width="560px" colspan="2">
										<form:select path="documentId" id="documentId" onchange="changeDocumentId();">
											<form:option value="0"><%=lang.__("* When sending a new info, select this line.")%></form:option>
											<c:forEach var="general" varStatus="s" items="${generalInformationList}">
												<form:option value= "${f:h(general.documentid)}"><c:out value="${f:h(general.title)}" /></form:option>
											</c:forEach>
										</form:select>
									</td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of past info")%></span>
								<br /><%=lang.__("Please specify the target info to update, correct or cancel contents sent before.")%>
							</div>
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Info identification type")%>：</td>
									<td width="560px" colspan="2">
										<form:select path="disasterInformationType">
											<form:option value= "ORDINARY"><%=lang.__("Peacetime info")%></form:option>
											<form:option value= "ACCIDENT"><%=lang.__("Accidents and failure")%></form:option>
											<form:option value= "ALERT"><%=lang.__("Alarms")%></form:option>
											<form:option value= "DISASTER"><%=lang.__("Disaster info")%></form:option>
											<form:option value= "UNKNOWN"><%=lang.__("Unknown")%></form:option>
										</form:select>
									</td>
								</tr>
							</table>
							<br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Classification")%>：</td>
									<td width="560px" colspan="2">
										<form:select path="division">
											<form:option value="1"><%=lang.__("Traffic")%>&nbsp;-&nbsp;<%=lang.__("Train")%></form:option>
											<form:option value="2"><%=lang.__("Traffic")%>&nbsp;-&nbsp;<%=lang.__("Bus")%></form:option>
											<form:option value="3"><%=lang.__("Traffic")%>&nbsp;-&nbsp;<%=lang.__("Airport")%></form:option>
											<form:option value="4"><%=lang.__("Traffic")%>&nbsp;-&nbsp;<%=lang.__("Ship")%></form:option>
											<form:option value="5"><%=lang.__("Traffic")%>&nbsp;-&nbsp;<%=lang.__("Road")%></form:option>
											<form:option value="6"><%=lang.__("Traffic")%>&nbsp;-&nbsp;<%=lang.__("Other")%></form:option>
											<form:option value="7"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Electricity")%></form:option>
											<form:option value="8"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Gas")%></form:option>
											<form:option value="9"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Water supply<!--2-->")%></form:option>
											<form:option value="10"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Water supply")%></form:option>
											<form:option value="11"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Communication")%></form:option>
											<form:option value="12"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Broadcasting")%></form:option>
											<form:option value="13"><%=lang.__("Utility")%>&nbsp;-&nbsp;<%=lang.__("Other")%></form:option>
											<form:option value="14"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Administrative procedure")%></form:option>
											<form:option value="15"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Support victims")%></form:option>
											<form:option value="16"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Welfare, education and childcare")%></form:option>
											<form:option value="17"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Environment")%></form:option>
											<form:option value="18"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Security")%></form:option>
											<form:option value="19"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Health and hygiene")%></form:option>
											<form:option value="20"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Medical care")%></form:option>
											<form:option value="21"><%=lang.__("Living info")%>&nbsp;-&nbsp;<%=lang.__("Other")%></form:option>
											<form:option value="22"><%=lang.__("Public relations")%>&nbsp;-&nbsp;<%=lang.__("Public relations")%></form:option>
											<form:option value="23"><%=lang.__("Tourism and culture")%>&nbsp;-&nbsp;<%=lang.__("Tourism and culture")%></form:option>
											<form:option value="24"><%=lang.__("Tourism and culture")%>&nbsp;-&nbsp;<%=lang.__("Other")%></form:option>
											<form:option value="25"><%=lang.__("Other")%>&nbsp;-&nbsp;<%=lang.__("Other")%></form:option>
										</form:select>
									</td>
								</tr>
							</table>
							<br />
							<table>
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Place<!--2-->")%>：</td>
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
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Title<!--2-->")%>：</td>
									<td width="200px" colspan="2"><form:input path="title" id="title" readonly="false" cssStyle="resize: none; width:100%" /></td>
									<td width="200px"><a class="thickbox " href="${f:url('/template/')}${f:h(pageDto.menuInfo.id)}/${NoticeType.COMMONSMADIA}/?<%=FormUtils.getTokenParam(request)%>&TB_iframe=true&width=920&height=450"><input type="button" value="<%=lang.__("Reference to template")%>" class="ui-button ui-widget ui-state-default ui-corner-all" /></a></td>

								</tr>
							</table>
							<br />
							<table style="width:100%;">
								<tr>
									<td width="110px" rowspan="2" valign="top"><%=lang.__("Text")%>：</td>
									<td colspan="3">
										<form:textarea path="text" id="pcommonsmediaContent" readonly="false" rows="8" cols="60" cssStyle="resize: none; "></form:textarea>
									</td>
								</tr>
							</table>
							<div class="comment" style="height:150px;">
								<span class="bold"><%=lang.__("Notes of text")%></span>
								<br /><%=lang.__("1. Please enter HTML tags after click the source button.")%>
								<br /><%=lang.__("2. Source button clicked again, only plain text will be allowed to enter.")%>
								<br /><%=lang.__("3. HTML tags allowed to be enter")%>
								<br /><%=lang.__("･{0}", "&lt;a&gt; &lt;br&gt; &lt;h2&gt; &lt;dl&gt; &lt;ul&gt; &lt;ol&gt;")%>
								<br /><%=lang.__("･it is only valid in {0}", "&lt;dt&gt; &lt;dd&gt; ※&lt;dl&gt;")%>
								<br /><%=lang.__("･it is only valid in {0}", "&lt;li&gt; ※&lt;ul&gt; &lt;ol&gt;")%>
							</div>
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
							<br />
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Disaster name<!--2-->")%>：</td>
									<td width="540px" colspan="2"><form:input path="trackdataname" readonly="false" cssStyle="resize: none; width:90%" /></td>
								</tr>
							</table>
							<div class="comment" style="height:70px;">
								<span class="bold"><%=lang.__("Notes of disaster name")%></span>
								<br /><%=lang.__("1. Disaster name will be communicated to the L-Alert only if you select the disaster info in the info identification type.")%>
								<br /><%=lang.__("2. It is no problem in the tentative name until determined formal disaster name.")%>
							</div>
						</td>
					</tr>
				</table>
			</c:when>
<%-- 災害対策本部設置状況 --%>
			<c:when test="${pcommonsmediaForm.isAntidisaster()}">
				<span class="page_title"><%=lang.__("･Establish disaster response HQ situation")%></span>
				<table border="0" style="font-size:12px;border:1px;padding:4px;">
					<tr>
						<td valign="top" style="width: 40px;">XML：</td>
						<td colspan="2">
							<table border="1" style="font-size: 12px; border: 1px; padding: 4px;">
								<tr align="center">
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("HQ type")%></td>
									<td width="100px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Last time")%><br /><%=lang.__("Post-to HQ type")%></td>
									<td width="140px" style="background-color: #F0F0F6; padding: 4px;"><%=lang.__("Installation and dissolution date and time")%></td>
								</tr>
									<tr align="center" height="27px">
										<td>${f:h(antidisasterInformationDto.antidisasterKbn)}</td>
										<td>${f:h(antidisasterInformationDto.lastAntidisasterKbn)}</td>
										<td>${f:h(antidisasterInformationDto.hatureiDateTime)}</td>
									</tr>
							</table>
							<div class="comment" style="height:70px;">
								<span class="bold"><%=lang.__("Attentions of establishing disaster response HQ status")%></span>
								<br /><%=lang.__("1. HQ type is determined on the basis of the HQ name.")%>
								<br /><%=lang.__("If HQ type is wrong, change settings system type in admin window.")%>
							</div>
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("HQ name")%>：</td>
									<td width="540px" colspan="2"><form:input path="name" readonly="false" cssStyle="resize: none; width:90%" /></td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of HQ name")%></span>
								<br /><%=lang.__("Please enter if the change of HQ name is required")%>
							</div>
							<table>
								<tr>
									<td width="120px" rowspan="2" valign="top"><%=lang.__("Supplementary info")%>：</td>
									<td width="540px" colspan="2"><form:input path="complementaryInfo" readonly="false" cssStyle="resize: none; width:90%" /><%=lang.__("* Arbitrary")%></td>
								</tr>
							</table>
							<div class="comment" style="height:50px;">
								<span class="bold"><%=lang.__("Notes of supplementary info")%></span>
								<br /><%=lang.__("Enter supplementary explanation in case of system, such as prevention HQ or response HQ, which is lacking in explanation.")%>
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
								<br /><%=lang.__("1. Disaster name will be communicated to the L-Alert as the target of the HQ establishing and dissolution.")%>
								<br /><%=lang.__("2. It is no problem in the tentative name until determined formal disaster name.")%>
							</div>
						</td>
					</tr>
				</table>
			</c:when>
