<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="/WEB-INF/view/common/jsp_lang.jsp" %>

<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/style.css')}" />
<!--link rel="stylesheet" type="text/css" href="${f:url('/js/tablesorter-2.28.5/css/theme.jui.css')}" /-->
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery-ui-timepicker-addon.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/lightbox.min.css')}" />
<style type="text/css">
<!--
.tablesorter TD.sum {
	background-color: #1E4B78;
	color: white;
	text-align: center;
}
.ui-datepicker .ui-datepicker-buttonpane button.ui-datepicker-current {
	opacity: 1;
}
.fixed {
	position: fixed;
	z-index: 10000;
}
tr.group-header td {
	background: #eee;
}
.group-name {
	text-transform: uppercase;
	font-weight: bold;
}
.group-count {
	color: #999;
}
.group-hidden {
	display: none;
}
.icon-gray {
	-webkit-filter: grayscale(1); /* Webkit */
	filter: gray; /* IE6-9 */
	filter: grayscale(1); /* W3C */
}

/* collapsed arrow */
tr.group-header td i {
	display: inline-block;
	width: 0;
	height: 0;
	border-top: 4px solid transparent;
	border-bottom: 4px solid #888;
	border-right: 4px solid #888;
	border-left: 4px solid transparent;
	margin-right: 7px;
}
tr.group-header.collapsed td i {
	border-top: 5px solid transparent;
	border-bottom: 5px solid transparent;
	border-left: 5px solid #888;
	border-right: 0;
	margin-right: 10px;
}
//-->
</style>
<!--script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script-->
<script type="text/javascript" src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.js')}"></script>
<script src="${f:url('/js/tablesorter-2.28.5/js/jquery.tablesorter.widgets.js')}"></script>
<script src="${f:url('/js/tablesorter-2.28.5/js/parsers/parser-input-select.js')}"></script>
<script src="${f:url('/js/tablesorter-2.28.5/js/widgets/widget-grouping.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-timepicker-addon.js')}"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script type="text/javascript" src="${f:url('/js/jquery-ui-timepicker-ja.js')}"></script>
</c:if>
<script type="text/javascript" src="${f:url('/js/jquery.upload-1.0.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.list.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.filter.js')}"></script>
<!-- 意志決定支援のJavascript -->
<script type="text/javascript" src="${f:url('/js/saigaitask.page.decisionSupport.js')}"></script>
<script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/falutil.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.detail.js')}"></script>
<script type="text/javascript">SaigaiTask.timesliderConf = ${empty pageDto.timesliderConf ? 'null' : pageDto.timesliderConf}</script>
<script type="text/javascript" src="${f:url('/js/saigaitask.page.timeslider.js')}"></script>
<script type="text/javascript">
$(function() {
	//ファイルアップロード
	// アップロードファイルを指定したら、ファイル送信するイベントハンドラ初期化
	// ecommap用のファイルアップロード処理
	$('#upfile').change(function() {
		//var gid = $('#upgid').val();
		var gid = $('#orgid').val();
		var mid = $('#menuid').val();
		var session_token = $('[name=session_token]').val();
		var type = 'text';
		// callback関数の処理に関わらず、地物への登録は完了済み
		// なので、リストの更新ボタンをクリックする必要は無い
		var callback = function(res) {
		    //alert('File uploaded');
		    $("#file-dialog").dialog("close");
			//alert("<%=lang.__("File registered.")%>");
			if(typeof res!="undefined") {
				try{
					var result = Ext.decode(res);
					var msg = result.success ? "" : "<%=lang.__("Failed to upload file.")%>";
					if(typeof result.msg!="undefined") {
						msg += "</br>" + result.msg;
					}
					Ext.Msg.show({
						title: result.success ? "" : "<%=lang.__("Error")%>",
						msg: msg,
						buttons: Ext.MessageBox.OK,
						icon: result.success ? Ext.MessageBox.INFO : Ext.MessageBox.ERROR
					});
				} catch(e) {
					// do nothing
				}
		    }
			return this;
		};
		if(confirm("<%=lang.__("Are you sure to try sending file?")%>")) {
			var url = "./list/upphoto/"+mid+"/"+gid+"?session_token="+session_token;
			$(this).upload(url, callback, type);
		}
		//$(this).upload(url, callback);
	});
});
var ecommapURL = "${ecommapURL}";
</script>

