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
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" />
<!--
<link rel="stylesheet" href="${f:url('/css/screen_admin.css')}"  type="text/css" media="screen" title="default" />
 -->
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
});

function loadTop() {
	var url = "<%=request.getContextPath()%>" + "/admin/";
	if (window.self != window.top) {
		top.location.href=url;
	}
}
</script>
</head>

<body id="login-bg" onLoad="loadTop()">
	<!-- Start: login-info -->
	<div id="login-info">
		<!-- start logininfo_box -->
		<div id="logininfo_box_1" class="logininfo_box">
		</div>
		<!-- end locgininfof_box -->
	</div>
	<!-- end: login-info -->

	<!-- Start: login-tabs -->
	<form:form modelAttribute="loginForm">
	<div id="login-tabs" style="width:500px;margin: 0 auto; background:none;background-color:#cccccc">
		<ul>
			<li><a href="#login-tabs-1"><%= lang.__("Disaster")%></a></li>
			<li><a href="#login-tabs-2"><%= lang.__("Normal")%></a></li>
		</ul>

		<% FormUtils.printToken(out, request); %>
		<form:hidden path="currenttab" value="0" id="currenttab" />
		<form:hidden path="type"/>
		<!-- Start: tab1 -->
		<div id="login-tabs-1">
			<!-- Start: login-holder -->
			<div id="login-holder">
				<!-- start logo -->
				<div id="logo-login">
					<!--<a href="index.html"><img src="images/shared/logo.png" width="156" height="40" alt="" /></a>-->
				</div>
				<!-- end logo -->
					<div class="clear"></div>
					<!--  start loginbox ..............-->
				<div id="loginbox">
					<div id="loginbox-title">${f:h(systemname)}</div>
					<div id="loginbox-title"><%=lang.__("(Admin Window)")%></div>
						<br />
					<form:hidden path="localgovinfoid"/>
					<form:hidden path="returnpath"/>
					<!--  start login-inner -->
					<div id="login-inner">
						<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<th><%=lang.__("Group name<!--2-->")%></th>
							<td><form:input path="groupid" value=""  onfocus="this.value=''" cssClass="login-inp" />
							  </td>
						</tr>
						<tr>
							<th><%=lang.__("Password")%></th>
							<td><input type="password" name="password" value=""  onfocus="this.value=''" class="login-inp" /></td>
						</tr>
						<tr>
							<th><%=lang.__("Language code")%></th>
							<td>
								<form:select path="langCode" cssClass="styledselect" id="langCode1">
									<c:forEach var="e" varStatus="s" items="${multilangInfoItems}">
										<form:option value="${e.code}"> ${f:h(e.name)} </form:option>
									</c:forEach>
								</form:select>
							</td>
						</tr>
						<tr>
							<th></th>
							<td>
								<input type="submit" name="logintask" class="submit-login" id="submit-login-1" value="<%=lang.__("Login")%>" />
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
		<!-- End: tab1 -->

		<!-- Start: tab2 -->
		<div id="login-tabs-2">
			<!-- Start: login-holder -->
			<div id="login-holder">
				<!-- start logo -->
				<div id="logo-login">
					<!--<a href="index.html"><img src="images/shared/logo.png" width="156" height="40" alt="" /></a>-->
				</div>
				<!-- end logo -->
					<div class="clear"></div>
					<!--  start loginbox ..............-->
				<div id="loginbox">
					<div id="loginbox-title">${f:h(systemname)}</div>
					<div id="loginbox-title"><%=lang.__("(Admin Window)")%></div>
						<br />
					<form:hidden path="localgovinfoid"/>
					<form:hidden path="returnpath"/>
					<!--  start login-inner -->
					<div id="login-inner">
						<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<th><%=lang.__("Unit name<!--2-->")%></th>
							<td><form:input path="unitid" value=""  onfocus="this.value=''" cssClass="login-inp" />
							  </td>
						</tr>
						<tr>
							<th><%=lang.__("Password")%></th>
							<td><input type="password" name="password2" value=""  onfocus="this.value=''" class="login-inp" /></td>
						</tr>
						<tr>
							<th><%=lang.__("Language code")%></th>
							<td>
								<form:select path="langCode" cssClass="styledselect" id="langCode2">
									<c:forEach var="e" varStatus="s" items="${multilangInfoItems}">
										<form:option value="${e.code}"> ${f:h(e.name)} </form:option>
									</c:forEach>
								</form:select>
							</td>
						</tr>
						<tr>
							<th></th>
							<td>
								<input type="submit" name="loginusual" class="submit-login" id="submit-login-2" value="<%=lang.__("Login")%>" />
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
		<!-- End: tab1 -->
	</div>
	</form:form>
	<!-- End: login-tabs -->
</body>
</html>
