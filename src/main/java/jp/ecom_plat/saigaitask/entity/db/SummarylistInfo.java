/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
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
 * SummarylistInfoエンティティクラス
 *
 */
@Entity
@Table(name = "summarylist_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2016/09/09 20:10:06")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"summarylistcolumnList"})
public class SummarylistInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 地方自治体情報ID */
    @Column(precision = 19, nullable = false, unique = true)
    public Long localgovinfoid;

    /** 集計対象テーブルID */
    @Column(precision = 19, nullable = false, unique = true)
    public Long targettablemasterinfoid;

    /** テーブルマスタ情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 自治体コード項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String localgovcode;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;

    /** テーブルマスター情報 集計対象テーブルID用*/
    @ManyToOne
    @JoinColumn(name="targettablemasterinfoid")
    public TablemasterInfo targettablemasterInfo;

    /** 集計リスト項目情報 */
    @OneToMany(mappedBy="summarylistInfo")
    public List<SummarylistcolumnInfo> summarylistcolumnList;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}