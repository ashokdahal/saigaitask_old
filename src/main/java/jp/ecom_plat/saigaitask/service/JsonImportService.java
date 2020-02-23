package jp.ecom_plat.saigaitask.service;

import static jp.ecom_plat.saigaitask.entity.Names.jsonimportlayerInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResult.AttrResult;
import jp.ecom_plat.map.exception.EcommapConflictException;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.JsonimportapiInfo;
import jp.ecom_plat.saigaitask.entity.db.JsonimportlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.JsonimportapiInfoService;
import jp.ecom_plat.saigaitask.service.db.JsonimportlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.ReschedulableExecutor;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

/**
 * JSON 取り込み機能
 */
@org.springframework.stereotype.Service
public class JsonImportService implements ApplicationRunner  {

	private static final double LON_COMPARE_MARGIN = 0.0000011;
	private static final double LAT_COMPARE_MARGIN = 0.0000009;

	private Map<Long, ReschedulableExecutor> schedulerMap;
	private Map<Long, String> errorMap = new HashMap<Long, String>();
	private Logger logger = Logger.getLogger(JsonImportService.class);

	@Resource
	protected JsonimportapiInfoService jsonimportapiInfoService;
	@Resource
	protected JsonimportlayerInfoService jsonimportlayerInfoService;
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected MapService mapService;
	@Resource
	protected SaigaiTaskDBLang lang;
	@Resource
	protected TableFeatureService tableFeatureService;
	@Resource
	protected NonWebAccessService nonWebAccessService;

	/**
	 * クラス内部で使用する Exception
	 */
	private class JsonImportException extends Exception {
		private static final long serialVersionUID = 1L;
		private JsonImportException(String msg) {
			super(msg);
		}
	}

	/**
	 * 一定時間間隔で実行される処理
	 */
	private class TimerTask implements Runnable {
		private JsonimportapiInfo apiInfo;
		private TableFeatureService tableFeatureService = null;
		private Set<Class<?>> classSet = new HashSet<Class<?>>();
		private LoginDataDto loginDataDto = new LoginDataDto();

		private TimerTask(JsonimportapiInfo apiInfo) {
			this.apiInfo = apiInfo;
		}

		@Override
		public void run() {

			// 訓練中の災害がある場合は何もしない
			List<TrackData> trainingTrackDataList = trackDataService.findByCurrentTrackDatas(apiInfo.localgovinfoid, true);
			if (trainingTrackDataList.size() > 0)
				return;
			// 災害がない場合は何もしない
			List<TrackData> trackDataList = trackDataService.findByCurrentTrackDatas(apiInfo.localgovinfoid);
			if (trackDataList.size() == 0)
				return;
			TrackData trackData = trackDataList.get(0);

			logger.info("json import start");
			try {

				// HTTP リクエストを送信する
				JSONObject result = sendHttpRequest(apiInfo.url, apiInfo.authkey);

				// 取得成功
				if (result.getBoolean("success")) {
					updateLayer(result, trackData.getId());
				}

				// 取得エラー
				else {
					throw new JsonImportException(result.getString("error"));
				}

				// エラーがなければ errorMap から削除
				synchronized(errorMap) {
					errorMap.remove(apiInfo.getLocalgovinfoid());
				}

			} catch (Exception e) {
				// errorMap に追加
				String error = e instanceof JsonImportException ? e.getMessage() : e.toString();
				synchronized(errorMap) {
					errorMap.put(apiInfo.getLocalgovinfoid(), error);
				}
				logger.error(error);
			}
			logger.info("json import end");
		}

