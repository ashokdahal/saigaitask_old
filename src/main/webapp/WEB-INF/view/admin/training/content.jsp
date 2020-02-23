<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../common/lang_resource.jsp" %>
<fmt:setLocale value="${lang.getLangCode()}" />

<!-- 右手訓練一覧 -->
<div class="rightList" id="planListDiv">
	<!-- <div class="green-white-button" style="margin:10px;">訓練プラン一覧</div> -->
	<div class="table_title"><%=lang.__("Training plan list")%></div>
	<div class="table_button">
		<div class="orange-brown-button" style="margin:10px;" id="newPlanBtn"><%=lang.__("Create new training plan")%></div>
	</div>
	<br style="clear:both" />
	<table border="0" cellpadding="3" cellspacing="2" id="trainingPlanTable" class="tablesorter-green" style="margin-left:10px; width:90%;">
		<thead>
		<tr>
			<c:forEach var="e" varStatus="s" items="${trainingPlanTh}">
				<th>${e}</th>
			</c:forEach>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="e" varStatus="s" items="${trainingPlanList}">
			<tr>
				<td style="padding-top:8px;">
					<c:choose>
						<c:when test="${empty e.localgovInfo.city}">${f:h(e.localgovInfo.pref)}</c:when>
						<c:otherwise>${f:h(e.localgovInfo.city)}</c:otherwise>
					</c:choose>
				</td>
				<%--<td style="padding-top:8px;">${f:h(e.disasterMaster.name)}</td> --%>
				<td style="padding-top:8px;"><span pat="planLists" ids="${f:h(e.id)}">${f:h(e.name)}</span></td>
				<td style="padding-top:8px;"> <fmt:formatDate value="${e.updatetime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM<!--3-->\")%>" /> </td>
				<td style="width:70px; text-align:center;"><div class="white-gray-button planEditBtn" style="padding:5px 13px; margin:0 auto;" planid="${f:h(e.id)}"><%=lang.__("Edit")%></div></td>
				<td style="width:70px; text-align:center;"><div class="white-gray-button planCopyBtn" style="padding:5px 13px; margin:0 auto;" planid="${f:h(e.id)}"><%=lang.__("Trainingplan copy")%></div></td>
				<td style="width:70px; text-align:center;"><div class="white-gray-button planDeleteBtn" style="padding:5px 13px; margin:0 auto;" id="plan_delete_${f:h(e.id)}"><%=lang.__("Delete")%></div></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="table_title" style="margin-top:30px;"><%=lang.__("Past training data list<Self-local gov.>")%></div>
	<br style="clear:both" />
	<table border="0" cellpadding="3" cellspacing="2" id="trainingDataTable" class="tablesorter-blue" style="margin-left:10px; width:90%;">
		<thead>
		<tr>
			<c:forEach var="e" varStatus="s" items="${trainingDataTh}">
				<th>${e}</th>
			</c:forEach>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="e" varStatus="s" items="${trainingDataList}">
			<tr>
				<%--<td>${f:h(e.disasterMaster.name)}</td>--%>
				<td>${f:h(e.name)}</td>
				<td> <fmt:formatDate value="${e.endtime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM<!--3-->\")%>"/> </td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<!-- 右手訓練プラン基本設定 -->
