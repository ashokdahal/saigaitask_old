/** setupper パッケージ */
SaigaiTask.training = {};

/**
 * content部分だけ再読み込みします.
 * @param {String} url
 */
SaigaiTask.training.loadContent = function(url) {
	var content = $('#content');
	try {

		var isXmlEditor = false;
		if(url.indexOf('xmlEditor/content') >= 0){
			isXmlEditor = true;
		}

		if(!isXmlEditor){
			// content 部分のレイアウトを破棄
			if(!!SaigaiTask.Layout.contentLayout) SaigaiTask.Layout.contentLayout.destroy();
			SaigaiTask.Layout.contentLayout = null;
			if(!!SaigaiTask.Layout.content) SaigaiTask.Layout.content.html("");
			SaigaiTask.Layout.content = null;
		}else{
			// 防災情報XML編集ボタン押下時
			// 右パネルを全て非表示
			$("#planListDiv").css({"display":"none"});
			$("#planBasicDiv").css({"display":"none"});
			$("#planExternalDataDiv").css({"display":"none"});
			$("#planControlDiv").css({"display":"none"});
		}

		// 開いているダイアログをすべて閉じる
		$(".ui-dialog-content").each(function(){
			var dialog = $(this);
			dialog.dialog("destroy");
		});

		// すでにリクエストを出していたらアボートする
		if(!!SaigaiTask.training.loadContentXHR) SaigaiTask.training.loadContentXHR.abort();
		// パラメータ
		var params = {};
		content.mask("Loading...");
		// リクエスト開始
		SaigaiTask.training.loadContentXHR = $.ajax({
			url: url,
			success: function(html, dataType) {
				//html = "<div></div>";
				if(!isXmlEditor){
					content.html(html);
					SaigaiTask.Layout.content=content;
					SaigaiTask.Layout.initContentLayout();
				}else{
					// 防災情報XML編集ボタン押下時
					$('#xmlEditorDiv').html(html);
					$('#xmlEditorDiv').css({'display':'block'});
				}
			},
			error: function(xhr, textStatus, errorThrown) {
				if(textStatus=="error") {
					if(!!xhr.responseText) {
						content.html(xhr.responseText);
						$("input[type='button']", content).hide();
					}
					else {
						content.html(lang.__("An error occurred."));
					}
				}
				else if(textStatus=="timeout") {
					content.html(lang.__("Timeout occurred."));
				}
			},
			complete: function(xhr, textStatus) {
				content.unmask();
				// ボタンの初期化
				btn.init();
			}
		});
	} catch(e) {
		 console.error(e);
		 alert(lang.__("Can not read<!--2-->"));
	}
}

/**
 * Form を送信します.
 * @param {String} form の styleId
 * @param {String} confirmMsg 確認ダイアログのメッセージ
 */
SaigaiTask.training.submitForm = function(formId, confirmMsg) {
	var form = document.getElementById(formId);
	Ext.MessageBox.confirm(lang.__("Sending confirmation"), confirmMsg, function(btn) {
		if (btn == "yes") {
			$("body").mask(lang.__("Now registering.."));
			form.submit();
		} else if (btn == "no") {
			// do nothing
		}
	}, window);
	return false;
}

/**
 * 自治体情報のエクスポート
 * @param {Number} localgovinfoid 自治体ID
 */
SaigaiTask.training.exportInfo = function(localgovinfoid) {
	var msg = lang.__("Are you sure to export following data?");
	msg += "<br/>・" + lang.__("System Master");
	msg += "<br/>・" + lang.__("Municipality setting");
	msg += "<br/>・" + lang.__("Master map");
	Ext.MessageBox.confirm(lang.__("Export"), msg, function(btn) {
		if (btn == "yes") {
			location.href=SaigaiTask.contextPath+"/admin/training/export?localgovinfoid="+localgovinfoid;
		} else if (btn == "no") {
			// do nothing
		}
	}, window);
}

