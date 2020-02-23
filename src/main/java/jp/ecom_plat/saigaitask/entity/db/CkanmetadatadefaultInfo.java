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
 * CKANメタデータデフォルト設定情報
 * 
 */
@Entity
@Table(name = "ckanmetadatadefault_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/08/19 12:19:57")
@lombok.Getter @lombok.Setter
public class CkanmetadatadefaultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 作成者 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String author;

    /** 作成者メールアドレス*/
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String authoremail;

    /** メンテナー */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String maintainer;

    /** メンテナーメールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String maintaineremail;

    /** localgovinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "localgovinfoid", referencedColumnName = "id")
    public LocalgovInfo localgovInfo;
}
