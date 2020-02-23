/**
 * PDFを出力するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.PdfControl = new OpenLayers.Class(OpenLayers.Control, {

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * 印刷ダイアログ
	 * @type {SaigaiTask.Map.view.PrintWindow}
	 */
	printWindow: null,

	/**
	 * eコミマップベースの印刷プレビュー付き印刷ダイアログ
	 * @type {SaigaiTask.Map.view.PrintPreviewWindow}
	 */
	printPreviewWindow: null,

	initialize: function(stmap) {
		var me = this;
		me.stmap = stmap;
		me.printWindow = new SaigaiTask.Map.view.PrintWindow(me);
		if(location.href.indexOf("http://localhost")==0) {
			me.printWindow = new SaigaiTask.Map.view.PrintPreviewWindow(me);
		}
	},

	/**
	 * 印刷ダイアログを表示します.
	 */
	show: function() {
		var me = this;
		me.printWindow.win.show();
	},

	/**
	 * レイヤ情報からPDF出力リクエストパラメータをビルドする関数の定義
	 * @param {Object} params
	 * @param {SaigaiTask.Map.Layer.LayerInfo} layerInfo
	 */
	buildPdfParams: function(params, layerInfo) {
		var me = this;

		// 時間パラメータ
		var time = SaigaiTask.PageURL.getTime();
		if(!time) time = new Date();
		var iso8601Time = new Date(time).toISOString();
		// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
		if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
			iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
		}

		// レイヤ情報を追加
		switch(layerInfo.type) {
		// TODO: メモ描画の印刷
		// 登録情報の印刷(下から重ねる順に指定、後の方が上)
		// eコミマップ2.4以降では指定順が逆となる(上から重ねる順に指定、前の方が上)
		// contentslayers=c3,c2,c1
		case SaigaiTask.Map.Layer.Type.LOCAL:
			// 表示中の登録情報レイヤを取得
			var contentsLayerInfo = layerInfo;
			if(contentsLayerInfo.visibility==true) {
				// レイヤIDを追加(下から重ねる順に指定)
				//if(0<params.contentslayers.length) params.contentslayers += ",";
				//params.contentslayers += contentsLayerInfo.layerId;
				// レイヤIDを追加(上から重ねる順に指定)
				if(0<params.contentslayers.length) params.contentslayers = "," + params.contentslayers;
				params.contentslayers = contentsLayerInfo.layerId + params.contentslayers;
				// フィルタレイヤならパラメータを設定(フィルタレイヤは１つのみという前提)
				if(contentsLayerInfo.filterkey!=null) {
					params.filterkey = contentsLayerInfo.filterkey;
					params.filterLayerId = contentsLayerInfo.layerId;
				}
				// 登録情報レイヤの時間パラメータ付与
				params.time = iso8601Time;
				// 登録情報レイヤのSLDルールの表示パラメータ付与
				if(typeof contentsLayerInfo.params.rule=="string"
					&& 0<contentsLayerInfo.params.rule.length) {
					params.rule += (0<params.rule.length?",":"")+contentsLayerInfo.params.rule;
				}
			}
			break;
		// KMLレイヤの印刷
		// kmllayers:kml1974:1,kml1981:1,kml1982:1,kml1983:1
		case SaigaiTask.Map.Layer.Type.KML:
			var kmlLayerInfo = layerInfo;
			if(kmlLayerInfo.visibility==true) {
				// レイヤIDを追加
				if(0<params.kmllayers.length) params.kmllayers += ",";
				params.kmllayers += kmlLayerInfo.layerId+":1";
				// 時間パラメータは対象外
			}
			break;
		// 主題図の印刷(下から重ねる順に指定、後の方が上)
		// reflayers:[["ref3",1,"ref3_2,ref3_1,ref3_0"],["ref2",1,"ref2_2,ref2_1,ref2_0"],["ref1",1,"ref1_2,ref1_1,ref1_0"]]
		case SaigaiTask.Map.Layer.Type.REFERENCE_WMS:
			// 配列文字列をオブジェクトに変換
			var reflayers = params.reflayers.length==0 ? [] : JSON.parse(params.reflayers);

			// 表示中の参照レイヤを取得
			var referenceLayerInfo = layerInfo;
			if(referenceLayerInfo.visibility==true) {
				var reflayer = [referenceLayerInfo.layerId, 1, ""];
				reflayers.push(reflayer);
				// 時間パラメータの付与
				params.layertimes += (0<params.layertimes.length?",":"")+referenceLayerInfo.layerId+","+iso8601Time;
			}

			// パラメータを配列文字列で設定
			if(0<reflayers.length) params.reflayers = JSON.stringify(reflayers);
			break;
		case SaigaiTask.Map.Layer.Type.REFERENCE:
			// 配列文字列をオブジェクトに変換
			var reflayers = params.reflayers.length==0 ? [] : JSON.parse(params.reflayers);

			var child = layerInfo;
			if(child.visibility==true) {
				// 親レイヤ情報を検索
				var success = false;
				var reflayer = null;
				for(var idx in reflayers) {
					reflayer = reflayers[idx];
					if(reflayer[0]==child.parentLayerId) {
						var layers = reflayer[2];
						var opacity = reflayer[1];
						// 透明度不一致の場合
						if(child.opacity!=opacity) {
							if(0<layers.length) continue;
							reflayer[1] = child.opacity;
						}
						if(0<layers.length) layers += ",";
						layers+=child.layerId;
						reflayer[2] = layers;
						success = true;
						break;
					}
				}
				// 透明度不一致の場合(主題図の個別透明度の対応)
				if(!success && reflayer[0]==child.parentLayerId) {
					var cloneReflayer = [].concat(reflayer);
					var cloneLayerId = cloneReflayer[0];
					var cloneNum = 1;
					if(cloneLayerId.indexOf("#")!=-1) {
						var cloneLayerIdArray = cloneLayerId.split("#");
						cloneLayerId = cloneLayerIdArray[0];
						cloneNum = parseInt(cloneLayerIdArray[1]) + 1;
					}
					cloneReflayer[0] = cloneLayerId+"#"+cloneNum;
					cloneReflayer[1] = child.opacity;
					cloneReflayer[2] = child.layerId;
					reflayers.push(cloneReflayer);
					success = true;
				}
				
				// エラーログ
				if(success) {
					// パラメータを配列文字列で設定
					if(0<reflayers.length) params.reflayers = JSON.stringify(reflayers);
					// 時間パラメータの付与
					params.layertimes += (0<params.layertimes.length?",":"")+child.layerId+","+iso8601Time;
				}
				else {
					console.log(lang.__("Unable to print {0}. Parent layer info not exist.", child.layerId));
				}
			}
			break;
		default:
			// 主題図(画像)の印刷(下から重ねる順に指定、後の方が上)
			// overlaylayers:wms2:1,wms1:1
			if(SaigaiTask.Map.Layer.Type.isOverlayLayerType(layerInfo.type)) {
				var overlayLayerInfo = layerInfo;
				if(overlayLayerInfo.visibility==true) {
					// レイヤIDを追加
					if(0<params.overlaylayers.length) params.overlaylayers += ",";
					params.overlaylayers += overlayLayerInfo.layerId+":1";
					// 時間パラメータの付与
					params.layertimes += (0<params.layertimes.length?",":"")+overlayLayerInfo.layerId+","+iso8601Time;
				}
				break;
			}
		}

		// 子レイヤ情報がある場合は下から順に再帰
		for(var idx=layerInfo.children.length-1; 0<=idx; idx--) {
			me.buildPdfParams(params, layerInfo.children[idx]);
		}
	},

	createPdfParams: function() {
		var me = this;
		var stmap = me.stmap;
		var ecommap = stmap.ecommaps[0];
		var mapInfo = ecommap.mapInfo;
		
		// PDF 生成パラメータの設定
		var params = me.printWindow.getValues();
		SaigaiTask.Map.copy(params, {
			cid: mapInfo.communityId,
			mid: mapInfo.mapId,
			epsg: stmap.map.getProjection().split(":")[1],
		});
		
		// bbox範囲が指定されていない場合
		if(!params.bbox) {
			// bboxに内接する設定ページ設定の範囲を返却
			params.bbox = map.map.getExtent().toString();
		}

		// パラメータ初期化
		params.memoVisible = false;
		params.contentslayers = "";
		params.kmllayers = "";
		params.reflayers = "";
		params.overlaylayers = "";
		params.layertimes = "";
		params.rule = "";
		var addLayertimes = function(layerId, iso8601Time) {
			params.layertimes += (0<params.layertimes.length?",":"")+layerId+","+iso8601Time;
		}

		//レイヤを表示している下から順にパラメータを生成
		var layerInfoTree = ecommap.layerInfoTree;
		for(var idx=layerInfoTree.length-1; 0<=idx; idx--) {
			me.buildPdfParams(params, layerInfoTree[idx]);
		}

		// 時間パラメータの付与
		var time = SaigaiTask.PageURL.getTime();
		if(!time) time = new Date();
		var toISOString = function(time) {
			var iso8601Time = new Date(time).toISOString();
			// eコミマップ対応で、タイムゾーン分プラスする（getTimezonOffset が 負数-540 となるので実際には除算で足す）
			if(SaigaiTask.PageURL.CONFIG_SHIFT_TIMEZONE_OFFSET) {
				iso8601Time = new Date(time - time.getTimezoneOffset()*60*1000).toISOString();
			}
			return iso8601Time;
		}
		var iso8601Time = toISOString(time);
		
		for(var layerId in ecommap.layerInfoStore) {
			var layerInfo = ecommap.layerInfoStore[layerId];
			if(layerInfo.visibility) {
				// 登録情報レイヤの場合
				if(layerInfo.type==SaigaiTask.Map.Layer.Type.LOCAL) {
					// 登録情報レイヤは個別指定の場合のみ layertimes を指定する（個別指定しない場合は time パラメータ）
					if(layerInfo.time!=null) {
						addLayertimes(layerInfo.layerId, toISOString(new Date(layerInfo.time)));
					}
				}
				// 主題図画像
				else if(SaigaiTask.Map.Layer.Type.isOverlayLayerType(layerInfo.type)) {
					var layertime = layerInfo.time!=null ? toISOString(new Date(layerInfo.time)) : iso8601Time;
					addLayertimes(layerInfo.layerId, layertime);
				}
				// 主題図(項目)
				else if(layerInfo.type==SaigaiTask.Map.Layer.Type.REFERENCE_WMS) {
					var layertime = layerInfo.time!=null ? toISOString(new Date(layerInfo.time)) : iso8601Time;
					addLayertimes(layerInfo.layerId, layertime);
				}
			}
		}
		// 時間情報を追加
		{
			// 印刷日時パラメータを追加
			// [例] 2016/10/11 10:37 印刷
			var printTime = new Date();
			printTime.setMilliseconds(0); // ミリ秒切捨て
			printTime.setSeconds(0); // 秒切捨て
			var printTimeStr = SaigaiTask.Page.Timeslider.formatDate(printTime, {
				dateSplit: "/",
				datetimeSplit: " ",
				timeSplit: ":"
			});
			printTimeStr += " " + lang.__("Print");

			// 表示時間パラメータを追加
			// [例] 2016/10/01 00:00 時点
			var time = SaigaiTask.PageURL.getTime();
			if(time!=null) {
				time.setMilliseconds(0); // ミリ秒切捨て
				var timeStr = SaigaiTask.Page.Timeslider.formatDate(time, {
					dateSplit: "/",
					datetimeSplit: " ",
					timeSplit: ":"
				});
				timeStr += " " + lang.__("As of");
				printTimeStr += " "+timeStr;
			}

			// 時間パラメータを追加
			params.description = printTimeStr + "\n" + params.description;
		}

		// LayerInfoをリクエストパラメータに追加するメソッド
		var getLayerInfoJSON = function(layerInfo) {
			var wmsProxy = "";
			var metadataId = "";
			// 外部サイトの認証情報があれば付与する
			if(layerInfo.wmsproxy && layerInfo.wmsproxy != 0 && layerInfo.wmsproxy != null){
				wmsProxy = layerInfo.wmsproxy;
			}
			if(layerInfo.metadataid && layerInfo.metadataid != "" && layerInfo.metadataid != null){
				metadataId = layerInfo.metadataid;
			}
			return JSON.stringify({
				layerId: layerInfo.layerId,
				name: layerInfo.name,
				type: layerInfo.type,
				opacity: layerInfo.opacity,
				wmsURL: layerInfo.wmsURL,
				wmsFormat: layerInfo.wmsFormat,
				featuretypeId: layerInfo.featuretypeId,
				wmsLegendURL: layerInfo.wmsLegendURL,
				wmsproxy: wmsProxy,
				metadataId: metadataId
			});
		}
		var addLayerInfoParam = function(layerInfo) {
			params["layerInfo."+layerInfo.layerId] = getLayerInfoJSON(layerInfo);
		};

		// 表示中の外部地図レイヤを取得
		var exlayers = [];
		var externalLayerInfos = ecommap.getExternalMapLayerInfos();
		var printExternalLayerInfos = [];
		var cloneNumMap = {};
		var cloneExternalLayerInfo = function(externalLayerInfo) {
			var cloneLayerId = externalLayerInfo.layerId;
			var cloneNum = cloneNumMap.hasOwnProperty(cloneLayerId) ? cloneNumMap[cloneLayerId] : 1;
			cloneNumMap[cloneLayerId] = cloneNum+1;

			var printExternalLayerInfo = JSON.parse(getLayerInfoJSON(externalLayerInfo));
			printExternalLayerInfo.layerId = cloneLayerId+"#"+cloneNum;
			printExternalLayerInfo.children = [];
			printExternalLayerInfo.visibility = externalLayerInfo.visibility;
			printExternalLayerInfos.push(printExternalLayerInfo);
			return printExternalLayerInfo;
		}
		// 透明度個別対応のため印刷用のレイヤ情報を作成する
		for(var idx in externalLayerInfos) {
			var externalLayerInfo = externalLayerInfos[idx];
			var printExternalLayerInfo = cloneExternalLayerInfo(externalLayerInfo);
			if(externalLayerInfo.visibility==true) {
				for(var cidx in externalLayerInfo.children) {
					var child = externalLayerInfo.children[cidx];
					if(child.visibility==true) {
						// 透明度不一致の場合
						if(child.opacity!=opacity) {
							if(0<printExternalLayerInfo.children.length) {
								printExternalLayerInfo = cloneExternalLayerInfo(externalLayerInfo);
							}
							printExternalLayerInfo.opacity = child.opacity;
						}
						printExternalLayerInfo.children.push(child);
					}
				}
			}
		}
		for(var idx in printExternalLayerInfos) {
			var externalLayerInfo = printExternalLayerInfos[idx];
			if(externalLayerInfo.visibility==true) {
				// add exlayers parameter
				var opacity = externalLayerInfo.opacity;
				var exlayer = [externalLayerInfo.layerId, opacity];
				// 時間パラメータの付与
				addLayertimes(externalLayerInfo.layerId, iso8601Time);
				var layers = [];
				for(var cidx in externalLayerInfo.children) {
					var child = externalLayerInfo.children[cidx];
					if(child.visibility==true) {
						// extlayers.layers に追加
						layers.push(child.layerId);
						// add layerInfo parameter
						addLayerInfoParam(child);
						// 時間パラメータの付与
						addLayertimes(child.layerId, iso8601Time);
					}
				}
				exlayer.push(layers.reverse().join(","));
				exlayers.push(exlayer);
				// add layerInfo parameter
				addLayerInfoParam(externalLayerInfo);
			}
		}
		// UTMグリッドを印刷する場合
		if(0<=Number(params.mgrs)) {
			// LayerInfo
			var mgrsLayerInfo = stmap.controls.mgrsControl.layerInfo;
			var exlayer = [mgrsLayerInfo.layerId, 1];
			// add parameter
			var childLayerId = mgrsLayerInfo.layerId+"_";
			exlayer.push(childLayerId); // dummy child layerId ※現状1つしか指定できない
			exlayers.push(exlayer);
			addLayerInfoParam(mgrsLayerInfo);

			// 印刷するMGRSグリッド桁数は印刷ウィンドウで指定したもので上書き
			var featuretypeIds = [];
			var childLayerName = "UTM Grid";
			for(var p=0; p<=Math.min(5, Number(params.mgrs)); p++) {
				featuretypeIds.push("mgrs"+p);
				childLayerName =
					p==0 ? "100km" :
					p==1 ? "10km" :
					p==2 ? "1km" :
					p==3 ? "100m" :
					p==4 ? "10m" : "1m";
			}
			var paramLayerInfo = JSON.parse(params["layerInfo."+mgrsLayerInfo.layerId]);
			paramLayerInfo.featuretypeId = featuretypeIds.join(",");
			params["layerInfo."+mgrsLayerInfo.layerId] = JSON.stringify(paramLayerInfo);
			// dummy child layerInfo
			paramLayerInfo.name = childLayerName;
			params["layerInfo."+childLayerId] = JSON.stringify(paramLayerInfo);
		}
		if(0<exlayers.length) params.exlayers = JSON.stringify(exlayers);
		// 表示中の背景地図を取得
		if(0<params.printBaselayer) {
			var baseLayerInfos = ecommap.getBaseLayerInfos();
			for(var idx in baseLayerInfos) {
				var baseLayerInfo = baseLayerInfos[idx];
				if(baseLayerInfo.visibility == true) {
					params.baselayer = baseLayerInfo.layerId + ":1";
					// 時間パラメータの付与
					addLayertimes(baseLayerInfo.layerId, iso8601Time);
				}
			}
		}
		// 背景地図なしの場合
		else {
			// 凡例を非表示
			params.printlegend = 0;
			// 枠線を非表示
			params.noframe= 1;
			// フッターなし
			params.fpage = 0;    // ページ番号
			params.fmappage = 0; // マップ分割番号
			// 縮尺
			//params.scalealign = -1; // 非表示
		}
		delete params.printBaselayer;
		return params;
	},
	
	/**
	 * 印刷処理を実行します.
	 */
	print: function() {
		var me = this;
		var stmap = me.stmap;
		
		// PDF 生成処理をリクエスト
		var params = me.createPdfParams();
		stmap.api.createPdf(params);

		// PDF生成が終わるまでチェック
		setTimeout(function() {
			me.checkPdfDownload();
		}, 1000);
	},

	/** PDF生成が終わるまでチェック */
	checkPdfDownload : function() {
		var me = this;
		var printWindow = me.printWindow;
		var progressbar = me.printWindow.progressbar;
		$.ajax(SaigaiTask.contextPath+"/PdfServlet", {
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.name == "Error") {
					console.error(lang.__("An error occurred during the PDF creation process."));
				} else {
					console.debug("Creating pdf: "+ data[0]+ "/" + data[1]);
					var v = data[0]/data[1];
					try {
						if (data[0] == 0 && data[1] == 0) setTimeout( function(){me.checkPdfDownload();}, 1000 );
						else {
							if (data[0] < data[1]) {
								progressbar.updateProgress(v, lang.__('Creating PDF file..')+' '+Math.round(100 * v) + lang.__("%"));
								setTimeout( function(){me.checkPdfDownload();}, 1000 );
							}
							else {
								printWindow.onsuccess();
							}
						}
					} catch (e) {console.warn(e);}
				}
			}
		});
	},

	/**
	 * @param {String} pageType 用紙の版
	 * @return {Object} pageSize
	 * pageSize.width 幅
	 * pageSize.height 高さ
	 */
	getPrintSize: function(pageType) {
		var a0 = {
			width: 841,
			height: 1189
		};
		var b0 = {
			width: 1030,
			height: 1456
		};
		var pageSize = null;
		// A版、B版設定
		var type = pageType[0].toLowerCase();
		if(type=="a") pageSize = a0;
		else if(type=="b") pageSize = b0;
		// 等分する
		var num = Number(pageType[1]);
		while(0<num) {
			pageSize.width  = pageSize.width/2;
			pageSize.height = pageSize.height/2;
			num--;
		}
		return pageSize;
	}
});
