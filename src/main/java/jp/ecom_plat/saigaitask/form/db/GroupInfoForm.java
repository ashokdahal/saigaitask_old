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
public class GroupInfoForm {
	@LongType
	public String id = "";

	@Required
	@LongType
	public String localgovinfoid = "";

	@Required
	public String name = "";

	public String password = "";

	public String ecomuser = "";

	public String ecompass = "";

	@Required
	public String admin = "false";

	@Required
	public String headoffice = "false";

	public String extent = "";

	public String resolution = "";

	public String namekana = "";
	public String phone = "";
	public String fax = "";
	public String email = "";
	public String address = "";
	public String domain = "";
	public String apikey = "";

	public String note = "";

	@Required
	@IntegerType
	public String disporder = "";

	@Required
	public String valid = "true";

	public String deleted = "false";
}