	    /**
	     * 取得した JSON データより、地物の追加・更新・削除を行う
	     * 
	     * @param result
	     * @param trackDataId
	     * @throws Exception
	     */
	    private void updateLayer(JSONObject result, Long trackDataId) throws Exception {

	    	// 「JSON連携更新対象レイヤテーブル」検索
			BeanMap conditions = new BeanMap();
			conditions.put(jsonimportlayerInfo().jsonimportapiinfoid().toString(), apiInfo.getId());
			List<JsonimportlayerInfo> jLayerInfoList = jsonimportlayerInfoService.findByCondition(conditions, null, null, null, null);

			// category で検索するための Map を作成、同時に TablemasterInfo の  Map を作成
			Map<String, JsonimportlayerInfo> jLayerInfoMap = new HashMap<String, JsonimportlayerInfo>();
			Map<Long, TablemasterInfo> tableMap = new HashMap<Long, TablemasterInfo>();
			for (JsonimportlayerInfo fLayerInfo : jLayerInfoList) {
				jLayerInfoMap.put(fLayerInfo.getCategory(), fLayerInfo);
				tableMap.put(fLayerInfo.tablemasterinfoid, fLayerInfo.tablemasterInfo);
			}

			// 削除用に全地物の _orgid の Set を作成する
			Map<String, Set<Long>> deleteFeatureMap = new HashMap<String, Set<Long>>();
			for (TablemasterInfo tablemasterInfo : tableMap.values()) {
				Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
				LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(tablemasterInfo.getLayerid());
				vecLayerInfo.add(layerInfo);
				FeatureResultList featureResultList = FeatureDB.searchFeatureBbox(null, 0, vecLayerInfo, null, null, null,
						0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, null);
				for (int j = 0; j < featureResultList.countResult(); j++) {
					FeatureResult featureResult = featureResultList.getResult(j);
					Set<Long> orgidSet = deleteFeatureMap.get(tablemasterInfo.getLayerid());
					if (orgidSet == null) {
						orgidSet = new HashSet<Long>();
						deleteFeatureMap.put(tablemasterInfo.getLayerid(), orgidSet);
					}
					orgidSet.add(Long.valueOf(featureResult.featureId));
				}
			}

			Map<String, Map<String, FeatureResult>> layerFeatureMap = new HashMap<String, Map<String, FeatureResult>>();
			JSONArray jsonArray = result.getJSONArray("response");

			// JSON データの全地物に対して実行する
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject record = jsonArray.getJSONObject(i);
				AttrResult attrResult;
				FeatureResult featureResult;
				boolean save = false;

				// 「区分」を取得
				String category = record.getString("category");
				JsonimportlayerInfo jLayerInfo = jLayerInfoMap.get(category);
				if (jLayerInfo == null) {
					throw new JsonImportException(lang.__("\"category\" が不正です（{0}）.", category));
				}

				// レイヤごとに全地物を取得する
				String layerId = jLayerInfo.tablemasterInfo.getLayerid();
				Map<String, FeatureResult> featureMap = layerFeatureMap.get(layerId);
				if (featureMap == null) {
					Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
					LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
					vecLayerInfo.add(layerInfo);
					FeatureResultList featureResultList = FeatureDB.searchFeatureBbox(null, 0, vecLayerInfo, null, null, null,
							0, 0, false, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, null);

					featureMap = new HashMap<String, FeatureResult>();
					for (int j = 0; j < featureResultList.countResult(); j++) {
						featureResult = featureResultList.getResult(j);
						// ユニークID（整理番号）の値を取得
						if (jLayerInfo.getNoattr() != null) {
							attrResult = featureResult.getAttrResult(jLayerInfo.getNoattr());
							if (attrResult != null)
								featureMap.put(attrResult.getAttrValue(), featureResult);
						}
					}
					layerFeatureMap.put(layerId, featureMap);
				}

				HashMap<String, String> attrs = new HashMap<String, String>();

				// ユニークID（整理番号）
				String uniqueId = null;
				if (jLayerInfo.getNoattr() != null) {
					uniqueId = record.getString("no");
					attrs.put(jLayerInfo.getNoattr(), uniqueId);
				}

				// _orgid を取得
				featureResult = featureMap.get(uniqueId);
				Long orgid = 0L;
				// 対応する既存の地物がある場合は、更新
				if (featureResult != null) {
					// 更新対象の地物を削除対象から除外する
					Set<Long> orgidSet = deleteFeatureMap.get(layerId);
					orgid = Long.valueOf(featureResult.featureId);
					orgidSet.remove(orgid);
				}
				// 既存の地物がない場合は、新規
				else
					save = true;

				// 経度緯度
				if (record.has("locations")) {
					JSONObject locations = record.getJSONObject("locations");
					String lonStr = locations.getString("lon");
					String latStr = locations.getString("lat");
					attrs.put("theGeom", "POINT(" + lonStr + " " + latStr + ")");
					// 経度緯度が変化しているかチェックする
					if (featureResult != null) {
						double lonlat[] = featureResult.getLonLat();
						if (Math.abs(lonlat[0] - Double.valueOf(lonStr)) > LON_COMPARE_MARGIN || 
							Math.abs(lonlat[1] - Double.valueOf(latStr)) > LAT_COMPARE_MARGIN)
							save = true;
					}
				}

				// Contents
				if (jLayerInfo.getContentsattr() != null && record.has("contents")) {
					String contents = record.getString("contents");
					attrs.put(jLayerInfo.getContentsattr(), contents);
					// Contents が変化しているかチェックする
					if (featureResult != null) {
						attrResult = featureResult.getAttrResult(jLayerInfo.getContentsattr());
						if (attrResult == null || !strEqual(contents, attrResult.getAttrValue()))
							save = true;
					}
				}

				// Category_text
				if (jLayerInfo.getCategorytextattr() != null && record.has("category_text")) {
					String categoryText = record.getString("category_text");
					attrs.put(jLayerInfo.getCategorytextattr(), categoryText);
					// Category_text が変化しているかチェックする
					if (featureResult != null) {
						attrResult = featureResult.getAttrResult(jLayerInfo.getCategorytextattr());
						if (attrResult == null || !strEqual(categoryText, attrResult.getAttrValue()))
							save = true;
					}
				}

				// Subject
				if (jLayerInfo.getSubjectattr() != null && record.has("subject")) {
					String subject = record.getString("subject");
					attrs.put(jLayerInfo.getSubjectattr(), subject);
					// Subject が変化しているかチェックする
					if (featureResult != null) {
						attrResult = featureResult.getAttrResult(jLayerInfo.getSubjectattr());
						if (attrResult == null || !strEqual(subject, attrResult.getAttrValue()))
							save = true;
					}
				}

				//  Reception_datetime
				if (jLayerInfo.getReceptiondatetimeattr() != null && record.has("reception_datetime")) {
					String receptiondatetime = record.getString("reception_datetime");
					attrs.put(jLayerInfo.getReceptiondatetimeattr(), receptiondatetime);
					// Reception_datetime が変化しているかチェックする
					if (featureResult != null) {
						attrResult = featureResult.getAttrResult(jLayerInfo.getReceptiondatetimeattr());
						if (attrResult == null || !strEqual(receptiondatetime, attrResult.getAttrValue()))
							save = true;
					}
				}

				// 地物の追加／更新
				if (save) {
					Map<Long, HashMap<String, String>> features = new HashMap<Long, HashMap<String, String>>();
					features.put(Long.valueOf(orgid), attrs);
					updateFeature(jLayerInfo.tablemasterInfo, trackDataId, features, null);
				}
			}

