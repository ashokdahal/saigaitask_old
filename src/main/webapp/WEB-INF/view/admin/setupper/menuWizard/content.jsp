<%@page import="jp.ecom_plat.saigaitask.constant.Menutype"%>
<%@page import="jp.ecom_plat.saigaitask.util.Constants"%>
<%/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<%@include file="../../../common/lang_resource.jsp" %>
<style type="text/css">
#menuWizardStepError {
	color:red;
}

#menuWizardStepContent{
	margin-bottom:1em;
}
#menuWizardStepContent select{
	width:200px;
}
#menuWizardStepContent table{
	table-layout:fixed;
	width:1000px;
}

.menuWizard_contents_hidden{
  display: none;
}

.menuWizard_contents_tbody tr:first-child {
  display: none;
}
.menuWizard_contents_table td {
	padding-top: 3px;
	padding-bottom: 3px;
	padding-left: 1em;
	padding-right: 1em;
	text-align:left;
	border: 1px #000000 solid;
	font-size: 12px;
}

input[type=radio]{
	text-align:center!important;
}

.menuWizard_contents_editableText:focus{
	border-width: 5px 5px 5px 5px!important;
	border-color: red red red red!important;
	outline: 0;
}
.menuWizard_contents_editableText{
	border-width: 2px 2px 2px 2px!important;
	border-color: red red red red!important;
}
.menuWizard_contents_disable{
	background-color:#AAAAAA;
}

.menuWizard_contents_table th {
	padding-top: 3px;
	padding-bottom: 3px;
	padding-left: 1em;
	padding-right: 1em;
	border: 1px #000000 solid;
	background-color: #ABCDEF;
	font-size: 12px;
}
.menuWizard_head_target{
	width: 80px!important;
}
.menuWizard_head_important{
	width: 110px!important;
}
.menuWizard_head_select{
	width: 50px!important;
}
.menuWizard_head_availableFlag{
	width: 115px!important;
}
.menuWizard_head_addFlag{
	width: 110px!important;
}
.menuWizard_head_deleteFlag{
	width: 110px!important;
}
.menuWizard_head_totalFlag{
	width: 110px!important;
}

.menuWizard_head_editable{
	width: 80px!important;
}
.menuWizard_head_highlight{
	width: 80px!important;
}
.menuWizard_head_grouping{
	width: 80px!important;
}
.menuWizard_head_sortable{
	width: 80px!important;
}
.menuWizard_head_defaultsort{
	width: 80px!important;
}
.menuWizard_head_uploadable{
	width: 80px!important;
}
.menuWizard_head_loggable{
	width: 80px!important;
}

.menuWizard_head_visibleFlag{
	width: 80px!important;
}
.menuWizard_head_closedFlag{
	width: 80px!important;
}
.menuWizard_head_editableFlag{
	width: 80px!important;
}
.menuWizard_head_addableFlag{
	width: 80px!important;
}
.menuWizard_head_searchableFlag{
	width: 80px!important;
}
.menuWizard_head_snapableFlag{
	width: 80px!important;
}
.menuWizard_head_intersectionlayerid{
	width: 80px!important;
}

.menuWizard_head_filterid{
	width: 120px!important;
}

.menuWizard_head_disporder{
	width: 120px!important;
}
.menuWizard_head_addButton{
	width: 70px!important;
}
.menuWizard_head_delButton{
	width: 70px!important;
}
.menuWizard_contents_table tr:hover {
  background-color: #FFCC99;
}
.menuWizard_contents_table td:hover {
  background-color: #CCFFFF;
}
.dialog_add_menutasktype_error{
	color:red;
}
.dialog_upload_exceltemplate_error{
	color:red;
}
.dialog_upload_exceltemplate_success{
	color:black;
}



</style>

<script type="text/javascript" src="${f:url('/admin-js/js/ajaxfileupload.js')}" ></script>
<script type="text/javascript">
var cancelButtonEnable = false;
var backButtonEnable = false;
var nextButtonEnable = false;
var finishButtonEnable = false;
var reloadButtonEnable = false;
var step3ColNum;


