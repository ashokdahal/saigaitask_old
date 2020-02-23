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
 * ログ項目リスト
 *
 */
@Entity
@Table(name = "historycolumnlist_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/10/07 16:39:38")
@lombok.Getter @lombok.Setter
public class HistorycolumnlistInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 履歴テーブルID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long historytableinfoid;

    /** テーブルカラム名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrId;

    /** 項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrName;

    /** このカラムが変更されたときにログを記録するか */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean doLog;

    /** 数値情報か */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean isNumeric;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="historytableinfoid")
    public HistorytableInfo historytableInfo;
}