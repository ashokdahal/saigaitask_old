/**
 * 属性情報のクラスです.
 * @requires SaigaiTask/Map/Layer.js
 */
SaigaiTask.Map.Layer.AttrInfo = new OpenLayers.Class({

	attrId: null,
	dataExp: null,
	dataType: null,
	dataTypeId: null,
	dataTypeName: null,
	highlight: false,
	length: null,
	maxLength: null,
	name: null,
	nullable: null,
	status: null,

	/**
	 * 登録時時刻アップデートフラグ
	 * 登録時に現在時刻を自動で設定する
	 * @type {Boolean}
	 */
	updateInserted: false,

	/**
	 * 更新時時刻アップデートフラグ
	 * 更新時に現在時刻を自動で設定する
	 * @type {Boolean}
	 */
	updateModified: false,

	/**
	 * dataType が文字列の場合に、
	 * 住所ボタンを表示するかどうかのフラグ
	 * @type {Boolean}
	 */
	addAddressButton: false,

	/**
	 * ポップアップ時のツールバーボタンの設定
	 * URLはこの属性値が使われます.
	 * @type {Object}
	 */
	buttonData: null,
	
	/**
	 * コンストラクタ
	 * @param options
	 */
	initialize: function(options) {
		this.children = [];
		this.params = [];
		// オプションをコピー
		OpenLayers.Util.extend(this, options);

		this.searchable = this.visibility;
	}
});

/** 閲覧可、編集可 */
SaigaiTask.Map.Layer.AttrInfo.STATUS_DEFAULT = 0;
/** 閲覧可、編集不可 */
SaigaiTask.Map.Layer.AttrInfo.STATUS_READONLY = 1;
/** 閲覧不可、編集可？ */
SaigaiTask.Map.Layer.AttrInfo.STATUS_SEARCHHIDE = -1;
SaigaiTask.Map.Layer.AttrInfo.STATUS_DELETED = -100;
