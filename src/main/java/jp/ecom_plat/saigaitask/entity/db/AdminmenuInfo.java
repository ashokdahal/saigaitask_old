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
 * AdminmenuInfoエンティティクラス
 *
 */
@Entity
@Table(name="adminmenu_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/11/13 19:59:04")
@lombok.Getter @lombok.Setter
public class AdminmenuInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** idプロパティ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** classifyプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String classify;

    /** nameプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String name;

    /** levelプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer level;

    /** publicmodeプロパティ */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer publicmode;

    /** groupidプロパティ */
    @Column(precision = 19, nullable = false, unique = false)
    public Long groupid;

    /** unitidプロパティ */
    @Column(precision = 19, nullable = false, unique = false)
    public Long unitid;

    /** urlプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String url;

    /** validプロパティ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean valid;

    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;
}