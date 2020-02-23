<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<!DOCTYPE html>
<html lang="ja">
<head><jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<meta charset="utf-8">
<meta name="description" content="<%=lang.__("It is admin window of NIED disaster information sharing system.")%>">
<meta name=" keywords" content="<%=lang.__("NIED disaster information sharing system, admin window")%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />
<title><%=lang.__("Admin window : NIED disaster information sharing system")%></title>


</head>

<body>
<div id="wrapper">
	<h1><%=lang.__("Registration completion window to the clearinghouse")%></h1>

	<div>
		<font color="red"><ul><form:errors path="*" element="li"/></ul></font>
		<c:if test="${uploadckanDataForm.mdFileID!=''}">
		<c:if test="${!uploadckanDataForm.update}">
		${lang.__("Map title {0} <br/> is registered with <br/> metaID : {1}", uploadckanDataForm.title, uploadckanDataForm.mdFileID)}<br/>
		</c:if>
		<c:if test="${uploadckanDataForm.update}">
		${lang.__("Map title {0} <br/> is updated with <br/> metaID : {1}", uploadckanDataForm.title, uploadckanDataForm.mdFileID)}<br/>
		</c:if>
		</c:if>
		<c:if test="${uploadckanDataForm.error!=''}">
		${lang.__("Registration or update of metadata failed.")}：<br/>${uploadckanDataForm.error}
		</c:if>
		<c:if test="${uploadckanDataForm.updateDefault!=''}">
		${lang.__("Meta data default info updated.")}<br/>
		</c:if>
	</div><!-- /#contents_setup -->
</div><!-- /#wrapper -->
</body>
</html>
