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
 * 報告内容データ
 * 
 */
@Entity
@Table(name = "reportcontent_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/22 12:59:16")
@lombok.Getter @lombok.Setter
public class ReportcontentData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long reportdataid;

    /** 消防庁受信者氏名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String receiver;

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

    /** 報告者氏名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String reporter;

    /** 災害名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String disastername;

    /** 報告数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer reportno;

    /** 発生場所 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String place;

    /** 発生時刻 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String occurtime;

    /** 概況 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String summary;

    /** 死者 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties1;

    /** 不明 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties2;

    /** 負傷者 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer casualties3;

    /** 合計 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer total;

    /** 住家全壊 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer house1;

    /** 住家半壊 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer house2;

    /** 一部破損 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer house3;

    /** 床上浸水 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer house4;

    /** 災害対策本部等の設置状況（都道府県） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String headoffice1;

    /** 災害対策本部等の設置状況（市町村） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String headoffice2;

    /** 被害の状況 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String status1;

    /** 応急対策の状況 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String status2;
    
	@ManyToOne
	@JoinColumn(name = "reportdataid")
	public ReportData reportData;
}
