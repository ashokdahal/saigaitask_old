/* Copyright (c) 2006-2010 by OpenLayers Contributors (see authors.txt for 
 * full list of contributors). Published under the Clear BSD license.  
 * See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */


/**
 * @requires OpenLayers/Layer/Google.js
 */

/**
 * Constant: OpenLayers.Layer.Google.v3
 * 
 * Mixin providing functionality specific to the Google Maps API v3. Note that
 * this layer configures the google.maps.map object with the "disableDefaultUI"
 * option set to true. Using UI controls that the Google Maps API provides is
 * not supported by the OpenLayers API.
 */
OpenLayers.Layer.Google.v3 = {
    
    /**
     * Constant: DEFAULTS
     * {Object} It is not recommended to change the properties set here. Note
     * that Google.v3 layers only work when sphericalMercator is set to true.
     * 
     * (code)
     * {
     *     maxExtent: new OpenLayers.Bounds(
     *         -128 * 156543.0339,
     *         -128 * 156543.0339,
     *         128 * 156543.0339,
     *         128 * 156543.0339
     *     ),
     *     sphericalMercator: true,
     *     maxResolution: 156543.0339,
     *     units: "m",
     *     projection: "EPSG:900913"
     * }
     * (end)
     */
    DEFAULTS: {
        maxExtent: new OpenLayers.Bounds(
            -128 * 156543.0339,
            -128 * 156543.0339,
            128 * 156543.0339,
            128 * 156543.0339
        ),
        sphericalMercator: true,
        maxResolution: 156543.0339,
        units: "m",
        projection: "EPSG:900913"
    },

    /** 
     * Method: loadMapObject
     * Load the GMap and register appropriate event listeners. If we can't 
     *     load GMap2, then display a warning message.
     */
    loadMapObject:function() {
        if (!this.type) {
            this.type = google.maps.MapTypeId.ROADMAP;
        }
        var mapObject;
        var cache = OpenLayers.Layer.Google.cache[this.map.id];
        if (cache) {
            // there are already Google layers added to this map
            mapObject = cache.mapObject;
            // increment the layer count
            ++cache.count;
        } else {
            // this is the first Google layer for this map

            var container = this.map.viewPortDiv;
            var div = document.createElement("div");
            div.id = this.map.id + "_GMapContainer";
            div.style.position = "absolute";
            div.style.width = "100%";
            div.style.height = "100%";
            container.appendChild(div);

			var curtain = document.createElement("div");
			curtain.id = this.map.id + "_GMapCurtain";
			curtain.style.cssText = "position:absolute; width:100%; height:100%; z-index:400; background-color:transparent; color:white; font-size:15pt; padding-top:100px; text-align:center;";
			container.appendChild(curtain);
			this.curtain = curtain;

            // create GMap and shuffle elements
            var center = this.map.getCenter();
            
//			this.maxZoom = parseInt(this.options['MAX_ZOOM_LEVEL']);
//			this.minZoom = parseInt(this.options['MIN_ZOOM_LEVEL']);

//			var stylez = [{ featureType: "road", elementType: "all", stylers: [{ visibility: "on" },{ hue: "#ffaa00" },{ lightness: 53 },{ saturation: -31 }] }];
//			var stylez = [ { featureType: "all", elementType: "all", stylers: [ { visibility: "on" }, { hue: "#ffaa00" }, { lightness: 53 }, { saturation: -93 } ] } ];

			var mapOptions = {
                center: center ?
                    new google.maps.LatLng(center.lat, center.lon) :
                    new google.maps.LatLng(0, 0),
                zoom: this.map.getZoom() || 0,
				mapTypeId: this.type,
				mapTypeControlOptions: {
					mapTypeIds: [this.type]
				},
				disableDefaultUI: true,
				keyboardShortcuts: false,
				draggable: false,
				disableDoubleClickZoom: true,
				scrollwheel: false,
				streetViewControl: false
			};

            mapObject = new google.maps.Map(div, mapOptions);

/*
		var nostyledMapType = this.setStyledMapType(mapObject, "google1", this.style1);
		var styledMapType = this.setStyledMapType(mapObject, "google2", this.style2);
		var shobunshaMapType = this.setWMSMapType(mapObject, "shobunsha");
		var inetMapType = this.setWMSMapType(mapObject, "layer");

//		mapObject.mapTypes.set("google1", nostyledMapType);
//		mapObject.mapTypes.set("google2", styledMapType);
//		mapObject.mapTypes.set("shobunsha", shobunshaMapType);
//		mapObject.overlayMapTypes.insertAt(0, inetMapType);
*/

            // cache elements for use by any other google layers added to
            // this same map
            cache = {
                mapObject: mapObject,
                count: 1
            };
            OpenLayers.Layer.Google.cache[this.map.id] = cache;
            this.repositionListener = google.maps.event.addListenerOnce(
                mapObject, 
                "center_changed", 
                OpenLayers.Function.bind(this.repositionMapElements, this)
            );
            this.repositionListener = google.maps.event.addListener(
                mapObject,
                "layer_changed", 
                OpenLayers.Function.bind(this.repositionMapElements, this)
            );
			// add go
            this.repositionListener = google.maps.event.addListener(
                mapObject,
                "zoom_changed", 
                OpenLayers.Function.bind(this.rezoomMapElements, this)
            );
            this.repositionListener = google.maps.event.addListener(
                mapObject,
                "layer_changed", 
                OpenLayers.Function.bind(this.rezoomMapElements, this)
            );
        }

        this.mapObject = mapObject;
		var MERCATOR_RANGE = 256;
		this.tileSize = new google.maps.Size(MERCATOR_RANGE, MERCATOR_RANGE);
		if (this.options['SRS'] == "EPSG:900913" || this.options['SRS'] == "EPSG:3857") {
			var type = this.type.toUpperCase();
			if (this.options['STYLES'] != null && typeof this.options['STYLES'] != 'undefined' && this.options['STYLES'].length != 0) {
				eval("var stylez = "+this.options['STYLES']);
				this.setStyledMapType(mapObject, this.type, stylez);
			} else {
				if (type.indexOf("SATELLITE") == 0) {
					type = google.maps.MapTypeId.SATELLITE;
				} else if (type.indexOf("HYBRID") == 0) {
					type = google.maps.MapTypeId.HYBRID;
				} else if (type.indexOf("TERRAIN") == 0) {
					type = google.maps.MapTypeId.TERRAIN;
//					this.MAX_ZOOM_LEVEL = 15;
				} else if (type.indexOf("ROADMAP") == 0) {
					type = google.maps.MapTypeId.ROADMAP;
				}
				this.type = type;
			}
		} else {
			if (this.options['FORMAT'] == "image/png") {
				this.isPng = true;
			}
			this.setWMSMapType(mapObject, this.type, this.options['LAYERS']);
		}

//		mapObject.setMapTypeId("google1");

        this.setGMapVisibility(this.visibility);
    },

	mapType: null,

//	style1: [ ],
//	style2: [ { featureType: "road", elementType: "all", stylers: [ { hue: "#ffc300" }, { lightness: 70 } ] },{ featureType: "transit.line", elementType: "all", stylers: [ { hue: "#ffaa00" }, { lightness: 50 } ] }, { featureType: "Water", elementType: "all", stylers: [ { lightness: 50 } ] }, { featureType: "Landscape", elementType: "all", stylers: [ { lightness: 50 } ] } ],

	setStyledMapType: function(map, name, style) {
		var mapType = new google.maps.StyledMapType(style, { name: name, minZoom:this.minZoom, maxZoom:this.maxZoom });
		this.mapType = mapType;
		map.mapTypes.set(name, mapType);
	},

	getURL : function() {
		var url = this.options['URL'];
		if (this.options['tilecache']) {
			var tmp = url.split("/");
			url = tmp[0]+"//"+tmp[2]+"/"+this.type;
		} else {
			url = this.options['URL'];
			url += "&REQUEST="+this.options['REQUEST'];
//			url += "&VERSION="+this.options['VERSION'];
//			url += "&SERVICE="+this.options['SERVICE'];
//			url += "&SRS=EPSG:4326";
			url += "&TRANSPARENT="+this.options['TRANSPARENT'];
			url += "&FORMAT="+this.options['FORMAT'];
			url += "&EXCEPTIONS="+this.options['EXCEPTIONS'];
			url += "&BGCOLOR="+this.options['BGCOLOR'];
			url += "&STYLES="+this.options['STYLES'];
			if (this.options['DEPTH']) {
				url += "&DEPTH="+this.options['DEPTH'];
			}
			if (this.options['ANTIALIASING']) {
				url += "&ANTIALIASING="+this.options['ANTIALIASING'];
			}
			if (this.options['CENTERCROSS']) {
				url += "&CENTERCROSS="+this.options['CENTERCROSS'];
			}
			if (this.options['SCALEGAUGE']) {
				url += "&SCALEGAUGE="+this.options['SCALEGAUGE'];
			}
			if (this.options['MAPDIR']) {
				url += "&MAPDIR="+this.options['MAPDIR'];
			}
			if (this.options['USERPROFILEKEY']) {
				url += "&USERPROFILEKEY="+this.options['USERPROFILEKEY'];
			}
			url += "&MAGIC="+this.options['MAGIC'];
		}
		return url;
	},

	layers: "",
	setOverLayers: function(type, layers) {
		this.layers = layers;
		var zoom = this.mapObject.getZoom();
		if (zoom == this.MAX_ZOOM_LEVEL) {
			this.mapObject.setZoom(zoom-1);
			this.mapObject.setZoom(zoom);
		} else {
			this.mapObject.setZoom(zoom+1);
			this.mapObject.setZoom(zoom);
		}
	},
	setLayers: function(layers) {
		this.layers = layers;
	},
	getLayers: function() {
		return this.layers;
	},

	setWMSMapType: function(map, name, layers) {
		var mapURL = this.getURL();
		var format = this.options['FORMAT'];
		var tilecache = this.tilecache;
		var width = this.tileSize.width, height = this.tileSize.height;
		if (!tilecache) {
			mapURL += "&WIDTH=" + width + "&HEIGHT=" + height;
		}
		this.setLayers(layers);
		var mapType = new google.maps.ImageMapType({
			getTileUrl: function(coord, zoom) {
				var proj = map.getProjection();
				var ratio = 1 << zoom;
				var x = coord.x, y = coord.y;
				if (this.tilecache) {
					var h0 = "p"+Math.floor(x/100);
					var h1 = x%100;
					var v0 = "p"+Math.floor(y/100);
					var v1 = y%100;
					return mapURL+"/"+zoom+"/"+h0+"/"+h1+"/"+v0+"/"+v1+"."+this.format;
				} else {
					var lngwx = (x!=0)? x*width/ratio : 0, lngwy = 0;
					var lngex = (x!=-1)? (x+1)*width/ratio : 0, lngey = 0;
					var latnx = 0, latny = (y!=0)? y*height/ratio : 0;
					var latsx = 0, latsy = (y!=-1)? (y+1)*height/ratio: 0;
					var lngw = proj.fromPointToLatLng(new google.maps.Point(lngwx, lngwy), true).lng();
					var lnge = proj.fromPointToLatLng(new google.maps.Point(lngex, lngey), true).lng();
					var latn = proj.fromPointToLatLng(new google.maps.Point(latnx, latny), true).lat();
					var lats = proj.fromPointToLatLng(new google.maps.Point(latsx, latsy), true).lat();

					var bbox = lngw+","+lats+","+lnge+","+latn;
					return mapURL + "&BBOX=" + bbox + "&LAYERS=" + this.getLayers();
				}
			},
			format: this.FORMAT.substr(6),
			tilecache: this.tilecache,
			layers: layers,
			layerObject: this,
			setLayers: function(layers) {
				this.layerObject.setLayers(layers);
			},
			getLayers: function() {
				return this.layerObject.getLayers();
			},
			tileSize: this.tileSize,
			isPng: this.isPng,
			opacity: this.opacity,
			minZoom: this.minZoom,
			maxZoom: this.maxZoom,
			name: name
		});
		if (this.options['TRANSPARENT'] != null && typeof this.options['TRANSPARENT'] != 'undefined') {
			if (this.options['TRANSPARENT'].toUpperCase() == "TRUE") {
				map.overlayMapTypes.insertAt(0, mapType);
			} else {
				map.mapTypes.set(name, mapType);
			}
		} else {
			map.mapTypes.set(name, mapType);
		}
		this.mapType = mapType;
	},

	tileSize: null,
	minZoom: 5,
	maxZoom: 20,
	isPng: false,
    
    /**
     * Method: repositionMapElements
     *
     * Waits until powered by and terms of use elements are available and then
     * moves them so they are clickable.
     */
    repositionMapElements: function() {
        // This is the first time any Google layer in this mapObject has been
        // made visible.  The mapObject needs to know the container size.
        google.maps.event.trigger(this.mapObject, "resize");
        
        var div = this.mapObject.getDiv().firstChild;
        if (!div || div.childNodes.length < 3) {
            this.repositionTimer = window.setTimeout(
                OpenLayers.Function.bind(this.repositionMapElements, this),
                250
            );
            return false;
        }

        var cache = OpenLayers.Layer.Google.cache[this.map.id];
        var container = this.map.viewPortDiv;

		// move the Map Data popup to the container, if any
		while (div.lastChild.style.display == "none") {
			container.appendChild(div.lastChild);
		}

        // move the ToS and branding stuff up to the container div
        var termsOfUse = div.lastChild;
        container.appendChild(termsOfUse);
        termsOfUse.style.zIndex = "1100";
        termsOfUse.style.bottom = "";
        termsOfUse.className = "olLayerGoogleCopyright olLayerGoogleV3";
        termsOfUse.style.display = "";
//        termsOfUse.style.display = "none";
        termsOfUse.style.left = "-2000px";
        termsOfUse.style.bottom = "-2000px";
        cache.termsOfUse = termsOfUse;

/*
        var poweredBy = div.lastChild;
        container.appendChild(poweredBy);
        poweredBy.style.zIndex = "1100";
        poweredBy.style.width = "100%";
        poweredBy.style.bottom = "";
        poweredBy.className = "olLayerGooglePoweredBy olLayerGoogleV3 gmnoprint";
        poweredBy.style.display = "";
        cache.poweredBy = poweredBy;
*/
        this.setGMapVisibility(this.visibility);

    },

    /**
     * Method: rezoomMapElements
     *
     */
	requestZoom: 0,
    rezoomMapElements: function() {
        
        var cache = OpenLayers.Layer.Google.cache[this.map.id];
        var container = this.map.viewPortDiv;

		var maxzoom = Math.max(this.requestZoom, this.mapObject.zoom);
		if (cache.mapObject.mapTypeId == "terrain") {
			if (15 < maxzoom) {
				this.curtain.style.backgroundColor = "silver";
				this.curtain.innerHTML = OpenLayers.i18n("notile");
			} else {
				this.curtain.style.backgroundColor = "transparent";
				this.curtain.innerHTML = "";
			}
		} else {
			this.curtain.style.backgroundColor = "transparent";
			this.curtain.innerHTML = "";		
		}
		this.requestZoom = this.mapObject.zoom;
    },

    /**
     * APIMethod: onMapResize
     */
    onMapResize: function() {
        if (this.visibility) {
            google.maps.event.trigger(this.mapObject, "resize");
        } else {
            if (!this._resized) {
                var layer = this;
                google.maps.event.addListenerOnce(this.mapObject, "tilesloaded", function() {
                    delete layer._resized;
                    google.maps.event.trigger(layer.mapObject, "resize");
                    layer.moveTo(layer.map.getCenter(), layer.map.getZoom());
                });
            }
            this._resized = true;
        }
    },

    /**
     * Method: setGMapVisibility
     * Display the GMap container and associated elements.
     * 
     * Parameters:
     * visible - {Boolean} Display the GMap elements.
     */
    setGMapVisibility: function(visible) {
        var cache = OpenLayers.Layer.Google.cache[this.map.id];
        if (cache) {
            var type = this.type;
            var layers = this.map.layers;
            var layer;
            for (var i=layers.length-1; i>=0; --i) {
                layer = layers[i];
                if (layer instanceof OpenLayers.Layer.Google &&
                            layer.visibility === true && layer.inRange === true) {
                    type = layer.type;
                    visible = true;
                    break;
                }
            }
            var container = this.mapObject.getDiv();
            if (visible === true) {
                this.mapObject.setMapTypeId(type);                
                container.style.left = "";
                if (cache.termsOfUse && cache.termsOfUse.style) {
                    cache.termsOfUse.style.left = "";
                    cache.termsOfUse.style.display = "";
//                    cache.poweredBy.style.display = "";            
//        cache.poweredBy.className = "olLayerGooglePoweredBy olLayerGoogleV3 gmnoprint";
                }
                cache.displayed = this.id;
            } else {
                delete cache.displayed;
                container.style.left = "-9999px";
                if (cache.termsOfUse && cache.termsOfUse.style) {
                    cache.termsOfUse.style.display = "none";
                    // move ToU far to the left in addition to setting
                    // display to "none", because at the end of the GMap
                    // load sequence, display: none will be unset and ToU
                    // would be visible after loading a map with a google
                    // layer that is initially hidden. 
                    cache.termsOfUse.style.left = "-9999px";
//                    cache.poweredBy.style.display = "none";
                }
            }
        }
    },
    
    /**
     * Method: getMapContainer
     * 
     * Returns:
     * {DOMElement} the GMap container's div
     */
    getMapContainer: function() {
        return this.mapObject.getDiv();
    },
    
  //
  // TRANSLATION: MapObject Bounds <-> OpenLayers.Bounds
  //

    /**
     * APIMethod: getMapObjectBoundsFromOLBounds
     * 
     * Parameters:
     * olBounds - {<OpenLayers.Bounds>}
     * 
     * Returns:
     * {Object} A MapObject Bounds, translated from olBounds
     *          Returns null if null value is passed in
     */
    getMapObjectBoundsFromOLBounds: function(olBounds) {
        var moBounds = null;
        if (olBounds != null) {
            var sw = this.sphericalMercator ? 
              this.inverseMercator(olBounds.bottom, olBounds.left) : 
              new OpenLayers.LonLat(olBounds.bottom, olBounds.left);
            var ne = this.sphericalMercator ? 
              this.inverseMercator(olBounds.top, olBounds.right) : 
              new OpenLayers.LonLat(olBounds.top, olBounds.right);
            moBounds = new google.maps.LatLngBounds(
                new google.maps.LatLng(sw.lat, sw.lon),
                new google.maps.LatLng(ne.lat, ne.lon)
            );
        }
        return moBounds;
    },


    /************************************
     *                                  *
     *   MapObject Interface Controls   *
     *                                  *
     ************************************/


  // LonLat - Pixel Translation
  
    /**
     * APIMethod: getMapObjectLonLatFromMapObjectPixel
     * 
     * Parameters:
     * moPixel - {Object} MapObject Pixel format
     * 
     * Returns:
     * {Object} MapObject LonLat translated from MapObject Pixel
     */
    getMapObjectLonLatFromMapObjectPixel: function(moPixel) {
        var size = this.map.getSize();
        var lon = this.getLongitudeFromMapObjectLonLat(this.mapObject.center);
        var lat = this.getLatitudeFromMapObjectLonLat(this.mapObject.center);
        var res = this.map.getResolution();

        var delta_x = moPixel.x - (size.w / 2);
        var delta_y = moPixel.y - (size.h / 2);
    
        var lonlat = new OpenLayers.LonLat(
            lon + delta_x * res,
            lat - delta_y * res
        ); 

        if (this.wrapDateLine) {
            lonlat = lonlat.wrapDateLine(this.maxExtent);
        }
        return this.getMapObjectLonLatFromLonLat(lonlat.lon, lonlat.lat);
    },

    /**
     * APIMethod: getMapObjectPixelFromMapObjectLonLat
     * 
     * Parameters:
     * moLonLat - {Object} MapObject LonLat format
     * 
     * Returns:
     * {Object} MapObject Pixel transtlated from MapObject LonLat
     */
    getMapObjectPixelFromMapObjectLonLat: function(moLonLat) {
        var lon = this.getLongitudeFromMapObjectLonLat(moLonLat);
        var lat = this.getLatitudeFromMapObjectLonLat(moLonLat);
        var res = this.map.getResolution();
        var extent = this.map.getExtent();
        var px = new OpenLayers.Pixel(
            (1/res * (lon - extent.left)),
            (1/res * (extent.top - lat))
        );    
        return this.getMapObjectPixelFromXY(px.x, px.y);
    },

  
    /** 
     * APIMethod: setMapObjectCenter
     * Set the mapObject to the specified center and zoom
     * 
     * Parameters:
     * center - {Object} MapObject LonLat format
     * zoom - {int} MapObject zoom format
     */
    setMapObjectCenter: function(center, zoom) {
        this.mapObject.setOptions({
            center: center,
            zoom: zoom
        });
    },
   
    
  // Bounds
  
    /** 
     * APIMethod: getMapObjectZoomFromMapObjectBounds
     * 
     * Parameters:
     * moBounds - {Object} MapObject Bounds format
     * 
     * Returns:
     * {Object} MapObject Zoom for specified MapObject Bounds
     */
    getMapObjectZoomFromMapObjectBounds: function(moBounds) {
        return this.mapObject.getBoundsZoomLevel(moBounds);
    },

    /************************************
     *                                  *
     *       MapObject Primitives       *
     *                                  *
     ************************************/


  // LonLat
    
    /**
     * APIMethod: getMapObjectLonLatFromLonLat
     * 
     * Parameters:
     * lon - {Float}
     * lat - {Float}
     * 
     * Returns:
     * {Object} MapObject LonLat built from lon and lat params
     */
    getMapObjectLonLatFromLonLat: function(lon, lat) {
        var gLatLng;
        if(this.sphericalMercator) {
            var lonlat = this.inverseMercator(lon, lat);
            gLatLng = new google.maps.LatLng(lonlat.lat, lonlat.lon);
        } else {
            gLatLng = new google.maps.LatLng(lat, lon);
        }
        return gLatLng;
    },
    
  // Pixel
    
    /**
     * APIMethod: getMapObjectPixelFromXY
     * 
     * Parameters:
     * x - {Integer}
     * y - {Integer}
     * 
     * Returns:
     * {Object} MapObject Pixel from x and y parameters
     */
    getMapObjectPixelFromXY: function(x, y) {
        return new google.maps.Point(x, y);
    },
        
    /**
     * APIMethod: destroy
     * Clean up this layer.
     */
    destroy: function() {
        if (this.repositionListener) {
            google.maps.event.removeListener(this.repositionListener);
        }
        if (this.repositionTimer) {
            window.clearTimeout(this.repositionTimer);
        }
        OpenLayers.Layer.Google.prototype.destroy.apply(this, arguments);
    },

    /** 
     * APIMethod: getFullRequestString
     * Combine the layer's url with its params and these newParams. 
     *   
     *     Add the SRS parameter from projection -- this is probably
     *     more eloquently done via a setProjection() method, but this 
     *     works for now and always.
     *
     * Parameters:
     * newParams - {Object}
     * altUrl - {String} Use this as the url instead of the layer's url
     * 
     * Returns:
     * {String} 
     */
    getFullRequestString:function(newParams, altUrl) {
        var projectionCode = this.map.getProjection();
        var value = (projectionCode == "none") ? null : projectionCode
        if (parseFloat(this.options.VERSION) >= 1.3) {
            this.options.CRS = value;
        } else {
            this.options.SRS = value;
        }

        return OpenLayers.Layer.Grid.prototype.getFullRequestString.apply(
                                                    this, arguments);
    },

    /**
     * APIMethod: reverseAxisOrder
     * Returns true if the axis order is reversed for the WMS version and
     * projection of the layer.
     * 
     * Returns:
     * {Boolean} true if the axis order is reversed, false otherwise.
     */
    reverseAxisOrder: function() {
        return (parseFloat(this.options.VERSION) >= 1.3);
    }
    
};
