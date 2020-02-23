/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.validator;

public class CommonValidator {

	/** E-mail regular expression */
	public static final String MAIL_MATCH_STRING = "[\\w\\.\\-\\#\\$\\%\\&\\'\\^\\~\\u00C0-\\u01FF]+@(?:[\\w\\-]+\\.)+[\\w\\-]+";

	/**
	 * E-mail Address Check
	 *
	 * OK: true NG: false
	 *
	 * @param target
	 * @return
	 */
	public static boolean isEmail(String target) {
		return (target.matches(MAIL_MATCH_STRING));
	}
}
