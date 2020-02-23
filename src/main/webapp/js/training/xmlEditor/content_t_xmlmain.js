/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

// api type
// new 1
//
// save 4
var g_xml_main_data_array;

function getXmlMainData()
{
	return g_xml_main_data_array;
}

function ObjToJSON(o) {
    if (o == null)
        return "null";

    switch (o.constructor) {
        case String:
        	/*
        	// Jsonの文字列はただの文字列として処理する
            var s = o; // .encodeURI();
            if (s.indexOf("}") < 0) s = '"' + s.replace(/(["\\])/g, '\\$1') + '"';
            s = s.replace(/\n/g, "\\n");
            s = s.replace(/\r/g, "\\r");
            alert(s);
            */
        	var s = o; // .encodeURI();
        	s = '"' + s.replace(/(["\\])/g, '\\$1') + '"';
            s = s.replace(/\n/g, "\\n");
            s = s.replace(/\r/g, "\\r");
            return s;
        case Array:
            var v = [];
            for (var i = 0; i < o.length; i++)
                v.push(ObjToJSON(o[i]));
            if (v.length <= 0) return "\"\"";
            //alert(v);
            return "[" + v.join(",") + "]";
        case Number:
            return isFinite(o) ? o.toString() : ObjToJSON(null);
        case Boolean:
            return o.toString();
        case Date:
            var d = new Object();
            d.__type = "System.DateTime";
            d.Year = o.getUTCFullYear();
            d.Month = o.getUTCMonth() + 1;
            d.Day = o.getUTCDate();
            d.Hour = o.getUTCHours();
            d.Minute = o.getUTCMinutes();
            d.Second = o.getUTCSeconds();
            d.Millisecond = o.getUTCMilliseconds();
            d.TimezoneOffset = o.getTimezoneOffset();
            return ObjToJSON(d);
        default:
            if (o["toJSON"] != null && typeof o["toJSON"] == "function")
                return o.toJSON();
            if (typeof o == "object") {
                var v = [];
                for (attr in o) {
                    if (typeof o[attr] != "function")
                        v.push('"' + attr + '": ' + ObjToJSON(o[attr]));
                }
                //alert(v);
                if (v.length > 0)
                    return "{" + v.join(",") + "}";
                else
                    return "{}";
            }
            //alert(o.toString());
            //   alert(o.toString());
            return o.toString();
    }
};


function doXmlLoadFileTypeFromServer()
{
	var urldata = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxxml?"
		+"apitype="+101
		;

	var content = $("#content_all");
	content.mask("Loading...");

	$.ajax({
		url: urldata,
		type: 'POST',
		data: {
			apitype		    :4,
		},
		success: function(data, textStatus, xhr) {
			content.unmask();
			//alert(2);
			var jsonS = data;
			var strjson = JSON.stringify(jsonS);
			//alert(strjson);
			set_label_value_by_id_com("alarm_process_res_error_id", "");
			if(jsonS.errcode == -1) {
				set_label_value_by_id_com("alarm_process_res_error_id",
						jsonS.errmsg);
			}else {
				var $select = $("#file_type_combobox").empty();//flie_type_combobox
				for(var i = 0; i < jsonS.combo.length; i++) {
					var v = jsonS.combo[i];
					if(i == 0)fileTypeOnChanage(v.key);
					$select.append(new Option(v.value, v.key));
				}


			}
			//alert(strjson);
			return data;
		},
		error: function(xhr, textStatus, errorThrown) {
			content.unmask();
			doAjaxErrorCheck(textStatus, xhr.responseText);
		},
		contentType: "application/x-www-form-urlencoded;charset=utf-8",
		beforeSend: function(xhr) {
			xhr.setRequestHeader("Accept-Charset","utf-8");
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
		},
	});
}



function doXmlLoadFileListFromServer(edittype, filetype)
{
	// UT Test DEBUG
	// utT01Test01InitData();
	var vOldSel = $("#hidden_cur_save_file_name").val();
	$("#xmlfile_list_combobox").empty();

	var urldata = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxxml?"
		+"apitype="+102
		+"&filetype=" + filetype
		;

	var content = $("#content_all");
	content.mask("Loading...");

	$.ajax({
		url: urldata,
		type: 'POST',
		data: {
			apitype		    :4,
		},
		success: function(data, textStatus, xhr) {
			content.unmask();
			//alert(2);
			var json = data;
			var strjson = JSON.stringify(json);
			//alert(json.errcode);
			set_label_value_by_id_com("alarm_process_res_error_id", "");
			if(json.errcode == -1) {
				set_label_value_by_id_com("alarm_process_res_error_id",
						json.errmsg);
			} else {
				var combobox2 = document.getElementById('xmlfile_list_combobox');



				var $select = $("#xmlfile_list_combobox").empty();


				$select.append(new Option("", ""));
				for(var i = 0; i < json.combo.length; i++) {
					var v = json.combo[i];
					$select.append(new Option(v.file, v.file));
				}
				if(edittype == 2) {
					//alert(vOldSel);
					// edit
					var tt = $select.attr("disabled");
					$select.attr("disabled", false);
					$("#xmlfile_list_combobox").val(vOldSel);
					if(tt == "disabled")$select.attr("disabled", true);
				}
			}
			//alert(strjson);



			return data;
		},
		error: function(xhr, textStatus, errorThrown) {
			content.unmask();
			doAjaxErrorCheck(textStatus, xhr.responseText);
		},
		contentType: "application/x-www-form-urlencoded;charset=utf-8",
		beforeSend: function(xhr) {
			xhr.setRequestHeader("Accept-Charset","utf-8");
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
		},
	});
}



function doXmlLoadCurFileNameFromServerOnlyNewFile(edittype, filetype)
{
	// UT Test DEBUG
	//utT01Test01InitData();

	var urldata = SaigaiTask.contextPath + "/admin/training/xmlEditor/ajaxxml?"
		+"apitype="+103
		+"&filetype=" + filetype

		;

	var content = $("#content_all");
	content.mask("Loading...");

	$.ajax({
		url: urldata,
		type: 'POST',
		data: {
			apitype		    :4,
		},
		success: function(data, textStatus, xhr) {
			content.unmask();
			var json = data;
			var strjson = JSON.stringify(json);
			//alert(json.errcode);
			set_label_value_by_id_com("alarm_process_res_error_id", "");
			if(json.errcode == -1) {
				set_label_value_by_id_com("alarm_process_res_error_id",
						json.errmsg);
			} else {
				;
				$("#hidden_cur_save_file_name").val(json.filename);
			}
			//alert(strjson);


			return data;
		},
		error: function(xhr, textStatus, errorThrown) {
			content.unmask();
			doAjaxErrorCheck(textStatus, xhr.responseText);
		},
		contentType: "application/x-www-form-urlencoded;charset=utf-8",
		beforeSend: function(xhr) {
			xhr.setRequestHeader("Accept-Charset","utf-8");
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
		},
	});
}


