/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

@lombok.Getter @lombok.Setter
public class TablemasterInfoForm {

	public String id = "";

	public String mapmasterinfoid = "";

	public String layerid = "";

	public String tablename = "";

	public String name = "";

	public String geometrytype = "";

	public String copy = "";

	public String addresscolumn = "";

	public String updatecolumn = "";

	public String coordinatecolumn = "";

	public String mgrscolumn = "";

	public String mgrsdigit = "";

	public String note = "";

	public String deleted = "false";

	public String reset = "false";

	public String paging = "true";
}
