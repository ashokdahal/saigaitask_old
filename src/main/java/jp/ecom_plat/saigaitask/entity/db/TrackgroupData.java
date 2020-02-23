/* Copyright (c) 2015 National Research Institute for Earth Science and
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
 * 記録グループデータ
 *
 */
@Entity
@Table(name = "trackgroup_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/08/05 16:22:55")
@lombok.Getter @lombok.Setter
public class TrackgroupData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 記録グループID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録データID(都道府県) */
    @Column(precision = 19, nullable = true, unique = false)
    public Long preftrackdataid;

    /** 記録データID(市町村) */
    @Column(precision = 19, nullable = true, unique = false)
    public Long citytrackdataid;

    @ManyToOne
    @JoinColumn(name="preftrackdataid")
    public TrackData prefTrackData;

    @ManyToOne
    @JoinColumn(name="citytrackdataid")
    public TrackData cityTrackData;

}
