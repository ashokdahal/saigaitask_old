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
 * 地図参照レイヤ情報
 * 
 */
@Entity
@Table(name = "mapreferencelayer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/16 17:00:56")
@lombok.Getter @lombok.Setter
public class MapreferencelayerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 検索フラグのデフォルト値 */
    public static final boolean DEFAULT_SEARCHABLE = true;
    public static final Double DEFAULT_LAYEROPACITY = 1.0;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerid;

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

    /** layeropacityプロパティ */
    @Column(precision = 17, scale = 17, nullable = true, unique = false)
    public Double layeropacity;

    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;
}
