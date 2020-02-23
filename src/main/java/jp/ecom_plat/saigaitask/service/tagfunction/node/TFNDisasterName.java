/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;

/**
 * DISASTER_NAME() のクラス
 */
public class TFNDisasterName extends TFNFunction {

    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {

    	TrackData trackData = tagFunction.tagFunctionService.getTrackData();
    	Map<String, Object> result = new HashMap<String, Object>();
    	if(trackData != null){
        	result.put("value", trackData != null ? trackData.name : "");
    	}else{
        	result.put("value", "");
    	}
    	return result;
    }

}
