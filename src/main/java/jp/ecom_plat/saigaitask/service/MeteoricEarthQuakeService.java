/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerGroupInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.exception.EcommapConflictException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.AlertrequestInfoDto;
import jp.ecom_plat.saigaitask.entity.db.EarthquakegrouplayerData;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoareainformationcityMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.EarthquakegrouplayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteoareainformationcityMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.TimeUtil;



/**
 * 気象庁XMLの「震源震度に関する情報」から震度レイヤを作成するサービスクラス
 */
@org.springframework.stereotype.Service
public class MeteoricEarthQuakeService extends BaseService{


	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected MeteoareainformationcityMasterService meteoareainformationcityMasterService;
	@Resource
	protected EarthquakegrouplayerDataService earthquakegrouplayerDataService;
	@Resource
	protected EarthquakelayerDataService earthquakelayerDataService;
	@Resource
	protected MeteorequestInfoService meteorequestInfoService;
	@Resource
	protected TrackmapInfoService trackmapInfoService;
	@Resource
	protected MapService mapService;
	@Resource
	protected TableService tableService;
	/** Application scope */
	@Resource
	protected Map<String, Object> applicationScope;
	@Resource
	protected EdituserAttrService edituserAttrService;
	@Resource
	protected TrackdataidAttrService trackdataidAttrService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	@Resource
	protected EarthquakelayerInfoService earthquakelayerInfoService;
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected LayerService layerService;

	/** 自治体ID */
	Long localgovinfoid;
	/** 自治体のマスターマップID */
	Long masterMapID;
	/** 自治体の災害用マップID群 */
	ArrayList<Long> trackMapID = new ArrayList<Long>();
	/** 地震用クラス */
	List<EarthQuakeLayerAttr> earthQuakeLayerAttrList;
	/** 地震用クラス */
	String earthQuakeLayerName = "";
	/** 自治体のecomuserの_userテーブルのuser_id (管理者権限ユーザ) */
	String ecomuser = "";
	/** 配信時間 */
	String reportdateTime;
	/** 地震発生日時 */
	String originTime;
	/** 配信XMLのEventID */
	String eventId;
	/** 災害時共有対象とする作成レイヤの時間 */
	@Deprecated
	public int EARTHQUAKE_SHARELAYER_TIME_DIFF = -30;
	/** 震度レイヤの表示時間 Default=24h */
	public long EARTHQUAKE_LAYER_DISPLAY_TIME = 1000*60*60*24;
	/** レイヤ作成対象とする最高震度 */
	public String EARTHQUAKE_MINIMAM_INT = "4";
	/** 管理者権限を持つユーザがいなければスーパーユーザ */
	public static final int ECOM_ADMIN_USER_ID = 1;
	/** eコミの管理者権限ユーザID */
	public static final String ECOM_ADMIN_AUTH_ID = "admin";
	/** 地震用レイヤのテンプレートSLDファイル名 */
	public static final String EARTH_QUAKE_SLD_TEMPLATE_FILENAME = "/earthQuake.sld";
	/** 地震用レイヤのテンプレートSLDPath */
	public static final String EARTH_QUAKE_SLD_TEMPLATE_PATH = "/WEB-INF/template/sld/";

	/** JMX 空間名定義 */
	public static final Namespace NAMESPACE_JMX = Namespace.getNamespace("jmx", "http://xml.kishou.go.jp/jmaxml1/");
	/** JMX_BASIC 空間名定義 */
	public static final Namespace NAMESPACE_JMX_BASIC 	= Namespace.getNamespace("jmx_basic", "http://xml.kishou.go.jp/jmaxml1/informationBasis1/");
	/** JMX_SEIS 空間名定義 */
	public static final Namespace NAMESPACE_JMX_SEIS = Namespace.getNamespace("jmx_seis", "http://xml.kishou.go.jp/jmaxml1/body/seismology1/");
	/** JMX_EB 空間名定義 */
	public static final Namespace NAMESPACE_JMX_EB = Namespace.getNamespace("jmx_eb", "http://xml.kishou.go.jp/jmaxml1/elementBasis1/");
	/** 4612 測地系フラグ定義 */
	public static String JAPAN_REFERENCE_SYSTEM 	= "4612";
	/** 4326 測地系フラグ定義 */
	public static String WORLD_REFERENCE_SYSTEM 	= "4326";
	/** 記載内容が読み取れなかったり入っていない場合等は不明を入れる */
	public /*static */String getXML_PARAM_UNKNOWN() {
		return lang.__("Unknown");
	}
	/** レイヤ作成時のDescriptionは固定文 */
	public /*static */String getLAYER_DESCRIPTION() {
		return lang.__("Automatically-generated Layer by Japan Meteorological Agency delivery file.");
	}
	/** XPATHでエレメント検索用の空間名リスト */
	ArrayList<Namespace> listNamespace = new ArrayList<Namespace>();

	/** 言語メッセージマッピング */
	public Map<String, String> langMap = new HashMap<String, String>();

	/** 表示データフォーマット */
	// 気象庁XMLでは日本語のみを扱うので多言語化しない
	// demoAction等、ログインの必要がない機能から呼び出されると英語モードで解釈する為、formatDataTimeでエラーが発生する為の対応
	private String formatPosNorth 	= "北緯%.1f度";
	private String formatPosSouth 	= "南緯%.1f度";
	private String formatPosEast 	= "東経%.1f度";
	private String formatPosWest 	= "南経%.1f度";
	private String formatDateTime 	= "%s月%s日 %s時%s分";

	/** 市町村コードが格納された属性名 地物の検索で利用している */
	final String CITY_CODE_ATTR = "attr8";

	Logger logger = Logger.getLogger(MeteoricEarthQuakeService.class);

	/**
	 * 初期化
	 */
	public MeteoricEarthQuakeService(){
		ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
		// この共有設定はver2.0では使用しない
		/*
		if(bundle.containsKey("EARTHQUAKE_SHARELAYER_TIME_DIFF")){
			String shareLayerTimeDiff = bundle.getString("EARTHQUAKE_SHARELAYER_TIME_DIFF");
			shareLayerTimeDiff = shareLayerTimeDiff == null ? "-30" : shareLayerTimeDiff;
			try{
				EARTHQUAKE_SHARELAYER_TIME_DIFF = Integer.parseInt(shareLayerTimeDiff);
			}catch(NumberFormatException e){
				logger.info(lang.__("For EARTHQUAKE_SHARELAYER_TIME_DIFF has not been set, it was set 30 minutes earlier than default value."));
			}
		}
		*/
		// 地震発生から何分画面表示しているかの設定
		if(bundle.containsKey("EARTHQUAKE_LAYER_DISPLAY_TIME")){
			String layerDisplayTime = bundle.getString("EARTHQUAKE_LAYER_DISPLAY_TIME");
			try{
				EARTHQUAKE_LAYER_DISPLAY_TIME = layerDisplayTime == null ? EARTHQUAKE_LAYER_DISPLAY_TIME : Long.parseLong(layerDisplayTime);
			}catch(NumberFormatException e){
				logger.info("For EARTHQUAKE_LAYER_DISPLAY_TIME has not been set.");
			}
		}
		// 取得する最大震度の閾値の設定
		if(bundle.containsKey("EARTHQUAKE_MINIMAM_INT")){
			String earthquake_minimam_int = bundle.getString("EARTHQUAKE_MINIMAM_INT");
			EARTHQUAKE_MINIMAM_INT = earthquake_minimam_int.equals("") ? EARTHQUAKE_MINIMAM_INT : earthquake_minimam_int;
		}
	}

