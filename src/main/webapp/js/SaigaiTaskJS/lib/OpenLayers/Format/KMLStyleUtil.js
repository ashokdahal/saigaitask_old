/* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
OpenLayers.Format.KMLStyleUtil = {
	parseTextMemo: function(kmlFormat, node, style) {
		var self = this;
		
		// labelがあるかチェック
		var label = null;
		var list = node.parentNode.children;
		if(typeof list=="undefined") list = node.parentNode.childNodes;
		for(var idx in list) {
			if(list[idx].nodeName=="name") {
				//label = list[idx].innerHTML;
				label = list[idx].textContent;
				break;
			}
		}

		// ラベルがあればメモ描画モード
		if(label!=null) {
			style.label=label;

			// @see eMapBase.js #addMemoKMLFeatures : function(data, created)
			
			// メモ描画フラグ
			style.memo = true; //メモFeatureフラグ設定
			style.cursor = "pointer";

			style.fontWeight = "bold";
			style.labelAlign = "lt";

			//<span class="ja">メモ描画のテキストの場合</span><span class="en">In case of text of memo drawing</span>
			if (style.label) {
				var fontSize = parseInt(style.fontSize);
				if (!fontSize) try { style.fontSize.replace(/px$/,""); } catch (e) {};
				if (!fontSize) kmlFormat.FONTSIZE;
				var size = self.getLabelSize(style.label.replace(/\\n/g, "<br/>"), fontSize, style.fontWeight);
				style.graphicWidth = size.w;
				style.graphicHeight = size.h;
			}
		}
	},
	/**
	 * @see eMapBase.js #getLabelSize
	 */
	getLabelSize : function(label, fontSize, fontWeight)
	{
		var size = {w:100, h:fontSize};
		try { //<span class="ja">幅高さ取得</span><span class="en">Get width and height</span>
			var span = document.createElement("span");
			span.style.position = "absolute";
			span.style.lineHeight = "105%";
			span.style.padding = "0.15em";
			span.style.margin = 0;
			span.style.fontSize = fontSize+"px";
			span.style.fontWeight = fontWeight;
			span.innerHTML = label;

			// 官民修正：document.bodyだとCSSが違うため、Map表示領域でサイズを測るようにする
			var dest = document.body;
			var elems = document.getElementsByClassName("olMap");
			if(0<elems.length) dest = elems[0];

			dest.appendChild(span);
			size.w = span.offsetWidth;
			size.h = span.offsetHeight;
			dest.removeChild(span);

		} catch (e) {}
		return size;
	}
};

