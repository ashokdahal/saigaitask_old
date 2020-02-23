/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




@lombok.Getter @lombok.Setter
public class OutermapInfoForm {

	public String id = "";

	public String menuinfoid = "";

	public String name = "";

	public String metadataid = "";


	public String layerparent = "0";

	public String attribution = "";

	public String layeropacity = "1";

	public String wmscapsurl = "";

	public String wmsurl = "";

	public String wmsformat = "";

	public String wmslegendurl = "";

	public String wmsfeatureurl = "";

	public String layerdescription = "";

	public String legendheight = "0";

	public String selectedwmslayerids = "";

	public String selectedwmslayernames = "";


	public String list = "false";

	public String filterid = "";

	public String visible = "true";

	public String closed = "false";

	public String disporder = "";

	public String externaltabledatainfoid = "";

	public String externaltabledatainfofilterid = "";

	public String authorizationinfoid = "";
}
