/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

@lombok.Getter @lombok.Setter
public class MapmasterInfoForm {

	public String id = "";

	public String localgovinfoid = "";

	public String communityid = "";

	public String mapgroupid = "";

	public String mapid = "";

	public String restrictedextent;

	public String deleted = "false";

	public String copy = "false";
}
