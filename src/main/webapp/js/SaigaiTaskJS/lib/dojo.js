/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/**
 * eコミマップのライブラリが dojo を利用しているため、
 * 必要な関数だけここで定義する。
 * @class dojo
 */
if(typeof dojo=="undefined") {
	dojo = {};
}

if(typeof dojo.fromJson=="undefined") {
	dojo.fromJson=JSON.parse;
}
if(typeof dojo.toJson=="undefined") {
	dojo.toJson=JSON.stringify;
}

if(typeof dojo.trim=="undefined") {
	dojo.trim=$.trim;
}

if(typeof dojo.clone=="undefined") {
	dojo.clone=function(target){
		return $.extend({}, target);
	};
}

if(typeof dojo.isIE=="undefined") {
	var detectIE = function() {
		var ua = window.navigator.userAgent;

		  var msie = ua.indexOf('MSIE ');
		  if (msie > 0) {
		    // IE 10 or older => return version number
		    return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
		  }

		  var trident = ua.indexOf('Trident/');
		  if (trident > 0) {
		    // IE 11 => return version number
		    var rv = ua.indexOf('rv:');
		    return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
		  }

		  var edge = ua.indexOf('Edge/');
		  if (edge > 0) {
		    // Edge (IE 12+) => return version number
		    return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
		  }
		  return 0;
	}
	dojo.isIE = detectIE();
}

if(typeof dojo.isChrome=="undefined") {
	var getChromeVersion = function() {     
		var raw = navigator.userAgent.match(/Chrom(e|ium)\/([0-9]+)\./);
		return raw ? parseInt(raw[2], 10) : 0;
	}
	dojo.isChrome = getChromeVersion();
}

if(typeof dojo.create=="undefined") {
	dojo.create = function(tag, attrs, refNode, pos) {
		var e = $("<"+tag+">");
		if(typeof attrs!="undefined") {
			if(typeof attrs.style!="undefined") {
				e.attr("style", attrs.style);
			}
			if(typeof attrs.innerHTML!="undefined") {
				e.html(attrs.innerHTML);
			}
			if(typeof attrs.className!="undefined") {
				e.addClass(attrs.className);
			}
		}
		return e.get(0);
	}
}

if(typeof dojo.keys=="undefined") {
	dojo.keys = {};
	dojo.keys.LEFT_ARROW = 37
	dojo.keys.UP_ARROW = 38
	dojo.keys.RIGHT_ARROW = 39
	dojo.keys.DOWN_ARROW = 40
	dojo.keys.DELETE = 46
}
