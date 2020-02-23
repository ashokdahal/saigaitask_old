/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.disconnect;


@lombok.Getter @lombok.Setter
public class DatasyncForm {

	public String cloudurl;

	public String cloudurlChecked;

	public String selectLocalgov;

	public String targetLocalgovinfoid;

	public String [] selectFileTrackMultibox;

	public String [] selectDbTrackMultibox;

	public String uploadTrackDataDirName;

	public String uploadTrackDataFileName;

	public String trackMapText;

	public String [] updateFeatures;

	public String [] updateTrackTableDatas;

	public String updateTrackTableDatasHidden;
	
	public String syncAll = "1";
	
	public String syncAttachedFile = "1";
	
	public String hasError;
}
