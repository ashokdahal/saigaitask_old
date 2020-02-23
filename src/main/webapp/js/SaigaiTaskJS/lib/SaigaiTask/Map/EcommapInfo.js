/**
 * eコミマップ情報
 * @requires SaigaiTask/Map.js
 */
SaigaiTask.Map.EcommapInfo = new OpenLayers.Class({

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * eコミのURL
	 */
	ecommapURL: null,

	/**
	 * WMSのURL
	 * 指定しなければ自動生成します.
	 * @type {String}
	 */
	wmsAuthURL: null,

	/**
	 * 登録情報レイヤのルートレイヤ情報.
	 */
	contentsLayerInfo: null,

	/**
	 * 登録情報レイヤのレイヤ情報配列.
	 * @type {Array<SaigaiTask.Map.LayerInfo>}
	 */
	contentsLayerInfos: null,

	/**
	 * レイヤIDをキーにしてレイヤ情報を保存します.
	 * @type {Object<String, SaigaiTask.Map.Layer.LayerInfo>}
	 */
	layerInfoStore: {},

	/**
	 * レイヤ情報をツリー構造で保存します.
	 */
	layerInfoTree: [],

	initialize: function(options) {
		var me = this;
		// オプションをコピー
		OpenLayers.Util.extend(this, options);
		var ecommap = this;
		// レイヤ情報ストアを作成
		var layerInfos = [].concat(
			ecommap.contentsLayers,
			ecommap.groupContentsLayers,
			ecommap.kmlLayers,
			ecommap.referenceLayers,
			ecommap.overlayLayers,
			ecommap.externalMapLayers,
			ecommap.baseLayers
		);
		ecommap.layerInfoStore = {};
		for(var key in layerInfos) {
			var layerInfo = layerInfos[key];
			if(typeof layerInfo=="undefined") continue;
			layerInfo.ecommap = ecommap;
			layerInfo = new SaigaiTask.Map.Layer.LayerInfo(layerInfo);
			ecommap.layerInfoStore[layerInfo.layerId] = layerInfo;
			switch(layerInfo.type) {
			case SaigaiTask.Map.Layer.Type.LOCAL:
				layerInfo.params.LAYERS = layerInfo.layerId;
				layerInfo.params.keys = layerInfo.authkey;
				break;
			case SaigaiTask.Map.Layer.Type.OVERLAY_WMS:
			case SaigaiTask.Map.Layer.Type.OVERLAY_WMS_SINGLE:
			case SaigaiTask.Map.Layer.Type.OVERLAY_TILED:
			case SaigaiTask.Map.Layer.Type.OVERLAY_TILECACHE:
			case SaigaiTask.Map.Layer.Type.OVERLAY_OSM:
				layerInfo.params.LAYERS = [layerInfo.featuretypeId];
				break;
			case SaigaiTask.Map.Layer.Type.REFERENCE_WMS:
			case SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS:
				layerInfo.params.LAYERS = [];
				break;
			case SaigaiTask.Map.Layer.Type.REFERENCE:
			case SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS_LAYERS:
			case SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_ARCGIS_LAYERS:
				layerInfo.params.LAYERS = layerInfo.featuretypeId;
				break;
			}
		}
		// 登録情報レイヤのレイヤ情報を作成
		var contentsLayerInfo = me.createContentsLayerInfo();
		// MapLayerInfoでレイヤの表示順を設定
		ecommap.layerInfoTree = [];
		ecommap.contentsLayerInfos = [];
		var mapLayerInfos = ecommap.mapInfo.MapLayerInfo;
		for(var mapLayerInfosIdx in mapLayerInfos) {
			var mapLayerInfo = mapLayerInfos[mapLayerInfosIdx];
			var layerId = mapLayerInfo.layerId;
			var layerInfo = ecommap.layerInfoStore[layerId];
			if(typeof layerInfo =="undefined") {
				continue;
			}
			layerInfo.opacity = mapLayerInfo.opacity;
			// layer_closedから値を取得する
			if(typeof layerInfo.expanded == null) {
				layerInfo.expanded = ! mapLayerInfo.closed;
			}
			if(!!mapLayerInfo.reload) layerInfo.reload = mapLayerInfo.reload;
			// 親レイヤIDを取得
			var parent = null;
			if(layerInfo.parent != null) {
				parent = layerInfo.parent;
				if(typeof parent=="object") {
					parent = parent.layerId;
				}
			}
			// 親がいれば親レイヤの下に追加
			var append = false;
			if(parent!=null) {
				var parentLayerInfo = ecommap.layerInfoStore[parent];
				if(typeof parentLayerInfo=="object") {
					parentLayerInfo.appendChildLayerInfo(layerInfo);
					append = true;
				}
			}
			// 親がいない場合
			if(append==false){
				// 登録情報の場合
				if(layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL||
					layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL_GROUP) {
					contentsLayerInfo.appendChildLayerInfo(layerInfo);
					ecommap.contentsLayerInfos.push(layerInfo);
				}
				else {
					// ルートノードをlayerInfoTreeに保存
					ecommap.layerInfoTree.push(layerInfo);
					console.log("add layerInfoTree "+layerInfo.name);
				}
			}
		}

		// 外部地図のレイヤ情報を作成
		if(typeof ecommap.externalMapLayers!="undefined") {
			for (var i  =  0; i < ecommap.externalMapLayers.length; i++) {
				layerInfo = ecommap.layerInfoStore[ecommap.externalMapLayers[i].layerId];
				// 親がいれば親レイヤの下に追加
				var append = false;
				if (layerInfo.parent != null) {
					var parent = ecommap.layerInfoStore[layerInfo.parent];
					if(typeof parent=="object") {
						parent.appendChildLayerInfo(layerInfo);
						//layerInfo.expanded = false;
						append = true;
					}
				}
				// 親がいない場合
				if(append==false) {
					// ルートノードをlayerInfoTreeに保存
					ecommap.layerInfoTree.push(layerInfo);
				}
			}
		}

		// 登録情報レイヤがあれば先頭に追加する
		if(0<contentsLayerInfo.children.length) {
			ecommap.layerInfoTree.unshift(contentsLayerInfo);
			me.contentsLayerInfo = contentsLayerInfo;
		}

		// 背景地図の初期表示設定
		var baselayerInfos = me.getBaseLayerInfos();
		if(0<baselayerInfos.length) {
			// 初期表示 true のものが存在するかチェック
			var exist = false;
			for(var idx in baselayerInfos) {
				var baselayerInfo = baselayerInfos[idx];
				if(baselayerInfo.visibility) {
					exist = true;
					break;
				}
			}
			// なければ最後のものを初期表示する
			if(exist==false) {
				baselayerInfos[baselayerInfos.length-1].visibility = true;
			}
		}
	},

	createContentsLayerInfo: function() {
		return new SaigaiTask.Map.Layer.LayerInfo({
			name: lang.__("Registration info"),
			wmsURL: this.wmsAuthURL,
			visibility: true,
			_olSalt: Math.random(), //キャッシュ不使用パラメータキー生成
			tiled: true,
			tilesOrigin: "0,0",
			params: {
				cid: this.mapInfo.communityId,
				mid: this.mapInfo.mapId,
				FORMAT: "image/png",
				TRANSPARENT: true,
				LAYERS: [],
				keys: []
			}
		});
	},

	isBaseLayerInfo: function(layerInfo) {
		return SaigaiTask.Map.Layer.Type.isBaseLayerType(layerInfo.type);
	},

	/**
	 * ベースレイヤのレイヤ情報を配列で取得します.
	 * @returns {Array<SaigaiTask.Map.Layer.LayerInfo>}
	 */
	getBaseLayerInfos: function() {
		var types = SaigaiTask.Map.Layer.Type.getBaseLayerTypes();
		return this.getLayerInfosByTypes(types);
	},

	getOverlayLayerInfos: function() {
		var types = SaigaiTask.Map.Layer.Type.getOverlayLayerTypes();
		return this.getLayerInfosByTypes(types);
	},

	getReferenceLayerTypes: function() {
		return [SaigaiTask.Map.Layer.Type.REFERENCE_WMS];
	},

	getReferenceLayerInfos: function() {
		var types = this.getReferenceLayerTypes();
		return this.getLayerInfosByTypes(types);
	},

	getExternalMapLayerTypes: function() {
		return [SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS, SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_XYZ];
	},

	getExternalMapLayerInfos: function() {
		var types = this.getExternalMapLayerTypes();
		return this.getLayerInfosByTypes(types);
	},

	getArcGISLayerInfos: function() {
		var types = SaigaiTask.Map.Layer.Type.getArcGISLayerTypes();
		return this.getLayerInfosByTypes(types);
	},

	/**
	 * レイヤ種別指定でレイヤ情報を配列で取得します.
	 * @param types レイヤ種別の配列
	 * @returns {Array<SaigaiTask.Map.Layer.LayerInfo>} レイヤ情報の配列
	 */
	getLayerInfosByTypes: function(types) {
		var layerInfos = [];
		// MapLayerInfo でソートする
		var ecommap = this;
		var mapLayerInfos = ecommap.mapInfo.MapLayerInfo;
		var checked = [];
		for(var mapLayerInfosIdx in mapLayerInfos) {
			var mapLayerInfo = mapLayerInfos[mapLayerInfosIdx];
			var layerId = mapLayerInfo.layerId;
			checked.push(layerId);
			var layerInfo = ecommap.layerInfoStore[layerId];
			if(typeof layerInfo =="undefined") {
				continue;
			}
			if($.inArray(layerInfo.type, types)!=-1) {
				layerInfos.push(layerInfo);
			}
		}
		// 外部地図などeコミに未登録レイヤの対応
		for(var layerId in ecommap.layerInfoStore) {
			if($.inArray(layerId, checked)!=-1) continue;
			var layerInfo = ecommap.layerInfoStore[layerId];
			if($.inArray(layerInfo.type, types)!=-1) {
				layerInfos.push(layerInfo);
			}
		}
		return layerInfos;
	},

	/**
	 * 非表示レイヤIDを配列で取得する
	 * @return {Array<String>} 非表示レイヤID配列
	 */
	getHiddenLayerIds: function() {
		var me = this;
		var tree = me.layerInfoTree;
		var hiddenLayerIds = [];
		for(var idx in tree) {
			var layerInfo = tree[idx];
			layerInfo.getHiddenLayerIds(hiddenLayerIds);
		}
		return hiddenLayerIds;
	},

	/**
	 * このeコミマップ情報を複製します.
	 */
	clone: function(stmap) {
		// eコミ情報をまるごとコピーする
		var ecommap = this;
		var copyWithFunc = $.extend(true, {}, ecommap);
		var options = {};
		for(var name in copyWithFunc) {
			var prop = copyWithFunc[name];
			// 関数以外をコピー
			if(typeof prop!="function") {
				options[name] = prop;
			}
		}

		// 地図オブジェクトを設定
		options.stmap = stmap;

		return new SaigaiTask.Map.EcommapInfo(options);
	},

	/**
	 * 空間検索範囲レイヤの検索条件を更新します.
	 * @param {Object} conditionValue
	 * @returns
	 */
	updateSpatialLayerCondition: function(conditionValue) {
		var me = this;
		var layerId = conditionValue.layerId;
		// レイヤ情報の取得
		var layerInfo = me.layerInfoStore[layerId];
		if(typeof layerInfo=="undefined" || layerInfo==null) {
			alert(lang.__("Retrieval object layer is not displayed."));
			return;
		}
		// 検索条件をレイヤ情報にコピー
		layerInfo.spatialLayers = conditionValue.spatiallayer;
		var spatialLayer = layerInfo.spatialLayer;
		if(typeof spatialLayer=="undefined" || spatialLayer==null) {
			// TODO: 範囲レイヤがなければ作成する
			//layerInfo.createSpatialLayer()
			alert(lang.__("There is no search range layer."));
			return;
		}
		// 検索範囲レイヤのパラメータを更新
		var spatialLayerInfo = spatialLayer.layerInfo;
		spatialLayerInfo.params.spatiallayers = JSON.stringify(layerInfo.spatialLayers);
		spatialLayer._refreshParams({
			nocache: true
		});

	},

	moveToHome: function() {
		var me = this;
		var ecommap = me;
		var stmap = me.stmap;

		var layoutInfo = ecommap.layoutInfo;
		var initExtent = null;
		if( layoutInfo ) {
			var e = layoutInfo.mapExtent;
			if(e){
				initExtent = new OpenLayers.Bounds(e[0],e[1],e[2],e[3]); // wsen
			}
		}
		if(initExtent==null) initExtent = new OpenLayers.Bounds(120.20,22.93,151.35,46.78);
		me.initExtent = initExtent;
		// 範囲で表示
		if(layoutInfo.mapResolution==0) {
			stmap.zoomToExtent(initExtent);
		}
		// 範囲よりも解像度で表示
		else {
			var initResolution = layoutInfo.mapResolution;
			var zoom = stmap.map.getZoomForResolution(stmap.toMapResolution(initResolution), stmap);
			stmap.setCenter(initExtent.getCenterLonLat(), zoom);
		}

		// 凡例別の表示切替のリセット処理
		var legendpanel = stmap.components.mainpanel.legend;
		for(var idx in me.contentsLayerInfos) {
			var contentsLayerInfo = me.contentsLayerInfos[idx];
			var legendrules = contentsLayerInfo.legendrules;
			if(!!legendrules) {
				for(var ruleIdx in legendrules) {
					var rule = legendrules[ruleIdx];
					var node = rule.node;
					if(!!node) {
						// 親のチェック状態でリセット
						var nodeChecked = node.get("checked");
						var parentChecked = node.parentNode.get("checked");
						if(nodeChecked!=parentChecked) {
							node.set("checked", parentChecked);
							legendpanel.tree.fireEvent("checkchange", node, parentChecked, {});
						}
					}
				}
			}
		}
	}
});
