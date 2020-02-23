<%  /* Copyright (c) 2013 National Research Institute for Earth Science and
	 * Disaster Prevention (NIED).
	 * This code is licensed under the GPL version 3 license, available at the root
	 * application directory.
	 */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../../common/lang_resource.jsp" %>
<%--
<jsp:include page="../common/adminjs-header.jsp" flush="true" />
<jsp:include page="../adminbackupData/adminbackupdata.jsp" flush="true" />
--%>
<title><%=lang.__("Admin window")%></title>

<!-- スタイルの定義 -->
<link rel="stylesheet" type="text/css"
	href="${f:url('/css/header_admin.css')}" />
<link type="text/css" rel="stylesheet"
	href="${f:url('/css/jquery.layout-1.4.0.css')}" />
<link type="text/css" rel="stylesheet"
	href="${f:url('/admin-js/css/head.css')}" />
<link type="text/css" rel="stylesheet"
	href="${f:url('/admin-js/css/jquery.treeview.css')}" />
<style type="text/css">body {font-size: 11px;}</style>
<link type="text/css" rel="stylesheet"
	href="${f:url('/css/style_adminjqgrid.css')}" />
<link type="text/css" rel="stylesheet"
	href="${f:url('/admin-js/css/redmond/jquery-ui-1.10.3.custom.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/header.css')}" />
<style type="text/css">
.ui-layout-pane-north {
	padding: 0px;
	overflow: hidden;
}
.FreezePaneOff
{
	visibility: hidden;
    display: none;
    position: absolute;
    top: -100px;
    left: -100px;
}

.FreezePaneOn
{
	position: absolute;
    top: 0px;
    left: 0px;
    visibility: visible;
    display: block;
    width: 100%;
    height: 100%;
    background-color: rgba(255,255,255,0.3);
    z-index: 99999;
    -ms-filter: "alpha( opacity=85 )";
    filter:alpha(opacity=85);
    opacity: 0.85;
    -moz-opacity:0.85;
    padding-top: 20%;
}

.InnerFreezePane
{
	width: 3%;
	background-color: #fff;
/*	background-image: url(../../admin-js/css/images/wait.gif);*/
    background-image: url(../../admin-js/css/images/ajax-loader3.gif);
	background-repeat: no-repeat;
	background-position: center center;
	color: #000;
	font-size: large;
	width: 30px;
    height: 30px;
}
</style>
<style type="text/css">
span.admin_header1 {
	display: block;
/*	width: 120px;*/
	text-align:center;
	height: 23px;
	line-height: 23px;
	color:#FFF;
	font-size: 16px;
	font-weight: bold;
	text-indent: 0;
	text-decoration: none;
	padding: 4px 3px 3px 3px;
}
span.admin_header2 {
	font-size: small;
}
span.admin_header3 {
	background-image: .ui-icon-carat-2-e-w;
}
span.admin_header4 {
	margin-left:1em;
	font-size: 12px;
}
span.admin_menu1 {
	font-size: medium;
}
span.admin_menu2 {
	font-size: small;
}
/* inner divs inside Outer East/West panes */
.menu_header {
/*	background: #80ade5
		url(../../admin-js/css/images/80ade5_40x100_textures_04_highlight_hard_100.png)
		0 50% repeat-x;
	border-bottom: 1px solid #777;
	font-size: small;
	font-weight: bold;
	text-align: center;
	padding: 2px 0 4px;
	position: relative;
	overflow: hidden;
*/
    border-top-right-radius: 5px;
    border-top-left-radius: 5px;
    background: url("../../admin-js/css/redmond/images/ui-bg_gloss-wave_55_5c9ccc_500x100.png") repeat-x scroll 50% 50% #5C9CCC;
    border: 1px solid #4297D7;
    color: #FFFFFF;
	font-size: small;
    font-weight: bold;
/*	text-align: center;*/
	padding: 4px;
}
.menu_toggle {
	background: url(../../admin-js/css/images/go-oc.png) 0 100% repeat-x;
	padding: 1px 6px 15px;
	position: relative;
}
.header_command {
	font-size: small;
	padding: 0px 0px 0px 5px;
	position: relative;
	line-height: 25px;
}
#sidetreecontrol{
	background: url("../../admin-js/css/redmond/images/ui-bg_glass_85_dfeffc_1x400.png") repeat-x scroll 50% 50% #DFEFFC;
	padding: 4px;
	margin: 0px 0px 3px 0px;
}

