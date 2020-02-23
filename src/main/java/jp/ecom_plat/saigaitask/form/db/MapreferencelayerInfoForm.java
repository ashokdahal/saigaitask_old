/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class MapreferencelayerInfoForm {
	
	public String id = "";

	public String menuinfoid = "";

	public String layerid = "";

	public String visible = "";

	public String closed = "";

	public String searchable = "false";

	public String valid = "";

	public String disporder = "";

	public String layeropacity = "";

}
