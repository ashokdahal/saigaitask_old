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
 * 本部会議データ
 * 
 */
@Entity
@Table(name = "headoffice_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/18 11:17:07")
@lombok.Getter @lombok.Setter
public class HeadofficeData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 会議名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** ファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String filepath;

    /** 開催日時 */
    @Column(nullable = true, unique = false)
    public Timestamp meetingtime;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    @ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;
}
