/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.log4j.Logger;

/**
 * 地図情報
 *
 */
@Entity
@Table(name = "trackmap_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"trackDatas","tracktableInfos"})
public class TrackmapInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

//    /** 記録ID */
//    @Deprecated
//    @Column(precision = 19, nullable = true, unique = false)
//    public Long trackdataid;

    /** コミュニティID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer communityid;

    /** グループID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer mapgroupid;

    /** 地図ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long mapid;

//    @Deprecated
//    @ManyToOne
//    @JoinColumn(name="trackdataid")
//    public TrackData trackData;

    /** 記録データリスト */
    @OneToMany(mappedBy="trackmapInfo")
    public List<TrackData> trackDatas;

    /** 記録テーブル情報リスト */
    @OneToMany(mappedBy="trackmapInfo")
    public List<TracktableInfo> tracktableInfos;

    /**
     * 地図情報を結合した記録データの開始日時の降順でソートする Comparator
     */
	public static Comparator<TrackmapInfo> orderbyTrackDataStarttimeDesc = new Comparator<TrackmapInfo>() {
		@Override
		public int compare(TrackmapInfo o1, TrackmapInfo o2) {
			Timestamp starttime1 = null;
			if(o1.trackDatas!=null) {
				Collections.sort(o1.trackDatas, TrackData.orderbyStarttimeDesc);
				starttime1 = o1.trackDatas.get(0).starttime;
			}
			Timestamp starttime2 = null;
			if(o2.trackDatas!=null) {
				Collections.sort(o2.trackDatas, TrackData.orderbyStarttimeDesc);
				starttime2 = o2.trackDatas.get(0).starttime;
			}
			if(starttime1!=null && starttime2!=null) return starttime2.compareTo(starttime1);
			else Logger.getLogger(getClass()).warn("Unexpected sort.");
			return 0;
		}
	};
}
