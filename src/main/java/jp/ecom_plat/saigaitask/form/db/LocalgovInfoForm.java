/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongType;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.validator.Required;

@lombok.Getter @lombok.Setter
public class LocalgovInfoForm {
	@LongType
	public String id = "";

	@Required
	public String domain = "";

	public String systemname = "";

	@Required
	@IntegerType
	public String localgovtypeid = "";

	@LongType
	public String preflocalgovinfoid = "";

	@Required
	public String pref = "";

	@Required
	public String prefcode = "";

	public String city = "";

	public String citycode = "";

	public String section = "";

	public String autostart = "true";

	@LongType
	public String autostartgroupinfoid = "";

	public String alarminterval = "120";

	public String smtp = "";

	public String email = "";

	public String coordinatedecimal = "false";

	public String note = "";

	@Required
	public String valid = "true";

	public String multilanginfoid = "";

	public String disastercombined = "";

	public String logoimagefile = null;

	public MultipartFile logoimagefileupload = null;
}
