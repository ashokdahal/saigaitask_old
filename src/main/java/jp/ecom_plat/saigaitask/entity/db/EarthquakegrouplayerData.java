/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 震度グループレイヤデータ
 * 時系列版ではearthquakelayer_infoとearthquakelayer_dataのセットのみで管理する為、本テーブルは使用しない
 *
 */
@Entity
@Table(name = "earthquakegrouplayer_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/09/09 17:42:30")
@Deprecated
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"earthquakelayerDatas"})
public class EarthquakegrouplayerData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 地図情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackmapinfoid;

    /** 地図マスタID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long mapmasterinfoid;

    /** レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerid;

    /** 地図情報 */
    @ManyToOne
	@JoinColumn(name = "trackmapinfoid")
	public TrackmapInfo trackmapInfo;

    /** 地図マスター情報 */
    @ManyToOne
	@JoinColumn(name = "mapmasterinfoid")
	public MapmasterInfo mapmasterInfo;

    /** 震度情報レイヤデータリスト */
    @OneToMany(mappedBy = "earthquakegrouplayerData")
    public List<EarthquakelayerData> earthquakelayerDatas;
}
