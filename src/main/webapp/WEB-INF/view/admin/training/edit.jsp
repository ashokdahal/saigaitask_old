<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8" />
<script type="text/javascript">
window.onload = function(){
	var errorMsg = "${errorMsg}";
	if(errorMsg != ""){
		alert(errorMsg);
	}else{
		// 情報を取得し直す
		window.parent.locationHrefURL("${trainingplandataid}", "external");
	}
}
</script>
</head>
<body>
</body>
</html>
