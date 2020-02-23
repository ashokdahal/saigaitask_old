/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;


import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongType;

import jp.ecom_plat.saigaitask.validator.Required;

@lombok.Getter @lombok.Setter
public class MultilangInfoForm {
	@LongType
	public String id = "";

	@Required
	public String code = "";

	@Required
	public String name = "";

	@Required
	@IntegerType
	public String disporder = "";
}
