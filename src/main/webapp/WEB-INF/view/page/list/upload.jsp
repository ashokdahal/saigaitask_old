<%/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<script type="text/javascript">
window.onload = function(){
	var responseMsg = "${f:h(responseMessage)}";
	if(0<responseMsg.length) {
		responseMsg = responseMsg.replace(/&#034;/g, '"'); // ダブルクォートの復号
		alert(responseMsg);
	}
	window.parent.saveFilesAfter();
}
</script>
</head>
<body>
</body>
</html>
