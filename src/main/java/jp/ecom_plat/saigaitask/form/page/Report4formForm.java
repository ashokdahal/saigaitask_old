/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.List;

import jp.ecom_plat.saigaitask.validator.Required;

/**
 * 4号様式フォームクラス
 */
@lombok.Getter @lombok.Setter
public class Report4formForm extends AbstractPageForm {

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

	//その1

	/** 記録ID */
	@Required
	public String trackdataid = "";

	public String reporttypeinfoid = "0";

	public String userinfoid = "";
	/** ファイルURL */
	public String fileurl = "";
	/** 報告ID */
	public String reportdataid = "";
	/** 報告IDリスト */
	public List<String> reportdataids;

	//@Required
	public String receiver = "";

	public String reporttime = "";

	//@Required
	public String pref = "";

	//@Required
	public String city = "";

	//@Required
	public String reporter = "";

	//@Required
	public String disastername = "";

	//@Required
	public String reportno = "";

	public String place = "";

	public String occurtime = "";

	public String summary = "";

	public String casualties1 = "0";

	public String casualties2 = "0";

	public String casualties3 = "0";

	public String total = "0";

	public String house1 = "0";

	public String house2 = "0";

	public String house3 = "0";

	public String house4 = "0";

	public String headoffice1 = "";

	public String headoffice2 = "";

	public String status1 = "";

	public String status2 = "";

	public String note = "";

	public String deleted = "false";


	//その2
	/*
	 * その1と共通の変数なので、そのまま同じ値を使う
	 *
	public String reportdataid = "";

	public String reporttime = "";

	public String pref = "";

	public String city = "";

	public String disastername = "";

	public String reportno = "";

	public String reporter = "";
	*/

	public String casualties21 = "0";

	public String casualties22 = "0";

	public String casualties23 = "0";

	public String casualties24 = "0";

	public String houseall1 = "0";

	public String houseall2 = "0";

	public String houseall3 = "0";

	public String househalf1 = "0";

	public String househalf2 = "0";

	public String househalf3 = "0";

	public String housepart1 = "0";

	public String housepart2 = "0";

	public String housepart3 = "0";

	public String houseupper1 = "0";

	public String houseupper2 = "0";

	public String houseupper3 = "0";

	public String houselower1 = "0";

	public String houselower2 = "0";

	public String houselower3 = "0";

	public String build1 = "0";

	public String build2 = "0";

	public String field1 = "0";

	public String field2 = "0";

	public String farm1 = "0";

	public String farm2 = "0";

	public String school = "0";

	public String hospital = "0";

	public String road = "0";

	public String bridge = "0";

	public String river = "0";

	public String harbor = "0";

	public String landslide = "0";

	public String gabage = "0";

	public String cliff = "0";

	public String railway = "0";

	public String ship = "0";

	public String water = "0";

	public String telephone = "0";

	public String electricity = "0";

	public String gas = "0";

	public String block = "0";

	public String suffer1 = "0";

	public String suffer2 = "0";

	public String fire1 = "0";

	public String fire2 = "0";

	public String fire3 = "0";

	public String amount1 = "0";

	public String amount2 = "0";

	public String amount3 = "0";

	public String amount4 = "0";

	public String subtotal = "0";

	public String amount5 = "0";

	public String amount6 = "0";

	public String amount7 = "0";

	public String amount8 = "0";

	public String amount9 = "0";

	public String amount10 = "0";

	public String atotal = "0";

	public String headoffice21 = "";

	public String headoffice22 = "";

	public String headoffice23 = "";

	public String disastercity = "0";

	public String numcity = "0";

	public String fireman1 = "0";

	public String fireman2 = "0";

	public String status = "";

	public String note2 = "";

}
