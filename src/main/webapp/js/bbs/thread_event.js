/**************************************************
 * このjsには、掲示板機能のクリックイベントに関わる処理を記載
 * スレッドやレスの処理はthread_method.jsを参照の事
 **************************************************/

/********************************************************
 *  優先度設定のRadioボックスイベントに関する処理
 ********************************************************/

function priorityRadioEvent(){
	/* radioボタンのchangeイベントの設定( 新規用 ) */
	$(document).on('change','input[name=thread_priority_val]', function(){
		priorityChange($(this),"");
	});
	/* radioボタンのchangeイベントの設定( 編集用 ) */
	$(document).on('change','input[name=thread_priority_val_edit]', function(){
		priorityChange($(this),"_edit");
	});
	// 優先度変更ボタン
	$(document).on('click', '#editPriprityBtn', function(){
		setFieldDisplay("priority_edit");
	});
	// 優先度変更完了ボタン
	$(document).on('click', '#editPriorityEnterBtn', function(){
		var checkval = $("input:radio[name=thread_priority_val_edit]:checked").val()
		if(checkval == null) return;
		var senddata = "priorityEdit=" + checkval;
		editThreadDataAjax(senddata, "priority");
	});
	// 優先度変更を中止するボタン
	$(document).on('click', '#editPriorityCancelBtn', function(){
		$('#priorityRadio_edit').addClass('hide');
		$('#main_middle').removeClass('hide');
	});
	/* radioボタンのchangeイベントの設定( 編集用 ) ここまで */
}


/********************************************************
 *  スレッド取得に関する処理
 ********************************************************/
function setThreadListEvent(){
	/* 新規スレッド作成ボタン */
	$(document).on('click','#newThread-button',function(){
		// スレッドの内容部を全て閉じて、新規作成欄を表示する
		$('#main_top').addClass('hide');
		$('#main_middle').addClass('hide');
		$('#main_bottom').addClass('hide');
		$('#priorityRadio_edit').addClass('hide');
		$('#AddSendToGroupBtn_edit').addClass('hide');
		$('#new_threadmain').removeClass('hide');
		$('.threadmain').css({"visibility":"visible"});

		// 送信先のタブを作成
		// 一旦全て未選択状態にする(各スレッドの送信先追記機能で、ここのタブ実装をCloneしている関係で、各要素の添え字がずれてくる。)
		$(".tab li").css({"background-color":"#FFFFFF","color":"#000000"});
		$(".contents li").addClass("hide");

		$.each($('#thread_send_group_area').children('ul'),function(){
			// tabの初期設定
			if($(this).hasClass('tab')){
				$.each($(this).children('li'),function(){
					// 1つ目のみ変更する
					$(this).css({"background-color" : "#F58400", "color" : "#FFFFFF"});
					return false;
				});
			}
			// 中身の初期設定
			if($(this).hasClass('contents')){
				var cnt = 0;
				var own_groupId = $("#groupinfoid").val();
				$.each($(this).children('li'),function(){
					// 1つ目のみ変更する
					if(cnt == 0)
						$(this).removeClass("hide");
					$.each($(this).children('input'),function(){
						// 自身の班も表示するように要望があったので修正 2016/02/10
						if($(this).attr("ids") == own_groupId){
							$(this).removeClass('noSelect').removeClass('white-gray-button').addClass('white-red-button').addClass('selectOn');
						}else{
							// 未選択にする
							$(this).removeClass('white-red-button').removeClass('selectOn').addClass('noSelect').addClass('white-gray-button');
						}
					});
					cnt++;
				});
			}
		});
		// URLラジオボタンチェック
		$("#pageURLCheck").prop("checked",true);
	});

	/* 全スレッド表示用イベント */
	$(document).on('click', '#AllThead-button', function(){
		if($('#AllThead-button').val() == lang.__("Display all notice")){
			//getThreadListAjax('all_thread_flag=true');
			$('#AllThead-button').val(lang.__("Display only self-group's notice"));
		}else{
			$('#AllThead-button').val(lang.__('Display all notice'));
		}
		getThreadListAjax();
	});

	/* スレッド一覧からスレッドの中身を取得するイベント */
	$(document).on('click', '.threadListBox', function(){
		// ThreadIDを取得
		var threadID = $(this).attr("id");
		threadID = threadID.slice(threadID.indexOf("-")+1);
		// スレッド情報を取得する
		getThreadDataAjax(threadID, false);
	});

	// スレッド閉じるボタン
	$(document).on('click', '#threadCloseBtn', function(){
		if (confirm(lang.__('Are you sure to close notice?'))) {
			// データ送信
			editThreadDataAjax("closed=true", "closed");
		}
	});
	// スレッド閉じるボタン
	$(document).on('click', '#threadDeleteBtn', function(){
		if (confirm(lang.__('Delete?'))) {
			// データ送信
			editThreadDataAjax("deleted=true", "deleted");
		}
	});
}

