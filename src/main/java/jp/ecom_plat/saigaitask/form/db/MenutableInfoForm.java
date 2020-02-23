/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class MenutableInfoForm {
	
	public String id = "";

	public String menuinfoid = "";

	public String tablemasterinfoid = "";

	public String addable = "false";

	public String deletable = "false";

	public String totalable = "true";

	public String deleted = "false";
	
	public String disporder = "";
	
	public String accordionattrid = "";
	
	public String accordionname = "";
	
	public String accordionopen = "true";
}
