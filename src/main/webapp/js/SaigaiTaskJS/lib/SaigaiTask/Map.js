/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/**
 * Map client class using OpenLayers.
 * Connect to e-commap to get WMS etc.
 * @class SaigaiTask.SaigaiTask.Map
 * @param {String} div ID of div of OpenLayers.Map
 * @param {Object} options オプション
 */
SaigaiTask.Map = function(div, options){
	SaigaiTask.Map.util.CommonUtil.loadLibrary(options.contextPath);
	this.init(div, options);
}

/**
 * SVNリビジョン番号
 * @type {Number}
 */
SaigaiTask.Map.version = Number("$Revision: 6238 $".split(" ")[1]);

/**
 * srcからdestにプロパティをコピーします.
 * @param {Object} dest
 * @param {Object} src
 */
SaigaiTask.Map.copy = function(dest, src){
	if(src==null) return;
	if(dest==null) return;

	for(var property in src) {
		dest[property] = src[property];
	}
};

/**
 * destのundefinedまたはnullのプロパティをコピーします.
 * @param dest
 * @param src
 */
SaigaiTask.Map.extend = function(dest, src){
	if(src==null) return;
	if(dest==null) return;

	for(var property in src) {
		if(typeof dest[property]=="undefined"||dest[property]==null) {
			dest[property] = src[property];
		}
	}
};

/**
 * OpenLayers.Control.PanZoom のzoomworldボタンを拡張する.
 * @param {OpenLayers.Control.PanZoomBar | OpenLayers.Control.Zoom} panZoom
 */
SaigaiTask.Map.extendZoomWorld = function(panZoom){
	var zoomWorldListener = function(evt, map) {
		var zoomWorld = map.zoomWorld;
		if(typeof zoomWorld=="function"){
			zoomWorld();
			OpenLayers.Event.stop(evt);
			return false;
		}
	};

	// zoomworldアイコンを表示する
	if(panZoom.CLASS_NAME=="OpenLayers.Control.PanZoomBar") {
		panZoom.zoomWorldIcon = true;
		// onButtonClick で zoomworldボタンのイベントを拡張
		panZoom._onButtonClick = panZoom.onButtonClick;
		panZoom.onButtonClick = function(evt){
			var buttonElement = evt.buttonElement;
			var map = evt.object;
			switch (buttonElement.action) {
			case "zoomworld":
				return zoomWorldListener(evt, map);
			default:
				this._onButtonClick(evt);
			}
		};
	}

	if(panZoom.CLASS_NAME=="OpenLayers.Control.Zoom") {
		// create image btn(like OpenLayers.Control.PanZoom._addButton)
		var createImgButton = function(id, img, xy, sz, position) {
			var imgLocation = OpenLayers.Util.getImageLocation(img);
			var btn = OpenLayers.Util.createAlphaImageDiv(
					panZoom.id + "_" + id,
					xy, sz, imgLocation, "relative");
			btn.style.cursor = "pointer";
			btn.style.margin = "0px auto"; // centering
			return btn;
		};

		// create home button
		var id="zoomworld",
		img="zoom-world-mini.png",
		xy={x: 0, y: 2}, // outer div height:22. 2px margin of top and bottom.
		sz={w: 18, h:18};
		var btn = createImgButton(id, img, xy, sz, "relative");

		// set image title for tooltip
		var tooltip = lang.__("Move to the initial display position.");
		img = btn.getElementsByTagName("img")[0];
		img.title = tooltip;

		// click event
		btn.onclick = function(evt) {
			var map = panZoom.map;
			return zoomWorldListener(evt, map);
		}

		// wrap anchor element, for css "div.olControlZoom a"
		var link = document.createElement("a");
		link.appendChild(btn);

		// append to Zoom Control
		var div = panZoom.zoomInLink.parentElement;
		var position = null;
		switch(position) {
		// 上に追加
		case "head": div.insertBefore(link, panZoom.zoomInLink); break;
		// 下に追加
		case "tail": div.appendChild(link); break;
		// ＋と－の間に追加
		default: div.insertBefore(link, panZoom.zoomOutLink); break;
		}
	}

};

/**
 * オブジェクトをSAStrutsのクエリ文字列に変換する.
 * @param {Object} obj
 * @return {String} クエリ文字列
 */