<div class="rightList" id="planBasicDiv" style="margin-left:10px; width:80%; text-align:center; margin:0 auto; display:none;">
	<div class="page_title"><%=lang.__("Training plan basis settings")%></div>
	<div class="basicLeftDiv"> <%=lang.__("Training name")%> </div>

	<c:choose>
		<%-- 新規登録モード --%>
		<c:when test="${trainingplanForm.trainingplanData == null}">
			<div class="basicRightDiv">
				<input type="text" id="planName" name="planName" value="">
			</div>
			<br style="clear:both;">
			<%-- <div class="basicLeftDiv"> <%=lang.__("Disaster type")%> </div>
			<div class="basicRightDiv">
				<select id="planDisaster" name="planDisaster">
					<c:forEach var="e" varStatus="s" items="${disasterMasterList}">
						<option value="${f:h(e.id)}">${f:h(e.name)}</option>>
					</c:forEach>
				</select>
			</div>--%>
			<br style="clear:both;">
			<div class="basicLeftDiv"> <%=lang.__("Plan overview")%> </div>
			<div class="basicRightDiv">
				<textarea id="planNote"></textarea>
			</div>
			<br style="clear:both;">
			<div class="basicLeftDiv"> <%=lang.__("Notification limitation")%> </div>
			<div class="basicRightDiv">
				<table border="0" cellpadding="3" cellspacing="2" id="trainingNoticeTable" class="tablesorter-green" style="width:90%;">
					<thead>
						<tr>
							<th style="width:45%;"><%=lang.__("Notification function name")%></th>
							<th><%=lang.__("Usable or not")%></th>
						</tr>
					</thead>
					<tbody>
						<tr class="odd">
							<td class="textCenter"><%=lang.__("L-Alert")%></td>
							<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="commonsBtn">ON</div></td>
						</tr>
						<tr class="even">
							<td class="textCenter">Facebook</td>
							<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="facebookBtn">ON</div></td>
						</tr>
						<tr class="odd">
							<td class="textCenter">Twitter</td>
							<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="twitterBtn">ON</div></td>
						</tr>
						<tr class="even">
							<td class="textCenter"><%=lang.__("e-Com GW")%></td>
							<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="ecomGWBtn">ON</div></td>
						</tr>
					</tbody>
				</table>
			</div>
			<br style="clear:both;">
			<div class="basicLeftDiv"> <%=lang.__("Cooperation local gov.")%> </div>
			<div class="basicRightDiv">
				<table border="0" cellpadding="3" cellspacing="2" class="tablesorter-blue" style="width:90%;">
					<thead>
						<tr>
							<th style="width:45%;"><%=lang.__("Local gov. name")%></th>
							<th><%=lang.__("Cooperation ON/OFF")%></th>
						</tr>
					</thead>
					<tbody id="localgovLink">
						<c:forEach var="e" varStatus="s" items="${cityLocalgovInfos}">
							<c:choose>
								<c:when test="${s.index %2 == 0}">
									<tr class="odd"><td class="textCenter">
								</c:when>
								<c:otherwise>
									<tr class="even"><td class="textCenter">
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${empty e.city}">${f:h(e.pref)}</c:when>
								<c:otherwise>${f:h(e.city)}</c:otherwise>
							</c:choose>
							</td>
							<td class="textLeft"><div class="orange-brown-button on-off-btn link-btn" style="padding:5px 18px;" id="localgovBtn_${f:h(e.id)}">ON</div></td></tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<input type="hidden" name="planid" value="0">
		</c:when>

		<%-- 編集モード --%>
		<c:otherwise>
			<div class="basicRightDiv">
				<input type="text" id="planName" name="planName" value="${f:h(trainingplanForm.trainingplanData.name)}">
			</div>
			<br style="clear:both;">
			<%--<div class="basicLeftDiv"> <%=lang.__("Disaster type")%> </div>--%>
			<%-- <div class="basicRightDiv">
			${f:h(trainingplanForm.trainingplanData.disasterid)}
				<select id="planDisaster" name="planDisaster">
					<c:forEach var="e" varStatus="s" items="${disasterMasterList}">
						<!-- ${f:h(e.id)} -->
						<option value="${f:h(e.id)}" <c:if test="${ e.id == trainingplanForm.trainingplanData.disasterid }">selected</c:if> >${f:h(e.name)}</option>>
					</c:forEach>
				</select>
			</div>--%>
			<br style="clear:both;">
			<div class="basicLeftDiv"> <%=lang.__("Plan overview")%> </div>
			<div class="basicRightDiv">
				<textarea id="planNote">${f:h(trainingplanForm.trainingplanData.note)}</textarea>
			</div>
			<br style="clear:both;">
			<div class="basicLeftDiv"> <%=lang.__("Notification limitation")%> </div>
			<div class="basicRightDiv">
				<table border="0" cellpadding="3" cellspacing="2" id="trainingNoticeTable" class="tablesorter-green" style="width:90%;">
					<thead>
						<tr>
							<th style="width:45%;"><%=lang.__("Notification function name")%></th>
							<th><%=lang.__("Usable or not")%></th>
						</tr>
					</thead>
					<tbody>
						<tr class="odd">
							<td class="textCenter"><%=lang.__("L-Alert")%></td>
							<c:choose>
								<c:when test="${trainingplanForm.trainingplanData.publiccommonsflag}">
									<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="commonsBtn">ON</div></td>
								</c:when>
								<c:otherwise>
									<td class="textRight"><div class="white-gray-button on-off-btn" style="padding:5px 18px;" id="commonsBtn">OFF</div></td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr class="even">
							<td class="textCenter">Facebook</td>
							<c:choose>
								<c:when test="${trainingplanForm.trainingplanData.facebookflag}">
									<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="facebookBtn">ON</div></td>
								</c:when>
								<c:otherwise>
									<td class="textRight"><div class="white-gray-button on-off-btn" style="padding:5px 18px;" id="facebookBtn">OFF</div></td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr class="odd">
							<td class="textCenter">Twitter</td>
							<c:choose>
								<c:when test="${trainingplanForm.trainingplanData.twitterflag}">
									<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="twitterBtn">ON</div></td>
								</c:when>
								<c:otherwise>
									<td class="textRight"><div class="white-gray-button on-off-btn" style="padding:5px 18px;" id="twitterBtn">OFF</div></td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr class="even">
							<td class="textCenter"><%=lang.__("e-Com GW")%></td>
							<c:choose>
								<c:when test="${trainingplanForm.trainingplanData.ecommapgwflag}">
									<td class="textLeft"><div class="orange-brown-button on-off-btn" style="padding:5px 18px;" id="ecomGWBtn">ON</div></td>
								</c:when>
								<c:otherwise>
									<td class="textRight"><div class="white-gray-button on-off-btn" style="padding:5px 18px;" id="ecomGWBtn">OFF</div></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</tbody>
				</table>
			</div>
			<br style="clear:both;">
			<div class="basicLeftDiv"> <%=lang.__("Cooperation local gov.")%> </div>
			<div class="basicRightDiv">
				<table border="0" cellpadding="3" cellspacing="2" class="tablesorter-blue" style="width:90%;">
					<thead>
						<tr>
							<th style="width:45%;"><%=lang.__("Local gov. name")%></th>
							<th><%=lang.__("Cooperation ON/OFF")%></th>
						</tr>
					</thead>
					<tbody id="localgovLink">
						<c:forEach var="e" varStatus="s" items="${trainingplanLinkClassList}">
							<c:choose>
								<c:when test="${s.index %2 == 0}">
									<tr class="odd"><td class="textCenter">
								</c:when>
								<c:otherwise>
									<tr class="even"><td class="textCenter">
								</c:otherwise>
							</c:choose>
							${ e.localgovname }</td>
							<c:choose>
								<c:when test="${ e.flags }">
									<td class="textLeft"><div class="orange-brown-button on-off-btn link-btn" style="padding:5px 18px;" id="localgovBtn_${f:h(e.localgovinfoid)}">ON</div></td></tr>
								</c:when>
								<c:otherwise>
									<td class="textRight"><div class="white-gray-button on-off-btn link-btn" style="padding:5px 18px;" id="localgovBtn_${f:h(e.localgovinfoid)}">OFF</div></td></tr>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<input type="hidden" name="planid" value=${f:h(training_planid)}>
			<input type="hidden" name="training_Listtype" value=${f:h(training_Listtype)}>
			<input type="hidden" name="copyTrainingplanId" value="">
		</c:otherwise>
	</c:choose>
	<br style="clear:both;" />
	<div style="width:60%; margin:10px auto 0px auto;">
		<div class="white-red-button" style="width:30%; float:left; margin-right:10px;" id="planBasicSaveBtn"><%=lang.__("Save<!--2-->")%></div>
		<div class="black-lightblue-button" style="width:30%; float:left;" id="planBasicCancelBtn"><%=lang.__("Cancel<!--3-->")%></div>
		<br style="clear:both;" />
	</div>
