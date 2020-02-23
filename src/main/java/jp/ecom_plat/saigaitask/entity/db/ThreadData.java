/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * スレッドデータ
 *
 */
@Entity
@Table(name = "thread_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/08/06 20:58:09")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"threadresponseDataList","threadsendtoDataList"})
public class ThreadData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** スレッド作成班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** スレッドタイトル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String title;

    /** 優先度(1: 緊急、2:高、3:中、4:低) */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer priority;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 閉鎖日時 */
    @Column(nullable = true, unique = false)
    public Timestamp closetime;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;

    /** 課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 記録データ */
    @ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;

    /** スレッドレスポンスデータリスト */
    @OneToMany(mappedBy = "threaddata")
    public List<ThreadresponseData> threadresponseDataList;

    /** スレッド送信先データリスト */
    @OneToMany(mappedBy = "threaddata")
    public List<ThreadsendtoData> threadsendtoDataList;
}
