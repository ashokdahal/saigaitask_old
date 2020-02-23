/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.api;

import org.springframework.web.multipart.MultipartFile;

@lombok.Getter @lombok.Setter
public class TrackdatasyncForm {

	/**
	 * @since v1
	 */
	public MultipartFile trackDatafile;

	/**
	 * @since v1
	 */
	public String trackDatafileHash;

	/**
	 * @since v1
	 */
	public String uploadTrackDataDirName;

	/**
	 * @since v1
	 */
	public String uploadTrackDataFileName;

	/**
	 * @since v1
	 */
	public String updateFeatures;

	/**
	 * @since v1
	 */
	public String updateTrackTableDatas;

	/**
	 * @since v1
	 */
	public String updateTrackTableDatasHidden;

	/**
	 * @since v1
	 */
	public String trackMapText;

	/**
	 * @since v2
	 */
	public String syncAll;
	
	/**
	 * @since v2
	 */
	public String syncAttachedFile;
}
