/* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
////////////////////////////////////////////////////////////////
//	Rendrerer Polygon Label
////////////////////////////////////////////////////////////////
OpenLayers.Renderer.prototype.drawFeature = function(feature, style)
{
	try {
	if (style == null) {
		style = feature.style;
		if (style == null) style = {};
	}
	if (feature.geometry) {
		var res = this.getResolution();
		//メモ描画読み込み時にテキストはstyle.memoが設定される
		if (style.memo && style.label) {
			//<span class="ja">メモ描画の場合のみ先に背景描画</span><span class="en">Only for memo drawing, draw the background before the memo</span>
			
			var point;
			var rate = 1;
			//descriptionをoptionにオブジェクトとして格納
			//if (!feature.attributes.option) {
				try {
					feature.attributes.option = dojo.fromJson(feature.attributes.description);
				} catch (e) {
					feature.attributes.option = {};
				}
			//}
			var option = feature.attributes.option;
			//表示解像度に合せて枠と文字サイズ変更
			if (option.reso) {
				rate = option.reso/res;
			}
			var offsetX = 0;
			var offsetY = 0;
			//TODO アンカーの指定に合せてオフセット
			if (option.anchor) {
				
			}
			var rendered = false;
			var padding = (parseInt(feature.style.strokeWidth)/2+2)*res*rate;
			point = feature.geometry.getCentroid();
			if (style.fillColor) {
				//<span class="ja">枠のbgRectは削除用にfeatureのメンバに格納しておく</span><span class="en">Store feature's member for deletion</span>
				var x = point.x;
				var y = point.y;
				var w = style.graphicWidth*res*rate +padding*2;
				var h = style.graphicHeight*res*rate;
				if (feature.bgRect) {
					var points = feature.bgRect.components[0].components;
					points[0].x = x; points[0].y = y;
					points[1].x = x+w; points[1].y = y;
					points[2].x = x+w; points[2].y = y-h;
					points[3].x = x; points[3].y = y-h;
					points[4].x = x; points[4].y = y;
				}
				else {
					feature.bgRect = new OpenLayers.Geometry.Polygon([new OpenLayers.Geometry.LinearRing(
						[new OpenLayers.Geometry.Point(x, y),
						new OpenLayers.Geometry.Point(x+w, y),
						new OpenLayers.Geometry.Point(x+w, y-h),
						new OpenLayers.Geometry.Point(x, y-h),
						new OpenLayers.Geometry.Point(x, y)])]);
				}
				rendered = this.drawGeometry(feature.bgRect, style, feature.id);
			} else {
				//<span class="ja">背景削除</span><span class="en">Delete the background</span>
				if (feature.bgRect) {
					this.eraseGeometry(feature.bgRect);
					feature.bgRect = null;
				}
			}
			
			if (rendered !== false) {
				//<span class="ja">テキスト改行対応</span><span class="en">Support text breakline</span>
				//<span class="ja">描画時に行毎の文字列を設定するので待避</span><span class="en">Backup because string is set for each line while drawing</span>
				var labelBak = style.label;
				//<span class="ja">"\n"の文字は強制改行</span><span class="en">Use "\n" as breakline enforcement</span>
				var lines = style.label.split("\\n");
				
				var orgFontSize = style.fontSize;
				style.fontSize = (parseFloat(style.fontSize) || 14) * rate; //pxがついている場合があるのでparseFloatで除去
				style.label = lines[0];
				//文字位置調整 IE対応 
				// 官民修正：Edgeの場合に文字が上にずれるのでIE用の文字調整が効くようにした
				if (/Edge/.test(navigator.userAgent) || (dojo.isIE >= 9 && document.documentMode >= 9)) point.move(0, -style.fontSize*res*1.08);
				else if (dojo.isChrome >= 50) point.move(0, -style.fontSize*res*0.3);
				
				point.move(padding, 0);
				this.drawText(feature.id, style, point);
				//<span class="ja">２行目以降は独自ID</span><span class="en">From the 2nd line, ID is independent</span>
				for (var i=1; i<lines.length; i++) {
					style.label = lines[i];
					point.move(0, -style.fontSize*res * 1.05 ); //105%
					this.drawText(feature.id+"_"+i, style, point);
				}
				//<span class="ja">待避した文字列に戻す</span><span class="en">Restore the backup string</span>
				style.label = labelBak;
				//フォントサイズ戻す
				style.fontSize = orgFontSize;
			} else {
				this.removeText(feature.id);
				//<span class="ja">背景削除</span><span class="en">Delete background</span>
				if (feature.bgRect) {
					this.eraseGeometry(feature.bgRect);
					feature.bgRect = null;
				}
				//<span class="ja">テキスト改行対応</span><span class="en">Support text breakline</span>
				if (feature.style && feature.style.label) {
					var label = feature.style.label;
					var lineCount = 1;
					var start = -1;
					while ((start = label.indexOf("\\n", start+1)) != -1) { lineCount++; }
					for (var j=1; j<lineCount; j++) { this.removeText(feature.id+"_"+j); }
				}
			}
			return rendered;
			
		} else {
			//文字以外の出力
			var bounds = feature.geometry.getBounds();
			if(bounds) {
				var worldBounds;
				if (this.map.baseLayer && this.map.baseLayer.wrapDateLine) {
					worldBounds = this.map.getMaxExtent();
				}
				//画像の場合は幅高さ分広げる
				if (style.graphicWidth) {
					bounds.left -= style.graphicWidth*res/2;
					bounds.right += style.graphicWidth*res/2;
				}
				if (style.graphicHeight) {
					bounds.bottom -= style.graphicHeight*res/2;
					bounds.top += style.graphicHeight*res/2;
				}
				
				if (!bounds.intersectsBounds(this.extent, {worldBounds: worldBounds})) {
					style = {display: "none"};
				} else {
					this.calculateFeatureDx(bounds, worldBounds);
				}
				var rendered = this.drawGeometry(feature.geometry, style, feature.id);
				if(style.display != "none" && style.label && rendered !== false) {
					var location = feature.geometry.getCentroid(); 
					if(style.labelXOffset || style.labelYOffset) {
						var xOffset = isNaN(style.labelXOffset) ? 0 : style.labelXOffset;
						var yOffset = isNaN(style.labelYOffset) ? 0 : style.labelYOffset;
						var res = this.getResolution();
						location.move(xOffset*res, yOffset*res);
					}
					this.drawText(feature.id, style, location);
				} else {
					this.removeText(feature.id);
				}
				return rendered;
			}
		}
	}
	
	} catch (e) { console.error(e); }
};
OpenLayers.Renderer.SVG.prototype.drawFeature = OpenLayers.Renderer.prototype.drawFeature;
OpenLayers.Renderer.VML.prototype.drawFeature = OpenLayers.Renderer.prototype.drawFeature;

OpenLayers.Renderer.prototype.eraseFeatures = function(features)
{
	if(!(features instanceof Array)) {
		features = [features];
	}
	var len=features.length;
	for(var i=0; i<len; ++i) {
		var feature = features[i];
		this.eraseGeometry(feature.geometry);
		this.removeText(feature.id);
		//Modified Start
		//<span class="ja">背景削除</span><span class="en">Delete background</span>
		if (feature.bgRect) {
			this.eraseGeometry(feature.bgRect);
			feature.bgRect = null;
		}
		//<span class="ja">テキスト改行対応</span><span class="en">Support text breakline</span>
		if (feature.style && feature.style.label) {
			var label = feature.style.label;
			var lineCount = 1;
			var start = -1;
			while ((start = label.indexOf("\\n", start+1)) != -1) { lineCount++; }
			for (var j=1; j<lineCount; j++) { this.removeText(feature.id+"_"+j); }
		}
		//Modified End
	}
};
OpenLayers.Renderer.SVG.prototype.eraseFeatures = OpenLayers.Renderer.prototype.eraseFeatures;
OpenLayers.Renderer.VML.prototype.eraseFeatures = OpenLayers.Renderer.prototype.eraseFeatures;
