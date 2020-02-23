/**
 * 
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.MapPanel= function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.MapPanel.prototype = {
	map: null,
	panel: null,
	panelDivElem: null,
	initialize: function(map) {
		var me = this;
		me.map = map;
		var olmap = map.map;
		var mapDom = document.getElementById(map.div);
		var oldWidth = mapDom.clientWidth;
		var oldHeight = mapDom.clientHeight;
		mapDom.style.width = '100%';
		mapDom.style.height = '100%';
		var parent = mapDom.parentNode;
		// Extで要素のidを自動で付ける
		var panelDivElem = new Ext.Element(document.createElement('div'));
		parent.appendChild(panelDivElem.dom);
		var mapPanel = Ext.create("Ext.panel.Panel", {
			renderTo: panelDivElem.dom,
			region: 'center',
			layout: 'hbox',
			contentEl: map.div
		});
		mapPanel.on('resize', function() {
			olmap.updateSize();
		});
		// パネルを地図サイズに合わせる
		mapPanel.setWidth(oldWidth);
		mapPanel.setHeight(oldHeight);
		// パネルを保存
		this.panel = mapPanel;
		this.panelDivElem = panelDivElem;

		// マスク
		var loadMask = new Ext.LoadMask(mapPanel, {msg: lang.__("Now loading..")});
		map.events.on({
			"beforeloadecommap": function() {
				loadMask.show();
			},
			"loadendecommap": function(){
				loadMask.hide();
			}
		});

		// jQuery.Layout や ExtJSのパネル のリサイズ時に、地図上でうまくドラッグできないためドラッグ中だけマスクする。
		var resizeMask = new Ext.LoadMask(mapPanel, {useMsg: false});
		$(document).on("mousedown", ".x-resizable-handle, .ui-layout-resizer", function() {
			resizeMask.show();
		});
		$("body").on("mouseup", function() {
			if(!resizeMask.disabled)
				resizeMask.hide();
		});
	}
};
