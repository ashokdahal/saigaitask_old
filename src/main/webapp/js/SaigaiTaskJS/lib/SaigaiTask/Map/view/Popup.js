/**
 * OpenLayersのポップアップをExtJSを使って表示するクラスです.
 * @class SaigaiTask.Map.view.Popup
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.Popup = new OpenLayers.Class({

	/**
	 * ポップアップ
	 * @type {OpenLayers.Popup}
	 */
	popup: null,

	/**
	 * ポップアップのコンテンツ表示用パネル
	 * @type {Ext.panel.Panel}
	 */
	panel: null,

	initialize: function() {
	},

	/**
	 * ユニークなIDを生成する.
	 * PopupManager が使用する.
	 */
	createPopupId: function() {
		return 'extpopup-'+Ext.id();
	},

	/**
	 * ExtJS のパネル付きでポップアップを表示する
	 * @param {Object<String, *>} options オプション
	 * @param {Object<String, *>} options.searchButtonInfo 検索ボタンのオプション
	 * @param {String} options.searchButtonInfo.url 検索をリクエストするURL
	 * @param {Number} options.searchButtonInfo.mid 検索対象の地図ID
	 * @param {Array<Object.<String, *>>} options.searchButtonInfo.contentsLayers 検索対象の登録情報レイヤ情報の配列
	 * @return {OpenLayer.Popup}
	 */
	showExtPopup: function(options){
		var me = this;
		// オプション
		var map = options.map;
		var stmap = map;
		var olmap = options.olmap;
		var center = options.center;
		var items = options.items;
		var toolbarData = options.toolbarData;
		var panelWidth = options.panelWidth;
		var title = options.title;
		var pinned = (options.pinned==true);
		console.log('options');
		console.log(options);

		// ポップアップ表示
		var popupLonLat = center.clone();
		var size = options.size;
		if(!size) size = new OpenLayers.Size(300,200);
		var popupId = this.createPopupId();
		popupLonLat = popupLonLat.transform(new OpenLayers.Projection("EPSG:4326"), olmap.getProjectionObject());
		var popup = me.popup = new OpenLayers.Popup.FramedCloud("popup", popupLonLat, size, "<div id='"+popupId+"'></div>");
		popup.panMapIfOutOfView = true;
		olmap.addPopup(popup, false);
		popup.hide();
		popup.setSize(size);
		// onmouseup を上書き
		popup.events.un({
			mouseup: popup.onmouseup
		});
		popup.onmouseup = function(evt) {
			if(this.mousedown) {
				this.mousedown = false;
				// イベントを止めるとポップアップ内のExt.Button等が
				// mouseupを検知できなくなりクリックしっぱなしの状態になってしまうため
				// イベントは止めない
				//OpenLayer.Event.stop(evt, true);
			}
		};
		popup.events.on({
			mouseup: popup.onmouseup
		});

		// ポップアップマネージャーに追加
		var popupManager = map.popupManager;
		if(popupManager==null) { // 未定義なら作成
			popupManager = new SaigaiTask.Map.control.PopupManager(map);
		}
		popupManager.add(popup);
		if(pinned) {
			popupManager.pin(popup);
		}

		/**
		 * リンクを開く.
		 * ThickBoxも対応
		 */
		var openLink = function(url, TB_iframe) {
			if(url){
				var urlObj = SaigaiTask.parseURL(url);
				if(typeof urlObj.searchObject.time!="undefined") {
					var time = urlObj.searchObject.time;
					if(time=="") {
						// 時間パラメータの付与
						var time = SaigaiTask.PageURL.getTime();
						if(!!time) {
							var iso8601Time = time.toISOString();
							// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
							if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
								iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
							}
							// url に timeパラメータ付与
							time = iso8601Time;
							urlObj.searchObject.time = iso8601Time;
							url =  urlObj.toURL();
						}
					}
				}

				if(typeof TB_iframe!="undefined"){
					if(TB_iframe){
						if (url.indexOf('?') > 0)
							url = url + "&TB_iframe=false&width=950&height=700";
						else
							url = url + "?TB_iframe=true&width=950&height=700";
					}
					tb_show(/*caption*/"", url);
				}
				else {
					window.open(url);
				}
			}
		};

		// ツールバーの準備
		var toolbar = null;
		if(toolbarData){
			toolbar = new Array();

			// データの準備
			var buttonDataArray = new Array();
			if(jQuery.isArray(toolbarData.buttons)){
				buttonDataArray = buttonDataArray.concat(toolbarData.buttons);
			};

			for(var buttonDataArrayKey in buttonDataArray){
				var buttonData = buttonDataArray[buttonDataArrayKey];
				var opened = false;
				var button = (function(buttonData){
					var button = null;
					switch(buttonData.type){
					case "link":
						var handler = function(){
							openLink(buttonData.url, buttonData.TB_iframe);
						};
						button = Ext.create("Ext.Button", {
							text: buttonData.text,
							iconCls:'file-icon',
							enableToggle: false,
							handler: handler
						});
						// リンクを開くならポップアップを表示しないで処理を抜ける
						if(buttonData.open) {
							handler();
							opened = true;
						}
						break;
					case "search":
						break;
					default:
						button = Ext.create("Ext.Button", buttonData);
						break;
					}
					return button;
				})(buttonData);
				if(opened) return;
				toolbar.push(button);
			}
			// 検索ボタンの作成
			if(SaigaiTask.Map.view.Popup.searchButton) {
				var button = Ext.create("Ext.Button", {
					//text: buttonData.text,
					text: lang.__("Search"),
					iconCls:'search-icon',
					enableToggle: false,

					// 検索ダイアログを表示する
					handler: function() {
						if(stmap.popupManager!=null) {
							stmap.popupManager.closeAll();
						}
						var contentsSearchView = new SaigaiTask.Map.view.ContentsSearch(map);
						contentsSearchView.show({
							searchForm: options
						});
					}
				});
				toolbar.push(button);
			}
		}

		Ext.onReady(function(){
			popup.show();
			popup.panIntoView();

			var margin = 50; // バルーンマージン（バルーン余白分）
			var mapMaxWidth = olmap.getSize().w - margin;
			var mapMaxHeight = olmap.getSize().h - margin - 80; // -80 はバルーンが地物を指している部分の高さ
			var maxWidth = mapMaxWidth;
			var maxHeight = mapMaxHeight;

			// パネルを追加
			me.panel = Ext.create("Ext.panel.Panel", {
				//
				renderTo: popupId,
				width: panelWidth,
				autoScroll: true,
				maxWidth: maxWidth,
				maxHeight: maxHeight<600 ? maxHeight : 600, // バルーンの画像が 600px までしか対応していない
				collapsible: true,
				// headers
				title: (typeof title!="undefined"&&title!=null) ? title : "",//'ポップアップ',
				header: true,
				tools: [{
					type: 'pin',
					hidden: pinned,
					handler: function(event, toolEl, owner, tool) {
						// pin
						popupManager.pin(popup);
						// unpin に切替
						tool.hide();
						owner.child('tool[type=unpin]').show();
					}
				}, {
					type: 'unpin',
					hidden: !pinned,
					handler: function(event, toolEl, owner, tool) {
						// unpin
						popupManager.unpin(popup);
						// pin に切替
						tool.hide();
						owner.child('tool[type=pin]').show();
					}
				}, {
					type: 'close',
					handler: function(event, toolEl, owner, tool) {
						popupManager.close(popup);
					}
				}],
				tbar: toolbar,
				// body
				layout: {
					type: 'vbox',
					align: 'stretch' //,padding: 5
				},
				items: items,
				// listeners
				listeners: {
					// サイズが変わったらポップアップのサイズも変えて表示する
					resize: function(self, width, height, oldWidth, oldHeight, eOpts){
						// ポップアップのサイズを変更する
						var margin = 25;
						var size = new OpenLayers.Size(width+margin, height+margin);
						popup.setSize(size);
						SaigaiTask.Map.util.jQueryUtil.visibility('#'+popup.id, true);
					}
				}
			});

			// div の指定があれば追加する
			if(!options.div==false) {
				me.panel.update($("<div>").append(options.div).html());
			}
		});
		return popup;
	}
});

SaigaiTask.Map.view.Popup.searchButton = true;