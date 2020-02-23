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
 * 体制アラート情報
 *
 */
@Entity
@Table(name = "stationalarm_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/11/13 19:07:57")
@lombok.Getter @lombok.Setter
public class StationalarmInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** アラームメッセージID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long alarmmessageinfoid;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 体制ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long stationclassinfoid;

    /** 元体制ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long stationclassorgid;

    /** 体制区分 **/
    @ManyToOne
    @JoinColumn(name="stationclassinfoid")
    public StationclassInfo stationclassInfo;
    @ManyToOne
    @JoinColumn(name="stationclassorgid")
    public StationclassInfo stationclassOrgInfo;

    /** テーブルマスター情報 **/
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;

    /** アラームメッセージ設定 **/
    @ManyToOne
    @JoinColumn(name="alarmmessageinfoid")
    public AlarmmessageInfo alarmmessageInfo;

    /** 地方自治体情報 **/
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
