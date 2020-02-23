/**
 * @class SaigaiTask.Map.view.PrintPreviewWindow
 * eコミマップベースの印刷プレビュー付き印刷ダイアログです.
 * @requires SaigaiTask/Map/view.js
 * @see /map/map/PdfWriter.js (ecommap)
 */
SaigaiTask.Map.view.PrintPreviewWindow = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.PrintPreviewWindow.prototype = {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	pdfControl: null,

	contentsContainer: null,

	formPanel: null,

	/**
	 * 詳細設定から簡単設定に切り替えた際に、
	 * 簡単設定にない設定項目が消えてしまうので、
	 * ここですべての設定値を保持する。
	 */
	values: null,

	previewMapImageContainer: null,

	/**
	 * PDF範囲レイヤ
	 * @type {SaigaiTask.Map.Layer.PdfRangeLayer}
	 */
	pdfRangeLayer: null,

	progressbar: null,
	progressbarInitText: lang.__('Creating PDF file..'),

	exportButton: null,
	exportImageButton: null,
	downloadButton: null,

	win: null,
	topToolbar: null,

	initialize: function(pdfControl) {
		var me = this;
		me.pdfControl = pdfControl;
		var stmap = me.stmap = pdfControl.stmap;

		// デフォルト設定値
		me.values = {
			printBaselayer: "1", // 背景地図出力
			cols: 1,
			rows: 1,
			printmap: 1,
			printlegend: 1
		};

		// 印刷ダイアログ初期化
		me.createWindow({
			easy: true
		});

		// 印刷範囲レイヤを初期化
		me.pdfRangeLayer = new SaigaiTask.Map.Layer.PdfRangeLayer(me);
	},

	// BEGIN createWindow:
	createWindow: function(opt) {
		var me = this;
		var stmap = me.stmap;
		var easy = opt.easy;

		// コンテンツHBox
		var winWidth = 0;
		var hbox = {
			xtype: 'container',
			layout: "hbox",
			style: {
				background: "white"
			},
			items: []
		};
		var hboxContainerObj = Ext.create('Ext.container.Container', hbox);
		me.contentsContainer = hboxContainerObj;

		// 印刷プレビュー
		var previewContainer = null;
		{
			// プレビューコンテナ生成
			previewContainer = {
				xtype: 'container', cls: 'first-container', border: 1,
				margin: 5,
				layout: 'vbox', // テキストエリア最大化
				items: [{
					xtype: "label",
					html: lang.__("印刷プレビュー")+'<span style="font-size:.8em">'+lang.__("（登録情報のみ）")+'</span>'
				}, {
					xtype: "label",
					html: '<span style="font-size:.8em">'+lang.__("表示ページ")+'&nbsp;'+lang.__("縦")+'&nbsp;'+lang.__("横")+'</span>'
				}]
			};

			// プレビュー画像
			{
				// 画像のコンテナを生成
				var imgContainerWidth = 256; // ポップアップウィンドウの画像の幅はいくつにする？
				var extImgContainer = {
					xtype: 'container',
					width: imgContainerWidth,
					style: {
						'text-align': 'center',
						cursor: 'pointer'
					}
				};
				var extImgContainerObj = Ext.create('Ext.container.Container', extImgContainer);
				me.previewMapImageContainer = extImgContainerObj;

				winWidth += extImgContainer.width;
			}
			previewContainer.items.push(extImgContainerObj);

			// HBoxに配置する
			hboxContainerObj.add(previewContainer);
		}

		// 印刷設定フォーム
		//formPanel.render('form-ct'); // for debug
		var formPanel = easy ? me.createEasyFormPanel() : me.createDetailFormPanel();
		me.formPanel = formPanel;
		// HBoxに配置する
		hboxContainerObj.add(formPanel);
		winWidth += formPanel.width;
		winWidth += 30; // マージン調整用

		// プログレスバー
		var progressbar =  Ext.create('Ext.ProgressBar', {
			hidden: true,
			text : me.progressbarInitText
		});
		me.progressbar = progressbar;

		// PDF出力ボタン
		var exportButton = Ext.create("Ext.Button", {
			text: lang.__("マップのPDFを出力"),
			icon: stmap.icon.getURL("printIconURL"),
			disabled: !me.isExportable(),
			tooltip: !me.isExportable() ? lang.__("出力内容を１つ以上選択してください。") : "",
			handler: function() {
				// 電子国土の場合、用紙サイズをA4までに制限する
				if(formPanel.form.isValid()) {
					me.onexport();
				}
				else {
					Ext.MessageBox.show({
						title : lang.__('PDF output error'),
						msg : lang.__('Setting is incorrect.\n Check input content.'),
						buttons : Ext.MessageBox.OK,
						animateTarget : exportButton.getEl(),
						icon : Ext.MessageBox.ERROR
					});
				}
			}
		});
		me.exportButton = exportButton;

		// 画像出力ボタン
		var exportImageButton = Ext.create("Ext.Button", {
			text: lang.__("地図画像を出力"),
			icon: stmap.icon.getURL("downloadMapIconURL"),
			handler: function() {
				// 電子国土の場合、用紙サイズをA4までに制限する
				if(formPanel.form.isValid()) {
					me.onexport();
				}
				else {
					Ext.MessageBox.show({
						title : lang.__('PDF output error'),
						msg : lang.__('Setting is incorrect.\n Check input content.'),
						buttons : Ext.MessageBox.OK,
						animateTarget : exportButton.getEl(),
						icon : Ext.MessageBox.ERROR
					});
				}
			}
		});
		me.exportImageButton = exportImageButton;

		// ダウンロードボタン
		var downloadButton = Ext.create("Ext.Button", {
			text: lang.__("Download"),
			icon: stmap.icon.getURL("downloadIconURL"),
			href: SaigaiTask.contextPath+"/PdfServlet?download",
			hidden: true
		});
		me.downloadButton = downloadButton;

		// 印刷ダイアログ
		var win = Ext.create("Ext.Window", {
			cls: "printPreviewWindow",
			closeAction: "hide",
			title: lang.__("Print dialog"),
			width: winWidth,
			dockedItems: [{
				xtype: 'toolbar',
				dock: 'top',
				padding: "10 0 3 0",
			    style: {
			    	background: "white",
			    },
			    items: [{
					xtype: "tbtext",
					text: lang.__("マップとして印刷可能なPDFファイルに出力します。"),
					style: {
				    	fontSize: "12px"
					}
				}, {
					xtype: 'tbfill'
				}, {
					xtype: 'button',
					icon: stmap.icon.getURL("rangeIconURL"),
					text: lang.__("範囲設定"),
					padding: 0,
					style: {
				    	fontSize: "12px"
					},
					handler: function() {
						// 印刷ダイアログを一旦非表示にする
						me.hideWithoutReset();
						// 印刷範囲変更ダイアログを表示
						me.pdfRangeLayer.showPdfRangeDialog();
					}
				}, {
					xtype: 'button',
					text: lang.__("保存"),
					padding: 0,
					style: {
				    	fontSize: "12px"
					},
					handler: function() {
						alert("実装中");
					}
				}, {
					xtype: 'button',
					text: lang.__("読み込み"),
					padding: 0,
					style: {
				    	fontSize: "12px"
					},
					handler: function() {
						// 印刷ダイアログを一旦非表示にする
						me.hideWithoutReset();
						// 印刷範囲一覧ダイアログを表示
						me.pdfRangeLayer.showRangeListDialog();
					}
				}, {
					xtype: 'button',
					icon: stmap.icon.getURL("reloadIconURL"),
					text: easy ? lang.__("詳細設定") : lang.__("かんたん設定"),
					padding: 0,
					style: {
				    	fontSize: "12px"
					},
					handler: function() {
						// 現在の設定値を取得し、保存する
						var values = me.getValues();
						// ウィンドウを削除し、再作成
						me.win.destroy();
						me.createWindow({
							easy: !easy
						});
						// 設定を反映して、表示
						me.formPanel.form.setValues(values);
						me.win.show();
					}
			    }]
			}],
			//items: [formPanel, progressbar],
			items: [hboxContainerObj, progressbar],
			fbar: [downloadButton, exportButton, exportImageButton],
			listeners: {
				show: function(window, eOpts) {
					// 地図画面の中央に表示
					var x = -win.getWidth()/2;
					var y = -win.getHeight()/2;
					win.alignTo("map", "c", [x, y]);

					// プレビュー画像を表示
					me.showPreview();

					// 印刷範囲レイヤを表示
					me.showPrintRange();

					// MGRSグリッド表示の設定を初期化
					var mgrsField = formPanel.form.findField("mgrs");
					if(mgrsField.getValue()==null) {
						var value = ""+(stmap.controls.mgrsControl.layer.getVisibility() ? stmap.controls.mgrsControl.layerInfo.params.precision : "-1");
						mgrsField.setValue(value);
					}
					
					// プログレスバーの幅を調整
					progressbar.setWidth(win.getWidth()-10);
				},

				hide: function(window, eOpts) {
					// ユーザが印刷ダイアログを閉じた場合は終了処理を行う
					me.onHideDialog();
				}
			}
		});
		me.win = win;
		me.topToolbar = win.getDockedItems("toolbar[dock='top']")[0];
	},
	// END createWindow:

	resetOnHide: true,

	hideWithoutReset: function() {
		var me = this;
		me.resetOnHide = false;
		me.win.hide();
		me.resetOnHide = true;
	},

	createEasyFormPanel: function() {
		var me = this;
		var stmap = me.stmap;
		var formPanel = Ext.create('Ext.form.Panel', {
			frame : false,
			border: false,
			width : 380,
			bodyPadding : 5,

			fieldDefaults : {
				labelAlign : 'left',
				anchor : '100%'
			},

			items : [{
				xtype: 'container', cls: 'first-container', border: 1,
				layout: 'fit', // テキストエリア最大化
		        items: [{
					xtype : 'textareafield',
					name : 'maptitle',
					fieldLabel : lang.__('Map title'),
					labelAlign: "top",
					rows: 2,
					value : "",
					//allowBlank: false // 未入力だと地図タイトルが自動で挿入される（非表示にできない）
					allowBlank: true // 現在は未入力だと非表示にできるのでオプション入力に変更
		        }, { // saigaitask.page.map で初期値セットするので必要
					xtype : 'hiddenfield',
					name : 'description',
					value : ""
		        }]
			}, {
				xtype: 'container', cls: 'first-container', border: 1,
		        layout: 'hbox',
		        items: [{
					flex: 1,
					xtype:  'combo',
					fieldLabel: lang.__("Paper size"),
					labelWidth: '80px',
					store: new Ext.data.SimpleStore({
						fields: ['value', 'display'],
						data: [
							["a0", "A0"],
							["a1", "A1"],
							["a2", "A2"],
							["a3", "A3"],
							["a4", "A4"],
							["a5", "A5"],
							["b0", "B0"],
							["b1", "B1"],
							["b2", "B2"],
							["b3", "B3"],
							["b4", "B4"],
							["b5", "B5"]
						],
						autoLoad: false
					}),
					displayField: 'display',
					valueField: 'value',
					name: "pagesize",
					value: "a4",
					editable: false, // 自由入力不可
					//selectOnFocus: true, // editable:false の combo は selectOnFocus:false であるべき
					validator: function(value) {
						value = this.getRawValue();
						var baseLayerInfo = stmap.map.baseLayer.layerInfo;
						/*
						if(typeof baseLayerInfo!="undefined" && baseLayerInfo.type==SaigaiTask.Map.Layer.Type.WEBTIS) {
							// 地理院タイルの場合はA3サイズまで
							var a3 = pdfControl.getPrintSize("a3");
							var selected = pdfControl.getPrintSize(value);
							if(selected.width <= a3.width && selected.height <= a3.height) {
								return true;
							}
							else return lang.__("Paper size of the Geographical Survey Institute tile is up to A3.");
						}
						*/
						return true;
					}
				}, {
		            xtype: 'splitter'
		        }, {
					flex: 1,
					xtype : 'radiogroup',
					fieldLabel : lang.__('Paper orientation'),
					labelWidth: '60px',
					columns: 2,
					items : [{
						name : 'rotate',
						inputValue : '1',
						boxLabel : lang.__('Horizontal'),
						checked: true
					}, {
						name : 'rotate',
						inputValue : '0',
						boxLabel : lang.__('Vertical')
					}],
					listeners: {
						change: function(radiogroup, newValue, oldValue, eOpts) {
							me.onchangePaperField();
						}
					}
		        }]
			}, {
				xtype: 'container', cls: 'first-container', border: 1,
				layout: 'hbox',
				defaultMargins: { top:5,bottom:5,left:5,right:5 },
				items: [{
					flex: 1,
					xtype:  'combo',
					fieldLabel : lang.__('分割印刷(縦x横)'),
					labelAlign: "top",
					store: new Ext.data.SimpleStore({
						fields: ['value', 'display'],
						data: [
							["1,1", "1 x 1"],
							["2,1", "2 x 1"],
							["1,2", "1 x 2"],
							["2,2", "2 x 2"],
							["3,3", "3 x 3"],
							["4,4", "4 x 4"]
						],
						autoLoad: false
					}),
					displayField: 'display',
					valueField: 'value',
					name: "splitprint",
					value: "1,1",
					editable: false, // 自由入力不可
					//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
					listeners: {
						"change": function(field, newValue, oldValue, eOpts) {
							var cr = newValue.split(',');
							me.values.rows = cr[0]; 
							me.values.cols = cr[1]; 
							me.onchangePaperField();
							// TODO: pdf.setPreviewPageNum(f.cols.value, f.rows.value);
						}
					}
				}, {
					flex: 2,
					xtype: "displayfield",
					margin: 5,
					value: lang.__("地図を複数ページに分割して印刷します。 拡大した地図が印刷できます。")
				}]
			}, {
				xtype: 'container', cls: 'first-container', border: 1,
		        layout: 'fit',
		        items: [{
					xtype:  'combo',
					fieldLabel: lang.__("UTM grid"),
					store: new Ext.data.SimpleStore({
						fields: ['value', 'display'],
						data: [
							["-1", lang.__("Hide")],
							["0", lang.__("0 digit: 100km")],
							["1", lang.__("Single digit: 10km")],
							["2", lang.__("The second digit: 1km")],
							["3", lang.__("The third digit: 100m")],
							["4", lang.__("The forth digit: 10m")]
							//"1m(5桁)"
						],
						autoLoad: false
					}),
					displayField: 'display',
					valueField: 'value',
					name: "mgrs",
					editable: false // 自由入力不可
					//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
		        }]
			}, {
				xtype: 'container', cls: 'first-container', border: 1,
				layout: 'hbox',
				items: [{
					flex: 1,
					xtype:  'combo',
					fieldLabel : lang.__('凡例サイズ'),
					labelAlign: "top",
					store: new Ext.data.SimpleStore({
						fields: ['value', 'display'],
						data: [
							["1.8",  lang.__("特大")],
							["1.35", lang.__("大きい")],
							["1.0",  lang.__("普通")],
							["0.8",  lang.__("小さい")],
							["0.5",  lang.__("特小")]
						],
						autoLoad: false
					}),
					displayField: 'display',
					valueField: 'value',
					name: "legendrate",
					value: "1.0",
					editable: false // 自由入力不可
					//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
				}, {
					flex: 2,
					xtype: 'container',
					layout: 'vbox',
					margin: "5 5 0 5", // bottom:0
					items: [{
						xtype:  'checkbox',
						name: "legendpos",
						boxLabel : lang.__("凡例を別ページに表示"),
						margin: 0,
						inputValue : "0"
					}, {
						xtype:  'checkbox',
						name: "legenddisplay",
						boxLabel : lang.__("全ての登録情報の凡例を表示する"),
						margin: 0,
						inputValue : "0"
					}]
				}]
			}, {
				xtype: 'container', cls: 'first-container', border: 1,
				layout: 'hbox',
				items: [{
					xtype:  'checkbox',
					name: "index_enabled",
					fieldLabel : lang.__('外枠索引'),
					boxLabel: lang.__("表示する"),
					inputValue: "1"
				}, {
					xtype:  'checkbox',
					name: "printindex",
					boxLabel: lang.__("インデックス地図出力"),
					margin: "0 0 0 10",
					inputValue: "1"
				}]
			}]
		});
		return formPanel;
	},

	createDetailFormPanel: function() {
		var me = this;
		var stmap = me.stmap;
		var formPanel = Ext.create('Ext.form.Panel', {
			frame : false,
			border: false,
			width : 460,
			bodyPadding : 5,

			fieldDefaults : {
				labelAlign : 'left',
				anchor : '100%'
			},

			items : [{
				xtype: 'tabpanel',
				defaults: {
					padding: 5
				},
				items : [{
					// BEGIN 詳細設定:印刷タブ
					id: "print-tab",
					title: lang.__("印刷"),
					items: [{
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'hbox',
						items: [{
							xtype:  'checkbox',
							name: "printmap",
							fieldLabel : lang.__('出力内容'),
							labelWidth: "60px",
							boxLabel: lang.__("マップ"),
							inputValue: "1",
							listeners: {
								change: function ( field, newValue, oldValue, eOpts ) {
									// チェック状態に応じてタブを表示・非表示する
									var printPreviewWindow = me;
									var tabpanel = printPreviewWindow.formPanel.child("tabpanel");
									var maptabpanel = tabpanel.child("#map-tab");
									if(field.checked) maptabpanel.tab.show();
									else maptabpanel.tab.hide();

									// PDF出力ボタンの有効化状態を更新
									me.updateExportButtonEnabled();
								}
							}
						}, {
							xtype:  'checkbox',
							name: "printlegend",
							boxLabel: lang.__("凡例"),
							margin: "0 0 0 10",
							inputValue: "1",
							listeners: {
								change: function ( field, newValue, oldValue, eOpts ) {
									// チェック状態に応じてタブを表示・非表示する
									var printPreviewWindow = me;
									var tabpanel = printPreviewWindow.formPanel.child("tabpanel");
									var legendtabpanel = tabpanel.child("#legend-tab");
									if(field.checked) legendtabpanel.tab.show();
									else legendtabpanel.tab.hide();

									// PDF出力ボタンの有効化状態を更新
									me.updateExportButtonEnabled();
								}
							}
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'fit', // テキストエリア最大化コンテナ
						items: [{
							xtype: 'container', layout: 'fit', // テキストエリア最大化コンテナ
							items: [{
								xtype : 'textareafield',
								name : 'maptitle',
								fieldLabel : lang.__('Map title'),
								labelAlign: "top",
								rows: 2,
								value : "",
								//allowBlank: false // 未入力だと地図タイトルが自動で挿入される（非表示にできない）
								allowBlank: true // 現在は未入力だと非表示にできるのでオプション入力に変更
							}]
						}, {
							xtype: 'container', layout: 'fit', // テキストエリア最大化コンテナ
							items: [{
								xtype : 'textareafield',
								name : 'description',
								fieldLabel : lang.__('Explanation, annotations'),
								labelAlign: "top",
								rows: 3,
								value : ""
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: 'container',
							layout: 'hbox',
							items: [{
								flex: 1,
								xtype:  'combo',
								fieldLabel: lang.__("Paper size"),
								labelWidth: '80px',
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["a0", "A0"],
										["a1", "A1"],
										["a2", "A2"],
										["a3", "A3"],
										["a4", "A4"],
										["a5", "A5"],
										["b0", "B0"],
										["b1", "B1"],
										["b2", "B2"],
										["b3", "B3"],
										["b4", "B4"],
										["b5", "B5"]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "pagesize",
								value: "a4",
								editable: false, // 自由入力不可
								//selectOnFocus: true, // editable:false の combo は selectOnFocus:false であるべき
								validator: function(value) {
									value = this.getRawValue();
									var baseLayerInfo = stmap.map.baseLayer.layerInfo;
									/*
									if(typeof baseLayerInfo!="undefined" && baseLayerInfo.type==SaigaiTask.Map.Layer.Type.WEBTIS) {
										// 地理院タイルの場合はA3サイズまで
										var a3 = pdfControl.getPrintSize("a3");
										var selected = pdfControl.getPrintSize(value);
										if(selected.width <= a3.width && selected.height <= a3.height) {
											return true;
										}
										else return lang.__("Paper size of the Geographical Survey Institute tile is up to A3.");
									}
									*/
									return true;
								}
							}, {
								xtype: 'splitter'
							}, {
								flex: 1,
								xtype : 'radiogroup',
								fieldLabel : lang.__('Paper orientation'),
								labelWidth: '60px',
								columns: 2,
								items : [{
									name : 'rotate',
									inputValue : '1',
									boxLabel : lang.__('Horizontal'),
									checked: true
								}, {
									name : 'rotate',
									inputValue : '0',
									boxLabel : lang.__('Vertical')
								}],
								listeners: {
									change: function(radiogroup, newValue, oldValue, eOpts) {
										me.onchangePaperField();
									}
								}
							}]
						}, {
							xtype: "container",
							layout: "hbox",
							items: [{
								xtype:  'combo',
								fieldLabel : lang.__('マップ出力サイズ'),
								labelWidth: "110px",
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["0", "A0"],
										["1", "A1"],
										["2", "A2"],
										["3", "A3"],
										["4", "A4"],
										["5", "A5"]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "mapsize_a",
								value: "4",
								width: 150,
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype: 'displayfield',
								value: lang.__("（")+lang.__("縦")
							}, {
								xtype:  'combo',
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["1", "1"],
										["2", "2"],
										["3", "3"],
										["4", "4"],
										["5", "5"],
										["6", "6"],
										["7", "7"],
										["8", "8"],
										["10", "10"],
										["12", "12"],
										["14", "14"],
										["16", "16"]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "rows",
								value: "1",
								width: 50,
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype: 'displayfield',
								value: 'x'+lang.__("横")
							}, {
								xtype:  'combo',
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["1", "1"],
										["2", "2"],
										["3", "3"],
										["4", "4"],
										["5", "5"],
										["6", "6"],
										["7", "7"],
										["8", "8"],
										["10", "10"],
										["12", "12"],
										["14", "14"],
										["16", "16"]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "cols",
								value: "1",
								width: 50,
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype: 'displayfield',
								value: lang.__("ページ")+lang.__("）")
							}]
						}]
					}]
				// END   詳細設定:印刷タブ
				// BEGIN 詳細設定:マップタブ
				}, {
					id: "map-tab",
					title: lang.__("マップ"),
					hidden: me.values.printmap=="0",
					items: [{
						xtype: 'container', cls: 'first-container', border: 1,
						layout: "hbox",
						items: [{
							xtype: 'displayfield',
							fieldLabel: lang.__("ページ余白(mm)")
						}, {
							xtype: 'textfield',
							fieldLabel: lang.__("上"),
							labelWidth: "20px",
							width: 60,
							name: "mapmt",
							value: "5"
						}, {
							xtype: 'textfield',
							fieldLabel: lang.__("下"),
							labelWidth: "20px",
							width: 60,
							name: "mapmb",
							value: "5"
						}, {
							xtype: 'textfield',
							fieldLabel: lang.__("左"),
							labelWidth: "20px",
							width: 60,
							name: "mapml",
							value: "5"
						}, {
							xtype: 'textfield',
							fieldLabel: lang.__("右"),
							labelWidth: "20px",
							width: 60,
							name: "mapmr",
							value: "5"
						}, {
							xtype: 'splitter'
						}, {
							xtype: 'checkbox',
							boxLabel : lang.__("余白なし"),
							width: 80,
							name: "noframe",
							inputValue: "1"
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: 'container',
							layout: "hbox",
							items: [{
								xtype:  'combo',
								fieldLabel: lang.__("タイトル配置"),
								labelWidth: "95px",
								width: 180,
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["-1", lang.__("表示しない")],
										["0", lang.__("左寄せ")],
										["1", lang.__("中央")],
										["2", lang.__("右寄せ")]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "titlealign",
								value: "0",
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype: 'splitter'
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("文字サイズ"),
								labelWidth: "75px",
								width: 100,
								name: "titlefontsize",
								value: "32"
							}, {
								xtype: 'displayfield',
								value: "pt"
							}, {
								xtype: 'splitter'
							}, {
								xtype: 'checkbox',
								boxLabel : lang.__("自動"),
								width: 80,
								name: "titlefontauto",
								inputValue: "1"
							}]
						}, {
							xtype: 'container',
							layout: "hbox",
							items: [{
								xtype:  'combo',
								fieldLabel: lang.__("説明・注釈配置"),
								labelWidth: "95px",
								width: 180,
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["-1", lang.__("表示しない")],
										["1", lang.__("左上")],
										["3", lang.__("左下")],
										["2", lang.__("右上")],
										["4", lang.__("右下")]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "descalign",
								value: "2",
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype: 'splitter'
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("文字サイズ"),
								labelWidth: "75px",
								width: 100,
								name: "descfontsize",
								value: "16"
							}, {
								xtype: 'displayfield',
								value: "pt"
							}, {
								xtype: 'splitter'
							}, {
								xtype: 'checkbox',
								boxLabel : lang.__("自動"),
								width: 80,
								name: "descfontauto",
								inputValue: "1"
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: 'container',
							layout: "hbox",
							items: [{
								xtype: 'displayfield',
								fieldLabel: lang.__("登録情報の\n印刷サイズ"),
								labelWidth: "80px"
							}, {
								xtype: 'splitter',
							}, {
								xtype: 'container',
								defaults: { // 子要素のデフォルト
									labelWidth: "95px",
									width: 160
								},
								items: [{
									xtype:  'combo',
									fieldLabel: lang.__("アイコン・文字"),
									store: new Ext.data.SimpleStore({
										fields: ['value', 'display'],
										data: [
											["0.3", lang.__("特小")],
											["0.66", lang.__("小さい")],
											["1.0", lang.__("普通")],
											["1.5", lang.__("1.5倍")],
											["2.0", lang.__("2倍")],
											["3.0", lang.__("3倍")],
											["4.0", lang.__("4倍")],
											["6.0", lang.__("6倍")],
											["8.0", lang.__("8倍")],
											["10.0", lang.__("10倍")]
										],
										autoLoad: false
									}),
									displayField: 'display',
									valueField: 'value',
									name: "iconrate",
									value: "1.0",
									editable: false // 自由入力不可
									//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
								}, {
									xtype:  'combo',
									fieldLabel: lang.__("線の太さ"),
									store: new Ext.data.SimpleStore({
										fields: ['value', 'display'],
										data: [
											["0.66", lang.__("細い")],
											["1.0", lang.__("普通")],
											["1.5", lang.__("1.5倍")],
											["2.0", lang.__("2倍")],
											["3.0", lang.__("3倍")],
											["4.0", lang.__("4倍")],
											["6.0", lang.__("6倍")],
											["8.0", lang.__("8倍")],
											["10.0", lang.__("10倍")]
										],
										autoLoad: false
									}),
									displayField: 'display',
									valueField: 'value',
									name: "linerate",
									value: "1.0",
									editable: false // 自由入力不可
									//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
								}]
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: 'container',
							layout: "hbox",
							items: [{
								xtype:  'combo',
								fieldLabel: lang.__("縮尺位置"),
								labelWidth: "60px",
								width: 145,
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["-1", lang.__("表示しない")],
										["3", lang.__("左下")],
										["4", lang.__("右下")]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "scalealign",
								value: "4",
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype: "splitter"
							}, {
								xtype: 'checkbox',
								fieldLabel : lang.__("複数ページ"),
								labelWidth: "80px",
								boxLabel : lang.__("全ページに表示"),
								name: "scalepages",
								inputValue: "1"
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'fit',
						items: [{
							xtype:  'combo',
							fieldLabel: lang.__("UTM grid"),
							store: new Ext.data.SimpleStore({
								fields: ['value', 'display'],
								data: [
									["-1", lang.__("Hide")],
									["0", lang.__("0 digit: 100km")],
									["1", lang.__("Single digit: 10km")],
									["2", lang.__("The second digit: 1km")],
									["3", lang.__("The third digit: 100m")],
									["4", lang.__("The forth digit: 10m")]
									//"1m(5桁)"
								],
								autoLoad: false
							}),
							displayField: 'display',
							valueField: 'value',
							name: "mgrs",
							editable: false // 自由入力不可
							//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
						}]
					}]
				// END   詳細設定:マップタブ
				// BEGIN 詳細設定:凡例タブ
				}, {
					id: "legend-tab",
					title: lang.__("凡例"),
					hidden: me.values.printlegend=="0",
					items: [{
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: 'container',
							layout: "hbox",
							items: [{
								xtype: 'checkbox',
								fieldLabel : lang.__("列の幅"),
								labelWidth: "50px",
								boxLabel : lang.__("自動"),
								name: "legendcolauto",
								inputValue: "1",
								checked: true
							}, {
								xtype: "splitter"
							}, {
								xtype: "displayfield",
								value: "1/"
							}, {
								xtype:  'combo',
								width: 40,
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["2", "2"],
										["3", "3"],
										["4", "4"],
										["5", "5"],
										["6", "6"],
										["7", "7"],
										["8", "8"],
										["9", "9"],
										["10", "10"]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "legendcols",
								value: "2",
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'hbox',
						items: [{
							xtype: 'displayfield',
							fieldLabel : lang.__('凡例サイズ'),
							labelWidth: "80px"
						}, {
							xtype: 'container',
							items: [{
								xtype:  'combo',
								width: 80,
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["1.8",  lang.__("特大")],
										["1.35", lang.__("大きい")],
										["1.0",  lang.__("普通")],
										["0.8",  lang.__("小さい")],
										["0.5",  lang.__("特小")]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "legendrate",
								value: "1.0",
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype:  'checkbox',
								name: "legenddisplay",
								boxLabel : lang.__("全ての登録情報の凡例を表示する"),
								margin: 0,
								inputValue : 0
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: 'container',
							layout: 'hbox',
							items: [{
								xtype:  'combo',
								fieldLabel : lang.__('凡例表示位置'),
								labelWidth: "90px",
								width: 200,
								store: new Ext.data.SimpleStore({
									fields: ['value', 'display'],
									data: [
										["1",  lang.__("マップ内 左上")],
										["3",  lang.__("マップ内 左下")],
										["2",  lang.__("マップ内 右上")],
										["4",  lang.__("マップ内 右下")],
										["0",  lang.__("別ページ")]
									],
									autoLoad: false
								}),
								displayField: 'display',
								valueField: 'value',
								name: "legendpos",
								value: "3",
								editable: false // 自由入力不可
								//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
							}, {
								xtype:  'splitter',
							}, {
								xtype:  'checkbox',
								name: "legendposchk",
								boxLabel : lang.__("個別に指定"),
								margin: 0,
								inputValue : 0
							}]
						}, {
							xtype: 'container',
							layout: "hbox",
							margin: "5 0 5 0",
							items: [{
								xtype: 'displayfield',
								fieldLabel: lang.__("マップ内余白(mm)"),
								labelWidth: "120px"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("上"),
								labelWidth: "20px",
								width: 60,
								name: "legendmapmt",
								value: "3"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("下"),
								labelWidth: "20px",
								width: 60,
								name: "legendmapmb",
								value: "3"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("左"),
								labelWidth: "20px",
								width: 60,
								name: "legendmapml",
								value: "3"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("右"),
								labelWidth: "20px",
								width: 60,
								name: "legendmapmr",
								value: "3"
							}]
						}, {
							xtype: 'container',
							layout: "hbox",
							margin: "5 0 5 0",
							items: [{
								xtype: 'displayfield',
								fieldLabel: lang.__("別ページ余白(mm)"),
								labelWidth: "120px"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("上"),
								labelWidth: "20px",
								width: 60,
								name: "legendmt",
								value: "10"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("下"),
								labelWidth: "20px",
								width: 60,
								name: "legendmb",
								value: "10"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("左"),
								labelWidth: "20px",
								width: 60,
								name: "legendml",
								value: "10"
							}, {
								xtype: 'textfield',
								fieldLabel: lang.__("右"),
								labelWidth: "20px",
								width: 60,
								name: "legendmr",
								value: "10"
							}, {
								xtype: 'splitter'
							}, {
								xtype: 'checkbox',
								boxLabel : lang.__("余白なし"),
								width: 80,
								name: "legendnoframe",
								inputValue: "1"
							}]
						}]
					}]
				}, {
					id: "headerfooter-tab",
					title: lang.__("ヘッダフッタ"),
					items: [{
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'hbox',
						items: [{
							xtype : 'radiogroup',
							fieldLabel : lang.__('ヘッダ<br>左'),
							labelWidth: '50px',
							columns: 2,
							items : [{
								xtype: 'container',
								layout: 'hbox',
								items: [{
									xtype: 'radio',
									name : 'header_l',
									inputValue : null,
									fieldStyle: {
										"margin-top": "15px"
									},
									boxLabel : lang.__('なし'),
									checked: true
								}, {
									xtype: 'container',
									margin: "0 0 0 10",
									items: [{
										xtype: 'radio',
										margin: 0,
										name : 'header_l',
										inputValue : "title",
										boxLabel : lang.__('タイトル')
									}, {
										xtype: 'container',
										layout: "hbox",
										padding: "0 0 3 0",
										items: [{
											xtype: 'radio',
											margin: 0,
											name : 'header_l',
											inputValue : "text",
											boxLabel : lang.__('文字列')+"&nbsp;"
										}, {
											xtype: 'textfield',
											name: "header_l_text"
										}]
									}]
								}]
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'hbox',
						items: [{
							xtype : 'radiogroup',
							fieldLabel : lang.__('ヘッダ<br>&nbsp;&nbsp;中央'),
							labelWidth: '50px',
							columns: 2,
							items : [{
								xtype: 'container',
								layout: 'hbox',
								items: [{
									xtype: 'radio',
									name : 'header_c',
									inputValue : null,
									fieldStyle: {
										"margin-top": "15px"
									},
									boxLabel : lang.__('なし'),
									checked: true
								}, {
									xtype: 'container',
									margin: "0 0 0 10",
									items: [{
										xtype: 'radio',
										margin: 0,
										name : 'header_c',
										inputValue : "title",
										boxLabel : lang.__('タイトル')
									}, {
										xtype: 'container',
										layout: "hbox",
										padding: "0 0 3 0",
										items: [{
											xtype: 'radio',
											margin: 0,
											name : 'header_c',
											inputValue : "text",
											boxLabel : lang.__('文字列')+"&nbsp;"
										}, {
											xtype: 'textfield',
											name: "header_c_text"
										}]
									}]
								}]
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'hbox',
						items: [{
							xtype : 'radiogroup',
							fieldLabel : lang.__('ヘッダ<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;右'),
							labelWidth: '50px',
							columns: 2,
							items : [{
								xtype: 'container',
								layout: 'hbox',
								items: [{
									xtype: 'radio',
									name : 'header_r',
									inputValue : null,
									fieldStyle: {
										"margin-top": "15px"
									},
									boxLabel : lang.__('なし'),
									checked: true
								}, {
									xtype: 'container',
									margin: "0 0 0 10",
									items: [{
										xtype: 'container',
										layout: "hbox",
										items: [{
											xtype: 'radio',
											margin: 0,
											name : 'header_r',
											inputValue : "date",
											boxLabel : lang.__('印刷日・データ日')
										}, {
											xtype: 'displayfield',
											value: "&nbsp;&nbsp;&nbsp;"
										}, {
											xtype: 'radio',
											margin: 0,
											name : 'header_r',
											inputValue : "time",
											boxLabel : lang.__('印刷日時・データ日時')
										}]
									}, {
										xtype: 'container',
										layout: "hbox",
										padding: "0 0 3 0",
										items: [{
											xtype: 'radio',
											margin: 0,
											name : 'header_r',
											inputValue : "text",
											boxLabel : lang.__('文字列')+"&nbsp;"
										}, {
											xtype: 'textfield',
											name: "header_r_text"
										}]
									}]
								}]
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						layout: 'hbox',
						items: [{
							xtype:  'checkbox',
							name: "fpage",
							fieldLabel : lang.__('フッタ'),
							labelWidth: "60px",
							boxLabel: lang.__("ページ番号"),
							inputValue: "1"
						}, {
							xtype:  'checkbox',
							name: "fmappage",
							boxLabel: lang.__("マップ分割番号"),
							margin: "0 0 0 10",
							inputValue: "1"
						}]
					}]
				}, {
					id: "index-tab",
					title: lang.__("外枠索引"),
					items: [{
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype: "fieldcontainer",
							fieldLabel : lang.__('外枠索引'),
							labelWidth: 60,
							items: [{
								xtype:  'checkbox',
								name: "index_enabled",
								boxLabel: lang.__("表示する"),
								inputValue: "1"
							}, {
								xtype: "container",
								layout: "hbox",
								width: 300,
								items: [{
									xtype:  'combo',
									fieldLabel : lang.__('横'),
									labelWidth: 20,
									width: 150,
									store: new Ext.data.SimpleStore({
										fields: ['value', 'display'],
										data: [
											["1",  lang.__("数字 (1、2、3..)")],
											["2",  lang.__("英字 (A、B、C..)")]
										],
										autoLoad: false
									}),
									displayField: 'display',
									valueField: 'value',
									name: "index_h",
									value: "2",
									editable: false // 自由入力不可
									//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
								}, {
									xtype: "displayfield",
									value: "&nbsp;&nbsp;"
								}, {
									xtype:  'combo',
									width: 40,
									store: new Ext.data.SimpleStore({
										fields: ['value', 'display'],
										data: [
											["1",  "1"],
											["2",  "2"],
											["3",  "3"],
											["4",  "4"],
											["5",  "5"],
											["6",  "6"],
											["7",  "7"],
											["8",  "8"],
											["9",  "9"],
											["10",  "10"]
										],
										autoLoad: false
									}),
									displayField: 'display',
									valueField: 'value',
									name: "index_cols",
									value: "6",
									editable: false // 自由入力不可
									//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
								}, {
									xtype: "displayfield",
									value: lang.__("分割")
								}]
							}, {
								xtype: "container",
								layout: "hbox",
								width: 300,
								items: [{
									xtype:  'combo',
									fieldLabel : lang.__('縦'),
									labelWidth: 20,
									width: 150,
									store: new Ext.data.SimpleStore({
										fields: ['value', 'display'],
										data: [
											["1",  lang.__("数字 (1、2、3..)")],
											["2",  lang.__("英字 (A、B、C..)")]
										],
										autoLoad: false
									}),
									displayField: 'display',
									valueField: 'value',
									name: "index_v",
									value: "1",
									editable: false // 自由入力不可
									//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
								}, {
									xtype: "displayfield",
									value: "&nbsp;&nbsp;"
								}, {
									xtype:  'combo',
									width: 40,
									store: new Ext.data.SimpleStore({
										fields: ['value', 'display'],
										data: [
											["1",  "1"],
											["2",  "2"],
											["3",  "3"],
											["4",  "4"],
											["5",  "5"],
											["6",  "6"],
											["7",  "7"],
											["8",  "8"],
											["9",  "9"],
											["10",  "10"]
										],
										autoLoad: false
									}),
									displayField: 'display',
									valueField: 'value',
									name: "index_cols",
									value: "6",
									editable: false // 自由入力不可
									//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
								}, {
									xtype: "displayfield",
									value: lang.__("分割")
								}]
							}, {
								xtype:  'textfield',
								name: "index_size",
								fieldLabel : lang.__('幅(mm)'),
								labelWidth: 50,
								width: 95,
								value: "5"
							}]
						}]
					}, {
						xtype: 'container', cls: 'first-container', border: 1,
						items: [{
							xtype:  'checkbox',
							name: "printindex",
							fieldLabel : lang.__("インデックス地図"),
							boxLabel: lang.__("出力する(マップ出力時のみ)"),
							inputValue: "1"
						}]
					}]
				}]
			}]
		});
		return formPanel;
	},

	onHideDialog : function()
	{
		var me = this;
		if(me.resetOnHide) {
			//範囲レイヤ非表示
			this.pdfRangeLayer.onHideDialog();
		}
	},

	onchangePaperField: function() {
		var me = this;
		me.values.bbox = null; // 一旦リセット
		me.showPrintRange();
		me.showPreview();
	},

	showPrintRange: function() {
		var me = this;
		var bounds = null;
		var bbox = me.values.bbox;
		if(!!bbox && (bbox=bbox.split(",")).length==4) {
			bounds = new OpenLayers.Bounds(bbox[0], bbox[1], bbox[2], bbox[3]);
		}
		me.pdfRangeLayer.showPrintRange(bounds);
	},

	showPreview: function() {
		var me = this;
		var stmap = me.stmap;
		var pdfControl = me.pdfControl;
		// PDF 生成処理をリクエスト
		var params = pdfControl.createPdfParams();
		params.preview = "1"; // プレビューフラグをたてる
		stmap.api.createPdf(params, function(data) {
			var info = data;
			me._showPreview(info);
		});
	},

	// eコミマップのPdfWriterから移植
	/** <span class="ja">プレビュー表示</span><span class="en">Display preview</span> */
	_showPreview : function(info)
	{
		var me = this;
		var stmap = me.stmap;
		try {
/*
			var preview = dijit.byId('pdf_preview');
			if (!preview) return;

			var w = preview.domNode.offsetWidth;
			var h = preview.domNode.offsetHeight;
			var div = document.createElement('div');
			div.className = "pdf_preview_div";
*/

			var pdfW = info.pageWidth;
			var pdfH = info.pageHeight;

			if (pdfW && pdfH) {
/*
				var pageDiv = document.createElement('div');
				pageDiv.className = "preview_page";
				var pageX = 0;
				var pageY = 0;
				var pageW = w-4;
				var pageH = h-4;
				if (pdfW/pdfH > w/h) {
					pageH = pageW/pdfW * pdfH;
					pageY = (h-pageH)/2;
				} else {
					pageW = pageH/pdfH * pdfW;
					pageX = (w-pageW)/2;
				}
				this._setStyleRect(pageDiv.style, pageX, pageY, pageW, pageH);
				div.appendChild(pageDiv);
*/
				//<span class="ja">地図画像</span>
				//<span class="en">Map image</span>
				if (info.map) {
/*
					var mapDiv = this._createPreviewDiv(lang._('Map '), "preview_map", info.map, pdfW, pdfH, pageX, pageY, pageW, pageH);
					div.appendChild(mapDiv);
*/
					//<span class="ja">地図画像URLを設定</span>
					//<span class="en">Setting for map image URL</span>
					//var wmsUrl = this.eMap.getContentsWmsURL();

					var wmsUrl = null;
					var layerIds = stmap.getVisibleLayerIds();
					if(layerIds.length) {
						wmsUrl  = SaigaiTask.contextPath+"/page/map/wmsAuth/?SERVICE=WMS";
						var mapInfo = map.ecommaps[0].mapInfo;
						wmsUrl += "&mid="+mapInfo.mapId+"&cid="+mapInfo.communityId;
						wmsUrl += "&LAYERS="+stmap.getVisibleLayerIds().reverse().join(",");
					}
					if (wmsUrl) {
						var previewURL = wmsUrl+"&REQUEST=GetMap&FORMAT=image/png";
						previewURL += "&BBOX="+info.bbox.join(',');
						// FIXME: preview WIDTH/HEIGHT/pagerate/iconrate/linerate
						/*+"&WIDTH="+Math.floor(info.map[2]/pdfW*pageW)+"&HEIGHT="+Math.floor(info.map[3]/pdfH*pageH)
							+"&pagerate="+(pdfW/pageW)+"&iconrate="+(pageW/pdfW*info.iconRate)+"&linerate="+(pageW/pdfW*info.lineRate)
							*/
						previewURL += "&WIDTH=256&HEIGHT=256";
						previewURL += "&SRS=EPSG:"+stmap.epsg + "&epsg="+stmap.epsg;
						previewURL += "&"+new Date().getTime();

						var previewMapImageContainer = me.previewMapImageContainer;

						// 最大幅は画像コンテナの幅
						var maxw = previewMapImageContainer.width;
						// 最大高さはhboxコンテナの高さ
						var maxh = maxw;

						var extImgObj = stmap.createImg(previewURL, lang.__("印刷プレビュー（登録情報のみ）"), maxw, maxh);
						previewMapImageContainer.removeAll();
						previewMapImageContainer.add(extImgObj);

						// プレビュー画像の枠をセット
						if(!!extImgObj) extImgObj.getEl().setStyle("border", "1px solid lightgray");
					}
				}

				//index
				if (info.index_h) div.appendChild(this._createPreviewDiv(lang._('Index'), "preview_index_h", info.index_h, pdfW, pdfH, pageX, pageY, pageW, pageH));
				if (info.index_v) div.appendChild(this._createPreviewDiv(lang._('Index'), "preview_index_v", info.index_v, pdfW, pdfH, pageX, pageY, pageW, pageH));

				//<span class="ja">枠</span>
				//<span class="en">Frame</span>
				div.appendChild(this._createPreviewDiv(lang._('Map Frame'), "preview_frame", info.frame, pdfW, pdfH, pageX, pageY, pageW, pageH));

				//TODO marker

				//title
				if (info.title) div.appendChild(this._createPreviewDiv(lang._('Map Title'), "preview_title", info.title, pdfW, pdfH, pageX, pageY, pageW, pageH));
				//legend
				if (info.legend) div.appendChild(this._createPreviewDiv(lang._('Legend<!--2-->'), "preview_legend", info.legend, pdfW, pdfH, pageX, pageY, pageW, pageH));
				//scale
				if (info.scale) div.appendChild(this._createPreviewDiv(lang._('Scale'), "preview_scale", info.scale, pdfW, pdfH, pageX, pageY, pageW, pageH));
				//description
				if (info.description) div.appendChild(this._createPreviewDiv(lang._('Explanation and Comment'), "preview_description", info.description, pdfW, pdfH, pageX, pageY, pageW, pageH));
				//attribution
				if (info.attribution) div.appendChild(this._createPreviewDiv(lang._('Author Info'), "preview_attribution", info.attribution, pdfW, pdfH, pageX, pageY, pageW, pageH));
				//feature
				if (info.feature) div.appendChild(this._createPreviewDiv(lang._('Spot Description'), "preview_feature", info.feature, pdfW, pdfH, pageX, pageY, pageW, pageH));

				//TODO header
				//TODO footer
			}
			preview.set('content',div);

		} catch (e) { console.error(e); }
	},

	getValues: function() {
		var me = this;

		// 簡単設定のformPanelには一部の設定値しかないので、
		// me.values にすべての設定値を保持するようにする
		var values = me.formPanel.getValues();
		{
			var fields = me.formPanel.form.getFields().items;
			for(var idx in fields) {
				var field = fields[idx];
				if(field.xtype=="radiofield") {
					// radioの場合、チェック中の値だけ取得する
					if(field.checked) {
						var name = field.name;
						var val = field.inputValue;
						values[name] = val;
					}
				}
				else if(field.xtype=="checkbox") {
					// checkboxの場合、チェック中の値だけ取得する
					var name = field.name;
					var val = field.inputValue;
					if(field.checked) {
						values[name] = val;
					}
					else {
						values[name] = 0;
						//delete me.values[name];
					}
				}
			}

		}
		Ext.apply(me.values, values);

		//<span class="ja">範囲設定</span>
		//<span class="en">Setting the range</span>
		if(me.pdfRangeLayer!=null) {
			me.values.bbox = me.pdfRangeLayer.getBBOX();
		}

		// コピーを返却
		return Ext.apply({}, me.values);
	},

	isExportable: function() {
		var me = this;
		var values = me.getValues();
		return ! (values.printmap==0 && values.printlegend==0);
	},
	
	updateExportButtonEnabled : function() {
		var me = this;
		// 出力内容でマップと凡例のどちらも出力しない場合はエクスポート不可
		if(me.isExportable()) {
			me.exportButton.setTooltip("");
			me.exportButton.enable();
		}
		else {
			me.exportButton.setTooltip(lang.__("出力内容を１つ以上選択してください。"));
			me.exportButton.disable();
		}
	},

	/**
	 * 印刷実行時の処理
	 */
	onexport: function() {
		var me = this;
		me.progressbar.show();
		me.downloadButton.hide();
		//me.formPanel.hide(); // 再表示するとレイアウトが崩れる？
		me.exportButton.hide();
		me.exportImageButton.hide();

		me.pdfControl.print();

		// パラメータを取得してからformを無効にする
		//me.formPanel.disable();
		me.topToolbar.disable();
		me.contentsContainer.disable();
	},

	/**
	 * 印刷成功時の処理
	 */
	onsuccess: function() {
		var me = this;
		me.progressbar.reset(true);
		me.progressbar.text = me.progressbarInitText;
		me.progressbar.hide();
		me.downloadButton.show();
		//me.formPanel.enable();
		//me.formPanel.show();
		me.topToolbar.enable();
		me.contentsContainer.enable();
		me.exportButton.show();
		me.exportImageButton.show();
	}
};