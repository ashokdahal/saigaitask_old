/* Copyright (c) 2015 National Research Institute for Earth Science and
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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 気象情報レイヤ情報
 * 
 */
@Entity
@Table(name = "meteoareainformationcity_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/09/09 17:42:30")
@lombok.Getter @lombok.Setter
public class MeteoareainformationcityMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** コード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String code;

    /** コードが示す地域等 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 市町村名（気象関係） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String namewarn;

    /** "気象警報・注意報"及び"気象特別警報報知"で使用 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean warnflag;

    /** "竜巻注意情報"で使用 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean tatsumakiflag;

    /** "土砂災害警戒情報"で使用 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean landslideflag;

    /** "指定河川洪水予報"で使用 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean riverflag;

    /** 市町村名（地震津波関係） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String nameseismic;

    /** 市町村名（火山関係） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String namevolcano;

    /** 点（WKT） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String point;

    /** 線（WKT） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String line;

    /** 面（WKT） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String polygon;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;
}
