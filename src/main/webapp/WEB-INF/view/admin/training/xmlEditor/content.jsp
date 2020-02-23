<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>

<%
	/* Copyright (c) 2013 National Research Institute for Earth Science and
	 * Disaster Prevention (NIED).
	 * This code is licensed under the GPL version 3 license, available at the root
	 * application directory.
	 */
%>
<%@include file="../../../common/lang_resource.jsp" %>
<%@ page import="jp.ecom_plat.map.util.FormUtils"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>

<link rel="stylesheet" href="${f:url('/css/style_w.css')}" type="text/css" />

<style type="text/css">
	#dialog_selecttrainingmeteodata h1{
		font-size:small!important;
	}
	#dialog_selecttrainingmeteodata h2{
		font-size:small!important;
	}
	#dialog_selecttrainingmeteodata th{
		font-size:small!important;
	}
	#dialog_selecttrainingmeteodata td{
		font-size:small!important;
	}

	#meteoxmlTable td {
		padding: 4px;
		border-color: #808080;
		border-width: 1px;
	}

	.cell-style-end {
		border-style: solid solid solid solid;
	}

	.cell-style-b-top {
		border-style: solid none none none;         /*グレー            上*/                      /*罫線表示: 上  右  下  左;*/
		background-color: #CCDDEF;
		width: 0.5em;
	}
	.cell-style-b-left {
		border-style: none none none solid;         /*グレー            左*/
		background-color: #CCDDEF;
		width: 0.5em;
	}
	.cell-style-b-lefttop {
		border-style: solid none none solid;         /*グレー            左上*/
		background-color: #CCDDEF;
		width: 0.5em;
	}
	.cell-style-b-leftright {
		border-style: none solid none solid;        /*グレー            左右*/
		background-color: #CCDDEF;
		width: 0.5em;
	}
	.cell-style-b-none {
		border-style: none none none none;          /*グレー            無し*/
		background-color: #CCDDEF;
		width: 0.5em;
	}
	.cell-style-p-top {
		border-style: solid none none none;         /*ピンク            上*/
		background-color: #F7E8E8;
		width: 0.5em;
	}

	.messageArea{
		margin-top:5px;
		margin-bottom:5px;
		color:red;
	}

</style>

<script type="text/javascript" src="${f:url('/js/training/xmlEditor/content_t.js')}"></script>
<script type="text/javascript" src="${f:url('/js/training/xmlEditor/content_t_xmlmain.js')}"></script>
<script type="text/javascript" src="${f:url('/js/training/xmlEditor/content_t01_xmlmaindata.js')}"></script>

<link href="${f:url('/css/jquery-ui-timepicker-addon.css')}" rel="stylesheet" type="text/css" />
<script src="${f:url('/js/jquery-ui-timepicker-addon.js')}" type="text/javascript"></script>
<c:if test='${lang.getLangCode().equals("ja")}'>
<script src="${f:url('/js/jquery-ui-timepicker-ja.js')}" type="text/javascript"></script>
</c:if>

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
//-->
</style>

<script type="text/javascript">

var g_frame_param_type = 1;

function doXmlSaveOKMessage()
{
	/*
	var bStat = $('#xmlfile_list_combobox').attr("disabled");
	alert(bStat);
	$('#xmlfile_list_combobox').attr("disabled",false);
	if($("#hidden_cur_save_file_name").val() != null && $("#hidden_cur_save_file_name").val() != "") {
		alert($("#hidden_cur_save_file_name").val());
		$("#xmlfile_list_combobox").val($("#hidden_cur_save_file_name").val());
	}
	//$('#xmlfile_list_combobox').attr("disabled",bStat);
	*/
	alert(MessageFormat.format("<%=lang.__("{0} saved.")%>", $("#hidden_cur_save_file_name").val()));
}

