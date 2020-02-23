<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/lang_resource.jsp" %>
<!DOCTYPE HTML>
<fmt:setLocale value="${lang.getLangCode()}" />
<html>
<head>
<meta charset="utf-8">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<title><%=lang.__("Register disaster")%></title>
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/jquery-ui.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.css" />
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.min.js"></script>
<c:set var="winHeight" value="450"/>
<style type="text/css">
#container {
/*  margin-left: auto;  margin-right: auto; /* centering */
  width: 95%;
  height: ${winHeight-20}px;
}
table.table02 {
  margin-top: 0.2em;  margin-bottom: 0em;
  margin-left: auto;  margin-right: auto; /* centering */
/*  width:440px;*/
  width: 95%;
  border-collapse: collapse;
  border: solid 1px #999;
  font-size: 12px;
}

table.table02 caption {
  margin-top: 1em;
  text-align: left;
}

table.table02 th,
table.table02 td {
  border: solid 1px #999;
  padding: 4px 6px;
}

table.table02 th {
  background: #E6E6E6;
  text-align: center;
  white-space: nowrap;
  color: #666;
    font-weight: bolder;
}

table.table02 td.head {
	  background: #E6E6E6;
  text-align: center;
  font-size: 100%;
  font-weight: bolder;
}

.ui-tabs .ui-tabs-panel {
  padding: 0px;
  overflow: auto;
/*  height: 280px;*/
  height: ${winHeight-20-50}px;
}
div.pcommonsmessages{
	font-size: 12px;
	color: red;
	padding: 15px;
}
div.pcommonsmessages li{
	margin-bottom: 10px;
}
</style>
<script type="text/javascript">
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
});

$(function(){
	// ボタンの初期化
	var headerButton = $(".header-button");
	// ボタン化
	//headerButton.button();
	headerButton.css("padding", "2px");
	headerButton.css("cursor", "pointer");

	// タブメニュー
	$("#container").tabs().tabs("refresh");

	// 過去の災害の初期化
	var oldtrackidSelect = $("select[name='oldtrackid']");
	var selectButton = $("#select-button");
	var deleteButton = $("#delete-button");
	oldtrackidSelect.change(function() {
		var val = oldtrackidSelect.val();
		if(val==0) {
			selectButton.attr("disabled", "disabled");
			deleteButton.attr("disabled", "disabled");
		}
		else {
			selectButton.removeAttr("disabled");
			deleteButton.removeAttr("disabled");
		}
	});
	oldtrackidSelect.change();

	// タブメニュー
	$("#disastergroup").tabs().tabs("refresh");

});

function regist(msg) {
	if (!confirm(msg))
		return false;

	$('#container').mask("Loading...");
	return true;
}

/**
 * 災害終了時の処理を実行（リセット処理が必要か確認して、不要の場合に呼び出される）
 */
function trackComplete(){
	$('#container').mask("Loading...");
	//var urls = "${f:url('/track/')}resetComplete";
	var urls = "${f:url('/track/')}resetExecute";
	var forms = $("<form>").attr({"action":urls, "method":"POST", "target":"_self"}).appendTo($("body"));
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$('<input>').attr({"type":"hidden","name":"_csrf","value":''+token+''}).appendTo(forms);
	$('<input>').attr({"type":"hidden","name":"_csrf_header","value":''+header+''}).appendTo(forms);
	forms.submit();
}

