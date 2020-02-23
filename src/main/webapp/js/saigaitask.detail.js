
SaigaiTask.Detail = {

	file: null,

	upFiles: [],

	checkDetailData: function(dataType, value) {
		if(dataType.indexOf('Number') == 0) {
			if (value != "" && (!jQuery.isNumeric(value) || value.indexOf(".") > 0)) {
				alert(lang.__("Input value of a numeric is invalid."));
				return false;
			}
		}
		else if(dataType.indexOf('Float') == 0) {
			if (value != "" && !jQuery.isNumeric(value)) {
				alert(lang.__("Input value of a numeric is invalid."));
				return false;
			}
		}
		else if (dataType.indexOf('DateTime') == 0) {
			if (value != "" && !value.match(/^([1-2]\d{3}\/([0]?[1-9]|1[0-2])\/([0-2]?[0-9]|3[0-1])) (20|21|22|23|[0-1]?\d{1}):([0-5]?\d{1})$/)) {
				alert(lang.__("Input value of date and time is invalid."));
				return false;
			}
		}
		else if (dataType.indexOf('Date') == 0) {
			if (value != "" && !value.match(/^\d{4}\/\d{2}\/\d{2}$/)) {
				alert(lang.__("Input value of date is invalid."));
				return false;
			}
		}
		return true;
	},

	checkRequired: function(dataType, value) {
		if (dataType.indexOf('Required') >= 0) {
			if (value==null || value.length<1 || value=="null") {
				alert(lang.__("There is no value in the required fields!\n Please enter a value."));
				return false;
			}
		}
		return true;
	},

	saveDetailData: function(options) {
		var me = this;
		if(!!SaigaiTask.PageURL.getTime()) {
			alert(lang.__("Can't update during time set."));
			return;
		}

		var menuid = options.menuid;
		var form = options.form;
		//var token = options.token;
		var dialog = options.dialog;
		var callback = options.callback;

		var url = SaigaiTask.contextPath+"/page/list/update";
		// 送信データを生成
		var data = {
			saveDatas: []
		};

		var array = [];
		for (var i = 0; i < form.length; i++) {
			var elem = form.elements[i];
			if (typeof elem.name !== "undefined") {
				//値チェック
				var classes = elem.className.split(" ");
				for (var j = 0; j < classes.length; j++) {
					if (!me.checkDetailData(classes[j], elem.value)) {
						elem.focus();
						return false;
					}
	        	}
	        	//必須NULLチェック
	        	if (!me.checkRequired(elem.className, elem.value)) {
	        		elem.focus();
	        		return false;
	        	}
		        var ids = elem.name.split(":");
		        if (ids.length == 4) {
		        	if (elem.type == 'checkbox') {
		        		if (!elem.checked) elem.value = '';
		        	}
		        	else if (elem.type == 'radio') {
		        		if (!elem.checked) continue;
		        	}
		        	else if (elem.type == 'file') {
		        		continue;
		        	}
		    		data.saveDatas.push({
		    			id: elem.name,
		    			value: elem.value
		    		});
		        }
	        }
	    }

		dialog.mask(lang.__("saving..."));

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

				if (me.file != null || me.upFiles.length > 0) {
					if (me.file.files != undefined)
						me.saveFiles(menuid, features[0].id, dialog);
					if (me.upFiles != null && me.upFiles.length > 0)
						me.uploadFiles(menuid, features[0].id, dialog);
				}
				else {
					if (callback == undefined)
						dialog.dialog("close");
					$('#editTable').clearSaveData();
					$('#editTable').clearSaveFiles();
					reloadlist();
					if (callback != undefined)
						callback();
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert(lang.__("error : not saved"));
			},
			complete: function() {
				me.file = null;
				dialog.unmask();
			}
		});
	},

	storefile: function(tagid, file) {
		this.upFiles.push([tagid, file]);
		return;
	},

	deleteData: function(id, value) {
		if (!confirm(lang.__("Delete?"))) return;

		var url = SaigaiTask.contextPath+"/page/list/update";
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
			data: SaigaiTask.Edit.toSAStrutsParam(data),
			success: function(msg) {
				$('#detail-dialog').dialog('close');
				reloadlist();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(lang.__("Error : Failed to register"));
			}
		});
	},

	close: function() {
		$('#detail-dialog').dialog("close");
	},

	loadPhoto: function(layerid, fid, editable) {
		var url = SaigaiTask.contextPath+"/page/filelist/"+layerid+"/"+fid+"?";

		// タイムスライダー対応
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			url+="&time="+iso8601Time;
		}

		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: url,
			success: function(msg) {
				var data = msg.data;
				if($('[id="photoarea:'+fid+'"').length > 0){
					$('[id="photoarea:'+fid+'"').empty();
				}
				else{
					$('#photoarea').empty();
				}

				for(var i in data[0]) {
					var url = data[0][i];
					var lightbox = "data-lightbox=\"photo\"";
					if (url[3]==false)
						lightbox = "";
					if($('[id="photoarea:'+fid+'"').length > 0){
						$('[id="photoarea:'+fid+'"').append('<div style="float:left; margin: 5px; width:120px; height:150px" id=photo:'+fid+':'+i+'><a href="'+url[0]+'" '+lightbox+'><img src="'+url[2]+'" alt="'+url[1]+'" data-title="'+url[1]+'" style="max-width:100px;max-height:100px" /></a>')
					}else{
						$('#photoarea').append('<div style="float:left; margin: 5px; width:120px; height:150px" id=photo'+i+'><a href="'+url[0]+'" '+lightbox+'><img src="'+url[2]+'" alt="'+url[1]+'" data-title="'+url[1]+'" style="max-width:100px;max-height:100px" /></a>')
						if(editable){
							$("#photo"+i).append('<br><input type="button" value="' + lang.__('Delete') + '" onClick="photodelete(this)" id=delete'+i+' /></div>');
						}
					}
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//ファイルなし
			}
		});
	},

	selectPhoto: function(item) {
		this.file = item;
		var reader = new FileReader();
		reader.onload = function (e) {
            $('#preview').attr('src', e.target.result);
        }
		reader.readAsDataURL(item.files[0]);
	},

	saveFiles: function(menuid, fid, dialog) {
		var me = this;

		var url = SaigaiTask.contextPath+"/page/list/upphoto/"+menuid+"/"+fid;

		var fd = new FormData();
		fd.append('formFile', me.file.files[0]);
		$.ajax({
			type: 'post',
			processData: false,
			contentType: false,
			url: url,
			data: fd,
			dataType: 'html',
			success: function(data){
				dialog.dialog("close");
				$('#editTable').clearSaveData();
				$('#editTable').clearSaveFiles();
				reloadlist();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert("upload error : "+textStatus);
			}
		});
	},

	uploadFiles: function(menuid, fid, dialog) {
		var url = "./list/upload/";

		var $fdata, id;
		for(var idx in this.upFiles) {
			var file = this.upFiles[idx];
			$fdata = file[1];
			id = file[0];
		}
		//TODO:現状１ファイルにしか対応してない。
		//alert(id);
		var fd = new FormData();
		fd.append('formFile', $fdata.prop("files")[0]);
		fd.append('id', id);
		fd.append('value', fid);
		$.ajax({
			type: 'post',
			processData: false,
			contentType: false,
			url: url,
			data: fd,
			dataType: 'html',
			success: function(data){
				dialog.dialog("close");
				$('#editTable').clearSaveData();
				$('#editTable').clearSaveFiles();
				document.location.reload();
				var cs = currentSort;
				if (cs=='') cs = '-1';
				reloadlist();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert("upload error : "+textStatus);
			}
		});
	},

	createAttrListDialog: function() {
		if ($('#attrlist-dialog') !== "undefined") {
			$('#attrlist-dialog').dialog({
				modal: false,
				autoOpen: false,
				width: 1000,
				maxWidth: 1200,
				maxHeight: 630,
				buttons: {
					/*"選択": function() {
						//SaigaiTask.Detail.saveDetailData(${menuid}, this.form);
						return false;
					},
					"閉じる": function() {
						$(this).dialog("close");
						return false;
					}*/
				}
			});
			//attrlistbutton();
		}
	},

	/*attrlistbutton: function () {
		$(".relationattrlist").on("click", function(e) {
			e.preventDefault();
			$("#attrlist-dialog").html("");
			$("#attrlist-dialog").dialog("option", "title", "一覧表示").dialog("open");
			$("#attrlist-dialog").load(this.href, function() {
				$(this).dialog("option", "position", "{ my: 'center', at: 'center', of: 'window'}");
			});
			//$("#attrlist-dialog").dialog();
		});
	},*/

	openAttrlistDialog: function(url) {
		$("#attrlist-dialog").html("");
		$("#attrlist-dialog").dialog("option", "title", lang.__("Display list")).dialog("open");
		$("#attrlist-dialog").load(url, function() {
			$(this).dialog("option", "position", "{ my: 'center', at: 'center', of: 'window'}");
		});
	},

	addGeometry: function(menuid, gid) {
		var url = SaigaiTask.contextPath+"/page/list/addgeometry";

		// 変更登録
		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: url,
			data: "&menuid="+menuid+"&id="+gid,
			success: function(msg) {
				window.open(msg[0].rurl);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert(lang.__("Error : Failed to register"));
			}
		});
	},

	addRelationData: function(menuid, gid) {
		var url = SaigaiTask.contextPath+"/page/list/addrelation";

		// 変更登録
		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: url,
			data: "&menuid="+menuid+"&id="+gid,
			success: function(msg) {
				window.open(msg[0].rurl);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert(lang.__("Error : Failed to register"));
			}
		});
	},

	addRelationData2: function(menuid, menuid2, gid) {
		var url = SaigaiTask.contextPath+"/page/list/addrelation";

		// 変更登録
		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: url,
			data: "&menuid="+menuid+"&id="+gid+"&menuid2="+menuid2,
			success: function(msg) {
				window.open(msg[0].rurl);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				//TODO:エラー処理
				alert(lang.__("Error : Failed to register"));
			}
		});
	},

	showMap : function(url){
		$('#map-dialog').html('<iframe style="height: 100%; width: 100%; border: none;"></iframe>');
		$('#map-dialog iframe').attr("src",url);
		$('#map-dialog').dialog({
			title: lang.__("Map"),
			height:$(window).height()*0.85,
			width:$(window).width()*0.8,
			modal:true,
			buttons: [{
				text: lang.__("Close"),
				click: function(){
					$(this).dialog("close");
				}
			}]
		});
	},

	//様式ファイル出力
	outputReport: function(menuid, gid, form) {
		var url = SaigaiTask.contextPath+"/page/list/outputreport";

		var datas = [];
		datas.push(gid);

		//formのPOSTで文字列をJSON形式で送信
		do{
			if(form == null || form == undefined){
				alert(lang.__("Form has not been set correctly!"));
				break;
			}
			form.action = url;
			form.method = "POST";
			form.dataList.value = JSON.stringify(datas);
			form.submit();
		}while(0);
	}
};

function photodelete(obj){
	// ecommap用のファイルアップロード処理
	var layerid = $('[name=layerid]').val();
	var gid = $('#gid').val();
	var mid = $('#menuid').val();
	var menutaskid = $('#menutaskid').val();
	var session_token = $('[name=session_token]').val();
	var deleteidstr = obj.getAttribute("id");
	var deleteid = deleteidstr.replace("delete","");

	if(confirm(lang.__("File will be deleted. OK?"))) {
		//$("#detail-dialog").dialog("close");

		var url = "./list/deletephoto/"+mid+"/"+gid+"/"+layerid+"/"+deleteid;
		// 変更登録
		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: url,
			success: function(msg) {
				SaigaiTask.Detail.loadPhoto(layerid, gid, true);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(lang.__("Error : Failed to register"));
			}
		});
		
		
		/*var cs = currentSort;
		if (cs=='') cs = '-1';
		reloadContent("list/list/"+menutaskid+"/"+mid+"/"+cs);
		// 時間パラメータの付与
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			url += "&time="+iso8601Time;
		}
		reloadContent(url);*/
	}
}