function doAjaxErrorCheck(statusTxt, responseTxt) {
	var contentT = $('#content');
	if(statusTxt=="error") {
		if(!!responseTxt) {
			contentT.html(responseTxt);
			$("input[type='button']", contentT).hide();
		}
		else {
			contentT.html("<%=lang.__("An error occurred.")%>");
		}
	}
	else if(statusTxt=="timeout") {
		contentT.html("<%=lang.__("Timeout occurred.")%>");
	}

}

function set_label_value_by_id_com(id, val)
{
	var label=document.getElementById(id);
	label.innerText=val;
	$("#" + id).html(val);
}

function doCurFileNameOnlyNewContent()
{
	doXmlLoadCurFileNameFromServerOnlyNewFile(
			$("#hidden_edit_type_save_data").val(),
			$("#hidden_file_type_save_data").val()
		);
}

function doFileTypeContent()
{
	doXmlLoadFileTypeFromServer();
}

function doFileListContent()
{

	doXmlLoadFileListFromServer(
			$("#hidden_edit_type_save_data").val(),
			$("#hidden_file_type_save_data").val()
		);
}

function doCancelContent()
{
	var content = $("#content_all");
	content.mask("Loading...");
	$("#main_exec").show();
	$("#main_save").hide();
	$("#main_cancel").hide();

	$('#file_type_combobox').attr("disabled",false);
	$('#edit_type_radio_area').attr("disabled",false);

	$('#edit_type_radio_1').attr("disabled",false);
	$('#edit_type_radio_2').attr("disabled",false);
	$('#edit_type_radio_3').attr("disabled",false);
	$('#xmlfile_list_combobox').attr("disabled",false);

	$('#content_typesw').empty();

	$('#hidden_cur_save_file_name').val("");
	$('#xmlfile_list_combobox').val("");

	if($("#hidden_edit_type_save_data").val() == 2) {
		// 編集する場合、ファイルリスト更新する必要
		doFileListContent();
	}

	/*
	doXmlSaveDataToServerFile(
			$("#hidden_edit_type_save_data").val(),
			$("#hidden_file_type_save_data").val()
		);
	*/
	content.unmask();
}

function doSaveContent()
{
	if(checkData($("#hidden_file_type_save_data").val()) == false) {
		return;
	}
	$("#main_exec").hide();

	if ($("#hidden_edit_type_save_data").val() == 1) {
		var tempFileName = "" + $("#hidden_cur_save_file_name").val();
		// alert(tempFileName);
		if($("#hidden_cur_save_file_name").val() == "" || tempFileName.endWith(".xml.new")) {
			// 新規
			showFileListDialog(1);
		} else {
			// 上書き？
			showFileListDialog(2);
			//doSaveXmlFileDirect();
		}
	}
	if ($("#hidden_edit_type_save_data").val() == 2) {
		// 編集
		showFileListDialog(2);
	}

	/*
	doXmlSaveDataToServerFile(
			$("#hidden_edit_type_save_data").val(),
			$("#hidden_file_type_save_data").val()
		);
	*/
}

function doSaveXmlFileDirect() {
	//alert($("#hidden_callalertdialogtype").val());
	if($("#hidden_cur_save_file_name").val() == "") {
		alert("<%=lang.__("Enter file name.")%>");
		return;
	}


	if(!($("#hidden_cur_save_file_name").val()+"").endWith(".xml")) {
		alert("<%=lang.__("File name extension must be .xml.")%>");
		return;
	}

	getJsonDataByJsp($("#hidden_file_type_save_data").val());
	var jsondata = t01NewGetJsonDataUrl();
	var orgfilename = encodeURIComponent($("#hidden_cur_save_file_name").val());
	var orgfiletype = $("#hidden_file_type_save_data").val();

	var content = $("#content_all");
	content.mask("Loading...");

	$('#content_typesw').load(
			'${f:url('xmltable?filename=')}' + orgfilename +
			'&apitype='+4+
			'&orgfilename='+orgfilename+
			'&filetype='+$("#hidden_file_type_save_data").val()
			+ '&jsondata='+jsondata
			,
			function(responseText, status, XMLHttpRequest) {
				content.unmask();
				if(status == "success") {
					doXmlSaveOKMessage();
					selectTrainingmeteoData(orgfilename, orgfiletype);
				} else {
					doAjaxErrorCheck(status, responseText);
				}
			});
}