<style type="text/css">
/* BackUp 機能は外す */
 #menu_backup{
 	margin:auto;
 	text-align:left;
 }
 ul{
 	list-style:none;
 }
 #menu_backup li {
   position: relative;
   float: left;
   margin: 0;
   padding: 0px;
   width: 55px;
   height: 20px;
   line-height:1.4em;
 }
 #menu_backup li:hover {
 }
 #menu_backup li ul {
   padding:1px 2px 3px 2px;
   display: none;
   position: absolute;
   top: -35px;
   left: -28px;
   width: 70px;
   background-color:#dddddd;
   border: solid 1px #000000;
   line-height:0.1em;
 }
 #menu_backup li ul li {
   width: 70px;
   border: none;
 }
 #menu_backup li ul li a {
   align: left;
   display: inline-block;
   width:70px;
   height: 20px;
   text-decoration:none;
   color:#4444ff;
   text-align: left;
 }
 #menu_backup li ul li a:hover {
   align: left;
   background-color:#999999;
   color:#000000;
   text-decoration:underline;
   text-align: left;
 }
*/
</style>
<style type="text/css">
#global_header_title {
	font-size: 16px;
	color: #000;
	font-weight: bold;
	height:inherit;
	line-height: 29px;
	margin-left: 5px;
	text-shadow: 2px 2px 1px #fff,
	    -2px 2px 1px #fff,
	    2px -2px 1px #fff,
	    -2px -2px 1px #fff;
	vertical-align: middle;
}
</style>

<!-- JavaScript 定義 -->
<script type="text/javascript"
	src="${f:url('/js/jquery-1.10.2.js')}"></script>
<!--<script type="text/javascript" src="${f:url('/admin-js/js/jquery-1.9.0.min.js')}"></script>-->
<script type="text/javascript"
	src="${f:url('/admin-js/js/jquery-ui-1.10.3.custom.js')}"></script>
<script type="text/javascript"
	src="${f:url('/js/jquery.layout-1.4.0.js')}"></script>
<script type="text/javascript" src="${f:url('/admin-js/js/debug.js')}"></script>

<script type="text/javascript"
	src="${f:url('/admin-js/js/jquery.treeview.js')}"></script>
<script type="text/javascript"
	src="${f:url('/admin-js/js/jquery.cookie.js')}"></script>
<script type="text/javascript"
	src="${f:url('/admin-js/js/jquery.jqGrid.min.js')}" ></script>
<script type="text/javascript"
	src="${f:url('/admin-js/js/i18n/grid.locale-ja.js')}" ></script>
<script type="text/javascript" language="javascript">
	window.SaigaiTask = {
		contextPath: "<%=request.getContextPath()%>",
		csrfToken: "<%= session.getId() %>"
	};

// eコミマッップへのリンク
	function go_ecommap(ecommapurl, ecomuser, ecompass) {
		var url1 = ecommapurl + "map/login.jsp?logout=1";
		var url2 = ecommapurl + "map/login.jsp?auth=" + ecomuser + "&pass="
				+ ecompass;
		var childWin = window.open(url1, 'ecommap');
		childWin.focus();
		timerID = setTimeout(login_ecommap,2000,childWin,url2);
	}
	//	function loginOK(childWin,ecomuser,ecompass){
	function login_ecommap(win,url) {
		win.open(url, "ecommap");
	}

</script>

