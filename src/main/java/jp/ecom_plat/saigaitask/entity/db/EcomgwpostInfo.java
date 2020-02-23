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
 * Eコミグループ投稿先設定
 *
 */
@Entity
@Table(name = "ecomgwpost_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/04/04 11:41:40")
@lombok.Getter @lombok.Setter
public class EcomgwpostInfo implements Serializable {

	private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 画面表示用セット名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 投稿先グループID。APIのgroupidパラメータに該当。 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String groupid;

    /** パーツ共通ID。APIのpartsidパラメータに該当。 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String partsid;

    /** パーツ個別ID。APIのblockidパラメータに該当。 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String blockid;

    /** 投稿先URL（APIのURL）。 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String posturl;

    /** 備考。 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}