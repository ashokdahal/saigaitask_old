<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../common/lang_resource.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>${f:h(loginForm.systemname)}</title>
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" />
<link rel="stylesheet" href="${f:url('/css/screen.css')}"  type="text/css" media="screen" title="default" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/header.css')}" />
<script src="${f:url('/js/jquery-1.10.2.min.js')}" type="text/javascript"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<%--
<script src="${f:url('/js/jquery.pngFix.pack.js')}" type="text/javascript"></script>
 --%>
<script type="text/javascript">
$(document).ready(function(){
	var index = ${f:h(currenttab)};
    $("#currenttab").val(index);

    $( "#login-tabs" ).tabs({
		active : index,
		activate: function(event, ui) {
	        $("#currenttab").val($(this).tabs('option', 'active'));
	        var buttonId = "submit-login-" + (Number($("#currenttab").val()) + 1);
    		$("#"+buttonId).focus();

	    }
	});

	// Enterキー押下で災害タブのログインボタンを押下してしまうため、
	// 現在のタブのボタンを押し直す。
    $("#submit-login-1").on("click keydown", function(e) {
    	if(typeof e.keyCode === "undefined" || e.keyCode === 13) {
    		if(Number($("#currenttab").val()) >= 1){
    			// 言語コードは災害タブ側の選択値を送信しているので、
    			// 表示しているタブの言語コードで災害タブの言語コード選択値を更新
    			var langCode = $("#langCode2").val();
    			$("#langCode1").val(langCode);

        		$("#submit-login-2").click();
        		return false;
    		}
    	}
    });
    
    //$(document).pngFix( );
	reloadindex();
	//setInterval(reloadindex, 20000);//インターバル入れると、ユーザ画面でセッションが切れたときに大量アクセスが発生する

	var index = 0;
	<c:if test="${loginForm.password2!=''}">
		index = 1;
	</c:if>
	//$( "#login-tabs" ).tabs({active : index});
});
function reloadindex() {
	//document.location.reload();
	$("#login-info").load('${f:url('/triggerinfo')}/${loginForm.localgovinfoid}');
}
</script>
</head>
<body id="login-bg">

<!-- Start: login-info -->
<div id="login-info">
	<!-- start logininfo_box -->
	<div id="logininfo_box_1" class="logininfo_box">
	</div>
	<!-- end locgininfof_box -->
</div>
<!-- end: login-info -->

<form:form modelAttribute="loginForm">
<div id="login-tabs" style="width:500px;margin: 0 auto; background:none;background-color:#cccccc">
	<ul>
		<li><a href="#login-tabs-1"><%=lang.__("Disaster")%></a></li>
		<li><a href="#login-tabs-2"><%=lang.__("Normal")%></a></li>
	</ul>
<div id="login-tabs-1">

<!-- Start: login-holder -->
<div id="login-holder">

	<!--  start loginbox ..............-->
	<div id="loginbox" style="border:none;">

			<div id="loginbox-title">${f:h(loginForm.systemname)} <%=lang.__("Login")%></div>
			<p></p>
			<br />
		<% FormUtils.printToken(out, request); %>
		<form:hidden path="currenttab" value="0" id="currenttab" />
		<form:hidden path="localgovinfoid"/>
		<form:hidden path="returnpath"/>
		<form:hidden path="type"/>
		<c:if test="${0==loginForm.localgovinfoid}">
		<form:hidden path="groupid" value=""  onfocus="this.value=''" cssClass="login-inp" />
		</c:if>
		
	<!--  start login-inner -->
	<div id="login-inner">
		<table border="0" cellpadding="0" cellspacing="0">
<c:if test="${0<loginForm.localgovinfoid}">
	<%-- 記録データが無い場合は、災害種別を選択してログインする --%>
	<c:choose>
	<c:when test="${fn:length(trackDatas) == 0}">
		<c:if test="${!disasterCombined}">
			<tr>
				<th><%=lang.__("Disaster type")%></th>
				<c:if test="${(loginForm.disasterid != 0)}">
					<form:hidden path="disasterid"/>
				</c:if>
				<td><form:select path="disasterid" cssClass="styledselect">
				<c:if test="${fn:length(disasterItems)!=1}">
					<form:option value=""> <%=lang.__("Select.")%></form:option>
				</c:if>
	<c:forEach var="e" varStatus="s" items="${disasterItems}">
				<form:option value="${e.id}"> ${f:h(e.name)} </form:option>
	</c:forEach>
			  </form:select> </td>
			</tr>
		</c:if>
		<c:if test="${disasterCombined}">
			<input type="hidden" name="disasterid" value="1" >
		</c:if>
	</c:when>
	<%-- 記録データがある場合は、災害データを選択してログインする --%>
	<c:otherwise>
			<tr>
				<th><%=lang.__("Disaster name")%></th>
				<c:set var="dis" value="false" />
				<%-- 1つしかないなら、選ばせない --%>
				<c:if test="${fn:length(trackDatas) == 1}">
					<c:set var="dis" value="true" />
					<form:hidden path="trackdataid"/>
				</c:if>
				<td>
					<form:select path="trackdataid" cssClass="styledselect" disabled="${dis}">
						<c:forEach var="e" varStatus="s" items="${trackDatas}">
							<form:option value="${e.id}"> ${f:h(e.name)} </form:option>
						</c:forEach>
					</form:select>
				</td>
			</tr>
	</c:otherwise>
	</c:choose>

	<tr>
		<th><%=lang.__("Group name<!--2-->")%></th>
	  <td><form:input path="groupid" value=""  onfocus="this.value=''" cssClass="login-inp" />
	  </td>
	</tr>
