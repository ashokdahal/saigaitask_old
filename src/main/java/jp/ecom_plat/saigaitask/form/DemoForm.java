/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

import jp.ecom_plat.saigaitask.validator.Required;

/**
 * デモ画面のアクションフォーム
 */
@lombok.Getter @lombok.Setter
public class DemoForm {

	/** ID */
	@Required
	public String id = "";

	/** 自治体ID */
	public String localgovinfoid = "";
}
