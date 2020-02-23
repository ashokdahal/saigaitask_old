/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.EarthquakegrouplayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.util.TimeUtil;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class EarthquakelayerAction extends AbstractPageAction {

	@Resource
	protected EarthquakegrouplayerDataService earthquakegrouplayerDataService;
	@Resource
	protected EarthquakelayerDataService earthquakelayerDataService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected EarthquakelayerInfoService earthquakelayerInfoService;

	/** 震度レイヤの表示時間 Default=24h (MeteoricEarthQuakeServiceにも同じ変数を持ってる) */
	public long EARTHQUAKE_LAYER_DISPLAY_TIME = 1000*60*60*24;

	// List to store error details
	List<String> errorList = null;
	// days going back in normal time (1 year)
	final long BEFORE_TIME = 365*24*60*60*1000;

	/**
	 * Return the list of earthquakes associated with the logged-in disaster as a JSON string
	 * @return
	 */
	@RequestMapping(value="/page/earthquakelayer/", produces="application/json")
	@ResponseBody
	public String index(){
		// Initialize the error list
		errorList = new ArrayList<String>();
		// Return with JSON
		JSONObject json = new JSONObject();

		// Prepare output
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try{
			// put the count number in the initial stage
			json.put("total", "0");
			json.put("results", new JSONArray());
			json.put("errors", new JSONArray());

			// Training mode
			boolean isTraining = false;
			// Disaster reproduction mode
			boolean isOldTrack = false;
			if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
				TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
				isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
				isOldTrack = trackData!=null&&trackData.endtime!=null ? true : false;
			}

			// Get login information (disaster mode)
			List<TrackData> trackDatas = new ArrayList<TrackData>();
			if(isOldTrack){
				// Replay mode
				TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
				trackDatas.add(trackData);
			}else{
				// 災害モード
				trackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), isTraining);
			}

			// 検索範囲は時間
			// 現在時刻で初期設定
			Timestamp starttime = new Timestamp(System.currentTimeMillis());
			Timestamp endtime = null;

			// 震度レイヤのlayeridを取得
			//EarthquakegrouplayerData earthquakegroupLayerData = checkEarthQuakeGroupLayer();
			//if(earthquakegroupLayerData != null){

			// 災害モードなら、起動時の時刻から現在時刻、若しくは終了時刻で取得する
			if(trackDatas.size() > 0){
				for(TrackData td : trackDatas){
					// 設定済の災害起動時間より古いものがあれば上書き
					if(td.starttime.before(starttime)){
						starttime = td.starttime;
					}
					// 災害終了していれば格納（条件に入るのは複数同時で先に終了したもの or 災害復元モード）
					if(td.endtime != null){
						if(endtime == null){
							endtime = td.endtime;
						}
						// 設定済の災害起動時間より古いものがあれば上書き
						else if(td.endtime.after(endtime)){
							endtime = td.endtime;
						}
					}
				}
			}else{
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.YEAR, -1);
				//java.util.Date utilDate = cal.getTime();
				// 1年前で初期設定
				starttime = new Timestamp(cal.getTimeInMillis());
				//starttime = new Timestamp(utilDate.getTime());
				//starttime = new Timestamp(System.currentTimeMillis() - BEFORE_TIME);
				endtime = new Timestamp(System.currentTimeMillis());
			}
			// originTimeを期間内で検索してリスト取得する
			JSONArray arr = new JSONArray();
			//List<EarthquakelayerData> earthquakeLayer_timerange = earthquakelayerDataService.findByEarthquakeLayerinfoidAndTimerange(earthquakegroupLayerData.id, starttime, endtime);
			List<EarthquakelayerData> earthquakeLayer_timerange = checkEarthQuakeLayerInfo(starttime, endtime);

			// 1つ前のorigintimeを格納しておく変数。time_to演算用に使用
			Timestamp afterOriginTime = null;
			for(EarthquakelayerData data : earthquakeLayer_timerange){
				// originTimeの時刻で履歴を取得
				if(data.origintime != null){
					JSONObject obj = new JSONObject();
					String areaName = "";
					String mg = "";
					String maxIntStr = "";
					// 半角スペースで分割
					String[] layerDatas = data.name.split(" ");
					// 分割数は5が仕様
					if(layerDatas.length == 5){
						areaName = layerDatas[2];
						mg = layerDatas[3];
						maxIntStr = layerDatas[4];
					}
					obj.put("id", data.id);
					obj.put("layername", data.name);
					obj.put("origintime", data.origintime);
					obj.put("layerid", data.layerid);
					obj.put("areaname", areaName);
					obj.put("mg", mg);
					obj.put("maxint", maxIntStr);
					obj.put("time_from", TimeUtil.iso8601Formatter.format(data.origintime));
					obj.put("time_from_local", new Timestamp(data.origintime.getTime()).toString());

					// time_toは演算で求める
					Timestamp timeto = null;
					try {
						//long displayTime = MeteoricEarthQuakeService.EARTHQUAKE_LAYER_DISPLAY_TIME;
						Calendar cal = Calendar.getInstance();
						// 初期値は１日後
						cal.setTimeInMillis(data.origintime.getTime() + EARTHQUAKE_LAYER_DISPLAY_TIME);
						if(afterOriginTime == null){
							System.out.println(cal.toString() + " _ " + new Timestamp(cal.getTimeInMillis()).toString());
							timeto = new Timestamp(cal.getTimeInMillis());
						}else{
							// 1日以上経過しているか確認
							if(data.origintime.after(new Timestamp(cal.getTimeInMillis()))){
								timeto = new Timestamp(cal.getTimeInMillis());
							}else{
								// 1秒だけ戻す
								timeto = new Timestamp(afterOriginTime.getTime()-1000L);
							}
						}
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
					}
					obj.put("time_to", TimeUtil.iso8601Formatter.format(timeto));
					obj.put("time_to_local", timeto.toString());

					// 次の震度レイヤ用に今のtime_fromを記憶
					afterOriginTime = data.origintime;
					arr.put(obj);
				}
			}
			// 解析内容を格納
			json.put("total", arr.length());
			json.put("results", arr);

			// エラー文字列を出力
			JSONArray err_arr = new JSONArray();
			for(String str : errorList){
				//JSONObject obj = new JSONObject();
				//obj.put("error", str);
				arr.put(str);
			}
			json.put("errors", err_arr);

			// 出力
			PrintWriter out = response.getWriter();
			out.print( json.toString() );

		}catch(JSONException e){
			//throw new ServiceException(lang.__("Failed to parse seismic intensity layer."));
			throw new ServiceException(lang.__("Failed to parse seismic intensity layer."));
		} catch (IOException e) {
			//throw new ServiceException(lang.__("Failed to output seismic intensity layer."));
			throw new ServiceException(lang.__("Failed to output seismic intensity layer."));
		}

		return null;
	}

	/**
	 * Check existence of seismic intensity layer and return seismic intensity layer record
	 * @return
	 */
	/*
	@Deprecated
	private EarthquakegrouplayerData checkEarthQuakeGroupLayer(){
		EarthquakegrouplayerData earthquakegroupLayer = null;

		// マスターマップIDを取得
		List<MapmasterInfo> mapmasterInfos = mapmasterInfoService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid(), false);
		if(mapmasterInfos==null || mapmasterInfos.size()==0){
			//errorList.add(lang.__("Master map not found."));
			errorList.add(lang.__("Master map not found."));
		}else{
			// 震度グループレイヤ情報を取得する(災害マップも平時マップも同一なので平時マップを取得)
			List<EarthquakegrouplayerData> grouplayers = earthquakegrouplayerDataService.findByMapMasterInfoId(mapmasterInfos.get(0).id, false);
			if(grouplayers==null || grouplayers.size()==0){
				//errorList.add(lang.__("Seismic intensity group not found."));
				errorList.add(lang.__("Seismic intensity group not found."));
			}else{
				// グループレイヤは1枚
				earthquakegroupLayer = grouplayers.get(0);
			}
		}
		return earthquakegroupLayer;
	}
	*/

	/**
	 * 震度レイヤの存在チェックを行い、震度レイヤレコードを返却する
	 * Ver2.0
	 * @return
	 */
	private List<EarthquakelayerData> checkEarthQuakeLayerInfo(Timestamp starttime, Timestamp endtime){

		// 自治体で持つ震度レイヤを取得
		List<EarthquakelayerInfo> earthquakeLayerInfoList = earthquakelayerInfoService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid());
		if(earthquakeLayerInfoList.size() > 0){
			// 基本的に１レコードなので、先頭を取得する
			EarthquakelayerInfo earthquakeLayerInfo = earthquakeLayerInfoList.get(0);
			// 震度レイヤ情報を取得
			//List<EarthquakelayerData> earthquakelayerDataList = earthquakelayerDataService.findByEarthquakeLayerinfoidAndTimerange(earthquakeLayerInfo.id, starttime, endtime);
			return earthquakelayerDataService.findByEarthquakeLayerinfoidAndTimerange(earthquakeLayerInfo.id, starttime, endtime);
		}else{
			// 空で返す
			return new ArrayList<EarthquakelayerData>();
		}
	}

}