/** XmlEditor パッケージ */
SaigaiTask.training.XmlEditor = {};
/**
 * @param {Number} index
 */
SaigaiTask.training.XmlEditor.showEditGroupDialog = function(index) {
	try {
		// 編集対象のデータ取得
		var content = $("#content");
		var form = $("#XmlEditorForm");
		var table = $("#XmlEditor-table");
		var groupidInputs = $("input[name=groupid]", form);
		var groupidInput  = groupidInputs.eq(index);
		var groupnameInputs = $("input[name=groupname]", form);
		var groupnameInput  = groupnameInputs.eq(index);
		console.debug("[showGroupContextmenu] groupid: "+groupidInput.val()+", groupname: "+groupnameInput.val());

		// ダイアログ
		var dialogStyleId = "edit-group-dialog";
		var dialog = $("#"+dialogStyleId);

		// なければ新規作成
		if(dialog.length==0) {
			var dialog = $("<div>");
			dialog.attr("id", dialogStyleId);

			// index
			var indexInput = $("<input name='index' type='hidden'>");
			dialog.append(indexInput);

			// 班名
			var dialogNameInput = $("<input name='groupname' type='text'>");
			dialog.append($(lang.__("<span>Group name:</span>")));
			dialog.append(dialogNameInput);

			// 設定コピー
			var copyfromDiv = $("<div id='copyfrom-container'>");
			var dialogCopyfromSelect = $("<select name='copyfrom'>");
			copyfromDiv.append(dialogCopyfromSelect);
			copyfromDiv.append($(lang.__("<span>copy settings</span>")));
			dialog.append(copyfromDiv);

			// ダイアログ生成
			dialog.dialog({
				title: lang.__("Edit group info"),
				position: {
					of: "#content_main",
					at: "left top",
					my: "left top"
				},
				buttons: [
					{
						text: lang.__("Delete"),
						click: function() {
							// target index
							var index = indexInput.val();
							var groupid = $("input[name=groupid]").eq(index).val();
							var groupname = $("input[name=groupname]").eq(index).val();

							if(groupid==SaigaiTask.loginDataDto.groupid) {
								alert(lang.__("Logged-in users can not be deleted."));
							}
							else {
								var msg = lang.__("Group name: \"{0}\" will be deleted..<br/>(Restoration impossible after deletion.)<br/>Continue?", groupname);
								Ext.MessageBox.confirm(lang.__("Delete group info"), msg, function(btn) {
									if (btn == "yes") {
										SaigaiTask.training.XmlEditor.removeGroupColumn(index);
										dialog.dialog("close");
									} else if (btn == "no") {
										// do nothing
									}
								}, window);
							}
						}
					},
					{
						text: "OK",
						click: function(event) {
							// target index
							var index = indexInput.val();

							// 班名
							var name = dialogNameInput.val();
							if(name.length==0) {
								alert(lang.__("Name is required."))
								return;
							}
							$("input[name=groupname]").eq(index).val(name); // 送信入力フォームを更新
							$("#XmlEditor-table span.groupname").eq(index).html(name); // 表示を更新
							$("#XmlEditor-table-sticky span.groupname").eq(index).html(name); // 表示を更新(sticky)

							// copyfrom
							var copyfrom = dialogCopyfromSelect.val();
							if($.isNumeric(copyfrom) && copyfrom!=index) {
								SaigaiTask.training.XmlEditor.copy(copyfrom, index);
							}

							// dialog close
							dialog.dialog("close");
						}
					},
					{
						text: lang.__("Cancel"),
						click: function(event) {
							dialog.dialog("close");
						}
					}
				]
			});
		}

		// 値の設定
		$("input[name=index]", dialog).val(index);
		$("input[name=groupname]", dialog).val(groupnameInput.val());
		// 設定コピー SELECT の初期化
		var copyfromDiv = $("#copyfrom-container", dialog);
		var dialogCopyfromSelect = $("select[name=copyfrom]", dialog);
		dialogCopyfromSelect.val('');
		dialogCopyfromSelect.html('') // option 初期化;
		dialogCopyfromSelect.append($("<option value=''>--</option>"));
		var num = groupnameInputs.length;
		if(num<=1) {
			copyfromDiv.hide();
		}
		else {
			copyfromDiv.show();
			for(var idx=0; idx<num; idx++) {
				if(index==idx) continue; // 自分自身ははずす
				var name = groupnameInputs.eq(idx).val();
				dialogCopyfromSelect.append($("<option value='"+idx+"'>"+name+"</option>"));
			}
		}

		// ダイアログ表示
		dialog.dialog("open");
	} catch(e) {
		alert(lang.__("An error occurred."));
		throw e;
	}
}

