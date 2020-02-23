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
 * 公共情報コモンズ避難所レイヤ情報
 *
 */
@Entity
@Table(name = "publiccommons_report_shelter_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/09/28 0:42:04")
@lombok.Getter @lombok.Setter
public class PubliccommonsReportShelterInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** テーブルID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long tablemasterinfoid;

    /** 避難所名 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrshelter;

    /** 地区 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrarea;

    /** 住所 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attraddress;

    /** 電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrphone;

    /** FAX */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrfax;

    /** 代表者氏名 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrstaff;

    /** 開設状況 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrstatus;

    /** 収容定員数 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrcapacity;

    /** 開設日時 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrsetuptime;

    /** 閉鎖日時 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrclosetime;

    /** 種別 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrtype;

    /** 座標 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrcircle;

    /** 避難人数 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrheadcount;

    /** 避難人数(うち自主避難) */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrheadcountvoluntary;

    /** 避難世帯数 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrhouseholds;

    /** 避難世帯数(うち自主避難) */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String attrhouseholdsvoluntary;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** テーブルマスタ情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}
