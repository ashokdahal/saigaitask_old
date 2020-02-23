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
 * メニュー設定情報
 *
 */
@Entity
@Table(name = "menulogin_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/16 10:37:51")
@lombok.Getter @lombok.Setter
public class MenuloginInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** ユニットID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 災害種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disasterid;

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

    /** 災害種別マスタ */
    @JoinColumn(name="disasterid")
    @ManyToOne
    public DisasterMaster disasterMaster;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;

    /** 課情報 */
    @ManyToOne
    @JoinColumn(name="unitid")
    public UnitInfo unitInfo;

    /** メニュープロセス情報リスト */
    @OneToMany(mappedBy = "menuloginInfo")
    public List<MenuprocessInfo> menuprocessInfoList;
}
