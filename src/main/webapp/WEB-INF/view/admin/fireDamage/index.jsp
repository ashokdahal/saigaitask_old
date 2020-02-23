<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<%@include file="../common/adminjs-header.jsp" %>
</head>
<body>
<div id="wrapper">
	<header>
		<div id="h_main">
			<h2>JXR被害情報</h2>
			<hr>
		</div>
	</header>
	<div id="main">
			<form:form id="fireDamageForm" enctype="multipart/form-data">
				<input type="file" name="uploadFile" /> <br/><br/>
				<input type="submit" name="upload" value="アップロード" />
			</form:form>
		</div>
<!-- /#wrapper --></div>
</body>
</html>