</div>

<!-- 右手外部データ設定 -->
<div class="rightList" id="planExternalDataDiv" style="margin-left:10px; width:90%; text-align:center; margin:0 auto; display:none;">
	<div class="page_title"><%=lang.__("Training plan external data settings")%></div>
	<div class="table_title"></div>
	<div class="table_button" style="width:53%;">
		<div class="orange-brown-button" style="margin:10px;" id="newExternalDataBtn"><%=lang.__("Register new external data")%></div>
	</div>
	<br style="clear:both" />
	<table border="0" cellpadding="3" cellspacing="2" id="trainingExternalDataTable" class="tablesorter-green" style="width:100%;">
		<thead>
		<tr>
			<c:forEach var="e" varStatus="s" items="${trainingExternalDataTh}">
				<th>${e}</th>
			</c:forEach>
				<th style="width:80px;"><%=lang.__("Display")%></th>
				<th style="width:80px;"><%=lang.__("Edit")%></th>
				<th style="width:80px;"><%=lang.__("Delete")%></th>
		</tr>
		</thead>
		<tbody id="trainingExternalTbody">
			<c:forEach var="e" varStatus="s" items="${ trainingMeteoList }">
				<tr id="meteo_data${ f:h(e.id) }">
					<td id="meteo_name${ f:h(e.id) }">${ f:h(e.name) }</td>
					<td id="meteo_type${ f:h(e.id) }">${ f:h(e.meteotypeMaster.name) }</td>
					<td id="meteo_note${ f:h(e.id) }">${ f:h(e.note) }</td>
					<td> <fmt:formatDate value="${e.updatetime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM<!--3-->\")%>"/> </td>
					<td style="text-align:center">
						<div class="white-gray-button meteoDispBtn" style="padding:4px 14px;" id="meteo_disp${ f:h(e.id) }" meteoURL="${f:url('/upload')}${ f:h(e.meteourl) }"><%=lang.__("Display")%></div>
					</td>
					<td style="text-align:center">
						<div class="white-gray-button meteoEditBtn" style="padding:4px 14px;" id="meteo_edit${ f:h(e.id) }" meteotype="${ f:h(e.meteotypeid) }" meteoURL="${f:url('/upload')}${ f:h(e.meteourl) }"><%=lang.__("Edit")%></div>
					</td>
					<td style="text-align:center">
						<div class="white-gray-button meteoDeleteBtn" style="padding:4px 14px;" id="meteo_delete${ f:h(e.id) }" meteoURL="${f:url('/upload')}${ f:h(e.meteourl) }"><%=lang.__("Delete")%></div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<!-- 右手訓練制御 -->