function doSaveXmlFile() {
	//alert($("#hidden_callalertdialogtype").val());
	if($("#filename_input").val() == "") {
		alert("<%=lang.__("Enter file name.")%>");
		return;
	}


	if(!($("#filename_input").val()+"").endWith(".xml")) {
		alert("<%=lang.__("File name extension must be .xml.")%>");
		return;
	}

	if ($("#hidden_edit_type_save_data").val() == 1) {
		//new
	}
	if ($("#hidden_edit_type_save_data").val() == 1) {
		//edit
	}

	getJsonDataByJsp($("#hidden_file_type_save_data").val());
	var jsondata = t01NewGetJsonDataUrl();
	var orgfilename = encodeURIComponent($("#hidden_cur_save_file_name").val());
	var orgfiletype = $("#hidden_file_type_save_data").val();
	var filename = encodeURIComponent($("#filename_input").val());

	var content = $("#content_all");
	content.mask("Loading...");

	$('#content_typesw').load(
			'${f:url('xmltable?filename=')}' + filename +
			'&apitype='+4+
			'&orgfilename='+orgfilename+
			'&filetype='+$("#hidden_file_type_save_data").val()
			+ '&jsondata='+jsondata
			,
			function(responseText, status, XMLHttpRequest) {
				content.unmask();
				if(status == "success") {
					$("#hidden_cur_save_file_name").val($("#filename_input").val());
					doXmlSaveOKMessage();
					selectTrainingmeteoData($("#hidden_cur_save_file_name").val(), orgfiletype);
					doFileListContent();
				} else {
					doAjaxErrorCheck(status, responseText);
				}

			});
}

function doSaveEditXmlFile() {
	//alert($("#hidden_callalertdialogtype").val());


	if ($("#hidden_edit_type_save_data").val() == 1) {
		//new
	}
	if ($("#hidden_edit_type_save_data").val() == 1) {
		//edit
	}
	var content = $("#content_all");
	content.mask("Loading...");

	getJsonDataByJsp($("#hidden_file_type_save_data").val());
	var jsondata = t01NewGetJsonDataUrl();
	var orgfilename = encodeURIComponent($("#hidden_cur_save_file_name").val());
	var orgfiletype = $("#hidden_file_type_save_data").val();
	$('#content_typesw').load(
			'${f:url('xmltable?filename=')}' + orgfilename +
			'&apitype='+4+
			'&orgfilename='+orgfilename+
			'&filetype='+$("#hidden_file_type_save_data").val()
			+ '&jsondata='+jsondata
			,
			function(responseText, status, XMLHttpRequest) {
				content.unmask();
				if(status == "success") {
					doXmlSaveOKMessage();
					selectTrainingmeteoData(orgfilename, orgfiletype);
				} else {
					doAjaxErrorCheck(status, responseText);
				}

			});

}

function doSwitchContentSub()
{
	doXmlNewEditDelDataFromServerFile(
		$("#hidden_edit_type_save_data").val(),
		$("#hidden_file_type_save_data").val()
	);
}

