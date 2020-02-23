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
 * 地方自治体情報
 *
 */
@Entity
@Table(name = "localgov_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude = {"localgovtypeMaster", "groupInfo", "groupInfos", "unitInfos", "mapmasterInfoList",
		"noticegroupInfoList", "noticeaddressInfoList", "noticeTemplateList", "alarmmessageInfoList",
		"meteorequestInfoList", "meteoxsltInfoList", "assembleInfoList", "twitterInfoList", "autocompleteInfoList",
		"stationclassInfoList", "clearinghousemetadatadefaultInfoList",
		"clearinghousemetadataInfoList", "clearinghousesearchInfoList", "adminbackupDataList", "facebookInfoList",
		"publiccommonsReportRefugeInfoList", "publiccommonsReportShelterInfoList", "demoInfoList",
		"publiccommonsSendToInfoList", "stationalarmInfoList", "prefLocalgovInfo", "trackDatas", "landmarkInfoList",
		"multilangInfo", "safetystateInfoList", "summarylistInfoList" })
public class LocalgovInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体毎のドメイン */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String domain;

    /** システム名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String systemname;

    /** 自治体種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer localgovtypeid;

    /** 都道府県(自治体ID) */
    @Column(precision = 19, nullable = true, unique = false)
    public Long preflocalgovinfoid;

    /** 県名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String pref;

    /** 県コード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String prefcode;

    /** 市区町村 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String city;

    /** 市区町村コード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String citycode;

    /** 予備（区、自治会） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String section;

    /** 自動発報フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean autostart = false;

    /** 自動発報時の班ID(コモンズ発信作成組織情報) */
    @Column(precision = 19, nullable = true, unique = false)
    public Long autostartgroupinfoid;

    /** アラームの取得間隔（秒） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer alarminterval;

    /** smtpプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String smtp;

    /** emailプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String email;

    /** 座標10進法表示 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean coordinatedecimal = false;

    /** ロゴ画像ファイル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String logoimagefile;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 言語情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long multilanginfoid;

    /** 災害種別統合化済 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean disastercombined = false;

    /** 地方自治体種別 */
    @JoinColumn(name="localgovtypeid")
    @ManyToOne
    public LocalgovtypeMaster localgovtypeMaster;

    /** 班情報 */
    @JoinColumn(name="autostartgroupinfoid")
    @ManyToOne
    public GroupInfo groupInfo;

    /** 班情報リスト */
    @OneToMany(mappedBy="localgovInfo")
    public List<GroupInfo> groupInfos;

    /** 課情報リスト */
    @OneToMany(mappedBy="localgovInfo")
    public List<UnitInfo> unitInfos;

    /** 地図マスター情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<MapmasterInfo> mapmasterInfoList;

    /** 通知グループ情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<NoticegroupInfo> noticegroupInfoList;

    /** 通知連絡先情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<NoticeaddressInfo> noticeaddressInfoList;

    /** 通知テンプレートリスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<NoticeTemplate> noticeTemplateList;

    /** アラームメッセージ設定リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<AlarmmessageInfo> alarmmessageInfoList;

    /** 気象情報等取得情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<MeteorequestInfo> meteorequestInfoList;

    /** 気象情報XSLT情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<MeteoxsltInfo> meteoxsltInfoList;

    /** 職員参集情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<AssembleInfo> assembleInfoList;

    /** Twitter設定リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<TwitterInfo> twitterInfoList;

    /** 入力補完情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<AutocompleteInfo> autocompleteInfoList;

    /** 体制区分リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<StationclassInfo> stationclassInfoList;

    /** 通知デフォルト情報リスト */
    //@OneToMany(mappedBy = "localgovInfo")
    //public List<NoticedefaultInfo> noticedefaultInfoList;

    /** メタデータデフォルト設定情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<ClearinghousemetadatadefaultInfo> clearinghousemetadatadefaultInfoList;

    /** クリアリングハウス事前データ情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<ClearinghousemetadataInfo> clearinghousemetadataInfoList;

    /** クリアリングハウス検索情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<ClearinghousesearchInfo> clearinghousesearchInfoList;

    /** 管理バックアップデータリスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<AdminbackupData> adminbackupDataList;

    /** FacebookInfoエンティティクラスリスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<FacebookInfo> facebookInfoList;

    /** 公共情報コモンズ避難勧告レイヤ情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<PubliccommonsReportRefugeInfo> publiccommonsReportRefugeInfoList;

    /** 公共情報コモンズ避難所レイヤ情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<PubliccommonsReportShelterInfo> publiccommonsReportShelterInfoList;

    /** 訓練情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<DemoInfo> demoInfoList;

    /** 地方自治体情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<PubliccommonsSendToInfo> publiccommonsSendToInfoList;

    /** 体制アラート情報 */
    @OneToMany(mappedBy = "localgovInfo")
    public List<StationalarmInfo> stationalarmInfoList;

    /** 都道府県(自治体ID) */
    @ManyToOne
    @JoinColumn(name="preflocalgovinfoid")
    public LocalgovInfo prefLocalgovInfo;

    /** 記録データリスト */
    @OneToMany(mappedBy="localgovInfo")
    public List<TrackData> trackDatas;

    /** 目標物情報 */
    @OneToMany(mappedBy = "localgovInfo")
    public List<LandmarkInfo> landmarkInfoList;

    /** 言語情報 */
    @ManyToOne
    @JoinColumn(name="multilanginfoid")
    public MultilangInfo multilangInfo;

    /** 職員参集状況情報 */
    @OneToMany(mappedBy = "localgovInfo")
    public List<SafetystateInfo> safetystateInfoList;

    /** 集計リスト情報リスト */
    @OneToMany(mappedBy = "localgovInfo")
    public List<SummarylistInfo> summarylistInfoList;
}
