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
 * 気象情報等取得情報
 *
 */
@Entity
@Table(name = "meteorequest_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 18:59:48")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"meteotriggerInfoList","demoInfoList"})
public class MeteorequestInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 気象情報種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer meteotypeid;

    /** エリアID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String meteoareaid;

    /** エリアID予備 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String meteoareaid2;

    /** アラームフラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean alarm;

    /** 表示フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean view;

//    /** 地図表示フラグ */
//    @Column(length = 1, nullable = true, unique = false)
//    public Boolean map;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 気象情報等取得種別マスタ */
    @ManyToOne
    @JoinColumn(name="meteotypeid")
    public MeteotypeMaster meteotypeMaster;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 気象情報トリガー情報リスト */
    @OneToMany(mappedBy = "meteorequestInfo")
    public List<MeteotriggerInfo> meteotriggerInfoList;

    /** 訓練情報リスト */
    @OneToMany(mappedBy = "meteorequestInfo")
    public List<DemoInfo> demoInfoList;
}
