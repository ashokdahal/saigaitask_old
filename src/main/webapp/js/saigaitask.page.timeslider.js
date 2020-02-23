/**
 * フィルタの初期化
 */
SaigaiTask.Page.Timeslider = function(options) {
	this.initialize(options);
};

SaigaiTask.Page.Timeslider.UI = function(options) {
	this.initialize(options);
}
SaigaiTask.Page.Timeslider.UI.prototype = {

		/**
		 * @type SaigaiTask.Page.Timeslider
		 */
		timeslider: null,

		/**
		 * @type SaigaiTask.Map.Layer.LayerInfo
		 */
		layerInfo: null,

		div: null,

		/**
		 * 時間一覧
		 * @type {Array}
		 */
		timelist: null,

		/**
		 * レイヤID
		 * @type {String}
		 */
		layerId: null,

		/**
		 * 名称
		 * @type {String}
		 */
		name: null,

		/**
		 * @type {jQueryUI.slider}
		 */
		slider: null,

		/**
		 * Max側の表示テキスト
		 * @type {String}
		 */
		maxText: null,

		/**
		 * Min側の表示テキスト
		 * @type {String}
		 */
		minText: null,

		/**
		 * 時間指定解除ボタン
		 * @type {jQueryUI.Button}
		 */
		resetButton: null,

		/**
		 * 震源震度レイヤのAJAXのURL
		 * @type {String}
		 */
		eqURL: SaigaiTask.contextPath+"/page/earthquakelayer/",

		initialize: function(options) {
			var me = this;
			me.timeslider = options.timeslider;
			if(typeof options.layerInfo!="undefined") me.layerInfo = options.layerInfo;

			// 開始時刻と終了時刻を初期化
			if(0<SaigaiTask.Page.trackData.starttime) {
				me.minText = lang.__("Disaster start time");
				d=new Date(SaigaiTask.Page.trackData.starttime);
				d.setMilliseconds(0);
				d.setSeconds(d.getSeconds()+1);
				me.min = d.getTime();
			}
			else {
				me.minText = lang.__("A year before");
				me.min = -365*24*60*60*1000;
			}

			if(0<SaigaiTask.Page.trackData.endtime) {
				me.maxText = lang.__("Disaster end time");
				me.max = SaigaiTask.Page.trackData.endtime;
			}
			else {
				me.maxText = lang.__("Current time");
				me.max = "now";
			}

			me.initializeDiv();
		},

		initializeDiv: function(timelist) {
			var me = this;
			var time = null;
			// 全体のタイムスライダー
			if(me.layerInfo==null) time = SaigaiTask.PageURL.getTime();
			// レイヤ個別のタイムスライダー
			else {
				if(me.layerInfo.time!=null) {
					time = me.layerInfo.getTime();
				}
			}

			if(typeof timelist=="undefined") timelist = [];
			var layerInfo = me.layerInfo;

			var timesliderUI = $('<div class="timeslider-ui"></div>');
			{
				var controlDiv = $('<div id="timeslider-control" style="height: auto;"></div>');
				timesliderUI.append(controlDiv);

				if(0<timelist.length) {
					var prevMonth = $("<a class=\"btn_icon\">←</a>");
					prevMonth.click(function(){
						var date = me.getTime();
						date.setMonth(date.getMonth()-1);

						me.setTime(date);
						me.changetime(date.getTime());
					});
					controlDiv.append(prevMonth);

					var prevDay = $("<a class=\"btn_icon\">←</a>");
					prevDay.click(function(){
						var date = me.getTime();
						date.setDate(date.getDate()-1);

						me.setTime(date);
						me.changetime(date.getTime());
					});
					controlDiv.append(prevDay);

					var prevHours = $("<a class=\"btn_icon\">←</a>");
					prevHours.click(function(){
						var date = me.getTime();
						date.setHours(date.getHours()-1);

						me.setTime(date);
						me.changetime(date.getTime());
					});
					controlDiv.append(prevHours);

					var prevMinutes = $("<a class=\"btn_icon\">←</a>");
					prevMinutes.click(function(){
						var date = me.getTime();
						date.setMinutes(date.getMinutes()-1);

						me.setTime(date);
						me.changetime(date.getTime());
					});
					controlDiv.append(prevMinutes);
				}

				// 震度レイヤ処理
				if(layerInfo!=null && layerInfo.isEarthquakeLayer) {
					var eqSPAN = me.eqSPAN = $('<span>');
					me.setEqSPAN(lang.__("None"));
					controlDiv.append(eqSPAN);
					me.eqXHR = $.ajax({
						url: me.eqURL,
						dataType: "json",
						cache: false,
						success: function(data, dataType) {
							data.time = new Date(); // 取得時間
							me.eqData = data; // 記録

							// 指定時刻
							var date = me.getTime();

							// どの震度レイヤかチェック
							var earthquakelayerData = me.findEarthquakelayerDataByTime(date);
							if(earthquakelayerData!=null) {
								me.setEqSPAN(earthquakelayerData.layername);
							}
							else me.setEqSPAN(lang.__("None"));
						}
					});

					controlDiv.append('&nbsp;');
					var selectInput = $('<input type="button" value="'+lang.__('exchange')+'"/>');
					selectInput.click(function() {
						me.eqXHR = $.ajax({
							url: me.eqURL,
							dataType: "json",
							cache: false,
							success: function(data, dataType) {
								var results = data.results;
								me.eqDialog = (function(results){
									// ダイアログDIV
									var dialog = $("<div id='timeslider-dialog' title='"+lang.__("Select display data")+" : "+lang.__("Seismic intensity")+"'></div>");
									$("body").append(dialog);

									// 結果なし
									if(results.length==0) {
										dialog.append("<span>"+lang.__("Seismic intensity data not found.")+"</span>");
									}
									// 結果あり
									else {
										var table = $("<table>");
										dialog.append(table);

										var thead = $("<thead>");
										thead.append($("<tr>"
												+"<th>"+lang.__("Quake occurrence time<!--2-->")+"</th>"
												+"<th>"+lang.__("Epicenter")+"</th>"
												+"<th>"+lang.__("Magnitude")+"</th>"
												+"<th>"+lang.__("Maximum seismic intensity")+"</th>"
												+"<th>"+lang.__("Select")+"</th>"
												+"</tr>"))
										table.append(thead);

										var tbody = $("<tbody>");
										for(var idx in results) {
											var result = results[idx];
											var tr = $("<tr>"
													+"<td>"+result.origintime+"</td>"
													+"<td>"+result.areaname+"</td>"
													+"<td>"+result.mg+"</td>"
													+"<td>"+result.maxint+"</td>"
													+"<td>"+"<input type='button' value='"+lang.__("Select")+"'/>"+"</td>"
													+"</tr>");
											tbody.append(tr);
											// click event handler
											(function(tr, result) {
												var layerid = result.layerid;
												var fid = result.id;
												var time = result.origintime;
												$("input", tr).click(function() {
													//alert(layerid+"."+fid);
													me.setEqSPAN(result.layername);
													// 表示時刻変更
													var timefrom = new Date(result["time_from"]);
													me.setTime(timefrom);
													me.changetime(timefrom);
													me.eqDialog.dialog("close");
												});
											})(tr, result);
										}
										table.append(tbody);

										// tablesorter
										table.tablesorter({
											widgets: ['zebra']
										});
									}

									return dialog;
								})(results);

								// ダイアログ初期化
								me.eqDialog.dialog({
									width: "500px",
									minHeight: null,
									modal: true,
									position: {
										of: "body", //"#content_main",
										at: "center top",
										my: "center top"
									}
								});
							}
						});


						/*
						http://localhost:8080/SaigaiTask2-1504rc/page/
								//return me.loadTimelist().done(function(response) {
									//me.timelist = response.data;
									me.timelist = [];
									me.dialog = me.createDialog(me.timelist);
								//});
							}

							me.dialog.dialog("open");
							*/
					});
					controlDiv.append(selectInput);
					controlDiv.append($('<br/>'));
				}

				var inputLabel = $("<span>"+lang.__("Time display")+":</span>");
				//inputLabel.css("float", "left");
				controlDiv.append(inputLabel);

				// 日時入力
				input = me.input = $('<input type="text" style="width:180px;"/>'); // HH:mm までなら width:125px;
				var defaultDate = null;
				if(!!time) {
					defaultDate = me.setTime(time);
				}
				//input.css("float", "left");
				input.change(function() {
					var datetime = $(me.input).val();
					me.changetime(datetime);
				});
				controlDiv.append(input);
				//var input = me.input;
				var init = false;
				var initDatetimepicker = function() {
					if(!init) {
						init = true;
						// @see http://trentrichardson.com/examples/timepicker/
						var datetime = input.val();
						var d = new Date(datetime);
						d.setMilliseconds(0); // ミリ秒は切り落とし
						input.datetimepicker({
							controlType: 'select',
							timeFormat : 'HH:mm:ss',
							dateFormat : 'yy-mm-dd',
							defaultDate: datetime,
							parse: 'loose', // default strict
							showButtonPanel: false
						});
					}
				};
				initDatetimepicker();

				var resetButton = me.resetButton = $('<button type="button" style="margin-left:5px;">' + lang.__('Cancel time set') + '</button>');
				resetButton.button();
				resetButton.click(function() {
					me.changetime(null);
				});
				if(time==null) me.resetButton.css("visibility", "hidden");
				controlDiv.append(resetButton);

				/*
				var nextMinutes = $("<a class=\"btn_icon\">→</a>");
				nextMinutes.click(function(){
					var datetime = $(me.input).val();
					if(datetime==null||datetime.length==0) datetime = new Date().getTime(); // 日付未指定なら現在時刻を基点
					else datetime = new Date(datetime).getTime();
					var date = new Date(datetime);
					date.setMinutes(date.getMinutes()+1);

					me.setTime(date);
					me.changetime(date.getTime());
				});
				controlDiv.append(nextMinutes);

				var nextHours = $("<a class=\"btn_icon\">→</a>");
				nextHours.click(function(){
					var datetime = $(me.input).val();
					if(datetime==null||datetime.length==0) datetime = new Date().getTime(); // 日付未指定なら現在時刻を基点
					else datetime = new Date(datetime).getTime();
					var date = new Date(datetime);
					date.setHours(date.getHours()+1);

					me.setTime(date);
					me.changetime(date.getTime());
				});
				controlDiv.append(nextHours);

				var nextDay = $("<a class=\"btn_icon\">→</a>");
				nextDay.click(function(){
					var datetime = $(me.input).val();
					if(datetime==null||datetime.length==0) datetime = new Date().getTime(); // 日付未指定なら現在時刻を基点
					else datetime = new Date(datetime).getTime();
					var date = new Date(datetime);
					date.setDate(date.getDate()+1);

					me.setTime(date);
					me.changetime(date.getTime());
				});
				controlDiv.append(nextDay);

				var nextMonth = $("<a class=\"btn_icon\">→</a>");
				nextMonth.click(function(){
					var datetime = $(me.input).val();
					if(datetime==null||datetime.length==0) datetime = new Date().getTime(); // 日付未指定なら現在時刻を基点
					else datetime = new Date(datetime).getTime();
					var date = new Date(datetime);
					date.setMonth(date.getMonth()+1);

					me.setTime(date);
					me.changetime(date.getTime());
				});
				controlDiv.append(nextMonth);
				*/
			}

			/*
			timesliderUI.append($("<br/>"));

			{
				var name = $("<span>"+me.name+":</span>");
				name.css("float", "left");
				timesliderUI.append(name);


				var prev = $("<a class=\"btn_icon\">←</a>");
				prev.click(function(){
					var datetime = $(me.input).val();
					if(datetime==null||datetime.length==0) datetime = new Date().getTime(); // 日付未指定なら現在時刻を基点
					else datetime = new Date(datetime).getTime();
					var found = null;
					for(var idx in me.timelist) {
						var time = me.timelist[idx];
						if(time<datetime) {
							found = time;
						}
						else {
							if(isNaN(new Date(found))) { // 無効な日時であれば、つぎの日時から１分前の時間をチェック
								found = time - 60*1000;
								if(!(found<datetime)) {
									found = null; // 基点よりも前でなければ見つからない
								}
							}
							break;
						}
					}
					if(isNaN(new Date(found))) found = null; // 無効な日時であれば削除

					if(found==null) {
						alert("これ以上履歴がありません。");
					}
					else {
						var date = new Date(found);
						me.setTime(date);
						me.changetime(found);
					}
				});
				timesliderUI.append(prev);

				var next = $("<a class=\"btn_icon\">→</a>");
				timesliderUI.append(next);
				next.click(function() {
					var datetime = $(me.input).val();
					if(datetime==null||datetime.length==0) {
						alert("これ以上履歴がありません。");
						return; // 現在時刻表示なら次の履歴はない
					}
					else datetime = new Date(datetime).getTime();
					var found = null;
					for(var idx in me.timelist) {
						var time = me.timelist[idx];
						if(datetime<time) {
							found = time;
							break;
						}
					}
					if(found==null) {
						me.changetime(null);
					}
					else {
						me.setTime(new Date(found));
						me.changetime(found);
					}
				});
			}
			*/

			//timesliderUI.append($("<br/>"));

			{
				var slider = me.slider = $('<div id="slider" style="margin-top:5px;"></div>');
				var now = null;
				var updateNow = function() {
					now = new Date();
					now.setMilliseconds(0); // ミリ秒は切り落とし
					now = now.getTime();
				}
				updateNow();
				slider.slider({
					value: time!=null ? time.getTime() : now,
					min: 0<me.min ? me.min : (me.max=="now"?now:me.max)+me.min, // 現在時刻から一年前まで
					max: me.max=="now"?now:me.max, // 現在時刻,過去の災害の時は災害終了日時
					//step: 60*1000, // 1分間隔で移動
					step: 1000, // 1秒間隔で移動
					change: function(e, ui) {
					},
					start: function(e, ui) {
						// 現在時刻からスライド開始した場合
						if(me.max=="now" && ui.value==slider.slider("option", "max")) {
							updateNow();
							slider.slider("option", "max", now);
							slider.slider("option", "min", 0<me.min ? me.min : now+me.min);
							slider.slider("value", now);
						}
					},
					stop: function(e, ui) {
						// スライドし終わったらリロードさせる
						var time = ui.value==slider.slider("option", "max") ? null : ui.value; // max なら現在時刻にリセット
						me.changetime(time);
					},
					slide: function(e, ui) {
						// 時刻表示を更新
						var time = ui.value==slider.slider("option", "max") ? null : ui.value; // max なら現在時刻にリセット
						if(time!=null) me.setTime(new Date(time));

						// 震度レイヤでスライダーを動かした場合
						if(layerInfo!=null && layerInfo.isEarthquakeLayer) {
							// どの震度レイヤかチェック
							var earthquakelayerData = me.findEarthquakelayerDataByTime(new Date(time));
							if(earthquakelayerData!=null) {
								me.setEqSPAN(earthquakelayerData.layername);
							}
							else me.setEqSPAN(lang.__("None"));
						}
					}
				});
				// jQueryダイアログの初期表示で、最初の要素であるDatetimepickerのInputにフォーカスがあたってしまい
				// Datetimepickerが勝手に開いてしまうため、タイムスライダーのタグにフォーカスがあたるように autofocus 属性を指定する
				$("a", slider).attr("autofocus", "");
				timesliderUI.append(slider);

				//timesliderUI.append("<br/>");
				var rangeDiv = $("<div>");
				var minTextAnchor = $('<a style="float:left;cursor:pointer;">'+me.minText+'</a>');
				minTextAnchor.prop("title", me.min<0 ? me.minText : SaigaiTask.Page.Timeslider.formatDate(new Date(me.min)));
				minTextAnchor.click(function() {
					var time = me.min;
					if(me.max=="now") {
						updateNow();
						time = 0<me.min ? me.min : now+me.min
					}
					me.changetime(time);
					me.setTime(new Date(time));
				});
				rangeDiv.append(minTextAnchor);
				var maxTextAnchor = $('<a style="float:right;;cursor:pointer;">'+me.maxText+'</a>');
				maxTextAnchor.prop("title", me.max=="now" ? me.maxText : SaigaiTask.Page.Timeslider.formatDate(new Date(me.max)));
				maxTextAnchor.click(function() {
					me.changetime(null);
				});
				rangeDiv.append(maxTextAnchor);
				timesliderUI.append(rangeDiv).append("<br/>");
				//timesliderUI.append("<span>現在時刻から１年前まで指定できます。</span>");
			}

			me.div = timesliderUI;
		},

		setTime: function(time) {
			var me = this;
			var date = SaigaiTask.Page.Timeslider.formatDate(time);
			me.input.val(date);
			return date;
		},

		getTime: function() {
			var me = this;
			var datetime = $(me.input).val();
			if(datetime==null||datetime.length==0) datetime = new Date().getTime(); // 日付未指定なら現在時刻を基点
			else datetime = new Date(datetime).getTime();
			var date = new Date(datetime);
			return date;
		},

		changetime: function(ms) {
			var me = this;
			var change = false;
			// 時間指定解除
			if(ms==null) {
				if(me.max=="now") $(me.input).val(me.maxText);

				// slider は max にセットしておく
				var maxTime = me.slider.slider("option", "max");
				if(me.slider.slider("value")!=maxTime) {
					me.slider.slider("value", maxTime);
				}
				// 時間指定解除ボタンは非表示
				me.resetButton.css("visibility", "hidden");

				// 全体の時間指定
				var maxTime = me.max=="now" ? null : me.max;
				if(maxTime!=null) {
					var d = new Date(maxTime);
					maxTime = d.toISOString();
				}
				if(me.layerInfo==null) {
					if(SaigaiTask.PageURL.params.time!=maxTime) change = true;
					SaigaiTask.PageURL.params.time = maxTime;
				}
				// レイヤ個別指定
				else if(me.layerInfo!=null) {
					if(me.layerInfo.time!=maxTime) change = true;
					me.layerInfo.time = maxTime;
					me.setPageURLLayertimes();
				}
			}
			// 時間指定
			else {
				var d = new Date(ms);
				var time = d.toISOString();
				// slider を指定時刻に移動させる
				if(me.slider.slider("value")!=d.getTime()) {
					me.slider.slider("value", d.getTime());
				}
				// 時間指定解除ボタンは表示
				me.resetButton.css("visibility", "visible");

				// 全体の時間指定
				if(me.layerInfo==null) {
					if(SaigaiTask.PageURL.params.time==null || SaigaiTask.PageURL.params.time!=time) change = true;
					SaigaiTask.PageURL.params.time = time;
				}
				// レイヤ個別指定
				else if(me.layerInfo!=null) {
					if(me.layerInfo.time==null || me.layerInfo.time!=time) change = true;
					me.layerInfo.time = time;
					me.setPageURLLayertimes();
				}
			}
			var layerInfo = me.layerInfo;
			if(layerInfo!=null && layerInfo.isEarthquakeLayer) {
				var earthquakelayerData = me.findEarthquakelayerDataByTime(new Date(ms));
				if(earthquakelayerData!=null) {
					me.setEqSPAN(earthquakelayerData.layername);
				}
				else me.setEqSPAN(lang.__("None"));
			}
			if(change && typeof me.timeslider.onchangetime=="function") me.timeslider.onchangetime();
		},

		setPageURLLayertimes: function() {
			var me = this;
			var layertimes = "";
			var layerInfoStore = map.ecommaps[0].layerInfoStore;
			for(var layerId in layerInfoStore) {
				var layerInfo = layerInfoStore[layerId];
				if(layerInfo.time!=null) {
					if(layertimes.length!=0) layertimes += ",";
					var time = layerInfo.getTime();
					var iso8601Time = time.toISOString();
					layertimes += layerId +","+ iso8601Time;
				}
			}
			if(layertimes.length==0) delete SaigaiTask.PageURL.params.layertimes;
			else SaigaiTask.PageURL.params.layertimes = layertimes;
		},

		/**
		 * 震度レイヤ用関数
		 * @param date {Date} 指定時刻
		 * @return 指定時刻の震源震度レイヤデータ
		 */
		findEarthquakelayerDataByTime: function(date) {
			var me = this;
			if(!!me.eqData) {
				// FIXME: シーケンシャルサーチなので、データ量が多いとネックになると思う
				var results = me.eqData.results;
				for(var idx in results) {
					var result = results[idx];
					var timefrom = new Date(result["time_from"]);
					var timeto = new Date(result["time_to"]);
					if( (timefrom.getTime()<=date.getTime()) && (date.getTime()<=timeto.getTime()) ) {
						return result;
					}
				}
			}
			return null;
		},

		/**
		 * 震度レイヤ用関数
		 * 震度レイヤの表示データラベルを更新する
		 */
		setEqSPAN:  function(name) {
			var me = this;
			me.eqSPAN.html(lang.__("Display data")+": "+name);
		}
}

