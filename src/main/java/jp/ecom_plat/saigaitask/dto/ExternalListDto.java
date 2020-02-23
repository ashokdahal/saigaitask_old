/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;

/**
 * 外部リストDto
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ExternalListDto extends ListDto {

	/** メタデータID */
	public String metadataId;

	/** クリアリングハウス事前データ情報 */
	public ClearinghousemetadataInfo clearinghousemetadataInfo;

	/** 自治体コード */
	public String localgovcode;

}