$(function(){
	nextContents();
	disableFunctionButtons();
	enableFunctionButtons();

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});

	// メニュータスク種別追加ダイアログ
    $( "#dialog_add_menutasktype" ).dialog({
        title: "<%=lang.__("Menu task type")%><%=lang.__("Add")%>",
        width:600,
        height:300,
        modal: true,
        autoOpen: false,
        buttons: {
            <%=lang.__("Registration")%>: function() {

            	var token = "${cookie.JSESSIONID.value}";
            	var url = SaigaiTask.contextPath+"/admin/setupper/menuWizard/addMenutasktype";

            	var menutasktype_name = $("#dialog_add_menutasktype_name").val();
            	var menutasktype_note = $("#dialog_add_menutasktype_note").val();
            	
            	
            	if(menutasktype_name){
                	$.ajax({
                		dataType: "json",
                		type: "POST",
                		headers: {"X-CSRF-Token":token},
                		cache: false,
                		url: url,
                		data: {
                			"name" : menutasktype_name,
                			"note" : menutasktype_note
                		},
                		success: function(response) {
            				$("#dialog_add_menutasktype_message").empty();
                			if(! response.success){
                				$("#dialog_add_menutasktype_message").append(
                						"<span class=\"dialog_add_menutasktype_error\">"
                						+ response.message
                						+ "</span>"
                						);
                			}
                			else{
                				alert('<%=lang.__("{0} registered.",lang.__("Menu task type"))%>');
                                $("#dialog_add_menutasktype").dialog( "close" );
                                reloadStep2SelectContents();
                			}
                		},
                		error: function(XMLHttpRequest, textStatus, errorThrown) {
            				$("#dialog_add_menutasktype_message").empty();
            				$("#dialog_add_menutasktype_message").append(
            						"<span class=\"dialog_add_menutasktype_error\">"
            						+ '<%=lang.__("An error occurred.")%> :'
            						+ textStatus
            						+ "</span>"
            						);
                		}
                	});
            	}else{
    				$("#dialog_add_menutasktype_message").empty();
    				$("#dialog_add_menutasktype_message").append(
    						"<span class=\"dialog_add_menutasktype_error\">"
     						+ '<%=lang.__("Name is required.")%>'
    						+ "</span>"
    						);

            	}
            },
            <%=lang.__("Cancel")%>: function() {
	            $( this ).dialog( "close" );
			}
        },
        open:function(){
			$("#dialog_add_menutasktype_message").empty();
        }
    });
	// メニュータスク種別新規追加ボタンが押下された場合は、
	// メニュータスク種別追加ダイアログを開く
	$(document).on("click", "#menuWizard_step2_menutasktype_addbutton", function () {
		 $( "#dialog_add_menutasktype" ).dialog( "open" );
	});

	// エクセル帳票テンプレートファイルアップロードダイアログ
    $( "#dialog_upload_exceltemplate" ).dialog({
        title: "<%=lang.__("Excel template file")%><%=lang.__("Upload")%>",
        width:650,
        height:300,
        modal: true,
        autoOpen: false,
        buttons: {
            <%=lang.__("Registration")%>: function() {

            	$("#dialog_upload_exceltemplate").mask("Loading...");
            	var submitButtonName = "<%=lang.__("Registration")%>";
            	var cancelButtonName = "<%=lang.__("Cancel")%>";
            	$(".ui-dialog-buttonpane button:contains(submitButtonName)").button("disable");
				$(".ui-dialog-buttonpane button:contains(cancelButtonName)").button("disable");

            	var fileInput = $("#excellisttemplatetempfile").val();
            	if(fileInput.length > 0){
                	var token = "${cookie.JSESSIONID.value}";
                	var url = SaigaiTask.contextPath+"/admin/setupper/menuWizard/uploadExcelTemplate";
                	var fd = {
                			"mode": "temp"
                		};
                	$.ajaxFileUpload({
                		secureuri: false,
                		fileElementId: 'excellisttemplatetempfile',
                		async: false,
                		dataType: "json",
                		type: "POST",
                		headers: {"X-CSRF-Token":token},
                		cache: false,
                		url: url,
                		data: fd,
                        processData: false,
                        contentType: false,
                        success: function(data) {
                        	$("#dialog_upload_exceltemplate").unmask();
                        	$(".ui-dialog-buttonpane button:contains(submitButtonName)").button("enable");
            				$(".ui-dialog-buttonpane button:contains(cancelButtonName)").button("enable");

            				$("#dialog_upload_exceltemplate_message").empty();
            				if (data.message) {
                				$("#dialog_upload_exceltemplate_message").empty();
                				$("#dialog_upload_exceltemplate_message").append(
                						"<span class=\"dialog_upload_exceltemplate_error\">"
                						+ data.message
                						+ "</span>"
                						);
            				}
            				else {
                				$("#dialog_upload_exceltemplate_message").empty();
                				$("#dialog_upload_exceltemplate_message").append(
                						"<span class=\"dialog_upload_exceltemplate_success\">"
                						+ "<%=lang.__("Succeed at upload file.")%>"
                						+ "</span>"
                						);
                				// ファイル名を保存
                				var filename = data.filename;
                				var targetTd = $("#menuWizardStepContent").children("table").children("tbody").children("tr").children("td")[step3ColNum+1];
                				$(targetTd).empty();
                				$(targetTd).append("<span>" + filename + "</span>");
            				}
            			},
            			error: function(data, status, e) {
                        	$("#dialog_upload_exceltemplate").unmask();
                        	$(".ui-dialog-buttonpane button:contains(submitButtonName)").button("enable");
            				$(".ui-dialog-buttonpane button:contains(cancelButtonName)").button("enable");

            				$("#dialog_upload_exceltemplate_message").empty();
            				if(data.responseText){
            					try{
            						var responseJson = $.parseJSON(data.responseText);
                    				$("#dialog_upload_exceltemplate_message").append(
                    						"<span class=\"dialog_upload_exceltemplate_error\">"
                    						+ unescapeHTML(responseJson.message)
                    						+ "</span>"
                    						);
            					}
            					catch(e){
                    				$("#dialog_upload_exceltemplate_message").append(
                    						"<span class=\"dialog_upload_exceltemplate_error\">"
                    						+ "<%=lang.__("Failed to upload the excel template file.") %>"
                    						+ "</span>"
                    						);
            					}
            				}else{
                				$("#dialog_upload_exceltemplate_message").append(
                						"<span class=\"dialog_upload_exceltemplate_error\">"
                						+ "<%=lang.__("Failed to upload the excel template file.") %>"
                						+ "</span>"
                						);
            				}
            			}
                	});
            	}else{
    				$("#dialog_upload_exceltemplate_message").empty();
    				$("#dialog_upload_exceltemplate_message").append(
    						"<span class=\"dialog_add_menutasktype_error\">"
     						+ '<%=lang.__("Excel template file: this item is required.")%>'
    						+ "</span>"
    						);

            	}
            },
            <%=lang.__("Cancel")%>: function() {
	            $( this ).dialog( "close" );
			}
        },
        open:function(){
        	$("#excellisttemplatetempfile").val("");
        	$("#dialog_upload_exceltemplate_message").empty();
        }
    });

	// メニュータイププルダウンでエクセル帳票を選択した場合は、
	// エクセル帳票テンプレートファイルアップロードダイアログを開く
	$(document).on("change", ".menuWizard_contents_select_step3", function () {
		var selectedId =  $(this).val();
		if(selectedId ==='<%=Menutype.EXCELLIST%>'){
			 // 位置を特定
			 step3ColNum = $("td").index($(this).parent());
			 $( "#dialog_upload_exceltemplate" ).dialog( "open" );
		}
	});

});

