/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

import org.springframework.web.multipart.MultipartFile;

@lombok.Getter @lombok.Setter
public class LandmarkDataForm {

	public String id = "";

	public String landmarkinfoid = "";

	public String groupid = "";

	public String landmark = "";

	public String latitude = "";

	public String longitude = "";

	public String csvfilename = "";

	public MultipartFile csvfile;

	public String csvloadradio = "";
}
