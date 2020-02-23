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
 * 訓練外部XMLデータ
 * 
 */
@Entity
@Table(name = "trainingmeteo_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/05/13 16:28:26")
@lombok.Getter @lombok.Setter
public class TrainingmeteoData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 訓練プランID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trainingplandataid;

    /** 気象情報XMLファイルURL */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String meteourl;

    /** 気象情報種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer meteotypeid;

    /** 外部データ設定の名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 外部データ設定の備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 更新時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp updatetime;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;

    /** 訓練情報 */
    @ManyToOne
    @JoinColumn(name="trainingplandataid")
    public TrainingplanData trainingplanData;

    /** 気象情報等取得種別マスタ */
    @ManyToOne
    @JoinColumn(name="meteotypeid")
    public MeteotypeMaster meteotypeMaster;

}
