/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class MaplayerInfoForm {
	
	public String id = "";

	public String menuinfoid = "";

	public String tablemasterinfoid = "";

	public String visible = "true";

	public String closed = "false";

	public String editable = "false";

	public String addable = "false";
	
	public String searchable = "false";
	
	public String snapable = "false";

	public String intersectionlayerid = "";
	
	public String valid = "true";

	public String disporder = "";

	public String deleted = "false";


}
