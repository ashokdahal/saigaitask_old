/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

SaigaiTask.Page = {

	/**
	 * CSVダイアログ
	 * @type {jQuery.Dialog}
	 */
	csvDialog: null,

	/**
	 * PDFダイアログ
	 * @type {jQuery.Dialog}
	 */
	pdfDialog: null,

	/**
	 * マウスカーソル位置
	 */
	mouse: {
		x: null,
		y: null
	},

	initGetMousePosition: function() {
		document.addEventListener('mousemove', function(e){
			SaigaiTask.Page.mouse.x = e.clientX || e.pageX;
			SaigaiTask.Page.mouse.y = e.clientY || e.pageY;
		}, false);
	},

	/**
	 * マウスカーソルの現在位置を取得します.
	 */
	getMousePosition: function() {
		return SaigaiTask.Page.mouse;
	},

	/**
	 * ダイアログやウィンドウが１つでも開いているかどうかチェックします.
	 */
	anyDialogIsOpen: function() {
		// ダイアログが表示されているかチェック
		var anyDialogIsOpen=false;
		// check jQuery UI Dialog
		$(".ui-dialog-content").each(function() {
			if($(this).dialog("isOpen")==true)
				anyDialogIsOpen=true;
		});
		// check jqgrid Dialog
		$(".ui-jqdialog").each(function() {
			if($(this).css("display")=="block") {
				anyDialogIsOpen=true;
			}
		});
		// check Ext.window.Window
		Ext.WindowMgr.each(
			function(win) {
				if(win.isVisible()) {
					anyDialogIsOpen = true;
				}
			}
		);
		return anyDialogIsOpen;
	}
};

(function() {
	SaigaiTask.Page.initGetMousePosition();
})();
