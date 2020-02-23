<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<c:set var="isInsert" value="${loginDataDto.localgovinfoid==0}"/>

<script type="text/javascript">
$(function() {
	var form = document.getElementById("contents_setup_form");

	// 新規作成の場合
	if(SaigaiTask.loginDataDto.localgovinfoid==0) {
		var restore = document.getElementsByName("restoremode");
		var createnew = document.getElementById("createnew");
		var select = document.getElementById("select");
		$(window).on("load", function(){
			restoreFormDisp(restore, createnew, select);
		});
		$(restore).on("click", function(){
			restoreFormDisp(restore, createnew, select);
		});

		// 設定テンプレート欄のタブ化
		$("#templateFile-tabs").tabs({
			active: ${initSetupForm.templateFileMode}
		});
	}
	// 自治体指定の場合
	else {
		// 編集は未実装のため、新規作成でない場合は入力フォームを無効にする。
		$("input,select,textarea", form).attr("disabled", true);
	}

	// 登録リクエストがあれば進捗チェック
	var requestid = "${initSetupForm.requestid}";
	var success = ${empty initSetupForm.success ? "null" : initSetupForm.success};
	if(0<requestid.length && success==null) {
		SaigaiTask.setupper.InitSetup.startCheckProgress(requestid);
	}
});

function restoreFormDisp(restore, createnew, select){
	//新規作成にチェックを入れた場合サイト名称を入力
	if(restore[0].checked){
		createnew.style.display="";
		select.style.display="none";
	}
	//選択にチェックを入れた場合サイトIDとグループIDを入力
	else if(restore[1].checked){
		createnew.style.display="none";
		select.style.display="";
	}
}
</script>

<div id="content_head" class="ui-layout-north">
	<h1>${isInsert?lang.__('Local gov. creation window'):lang.__('Local gov. info')}</h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left;">
<form:form id="contents_setup_form" method="post" enctype="multipart/form-data" cssStyle="width: 550px;" servletRelativeAction="/admin/setupper/initSetup/insert?_csrf=${_csrf.token}" modelAttribute="initSetupForm">
<div>
<c:choose>
<c:when test="${isInsert}">
<span><%=lang.__("All info registered in process of city configuration are modified on admin window.")%></span>
</c:when>
<c:otherwise>
	<c:if test="${initSetupForm.success==true}"><span><%=lang.__("Registered.")%></span></c:if>
	<c:if test="${loginDataDto.localgovInfo.valid==false}"><span><%=lang.__("This local government info is invalid.")%></span></c:if>
</c:otherwise>
</c:choose>

	<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
    <c:forEach var="msg" items="${messages}">
		<br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
	</c:forEach>
	<c:remove var="messages" scope="session"/>

</div>
<br/>

	<form:hidden path="section" />
	<form:hidden path="valid" />
	<form:hidden path="localgovinfoid" />
	<form:hidden path="admin" />
