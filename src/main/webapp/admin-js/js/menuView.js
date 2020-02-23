/* muenuView.js */
var menu; // メニューを選択した時のリンク関数
// JSON からHTML へ変換する（CallBack）関数
function menuJsonCallback(data) {
	// alert("start sampleCallback");
	var w_level = 1;
	var w_exist = 0;
	var html = '<ul id="tree">';
	$.each(data, function() {
		// alert("start each>"+this.name);
		// 無効メニューであればスキップする
		if (this.valid == false)
			next;
		// レベルが上がった場合は</ul></li>を出力する
		else if (this.level < w_level) {
			html += '</li>';
			for (var i = w_level; i > this.level; i = i - 1) {
				w_exist -= 1;
				html += '</ul></li>';
			}
		}
		// レベルが下がった場合は<ul>タグを出力する
		else if (this.level > w_level) {
			w_exist += 1;
			html += '<ul>';
		}
		// 前行と同一レベルであれば </li>を出力する
		if (this.level == w_level && this.level != 1) {
			w_exist -= 1;
			html += '</li>';
		}
		// メニュー項目出力
		html += '<li>';
		//if (this.publicmode == 1 && this.url != "") {
		if (this.url != "") {
			html += '<a href="javascript:void(0)" ';
			html += 'onClick="menu(';
			html += "'";
			html += this.url.replace(/^\.\./, SaigaiTask.contextPath + '/admin');
			html += "'";
			html += ');return false;">';
		}
		if (this.level == 1) {
			html += '<span><strong>';
			html += this.name;
			html += '</strong></span>';
		} else {
			html += this.name;
		}
		//if (this.publicmode == 1 && this.url != "") {
		if (this.url != "") {
			html += '</a>';
		}
		w_exist += 1;
		w_level = this.level; // 読んだ行のレベルを保持
	});
	for (var i = w_level; i > 0; i = i - 1) {
		if (i > 1) {
			html += '</li></ul>';
		} else {
			html += '</li>';
		}
	}
	html += '</ul>';
	// alert(html);
	document.write(html);
	// $("#tree").html(html);
};

// メニューの全表示或は、折畳みを行うフック
$(function() {
	$("#tree").treeview({
		collapsed : true,
		animated : "medium",
		control : "#sidetreecontrol",
		persist : "location"
	});
});

// AjaxAction から管理メニュー情報をJSON形式で取得する。
function loadJSON(menuLink) {
	// メニューのリンク関数の設定
	menu = menuLink;
	$.ajax({
		async: false, // 同期通信
		url: SaigaiTask.contextPath + '/db/adminmenuInfo/load_json',
		type: 'GET',
		dataType: 'json',
		cache: false, // ブラウザにキャッシュさせません。
		error: function(){
			alert(lang.__("Failed to read json file."));
		},
		success: function(data){ // サーバからJSON形式のデータを受け取る
			// JSON 形式をHTMLへ変換する
			menuJsonCallback(data);
		}
	});
}
