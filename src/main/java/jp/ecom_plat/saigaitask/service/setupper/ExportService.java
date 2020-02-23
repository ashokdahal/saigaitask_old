/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.setupper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.util.ClassLoaderUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.ResourcesUtil;
import org.seasar.framework.util.ResourcesUtil.Resources;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.admin.MapBackup;
import jp.ecom_plat.map.db.ListSearchConditionInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.*;
import jp.ecom_plat.saigaitask.entity.names.AlarmdefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AuthorizationInfoNames;
import jp.ecom_plat.saigaitask.entity.names.CkanauthInfoNames;
import jp.ecom_plat.saigaitask.entity.names.EarthquakegrouplayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.EcomgwpostdefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternalmapdataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternaltabledataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FacebookpostdefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.HistorycolumnlistInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ImporttrackInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ImporttracktableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgealarmInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgenoticeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultDataNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultstyleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapbaselayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerattrInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapreferencelayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuloginInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenumapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteolayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotriggerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupaddressInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupuserInfoNames;
import jp.ecom_plat.saigaitask.entity.names.OauthtokenDataNames;
import jp.ecom_plat.saigaitask.entity.names.ObservlistInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PagemenubuttonInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ReportDataNames;
import jp.ecom_plat.saigaitask.entity.names.Reportcontent2DataNames;
import jp.ecom_plat.saigaitask.entity.names.ReportcontentDataNames;
import jp.ecom_plat.saigaitask.entity.names.StationlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SummarylistcolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablecalculateInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablecalculatecolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablelistcolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablerowstyleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterfileDataNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterofficeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterpointInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterserverInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TimelinetableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackgroupDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackmapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UnitInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UserInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.disconnect.TrackDataExportService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SpringContext;
import jp.ecom_plat.saigaitask.util.TableSheetReader;
import jp.ecom_plat.saigaitask.util.TableSheetWriter;

/**
 * 自治体設定をエクスポートするサービスクラス
 */
@org.springframework.stereotype.Service
public class ExportService extends BaseService {

	/** 危機管理クラウドバージョンファイルのプレフィックス */
	public static final String PREFIX_VERSION   = "saigaitask-version";
	/** システムマスタバックアップファイルのプレフィックス */
	public static final String PREFIX_MASTER = "saigaitask-master";
	/** 自治体設定バックアップファイルのプレフィックス */
	public static final String PREFIX_INFO   = "saigaitask-info";
	/** エクセル帳票テンプレートファイルZIPアーカイブファイルのプレフィックス */
	public static final String PREFIX_EXCELTEMPLATEZIP = "saigaitask-exceltemplate";

	// Logger
	Logger logger = Logger.getLogger(ExportService.class);

	// Service
	@Resource protected MapmasterInfoService mapmasterInfoService;

	/**
	 * エンティティ種別と、エンティティクラスリストのMap
	 */
	public static Map<EntityType, List<Class<?>>> entitys;

	/**
	 * 設定エンティティを自治体IDで join するルール
	 * Map<エンティティ, 内部結合する名前クラス>
	 */
	public static Map<Class<?>, Field[]> entitysLocalgovinfoidJoinRules;

	static {
		initEntitys();
		initEntitysLocalgovinfoidJoinRules();
	}

	/** ログイン情報 */
	@Resource protected LoginDataDto loginDataDto;

	/**
	 * エンティティ種別
	 */
	public static enum EntityType {
		/**
		 * システムマスタ
		 * 自治体エクスポートではシステムマスタとしてエクスポートされる。
		 */
		master,

		/**
		 * 自治体グループ設定などの自治体の上にある設定
		 * 自治体エクスポートではエクスポートされない。
		 */
		globalinfo,

		/**
		 * 自治体設定, 定型文
		 * 自治体エクスポートでは自治体設定としてエクスポートされる。
		 */
		info,

		/**
		 * 災害データ
		 * 通信途絶データとしてエクスポートされる。
		 */
		data;

		/**
		 * エンティティ種別から多言語対応された名称を取得する。
		 * @return 多言語対応された名称
		 */
		public String getName(SaigaiTaskDBLang lang) {
			String name = this.toString();
			switch(this) {
			case master: name = lang.__("System Master"); break;
			case globalinfo: name = lang.__("グローバル設定"); break;
			case info: name = lang.__("Municipality setting"); break;
			case data: name = lang.__("Disaster data"); break;
			}
			return name;
		}
	}

	/**
	 * entity.dbパッケージにあるクラスをロードして EntityType で振り分ける.
	 */
	public static void initEntitys() {

		final Logger logger = Logger.getLogger(ExportService.class);
		logger.trace("==================== start of entity type loading =====================");

		// リストの初期化
		entitys = new LinkedHashMap<ExportService.EntityType, List<Class<?>>>();

		// パッケージ以下のリソースを取得
		String entityPackage = "jp.ecom_plat.saigaitask.entity.db";
		Resources[] resourcesTypes = ResourcesUtil.getResourcesTypes(entityPackage);
		if(resourcesTypes.length==0) {
			logger.fatal("entity not found: "+entityPackage);
		}
		for(Resources resources : resourcesTypes) {
			try{
				resources.forEach(new ClassHandler() {
					@Override
					public void processClass(String packageName, String shortClassName) {
						String className = ClassUtil.concatName(packageName, shortClassName);
						Class<?> clazz = ClassLoaderUtil.loadClass(ResourceUtil.getClassLoader(), className);
						// エンティティクラスかチェック
						if(clazz.getEnclosingClass()!=null) {
							logger.trace(clazz.getName()+" is inner class.");
						}
						else {
							EntityType entityType = getEntityType(clazz);
							if(entityType==null) {
								logger.error("entity type [Unknown]"+className);
							}
							else {
								logger.trace("entity type ["+entityType+"]"+className);
								// get classes of entityType
								List<Class<?>> classes = entitys.get(entityType);
								if(classes==null) {
									classes = new ArrayList<Class<?>>();
									entitys.put(entityType, classes);
								}
								// add to entityType classes
								classes.add(clazz);
							}
						}
					}
				});
			} finally {
				resources.close();
			}
		}

		logger.trace("==================== end of entity type loading =====================");
	}

