
/**
 * @class SaigaiTask.Map.view.MainPanel
 * 地図パネルや凡例パネルなどを配置するメインパネルです.
 * @param map
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.MainPanel = function(){
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.MainPanel.prototype = {

	map: null,

	/**
	 * 各パネルを配置するためのメインパネル
	 * @type {Ext.panel.Panel}
	 */
	panel: null,

	/**
	 * 凡例パネル
	 * @type {Ext.tree.Panel}
	 */
	legend: null,

	/**
	 * 地図パネルを複数配置するためのコンテナパネル
	 * @type {Ext.Panel}
	 */
	mapPanelContainer: null,

	/**
	 * 地図パネル
	 * @type {SaigaiTask.Map.view.MapPanel}
	 */
	mapPanel: null,

	/**
	 * ヘッダーツールバー
	 * @type {SaigaiTask.Map.view.HeaderToolbar}
	 */
	headerToolbar: null,

	/**
	 * OpenLayers.Map を地図パネルに描画します.
	 * @param {SaigaiTask.Map} map
	 */
	initialize: function(map) {
		var me = this;
		me.map = map;
		map.components.mainpanel = me;

		var panelItems = [];

		var items = [];
		// 地図パネル
		var mapPanel = me.mapPanel = new SaigaiTask.Map.view.MapPanel(map);
		// ヘッダーのツルーバーを追加
		var headerToolbar = me.headerToolbar = new SaigaiTask.Map.view.HeaderToolbar(map);
		mapPanel.panel.addDocked(headerToolbar.tbar);
		// フッターのツールバーを追加
		var geocodingToolbar = new SaigaiTask.Map.view.GeocodingToolbar(map);
		geocodingToolbar.tbar.dock = "bottom";
		// 2画面表示ボタン
		var submap = null;
		geocodingToolbar.tbar.add(["->", Ext.create("Ext.Button", {
			text: lang.__("2 window display "),
			tooltip: lang.__("It displays map window bisection."),
			icon: map.icon.getURL("submapIconURL"),
			align: "left",
			handler: function() {
				var mapSyncControl = map.controls["syncControl"];
				// 2画面表示
				if(mapSyncControl.group.length<2) {
					submap = me.createSubmap();
				}
				else {
					me.removeSubmap(submap);
				}
			}
		})]);
		//mapPanel.panel.addDocked(geocodingToolbar.tbar);
		items.push(mapPanel.panel);
		// 地図パネルコンテナ
		var mapPanelContainer = me.mapPanelContainer = Ext.create("Ext.Panel", {
			region: "center",
			layout: {
				type: "hbox",
				align: "stretch"
			},
			defaults: {
				flex: 1
			},
			items: items
		});
		// 2画面表示でジオコーディングツールバーが途切れないように、コンテナの方に追加する
		mapPanelContainer.addDocked(geocodingToolbar.tbar);
		panelItems.push(mapPanelContainer);

		// 凡例パネル
		var legend = me.legend = new SaigaiTask.Map.view.LegendPanel(map);
		legend.tree.region = "west";
		legend.tree.margin = "0 0 0 0";
		legend.tree.draggable = false;
		panelItems.push(legend.tree);
		legend.collapsed = true;

		// 下側パネル
/*
		var bottomPanel = null;
		var bottomPanelDomId = map.div+'-bottom-panel';
		var bottomPanelDom = document.getElementById(bottomPanelDomId);
		if(bottomPanelDom!=null) {
			console.log(bottomPanelDom);
			bottomPanel = Ext.create('Ext.panel.Panel', {
				region: 'south',
				margin: '5 0 0 0',
				resizable: true,
				contentEl: bottomPanelDomId
			});
			panelItems.push(bottomPanel);
		}
*/

		// 各パネルを配置するメインパネル
		var mapContainerHeight = $(mapPanel.panelDivElem.dom).parent().height();
		var main = me.panel = Ext.create("Ext.panel.Panel", {
			layout: 'border',
			height: mapContainerHeight,
			items: panelItems,
			renderTo: mapPanel.panelDivElem,
			listeners: {
				"beforeadd": function(container, component, index, eOpts) {
					if(me.legend.tree.id==component.id) {
						map.events.triggerEvent(map.EventType.beforeaddlegend, {
							legend: component
						});
					}
					return true;
				}
			}
		});

		// パネルサイズを最大まで広げる
		map.resize = function(){
			var size = me.getExpandSize(main.getEl().dom);
			main.setWidth(size.width);
			main.setHeight(size.height);
		};
		$(window).resize(map.resize);
		$(window).scroll(map.resize);
		map.resize();

		// イベント発火
		map.events.triggerEvent("initmainpanel", {
			mainpanel: me
		});
	},

	/**
	 * コンポーネントを配置と逆のリサイザーのみ有効化します.
	 * TODO: 凡例パネルを描画後に呼び出すようにする？
	 * @param {Ext.Component} component
	 */
	activateResizer: function(component) {
		var me = this;
		var map = me.map;
		var activateRegions = {
			"north": "south",
			"east": "west",
			"west": "east",
			"south": "north"
		};
		var activateRegion = activateRegions[component.region];
		if(typeof activateRegion!="undefined") {
			var div = $(component.getEl().dom);
			var handles = $(".x-resizable-handle", div);
			// 一旦すべてのリサイズハンドルを無効化
			handles.removeClass("x-resizable-handle").addClass("x-unresizable-handle");
			// 有効化
			handles.filter(".x-resizable-handle-"+activateRegion).addClass("x-resizable-handle").removeClass("x-unresizable-handle");
		}
	},

	/**
	 * 地図の大きさをウィンドウサイズに合わせて広げる.
	 * @param target DOMオブジェクト
	 * @return {width: int, height: int}
	 */
	getExpandSize: function(target){

		// スクロール位置
		var sLeft = jQuery(window).scrollLeft();
		var sTop = jQuery(window).scrollTop();

		// ウィンドウの大きさ
		var winWidth = $(window).width();
		var winHeight = $(window).height();

		// ウィンドウの右下の位置
		var winRight = sLeft + winWidth;
		var winBottom = sTop + winHeight;

		// 地図の位置
		var elem = jQuery(target);
		var elemLeft = elem.offset().left;
		var elemTop = elem.offset().top;

		// 地図の大きさ
		var anchorX = winRight;
		var anchorY = winBottom;
		var margin = 5;
		var width = anchorX - elemLeft - margin;
		var height = anchorY - elemTop - margin;

		return {width: width, height: height};
	},

	/**
	 * 凡例パネルを再描画する.
	 */
	redrawLegendPanel: function(){
		var me = this;
		var oldLegend = me.legend.tree;
		me.legend = new SaigaiTask.Map.view.LegendPanel(me.map);
		var legend = me.legend.tree;
		legend.region = oldLegend.region;
		legend.margin = oldLegend.margin;
		legend.draggable = oldLegend.draggable;
		var panel = me.panel;
		panel.remove(oldLegend);
		panel.add(legend);
		panel.doLayout();
		me.map.events.triggerEvent("afterredrawlegendpanel");
	},

	/**
	 * 2画面表示用のサブマップを作成してパネルに追加する.
	 */
	createSubmap: function() {
		var me = this;
		var map = me.map;
		var mapSyncControl = map.controls["syncControl"];
		var mapPanelContainer = me.mapPanelContainer;

		// OpenLayers.Mapのdivを生成する
		var newDiv = new Ext.Element(document.createElement('div'));
		newDiv.appendTo(Ext.getBody());

		// 地図を生成
		var submap = window.submap = new SaigaiTask.Map(newDiv.id, {contextPath: map.contextPath, api: map.api, icon: map.icon});
		submap.mapId = map.mapId;

		// eコミ情報をロード
		var ecommap = map.ecommaps[0].clone(submap);
		submap.registEcommapInfo(ecommap);

		// ズームバーをズームだけにする
		submap.controls["panZoom"].destroy();
		var zoom = new OpenLayers.Control.Zoom();
		submap.addControl(zoom, "zoom");

		// LayerSwitcherの追加
		submap.addControl(new OpenLayers.Control.LayerSwitcher(), "layerSwitcher");

		// TODO: ベースレイヤのデフォルト設定
		var submapPanel = new SaigaiTask.Map.view.MapPanel(submap);
		submap.components.submapPanel = submapPanel;

		// ヘッダーのツルーバーを追加
		var headerToolbar = new SaigaiTask.Map.view.HeaderToolbar(submap);
		headerToolbar.tbar.add(["->", Ext.create("Ext.Button", {
			tooltip: lang.__("Scale synchronization"),
			enableToggle: true,
			pressed: true,
			icon: map.icon.getURL("syncIconURL"),
			handler: function(button) {
				if(button.pressed) {
					mapSyncControl.activate();
					submapSyncControl.activate();
				}
				else {
					mapSyncControl.deactivate();
					submapSyncControl.deactivate();
				}
			}
		}), Ext.create("Ext.Button", {
			tooltip: lang.__("Close"),
			icon: map.icon.getURL("deleteIconURL"),
			handler: function() {
				me.removeSubmap(submap);
			}
		})]);
		submapPanel.panel.addDocked(headerToolbar.tbar);

		// 地図の同期設定
		var submapSyncControl = submap.controls["syncControl"];
		mapSyncControl.mergeGroup(submapSyncControl);
		mapSyncControl.activate();
		submapSyncControl.activate();

		// 登録情報レイヤのレイヤリフレッシュの同期
		var syncLayerRefresh = function(src, dst) {
			if(src==null) return;
			src.events.on({
				"refreshParams": function() {
					(function(layer) {
						if(layer!=null) {
							if(typeof layer._refreshParams=="function") {
								return layer._refreshParams({
									nocache: true
								});
							}
						}
					})(dst);
				}
			});
		};
		if(map.ecommaps[0].contentsLayerInfo!=null) {
			var    layer =    map.ecommaps[0].contentsLayerInfo.getLayer();
			var sublayer = submap.ecommaps[0].contentsLayerInfo.getLayer();
			syncLayerRefresh(   layer, sublayer);
			syncLayerRefresh(sublayer,    layer);
		}

		// 地図パネルを追加
		mapPanelContainer.add(submapPanel.panel);

		return submap;
	},

	/**
	 * サブマップとパネルを削除する.
	 * @param {SaigaiTask.Map} submap
	 */
	removeSubmap: function(submap) {
		var me = this;

		// パネルを削除
		var mapPanelContainer = me.mapPanelContainer;
		var submapPanel = submap.components.submapPanel;
		mapPanelContainer.remove(submapPanel.panel);

		// 同期グループから削除
		var mapSyncControl = me.map.controls["syncControl"];
		var submapSyncControl = submap.controls["syncControl"];
		mapSyncControl.removeGroup(submapSyncControl);
	}

};
