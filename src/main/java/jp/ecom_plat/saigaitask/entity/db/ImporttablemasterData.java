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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * インポートテーブルマスター情報
 * 
 */
@Entity
@Table(name = "importtablemaster_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/11/18 15:52:28")
@lombok.Getter @lombok.Setter
public class ImporttablemasterData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** テーブルマスター情報ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long tablemasterinfoid;

    /** 地図マスター情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long mapmasterinfoid;

    /** レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerid;

    /** テーブル名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String tablename;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** ジオメトリのタイプ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String geometrytype;

    /** コピーフラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean copy;

    /** 住所項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String addresscolumn;

    /** 更新日時項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String updatecolumn;

    /** 座標表示目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String coordinatecolumn;

    /** MRGSグリッド項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mgrscolumn;

    /** MRGS桁数 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer mgrsdigit;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;
}