	/**
	 * クラスから EntityType を取得する.
	 * @param clazz エンティティのクラス
	 * @return EntityType
	 */
	public static EntityType getEntityType(Class<?> clazz) {

		// Entity アノテーションのチェック
		Entity entity = clazz.getAnnotation(Entity.class);
		if(entity==null || StringUtil.isNotEmpty(entity.name())) {
			return null;
		}

		// 精査するために、クラス指定で タイプを取得
		if(clazz==AdminbackupData.class) return EntityType.data;

		// 管理画面メニューは master
		if(clazz==AdminmenuInfo.class) return EntityType.master;

		if(clazz==AlarmdefaultgroupInfo.class) return EntityType.info;
		if(clazz==AlarmmessageData.class) return EntityType.data;
		if(clazz==AlarmmessageInfo.class) return EntityType.info;
		if(clazz==AlarmshowData.class) return EntityType.data;
		if(clazz==AlarmtypeMaster.class) return EntityType.master;
		if(clazz==AssembleInfo.class) return EntityType.info;
		if(clazz==AssemblestateData.class) return EntityType.data;
		if(clazz==AutocompleteData.class) return EntityType.data;
		if(clazz==AutocompleteInfo.class) return EntityType.info;
		if(clazz==ClearinghousemetadatadefaultInfo.class) return EntityType.info;
		if(clazz==ClearinghousemetadataInfo.class) return EntityType.info;
		if(clazz==ClearinghousesearchInfo.class) return EntityType.info;
		if(clazz==DemoInfo.class) return EntityType.info;
		if(clazz==DisasterbuildData.class) return EntityType.data;
		if(clazz==DisastercasualtiesData.class) return EntityType.data;
		if(clazz==DisasterfarmData.class) return EntityType.data;
		if(clazz==DisasterfireData.class) return EntityType.data;
		if(clazz==DisasterhospitalData.class) return EntityType.data;
		if(clazz==DisasterhouseData.class) return EntityType.data;
		if(clazz==DisasterhouseholdData.class) return EntityType.data;
		if(clazz==DisasterhouseregidentData.class) return EntityType.data;
		if(clazz==DisasteritemInfo.class) return EntityType.info;
		if(clazz==DisasterlifelineData.class) return EntityType.data;
		if(clazz==DisasterMaster.class) return EntityType.master;
		if(clazz==DisasterroadData.class) return EntityType.data;
		if(clazz==DisasterschoolData.class) return EntityType.data;
		if(clazz==DisastersituationhistoryData.class) return EntityType.data;
		if(clazz==DisastersummaryhistoryData.class) return EntityType.data;
		if(clazz==DisasterwelfareData.class) return EntityType.data;
		if(clazz==EarthquakegrouplayerData.class) return EntityType.data;
		if(clazz==EarthquakelayerData.class) return EntityType.data;
		if(clazz==EcomgwpostdefaultInfo.class) return EntityType.info;
		if(clazz==EcomgwpostInfo.class) return EntityType.info;
		if(clazz==ExternalmapdataInfo.class) return EntityType.info;
		if(clazz==ExternaltabledataInfo.class) return EntityType.info;
		if(clazz==FacebookInfo.class) return EntityType.info;
		if(clazz==FacebookMaster.class) return EntityType.master;
		if(clazz==FacebookpostdefaultInfo.class) return EntityType.info;
		if(clazz==FacebookpostInfo.class) return EntityType.info;
		if(clazz==GeneralizationhistoryData.class) return EntityType.data;
		if(clazz==GroupInfo.class) return EntityType.info;
		if(clazz==HeadofficeData.class) return EntityType.data;

		// 履歴テーブルとカラム情報は data
		if(clazz==HistorycolumnlistInfo.class) return EntityType.data;
		if(clazz==HistorytableInfo.class) return EntityType.data;

		// 通信途絶データ
		if(clazz==ImporttrackInfo.class) return EntityType.data;
		if(clazz==ImporttracktableInfo.class) return EntityType.data;

		if(clazz==IssuelayerInfo.class) return EntityType.info;
		if(clazz==JudgealarmInfo.class) return EntityType.info;
		if(clazz==JudgeformulaMaster.class) return EntityType.master;
		if(clazz==JudgeInfo.class) return EntityType.info;
		if(clazz==JudgemanInfo.class) return EntityType.info;
		if(clazz==JudgenoticeInfo.class) return EntityType.info;
		if(clazz==JudgeresultData.class) return EntityType.data;
		if(clazz==JudgeresultstyleData.class) return EntityType.data;
		if(clazz==JudgeresultstyleInfo.class) return EntityType.info;
		if(clazz==LocalgovInfo.class) return EntityType.info;

		// 自治体グループに関する設定は自治体エクスポート対象外
		if(clazz==LocalgovgroupInfo.class) return EntityType.globalinfo;
		if(clazz==LocalgovgroupmemberInfo.class) return EntityType.globalinfo;

		if(clazz==LocalgovtypeMaster.class) return EntityType.master;
		if(clazz==LoginData.class) return EntityType.data;
		if(clazz==MapbaselayerInfo.class) return EntityType.info;
		if(clazz==MaplayerattrInfo.class) return EntityType.info;
		if(clazz==MaplayerInfo.class) return EntityType.info;
		if(clazz==MapmasterInfo.class) return EntityType.info;
		if(clazz==MapreferencelayerInfo.class) return EntityType.info;
		if(clazz==MenuInfo.class) return EntityType.info;
		if(clazz==MenuloginInfo.class) return EntityType.info;
		if(clazz==MenumapInfo.class) return EntityType.info;
		if(clazz==MenuprocessInfo.class) return EntityType.info;
		if(clazz==MenutableInfo.class) return EntityType.info;
		if(clazz==MenutaskInfo.class) return EntityType.info;
		if(clazz==MenutaskmenuInfo.class) return EntityType.info;
		if(clazz==MenutasktypeInfo.class) return EntityType.info;
		if(clazz==MenutypeMaster.class) return EntityType.master;
		if(clazz==MeteoareainformationcityMaster.class) return EntityType.master;
		if(clazz==MeteoData.class) return EntityType.data;
		//if(clazz==MeteolandslideareaMaster.class) return EntityType.master;
		if(clazz==MeteolayerInfo.class) return EntityType.info;
		if(clazz==MeteorainareaMaster.class) return EntityType.master;
		if(clazz==MeteorequestInfo.class) return EntityType.info;
		if(clazz==MeteoriverareaMaster.class) return EntityType.master;
		if(clazz==MeteoriverMaster.class) return EntityType.master;
		if(clazz==Meteoseismicarea1Master.class) return EntityType.master;
		//if(clazz==MeteotatsumakiareaMaster.class) return EntityType.master;
		if(clazz==MeteotriggerData.class) return EntityType.data;
		if(clazz==MeteotriggerInfo.class) return EntityType.info;
		if(clazz==MeteotsunamiareaMaster.class) return EntityType.master;
		if(clazz==MeteotypeMaster.class) return EntityType.master;
		if(clazz==MeteovolcanoMaster.class) return EntityType.master;
		//if(clazz==MeteowarnareaMaster.class) return EntityType.master;
		if(clazz==MeteoxsltInfo.class) return EntityType.info;

		// 多言語用の_info で終わらないものはマスタとする
		if(clazz==MultilangInfo.class) return EntityType.master; // master?
		if(clazz==MultilangmesInfo.class) return EntityType.master; // master?

		if(clazz==NoticeaddressInfo.class) return EntityType.info;
		if(clazz==NoticedefaultgroupInfo.class) return EntityType.info;
		if(clazz==NoticedefaultInfo.class) return EntityType.info;
		if(clazz==NoticegroupaddressInfo.class) return EntityType.info;
		if(clazz==NoticegroupInfo.class) return EntityType.info;
		if(clazz==NoticegroupuserInfo.class) return EntityType.info;
		if(clazz==NoticemailData.class) return EntityType.data;
		if(clazz==NoticemailsendData.class) return EntityType.data;
		if(clazz==NoticeTemplate.class) return EntityType.info;
		if(clazz==NoticetemplatetypeMaster.class) return EntityType.master;
		if(clazz==NoticetypeMaster.class) return EntityType.master;
		if(clazz==ObservatorydamInfo.class) return EntityType.info;
		if(clazz==ObservatorydamlayerInfo.class) return EntityType.info;
		if(clazz==ObservatoryrainInfo.class) return EntityType.info;
		if(clazz==ObservatoryrainlayerInfo.class) return EntityType.info;
		if(clazz==ObservatoryriverInfo.class) return EntityType.info;
		if(clazz==ObservatoryriverlayerInfo.class) return EntityType.info;
		if(clazz==ObservlistInfo.class) return EntityType.info;
		if(clazz==ObservMaster.class) return EntityType.master;
		if(clazz==ObservmenuInfo.class) return EntityType.info;
		if(clazz==PagebuttonMaster.class) return EntityType.master;
		if(clazz==PagemenubuttonInfo.class) return EntityType.info;
		if(clazz==PubliccommonsReportData.class) return EntityType.data;

		// _data で終わらないエンティティ
		if(clazz==PubliccommonsReportDataLastAntidisaster.class) return EntityType.data;
		if(clazz==PubliccommonsReportDataLastDamage.class) return EntityType.data;
		if(clazz==PubliccommonsReportDataLastEvent.class) return EntityType.data;
		if(clazz==PubliccommonsReportDataLastGeneral.class) return EntityType.data;
		if(clazz==PubliccommonsReportDataLastRefuge.class) return EntityType.data;
		if(clazz==PubliccommonsReportDataLastShelter.class) return EntityType.data;

		if(clazz==PubliccommonsReportRefugeInfo.class) return EntityType.info;
		if(clazz==PubliccommonsReportShelterInfo.class) return EntityType.info;
		if(clazz==PubliccommonsSendHistoryData.class) return EntityType.data;
		if(clazz==PubliccommonsSendToInfo.class) return EntityType.info;
		if(clazz==Reportcontent2Data.class) return EntityType.data;
		if(clazz==ReportcontentData.class) return EntityType.data;
		if(clazz==ReportData.class) return EntityType.data;
		if(clazz==SafetystateMaster.class) return EntityType.master;
		if(clazz==StationalarmInfo.class) return EntityType.info;
		if(clazz==StationclassInfo.class) return EntityType.info;
		if(clazz==StationlayerInfo.class) return EntityType.info;
		if(clazz==StationMaster.class) return EntityType.master;
		//if(clazz==StationorderData.class) return EntityType.data;
		//if(clazz==StationorderHistory.class) return EntityType.data;
		if(clazz==SummarylistcolumnInfo.class) return EntityType.info;
		if(clazz==TablecalculatecolumnInfo.class) return EntityType.info;
		if(clazz==TablecalculateInfo.class) return EntityType.info;
		if(clazz==TablecolumnMaster.class) return EntityType.master;
		//if(clazz==TablecolumnsortInfo.class) return EntityType.info;
		if(clazz==TablelistcolumnInfo.class) return EntityType.info;
		if(clazz==TablemasterInfo.class) return EntityType.info;

		// テーブルリセット対象属性情報 は _data だが、 _info でエクスポート対象とする
		if(clazz==TableresetcolumnData.class) return EntityType.info;

		if(clazz==TablerowstyleInfo.class) return EntityType.info;
		if(clazz==TelemeterData.class) return EntityType.data;
		if(clazz==TelemeterfileData.class) return EntityType.data;
		if(clazz==TelemeterofficeInfo.class) return EntityType.info;
		if(clazz==TelemeterpointInfo.class) return EntityType.info;
		if(clazz==TelemeterserverInfo.class) return EntityType.info;
		if(clazz==TelemetertimeData.class) return EntityType.data;
		if(clazz==TelopData.class) return EntityType.data;
		if(clazz==TeloptypeMaster.class) return EntityType.master;
		if(clazz==ThreadData.class) return EntityType.data;
		if(clazz==ThreadresponseData.class) return EntityType.data;
		if(clazz==ThreadsendtoData.class) return EntityType.data;
		if(clazz==TimelinemenueMaster.class) return EntityType.master;
		if(clazz==TimelinetableInfo.class) return EntityType.info;

		// 管理画面＞自治体＞その他＞ツールボックス情報
		if(clazz==ToolboxData.class) return EntityType.info;

		if(clazz==TrackData.class) return EntityType.data;
		if(clazz==TrackgroupData.class) return EntityType.data;

		// 災害マップ情報、災害レイヤ情報は data
		if(clazz==TrackmapInfo.class) return EntityType.data;
		if(clazz==TracktableInfo.class) return EntityType.data;

		if(clazz==TwitterInfo.class) return EntityType.info;
		if(clazz==TwitterMaster.class) return EntityType.master;
		if(clazz==UnitInfo.class) return EntityType.info;
		if(clazz==UserInfo.class) return EntityType.info;

		// 末尾の一致で判定
		if(clazz.getName().toLowerCase().endsWith("master")) return EntityType.master;
		if(clazz.getName().toLowerCase().endsWith("info")) return EntityType.info;
		if(clazz.getName().toLowerCase().endsWith("data")) return EntityType.data;

		return null;
	}

