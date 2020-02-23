/******************  訓練パネルのクリックイベント関連  *******************************/

function trainingPlanClickEvents(){

	// ボタンクリックイベント(全般)
	btnClickEvent();
	// 左手リスト関連のイベント
	leftListEvent();


}


/**
 * ボタンクリックイベント関連(全般)
 */
function btnClickEvent(){
	// 新規訓練プラン作成ボタン
	$(document).on('click', '#newPlanBtn', function(){
		// IDは0で統一
		$("*[name=planid]").val("0");
		// プランが何も作成されていない時に表示される説明文を外す
		if($("#noPlanLi")[0]){
			$("#noPlanLi").remove();
		}
		// Liを追加する
		var Lis = $("<li>").attr({"ids":"plan_0", "name":"plan_0", "id":"newPlanLi"}).html(lang.__("New training plan")).addClass("selectRed").appendTo("#leftPlanList");
		// 基本設定が登録出来るまで他のメニューは表示しない
		$("<dl>").addClass("leftDL").attr({"ids":"plan_0","pat":"basic"}).html(lang.__("Basic Settings")).addClass("selectRed").appendTo(Lis);
		// 一覧表示領域を非表示
		$("#planListDiv").css({"display":"none"});
		// 基本設定の通知機能を全てONで初期化
		//$.each( $(".white-gray-button"), function(){
		$.each( $(BTN_OFF), function(){
			if($(this).attr("pat") == "notice"){
				//$(this).removeClass("white-gray-button").addClass("orange-brown-button").html("ON");
				$(this).removeClass(BTN_OFF).addClass(BTN_ON).html("ON");
			}
		});
		// 訓練名と概要欄を空にする
		$("#planName").val("");
		$("#planNote").val("");
		// 基本設定登録用の領域を表示
		$("#planBasicDiv").css({"display":"block"});
	});

	// プラン編集ボタン
	$(document).on('click', '.planEditBtn', function(){
		var planid = $(this).attr("planid");
		if(planid != null){
			locationHrefURL(planid, "basic");
		}
	});
	// プラン複製ボタン
	$(document).on('click', '.planCopyBtn', function(){
		if(!window.confirm(lang.__("Are you sure that you want to copy the training plan"))){
			return false;
		}
		var planid = $(this).attr("planid");
		if(planid != null){
			locationHrefURL(planid, "copy");
		}
	});
	// プラン削除ボタン
	$(document).on('click', '.planDeleteBtn', function(){
		var ids = $(this).attr("id");
		// 空文字なら何もしない
		if(ids.length == 0) return;
		if(ids.indexOf("plan_delete_") == -1) return;
		// plan_delete_以降の文字を切り出し
		var planid = ids.substring(12, ids.length);
		if(planid.length > 0){
			$("*[name=planid]").val(planid);
			// Dialog生成
			var bodys = $('<div>').css({"text-align":"center"});
			$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){trainingplanDataDelete('ok');}).html("O K").appendTo(bodys);
			$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){trainingplanDataDelete('cancel');}).html("Cancel").appendTo(bodys);
			alertDialogOpen(lang.__("Deletion confirmation of training plan"),lang.__("Are you sure to delete training plan?"),bodys, BG_BLUE, 150, 350);
		}
	});


	// 基本設定の保存ボタン
	$(document).on('click', '#planBasicSaveBtn', function(){
		var trainingName = $("#planName").val();
		// ダイアログの中身枠
		var bodys = $('<div>').css({"text-align":"center"});
		if(trainingName == "" || trainingName == undefined){
			$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){alertDialog.dialog("close");}).html("O K").appendTo(bodys);
			// 赤背景
			alertDialogOpen(lang.__("[Unconfirmed] Training plan name"),lang.__("Plan name is required."), bodys, BG_RED, 150, 350);
			return;
		}
		// Dialog生成
		$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){planBasicData('ok');}).html("O K").appendTo(bodys);
		$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){planBasicData('cancel');}).html("Cancel").appendTo(bodys);
		// タイトル背景は青
		alertDialogOpen(lang.__("Confirmation to save basic settings"),lang.__("Are you sure to save basic settings?"),bodys, BG_BLUE, 150, 350);
	});

	// 基本設定のキャンセルボタン
	$(document).on('click', '#planBasicCancelBtn', function(){
		location.href = URLS;
	});

	// ON / OFF を切り替えるボタンイベント
	$(document).on('click', '.on-off-btn', function(){
		console.log("On Off Button " + $(this).html());
		// ON / OFFを切り替える
		if($(this).html() == "ON"){
			$(this).removeClass(BTN_ON).addClass(BTN_OFF).html("OFF");
			$(this).parent().removeClass("textLeft").addClass("textRight");
		}
		else if($(this).html() == "OFF"){
			console.log("off = " + $(this).html());
			$(this).removeClass(BTN_OFF).addClass(BTN_ON).html("ON");
			$(this).parent().removeClass("textRight").addClass("textLeft");
		}
	});

	// 訓練実施用ボタン
	$(document).on('click', '.trainingBtn', function(){
		// 自治体IDの部分のみ切り出す
		var attr_ids = $(this).attr("id");
		if(attr_ids.indexOf("controlBtn_") == -1) return;
		var ids = attr_ids.substring(attr_ids.indexOf("_") + 1, attr_ids.length);

		// ダイアログの中身枠
		var bodys = $('<div>').css({"text-align":"center"});
		$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){doTraining('ok',ids);}).html("O K").appendTo(bodys);
		$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){doTraining('cancel',ids);}).html("Cancel").appendTo(bodys);
		// タイトル背景は青
		alertDialogOpen(lang.__("Confirm training mode"), lang.__("Are you sure to shift training mode?"),bodys, BG_BLUE, 150, 350);
	});

	$(document).on('click', '#trainingControlBtnAll', function(){
		// ダイアログの中身枠
		var bodys = $('<div>').css({"text-align":"center"});
		$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){doTrainingAll('ok');}).html("O K").appendTo(bodys);
		$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){doTrainingAll('cancel');}).html("Cancel").appendTo(bodys);
		// タイトル背景は青
		alertDialogOpen(lang.__("Confirm training mode"), lang.__("Are you sure to shift peace mode to training mode?"),bodys, BG_BLUE, 150, 350);
	});

}


