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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * テレメータ観測所情報
 *
 */
@Entity
@Table(name = "telemeterpoint_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/27 23:48:06")
@lombok.Getter @lombok.Setter
public class TelemeterpointInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 管理事務所ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long telemeterofficeinfoid;

    /** データ種別コード */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer itemkindcode;

    /** 観測所コード */
    @Column(precision = 19, nullable = true, unique = false)
    public Long pointcode;

    /** アイテムコードのCSV */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String itemcode;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効／無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** テレメータ管理事務所情報 */
    @ManyToOne
    @JoinColumn(name="telemeterofficeinfoid")
    public TelemeterofficeInfo telemeterofficeInfo;
}
