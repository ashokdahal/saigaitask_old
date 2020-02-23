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
 * メニュープロセス情報
 *
 */
@Entity
@Table(name = "menuprocess_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class MenuprocessInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニュー設定情報 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menulogininfoid;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 重要フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean important = false;

    /** 表示・非表示 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean visible;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 削除フラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted;

    /** メニュータスク情報リスト */
    @OneToMany(mappedBy="menuprocessInfo")
    public List<MenutaskInfo> menutaskInfos;

    /** メニュー設定情報 */
    @ManyToOne
    @JoinColumn(name="menulogininfoid")
    public MenuloginInfo menuloginInfo;
}
