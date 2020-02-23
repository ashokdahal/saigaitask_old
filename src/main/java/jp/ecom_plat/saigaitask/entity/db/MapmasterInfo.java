/* Copyright (c) 2013 National Research Institute for Earth Science and
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
 * 地図マスター情報
 *
 */
@Entity
@Table(name = "mapmaster_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/11 18:21:40")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"tablemasterInfoList"})
public class MapmasterInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治他ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** コミュニティID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer communityid;

    /** グループID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer mapgroupid;

    /** 地図ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long mapid;

    /** 表示制限範囲のWKT(BBOX) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String restrictedextent;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** テーブルマスター情報リスト */
    @OneToMany(mappedBy = "mapmasterInfo")
    public List<TablemasterInfo> tablemasterInfoList;

    /**
     * コピーフラグ
     * false: 災害時はコピーせず、訓練のみマップコピー
     * true: 災害時も訓練時もマップコピー
     */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean copy = false;

    /** 削除フラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted = false;
}
