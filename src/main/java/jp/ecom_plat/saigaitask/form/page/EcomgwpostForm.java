/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.validator.Required;

@lombok.Getter @lombok.Setter
public class EcomgwpostForm extends AbstractPageForm {

	/** 緊急度 */
	public String ecomgwpostUrgent = "";

	/** ecom 件名 */
	@Required
	public String ecomgwpostTitle = "";

	/** ecom 本文(html) */
	public String ecomgwpostContent = "";

	/** email 件名 */
	@Required
	public String emailTitle = "";

	/** Eコミグループ投稿先設定ID */
	public List<String> ecomgwpostinfoid = new ArrayList<String>();

	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();

	/** 追加送付先 */
	public String additionalReceiver = "";

	/** テンプレート番号 */
	public String templateid = "";
}
