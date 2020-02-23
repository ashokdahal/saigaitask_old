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
 * 気象情報取得データ
 * 
 */
@Entity
@Table(name = "meteo_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 19:01:49")
@lombok.Getter @lombok.Setter
public class MeteoData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 気象情報取得ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long meteorequestinfoid;

    /** XMLファイルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long meteoid;

    /** 報告日時 */
    @Column(nullable = true, unique = false)
    public Timestamp reporttime;

    /** ファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String filepath;

	@ManyToOne
	@JoinColumn(name = "meteorequestinfoid")
	public MeteorequestInfo meteorequestInfo;
}
