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
 * 公共情報コモンズ避難勧告／避難指示最終送信履歴データ
 *
 */
@Entity
@Table(name = "publiccommons_report_data_last_refuge")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/04/07 13:54:30")
@lombok.Getter @lombok.Setter
public class PubliccommonsReportDataLastRefuge implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 公共情報コモンズレポートデータID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long pcommonsreportdataid;

    /** 地区名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String chikuname;

    /** 発令状況 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String hatureikbn;

    /** 発令or解除 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String issueorlift;

    /** 人数 */
    @Column(precision = 10, nullable = false, unique = true)
    public Integer people;

    /** 対象世帯数 */
    @Column(precision = 10, nullable = false, unique = true)
    public Integer targethouseholds;

    /** 発令日時 */
    @Column(nullable = true, unique = false)
    public Timestamp hatureidatetime;

    /** 公共情報コモンズレポートデータ */
    @ManyToOne
    @JoinColumn(name="pcommonsreportdataid", referencedColumnName="id")
    public PubliccommonsReportData publiccommonsReportData;
}