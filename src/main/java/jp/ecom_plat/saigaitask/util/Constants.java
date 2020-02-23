/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

public class Constants {
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	/** 災害種別 */
	/** @deprecated */
	public static final Integer DISASTER_FLOOD = 1;
	/** @deprecated */
	public static final Integer DISASTER_EARTHQUAKE = 2;
	/** @deprecated */
	public static final Integer DISASTER_VOLCANO = 3;

	/** 気象庁XML */
	public static final Integer METEO_KISHOUKEIHOUCHUIHOU = 1;
	public static final Integer METEO_SHINDOSOKUHOU = 2;
	public static final Integer METEO_TSUNAMIKEIHOUCHUIHOU = 3;
	public static final Integer METEO_SHINGENSHINDOJOUHOU = 4;
	public static final Integer METEO_SHITEIKASENKOUZUIYOHOU = 5;
	public static final Integer METEO_DOSYASAIGAIKEIKAIJOUHOU = 6;
	public static final Integer METEO_KIROKUTEKITANJIKANOOAMEJOUHOU = 7;
	public static final Integer METEO_TASTUMAKICHUUIJOUHOU = 8;
	public static final Integer METEO_FUNKAKEIHOUYOHOU = 9;
	public static final Integer METEO_KINKYUJISHINSOKUHOU = 10;
	public static final Integer JALERT_KOKUMINHOGOJYOUHOU = 11;
	public static final Integer JALERT_KINKYURENRAKU = 12;


	/** テロップ種別 */
	public static final Integer TELOPTYPE_KISHOUKEIHOUCHUIHOU = 1;
	public static final Integer TELOPTYPE_SHINDOSOKUHOU = 2;
	public static final Integer TELOPTYPE_SHINGENSHINDOJOUHOU = 3;
	public static final Integer TELOPTYPE_TSUNAMIKEIHOUCHUIHOU = 4;
	public static final Integer TELOPTYPE_SHITEIKASENKOUZUIYOHOU = 5;
	public static final Integer TELOPTYPE_DOSYASAIGAIKEIKAIJOUHOU = 6;
	public static final Integer TELOPTYPE_KIROKUTEKITANJIKANOOAMEJOUHOU = 7;
	public static final Integer TELOPTYPE_TASTUMAKICHUUIJOUHOU = 8;
	public static final Integer TELOPTYPE_FUNKAKEIHOUYOHOU = 9;
	public static final Integer TELOPTYPE_KINKYUJISHINSOKUHOU = 9;
	public static final Integer TELOPTYPE_KOKUMINHOGOJYOUHOU = 11;
	public static final Integer TELOPTYPE_KINKYURENRAKU = 12;

	/** データベースのカラムの型
	 * 他にもあるが、現状使っているものだけ定義。必要に応じて追加する */
	public static final String DATABASE_COLTYPE_BOOLEAN = "bool";
	public static final String DATABASE_COLTYPE_BIGINT = "int8";
	public static final String DATABASE_COLTYPE_INT = "int4";
	public static final String DATABASE_COLTYPE_FLOAT = "float4";
	public static final String DATABASE_COLTYPE_DOUBLE = "float8";
	public static final String DATABASE_COLTYPE_TEXT = "text";
	public static final String DATABASE_COLTYPE_TIMESTAMP = "timestamp";

	/** 管理者の自治体ID */
	public static final long ADMIN_LOCALGOVINFOID = 0L;

	/** 日付フォーマット */
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

