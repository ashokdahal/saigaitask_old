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
 * 履歴テーブル情報
 * 
 */
@Entity
@Table(name = "historytable_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/10/08 0:42:35")
@lombok.Getter @lombok.Setter
public class HistorytableInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 地図情報 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackmapinfoid;
   
    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 履歴テーブル名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String historytablename;

    /** ユニークなカラムの名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String idColumn;

    /** 記録地図情報 */
    @ManyToOne
    @JoinColumn(name="trackmapinfoid")
    public TrackmapInfo trackmapInfo;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}