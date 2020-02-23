/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

import org.springframework.web.multipart.MultipartFile;




@lombok.Getter @lombok.Setter
public class MenuInfoForm {

	private String id = "";

	private String menutasktypeinfoid = "";

	private String menutypeid = "";

	private String name = "";

	private String helphref = "";

	private String visible = "true";

	private String note = "";

	private String valid = "true";

	private String excellistoutputtablemasterinfoid = "";

	private String excellistoutputtableregisttimeattrid = "";

	private String excellistoutputtabledownloadlinkattrid = "";

	private String deleted = "false";

	private MultipartFile excellisttemplatefile = null;
}
