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
<link rel="stylesheet" href="${f:url('/css/screen.css')}"  type="text/css" media="screen" title="default" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/bbs/bbs_table.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/bbs/badger.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/btn.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" />
<script src="${f:url('/js/jquery-1.10.2.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/btn.js')}" type="text/javascript"></script>
<script src="${f:url('/js/bbs/thread_method.js')}" type="text/javascript"></script>
<script src="${f:url('/js/bbs/thread_event.js')}" type="text/javascript"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<script type="text/javascript">
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
});

// 送信先グループIDを格納する変数
var sendMemberID;
var pagestart = false;
//
var csrf_token = '${cookie.JSESSIONID.value}';
// 訓練用の初期タイトル用（訓練でなければ空文字)
var thread_default_title = "${ f:h(thread_default_title) }";

$(document).ready(function() {
	/* 各領域のサイズ調整 */
	var window_h = $(document).height()-16;
	var window_w = $(document).width();
	$(".threadmenu").width(window_w*0.3).height(window_h);
	$(".threadmain").width(window_w*0.65).height(window_h);
	$(".threadmenu_middle").height(window_h - 80);
	$(".threadmenu_middle").width($(".threadmenu").width() * 0.9 - 15);
	$(".threadmain_middle").height(window_h - 235);
	// 送信先追加の欄と優先度変更欄はレス表示領域を使用する為、同じ大きさで登録
	$("#priorityRadio_edit").height(window_h - 235);
	$("#AddSendToGroupBtn_edit").height(window_h - 240);

	// 初回のみ、一番上のスレッドを自動で開く為のflag
	pagestart = true;

	// スレッド一覧の取得
	var watchThread = function(){
		getThreadListAjax();
		// スレッド情報を取得する
		var threadid = $('#threaddataid').val();
		if(threadid !== undefined || threadid !== "" || threadid != null){
			getThreadDataAjax(threadid, true);
		}
		setTimeout(watchThread, 20000);
	};
	watchThread();
	//getThreadListAjax();

	// メッセージ入力欄の調整
	var threadMainWidth = $(document).width()*0.65 - 10;
	$('#message_input_space').width(threadMainWidth - 180);

	// 優先度のRadioボタン
	$('#priorityRadio').buttonset();
	$('#priorityRadio_edit').buttonset();

	/* 優先度の設定用Radioボックスイベント */
	priorityRadioEvent();
	/* スレッド取得に関わるイベント */
	setThreadListEvent();
	/* ファイル送信、削除イベント */
	setFileSendEvent();
	/* 送信先に関するイベント */
	setSendToEvent();
	/* メッセージ送信に関するイベント */
	setSendMessageEvent();
});

/**
 * スレッドIDからスレッド情報を取得する
 * auto : 自動取得フラグ
 */
function getThreadDataAjax(threadID, auto){
	if(threadID == null) return;
	$.ajax({
		url : "${f:url('/bbs/haveThreadDatajson')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		data : "threadid=" + threadID,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			datas = eval(response);
			if(auto){
				// 内部的に更新
				$('#main_middle').empty();
				setResponseBox(datas.responseData);
				$('#threadUpdateTime').val(datas.latestTime);
			}else{
				// 右側のスペースを更新
				setThreadData(datas);
			}
		},
		error : function(data){
		}
	});
}

