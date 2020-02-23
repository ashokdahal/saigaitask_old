/**
 * 1件分のKML情報をポップアップ表示するクラスです.
 * @class SaigaiTask.Map.view.KMLPopup
 * @requires SaigaiTask/Map/view/Popup.js
 * @see eMapBase.openKMLPopup
 */
SaigaiTask.Map.view.KMLPopup = new OpenLayers.Class(SaigaiTask.Map.view.Popup, {

	stmap: null,

	initialize: function(stmap) {
		this.stmap = stmap;
	},

	show: function(feature, layerInfo) {
		var me = this;
		var stmap = me.stmap;
		if(stmap.popupManager!=null) {
			stmap.popupManager.closeAll();
		}

		try {
			var lonlat = feature.geometry.getBounds().getCenterLonLat().clone();
			//<span class="ja">Featureは地図座標なので一旦緯度経度に戻す</span>
			//<span class="en">Because feature is map coordinate, return to longtitude-latitude once </span>
			if (me.stmap.epsg!=4326) lonlat.transform(me.stmap.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));

			var popupDiv = document.createElement('div');
			var div, span;

			//Attrs
			div = document.createElement('div');
			div.className = "popup_attr";
			//<span class="ja">aタグはtargetとhrefのみにする</span><span class="en">Set only 'target' and 'href' for 'A' tag</span>
			if (feature.attributes.description) {
				div.innerHTML = (feature.attributes.name ? "<h3>"+SaigaiTask.Map.util.CommonUtil.escapeXml(feature.attributes.name)+"</h3>" : "")
					+feature.attributes.description.replace(/<\s*a\s+/ig, '<a target="_blank" ').replace(/onclick\s*=\s*"[^"]*"/ig,'');
				//attrDiv.style.width = w+"px";
			} else if (feature.attributes.name) {
				div.innerHTML = "<h3>"+SaigaiTask.Map.util.CommonUtil.escapeXml(feature.attributes.name)+"</h3>";
			}

			try {
				var tbody = null;
				var td;
				for (var key in feature.attributes) {
					if (key != "name" && key != "description" && key != "styleUrl") {
						if (feature.attributes[key].value) {
							if (!tbody) tbody = document.createElement('tbody');
							var attrTr = document.createElement('tr');
							td = document.createElement('td');
							attrTr.appendChild(td);
							td.innerHTML = key;
							td = document.createElement('td');
							td.innerHTML = feature.attributes[key].value;
							attrTr.appendChild(td);
							tbody.appendChild(attrTr);
						}
					}
				}
				if (tbody) {
					table = document.createElement('table');
					table.cellSpacing = 0;
					table.cellPadding = 0;
					table.appendChild(tbody);
					div.appendChild(table);
				}
			} catch (e) {}

			popupDiv.appendChild(div);

			// DIV用のパネル（ヘッダーなし）
			var panel = Ext.create('Ext.panel.Panel', {
				width: 200,
				contentEl: popupDiv
			});

			// ExtPopupのオプション
			var option = {
				map: me.stmap,
				olmap: me.stmap.map,
				center: lonlat,
				items: [panel],
				title: layerInfo.name,
				pinned: false
			};
			//var extPopup = new SaigaiTask.Map.view.Popup();
			//var popup = extPopup.showExtPopup(option);
			me.showExtPopup(option);
		} catch (e) { console.error(e); }
	},

	CLASS_NAME: "SaigaiTask.Map.view.KMLPopup"
});