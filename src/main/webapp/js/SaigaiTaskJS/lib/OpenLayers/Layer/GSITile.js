/* Copyright (c) 2006-2013 by OpenLayers Contributors (see authors.txt for
 * full list of contributors). Published under the 2-clause BSD license.
 * See license.txt in the OpenLayers distribution or repository for the
 * full text of the license. */

/**
 * requires OpenLayers/Layer/Grid.js
 * @requires OpenLayers/Layer/XYZZoom.js
 */

/**
 * Class: OpenLayers.Layer.XYZ
 * The XYZ class is designed to make it easier for people who have tiles
 * arranged by a standard XYZ grid.
 *
 * Inherits from:
 *	- <OpenLayers.Layer.Grid>
 */
OpenLayers.Layer.GSITile = OpenLayers.Class(OpenLayers.Layer.XYZZoom,
{
	gsiInfos : {
		STD:{url:"http://cyberjapandata.gsi.go.jp/xyz/std/${z}/${x}/${y}.png",maxZoomLevel:18}, //2～18
		ORT:{url:"http://cyberjapandata.gsi.go.jp/xyz/ort/${z}/${x}/${y}.jpg",maxZoomLevel:18}, //2～18
		GAZO1:{url:"http://cyberjapandata.gsi.go.jp/xyz/gazo1/${z}/${x}/${y}.jpg",maxZoomLevel:17}, //10～17
		GAZO2:{url:"http://cyberjapandata.gsi.go.jp/xyz/gazo2/${z}/${x}/${y}.jpg",maxZoomLevel:17}, //15～17
		GAZO3:{url:"http://cyberjapandata.gsi.go.jp/xyz/gazo3/${z}/${x}/${y}.jpg",maxZoomLevel:17}, //15～17
		GAZO4:{url:"http://cyberjapandata.gsi.go.jp/xyz/gazo4/${z}/${x}/${y}.jpg",maxZoomLevel:17}, //15～17
		TOHO1:{url:"http://cyberjapandata.gsi.go.jp/xyz/toho1/${z}/${x}/${y}.jpg",maxZoomLevel:17}, //15～17
		TOHO2:{url:"http://cyberjapandata.gsi.go.jp/xyz/toho2/${z}/${x}/${y}.jpg",maxZoomLevel:18}, //15～18
		TOHO3:{url:"http://cyberjapandata.gsi.go.jp/xyz/toho3/${z}/${x}/${y}.jpg",maxZoomLevel:18}, //15～18
		TOHO4:{url:"http://cyberjapandata.gsi.go.jp/xyz/toho4/${z}/${x}/${y}.jpg",maxZoomLevel:18}, //15～18
		PALE:{url:"http://cyberjapandata.gsi.go.jp/xyz/pale/${z}/${x}/${y}.png",maxZoomLevel:18}, //12～18
		//RELIEF:{url:"http://cyberjapandata.gsi.go.jp/xyz/relief/${z}/${x}/${y}.png",maxZoomLevel:15}, //5～15
		BLANK:{url:"http://cyberjapandata.gsi.go.jp/xyz/blank/${z}/${x}/${y}.png",maxZoomLevel:14} //5～14
	},
	//5-14 航空写真広域用
	BLANK_MAP_URL : "http://cyberjapandata.gsi.go.jp/xyz/blank/${z}/${x}/${y}.png",

	type : "STD",

	/** 標準のクレジット表記 */
	GSI_ATTRIBUTION : ((("<a href=\"https://maps.gsi.go.jp/development/ichiran.html\" target=\"_blank\">国土地理院</a>"))),


	initialize: function(name, type, options)
	{
		this.type = type;
		var gsiInfo = this.gsiInfos[type];

		if (!options) options = {};
		options.projection =  "EPSG:900913";

		this.url = gsiInfo.url;
		this.maxZoomLevel = gsiInfo.maxZoomLevel;

		//標準クレジットを設定
		this.attribution = this.GSI_ATTRIBUTION;

		OpenLayers.Layer.Grid.prototype.initialize.apply(this, [
			name || this.name, this.url, {}, options
		]);
	},

	clone: function (obj) {

		if (obj == null) {
			obj = new OpenLayers.Layer.GSITile(this.name, this.url, this.getOptions());
		}
		//get all additions from superclasses
		obj = OpenLayers.Layer.XYZZoom.prototype.clone.apply(this, [obj]);

		return obj;
	},

	getURL: function (bounds) {
		var xyz = this.getXYZ(bounds);
		var url = this.url;

		// 地図画面が HTTPS の場合は地理院タイルもHTTPSにする
		if(window.location.protocol=="https:") {
			url = url.replace("http:", "https:")
		}

		//航空写真はズームレベルに応じてURLを切り替える
		if (this.type == "PALE") {
			if (xyz.z < 12) {
				url = this.gsiInfos.STD.url;//標準地図
			}
		} else if (this.type != "STD" && this.type != "ORT") {
			if (xyz.z < 10 || (this.type != "GAZO1" && xyz.z < 15)) {
				url = this.BLANK_MAP_URL;
			}
		}

		if (OpenLayers.Util.isArray(url)) {
			var s = '' + xyz.x + xyz.y + xyz.z;
			url = this.selectUrl(s, url);
		}

		return OpenLayers.String.format(url, xyz);
	},

	moveTo: function(bounds, zoomChanged, dragging) {
		if (zoomChanged) {
			var z = this.map.getZoom();
			//attribution入れ替え
			var update = this.attribution == this.GSI_ATTRIBUTION;
			if (this.type == "STD" && 5 <= z && z <= 8) {
				this.attribution =
					"The bathymetric contours are derived from those contained within the GEBCO Digital Atlas,"
					+"published by the BODC on behalf of IOC and IHO (2003) (http://www.gebco.net)<br/>"
					+((("海上保安庁許可第２２２５１０号（水路業務法第２５条に基づく類似刊行物）<br/>")))
					//+lang.__("Japan Coast Guard permission No. 222510 (similar publications based on the waterway business Law Article 25)<br/>")
					+this.GSI_ATTRIBUTION;
			} else if (this.type == "ORT" && 5 <= z && z <= 12) {
				this.attribution =
					((("データソース：Landsat8画像(GSI,TSIC,GEO Grid/AIST), 海底地形(GEBCO)<br/>")))
					+this.GSI_ATTRIBUTION;
			} else {
				this.attribution = this.GSI_ATTRIBUTION;
			}
			//attributionコントロール更新
			if (update) {
				var controls = this.map.controls;
				for (var i=0; i<controls.length; i++) {
					if (controls[i].updateAttribution) controls[i].updateAttribution();
				}
			}
		}
		return OpenLayers.Layer.XYZZoom.prototype.moveTo.apply(this, arguments);
	},

	CLASS_NAME: "OpenLayers.Layer.GSITile"
});
