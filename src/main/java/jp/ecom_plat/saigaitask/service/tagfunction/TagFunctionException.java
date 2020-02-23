/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction;

import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

public class TagFunctionException extends Exception {
	private static final long serialVersionUID = 1L;
	private static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	public static final String PARSE_ERROR = "Syntax error";
	public static final String INTERNAL_ERROR = "Internal error";
	public static final String INCOMPATIBLE_DATATYPE = "Data type incompatible";
	public static final String INVALID_LAYER = "Invalid layer";
	public static final String INVALID_ATTR = "Invalid attribution";
	public static final String INVALID_FILTERID = "Invalid filterid";
	public static final String DATA_ERROR = "Invalid data";
	public static final String INVALID_FORMAT_STRING = "Invalid format string";

	public TagFunctionException(String msg) {
		super(msg);
	}

	public String getMessage() {
		return lang.__(super.getMessage());
	}

}
