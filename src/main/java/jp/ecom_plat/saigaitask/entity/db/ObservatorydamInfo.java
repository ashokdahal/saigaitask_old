/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.text.DecimalFormat;

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
 * ダム観測所情報
 *
 */
@Entity
@Table(name = "observatorydam_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/24 19:23:32")
@lombok.Getter @lombok.Setter
public class ObservatorydamInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** エリア番号 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer areacode;

    /** 管理事務所番号 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer officecode;

    /** 管理事務所名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officename;

    /** 所管機関コード */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer jurisfacilitycode;

    /** 所管機関名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String jurisfacilityname;

    /** 観測所番号 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer obsrvtncode;

    /** 観測所名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** ふりがな */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String readname;

    /** 緯度 */
    @Column(precision = 17, scale = 17, nullable = true, unique = false)
    public Double latitude;

    /** 経度 */
    @Column(precision = 17, scale = 17, nullable = true, unique = false)
    public Double longitude;

    /** 都道府県 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String prefname;

    /** 所在地 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String address;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** フィーチャＩＤ */
    @Column(precision = 19, nullable = true, unique = false)
    public Long featureid;

    /** true:iframe, false:popup */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean iframe;

    /** URL */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String url;

    /** 幅 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer width;

    /** 高さ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer height;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;

    /**
     * @return 観測所フルコードを返す。
     */
    public Long getPointFullCode() {
    	try {
        	DecimalFormat df = new DecimalFormat("00000");
        	String code = ""+officecode+"007"+df.format(obsrvtncode);
        	return Long.parseLong(code);
		} catch (Exception e) {}
    	return null;
    }
}
