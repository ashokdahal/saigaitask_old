/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.DecisionsupportInfo;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoareainformationcityMaster;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.ToolboxData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;
import jp.ecom_plat.saigaitask.service.db.DecisionsupportInfoService;
import jp.ecom_plat.saigaitask.service.db.EarthquakegrouplayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteoareainformationcityMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteolayerInfoService;
import jp.ecom_plat.saigaitask.service.db.ToolboxDataService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 意思決定支援（避難者数推定）機能のアクションクラス
 */


@jp.ecom_plat.saigaitask.action.RequestScopeController
public class DecisionsupportAction extends AbstractPageAction {

	// 意思決定支援サービス
	@Resource protected DecisionsupportInfoService decisionsupportInfoService;
	// 地図マスター
	@Resource protected MapmasterInfoService mapmasterInfoService;
	// 地図サービス
	@Resource protected MapService mapService;
	// 気象情報レイヤサービス
	@Resource protected MeteolayerInfoService meteolayerInfoService;
	// 気象情報グループレイヤサービス
	@Resource protected EarthquakegrouplayerDataService earthquakegrouplayerDataService;
	// 震度情報解析サービス
	@Resource protected MeteoricEarthQuakeService meteoricEarthQuakeService;
	@Resource protected EarthquakelayerDataService earthquakelayerDataService;
	@Resource protected EarthquakelayerInfoService earthquakelayerInfoService;
	// ツールボックスサービス
	@Resource protected ToolboxDataService toolboxDataService;
	// 気象庁エリア名称
	@Resource protected MeteoareainformationcityMasterService meteoareainformationcityMasterService;
	// 編集者ユーザ記録用の st_edituser 属性を登録情報レイヤに追加するサービスクラス
	@Resource protected EdituserAttrService edituserAttrService;


	/** 意思決定支援種別マスタ */
	// 建物被害推定レイヤ種別
	final int DECISIONSUPPORTTYPE_BUILDING = 1;
	// 人口レイヤ種別
	final int DECISIONSUPPORTTYPE_PEOPLE = 2;
	// 停電エリアレイヤ種別 -> ライフライン等の被害エリアレイヤ種別に集約
	final int DECISIONSUPPORTTYPE_POWER_OUTAGE = 3;
	// 断水エリアレイヤ種別 -> ライフライン等の被害エリアレイヤ種別に集約
	final int DECISIONSUPPORTTYPE_WATER_OUTAGE = 4;
	// 自治体の250mメッシュレイヤ
	final int DECISIONSUPPORTTYPE_MESH_LAYER = 5;
	// 建物被害による避難者数レイヤ種別
	final int DECISIONSUPPORTTYPE_BUILDING_EVACUEE = 6;
	// 停電、断水による避難者数推定レイヤ種別
	final int DECISIONSUPPORTTYPE_LIFELINE_EVACUEE = 7;
	// 帰宅困難者による避難者数推定レイヤ種別
	final int DECISIONSUPPORTTYPE_UNABALE_RETURNHOME_EVACUEE = 8;
	// 総避難者数推定レイヤ種別
	final int DECISIONSUPPORTTYPE_TOTAL_EVACUEE = 9;
	// 避難所レイヤ種別
	final int DECISIONSUPPORTTYPE_SHELTER = 10;
	// ライフライン等の被害エリアレイヤ種別
	final int DECISIONSUPPORTTYPE_ETC_AREA_LAYER = 11;


	// 確定震度レイヤの地震発生日時が入っているカラム名
	final String EARTHQUAKELAYER_ORIGINTIME_COLUMN = "attr0";
	// 確定震度レイヤの市町村名（県名＋市町村名）が入っているカラム名
	final String EARTHQUAKELAYER_CITYNAME_COLUMN = "attr5";
	// 確定震度レイヤの自治体の最高震度が入っているカラム名
	final String EARTHQUAKELAYER_QUAKEINT_COLUMN = "attr6";

	/** eコミの管理者権限ユーザID */
	public static final String ECOM_ADMIN_AUTH_ID = "admin";

	// 将来的に何処かでパラメータ化する必要有り
	// 住宅総数（岡崎市の居住世帯有の総数 H25）
	// https://www.e-stat.go.jp/SG1/estat/GL08020103.do?_toGL08020103_&tclassID=000001056227&cycleCode=0&requestSender=search
	//final int HOUSING_TOTAL = 135870;
	// 人口総数 (H27 4/1)
	//final int PEOPLE_TOTAL = 380764;
	// 黄色にする基準(推定避難者数が収容者数の何倍になるか)
	final double ALERT_YELLOW = 1.0;
	// 赤色にする基準(推定避難者数が収容者数の何倍になるか)
	final double ALERT_RED = 3.0;

	// 避難所比率
	double evacuationRate = 0.0;
	// ライフライン困難率
	double lifeLineTrouble_PowerRate = 0.0;
	double lifeLineTrouble_WaterRate = 0.0;
	// 1日
	final long ONEDAY_TIME = 1*24*60*60*1000;
	// 1日(分単位)
	final double ONEDAY_MINTIME = 24*60;
	// 7日
	final long ONEWEEK_TIME = 7*24*60*60*1000;
	// 7日(分単位)
	final double ONEWEEK_MINTIME = 7*24*60;
	// 30日
	final long ONEMONTH_TIME = 30*24*60*60*1000;
	// エラー内容を格納するList
	List<String> errorList = null;

	// ツールボックス機能の人口レイヤ用の種別ID
	final long TOOLBOX_TYPE_PEOPLE = 1;

	// これはクライアントに決めさせる必要がある
	String earthQuakeLayer = "";

	/**
	 * 避難者数推定データの演算
	 * @return : null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/decisionsupport/calculation"}, produces="application/json", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public ResponseEntity<String> calculation(Map<String,Object>model){
		// 二度押し防止トークン
		//TokenProcessor.getInstance().saveToken(SpringContext.getRequest());
		// エラー用のListを初期化
		errorList = new ArrayList<String>();

		JSONObject json = new JSONObject();
		// 出力の準備
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");*/

		// 地図ID
		long mapId = 0L;

