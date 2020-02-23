/**
 * 登録情報ウィンドウ
 * @require SaigaiTask/Map/view.js
 * @returns {SaigaiTask.Map.view.SelectFeatureWindow}
 */
SaigaiTask.Map.view.SelectFeatureWindow = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.SelectFeatureWindow.prototype = {

	stmap: null,

	/**
	 * @type {Ext.window.Window}
	 */
	window: null,

	/**
	 * @type {Ext.grid.Panel}
	 */
	grid: null,

	/**
	 * 選択解除
	 * Ext.grid.column.Action の items に登録した Object
	 * @type {Object}
	 */
	removeActionColumnItem: null,

	/**
	 * 一括変更ボタン
	 * @type {Ext.button.Button}
	 */
	slimerButton: null,

	/**
	 * 登録情報ウィンドウを表示します.
	 * @param option.stmap 地図オブジェクト
	 */
	initialize: function(option) {
		var me = this;
		var stmap = option.stmap;
		var featureData = option.featureData;
		var nameAttr = featureData.nameAttr;

		// グリッドパネルの作成
		var fields = ["nameAttr", "layerId", "featureId"];
		var store = Ext.create("Ext.data.ArrayStore",{
			fields: fields,
			data: [fields] // ダミーデータとしてフィールドを登録
		});
		me.removeActionColumnItem = {
			icon: stmap.icon.getURL("removeIconURL"),
			tooltip: lang.__("Cancel<!--4-->")
		};
		me.grid = Ext.create('Ext.grid.Panel',{
			store: store,
			stateful: true,
			collapsible: false,
			multiSelect: false,
			header: false,
			width: 250,
//			closable: true,
			frame: false,
			stateId: 'stateGrid',
			columns: [{
				//text: nameAttr[1],
				text: lang.__("Name"),
				dataIndex: "nameAttr",
				flex: 1
			}, {
				xtype: "actioncolumn",
				width: 30,
				//text: "解除",
				items: [me.removeActionColumnItem],
				renderer : function(val, metadata, record) {
					metadata.style = 'cursor: pointer;';
					return val;
				}
			}],
			viewConfig: {
				stripeRows: true,
				enableTextSelection: true
			}
		});

		// グリッド作成後にダミーデータを削除
		store.removeAll();
		me.add(featureData);

		// ウィンドウを生成
		me.slimerButton = Ext.create("Ext.button.Button", {
			text: lang.__('Bulk change'),
			textAlign: "center"
		});
		me.window = Ext.create('Ext.window.Window', {
			title: lang.__('Hide select.<!--2-->'),
			width: 300, //height: 600,
			maxWidth: document.body.clientWidth,
			maxHeight: document.body.clientHeight,
			collapsible: true,
			layout: 'fit',
			items: me.grid,
			dockedItems: [{
				xtype: 'toolbar',
				dock: 'bottom',
				ui: 'footer',
				layout: 'fit',
				items: [me.slimerButton]
			}]
		});
	},

	/**
	 * フィーチャをグリッドに追加します.
	 * @param {SaigaiTask.Map.control.SelectFeatureControl.FeatureData} featureData
	 */
	add: function(featureData) {
		var me = this;
		var grid = me.grid;
		var store = grid.getStore();
		store.add({
			"nameAttr": featureData.nameAttr[1]+":"+featureData.nameAttr[2],
			"layerId": featureData.layerId,
			"featureId": featureData.featureId
		});
	},

	/**
	 * フィーチャをグリッドから削除します.
	 * @param {SaigaiTask.Map.control.SelectFeatureControl.FeatureData} featureData
	 */
	remove: function(featureData) {
		var me = this;
		var grid = me.grid;
		var store = grid.getStore();
		var index = store.findBy(function(record, id) {
			var data = record.getData();
			if(data.layerId!=featureData.layerId) return false;
			if(data.featureId!=featureData.featureId) return false;
			return true;
		});
		if(index!=-1) {
			store.removeAt(index);
		}
	},

	/**
	 * ウィンドウを表示します.
	 */
	show: function() {
		var me = this;
		var win = me.window;
		win.show();
		win.alignTo(document, "tr", [-win.getWidth(), 0/*-win.getHeight()*/]);
	}
};
