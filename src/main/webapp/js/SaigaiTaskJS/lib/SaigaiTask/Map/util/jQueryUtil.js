/**
 * @class SaigaiTask.Map.util.jQueryUtil
 * @static
 * @requires SaigaiTask/Map/util.js
 */
SaigaiTask.Map.util.jQueryUtil = {
	//=====================================================================
	// Form 操作
	//=====================================================================
	/**
	 * フォームからパラメータを取得します.
	 * @param {String} selector
	 * @return {Array<String>}
	 */
	getFormParams: function(selector) {
		var form = $(selector);
		// form のパラメータを配列に保存する
		var params = new Array(); // パラメータ配列 {'name=value'}
		var tags = new Array("input","select");
		for(var k in tags){
			var param = $.param(form.find(tags[k]));
			params = params.concat(param.split("&"));
		}
		// チェックされていないチェックボックスはパラメータから削除する
		$.each(form.find("input[type='checkbox']"), function(){
			var cb = $(this);
			if(cb.is(":checked")==false){
				params.splice( jQuery.inArray($.param(cb), params),1);
			}
		});
		return params;
	},

	/**
	 * form をAjaxで送信する
	 * input, select のみ対応。
	 * @param selector フォームのセレクタ
	 * @return responseText
	 */
	submitForm: function(selector, submitOption){
		// オプション設定
		var option = {
			async: false,
			data: SaigaiTask.Map.util.jQueryUtil.getFormParams(selector).join("&") // パラメータ文字列 name=value&name=value
		};
		SaigaiTask.Map.extend(option, submitOption);

		// メソッドに合わせて URL とデータを準備する
		var form = $(selector);
		option.url = form.attr("action");
		option.type = form.attr("method");
		return $.ajax(option).responseText;
	},

	//=====================================================================
	// CSS
	//=====================================================================
	/**
	 * 要素のvisibilityプロパティをセットする
	 * visibilityを指定しない場合は現在の値を取得する
	 */
	visibility: function(selector,visibility){
		var name = 'visibility';
		if( typeof visibility=='undefined') return $(selector).css(name);
		else return $(selector).css(name,(visibility?'visible':'hidden'));
	}
};

