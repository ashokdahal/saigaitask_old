/**
 * 主題図のフィーチャポップアップ
 * @class SaigaiTask.Map.view.RefContentsPopup
 * @requires SaigaiTask/Map/view/ContentsPopup.js
 */
SaigaiTask.Map.view.RefContentsPopup = new OpenLayers.Class(SaigaiTask.Map.view.ContentsPopup, {

	stmap: null,

	debug: false,

	/**
	 * @type {OpenLayers.Format.GML}
	 */
	gmlFormat: null,

	// 引数
	center: null,
	bbox: null,

	/** 検索ステータス */
	requests: null,

	listPopup: null,

	initialize: function(stmap) {
		this.stmap = stmap;
		this.gmlFormat = new OpenLayers.Format.GML();
	},

	debugBbox: function(bbox) {
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;
		if(me.debug) {
			console.log("BBOX");
			var layer = new OpenLayers.Layer.Vector();
			var bboxGeom = new OpenLayers.Bounds(bbox[0], bbox[1], bbox[2], bbox[3]).toGeometry();
			bboxGeom = bboxGeom.transform(new OpenLayers.Projection("EPSG:4326"), olmap.getProjectionObject());
			layer.addFeatures([new OpenLayers.Feature.Vector(bboxGeom)])
			stmap.map.addLayer(layer);
			layer.redraw();
		}
	},

	uniqueID: function(){
		 var randam = Math.floor(Math.random()*1000)
		 var date = new Date();
		 var time = date.getTime();
		 return randam + time.toString();
	},

	/**
	 * 地図に表示されている主題図レイヤを順番に検索する.
	 */
	getReferenceFeatureInfo : function(center, bbox, div, fromContents, pinned)
	{
		var me = this;
		me.center = center;
		me.bbox = bbox;
		if(typeof pinned=="undefined" || pinned==null) pinned=false;
		try {
			var popupDiv = div;
			if (!div) {
				popupDiv = document.createElement("div");
				popupDiv.className = "popup_div";
				popupDiv.id = "ref-popup-"+me.uniqueID();
			}

			// 検索対象のレイヤ情報を取得する
			var requests = me.requests = [];
			var ecommap = me.stmap.ecommaps[0];
			var referenceLayerInfos = ecommap.getReferenceLayerInfos().concat(ecommap.getExternalMapLayerInfos());
			for (var idx in referenceLayerInfos) {
				var referenceLayerInfo = referenceLayerInfos[idx];
				// 検索可能かチェックし、検索対象に追加
				if(referenceLayerInfo.alwaysNotSearch==false && referenceLayerInfo.searchable) {
					requests.push({
						layerInfo: referenceLayerInfo,
						center: center,
						bbox: bbox,
						popupDiv: popupDiv,
						fromContents: fromContents,
						popupOption: {
							map: me.stmap,
							olmap: me.stmap.map,
							center: center,
							div: popupDiv,
							items: [],
							toolbarData: {
								buttons: []
							},
							title: referenceLayerInfo.name,
							pinned: pinned
						}
					});
				}
			}

			// リストポップアップを表示
			me.showReferenceListPopup(requests, pinned);

			//<span class="ja">WMS毎に検索</span><span class="en">Search on each WMS</span>
			for(var idx in requests) {
				me.getReferenceFeatureInfoScope(requests[idx]);
			}

		} catch (e) { console.error(e); }
	},

	showReferenceListPopup: function(requests, pinned) {

		var me = this;
		var stmap = me.stmap;
		me.listPopup = {};

		// Ex.data.Store にそのままデータ登録すると、rawが保存されない
		// そのため、Modelを定義し、Modelインスタンスのrawに値を保存後
		// ModelインスタンスをStoreに追加する手順とする。
		Ext.define('SaigaiTask.Map.view.RefContentsPopup.Result', {
			extend: 'Ext.data.Model',
			fields: [{
				name: lang.__('Layer name'), type: 'String'
			}]
		});
		var store = Ext.create("Ext.data.ArrayStore",{
			model: 'SaigaiTask.Map.view.RefContentsPopup.Result'
		});
		// データの初回追加時にポップアップを表示
		store.addListener('add', function(view, record, item, index, e, eOpts) {
			if(grid.store.getTotalCount()==0) {
				if(me.popup==null) {
					// すべてのポップアップを閉じる
					if(stmap.popupManager!=null) {
						stmap.popupManager.closeAll();
					}
					me.showExtPopup({
						title: lang.__("External map feature list"),
						map: me.stmap,
						olmap: me.stmap.map,
						center: requests[0].center,
						panelWidth: grid.width,
						pinned: pinned,
						items: [grid]
					});
				}
			}
		});

		var grid = me.listPopup.grid = Ext.create('Ext.grid.Panel',{
			store: store,
			stateful: true,
			collapsible: false,
			multiSelect: false,
			header: false,
			width: 250,
//			closable: true,
			frame: false,
			stateId: 'stateGrid',
			columns: [{
				text: lang.__("Layer name"),
				dataIndex: lang.__("Layer name"),
				flex: 1
			}],
			viewConfig: {
				stripeRows: true,
				enableTextSelection: true
			}
		});
		// グリッド内のクリックイベントを定義
		grid.addListener('itemclick', function(view, record, item, index, e, eOpts) {
			// すべてのポップアップを閉じる
			if(stmap.popupManager!=null) {
				stmap.popupManager.closeAll();
			}

			// このポップアップを閉じる
			if(stmap.popupManager!=null) {
				stmap.popupManager.close(me.popup);
			}

			// ポップアップのツールバーに戻るボタンを追加
			var request = record.raw.request;
			var button = Ext.create("Ext.Button", {
				text: lang.__("Return to list<!--2-->"),
				scale : 'small',
				handler: function() {
					var pinned = me.popup.pinned;
					if(stmap.popupManager!=null) {
						stmap.popupManager.close(me.popup);
						me.popup = null;
					}
					me.getReferenceFeatureInfo(me.center, me.bbox, null, null, pinned);
					return false;
				}
			});
			request.popupOption.toolbarData.buttons.push(button);

			// ピン留めフラグ
			request.popupOption.pinned = me.popup.pinned;

			// レスポンスの種類に応じてポップアップを表示
			if(request.type=="gml") {
				// フィーチャ単位のポップアップ
				me.showRefGmlFeaturePopup(request, record.raw.feature);
				// レイヤ単位のポップアップ
				//me.showRefGmlPopup(request);
			}
			else if(request.type=="html") {
				me.showRefHtmlPopup(request);
			}
			else {
				alert("RefContentsPopup unknown request type: "+request.type);
			}
		});
	},

	/**
	 * レイヤ情報単位で検索する
	 * はじめにGMLでリクエスト、結果なしの場合は続けて HTMLでリクエスト
	 */
	getReferenceFeatureInfoScope : function(request)
	{
		var me = this;
		var self = this;
		var stmap = self.stmap;
		var ecommap = stmap.ecommaps[0];
		var layerIds = "";
		var featureIds = "";
		var layerInfo = request.layerInfo;
		// 認証が必要な場合に備えて取得
		var metadataIds = "";
		if(!layerInfo==false) {
			var visibleLayerIds = [];
			var visibleFeatureIds = [];
			var visibleMetadataIds = [];
			for(var idx in layerInfo.children) {
				var child = layerInfo.children[idx];
				if(child.visibility) {
					visibleLayerIds.push(child.layerId);
					visibleFeatureIds.push(child.featuretypeId);
					visibleMetadataIds.push(child.metadataid);
				}
			}
			layerIds = visibleLayerIds.join(",");
			featureIds = visibleFeatureIds.join(",");
			metadataIds = visibleMetadataIds.join(",");
		}
		if (layerIds && layerIds.length > 0) {
			//Reverse LayerId
			var url = layerInfo.wmsFeatureURL!=null ? layerInfo.wmsFeatureURL: layerInfo.wmsURL;
			url += this.getWmsGetFeatureInfoURL(layerInfo.layerId, featureIds, request.bbox, 4326, stmap.clickBuffer, stmap.clickBuffer, 5);
			request.type = "gml";
			request.xhr = jQuery.ajax({
				url: map.api.url.wfsProxyURL + encodeURIComponent(url) + "&metadataid=" + metadataIds,
				type: "GET",
				async: true,
				cache: false,
				headers: {
					"X-CSRF-Token": SaigaiTask.csrfToken
				},
				dataType: "text",
				success: function(data) {
					console.log(data);
					if (typeof data == "error") {
						console.warn("error!",args);
					} else {
						try {
						if (data.length > 0) {
							//for Firefox 4096 bytes limit
							if (data.normalize) data.normalize();
							/* <span class="ja">INFO_FORMAT=application/vnd.ogc.gml の場合はGMLをテーブルに変換</span><span class="en">In case of INFO_FORMAT=application/vnd.ogc.gml, convert to GML table </span> */
							var features = request.response = [];
							try {
								features = request.response = self.gmlFormat.read(data);
							} catch(e) {console.error(e);}
							//<span class="ja">featuresが空ならHTMLで再取得</span><span class="en">If features are blank, get HTML again</span>
							if (features.length == 0 || !features[0].fid) {
								//<span class="ja">フォーマット変更</span><span class="en">Update format</span>
								url = url.replace(/&INFO_FORMAT=.+?&/, "&INFO_FORMAT=text/html&");
								console.log(url);
								//self.getWmsGetFeatureInfoURL(groupId, self.getVisibleReferenceFeatureId(groupId), bbox, 4326, self.clickBuffer, self.clickBuffer, 5, 'text/html');
								request.type = "html";
								request.xhr = jQuery.ajax({
									url: map.api.url.wfsProxyURL + encodeURIComponent(url) + "&metadataid=" + metadataIds,
									type: "GET",
									async: true,
									cache: false,
									dataType: "text",
									success: function(data) {
										if (data) {
											//<span class="ja">文字列置換 GeoServerの形式に対応</span><span class="en">Support format of string replacement GeoServer</span>
											try {
											//<span class="ja">fidが表の一行目にあるので削除</span><span class="en">Because fid appears on the first line, delete it</span>
											data = data.replace(/(<tr[^>]*?>)[^<]*<t[d|h]>[^<]*<\/t[d|h]>/g, "$1");
											//<span class="ja">レイヤ名変換</span><span class="en">Convert layer name</span>
											layerIds = layerIds.split(',');
											for (var i=layerIds.length-1; i>=0; i--) {
												var layerInfo = ecommap.layerInfoStore[layerIds[i]];
												console.log(layerIds[i]);
												console.log(layerInfo);
												if (layerInfo && layerInfo.featuretypeId)
													data = data.replace(">"+layerInfo.featuretypeId.split(":")[1]+"<", ">"+SaigaiTask.Map.util.CommonUtil.escapeXml(layerInfo.name)+"<");
											}
											} catch (e) { console.error(e); }
											request.response = data;
											// この検索リクエストしかないのなら直接HTMLポップアップを表示する
											if(me.requests.length==1) {
												self.showRefHtmlPopup(request);
											}
											else {
												if(data=="<html></html>") {
													// 結果が空なら表示させない。
												}
												else {
													var raw = {};
													var raw = {
														request: request
													};
													raw[lang.__("Layer name")] = request.layerInfo.name;
													var result = SaigaiTask.Map.view.RefContentsPopup.Result.create(raw);
													result.raw = raw;
													me.listPopup.grid.store.add(result);
												}
											}
										}
									}
								});
							} else {
								// この検索リクエストしかないのかつ、地物１件なら直接GMLポップアップを表示する
								if(me.requests.length==1 && features.length==1) {
									self.showRefGmlPopup(request);
								}
								else {
									for(var idx in features) {
										var feature = features[idx];
										// Modelインスタンスを作成後、rawを保存
										var raw = {
											request: request,
											feature: feature // ポップアップ対象としてこのインデックス位置のfeatureを指定
										};
										raw[lang.__("Layer name")] = request.layerInfo.name;
										// フィーチャのfid="c10.1" からレイヤIDを取得して、レイヤ名を取得する
										try{
											if(feature.fid!=null) {
												var fidElems = feature.fid.split(".");
												if(fidElems.length==2) {
													var featureLayerId = fidElems[0];
													var featureLayerName = null;
													for(var idx in layerInfo.children) {
														var child = layerInfo.children[idx];
														if(child.featuretypeId!=null) {
															// namespace が付いていたら外して比較する
															// gServer の場合、child.featuretypeId=hazardmap:レイヤIDとなるが、
															// featureLayerIdはhazardmap:が付かない
															var childFeatureTypeId = child.featuretypeId;
															if(featureLayerId.indexOf(':')==-1 && childFeatureTypeId.indexOf(':')!=-1) {
																var elems = child.featuretypeId.split(':');
																childFeatureTypeId = elems[elems.length-1];
															}
															if(childFeatureTypeId==featureLayerId) {
																raw[lang.__("Layer name")] += "/"+child.name;
																break;
															}
														}
													}
												}
											}
										} catch(e) {
											// do nothing
										}
										var result = SaigaiTask.Map.view.RefContentsPopup.Result.create(raw);
										result.raw = raw;
										me.listPopup.grid.store.add(result);
									}
								}
							}
						}
						} catch (e) { console.error(e); }
					}
				}
			});
		}
	},
	/** <span class="ja">WMSのgetFeatureInfoリクエスト用のURLを返す</span><span class="en">Return URL for WMS's getFeatureInfo request</span>
	 * @param format <span class="ja">FORMATパラメータ 指定がなければGML形式(INFO_FORMAT=application/vnd.ogc.gml)</span><span class="en">FORMAT parameter    If not specified, GML format (INFO_FORMAT=application/vnd.ogc.gml)</span>
	 * @param bbox <span class="ja">検索範囲</span><span class="en">Search range</span>
	 * @param epsg <span class="ja">bboxのEPSGの数値</span><span class="en">Numeric value of bbox's EPSG</span> */
	getWmsGetFeatureInfoURL : function(groupId, layers, bbox, epsg, x, y, count, format)
	{
		var me = this;
		var stmap = me.stmap;
		var olmap = stmap.map;

		// 20px 20px の画像の、左上原点の(X,Y)=(10,10) つまり 画像の中心位置にある地物をもとめる
		// bbox は画像と同じサイズだと、点と線が検索にかかりにくいため
		// bbox は buffer*3 の大きさにする。
		var lonLength = bbox[2]-bbox[0];
		var latLength = bbox[3]-bbox[1];
		var bufferedBbox = [bbox[0]-lonLength, bbox[1]-latLength, bbox[2]+lonLength, bbox[3]+latLength];

		me.debugBbox(bufferedBbox);

		// 時間パラメータの付与
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			time = iso8601Time;
		}
		else {
			time = null;
		}

		if (!format) format = "application/vnd.ogc.gml";
		var url = /*this.wfsProxyURL+"/"+groupId+"/redirect/wms_feature/?*/ "WIDTH="+x*2+"&HEIGHT="+y*2+"&LAYERS="+layers
		+"&STYLES=&SRS=epsg:"+epsg+"&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml&BBOX="
		+(bufferedBbox.join(","))+"&X="+x+"&Y="+y+"&INFO_FORMAT="+format+"&QUERY_LAYERS="+layers+"&FEATURE_COUNT="+count+"&buffer=2";

		if(!!time) url += "&TIME="+time;

		return url;
	},
	/** <span class="ja">主題図ポップアップ</span><span class="en">Thematic map popup</span> */
	showRefGmlPopup : function(request)
	{
		var me = this;
		var features = request.response;
		try {

		if (features.length == 0) {
			features = null;
//			if (request.fromContents) {
//				var div = document.createElement("div");
//				div.innerHTML = lang.__('No thematic map info');
//				var linkDiv = this.getContentsLinkDiv(center, bbox);
//				this.showPopupWindow(center, div, linkDiv?linkDiv:document.createElement("br"), {});
//			}
			return;
		}

		if(features.length==1) {
			me.showRefGmlFeaturePopup(request, features[0]);
		}
		else {
			me.showRefGmlTablePopup(request, features);
		}

		features = null;

		} catch (e) {console.error(e);}
	},

	showRefGmlFeaturePopup : function(request, feature) {
		var me = this;
		var layerInfo = request.layerInfo;

		var ecommapURL = me.stmap.ecommaps[0].ecommapURL;

		// featureTypeName
		var featureType = feature.gml.featureType;
		var featureTypeName = featureType;
		for(var idx in layerInfo.children) {
			var child = layerInfo.children[idx];
			if(featureType==child.featuretypeId) {
				featureTypeName = child.name;
				break;
			}
		}

		// タイトル
		var title = null;
		title = featureTypeName+" ["+layerInfo.name+"]";

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

		// 属性
		var attrs = [];
		for(var attrId in feature.attributes) {
			var attrValue = feature.attributes[attrId];
			var attr = {
				attrId: attrId,
				attrName: attrId,
				attrValue: attrValue
			}
			attrs.push(attr);
		}
		// 属性グリッドを表示
		var attrLayerInfo = null;
		// eコミからのGetFeatureInfoの場合
		if(feature.gml.featureNS=="http://map.ecom-plat.jp/map") {
			// レイヤの名称が同じ登録情報レイヤがあれば、属性グリッドはそのレイヤに従う
			var ecommap = me.stmap.ecommaps[0];
			for(var idx in ecommap.contentsLayerInfos) {
				var contentsLayerInfo = ecommap.contentsLayerInfos[idx];
				if(contentsLayerInfo.name==featureTypeName) {
					// 属性情報を初期化
					layerInfo.attrInfos = [];
					for(var attrIdx in contentsLayerInfo.attrInfos) {
						// 属性情報をコピーする
						var attrInfo = new SaigaiTask.Map.Layer.AttrInfo(contentsLayerInfo.attrInfos[attrIdx]);
						// 属性IDは属性名になっているため修正しておく
						attrInfo.attrId = attrInfo.name;
						layerInfo.attrInfos.push(attrInfo);
					}
					// 属性グリッドの引数に渡す
					attrLayerInfo = layerInfo;
				}
			}
		}
		var grid = me.createAttrGrid(attrs, attrLayerInfo);
		//request.popupOption.items.push(grid);
		// HBoxに配置する
		hboxContainerObj.add(grid);
		hbox.width += grid.width;

		// ファイル
		// eコミからのGetFeatureInfoの場合
		if(feature.gml.featureNS=="http://map.ecom-plat.jp/map") {
			if(hboxContainerObj.layout.type=="vbox") {
				// ファイルフォームパネル
				// 添付ファイルフォームを生成
				var contentsFileFormPanel = me.contentsFileFormPanel = new SaigaiTask.Map.view.ContentsFileFormPanel(me.stmap, null, {
					//mid: stmap.mapId,
					//layerId: layerId,
					//fid: fid,
					editable: false
				});
				
				// 画像データを登録
				for(var attrId in feature.attributes) {
					var attr = feature.attributes[attrId];
					//リンクと画像
					var match = attr.match(/^(https?:\/\/[^ ]+)$/i);
					if (match && match.length > 0) {
						var fileTitle = "";
						var fileURL = match[1];

						// サムネイル画像
						var url = fileURL;
						var ext = FalUtil.getFileExt(url);
						var thumbnail = url;
						// 画像以外のファイルの場合はアイコン表示
						if (! ext.match(/jpg|gif|png|jpeg|file/)) {
							thumbnail = ecommapURL+"/map/fileicons/"+ext+".png";
						}

						contentsFileFormPanel.store.add({
							title: fileTitle,
							url: fileURL,
							thumbnail: thumbnail
						});
					}
				}
				
				var imagesViewPanel = contentsFileFormPanel.formPanel;
				imagesViewPanel.setWidth(250);
				hboxContainerObj.add(imagesViewPanel);
			}
		}

		request.popupOption.items.push(hboxContainerObj);
		request.popupOption.panelWidth = hbox.width;

		// div 表示しない
		delete request.popupOption.div;

		me.showExtPopup(request.popupOption);
	},

	/**
	 * （複数地物対応）テーブル一覧ポップアップ
	 * あるレイヤで検索されたすべての地物をテーブル表示する際に使用する
	 */
	showRefGmlTablePopup : function(request, features) {
		var me = this;
			var headers = {};
			var featureDiv;
			var preTypeName = "";
			var table, tbody, tr, td;
			var resultDiv = document.createElement("div");
			resultDiv.className = 'popup_ref';
			///resultDiv.appendChild(document.createElement("br"));//<span class="ja">改行</span><span class="en">Break line</span>
			var noattr = true;

			//<span class="ja">ヘッダ取得</span><span class="en">Get header</span>
			for (var i=0; i<features.length; i++) {
				var feature = features[i];
				//<span class="ja">.以降削除</span><span class="en">Delete from the .</span>
				var typeName = feature.fid.replace(/\..+$/,"");
				var header;
				//<span class="ja">ヘッダ とりあえず項目が多いヘッダを利用 FIXME 属性IDでマージする</span><span class="en">Header   Tentively, use the header which contains many items   FIXME Merge at attribute ID</span>
				if (preTypeName != typeName) {
					header = [];
					preTypeName = typeName;
				}
				var attrLength = 0;
				for (var attrid in feature.attributes) attrLength++;
				if (header.length < attrLength) {
					var idx = 0;
					for (var attrid in feature.attributes) {
						if (attrid != "style") header[idx++] = attrid;  //<span class="ja">idがstyleの属性は非表示</span><span class="en">id hides the style attribute</span>
					}
					headers[typeName] = header;
				}
			}

			preTypeName = "";
			var layerName = null;
			for (var i=0; i<features.length; i++) {
				var typeName = features[i].fid.replace(/\..+$/,"");
				var h = headers[typeName];
				if (!h) h = [];
				if (preTypeName != typeName) {
					//<span class="ja">レイヤ名称</span><span class="en">Layer name</span>
					//<span class="ja">レイヤ名称変換</span><span class="en">Convert layer name</span>
					layerName = typeName;
					var groupLayerInfo = request.layerInfo;
					if(!groupLayerInfo==false) {
						for(var idx in groupLayerInfo.children) {
							var layerInfo = groupLayerInfo.children[idx];
							if (layerInfo && (layerInfo.featuretypeId.replace(/^.*?\:/, "") == typeName)) layerName = layerInfo.name;
						}
					}
					featureDiv = document.createElement("div")
					featureDiv.className = "featureTypeName";
					featureDiv.innerHTML = layerName+" " + lang.__("({0} items )", features.length);
					//console.log(groupId+":"+typeName);
					resultDiv.appendChild(featureDiv);
					preTypeName = typeName;
					table = document.createElement("table");
					table.className = "featureInfo";
					tbody = document.createElement("tbody");

					//<span class="ja">ヘッダ</span><span class="en">Header</span>
					tr = document.createElement("tr");
//					//<span class="ja">検索範囲追加アイコン列</span><span class="en">Icon array to insert search range</span>
//					if (this.callbacks['add_search_range']) {
//						tr.appendChild(document.createElement("th"));
//					}
					noattr = true;
					for (var j=0; j<h.length; j++) {
						td = document.createElement("th");
						td.innerHTML = h[j];
						td.style.minWidth = (Math.min(100,h[j].length*16))+"px";
						tr.appendChild(td);
						noattr = false;
					}
					if (!noattr) {
						tbody.appendChild(tr);
					}
				}
				tr = this._createRefPopupTr(layerName, features[i], h, noattr);
				tbody.appendChild(tr);
				table.appendChild(tbody);
				resultDiv.appendChild(table);
			}

//			var linkDiv;
//			if (fromContents) linkDiv = this.getContentsLinkDiv(center, bbox);
//			if (!linkDiv) linkDiv = document.createElement("br");

			var options = {minWidth:250, maxWidth: me.stmap.map.getSize().w*0.6, panIntoView:true, turned:true};
			var popupDiv = request.popupDiv;
			if (popupDiv) {
				//<span class="ja">追加</span><span class="en">Insert</span>
				popupDiv.appendChild(resultDiv);
				//this.showPopupWindow(center, popupDiv, linkDiv, options);
				//alert(popupDiv.innerHTML);
				if(document.getElementById(popupDiv.id)==null) {
					me.showExtPopup(request.popupOption);
				}
				else {
					var panelId = $("#"+popupDiv.id).parents(".x-panel")[0].id;
					var panel = Ext.getCmp(panelId);
					// 再描画
					panel.update($("<div>").append(popupDiv).html());
				}
//			} else {
//				this.showPopupWindow(center, resultDiv, linkDiv, options);
			}
	},

	/** <span class="ja">主題図ポップアップの行のTRを生成<br/> マーカー表示と検索範囲に追加のリンクとイベントを生成</span><span class="en">Create TR of line of thematic map popup <br/>Create event and links inserted to marker display and search range</span> */
	_createRefPopupTr : function(layerName, feature, header, noattr)
	{
		var tr = document.createElement("tr");
		var td;
//		//<span class="ja">検索範囲に追加アイコン</span><span class="en">Icon to insert search range</span>
//		var addSearchRangeCallback = this.callbacks['add_search_range'];
//		if (addSearchRangeCallback && feature.geometry) {
//			td = document.createElement("td");
//			var div = document.createElement("div");
//			div.className = "addSearchRangeIcon";
//			div.title = lang.__('Add Search Scope');
//			div.onclick = function(){
//				for (var i=0; i<addSearchRangeCallback.length; i++)
//					addSearchRangeCallback[i](null, null, feature.geometry.toString(), layerName);
//			};
//			td.appendChild(div);
//			tr.appendChild(td);
//		}

		for (var j=0; j<header.length; j++) {
			td = document.createElement("td");
			var attr = feature.attributes[header[j]];
			if (attr) {
				//リンクと画像
				var match = attr.match(/^(https?:\/\/[^ ]+)$/i);
				if (match && match.length > 0) {
					if (match[1].match(/\.(png|jpg|jpeg|gif)$/i)) {
						td.innerHTML = '<a href="'+match[1]+'" target="?blank"><img src="'+match[1]+'" class="popup_attr_image"/></a>';
					} else {
						td.innerHTML = this.replaceHref(SaigaiTask.Map.util.CommonUtil.escapeXml(attr)).replace(/\n+/gm,"<br/>");
					}
				} else {
					td.innerHTML = attr;
				}

				td.style.minWidth = (Math.min(100,attr.length*8))+"px";
			}
			tr.appendChild(td);
		}
		if (noattr) {
			td = document.createElement("td");
			td.innerHTML = lang.__('No Attribute');
			tr.appendChild(td);
		}
//		if (feature.geometry) {
//			var self = this;
//			tr.onclick = function(){
//				try {
//					//選択済み
//					var selected = this.className == "selected";
//					self.clearPopupMarkers();
//					//行の背景色を選択色にする
//					var tbody = this.parentNode;
//					for (var i=0; i<tbody.children.length; i++) {
//						tbody.children[i].className = "";
//					}
//					//選択済みでなkれば選択状態にする
//					if (!selected) {
//						var lonlats = [];
//						var points = feature.geometry.getVertices();
//						var step = Math.round(Math.max(1, points.length/30));//<span class="ja">点数制限</span><span class="en">Limit number of points</span>
//						for (var i=points.length-1; i>=0; i-=step) {
//							lonlats.push(new OpenLayers.LonLat(points[i].x, points[i].y));
//						}
//						self.showPopupMarkers(lonlats);
//						this.className = "selected";
//					}
//				} catch (e) { console.error(e); }
//			};
//		}
		return tr;
	},
	/** <span class="ja">HTML形式のデータをそのままポップアップ表示 </span><span class="en">Display popup with HTML data</span>*/
	showRefHtmlPopup : function(request)
	{
		var me = this;
		try {
		var html = request.response;
		var popupOption = request.popupOption;

		if (!html) return;

		if ($.trim(html.substring(html.indexOf('<body>')+6, html.indexOf('</body>'))).length == 0) return;

//		var fromContents = request.fromContents;
//		var linkDiv;
//		if (fromContents) linkDiv = this.getContentsLinkDiv(center, bbox);
//		if (!linkDiv) linkDiv = document.createElement("br");

		//HTML
		var div = document.createElement('div');
		div.className = "popup_ref";
		//<span class="ja">aタグはtargetとhrefのみにする</span><span class="en">Set only 'target' and 'href' for 'A' tag</span>
		if (html) {
			div.innerHTML = html.replace(/<\s*a\s+/ig, '<a target="_blank" ').replace(/onclick\s*=\s*"[^"]*"/ig,'');
		}
		popupOption.div.appendChild(div);

		//<span class="ja">ポップアップ表示</span><span class="en">Display popup</span>
		//var popup = this.showPopupWindow(center, popupDiv, linkDiv, {minWidth:250, maxWidth:this.map.getSize().w*0.6, panIntoView:true, turned:true});
		me.showExtPopup(popupOption);
		//<span class="ja">ポップアップにフィーチャの情報追加</span><span class="en">Insert feature information to popup</span>
		//popup.featureInfo = {typeName:layerId, fid:featureId};
		//popup.hide = function() { OpenLayers.Element.hide(me.div); };

		} catch (e) { console.error(e); }
	}
});
