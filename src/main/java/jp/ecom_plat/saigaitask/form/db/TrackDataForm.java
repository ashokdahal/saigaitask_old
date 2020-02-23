/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;


@lombok.Getter @lombok.Setter
public class TrackDataForm {
	
	public String id = "";

	public String trackmapinfoid;

	public String localgovinfoid = "";

    public String localgovgroupinfoid = "";

    public String demoinfoid = "";

	//public String disasterid = "";

	public String disasterinfoid = "";

	public String trainingplandataid = "";

	public String name = "";

	public String note = "";

	public String starttime = "";

	public String endtime = "";

	public String deleted = "";


}
