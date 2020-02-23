SaigaiTask.Map.Icon = function() {
	this.initialize.apply(this, arguments);
};

SaigaiTask.Map.Icon.prototype = {

	baseURL: null,

	/**
	 * アイコンMap
	 * @type {Object<String, String>} <アイコン名, URL>
	 */
	icon: {

		/**
		 * 追加アイコンを取得するためのURL
		 * @type {String}
		 */
		addIconURL: "/icons/add.png",

		/**
		 * 除外アイコン
		 */
		removeIconURL: "/icons/remove.png",

		/**
		 * 削除アイコン
		 */
		deleteIconURL: "/icons/delete.png",

		/**
		 * 編集アイコン
		 */
		editIconURL: "/icons/edit.png",

		/**
		 * 地図編集アイコン(メモ描画アイコン)
		 */
		mapEditIconURL: "/icons/map_edit.png",

		/**
		 * 元に戻す
		 */
		undoIconURL: "/icons/undo.png",

		/**
		 * やり直す
		 */
		redoIconURL: "/icons/redo.png",

		/**
		 * 地図上の編集アイコン
		 * @type {String}
		 */
		editingIconURL: "/icons/editing.png",

		/**
		 * 地図上の選択アイコン
		 * @type {String}
		 */
		selectingIconURL: "/icons/selecting.png",

		/**
		 * フィーチャ編集時の頂点アイコンを取得するためのURL
		 * @type {String}
		 */
		pointnodeIconURL: "/icons/pointnode.png",

		/** 印刷アイコン */
		printIconURL: "/icons/print.png",

		/** 地図画像出力アイコン */
		downloadMapIconURL: "/icons/map_down.png",

		/** ダウンロードアイコン */
		downloadIconURL: "/icons/download.png",

		/** 範囲アイコン */
		rangeIconURL: "/icons/range.png",

		/** 印刷設定読み込みアイコン */
		folderOpenIconURL: "/icons/folder_open.png",

		/** 閉じるアイコン */
		closeIconURL: "/icons/close.png",

		/** リロードアイコン */
		reloadIconURL: "/icons/reload.png",

		/**
		 * フィーチャ編集時の頂点追加アイコンを取得するためのURL
		 * @type {String}
		 */
		verticeIconURL: "/icons/vertice.png",

		/**
		 * 2画面表示アイコン
		 */
		submapIconURL: "/images/submap.png",

		/**
		 * 同期アイコン
		 */
		syncIconURL: "/images/CircledSync.png",

		/**
		 * マーカーアイコン
		 */
		markerIconURL: "/icons/geosilk/marker.png",

		/**
		 * 新しいウィンドウで開くアイコン
		 * @type {String}
		 */
		newwinIconURL: "/images/newwinicon.gif",

		/**
		 * MGRSアイコン
		 */
		mgrsIconURL: "/icons/mgrs.png",

		/**
		 * 計測アイコン
		 */
		rulerIconURL: "/icons/geosilk/ruler_triangle.png",

		/**
		 * 透明度変更アイコン
		 */
		transparencyURL: "/icons/pen.png"
	},

	initialize: function(baseURL) {
		var me = this;
		if(typeof baseURL!="undefined") {
			me.baseURL = baseURL;
		}
	},

	getURL: function(name) {
		var me = this;
		return me.baseURL + me.icon[name];
	}
};