<script type="text/javascript">
	// サイドメニューの展開を畳み込み
	function toggleLiveResizing() {
		$.each($.layout.config.borderPanes, function(i, pane) {
			var o = myLayout.options[pane];
			o.livePaneResizing = !o.livePaneResizing;
		});
	};

	var myLayout;

	// レイアウト定義
	$(document).ready(function() {

		// this layout could be created with NO OPTIONS - but showing some here just as a sample...
		// myLayout = $('body').layout(); -- syntax with No Options

		myLayout = $('body').layout({

			//	reference only - these options are NOT required because 'true' is the default
			north__closable : false //true	// pane can open & close
			,
			west__closable : true // pane can open & close
			,
			north__cresizable : false //true	// when open, pane can be resized
			,
			north__resizable : true // when open, pane can be resized
			,
			west__resizable : true // when open, pane can be resized
			,
			slidable : true // when closed, pane can 'slide' open over other panes - closes on mouse-out
			,
			livePaneResizing : true
			//,
			//north__size : 50
			,
			north__minSize : 55
			//north__minSize : 80
			//	some pane-size settings
			,
			west__minSize : 100,
			center__minWidth : 100
			,
			//	some pane animation settings
			west__animatePaneSizing : false,
			west__fxSpeed_size : "fast" // 'fast' animation when resizing west-pane
			,
			west__fxSpeed_open : 1000 // 1-second animation when opening west-pane
			,
			west__fxSettings_open : {
				easing : "easeOutBounce"
			} // 'bounce' effect when opening
			,
			west__fxName_close : "none" // NO animation when closing west-pane

			//	enable showOverflow on west-pane so CSS popups will overlap north pane
			,
			west__showOverflowOnHover : false

			//	enable state management
			,
			stateManagement__enabled : true // automatic cookie load & save enabled by default

			,
			showDebugMessages : true
		// log and/or display messages from debugging & testing code
		});
		/* BackUp 機能は外す
		// backupdata_init();
		*/

	});

	if(typeof SaigaiTask=="undefined") {
		window.SaigaiTask = {
			contextPath: "<%=request.getContextPath()%>"
		};
	}
</script>

<script type="text/javascript" src="${f:url('/admin-js/js/menuView.js')}"></script>

<script type="text/javascript">
<!--
	// バックアップ機能のプルダウンを表示する
	$(document).ready(function() {
		$("#menu_backup li").hover(function() {
			$(this).children('ul').show();
		}, function() {
			$(this).children('ul').hide();
		});
	});

// メニュー選択
function menuLink(targetURL) {
	//２度押し対策
	//メニューリンクの押下直後、コンテンツエリアにすでにコンテンツが存在していた場合は、
	//wrapperエレメントは必ず存在しているため２度押し防止の解除に使えない。
	//コンテンツエリアのDOM構築済みフラグisReady（jQuery.ready() メソッドが実行済み）を利用する。
	//isReadyはコンテンツエリアの側だけのグリッド後、グリッド内容取得Ajax実行前にtureになる（はず）。
	//そこでメニューリンク押下直後にisReady＝falseをセットすることで、ページを読み込んでAjax実行前までを押下不可にできる。
	//なお、Ajax実行後はコンテンツ（Jqgrid）側の制御に任せる。
	var contentWindow = $('#mainFrame')[0].contentWindow;
	if(contentWindow.$ && contentWindow.$.isReady){
		contentWindow.$.isReady=false;
	}
	// 画面操作を行えない様にする
	FreezeScreen("");
	// 選択メニューに従いコンテンツエリアにコンテンツを表示する。
	mainFrame.location.href = targetURL;
	// コンテンツがロードされるまで監視する
	setTimeout(waiting, 500);
}

// イベント処理中の判定
function waiting() {
	try {
/*		if(parent.mainFrame.document.getElementById('wrapper')) {
			// フリーズ状態を解除
			FreezeScreenOff();
*/
		var contentWindow = $("#mainFrame")[0].contentWindow;
		if(contentWindow.$ && contentWindow.$.isReady){
			FreezeScreenOff();
		}else{
			// 管理を継続する
			setTimeout(waiting, 500);
		}
	} catch (e) {
		var my_url = window.location;
		top.location.href = my_url;
	}
}

// イベント時のフリーズ状態
function FreezeScreen(msg) {
	scroll(0,0);
	var outerPane = document.getElementById('FreezePane');
	var innerPane = document.getElementById('InnerFreezePane');
	if (outerPane) outerPane.className = 'FreezePaneOn';
	if (innerPane) innerPane.innerHTML = msg;
}


// イベント時のフリーズ状態をキャンセル
function FreezeScreenOff() {
	var outerPane = document.getElementById('FreezePane');
	if (outerPane) outerPane.className = 'FreezePaneOff';
}


//選択されたリンクに背景色を付ける。
$(document).ready(function() {
	$('#tree a').on('click', function(){
		$('li.collapsable>ul>li').css('background-color', '');
		$(this).parent().css('background-color', '#FBEC88');
	})
});

