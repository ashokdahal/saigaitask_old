/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

/**
 * 職員参集のアクションフォーム
 */
@lombok.Getter @lombok.Setter
public class AssembleForm extends AbstractPageForm {

	/** 要請時の記録ID */
	public String trackdataid = "";
	
	/** 職員参集ID */
	public String assembleinfoid = "";
	
	/** 通知グループID */
	public String noticegroupinfoid = "";
	
	/** 通知グループ名 */
	public String noticegroupinfoname = "";
	
	/** 要請メールの件名 */
	public String mailtitle = "";
	
	/** 要請メールの内容 */
	public String mailcontent = "";
	
	/** 要請メール時のポップアップ処理 */
	public boolean enablePopup = false;
	
	/** 追加送付先 */
	public String additionalReceiver = "";
	
	public String message = "";
	
	/**
	 * ポップアップ処理のチェックボックスの初期化
	 */
	public void resetEnablePopup() {
		this.enablePopup = false;
	}	
}
