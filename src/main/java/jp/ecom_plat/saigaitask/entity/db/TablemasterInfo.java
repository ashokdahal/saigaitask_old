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
 * テーブルマスター情報
 *
 */
@Entity
@Table(name = "tablemaster_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class TablemasterInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** コピーフラグ0：引用 */
    public static final Short COPY_SHARE = 0;
    /** コピーフラグ1：最新の地物のみ複製 */
    public static final Short COPY_LATEST = 1;
    /** コピーフラグ2：履歴を含めたすべて複製 */
    public static final Short COPY_ALL = 2;
    /** コピーフラグ3：地物なし複製 */
    public static final Short COPY_NOFEATURE = 3;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

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

    /**
     * コピーフラグ.
     * 0: コピーしない（レイヤの引用）
     * 1: レイヤ複製、最新のみコピーする（デフォルト）
     * 2: レイヤ複製、すべてコピーする
     * 3: レイヤ複製、地物なし the_geom
     */
    @Column(length = 1, nullable = true, unique = false)
    public Short copy;

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
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted;

    /** リセットフラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean reset = false;

    /** ページングフラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean paging = false;

    /** 地図レイヤ情報リスト */
    @OneToMany(mappedBy="tablemasterInfo")
    public List<MaplayerInfo> maplayerInfos;

    /** 記録テーブル情報リスト */
    @OneToMany(mappedBy="tablemasterInfo")
    public List<TracktableInfo> tracktableInfos;

    /** 地図マスター情報 */
    @ManyToOne
    @JoinColumn(name="mapmasterinfoid")
    public MapmasterInfo mapmasterInfo;

    /** 公共情報コモンズ避難勧告レイヤ情報リスト */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<PubliccommonsReportRefugeInfo> publiccommonsReportRefugeInfoList;

    /** 公共情報コモンズ避難所レイヤ情報リスト */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<PubliccommonsReportShelterInfo> publiccommonsReportShelterInfoList;

    /** クリアリングハウス事前データ情報 */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<ClearinghousemetadataInfo> clearinghousemetadataInfoList;

    /** CKAN事前データ情報 */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<CkanmetadataInfo>ckanmetadataInfoList;

    /** 体制アラート情報 */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<StationalarmInfo> stationalarmInfoList;

    /** 入力補完情報 */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<AutocompleteInfo> autocompleteInfoList;

    /** テーブル演算項目情報 */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<TablecalculatecolumnInfo> tablecalculatecolumnInfoList;

    /** リセット設定情報 */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<TableresetcolumnData> tableresetcolumnDataList;

    /** JSON連携更新対象レイヤ */
    @OneToMany(mappedBy = "tablemasterInfo")
    public List<JsonimportlayerInfo> jsonimportlayerInfoList;
}
