/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin;

import jp.ecom_plat.saigaitask.validator.Required;

/**
 * クリアリングハウスデータ登録アクションフォーム
 */
@lombok.Getter @lombok.Setter

public class UploadckanDataForm {

	/** 自治体ID */
	public String localgovinfoid = "";

	//
	// メタデータに関する情報
	//
	/** 公開／非公開  */
	public String isOpen = "1";

	/** 情報種別ID */
	public String infoType = "";

	/** 依存関係のある情報種別ID */
	public String depInfoType = "";

	/** システム名 */
	public String systemName = "";

	/** システムURL */
	public String systemUrl = "";

	/** 言語 */
	public String language = "";

	/** 地図情報 */
	public String maxx = "";

	/** 地図情報 */
	public String maxy = "";

	/** 地図情報 */
	public String minx = "";

	/** 地図情報 */
	public String miny = "";

	/** 地図情報 */
	public String spatial = "";

	//
	// 地図データに関する情報
	//
	/** 対象レイヤ */
	@Required
	public String layerid = "";

	/** メタデータタイトル */
	public String title = "";

	/** 内容の要約 */
	//public String summary = ""; 
	public String abstr = "";

	/** 検索タグ */
	public String tags = "";

	/** "ライセンス情報 */
	public String licenseInfo = "";

	//
	// メタデータ/地図データに関する問い合わせ先情報
	//
	/** 組織 */
	public String organization = "";

	/** 組織名称 */
	public String organizationTitle = "";

	/** 作成者 */
	public String author = "";

	/** 作成者のメールアドレス */
	public String authorEmail = "";

	/** "作成日 */
	public String metadataCreated = "";

	/** メンテナー */
	public String maintainer = "";

	/** メンテナーのメールアドレス */
	public String maintainerEmail = "";

	/** 修正日 */
	public String metadataModified = "";

	/** "バージョン番号 */
	public String version = "";

	//
	// 地図データの時間範囲に関する情報
	//
	/** 時間範囲に関する情報 */
	public String[] exTemp;

	//
	// 地図データのアクセスに関する情報
	//
	/** WMS URL */
	public String wms = "";
	
	/** WFS URL */
	public String wfs = "";

	//
	// 完了画面に表示するためのデータ
	// 
	/** メタデータID */
	public String mdFileID = "";

	/** INSERT or UPDATE */
	public boolean update = false;

	/** メタデータ登録/更新時にメタデータデフォルト設定情報の更新実行フラグ */
	public boolean updateDefault = false;

	/** 登録済み？　*/
	private boolean registered = false;

	/** API キーと組織情報を取得可能 */
	private boolean authorized = true;

	/** エラー */
	public String error = "";
}
