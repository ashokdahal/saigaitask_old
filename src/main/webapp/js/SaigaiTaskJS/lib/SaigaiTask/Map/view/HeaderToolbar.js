/**
 *
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.HeaderToolbar = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.HeaderToolbar.prototype = {
	map: null,
	tbar: null,
	measure: null,
	baselayerCombo: null,
	initialize: function(map) {
		var me = this;
		me.map = map;
		var headerTbarItems = [];

		// ツールバーを初期化
		var headerTbar = Ext.create("Ext.toolbar.Toolbar", {
			items: headerTbarItems
		});
		me.tbar = headerTbar;

		// ベースレイヤ切り替えコンボボックスを追加
		me.updateBaselayerCombo();

		// "loadendecommap"イベントで凡例パネルを再描画
		map.events.register("loadendecommap", map, function(){
			me.updateBaselayerCombo();
		});
	},

	/**
	 * ベースレイヤ切替コンボボックスを更新
	 */
	updateBaselayerCombo: function() {
		var me = this;
		var map = me.map;

		// data 作成
		var baseLayers = [];
		for(var ecommapsIdx in map.ecommaps) {
			var ecommap = map.ecommaps[ecommapsIdx];
			baseLayers = baseLayers.concat(ecommap.getBaseLayerInfos());
		}

		// Store の更新
		var store = null;
		if(me.baselayerCombo==null) {
			// コンボボックスがまだない場合は新規作成
			var store = Ext.create('Ext.data.Store', {
				fields: ['layerId', 'name'],
				data: baseLayers
			});
		}
		else {
			// data の入れ替え
			store = me.baselayerCombo.getStore();
			store.removeAll();
			store.add(baseLayers);
		}

		// 選択値
		var value = null;
		for(idx in baseLayers) {
			var baseLayer = baseLayers[idx];
			if(baseLayer.visibility) {
				value = baseLayer.layerId;
				break;
			}
		}

		// 選択値の更新
		var baselayerCombo = null;
		if(me.baselayerCombo==null) {
			baselayerCombo = me.baselayerCombo = Ext.create('Ext.form.ComboBox', {
				store: store,
				queryMode: 'local',
				displayField: 'name',
				valueField: 'layerId',
				value: value,
				listeners: {
					change: function(combo, newValue, oldValue, eOpts){
						map.setBaseLayerById(newValue);
					}
				}
			});
			me.tbar.add(baselayerCombo);
		}
		else {
			me.baselayerCombo.setValue(value);
		}

		// 初期表示がすべて非表示の場合もあるため、
		// 選択された背景地図レイヤを表示
		map.setBaseLayerById(value);

		me.baselayerCombo = baselayerCombo;
		me.tbar.add(baselayerCombo);
		//距離・面積計測を追加 (ツールバーに移動)
		//var measure = me.measure = new SaigaiTask.Map.view.Measure(map);
		//me.tbar.add(measure.measure);
	}
};
