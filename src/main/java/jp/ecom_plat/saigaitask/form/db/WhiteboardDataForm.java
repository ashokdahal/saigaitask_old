package jp.ecom_plat.saigaitask.form.db;



import org.seasar.struts.annotation.LongType;

import jp.ecom_plat.saigaitask.validator.Required;

public class WhiteboardDataForm {
	// ID
	@LongType
	public String id = "";

	// 班ID
	@Required
	@LongType
	public String groupid = "";

	// メッセージ
	public String message = "";

	// 登録日時
	public String registtime = "";
}