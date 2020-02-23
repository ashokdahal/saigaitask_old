/* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

/**
 * - 180420 eコミマップ trunk@3463 を反映
 */
/** <span class="ja">KMLのスタイルに対応したKMLFormat</span><span class="en">KML format that supports KML style</span> */
OpenLayers.Format.KMLStyle = OpenLayers.Class(OpenLayers.Format.KML,
{
	/** <span class="ja">基準とするフォントサイズ</span><span class="en">Standard fontsize</span> */
	FONTSIZE : 14,
	
	/** 画像取得前にstyleに設定する仮のサイズ */
	ICONSIZE : 48,
	
	/** スタイル無しの時の基本スタイル */
	defaultStyle : {
		pointRadius: 5,
		strokeColor: "#ff0000",
		strokeWidth : 2,
		fillColor: "#ff0000",
		fillOpacity : 0.5
	},
	
	/** <span class="ja">親のOpenLayers.Format.KMLを初期化</span><span class="en">Initialize parent's OpenLayers.Format.KML</span> */
	initialize : function(options) {
		OpenLayers.Format.KML.prototype.initialize.apply(this, [options]);
	},
	
	/**<span class="ja">FeatureをKMLに変換</span><span class="en">Convert Feature to KML</span> */
	write: function(features)
	{
		this.internalProjection.readyToUse = true;
		this.externalProjection.readyToUse = true;
		
		if (!(features instanceof Array)) {
			features = [features];
		}
		var kml = this.createElementNS(this.kmlns, "kml");
		var folder = this.createFolderXML();
		for(var i=0, len=features.length; i<len; ++i) {
			folder.appendChild(this.createPlacemarkXML(features[i]));
		}
		kml.appendChild(folder);
		return OpenLayers.Format.XML.prototype.write.apply(this, [kml]);
	},
	
	/** <span class="ja">KML読み込み.</span><span class="en">Read KML</span>
	 * <span class="ja">Firefoxで4096バイト以上の文字列を読み込むときに normalize() をするように修正</span><span class="en">When a string with over 4096 bytes is read on Firefox, fix to use normalize() </span> */
	parseData: function(data, options) {
		if(typeof data == "string") {
			xmldoc = OpenLayers.Format.XML.prototype.read.apply(this, [data]);
		}
		//for Firefox 4096 bytes limit
		if (xmldoc.normalize) xmldoc.normalize();
		
		//IE11 retry
		if (!xmldoc.firstChild.tagName != "kml") {
			xmldoc = new DOMParser().parseFromString(data, 'text/xml');
		}
		
		var folderNode = xmldoc.firstChild.firstChild;
		while (folderNode && folderNode.nodeType == Node.TEXT_NODE) { folderNode = folderNode.nextSibling; }
		this.foldersDesc = OpenLayers.Util.getXmlNodeValue(this.getElementsByTagNameNS(folderNode, "*", "description")[0]);
		
		// Loop throught the following node types in this order and
		// process the nodes found 
		var types = ["Link", "NetworkLink", "Style", "StyleMap", "Placemark"];
		for(var i=0, len=types.length; i<len; ++i) {
			var type = types[i];
			var nodes = this.getElementsByTagNameNS(xmldoc, "*", type);
			// skip to next type if no nodes are found
			if(nodes.length == 0) { 
				continue;
			}
			switch (type.toLowerCase()) {
				// Fetch external links 
				case "link":
				case "networklink":
					this.parseLinks(nodes, options);
					break;
				// parse style information
				case "style":
					if (this.extractStyles) {
						this.parseStyles(nodes, options);
					}
					break;
				case "stylemap":
					if (this.extractStyles) {
						this.parseStyleMaps(nodes, options);
					}
					break;
				// parse features
				case "placemark":
					this.parseFeatures(nodes, options);
					break;
			}
		}
		
		//画像サイズを取得して幅高さを再計算する
		this.adjustImageSize(this.features);
		
		return this.features;
	},
	
	adjustImageSize : function(features)
	{
		var imageFeatuers = {};
		
		for (var i=0; i<features.length; i++) {
			var style = features[i].style;
			var url = style.externalGraphic;
			if (url) {
				//テキストは除外
				if (url)
				if (!imageFeatuers[url]) imageFeatuers[url] = [];
				imageFeatuers[url].push(features[i]);
			}
		}
		for (var url in imageFeatuers) {
			this._adjustImageSize(url, imageFeatuers[url]);
		}
	},
	_adjustImageSize : function(url, features)
	{
		var img = new Image();
		img.onload = function(){
			//メモ描画の古いデータはテキストが1x1の画像なのでサイズは変更しない
			if (img.width <= 1 || img.height <= 1) return;
			for (var i=0; i<features.length; i++) {
				var style = features[i].style;
				//スケール情報がgraphicScaleに保存されているので拡大
				if (style.graphicScale > 0) {
					var w = img.width*style.graphicScale;
					var h = img.height*style.graphicScale;
					//Offsetは仮サイズで指定されているので調整
					if (style.graphicXOffset) style.graphicXOffset = Math.round(parseFloat(style.graphicXOffset) * w/style.graphicWidth);
					if (style.graphicYOffset) style.graphicYOffset = Math.round(parseFloat(style.graphicYOffset) * h/style.graphicHeight);
					style.graphicWidth = w;
					style.graphicHeight = h;
				} else {
					if (img.width > img.height) {
						var h = style.graphicWidth * img.height/img.width;
						if (style.graphicYOffset) style.graphicYOffset = Math.round(parseFloat(style.graphicYOffset) * h/style.graphicHeight);
						style.graphicHeight = h;
					} else if (img.width < img.height) {
						var w = style.graphicHeight * img.width/img.height;
						if (style.graphicXOffset) style.graphicXOffset = Math.round(parseFloat(style.graphicXOffset) * w/style.graphicWidth);
						style.graphicWidth = w;
					}
				}
			}
			if (features[0].layer) features[0].layer.redraw();
		};
		img.src = url;
		img.onload();
	},
	
	getStyle: function(styleUrl, options) {
		var styleBaseUrl = OpenLayers.Util.removeTail(styleUrl);

		var newOptions = OpenLayers.Util.extend({}, options);
		newOptions.depth++;
		newOptions.styleBaseUrl = styleBaseUrl;

		// Fetch remote Style URLs (if not fetched before) 
		if (!this.styles[styleUrl] 
				&& !OpenLayers.String.startsWith(styleUrl, "#") 
				&& newOptions.depth <= this.maxDepth
				&& !this.fetched[styleBaseUrl] ) {

			var data = this.fetchLink(styleBaseUrl);
			if (data) {
				this.parseData(data, newOptions);
			}

		}
		
		//スタイルが無い場合はデフォルトスタイルを返す
		if (!this.styles[styleUrl]) return this.defaultStyle;
		
		// return requested style
		var style = OpenLayers.Util.extend({}, this.styles[styleUrl]);
		
		return style;
	},
	
	parseStyle : function(node) {
		var style = {};
		
		var types = ["LineStyle", "PolyStyle", "IconStyle", "BalloonStyle", "LabelStyle"];
		var type, nodeList, geometry, parser, styleTypeNode;
		for(var i=0, len=types.length; i<len; ++i) {
			type = types[i];
			styleTypeNode = this.getElementsByTagNameNS(node, "*", type)[0];
			if(!styleTypeNode) { 
				continue;
			}
			// only deal with first geometry of this type
			switch (type.toLowerCase()) {
				case "linestyle":
					var color = this.parseProperty(styleTypeNode, "*", "color");
					if (color) {
						var matches = (color.toString()).match(this.regExes.kmlColor);
						if (matches) {
							// transparency
							var alpha = matches[1];
							style["strokeOpacity"] = parseInt(alpha, 16) / 255;
							style["strokeColor"] = "#"+matches[4]+matches[3]+matches[2];
						}
					}
					
					var width = this.parseProperty(styleTypeNode, "*", "width");
					if (width < 1) width = 0;
					style["strokeWidth"] = width;
				case "polystyle":
					var color = this.parseProperty(styleTypeNode, "*", "color");
					if (color) {
						var matches = (color.toString()).match( this.regExes.kmlColor);
						if (matches) {
							// transparency
							var alpha = matches[1];
							style["fillOpacity"] = parseInt(alpha, 16) / 255;
							style["fillColor"] = "#"+matches[4]+matches[3]+matches[2];
						}
					}
					 // Check is fill is disabled
					var fill = this.parseProperty(styleTypeNode, "*", "fill");
					if (fill == "0") {
						style["fillColor"] = "none";
					}
					
					break;
					
				case "iconstyle":
					var iconNode = this.getElementsByTagNameNS(styleTypeNode, "*", "Icon")[0];
					if (iconNode) {
						var href = this.parseProperty(iconNode, "*", "href");
						if (href) {
							href = href.replace(/%3F/i, "?");
							
							// set scale
							var scale = parseFloat(this.parseProperty(styleTypeNode, "*", "scale") || 1);
							if (scale <= 0) scale = 1;
							
							var width = this.ICONSIZE * scale;
							var height = this.ICONSIZE * scale;
							
							if (OpenLayers.String.startsWith(href, "http://maps.google.com/mapfiles/kml")) {
								//google icons
								width = 64;
								height = 64;
								scale = scale / 2;
							} else {
								//画像読み込み後に設定されるまでの仮の画像サイズ Offset設定用
								var width = this.ICONSIZE * scale;
								var height = this.ICONSIZE * scale;
								//後から画像サイズを取得するのでここでスケールを設定
								style.graphicScale = scale;
							}
							
							// support for internal icons 
							//	  (/root://icons/palette-x.png)
							// x and y tell the position on the palette:
							// - in pixels
							// - starting from the left bottom
							// We translate that to a position in the list 
							// and request the appropriate icon from the 
							// google maps website
							var matches = href.match(this.regExes.kmlIconPalette);
							if (matches)  {
								var palette = matches[1];
								var file_extension = matches[2];
								var x = this.parseProperty(iconNode, "*", "x");
								var y = this.parseProperty(iconNode, "*", "y");
								var posX = x ? x/32 : 0;
								var posY = y ? (7 - y/32) : 7;
								var pos = posY * 8 + posX;
								href = "http://maps.google.com/mapfiles/kml/pal" 
									 + palette + "/icon" + pos + file_extension;
							}
							style["graphicOpacity"] = 1; // fully opaque
							style["externalGraphic"] = href;
							
							// hotSpots define the offset for an Icon
							var hotSpotNode = this.getElementsByTagNameNS(styleTypeNode, "*", "hotSpot")[0];
							if (hotSpotNode) {
								var x = parseFloat(hotSpotNode.getAttribute("x"));
								var y = parseFloat(hotSpotNode.getAttribute("y"));
								
								var xUnits = hotSpotNode.getAttribute("xunits");
								if (xUnits == "pixels") {
									style["graphicXOffset"] = -x * scale;
								}
								else if (xUnits == "insetPixels") {
									style["graphicXOffset"] = -width + (x * scale);
								}
								else if (xUnits == "fraction") {
									style["graphicXOffset"] = -width * x;
								}
								
								var yUnits = hotSpotNode.getAttribute("yunits");
								if (yUnits == "pixels") {
									style["graphicYOffset"] = -height + (y * scale);
								}
								else if (yUnits == "insetPixels") {
									style["graphicYOffset"] = -(y * scale);
								}
								else if (yUnits == "fraction") {
									style["graphicYOffset"] =  -height * (1 - y);
								}
							}
							
							style["graphicWidth"] = width;
							style["graphicHeight"] = height;
						}
					}
					break;
					
				case "balloonstyle":
					var balloonStyle = OpenLayers.Util.getXmlNodeValue(styleTypeNode);
					if (balloonStyle) {
						style["balloonStyle"] = balloonStyle.replace(this.regExes.straightBracket, "${$1}");
					}
					break;
					
				case "labelstyle":
					var color = this.parseProperty(styleTypeNode, "*", "color");
					if (color) {
						var matches = (color.toString()).match(this.regExes.kmlColor);
						if (matches) {
							var opacity = parseInt(matches[1], 16) / 255;
							style.fontOpacity = opacity; //Support OpenLayers2.9 later
							//<span class="ja">透明なら色を設定しない → ラベルを表示しない</span><span class="en">If it is transparent, color is not set  --> do not display label</span>
							if (opacity > 0) style.fontColor = "#"+matches[4]+matches[3]+matches[2];
						}
					}
					var scale = parseFloat(this.parseProperty(styleTypeNode, "*", "scale") || 1);
					if (scale <= 0) scale = 1;
					style.fontSize = this.FONTSIZE*scale;

					// 官民修正：テキストメモ表示の対応
					OpenLayers.Format.KMLStyleUtil.parseTextMemo(this, node, style);

					break;
				default:
			}
		}
		
		// Some polygons have no line color, so we use the fillColor for that
		if (!style["strokeColor"] && style["fillColor"]) {
			style["strokeColor"] = style["fillColor"];
		}
		
		var id = node.getAttribute("id");
		if (id && style) {
			style.id = id;
		}
		
		return style;
	},
	
	/** <span class="ja">featureのスタイル情報も含めたKMLのPlacemarkのXMLノード生成</span><span class="en">Create XML node of KML's Placemark which includes feature's style information</span> */
	createPlacemarkXML: function(feature)
	{
		// Placemark name
		var placemarkName = this.createElementNS(this.kmlns, "name");
		var name = feature.attributes.name || "";
		placemarkName.appendChild(this.createTextNode(name));
		
		// Placemark description
		var placemarkDesc = this.createElementNS(this.kmlns, "description");
		var desc = feature.attributes.description || "";// || this.placemarksDesc;
		placemarkDesc.appendChild(this.createTextNode(desc));
		
		// Placemark
		var placemarkNode = this.createElementNS(this.kmlns, "Placemark");
		if(feature.fid != null) {
			placemarkNode.setAttribute("id", feature.fid);
		}
		placemarkNode.appendChild(placemarkName);
		placemarkNode.appendChild(placemarkDesc);
		
		if (feature.style) {
			var styleNode = this.createStyleNode(feature.style);
			placemarkNode.appendChild(styleNode);
		}
		// Geometry node (Point, LineString, etc. nodes)
		var geometryNode = this.buildGeometryNode(feature.geometry);
		placemarkNode.appendChild(geometryNode);
		
		// TBD - deal with remaining (non name/description) attributes.
		return placemarkNode;
	},
	
	/** <span class="ja">RGB表記＋不透明度をKMLの色表記(AABBGGRR)に変換</span><span class="en">Convert RGB format and transparency to KML's color format (AABBGGRR)</span> */
	createKmlColorNode: function(color, opacity)
	{
		if (opacity === undefined || opacity === null) {
			var alpha = "ff";
		} else {
			var alpha = Math.round(parseFloat(opacity) * 255).toString(16);
			if (alpha.length==1) alpha = "0"+alpha;
		}
		
		// TBD: handle '#ccc', 'red'
		// only match '#rrggbb'
		var value;
		if (!color || color.length < 4) {
			value = alpha + '000000';
		} else if (color.length < 7) {
			//#FFF
			var r = color.slice(1, 2);
			var g = color.slice(2, 3);
			var b = color.slice(3, 4);
			value = alpha + b + b + g + g + r + r;
		} else {
			//#FFFFFF
			var r = color.slice(1, 3);
			var g = color.slice(3, 5);
			var b = color.slice(5, 7);
			value = alpha + b + g + r;
		}
		
		var colorNode = this.createElementNS(this.kmlns, "color");
		colorNode.appendChild(this.createTextNode(value));
		return colorNode;
	},
	
	createStyleNode: function(style)
	{
		// TBD: search if the already exists.
		if (true) {
			var styleNode = this.createElementNS(this.kmlns, "Style");
			var id = OpenLayers.Util.createUniqueID("style_");
			styleNode.setAttribute("id", id);
			
			// LineStyle
			if (style.strokeColor || style.strokeOpacity) {
				var lineNode = this.createElementNS(this.kmlns, "LineStyle");
				var colorNode = this.createKmlColorNode(style.strokeColor, style.strokeOpacity);
				lineNode.appendChild(colorNode);
				
				if (style.strokeWidth) {
					var width = this.createElementNS(this.kmlns, "width");
					width.appendChild(this.createTextNode(style.strokeWidth));
					lineNode.appendChild(width);
				}
				styleNode.appendChild(lineNode);
			}
			
			// PolyStyle (IE: style.fillColor = 'none')
			if (style.fillColor || style.fillOpacity) {
				var polyNode = this.createElementNS(this.kmlns, "PolyStyle");
				var colorNode = this.createKmlColorNode(style.fillColor, style.fillOpacity);
				polyNode.appendChild(colorNode);
				styleNode.appendChild(polyNode);
			}
			if (polyNode && style.strokeWidth == "0") {
				var outline = this.createElementNS(this.kmlns, "outline");
				outline.appendChild(this.createTextNode("0"));
				polyNode.appendChild(outline);
				styleNode.appendChild(polyNode);
			}
			
			// IconStyle
			if (style.externalGraphic && style.graphic !== false) {
				var iconstyleNode = this.createElementNS(this.kmlns, "IconStyle");
				var iconNode = this.createElementNS(this.kmlns, "Icon");
				
				var href = this.createElementNS(this.kmlns, "href");
				var urlObj = OpenLayers.Util.createUrlObject(
					style.externalGraphic.replace("?", "%3F"),
					{ignorePort80: true}
				);
				url = urlObj.protocol+'//'+urlObj.host+urlObj.port+urlObj.pathname;
				href.appendChild(this.createTextNode(url));
				iconNode.appendChild(href);
				//scaleは実画像のサイズとの比率 メモ描画では feature.style.graphicScale に格納している
				var scale = this.createElementNS(this.kmlns, "scale");
				scale.appendChild(this.createTextNode( style.graphicScale>0 ? style.graphicScale : 1 ));
				iconstyleNode.appendChild(scale);
				
				iconstyleNode.appendChild(iconNode);
				
				if (style.graphicXOffset || style.graphicYOffset) {
					var hotSpotNode = this.createElementNS(this.kmlns, "hotSpot");
					if (style.graphicXOffset) {
						hotSpotNode.setAttribute("x", -style.graphicXOffset/style.graphicWidth);
						hotSpotNode.setAttribute("xunits", "fraction");
					}
					if (style.graphicYOffset) {
						hotSpotNode.setAttribute("y", 1+style.graphicYOffset/(style.graphicHeight));
						hotSpotNode.setAttribute("yunits", "fraction");
					}
					iconstyleNode.appendChild(hotSpotNode);
				}
				
				/*
				//<span class="ja">ポイントのスケールはメモでは不要</span><span class="en">Memo is not required for point's scale</span>
				//<span class="ja">汎用的にするなら実画サイズの幅高さから計算する必要有り</span><span class="en">For general purpose, the calculation of width and height for the real size is required</span>
				var scaleNode = this.createElementNS(this.kmlns, "scale");
				
				// in KML 2.2, w and h <Icon> attributes are deprecated
				// this means that we can't modify the width/height ratio of the image
				
				var scale = style.graphicWidth || style.graphicHeight || style.pointRadius * 2;
				scaleNode.appendChild(this.createTextNode(scale/32));
				iconstyleNode.appendChild(scaleNode);
				*/
				// graphicOpacity, since opacity seems to be only supported using a color node in KML,
				// which will eventually change the color of the image, we choose not to support it
				
				// rotation (heading), not supported yet
				
				// graphicName, cannot be supported since nothing similar exists in KML
				
				styleNode.appendChild(iconstyleNode);
			}
			
			// <span class="ja">LabelStyle 色と大きさのみ</span><span class="en">LabelStyle   Only color and size</span>
			if (style.fontColor != undefined) {
				var colorNode = this.createKmlColorNode(style.fontColor, style.fontOpacity);
				var labelStyle = this.createElementNS(this.kmlns, "LabelStyle");
				labelStyle.appendChild(colorNode);
				styleNode.appendChild(labelStyle);
				try {
				if (style.fontSize) {
					var fontSize = parseFloat(style.fontSize);
					var fontScaleNode = this.createElementNS(this.kmlns, "scale");
					fontScaleNode.appendChild(this.createTextNode(fontSize/this.FONTSIZE));
					labelStyle.appendChild(fontScaleNode);
				}
				} catch (e) {}
			}
			return styleNode;
		}
	},	
	
	CLASS_NAME: "OpenLayers.Format.KMLStyle"
});