function openSelectCitytrackdataDialog(option) {
	var me = this;
	var tabDiv = $("#"+option.type+"-tab");
	var tr = $("#selected-"+option.recordId, tabDiv);
	var selectedCitytrackdataid = !!option.selectedCitytrackdataid ? option.selectedCitytrackdataid : null;
	// 現在選択中の市町村の記録データID
	var citytrackdataid = $("input[name='citytrackdataids']", tr).val();
	var cityTd = $(".city", tr)
	var disasterNameTd = $(".disasterName", tr)
	var startendtimeTd = $(".startendtime", tr);
	var selectTrackData = function(trackData) {
		if(trackData==null) {
			disasterNameTd.html("―");
			startendtimeTd.html("―");
		}
		else {
			disasterNameTd.html(trackData.name);
			var citytrackdataidsInput = $("<input type='hidden' name='citytrackdataids'/>");
			citytrackdataidsInput.val(trackData.id);
			disasterNameTd.prepend(citytrackdataidsInput);
			var startendtime = trackData.starttime+" <%=lang.__("Start")%>";
			if(trackData.endtime!=null) startendtime += "<br/>"+trackData.endtime+" <%=lang.__("End")%>";
			startendtimeTd.html(startendtime);
		}
	};
	var container = $("#container");
	var dialog = $("#dialog");
	container.mask("Loading...");
	var requestData = {
		localgovinfoid: option.citylocalgovinfoid
	};
	if(selectedCitytrackdataid!=null) requestData.citytrackdataids = selectedCitytrackdataid;
	$.ajax("${f:url('/track/selectCitytrackDialogContent')}", {
		async: true,
		dataType: "json",
		data: requestData,
		cache: false,
		success: function(data, textStatus, jqXHR) {

			var table = $("table", dialog);
			// 削除
			$("tr.record", table).remove();

			for(var idx in data) {
				var tr = $("<tr class='record'>");
				var trackData = data[idx];
				(function(trackData) {
					//tr.append("<td>"+trackData.disasterMaster.name+"</td>");
					tr.append("<td>"+trackData.name+"</td>");
					var startendtime = trackData.starttime+" <%=lang.__("Start")%>";
					if(trackData.endtime!=null) startendtime += "<br/>"+trackData.endtime+" <%=lang.__("End")%>";
					tr.append("<td>"+startendtime+"</td>");
					var selectBtn = $("<input type='button' value='<%=lang.__("Select")%>'/>");
					selectBtn.click(function() {
						selectTrackData(trackData);
						dialog.dialog("close");
					});
					tr.append($("<td>").append(selectBtn));
					if(citytrackdataid==trackData.id) {
						$("td", tr).css("background-Color", "yellow");
					}
					table.append(tr);
				})(trackData);
			}

			dialog.dialog({
				modal: true,
				title: MessageFormat.format("<%=lang.__("Select disaster of {0}.")%>", cityTd.text()),
				width: container.width(),
				height: container.height(),
				position: {
					of: container
				},
				open: function() {
				},
				buttons: [{
					text: "<%=lang.__("Deselect")%>",
					click: function() {
						selectTrackData(null);
						$(this).dialog("close");
					}
				}, {
					text: "<%=lang.__("Cancel")%>",
					click: function() {
						$(this).dialog("close");
					}
				}]
			});

			container.unmask();
		},
		error: function(jqXHR, status, errorThrown) {
			container.unmask();
			if(jqXHR.status!=0) {
				alert("<%=lang.__("Failed to get data.")%>"+me.errorMsg(jqXHR));
			}
			console.error(errorThrown);
		}
	});
}
</script>
</head>
<body>

<!-- 連携自治体の災害選択ダイアログ -->
<div id="dialog" style="display:none;">
    <table class="table02">
      <tr>
        <%--<td class="head"><%=lang.__("Disaster type")%></td> --%>
        <td class="head"><%=lang.__("Disaster name")%></td>
        <td class="head"><%=lang.__("Start/end time")%></td>
        <td class="head" width="40px"><%=lang.__("Select")%></td>
      </tr>
    </table>
</div>

<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
<c:set var="disableMultiDisaster" value="false"/> <%-- 複数同時災害の無効フラグ --%>
<c:set var="exist" value="${0<fn:length(currentTrackItems)}"/>

