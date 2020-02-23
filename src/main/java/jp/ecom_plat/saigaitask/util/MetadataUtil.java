/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * NIED version of Catalogue Service for the Web メタデータ
 */
public class MetadataUtil{
    static final Logger logger = Logger.getLogger(MetadataUtil.class);
	
    private MetadataUtil(){}

    //////////////// JMP2.0 メタデータコード定義 ////////////////

    public static class CI_DateTypeCode{
        /** 作成日 */
        public static final String CREATION = "001";
        /** 刊行日 */
        public static final String PUBLICATION = "002";
        /** 改訂日 */
        public static final String REVISION = "003";
    }

    public static class CI_RoleCode{
        /** 情報資源提供者 */
        public static final String RESOURCE_PROVIDER = "001";
        /** 管理者 */
        public static final String CUSTODIAN = "002";
        /** 所有者 */
        public static final String OWNER = "003";
        /** 利用者 */
        public static final String USER = "004";
        /** 配布者 */
        public static final String DISTRIBUTOR = "005";
        /** 創作者 */
        public static final String ORIGINATOR = "006";
        /** 問い合せ先 */
        public static final String POINT_OF_CONTACT = "007";
        /** 主要な調査担当者 */
        public static final String PRINCIPAL_INVESTIGATOR = "008";
        /** 処理担当者 */
        public static final String PROCESSOR = "009";
        /** 刊行者 */
        public static final String PUBLISHER = "010";
        /** 著作者 */
        public static final String AUTHOR = "011";
    }

    public static class MD_KeywordTypeCode{
        /** 学問分野 */
        public static final String DISCIPLINE = "001";
        /** 場所 */
        public static final String PLACE = "002";
        /** 層 */
        public static final String STRATUM = "003";
        /** 時間 */
        public static final String TEMPORAL = "004";
        /** 主題 */
        public static final String THEME = "005";
    }

    public static class MD_TopicCategoryCode{
        /** 農業 */
        public static final String FARMING = "001";
        /** 生物相 */
        public static final String BIOTA = "002";
        /** 境界 */
        public static final String BOUNDARIES = "003";
        /** 気象 */
        public static final String CLIMATOLOGY_METEOROLOGY_ATMOSPHERE = "004";
        /** 経済 */
        public static final String ECONOMY = "005";
        /** 高さ */
        public static final String ELEVATION = "006";
        /** 環境 */
        public static final String ENVIRONMENT = "007";
        /** 地球科学の情報 */
        public static final String GEOSCIENTIFIC_INFORMATION = "008";
        /** 健康 */
        public static final String HEALTH = "009";
        /** 全地球基本地図画像 */
        public static final String IMAGERY_BASE_MAPS_EARTH_COVER = "010";
        /** 軍事情報 */
        public static final String INTELLIGENCE_MILITARY = "011";
        /** 陸水 */
        public static final String INLAND_WATERS = "012";
        /** 位置 */
        public static final String LOCATION = "013";
        /** 大洋 */
        public static final String OCEANS = "014";
        /** 土地台帳計画 */
        public static final String PLANNING_CADASTRE = "015";
        /** 社会 */
        public static final String SOCIETY = "016";
        /** 構造物 */
        public static final String STRUCTURE = "017";
        /** 運輸 */
        public static final String TRANSPORTATION = "018";
        /** 公共事業・通信 */
        public static final String UTILITIES_COMMUNICATION = "019";
    }

    public static class MD_SpatialRepresentationTypeCode {
        /** ベクトル */
        public static final String VECTOR = "001";
        /** グリッド */
        public static final String GRID = "002";
        /** テキスト表形式 */
        public static final String TEXT_TABLE = "003";
        /** 不規則三角形ネットワーク */
        public static final String TIN = "004";
        /** ステレオモデル */
        public static final String STEREO_MODEL = "005";
        /** ビデオ */
        public static final String VIDEO = "006";
    }

    public static class MD_ScopeCode{
        /** 属性 */
        public static final String ATTRIBUTE = "001";
        /** 属性型 */
        public static final String ATTRIBUTE_TYPE = "002";
        /** 収集用機器 */
        public static final String COLLECTION_HARDWARE = "003";
        /** 収集作業 */
        public static final String COLLECTION_SESSION = "004";
        /** データ集合 */
        public static final String DATASET = "005";
        /** シリーズ */
        public static final String SERIES = "006";
        /** 非地理データ集合 */
        public static final String NON_GEOGRAPHIC_DATASET = "007";
        /** 次元グループ */
        public static final String DIMENSION_GROUP = "008";
        /** 地物 */
        public static final String FEATURE = "009";
        /** 地物型 */
        public static final String FEATURE_TYPE = "010";
        /** 特質の型 */
        public static final String PROPERTY_TYPE = "011";
        /** 現場作業 */
        public static final String FIELD_SESSION = "012";
        /** ソフトウェア */
        public static final String SOFTWARE = "013";
        /** サービス */
        public static final String SERVICE = "014";
        /** モデル */
        public static final String MODEL = "015";
        /** タイル */
        public static final String TITLE = "016";
    }

    public static class MD_CharacterSetCode{
        public static final String UCS_2 = "001";
        public static final String UCS_4 = "002";
        public static final String UTF_7 = "003";
        public static final String UTF_8 = "004";
        public static final String UTF_16 = "005";
        public static final String ISO_8859_1 = "006";
        public static final String ISO_8859_2 = "007";
        public static final String ISO_8859_3 = "008";
        public static final String ISO_8859_4 = "009";
        public static final String ISO_8859_5 = "010";
        public static final String ISO_8859_6 = "011";
        public static final String ISO_8859_7 = "012";
        public static final String ISO_8859_8 = "013";
        public static final String ISO_8859_9 = "014";
        public static final String ISO_8859_10 = "015";
        public static final String ISO_8859_11 = "016";
        // public static final String ISO_8859_12 = "017";	// reserved for future use
        public static final String ISO_8859_13 = "018";
        public static final String ISO_8859_14 = "019";
        public static final String ISO_8859_15 = "020";
        public static final String ISO_8859_16 = "021";
        public static final String JIS = "022";
        public static final String SHIFT_JIS = "023";
        public static final String EUC_JP = "024";
        public static final String US_ASCII = "025";
        public static final String EBCDIC = "026";
        public static final String EUC_KR = "027";
        public static final String BIG5 = "028";
        public static final String GB2312 = "029";
    }

    public static class CI_OnLineFunctionCode{
        /** ダウンロード */
        public static final String DOWNLOAD = "001";
        /** 情報 */
        public static final String INFORMATION = "002";
        /** オフラインアクセス */
        public static final String OFFLINEACCESS = "003";
        /** 注文 */
        public static final String ORDER = "004";
        /** 検索 */
        public static final String SEARCH = "005";
    }

    public static class DQ_TypeOfQualityEvaluationCode{
        /** 完全性/過剰 */
        public static final String COMPLETENESS_COMMISSION = "001";
        /** 完全性/漏れ */
        public static final String COMPLETENESS_OMMISSION = "002";
        /** 論理一貫性/概念一貫性 */
        public static final String CONCEPTUAL_CONSISTENCY = "003";
        /** 論理一貫性/定義域一貫性 */
        public static final String DOMAIN_CONSISTENCY = "004";
        /** 論理一貫性/書式一貫性 */
        public static final String FORMAT_CONSISTENCY = "005";
        /** 論理一貫性/位相一貫性 */
        public static final String TOPOLOGICAL_CONSISTENCY = "006";
        /** 位置正確度/絶対正確度又は外部正確度 */
        public static final String ABSOLUTE_EXTERNAL_POSITIONAL_ACCURACY = "007";
        /** 位置正確度/グリッドデータ位置正確度 */
        public static final String GRIDDED_DATA_POSITION_ACCURACY = "008";
        /** 位置正確度/相対正確度又は内部正確度 */
        public static final String RELATIVE_INTERNAL_POSITIONAL_ACCURACY = "009";
        /** 時間正確度/時間測定正確度 */
        public static final String ACCURACY_OF_A_TIME_MEASUREMENT = "010";
        /** 時間正確度/時間一貫性 */
        public static final String TEMPORAL_CONSISTENCY = "011";
        /** 時間正確度/時間妥当性 */
        public static final String TEMPORAL_VALIDITY = "012";
        /** 分類正確度/分類の正しさ */
        public static final String THEMATIC_CLASSIFICATION_CORRECTNESS = "013";
        /** 分類正確度/非定量的属性の正しさ */
        public static final String NON_QUANTITATIVE_ATTRIBUTE_ACCURACY = "014";
        /** 分類正確度/定量的属性の正確度 */
        public static final String QUANTITATIVE_ATTRIBUTE_ACCURACY = "015";
    }

    public static class DS_AssociationTypeCode{
        /** 相互参照 */
        public static final String CROSSREFERENCE = "001";
        /** より大きな製品の引用 */
        public static final String LARGEWORKCITATION = "002";
        /** 継ぎ目の無いデータベースの一部 */
        public static final String PARTOFSEAMLESSDATABASE = "003";
        /** 情報源 */
        public static final String SOURCE = "004";
        /** ステレオメイト */
        public static final String STEREOMATE = "005";
    }

    public static class DS_InitiativeTypeCode{
        /** 作戦 */
        public static final String CAMPAIGN = "001";
        /** 収集 */
        public static final String COLLECTION = "002";
        /** 演習 */
        public static final String EXERCISE = "003";
        /** 実験 */
        public static final String EXPERIMENT = "004";
        /** 調査 */
        public static final String INVESTIGATION = "005";
        /** 任務 */
        public static final String MISSION = "006";
        /** センサ */
        public static final String SENSOR = "007";
        /** 作業 */
        public static final String OPERATION = "008";
        /** プラットフォーム */
        public static final String PLATFORM = "009";
        /** 処理過程 */
        public static final String PROCESS = "010";
        /** プログラム */
        public static final String PROGRAM = "011";
        /** プロジェクト */
        public static final String PROJECT = "012";
        /** 研究 */
        public static final String STUDY = "013";
        /** タスク */
        public static final String TASK = "014";
        /** 試行 */
        public static final String TRIAL = "015";
    }

    public static class MD_MediumNameCode{
        /** CD-ROM */
        public static final String CD_ROM = "001";
        /** DVD */
        public static final String DVD = "002";
        /** DVD-ROM */
        public static final String DVD_ROM = "003";
        /** 3.5インチFD */
        public static final String FLOPPY_3HALF_INCH = "004";
        /** 5.25インチFD */
        public static final String FLOPPY_5QUARTER_INCH = "005";
        /** 7トラック磁気テープ */
        public static final String TAPE_7TRACK = "006";
        /** 9トラック磁気テープ */
        public static final String TAPE_9TRACK = "007";
        /** 3480のカセットテープドライブ */
        public static final String CARTRIDGE_3480 = "008";
        /** 3490のカセットテープドライブ */
        public static final String CARTRIDGE_3490 = "009";
        /** 3580のカセットテープドライブ */
        public static final String CARTRIDGE_3580 = "010";
        /** 4ミリ磁気テープ */
        public static final String TAPE_4MM_CARTRIDGE = "011";
        /** 8ミリ磁気テープ */
        public static final String TAPE_8MM_CARTRIDGE = "012";
        /** 0.25インチ磁気テープ */
        public static final String TAPE_1QUARTER_INCH_CARTRIDGE = "013";
        /** 半インチカートリッジストリームテープドライブ */
        public static final String DIGITAL_LINEAR_TAPE = "014";
        /** オンライン */
        public static final String ON_LINE = "015";
        /** 衛星通信 */
        public static final String SATELLITE = "016";
        /** 電話回線 */
        public static final String TELEPHONE_LINK = "017";
        /** 非デジタル媒体 */
        public static final String HARDCOPY = "018";
        /** MO */
        public static final String MO = "019";
    }

    public static class MD_ProgressCode{
        /** 完成 */
        public static final String COMPLETED = "001";
        /** 古文書 */
        public static final String HISTORICAL_ARCHIVE = "002";
        /** 廃棄 */
        public static final String OBSOLETE = "003";
        /** 進行中 */
        public static final String ON_GOING = "004";
        /** 計画済み */
        public static final String PLANNED = "005";
        /** 要求中 */
        public static final String REQUIRED = "006";
        /** 作業中 */
        public static final String UNDERDEVELOPMENT = "007";
        /** 予定 */
        public static final String EXPECTED = "999";
    }

    //////////////// JMP2.0 メタデータコード定義(end) ////////////////

    //////////////// JMP2.0 メタデータタグ名定義 ////////////////

