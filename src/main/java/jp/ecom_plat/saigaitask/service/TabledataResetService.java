/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.entity.db.TableresetcolumnData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.TableresetcolumnDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;

/**
 * 災害終了時に地物のデータリセットを行うサービスクラス
 */
@org.springframework.stereotype.Service
public class TabledataResetService extends BaseService{

	@javax.annotation.Resource protected SaigaiTaskDBLang lang;
	@Resource
	protected TableService tableService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	@Resource
	protected TableresetcolumnDataService tableresetcolumnDataService;
	@Resource
	protected StationService stationService;

	/** 体制のデフォルト値 */
    String getNO_STATION_NAME() {
    	return lang.__("No system");
    }
	/** Logger */
	protected Logger logger = Logger.getLogger(AbstractAction.class);

	/**
	 * 時系列レイヤに対し、特定の日時の過去データでリセットする
	 * 本機能は、全ての災害が終了した時に呼ばれる事を想定している
	 * @param userInfo : ecommapのUserInfo
	 * @param resetReq : リセット情報 JSON
	 * @param endtime  : 災害終了時間(UTCではなく通常のシステム時間)
	 * @throws Exception
	 */
	public void dataReset(String ecomuser, String resetReq, Timestamp endtime) throws Exception{

		MapDB mapDB = MapDB.getMapDB();
		UserInfo userInfo = mapDB.getAuthIdUserInfo(ecomuser);
		if(userInfo==null) {
			throw new ServiceException(lang.__("e-community map user not found."));
		}

		// 時系列データ(UTC対応)
		//Timestamp time = endtime == null ? new Timestamp(System.currentTimeMillis()) : endtime;
		Timestamp time = endtime == null ? new Timestamp(TimeUtil.newDate().getTime() ): new Timestamp(TimeUtil.newUTCDate(endtime.getTime()).getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf_timefrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String now = sdf.format(time);

		JSONObject obj = new JSONObject(resetReq);
		if(obj.has("resetLayers") && obj.has("restoretime") ){
			// 復元ポイント yyyy/MM/dd HH:mm:ssの形式
			String restoreTime = obj.getString("restoretime");
			JSONArray resetArr = obj.getJSONArray("resetLayers");
			if(resetArr != null && StringUtils.isNotEmpty(restoreTime)){
				// レイヤ情報の取得
				for(int i = 0; i < resetArr.length(); i++){
					JSONObject resetObj = resetArr.getJSONObject(i);
					if(resetObj.has("layerid") && resetObj.has("attrs")){
						String layerid = resetObj.getString("layerid");
						// layeridが空なら何もしない
						if (StringUtils.isEmpty(layerid)) {
							continue;
						}
						// 属性情報の取得
						JSONArray resetAttrs = resetObj.getJSONArray("attrs");
						// レイヤ全体を復元ポイントに戻すかどうか（図形含む）
						boolean allreset = true;
						// リセット対象となる属性情報を格納する
						List<String> resetAttrList = new ArrayList<String>();
						for(int j = 0; j < resetAttrs.length(); j++){
							JSONObject attrObj = resetAttrs.getJSONObject(j);
							if(attrObj.has("attrid") && attrObj.has("reset")){
								String resetStr = attrObj.getString("reset");
								if(resetStr.equals("true")){
									resetAttrList.add(attrObj.getString("attrid"));
								}else{
									allreset = false;
								}
							}
						}
						if(resetAttrList.size() == 0) continue;
						// レイヤInfo
						LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerid);
						// 履歴テーブルのみ対象とする
						if(TimeSeriesType.HISTORY!=layerInfo.timeSeriesType) continue;

						// 復元ポイントのDate配列(UTC対応)
						Date restoreDateUTC = TimeUtil.newUTCDate( sdf.parse(restoreTime).getTime() );
						Date[] restoreDateArr = new Date[]{restoreDateUTC};
						Date[] nowDateArr = new Date[]{TimeUtil.newDate()};

						// 現在登録されているデータの一覧を取得する
						List<BeanMap> result = tableService.selectAll(layerid, "desc", "");
						for(BeanMap map : result){
							if(map.containsKey("gid")){
								// 履歴レイヤでは_orgidを利用する
								long fid = (long)map.get("_orgid");

								// 復元ポイントの属性情報を取得
								FeatureResult restoreResult = FeatureDB.getFeatureContent(layerInfo, fid, false, FeatureDB.GEOM_TYPE_GEOM, null, restoreDateArr);
								// リセット時間に属性が存在しなかった場合は削除する
								if(restoreResult == null || restoreResult.countAttrResult() == 0){
									// 時系列レイヤでは第四引数は利用していないようなのでNull
									FeatureDB.deleteFeature(userInfo, layerid, fid, null, nowDateArr[0]);
									continue;
								}

								// 復元ポイントの図形
								String restoreWKT = restoreResult.getWKT();
								// Hashmapに変換
								HashMap<String, String> restore_attributes = ExMapDB.getAttributes(restoreResult);
								// trackdataidは0で固定
								restore_attributes.put("trackdataid", "0");

								long newFid = 0;
								// オールリセットなら、リセット時間の属性情報でそのまま上書き
								if(allreset){
									restore_attributes.put("time_from", sdf_timefrom.format(time));
									newFid = FeatureDB.insertFeatureHistory(layerInfo, /*orgId*/fid, restoreWKT, restore_attributes, userInfo);
								}
								// 部分リセットなら、リセット対象の属性のみを更新(WKTはそのまま)
								else{
									// 現在の属性値を取得
									FeatureResult nowResult = FeatureDB.getFeatureContent(layerInfo, fid, false, FeatureDB.GEOM_TYPE_GEOM, null, nowDateArr);
									String nowWKT = nowResult.getWKT();
									HashMap<String, String> now_attributes = ExMapDB.getAttributes(nowResult);
									// 属性更新
									for(String str : resetAttrList){
										now_attributes.put(str, restore_attributes.get(str));
									}
									now_attributes.put("time_from", sdf_timefrom.format(time));
									newFid = FeatureDB.insertFeatureHistory(layerInfo, /*orgId*/fid, nowWKT, now_attributes, userInfo);
								}
								// 更新したfidが0なら警告を出す
								if(newFid == 0){
									logger.warn("LayerReset -> newFid = 0");
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ecommapの属性情報一覧を取得
	 * 属性情報取得の際、リセット対象となる属性があればreset=trueとする
	 * @param layerId : eコミ layerid
	 * @param resetcolumnDataList : テーブルリセット属性情報id
	 * @return 属性情報一覧のJSONArray
	 * @throws JSONException
	 */
	public JSONArray layerInfoAttrName(String layerId, Long tablemasterinfoid) throws JSONException{
		JSONArray arr = new JSONArray();
		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
		// リセット対象属性を取得
		List<TableresetcolumnData> resetcolumnDataList = resetColumnList(tablemasterinfoid);
		// 属性の取得
		for(AttrInfo attrInfo : layerInfo.getAttrIterable()) {
			// trackdataidとst_edituserは表示しない
			if(attrInfo.attrId.equals("trackdataid")) continue;
			if(attrInfo.attrId.equals("st_edituser")) continue;
			JSONObject obj = new JSONObject();
			obj.put("attrid", attrInfo.attrId);
			obj.put("attrname", attrInfo.name);

			boolean resetColumn = false;

			for(TableresetcolumnData data : resetcolumnDataList){
				// attridが一致すればリセット対象をtrueとして返却する
				if(attrInfo.attrId.equals(data.attrid)){
					obj.put("reset", true);
					resetColumn = true;
					break;
				}
			}
			if(!resetColumn){
				obj.put("reset", false);
			}
			arr.put(obj);
		}
		return arr;
	}

	/**
	 * リセットフラグがtrueのtracktableInfoListを取得する
	 * @param trackmapinfoid : loginDataDto.getTrackmapinfoid()
	 * @return
	 */
	public List<TracktableInfo> resetTableList(long trackmapinfoid){
		return tracktableInfoService.findByTrackmapInfoAndResetLayerId(trackmapinfoid);
	}

	/**
	 * リセット対象属性情報を取得
	 * @return
	 */
	public List<TableresetcolumnData> resetColumnList(Long tablemasterinfoid){
		return tableresetcolumnDataService.findByTablemasterinfoId(tablemasterinfoid);
	}

	/**
	 * 体制名が体制なしならtrue, それ以外はfalse
	 */
	public String isStationOFF(){
		// 現在の体制名を取得
		String stationName = stationService.getLoginCurrentSationName();
		// 体制未設定？は体制発令はしていないのでtrueで返却
		if(stationName == null) return "true";
		// 体制なしならtrue
		if(stationName.equals(getNO_STATION_NAME())) return "true";
		StationclassInfo currentStationClassInfo = stationService.getLoginCurrentStationclassInfo();
		if(currentStationClassInfo==null) {
			throw new ServiceException(lang.__("Set system type of current system ({0}).", stationService.getLoginCurrentSationName()));
		}
		// 体制区分の体制IDが解除ならtrue
		if(currentStationClassInfo.stationid==0) return "true";
		// それ以外は、発令されていると判断してfalse
		return "false";
	}

}
