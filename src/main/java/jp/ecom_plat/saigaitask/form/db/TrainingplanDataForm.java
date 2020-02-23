/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;



@lombok.Getter @lombok.Setter
public class TrainingplanDataForm {
	
	public String id = "";

	public String localgovinfoid = "";

    public String localgovgroupinfoid = "";

    //public String disasterid = "";

	public String name = "";

	public String note = "";

	public String publiccommonsflag = "";

	public String facebookflag = "";

	public String twitterflag = "";

	public String ecommapgwflag = "";

	public String updatetime = "";

	public String deleted = "";


}
