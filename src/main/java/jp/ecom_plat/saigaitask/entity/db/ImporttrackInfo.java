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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * インポート記録情報
 *
 */
@Entity
@Table(name = "importtrack_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/02/18 15:12:10")
@lombok.Getter @lombok.Setter
public class ImporttrackInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** インポート元自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long oldlocalgovinfoid;

    /** 記録データID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long trackdataid;

    /** インポート元記録データID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long oldtrackdataid;

    /** 地図ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long mapid;

    /** インポート元地図ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long oldmapid;

	/** 記録データ */
	@ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;
}
