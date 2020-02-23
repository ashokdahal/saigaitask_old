<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@page pageEncoding="UTF-8"%>
<%--
<link rel="shortcut icon" href="${f:url('/map/icons/favicon.ico')}" type="image/vnd.microsoft.icon" />
--%>

<title>${f:h(siteName)}</title>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.layout-1.4.0.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/btn.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery-ui-button.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/extjs/resources/css/ext-all.css')}"/>
<link rel="stylesheet" type="text/css" href="${f:url('/js/extjs/resources/css/data-view.css')}"/>
<link rel="stylesheet" type="text/css" href="${f:url('/css/thickbox.css')}"/>
<link rel="stylesheet" type="text/css" href="${f:url('/css/print.css')}" media="print" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.css" />
<%--各ライブラリのフォントなどを上書きするため、一番最後に header.cssをロードする --%>
<link rel="stylesheet" type="text/css" href="${f:url('/css/header.css')}" />
<style type="text/css">
#content_main {
	padding: 0;
	padding-top: 3px;
}
/* jQuery のモーダルがExtJSよりも上になるように修正 */
.ui-front {
	z-index: 20000;
}
/* ext-all.css のスタイルを先に定義しておく。後からレイアウト調整されてちらつかないように。*/
body {
	font-size: 12px;
	font-family: tahoma,arial,verdana,sans-serif;
}
table {
	border-collapse: collapse;
	border-spacing: 0;
}
* {
	box-sizing: border-box; /* x-border-box */
}
/* jquery.layout-default.css のオーバーライド */
.ui-layout-pane-north {
	padding: 0px;
	overflow: hidden;
}
</style>
<%@include file="lang_resource.jsp" %>
<script type="text/javascript">
window.SaigaiTask = {
	contextPath: "<%=request.getContextPath()%>",
	csrfToken: "<%= session.getId() %>"
};

//for IE
{
	// ダミーのコンソール
	SaigaiTask.console = {
		log : function() {},
		debug : function() {},
		info : function() {},
		warn : function() {},
		error : function() {},
		assert : function() {},
		trace : function() {},
		time: function() {},
		timeEnd: function() {}
	};

	if (typeof window.console == "undefined") {
		window.console = SaigaiTask.console;
	}
	else {
		for ( var idx in SaigaiTask.console) {
			if (typeof window.console[idx] == "undefined") {
				window.console[idx] = SaigaiTask.console[idx];
			}
		}
	}
}
</script>
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<c:if test="${lang.getLangCode()!='en'}">
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/i18n/jquery.ui.datepicker-'.concat(lang.getLangCode()).concat('.js'))}"></script>
</c:if>
<script type="text/javascript" src="${f:url('/js/jquery.layout-1.4.0.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.form.js')}"></script>
<script type="text/javascript" src="${f:url('/js/btn.js')}"></script>
<script type="text/javascript" src="${f:url('/js/extjs/ext-all.js')}"></script>
<script type="text/javascript">
console.time("Ext.onReady");
Ext.onReady(function() {
	console.timeEnd("Ext.onReady");
	// ツールチップを初期化
	Ext.QuickTips.init();
});
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
	SaigaiTask.ajaxcsrfToken = token;
});
</script>
<script type="text/javascript" src="${f:url('/js/saigaitask.layout.js')}"></script>
<script type="text/javascript" src="${f:url('/js/extjs/ux/DataView/LabelEditor.js')}"></script>
<script type="text/javascript" src="${f:url('/js/extjs/ux/DataView/DragSelector.js')}"></script>
<script type="text/javascript" src="${f:url('/js/extjs/locale/ext-lang-ja.js')}"></script>
<script type="text/javascript" src="${f:url('/js/thickbox.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.min.js"></script>
<script type="text/javascript" src="${f:url('/js/jquery.edit-table.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.pageurl.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.date.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.edit.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.finger.js')}"></script>
