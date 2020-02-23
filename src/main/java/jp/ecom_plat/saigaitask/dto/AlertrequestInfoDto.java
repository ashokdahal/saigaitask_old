/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class AlertrequestInfoDto {

    /** ID */
    public Long id;

    /** 自治体ID */
    public Long localgovinfoid;

    /** 気象情報種別 */
    public Integer meteotypeid;

    /** エリアID */
    public String meteoareaid;

    /** エリアID予備 */
    public String meteoareaid2;

    /** アラームフラグ */
    public Boolean alarm;

    /** 表示フラグ */
    public Boolean view;

    /** 備考 */
    public String note;

    /** 有効・無効 */
    public Boolean valid;

}
