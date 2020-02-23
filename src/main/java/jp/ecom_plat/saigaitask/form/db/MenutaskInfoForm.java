/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class MenutaskInfoForm {

	public String id = "";

	public String menuprocessinfoid = "";

	public String menutasktypeinfoid = "";

	public String name = "";

	public String important = "false";

	public String visible = "true";

	public String disporder = "";

	public String note = "";

	public String valid = "true";

	public String deleted = "false";


}