	//Jqgrid関連定数
	/** 操作種別 */
	public static final String JQG_OPER = "oper";
	/** 登録 */
	public static final String JQG_OPER_ADD = "add";
	/** 編集 */
	public static final String JQG_OPER_EDIT = "edit";
	/**  */
	public static final String JQG_OPER_DEL = "del";
	/**  一括削除 */
	public static final String JQG_OPER_DELALL = "delall";
	/** 検索実行時か否か */
	public static final String JQG_DO_SEARCH = "_search";
	/** 検索対象 */
	public static final String JQG_SEARCH_FIELD = "searchField";
	/** 比較演算子 */
	public static final String JQG_SEARCH_OPER = "searchOper";
	/** 検索文字列 */
	public static final String JQG_SEARCH_STRING = "searchString";
	/** 最初のページ番号 */
	public static final int JQG_FIRST_PAGE = 1;
	/** 現在ページ番号 */
	public static final String JQG_PAGER_PAGE = "page";
	/** 1ページの件数 */
	public static final String JQG_PAGER_ROWS = "rows";
	/** ソート項目名 */
	public static final String JQG_PAGER_SIDX = "sidx";
	/** ソート順（昇順 or 降順） */
	public static final String JQG_PAGER_SORD = "sord";
	/** 総結果件数 */
	public static final String JQG_PAGER_RECORDS = "records";
	/** 総ページ数 */
	public static final String JQG_PAGER_TOTAL = "total";
	/** グリッドデータロードが初回フラグ */
	public static final String JQG_LOADONCE = "loadonce";
	/** ユーザーデータ格納パラメータ */
	public static final String JQG_USERDATA = "userdata";
	/** SELECTタグ */
	public static final String JQG_SELECT_TAG = "selectTag";
	/** selectタグとするグリッドカラム名 */
	public static final String JQG_SELECT_TAG_COLUMN = "selectTagColumn";
	/** デコード値を保持しているテーブル名 */
	public static final String JQG_TABLE_NAME = "tableName";
	/** デコード値を保持しているテーブルのコードカラム */
	public static final String JQG_CODE_COLUMN = "codeColumn";
	/** デコード値を保持しているテーブルのデコードカラム */
	public static final String JQG_DECODE_COLUMN = "decodeColumn";
	/** 編集前シリアライズデータ */
	public static final String JQG_SERIALIZED_PRE_EDIT_DATA = "serializedPreEditData";
	/** 登録エンティティ */
	public static final String JQG_NEW_ENTITY = "newEntity";
	/** 編集エンティティ */
	public static final String JQG_EDIT_ENTITY = "editEntity";
	/** Ajax返却メッセージ */
	public static final String JQG_MESSAGE = "message";
	/** コピー対象ID */
	public static final String JQG_ALLCOPY_ID = "allCopyId";
	/** 全コピーフラグ */
	public static final String JQG_ALLCOPY = "isAllCopy";
	/** 親ID */
	public static final String JQG_PARENTID = "parentid";

	/** JDBCマネージャクラス名 */
	public static final String DB_JDBCMANAGER_CLASSNAME = "jdbcManager";
	/** エンティティパッケージ名 */
	public static final String ENTITY_PKG = "jp.ecom_plat.saigaitask.entity.db";
	/** データベースサービスパッケージ名 */
	public static final String DB_SERVICE_PKG = "jp.ecom_plat.saigaitask.service.db";
	/** データベースサービスサブクラスサフィックス */
	public static final String DB_SERVICE_SUFFIX = "Service";
	/** 件数縛りなし */
	public static final int NON_LIMIT = 0;
	/** 検索開始件数番号なし */
	public static final int NON_SELECT_OFFSET = 0;
	/** 昇順 */
	public static final String ASC = "ASC";
	/** 降順 */
	public static final String DESC = "DESC";

	// mori add 20130904
	/** 公共情報コモンズ送信状況 */
	public static class PublicCommonsSendStatus{
		/** 未送信 */ public static final String UNSENT = "UNSENT";
		/** 送信中 */ public static final String SENDING = "SENDING";
		/** 送信済 */ public static final String SEND = "SEND";
		/** 失敗   */ public static final String FAILED = "FAILED";
	}

	/** 自動発報有効 */
	public static final String ISSUE_AUTO_VALID() { return lang.__("Auto alarm on"); }
	/** 自動発報無効 */
	public static final String ISSUE_AUTO_INVALID() { return lang.__("Auto alarm off"); }

	// 避難情報マスタ
	/** 避難情報解除 */
	public static final Integer ISSUE_CANCEL = -1;
	/** 避難準備情報(避難情報マスタ) */
	public static final Integer ISSUE_PREPARE = 1;
	/** 避難勧告情報(避難情報マスタ) */
	public static final Integer ISSUE_ADVISE = 2;
	/** 避難指示情報(避難情報マスタ) */
	public static final Integer ISSUE_INDICATE = 3;

