/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

function tb(form, ref) {
	//Formの値をGETで送信したあと、thickboxで開く
	  //var query2 = $(form.name).serialize();
	  //alert(query2);
	  //ref += query2;
	  for (var i = 0; i < form.elements.length; i++) {
		  if (form.elements[i].type == "radio" && form.elements[i].checked == false)
			  continue;
		  ref += "&"+form.elements[i].name;
		  ref += "="+encodeURIComponent(form.elements[i].value);
		  //ref += "="+form.elements[i].value;
	  }

	  tb_show('', ref, null);
}

function selgroup(sel, name, url) {
	var gid = sel.options[sel.selectedIndex].value;
	var $usersel = $("#"+name);

	$usersel.empty();
	$.ajax(url+gid, {
		success:function( data, textStatus ) {
			$usersel.append(data);
		}, dataType: 'html'
	});
}

function selgroup2(sel, name, name2, url) {
	var gid = sel.options[sel.selectedIndex].value;
	var $usersel = $("#"+name);
	var $usersel2 = $("#"+name2);

	$usersel.empty();
	$usersel2.empty();
	$.ajax(url+gid, {
		success:function( data, textStatus ) {
			$usersel.append(data);
			var val = $("#"+name+" option:selected");

			$.ajax(url+val[0].value, {
				success:function( data, textStatus ) {
					$usersel2.append(data);
				}, dataType: 'html'
			});
		}, dataType: 'html'
	});
}

function selgroup3(radio, name, name2, url) {
	var gid = radio.value;
	var $usersel = $("#"+name);
	var $usersel2 = $("#"+name2);

	$usersel.empty();
	$usersel2.empty();
	$.ajax(url+gid, {
		success:function( data, textStatus ) {
			$usersel.append(data);
			var val = $("#"+name+" option:selected");

			$.ajax(url+val[0].value, {
				success:function( data, textStatus ) {
					$usersel2.append(data);
				}, dataType: 'html'
			});
		}, dataType: 'html'
	});
}

