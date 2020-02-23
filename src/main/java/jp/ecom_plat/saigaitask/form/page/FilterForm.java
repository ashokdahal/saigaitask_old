/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import org.json.JSONObject;

/**
 * フィルターフォーム
 */
@lombok.Getter @lombok.Setter
public class FilterForm extends AbstractPageForm {

	/*AbstractPageFrom で定義済み*/
	//public Long menuid;

	/**
	 * フィルター情報ID
	 */
	public Long filterid;

	/** 検索条件JSON形式 */
	public JSONObject conditionValue;
}
