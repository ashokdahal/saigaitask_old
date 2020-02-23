/**
 * PDF Range Layer
 * @requires SaigaiTask/Map/Layer.js
 */
SaigaiTask.Map.Layer.PdfRangeLayer = new OpenLayers.Class(OpenLayers.Layer.Vector, {

	/**
	 * @type {SaigaiTask.Map.view.PrintPreviewWindow}
	 */
	printPreviewWindow: null,
	
	/** <span class="ja">印刷範囲表示レイヤ</span><span class="ja">Layer to display print area</span> */
	pdfRangeLayer : null,

	/** <span class="ja">印刷範囲形状</span><span class="en">Print range shape</span> */
	printFeature : null,

	/** <span class="ja">用紙縦横比</span><span class="en"></span> */
	paperRate : {'a':0.7073171, 'b':0.7074176},
	
	/** 印刷範囲用ノードレイヤ **/
	printRangeNodeLayer : null,
	/** 印刷範囲用ノード **/
	printRangeNode : null,
	/** 印刷範囲用ノードマーカー **/
	printRangeNodeMaker : [],
	/** 印刷範囲用ノードドラッグイベント **/
	dragNodeMarker : null,
	/** 画像範囲用ノードドラッグイベント **/
	dragImageNodeMarker : null,
	/** **/
	dragEvent : "",
	/**  **/
	resizeOrigin : null,

	initialize: function(printPreviewWindow) {
		var me = this, self = this, layer=this;
		me.printPreviewWindow = printPreviewWindow;
	},
	
	initLayer: function() {
		var me = this, self = this, layer=this;
		var printPreviewWindow = me.printPreviewWindow;
		var stmap = printPreviewWindow.stmap;
		var map = stmap.map;

		if (!self.pdfRangeLayer) {
			me.pdfRangeLayer = this;

			var name = "PdfRangeLayer";
			OpenLayers.Layer.Vector.prototype.initialize.apply(this, [name]);

			// LayerSwitcher非表示
			me.displayInLayerSwitcher = false;
			map.addLayer(layer);

			// スタイルを赤枠にする
			layer.styleMap.styles['default'] = new OpenLayers.Style({
				fillColor: "#FFFFFF", fillOpacity: 0.25,
				strokeColor: "#FF0000", strokeOpacity: 1, strokeWidth: 1
			});
		}

		if (!this.printRangeNodeLayer){
			this.printRangeNodeLayer = new OpenLayers.Layer.Markers('rangeNode');
			this.printRangeNode = new OpenLayers.Icon(stmap.icon.getURL("pointnodeIconURL"), new OpenLayers.Size(9,9), new OpenLayers.Pixel(-5,-5));
			map.addLayer(self.printRangeNodeLayer);
		}

		self.dragNodeMarker = new OpenLayers.Control.DragMarker(
				this.printRangeNodeLayer, {
					onStart:function(marker){
						self.nodeDragStart(marker, self.pdfRangeLayer);
					},
					onDrag:function(marker){
						self.nodeDrag(marker, self.pdfRangeLayer);
					},
					onComplete:function(marker){
						self.nodeDragComplete(marker, self.pdfRangeLayer);
						if (marker) marker.events.triggerEvent("mouseout");
					}
		});
		map.addControl(this.dragNodeMarker);
		this.dragNodeMarker.activate();
		self.dragImageNodeMarker = new OpenLayers.Control.DragMarker(
			this.printRangeNodeLayer, {
				onStart:function(marker){
					self.nodeDragStart(marker, self.imageRangeLayer);
				},
				onDrag:function(marker){
					self.nodeDrag(marker, self.imageRangeLayer);
				},
				onComplete:function(marker){
					self.nodeDragComplete(marker, self.imageRangeLayer);
					if (marker) marker.events.triggerEvent("mouseout");
				}
			}
		);
		map.addControl(this.dragImageNodeMarker);
		this.dragImageNodeMarker.activate();
	},

	onHideDialog : function()
	{
		//範囲レイヤ非表示
		this.pdfRangeLayer.setVisibility(false);
		this.printRangeNodeLayer.setVisibility(false);

		//<span class="ja">範囲クリア</span>
		//<span class="en">Clear the range</span>
		if (this.printFeature) this.pdfRangeLayer.removeFeatures([this.printFeature]);
		if (this.printFeatureInner != null){
			for (var i = 0; i < this.printFeatureInner.length; i++){
				this.pdfRangeLayer.removeFeatures([this.printFeatureInner[i]]);
			}
		}
		this.printFeature = null;

		// ノードマーカーの削除
		this.dragNodeMarker.deactivate();
		this.dragImageNodeMarker.deactivate();
		if (this.printRangeNodeMaker.length){
			this.printRangeNodeLayer.clearMarkers();
		}
		this.printRangeNodeMarker = [];
	},

	showPrintRange: function(bbox) {
		var me = this;
		me.initLayer();
		
		var map = this.map;
		try {
			//<span class="ja">範囲指定がなければ表示範囲</span>
			//<span class="en">If the range is not specified, display range</span>
			if (!bbox) bbox = map.getExtent();
			if (this.printFeature) this.pdfRangeLayer.removeFeatures([this.printFeature]);
			var points = this.getPageRange(bbox).toGeometry().components[0].components;
			points[0] = points[0].clone();//<span class="ja">始点と終点が同じObjectなので始点をclone</span><span class="en"></span>
			this.printFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(points), null,
				{strokeColor:"#FF0000", strokeOpacity:0.7, strokeWidth:4, cursor:'pointer'});
			this.addFeatures([this.printFeature]);
			
			//一番上のレイヤにする
			this.moveToFrontLayer(this.pdfRangeLayer);
			this.pdfRangeLayer.setVisibility(true);
				
		} catch (e) { console.error(e); }
	},
	
	/**　選択ノード設定
	 * @param feature
	 */
	setPrintRangeNode : function(feature)
	{
		var bounds = feature.geometry.getBounds();
		this.printRangeNodeLayer.clearMarkers();
		var lonLat = {};
		var tag = {}

		// 上
		lonLat.lon = bounds.left + ((bounds.right - bounds.left)/2);
		lonLat.lat = bounds.top;
		tag = {math : 'y', pos : 't'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'n-resize' ,tag);
		// 下
		lonLat.lon = bounds.left + ((bounds.right - bounds.left)/2);
		lonLat.lat = bounds.bottom;
		tag = {math : 'y', pos : 'b'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 's-resize' ,tag);
		// 左
		lonLat.lon = bounds.left;
		lonLat.lat = bounds.top + ((bounds.bottom - bounds.top)/2);
		tag = {math : 'x', pos : 'l'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'w-resize' ,tag);
		// 右
		lonLat.lon = bounds.right;
		lonLat.lat = bounds.top + ((bounds.bottom - bounds.top)/2);
		tag = {math : 'x', pos : 'r'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'e-resize' ,tag);
		// 左上
		lonLat.lon = bounds.left;
		lonLat.lat = bounds.top;
		tag = {math : 'y', pos : 'tl'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'nw-resize' ,tag);
		// 右上
		lonLat.lon = bounds.right;
		lonLat.lat = bounds.top;
		tag = {math : 'y', pos : 'tr'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'ne-resize' ,tag);
		// 左下
		lonLat.lon = bounds.left;
		lonLat.lat = bounds.bottom;
		tag = {math : 'y', pos : 'bl'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'sw-resize' ,tag);
		// 右下
		lonLat.lon = bounds.right;
		lonLat.lat = bounds.bottom;
		tag = {math : 'y', pos : 'br'};
		this.addPrintRangeNode(((this.dragEvent == "pdf") ? this.dragNodeMarker : this.dragImageNodeMarker), feature, new OpenLayers.LonLat(lonLat.lon, lonLat.lat), this.printRangeNode.clone(), 'se-resize' ,tag);
		this.moveToFrontLayer(this.printRangeNodeLayer);
	},

	/** 選択ノード追加
	 * @parama control 選択マーカーレイヤ
	 * @parama feature 対象フィーチャー
	 * @parama lonlat lonlat
	 * @parama icon 表示画像
	 * @parama cursor カーソル
	 * @parama tag その他情報
	 */
	addPrintRangeNode : function(control, feature, lonlat, icon, cursor ,tag)
	{
		icon.imageDiv.style.cursor = cursor;
		var marker = new OpenLayers.Marker(lonlat, icon);
		marker.linkid = feature.id;
		marker.tag = tag;
		this.printRangeNodeLayer.addMarker(marker);
		this.printRangeNodeMaker[this.printRangeNodeMaker.length] = marker;
		var self = this;

		if (control) {
			marker.events.register("mouseover", marker, function(){
				control.overFeature(this);
			});
			marker.events.register("mouseout", marker, function(){
				control.outFeature(this);
			});
		}
		// 初回アクション時にイベントが反応しない時がある為、一度トリガーさせる

		marker.events.triggerEvent("mouseover");
		marker.events.triggerEvent("mouseout");
	},

	/** 選択ノードドラッグ開始
	 * @parama marker ドラッグ開始されたマーカー
	 * @parama layer 対象レイヤ
	 */
	nodeDragStart : function(marker, layer)
	{
		if (!marker) return;
		var feature = layer.getFeatureById(marker.linkid);
		try {
			this.resizeOrigin = null;
			//外枠取得
			var bounds = feature.geometry.getBounds();
			//始点取得
			var x = 0;
			var y = 0;
			// 上下位置設定
			//if (this.dragEvent == "pdf"){
				// 上辺
				if (marker.tag.pos.indexOf("t") != -1){
					y = bounds.bottom;
				}
				// 下辺
				else if (marker.tag.pos.indexOf("b") != -1){
					y = bounds.top;
				}
				// その他
				else {
					y = ((bounds.bottom - bounds.top) / 2) + bounds.top;
				}
				// 左右位置設定
				// 右辺
				if (marker.tag.pos.indexOf("r") != -1){
					x = bounds.left;
				}
				// 左辺
				else if (marker.tag.pos.indexOf("l") != -1){
					x = bounds.right;
				}
				// その他
				else {
					x = ((bounds.right - bounds.left) / 2) + bounds.left;
				}
			//}
			//else{
			//	var isLeft = (marker.tag.pos.indexOf("l") != -1) ? true : false;
			//	var isTop = (marker.tag.pos.indexOf("t") != -1) ? true : false;
			//	x = (isLeft) ? bounds.right : bounds.left;
			//	y = (isTop) ? bounds.bottom : bounds.top;
			//}
			this.resizeOrigin = new OpenLayers.Geometry.Point(x,y);
			//Point内の変数に移動前のlonlat格納
			this.resizeOrigin.prevLon = marker.lonlat.lon;
			this.resizeOrigin.prevLat = marker.lonlat.lat;

			//選択解除と他のノードの非表示
			var markers = this.printRangeNodeLayer.markers;
			for (var i=markers.length-1; i>=0; i--) {
				if (markers[i] != marker) {
					this.printRangeNodeLayer.removeMarker(markers[i]);
				}
			}
		}
		catch (e) {
			console.error(e);
		}
	},
	/** 選択ノードドラッグ
	 * @parama marker ドラッグ中のマーカー
	 * @parama layer 対象レイヤ
	 */
	nodeDrag : function(marker, layer)
	{
		if (this.resizeOrigin) {
			var lonlat = marker.lonlat;
			var feature = layer.getFeatureById(marker.linkid);
			this.resizeFeature(feature, this.resizeOrigin, lonlat, layer ,marker.tag);
		}
	},
	/** 選択ノードドラッグ完了
	 * @parama marker ドラッグ完了したマーカー
	 * @parama layer 対象レイヤ
	 */
	nodeDragComplete : function(marker, layer)
	{
		try {
			if (this.resizeOrigin) {
				this.resizeOrigin = null;
				var feature = layer.getFeatureById(marker.linkid);
				this.setPrintRangeNode(((this.dragEvent == "pdf") ? this.printFeature:this.imageRangeFeature));
			}
		}
		catch (e) {
			console.error(e);
		}
	},
	/** 開始点と終了店の外接矩形内にfeatureをリサイズ
	 * @param feature リサイズするFeature
	 * @param originGeometry ドラッグ開始時の地図座標範囲 前回のリサイズ時のマウス位置も記録する
	 * @param lonlat マウス位置の地図座標
	 * @param layer 対象レイヤ
	 * @param tag タグ {math:計算方式}
	 */
	resizeFeature : function(feature, originGeometry, lonlat, layer ,tag)
	{
		try {
			var geometry = feature.geometry;
			var dx0 = originGeometry.prevLon - originGeometry.x;
			var dy0 = originGeometry.prevLat - originGeometry.y;
			var dx1 = lonlat.lon - originGeometry.x;
			var dy1 = lonlat.lat - originGeometry.y;

			originGeometry.prevLon = lonlat.lon;
			originGeometry.prevLat = lonlat.lat;
			var scale ;
			// 縦ベース
			if (tag.math == "y"){
				scale = dy1 / dy0;
			}
			// 横ベース
			else {
				scale = dx1 / dx0;
			}

			if(scale == 0) return;

			var ratio = 1;
			if (this.dragEvent != "pdf"){
				if (tag.pos.length == 2){
					ratio = (dx1 / dx0) / scale;
				}
				else{
					if (tag.math == "y"){
						ratio = 1 / scale;
					}
					else{
						var points = geometry.components;
						var bounds = feature.geometry.getBounds();
						var res = this.eMap.getResolution();
						points[0].y = bounds.bottom;
						points[1].y = bounds.top;
						points[2].y = bounds.top;
						points[3].y = bounds.bottom;
						points[4].y = bounds.bottom;
						if (tag.pos == "l"){
							bounds.left = lonlat.lon;
							points[0].x = bounds.left
							points[1].x = bounds.left;
							points[2].x = bounds.right;
							points[3].x = bounds.right;
							points[4].x = bounds.left;
						}
						else{
							bounds.right = lonlat.lon;
							points[0].x = bounds.left;
							points[1].x = bounds.left;
							points[2].x = bounds.right;
							points[3].x = bounds.right;
							points[4].x = bounds.left;
						}
					}
				}
			}
			if (!(this.dragEvent != "pdf" && (tag.pos == "l" || tag.pos == "r"))){
				geometry.resize(scale, originGeometry, ratio);
			}
			layer.drawFeature(feature);
		}
		catch (e) {
			console.error(e);
		}
	},

	getPageRange : function(bbox)
	{
		var me = this;
		var values = me.printPreviewWindow.getValues();
		
		//<span class="ja">bboxに内接する設定ページ設定の範囲を返却</span>
		//<span class="en">Return the range of page setting inside bbox</span>
		var paperType = values.pagesize.substr(0,1);
		var cols = values.cols;
		var rows = values.rows;
		var yoko = values.rotate=="1";
		//<span class="ja">仮想ページ高さを1としたときの幅を取得</span>
		//<span class="en">Get the width when considering virtual page's height as 1</span>
		var pageWidth = this.paperRate[paperType];
		if (yoko) pageWidth = 1/pageWidth;
		pageWidth = pageWidth*cols / rows;
		
		var w = bbox.getWidth();
		var h = bbox.getHeight();
		if (w/h < pageWidth) {
			//<span class="ja">高さを変える</span>
			//<span class="en">Change the height</span>
			h = w/pageWidth;
			var ch = (bbox.top+bbox.bottom)/2;
			return new OpenLayers.Bounds(bbox.left, ch-h/2, bbox.right,ch+h/2);
		} else {
			//<span class="ja">幅を変える</span>
			//<span class="en">Change the width</span>
			w = h*pageWidth;
			var cw = (bbox.right+bbox.left)/2;
			return new OpenLayers.Bounds(cw-w/2, bbox.bottom, cw+w/2, bbox.top);
		}
	},

	/** <span class="ja">範囲設定ダイアログ表示</span><span class="en">Display dialog to set the range</span> */
	showPdfRangeDialog : function()
	{
		var self = this;
		var printPreviewWindow = this.printPreviewWindow;
		var map = printPreviewWindow.stmap.map;
		if (!this.pdfRangeDialog) {
			var dialog = Ext.create('Ext.window.Window', {
				id: "pdfRangeDialog",
				title:lang.__('印刷範囲設定'),
				bodyStyle: {
					"padding": "5px",
					"text-align": "center",
					"background-Color": "white"
				},
				html: Ext.DomHelper.createHtml({
					tag: "div",
					children: [{
						tag: "div",
						html: '<span style="font-size:0.8em;white-space:nowrap;">'+
							lang.__('赤い枠をドラッグすると出力範囲が移動し、<br>各ノードをドラッグすると出力範囲の変更が出来ます。')+
							'</span>'
					}]
				}),
				closeAction: "hide",
				buttonAlign: "center",
				buttons: [{
					text: lang.__('範囲設定終了'),
					handler: function() {
						dialog.hide();
					}
				}]
			});
			dialog.on("hide",  function(){ self._hidedPdfRangeDialog(); });
			this.pdfRangeDialog = dialog;
		}

		// 地図画面の中央に表示
		this.pdfRangeDialog.show();
		var x = -this.pdfRangeDialog.getWidth()/2;
		var y = -this.pdfRangeDialog.getHeight()/2;
		this.pdfRangeDialog.alignTo("map", "c", [x, y]);
		
		this.dragEvent = "pdf";
		this.moveControl = new OpenLayers.Control.DragFeature(this.pdfRangeLayer,{
			dragCallbacks  : {
				down : function(e){
					self.moveFeatureStart(e);
					this.downFeature(e);
				},
				up : function(e){
					this.upFeature(e);
					self.moveFeatureEnd(e);
				}
			}
		});
		this.map.addControl(this.moveControl);
		this.moveControl.activate();
		this.setPrintRangeNode(this.printFeature);
		this.pdfRangeLayer.setVisibility(true);
		this.moveToFrontLayer(this.pdfRangeLayer);
		this.moveToFrontLayer(this.printRangeNodeLayer);
		this.pdfRangeLayer.setVisibility(true);
		this.printRangeNodeLayer.setVisibility(true);
	},
	moveFeatureStart : function(e){
		if (this.printRangeNodeMaker.length){
			this.printRangeNodeLayer.clearMarkers();
		}
	},
	moveFeatureEnd : function(e){
		this.setPrintRangeNode(this.printFeature);
	},
	moveImageFeatureStart : function(e){
		if (this.printRangeNodeMaker.length){
			this.printRangeNodeLayer.clearMarkers();
		}
	},
	moveImageFeatureEnd : function(e){
		this.setPrintRangeNode(this.imageRangeFeature);
	},
	/** <span class="ja">範囲設定ダイアログ非表示</span><span class="en">Hide the range setting dialog</span> */
	_hidedPdfRangeDialog : function()
	{
		var self = this;
		var printPreviewWindow = this.printPreviewWindow;
		var map = printPreviewWindow.stmap.map;
		printPreviewWindow.win.show();
		if (this.printRangeNodeMaker.length){
			this.printRangeNodeLayer.clearMarkers();
		}
		//<span class="ja">範囲ポリゴンの編集終了</span>
		//<span class="en">Finish editing range polygon</span>
		this.moveControl.deactivate();

		// BBOXを保存
		printPreviewWindow.values.bbox = this.getBBOX();

		// プレビュー画像を更新
		printPreviewWindow.showPreview();
	},

	getBBOX: function() {
		var bbox = null;
		if(!!this.printFeature) {
			var bounds = this.printFeature.geometry.getBounds();
			//if(!bounds) bounds = this.eMap.getBounds();
			//if (bounds) bounds = this.eMap.transformToLonLat(bounds, true);
			//else bounds = this.eMap.getBounds();
			var bbox =
				Math.min(bounds.left, bounds.right)+","+
				Math.min(bounds.bottom, bounds.top)+","+
				Math.max(bounds.left, bounds.right)+","+
				Math.max(bounds.bottom, bounds.top);
		}
		return bbox;
	},

	moveToFrontLayer : function(layer)
	{
		var me = this, self = this;
		var printPreviewWindow = this.printPreviewWindow;
		var map = printPreviewWindow.stmap.map;

		map.setLayerIndex(layer, map.layers.length);
		map.resetLayersZIndex();
	},

	////////////////////////////////////////////////////////////////
	// 印刷範囲保存
	////////////////////////////////////////////////////////////////

	/** <span class="ja">印刷範囲一覧ダイアログを表示する</span><span class="en">Display Search history list dialog</span> */
	showRangeListDialog : function()
	{
		var me = this, self = this, layer=this;
		var printPreviewWindow = me.printPreviewWindow;
		var stmap = printPreviewWindow.stmap;
		var map = stmap.map;

		if (!this.rangeListDialog) {
			var types = Ext.data.Types;
			Ext.define('SaigaiTask.model.PdfRange', {
				extend: 'Ext.data.Model',
				fields: [{
					name: 'id',
					type: types.NUMBER
				}, {
					name: 'name',
					type: types.STRING
				}, {
					name: 'created',
					type: types.STRING
				}, {
					name: 'options',
					type: types.STRING
				}, {
					name: 'range',
					type: types.STRING
				}, {
					name: 'status',
					type: types.INT
				}, {
					name: 'user_id',
					type: types.NUMBER
				}, {
					name: 'user_name',
					type: types.STRING
				}]
			});
			
			var dialog = Ext.create('Ext.window.Window', {
				id: "rangeListDialog",
				title:lang.__("印刷設定読み込み"),
				width: 420,
				height: 300,
				layout: "fit",
				items: [{
					xtype: "grid",
					loadMask: true,
					emptyText: lang.__("保存された印刷設定はありません。"),
					store: {
						model: "SaigaiTask.model.PdfRange",
						autoLoad: true,
						proxy: {
							type: "ajax",
							url: SaigaiTask.contextPath+"/page/map/pdfRange/list",
							reader: {
								type: "json",
								root: "items"
							}
						}
					},
					overflowY: "auto", // scrollbar
					layout: "column",
					columns: [{
						text: lang.__("印刷設定名称"),
						dataIndex: "name",
						width: 255
					}, {
						text: lang.__("保存日時"),
						dataIndex: "created",
						width: 150
					}]
				}],
				closeAction: "hide",
				buttonAlign: "left",
				buttons: [{
					icon: stmap.icon.getURL("folderOpenIconURL"),
					text: lang.__('印刷設定読み込み'),
					handler: function() {
						alert("実装中");
						dialog.hide();
					}
				}, {
					xtype: "checkbox",
					boxLabel: lang.__('印刷範囲のみ')
				}, {
					xtype: "tbfill"
				}, {
					icon: stmap.icon.getURL("deleteIconURL"),
					text: lang.__('削除'),
					handler: function() {
						alert("実装中");
					}
				}, {
					xtype: "tbfill"
				}, {
					icon: stmap.icon.getURL("closeIconURL"),
					text: lang.__('閉じる'),
					handler: function() {
						alert("実装中");
						dialog.hide();
					}
				}],
				listeners: {
					hide: function() {
						// 印刷ダイアログを表示
						printPreviewWindow.win.show();
					}
				}
			});
			this.rangeListDialog = dialog;
			this.rangeListDialog.show();
			var x = -dialog.getWidth()/2;
			var y = -dialog.getHeight()/2;
			this.rangeListDialog.alignTo("map", "c", [x, y]);
		} else {
			this.rangeListDialog.show();
			this.onRangeListSelect();
			//<span class="ja">リスト更新</span><span class="en">Update list</span>
			this.initRangeList();
		}
		this.pdfDialog.domNode.style.display='none';
	},
	/** <span class="ja">検索履歴読み込み一覧ダイアログを閉じる</span><span class="en">Read the search history and close list dialog</span> */
	closeRangeListDialog : function()
	{
		if (this.rangeListDialog) this.rangeListDialog.hide();
		this.pdfDialog.domNode.style.display='';
	},

	CLASS_NAME: "SaigaiTask.Map.Layer.PdfRangeLayer"
});
