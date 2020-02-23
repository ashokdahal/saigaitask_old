/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 避難情報
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class RefugeInformationDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 地区 */
	public String chikuName;

	/** 地区(コモンズ対応表記に編集した全域) */
	public String chikuNameAllArea;

	/** 発令状況 */
	public String hatureiKbn;

	/** 前回発令状況 */
	public String lasthatureiKbn;

	/** 対象世帯数 */
	public Integer targetHouseholds;

	/** 人数 */
	public Integer people;

	/** 発令日時 */
	public Timestamp hatureiDateTime;

	/** テーブル名 */
	public String tablename;

	public String getTargetHouseholds() {
		return targetHouseholds == null ? "" : targetHouseholds.toString();
	}

	public String getPeople() {
		return people == null ? "" : people.toString();
	}

	public String getHatureiDateTime() {
		return hatureiDateTime == null ? "" : new SimpleDateFormat("yyyy/MM/dd HH:mm").format(hatureiDateTime);
	}
}