    public static final String TAG_MDMetadata = "MD_Metadata";
    public static final String TAG_identificationInfo = "identificationInfo";
    public static final String TAG_MDDataIdentification = "MD_DataIdentification";
    public static final String TAG_citation = "citation";
    public static final String TAG_title = "title";
    public static final String TAG_date = "date";
    public static final String TAG_dateType = "dateType";
    public static final String TAG_abstract = "abstract";
    public static final String TAG_purpose = "purpose";
    public static final String TAG_status = "status";
    public static final String TAG_pointOfContact = "pointOfContact";
    public static final String TAG_role = "role";
    public static final String TAG_resourceConstraints = "resourceConstraints";
    public static final String TAG_MDConstraints = "MD_Constraints";
    public static final String TAG_useLimitation = "useLimitation";
    public static final String TAG_aggregationInfo = "aggregationInfo";
    public static final String TAG_MDAggregateInformation = "MD_AggregateInformation";
    public static final String TAG_aggregateDataSetName = "aggregateDataSetName";
    public static final String TAG_aggregateDataSetIdentifier = "aggregateDataSetIdentifier";
    public static final String TAG_associationType = "associationType";
    public static final String TAG_initiativeType = "initiativeType";
    public static final String TAG_spatialRepresentationType = "spatialRepresentationType";
    public static final String TAG_spatialResolution = "spatialResolution";
    public static final String TAG_MDResolution = "MD_Resolution";
    public static final String TAG_equivalentScale = "equivalentScale";
    public static final String TAG_MDRepresentativeFraction = "MD_RepresentativeFraction";
    public static final String TAG_denominator = "denominator";
    public static final String TAG_distance = "distance";
    public static final String TAG_Distance = "Distance";
    public static final String TAG_value = "value";
    public static final String TAG_uom = "uom";
    public static final String TAG_unitOfMeasure = "unitOfMeasure";
    public static final String TAG_measurementType = "measurementType";
    public static final String TAG_descriptiveKeywords = "descriptiveKeywords";
    public static final String TAG_MDKeywords = "MD_Keywords";
    public static final String TAG_topicCategory = "topicCategory";
    public static final String TAG_individualName = "individualName";
    public static final String TAG_organisationName = "organisationName";
    public static final String TAG_positionName = "positionName";
    public static final String TAG_contactInfo = "contactInfo";
    public static final String TAG_phone = "phone";
    public static final String TAG_voice = "voice";
    public static final String TAG_facsimile = "facsimile";
    public static final String TAG_address = "address";
    public static final String TAG_deliveryPoint = "deliveryPoint";
    public static final String TAG_city = "city";
    public static final String TAG_cityCode = "cityCode";
    public static final String TAG_administrativeArea = "administrativeArea";
    public static final String TAG_administrativeAreaCode = "administrativeAreaCode";
    public static final String TAG_postalCode = "postalCode";
    public static final String TAG_country = "country";
    public static final String TAG_electronicMailAddress = "electronicMailAddress";
    public static final String TAG_onlineResource = "onlineResource";
    public static final String TAG_hoursOfService = "hoursOfService";
    public static final String TAG_contactInstructions = "contactInstructions";
    public static final String TAG_linkage = "linkage";
    public static final String TAG_extent = "extent";
    public static final String TAG_temporalElement = "temporalElement";
    public static final String TAG_geographicElement = "geographicElement";
    public static final String TAG_graphicOverview = "graphicOverview";
    public static final String TAG_MDBrowseGraphic = "MD_BrowseGraphic";
    public static final String TAG_fileName = "fileName";
    public static final String TAG_fileDescription = "fileDescription";
    public static final String TAG_fileType = "fileType";
    public static final String TAG_referenceSystemInfo = "referenceSystemInfo";
    public static final String TAG_MDReferenceSystem = "MD_ReferenceSystem";
    public static final String TAG_referenceSystemIdentifier = "referenceSystemIdentifier";
    public static final String TAG_RSIdentifier = "RS_Identifier";
    public static final String TAG_MDIdentifier = "MD_Identifier";
    public static final String TAG_authority = "authority";
    public static final String TAG_code = "code";
    public static final String TAG_CICitation = "CI_Citation";
    public static final String TAG_edition = "edition";
    public static final String TAG_identifier = "identifier";
    public static final String TAG_distributionInfo = "distributionInfo";
    public static final String TAG_MDDistribution = "MD_Distribution";
    public static final String TAG_distributionFormat = "distributionFormat";
    public static final String TAG_transferOptions = "transferOptions";
    public static final String TAG_type = "type";
    public static final String TAG_contact = "contact";
    public static final String TAG_keyword = "keyword";
    public static final String TAG_MDDigitalTransferOptions = "MD_DigitalTransferOptions";
    public static final String TAG_onLine = "onLine";
    public static final String TAG_offLine = "offLine";
    public static final String TAG_mediumNote = "mediumNote";
    public static final String TAG_MDFormat = "MD_Format";
    public static final String TAG_name = "name";
    public static final String TAG_version = "version";
    public static final String TAG_protocol = "protocol";
    public static final String TAG_applicationProfile = "applicationProfile";
    public static final String TAG_description = "description";
    public static final String TAG_function = "function";
    public static final String TAG_fileIdentifier = "fileIdentifier";
    public static final String TAG_parentIdentifier = "parentIdentifier";
    public static final String TAG_language = "language";
    public static final String TAG_isoCode = "isoCode";
    public static final String TAG_characterSet = "characterSet";
    public static final String TAG_dateStamp = "dateStamp";
    public static final String TAG_hierarchyLevel = "hierarchyLevel";
    public static final String TAG_hierarchyLevelName = "hierarchyLevelName";
    public static final String TAG_metadataStandardName = "metadataStandardName";
    public static final String TAG_metadataStandardVersion = "metadataStandardVersion";
    public static final String TAG_EXGeographicBoundingBox = "EX_GeographicBoundingBox";
    public static final String TAG_EXCoordinateBoundingBox = "EX_CoordinateBoundingBox";
    public static final String TAG_EXGeographicDescription = "EX_GeographicDescription";
    public static final String TAG_geographicIdentifier = "geographicIdentifier";
    public static final String TAG_EXBoundingPolygon = "EX_BoundingPolygon";
    public static final String TAG_GMPolygon = "GM_Polygon";
    public static final String TAG_polygon = "polygon";
    public static final String TAG_exterior = "exterior";
    public static final String TAG_interior = "interior";
    public static final String TAG_coordinates = "coordinates";
    public static final String TAG_pos = "pos";
    public static final String TAG_coord = "coord";
    public static final String TAG_extentTypeCode = "extentTypeCode";
    public static final String TAG_extentReferenceSystem = "extentReferenceSystem";
    public static final String TAG_westBoundLongitude = "westBoundLongitude";
    public static final String TAG_eastBoundLongitude = "eastBoundLongitude";
    public static final String TAG_southBoundLatitude = "southBoundLatitude";
    public static final String TAG_northBoundLatitude = "northBoundLatitude";
    public static final String TAG_westBoundCoordinate = "westBoundCoordinate";
    public static final String TAG_eastBoundCoordinate = "eastBoundCoordinate";
    public static final String TAG_southBoundCoordinate = "southBoundCoordinate";
    public static final String TAG_northBoundCoordinate = "northBoundCoordinate";
    public static final String TAG_EXTemporalExtent = "EX_TemporalExtent";
    public static final String TAG_beginEnd = "beginEnd";
    public static final String TAG_begin = "begin";
    public static final String TAG_end = "end";
    public static final String TAG_instant = "instant";
    public static final String TAG_verticalElement = "verticalElement";
    public static final String TAG_EXVerticalExtent = "EX_VerticalExtent";
    public static final String TAG_minimumValue = "minimumValue";
    public static final String TAG_maximumValue = "maximumValue";
    public static final String TAG_verticalDatum = "verticalDatum";
    public static final String TAG_SCVerticalDatum = "SC_VerticalDatum";
    public static final String TAG_datumID = "datumID";
    public static final String TAG_dataQualityInfo = "dataQualityInfo";
    public static final String TAG_DQDataQuality = "DQ_DataQuality";
    public static final String TAG_scope = "scope";
    public static final String TAG_level = "level";
    public static final String TAG_levelDescription = "levelDescription";
    public static final String TAG_report = "report";
    public static final String TAG_DQElement = "DQ_Element";
    public static final String TAG_evaluationMethodDescription = "evaluationMethodDescription";
    public static final String TAG_result = "result";
    public static final String TAG_DQConformanceResult = "DQ_ConformanceResult";
    public static final String TAG_specification = "specification";
    public static final String TAG_explanation = "explanation";
    public static final String TAG_pass = "pass";
    public static final String TAG_DQQuantitativeResult = "DQ_QuantitativeResult";
    public static final String TAG_valueUnit = "valueUnit";
    public static final String TAG_errorStatistic = "errorStatistic";
    public static final String TAG_otherValue = "otherValue";
    public static final String TAG_typeOfQualityEvaluation = "typeOfQualityEvaluation";
    public static final String TAG_lineage = "lineage";
    public static final String TAG_LILineage = "LI_Lineage";
    public static final String TAG_statement = "statement";
    public static final String TAG_processStep = "processStep";
    public static final String TAG_LIProcessStep = "LI_ProcessStep";
    public static final String TAG_rationale = "rationale";
    public static final String TAG_dateTime = "dateTime";
    public static final String TAG_processor = "processor";
    public static final String TAG_CIResponsibleParty = "CI_ResponsibleParty";
    public static final String TAG_source = "source";
    public static final String TAG_LISource = "LI_Source";
    public static final String TAG_scaleDenominator = "scaleDenominator";
    public static final String TAG_sourceReferenceSystem = "sourceReferenceSystem";
    public static final String TAG_sourceCitation = "sourceCitation";
    public static final String TAG_sourceExtent = "sourceExtent";
    public static final String TAG_sourceStep = "sourceStep";

    //////////////// JMP2.0 メタデータタグ名定義(end) ////////////////

    //////////////// 災害リスク情報クリアリングハウス 固有定義(end) ////////////////

	/**
	 * 災害区分
	 */
	public static final String DT_UNCLASSIFIED = "0";       // 区分なし
	public static final String DT_EARTHQUAKE = "1";       // 地震
	public static final String DT_TSUNAMI_HIGHTIDE = "2"; // 津波・高潮
	public static final String DT_VOLCANO = "3";          // 火山
	public static final String DT_STORM_FLOOD = "4";      // 風水害
	public static final String DT_LANDSLIDE = "5";        // 土砂
	public static final String DT_ICE_SNOW = "6";         // 氷雪
	public static final String DT_OTHER = "7";            // その他

    /**
     * 公開モード
     */
    public static final String OM_PUBLIC = "0"; // 常に公開
    public static final String OM_LOGINUSER = "1"; // ログインユーザに公開
    public static final String OM_USERGROUP = "2"; // ユーザグループに公開
    public static final String OM_PRIVATE = "-1"; // 非公開

    //////////////// 災害リスク情報クリアリングハウス 固有定義 ////////////////

    //////////////// namespace に関するデフォルト ////////////////

    static String URI_jmpex = "http://schemas.info-bosai.jp/ch/jmpex/";
    static String URI_jmp = "http://zgate.gsi.go.jp/ch/jmp/";
    static String URI_csw = "http://www.opengis.net/cat/csw/2.0.2";
    static String jmpPrefix = "jmp";
    static String cswPrefix = "csw";
    static Namespace XMLNS_jmpex = Namespace.getNamespace(""/*jmpPrefix*/,URI_jmpex);
    static Namespace XMLNS_jmp = Namespace.getNamespace(""/*jmpPrefix*/,URI_jmp);

    //////////////// namespace に関するデフォルト(end) ////////////////

    //////////////// メタデータ要素操作汎用メソッド ////////////////

    /**
     * Namespace の入れ替え
     */
    static Element replaceNamespace(Element elm,Namespace ns){
        elm.setNamespace(ns);
        List<Element> children = elm.getChildren();
        for(Element c : children){
            replaceNamespace(c,ns);
        }
        return elm;
    }

    /**
     * XML文字列を Element に変換
     */
    static Element XMLToElement(String metadata){
        if(metadata != null){
            try{
                SAXBuilder builder = new SAXBuilder();
                builder.setIgnoringBoundaryWhitespace(true);
                builder.setIgnoringElementContentWhitespace(true);
                Document  doc = builder.build(new StringReader(metadata));
                return doc.getRootElement().detach();
            }catch(Exception e){
            }
        }
        return null;
    }

    /**
     * Element を XML文字列に変換
     */
    static String ElementToXML(Element data,boolean indent) {
        if(data == null)
            return null;
        Format format = null;
        if(indent){
            format = Format.getPrettyFormat();
            format.setIndent("  ");
            format.setLineSeparator(LineSeparator.UNIX);
        }else{
            format = Format.getRawFormat();
        }
        XMLOutputter outputter = new XMLOutputter(format);
        return outputter.outputString(data);
    }

    /**
     * ネームスペースを取得
     */
    static List<Namespace> getNamespaces(Element elm){
        List<Namespace> theNSorg = elm.getNamespacesInScope();
        List<Namespace> theNSs = new ArrayList<Namespace>();
        for(Namespace n : theNSorg){
            if(n.getPrefix() != null && n.getPrefix().length() > 0)
                theNSs.add(n);
            else if(URI_jmp.equals(n.getURI()))
                theNSs.add(Namespace.getNamespace(jmpPrefix,n.getURI()));
            else if(URI_jmpex.equals(n.getURI()))
                theNSs.add(Namespace.getNamespace(jmpPrefix,n.getURI()));
            else if(URI_csw.equals(n.getURI()))
                theNSs.add(Namespace.getNamespace(cswPrefix,n.getURI()));
        }
        return theNSs;
    }

