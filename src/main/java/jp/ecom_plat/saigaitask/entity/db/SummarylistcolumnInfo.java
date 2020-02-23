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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 集計リスト項目情報
 *
 */
@Entity
@Table(name = "summarylistcolumn_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/06/03 10:42:30")
@lombok.Getter @lombok.Setter
public class SummarylistcolumnInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /**
     * メニューID
     * V2.0 で廃止されたが、旧バージョンのインポート用で残しておく
     */
    //@Deprecated
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** 演算 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String function;

    /** 絞り込み条件 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String condition;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 集計リスト情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long summarylistinfoid;

    /** 集計結果保存属性 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrid;

    /** 集計リスト情報 */
    @ManyToOne
    @JoinColumn(name="summarylistinfoid")
    public SummarylistInfo summarylistInfo;
}
