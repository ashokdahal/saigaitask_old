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
 * CKAN事前データ情報
 * 
 */
@Entity
@Table(name = "ckanmetadata_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/08/19 12:19:33")
@lombok.Getter @lombok.Setter
public class CkanmetadataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 'テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** CKAN名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 情報種別ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String infotype;

    /** 組織キー */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ownerorg;

    /** 組織名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ownerorgtitle;

    /** タイトル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String title;

    /** 要約 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 検索タグ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String tags;

    /** レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerid;
    
    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}
