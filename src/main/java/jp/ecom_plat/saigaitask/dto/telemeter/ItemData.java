/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto.telemeter;

import org.seasar.framework.util.StringUtil;

@lombok.Getter @lombok.Setter
public class ItemData {

	/** 項目番号 */
	public String itemCode;
	/** 項目名 */
	public String itemName;
	/** 型 */
	public String itemType;
	
	public String unitName;
	
	public String value;
	
	public String contentsCode;
	
	public String contentsName;
	
	public int getItemCode() {
		if (StringUtil.isEmpty(itemCode))
			return 0;
		return Integer.parseInt(itemCode);
	}
	
	public int getContentsCode() {
		if (StringUtil.isEmpty(contentsCode))
			return 0;
		return Integer.parseInt(contentsCode);
	}
}
