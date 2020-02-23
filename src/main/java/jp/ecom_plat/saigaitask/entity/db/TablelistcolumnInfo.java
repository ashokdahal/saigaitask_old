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
 * テーブルリスト項目情報
 *
 */
@Entity
@Table(name = "tablelistcolumn_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class TablelistcolumnInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューテーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menutableinfoid;

    /** テーブル項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrid;

    /** 項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 編集可 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean editable;

    /** 強調表示 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean highlight;

    /** グループ化 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean grouping;

    /** ソート可 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean sortable;

    /** デフォルトソート */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer defaultsort;

    /** アップロード可 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean uploadable;

    /** ログ出力可 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean loggable;

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

    /** 属性行スタイル情報リスト */
    @OneToMany(mappedBy = "tablelistcolumnInfo")
    public List<TablerowstyleInfo> tablerowstyleInfoList;

    /** メニューテーブル情報 */
    @ManyToOne
    @JoinColumn(name="menutableinfoid")
    public MenutableInfo menutableInfo;
}
