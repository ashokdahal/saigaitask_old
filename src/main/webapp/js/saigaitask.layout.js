/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
SaigaiTask.Layout = {
	body: null,
	bodyLayout: null,
	content: null,
	contentLayout: null,
	initAll: true,

	init: function(options) {
		var me = this;

		if(!!options) {
			if(!!options.body) me.body = options.body;
			if(!!options.content) me.content = options.content;
			if(typeof options.initAll!="undefined") me.initAll = options.initAll;
		}

		// body
		if(me.body==null) me.body = $("body");
		var body = me.body;

		// content
		if(me.content==null) me.content = $("#content")

		// レイアウトを ExtJS の Ready後にすべて初期化する。
		// false の場合は手動で呼び出すこと。
		if(me.initAll) {
			Ext.onReady(function(){
				me.initBodyLayout();
				me.initContentLayout();
				me.completeLayout();
			});
		}
	},

	initBodyLayout: function() {
		console.time("initBodyLayout");
		var me = this;
		// body レイアウト
		var body = me.body;
		var fullscreen = false;
		if(!!SaigaiTask.Page) {
			// 全画面表示の場合
			fullscreen = SaigaiTask.Page.fullscreen; 
			var othermenu = SaigaiTask.Page.othermenu;   // 自分以外のメニューの場合
			if(othermenu) fullscreen;
		}
		//Ext.onReady(function(){
		me.bodyLayout = window.bodyLayout = body.layout({
			onopen: function(){
				body.trigger("onclose");
			},
			onclose: function(){
				body.trigger("onclose");
			},
			onresize: function(){
				body.trigger("onresize");
			},
			north: {
				closable: false,
				resizable: false,
				// space between pane and adjacent panes - when pane is 'open' and "closed"
				spacing_open: 0,
				spacing_closed: 0
			},
			north__initHidden: fullscreen, // always hidden
			// フルスクリーンでもメニューPaneは非表示ではなく、折り畳み状態に変更。（別タスク・メニューに移動できるようにするため）
			//west__initHidden: fullscreen
			west: {
				initClosed: fullscreen,
				// space between pane and adjacent panes - when pane is 'open' and "closed"
				spacing_open: 6,
				spacing_closed: 20
			},
			//south__initHidden: fullscreen
			togglerLength_open: 100,
			togglerLength_closed: 100
		});
		me.body.trigger("oninitbodylayout");
		console.timeEnd("initBodyLayout");
		//});
		
		me.content.mask("Loading...");
	},

	initContentLayout: function() {
		var me = this;
		// contentレイアウト
		var content = me.content;
		if(0<content.length && content.children(".ui-layout-center").length==1) {
			// すでにレイアウトしていた場合は、破棄する
			if(!!me.contentLayout && !!me.contentLayout.destroy) me.contentLayout.destroy();
			Ext.onReady(function(){
				console.time("initContentLayout");
				// レイアウト初期化
				me.contentLayout = content.layout({
					onopen: function(){
						me.body.trigger("onclose");
					},
					onclose: function(){
						me.body.trigger("onclose");
					},
					onresize: function(){
						me.body.trigger("onresize");
					},
					north: {
						closable: false,
						resizable: false,
						// space between pane and adjacent panes - when pane is 'open' and "closed"
						spacing_open: 0,
						spacing_closed: 0
					},
					togglerLength_open: 100,
					togglerLength_closed: 100
				});
				$(".ui-layout-pane", content).css("border", "none");
				me.body.trigger("oninitcontentlayout");
				console.timeEnd("initContentLayout");
			});
		}
		me.content.unmask();
	},

	completeLayout: function() {
		var me = this;
		// レイアウト済みのページを表示
		Ext.onReady(function(){
			me.body.css("visibility", "visible");
			me.body.trigger("onvisible");
			console.timeEnd("layoutTime");
		});
	},
	
	/**
	 * 全画面表示
	 */
	fullscreen: function() {
		var me = this;
		var layouts = [me.bodyLayout, me.contentLayout];
		for(var idx in layouts) {
			var layout = layouts[idx];
			layout.hide("north");
			layout.hide("east");
			layout.hide("west");
			layout.hide("south");
		}
	}
};

/**
 * content部分だけ再読み込みします.
 * @param {String} url
 * @param {Function} callback
 */
function reloadContent(url, callback) {
	var params={};
	$('#content').mask("Loading...");
	var contentMain = $('#content_main');
	contentMain.load(url, params, function(responseText, textStatus, xhr) {
		$('#content').unmask();
		if(typeof callback=="function") callback();
		if(textStatus=="error") {
			contentMain.html(responseText);
			// 戻るボタンを非表示
			$("input[type='button']", contentMain).hide();
		}
    });
}