	/**
	 * 認証情報かどうか
	 * @param clazz エンティティクラス
	 * @return boolean
	 */
	public static boolean isAuthTable(Class<?> clazz) {
		// master
		if(clazz==TwitterMaster.class) return true;
		if(clazz==FacebookMaster.class) return true;

		// info
		if(clazz==TwitterInfo.class) return true;
		if(clazz==FacebookInfo.class) return true;
		if(clazz==FacebookpostInfo.class) return true;
		if(clazz==EcomgwpostInfo.class) return true;
		if(clazz==PubliccommonsSendToInfo.class) return true;
		if(clazz==JalertserverInfo.class) return true;
		if(clazz==DatatransferInfo.class) return true;
		return false;
	}

	/**
	 * 認証情報カラムかどうか
	 * @param clazz エンティティクラス
	 * @param column フィールド名
	 * @return boolean
	 */
	public static boolean isAuthColumn(Class<?> clazz, String column) {
		// info
		if(clazz==GroupInfo.class) {
			if(GroupInfoNames.password().toString().equals(column)) return true;
			if(GroupInfoNames.ecomuser().toString().equals(column)) return true;
			if(GroupInfoNames.ecompass().toString().equals(column)) return true;
		}
		if(clazz==UnitInfo.class) {
			if(UnitInfoNames.password().toString().equals(column)) return true;
			if(UnitInfoNames.ecomuser().toString().equals(column)) return true;
		}
		if(clazz==UserInfo.class) {
			if(UserInfoNames.pushtoken().toString().equals(column)) return true;
		}
		if(clazz==TelemeterserverInfo.class) {
			if(TelemeterserverInfoNames.userid().toString().equals(column)) return true;
			if(TelemeterserverInfoNames.password().toString().equals(column)) return true;
		}
		if(clazz==AuthorizationInfo.class) {
			if(AuthorizationInfoNames.authtype().toString().equals(column)) return true;
			if(AuthorizationInfoNames.username().toString().equals(column)) return true;
			if(AuthorizationInfoNames.userpass().toString().equals(column)) return true;
			if(AuthorizationInfoNames.authword().toString().equals(column)) return true;
		}
		if(clazz==CkanauthInfo.class) {
			if(CkanauthInfoNames.authkey().toString().equals(column)) return true;
			if(CkanauthInfoNames.trainingauthkey().toString().equals(column)) return true;
		}
		return false;
	}

