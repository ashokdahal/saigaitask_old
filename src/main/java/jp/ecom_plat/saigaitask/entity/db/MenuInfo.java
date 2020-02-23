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
 * メニュー情報
 *
 */
@Entity
@Table(name = "menu_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class MenuInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** タスク種別 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menutasktypeinfoid;

    /** メニュータイプ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer menutypeid;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** オンラインヘルプ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String helphref;

    /**
     * フィルター
     * @deprecated 複数フィルター対応により、別テーブル化されたため廃止。
     * @see FilterInfo
     */
    @Deprecated
    @Column(precision = 19, nullable = true, unique = false)
    public Long filterid;

    /** 表示・非表示 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean visible;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** エクセル帳票出力テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long excellistoutputtablemasterinfoid;

    /** エクセル帳票出力テーブル 登録日時の属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String excellistoutputtableregisttimeattrid;

    /** エクセル帳票出力テーブル ダウンロードリンクの属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String excellistoutputtabledownloadlinkattrid;

    /** 削除フラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted;

    /** メニューテーブル情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<MenutaskmenuInfo> menutaskmenuInfoList;

    /** メニュータスク種別情報 */
    @ManyToOne
    @JoinColumn(name="menutasktypeinfoid")
    public MenutasktypeInfo menutasktypeInfo;

    /** メニュータイプマスタ */
    @ManyToOne
    @JoinColumn(name="menutypeid")
    public MenutypeMaster menutypeMaster;

    /** メニューテーブル情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<MenutableInfo> menutableInfoList;

    /** 外部地図データ情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<ExternalmapdataInfo> externalmapdataInfoList;

    /** 外部リストデータ情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<ExternaltabledataInfo> externaltabledataInfoList;

    /** ページボタン表示マスタリスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<PagemenubuttonInfo> pagemenubuttonInfoList;

    /** 地図ベースレイヤ情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<MapbaselayerInfo> mapbaselayerInfoList;

    /** 地図参照レイヤ情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<MapreferencelayerInfo> mapreferencelayerInfoList;

    /** メニュー地図情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<MenumapInfo> menumapInfoList;

    /** 通知デフォルト情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<NoticedefaultInfo> noticedefaultInfoList;

    /** フィルター情報リスト */
    @OneToMany(mappedBy = "menuInfo")
    public List<FilterInfo> filterInfoList;
}
