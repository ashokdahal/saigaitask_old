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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * メニューテーブル情報
 *
 */
@Entity
@Table(name = "menutable_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/16 14:47:33")
@lombok.Getter @lombok.Setter
public class MenutableInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 追加フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean addable;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deletable;

    /** 合計フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean totalable;

    /** 削除フラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted;

    /** アコーディオン属性ID */
    @Column(length = 1, nullable = true, unique = false)
    public String accordionattrid;

    /** アコーディオン属性名*/
    @Column(length = 1, nullable = true, unique = false)
    public String accordionname;

    /** アコーディオン初期解放 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean accordionopen;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;

    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** テーブルリスト項目情報リスト */
    @OneToMany(mappedBy = "menutableInfo")
    public List<TablelistcolumnInfo> tablelistcolumnInfoList;

    /** 地図レイヤ情報リスト */
    @OneToMany(mappedBy = "menutableInfo")
    public List<MaplayerInfo> maplayerInfoList;

}
