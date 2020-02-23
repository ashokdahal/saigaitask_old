
/**
 * @class SaigaiTask.Map.view.LengendPanel
 * 凡例ツリーパネルのビューです.
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.LegendPanel = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.LegendPanel.prototype = {

	/**
	 * SaigaiTask.Mapオブジェクト
	 * @type {SaigaiTask.Map}
	 */
	map: null,

	/**
	 * 凡例ツリーパネル
	 * @type {Ext.tree.Panel}
	 */
	tree: null,

	/**
	 * フッターツールバー
	 * @type {Ext.toolbar.Toolbar}
	 */
	fbar: null,

	/**
	 * 地図追加ボタン
	 */
	addMapBtn: null,

	initialize: function(map) {
		var me = this;
		me.map = map;

		var store = Ext.create('Ext.data.TreeStore', {
			expanded: true
		});

		var tree = Ext.create('Ext.tree.Panel', {
			store: store,
			rootVisible: false,
			title: lang.__('Legend'),
			// maxWidth: 300, maxHeight: 200,
			width: 200, // height: 200,
			border: true,
			useArrows: true,
			scroll: false,
			draggable: true,
			collapsible: true,
			resizable: true,
			resizeHandles: "e",
			collapsed: me.map.options.collapsed,
			viewConfig: {
				style: {
					overflow: 'auto'
				}
			},
			listeners: {
				afterrender: function(panel, eOps) {
					obj = $(panel.getEl().dom);

					// Z-indexを設定する
					panel.getEl().setStyle('z-index','1010');

					// 各イベントのバブリングを抑止する
					obj.click(function(){ return false; });
					obj.dblclick(function(){ return false; });
					obj.children().get(2).style.overflow = 'auto';
				},
				viewready: function(panel, eOps) {
					console.log("viewready");
					// ツールバーアイテム
					var barItems = [];

					// 凡例のアイテムを追加
					// getRootNode() は パネルを作っておかないと null が返るため、ここで行う.
					var rootNode = store.getRootNode();
					// ノードを追加時に、チェックボックスに戻ってしまうためラジオに変換する
					rootNode.on("append", function(thisNode, node, index, eOpts) {me.changeViewCheckbox2Radio(thisNode);});
					rootNode.on("insert", function(thisNode, node, refNode, eOpts) {me.changeViewCheckbox2Radio(thisNode);});

					for(var ecommapsKey in map.ecommaps) {
						var ecommap = map.ecommaps[ecommapsKey];
						// 凡例ウィンドウに表示しないレイヤをレイヤIDで非表示にする
						var excludesLayerIds = map.excludesLayerIdsFromLegendWindow; // 凡例ウィンドウに表示しないレイヤのレイヤIDを設定する
						for(var idx in excludesLayerIds) {
							var excludesLayerId = excludesLayerIds[idx];
							var excludesLayerInfo = ecommap.layerInfoStore[excludesLayerId];
							if(typeof excludesLayerInfo!="undefined") {
								excludesLayerInfo.visibility = false;
							}
						}
						// すべてのレイヤを凡例の対象にする
						for(var layerInfoTreeKey in ecommap.layerInfoTree) {
							var layerInfo = ecommap.layerInfoTree[layerInfoTreeKey];
							var node = me.createTreeNode(rootNode, layerInfo);
							if(node!=null) {
								rootNode.appendChild(node);
								me.changeViewCheckbox2Radio(node);
								var layer = layerInfo.layer;
								if(layer!=null && typeof layer.refreshParams=="function") {
									layer.refreshParams();
								}
							}
						}
						// クリアリングハウスのURLが指定されていれば、
						// 地図追加ボタンを配置する
						if(!!ecommap.cswURL && !me.addMapBtn) {
							me.addMapBtn = {
								text: lang.__('Add map'),
								icon: map.icon.getURL("addIconURL"),
								handler: function() {
									outermap_select();
								}
							};
							barItems.push(me.addMapBtn);
						}
					}

					// ツールバー
					var bar = null;
					if(0<barItems.length) {
						me.addTbarItems(barItems);
					}
				},
				checkchange: function(node, checked, eOpts) {
					/**
					 * 各ノードで行うチェックボックスの処理
					 */
					var onCheckChange = function(node, checked, option) {
						if(node!=null) {
							// チェックボックスでレイヤの表示を切り替える
							var layerInfo = node.raw.layerInfo;
							if(typeof layerInfo!="undefined") {
								// 凡例別の表示切替対応
								if(!!node.raw && !!node.raw.isRuleNode) {
									// チェックしたレイヤの全ルールノードのチェック状態を取得する
									var ruleNodes = node.parentNode.childNodes;
									var checkedMap = {};
									for(var ruleNodesIdx in ruleNodes) {
										var ruleNode = ruleNodes[ruleNodesIdx];
										var ruleId = ruleNode.raw.rule.ruleId;
										checkedMap[ruleId] = ruleNode.data.checked;
									}

									// ルールノードのチェック状態をLayerInfoに反映する
									for(var legendrulesIdx in layerInfo.legendrules) {
										var rule = layerInfo.legendrules[legendrulesIdx];
										rule.visibility = rule.searchable = checkedMap[rule.ruleId];
									}

									// レイヤ情報のパラメータを更新
									layerInfo.updateRuleParam();
								}
								// 通常の凡例
								else {
									layerInfo.visibility = checked;
									layerInfo.searchable = checked;
								}
								var recursive = function(n) {
									// チェックボックスを利用している場合のみ親のチェック状態を設定
									if(n.get("checked")!=null) {
										if(layerInfo.radio!=null) { // 親ノードにラジオの指定があるなら
											// ON の場合
											if(checked) {
												// ON の兄弟がいるならなにもしない
												if(n.existOnBrother()) return;
												// ONになっている兄弟がいなければ
												// ラジオの場合は最初のノードだけがONになる
												else {
													var brothers = node.childNodes;
													for(var idx in brothers) {
														var brother = brothers[idx];
														if(brother.get("checked")!=null) {
															// 最初のチェックボックスノード以外は処理しない
															if(n!=brother) return;
															else break;
														}
													}
												}
											}
											// OFF の場合は、チェックを外さないので何も処理しない
											else return;
										}
										n.set("checked", checked);
										me.changeViewCheckbox2Radio(n);
										onCheckChange(n, checked, option);
									}
								};
								if(!option==false) {
									if(option.doChildren) {
										// チェック状態の取得
										var childIsRuleNode = false;
										for(var childNodesIdx in node.childNodes) {
											var childNode = node.childNodes[childNodesIdx];
											if(!!childNode.raw)
												childIsRuleNode = !!childNode.raw.isRuleNode;
										}

										// SLD表示ルールを制御するノードの場合
										if(childIsRuleNode) {
											// 一部の子ノードがすでにチェック済みなら、子ノードのチェック状態は変えない
											// 子ノードの全ON/OFF処理を無効化する
											if(!!layerInfo.params && layerInfo.params.rule!=null) {
												return;
											}
										}

										// チェック状態をすべての子ノードに設定
										node.eachChild(recursive);
									}
									if(option.doParent) {
										// チェック状態をすべての親ノードに設定
										recursive(node.parentNode);
									}
								}
								// ラジオの場合は他の兄弟をOFFにする
								var isRadio = layerInfo.parent!=null && layerInfo.parent.radio!=null;
								if(checked && isRadio) {
									var brothers = node.parentNode.childNodes;
									for(var idx in brothers) {
										if(node!=brothers[idx]) {
											var brother = brothers[idx];
											if(brother.get("checked")!=null) {
												brother.set("checked", false);
												me.changeViewCheckbox2Radio(brother);
												onCheckChange(brother, false);
											}
										}
									}
								}
							}
						}
					};
					// ラジオの場合、OFFさせない
					var isRadio = typeof node.raw.radio!="undefined" && node.raw.radio!=null;
					if(isRadio) {
						me.changeViewCheckbox2Radio(node);
						if(checked==false) {
							node.set("checked", true);
							me.changeViewCheckbox2Radio(node);
							return;
						}
					}
					onCheckChange(node, checked, {doChildren: true});
					// チェックを入れた場合は親ノードのチェックをすべて入れる
					if(checked==true) {
						onCheckChange(node, checked, {doParent: true});
					}
					// レイヤの表示状態を変更
					var refreshLayerInfos = [node.raw.layerInfo];
					while(0<refreshLayerInfos.length) {
						var layerInfo = refreshLayerInfos.pop();
						var layer = layerInfo.getLayer();
						// レイヤ情報にレイヤが見つからない場合は、下位のレイヤ情報を検索する
						if(!layer) {
							for(var idx in layerInfo.children) {
								refreshLayerInfos.push(layerInfo.children[idx]);
							}
						}
						// レイヤがある場合
						else {
							// レイヤの refreshParam を呼ぶ
							if(typeof layer.refreshParams=="function") {
								layer.refreshParams();
							}
							else {
								layer.setVisibility(checked);
							}
						}
					}
					map.events.triggerEvent("legendcheckchange");
				},
				afteritemexpand: function(node, index, item, eOpts) {
					me.changeViewCheckbox2Radio(node);
				},
				itemexpand: function(node, eOpts) { me.onChangeExpanded(node, eOpts); },
				itemcollapse: function(node, eOpts) { me.onChangeExpanded(node, eOpts); },
				itemcontextmenu: function(view, record, htmlItem, index, e, eOpts) {
					var layerInfo = record.raw.layerInfo;
					if(typeof layerInfo!="undefined") {
						var contextmenu = map.components.contextmenu;
						if(typeof contextmenu!="undefined") {
							e.stopEvent();
							var option = {
								tree: tree,
								record: record,
								layerInfo: layerInfo,
								addContentsMenu: true,
								addContentsMenuLayerInfos: [layerInfo],
								deleteLayerMenu: layerInfo.isDeletableSessionExternalMapLayer(),
								transparencyLayerMenu: SaigaiTask.Map.Layer.Type.isOverlayLayerType(layerInfo.type) || [
									SaigaiTask.Map.Layer.Type.REFERENCE,
									SaigaiTask.Map.Layer.Type.REFERENCE_WMS,
									SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS,
									SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS_LAYERS,
									SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_XYZ
									].includes(layerInfo.type)
							};
							contextmenu.showMenu(e.xy[0], e.xy[1], option);
						}
					}
				}
			}
		});
		me.tree = tree;

		map.events.triggerEvent("legendinitialize", {
			legend: me
		});
	},

	/**
	 * ツールバーに追加する.
	 * @params {Ext.Component[]} items
	 */
	addTbarItems: function(items) {
		var me = this;
		if(!!me.fbar) {
			me.fbar.add(items);
		}
		else {
			bar = me.fbar = Ext.create('Ext.toolbar.Toolbar', {
				items: items,
				dock: "bottom"
			});
			me.tree.addDocked(bar);
		}
	},

	onChangeExpanded: function(node, eOpts) {
		var me = this;
		var map = me.map;
		me.changeViewCheckbox2Radio(node);
		// 凡例の畳み込み情報を保存する
		var expanded = node.isExpanded();
		var layerInfo = node.raw.layerInfo;
		layerInfo.expanded = expanded;
		map.events.triggerEvent("legenditemexpanded");
	},

	/**
	 * レイヤ情報から凡例ツリーのノードを生成する.
	 * @param parentNode 親ノード
	 * @param layerInfo レイヤ情報
	 * @returns 凡例ツリーのノード 除く場合はnullを返す
	 */
	createTreeNode: function(parentNode, layerInfo) {

		var me = this;
		var map = me.map;
		var ecommap = layerInfo.ecommap;

		// 凡例ウィンドウに表示しないレイヤをレイヤIDで取り除く
		var excludesLayerIds = map.excludesLayerIdsFromLegendWindow; // 凡例ウィンドウに表示しないレイヤのレイヤIDを設定する
		if($.inArray(layerInfo.layerId, excludesLayerIds)!=-1) {
			return null;
		}
		// レイヤ種別で取り除く
		if(ecommap!=null) {
			var excludesLayerTypes = SaigaiTask.Map.Layer.Type.getBaseLayerTypes();
			if($.inArray(layerInfo.type, excludesLayerTypes)!=-1) {
				return null;
			}
		}

		// ノードの設定
		var nodeConfig = {};
		nodeConfig.expanded = layerInfo.expanded;
		nodeConfig.text = layerInfo.name;
		if(!!layerInfo.loaderror) nodeConfig.text = lang.__("[Read error]")+nodeConfig.text; // for extlayer
		nodeConfig.layerInfo = layerInfo;


		// 内部ノード
		if(0<layerInfo.children.length) {
			nodeConfig.checked = layerInfo.visibility;
			nodeConfig.children = [];
			var node = parentNode.createNode(nodeConfig);
			node.raw = nodeConfig;
			if(ecommap!=null) {
				// 凡例画像を取得できるか確認
				var legendUrl = ecommap.ecommapURL+'/legend?WIDTH=44&HEIGHT=32&cid='+ecommap.mapInfo.communityId+'&mid='+ecommap.mapInfo.mapId+'&layer='+layerInfo.layerId+'&SCALE=1000';
				var img = new Image();
				//img.onerror = function() {alert("image load error!");}
				img.onload = function() {
					// 先頭に凡例画像を追加
					node.insertChild(0, node.createNode({
						icon: legendUrl,
						leaf: true
					}));
				}
				img.src = legendUrl;
			}
			for(var childLayerInfosKey in layerInfo.children) {
				var childLayerInfo = layerInfo.children[childLayerInfosKey];
				var childNode = this.createTreeNode(node, childLayerInfo);
				if(childNode!=null) {
					node.appendChild(childNode);
				}
			}
			return node;
		}
		// 葉ノード
		else {
			// 子ノードの設定
			if(layerInfo.type!=SaigaiTask.Map.Layer.Type.LOCAL_GROUP) {
				//
				nodeConfig.iconCls = 'no-icon';
				nodeConfig.checked = layerInfo.visibility;
				// ラジオ化
				if(layerInfo.parent!=null) {
					if(layerInfo.parent.radio!=null) {
						nodeConfig.radio = layerInfo.parent.radio;
						nodeConfig.id = layerInfo.layerId;
					}
				}
				var node = parentNode.createNode(nodeConfig);
				node.raw = nodeConfig;
				if(nodeConfig.radio!=null) {
					// すでにONの兄弟がいれば、OFFにしておく
					node.existOnBrother = function() {
						var existOnBrother = false;
						for(var idx in parentNode.childNodes) {
							if(parentNode.childNodes[idx])
							var brother = parentNode.childNodes[idx];
							if(brother.get("checked")==true) {
								existOnBrother = true;
								break;
							}
						}
						return existOnBrother;
					}
					if(node.existOnBrother()) {
						node.set("checked", false);
						layerInfo.visibility = false;
						layerInfo.searchable = false;
						var layer = layerInfo.getLayer();
						layer.refreshParams();
					}
				}
				// 凡例画像
				if(ecommap!=null){
					// 登録情報レイヤで属性による描画切替が２つ以上ある場合
					if(layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL
							&& !!layerInfo.legendrules && 2<=layerInfo.legendrules.length) {
						var iconHeight = 32;
						for(var legendrulesIdx in layerInfo.legendrules) {
							// ruleNode 生成
							var rule = layerInfo.legendrules[legendrulesIdx];
							var ruleNodeConfig = {
								cls: "legendpanel-legendrule-node",
								text: rule.title,
								checked: rule.visibility,
								icon: ecommap.ecommapURL+'/legend?WIDTH=44&HEIGHT='+iconHeight+'&cid='+ecommap.mapInfo.communityId+'&mid='+ecommap.mapInfo.mapId+'&layer='+layerInfo.layerId+'&SCALE=1000&'+new Date().getTime()+"&rule="+layerInfo.layerId+":"+rule.ruleId,
								leaf: true
							};
							ruleNodeConfig.layerInfo = layerInfo;
							ruleNodeConfig.isRuleNode = true;
							ruleNodeConfig.rule = rule;
							var ruleNode = node.createNode(ruleNodeConfig);
							ruleNode.raw = ruleNodeConfig;

							// ノード情報を記録
							rule.node = ruleNode;

							// ノード追加
							node.insertChild(legendrulesIdx, ruleNode);
						}
					}
					// 登録情報レイヤの凡例画像ノードを追加
					else if(layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL
							|| layerInfo.type == SaigaiTask.Map.Layer.Type.REFERENCE) {
						node.insertChild(0, node.createNode({
							icon: ecommap.ecommapURL+'/legend?WIDTH=44&HEIGHT=32&cid='+ecommap.mapInfo.communityId+'&mid='+ecommap.mapInfo.mapId+'&layer='+layerInfo.layerId+'&SCALE=1000&'+new Date().getTime(),
							leaf: true
						}));
					}
					// 外部地図レイヤの凡例画像ノードを追加
					else if (layerInfo.type == SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS_LAYERS) {
						var url = layerInfo.wmsLegendURL;

						// 親のwmsLegendURL は GetLegendGraphic になっているはず
						if (!url && layerInfo.parent) {
							url = layerInfo.parent.wmsLegendURL;
							if(0<url.length) url += '&REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH=44&HEIGHT=32&layer=' + layerInfo.featuretypeId+'&'+new Date().getTime();
						}

						// URLがある場合
						if(!!url && 0<url.length) {
							// 認証情報
							if(layerInfo.wmsproxy != null){
								if (layerInfo.wmsproxy != 0 && url.indexOf("http") == 0) {
									var page_url = SaigaiTask.PageURL.getUrl();
									// Proxy用のActionに飛ばす
									url = page_url.substr(0, page_url.indexOf("/page/") + 6) + "map/externalWmsAuth/?url=" + encodeURIComponent(url) + "&externalmapdatainfoid="+layerInfo.wmsproxy + "&metadataid="+layerInfo.metadataid;
								}
							}

							node.insertChild(0, node.createNode({
								icon: url,
								leaf: true
							}));
						}
					}
					// ArcGIS用の凡例画像
					else if(layerInfo.type == SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_ARCGIS_LAYERS){
						var url = layerInfo.wmsLegendURL;
						if (!url && layerInfo.parent)
							url = layerInfo.parent.wmsLegendURL;
						if (url){
							$.ajax({
								url : url + "&f=json",
								dataType : "json",
								data : this.param,
								type : "POST",
								contentType: "application/x-www-form-urlencoded; charset=UTF-8",
								success : function(data, dataType){
									var legendURL = "";
									if('layers' in data){
										var j_layers = data.layers;
										for(var i = 0; i < j_layers.length; i++){
											if("legend" in j_layers[i]){
												for(var j = 0; j < j_layers[i].legend.length; j++){
													if("imageData" in j_layers[i].legend[j]){
														node.insertChild(0, node.createNode({
															icon: "data:image/png;base64," + j_layers[i].legend[j].imageData,
															text: j_layers[i].legend[j].label,
															leaf: true
														}));
													}
												}
											}
										}
									}
								},
								error : function(XMLHttpRequest, textStatus, errorThrown){
									console.log("ArcGIS Legend ImageData error");
								}
							});
						}
					}
					// 凡例画像がない場合はノードを追加しない
					else {
						console.log("LegendPanel is Else");
						node.leaf = true;
					}
				}
			}
			return node;
		}

		return null;
	},

	/**
	 * (ExtJSのラジオボタン画像を指定している)独自のClassを追加することで
	 * チェックボックスをラジオボタンの見た目に変更する.
	 * @param {Ext.data.NodeInterface} node
	 */
	changeViewCheckbox2Radio: function(node) {
		var me = this;
		if(!!node.raw && node.raw.radio!=null) {
			view = me.tree.getView()
			var elm = Ext.get(view.getNode(node));
			if(elm!=null) {
				elm.select(".x-tree-checkbox").addCls("x-tree-radio");
			}
		}

		if(node.childNodes!=null) {
			for(var idx in node.childNodes) {
				var child = node.childNodes[idx];
				me.changeViewCheckbox2Radio(child);
			}
		}
	}

};
