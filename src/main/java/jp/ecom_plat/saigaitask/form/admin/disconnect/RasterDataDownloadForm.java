/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.disconnect;


@lombok.Getter @lombok.Setter
public class RasterDataDownloadForm {

	public String systemname;

	public String selectLocalgov;

	public String localgovType;

	public String selectMapUrl;

	public String startZoomLevel;

	public String endZoomLevel;

	public String startLat = "35.654537";

	public String startLon = "139.697353";

	public String endLat = "35.657213";

	public String endLon = "139.70144";

	public String dataSize;

	public String downloadTime;

	public String alertMessage;
}
