/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/**
 * リストからおよびマップからの更新処理関連で、どちらからも利用する関数を定義
 */

SaigaiTask.Edit = {

	/**
	 * オブジェクトをSAStrutsのクエリ文字列に変換する.
	 * @param {Object} obj
	 * @return {String} クエリ文字列
	 */
	toSAStrutsParam:function (obj) {
		var prefix =null;
		var params = [];
		var add = function(key, value) {
			value = value == null ? "" : value;
			params[params.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
		};
		/**
		 * @param prefix HTTPリクエストパラメータ名
		 * @param obj HTTPリクエストパラメータ値
		 */
		var buildParams = function(prefix, obj) {
			if(jQuery.isArray(obj)) {
				jQuery.each(obj, function(idx, value) {
					buildParams(prefix+"["+idx+"]", value);
				});
			}
			else if(jQuery.type(obj)==="object") {
				var name = null;
				for(name in obj) {
					buildParams(prefix+"."+name, obj[name]);
				}
			}
			else {
				add(prefix, obj);
			}
		};

		for(prefix in obj) {
			buildParams(prefix, obj[prefix]);
		}

		return params.join("&").replace(/%20/g, "+");
	},

	/**
	 * 一括変更でNumber型のカラムに値を設定する場合、入力を半角数字だけにする
	 */
	numOnly: function(event) {
		//IE uses this
	    if(window.event) {
	        code = window.event.keyCode;
	    }
	    //FF uses this
	    else {
	        code = (event.which) ? event.which : event.keyCode
	    }
	    if( code >= 96 && code <= 105 ||// テンキー0～9チェック
	        code >= 48 && code <= 57 ||// 0～9チェック
	        code == 8) //Backspace
	    	return true;
		return false;
	},

	/**
	 * 一括変更でNumber型のカラムに値を設定する場合、入力を半角数字だけにする
	 */
	floatOnly: function(event) {
		//IE uses this
	    if(window.event) {
	        code = window.event.keyCode;
	    }
	    //FF uses this
	    else {
	        code = (event.which) ? event.which : event.keyCode
	    }
	    if( code >= 96 && code <= 105 ||// テンキー0～9チェック
	        code >= 48 && code <= 57 || // 0～9チェック
	        code == 8 || //Backspace
	        code == 190 || code == 110)//Period
	    	return true;
		return false;
	}

};




/**
 * 一括変更クラス
 * イベントは dialog に対して設定する.
 * - slimersuccess 一括変更の成功
 */
SaigaiTask.Edit.Slimer = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Edit.Slimer.prototype = {

	/**
	 * IDのフィールド名(キー)
	 */
	key: null,

	/**
	 * 一括対象テーブル名
	 */
	table: null,

	/**
	 * 一括変更カラムの配列
	 * @type {Array}
	 * column.name {String} カラム名
	 * column.field {String} カラムのフィールド名
	 * column.dataType {String} カラムのデータ型
	 * column.selectOptions {Array<selectOption>} Selectデータ型の場合、Selectに用いる表示値(text)と値(value)の情報配列
	 * column.checkDisplay {String} チェック時の表示値
	 * column.editable {Boolean} 編集可フラグ
	 * column.nullable {Boolean} 必須項目でないフラグ
	 * column.addable {Boolean} 追記可フラグ
	 * column.defaultcheck {Boolean} 初期チェック
	 * column.check {jQuery} 変更チェックボックスの input
	 * column.check2 {jQuery} 追記チェックボックスの input
	 * column.input {jQuery} input か select
	 *
	 */
	columns: null,

	/**
	 * 一括変更グルーピング
	 * @type {Array}
	 * grouping.name {String} カラム名
	 * grouping.field {String} カラムのフィールド名
	 * grouping.dataType {String} カラムのデータ型
	 * grouping.selectOptions {Array<selectOption>} Selectデータ型の場合、Selectに用いる表示値(text)と値(value)の情報配列
	 * grouping.check {jQuery} チェックボックスの input
	 * grouping.select {jQuery} select
	 * grouping.groupdefaultcheck {Boolean} グループ初期チェック
	 */
	groupings: null,

	/**
	 * 一括変更対象のID
	 * 指定がない場合はすべて対象となる
	 */
	targetIds: null,

	/**
	 * ダイアログ用Element
	 * @type {jQuery}
	 */
	dialog:null,

	/**
	 * 一括変更処理中フラグ
	 * @type {Boolean}
	 */
	proceed: false,

	initialize: function(slimerInfo) {
		var me = this;
		me.key = slimerInfo.key;
		me.table = slimerInfo.table;
		me.columns = slimerInfo.columns;
		if(!!slimerInfo.fids) me.targetIds = slimerInfo.fids;
		if(!!slimerInfo.targetIds) me.targetIds = slimerInfo.targetIds;
		if(!!slimerInfo.groupings) me.groupings = slimerInfo.groupings;

		var slimerDialog = me.dialog = $("<div>").hide();
		$(document.body).append(slimerDialog);

		if(me.hasEditableColumn()) {
			var enableGrouping = !!me.groupings && 0<me.groupings.length;

			// グルーピングがないなら、参考までに選択中の数を表示させる
			if(!enableGrouping && !!me.targetIds && 0<me.targetIds.length) {
				slimerDialog.append($("<div style='float: right;'>").append("<font color='gray'>" + lang.__("(Target of change {0} items selected", me.targetIds.length) + "</font>")).append("<br>");
			}
			// 説明
			var description = $("<div>")
			.append(lang.__("Bulk change is possible for following items.")).append("<br>")
			.append(lang.__("Input or select value to be set.")).append("<br>")
			.append(lang.__("* Item that you do not change, please uncheck the box."));
			slimerDialog.append(description);

			//slimerDialog.append("<br>");

			// TD のクリックを チェックボックスに連動させる
			var syncCheckClickWithTd = function(td, check) {
				td.click(function() { check.click(); });
				check.click(function(event) { event.stopPropagation(); } );
			};

			// TD のクリックを 変更、追記チェックボックスに連動させる
			var syncDoubleCheckClickWithTd = function(td, check, check2, defaultcheck) {
				var count = 0;
				//初期チェック状態がtrueの場合、countを1ずらしておく
				if(defaultcheck) count++;
				td.click(function() {
					count++;
					//追記チェックボックスのチェックを外す
					if(count%3==0) check2.click();
					//変更チェックボックスにチェックを入れる
					if(count%3==1) check.click();
					//追記チェックボックスにチェックを入れる
					if(count%3==2) check2.click();
				});
				//チェックボックスをクリックしたときは処理を行わない
				check.click(function(event) {
					event.stopPropagation();
				});
				check2.click(function(event){
					event.stopPropagation();
				});
			};

			// table
			var table = $('<table border="0" cellpadding="3" cellspacing="2" id="slimerTable" class="tablesorter" style="margin-left:10px;">');
			slimerDialog.append(table);
			// グルーピングに指定されている項目があれば表示
			if(enableGrouping) {
				table.append($('<tr><td colspan="4">' + lang.__('Bulk change group') + '</td></tr>'));
				for(var key in me.groupings) {
					var grouping = me.groupings[key];
					var columnId = me.table + ":" + grouping.field;
					var tr = $('<tr>');
					// チェックボタン TD
					grouping.check = $('<input type="checkbox" id="'+columnId+':grouping" value="'+grouping.field+'"/>');
					tr.append($('<td colspan="2">').append(grouping.check));
					// グルーピング名 TD
					tr.append($("<td>").append(grouping.name));

					// 行クリックでチェックボックスの ON/OFF
					syncCheckClickWithTd($("td", tr), grouping.check);

					// グルーピング値 TD
					grouping.select = $('<select name="'+key+'" id="'+columnId+':grouping">');
					grouping.select.css("width", "90%");
					var selectOptions = grouping.selectOptions;
					for(var selectOptionsIdx=0; selectOptionsIdx<selectOptions.length; selectOptionsIdx++) {
						var selectOption = selectOptions[selectOptionsIdx];
						// TODO: 選択型なら、選択値以外は出さないようにチェックする
						//if(grouping.dataType=="Select") {
						var option = $('<option value="'+selectOption.value+'">'+selectOption.text+'</option>');
						grouping.select.append(option);
					}
					tr.append($('<td>').append(grouping.select));

					// 初期チェックがfalseなら入力フォームをグレーアウト
					if(grouping.groupdefaultcheck){
						$(grouping.check).attr('checked',true);
					}
					else{
						grouping.select.attr("disabled", "disabled");
					}

					// チェックボックスの ON/OFF と 入力フォームの 有効/無効 の連動
					me.syncInputDisabled(grouping.check, grouping.select);

					table.append(tr);
				}
				// 一括変更グループのセルの背景色を変更
				$("td", table).css("background-Color", "#eeece1");
				table.append($('<tr><td colspan="4" >' + lang.__('Bulk change target item') + '</td></tr>'));
			}

			// 一括変更カラム
			var tr = $("<tr>");
			table.append(tr);
			var changeTd = $('<td></td>').append(lang.__("Change"));
			tr.append(changeTd);
			var postscriptTd = $('<td></td>').append(lang.__("Postscript"));
			tr.append(postscriptTd);
			var itemTd = $('<td></td>').append(lang.__("Item name"));
			tr.append(itemTd);
			var valueTd = $('<td></td>').append(lang.__("Value"));
			tr.append(valueTd);

			for(var idx in me.columns) {
				var column = me.columns[idx];
				var columnId = me.table+':'+column.field;
				if(column.editable) {
					var tr = $("<tr>");
					table.append(tr);
					column.check = $('<input type="checkbox" id="'+columnId+':ck" value="'+column.field+'"/>');
					//一括追記可能だった場合
					if(column.addable){
						column.check2 = $('<input type="checkbox" id="'+columnId+':ck2" value="'+column.field+'"/>');
					}
					// 一括変更対象のチェックフラグ
					var checkTd = $('<td></td>').append(column.check);
					tr.append(checkTd);
					// 一括追記対象のチェックフラグ
					var checkTd2 = $('<td></td>').append(column.check2);
					tr.append(checkTd2);
					// 項目名
					var nameTd = $('<td id="'+columnId+':td">'+column.name
							+(!column.nullable?'<font style="color:FF0000"> ※</font>':'')+'</td>');
					tr.append(nameTd);

					// 初期チェック状態がtrueの場合、変更のチェックボックスにチェックを入れる
					if(column.defaultcheck){
						$(column.check).attr('checked',true);
					}

					// 一括変更グループ初期チェック状態がtrueの場合、チェックボックスにチェックを入れる
					if(column.groupdefaultcheck){
						$("input[id='"+columnId+":grouping'").attr('checked',true);
					}

					// 行クリックでチェックボックスの ON/OFF
					//変更のチェックボックス
					if(column.check2==undefined){
						syncCheckClickWithTd($("td", tr), column.check);
					}else{
						syncDoubleCheckClickWithTd($("td", tr), column.check, column.check2, column.defaultcheck);
					}

					//変更と追記のチェックボックスを同時にチェックできないようにする
					$("input[id$=':ck']").click(function(event){
						$("input[id='"+event.target.id+"2']").prop('checked', false);
					});
					$("input[id$=':ck2']").click(function(event){
						var id = event.target.id;
						var result = id.substr(0,id.length-1);
						$("input[id='"+result+"']").prop('checked', false);
					});

					// 入力フォーム
					var valueTd = $('<td id="'+columnId+':td" class="'+column.dataType+'">');
					tr.append(valueTd);
					var input = null;
					switch(column.dataType.toLowerCase()) {
					case "text":
					case "string":
						input = $('<input type="text" id="'+columnId+':input">');
						valueTd.append(input);
						break;
					case "date":
						input = $('<input type="text" id="'+columnId+':input">');
						$(input).datepicker({
							controlType: 'select',
							dateFormat : 'yy/mm/dd'
						});
						valueTd.append(input);
						break;
					case "datetime":
						input = $('<input type="text" id="'+columnId+':input">');
						$(input).datetimepicker({
							controlType: 'select',
							timeFormat : 'HH:mm:ss',
							dateFormat : 'yy/mm/dd'
						});
						valueTd.append(input);
						break;
					case "number":
					case "integer":
						input = $('<input type="text" id="'+columnId+':input">');
						input.keydown(function(event) { return SaigaiTask.Edit.numOnly(event); });
						valueTd.append(input);
						break;
					case "float":
						input = $('<input type="text" id="'+columnId+':input">');
						input.keydown(function(event) { return SaigaiTask.Edit.floatOnly(event); });
						valueTd.append(input);
						break;
					case "select":
						valueTd.append($('<span id="'+column.field+'" style="display:none;">'+column.field+'</span>'));
						var select = input = $('<select name="'+column.field+'" id="'+columnId+':input">');
						if(!!column.selectOptions) {
							// 必須でない項目で、最初の項目が空値でなかったら、空値を追加する
							if(column.nullable && column.selectOptions[0].value!='') {
								select.append('<option value=""></option>');
							}
							for(var selectOptionsIdx in column.selectOptions) {
								var selectOption = column.selectOptions[selectOptionsIdx];
								// FIXME: selected あってる？
								var selected = (selectOption.value==column.field);
								select.append('<option value="'+selectOption.value+'" '+(selected?'selected':'')+'">'+selectOption.text+'</option>');
							}
						}
						else {
							// 必須でない項目で、最初の項目が空値でなかったら、空値を追加する
							if(column.nullable) { select.append('<option value=""></option>');}
						}
						valueTd.append(select);
						break;
					case "checkbox":
						valueTd.append($('<span id="'+column.field+'" style="display:none;">'+column.field+'</span>'));
						// FIXME: checked あってる？
						input = $('<input type="checkbox" name="'+column.field+'" value="'+column.checkDisplay+'" '+(column.checkDisplay==column.field?"checked":"")+' id="'+columnId+':input">');
						valueTd.append(input);
						break;
					case "textarea":
						input = $('<textarea id="'+columnId+':input">');
						valueTd.append(input);
						break;
					}

					// チェックボックスの ON/OFF と 入力フォームの 有効/無効 の連動
					me.syncInputDoubleDisabled(column.check, column.check2,input);
					// 2014/12/17 errorになるのでコメントアウト
					// input.css("width", "90%");
					// 各jQueryオブジェクトを保存
					column.input = input;
					if(!column.defaultcheck) input.attr("disabled","disabled");
				}
			}
			//slimerDialog.append("<br>");

			// button
			var dialogButtonList = $('<ul id="dialog-button-list">');
			// 保存ボタン
			var saveButton = $('<a href="#" class="dialog-button">' + lang.__('Save') + '</a>');
			saveButton.click(function() {
				var confirmDiv = $("<div>")
				.append(lang.__("Set the value specified in bulk."))
				.append("<br>")
				.append(lang.__("Are you sure?"));
				$(confirmDiv).dialog({
					resizable : false,
					//height : 200,
					modal : true,
					buttons : [
						{
							text : "OK",
							click : function() {
								$(this).dialog("close");
								me.slimerEdit(grouping);
							}
						},
						{
							text : lang.__("Cancel"),
							click : function() {
								$(this).dialog("close");
							}
						}
					]
				});
			});
			saveButton.button();
			dialogButtonList.append(saveButton);
			// キャンセルボタン
			var cancelButton = $('<a href="#" class="dialog-button">' + lang.__('Cancel') + '</a>');
			cancelButton.click(function() {
				me.dialog.dialog("close");
			});
			cancelButton.button();
			dialogButtonList.append(cancelButton);
			$("li", dialogButtonList).css("margin", "5px").css("float", "left").css("list-style", "none");
			slimerDialog.append($('<div style="float: right;">').append(dialogButtonList));
		}
		else {
			slimerDialog.append("<div>" + lang.__("There is no item that can be changed in batch in this page. ") + "<br></div>");
		}
	},

	/**
	 * フォームに値をセットします.
	 *
	 * @param {Object} values
	 */
	setValues: function(values) {
		console.log("values");
		console.log(values);
		var me = this;
		for(var idx in me.columns) {
			var column = me.columns[idx];
			var value = values[column.field];
			// 値がない場合は空にする
			if(typeof value=="undefined") value = null;

			// チェックボックスの value は消さない
			if(column.editable && !!column.input && column.dataType!="Checkbox") {
				column.input.val(value);
			}
		}
	},

	/**
	 * 1つのチェックボックスの ON/OFF で フォームを有効/無効にする
	 * @param {jQuery} check 変更チェックボックスのinput
	 * @param {jQuery} input 入力フォーム(inputでもselectでも、disabled 属性が効くもの)
	 */
	syncInputDisabled: function(check, input) {
		if(check.prop("checked")!=undefined) {
			//alert(check.prop("checked"));
			check.click(function() {
				if(check.prop("checked")) input.removeAttr("disabled");
				else input.attr("disabled", "disabled");
			});
		}
	},

	/**
	 * 2つのチェックボックスの ON/OFF で フォームを有効/無効にする
	 * @param {jQuery} check 変更チェックボックスのinput
	 * @param {jQuery} check2 追記チェックボックスのinput
	 * @param {jQuery} input 入力フォーム(inputでもselectでも、disabled 属性が効くもの)
	 */
	syncInputDoubleDisabled: function(check, check2, input) {
		if(!!check2){
			if(check.prop("checked")!=undefined && check2.prop("checked")!=undefined){
				check.click(function(){
					if(check.prop("checked") || check2.prop("checked")) input.removeAttr("disabled");
					else input.attr("disabled", "disabled");
				});
				check2.click(function(){
					if(check.prop("checked") || check2.prop("checked")) input.removeAttr("disabled");
					else input.attr("disabled", "disabled");
				});
			}
		}else{
			if(check.prop("checked")!=undefined) {
				check.click(function() {
					if(check.prop("checked")) input.removeAttr("disabled");
					else input.attr("disabled", "disabled");
				});
			}
		}
	},
	/**
	 * 編集可のカラムがあるかどうか
	 */
	hasEditableColumn: function() {
		var me = this;
		for(var idx in me.columns) {
			var column = me.columns[idx];
			if(column.editable==true) return true;
		}
		return false;
	},

	/**
	 * 終了処理
	 * もう使う予定がない場合は呼ぶこと
	 */
	destroy: function() {
		var me = this;
		me.dialog.remove();
	},

	/**
	 * 一括変更の開始処理
	 */
	onSlimerStart: function() {
		var me = this;
		me.proceed = true;
		// 閉じるボタンをクリックされないように親からマスクする
		me.dialog.parent().mask(lang.__("In processing of bulk change"));
	},

	/**
	 * 一括変更の終了処理
	 */
	onSlimerEnd: function() {
		var me = this;
		me.proceed = false;
		// マスクをはずす
		me.dialog.parent().unmask();
		me.dialog.dialog("destroy");
	},

	/**
	 * 一括変更実行
	 */
	slimerEdit: function(){

		var me = this;
		if(me.proceed) {
			alert(lang.__("In processing of bulk change."));
			return;
		}

		//送信データ定義
		var data = {
			//変更対象項目
			columns: [],
			tablename: me.table,
			key: me.key,
			//変更対象レコードID
			targetIds: [0],
			//変更対象項目
			slimerDatas: [],
			groupingDatas: []
		};

		//変更対象レコードIDのセット
		if(me.targetIds!=null && 0<me.targetIds.length) {
			data.targetIds = me.targetIds;
		}
		//data.targetIds = JSON.stringify(data.targetIds);
		//data.targetIds = data.targetIds.join(",");

		//変更対象項目のセット
		for(var idx in me.columns) {
			var column = me.columns[idx];
			//変更がチェックされていたら一括変更対象とする
			var addable = (!!column.check2 && column.check2.prop("checked"));
			if(!!column.check && column.check.prop("checked") || addable) {
				var input = column.input;
				var dataType = column.dataType;
				//データタイプごとにvalueを取得
				var value;
				//CheckBoxは1つのみという前提？
				if(dataType=='Checkbox'){
					if(input.prop("checked")) value = input.val();
					else value = "";
				}else{
					value = input.val();
					//nullチェック
					if(!column.nullable) {
						if(value==null || value.length==0 || value=="null") {
							alert(lang.__("There is no value in the required fields!\n Please enter a value."));
							me.onSlimerEnd();
							return false;
						}
					}

					if(dataType=='Number') {
						if (value != "" && (!jQuery.isNumeric(value) || value.indexOf(".") > 0)) {
							alert(lang.__("Input value of a numeric is invalid."));
							me.onSlimerEnd();
							return false;
						}
					}
					if(dataType=='Float') {
						if (value != "" && !jQuery.isNumeric(value)) {
							alert(lang.__("Input value of a numeric is invalid."));
							me.onSlimerEnd();
							return false;
						}
					}
					if (dataType=='Date') {
						if (value != "" && !value.match(/^\d{4}\/\d{2}\/\d{2}$/)) {
							alert(lang.__("Input value of date is invalid."));
							me.onSlimerEnd();
							return false;
						}
					}
					if (dataType=='DateTime') {
						if (value != "" && !value.match(/^([1-2]\d{3}\/([0]?[1-9]|1[0-2])\/([0-2]?[0-9]|3[0-1])) (20|21|22|23|[0-1]?\d{1}):([0-5]?\d{1}):?([0-5]?\d{1})?$/)) {
							alert(lang.__("Input value of date and time is invalid."));
							me.onSlimerEnd();
							return false;
						}
					}
				}
				//ID文字列の整形
				//「table名:カラム名」を「table名:field名:key名:ID」に変換
				//フィルタリングがある場合は末尾のIDを各レコードにセットする
				data.slimerDatas.push({
					//id: idStr,
					column: column.field,
					value: value,
					addable: addable
				});
			}
		}
		//data.slimerDatas = toSAStrutsParam(data.slimerDatas);

		//グルーピング
		if(!!me.groupings && 0<me.groupings.length) {
			for(var idx in me.groupings) {
				var grouping = me.groupings[idx];
				//チェックされていたら一括変更対象とする
				if(grouping.check.prop("checked")){
					data.groupingDatas.push({
						id: me.table+":"+grouping.field,
						value: grouping.select.val()
					});
				}
			}
		}
		//data.groupingDatas = toSAStrutsParam(data.groupingDatas);
		//alert(data.slimerDatas[0].id+","+data.slimerDatas[0].value);
		//alert(toSAStrutsParam(data));

		// 開始処理
		me.onSlimerStart();

		//データ送信
		$.ajax({
			dataType: "json",
			type: "POST",
			cache: false,
			url: SaigaiTask.contextPath +"/page/slimer/",
			//headers: {"X-CSRF-Token":SaigaiTask.csrfToken},
			data: toSAStrutsParam(data),
			success: function(msg) {
				//document.location.reload();
				val = msg[0].updatetime;
				if (val) {
					//最終更新日時の設定
					jQuery.ajax({
						url: SaigaiTask.contextPath+"/page/getUpdateTime",
						data: {
							timestamp: val
						},
						success: function(result) {
							if(typeof result.text=="string") {
								SaigaiTask.setUpdateTime(result.text);
							}
						}
					});
				}

				me.dialog.trigger("slimersuccess");

				// 終了処理
				me.onSlimerEnd();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				// エラー処理
				alert(lang.__("Error : Failed to register"));
				me.onSlimerEnd();
			}
		});
	}
}
