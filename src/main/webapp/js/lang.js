/** JSPソースと同じ呼び方(lang.getString(""))にするため関数定義 */
// lang.getString = function(string) {
// 	return lang_json_obj[string];
// };


/** JSPソースと同じ呼び方(lang.__("",...))にするため関数定義 */
var lang = new Object();
lang.__ = function(string, param1, param2, param3, param4) {
	if (lang_json_obj[string])
		string = lang_json_obj[string];
	else
		string = string.replace(/<!--[0-9]*-->$/,"");

	if (typeof param1 != 'undefined') {
		string = string.replace("{0}", param1);
		if (typeof param2 != 'undefined') {
			string = string.replace("{1}", param2);
			if (typeof param3 != 'undefined'){
				string = string.replace("{2}", param3);
				if (typeof param4 != 'undefined')
					string = string.replace("{3}", param4);
			}
		}
	}
	return string;
};
