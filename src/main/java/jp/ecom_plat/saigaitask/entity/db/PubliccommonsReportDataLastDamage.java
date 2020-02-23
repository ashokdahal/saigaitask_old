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
 * 公共情報コモンズ被害情報最終送信履歴データ
 *
 */
@Entity
@Table(name = "publiccommons_report_data_last_damage")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/10/20 18:30:00")
@lombok.Getter @lombok.Setter
public class PubliccommonsReportDataLastDamage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 公共情報コモンズレポートデータID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long pcommonsreportdataid;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String remarks;

    /** 死者 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String deadpeople;

    /** 行方不明者数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String missingpeople;

    /** 負傷者 重傷 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String seriouslyinjuredpeople;

    /** 負傷者 軽傷 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String slightlyinjuredpeople;

    /** 全壊 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String totalcollapsebuilding;

    /** 全壊 世帯 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String totalcollapsehousehold;

    /** 全壊 人 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String totalcollapsehuman;

    /** 半壊 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String halfcollapsebuilding;

    /** 半壊 世帯 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String halfcollapsehousehold;

    /** 半壊 人 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String halfcollapsehuman;

    /** 一部破壊 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String somecollapsebuilding;

    /** 一部破壊 世帯 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String somecollapsehousehold;

    /** 一部破壊 人 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String somecollapsehuman;

    /** 床上浸水 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String overinundationbuilding;

    /** 床上浸水 世帯 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String overinundationhousehold;

    /** 床上浸水 人 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String overinundationhuman;

    /** 床下浸水 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String underinundationbuilding;

    /** 床下浸水 世帯 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String underinundationhousehold;

    /** 床下浸水 人 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String underinundationhuman;

    /** 公共建物 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String publicbuilding;

    /** その他 棟 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String otherbuilding;

    /** 田_流出埋没 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ricefieldoutflowburied;

    /** 田_冠水 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ricefieldflood;

    /** 畑_流出埋没 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fieldoutflowburied;

    /** 畑_冠水 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fieldflood;

    /** 文教施設 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String educationalfacilities;

    /** 病院 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String hospital;

    /** 道路 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String road;

    /** 橋りょう */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String bridge;

    /** 河川 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String river;

    /** 港湾 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String port;

    /** 砂防 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String sedimentcontrol;

    /** 清掃施設 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String cleaningfacility;

    /** 崖崩れ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String cliffcollapse;

    /** 鉄道不通 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String railwayinterruption;

    /** 被害船舶 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ship;

    /** 水道 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String water;

    /** 電話 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String phone;

    /** 電気 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String electric;

    /** ガス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String gas;

    /** ブロック塀等 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String blockwalls_etc;

    /** り災世帯数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String suffererhousehold;

    /** り災者数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String suffererhuman;

    /** 火災 建物 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String firebuilding;

    /** 火災 危険物 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String firedangerousgoods;

    /** 火災 その他 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String otherfire;

    /** 公共文教施設 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String publicscoolfacillities;

    /** 農林水産業施設 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String agriculturefacilities;

    /** 公共土木施設 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String publicengineeringfacilities;

    /** 施設被害小計 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String subtotaldamagefacilities;

    /** 農業被害 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String farmingdamage;

    /** 林業被害 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String forestrydamage;

    /** 畜産被害 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String animaldamage;

    /** 水産被害 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fisheriesdamage;

    /** 商工被害 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String commerceandindustrydamage;

    /** その他被害 その他 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String otherdamageother;

    /** 被害総計 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String totaldamage;

    /** 文教被害額 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String schoolmount;

    /** 農林被害額 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String farmmount;

    /** その他被害小計 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String subtotalotherdamage;

    /** 消防職員出動延人数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fireman1;

    /** 消防団員出動延人数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fireman2;


    /** 公共情報コモンズレポートデータ */
    @ManyToOne
    @JoinColumn(name="pcommonsreportdataid", referencedColumnName="id")
    public PubliccommonsReportData publiccommonsReportData;
}