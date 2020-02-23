/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 報告データ
 *
 */
@Entity
@Table(name = "report_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/22 12:50:30")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"reportcontentDatas","reportcontent2Datas"})
public class ReportData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** ファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String filepath;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 登録時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

	@ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;

	/** その１(災害概況即報) */
	@OneToMany(mappedBy="reportData")
	public List<ReportcontentData> reportcontentDatas;

	/** その２ */
	@OneToMany(mappedBy="reportData")
	public List<Reportcontent2Data> reportcontent2Datas;
}
