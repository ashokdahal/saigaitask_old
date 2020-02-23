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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 公共情報コモンズ発信データ
 *
 */
@Entity
@Table(name = "publiccommons_report_data_last_antidisaster")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/04/07 13:54:30")
@lombok.Getter @lombok.Setter
public class PubliccommonsReportDataLastAntidisaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 公共情報コモンズレポートデータID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long pcommonsreportdataid;

    /** 設置・解散日時 */
    @Column(nullable = true, unique = false)
    public Timestamp hatureidatetime;

    /** 設置or解散 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String issueorlift;

    /** 本部種別 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String antidisasterkbn;

    /** 本部名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 公共情報コモンズレポートデータ */
    @OneToOne
    @JoinColumn(name="pcommonsreportdataid", referencedColumnName="id")
    public PubliccommonsReportData publiccommonsReportData;
}