/**
 * @require SaigaiTask/Map/view.js
 * @returns {SaigaiTask.Map.view.CotnentsFileFormPanel}
 */
SaigaiTask.Map.view.ContentsFileFormPanel = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.ContentsFileFormPanel.prototype = {

	/**
	 * 地図
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * @type {Ext.Form.Panel}
	 */
	formPanel: null,

	/**
	 * 画像一覧ビュー
	 * @type {Ext.view.View}
	 */
	imageView: null,

	/** ファイル情報ストア */
	store: null,

	/**
	 * 地図ID
	 * @type {Number}
	 */
	mid: null,

	/**
	 * レイヤID
	 * @type {String}
	 */
	layerId: null,

	/**
	 * フィーチャID
	 * @type {Number}
	 */
	fid: null,

	/**
	 * 登録情報登録ウィンドウを表示します.
	 * @param content 登録情報
	 * @param options.mid 地図ID
	 * @param options.layerId レイヤID
	 * @param options.fid フィーチャID
	 */
	initialize: function(stmap, content, options) {

		var me = this;
		me.stmap = stmap;
		me.mid = options.mid;
		me.layerId = options.layerId;
		me.fid = options.fid;
		if(options.editable == 'undefined') options.editable = true;

		var ecommapURL = me.stmap.ecommaps[0].ecommapURL;

		// ファイル情報データモデルを定義
		ImageModel = Ext.define('ImageModel', {
			extend: 'Ext.data.Model',
			fields: [
			   {name: 'title'},
			   {name: 'url'},
			   {name: 'thumbnail'}
			]
		});

		// ファイル情報からデータを生成
		var data = [];
		if(content!=null) {
			var files = content.files;
			if(files && 0<files.length ) {
				var filesIdx = 0;
				while(filesIdx<files.length) {
					// 画像URLのサーバ名を動的に取得する
					var server = stmap.ecommaps[0].ecommapURL+'/..';

					// 画像URL
					var fileURL = files[filesIdx];
					if(fileURL.indexOf('http')==-1) fileURL = server + fileURL;
					var fileTitle = files[filesIdx+1];

					// サムネイル画像
					var url = fileURL;
					var ext = FalUtil.getFileExt(url);
					var thumbnail = url;
					// 画像以外のファイルの場合はアイコン表示
					if (! ext.match(/jpg|gif|png|jpeg|file/)) {
						thumbnail = ecommapURL+"/map/fileicons/"+ext+".png";
					}

					data.push({
						title: fileTitle,
						url: fileURL,
						thumbnail: thumbnail
					})
					filesIdx += 2;
				}
			}
		}

		// データストアを生成
		var store = me.store = Ext.create('Ext.data.Store', {
			model: 'ImageModel',
			data: data
		});

		// 画像一覧ビュー
		var imageView = me.imageView = Ext.create('Ext.view.View', {
			store: store,
			tpl: [
				'<tpl for=".">',
				'<div class="thumb-wrap" id="{title:stripTags}" style="'+(options.editable?"":"cursor:pointer;")+'">',
				//'<div class="thumb"><img src="{thumbnail}" title="{title:htmlEncode}"></div>',
				// 幅は固定で86px。1920x1080の16:9の比率の場合、最適な高さは48.375pxとなるので49pxとする。つまり画像サイズは86x49。 
				'<div class="thumb" style="height:49px; background-image: url(\'{thumbnail}\'); background-size:contain; background-repeat:no-repeat;background-position:center center;"></div>',
				'<span class="x- editable">{shortName:htmlEncode}</span>',
				'</div>',
				'</tpl>',
				'<div class="x-clear"></div>'
			],
			multiSelect: false,
			//maxHeight: 310,
			trackOver: true,
			overItemCls: 'x-item-over',
			itemSelector: 'div.thumb-wrap',
			emptyText: lang.__('The file is not registered.'),
			plugins: [
				Ext.create('Ext.ux.DataView.DragSelector', {}),
				Ext.create('Ext.ux.DataView.LabelEditor', {dataIndex: 'title'})
			],
			prepareData: function(data) {
				Ext.apply(data, {
					shortName: Ext.util.Format.ellipsis(data.title, 15)
				});
				return data;
			},
			listeners: {
				selectionchange: function(dv, nodes ){
					//FalUtil.showImageWindow(dv.lastSelected.data.url);
				},
				itemclick: function( dv, record, item, index, e, eOpts ) {
					// 閲覧時はクリックして 拡大表示 or ダウンロード
					if(!options.editable) {
						var url = record.data.url;
						var ext = FalUtil.getFileExt(url);
						// 画像の場合はプレビュー表示
						if (ext.match(/jpg|gif|png|jpeg|file/)) {
							FalUtil.showImageWindow(url);
						} else {
							//img.src = "/map/map/fileicons/"+ext+".png";
							FalUtil.downloadFile({
								url: url
							});
						}
					}
				}
			},
			fbar: [{
				xtype: "button",
				text: lang.__("Add<!--2-->")
			}]
		});

		// 添付ファイルフォームを生成
		var formPanel = me.formPanel = Ext.create("Ext.form.Panel", {
			//xtype: "panel",
			// id指定すると、同時に２以上表示したときにデータが新しい方に移ってしまうので、cls指定とする
			//id: 'images-view',
			cls: 'images-view',
			autoScroll: true,
			frame: false,
			collapsible: true,
			header: false,
			tbar: !options.editable ? null : {
				//style: "border-Color: transparent; background-Color: transparent; background-Image: none;",
				items: [{
					xtype: "button",
					text: lang.__("Add<!--2-->"),
					icon: stmap.icon.getURL("addIconURL"),
					handler: function() {
						me.showAddFileWindow();
					}
				}, {
					xtype: "button",
					text: lang.__("Delete"),
					icon: stmap.icon.getURL("deleteIconURL"),
					handler: function() {
						var selection = imageView.getSelectionModel().getSelection();
						if (0<selection.length) {

							store.remove(selection);
						}
						else alert(lang.__("Click after select file that you want to delete."));
					}
				}]
			},
			items: imageView
		});

	},

	showAddFileWindow: function() {
		var me = contentsFileFormPanel = this;
		var stmap = me.stmap;
		// 登録情報の添付ファイル登録フォームを表示
		// TODO: ファイルアップロードフォームとURL入力フォームをユーザが選べるようにする。
		// 以下のURL入力フォームは動作確認済みであとはタブかなにかで選べるようにするだけ。。。
		// URL入力フォームを生成
		var formPanel = Ext.create("Ext.form.Panel", {
			autoScroll: true,
			border: false,
			fieldDefaults: {
				labelWidth: 100,
				anchor: '100%'
			},
			defaultType: 'textfield',
			bodyPadding: 5,
			items: [{
				xtype: "textfield",
				fieldLabel: lang.__("Title<!--2-->"),
				name: "title",
				allowBlank: false
			}, {
				xtype: "textfield",
				fieldLabel: "URL",
				name: "url",
				vtype: 'url',
				allowBlank: false
//			}, {
//				xtype: "filefield",
//				name: "file",
//				fieldLabel: "ファイル",
//				msgTarget: "side",
//				buttonText: "ファイルを選択"
			}],
			/**
			 * データストアに入力値を追加します.
			 */
			appendData: function() {
				var formValues = formPanel.form.getValues();
				var title = formValues["title"];
				var url = formValues["url"];
				me.store.loadRawData([{
					title: title,
					url: url
				}], true);
			}
		});
		// ファイルアップロードフォームを生成
		formPanel = Ext.create("Ext.form.Panel", {
			fileUpload: true,
			autoScroll: true,
			border: false,
			fieldDefaults: {
				labelWidth: 100,
				anchor: '100%'
			},
			defaultType: 'textfield',
			bodyPadding: 5,
			items: [{
				xtype: "hidden",
				name: "mid",
				value: me.mid
			}, {
				xtype: "hidden",
				name: "layer",
				value: me.layerId
			}, /*{
				xtype: "hidden",
				name: "fid",
				value: me.fid
			},*/ {
				xtype: "textfield",
				fieldLabel: lang.__("Title<!--2-->"),
				name: "files[0].title"
				//allowBlank: false
//			}, {
//				xtype: "textfield",
//				fieldLabel: "URL",
//				name: "url",
//				vtype: 'url'
			}, {
				xtype: "filefield",
				name: "files[0].formFile",
				fieldLabel: lang.__("File"),
				msgTarget: "side",
				buttonText: lang.__("Choose file"),
				allowBlank: false
			}],
			/**
			 * ファイルアップロードを実行します.
			 */
			upload: function() {
				var me = this;
				var ecommapURL = stmap.ecommaps[0].ecommapURL;
				var form = me.getForm();
				if(form.isValid()) {
					form.submit({
						url: SaigaiTask.contextPath+"/page/map/ecommap/contents/uploadFile/",
						params: '&_csrf='+SaigaiTask.ajaxcsrfToken,
						waitMsg: lang.__("Uploading file.."),
						success: function(form, action) {
							var fileList = action.result.fileList;
							if(0<fileList.length) {
								for(var key in fileList) {
									var values = fileList[key];
									if(values.length==2) {
										var fileUrl = values[0];
										var fileTitle = values[1];
										var ext = FalUtil.getFileExt(fileUrl);
										var thumbnail = fileUrl;
										// 画像以外のファイルの場合はアイコン表示
										if (! ext.match(/jpg|gif|png|jpeg|file/)) {
											thumbnail = ecommapURL+"/map/fileicons/"+ext+".png";
										}
										contentsFileFormPanel.store.loadRawData([{
											title: fileTitle,
											url: fileUrl,
											thumbnail: thumbnail
										}], true);
									}
								}
							}
							win.close();
						},
						failure: function(form, action) {
							win.close();

							var msg = lang.__("Failed to upload file.");
							try{
								var result = Ext.decode(action.response.responseText);
								if(typeof result.msg!="undefined") {
									msg += "</br>" + result.msg;
								}
							} catch(e) {
								// do nothing
							}
							var msgwin = Ext.Msg.show({
								title: lang.__("Error"),
								msg: msg,
								buttons: Ext.MessageBox.OK,
								icon: Ext.MessageBox.ERROR
							});
						}
					})
				}
			}
		});
		// フォームのウィンドウを生成
		var win = null;
		win = me.window = Ext.create('Ext.window.Window', {
			title: lang.__('Attachment registration form'),
			width: 500,
			maxWidth: document.body.clientWidth,
			maxHeight: document.body.clientHeight,
			collapsible: false,
			modal: true,
			layout: 'fit',
			items: formPanel,
			buttons: [{
				text: 'OK',
				handler: function() {
					formPanel.upload();
					//win.close();
				}
			}, {
				text: lang.__('Cancel'),
				handler: function(){ win.close(); }
			}]
		});
		win.show();
	},

	/**
	 * ファイルの登録数に応じて、
	 * 1件もない場合は、
	 * ・追加ボタンを押してから、画像パネルを出す
	 * 登録があれば、
	 * ・最初から画像パネルを出す
	 * ような、アイテムを返す
	 */
	getFieldSetItems: function() {
		var me = this;
		var items = null;
		if(me.store.getCount()==0) {
			items = [{
				xtype: "button",
				text: lang.__("Add<!--2-->"),
				handler: function(button, e) {
					// 存在するかチェックする
					//button.ownerCt.getComponent("images-view");
					// 添付ファイル登録パネルを追加
					button.ownerCt.add(me.formPanel);
					// 追加ボタンを削除
					button.ownerCt.remove(button);
					// 登録ウィンドウを表示
					me.showAddFileWindow();
				}
			}];
		}
		else {
			items = me.formPanel;
		}
		return items;
	},

	/**
	 * ファイルパラメータをJSON配列で取得します.
	 * @type {JSONArray}
	 */
	getJSONArray: function() {
		var me = this;
		var array = [];
		var data = me.store.data;
		for(var key in data.items) {
			var item = data.items[key];
			array.push([item.data.url, item.data.title]);
		}
		return array;
	}
};