function disableFunctionButtons(){
	// TABLE行の追加
	$(document).off("click", ".menuWizard_addbutton");
	// TABLE行の削除
	$(document).off("click", ".menuWizard_delbutton");
	// TABLE行の上移動
	$(document).off("click", ".menuWizard_contents_lineup");
	// TABLE行の下移動
	$(document).off("click", ".menuWizard_contents_linedown");
}

function enableFunctionButtons(){
	// TABLE行の追加
	$(document).on("click", ".menuWizard_addbutton", function () {
		$(".menuWizard_contents_tbody > tr").eq(0).clone(true).insertAfter(
			$(this).parent().parent()
		);
	});

	// TABLE行の削除
	$(document).on("click", ".menuWizard_delbutton", function () {
		$(this).parent().parent().css({display:'none'});
		// 削除フラグセット
		$(this).parent().parent().children().eq(1).text("1");
		// 処理対象ラジオボタン解除
		$(this).parent().parent().children().eq(2).children().eq(0).prop('checked', false);

	});

	// TABLE行の上移動
	$(document).on("click", ".menuWizard_contents_lineup", function () {
		var row = $(this).parent().parent();
		if($(row).hasClass('menuWizard_contents_disable')){
			return;
		}
		var dest = getVisblePrevRow(row);
		if(dest != null){
			if($(dest).hasClass('menuWizard_contents_disable')){
				return;
			}
			row.insertBefore(dest);
		}
	});

	// TABLE行の下移動
	$(document).on("click", ".menuWizard_contents_linedown", function () {
		var row = $(this).parent().parent();
		if($(row).hasClass('menuWizard_contents_disable')){
			return;
		}
		var dest = getVisbleNextRow(row);
		if(dest != null){
			if($(dest).hasClass('menuWizard_contents_disable')){
				return;
			}
			row.insertAfter(dest);
		}
	});

}


function getVisblePrevRow(table){
	if(table.prev("tr")) {
		if(table.prev("tr").css("display") === 'none'){
			return getVisblePrevRow(table.prev("tr"));
		}else{
			return table.prev("tr")[0];
		}
	}else{
		return null;
	}
}

function getVisbleNextRow(table){
	if(table.next("tr")) {
		if(table.next("tr").css("display") === 'none'){
			return getVisbleNextRow(table.next("tr"));
		}else{
			return table.next("tr")[0];
		}
	}else{
		return null;
	}
}

function nextContents(){
	deleteCheck();
	return loadContents("next");
}

function backContents(){
	return loadContents("back");
}

function reloadContents(){
	return loadContents("reload");
}

function reloadStep2SelectContents(){
	return loadContents("reload");
}


