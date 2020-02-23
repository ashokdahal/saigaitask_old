/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;


/**
 * 公共情報コモンズ発信先データフォーム
 *
 */
@lombok.Getter @lombok.Setter
public class PubliccommonsSendToInfoForm {
	/** ID */
	public String id = "";
	/** 自治体ID */
	public String localgovinfoid = "";
	/** エンドポイントURL */
	public String endpointUrl = "";
	/** ユーザ名 */
	public String username = "";
	/** パスワード */
	public String password = "";
	/** 送信順 */
	public String sendOrder = "";
	/** 運用種別 */
	public String statusValues = "";
	/** エンドポイントURLバックアップノード */
	public String endpointUrlBackup = "";
	/** ユーザ名バックアップノード */
	public String usernameBackup = "";
	/** パスワードバックアップノード */
	public String passwordBackup = "";

}
