/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;



import org.springframework.web.multipart.MultipartFile;

@lombok.Getter @lombok.Setter
public class MeteoxsltInfoForm {

	public String id = "";

	public String localgovinfoid = "";

	public String meteotypeid = "";

	public String noticetypeid = "";

	public String filepath = "";

	public MultipartFile uploadFile = null;
}
