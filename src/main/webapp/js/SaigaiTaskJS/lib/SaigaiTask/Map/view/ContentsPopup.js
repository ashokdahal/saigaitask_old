/**
 * 1件分の登録情報をポップアップ表示するクラスです.
 * @class SaigaiTask.Map.view.ContentsPopup
 * @requires SaigaiTask/Map/view/Popup.js
 */
SaigaiTask.Map.view.ContentsPopup = new OpenLayers.Class(SaigaiTask.Map.view.Popup, {

	stmap: null,

	/**
	 * 登録情報のレイヤ情報
	 * @type {SaigaiTask.Map.Layer.LayerInfo}
	 */
	layerInfo: null,

	initialize: function(stmap) {
		this.stmap = stmap;
	},

	/**
	 * 1件分の登録情報をポップアップします.
	 * 属性はグリッド形式で表示します.
	 * @param {Object}  options
	 * @param {Boolean} options.fromList リストへ戻るフラグ
	 * @param {Boolean} options.pinned ピン留めフラグ
	 */
	show: function(layerId, fid, result, center, bbox, options) {
		var me = this;
		var stmap = me.stmap;
		var items = new Array(); // Ext widget

		//地図初期表示時かどうか
		var pinned = false;
		if(typeof options != 'undefined'){
			pinned = options.pinned;
		}

		// ポップアップの位置が決まっていない場合は、登録情報から取得する
		if((typeof center=='undefined' || center==null) && result.geom[0] != null){
			var wkt = result.geom[0];
			try{
				var wktFormat = new OpenLayers.Format.WKT();
				var feature = wktFormat.read(wkt);
				var centroid = feature.geometry.getCentroid();
				center = new OpenLayers.LonLat(centroid.x, centroid.y);
				// センターに設定
				stmap.setCenter(center.clone());
			}catch(e) {
				alert(lang.__("Unable to get position info."));
				console.error(lang.__("Failed to get position info :")+layerId+"."+fid);
				console.error(e);
				return;
			}
		}

		// タイトル
		var layerInfo = me.layerInfo = stmap.getLayerInfo(layerId);
		var layer = layerInfo.getLayer();
		var title = null;
		if(layerInfo!=null) title = layerInfo.name;

		// 添付ファイルのレイアウトを指定する
		// 縦型: vbox   横型: hbox
		var layout = "vbox";

		// 画像と属性を横並びにするHBox
		var hbox = {
			xtype: 'container',
			layout: layout,
			width: 0,
			items: []
		};
		var hboxContainerObj = Ext.create('Ext.container.Container', hbox);

		// ファイル
		var files = result.files;
		if(files && 0<files.length) {
			if(hboxContainerObj.layout.type=="hbox") {
				// 画像のコンテナを生成
				var imgContainerWidth = 256; // ポップアップウィンドウの画像の幅はいくつにする？
				var extImgContainer = {
					xtype: 'container',
					width: imgContainerWidth,
					style: {
						'text-align': 'center',
						cursor: 'pointer'
					}
				};
				var extImgContainerObj = Ext.create('Ext.container.Container', extImgContainer);

				// HBoxに配置する
				hboxContainerObj.add(extImgContainerObj);
				hbox.width += extImgContainer.width;

				// 最大幅は画像コンテナの幅
				var maxw = imgContainerWidth;
				// 最大高さはhboxコンテナの高さ
				var maxh = maxw;

				var filesIdx = 0;
				while(filesIdx<files.length) {
					// 画像URLのサーバ名を動的に取得する
					var server = stmap.ecommaps[0].ecommapURL+'/..';
					// 画像URL
					var fileURL = files[filesIdx];
					if(fileURL.indexOf('http')==-1) fileURL = server + fileURL;
					var fileTitle = files[filesIdx+1];

					var extImgObj = stmap.createImg(fileURL, fileTitle, maxw, maxh);
					extImgContainerObj.add(extImgObj);

					filesIdx += 2;
				}
			}
		}

		// 属性
		var attrs = result.attrs;
		// レイヤ情報から編集フラグを取得する
		var editable = false;
		if(typeof layerInfo.editable!="undefined") {
			editable = layerInfo.editable;
		}
		var editablePopup = false;
		if(editable && editablePopup) {
			// ポップアップ内編集モードを表示
			// 編集フォームを追加
			var contentsFormPanel = new SaigaiTask.Map.view.ContentsFormPanel(stmap, layerId, null, fid, content);
			var width = 250;
			contentsFormPanel.formPanel.setWidth(width);
			hboxContainerObj.add(contentsFormPanel.formPanel);
			hbox.width += width;
		}
		else {
			// 属性グリッドを表示
			var grid = me.createAttrGrid(attrs, layerInfo);
			// HBoxに配置する
			hboxContainerObj.add(grid);
			hbox.width += grid.width;
		}

		// ファイル
		var files = result.files;
		if(files && 0<files.length) {
			if(hboxContainerObj.layout.type=="vbox") {
				// ファイルフォームパネル
				// 添付ファイルフォームを生成
				var contentsFileFormPanel = me.contentsFileFormPanel = new SaigaiTask.Map.view.ContentsFileFormPanel(stmap, result, {
					mid: stmap.mapId,
					layerId: layerId,
					fid: fid,
					editable: false
				});
				var imagesViewPanel = contentsFileFormPanel.formPanel;
				imagesViewPanel.setWidth(250);
				hboxContainerObj.add(imagesViewPanel);
			}
		}

		items.push(hboxContainerObj);


		// 一覧へ戻るボタン
		if(typeof options!='undefined' && options['fromList']){
			var button = Ext.create("Ext.Button", {
				text: lang.__("Return to list<!--2-->"),
				scale : 'small',
				handler: function() {
					if(stmap.popupManager!=null) {
						stmap.popupManager.close(me.popup);
					}
					stmap.clickSearch(center, bbox, me.popup.pinned);
					return false;
				}
			});
			items.push(button);
		}

		var map = stmap.map;
		if(stmap.popupManager!=null) {
			stmap.popupManager.closeAll();
		}

		var externalData = result.external;
		var toolbarData = {
			buttons: []
		};
		if(externalData){
			toolbarData = externalData.toolbar;
		}
		else {
			// 属性情報の設定からツールバーボタンを生成する
			for(var idx=0; idx<attrs.length; idx++) {
				var attr = attrs[idx];
				var attrId = attr.attrId;
				var value = attr.attrValue;
				var attrInfo = layerInfo.getAttrInfo(attrId);
				if(attrInfo==null || attrInfo.buttonData==null) continue;
				var buttonData = {
					url: value
				};
				SaigaiTask.Map.extend(buttonData, attrInfo.buttonData);
				toolbarData.buttons.push(buttonData);
			}
		}

		// 編集可のレイヤの場合
		if(editable) {
			if(typeof toolbarData.buttons=="undefined") {
				toolbarData.buttons = [];
			}
			var deleteButton = {
				text: lang.__("Delete"),
				icon: stmap.icon.getURL("deleteIconURL"),
				handler: function() {
					// 時間指定中なら削除禁止とする
					if(!!SaigaiTask.PageURL.getTime()) {
						alert(lang.__("Can't delete during time set."));
						return;
					}
					if(confirm(lang.__("Are you sure to delete?"))) {
						stmap.api.deleteContent(layerId, fid, function() {
							if(stmap.popupManager!=null) {
								stmap.popupManager.close(me.popup);
							}
							if(layer!=null) {
								layer.refreshParams({
									nocache: true
								});
							}
							stmap.events.triggerEvent(stmap.EventType.successcontentsdelete, {
								contentsPopup: me,
								layerInfo: layerInfo
							});
						});
					}
				}
			};
			if(editablePopup) {
				// 保存ボタンの追加
				toolbarData.buttons.push({
					text: lang.__("Save"),
					icon: stmap.icon.getURL("editIconURL"),
					handler: function() {
						contentsFormPanel.submit();
					}
				});
				// 削除ボタンの追加
				toolbarData.buttons.push(deleteButton);
			}
			else {
				// 編集ボタンの追加
				toolbarData.buttons.push({
					text: lang.__("Edit"),
					icon: stmap.icon.getURL("editIconURL"),
					handler: function() {
						if(stmap.popupManager!=null) {
							// 時間指定中なら編集禁止とする
							if(!!SaigaiTask.PageURL.getTime()) {
								alert(lang.__("Can't edit during time set."));
								return;
							}

							var wkt = result.geom[0];
							var wktFormat = new OpenLayers.Format.WKT();
							var feature = wktFormat.read(wkt);
							stmap.popupManager.close(me.popup);
							new SaigaiTask.Map.view.ContentsFormWindow({
								stmap: stmap,
								layerInfo: layerInfo,
								fid: fid,
								lonlat: center,
								feature: feature,
								content: result
							});
						}
					}
				});
				// 削除ボタンの追加
				toolbarData.buttons.push(deleteButton);
			}
		}

		// ExtPopupのオプション
		var option = {
			map: stmap,
			olmap: map,
			center: center,
			items: items,
			toolbarData: toolbarData,
			panelWidth: hbox.width,
			layerId: layerId,
			featureId: fid,
			title: title,
			pinned: pinned,
			metadataId: layerInfo.metadataid,
			searchButtonInfo: {
				url: stmap.api.url.contentsSearchURL,
				mid: stmap.mapId,
				contentsLayers: stmap.ecommaps[0].contentsLayers,
				featureInfo: result
			}
		};

		// 登録情報ポップアップ表示前イベント
		stmap.events.triggerEvent(stmap.EventType.beforeshowcontentpopup, {
			options: option
		});

		me.showExtPopup(option);

		// URL再現用のポップアップパラメータの付与
		me.popup.popupParam = layerId+"."+fid;
	},

	createDeleteButton: function() {

	},

	/**
	 * 登録情報の属性グリッドを生成します.
	 * @param attrs 属性情報配列
	 * @param layerInfo レイヤ情報
	 */
	createAttrGrid: function(attrs, layerInfo) {
		var grid = null;
		if(attrs){
			var data = (function(attrs){
				var data = new Array();
				for(var idx=0; idx<attrs.length; idx++) {
					var attr = attrs[idx];
					var attrId = attr.attrId;
					// 属性情報を取得
					var attrInfo = !!layerInfo ? layerInfo.getAttrInfo(attrId) : null;
					var name = attr.attrName;
					// レイヤ情報があれば、そちらの属性名称を優先して表示する
					if(attrInfo!=null) {
						name = attrInfo.name;
					}
					var value = attr.attrValue;
					// JSONArray 文字列かどうか
					var jsonDataExps = null;
					try {jsonDataExps=jQuery.parseJSON(attrInfo.dataExp)} catch(e){/* do nothing */}
					if(jsonDataExps!=null) {
						for(var jsonDataExpsIdx in jsonDataExps) {
							var jsonDataExp = jsonDataExps[jsonDataExpsIdx];
							if(typeof jsonDataExp[attr.attrValue]!="undefined") {
								value = jsonDataExp[attr.attrValue];
								break;
							}
						}
					}
					// レイヤ情報で非表示属性になっていれば表示しない
					if(!!layerInfo && !layerInfo.isVisibleAttr(attrId)) continue;
					data.push({
						attrId: attrId,
						name: name,
						value: value
					});
				}
				return data;
			})(attrs);
			// もし、表示属性がない場合は追加する
			if(!!layerInfo) {
				for(var idx in layerInfo.attrInfos) {
					var attrInfo = layerInfo.attrInfos[idx];
					var attrId = attrInfo.attrId;
					if(layerInfo.isVisibleAttr(attrId)) {
						var exist = false;
						for(var idx2 in data) {
							var attr = data[idx2];
							if(attr.attrId==attrId) {
								exist = true;
								break;
							}
						}
						if(!exist) {
							data.push({
								attrId: attrId,
								name: attrInfo.name,
								value: null //"(属性がありません)"
							});
						}
					}
				}
			}
			// disporder順でソート
			if(!!layerInfo) {
				data.sort(function(attr1, attr2) {
					return layerInfo.evalAttrSort(attr1.attrId, attr2.attrId);
				});
			}
			Ext.define('ContentsAttrModel', {
				extend: 'Ext.data.Model',
				fields: [{
					name: 'attrId',
					type: 'text'
				}, {
					name: 'name',
					type: 'text'
				}, {
					name: 'value',
					type:'text'
				}]
			});
			var store = Ext.create("Ext.data.Store", {
				model: 'ContentsAttrModel',
				data: data
			});
			grid = Ext.create("Ext.grid.Panel", {
				store: store,
				width: 250,
				cls: "attrgridpanel",
				columns: [{
					text: lang.__('Attribute ID'),
					dataIndex: 'attrId',
					flex: 1,
					sortable: false,
					hidden: true
				}, {
					text: lang.__('Attribute'),
					dataIndex: 'name',
					flex: 1,
					sortable: false
				}, {
					text: lang.__('Value'),
					dataIndex: 'value',
					flex: 2,
					sortable: false
				}],
				viewConfig: {
					getRowClass: function(record, rowIndex, rowParams, store) {
						var classes = [];
						var attrId = record.data.attrId;
						var attrInfo = !!layerInfo ? layerInfo.getAttrInfo(attrId) : null;
						if(attrInfo!=null && attrInfo.highlight==true) {
							return "highlight";
						}
						return null;
					}
				}
			});
		}
		return grid;
	}
});
