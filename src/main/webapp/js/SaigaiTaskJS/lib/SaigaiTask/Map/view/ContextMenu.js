/**
 * 地図のコンテキストメニューです.
 * @require SaigaiTask/Map/view.js
 * @returns {SaigaiTask.Map.view.ContextMenu}
 */
SaigaiTask.Map.view.ContextMenu = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.view.ContextMenu.prototype = {

	stmap: null,

	/**
	 * クリックしたときのマウスの座標
	 * @type {OpenLayers.LonLat}
	 */
	lonlat: null,

	initialize: function(stmap, options) {
		var me = this;
		me.stmap = stmap;

		document.getElementById(stmap.div).oncontextmenu = function(e){
			// IE8 対応
			e = e?e:window.event;

			// 登録メニューは使用しない
			if(options!="object") options = {};
			jQuery.extend(options, {
				addContentsMenu: false
			});

			// コンテキストメニューを開いたときのマウスの座標
			var evt = new OpenLayers.Events(e, document.getElementById(stmap.div));
			var xy = evt.getMousePosition(e);
			me.lonlat = stmap.map.getLonLatFromPixel(new OpenLayers.Pixel(xy.x, xy.y));

			// IE8 対応
			var x = e.pageX ? e.pageX : e.clientX;
			var y = e.pageY ? e.pageY : e.clientY;
			return me.showMenu(x, y, options);
		};

		// イベントハンドラ
		me.stmap.events.on({
			// 地図上クリックしたらメニューを閉じる
			"beforemapclick": function() {
				if(!!me.menu && me.menu.isVisible()) {
					me.menu.close();
				}
			}
		})
	},

	/**
	 * メニューを表示します.
	 * @param {Number} x メニューを表示するX座標
	 * @param {Number} y メニューを表示するY座標
	 * @param {SaigaiTask.Map.model.ShowMenuOptions} オプション
	 * @return {Boolean} イベントバブリングするかどうか
	 */
	showMenu: function(x, y, showMenuOptions) {

		if(typeof Ext=='undefined') return;

		var me = this;
		var stmap = me.stmap;

		// メニューのアイテム
		var items = [];
		var lonlat = me.lonlat;

		// オプションの設定
		var options = new SaigaiTask.Map.model.ShowMenuOptions();
		SaigaiTask.Map.copy(options, showMenuOptions);

		// 登録メニューを追加
		if(options.addContentsMenu) {
			var addContentsMenuLayerInfos = [];
			if(typeof options.addContentsMenuLayerInfos!="undefined") {
				addContentsMenuLayerInfos = options.addContentsMenuLayerInfos;
			}
			else {
				for(var ecommapsKey in stmap.ecommaps) {
					var ecommap = stmap.ecommaps[ecommapsKey];
					var contentsLayers = ecommap.contentsLayers;
					for(var contentsLayersKey in contentsLayers) {
						var contentsLayer = contentsLayers[contentsLayersKey];
						addContentsMenuLayerInfos.push(contentsLayer);
					}
				}
			}
			items.push(me.createAddContentsMenuItem(lonlat, addContentsMenuLayerInfos));
		}

		// 削除メニューを追加
		if(options.deleteLayerMenu) {
			var layerInfo = options.layerInfo; // レイヤ情報
			var record = options.record; // 凡例パネルのレコード
			items.push(me.createDeletemenuItem(layerInfo, record));
		}

		//透明度変更メニューを追加
		if(options.transparencyLayerMenu) {
			var layerInfo = options.layerInfo; // レイヤ情報
			var record = options.record; // 凡例パネルのレコード
			items.push(me.createTransparencyMenuItem(layerInfo, record));
		}

		//この場所について
		if(lonlat!=null) {
			items.push(me.createAddPlaceMenuItem(lonlat));
		}

		// メニューを表示する
		if(0<items.length) {
			var menu = me.menu = Ext.create('Ext.menu.Menu', {
				renderTo: Ext.getBody(),
				items: items
			});

			// 指定された位置が無効であれば画面右上に表示する
			if(typeof x=='undefined') x = y = 0;
			menu.setPosition(x, y);

			// メニューを表示
			menu.show();
			return false;
		}

		return true;
	},

	/**
	 * 新規登録メニュー情報を作成します.
	 * 1つしかない場合はサブメニューを作らない
	 * @param {Array<Object>} layerInfos 登録情報レイヤ情報配列
	 */
	createAddContentsMenuItem: function(lonlat, layerInfos) {
		var me = this;
		var stmap = me.stmap;

		// レイヤ選択メニューを作成
		var items = [];
		/** 再帰で子の登録情報を追加する */
		var createItems = function(layerInfo) {
			if(layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL) {
				// 追加可能なレイヤならば、メニューに表示する
				if(layerInfo.addable==true) {
					items.push({
						text: layerInfo.name,
						layerInfo: layerInfo,
						icon: layerInfo.ecommap.ecommapURL+'/legend?WIDTH=44&HEIGHT=32&cid='+layerInfo.ecommap.mapInfo.communityId+'&mid='+layerInfo.ownerMapId+'&layer='+layerInfo.layerId+'&SCALE=1000'
					});
				}
			}
			else {
				for(var idx in layerInfo.children) {
					var childLayerInfo = layerInfo.children[idx];
					createItems(childLayerInfo);
				}
			}
		};
		for(var idx in layerInfos) {
			var layerInfo = layerInfos[idx];
			createItems(layerInfo);
		}
		// 新規登録メニュー情報
		var menuItem = {
			text: lang.__('Register'),
			icon: stmap.icon.getURL("addIconURL")
		};

		// メニュー選択時の処理
		var onClick = function(layerInfo) {
			// 登録フォームのウィンドウを表示する
			new SaigaiTask.Map.view.ContentsFormWindow({
				stmap: stmap,
				layerInfo: layerInfo,
				lonlat: lonlat
			});
		};

		// 新規登録可能なレイヤがない場合
		if(items.length==0) {
			return null;
		}
		// 1つしかない場合はサブメニューを作成しない
		else if(items.length==1) {
			jQuery.extend(menuItem, {
				listeners: {
					click: function(menu, item, e, eOpts) {
						var layerInfo = items[0].layerInfo;
						onClick(layerInfo);
					}
				}
			});
		}
		// 新規登録可能なレイヤが複数ある場合
		else {
			jQuery.extend(menuItem, {
				hideOnClick: false,
				menu: {
					items: items,
					listeners: {
						click: function(menu, item, e, eOpts) {
							var layerInfo = item.layerInfo;
							onClick(layerInfo);
						}
					}
				}
			});
		}

		return menuItem;
	},

	/**
	 * 削除メニュー情報を作成します.
	 * @type {SaigaiTask.Map.Layer.LayerInfo} layerInfo
	 * @type {Ext.data.Model} record LegendPanel の TreePanel のレコード
	 */
	createDeletemenuItem: function(layerInfo, record) {
		var me = this;
		var stmap = me.stmap;

		// 削除メニュー情報
		var menuItem = {
			text: lang.__('Delete'),
			icon: stmap.icon.getURL("deleteIconURL"),
			listeners: {
				click: function(menu, item, e, eOpts) {
					// 凡例パネルから削除
					var parentNode = record.parentNode;
					record.remove();
					// 地図からレイヤを削除
					{
						// レイヤ情報ツリーを配列として取得
						var layerInfos = [layerInfo];
						if(!!layerInfo.children && layerInfo.children.length > 0) {
							layerInfos = layerInfos.concat(layerInfo.children);
						}

						var records = [];
						for(var idx in layerInfos) {
							// set LayerInfo opacity
							var linfo = layerInfos[idx];
							// set Layer opacity
							var l = linfo.getLayer();
							if(l!=null) {
								stmap.map.removeLayer(l);
							}
						}
						// 凡例パネルの親も削除する
						if(!!parentNode && parentNode.childNodes.length==0) {
							parentNode.remove()
						}
					}
					// セッションの外部地図の場合は、セッションから削除する
					if(layerInfo.isSessionExternalMapLayer()) {
						var records = [];
						var registDeleteRecordRecursive = function(records, layerInfo) {
							if(layerInfo.metadataid!=null) {
								records.push({
									/** メニューID */
									menuinfoid: SaigaiTask.Page.menuInfo.id,
									/** メタデータID */
									metadataid: layerInfo.metadataid,
									updateTime: Ext.Date.format(new Date(), "Y-m-d\\TH:i:s"),
									state: "remove"
								});
							}
							if(layerInfo.children!=null) {
								for(var idx in layerInfo.children) {
									var child = layerInfo.children[idx];
									registDeleteRecordRecursive(records, child);
								}
							}
						};
						registDeleteRecordRecursive(records, layerInfo);

						// サーバのセッションに保存
						if(0<records.length) {
							jQuery.ajax(SaigaiTask.contextPath+"/page/map/sessionMetadata", {
								async: true,
								type: "POST",
								dataType: "json",
								data: {
									records: JSON.stringify(records)
								},
								success: function(data, textStatus, jqXHR) {
									// do nothing
								},
								error: function(jqXHR, status, errorThrown) {
									// do nothing
									console.error(errorThrown);
								}
							});
						}
					}
				}
			}
		};

		return menuItem;
	},

	createAddPlaceMenuItem: function(lonlat) {
		var me = this;
		var stmap = me.stmap;

		// 新規登録メニュー情報
		var menuItem = {
			text: lang.__('About this place,'),
			icon: stmap.icon.getURL("markerIconURL")
		};
		//ランドマーク検索機能が有効か無効か
		var resultLandmark = stmap.api.landmarkValid();
		var landmarkValid = 0;
		var dialogH = 200;//false時の値
		resultLandmark.done(function(message){
			if(message.valid !== undefined){
				if(message.valid == 1){
					landmarkValid = 1;
					dialogH = 290;
				}
			}
		});

		var onClick = function() {
			var win = null;
			lonlat = lonlat.clone().transform(stmap.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));

			Ext.Ajax.request({
				url: stmap.api.url.pointInfoURL,
				params: 'lon='+lonlat["lon"]+'&lat='+lonlat["lat"]+'&_csrf='+SaigaiTask.ajaxcsrfToken,
				success: function(param) {
					var result = JSON.parse(param.responseText);
					var formPanel;

					//ランドマーク登録機能有効時
					if(landmarkValid == 1){
						formPanel = Ext.create('Ext.form.Panel',{
							xtype: 'form',
							autoScroll: true,
							items: [
								{
									xtype: 'fieldset',
									title: lang.__('About this place,'),
									margin: '5',
									// layout:fit なので、幅高さを指定しない
									//width: 275,
									//height: 125,
									defaults: {xtype: 'displayfield', margin: '3', labelWidth: 100, width: 260},
									items: result
								},

								{
									xtype: 'fieldset',
									title: lang.__('Register landmark'),
									margin: '5',
									// layout:fit なので、幅高さを指定しない
									//width: 275,
									//height: 90,
									//style: 'text-align:right;',//「ランドマーク登録」のタイトル文字も右寄せになってしまう
									//defaults: {xtype: 'displayfield', margin: '3', labelWidth: 100, width: 260},
									items: [
										{
											xtype : 'textfield',
											fieldLabel : lang.__('Landmark name'),
											name : 'landmark',
											width : 250
										},
										{
											xtype : 'button',
											text : lang.__('Registration'),
											width : 50,
											//style: 'text-align:right;',//効かない
											handler: function(){
												var landmarkVal = formPanel.getForm().findField('landmark').getValue();
												if(landmarkVal.length>0){
													//alert(landmarkVal+'を登録しました。\nlon='+lonlat["lon"]+', lat='+lonlat["lat"]);
													var result = stmap.api.landmarkRegist(landmarkVal, lonlat["lon"], lonlat["lat"]);
													result.done(function(message){
														if(message.error === undefined){
															alert(lang.__('{0} registered.', message.landmark));
														}else{
															//エラー時にはmessage.errorにメッセージが入っている
															alert(message.error);
														}
													});
													result.fail(function(message){
														alert(lang.__("Failed to register landmark."));
													});
													win.close();
												}else{
													alert(lang.__("Enter the name of landmark to be registered"));
												}
											}
										}
									]
								}
							]
						});

					//ランドマーク登録機能有効時
					}else{
						formPanel = Ext.create('Ext.form.Panel',{
							xtype: 'form',
							autoScroll: true,
							items: [
								{
									xtype: 'fieldset',
									title: lang.__('About this place,'),
									margin: '5',
									// layout:fit なので、幅高さを指定しない
									//width: 275,
									//height: 125,
									defaults: {xtype: 'displayfield', margin: '3', labelWidth: 100, width: 260},
									items: result
								}
							]
						});
					}

					if(!!me.window) me.window.close();
					win = me.window = Ext.create('Ext.window.Window', {
						title: lang.__('About this place,'),
						//width: 300, height: 290,
						width: 300, height: dialogH,
						maxWidth: document.body.clientWidth,
						maxHeight: document.body.clientHeight,
						layout: 'fit',
						items: formPanel,
						buttons: [{
							text: lang.__('Close'),
							handler: function(){ win.close(); }
						}]
					});

					// ウィンドウ表示
					win.show();
				},
				callback: function() {
				}
			});
		};

		jQuery.extend(menuItem, {
			listeners: {
				click: function(menu, item, e, eOpts) {
					onClick();
				}
			}
		});

		return menuItem;
	},

	/**
	 * 透明度変更メニュー情報を作成します.
	 * @type {Map.Layer.LayerInfo} layerInfo
	 */
	createTransparencyMenuItem: function(layerInfo, record){
		var me = this;
		var stmap = me.stmap;

		// 透明度変更メニュー情報
		var menuItem = {
			text: lang.__('Transmittance change'),
			icon: stmap.icon.getURL("transparencyURL"),
			listeners:{
				click: function(){
					var form = Ext.create('Ext.form.Panel',{
						xtype: 'form',
						autoScroll: true,
						items: [{
							xtype: 'combo',
							name: 'transparency',
							fieldLabel: lang.__('Transmittance'),
							store:{
								fields:['name','value'],
								data:[
								      {"name":lang.__("0%(nontransparent)"), "value":"1.0"},
								      {"name":"10%", "value":"0.9"},
								      {"name":"20%", "value":"0.8"},
								      {"name":"30%", "value":"0.7"},
								      {"name":"40%", "value":"0.6"},
								      {"name":"50%", "value":"0.5"},
								      {"name":"60%", "value":"0.4"},
								      {"name":"70%", "value":"0.3"},
								      {"name":"80%", "value":"0.2"},
								      {"name":"90%", "value":"0.1"}
								      ]
							},
							queryMode:'local',
							displayField:'name',
							valueField: 'value',
							//opcacityの値がきれいな数字ではないので四捨五入
							value: Math.round((1 - layerInfo.opacity) * 100)+'%'
						}],
						buttons:[{
							text: lang.__('Proceed'),
							handler: function() {
								// 透明度
								var opacity = form.getValues().transparency;

								// レイヤ情報ツリーを配列として取得
								var layerInfos = [layerInfo];
								if(!!layerInfo.children && layerInfo.children.length > 0) {
									layerInfos = layerInfos.concat(layerInfo.children);
								}

								var records = [];
								for(var idx in layerInfos) {
									// set LayerInfo opacity
									var linfo = layerInfos[idx];
									linfo.opacity = opacity;
									// set Layer opacity
									var l = linfo.getLayer();
									if(l!=null && l.opacity!=opacity) l.setOpacity(opacity);
									
									// create sessionMetadata record
									var record = {
											/** 更新リクエスト */
											state: "update",
											/** メニューID */
											menuinfoid: SaigaiTask.Page.menuInfo.id,
											/** 透明度 */
											layeropacity: opacity
									};
									
									// 外部地図の場合
									if(SaigaiTask.Map.Layer.Type.isExternalmapLayerType(linfo.type)) {
										if(!linfo.metadataid) continue;

										/** メタデータID */
										record.metadataid = linfo.metadataid;
										/** セッションに保存された外部地図レイヤかどうか */
										record.sessionExternalMapLayer = linfo.layerId.indexOf("extmap_session")==0;
									}
									// 主題図などの外部地図ではないeコミで持つレイヤの場合
									else {
										record.layerId = linfo.layerId;
									}
									records.push(record);
								}

								jQuery.ajax(SaigaiTask.contextPath+"/page/map/sessionMetadata", {
									headers: {"X-CSRF-Token":'${cookie.JSESSIONID.value}'},
									async: false,
									type: "post",
									data: {
										records: JSON.stringify(records)
									},
									success: function(data, textStatus, jqXHR) {
										//SaigaiTask.PageURL.move();
										
									},
									error: function(jqXHR, status, errorThrown) {
										// do nothing
										console.error(errorThrown);
									}
								});

				                // フォームデータの取得
				                console.log(form.getValues().transparency);
				                win.close();
				            },
						},{
							text: lang.__('Close'),
							handler: function(){ win.close(); }
						}
						]
					});
					if(!!me.window) me.window.close();
					win = me.window = Ext.create('Ext.window.Window', {
						title: lang.__('Transmittance change'),
						width: 300, height: 100,
						maxWidth: document.body.clientWidth,
						maxHeight: document.body.clientHeight,
						layout: 'fit',
						items: form
					});

					// ウィンドウ表示
					win.show();
				}
			}
		};

		return menuItem;
	}
};
