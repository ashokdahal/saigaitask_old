/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.CkanauthInfo;
import jp.ecom_plat.saigaitask.service.db.CkanauthInfoService;
import jp.ecom_plat.saigaitask.util.CKANUtil;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * クリアリングハウスの検索を行うサービスクラスです.
 */
@org.springframework.stereotype.Service
public class CkanService extends ClearinghouseService {
	
    @Resource
    private LoginDataDto loginDataDto;
	@Resource
	protected CkanauthInfoService ckanauthInfoService;

    /**
     * クリアリングハウスよりメタデータを取得する
     * @param metadataid メタデータID
     * @param isTraining 訓練フラグ
     * @return メタデータのJSONObject
     * @throws JSONException
     */
    public JSONObject getRecordById(String metadataid, boolean isTraining) throws JSONException {
		long starttime = System.currentTimeMillis();

		String ckanurl = isTraining ? Config.getString("CKAN_URL_TRAINING") : Config.getString("CKAN_URL");
		JSONObject json = CKANUtil.getRecordById(ckanurl, getApiKey(isTraining), metadataid);
		
		// 訓練モードで取得できなかった場合は URL を変えて再実行
		if (json == null && isTraining) {
			ckanurl = Config.getString("CKAN_URL");
			json = CKANUtil.getRecordById(ckanurl, getApiKey(false), metadataid);
		}

		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] CkanService.getRecordById elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");

		return json;
	}
    
    /**
     * CKAN の API キーを取得する
     * @return
     */
    public String getApiKey(boolean isTraining) {
    	long localgovinfoid = loginDataDto.getLocalgovinfoid();
    	List<CkanauthInfo> list = ckanauthInfoService.findByLocalgovinfoid(localgovinfoid);
    	for (CkanauthInfo info : list) {
    		return isTraining ? info.getTrainingauthkey() : info.getAuthkey();
    	}
		//throw new ServiceException(lang.__("API key can't be acquired."));
    	return null;
    }

}