<div class="rightList" id="planControlDiv" style="margin-left:10px; width:90%; margin:0 auto; display:none;">
	<div class="table_title" style="text-align:left;"><%=lang.__("Shift training mode<!--2-->")%></div>
	<br style="clear:both" />
	<table border="0" cellpadding="3" cellspacing="2" class="tablesorter-blue" style="width:90%;">
		<thead>
		<tr>
			<c:forEach var="e" varStatus="s" items="${planControlTh}">
				<th style="width:33%;">${e}</th>
			</c:forEach>
		</tr>
		</thead>
		<tbody id="localgovLinkControl">
			<c:forEach var="e2" varStatus="s2" items="${trainingplanLinkClassList}">
				<c:choose>
					<%-- 連携ONの自治体のみ表示する --%>
					<c:when test="${ e2.flags }">
						<tr>
							<td class="textCenter">${ e2.localgovname }</td>
							<td class="textCenter" id="trainingMode_${ e2.localgovinfoid }">${f:h(e2.mode)}</td>
							<td class="textCenter">
								<c:if test="${!e2.saigaiFlag}">
								<div class="orange-brown-button trainingBtn" style="padding:5px 18px;" id="controlBtn_${f:h(e2.localgovinfoid)}"><%=lang.__("Shift training mode")%></div>
								</c:if>
							</td>
						</tr>
					</c:when>
				</c:choose>
			</c:forEach>
		</tbody>
	</table>
	<div class="white-red-button" style="width:90%; text-align:center;" id="trainingControlBtnAll"><%=lang.__("Shift to training mode in bulk.")%></div>

	<c:if test="${fn:length(trainingMeteoList) > 0}">
		<div class="table_title" style="margin-top:30px; text-align:left;"><%=lang.__("Perform sending external data")%></div>
	</c:if>
	<br style="clear:both" />
	<!-- 外部データ配信実施 -->
	<div id="externalControlWindow">
		<c:forEach var="e" varStatus="s" items="${ trainingMeteoList }">
			<div class="table_title" style="margin-top:8px; text-align:left; font-size:14px;">${ f:h(e.name) }</div>
			<br style="clear:both" />
			<table border="0" cellpadding="3" cellspacing="2" class="tablesorter-green" style="width:90%;">
				<thead>
					<tr>
						<th style="width:33%;"><%=lang.__("Local gov. name")%></th>
						<th style="width:33%;"><%=lang.__("Delivery execution time")%></th>
						<th style="width:33%;"><%=lang.__("Delivery completion")%></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="e2" varStatus="s2" items="${trainingplanLinkClassList}">
						<c:choose>
							<%-- 連携ONの自治体のみ表示する --%>
							<c:when test="${ e2.flags && e2.mode==lang.__('Undergoing training') }">
								<tr class="odd">
									<td class="textCenter">${ f:h(e2.localgovname) }</td>
									<td class="textCenter sendExternalTime_${ f:h(e.id) }_${ f:h(e2.localgovinfoid) }"> <fmt:formatDate value="${e2.updatetime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM<!--3-->\")%>"/> </td>
									<td class="textCenter">
										<div class="orange-brown-button externalBtn" style="padding:5px 18px;" onclick="sendExternalFile(${ f:h(e.id) },${ f:h(e2.localgovinfoid) });"><%=lang.__("Delivery completion")%></div>
									</td>
								</tr>
							</c:when>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
			<div class="white-red-button" style="width:90%; text-align:center;" id="externalControlBtn_all_${ f:h(e.id) }" onclick="sendExternalFileAll( ${ f:h(e.id) });"><%=lang.__("Deliver in bulk")%></div>
		</c:forEach>
	</div>
