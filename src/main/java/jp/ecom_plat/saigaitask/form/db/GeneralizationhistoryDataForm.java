/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;


@lombok.Getter @lombok.Setter
public class GeneralizationhistoryDataForm {
	
	public String id = "";

	public String trackdataid = "";

//	public String menutypeid = "";

	public String pagetype = "";

	public String listid = "";

	public String csvpath = "";

	public String pdfpath = "";

	public String note = "";

	public String registtime = "";

	public String deleted = "";


}
