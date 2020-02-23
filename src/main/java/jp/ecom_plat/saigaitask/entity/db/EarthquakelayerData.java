/* Copyright (c) 2015 National Research Institute for Earth Science and
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
 * 震度レイヤデータ
 *
 */
@Entity
@Table(name = "earthquakelayer_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/09/09 17:42:30")
@lombok.Getter @lombok.Setter
public class EarthquakelayerData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 気象情報取得データID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long meteodataid;

    /** 震度グループレイヤID */
    /** 時系列版ではグループレイヤを使用しない */
    @Column(precision = 19, nullable = true, unique = false)
    @Deprecated
    public Long earthquakegrouplayerid;

    /** 地震識別番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String eventid;

    /** 地震発生日時 */
    @Column(nullable = true, unique = false)
    public Timestamp origintime;

    /** 発表時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp reportdatetime;

    /** レイヤID */
    /** 時系列版ではレイヤは１つのみ使用かつearthquakelayer_infoで管理する */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerid;

    /** レイヤ名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 震度レイヤ設定ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long earthquakelayerinfoid;

    /** 気象情報取得データ */
    @ManyToOne
    @JoinColumn(name = "meteodataid", referencedColumnName = "id")
    public MeteoData meteoData;

    /** 震度情報グループレイヤデータ */
    @ManyToOne
    @JoinColumn(name = "earthquakegrouplayerid", referencedColumnName = "id")
    @Deprecated
    public EarthquakegrouplayerData earthquakegrouplayerData;

    /** 震度レイヤ設定 */
    @ManyToOne
    @JoinColumn(name = "earthquakelayerinfoid", referencedColumnName = "id")
    public EarthquakelayerInfo earthquakelayerInfo;
}