	/**
	 * エンティティを自治体IDで join するルール を設定する.
	 * @param entity エンティティクラス
	 * @param fieldNames joinするフィールド名
	 */
	public static void putRules(Class<?> entity, String... fieldNames) {
		List<Field> fields = new ArrayList<Field>();
		for(String fieldName : fieldNames) {
			try {
				Field field = entity.getField(fieldName);
				fields.add(field);
			} catch(NoSuchFieldException e) {
				Logger.getLogger(ExportService.class).fatal(MessageFormat.format("Field {1} not found in Entity ({0}).", entity.getSimpleName(), fieldName));
			}
		}
		entitysLocalgovinfoidJoinRules.put(entity, fields.toArray(new Field[fields.size()]));
	}

	/**
	 * エンティティを自治体IDで join するルール を初期化.
	 * Map<エンティティ, 内部結合する名前クラス>
	 */
	public static void initEntitysLocalgovinfoidJoinRules() {
		entitysLocalgovinfoidJoinRules = new HashMap<Class<?>, Field[]>();
		putRules(AlarmdefaultgroupInfo.class, AlarmdefaultgroupInfoNames.groupInfo().toString());
		putRules(EcomgwpostdefaultInfo.class, EcomgwpostdefaultInfoNames.ecomgwpostInfo().toString());
		putRules(ExternalmapdataInfo.class, ExternalmapdataInfoNames.menuInfo().toString());
		putRules(ExternaltabledataInfo.class, ExternaltabledataInfoNames.menuInfo().toString());
		putRules(FacebookpostdefaultInfo.class, FacebookpostdefaultInfoNames.facebookpostInfo().toString());
		putRules(JudgealarmInfo.class, JudgealarmInfoNames.judgemanInfo().toString());
		putRules(JudgeInfo.class, JudgeInfoNames.judgemanInfo().toString());
		putRules(JudgenoticeInfo.class, JudgenoticeInfoNames.judgealarmInfo().toString());
		putRules(JudgeresultstyleInfo.class, JudgeresultstyleInfoNames.judgemanInfo().toString());
		putRules(MapbaselayerInfo.class, MapbaselayerInfoNames.menuInfo().toString());
		putRules(MaplayerattrInfo.class, MaplayerattrInfoNames.maplayerInfo().toString());
		putRules(MaplayerInfo.class, MaplayerInfoNames.menuInfo().toString());
		putRules(MapreferencelayerInfo.class, MapreferencelayerInfoNames.menuInfo().toString());
		putRules(MenuInfo.class, MenuInfoNames.menutasktypeInfo().toString());
		putRules(MenuloginInfo.class, MenuloginInfoNames.groupInfo().toString());
		putRules(MenumapInfo.class, MenumapInfoNames.menuInfo().toString());
		putRules(MenuprocessInfo.class, MenuprocessInfoNames.menuloginInfo().toString());
		putRules(MenutableInfo.class, MenutableInfoNames.menuInfo().toString());
		putRules(MenutaskInfo.class, MenutaskInfoNames.menuprocessInfo().toString());
		putRules(MenutaskmenuInfo.class, MenutaskmenuInfoNames.menutaskInfo().toString());
		putRules(MeteolayerInfo.class, MeteolayerInfoNames.menuInfo().toString());
		putRules(MeteotriggerInfo.class, MeteotriggerInfoNames.meteorequestInfo().toString());
		putRules(NoticedefaultgroupInfo.class, NoticedefaultgroupInfoNames.noticedefaultInfo().toString());
		putRules(NoticedefaultInfo.class, NoticedefaultInfoNames.menuInfo().toString());
		putRules(NoticegroupaddressInfo.class, NoticegroupaddressInfoNames.noticegroupInfo().toString());
		putRules(NoticegroupuserInfo.class, NoticegroupuserInfoNames.noticegroupInfo().toString());
		putRules(ObservlistInfo.class, ObservlistInfoNames.observmenuInfo().toString());
		putRules(ObservmenuInfo.class, ObservmenuInfoNames.menuInfo().toString());
		putRules(PagemenubuttonInfo.class, PagemenubuttonInfoNames.menuInfo().toString());
		putRules(StationlayerInfo.class, StationlayerInfoNames.tablemasterInfo().toString());
		putRules(SummarylistcolumnInfo.class, SummarylistcolumnInfoNames.summarylistInfo().toString());
		putRules(TablecalculatecolumnInfo.class, TablecalculatecolumnInfoNames.tablemasterInfo().toString());
		putRules(TablecalculateInfo.class, TablecalculateInfoNames.tablecalculatecolumnInfo().toString());
		//putRules(TablecolumnsortInfo.class, TablecolumnsortInfoNames.tablemasterInfo().toString());
		putRules(TablelistcolumnInfo.class, TablelistcolumnInfoNames.menutableInfo().toString());
		putRules(TablemasterInfo.class, TablemasterInfoNames.mapmasterInfo().toString());
		putRules(TablerowstyleInfo.class, TablerowstyleInfoNames.tablelistcolumnInfo().toString());
		putRules(TelemeterofficeInfo.class, TelemeterofficeInfoNames.telemeterserverInfo().toString());
		putRules(TelemeterpointInfo.class, TelemeterpointInfoNames.telemeterofficeInfo().toString());
		putRules(TimelinetableInfo.class, TimelinetableInfoNames.tablemasterInfo().toString());
		//putRules(UnitInfo.class, UnitInfoNames.groupInfo().toString());
		putRules(UserInfo.class, UserInfoNames.unitInfo().toString());

		// Data
		putRules(HistorycolumnlistInfo.class, HistorycolumnlistInfoNames.historytableInfo().toString());
		putRules(JudgeresultData.class, JudgeresultDataNames.judgemanInfo().toString());
		putRules(OauthtokenData.class, OauthtokenDataNames.groupInfo().toString());
		putRules(ReportData.class, ReportDataNames.trackData().toString());
		putRules(ReportcontentData.class, ReportcontentDataNames.reportData().toString());
		putRules(Reportcontent2Data.class, Reportcontent2DataNames.reportData().toString());
		putRules(TelemeterfileData.class, TelemeterfileDataNames.telemeterofficeInfo().toString());
		putRules(TrackmapInfo.class, TrackmapInfoNames.trackDatas().toString());
		putRules(TrackgroupData.class, TrackgroupDataNames.cityTrackData().toString()
				, TrackgroupDataNames.prefTrackData().toString());
		putRules(EarthquakegrouplayerData.class, EarthquakegrouplayerDataNames.mapmasterInfo().toString());
		putRules(EarthquakelayerData.class, EarthquakelayerDataNames.earthquakegrouplayerData().toString());
		putRules(ImporttrackInfo.class, ImporttrackInfoNames.trackData().toString());
		putRules(ImporttracktableInfo.class, ImporttracktableInfoNames.importtrackInfo().toString());
	}

