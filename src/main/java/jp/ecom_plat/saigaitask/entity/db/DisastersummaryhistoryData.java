/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
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
 * 被災集計履歴データ
 *
 */
@Entity
@Table(name = "disastersummaryhistory_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/03/11 17:38:36")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"disastersituationhistoryDataList"})
public class DisastersummaryhistoryData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自冶体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** 記録ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long trackdataid;

    /** 期間 */
    @Column(nullable = false, unique = false)
    public Timestamp period;

    /** ユーザ日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String usertime;

    /** 地区数 */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer areanum;

    /** 地区１ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area1;

    /** 地区２ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area2;

    /** 地区３ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area3;

    /** 地区４ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area4;

    /** 地区５ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area5;

    /** 地区６ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area6;

    /** 地区７ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area7;

    /** 地区８ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area8;

    /** 地区９ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area9;

    /** 地区１０ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area10;

    /** 地区１１ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area11;

    /** 地区１２ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area12;

    /** 地区１３ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area13;

    /** 地区１４ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area14;

    /** 地区１５ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area15;

    /** 地区１６ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area16;

    /** 地区１７ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area17;

    /** 地区１８ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area18;

    /** 地区１９ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area19;

    /** 地区２０ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area20;

    @OneToMany(mappedBy = "disastersummaryhistoryData")
    public List<DisastersituationhistoryData> disastersituationhistoryDataList;

    /** 自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}