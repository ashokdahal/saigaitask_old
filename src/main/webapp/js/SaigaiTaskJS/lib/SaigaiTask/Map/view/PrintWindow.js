/**
 * @class SaigaiTask.Map.view.PrintWindow
 * 印刷ダイアログです.
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.PrintWindow = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.PrintWindow.prototype = {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	pdfControl: null,

	formPanel: null,

	progressbar: null,
	progressbarInitText: lang.__('Creating PDF file..'),

	exportButton: null,
	downloadButton: null,

	win: null,

	initialize: function(pdfControl) {
		var me = this;
		me.pdfControl = pdfControl;
		var stmap = me.stmap = pdfControl.stmap;

		// 印刷設定フォーム
		var formPanel = Ext.create('Ext.form.Panel', {
			frame : false,
			border: false,
			width : 340,
			bodyPadding : 5,

			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},

			items : [{
				xtype : 'textfield',
				name : 'maptitle',
				fieldLabel : lang.__('Map title'),
				value : "",
				//allowBlank: false // 未入力だと地図タイトルが自動で挿入される（非表示にできない）
				allowBlank: true // 現在は未入力だと非表示にできるのでオプション入力に変更
			}, {
				xtype : 'textareafield',
				name : 'description',
				fieldLabel : lang.__('Explanation, annotations'),
				value : ""
			}, {
				xtype:  'combo',
				fieldLabel: lang.__("Paper size"),
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
				xtype : 'checkboxgroup',
				fieldLabel : lang.__('Paper orientation'),
				cls : 'x-check-group-alt',
				columns: 2,
				items : [{
					xtype : 'radiofield',
					name : 'rotate',
					inputValue : '1',
					boxLabel : lang.__('Horizontal'),
					checked: true
				}, {
					xtype : 'radiofield',
					name : 'rotate',
					inputValue : '0',
					boxLabel : lang.__('Vertical')
				}]
			}, {
				xtype : 'checkboxgroup',
				fieldLabel : lang.__('Base map'),
				cls : 'x-check-group-alt',
				columns: 2,
				items : [{
					xtype : 'radiofield',
					name : 'printBaselayer',
					inputValue : '1',
					boxLabel : lang.__('Existing'),
					checked: true
				}, {
					xtype : 'radiofield',
					name : 'printBaselayer',
					inputValue : '0',
					boxLabel : lang.__('None<!--2-->')
				}]
			}, {
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
				value: "-1",
				editable: false, // 自由入力不可
				//selectOnFocus: true // editable:false の combo は selectOnFocus:false であるべき
			}]
		});
		//formPanel.render('form-ct'); // for debug
		me.formPanel = formPanel;

		// プログレスバー
		var progressbar =  Ext.create('Ext.ProgressBar', {
			hidden: true,
			width : 340,
			text : me.progressbarInitText
		});
		me.progressbar = progressbar;

		// PDF出力ボタン
		var exportButton = Ext.create("Ext.Button", {
			text: lang.__("PDF output"),
			icon: stmap.icon.getURL("printIconURL"),
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

		// ダウンロードボタン
		var downloadButton = Ext.create("Ext.Button", {
			text: lang.__("Download"),
			href: SaigaiTask.contextPath+"/PdfServlet?download",
			hidden: true
		});
		me.downloadButton = downloadButton;

		// 印刷ダイアログ
		var win = Ext.create("Ext.Window", {
			closeAction: "hide",
			title: lang.__("Print dialog"),
			items: [formPanel, progressbar],
			fbar: [exportButton, downloadButton],
			listeners: {
				show: function(window, eOpts) {
					var value = ""+(stmap.controls.mgrsControl.layer.getVisibility() ? stmap.controls.mgrsControl.layerInfo.params.precision : "-1");
					var mgrsField = formPanel.form.findField("mgrs");
					mgrsField.setValue(value);
				}
			}
		});
		me.win = win;
	},

	getValues: function() {
		var me = this;
		return me.formPanel.getValues();
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

		me.pdfControl.print();

		// パラメータを取得してからformを無効にする
		me.formPanel.disable();
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
		me.formPanel.enable();
		me.formPanel.show();
		me.exportButton.show();
	}
};