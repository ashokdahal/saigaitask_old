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
	var errorMsg = "${f:h(errorMsg)}";
	if(errorMsg != ""){
		alert(errorMsg);
	}else{
		window.parent.getSendResponseAjax();
	}
}
</script>
</head>
<body>
</body>
</html>