function doTraining(param, ids){
	// 開いていたダイアログを閉じる
	alertDialog.dialog("close");
	if(param != "ok") return;

	// データ送信
	var ajaxData = new GetDatas(URLS + "doTraining", "localgovinfoid=" + ids + "&trainingplandataid=" + $("*[name=planid]").val());
	// 表示を空にする
	$("#trainingMode_" + ids).html("");
	$("<div>").attr("id","modeChange").html(lang.__("Training mode changing")).appendTo($("#trainingMode_" + ids));

	changeTrainingMode(ajaxData);
}

function doTrainingAll(param){
	// 開いていたダイアログを閉じる
	alertDialog.dialog("close");
	if(param != "ok") return;

	var localgovList = [];

	$.each($(".trainingBtn"), function(){
		var ids = $(this).attr("id");
		if(ids.indexOf("controlBtn_") != -1){
			var localgovinfoid = ids.substring( ids.indexOf("controlBtn_") + 11, ids.length);
			localgovList.push(localgovinfoid);
		}
	});
	// 処理中の表示に変更
	for(var i = 0; i < localgovList.length; i++){
		// 表示を空にする
		$("#trainingMode_" + localgovList[i]).html("");
		$("<div>").html(lang.__("Waiting for process of shift to training mode..")).appendTo($("#trainingMode_" + localgovList[i]));
	}
	var dfd = (new $.Deferred()).resolve();
	for(var i = 0; i < localgovList.length; i++){
		// 表示を空にする
		$("#trainingMode_" + localgovList[i]).html("");
		$("<div>").attr("id","modeChange").html(lang.__("Training mode changing")).appendTo($("#trainingMode_" + localgovList[i]));
		// Ajaxの処理をchainする
		var ajaxData = new GetDatas(URLS + "doTraining", "localgovinfoid=" + localgovList[i] + "&trainingplandataid=" + $("*[name=planid]").val());
		dfd = dfd.then( function(){ return changeTrainingMode(ajaxData); });
	}
}