	/** 避難情報解除 */
	public static final String ISSUE_CANCEL_NAME() { return lang.__("Cancel<!--4-->"); }
	/** 避難情報発令 */
	public static final String ISSUE_HATUREI_NAME() { return lang.__("Announcement"); }
	/** 避難準備情報(避難情報マスタ) */
	public static final String ISSUE_PREPARE_NAME() { return lang.__("Evacuation preparation info"); }
	/** 避難準備情報(公共情報コモンズ) */
	public static final String ISSUE_PREPARE_PCOMMONS_NAME() { return lang.__("Evacuation preparation"); }
	/** 避難勧告(避難情報マスタ) */
	public static final String ISSUE_ADVISE_NAME() { return lang.__("Evacuation advisory"); }
	/** 避難指示(避難情報マスタ) */
	public static final String ISSUE_INDICATE_NAME() { return lang.__("Evacuation order"); }
	/** 全域 */
	public static final String ALL_AREA() { return lang.__("Whole district"); }

	/** 避難所開設状況(本システムの開設状況) */
	public static final String SHELTER_ESTABLISHMENT_INDICATE_NAME() { return lang.__("Already instructed to open"); }
	public static final String SHELTER_ESTABLISHMENT_COMPLETION_NAME() { return lang.__("Already opened"); }
	public static final String SHELTER_ESTABLISHMENT_IMPOSSIBLE_NAME() { return lang.__("Non-opening"); }
	public static final String SHELTER_ESTABLISHMENT_NOT_NAME() { return lang.__("Not opening"); }
	public static final String SHELTER_CLOSE_NAME() { return lang.__("Closing"); }
	public static final String SHELTER_ESTABLISHMENT_FULLTIME_NAME() { return lang.__("Permanent status"); }
	public static final String SHELTER_OTHER_NAME() { return lang.__("Unknown"); }

	/** 避難所開設状況(公共情報コモンズの開設状況) */
	public static final String SHELTER_CLOSE() { return lang.__("Closing"); }
	public static final String SHELTER_OPEN() { return lang.__("Opening"); }
	public static final String SHELTER_FULLTIME() { return lang.__("Permanent status"); }
	public static final String SHELTER_NOESTABLISH() { return lang.__("Not opening"); }

	/** 避難所種別 */
	public static final String SHELTER_TYPE_SHELTER() { return lang.__("Shelter"); }
	public static final String SHELTER_TYPE_URGENT() { return lang.__("Emergency shelter"); }
	public static final String SHELTER_TYPE_WELFARE() { return lang.__("Welfare shelter"); }
	public static final String SHELTER_TYPE_REGARD() { return lang.__("Informal shelter"); }
	public static final String SHELTER_TYPE_EXTRA() { return lang.__("Temporary shelter<!--2-->"); }
	public static final String SHELTER_TYPE_WIDE() { return lang.__("Evacuation district"); }
	public static final String SHELTER_TYPE_TEMPORARY() { return lang.__("Temporary shelter"); }

	/** 本部設置状況解散 */
	public static final String ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME() { return lang.__("Dissolution"); }


	/** パス区切り文字 */
	public static final String COMMON_PATHSEPARATOR = "/";

	/** IDと名前の結合文字 */
	public static final String COMMON_CONCATIDNAME = ":";

	/** セッションパラメータ 地図表示／非表示*/
	public static final String SESSIONPARAM_MAPVISIBLE = "mapvisible";
	public static final String MAPVISIBLE_VISIBLE = "1";
	public static final String MAPVISIBLE_UNVISIBLE = "0";

	/** セッションパラメータ 言語情報*/
	public static final String SESSIONPARAM_LANG = "lang";


	/** 自治体種別 県*/
	public static final int LOCALGOVINFOTYPE_PREF = 1;
	/** 自治体種別 市区町村*/
	public static final int LOCALGOVINFOTYPE_CITY = 2;