function finishContents(){
	if(window.confirm("<%=lang.__("Complete wizard?")%>")){
		deleteCheck();
		return loadContents("finish");
	}else{
		return false;
	}
}

function cancelContents(){
	if(window.confirm("<%=lang.__("Cancel wizard?")%>")){
		var isCalledAdminPage = false;
		if(document.referrer.indexOf(SaigaiTask.contextPath+"/admin/") != -1){
			isCalledAdminPage = true;
		}
		if(isCalledAdminPage){
			window.location.href = SaigaiTask.contextPath+"/admin/setupper/";
		}else{
			window.close();
		}
		return true;
	}else{
		return false;
	}
}

function loadGUList(){
	var token = "${cookie.JSESSIONID.value}";
	var url = SaigaiTask.contextPath+"/admin/setupper/menuWizard/loadGUList";
	var localgovinfoid = $('#menuWizardStepM1_select1').val();

	$('#menuWizardStepM1_select2 > option').remove();

	$.ajax({
		dataType: "json",
		type: "POST",
		headers: {"X-CSRF-Token":token},
		cache: false,
		url: url,
		data: {
			localgovinfoid:localgovinfoid
		},
		success: function(response) {
			var data = response.list;
			if(data){
				for(var i = 0; i < data.length; i++){
					$('#menuWizardStepM1_select2').append($('<option>').html(data[i].value).val(data[i].key));
				}
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
		}
	});
}


function loadContents(ope){

	var token = "${cookie.JSESSIONID.value}";
	var url = SaigaiTask.contextPath+"/admin/setupper/menuWizard/wizard";

	if(ope === "next" || ope === "finish"){
		if(! validate()){
			return false;
		}
	}

	jsonDataSetting(ope);
	var jsonData = $("#menuWizardJsonData").val();
	// マスク
	$("#menuWizardPanel").mask("Loading...");
	$.ajax({
		dataType: "json",
		type: "POST",
		headers: {"X-CSRF-Token":token},
		cache: false,
		url: url,
		data:{jsonData:jsonData},
		success: function(response) {
			if(response.errors){
			}
			if(response.finished){
				var isCalledAdminPage = false;
				if(document.referrer.indexOf(SaigaiTask.contextPath+"/admin/") != -1){
					isCalledAdminPage = true;
				}
				if(isCalledAdminPage){
					window.location.href = SaigaiTask.contextPath+"/admin/setupper/";
				}else{
					window.close();
				}
			}else{
				if(response.cancelButton){
					cancelButtonEnable = true;
					$('#menuWizardCancelButton').removeClass('lgray');
					$('#menuWizardCancelButton').addClass('blue');
				}else{
					cancelButtonEnable = false;
					$('#menuWizardCancelButton').removeClass('blue');
					$('#menuWizardCancelButton').addClass('lgray');
				}

				if(response.backButton){
					backButtonEnable = true;
					$('#menuWizardBackButton').removeClass('lgray');
					$('#menuWizardBackButton').addClass('blue');
				}else{
					backButtonEnable = false;
					$('#menuWizardBackButton').removeClass('blue');
					$('#menuWizardBackButton').addClass('lgray');
				}

				if(response.nextButton){
					nextButtonEnable = true;
					$('#menuWizardNextButton').removeClass('lgray');
					$('#menuWizardNextButton').addClass('blue');
				}else{
					nextButtonEnable = false;
					$('#menuWizardNextButton').removeClass('blue');
					$('#menuWizardNextButton').addClass('lgray');
				}

				if(response.finishButton){
					finishButtonEnable = true;
					$('#menuWizardFinishButton').removeClass('lgray');
					$('#menuWizardFinishButton').addClass('blue');
				}else{
					finishButtonEnable = false;
					$('#menuWizardFinishButton').removeClass('blue');
					$('#menuWizardFinishButton').addClass('lgray');
				}

				if(response.reloadButton){
					reloadButtonEnable = true;
					$('#menuWizardReloadButton').removeClass('lgray');
					$('#menuWizardReloadButton').addClass('blue');
				}else{
					reloadButtonEnable = false;
					$('#menuWizardReloadButton').removeClass('blue');
					$('#menuWizardReloadButton').addClass('lgray');
				}
				$("#menuWizardStepTitle").text(response.title);
				$("#menuWizardStepDescription").text(response.description);

				// エラーメッセージ
				$("#menuWizardStepError").empty();
				if(response.errors){
					$("#menuWizardStepError").append(response.errors);
				}

				// 各ステップのコンテンツ
				$("#menuWizardStepContent").empty();
				$("#menuWizardStepContent").append(response.contents);

				// 1件もデータがない場合、行を追加しておく
				var tableRow = $(".menuWizard_contents_tbody tr");
				var jd = JSON.parse(jsonData);
				if(jd.nextStep > 0 && tableRow.length < 2){
					$(".menuWizard_contents_tbody > tr").eq(0).clone(true).insertAfter(
							$(".menuWizard_contents_tbody > tr").eq(0)
						);
				}

				// 表示行が1件しかない場合、作成対象ラジオボタンをONにしておく
				tableRow = $(".menuWizard_contents_tbody tr");
				if(jd.nextStep > 0 && tableRow.length == 2){
					$('input[type="radio"]').each(function(index, element){
						if(index === 1){
							$(element).prop("checked", true);
						}
					});
				}

				// ページ表示
				$("#menuWizardJsonData").val(JSON.stringify(response.jsonData));

				// ヘルプバルーン
				$('.menuWizardHelp').each(function(index, element){
					var mid = index+1;
					$(element).balloon({
						contents : response.helpList["message" + mid],
						css: {
							"color": "#FFFFFF",
							"backgroundColor": "#777777",
							"opacity": "0.85",
							"font-size": "12px",
							"font-weight": "bold",
							"padding": "20px"
						}
					});
				});

				// フィルター情報のバルーン
				if($(".menuWizardStep9_hover").length){
					$('.menuWizardStep9_hover').each(function(index, element){
						var filterid = Number($(element).text());
						var filterContentsJSONArray = response.filterDetails.filter(function(item, index){
							  if (item.id == filterid) return true;
						});
						var filterContentsJSON = filterContentsJSONArray[0];
						var filterContents = '<%=lang.__("Implementer")%>: ' + filterContentsJSON.user_name + ', '
							+ '<%=lang.__("Retrieval object layer")%>: ' + filterContentsJSON.layer_id + ':' + filterContentsJSON.layer_name + ', '
							+ '<%=lang.__("Search condition")%>: ' + filterContentsJSON.name;

						$(element).balloon({
							contents: filterContents,
							position: "right",
							css: {
								"color": "#FFFFFF",
								"backgroundColor": "#777777",
								"opacity": "0.85",
								"font-size": "12px",
								"font-weight": "bold",
								"padding": "20px"
							}
						});
					});
				}

				var jd = JSON.parse(jsonData);
				// 利用者ページからウイザードを使用した場合、STEP4以降は呼び出し元ページをリロードする
				if((document.referrer.indexOf(SaigaiTask.contextPath+"/admin/") <= 0)
						&& jd.nextStep > 3
						&& ope === "next"){
					window.opener.location.reload();
				}

				// システム管理者の場合は自治体と班または課の選択を行う
				if(jd.nextStep === -1){
					$('#menuWizardStepM1_select1').change(function() {
						loadGUList();
					});
					loadGUList();
				}
			}
			$("#menuWizardPanel").unmask();

		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
		}
	});
}

function validate(){
	var jsonDataStr = $("#menuWizardJsonData").val();
	var jsonData = JSON.parse(jsonDataStr);
	var errorMessage = "";
	var errorMessage1 = "";
	var errorMessage2 = "";
	switch (jsonData.nextStep){
	case -2:
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case -1:
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 0:
	    break;
	case 1:
		if(! $('input[name=menuWizardStep0_radio]:checked').val()){
			errorMessage1 = '<%=lang.__("Select editing target.")%>';
		}
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 2:
		if(! $('input[name=menuWizardStep1_radio]:checked').val()){
			errorMessage1 = '<%=lang.__("Select the 1st layer task.")%>';
		}
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 3:
		if(! $('input[name=menuWizardStep2_radio]:checked').val()){
			errorMessage1 = '<%=lang.__("Select the 2nd layer task.")%>';
		}
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 4:
		if(! $('input[name=menuWizardStep3_radio]:checked').val()){
			errorMessage1 = '<%=lang.__("Select the menu.")%>';
		}
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 5:
	    break;
	case 6:
		if(! $('input[name=menuWizardStep5_radio]:checked').val()){
			errorMessage1 = '<%=lang.__("Select the table.")%>';
		}
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 7:
		$('input[name="menuWizardStep6_checkbox1"]').each(function(index, element){
			if($(element).prop('checked')){
				$(element).parent().parent().find(".menuWizard_contents_text").each(function(index2, element2){
					if(index2 == 1){
//						if(! $(element2).text()){
//							errorMessage1 = '<%=lang.__("{0} is required.", lang.__("Default sort item"))%>';
//						}else if(! $.isNumeric($(element2).text())){
//							errorMessage1 = '<%=lang.__("The default sort item is not numeric.")%>';
//						}
						var inputDefaultsort = $(element2).text();
						console.log("#" + inputDefaultsort + "#");
						inputDefaultsort = inputDefaultsort.replace(/\s+/g, "");
						console.log("*" + inputDefaultsort + "*");
						if(! inputDefaultsort){
							errorMessage1 = '<%=lang.__("{0} is required.", lang.__("Default sort item"))%>';
						}else if(! inputDefaultsort){
							errorMessage1 = '<%=lang.__("The default sort item is not numeric.")%>';
						}
					}
				});

			}
		});
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;

	    break;
	case 8:
		break;
	case 9:
	    break;
	case 10:
		errorMessage2 = emptyCheck(jsonData.nextStep);
	    break;
	case 11:
	    break;
	}

	if(errorMessage1 && errorMessage2){
		errorMessage = errorMessage1 + "\n" + errorMessage2;
	}else if(errorMessage1){
		errorMessage = errorMessage1;
	}else if(errorMessage2){
		errorMessage = errorMessage2;
	}

	if(errorMessage){
		alert(errorMessage);
		return false;
	}else{
		var delMessage = deleteCheck();
		if(delMessage){
			if(window.confirm(delMessage)){
				return true;
			}else{
				return false;
			}
		}

		return true;
	}
}

function jsonDataSetting(ope){
	var jsonDataStr = $("#menuWizardJsonData").val();
	var jsonData = JSON.parse(jsonDataStr);


	if(ope === "next"){
		jsonData.isNext = true;
	}else if(ope === "back"){
		jsonData.isNext = false;
	}else{
		jsonData.isNext = true;
	}

	if(ope === "finish"){
		jsonData.isFinish = true;
	}else{
		jsonData.isFinish = false;
	}

	if(ope === "reload"){
		jsonData.isReload = true;
	}else{
		jsonData.isReload = false;
	}


	switch (jsonData.nextStep){
	case -2:
	    break;
	case -1:
		jsonData.stepM2Data = {};
		jsonData.stepM2Data.guname = $('input[name=menuWizardStepM2_text1]').val();
		jsonData.stepM2Data.gupass = $('input[name=menuWizardStepM2_text2]').val();
	    break;
	case 0:
		jsonData.stepM1Data = {};
		jsonData.stepM1Data.localgovinfoid = $('#menuWizardStepM1_select1').val();
		jsonData.stepM1Data.guid = $('#menuWizardStepM1_select2').val();
	    break;
	case 1:
		jsonData.step0Data = {};
		jsonData.step0Data.radio = $('input[name=menuWizardStep0_radio]:checked').val();
	    break;
	case 2:
		jsonData.step1Data = {};
		jsonData.step1Data.radio = $('input[name=menuWizardStep1_radio]:checked').val();
	    break;
	case 3:
		jsonData.step2Data = {};
		jsonData.step2Data.radio = $('input[name=menuWizardStep2_radio]:checked').val();
	    break;
	case 4:
		jsonData.step3Data = {};
		jsonData.step3Data.radio = $('input[name=menuWizardStep3_radio]:checked').val();
	    break;
	case 5:
		jsonData.step4Data = {};
		jsonData.step4Data.chexbox = {};
	    break;
	    break;
	case 6:
		jsonData.step5Data = {};
		jsonData.step5Data.radio = $('input[name=menuWizardStep5_radio]:checked').val();
	    break;
	    break;
	case 7:
	    break;
	case 8:
		break;
	case 9:
	    break;
	case 10:
	    break;
	case 11:
	    break;
	}

	// 画面に表示しているテーブルの内容をJSONオブジェクトに変換する
	if(jsonData.isNext){
		jsonData.editTableData = {};
		var tableHeadRow = $(".menuWizard_contents_thead tr");
		var columnNames = [];
		for( var rowIndex = 0, rowMax = tableHeadRow.length; rowIndex < rowMax; rowIndex++ ){
	 		var cells = tableHeadRow.eq(rowIndex).children();
	 		// 表示順変更列と追加・削除ボタン列は不要
	 		for( var colIndex = 0 , colMax = cells.length -3; colIndex < colMax; colIndex++ ){
	 			var cellChild = cells.eq(colIndex).children();
	 			for( var i = 0; i< cellChild.length; i++ ){
		 			if(cellChild.eq(i).hasClass('menuWizard_contents_columnName') &&
		 					cellChild.eq(i).hasClass('menuWizard_contents_hidden')	){
		 				columnNames[colIndex] = cellChild.eq(i).text();
		 			}
				}
	 		}
		}

		var tableData = [];
		var tableRow = $(".menuWizard_contents_tbody tr");
		for( var rowIndex = 0, rowMax = tableRow.length; rowIndex < rowMax; rowIndex++ ){
	 		var cells = tableRow.eq(rowIndex).children();
	 		// 表示順変更列と追加・削除ボタン列は不要
	 		for( var colIndex = 0 , colMax = cells.length -3; colIndex < colMax; colIndex++ ){
	 			if( typeof tableData[rowIndex] == "undefined" ){
	 				tableData[rowIndex] = {};
	 			}
	 			if(cells.eq(colIndex).hasClass('menuWizard_contents_text') ||
	 					cells.eq(colIndex).hasClass('menuWizard_contents_hidden')	){
		 			tableData[rowIndex][columnNames[colIndex]] = cells.eq(colIndex).text();
	 			}else if(cells.eq(colIndex).hasClass('menuWizard_contents_select')){
		 			tableData[rowIndex][columnNames[colIndex]] = cells.eq(colIndex).children().eq(0).val();
	 			}else{
		 			tableData[rowIndex][columnNames[colIndex]] = cells.eq(colIndex).children().eq(0).prop('checked');
	 			}
	 		}
		}
		jsonData.editTableData = tableData;
	}

	if(ope === "reload"){
		jsonData.nextStep = jsonData.nextStep -1;
	}

	if(document.getElementById("menuWizard_blankpage") != null){
		jsonData.blankPage = $("#menuWizard_blankpage").val();
	}else{
		jsonData.blankPage = "0";
	}

	$("#menuWizardJsonData").val(JSON.stringify(jsonData));
}

function clickCancel(){
	if(!cancelButtonEnable){
		return false;
	}
	cancelContents();
	return true;
}
function clickBack(){
	if(!backButtonEnable){
		return false;
	}
	backContents();
	return true;
}
function clickNext(){
	if(!nextButtonEnable){
		return false;
	}
	nextContents();
	return true;
}
function clickFinish(){
	if(!finishButtonEnable){
		return false;
	}
	finishContents();
	return true;
}
function clickReload(){
	if(!reloadButtonEnable){
		return false;
	}
	if(window.confirm('<%=lang.__("Discard modified info?")%>')){
		reloadContents();
	}
	return true;
}

function deleteCheck(){
	var returnMessage = "";
	var resultMessage = "";
	var tableHeadRow = $(".menuWizard_contents_thead tr");
	var columnNames = [];
	for( var rowIndex = 0, rowMax = tableHeadRow.length; rowIndex < rowMax; rowIndex++ ){
 		var cells = tableHeadRow.eq(rowIndex).children();
 		// 表示順変更列と追加・削除ボタン列は不要
 		for( var colIndex = 0 , colMax = cells.length -3; colIndex < colMax; colIndex++ ){
 			var cellChild = cells.eq(colIndex).children();
 			for( var i = 0; i< cellChild.length; i++ ){
	 			if(cellChild.eq(i).hasClass('menuWizard_contents_columnName') &&
	 					 (! cellChild.eq(i).hasClass('menuWizard_contents_hidden'))	){
	 				columnNames[colIndex] = cellChild.eq(i).text();
	 			}
			}
 		}
	}

	var tableRow = $(".menuWizard_contents_tbody tr");
	for( var rowIndex = 0, rowMax = tableRow.length; rowIndex < rowMax; rowIndex++ ){
 		var cells = tableRow.eq(rowIndex).children();

 		var idVal   = cells.eq(0).text();
 		var delFlag = cells.eq(1).text();
 		// ID=0は新規作成列なので無視する
 		if(delFlag === '1' && idVal !== '0'){

 			var lineText = '<%=lang.__("row")%>' + rowIndex + "  ["
	 		// 表示順変更列と追加・削除ボタン列は不要
	 		for( var colIndex = 0 , colMax = cells.length -3; colIndex < colMax; colIndex++ ){
	 			if(colIndex == 1){
	 				continue;
	 			}
	 			if(cells.eq(colIndex).hasClass('menuWizard_contents_text') ||
	 					cells.eq(colIndex).hasClass('menuWizard_contents_hidden')	){
	 				lineText +=  columnNames[colIndex] + ":" + cells.eq(colIndex).text() + ",";
	 			}
	 		}
 			lineText = lineText.substr( 0, lineText.length-1 ) ;
 			lineText += "]\n";
 			resultMessage += lineText
 		}
	}
	resultMessage = resultMessage.substr( 0, resultMessage.length-1 ) ;

	if(resultMessage){
		returnMessage = '<%=lang.__("Rows are requested to be deleted. If proceeding, delete process will execute and delete its child tables. Delete process cannot be canceled. Continue?")%>';
		returnMessage += "\n";
		returnMessage += resultMessage;
	}

	return returnMessage;

}

function emptyCheck(nextStep){
	var tableHeadRow = $(".menuWizard_contents_thead tr");
	var columnNames = [];
	for( var rowIndex = 0, rowMax = tableHeadRow.length; rowIndex < rowMax; rowIndex++ ){
 		var cells = tableHeadRow.eq(rowIndex).children();
 		// 表示順変更列と追加・削除ボタン列は不要
 		for( var colIndex = 0 , colMax = cells.length -3; colIndex < colMax; colIndex++ ){
 			var cellChild = cells.eq(colIndex).children();
 			for( var i = 0; i< cellChild.length; i++ ){
	 			if(cellChild.eq(i).hasClass('menuWizard_contents_columnName') &&
	 					cellChild.eq(i).hasClass('menuWizard_contents_hidden')	){
	 				columnNames[colIndex] = columnNames[colIndex] = cellChild.eq(i).text();
	 			}
			}
 		}
	}

	var tableRow = $(".menuWizard_contents_tbody tr");
	var rowText = "";
	for( var rowIndex = 1, rowMax = tableRow.length; rowIndex < rowMax; rowIndex++ ){
 		var cells = tableRow.eq(rowIndex).children();
 		var delFlag = cells.eq(1).text();
 		if(delFlag === '0'){
	 		// 表示順変更列と追加・削除ボタン列は不要
	 		var colText = "";
	 		for( var colIndex = 0 , colMax = cells.length -3; colIndex < colMax; colIndex++ ){
	 			if(colIndex == 1){
	 				continue;
	 			}
	 			if(cells.eq(colIndex).hasClass('menuWizard_contents_text') &&
	 					(! cells.eq(colIndex).hasClass('menuWizard_contents_hidden'))	){
	 				var text = cells.eq(colIndex).text();
	 				if(!text){
	 					if(( (nextStep > 0 && nextStep < 5)) && columnNames[colIndex]  === 'name' ){
	 						colText = '<%= lang.__("Name is required.")%>';
	 					}else if(nextStep === 10 && columnNames[colIndex]  === 'name'){
	 						if(cells.eq(colIndex + 1).children('input').prop("checked") === true){
		 						colText = '<%= lang.__("Name is required.")%>';
	 						}
	 					}
	 				}
	 			}
	 		}
	 		if(colText){
	 			rowText += '<%=lang.__("row")%>' + rowIndex + " : " + colText + "\n";
	 		}
 		}
	}
	if(rowText){
		rowText = rowText.substr( 0, rowText.length-1 ) ;
	}

	return rowText;
}

</script>



<div id="content_head" class="ui-layout-north">
	<h1>
		<span><%=lang.__("Menu wizard")%></span>
	</h1>
</div>

<div id="content_main" class="ui-layout-center" style="text-align: left; padding: 0px;">
<font color="red"><ul><form:errors path="*" element="li"/></ul></font>


<div id="menuWizardPanel">
	<h2 id="menuWizardStepTitle"></h2>
	<p id="menuWizardStepDescription"></p>
	<div id="menuWizardStepError">
	</div>
	<div id="menuWizardStepContent">
	</div>
	<c:if test="${isAdmin==false}">
		<form:hidden id="menuWizardJsonData" path="menuWizardForm.jsonData" value="{\"nextStep\":-2}"/>
	</c:if>
	<c:if test="${isAdmin==true}">
		<form:hidden id="menuWizardJsonData" path="menuWizardForm.jsonData" value="{\"nextStep\":-1}"/>
	</c:if>
	<div id="menuWizardButtonDiv">
	<a href="#" id="menuWizardCancelButton" class="btn blue" onclick="return clickCancel();"><%=lang.__("Cancel<!--2-->")%></a>
	<a href="#" id="menuWizardBackButton" class="btn blue" onclick="return clickBack();"><%=lang.__("Return")%></a>
	<a href="#" id="menuWizardNextButton" class="btn blue" onclick="return clickNext();"><%=lang.__("Next")%></a>
	<a href="#" id="menuWizardFinishButton" class="btn blue" onclick="return clickFinish();"><%=lang.__("Completed<!--2-->")%></a>
	<a href="#" id="menuWizardReloadButton" class="btn blue" onclick="return clickReload();"><%=lang.__("Discard editing.")%></a>
	</div>
</div>

<div id="dialog_add_menutasktype">
	<div id="dialog_add_menutasktype_message">
	</div>
	<div>
		<table class="menuWizard_contents_table">
				<tr>
					<th>
						<%=lang.__("Name(*)")%>
					</th>
					<td>
						<input type="text" id="dialog_add_menutasktype_name" name="dialog_add_menutasktype_name" size="50" maxlength="100">
					</td>
				</tr>
				<tr>
					<th>
						<%=lang.__("Notes")%>
					</th>
					<td>
						<input type="text" id="dialog_add_menutasktype_note" name="dialog_add_menutasktype_note" size="50" maxlength="100">
					</td>
				</tr>
		</table>
	</div>
</div>

<div id="dialog_upload_exceltemplate">
	<div id="dialog_upload_exceltemplate_message">
	</div>
	<div>
		<table class="menuWizard_contents_table">
				<tr>
					<th>
						<%=lang.__("Excel template file")%>
					</th>
					<td>
						<form:form styleId ="excellisttemplatetempform" modelAttribute="menuWizardForm">
							<input type="file" name="excellisttemplatetempfile" id="excellisttemplatetempfile"/>
						</form:form>
					</td>
				</tr>
		</table>
	</div>
</div>
</div>



