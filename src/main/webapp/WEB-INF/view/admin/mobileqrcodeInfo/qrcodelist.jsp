<%@page import="jp.ecom_plat.saigaitask.entity.db.MobileqrcodeInfo"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@include file="../../common/jsp_lang.jsp" %>
<c:set var="title" value="${lang.__('post-appli authorized QR')}"/>
<head>
<title>${loginDataDto.groupInfo.name}${loginDataDto.unitInfo.name} - ${title}</title>
<style type="text/css">
.qrcode_container {
  border: 1px solid gray;
  margin: 5px;
}
.qrcode_title,.qrcode_image {
  border: 1px solid gray;
  padding: 3px;
  text-align: center;
}
</style>
<script>
function addPassword(qrcodeImageDiv) {
	// ready dom
	var withpassImg = document.querySelector("img.withpass", qrcodeImageDiv);
	var nopassImg = document.querySelector('img.nopass', qrcodeImageDiv);
	var passwordInput = document.querySelector("input[type='password']", qrcodeImageDiv);

	// load with password qr code image
	var password = passwordInput.value;
	var url = nopassImg.src;
	withpassImg.src = url+"&password="+encodeURIComponent(password);
	
	// show password qrcode
	withpassImg.style.display='inline';
	nopassImg.style.display='none';
	
	// show alert message
	passwordAlertSpan = document.querySelector("span.password-alert", qrcodeImageDiv);
	passwordAlertSpan.innerText="";
	if(0<password.length) {
		passwordAlertSpan.innerText="※パスワードが含まれています";
	}
}
</script>
</head>
<body>
<h2>${title}</h2>
<c:set var="isAdmin" value="${loginDataDto.groupInfo.admin || loginDataDto.unitInfo.admin}"/>
<c:if test="${isAdmin}"><h3><%= lang.__("(admin mode)") %></h3></c:if>

<c:set var="total" value="0"/>
<c:forEach var="info" items="${infos}">
<div style="page-break-inside: avoid;">
	<c:set var="isGroup" value="${fn:endsWith(info['class'].name, 'GroupInfo')}"/>
	<c:set var="count" value="0"/>
	<c:forEach var="mobileqrcodeInfo" items="${mobileqrcodeInfos}">
		<c:set var="isTarget" value="${false}"/>
		<c:if test="${isGroup==true}" ><c:set var="isTarget" value="${(info.id==-1 && mobileqrcodeInfo.groupid==-1) || mobileqrcodeInfo.groupid==info.id}"></c:set></c:if>
		<c:if test="${isGroup==false}"><c:set var="isTarget" value="${(info.id==-1 && mobileqrcodeInfo.unitid==-1)  || mobileqrcodeInfo.unitid ==info.id}"></c:set></c:if>
		<c:if test="${isTarget}"><c:set var="count" value="${count+1}"/></c:if>
	</c:forEach>
	<c:set var="total" value="${total+count}"/>
	<c:if test="${0<count}">
	<h2>${info.name}</h2>
	<div>
	<c:forEach var="mobileqrcodeInfo" items="${mobileqrcodeInfos}">
		<c:set var="isTarget" value="${false}"/>
		<c:if test="${isGroup==true}" ><c:set var="isTarget" value="${(info.id==-1 && mobileqrcodeInfo.groupid==-1) || mobileqrcodeInfo.groupid==info.id}"></c:set></c:if>
		<c:if test="${isGroup==false}"><c:set var="isTarget" value="${(info.id==-1 && mobileqrcodeInfo.unitid==-1)  || mobileqrcodeInfo.unitid ==info.id}"></c:set></c:if>
		<c:if test="${isTarget}">
			<div class="qrcode_container" style="float:left; width:300px;">
			<div class="qrcode_title">${mobileqrcodeInfo.title}</div>
			<div class="qrcode_image"><img class="nopass" src="${f:url('/admin/mobileqrcodeInfo/qrcode')}?id=${mobileqrcodeInfo.id}&${isGroup?'groupid':'unitid'}=${info.id}"/>
			<c:if test="${mobileqrcodeInfo.id!=0}"><%-- id=0はAPIキー用 --%>
			<img class="withpass" style="display:none;"/>
			<%
				MobileqrcodeInfo mobileqrcodeInfo = (MobileqrcodeInfo) pageContext.findAttribute("mobileqrcodeInfo");
			    String authenticationdate = new SimpleDateFormat("yyyy-MM-dd").format(mobileqrcodeInfo.authenticationdate);
			%>
			<br><%= lang.__("※"+authenticationdate+"まで有効")%>
			<br><span class="password-alert"></span><input type="password" value=""/><input type="button" value="パスワード設定" onclick="addPassword(this.parentNode);"/>
			</c:if>
			</div>
			</div>
		</c:if>
	</c:forEach>
		<div style="clear:both;"></div>
	</div>
	<br/>
	</c:if>
</div>
</c:forEach>
<c:if test="${total==0}"><%= lang.__("No post-appli authorized QR setting")%></c:if>
</body>