		try{
			long start = System.currentTimeMillis();

			// 対象とする地震レイヤ
			String earthquakelayerdataid = request.getParameter("layers");
			if(earthquakelayerdataid != null){
				// ver2はmeteodataidが返ってくる
				earthQuakeLayer = earthquakelayerdataid;
				//errorListAdd("帰宅困難者推計に必要な地震レイヤがありません");
			}
			// 地図IDの取得
			mapId = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid()).mapid;
			if(loginDataDto.getTrackdataid() > 0){
				TrackmapInfo trackmapInfo = trackmapInfoService.findById(loginDataDto.getTrackmapinfoid());
				if(trackmapInfo != null) mapId = trackmapInfo.mapid;
			}
			// 自自治体が訓練モードかどうか確認する
			boolean trainingMode = false;
			if(loginDataDto.getTrackdataid() > 0){
				TrackData nowTrackData = trackDataService.findById(loginDataDto.getTrackdataid());
				// 訓練モード
				if(nowTrackData.trainingplandataid != null){
					trainingMode = true;
				}
			}
			// 現在から災害モード起動日が何日前になるかを演算する
			List<TrackData> trackdatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), trainingMode);
			if(trackdatas == null){
				logger.warn(lang.__("Disaster mode not running."));
				return null;
			}
			// デフォルトは１年前
			Timestamp trackStartTime = new Timestamp(System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000);
			// 複数同時災害時は古い方を正とする
			for(TrackData trackdata : trackdatas){
				Timestamp startTime = trackdata.starttime;
				if(trackStartTime.getTime() > startTime.getTime()){
					trackStartTime = startTime;
				}
			}
			// ここも見直す（これにしか使わなくなったので）
			int afterDisasterDay = afterDays(trackStartTime.getTime());
			// 災害モード起動から1日未満
			if(afterDisasterDay == 0){
				// 避難所率
				evacuationRate = 0.6;
			}
			// 1日経過
			else if(afterDisasterDay == 1){
				evacuationRate = 0.6;
			}
			// 2日経過 + 7日未満
			else if(afterDisasterDay >= 2 && afterDisasterDay < 7){
				// 2日～7日にかけて0.1数字が落ちるので6で割る
				evacuationRate = 0.5 - (0.1 / 6.0 * (afterDisasterDay-1));
			}
			// 1週間経過 + 1ヶ月未満
			else if(afterDisasterDay >= 7 && afterDisasterDay < 30){
				// 7日～30日にかけて0.2数字が落ちるので24で割る
				evacuationRate = 0.5 - (0.2 / 24.0 * (afterDisasterDay-7));
			}
			// それ以降
			else{
				evacuationRate = 0.3;
			}
			logger.info("afterDisasterDay = " + afterDisasterDay);
			logger.info("evacuationRate = " + evacuationRate);

			UserInfo userInfo = null;
			String ecomuser = loginDataDto.getEcomUser();
			if(ecomuser.equals("")) ecomuser = ECOM_ADMIN_AUTH_ID;
			MapDB mapDB = MapDB.getMapDB();
			userInfo = mapDB.getAuthIdUserInfo(ecomuser);

			// 総避難者数レイヤの情報を取得
			DecisionsupportInfo dInfo_TotalEvacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_TOTAL_EVACUEE);
			// 避難所レイヤの情報を取得
			DecisionsupportInfo dInfo_Shelter = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_SHELTER);
			// 人口レイヤの情報を取得
			DecisionsupportInfo dInfo_People = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_PEOPLE);
			// メッシュレイヤの情報を取得
			DecisionsupportInfo dInfo_Mesh = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_MESH_LAYER);

			// 人口レイヤ、避難所レイヤ、総避難者数レイヤの設定が存在しなければ中止する
			if(!decisionsupportInfoService.isNotEmptyParam(dInfo_TotalEvacuee) || !decisionsupportInfoService.isNotEmptyParam(dInfo_Shelter) || !decisionsupportInfoService.isNotEmptyParam(dInfo_People) || !decisionsupportInfoService.isNotEmptyParam(dInfo_Mesh)){
				errorListAdd(lang.__("Canceled to calculate the number of all refugees due to no configurations."));
			}else{
				// 建物被害による避難者数メッシュデータ
				List<PeopleGeomData> buildPeopleList = calculationBuilding(dInfo_People);
				// エリアレイヤ被害による避難者数メッシュデータ
				List<PeopleGeomData> areaLayerPeopleList = calculationLifeLine(trackStartTime.getTime(), userInfo, mapId, dInfo_People);
				// 総避難者数
				calculationTotalEvacuee(buildPeopleList, areaLayerPeopleList, mapId, mapDB, userInfo, dInfo_Shelter, dInfo_Mesh);
			}

			// エラーをJSONで返す
			JSONArray error_arr = new JSONArray();
			for(String err : errorList){
				JSONObject obj = new JSONObject();
				obj.put("msg", err);
				error_arr.put(obj);
			}
			json.put("error", error_arr);

			json.put("status", "OK");
			// 出力
			/*PrintWriter out = response.getWriter();
			out.print( json.toString() );*/

			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

			long end = System.currentTimeMillis();

			logger.info("[CalcTime] " + TimeUnit.MILLISECONDS.toSeconds(end-start) + " s");
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * 建物被害演算用メソッド
	 * return値の内容は大きく以下の通り
	 * -1 : 建物被害演算に必要なレイヤ設定が不足しています
	 * -2 : 建物被害演算に必要なデータが不足しています
	 *  1 : 正常終了
	 * @return : int
	 */
	public List<PeopleGeomData> calculationBuilding(DecisionsupportInfo dInfo_People){

		// 人口データClass
		List<PeopleGeomData> peopleGeomList = new ArrayList<PeopleGeomData>();
		// 建物被害推定レイヤの情報を取得
		DecisionsupportInfo dInfo_Building = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_BUILDING);
		// 各種パラメータが設定されているかチェック
		if(!decisionsupportInfoService.isNotEmptyParam(dInfo_Building)){
			errorListAdd(lang.__("Canceled to calculate the number of refugees suffered from building damage due to no configuration."));
			return peopleGeomList;
		}
		try {
			// コピーフラグtrue対応
			String buildingLayerID = dInfo_Building.tablemasterInfo.layerid;
			String peopleLayerID = dInfo_People.tablemasterInfo.layerid;
			if(loginDataDto.getTrackdataid() > 0){
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo_Building.tablemasterinfoid, loginDataDto.getTrackdataid());
				if(ttbl != null) buildingLayerID = ttbl.layerid;
				ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo_People.tablemasterinfoid, loginDataDto.getTrackdataid());
				if(ttbl != null) peopleLayerID = ttbl.layerid;
			}

			// 人口データカラム
			String peopleAttr = dInfo_People.attrid1;
			// 世帯数データカラム
			String houseHoldAttr = dInfo_People.attrid2;

			// 建物被害推定のデータを取得
			List<BeanMap> result_building = tableService.selectAll(buildingLayerID);
			List<BuildingGeomData> buildingDataList = new ArrayList<BuildingGeomData>();
			// 全壊カラム
			String zenkaiAttr = dInfo_Building.attrid1;
			// 半壊カラム
			String hankaiAttr = dInfo_Building.attrid2;
			// データを格納する
			for(BeanMap map : result_building){
				if(map.containsKey(zenkaiAttr) && map.containsKey(hankaiAttr) && map.containsKey("theGeom")){
					buildingDataList.add( new BuildingGeomData((String)map.get(zenkaiAttr), (String)map.get(hankaiAttr), map) );
				}
			}

			// 人口レイヤデータを取得
			List<BeanMap>result = tableService.selectAll(peopleLayerID);
			for(BeanMap map : result){
				if(map.containsKey(peopleAttr) && map.containsKey(houseHoldAttr) && map.containsKey("theGeom")){
					// String に変換
					String wkt = map.get("theGeom").toString();
					// SRID=4326; のような文字列がくっついてくるようになったので削除する
					if(wkt.indexOf(";") != -1){
						wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
					}
					WKTReader reader = new WKTReader();
					Geometry peopleGeom = reader.read(wkt);
					Point peopleCenter = peopleGeom.getCentroid();

					for(int i = 0; i < buildingDataList.size(); i++){
						BuildingGeomData build = buildingDataList.get(i);
						if(build.isIntersects(peopleCenter)){
							try{
								// 演算
								// 計算式 : （避難率100%×全壊棟数＋避難率13%×半壊棟数）× メッシュ内人口 / 世帯数 ×避難所比率
								//double evacueeD = (build.zenkai_per * (double)data.setai + 0.13 * build.hankai_per * (double)data.setai) * (double)data.people * evacuationRate;
								// 計算式 : （避難率100%×全壊棟率＋避難率13%×半壊棟率）× メッシュ内人口 × 避難所比率
								String peopleStr = (String)map.get(peopleAttr);
								double evacueeD = (build.zenkai_per + 0.13 * build.hankai_per) * Double.parseDouble(peopleStr) * evacuationRate;
								// 値が小さいの小数点第二位で四捨五入してみる
								evacueeD = Math.round(evacueeD * 100) / 100;

								if(evacueeD >= 1){
									// 人口Classに格納
									PeopleGeomData data = new PeopleGeomData(evacueeD, peopleGeom);
									peopleGeomList.add(data);
									// 取得した建物被害データを外す
									//buildingDataList.remove(i);
								}
								break;
							}catch(NumberFormatException e){
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return peopleGeomList;
	}

	/**
	 * ライフライン被害による避難者数演算用メソッド
	 * return値の内容は大きく以下の通り
	 * -1 : ライフライン被害演算に必要なレイヤ設定が不足しています
	 * -2 : ライフライン被害演算に必要なデータが不足しています
	 *  1 : 正常終了
	 * @return : int
	 */
	public List<PeopleGeomData> calculationLifeLine(long trackStartTime, UserInfo userInfo, long mapId, DecisionsupportInfo decisionsupportInfo_People){

		// 人口データClass
		List<PeopleGeomData> peopleGeomList = new ArrayList<PeopleGeomData>();

		// ライフライン等の被害エリアレイヤデータ取得(複数想定)
		List<DecisionsupportInfo> etcAreaLayerInfos = decisionsupportInfoService.findByLocalgovinfoIdAndTypeIdList(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_ETC_AREA_LAYER);
		// ver1の対応を考えて、停電エリアレイヤの情報を取得
		List<DecisionsupportInfo> decisionsupportInfo_PowerOutage = decisionsupportInfoService.findByLocalgovinfoIdAndTypeIdList(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_POWER_OUTAGE);
		// ver1の対応を考えて、断水レイヤの情報を取得
		List<DecisionsupportInfo> decisionsupportInfo_WaterOutage = decisionsupportInfoService.findByLocalgovinfoIdAndTypeIdList(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_WATER_OUTAGE);

		// ライフライン等のデータ管理用
		List<DecisionsupportInfo> lifeLineInfos = new ArrayList<DecisionsupportInfo>();
		// 正常に設定が入っていれば演算対象とする
		for(DecisionsupportInfo decisionsupportInfo : etcAreaLayerInfos){
			if(checkDecisionLayer(decisionsupportInfo)){
				lifeLineInfos.add(decisionsupportInfo);
			}
		}
		for(DecisionsupportInfo decisionsupportInfo : decisionsupportInfo_PowerOutage){
			if(checkDecisionLayer(decisionsupportInfo)){
				lifeLineInfos.add(decisionsupportInfo);
			}
		}
		for(DecisionsupportInfo decisionsupportInfo : decisionsupportInfo_WaterOutage){
			if(checkDecisionLayer(decisionsupportInfo)){
				lifeLineInfos.add(decisionsupportInfo);
			}
		}
		if(lifeLineInfos.size() == 0){
			// errorList.add(lang.__("The process is skipped because proper area layer info records are not found."));
			errorList.add(lang.__("The process is skipped because proper area layer info records are not found."));
			return peopleGeomList;
		}

		/*
		// ライフライン被害による避難者数レイヤの情報を取得
		DecisionsupportInfo decisionsupportInfo_LifeLineEvacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_LIFELINE_EVACUEE);
		// Null または 必要な設定が入っていなければ終了する
		if(!checkDecisionLayer(decisionsupportInfo_LifeLineEvacuee) || !checkDecisionLayer(decisionsupportInfo_People)){
			return -1;
		}
		*/

		try{
			// 人口レイヤのレイヤID
			String peopleLayerID = "";
			// ライフライン被害推計の1次演算結果格納用のレイヤID
			//String lifeLineEvacueeLayerID = "";
			// 災害モードならTracktable_infoから取得
			if(loginDataDto.getTrackdataid() != 0){
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(decisionsupportInfo_People.tablemasterinfoid, loginDataDto.getTrackdataid());
				if(ttbl != null){
					peopleLayerID = ttbl.layerid;
				}
				//ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(decisionsupportInfo_LifeLineEvacuee.tablemasterinfoid, loginDataDto.getTrackdataid());
				//if(ttbl != null) lifeLineEvacueeLayerID = ttbl.layerid;
			}

			// エリアレイヤの図形データ等のClassをList形式で格納する
			List<EtcAreaLayerGeomData> etcAreaLayerList = new ArrayList<EtcAreaLayerGeomData>();

			// エリアレイヤを順に解析し、最後にライフライン被害推計レイヤに格納
			for(DecisionsupportInfo dInfo : lifeLineInfos){
				// エリアレイヤのレイヤ情報を取得する(コピーフラグtrueのレイヤを想定。無いならマスタのものを使用)
				String etcAreaLayerId = dInfo.tablemasterInfo.layerid;
				// 災害モードならTracktable_infoから取得
				if(loginDataDto.getTrackdataid() != 0){
					TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo.tablemasterinfoid, loginDataDto.getTrackdataid());
					if(ttbl != null) etcAreaLayerId = ttbl.layerid;
				}
				// 演算式解析した避難率
				double methodParam = etcAreaLayerMethod(dInfo.calculation_method, trackStartTime);

				// 描画されているポリゴンを全取得する
				List<BeanMap> result_AreaPolygon = tableService.selectAll(etcAreaLayerId);
				// コンストラクタで演算式とBufferを格納しておく
				EtcAreaLayerGeomData etcAreaData = new EtcAreaLayerGeomData(methodParam, dInfo.buffer==null?0:dInfo.buffer);
				// 図形をエリアレイヤ用のClassに格納
				for(BeanMap map : result_AreaPolygon){
					etcAreaData.insertGeomData(map);
				}
				etcAreaLayerList.add(etcAreaData);

/*
				for(Geometry test : etcAreaData.geomList){
					// デバッグ用にレイヤに書き込む
					HashMap<String, String> attributes = new HashMap<String, String>();
					attributes.put("attr0",String.valueOf(etcAreaData.buffer));
					//FeatureDB.insertFeature(userInfo, "c352", test.toText(), attributes);
					String wkts = test.toText();
					if(wkts.indexOf("MULTI") == -1){
						wkts = "MULTIPOLYGON(" + wkts.substring(7, wkts.length()) + ")";
					}
					FeatureDB.insertFeature(userInfo, "c261", wkts, attributes);
				}
*/
			}

			/*
			// 避難者数格納前に、ライフライン被害による避難者数レイヤのデータをクリア(時系列レイヤ的に問題がないか確認する)
			if(tableService.getCount(lifeLineEvacueeLayerID) > 0){
				tableService.deleteAllNoResetSequence(lifeLineEvacueeLayerID);
			}
			*/

			// 人口レイヤのポリゴン単位で重なるエリアレイヤを検索し、最も避難率の高い演算を採用し２次レイヤに書き込む
			// 人口レイヤのデータを全取得する(最新のみ)
			List<BeanMap> result_people = tableService.selectAll(peopleLayerID);
			// 人口データカラム
			String peopleAttr = decisionsupportInfo_People.attrid1;

			for(BeanMap map : result_people){
				if(map.containsKey("theGeom")){
					if(map.get("theGeom") != null){
						// String に変換
						String wkt = map.get("theGeom").toString();
						// SRID=4326; のような文字列がくっついてくるようになったので削除する
						if(wkt.indexOf(";") != -1){
							wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
						}
						WKTReader reader = new WKTReader();
						Geometry peopleGeom = reader.read(wkt);

						// 各エリアレイヤと重なるかチェックして最大の避難率を求める
						double methodParam = 0.0;
						for(EtcAreaLayerGeomData etcAreaLayer : etcAreaLayerList){
							if(etcAreaLayer.isIntersects(peopleGeom)){
								// 避難率が高ければ入れ替え
								methodParam = methodParam < etcAreaLayer.calc ? etcAreaLayer.calc : methodParam;
								continue;
							}
						}
						if(methodParam > 0){
							// 避難者数を計算する
							String peopleStr = map.containsKey(peopleAttr) ? (String)map.get(peopleAttr) : "0";
							double evacueeD = 0;
							try{
								// 演算
								evacueeD = Double.parseDouble(peopleStr) * methodParam * evacuationRate;
								evacueeD = Math.round(evacueeD * 100) / 100.0;

								if(evacueeD >= 1){
									// 人口Classに格納
									PeopleGeomData data = new PeopleGeomData(evacueeD, peopleGeom);
									peopleGeomList.add(data);

									/*
									// デバッグ用にレイヤに書き込む
									HashMap<String, String> attributes = new HashMap<String, String>();
									attributes.put("attr0",String.valueOf(evacueeD));
									attributes.put("attr1",String.valueOf(methodParam));
									FeatureDB.insertFeature(userInfo, "c262", wkt, attributes);
									*/
								}
							}catch(NumberFormatException e){
								// 登録をしない
							}
						}
						/*
						// ライフライン被害による避難者数レイヤに登録する
						HashMap<String, String> attributes = new HashMap<String, String>();
						attributes.put(decisionsupportInfo_LifeLineEvacuee.attrid1, String.valueOf((int)evacueeD));
						FeatureDB.insertFeature(userInfo, lifeLineEvacueeLayerID, wkt, attributes);
						*/
					}
				}
			}


				/*
				// ポリゴンを格納するList
				List<Geometry> areaPolygonList = new ArrayList<Geometry>();
				for(BeanMap map : result_AreaPolygon){
					if(map.containsKey("theGeom")){
						if(map.get("theGeom") != null){
							// String に変換
							String wkt = map.get("theGeom").toString();
							// SRID=4326; のような文字列がくっついてくるようになったので削除する
							if(wkt.indexOf(";") != -1){
								wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
							}
							WKTReader reader = new WKTReader();
							Geometry areaGeom = reader.read(wkt);
							areaGeom.buffer(dInfo.buffer);

							// 空間検索用のレイヤ情報を設定する
							JSONArray spatialLayer = null;
							JSONObject obj = new JSONObject();
							// ST_Intersects
							obj.put("type", 1);
							obj.put("layer", etcAreaLayerId);
							spatialLayer.put(obj);

							// sessionを渡すと、入力範囲のレイヤが追加されてしまい、SQL構文がおかしくなるため null を渡す.
							FeatureResultList featureList = FeatureDB.searchFeatureSessionGeometry(null, mapId, vecLayerInfo, AttrInfo.STATUS_DEFAULT,
									null, 0, 0, true, FeatureDB.GEOM_TYPE_GEOM, null, false, false, SessionGeometry.TYPE_CONDITION, 0, (double)dInfo.buffer, spatialLayer, null, null, new Date[]{TimeUtil.newDate()});

							for(int i = 0; i < featureList.total; i++){
								FeatureResult result = featureList.getResult(i);
								// Geometryを取得
								Geometry geom = result.getGeometry();
								if(geom == null) continue;

								// 人口取得
								AttrResult attrResult = result.getAttrResult(decisionsupportInfo_People.attrid1);
								String peopleStr = attrResult.getAttrValue();

								// 避難率が高いかどうかを判断して高い場合は更新する
								if(!findByLifeLineData(lifeLineList, geom.getCentroid(), methodParam)){
									// 該当メッシュにデータ未格納ならそのまま入れる
									lifeLineList.add( new LifeLineGeomData(Double.parseDouble(peopleStr), geom, geom.getCentroid(), methodParam) );
								}
							}
						}
					}
				}
				*/

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return peopleGeomList;
	}

	/**
	 * ライフライン（従来の浸水と停電対応用）
	 * あとは全体の処理を色々と直す必要があるのでテスト用
	 */
	public boolean checkDecisionLayer(DecisionsupportInfo decisionsupportInfo){

		// ライフライン被害による避難者数レイヤの情報を取得
		//DecisionsupportInfo decisionsupportInfo = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), decision_typeid);
		if(decisionsupportInfo == null){
			errorListAdd(lang.__("Canceled to calculate the number of refugees suffered from utility damage due to no configuration. of its calculation."));
			return false;
		}
		// 必要な設定があるかチェック
		if(!decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo)){
			errorListAdd(lang.__("Canceled to calculate the number of refugees suffered from utility damage due to no configuration. of its calculation."));
			return false;
		}
		// 従来の停電or断水被害の設定で、設定がなければ対応
		if(decisionsupportInfo.decisionsupporttypeid == DECISIONSUPPORTTYPE_POWER_OUTAGE || decisionsupportInfo.decisionsupporttypeid == DECISIONSUPPORTTYPE_WATER_OUTAGE){
			// Ver2.0の高度化に応じた設定があるか確認
			if(StringUtil.isNotEmpty(decisionsupportInfo.calculation_method)){
				// 関数文字列
				String dMethod = decisionsupportInfo.calculation_method;
				// 00d00h00:parameterの形式なので、コロンがなければ中止
				if(dMethod.indexOf(":") == -1) return false;
			}
			// 無い場合はVer1.4の固定値で埋める
			else{
				// 停電
				if(decisionsupportInfo.decisionsupporttypeid == DECISIONSUPPORTTYPE_POWER_OUTAGE){
					decisionsupportInfo.calculation_method = "1d:0,7d:0.25,30d:0.5";
					decisionsupportInfo.buffer = 0;
				}
				// 断水
				else{
					decisionsupportInfo.calculation_method = "1d:0,7d:0.25,30d:0.9";
					decisionsupportInfo.buffer = 0;
				}
			}
		}
		return true;
	}

	/**
	 * 各レイヤの演算式
	 * @param str
	 * @param trackStartTime
	 * @return
	 */
	public double etcAreaLayerMethod(String str, long trackStartTime){
		if(str == null) return -1;
		if(str.indexOf(":") == -1) return -1;

		long defMSECTime = System.currentTimeMillis() - trackStartTime;
		if(defMSECTime < 0) return -1;
		// defTime はmsecなので分にする
		long defMINTime = TimeUnit.MILLISECONDS.toMinutes(defMSECTime);

		double method_double = 0.0;

		// 前回の演算閾値時間を記録する変数
		long beforeMethodTime = 0;
		double beforeMethodParam = -1;

		// 特定文字列以外を全て弾く
		str = methodStrExclusion(str);

		// コロンで分割
		String[] method_arr = str.split(",");
		for(int i = 0; i < method_arr.length; i++){
			if(method_arr[i].indexOf(":") == -1) continue;

			String methodStr = method_arr[i];
			// 空白を削除する
			methodStr = methodStr.trim();

			try{
				// 分に統一する
				long methodMINTime = 0;

				// d, h, その後で分割
				String[] dayStr = methodStr.split("d");
				if(dayStr.length > 1){
					// dがいくつも定義されているような入力ミスを弾くため、先頭のd以外は何もしない
					methodMINTime += Integer.parseInt(dayStr[0]) * 24 * 60;
					// 配列の最後の要素を格納して次へ
					methodStr = dayStr[dayStr.length-1];
				}
				String[] hourStr = methodStr.split("h");
				if(hourStr.length > 1){
					// hがいくつも定義されているような入力ミスを弾くため、先頭のd以外は何もしない
					methodMINTime += Integer.parseInt(hourStr[0]) * 60;
					// 配列の最後の要素を格納して次へ
					methodStr = hourStr[hourStr.length-1];
				}
				String[] minStr = methodStr.split(":");
				if(minStr.length > 1){
					// hがいくつも定義されているような入力ミスを弾くため、先頭のd以外は何もしない
					methodMINTime += minStr[0].length() == 0 ? 0 : Integer.parseInt(minStr[0]);
					// 配列の最後の要素を格納して次へ
					methodStr = minStr[minStr.length-1];
				}
				// 演算値(避難所率)
				method_double = methodStr.length() > 0 ? Double.parseDouble(methodStr) : 0;

				// 間に含まれているかチェック
				if( (beforeMethodTime < defMINTime) && (defMINTime <= methodMINTime) ){
					// １つ目の演算式なので、そのまま
					if(beforeMethodParam == -1){
						return anxietyAfterShock(defMINTime) + method_double;
					}
					// 間のどの辺りの点になるのかを調査する（前後のポイントを利用した１次関数）
					else{
						// (演算ポイント - 前の演算ポイント)の時間情報
						long diffTime = methodMINTime - beforeMethodTime;
						double diffParam = method_double - beforeMethodParam;
						// 精神的不安による避難率 + (前回ポイントの演算値 * (今の演算値 * 演算日時の間隔に応じた割合))
						return anxietyAfterShock(defMINTime) + (beforeMethodParam - diffParam * ((double)(defMINTime - beforeMethodTime) / diffTime) );
					}
				}else{
					// 今回の演算値と経過日数（単位：分）のデータを格納する
					beforeMethodTime = methodMINTime;
					beforeMethodParam = method_double;
				}
			}catch(NumberFormatException e){
				// 無視する
			}
		}
		// 間となる値がなければ最後の演算結果を返す（最後はそのままの数値で継続扱いなので）
		return method_double;
	}

	/**
	 * 余震への不安等の精神的要因による避難率 当日：10%, 1週間後：0%
	 * @param defMinTime : 災害起動時からの経過時間[m]
	 * @return 避難率
	 */
	public double anxietyAfterShock(long defMinTime){
		// 1日未満は10%
		if(defMinTime < ONEDAY_MINTIME){
			return 0.1;
		}
		// 演算
		else if(defMinTime < ONEWEEK_MINTIME){
			// 1日経過後～7日目までなので、6日間で10%落ちる
			return (0.1 - (0.1 / (ONEWEEK_MINTIME / defMinTime)));
		}
		// 1週間以降は0%
		else{
			return 0;
		}
	}

	/**
	 * 演算式で使える文字以外は全て外す
	 * @param str
	 * @return
	 */
	public String methodStrExclusion(String str){
		if(str == null || str.length() == 0) return str;

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < str.length(); i++){
			switch(str.charAt(i)){
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'd':
			case 'h':
			case 'm':
			case ':':
			case ',':
			case '.':
				sb.append(str.charAt(i));
				break;
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("static-access")
	public int calculationTotalEvacuee(List<PeopleGeomData> buildPeopleList, List<PeopleGeomData> areaLayerPeopleList, long mapId, MapDB mapDB, UserInfo userInfo, DecisionsupportInfo dInfo_Shelter, DecisionsupportInfo dInfo_Mesh){
		// 帰宅困難者を演算するかどうか
		boolean isUnableReturnHomeCalculation = true;
		List<PeopleGeomData> unableReturnPeopleList = new ArrayList<PeopleGeomData>();

		// 帰宅困難者による避難者数レイヤの情報を取得
		DecisionsupportInfo dInfo_UnableReturnHome_Evacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_UNABALE_RETURNHOME_EVACUEE);
		// 総避難者数レイヤの情報を取得
		DecisionsupportInfo dInfo_TotalEvacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_TOTAL_EVACUEE);

		// 各種パラメータが設定されているかチェック
		if(!decisionsupportInfoService.isNotEmptyParam(dInfo_TotalEvacuee)){
			errorListAdd(lang.__("Canceled to calculate the number of all refugees due to no configurations."));
			return -1;
		}
		// 帰宅困難者数レイヤ設定がされていれば取得する
		if(!decisionsupportInfoService.isNotEmptyParam(dInfo_UnableReturnHome_Evacuee)){
			errorListAdd(lang.__("Excluded the number of persons difficult to go back home, because no layer info for the persons needed for all victims calculation."));
			isUnableReturnHomeCalculation = false;
		}

		// コピーフラグtrue対応
		String shelterLayerID = dInfo_Shelter.tablemasterInfo.layerid;
		String totalLayerID = dInfo_TotalEvacuee.tablemasterInfo.layerid;
		String unableReturnHomeLayerID = dInfo_UnableReturnHome_Evacuee.tablemasterInfo.layerid;
		String meshLayerID = dInfo_Mesh.tablemasterInfo.layerid;
		if(loginDataDto.getTrackdataid() > 0){
			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo_Shelter.tablemasterinfoid, loginDataDto.getTrackdataid());
			if(ttbl != null) shelterLayerID = ttbl.layerid;
			ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo_TotalEvacuee.tablemasterinfoid, loginDataDto.getTrackdataid());
			if(ttbl != null) totalLayerID = ttbl.layerid;
			if(isUnableReturnHomeCalculation){
				ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo_UnableReturnHome_Evacuee.tablemasterinfoid, loginDataDto.getTrackdataid());
				if(ttbl != null) unableReturnHomeLayerID = ttbl.layerid;
			}
			ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(dInfo_Mesh.tablemasterinfoid, loginDataDto.getTrackdataid());
			if(ttbl != null) meshLayerID = ttbl.layerid;
		}

		try{
			// 帰宅困難者を集計するのは震度データが必要
			// 対象となる自治体の震度情報を取得する
			String earthQuakeCityStr = "0";
			// 市町村名を取得する
			String myPrefName = loginDataDto.getLocalgovInfo().pref == null ? "" : loginDataDto.getLocalgovInfo().pref;
			String myCityName = loginDataDto.getLocalgovInfo().city == null ? myPrefName + "" : myPrefName + loginDataDto.getLocalgovInfo().city;
			if(myCityName.equals("")){
				errorListAdd(lang.__("Because no prefecture and city name are set, the number of persons difficult to go back home are excluded."));
				isUnableReturnHomeCalculation = false;
			}
			if(StringUtil.isEmpty(earthQuakeLayer)){
				errorListAdd(lang.__("Due to no seismic intensity info is set, the number of persons difficult to go back home are excluded."));
				isUnableReturnHomeCalculation = false;
			}
			if(isUnableReturnHomeCalculation){
				try{
					// 帰宅困難者が発生するのは震度5+以上の時（公共交通機関が停止する基準）
					EarthquakelayerInfo earthquakelayerInfo = earthquakelayerInfoService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());
					EarthquakelayerData layerData = earthquakelayerDataService.findById(Long.parseLong(earthQuakeLayer));
					// 県名＋市町村名に一致するレコードを取得し、市町村コードを検索で利用する
					MeteoareainformationcityMaster cityInfo = meteoareainformationcityMasterService.findByNamevolcano(myCityName);
					if(cityInfo == null){
						// errorListAdd(lang.__("Because no municipal code is found corresponding to the prefecture and city name, the number of persons difficult to go back home is calculated to be zero.") + myCityName);
						errorListAdd(lang.__("Because no municipal code is found corresponding to the prefecture and city name, the number of persons difficult to go back home is calculated to be zero.") + myCityName);
					}else{
						// 検索キーワード
						String keywords = "attr0~*" + layerData.origintime.toString() + " AND attr8~*" + cityInfo.code;

						Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
						LayerInfo quakelayerInfo = mapDB.getLayerInfo(earthquakelayerInfo.tablemasterInfo.layerid);
						vecLayerInfo.add(quakelayerInfo);
						FeatureResultList resultList = FeatureDB.searchFeatureBbox(null, mapId, vecLayerInfo, null/*bbox*/, keywords, 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, null);
						// 検索にヒットした場合(1件を想定)
						if(resultList.countResult() > 0){
							// 1件以外有り得ない
							FeatureResult fResult = resultList.getResult(0);
							// 最大深度
							earthQuakeCityStr = fResult.getAttrResult("attr6").getAttrValue();

							if(earthQuakeCityStr.equals("0") || earthQuakeCityStr.equals("1") || earthQuakeCityStr.equals("2") || earthQuakeCityStr.equals("3")
									|| earthQuakeCityStr.equals("4") || earthQuakeCityStr.equals(lang.__("lower 5"))){
								errorListAdd(lang.__("Calculated the number of persons difficult to go back home as 0, because max seismic intensity is below 5.5 at its local government.", earthQuakeCityStr));
							}else{
								// 帰宅困難者数レイヤのデータを格納する
								List<BeanMap> result_unableReturnHome = tableService.selectAll(unableReturnHomeLayerID);
								for(BeanMap map : result_unableReturnHome){
									if(map.containsKey(dInfo_UnableReturnHome_Evacuee.attrid1) && map.containsKey("theGeom")){
										String people = (String)map.get(dInfo_UnableReturnHome_Evacuee.attrid1);
										PeopleGeomData peopleData = new PeopleGeomData(Double.parseDouble(people), map);
										unableReturnPeopleList.add(peopleData);

										/*
											EvacueeGeomData data = new EvacueeGeomData( (String)map.get(decisionsupportInfo_UnableReturnHome_Evacuee.attrid1), map.get("theGeom") );
											if(data.geom != null){
												// 登録済ならtrue
												boolean addCheck = false;
												for(EvacueeGeomData evacueeData : evacueeList){
													// 見つかったら人数を足してループを抜ける
													if(evacueeData.checkCentroid(data.center, data.people)){
														addCheck = true;
														break;
													}
												}
												// 見つからなければListに格納する
												if(!addCheck) evacueeList.add(data);
											}
										 */
									}
								}
							}
						}
					}
				}catch(NumberFormatException e){
					// earthquakelayer_dataのidをlong型にパースする時にエラーが出ても無視
					isUnableReturnHomeCalculation = false;
				}
			}

			// 避難者数格納前に、総避難者数レイヤのデータをクリア
			if(tableService.getCount(totalLayerID) > 0){
				tableService.deleteAllNoResetSequence(totalLayerID);
			}

			SimpleDateFormat sdf_timefrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// time_fromに現在時刻を入れる
			Timestamp writeTime = new Timestamp(TimeUtil.newDate().getTime());

			// メッシュレイヤをベースに避難者数が1以上いればデータを格納するList
			//List<EvacueeGeomData> evacueeList = new ArrayList<EvacueeGeomData>();
			List<PeopleGeomData> evacueeList = new ArrayList<PeopleGeomData>();
			List<BeanMap> result_mesh = tableService.selectAll(meshLayerID);
			LayerInfo totalPeopleLayerInfo = mapDB.getLayerInfo(totalLayerID);
			for(BeanMap map : result_mesh){
				if(map.containsKey("theGeom")){
					PeopleGeomData meshData = new PeopleGeomData(0, map);
					for(PeopleGeomData buildGeom : buildPeopleList){
						// 一致する図形が見つかれば人口データを加算する
						if(meshData.isEquals(buildGeom.center)){
							meshData.people += buildGeom.people;
							break;
						}
					}
					for(PeopleGeomData areaGeom : areaLayerPeopleList){
						// 一致する図形が見つかれば人口データを加算する
						if(meshData.isEquals(areaGeom.center)){
							meshData.people += areaGeom.people;
							break;
						}
					}
					for(PeopleGeomData unableReturnGeom : unableReturnPeopleList){
						// 一致する図形が見つかれば人口データを加算する
						if(meshData.isEquals(unableReturnGeom.center)){
							meshData.people += unableReturnGeom.people;
							break;
						}
					}
					// 人数が1人以上いれば総避難者数レイヤに格納する
					if(meshData.people > 0){
						// ライフライン被害による避難者数レイヤに登録する
						HashMap<String, String> attributes = new HashMap<String, String>();
						attributes.put(dInfo_TotalEvacuee.attrid1, String.valueOf((int)meshData.people));
						if(StringUtils.isNotEmpty(dInfo_TotalEvacuee.tablemasterInfo.updatecolumn)){
							attributes.put(dInfo_TotalEvacuee.tablemasterInfo.updatecolumn, sdf_timefrom.format(writeTime));
						}
						attributes.put("time_from", sdf_timefrom.format(writeTime));
						// 編集者も更新項目として追加
						if(edituserAttrService.hasEdituserAttr(totalPeopleLayerInfo)) {
							attributes.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
						}
						FeatureDB.insertFeature(userInfo, totalLayerID, meshData.geom.toText(), attributes);
						evacueeList.add(meshData);
					}
				}
			}

			// 避難所の推定避難者数カラムを0で更新
			LayerInfo layerInfo = mapDB.getLayerInfo(shelterLayerID);
			Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
			vecLayerInfo.add(layerInfo);

			FeatureResultList shelterList = FeatureDB.searchFeatureBbox(null, mapId, vecLayerInfo, /*bbox*/null, /*keywords*/null, 0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, null);
			// 時系列化の場合、一気に書き込まないと履歴レコードが増えるのでListに一旦格納する
			List<ShelterGeomData> shelterGeomList = new ArrayList<ShelterGeomData>();

			int shelterListCnt = shelterList.countResult();
			for(int j = 0; j < shelterListCnt; j++){
				FeatureResult fresult = shelterList.getResult(j);
				long orgId = fresult.featureId;
				// 属性情報
				HashMap<String, String> attrs = new HashMap<String, String>();
				int cntAttrSum = fresult.countAttrResult();
				for(int k = 0; k < cntAttrSum; k++){
					attrs.put(fresult.getAttrResult(k).getAttrId(), fresult.getAttrResult(k).getAttrValue());
				}
				attrs.put(dInfo_Shelter.attrid1, "0");
				shelterGeomList.add( new ShelterGeomData(orgId, fresult.getWKT(), attrs) );

				/*
				boolean check = FeatureDB.updateFeatureAttribute(userInfo, shelterLayerID, fresult.featureId, attrs, null);
				if(!check){
					errorListAdd(lang.__("Failed to update map."));
				}
				*/
				/*
				// 履歴追加
				long newFid = FeatureDB.insertFeatureHistory(layerInfo, orgId, fresult.getWKT(), attrs, userInfo);
				if(newFid == 0){
					errorListAdd(lang.__("Failed to update map."));
				}
				*/
			}

			/*
			// デッドロックするので中止
			List<BeanMap>shelterResult = tableService.selectAll(shelterLayerID);
			for(BeanMap map : shelterResult){
				if(map.containsKey(decisionsupportInfo_Shelter.attrid1)){
					Long gid = (Long)map.get("gid");
					tableService.update(shelterLayerID, decisionsupportInfo_Shelter.attrid1, "gid", gid, "0");
				}
			}
			*/
			// 更新時間
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:dd");

			// 取得した避難者数Listデータを利用して、避難所Listに加算する
			for(PeopleGeomData data : evacueeList){
				// 避難所レイヤの中から一番近い避難所を取得
				FeatureResultList fresultList = FeatureDB.searchFeatureBbox(null, mapId, vecLayerInfo, null/*bbox*/, null/*keywords*/, 1/*limit*/, 0/*offset*/, false, FeatureDB.GEOM_TYPE_CENTER, null/*sortAttrId*/, false/*desc*/, new double[]{data.center.getX(), data.center.getY()}, false/*hasGeom*/, null/*timeParam*/);
				if(fresultList.total > 0){
					// 基本的に1件のみ
					FeatureResult fresult = fresultList.getResult(0);
					long orgId = fresult.featureId;
					// 避難所Listから該当するorgIdを検索する
					for(ShelterGeomData shelter : shelterGeomList){
						if(shelter.orgId == orgId){
							shelter.people += data.people;
							break;
						}
					}
				}
			}
			// 避難所Listに加算された推定避難者数を地物に書き込む（履歴追加）
			for(ShelterGeomData shelter : shelterGeomList){
				// 推定避難者数をHashmapに書き込む
				if(shelter.attributes.containsKey(dInfo_Shelter.attrid1)){
					// 四捨五入して格納
					shelter.attributes.put(dInfo_Shelter.attrid1, String.valueOf(Math.round(shelter.people)) );
					// time_fromに現在時刻を入れる
					//Timestamp writeTime = new Timestamp(TimeUtil.newDate().getTime());
					if(StringUtils.isNotEmpty(dInfo_Shelter.tablemasterInfo.updatecolumn)){
						shelter.attributes.put(dInfo_Shelter.tablemasterInfo.updatecolumn, sdf_timefrom.format(writeTime));
					}
					shelter.attributes.put("time_from", sdf_timefrom.format(writeTime));
					// 編集者も更新項目として追加
					if(edituserAttrService.hasEdituserAttr(layerInfo)) {
						shelter.attributes.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
					}
					// 履歴追加
					long newFid = FeatureDB.insertFeatureHistory(layerInfo, shelter.orgId, shelter.wkt, shelter.attributes, userInfo);
					if(newFid == 0){
						errorListAdd(lang.__("Failed to update map."));
					}
				}
			}

		} catch (MismatchedDimensionException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
			logger.error("error", e);
			errorListAdd(lang.__("Failed to update map."));
			return -3;
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			logger.error("error", e);
			errorListAdd(lang.__("Failed to update map."));
			return -3;
			//e.printStackTrace();
		}
		return 1;
	}

	/**
	 * メニューに紐付く確定震度グループレイヤを検索し、地震レイヤ一覧を返却する
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/decisionsupport/JSONEarthQuakeLayerList", produces="application/json", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public ResponseEntity<String> JSONEarthQuakeLayerList(){
		// 二度押し防止トークン
		//TokenProcessor.getInstance().saveToken(SpringContext.getRequest());
		// エラー用のListを初期化
		errorList = new ArrayList<String>();

		// menuinfoid取得
		String menuInfoId = request.getParameter("menuInfoId");
		menuInfoId = menuInfoId == null ? "0" : menuInfoId;

		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		// 返却用JSONArray
		JSONArray arr = new JSONArray();
		JSONArray arr_area = new JSONArray();

		try{
			// ログイン中の記録データ
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			// 検索する期間を取得(平時の時は基本使わないが、現在時刻から1年前くらいまでを検索範囲とする)
			Timestamp startTime = trackData == null ? new Timestamp(System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000) : trackData.starttime;
			// 災害終了時間がなければ現在時刻を格納
			Timestamp endTime = (trackData==null || trackData.endtime == null) ? new Timestamp(System.currentTimeMillis()) : trackData.endtime;
			// 自治体で持つ災害起動時からの震度レイヤ情報を取得
			EarthquakelayerInfo earthquakelayerInfo = earthquakelayerInfoService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());
			// 震度レイヤがなければ何もしない
			if(earthquakelayerInfo != null){
				List<EarthquakelayerData> earthquakelayerDataList = earthquakelayerDataService.findByEarthquakeLayerinfoidAndTimerange(earthquakelayerInfo.id, startTime, endTime);
				// マグニチュード順にする為、一度mapで受け取る
				HashMap<Integer, Float> sortMaps = new HashMap<Integer, Float>();
				//for(EarthquakelayerData data : earthquakelayerDataList){
				for(int j = 0; j < earthquakelayerDataList.size(); j++){
					EarthquakelayerData data = earthquakelayerDataList.get(j);
					String name = data.name;
					try{
						if(name.indexOf("M") != -1){
							String magStr = name.substring(name.indexOf("M")+1, name.indexOf("M")+4);
							sortMaps.put(j, Float.parseFloat(magStr));
						}
					}catch(NumberFormatException e){
					}
				}
				ArrayList entries = new ArrayList(sortMaps.entrySet());
				Collections.sort(entries, new Comparator(){
					public int compare(Object obj1, Object obj2){
						Map.Entry ent1 =(Map.Entry)obj1;
						Map.Entry ent2 =(Map.Entry)obj2;
						String val1 = (String) ent1.getValue();
						String val2 = (String) ent2.getValue();
						return val1.compareTo(val2);
					}
				});
				for(Map.Entry<Integer, Float> e : sortMaps.entrySet()) {
					long dataId = e.getKey();
					EarthquakelayerData data = earthquakelayerDataList.get((int)dataId);
					JSONObject obj = new JSONObject();
					obj.put("name", data.name);
					obj.put("layerdataid", data.id);
					arr.put(obj);
				}

				/*
				for(EarthquakelayerData data : earthquakelayerDataList){
					JSONObject obj = new JSONObject();
					obj.put("name", data.name);
					obj.put("layerdataid", data.id);
					arr.put(obj);
				}
				*/
			}
			json.put("earthquakelayers", arr);

			/* ここから各種演算に必要な設定が存在するか確認し、事前に通知する */

			// 建物被害推定レイヤの情報を取得
			DecisionsupportInfo decisionsupportInfo_Building = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_BUILDING);
			// 人口レイヤの情報を取得
			DecisionsupportInfo decisionsupportInfo_People = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_PEOPLE);
			// 建物被害による避難者数レイヤの情報を取得
			//DecisionsupportInfo decisionsupportInfo_BuildingEvacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_BUILDING_EVACUEE);
			// 各種パラメータが設定されているかチェック
			if(!decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_Building) || !decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_People)){
				errorListAdd(lang.__("No configurations for buildings damage estimation"));
			}
			// 停電エリアレイヤの情報を取得(Ver1.4のみ)
			DecisionsupportInfo decisionsupportInfo_PowerOutage = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_POWER_OUTAGE);
			// 断水レイヤの情報を取得(Ver1.4のみ)
			DecisionsupportInfo decisionsupportInfo_WaterOutage = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_WATER_OUTAGE);
			// エリアレイヤの情報を取得
			List<DecisionsupportInfo> decisionsupportInfo_areaLayer = decisionsupportInfoService.findByLocalgovinfoIdAndTypeIdList(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_ETC_AREA_LAYER);
			// エリアレイヤがない場合は停電、断水エリアの有無を確認する
			if(decisionsupportInfo_areaLayer.size() == 0){
				// 上記3つのうちいずれもないと警告
				// 各種パラメータが設定されているかチェック
				if(!decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_PowerOutage) || !decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_WaterOutage)){
					errorListAdd(lang.__("No configuration exists to calculate utility damage estimation."));
				}else{
					decisionsupportInfo_areaLayer.add(decisionsupportInfo_PowerOutage);
					decisionsupportInfo_areaLayer.add(decisionsupportInfo_WaterOutage);
				}
			}else{
				// 各種パラメータが設定されているかチェック
				for(DecisionsupportInfo info : decisionsupportInfo_areaLayer){
					if(!decisionsupportInfoService.isNotEmptyParam(info)){
						errorListAdd(lang.__("No configuration exists to calculate utility damage estimation."));
					}
				}
			}
			for(DecisionsupportInfo info : decisionsupportInfo_areaLayer){
				JSONObject obj = new JSONObject();
				obj.put("id", info.id);
				//obj.put("note", StringUtils.isEmpty(info.note) ? lang.__("Area layer" + info.id) : info.note);
				obj.put("note", StringUtils.isEmpty(info.note) ? lang.__("Area layer" + info.id) : info.note);
				obj.put("buffer", info.buffer == null ? 0 : info.buffer);
				arr_area.put(obj);
			}
			json.put("arealayers", arr_area);

			// 帰宅困難者による避難者数レイヤの情報を取得
			DecisionsupportInfo decisionsupportInfo_UnableReturnHome_Evacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_UNABALE_RETURNHOME_EVACUEE);
			// 各種パラメータが設定されているかチェック
			if(!decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_UnableReturnHome_Evacuee)){
				errorListAdd(lang.__("No configuration exists to calculate the number of persons difficult to get back home."));
			}
			// 総避難者数レイヤの情報を取得
			DecisionsupportInfo decisionsupportInfo_TotalEvacuee = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_TOTAL_EVACUEE);
			// 避難所レイヤの情報を取得
			DecisionsupportInfo decisionsupportInfo_Shelter = decisionsupportInfoService.findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_SHELTER);
			// 各種パラメータが設定されているかチェック
			if(!decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_TotalEvacuee) || !decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo_Shelter)){
				errorListAdd(lang.__("No configuration to calculate the number of all refugees."));
			}
			// エラーをJSONで返す
			JSONArray error_arr = new JSONArray();
			for(String err : errorList){
				JSONObject obj = new JSONObject();
				obj.put("msg", err);
				error_arr.put(obj);
			}
			json.put("error", error_arr);

			// 出力
			/*PrintWriter out = response.getWriter();
			out.print( json.toString() );*/

			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (NumberFormatException e){
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * 避難者推定値と収容人数を比較し、規定値を超える避難所のgidとClassNameを返却する
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/decisionsupport/JSONAlertShelterList", produces="application/json", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public ResponseEntity<String> JSONAlertShelterList(){
		JSONObject json = new JSONObject();
		// 出力の準備
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");*/
		PrintWriter out = null;
		// 返却用JSONArray
		JSONArray arr_yellow = new JSONArray();
		JSONArray arr_red = new JSONArray();

		try{
			// 推定避難者数超過の一覧<WKT, ClassName>
			HashMap<String, String> filterMap = decisionsupportInfoService.filterShelter(false);
			for(Map.Entry<String, String> map : filterMap.entrySet()){
				if(map.getValue().equals("yellow")){
					arr_yellow.put(map.getKey());
				}else if(map.getValue().equals("red")){
					arr_red.put(map.getKey());
				}
			}
			json.put("yellow", arr_yellow);
			json.put("red", arr_red);
			// 出力
			/*out = response.getWriter();
			out.print( json.toString() );*/
			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (NumberFormatException e){
			logger.error(loginDataDto.logInfo(), e);
		} finally {
			if(out != null){
				out.close();
			}
		}
		return null;
	}

	/**
	 * リクエストで得られたWKTを元に、人口メッシュレイヤから人口と世帯数を演算してJSON形式で返却
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/decisionsupport/JSON_WKTAreaPeople", produces="application/json", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> JSON_WKTAreaPeople(){
		JSONObject json = new JSONObject();
		// 出力の準備
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");*/
		PrintWriter out = null;

		try{
			// エラーがあれば後で上書きする
			json.put("error", "");
			// 初期値
			json.put("people", 0);
			json.put("house", 0);
			// リクエストからBBOXを取得
			String wkts = request.getParameter("wkts");
			wkts = wkts == null ? "" : wkts;
			// 重なったら100%で演算とした場合、世帯=1873144, 人口=4057002 (大阪地域のH22国政調査データを利用)
			// wkts = "POLYGON((135.39823148503703 34.740026794121405,135.58087919010595 34.72027574372327,135.66053006901356 34.518545684433015,135.48818204656078 34.47666917612647,135.39823148503703 34.740026794121405))";

			// ツールボックス設定から人口レイヤを取得する
			List<ToolboxData> toolboxDataList = toolboxDataService.findByLocalgovinfoIdAndToolboxTypeId(loginDataDto.getLocalgovinfoid(), TOOLBOX_TYPE_PEOPLE);
			if(toolboxDataList != null && toolboxDataList.size() > 0 && !wkts.equals("")){
				// 複数件対応
				int people = 0;
				int house = 0;
				for(ToolboxData toolboxData : toolboxDataList){
					// 人口レイヤ情報を取得する
					if(toolboxData.tablemasterInfo != null && StringUtil.isNotEmpty(toolboxData.tablemasterInfo.layerid)){

						// 人口数
						if(StringUtil.isNotEmpty(toolboxData.attrid1) && StringUtil.isNotEmpty(toolboxData.attrid2)){
							// 人口レイヤの地物を取得
							List<BeanMap> result_people = tableService.selectAll(toolboxData.tablemasterInfo.layerid);
							Geometry searchPolygon = new WKTReader().read(wkts);

							for(BeanMap map : result_people){
								if(map.containsKey("theGeom")){
									if(map.get("theGeom") != null){
										String theGeom = map.get("theGeom").toString(); // MultiPolygon
										String[] theGeomArr = theGeom.split(";");
										// SRID=4326;MultiPolygon(((...の形でくるため
										if(theGeomArr.length == 2){
											theGeom = theGeomArr[1];
										}
										Geometry geom = new WKTReader().read(theGeom);

										// 含まれるポリゴンの配列数を取得
										int n = geom.getNumGeometries();
										// Polygonに分割する
										for(int i = 0; i < n; i++){
											Geometry poly = geom.getGeometryN(i);
											// 検索するポリゴンと重なるかチェック
											if(poly.intersects(searchPolygon)){
												// 比率で面積を出す処理を追加依頼対応 2017/08/04
												// 面積計算(重なってる図形を算出)
												Geometry andGeom = searchPolygon.intersection(poly);
												double per = andGeom.getArea() / poly.getArea();
												// 変な値になれば抜ける
												if(per <= 0) break;

												String peopleObj = (String)map.get(toolboxData.attrid1);
												String houseObj  = (String)map.get(toolboxData.attrid2);

												if(peopleObj != null) people += Math.round(Double.parseDouble(peopleObj) * per);
												if(houseObj != null) house += Math.round(Double.parseDouble(houseObj) * per);

												break;
											}
										}
									}
								}
							}
						}
					}
				}
				json.put("people", people);
				json.put("house",  house);
			}
			// 出力
			/*out = response.getWriter();
			out.print( json.toString() );*/
			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (NumberFormatException e){
			logger.error(loginDataDto.logInfo(), e);
		} catch (com.vividsolutions.jts.io.ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if(out != null){
				out.close();
			}
		}
		return null;
	}


	/**
	 * 避難者推計の実行ロック
	 * 複数ユーザが同時に編集しないように、排他ロックします.
	 * @return : null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/decisionsupport/decisionLock", produces="application/json")
	public ResponseEntity<String> decisionLock() {

		JSONObject result = lock(true);

		// 出力
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		ResponseUtil.write(result.toString());
*/
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(result.toString(), httpHeaders, HttpStatus.OK);
	}

	/**
	 * 避難者推計の実行ロック解除
	 * @return : null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/decisionsupport/decisionUnlock", produces="application/json")
	public ResponseEntity<String> decisionUnlock() {

		JSONObject result = lock(false);

		// 出力
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		ResponseUtil.write(result.toString());*/

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(result.toString(), httpHeaders, HttpStatus.OK);
	}

	/**
	 * 避難者推計の実行ロック(RakugakiActionから引用)
	 * @param lockMode true: ロックする, false: ロック解除
	 * @return ロック結果
	 */
	protected JSONObject lock(boolean lockMode) {

		// ロックユーザ
		//long trackdataid = loginDataDto.getTrackdataid();
		long localgovinfoid = loginDataDto.getLocalgovinfoid();
		String sessionId = session.getId();
		Date now = new Date();

		// ロック有効秒数
		int EXPIRE_SEC = 30;
		Date expireDate = new Date(now.getTime()+(EXPIRE_SEC*1000));
		// ServletContext の Attribute に保存に使用するキー
		String key = "Decisionsupport.lock.localgovinfoid"+localgovinfoid;
		// ロックしたタイムスタンプの保存形式
		final String DATE_PATTERN ="yyyy/MM/dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

		try {
			// ロックフラグ
			boolean lock = true;
			// ロック情報
			JSONObject lockInfo = null;
			synchronized(application) {
				// ロックされているかチェック
				lockInfo = (JSONObject) application.getAttribute(key);
				if(lockInfo!=null) {
					// 有効期限を取得
					try {
						Date timestamp = dateFormat.parse(lockInfo.getString("timestamp"));
						expireDate = new Date(timestamp.getTime()+(EXPIRE_SEC*1000));
					} catch (ParseException e) {
						//throw new ServiceException(lang.__("Unable to read memo editing lock time."), e);
						throw new ServiceException("Unable to read decisionsupport editing lock time", e);
					}

					// 他の人がロックしているかチェック
					String lockedSessionId = (String) lockInfo.get("sessionid");
					if(sessionId.equals(lockedSessionId)==false) {
						// 有効期限がまだ過ぎていないかチェック
						if(now.before(expireDate)) {
							// ロック失敗
							lock=false;
						}
					}
				}

				// ロック可能ならば
				if(lock) {
					// ロックする
					if(lockMode) {
						// ロック情報を作成
						lockInfo = new JSONObject();
						lockInfo.put("localgovinfoid", localgovinfoid);
						lockInfo.put("sessionid", sessionId);
						lockInfo.put("timestamp", dateFormat.format(now));
						// ロックしている班情報
						if(loginDataDto.getGroupInfo()!=null) {
							GroupInfo info = loginDataDto.getGroupInfo();
							JSONObject infoJSON = new JSONObject();
							infoJSON.put("id", info.id);
							infoJSON.put("name", info.name);
							lockInfo.put("groupInfo", info);
						}
						// ロックしている課情報
						if(loginDataDto.getUnitInfo()!=null) {
							UnitInfo info = loginDataDto.getUnitInfo();
							JSONObject infoJSON = new JSONObject();
							infoJSON.put("id", info.id);
							infoJSON.put("name", info.name);
							lockInfo.put("unitInfo", info);
						}

						// ServletContext に保存
						application.setAttribute(key, lockInfo);
					}
					// ロック解除
					else {
						// ServletContext をクリア
						application.removeAttribute(key);
						lock = false;
						lockInfo = null;
						expireDate = null;
					}
				}
			}

			// 結果情報
			JSONObject result = new JSONObject();
			result.put("lock", lock);
			result.put("timestamp", dateFormat.format(now));
			if(expireDate!=null) {
				result.put("expireDate", dateFormat.format(expireDate));
			}
			// ロック情報
			if(lockInfo!=null) {
				// セッション情報はハイジャックされる可能性があるため取り除く
				JSONObject clone = new JSONObject(lockInfo.toString());
				clone.remove("sessionid");
				result.put("lockInfo", clone);
			}

			return result;

		} catch(JSONException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}


	/**
	 * メッシュの重心点（center）と同じものが、建物被害推定、ライフライン被害推定が格納された
	 * Listの中に存在すればTrueを返す
	 * @param lifeLineDatas : 建物被害,ライフライン推定が格納されたList
	 * @param center : 検索対象メッシュの重心点
	 * @return : 存在すればtrue, 無ければfalse
	 */
	public boolean isLifeLineDataExist(List<LifeLineGeomData> lifeLineDatas, Point center){
		for(LifeLineGeomData lifeLineData : lifeLineDatas){
			if(lifeLineData.center.equals(center)){
				return true;
			}
		}
		return false;
	}
	/**
	 * メッシュの重心点（center）と同じものが、建物被害推定、ライフライン被害推定が格納された
	 * Listの中に存在すればLifeLineGeomDataを返す
	 * @param lifeLineDatas : 建物被害,ライフライン推定が格納されたList
	 * @param center : 検索対象メッシュの重心点
	 * @return : 存在すればLifeLineGeomData, 無ければnull
	 */
	public boolean findByLifeLineData(List<LifeLineGeomData> lifeLineDatas, Point center, double calc){
		for(LifeLineGeomData lifeLineData : lifeLineDatas){
			if(lifeLineData.center.equals(center)){
				if(lifeLineData.calc < calc){
					lifeLineData.calc = calc;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 処理完了時にエラーを返却する為に格納しているListにエラー内容を格納する関数
	 * @param msg : エラー内容
	 */
	public void errorListAdd(String msg){
		logger.warn(msg);
		errorList.add(msg);
	}

	/**
	 * 避難者推計2次データレイヤのゼロクリア関数
	 * 設定不備があればfalseで返して、クリアは実施しない
	 * @param info : 2次データレイヤの設定
	 * @return : 成功すればtrue
	 */
	public boolean layerDataClear(DecisionsupportInfo info){
		// 設定がNullなら終了する
		if(info == null) return false;
		// 各種パラメータが設定されているかチェック
		if(!decisionsupportInfoService.isNotEmptyParam(info)) return false;
		// テーブルマスタを取得する
		TablemasterInfo tableInfo = tablemasterInfoService.findByNotDeletedId(info.tablemasterinfoid);
		if(tableInfo == null) return false;

		try {
			// コピーフラグtrue対応
			String layerid = "";
			if(loginDataDto.getTrackdataid() == 0){
				layerid = tableInfo.layerid;
			}else{
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tableInfo.id, loginDataDto.getTrackdataid());
				if(ttbl != null) layerid = ttbl.layerid;
			}
			if(StringUtils.isEmpty(layerid)) return false;
			// 避難者数レイヤのデータをクリア
			if(tableService.getCount(layerid) > 0){
				tableService.deleteAllNoResetSequence(layerid);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 引数の時間から何日経過しているかを計算し、数字で返す
	 * 0 = 当日
	 * 1～ = 1～日後
	 * @param startTime : 災害モード開始時間
	 * @return : 日数
	 */
	public int afterDays(Long startTime){
		long now = System.currentTimeMillis();
		long yesterday = System.currentTimeMillis()-ONEDAY_TIME;
		long oneWeekAgo = System.currentTimeMillis()-ONEWEEK_TIME;
		long twoWeekAgo = System.currentTimeMillis()-ONEWEEK_TIME*2;
		long threeWeekAgo = System.currentTimeMillis()-ONEWEEK_TIME*3;
		long fourMonthAgo = System.currentTimeMillis()-ONEWEEK_TIME*4;

		// 災害モード起動から1日未満
		if( startTime >= yesterday ){
			return 0;
		}
		// 1日後～
		else{
			for(int i = 1; i < 30; i++){
				long ago = now - ONEDAY_TIME * i;
				long ago_1 = now - ONEDAY_TIME * (i+1);
				if( startTime < ago && startTime >= ago_1 ){
					return i;
				}
			}
		}
		// 1ヶ月以上経過と同じにする
		return 32;
	}


	/**
	 * 建物被害用Class
	 */
	class BuildingGeomData{
		double zenkai_per = 0.0;
		double hankai_per = 0.0;
		Geometry geom = null;
		public BuildingGeomData(String zenkai, String hankai, Object geomObj){
			try{
				this.zenkai_per = zenkai == null ? 0.0 : Double.parseDouble(zenkai);
				this.hankai_per = hankai == null ? 0.0 : Double.parseDouble(hankai);
				if(geomObj != null){
					// String に変換
					String wkt = geomObj.toString();
					// SRID=4326; のような文字列がくっついてくるようになったので削除する
					if(wkt.indexOf(";") != -1){
						wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
					}
					WKTReader reader = new WKTReader();
					this.geom = reader.read(wkt);
				}
			} catch (NumberFormatException e){
				// 無視する
			} catch (com.vividsolutions.jts.io.ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		/**
		 * 引数で渡された図形と重なりがあるかチェック
		 * @param search
		 * @return
		 */
		public boolean isIntersects(Geometry search){
			if(this.geom.intersects(search)){
				return true;
			}
			return false;
		}
	}

	/**
	 * 人口データ用Class
	 */
	class PeopleGeomData{
		double people = 0;
		//int setai = 0;
		Geometry geom = null;
		Point center = null;
		public PeopleGeomData(double people, BeanMap map){
			try{
				this.people = people;
				//this.setai = setai == null ? 0 : Integer.parseInt(setai);
				if(map.containsKey("theGeom")){
					if(map.get("theGeom") != null){
						// String に変換
						String wkt = map.get("theGeom").toString();
						// SRID=4326; のような文字列がくっついてくるようになったので削除する
						if(wkt.indexOf(";") != -1){
							wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
						}
						WKTReader reader = new WKTReader();
						Geometry geom = reader.read(wkt);
						this.geom = geom;
						this.center = geom.getCentroid();
					}
				}
				/*
				if(geomObj != null){
					// WKBReader reader = new WKBReader();
					// this.geom = reader.read(WKBReader.hexToBytes(geomObj.toString()));

					// String に変換
					String wkt = geomObj.toString();
					// SRID=4326; のような文字列がくっついてくるようになったので削除する
					if(wkt.indexOf(";") != -1){
						wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
					}
					WKTReader reader = new WKTReader();
					this.geom = reader.read(wkt);
					this.center = this.geom.getCentroid();
				}
				*/
			} catch (NumberFormatException e){
				// 無視する
			} catch (com.vividsolutions.jts.io.ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		public PeopleGeomData(double people, Geometry geom){
			this.people = people;
			this.geom = geom;
			this.center = this.geom == null ? null : this.geom.getCentroid();
		}
		/**
		 * 引数で渡された図形と重心が一致するかチェック
		 * @param search
		 * @return
		 */
		public boolean isEquals(Point search){
			if(search == null) return false;
			if(this.center.equals(search)){
				return true;
			}
			return false;
		}
	}

	/**
	 * ライフライン被害データ用Class
	 */
	class LifeLineGeomData{
		String people = "0";
		Geometry geom = null;
		// 停電、断水データで同じメッシュかどうかを比較する為のPointデータ
		Point center = null;
		// 演算式
		double calc = 0.0;
		public LifeLineGeomData(double people, Geometry geomObj, Point center, double calc){
			try{
				this.people = String.valueOf(people);
				this.calc = calc;
				if(geomObj != null){
					this.geom = geomObj;
					this.center = center;
				}
			} catch (NumberFormatException e){
				// 無視する
			}
		}
	}

	/**
	 * ライフライン等エリアレイヤデータ用Class
	 */
	class EtcAreaLayerGeomData{
		// 図形のポリゴンが格納されるList
		List<Geometry> geomList;
		// 演算式
		double calc = 0.0;
		int buffer = 0;
		public EtcAreaLayerGeomData(double calc, int buffer){
			this.calc = calc;
			this.buffer = buffer;
			// Listの初期化
			geomList = new ArrayList<Geometry>();
		}
		/**
		 * 図形格納用
		 * @param map
		 */
		public void insertGeomData(BeanMap map){
			try {
				if(map.containsKey("theGeom")){
					if(map.get("theGeom") != null){
						// String に変換
						String wkt = map.get("theGeom").toString();
						// SRID=4326; のような文字列がくっついてくるようになったので削除する
						if(wkt.indexOf(";") != -1){
							wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
						}
						WKTReader reader = new WKTReader();
						Geometry areaGeom = reader.read(wkt);

						if(buffer != 0){
							GeometryFactory factory = new GeometryFactory();
							Geometry newGeom = factory.createGeometry(areaGeom);
							MathTransform transform1 = CRS.findMathTransform(DefaultGeographicCRS.WGS84, CRS.decode("EPSG:3857"));
							MathTransform transform2 = CRS.findMathTransform(CRS.decode("EPSG:3857"), DefaultGeographicCRS.WGS84);
							Geometry transGeom = JTS.transform(newGeom, transform1);
							transGeom = transGeom.buffer(this.buffer);
							areaGeom = JTS.transform(transGeom, transform2);
						}
						this.geomList.add(areaGeom);
					}
				}
			} catch (com.vividsolutions.jts.io.ParseException e) {
				e.printStackTrace();
			} catch (NoSuchAuthorityCodeException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (FactoryException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (MismatchedDimensionException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		/**
		 * 引数で渡された図形と重なりがあるかチェック
		 * @param search
		 * @return
		 */
		public boolean isIntersects(Geometry search){
			for(Geometry geom : this.geomList){
				//if(geom.buffer(this.buffer).intersects(search)){
				if(geom.intersects(search)){
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 総避難者数データ用Class
	 */
	class EvacueeGeomData{
		double people = 0;
		Geometry geom = null;
		// 停電、断水データで同じメッシュかどうかを比較する為のPointデータ
		Point center = null;
		public EvacueeGeomData(String people, Object geomObj){
			try{
				this.people = Double.parseDouble(people);
				if(geomObj != null){
					// String に変換
					String wkt = geomObj.toString();
					// SRID=4326; のような文字列がくっついてくるようになったので削除する
					if(wkt.indexOf(";") != -1){
						wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
					}
					WKTReader reader = new WKTReader();
					this.geom = reader.read(wkt);

					//WKBReader reader = new WKBReader();
					//this.geom = reader.read(WKBReader.hexToBytes(geomObj.toString()));
					this.center = this.geom.getCentroid();
				}
			} catch (NumberFormatException e){
				// 無視する
			} catch (com.vividsolutions.jts.io.ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		/**
		 * 重心が一致したら避難者数を足す
		 * @param point
		 * @param people
		 */
		public boolean checkCentroid(Point point, double people){
			try{
				if(this.center.equals(point)){
					this.people += people;
					return true;
				}

			}catch(NumberFormatException e){
			}
			return false;
		}
	}

	/**
	 * 避難所データ用Class
	 */
	class ShelterGeomData{
		// 推定避難者数
		double people = 0;
		String wkt = "";
		long orgId = 0;
		HashMap<String, String> attributes = null;
		public ShelterGeomData(long orgId, String wkt, HashMap<String, String> attributes){
			this.orgId = orgId;
			this.attributes = attributes;
			this.wkt = wkt;
		}
	}

}