function buttonAddClick(lineuid) {

	var content = $("#content_all");
	content.mask("Loading...");
	//content_typesw
	getJsonDataByJsp($("#hidden_file_type_save_data").val());
	var jsondata = t01NewGetJsonDataUrl();
	var filename = encodeURIComponent($("#hidden_cur_save_file_name").val());


	//$('#meteoxmlTable tbody').load(
	$('#content_typesw').load(
			'${f:url('xmltable?filename=')}' + filename +
			'&apitype='+5+
			'&filetype='+$("#hidden_file_type_save_data").val()
			+ '&lineuid='+lineuid
			+ '&jsondata='+jsondata
			,
			function(responseText, status, XMLHttpRequest) {
				addDataTime();
				content.unmask();
			});
}
function buttonDelClick(lineuid) {
	var content = $("#content_all");
	content.mask("Loading...");
	getJsonDataByJsp($("#hidden_file_type_save_data").val());
	var jsondata = t01NewGetJsonDataUrl();
	var filename = encodeURIComponent($("#hidden_cur_save_file_name").val());
	$('#content_typesw').load(
			'${f:url('xmltable?filename=')}' + filename +
			'&apitype='+6+
			'&filetype='+$("#hidden_file_type_save_data").val()
			+ '&lineuid='+lineuid
			+ '&jsondata='+jsondata
			,
			function(responseText, status, XMLHttpRequest) {
				addDataTime();
				content.unmask();
			});
}

function comboboxChange(lineuid) {
	getJsonDataByJsp($("#hidden_file_type_save_data").val());
	var jsondata = t01NewGetJsonDataUrl();
	var filename = encodeURIComponent($("#hidden_cur_save_file_name").val());

	var content = $("#content_all");
	content.mask("Loading...");


	$('#content_typesw').load(
			'${f:url('xmltable?filename=')}' + filename +
			'&apitype='+7+
			'&filetype='+$("#hidden_file_type_save_data").val()
			+ '&lineuid='+lineuid
			+ '&jsondata='+jsondata
			,
			function(responseText, status, XMLHttpRequest) {
				addDataTime();
				content.unmask();
			});
}

function doSwitchContent()
{
	if($("#hidden_file_type_save_data").val() == -1) {
		alert("<%=lang.__("File type is not selected.")%>");
		return;
	}
	if($("#hidden_edit_type_save_data").val() == 1) {
		//１．新規追加時：ファイル名を入力させて保存。この場合同一のファイル名が
		//存在する場合警告「ファイル名がすでにあります。うわがきしますか？」
		//「はい」で上書き実行。
		// new
		$("#hidden_cur_save_file_name").val("");
	}
	if($("#hidden_edit_type_save_data").val() == 2) {
		// edit
		if($("#hidden_cur_save_file_name").val() == "") {
			alert("<%=lang.__("Select a file.")%>");
			return;
		}

	}
	if($("#hidden_edit_type_save_data").val() == 3) {
		// del
		if($("#hidden_cur_save_file_name").val() == "") {
			alert("<%=lang.__("Select a file.")%>");
			return;
		}
		var delAppendMessage = getDelAppendMessage();
		var r = confirm("<%=lang.__("Delete?")%>" + delAppendMessage);
		if(r == true) {

		} else {
			return;
		}
	}

	if($("#hidden_edit_type_save_data").val() != 3) {
		$("#main_exec").hide();
		$("#main_save").show();
		$("#main_cancel").show();
		$('#file_type_combobox').attr("disabled",true);
		$('#edit_type_radio_area').attr("disabled",true);

		$('#edit_type_radio_1').attr("disabled",true);
		$('#edit_type_radio_2').attr("disabled",true);
		$('#edit_type_radio_3').attr("disabled",true);
		$('#xmlfile_list_combobox').attr("disabled",true);
	}

	var iType = 0;
	var iType = 0;
	var filename = encodeURIComponent($("#hidden_cur_save_file_name").val());

	var jsondata = null;
	var apitypet = 1;
	var edittype = $("#hidden_edit_type_save_data").val();
	if(edittype == 1) {
		// new
		apitypet = 1;
		jsondata = "";
	}
	if(edittype == 2) {
		// edit
		apitypet = 2;
		jsondata = getT01EditInfo_JsonDataUrl($("#hidden_cur_save_file_name").val());

	}
	if(edittype == 3) {
		// del
		apitypet = 3;
		jsondata = getT01EditInfo_JsonDataUrl($("#hidden_cur_save_file_name").val());
	}

	if($("#hidden_file_type_save_data").val() != -1) {
		var content = $("#content_all");
		content.mask("Loading...");
		$('#content_typesw').load(
				'${f:url('xmltable?filename=')}' + filename +
				'&apitype='+$("#hidden_edit_type_save_data").val()+
				'&filetype='+$("#hidden_file_type_save_data").val()+
				'&jsondata='+jsondata
				,function(responseTxt,statusTxt,xhr){
					content.unmask();
					//alert(statusTxt);
					if(statusTxt == "success") {

						if($("#hidden_edit_type_save_data").val() == 1) {
							// new 一時ファイル名を取得する
							doCurFileNameOnlyNewContent();

						}
						if($("#hidden_edit_type_save_data").val() == 2) {
							// 編集する場合、ファイルリスト更新する必要
							doFileListContent();
						}

						if($("#hidden_edit_type_save_data").val() == 3) {
							// 削除する場合、ファイルリスト更新する必要
							doFileListContent();

						}

						if($("#hidden_edit_type_save_data").val() == 3) {
							alert("<%=lang.__("Deleted")%>");
						}
					} else {
						doAjaxErrorCheck(statusTxt, responseText);
					}
				}

				);
	}
	if($("#hidden_edit_type_save_data").val() == 3) {
		// ファイルListを取得する
		doFileListContent();
	}

}
function fileTypeOnChanage(val) {
	$("#hidden_file_type_save_data").val(val);
	if($("#hidden_edit_type_save_data").val() == 2 ||
	   $("#hidden_edit_type_save_data").val() == 3) {
		doFileListContent();
	}
}
function fileListOnChanage(val) {
	$("#hidden_cur_save_file_name").val(val);
}


