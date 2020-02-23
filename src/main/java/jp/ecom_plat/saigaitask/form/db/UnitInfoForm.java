/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

import org.seasar.struts.annotation.LongType;

import jp.ecom_plat.saigaitask.validator.Required;

// ユニット情報（unit_info）
@lombok.Getter @lombok.Setter
public class UnitInfoForm {
	// ID
	@LongType
	public String id = "";

	@Required
	@LongType
	public String localgovinfoid = "";

	// 班ID
	@Required
	@LongType
	public String groupid = "";

	// ユニット名
	@Required
	public String name = "";

	public String password = "";

	public String ecomuser = "";

	public String admin = "false";

	public String extent = "";

	public String resolution = "";

	// 代表電話番号
	public String telno = "";

	// FAX番号
	public String faxno = "";

	// メールアドレス
	public String email = "";

	// APIキー
	public String apikey = "";

	// 備考
	public String note = "";

	// 表示順
	public String disporder = "";

	// 有効（true）
	public String valid = "true";

	public String deleted = "false";
}