	/**
	 * 自治体設定をZIPでエクスポートする
	 * @param localgovinfoid
	 * @param exportAuthInfo
	 * @return ZIPファイル
	 */
	public File zip(Long localgovinfoid, boolean exportAuthInfo) {

		// 自治体IDが指定されている場合、存在するかチェックする
		if(localgovinfoid!=null) {
			LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
			if(localgovInfo==null) throw new ServiceException(lang.__("Local gov. ID = {0} not exist.", localgovinfoid));
		}

		// タイムスタンプからZipファイル名を生成
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String zipFileName = "saigaitask-export";
		String systemVersion = Config.getVersionForView();
		if(systemVersion!=null) zipFileName += "-V"+systemVersion; // システムバージョンが取得できればエクスポートファイル名に追加
		if(localgovinfoid!=null) zipFileName += "-localgovinfoid"+localgovinfoid;
		zipFileName += "-"+timestamp;

		// 作業ディレクトリの作成
		File workingDir = new File(FileUtil.getTmpDir(), zipFileName);
		if(!workingDir.mkdir()) {
			throw new ServiceException(lang.__("Unable to create working directory of export."));
		}

		ExportFileSet exportFileSet = new ExportFileSet();

		// 危機管理クラウドバージョン情報ファイルをエクスポート
		exportFileSet.saigaitaskVersionFile = exportVersionFile(workingDir, localgovinfoid);

		// システムマスタのエクスポート
		exportFileSet.masterExportXsl = exportMaster(workingDir, exportAuthInfo);
		// 自治体情報のエクスポート
		exportFileSet.infoExportXsl = exportInfo(workingDir, localgovinfoid, exportAuthInfo);
		// エクセル帳票テンプレートファイルのエクスポート
		exportFileSet.exceltenplateZip = exportExceltemplateFile(workingDir, localgovinfoid);

		// 地図のエクスポート
		try {
			MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
			if(mapmasterInfo==null) throw new ServiceException(lang.__("Map master info has not been set."));
			if(mapmasterInfo.mapid==null) throw new ServiceException(lang.__("Map ID of map master info has not been set."));
			MapBackup mapBackup = new MapBackup();
			exportFileSet.mapbackupZip = mapBackup.backupMap(mapmasterInfo.mapid);
		} catch (Exception e) {
			throw new ServiceException(lang.__("Unable to export master map."), e);
		}

		return exportFileSet.zip(zipFileName);
	}

	/**
	 * エクスポートZIPの圧縮/解凍クラス
	 */
	public static class ExportFileSet {

		/** バージョン */
		public String version;

		/** 危機管理クラウドバージョン情報格納ファイル */
		public File saigaitaskVersionFile;

		/** システムマスタのエクスポートExcelファイル */
		public File masterExportXsl;
		/** 自治体情報のエクスポートExcelファイル */
		public File infoExportXsl;
		/** マスタマップのバックアップZIPファイル */
		public File mapbackupZip;

		/** エクセル帳票テンプレートファイルのZIPアーカイブファイル */
		public File exceltenplateZip;

		/** エクスポートZIPファイル名 */
		public String zipFileName;

		/**
		 * ZIPファイルに圧縮する
		 * @param zipFileName
		 * @return ZIPファイル
		 */
		public File zip(String zipFileName) {
			File zipFile = FileUtil.zip(zipFileName, masterExportXsl, infoExportXsl, mapbackupZip, saigaitaskVersionFile, exceltenplateZip);
			return zipFile;
		}

		/**
		 * ZIPファイルを解凍する
		 * @param zipFile ZIPファイル
		 * @return ZIPファイル展開結果
		 * @throws IOException
		 */
		public static ExportFileSet unzip(File zipFile) throws IOException {
			ExportFileSet exportFileSet = new ExportFileSet();
			exportFileSet.zipFileName = zipFile.getName();
			List<File> files = FileUtil.unzip(zipFile, "Windows-31J");
			for(File file : files) {
				String fileName = file.getName();
				// システムマスタ
				if(fileName.startsWith(PREFIX_MASTER)) {
					exportFileSet.masterExportXsl = file;
				}
				// 自治体情報
				else if(fileName.startsWith(PREFIX_INFO)) {
					exportFileSet.infoExportXsl = file;
				}
				// マスタマップ
				else if(fileName.startsWith(MapBackup.TMP_PREFIX)) {
					exportFileSet.mapbackupZip = file;
				}
				// マスタマップ
				else if(fileName.startsWith(PREFIX_EXCELTEMPLATEZIP)) {
					exportFileSet.exceltenplateZip = file;
				}
				// バージョンファイル
				else if(fileName.startsWith(PREFIX_VERSION)) {
					exportFileSet.saigaitaskVersionFile = file;
				}
				// エクセル帳票テンプレートファイルZIPアーカイブファイル
				else if(fileName.startsWith(PREFIX_EXCELTEMPLATEZIP)) {
					exportFileSet.exceltenplateZip = file;
				}
			}
			return exportFileSet;
		}

		/**
		 * @return エクスポートファイルのバージョン
		 * @throws IOException
		 */
		public String getVersion() throws IOException {
			if(version==null) {
				BufferedReader br = null;
				// バージョンファイルがある場合
				if(saigaitaskVersionFile!=null) {
					try {
						//ファイルを読み込む
						FileReader fr = new FileReader(saigaitaskVersionFile);
						br = new BufferedReader(fr);

						//読み込んだファイルを１行ずつ処理する
						String line;
						String fileVersion = "";
						while ((line = br.readLine()) != null) {
							fileVersion = line;
						}
						br.close();

						version = fileVersion;
					} finally {
						if(br!=null) br.close();
					}
				}
				// バージョンファイルがない場合
				else {
					if(infoExportXsl!=null) {
						// エクセルを開く
						POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(infoExportXsl));
						HSSFWorkbook wb = new HSSFWorkbook(fs);

						TableSheetReader reader = null;
						ExcelImportInfoService excelImportService = SpringContext.getApplicationContext().getBean(ExcelImportInfoService.class);

						// V1.5チェック
						reader = excelImportService.getTableSheetReader(wb, TablelistkarteInfo.class);
						if(reader!=null) return version="1.5";

						// V1.4チェック
						reader = excelImportService.getTableSheetReader(wb, PostingphotolayerInfo.class);
						if(reader!=null) return version="1.4";

						// V1.3チェック
						reader = excelImportService.getTableSheetReader(wb, LandmarkInfo.class);
						if(reader!=null) return version="1.3";

						// V1.2チェック
						reader = excelImportService.getTableSheetReader(wb, MapkmllayerInfo.class);
						if(reader!=null) return version="1.2";

						// V1.1チェック
						reader = excelImportService.getTableSheetReader(wb, DisastersummaryhistoryData.class);
						if(reader!=null) return version="1.1";
					}
				}
			}
			return version;
		}