$(function() {
	var combobox = document.getElementById('file_type_combobox');
	doFileTypeContent();

	$.ajaxSetup({
	cache: false
	});

	var form = document.getElementById("contents_setup_form");
	// 新規作成の場合
	//if(SaigaiTask.loginDataDto.localgovinfoid==0) {
		// do nothing
	//}
	// 自治体指定の場合
	//else {
		// 編集は未実装のため、新規作成でない場合は入力フォームを無効にする。
		// とりあえず何もしない
//		$("input,select,textarea", form).attr("disabled", true);
	//}
	$("#main_exec").show();
	$("#main_save").hide();
	$("#main_cancel").hide();

	$('#file_type_combobox').attr("disabled",false);
	$('#edit_type_radio_area').attr("disabled",false);
	$('#edit_type_radio_1').attr("disabled",false);
	$('#edit_type_radio_2').attr("disabled",false);
	$('#edit_type_radio_3').attr("disabled",false);
	$('#xmlfile_list_combobox').attr("disabled",false);

	<c:set var="govid" value="${JsonGetCurLocalGovID}"/>
	<c:if test="${govid==0}">
		//BUG001 20151002
		$("#main_exec").hide();
		$("#main_save").hide();
		$("#main_cancel").hide();
		$('#file_type_combobox').attr("disabled",true);
		$('#edit_type_radio_area').attr("disabled",true);
		$('#edit_type_radio_1').attr("disabled",true);
		$('#edit_type_radio_2').attr("disabled",true);
		$('#edit_type_radio_3').attr("disabled",true);
		$('#xmlfile_list_combobox').attr("disabled",true);

	</c:if>
});

