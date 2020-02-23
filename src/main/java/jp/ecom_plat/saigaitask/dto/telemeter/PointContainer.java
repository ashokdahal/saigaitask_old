/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto.telemeter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@lombok.Getter @lombok.Setter
public class PointContainer {

	/** 観測所コード */
	public String pointFullCode;
	/** 観測所名 */
	public String pointName;
	/** 観測時刻 */
	public Timestamp observTime;
	
	public List<ItemData> itemList;
	
	/**
	 * コンストラクタ
	 */
	public PointContainer() {
		itemList = new ArrayList<ItemData>();
	}
	
	public long getPointFullCode() {
		if (pointFullCode == null)
			return 0l;
		return Long.parseLong(pointFullCode);
	}
}
