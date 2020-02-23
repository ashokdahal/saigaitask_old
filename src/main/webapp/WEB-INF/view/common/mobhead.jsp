<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>

<%@include file="lang_resource.jsp" %>
<title>${siteName}</title>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" href="${f:url('/css/jQuery-Mobile-Bootstrap/themes/Bootstrap.css')}">
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery.mobile.structure-1.4.3.min.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jQuery-Mobile-Bootstrap/themes/jquery.mobile.icons.min.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jquery.tablesorter/stylem.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/css/jqm-datebox-1.4.5.min.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/extjs/resources/css/ext-all.css')}"/>
<%@ page import="jp.ecom_plat.map.util.FormUtils" %>
<script type="text/javascript">
window.SaigaiTask = {
	contextPath: "<%=request.getContextPath()%>"
};

//for IE
{
	var console = {
		log : function() {},
		debug : function() {},
		info : function() {},
		warn : function() {},
		error : function() {},
		assert : function() {},
		trace : function() {}
	};

	if (typeof window.console == "undefined") {
		window.console = console;
	}
	else {
		for ( var idx in console) {
			if (window.console[idx] == "undefined") {
				window.console[idx] = console[idx];
			}
		}
	}
}
</script>
<script type="text/javascript" src="${f:url('/js/jquery-2.1.1.min.js')}"></script>
<script type="text/javascript">
$(document).bind("mobileinit", function(){
  // jQuery Mobile がリンク移動やform送信を自動でAjaxリクエストに変えてしまうため無効化。
  // /mob/ でログインして、 process -> task と移動後、task からブラウザバックしたときに process に戻すため。
  /*$.extend( $.mobile , {
    ajaxEnabled: false
  });*/
});
</script>
<script type="text/javascript" src="${f:url('/js/jquery.mobile-1.4.3.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jqm-datebox-1.4.5.core.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jqm-datebox-1.4.5.mode.calbox.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jqm-datebox-1.4.5.mode.datebox.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jqm-datebox-1.4.5.mode.flipbox.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery.upload-1.0.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/saigaitask.edit.js')}"></script>
<script type="text/javascript" src="${f:url('/js/SaigaiTaskJS/lib/falutil.js')}"></script>
<script type="text/javascript" src="${f:url('/js/extjs/ext-all.js')}"></script>
<script type="text/javascript">
$(document).ready(function(){
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	});
	//SaigaiTask.ajaxcsrfToken = token;
});
function deleteData(id, value, token) {
	if (!confirm("<%=lang.__("Delete?<!--2-->")%>")) return;

	var url = "${f:url('/page/list/update')}";
	// 送信データを生成
	var data = {
		saveDatas: []
	};
	data.saveDatas.push({
		id: id,
		value: value
	});

	// 変更登録
	$.ajax({
		dataType: "json",
		type: "POST",
		cache: false,
		url: url,
		//headers: {"X-CSRF-Token":token},
		data: SaigaiTask.Edit.toSAStrutsParam(data),
		success: function(msg) {
			$('.ui-dialog').dialog('close');
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("<%=lang.__("Error : Failed to register")%>");
		}
	});
}

function checkDetailData(dataType, value) {
	if(dataType.indexOf('Number') >= 0) {
		if (value != "" && (!jQuery.isNumeric(value) || value.indexOf(".") > 0)) {
			alert("<%=lang.__("Input value of a numeric is invalid.")%>");
			return false;
		}
	}
	else if(dataType.indexOf('Float') >= 0) {
		if (value != "" && !jQuery.isNumeric(value)) {
			alert("<%=lang.__("Input value of a numeric is invalid.")%>");
			return false;
		}
	}
	else if (dataType.indexOf('DateTime') >= 0) {
		if (value != "" && !value.match(/^([1-2]\d{3}\/([0]?[1-9]|1[0-2])\/([0-2]?[0-9]|3[0-1])) (20|21|22|23|[0-1]?\d{1}):([0-5]?\d{1})$/)) {
			alert("<%=lang.__("Input value of date and time is invalid.")%>");
			return false;
		}
	}
	else if (dataType.indexOf('DateTime') == -1 && dataType.indexOf('Date') >= 0) {
		if (value != "" && !value.match(/^\d{4}\/\d{2}\/\d{2}$/)) {
			alert("<%=lang.__("Input value of date is invalid.")%>");
			return false;
		}
	}
	return true;
}