</c:if>
		<tr>
			<th><%=lang.__("Password")%></th>
			<td><input type="password" name="password" value=""  onfocus="this.value=''" class="login-inp" /></td>
		</tr>
<c:if test="${0<loginForm.localgovinfoid}">
		<tr>
			<th><%=lang.__("Map display")%></th>
			<td>
				<form:radiobutton path="mapVisible" value="1" /> <%=lang.__("Display")%>
				<form:radiobutton path="mapVisible" value="0" /> <%=lang.__("Hide")%>
			</td>
		</tr>
</c:if>
		<tr>
			<th><%=lang.__("Language code")%></th>
			<td>
				<form:select path="langCode" cssClass="styledselect">
					<c:forEach var="e" varStatus="s" items="${multilangInfoItems}">
						<form:option value="${e.code}" > ${f:h(e.name)} </form:option>
					</c:forEach>
				</form:select>
			</td>
		</tr>
		<tr>
			<th></th>
			<td>
				<c:if test="${(!bmaster)}"><input type="submit" name="logintask" class="submit-login" id="submit-login-1" value="<%=lang.__("Login")%>" /></c:if>
				<!--<c:if test="${(loginForm.disasterid == 0)}"><input type="submit" name="loginview" value="閲覧ログイン" /></c:if>-->
				<c:if test="${(bmaster)}"><input type="submit" name="loginmaster" value="<%=lang.__("Master login")%>" /></c:if>
			</td>
		</tr>

		</table>
	</div>
 	<!--  end login-inner -->
<font color="red"><ul><c:forEach var="msg" items="${errors}"><li><c:out value="${f:h(msg)}" escapeXml="false"/></li></c:forEach></ul></font>
 </div>
 <!--  end loginbox -->

	</div>
<!-- End: login-holder -->
</div>

<!--  平常時モードログイン -->
<div id="login-tabs-2">

<!-- Start: login-holder -->
<div id="login-holder">

	<!--  start loginbox ..............-->
	<div id="loginbox" style="border:none;">

			<div id="loginbox-title">${f:h(loginForm.systemname)} <%=lang.__("Login")%></div>
			<p></p>
			<br />
		<% FormUtils.printToken(out, request); %>
	<!--  start login-inner -->
	<div id="login-inner">
		<table border="0" cellpadding="0" cellspacing="0">
<c:if test="${0<loginForm.localgovinfoid}">
	<tr>
		<th><%=lang.__("Unit name<!--2-->")%></th>
		<td><form:input path="unitid" value=""  onfocus="this.value=''" cssClass="login-inp" />
		</td>
	</tr>
</c:if>
		<tr>
			<th><%=lang.__("Password")%></th>
			<td><input type="password" name="password2" value=""  onfocus="this.value=''" class="login-inp" /></td>
		</tr>
		<tr>
			<th><%=lang.__("Language code")%></th>
			<td>
				<form:select path="langCode" cssClass="styledselect">
					<c:forEach var="e" varStatus="s" items="${multilangInfoItems}">
						<form:option value="${e.code}" > ${f:h(e.name)} </form:option>
					</c:forEach>
				</form:select>
			</td>
		</tr>
		<tr>
			<th></th>
			<td>
				<c:if test="${(!bmaster)}"><input type="submit" name="loginusual" class="submit-login" id="submit-login-2" value="<%=lang.__("Login")%>" /></c:if>
				<!--<c:if test="${(loginForm.disasterid == 0)}"><input type="submit" name="loginview" value="閲覧ログイン" /></c:if>-->
				<c:if test="${(bmaster)}"><input type="submit" name="loginmasterusual" value="<%=lang.__("Master login")%>" /></c:if>
			</td>
		</tr>

		</table>
	</div>
 	<!--  end login-inner -->
<font color="red"><ul><c:forEach var="msg" items="${errors}"><li><c:out value="${f:h(msg)}" escapeXml="false"/></li></c:forEach></ul></font>
 </div>
 <!--  end loginbox -->

	</div>
<!-- End: login-holder -->
</div>

</div><!-- tabs -->
 </form:form>

	<div style="position: absolute; bottom: 0%; right: 0%;">
		<span title="${f:h(versionDetail)}"><c:if test="${! empty version}">Ver ${f:h(version)}</c:if></span>
	</div>
</body>
</html>
