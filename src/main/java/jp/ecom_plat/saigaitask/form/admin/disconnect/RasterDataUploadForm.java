/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.disconnect;

import org.springframework.web.multipart.MultipartFile;

@lombok.Getter @lombok.Setter
public class RasterDataUploadForm {
	
	public String mapId;
	
	public String ecomUser;

	public MultipartFile rasterDataFile;

	public String selectLocalgov;

}