SaigaiTask.Page.Timeslider.prototype = {

		/**
		 * @type {jQuery.Dialog}
		 */
		dialog: null,

		/**
		 * 時間パラメータ変更時のイベントハンドラ
		 * @type {Function}
		 */
		onchangetime: null,

		initialize: function(option) {
			var me = this;
			if(!!option) {
				me.name = option.name;
				me.layerId = option.layerId;
			}
		},

		createDialog: function() {
			var me = this;

			// ダイアログDIV
			var dialog = $("<div id='timeslider-dialog' title='"+lang.__("Time slider")+"'></div>");
			$("body").append(dialog);

			// fieldset を使うため、formを追加
			var form = $("<form></form>");
			dialog.append(form);

			var wrapFieldset = function(option) {
				var ui = option.ui;
				var label = option.label;
				var appendCheckbox = option.appendCheckbox;
				var checkbox = '<input type="checkbox"/>';
				var fieldset = $('<fieldset>'+
						'<legend>'+(appendCheckbox?checkbox:"")+label+'</legend>'+
						'</fieldset>');
				// デフォルトのfieldsetだと、ExtJSのCSSがスタイルを打ち消してしまうので、
				// 手動で x-fieldset クラスを追加して、ExtJS の fieldset スタイルで表示させる
				fieldset.addClass("x-fieldset");
				fieldset.append(ui);
				return fieldset;
			}

			// 全体用のタイムスライダーUIの生成
			var timesliderUI = new SaigaiTask.Page.Timeslider.UI({
				timeslider: me
			}).div;
			//var timesliderUI = me.createTimesliderUI(timelist);
			timesliderUI = wrapFieldset({
				ui: timesliderUI,
				label: lang.__('whole'),
				appendCheckbox: false
			});
			form.append(timesliderUI);

			// 地図画面の場合
			if(typeof map!="undefined") {
				var ecommap = map.ecommaps[0];
				// 登録情報レイヤの個別指定インタフェースを追加
				var contentsLayerInfos = ecommap.contentsLayerInfos;
				for(var idx in contentsLayerInfos) {
					var layerInfo = contentsLayerInfos[idx];

					// 震度レイヤ以外は個別指定インタフェース非表示
					if(layerInfo.isEarthquakeLayer==false) continue;

					var timesliderUI = new SaigaiTask.Page.Timeslider.UI({
						timeslider: me,
						layerInfo: layerInfo
					}).div;
					timesliderUI = wrapFieldset({
						ui: timesliderUI,
						label: lang.__('separate specification')+":"+(layerInfo.isEarthquakeLayer?lang.__('Seismic intensity'):layerInfo.name),
						appendCheckbox: false
					});
					form.append(timesliderUI);
				}
			}

			return dialog;
		},

		show: function() {
			var me = this;

			if(me.timelist==null) {
				//return me.loadTimelist().done(function(response) {
					//me.timelist = response.data;
					me.timelist = [];
					me.dialog = me.createDialog(me.timelist);
					// ダイアログ初期化
					me.dialog.dialog({
						width: "500px",
						minHeight: null,
						position: {
							of: "body", //"#content_main",
							at: "center top",
							my: "center top"
						},
						/*
						buttons: [{
							text: "時間変更",
							click: function() {
								var datetime = $(me.input).val();
								me.changetime(datetime);
							}
						}, {
							text: "時間指定解除",
							click: function() {
								me.changetime(null);
							}
						}],
						*/
						open: function() {
							//me.input.blur();
						}
					});
					me.dialog.parent(".ui-dialog").css('zIndex',1400); // 監視観測のダイアログが1500 なので、それよりも後ろになるように調整
				//});
			}

			me.dialog.dialog("open");
		}
};