<%--
	<form:hidden path="editable" />
	<form:hidden path="visible" />
	<form:hidden path="disporder" />
 --%>

	<h2 style="margin: 0px;"><%=lang.__("System settings")%></h2>
	<table border="1" class="form">
		<tr>
			<th><%=lang.__("System name<!--2-->")%></th>
			<td><form:input path="systemname" size="50"/></td>
		</tr>
		<tr>
			<th><%=lang.__("Domain name")%></th>
			<td><form:input path="domain" size="50" value="${!empty initSetupForm.domain ? initSetupForm.domain : 'cityname.bosai-cloud.jp'}" /></td>
		</tr>
		<tr>
			<th><%=lang.__("Interval of alarm confirmation(in seconds)")%><br></th>
			<td><form:input path="alarminterval" size="10"/>
				<br>
				<%=lang.__("(At this interval, browser confirm the presence or absence of alarm)")%></td>
		</tr>
	</table>

	<h2 style="margin: 0px;"><%=lang.__("Local gov. info")%></h2>
	<table border="1" class="form">
		<tr>
			<th><%=lang.__("Local gov. type")%></th>
			<td>
			<c:forEach var="localgovtypeItem" items="${localgovtypeItems}" varStatus="s">
				<c:set var="styleId" value="localgovtype-${localgovtypeItem.id}"/>
				<form:radiobutton path="localgovtypeid" value="${localgovtypeItem.id}" id="${styleId}"/>
				<label for="${styleId}">${localgovtypeItem.name}</label>
			</c:forEach>
			</td>
		</tr>
		<tr>
			<th><%=lang.__("Prefecture name")%></th>
			<td><form:input path="pref" size="10"/></td>
		</tr>
		<tr>
			<th><%=lang.__("Prefecture code")%></th>
			<td><form:input path="prefcode" size="10"/><%=lang.__("(Local gov. code 2 digits)")%></td>
		</tr>
		<tr>
			<th><%=lang.__("City name")%></th>
			<td><form:input path="city" size="30"/></td>
		</tr>
		<tr>
			<th><%=lang.__("City code")%></th>
			<td><form:input path="citycode" size="10"/><%=lang.__("(Local gov. code 3 digits)")%></td>
		</tr>
		<tr>
			<th><%=lang.__("Notes")%> </th>
			<td><form:textarea path="note" cssStyle="width: 100%;" rows="6"></form:textarea></td>
		</tr>
	</table>

<%--
	<c:if test="${loginDataDto.localgovinfoid==0}">
	<h2 style="margin: 0px;">管理班情報</h2>
	<table border="1" class="form">
		<tr>
			<th>班名</th>
			<td><form:input path="name" size="30" /><br>
				（官民協働危機管理クラウドシステム 管理班名）</td>
		</tr>
		<tr>
			<th>パスワード</th>
			<td><form:input path="password" size="30" /><br>
			（官民協働危機管理クラウドシステムにログインするパスワード）</td>
		</tr>
		<tr>
			<th>eコミマップユーザアカウント </th>
			<td><form:input path="ecomuser" size="30" /></td>
		</tr>
	</table>
	</c:if>
 --%>

	<c:if test="${isInsert}">
	<h2 style="margin: 0px;"><%=lang.__("Import settings")%></h2>
	<table border="1" class="form">
		<tr>
			<th><%=lang.__("Import method")%></th>
			<td>
				<c:forEach var="title" items='<%=lang.__("Do not import, Import local gov. settings and master map ,Import local gov. settings and master map and replacement of the system master")%>' varStatus="s">
