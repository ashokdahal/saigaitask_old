/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.ArrayList;
import java.util.List;

import facebook4j.User;
import jp.ecom_plat.saigaitask.validator.Required;

@lombok.Getter @lombok.Setter
public class FacebookForm extends AbstractPageForm {

	//タイトル削除
//	/** facebook 件名 */
//	@Required
//	public String facebookTitle = "";

	/** facebook 本文 */
	//@Required
	public String facebookContent = "";

	/** email 件名 */
	//@Required
	public String emailTitle = "";

	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();

	/** FacebookグループID */
	public List<String> facebookgroupinfo = new ArrayList<String>();
	public List<String> facebookaccountinfo = new ArrayList<String>();
	public String facebookHomeinfo;
	public String facebookHomeid;
	public User facebookUsername;

	/** 追加送付先 */
	public String additionalReceiver = "";

	public String templateid = "";
}
