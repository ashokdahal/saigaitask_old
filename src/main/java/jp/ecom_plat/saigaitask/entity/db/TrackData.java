/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
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
 * 記録データ
 *
 */
@Entity
@Table(name = "track_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class TrackData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 地図情報ID(複合災害の場合) */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackmapinfoid;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** デモID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long demoinfoid;

    /** 災害種別ID */
    /*@Column(precision = 10, nullable = true, unique = false)
    public Integer disasterid;*/

    /** 訓練プランID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trainingplandataid;

    /** 災害名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 記録開始時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp starttime;

    /** 記録終了時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp endtime;

    /** 自治体グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovgroupinfoid;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;

    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    @ManyToOne
    @JoinColumn(name="demoinfoid")
    public DemoInfo demoInfo;

    /*@ManyToOne
    @JoinColumn(name="disasterid")
    public DisasterMaster disasterMaster;
	*/
    @ManyToOne
    @JoinColumn(name="trackmapinfoid")
    public TrackmapInfo trackmapInfo;

    @ManyToOne
    @JoinColumn(name="trainingplandataid")
    public TrainingplanData trainingplanData;

    @OneToMany(mappedBy="prefTrackData")
    public List<TrackgroupData> prefTrackgroupDatas;

    @OneToMany(mappedBy="cityTrackData")
    public List<TrackgroupData> cityTrackgroupDatas;

    /** localgovgroupinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "localgovgroupinfoid", referencedColumnName = "id")
    public LocalgovgroupInfo localgovgroupinfo;

    /**
     * 開始日時の降順でソートする Comparator
     */
    public static Comparator<TrackData> orderbyStarttimeDesc = new Comparator<TrackData>() {
		@Override
		public int compare(TrackData o1, TrackData o2) {
			return o2.starttime.compareTo(o1.starttime);
		}
	};

	/**
	 * @return 訓練フラグ
	 */
	public boolean isTraining() {
		return trainingplandataid==null ? false : 0<trainingplandataid;
	}
}