function changeTrainingMode(ajaxData){
	var dfd_method = new $.Deferred();

	var dfd = ajaxData.doAjax();

	// 実行中は文字列を点灯させる
	setTimeout(function sleeps(){
		if(dfd.state() === "pending"){
			$("#modeChange").fadeOut(1000, function(){$(this).fadeIn(1000)});
			setTimeout(sleeps, 2000);
		}
	});
	dfd.done(function(response){
		datas = eval(response);
		// 返却されたモード名を表示
		$("#trainingMode_" + datas.localgovinfoid).html(datas.trainingModeName);
		// 移行ボタンを削除する
		$("#controlBtn_" + datas.localgovinfoid).remove();
		if(datas.error != ""){
			ajaxError(datas.error);
		}
		// 関数終了を伝える
		dfd_method.resolve();
	})
	.fail(function(uqXHR, textStatus, errorThrow){
		dfd_method.reject();
		$("#modeChange").parent().html(lang.__("Peacetime"));
		// 独自ダイアログを使用してエラー表示
		ajaxError(" " + lang.__("Failed to start training mode.") + " ");
	});

	return dfd_method.promise();
}



/**
 * スリープ関数

var sleeps = function(){
	return (function(){
		var dfd = $.Deferred();
		//setTimeout(function(){ dfd.resolve(); }, time);
		setInterval(function(){
			$("#modeChange").fadeOut(1000, function(){$(this).fadeIn(1000)});
		}, 2000);
		return dfd.promise();
	});
}

function loop_sleep(){
	var dfd = $.Deferred();
	dfd.then
}
 */

/**
 * 左手リスト表示に関するイベント
 */
function leftListEvent(){
	// 左側のプラン内容メニューのイベント
	$(document).on('click', '.leftDL', function(){
		// pat情報を取得
		var patName = $(this).attr("pat");
		// クリックしたDL要素のクラスを確認する
		if($(this).hasClass("selectRed")){
			// 防災情報XML編集画面を表示中だった場合
			if($("#xmlEditorDiv").css("display") === 'block'){
				// 防災情報XML編集画面は非表示
				$('#xmlEditorDiv').css({'display':'none'});

				// クリックしたDL要素のページを再表示
				// まず開いているページを閉じる
				leftDLChange();
				// 選択色にする
				$(this).removeClass("noSelectGray").addClass("selectRed");
				// クリックした要素のページを開く
				planDispPage(patName);
			}
			// 現在開いているページなので何もしない
			return;
		}else{
			// 防災情報XML編集画面を表示中だった場合
			if($("#xmlEditorDiv").css("display") === 'block'){
				// 防災情報XML編集画面は非表示
				$('#xmlEditorDiv').css({'display':'none'});
				// 右パネルを一旦全て非表示
				$("#planListDiv").css({"display":"none"});
				$("#planBasicDiv").css({"display":"none"});
				$("#planExternalDataDiv").css({"display":"none"});
				$("#planControlDiv").css({"display":"none"});
			}

			// まず開いているページを閉じる
			leftDLChange();
			// 選択色にする
			$(this).removeClass("noSelectGray").addClass("selectRed");
			// クリックした要素のページを開く
			planDispPage(patName);
		}
	});

	// 左側のプラン名称のイベント
	$(document).on('click', '#leftPlanList li', function(){
		// 子のdl要素クリックでも反応してしまうので処理追加
		if($(this).hasClass("selectRed")){

			// 防災情報XML編集画面を表示中だった場合
			if($("#xmlEditorDiv").css("display") === 'block'){
				// 防災情報XML編集画面は非表示
				$('#xmlEditorDiv').css({'display':'none'});

				// 今選択状態の子DL要素のページを表示
				var dls = $(this).children();
				for(var i=0; i < dls.length; i++) {
					var dl = dls.eq(i);
					if($(dl).hasClass('selectRed')){
						var patName = $(dl).attr("pat");
						planDispPage(patName);
						break;
					}
				}
			}
			return;
		}
		// 防災情報XML編集画面を表示中だった場合は非表示にする
		if($("#xmlEditorDiv").css("display") === 'block'){
			$('#xmlEditorDiv').css({'display':'none'});
		}

		console.log("aaa");
		// idを取得する
		var planid = $(this).attr("id");
		if(planid.indexOf("planid_") == -1){
			console.log(planid);
			return;
		}else{
			// IDを整形する
			planid = planid.substring(planid.indexOf("planid_") + 7, planid.length);
			console.log("else is " + planid);
			// 編集画面を表示するURLを実行する
			locationHrefURL(planid, "basic");
		}
	});
}


