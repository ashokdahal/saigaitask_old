/* Copyright (c) 2013 National Research Institute for Earth Science and
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
 * 体制区分
 * 
 */
@Entity
@Table(name = "stationclass_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/17 20:18:30")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"assembleInfoList","meteotriggerInfoList","stationalarmInfoList"})
public class StationclassInfo implements Serializable {

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
    /*@Column(precision = 10, nullable = true, unique = false)
    public Integer disasterid;*/

    /** 体制ID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer stationid;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 出動職員 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String staff;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 職員参集情報リスト */
    @OneToMany(mappedBy = "stationclassInfo")
    public List<AssembleInfo> assembleInfoList;

    /** 体制マスタ */
    @ManyToOne
    @JoinColumn(name="stationid")
    public StationMaster stationMaster;

    /** 災害種別マスタ */
    /*@ManyToOne
    @JoinColumn(name="disasterid")
    public DisasterMaster disasterMaster;
	*/
    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 気象情報トリガー情報リスト */
    @OneToMany(mappedBy = "stationclassInfo")
    public List<MeteotriggerInfo> meteotriggerInfoList;
    
    /** 体制アラート情報 */
    @OneToMany(mappedBy = "stationclassInfo")
    public List<StationalarmInfo> stationalarmInfoList;    
}
