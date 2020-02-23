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
 * 被災状況農林被害
 * 
 */
@Entity
@Table(name = "disasterfarm_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/19 12:37:26")
@lombok.Getter @lombok.Setter
public class DisasterfarmData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 田・流出・埋没 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float field1;

    /** 田・冠水 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float field2;

    /** 畑・流出・埋没 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float farm1;

    /** 畑・冠水 */
    @Column(precision = 8, scale = 8, nullable = true, unique = false)
    public Float farm2;

    /** 被害額 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer farmmount;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    @ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;
}
