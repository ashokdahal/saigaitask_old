/**
 * WMS layer
 * @requires SaigaiTask/Map/Layer.js
 * @requires SaigaiTask/Map/Layer/Type.js
 */
SaigaiTask.Map.Layer.WMSLayer = new OpenLayers.Class(OpenLayers.Layer.WMS, {

	/**
	 * Layer information
	 * @type {SaigaiTask.Map.Layer.LayerInfo}
	 */
	layerInfo: null,

	params: null,

	/**
	 *
	 * @param {SaigaiTask.Map.Layer.LayerInfo} layerInfo レイヤ情報
	 */
	initialize: function(layerInfo) {

		// 引数を処理
		var me = this;
		me.layerInfo = layerInfo;

		// 表示するレイヤのvisibility初期化
		for(var childLayerInfoKey in layerInfo.children) {
			var childLayerInfo = layerInfo.children[childLayerInfoKey];
			if(typeof childLayerInfo.visibility=="undefined") {
				childLayerInfo.visibility = true;
			}
			childLayerInfo.parent = layerInfo;
		}

		// layerInfoからWMS初期化パラメータを生成
		var name = layerInfo.name;
		var url = layerInfo.wmsURL;
		var params = this.params = {
			TRANSPARENT: true
		};
		// ArcGISの表示でエラーになるのでコメントアウト（不要なはず）
		//if(layerInfo.featuretypeId!=null) {
		//	params.layers = layerInfo.featuretypeId;
		//}
		var options = {
			visibility: layerInfo.visibility,
			alpha: true,
			opacity: layerInfo.opacity,
			isBaseLayer: SaigaiTask.Map.Layer.Type.isBaseLayerType(layerInfo.type),
			attribution: layerInfo.attribution,
			transitionEffect: layerInfo.transitionEffect,
			singleTile: layerInfo.singleTile
		};
		if(layerInfo.ecommap!=null) {
			options.isBaseLayer = layerInfo.ecommap.isBaseLayerInfo(layerInfo);
		}

		// WMSレイヤの初期化
		OpenLayers.Layer.WMS.prototype.initialize.apply(this, [name, url, params, options]);
		me.refreshParams();

		layerInfo.layer = this;
	},

	/**
	 * WMSリクエストパラメータを再読み込みします.
	 * @param {Object} option
	 * @param {Boolean} option.nocache キャッシュ使用フラグ
	 */
	refreshParams: function(option) {
		var me = this;
		me._refreshParams(option);
		me.events.triggerEvent("refreshParams");
	},

	_refreshParams: function(option) {
		var defaultOption = {
			nocache: false
		};
		if(typeof option=="undefined") option = {};
		Ext.applyIf(option, defaultOption);

		// レイヤ情報からパラメータを作成し反映
		delete this.params.layertimes; // layertimes が残る場合があるので削除
		OpenLayers.Util.extend(this.params, this.getParams());
		// キャッシュを読み込まないように乱数を更新
		if(option.nocache) {
			this.params._olSalt = Math.random();
		}

		console.log("params "+this.layerInfo.name);
		console.log(this.params);

		// LAYERSが指定されていないとエラーになるためレイヤ自体を非表示にする
		if(typeof this.params.LAYERS=="undefined" ||
				this.params.LAYERS.length==0) {
			this.setVisibility(false);
		}
		else {
			this.setVisibility(this.layerInfo.visibility);
		}

		// 時間パラメータの付与
		var time = SaigaiTask.PageURL.getTime();
		if(!!time) {
			var iso8601Time = time.toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			this.params.time = iso8601Time;
		}
		else {
			this.params.time = null;
		}

		console.log("visibility");
		console.log(this.getVisibility());

		console.log("opacity");
		console.log(this.opacity);

		console.log(this);

		this.redraw(true);
	},

	/**
	 * レイヤ情報からWMSリクエストパラメータを取得します.
	 * 子レイヤ情報のパラメータはカンマ区切りの文字列に変換します.
	 * @returns {Object} パラメータ
	 */
	getParams: function() {

		var params = $.extend(true, {}, this.layerInfo.params);

		var getParamsRecursive = function(params, layerInfo) {
			// 内部ノード
			if(0<layerInfo.children.length) {
				for(var key in layerInfo.children) {
					var child = layerInfo.children[key];
					getParamsRecursive(params, child);
				}
			}
			// 葉ノード
			else {
				/// param をコピー
				if(layerInfo.visibility) {
					layerInfo.mergeParams(params);
				}
			}
		};

		if(0<this.layerInfo.children.length) {
			getParamsRecursive(params, this.layerInfo);
		}

		// 整形
		for(var key in params) {
			var param = params[key];
			// 葉ノードの配列パラメータをCSV文字列に変換
			if($.isArray(param)) {
				params[key] = param.reverse().join(",");
			}
			// JSONオブジェクトをJSON文字列に変換
			else if(typeof param=="object") {
				params[key] = JSON.stringify(param);
			}
		}

		return params;
	},

	/**
	 * WMSのLAYERSパラメータを生成します.
	 * featuretypeIdが設定されていればそれを返します.
	 * childLayerInfoが設定されている場合はvisibilityがtrueのもののみ取得できます.
	 * @deprecated
	 * @returns {String} LAYERSパラメータ
	 */
	buildLayersParam: function() {
		var me = this;
		var info = me.layerInfo;
		// 表示するレイヤIDを取得する
		var layers = [];

		// featuretypeId が設定されているならそれを返す
		if(typeof info.featuretypeId !="undefined") {
			return info.featuretypeId;
		}

		// childLayerInfos が設定されているなら visibility をみて返す
		for(var childLayerInfoKey in info.childLayerInfos) {
			var childLayerInfo = info.childLayerInfos[childLayerInfoKey];
			if(childLayerInfo.visibility==true) {
				layers.push(childLayerInfo.featuretypeId);
			}
		}
		return layers.join(",");
	},

	//@Override
	getURL: function(bounds){
		var me = this;
		var info = me.layerInfo;

		var url = OpenLayers.Layer.WMS.prototype.getURL.call(this, bounds);

		if(info.wmsproxy != null){
			if (info.wmsproxy != 0 && url.indexOf("http") == 0) {
				var page_url = SaigaiTask.PageURL.getUrl();
				var metadataid = "";
				if(info.children.length > 0){
					metadataid = info.children[0].metadataid;
				}
				// Proxy用のActionに飛ばす
				url = page_url.substr(0, page_url.indexOf("/page/") + 6) + "map/externalWmsAuth/?url=" + encodeURIComponent(url) + "&externalmapdatainfoid="+info.wmsproxy + "&metadataid=" + metadataid;
			}
		}
		return url;
	},

	/**
	 * WMSのLAYERSのvisibilityを設定します.
	 * @deprecated
	 * @param {String} featuretypeId
	 * @param {Boolean} visibility
	 */
	setLayersParamVisibility: function(featuretypeId, visibility) {
		var me = this;
		var info = me.layerInfo;
		for(var childLayerInfoKey in info.childLayerInfos) {
			var childLayerInfo = info.childLayerInfos[childLayerInfoKey];
			// 指定フィーチャタイプIDならばvisibilityを設定
			if(childLayerInfo.featuretypeId == featuretypeId) {
				childLayerInfo.visibility = visibility;
				break;
			}
		}
	},
	CLASS_NAME: "SaigaiTask.Map.Layer.WMSLayer"
});
