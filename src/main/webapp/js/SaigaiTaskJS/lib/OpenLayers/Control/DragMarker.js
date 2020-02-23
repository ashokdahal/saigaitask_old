/* Copyright (c) 2006 MetaCarta, Inc., published under a modified BSD license.
 * See http://svn.openlayers.org/trunk/openlayers/repository-license.txt 
 * for the full text of the license. */

/**
 * @ requires OpenLayers/Control.js
 * @ requires OpenLayers/Handler/Drag.js
 * @ requires OpenLayers/Handler/Feature.js
 */
/**
 * @class マーカードラックのためのコントロール
 * FeatureのDragの処理と同様の処理をマーカーで行う
 * 
 * Move a marker with a drag.
 */
OpenLayers.Control.DragMarker = OpenLayers.Class(OpenLayers.Control, {
	/** コントロールを初期化
	 * @param {OpenLayers.Layer.Vector} layer
	 * @param {Object} options
	 */
	initialize: function(layer, options) {
		OpenLayers.Control.prototype.initialize.apply(this, [options]);
		this.layer = layer;
		this.handlers = {
			drag: new OpenLayers.Handler.Drag(
				this, OpenLayers.Util.extend({
					down: this.downFeature,
					move: this.moveFeature,
					up: this.upFeature,
					out: this.cancel
					//done: this.doneDragging
				}, this.dragCallbacks), {
					documentDrag: this.documentDrag
				}
			)
		};
	},
	
	/** コントロールを破棄
	 * APIMethod: destroy
	 * Take care of things that are not handled in superclass
	 */
	destroy: function() {
		this.layer = null;
		OpenLayers.Control.prototype.destroy.apply(this, []);
	},

	/** コントロールを有効化
	 * APIMethod: activate
	 * Activate the control and the feature handler.
	 * 
	 * Returns:
	 * {Boolean} Successfully activated the control and feature handler.
	 */
	activate: function() {
		return (this.handlers.drag.activate() &&
				OpenLayers.Control.prototype.activate.apply(this, arguments));
	},

	/** コントロールを無効化
	 * APIMethod: deactivate
	 * Deactivate the control and all handlers.
	 * 
	 * Returns:
	 * {Boolean} Successfully deactivated the control.
	 */
	deactivate: function() {
		// the return from the handlers is unimportant in this case
		this.handlers.drag.deactivate();
		this.feature = null;
		this.dragging = false;
		this.lastPixel = null;
		OpenLayers.Element.removeClass(
			this.map.viewPortDiv, this.displayClass + "Over"
		);
		return OpenLayers.Control.prototype.deactivate.apply(this, arguments);
	},

	/** マウスがマーカ内に入ったときの処理
	 * Method: overFeature
	 * Called when the feature handler detects a mouse-over on a feature.
	 *	   This activates the drag handler.
	 *
	 * Parameters:
	 * feature - {<OpenLayers.Feature.Vector>} The selected feature.
	 */
	overFeature: function(feature) {
		if(!this.handlers.drag.dragging) {
			this.feature = feature;
			this.handlers.drag.activate();
			OpenLayers.Element.addClass(this.map.viewPortDiv, this.displayClass + "Over");
		}
		this.over = true;
	},

	/** マーカ上でマウスが押下されたときの処理
	 * Method: downFeature
	 * Called when the drag handler detects a mouse-down.
	 *
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} Location of the mouse event.
	 */
	downFeature: function(pixel) {
		if (this.over) {
			this.lastPixel = pixel;
			this.onStart(this.feature, pixel);
			this.handlers.drag.activate();
			this.handlers.drag.dragging = true;//outFeatureの挙動調整
		}
	},

	/** マウスが移動したときの処理
	 * Method: moveFeature
	 * Called when the drag handler detects a mouse-move.  Also calls the
	 *	   optional onDrag method.
	 * 
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} Location of the mouse event.
	 */
	moveFeature: function(pixel) {
		if (this.feature) {
			var px = this.feature.icon.px.add(pixel.x - this.lastPixel.x, pixel.y - this.lastPixel.y);
			this.feature.moveTo(px);
			this.lastPixel = pixel;
			this.onDrag(this.feature, pixel);
		}
	},

	/** マウスの押下が終わったときの処理
	 * Method: upFeature
	 * Called when the drag handler detects a mouse-up.
	 * 
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} Location of the mouse event.
	 */
	upFeature: function(pixel) {
		if(!this.over) {
			this.handlers.drag.deactivate();
		}
		this.onComplete(this.feature, pixel);
	},

	/** ドラッグ完了時の処理
	 * Method: doneDragging
	 * Called when the drag handler is done dragging.
	 *
	 * Parameters:
	 * pixel - {<OpenLayers.Pixel>} The last event pixel location.	If this event
	 *	   came from a mouseout, this may not be in the map viewport.
	 */
	doneDragging: function(pixel) {
		this.onComplete(this.feature, pixel);
	},

	/** マウスがマーカから出たときの処理
	 * Method: outFeature
	 * Called when the feature handler detects a mouse-out on a feature.
	 *
	 * Parameters:
	 * feature - {<OpenLayers.Feature.Vector>} The feature that the mouse left.
	 */
	outFeature: function(feature) {
		this.over = false;
		if(!this.handlers.drag.dragging) {
			this.handlers.drag.deactivate();
			OpenLayers.Element.removeClass(
				this.map.viewPortDiv, this.displayClass + "Over"
			);
			this.feature = null;
		}
	},
		
	/** ドラッグキャンセル時の処理
	 * Method: cancel
	 * Called when the drag handler detects a mouse-out (from the map viewport).
	 */
	cancel: function() {
		this.onComplete(this.feature, this.lastPixel);
		this.handlers.drag.deactivate();
		this.over = false;
		this.feature = null;
	},

	/**
	 * Method: setMap
	 * Set the map property for the control and all handlers.
	 *
	 * Parameters: 
	 * map - {<OpenLayers.Map>} The control's map.
	 */
	setMap: function(map) {
		this.handlers.drag.setMap(map);
		OpenLayers.Control.prototype.setMap.apply(this, arguments);
	},
	
	/** クラス名
	 * @final @type String */
	CLASS_NAME: "OpenLayers.Control.DragMarker"
});
