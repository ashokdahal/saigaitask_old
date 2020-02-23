/**
 * 検索結果リストをポップアップ表示するクラスです.
 * @class SaigaiTask.Map.view.ListPopup
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.ListPopup =  new OpenLayers.Class(SaigaiTask.Map.view.Popup, {
	stmap: null,

	initialize: function(stmap) {
		this.stmap = stmap;
	},

	show: function(center,bbox,result, pinned) {
		var me = this;
		var stmap = me.stmap;
		// result からデータを作成する
		var count = result[0][0];
		var rows = result[1];
		var dataMapList = new Array();
		var layerInfoCache = {};
		for(var idx=0; idx<rows.length; idx++){
			var layerId = rows[idx][0];
			var featureId = rows[idx][1];
			var geom = rows[idx][2];
			var firstAttr = rows[idx][3][0];
			// データ
			var dataMap = new Array();
			// レイヤ名
			var layerInfo = layerInfoCache[layerId];
			if(layerInfo==null) {
				layerInfo = stmap.getLayerInfo(layerId);
				layerInfoCache[layerId] = layerInfo;
			}
			dataMap[lang.__('Layer name')] = layerInfo.name;
			// 名称などの属性
			var firstAttrInfo = layerInfo.getAttrInfo(firstAttr[0]);
			dataMap[lang.__('Name')] = (firstAttrInfo!=null ? firstAttrInfo.name+": "+firstAttr[2] : "name attr exists");
			dataMap[lang.__('Distance')] =  geom[2];
			dataMap[lang.__('Layer ID')] = layerId;
			dataMap[lang.__('Feature ID')] = featureId;
			dataMapList.push(dataMap);
		}
		var createDataSet = function(dataMapList){
			if(!dataMapList||dataMapList.length==0) return null;
			//var fieldSize = dataMapList[0].length;
			var fields = new Array();
			for(var key in dataMapList[0]){fields.push(key);}
			//var valueSize = dataMapList.length;
			var values = new Array();
			for(var idx=0;idx<dataMapList.length;idx++){
				var dataMap = dataMapList[idx];
				var value = new Array();
				for(var fieldsIdx in fields){
					var key = fields[fieldsIdx];
					var data = dataMap[key];
					value.push(data);
				}
				values.push(value);
			}
			return {fields:fields, values: values};
		};
		var dataset = createDataSet(dataMapList);
		console.log('dataset');
		console.log(dataset);


		//============================================
		// グリッド
		//============================================
		var createDataStore = function(dataset){
			// フィールド生成
			var fields = new Array();
			for(var idx=0; idx<dataset.fields.length; idx++){
				var name = dataset.fields[idx];
				var field = {name: name};
				fields.push(field);
			}
			// データストア生成
			var data = dataset.values;
			var store = Ext.create("Ext.data.ArrayStore",{
				fields: fields,
				data: data
			});
			return store;
		};
		var store = createDataStore(dataset);

		var createGridPanel = function(option){
			var columns = new Array();
			for(var idx=0; idx<dataset.fields.length; idx++){
				var name = dataset.fields[idx];
				var column = {text: name, dataIndex: name, flex: 1};
				if(name==lang.__('Distance')) {
					column['renderer'] = Ext.util.Format.numberRenderer('0m');
					column['flex'] = null;
					column['fixed'] = true;
					column['width'] = 60;
				}
				if(name==lang.__('Feature ID')||name==lang.__('Layer ID')){ column['hidden']=true; }
				columns.push(column);
			}
			console.log('columns');
			console.log(columns);
			var grid = Ext.create('Ext.grid.Panel',{
				store: option.store,
				stateful: true,
				collapsible: false,
				multiSelect: false,
				header: false,
				width: 250,
	//			closable: true,
				frame: false,
				stateId: 'stateGrid',
					columns: columns,
					viewConfig: {
						stripeRows: true,
						enableTextSelection: true
					}
			});
			return grid;
		};
		var option = {store:store};
		var grid = createGridPanel(option);

		// Grid のクリックイベント
		var self = this;
		var onItemClick = function(view, record, item, index, e, eOpts){
			if(stmap.popupManager!=null) {
				stmap.popupManager.close(me.popup);
			}
			var layerId = record.data[lang.__('Layer ID')];
			var fid = record.data[lang.__('Feature ID')];
			stmap.getContent(layerId, fid, center, bbox, {
				// リストへもどるためのフラグ
				fromList: true,
				// ピン留めフラグ
				pinned: me.popup.pinned
			});
			return false;
		};
		grid.addListener('itemclick',onItemClick);

		if(stmap.popupManager!=null) {
			stmap.popupManager.closeAll();
		}
		var items = [grid];
		me.showExtPopup({
			title: count+lang.__("Items"),
			map: stmap,
			olmap: stmap.map,
			center: center,
			panelWidth: grid.width,
			pinned: pinned,
			items: items
		});
	}
});
