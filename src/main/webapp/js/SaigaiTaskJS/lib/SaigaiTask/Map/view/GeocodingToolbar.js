/**
 *
 * @requires SaigaiTask/Map/view.js
 */
SaigaiTask.Map.view.GeocodingToolbar = function() {
	this.initialize.apply(this, arguments);
};
SaigaiTask.Map.view.GeocodingToolbar.prototype = {
	tbar: null,
	map: null,
	addressField: null,
	resultAddressField: null,
	resultAddressFieldTip: null,

	/**
	 * コンストラクタ
	 */
	initialize: function(map) {
		var me = this;
		me.map = map;
		var olmap = map.map;
		var footerTbarItems = [];
		// 地図を移動のインタフェースを追加
		var addressField = me.addressField = Ext.create("Ext.form.field.Text", {
			//emptyText: "移動したい地名を入力", // フォーカス時に消えてくれないことがあるため使用しない
			enableKeyEvents: true,
			listeners: {
				afterrender: function(field, event) {
					// エンターキーで送信しないようにする
					var el = field.getEl();
					$(el.dom).keydown(function(event) {
						if(event.keyCode==13) {
							event.preventDefault();
							return false;
						}
					});
				},
				specialkey: function(field, e, eOpts) {
					if(e.getKey()==e.ENTER) {
						me.fireJumpAddressButton();
					}
				}
			}
		});
		var jumpAddressButton = Ext.create("Ext.button.Button", {
			text : lang.__('Move map'),
			tooltip: lang.__("Move to the location of entered address or of self defense force code"),
			margin : "0 5 0 5",
			listeners: {
				click: function() {
					me.fireJumpAddressButton();
				}
			}
		});
		footerTbarItems.push(addressField);
		footerTbarItems.push(jumpAddressButton);
		// 中心位置の住所を取得インタフェースを追加
		var resultAddressField = me.resultAddressField = Ext.create("Ext.form.Label", {
			text: lang.__("Due to display crumbled on IE, messages are displayed beforehand."),
			hidden: true // ダミーのtext が見えないように隠す
		});
		var rgeocodeButton = Ext.create("Ext.button.Button", {
			text : lang.__('Get address of center position'),
			tooltip: lang.__("The address of map center is displayed."),
			margin : "0 5 0 5",
			listeners: {
				click: function() {
					// 地図の中心位置
					var center = olmap.getCenter();
					center = center.transform(olmap.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));

					me.map.geocode(center)
					.done(function(results) {
						var result = results[0];
						me.updateAddress(result);
					})
					.fail(function(results, status) {
						alert(lang.__("Failed to get address.")+status);
					});
				}
			}
		});
		footerTbarItems.push("-");
		footerTbarItems.push(rgeocodeButton);
		footerTbarItems.push(resultAddressField);
		var footerTbar = Ext.create("Ext.toolbar.Toolbar", {
			dock: "bottom",
			items: footerTbarItems
		});
		me.tbar = footerTbar;
	},

	/**
	 * 逆ジオコード結果の住所を更新します.
	 * @param result Googleジオコーダの1件分の検索結果
	 */
	updateAddress: function(result) {
		var me = this;
		// 住所を取得
		var addr = null;
		if(result instanceof String) {
			addr = result;
		}
		if(result instanceof Object) {
			addr = me.map.getFormattedAddress(result);
		}

		// ラベル更新
		me.updateResultAddress(addr);
	},

	/**
	 * 住所検索結果フィールドのラベルを更新する
	 * @param {String} addr ラベルに設定する文字列
	 */
	updateResultAddress: function(addr) {
		var me = this;
		// ラベル更新
		me.resultAddressField.setText(addr);
		// 最初はダミーのtextが入っていて、非表示になっていることがあるので、表示させる
		if(me.resultAddressField.isHidden()) {
			me.resultAddressField.show();
		}
		if(me.resultAddressFieldTip==null) {
			me.resultAddressFieldTip = Ext.create('Ext.tip.ToolTip', {
				target: me.resultAddressField.getEl()
			});
		}
		me.resultAddressFieldTip.update(addr);
	},

	/**
	 * 「地図を移動」ボタンを実行します.
	 */
	fireJumpAddressButton: function() {
		var me = this;
		var map = me.map;
		var olmap = map.map;
		var addressField = me.addressField;
		var address = addressField.getValue();

		// MGRSコードによる移動
		var isMGRSFormat = /^\d\d?[a-zA-Z][a-zA-Z][a-zA-Z](\d*)$/;
		var result = isMGRSFormat.exec(address.replace(/\s+/g, "")); // スペースを除去して判定

		if(result==null) {
			isMGRSFormat = /^(\d*)$/;
			result = isMGRSFormat.exec(address.replace(/\s+/g, ""));

			if(result != null) {
				var left = map.ecommaps[0].initExtent.left;
				var bottom = map.ecommaps[0].initExtent.bottom;
				var right = map.ecommaps[0].initExtent.right;
				var top = map.ecommaps[0].initExtent.top;
				var initExtent = new OpenLayers.Bounds(left,bottom,right,top);
				//var center = initExtent.getCenterLonLat();
				// 現在表示している地図の中心
				var center = map.getCenter();
				var wkt = "POINT(" + center.lon + " " + center.lat + ")";

				var url = SaigaiTask.contextPath + "/page/map/getGrid/";
				jQuery.ajax(url, {
					type: "get",
					dataType: "json",
					async: false,
					data: {
						wkt: wkt
					},
					success: function(data) {
						result[0] = new Array();
						result[0].push(data.wkt.slice(0, data.wkt.indexOf(" "))+result[0]+result[1]);

						//UTMグリッドで使われるアルファベット
						var UTMalphabets = new Array();
						for(var i = 'A'.charCodeAt(0); i <= 'Z'.charCodeAt(0); i++) {
							// IとOは1と0と間違えやすいため欠番
							if(String.fromCodePoint(i) != "I" && String.fromCodePoint(i) != "O")
								UTMalphabets.push(String.fromCodePoint(i));
						}
						var alphabet1 = result[0][0].substr(3, 1);
						var alphabet2 = result[0][0].substr(4, 1);
						// ホーム範囲の中心点とその隣接するブロックの合計9ブロックが候補
						for(var i = 0; i < UTMalphabets.length;i++) {
							if(alphabet1 == UTMalphabets[i]) {
								result[0].push(result[0][0].replace(alphabet1, UTMalphabets[i-1]));
								result[0].push(result[0][0].replace(alphabet1, UTMalphabets[i+1]));
							}
							if(alphabet2 == UTMalphabets[i]) {
								result[0].push(result[0][0].replace(alphabet2, UTMalphabets[i-1]));
								result[0].push(result[0][0].replace(alphabet2, UTMalphabets[i+1]));
							}

						}
						for(var i = 0; i < UTMalphabets.length;i++) {
							if(alphabet1 == UTMalphabets[i]) {
								result[0].push(result[0][3].replace(alphabet1, UTMalphabets[i-1]));
								result[0].push(result[0][3].replace(alphabet1, UTMalphabets[i+1]));
								result[0].push(result[0][4].replace(alphabet1, UTMalphabets[i-1]));
								result[0].push(result[0][4].replace(alphabet1, UTMalphabets[i+1]));
							}
						}
						var nearest = 0;
						var no;
						// ホーム範囲の中心点から各候補地の距離を計測する。その中から最も近い点を採用する。
						for(i = 0; i < result[0].length; i++) {
							// MGRSコードから経緯度へ変換
							var mgrs = result[0][i];
							var json = map.api.mgrs2lonlat(mgrs);
							if(!!json.lon && !!json.lat) {
								function radians(deg) {
									return deg * Math.PI / 180;
								}
								var lat1 = json.lat;
								var lng1 = json.lon;
								var lat2 = center.lat;
								var lng2 = center.lon;
								var distance = 6378.14 * Math.acos(Math.cos(radians(lat1))*
										Math.cos(radians(lat2))*
										Math.cos(radians(lng2)-radians(lng1))+
										Math.sin(radians(lat1))*
										Math.sin(radians(lat2)));

								if(distance - nearest < 0 || i == 0) {
									nearest = distance;
									no = i;
								}
								else {
									continue;
								}
							}
						}
						result[0] = result[0][no];
					},
					error: function(data) {
						throw lang.__("An error occurred during the conversion MGRS code into coordinates.");
					}
				});
			}
		}
		if(result!=null) {
			do {
				// 最大桁数を超えていないか
				var numericalLocation = result[1];
				var maxDigits = 5;
				if(maxDigits*2 < numericalLocation.length) {
					alert(lang.__("The number of digits is too large.\n maximum number of digits of MGRS code is {0}.", maxDigits));
					break;
				}
				// 桁がそろっているか
				if(numericalLocation.length%2!=0) {
					alert(lang.__("MGRS code is invalid. Please align the number of digits."));
					break;
				}

				// MGRSコードから経緯度へ変換
				var mgrs = result[0];
				var json = map.api.mgrs2lonlat(mgrs);
				if(!!json.lon && !!json.lat) {
					// 単位がメートルなら
					var digits = json["mgrs_digits"];
					var zoom = map.map.getZoom();
					var mgrsControl = map.controls.mgrsControl;
					if(!!mgrsControl) zoom = mgrsControl.getZoomForPrecision(digits);
					// 移動
					//console.log("MGRS("+json.mgrs+") to Degrees("+json.lon+", "+json.lat+") zoom="+zoom);
					map.setCenter(new OpenLayers.LonLat(json.lon, json.lat), zoom);
					// ラベル更新
					me.updateResultAddress(lang.__("MGRS code: ")+json.mgrs);
				}
			} while(0);
			return;
		}

		//ランドマークデータを検索して、あればそれを使う
		var defResult = map.api.landmarkSearch(address);
		var landmarkFlag = false;//defferdを使っているので、done関数の外で結果の有無をチェック
		defResult.done(function(landmarkArr){
			if(landmarkArr.length>0){
				for(var cnt in landmarkArr){
					var landmark = landmarkArr[cnt].landmark;
					var lon = landmarkArr[cnt].lon;
					var lat = landmarkArr[cnt].lat;
					// 移動
					//var zoom = map.map.getZoom();
					map.setCenter(new OpenLayers.LonLat(lon, lat), 16);//zoomはとりあえず16で固定
					// ラベル更新
					me.updateResultAddress(lang.__("Landmark:")+landmark);
					landmarkFlag = true;
					//TODO とりあえず先頭の1件のみを返す。複数の検索結果をリスト表示したい場合は、ここの処理を変更する。
					break;
				}
			}
		});
		//alert("landmarkFlag:"+landmarkFlag);
		if(landmarkFlag){return;}

		// 住所からジオコーディングして移動する
		map.moveAddress(address)
		.done(function(result) {
			me.updateAddress(result);
		});
	}
};
