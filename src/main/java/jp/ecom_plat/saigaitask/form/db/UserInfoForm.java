/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

import org.seasar.struts.annotation.LongType;

import jp.ecom_plat.saigaitask.validator.Required;

// ユーザ情報（user_info）
@lombok.Getter @lombok.Setter
public class UserInfoForm {
	// ID
	@LongType
	public String id = "";

	@Required
	@LongType
	public String groupid = "";

	// ユニットID
	@Required
	@LongType
	public String unitid = "";

	public String staffno = "";
	
	// 名前
	@Required
	public String name = "";

	// 役割
	public String duty = "";

	// 電話番号
	public String telno = "";

	// 携帯電話番号
	public String mobileno = "";

	// メールアドレス
	public String email = "";

	// 携帯電話のメールアドレス
	public String mobilemail = "";

	// 備考
	public String note = "";

	// 表示順
	public String disporder = "";

	// 有効（true）
	public String valid = "true";

	public String pushtoken = "";
}
