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
<script src="${f:url('/js/jquery-1.10.2.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/ckeditor-4.6.2/ckeditor.js')}" type="text/javascript"></script>
<script type="text/javascript">

//
$(document).ready(function() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
	/* 各領域のサイズ調整 */
	var window_h = $(document).height()-100;
	$("#editor").height(window_h);
	//var window_w = $(document).width();

	ckeditorInit();

	$("#save").click(function(){
		if(confirm("<%=lang.__("Register messages?")%>")){
			var message = CKEDITOR.instances.editor.getData();
			var data = {
				"message" : message,
				"groupid" : ${loginDataDto.groupid}
			};
			//メッセージの送信
			$.ajax({
				url : "${f:url('/whiteboard/saveMessage/')}",
				//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
				type : "POST",
				dataType : "json",
				data : data,
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",
				success : function(data, dataType){
					alert("<%=lang.__("Saved.")%>");
					$("#registtime").val(data.registtime);
					$("#change").val(false);
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					//TODO:エラー処理
					alert("error : "+textStatus);
				}
			});
		}
	});
});

function ckeditorInit(){
	var window_h = $(document).height()-100;
	// エディタへの設定を適用する
	var editor = CKEDITOR.replace('editor',
	{
		uiColor : '#EEEEEE',
		height : window_h,
		toolbar : [
			[ "Source" ],
			[ "Link", "Table", "HorizontalRule" ],
			[ "Bold", "Underline", "Strike" ],
			[ "FontSize", "TextColor" ], [ "code" ] ],
		// スペルチェック機能OFF
		scayt_autoStartup : false,
		// Enterを押した際に改行タグを挿入
		enterMode : CKEDITOR.ENTER_BR,
		// Shift+Enterを押した際に段落タグを挿入
		shiftEnterMode : CKEDITOR.ENTER_P,
		// idやclassを指定可能にする
		allowedContent : true,
		// ツールバーを下にする
		toolbarLocation : 'bottom',
		// preコード挿入時
		format_pre : {
			element : 'pre',
			attributes : {
				'class' : 'code'
			}
		},
		// タグのパンくずリストを削除
		removePlugins : 'elementspath',
		// webからコピペした際でもプレーンテキストを貼り付けるようにする
		forcePasteAsPlainText : true,
		// 自動で空白を挿入しないようにする
		fillEmptyBlocks : false,
		// タブの入力を無効にする
		tabSpaces : 0,

	});
	CKEDITOR.instances.editor.setData('${message}');
}

// 自班以外を表示していたら編集不可にする。
$(function($){
	CKEDITOR.on('instanceReady',function(e){
		if(${f:h(whiteboardForm.groupid)} != ${f:h(loginDataDto.groupid)}){
			var editor = e.editor;
			editor.setReadOnly(true);
		}
	})
})

// XSS対策
function escapeHTML(val) { return $('<div />').text(val).html(); };

</script>
<style></style>
</head>
<body>
<div style="float:left; width:48%;">
	<select style="font-size:18px; margin-bottom:10px;" onChange="location.href=this.options[this.selectedIndex].value">
	<!-- ログイン中の班を一番上に持ってくる -->
	<c:forEach var="groupInfo" varStatus="s1" items="${groupInfoItems}">
		<c:if test="${groupInfo.id == whiteboardForm.groupid }">
			<option value="?groupid=${groupInfo.id}">${f:h(groupInfo.name)}</option>
		</c:if>
	</c:forEach>
	<!-- ログイン中ではない残りの班を表示する -->
	<c:forEach var="groupInfo" varStatus="s1" items="${groupInfoItems}">
		<c:if test="${groupInfo.id != whiteboardForm.groupid }">
			<option value="?groupid=${groupInfo.id}">${f:h(groupInfo.name)}</option>
		</c:if>
	</c:forEach>
	</select>
</div>
<div style="float:right; width:30%; text-align:right;">
	<%=lang.__("Last updated")%>:<input type=text id="registtime" value="${whiteboardForm.registtime}" readonly="readonly" />
</div>
<div style="float:left; width:51%; text-align:right;">
<c:choose>
	<c:when test="${whiteboardForm.groupid != loginDataDto.groupInfo.id }"> <font size=4><b><font color=#ff0000><b><%=lang.__("Editing other group's white board is not permitted.")%></b></font></b></font> </c:when>
	<c:otherwise>
		<input type="button" id="save" value="<%=lang.__("Save")%>" style="font-size:16px; width:80px;">
	</c:otherwise>
</c:choose>
</div>

<br style="clear:both;">
<div id="editor" style="background-color:#DDDDDD; width:95%; height:300px; margin:0 auto;">
</div>
</body>
</html>
