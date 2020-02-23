
// Ajax用クラス
function GetDatas(){
	this.initialize.apply(this, arguments);
}
GetDatas.prototype = {
	initialize : function(url, param, token){
		this.url = url;
		this.param = param;
		this.token = token;
	},
	doAjax : function(){
		var jqXHR = $.ajax({
			url : this.url,
			type: "POST",
			//headers: {"X-CSRF-Token":this.token},
			dataType : "json",
			data : this.param,
			contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
		return jqXHR.promise();
	},
	lock : function(){
		var jqXHR = $.ajax({
			url : this.url,
			dataType : "json",
			data : this.param,
			contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
		return jqXHR.promise();
	}
}


/**
 * 意思決定支援用JS
 */
SaigaiTask.DecisionSupport = function(options) {
	this.initialize(options);
};

SaigaiTask.DecisionSupport.prototype = {

		// 地図ならmap, リストならlistが入る
		mode : "",
		lock : false,
		lockTimer : null,

		initialize: function(mode) {
			this.mode = mode;
		},

		/**
		 * 地震レイヤの一覧を取得して、inputtype=radioでDialog内に追加する
		 * @param url : アクセス先
		 * @param token : X-CSRF-Token
		 * @param div : Dialog内に設置するDiv要素
		 * @returns : 無し
		 */
		getEarthQuakeLayer : function(url, token, menuInfoId){
			var dfd = $.Deferred();
			var me = this;
			var div = $("<div>");

			var ajaxData = new GetDatas(url + "/JSONEarthQuakeLayerList", "menuInfoId=" + menuInfoId + "&X-CSRF-Token=" + token, token);

			ajaxData.doAjax()
			.done(function(data, statusText, jqXHR){

				var dialogMsg = $("<p>").attr({"id":"dialog_message"}).css({"color":"#FF0000"}).html("").appendTo(div);
				var warningMsg = $("<p>").css({"color":"#FF0000"}).html("").appendTo(div);
				if(data == null){
					$("<p>").css({"color":"#FF0000"}).html(lang.__("Error occurred in process of getting configurations needed for calculation.")).appendTo(div);
					dfd.resolve(div);
				}else{
					if('arealayers' in data){
						var quakeDiv = $("<div>").prependTo(div);
						//$("<p>").css({"margin-top":"5px"}).html(lang.__("Area layer list")).appendTo(quakeDiv);
						$("<p>").css({"margin-top":"5px"}).html(lang.__("Area layer list")).appendTo(quakeDiv);
						var layers = data.arealayers;
						console.log(layers.length);
						// Divに追加していく
						for(var i = 0; i < layers.length; i++){
							if('id' in layers[i] && 'note' in layers[i] && 'buffer' in layers[i]){
								var note = layers[i].note;
								//$("<br>").appendTo(quakeDiv);
								//$("<p>").html(layers[i].note);
								$("<div>").css({"float":"left", "width":"65%"}).html(note).appendTo(quakeDiv);
								var innerDiv = $("<div>").css({"float":"left", "width":"32%"}).appendTo(quakeDiv);
								$("<span>").html("&nbsp;&nbsp;buffer&nbsp;").appendTo(innerDiv);
								$("<input>").attr({"type":"text", "name":"arealayers", "id":"areaLayers_" + i}).width(50).val(layers[i].buffer).appendTo(innerDiv);
								$("<span>").html("&nbsp;m").appendTo(innerDiv);
								$("<br>").appendTo(quakeDiv);
							}
						}
					}
					if('earthquakelayers' in data){
						var quakeDiv = $("<div>").prependTo(div);
						//$("<p>").html(lang.__("Select earthquake layer")).appendTo(quakeDiv);
						$("<p>").html(lang.__("Select earthquake layer")).appendTo(quakeDiv);
						var layers = data.earthquakelayers;
						// Divに追加していく
						var check = true;
						for(var i = 0; i < layers.length; i++){
							if('name' in layers[i] && 'layerdataid' in layers[i]){
								//$("<br>").prependTo(quakeDiv);
								$("<input>").attr({"type":"radio", "name":"earthQuakeLayers", "id":"earthQuakeLayers_" + i, "checked":check}).val(layers[i].layerdataid).appendTo(quakeDiv);
								$("<label>").attr({"for":"earthQuakeLayers_" + i}).html(layers[i].name).appendTo(quakeDiv);
								if(check=="") check=false;
							}
						}
						if(layers.length == 0){
							warningMsg.html("[WARNING]");
							$("<p>").css({"color":"#FF0000"}).html(lang.__("Estimate the number of persons difficult to get back home as 0, due to no configurations of quake layer needed for the calculation.")).appendTo(quakeDiv);
						}
					}
					if('error' in data){
						var errs = data.error;
						if(errs.length > 0){
							warningMsg.html("[WARNING]");
						}
						for(var i = 0; i < errs.length; i++){
							if('msg' in errs[i]){
								$("<p>").css({"color":"#FF0000"}).html(errs[i].msg).appendTo(div);
							}
						}
					}
				}
				dialogMsg.css({"margin-top":"5px"}).html(lang.__("Much time needed to create estimation data. Continue to estimate the number of refugees?"));
				dfd.resolve(div);
			}).fail(function(jqXHR, statusText, errorThrown){
				alert(lang.__("Failed to get quake layer."));
				dfd.reject();
			});
			return dfd.promise();
		},

		/**
		 * 演算処理を実行する
		 */
		calculation : function(url, token, layerid){
			var dfd = $.Deferred();
			var ajaxData = new GetDatas(url + "/calculation", "layers=" + layerid + "&X-CSRF-Token=" + token, token);
			ajaxData.doAjax().done(function(data, statusText, jqXHR){
				dfd.resolve(data);
			}).fail(function(data){
				console.log("decisionsupport.calculation fail");
				console.log(data);
				dfd.reject();
			});
			return dfd.promise();
		},

		/**
		 * 地図画面でアラート矢印表示を行う
		 */
		displayDecisionLayer : function(url, token){
			var dfd = $.Deferred();
			var me = this;
			var stmap = map;
			var olmap = stmap.map;

			// レイヤ追加
			var layer = new OpenLayers.Layer.Vector(lang.__("Decision support"));
			// OpenLayers
			olmap.addLayer(layer)
			// 災対マップ
			stmap.toFront(layer);
			// 描画スタイルを設定
			layer.styleMap = new OpenLayers.StyleMap();
			var style = {
				// 塗りつぶしの設定
				fillColor: "white",
				fillOpacity: 0.4,
				// ラインの設定
				strokeColor: "black",//"#ee9900",
				strokeOpacity: 1,
				strokeWidth: 2,
				strokeLinecap: "round", // butt, round, square
				strokeDashstyle: "solid", // dot, dash, dashdot, longdash, longdashdot, solid
				// 図の設定
				externalGraphic: stmap.icon.getURL("editingIconURL"),
				graphicWidth: 19,
				graphicHeight: 32,
				graphicXOffset: -9,
				graphicYOffset: -32,
				graphicOpacity: 1
			};
			layer.styleMap.styles['default'].defaultStyle = style;

			// コントロール追加
			// http://dev.openlayers.org/apidocs/files/OpenLayers/Control/SelectFeature-js.html
			var selectFeature = new OpenLayers.Control.SelectFeature(layer);
			olmap.addControl(selectFeature);
			selectFeature.activate();

			var ajaxData = new GetDatas(url + "/JSONAlertShelterList");
			ajaxData.doAjax()
			.done(function(data, statusText, jqXHR){
				if(data == null){
					dfd.reject();
				}else{
					// WKT
					var wktFormat = new OpenLayers.Format.WKT();

					if('yellow' in data){
						// 黄色矢印を避難所に設置する
						var yellows = data.yellow;
						for(var i = 0; i < yellows.length; i++){
							var feature = wktFormat.read(yellows[i]);
							// WGS84(EPSG:4326) から、表示中のプロジェクションに変換
							feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:"+stmap.epsg));
							//feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
							// styleの設定
							feature.style = new Object();
							for(var key in style){
								feature.style[key] = style[key];
							}
							feature.style['externalGraphic'] = stmap.icon.getURL("selectingIconURL");
							layer.addFeatures([feature]);
						}
					}

					if('red' in data){
						// 黄色矢印を避難所に設置する
						var reds = data.red;
						for(var i = 0; i < reds.length; i++){
							var feature = wktFormat.read(reds[i]);
							// WGS84(EPSG:4326) から、表示中のプロジェクションに変換
							//feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:"+stmap.epsg));
							feature.geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
							// styleの設定
							feature.style = new Object();
							for(var key in style){
								feature.style[key] = style[key];
							}
							layer.addFeatures([feature]);
						}
					}
					// 再描画
					layer.redraw();
					dfd.resolve();
				}
			}).fail(function(jqXHR, statusText, errorThrown){
				alert(lang.__("Failed to get refugee estimation alert data."));
				dfd.reject();
			});
			return dfd.promise();
		},

		/**
		 * 他ユーザと同タイミングで実行出来ないようにロックする
		 */
		doLock : function(url, flags){
			var dfd = $.Deferred();
			var me = this;

			var locks = flags == true ? true : false;

			if(flags){
				var ajaxData = new GetDatas(url + "/decisionLock", "", "");
				// ロックする
				ajaxData.lock()
				.done(function(result){
					// ロックフラグを取得
					var lock = false;
					if(!!result) lock = !!result.lock;

					// ロック失敗処理
					if(lock==false) {
						var lockInfo = result.lockInfo;
						var username ="";
						if(!!lockInfo.groupInfo && !!lockInfo.groupInfo.name) username="("+lockInfo.groupInfo.name+")";
						if(!!lockInfo.unitInfo && !!lockInfo.unitInfo.name) username="("+lockInfo.unitInfo.name+")";
						alert(lang.__("Calculation by {0} in process", username));
						dfd.reject();
					}else{
						// lockを定期的にリクエストし、ロック時間を延長する
						me.lockTimer = setInterval(function() {
							ajaxData.lock();
						}, 10*1000);
						dfd.resolve();
					}
				}).fail(function(){
					alert(lang.__("Failed to confirm the exclusion of refugee estimation."));
					dfd.reject();
				});
			}else{
				// 編集ロックの延長リクエストを停止
				clearInterval(me.lockTimer);
				var ajaxData = new GetDatas(url + "/decisionUnlock", "", "");
				// ロック解除
				ajaxData.lock()
				.done(function(){
					console.log(lang.__("Succeeded to unlock."));
				}).fail(function(){
					alert(lang.__("Failed to unlock."));
				});
			}
			return dfd.promise();
		}
}
