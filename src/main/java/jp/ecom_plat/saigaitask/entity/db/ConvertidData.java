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
 * ConvertidDataエンティティクラス
 *
 */
@Entity
@Table(name = "convertid_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/02/18 11:50:22")
@lombok.Getter @lombok.Setter
public class ConvertidData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** エンティティ名 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String entityname;

    /** ID名 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String idname;

    /** 変換前値 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String oldval;

    /** 変更後値 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String newval;

    /** 自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
