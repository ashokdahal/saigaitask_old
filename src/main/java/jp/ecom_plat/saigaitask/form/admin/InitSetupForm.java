/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin;

import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongType;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.validator.Required;

/**
 * 自治体セットアッパーのアクションフォーム
 */
@lombok.Getter @lombok.Setter
public class InitSetupForm {

	/** 自治体ID */
	@LongType
	public String id = "";

//	//eコミ関連
//	@Required
//	public String url = "";
//
//	public String consumerKey = "";
//
//	public String consumerKeySecret = "";

	//自治体関連
	/** システム名称 */
	public String systemname;

	/** ドメイン名 */
	@Required
	public String domain = "";

	/** 自治体種別 */
	@Required
	@IntegerType
	public String localgovtypeid = "1";

	/** 県名 */
	@Required
	public String pref = "";

	/** 県コード */
	@Required
	public String prefcode = "";

	/** 市区町村名 */
	public String city = "";

	/** 市区町村コード */
	public String citycode = "";

	/** 予備（区、自治会） */
	public String section = "";

	/** 自動発報フラグ */
	public String autostart = "false";

	/** アラーム確認の間隔(秒) */
	@Required
	public int alarminterval = new PageDto().getAlarmInterval() / 1000;

	/** 備考 */
	public String note = "";

	/** 有効・無効 */
	@Required
	public String valid = "true";

	/** システム管理者向けの自治体切り替え用自治体ID */
	@LongType
	public String localgovinfoid = null;

	// 管理班は推奨設定に含まれているため作成しない。
//	@Required
//	public String name = "";

	/** パスワード */
	//@Required
	public String password = "";

	/** eコミマップユーザアカウント */
	//@Required
	public String ecomuser = "";

//	@Required
//	public String ecompass = "";

	/** 管理権限 */
	@Required
	public String admin = "true";

//	@Required
//	public String editable = "true";
//
//	@Required
//	public String visible = "true";
//
//	@Required
//	@IntegerType
//	public String disporder = "1";

	/**
	 * 自治体登録処理成功フラグ
	 */
	public Boolean success = null;

	/**
	 * インポートモード
	 * 0: インポートしない
	 * 1: 自治体設定のみインポート
	 * 2: 自治体設定とシステムマスタのインポート
	 */
	public Integer importmode = 0;

	/** 設定テンプレートファイル */
	public MultipartFile templateFile;

	/** システムバージョン */
	public String systemVersion;

	/** アップロード済みの設定テンプレートファイルバージョン */
	public String templateFileVersion;

	/** アップロード済みの設定テンプレートファイル名 */
	public String templateFileName;

	/**
	 * 0: バージョンの一致を確認する
	 * 1: そのまま登録する
	 */
	public Integer templateFileMode = 0;

	/**
	 * 設定テンプレートファイルの確認モード
	 */
	public boolean confirmTemplateFile = false;

	/**
	 * リストアモード
	 * 0: 新規作成
	 * 1: 選択
	 */
	public int restoremode;

	/** マスタマップ復元先サイトID */
	public int mapRestoreCommunityid;
	/** マスタマップ復元先グループID */
	public int mapRestoreGroupid;

	/** サイト名称 */
	public String siteName;

	/** 自治体インポートリクエストID */
	public String requestid;
}
