/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.sql.Timestamp;

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class AlerttriggerDataDto {

    /** ID */
    public Long id;

    /** 自治体ID */
    public Long localgovinfoid;

    /** 記録ID */
    public Long trackdataid;

    /** トリガーID */
    public Long meteotriggerinfoid;

    /** トリガー発生時刻 */
    public Timestamp triggertime;

    /** 災害モード起動済 */
    public Boolean startup;

    /** 通知グループID */
    public Long noticegroupinfoid;

    /** 体制ID */
    public Long stationclassinfoid;

    /** 職員参集メール送信済 */
    public Boolean assemblemail;

    /** 避難勧告済 */
    public Boolean issue;

    /** 避難情報文字列 */
    public String issuetext;

    /** 公共コモンズ送信済 */
    public Boolean publiccommons;

    /** エリアメール送信済 */
    public Boolean publiccommonsmail;

    /** SNS送信フラグ */
    public Boolean sns;
}
