/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * JAlert情報トリガーデータ
 * 
 */
@Entity
@Table(name = "jalerttrigger_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/03/13 16:24:59")
@lombok.Getter @lombok.Setter
public class JalerttriggerData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** トリガーID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long jalerttriggerinfoid;

    /** トリガー発生時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp triggertime;

    /** 災害モード起動 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean startup;

    /** メール通知 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticegroupinfoid;

    /** 体制移行 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long stationclassinfoid;

    /** 職員参集 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean assemblemail;

    /** 避難勧告 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean issue;

    /** 避難情報文字列 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String issuetext;

    /** 公共コモンズ送信 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommons;

    /** エリアメール */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommonsmail;

    /** SNS */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean sns;
}
