/**************************************************
 * このjsには、掲示板機能のスレッドやレスポンスに関わる処理を記載
 * 各イベント処理はthread_event.jsを参照の事
 **************************************************/

/* スレッドの内容を右側の領域に生成 */
function setThreadData(datas){
	if(datas == null) return;
	// タイトルの表示
	$('#threadtitleP').html(escapeHTML(datas.title));
	// スレッド情報の表示
	var threadinfo = lang.__("Creation date and time:") + datas.registTime + "<br>" + lang.__("Create:") + datas.registGroupName + " " + lang.__("Send to:") + datas.sendtoMember;
	// 送信先グループのIDをグローバル変数に格納
	var memberid = datas.sendtoMemberID;
	sendMemberId = memberid.split(",");
	$('#threadInfoDiv').html(threadinfo);
	// スレッドが閉じられている or 自身が送信先に含まれていない場合は各種ボタンを非表示
	if(datas.closed == true || datas.sendtoMe == false){
		//$('#threadmain_top_menu').css({"visibility":"hidden"});
		$('#AddSendToGroupBtn').css({"visibility":"hidden"});
		$('#editPriprityBtn').css({"visibility":"hidden"});
		$('#threadCloseBtn').css({"visibility":"hidden"});
		if(datas.sendtoMe == false){
			$('.message_upload').css({"visibility":"hidden"});
			$('.message_space').css({"visibility":"hidden"});
			$('.message_send').css({"visibility":"hidden"});
			$('.message_check').css({"visibility":"hidden"});
		}else{
			$('.message_upload').css({"visibility":"visible"});
			$('.message_space').css({"visibility":"visible"});
			$('.message_send').css({"visibility":"visible"});
			$('.message_check').css({"visibility":"visible"});
		}
		if(datas.admin == true){
			$('#threadmain_top_menu').css({"display":"block"});
		}else{
			$('#threadmain_top_menu').css({"display":"none"});
		}
	}else{
		// 連絡を閉じるボタンの表示/非表示
		if(datas.ownCreateThread == true){
			$('#threadmain_top_menu').css({"display":"block"});
			$('#AddSendToGroupBtn').css({"visibility":"visible"});
			$('#editPriprityBtn').css({"visibility":"visible"});
			$('#threadCloseBtn').css({"visibility":"visible"});
		}else{
			if(datas.admin != true){
				$('#threadmain_top_menu').css({"display":"none"});
			}
			$('#AddSendToGroupBtn').css({"visibility":"hidden"});
			$('#editPriprityBtn').css({"visibility":"hidden"});
			$('#threadCloseBtn').css({"visibility":"hidden"});
		}
		$('.message_upload').css({"visibility":"visible"});
		$('.message_check').css({"visibility":"visible"});
		$('.message_space').css({"visibility":"visible"});
		$('.message_send').css({"visibility":"visible"});
	}
	if(datas.admin == true){
		$('#threadDeleteBtn').css({"visibility":"visible"});
	}else{
		$('#threadDeleteBtn').css({"display":"none"});
	}
	// スレッドの更新時間
	$('#threadUpdateTime').val(datas.latestTime);
	// スレッドのID
	$('#threaddataid').val(datas.id);
	$('#fileform_id').val(datas.id);
	// スレッドの中身を生成
	$('#main_middle').empty();
	setResponseBox(datas.responseData);
	// スレッドを表示する
	$('#main_top').removeClass('hide');
	$('#main_middle').removeClass('hide');
	$('#main_bottom').removeClass('hide');
	$('#priorityRadio_edit').addClass('hide');
	$('#AddSendToGroupBtn_edit').addClass('hide');
	$('#new_threadmain').addClass('hide');
	$('.threadmain').css({"visibility":"visible"});
	// 優先度変更機能の為に、裏でPriorityをセットする
	$("#priorityRadio_edit input:radio[name=thread_priority_val_edit][value=" + datas.priority + "]").click();
}



/********************************************************
 *  新規スレッドに関する処理
 ********************************************************/
