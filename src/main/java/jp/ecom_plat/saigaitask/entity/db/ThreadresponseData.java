/* Copyright (c) 2015 National Research Institute for Earth Science and
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
 * スレッドレスポンスデータ
 *
 */
@Entity
@Table(name = "threadresponse_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/08/05 18:35:18")
@lombok.Getter @lombok.Setter
public class ThreadresponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** スレッドID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long threaddataid;

    /** 班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** 送信メッセージ or ファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String message;

    /** True : ファイル false : 送信メッセージ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean fileflag;

    /** 再現URL */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String url;

    /** 送信時間 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 更新時間 */
    @Column(nullable = true, unique = false)
    public Timestamp updatetime;

    /** 削除時間 */
    @Column(nullable = true, unique = false)
    public Timestamp deletetime;

    /** 課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** スレッド */
    @ManyToOne
    @JoinColumn(name="threaddataid")
    public ThreadData threadData;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;
}