	/**
	 * 震度レイヤに震度情報を登録する
	 * Ver2.0系
	 * @param jmaxml
	 * @param meteodataid
	 * @return 成功フラグ
	 */
	public boolean inputEarthQuake(File jmaxml, Long meteodataid, AlertrequestInfoDto requestDto){
		boolean check = true;

		// 初期化
		earthQuakeLayerAttrList = new ArrayList<EarthQuakeLayerAttr>();

		// 解析
		int analyze = analyzeKakuteishindoXML(jmaxml);
		if(analyze == -1){
			logger.error("Failed to EarthQuakeFile Analyze");
			return false;
		}else if(analyze == 0){
			// 閾値より震度が小さい時
			logger.info("Following earthquake threshold");
			return false;
		}

		// MeteotypeID = 4(震源震度情報)の取得を行う自治体のみ実施
		try {
			//List<MeteorequestInfo> requestInfo = meteorequestInfoService.findByMeteoTypeIDValid(Constants.METEO_SHINGENSHINDOJOUHOU);
			//for(MeteorequestInfo info : requestInfo)
			MeteorequestInfo info = meteorequestInfoService.findById(requestDto.id);
			{
				MapDB mapDB = MapDB.getMapDB();

				// 各種パラメータ設定
				// 自治体ID
				this.localgovinfoid = info.localgovinfoid;
				// ecommapのUser情報の取得
				UserInfo userInfo = searchEcomUserInfo(mapDB, this.localgovinfoid);

				// マスターマップIDの取得
				MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(this.localgovinfoid);
				this.masterMapID = mapmasterInfo.mapid;
				// 日付フォーマット
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// earthquakelayer_infoから自自治体のレコードを取得する
				List<EarthquakelayerInfo> earthquakelayerInfos = earthquakelayerInfoService.findByLocalgovinfoid(this.localgovinfoid);
				// tablemaster_infoに未設定の場合はマスタと現在の災害マップにレイヤを作成する（コピーフラグ対応：訓練モード）
				if(earthquakelayerInfos.size() == 0){
					// マスタマップに震度レイヤを作成
					// 実レイヤ作成と共にearthquakelayer_infoも合わせて作成
					EarthquakelayerData layerData = insertEarthQuakeHistoryLayer(earthQuakeLayerName, getLAYER_DESCRIPTION(), this.masterMapID, meteodataid, this.localgovinfoid, mapmasterInfo.id, false);
					// 現在の配信データを入れる
					geometryInsertOnly(mapDB, layerData.layerid, this.masterMapID, userInfo);
					// Ver1系と同様、マップコピーの為、災害マップがあれば複製する
					// 災害マップ
					List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(this.localgovinfoid, false);
					for(TrackData td : trackDatas){
						String layerId = layerData.layerid;
						//Trackmapを探す
						TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
						// マップ複製の場合
						if(tmapInfo.mapid.equals(this.masterMapID)==false) {
							// 実レイヤ作成と共にearthquakelayer_infoも合わせて作成
							EarthquakelayerData tracklayerData = insertEarthQuakeHistoryLayer(earthQuakeLayerName, getLAYER_DESCRIPTION(), tmapInfo.mapid, meteodataid, this.localgovinfoid, -1, false);
							// 現在の配信データを入れる
							geometryInsertOnly(mapDB, tracklayerData.layerid, tmapInfo.mapid, userInfo);
							layerId = tracklayerData.layerid;
						}
						// TrackmapInfoに登録
						TracktableInfo tracktableInfo = new TracktableInfo();
						tracktableInfo.trackmapinfoid = tmapInfo.id;
						tracktableInfo.tablemasterinfoid = layerData.earthquakelayerInfo.tablemasterinfoid;
						tracktableInfo.layerid = layerId;
						tracktableInfo.tablename = layerId;
						tracktableInfoService.insert(tracktableInfo);
						// 複数同時であってもマップは１つなのでここでやめる
						break;
					}
					// 訓練マップはコピーフラグに関係なく作成
					List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(this.localgovinfoid, true);
					for(TrackData td : trainingDatas){
						//Trackmapを探す
						TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
						// 実レイヤ作成と共にearthquakelayer_infoも合わせて作成
						EarthquakelayerData tracklayerData = insertEarthQuakeHistoryLayer(earthQuakeLayerName, getLAYER_DESCRIPTION(), tmapInfo.mapid, meteodataid, this.localgovinfoid, -1, false);
						// 現在の配信データを入れる
						geometryInsertOnly(mapDB, tracklayerData.layerid, tmapInfo.mapid, userInfo);
						// TrackmapInfoに登録
						TracktableInfo tracktableInfo = new TracktableInfo();
						tracktableInfo.trackmapinfoid = tmapInfo.id;
						tracktableInfo.tablemasterinfoid = layerData.earthquakelayerInfo.tablemasterinfoid;
						tracktableInfo.layerid = tracklayerData.layerid;
						tracktableInfo.tablename = tracklayerData.layerid;
						tracktableInfoService.insert(tracktableInfo);
						// 複数同時であってもマップは１つなのでここでやめる
						break;
					}
				}
				// TablemasterInfoが存在する場合
				else{
					// 基本的に１件しか存在しない
					for(EarthquakelayerInfo quakeLayerInfo : earthquakelayerInfos){
						// マスタマップの震度レイヤ
						TablemasterInfo tablemasterInfo_master = tablemasterInfoService.findById(quakeLayerInfo.tablemasterinfoid);
						// 過去に配信された地震か確認
						List<EarthquakelayerData> old_earthquakeLayerDataList = earthquakelayerDataService.findByEventIdAndInfoId(eventId, quakeLayerInfo.id);

						// 配信済みの震度情報の続報(履歴のUpdate)
						if(old_earthquakeLayerDataList.size() > 0){
							LayerInfo layerInfo = mapDB.getLayerInfo(tablemasterInfo_master.layerid);
							// マスタマップの震度レイヤを更新
							updateLayerHistoryToEqualsEventIdLayer(layerInfo, sdf, this.masterMapID, userInfo);
							// マップコピーモードがtrueなら災害マップを確認する
							if(mapmasterInfo.copy){
								List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(this.localgovinfoid, false);
								for(TrackData td : trackDatas){
									//Trackmapを探す
									TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
									TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo_master.id, td.id);
									// 災害マップかつlayernameが異なるものであれば処理を実行
									if(ttbl != null && !tablemasterInfo_master.layerid.equals(ttbl.layerid)){
										layerInfo = mapDB.getLayerInfo(ttbl.layerid);
										updateLayerHistoryToEqualsEventIdLayer(layerInfo, sdf, tmapInfo.mapid, userInfo);
									}
									// 複数同時であってもマップは１つなのでここでやめる
									break;
								}
							}
							// 訓練マップはコピーフラグに関係なく対応
							List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(this.localgovinfoid, true);
							for(TrackData td : trainingDatas){
								//Trackmapを探す
								TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
								TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo_master.id, td.id);
								// 災害マップかつlayernameが異なるものであれば処理を実行
								if(ttbl != null && !tablemasterInfo_master.layerid.equals(ttbl.layerid)){
									layerInfo = mapDB.getLayerInfo(ttbl.layerid);
									updateLayerHistoryToEqualsEventIdLayer(layerInfo, sdf, tmapInfo.mapid, userInfo);
								}
								// 複数同時であってもマップは１つなのでここでやめる
								break;
							}
						}
						// 新規配信の震度情報の処理
						else{
							// 配信履歴としてearthquakelayer_dataに新規登録(日本時間のまま登録する)
							EarthquakelayerData earthquakelayerData = new EarthquakelayerData();
							earthquakelayerData.meteodataid = meteodataid;
							earthquakelayerData.eventid = eventId;
							earthquakelayerData.origintime = Timestamp.valueOf(originTime);
							earthquakelayerData.reportdatetime = Timestamp.valueOf(reportdateTime);
							earthquakelayerData.name = earthQuakeLayerName;
							earthquakelayerData.earthquakelayerinfoid = quakeLayerInfo.id;
							earthquakelayerDataService.insert(earthquakelayerData);

							// マスタマップの震度レイヤを更新
							LayerInfo layerInfo = mapDB.getLayerInfo(tablemasterInfo_master.layerid);
							insertLayerHistory(mapDB, layerInfo, sdf, this.masterMapID, userInfo);

							// マップコピーモードがtrueなら災害マップを確認する
							if(mapmasterInfo.copy){
								List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(this.localgovinfoid, false);
								for(TrackData td : trackDatas){
									//Trackmapを探す
									TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
									TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo_master.id, td.id);
									// 災害マップかつlayernameが異なるものであれば処理を実行
									if(ttbl != null && !tablemasterInfo_master.layerid.equals(ttbl.layerid)){
										layerInfo = mapDB.getLayerInfo(ttbl.layerid);
										insertLayerHistory(mapDB, layerInfo, sdf, tmapInfo.mapid, userInfo);
									}
									// 複数同時であってもマップは１つなのでここでやめる
									break;
								}
							}
							// 訓練マップはコピーフラグに関係なく対応
							List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(this.localgovinfoid, true);
							for(TrackData td : trainingDatas){
								//Trackmapを探す
								TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
								TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo_master.id, td.id);
								// 災害マップかつlayernameが異なるものであれば処理を実行
								if(ttbl != null && !tablemasterInfo_master.layerid.equals(ttbl.layerid)){
									layerInfo = mapDB.getLayerInfo(ttbl.layerid);
									insertLayerHistory(mapDB, layerInfo, sdf, tmapInfo.mapid, userInfo);
								}
								// 複数同時であってもマップは１つなのでここでやめる
								break;
							}
						}
					}
				}
			}
			// 自動起動のみに変更
			earthquakeLayerDelete();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return check;
	}

	/**
	 * Can be used when creating a new layer or when all features have been deleted (when there is a history but all data has a value in time_to)
	 * InsertFeature or InsertfeatureHistoryのみ
	 * @param mapDB : ecommapのMapDB
	 * @param layerId : 対象となる震度レイヤID
	 * @param mapId : 対象となるマップID
	 * @param userInfo : ecommapのUserInfo
	 * @throws Exception
	 */
	private void geometryInsertOnly(MapDB mapDB, String layerId, long mapId, UserInfo userInfo){
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
		LayerInfo layerInfo2 = mapDB.getLayerInfo(layerId);
		vecLayerInfo.add(layerInfo2);

		try{

		String keywords = "";
		// レイヤ名称を配信された地震名に変更する
		MapLayerInfo maplayerInfo = mapDB.getMapLayerInfo(mapId, layerId);
		maplayerInfo.layerName = earthQuakeLayerName;
		MapInfo mapInfo = mapDB.getMapInfo(mapId);
		mapDB.updateMapLayerInfo(mapInfo, maplayerInfo);

		// 地物の新規追加 or 既存地物の属性値を更新する
		for(int i = 0; i < earthQuakeLayerAttrList.size(); i++){
			EarthQuakeLayerAttr layerAttr = earthQuakeLayerAttrList.get(i);
			// 地震用クラスをHashMapに変換
			HashMap<String, String> attributes = layerAttr.toHashMap();
			// 市町村コードで検索
			keywords = CITY_CODE_ATTR + "~*" + layerAttr.cityCode;
			FeatureResultList cityList = searchEcomFeatureList(mapId, vecLayerInfo, keywords);
			// 通常の新規登録
			if(cityList.countResult() == 0){
				FeatureDB.insertFeature(userInfo, layerId, layerAttr.getWKT(), attributes);
			}
			// 更新(履歴追加)
			else{
				// 基本的には1件
				for(int j = 0; j < cityList.countResult(); j++){
					FeatureResult result = cityList.getResult(j);
					long orgId = result.featureId;
					LayerInfo layerInfo = mapDB.getLayerInfo(result.layerId);
					// 履歴追加
					long newFid = FeatureDB.insertFeatureHistory(layerInfo, /*orgId*/orgId, layerAttr.getWKT(), attributes, userInfo);
					if(newFid == 0) logger.warn("EarthquakeService.insertHistory -> new Fid = 0");
				}
			}
		}

		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 過去に配信があった地震の続報が来た際の処理
	 * @param layerInfo : 対象となるLayerInfo
	 * @param sdf : SimpleDateFormat
	 * @param mapId : 対象となるマップID
	 * @param userInfo : ecommapのUserInfo
	 * @throws Exception : ParseExceptionとか色々
	 */
	private void updateLayerHistoryToEqualsEventIdLayer(LayerInfo layerInfo, SimpleDateFormat sdf, long mapId, UserInfo userInfo){
		// vecLayerInfoのみ先に格納する
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
		vecLayerInfo.add(layerInfo);
		try{

		// 更新する
		String keywords = "";
		// 処理しようとしている震度情報が最新なのかチェック
		boolean isLatest = false;
		// 震央の市町村コードは0000000で設定してある
		keywords = CITY_CODE_ATTR+"~*0000000";
		// Keyword検索で震央情報を取得する
		FeatureResultList nowQuakeCenter = searchEcomFeatureList(mapId, vecLayerInfo, keywords);

		// 発生時間を格納
		String nowQuakeOriginTime = nowQuakeCenter.getResult(0).getAttrResult("attr0").getAttrValue();
		// 今の最新データより新しいか確認
		if(isAfterOriginTime(sdf, nowQuakeOriginTime, earthQuakeLayerAttrList.get(0).originTime)){
			isLatest = true;
		}

		// 削除されている地物の対応の為、配信時間のレコードは一覧で取得する
		keywords = "attr0~*" + earthQuakeLayerAttrList.get(0).originTime + " AND attr6!~*0";
		// 過去に配信された震度情報を取得。後で続報によって削除となった地物にアプローチする。
		// String型で格納 string = layerid_featureid_citycodeの形
		List<String> orgQuakeFeatureList = searchFeatureListToString(searchEcomFeatureList(mapId, vecLayerInfo, keywords));

		// レコード更新
		for(int i = 0; i < earthQuakeLayerAttrList.size(); i++){
			EarthQuakeLayerAttr layerAttr = earthQuakeLayerAttrList.get(i);
			// 地震用クラスをHashMapに変換
			HashMap<String, String> attributes = layerAttr.toHashMap();
			// 履歴は触らずに中身だけ更新
			keywords = CITY_CODE_ATTR + "~*" + layerAttr.cityCode;
			// 登録しようとしている市町村が登録済かチェック
			FeatureResultList cityList = searchEcomFeatureList(mapId, vecLayerInfo, keywords);
			// 存在しなければ続報で追加された市町村なので、通常の新規登録
			if(cityList.countResult() == 0){
				long fid = FeatureDB.insertFeature(userInfo, layerInfo.layerId, layerAttr.getWKT(), attributes);
				// 別の地震が発生していて、続報を受け取った地震が最新データでない場合は、time_fromに1秒追加して、図形削除処理を行う
				if(!isLatest){
					// 削除用のHashMapを作成
					HashMap<String, String> del_attributes = new EarthQuakeLayerAttr(sdf, attributes, layerAttr.cityCode, layerAttr.originTime, true).toHashMap();
					// 図形無しで再登録
					FeatureDB.insertFeatureHistory(layerInfo, fid, null, del_attributes, userInfo);
				}
			}
			// 存在していれば履歴の内容だけ更新(履歴追加なし)
			else{
				// 基本的には1件
				for(int j = 0; j < cityList.countResult(); j++){
					FeatureResult result = cityList.getResult(j);
					long orgId = result.featureId;
					// _orgidからgidを取得する(UTC対応)
					//long historyId = ExMapDB.getHistoryFeatureId(result.layerId, orgId, new Date[]{sdf.parse(layerAttr.originTime)});
					long historyId = ExMapDB.getHistoryFeatureId(result.layerId, orgId, new Date[]{sdf.parse(layerAttr.time_from)});
					// 履歴の更新
					FeatureDB.updateFeatureHistory(layerInfo, orgId, historyId, layerAttr.getWKT(), attributes, null, userInfo);
					// 削除対象から外す
					if(orgQuakeFeatureList.contains(result.layerId + "_" + orgId + "_" + layerAttr.cityCode)){
						orgQuakeFeatureList.remove(result.layerId + "_" + orgId + "_" + layerAttr.cityCode);
					}
				}
			}
		}
		// 更新対象から外れている地物の履歴を削除
		for(String orgQuake : orgQuakeFeatureList){
			String[] orgQuakeArr = orgQuake.split("_");
			long orgId = Long.parseLong(orgQuakeArr[1]);

			try {
				// UTC対応
				Date timeFrom = sdf.parse(earthQuakeLayerAttrList.get(0).originTime);
				Date utcTimeFrom = TimeUtil.newUTCDate(timeFrom.getTime());
				long historyId = ExMapDB.getHistoryFeatureId(orgQuakeArr[0], orgId, new Date[]{utcTimeFrom});
				FeatureDB.deleteFeatureHistory(layerInfo, orgId, historyId, null);
			} catch (ParseException e) {
				// エラーの場合は何もしない
				logger.error("fail originTime parseError " + earthQuakeLayerAttrList.get(0).originTime);
			}
		}

		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 震度レイヤに過去データが格納された事のある場合の登録、更新用
	 * @param mapDB : ecommapのmapDB
	 * @param layerInfo : 更新対象のLayerInfo
	 * @param sdf : SimpleDateFormat
	 * @param mapId : ecommapのマップID
	 * @param userInfo : ecommapのUserInfo
	 * @throws Exception : parseExceptionとか色々
	 */
	private void insertLayerHistory(MapDB mapDB, LayerInfo layerInfo, SimpleDateFormat sdf, long mapId, UserInfo userInfo) throws Exception{
		// 現在の最新震央地点の発生時間と同一の地点を収集しておく
		// 図形をもつ地物を図形なし＋震度情報0にする
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
		vecLayerInfo.add(layerInfo);

		String keywords = "";
		String nowQuakeOriginTime = "";
		// パターン1 : 登録される震度情報が現在よりも新しい
		// パターン2 : 登録される震度情報が現在よりも古い
		boolean isLatest = false;
		// 現在の震度情報を格納するリスト
		List<String> orgQuakeFeatureList = new ArrayList<String>();

		// 震央の市町村コードはattr8=0000000, attr6='-'で設定してあるので1件は必ず取得出来るはず
		keywords = CITY_CODE_ATTR+"~*0000000 AND attr6!~*0";
		FeatureResultList nowQuakeCenter = searchEcomFeatureList(mapId, vecLayerInfo, keywords);
		// ローテーション等で地図上の描画がない場合は、新規登録と同じ処理を実施
		if(nowQuakeCenter.total == 0){
			geometryInsertOnly(mapDB, layerInfo.layerId, mapId, userInfo);
		}
		// 何かしらの地震が出ている状態
		else{
			// 発生時間を格納
			nowQuakeOriginTime = nowQuakeCenter.getResult(0).getAttrResult("attr0").getAttrValue();
			// 震度0でない地域のデータを取得する
			keywords = "attr6!~*0";
			// 削除に必要なデータをString型で格納 string = layerid_featureid_citycodeの形
			orgQuakeFeatureList = searchFeatureListToString(searchEcomFeatureList(mapId, vecLayerInfo, keywords));
			// 今の最新データより新しいか確認
			if(isAfterOriginTime(sdf, nowQuakeOriginTime, earthQuakeLayerAttrList.get(0).originTime)){
				isLatest = true;
			}
			// 最新データ取得時に使用するDate(UTC対応)
			Date[] latestDate = new Date[]{TimeUtil.newDate()};
			// レイヤ名称を変更する
			/*
			MapLayerInfo maplayerInfo = mapDB.getMapLayerInfo(mapId, layerInfo.layerId);
			maplayerInfo.layerName = earthQuakeLayerName;
			MapInfo mapInfo = mapDB.getMapInfo(mapId);
			mapDB.updateMapLayerInfo(mapInfo, maplayerInfo);
			*/

			// 属性値を更新する
			for(int i = 0; i < earthQuakeLayerAttrList.size(); i++){
				EarthQuakeLayerAttr layerAttr = earthQuakeLayerAttrList.get(i);
				// 地震用クラスをHashMapに変換
				HashMap<String, String> attributes = layerAttr.toHashMap();
				// 市町村コードで検索
				keywords = CITY_CODE_ATTR + "~*" + layerAttr.cityCode;
				FeatureResultList cityList = searchEcomFeatureList(mapId, vecLayerInfo, keywords);
				// 通常の新規登録
				if(cityList.countResult() == 0){
					FeatureDB.insertFeature(userInfo, layerInfo.layerId, layerAttr.getWKT(), attributes);
				}
				// 更新(履歴追加)
				else{
					// 基本的には1件
					for(int j = 0; j < cityList.countResult(); j++){
						FeatureResult result = cityList.getResult(j);
						long orgId = result.featureId;
						//LayerInfo layerInfo = mapDB.getLayerInfo(result.layerId);
						// _orgidからgidを取得する
						// 履歴追加
						long newFid = FeatureDB.insertFeatureHistory(layerInfo, orgId, layerAttr.getWKT(), attributes, userInfo);
						if(newFid == 0) logger.warn("EarthquakeService.insertHistory -> new Fid = 0");
						// 削除対象から外す
						if(orgQuakeFeatureList.contains(result.layerId + "_" + orgId + "_" + layerAttr.cityCode)){
							orgQuakeFeatureList.remove(result.layerId + "_" + orgId + "_" + layerAttr.cityCode);
						}
						// パターン2の処理
						// 最新データでない場合 and 同一地点にそれ以降のデータが無い場合は、time_fromに1秒追加して、図形削除処理を行う
						if(!isLatest){
							// 最新データを取得
							FeatureResult latestData = FeatureDB.getFeatureContent(layerInfo, orgId, false, FeatureDB.GEOM_TYPE_GEOM, null, latestDate);
							String latestOriginTime = latestData.getAttrResult("attr0").getAttrValue();
							Date latestOriginDate = sdf.parse(latestOriginTime);
							Date insertOriginDate = sdf.parse(layerAttr.originTime);
							// 最新が今登録したデータの場合は、地物の削除を実行する(日本時間同士の比較)
							if(insertOriginDate.equals(latestOriginDate)){
								// 削除用のHashMapを作成
								HashMap<String, String> del_attributes = new EarthQuakeLayerAttr(sdf, attributes, layerAttr.cityCode, layerAttr.originTime, true).toHashMap();
								// 図形無しで再登録
								FeatureDB.insertFeatureHistory(layerInfo, orgId, null, del_attributes, userInfo);
							}
						}
					}
				}
			}
			// パターン1 : 登録される震度情報が現在よりも新しい
			if(isLatest){
				// 更新対象から外れている地物の図形を削除する（DeleteFeatureだと履歴が遡れなくなるので）
				for(String orgQuake : orgQuakeFeatureList){
					String[] orgQuakeArr = orgQuake.split("_");
					long orgId = Long.parseLong(orgQuakeArr[1]);
					String cityCode = orgQuakeArr[2];
					// 更新するのは図形の削除とtime_toだけなのでattributersを取得する
					keywords = CITY_CODE_ATTR + "~*" + cityCode;
					FeatureResultList fresultList = searchEcomFeatureList(mapId, vecLayerInfo, keywords);
					for(int i = 0; i < fresultList.total; i++){
						FeatureResult result = fresultList.getResult(i);
						// _orgidのチェック
						if(orgId != result.featureId) continue;
						// 属性情報
						HashMap<String, String> attrs = new HashMap<String, String>();
						int cntAttrSum = result.countAttrResult();
						for(int k = 0; k < cntAttrSum; k++){
							attrs.put(result.getAttrResult(k).getAttrId(), result.getAttrResult(k).getAttrValue());
						}
						// 最大震度は0にする
						attrs.put(CITY_CODE_ATTR, "0");
						// 図形削除をする更新時間を作成（削除するわけではないのでtime_toではなくtime_from）
						String delTimeFrom = convertTimeFrom(sdf, earthQuakeLayerAttrList.get(0).originTime, false);
						attrs.put("time_from", delTimeFrom);

						// 図形削除用のコンストラクタを利用して生成(time_fromを1秒戻して登録する)
						//HashMap<String, String> attributes2 = new EarthQuakeLayerAttr(sdf, attrs, orgQuakeArr[2], earthQuakeLayerAttrList.get(0).originTime, false).toHashMap();
						// 更新する
						FeatureDB.insertFeatureHistory(layerInfo, /*orgId*/orgId, null, attrs, userInfo);
					}
				}
			}
		}
	}


	/**
	 * ecommapのKeywords検索を実行する
	 * @param mapid : 対象地図ID
	 * @param vecLayerInfo : 対象LayerInfo
	 * @param keywords : 検索文字列
	 * @return : FeatureResultList
	 * @throws Exception
	 */
	private FeatureResultList searchEcomFeatureList(long mapid, Vector<LayerInfo> vecLayerInfo, String keywords) throws Exception{
		return FeatureDB.searchFeatureBbox(null, mapid, vecLayerInfo, null/*bbox*/, keywords, 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, null);
	}
	/**
	 * eコミのFeatureResultListをStringに変換する関数
	 * layerid_featureid(orgid)_citycodeの形
	 * @param resultList
	 * @return
	 */
	private List<String> searchFeatureListToString(FeatureResultList resultList){
		List<String> resultStringList = new ArrayList<String>();
		for(int j = 0; j < resultList.countResult(); j++){
			FeatureResult result = resultList.getResult(j);
			String cityCode = result.getAttrResult(CITY_CODE_ATTR).getAttrValue();
			//long historyId = ExMapDB.getHistoryFeatureId(result.layerId, result.featureId, new Date[]{sdf.parse(nowQuakeOriginTime)});
			resultStringList.add(result.layerId + "_" + result.featureId + "_" + cityCode);
		}
		return resultStringList;
	}

	/**
	 * inserttimeの方が最新かチェック
	 * @param sdf
	 * @param latesttime : 今の最新データの日付文字列
	 * @param inserttime : 挿入しようとしているデータの日付文字列
	 * @return ; insertimeの方が新しいならtrue
	 * @throws ParseException
	 */
	private boolean isAfterOriginTime(SimpleDateFormat sdf, String latesttime, String inserttime) throws ParseException{
		/*
		String latestOriginTime = latestData.getAttrResult("attr0").getAttrValue();
		Date latestOriginDate = sdf.parse(latestOriginTime);
		Date insertOriginDate = sdf.parse(layerAttr.originTime);
		*/
		if(latesttime == null || latesttime.equals("") || inserttime == null || inserttime.equals("")) return false;
		// 今の最新の発生時間
		Date latestOriginDate = sdf.parse(latesttime);
		// 今から投入しようとしている震度情報の発生時間
		Date insertOriginDate = sdf.parse(inserttime);
		// 新しい震度情報として扱う
		if(insertOriginDate.after(latestOriginDate)){
			return true;
		}
		return false;
	}

	/**
	 * 一定期間経過した震度レイヤを見えなくする関数(デフォルト24h)
	 */
	public void earthquakeLayerDeleteCheck(){
		// Application Scopeの日付データを参照して、24h経過していたら実行する
		if(applicationScope == null){
			applicationScope = new HashMap<String, Object>();
			applicationScope.put("delete_layer_time", new Date());
		}else if(!applicationScope.containsKey("delete_layer_time")){
			applicationScope.put("delete_layer_time", new Date());
		}else{
			Date deleteTime = (Date)applicationScope.get("delete_layer_time");
			if(deleteTime.after( new Date( new Date().getTime()-EARTHQUAKE_LAYER_DISPLAY_TIME )) ) return;
		}
		// 24時間前のDate型を作成(日本時間同士の比較)
		Timestamp ago24h = new Timestamp(new Date().getTime()-EARTHQUAKE_LAYER_DISPLAY_TIME);
		// 24時間以上経過したレイヤがあるかチェック EARTHQUAKE_LAYER_DISPLAY_TIME
		List<EarthquakelayerData> earthquakelayerDatas = earthquakelayerDataService.findOldQuakeLayers(ago24h);
		if(earthquakelayerDatas.size() == 0){
			logger.info("EarthquakeLayer_OldData count = 0");
			return;
		}
		// 削除関数をキック
		earthquakeLayerDelete();
	}

	/**
	 * 古い震度レイヤを探して図形を削除する
	 */
	private void earthquakeLayerDelete(){
		// 24時間前のDate型を作成(日本時間同士の比較)
		Timestamp ago24h = new Timestamp(new Date().getTime()-EARTHQUAKE_LAYER_DISPLAY_TIME);
		List<EarthquakelayerData> earthquakelayerDatas = earthquakelayerDataService.findOldQuakeLayers(ago24h);
		try{
			// eコミDB取得
			MapDB mapDB = MapDB.getMapDB();
			// 削除したレイヤを収集するList
			List<String> deletedLayers = new ArrayList<String>();
			// 自治体毎のecomuserを利用する
			for(EarthquakelayerData layerData : earthquakelayerDatas){
				if(layerData.earthquakelayerInfo == null) continue;
				if(layerData.earthquakelayerInfo.localgovinfoid == null) continue;
				if(layerData.earthquakelayerInfo.tablemasterInfo == null) continue;

				TablemasterInfo tablemasterInfo = layerData.earthquakelayerInfo.tablemasterInfo;
				// 作業済なら何もしない
				if(deletedLayers.contains(tablemasterInfo.layerid)) continue;

				// ecomユーザ名
				UserInfo userInfo = searchEcomUserInfo(mapDB, layerData.earthquakelayerInfo.localgovinfoid);

				// 削除対象のレイヤ名を取得
				String layerId = tablemasterInfo.layerid;
				if(org.seasar.framework.util.StringUtil.isEmpty(layerId)) continue;
				// 受信した時のOriginTime
				Timestamp originTime = layerData.origintime;
				LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
				Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
				vecLayerInfo.add(layerInfo);

				// 検索キーワード
				String keywords = "attr0~*" + originTime.toString() + " AND attr6!~*0";
				FeatureResultList resultList = searchEcomFeatureList(tablemasterInfo.mapmasterInfo.mapid, vecLayerInfo, keywords);
				for(int i = 0; i < resultList.countResult(); i++){
					long orgId = 0;
					Timestamp delTimeFrom = null;
					try {
						FeatureResult result = resultList.getResult(i);
						orgId = result.featureId;
						// 属性情報
						HashMap<String, String> attributes = new HashMap<String, String>();
						int cntAttrSum = result.countAttrResult();
						for(int k = 0; k < cntAttrSum; k++){
							attributes.put(result.getAttrResult(k).getAttrId(), result.getAttrResult(k).getAttrValue());
						}
						// 最大震度は0にする
						attributes.put("attr6", "0");
						// 図形削除をする更新時間を作成（削除するわけではないのでtime_toではなくtime_from）24h後のutc_timeを設定
						delTimeFrom = new Timestamp(TimeUtil.newUTCDate(originTime.getTime() + 24 * 60 * 60 * 1000).getTime());
						attributes.put("time_from", delTimeFrom.toString());
						// 更新する
						FeatureDB.insertFeatureHistory(layerInfo, /*orgId*/orgId, null, attributes, userInfo);
					} catch(Exception e) {
						logger.error("ERROR EarthquakeLayer_OldData deleted ; _orgId="+orgId+" delTimeFrom="+delTimeFrom, e);
					}
				}
				// 削除したレイヤを登録
				deletedLayers.add(layerId);
				logger.info("EarthquakeLayer_OldData deleted -> " + layerId);

				/*
				// Ver1系と同様、マップコピーの為、災害マップがあれば削除する（ただしVer1の仕様は災害マップの自動削除はOFF）
				if(tablemasterInfo.mapmasterInfo.copy){
					// 災害マップ
					List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(layerData.earthquakelayerInfo.localgovinfoid, false);
					for(TrackData td : trackDatas){
						//Trackmapを探す
						TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
						TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo.id, td.id);
						if(ttbl == null) continue;

						vecLayerInfo = new Vector<LayerInfo>();
						layerInfo = mapDB.getLayerInfo(ttbl.layerid);
						vecLayerInfo.add(layerInfo);

						// 検索キーワード
						keywords = "attr0~*" + originTime.toString();
						resultList = searchEcomFeatureList(tmapInfo.mapid, vecLayerInfo, keywords);
						for(int i = 0; i < resultList.countResult(); i++){
							FeatureResult result = resultList.getResult(i);
							long orgId = result.featureId;
							// 属性情報
							HashMap<String, String> attributes = new HashMap<String, String>();
							int cntAttrSum = result.countAttrResult();
							for(int k = 0; k < cntAttrSum; k++){
								attributes.put(result.getAttrResult(k).getAttrId(), result.getAttrResult(k).getAttrValue());
							}
							// 図形削除をする更新時間を作成（削除するわけではないのでtime_toではなくtime_from）24h後のutc_timeを設定
							Date delTimeFrom = TimeUtil.newUTCDate(originTime.getTime() + 24 * 60 * 60 * 1000);
							attributes.put("time_from", delTimeFrom.toString());
							// 更新する
							FeatureDB.insertFeatureHistory(layerInfo, orgId, null, attributes, userInfo);
						}
						// 複数同時であってもマップは１つなのでここでやめる
						break;
					}
				}
				// 訓練マップの対応（ただしVer1の仕様は災害マップの自動削除はOFF）
				List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(layerData.earthquakelayerInfo.localgovinfoid, true);
				for(TrackData td : trainingDatas){
					//Trackmapを探す
					TrackmapInfo tmapInfo = trackmapInfoService.findById(td.trackmapinfoid);
					TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo.id, td.id);
					if(ttbl == null) continue;

					vecLayerInfo = new Vector<LayerInfo>();
					layerInfo = mapDB.getLayerInfo(ttbl.layerid);
					vecLayerInfo.add(layerInfo);

					// 検索キーワード
					keywords = "attr0~*" + originTime.toString();
					resultList = searchEcomFeatureList(tmapInfo.mapid, vecLayerInfo, keywords);
					for(int i = 0; i < resultList.countResult(); i++){
						FeatureResult result = resultList.getResult(i);
						long orgId = result.featureId;
						// 属性情報
						HashMap<String, String> attributes = new HashMap<String, String>();
						int cntAttrSum = result.countAttrResult();
						for(int k = 0; k < cntAttrSum; k++){
							attributes.put(result.getAttrResult(k).getAttrId(), result.getAttrResult(k).getAttrValue());
						}
						// 図形削除をする更新時間を作成（削除するわけではないのでtime_toではなくtime_from）24h後のutc_timeを設定
						Date delTimeFrom = TimeUtil.newUTCDate(originTime.getTime() + 24 * 60 * 60 * 1000);
						attributes.put("time_from", delTimeFrom.toString());
						// 更新する
						FeatureDB.insertFeatureHistory(layerInfo, orgId, null, attributes, userInfo);
					}
					// 複数同時であってもマップは１つなのでここでやめる
					break;
				}
				*/
			}

		}catch(Exception e){
		logger.error(e.getMessage(), e);
		}
	}

	/** 言語メッセージを初期化する。*/
	protected void initMessage() {
		langMap.put("AREA_NAME"	, lang.__("District name"));
		langMap.put("SCALE"		, lang.__("Seismic intensity"));
		langMap.put("JAPAN_REFERENCE_SYSTEM", lang.__("Tokyo Datum"));

		langMap.put("M_S"	, "m/s");
		langMap.put("KM"	, "km");
		langMap.put("KM_H"	, "km/h");

		langMap.put("VERY_SHALLOW", lang.__("Very shallow"));	//5kmより浅い
	}

	/**
	 * 確定震度のXMLファイルを解析し、DBへ登録する。
	 * @param	strXml	分析するXMLデータ
	 * @return	1: 成功、0: 失敗
	 */
	private int analyzeKakuteishindoXML(File fileXml) {

		//メインプロセッシング
		int i, j, k, l;
		String str = null;
		String arrivalTime = null;
		String name = getXML_PARAM_UNKNOWN();
		String magnitude = getXML_PARAM_UNKNOWN();
		double depth = -1;
		double x = -370, y = -370;
		@SuppressWarnings("unused")
		String referenceSystem = JAPAN_REFERENCE_SYSTEM;

		// 最大震度による取得可否
		String maxQuake = "1";

		try {
			// ファイルの読み込み
			Document doc = new SAXBuilder().build( fileXml );
			Element elRoot = doc.getRootElement();

			//空間名リスト作成
			listNamespace.add(NAMESPACE_JMX);
			listNamespace.add(NAMESPACE_JMX_BASIC);
			listNamespace.add(NAMESPACE_JMX_SEIS);
			listNamespace.add(NAMESPACE_JMX_EB);

			List<Element> lstPrefs = null;
			List<Element> lstAreas = null;
			List<Element> lstCities = null;
			List<Element> lstStations = null;

			//地震情報EventID
			String strXPath = "/jmx:Report/jmx_basic:Head/jmx_basic:EventID";
			Element elEventID = selectElement(elRoot, strXPath, listNamespace);
			if (elEventID != null) {
				eventId = (String)elEventID.getValue();
			}

			// 地震情報発信時間
			strXPath = "/jmx:Report/jmx_basic:Head/jmx_basic:ReportDateTime";
			Element elDateTime = selectElement(elRoot, strXPath, listNamespace);
			if (elDateTime != null) {
				reportdateTime = getTimestamp(elDateTime.getValue()).toString();
			}

			//地震発生時刻
			strXPath = "/jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:OriginTime";
			Element elOriginTime = selectElement(elRoot, strXPath, listNamespace);
			if (elOriginTime != null) {
				originTime = getTimestamp(elOriginTime.getValue()).toString();
			}

			//地震発現時刻
			strXPath = "/jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:ArrivalTime";
			Element elArrivalTime = selectElement(elRoot, strXPath, listNamespace);
			if (elArrivalTime != null) {
				arrivalTime = getTimestamp(elArrivalTime.getValue()).toString();
			}

			//震央地名(Name)
			strXPath = "/jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_seis:Name";
			Element elHypocenterName = selectElement(elRoot, strXPath, listNamespace);
			if (elHypocenterName != null) {
				if (!elHypocenterName.getValue().equals(""))
					name = elHypocenterName.getValue();
			}

			//震央点の位置情報、震源の深さ(Coordinate)
			strXPath = "/jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_eb:Coordinate";
			Element elHypocenterCoord = selectElement(elRoot, strXPath, listNamespace);
			if (elHypocenterCoord != null) {
				// +30.3+131.4+40000/
				str = elHypocenterCoord.getValue();
				if (str.length() > 0) {
					// 最後の文字（/）を除外
					str = str.substring(0, str.length() - 1);
					double[] doubles = getLatLonDepth(str);
					x = doubles[0]; y = doubles[1]; depth = doubles[2];
					if (elHypocenterCoord.getAttributeValue("datum") != null && elHypocenterCoord.getAttributeValue("datum").equals(langMap.get("JAPAN_REFERENCE_SYSTEM")))
						referenceSystem = JAPAN_REFERENCE_SYSTEM;
					else
						referenceSystem = WORLD_REFERENCE_SYSTEM;
				}
			}

			//震央点のマグニチュード
			strXPath = "/jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_eb:Magnitude";
			Element elMagnitude = selectElement(elRoot, strXPath, listNamespace);
			if (elMagnitude != null) {
				magnitude = elMagnitude.getValue();

				if (magnitude.equals("NaN") || magnitude.length() == 0) {
					magnitude = getXML_PARAM_UNKNOWN();
				}
				else {
					try {
						@SuppressWarnings("unused")
						double f = Double.parseDouble(magnitude);
					}
					catch (NumberFormatException ex) {
						magnitude = getXML_PARAM_UNKNOWN();
					}
				}
			}

            //----- 地震の詳細情報(earthquakelayer_data)を生成する -----

			// 経度緯度
			String quakeArea = "";
			if (x == -370 || y == -370) {
				quakeArea += getXML_PARAM_UNKNOWN();
			}
			else {
				if (y > 0)
					quakeArea += String.format(formatPosNorth, y);
				else
					quakeArea += String.format(formatPosSouth, -y);
				quakeArea += "  ";
				if (x > 0)
					quakeArea += String.format(formatPosEast, x);
				else
					quakeArea += String.format(formatPosWest, -x);
			}

			// 日付＋震央地名＋マグニチュードの形式で生成
			if(originTime == null){
				earthQuakeLayerName = lang.__("No info of occurrence time") + " " + name + " M" + magnitude;
			}else{
				earthQuakeLayerName = String.format(formatDateTime, originTime.substring(5, 7), originTime.substring(8, 10), originTime.substring(11, 13), originTime.substring(14, 16));
				earthQuakeLayerName += " " + name + " M" + magnitude;
			}

			// 震央点のWKTを作成
			String quakeCenterWKT = "POINT(" + x + " " + y + ")";

			// 震央情報
			earthQuakeLayerAttrList.add(new EarthQuakeLayerAttr(originTime, arrivalTime, name, quakeArea + " " + lang.__("Depth of hypocenter") + " " + (int)(depth / 1000) + "km", magnitude, "-", "-", lang.__("Epicenter, seismic intensity info (municipalities, etc.)"), quakeCenterWKT, eventId, "0000000"));

			// 都道府県
			strXPath = "/jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Observation/jmx_seis:Pref";
			lstPrefs = selectElements(elRoot, strXPath, listNamespace);
			if (lstPrefs.size() > 0) {
				for (i = 0; i < lstPrefs.size(); i++) {
					Element elPref = lstPrefs.get(i);
					// 地域
					strXPath = "jmx_seis:Area";
					lstAreas = selectElements(elPref, strXPath, listNamespace);

					if (lstAreas.size() > 0) {
						for (j = 0; j < lstAreas.size(); j++) {
							Element elArea = lstAreas.get(j);
							@SuppressWarnings("unused")
							String areaName = "";

							Element elAreaName = selectElement(elArea, "jmx_seis:Name", listNamespace);
							if (elAreaName != null)
								areaName = elAreaName.getValue();

							//市区町村
							strXPath = "jmx_seis:City";
							lstCities = selectElements(elArea, strXPath, listNamespace);

							for (k = 0; k < lstCities.size(); k++) {
								Element elCity = lstCities.get(k);
								String cityInformation = "";
								String cityName = "";
								String quakeNum = "";
								String code = "";

								Element elCityName = selectElement(elCity, "jmx_seis:Name", listNamespace);
								if (elCityName != null)
									cityName = (String)elCityName.getValue();

								Element elCode = selectElement(elCity, "jmx_seis:Code", listNamespace);
								if (elCode != null)
									code = elCode.getValue();

								Element elMaxInt = selectElement(elCity, "jmx_seis:MaxInt", listNamespace);
								if (elMaxInt != null)
									quakeNum = maxQuakeToString((String)elMaxInt.getValue());

								//codeで市町村を検索する。
								MeteoareainformationcityMaster cityInfo = meteoareainformationcityMasterService.findByCityCode(code);

								if (cityInfo != null) {

									// 観測点
									strXPath = "jmx_seis:IntensityStation";
									lstStations = selectElements(elCity, strXPath, listNamespace);

									for (l = 0; l < lstStations.size(); l++) {
										Element elStation = lstStations.get(l);
										String stationName = "";
										String stationInt = "";

										Element elName = selectElement(elStation, "jmx_seis:Name", listNamespace);
										if (elName != null)
											stationName = (String)elName.getValue();

										Element elInt = selectElement(elStation, "jmx_seis:Int", listNamespace);
										if (elInt != null)
											stationInt = (String)elInt.getValue();

										// 震度５弱以上未入電 というものが過去に出て取得に失敗していたので対処
										if(stationInt.indexOf(lang.__("Seismic intensity")) == 0){
											stationInt = stationInt.substring(2, stationInt.length());
											quakeNum = quakeNum.equals("") ? stationInt : quakeNum;
										}
										cityInformation += stationName + " ： " + lang.__("Seismic intensity") + stationInt;
									}
									//地震が発生した市町村をDBへ格納する（quake_city）
									earthQuakeLayerAttrList.add(new EarthQuakeLayerAttr(originTime, arrivalTime, name, quakeArea, magnitude, cityInfo.name, quakeNum, cityInformation, cityInfo.point, eventId, cityInfo.code));
								}
								else {
									logger.error(lang.__("Applicable city:{0}, code:{1} not registered.", cityName, code));
								}
								// 地震の最大震度を判定する
								if(compMaxQuake(maxQuake, quakeNum)){
									maxQuake = quakeNum;
								}
							}
						}
					}
				}
			}

			// 震度が設定より低かったら0で返却する
			if(compMaxQuake(maxQuake, EARTHQUAKE_MINIMAM_INT)){
				return 0;
			}
			// レイヤ名称に最大震度を追加
			earthQuakeLayerName += " " + lang.__("Maximum seismic intensity") + maxQuakeToString(maxQuake);

			return 1;
		} catch (Exception ex) {
			logger.error(lang.__("Error occurred in processing of XML data.") + ex.getMessage());
			return -1;
		}

	}

	/**
	 * XMLに記載される日時文字列を分析する。
	 * @param	strTime		日時文字列。例： 2012-08-01T12:00:00+09:00 (日本時間)
	 * @return	日時値
	 */
	protected Timestamp getTimestamp(String strTime) {
		strTime = strTime.substring(0, 10) + " " + strTime.substring(11, 19);
		Timestamp time = Timestamp.valueOf(strTime);
		return time;
	}

	/**
	 * 震源の位置情報と深さの文字列を分析する。<br/>
	 * +30.3+131.4-40000	--> [30.3,131.4,40000]<br/>
	 * +30.3-131.4-10000	--> [30.3,-131.4,10000] <br/>
	 * +30.3+131.4			--> [30.3,131.4,-1] (深さ不明) <br/>
	 * +30.3+131.4+0		--> [30.3,131.4,0] (ごく浅い)<br/>
	 *  (BLANK)				--> [-370,-370,-1] (震源要素不明)<br/>
	 * ※注意：<br/>
	 * １．入力の順番は緯度、経度、深さ<br/>
	 * ２．入力の経度緯度は「世界測地系」（EPSG:4326）と「日本測地系」（EPSG:4612）の経度緯度ですが、この２つの基準の精度はほぼ同一なので、全て「世界測地系」で処理しています。<br/>
	 *
	 * @param	strPosition		震源の位置情報と深さの文字列
	 * @return	３つdouble値の列： 緯度、経度、深さ
	 */
	protected double[] getLatLonDepth(String strPosition) {
		double[] tokens = new double[3];
		int i = 1, i1 = 1;

		//2番目の-/+を探す
		while (i < strPosition.length() && (!strPosition.substring(i, i + 1).equals("-") && !strPosition.substring(i, i + 1).equals("+")))
			i++;

		if (i1 < strPosition.length()) {
			i1 = i + 1;
			//3番目の-/+を探す
			while (i1 < strPosition.length() && (!strPosition.substring(i1, i1 + 1).equals("-") && !strPosition.substring(i1, i1 + 1).equals("+")))
				i1++;
			tokens[1] = Double.parseDouble(strPosition.substring(0, i));
			tokens[0] = Double.parseDouble(strPosition.substring(i, i1));
			if (i1 < strPosition.length())
				tokens[2] = Double.parseDouble(strPosition.substring(i1));
			else
				tokens[2] = -1;
		}
		else {
			tokens[0] = tokens[1] = tokens[2] = -1;
		}

		return tokens;
	}


	/**
	 * 時系列2.0系の処理
	 * 震度レイヤを時系列レイヤとして作成し、tablemasterに追加
	 * ただし、mapmasterinfoid=-1ならレイヤを新設するのみ。
	 * @return
	 */
	//public EarthquakelayerData insertEarthQuakeHistoryLayer(String name, String description, long mapid, Long meteodataid, EarthquakegrouplayerData grouplayer, long localgovinfoid, long mapmasterinfoid){
	public EarthquakelayerData insertEarthQuakeHistoryLayer(String name, String description, long mapid, Long meteodataid, long localgovinfoid, long mapmasterinfoid, boolean isTimeDimension){
		EarthquakelayerData earthquakelayerData = null;
		try {
			MapDB mapDB = MapDB.getMapDB();
			MapInfo mapInfo = mapDB.getMapInfo(mapid);

			//レイヤ種別(登録情報レイヤ)
			short type = LayerInfo.TYPE_LOCAL;

			String layerId;
			LayerInfo newLayerInfo;
			// 属性情報
			List<AttrInfo> attrInfoList = new ArrayList<AttrInfo>();
			for(int i = 0; i < 9; i++){
				String attrname = "";
				int attrLength = 20;
				switch(i){
				case 0: attrname = lang.__("Quake occurrence time<!--2-->"); break;
				case 1: attrname = lang.__("Quake occurrence time"); break;
				case 2: attrname = lang.__("Epicenter"); break;
				case 3: attrname = lang.__("Earthquake location"); break;
				case 4: attrname = lang.__("Magnitude"); break;
				case 5: attrname = lang.__("Place name"); break;
				case 6: attrname = lang.__("Maximum seismic intensity"); break;
				case 7: attrname = lang.__("Info<!--5-->"); attrLength = 200; break;
				case 8: attrname = lang.__("Municipal code"); break;
				default : break;
				}
				if(attrname.equals("")) continue;
				AttrInfo attrInfo = new AttrInfo("attr" + i, attrname, AttrInfo.STATUS_DEFAULT, attrLength, 0/*maxLength*/, AttrInfo.DATATYPE_TEXT, ""/*dataExp*/, true/*nullable*/);
				attrInfoList.add(attrInfo);
			}
			short layerGroupType = LayerInfo.getGroupType(type);

			//パラメータ読み込み
			boolean visible = false;//初期非表示
			boolean closed = true;//初期折りたたむ
			float opacity = 1.0f;//透明度
			int scale = 0;
			int maxScale = 0;

			// ecomユーザ名
			UserInfo userInfo = searchEcomUserInfo(mapDB, localgovinfoid);

			// 登録するレイヤIDを取得
			layerId = mapDB.createFeatureTableName(LayerInfo.getLayerPrefix(mapid, layerGroupType));
			//新規作成(時系列無しで一旦作成)
			// レイヤ名称は震源震度レイヤで固定化する
			//newLayerInfo = createLocalLayerInfo(layerId, name, description, LayerInfo.STATUS_DEFAULT, userInfo.userId, mapid, "", 0, 0);
			newLayerInfo = createLocalLayerInfo(layerId, lang.__("Epicenter layer"), description, LayerInfo.STATUS_DEFAULT, userInfo.userId, mapid, "", 0, 0);
			//_layerにLayerInfoの情報追加
			mapDB.insertLayerInfo(newLayerInfo);

			//地図情報に追加
			//MapLayerInfo newMapLayerInfo = new MapLayerInfo(newLayerInfo.layerId, newLayerInfo.name, newLayerInfo.type, grouplayer.layerid, scale, maxScale, null/*attrNameId*/);
			// グループレイヤの設定はしない
			MapLayerInfo newMapLayerInfo = new MapLayerInfo(newLayerInfo.layerId, newLayerInfo.name, newLayerInfo.type, null, scale, maxScale, null/*attrNameId*/);
			newMapLayerInfo.visible = visible;
			newMapLayerInfo.opacity = opacity;
			newMapLayerInfo.closed = closed;
			// 最大震度項目をラベルにする
			newMapLayerInfo.attrNameId = "attr6";

			// 属性情報の追加
			for(int i = 0; i < attrInfoList.size(); i++){
				// c***レイヤのattributeカラム追加
				newLayerInfo.addAttrInfo(attrInfoList.get(i));
				// _attrレイヤのattributeカラム追加
				mapDB.insertAttrInfo(newLayerInfo, attrInfoList.get(i));
			}

			mapDB.insertMapLayerInfo(mapInfo, newMapLayerInfo);
			mapDB.updateMapInfoModified(mapInfo, new Timestamp(TimeUtil.newDate().getTime()));
			mapDB.updateLayerInfo(newLayerInfo);

			// Geoserverへの登録
			//<div lang="ja">PostGISテーブル生成</div>
			//<div lang="en">create a PostGIS table</div>
			mapDB.createGeometryTable(newLayerInfo, "POINT");
			mapDB.createGeoServerFeature(newLayerInfo.layerId);

			// マスタマップへの設定なら追加
			if(mapmasterinfoid != -1 && !isTimeDimension){
				// earthquakelayer_dataに登録
				earthquakelayerData = new EarthquakelayerData();
				earthquakelayerData.meteodataid = meteodataid;
				//earthquakelayerData.earthquakegrouplayerid = null;
				// 時系列レイヤ統合化のみグローバル変数各種がnullままなので対応
				earthquakelayerData.eventid = eventId == null ? "" : eventId;
				earthquakelayerData.origintime = originTime == null ? null : Timestamp.valueOf(originTime);
				earthquakelayerData.reportdatetime = reportdateTime == null ? null : Timestamp.valueOf(reportdateTime);
				earthquakelayerData.layerid = layerId;
				earthquakelayerData.name = earthQuakeLayerName == null ? "" : earthQuakeLayerName;
				earthquakelayerDataService.insert(earthquakelayerData);
			}

			// SLDのパスを設定ファイルから取得
			ResourceBundle bundle1 = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
			ResourceBundle bundle2 = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
			String sldCommunityFilePath = bundle2.getString("MAPDIR") + bundle1.getString("STYLE_PATH") + "c" + mapInfo.communityId + "/" + layerId + ".sld";
			String sldMapFilePath = bundle2.getString("MAPDIR") + bundle1.getString("STYLE_PATH") + mapInfo.mapId + "/" + layerId + ".sld";
			// SLDをtemplateで書き換える
			String baseSLDURL = application.getRealPath(EARTH_QUAKE_SLD_TEMPLATE_PATH) + EARTH_QUAKE_SLD_TEMPLATE_FILENAME;
			File baseSLDFile = new File(baseSLDURL);
			if(baseSLDFile.exists()){
				FileUtils.copyFile(baseSLDFile, new File(sldCommunityFilePath));
				FileUtils.copyFile(baseSLDFile, new File(sldMapFilePath));
			}
			// 時系列化
			newLayerInfo.timeSeriesType = TimeSeriesType.HISTORY;
			mapDB.setTimeSeriesType(newLayerInfo);
			mapDB.updateLayerInfo(newLayerInfo);

			// edit_userカラムの追加
			trackdataidAttrService.alterTableAddTrackdataidColumnIfNotExists(layerId);
			edituserAttrService.alterTableAddEdituserColumnIfNotExists(layerId);

			//<div lang="ja">サイトとグループに追加</div>
			LayerGroupInfo.insert(mapInfo.communityId, mapInfo.groupId, layerId, LayerGroupInfo.SHARE_ALL);

			// マスタマップへの設定なら追加
			if(mapmasterinfoid != -1){
				EarthquakelayerInfo earthquakelayerInfo = createEarthquakelayerInfo(mapmasterinfoid, layerId, localgovinfoid);

				/*
				// tablemaster_infoに追加
				TablemasterInfo tablemasterInfo = new TablemasterInfo();
				tablemasterInfo.mapmasterinfoid = mapmasterinfoid;
				tablemasterInfo.layerid = layerId;
				tablemasterInfo.tablename = layerId;
				tablemasterInfo.name = lang.__("Epicenter layer");
				tablemasterInfo.geometrytype = "POINT";
				tablemasterInfo.copy = TablemasterInfo.COPY_LATEST;
				tablemasterInfo.addresscolumn = "";
				tablemasterInfo.updatecolumn = "";
				tablemasterInfo.coordinatecolumn = "";
				tablemasterInfo.mgrscolumn = "";
				tablemasterInfo.mgrsdigit = 4;
				tablemasterInfo.note = "";
				tablemasterInfo.deleted = false;
				tablemasterInfo.reset = false;
				tablemasterInfoService.insert(tablemasterInfo);

				// 震度レイヤ設定に入れる
				EarthquakelayerInfo earthquakelayerInfo = new EarthquakelayerInfo();
				earthquakelayerInfo.localgovinfoid = localgovinfoid;
				earthquakelayerInfo.tablemasterinfoid = tablemasterInfo.id;
				earthquakelayerInfo.tablemasterInfo = tablemasterInfo;
				earthquakelayerInfoService.insert(earthquakelayerInfo);
				*/

				// infoidをlayerdataに入れる
				if(!isTimeDimension){
					earthquakelayerData.earthquakelayerinfoid = earthquakelayerInfo.id;
					earthquakelayerData.earthquakelayerInfo = earthquakelayerInfo;
					earthquakelayerDataService.update(earthquakelayerData);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return earthquakelayerData;
	}

	@SuppressWarnings("deprecation")
	//public boolean earthquakeTimeDimension(MapDB mapDB, Long mapmasterinfoid, List<TrackmapInfo> trackmapinfos, Long localgovinfoid) throws Exception{
	public boolean earthquakeTimeDimension(MapDB mapDB, Long localgovinfoid) throws Exception{
		logger.info("Start EarthquakeLayer TimeDimension");
		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);

		// すでに時系列版の震度レイヤ情報(earthquakelayer_info)を持っているか確認
		List<EarthquakelayerInfo> earthquakelayerInfos = earthquakelayerInfoService.findByLocalgovinfoid(localgovinfoid, false);
		if(earthquakelayerInfos.size() > 0){
			// すでに作成している震度レイヤにマージする？
			return true;
		}

		// 自自治体の所有している震度グループを検索(マスタマップと災害マップ)
		List<EarthquakegrouplayerData> master_grouplayers = earthquakegrouplayerDataService.findByMapMasterInfoId(mapmasterInfo.id, false);
		List<EarthquakegrouplayerData> track_grouplayers = earthquakegrouplayerDataService.findByMapMasterInfoId(mapmasterInfo.id, true);

		if(master_grouplayers.size() == 0 && track_grouplayers.size() == 0){
			// グループレイヤは存在しないが、マップで持っている可能性はあるので削除
			MapInfo masterMapInfo = mapDB.getMapInfo(mapmasterInfo.mapid);
			v1_EarthquakelayerDelete(mapDB, masterMapInfo);
			return true;
		}
		// meteodataidがマスタと災害マップで重複するので、meteodataidが被らないように収集する
		List<Long> addedMeteoId = new ArrayList<Long>();
		List<EarthquakelayerData> addedLayerDatas = new ArrayList<EarthquakelayerData>();
		// マスタマップ
		for(EarthquakegrouplayerData group: master_grouplayers){
			if(group == null) continue;
			List<EarthquakelayerData> layerDatas = earthquakelayerDataService.findByEarthGroupId(group.id);
			if(layerDatas.size() == 0) continue;
			for(EarthquakelayerData layer : layerDatas){
				if(!addedMeteoId.contains(layer.meteodataid)){
					addedMeteoId.add(layer.meteodataid);
					layer.earthquakegrouplayerData = group;
					addedLayerDatas.add(layer);
				}
			}
		}
		// 災害マップ
		for(EarthquakegrouplayerData group: track_grouplayers){
			if(group == null) continue;
			List<EarthquakelayerData> layerDatas = earthquakelayerDataService.findByEarthGroupId(group.id);
			if(layerDatas.size() == 0) continue;
			for(EarthquakelayerData layer : layerDatas){
				if(!addedMeteoId.contains(layer.meteodataid)){
					addedMeteoId.add(layer.meteodataid);
					layer.earthquakegrouplayerData = group;
					addedLayerDatas.add(layer);
				}
			}
		}
		// ecommapのUserInfoを検索する
		UserInfo userInfo = searchEcomUserInfo(mapDB, localgovinfoid);

		// 新規でEarthquakelayer_infoを作成してデータを格納していく
		int cnt = 0;
		// 新規で入れた場合、orgIdを格納するList key=citycode, value=orgid
		HashMap<String, Long> orgIdMap = new HashMap<String, Long>();

		EarthquakelayerInfo earthquakelayerInfo = new EarthquakelayerInfo();

		// 時系列レイヤ
		LayerInfo timeLayerInfo = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// 統合先となる時系列レイヤ
		Vector<LayerInfo> vecMasterLayerInfo = new Vector<LayerInfo>();

		for(EarthquakelayerData layer : addedLayerDatas){
			if(cnt == 0){
				//MapmasterInfo mapInfo = mapmasterInfoService.findById(mapmasterinfoid);
				//EarthquakelayerData earthquakelayerData = insertEarthQuakeHistoryLayer("震度レイヤ", "", mapmasterInfo.mapid, layer.meteodataid, localgovinfoid, mapmasterInfo.id, true);
				// 戻り値はNullなので受け取らない
				insertEarthQuakeHistoryLayer(lang.__("Seismic intensity layer"), "", mapmasterInfo.mapid, layer.meteodataid, localgovinfoid, mapmasterInfo.id, true);
				//earthquakelayerInfo = earthquakelayerData.earthquakelayerInfo;
				earthquakelayerInfos = earthquakelayerInfoService.findByLocalgovinfoid(localgovinfoid, false);
				// 存在しない時は何かしら上手くいっていない状態
				if(earthquakelayerInfos.size() == 0) return false;
				earthquakelayerInfo = earthquakelayerInfos.get(0);
				earthquakelayerInfo.tablemasterInfo = tablemasterInfoService.findById(earthquakelayerInfo.tablemasterinfoid);

				// 時系列レイヤ
				timeLayerInfo = mapDB.getLayerInfo(earthquakelayerInfo.tablemasterInfo.layerid);
				//earthquakelayerInfoService.insert(earthquakelayerInfo);

				// 最新データかどうか確認する為に必要
				LayerInfo masterLayerInfo = mapDB.getLayerInfo(earthquakelayerInfo.tablemasterInfo.layerid);
				vecMasterLayerInfo.add(masterLayerInfo);
			}else{
				// 今の最新データより新しいか確認
				String keywords_center = CITY_CODE_ATTR+"~*0000000 AND attr6!~*0";
				FeatureResultList nowQuakeCenter = searchEcomFeatureList(mapmasterInfo.mapid, vecMasterLayerInfo, keywords_center);
				if(nowQuakeCenter.total > 0){
					// 何かしらの地震が出ている状態
					// 発生時間を格納
					String nowQuakeOriginTime = nowQuakeCenter.getResult(0).getAttrResult("attr0").getAttrValue();
					if(isAfterOriginTime(sdf, nowQuakeOriginTime, layer.origintime.toString())){

						// 冗長な処理も出てくるが、現在のレイヤの地物データの図形を削除して更新する
						// time_fromに1秒追加して、図形削除処理を行う
						// 現在のデータを取得
						keywords_center = "attr6!~*0";
						FeatureResultList now_resultList = searchEcomFeatureList(mapmasterInfo.mapid, vecMasterLayerInfo, keywords_center);
						for(int i = 0; i < now_resultList.countResult(); i++){
							FeatureResult result = now_resultList.getResult(i);
							if(result == null){
								logger.error("result is null");
								continue;
							}
							Timestamp time_from = result.metaInfo.time_from;
							if(time_from == null){
								// 無い事自体仕様上ありえない
								String originTime = result.getAttrResult("attr0").getAttrValue();
								Date timefromDate = sdf.parse(originTime);
								// UTC対応
								time_from = new Timestamp(TimeUtil.newUTCDate(timefromDate.getTime()).getTime());
							}
							// 属性情報
							HashMap<String, String> attributes = new HashMap<String, String>();
							int cntAttrSum = result.countAttrResult();
							for(int k = 0; k < cntAttrSum; k++){
								attributes.put(result.getAttrResult(k).getAttrId(), result.getAttrResult(k).getAttrValue());
							}
							// 最大震度は0にする
							attributes.put(CITY_CODE_ATTR, "0");
							// 図形削除をする更新時間を作成
							Timestamp delTimeFrom = new Timestamp(time_from.getTime() + 1);
							attributes.put("time_from", delTimeFrom.toString());
							// 図形無しで再登録
							FeatureDB.insertFeatureHistory(vecMasterLayerInfo.get(0), result.featureId, null, attributes, userInfo);
						}
					}
				}
			}
			// 統合元のレイヤで震度が0でない地物を検索する
			String keywords = "attr6!~*0";
			Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
			LayerInfo layerInfo = mapDB.getLayerInfo(layer.layerid);
			vecLayerInfo.add(layerInfo);

			long searchMapId = layer.earthquakegrouplayerData.trackmapinfoid == null ? layer.earthquakegrouplayerData.mapmasterInfo.mapid : layer.earthquakegrouplayerData.trackmapInfo.mapid;
			FeatureResultList resultList = searchEcomFeatureList(searchMapId, vecLayerInfo, keywords);
			// 属性情報
			String attr0 = "";
			String attr1 = "";
			String attr2 = "";
			String attr3 = "";
			String attr4 = "";
			String attr5 = "";
			String attr6 = "";
			String attr7 = "";
			String attr8 = "";

			for(int i = 0; i < resultList.countResult(); i++){
				FeatureResult result = resultList.getResult(i);
				if(result == null){
					logger.error("result is null");
					continue;
				}
				// 属性情報を取得
				attr0 = result.getAttrResult("attr0").getAttrValue();
				attr1 = result.getAttrResult("attr1").getAttrValue();
				attr2 = result.getAttrResult("attr2").getAttrValue();
				attr3 = result.getAttrResult("attr3").getAttrValue();
				attr4 = result.getAttrResult("attr4").getAttrValue();
				attr5 = result.getAttrResult("attr5").getAttrValue();
				attr6 = result.getAttrResult("attr6").getAttrValue();
				attr7 = result.getAttrResult("attr7").getAttrValue();

				//logger.info(searchMapId + "_" + attr0 + " : " + attr5 + " : " + result.getWKT());

				if(attr0.equals("")) continue;
				// Ver2から新設したカラムなので、図形から取得したい市町村コードを検索する
				if(result.getWKT() != null){
					MeteoareainformationcityMaster pointMaster = meteoareainformationcityMasterService.findByPointWKT(result.getWKT());
					// 該当無しは震央
					attr8 = pointMaster == null ? "0000000" : pointMaster.code;
				}
				// EventIDは地物の登録に必要ない
				HashMap<String, String> attributes = new EarthQuakeLayerAttr(attr0, attr1, attr2, attr3, attr4, attr5, attr6, attr7, result.getWKT(), "" /*eventID*/, attr8).toHashMap();

				// 新規登録(マスタマップのレイヤに登録)
				if(cnt == 0 || !orgIdMap.containsKey(attr8)){
					long insertFid = FeatureDB.insertFeature(userInfo, earthquakelayerInfo.tablemasterInfo.layerid, result.getWKT(), attributes);
					// 市町村コードをキーとしてorgIdを保存
					orgIdMap.put(attr8, insertFid);
				}
				// 履歴更新
				else{
					try{
						long updateFid = FeatureDB.insertFeatureHistory(timeLayerInfo, orgIdMap.get(attr8), result.getWKT(), attributes, userInfo);
						if(updateFid == 0) logger.warn("EarthquakeService.insertHistory -> new Fid = 0");

					}catch(EcommapConflictException e){
						// 同一時間の震度レイヤがあるとコンフリクトするので、コンフリクトしたら次の震度レイヤ処理に遷移させる
						// 基本的に、v1 -> v2では起きない。起きるパターンは、震度レイヤ情報が作られた後の統合。
						logger.warn("EarthquakeService.insertHistory Conflict layerid = " + timeLayerInfo.layerId);
						break;
					}
				}
			}
			// 統合したlayerDataをVer2用のレコードに更新(layeridも更新してもよいが、tablemasterInfoから取得するので意味はない)
			layer.earthquakelayerinfoid = earthquakelayerInfo.id;
			layer.name = layerInfo.name;
			earthquakelayerDataService.update(layer);

			cnt++;
		}
		// 訓練マップに複製 + 不要なレイヤの削除フラグ更新
		earthquakeLayerClone(mapDB, localgovinfoid, mapmasterInfo, userInfo);

		// 不要なレイヤの削除
		MapInfo masterMapInfo2 = mapDB.getMapInfo(mapmasterInfo.mapid);
		v1_EarthquakelayerDelete(mapDB, masterMapInfo2);

		return true;
	}

	/**
	 * 訓練マップにマスタで作成した震度レイヤを複製する
	 * @param mapDB
	 * @param localgovinfoid : 作成する自治体
	 * @param mapmasterInfo : マスタマップ
	 * @param userInfo
	 */
	public void earthquakeLayerClone(MapDB mapDB, long localgovinfoid, MapmasterInfo mapmasterInfo, UserInfo userInfo){

		// 出来上がったマスタマップの震度レイヤを訓練マップに登録する
		List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid, true);
		if(trainingDatas.size() == 0){
			logger.info("No TrainingMap");
			return;
		}
		// 時系列版の震度レイヤ情報(earthquakelayer_info)を持っているか確認。無いなら複製対象がいないので何もしない
		List<EarthquakelayerInfo> earthquakelayerInfos = earthquakelayerInfoService.findByLocalgovinfoid(localgovinfoid, false);
		if(earthquakelayerInfos.size() == 0){
			logger.info("No EarthquakelayerInfo");
			return;
		}
		// テーブルマスタの存在確認
		TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(earthquakelayerInfos.get(0).tablemasterinfoid);
		if(tablemasterInfo == null){
			logger.warn("EarthquakelayerInfo is TablemasterInfo is null!!");
			return;
		}
		// コピー元レイヤID
		String srcLayerId = tablemasterInfo.layerid;
		// マスタマップ
		MapInfo srcMapInfo = mapDB.getMapInfo(mapmasterInfo.mapid);
		// フィーチャコピーモード
		boolean cloneFeatures = true;
		// 全ての訓練マップを対象とする
		for(TrackData track : trainingDatas){
			try {
				// 訓練マップ
				TrackmapInfo trackmapInfo = trackmapInfoService.findById(track.trackmapinfoid);
				MapInfo trainingMapInfo = mapDB.getMapInfo(trackmapInfo.mapid);

				int track_cid = trainingMapInfo.communityId;
				int track_groupid = trainingMapInfo.groupId;
				int master_cid = srcMapInfo.communityId;
				int newOwnerId = userInfo.userId;
				String cloneLayerId = mapDB.cloneLayer(track_cid, track_groupid, trainingMapInfo, master_cid, mapmasterInfo.mapid, srcLayerId, newOwnerId, cloneFeatures);

				// tracktableinfoに追加
				TracktableInfo tracktableInfo = new TracktableInfo();
				tracktableInfo.trackmapinfoid = trackmapInfo.id;
				tracktableInfo.tablemasterinfoid = tablemasterInfo.id;
				tracktableInfo.layerid = cloneLayerId;
				tracktableInfo.tablename = cloneLayerId;
				tracktableInfoService.insert(tracktableInfo);
				// 不要なレイヤの削除
				v1_EarthquakelayerDelete(mapDB, trainingMapInfo);

			} catch(Exception e) {
				throw new ServiceException(lang.__("Failed to copy layer."), e);
			}
		}
	}

	/**
	 * V1で作成した震度レイヤと震度グループレイヤを削除する関数
	 * 名称一致で消すので、同一名称のグループレイヤがあると消すので注意
	 * @param mapDB
	 * @param mapInfo
	 */
	public void v1_EarthquakelayerDelete(MapDB mapDB, MapInfo mapInfo){
		try {
			// マップ内の震度グループとそれに紐付くレイヤを削除(名称固定で削除する)
			for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
				if(mapLayerInfo.getLayerType()!=LayerInfo.TYPE_LOCAL) continue;
				// 日本語では震源震度情報を親に持つかチェック
				if(mapLayerInfo.parent == null) continue;
				MapLayerInfo parentLayer = mapDB.getMapLayerInfo(mapInfo.mapId, mapLayerInfo.parent);
				if(!parentLayer.layerName.equals(lang.__("Epicenter seismic intensity info"))) continue;

				// 削除フラグを立てる
				LayerInfo childLayerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId);
				childLayerInfo.status = LayerInfo.STATUS_DELETED;
				// 更新
				mapDB.updateLayerInfo(childLayerInfo);
				/*
				// 震度レイヤ情報の該当レコードを削除
				List<EarthquakelayerData> layerDatas = earthquakelayerDataService.findByLayerId(mapLayerInfo.layerId);
				for(EarthquakelayerData datas : layerDatas){
					earthquakelayerDataService.delete(datas);
				}
				*/
			}
			// 震度グループを削除(浮いてるものも同一名称なら全て削除)
			for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
				if(mapLayerInfo.getLayerType()!=LayerInfo.TYPE_LOCAL_GROUP) continue;
				// 日本語では震源震度情報という名称かチェック
				if(!mapLayerInfo.layerName.equals(lang.__("Epicenter seismic intensity info"))) continue;

				// 削除フラグを立てる
				LayerInfo groupLayerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId);
				groupLayerInfo.status = LayerInfo.STATUS_DELETED;
				// 更新
				mapDB.updateLayerInfo(groupLayerInfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * layeridで検索
	 * @param layerid
	 * @return boolean true: earthquakelayer_dataで所有する, false : 所有していない
	 */
	public boolean isExistLayerId(String layerid){
		List<EarthquakelayerData> datas = earthquakelayerDataService.findByLayerId(layerid);
		return datas.size() > 0;
	}

	/**
	 * 震度レイヤ一覧からレイヤIDのリストを返却する
	 * 管理画面の自治体セットアッパー時系列化メニュー用
	 * @param layerid
	 * @return list
	 */
	public List<String> earthquakeLayerDataList(){
		List<EarthquakelayerData> datas = earthquakelayerDataService.findAll();
		List<String> layers = new ArrayList<String>();
		for(EarthquakelayerData data : datas){
			if(StringUtil.isNotEmpty(data.layerid) )
				layers.add(data.layerid);
		}
		return layers;
	}
	/**
	 * 震度レイヤ設定からTablemasterInfoを検索し、震度レイヤIDを返却する
	 * MapActionのタイムスライダー対応で利用
	 * @param localgovinfoid : 対象自治体ID
	 * @param isTraining : 地図複製がある訓練モードならtrue
	 * @return layerid (自治体には基本的に1件)
	 */
	public String earthquakeLayerId(Long localgovinfoid, Long trackdataid){
		EarthquakelayerInfo earthquakelayerInfo = earthquakelayerInfoService.findByLocalgovinfoId(localgovinfoid);
		// 取得出来なければ空文字
		if(earthquakelayerInfo == null || earthquakelayerInfo.tablemasterInfo == null) return "";
		// 災害起動中ならtracktableinfoからlayeridを取得する
		TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(earthquakelayerInfo.tablemasterinfoid, trackdataid);
		if(ttbl != null) return ttbl.layerid;
		// そうじゃないならTablemasterInfoから取得
		return earthquakelayerInfo.tablemasterInfo.layerid;
	}
	/**
	 * ecommmapの編集権限を持つユーザのUserInfoを返す
	 * @param mapDB
	 * @param localgovinfoid : 所属自治体ID
	 * @return UserInfo
	 */
	public UserInfo searchEcomUserInfo(MapDB mapDB, long localgovinfoid){
		UserInfo userInfo = null;
		String ecomuser = "";
		List<GroupInfo> groupinfo = groupInfoService.findByLocalgovInfoIdAndValid(localgovinfoid);
		for(GroupInfo group : groupinfo){
			// 表示順の一番若いレコードから検索して、管理者権限ユーザがいれば使用 (システム管理者adminで実施)
			if(group.admin && !group.deleted){
				ecomuser = group.ecomuser;
				break;
			}
		}
		if(ecomuser.equals("")) ecomuser = ECOM_ADMIN_AUTH_ID;
		userInfo = mapDB.getAuthIdUserInfo(ecomuser);
		return userInfo;
	}

	/**
	 * Create seismic intensity layer information + set seismic intensity layer in table master
	 * @param mapmasterinfoid : マスタマップ
	 * @param layerId : 震度レイヤID
	 * @param localgovinfoid : 対象自治体ID
	 * @return
	 */
	public EarthquakelayerInfo createEarthquakelayerInfo(long mapmasterinfoid, String layerId, long localgovinfoid){
		// tablemaster_infoに追加
		TablemasterInfo tablemasterInfo = new TablemasterInfo();
		tablemasterInfo.mapmasterinfoid = mapmasterinfoid;
		tablemasterInfo.layerid = layerId;
		tablemasterInfo.tablename = layerId;
		tablemasterInfo.name = lang.__("Epicenter layer");
		tablemasterInfo.geometrytype = "POINT";
		tablemasterInfo.copy = TablemasterInfo.COPY_LATEST;
		tablemasterInfo.addresscolumn = "";
		tablemasterInfo.updatecolumn = "";
		tablemasterInfo.coordinatecolumn = "";
		tablemasterInfo.mgrscolumn = "";
		tablemasterInfo.mgrsdigit = 4;
		tablemasterInfo.note = "";
		tablemasterInfo.deleted = false;
		tablemasterInfo.reset = false;
		tablemasterInfoService.insert(tablemasterInfo);

		// 震度レイヤ設定に入れる
		EarthquakelayerInfo earthquakelayerInfo = new EarthquakelayerInfo();
		earthquakelayerInfo.localgovinfoid = localgovinfoid;
		earthquakelayerInfo.tablemasterinfoid = tablemasterInfo.id;
		earthquakelayerInfo.tablemasterInfo = tablemasterInfo;
		earthquakelayerInfoService.insert(earthquakelayerInfo);

		return earthquakelayerInfo;
	}

	/**
	 * 文字列の時刻を1秒前にするor後ろにする
	 * @param sdf : simpledataformat("yyyy-MM-dd HH:mm:ss")
	 * @param time_from : 対象となる文字列日時
	 * @param plus : true->足す, false->戻す
	 * @return
	 */
	public String convertTimeFrom(SimpleDateFormat sdf, String time_from, boolean plus){
		// time_fromの設定があれば1秒追加して格納する
		if(time_from == null || time_from.equals("")){
			return "";
		}else{
			Date timeFrom;
			try {
				timeFrom = sdf.parse(time_from);
				Date utcTimeFrom = TimeUtil.newUTCDate(timeFrom.getTime());
				return sdf.format(utcTimeFrom.getTime()+(plus?1000L:-1000L));
			} catch (ParseException e) {
				logger.error(e);
			}
		}
		return "";
	}

	public class EarthQuakeLayerAttr{
		// 地震発生日時
		String originTime;
		// 地震発現時刻
		String arrivalTime;
		// 震源地
		String name;
		// 震源位置
		String area;
		// マグニチュード
		String magnitude;
		// 観測地名（都道府県＋市町村名）
		String prefCity;
		// 最大観測震度
		String maxInt;
		// 情報（震源は固定文、観測した市町村は観測点）
		String information;
		// wkt
		String wkt;
		// eventID
		String eventID;
		// 市町村コード
		String cityCode;
		// 時系列パラメータ
		String time_from;

		/**
		 * 通常の登録用に利用するコンストラクタ
		 * time_fromはoriginTimeと同値
		 * @param originTime
		 * @param arrivalTime
		 * @param name
		 * @param area
		 * @param magnitude
		 * @param prefCity
		 * @param maxInt
		 * @param information
		 * @param wkt
		 * @param eventID
		 * @param cityCode
		 */
		public EarthQuakeLayerAttr(String originTime, String arrivalTime, String name, String area, String magnitude, String prefCity, String maxInt, String information, String wkt, String eventID, String cityCode){
			this.originTime = originTime;
			this.arrivalTime = arrivalTime;
			this.name = name;
			this.area = area;
			this.magnitude = magnitude;
			this.prefCity = prefCity;
			this.maxInt = maxInt;
			this.information = information;
			this.wkt = wkt;
			this.eventID = eventID;
			this.cityCode = cityCode;
			this.time_from = originTime;
			// 普通に考えて空文字は有り得ないが念の為
			if(StringUtils.isNotEmpty(originTime)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date timeFrom;
				try {
					timeFrom = sdf.parse(originTime);
					Date utcTimeFrom = TimeUtil.newUTCDate(timeFrom.getTime());
					this.time_from = sdf.format(utcTimeFrom);
				} catch (ParseException e) {
					// エラーの場合はoriginTimeのまま
				}
			}
		}

		/**
		 * 図形削除用のコンストラクタ
		 * @param sdf : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * @param cityCode : 市町村コード
		 * @param time_from : 履歴を入れる時間文字列
		 * @param plus : time_fromに1秒足すならtrue, 1秒減らすならfalse
		 */
		public EarthQuakeLayerAttr(SimpleDateFormat sdf, HashMap<String,String> attributes, String cityCode, String time_from, boolean plus){
			this.originTime = "";
			this.arrivalTime = "";
			this.name = "";
			this.area = "";
			this.magnitude = "";
			this.prefCity = "";
			// 震央地点対応
			this.maxInt = cityCode.equals("000000") ? "-" : "0";
			this.information = "";
			this.wkt = "";
			this.eventID = "";
			this.cityCode = cityCode;
			// time_fromの設定があれば1秒追加して格納する
			if(time_from == null || time_from.equals("")){
				this.time_from = originTime;
			}else{
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date timeFrom;
				try {
					timeFrom = sdf.parse(time_from);
					Date utcTimeFrom = TimeUtil.newUTCDate(timeFrom.getTime());
					this.time_from = sdf.format(utcTimeFrom.getTime()+(plus?1000L:-1000L));
				} catch (ParseException e) {
					// エラーの場合はoriginTimeにしておく
					this.time_from = originTime;
				}
			}
		}

		public HashMap<String, String> toHashMap(){
			HashMap<String, String> hashmap = new HashMap<String, String>();
			hashmap.put("attr0", this.originTime);
			hashmap.put("attr1", this.arrivalTime);
			hashmap.put("attr2", this.name);
			hashmap.put("attr3", this.area);
			hashmap.put("attr4", this.magnitude);
			hashmap.put("attr5", this.prefCity);
			hashmap.put("attr6", this.maxInt);
			hashmap.put("attr7", this.information);
			hashmap.put(CITY_CODE_ATTR, this.cityCode); // attr8
			hashmap.put("time_from", this.time_from);
			return hashmap;
		}

		public String getOriginTime(){ return this.originTime; }
		public String getArrivalTime(){ return this.arrivalTime; }
		public String getName(){ return this.name; }
		public String getArea(){ return this.area; }
		public String getMagnitude(){ return this.magnitude; }
		public String getPrefCity(){ return this.prefCity; }
		public String getMaxInt(){ return this.maxInt; }
		public String getInformation(){ return this.information; }
		public String getWKT(){ return this.wkt; }
		public String getEventID(){ return this.eventID; }
		public void setOriginTime(String originTime){ this.originTime = originTime; }
		public void setArrivalTime(String arrivalTime){ this.arrivalTime = arrivalTime; }
		public void setName(String name){ this.name = name; }
		public void setArea(String area){ this.area = area; }
		public void setMagnitude(String magnitude){ this.magnitude = magnitude; }
		public void setPrefCity(String prefCity){ this.prefCity = prefCity; }
		public void setMaxInt(String maxInt){ this.maxInt = maxInt; }
		public void setInformation(String information){ this.information = information; }
		public void setWKT(String wkt){ this.wkt = wkt; }
		public void setEventID(String eventID){ this.eventID = eventID; }
	}

	/**
	 * <div lang="ja">
	 * グループ用LayerInfo生成。
	 * @see #LayerInfo(String, String, String, int, int, int, long, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, int, int)
	 * @return グループレイヤ用のメンバ変数が設定されたレイヤ情報
	 * </div>
	 *
	 * <div lang="en">
	 * Generate a LayerInfo for group.
	 * @see #LayerInfo(String, String, String, int, int, int, long, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, int, int)
	 * @return the layer information with the member variable for group layer
	 * </div>
		 * @throws SQLException
	 */
	static public LayerInfo createGroupLayerInfo(String layerId, String name, String description, int status, int type, int ownerId, long ownerMapId) throws SQLException
	{
		return new LayerInfo(layerId, name, description, status, type, ownerId, ownerMapId, null, layerId,
				null, null, null, null, null, null, null, null, null, null, null, null, TimeSeriesType.NONE, 0, 0, 0, null, null);
	}

		/**
	 * <div lang="ja">
	 * コンテンツレイヤ用LayerInfo生成。
	 * @see #LayerInfo(String, String, String, int, int, int, long, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, int, int)
	 * @return コンテンツレイヤ用のメンバ変数が設定されたレイヤ情報
	 * </div>
	 *
	 * <div lang="en">
	 * Genarate a LayerInfo for contents layer.
	 * @see #LayerInfo(String, String, String, int, int, int, long, String, String, String, String, String, String, String, String, String, String, String, String, Timestamp, Timestamp, int, int)
	 * @return the layer information with the member variable set for contents layer
	 * </div>
	 */
	static public LayerInfo createLocalLayerInfo(String layerId, String name, String description, int status, int ownerId, long ownerMapId, String attribution, int scale, int maxScale) throws SQLException
	{
		return new LayerInfo(layerId, name, description, status, LayerInfo.TYPE_LOCAL, ownerId, ownerMapId, attribution, layerId,
				null, null, null, null, null, null, null, null, null, null, null, null, TimeSeriesType.HISTORY, scale, maxScale, 0, null, null);
	}

	/**
	 * XPATHを準備する。
	 *
	 * @param 	xml		Elementオブジェクト
	 * @param	xpath	XPath 文字列
	 * @param	theNSs	XPathに含むネームスペースリスト
	 * @return	XPathExpressionオブジェクトを返す。
	 */
	private static XPathExpression<Element> prepareXPath(Element xml, String xpath, List<Namespace> theNSs) throws JDOMException
	{
		XPathExpression<Element> xp = XPathFactory.instance().compile(xpath, Filters.element(), null, theNSs);
		return xp;
	}


	/**
	 * XPATHで１つのオブジェクトを検索
	 *
	 * @param 	xml		Elementオブジェクト
	 * @param	xpath	XPath 文字列
	 * @param	theNSs	XPathに含むネームスペースリスト
	 * @return	Objectとして検索結果を返す。
	 */
	public static Object selectSingle(Element xml, String xpath, List<Namespace> theNSs) throws JDOMException {
		XPathExpression<Element> xp = prepareXPath(xml, xpath, theNSs);
		return xp.evaluateFirst(xml);
	}


	/**
	 * XPATHで複数のエレメントを検索
	 *
	 * @param 	xml		Elementオブジェクト
	 * @param	xpath	XPath 文字列
	 * @param	theNSs	XPathに含むネームスペースリスト
	 * @return	エレメントリストとして検索結果を返す。
	 */
	public static List<Element> selectElements(Element xml, String xpath, List<Namespace> theNSs) throws JDOMException {
		XPathExpression<Element> xp = prepareXPath(xml, xpath, theNSs);
		return xp.evaluate(xml);
	}


	/**
	 * XPATHで１つのエレメントを検索
	 *
	 * @param 	xml		Elementオブジェクト
	 * @param	xpath	XPath 文字列
	 * @param	theNSs	XPathに含むネームスペースリスト
	 * @return	エレメントとして検索結果を返す。
	 */
	public static Element selectElement(Element xml, String xpath, List<Namespace> theNSs) throws JDOMException {
		Object result = selectSingle(xml, xpath, theNSs);
		if (result == null) {
			return null;
		} else if (result instanceof Element) {
			Element elem = (Element)result;
			return (Element)(elem);
		} else {
			//-- Found something but not an element
			return null;
		}
	}

	/**
	 * 引数1より引数2の方が震度が大きい場合はtrueを返す関数
	 * @param quake1 : 震度
	 * @param quake2 : 震度
	 * @return quake2の方が大きい時true
	 */
	public boolean compMaxQuake(String quake1, String quake2){
		boolean isComp = false;
		switch(quake1){
		case "1":
			if(quake2.equals("2")){ isComp = true; break; }
			if(quake2.equals("3")){ isComp = true; break; }
			if(quake2.equals("4")){ isComp = true; break; }
			if(quake2.equals("5-")){ isComp = true; break; }
			if(quake2.equals("5+")){ isComp = true; break; }
			if(quake2.equals("6-")){ isComp = true; break; }
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "2":
			if(quake2.equals("3")){ isComp = true; break; }
			if(quake2.equals("4")){ isComp = true; break; }
			if(quake2.equals("5-")){ isComp = true; break; }
			if(quake2.equals("5+")){ isComp = true; break; }
			if(quake2.equals("6-")){ isComp = true; break; }
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "3":
			if(quake2.equals("4")){ isComp = true; break; }
			if(quake2.equals("5-")){ isComp = true; break; }
			if(quake2.equals("5+")){ isComp = true; break; }
			if(quake2.equals("6-")){ isComp = true; break; }
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "4":
			if(quake2.equals("5-")){ isComp = true; break; }
			if(quake2.equals("5+")){ isComp = true; break; }
			if(quake2.equals("6-")){ isComp = true; break; }
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "5-":
			if(quake2.equals("5+")){ isComp = true; break; }
			if(quake2.equals("6-")){ isComp = true; break; }
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "5+":
			if(quake2.equals("6-")){ isComp = true; break; }
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "6-":
			if(quake2.equals("6+")){ isComp = true; break; }
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "6+":
			if(quake2.equals("7")){ isComp = true; break; }
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		case "7":
			if(quake2.equals(lang.__("Unknown"))){ isComp = true; break; }
			break;
		}
		return isComp;
	}
	/**
	 * 最大震度を数値化する
	 * @param maxQuake : maxIntの値
	 * @return : 数値化した結果
	 */
	public String maxQuakeToString(String maxQuake){
		// 最大震度をレイヤ名称に震度入れる為に数字化する
		if(maxQuake.equals("1")) return "1";
		if(maxQuake.equals("2")) return "2";
		if(maxQuake.equals("3")) return "3";
		if(maxQuake.equals("4")) return "4";
		if(maxQuake.equals("5-")) return lang.__("lower 5");
		if(maxQuake.equals("5+")) return lang.__("upper 5");
		if(maxQuake.equals("6-")) return lang.__("lower 6");
		if(maxQuake.equals("6+")) return lang.__("upper 6");
		if(maxQuake.equals("7")) return "7";
		if(maxQuake.equals(lang.__("Unknown"))) return lang.__("Unknown");
		if(maxQuake.indexOf(lang.__("Over 5-")) != -1) return lang.__("lower 5");
		if(maxQuake.indexOf(lang.__("Over 5+")) != -1) return lang.__("upper 5");
		if(maxQuake.indexOf(lang.__("Over 6-")) != -1) return lang.__("lower 6");
		if(maxQuake.indexOf(lang.__("Over 6+")) != -1) return lang.__("upper 6");
		return lang.__("Seismic intensity unknown");
	}
}
