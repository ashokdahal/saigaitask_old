<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../common/lang_resource.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/WEB-INF/view/common/head.jsp"></jsp:include>
<script type="text/javascript" src="${f:url('/js/saigaitask.js_util.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.training.js')}"></script>


<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/trainingPanel.css')}" />

<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script type="text/javascript" src="${f:url('/js/training/training_panel_event.js')}"></script>



<style type="text/css">
html body #content .link {
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">

// ON / OFF ボタンのスタイル名
var BTN_ON = "orange-brown-button";
var BTN_OFF = "white-gray-button";
// DialogのOK / Cancelボタンのスタイル名
var BTN_OK = "white-red-button";
var BTN_CANCEL = "black-lightblue-button";
// Dialogのタイトル背景色
var BG_BLUE = "rgba(50,100,255,0.5)";
var BG_RED = "rgba(240,0,0,0.5)";
// 自作Dialog用変数
var alertDialog;
// Ajax用固定パラメータ
var URLS = "${f:url('/admin/training/')}";

// Ajax用クラス
function GetDatas(){
	this.initialize.apply(this, arguments);
}
GetDatas.prototype = {
	initialize : function(url, param){
		this.url = url;
		this.param = param;
	},
	doAjax : function(){
		var jqXHR = $.ajax({
			url : this.url,
			method: "POST",
			headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
			dataType : "json",
			data : this.param,
			contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
		return jqXHR.promise();
	}
}

$(function(){
	// レイアウト初期化
	SaigaiTask.Layout.init();

	// TableSorter 初期化
	$("#trainingPlanTable").tablesorter({ widgets: ['zebra'], headers: {4:{sorter:false}} });
	$("#trainingDataTable").tablesorter({ widgets: ['zebra'], headers: {3:{sorter:false}} });
	$("#trainingExternalDataTable").tablesorter({ widgets: ['zebra'], headers: {3:{sorter:false},4:{sorter:false},5:{sorter:false}} });


	var tablesorter_headers_3 = {3:{sorter:false}};

	// 訓練パネル用の各種クリックイベントをセットする
	trainingPlanClickEvents();

	// 外部ファイル設定イベントをセットする
	externalEvent();

	// 外部データ登録用ダイアログ
	$('#external-dialog').hide();

	// サーバ上防災情報XMLファイル選択用ダイアログ
	$('#external-bousaixmlfiles-dialog').hide();

	// planidが0でない場合は、リストを開く
	if($("*[name=planid]").val() != 0){
		var planid = $("*[name=planid]").val();
		var openplanid = "planid_" + planid;
		$.each( $("#leftPlanList").children("li"), function(){
			if($(this).attr("id") == openplanid){
				//$(this).trigger("click");
				// Liを追加する
				var Lis = $(this);
				$(this).addClass("selectRed")
				var basic = $("<dl>").addClass("leftDL").attr({"ids":"plan_" + planid,"pat":"basic"}).html("<%=lang.__("Basic Settings")%>").addClass("noSelectGray").appendTo(Lis);
				var external = $("<dl>").addClass("leftDL").attr({"ids":"plan_" + planid,"pat":"external"}).html("<%=lang.__("External data settings")%>").addClass("noSelectGray").appendTo(Lis);
				var control = $("<dl>").addClass("leftDL").attr({"ids":"plan_" + planid,"pat":"control"}).html("<%=lang.__("Training control")%>").addClass("noSelectGray").appendTo(Lis);
				//var restore = $("<dl>").addClass("leftDL").attr({"ids":"plan_" + planid,"pat":"restore"}).html("訓練モード復元").addClass("noSelectGray").appendTo(Lis);
				// 一覧表示領域を非表示
				$("#planListDiv").css({"display":"none"});
				// 遷移するページ種別も設定されていれば合わせて処理する
				var listtype = $("*[name=training_Listtype]").val();
				// 選択色にする
				if(listtype == "external"){ external.removeClass("noSelectGray").addClass("selectRed"); }
				else if(listtype == "control"){ control.removeClass("noSelectGray").addClass("selectRed"); }
				//else if(listtype == "restore"){ restore.removeClass("noSelectGray").addClass("selectRed"); }
				else { basic.removeClass("noSelectGray").addClass("selectRed"); }
				// 詳細ページを表示
				planDispPage(listtype);
			}
		});
	}

	// 外部データ登録用ダイアログのソース選択ラジオボタン
    $( 'input[name="formFile_choice_source"]:radio' ).change( function() {
    	$("#formFile_source_field").empty();
    	if($( this ).val() === '1'){
        	$("#formFile_source_field").append("<input type=\"file\" name=\"formFile_external_xml\">");
    	}else{
        	$("#formFile_source_field").append("<input type=\"button\" id=\"bousaixmlfile_select_button\" value=\"&nbsp;<%= lang.__("Select a file on the server.")%>&nbsp;\">");
        	$("#formFile_source_field").append("<span id=\"bousaixmlfile_select_span\"></span>");
        	$("#formFile_source_field").append("<input type=\"hidden\" name =\"bousaixmlfile_selected_type\" id=\"bousaixmlfile_selected_type\" value=\"\">");
        	$("#formFile_source_field").append("<input type=\"hidden\" name =\"bousaixmlfile_selected_filename\" id=\"bousaixmlfile_selected_filename\" value=\"\">");
    	}
    });
});

/**
 * ページ再読み込み用
 * @param planid : 現在表示中の訓練プラン
 * @param pageType : 再読み込み後に開くメニュー
 */
function locationHrefURL(planid, pageType){
	var sendURL = pageType=="copy"?"planCopy/"+planid:"";
	//planid = pageType=="copy"?"0":planid;
	var forms = $("<form>").attr({"action":URLS + sendURL, "method":"POST", "target":"_self"}).appendTo($("body"));
	$("<input>").attr({"name":"training_planid"}).val(planid).appendTo(forms);
	$("<input>").attr({"name":"training_Listtype"}).val(pageType).appendTo(forms);
	$("<input>").attr({"name":"copyTrainingplanId"}).val(planid).appendTo(forms);
	var token = $("meta[name='_csrf']").attr("content");
	$("<input>").attr({"name":"_csrf"}).val(token).appendTo(forms);
	forms.submit();
}


/**
 * 外部データ登録用ダイアログ表示関数
 * @param meteoid : 外部データID
 */
function externalDialogOpen(meteoid){
	if(meteoid == null) return;
	var titles = "<%=lang.__("External data")%>";
	if(meteoid == 0) {
		titles += "<%=lang.__("Register <Japan Meteorological Agency XML>")%>";
	}else{
		titles += "<%=lang.__("Edit <Japan Meteorological Agency XML>")%>";
	}
	// 現在設定されている気象情報種別を持っているか確認する
	var ajaxData = new GetDatas(URLS + "checkMeteoRequestInfo", "checkMeteoTypeid="+$("*[name=meteoType]").val());
	ajaxData.doAjax()
	.then(function(response){
		// done
		datas = eval(response);
		if(datas.message != ""){
			$("#checkMeteoDataMessage").html(datas.message);
			$(".checkMeteoRow").css({"display":"block"});
		}else{
			$("#checkMeteoDataMessage").html("");
			$(".checkMeteoRow").css({"display":"none"});
		}
		$('#external-dialog').dialog({
			title: titles,
			height:400,
			width:550,
			modal:true,
			buttons: {
				"<%=lang.__("Save")%>": function(){
					if($("#meteoName").val() == ""){
						alert("<%=lang.__("XML registration name is a required.")%>");
						return;
					}
					$('<input>').attr({"type":"hidden","name":"trainingplandataid"}).val( $("*[name=planid]").val() ).appendTo("#external-dialog-form");
					$('<input>').attr({"type":"hidden","name":"meteotypeid"}).val( $("*[name=meteoType]").val() ).appendTo("#external-dialog-form");
					$("#external-dialog-form").submit();
					$(this).dialog("close");
				},
				"<%=lang.__("Close")%>": function(){
					$(this).dialog("close");
				}
			}
		});
	}, function(uqXHR, textStatus, errorThrow){
		// 独自ダイアログを使用してエラー表示
		ajaxError(" <%=lang.__("Failed to check weather info type.")%> ");
	})
}


/**
 * サーバ上防災情報XMLファイル選択用ダイアログ
 */
function externalBousaixmlfilesDialogOpen(){
	var titles = "<%=lang.__("Select a meteorological xml file on the server.")%>";

	// ダイアログ内を空にする
	$("#external-bousaixmlfiles-dialog-content").empty();

	// サーバ上にある防災情報XMLファイルのリストを取得する
	var ajaxData = new GetDatas(URLS + "loadBousaixmlFileList", "trainingplandataid="+$("*[name=planid]").val() +
			"&checkMeteoTypeid="+$("*[name=meteoType]").val());
	ajaxData.doAjax()
	.then(function(response){
		// done
		datas = eval(response);
    	$("#external-bousaixmlfiles-dialog-content").append(datas.filesContent);

    	$('#external-bousaixmlfiles-dialog').dialog({
			title: titles,
			height:420,
			maxHeight:420,
			width:560,
			modal:true,
		});
	}, function(uqXHR, textStatus, errorThrow){
		// 独自ダイアログを使用してエラー表示
		ajaxError(" <%=lang.__("Failed to check weather info type.")%> ");
	})
}


// 自作Dialogの閉じる関数
function alertDialogClose(){
	alertDialog.dialog("close");
}

// 通知制限等のON/OFF変更関数
function changeBasicNotice(buttonName, flags){
	if(flags){
		// ON表示なので何もしない
		if($("#" + buttonName).hasClass(BTN_ON)){ return;}
		else{
			$("#" + buttonName).removeClass(BTN_OFF).addClass(BTN_ON).html("ON");
			$("#" + buttonName).parent().removeClass("textRight").addClass("textLeft");
		}
	}else{
		if($("#" + buttonName).hasClass(BTN_ON)){
			$("#" + buttonName).removeClass(BTN_ON).addClass(BTN_OFF).html("OFF");
			$("#" + buttonName).parent().removeClass("textLeft").addClass("textRight");
		}else{ return; }
	}
}

// 基本設定登録関数
function planBasicData(param){
	// 開いていたダイアログを閉じる
	alertDialog.dialog("close");
	if(param != "ok") return;
	// 各パラメータを取得して動的Formに登録
	var forms = $("<form>");
	var trainingName = $("#planName").val();
	var trainingNote = $("#planNote").val();
	var planDisaster = $("#planDisaster").val();
	if(trainingName == "" || trainingName == undefined){
		alert("<%=lang.__("Plan name is not filled.")%>");
		return;
	}
	// planIDを取得
	var planid = $("*[name=planid]").val();
	// planidは使用しているのでplanidsとして登録
	$("<input>").attr({"name":"planids"}).val(planid).appendTo(forms);
	$("<input>").attr({"name":"planName"}).val(trainingName).appendTo(forms);
	$("<input>").attr({"name":"planDisasters"}).val(planDisaster).appendTo(forms);
	$("<input>").attr({"name":"planNote"}).val(trainingNote).appendTo(forms);
	// 通知機能制限のON/OFF取得
	var commonsBtn = $("#commonsBtn").html();
	var facebookBtn = $("#facebookBtn").html();
	var twitterBtn = $("#twitterBtn").html();
	var ecomGWBtn = $("#ecomGWBtn").html();
	var commonsFlag = commonsBtn == "ON" ? "true" : "false";
	var facebookFlag = facebookBtn == "ON" ? "true" : "false";
	var twitterFlag = twitterBtn == "ON" ? "true" : "false";
	var ecomGWFlag = ecomGWBtn == "ON" ? "true" : "false";
	// Formに格納
	$("<input>").attr({"name":"commonsFlag"}).val(commonsFlag).appendTo(forms);
	$("<input>").attr({"name":"facebookFlag"}).val(facebookFlag).appendTo(forms);
	$("<input>").attr({"name":"twitterFlag"}).val(twitterFlag).appendTo(forms);
	$("<input>").attr({"name":"ecomGWFlag"}).val(ecomGWFlag).appendTo(forms);
	// 連携自治体のONになっているものを取得
	$.each( $(".link-btn"), function(){
		if($(this).hasClass("orange-brown-button")){
			var ids_all = $(this).attr("id");
			var ids = 0;
			// localgovBtn_の後ろにあるlocalgovinfoidを取得する
			if(ids_all.indexOf("localgovBtn_") != -1){
				ids = ids_all.substring(ids_all.indexOf("localgovBtn_") + 12, ids_all.length);
				// 作成したFormの下に格納
				$("<input>").attr({"name":"localgovinfoid"}).val(ids).appendTo(forms);
			}
		}
	});
	var formSerialize = forms.serialize();
	// 削除
	forms.remove();

	var ajaxData = new GetDatas(URLS + "updatePlan", formSerialize);
	ajaxData.doAjax().done(function(response){
		datas = eval(response);
		// 基本設定のデータ更新が入った場合は一旦リロードする
		locationHrefURL(datas.planid, "basic");
	})
	.fail(function(uqXHR, textStatus, errorThrow){
		// 独自ダイアログを使用してエラー表示
		ajaxError(" <%=lang.__("Failed to register basic settings.")%> ");
	});
}

/**
 * 訓練プラン論理削除
 */
function trainingplanDataDelete(param){
	// 開いていたダイアログを閉じる
	alertDialog.dialog("close");
	if(param != "ok") return;

	var ajaxData = new GetDatas(URLS + "deleteTrainingPlanData", "trainingplandataid=" + $("*[name=planid]").val());
	ajaxData.doAjax().done(function(response){
		datas = eval(response);
		// 受け取ったmessage内容をそのまま表示
		//dialogSuccess("訓練プラン削除完了", datas.message);
		// リストの更新
		// データ更新が入った場合は一旦リロードする
		locationHrefURL("", "");
	})
	.fail(function(uqXHR, textStatus, errorThrow){
		// 独自ダイアログを使用してエラー表示
		ajaxError("<%=lang.__("Failed to delete data. Training plan ID =")%>" + $("*[name=planid]").val());
	});

}

/**
 * 外部データ設定削除用関数
 * @param param : OK or cancel
 */
function externalDataDelete(param){
	// 開いていたダイアログを閉じる
	alertDialog.dialog("close");
	if(param != "ok") return;

	var ajaxData = new GetDatas(URLS + "deleteExternalData", "meteoid=" + $("#meteoid").val());
	ajaxData.doAjax().done(function(response){
		datas = eval(response);
		// 受け取ったmessage内容をそのまま表示
		<%-- dialogSuccess("<%=lang.__("External data has been deleted completely")%>", datas.message); --%>
		// リストの更新
		// データ更新が入った場合は一旦リロードする
		locationHrefURL(datas.planid, "external");
	})
	.fail(function(uqXHR, textStatus, errorThrow){
		// 独自ダイアログを使用してエラー表示
		ajaxError("<%=lang.__("Failed to delete data. External data set ID =")%>" + $("#meteoid").val());
	});
}

function sendExternalFileAll_ok(meteoid){
	alertDialog.dialog("close");
	// データ送信用のformを作成する
	var forms = $("<form>").attr({"action":URLS, "method":"POST", "target":"_self"}).appendTo($("body"));
	// 外部データのid
	$("<input>").attr({"name":"trainingmeteoid","type":"hidden"}).val(meteoid).appendTo(forms);
	// planid
	$('<input>').attr({"name":"trainingplandataid","type":"hidden"}).val( $("*[name=planid]").val() ).appendTo(forms);
	var formSerialize = forms.serialize();

	// 削除
	forms.remove();

	// データ送信
	var ajaxData = new GetDatas(URLS + "sendExternalDataAll", formSerialize);
	ajaxData.doAjax().done(function(response){
		datas = eval(response);
		// リストの更新
		for(var i = 0; i < datas.sendData.length; i++){
			var classnames = ".sendExternalTime_" + datas.sendData[i].trainingmeteoid + "_" + datas.sendData[i].localgovinfoid;
			$(classnames).html(datas.sendData[i].updatetime);
		}
	})
	.fail(function(uqXHR, textStatus, errorThrow){
		//console.log("fail");
	});

}

function sendExternalFile_ok(meteoid, localgovinfoid){
	alertDialog.dialog("close");
	var ajaxData = new GetDatas(URLS + "sendExternalData", "trainingmeteoid=" + meteoid + "&externalLocalgovSend=" + localgovinfoid);
	ajaxData.doAjax().done(function(response){
		datas = eval(response);
		// リストの更新
		for(var i = 0; i < datas.sendData.length; i++){
			var classnames = ".sendExternalTime_" + datas.sendData[i].trainingmeteoid + "_" + datas.sendData[i].localgovinfoid;
			//console.log(classnames + " updatetime = " + datas.sendData[i].updatetime);
			$(classnames).html(datas.sendData[i].updatetime);
		}
		// 受け取ったmessage内容をそのまま表示
		//dialogSuccess("外部データ送信完了", );
	})
	.fail(function(uqXHR, textStatus, errorThrow){
		// 独自ダイアログを使用してエラー表示
		ajaxError("<%=lang.__("Failed to send external data.")%> ");
	});
}


function setServerSendBousaiXmlFile(idStr){
	var typeId = "bousaixmlfile_lists_type_" + idStr;
	var fileNameId = "bousaixmlfile_lists_filename_" + idStr;

	var typeValue = $("#" + typeId).val();
	var fileNameValue = $("#" + fileNameId).val();

	$("#bousaixmlfile_selected_type").val(typeValue);
	$("#bousaixmlfile_selected_filename").val(fileNameValue);
	$("#bousaixmlfile_select_span").text(" " + fileNameValue);
	$("#external-bousaixmlfiles-dialog").dialog("close");
}

</script>
</head>
<body>
	<div id="header" class="ui-layout-north">
		<jsp:include page="/WEB-INF/view/admin/training/header.jsp" />
	</div>
	<div id="menu" class="ui-layout-west"><jsp:include page="/WEB-INF/view/admin/training/menu.jsp" /></div>
	<div id="content" class="ui-layout-center">
		<tiles:insertAttribute name="content" />
	</div>
</body>
</html>
