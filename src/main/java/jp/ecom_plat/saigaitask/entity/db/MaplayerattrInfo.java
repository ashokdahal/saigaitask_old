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
 * 地図レイヤ属性情報
 *
 */
@Entity
@Table(name = "maplayerattr_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/16 16:47:56")
@lombok.Getter @lombok.Setter
public class MaplayerattrInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 地図レイヤID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long maplayerinfoid;

    /** 属性項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrid;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 編集フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean editable;

    /** ハイライト */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean highlight;

    /** グループ化 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean grouping;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 初期チェック */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean defaultcheck = false;

    /** グループ初期チェック */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean groupdefaultcheck = false;

    /** 一括追記 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean addable = false;
    /** 地図レイヤ情報 */
    @ManyToOne
    @JoinColumn(name="maplayerinfoid")
    public MaplayerInfo maplayerInfo;
}
