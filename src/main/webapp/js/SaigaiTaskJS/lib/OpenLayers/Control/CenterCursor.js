/* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/**
 * @class
 *
 * requires OpenLayers/Control.js
 */
OpenLayers.Control.CenterCursor = OpenLayers.Class(OpenLayers.Control,
{

	/** @type DOMElement */
	element: null,
	elementMap: null,

	size : 23,
	opacity : 0.8,

	/**
	 * @constructor
	 */
	initialize: function()
	{
		OpenLayers.Control.prototype.initialize.apply(this, arguments);
	},

	/**
	 * @type DOMElement
	 */
	draw: function()
	{
		OpenLayers.Control.prototype.draw.apply(this, arguments);

		var cursor = this.createCursor();
		this.div.appendChild(cursor);
		this.element = cursor;
		this.element.style.display = 'none';

		this.moveCenter();
		this.map.events.register("movestart", this, this.moveStart);
		this.map.events.register("resize", this, this.moveCenter);
		this.map.events.register("moveend", this, this.moveEnd);
		return this.div;
	},

	createCursor : function()
	{
		var color = 'red';
		var bgColor = 'white';
		var cursor = document.createElement("div");
		cursor.className = "CenterCursor";
		var s = cursor.style;
		s.width = s.height = "2px";
		s.position = "absolute";
		s.opacity = this.opacity;

		var half = Math.floor(this.size/2.0);
		cursor.appendChild(this.createLine(0,     half-1, half-3,3, bgColor));
		cursor.appendChild(this.createLine(half+4,half-1, half-3,3, bgColor));
		cursor.appendChild(this.createLine(half-1,0,      3,half-3, bgColor));
		cursor.appendChild(this.createLine(half-1,half+4, 3,half-3, bgColor));
		cursor.appendChild(this.createLine(1,     half,      half-4,1, color));
		cursor.appendChild(this.createLine(half+4,half, half-4,1, color));
		cursor.appendChild(this.createLine(half,1,      1,half-4, color));
		cursor.appendChild(this.createLine(half,half+4, 1,half-4, color));
		return cursor;
	},

	createLine : function(left, top, w, h, color)
	{
		var line = document.createElement("div");
		var s = line.style;
		s.position = "absolute";
		s.overflow = "hidden";
		s.top = top+"px";
		s.left = left+"px";
		s.width = w+"px";
		s.height = h+"px";
		s.backgroundColor = color;
		return line;
	},

	moveStart: function()
	{
		this.element.style.display = '';
		this.elementMap.style.display = 'none';
	},

	moveEnd: function()
	{
		this.element.style.display = 'none';
		this.elementMap.style.display = '';
		this.moveCenter();
	},

	moveCenter: function()
	{
		var s = this.map.getSize();
		this.element.style.left = ((s.w-this.size) / 2) + "px";
		this.element.style.top = ((s.h-this.size) / 2) + "px";
		//<span class="ja">地図上に描画</span><span class="en">Draw on the top of map</span>
		var div = this.map.layerContainerDiv;
		if (!this.elementMap) {
			this.elementMap = this.createCursor();
			this.elementMap.style.zIndex = 750;
			div.appendChild(this.elementMap);
		} else {
			this.elementMap.style.left = ((s.w-this.size) / 2 - div.offsetLeft) + "px";
			this.elementMap.style.top = ((s.h-this.size) / 2 - div.offsetTop) + "px";
		}
	},

	/** @final @type String */
	CLASS_NAME: "OpenLayers.Control.CenterCursor"
});