// スレッド情報の更新
function editThreadDataAjax(param, meth){
	$.ajax({
		url : "${f:url('/bbs/editThreadData')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		data : param + "&threaddataid=" + $('#threaddataid').val(),
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			if(meth == "priority"){
				// 優先度設定欄を非表示
				$('#priorityRadio_edit').addClass('hide');
				// 優先度が変更されたのでスレッド一覧を取得し直す
				getThreadListAjax();
				// スレッド内容表示欄を表示
				$('#main_middle').removeClass('hide');
			}else if(meth == "sendmember"){
				$('#AddSendToGroupBtn_edit').addClass('hide');
				// 送信先タブを一旦削除
				$('#AddSendToGroupBtn_edit').empty();
				// スレッドIDを使って再度開き直す
				getThreadDataAjax($('#threaddataid').val(), false);
				// スレッド内容表示欄を表示
				$('#main_middle').removeClass('hide');
			}else if(meth == "closed" || meth == "deleted"){
				// スレッド内容表示欄を閉じる
				$('.threadmain').css({"visibility":"hidden"});
				$('#main_top').addClass('hide');
				$('#main_middle').addClass('hide');
				$('#main_bottom').addClass('hide');
				// スレッドが閉じられたのでスレッド一覧を取得し直す
				getThreadListAjax();
			}else if(meth == "checkRead"){
				// スレッド一覧の再取得
				getThreadListAjax();
				// header.jspの未読数の更新
				window.parent.unReadThreadBBS();
				// 未読状態のものを既読状態にする
				$.each($('#main_middle').children('div'), function(){
					$.each($(this).children('div'), function(){
						if($(this).hasClass('threadResponse_unCheck')){
							$(this).removeClass('threadResponse_unCheck').addClass('threadResponse');
						}
					});
				});
			}
		},
		error : function(data){
			//alert("Error");
		}
	});
}

// 新規スレッドの作成
function sendNewThreadData(){
	if($("input[name=thread_title_val]").val() == ""){
		alert('<%=lang.__("Title of notice is required.")%>');
		return;
	}
	if($("textarea[name=thread_message_val_2]").val() == ""){
		alert('<%=lang.__("Message of notice is required.")%>');
		return;
	}
	var selectOnArr = new Array();
	$.each($('#thread_send_group_area').children('ul'),function(){
		// 中身のselectOnを取得する
		if($(this).hasClass('contents')){
			$.each($(this).children('li'),function(){
				$.each($(this).children('input'),function(){
					if($(this).hasClass("selectOn")){
						selectOnArr.push($(this).attr("ids"));
					}
				});
			});
		}
	});
	// 送信先が0件かチェック
	if(selectOnArr.length <= 1){
		if(!window.confirm('<%=lang.__("No send-to other than self-group is selected. Continue to create?")%>')){
			return;
		}
	}
	// 選択済送信先をForm部品に挿入する
	for(var i = 0; i < selectOnArr.length; i++){
		$('<input>').attr({type:"hidden",name:"sendto"}).val(selectOnArr[i]).addClass('sendSelect').appendTo('#new_thread_form');
	}
	/*
	$.each($(".selectOn"), function(){
		$('<input>').attr({type:"hidden",name:"sendto",value:$(this).attr('ids')}).addClass('sendSelect').appendTo('#new_thread_form');
	});
	*/

	// Formの値を取得
	var formdata = $('#new_thread_form').serialize();
	// URLボタンの生成チェック
	var message = $("textarea[name=thread_message_val_2]").val();
	message = escapeHTML(message);

	if($("#pageURLCheck").prop("checked")){
		message += window.parent.pageURLBBS();
	}
	formdata += "&" + $.param({"thread_message_val":message});

	// 先ほど追加した送信先データをFormから削除
	$.each( $(".sendSelect"), function(){
		$(this).remove();
	});
	$.ajax({
		url : "${f:url('/bbs/newThreadJson')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		data : formdata,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			datas = eval(response);
			// 登録した情報をクリアする
			sendNewThreadCancel();
			// スレッド一覧の更新
			getThreadListAjax();
		},
		error : function(data){
		}
	});

}

// メッセージの送信
function setSendMessage(message){
	var obj = {"message": message, "threaddataid": $('#threaddataid').val()};

	$.ajax({
		url : "${f:url('/bbs/sendMessage')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		data : $.param(obj),
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			datas = eval(response);
			// 中身を空にする
			$('#message_input_space').val("");
			// 未読状態のものを既読状態にする
			$.each($('#main_middle').children('div'), function(){
				$.each($(this).children('div'), function(){
					if($(this).hasClass('threadResponse_unCheck')){
						$(this).removeClass('threadResponse_unCheck').addClass('threadResponse');
					}
				});
			});
			getSendResponseAjax();
			getThreadListAjax();
			window.parent.unReadThreadBBS();
		},
		error : function(data){}
	});
}

