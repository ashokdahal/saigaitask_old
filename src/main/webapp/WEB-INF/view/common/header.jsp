<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%
	/* Copyright (c) 2013 National Research Institute for Earth Science and * Disaster Prevention (NIED). * This code is licensed under the GPL version 3 license, available at the root * application directory. */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>
<link rel="stylesheet" href="http://sdss.geoinfo.ait.ac.th/css/bootstrap.css" type='text/css'/>
 <!--  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
-->
<style>

.dropbtn {
  background-color: #3498DB;
  color: white;
  padding: 8px;
  font-size: 14px;
  border: none;
  cursor: pointer;
  margin-right:80px;
  margin-left:20px;
}

.dropbtn:hover, .dropbtn:focus {
  background-color: #2980B9;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: #f1f1f1;
  min-width: 160px;
  overflow: auto;
  box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
  z-index: 1;
}

.dropdown-content a {
  color: black;
  padding: 2px 6px;
  text-decoration: none;
  display: block;
}

.dropdown a:hover {background-color: #ddd;}

.show {display: block;}
</style>
<script type="text/javascript">
$(function() {
	$("#header").css("z-index", "100");
});
function openMenuWizard() {
	var url = "${f:url('/admin/setupper/menuWizard/userpage')}";
	window.open(url, 'wizard', 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes');
}
function openMobileQR() {
	var url = "${f:url('/admin/mobileqrcodeInfo/qrcodelist')}";
	window.open(url, 'wizard', 'width=350, height=600, menubar=no, toolbar=no, scrollbars=yes');
}
function openEcommap() {
	  var cid = "${pageDto.trackData.id}" == 0 ? "${pageDto.mapmasterInfo.communityid}" : "${pageDto.trackData.trackmapInfo.communityid}";
	  var gid = "${pageDto.trackData.id}" == 0 ? "${pageDto.mapmasterInfo.mapgroupid}" : "${pageDto.trackData.trackmapInfo.mapgroupid}";
	  var mapid = "${pageDto.trackData.id}" == 0 ? "${pageDto.mapmasterInfo.mapid}" : "${pageDto.trackData.trackmapInfo.mapid}";
	  var url = "${pageDto.ecommapURL}map/?cid=1&gid=1"
	  ///login.jsp?protocol=http%3A&auth=admin&pass=gicait123"
	  // map/map/?cid="+cid+"&gid="+gid+"&mid="+mapid;
	  var childWin = window.open(url, 'ecommap');
	  childWin.focus();
	}
</script>
<script>
/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */
function myFunction() {
  document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbtn')) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}
</script>

<link href="${f:url('/js/jquery.marquee/css/jquery.marquee.min.css')}" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="${f:url('/css/bbs/badger.css')}" />
<script type="text/javascript" src="${f:url('/js/ui.common.js')}"></script>
<script type="text/javascript" src="${f:url('/js/noty/jquery.noty.js')}"></script>
<script type="text/javascript" src="${f:url('/js/noty/layouts/top.js')}"></script>
<script type="text/javascript" src="${f:url('/js/noty/themes/default.js')}"></script>
<script type="text/javascript" src="${f:url('/js/autoresize.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.marquee/lib/jquery.marquee.min.js')}"></script>
<script src="${f:url('/js/bbs/thread_method.js')}" type="text/javascript"></script>

<script type="text/javascript">
//テロップ読み込み
var loadtelop = function(){
	$('#telop_msg').load('${f:url('/page/telopdata?')}'+(new Date()).getTime(), null,
		function(responseText, status, XMLHttpRequest) {
			//alert($('#telop_msg').text().length);
			//alert(responseText.length);
			$('#telop_msg').width(responseText.length*20);
			$('#marquee').marquee("resume");
		});
};

//アラーム読み込み
var loadalert = function(){
	jQuery.ajax({
		url: SaigaiTask.contextPath+"/page/newalert",
		cache: false,
		dataType: "json",
		success: function(data) {
			console.log(data);
			var items = data.item;
			for(var idx in items) {
				var item = items[idx];
				//alert("alert.item : "+item.message);

				// URLが含まれていれば自動的にリンクを張る
				item.message = item.message.replace(/(https?:\/\/[\x21-\x7e]+)/gi, "<a href='$1' target='_blank'>$1</a>");

				noty({
					// "\n"文字列をbrタグに変換
					text: item.message.replace(/[\n\r]/g, "<br />"),
					layout: 'top',
					theme: 'defaultTheme',
					type: item.type,
					//modal: true,//アラーム表示時に他の操作をできないようにする
					maxVisible: 5,//最大いくつまで同時に表示させるか
					timeout: item.duration
				});
				//alert(item.message);
				// すでに存在するかどうか
				var notExists = $("#info_alarm_msg #showAlarmDetail_"+item.id).length==0;
				// アラームメッセージ情報を更新する
				if(item.message && notExists==true) {
					var ul = $('#info_alarm_msg').children('ul');
					//ul.prepend('<li>'+item.message+'</li>');
					ul.prepend("<li><a href=\"#\" id=\"showAlarmDetail_"+item.id+"\" >" + item.message + "</a></li>");
				}
			}
		}
	});
	//アラームが追加された後にイベント再設定
	//TODO 読み込みの順番をちゃんと考える必要あり
	setTimeout("showAlarmDetail()",2000);
};

//アラーム詳細表示
var showAlarmDetail = function(){
	$("*[id^=showAlarmDetail]").on("click", function(){
		var idStr = new String($(this).attr("id"));//Stringとして取得する必要あり！
		var id = idStr.substr(idStr.indexOf("_")+1);
		var alarmDetail = $("#alarm_detail");
		//指定したIDのアラーム詳細データ取得
		alarmDetail.load('${f:url('/page/alarmdetail?')}'+"id="+id);
		//ダイアログ表示
		alarmDetail.dialog({
			title: "<%=lang.__("Alarm data detailed display")%>",
			modal: true,
			maxHeight: 500,
			minWidth: 700
		});
	});
};

function showTimeline(){
	$('#timeline-dialog').html('<iframe style="height: 100%; width: 100%; border: none;"></iframe>');
	$('#timeline-dialog iframe').attr("src","${f:url('/timeline')}");
	$('#timeline-dialog').dialog({
		title: "<%=lang.__("Timeline")%>",
		height:$(window).height()*0.85,
		width:$(window).width()*0.8,
		modal:true,
		buttons: {
			"<%=lang.__("Print")%>": function(){
				location.href="${f:url('/timeline/outputpdf')}?<%=FormUtils.getTokenParam(request)%>"
//				location.href="${f:url('/timeline/outputpdf')}"
			},
			"<%=lang.__("Close")%>": function(){
				$(this).dialog("close");
			}
		}
	});
}

function showHeadoffice(){
	$('#headoffice-dialog').html('<iframe id="headoffice-dialog-iframe" style="height: 100%; width: 100%; border: none;"></iframe>');
	$('#headoffice-dialog iframe').load(function() {
		var iframeWin = document.getElementById("headoffice-dialog-iframe").contentWindow;
		var iframeDoc = iframeWin.document;
		// 閉じるボタンのクリックイベントをダイアログ閉じるに上書き
		var closeBtn = $("#fullscreen-close-button", iframeDoc);
		closeBtn.click(function() {
			$('#headoffice-dialog').dialog("close");
		});
		// 会議録以外のタスクに移動させないため、タスクリストは非表示
		$("#tasklist-container", iframeDoc).css("display", "none");
		// サイドメニューを表示
		iframeWin.SaigaiTask.Layout.bodyLayout.open("west");

	});
	$('#headoffice-dialog iframe').attr("src","${f:url('/page/')}?menutaskid=${pageDto.headofficeTaskId}&fullscreen=true");
	$('#headoffice-dialog').dialog({
		title: "<%=lang.__("Minutes")%>",
		height:$(window).height()*0.85,
		width:$(window).width()*0.8,
		modal:true
/*
		buttons: {
			"<%=lang.__("Close")%>": function(){
				$(this).dialog("close");
			}
		}
*/
	});
}

// 掲示板ダイアログ表示
function showBBS(){
	$('#bbs-dialog').html('<iframe style="height: 100%; width: 100%; border: none;"></iframe>');
	$('#bbs-dialog iframe').attr("src","${f:url('/bbs')}");
	$('#bbs-dialog').dialog({
		title: "<%=lang.__("Board")%>",
		height:$(window).height()*0.85,
		width:$(window).width()*0.8,
		modal:true,
		buttons: {
			"<%=lang.__("Close")%>": function(){
				$(this).dialog("close");
			}
		}
	});
}
// 掲示板の未読スレッド数表示
function unReadThreadBBS(){
	$.ajax({
		url : "${f:url('/bbs/haveUnReadMessage')}",
		type : "POST",
		dataType : "json",
		data : "",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response, ioArgs){
			datas = eval(response);
			var unReadInt = parseInt(datas.unReadThread, 10);
			// 未読メッセージがあれば通知音を鳴らす
			if(unReadInt !=0){
				document.getElementById("alarmsounds").play();
			}
			if(unReadInt >= 10){
				// 10件以上になったら+表記にする
				$('#Badger_bbs-button').remove();
				badger_copy_header($('#bbs-button'), 'bbs-button', "10+");
			}else if(unReadInt == 0){
				// 何もしない
				$('#Badger_bbs-button').remove();
			}else{
				// 数字をそのまま渡す
				$('#Badger_bbs-button').remove();
				badger_copy_header($('#bbs-button'), 'bbs-button', unReadInt);
			}
		},
		error : function(data){
			//alert("Error");
		},
		always : function(){
			setTimeout(unReadThreadBBS(), 20000);
		}
	});
}

// 掲示板に現在のページURLを渡す関数
function pageURLBBS(){
	return "{keywordurlbbs}" + encodeURIComponent(SaigaiTask.PageURL.getUrl());
}

// ホワイトボードダイアログ表示
function showWhiteboard(){
	$('#whiteboard-dialog').html('<iframe style="height: 100%; width: 100%; border: none;"></iframe>');
	$('#whiteboard-dialog iframe').attr("src","${f:url('/whiteboard/')}?groupid=${loginDataDto.groupid}");
	$('#whiteboard-dialog').dialog({
		title: "<%=lang.__("Whiteboard")%>",
		height:$(window).height()*0.95,
		width:$(window).width()*0.95,
		modal:true,
		buttons: {
			"<%=lang.__("Close")%>": function(){
				$(this).dialog("close");
			}
		}
	});
}

//アラート履歴ダイアログ表示
function showAlert(){
	$('#alert-dialog').html('<iframe style="height: 100%; width: 100%; border: none;"></iframe>');
	$('#alert-dialog iframe').attr("src","${f:url('/alertContent')}");
	$('#alert-dialog').dialog({
		title: "<%=lang.__("Receive history")%>",
		height:500,
		width:800,
		modal:true,
		buttons: {
			"<%=lang.__("Close")%>": function(){
				$(this).dialog("close");
			}
		}
	});
}

function initTimelineDialog(){
	$('#timeline-dialog').hide();
}

function initBBSDialog(){
	$('#bbs-dialog').hide();
}

function initWhiteboardDialog(){
	$('#whiteboard-dialog').hide();
}

function initAlertDialog(){
	$('#alert-dialog').hide();
}

// 地図の表示／非表示切り替え
var switchMapVisible = function(){
	$.ajax({
		url : "${f:url('/page/switchMapVisible')}",
		type : "POST",
		dataType : "json",
		data : "",
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(response){
			//console.log(response);
		},
		error : function(data){
			//alert("Error");
		}
	});
	window.location.reload(true);
};

$(document).ready(function() {
	loadtelop();
	setInterval(loadtelop, ${pageDto.alarmInterval});

	//アラーム
	loadalert();
	setInterval(loadalert, ${pageDto.alarmInterval});

	//アラームのアコーディオン開閉
	var accordionHead = $('.accordion_head');
	accordionHead.click(function() {
		$('#alarm_dummy').load('${f:url('/page/alarmopen?')}'+(new Date()).getTime());
		accordionHead.next().slideToggle("normal", function(){
			autoResize();
		});
	}).next();

	var autobtn = $('#autobutton');
	autobtn.click(function() {
		$.ajaxSetup({ cache: false });
		//alert($(this).children().text());
		//$(this).children().text('自動発報無効');
		$(this).children().load('${f:url('/page/autostart?')}');
	});

	<c:if test="${pageDto.isAlarmOpen() == false}">
	$('.accordion_head:first').next().hide();
	if(ua.indexOf("msie") != -1){
		setTimeout("autoResize()",2000);
	}else{
		autoResize();
	}
	</c:if>

	initTimelineDialog();
	initBBSDialog();
	initWhiteboardDialog();
	initAlertDialog();
	$('#headoffice-button').click(function(){showHeadoffice()});
	$('#timeline-button').click(function(){showTimeline()});
	$('#bbs-button').click(function(){showBBS()});
	$('#whiteboard-button').click(function(){showWhiteboard()});
	$('#alert-button').click(function(){showAlert()});

	// bbs作成
	var iframe_bbs = document.createElement('iframe');
	iframe_bbs.frameBorder=0;
	document.getElementById('bbs-dialog').appendChild(iframe_bbs);
	// bbs未読スレッド表示
	unReadThreadBBS();
	// タイマーで定期的にチェックする
	//setInterval(function(){ unReadThreadBBS();}, 60000);

	var iframe_whiteboard = document.createElement('iframe');
	iframe_whiteboard.frameBorder=0;
	document.getElementById('whiteboard-dialog').appendChild(iframe_whiteboard);
});

/**
 * 地物のデータリセット確認
 */
function resetLayer(msg){

	$.ajax({
		url : "${f:url('/track/')}checkResetLayers",
		headers: {"X-CSRF-Token":"${cookie.JSESSIONID.value}"},
		dataType : "json",
		data : this.param,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8"
	}).success(function(data, statusText, jqXHR){
		// リセットするかどうかのフラグを確認
		var _reset = false;
		if(data != null && 'resetbool' in data){
			if(data.resetbool == "true"){
				_reset = true
				// 災害管理ダイアログを閉じる
				tb_remove();
				// リセットダイアログの表示
				createResetDialog();
			}
		}
		if(!_reset){
			// 災害終了しないならキャンセル
			if (!confirm(msg))
				return false;
			// 災害終了
			document.getElementById("TB_iframeContent").contentWindow.trackComplete();
		}
	}).fail(function(){
		if (!confirm("<%=lang.__("Failed to get disaster data reset info. Proceed to finish disaster treatment?")%>"))
			return false;
		document.getElementById("TB_iframeContent").contentWindow.trackComplete();
	});
}

function createResetDialog(){
	$('#reset-dialog').html('<iframe id="resetframe" style="height: 100%; width: 100%; border: none;"></iframe>');
	$('#reset-dialog iframe').attr("src", "${f:url('/track/')}tablereset");
	$('#reset-dialog').dialog({
		title: "<%=lang.__("Reset exe confirmation")%>",
		height:$(window).height()*0.85,
		width:$(window).width()*0.8,
		modal:true,
		buttons: [
			{
				text: "<%=lang.__("Run completion process")%>",
				click: function() {
					var flags = document.getElementById('resetframe').contentWindow.updateResetLayer();
					if(flags) $(this).dialog("close");
					//$('#resetframe').contentWindow.updateResetLayer();
					//$(this).dialog("close");
				}
			},{
				text: "<%=lang.__("Close")%>",
				click: function() {
					$(this).dialog("close");
				}
			}
		]
	});

	$.each($(".ui-dialog-title"), function(){
		if($(this).html() == "<%=lang.__("Reset exe confirmation")%>"){
			$.each($(this).parent("div"), function(){
				$(this).css({"background-color":"rgba(79,129,189,0.5)", "background-image":"none", "font-size":"18px"});
			});
			return;
		}
	});
	$.each($(".ui-button-text"), function(){
		if($(this).html() == "<%=lang.__("Run completion process")%>" || $(this).html() == "<%=lang.__("Close")%>"){
			$.each($(this).parent("button"), function(){
				$(this).css({"background-color":"rgba(79,129,189,0.5)","font-size":"16px",
					"background-image":"none,linear-gradient(rgba(255, 255, 255, 0.6) 0%, rgba(255, 255, 255, 0.4) 50%, rgba(255, 255, 255, 0.2) 50%, rgba(255, 255, 255,0) 100%)"
				});
			});
			return;
		}
	});

}
</script>
<script type="text/javascript">
			if(${!empty pageDto.headerbutton}){
				var buttons = JSON.parse(${pageDto.headerbutton});
				for(var idx in buttons){
					var buttonList = $('<li><a href='+buttons[idx].url+' class="header-button" target="_blank" id="button'+[idx]+'" style="font-size:12px;">'+buttons[idx].name+'</a></li>');
					$("#header-button-list").prepend(buttonList);
					var button = $(".header-button", buttonList);
					button.button();
					buttonList.css("margin", "5px").css("float", "left").css("list-style", "none").css("font-size", "12px");
				}
			}
			$(".header-button").button().css("visibility", "visible");
			$("#header-button-list li").css("margin", "5px").css("float", "left").css("list-style", "none").css("font-size", "12px");
		</script>
<div id="header_jsp" style="height:auto;">

<nav id="header"class="navbar navbar-expand-lg navbar-dark bg-dark">
  <a class="navbar-brand" href="#">${f:h(siteName)}</a>
 
  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
<li><a href="#" class="btn-default btn-sm nav-tabs btn-outline-secondary" id="bbs-button" style="font-size:16px;"><%=lang.__("Board")%></a></li>
      <c:if test="${!pageDto.usual }">
      <li><a href="#" class="btn-default nav-tabs btn-sm btn-outline-secondary" id="timeline-button" style="font-size:16px;"><%=lang.__("Timeline")%></a></li>
      <li><a href="#" class="btn-default nav-tabs btn-sm btn-outline-secondary" id="headoffice-button" style="font-size:16px;"><%=lang.__("Minutes")%></a></li>
      </c:if>
      <li><a href="#" class="btn-default nav-tabs btn-sm btn-outline-secondary"  id="whiteboard-button" style="font-size:16px;"><%=lang.__("Whiteboard")%></a></li>
      </ul>
      		
      
      <li class="my-2">
					<c:set var="fontcolor" value="${pageDto.trackData==null ? 'green' : pageDto.trackData.endtime!=null ? 'gray' : 'red'}"/>
					<span style="color: ${fontcolor}; font-weight: bold;font-size:16px;">${f:h(pageDto.saigaiName)}</span>
					<c:if test="${!pageDto.usual }">
				<!-- 	<a href="${f:url('/track/')}?TB_iframe=true&width=600&height=450" target="_app" class="btn-warning btn-sm btn-outline-secondary" style="font-size:12px;">
					--><a href="${f:url('/track/')}?TB_iframe=true&amp;width=600&amp;height=450" class="thickbox header-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="font-size: 12px; visibility: visible;" role="button" aria-disabled="false"><span class="ui-button-text">
					<c:if test="${!pageDto.isSaigai()}"><%=lang.__("Registration")%></c:if>
					<c:if test="${pageDto.isSaigai()}"><%=lang.__("Update")%></c:if>
					</span></a>
					</c:if>
				</li>
				
		<div class="dropdown my-1">
  <button onclick="myFunction()" class="dropbtn">${f:h(pageDto.loginName)}</button>
  <div id="myDropdown" class="dropdown-content">
    <c:if test="${loginDataDto.admin }">
	<a href="${f:url('/admin/mainFrame/')}" target="admin">System <%=lang.__("Update")%></a>
	</c:if>
    <c:if test="${loginDataDto.usual == false }">
							<c:if test="${loginDataDto.groupInfo.headoffice == true }">
								<a href="javascript:showMenuwizard();"><%=lang.__("Wizard")%></a>
							</c:if>
						</c:if>
	<a href="javascript:openMobileQR();"><%=lang.__("application authorized QR")%></a>
	<c:if test="${pageDto.mapVisible == '1'}">
				<a href="#"  onclick="switchMapVisible();"><%=lang.__("Hide map")%></a>
</c:if>
<c:if test="${pageDto.mapVisible == '0'}">
		   		<a href="#" onclick="switchMapVisible();"><%=lang.__("Map display")%></a>
</c:if>
<a href="#" onclick="openEcommap();">Data Analysis</a>
<a href="http://www.dmdod.in/UK/IM_ActivateIncidentFacility.aspx" target="_blank">Resource Management</a>
<a href="http://dss.geoinfo.ait.ac.th/ODKAggregate/" target="_blank">CrowdSourcing</a>
<a href="http://203.159.29.143/" target="_blank">Data Sharing</a>
     <div class="dropdown-divider" style="border-color:red;"></div>
    <a href="${f:url('/logout')}"><%=lang.__("Logout")%></a>
  </div>
</div>
      
      
					
     
   
			
      

    
   
  </div>
</nav>
<div id="info_alarm" class="info_list_container" style="display:none;height:104px;">
		<div class="info_list">
			<div id="info_alarm_msg"></div>
		</div>
	</div>
<div id="headoffice-dialog" style="display:none; height:80%; width:85%;"><iframe style="height: 100%; width: 100%; border: none;"></iframe></div>
<div id="timeline-dialog" style="display:none; height:80%; width:85%;"><iframe style="height: 100%; width: 100%; border: none;"></iframe></div>
<div id="bbs-dialog" style="display:none; height:80%; width:85%;"></div>
<div id="alert-dialog" style="display:none; height:80%; width:85%;"></div>
<div id="reset-dialog" style="display:none; height:80%; width:85%;"></div>
<div id="whiteboard-dialog" style="display:none; height:95%; width:95%;"></div>
<div id="menuwizard-dialog" style="display:none; height:80%; width:85%;"><iframe style="height: 100%; width: 100%; border: none;"></iframe></div>
</div>
