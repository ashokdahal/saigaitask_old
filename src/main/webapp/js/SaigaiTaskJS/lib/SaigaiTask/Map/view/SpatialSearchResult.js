
/**
 * 登録情報の空間検索の検索結果のビューです.
 * @class SaigaiTask.Map.view.SpatialSearchResult
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.SpatialSearchResult = function(){
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.SpatialSearchResult.prototype = {

	/**
	 * 検索結果のグリッド
	 * @type {Ext.grid.Panel}
	 */
	grid: null,

	/**
	 * グリッドのイベント
	 * @type {Object.<String, Boolean>}
	 */
	gridEvents: {
		popup: true
	},

	/**
	 * 表示件数のコンボボックス
	 * @type {Ext.form.field.ComboBox}
	 */
	limitCombo: null,

	/**
	 * ページングツールバー
	 * @type {Ext.toolbar.Paging}
	 */
	pagingTbar: null,

	/**
	 * ページングツールバーのイベント
	 * @type {Object.<String, Boolean>}
	 */
	pagingTbarEvents: {
		movefirst: true,
		movelast: true,
		movenext: true,
		moveprevious: true
	},

	/**
	 * 登録情報の空間検索の検索結果のパネルを取得します.
	 * @return {Ext.grid.Panel} 検索結果がなければ null
	 */
	initialize: function() {

		// グリッドを定義する
		var store = Ext.create("Ext.data.Store", {
			fields: [],
			data: []
		});
		var columns = [];

		// グリッドのページングツールバーを定義する
		var pagingTbar = null;
		pagingTbar = Ext.create('Ext.PagingToolbar', {
			store : store,
			displayInfo : true,
			displayMsg : lang.__('{0} - {1} / {2}items'),
			emptyMsg : lang.__("No match info."),
			moveFirst: function() {
				pagingTbar.fireEvent('movefirst');
			},
			moveLast: function() {
				pagingTbar.fireEvent('movelast');
			},
			moveNext: function() {
				pagingTbar.fireEvent('movenext');
			},
			movePrevious: function() {
				pagingTbar.fireEvent('moveprevious');
			}
		});
		pagingTbar.addEvents(this.pagingTbarEvents);

		var grid = null;
		grid = Ext.create("Ext.grid.Panel", {
			autoScroll: true,
			// 横スクロールを表示するにはscrollオプションをtrueまたは"both"に設定する.
			// しかし、マウスホイールの移動量scrollDeltaが効かなくなる.
			scroll: "vertical",
			scrollDelta: 400,
			store: store,
			columns: columns,
 			// paging bar on the bottom
			bbar: pagingTbar
		});
		grid.addEvents(this.gridEvents);

		// 表示件数のコンボボックスをツールバーに定義する
		var limitCombo = Ext.create("Ext.form.field.ComboBox", {
			name : 'limit',
			width : 80,
			allowBlank : false,
			store : new Ext.data.SimpleStore({
				data : [[
					10, lang.__('10 items')
				], [
					20, lang.__('20 items')
				], [
					30, lang.__('30 items')
				], [
					40, lang.__('40 items')
				], [
					50, lang.__('50 items')
				]],
				fields : ['limit', 'text']
			}),
			value : 20,
			valueField : 'limit',
			displayField : 'text',
			editable : false,
			listeners : {
				select : function(combo, records, eOps) {
					var limit = combo.value;
					store.pageSize = limit;
					store.load();
				}
			}
		});

		var tbar = Ext.create("Ext.toolbar.Toolbar", {
			items: [{
				xtype : 'label',
				text : lang.__('Item:<!--2-->'),
				margin : "0 5 0 5"
			}, limitCombo]
		});
		grid.addDocked(tbar);

		this.grid = grid;
		this.limitCombo = limitCombo;
		this.pagingTbar = pagingTbar;

		return grid;
	},

	/**
	 * グリッドを空にします.
	 */
	empty: function() {
		var me = this;
		var pagingTbar = me.pagingTbar;
		var store = pagingTbar.store;

		// 空のデータをロードする
		store.loadData([]);

		// ページ情報を空にする
		store.totalCount = 0;
		store.currentPage = 1;

		// ページングツールバーの表示を更新
		pagingTbar.onLoad();
	},

	/**
	 * 検索結果を読み込みます.
	 * @param {SaigaiTask.Map.model.SearchResult} searchResult
	 */
	load: function(searchResult) {
		var me = this;

		// 検索結果があるかチェックする
		var counts = searchResult.counts;
		var features = searchResult.features;
		if(counts.total==0 || features.length==0) {
			me.empty();
			return null;
		}

		// データストアを定義する
		var attrNames = features[0].getAttributeNames();
		var fields = [];
		for(var attrNamesIdx in attrNames) {
			fields.push(attrNamesIdx);
		}
		var data = [];
		for(var featuresIdx in features) {
			var feature = features[featuresIdx];
			var d = feature.getAttributeValues();
			d.feature = feature;
			data.push(d);
		}

		var store = Ext.create("Ext.data.Store", {
			fields: fields,
			data: data,
			pageSize: counts.limit
		});
		store.totalCount = counts.total;
		store.currentPage = counts.offset/counts.limit+1;

		var columns = [];
		for(var attrNamesIdx in attrNames) {
			var attrName = attrNames[attrNamesIdx];
			columns.push({
				text: attrName,
				dataIndex: attrNamesIdx,
				flex: 1
			});
		}

		// 地図のポップアップアイコンカラムを追加
		var grid = this.grid;
		columns.push({
			xtype:'actioncolumn',
			width: 25,
			items:[{
				getClass: function(v, meta, rec) {
					return 'map-icon pointer';
				},
				handler : function(grid, rowIndex, colIndex) {
					var rec = grid.getStore().getAt(rowIndex);
					var feature = rec.raw.feature;
					me.doPopup(feature.layerId, feature.featureId);
				}
			}]
		});

		grid.reconfigure(store, columns);
		var pagingTbar = this.pagingTbar;
		pagingTbar.store = store;
		pagingTbar.onLoad();
	},

	/**
	 * 登録情報の空間検索の検索結果のウィンドウを表示します.
	 * @param {SaigaiTask.Map.model.SearchResult} searchResult
	 */
	show: function(searchResult) {
		var grid = this.get(searchResult);
		if(grid) {
			// ウィンドウを定義する
			var win = Ext.create("Ext.window.Window", {
				title: lang.__('Search result'),
				layout: 'fit',
				items: [grid]
			});

			win.show();
		}
	},

	/**
	 * ポップアップイベントを起動します.
	 * @param {String} layerId レイヤID
	 * @param {Number} featureId フィーチャID
	 */
	doPopup: function(layerId, featureId) {
		this.grid.fireEvent('popup', layerId, featureId);
	}
};
