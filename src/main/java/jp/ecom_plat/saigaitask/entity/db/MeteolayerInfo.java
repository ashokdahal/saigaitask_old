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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 気象情報レイヤ情報
 *
 */
@Entity
@Table(name = "meteolayer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/09/09 17:42:30")
@lombok.Getter @lombok.Setter
public class MeteolayerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** 気象情報取得種別ID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer meteotypeid;

    /** 初期表示フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean visible;

    /** 凡例折りたたみ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean closed;

    /** 検索フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean searchable;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** 気象情報等取得種別マスタ */
    @ManyToOne
    @JoinColumn(name="meteotypeid")
    public MeteotypeMaster meteotypeMaster;

}
