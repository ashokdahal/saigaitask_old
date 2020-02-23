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
 * 被災状況履歴データ
 * 
 */
@Entity
@Table(name = "disastersituationhistory_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/03/11 13:32:15")
@lombok.Getter @lombok.Setter
public class DisastersituationhistoryData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 被災集計履歴ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long disastersummaryhistoryid;

    /** 行番号 */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer lineno;

    /** 被害項目 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String damageitem;

    /** 表示フラグ	 true:表示,false:非表示 */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean dispflag;

    /** 単位 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String unit;

    /** 地区１人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area1people;

    /** 地区２人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area2people;

    /** 地区３人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area3people;

    /** 地区４人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area4people;

    /** 地区５人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area5people;

    /** 地区６人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area6people;

    /** 地区７人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area7people;

    /** 地区８人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area8people;

    /** 地区９人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area9people;

    /** 地区１０人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area10people;

    /** 地区１１人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area11people;

    /** 地区１２人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area12people;

    /** 地区１３人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area13people;

    /** 地区１４人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area14people;

    /** 地区１５人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area15people;

    /** 地区１６人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area16people;

    /** 地区１７人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area17people;

    /** 地区１８人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area18people;

    /** 地区１９人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area19people;

    /** 地区２０人数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer area20people;

    /** 合計自動集計 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer autototal;

    /** 合計手入力 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer manualtotal;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    @ManyToOne
    @JoinColumn(name="disastersummaryhistoryid")
    public DisastersummaryhistoryData disastersummaryhistoryData;
}