function _commonDialogInit(dialogName) {
	$('#' + dialogName).dialog({
		'autoOpen' : false
	});
}
function showFileListDialog(type) {

	var title = "<%=lang.__("Enter file name.<!--2-->")%>";
	if(type == 1 || type == 3) {
		title = "<%=lang.__("Enter file name.<!--2-->")%>";
	}
	if(type == 2) {
		title = "<%=lang.__("Info<!--5-->")%>";
	}

	//var url = 	SaigaiTask.contextPath + '/include/admin/training/xmlEditor/content_t_filelistdlg.html';
	var url = 	SaigaiTask.contextPath + '/admin/training/xmlEditor/filelistdlg';
	$('#dialog_tfilelist_xml')
			.load(
					url,
	function() {
		_commonDialogInit('dialog_tfilelist_xml');
		//領域名
		$("#dialog_tfilelist_xml").dialog({
			title : title,
			autoOpen : false,
			modal : true,
			buttons : {

				"<%=lang.__("Close")%>" : function() {
					//g_dialog_alarm_self_close_end = 3;
					$(this).dialog('close');
				},

			},
			width : 880,
			height : 195,
		});
		var dialogOpts = {
			open : function() {
				//alert('open');
				dialog_file_input_info_disp_init(type);
			},
			close : function() {

				//alert("Close=" + g_dialog_alarm_self_close_end); //OK

				//$("#dialog_alarm_process").dialog('close');
			},
			beforeclose : function() {
				//NG Support
				//alert('c3');
			}
		};

		$("#dialog_tfilelist_xml").dialog(dialogOpts);
		$("#dialog_tfilelist_xml").dialog('open');
		//table_tfilelist_xml_init_01();

	});

}


// INUIADD
function selectTrainingmeteoData(xmlFileName, fileType){
	var title = "<%=lang.__("Drill plan external data selection")%>";

	//var url = 	SaigaiTask.contextPath + '/include/admin/training/xmlEditor/content_t_selecttrainingmeteodatadlg.html';
	var url = SaigaiTask.contextPath + '/admin/training/xmlEditor/selecttrainingmeteodatadlg';
	$('#dialog_selecttrainingmeteodata')
			.load(
				url,
	function() {
		_commonDialogInit('dialog_selecttrainingmeteodata');
		//領域名
		$("#dialog_selecttrainingmeteodata").dialog({
			title : title,
			autoOpen : false,
			modal : true,
			buttons : {

				"<%=lang.__("Reflection")%>" : {
					text : "<%=lang.__("Reflection")%>",
					id : "dialog_selecttrainingmeteodata_saveButton",
					click : function() {
						if(confirmUpdateTrainingmeteoData()){
							$(this).dialog('close');
						}
					}
				},
				"<%=lang.__("Close")%>" : {
					text : "<%=lang.__("Close")%>",
					id : "dialog_selecttrainingmeteodata_closeButton",
					click : function() {
						$(this).dialog('close');
					}
				}
			},
			width : 900,
			height : 330,
		});
		var dialogOpts = {
			open : function() {
				dialog_select_trainingmeteo_data(xmlFileName, fileType);
			},
			close : function() {
				//alert("Close=" + g_dialog_alarm_self_close_end); //OK
				//$("#dialog_alarm_process").dialog('close');
			},
			beforeclose : function() {
			}
		};

		$("#dialog_selecttrainingmeteodata").dialog(dialogOpts);
		$("#dialog_selecttrainingmeteodata").dialog('open');

	});
}

function getDelAppendMessage(){
	var url = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxGetDelTrainingmeteoData";
	var localgovinfoid = $("#loginlocalgov-select").val();
	if(!localgovinfoid){
		localgovinfoid = "";
	}
	var meteotypeid = $("#file_type_combobox").val();
	var xmlFileName = $("#xmlfile_list_combobox").val();

    var retMessage = ""
    $.ajax({
		async : false,
		url : url,
		type : 'POST',
		data : {
			localgovinfoid : localgovinfoid,
			meteotypeid : meteotypeid,
			xmlFileName : xmlFileName
		},
		success : function(data, textStatus, xhr) {
			var json = data;
			retMessage = data.retmessage;
		},
		error: function(xhr, textStatus, errorThrown) {
			content.unmask();
			doAjaxErrorCheck(textStatus, xhr.responseText);

		},
		contentType : "application/x-www-form-urlencoded;charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept-Charset", "utf-8");
			xhr.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=utf-8");
		},
	});
	return retMessage;
}
</script>


<script type="text/javascript">

