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
 * タスクメニュー情報
 *
 */
@Entity
@Table(name = "menutaskmenu_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/04 15:55:29")
@lombok.Getter @lombok.Setter
public class MenutaskmenuInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** タスクID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menutaskinfoid;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** 重要フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean important = false;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** メニュータスク情報 */
    @ManyToOne
    @JoinColumn(name="menutaskinfoid")
    public MenutaskInfo menutaskInfo;


    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;
}
