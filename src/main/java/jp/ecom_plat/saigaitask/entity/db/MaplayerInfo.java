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
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 地図レイヤ情報
 * 
 */
@Entity
@Table(name = "maplayer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"maplayerattrInfos"})
public class MaplayerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 検索フラグのデフォルト値 */
    public static final boolean DEFAULT_SEARCHABLE = true;

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

    /** 初期表示フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean visible;

    /** 凡例折りたたみ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean closed;

    /** 編集フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean editable;

    /** 追加フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean addable;

    /** 検索フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean searchable;

    /** スナップフラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean snapable;

    /** 切り出しレイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String intersectionlayerid;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 削除フラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted;

    /** 地図レイヤ属性情報リスト */
    @OneToMany(mappedBy="maplayerInfo")
    public List<MaplayerattrInfo> maplayerattrInfos;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;

    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** メニューテーブル情報 */
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="menuinfoid", referencedColumnName="menuinfoid"),
        @JoinColumn(name="tablemasterinfoid", referencedColumnName="tablemasterinfoid")
    })
    public MenutableInfo menutableInfo;
}
