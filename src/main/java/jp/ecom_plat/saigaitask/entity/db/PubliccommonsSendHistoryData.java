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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 公共情報コモンズ発信履歴データ
 *
 */
@Entity
@Table(name = "publiccommons_send_history_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 16:52:44")
@lombok.Getter @lombok.Setter
public class PubliccommonsSendHistoryData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 公共情報コモンズ発信データID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long publiccommonsReportDataId;

    /** 公共情報コモンズ発信先データID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer publiccommonsSendToInfoId;

    /** 発信日時 */
    @Column(nullable = true, unique = false)
    public Timestamp sendtime;

    /** 発信成功フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean success;

	@ManyToOne
	@JoinColumn(name = "publiccommons_report_data_id")
	public PubliccommonsReportData publiccommonsReportData;

	@ManyToOne
	@JoinColumn(name = "publiccommons_send_to_info_id")
	public PubliccommonsSendToInfo publiccommonsSendToInfo;
}
