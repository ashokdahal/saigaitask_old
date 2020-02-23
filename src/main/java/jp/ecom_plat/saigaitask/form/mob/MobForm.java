/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.mob;

import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

@lombok.Getter @lombok.Setter
public class MobForm {
	private static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	public long processid = 0;

	public long taskid = 0;

	/**
	 * 移動体名称など
	 */
	public String name = "";

/*	public Long getGroupidLong() {
		if (groupid.length() > 0)
			return Long.parseLong(groupid);
		return 0L;
	}*/
}
