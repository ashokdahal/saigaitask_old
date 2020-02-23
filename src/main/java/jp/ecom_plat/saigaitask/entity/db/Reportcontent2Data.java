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
 * 報告内容データ２
 * 
 */
@Entity
@Table(name = "reportcontent2_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/22 12:59:30")
@lombok.Getter @lombok.Setter
public class Reportcontent2Data implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long reportdataid;

    /** 報告日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String reporttime;

    /** 都道府県 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String pref;

    /** 市町村 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String city;

    /** 災害名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String disastername;

    /** 報告数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer reportno;

    /** 報告者氏名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String reporter;

    /** 死者 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties21;

    /** 行方不明者 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties22;

    /** 負傷者（重傷） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties23;

    /** 負傷者（軽傷） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties24;

    /** 全壊（棟） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houseall1;

    /** 全壊（世帯） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houseall2;

    /** 全壊（人） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houseall3;

    /** 半壊（棟） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer househalf1;

    /** 半壊（世帯） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer househalf2;

    /** 半壊（人） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer househalf3;

    /** 一部破損（棟） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer housepart1;

    /** 一部破損（世帯） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer housepart2;

    /** 一部破損（人） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer housepart3;

    /** 床上浸水（棟） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houseupper1;

    /** 床上浸水（世帯） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houseupper2;

    /** 床上浸水（人） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houseupper3;

    /** 床下浸水（棟） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houselower1;

    /** 床下浸水（世帯） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houselower2;

    /** 床下浸水（人） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer houselower3;

    /** 公共の建物（非住家） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer build1;

    /** その他（非住家） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer build2;

    /** 田・流出・埋没 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float field1;

    /** 田・冠水 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float field2;

    /** 畑・流出・埋没 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float farm1;

    /** 畑・冠水 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float farm2;

    /** 文教施設 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer school;

    /** 病院 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer hospital;

    /** 道路 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer road;

    /** 橋梁 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer bridge;

    /** 河川 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer river;

    /** 港湾 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer harbor;

    /** 砂防 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer landslide;

    /** 清掃施設 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer gabage;

    /** かけ崩れ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer cliff;

    /** 鉄道不通 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer railway;

    /** 被害船舶 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer ship;

    /** 水道 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer water;

    /** 電話 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer telephone;

    /** 電気 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer electricity;

    /** ガス */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer gas;

    /** ブロック塀等 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer block;

    /** 罹災世帯数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer suffer1;

    /** 罹災者数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer suffer2;

    /** 建物 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fire1;

    /** 危険物 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fire2;

    /** その他 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fire3;

    /** 公共文教施設 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount1;

    /** 農林水産業施設 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount2;

    /** 公共土木施設 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount3;

    /** その他公共施設 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount4;

    /** 小計 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer subtotal;

    /** 農業被害 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount5;

    /** 林業被害 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount6;

    /** 畜産被害 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount7;

    /** 水産被害 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount8;

    /** 商工被害 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount9;

    /** その他被害 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer amount10;

    /** 被害総額 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer atotal;

    /** 本部名 もしくは 都道府県 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String headoffice21;

    /** 本部設置 もしくは 市町村 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String headoffice22;

    /** 解散 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String headoffice23;

    /** disastercityプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String disastercity;

    /** numcityプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer numcity;

    /** 消防職員出動延人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fireman1;

    /** 消防団員出動延人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fireman2;

    /** 住民避難の状況 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String status;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note2;
    
	@ManyToOne
	@JoinColumn(name = "reportdataid")
	public ReportData reportData;
}