		/**
		 * @return エクスポートファイルのバージョンとこのシステムのバージョンが一致するか
		 * @throws IOException
		 */
		public boolean isValidVersion() throws IOException {
			// このシステムのバージョンを取得
			int [] majorVersion = Config.getMajorVersion();
			String version = null;
			if(majorVersion == null){
				version = TrackDataExportService.VERSION_DEV;
			}else{
				version = majorVersion[0] + "." + majorVersion[1];
			}

			// エクスポートファイルのバージョンと同じかチェック
			String fileVersion = getVersion();
			boolean validVersion = false;
			// 自治体セットアッパーのインポートでは、
			// Ver1.X系はバージョンファイルがない。
			// Ver2.0からバージョンファイルをつけるようにした。
			if(fileVersion!=null) {
				validVersion = version.equals(fileVersion);
			}

			return validVersion;
		}
	}

	/**
	 * システムマスタをエクセルファイルにエクスポートする.
	 * @param workingDir 作業ディレクトリ
	 * @param exportAuthInfo 認証情報のエクスポートフラグ
	 * @return エクセルファイル
	 */
	public File exportMaster(File workingDir, boolean exportAuthInfo) {
		try {
			// initialize
			HSSFWorkbook wb = new HSSFWorkbook();
			export(wb, EntityType.info, 0L, exportAuthInfo);
			export(wb, EntityType.master, null, exportAuthInfo);

			// 一時ファイルに保存
			String filename = PREFIX_MASTER;
			File xlsFile = new File(workingDir, filename+".xls");
			logger.debug("temp: "+xlsFile.getAbsolutePath());
			FileOutputStream xlsOut = new FileOutputStream(xlsFile);
			try {
				wb.write(xlsOut);
			} finally {
				xlsOut.close();
			}
			return xlsFile;
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}

	/**
	 * 自治体設定をエクセルファイルにエクスポートする.
	 * @param workingDir 作業ディレクトリ
	 * @param localgovinfoid 自治体ID
	 * @param exportAuthInfo 認証情報のエクスポートフラグ
	 * @return エクセルファイル
	 */
	public File exportInfo(File workingDir, Long localgovinfoid, boolean exportAuthInfo) {
		try {
			// initialize
			HSSFWorkbook wb = new HSSFWorkbook();
			export(wb, EntityType.info, localgovinfoid, exportAuthInfo);

			// 一時ファイルに保存
			String filename = PREFIX_INFO;
			if(localgovinfoid!=null) filename += "-localgovinfoid"+localgovinfoid;
			File xlsFile = new File(workingDir, filename+".xls");
			logger.debug("temp: "+xlsFile.getAbsolutePath());
			FileOutputStream xlsOut = new FileOutputStream(xlsFile);
			try {
				wb.write(xlsOut);
			} finally {
				xlsOut.close();
			}
			return xlsFile;
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}

	/**
	 * バージョンファイルのエクスポート
	 * @param workingDir 作業ディレクトリ
	 * @param localgovinfoid 自治体ID
	 * @return エクスポートファイル
	 */
	public File exportVersionFile(File workingDir, Long localgovinfoid) {
		try {
			// バージョン取得
			int [] majorVersion = Config.getMajorVersion();
			String version = null;
			if(majorVersion == null){
				version = TrackDataExportService.VERSION_DEV;
			}else{
				version = majorVersion[0] + "." + majorVersion[1];
			}

			// 一時ファイルに保存
			String filename = PREFIX_VERSION;
			if(localgovinfoid!=null) filename += "-localgovinfoid"+localgovinfoid;
			File txtFile = new File(workingDir, filename+".txt");
			logger.debug("temp: "+txtFile.getAbsolutePath());

			FileWriter fw = new FileWriter(txtFile, false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			pw.print(version);
			pw.close();
			return txtFile;
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}

	/**
	 * エクセル帳票テンプレートファイルのエクスポート
	 * @param workingDir 作業ディレクトリ
	 * @param localgovinfoid 自治体ID
	 * @return エクスポートファイル
	 */
	public File exportExceltemplateFile(File workingDir, Long localgovinfoid) {
		File zipWorkDirLevel0 = null;
		try {
			// 作業ディレクトリ作成
			String dirname = PREFIX_EXCELTEMPLATEZIP;
			if(localgovinfoid!=null) dirname += "-localgovinfoid"+localgovinfoid;
			String zipFileName = dirname + ".zip";
			String zipFilePath = workingDir.getAbsolutePath() + File.separator + zipFileName;

			zipWorkDirLevel0 = new File(workingDir, dirname);
			if(!zipWorkDirLevel0.exists()){
				zipWorkDirLevel0.mkdirs();
			}

//			File zipWorkDirLevel1 = new File(zipWorkDirLevel0,localgovinfoid.toString());
//			if(!zipWorkDirLevel1.exists()){
//				zipWorkDirLevel1.mkdirs();
//			}

			// アップロードされているエクセル帳票テンプレートファイルを作業ディレクトリにコピーする
			String baseDirPath = application.getRealPath(Constants.EXCELLIST_BASEDIR + localgovinfoid);
			// エクセル帳票テンプレートファイル名パターン定義
			Pattern pattern = Pattern.compile(Constants.MENUINFOID_EXCELLIST_FILENAMEPATTERN);
			File baseDir = new File(baseDirPath);
			if(baseDir.exists()){
				File[] files = baseDir.listFiles();
				if(files != null && files.length > 0){
					for(int i = 0; i < files.length ; i++){
						String fileName = files[i].getName();
						// パターンに一致すればコピー
						Matcher matcher = pattern.matcher(fileName);
						if(matcher.matches()){
							String newPath = zipWorkDirLevel0.getAbsolutePath() + "/" + fileName;
							FileUtil.fileCopyByPath(files[i].getAbsolutePath(), newPath);
						}
					}
				}
			}

			Boolean zipFileCreated = FileUtil.zipDirectory(zipWorkDirLevel0.getAbsolutePath(), zipFilePath);
			if(zipFileCreated){
				return new File(zipFilePath);
			}else{
				return null;
			}
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		} finally{
			if(zipWorkDirLevel0 != null && zipWorkDirLevel0.exists()){
				FileUtil.dirDelete(zipWorkDirLevel0);
			}
		}
	}

	/**
	 * エンティティタイプを指定してエクセルファイルにエクスポートします.
	 *
	 * @param wb エクセルWorkbook
	 * @param type エンティティタイプ
	 * @param localgovinfoid 自治体ID
	 * @param exportAuthInfo 認証情報エクスポートフラグ
	 */
	public void export(HSSFWorkbook wb, EntityType type, Long localgovinfoid, boolean exportAuthInfo) {
		// エンティティ
		List<Class<?>> classes = new ArrayList<Class<?>>(entitys.get(type));
		List<Class<?>> exported = new ArrayList<Class<?>>();
		List<Class<?>> notexported = new ArrayList<Class<?>>();
		List<Class<?>> skipped = new ArrayList<Class<?>>();
		for(Class<?> entity : classes) {
			try {
				if(!exportAuthInfo && isAuthTable(entity)) {
					skipped.add(entity);
				}
				else {
					appendTableSheet(wb, entity, localgovinfoid, exportAuthInfo);
					exported.add(entity);
				}
			} catch(Exception e) {
				logger.warn("cannot export "+entity.getSimpleName(), e);
				notexported.add(entity);
			}
		}

		// 自治体情報のエクスポートの場合
		if(EntityType.info.equals(type)) {
			// フィルタ条件を追加
			boolean success = exportListSearchConditionInfo(wb, localgovinfoid);
			// 結果シート用
			classes.add(ListSearchConditionInfo.class);
			if(success) exported.add(ListSearchConditionInfo.class);
			else notexported.add(ListSearchConditionInfo.class);
		}

		// エクスポート結果をシートに出力
		appendExportResultSheet(wb, type, classes, exported, notexported, skipped);
		// log
		if(0<notexported.size()) {
			logger.warn("not export classes("+notexported.size()+"): "+notexported.toString());
		}
		logger.debug("export classes("+exported.size()+"): "+exported.toString());
	}

	/**
	 * フィルタ条件をエクスポートします.
	 * eコミマップの Backup&Restore では、フィルタ条件が含まれていないため、
	 * エクセルにシートを追加して import/export 対応する.
	 * @param wb エクセルWorkbook
	 * @param localgovinfoid 自治体ID
	 * @return エクスポート成功フラグ
	 */
	public boolean exportListSearchConditionInfo(HSSFWorkbook wb, Long localgovinfoid) {

		try {
			String tablename = "_list_condition";

			// ヘッダの定義
			List<String> columns = new ArrayList<String>();
			columns.add("id");
			columns.add("status");
			columns.add("created");
			columns.add("map_id");
			columns.add("layer_id");
			columns.add("layer_name"); // optional
			columns.add("user_id");
			columns.add("user_name"); // optional
			columns.add("name");
			columns.add("condition");
			columns.add("spatial");

			TableSheetWriter tableSheetWriter = new TableSheetWriter(wb, tablename, columns);

			MapDB mapDB = MapDB.getMapDB();
			List<MapmasterInfo> mapmasterInfos = mapmasterInfoService.findByLocalgovinfoid(localgovinfoid, true);
			for(MapmasterInfo mapmasterInfo : mapmasterInfos) {
				if(mapmasterInfo.mapid==null) continue;
				MapInfo mapInfo = mapDB.getMapInfo(mapmasterInfo.mapid);

				JSONObject json = ListSearchConditionInfo.getConditionList(/*userInfo*/null, ListSearchConditionInfo.STATUS_DEFAULT, mapInfo, /*order*/ null, /*desc*/ false, /*limit*/0, /*offset*/0);
				JSONArray items = json.getJSONArray("items");
				for(int idx=0; idx<items.length(); idx++) {
					JSONObject item = items.getJSONObject(idx);
					long id = item.getLong("id");
					// map_id が得られていないため設定
					item.put("map_id", mapInfo.mapId);

					// condition カラムが得られてないので別途取得
					JSONObject condition = ListSearchConditionInfo.getConditionValue(/*userInfo*/null, id);
					item.put("condition", condition);

					// シートに1行エクスポート
					tableSheetWriter.writeLine(item);
				}

				tableSheetWriter.end();
			}

			return true;

		} catch (Exception e) {
			logger.error(lang.__("Unable to export filter conditions."), e);
		}

		return false;
	}

	/**
	 * エクスポート結果シートを追加
	 *
	 * @param wb エクセルWorkbook
	 * @param type エンティティタイプ
	 * @param classes 結果シートに出力するクラスのリスト
	 * @param exported エクスポートできたクラスのリスト
	 * @param notexported エクスポートできなかったクラスのリスト
	 * @param skipped エクスポートをスキップしたクラスのリスト
	 */
	public void appendExportResultSheet(HSSFWorkbook wb, EntityType type, List<Class<?>> classes, List<Class<?>> exported, List<Class<?>> notexported, List<Class<?>> skipped) {
		//ファイルを開く
		try {
			// CSVシートにデータを書き込み
			final String SHEET_NAME="Export "+type;
			HSSFSheet sheet = wb.getSheet(SHEET_NAME);
			if(sheet==null) sheet = wb.createSheet(SHEET_NAME);
			wb.setSheetOrder(SHEET_NAME, 0); // 先頭へ
			int rowIdx = 0;

			// ヘッダーの出力
			int cellNum = 0;
			if(true) {
				HSSFRow row = sheet.createRow(rowIdx++);
				int cellIdx = 0;
				row.createCell(cellIdx++).setCellValue(new HSSFRichTextString("Table"));
				row.createCell(cellIdx++).setCellValue(new HSSFRichTextString("Result"));
				row.createCell(cellIdx++).setCellValue(new HSSFRichTextString("Count"));
				cellNum = cellIdx;
			}

			// link style
			HSSFCellStyle linkstyle = wb.createCellStyle();
			HSSFFont linkfont = wb.createFont();
			linkfont.setUnderline(HSSFFont.U_SINGLE);
			linkfont.setColor(HSSFColor.BLUE.index);
			linkstyle.setFont(linkfont);

			// success style
			HSSFCellStyle successstyle = wb.createCellStyle();
			HSSFFont successfont = wb.createFont();
			successfont.setColor(HSSFColor.GREEN.index);
			successstyle.setFont(successfont);

			// fail style
			HSSFCellStyle failstyle = wb.createCellStyle();
			HSSFFont failfont = wb.createFont();
			failfont.setColor(HSSFColor.RED.index);
			failstyle.setFont(failfont);

			// Resultの出力
			for(Class<?> clazz : classes) {
				String tablename = null;
				// フィルタ条件のクラスの場合
				if(clazz==ListSearchConditionInfo.class) {
					tablename = "_list_condition";
				}
				else {
					// Table アノテーションがあるかチェック
					Table table = clazz.getAnnotation(Table.class);
					if(table==null || StringUtil.isEmpty(table.name())) {
						return;
					}
					tablename = table.name();
				}

				boolean isExported = false;
				//boolean isNotExported = false;
				boolean isSkipped = false;
				while(true) {
					if(isExported=exported.contains(clazz)) break;
					//if(isNotExported=notexported.contains(clazz)) break;
					isSkipped=skipped.contains(clazz);
					break;
				}

				// sheet link
				HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper();
				HSSFHyperlink link = createHelper.createHyperlink(HSSFHyperlink.LINK_DOCUMENT);
				String linkSheetName = tablename;
				if(31<linkSheetName.length()) linkSheetName = tablename.substring(0, 31); // シート名は31文字まで
				link.setAddress(linkSheetName+"!A1");

				// row
				HSSFRow row = sheet.createRow(rowIdx++);
				int cellIdx = 0;
				// cell table
				HSSFCell cell = row.createCell(cellIdx++);
				cell.setCellValue(new HSSFRichTextString(tablename));
				cell.setHyperlink(link);
				cell.setCellStyle(linkstyle);
				// cell result
				cell = row.createCell(cellIdx++);

				cell.setCellValue(isExported?"Success":isSkipped?"Skipped":"Failure");
				if(!isSkipped) {
					cell.setCellStyle(isExported?successstyle:failstyle);
				}
				// cell count
				int count = 0;
				HSSFSheet tableSheet = wb.getSheet(linkSheetName);
				if(tableSheet!=null) count = tableSheet.getPhysicalNumberOfRows()-1; // decrease header num
				cell = row.createCell(cellIdx++);
				cell.setCellValue(count);
			}

			// フィルター
			if(0<cellNum) {
				sheet.setAutoFilter(new CellRangeAddress(0, sheet.getLastRowNum(), 0, cellNum-1));
			}

			// 幅を自動調整
			for(int idx=0; idx<cellNum; idx++) {
				sheet.autoSizeColumn(idx);
			}

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * エンティティのテーブルから指定した自治体IDのみのレコードから成るエクスポートシートを追加する.
	 *
	 * @param wb エクセルWorkbook
	 * @param clazz エンティティクラス
	 * @param localgovinfoid 自治体ID
	 * @param exportAuthInfo 認証情報エクスポートフラグ
	 */
	protected void appendTableSheet(HSSFWorkbook wb, Class<?> clazz, Long localgovinfoid, boolean exportAuthInfo) {

		// テーブル名が取得できなければ、エクスポートしない
		LocalgovFilterTable localgovFilterTable = new LocalgovFilterTable(localgovinfoid, clazz);
		if(StringUtil.isEmpty(localgovFilterTable.tablename)) return;

		//
		List<String> columns = localgovFilterTable.columns;
		TableSheetWriter tableSheetExporter = new TableSheetWriter(wb, localgovFilterTable.tablename, columns);


		//ファイルを開く
		try {
			// 認証情報カラム
			Map<String, Boolean> isAuthColumn = new HashMap<String, Boolean>();
			for(String column : columns) {
				isAuthColumn.put(column, isAuthColumn(clazz, column));
			}
			tableSheetExporter.skipDataExport = isAuthColumn;

			// データの出力
			for(TableSheetWriter.TableSheetRow tableSheetRow : localgovFilterTable) {
				tableSheetExporter.writeLine(tableSheetRow);
			}

			tableSheetExporter.end();
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 指定した自治体IDに関連するレコードのみを取得するクラスです.
	 */
	public static class LocalgovFilterTable implements TableSheetWriter.TableSheetRow {

		// Logger
		Logger logger = Logger.getLogger(LocalgovFilterTable.class);

		Long localgovinfoid = null;
		Class<?> entityClass = null;
		String tablename = null;
		List<String> columns = null;
		Map<String, Field> fields = null;
		List<?> result = null;

		int tableSheetRowItIdx = 0;
		Object currentTableSheetRow = null;

		/**
		 *
		 * @param localgovinfoid 絞り込みたい自治体ID
		 * @param entityClass エンティティクラス
		 */
		public LocalgovFilterTable(Long localgovinfoid, Class<?> entityClass) {
			this.localgovinfoid = localgovinfoid;
			this.entityClass = entityClass;

			// Table アノテーションがあるかチェック
			Table table = entityClass.getAnnotation(Table.class);
			if(table==null || StringUtil.isEmpty(table.name())) {
				return;
			}
			tablename = table.name();

			// ヘッダの定義
			fields = new HashMap<String, Field>();
			columns = new ArrayList<String>();
			for(Field field : entityClass.getFields()) {
				Column column = field.getAnnotation(Column.class);
				if(column!=null) {
					String name = field.getName();
					columns.add(name);
					fields.put(name, field);
				}
			}

			// データをDBからロードする
			loadData();
		}

		/**
		 * DBからデータをロードする.
		 */
		public void loadData() {
			// データを取得
			SimpleWhere where = new SimpleWhere();
			// 自治体設定などで自治体IDを指定する場合
			if(localgovinfoid!=null) {
					// Service クラス
					Object service = null;
					Class<?> serviceClass = DatabaseUtil.getServiceClass(entityClass);
					service = SpringContext.getApplicationContext().getBean(serviceClass);

					if(service instanceof AbstractService<?>) {
						AbstractService<?> abstractService = (AbstractService<?>) service;
						result = abstractService.findByLocalgovinfoid(localgovinfoid, true);
					}
					else {
						throw new ServiceException("cannot export "+tablename+" by localgovinfoid="+localgovinfoid);
					}
//				}
			}
			// システムマスタの場合は自治体IDが指定されていない
			if(result==null) {
				JdbcManager jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
				AutoSelect<?> select = jdbcManager.from(entityClass);
				if(0<where.getParams().length) select.where(where);
				result = select.orderBy("id").getResultList();
				logger.debug("SELECT "+entityClass.getSimpleName()+" order by id");
			}
		}

		/**
		 * 現在の行の指定カラムの値を取得する.
		 * @param column 値を取得したいカラム名
		 */
		public Object getColumnValueOnCurrentRow(String column) {
			// 値を取得
			Field field = fields.get(column);
			Object val = null;
			try {
				val = field.get(currentTableSheetRow);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return val;
		}

		/**
		 * @return 削除フラグフィールド deleted(あれば)
		 */
		public Field getDeletedField() {
			try {
				Field field = entityClass.getField("deleted");
				return field;
			} catch(Exception e) {
				return null;
			}
		}

		/**
		 * @return 全件カウント
		 */
		public int getCount() {
			return result.size();
		}

		/**
		 * 未削除数をカウントする.
		 * 削除フラグがあれば、true以外をカウント.
		 * 削除フラグがなければ、全レコード数をカウント.
		 * @return 未削除数
		 */
		public int getCountNotDeleted() {
			Integer deleted = getCountDeleted();
			if(deleted!=null) {
				return result.size() - deleted;
			}
			return result.size();
/*
			Field deletedField = getDeletedField();
			if(deletedField!=null) {
				int count = 0;
				try {
					for(Object entity : result) {
						Object deleted = deletedField.get(entity);
						if(deleted==null || ! ((Boolean) deleted)) {
							count++;
						}
					}
					return count;
				} catch(IllegalAccessException e) {
					throw new ServiceException("Internal Error: "+e.getMessage(), e);
				}
			}
			else {
				return result.size();
			}
*/
		}

		/**
		 * 削除数をカウントする.
		 * 削除フラグがあれば、trueをカウント.
		 * 削除フラグがなければ null
		 * @return 削除数、deletedフラグがなければ null
		 */
		public Integer getCountDeleted() {
			Field deletedField = getDeletedField();
			if(deletedField!=null) {
				int count = 0;
				try {
					for(Object entity : result) {
						Object deleted = deletedField.get(entity);
						if(deleted!=null && (Boolean) deleted) {
							count++;
						}
					}
					return count;
				} catch(IllegalAccessException e) {
					throw new ServiceException("Internal Error: "+e.getMessage(), e);
				}
			}
			return null;
		}

		/**
		 * @return {@link TableSheetWriter.TableSheetRow}のためのイテレータ
		 */
		@Override
		public Iterator<TableSheetWriter.TableSheetRow> iterator() {
			return this;
		}

		/**
		 * 次の行{@link TableSheetWriter.TableSheetRow}があるかどうか
		 * @return 次の行があるなら true
		 */
		@Override
		public boolean hasNext() {
			if(result==null) return false;
			return tableSheetRowItIdx < result.size();
		}

		/**
		 * @return 次の行{@link TableSheetWriter.TableSheetRow}
		 */
		@Override
		public TableSheetWriter.TableSheetRow next() {
			try {
				currentTableSheetRow = result.get(tableSheetRowItIdx++);
			} catch(Exception e) {
				throw new NoSuchElementException();
			}
			return this;
		}

		/**
		 * 特に処理しない
		 */
		@Override
		public void remove() {
			// do nothing
		}
	}
}
