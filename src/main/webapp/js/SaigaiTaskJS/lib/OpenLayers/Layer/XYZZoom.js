/* Copyright (c) 2006-2013 by OpenLayers Contributors (see authors.txt for
 * full list of contributors). Published under the 2-clause BSD license.
 * See license.txt in the OpenLayers distribution or repository for the
 * full text of the license. */

/**
 * requires OpenLayers/Layer/Grid.js
 */

/** 
 * Class: OpenLayers.Layer.XYZZoom
 * The XYZ class is designed to make it easier for people who have tiles
 * arranged by a standard XYZ grid. 
 * 
 * This clone from OpenLayers.Layer.XYZ.
 * Customized for digital zoom and tile direction.
 * 
 * Inherits from:
 *	- <OpenLayers.Layer.Grid>
 */
OpenLayers.Layer.XYZZoom = OpenLayers.Class(OpenLayers.Layer.Grid, {
	
	isBaseLayer: true,
	
	sphericalMercator: false,
	
	zoomOffset: 0,
	
	serverResolutions: null,
	
	/** 最大ズームレベル これを超えたらデジタルズーム */
	maxZoomLevel : 22,
	
	/** 変更前のタイルサイズ OpenLayers.Size*/
	orgTileSize : null,
	
	/**
	 * Constructor: OpenLayers.Layer.XYZ
	 *
	 * Parameters:
	 * name - {String}
	 * url - {String}
	 * options - {Object} Hashtable of extra options to tag onto the layer
	 */
	initialize: function(name, url, options) {
		if (options && options.sphericalMercator || this.sphericalMercator) {
			options = OpenLayers.Util.extend({
				projection: "EPSG:900913",
				numZoomLevels: 22
			}, options);
		}
		
		if (options) {
			if (options.maxZoomLevel > 0) this.maxZoomLevel = options.maxZoomLevel;
			//Gridには送らない
			delete options.maxZoomLevel;
		}
		
		OpenLayers.Layer.Grid.prototype.initialize.apply(this, [
			name || this.name, url || this.url, {}, options
		]);
		
	},
	
	/**
	 * APIMethod: clone
	 * Create a clone of this layer
	 *
	 * Parameters:
	 * obj - {Object} Is this ever used?
	 * 
	 * Returns:
	 * {<OpenLayers.Layer.XYZ>} An exact clone of this OpenLayers.Layer.XYZ
	 */
	clone: function (obj) {
		
		if (obj == null) {
			obj = new OpenLayers.Layer.XYZ(this.name, this.url, this.getOptions());
		}

		//get all additions from superclasses
		obj = OpenLayers.Layer.Grid.prototype.clone.apply(this, [obj]);

		return obj;
	},

	/**
	 * Method: getURL
	 *
	 * Parameters:
	 * bounds - {<OpenLayers.Bounds>}
	 *
	 * Returns:
	 * {String} A string with the layer's url and parameters and also the
	 *			passed-in bounds and appropriate tile size specified as
	 *			parameters
	 */
	getURL: function (bounds) {
		var xyz = this.getXYZ(bounds);
		var url = this.url;
		if (OpenLayers.Util.isArray(url)) {
			var s = '' + xyz.x + xyz.y + xyz.z;
			url = this.selectUrl(s, url);
		}
		
		return OpenLayers.String.format(url, xyz);
	},
	
	/**
	 * Method: getXYZ
	 * Calculates x, y and z for the given bounds.
	 *
	 * Parameters:
	 * bounds - {<OpenLayers.Bounds>}
	 *
	 * Returns:
	 * {Object} - an object with x, y and z properties.
	 */
	getXYZ: function(bounds) {
		bounds = this.adjustBounds(bounds);
		var res = this.getServerResolution();
		var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
		var y = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
		var z;
		if (this.map.getZoom() > this.maxZoomLevel) {
			z = this.maxZoomLevel;
			if (this.zoomOffset) z += this.zoomOffset;
		} else {
			z = this.getServerZoom();
		}
		
		if (this.wrapDateLine) {
			var limit = Math.pow(2, z);
			x = ((x % limit) + limit) % limit;
		}
		
		return {'x': x, 'y': y, 'z': z};
	},
	
	/* APIMethod: setMap
	 * When the layer is added to a map, then we can fetch our origin 
	 *	  (if we don't have one.) 
	 * 
	 * Parameters:
	 * map - {<OpenLayers.Map>}
	 */
	setMap: function(map) {
		OpenLayers.Layer.Grid.prototype.setMap.apply(this, arguments);
		if (!this.tileOrigin) { 
			this.tileOrigin = new OpenLayers.LonLat(this.maxExtent.left, this.maxExtent.bottom);
		}
	},
	
	moveTo: function(bounds, zoomChanged, dragging) {
		if (zoomChanged) {
			var dz = this.map.getZoom() - this.maxZoomLevel;
			if (dz > 0) {
				//タイルサイズ変更
				var rate = Math.pow(2, dz);
				this.tileSize = new OpenLayers.Size(
					this.orgTileSize.w * rate, this.orgTileSize.h * rate
				);
			} else {
				this.tileSize = this.orgTileSize;
			}
		}
		return OpenLayers.Layer.Grid.prototype.moveTo.apply(this, arguments);
	},
	
	setTileSize: function(size) {
		OpenLayers.Layer.Grid.prototype.setTileSize.apply(this, [size]);
		this.orgTileSize = this.tileSize;
	},
	
	CLASS_NAME: "OpenLayers.Layer.XYZZoom"
});
