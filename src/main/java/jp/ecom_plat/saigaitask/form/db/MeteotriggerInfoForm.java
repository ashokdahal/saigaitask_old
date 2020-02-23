/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class MeteotriggerInfoForm {

	public String id = "";

	public String meteorequestinfoid = "";

	public String trigger = "";

	public String startup = "true";

	public String noticegroupinfoid = "";

	public String stationclassinfoid = "";

	public String assemblemail = "true";

	public String issuetablemasterinfoid = "";

	public String issueattrid = "";

	public String issuetext = "";

	public String publiccommons = "true";

	public String publiccommonsmail = "true";

	public String sns = "true";

	public String addlayer = "true";

	public String note = "";

	public String valid = "true";


}
