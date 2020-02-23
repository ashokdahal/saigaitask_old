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
 * データ判定管理情報
 *
 */
@Entity
@Table(name = "judgeman_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/25 0:58:07")
@lombok.Getter @lombok.Setter
public class JudgemanInfo implements Serializable {

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

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 判定インターバル（分） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer interval;

    /** 遅延（分） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer delay;

    /** judgeorderプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer judgeorder;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 災害種別マスタ */
    /*@ManyToOne
    @JoinColumn(name="disasterid")
    public DisasterMaster disasterMaster;*/

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