	/** 目標物データ CSVファイル 拡張子 */
	public static final String LANDMARKDATA_CSVFILE_EXT = "csv";
	/** 目標物データ CSVファイル 文字コード */
	public static final String LANDMARKDATA_CSVFILE_ENCODE = "SHIFT_JIS";
	/** 目標物データ CSVファイル デリミタ */
	public static final String LANDMARKDATA_CSVFILE_DELIMITER = ",";
	/** 目標物データ CSVファイル カラム数 */
	public static final int LANDMARKDATA_CSVFILE_COLUMNS = 3;
	/** 目標物データ CSVファイル ランドマークデータ文字列カラムインデックス */
	public static final int LANDMARKDATA_CSVFILE_DATASTRIDX = 0;
	/** 目標物データ CSVファイル ランドマークデータ経度カラムインデックス */
	public static final int LANDMARKDATA_CSVFILE_LONIDX = 1;
	/** 目標物データ CSVファイル ランドマークデータ緯度カラムインデックス */
	public static final int LANDMARKDATA_CSVFILE_LATIDX = 2;
	/** 目標物データ CSVファイル 入れ替え */
	public static final String LANDMARKDATA_CSVFILE_SWAP = "1";
	/** 目標物データ CSVファイル 追加登録 */
	public static final String LANDMARKDATA_CSVFILE_APPEND = "2";

	/** 通信途絶 インポートモード */
	public static final String DISCONNECT_IMPORTMODE_TRACK = "1";
	public static final String DISCONNECT_IMPORTMODE_INFOANDTRACK = "2";

	/** 通信途絶 インポート画面遷移ステータス */
	public static final String DISCONNECT_IMPORTSTATUS_STEP1 = "1";
	public static final String DISCONNECT_IMPORTSTATUS_STEP2 = "2";
	public static final String DISCONNECT_IMPORTSTATUS_STEP3 = "3";

	/** 通信途絶 データ同期 */
	public static final String OAUTH_APPLICATION_NAME() { return lang.__("NIED disaster information sharing system surrogate server"); }
	public static final String OAUTH_TOKEN_SESSION_KEY = "disconnectOauthAccessToken";
	public static final String OAUTH_LOCALGOVINFOID_SESSION_KEY = "disconnectOauthLocalgovinfoid";
	public static final String OAUTH_CLOUDURL_SESSION_KEY = "disconnectOauthCloudurl";
	public static final String OAUTH_CODE_SESSION_KEY ="disconnectOauthCode";
	public static final String API_GETURL="api/v2/userInfos/1/";
	public static final String JSON_KEY_STATUS="status";
	public static final String JSON_KEY_STATUSSUCCESS="success";
	public static final String JSON_KEY_STATUSERROR="error";
	public static final String JSON_KEY_ERRORMESSAGE="errormessage";

	/** 時系列対応 書類作成 */
	public static final String EXCELLIST_BASEDIR="/WEB-INF/excellisttemplate/";
	public static final String MENUINFOID_EXCELLIST_FILENAMEPREFIX = "excellist_";
	public static final String EXCELLIST_TAGSTARTSTR = "<%";
	public static final String EXCELLIST_TAGENDSTR = "%>";
	public static final int TAGCHECK_NOTAG = 0;
	public static final int TAGCHECK_SUCCESS = 1;
	public static final int TAGCHECK_NOSTART = 2;
	public static final int TAGCHECK_NOEND = 3;
	public static final int TAGCHECK_INVALID = 4;
	public static final String EXCELLIST_USERINPUTPREFIX = "userinputs";
	public static final String MENUINFOID_EXCELLIST_FILENAMEPATTERN = MENUINFOID_EXCELLIST_FILENAMEPREFIX + "menuinfoid_[0-9]+_template.xlsx";

	/** ロゴ画像ファイル 保存ディレクトリルート */
	public static final String LOGOIMAGEFILE_BASEDIR="/images/logo/";

	/** タスク種別情報 テンプレートメニューフラグ */
	public static final int MENUTASKTYPE_TEMPLATE_NONE   = 0;
	public static final int MENUTASKTYPE_TEMPLATE_PARENT = 1;
	public static final int MENUTASKTYPE_TEMPLATE_CHILD  = 2;
}
