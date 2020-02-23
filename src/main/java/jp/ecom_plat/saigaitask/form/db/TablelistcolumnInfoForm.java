/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class TablelistcolumnInfoForm {

	public String id = "";

	public String menutableinfoid = "";

	public String attrid = "";

	public String name = "";

	public String editable = "false";

	public String highlight = "false";

	public String grouping = "false";

	public String sortable = "true";

	public String defaultsort = "false";

	public String uploadable = "false";

	public String loggable = "false";

	public String disporder = "";

	public String defaultcheck = "false";

	public String groupdefaultcheck = "false";

	public String addable = "false";


}