<div id="container" style="float:center;">
  <c:set var="showUpdate" value="${!(trackForm.id == 0 || trackForm.id == '')}" />
  <!-- タブリスト -->
  <ul>
    <c:if test="${showUpdate}">
    <li><a href="#update-tab"><%=lang.__("Update disaster")%></a></li>
    </c:if>

    <c:if test="${groupInfo.headoffice &&(disableMultiDisaster==false||exist==false)}">
    <li><a href="#insert-tab"><%=lang.__("Resister disaster")%></a></li>
    </c:if>

    <li><a href="#select-tab"><%=lang.__("Disaster list")%></a></li>
  </ul>

  <!-- 災害の登録/更新タブ -->
  <c:set var="types" value="update"/>
  <c:if test="${groupInfo.headoffice}"><c:set var="types" value="insert,update"/></c:if>
  <c:forEach var="type" items="insert,update">
  <c:if test="${(type=='insert'&&(groupInfo.headoffice &&(disableMultiDisaster==false||exist==false))) || (type=='update'&&showUpdate)}">
  <div id="${type}-tab">
  <c:if test="${type=='insert'}">
  	<c:if test="${exist}">
  		<c:choose>
  			<c:when test="${trackForm.trainingplandataid==null}">
  				<span style="color:red; font-size:15px;"><%=lang.__("* There is an incomplete disaster.")%><br/>
  				　<%=lang.__("This disaster will be added to the incomplete disaster.")%></span>
  			</c:when>
  			<c:otherwise>
  				<span style="color:red; font-size:15px;"><%=lang.__("* Now training..")%><br/>
  				　<%=lang.__("When you perform the registration of the disaster, the current training will end automatically.")%></span>
  			</c:otherwise>
  	</c:choose>
  	</c:if>
  </c:if>
  <form:form cssClass="${type}" modelAttribute="trackForm">
    <%-- <% FormUtils.printToken(out, request); %> --%>

    <form:hidden path="id"/>
    <form:hidden path="localgovinfoid"/>
    <form:hidden path="trackmapinfoid"/>
    <form:hidden path="demoinfoid"/>
    <form:hidden path="trainingplandataid"/>
    <form:hidden path="starttime"/>
    <form:hidden path="endtime"/>
    <form:hidden path="deleted"/>
    <form:hidden path="ref"/>
    <table class="table02">
      <tr>
        <td class="head"><%=lang.__("Disaster name")%></td>
        <td><form:input path="name" cssStyle="width:90%;" value="${type=='insert'?'':(f:h(name))}"/></td>
      </tr>
      <%--<tr>
        <td class="head"><%=lang.__("Disaster type")%></td>
        <td>
          <form:select path="disasterid" cssClass="styledselect" cssStyle="width:90%;">
            <form:option value=""> <%=lang.__("Select.")%> </form:option>
            <c:forEach var="e" varStatus="s" items="${disasterItems}">
              <form:option value="${e.id}"> ${f:h(e.name)} </form:option>
            </c:forEach>
          </form:select>
        </td>
      </tr> --%>
      <tr>
        <td class="head"><%=lang.__("Notes")%></td>
        <td><form:textarea path="note" rows="3" cols="30" cssStyle="width:90%;" value="${type=='insert'?'':(f:h(note))}"></form:textarea></td>
      </tr>
    </table>

  <c:if test="${0<fn:length(cityLocalgovInfos)}">
  <!-- 災害グループ -->
  <c:set var="hiddenEndtime" value="true"></c:set>
    <table class="table02">
      <tr>
        <td class="head" colspan="6"><%=lang.__("Disaster group")%></td>
      </tr>
      <tr>
        <td class="head city"><%=lang.__("City name<!--2-->")%></td>
        <td class="head disasterName"><%=lang.__("Disaster name")%></td>
        <td class="head startendtime"><%=lang.__("Start/end time")%></td>
        <c:if test="${groupInfo.headoffice}">
        <td class="head" width="40px"><%=lang.__("Change")%></td>
        </c:if>
      </tr>
      <%-- 連携自治体の記録データ --%>
      <c:set var="recordId" value="1"/>
      <c:forEach var="cityLocalgovInfo" varStatus="s1" items="${cityLocalgovInfos}">
      <c:choose>
      <c:when test="${type=='insert'||fn:length(cityLocalgovInfo.trackDatas)==0}">
        <tr id="selected-${recordId}">
          <td class="city">
			<c:choose>
			<c:when test="${cityLocalgovInfo.localgovtypeid==1}">
				${f:h(cityLocalgovInfo.pref)}
			</c:when>
			<c:when test="${cityLocalgovInfo.localgovtypeid==2}">
				${f:h(cityLocalgovInfo.city)}
			</c:when>
			<c:when test="${cityLocalgovInfo.localgovtypeid==3}">
				${f:h(cityLocalgovInfo.section)}
			</c:when>
			</c:choose>
          </td>
          <td class="disasterName">―</td>
          <td class="startendtime">―</td>
          <td><input type="button" value="<%=lang.__("Change")%>" onclick="openSelectCitytrackdataDialog({type: '${type}', citylocalgovinfoid: ${cityLocalgovInfo.id}, recordId: ${recordId}})"/></td>
        </tr>
        <c:set var="recordId" value="${recordId+1}"/>
      </c:when>
      <c:otherwise>
        <c:forEach var="cityTrackData" varStatus="s2" items="${cityLocalgovInfo.trackDatas}">
        <tr id="selected-${recordId}">
          <td class="city">
			<c:choose>
			<c:when test="${cityLocalgovInfo.localgovtypeid==1}">
				${f:h(cityLocalgovInfo.pref)}
			</c:when>
			<c:when test="${cityLocalgovInfo.localgovtypeid==2}">
				${f:h(cityLocalgovInfo.city)}
			</c:when>
			<c:when test="${cityLocalgovInfo.localgovtypeid==3}">
				${f:h(cityLocalgovInfo.section)}
			</c:when>
			</c:choose>
          </td>
          <td class="disasterName"><input type="hidden" name="citytrackdataids" value="${cityTrackData.id}" />${fn:length(cityTrackData.name)==0?lang.__('No disaster name'):f:h(cityTrackData.name)}</td>
          <td class="startendtime"><fmt:formatDate value="${cityTrackData.starttime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM 'start'\")%>"/>
              ${cityTrackData.endtime!=null?'<br/>':''}<fmt:formatDate value="${cityTrackData.endtime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM 'end'\")%>"/>
          </td>
          <c:if test="${groupInfo.headoffice}">
          <td><input type="button" value="<%=lang.__("Change")%>" onclick="openSelectCitytrackdataDialog({type: '${type}', citylocalgovinfoid: ${cityLocalgovInfo.id}, recordId: ${recordId}, selectedCitytrackdataid: ${cityTrackData.id}})"/></td>
          </c:if>
        </tr>
        <c:set var="recordId" value="${recordId+1}"/>
        </c:forEach>
      </c:otherwise>
      </c:choose>
      </c:forEach>
      <tr>
        <td colspan="6"><input type="checkbox" name="overrideTrackDataName" value="true" id="${type}-ck" /><label for="${type}-ck"><%=lang.__("Unify and notify disaster name ")%></label></td>
      </tr>
    </table>
    </c:if>

	<c:if test="${0<fn:length(pcommonsMessages)}">
		<div class="pcommonsmessages">
			<ul>
				<c:forEach var="row" varStatus="s" items="${pcommonsMessages}">
					<li>${f:h(row)}</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
    <table border="0" class="table02">
      <tr>
        <td align="center">
          <c:choose>
          <c:when test="${groupInfo.headoffice}">
            <c:set var="trackOrTraining" value="${ trainingplandataid==null?lang.__('Disaster response'):lang.__('Training') }"/>
            <c:choose>
            <c:when test="${type=='insert'}">
              <c:if test="${ empty(trainingplandataid) || trainingplandataid == 0 }">
                <input type="submit" name="insert" value="${exist?lang.__('Add<!--2-->'):lang.__('Register<!--2-->')}" onclick="return regist('${exist?lang._E('Are you sure to add this disaster to incomplete disasters?'):lang.__('Do you want to start the disaster response?')}');">
                <div style="display:none;"><form:checkbox path="copyMap" value="true"></form:checkbox><%=lang.__("Copy map")%><br/></div>
              </c:if>
              <%-- 訓練モード時 --%>
              <c:if test="${ trainingplandataid > 0 }">
                <input type="submit" name="insert" value="<%=lang.__("Register<!--2-->")%>" onclick="return regist('<%=lang.__("Do you want to start the disaster response after the end of the current training?")%>');">
              </c:if>
            </c:when>
            <c:when test="${type=='update'}">
              <input type="submit" name="update" value="<%=lang.__("Save the changes")%>" class="header-button">
              <input type="button" name="completebutton" value="${ trackOrTraining }${endtime!=null?lang.__('Completed'):lang.__(' complete')}" onclick="return self.parent.resetLayer('${ trackOrTraining }<%=lang.__(" finish?")%>');" class="header-button" ${endtime!=null?'disabled="disabled"':''}/>
            </c:when>
            </c:choose>
          </c:when>
          <c:otherwise>
            <input type="submit" value="<%=lang.__("Close")%>" onClick="self.parent.tb_remove(); return false;" class="header-button">
          </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </table>
  </form:form>
  </div>
  </c:if>
  </c:forEach>

