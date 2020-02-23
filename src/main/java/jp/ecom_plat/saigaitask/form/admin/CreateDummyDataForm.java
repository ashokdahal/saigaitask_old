/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin;

import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.constant.Observ;

@lombok.Getter @lombok.Setter
public class CreateDummyDataForm {

	//ハザードマップ
	public String hazardLayerIds = "c1917";

	public String hazardAttrId = "attr1";
	
	public String meshLayerId = "c1909";

    public String outLayerId = "c1919";
    
    public String outAttrId = "attr2";
	
    public int minus = 1;

    //テレメータ
	public int observtype = Observ.RAIN;

	public long observpointinfoid = 4l;
	
	public String untildate = "2014/10/13";
	
	public String untiltime = "17:00";

	public String wplus = "0.0";
	
	public MultipartFile formFile;
	
}