SaigaiTask.training.XmlEditor.copy = function(copyfrom, copyto) {
	var table = $("#XmlEditor-table");
	var trList = $("tr", table);
	for(var trIdx in trList) {
		var tr = trList.eq(trIdx);
		if(tr.hasClass("tablesorter-headerRow")) continue;

		var groupCols = $("td.group-col", tr);
		// copyfrom
		var copyfromTd = groupCols.eq(copyfrom);
		var copyfromInputs = $("input", copyfromTd);
		// copyto
		var copytoTd = groupCols.eq(copyto);
		var copytoInputs = $("input", copytoTd);
		// copy input val
		for(var inputIdx in copyfromInputs) {
			var copyfromInput = copyfromInputs.eq(inputIdx);
			var copytoInput = copytoInputs.eq(inputIdx);
			// チェックボックスの場合はチェック状態をコピー
			if(copyfromInput.attr("type")=="checkbox") {
				var checked = copyfromInput.prop("checked");
				copytoInput.attr("checked", checked);
			}
			else {
				copytoInput.val(copyfromInput.val());
			}
		}
	}
}

SaigaiTask.training.XmlEditor.newIdSeq = -1;
SaigaiTask.training.XmlEditor.getNewIdSeq = function() {
	return SaigaiTask.training.XmlEditor.newIdSeq--;
}

SaigaiTask.training.XmlEditor.addGroupColumn = function(groupname) {
	if(!groupname) groupname = lang.__("New group");
	SaigaiTask.training.XmlEditor.replaceTable(function(table) {
		var form = $("#XmlEditorForm");
		var groupid = SaigaiTask.training.XmlEditor.getNewIdSeq();
		// 列を追加
		$("thead tr", table).each(function(index, elem) {
			var tr = $(this);
			// ヘッダーの追加
			if(tr.hasClass("tablesorter-headerRow")) {
				var th = $('<th class="sorter-false filter-false link group-col" style="background-color: burlywood;">'
				+'<span class="groupname">'+groupname+'</span>'
				+'</th>');
				tr.append(th);
			}
		});
		$("tbody tr", table).each(function(index, elem) {
			var tr = $(this);
			// menuid の取得(tr内では、すべて同じmenuid)
			var menuid = tr.find("input[name=menu]").eq(0).val().split(":")[2];

			// td 追加
			var td = $('<td class="group-col">'
			+'<input type="checkbox" name="menu"  value="c:'+groupid+':'+menuid+'"/>'
			+'<input type="text" size="1" name="d:'+groupid+':'+menuid+'"/>'
			+'</td>');
			tr.append(td);
		});
		// form に送信フォームを追加
		form.append($("<input name='groupid'   type='hidden' value='"+groupid+"'/>"));
		form.append($("<input name='groupname' type='hidden' value='"+groupname+"'/>"));
	});
}

