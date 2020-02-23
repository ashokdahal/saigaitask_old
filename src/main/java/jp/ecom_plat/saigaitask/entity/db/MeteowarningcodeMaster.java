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
 * 警戒情報コードマスタ
 *
 */
@Entity
@Table(name = "meteowarningcode_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/09/11 16:50:23")
@lombok.Getter @lombok.Setter
public class MeteowarningcodeMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 10, nullable = false, unique = true)
    public Integer id;

    /** 気象情報等取得種別マスタのID */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer meteotypemasterid;

    /** コード */
    @Column(precision = 10, nullable = false, unique = false)
    public String code;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String name;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer disporder;

    /** 利用可否 */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean valid;

    /** meteotypemaster関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "meteotypemasterid", referencedColumnName = "id")
    public MeteotypeMaster meteotypemaster;
}