// ファイル削除
function fileDeleteAjax(ids){
	$.ajax({
		url : "${f:url('/bbs/fileDelete')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		data : "deleteFile=" + ids + "&threaddataid=" + $('#threaddataid').val(),
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			// 該当のコメントを削除
			$.each($('.fileDeleteBtn'), function(){
				if($(this).attr('ids') == ids){
					// 親毎消す
					$(this).parent().parent().remove();
				}
			});
		},
		error : function(data){}
	});
}
// 該当スレッドのレスポンスを取得
function getSendResponseAjax(){
	// file要素を空にする
	$("#formFile_bbs").replaceWith($("#formFile_bbs").clone());
	$.ajax({
		url : "${f:url('/bbs/nowSendResponseAjax')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		/*data : "latesttime=" + $('#threadUpdateTime').val() + "&threaddataid=" + $('#threaddataid').val(),*/
		data : "latesttime=" + $('#threadUpdateTime').val() + "&threaddataid=" + $('#threaddataid').val(),
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			datas = eval(response);
			setResponseBox(datas.responseData);
			$('#threadUpdateTime').val(datas.updateTime);
		},
		error : function(data){
			//alert("Error");
		}
	});
}

// スレッド一覧を取得
function getThreadListAjax(){
	var reqData = "";

	if($('#AllThead-button').val() == "<%=lang.__("Display only self-group's notice")%>"){
		reqData = 'all_thread_flag=true';
	}
	$.ajax({
		url : "${f:url('/bbs/nowThreadListJSON2')}",
		//headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
		type : "POST",
		dataType : "json",
		data : reqData,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			datas = eval(response);
			$(".threadmenu_middle").empty();
			if(datas.threaddata == null || datas.threaddata.length == 0){
				$('<div>').append($('<p>').html('<%=lang.__("Currently notice has not been posted.")%>')).appendTo($(".threadmenu_middle"));
			}else{
				var threadList = datas.threaddata;
				for(var i = 0; i < threadList.length; i++){
					var divs = $('<div>').attr({"id":"thread-" + threadList[i].id})
						.addClass("threadListBox").addClass(threadList[i].threadStyle)
						.html(threadList[i].title + "<br>" + (threadList[i].latestTime == null ? "" : "<%=lang.__("Final editing")%>："+threadList[i].latestResponseTime + "<br><%=lang.__("Final read")%>："+threadList[i].latestTime))
						.appendTo($(".threadmenu_middle"));
					var unReadInt = parseInt(threadList[i].unRead, 10);
					if(unReadInt >= 10){
						// 10件以上になったら+表記にする
						badger_copy(divs, "thread-" + threadList[i].id, "10+");
					}else if(unReadInt == 0){
						// 終了スレッドかつ未読が0件なら完了マークを出す
						if(threadList[i].threadStyle == "endThread"){
							badger_copy_brown(divs, "thread-" + threadList[i].id, "<%=lang.__("Finished")%>");
						}else{
							// 何もしない
						}
					}else{
						// 数字をそのまま渡す
						badger_copy(divs, "thread-" + threadList[i].id, unReadInt);
					}
					// 起動時のみ、最初のスレッドを開く
					if(i == 0 && pagestart) divs.click();
				}
			}
			pagestart = false;
		},
		error : function(data){
			//alert("Error");
		}
	});
}

// XSS対策
function escapeHTML(val) { return $('<div />').text(val).html(); };

// 改行
function br_change(val){
	var txt = val;
	// まず改行らしき文字を\nに統一。\r、\r\n → \n
	txt = txt.replace(/\r\n/g, '\n');
	txt = txt.replace(/\r/g, '\n');
	// 改行を区切りにして入力されたテキストを分割して配列に保存する。
	var lines = txt.split('\n');
	if(lines.length == 0) return txt;
	else txt = "";
	for(var i = 0; i < lines.length; i++){
		if(i != 0) txt += "<br />";
		txt += lines[i];
	}
	return txt;
}