<!-- 過去の災害タブのコンテンツ -->
	<div id="select-tab">
	<form:form modelAttribute="trackForm">
    <% FormUtils.printToken(out, request); %>
	<form:hidden path="oldtrackid"/>
    <table class="table02" style="width:550px; margin-left: 5px;">
      <tr>
        <!-- <td class="head" colspan="2"><%=lang.__("Disaster type")%></td>-->
        <td class="head" colspan="2"><%=lang.__("Disaster name")%></td>
        <td class="head"><%=lang.__("Start/end time")%></td>
        <c:if test="${0<fn:length(cityLocalgovInfos)}">
        <td class="head"><%=lang.__("Disaster group")%></td>
        </c:if>
        <c:if test="${groupInfo.headoffice}">
        <td class="head" width="40px"><%=lang.__("Delete")%></td>
        </c:if>
      </tr>
<%-- 訓練データは背景色を変更して視覚的に分かるようにする --%>
<c:set var="trainingTDColor" value="style='background-color:#ccccff;'" />
<%-- 過去の記録データ --%>
<%-- セルの結合をしているため、並び替えは対応しない(デフォルト、開始日降順) --%>
<c:forEach var="trackmapInfo" varStatus="s" items="${trackmapInfos}">
  <c:set var="trackDataNum" value="${fn:length(trackmapInfo.trackDatas)}"/>
  <c:if test="${0<trackDataNum}">
      <c:forEach var="e" varStatus="s2" items="${trackmapInfo.trackDatas}">
      <tr>
        <%--<td colspan="${trackDataNum==1?2:1}" ${ e.trainingplandataid!=null?trainingTDColor:"" }><c:forEach var="disasterMaster" items="${disasterItems}">
        <c:if test="${disasterMaster.id==e.disasterid}">${f:h(disasterMaster.name)}</c:if>
        </c:forEach></td> --%>
        <c:choose>
        <c:when test="${1<trackDataNum}">
          <c:if test="${s2.index==0}"><td rowspan="${trackDataNum}"><%=lang.__("Simultaneous")%><br/><%=lang.__("Disaster")%></td></c:if>
          <td ${ e.trainingplandataid!=null?trainingTDColor:"" } colspan="1">
        </c:when>
        <c:otherwise>
          <td ${ e.trainingplandataid!=null?trainingTDColor:"" } colspan="2">
        </c:otherwise>
        </c:choose>
          <input id="select-button" type="submit" name="select" value="${fn:length(e.name)==0?lang.__('No disaster name'):f:h(e.name)}" ${e.id==id ? 'disabled="disabled"' : ''}
           class="header-button" onclick="$('input[name=oldtrackid]').val(${e.id})"/>
        </td>
        <td ${ e.trainingplandataid!=null?trainingTDColor:"" }>
          <fmt:formatDate value="${e.starttime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM 'start'\")%>"/>
          ${e.endtime!=null?'<br/>':''}<fmt:formatDate value="${e.endtime}" pattern="<%=lang.__(\"MMM.d,yyyy 'at' HH:MM 'end'\")%>"/>
        </td>
        <c:if test="${0<fn:length(cityLocalgovInfos)}">
        <td ${ e.trainingplandataid!=null?trainingTDColor:"" }>
          <c:forEach var="cityTrackgroupData" varStatus="s3" items="${e.cityTrackgroupDatas}">
            ${0<s3.index?"<br/>":""}${f:h(cityTrackgroupData.cityTrackData.localgovInfo.city)}
          </c:forEach>
        </td>
        </c:if>
        <c:if test="${groupInfo.headoffice}">
        <td ${ e.trainingplandataid!=null?trainingTDColor:"" }>
        	<input id="delete-button" type="submit" name="delete" value="<%=lang.__("Delete")%>" class="header-button" ${e.endtime==null ? 'disabled="disabled"' : ''}
        	 onclick="$('input[name=oldtrackid]').val(${e.id}); return regist('<%=lang._E("Do you want to delete the disaster?\nit can not be restored.")%>');" />
        </td>
        </c:if>
      </tr>
      </c:forEach>
  </c:if>
</c:forEach>
    </table>
  </form:form>
	</div>

</div>
<!-- end .container -->
</body>
</html>
