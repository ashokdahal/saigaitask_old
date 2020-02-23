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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * アラームメッセージデータ
 * 
 */
@Entity
@Table(name = "alarmmessage_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:49:01")
@lombok.Getter @lombok.Setter
public class AlarmmessageData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** トラックデータID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** アラームメッセージID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long alarmmessageinfoid;

    /** 送信班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long sendgroupid;

    /** 閲覧班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** 通知先 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String noticeto;

    /** 成形されたメッセージ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String message;

    /** 再現URL */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String noticeurl;

    /** メッセージ表示フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean showmessage;

    /** "success", "error", "warning", "information" */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String messagetype;

    /** 表示時間、0で手動非表示 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer duration;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;

    /** 課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 送信課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long sendunitid;

    @ManyToOne
    @JoinColumn(name="trackdataid")
    public TrackData trackData;

    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    @ManyToOne
    @JoinColumn(name="alarmmessageinfoid")
    public AlarmmessageInfo alarmmessageInfo;
    
    @ManyToOne
    @JoinColumn(name="sendgroupid")
    public GroupInfo sendgroupInfo;

    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;
}
