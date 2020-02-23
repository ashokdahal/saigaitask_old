/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin;

import jp.ecom_plat.saigaitask.util.MetadataUtil;
import jp.ecom_plat.saigaitask.validator.Required;

/**
 * クリアリングハウスデータ登録アクションフォーム
 */
@lombok.Getter @lombok.Setter
public class UploadclearinghouseDataForm {

	/** 自治体ID */
	public String localgovinfoid = "";

	//
	// メタデータに関する情報
	//

	// 制約
	//public String constraint = "";
	/** メタデータを公開する対象 */
	public String useLimit = "";			//64: useLimit


	//
	// 地図データに関する情報
	//

	/** 対象レイヤ */
	@Required
	public String layerid = "";				//28: title

	/** 地図データのタイトル接頭語 */
	public String prefix = "";

	/** 地図データのタイトル接尾語 */
	public String suffix = "";

	/** 地図データの内容の要約 */
	//public String summary = ""; 
	public String abstr = "";				//32: abstract

	/** 地図データが作成された目的 */
	public String purpose = ""; 			//33: purpose

	/** 地図データの作成状況 */
	public String status = MetadataUtil.MD_ProgressCode.EXPECTED; 				//34: status

	//
	// メタデータ/地図データに関する問い合わせ先情報
	//

	// 問い合わせ
	//public String reference = ""; 
	
	/** 問い合わせ先の名称 */
	public String organizationName= "";		//7: organizationName

	/** 郵便番号 */
	public String postCode= "";

	/** 都道府県名（contact/contactInfo/address/administrativeArea） */
	public String adminArea = "";

	/**  市区町村（contact/contactInfo/address/city） */
	public String city = "";

	/** 都道府県コード */
	public String adminAreaCode = "";

	/** 市区町村コード */
	public String cityCode = "";

	/** 町名、番地、ビル名等 */
	public String delPoint = "";

	/** 電話番号 */
	public String voice = "";				//10: voice

	/** 電子メールアドレス（contact/contactInfo/address/electronicMailAddress）*/
	public String eMailAdd = "";

	/** 問い合わせ先のHP等のURL （contact/contactInfo/onlineResource/linkage）*/
	public String linkage = "";

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
	/** メタデータタイトル */
	public String title = "";

	/** メタデータID */
	public String mdFileID = "";
	
	/** INSERT or UPDATE */
	public boolean update = false;

	/** メタデータ登録/更新時にメタデータデフォルト設定情報の更新実行フラグ */
	public boolean updateDefault = false;
	
	private boolean registered = false;
}
