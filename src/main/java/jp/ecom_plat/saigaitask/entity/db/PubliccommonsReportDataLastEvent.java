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
 * 公共情報コモンズイベント最終送信履歴データ
 *
 */
@Entity
@Table(name = "publiccommons_report_data_last_event")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/04/07 13:54:30")
@lombok.Getter @lombok.Setter
public class PubliccommonsReportDataLastEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 公共情報コモンズレポートデータID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long pcommonsreportdataid;

    /** 分類 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String division;

    /** 場所 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area;

    /** タイトル */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String title;

    /** 本文 */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String text;

    /** 告知URI */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String notificationuri;

    /** ファイルURI */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fileuri;

    /** メディアタイプ */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mediatype;

    /** MIMEタイプ */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mimetype;

    /** ドキュメントID */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String documentid;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 版数 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long documentrevision;

    /** 情報識別区分 */
    @Column(length = 2147483647, nullable = true, unique = false)
	public String disasterinformationtype;

    /** 希望公開終了日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String validdatetime;

    /** 更新種別 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String distributiontype;

    /** 開催開始日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String eventfrom;

    /** 開催終了日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String eventto;

    /** 参加料金 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String eventfee;

    /** ファイルタイトル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String filecaption;

    /** メールタイトル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String emailtitle;

    /** 送信本文 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
	public String content;

    /** 公共情報コモンズレポートデータ */
    @ManyToOne
    @JoinColumn(name="pcommonsreportdataid", referencedColumnName="id")
    public PubliccommonsReportData publiccommonsReportData;
}