    /**
     * XPath 構築
     */
    static XPathExpression<Element> prepareXPath(String[] xpath,Element parent,String prefix){
        StringBuffer sbuf = new StringBuffer();
        for(String nm:xpath){
            if(sbuf.length() > 0)
                sbuf.append("/");
            if(nm.indexOf(":") == -1)
                sbuf.append(prefix+":");
            sbuf.append(nm);
        }
        List<Namespace> theNSs = getNamespaces(parent);
        // System.out.println(sbuf.toString());
        return XPathFactory.instance().compile(sbuf.toString(), Filters.element(), null, theNSs);
    }

    /**
     * 要素の値を取得
     */
    static String getXMLValue(String[] xpath,Element parent,String prefix){
        Element elm = getXMLElement(xpath,parent,prefix);
        if(elm != null)
            return elm.getValue();
        return null;
    }

    /**
     * 要素の値を取得（複数）
     */
    static String[] getXMLValues(String[] xpath,Element parent,String prefix){
        List<Element> elms = getXMLElements(xpath,parent,prefix);
        if(elms != null && elms.size() > 0){
            ArrayList<String> ret = new ArrayList<String>();
            for(Element elm : elms) {
                ret.add(elm.getValue());
            }
            return ret.toArray(new String[elms.size()]);
        }
        return null;
    }

    /**
     * 要素を取得
     */
    static Element getXMLElement(String[] xpath,Element parent,String prefix){
        XPathExpression<Element> xp = prepareXPath(xpath,parent,prefix);
        return xp.evaluateFirst(parent);
    }

    /**
     * 要素を取得（複数）
     */
    static List<Element> getXMLElements(String[] xpath,Element parent,String prefix){
        XPathExpression<Element> xp = prepareXPath(xpath,parent,prefix);
        List<Element> l = xp.evaluate(parent);
        return l.size()>0?l:null;
    }

    /**
     * 要素を作成
     */
    static Element createXMLElement(String nm,Element parent){
        Element e = new Element(nm,parent.getNamespace(""));
        parent.addContent(e);
        return e;
    }

    /**
     * 要素を作成
     */
    static Element createXMLElement(String nm,Namespace ns){
        Element e = new Element(nm,ns);
        return e;
    }

    /**
     * 要素を作成（複数）
     */
    static Element createXMLElement(String[] nm,Element parent){
        Element e = null;
        for(String n : nm){
            e = createXMLElement(n,parent);
            parent = e;
        }
        return e;
    }

    /**
     * 要素の取得、なければ作成する
     */
    static Element getOrCreateXMLElement(String nm,Element parent,String prefix){
        Element e = getXMLElement(new String[]{nm},parent,prefix);
        if(e == null)
            e = createXMLElement(nm,parent);
        return e;
    }

    /**
     * 要素の取得、なければ作成する
     */
    static Element getOrCreateXMLElement(String[] nm,Element parent,String prefix){
        Element e = null;
        for(String n : nm){
            e = getXMLElement(new String[]{n},parent,prefix);
            if(e == null)
                e = createXMLElement(n,parent);
            parent = e;
        }
        return e;
    }

    /**
     * 要素の削除
     */
    static boolean removeXMLElements(String nm,Element parent){
        return parent.removeChildren(nm,parent.getNamespace(""));
    }

    /**
     * 要素の削除
     */
    static boolean removeXMLElement(Element child,Element parent){
        return parent.removeContent(child);
    }

    /**
     * 空の要素を削除する
     */
    static boolean removeEmptyElement(Element elm){
        List<Element> children = elm.getChildren();
        if(children.size() > 0){
            Element[] c = children.toArray(new Element[children.size()]);
            for(Element c1 : c){
                boolean be = removeEmptyElement(c1);
                if(be)
                    elm.removeContent(c1);
            }
        }
        children = elm.getChildren();
        String val = elm.getValue();
        if(val != null && val.trim().length() == 0)
            val = null;
        return children.size() == 0 && val == null;
    }

    //////////////// メタデータ要素操作汎用メソッド(end) ////////////////

    //////////////// JMP2.0アクセス ////////////////

    /**
     * 新規メタデータ作成
     *
     */
    public static Element createMetadata(){
        Namespace ns = Namespace.getNamespace(""/*jmpPrefix*/,URI_jmpex);
        return createXMLElement(TAG_MDMetadata,ns);
    }

    /**
     * XMLテキストからメタデータ作成
     */
    public static Element parseMetadata(String xml){
        Element ret = XMLToElement(xml);
        // removeEmptyElement(ret);
        return ret;
    }

	/**
	 * 2.ファイル識別子
	 */
    public static String getFileIdentifier(Element xmlElement){
        return getXMLValue(new String[]{TAG_fileIdentifier},xmlElement,jmpPrefix);
    }

	/**
	 * 2.ファイル識別子
	 */
    public static void setFileIdentifier(String fid,Element xmlElement){
        Element e = getOrCreateXMLElement(TAG_fileIdentifier,xmlElement,jmpPrefix);
        e.setText(fid);
    }

	/**
	 * 3.言語
	 */
    public static String getLanguage(Element xmlElement){
        return getXMLValue(new String[]{TAG_language,TAG_isoCode},xmlElement,jmpPrefix);
    }

	/**
	 * 3.言語
	 */
    public static void setLanguage(String isocode,Element xmlElement){
        Element e = getOrCreateXMLElement(new String[]{TAG_language,TAG_isoCode},xmlElement,jmpPrefix);
        e.setText(isocode);
    }

	/**
	 * 4.文字集合 (MD_CharacterSetCode)
	 */
	public static String getCharacterSet(Element xmlElement){
        return getXMLValue(new String[]{TAG_characterSet},xmlElement,jmpPrefix);
    }

	/**
	 * 4.文字集合 (MD_CharacterSetCode)
	 */
	public static void setCharacterSet(String cs,Element xmlElement){
        Element e = getOrCreateXMLElement(TAG_characterSet,xmlElement,jmpPrefix);
        e.setText(cs);
	}

	/**
	 * 5.親識別子
	 */
	public static String getParentIdentifier(Element xmlElement){
        return getXMLValue(new String[]{TAG_parentIdentifier},xmlElement,jmpPrefix);
    }

	/**
	 * 5.親識別子
	 */
	public static void setParentIdentifier(String pid,Element xmlElement){
        Element e = getOrCreateXMLElement(TAG_parentIdentifier,xmlElement,jmpPrefix);
        e.setText(pid);
	}

	/**
	 * 6.階層レベル (MD_ScopeCode)
	 */
	public static String[] getHierarchyLevel(Element xmlElement){
        return getXMLValues(new String[]{TAG_hierarchyLevel},xmlElement,jmpPrefix);
	}