function checkRequired(dataType, value) {
	if (dataType.indexOf('Required') >= 0) {
		if (value==null || value.length<1 || value=="null") {
			alert("<%=lang._E("There is no value in the required fields!\n Please enter a value.")%>");
			return false;
		}
	}
	return true;
}

function saveData(menuid, form, token) {
	if (!confirm("<%=lang.__("Are you sure to save?<!--2-->")%>")) return;

	var url = "${f:url('/page/list/update')}";
	// 送信データを生成
	var data = {
		saveDatas: []
	};

	var array = [];
	for (var i = 0; i < form.length; i++) {
        var elem = form.elements[i];
    	//値チェック
        if (!checkDetailData(elem.className, elem.value)) return false;
    	//必須NULLチェック
    	if (!checkRequired(elem.className, elem.value)) return false;
        if (typeof elem.name !== "undefined") {
	        var ids = elem.name.split(":");
	        if (ids.length == 4) {
	    		data.saveDatas.push({
	    			id: elem.name,
	    			value: elem.value
	    		});
	        }
        }
    }

	// 変更登録
	$.ajax({
		dataType: "json",
		type: "POST",
		cache: false,
		url: url,
		data: SaigaiTask.Edit.toSAStrutsParam(data)+"&menuid="+menuid,
		success: function(result) {
			if(!result.success) {
				alert("ERROR: "+(!!result.msg?result.msg:""));
				return;
			}

			var features = result.features;

			//$('.ui-dialog').dialog('close');
			if (file != null)
				saveFiles(menuid, features[0].id, token);
			else
				$('.ui-dialog').dialog('close');
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("<%=lang.__("Error : Failed to register")%>");
		}
	});
}
function getLocation() {
	navigator.geolocation.getCurrentPosition(
		function(position){
			$('#location').html('');
			var loc = "POINT("+position.coords.longitude+" "+position.coords.latitude+")";
			$('#location').html(loc);
			$('#thegeom').val(loc);
		}
	);
}

function loadPhoto(layerid, fid) {
	var url = "${f:url('/mob/mlist/filelist')}/"+layerid+"/"+fid;
	$.ajax({
		dataType: "json",
		type: "POST",
		cache: false,
		url: url,
		success: function(msg) {
			var data = msg.data;
			$('#photoarea').empty();
			for(var i in data[0]) {
				var files = data[0][i];
				(function(files){
					// 画像URLのサーバ名を動的に取得する
					var server = ecommapURL+'/..';
					// 画像URL
					var fileURL = files[0];
					if(fileURL.indexOf('http')==-1) fileURL = server + fileURL;

					// サムネイル画像
					var url = fileURL;
					var ext = FalUtil.getFileExt(url);
					var thumbnail = url;
					// 画像以外のファイルの場合はアイコン表示
					if (! ext.match(/jpg|gif|png|jpeg|file/)) {
						thumbnail = ecommapURL+"/map/fileicons/"+ext+".png";
					}

					var img = $('<img src="'+thumbnail+'" alt="'+files[1]+'" style="max-width:150px;max-height:150px; cursor:pointer;"/>');
					img.click(function(){
						// 画像の場合はプレビュー表示
						if (ext.match(/jpg|gif|png|jpeg|file/)) {
							FalUtil.showImageWindow(url);
						} else {
							//img.src = "/map/map/fileicons/"+ext+".png";
							FalUtil.downloadFile({
								url: url
							});
						}
					});

					$("#photoarea").prepend(img);
					$("#photoarea").prepend('&nbsp;');
				})(files);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//ファイルなし
		}
	});
}

var file;
function selectPhoto(item) {
	file = item;
}

function saveFiles(menuid, fid, token) {
	var url = "${f:url('/page/list/upphoto/')}"+menuid+"/"+fid;

	//TODO:現状１ファイルにしか対応してない。
	var fd = new FormData();
	fd.append('formFile', file.files[0]);
	$.ajax({
		type: 'post',
		processData: false,
		contentType: false,
		url: url,
		data: fd,
		dataType: 'text',
		headers: {"X-CSRF-Token":token},
		success: function(res){
			file = null;
			$('.ui-dialog').dialog('close');
			
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
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//TODO:エラー処理
			alert("upload error : "+textStatus);
		}
	});
}

function openMap(geom) {
	var geomstr = geom.toString();
	var latlon = geomstr.slice(geomstr.indexOf("(") + 1, geomstr
			.indexOf(")"));
	var lat = latlon.slice(0, latlon.indexOf(" "));
	var lon = latlon.slice(latlon.indexOf(" "));
	window.open("https://www.google.co.jp/maps/?q=" + lon + "," + lat);
}
</script>
