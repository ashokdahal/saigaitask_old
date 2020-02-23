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
 * 訓練プラン
 * 
 */
@Entity
@Table(name = "trainingplan_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/05/13 16:32:32")
@lombok.Getter @lombok.Setter
public class TrainingplanData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 災害種別ID */
    /*@Column(precision = 10, nullable = true, unique = false)
    public Integer disasterid;*/

    /** 訓練プラン名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 公共コモンズ利用制限フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommonsflag;

    /** Facebook利用制限フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean facebookflag;

    /** Twitter利用制限フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean twitterflag;

    /** eコミマップグループウェア利用制限フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean ecommapgwflag;

    /** 更新時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp updatetime;

    /** 自治体グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovgroupinfoid;
    
    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;

    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** localgovgroupinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "localgovgroupinfoid", referencedColumnName = "id")
    public LocalgovgroupInfo localgovgroupinfo;

    /*@ManyToOne
    @JoinColumn(name="disasterid")
    public DisasterMaster disasterMaster;*/
}