	/**
	 * 6.階層レベル (MD_ScopeCode)
	 */
	public static void setHierarchyLevel(String[] hl,Element xmlElement) {
        Element mdid = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification},xmlElement,jmpPrefix);
        removeXMLElements(TAG_hierarchyLevel,xmlElement);
        for(String h : hl){
            Element e = createXMLElement(TAG_hierarchyLevel,xmlElement);
            e.setText(h);
        }
	}

	/**
	 * 7.階層レベル名
	 */
	public static String[] getHierarchyLevelName(Element xmlElement){
        return getXMLValues(new String[]{TAG_hierarchyLevelName},xmlElement,jmpPrefix);
    }

	/**
	 * 7.階層レベル名
	 */
	public static void setHierarchyLevelName(String[] hln,Element xmlElement) {
        Element mdid = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification},xmlElement,jmpPrefix);
        removeXMLElements(TAG_hierarchyLevelName,xmlElement);
        for(String h : hln){
            Element e = createXMLElement(TAG_hierarchyLevelName,xmlElement);
            e.setText(h);
        }
    }

	/**
	 * 8.問合せ先
	 */
	public static Element getContact(Element xmlElement) {
        return getXMLElement(new String[]{TAG_contact},xmlElement,jmpPrefix);
    }
	/**
	 * 8.問合せ先
	 */
    public static void setContact(Element ct,Element xmlElement){
        Element oldct = getXMLElement(new String[]{TAG_contact},xmlElement,jmpPrefix);
        if(oldct != null)
            removeXMLElement(oldct,xmlElement);
        if(ct != null)
            xmlElement.addContent(ct);
    }
    /**
     * 126.個人名
     */
    public static String getCIResponsiblePartyIndividualName(Element rp){
        return getXMLValue(new String[]{TAG_individualName},rp,jmpPrefix);
    }
    /**
     * 127.組織名
     */
    public static String getCIResponsiblePartyOrganisationName(Element rp){
        return getXMLValue(new String[]{TAG_organisationName},rp,jmpPrefix);
    }
    /**
     * 128.役職名
     */
    public static String getCIResponsiblePartyPositionName(Element rp){
        return getXMLValue(new String[]{TAG_positionName},rp,jmpPrefix);
    }
    /**
     * 129.問い合わせ情報
     */
    public static Element getCIResponsiblePartyContactInfo(Element rp){
        return getXMLElement(new String[]{TAG_contactInfo},rp,jmpPrefix);
    }
    /**
     * 130.役割 (CI_RoleCode)
     */
    public static String getCIResponsiblePartyRole(Element rp){
        return getXMLValue(new String[]{TAG_role},rp,jmpPrefix);
    }
    /**
     * 電話
     */
    public static Element getCIContactPhone(Element ct){
        return getXMLElement(new String[]{TAG_phone},ct,jmpPrefix);
    }
    /**
     * 151.電話番号
     */
    public static String[] getCIContactPhoneVoice(Element ct){
        return getXMLValues(new String[]{TAG_phone,TAG_voice},ct,jmpPrefix);
    }
    /**
     * 151.電話番号
     */
    public static String[] getPhoneVoice(Element ph){
        return getXMLValues(new String[]{TAG_voice},ph,jmpPrefix);
    }
    /**
     * 152.ファクシミリ番号
     */
    public static String[] getCIContactPhoneFacsimile(Element ct){
        return getXMLValues(new String[]{TAG_phone,TAG_facsimile},ct,jmpPrefix);
    }
    /**
     * 152.ファクシミリ番号
     */
    public static String[] getPhoneFacsimile(Element ph){
        return getXMLValues(new String[]{TAG_facsimile},ph,jmpPrefix);
    }
    /**
     * 住所
     */
	public static Element getCIContactCIAddress(Element ct){
        return getXMLElement(new String[]{TAG_address},ct,jmpPrefix);
	}
    /**
     * 132.住所詳細
     */
	public static String[] getCIContactCIAddressDeliveryPoint(Element ct){
        return getXMLValues(new String[]{TAG_address,TAG_deliveryPoint},ct,jmpPrefix);
	}
    /**
     * 132.住所詳細
     */
	public static String[] getCIAddressDeliveryPoint(Element ad){
        return getXMLValues(new String[]{TAG_deliveryPoint},ad,jmpPrefix);
	}
    /**
     * 133.市区町村
     */
	public static String getCIContactCIAddressCity(Element ct){
        return getXMLValue(new String[]{TAG_address,TAG_city},ct,jmpPrefix);
	}
    /**
     * 133.市区町村
     */
	public static String getCIAddressCity(Element ad){
        return getXMLValue(new String[]{TAG_city},ad,jmpPrefix);
	}
    /**
     * 134.都道府県名
     */
	public static String getCIContactCIAddressAdministrativeArea(Element ct){
        return getXMLValue(new String[]{TAG_address,TAG_administrativeArea},ct,jmpPrefix);
	}
    /**
     * 134.都道府県名
     */
	public static String getCIAddressAdministrativeArea(Element ad){
        return getXMLValue(new String[]{TAG_administrativeArea},ad,jmpPrefix);
	}
    /**
     * 135.郵便番号
     */
	public static String getCIContactCIAddressPostalCode(Element ct){
        return getXMLValue(new String[]{TAG_address,TAG_postalCode},ct,jmpPrefix);
	}
    /**
     * 135.郵便番号
     */
	public static String getCIAddressPostalCode(Element ad){
        return getXMLValue(new String[]{TAG_postalCode},ad,jmpPrefix);
	}
    /**
     * 136.国
     */
	public static String getCIContactCIAddressCountry(Element ct){
        return getXMLValue(new String[]{TAG_address,TAG_country},ct,jmpPrefix);
	}
    /**
     * 136.国
     */
	public static String getCIAddressCountry(Element ad){
        return getXMLValue(new String[]{TAG_country},ad,jmpPrefix);
	}
    /**
     * 137.電子メールアドレス
     */
	public static String[] getCIContactCIAddressElectronicMailAddress(Element ct){
        return getXMLValues(new String[]{TAG_address,TAG_electronicMailAddress},ct,jmpPrefix);
	}
    /**
     * 137.電子メールアドレス
     */
	public static String[] getCIAddressElectronicMailAddress(Element ad){
        return getXMLValues(new String[]{TAG_electronicMailAddress},ad,jmpPrefix);
	}
    /**
     * 141.オンライン情報資源
     */
    public static Element getCIContactOnlineResource(Element ct){
        return getXMLElement(new String[]{TAG_onlineResource},ct,jmpPrefix);
    }
    /**
     * 148.リンク
     */
	public static String getCIContactCIOnlineResourceLinkage(Element ct){
        return getXMLValue(new String[]{TAG_onlineResource,TAG_linkage},ct,jmpPrefix);
	}
    /**
     * 149.記述
     */
	public static String getCIContactCIOnlineResourceDescription(Element ct){
        return getXMLValue(new String[]{TAG_onlineResource,TAG_description},ct,jmpPrefix);
	}
    /**
     * 142.案内時間
     */
	public static String getCIContactHoursOfService(Element ct){
        return getXMLValue(new String[]{TAG_hoursOfService},ct,jmpPrefix);
	}
    /**
     * 143.問合せのための手引き
     */
	public static String getCIContactContactInstructions(Element ct){
        return getXMLValue(new String[]{TAG_contactInstructions},ct,jmpPrefix);
	}

	/**
	 * 9.日付
	 */
	public static String getDateStamp(Element xmlElement){
        return getXMLValue(new String[]{TAG_dateStamp},xmlElement,jmpPrefix);
    }

	/**
	 * 9.日付
	 */
	public static void setDateStamp(String date,Element xmlElement){
        Element e = getOrCreateXMLElement(TAG_dateStamp,xmlElement,jmpPrefix);
        e.setText(date);
    }

	/**
	 * 10.メタデータ規格の名称
	 */
	public static String getMetadataStandardName(Element xmlElement) {
        return getXMLValue(new String[]{TAG_metadataStandardName},xmlElement,jmpPrefix);
    }

	/**
	 * 10.メタデータ規格の名称
	 */
	public static void setMetadataStandardName(String msn,Element xmlElement){
        Element e = getOrCreateXMLElement(TAG_metadataStandardName,xmlElement,jmpPrefix);
        e.setText(msn);
	}

	/**
	 * 11.メタデータ規格の版
	 */
	public static String getMetadataStandardVersion(Element xmlElement){
        return getXMLValue(new String[]{TAG_metadataStandardVersion},xmlElement,jmpPrefix);
    }

	/**
	 * 11.メタデータ規格の版
	 */
	public static void setMetadataStandardVersion(String msv,Element xmlElement){
        Element e = getOrCreateXMLElement(TAG_metadataStandardVersion,xmlElement,jmpPrefix);
        e.setText(msv);
    }

    /**
     * 12.参照系情報
     */
	public static List<Element> getReferenceSystemInfos(Element xmlElement) {
        return getXMLElements(new String[]{TAG_referenceSystemInfo,TAG_MDReferenceSystem},xmlElement,jmpPrefix);
	}

    /**
     * 12.参照系情報
     */
	public static void setReferenceSystemInfos(List<Element> rsi,Element xmlElement){
        removeXMLElements(TAG_referenceSystemInfo,xmlElement);
        if(rsi != null){
            for(Element e : rsi){
                xmlElement.addContent(e);
            }
        }
    }

    /**
     * 75.参照系識別子
     */
    public static Element getMDReferenceSystemReferenceSystemIdentifier(Element rsi){
        return getXMLElement(new String[]{TAG_referenceSystemIdentifier/*,TAG_RSIdentifier*/},rsi,jmpPrefix);
    }
    /**
     * 77.典拠
     */
    public static Element getRSIdentifierAuthority(Element rsid){
        return getXMLElement(new String[]{TAG_authority/*,TAG_CICitation*/},rsid,jmpPrefix);
    }
    /**
     * 78.符号
     */
    public static String getRSIdentifierCode(Element rsid){
        return getXMLValue(new String[]{TAG_code},rsid,jmpPrefix);
    }
    /**
     * 123.タイトル
     */
    public static String getCICitationTitle(Element cit){
        return getXMLValue(new String[]{TAG_title},cit,jmpPrefix);
    }
    /**
     * 124.日付
     * 145.日付
     */
    public static String[] getCICitationDate(Element cit){
        List<Element> date = getXMLElements(new String[]{TAG_date},cit,jmpPrefix);
        if(date != null && date.size() > 0){
            String[] ret = new String[date.size()];
            int i = 0;
            for(Element d : date){
                ret[i++] = getXMLValue(new String[]{TAG_date},d,jmpPrefix);
            }
            return ret;
        }
        return null;
    }
    /**
     * 146.日付型 (CI_DateTypeCode)
     */
    public static String[] getCICitationDateType(Element cit){
        List<Element> date = getXMLElements(new String[]{TAG_date},cit,jmpPrefix);
        if(date != null && date.size() > 0){
            String[] ret = new String[date.size()];
            int i = 0;
            for(Element d : date){
                ret[i++] = getXMLValue(new String[]{TAG_dateType},d,jmpPrefix);
            }
            return ret;
        }
        return null;
    }
    /**
     * 版
     */
    public static String getCICitationEdition(Element cit){
        return getXMLValue(new String[]{TAG_edition},cit,jmpPrefix);
    }
    /**
     * 識別子
     */
    public static List<Element> getCICitationIdentifier(Element cit){
        return getXMLElements(new String[]{TAG_identifier,TAG_MDIdentifier},cit,jmpPrefix);
    }

	/**
	 * 13.識別情報（MD_DataIdentification）
	 */
    public static Element getIdentificationInfo(Element xmlElement){
        return getXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification},xmlElement,jmpPrefix);
    }

	/**
	 * 13.識別情報（MD_DataIdentification）
	 */
    public static void setIdentificationInfo(Element dinfo,Element xmlElement){
        removeXMLElements(TAG_identificationInfo,xmlElement);
        if(dinfo != null)
            xmlElement.addContent(dinfo);
    }

    /**
     * 17.引用
     */
    public static Element getMDDataIdentificationCitation(Element ident){
        return getXMLElement(new String[]{TAG_citation},ident,jmpPrefix);
    }
    /**
     * 18.要約
     */
    public static String getMDDataIdentificationAbstract(Element ident){
        return getXMLValue(new String[]{TAG_abstract},ident,jmpPrefix);
    }
    /**
     * 19.目的
     */
    public static String getMDDataIdentificationPurpose(Element ident){
        return getXMLValue(new String[]{TAG_purpose},ident,jmpPrefix);
    }
    /**
     * 20.状態 (MD_ProgressCode)
     */
    public static String[] getMDDataIdentificationStatus(Element ident){
        return getXMLValues(new String[]{TAG_status},ident,jmpPrefix);
    }
    /**
     * 21.問合せ先
     */
    public static List<Element> getMDDataIdentificationPointOfContact(Element ident){
        return getXMLElements(new String[]{TAG_pointOfContact},ident,jmpPrefix);
    }
    /**
     * 22.概要の図示
     */
    public static List<Element> getMDDataIdentificationGraphicOverview(Element ident){
        return getXMLElements(new String[]{TAG_graphicOverview,TAG_MDBrowseGraphic},ident,jmpPrefix);
    }
    /**
     * 34.ファイル名
     */
    public static String getMDBrowseGraphicFileName(Element go){
        return getXMLValue(new String[]{TAG_fileName},go,jmpPrefix);
    }
    /**
     * 35.ファイル記述
     */
    public static String getMDBrowseGraphicFileDescription(Element go){
        return getXMLValue(new String[]{TAG_fileDescription},go,jmpPrefix);
    }
    /**
     * 36.ファイル型
     */
    public static String getMDBrowseGraphicFileType(Element go){
        return getXMLValue(new String[]{TAG_fileType},go,jmpPrefix);
    }
    /**
     * 23.記述的キーワード
     */
    public static List<Element> getMDDataIdentificationDescriptiveKeywords(Element ident){
        return getXMLElements(new String[]{TAG_descriptiveKeywords,TAG_MDKeywords},ident,jmpPrefix);
    }
    /**
     * 38.キーワード
     */
    public static String[] getMDKeywordsKeyword(Element kw){
        return getXMLValues(new String[]{TAG_keyword},kw,jmpPrefix);
    }
    /**
     * 39.型 (MD_KeywordTypeCode)
     */
    public static String getMDKeywordsType(Element kw){
        return getXMLValue(new String[]{TAG_type},kw,jmpPrefix);
    }
    /**
     * 24.情報資源の制約
     */
    public static List<Element> getMDDataIdentificationResourceConstraints(Element ident){
        return getXMLElements(new String[]{TAG_resourceConstraints,TAG_MDConstraints},ident,jmpPrefix);
    }
    /**
     * 46.利用制限
     */
    public static String[] getMDConstraintsUseLimitation(Element cnst){
        return getXMLValues(new String[]{TAG_useLimitation},cnst,jmpPrefix);
    }
    /**
     * 集成情報
     */
    public static List<Element> getMDDataIdentificationAggregationInfo(Element ident){
        return getXMLElements(new String[]{TAG_aggregationInfo,TAG_MDAggregateInformation},ident,jmpPrefix);
    }
    /**
     * 集成データ集合名
     */
    public static Element getMDAggregateInformationAggregateDataSetName(Element aggr){
        return getXMLElement(new String[]{TAG_aggregateDataSetName,TAG_CICitation},aggr,jmpPrefix);
    }
    /**
     * 集成データ集合識別子
     */
    public static Element getMDAggregateInformationAggregateDataSetIdentifier(Element aggr){
        return getXMLElement(new String[]{TAG_aggregateDataSetIdentifier,TAG_MDIdentifier},aggr,jmpPrefix);
    }
    /**
     * 関連型
     */
    public static String getMDAggregateInformationAssociationType(Element aggr){
        return getXMLValue(new String[]{TAG_associationType},aggr,jmpPrefix);
    }
    /**
     * 活動型
     */
    public static String getMDAggregateInformationInitiativeType(Element aggr){
        return getXMLValue(new String[]{TAG_initiativeType},aggr,jmpPrefix);
    }
    /**
     * 26.空間表現型 (MD_SpatialRepresentationTypeCode)
     */
	public static String[] getMDDataIdentificationSpatialRepresentationType(Element ident){
        return getXMLValues(new String[]{TAG_spatialRepresentationType},ident,jmpPrefix);
	}
    /**
     * 27.空間解像度
     */
    public static List<Element> getMDDataIdentificationSpatialResolution(Element ident){
        return getXMLElements(new String[]{TAG_spatialResolution/*,TAG_MDResolution*/},ident,jmpPrefix);
    }
    /**
     * 43.等価縮尺
     */
    public static Element getMDResolutionEquivalent(Element reso){
        return getXMLElement(new String[]{TAG_equivalentScale/*,TAG_MDRepresentativeFraction*/},reso,jmpPrefix);
    }
    /**
     * 41.分母
     */
    public static String getMDRepresentativeFractionDenominator(Element rpfr){
        return getXMLValue(new String[]{TAG_denominator},rpfr,jmpPrefix);
    }
    /**
     * 41.分母
     */
    public static String getMDResolutionEquivalentScaleDenominator(Element reso){
        return getXMLValue(new String[]{TAG_equivalentScale/*,TAG_MDRepresentativeFraction*/,TAG_denominator},reso,jmpPrefix);
    }
    /**
     * 44.距離
     */
    public static Element getMDResolutionDistance(Element reso){
        return getXMLElement(new String[]{TAG_distance,TAG_Distance},reso,jmpPrefix);
    }
    /**
     * 6.6.測定値
     */
    public static String getDistanceValue(Element dist){
        return getXMLValue(new String[]{TAG_value},dist,jmpPrefix);
    }
    /**
     * 6.7.測定単位
     */
    public static Element getDistanceUnitOfMeasure(Element dist){
        return getXMLElement(new String[]{TAG_uom,TAG_unitOfMeasure},dist,jmpPrefix);
    }
    /**
     * 6.2.単位の名称
     */
    public static String getUnitOfMeasureName(Element uom){
        return getXMLValue(new String[]{TAG_name},uom,jmpPrefix);
    }
    /**
     * 6.3.単位の種類
     */
    public static String getUnitOfMeasureMeasurementType(Element uom){
        return getXMLValue(new String[]{TAG_measurementType},uom,jmpPrefix);
    }
    /**
     * 28.言語
     */
    public static String[] getMDDataIdentificationLanguage(Element ident){
        return getXMLValues(new String[]{TAG_language,TAG_isoCode},ident,jmpPrefix);
    }
    /**
     * 29.文字集合 (MD_CharacterSetCode)
     */
    public static String[] getMDDataIdentificationCharacterSet(Element ident){
        return getXMLValues(new String[]{TAG_characterSet},ident,jmpPrefix);
    }
    /**
     * 30.主題分類 (MD_TopicCategoryCode)
     */
    public static String[] getMDDataIdentificationTopicCategory(Element ident){
        return getXMLValues(new String[]{TAG_topicCategory},ident,jmpPrefix);
    }
    /**
     * 32.範囲
     */
    public static List<Element> getMDDataIdentificationExtent(Element ident){
        return getXMLElements(new String[]{TAG_extent},ident,jmpPrefix);
    }
    /**
     * 94.記述
     */
    public static String getEXExtentDescription(Element ext){
        return getXMLValue(new String[]{TAG_description},ext,jmpPrefix);
    }
    /**
     * 95.地理要素
     * 103.地理境界ボックス
     */
    public static List<Element> getEXExtentEXGeographicBoundingBox(Element ext){
        return getXMLElements(new String[]{TAG_geographicElement,TAG_EXGeographicBoundingBox},ext,jmpPrefix);
    }
    /**
     * 104.西側境界経度
     * 105.東側境界経度
     * 106.南側境界緯度
     * 107.北側境界緯度
     */
    public static String[] getEXGeographicBoundingBox(Element gbbox){
        String west = getXMLValue(new String[]{TAG_westBoundLongitude},gbbox,jmpPrefix);
        String east = getXMLValue(new String[]{TAG_eastBoundLongitude},gbbox,jmpPrefix);
        String south = getXMLValue(new String[]{TAG_southBoundLatitude},gbbox,jmpPrefix);
        String north = getXMLValue(new String[]{TAG_northBoundLatitude},gbbox,jmpPrefix);
        return new String[]{west,east,south,north};
    }
    /**
     * 108.座標境界ボックス
     */
    public static List<Element> getEXExtentEXCoordinateBoundingBox(Element ext){
        return getXMLElements(new String[]{TAG_geographicElement,TAG_EXCoordinateBoundingBox},ext,jmpPrefix);
    }
    /**
     * 109.西側境界座標
     * 110.東側境界座標
     * 111.南側境界座標
     * 112.北側境界座標
     */
    public static String[] getEXCoordinateBoundingBox(Element cbbox){
        String west = getXMLValue(new String[]{TAG_westBoundCoordinate},cbbox,jmpPrefix);
        String east = getXMLValue(new String[]{TAG_eastBoundCoordinate},cbbox,jmpPrefix);
        String south = getXMLValue(new String[]{TAG_southBoundCoordinate},cbbox,jmpPrefix);
        String north = getXMLValue(new String[]{TAG_northBoundCoordinate},cbbox,jmpPrefix);
        return new String[]{west,east,south,north};
    }
    /**
     * 113.地理記述
     */
    public static List<Element> getEXExtentEXGeographicDescription(Element ext){
        return getXMLElements(new String[]{TAG_geographicElement,TAG_EXGeographicDescription},ext,jmpPrefix);
    }
    /**
     * 114.地理識別子
     */
    public static Element getEXGeographicDescriptionGeographicIdentifier(Element gdesc){
        return getXMLElement(new String[]{TAG_geographicIdentifier/*,TAG_MDIdentifier*/},gdesc,jmpPrefix);
    }
    /**
     * 101.境界ポリゴン
     */
    public static List<Element> getEXExtentEXBoundingPolygon(Element ext){
        return getXMLElements(new String[]{TAG_geographicElement,TAG_EXBoundingPolygon},ext,jmpPrefix);
    }
    /**
     * 102.境界ポリゴン
     */
    public static Element getEXBoundingPolygonGMPolygon(Element bpoly){
        // FIXME! ほんとはマルチ
        return getXMLElement(new String[]{TAG_GMPolygon},bpoly,jmpPrefix);
    }
    /**
     * 6.27 ポリゴン
     */
    public static Element getGMPolygonPoygon(Element gmpoly){
        return getXMLElement(new String[]{TAG_polygon},gmpoly,jmpPrefix);
    }
    /**
     * 6.28 外側境界
     */
    public static Element getPolygonExterior(Element poly){
        return getXMLElement(new String[]{TAG_exterior},poly,jmpPrefix);
    }
    /**
     * 6.29 内側境界
     */
    public static List<Element> getPolygonInterior(Element poly){
        return getXMLElements(new String[]{TAG_interior},poly,jmpPrefix);
    }
    /**
     * 6.30 閉曲線
     * 6.31 座標列
     */
    public static String getRingCoordinates(Element r){
        return getXMLValue(new String[]{TAG_coordinates},r,jmpPrefix);
    }
    /**
     * 6.32 位置
     */
    public static String[] getRingPos(Element r){
        return getXMLValues(new String[]{TAG_pos},r,jmpPrefix);
    }
    /**
     * 6.33 個別座標
     */
    public static List<Element> getRingCoord(Element r){
        return getXMLElements(new String[]{TAG_coord},r,jmpPrefix);
    }
    /**
     * 99.範囲型符号
     */
    public static String getEXGeographicExtentExtentTypeCode(Element gext){
        return getXMLValue(new String[]{TAG_extentTypeCode},gext,jmpPrefix);
    }
    /**
     * 100.範囲参照系
     */
    public static Element getEXGeographicExtentExtentReferenceSystem(Element gext){
        return getXMLElement(new String[]{TAG_extentReferenceSystem},gext,jmpPrefix);
    }
    /**
     * 96.時間要素
     */
    public static List<Element> getEXExtentTemporalElement(Element ext){
        return getXMLElements(new String[]{TAG_temporalElement,TAG_EXTemporalExtent},ext,jmpPrefix);
    }
    /**
     * 116.範囲
     */
    public static Element getEXTemporalExtentExtent(Element tex){
        return getXMLElement(new String[]{TAG_extent},tex,jmpPrefix);
    }
    /**
     * 6.13 瞬間
     */
    public static String getTMPrimitiveInstant(Element tmp){
        return getXMLValue(new String[]{TAG_instant},tmp,jmpPrefix);
    }
    /**
     * 6.14 期間
     */
    public static String[] getTMPrimitiveBeginEnd(Element tmp){
        String beg = getXMLValue(new String[]{TAG_beginEnd,TAG_begin},tmp,jmpPrefix);
        String end = getXMLValue(new String[]{TAG_beginEnd,TAG_end},tmp,jmpPrefix);
        if(beg != null || end != null)
            return new String[]{beg,end};
        return null;
    }
    /**
     * 97.垂直要素
     */
    public static List<Element> getEXExtentVerticalElement(Element ext){
        return getXMLElements(new String[]{TAG_verticalElement,TAG_EXVerticalExtent},ext,jmpPrefix);
    }
    /**
     * 118.最低値
     */
    public static String getEXVerticalExtentMinimumValue(Element vex){
        return getXMLValue(new String[]{TAG_minimumValue},vex,jmpPrefix);
    }
    /**
     * 119.最高値
     */
    public static String getEXVerticalExtentMaximumValue(Element vex){
        return getXMLValue(new String[]{TAG_maximumValue},vex,jmpPrefix);
    }
    /**
     * 120.測定単位
     */
    public static Element getEXVerticalExtentUnitOfMeasure(Element vex){
        return getXMLElement(new String[]{TAG_unitOfMeasure},vex,jmpPrefix);
    }
    /**
     * 121.垂直原子
     */
    public static Element getEXVerticalExtentVerticalDatum(Element vex){
        return getXMLElement(new String[]{TAG_verticalDatum,TAG_SCVerticalDatum},vex,jmpPrefix);
    }
    /**
     * 6.39.原子の識別子
     */
    public static Element getSCVerticalDatumDatumID(Element vd){
        return getXMLElement(new String[]{TAG_datumID},vd,jmpPrefix);
    }

    /**
	 * 14.配布情報（MD_Distribution）
     */
    public static Element getDistributionInfo(Element xmlElement){
        return getXMLElement(new String[]{TAG_distributionInfo,TAG_MDDistribution},xmlElement,jmpPrefix);
    }

    /**
	 * 14.配布情報（MD_Distribution）
     */
    public static void setDistributionInfo(Element db,Element xmlElement){
        removeXMLElements(TAG_distributionInfo,xmlElement);
        if(db != null){
            xmlElement.addContent(db);
        }
    }

    /**
     * 81.配布書式
     */
    public static List<Element> getMDDistributionDistributionFormat(Element dinfo){
        return getXMLElements(new String[]{TAG_distributionFormat,TAG_MDFormat},dinfo,jmpPrefix);
    }
    /**
     * 87.書式名
     */
    public static String getMDFormatName(Element fmt){
        return getXMLValue(new String[]{TAG_name},fmt,jmpPrefix);
    }
    /**
     * 88.バージョン
     */
    public static String getMDFormatVersion(Element fmt){
        return getXMLValue(new String[]{TAG_version},fmt,jmpPrefix);
    }
    /**
     * 82.交換任意選択
     */
    public static List<Element> getMDDistributionTransferOptions(Element dinfo){
        return getXMLElements(new String[]{TAG_transferOptions,TAG_MDDigitalTransferOptions},dinfo,jmpPrefix);
    }
    /**
     * 84.オンライン
     */
    public static List<Element> getMDDigitalTransferOptionsOnLine(Element dtran){
        return getXMLElements(new String[]{TAG_onLine},dtran,jmpPrefix);
    }
    /**
     * 148.リンク
     */
    public static String getCIOnlineResourceLinkage(Element onl){
        return getXMLValue(new String[]{TAG_linkage},onl,jmpPrefix);
    }
    /**
     * プロトコル
     */
    public static String getCIOnlineResourceProtocol(Element onl){
        return getXMLValue(new String[]{TAG_protocol},onl,jmpPrefix);
    }
    /**
     * 応用プロファイル
     */
    public static String getCIOnlineResourceApplicationProfile(Element onl){
        return getXMLValue(new String[]{TAG_applicationProfile},onl,jmpPrefix);
    }
    /**
     * オンライン資源名
     */
    public static String getCIOnlineResourceName(Element onl){
        return getXMLValue(new String[]{TAG_name},onl,jmpPrefix);
    }
    /**
     * 149.記述
     */
    public static String getCIOnlineResourceDescription(Element onl){
        return getXMLValue(new String[]{TAG_description},onl,jmpPrefix);
    }
    /**
     * 機能
     */
    public static String getCIOnlineResourceFunction(Element onl){
        return getXMLValue(new String[]{TAG_function},onl,jmpPrefix);
    }
    /**
     * 85.オフライン
     */
    public static Element getMDDigitalTransferOptionsOffLine(Element dtran){
        return getXMLElement(new String[]{TAG_offLine},dtran,jmpPrefix);
    }
    /**
     * 90.媒体名 (MD_MediumNameCode)
     */
    public static String getMDMediumName(Element med){
        return getXMLValue(new String[]{TAG_name},med,jmpPrefix);
    }
    /**
     * 90.媒体名 (MD_MediumNameCode)
     */
    public static String getMDMediumMediumNote(Element med){
        return getXMLValue(new String[]{TAG_mediumNote},med,jmpPrefix);
    }

	/**
	 * 15.データ品質情報
	 */
	public static List<Element> getDataQualityInfos(Element xmlElement){
        return getXMLElements(new String[]{TAG_dataQualityInfo,TAG_DQDataQuality},xmlElement,jmpPrefix);
    }

	/**
	 * 15.データ品質情報
	 */
    public static void setDataQualityInfos(List<Element> dq,Element xmlElement){
        removeXMLElements(TAG_dataQualityInfo,xmlElement);
        if(dq != null){
            for(Element e : dq){
                xmlElement.addContent(e);
            }
        }
    }

    /**
     * 48.適用範囲
     */
    public static Element getDQDataQualityScope(Element dq){
        return getXMLElement(new String[]{TAG_scope},dq,jmpPrefix);
    }
    /**
     * 68.レベル (MD_ScopeCode)
     */
    public static String getDQScopeLevel(Element scp){
        return getXMLValue(new String[]{TAG_level},scp,jmpPrefix);
    }
    /**
     * 69.範囲
     */
    public static Element getDQScopeExtent(Element scp){
        return getXMLElement(new String[]{TAG_extent},scp,jmpPrefix);
    }
    /**
     * 70.レベル記述
     */
    public static String[] getDQScopeLevelDescription(Element scp){
        return getXMLValues(new String[]{TAG_levelDescription},scp,jmpPrefix);
    }
    /**
     * 49.報告
     */
    public static List<Element> getDQDataQualityReport(Element dq){
        return getXMLElements(new String[]{TAG_report,TAG_DQElement},dq,jmpPrefix);
    }
    /**
     * 54.評価手法の記述
     */
    public static String getDQElementEvaluationMethodDescription(Element dqe){
        return getXMLValue(new String[]{TAG_evaluationMethodDescription},dqe,jmpPrefix);
    }
    /**
     * (4.4.4) 59.適合性の結果
     */
    public static Element getDQElementDQConformanceResult(Element dqe){
        return getXMLElement(new String[]{TAG_result,TAG_DQConformanceResult},dqe,jmpPrefix);
    }
    /**
     * 60.仕様
     */
    public static Element getDQConformanceResultSpecification(Element crslt){
        return getXMLElement(new String[]{TAG_specification},crslt,jmpPrefix);
    }
    /**
     * 61.説明
     */
    public static String getDQConformanceResultExplanation(Element crslt){
        return getXMLValue(new String[]{TAG_explanation},crslt,jmpPrefix);
    }
    /**
     * 62.合否
     */
    public static String getDQConformanceResultPass(Element crslt){
        return getXMLValue(new String[]{TAG_pass},crslt,jmpPrefix);
    }
    /**
     * (4.4.4) 63.定量的結果
     */
    public static Element getDQElementDQQuantitativeResult(Element dqe){
        return getXMLElement(new String[]{TAG_result,TAG_DQQuantitativeResult},dqe,jmpPrefix);
    }
    /**
     * 64.測定値の単位
     */
    public static Element getDQQuantitativeResultValueUnit(Element qrslt){
        return getXMLElement(new String[]{TAG_valueUnit},qrslt,jmpPrefix);
    }
    /**
     * 65.誤差統計
     */
    public static String getDQQuantitativeResultErrorStatistic(Element qrslt){
        return getXMLValue(new String[]{TAG_errorStatistic},qrslt,jmpPrefix);
    }
    /**
     * 66.測定値
     */
    public static String[] getDQQuantitativeResultValue(Element qrslt){
        return getXMLValues(new String[]{TAG_value,TAG_otherValue},qrslt,jmpPrefix);
    }
    /**
     * 56.データ品質要素型 (DQ_TypeOfQualityEvaluationCode)
     */
    public static String getDQElementTypeOfQualityEvaluation(Element dqe){
        return getXMLValue(new String[]{TAG_typeOfQualityEvaluation},dqe,jmpPrefix);
    }
    /**
     * 50.系譜
     */
    public static Element getDQDataQualityLineage(Element dq){
        return getXMLElement(new String[]{TAG_lineage,TAG_LILineage},dq,jmpPrefix);
    }
    /**
     * 52.説明
     */
    public static String getLILineageStatement(Element lin){
        return getXMLValue(new String[]{TAG_statement},lin,jmpPrefix);
    }
    /**
     * 処理過程
     */
    public static List<Element> getLILineageProcessStep(Element lin){
        return getXMLElements(new String[]{TAG_processStep,TAG_LIProcessStep},lin,jmpPrefix);
    }
    /**
     * 記述
     */
    public static String getLIProcessStepDescription(Element ps){
        return getXMLValue(new String[]{TAG_description},ps,jmpPrefix);
    }
    /**
     * 論理的根拠
     */
    public static String getLIProcessStepRationale(Element ps){
        return getXMLValue(new String[]{TAG_rationale},ps,jmpPrefix);
    }
    /**
     * 日時
     */
    public static String getLIProcessStepDateTime(Element ps){
        return getXMLValue(new String[]{TAG_dateTime},ps,jmpPrefix);
    }
    /**
     * 作成者
     */
    public static List<Element> getLIProcessStepProcessor(Element ps){
        return getXMLElements(new String[]{TAG_processor,TAG_CIResponsibleParty},ps,jmpPrefix);
    }
    /**
     * 元情報
     */
    public static List<Element> getLIProcessStepSource(Element ps){
        return getXMLElements(new String[]{TAG_source,TAG_LISource},ps,jmpPrefix);
    }
    /**
     * 元情報
     */
    public static List<Element> getLILineageSource(Element lin){
        return getXMLElements(new String[]{TAG_source,TAG_LISource},lin,jmpPrefix);
    }
    /**
     * 記述
     */
    public static String getLISourceDescription(Element s){
        return getXMLValue(new String[]{TAG_description},s,jmpPrefix);
    }
    /**
     * 縮尺分母
     */
    public static Element getLISourceScaleDenominator(Element s){
        return getXMLElement(new String[]{TAG_scaleDenominator,TAG_MDRepresentativeFraction},s,jmpPrefix);
    }
    /**
     * 元情報の参照系
     */
    public static Element getLISourceSourceReferenceSystem(Element s){
        return getXMLElement(new String[]{TAG_sourceReferenceSystem,TAG_MDReferenceSystem},s,jmpPrefix);
    }
    /**
     * 元情報の引用
     */
    public static Element getLISourceSourceCitation(Element s){
        return getXMLElement(new String[]{TAG_sourceCitation,TAG_CICitation},s,jmpPrefix);
    }
    /**
     * 元情報の範囲
     */
    public static List<Element> getLISourceSourceExtent(Element s){
        return getXMLElements(new String[]{TAG_sourceExtent},s,jmpPrefix);
    }
    /**
     * 元情報の作成工程
     */
    public static List<Element> getLISourceSourceStep(Element s){
        return getXMLElements(new String[]{TAG_sourceStep,TAG_LIProcessStep},s,jmpPrefix);
    }

	/**
	 * 識別情報-データ識別-引用-タイトルを取得
	 */
	public static String getTitle(Element xmlElement){
        return getXMLValue(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_citation,TAG_title},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-引用-タイトルを設定
	 */
	public static void setTitle(String title,Element xmlElement){
        Element t = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_citation,TAG_title},xmlElement,jmpPrefix);
        t.setText(title);
	}

	/**
	 * 識別情報-データ識別-引用-日付を取得
	 */
    public static String getIdentificationDate(String type,Element xmlElement){
        List<Element> date = getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_citation,TAG_date},xmlElement,jmpPrefix);
        if(date != null){
            for(Element d : date){
                if(type.equals(getXMLValue(new String[]{TAG_dateType},d,jmpPrefix)))
                    return getXMLValue(new String[]{TAG_date},d,jmpPrefix);
            }
		}
		return null;
    }

	/**
	 * 識別情報-データ識別-引用-日付を取得
	 */
    public static void setIdentificationDate(String date,String type,Element xmlElement){
        Element cit = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_citation},xmlElement,jmpPrefix);
        List<Element> d1 = getXMLElements(new String[]{TAG_date},cit,jmpPrefix);
        Element d2 = null;
        if(d1 != null){
            for(Element d : d1){
                if(type.equals(getXMLValue(new String[]{TAG_dateType},d,jmpPrefix))){
                    d2 = d;
                    break;
                }
            }
        }
        if(d2 == null){
            d2 = createXMLElement(TAG_date,cit);
            Element dt = createXMLElement(TAG_dateType,d2);
            dt.setText(type);
        }
        Element d3 = getOrCreateXMLElement(TAG_date,d2,jmpPrefix);
        d3.setText(date);
    }

	/**
	 * 識別情報-データ識別-引用-日付-作成日を取得
	 */
	public static String getCreationDate(Element xmlElement){
        return getIdentificationDate(CI_DateTypeCode.CREATION,xmlElement);
	}

	/**
	 * 識別情報-データ識別-引用-日付-作成日を設定
	 */
	public static void setCreationDate(String date,Element xmlElement){
        setIdentificationDate(date,CI_DateTypeCode.CREATION,xmlElement);
	}

	/**
	 * 識別情報-データ識別-引用-日付-刊行日を取得
	 */
	public static String getPublicationDate(Element xmlElement){
        return getIdentificationDate(CI_DateTypeCode.PUBLICATION,xmlElement);
	}

	/**
	 * 識別情報-データ識別-引用-日付-刊行日を設定
	 */
	public static void setPublicationDate(String date,Element xmlElement){
        setIdentificationDate(date,CI_DateTypeCode.PUBLICATION,xmlElement);
	}

	/**
	 * 識別情報-データ識別-引用-日付-改訂日を取得
	 */
	public static String getRevisionDate(Element xmlElement){
        return getIdentificationDate(CI_DateTypeCode.REVISION,xmlElement);
	}

	/**
	 * 識別情報-データ識別-引用-日付-改訂日を設定
	 */
	public static void setRevisionDate(String date,Element xmlElement){
        setIdentificationDate(date,CI_DateTypeCode.REVISION,xmlElement);
	}

	/**
	 * 識別情報-データ識別-要約を取得
	 */
	public static String getAbstract(Element xmlElement){
        return getXMLValue(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_abstract},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-要約を設定
	 */
	public static void setAbstract(String text,Element xmlElement){
        Element abs = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_abstract},xmlElement,jmpPrefix);
        abs.setText(text);
	}

	/**
	 * 識別情報-データ識別-目的を取得
	 */
	public static String getPurpose(Element xmlElement){
        return getXMLValue(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_purpose},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-目的を設定
	 */
	public static void setPurpose(String text,Element xmlElement){
        Element pur = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_purpose},xmlElement,jmpPrefix);
        pur.setText(text);
	}

	/**
	 * 識別情報-データ識別-状態を取得
	 */
	public static String getStatus(Element xmlElement){
        return getXMLValue(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_status},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-状態を設定
	 */
	public static void setStatus(String c,Element xmlElement){
        Element st = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_status},xmlElement,jmpPrefix);
        st.setText(c);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-役割を取得
	 */
	public static String[] getRoles(Element xmlElement){
        return getXMLValues(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_pointOfContact,TAG_role},xmlElement,jmpPrefix);
	}

    /**
     *
     */
    public static Element getPointOfContact(String role,Element xmlElement){
        List<Element> poc = getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_pointOfContact},xmlElement,jmpPrefix);
        if(poc != null){
            for(Element p : poc){
                if(role.equals(getXMLValue(new String[]{TAG_role},p,jmpPrefix)))
                    return p;
            }
		}
        return null;
    }

    /**
     *
     */
    public static Element getOrCreatePointOfContact(String role,Element xmlElement){
        Element mdid = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification},xmlElement,jmpPrefix);
        List<Element> poc = getXMLElements(new String[]{TAG_pointOfContact},mdid,jmpPrefix);
        Element p = null;
        if(poc != null){
            for(Element p1 : poc){
                if(role.equals(getXMLValue(new String[]{TAG_role},p1,jmpPrefix))){
                    p = p1;
                    break;
                }
            }
        }
        if(p == null){
            p = createXMLElement(TAG_pointOfContact,mdid);
            Element r = createXMLElement(TAG_role,p);
            r.setText(role);
        }
        return p;
    }

	/**
	 * 識別情報-データ識別-問合わせ先-個人名を取得
	 */
	public static String getIndividualName(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_individualName},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-個人名を設定
	 */
	public static void setIndividualName(String role,String name,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element nm = getOrCreateXMLElement(TAG_individualName,p,jmpPrefix);
        nm.setText(name);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-組織名を取得
	 */
	public static String getOrganisationName(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_organisationName},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-組織名を設定
	 */
	public static void setOrganisationName(String role,String name,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element nm = getOrCreateXMLElement(TAG_organisationName,p,jmpPrefix);
        nm.setText(name);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-電話番号-電話番号を取得
	 */
	public static String[] getPhone(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValues(new String[]{TAG_contactInfo,TAG_phone,TAG_voice},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-電話番号-電話番号を設定
	 */
	public static void setPhone(String role,String[] phone,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element ph = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_phone},p,jmpPrefix);
        removeXMLElements(TAG_voice,ph);
        for(String num : phone){
            Element v = createXMLElement(TAG_voice,ph);
            v.setText(num);
        }
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-住所を取得
	 */
	public static String[] getDeliveryPoint(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValues(new String[]{TAG_contactInfo,TAG_address,TAG_deliveryPoint},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-住所を設定
	 */
	public static void setDeliveryPoint(String role,String[] dp,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element addr = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address},p,jmpPrefix);
        removeXMLElements(TAG_deliveryPoint,addr);
        for(String dp1 : dp){
            Element e = createXMLElement(TAG_deliveryPoint,addr);
            e.setText(dp1);
        }
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-市区町村を取得
	 */
	public static String getCity(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_address,TAG_city},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-市区町村を設定
	 */
	public static void setCity(String role,String city,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element c = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_city},p,jmpPrefix);
        c.setText(city);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-市区町村コードを取得
	 */
	public static String getCityCode(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_address,TAG_cityCode},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-市区町村コードを設定
	 */
	public static void setCityCode(String role,String city,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element c = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_cityCode},p,jmpPrefix);
        c.setText(city);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-都道府県名を取得
	 */
	public static String getAdministrativeArea(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_address,TAG_administrativeArea},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-都道府県名を設定
	 */
	public static void setAdministrativeArea(String role,String area,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element a = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_administrativeArea},p,jmpPrefix);
        a.setText(area);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-都道府県コードを取得
	 */
	public static String getAdministrativeAreaCode(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_address,TAG_administrativeAreaCode},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-都道府県コードを設定
	 */
	public static void setAdministrativeAreaCode(String role,String area,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element a = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_administrativeAreaCode},p,jmpPrefix);
        a.setText(area);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-郵便番号を取得
	 */
	public static String getPostalCode(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_address,TAG_postalCode},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-郵便番号を設定
	 */
	public static void setPostalCode(String role,String code,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element pos = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_postalCode},p,jmpPrefix);
        pos.setText(code);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-国を取得
	 */
	public static String getCountry(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_address,TAG_country},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-国を設定
	 */
	public static void setCountry(String role,String country,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element c = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_country},p,jmpPrefix);
        c.setText(country);
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-電子メールアドレスを取得
	 */
	public static String[] getEMailAddress(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValues(new String[]{TAG_contactInfo,TAG_address,TAG_electronicMailAddress},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-住所-電子メールアドレスを設定
	 */
	public static void setEMailAddress(String role,String[] email,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element addr = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address},p,jmpPrefix);
        removeXMLElements(TAG_electronicMailAddress,addr);
        for(String em : email){
            Element e = createXMLElement(TAG_electronicMailAddress,addr);
            e.setText(em);
        }
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-オンライン情報資源-リンクを取得
	 */
	public static String getLinkage(String role,Element xmlElement){
        Element p = getPointOfContact(role,xmlElement);
        if(p != null)
            return getXMLValue(new String[]{TAG_contactInfo,TAG_onlineResource,TAG_linkage},p,jmpPrefix);
		return null;
	}

	/**
	 * 識別情報-データ識別-問合わせ先-問合せ情報-オンライン情報資源-リンクを設定
	 */
	public static void setLinkage(String role,String link,Element xmlElement){
        Element p = getOrCreatePointOfContact(role,xmlElement);
        Element lk = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_onlineResource,TAG_linkage},p,jmpPrefix);
        lk.setText(link);
	}

	/**
	 * 識別情報-データ識別-制約情報-利用制限を取得
	 */
	public static String[] getUseLimitation(Element xmlElement){
        return getXMLValues(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_resourceConstraints,TAG_MDConstraints,TAG_useLimitation},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-制約情報-利用制限を設定
	 */
	public static void setUseLimitation(String[] ulimit,Element xmlElement){
        Element mdconst = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_resourceConstraints,TAG_MDConstraints},xmlElement,jmpPrefix);
        removeXMLElements(TAG_useLimitation,mdconst);
        for(String ul : ulimit){
            Element e = createXMLElement(TAG_useLimitation,mdconst);
            e.setText(ul);
        }
	}

	/**
	 * 識別情報-データ識別-言語
	 */
    public static String getDataLanguage(Element xmlElement){
        return getXMLValue(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_language,TAG_isoCode},xmlElement,jmpPrefix);
    }

	/**
	 * 識別情報-データ識別-言語
	 */
    public static void setDataLanguage(String isocode,Element xmlElement){
        Element e = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_language,TAG_isoCode},xmlElement,jmpPrefix);
        e.setText(isocode);
    }

	/**
	 * 識別情報-データ識別-文字集合 (MD_CharacterSetCode)
	 */
	public static String getDataCharacterSet(Element xmlElement){
        return getXMLValue(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_characterSet},xmlElement,jmpPrefix);
    }

	/**
	 * 識別情報-データ識別-文字集合 (MD_CharacterSetCode)
	 */
	public static void setDataCharacterSet(String cs,Element xmlElement){
        Element e = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_characterSet},xmlElement,jmpPrefix);
        e.setText(cs);
	}

	/**
	 * 識別情報-データ識別-記述的キーワードを取得
	 */
	public static Map<String,String[]> getKeywords(Element xmlElement){
        List<Element> mdkey = getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_descriptiveKeywords,TAG_MDKeywords},xmlElement,jmpPrefix);
        if(mdkey != null){
            TreeMap<String,String[]> t = new TreeMap<String,String[]>();
            for(Element mdk : mdkey){
                String type = getXMLValue(new String[]{TAG_type},mdk,jmpPrefix);
                if(type == null)
                    type = "";
                String[] kw = getXMLValues(new String[]{TAG_keyword},mdk,jmpPrefix);
                String[] kwcur = t.get(type);
                if(kwcur != null){
                    String[] kwnew = new String[kwcur.length+kw.length];
                    System.arraycopy(kwcur,0,kwnew,0,kwcur.length);
                    System.arraycopy(kw,0,kwnew,kwcur.length,kw.length);
                    kw = kwnew;
                }
                t.put(type,kw);
            }
            return t;
        }
		return null;
	}

	/**
	 * 識別情報-データ識別-記述的キーワードを設定
	 */
	public static void setKeywords(String type,String[] keywords,Element xmlElement){
        Element desckw = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_descriptiveKeywords},xmlElement,jmpPrefix);
        List<Element> kws = getXMLElements(new String[]{TAG_MDKeywords},desckw,jmpPrefix);
        Element kw = null;
        if(kws != null){
            for(Element kw1 : kws){
                // if(type.equals(getXMLValue(new String[]{TAG_type},kw1))){
                //     kw = kw1;
                //     break;
                // }
                // type が一致するものを一旦削除
                if(type.equals(getXMLValue(new String[]{TAG_type},kw1,jmpPrefix))){
                    removeXMLElement(kw1,desckw);
                }
            }
        }
        if(kw == null){
            kw = createXMLElement(TAG_MDKeywords,desckw);
            Element k = createXMLElement(TAG_type,kw);
            k.setText(type);
        }
        removeXMLElements(TAG_keyword,kw);
        for(String w : keywords){
            Element e = createXMLElement(TAG_keyword,kw);
            e.setText(w);
        }
	}

	/**
	 * 識別情報-データ識別-主題分類を取得
	 */
	public static String[] getTopicCategory(Element xmlElement){
        return getXMLValues(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_topicCategory},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-主題分類を設定
	 */
	public static void setTopicCategory(String[] category,Element xmlElement){
        Element mdid = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification},xmlElement,jmpPrefix);
        removeXMLElements(TAG_topicCategory,mdid);
        for(String cat : category){
            Element e = createXMLElement(TAG_topicCategory,mdid);
            e.setText(cat);
        }
	}

    /**
     *
     */
    public static Element getOrCreatePointOfContact(Element xmlElement){
        Element con = getOrCreateXMLElement(TAG_contact,xmlElement,jmpPrefix);
        String rt = getXMLValue(new String[]{TAG_role},con,jmpPrefix);
        if(rt == null || rt.length() == 0){
            Element role = getOrCreateXMLElement(TAG_role,con,jmpPrefix);
            role.setText(CI_RoleCode.POINT_OF_CONTACT);
        }
        return con;
    }

	/**
	 * 問合わせ先-個人名を取得
	 */
	public static String getIndividualName(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_individualName},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-個人名を設定
	 */
	public static void setIndividualName(String name,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element iname = getOrCreateXMLElement(TAG_individualName,con,jmpPrefix);
        iname.setText(name);
	}

	/**
	 * 問合わせ先-組織名を取得
	 */
	public static String getOrganisationName(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_organisationName},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-組織名を設定
	 */
	public static void setOrganisationName(String name,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element oname = getOrCreateXMLElement(TAG_organisationName,con,jmpPrefix);
        oname.setText(name);
	}

	/**
	 * 問合わせ先-問合せ情報-電話番号-電話番号を取得
	 */
	public static String[] getPhone(Element xmlElement){
        return getXMLValues(new String[]{TAG_contact,TAG_contactInfo,TAG_phone,TAG_voice},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-電話番号-電話番号を設定
	 */
	public static void setPhone(String[] phone,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element ph = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_phone},con,jmpPrefix);
        removeXMLElements(TAG_voice,ph);
        for(String num : phone){
            Element v = createXMLElement(TAG_voice,ph);
            v.setText(num);
        }
	}

	/**
	 * 問合わせ先-問合せ情報-住所-住所を取得
	 */
	public static String[] getDeliveryPoint(Element xmlElement){
        return getXMLValues(new String[]{TAG_contact,TAG_contactInfo,TAG_address,TAG_deliveryPoint},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-住所を設定
	 */
	public static void setDeliveryPoint(String[] dp,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element addr = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address},con,jmpPrefix);
        removeXMLElements(TAG_deliveryPoint,addr);
        for(String dp1 : dp){
            Element e = createXMLElement(TAG_deliveryPoint,addr);
            e.setText(dp1);
        }
	}

	/**
	 * 問合わせ先-問合せ情報-住所-市区町村を取得
	 */
	public static String getCity(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_address,TAG_city},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-市区町村を設定
	 */
	public static void setCity(String city,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element c = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_city},con,jmpPrefix);
        c.setText(city);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-市区町村コードを取得
	 */
	public static String getCityCode(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_address, TAG_cityCode},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-市区町村コードを設定
	 */
	public static void setCityCode(String code,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element pos = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_cityCode},con,jmpPrefix);
        pos.setText(code);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-都道府県名を取得
	 */
	public static String getAdministrativeArea(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_address,TAG_administrativeArea},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-都道府県名を設定
	 */
	public static void setAdministrativeArea(String area,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element a = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_administrativeArea},con,jmpPrefix);
        a.setText(area);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-都道府県コードを取得
	 */
	public static String getAdministrativeAreaCode(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_address, TAG_administrativeAreaCode},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-都道府県コードを設定
	 */
	public static void setAdministrativeAreaCode(String code,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element pos = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_administrativeAreaCode},con,jmpPrefix);
        pos.setText(code);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-郵便番号を取得
	 */
	public static String getPostalCode(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_address,TAG_postalCode},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-郵便番号を設定
	 */
	public static void setPostalCode(String code,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element pos = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_postalCode},con,jmpPrefix);
        pos.setText(code);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-国を取得
	 */
	public static String getCountry(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_address,TAG_country},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-国を設定
	 */
	public static void setCountry(String country,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element c = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address,TAG_country},con,jmpPrefix);
        c.setText(country);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-電子メールアドレスを取得
	 */
	public static String[] getEMailAddress(Element xmlElement){
        return getXMLValues(new String[]{TAG_contact,TAG_contactInfo,TAG_address,TAG_electronicMailAddress},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-住所-電子メールアドレスを設定
	 */
	public static void setEMailAddress(String[] email,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element addr = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_address},con,jmpPrefix);
        removeXMLElements(TAG_electronicMailAddress,addr);
        for(String em : email){
            Element e = createXMLElement(TAG_electronicMailAddress,addr);
            e.setText(em);
        }
	}

	/**
	 * 問合わせ先-問合せ情報-オンライン情報資源-リンクを取得
	 */
	public static String getLinkage(Element xmlElement){
        return getXMLValue(new String[]{TAG_contact,TAG_contactInfo,TAG_onlineResource,TAG_linkage},xmlElement,jmpPrefix);
	}

	/**
	 * 問合わせ先-問合せ情報-オンライン情報資源-リンクを設定
	 */
	public static void setLinkage(String link,Element xmlElement){
        Element con = getOrCreatePointOfContact(xmlElement);
        Element lk = getOrCreateXMLElement(new String[]{TAG_contactInfo,TAG_onlineResource,TAG_linkage},con,jmpPrefix);
        lk.setText(link);
	}

	/**
	 * 識別情報-データ識別-概要の図示
	 */
	public static String[][] getBrowseGraphic(Element xmlElement){
        List<Element> bg = getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_graphicOverview,TAG_MDBrowseGraphic},xmlElement,jmpPrefix);
        if(bg != null){
            String[][] ret = new String[bg.size()][];
            int i = 0;
            for(Element b : bg){
                String fn = getXMLValue(new String[]{TAG_fileName},b,jmpPrefix);
                String fd = getXMLValue(new String[]{TAG_fileDescription},b,jmpPrefix);
                String ft = getXMLValue(new String[]{TAG_fileType},b,jmpPrefix);
                ret[i] = new String[]{fn,fd,ft};
            }
            return ret;
        }
        return null;
	}

	/**
	 * 識別情報-データ識別-概要の図示を設定
	 */
	public static void setBrowseGraphic(String filename,String filedesc,String filetype,Element xmlElement){
        Element go =  getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_graphicOverview},xmlElement,jmpPrefix);
        removeXMLElements(TAG_MDBrowseGraphic,go);
        Element bg = createXMLElement(TAG_MDBrowseGraphic,go);
        Element fn = createXMLElement(TAG_fileName,bg);
        fn.setText(filename);
        if(filedesc != null){
            Element fd = createXMLElement(TAG_fileDescription,bg);
            fd.setText(filedesc);
        }
        if(filetype != null){
            Element ft = createXMLElement(TAG_fileType,bg);
            ft.setText(filetype);
        }
	}

	/**
	 * 識別情報-データ識別-範囲-地理要素
	 */
	public static List<Element> getGeographicElement(Element xmlElement){
        return getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_extent,TAG_geographicElement},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-範囲-地理要素-地理境界ボックス
	 */
	public static String[][] getGeographicBoundingBox(Element xmlElement){
        List<Element> bbox = getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_extent,TAG_geographicElement,TAG_EXGeographicBoundingBox},xmlElement,jmpPrefix);
        if(bbox != null){
            String[][] ret = new String[bbox.size()][];
            int i = 0;
            for(Element b : bbox){
                String c = getXMLValue(new String[]{TAG_extentReferenceSystem,TAG_code},b,jmpPrefix);
                String west = getXMLValue(new String[]{TAG_westBoundLongitude},b,jmpPrefix);
                String east = getXMLValue(new String[]{TAG_eastBoundLongitude},b,jmpPrefix);
                String south = getXMLValue(new String[]{TAG_southBoundLatitude},b,jmpPrefix);
                String north = getXMLValue(new String[]{TAG_northBoundLatitude},b,jmpPrefix);
                ret[i] = new String[]{c,west,east,south,north};
            }
            return ret;
        }
        return null;
	}

	/**
	 * 識別情報-データ識別-範囲-地理要素-地理境界ボックスを設定
	 */
	public static void setGeographicBoundingBox(String code,double w,double e,double s,double n,Element xmlElement){
        // FIXME １つしか設定できない
        Element bbox = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_extent,TAG_geographicElement,TAG_EXGeographicBoundingBox},xmlElement,jmpPrefix);
        if(code != null){
            Element c = getOrCreateXMLElement(new String[]{TAG_extentReferenceSystem,TAG_code},bbox,jmpPrefix);
            c.setText(code);
        }
        Element west = getOrCreateXMLElement(TAG_westBoundLongitude,bbox,jmpPrefix);
        west.setText(String.valueOf(w));
        Element east = getOrCreateXMLElement(TAG_eastBoundLongitude,bbox,jmpPrefix);
        east.setText(String.valueOf(e));
        Element south = getOrCreateXMLElement(TAG_southBoundLatitude,bbox,jmpPrefix);
        south.setText(String.valueOf(s));
        Element north = getOrCreateXMLElement(TAG_northBoundLatitude,bbox,jmpPrefix);
        north.setText(String.valueOf(n));
	}

	/**
	 * 識別情報-データ識別-範囲-時間要素
	 */
	public static List<Element> getTemporalElement(Element xmlElement){
        return getXMLElements(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_extent,TAG_temporalElement},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-範囲-時間要素を設定
	 */
	public static void setTemporalElement(String b,String e,Element xmlElement){
        // FIXME １つしか設定できない
        Element temp = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_extent,TAG_temporalElement},xmlElement,jmpPrefix);
        removeXMLElements(TAG_EXTemporalExtent,temp);
        Element be = getOrCreateXMLElement(new String[]{TAG_EXTemporalExtent,TAG_extent,TAG_beginEnd},temp,jmpPrefix);
        Element begin = getOrCreateXMLElement(TAG_begin,be,jmpPrefix);
        begin.setText(b);
        Element end = getOrCreateXMLElement(TAG_end,be,jmpPrefix);
        end.setText(e);
	}

	/**
	 * 識別情報-データ識別-範囲-時間要素を設定
	 */
	public static void setTemporalElement(String instant,Element xmlElement){
        // FIXME １つしか設定できない
        Element temp = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_extent,TAG_temporalElement},xmlElement,jmpPrefix);
        removeXMLElements(TAG_EXTemporalExtent,temp);
        Element inst = getOrCreateXMLElement(new String[]{TAG_EXTemporalExtent,TAG_extent,TAG_instant},temp,jmpPrefix);
        inst.setText(instant);
	}

	/**
	 * 識別情報-データ識別-空間表現型を取得
	 */
	public static String[] getSpatialRepresentationType(Element xmlElement){
        return getXMLValues(new String[]{TAG_identificationInfo,TAG_MDDataIdentification,TAG_spatialRepresentationType},xmlElement,jmpPrefix);
	}

	/**
	 * 識別情報-データ識別-空間表現型を設定
	 */
	public static void setSpatialRepresentationType(String[] type,Element xmlElement){
        Element mdid = getOrCreateXMLElement(new String[]{TAG_identificationInfo,TAG_MDDataIdentification},xmlElement,jmpPrefix);
        removeXMLElements(TAG_spatialRepresentationType,mdid);
        for(String t : type){
            Element e = createXMLElement(TAG_spatialRepresentationType,mdid);
            e.setText(t);
        }
	}

	/**
	 * 参照系情報-参照系識別子-符号を取得
	 */
	public static String[] getReferenceSystemIdentifierCode(Element xmlElement){
        return getXMLValues(new String[]{TAG_referenceSystemInfo,TAG_MDReferenceSystem,TAG_referenceSystemIdentifier/*,TAG_RSIdentifier*/,TAG_code},xmlElement,jmpPrefix);
	}

	/**
	 * 参照系情報-参照系識別子-符号を設定
	 */
	public static void setReferenceSystemIdentifierCode(String[] code,Element xmlElement){
        removeXMLElements(TAG_referenceSystemInfo,xmlElement);
        if(code != null){
            for(String c1 : code){
                Element rsinfo = createXMLElement(TAG_referenceSystemInfo,xmlElement);
                Element c = createXMLElement(new String[]{TAG_MDReferenceSystem,TAG_referenceSystemIdentifier/*,TAG_RSIdentifier*/,TAG_code},rsinfo);
                c.setText(c1);
            }
        }
	}

	/**
	 * 配布情報-配布書式
	 * 配布情報-交換任意選択-オンライン
	 * WMS などデータアクセスのURLを取得するためのメソッド
	 */
	public static String[][] getDataAccessURL(Element xmlElement){
        // FIXME transferOptions,MD_DigitalTransferOptions それぞれ複数ある作り
        // 既存のデータで複数あるものを調べる
        List<Element> fmt = getXMLElements(new String[]{TAG_distributionInfo,TAG_MDDistribution,TAG_distributionFormat},xmlElement,jmpPrefix);
        List<Element> opt = getXMLElements(new String[]{TAG_distributionInfo,TAG_MDDistribution,TAG_transferOptions},xmlElement,jmpPrefix);
        if(opt != null){
			Vector<String[]> url = new Vector<String[]>();
            for(int i = 0;i < opt.size();i++){
                List<Element> onLine = getXMLElements(new String[]{TAG_MDDigitalTransferOptions,TAG_onLine},opt.get(i),jmpPrefix);
                for(int j = 0;onLine != null && j < onLine.size();j++){
                    url.add(new String[]{
                            fmt!=null&&i<fmt.size()?getXMLValue(new String[]{TAG_MDFormat,TAG_name},fmt.get(i),jmpPrefix):null,
                            fmt!=null&&i<fmt.size()?getXMLValue(new String[]{TAG_MDFormat,TAG_version},fmt.get(i),jmpPrefix):null,
                            getXMLValue(new String[]{TAG_linkage},onLine.get(j),jmpPrefix),
                            getXMLValue(new String[]{TAG_protocol},onLine.get(j),jmpPrefix),
                            getXMLValue(new String[]{TAG_applicationProfile},onLine.get(j),jmpPrefix),
                            getXMLValue(new String[]{TAG_name},onLine.get(j),jmpPrefix),
                            getXMLValue(new String[]{TAG_description},onLine.get(j),jmpPrefix),
                            getXMLValue(new String[]{TAG_function},onLine.get(j),jmpPrefix)
						});
                }
            }
            if(url.size() > 0)
                return url.toArray(new String[url.size()][]);
        }
        return null;
	}

	/**
	 * 配布情報-配布書式
	 * 配布情報-交換任意選択-オンライン
	 * WMS などデータアクセスのURLを追加するためのメソッド
	 */
	public static void addDataAccessURL(String name,String ver,String link,String protocol,String appprofile,String onlinename,String desc,String func,Element xmlElement){
        if(link == null)
            return;
        Element dist = getOrCreateXMLElement(new String[]{TAG_distributionInfo,TAG_MDDistribution},xmlElement,jmpPrefix);
        if(name != null || ver != null){
            Element fmt = createXMLElement(new String[]{TAG_distributionFormat,TAG_MDFormat},dist);
            Element nm = createXMLElement(TAG_name,fmt);
            nm.setText(name!=null?name:"");
            Element v = createXMLElement(TAG_version,fmt);
            v.setText(ver!=null?ver:"");
        }
        Element onl = createXMLElement(new String[]{TAG_transferOptions,TAG_MDDigitalTransferOptions,TAG_onLine},dist);
        Element ln = createXMLElement(TAG_linkage,onl);
        ln.setText(link);
        if(protocol != null){
            Element p = createXMLElement(TAG_protocol,onl);
            p.setText(protocol);
        }
        if(appprofile != null){
            Element a = createXMLElement(TAG_applicationProfile,onl);
            a.setText(appprofile);
        }
        if(onlinename != null){
            Element n = createXMLElement(TAG_name,onl);
            n.setText(onlinename);
        }
        if(desc != null){
            Element d = createXMLElement(TAG_description,onl);
            d.setText(desc);
        }
        if(func != null){
            Element f = createXMLElement(TAG_function,onl);
            f.setText(func);
        }
	}

	/**
	 * 配布情報-配布書式
	 * 配布情報-交換任意選択-オンライン
	 * WMS などデータアクセスのURLを追加するためのメソッド
	 */
	public static void setDataAccessURL(String[][] distinfo,Element xmlElement){
        Element dist = getOrCreateXMLElement(new String[]{TAG_distributionInfo,TAG_MDDistribution},xmlElement,jmpPrefix);
        removeXMLElements(TAG_distributionFormat,dist);
        removeXMLElements(TAG_transferOptions,dist);
        for(int i = 0;i < distinfo.length;i++){
            addDataAccessURL(distinfo[i][0],distinfo[i][1],distinfo[i][2],distinfo[i][3],distinfo[i][4],distinfo[i][5],distinfo[i][6],distinfo[i][7],xmlElement);
        }
	}

    /**
	 * 配布情報-交換任意選択-オンラインから WMS,WFS,WCS,XYZ,TMS の情報を取得
	 * @param type 応用プロファイルを指定する。(WMS,WFS,WCS,XYZ,TMSなど)
	 * @param xmlElement メタデータXMLエレメント
	 * @return 指定した応用プロファイルの情報があれば、配列で返す.複数ある場合は先頭のみ。なければ null
	 *         [0] wmsurl    リンク
	 *         [1] optparam  応用プロファイル後ろにコロンで区切って付与したリクエストパラメータ形式（オプション）
	 *         [2] layers    optparam に layers というパラメータがある場合に取得できる
	 *         [3] opts      optparam がリクエストパラメータ形式でない場合に取得できる
	 *         [4] desc2     記述(XYZ, TMSは、JSON形式のオプションを取得できる)
     */
    static String[] getServiceInfo(String type,Element xmlElement){
        String type2 = type+":";
		String[][] wmsinfo = getDataAccessURL(xmlElement);
		if(wmsinfo != null){
			String wmsurl = null;
			String optparam = null;
			String layers = null;
			String opts = null;
			String desc2 = null;
			for(int j = 0;j < wmsinfo.length;j++){
				String fmtname = wmsinfo[j][0];
				String fmtver = wmsinfo[j][1];
				String link = wmsinfo[j][2];
				String proto = wmsinfo[j][3];
				String appprof = wmsinfo[j][4];
				String name = wmsinfo[j][5];
				String desc = wmsinfo[j][6];
				String func = wmsinfo[j][7];
				if(appprof != null && (appprof.equals(type) || appprof.startsWith(type2))){
					wmsurl = link;
					desc2 = desc;
					if(appprof.startsWith(type2)){
						String param = appprof.substring(type2.length());
						String[] p = param.split("&");
						StringBuffer pbuff = new StringBuffer();
						for(int k = 0;p != null && k < p.length;k++){
							int ch = p[k].indexOf("=");
							if(ch != -1){
								String pname = p[k].substring(0,ch);
								String pval = p[k].substring(ch+1);
								if(pname.equals("LAYERS")){
									if(pval.length() > 0)
										layers = pval;
								}else{
									if(pbuff.length() > 0)
										pbuff.append("&");
									else
										opts = param;
									pbuff.append(p[k]);
								}
							}
						}
						if(pbuff.length() > 0)
							optparam = pbuff.toString();
					}
					break;
				}
			}
			//return new String[]{wmsurl,optparam,layers};
			return new String[]{wmsurl,optparam,layers,opts,desc2};
		}
		return null;
    }

	/**
	 * WMS サーバ情報取得
	 * 配布情報からWMS サーバの情報を取得する
	 */
	public static String[] getWMSInfo(Element xmlElement){
        return getServiceInfo("WMS",xmlElement);
	}

	/**
	 * WFS サーバ情報取得
	 * 配布情報からWFS サーバの情報を取得する
	 */
	public static String[] getWFSInfo(Element xmlElement){
        return getServiceInfo("WFS",xmlElement);
	}

	/**
	 * WCS サーバ情報取得
	 * 配布情報からWCS サーバの情報を取得する
	 */
	public static String[] getWCSInfo(Element xmlElement){
        return getServiceInfo("WCS",xmlElement);
	}

    //////////////// JMP2.0アクセス(end) ////////////////

    /**
     * ExceptionReport から ExceptionText を取得
     */
    public static String getExceptionText(Element ext){
        return getXMLElement(new String[]{"Exception", "ExceptionText"},ext,"ows").getTextNormalize();
    }
}
