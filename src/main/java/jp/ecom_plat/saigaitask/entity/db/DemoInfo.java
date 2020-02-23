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
 * 訓練情報
 * 
 */
@Entity
@Table(name = "demo_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/10/16 15:56:11")
@lombok.Getter @lombok.Setter
public class DemoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 災害種別ID */
    //@Column(precision = 10, nullable = true, unique = false)
    //public Integer disasterid;

    /** トリガーとなるファイルURL */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String triggerurl;

    /** 気象情報等取得情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long meteorequestinfoid;

    /** 訓練名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 災害種別マスタ */
    /*@ManyToOne
    @JoinColumn(name="disasterid")
    public DisasterMaster disasterMaster;
	*/
    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 気象情報等取得情報 */
    @ManyToOne
    @JoinColumn(name="meteorequestinfoid")
    public MeteorequestInfo meteorequestInfo;
}