// 新規スレッド入力欄で記載した内容を消去する
function sendNewThreadCancel(){
	// 表示を隠す
	$('#new_threadmain').addClass('hide');
	// 送信先ボタンの選択状態を戻す
	$.each($(".selectOn"), function(){
		$(this).removeClass("selectOn").addClass("noSelect");
		$(this).removeClass("white-red-button").addClass("white-gray-button");
	});
	// 送信先タブを一旦背景色を全てグレーにする
	$(".tab li").css({"background-color":"#FFFFFF","color":"#000000"});
	// 一旦全てHidden
	$(".contents li").addClass("hide");
	// 選択された番号のコンテンツを表示
	$(".contents li").eq(0).removeClass("hide");
	// 背景色を選択色にする
	$(".tab li").eq(0).css({"background-color" : "#F58400", "color" : "#FFFFFF"});
	// 優先度を設定無しに設定
	$("#priorityRadio input:radio[name=thread_priority_val][value=4]").click();
	// タイトルを初期値にする
	$('input:text[name=thread_title_val]').val(thread_default_title);
	// メッセージ欄を空にする
	$('textarea[name=thread_message_val_2]').val('');
}


/********************************************************
 *  メッセージ表示欄に関する処理
 ********************************************************/
// スレッドの投稿内容を生成
function setResponseBox(res){
	// 未読状態のものを既読状態にする
	$.each($('#main_middle').children('div'), function(){
		if($(this).hasClass('threadResponse_unCheck')){
			$(this).removeClass('threadResponse_unCheck').addClass('threadResponse');
		}
	});
	// 送信メッセージ領域の計算用
	var mes_box_height = $('#main_middle').height();
	$.each($('#main_middle').children('div'), function(){
		$.each($(this).children('div'), function(){
			if($(this).hasClass('threadResponse') || $(this).hasClass('threadResponse_unCheck')){
				mes_box_height += $(this).height();
			}
		});
	});

	// 送信Boxの作成
	for(var i = 0; i < res.length; i++){
		// 親Div
		var parentDiv = $('<div>').css({"width":"100%"}).appendTo('#main_middle');
		//var mes = escapeHTML(res[i].message);
		var mes = res[i].message;
		mes = br_change(mes);
		// 班の表示は１段下に書く
		//var groupnameLine = "<br>(" + res[i].groupname + ")";
		var groupnameLine = "<br><span style='color:"+(res[i].unRead?'#993333':'#4F81BD')+"'>(" + res[i].groupname + ")</span>";
		mes += groupnameLine;

		var divs;
		// 未読確認
		var uncheck = "";
		if(res[i].unRead) uncheck = "_unCheck";

		if(res[i].fileflag){
			// ファイルパス生成
			var file_a = $('<a>').html(res[i].message).attr({"href":escapeHTML(res[i].message), "target":"_blank"});
			var file_link = $('<span>').html(groupnameLine);
			// 自班が投稿したファイルなら削除ボタンを設置
			if(res[i].ownMessage){
				var deleteBtn = $('<input>').attr({"type":"button","ids":res[i].id})
					.addClass('black-blue-button').addClass('fileDeleteBtn').val(lang.__('Delete'))
					.css({"padding":"4px 8px","margin-left":"5px"});
				divs = $('<div>').addClass('threadResponse' + uncheck).append(file_a).append(deleteBtn).append(file_link).appendTo(parentDiv);
			}else{
				divs = $('<div>').addClass('threadResponse' + uncheck).append(file_a).append(deleteBtn).append(file_link).appendTo(parentDiv);
			}
		}else{
			// pageURL対応 keywordに括られたアドレスをボタンにして表示する
			if(mes.indexOf("{keywordurlbbs}") != -1){
				var mes_mes = res[i].message.split("{keywordurlbbs}");
				if(mes_mes.length > 1){
					//var cssClass = "orange-brown-button";
					var cssClass = "bbs-event-button"; // 17/06/15 依頼によりオレンジボタンからグレーに変更
					var pageurl = "<a href='" + mes_mes[1] + "' target='_parent'><input type='button' class='"+cssClass+"' style='font-size:11px; padding:6px 12px; margin-left:10px;' value='" + lang.__("Open URI") + "'></a>";
					divs = $('<div>').addClass('threadResponse' + uncheck).html(mes_mes[0] + groupnameLine+"<br>" + pageurl).appendTo(parentDiv);
				}else{
					// この内容は有り得ない
					divs = $('<div>').addClass('threadResponse' + uncheck).html(mes).appendTo(parentDiv);
				}
			}else{
				divs = $('<div>').addClass('threadResponse' + uncheck).html(mes).appendTo(parentDiv);
			}
		}
		if(!res[i].ownThread){
			// 右寄せ
			divs.addClass("positionR").css({"float":"right"});
			$('<div>').html(res[i].registtime).addClass('threadResponseDate_R').css({"padding-top":divs.height()/2}).appendTo(parentDiv);
		}else{
			divs.css({"float":"left"});
			$('<div>').html(res[i].registtime).addClass('threadResponseDate_L').css({"padding-top":divs.height()/2}).appendTo(parentDiv);
		}
		mes_box_height += divs.height() + 12;
	}
	if(res.length> 0){
		var boxpix_cnt = $('#main_middle').children('div').length + 1;
		//$('#main_middle').scrollTop(40 * boxpix_cnt + $('#main_middle').height());
		$('#main_middle').scrollTop(mes_box_height);
	}
}


