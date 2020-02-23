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
public class TwitterForm extends AbstractPageForm {


	//タイトル削除
//	/** twitter 件名 */
//	@Required
//	public String twitterTitle = "";

	/** twitter 本文 */
	//@Required
	public String twitterContent = "";

	/** email 件名 */
	//@Required
	public String emailTitle = "";

	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();

	/** 追加送付先 */
	public String additionalReceiver = "";

	/** テンプレート番号 */
	public String templateid = "";
}
