<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../../../common/lang_resource.jsp" %>

<div style="overflow: auto;">
<style>
	.resizable-textarea textarea {
		display:block;
		margin-bottom:0pt;
		width:100%;
		height:28%;
	}
</style>
<script>



//$(window).load(function() {
//});

var g_dialog_type_flag = 0;

//$(document).ready(function(){
  //$(".btn1").click(function(){
	//$("p").slideToggle();
  //});
  //alert(g_frame_param_type);
  //if($("#hidden_callalertdialogtype").val());


//});

function dialog_select_trainingmeteo_data(xmlFileName, fileType) {

	var url = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxGetTrainingmeteoData";
	var meteotypename = $("#file_type_combobox option:selected").text();
	var meteotypeid = $("#file_type_combobox").val();
	var localgovinfoid = $("#loginlocalgov-select").val();

	if(!localgovinfoid){
		localgovinfoid = "";
	}

	$.ajax({
		url : url,
		type : 'POST',
		data : {
			localgovinfoid : localgovinfoid,
			xmlFileName : xmlFileName,
			fileType : fileType,
			meteotypeid : meteotypeid,
			meteotypename: meteotypename
		},
		success : function(data, textStatus, xhr) {
			console.log(data);
			var json = data;
			$("#dialog_selecttrainingmeteodata_viewarea").append(json.innerHtml);
			if(json.retcode !== '0'){
				$("#dialog_selecttrainingmeteodata_saveButton").prop('disabled',true);
				$("#dialog_selecttrainingmeteodata_saveButton").hide();
			}
			return ;
		},
		error: function(xhr, textStatus, errorThrown) {
			doAjaxErrorCheck(textStatus, xhr.responseText);

		},
		contentType : "application/x-www-form-urlencoded;charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept-Charset", "utf-8");
			xhr.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=utf-8");
		},
	});
}

function confirmUpdateTrainingmeteoData() {
	var checkValue = $("input[name='select_trainingmeteoData_radio']:checked").val();
	if(! checkValue){
		alert("<%=lang.__("Select training plan external data.")%>")
		return false;
	}

	var content = $("#content_all");
	content.mask("Loading...");

	var url = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxUpdateTtrainingmeteoData";
	var trainingplanDataId = checkValue;
	var localgovinfoid = $("#loginlocalgov-select").val();
	if(!localgovinfoid){
		localgovinfoid = "";
	}
	var localgovinfoid = $("#loginlocalgov-select").val();
	var xmlFilePath = $("#select_trainingmeteoData_xmlFilePath_hidden").val();

	$.ajax({
		url : url,
		type : 'POST',
		data : {
			localgovinfoid : localgovinfoid,
			trainingplanDataId : trainingplanDataId,
			xmlFilePath : xmlFilePath
		},
		success : function(data, textStatus, xhr) {
			content.unmask();
			var json = data;
			alert(json.retmessage);
			return data;
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
	return true;
}

</script>


<div id="dialog_selecttrainingmeteodata_viewarea">
</div>
