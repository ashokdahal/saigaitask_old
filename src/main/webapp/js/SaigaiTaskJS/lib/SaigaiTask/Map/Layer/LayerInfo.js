/**
 * 
 * @requires SaigaiTask/Map/Layer.js
 */
SaigaiTask.Map.Layer.LayerInfo = new OpenLayers.Class({

	/**
	 * eコミマップ情報
	 * @type {SaigaiTask.Map.EcommapInfo}
	 */
	ecommap: null,

	/**
	 * レイヤID
	 * 葉ノードのみ指定します.
	 * @type {String}
	 */
	layerId: null,

	/**
	 * 名称
	 * すべてのノードで指定します.
	 * @type {String}
	 */
	name: null,

	/**
	 * WMSのURL
	 * ルートノードのみ指定します.
	 * @type {String}
	 */
	wmsURL: null,

	/**
	 * WMS画像のフォーマットを指定
	 */
	format: "image/png",

	/**
	 * WMS画像の透過度
	 */
	opacity: 1.0,

	/**
	 * リロード間隔秒数
	 * MapLayerInfoから設定される。
	 * @type {Object}
	 */
	reload: null,

	/**
	 * レイヤ種別
	 * @type {String} SaigaiTask.Map.Layer.Type の中から指定される
	 */
	type: null,

	/**
	 * 新規登録可能フラグ
	 * @type {Boolean}
	 */
	addable: false,

	/**
	 * ポップアップ編集フラグ
	 * @type {Boolean}
	 */
	editable: false,

	/**
	 * 編集時にスナップ可能フラグ
	 * @type {Boolean}
	 */
	snappable: false,

	/**
	 * 折りたたみフラグ
	 * すべてのノードで指定します.
	 * @type {Boolean}
	 */
	expanded: true,

	/**
	 * 表示フラグ
	 * すべてのノードで指定します.
	 * @type {Boolean}
	 */
	visibility: true,

	/**
	 * 検索フラグ
	 * 地図クリック時に検索するかどうかのフラグ
	 */
	searchable: false,

	/**
	 * 常に検索しないフラグ
	 * 地図クリック時に検索するかどうかのフラグ
	 */
	alwaysNotSearch: false,

	/**
	 * WMSのリクエストに付加するパラメータ
	 */
	params: null,
	
	/**
	 * 親レイヤID
	 * ルートノード以外指定します.
	 * @type {String}
	 */
	parentLayerId: null,

	/**
	 * 親レイヤ情報
	 * 親レイヤIDからレイヤ情報を設定します.
	 * @type {SaigaiTask.Map.Layer.LayerInfo}
	 */
	parent: null,

	/**
	 * 子レイヤ情報の配列
	 * 子レイヤ情報の親レイヤ設定のタイミングで親レイヤ側で追加します.
	 * @type {Array<SaigaiTask.Map.Layer.LayerInfo>}
	 */
	children: null,

	/**
	 * レイヤ
	 * @type {OpenLayers.Layer}
	 */
	layer: null,

	/**
	 * 属性情報配列
	 * @type {Array<SaigaiTask.Map.Layer.AttrInfo>}
	 */
	attrInfos: null,

	/**
	 * The transition effect to use when the map is zoomed.
	 * ズーム時のエフェクト.
	 */
	transitionEffect: null,

	/**
	 * フィルタキー
	 * @type {String}
	 */
	filterkey: null,

	/**
	 * グレーアウト(かつ不透明度)
	 * 0だと非表示、1だとそのまま
	 * 0.5は半透明
	 * @type {Number}
	 */
	grayout: null,

	/**
	 * 空間検索範囲レイヤ
	 * @type {SaigaiTask.Map.Layer.WMSLayer}
	 */
	spatialLayer: null,

	/**
	 * 検索範囲に利用するレイヤ条件のJSONオブジェクト配列
	 * @type {Array<Object>}
	 */
	spatialLayers: null,

	/**
	 * WMSをタイルで取得するかどうかのフラグ
	 * @type {Boolean}
	 */
	singleTile: false,

	/**
	 * 切り出しレイヤID
	 * @type {String}
	 */
	intersectionlayerid: null,

	/**
	 * 切り出しレイヤ名
	 * @type {String}
	 */
	intersectionlayername: null,

	/**
	 * 最終更新日時の属性ID
	 * @type {String}
	 */
	updatecolumn: null,

	/**
	 * 震度レイヤフラグ
	 * @type {Boolean}
	 */
	isEarthquakeLayer: false,

	/**
	 * レイヤ個別の時間パラメータ（ISO8601日時文字列）
	 * @type {String}
	 */
	time: null,

	/**
	 * データ表示時間パラメータを取得
	 * @return ISO8601日付文字列（指定がなければ null)
	 */
	getTime: function() {
		var timeStr = this.time;
		return (!!timeStr ? new Date(timeStr) : null);
	},

	/**
	 * コンストラクタ
	 * @param options
	 */
	initialize: function(options) {
		var me = this;
		me.children = [];
		me.params = [];
		// オプションをコピー
		OpenLayers.Util.extend(me, options);

		me.attrInfos = [];
		for(var idx in options.attrInfos) {
			var attrInfo = new SaigaiTask.Map.Layer.AttrInfo(options.attrInfos[idx]);
			me.attrInfos.push(attrInfo);
		}

		// もし、指定がなければ、検索フラグは表示フラグを使う
		if(typeof options.searchable == "undefined" || options.searchable == null) {
			this.searchable = this.visibility;
		}
		// searchable=false で初期化したら、常に検索しない
		else if(options.searchable==false) {
			this.alwaysNotSearch = true;
		}

		// 時間パラメータがあれば SaigaiTask.PageURL からロード
		me.time = SaigaiTask.PageURL.getLayerTime(me.layerId);

		// 凡例別の表示切替対応
		// 初期の visibility をパラメータに反映する
		me.updateRuleParam();
	},

	/**
	 * OpenLayers.Layerを取得します.
	 * 持っていなければ親を辿って探します.
	 * @type {OpenLayers.Layer}
	 */
	getLayer: function() {
		if(this.layer!=null) {
			return this.layer;
		}
		if(this.parent!=null) {
			return this.parent.getLayer();
		}
		return null;
	},

	/**
	 * 子レイヤ情報を追加します.
	 * @param childLayerInfo
	 */
	appendChildLayerInfo: function(childLayerInfo) {
		this.children.push(childLayerInfo);
		childLayerInfo.parent = this;
		childLayerInfo.parentLayerId = this.layerId;
	},

	/**
	 * WMSリクエストのパラメータに指定したパラメータをマージします.
	 */
	mergeParams: function(params) {
		var layerInfo = this;
		for(var paramName in layerInfo.params) {
			var param = layerInfo.params[paramName];
			if(typeof params[paramName]=="undefined") {
				params[paramName] = [];
			}
			// spatiallayer が string のため上書きにする
			if(typeof params[paramName]=="string") {
				params[paramName] = param;
			}
			// LAYERS などの複数のLayerInfoから構成されるパラメータは配列でマージする
			if($.isArray(params[paramName])) {
				params[paramName].push(param);
			}
		}
		// 時間パラメータの個別指定対応
		if(layerInfo.time!=null) {
			// 登録情報レイヤの場合
			if(layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL) {
				if(typeof params.layertimes=="undefined") params.layertimes = "";
				if(0<params.layertimes.length) params.layertimes += ",";
				params.layertimes += layerInfo.layerId;
				params.layertimes += ",";
				var time = layerInfo.getTime();
				var iso8601Time = time.toISOString();
				// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
				if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
					iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
				}
				params.layertimes += iso8601Time;
			}
		}
	},

	/**
	 * 属性IDから属性情報を取得します.
	 */
	getAttrInfo: function(attrId) {
		var me = this;
		for(var idx in me.attrInfos) {
			var attrInfo = me.attrInfos[idx];
			if(attrId == attrInfo.attrId) {
				return attrInfo;
			}
		}
		return null;
	},

	/**
	 * 表示可能な属性かどうかチェックします.
	 */
	isVisibleAttr: function(attrId) {
		var attrInfo = this.getAttrInfo(attrId);
		if(attrInfo!=null) {
			// ステータスで表示をフィルタ
			var STATUS_SEARCHHIDE = SaigaiTask.Map.Layer.AttrInfo.STATUS_SEARCHHIDE;
			return STATUS_SEARCHHIDE<attrInfo.status;
		}
		return false;
	},

	/**
	 * 編集可能な属性かどうかチェックします.
	 */
	isEditableAttr: function(attrId) {
		var attrInfo = this.getAttrInfo(attrId);
		if(attrInfo!=null) {
			// ステータスで表示をフィルタ
			var STATUS_DEFAULT = SaigaiTask.Map.Layer.AttrInfo.STATUS_DEFAULT;
			return STATUS_DEFAULT==attrInfo.status;
		}
		return false;
	},

	/**
	 * 属性のソートをdisporder順で評価します.
	 * attr1の方が先ならば-1を返す.
	 * attr2の方が先ならば 1を返す.
	 * 同じなら0を返す.
	 */
	evalAttrSort: function(attrId1, attrId2) {
		var attr1 = this.getAttrInfo(attrId1);
		var attr2 = this.getAttrInfo(attrId2);
		if(attr1==null && attr2==null) return 0;
		if(attr2==null || attr1.disporder < attr2.disporder) return -1;
		if(attr1==null || attr2.disporder < attr1.disporder) return  1;
		return 0;
	},

	/**
	 * 非表示のレイヤIDを配列で取得します.
	 * @param {Array<String>} hiddenLayerIds 非表示レイヤIDを追加する配列
	 * @return {Array<String>}
	 */
	getHiddenLayerIds: function(hiddenLayerIds) {
		var layerInfo = this;
		// 非表示なら追加する
		if(layerInfo.visibility==false) {
			if(layerInfo.layerId!=null) {
				hiddenLayerIds.push(layerInfo.layerId);
			}
		}
		// 子の非表示をチェック
		if(layerInfo.children!=null) {
			for(var idx in layerInfo.children) {
				var child = layerInfo.children[idx];
				child.getHiddenLayerIds(hiddenLayerIds);
			}
		}
		return hiddenLayerIds;
	},

	/**
	 * 空間検索条件レイヤを持つかどうかチェックします.
	 * @return 空間検索条件があれば true
	 */
	hasSpatialLayer: function() {
		var me = this;
		return me.spatialLayers!=null;
	},

	/**
	 * WMSのGetMapリクエストのグレーアウトパラメータを設定します.
	 * レイヤ情報の方は透明度を残しておくため更新しません.
	 * @param {Number} grayout
	 */
	setGrayout: function(grayout) {
		var me = this;
		//me.grayout = grayout;
		var layer = me.getLayer();
		if(layer!=null) {
			var layerInfo = layer.layerInfo;
			if(layerInfo!=null) {
				layerInfo.params.grayout = grayout;
				layer.refreshParams({
					nocache: true
				});
			}
		}
	},

	/**
	 * 空間検索範囲レイヤを作成する
	 */
	createSpatialLayer: function() {
		var layerInfo = this;
		var ecommap = layerInfo.ecommap;
		var stmap = ecommap.stmap;
		var olmap = stmap.map;
		var spatialLayerInfo = ecommap.createContentsLayerInfo();
		spatialLayerInfo.singleTile = true;
		spatialLayerInfo.appendChildLayerInfo(layerInfo);
		spatialLayerInfo.params.filterlayer = layerInfo.layerId;
		spatialLayerInfo.params.filterkey = layerInfo.filterkey;
		spatialLayerInfo.params.spatiallayer = 1;
		spatialLayerInfo.params.spatiallayers = JSON.stringify(layerInfo.spatialLayers);
		
		// 時間パラメータの付与
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			spatialLayerInfo.params.time = iso8601Time;
		}

		spatialLayerInfo.params.spatiallayers = JSON.stringify(layerInfo.spatialLayers);
		var spatialLayer = new SaigaiTask.Map.Layer.WMSLayer(spatialLayerInfo);
		// 地図が表示されていない状態でSingleTileのレイヤを表示しようとすると、
		// OpenLayers.Tileにlayerが渡らず、nullになって地図が真っ白になるため、
		// デフォルトで非表示とし、地図表示後に必要であれば表示させること.
		spatialLayer.setVisibility(false);
		olmap.addLayer(spatialLayer);
		layerInfo.spatialLayer = spatialLayer;
	},

	/**
	 * セッションに保存された外部地図レイヤかどうか判定します.
	 * @return {Boolean}
	 */
	isSessionExternalMapLayer: function() {
		var layerInfo = this;
		return (layerInfo.type==SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS || layerInfo.type==SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS_LAYERS) && (layerInfo.layerId.indexOf("extmap_session")==0)		
	},


	/**
	 * 削除可能なセッションに保存された外部地図レイヤかどうか判定します.
	 * @return {Boolean}
	 */
	isDeletableSessionExternalMapLayer: function() {
		var layerInfo = this;
		if(!layerInfo.isSessionExternalMapLayer()) return false;
		
		if(layerInfo.type==SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS) return true;
		
		if(layerInfo.type==SaigaiTask.Map.Layer.Type.EXTERNAL_MAP_WMS_LAYERS) {
			var children = layerInfo.parent.children;
			for(var idx in children) {
				var child = children[idx];
				if(child==layerInfo) continue;
				if(child.metadataid==layerInfo.metadataid) {
					return false;
				}
			}
		}

		return true;
	},

	/**
	 * @return {String} 時間パラメータ
	 */
	getTimeParam: function() {
		var me = this;
		// レイヤ個別で時間パラメータが指定されていたら優先
		if(me.time!=null) return me.time;
		// 全体共通の時間パラメータ
		return me.getLayer().params.time;
	},

	/**
	 * ruleパラメータを再構築
	 */
	updateRuleParam: function() {
		var me = this;
		var layerInfo = me;
		if(!!me.legendrules && 2<me.legendrules.length) {
			// 表示するルールを取得
			var visibleRules = [];
			var allHide = true;
			for(var legendrulesIdx in layerInfo.legendrules) {
				var rule = layerInfo.legendrules[legendrulesIdx];
				if(rule.visibility) {
					visibleRules.push(rule.ruleId);
				}
				// すべてのルールが非表示フラグ
				if(allHide && rule.visibility) allHide = false;
			}

			// パラメータ更新
			// 全表示ならルールを指定しない
			if(visibleRules.length==0 || visibleRules.length==layerInfo.legendrules.length) {
				me.params.rule = null;
			}
			else {
				me.params.rule = me.layerId+":"+visibleRules.join(":");
			}

			// すべてのルールが非表示の場合はレイヤ非表示
			if(allHide) {
				layerInfo.visibility = false;
				layerInfo.searchable = false;
			}
		}
	},

	CLASS_NAME: "SaigaiTask.Map.Layer.LayerInfo"
});
