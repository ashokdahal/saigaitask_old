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

function dialog_file_input_info_disp_init(type) {
	if(type == 2) {
		if ($("#hidden_edit_type_save_data").val() == 1) {

			if($("#hidden_cur_save_file_name").val().endWith(".xml")) {
				//edit
				$("#filealertbutton_save").hide();
				$("#filealertbutton_write").show();
				$("#filealertbutton_saveas").show();

				$("#ui-id-1").val("<%=lang.__("Info<!--5-->")%>");

				$("#filename_input_table").hide();
				$("#file_display_msg").show();

			} else {
				//new
				$("#filealertbutton_save").show();
				$("#filealertbutton_write").hide();
				$("#filealertbutton_saveas").hide();
				//ファイル名を入力
				$("#ui-id-1").val("<%=lang.__("Enter file name.<!--2-->")%>");

				$("#filename_input_table").show();
				$("#file_display_msg").hide();

			}

		}
		if ($("#hidden_edit_type_save_data").val() == 2) {
			//edit
			$("#filealertbutton_save").hide();
			$("#filealertbutton_write").show();
			$("#filealertbutton_saveas").show();

			$("#filename_input_table").hide();
			$("#file_display_msg").show();

		}
		if ($("#hidden_edit_type_save_data").val() == 3) {
			//del
		}

	}
	else if(type == 1 || type == 3)
	{
		$("#filealertbutton_save").show();
		$("#filealertbutton_write").hide();
		$("#filealertbutton_saveas").hide();
		//ファイル名を入力
		$("#ui-id-1").val("<%=lang.__("Enter file name.<!--2-->")%>");

		$("#filename_input_table").show();
		$("#file_display_msg").hide();
	}
	g_dialog_type_flag = 0;
}


function confirmFileName() {
	//alert($("#hidden_callalertdialogtype").val());
	if($("#filename_input").val() == "") {
		alert("<%=lang.__("Enter file name.")%>");
		return;
	}


	if(!($("#filename_input").val()+"").endWith(".xml")) {
		alert("<%=lang.__("File name extension must be .xml.")%>");
		return;
	}

	if(getBytesSaigai($("#filename_input").val()) > 255 ) {
		//ファイル名が長すぎます。
		alert("<%=lang.__("File name is too long.")%>");
		return;
	}

	if ($("#hidden_edit_type_save_data").val() == 1) {
		//new
	}
	if ($("#hidden_edit_type_save_data").val() == 1) {
		//edit
	}
	//$("#hidden_cur_save_file_name").val()
	var jsondata = getT01ConfirmFileNameInfo_JsonDataUrl(
			$("#filename_input").val(),
			$("#hidden_edit_type_save_data").val(), 0);

	// UT Test DEBUG
	var urldata = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxxml?"
			+ "apitype=" + 104
			+ "&filetype=" + $("#hidden_file_type_save_data").val()
			+ "&jsondata=" + jsondata
			;

	var content = $("#content_all");
	content.mask("Loading...");


	$.ajax({
		url : urldata,
		type : 'POST',
		data : {
			apitype : 4,
		},
		success : function(data, textStatus, xhr) {
			content.unmask();
			//alert(2);
			var json = data;
			var strjson = JSON.stringify(json);
			//alert(strjson);
			set_label_value_by_id_com("alarm_process_res_error_id", "");
			if (json.errcode == -1) {
				set_label_value_by_id_com("alarm_process_res_error_id",
						json.errmsg);
			} else {
				if (json.filenameok == 1) {
					//save
					//var r = confirm("ファイル名がすでにあります。うわがきしますか？");
					$("#dialog_tfilelist_xml").dialog('close');
					doSaveXmlFile();
				} else {
					var r = confirm("<%=lang.__("File name you entered already exists. Overwrite the content?")%>");
					if (r == true) {
						// save
						$("#dialog_tfilelist_xml").dialog('close');
						doSaveXmlFile();
					} else {
						// no save
						$("#dialog_tfilelist_xml").dialog('close');
					}
				}
				//;
				//$("#hidden_cur_save_file_name").val(json.filename);
			}
			//alert(strjson);

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

}

function confirmWrite()
{
	$("#dialog_tfilelist_xml").dialog('close');
	doSaveEditXmlFile();
}
function confirmSaveAs()
{
	$("#dialog_tfilelist_xml").dialog('close');
	showFileListDialog(3);
}
</script>



<table id="filename_input_table" border="1" class="form">
	<tr>
	  <th><%=lang.__("File Name")%></th>
		<td>
			<div id="head2_text">
			<!--<xsl:value-of select="jmx:Report/jmx:Control/jmx:Title"/>-->
				<input type="text" id="filename_input" style="width:600px;" value=""/>
			</div>
		</td>
	</tr>

</table>

<div id="file_display_msg"><br/><label ><%=lang.__("Overwrite or save another name?")%></label><br/></div>

<div>
<button id="filealertbutton_save" class="btn blue" name="alarm_process_uid" onclick="confirmFileName();"><%=lang.__("Save")%></button>
<button id="filealertbutton_write" class="btn blue" name="alarm_process_uid" onclick="confirmWrite();"><%=lang.__("Overwrite")%></button>
<button id="filealertbutton_saveas" class="btn blue" name="alarm_process_uid" onclick="confirmSaveAs();"><%=lang.__("Save another name.")%></button>
</div>