/**
 *  左手リストの選択済ページ色を元に戻す関数
 */
function leftDLChange(){
	$.each( $(".leftDL"), function(){
		if($(this).hasClass("selectRed")){
			var patName = $(this).attr("pat");
			// 現在開いているページを閉じる
			$(this).removeClass("selectRed").addClass("noSelectGray");
			if(patName == "basic"){
				// 基本設定画面を閉じる
				$("#planBasicDiv").css({"display":"none"});
			}else if(patName == "external"){
				// 外部データ設定画面を閉じる
				$("#planExternalDataDiv").css({"display":"none"});
			}else if(patName == "control"){
				$("#planControlDiv").css({"display":"none"});
			//}else if(patName == "restore"){
				//$("#planDataRestoreDiv").css({"display":"none"});
			}
		}
	});
}


/******************  訓練パネルの自作Dialog表示関数全般  *******************************/

/**
 * 自作Dialog表示用関数
 */
function alertDialogOpen(title, message, bodys, titlecolor, hgt, wid){
	alertDialog = $('#alert-dialog').dialog({
		title: title,
		modal:true,
		height:hgt,
		width:wid,
		buttons: {}
	});
	$("#alert-dialog-message").html(message);
	if(bodys != ""){
		$("#alert-dialog-body").html(bodys);
	}
	// DialogのTitle部のカラーを替える
	if(titlecolor != ""){
		$.each($(".ui-dialog-title"), function(){
			if($(this).html() == title){
				$.each($(this).parent("div"), function(){
					$(this).css({"background-color":titlecolor, "background-image":"none"});
				});
			}
		});
	}
}

// Dialogを閉じる用
function dialogCancel(){
	alertDialog.dialog("close");
}

// Dialog error表示用
function ajaxError(messages){
	// ダイアログでエラー内容を表示
	var bodys = $('<div>').css({"text-align":"center"});
	$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){alertDialog.dialog("close");}).html("O K").appendTo(bodys);
	// 赤背景
	alertDialogOpen(lang.__("[Error]"), messages, bodys, "rgba(240,0,0,0.5)", 160, 380);
}

// Dialog 処理成功用
function dialogSuccess(titles, messages){
	var bodys = $('<div>').css({"text-align":"center"});
	$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){alertDialog.dialog("close");}).html("O K").appendTo(bodys);
	alertDialogOpen(titles, messages, bodys, "rgba(50,100,255,0.5)", 170, 300);
}

// プランの各ページ表示用関数
function planDispPage(patName){
	// クリックした要素のページを開く
	if(patName == "external"){
		// planidを元に外部データ情報を生成する
		$("#planExternalDataDiv").css({"display":"block"});
	}else if(patName == "control"){
		$("#planControlDiv").css({"display":"block"});
	//}else if(patName == "restore"){
		//$("#planDataRestoreDiv").css({"display":"block"});
	}else{
		$("#planBasicDiv").css({"display":"block"});
	}
}


/******************  訓練外部ファイル設定のクリックイベント関連  *******************************/

