/**
 * @class SaigaiTask.Map.util.CommonUtil
 * @static
 * @requires SaigaiTask/Map/util.js
 */
SaigaiTask.Map.util.CommonUtil = {
	/**
	 * Get the number of properties of an object.
	 * @param {Object} obj
	 * @return {Number}Number of properties
	 */
	length: function(obj) {
		var count = 0;
		if(obj!=null) {
			for(var key in obj) {
				if(key!=null) {
					count++;
				}
			}
		}
		return count;
	},

	/**
	 * Automatically load unloaded Javascript libraries.
	 * @param {String} contextPath コンテキストパス
	 */
	loadLibrary: function(contextPath) {
		var server = location.href.substring(0, location.href.indexOf(contextPath)) + contextPath;

		var libs = [];
		if(typeof FalUtil=='undefined') {
			libs.push(server+'/assets2/js/falutil.js');
		}
//		if(typeof jsts=='undefined') {
//			if(typeof javascript=='undefined') {
//				libs.push('/jsts/lib/javascript.util.js');
//			}
//			libs.push('/jsts/lib/jsts.js');
//		}
		if(typeof Ext=='undefined') {
			libs.push(server+"/extjs/ext-all.js");
		}

		for(var idx in libs) {
			var lib = libs[idx];
			var jsElement = document.createElement("script");
			jsElement.type = "text/javascript";
			jsElement.src = lib;
			document.getElementsByTagName("head")[0].appendChild(jsElement);
			console.log('load javascript: '+lib);
		}
	},

	/**
	 * XMLHttpRequest を取得する
	 */
	getXHR: function() {
		//Win ie用
		if(window.ActiveXObject){
			try {
				//MSXML2以降用
				return new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				try {
					//旧MSXML用
					return new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e2) {
					return null;
				};
			};
		} else if(window.XMLHttpRequest){
			//Win ie以外のXMLHttpRequestオブジェクト実装ブラウザ用
			return new XMLHttpRequest();
		} else {
			return null;
		}
	},

	/** <span class="ja">xml内で利用できない文字をエスケープ</span><span class="en">Escape the characters which cannot be used in XML</span> */
	escapeXml : function(str)
	{
		return str.replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
	},

	//=====================================================================
	// GET リクエストパラメータ
	//=====================================================================
	getQueryString: function(){ return window.location.search.substring(1); },
	getParameterMap: function(query){
		var map = new Array();
		var params = query.split("&");
		for(var idx in params){
			var param = params[idx];
			var pos = param.indexOf("=");
			if(0<pos){
				var key = param.substring(0,pos);
				var val = param.substring(pos+1);
				map[key]=val;
			}
		}
		return map;
	},
	getParameter: function(name){
		var query = SaigaiTask.Map.util.CommonUtil.getQueryString();
		var paramMap = SaigaiTask.Map.util.CommonUtil.getParameterMap(query);
		return paramMap[name];
	}
};