/********************************************************
 *  ファイル送信、削除イベントに関する処理
 ********************************************************/
function setSendMessageEvent(){
	// メッセージ送信イベント
	$(document).on('click', '.message_send', function(){
		var message = $('#message_input_space').val();
		message = escapeHTML(message);
		if(message == ""){
			alert(lang.__("Input one or more character message."));
			return;
		}
		// URLを追加のチェックボックスがあればメッセージに追記する
		if($("#pageURLCheck_2").prop("checked")){
			message += window.parent.pageURLBBS();
		}
		setSendMessage(message);
	});

	// メッセージ入力欄のEnterキーイベント対応
	$(document).on('keydown', '#message_input_space', function(e){
		if(e.keyCode == 13){
			if(e.shiftKey){
				//e.preventDefault();
				return;
			}
			var message = $('#message_input_space').val();
			message = escapeHTML(message);
			if(message == ""){
				alert(lang.__("Input one or more character message."));
				$('#message_input_space').val('');
				return false;
			}else{
				// URLを追加のチェックボックスがあればメッセージに追記する
				if($("#pageURLCheck_2").prop("checked")){
					message += window.parent.pageURLBBS();
				}
				setSendMessage(message);
				// スレッド一覧の再取得
				//getThreadListAjax();
				// header.jspの未読数の更新
				//window.parent.unReadThreadBBS();
			}
		}
	});

	// 既読イベント
	$(document).on('click', '.message_check', function(){
		// 送信
		editThreadDataAjax("checkRead=true", "checkRead");
	});
}


/********************************************************
 *  ファイル送信、削除イベントに関する処理
 ********************************************************/
function setFileSendEvent(){
	// ファイル削除
	$(document).on('click', '.fileDeleteBtn', function(){
		fileDeleteAjax($(this).attr('ids'));
	});
}

/********************************************************
 *  送信先に関する処理
 ********************************************************/
