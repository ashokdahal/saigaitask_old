var mapobj;
var mapobj_header;
var minzoomlevel = 4;
var maxzoomlevel = 15;
var initMyMap = function(id, searchcallback) {
    var search = new OpenLayers.Control.SearchBox({
        afterSearchBox: function(b) {
                addPolygonFromBounds(b, this.map);
            }
        });
    mapobj = new OpenLayers.Map({
        div: id,
        theme: null,
        projection: new OpenLayers.Projection("EPSG:900913"),
        units: "m",
        numZoomLevels: (maxzoomlevel-minzoomlevel+1),
        controls: [
            new OpenLayers.Control.Navigation({
                dragPanOptions: {
                    enableKinetic: true
                },
                zoomWheelEnabled:true
            }),
            new OpenLayers.Control.Attribution(),
            search,
            new OpenLayers.Control.Zoom()
        ]
    });
    search.deactivate();
    /*
    // 2016/06/22 でAPIキーが必須となったので、google の代わりに地理院タイルを利用する
    var gmap = new OpenLayers.Layer.Google("google",{
          type:"TERRAIN",
          SRS:"EPSG:900913",
          numZoomLevels: (maxzoomlevel-minzoomlevel+1),
          MIN_ZOOM_LEVEL: minzoomlevel,
          MAX_ZOOM_LEVEL: maxzoomlevel
        });
    mapobj.addLayers([gmap]);
    */
    mapobj.addLayer(new OpenLayers.Layer.XYZ(lang.__("Standard map"),
    		  "http://cyberjapandata.gsi.go.jp/xyz/std/${z}/${x}/${y}.png", {
    		    attribution: "<a href='http://maps.gsi.go.jp/development/ichiran.html' target='_blank'>" + lang.__("Geographical Survey Institute Tile") + "</a>",
    		    maxZoomLevel: 18
    		}));

    mapobj.addLayer(new OpenLayers.Layer.XYZ("OpenStreetMap",
    		"http://tile.openstreetmap.org/${z}/${x}/${y}.png", {
    			attribution: "map data &copy; <a href='http://osm.org/copyright' target='_blank'>OpenStreetMap</a> contributors",
    			maxZoomLevel: 18
    }));
    var center = new OpenLayers.LonLat(136.9,36).transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    var level = 5;
    mapobj.setCenter(center, level);
    return mapobj;
};
var analizeZoomLevel = function(left, bottom, right, top, map) {
    var gmap = map.getLayersByName("google");
    if (gmap.length == 0)
        return;
    var b = new OpenLayers.Bounds(left, bottom, right, top).transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    var w = map.div.clientWidth?map.div.clientWidth:map.div.offsetWidth;
    var h = map.div.clientHeight?map.div.clientHeight:map.div.offsetHeight;
    var dw = b.right - b.left;
    var dh = b.top - b.bottom;
    var level = 0;
    for (var i=maxzoomlevel; minzoomlevel<=i; i--) {
        if ((w-10)*gmap[0].RESOLUTIONS[i] < dw || (h-10)*gmap[0].RESOLUTIONS[i] < dh) {
            continue;
        }
        level = i;
        break;
    }
    level = level - minzoomlevel;
    return level;
};

