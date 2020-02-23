/**
 * 登録情報の空間検索フォームのビューです.
 * @class SaigaiTask.Map.view.SpatialSearchForm
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.SpatialSearchForm = function(){};
SaigaiTask.Map.view.SpatialSearchForm.prototype = {

	/**
	 * デフォルトオプション
	 */
	defaultOptions: {
		limit: 0,
		offset: 0,
		searchButtonInfo: {
			featureInfo: null
		}
	},

	/**
	 * コンストラクタのオプションの保存用
	 */
	options: null,

	/**
	 * フォームパネル
	 * @type {Ext.form.Panel}
	 */
	form: null,

	/**
	 * 登録情報の空間検索フォームのパネルを取得します.
	 * @param {Object<String, *>} options
	 * @param {String} options.layerId
	 * @param {String} options.featureId
	 * @param {Object<String, *>} options.searchButtonInfo
	 * @param {String} options.searchButtonInfo.url
	 * @param {Number} options.searchButtonInfo.mid
	 * @param {Array<Object<String, *>>} options.searchButtonInfo.contentsLayers
	 * @return {Ext.form.Panel}
	 */
	get: function(options) {
		var me = this;

		// 未指定のオプションをデフォルトで上書きする
		me.options = options;
		Ext.applyIf(options, me.defaultOptions);

		var featureInfo = options.searchButtonInfo.featureInfo;

		// 検索対象のデータストア
		var targetsConfig = {
			fields: ['id', 'name'],
			data : []
		};

		// 検索対象のリストの定義と初期値の定義
		var defaultTargetValue = null;
		for(var contentsLayersIdx in options.searchButtonInfo.contentsLayers) {
			var contentsLayerInfo = options.searchButtonInfo.contentsLayers[contentsLayersIdx];
			targetsConfig.data.push({
				id: contentsLayerInfo.layerId,
				name: contentsLayerInfo.name
			});
			if(defaultTargetValue==null) {
				defaultTargetValue = contentsLayerInfo.layerId;
			}
		}
		var targets = Ext.create('Ext.data.Store', targetsConfig);

		// spatialType 空間検索の範囲検索の検索方法のデータストア
		var methods = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data : [{
				"id": 1,
				"name": lang.__("Include")
			}, {
				"id": 2,
				"name": lang.__("Not include")
			}, {
				"id": 3,
				"name": lang.__("Fully include")
			}, {
				"id": 4,
				"name": lang.__("Not included completely")
			}, {
				"id": 5,
				"name": lang.__("Overlap")
			}, {
				"id": 6,
				"name": lang.__("Not overlap")
			}]
		});

		// 検索フォームの定義
		var form = null;
		form = Ext.create("Ext.form.Panel", {
			bodyPadding: 5,
			url: options.searchButtonInfo.url,
			buttonAlign: 'center',
			fieldDefaults: {
				labelAlign: 'left',
				labelWidth: 100,
				anchor: '100%'
			},
			items: [{
				xtype: 'fieldcontainer',
				fieldLabel: lang.__('Retrieval object'),
				labelWidth: 60,
				layout: 'hbox',
				items: [{
					xtype: "combo",
					editable: false,
					store: targets,
					queryMode: 'local',
					name: 'layer',
					displayField: 'name',
					valueField: 'id',
					value: defaultTargetValue,
					flex: 1
				}, {
					xtype: 'button',
					text: lang.__('Search'),
					margin: '0 0 0 10',
					padding: '2 10 2 10',
					iconCls: 'search-icon',
					handler: function(){
						form.doSearch();
					}
				}]
			}, {
				xtype: 'fieldset',
				title: lang.__('Search condition'),
				margin: 0,
				collapsible: true,
/*
				items: [{
					xtype: 'fieldset',
					title: '属性検索',
					//collapsible: true,
					padding: '0 10 0 10',
					defaults: {
						labelWidth: 80
					},
					items: [{
						xtype: "textfield",
						name: "keyword",
						fieldLabel: "キーワード"
					}]
				}, {
					xtype: 'fieldset',
					title: '空間検索',
*/
					//collapsible: true,
					margin: 0,
					padding: '0 10 0 10',
					defaults: {
						labelWidth: 120
					},
					items: [{
						// 名称を表示
						xtype: "displayfield",
						fieldLabel: lang.__("Search range"),
						value: featureInfo.attrs[0].attrValue
					}, {
						xtype: "textfield",
						hidden: true,
						name: "features",
						value: options.layerId+"."+options.featureId
					}, {
						xtype: "numberfield",
						name: "buffer",
						fieldLabel: lang.__("Search range buffer (m)"),
						value: 0
					}, {
						xtype: "combo",
						fieldLabel: lang.__("Search method"),
						editable: false,
						store: methods,
						queryMode: 'local',
						name: "spatialType",
						displayField: 'name',
						valueField: 'id',
						value: 1
//					}]
				}]
			}],

			/**
			 * 検索イベント(search)を起動する
			 */
			doSearch: function() {
				form.fireEvent('search');
			}
		});
		form.addEvents('search', 'aftersearch');

		me.form = form;
		return form;
	},

	/**
	 * 登録情報の空間検索フォームのウィンドウを表示します.
	 * @param {Object<String, *>} options
	 * @param {String} options.layerId
	 * @param {String} options.featureId
	 * @param {Object<String, *>} options.searchButtonInfo
	 * @param {String} options.searchButtonInfo.url
	 * @param {Number} options.searchButtonInfo.mid
	 * @param {Array<Object<String, *>>} options.searchButtonInfo.contentsLayers
	 */
	show: function(options) {
		var form = this.get(options);
		form.on('search', function(){
			form.submit({
				params: {
					mid: options.searchButtonInfo.mid
				}, success: function(form, action) {
					var result = action.result;
					var spatialSearch = new SaigaiTask.Map.control.SpatialSearch();
					var searchResult = spatialSearch.getSearchResult(result);
					var spatialSearchResult = new SaigaiTask.Map.view.SpatialSearchResult();
					spatialSearchResult.show(searchResult);
				}
			});
		});

		// 検索フォームのウィンドウを表示
		var win = Ext.create("Ext.window.Window", {
			extend: "Ext.window.Window",
			title: lang.__("Registration info Search"),
			width: 500,
			layout: "fit",
			items: [form]
		});
		win.show();
	},

	/**
	 * 追加パラメータ付きでパラメータマップを取得します.
	 * @return {Map.<String, *}
	 */
	getValues: function() {
		var me = this;
		var options = me.options;
		var values = me.form.getValues();
		values.mid = options.searchButtonInfo.mid;
		values.limit = options.limit;
		values.offset = options.offset;
		return values;
	}

};