</div>

<!-- 防災情報XML編集画面 -->
<div class="rightList" id="xmlEditorDiv">
</div>


<!-- 外部データ登録用ダイアログ -->
<div id="external-dialog" style="display:none; height:70%; width:75%;">
	<% FormUtils.printToken(out, request); %>
	<form:form id="external-dialog-form" method="POST" servletRelativeAction="updateExternalFile" enctype='multipart/form-data' target="fileframe" modelAttribute="trainingplanForm">
		<div class="basicLeftDiv"> <%=lang.__("XML registration name")%> </div>
		<div class="basicRightDiv" style="padding-top:2px;"> <input type="text" id="meteoName" name="meteoName"> </div>
		<br style="clear:both;">
		<div class="basicLeftDiv"> <%=lang.__("Overview")%> </div>
		<div class="basicRightDiv" style="padding-top:2px;"> <textarea name="meteoNote" style="width:90%; height:100px;"></textarea> </div>
		<br style="clear:both;">
		<div class="basicLeftDiv"> <%=lang.__("Weather info type")%> </div>
		<div class="basicRightDiv" style="padding-top:2px;">
			<select name="meteoType" id="meteoType">
			<c:forEach var="e" varStatus="s" items="${trainingplanForm.meteoTypeMasterList}">
				<option value="${e.id}">${e.name}</option>
			</c:forEach>
			</select>
		</div>
		<br style="clear:both;">
		<div class="basicLeftDiv checkMeteoRow"> </div>
		<div class="basicRightDiv checkMeteoRow" id="checkMeteoDataMessage" style="padding-top:6px; color:#FF0000;"></div>
		<br class="checkMeteoRow" style="clear:both;">
		<div class="basicLeftDiv"> <%=lang.__("XML file registry")%> </div>
		<div class="basicRightDiv" style="padding-top:6px;">
			<input type="radio" name="formFile_choice_source" value = "1" checked="checked"><%=lang.__("Local") %>
			<input type="radio" name="formFile_choice_source" value = "2"><%=lang.__("Server") %>
			<div id="formFile_source_field">
				<input type="file" name="formFile_external_xml">
			</div>
		</div>
		<br style="clear:both;">
		<div class="basicLeftDiv"></div>
		<div class="basicRightDiv" style="padding-top:6px;" id="meteo_url_filename"></div>
		<br style="clear:both;">
		<input type="hidden" id="meteoid" name="meteoid" value="">
	</form:form>
</div>

<!-- サーバ上防災情報XMLファイル選択用ダイアログ -->
<div id="external-bousaixmlfiles-dialog" style="display:none; height:70%; width:75%;">
	<div id="external-bousaixmlfiles-dialog-content">
	</div>
</div>


<!-- 確認ダイアログ -->
<div id="alert-dialog" style="display:none; padding:5px;">
	<p id="alert-dialog-message" style="text-align:center; margin-top:5px;"></p>
	<div id="alert-dialog-body" style="margin-top:15px;">
	</div>
</div>

<!-- ファイル送信用 -->
<iframe style="width:0; height:0; border:none;" id="fileframe" name="fileframe"></iframe>



