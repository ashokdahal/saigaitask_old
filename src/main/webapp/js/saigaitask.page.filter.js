/**
 * フィルタの初期化
 */
SaigaiTask.Page.Filter = function(options) {
	this.initialize(options);
};

SaigaiTask.Page.Filter.prototype = {

	/**
	 * イベントオブジェクト
	 * @type {jQuery}
	 */
	events: null,

	/**
	 * フィルタ条件変更ダイアログ
	 * @type {jQuery.UI.Dialog}
	 */
	filterDialog: null,

	/**
	 * @type {Array<Object>} フィルター情報配列
	 */
	filterInfos: null,

	initialize: function(options) {
		var me = this;

		me.filterDialog = $("<div>");
		me.events = me.filterDialog;

		// フィルタ名称をリクエストパラメータから取得する
		if(!!SaigaiTask.PageURL.params.conditionValue) {
			var name = JSON.parse(SaigaiTask.PageURL.params.conditionValue).name;
			me.getFilterNameEl().text(!!name?name:lang.__("No filter name"));
		}
	},

	/**
	 * フィルタ名称要素の取得
	 */
	getFilterNameEl: function() {
		return $("#filter-name");
	},

	/**
	 * フィルタ結果数要素の取得
	 */
	getFilterNumEl: function() {
		return $("#filter-num");
	},

	/**
	 * フィルタ結果数の更新
	 * @param result
	 */
	updateFilterNum: function(result) {
		var me = this;
		var total = result.total;
		var num = total - result.filteredFeatureIds.length;
		var isnot = !!result.conditionValueActual && !!result.conditionValueActual.isnot ? result.conditionValueActual.isnot : false;
		if(isnot) num = result.filteredFeatureIds.length;
		var nofilter = !!result.conditionValueActual && !!result.conditionValueActual.nofilter ? result.conditionValueActual.nofilter : false;
		if(nofilter) {
			me.getFilterNumEl().html(" [ " + total + lang.__("Items")+" ]");
		}
		else {
			me.getFilterNumEl().html(" " + lang.__("({0} items / {1} items)", num, total));
		}
	},

	/**
	 * サーバからフィルタ情報を取得
	 */
	loadFilterInfos: function() {
		var defer = $.Deferred();
		$.ajax({
			url: SaigaiTask.contextPath+"/page/filter/list",
			data: {
				menuid: SaigaiTask.Page.menuInfo.id
			},
			dataType: "json",
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	},

	logicalTexts: {
		"and": lang.__("And"),
		"or": lang.__("or")
	},

	getLogicalText: function(logical) {
		var me = this;
		if(typeof logical=="string") {
			return me.logicalTexts[logical.toLowerCase()];
		}
		return "";
	},

	/**
	 * typeからテキスト文字列を取得します
	 */
	typeTexts: {
		1: lang.__("Overlap with the extent of {0}"),
		2: lang.__("Completely included in range of {0}"),
		101: lang.__("No overlap with the extent of {0}"),
		103: lang.__("Not completely included in range of {0}")
	},

	compTexts: {
		"~*": lang.__("{0} includes {1}"),
		"!~*": lang.__("{0} does not include {1}"),
		"=": lang.__("{0} is {1}"), // と等しい
		"<>": lang.__("{0 } is not {1}"), // と異なる
		"<=": lang.__("{0} is {1} or less"),
		">=": lang.__("{0} is {1} or more"),
		"<": lang.__("{0} is less than {1}"),
		">": lang.__("{0} is more than {1}")
	},

	initDialog: function(callback) {
		var me = this;
		// ダイアログがなければ作成し終わってから表示
		return me.loadFilterInfos().done(function(result) {
			var filterInfos = result.filterInfos;
			me.filterInfos = result.filterInfos;
			me.filterDialog = me.createDialog(filterInfos);
			if(!!SaigaiTask.PageURL.params.conditionValue) {
				me.applyCondition(JSON.parse(SaigaiTask.PageURL.params.conditionValue));
			}
			if(typeof callback=="function") callback();
		});
	},

	/**
	 * ダイアログを生成
	 */
	createDialog: function(filterInfos) {
		var me = this;

		// create table
		var filterDialog = me.filterDialog;
		filterDialog.attr("id", "select-feature-dialog");

		// assemble list
		for(var idx in filterInfos) {
			var filterInfo = filterInfos[idx];
			var layerInfos = filterInfo.layerInfos;

			if(0<idx) filterDialog.append($("<br/>"));
			var div = $("<div>", {
				id: "filterInfo-"+filterInfo.id
			});
			filterDialog.append(div);
			// フィルタ選択
			{
				var checkDiv = $("<div>");
				var check = $("<input type='radio' name='filterinfoid' id='select-filter-"+filterInfo.id+"' value='"+filterInfo.id+"'/>");
				var label = $("<label for='select-filter-"+filterInfo.id+"'>"+filterInfo.name+"</label>");
				checkDiv.append(check).append(label);
				div.append(checkDiv);
			}
			// 条件
			var conditionValue = filterInfo.conditionValue;
			var conditionDiv = $("<div>", {
				style: "margin-left: 15px"
			});
			div.append(conditionDiv);
			{
				// 属性検索
				if(!!conditionValue.condition && 0<conditionValue.condition.length) {
					var layerInfo = layerInfos[conditionValue.layerId];

					conditionDiv
					.append($("<span>").text(lang.__("[Attribute condition]")))
					.append("<br/>");

					{
						// table (condition)
						var table = $("<table>", {
							border: 0,
							style: "margin-left: 20px"
						});

						// tbody
						var tbody = $("<tbody>");
						table.append(tbody);
						for(var conditionIdx in conditionValue.condition) {
							var condition = conditionValue.condition[conditionIdx];
							var attrName = condition.attrId;
							if(!!layerInfo.AttrInfo) {
								for(var attrInfoIdx in layerInfo.AttrInfo) {
									var attrInfo = layerInfo.AttrInfo[attrInfoIdx];
									if(condition.attrId==attrInfo.attrId) {
										attrName = attrInfo.name;
									}
								}
							}
							tbody.append(
								$("<tr>")
								.append($("<td>").text(me.getLogicalText(condition.logical)))
								.append($("<td>")
/*
									.append($("<span>", {
										style: "padding: 0px 5px"
									}).text(attrName))
									.append($("<span>", {
										style: "padding: 0px 5px"
									}).text("が"))
									.append($("<span>", {
										style: "padding: 0px 5px"
									}).text(condition.value))
									.append($("<span>", {
										style: "padding: 0px 5px"
									}).text(me.compTexts[condition.comp]))
*/
									.append(
										MessageFormat.format(
											me.compTexts[condition.comp],
											'<span style="padding: 0px 10px">' + attrName + '</span>',
											'<span style="padding: 0px 10px">' + condition.value + '</span>'
										)
									)
								)
							);
						}
						conditionDiv.append(table);
					}
				}
				// 空間検索の条件
				if(!!conditionValue.spatiallayer && 0<conditionValue.spatiallayer.length) {
					conditionDiv
					.append($("<span>").text(lang.__("[Search scope]")))
					.append("<br/>");

					{
						// table (spatiallayer)
						var table = $("<table>", {
							border: 0,
							style: "margin-left: 20px"
						});

						// tbody
						var tbody = $("<tbody>");
						table.append(tbody);
						for(var spatiallayerIdx in conditionValue.spatiallayer) {
							var spatiallayer = conditionValue.spatiallayer[spatiallayerIdx];
							var layerInfo = layerInfos[spatiallayer.layer];
							var layerName = spatiallayer.layer;
							if(!!layerInfo && !!layerInfo.name) {
								layerName = layerInfo.name;
							}
							tbody.append(
								$("<tr>")
								.append($("<td>").text(me.getLogicalText(spatiallayer.logical)))
/*
								.append($("<td>", {
									style: "padding: 0px 5px"
								}).text(layerName))
								.append($("<td>").text("の"+me.typeTexts[spatiallayer.type]))
*/
								.append($("<td>", {
									style: "padding: 0px 5px"
									})
									.append(
										MessageFormat.format(
											me.typeTexts[spatiallayer.type],
											'<span style="padding: 0px 10px">' + layerName + '</span>'
										)
									)
								)
								.append($("<td>",{
										style: "padding: 0px 5px"
									})
									.append($("<span>").text(lang.__("(Buffer")))
									.append($("<input>", {
										type: "text",
										name: "buffer",
										style: "text-align: right; width: 50px",
										value: spatiallayer.buffer
									}))
									.append($("<span>").text("m)"))
								)
							);
						}
						conditionDiv.append(table);
					}
				}
			}
		}

		return filterDialog;
	},

	/**
	 * フィルタ条件を読み込んで、ダイアログの選択状態やバッファの値を反映する。
	 */
	applyCondition: function(conditionValue) {
		var me = this;
		if(!!conditionValue) {
			var filterinfoid = conditionValue.filterinfoid;
			if(!!filterinfoid) {
				var filterInfoDiv = $("#filterInfo-"+filterinfoid, me.filterDialog);
				if(0<filterInfoDiv.length) {
					// チェック状態の反映
					filterInfoDiv.find("input[name='filterinfoid']").attr("checked", "checked");
					if(!!conditionValue.spatiallayer) {
						// バッファの反映
						var bufferInputs = filterInfoDiv.find("input[name='buffer']");
						bufferInputs.each(function(index) {
							var spatiallayer = conditionValue.spatiallayer[index];
							if(!!spatiallayer && !!spatiallayer.buffer) {
								$(this).val(spatiallayer.buffer);
							}
						});
					}
				}
			}
		}
	},

	/**
	 * ダイアログを表示
	 */
	showDialog: function() {
		var me = this;

		// ダイアログがなければ作成し終わってから表示
		if(me.filterInfos==null) {
			return me.initDialog(function() {
				me.showDialog();
			});
		}

		// ダイアログを表示
		var filterDialog = me.filterDialog;
		filterDialog.dialog({
			title: lang.__("Change filter conditions"),
			modal: true,
			maxHeight: 500,
			minWidth: 700,
			buttons: [
				{
					text: lang.__("Search"),
					click: function(event){
						me.search({
							alert: true
						});
					}
				},
				{
					text: lang.__("Close"),
					click: function(event){
						filterDialog.dialog("close");
					}
				}
			]
		});
	},

	search: function(opt) {
		var me = this;
		var filterDialog = me.filterDialog;
		var alert = !!opt && !!opt.alert ? window.alert : function(){};

		// ダイアログがなければ作成し終わってから検索
		if(me.filterInfos==null) {
			return me.initDialog(function() {
				me.search();
			});
		}

		var checked = $("input[name='filterinfoid']:checked", filterDialog);
		if(checked.length==0) {
			if(0<me.filterInfos.length) {
				alert(lang.__("Select."));
			}
		}
		else {
			var filterinfoid = checked.val();

			// フィルタ情報の取得
			var filterInfo = null;
			for(var idx in me.filterInfos) {
				if(me.filterInfos[idx].id==filterinfoid) {
					filterInfo = me.filterInfos[idx];
					var bufferError = false;
					// バッファの変更
					var filterInfoDiv = $("#filterInfo-"+filterInfo.id, filterDialog);
					var bufferInputs = filterInfoDiv.find("input[name='buffer']");
					bufferInputs.each(function(index) {
						if(bufferError) return;
						var spatiallayer = filterInfo.conditionValue.spatiallayer;
						var buffer = $(this).val();
						if($.isNumeric(buffer)==false || buffer<0) {
							alert(lang.__("Please enter numeric to buffer"));
							bufferError = true;
						}
						spatiallayer[index].buffer = buffer;
					});
					if(bufferError) return;
					break;
				}
			}

			// マスク対象のエレメント
			var maskEl = filterDialog.parents(".ui-dialog");
			maskEl.mask(lang.__("Searching..."));

			// フィルタ名称の更新
			me.getFilterNameEl().html(filterInfo.name);
			// フィルタ結果数は検索中と表示しておく
			me.getFilterNumEl().html(" " + lang.__("(Searching ...)"));

			// リクエストパラメータの更新
			SaigaiTask.PageURL.override({
				conditionValue: filterInfo.conditionValue
			});

			// 再検索
			me.searchAjax(filterInfo.conditionValue)
				.done(function(result) {
					// フィルタ結果数の更新
					me.updateFilterNum(result);

					// ダイアログを閉じる
					if(filterDialog.hasClass("ui-dialog-content")) {
						filterDialog.dialog("close");
					}

					// フィルタ変更イベントのトリガー
					me.events.trigger("search", [filterInfo, result]);
				}).fail(function() {
					me.getFilterNumEl().html(" " + lang.__("(Search error)"));
				}).always(function(xhr, textStatus) {
					// マスクをはずす
					maskEl.unmask();
				});
		}
	},

	/**
	 * 再検索
	 * //@ param {Number} filterinfoid フィルタ情報ID
	 * @param {Object} conditionValue 検索条件JSONオブジェクト
	 */
	searchAjax: function(conditionValue) {
		var defer = $.Deferred();
		$.ajax({
			url: SaigaiTask.contextPath+"/page/filter/search",
			data: {
				menuid: SaigaiTask.Page.menuInfo.id,
				//filterid: filterinfoid
				conditionValue: JSON.stringify(conditionValue),
				time: SaigaiTask.PageURL.params.time,
				layertimes: SaigaiTask.PageURL.params.layertimes
			},
			dataType: "json",
			success: defer.resolve,
			error: defer.reject
		});
		return defer.promise();
	}
};
