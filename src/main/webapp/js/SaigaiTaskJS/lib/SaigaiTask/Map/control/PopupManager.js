/**
 * ポップアップを管理するクラスです.
 * @requires SaigaiTask/Map/control.js
 */
SaigaiTask.Map.control.PopupManager = new OpenLayers.Class({

	/**
	 * @type {SaigaiTask.Map}
	 */
	stmap: null,

	/**
	 * ポップアップの配列.
	 * z-Index順で格納します.
	 * @type {Array<OpenLayers.Popup>}
	 */
	popups: null,

	initialize: function(stmap, options) {
		var me = this;
		me.stmap = stmap;
		stmap.popupManager = me;
		me.popups = [];
	},

	/**
	 * ポップアップを追加します.
	 * @param {OpenLayers.Popup} popup
	 */
	add: function(popup) {
		var me = this;
		me.popups.push(popup);
		// ポップアップクリック時に最前面へ移動する
		$(popup.groupDiv).children().click(function() {
			me.toFrontend(popup);
		});
	},

	/**
	 * ポップアップをピン留めします.
	 */
	pin: function(popup) {
		popup.pinned = true;
	},

	/**
	 * ポップアップのピン留めを外します.
	 */
	unpin: function(popup) {
		popup.pinned = false;
	},

	/**
	 * ポップアップを閉じます.
	 */
	close: function(popup) {
		var me = this;
		var olmap = me.stmap.map;
		var deleteIdx = $.inArray(popup, me.popups);
		if(-1<deleteIdx) {
			// z-Indexを1つ下げる
			for(var idx=deleteIdx+1; idx<me.popups.length; idx++) {
				if(deleteIdx!=null) {
					var target = me.popups[idx];
					me.setZIndex(target, me.getZIndex(target)-1);
				}
			}
			// 削除
			me.popups.splice(deleteIdx, 1);
		}
		olmap.removePopup(popup);
	},

	/**
	 * ポップアップをすべて閉じます.
	 * @param force ピン留めも閉じる
	 */
	closeAll: function(force) {
		var me = this;
		var olmap = me.stmap.map;
		var closePinned = (force==true);
		var newPopups = [];
		while(0<me.popups.length) {
			var popup = me.popups.shift();
			var close = true;
			// ピン留めしてなかったら閉じる
			if(force!=true) {
				close = (popup.pinned!=true);
			}
			if(close) {
				me.close(popup);
			}
			else {
				newPopups.push(popup);
			}
		}
		me.popups = newPopups;
	},

	setZIndex: function(popup, zIndex) {
		return $(popup.div).css("z-Index", zIndex);
	},

	/**
	 * @return {Number}
	 */
	getZIndex: function(popup) {
		return Number($(popup.div).css("z-Index"));
	},

	/**
	 * ポップアップを最前面に移動する.
	 */
	toFrontend: function(popup) {
		var me = this;
		var targetIdx = $.inArray(popup, me.popups);
		// 最前面じゃなかったら
		if(-1<targetIdx && targetIdx<me.popups.length-1) {
			// z-Indexを1つ下げる
			for(var idx=targetIdx+1; idx<me.popups.length; idx++) {
				var sequencePopup = me.popups[idx];
				me.setZIndex(sequencePopup, me.getZIndex(sequencePopup)-1);
			}
			// 最前面に移動する
			me.setZIndex(popup, me.getZIndex(sequencePopup)+1);
			me.popups.splice(targetIdx, 1);
			me.popups.push(popup);
		}
	},

	/**
	 * ポップアップを最背面に移動する.
	 */
	toBackend: function(popup) {
		var me = this;
		var targetIdx = $.inArray(popup, me.popups);
		// 最背面じゃなかったら
		if(targetIdx!=0) {
			// z-Indexを1つあげる
			for(var idx=targetIdx-1; 0<=idx; idx--) {
				var sequencePopup = me.popups[idx];
				me.setZIndex(sequencePopup, me.getZIndex(sequencePopup)+1);
			}
			// 最背面に移動する
			me.setZIndex(popup, me.getZIndex(sequencePopup)-1);
			me.popups.splice(targetIdx, 1);
			me.popups.unshift(popup);
		}
	}
});
