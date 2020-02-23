/**
 * 描画レイヤ（地物選択関係）
 * @see ecommap : webapps/map/map/widgets/memo/memo.js
 * @requires SaigaiTask/Map/Layer.js
 */
SaigaiTask.Map.Layer.DrawLayerSelectDrag = new OpenLayers.Class({

	/** 追加時の形状ID 点 */
	POINT : 10,
	/** 追加時の形状ID 線 */
	LINESTRING : 20,
	/** 追加時の形状ID 面 */
	POLYGON : 30,
	/** 追加時の形状ID 文字 */
	TEXT : 40,
	/** 追加時の形状ID 画像 */
	PHOTO : 60,
	/** 追加時の形状ID フォルダ（未使用） */
	GROUP : 100,

	initialize: function(memoLayer) {
		var me = this;
		var stmap = me.stmap;

		//<span class="ja">選択用レイヤ</span><span class="en">Layer for selection</span>
		this.memoSelectLayer = new OpenLayers.Layer.Markers('memoSelect');
		this.stmap.map.addLayer(this.memoSelectLayer);
		this.selectIcon = new OpenLayers.Icon(stmap.icon.getURL("pointnodeIconURL"), new OpenLayers.Size(9,9), new OpenLayers.Pixel(-5,-5));
		this.memoLayer = memoLayer;
		this.eMap = {
			map: this.stmap.map,
			getLabelSize: OpenLayers.Format.KMLStyleUtil.getLabelSize,
			isFocus: this.stmap.isFocus
		}

		//<span class="ja">初期状態の色設定 ボタンの色も変更される</span><span class="en">Set color for initialization    Also change the buttons' color</span>
		//this.setFontColor("#0000FF");
		//this.setDrawColor("#0000FF");
		//this.setFillColor("#FFFFFF");
		//this.setIcon('widgets/memo/icons/pin01.png',31,48,-15,-48);
		this.setFontSize(14);

		//<span class="ja">編集時のスタイル defaultは新規作成時のフォームと合わせる</span><span class="en">Style when editing   In default, comply with the form when creating new </span>
		var modifyStyleMap = me.modifyStyleMap = new OpenLayers.StyleMap();
		modifyStyleMap.styles['default'] = new OpenLayers.Style({
			fillColor:"#0000FF", fillOpacity:0.4,
			hoverFillColor:"#FFFFFF", hoverFillOpacity:0.8,
			strokeColor:"#0000FF", strokeOpacity:1, strokeWidth:2,// strokeLinecap:"round",strokeDashstyle:"dash",
			graphicOpacity:1, 
			graphicZIndex: 0,
			//externalGraphic:"icons/default.png", graphicWidth:20, graphicHeight:40, graphicXOffset:-10, graphicYOffset:-20,
			//externalGraphic:"icons/pointnode.png", graphicWidth:9, graphicHeight:9, graphicXOffset:-5, graphicYOffset:-5,
			//hoverPointRadius:1, hoverPointUnit: "%",
			//pointerEvents:"visiblePainted",
			fontColor:"#0000FF", fontWeight:"bold", fontSize:memoLayer.styleMap.styles.default.defaultStyle.fontSize, labelAlign:"lt",
			cursor:"pointer"
		});

		var selectDragFeature = me.controls.selectDragFeature;
		selectDragFeature.events.on({
			"deactivate": function() {
				// 選択を無効にしたら、選択マーカをクリアする
				me.clearFeatureSelection();
			}
		});
		// 地図を移動・拡縮したら、テキスト編集を終了する
		stmap.map.events.on({
			"moveend": function() {
				if(selectDragFeature.active) {
					$(".memo_text_div textarea").blur();
				}
			}
		})
		

		me.layer.events.on({
			"historychange": function(option) {
				if(option.operation=="undo"
					|| option.operation=="redo" ) {
					// undo/redo時に選択マーカをクリアする
					me.clearFeatureSelection();

					// 選択コントロールが有効な場合、選択中にする
					if(me.activeDrawKey=="selectDragFeature") {
						me.getSelectDragFeatureControl().select(option.feature);

						// undo/redo時に選択マーカをリセットする
						me.resetFeatureSelection(me.memoLayer);
					}
					
				}
			}
		});
	},
	
	////////////////////////////////////////////////////////////////
	//	<span class="ja">選択</span><span class="en">Selection</span>
	////////////////////////////////////////////////////////////////
	isSelected : function(feature)
	{
		var sf = this.memoLayer.selectedFeatures;
		for (var i=sf.length-1; i>=0; i--) {
			if (sf[i] == feature) return true;
		}
		return false;
	},
	/** <span class="ja">SelectFeatureを使わずに選択状態にする</span><span class="en">Set Selection state without using SelectFeature </span> */
	selectFeature : function(feature)
	{
		this.memoLayer.selectedFeatures.push(feature);
		this._featureSelected(feature);
	},
	
	/** <span class="ja">Featureが選択されたときのスタイルを変更 <br/> 複数選択時は一つづつ呼ばれる</span><span class="en">Change the styles used when Features are selected</span> */
	clearFeatureSelection : function()
	{
		this.memoSelectLayer.clearMarkers();
		this.memoLayer.selectedFeatures = [];
	},
	
	/** 選択マーカーをリセット
	 * @param layer {OpenLayers.Layer.Vector} 選択している図形のあるレイヤ */
	resetFeatureSelection : function(layer)
	{
		var selected = layer.selectedFeatures;
		if (selected) {
			for (var i=selected.length-1; i>=0; i--) {
				this._featureUnselected(selected[i]);
				this._featureSelected(selected[i]);
			}
		}
	},
	
	/** SelectFeatureControlから呼ばれる
	 * Featureが選択されたときに選択ノードのマーカー表示
	 * 図形の場合も外説矩形にマーカー表示
	 * 複数選択時はfeatureごとに呼ばれる
	 * @param feature 選択されたFeature */
	_featureSelected : function(feature, noControl)
	{
		try {
			
			// フォーカスを外す
			// ※メモ描画でテキストメモ編集モードのテキストエリアを閉じるために必要
			// 　@see src/main/webapp/js/SaigaiTaskJS/lib/SaigaiTask/Map/Layer/DrawLayerSelectDrag.js _startTextEdit
			if(!!document.activeElement && !!document.activeElement.blur) document.activeElement.blur();
			
			if (feature.geometry.CLASS_NAME == "OpenLayers.Geometry.Point") {
				var vartices = feature.geometry.getVertices();
				for (var i=0; i<vartices.length; i++) {
					this._addSelectMarker(this.dragMarker, feature, new OpenLayers.LonLat(vartices[i].x, vartices[i].y), this.selectIcon.clone(), 'move');
				}
			} else {
				//外接矩形の4点のノード
				var bounds = feature.geometry.getBounds();
				this._addSelectMarker(this.dragMarker, feature, new OpenLayers.LonLat(bounds.left, bounds.top), this.selectIcon.clone()/*, 'nw-resize'*/);
				this._addSelectMarker(this.dragMarker, feature, new OpenLayers.LonLat(bounds.right, bounds.top), this.selectIcon.clone()/*, 'ne-resize'*/);
				this._addSelectMarker(this.dragMarker, feature, new OpenLayers.LonLat(bounds.left, bounds.bottom), this.selectIcon.clone()/*, 'sw-resize'*/);
				this._addSelectMarker(this.dragMarker, feature, new OpenLayers.LonLat(bounds.right, bounds.bottom), this.selectIcon.clone()/*, 'se-resize'*/);
			}
			//選択ノードを前面に表示させる Vector側に追加すると下になるため毎回実行
			//this.eMap.map.resetLayersZIndex();
			this.memoSelectLayer.setZIndex(726);
			
		} catch (e) { console.error(e); }
	},
	/** Featureの選択が解除されたときのスタイルを変更
	 * 複数選択時はfeatureごとに呼ばれる
	 * @param feature 選択解除されたFeature */
	_featureUnselected : function(feature, noControl)
	{
		if (this.dragMarker) this.dragMarker.deactivate();
		var markers = this.memoSelectLayer.markers;
		for (var i=markers.length-1; i>=0; i--) {
			if (markers[i].linkid == feature.id) {
				this.memoSelectLayer.removeMarker(markers[i]);
			}
		}
	},
	/** 選択マーカーを追加.
	 * マーカーのドラッグイベントもここで定義
	 * @param feature 選択されているFeature マーカーの linkidにfeature.idを格納
	 * @param lonlat マーカーの座標（メルカトルならメートル）
	 * @param icon マーカーに表示するアイコン cloneして別のimgDivにしておく
	 * @param cursor マーカー上のカーソル形状 */
	_addSelectMarker : function(control, feature, lonlat, icon, cursor)
	{
		icon.imageDiv.style.cursor = cursor;
		var marker = new OpenLayers.Marker(lonlat, icon);
		marker.linkid = feature.id;
		this.memoSelectLayer.addMarker(marker);
		//FIXME DragMarker.jsでaddMarker()に連動して設定できるようにする
		var self = this;
		if (control) {
			marker.events.register("mouseover", marker, function(){ control.overFeature(this); });
			marker.events.register("mouseout", marker, function(){ control.outFeature(this); });
			if (this.isText(feature)) marker.events.register("dblclick", marker, function(){ self._startTextEdit(feature, true); });
		}
	},
	
	/** 選択ノードドラッグ開始
	 * @parama marker ドラッグ開始されたマーカー*/
	_nodeDragStart : function(marker, layer)
	{
		if (!marker) return;
		var feature = layer.getFeatureById(marker.linkid);
		try {
		this.resizeOrigin = null;
		//外枠取得
		var bounds = feature.geometry.getBounds();
		//始点取得
		var isLeft = Math.abs(bounds.left-marker.lonlat.lon) > Math.abs(bounds.right-marker.lonlat.lon);
		var isBottom = Math.abs(bounds.bottom-marker.lonlat.lat) > Math.abs(bounds.top-marker.lonlat.lat);
		this.resizeOrigin = new OpenLayers.Geometry.Point(
			isLeft ? bounds.left : bounds.right, isBottom ? bounds.bottom : bounds.top
		);
		//Point内の変数に移動前のlonlat格納
		this.resizeOrigin.prevLon = marker.lonlat.lon;
		this.resizeOrigin.prevLat = marker.lonlat.lat;
		this.resizeOrigin.isPoint = feature.geometry.CLASS_NAME == "OpenLayers.Geometry.Point" || this.isText(feature);
		
		//選択解除と他のノードの非表示
		var markers = this.memoSelectLayer.markers;
		for (var i=markers.length-1; i>=0; i--) {
			if (markers[i] != marker) {
				this.memoSelectLayer.removeMarker(markers[i]);
			}
		}
		this.memoLayer.selectedFeatures = [feature];
		
		} catch (e) { console.error(e); }
	},
	/** 選択ノードドラッグ
	 * @parama marker ドラッグ中のマーカー*/
	_nodeDrag : function(marker, layer)
	{
		if (this.resizeOrigin) {
			var lonlat = marker.lonlat;
			var feature = layer.getFeatureById(marker.linkid);
			if (this.resizeOrigin.isPoint) {
				var geometry = feature.geometry;
				feature.geometry.move(lonlat.lon-geometry.x, lonlat.lat-geometry.y);
				layer.drawFeature(feature);
			} else {
				this.resizeFeature(feature, this.resizeOrigin, lonlat, layer);
			}
		}
	},
	/** 選択ノードドラッグ完了
	 * @parama marker ドラッグ完了したマーカー*/
	_nodeDragComplete : function(marker, layer)
	{
		try {
		if (this.resizeOrigin) {
			this.resizeOrigin = null;
			var feature = layer.getFeatureById(marker.linkid);
			this._featureUnselected(feature);
			this._featureSelected(feature);
			
			//図形更新情報設定
			this._featureModified(feature);
		}
		} catch (e) { console.error(e); }
	},
	/** 開始点と終了店の外接矩形内にfeatureをリサイズ
	 * @param feature リサイズするFeature
	 * @param originGeometry ドラッグ開始時の地図座標範囲 前回のリサイズ時のマウス位置も記録する
	 * @param lonlat マウス位置の地図座標 */
	resizeFeature : function(feature, originGeometry, lonlat, layer)
	{
		try {
			var geometry = feature.geometry;
			var dx0 = originGeometry.prevLon - originGeometry.x;
			var dy0 = originGeometry.prevLat - originGeometry.y;
			var dx1 = lonlat.lon - originGeometry.x;
			var dy1 = lonlat.lat - originGeometry.y;
			if (dx0 == 0 || dy0 == 0 || dx1 == 0 || dy1 == 0) return;
			originGeometry.prevLon = lonlat.lon;
			originGeometry.prevLat = lonlat.lat;
			var scale = dy1 / dy0;
			var ratio = (dx1 / dx0) / scale;
			//console.log(dx1+" , "+dy1+" , "+dx0+" , "+dy0+" , "+scale+" , "+ratio);
			geometry.resize(scale, originGeometry, ratio);
			layer.drawFeature(feature);
			//Point内の変数に移動前のlonlat格納
		} catch (e) { console.error(e); }
	},
	
	/** 地図画面上でキーが押された時の処理
	 * 選択Featureの移動と削除 */
	_onKeyDown : function(evt)
	{
		var offset = 4 * this.memoLayer.getResolution();
		switch (evt.keyCode) {
		case dojo.keys.LEFT_ARROW:
			this.moveSelectedFeature(-offset, 0); break;
		case dojo.keys.DOWN_ARROW:
			this.moveSelectedFeature(0, -offset); break;
		case dojo.keys.RIGHT_ARROW:
			this.moveSelectedFeature(offset, 0); break;
		case dojo.keys.UP_ARROW:
			this.moveSelectedFeature(0, offset); break;
		case dojo.keys.DELETE:
			//削除 コントロールでマウス下のFeatureもクリア
			this.removeSelectedFeature();
			break;
		}
		//var control = this.controls[this.editMode];
		//if (control.outFeatureByKey) control.outFeatureByKey();
	},
	
	/** 選択されているFeatureを移動
	 * @param xOffset 横移動量 マイナスが左
	 * @param yOffset 縦移動量 マイナスが上 */
	moveSelectedFeature : function(xOffset, yOffset)
	{
		var me = this;
		var selected = this.memoLayer.selectedFeatures;
		for (var i=selected.length-1; i>=0; i--) {
			var feature = selected[i];
			var geometry = feature.geometry;
			if (geometry) {
				var beforeFeature = me.cloneTextFeature(feature);
				
				geometry.move(xOffset, yOffset);
				this.memoLayer.drawFeature(feature);
				this._featureUnselected(feature);
				this._featureSelected(feature);
				
				
				// 地物の移動イベントをトリガーする
				var event = {
					type: "featuremoved",
					feature: feature,
					afterFeature: me.cloneTextFeature(feature),
					beforeFeature: beforeFeature
				};
				me.layer.events.triggerEvent(event.type, event);
			}
		}
	},

	/** <span class="ja">凡例と地図からFeature削除</span><span class="en">Delete Feature from legend and map</span>
	 * @return 選択済が無い場合はtrue */
	removeSelectedFeature : function(force)
	{
		try {
			if(typeof force=="undefined") force=false;
			var features = this.memoLayer.selectedFeatures;
			//console.log(features);
			if (features.length == 0) return true;
			
			if (force==false && !confirm(lang.__('Delete selected note?'))) {
				return;
			}

			/*
			//<span class="ja">変形中</span><span class="en">Changing shape</span>
			if (features.length == 1) {
				switch (this.editMode) {
				case 'MODIFY':
				case 'RESIZE':
				case 'ROTATE':
					this.controls[this.editMode].unselectFeature(features[0]);
					break;
				}
			}
			*/
			this.clearFeatureSelection();
			this.memoLayer.removeFeatures(features);

			/*
			//<span class="ja">リスト更新</span><span class="en">Update list</span>
			if (this.dataGrid) this.showList();
			*/
			
			} catch (e) { console.error(e); }
	},

	////////////////////////////////////////////////////////////////
	//	<span class="ja">メモ編集モード</span><span class="en">Memo editing mode</span>
	preExButton : null,
	
	/**
	 * @param mode {String} <span class="ja">mode文字列 nullならすべて無効</span><span class="en">"mode" string  If null, disable all</span>
	 * @param mode style 追加指定ボタンでのスタイル指定
	 * @param mode exButton 追加指定ボタン トグル制御用
	 * */
	changeEditControl : function(mode, style, exButton)
	{
		var me = this;
		if(mode=="SELECT") {
			me.setSelectDragFeatureControlActivation(true);
			// テキスト入力モードでは、地図クリック時にポップアップを出さないようにする
			me.stmap.clickHandler.deactivate();
			// 遅延させて地図クリックを有効に戻す
			// ※遅延させないと、このクリックイベントも拾われてしまうため
			setTimeout(function() {
				me.stmap.clickHandler.activate();
			}, 500);		
		}
		/*
		//<span class="ja">同じなら何もしない</span><span class="en">Do nothing if modes are the same</span>
		if ((this.editMode == mode || !exButton) && exButton && this.preExButton == exButton) {
			//this.editMode = null;
			return;
		}
		
		try {
		//スタイル指定
		if (style) {
			if (style.icon) this.setIcon(style.icon, style.size[0], style.size[1], style.size[2], style.size[3]);
			if (style.width) this.setLineWidth(style.width);
			if (style.color) this.setDrawColor(style.color);
			if (style.fill) this.setFillColor(style.fill);
			if (style.opacity) this.setFillOpacity(style.opacity);
			if (style.fontcolor) this.setFontColor(style.fontcolor);
			if (style.fontsize) this.setFontSize(style.fontsize);
		}
		
		//<span class="ja">選択削除</span><span class="en">Clear the selection</span>
		if ((this.editMode=='SELECT' || this.editMode=='SELECTBOX') && (mode == 'SELECT' || mode == 'SELECTBOX')) {
		} else {
			this.clearFeatureSelection();
		}
		
		for(key in this.controls) {
			this.controls[key].deactivate();
			
			var button = this.modeButtons[key];
			if (button) button.domNode.className = "toggle_button";
		}
		if (this.preExButton) {
			this.preExButton.domNode.className = "toggle_button";
			this.preExButton = null;
		}
		
		this.editMode = mode;
		
		if (mode && this.controls[mode]) {
			this.controls[mode].activate();
			
			if (exButton) {
				exButton.domNode.className = "toggle_button toggled";
				this.preExButton = exButton;
			}
			else this.modeButtons[mode].domNode.className = "toggle_button toggled";
			
			//<span class="ja">モードに応じたスタイルに変更</span><span class="en">Change the styles which are suitable to the mode</span>
			
			var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
			
			switch (mode) {
			case 'SELECT':
			case 'REMOVE':
				//this.setStyleGraphic(defaultStyle, "icons/pointnode.png", 9, 9, -5, -5);
				break;
			case 'LINE':
			case 'POLYGON':
			case 'MODIFY':
			case 'PHOTO':
				this.setStyleGraphic(defaultStyle, "icons/pointnode.png", 9, 9, -5, -5, 1);
				break;
			case 'RESIZE':
				this.setStyleGraphic(defaultStyle, "icons/resize.png", 24, 24, -12, -12, 1);
				break;
			case 'ROTATE':
				this.setStyleGraphic(defaultStyle, "icons/rotate.png", 24, 24, -12, -12, 1);
				break;
			}
			
			//<span class="ja">メモレイヤが非表示なら表示する</span><span class="en">If the memo layer is invisible, display it</span>
			dijit.byId("memoVisible").set('checked',true);
			
		}
		} catch (e) { console.error(e); }
		*/
	},
	/** <span class="ja">OpenLayers.Feature.Vectorのスタイルのgraphicを設定（コード短縮用）</span><span class="en">Set graphic of the OpenLayers.Feature.Vector's style (to reduce code)</span>
	 * @pram style <span class="ja">Graphicを設定するOpenLayers.Feature.Vectorのスタイル</span><span class="en">OpenLayers.Feature.Vector's style which sets the Graphic</span> */
	setStyleGraphic : function(style, externalGraphic, graphicWidth, graphicHeight, graphicXOffset, graphicYOffset, graphicOpacity)
	{
		style.externalGraphic = externalGraphic;
		style.graphicWidth = graphicWidth;
		style.graphicHeight = graphicHeight;
		style.graphicXOffset = graphicXOffset;
		style.graphicYOffset = graphicYOffset;
		if (graphicOpacity) style.graphicOpacity = graphicOpacity;
	},
	
	/** <span class="ja">メモの形状名称を返却</span><span class="en">Return geometry name of the memo</span> */
	getGeomName : function(feature)
	{
		var geom = feature.geometry;
		if (!geom) return lang._('Nothing');
		if (geom.CLASS_NAME.match(/Point$/)) {
			if (this.isText(feature)) return lang._('Font');
			return lang._('Point<!--2-->');
		}
		if (geom.CLASS_NAME.match(/LineString$/)) return lang._('Line');
		if (geom.CLASS_NAME.match(/Polygon$/)) return lang._('Polygon');
	},
	
	////////////////////////////////////////////////////////////////
	//	<span class="ja">テキスト編集</span><span class="en">Edit text</span>
	////////////////////////////////////////////////////////////////
	isText : function(feature)
	{
		return feature.style.fontColor && feature.style.label;
	},
	checkTextEdit : function(feature)
	{
		if (this.isText(feature)) {
			if (this.isSelected(feature)) this._startTextEdit(feature, true); 
		}
	},
	/** テキスト編集を終了しないフラグ */
	_textNoClose : false,
	_startTextEdit : function(feature, modified)
	{
		var me = this;
		try {
		//<span class="ja">追加時はモード変更</span><span class="en">Change mode when inserting</span>
		if (!modified) this.changeEditControl('SELECT');
		
		var self = this;
		
		var parentDiv = this.eMap.map.div.parentNode;
		var px = this.eMap.map.getViewPortPxFromLonLat(new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y));
		
		var div = dojo.create("div", {className:"memo_text_div"});
		div.style.left = px.x+"px";
		div.style.top = px.y+"px";
		
		//文字サイズ連動設定を取得
		var textFixed = false;
		if (feature.attributes && feature.attributes.description) {
			try {
				var option = dojo.fromJson(feature.attributes.description);
				if (option.reso) textFixed = true;
				// デバッグ用ログ出力
				if (option.reso) console.log("reso: "+option.reso+", zoom:"+me.stmap.map.getZoomForResolution(option.reso));
			} catch (e) {}
		}
		
		//<span class="ja">テキストエリア追加</span><span class="en">Insert textarea element</span>
		var textarea = this.textarea = me.createExtTextArea({
			rows: 1,
			cols: 10,
			trim: true,
			value: modified ? feature.attributes.name.replace(/\\n/g, "\n") : null,
			div: div
		});
		var textChk = document.createElement("input"); textChk.type="checkbox"; textChk.checked=textFixed;// new dijit.form.CheckBox({checked:textFixed});
		var label = dojo.create("label", {style:"background-color:white; cursor:pointer;"});
		var labelDiv = dojo.create("div");
		var span = dojo.create("span",{innerHTML:lang.__("Resize with map scale"), style:"padding:0 2px;"});
		label.appendChild(textChk);
		label.appendChild(span);
		labelDiv.appendChild(label);
		div.appendChild(labelDiv);
		//クリックで閉じないようにフラグを立てる
		span.onmouseover = function(){ self._textNoClose = true; };
		textChk.onmouseover = span.onmouseover;
		span.onmouseout = function(){ self._textNoClose = false; textarea.focus(); };
		textChk.onmouseout = span.onmouseout;
		// 縮尺更新のチェックボックスを追加
		if(modified && textFixed) {
			var updateChk = document.createElement("input"); updateChk.type="checkbox"; updateChk.checked=false;
			var updateLabel = dojo.create("label", {style:"background-color:white; cursor:pointer;"});
			var updateLabelDiv = dojo.create("div");
			var updateSpan = dojo.create("span",{innerHTML:lang.__("Update scale"), style:"padding:0 2px;"});
			var s=document.createElement("span"); s.innerHTML="("; updateLabel.appendChild(s);
			updateLabel.appendChild(updateSpan);
			updateLabel.appendChild(updateChk);
			var s=document.createElement("span"); s.innerHTML=")"; updateLabel.appendChild(s);
			labelDiv.appendChild(updateLabel);
			div.appendChild(updateLabelDiv);
			updateChk.onmouseover = span.onmouseover;
			updateChk.onmouseout = span.onmouseout;
			updateSpan.onmouseover = span.onmouseover;
			updateSpan.onmouseout = span.onmouseout;
		}
		
		//テキストからフォーカスが外れたら編集終了
		textarea.onblur = function(){ if (self._textNoClose) return; self._endTextEdit(div, textarea, textChk, feature, modified, updateChk); };
		
		parentDiv.appendChild(div);
		textarea.focus();
		
		} catch (e) { console.error(e); }
	},
	_endTextEdit : function(div, textarea, textChk, feature, modified, updateChk)
	{
		var me = this;
		var beforeFeature = me.cloneTextFeature(feature);

		try {
		var parentDiv = this.eMap.map.div.parentNode;
		var label = textarea.value;
		var textFixed = textChk.checked;
		var textFixedUpdate = !!updateChk ? (updateChk.checked||!textFixed) : true;
		//textarea.destroy();
		//textChk.destroy();
		parentDiv.removeChild(div);
		
		//<span class="ja">空文字なら削除して終了</span><span class="en">Delete the spaces and finish</span>
		if (!label || dojo.trim(label) == "") {
			this.clearFeatureSelection();
			this.memoLayer.removeFeatures(feature);
			return;
		}
		
		feature.attributes.name = label;
		if (modified) {
			this.memoLayer.eraseFeatures([feature]);
			this._featureModified(feature);
			//<span class="ja">改行がXMLでは消えるので "\\n" に変更</span><span class="en">Because the breakline characters in XML are ignored, convert them to "\\n"</span>
			feature.attributes.name = feature.attributes.name.replace(/\n/g, "\\n");
			//<span class="ja">選択用の背景</span><span class="en">Background of the selection</span>
			this.setLabelBackground(feature);
			feature.style.label = feature.attributes.name;
			this.memoLayer.drawFeature(feature);
		}
		else {
			//<span class="ja">追加前に選択削除</span><span class="en">Remove the selection before inserting</span>
			this._featureAdded(this.TEXT, feature);
		}
		
		//連動設定
		if(textFixedUpdate) {
			this.setTextZoomOption(feature.attributes, false, !textFixed);
		}
		
		} catch (e) { console.error(e); }
		
		var afterFeature = me.cloneTextFeature(feature);

		if(me.isTextChanged(beforeFeature, afterFeature)) {
			// 地物の移動イベントをトリガーする
			var event = {
				type: "featuremoved",
				feature: feature,
				afterFeature: afterFeature,
				beforeFeature: beforeFeature
			};
			me.layer.events.triggerEvent(event.type, event);
		}

		// 再描画
		me.layer.redraw();
	},
	
	isTextChanged: function(feature1, feature2) {
		var getTextFixed = function(feature) {
			//文字サイズ連動設定を取得
			var textFixed = false;
			if (feature.attributes && feature.attributes.description) {
				try {
					var option = dojo.fromJson(feature.attributes.description);
					if (option.reso) textFixed = option.reso;
				} catch (e) {}
			}
			return textFixed;
		};
		var getTextLabel = function(feature) {
			var label = null;
			if (feature.attributes && feature.attributes.name) {
				label = feature.attributes.name;
			}
			return label;
		};
		if(getTextLabel(feature1)!=getTextLabel(feature2)) return true;
		if(getTextFixed(feature1)!=getTextFixed(feature2)) return true;
		return false;
	},

	cloneTextFeature: function(feature) {
		var clone = null;
		if(!!feature) {
			clone = feature.clone();
			// copy data
			clone.data = {};
			for(var key in feature.data) {
				clone.data[key] = feature.data[key];
			}
			// copy style
			clone.style = {};
			for(var key in feature.style) {
				clone.style[key] = feature.style[key];
			}
		}
		return clone;
	},
	createExtTextArea: function(option) {
		/*
		var textarea = document.createElement("textarea");
		if(option.value!=null) textarea.value = option.value;
		option.div.append(textarea);
		*/
		var exTextarea = Ext.create("Ext.form.field.TextArea", {
			//grow: true, // automatically grow and shrink to its content
			//rows: option.rows,
			//cols: option.cols,
			//resizable: true,
			renderTo: option.div,
			value: option.value
		});
		var textarea = exTextarea.inputEl.dom;
		var basic = Ext.create('Ext.create', 'Ext.resizer.Resizer', {
	        target: exTextarea.inputEl,
	        width: 200,
	        height: 100,
	        minWidth:50,
	        minHeight:20
		});
		
		return textarea;
	},
	
	setLabelBackground : function(feature)
	{
		var fontSize = parseInt(feature.style.fontSize);
		if (!fontSize) try { feature.style.fontSize.replace(/px$/,""); } catch (e) {};
		if (!fontSize) this.kmlFormat.FONTSIZE;
		var size = this.eMap.getLabelSize(feature.attributes.name.replace(/\\n/g, "<br/>"), fontSize, feature.style.fontWeight);
		this.setStyleGraphic(feature.style, null, size.w, size.h, 0, 0);
	},
	
	////////////////////////////////////////////////////////////////
	//	<span class="ja">追加 削除 変更</span><span class="en">Insert  Delete  Change</span>
	////////////////////////////////////////////////////////////////
	/** <span class="ja">凡例と地図のFeatureが追加された時の処理 <br/> (作図コントロールでmemoLayerには追加されている)</span><span class="en">Processing when legend and map's features are inserted</span>
	 * @param type <span class="ja">ジオメトリ形状のタイプ</span><span class="en">Type of geometry shapes</span>
	 * @param feature <span class="ja">追加されたOpenLayers.Feature.Vector</span><span class="en">The inserted OpenLayers.Feature.Vector</span> */
	_featureAdded : function(type, feature)
	{
		console.log(feature);
		
		//<span class="ja">地図のメモレイヤにフィーチャ追加</span><span class="en">Insert feature into map's memo layer</span>
		//feature.fid = fid;
		feature.attributes.description = "{created:"+new Date().getTime()+"}";
		
		//<span class="ja">デフォルトスタイルをFeatureのスタイルにコピー</span><span class="en">Copy default styles to Feature's styles</span>
		//デフォルトスタイルをFeatureのスタイルにコピー
		if (type != this.PHOTO) {
			var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
			feature.style = dojo.clone(defaultStyle);
		}
		feature.style.memo = true; //メモFeatureフラグ設定
		
		//<span class="ja">個別スタイルを調整</span><span class="en">Adjust the styles individually</span>
		switch (type) {
		case this.POINT:
			var style = this.iconStyle;
			//選択済のアイコンサイズで追加
			if (this.iconSize > 0) {
				var scale = this.iconSize/Math.max(style.graphicWidth, style.graphicHeight);
				style.graphicWidth *= scale;
				style.graphicHeight *= scale;
				style.graphicXOffset *= scale;
				style.graphicYOffset *= scale;
			}
			this.setStyleGraphic(feature.style, style.externalGraphic, style.graphicWidth, style.graphicHeight, style.graphicXOffset, style.graphicYOffset);
			//KML出力用のscaleをstyle.graphicScaleに設定
			this._adjustIconScale(feature.style);
			this.memoLayer.drawFeature(feature);
			break;
		case this.PHOTO:
			this.memoLayer.drawFeature(feature);
			break;
		case this.TEXT:
			//<span class="ja">改行がXMLでは消えるので "\\n" に変更</span><span class="en">Because the breakline characters in XML are ignored, convert them to "\\n"</span>
			feature.attributes.name = feature.attributes.name.replace(/\n/g, "\\n");
			//<span class="ja">選択用の背景</span><span class="en">Background of the selection</span>
			this.setLabelBackground(feature);
			feature.style.label = feature.attributes.name;
			//官民修正：新規登録時の線幅をユーザが選択した太さになるようにした。
			//feature.style.strokeWidth = 1;//<span class="ja">線幅は最初は１</span><span class="en">Set initialized stroke width to 1</span>
			feature.style.externalGraphic = null;
			this.memoLayer.drawFeature(feature);
			break;
		case this.LINESTRING:
		case this.POLYGON:
			this.setStyleGraphic(feature.style, null, null, null, null, null);
			break;
		}
		
		//<span class="ja">追加中のFeatureに設定</span><span class="en">Set to adding Feature</span>
		this.addingFeature = feature;
		
		//<span class="ja">モード変更</span><span class="en">Change mode</span>
		//this.changeEditControl('SELECT');
		
		this.clearFeatureSelection();
		this.selectFeature(feature);
		
		//<span class="ja">リスト更新</span><span class="en">Update the list</span>
		if (this.dataGrid) this.showList();
	},

	/** <span class="ja">フィーチャ変更時に呼び出し</span><span class="en">Call this function when modifying feature</span>
	 * @param <span class="ja">変更されたOpenLayers.Feature.Vector </span><span class="en">The modified OpenLayers.Feature.Vector</span>*/
	_featureModified : function(feature)
	{
		var json = {};
		try { json = dojo.fromJson(feature.attributes.description); } catch (e) {}
		json.modified = new Date().getTime();
		feature.attributes.description = dojo.toJson(json);
		//<span class="ja">リスト更新</span><span class="en">Update list</span>
		if (this.dataGrid) this.showList();
	},

	/** 選択中のフィーチャの更新情報を設定し図形一覧も更新
	 *  フィーチャ変更時に呼び出し
	 * @param 変更されたOpenLayers.Feature.Vector */
	_selectedFeaturesModified : function()
	{
		var selected = this.memoLayer.selectedFeatures;
		for (var i=selected.length-1; i>=0; i--) {
			this._featureModified(selected[i], true);
		}
		//リスト更新
		if (this.dataGrid) this.showList();
	},
	
	
	////////////////////////////////////////////////////////////////
	//	<span class="ja">スタイル</span><span class="en">Style</span>
	////////////////////////////////////////////////////////////////
	/** <span class="ja">最前面または最背面に移動</span><span class="en">Move to top or bottom</span> */
	changeOrderToEnds :function(toTop)
	{
		try {
			var f = this.memoLayer.features;
			var sf = this.memoLayer.selectedFeatures;
			var sfMap = {};
			for (var i=sf.length-1; i>=0; i--) {
				sfMap[sf[i].id] = true;
			}
			
			if (toTop) {
				f.sort(function(a,b){
					return (sfMap[a.id]?1:0) - (sfMap[b.id]?1:0);
				});
			} else {
				f.sort(function(b,a){
					return (sfMap[a.id]?1:0) - (sfMap[b.id]?1:0);
				});
			}
			//<span class="ja">再描画</span><span class="en">Redraw</span>
			this.memoLayer.eraseFeatures(f);
			for (var i=0; i<f.length; i++) {
				this.memoLayer.drawFeature(f[i]);
			}
		} catch (e) { console.error(e); }
	},
	
	/** <span class="ja">図形種別変更 </span><span class="en">Set shape type</span>*/
	setShapeType : function(mode, iconClass)
	{
		this.shapeType = mode;
		dijit.byId('memoSHAPE').iconNode.className = "dijitReset dijitInline "+iconClass;
		this.changeEditControl(mode);
	},
	
	/** <span class="ja">アイコン変更</span><span class="en">Set icon</span> */
	setIcon : function(iconURL, w, h, x, y)
	{
		//<span class="ja">アイコンスタイル設定</span><span class="en">Set icon styles</span>
		this.iconStyle = {externalGraphic:iconURL, graphicWidth:w, graphicHeight:h, graphicXOffset:x, graphicYOffset: y};
		
		//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i in features) {
			var feature = features[i];
			var style = feature.style;
			//点のみ
			if (feature.geometry.CLASS_NAME.match(/Point$/)) {
				if (!this.isText(feature) && !this.isPhoto(feature)) {
					var scale = 1;
					if (style.externalGraphic) {
						//変更前のサイズ
						var size = Math.max(style.graphicWidth, style.graphicHeight);
						//アイコン変更＋サイズ変更
						var scale = size/Math.max(w, h);
					}
					style.externalGraphic = iconURL;
					style.graphicWidth = w*scale;
					style.graphicHeight = h*scale;
					style.graphicXOffset = x*scale;
					style.graphicYOffset = y*scale;
					this.memoLayer.drawFeature(features[i]);
					this._adjustIconScale(style);
				}
			}
		}
		
		/*
		//var iconUrl = "url("+iconFile.replace(/\.png$/, "_s.png")+")";
		var url = "url("+iconURL+")";
		dijit.byId('memoIcon').iconNode.style.backgroundImage = url;
		dijit.byId('memoPOINT').iconNode.style.backgroundImage = url;
		dijit.byId('memoIconList').onCancel();
		*/
	},
	
	/** アイコンサイズ変更
	 * @param size アイコンサイズ */
	setIconSize : function(size)
	{
		if (size > 0) {
		//追加時のアイコンサイズ
		this.iconSize = size;
		
		//選択済みのFeatureの文字サイズを設定
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			//点のみアイコンサイズ変更
			if (features[i].geometry.CLASS_NAME.match(/Point$/)) {
				var style = features[i].style;
				if (style.externalGraphic) {
					//変更前アイコンサイズ
					var scale = size/Math.max(style.graphicWidth, style.graphicHeight);
					console.log(scale);
					style.graphicWidth *= scale;
					style.graphicHeight *= scale;
					style.graphicXOffset *= scale;
					style.graphicYOffset *= scale;
					style.graphicScale *= scale;
				} else {
					style.pointRadius = size;
				}
				this.memoLayer.drawFeature(features[i]);
			}
		}
		//var icon = dojo.byId('memoIconSize').firstChild;
		//icon.className = icon.className.replace(/iconSize\d+/, "iconSize"+size);
		}
	},
	
	/** KML出力用のアイコンのscaleをstyle.graphicScaleに設定 */
	_adjustIconScale : function(style)
	{
		var img = new Image();
		img.onload = function() {
			style.graphicScale = Math.max(style.graphicWidth)/this.width;
		};
		img.src = style.externalGraphic;
		if (img.width) {
			style.graphicScale = Math.max(style.graphicWidth)/img.width;
		}
	},

	/**
	 * @since SaigaiTask-2.2
	 */
	setSelectedFeatureStyle: function(changeStyleFunc) {
		var me = this;
		var memoLayer = me.memoLayer;
		var features = memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			var f = features[i];
			var beforeFeature = me.cloneTextFeature(f);
			changeStyleFunc(f);
			memoLayer.drawFeature(f);
	        me.layer.events.triggerEvent("featuremodified", {
	        	type: "featuremodified",
	        	feature: f,
	        	afterFeature: me.cloneTextFeature(f),
	        	beforeFeature: beforeFeature
	        });
		}
	},
	
	/** <span class="ja">文字色変更</span><span class="en">Change text color</span>
	 * @param color <span class="ja">#RRGGBB 形式の文字列</span><span class="en">String in #RRGGBB format</span> */
	setFontColor : function(color)
	{
		var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
		//<span class="ja">デフォルトに設定</span><span class="en">Set default value</span>
		defaultStyle.fontColor = color;
		
		//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			var f = features[i];
			f.style.fontColor = color;
			this.memoLayer.drawFeature(f);
		}
		dijit.byId('memoFontColor').iconNode.style.backgroundColor = color;
	},
	/** <span class="ja">線色変更</span><span class="en">Change stroke color</span>
	 * @param color <span class="ja">#RRGGBB 形式の文字列</span><span class="en">String in #RRGGBB format</span> */
	setDrawColor : function(color)
	{
		var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
		//<span class="ja">デフォルトに設定</span><span class="en">Set default value</span>
		defaultStyle.strokeColor = color;
		
		//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			var f = features[i];
			f.style.strokeColor = color;
			this.memoLayer.drawFeature(f);
		}
		dijit.byId('memoDrawColor').iconNode.style.backgroundColor = color;
	},
	/** <span class="ja">線幅変更</span><span class="en">Change stroke width</span>
	 * @param width <span class="ja">0～10の整数</span><span class="en">Integer in 0～10</span> */
	setLineWidth : function(width)
	{
		var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
		//<span class="ja">デフォルトに設定</span><span class="en">Set default value</span>
		defaultStyle.strokeWidth = width;
		
		//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			features[i].style.strokeWidth = width;
			this.memoLayer.drawFeature(features[i]);
		}
		//var icon = dojo.byId('memoLineWidth').firstChild;
		//icon.className = icon.className.replace(/lineWidth\d+/, "lineWidth"+Math.min(10, width));
	},
	/** <span class="ja">面の色変更</span><span class="en">Change area color</span>
	 * @param color <span class="ja">#RRGGBB 形式の文字列</span><span class="en">String in #RRGGBB format</span> */
	setFillColor : function(color)
	{
		var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
		//<span class="ja">デフォルトに設定</span><span class="en">Set default value</span>
		defaultStyle.fillColor = color;
		//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			var style = features[i].style;
			style.fillColor = color;
			//<span class="ja">if (style.label) style.externalGraphic = "/map/texture?fg="+style.fillColor.substring(1);
			this.memoLayer.drawFeature(features[i]);
		}
		//dojo.byId('memoFillColor').firstChild.style.backgroundColor = color;
	},
	/** <span class="ja">塗潰し不透明度</span><span class="en">Set fill opacity</span>
	 * @param opacity 0.0～1.0 */
	setFillOpacity : function(opacity)
	{
		var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
		//<span class="ja">デフォルトに設定</span><span class="en">Set default value</span>
		defaultStyle.fillOpacity = opacity;
		
		//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			features[i].style.fillOpacity = opacity;
			this.memoLayer.drawFeature(features[i]);
		}
	},
	/** <span class="ja">線幅変更</span><span class="en">Change stroke width</span>
	 * @param width <span class="ja">0～10の整数</span><span class="en">Integer in 0～10</span> */
	setFontSize : function(size)
	{
		var defaultStyle = this.memoLayer.styleMap.styles['default'].defaultStyle;
		//<span class="ja">デフォルトに設定</span><span class="en">Set default value</span>
		defaultStyle.fontSize = size;
		
		//<span class="ja">選択済みのFeatureの文字サイズを設定</span><span class="en">Set font size for the Features which are already selected</span>
		var features = this.memoLayer.selectedFeatures;
		for (var i=0; i<features.length; i++) {
			var f = features[i];
			if (f.attributes.name) {
				if (f.attributes.description) {
					this.setTextZoomOption(f.attributes, true);
				}
				f.style.fontSize = size;
				this.setLabelBackground(f);
				this.memoLayer.drawFeature(f);
			}
		}
		//var icon = dojo.byId('memoFontSize').firstChild;
		//icon.className = icon.className.replace(/fontSize\d+/, "fontSize"+size);
	},
	/** テキストサイズ連動のオプションをdescriptionに設定 */
	setTextZoomOption : function(attributes, update, remove)
	{
		try {
		var option = {};
		try { option = dojo.fromJson(attributes.description); } catch (e) {}
		//基準解像度設定
		if (update) {
			//設定されている場合は解像度更新
			if (option.reso) option.reso = this.memoLayer.getResolution();
		} else {
			//有効と無効を切り替える
			if (remove) {
				if (option.reso) delete option.reso;
			} else {
				option.reso = this.memoLayer.getResolution();
			}
		}
		attributes.description = dojo.toJson(option);
		attributes.option = option; //レンダラー利用中のoptionも更新
		} catch (e) { console.error(e); }
	},
	
	/** <span class="ja">凡例名称と地図のFeatureスタイルを更新</span><span class="en">Update styles of legend name and map's Feature</span> */
	updateStyle : function(type, feature, form)
	{
		//<span class="ja">地図のメモレイヤのフィーチャ更新</span><span class="en">Change feature of map's memo layer</span>
		
		
		this.changeEditControl(null);
		
		//<span class="ja">KMLに保存</span><span class="en">Save to KML</span>
		
	},

	CLASS_NAME: "SaigaiTask.Map.Layer.DrawLayerSelectDrag"
});