/**
 * @param time {Date}
 */
SaigaiTask.Page.Timeslider.formatDate = function(time, option) {
	var dateSplit = "-";
	var datetimeSplit = " ";
	var timeSplit = ":";
	var msSplit = ".";
	if(!!option) {
		if(typeof option.dateSplit!="undefined") dateSplit = option.dateSplit;
		if(typeof option.datetimeSplit!="undefined") datetimeSplit = option.datetimeSplit;
		if(typeof option.timeSplit!="undefined") timeSplit = option.timeSplit;
		if(typeof option.msSplit!="undefined") msSplit = option.msSplit;
	}

	var pad2 = function(n) {  // always returns a string
		return (n < 10 ? '0' : '') + n;
    }
	date = time.getFullYear()+dateSplit+pad2(time.getMonth()+1)+dateSplit+pad2(time.getDate());
	date += datetimeSplit;
	date += pad2(time.getHours())+timeSplit+pad2(time.getMinutes());
	if(0<time.getSeconds()) date += timeSplit+pad2(time.getSeconds());
	if(0<time.getMilliseconds()) date += msSplit+time.getMilliseconds();
	return date;
};

/**
 * 履歴ボタンクリック時にコールされる
 */
window.timesliderDialog = function() {
	SaigaiTask.Page.timeslider.show();
}

// 初期化
SaigaiTask.Page.timeslider = new SaigaiTask.Page.Timeslider(SaigaiTask.timesliderConf);
$(function() {
	SaigaiTask.Layout.body.on("oninitcontentlayout", function() {
		// 時刻が指定されているなら、タイムスライダーダイアログを初期表示
		var time = SaigaiTask.PageURL.getTime();
		if(!!time && time.getTime()!=SaigaiTask.Page.trackData.endtime) {
			SaigaiTask.Page.timeslider.show();
		}
	});
});