SaigaiTask.Map.param = function(obj) {
	var prefix =null;
	var params = [];
	var add = function(key, value) {
		value = value == null ? "" : value;
		params[params.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
	};
	/**
	 * @param prefix HTTPリクエストパラメータ名
	 * @param obj HTTPリクエストパラメータ値
	 */
	var buildParams = function(prefix, obj) {
		if(jQuery.isArray(obj)) {
			jQuery.each(obj, function(idx, value) {
				buildParams(prefix+"["+idx+"]", value);
			});
		}
		else if(jQuery.type(obj)==="object") {
			var name = null;
			for(name in obj) {
				buildParams(prefix+"."+name, obj[name]);
			}
		}
		else {
			add(prefix, obj);
		}
	};

	for(prefix in obj) {
		buildParams(prefix, obj[prefix]);
	}

	return params.join("&").replace(/%20/g, "+");
};

SaigaiTask.Map.prototype = {

	/**
	 * 初期化オプション
	 * @type {Object}
	 */
	options: null,

	/**
	 * OpenLayersのイベントクラス
	 * @type {OpenLayers.Events}
	 */
	events: null,

	/**
	 * SaigaiTask.Mapクラスのイベントタイプ
	 * @type {Object<String, String>}
	 */
	EventType: {
		/** eコミマップのロード前 */
		beforeloadecommap: "beforeloadecommap",

		/** eコミマップのロード後 */
		loadendecommap: "loadendecommap",

		/** 全レイヤのロード完了後 */
		alllayerloadend: "alllayerloadend",

		/** 登録情報検索ウィンドウ表示前 */
		beforeshowcontentsearchview: "beforeshowcontentsearchview",

		/** 登録情報ポップアップ表示前 */
		beforeshowcontentpopup: "beforeshowcontentpopup",

		/** 凡例パネルをパネルに追加する前 */
		beforeaddlegend: "beforeaddlegend",

		/** 凡例パネルの再描画後 */
		afterredrawlegendpanel: "afterredrawlegendpanel",

		/** 描画レイヤ描画後 */
		afterdraw: "afterdraw",

		/** 登録情報の登録/更新後 */
		successcontentssubmit: "successcontentssubmit",

		/** 登録情報の削除後 */
		successcontentsdelete: "successcontentsdelete",

		/** 地図上クリックのハンドラ実行前 */
		beforemapclick: "beforemapclick",

		/** 地図クリックによる検索 */
		clicksearch: "clicksearch"
	},

	/**
	 * jQueryを使っているかのフラグ
	 * @type {boolean}
	 */
	usejQuery: null,

	/**
	 * SaigaiTaskのコンテキストパス
	 * @type {String}
	 */
	contextPath: null,

	/**
	 * AJAX APIクラス
	 * @type {SaigaiTask.Map.API}
	 */
	api: null,

	/**
	 * SaigaiTaskのコンテキストパスに /map を加えたもの
	 * @type {String}
	 */
	saigaitaskServer: null,

	/**
	 * OpenLayersの投影法のEPSGコード
	 * @type {Number}
	 */
	epsg: 4326,

	/**
	 * OpenLayersにしたい divのID
	 * @type {String}
	 */
	div: null,

	/**
	 * OpenLayers オブジェクト
	 * @type {OpenLayers.Map}
	 */
	map: null,

	/**
	 * コントロールの連想配列
	 * @type {Object.<String, OpenLayers.Control>}
	 */
	controls: null,

	/**
	 * ポップアップ管理クラス
	 * @type {SaigaiTask.Map.control.PopupManager}
	 */
	popupManager: null,

	/**
	 * SaigaiTaskのeコミマップの地図ID
	 * @type {Number}
	 */
	mapId: null,

	/**
	 * 登録情報のWMSレイヤでフィルタするフィーチャIDリストをレイヤIDでマップ
	 * @type {Object<String, Array<Number>}
	 */
	filter: null,

	/**
	 * 登録情報の検索でフィルタするフィーチャIDリストをレイヤIDでマップ
	 * @type {Object<String, Array<Number>}
	 */
	searchFilter: null,

	/**
	 * パネルなどのコンポネーント保存用オブジェクト
	 * @type {Object<String, *>}
	 */
	components: {
		/**
		 * 各パネルを配置するためのメインパネルのコンテナ
		 * @type {SaigaiTask.Map.view.MainPanel}
		 */
		mainpanel: null,

		/**
		 * 登録情報ウィンドウ
		 * @type {SaigaiTask.Map.view.ContentsFormWindow}
		 */
		contentsFormWindow: null
	},

	/**
	 * 凡例ウィンドウ表示フラグ
	 * @type {Boolean}
	 */
	showLegend: false,
	/** 凡例ウィンドウに表示しないレイヤのレイヤID配列 */
	excludesLayerIdsFromLegendWindow: [],

	// click
	clickHandler : null,
	clickBuffer: 10,

	/**
	 * eコミマップ情報配列
	 * @type {Array<SaigaiTask.Map.EcommapInfo>}
	 */
	ecommaps: null,

	// 移動量(px)
	pandx: 100,
	pandy: 50,

	/**
	 * 地図アイコン
	 * @type {SaigaiTask.Map.Icon}
	 */
	icon: null,

	/**
	 * 利用するジオコーダ
	 * support: GOOGLE
	 * @type {String}
	 */
	geocoder: null,

	/**
	 * データ表示時間パラメータ
	 */
	time: null,

	/**
	 * ブラウザリサイズ時に呼ばれるハンドラ
	 * リサイズ後のブラウザサイズに合わせて地図もリサイズする。
	 */
	resize: function() {
		var me = this;
		me.map.updateSize();
	},

	//=====================================================================
	// コンストラクタ
	//=====================================================================
	/**
	 * Initialization Prepare OpenLayers
	 * @param {String} div OpenLayersにしたい divのID
	 * @param {SaigaiTask.Map.model.InitOptions} initOptions 初期化オプション
	 */
	init: function(div, initOptions){

		var me = this;

		// オプション設定
		var options = new SaigaiTask.Map.model.InitOptions();
		SaigaiTask.Map.copy(options, initOptions);
		me.options = options;
		// フィルタ(文字列を数字に変換して保存)
		me.filter = {};
		for(var filterLayerId in options.filterFeatureMap) {
			me.filter[filterLayerId] = [];
			var filterFeatureIds = options.filterFeatureMap[filterLayerId];
			for(var filterFeatureIdsKey in filterFeatureIds) {
				var filterFeatureId = parseInt(filterFeatureIds[filterFeatureIdsKey]);
				me.filter[filterLayerId].push(filterFeatureId);
			}
		}
		// 検索用フィルタも同じ
		SaigaiTask.Map.copy(me.searchFilter, me.filter);

		// ThickBox の iframe かチェックする
		(function(id){
			var iframe = parent.document.getElementById(id);
			if(iframe) {
				// 古いFirefoxだと "setting a property that has only a getter" のようなエラーが出るためtry-catchで回避
				try{
					console = parent.window.console;
					console.log(lang.__("Changed console to the parent of iframe"));
				} catch(e) {
					// do nothing
				}
			}
		})("TB_iframeContent");

		me.events = new OpenLayers.Events(me);
		// イベント定義を追加
		for(var eventtypeIdx in me.EventType) {
			var type = me.EventType[eventtypeIdx];
			me.events.extensions[type] = true;
		}

		// 変数初期化
		me.ecommaps = new Array();
		me.div = div;
		if(options) {
			// APIの設定
			if(options.api!=null) {
				this.api = options.api;
				this.api.stmap = this;
			}
			// アイコンの設定
			if(options.icon!=null) {
				this.icon = options.icon;
			}
			// コンテキストパスの設定
			var contextPath = options.contextPath;
			if(contextPath){
				me.contextPath = contextPath;
				me.saigaitaskServer = contextPath+"/map";
				if(this.api==null) {
					this.api = new SaigaiTask.Map.SaigaiTaskAPI(contextPath);
				}
				if(this.icon==null) {
					this.icon = new SaigaiTask.Map.Icon(contextPath+"/js/SaigaiTaskJS/css");
				}
			}
			// 凡例表示の設定
			if(typeof options.showLegend!='undefined') {
				me.showLegend = options.showLegend;
			}
			// 投影法
			me.epsg = options.epsg;
			// ジオコーダ
			if(options.geocoder!=null) {
				me.geocoder = options.geocoder;
			}
		}
		me.baseLayer = new Object();
		me.usejQuery = (typeof jQuery!='undefined');
		if(!me.usejQuery) {
			console.warn('jQuery was not found.');
		}

		// OpenLayers option
		var mapOptions = {
			// @see https://github.com/openlayers/ol2/issues/1302
			// Drawing problems with WMS (singleTile:true) and jQuery layout (map.updateSize() events) #1302
			// With a single Tile = true WMS layer (exactly a Tile layer)
			// If you call map size update method map.updateSize (), there is a problem that layer = null is passed and the map becomes white at Null portる。
			// In the above information, it seems good to invalidate tileManager, so set it as null
			// * Since the filter layer spatial search range layer is a single tile, it occurred on the filter-added map screen
			tileManager: null,
			controls: [],
			// avoid animated zooming
			transitionEffect: null,
			zoomMethod: null
		};
		// epsg 900913, 3857
		if(me.epsg!=900913 || me.epsg == 3857) {
			if (options){
				if(options.scales) {
					mapOptions.scales = options.scales;
				} else {
					mapOptions.maxResolution = 0.703125;
					mapOptions.numZoomLevels = 22;
				}
			}
		}
		me.map = new OpenLayers.Map(div, mapOptions);
		me.setEpsg(me.epsg);
		me.controls = new Object();
		me.addControl(new OpenLayers.Control.Navigation({
			zoomWheelEnabled: true,
			handleRightClicks: true,
			zoomBoxKeyMask: OpenLayers.Handler.MOD_ALT
		}), me.getNavigationControlKey());
		// OpenLayers.Control.Navigation が内部で自動で登録する OpenLayers.Control.DragPan の
		// OpenLayers.Handler.Drag が mousedown, touchstart などのイベントでデフォルトでpropagation(伝播）しないようになっている。
		// タブレットなどのタッチ操作の場合に touchstart, touchend による擬似clickイベントがハンドラ順序によっては起動されない。
		// clickHandlerが OpenLayers.Handler.Drag よりも後になった場合でもイベントを伝播させるために stopDown を false (changed from Default:true) とする.
		// 他のところで OpenLayers.Handler.Drag を使用する場合は stopDown に注意すること
		me.controls.navigation.dragPan.handler.stopDown = false;
		var panZoom = new OpenLayers.Control.PanZoom();
		SaigaiTask.Map.extendZoomWorld(panZoom);
		me.addControl(panZoom, "panZoom");
		// マウス位置の座標を取得
		if (options && options.coordinateDecimal) {
			me.addControl(new OpenLayers.Control.MousePosition({
				displayProjection: new OpenLayers.Projection("EPSG:4326")
			}), 'mouseposition');
		}
		else {
			me.addControl(new SaigaiTask.Map.control.MousePosition60({
				displayProjection: new OpenLayers.Projection("EPSG:4326")
			}), 'mouseposition');
		}
		// 縮尺バーを表示
		//me.addControl(new OpenLayers.Control.ScaleLine(), "scaleLine");
		me.addControl(new OpenLayers.Control.ScaleBar(), "scaleBar");
		// 中心にカーソルを表示
		var centerCursorControl = new OpenLayers.Control.CenterCursor();
		me.addControl(centerCursorControl);
		centerCursorControl.moveCenter();
		// 帰属先を表示
		me.map.addControl(new OpenLayers.Control.Attribution());
		// 表示状態をセッションに保存
		//var sessionLayout = new SaigaiTask.Map.control.SessionLayout(this);
		//me.addControl(sessionLayout, "sessionLayout");
		// 地図印刷
		var pdfControl = new SaigaiTask.Map.control.PdfControl(this);
		me.addControl(pdfControl, "pdfControl");
		// 複数地図同期
		var syncControl = new SaigaiTask.Map.control.SyncControl(this);
		me.addControl(syncControl, "syncControl");
		// 登録情報の地物の複数選択
		var selectFeatureControl = new SaigaiTask.Map.control.SelectFeatureControl(this);
		me.addControl(selectFeatureControl, "selectFeatureControl");
		// UTMグリッド
		var mgrsControl = new SaigaiTask.Map.control.MgrsControl(this);
		me.addControl(mgrsControl, "mgrsControl");
		// メモ
		var rakugakiControl = new SaigaiTask.Map.control.RakugakiControl(this);
		me.addControl(rakugakiControl, "rakugakiControl");
		// KMLなどのベクタレイヤの地物のクリック用
		var kmlSelectControl = new OpenLayers.Control.SelectFeature([], {
			clickout: true, toggle: false, multiple: false, hover: false
		});
		me.addControl(kmlSelectControl, "kmlSelectControl");

		// 描画の初期化
		if(me.initDraw) me.initDraw();

		// イベントハンドラ
		me.clickHandler = new OpenLayers.Handler.Click(
			me, // control
			{click: me.onMapClick}, // callback
			{single: true, pixelTolerance: 5} // options
		);
		me.clickHandler.activate();

		// WFSProxyの初期化
		OpenLayers.ProxyHost = this.api.url.wfsProxyURL;

		// コンテキストメニューの設定
		me.components.contextmenu = new SaigaiTask.Map.view.ContextMenu(me, options.showMenuOptions);

		// 凡例を表示する
		if(me.showLegend) {
			new SaigaiTask.Map.view.MainPanel(me);
		}
		else {
			$("#"+me.div).width("100%").height("100%");
		}
		
		// レイヤ追加時の共通処理
		me.map.events.on({
			"preaddlayer": function(option) {
				var layer = option.layer;
				// loadingプロパティを追加
				layer.loading = false;
				layer.events.on({
					"loadstart": function() {
						layer.loading = true;
					},
					"loadend": function() {
						layer.loading = false;
						
						// check all layer loading
						var allloadend = true;
						for(var idx in me.map.layers) {
							var l = me.map.layers[idx];
							if(l.loading) {
								allloadend = false;
								break;
							}
						}
						
						if(allloadend) {
							me.events.triggerEvent("alllayerloadend");
						}
					}
				});
			}
		});
	},

	setEpsg: function(epsg, scales) {
		var me = this;
		var mapOptions;
		if (epsg == 900913) {
			mapOptions = {
				maxResolution : 156543.0339,
				numZoomLevels : 22,
				maxExtent : new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
				units : 'm',
				projection : new OpenLayers.Projection("EPSG:900913"),
				displayProjection : new OpenLayers.Projection("EPSG:4326")
			};
		}
		else if (epsg == 3857) {
			mapOptions = {
				maxResolution : 156543.0339,
				numZoomLevels : 22,
				maxExtent : new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
				units : 'm',
				projection : new OpenLayers.Projection("EPSG:3857"),
				displayProjection : new OpenLayers.Projection("EPSG:4326")
			};
		} else {
			mapOptions = {
				maxExtent: new OpenLayers.Bounds(-180,-90,180,90),
				projection: "EPSG:4326"
			};
			if (!!scales) {
				mapOptions.scales = scales;
			} else {
				mapOptions.maxResolution = 0.703125;
				mapOptions.numZoomLevels = 22;
			}
		}
		me.map.setOptions(mapOptions);
		me.epsg = epsg;

		me.events.triggerEvent("epsgchanged");
	},

	/**
	 * マウスポジションの経緯度を取得する.
	 * @return {OpenLayers.LonLat}
	 */
	getMouseLonLat: function() {
		var me = this;
		var lonlat = me.map.getLonLatFromPixel(me.controls['mouseposition'].lastXy);
		return lonlat;
	},

	/**
	 * Reload the layer.
	 */
	reload: function() {
		var me = this;
		var map = me.map;
		var layers = map.layers;
		for(var layersIdx in layers) {
			var layer = layers[layersIdx];
			// ベースレイヤは除く
			if(layer.isBaseLayer) continue;
			layer.redraw(true);
		}
	},

	/**
	 * Load the e-com map with the map ID specified
	 * @param {Long} mapId 地図ID
	 * @param {String} layerId 凡例ウィンドウに表示しないレイヤID（コンマ区切り文字)
	 * @param {String} onlyLayerId 指定したレイヤIDだけ表示（コンマ区切り文字）
	 */
	loadEcommap: function(mapId, layerIds, onlyLayerIds){
		var me = this;

		me.events.triggerEvent(me.EventType.beforeloadecommap);

		var lids = null;
		var olids = null;
		if (layerIds != null) {
			lids = layerIds.split(",");
			for (var i=0; i<lids.length; i=i+1)
				me.excludesLayerIdsFromLegendWindow.push(lids[i]);
		}
		if (onlyLayerIds != null && onlyLayerIds != "null")
			olids = onlyLayerIds.split(",");

		this.mapId = mapId;
		me.api.getEcommapInfo(mapId, function(ecommap) {
			if(ecommap!=null) {
				ecommap.stmap = me;
				ecommap = new SaigaiTask.Map.EcommapInfo(ecommap);
				me.registEcommapInfo(ecommap, lids, olids);
			}
		});
	},

	/**
	 * eコミ情報を地図へ登録
	 * @param {SaigaiTask.Map.EcommapInfo} ecommap eコミ情報
	 * @param {Array<Integer>} lids 凡例ウィンドウに表示しないレイヤID
	 * @param {Array<Integer>} olids 指定レイヤのみ表示ONにする
	 */
	registEcommapInfo: function(ecommap, lids, olids) {
		var me = this;

		// eコミマップ情報を取得
		me.ecommaps.push(ecommap);
		var ecommapIdx = me.ecommaps.length-1;
		ecommap.ecommapIdx = ecommapIdx;

		// epsg 再設定
		me.setEpsg(ecommap.epsg, ecommap.scales);
		// 表示制限範囲設定
		try {
			if(!!ecommap.layoutInfo && !!ecommap.layoutInfo.restrictedExtent) {
				var restrictedExtentArr = ecommap.layoutInfo.restrictedExtent
				if(restrictedExtentArr.length==4) {
					var restrictedExtent = new OpenLayers.Bounds(restrictedExtentArr[0], restrictedExtentArr[1], restrictedExtentArr[2], restrictedExtentArr[3]);
					var degProj = new OpenLayers.Projection("EPSG:4326");
					var maxExtent = new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34).transform( new OpenLayers.Projection("EPSG:900913"), degProj);
					if(restrictedExtent.top    > maxExtent.top)    restrictedExtent.top = maxExtent.top;
					if(restrictedExtent.left   < maxExtent.left)   restrictedExtent.left = maxExtent.left;
					if(restrictedExtent.right  > maxExtent.right)  restrictedExtent.right = maxExtent.right;
					if(restrictedExtent.bottom < maxExtent.bottom) restrictedExtent.bottom = maxExtent.bottom;
					me.map.setOptions({restrictedExtent: restrictedExtent.clone().transform(degProj, new OpenLayers.Projection("EPSG:"+ecommap.epsg))});
				}
			}
		} catch(e) {
			console.error(lang.__("Failed to set display limitation range."), e);
		}

		// ベースレイヤ 初期化
		var baseLayers = ecommap.getBaseLayerInfos();
		me.addBaseLayers(baseLayers);
		// ArcGIS外部地図レイヤ初期化
		var arcgisLayerInfos = ecommap.getArcGISLayerInfos();
		me.addArcGISLayer(arcgisLayerInfos);
		// 主題図（画像）項目レイヤ初期化
		var overlayLayerInfos = ecommap.getOverlayLayerInfos();
		me.addOverlayLayer(overlayLayerInfos);
		// External map layer initialization
		var externalMapLayerInfos = ecommap.getExternalMapLayerInfos();
		me.addExternalMapLayer(externalMapLayerInfos, /*split*/true);
		// 主題図項目レイヤ初期化
		var referenceLayerInfos = ecommap.getReferenceLayerInfos();
		me.addReferenceLayer(referenceLayerInfos, /*split*/true);
		// KMLレイヤ初期化
		var kmlLayerInfos = ecommap.getLayerInfosByTypes([SaigaiTask.Map.Layer.Type.KML]);
		me.addLayers(kmlLayerInfos);
		// コンテンツレイヤ 初期化
		ecommap.contentsLayers = ecommap.getLayerInfosByTypes([SaigaiTask.Map.Layer.Type.LOCAL]);
		var contentsLayers = ecommap.contentsLayers;
		for(var key in contentsLayers) {
			var contentsLayer = contentsLayers[key];
			// 指定レイヤを表示ONにする。（被災状況、対応状況）
			if (olids != null) {
				contentsLayer.visibility = false;
				for (var i=0; i<olids.length; i=i+1) {
					if (olids[i]==contentsLayer.layerId)
						contentsLayer.visibility = true;
				}
			}
			else {
				if (lids!=null) {
					for (var i=0; i<lids.length; i=i+1){
						if (lids[i]==contentsLayer.layerId)
							contentsLayer.visibility = true; // 初期表示
					}
				}
			}
			// 検索フラグを立てる
			contentsLayer.searchable = contentsLayer.visibility;
		}
		me.addContentsLayers(ecommap);

		// Homeボタンクリック時の処理
		me.map.zoomWorld = function() {
			ecommap.moveToHome();
		};
		me.initHome();

		var callback = function(){
			// ポップアップの初期表示
			var popup = null;
			// HTTPリクエストパラメータから取得
			popup = SaigaiTask.Map.util.CommonUtil.getParameter("popup");
			// ポップアップの指定がなければオプションから取得
			if(popup==null) popup = me.options.popupid;
			// ポップアップを表示する
			if(popup!=null){
				var popups = popup.split(",");
				for(var idx in popups) {
					// ポップアップのフィーチャIDを取得する
					var layer = popups[idx].split(".")[0];
					var fid = popups[idx].split(".")[1];
					me.options.pinned = true; // ピン留め
					me.options.center = true; // 地図の中心に表示
					me.getContent(layer,fid, null, null, me.options);
					var layerInfo = me.getLayerInfo(layer);
					if(!!layerInfo) {
						layerInfo.visibility = true;
						layerInfo.searchable = true;
						var layer = layerInfo.getLayer();
						layer.refreshParams();
					}
				}
			}

			var legendcollapsed = null;
			legendcollapsed = SaigaiTask.Map.util.CommonUtil.getParameter("legendCollapsed");
			if(legendcollapsed == "true"){
				me.options.collapsed = true;
			}

			// イベント発火
			me.events.triggerEvent(me.EventType.loadendecommap, {
				ecommap: ecommap
			});
		};

		// 表示状態を復元する
		var sessionLayout = me.controls["sessionLayout"];
		if(!!sessionLayout)sessionLayout.loadLayout(callback, (olids == null));
		else callback();

		// 凡例パネル再描画
		me.components.mainpanel.redrawLegendPanel();
	},

	/**
	 * 表示状態をサーバに保存するイベントの初期化.
	 * @deprected
	 * sessionLayout.activate(); を直接使ってください.
	 */
	initSaveLayout: function(){
		var sessionLayout = this.controls["sessionLayout"];
		sessionLayout.activate();
		sessionLayout.loadLayout();
	},

	initHome: function() {
		var me = this;
		var olmap = me.map;

		// ホーム位置に移動
		olmap.zoomWorld();

		// ホーム位置記憶
		var center = me.getCenter();
		var zoom = me.map.getZoom();
		var homeLocation = {
			center: center,
			zoom: zoom
		};
		me.isHome = function() {
			var center = me.getCenter();
			var zoom = me.map.getZoom();
			if(homeLocation.center.equals(center)==false) return false;
			if(homeLocation.zoom!=zoom) return false;
			return true;
		};

		// 位置情報があれば移動
		var map = this;
		var popup = SaigaiTask.Map.util.CommonUtil.getParameter("popup");
		if(popup==null) {
			// パラメータに指定があれば中心位置を移動する
			var initCenter = SaigaiTask.PageURL.getInitCenter();
			if(initCenter!=null) {
				//initCenter.transform(new OpenLayers.Projection("EPSG:4326"), map.map.getProjectionObject());
				//map.map.setCenter(initCenter);
				map.setCenter(initCenter);
			}
			// パラメータに指定があればズームする
			var initZoom = SaigaiTask.PageURL.getInitZoom();
			if(isFinite(initZoom) && 0<initZoom) {
				//  ズームが効かないので遅延させる
				//setTimeout(function() {
					map.map.zoomTo(initZoom);
				//}, 1000);
			}
		}
	},

	onMapClick: function(evt){
		var me = this;

		// onmapclick before
		me.events.triggerEvent(me.EventType.beforemapclick);

		// ポップアップを閉じる
		if(me.popupManager!=null) {
			me.popupManager.closeAll();
		}

		// フォーカスを外す
		// ※メモ描画でテキストメモ編集モードのテキストエリアを閉じるために必要
		// 　@see src/main/webapp/js/SaigaiTaskJS/lib/SaigaiTask/Map/Layer/DrawLayerSelectDrag.js _startTextEdit
		if(!!document.activeElement && !!document.activeElement.blur) document.activeElement.blur();

/*
		// SelectFeature の clickFeature が呼ばれた場合、onMapClick は呼ばれないため不要
		var kmlSelectControl = me.controls["kmlSelectControl"];
		//KMLレイヤのFeatureがクリック位置にあるかチェック
		if (kmlSelectControl) {
			var selectedLayers = kmlSelectControl.layers;
			if (selectedLayers) {
				for (var i=0; i<selectedLayers.length; i++) {
					if (selectedLayers[i] && selectedLayers[i].getFeatureFromEvent(evt)) {
						alert("click!");
					}
				}
			}
		}
*/

		var lb = this.map.getLonLatFromPixel(new OpenLayers.Pixel(evt.xy.x-this.clickBuffer, evt.xy.y+this.clickBuffer));
		var rt = this.map.getLonLatFromPixel(new OpenLayers.Pixel(evt.xy.x+this.clickBuffer, evt.xy.y-this.clickBuffer));
		if (this.map.displayProjection) {
			lb.transform(this.map.getProjectionObject(), this.map.displayProjection);
			rt.transform(this.map.getProjectionObject(), this.map.displayProjection);
		}
		var bbox = [lb.lon,lb.lat,rt.lon,rt.lat];

		this.clickSearch(new OpenLayers.LonLat((lb.lon+rt.lon)/2, (lb.lat+rt.lat)/2), bbox, /*pinned*/false, {
			evt: evt
		});
		$("#txtLon").val(lb.lon);
		$("#txtLat").val(lb.lat);
	},

	/**
	 * @param {OpenLayers.LonLat} center
	 * @param {Array<Number>} bbox
	 * @param {Boolean} pinned ポップアップのピン留め
	 * @param {Object} option
	 * @param {Event} option.evt ハンドラが受け取ったイベント
	 */
	clickSearch: function(center, bbox, pinned, option){

		var me = this;

		// 他のポップアップを閉じる
		if(me.popupManager!=null) {
			me.popupManager.closeAll();
		}

		// 検索対象のレイヤIDを取得
		var layerIds = me.getSearchableLayerIds();
		if(layerIds.length==0){
			// 外部参照レイヤを検索
			var refcontentsPopup = new SaigaiTask.Map.view.RefContentsPopup(me);
			refcontentsPopup.getReferenceFeatureInfo(center, bbox);
			return;
		}

		// SLDのruleパラメータを取得する
		var rule = "";
		for(var idx in layerIds) {
			var layerId = layerIds[idx];
			// 登録情報レイヤのSLDルールの表示パラメータ付与
			var contentsLayerInfo = me.getLayerInfo(layerId);
			if(typeof contentsLayerInfo.params.rule=="string"
				&& 0<contentsLayerInfo.params.rule.length) {
				rule += (0<rule.length?",":"") + contentsLayerInfo.params.rule;
			}
		}

		// cidを取得する（SLDの取得で必要になるので）
		var cid = null;
		var ecommaps = me.ecommaps;
		for( var ecommapsKey in ecommaps ) {
			// レイヤが表示されているか調べる
			var ecommap = ecommaps[ecommapsKey];
			var mapInfo = ecommap.mapInfo;
			if(mapInfo.mapId==me.mapId) {
				cid = mapInfo.communityId;
				break;
			}
		}

		// フィルタするレイヤ数に応じて検索結果取得数を増やす
		var limit = 10;
		var filter = me.searchFilter;
		var filterLength = SaigaiTask.Map.util.CommonUtil.length(filter);
		limit += filterLength * 5;

		// カーソルをbusyに変更
		var mapDiv = $("#"+me.div);
		mapDiv.css("cursor", "wait");

		me.searchContentsByBbox(bbox, {
			cid: cid,
			layerIds: layerIds,
			limit: limit,
			async: true,
			rule: rule,
			success: function(result) {
				// フィルタが指定されているレイヤは指定フィーチャIDのみ検索結果に残す
				if(0<filterLength) {
					var featureInfos = result[1];
					var filteredResult = [];
					for(var featureInfosKey in featureInfos) {
						// 取得データのレイヤIDとフィーチャIDを得る
						var featureInfo = featureInfos[featureInfosKey];
						var layerId = featureInfo[0];
						var featureId = featureInfo[1];
						// フィルタに存在するかチェックする
						var isFilter = false;
						var filterFeatureIds = filter[layerId];
						if(typeof filterFeatureIds!="undefined") {
							isFilter = jQuery.inArray(featureId, filterFeatureIds) == -1;
						}
						// フィルタしないものは残す
						if(isFilter==false) {
							filteredResult.push(featureInfo);
						}
					}
					result[1] = filteredResult;
				}

				// 結果数を減らす
				result[1] = result[1].slice(0, 10);

				// イベント発火
				if(!option) option = {}; // なければ作成
				option.executePopup = true;
				var args = {
					layerIds: layerIds,
					center: center,
					bbox: bbox,
					option: option,
					result: result,
					executePopup: true
				}
				me.events.triggerEvent(me.EventType.clicksearch, args);

				// ポップアップはイベントからも制御できる
				if(args.executePopup) {
					// 結果をポップアップする
					if (result[1].length == 1) {
						//一つの場合は、コンテンツを再取得してポップアップ
						me.getContent(result[1][0][0], result[1][0][1], center, bbox, {
							pinned: pinned
						});
					} else if (result[1].length > 1) {
						//一覧をポップアップ
						var listPopup = new SaigaiTask.Map.view.ListPopup(me);
						listPopup.show(center, bbox, result, pinned);
					} else {
						// 外部参照レイヤを検索
						var refcontentsPopup = new SaigaiTask.Map.view.RefContentsPopup(me);
						refcontentsPopup.getReferenceFeatureInfo(center, bbox);
					}
				};

				mapDiv.css("cursor", "default");

			},
			error: function() {
				mapDiv.css("cursor", "default");
			}
		});


	},

	/**
	 * 矩形で登録情報を検索します.
	 * @param {Array<Number>} bbox WGS84の矩形配列 [SW.x, SW.y, NE.x, NE.y]
	 * @param {Object<String, Object>} options オプション
	 * @param {Array<String>} options.mapId 検索対象のマップID (default 表示中のマップID)
	 * @param {Array<String>} options.layerIds レイヤIDの配列 (default 表示中のレイヤID)
	 * @param {Number} options.limit 取得数の上限 (default 0)
	 * @param {Boolean} options.async AJAXの同期・非同期通信 (default false)
	 * @param {String} options.rule SLDの表示ルールパラメータ
	 * @return {Object} result(同期通信の場合のみ)
	 * {Number} result[0] 検索結果情報の配列
	 * {Number} result[0][0] total
	 * {Number} result[0][1] limit
	 * {Number} result[0][2] offset
	 * {Array.<Array>}} result[1] 検索されたフィーチャ情報の配列
	 * {Array.<Object>} result[1][#] フィーチャ情報配列
	 * {String} result[1][#][0] レイヤID
	 * {String} result[1][#][1] フィーチャID
	 * {Array.<Number>} result[1][#][2] ジオメトリ情報配列
	 * {Number} result[1][#][2][0] 中心位置のx座標
	 * {Number} result[1][#][2][1] 中心位置のy座標
	 * {Number} result[1][#][2][2] 矩形からの距離[m]
	 * {Array.<Object>} result[1][#][3] 属性情報配列
	 * {Array.<Object>} result[1][#][4] ファイル情報配列
	 * {Array.<Object>} result[1][#][5] メタ情報配列
	 */
	searchContentsByBbox: function(bbox, options) {

		var me = this;

		// デフォルトオプションの生成
		var defaultOptions = {
			cid: null,
			mapId: me.mapId,
			layerIds: me.getVisibleLayerIds(),
			limit: 0,
			async: false,
			rule: ""
		};

		// 未指定のオプションをデフォルトで上書きする
		var op = {};
		Ext.applyIf(op, options);
		Ext.applyIf(op, defaultOptions);
		console.log("op");
		console.log(op);

		return me.api.searchContentsByBbox(bbox, op.cid, op.mapId, op.layerIds, op.rule, op.limit, op.async, op.success);
	},

	/**
	 * 避難所情報をサーバから検索します.
	 * @param {SaigaiTask.Map.model.ShelterSearchOptions} options
	 */
	searchShelter: function(options) {
		var me = this;
		var result = null;
		var url = me.contextPath + me.url.searchShelterURL;
		options._csrf = SaigaiTask.ajaxcsrfToken;
		Ext.Ajax.request({
			url: url,
			method: "GET",
			params: options,
			async: false,
			success: function(response) {
				result = JSON.parse(response.responseText);
			}
		});
		return result;
	},

	/**
	 * 指定住所をジオコードして取得できれば移動します.
	 * @param {String} address 住所文字列
	 */
	moveAddress: function(address) {
		var me = this;

		var defer = $.Deferred();

		me.geocode(address)
		.done(function(results, status) {
			var result = results[0];
            var point = result.geometry.location;
			//console.log("Google Results : ");
			//console.log(results[0]);
			//TODO : 取得した住所のtype種別に応じてzoomLevelを調整する？
			var lonlat = new OpenLayers.LonLat(point.lng(),point.lat());
			// WGS84から地図の投影法に変換
			lonlat = lonlat.transform(new OpenLayers.Projection("EPSG:4326"), me.map.getProjectionObject());
			if(!!me.map.restrictedExtent) {
				if(me.map.restrictedExtent.containsLonLat(lonlat)==false) {
					alert(lang.__("Failed to move due to out of range."));
					defer.reject(result);
					return;
				}
			}
			me.map.setCenter(lonlat, 15);

			defer.resolve(result);
		});

		return defer.promise();
	},

	/**
	 * 引数に応じてジオコーディング・逆ジオコーディングします。
	 * 将来、ジオコーダを切り替えるためにこれを利用します.
	 * @param {OpenLayers.Geometry.Point} opt ジオコーディング
	 * @param {OpenLayers.LonLat} opt ジオコーディング
	 * @param {String} opt 逆ジオコーディング
	 * @return {jQuery.Deffered}
	 */
	geocode: function(opt) {
		var me = this;
		if(me.geocoder!=null) {
			if(me.geocoder.toUpperCase()=="GOOGLE") {
				return me.geocodeGoogle(opt);
			}
		}

		// otherwise
		alert(lang.__("No geocoder to be available."));
		var defer = $.Deferred();
		defer.reject();
		return defer.promise();
	},

	/**
	 * Googleジオコーダーを使ってジオコーディング・逆ジオコーディングします.
	 * @return {jQuery.Deffered}
	 */
	geocodeGoogle: function(opt) {
		// バージョンチェック
		var isV3 = false;
		try{ isV3 = google.maps.version.indexOf("3.")==0; } catch(e) {alert(lang.__("Google Map JavaScript API v3 has not been loaded."));}

		// Google Maps JavaScript API v3
		var defer = $.Deferred();
		geocoder = new google.maps.Geocoder();

		// parse opt, build option
		var option = {};
		switch(typeof opt) {
		// 文字列の場合は逆ジオコーディングとする
		case "string":
			option.address = opt;
			break;
		case "object":
			if(!!opt.CLASS_NAME) {
				switch(opt.CLASS_NAME) {
				case "OpenLayers.Geometry.Point":
					var point = opt;
					var lat = point.y;
					var lng = point.x;
					option.latLng = new google.maps.LatLng(lat, lng);
					break;
				case "OpenLayers.LonLat":
					var lonlat = opt;
					var lat = lonlat.lat;
					var lng = lonlat.lon;
					option.latLng = new google.maps.LatLng(lat, lng);
					break;
				}
			}
			break;
		}
		// execute
		geocoder.geocode(option, function(results, status) {
			if(status==google.maps.GeocoderStatus.OK) {
				defer.resolve(results, status);
			}
			else {
				defer.reject(results, status);
			}
		});

		return defer.promise();
	},

	/**
	 * 逆ジオコーディング結果を整形した住所文字列に変換します.
	 * @param {Object} result 逆ジオコーディング結果
	 * @param {Object} formatOption 整形オプション
	 */
	getFormattedAddress: function(result, formatOption) {
		var addr = result.formatted_address;

		// 日本を削除
		addr = addr.replace(/^日本, /,'');

		// 郵便番号を削除
		addr = addr.replace(/^〒\d\d\d-\d\d\d\d /,'');

		// 県名を取得
		var pref = null;
		var addressComponentsIdx = result.address_components.length-1;
		for(;0<=addressComponentsIdx; addressComponentsIdx--) {
			var component = result.address_components[addressComponentsIdx];
			if(component.types[0]=="administrative_area_level_1") {
				pref = component.long_name;
			}
		}
		// 県名以下の住所情報がある場合は県名を削除
		if(pref!=null) {
			var replacedAddr = addr.replace(pref, "");
			if(replacedAddr.length!=0) {
				addr = replacedAddr;
			}
		}
		return addr;
	},

	/**
	 * 登録情報をフィーチャIDを指定してポップアップ表示します.
	 * @param {String} layer レイヤID
	 * @param {Number} fid フィーチャID
	 * @param {OpenLayers.LonLat} center クリック位置(optional)
	 * @param {Array<Number>} bbox クリック位置の矩形範囲(optional)
	 * @param {Object} options そのほかオプション(optional)
	 */
	getContent: function(layer, fid, center, bbox, options) {
		// 引数チェック
		if(typeof layer=="undefined" || layer==null || layer=="") {
			console.warn(lang.__("Layer ID has not been specified."));
			return;
		}
		if(isNaN(parseFloat(fid)) || isFinite(fid)==false) {
			console.warn(lang.__("Feature ID is not specified in numeric."));
			return;
		}
		// 登録情報を取得
		var me = this;
		me.api.getContent(me.mapId, layer, fid, center, bbox, function(data) {
			if(data==null) {
				return;
			}
			else if(typeof data=="error"){
				console.warn("error!",args);
			}
			else{
				var contentsPopup = new SaigaiTask.Map.view.ContentsPopup(me);
				contentsPopup.show(layer, fid, data, center, bbox, options);
			}
		});
	},

	/**
	 * @param url URL
	 * @param title 画像タイトル
	 * @param maxw 最大幅
	 * @param maxh 最大高
	 */
	createImg: function(url, title, maxw, maxh) {
		var me = this;
		// ロード中の画像を生成
		var extImg = {
			xtype: 'image',
			src: me.saigaitaskServer+'/images/loading.gif',
			style: {
				width: '32px',
				height: '32px'
			}
		};
		var extImgObj = Ext.create('Ext.Img', extImg);

		// 画像を読み込み
		var img = new Image();
		//var imgResized = false;
		img.onload = function(){
			// DOMに要素ができるまで処理しない
			if (!extImgObj.getEl()) {
				setTimeout(img.onload, 100);
				return;
			}
			imgResized = true;
			// 画像をリサイズする
			var w = img.width;
			var h = img.height;
			if(typeof img.naturalWidth!='undefined') { // for Firefox, Safari, Chrome
				w = img.naturalWidth;
				h = img.naturalHeight;
			}
			// 最大サイズより大きい場合は縮小する
			var rate = Math.min(maxw/w, maxh/h);
			if(rate<1) {
				w = Math.round(w*rate);
				h = Math.round(h*rate);
			}
			// 変更がある場合のみ更新する(毎回更新するとafterlayoutイベントが発生して無限ループになる)
			if(w!=extImgObj.getWidth()) extImgObj.setWidth(w);
			if(h!=extImgObj.getHeight()) extImgObj.setHeight(h);
			if(extImgObj.src!=url) {
				extImgObj.setSrc(url);
			}
			// タイトルの設定
			if(typeof title!="undefined"&&title!=null) {
				extImgObj.getEl().set({title: title});
			}

			// 画像クリック時にライトボックス風に表示する
			extImgObj.getEl().dom.onclick = function() {
				var img = this;
				FalUtil.showImageWindow(img.src);
			};

			// TODO: ポップアップの画像を縦に中央揃えにする
		};
		img.src = url;

		window.extImgObj = extImgObj;

		return extImgObj;
	},

	/**
	 * Add WFS layer for registration information
	 * @param cid
	 * @param mid
	 * @param layerId
	 * @param fids
	 * @param options
	 * @param options.styleMap
	 * @return {OpenLayers.Layer.Vector} WFS layer
	 */
	addContentsWFSLayer: function(cid,mid,layerId,fids,options){
		// デフォルトオプションの生成
		var defaultOptions = {
			styleMap: null
		};

		// 未指定のオプションをデフォルトで上書きする
		var op = {};
		Ext.applyIf(op, options);
		Ext.applyIf(op, defaultOptions);

		var me = this;
		var layerInfo = me.getLayerInfo(layerId);
		var ecommap = me.ecommaps[0];
		var url = ecommap.ecommapURL+"/wfs?";
		if(ecommap.wfsURL!=null) url = ecommap.wfsURL;
		url += "cid="+cid;
		var featureType = layerId;
		var featureNS = "";

		// filter を設定する
		if(!fids) fids = [];
		var filter = null;
		if(fids!=null) {
			new OpenLayers.Filter.FeatureId({ fids: fids });
		}
		if(layerInfo.timeSeriesType==SaigaiTask.Map.Layer.TimeSeriesType.HISTORY) {
			//filter
			var time = SaigaiTask.PageURL.getTime();
			if(!time) time = new Date();
			var iso8601Time = new Date(time).toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			filter = new OpenLayers.Filter.Logical({
				type: OpenLayers.Filter.Logical.AND,
				filters: [ new OpenLayers.Filter.Comparison({
					type: OpenLayers.Filter.Comparison.LESS_THAN_OR_EQUAL_TO,
					property: "time_from",
					value: iso8601Time
				}), new OpenLayers.Filter.Comparison({
					type: OpenLayers.Filter.Comparison.GREATER_THAN_OR_EQUAL_TO,
					property: "time_to",
					value: iso8601Time
				})]
			});
		}

		// styleMap
		var styleMap = op.styleMap;
		if(styleMap==null) {
			var sldXML = this.getSLD(mid, layerId);
			var sldFormat = new OpenLayers.Format.SLD();
			var sld = sldFormat.read(sldXML);
			for(var l in sld.namedLayers){
				var styles = sld.namedLayers[l].userStyles;
				styleMap = styles[0];
			}
		}
		return this.addWFSLayer(url, featureType, featureNS, filter, styleMap);
	},

	/**
	 * WFSレイヤを地図に追加する。
	 * @param url
	 * @param featureType
	 * @param featureNS
	 * @param filter
	 * @return {OpenLayers.Layer.Vector} WFSレイヤ
	 */
	addWFSLayer: function(url, featureType, featureNS, filter, styleMap){
		var protocol = new OpenLayers.Protocol.WFS({
			url: url,
			featureType: featureType,
			featureNS: featureNS,
			srsName: "EPSG:" + this.epsg,
			srsNameInQuery: "EPSG:" + this.epsg,
			outputFormat: "json", // For XML, GeoServer cannot parse infinity and raises an exception, requesting JSON format
			readFormat: new OpenLayers.Format.GeoJSON()
		});
		// Test once if you can connect
		var response = protocol.read({
			maxFeatures: 100,
			callback: function(response) {
				console.log("test wfs response");
				console.log(response);
				console.log(response.error);
				if(-1 < response.priv.responseText.indexOf("<servlet-exception>")) {
					console.error(lang.__("Failed to get WFS.")+url + "\n" + response.priv.responseText);
				}
			}
		})
		var wfs = new OpenLayers.Layer.Vector("WFS", {
	        strategies: [new OpenLayers.Strategy.BBOX()],
			protocol: protocol,
			filter: filter,
			styleMap: styleMap
		});
		this.map.addLayer(wfs);
		return wfs;
	},

	/**
	 * 複数のコンテンツレイヤを登録する
	 * @param ecommap eコミマップ情報
	 */
	addContentsLayers: function(ecommap){
		return this.addContentsLayer(ecommap);
	},

	/**
	 * Register the content layer with a new layer.
	 * @param ecommap eコミマップ情報
	 */
	addContentsLayer: function(ecommap){
		var contentsLayerInfo = ecommap.contentsLayerInfo;
		if(contentsLayerInfo==null) {
			return null;
		}
		// フィルタ対象レイヤがあるかどうかチェック
		var hasFilterLayer = false;
		for(var idx=ecommap.contentsLayerInfos.length-1; 0<=idx; idx--) {
			var layerInfo = ecommap.contentsLayerInfos[idx];
			if(layerInfo.filterkey!=null) {
				hasFilterLayer = true;
				break;
			}
		}
		// フィルタ対象レイヤがある場合はWMSレイヤを１つずつわける
		if(hasFilterLayer) {
			// 試しにレイヤ情報１つにつきレイヤを１つ作成する
			for(var idx=ecommap.contentsLayerInfos.length-1; 0<=idx; idx--) {
				var layerInfo = ecommap.contentsLayerInfos[idx];
				var contentsLayerInfo = ecommap.createContentsLayerInfo();
				contentsLayerInfo.appendChildLayerInfo(layerInfo);
				var isFilterLayer = layerInfo.filterkey!=null;
				// フィルタ対象レイヤの場合
				if(isFilterLayer) {
					// パラメータを設定
					contentsLayerInfo.params.filterlayer = layerInfo.layerId;
					contentsLayerInfo.params.filterkey = layerInfo.filterkey;
					contentsLayerInfo.params.grayout = layerInfo.grayout;
					// 空間検索範囲レイヤがあれば追加する
					if(layerInfo.hasSpatialLayer()) {
						layerInfo.createSpatialLayer();
					}
				}
				var contentsLayer = new SaigaiTask.Map.Layer.WMSLayer(contentsLayerInfo);
				this.map.addLayer(contentsLayer);

				console.log("contensLayer.params");
				console.log(contentsLayer.params);

				// 自動リロード
				var reload = ecommap.mapInfo.reload;
				if(0<reload) {
					var timerId = setInterval(function(){
						contentsLayer.redraw(true);
					}, reload*1000);
					ecommap.mapInfo.reloadkTimerId = timerId;
				}
			}
			return;
		}
		// Normally include all layers in one WMS layer
		var contentsLayer = new SaigaiTask.Map.Layer.WMSLayer(contentsLayerInfo);
		this.map.addLayer(contentsLayer);

		console.log("contensLayer.params");
		console.log(contentsLayer.params);

		// 自動リロード
		var reload = ecommap.mapInfo.reload;
		if(0<reload) {
			var timerId = setInterval(function(){
				contentsLayer.redraw(true);
			}, reload*1000);
			ecommap.mapInfo.reloadkTimerId = timerId;
		}

		return contentsLayer;
	},

	/**
	 * Get WMS URL and layer display properties of registration information layer
	 * @deprecated もう使用しないがFeatureIdのフィルタは移設する必要がある？
	 * @param ecommapIdx eコミマップ情報インデックス
	 * @return オブジェクト ret
	 */
	getContentsWmsURLInfo: function(ecommapIdx){
		console.log("getContentsWmsURL");
		var ecommap = this.ecommaps[ecommapIdx];
		var mapInfo = ecommap.mapInfo;
		var contentsLayers = ecommap.contentsLayers;
		var layerAuthKeys = ecommap.layerAuthKeyMap;

		// 変数初期化
		var mapCid = mapInfo.communityId;
		var mapId = mapInfo.mapId;
		var layers="",keys="";
		var layerArray=[], keyArray=[];

		// 各レイヤの情報を取得する
		var visible = true;
		var layerId = null, layerAuthKey = null;
		for(var key in contentsLayers){
			console.log("contentsLayers["+key+"]");
			var contentsLayer = contentsLayers[key];
			layerId = contentsLayer.layerId;
			//var layerName = contentsLayer.name;
			//var contentsAttr = new Array(0);
			layerAuthKey = layerAuthKeys[layerId];
			console.log("visible: "+visible);
			console.log("layerId: "+layerId);
			console.log("layerAuthKey: "+layerAuthKey);
			if( layerId!=null && layerAuthKey!=null && contentsLayer.visibility==true ) {
				if(jQuery.inArray(layerId, layerArray)==-1) {
					layerArray.push(layerId);
					keyArray.push(layerAuthKey);
				}
			}
		}

		if(0<layerArray.length) {
			layers = layerArray.join(",");
			keys = keyArray.join(",");
		}

		if(layers.length==0){
			layers = layerId;
			keys = layerAuthKey;
			visible = false;
		}

		// wms URL
		var server = this.saigaitaskServer;
		var wmsBaseURL = server+this.url.wmsAuthURL;
		// フィーチャIDフィルタ
		var featureId = "";
		if(this.filter){
			var first = true;
			var filterLayerId = new Array();
			for(var layerId in this.filter){
				// レイヤIDが指定されていなければ次へ
				if(!layerId) continue;
				var featureIdList = this.filter[layerId];
				if(featureIdList){
					for(var idx in featureIdList){
						var fid = featureIdList[idx];
						if(first)first=false;
						else featureId += ",";
						featureId += layerId+"."+fid;
						filterLayerId.push(layerId);
					}
				}
			}
			// フィルタがあれば、レイヤは最低限のものだけにする
			if(0<filterLayerId.length){
				layers = filterLayerId.join(',');
				keys = new Array();
				for(var idx in filterLayerId){
					keys.push( layerAuthKeys[filterLayerId[idx]] );
				}
				keys = keys.join(',');
			}
		}
		//var wmsURL = wmsBaseURL+"?cid="+mapCid+"&mid="+mapId+"&keys="+keys+"&featureId="+featureId+"&";
		var wmsURL = wmsBaseURL+"?cid="+mapCid+"&mid="+mapId+"&featureId="+featureId+"&";

		var ret = {
				wmsURL: wmsURL,
				layers: layers,
				visibility: visible
		};
		console.log("ret");
		console.log(ret);
		return ret;
	},

	/**
	 * SLDファイルをhttpで取得します.
	 * @param mid
	 * @param layerId
	 * @return {String} SLD文字列
	 */
	getSLD: function(mid, layerId){
		return this.api.getSld(mid, layerId);
	},

	/**
	 * レイヤIDでレイヤ情報をマップ.
	 */
	getLayerMap: function(){
		var me = this;
		var layers = me.getAllLayers();
		var map = {};
		for(var idx in layers) {
			var layer = layers[idx];
			map[layer.layerId] = layer;
		}
		return map;
	},

	/**
	 * すべてのレイヤ情報を取得する.
	 * @returns {Array}
	 */
	getAllLayers: function(){
		var me = this;
		var ecommap = me.ecommaps[0];
		 return ecommap.baseLayers.concat(ecommap.contentsLayers).concat(ecommap.overlayLayers)
		 .concat(ecommap.referenceLayers);
	},

	redrawLayer: function(obj) {
		var me = this;
		if(typeof obj=="object") {
			if(!!obj.CLASS_NAME) {
				if(obj.CLASS_NAME=="SaigaiTask.Map.Layer.WMSLayer") {
					var layer = obj;
					if(layer!=null && typeof layer.refreshParams=="function") {
						// refreshParams でセットされる Visibility は、LayerInfo を見ているが、
						// 空間検索範囲の LayerInfo は元の方になっていて、
						// 場合によっては反転するためもとの状態を保持して再設定する
						var visibility = layer.getVisibility();

						// 再読み込み
						success |= layer.refreshParams({
							nocache: true
						});

						layer.setVisibility(visibility);
					}
					return success;
				}
				if(obj.CLASS_NAME=="SaigaiTask.Map.Layer.LayerInfo") {
					var success = false;
					var layerInfo = obj;
					var layers = []
					var layer = layerInfo.getLayer();
					if(layer!=null) {
						me.redrawContentsLayer(layer);
						layer = layerInfo.spatialLayer;
						if(layer!=null) {
							me.redrawContentsLayer(layer);
						}
					}
				}
			}
		}
		return false;
	},

	/**
	 * 登録情報レイヤを再描画する
	 * @deprecated
	 * @param ecommapIdx eコミマップ情報インデックス
	 * @return 再描画した場合は true
	 */
	redrawContentsLayer: function(obj){
		var me = this;

		if($.isNumeric(obj)) {
			var ecommapIdx = obj;
			var ecommap = me.ecommaps[ecommapIdx];
			var contentsLayerInfo = ecommap.contentsLayerInfo;
			if(contentsLayerInfo!=null) {
				var layer = contentsLayerInfo.getLayer();
				var reloaded = me.redrawContentsLayer(contentsLayerInfo);
				// リロードできなかった場合は、子レイヤのリロードを行う
				if(!reloaded) {
					for(var idx in contentsLayerInfo.children) {
						var child = contentsLayerInfo.children[idx];
						me.redrawContentsLayer(child);
					}
				}
			}
		}
		else if(typeof obj=="object") {
			return me.redrawLayer(obj);
		}
		return false;
	},

	addBaseLayers: function(baseLayers){
		for( var key in baseLayers )
			this.addBaseLayer(baseLayers[key]);
	},

	/**
	 *
	 * @param layerInfo ベースレイヤ情報
	 */
	addBaseLayer: function(layerInfo){
		if( !layerInfo ) return;

		layerInfo = new SaigaiTask.Map.Layer.LayerInfo(layerInfo);

		layer = SaigaiTask.Map.Layer.newLayerFromLayerInfo(layerInfo);
		if(layer instanceof SaigaiTask.Map.Layer.GoogleLayer) {
			if(typeof google == "undefined") {
				alert(lang.__("Set geocoder setting to Google's when to user Google Map."));
				return;
			}
		}


		// ベースレイヤを追加
		this.map.addLayer(layer);

		// レイヤの切り替え
		if(layerInfo.visibility) {
			this.map.setBaseLayer(layer);
		}

		this.baseLayer[layerInfo.layerId] = layer;
	},

	/**
	 * 登録情報レイヤで表示中のレイヤのIDを配列で取得します.
	 * @return {Array.<String>} 表示しているレイヤがない場合は空の配列
	 */
	getVisibleLayerIds: function() {
		var me = this;
		var layerIds = [];
		var ecommaps = me.ecommaps;
		for( var ecommapsKey in ecommaps ) {
			// レイヤが表示されているか調べる
			var ecommap = ecommaps[ecommapsKey];
			var contentsLayers = ecommap.contentsLayers;
			if(contentsLayers){
				for(var contentsLayersKey in contentsLayers){
					var contentsLayer = contentsLayers[contentsLayersKey];
					var layerId = contentsLayer.layerId;
					var visible = contentsLayer.visibility;
					if(visible){
						layerIds.push(layerId);
					}
				}
			}
		}
		return layerIds;
	},

	/**
	 * 検索対象のレイヤIDを取得します.
	 * @return {Array.<String>} 検索対象のレイヤがない場合は空の配列
	 */
	getSearchableLayerIds: function() {
		var me = this;
		var layerIds = [];
		var ecommaps = me.ecommaps;
		for( var ecommapsKey in ecommaps ) {
			// レイヤが表示されているか調べる
			var ecommap = ecommaps[ecommapsKey];
			var contentsLayers = ecommap.contentsLayers;
			if(contentsLayers){
				for(var contentsLayersKey in contentsLayers){
					var contentsLayer = contentsLayers[contentsLayersKey];
					var layerId = contentsLayer.layerId;
					if(contentsLayer.alwaysNotSearch==false && contentsLayer.searchable){
						layerIds.push(layerId);
					}
				}
			}
		}
		return layerIds;
	},

	/**
	 * レイヤIDからレイヤ情報を取得します.
	 * @param {String} layerId
	 * @return layerInfo
	 */
	getLayerInfo: function(layerId) {
		var layerInfo = null;
		var ecommaps = this.ecommaps;
		for( var ecommapsKey in ecommaps ) {
			var ecommap = ecommaps[ecommapsKey];
			var contentsLayers = ecommap.contentsLayers;
			for( var contentsLayersKey in contentsLayers ) {
				var contentsLayer = contentsLayers[contentsLayersKey];
				if( contentsLayer.layerId==layerId ) {
					layerInfo = contentsLayer;
					return layerInfo;
				}
			}
		}
		return null;
	},

	/**
	 *
	 * @param {Array<SaigaiTask.Map.Layer.LayerInfo>} レイヤ情報配列
	 */
	addLayers: function(layerInfos) {
		var me = this;
		for(var key in layerInfos) {
			var info = layerInfos[key];
			me.addLayer(info);
		}
	},
	/**
	 * レイヤを追加する
	 * @param {Object} obj OpenLayers.Layer または SaigaiTask.Map.Layer.LayerInfo
	 */
	addLayer: function(obj) {
		var me = this;

		switch(obj.CLASS_NAME) {
		case "SaigaiTask.Map.Layer.LayerInfo":
			var layer = SaigaiTask.Map.Layer.newLayerFromLayerInfo(obj);
			// KMLLayer は OpenLayers.Control.SelectFeature に追加するため、addLayerの必要なし
			if(layer.CLASS_NAME!="SaigaiTask.Map.Layer.KMLLayer"
				/* 汎用レイヤグループは、addLayerの必要なし */
				&& layer.CLASS_NAME!="SaigaiTask.Map.Layer.Group") {
				me.map.addLayer(layer);
			}
			break;
		default:
			if(obj instanceof OpenLayers.Layer) {
				me.map.addLayer(obj);
			}
			break;
		}
	},

	/**
	 * 主題図項目レイヤ情報配列からレイヤを作成して追加します.
	 * @param referenceWMSLayerInfos 主題図項目レイヤ情報配列
	 * @param split false:１つのレイヤとして追加、true:子レイヤそれぞれで追加
	 */
	addReferenceLayer: function(referenceLayerInfos, split) {
		var me = this;
		if(!!!split) {
			this.addLayers(referenceLayerInfos);
		}
		else {
			var layerInfos = referenceLayerInfos;
			for(var key in layerInfos) {
				var info = layerInfos[key];
				var childLayerInfos = info.children;
				
				for(var key2 in childLayerInfos) {
					var childLayerInfo = childLayerInfos[key2];
					childLayerInfo.wmsURL = info.wmsURL;
					me.addLayer(childLayerInfo);
				}
			}
		}
	},

	/**
	 * 主題図（画像）項目レイヤ情報配列からレイヤを作成して追加します.
	 * @param overlayLayerInfos 主題図（画像）項目レイヤ情報配列
	 */
	addOverlayLayer: function(_overlayLayerInfos) {
		var overlayLayerInfos = [].concat(_overlayLayerInfos).reverse();
		for(var key in overlayLayerInfos) {
			var info = overlayLayerInfos[key];

			// レイヤを追加
			var layer = null;
			if (info.type == SaigaiTask.Map.Layer.Type.OVERLAY_XYZ)
				layer = new SaigaiTask.Map.Layer.XYZLayer(info);
			else
				layer = new SaigaiTask.Map.Layer.WMSLayer(info);
			this.map.addLayer(layer);
		}
	},

	/**
	 * 外部地図(ArcGIS)レイヤ情報配列からレイヤを作成して追加します.
	 * @param externalMapWMSLayerInfos 外部地図レイヤ情報配列
	 */
	addArcGISLayer: function(arcgisLayerInfos) {
		var me = this;
		var layers = [];
		for(var key in arcgisLayerInfos) {
			var info = arcgisLayerInfos[key];
			var layer = SaigaiTask.Map.Layer.newLayerFromLayerInfo(info);
			layers.push(layer);
		}

		// 先に追加したものが下になるので、逆順にする
		layers.reverse();
		me.map.addLayers(layers);
	},

	/**
	 * Create a layer from the external map layer information array and add it.
	 * @param externalMapWMSLayerInfos External map layer information array
	 * @param split false: Add as one layer, true: Add in each child layer
	 */
	addExternalMapLayer: function(externalMapLayerInfos, split) {
		var me = this;
		var layers = [];
		for(var key in externalMapLayerInfos) {
			var info = externalMapLayerInfos[key];
			if(!!!split || info.type==SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_XYZ) {
				var layer = SaigaiTask.Map.Layer.newLayerFromLayerInfo(info);
				layers.push(layer);
			}
			else {
				var childLayerInfos = info.children;
				for(var key2 in childLayerInfos) {
					var childLayerInfo = childLayerInfos[key2];
					childLayerInfo.wmsURL = info.wmsURL;
					var layer = SaigaiTask.Map.Layer.newLayerFromLayerInfo(childLayerInfo);
					layers.push(layer);
				}
			}
		}
		
		// 先に追加したものが下になるので、逆順にする
		layers.reverse();
		me.map.addLayers(layers);
	},

	replaceHref: function(str) {
		return str.replace(/(https?\:\/\/[^\s|<]+)/ig, '<a href="#" onclick="openWin(\'$1\'); return false;">$1</a>');
	},

	//Layer Scale
	/** レイヤの標準スケールを返却 設定されていない場合は0 */
	getLayerScale: function(layerId) {
		var layerInfo = this.getLayerInfo(layerId);
		if (layerInfo) return layerInfo.scale;
		return 10000;
	},

	/** 縮尺に応じたズームレベルを返却 */
	getLayerScaleZoom: function(layerId){
		var layerScale = this.getLayerScale(layerId);
		if (layerScale == 0) return this.contentsZoomLevel;
		return this.map.getZoomForResolution(OpenLayers.Util.getResolutionFromScale(layerScale, this.map.baseLayer.units), true);
	},

	getFilterFeatureIdsByForm: function(formId){
		// フィルターを更新する
		var responseText = SaigaiTask.Map.util.jQueryUtil.submitForm('#'+formId);
		var json = eval("("+responseText+")");
		this.filter = json;
		console.log(json);

		// 登録情報をすべて再描画する
		for(var idx=0; idx<this.ecommaps.length; idx++){
			this.redrawContentsLayer(idx);
		}
	},

	/**
	 * 描画用ツールバーを表示します.
	 * @param {Object} toolbarOptions
	 * @return {Ext.toolbar.Toolbar} ツールバー
	 */
	initDrawToolbar: function(toolbarOptions) {
		var me = this;
		return new SaigaiTask.Map.view.DrawToolbar(me, toolbarOptions);
	},

	readFeatureGeometryValue: function(selector){
		var wkt = jQuery(selector).val();
		var features = null;
		if(typeof wkt!="undefined"){
			var wktFormat = new OpenLayers.Format.WKT();
			features = wktFormat.read(wkt);
		}
		return features;
	},

	/**
	 * jQueryを使います。
	 * フィーチャの値を指定クラスに書き込む
	 * @param feature
	 */
	writeFeatureGeometoryValue: function(feature){
		if(!feature) return;
		var me = this;
		var wkt = me.getWKT(feature);
		var featureId = feature.fid;
		switch(feature.geometry.CLASS_NAME){
		case "OpenLayers.Geometry.Point":
		case "OpenLayers.Geometry.LineString":
		case "OpenLayers.Geometry.Polygon": // 四角形も含む
		default:
			// 値を書き込む
			if(me.usejQuery){
				var c = "."+featureId;
				var jqueryObj = jQuery(c);
				jqueryObj.val(wkt);
			}
			break;
		}
	},

	/**
	 * フィーチャをWKTで取得します.
	 * @param feature フィーチャ
	 * @returns wkt なければ null
	 */
	getWKT: function(feature) {
		if(!feature) return;
		feature = feature.clone();
		var geometry = feature.geometry;
		var wkt = null;
		switch(geometry.CLASS_NAME){
		case "OpenLayers.Geometry.Point":
		case "OpenLayers.Geometry.LineString":
		case "OpenLayers.Geometry.Polygon": // 四角形も含む
		default:
			feature.geometry.transform(new OpenLayers.Projection("EPSG:"+this.epsg), new OpenLayers.Projection("EPSG:4326"));
			var wktFormat = new OpenLayers.Format.WKT();
			wkt = wktFormat.write(feature);
			break;
		}
		return wkt;
	},

	//=====================================================================
	//イベントの自動バインド
	//=====================================================================
	/**
	 * jQuery を使ってクリックイベントを指定のクラスに追加する
	 */
	bindMapEvent: function(){
		if(!this.usejQuery)return;
		// jQuery の初期化関数でイベントをバインドする
		var m = this;
		jQuery(document).ready(function(){
			var mClass = m.div;
			// zoom in
			var zoomInClass = "."+mClass+"ZoomIn";
			console.log("zoomInClass: "+zoomInClass);
			var zoomInElems = jQuery(zoomInClass);
			zoomInElems.each(function(){
				jQuery(this).click(function(){
					m.zoomIn();
				});
			});
			// zoom out
			var zoomOutClass = "."+mClass+"ZoomOut";
			console.log("zoomOutClass: "+zoomOutClass);
			var zoomOutElems = jQuery(zoomOutClass);
			zoomOutElems.each(function(){
				jQuery(this).click(function(){
					m.zoomOut();
				});
			});
			// 選択
			var navigationClass = "."+mClass+"Navigation";
			console.log("navigationClass: "+navigationClass);
			navigationElems = jQuery(navigationClass);
			navigationElems.each(function(){
				jQuery(this).click(function(){
					m.setNavigationControlActivation(true);
				});
			});
			// 移動
			var moveClass = "."+mClass+"Move";
			console.log("moveClass: "+moveClass);
			moveElems = jQuery(moveClass);
			moveElems.each(function(){
				jQuery(this).click(function(){
					m.setDragPanControlActivation(true);
				});
			});

		});
	},

	//=====================================================================
	//OpenLayersラッパーメソッド
	//=====================================================================

	/** control **/
	/**
	 * コントローラをOpenLayersに追加する
	 * キーがあれば連想配列に保存する
	 * @param control コントローラ
	 * @param key 連想配列のキー
	 */
	addControl: function(control, key) {
		if(typeof key!='undefined'){
			this.controls[key]=control;
		}
		return this.map.addControl(control);
	},

	setControlActivation: function(control,activation) {
		if(activation)return control.activate();
		else return control.deactivate();
	},

	getNavigationControlKey: function() {
		return "navigation";
	},

	/**
	 * マウス操作コントロールをすべて解除して選択コントロールにする
	 * マウスカーソルの変更にjQueryを使います。
	 */
	deactivateMouseControl: function() {
		this.setNavigationControlActivation(false); // 選択
		this.setDragPanControlActivation(false); // 移動
		this.clickHandler.deactivate(); // クリック時のポップアップ
		// 地図ポップアップ無効時にKMLのポップアップも無効にする
		var kmlSelectControl = this.controls.kmlSelectControl;
		if(kmlSelectControl.active) {
			kmlSelectControl.deactivate();
		}

		// マウスカーソル
		if(this.usejQuery){
			jQuery("#"+this.div).css("cursor","default");
		}
	},

	/**
	 * 選択
	 */
	setNavigationControlActivation: function(activation) {
		var key = this.getNavigationControlKey();
		var control = this.controls[key];
		if(!control)return;
		if(typeof activation=="undefined"){
			// 指定がなければ反転する
			activation = !control.active;
		}

		if(activation){
			this.deactivateMouseControl();
			this.clickHandler.activate();
			// clickHandler の後に kmlSelectControl を有効にしないと、
			// KMLフィーチャクリックとclickHandlerの両方のイベントが発生するので注意
			var kmlSelectControl = this.controls.kmlSelectControl;
			// KMLSelectControlがすでに有効になっている場合は一旦無効にして再度有効化する
			if(kmlSelectControl.active) {
				kmlSelectControl.deactivate();
			}
			kmlSelectControl.activate();
		}

		return this.setControlActivation(control, activation);
	},

	/**
	 * 移動
	 * マウスカーソルの変更にjQueryを使います。
	 */
	setDragPanControlActivation: function(activation) {
		var key = this.getNavigationControlKey();
		var control = this.controls[key];
		if(!control)return;
		if(typeof activation=="undefined"){
			// 指定がなければ反転する
			activation = !control.active;
		}

		if(activation){
			this.deactivateMouseControl();
			this.clickHandler.deactivate();
			// マウスカーソル
			if(this.usejQuery){
				jQuery("#"+this.div).css("cursor","move");
			}
		}
		return this.setControlActivation(control, activation);
	},

	/**
	 * 中心位置をWGS84で取得します.
	 * @return {OpenLayers.LonLat} EPSG:4326のLonLat
	 */
	getCenter: function() {
		var center = this.map.getCenter();
		if(center==null) return null;
		return center.transform(this.map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));
	},

	/**
	 * @param {OpenLayers.LonLat} EPSG:4326のLonLat
	 */
	setCenter: function(lonlat, zoomLevel) {
		lonlat = lonlat.transform(new OpenLayers.Projection("EPSG:4326"), this.map.getProjectionObject());

		//popup表示の場合にのみlevelを15で表示
		if(SaigaiTask.Map.util.CommonUtil.getParameter("popup")!=null || this.options.popupid!=null){
			zoomLevel = 15;//とりあえず15に固定
			return this.map.setCenter(lonlat, zoomLevel);
		}else{
			return this.map.setCenter(lonlat, zoomLevel);
		}
	},

	// pan
	pan: function(dx,dy,options) {
		return this.map.pan(dx,dy,options);
	},
	panLeft: function() {
		return this.pan(-this.pandx,0);
	},
	panTop: function() {
		return this.pan(0,-this.pandy);
	},
	panRight: function() {
		return this.pan(this.pandx,0);
	},
	panBottom: function() {
		return this.pan(0,this.pandy);
	},
	panTR: function() {
		return this.pan(this.pandx,-this.pandy);
	},
	panTL: function() {
		return this.pan(-this.pandx,-this.pandy);
	},
	panBR: function() {
		return this.pan(this.pandx,this.pandy);
	},
	panBL: function() {
		return this.pan(-this.pandx,this.pandy);
	},
	// zoom
	zoomIn: function() {
		return this.map.zoomIn();
	},
	zoomOut: function() {
		return this.map.zoomOut();
	},
	zoomTo: function(zoom) {
		return this.map.zoomTo(zoom);
	},
	/**
	 *
	 * @param {OpenLayers.Bounds} bounds EPSG:4326のbounds
	 * @param closest
	 */
	zoomToExtent: function(bounds,closest) {
		// 投影法の変換
		bounds = bounds.clone().transform(new OpenLayers.Projection("EPSG:4326"), this.map.getProjectionObject());
		// zoomToExtent
		if(this.map.baseLayer) {
			return this.map.zoomToExtent(bounds,closest);
		}
		return null;
	},
	getLayer: function(idx) {
		var layers = this.map.layers;
		var layer = layers[idx];
		return layer;
	},

	// layer visibility
	setLayerVisibility: function(idx,visibility) {
		var layer = this.getLayer(idx);
		layer.setVisibility(visibility);
	},
	getLayerVisibility: function(idx) {
		var layer = this.getLayer(idx);
		return layer.getVisibility();
	},
	toggleLayerVisibility: function(idx) {
		var visibility = ! this.getLayerVisibility(idx);
		this.setLayerVisibility(idx,visibility);
	},
	/**
	 * 指定レイヤを最前面に表示する
	 * @param {OpenLayers.Layer.Layer} layer
	 */
	toFront: function(layer) {
		var me = this;
		var layers = me.map.layers, layersIdx=null, l, newZIndex=0;
		for(layersIdx in layers) {
			l = layers[layersIdx];
			if(l.id==layer.id){
				newZIndex = Math.max(Number(l.getZIndex()), newZIndex);
			}
			else {
				newZIndex = Math.max(Number(l.getZIndex())+1, newZIndex);
			}
		}
		layer.setZIndex(newZIndex);
	},
	/**
	 * ベースレイヤを切り替える
	 * @param 切り替えたいベースレイヤのID
	 */
	setBaseLayerById: function(layerId) {
		if(layerId){
			var layer = this.baseLayer[layerId];
			return this.setBaseLayer(layer);
		}
	},
	/**
	 * ベースレイヤを切り替える
	 * @param 切り替えたいベースレイヤ
	 */
	setBaseLayer: function(layer) {
		if(layer){
			// visibility を更新
			var ecommap = this.ecommaps[0];
			var baseLayerInfos = ecommap.getBaseLayerInfos();
			for(var idx in baseLayerInfos) {
				var baseLayerInfo = baseLayerInfos[idx];
				baseLayerInfo.visibility = (layer.layerInfo.layerId==baseLayerInfo.layerId);
			}
			//this.map.baseLayer.layerInfo.visibility = false;
			//layer.layerInfo.visibility = true;
			return this.map.setBaseLayer(layer);
		}
	},
	/**
	 * コンテンツレイヤ情報を取得する
	 * @param ecommapIdx eコミマップ情報インデックス
	 * @param layerId 登録情報レイヤID
	 * @return 登録情報レイヤの情報
	 */
	findContentsLayerInfo: function(ecommapIdx, layerId) {
		var ecommap = this.ecommaps[ecommapIdx];
		var contentsLayers = ecommap.contentsLayers;
		for(var key in contentsLayers){
			var layer = contentsLayers[key];
			if(layer.layerId==layerId) return layer;
		}
		return null;
	},
	/**
	 * レイヤの検索対象フラグを設定します.
	 * @param {String} layerId
	 * @param {Boolean} searchable 検索に含めるかどうか
	 */
	setLayerSearchable: function(layerId, searchable) {
		var me = this;
		var layerInfo = me.getLayerInfo(layerId);
		if(layerInfo!=null) {
			layerInfo.searchable = searchable;
		}
	},

	/** <span class="ja">度単位の解像度を地図の解像度に変更 </span><span class="en">Convert map resolution to degree unit</span>*/
	toMapResolution: function(reso) {
		if (this.epsg == 4326) return reso;
		return reso * OpenLayers.INCHES_PER_UNIT.dd/OpenLayers.INCHES_PER_UNIT[this.map.units];
	},

	////////////////////////////////////////////////////////////////
	/** 地図のdivをfocusして地図外のfocusを解除 */
	focus : function()
	{
		this.map.div.tabIndex = 0;
		this.map.div.focus();
	},
	/** 地図がfocusされているか */
	isFocus : function()
	{
		return document.activeElement == this.map.div;
	}

};
