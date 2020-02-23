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
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 外部地図データ情報
 *
 */
@Entity
@Table(name = "externalmapdata_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/03/12 18:24:21")
@lombok.Getter @lombok.Setter
public class ExternalmapdataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** メタデータの識別子 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String metadataid;

    /** データに付けられた名前 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** フィルター */
    @Column(precision = 19, nullable = true, unique = false)
    public Long filterid;

    /** 初期表示フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean visible;

    /** 凡例折りたたみ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean closed;

    /** 検索フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean searchable;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** layerparentプロパティ */
    @Column(precision = 19, nullable = true, unique = false)
    public Long layerparent;

    /** attributionプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attribution;

    /** layeropacityプロパティ */
    @Column(precision = 17, scale = 17, nullable = true, unique = false)
    public Double layeropacity;

    /** wmscapsurlプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String wmscapsurl;

    /** wmsurlプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String wmsurl;

    /** wmsformatプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String wmsformat;

    /** wmslegendurlプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String wmslegendurl;

    /** wmsfeatureurlプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String wmsfeatureurl;

    /** featuretypeidプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String featuretypeid;

    /** layerdescriptionプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerdescription;

    /** 認証情報テーブルの識別子 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long authorizationinfoid;

    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** 認証情報 */
    @ManyToOne
    @JoinColumn(name="authorizationinfoid")
    public AuthorizationInfo authorizationInfo;

    /** 外部リストデータ情報 */
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="menuinfoid", referencedColumnName="menuinfoid"),
        @JoinColumn(name="metadataid", referencedColumnName="metadataid")
    })
    public ExternaltabledataInfo externaltabledataInfo;

    public static ExternalmapdataInfo valueOf(net.sf.json.JSONObject record) {
		try {
			ExternalmapdataInfo externalmapdataInfo = new ExternalmapdataInfo();
			externalmapdataInfo.closed = record.containsKey("closed") ? record.getBoolean("closed") : null;
			externalmapdataInfo.visible = record.containsKey("visible") ? record.getBoolean("visible") : null;
			externalmapdataInfo.searchable = record.containsKey("searchable") ? record.getBoolean("searchable") : null;
			externalmapdataInfo.name = record.containsKey("name") ? record.getString("name") : null;
			externalmapdataInfo.metadataid = record.containsKey("metadataid") ? record.getString("metadataid") : null;

			externalmapdataInfo.layerparent = record.containsKey("layerparent") ? record.getLong("layerparent") : null;
			externalmapdataInfo.attribution = record.containsKey("attribution") ? record.getString("attribution") : null;
			externalmapdataInfo.layeropacity = record.containsKey("layeropacity") ? (Double)record.getDouble("layeropacity") : null;
			externalmapdataInfo.wmscapsurl = record.containsKey("wmscapsurl") ? record.getString("wmscapsurl") : null;
			externalmapdataInfo.wmsurl = record.containsKey("wmsurl") ? record.getString("wmsurl") : null;
			externalmapdataInfo.wmsformat = record.containsKey("wmsformat") ? record.getString("wmsformat") : null;
			externalmapdataInfo.wmslegendurl = record.containsKey("wmslegendurl") ? record.getString("wmslegendurl") : null;
			externalmapdataInfo.wmsfeatureurl = record.containsKey("wmsfeatureurl") ? record.getString("wmsfeatureurl") : null;
			externalmapdataInfo.featuretypeid = record.containsKey("featuretypeid") ? record.getString("featuretypeid") : null;
			externalmapdataInfo.layerdescription = record.containsKey("layerdescription") ? record.getString("layerdescription") : null;

			return externalmapdataInfo;
		} catch (net.sf.json.JSONException e) {
			e.printStackTrace();
		}
		return null;
    }
}