// ”新規追加”、”編集”、”削除”、radioボタン毎に下記の通り値とメゾンドを定義する。
// 新規追加の場合は”ファイル選択のコンボボックス”を表示しない、編集、削除の場合はそのコンボボックスを表示する

//  radioボタン（名）         val（値）         対応するアクション
//   新規追加                     1         表示しない
//	  編集			 2                     表示する
//   削除			 3                     表示する
function editTypeOnChanage(tag){
	$("#hidden_edit_type_save_data").val(tag);
	$("#hidden_cur_save_file_name").val("");

	var combobox2 = document.getElementById('xmlfile_list_combobox');
	if(tag=='1'){
		combobox2.style.display = 'none';
	}
	else{
		combobox2.style.display = '';
		doFileListContent();
	}
}

</script>

<div id="content_all">
<label class="alarm_process_error" id="alarm_process_res_error_id"></label>
<br />

<input type="hidden" id="hidden_file_type_save_data" value="-1">
<input type="hidden" id="hidden_edit_type_save_data" value="1">
<input type="hidden" id="hidden_cur_save_file_name" value="">
<input type="hidden" id="hidden_callalertdialogtype" value="1">


<div id="content_head" class="ui-layout-north">
	<h1><%=lang.__("Meteorological disaster info XML editing window")%></h1>
	<div class="messageArea">
		<c:if test="${localgovinfoidSelected == false}" >
			<%=lang.__("First, you need to select local govornment.")%>
		</c:if>
	</div>
</div>

<h2 style="margin: 0px;"><%=lang.__("Disaster info type")%></h2>
<table border="1" class="form">
	<tr>
		<th><%=lang.__("Select disaster info type")%></th>
		<td>
			<div id="head2_text">
<!--
					<option value="1">地震速報</option>
					<option value="2">地震・震度に関する情報</option>
					<option value="3">津波警報・注意報</option>
					<option value="4">噴火警報・予報</option>
					<option value="5">気象警報・注意報</option>
					<option value="6">指定河川洪水予報</option>
					<option value="7">土砂災害警戒情報</option>
					<option value="8">記録的短時間大雨情報</option>
					<option value="9">竜巻注意報</option>
-->
				<select id="file_type_combobox" style="width: 230px;" class="box" onchange="fileTypeOnChanage(this.value);">
					<option></option>

				</select>
			</div>
		</td>
	</tr>
	<tr>
		<th><%=lang.__("Select editing mode")%></th>
		<td>
			<div id="edit_type_radio_area">
				<input id="edit_type_radio_1" type="radio" name="example" value="1" onclick="editTypeOnChanage(1);" checked="checked" ><%=lang.__("Add new")%>
				<input id="edit_type_radio_2" type="radio" name="example" value="2" onclick="editTypeOnChanage(2);" ><%=lang.__("Edit")%>
				<input id="edit_type_radio_3" type="radio" name="example" value="3" onclick="editTypeOnChanage(3);" ><%=lang.__("Delete")%>
				<div>
  						<select id="xmlfile_list_combobox" style= "width: 300px; display:none" class="box"
  						onchange="fileListOnChanage(this.value);" >
   						<option></option>

	  					</select>
 				</div>
			</div>
		</td>
	</tr>


</table>

<button id="main_exec" class="btn botton" name="alarm_process_uid" onclick="doSwitchContent();"><%=lang.__("Execution")%></button>
<button id="main_save" class="btn botton" name="alarm_process_uid" onclick="doSaveContent();"><%=lang.__("Save")%></button>
<button id="main_cancel" class="btn botton" name="alarm_process_uid" onclick="doCancelContent();"><%=lang.__("Cancel")%></button>
<br/>
<br/>
<br/>


<div id="content_typesw">

</div>


<div id="content_footer" class="ui-layout-south"></div>

<div id="dialog_tfilelist_xml"></div>
<div id="dialog_selecttrainingmeteodata"></div>

</div>

