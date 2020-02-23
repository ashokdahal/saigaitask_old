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
 * 災害対策本部設置状況
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class AntidisasterInformationDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 災害対策本部設置状況 */
	public String antidisasterKbn;

	/** 前回災害対策本部設置状況 */
	public String lastAntidisasterKbn;

	/** 発令日時 */
	public Timestamp hatureiDateTime;

	/** 本部名称 */
	public String name;

	public String getHatureiDateTime() {
		return hatureiDateTime == null ? "" : new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(hatureiDateTime);
	}
}