<%--				<c:forEach var="title" items="do not import, import local gov. settings and master map ,import local gov. settings and master map and replacement of the system master" varStatus="s"> --%>
				<c:set var="styleId" value="importmode-${s.index}"/>
				<form:radiobutton path="importmode" value="${s.index}" id="${styleId}"/>
				<label for="${styleId}">${title}</label><br/>
				</c:forEach>
				<span style="color:red;"><%=lang.__("* Replacing system master table deletes existed local gov. data.")%></span>
			</td>
		</tr>
		<tr>
			<th><%=lang.__("Password")%></th>
			<td><form:input path="password" size="30" /><br>
			<%=lang.__("Set password for groups and units created by import.")%>
			</td>
		</tr>
		<tr>
			<th><%=lang.__("e-Com map user account")%> </th>
			<td><form:input path="ecomuser" size="30" /><br/>
			<%=lang.__("Set e-community map user for groups and units created by import.")%><br/>
			</td>
		</tr>
		<tr class="templateFile">
			<th><%=lang.__("Setting template")%></th>
			<td>

				<form:hidden path="templateFileMode"/>
				<div id="templateFile-tabs" style="display:block; word-wrap: break-word; word-break : break-all;">
					<ul>
						<li><a href="#templateFile-tabs-0" onclick="$('[name=templateFileMode]').val(0)"><%=lang.__("Upload")%></a></li>
						<li><a href="#templateFile-tabs-1" onclick="$('[name=templateFileMode]').val(1)"><%=lang.__("uploaded configuration template")%></a></li>
					</ul>
					<div id="templateFile-tabs-0" style="padding:5px;">
						${systemVer}<%=lang.__("System version")%>:${systemVersion}<br/>
						<input type="file" name="templateFile"/><br/>
					</div>
					<div id="templateFile-tabs-1" style="padding:5px;">
					<c:choose>
					<c:when test="${initSetupForm.confirmTemplateFile==false}">
						<%=lang.__("No uploaded files.")%><br/>
						&nbsp;
					</c:when>
					<c:otherwise>
						<%=lang.__("System version")%>:${systemVersion}
						&nbsp;<%=lang.__("Configuration template version")%>:${initSetupForm.templateFileVersion}<br/>
						<%=lang.__("Configuration template file name")%>:<input type="text" readonly style="width:450px;" title="${initSetupForm.templateFileName}" alt="${initSetupForm.templateFileName}" value="${initSetupForm.templateFileName}"/><br/>
					</c:otherwise>
					</c:choose>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<th><%=lang.__("Restore destination of master map")%></th>
			<td>
				<c:forEach var="title" items="${lang.__('Create, Select')}" varStatus="s">
					<c:set var="styleId" value="restoremode-${s.index}"/>
					<form:radiobutton path="restoremode" value="${s.index}" id="${styleId}"/>
				<label for="${styleId}">${title}</label>
					&nbsp;
				</c:forEach>
				<br/>
				<div id=createnew>
				<span><%=lang.__("Site name")%>:<form:input path="siteName" /></span>
				</div>
				<div id=select style="display:none">
				<span><%=lang.__("Site ID")%>:<form:input path="mapRestoreCommunityid" size="10" /></span>
				<span><%=lang.__("Group ID")%>:<form:input path="mapRestoreGroupid" size="10" /></span>
				<br/><span><%=lang.__("Restore as entire site in case of group ID unspecified")%></span>
				</div>
			</td>
		</tr>
	</table>
	</c:if>

	<c:if test="${!isInsert}">
	<h2 style="margin: 0px;"><%=lang.__("Master map")%></h2>
	<c:choose>
	<c:when test="${mapmasterInfo!=null}">
	<table border="1" class="form">
		<tr>
			<th><%=lang.__("e-Com map window")%></th>
			<td><a target="ecommap" href="${ecommapURL}map/map/?cid=${mapmasterInfo.communityid}&gid=${mapmasterInfo.mapgroupid}&mid=${mapmasterInfo.mapid}"><%=lang.__("Open")%></a></td>
		</tr>
	</table>
	</c:when>
	<c:otherwise>
	<span><%=lang.__("Set master map in administration window.")%></span>
	</c:otherwise>
	</c:choose>
	</c:if>
</form:form>
</div>

<div id="content_footer" class="ui-layout-south">
	<c:if test="${loginDataDto.localgovinfoid==0}">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.submitForm('contents_setup_form', '<%=lang._E("Are you sure to create local gov.?")%>');"><%=lang.__("Registration")%></a>
	</c:if>
	<c:if test="${0<loginDataDto.localgovinfoid}">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.exportInfo(${loginDataDto.localgovinfoid});"><%=lang.__("Export")%></a>
	</c:if>
	<%-- システム管理者でログイン中かつ自治体を選択時のみ(新規作成以外)、「自治体削除ボタン」を表示する --%>
	<c:if test="${0<loginDataDto.localgovinfoid && loginDataDto.groupid==0}">
	<a href="#" class="btn blue" onclick="return SaigaiTask.setupper.loadContent(SaigaiTask.contextPath+'/admin/setupper/deleteLocalgovInfo/content');"><%=lang.__("Municipality Deletion")%></a>
	</c:if>
</div>

