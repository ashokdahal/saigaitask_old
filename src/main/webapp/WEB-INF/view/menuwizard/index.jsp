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
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<link rel="stylesheet" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<link rel="stylesheet" href="${f:url('/css/jquery-ui-timepicker-addon.css')}" />
<link rel="stylesheet" href="${f:url('/css/screen.css')}"  type="text/css" media="screen" title="default" />
<script src="${f:url('/js/jquery-1.10.2.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
});
function sendAddmenu(){
	if(confirm("<%=lang.__("メニューを追加しますか?")%>")){
		console.log($('#menuwizard-dialog #addmenuname').val());
		var data = {
			"addmenuname" : $('#menuwizard-dialog #addmenuname').val()
		};
		//メッセージの送信
		$.ajax({
			url : "${f:url('/menuwizard/createmenu/')}",
			type : "POST",
			dataType : "json",
			data : data,
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			success : function(data, dataType){
				console.log(data);
				alert("<%=lang.__("追加しました。")%>");
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert("error : "+textStatus);
			}
		});
	}
	
}
</script>
</head>
<body>
<div style="font-size: 0.8em;">
<%=lang.__("追加情報タブに追加するメニューの名称を設定して下さい。")%>
</div>
<div style="width:80%;">
	<%=lang.__("名称")%>:<input type=text id="addmenuname" style="width:80%;"/>
</div>
</body>
</html>