SaigaiTask.training.XmlEditor.removeGroupColumn = function(removeIndex) {
	console.debug.apply(console, ["removeGroupColumn: "].concat(arguments));

	// テーブルの列を削除
	SaigaiTask.training.XmlEditor.replaceTable(function(table) {
		// 列を追加
		$("thead tr", table).each(function(index, elem) {
			var tr = $(this);
			// THの列削除
			if(tr.hasClass("tablesorter-headerRow")) {
				$("th.group-col", tr).eq(removeIndex).remove();
			}
		});
		$("tbody tr", table).each(function(index, elem) {
			var tr = $(this);
			// TDの列削除
			$("td.group-col", tr).eq(removeIndex).remove();
		});
	});

	// form から削除
	var form = $("#XmlEditorForm");
	var groupidInput = $("input[name=groupid]", form).eq(removeIndex);
	var deletegroupid = groupidInput.val();
	groupidInput.remove()
	$("input[name=groupname]", form).eq(removeIndex).remove()
	// 削除groupid を追加
	if(0<deletegroupid) {
		var deletegroupidInput = $("<input type='hidden' name='deletegroupid'/>");
		deletegroupidInput.val(deletegroupid);
		form.append(deletegroupidInput);
	}
}

SaigaiTask.training.XmlEditor.replaceTable = function(process) {
	// テーブル取得
	var table = $("#XmlEditor-table");
	var container = $(table.parent());

	// テーブルをdocumentから取り除く
	table.remove();
	table.tablesorter("destroy");
	$("#XmlEditor-table-sticky").remove(); // sticky header
	$("tr.tablesorter-filter-row", table).remove(); // filter header

	// 新しいテーブル
	var newtable = $('<table id="XmlEditor-table" style="margin-top:0px; margin-left:0px;">');
	//newtable.html(table.html()); // これだと、input の値がもとにもどる
	newtable.append(table.children());

	// 処理実行
	process(newtable);

	// document へ戻す
	container.append(newtable);

	// tablesorter 初期化
	newtable.trigger('refreshWidgets', [true, true]);
	SaigaiTask.training.XmlEditor.initTable();
}

SaigaiTask.training.XmlEditor.initTable = function() {
	// tablesorter
	var table = $("#XmlEditor-table");
	table.tablesorter({
		theme: "blue"
		,widthFixed: false
		,initWidgets: true
		,widgets: ['stickyHeaders', "resizable", "filter"]
		,widgetOptions: {
			// sticky headers
			stickyHeaders_attachTo: "#content_main"
			// filter
			,filter_hideFilters: true
		}
		//,debug: true
	});
	// .appear-text-on-hover のイベント初期化
	var appearTextOnHoverSelector = "td.task-col,td.subtask-col";
	$(appearTextOnHoverSelector, table).not(".first").css("color", "transparent");
	$("tr", table).on({
		"mouseenter": function() {
			$(this).find(appearTextOnHoverSelector).not(".first").css("color", "inherit");
		},
		"mouseleave": function() {
			$(this).find(appearTextOnHoverSelector).not(".first").css("color", "transparent");
		}
	});
	// separator
	$("tr", table).each(function() {
		var tr = $(this);
		// task-separator
		if(0<tr.children(".task-col.first").length) {
			tr.css("border-top", "3px solid");
		}
		// subtask-separator
		else if(0<tr.children(".subtask-col.first").length) {
			tr.children("td").not(".task-col").css("border-top", "2px solid");
		}
	});
	// td をクリックしてチェックする
	$("td", table).click(function(e) {
		var input    = $(this).find("input:text").get(0);
		var checkbox = $(this).find("input:checkbox").get(0);
		if(!!checkbox && e.target!=checkbox && e.target!=input) {
			checkbox.checked = !checkbox.checked;
		}
	});
	// table, sticky-table も
	$(".groupname").on("click", function() {
		var targetThEl = $(this).parents('.group-col').get(0);
		var targetTrEl = $(targetThEl).parents("tr").get(0);
		var index = jQuery.inArray(targetThEl, $(".group-col", $(targetTrEl))); // 編集対象の班情報インデックス
		SaigaiTask.training.XmlEditor.showEditGroupDialog(index);
	});
}