function externalEvent(){

	// 外部データ新規登録用ダイアログ
	$(document).on('click', '#newExternalDataBtn', function(){
		// 新規登録時はmeteoid=0にセット
		$("#meteoid").val("0");
		externalDialogOpen(0);
	});

	// 外部データ表示イベント
	$(document).on('click', '.meteoDispBtn', function(){
		window.open($(this).attr("meteoURL"), "_blank");
	});

	// 外部データ編集イベント
	$(document).on('click', '.meteoEditBtn', function(){
		var ids = $(this).attr("id");
		ids = ids.substring(ids.indexOf("meteo_edit") + 10, ids.length);
		var meteotypeid = $(this).attr("meteotype");
		var meteoURL = $(this).attr("meteoURL");
		meteoURL = meteoURL.substring(meteoURL.lastIndexOf("/") + 1, meteoURL.length);
		$("#meteoid").val(ids);
		$("#meteoName").val($("#meteo_name" + ids).html());
		$("*[name=meteoNote]").val($("#meteo_note" + ids).html());
		$("#meteoType").val(meteotypeid);
		$("#meteo_url_filename").html(meteoURL);
		externalDialogOpen(ids);
	});

	// サーバ上防災情報XMLファイル選択用ダイアログ
	$(document).on('click', '#bousaixmlfile_select_button', function(){
		externalBousaixmlfilesDialogOpen();
	});

	// 外部データ削除イベント
	$(document).on('click', '.meteoDeleteBtn', function(){
		var ids = $(this).attr("id");
		ids = ids.substring(ids.indexOf("meteo_delete") + 12, ids.length);
		var meteoName = $("#meteo_name" + ids).html();
		$("#meteoid").val(ids);

		// ダイアログの中身枠
		var bodys = $('<div>').css({"text-align":"center"});
		$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){externalDataDelete('ok');}).html("O K").appendTo(bodys);
		$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){externalDataDelete('cancel');}).html("Cancel").appendTo(bodys);
		// タイトル背景は青
		alertDialogOpen(lang.__("Deletion confirmed of external data settings"),meteoName + lang.__(" will be deleted. Are you sure?"),bodys, BG_BLUE, 150, 350);
	});

	// 気象情報種別のイベント
	$(document).on('change', '#meteoType', function(){
		var ajaxData = new GetDatas(URLS + "checkMeteoRequestInfo", "checkMeteoTypeid="+$("*[name=meteoType]").val());
		ajaxData.doAjax().done(function(response){
			datas = eval(response);
			if(datas.message != ""){
				$("#checkMeteoDataMessage").html(datas.message);
				$(".checkMeteoRow").css({"display":"block"});
			}else{
				$("#checkMeteoDataMessage").html("");
				$(".checkMeteoRow").css({"display":"none"});
			}
		})
		.fail(function(uqXHR, textStatus, errorThrow){
			// 独自ダイアログを使用してエラー表示
			ajaxError(" " + lang.__("Failed to check weather info type.") + " ");
		});
	});

}

	// 外部ファイル発信ボタン（個別）
function sendExternalFile(meteoid, localgovinfoid){
	// ダイアログの中身枠
	var bodys = $('<div>').css({"text-align":"center"});
	$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){sendExternalFile_ok(meteoid,localgovinfoid);}).html("O K").appendTo(bodys);
	$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){dialogCancel();}).html("Cancel").appendTo(bodys);

	alertDialogOpen(lang.__("Send external data"), lang.__("Are you sure to send external data?"), bodys, BG_BLUE, 150, 350);

	//dialogConfirm("外部データを送信", "外部データを配信しますか？", sendExternalFile_ok(meteoid,localgovinfoid), dialogCancel());
}

function sendExternalFileAll(meteoid){
	// ダイアログの中身枠
	var bodys = $('<div>').css({"text-align":"center"});
	$('<div>').addClass(BTN_OK).css({"padding":"5px 18px","margin-right":"8px"}).click(function(){sendExternalFileAll_ok(meteoid);}).html("O K").appendTo(bodys);
	$('<div>').addClass(BTN_CANCEL).css({"padding":"5px 18px"}).click(function(){dialogCancel();}).html("Cancel").appendTo(bodys);

	alertDialogOpen(lang.__("Send bulk external data"), lang.__("Deliver external data in bulk?"), bodys, BG_BLUE, 150, 350);
}