			// 地物の削除
			for (TablemasterInfo tablemasterInfo : tableMap.values()) {
				Set<Long> orgidSet = deleteFeatureMap.get(tablemasterInfo.getLayerid());
				if (orgidSet == null)
					continue;
				Map<Long, HashMap<String, String>> features = new HashMap<Long, HashMap<String, String>>();
				List<Long> delList = new ArrayList<Long>();
				for (Long orgid: orgidSet) {
					features.put(orgid, null);
					delList.add(orgid);
				}
				if (delList.size() > 0) {
					updateFeature(tablemasterInfo, trackDataId, features, delList);
				}
			}
	    }

		/**
		 * 地物の追加・更新・削除を行う
		 * 
		 * @param master
		 * @param trackDataId
		 * @param features
		 * @param delfids
		 * @return
		 * @throws EcommapConflictException
		 * @throws Exception
		 */
		private long updateFeature(TablemasterInfo master, long trackDataId,
				Map<Long, HashMap<String, String>> features, List<Long> delfids) throws EcommapConflictException, Exception {

	    	// TableFeatureService が使用する loginDataDto を準備
	    	loginDataDto.setLocalgovinfoid(apiInfo.localgovinfoid);
		    loginDataDto.setTrackdataid(trackDataId);

			// 初回処理
			if (tableFeatureService == null) {

		    	// TableFeatureService より間接的に参照される、loginDataDto を使用しているクラス
		    	classSet.add(TriggerAlertService.class);
		    	classSet.add(StationService.class);
		    	classSet.add(MapService.class);

		    	// TableFeatureService のインスタンスを生成する
		    	tableFeatureService = (TableFeatureService)nonWebAccessService.createService(
						TableFeatureService.class, classSet, loginDataDto);
			}

			for (; ; ) {
				try {
					// TableFeatureService の execute メソッドを呼び出す
					return tableFeatureService.execute(master, null, features, null, delfids);
				} catch (BeanCreationException ex) {
					// 失敗した場合（classSet に漏れがあった場合）は classSet を更新して TableFeatureService のインスタンスを再生成する
					tableFeatureService = (TableFeatureService)nonWebAccessService.createService(
							TableFeatureService.class, classSet, loginDataDto, ex);
					// ロールバックができないため、再実行は行わない
					throw ex;
				}
			}
		}
	}

	/**
	 * null と ""（空文字列）を同一視して、文字列を比較する
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean strEqual(String s1, String s2) {
		return (s1 == null ? "" : s1).equals(s2 == null ? "" : s2);
	}

	/**
	 * サーバ起動時にスケジューラを起動する
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		String delayStr = Config.getString("JSONIMPORT_INITIAL_DELAY");
		long delay = delayStr == null ? 0 : Long.parseLong(delayStr);
		schedulerMap = new HashMap<Long, ReschedulableExecutor>();
		List<JsonimportapiInfo> apiInfoList = jsonimportapiInfoService.findAll();
		for (JsonimportapiInfo apiInfo : apiInfoList) {
			if (apiInfo.getValid() && apiInfo.getInterval() > 0) {
				ReschedulableExecutor scheduler = new ReschedulableExecutor();
				schedulerMap.put(apiInfo.id, scheduler);
				scheduler.start(new TimerTask(apiInfo), apiInfo.getInterval(), delay);
			}
		}
	}

	/**
	 * 管理画面より変更された設定をスケジューラに反映する
	 * 
	 * @param localgovinfoid
	 */
	public void reschedule(JsonimportapiInfo apiInfo) {
		ReschedulableExecutor scheduler = schedulerMap.get(apiInfo.id);
		if (apiInfo.getValid() && apiInfo.getInterval() > 0) {
			synchronized (schedulerMap) {
				if (scheduler == null) {
					scheduler = new ReschedulableExecutor();
					schedulerMap.put(apiInfo.getLocalgovinfoid(), scheduler);
				}
			}
			TimerTask timerTask = new TimerTask(apiInfo);
			scheduler.start(timerTask, apiInfo.getInterval(), 0);
			return;
		}

		else if (scheduler != null)
			scheduler.start(null, 0, 0);
	}

    /**
     * HTTP リクエストを送信する
     * 
     * @param url
     * @param authkey
     * @return
     */
    JSONObject sendHttpRequest(String url, String authkey) {
        int httpstatus;
        HttpURLConnection urlcon = null;
    	JSONObject result = new JSONObject();
        try {
            URL urlobj = new URL(url);
            urlcon = (HttpURLConnection)urlobj.openConnection();
            urlcon.setUseCaches(false);
            urlcon.setDefaultUseCaches(false);
            urlcon.setInstanceFollowRedirects(true);
            if (StringUtil.isNotEmpty(authkey)) {
                urlcon.setRequestProperty("x-api-key", authkey);
            }
            urlcon.setRequestMethod("GET");
            httpstatus = urlcon.getResponseCode();
            StringBuilder sb = new StringBuilder();
            String line;
            if (httpstatus == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null)
                    sb.append(line);
                String jsonStr = sb.toString();
                try {
	                JSONArray jsonAry = new JSONArray(jsonStr);
	                result.put("success", true);
	                result.put("response", jsonAry);
                } catch (JSONException e) {
	                JSONObject jsonObj = new JSONObject(jsonStr);
	                result.put("success", false);
	                String err = null;
	                if (jsonObj.has("Result") && "Error".equals(jsonObj.get("Result")) && jsonObj.has("Reason"))
		                err = jsonObj.getString("Reason");
	                if (err == null || err.isEmpty())
	                	err = "Error";
	                result.put("error", err);
                }
            }
            else {
            	InputStream is = urlcon.getErrorStream();
            	if (is != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
                    while ((line = br.readLine()) != null)
                        sb.append(line);
            	}
	            result.put("success", false);
	            result.put("error", sb.toString());
            }
        } catch (Exception e) {
            try {
            	result.put("success", false);
				result.put("error", e.toString());
			} catch (JSONException e1) {
			}
        } finally {
        }
        return result;
    }

    /**
     * JSON 取り込み処理で発生したエラーを取得する
     * 
     * @param localgovinfoid
     * @return
     */
    public String getError(long localgovinfoid) {
    	synchronized (errorMap) {
    		return errorMap.get(localgovinfoid);
    	}
    }
}
