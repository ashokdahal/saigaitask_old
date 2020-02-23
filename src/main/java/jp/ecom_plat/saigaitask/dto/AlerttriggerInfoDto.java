/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class AlerttriggerInfoDto {

    /** ID */
    public Long id;

    /** 気象情報取得ID */
    public Long meteorequestinfoid;

    /** トリガー */
    public String trigger;

    /** 災害モード起動フラグ */
    public Boolean startup;

    /** 通知グループID */
    public Long noticegroupinfoid;

    /** 体制ID */
    public Long stationclassinfoid;

    /** 職員参集メール送信フラグ */
    public Boolean assemblemail;

    /** 避難勧告テーブルID */
    public Long issuetablemasterinfoid;

    /** 避難勧告属性項目 */
    public String issueattrid;

    /** 避難勧告 */
    public String issuetext;

    /** 公共コモンズ送信フラグ */
    public Boolean publiccommons;

    /** エリアメール送信フラグ */
    public Boolean publiccommonsmail;

    /** SNS送信フラグ */
    public Boolean sns;

    /** 備考 */
    public String note;

    /** 有効・無効 */
    public Boolean valid;

    /** レイヤ追加フラグ */
    public Boolean addlayer;
}
