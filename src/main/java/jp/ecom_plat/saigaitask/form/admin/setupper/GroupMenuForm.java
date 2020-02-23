/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.setupper;

import java.util.List;

@lombok.Getter @lombok.Setter

public class GroupMenuForm {

	/** 災害種別ID */
	public Integer disasterid;

	public List<String> menu = null;

	// 班情報
	public List<String> groupname = null;
	public List<String> groupid = null;

	/** 削除する班ID */
	public List<String> deletegroupid = null;
}
