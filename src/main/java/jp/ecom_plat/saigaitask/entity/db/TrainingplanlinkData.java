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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;

/**
 * 訓練プラン連携自治体情報
 * 
 */
@Entity
@Table(name = "trainingplanlink_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/05/13 16:40:31")
@lombok.Getter @lombok.Setter
public class TrainingplanlinkData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 訓練プラン連携情報ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 訓練プランID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trainingplandataid;

    /** 訓練プラン連携中自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 更新時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp updatetime;

    @ManyToOne
    @JoinColumn(name="trainingplandataid")
    public TrainingplanData trainingplanData;

    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
