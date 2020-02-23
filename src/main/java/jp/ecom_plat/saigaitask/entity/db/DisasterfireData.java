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
 * 被災状況火災発生
 * 
 */
@Entity
@Table(name = "disasterfire_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/22 12:19:36")
@lombok.Getter @lombok.Setter
public class DisasterfireData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 建物 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fire1;

    /** 危険物 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fire2;

    /** その他 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer fire3;

    /** buildmountプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer buildmount;

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