/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import jp.ecom_plat.saigaitask.validator.Required;

@lombok.Getter @lombok.Setter
public class ExcellistForm extends AbstractPageForm{

	/** 表示年 */
	public String showtime_yy = "";
	/** 表示月 */
	public String showtime_mm = "";
	/** 表示日 */
	public String showtime_dd = "";
	/** 表示時刻 */
	public String showtime_hh = "";
	/** 表示分 */
	public String showtime_mm2 = "";
	/** 表示年選択 */
	public String[] showtime_yy_list;

	/** エクセル帳票コンテンツ */
	@Required
	public String excellistContent = "";

}