/********************************************************
 *  優先度に関する処理
 ********************************************************/
// 優先度設定用Radioボタンのイベント関数 (selには空文字か_editが入る)
function priorityChange(thisid ,sel){
	// 一旦全ての背景色用Classを外す
	for(var i = 1; i <= 4; i++){
		$('#priority'+ i +'_label' + sel).removeClass("label_red").removeClass("label_orange").removeClass("label_yellow").removeClass("label_white");
		$('#priority'+ i +'_label' + sel).css({"background":"#E6E6E6","color":"#555"})
	}
	// valueによって背景色を変更する
	var val = thisid.val();
	if     (val == "1"){ $('#priority1_label' + sel).css({"background-color":"#FF0000","color":"#FFFFFF"}).addClass('label_red');}
	else if(val == "2"){ $('#priority2_label' + sel).css({"background-color":"#ff9900","color":"#FFFFFF"}).addClass('label_orange');}
	else if(val == "3"){ $('#priority3_label' + sel).css({"background-color":"#FFFF00","color":"#000000"}).addClass('label_yellow');}
	else if(val == "4"){ $('#priority4_label' + sel).css({"background-color":"#FFFFFF","color":"#000000"}).addClass('label_white');}
	$('#priorityRadio' + sel).buttonset();
}


/********************************************************
 *  表示切替に関する処理
 ********************************************************/
// 各イベント時における表示領域のON / OFF
function setFieldDisplay(param){
	// スレッドの優先度変更ボタンクリック時
	if(param == "priority_edit"){
		// メッセージ欄と送信先追加入力欄を閉じる
		$('#main_middle').addClass('hide');
		$('#AddSendToGroupBtn_edit').addClass('hide');
		// 追加のみとする為、新規作成用とは別の優先度領域を表示
		$('#priorityRadio_edit').removeClass('hide');
	}
}

/********************************************************
 *  未読の数を表示する処理
 ********************************************************/
// 掲示板表示した際の各スレッドに載る数字
function badger_copy(doms, ids, param){
	var outerDiv = $('<div>').addClass('badger-outter').attr({"id":"Badger_" + ids});
	var innerDiv = $('<div>').addClass('badger-inner').appendTo(outerDiv);
	var innerP = $('<p>').addClass('badger-badge').attr({"id":"Badger_p_" + ids}).html(param).appendTo(innerDiv);
	doms.css({"position":"relative"});
	doms.append(outerDiv);
}
function badger_copy_brown(doms, ids, param){
	var outerDiv = $('<div>').addClass('badger-outter').attr({"id":"Badger_" + ids});
	var innerDiv = $('<div>').addClass('badger-inner-brown').appendTo(outerDiv);
	var innerP = $('<p>').addClass('badger-badge').attr({"id":"Badger_p_" + ids}).html(param).appendTo(innerDiv);
	doms.css({"position":"relative"});
	doms.append(outerDiv);
}
// header.jspで使用している関数
function badger_copy_header(doms, ids, param){
	var outerDiv = $('<div>').addClass('badger-outter_header').attr({"id":"Badger_" + ids});
	var innerDiv = $('<div>').addClass('badger-inner_header').appendTo(outerDiv);
	var innerP = $('<p>').addClass('badger-badge_header').attr({"id":"Badger_p_" + ids}).html(param).appendTo(innerDiv);
	doms.css({"position":"relative"});
	doms.append(outerDiv);
}
