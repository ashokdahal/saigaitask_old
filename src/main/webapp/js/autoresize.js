/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

/**
 * アラーム情報を表示する領域の表示・非表示を制御
 */

//アラーム表示部開閉部分の高さ
var infoAlarmHeight = 0;
//画面全体のリサイズか否か
var resizeFlag = false;
//ブラウザ判定用
var ua = window.navigator.userAgent.toLowerCase();

function autoResize() {
	//アラーム表示部
	var infoAlarm = document.getElementById("info_alarm");
	//全体のヘッダ部
	var header = document.getElementById("header");
	//header.jspに記載されているヘッダ部
	var header_jsp = document.getElementById("header_jsp");
	//メニュー表示部
	var divMenu = document.getElementById("menu");
	//コンテンツ表示部
	var divContent = document.getElementById("content");
	//メニューの表示/非表示を制御する領域
	var resizerWest = $(".ui-layout-resizer-west");

	//動的に変更する高さ
	var height;
	var height_jsp;

	//表示
	if (infoAlarm.style.display == "" || infoAlarm.style.display == "block"){
		//初期表示時対応
		if(infoAlarmHeight==0)infoAlarmHeight=infoAlarm.offsetHeight;
		//画面のリサイズに伴う場合は、アラート表示領域の大きさは変わらない
		if(resizeFlag){
			height = header.offsetHeight;
			height_jsp = header_jsp.offsetHeight;
		}else{
			height = header.offsetHeight+infoAlarmHeight;
			height_jsp = header_jsp.offsetHeight+infoAlarmHeight;
		}
		//alert("open infoAlarmHeight : "+infoAlarmHeight);
		//ヘッダ部分の高さに、アラート表示部分の高さを足す
		header.style.height = height+"px";
		header_jsp.style.height = height_jsp+"px";
		//alert("open : " + header.style.height+", "+header_jsp.style.height);
		//メニュー・コンテンツ表示部分のtopに、アラート表示部分の高さを足す
		divMenu.style.top = height+"px";
		divContent.style.top = height+"px";
		resizerWest.css("top", height);
		//変更後のアラーム表示部分の高さをグローバル変数に保持
		infoAlarmHeight = infoAlarm.offsetHeight;
	//非表示
	}else{
		//alert(infoAlarmHeight);
		//画面のリサイズに伴う場合は、アラート表示領域の大きさは変わらない
		if(resizeFlag){
			height = header.offsetHeight;
			height_jsp = header_jsp.offsetHeight;
		}else{
			height = header.offsetHeight-infoAlarmHeight;
			height_jsp = header_jsp.offsetHeight-infoAlarmHeight;
		}
		//alert("close infoAlarmHeight : "+infoAlarmHeight);
		//ヘッダ部分の高さから、アラートを表示していた部分の高さを引く
		header.style.height = height+"px";
		header_jsp.style.height = height_jsp+"px";
		//alert("close : " + header.style.height+", "+header_jsp.style.height);
		//メニュー・コンテンツ表示部分のtopから、アラートを表示していた部分の高さを引く
		divMenu.style.top = height+"px";
		divContent.style.top = height+"px";
		resizerWest.css("top", height);
		//変更後のアラーム表示部分の高さをグローバル変数に保持（IEのみ）
		if(ua.indexOf("msie") != -1){
			infoAlarmHeight = infoAlarm.offsetHeight;
		}
	}

	//画面の大きさ、アラート表示領域の大きさを取得してstyleに設定
	var windowpos = findWindowSize();
	var size = findSize(header);
	divMenu.style.height = (windowpos[1]-size[1])+"px";
	divContent.style.height = (windowpos[1]-size[1])+"px";

	//alert(height);
	//alert(header.style.height);
	//alert(divMenu.style.top);
	//alert(divContent.style.top);

	// 全体のレイアウトを調整.
	if(typeof bodyLayout!="undefined") {
		bodyLayout.resizeAll();
	}
}

//リサイズ後の画面の幅と高さを取得
function findWindowSize() {
	if(typeof window.innerWidth!="undefined") {
		return [window.innerWidth, window.innerHeight];
	}
	return [window.document.body.clientWidth, window.document.body.clientHeight]; // for IE4～IE9
}

//指定した両機の幅と高さを取得
function findSize(obj) {
	var curwidth = obj.offsetWidth;
	var curheight = obj.offsetHeight;
	return [curwidth,curheight];
}

//画面表示時の実行
//->初期表示時にアラームリスト部分を非表示にしたので、不要になった
//$(document).ready(function(){
//	//IEの場合は読み込みのタイミングを遅らせる
//	if(ua.indexOf("msie") != -1){
//		setTimeout("autoResize()", 2000);
//	}else{
//		autoResize();
//	}
//});

//画面リサイズ時の実行
$(window).resize(function(){
	resizeFlag = true;
	autoResize();
	resizeFlag = false;
});
