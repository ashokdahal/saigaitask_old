/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;


/**
 * 一括更新のDTO
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ListEditDto {

	/** ID */
	public String id = "";

	/** 更新対象カラム名 */
	public String column = "";
	/** 値 */
	public String value = "";
	/** 追記フラグ */
	public Boolean addable = false;
}