function setSendToEvent(){
	// 送信先選択用タブのクリックイベント
	$(document).on('click', '.tab li', function(){
		// 要素の番号取得
		var index = $(".tab li").index(this);
		// 一旦背景色を全てグレーにする
		$(".tab li").css({"background-color":"#FFFFFF","color":"#000000"});
		// 一旦全てHidden
		$(".contents li").addClass("hide");
		// 選択された番号のコンテンツを表示
		$(".contents li").eq(index).removeClass("hide");
		// 背景色を選択色にする
		$(".tab li").eq(index).css({"background-color" : "#F58400", "color" : "#FFFFFF"});
	});
	// 送信先選択用ボタンで未選択⇒選択となった場合のイベント
	$(document).on('click', '.noSelect', function(){
		$(this).removeClass("noSelect").addClass("selectOn");
		$(this).removeClass("white-gray-button").addClass("white-red-button");
	});
	// 送信先選択用ボタンで選択⇒未選択となった場合のイベント
	$(document).on('click', '.selectOn', function(){
		var own_groupId = $("#groupinfoid").val();
		// 自身の班も表示するように要望があったので修正 2016/02/10
		if($(this).attr("ids") == own_groupId) return;
		$(this).removeClass("selectOn").addClass("noSelect");
		$(this).removeClass("white-red-button").addClass("white-gray-button");
	});
	// 送信先全選択
	$(document).on('click', '.priorityAllSelect', function(){
		var parent = $(this).parent().parent();
		parent.children("input").each(function(val){
			if($(this).hasClass("noSelect")){
				$(this).removeClass("noSelect").addClass("selectOn");
				$(this).removeClass("white-gray-button").addClass("white-red-button");
			}
		});
	});
	// 送信先全解除
	$(document).on('click', '.priorityAllCancel', function(){
		var own_groupId = $("#groupinfoid").val();

		var parent = $(this).parent().parent();
		parent.children("input").each(function(val){
			if($(this).hasClass("selectOn")){
				if($(this).attr("ids") == own_groupId) return;
				$(this).removeClass("selectOn").addClass("noSelect");
				$(this).removeClass("white-red-button").addClass("white-gray-button");
			}
		});
	});

	// 送信先追加ボタン
	$(document).on('click', '#AddSendToGroupBtn', function(){
		$('#main_middle').addClass('hide');
		$('#priorityRadio_edit').addClass('hide');
		// 送信先ボタンを何度も押されると再生成されてしまうので一旦消す
		$('#AddSendToGroupBtn_edit').empty();
		// 新規作成用の領域から、送信先のデータをコピー
		$('#thread_send_group_area ul.tab').clone().appendTo($('#AddSendToGroupBtn_edit'));
		$('#thread_send_group_area ul.contents').clone().appendTo($('#AddSendToGroupBtn_edit'));
		// 1番左のタブを選択状態にして初期化
		$(".tab li").css({"background-color":"#FFFFFF","color":"#000000"});
		$(".contents li").addClass("hide");
		// タブが複数になるので、選択済班を格納する配列を別に作成する
		var selectedGroupIdArr = new Array();
		for(var i = 0; i < sendMemberId.length; i++){
			selectedGroupIdArr.push(sendMemberId[i]);
		}
		// すでに送信先に設定されているグループの色を変更する
		$.each($('#AddSendToGroupBtn_edit').children('ul'),function(){
			// tabの初期設定
			if($(this).hasClass('tab')){
				$.each($(this).children('li'),function(){
					// 1つ目のみ変更する
					$(this).css({"background-color" : "#F58400", "color" : "#FFFFFF"});
					return false;
				});
			}
			// 中身の初期設定
			if($(this).hasClass('contents')){
				var cnt = 0;
				$.each($(this).children('li'),function(){
					// 1つ目のみ表示する
					if(cnt == 0)
						$(this).removeClass("hide");
					$.each($(this).children('input'),function(){
						for(var i = 0; i < selectedGroupIdArr.length; i++){
							if($(this).attr('ids') == selectedGroupIdArr[i]){
								// すでに登録済みグループは変更出来ないように修正
								$(this).removeClass("white-gray-button").removeClass("noSelect").addClass("white-red-button");
								selectedGroupIdArr.splice(i,1);
								break;
							}else{
								$(this).removeClass('selectOn').removeClass('white-red-button').addClass('noSelect').addClass('white-gray-button');
							}
						}
					});
					cnt++;
				});
			}
		});
		// 変更 and キャンセルボタンの生成
		var divbox = $('<div>').css({"text-align":"center"}).appendTo($('#AddSendToGroupBtn_edit'));
		$('<input>').addClass('orange-brown-button').css({"margin":"15px 10px 0 auto", "width":"30%"}).attr({"type":"button","id":"AddSendToGroupEnterBtn"}).val(lang.__("Change<!--2-->")).appendTo(divbox);
		$('<input>').addClass('black-blue-button').css({"margin":"15px auto 0 auto", "width":"30%"}).attr({"type":"button","id":"AddSendToGroupCancelBtn"}).val(lang.__("Cancel<!--3-->")).appendTo(divbox);

		// 変更ページを表示する
		$('#AddSendToGroupBtn_edit').removeClass('hide');

	});

	// 送信先追加完了ボタン
	$(document).on('click', '#AddSendToGroupEnterBtn', function(){
		var dummyform_val = $('<form>').attr({"id":"dummyform"}).appendTo("body");
		// 送信先に設定したグループを取得してFormに登録する
		$.each($('#AddSendToGroupBtn_edit').children('ul'),function(){
			if($(this).hasClass('contents')){
				$.each($(this).children('li'),function(){
					$.each($(this).children('input'),function(){
						if($(this).hasClass('selectOn')){
							$('<input>').attr({type:"hidden",name:"sendMemberEdit",value:$(this).attr('ids')}).appendTo(dummyform_val);
						}
					});
				});
			}
		});
		// Formの値を取得
		var formdata = dummyform_val.serialize();
		// 先ほど追加したFormを削除
		dummyform_val.remove();
		// データ送信
		editThreadDataAjax(formdata, "sendmember");
	});
	// 送信先追加キャンセルボタン
	$(document).on('click', '#AddSendToGroupCancelBtn', function(){
		$('#AddSendToGroupBtn_edit').addClass('hide');
		$('#AddSendToGroupBtn_edit').empty();
		$('#main_middle').removeClass('hide');
	});
}


