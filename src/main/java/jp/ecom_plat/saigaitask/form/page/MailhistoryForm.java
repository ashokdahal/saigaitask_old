/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

/**
 * 通知履歴フォーム
 */
@lombok.Getter @lombok.Setter
public class MailhistoryForm extends AbstractPageForm {
	/** ID */
	public String id = "";
	/** 記録データID */
	public String trackdataid = "";
	/** 通知種別ID */
	public String noticetypeid = "";
	/** 送信先 */
	public String mailto = "";
	/** タイトル */
	public String title = "";
	/** 内容 */
	public String content = "";
	/** 送信時間 */
	public String sendtime = "";
	/** 送信結果 */
	public String send = "";
	/** 現在のページ番号 */
    public String numPage;
}
