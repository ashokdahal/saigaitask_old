/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * JAlert情報トリガー情報
 *
 */
@Entity
@Table(name = "jalerttrigger_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/03/13 16:24:29")
@lombok.Getter @lombok.Setter
public class JalerttriggerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** JAlert情報取得ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long jalertrequestinfoid;

    /** トリガー */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String trigger;

    /** 災害モード起動フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean startup;

    /** 通知グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticegroupinfoid;

    /** 体制ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long stationclassinfoid;

    /** 職員参集メール送信フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean assemblemail;

    /** 避難勧告テーブル */
    @Column(precision = 19, nullable = true, unique = false)
    public Long issuetablemasterinfoid;

    /** 避難勧告属性項目 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String issueattrid;

    /** 避難情報の文字列 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String issuetext;

    /** 公共コモンズ送信フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommons;

    /** エリアメール送信フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommonsmail;

    /** SNS送信フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean sns;

    /** レイヤ追加フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean addlayer;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 通知グループ情報 */
    @ManyToOne
    @JoinColumn(name="noticegroupinfoid")
    public NoticegroupInfo noticegroupInfo;

    /** JAlert情報取得情報 */
    @ManyToOne
    @JoinColumn(name="jalertrequestinfoid")
    public JalertrequestInfo jalertrequestInfo;

    /** 体制区分 */
    @ManyToOne
    @JoinColumn(name="stationclassinfoid")
    public StationclassInfo stationclassInfo;
}
