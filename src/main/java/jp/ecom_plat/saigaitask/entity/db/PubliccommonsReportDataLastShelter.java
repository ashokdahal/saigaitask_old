/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;

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
 * 公共情報コモンズ避難所最終送信履歴管理テーブル
 *
 */
@Entity
@Table(name = "publiccommons_report_data_last_shelter")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 16:51:19")
@lombok.Getter @lombok.Setter
public class PubliccommonsReportDataLastShelter implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録データID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long pcommonsreportdataid;

    /** 地区名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String chikuname;

    /** 閉鎖時間 */
    @Column(nullable = true, unique = false)
    public Timestamp closetime;

    /** 開設時間 */
    @Column(nullable = true, unique = false)
    public Timestamp setuptime;

    /** 避難所住所 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String shelteraddress;

    /** 収容人数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String sheltercapacity;

    /** 避難所FAX */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String shelterfax;

    /** 避難所名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String sheltername;

    /** 避難所電話 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String shelterphone;

    /** 代表者氏名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String shelterstaff;

    /** 避難所状態 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String shelterstatus;

    /** 座標  */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String circle;

	/** 種別 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String type;

	/** 種別詳細(種別だけで表現しきれない時) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String typedetail;

	/** 避難人数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String headcount;

	/** 避難人数(うち自主避難) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String headcountvoluntary;

	/** 避難世帯数 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String households;

	/** 避難世帯数(うち自主避難) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String householdsvoluntary;

    /** 公共情報コモンズレポートデータ */
    @ManyToOne
    @JoinColumn(name="pcommonsreportdataid", referencedColumnName="id")
    public PubliccommonsReportData publiccommonsReportData;
}