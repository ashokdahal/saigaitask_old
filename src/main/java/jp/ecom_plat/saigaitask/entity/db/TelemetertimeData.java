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
import javax.persistence.Table;

/**
 * テレメータ時刻データ
 * 
 */
@Entity
@Table(name = "telemetertime_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/28 0:02:14")
@lombok.Getter @lombok.Setter
public class TelemetertimeData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 観測所コード */
    @Column(precision = 19, nullable = true, unique = false)
    public Long code;

    /** データ項目コード */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer itemcode;

    /** コンテンツコード */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer contentscode;

    /** observtimeプロパティ */
    @Column(nullable = true, unique = false)
    public Timestamp observtime;

    /** 値 */
    @Column(nullable = true, unique = false)
    public Timestamp val;
}