var FalUtil = {

	/**
	 * 写真の元ファイルをウィンドウで表示する
	 */
	showImageWindow: function(oneUrl, oneTitle){
		var image = new Image();
		var newW;
		var newH;
		var win;
		var winSize = 500; // デフォルト
		var url;
		var title;
		var ret;
		Ext.onReady(function(){

			//ウィンドウサイズとImageオブジェクトから、適切な画像サイズを設定する
			function setNewSize(){
				var winSize = 500; // デフォルト
				//var minSize = Math.min(document.body.clientWidth, document.body.clientHeight);
				var minSize = Math.min(document.documentElement.clientWidth, document.documentElement.clientHeight);
				winSize = Math.max(winSize, minSize) - 100;

				var orgW = image.width;
				var orgH = image.height;
				var longSide = Math.max(orgW,orgH);
				if(longSide > winSize){
					var rate = winSize / longSide;
					newW = Math.floor(orgW * rate);
					newH = Math.floor(orgH * rate);
					//$("#imageOrg").attr("src",url);
				}else{
					newW = orgW;
					newH = orgH;
				}
				//console.log("orgW : "+orgW+", newW : "+newW+", orgH : "+orgH+", newH : "+newH);
			}

			//画像のスタイルでサイズを変更
			function setCssSize(){
				$("#imageOrg").css("width",newW);
				$("#imageOrg").css("height",newH);
			}

			//ウィンドウサイズとImageオブジェクトから、適切な画像サイズを設定する
			//既に開いていてリサイズする場合
			function resizeImage(){
				//ダイアログの大きさ
				var winW = $(win.body.dom).width();
				var winH = $(win.body.dom).height();

				var orgW = image.width;
				var orgH = image.height;
				var rate;
				if((orgW >= winW) && (orgH <= winH)){
					rate = winW / orgW;
				}else if((orgW <= winW) && (orgH >= winH)){
					rate = winH / orgH;
				//画像の両辺が、画面の両辺よりも大きい場合
				}else if((orgW >= winW) && (orgH >= winH)){
					if(orgW <= orgH){
						//小さい方に合わせる
						rate = winW / orgW;
					}else{
						rate = winH / orgH;
					}
				}else{
					rate=1;
				}
				newW = Math.floor(orgW * rate);
				newH = Math.floor(orgH * rate);
				//alert("new Image : "+newW+", "+newH);
				//alert("rate : "+rate);
				$("#imageOrg").css("width",newW);
				$("#imageOrg").css("height",newH);
				//$("#imageOrg").attr("src",url);
			}

			//画像のURLとタイトルをグローバル変数にセット
			url = oneUrl;
			title = oneTitle;

			// ブラウザのウィンドウサイズを求める
			/***
			注意！
			ウィンドウサイズを取得する対象のページに、
			<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
			の記載があると、ブラウザのレンダリングモードが「Quirks」から「標準（Standard)」に変わり、
			clientHeightがscrollMaxになってしまうので、差し支えなければDOCTYPEは削除する
			->document.body.clientWidth　から、document.documentElement.clientWidth　に、サイズ取得方法変更（2012.9.21）
			***/
			//var minSize = Math.min(document.body.clientWidth, document.body.clientHeight);
			var minSize = Math.min(document.documentElement.clientWidth, document.documentElement.clientHeight);
			winSize = Math.max(winSize, minSize) - 100;

			//画像のサイズを求め、ウィンドウサイズより大きい場合は縮小する
			//長辺の長さを基準に縮小率を求める
			image.src = url;
			//画像の読み込みに時間がかかるので、読み込まれるまで待ってからリサイズ処理
			var resize = function() {
				//alert(image.complete);
				if(image.complete){

					//画像読み込みが完了してから新しいサイズの取得
					setNewSize();

					// Window を表示する
					var handler = new Object();
					win = Ext.create('Ext.window.Window',{
						title: title,
						minWidth: 100, minHeight: 100,
						width: winSize, height: winSize,
						html: '<div style="width:100%; height:100%;"><center><img id="imageOrg" style="width:'+newW+'; height:'+newH+';" src='+url+'></img></center></div>',
						maximizable: true, minimizable: false,
						modal: true,
						fbar: [
							{type: 'button', text: lang.__('Close'), handler: function(){return handler.cancel();}}
						]
					});

					//画像の大きさの比率に合わせて、winサイズも変更する
					//alert(winSize);
					//alert(newW+", "+newH);
					if(newW == winSize){
						win.height = newH + 60;
					}else if(newH == winSize){
						win.width = newW + 10;
					//画像の両辺が、winSizeよりも小さい場合
					}else if ((newW < winSize) && (newH < winSize)){
						win.width = newW + 10;
						win.height = newH + 60;
					}

					win.show();
					//console.log(win);

					//画像サイズの変更、imgタグが表示されてからでないと変更が効かないので、win.showの後に実行
					setCssSize();

					//イベントとハンドラの定義
					win.addListener('resize', function(){resizeImage()});
					handler.cancel = function(){/*console.info('cancel');*/ win.close();};

					ret = {
						win: win
					}

					return true;
				}
				return false;
			}

			// リサイズできるまで繰り返す
			if(resize) {
				setTimeout(resize, 500);
			}

		});

		return ret;
	},

	/** <span class="ja">拡張子取得</span><span class="en">Get file extension</span> */
	getFileExt: function(fileName)
	{
		var ext = "";
		// <span class="ja">ドメイン名または最後が/の場合</span><span class="en">In case of domain name or ending with / character</span>
		if (fileName.match(/^https?:\/\/[^\/]+$/) || fileName.match(/.*\/$/)) return "html";
		var lastIdx = fileName.lastIndexOf('.');
		if (lastIdx > fileName.lastIndexOf('/') && lastIdx != -1) ext = fileName.substring(lastIdx+1);
		if (ext.length == 0 || ext.length > 5) {
			//if (fileName.match(/^https?:\/\/.*/)) return "html";
			ext = "file";
			//ext = "html";
		}
		return ext.toLowerCase();
	},

	/**
	 * オブジェクトをSAStrutsのクエリ文字列に変換する.
	 * @param {Object} obj
	 * @return {String} クエリ文字列
	 */
	toSAStrutsParam: function(obj) {
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
	},

	/**
	 * java.sql.TimestampのJSONオブジェクトを和暦に変換します（平成限定）．
	 */
	toWareki: function(timestamp) {
		var seireki = FalUtil.toTimestampText(timestamp, lang.__("M.d,yyyy 'at' HH:MM<!--3-->"));
		var year = seireki.split(lang.__("Year"));
		var heisei = Number(year[0]) - 1988;
		return lang.__("Heisei {0} /{1}", heisei, year[1]);
	},

	/**
	 * java.sql.TimestampのJSONオブジェクトを指定フォーマットに変換します.
	 * @param format
	 * MM: 月
	 * dd: 月における日
	 * HH: 一日における時(0～23)
	 * mm: 分
	 */
	toTimestampText: function(timestamp, format) {
		var date = new Date();
		date.setTime(timestamp.time);
		var text = format;
		var formats = ["yyyy", "MM", "dd", "HH", "mm"];
		for(var idx in formats) {
			var f = formats[idx];
			if(-1<text.indexOf(f)) {
				var length = null;
				if(1<f.length) length = f.length;
				switch(f) {
				case "yyyy":
					val = (1900+timestamp.year);
					break;
				case "MM":
					val = 1+timestamp.month;
					break;
				case "dd":
					val = timestamp.date;
					break;
				case "HH":
					val = timestamp.hours;
					break;
				case "mm":
					val = timestamp.minutes;
					break;
				}
				if(val!=null) {
					var rep = length!=null ? FalUtil.fill(val, length, '0') : val;
					text = text.replace(f, rep);
				}
			}
		}
		return text;
	},

	fill: function(text, length, ch) {
		return new Array(length - ('' + text).length + 1).join(ch) + text;
	},

	/**
	 * パラメータなしURLをThickbox用URLに変換します.
	 * @param url パラメータなしURL
	 * @param width 幅ピクセル値(optional)
	 * @param height 高さピクセル値(optional)
	 * @returns {String} Thickbox用URL
	 */
	getThickboxUrl: function(url, width, height) {
		if(typeof width=="undefined") width=950;
		if(typeof height=="undefined") height=700;
		return url+"?TB_iframe=true&width="+width+"&height="+height;
	},

	/**
	 * ThickboxのURLにパラメータを追加します.
	 * @param url ThickboxのURL
	 * @param param パラメータ文字列
	 * @returns パラメータを追加したURL
	 */
	addThickboxParam: function(url, param) {
		var urlNoQuery = url.split('TB_');
		if(param!=null && 0<param.length) {
			urlNoQuery[0] += param+"&";
			return urlNoQuery.join('TB_');
		}
		else return url;
	},
	
	downloadFile: function(config){
		config = config || {};
		var url = config.url,
		method = config.method || 'POST',// Either GET or POST. Default is POST.
		params = config.params || {};

		// Create form panel. It contains a basic form that we need for the file download.
		var form = Ext.create('Ext.form.Panel', {
			standardSubmit: true,
			url: url,
			method: method
		});

		// Call the submit to begin the file download.
		form.submit({
			target: '_blank', // Avoids leaving the page. 
			params: params
		});

		// Clean-up the form after 100 milliseconds.
		// Once the submit is called, the browser does not care anymore with the form object.
		Ext.defer(function(){
			form.close();
		}, 100);

	}
};

Masters = function() {
	this.initialize.apply(this, arguments);
};
Masters.prototype = {
	list: null,
	map: null,
	initialize: function(list) {
		var me = this;
		me.list = list;
		// マップを初期化
		me.map = {};
		for(var idx in list) {
			var master = list[idx];
			me.map[master.id] = master;
		}
	},
	get: function(id) {
		return this.map[id];
	},
	getOrderedList: function() {
		return this.list;
	}
};

