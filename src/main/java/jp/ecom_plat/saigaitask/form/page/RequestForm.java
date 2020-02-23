/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.ArrayList;
import java.util.List;


/**
 * 要請ページのアクションフォームクラスです.
 * 要請ページ固有のプロパティを定義します.
 */
@lombok.Getter @lombok.Setter
public class RequestForm extends AbstractPageForm {
	
	/** 要請時の記録ID */
	public String trackdataid = "";
	
	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();
	
	/** 要請メールの件名 */
	public String mailtitle = "";
	
	/** 要請メールの内容 */
	public String mailcontent = "";
	
	/** alert 宛先リスト 選択結果 */
	public List<String> checkedAlertList = new ArrayList<String>();
	
	/** 要請メール時のポップアップ処理 */
	public boolean enablePopup = false;
	
	/** テンプレート番号 */
	public String templateid = "";
	
	/** 追加送付先 */
	public String additionalReceiver = "";
	
	/**
	 * ポップアップ処理のチェックボックスの初期化
	 */
	public void resetEnablePopup() {
		this.enablePopup = false;
	}
}