function changeFileForm(){
	var forms = $("#fileupload");
	var actionurl = "${f:url('/bbs/upload')}?_csrf=${_csrf.token}";
	forms.attr('action',  actionurl);
	$('<input>').attr({"type":"hidden","name":"session_token","value":'${cookie.JSESSIONID.value}'}).appendTo(forms);

	forms.submit();
}


</script>
<style></style>
</head>
<body>

<div class="threadmenu clearfix">
	<div class="threadmenu_top">
		<div style="text-align: center;">
			<input type="button" value="<%=lang.__("Create a new notice")%>" id="newThread-button" style="font-size:14px; width: 90%;" class="bbs-event-button"/>
		</div>
	</div>
	<div class="threadmenu_middle"></div>
	<div class="threadmenu_bottom" id="bbbb">
		<div style="text-align: center;">
			<input type="button" value="<%=lang.__("Display all notice")%>" id="AllThead-button" style="font-size:14px; width: 90%;" class="bbs-event-button"/>
		</div>
	</div>
</div>
<div class="threadmain">
	<!-- スレッドタイトル等の情報が表示される領域 -->
	<div class="threadmain_top" id="main_top">
		<div class="threadtitle" id="threadTitleDiv">
			<p id="threadtitleP" style="margin:0 0 5px 0;"></p>
			<div class="" id="threadmain_top_menu" style="text-align:right; width:100%; margin-bottom:5px;">
				<input type="button" value="<%=lang.__("Add destination")%>" id="AddSendToGroupBtn" style="visibility:hidden;" class="bbs-event-button"/>
				<input type="button" value="<%=lang.__("Priority change of notice")%>" id="editPriprityBtn" style="visibility:hidden;" class="bbs-event-button"/>
				<input type="button" value="<%=lang.__("Close notice")%>" id="threadCloseBtn" style="visibility:hidden;" class="bbs-event-button"/>
				<input type="button" value="<%=lang.__("Delete")%>" id="threadDeleteBtn" style="visibility:hidden;" class="bbs-event-button"/>
				<input type="hidden" value="" id="threadUpdateTime">
				<input type="hidden" value="" id="threaddataid">
			</div>
		</div>
		<div class="threadSubTitle" id="threadInfoDiv"></div>
	</div>
	<!-- メッセージ表示領域 -->
	<div class="threadmain_middle" id="main_middle"></div>
	<!-- 優先度変更表示領域 -->
	<div id="priorityRadio_edit" class="hide" style="margin:0 auto; text-align:center;">
		<input type="radio" name="thread_priority_val_edit" id="priority1_edit" value="1" />
		<label for="priority1_edit" id="priority1_label_edit"><%=lang.__("Emergency<!--2-->")%></label>
		<input type="radio" name="thread_priority_val_edit" id="priority2_edit" value="2" />
		<label for="priority2_edit" id="priority2_label_edit"><%=lang.__("High<!--3-->")%></label>
		<input type="radio" name="thread_priority_val_edit" id="priority3_edit" value="3" />
		<label for="priority3_edit" id="priority3_label_edit"><%=lang.__("Low<!--2-->")%></label>
		<input type="radio" name="thread_priority_val_edit" id="priority4_edit" value="4" />
		<label for="priority4_edit" id="priority4_label_edit"><%=lang.__("Not set")%></label>
		<br>
		<!-- <input class="orange-brown-button" id="editPriprityEnterBtn" type="button" value="変更完了" style="width:80%; margin:15px auto 0 auto;" /> -->
		<div style="text-align:center; width:100%;">
			<div class="orange-brown-button" id="editPriorityEnterBtn" style="width:30%; margin:15px 10px 0 auto;"><%=lang.__("Change<!--2-->")%></div>
			<div class="black-blue-button" id="editPriorityCancelBtn" style="width:30%; margin:15px auto 0 auto;"><%=lang.__("Cancel<!--3-->")%></div>
		</div>
	</div>
	<!-- 送信先変更表示領域 -->
	<div id="AddSendToGroupBtn_edit" class="hide" style="margin:0 auto;"></div>
	<!-- メッセージ作成領域 -->
	<div class="threadmain_bottom" id="main_bottom">
		<div class="message_upload"><%=lang.__("Bbs_Upload")%>
			<!-- ファイル送信用 -->
			<form:form id="fileupload" method="POST" enctype='multipart/form-data' target="fileframe">
				<% FormUtils.printToken(out, request); %>
				<p class="message_upload_hidden_p">
					<input type="file" name="formFile_bbs" id="formFile_bbs" onchange="changeFileForm()" />
				</p>
				<label for="formFile_bbs" id="labeldummy">.</label>
				<input type="hidden" name="form_threaddataid" id="fileform_id">
			</form:form>
		</div>
		<div class="message_check"><%=lang.__("Already read")%></div>
		<div class="message_space">
			<!-- <input type="text" value="" id="message_input_space" style="background-color:#5ED2FF;"> -->
			<textarea id="message_input_space" style="background-color:#5ED2FF; height:35px;"></textarea>
		</div>
		<div class="message_send"><%=lang.__("Send")%></div>
		<div style="font-size:16px; margin-top:10px; margin-bottom:4px;">
			<input type="checkbox" id="pageURLCheck_2"><label for="pageURLCheck_2"><%=lang.__("Paste opened page URL ")%></label>
		</div>
	</div>

	<!-- 新規作成時に表示される領域 -->
	<div class="new_threadmain hide" id="new_threadmain">
		<form name="new_thread_form" id="new_thread_form">
		<% FormUtils.printToken(out, request); %>
		<table class="newThreadTable">
			<thead>
				<tr><th colspan="2" id="new_thread_tabletitle"><%=lang.__("Create a new notice")%></th></tr>
			</thead>
			<tbody>
				<tr>
					<td width="100px"><%=lang.__("Title<!--2-->")%></td>
					<td><input name="thread_title_val" type="text" value="${ f:h(thread_default_title) }" style="width:95%;" /></td>
				</tr>
				<tr>
					<td><%=lang.__("Priority")%></td>
					<td>
						<div id="priorityRadio" class="priority_label">
						<input type="radio" name="thread_priority_val" id="priority1" value="1" />
						<label for="priority1" id="priority1_label"><%=lang.__("Emergency<!--2-->")%></label>
						<input type="radio" name="thread_priority_val" id="priority2" value="2" />
						<label for="priority2" id="priority2_label" class="priority_label"><%=lang.__("High<!--3-->")%></label>
						<input type="radio" name="thread_priority_val" id="priority3" value="3" />
						<label for="priority3" id="priority3_label" class="priority_label"><%=lang.__("Low<!--2-->")%></label>
						<input type="radio" name="thread_priority_val" id="priority4" value="4" checked="checked" />
						<label for="priority4" id="priority4_label" class="label_white priority_label"><%=lang.__("Not set")%></label>
						</div>
					</td>
				</tr>
				<tr>
					<td><%=lang.__("Send to<!--2-->")%></td>
					<c:choose>
						<c:when test="${empty cityLocalgovInfos && empty groupInfoItems && empty unitInfoItems}">
							<td><%=lang.__("There is no cooperation destination.")%></td>
						</c:when>
						<c:otherwise>
						<td>
							<div id="thread_send_group_area">
								<ul class="tab">
								<c:choose>
									<c:when test="${!empty groupInfoItems || !empty unitInfoItems}">
										<%--
										<li> <%=lang.__("Prefecture")%> </li>
										 --%>
										<c:choose>
										<c:when test="${prefLocalgovInfo.localgovtypeid==1}">
											<li>${f:h(prefLocalgovInfo.pref)}</li>
										</c:when>
										<c:when test="${prefLocalgovInfo.localgovtypeid==2}">
											<li>${f:h(prefLocalgovInfo.city)}</li>
										</c:when>
										<c:when test="${prefLocalgovInfo.localgovtypeid==3}">
											<li>${f:h(prefLocalgovInfo.section)}</li>
										</c:when>
										</c:choose>
									</c:when>
								</c:choose>
								<c:forEach var="cityLocalgovInfo" varStatus="s1" items="${cityLocalgovInfos}">
									<c:choose>
									<c:when test="${cityLocalgovInfo.localgovtypeid==1}">
										<li>${f:h(cityLocalgovInfo.pref)}</li>
									</c:when>
									<c:when test="${cityLocalgovInfo.localgovtypeid==2}">
										<li>${f:h(cityLocalgovInfo.city)}</li>
									</c:when>
									<c:when test="${cityLocalgovInfo.localgovtypeid==3}">
										<li>${f:h(cityLocalgovInfo.section)}</li>
									</c:when>
									</c:choose>
								</c:forEach>
								</ul>
								<ul class="contents">
								<c:choose>
									<c:when test="${!empty groupInfoItems}">
										<li class="hide">
											<div class="priority_select_all">
												<span class="priorityAllSelect"><%=lang.__("Select all<!--2-->")%></span> |
												<span class="priorityAllCancel"><%=lang.__("All cleared<!--2-->")%></span>
											</div>
											<c:forEach var="groupInfo" varStatus="s1" items="${groupInfoItems}">
												<input class="white-gray-button noSelect" type="button" ids="${groupInfo.id}" value="${f:h(groupInfo.name)}" style="padding:6px 15px; font-size:14px;" />
											</c:forEach>
										</li>
									</c:when>
								</c:choose>
								<c:choose>
									<c:when test="${!empty unitInfoItems}">
										<li class="hide">
											<div class="priority_select_all">
												<span class="priorityAllSelect"><%=lang.__("Select all<!--2-->")%></span> |
												<span class="priorityAllCancel"><%=lang.__("All cleared<!--2-->")%></span>
											</div>
											<c:forEach var="unitInfo" varStatus="s1" items="${unitInfoItems}">
												<input class="white-gray-button noSelect" type="button" ids="${unitInfo.id}" value="${f:h(unitInfo.name)}" style="padding:6px 15px; font-size:14px;" />
											</c:forEach>
										</li>
									</c:when>
								</c:choose>
								<c:forEach var="cityLocalgovInfo" varStatus="s1" items="${cityLocalgovInfos}">
									<li class="hide">
										<div class="priority_select_all">
											<span class="priorityAllSelect"><%=lang.__("Select all<!--2-->")%></span> |
											<span class="priorityAllCancel"><%=lang.__("All cleared<!--2-->")%></span>
										</div>
									<c:forEach var="groupInfo" varStatus="s2" items="${cityLocalgovInfo.groupInfos}">
										<input class="white-gray-button noSelect" type="button" ids="${groupInfo.id}" value="${f:h(groupInfo.name)}" style="padding:6px 15px; font-size:14px;" />
									</c:forEach>
									<c:forEach var="unitInfo" varStatus="s3" items="${cityLocalgovInfo.unitInfos}">
										<input class="white-gray-button noSelect" type="button" ids="${unitInfo.id}" value="${f:h(unitInfo.name)}" style="padding:6px 15px; font-size:14px;" />
									</c:forEach>
									</li>
								</c:forEach>
								</ul>
							</div>
						</td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<td width="100px"><%=lang.__("Message box")%></td>
					<td>
						<textarea name="thread_message_val_2" style="width:96%; height:100px;"></textarea>
					</td>
				</tr>
			</tbody>
		</table>
		<div style="font-size:16px; margin-top:10px; margin-bottom:4px;">
			<input type="checkbox" id="pageURLCheck"><label for="pageURLCheck"><%=lang.__("Paste opened page URL ")%></label>
		</div>
		<div style="margin:0 auto; width:100%; text-align:center">
			<input class="orange-brown-button" id="new_thread_create_button" onClick="sendNewThreadData()" type="button" value="<%=lang.__("Create")%>" style="margin:15px 5px 0 auto; width:45%;" />
			<input class="black-blue-button" id="new_thread_cancel_button" onClick="sendNewThreadCancel()" type="button" value="<%=lang.__("Cancel<!--3-->")%>" style="margin:15px auto 0 auto; width:45%;" />
		</div>
		</form>
	</div>
	<!-- 新規作成時に表示される領域ここまで -->
</div>
<input type="hidden" id="groupinfoid" value="${f:h(groupID)}">
<iframe style="width:0; height:0; border:none;" id="fileframe" name="fileframe"></iframe>

</body>
</html>
