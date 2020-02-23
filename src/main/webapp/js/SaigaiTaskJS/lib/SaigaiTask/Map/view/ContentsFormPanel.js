/**
 * @require SaigaiTask/Map/view.js
 * @returns {SaigaiTask.Map.view.CotnentsFormPanel}
 */
SaigaiTask.Map.view.ContentsFormPanel = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.ContentsFormPanel.prototype = {

	/**
	 * 地図
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * サブミット時に付加するパラメータ
	 * @type {Object}
	 */
	params: null,

	/**
	 * 属性情報JSON
	 * @type {Object}
	 */
	json: null,

	/**
	 * レイヤ情報
	 */
	layerInfo: null,

	/**
	 * 描画フィーチャ
	 */
	feature: null,

	/**
	 * 属性フォームパネル
	 * @type {Ext.Form.Panel}
	 */
	formPanel: null,

	/** 更新フラグ(登録時はfalse) */
	update: false,

	/**
	 * 登録情報ファイルフォームパネル
	 * @type {SaigaiTask.Map.view.ContentsFileFormPanel}
	 */
	contentsFileFormPanel: null,

	/**
	 * 登録情報登録ウィンドウを表示します.
	 * @param layerId レイヤID
	 * @param feature 描画フィーチャ(ジオメトリ)
	 * @param fid 描画フィーチャ
	 * @param content 登録情報
	 */
	initialize: function(stmap, layerId, feature, fid, content) {

		var me = this;
		me.stmap = stmap;
		me.feature = feature;

		// 属性情報を取得します.
		var json = me.json = stmap.api.getAttrInfos(layerId);
		var attrInfos = json.attrInfos;

		// 登録情報から属性値リストを作成
		var attrValues = {};
		if(content!=null) {
			var attrs = content.attrs;
			for(var idx in attrs) {
				var attr = attrs[idx];
				attrValues[attr.attrId] = attr.attrValue;
			}
		}

		// フォームのアイテム
		var items = [];

		// サブミット時に付けるパラメータ
		var params = me.params = {};

		// レイヤ情報
		var layerInfo = me.layerInfo = stmap.getLayerInfo(layerId);
		params.layer = layerInfo.layerId;

		// 地図情報
		var mid = layerInfo.ecommap.mapInfo.mapId;
		params.mid = mid;

		var update = false; // true: 更新, false: 登録
		// 更新時の場合はフィーチャIDを指定
		if(typeof fid!="undefined" && fid!=null) {
			params.fid = fid;
			update = true;
		}
		me.update = update;

		if(SaigaiTask.PageURL.params.drawGeometry != true){
			// 属性フォームを生成
			if(attrInfos) {
				var attrItems = [];
				var attrIds = params.attrIds = [];
				var attrInfoMap = {};
				for(var key in attrInfos) {
					var attrInfo = attrInfos[key];
					var attrId = attrInfo.attrId;
					attrInfoMap[attrId] = attrInfo;

					// assert
					if(!attrId) continue;

					var layerAttrInfo = layerInfo.getAttrInfo(attrId);

					var visible = layerInfo.isVisibleAttr(attrId);
					var editable = layerInfo.isEditableAttr(attrId);
					var highlight = layerAttrInfo!=null ? layerAttrInfo.highlight : false;
					if(update==false) {
//						// 登録処理の場合は必須項目は閲覧・編集権限に関係なく入力させる
//						if(attrInfo.nullable==false) {
//						visible = editable = true;
//						}
						// 登録処理の場合はすべて入力可。
						editable = true;
						// 登録処理の場合は強調表示しない
						highlight = false;
					}
					// TODO: 閲覧不可の場合は折り畳みの状態で出す？

					if(visible) {
						// 属性値を取得
						if(typeof attrValues!="undefined" && attrValues!=null) {
							var attrValue = attrValues[attrId];
							if(typeof attrValue!="undefined") {
								attrInfo.value = attrValue;
							}
						}

						// 属性名に別名があれば設定する
						if(layerAttrInfo!=null && !!layerAttrInfo.name) attrInfo.name = layerAttrInfo.name;
						// 住所ボタンフラグがあれば設定する
						if(layerAttrInfo!=null && !!layerAttrInfo.addAddressButton) attrInfo.addAddressButton = true;
						// 属性アイテムを追加
						var item = me.createFormItem(attrInfo, editable, highlight);
						attrItems.push(item);

						// 登録・更新対象の属性IDを保存（編集可のみ）
						if(editable) {
							attrIds.push(attrId);
						}
					}
				}
				// disporder順でソート
				attrItems.sort(function(attrItem1, attrItem2) {
					var attrId1 = attrItem1.name;
					var attrId2 = attrItem2.name;
					return layerInfo.evalAttrSort(attrId1, attrId2);
				});
//				// 登録処理の場合は必須を上に持ってくる
//				if(update==false) {
//				attrItems.sort(function(attrItem1, attrItem2) {
//				var attrId1 = attrItem1.name;
//				var attrId2 = attrItem2.name;
//				var attrInfo1 = attrInfoMap[attrId1];
//				var attrInfo2 = attrInfoMap[attrId2];
//				if(attrInfo1.nullable==attrInfo2.nullable) return 0;
//				else if(attrInfo1.nullable==false) return -1;
//				else return 1;
//				});
//				}
				if(0<attrItems.length) {
					// 属性入力フィールドセットを追加
					items.push({
						xtype: 'fieldset',
						title: lang.__('Attribute'),
						layout: 'anchor',
						defaults: {
							anchor: '100%'
						},
						collapsible: true,
						items: attrItems
					});
				}
			}

			// 添付ファイルフォームを生成
			var contentsFileFormPanel = me.contentsFileFormPanel = new SaigaiTask.Map.view.ContentsFileFormPanel(stmap, content, {
				mid: mid,
				layerId: layerId,
				fid: fid,
				editable: true
			});

			// 添付ファイルフォームを追加
			items.push({
				xtype: 'fieldset',
				title: lang.__('Attachment<!--2-->'),
				layout: 'anchor',
				defaults: {
					anchor: '100%'
				},
				collapsible: true,
				items: contentsFileFormPanel.getFieldSetItems()
			});
		}

		// フォームを定義
		Ext.define('Fal.form.Panel', {
			extend: 'Ext.form.Panel',
			/** 必須のフィールドに赤色のアスタリスクを付けるフラグ */
			markupRequired: false,
			initComponent: function() {
				// 必須のフィールドに赤色のアスタリスクを付ける
				this.on('beforeadd', function(me, field){
					var fields = [];
					if(field.xtype=='fieldset'){
						fields = field.items.items;
					}
					else {
						fields.push(field);
					}

					for(var key in fields) {
						var f = fields[key];
						if (!f.allowBlank&&me.markupRequired) {
							f.labelSeparator += '<span style="color: rgb(255, 0, 0); padding-left: 2px;">*</span>';
						}
					}
				});
				this.callParent(arguments);
			}
		});

		// フォームを生成
		var formPanel = this.formPanel = Ext.create('Fal.form.Panel', {
			autoScroll: true,
			title: attrInfos.layerName,
			border: false,
			fieldDefaults: {
				labelWidth: 100,
				anchor: '100%'
			},
			url: update ? stmap.api.url.contentsUpdateURL : stmap.api.url.contentsCreateURL, //stmap.saigaitaskServer+'/contents/create/',
			markupRequired: true,
			defaultType: 'textfield',
			bodyPadding: 5,
			items: items
		});
	},

	/**
	 * 属性情報から入力フォームを生成します.
	 * @param attrInfo 属性情報
	 * @param editable 編集フラグ
	 * @param highlight 強調表示フラグ
	 * @returns {Object} 入力フォームアイテム情報
	 */
	createFormItem: function(attrInfo, editable, highlight) {
		var me = this;

		// 属性の基本情報の設定
		var value = null;
		if(typeof attrInfo.value!="undefined") {
			value = attrInfo.value;
		}
		var item = {
			fieldLabel: attrInfo.name,
			name: attrInfo.attrId,
			value: value,
			allowBlank: attrInfo.nullable
		};
		// ハイライト
		if(typeof highlight=="undefined") {
			highlight = false;
		}
		if(highlight) {
			jQuery.extend(item, {
				border: 2,
				style: {
					borderColor: 'red',
					borderStyle: 'solid'
				}
			});
		}
		// 編集不可は表示のみ
		if(editable==false) {
			item.xtype = 'displayfield';
			return item;
		}
		// 属性の型の設定
		switch(attrInfo.dataTypeId) {
		// 文字
		case 'text':
			var fieldClass = attrInfo.length<=20 ? "Ext.form.field.Text" : "Ext.form.field.TextArea";

			// フィールドコンテナを定義
			item.xtype = 'fieldcontainer';
			item.combineErrors = true;
			item.msgTarget = 'side';
			item.layout = {
				type: "hbox",
				align: "stretch"
			};
			item.defaults = {
				hideLabel: true
			};
			item.items = [];

			// フィールドを追加
			var textField = Ext.create(fieldClass, {
				name: item.name,
				value: item.value,
				allowBlank: item.allowBlank,
				flex: 1
			});
			item.items.push(textField);

			// 住所ボタンを追加
			if(attrInfo.addAddressButton) {
				var addressButton = Ext.create("Ext.button.Button", {
					xtype: "button",
					width: 40,
					text: lang.__("Address"),
					handler: (function(textField) {
						return function() {
							me.formPanel.fireEvent("clickaddressbtn", {
								addressBtn: this,
								textField: textField
							});
						};
					})(textField)
				});
				item.items.push(addressButton);
			}
			break;
		// 数値（整数）
		case 'integer':
			item.xtype = 'numberfield';
			item.allowDecimals = false;
			break;
		// 数値
		case 'float':
			item.xtype = 'numberfield';
			break;
		// 日付のみ
		case 'date':
			item.xtype = 'datefield';
			item.format = 'Y/m/d';
			//item.emptyText = 'yyyy/mm/dd';
			item.selectOnFocus = true;
			//item.editable = false;
			break;
		// 時間のみ
		case 'time':
			item.xtype = 'timefield';
			item.format = item.submitFormat = 'H:i:s';
			item.selectOnFocus = true;
			//item.editable = false;
			item.increment = 10;
			break;
		// 日時
		case 'datetime':
			item.xtype = 'fieldcontainer';
			item.combineErrors = true;
			item.msgTarget = 'side';
			item.layout = 'fit';
			item.defaults = {
				flex: 1,
				hideLabel: true
			};
			item.items = [];
			var dateValue = null;
			var timeValue = null;
			if(attrInfo.value!=null) {
				var datetime = attrInfo.value.split(" ");
				for(var idx in datetime) {
					var val = datetime[idx];
					// 日付チェック
					if(val.match(/^\d{4}\/\d{2}\/\d{2}$/)) {
						dateValue = val;
					}
					// 時刻チェック
					if(val.match(/^\d{2}:\d{2}:\d{2}$/)) {
						timeValue = val;
					}
				}
			}
			// 日付
			var dateAttrInfo = Ext.clone(attrInfo);
			dateAttrInfo.dataTypeId = "date";
			dateAttrInfo.value = dateValue;
			var dateItem = this.createFormItem(dateAttrInfo, editable, false);
			dateItem.border = 0; // ハイライトのキャンセル
			item.items.push(dateItem);
			// 時間
			var timeAttrInfo = Ext.clone(attrInfo);
			timeAttrInfo.dataTypeId = "time";
			timeAttrInfo.value = timeValue;
			var timeItem = this.createFormItem(timeAttrInfo, editable, false);
			timeItem.border = 0; // ハイライトのキャンセル
			item.items.push(timeItem);
			break;
		case 'select':
			item.xtype = 'combo';
			var data = [];
			// JSONArray 文字列かどうか
			var jsonDataExps = null;
			try {jsonDataExps=jQuery.parseJSON(attrInfo.dataExp)} catch(e){/* do nothing */}
			if(jsonDataExps!=null) {
				for(var jsonDataExpsIdx in jsonDataExps) {
					var jsonDataExp = jsonDataExps[jsonDataExpsIdx];
					for(var key in jsonDataExp) {
						var val = jsonDataExp[key];
						data.push([key, val]);
					}
				}
			}
			else {
				// カンマ区切り
				var dataExps = attrInfo.dataExp.split(',');
				for(var dataExpsKey in dataExps) {
					var dataExp = dataExps[dataExpsKey];
					data.push([dataExp, dataExp]);
				}
			}
			if(item.allowBlank) {
				data.push(['', '　']);
			}
			else {
				// TODO: "null"が入ってしまうことがあるようなので修正する
				// 空欄不可かつ値がなかったらリストの最初を選択しておく
				if(typeof item.value=="undefined" || item.value==null || item.value=="") {
					if(0<data.length) {
						item.value = data[0][0];
					}
				}
			}
			item.store = new Ext.data.SimpleStore({
				fields: ['value', 'display'],
				data: data,
				autoLoad: false
			});
			item.displayField = 'display';
			item.valueField = 'value';
			item.editable = false; // 自由入力不可
			item.selectOnFocus = true;
			break;
		case 'checkbox':
			if(item.value == true) item.value = "○";
			item.xtype = 'checkbox';
			item.inputValue = attrInfo.dataExp;
			item.checked = item.value==item.inputValue;
			break;
		default:
			item.xtype = 'textfield';
		}
		return item;
	},

	/**
	 * 登録・更新をリクエストします.
	 * @param {Object} optionParams 指定パラメータ
	 */
	submit: function(optionParams) {
		var me = this;
		var stmap = me.stmap;
		var params = me.params;
		var formPanel = me.formPanel;
		var form = formPanel.form;
		var layerInfo = me.layerInfo;

		// マスク
		var loadMask = new Ext.LoadMask(formPanel, {msg: lang.__("Now saving..<!--2-->")});
		loadMask.show();

		// WKTを取得する
		if(me.feature!=null) {
			params.wkt = stmap.getWKT(me.feature);
		}

		// 属性値を追加
		var formValues = form.getValues();
		for(var name in formValues) {
			// 配列のパラメータはスペースで文字列を連結する
			var formValue = formValues[name];
			if(Ext.isArray(formValue)) {
				formValues[name] = formValue.join(" ");
			}
			params[name] = formValues[name];
		}

		// ファイルのパラメータを追加
		params.fileList = JSON.stringify(me.contentsFileFormPanel.getJSONArray());

		// 登録/更新時に現在時刻を設定する
		var attrInfos = layerInfo.attrInfos;
		for(var idx in attrInfos) {
			var attrInfo = attrInfos[idx];
			var attrId = attrInfo.attrId;
			if( // 登録時にアップデート
				(me.update==false && attrInfo.updateInserted)
				// 更新時にアップデート
				|| (me.update && attrInfo.updateModified) ) {
				var val = null;
				switch(attrInfo.dataTypeId) {
				case 'date':
				case 'time':
				case 'datetime':
				case 'text':
					val = Ext.Date.format(new Date(), "Y/m/d H:i:s");
					break;
				case 'integer':
				case 'float':
				case 'select':
				case 'checkbox':
				default:
					// do nothing
				}
				if(val!=null) {
					params[attrId] = val;
					params.attrIds.push(attrId);
				}
			}
		}

		// 引数に指定パラメータを指定した場合はそのパラメータをマージする
		OpenLayers.Util.extend(params, optionParams);

		// 登録情報の登録処理を送信する
		params._csrf = SaigaiTask.ajaxcsrfToken;
		Ext.Ajax.request({
			url: form.url,
			params: params,
			success: function(response, options) {
				var result = JSON.parse(response.responseText);
				if(result.success) {
					stmap.events.triggerEvent(stmap.EventType.successcontentssubmit, {
						contentsFormPanel: me
					});
					var layer = layerInfo.getLayer();
					if(layer!=null) {
						if(typeof layer.refreshParams=="function") {
							return layer.refreshParams({
								nocache: true
							});
						}
					}
					stmap.redrawContentsLayer(0);
				}
				else {
					alert("ERROR:"+result.msg);
				}

				if(SaigaiTask.PageURL.params.oninsertFeature.match("redirecturl:")){
					var oninsertFeature = SaigaiTask.PageURL.params.oninsertFeature
					var url = oninsertFeature.slice(oninsertFeature.indexOf(":")+1);
					if(confirm(lang.__("Move to {0}", url))){
						location.href=url;
					}
				}else if(SaigaiTask.PageURL.params.oninsertFeature.match("close")){
					// windowcloseの前にalertを挟まないと登録失敗する
					// 原因追及の必要あり
					//alert("windowを閉じます");
					window.open('about:blank','_self').close()
				}
			},
			failure: function() {
				alert(lang.__("Registration not completed."));
			},
			callback: function() {
				loadMask.hide();
			}
		});
	}

};