-->
</script>

</head>

<body>

	<!-- イベント時のフリーズ指定 -->
	<div align="center" id="FreezePane" class="FreezePaneOff">
	   <div id="InnerFreezePane" class="InnerFreezePane"> </div>
	</div>

	<!-- レイアウト設定でヘッダー部分 -->
	<div class="ui-layout-north">

		<div id="header_jsp">

		  <div id="global_header_admin">
			<div style="float: left;">
				<div id="global_header_title" style="float: left;">
					${f:h(systemName)}
				</div>
			</div>
			<div style="float: left;">
				<span class="admin_header1"><c:if test="${!empty version}">Ver ${f:h(version)} </c:if><%=lang.__("Admin window")%></span>
			</div>
			<div id="globalnav" style="float: right; padding-top:5px; width:auto; height:23px; line-height:23px; font-size:12px; ">
				<c:if test="${! loginDataDto.usual}">
					<a href="#" style="color:white; text-decoration:none; cursor: default;">[${f:h(loginDataDto.groupInfo.name)}]</a>
				</c:if>
				<c:if test="${loginDataDto.usual}">
					<a href="#" style="color:white; text-decoration:none; cursor: default;">[${f:h(loginDataDto.unitInfo.name)}]</a>
				</c:if>
				<a href="${f:url('/logout?type=admin')}" style="color:white; margin-left: 5px;"><%=lang.__("Logout")%></a>
			</div>
			<div style="clear: both;"></div>
		  </div>
		  <div>
			<!-- BackUp 機能は外す
		    <div style="float: right;">
				<ul id="menu_backup">
				<li><span class="header_command">Backup</span>
					<ul>
		       			<li><a href="#" onclick="backupdata_read()"><img class="icon" src="${f:url('/admin-js/css/images/backup-out.png')}">&nbsp;&nbsp;読込み</a></li>
						<li><a href="#" onclick="backupdata_save()"><img class="icon" src="${f:url('/admin-js/css/images/backup-in.png')}">&nbsp;&nbsp;保存</a></li>
					</ul>
				</li>
				</ul>
		    </div>
		    -->
		    <div style="clear:both;">
				<div style="float: left;margin-top:3px;">
					<span class="admin_header4">${f:h(localgovName)}</span>
				</div>
				<c:if test="${loginDataDto.admin}">
			    <div style="float: right;">
				  <a href="#" onclick="go_ecommap('${f:h(ecomimapUrl)}','${f:h(ecomuser)}','${f:h(ecompass)}')"><span
					class="header_command"><%=lang.__("e-Com map")%></span></a>
				  <a href="${f:url('/admin/setupper/')}"><span class="header_command"><%=lang.__("City configuration")%></span></a>
				  <a href="${f:url('/admin/disconnect/')}"><span class="header_command"><%=lang.__("Communication disruption")%></span></a>
				  <a href="${f:url('/admin/training/')}"><span class="header_command"><%=lang.__("Training panel")%></span></a>
			    </div>
			    </c:if>
				<c:if test="${! loginDataDto.admin}">
			    <div style="float: right;">
				  <a href="${f:url('/admin/setupper/')}"><span class="header_command"><%=lang.__("City configuration")%></span></a>
			    </div>
			    </c:if>
		    </div>
		  </div>
		</div>
	</div>

	<!-- レイアウト設定でサイドメニュー部分 -->
	<div class="ui-layout-west">

		<div class="menu_header"><%=lang.__("Configure and manage menu")%></div>

		<div id="main">
			<div id="sidetree">
				<!--<div class="treeheader">&nbsp;</div>  -->
				<div id="sidetreecontrol">
					<a href="?#"><%=lang.__("Collapse all")%></a> | <a href="?#"><%=lang.__("Expand all")%></a>
				</div>
				<script type="text/javascript">
					loadJSON(menuLink);
				</script>
			</div>
		</div>

	</div>

	<!-- レイアウト設定でセンター（コンテンツ表示）部分 -->
	<iframe id="mainFrame" name="mainFrame" class="ui-layout-center"
		width="100%" height="600" frameborder="0" scrolling="auto"
		src="about:blank"></iframe>

</body>
</html>
