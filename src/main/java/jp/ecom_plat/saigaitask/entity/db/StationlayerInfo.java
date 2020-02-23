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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 体制レイヤ情報
 *
 */
@Entity
@Table(name = "stationlayer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/09/09 17:42:30")
@lombok.Getter @lombok.Setter
public class StationlayerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 体制区分の属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String stationclassattrid;

    /** 移行時間の属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String shifttimeattrid;

    /** 登録時間の属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String registtimeattrid;

    /** 終了時間の属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String closetimeattrid;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}
