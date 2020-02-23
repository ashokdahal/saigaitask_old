/* Copyright (c) 2006-2010 by OpenLayers Contributors (see authors.txt for 
 * full list of contributors). Published under the Clear BSD license.  
 * See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * @ requires OpenLayers/Control/SelectFeature.js
 */
/**
 * @class Featureの複数選択とドラッグ移動を可能にしたクラス.<br/>
 * 範囲選択(box=true)時はドラッグ移動不可<br/>
 * OpenLayers.Control.SelectFeature を拡張し OpenLayers.Control.DragFeature のDragを追加<br/>
 * hoverの動作は未検証<br/>
 * 
 * class: OpenLayers.Control.SelectDragFeature
 * The SelectFeature control selects vector features from a given layer on 
 * click or hover. 
 *
 * Inherits from:
 *	- <OpenLayers.Control>
 */
OpenLayers.Control.SelectDragFeature = OpenLayers.Class(OpenLayers.Control.SelectFeature, {
	
	/** Handler.Dragのcallback 追加可 */
	dragCallbacks: {},
	
	/** Handler.Featureのcallback 追加可 */
	featureCallbacks: {},
	
	/** OpenLayers.Handler.Keyboardのcallback 追加可 */
	keyboardCallbacks: {},
	
	/** マウスの下のfeature */
	mouseFeature : null,
	
	/**
	 * @since SaigaiTask-2.2
	 * @return タッチ操作の場合は true、クリック操作の場合は false
	 */
	directTouch: false,
	
	/** コントロール初期化処理
	 * @param layer コントロールを有効にするレイヤ
	 * @param options SelectFeatureと同様のオプション
	 * 
	 * Constructor: OpenLayers.Control.SelectFeature
	 * Create a new control for selecting features.
	 *
	 * Parameters:
	 * layers - {<OpenLayers.Layer.Vector>}, or an array of vector layers. The
	 *	   layer(s) this control will select features from.
	 * options - {Object} 
	 */
	initialize: function(layers, options) {
		var me = this;
		OpenLayers.Control.prototype.initialize.apply(this, [options]);
		
		if(this.scope === null) {
			this.scope = this;
		}
		this.initLayer(layers);

		this.handlers = {
			drag: new OpenLayers.Handler.Drag(
				this, OpenLayers.Util.extend({
					down: function(pixel) {
						//console.log("drag.down");
						me.downFeature(pixel);
					},
					move: function(pixel) {
						//console.log("drag.move");
						me.moveFeature(pixel);
					},
					up: function(pixel) {
						//console.log("drag.up");
						if(me.directTouch) {
							// タッチ操作ではマウスアウトが無いので、
							// タッチ終了後に即座にマウスアウト扱いにする
							me.upFeature(pixel);
							me.outFeature();
							me.handlers.drag.deactivate();
						}
						else {
							return me.upFeature(pixel);
						}
					},
					out: function(pixel) {
						//console.log("drag.out");
						me.cancel(pixel);
					},
					done: function(pixel) {
						//console.log("drag.done");
						me.doneDragging(pixel);
					}/*,
					// @since SaigaiTask-2.2
					// タッチデバイスの対応
					touchstart: function(pixel) {
						if(me.isTouchDevice()) {
							console.log("drag.touchstart");
							me.downFeature(pixel);
						}
					},
					touchmove: function(pixel) {
						if(me.isTouchDevice()) {
							console.log("drag.touchmove");
							me.moveFeature(pixel);
						}
					}
*/
				}, this.dragCallbacks),
				{documentDrag: this.documentDrag}
			),
			feature: new OpenLayers.Handler.Feature(
				this, this.layer, OpenLayers.Util.extend({
					//click: this.clickFeature, //dragのdown内から実行される
					dblclick : this.dblclickFeature,
					clickout: function() {
						if(me.isTouchDevice()) {
							me.handlers.drag.deactivate();
						}
						me.clickoutFeature();
					},
					over: this.overFeature,
					out: this.outFeature
				}, this.featureCallbacks),
				{geometryTypes: this.geometryTypes}
			),
			keyboard : new OpenLayers.Handler.Keyboard(
				this, {
					keydown: this.keydown
				}
			)
		};
		if (this.box) {
			this.handlers.box = new OpenLayers.Handler.Box(
				this, {done: this.selectBox},
				{boxDivClassName: "olHandlerBoxSelectFeature"}
			); 
		}

		// @since SaigaiTask-2.2
		// IE11の場合だとタッチ操作に対応しているためなのか、
		// IE11でマウスのクリックをしたときに touchstartイベントが発火される。
		// タイミングによってはIE11でテキストメモのダブルクリックができない場合があるため、
		// IE11ではタッチ操作をできないようにする。
		// @see OpenLayers.Handler.Feature#touchstart
		if(!me.isTouchDevice()) {
			me.handlers.feature.touchstart=null;
			//me.handlers.drag.touchstart=null;
		}
		else {
			// タッチ操作ができる場合は、
			// タッチすると clickイベントが発火されるため、
			// タッチハンドラをここで定義する
			me.handlers.feature.callbacks.click = function(feature) {
				me.directTouch = false;
				if(me.isTouchDevice()) {
					// タッチ操作ではmouseoverができないため、
					// mouseoverで dragコントロールを有効化できないのでその対策
					if(me.handlers.drag.active==false) {
						//console.log("touchclick "+feature.attributes.name);

						// ダイレクトタッチフラグをON
						me.directTouch = true;

						// イベントをdragコントロールにevtをセット
						var evt = me.handlers.feature.evt;
						me.handlers.drag.evt = evt;

						// タッチ操作ではマウスオーバが無いので、
						// タッチしたら即座にマウスオーバ扱いにする
						me.overFeature(feature);
						
						var evt = me.handlers.feature.evt;
						// クリック時にドラッグコントロールを有効にする
						//me.handlers.drag.activate();
						// タッチしてすぐにドラッグできるように
						// このクリックイベントをドラッグコントロールのdragstartを手動で呼び出す。
						// downFeatureはこの中で実行される
						me.handlers.drag.dragstart(evt)
					}
				}
				return false;
			};
		}

		// dragのデバッグ用
		var drag = this.handlers.drag;
		drag._activate = drag.activate;
		drag.activate = function(){ console.log("[drag] activate"); return drag._activate(); };
		drag._deactivate = drag.deactivate;
		drag.deactivate = function(){ console.log("[drag] deactivate"); return drag._deactivate(); };
	},
	
	/**
	 * Method: activate
	 * Activates the control.
	 * 
	 * Returns:
	 * {Boolean} The control was effectively activated.
	 */
	activate: function () {
		if (!this.active) {
			if(this.layers) {
				this.map.addLayer(this.layer);
			}
			this.handlers.feature.activate();
			this.handlers.keyboard.activate();
			if (this.box) {
				this.handlers.box.activate();
			}
		}
		return OpenLayers.Control.prototype.activate.apply(
			this, arguments
		);
	},
	
	/**
	 * Method: deactivate
	 * Deactivates the control.
	 * 
	 * Returns:
	 * {Boolean} The control was effectively deactivated.
	 */
	deactivate: function () {
		if (this.active) {
			this.handlers.feature.deactivate();
			this.handlers.keyboard.deactivate();
			this.handlers.drag.deactivate();
			if(this.box) this.handlers.box.deactivate();
			this.mouseFeature = null;
			if(this.layers) {
				this.map.removeLayer(this.layer);
			}
		}
		return OpenLayers.Control.prototype.deactivate.apply(
			this, arguments
		);
	},

	/**
	 * @since SaigaiTask-2.2
	 * @return タッチ可能なブラウザであれば true
	 */
	isTouchDevice: function() {
		// PC用chromeでもタッチ操作可能なため true になるので注意
		return 'ontouchstart' in document.documentElement;
	},
	
	/**
	 * Method: overFeature
	 * Called on over a feature.
	 * Only responds if this.hover is true.
	 *
	 * Parameters:
	 * feature - {<OpenLayers.Feature.Vector>} 
	 */
	overFeature: function(feature) {
		//console.log("overFeature");
		//console.log("feature "+feature.attributes.name);
		if(!this.handlers.drag.dragging) {
			//console.log("this.handlers.drag.activate();");
			this.handlers.drag.activate();
			this.over = true;
			this.mouseFeature = feature;
			OpenLayers.Element.addClass(this.map.viewPortDiv, this.displayClass + "Over");
		} else {
			if(this.box) this.handlers.box.deactivate();
			if(this.mouseFeature && feature && this.mouseFeature.id == feature.id) {
				this.over = true;
			} else {
				this.over = false;
			}
		}
		if(this.hover && feature) {
			if(this.highlightOnly) {
				this.highlight(feature);
			} else if(OpenLayers.Util.indexOf(
				feature.layer.selectedFeatures, feature) == -1) {
				this.select(feature);
			}
		}
	},
	
	/**
	 * Method: outFeature
	 * Called on out of a selected feature.
	 * Only responds if this.hover is true.
	 *
	 * Parameters:
	 * feature - {<OpenLayers.Feature.Vector>} 
	 */
	outFeature: function(feature) {
		//console.log("outFeature");
		//console.log("feature "+feature.attributes.name);
		if(!this.handlers.drag.dragging) {
			this.over = false;
			this.mouseFeature = null;
			if(!this.directTouch) this.handlers.drag.deactivate();
			//console.log("this.handlers.drag.deactivate();");
			OpenLayers.Element.removeClass(
				this.map.viewPortDiv, this.displayClass + "Over"
			);
		} else {
			if (this.mouseFeature && this.mouseFeature.id == feature.id) {
				this.over = false;
			}
		}
		if(this.box) this.handlers.box.activate();
		if(this.hover) {
			if(this.highlightOnly) {
				// we do nothing if we're not the last highlighter of the
				// feature
				if(feature._lastHighlighter == this.id) {
					// if another select control had highlighted the feature before
					// we did it ourself then we use that control to highlight the
					// feature as it was before we highlighted it, else we just
					// unhighlight it
					if(feature._prevHighlighter && feature._prevHighlighter != this.id) {
						delete feature._lastHighlighter;
						var control = this.map.getControl(
							feature._prevHighlighter);
						if(control) {
							control.highlight(feature);
						}
					} else {
						this.unhighlight(feature);
					}
				}
			} else {
				this.unselect(feature);
			}
		}
	},
	
	/** キーによってFeatureがマウスの下から消える可能性がある場合に呼び出す */
	outFeatureByKey : function()
	{
		this.mouseFeature = null;
	},
	
	////////////////////////////////
	/** featureダブルクリック時に呼ばれる関数 ダミー */
	onDblClick : function(){},
	/** Handler.Featureダブルクリック時に実行 */
	dblclickFeature: function(feature) {
		//console.log("dblClick");
		this.onDblClick(feature);
	},
	
	/** 地図上でのキー入力時に呼ばれる関数 ダミー */
	onKeyDown : function(){},
	/** キーイベント 地図上のみ有効になるように地図内かどうかは別途取得 */
	keydown : function(evt)
	{
		this.onKeyDown(evt);
	},
	
	////////////////////////////////
	// from DragFeature
	/**
	 * Method: downFeature
	 * Called when the drag handler detects a mouse-down.
	 *
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} Location of the mouse event.
	 */
	downFeature: function(pixel) {
		//console.log("downFeature");
		this.lastPixel = pixel;
		// @since SaigaiTask-2.2
		// タッチ操作時にフィーチャが選択済みの場合に地図をドラッグしようとすると、
		// 地図移動ではなく、フィーチャ移動になってしまうため、
		// mouseFeatureはここで取得しなおす
		if (!!this.mouseFeature) {
			var evtFeature = this.layer.getFeatureFromEvent(this.handlers.drag.evt);
			if(this.mouseFeature!=evtFeature) {
				//console.log("overwrite mouseFeature");
				this.mouseFeature=evtFeature;
			}
			// mouseFeatureが無くなった場合は空にする
			if(!this.mouseFeature) {
				this.handlers.feature.layer.selectedFeatures = [];
			}
		}
		//キーで削除等の場合this.mouseFeatureがnullになっている DragはactiveなのでmouseDownでoutかoverの処理をする
		if (!this.mouseFeature) {
			this.mouseFeature = this.layer.getFeatureFromEvent(this.handlers.drag.evt);
			//削除等で消えた下にfeatureが無ければoutFeatureと同等の処理
			if (!this.mouseFeature) {
				this.outFeature();
				return;
			}
			this.mouseFeature.layer = this.layer;
			this.overFeature(this.mouseFeature);
		}
		//console.log("feature "+this.mouseFeature.attributes.name);
		var selected = OpenLayers.Util.indexOf(this.mouseFeature.layer.selectedFeatures, this.mouseFeature) != -1;
		if (selected) {
			//選択されていたらそのままドラッグ
			if (this.handlers.drag.evt[this.toggleKey]) {
				//トグルなら非選択
				this.unselect(this.mouseFeature);
			}
		} else {
			if (this.handlers.drag.evt[this.toggleKey]) {
				//トグルなら非選択
				this.select(this.mouseFeature);
			} else {
				//選択されていない場合は選択
				this.clickFeature(this.mouseFeature);
			}
		}
		this.onStart(this.mouseFeature, pixel, this.handlers.drag.evt[this.toggleKey]);
	},
	
	/**
	 * Method: moveFeature
	 * Called when the drag handler detects a mouse-move.  Also calls the
	 *	   optional onDrag method.
	 * 
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} Location of the mouse event.
	 */
	moveFeature: function(pixel) {
		var me = this;
		//console.log("moveFeature");
		var res = this.map.getResolution();
		for (var i=this.layer.selectedFeatures.length-1; i>=0; i--) {
			feature = this.layer.selectedFeatures[i];
			if (feature.geometry) {
				feature.geometry.move(res * (pixel.x - this.lastPixel.x), res * (this.lastPixel.y - pixel.y));
			}
			this.layer.drawFeature(feature);
		}
		this.lastPixel = pixel;
		this.onDrag(pixel);
	},

	/**
	 * Method: upFeature
	 * Called when the drag handler detects a mouse-up.
	 * 
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} Location of the mouse event.
	 */
	upFeature: function(pixel) {
		//console.log("upFeature");
		//console.log("feature "+this.mouseFeature.attributes.name);
		if(!this.over) {
			if(!this.isTouchDevice()) this.handlers.drag.deactivate();
		}
	},

	/**
	 * Method: doneDragging
	 * Called when the drag handler is done dragging.
	 *
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} The last event pixel location.	If this event
	 *	   came from a mouseout, this may not be in the map viewport.
	 */
	doneDragging: function(pixel) {
		this.onComplete(pixel);
	},

	/**
	 * Method: cancel
	 * Called when the drag handler detects a mouse-out (from the map viewport).
	 */
	cancel: function() {
		//console.log("cancel");
		if(!this.isTouchDevice()) this.handlers.drag.deactivate();
		this.over = false;
	},
	////////////////////////////////
	

	/** 
	 * Method: setMap
	 * Set the map property for the control. 
	 * 
	 * Parameters:
	 * map - {<OpenLayers.Map>} 
	 */
	setMap: function(map) {
		this.handlers.feature.setMap(map);
		this.handlers.drag.setMap(map);
		this.handlers.keyboard.setMap(map);
		if(this.box) this.handlers.box.setMap(map);
		
		OpenLayers.Control.prototype.setMap.apply(this, arguments);
	},
	
	/** クラス名
	 * @final @type String */
	CLASS_NAME: "OpenLayers.Control.SelectDragFeature"
});