OpenLayers.Control.SearchBox = OpenLayers.Class(OpenLayers.Control, {
    type: OpenLayers.Control.TYPE_TOOL,
    out: false,
    keyMask: null,
    alwaysSearch: false,
    draw: function() {
        this.handler = new OpenLayers.Handler.Box(this,{done: this.searchBox}, {keyMask: this.keyMask} );
        this.events.on({
            activate: this.iconOn,
            deactivate: this.iconOff
        });
    },
    searchBox: function (position) {
        if (position instanceof OpenLayers.Bounds) {
            var bounds;
            if (!this.out) {
                var minXY = this.map.getLonLatFromPixel({
                    x: position.left,
                    y: position.bottom
                });
                var maxXY = this.map.getLonLatFromPixel({
                    x: position.right,
                    y: position.top
                });
                bounds = new OpenLayers.Bounds(minXY.lon, minXY.lat,
                                               maxXY.lon, maxXY.lat);
            } else {
                var pixWidth = Math.abs(position.right-position.left);
                var pixHeight = Math.abs(position.top-position.bottom);
                var searchFactor = Math.min((this.map.size.h / pixHeight),
                    (this.map.size.w / pixWidth));
                var extent = this.map.getExtent();
                var center = this.map.getLonLatFromPixel(
                    position.getCenterPixel());
                var xmin = center.lon - (extent.getWidth()/2)*searchFactor;
                var xmax = center.lon + (extent.getWidth()/2)*searchFactor;
                var ymin = center.lat - (extent.getHeight()/2)*searchFactor;
                var ymax = center.lat + (extent.getHeight()/2)*searchFactor;
                bounds = new OpenLayers.Bounds(xmin, ymin, xmax, ymax);
            }
			var org = bounds.clone();
			this.afterSearchBox(org);

        } else { // it's a pixel
        }
    },
	beforeSearchBox: function() {},
	afterSearchBox: function() {},
	iconOn: function(evt) {},
	iconOff: function(evt) {},

    CLASS_NAME: "OpenLayers.Control.SearchBox"
});
var initVectorLayer = function(map) {
    var vector = getVectorLayer(map);
    if (!vector || vector.length == 0) {
        vector = new OpenLayers.Layer.Vector("vector",{
              isBaseLayer: false,
              wrapDataLine: false,
              vibility: true
            }
        );
        map.addLayer(vector);
    }
    return vector;
};
var addPolygonFromRect = function(left, bottom, right, top, map) {
    var b = new OpenLayers.Bounds(left, bottom, right, top);
    b = b.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913")),
    addPolygonFromBounds(b, map);
};
var addPolygonFromBounds = function(bounds, map) {
	addPolygon(new OpenLayers.Feature.Vector(bounds.toGeometry(), "polygon"), map);
};
var addPolygon = function(feature, map) {
    initVectorLayer(map);
    removePolygon(map);
    var vector = getVectorLayer(map);
    if (vector && vector.length == 1) {
        vector[0].addFeatures([feature]);
    }
};
var removePolygon = function(map) {
    var vector = getVectorLayer(map);
    if (vector && vector.length == 1) {
        vector[0].removeAllFeatures();
    }
};
var getSearchBoxControl = function(map) {
    return map.getControlsBy("CLASS_NAME", "OpenLayers.Control.SearchBox");
};
var getVectorLayer = function(map) {
    return map.getLayersBy("CLASS_NAME", "OpenLayers.Layer.Vector");
};
var clearMapArea = function(map){
    var search = getSearchBoxControl(map);
    if (search && search.length == 1) {
        removePolygon(map);
        search[0].deactivate();
    }
};
var startMapArea = function(map){
    initVectorLayer(map);
    clearMapArea(map);
    var search = getSearchBoxControl(map);
    if (search && search.length == 1) {
        search[0].activate();
    }
};


var WKTPOLYGONPATTERN = /POLYGON\(\(([0-9\.]+ +[0-9\.]+ *, *){4}[0-9\.]+ +[0-9\.]{1,} *\)\)/i;
var BBOXPATTERN = /([0-9\.]+ *, *){3}[0-9\.]+ */i;

var isWKTPolygon = function(wkt) {
	return wkt.match(WKTPOLYGONPATTERN);
}

var isBBox = function(bBox) {
	return bBox.match(BBOXPATTERN);
}


var parseWKTPologon = function(wkt) {
	if (isWKTPolygon(wkt)) {
		wkt = wkt.replace(/POLYGON\(\(|\)\)/i, "");
		var parts = wkt.split(",");
		var i;
		for (i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
			parts[i] = parts[i].replace(/  /, " ");
		}

		var box = {left:0, right:0, top:0, bottom:0};

		if (parts.length == 5) {
			var tokens = parts[0].split(" ");
			box.left = parseFloat(tokens[0]);
			box.top = parseFloat(tokens[1]);

			tokens = parts[2].split(" ");
			box.right = parseFloat(tokens[0]);
			box.bottom = parseFloat(tokens[1]);

			return box;
		}
		else
			return null;
	}
	else
		return null;
}


var parseBBox = function(bBox) {
	if (isBBox(bBox)) {
		bBox = bBox.replace(/ /, "");
		var tokens = bBox.split(",");
		var box = {left:0, right:0, top:0, bottom:0};

		if (tokens.length == 4) {
			box.left = parseFloat(tokens[0]);
			box.top = parseFloat(tokens[1]);
			box.right = parseFloat(tokens[2]);
			box.bottom = parseFloat(tokens[3]);
			return box;
		}
		else
			return null;
	}
	else
		return null;
}
