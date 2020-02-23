/**
 *
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.DrawToolbar = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.DrawToolbar.prototype = {

	/**
	 * @type {SaigaiTask.Map}
	 */
	map: null,
	tbar: null,
	tbarItems: null,

	/**
	 * @type {SaigaiTask.Map.Layer.DrawLayer}
	 */
	drawLayer: null,

	options: null,

	initialize: function(map, toolbarOptions) {
		var me = this;
		me.map = map;
		me.drawLayer = new SaigaiTask.Map.Layer.DrawLayer(map);
		me.tbarItems = me.initDrawToolbarItems(toolbarOptions);
		if(me.options.init) {
			me.tbar = me.getSimpleToolbar();
		}

		me.loadFeatureGeometry();
	},

	/**
	 * ツールバーの描画
	 */
	getSimpleToolbar: function() {
		var me = this;
		if(me.tbar==null) {
			me.tb = Ext.create("Ext.toolbar.Toolbar", {
				items: me.tbarItems,
				renderTo: me.options.renderTo
			});
		}
		return me.tb;
	},

	/**
	 * 描画用ツールバーを表示します.
	 * @param {Object} toolbarOptions
	 * @return {Ext.toolbar.Toolbar} ツールバー
	 */
	initDrawToolbarItems: function(toolbarOptions) {
		var me = this;
		var map = me.map;
		var olmap = map.map;
		// オプションの読み込み
		var options = {};
		Ext.applyIf(options, toolbarOptions);

		// 未定義オプションはデフォルトを設定する
		var defaultOptions = {
			init: true,
			renderTo: document.body,
			label: null,
			drawPoint: false,
			drawLine: false,
			drawPolygon: false,
			drawCircle: false,
			strokeColor: false,
			modifyFeature: true,
			dragFeature: true,
			removeFeature: true,
			maxDrawNum: 1
		};
		Ext.applyIf(options, defaultOptions);
		me.options = options;

		var geomType = options.geomType;

		// ツールバーのアイテム配列
		var buttons = me.buttons = {
			drawPointButton: null,
			drawLineButton: null,
			drawPolygonButton: null,
			modifyFeatureButton: null,
			dragFeatureButton: null
		};
		var items = [];

		var maxDrawNum = options.maxDrawNum;

		/**
		 * @param drawFeatureOptions
		 */
		var drawFeature = function(drawFeatureOptions) {
			var options = {};
			Ext.applyIf(options, drawFeatureOptions);

			var defaultOptions = {
				button: null,
				map: null,
				drawKey: "point",
				maxDrawNum: 1
			};
			Ext.applyIf(options, defaultOptions);

			var button = options.button;
			var map = options.map;
			var drawKey = options.drawKey;
			var maxDrawNum = options.maxDrawNum;

			// 描画コントローラを取得
			var drawLayer = me.drawLayer;
			var drawControl = drawLayer.getDrawControl(drawKey);
			var layer = drawLayer.layer;
			var currentDrawNum = layer.features.length;

			if(currentDrawNum<maxDrawNum) {
				var onFeatureAdded = null;
				onFeatureAdded = function(){
					// 指定数描画したら
					if(maxDrawNum<=layer.features.length) {
						drawLayer.deactivateDrawControl(drawKey);
						drawControl.events.unregister("featureadded", map, onFeatureAdded);
						map.toFront(layer);
						map.setNavigationControlActivation(true);
						map.events.triggerEvent(map.EventType.afterdraw, {
							drawKey: drawKey
						});
						// ボタン押下状態にしない
						if(button!=null) {
							button.toggle(false);
						}
					}
				};
				drawControl.events.register("featureadded", map, onFeatureAdded);

				// 描画を有効にする
				drawLayer.activateDrawControl(drawKey);
				map.toFront(layer);
			}
		};

		// ラベル追加
		if(options.label) {
			items.push(options.label);
		}

		// ポイント描画ボタン
		if(options.drawPoint) {
			// UI作成
			var drawPointButton = buttons.drawPointButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Point"),
				iconCls: "draw-point-icon",
				tooltip: lang.__("Draw a point."),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "drawPoint"
			});
			items.push(drawPointButton);

			// 点描画イベント
			drawPointButton.on("click", function(button, e, eOpts){
				var drawKey = "point";

				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var drawControl = drawLayer.getDrawControl(drawKey);
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				// 描画最大数を超えた場合は有効をキャンセルする
				if(maxDrawNum<=currentDrawNum) {
					button.toggle(false);
					alert(lang.__("This feature can register {0} {1} or less.", drawPointButton.text, maxDrawNum));
				}
				else if(button.pressed) {
					// 有効処理
					drawFeature({
						button: button,
						map: map,
						drawKey: drawKey,
						drawNum: 1,
						maxDrawNum: maxDrawNum
					});
				}
				else {
					map.setNavigationControlActivation(true);
				}
			});

			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(drawPointButton, "point");
		}

		// ポリゴン描画ボタン
		if(options.drawPolygon) {
			// UI作成
			var drawPolygonButton = buttons.drawPolygonButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Surface"),
				iconCls: "draw-polygon-icon",
				tooltip: lang.__("Draw surface."),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "drawPolygon"
			});
			items.push(drawPolygonButton);

			// ポリゴン描画イベント
			drawPolygonButton.on("click", function(button, e, eOpts){
				var drawKey = "polygon";

				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var drawControl = drawLayer.getDrawControl(drawKey);
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				// 描画最大数を超えた場合は有効をキャンセルする
				if(maxDrawNum<=currentDrawNum) {
					button.toggle(false);
					alert(lang.__("This feature can register {0} {1} or less.", drawPolygonButton.text, maxDrawNum));
				}
				else if(button.pressed) {
					// 有効処理
					drawFeature({
						button: button,
						map: map,
						drawKey: drawKey,
						drawNum: 1,
						maxDrawNum: maxDrawNum
					});
				}
				else {
					map.setNavigationControlActivation(true);
				}

				// コントローラの有効/無効にボタンを連動させる
				me.bindButtonControlActivation(drawPolygonButton, "polygon");
			});
		}

		// 円描画ボタン
		if(options.drawCircle) {
			// UI作成
			var drawCircleButton = buttons.drawCircleButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Circle"),
				iconCls: "draw-circle-icon",
				tooltip: lang.__("Draw a circle."),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "drawCircle"
			});
			items.push(drawCircleButton);

			// ポリゴン描画イベント
			drawCircleButton.on("click", function(button, e, eOpts){
				var drawKey = "circle";

				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var drawControl = drawLayer.getDrawControl(drawKey);
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				// 描画最大数を超えた場合は有効をキャンセルする
				if(maxDrawNum<=currentDrawNum) {
					button.toggle(false);
					alert(lang.__("This feature can register {0} {1} or less.", drawCircleButton.text, maxDrawNum));
				}
				else if(button.pressed) {
					// 有効処理
					drawFeature({
						button: button,
						map: map,
						drawKey: drawKey,
						drawNum: 1,
						maxDrawNum: maxDrawNum
					});
				}
				else {
					map.setNavigationControlActivation(true);
				}

				// コントローラの有効/無効にボタンを連動させる
				me.bindButtonControlActivation(drawCircleButton, "circle");
			});
		}

		// ライン描画ボタン
		if(options.drawLine) {
			// UI作成
			var drawLineButton = buttons.drawLineButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Line"),
				iconCls: "draw-line-icon",
				tooltip: lang.__("Draw line."),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "drawLine"
			});
			items.push(drawLineButton);

			// ライン描画イベント
			drawLineButton.on("click", function(button, e, eOpts){
				var drawKey = "line";
				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var drawControl = drawLayer.getDrawControl(drawKey);
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				// 描画最大数を超えた場合は有効をキャンセルする
				if(maxDrawNum<=currentDrawNum) {
					// ボタン押下状態にしない
					button.toggle(false);
					alert(lang.__("This feature can register {0} {1} or less.", drawLineButton.text, maxDrawNum));
				}
				else if(button.pressed) {
					// 有効処理
					drawFeature({
						button: button,
						map: map,
						drawKey: drawKey,
						drawNum: 1,
						maxDrawNum: maxDrawNum
					});
				}
				else {
					map.setNavigationControlActivation(true);
				}
			});

			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(drawLineButton, "line");
		}

		// 手書きライン描画ボタン
		if(options.drawFreeLine) {
			// UI作成
			var drawFreeLineButton = buttons.drawFreeLineButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Line by hand"),
				iconCls: "draw-freeline-icon",
				tooltip: lang.__("Draw line by hand"),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "drawFreeLine"
			});
			items.push(drawFreeLineButton);

			// ライン描画イベント
			drawFreeLineButton.on("click", function(button, e, eOpts){
				var drawKey = "freeline";
				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var drawControl = drawLayer.getDrawControl(drawKey);
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				// 描画最大数を超えた場合は有効をキャンセルする
				if(maxDrawNum<=currentDrawNum) {
					// ボタン押下状態にしない
					button.toggle(false);
					alert(lang.__("This feature can register {0} {1} or less.", drawFreeLineButton.text, maxDrawNum));
				}
				else if(button.pressed) {
					// 有効処理
					drawFeature({
						button: button,
						map: map,
						drawKey: drawKey,
						drawNum: 1,
						maxDrawNum: maxDrawNum
					});
				}
				else {
					map.setNavigationControlActivation(true);
				}
			});

			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(drawFreeLineButton, "freeline");
		}

		// テキスト描画ボタン ※メモ機能専用、登録情報では利用不可
		if(options.drawText) {
			// UI作成
			var drawTextButton = buttons.drawTextButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Text<!--1-->"),
				iconCls: "draw-text-icon",
				tooltip: lang.__("Draw text"),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "drawText"
			});
			items.push(drawTextButton);

			// テキスト描画イベント
			drawTextButton.on("click", function(button, e, eOpts){
				var drawKey = "text";
				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var drawControl = drawLayer.getDrawControl(drawKey);
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				if(button.pressed) {
					// 有効処理
					drawFeature({
						button: button,
						map: map,
						drawKey: drawKey,
						drawNum: 1,
						maxDrawNum: maxDrawNum
					});
				}
				else {
					map.setNavigationControlActivation(true);
				}
			});

			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(drawTextButton, "text");
		}

		items.push("-");

		//
		// スタイルの変更ボタン
		//

		// 線の色
		if(options.strokeColor) {
			var colorMenu = Ext.create("Ext.menu.ColorPicker", {
				handler: function(cm, color) {
					//Ext.Msg.alert('Color Selected', '<span style="color:#' + color + ';">You choose '+color+'.</span>');
					// フィーチャ用の線の色を変更
					me.drawLayer.layer.styleMap.styles['default'].defaultStyle.strokeColor = "#"+color;
					me.drawLayer.userStyle.strokeColor = "#"+color;
					// 描画時のスタイルを変更
					me.drawLayer.layer.styleMap.styles['temporary'].defaultStyle.strokeColor = "#"+color;
					// ボタンの色も変更
					buttonEl = buttons.strokeColorButton.getEl();
					buttonIconEl = buttonEl.select(".x-btn-icon");
					buttonIconEl.setStyle("background-color", "#"+color);

					// 選択した地物があれば、線の色を変更する
					//<span class="ja">選択済みのFeatureの色を設定</span><span class="en">Set color for the Features which are already selected</span>
					me.drawLayer.setSelectedFeatureStyle(function(f) {
						f.style.strokeColor = "#"+color;
					});
				}
			});

			var menu = Ext.create("Ext.menu.Menu", {
				style: {
					//overflow: 'visible'
				},
				items: [{
					menu: colorMenu
				}]
			});

			var strokeColorButton = buttons.strokeColorButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Line color"),
				iconCls: "draw-freeline-icon",
				tooltip: lang.__("Change line color."),
				menu: colorMenu,
				drawToolbarType: "strokeColor"
			});
			items.push(strokeColorButton);
		}

		// 線の太さ
		if(!!options.strokeWidth) {
			var defaultStrokeWidth = me.drawLayer.layer.styleMap.styles.default.defaultStyle.strokeWidth;
			var data = [];
			var strokeWidthMenuItems = [];
			for(var strokeWidth=0; strokeWidth<=10; strokeWidth++) {
				data.push({
					display: strokeWidth+"px",
					value: strokeWidth
				});
				var getStrokeWidthIconUrl = function(strokeWidth) {
					return map.icon.baseURL+"/icons/stroke_width/line_width_"+strokeWidth+".png";
				}
				var iconUrl = getStrokeWidthIconUrl(strokeWidth);
				var handler = (function(strokeWidth) {
					return function(item, e) {
						// メニューの選択状態を変更する
						var menuItem = $(item.getEl().dom);
						menuItem.siblings().removeClass("selected-menu");
						menuItem.addClass("selected-menu");
						
						// 線の太さを変更
						me.drawLayer.layer.styleMap.styles['default'].defaultStyle.strokeWidth = strokeWidth;
						me.drawLayer.userStyle.strokeWidth = strokeWidth;
						// 描画時のスタイルを変更
						me.drawLayer.layer.styleMap.styles['temporary'].defaultStyle.strokeWidth = strokeWidth;
						var iconUrl = getStrokeWidthIconUrl(strokeWidth);
						// ボタンのアイコンも変更
						buttonEl = buttons.strokeWidthButton.getEl();
						buttonIconEl = buttonEl.select(".x-btn-icon");
						buttonIconEl.setStyle("background-image", "url('"+iconUrl+"')");

						// 選択した地物があれば、線の太さを変更する
						me.drawLayer.setSelectedFeatureStyle(function(f) {
							f.style.strokeWidth = strokeWidth;
						});
					}
				})(strokeWidth);
				strokeWidthMenuItems.push({
					text: strokeWidth+"px",
					icon: iconUrl,
					handler: handler,
					cls: strokeWidth==defaultStrokeWidth ? "selected-menu" : ""
				});
			}
			var icon = getStrokeWidthIconUrl(defaultStrokeWidth);
			var strokeWidthButton = buttons.strokeWidthButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Thickness of the line"),
				icon: icon,
				tooltip: lang.__("Change thickness of the line."),
				menu: strokeWidthMenuItems,
				drawToolbarType: "strokeWidth"
			});
			items.push(strokeWidthButton);
		}

		// 塗りつぶしの色
		if(options.fillColor) {
			var colorMenu = Ext.create("Ext.menu.ColorPicker", {
				handler: function(cm, color) {
					//Ext.Msg.alert('Color Selected', '<span style="color:#' + color + ';">You choose '+color+'.</span>');
					// フィーチャ用の塗りつぶしの色を変更
					me.drawLayer.layer.styleMap.styles['default'].defaultStyle.fillColor = "#"+color;
					me.drawLayer.userStyle.fillColor = "#"+color;
					// 描画時のスタイルを変更
					me.drawLayer.layer.styleMap.styles['temporary'].defaultStyle.fillColor = "#"+color;
					// ボタンの色も変更
					buttonEl = buttons.fillColorButton.getEl();
					buttonIconEl = buttonEl.select(".x-btn-icon");
					buttonIconEl.setStyle("background-color", "#"+color);

					// 選択した地物があれば、塗りつぶしの色を変更する
					me.drawLayer.setSelectedFeatureStyle(function(f) {
						f.style.fillColor = "#"+color;
					});
				}
			});

			// 透明度スライダーHBoxコンテナ
			var fieldContainer = Ext.create('Ext.form.FieldContainer', {
		        hideLabel: true,
				layout: {
					type: "hbox",
					align: "stretch"
				},
				defaults: {
					flex: 1
				},
				items: [{
			        xtype: 'displayfield',
			        style: {
			            "text-align": 'left',
			        },
			        value: lang.__("0%(nontransparent)")
/*			    }, {
			        xtype: 'displayfield',
			        style: {
			            "text-align": 'center',
			        },
			        value: lang.__("Transparency")*/
			    }, {
			        xtype: 'displayfield',
			        style: {
			            "text-align": 'right',
			        },
			        value: '100%'
			    }]
			});
			colorMenu.add(fieldContainer);

			// 透明度スライダー
			var opacitySlider = Ext.create('Ext.slider.Single', {
		        hideLabel: true,
		        increment: 10,
		        minValue: 0,
		        maxValue: 100,
		        value: me.drawLayer.modifyStyleMap.styles['default'].defaultStyle.fillOpacity*100, // @see DrawLayerSelectDrag.js modifyStyleMap.fillOpacity
		        tipText: function(sliderThumb) {
		        	if(sliderThumb.value==0) return lang.__("Transparency")+lang.__("0%(nontransparent)");
		        	return lang.__("Transparency")+sliderThumb.value+"%";
		        },
		        listeners: {
		        	changecomplete: function(slider, newValue, thumb, eOpts) {
		        		var opacity = Math.abs(100 - thumb.value)/100;
						// フィーチャ用の塗りつぶしの透明度を変更
						me.drawLayer.layer.styleMap.styles['default'].defaultStyle.fillOpacity= opacity;
						me.drawLayer.userStyle.fillOpacity = opacity;
						// 描画時のスタイルを変更
						me.drawLayer.layer.styleMap.styles['temporary'].defaultStyle.fillOpacity = opacity;
						/*
						// ボタンの色も変更
						buttonEl = buttons.fillColorButton.getEl();
						buttonIconEl = buttonEl.select(".x-btn-icon");
						buttonIconEl.setStyle("opacity", opacity);
						*/

						// 選択した地物があれば、塗りつぶしの透明度を変更する
						me.drawLayer.setSelectedFeatureStyle(function(f) {
							f.style.fillOpacity = opacity;
						});		        		
		        	}
		        }
		    });
			colorMenu.add(opacitySlider);

			var menu = Ext.create("Ext.menu.Menu", {
				style: {
					//overflow: 'visible'
				},
				items: [{
					menu: colorMenu
				}]
			});

			var fillColorButton = buttons.fillColorButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Fill color"),
				iconCls: "paintcan-icon",
				tooltip: lang.__("Change fill color."),
				menu: colorMenu,
				drawToolbarType: "fillColor"
			});
			items.push(fillColorButton);
		}

		// 文字の色
		if(options.fontColor) {
			var colorMenu = Ext.create("Ext.menu.ColorPicker", {
				handler: function(cm, color) {
					//Ext.Msg.alert('Color Selected', '<span style="color:#' + color + ';">You choose '+color+'.</span>');
					// フィーチャ用の文字の色を変更
					me.drawLayer.layer.styleMap.styles['default'].defaultStyle.fontColor = "#"+color;
					me.drawLayer.userStyle.fontColor = "#"+color;
					// 描画時のスタイルを変更
					me.drawLayer.layer.styleMap.styles['temporary'].defaultStyle.fontColor = "#"+color;
					// ボタンの色も変更
					buttonEl = buttons.fontColorButton.getEl();
					buttonIconEl = buttonEl.select(".x-btn-icon");
					buttonIconEl.setStyle("background-color", "#"+color);

					// 選択した地物があれば、文字の色を変更する
					me.drawLayer.setSelectedFeatureStyle(function(f) {
						f.style.fontColor = "#"+color;
					});
				}
			});

			var menu = Ext.create("Ext.menu.Menu", {
				style: {
					//overflow: 'visible'
				},
				items: [{
					menu: colorMenu
				}]
			});

			var fontColorButton = buttons.fontColorButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Font color"),
				iconCls: "draw-text-icon",
				tooltip: lang.__("Change font color."),
				menu: colorMenu,
				drawToolbarType: "fontColor"
			});
			items.push(fontColorButton);
		}

		// 文字のサイズ
		if(!!options.fontSize) {
			var defaultFontSize = me.drawLayer.layer.styleMap.styles.default.defaultStyle.fontSize;
			var data = [];
			var fontSizeMenuItems = [];
			var fontSizes = [10, 12, 14, 18, 22, 28];
			for(var idx in fontSizes) {
				fontSize=fontSizes[idx];
				data.push({
					display: fontSize+"px",
					value: fontSize
				});
				var getFontSizeIconUrl = function(fontSize) {
					var pt = fontSize;
					switch(fontSize) {
					case 10: pt=8; break; 
					case 12: pt=10; break; 
					case 14: pt=12; break; 
					case 18: pt=14; break; 
					case 22: pt=18; break; 
					case 28: pt=21; break;
					}
					return map.icon.baseURL+"/icons/font_size/font_size_"+pt+"pt.png";
				}
				var iconUrl = getFontSizeIconUrl(fontSize);
				var handler = (function(fontSize) {
					return function(item, e) {
						// メニューの選択状態を変更する
						var menuItem = $(item.getEl().dom);
						menuItem.siblings().removeClass("selected-menu");
						menuItem.addClass("selected-menu");
						
						// 文字のサイズを変更
						me.drawLayer.layer.styleMap.styles['default'].defaultStyle.fontSize = fontSize;
						me.drawLayer.userStyle.fontSize = fontSize;
						// 描画時のスタイルを変更
						me.drawLayer.layer.styleMap.styles['temporary'].defaultStyle.fontSize = fontSize;
						var iconUrl = getFontSizeIconUrl(fontSize);
						// ボタンのアイコンも変更
						buttonEl = buttons.fontSizeButton.getEl();
						buttonIconEl = buttonEl.select(".x-btn-icon");
						buttonIconEl.setStyle("background-image", "url('"+iconUrl+"')");

						// 選択した地物があれば、文字のサイズを変更する
						me.drawLayer.setSelectedFeatureStyle(function(f) {
							f.style.fontSize = fontSize;
							me.drawLayer.setLabelBackground(f);
						});
					}
				})(fontSize);
				fontSizeMenuItems.push({
					text: fontSize+"px",
					icon: iconUrl,
					handler: handler,
					cls: fontSize==defaultFontSize? "selected-menu" : ""
				});
			}
			var icon = getFontSizeIconUrl(defaultFontSize);
			var fontSizeButton = buttons.fontSizeButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Font Size"),
				icon: icon,
				tooltip: lang.__("Change size of the font."),
				menu: fontSizeMenuItems,
				drawToolbarType: "fontSize"
			});
			items.push(fontSizeButton);
		}

		items.push("-");

		// フィーチャ編集
		if(options.modifyFeature) {
			// UI作成
			var modifyFeatureButton = buttons.modifyFeatureButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Edit"),
				iconCls: "modify-feature-icon",
				tooltip: lang.__("Edit vertex of the line and face."),
				toggleGroup: "draw-button",
				enableToggle: true,
				drawToolbarType: "modifyFeature"
			});
			items.push(modifyFeatureButton);

			// フィーチャ編集イベント
			modifyFeatureButton.on("click", function(button, e, eOpts) {
				// 描画コントローラを取得
				var drawLayer = me.drawLayer;
				drawLayer.deactivateCurrentDrawControl();
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				if(button.pressed&&0<currentDrawNum) {
					drawLayer.setModifyFeatureControlActivation(true);
					map.toFront(layer);
				}
				else {
					button.toggle(false);
					map.setNavigationControlActivation(true);
				}
			});

			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(modifyFeatureButton, "modifyFeature");
		}

		// フィーチャ移動
		if(options.dragFeature) {
			var drawLayer = me.drawLayer;
			// UI作成
			var dragFeatureButton = buttons.dragFeatureButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Move"),
				iconCls: "drag-feature-icon",
				tooltip: lang.__("Move point, line, surface."),
				toggleGroup: "draw-button",
				pressed: drawLayer.getDragFeatureControlActivation(),
				enableToggle: true,
				drawToolbarType: "dragFeature"
			});
			items.push(dragFeatureButton);

			// フィーチャ移動イベント
			dragFeatureButton.on("click", function(button, e, eOpts){
				drawLayer.deactivateCurrentDrawControl();
				// 描画コントローラを取得
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				if(button.pressed&&0<currentDrawNum) {
					drawLayer.setDragFeatureControlActivation(true);
					map.toFront(layer);
				}
				else {
					drawLayer.setDragFeatureControlActivation(false);
					map.setNavigationControlActivation(true);
				}
			});
			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(dragFeatureButton, "dragFeature");
		}

		// フィーチャ選択
		if(options.selectDragFeature) {
			var drawLayer = me.drawLayer;
			// UI作成
			var selectDragFeatureButton = buttons.selectDragFeatureButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Select"),
				iconCls: "cursor-icon",
				tooltip: lang.__("Select feature by clicking.")+"<br/>"
				        +lang.__("To select more than one, press Ctrl key.")+"<br/>"
				        +lang.__("Delete selected features by DELETE key.")+"<br/>"
				        +lang.__("Move selected features by arrow keys.")+"<br/>"
				        +lang.__("Move feature by dragging.")+"<br/>"
				        +lang.__("Edit text by double-click."),
				toggleGroup: "draw-button",
				pressed: drawLayer.getSelectDragFeatureControlActivation(),
				enableToggle: true,
				drawToolbarType: "selectDragFeature"
			});
			items.push(selectDragFeatureButton);

			// フィーチャ選択イベント
			selectDragFeatureButton.on("click", function(button, e, eOpts){
				drawLayer.deactivateCurrentDrawControl();
				// 描画コントローラを取得
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				if(button.pressed&&0<currentDrawNum) {
					drawLayer.setSelectDragFeatureControlActivation(true);
				}
				else {
					drawLayer.setSelectDragFeatureControlActivation(false);
					map.setNavigationControlActivation(true);
				}
			});
			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(selectDragFeatureButton, "selectDragFeature");
		}

		// フィーチャ範囲選択
		if(options.selectRangeFeature) {
			var drawLayer = me.drawLayer;
			// UI作成
			var selectRangeFeatureButton = buttons.selectRangeFeatureButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Range Select"),
				iconCls: "range-icon",
				tooltip: lang.__("Select feature by box."),
				toggleGroup: "draw-button",
				pressed: drawLayer.getBoxSelectDragFeatureControlActivation(),
				enableToggle: true,
				drawToolbarType: "selectRangeFeature"
			});
			items.push(selectRangeFeatureButton);

			// フィーチャ選択イベント
			selectRangeFeatureButton.on("click", function(button, e, eOpts){
				drawLayer.deactivateCurrentDrawControl();
				// 描画コントローラを取得
				var layer = drawLayer.layer;
				var currentDrawNum = layer.features.length;
				if(button.pressed&&0<currentDrawNum) {
					drawLayer.setBoxSelectDragFeatureControlActivation(true);
				}
				else {
					drawLayer.setBoxSelectDragFeatureControlActivation(false);
					map.setNavigationControlActivation(true);
				}
			});
			// コントローラの有効/無効にボタンを連動させる
			me.bindButtonControlActivation(selectRangeFeatureButton, "selectDragFeatureBox");
		}

		// イベント定義
		if(options.removeSelectedFeature) {
			// UI作成
			var removeSelectedFeatureButton = Ext.create("Ext.button.Button", {
				text: lang.__("Delete"),
				iconCls: "removeselected-feature-icon",
				tooltip: lang.__("Delete selected note."),
				drawToolbarType: "removeSelectedFeature"
				//toggleGroup: "draw-button",
				//enableToggle: true
			});
			items.push(removeSelectedFeatureButton);

			// フィーチャ削除イベント
			removeSelectedFeatureButton.on("click", function(button, e, eOpts){

				// フィーチャが複数ある場合は確認する
				if(0<drawLayer.layer.selectedFeatures.length) {
					Ext.MessageBox.confirm("", lang.__("Delete selected note?"),
						function(btn) {
							if(btn=="yes") {
								me.drawLayer.removeSelectedFeature(true);
							}
						}
					);
				}
			});
		}
		if(options.removeFeature) {
			// UI作成
			var removeAllFeatureButton = Ext.create("Ext.button.Button", {
				text: lang.__("Delete all<!--2-->"),
				iconCls: "removeall-feature-icon",
				tooltip: lang.__("Delete all drawn objects."),
				drawToolbarType: "removeFeature"
				//toggleGroup: "draw-button",
				//enableToggle: true
			});
			items.push(removeAllFeatureButton);

			// フィーチャ削除イベント
			removeAllFeatureButton.on("click", function(button, e, eOpts){

				// フィーチャが複数ある場合は確認する
				if(1<drawLayer.layer.features.length) {
					Ext.MessageBox.confirm(lang.__("Confirmation of all Delete"), lang.__("Are you sure to delete all drawn objects?"),
						function(btn) {
							if(btn=="yes") {
								// 地物をすべて削除
								me.drawLayer.layer.removeAllFeatures();
							}
						}
					);
				}
				else {
					// 地物をすべて削除
					me.drawLayer.layer.removeAllFeatures();
				}
			});
		}

		// 切り出しボタン
		if(options.intersectLayerId!=null) {
			var intersectLayerId = options.intersectLayerId;
			var intersectButton = Ext.create("Ext.button.Button", {
				text: lang.__("Crop"),
				iconCls: "intersection-feature-icon",
				tooltip: lang.__("Crop and shape drawing."),
				drawToolbarType: "intersect"
			});
			items.push(intersectButton);

			intersectButton.on("click", function(button, e, eOpts) {
				var feature = null;
				try{
					feature = me.getDrawFeature(geomType);
				} catch (e) {
					alert(lang.__("Invalid graphic. Please correct that graphics do not cross."));
				}
				var feature = me.getDrawFeature(geomType);
				if(feature==null) {
					alert(lang.__("No shape drawn."));
				}
				else {
					Ext.MessageBox.confirm(
						lang.__("Confirmation of cropping"),
						lang.__("Cropping by {0}.<br/>Are you sure ?", options.intersectLayerName),
						function(btn) {
							if(btn=="yes") {
								try{
									feature = feature.clone();
									var geometry = feature.geometry;
									var wkt = null;
									feature.geometry.transform(olmap.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));
									var wktFormat = new OpenLayers.Format.WKT();
									wkt = wktFormat.write(feature);
									console.log("wkt");
									console.log(wkt);
									var result = map.api.intersection(wkt, intersectLayerId);
									console.log("result");
									console.log(result);
									// 結果が返ってきたら描画しなおす
									if(result!=null && typeof result.wkt=="string") {
										var drawLayer = me.drawLayer;
										var layer = drawLayer.layer;
										// 全削除
										layer.removeAllFeatures();

										var features = wktFormat.read(result.wkt);
										if(typeof features=="undefined") {
											alert(lang.__("Shape empty."));
										}
										else {
											var geometry = features.geometry;
											var resultGeomType = geometry.CLASS_NAME.split(".")[2].toUpperCase();
											if(geomType.indexOf(resultGeomType)==-1) {
												var getGeomName = function(geomType) {
													var type = geomType.toUpperCase();
													var name =
														type.indexOf("POINT")!= -1 ? lang.__("Point"):
														type.indexOf("LINESTRING")!=-1 ? lang.__("Line"):
														type.indexOf("POLYGON")!=-1 ? lang.__("Surface"):
														lang.__("Unknown");
													if(type.indexOf("MULTI")==0) name+=lang.__("(Multiple)");
													return name;
												};
												var message = lang.__("Shape type not the same.");
												message += "\n";
												message += lang.__("Unable to draw {0} in {1}.", getGeomName(resultGeomType), getGeomName(geomType));
												alert(message);
											}
											else {
												drawLayer.drawFeatureByGeometry(geometry);
												layer.redraw(true);
											}
										}
									}
								} catch (e) {
									Ext.MessageBox.alert(lang.__("Failed to crop"), e);
								}
							}
						}
					);
				}
			});
		}

		items.push("-");

		// 元に戻す
		if(!!options.undo) {
			// UI作成
			var undoButton = buttons.undoButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Restore"),
				icon: map.icon.getURL("undoIconURL"),
				tooltip: lang.__("Restore drawing."),
				drawToolbarType: "undo"
				//toggleGroup: "draw-button",
				//enableToggle: true
			});
			// undo イベント
			undoButton.on("click", function(button, e, eOpts) {
				me.drawLayer.historyControl.undo();
			});
			items.push(undoButton);
			me.drawLayer.layer.events.on({
				"historychange": function(args) {
					var historyControl = args.historyControl;
					enable = 0<=historyControl.stackIndex;
					undoButton.setDisabled(!enable);
					console.log("undoButton: "+(enable?"enabled":"disabled")+" stackIndex:"+historyControl.stackIndex);
				}
			});
		}
		// やり直す
		if(!!options.redo) {
			// UI作成
			var redoButton = buttons.redoButton =
			Ext.create("Ext.button.Button", {
				text: lang.__("Redo"),
				icon: map.icon.getURL("redoIconURL"),
				tooltip: lang.__("Redo drawing."),
				drawToolbarType: "redo"
				//toggleGroup: "draw-button",
				//enableToggle: true
			});
			// redo イベント
			redoButton.on("click", function(button, e, eOpts) {
				me.drawLayer.historyControl.redo();
			});
			items.push(redoButton);
			me.drawLayer.layer.events.on({
				"historychange": function(args) {
					var historyControl = args.historyControl;
					enable = historyControl.stackIndex+1 < historyControl.stack.length;
					redoButton.setDisabled(!enable);
					console.log("undoButton: "+(enable?"enabled":"disabled")+" stackIndex:"+historyControl.stackIndex);
				}
			});
		}

		var splitItem = function(items) {
			var maxRowNum = 6;
			// itemの総数が1行あたりの最大個数をこえているか
			if(maxRowNum<=items.length) {
				// "-" があればそこで分割
				if(-1<items.indexOf("-")) {

				}
			}
		}

		return items;
	},

	/**
	 * 描画したフィーチャを１つのフィーチャとして取得します.
	 * @return {OpenLayers.Feature.Vector}
	 */
	getDrawFeature: function(geomType, option) {
		var me = this;
		var drawLayer = me.drawLayer;
		var layer = drawLayer.layer;
		var features = layer.features;
		if(features.length==0) return null;
		// フィーチャをコピーする
		// コピーせずにオリジナルを変換してしまうと、
		// Invalid Geometryが発生すると、変換が２回はいってしまうため
		copy = [];
		for(var featuresIdx in features) {
			copy.push(features[featuresIdx].clone());
		}
		features = copy;
		// 擬似ジオメトリOKなら変換する
		if(option && option.pseudo) {
			for(var featuresIdx in features) {
				var feature = features[featuresIdx];
				var pseudoGeom = me.convertPseudoGeometry(geomType, feature.geometry);
				if(pseudoGeom!=null) feature.geometry = pseudoGeom;
			}
		}
		return me.mergeFeatures(features, geomType);
	},

	/**
	 * features を指定したジオメトリに擬似的に変換する。
	 * @params {String} geomType 擬似ジオメトリタイプ
	 *  (MULTI)POLYGON: "POINT"は y座標を上下0.5cmずつずらす
	 * @params {OpenLayers.Geometry} geometry 変換対象ジオメトリ
	 */
	convertPseudoGeometry: function(geomType, geometry) {
		var me = this;
		var drawLayer = me.drawLayer;
		var layer = drawLayer.layer;
		// MULTI を外して、擬似ジオメトリタイプを取得
		var pseudoGeomType = (-1<geomType.indexOf("MULTI") ? geomType.substring(geomType.indexOf("MULTI")+5) : geomType);

		// 擬似ジオメトリに変換する
		switch(pseudoGeomType) {
		case "POINT" :
			// todo: pseudo Point
			break;
		case "LINESTRING" :
			// todo: pseudo LineString
			break;
		// 擬似ポリゴン
		case "POLYGON" :
			// ポリゴンレイヤでの擬似ポイントに変換
			var pseudoPointDiffMeter = 0.01; // 擬似ポイントのポリゴンは, 1cm ずらしたポリゴンとする
			var meterProj = new OpenLayers.Projection("EPSG:900913");
			if(geometry.CLASS_NAME=="OpenLayers.Geometry.Point") {
				// x座標は変えずに、y座標だけ上下に 1cm ずつずらした 擬似ポイントのポリゴンにする
				var northPoint = null;
				var southPoint = null;
				{
					var p = geometry.clone();
					// メートル座標系に変換する
					p.transform(layer.projection, meterProj);
					// x座標は変えない
					// y座標のみずらす
					var northPoint = p.clone();
					northPoint.y = p.y+(pseudoPointDiffMeter/2);
					var southPoint = p.clone();
					southPoint.y = p.y-(pseudoPointDiffMeter/2);
					// もとの座標系に戻す
					northPoint.transform(meterProj, layer.projection);
					southPoint.transform(meterProj, layer.projection);
				}
				var ring = new OpenLayers.Geometry.LinearRing([southPoint, northPoint, northPoint, southPoint]);
				var polygon = new OpenLayers.Geometry.Polygon([ring]);
				return polygon;
			}
			// ポリゴンレイヤでの擬似ラインに変換
			else if(geometry.CLASS_NAME=="OpenLayers.Geometry.LineString") {
				var points = [].concat(geometry.components).concat([].concat(geometry.components).reverse());
				var ring = new OpenLayers.Geometry.LinearRing(points);
				var polygon = new OpenLayers.Geometry.Polygon([ring]);
				return polygon;
			}
			// Polygon が擬似ポイントなら元に戻す
			else if(geometry.CLASS_NAME=="OpenLayers.Geometry.Polygon") {
				// 擬似ポイント
				var isPseudoPoint = function(polygon) {
					if(polygon.components.length!=1) return false;
					var line = polygon.components[0];
					var southPoint = line.components[0].clone();
					var northPoint = line.components[1].clone();
					if(line.components.length!=4) return false;
					if(southPoint.equals(line.components[3])==false) return false; // assert 最初と最後の点が同じ
					if(northPoint.equals(line.components[2])==false) return false; // assert 間の点も同じ
					if(southPoint.x!=northPoint.x) return false; // assert x座標が同じ
					northPoint.transform(layer.projection, meterProj);
					southPoint.transform(layer.projection, meterProj);
					if(pseudoPointDiffMeter*2<(northPoint.y-southPoint.y)) return false; // assert y座標の距離が 2cm 以下(誤差を考慮して2倍する)
					return true;
				}
				// 擬似ライン
				var isPseudoLineString = function(polygon) {
					if(polygon.components.length!=1) return false;
					var line = polygon.components[0];
					// ラインの点を逆にするため、偶数であるはず
					if(line.components.length%2!=0) return false;

					// 2つの線を取り出して、ラインの点を逆にして各点の位置が同じかチェックする
					var pointsNum = line.components.length/2;
					var line1 = new OpenLayers.Geometry.LineString(line.components.slice(0, pointsNum));
					var line2 = new OpenLayers.Geometry.LineString(line.components.slice(pointsNum).reverse());
					return line1.equals(line2);
				}
				if(isPseudoPoint(geometry)) {
					var line = geometry.components[0];
					var southPoint = line.components[0];
					var northPoint = line.components[1];
					point = southPoint.clone();
					point.y = (southPoint.y + northPoint.y)/2;
					return point;
				}
				else if(isPseudoLineString(geometry)) {
					var line = geometry.components[0];
					var pointsNum = line.components.length/2;
					var line1 = new OpenLayers.Geometry.LineString(line.components.slice(0, pointsNum));
					return line1;
				}
			}
			break;
		}

		return null;
	},

	/**
	 * 複数のFeatureをマルチやコレクションのFeatureにまとめる
	 * @param {Array<OpenLayers.Feature.Vector>} features
	 * @param {String} geomType
	 * @return {OpenLayers.Feature.Vector}
	 */
	mergeFeatures : function(features, geomType) {
		var feature;
		if (geomType.match(/^MULTI/)) {
			switch(geomType) {
			case "MULTIPOINT" :
				feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.MultiPoint(features[0].geometry));
				break;
			case "MULTILINESTRING" :
				feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.MultiLineString(features[0].geometry));
				break;
			case "MULTIPOLYGON" :
				if(features[0].geometry.CLASS_NAME=="OpenLayers.Geometry.MultiPolygon") {
					feature = features[0];
				}
				else {
					feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.MultiPolygon(features[0].geometry));
				}
				break;
			/*case this.GEOMETRYCOLLECTION :
				feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Collection(features[0].geometry));
				break;*/
			}
			//<span class="ja">マルチポリゴンの重なりチェック</span><span class="en">Check the multiple polygon overlap</span>
			if (geomType == "MULTIPOLYGON") {
				for (var i=1; i<features.length; i++) {
					if (!features[i]._sketch) {
						for (var j=0; j<i; j++) {
							if (!features[j]._sketch && features[i].geometry.intersects(features[j].geometry)) throw "Invalid Geometry";
						}
					}
				}
			}

			//<span class="ja">追加</span><span class="en">Insert</span>
			for (var i=1; i<features.length; i++) {
				if (!features[i]._sketch) feature.geometry.addComponents(features[i].geometry);
			}
			feature.fid = features[0].fid;
			feature.layer = features[0].layer;

		} else {
			feature = features[0];
		}
		return feature;
	},

	/**
	 * 0番目のジオメトリの値を読み込む
	 */
	loadFeatureGeometry: function() {
		console.log("loadFeatureGeometry");
		var me = this;
		var stmap = me.map;
		var drawLayer = me.drawLayer;
		var selector = "."+drawLayer.getFeatureIDPrefix()+"0";
		var features = stmap.readFeatureGeometryValue(selector);
		if(features!=null){
			var geometry = features.geometry;
			geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:"+stmap.epsg));
			var gClass = geometry.CLASS_NAME;
			switch(gClass){
			case "OpenLayers.Geometry.Point":
				var lonlat = new OpenLayers.LonLat(geometry.x, geometry.y);
				drawLayer.moveSinglePoint(lonlat);
				break;
			case "OpenLayers.Geometry.LineString":
			case "OpenLayers.Geometry.MultiLineString":
				drawLayer.drawFeature("line", geometry);
				break;
			case "OpenLayers.Geometry.Polygon":
			case "OpenLayers.Geometry.MultiPolygon":
				drawLayer.drawFeature("polygon", geometry);
				break;
			default:
				console.warn(lang.__("Not support.({0})", gClass));
				break;
			}
		}
	},

	/**
	 * コントローラの有効/無効にボタンを連動させる
	 */
	bindButtonControlActivation: function(button, drawKey) {
		var me = this;
		var control = me.drawLayer.getDrawControl(drawKey);
		control.events.register("activate", null, function() {
			button.toggle(true);
		});
		control.events.register("deactivate", null, function() {
			button.toggle(false);
		});
	}
};
