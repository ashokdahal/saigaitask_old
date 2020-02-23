/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;

/**
 * PREF_NAME() のクラス
 */
public class TFNPrefName extends TFNFunction {

    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {

    	LoginDataDto loginDataDto = tagFunction.tagFunctionService.getLoginDataDto();
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("value", loginDataDto.getLocalgovInfo().pref);
    	return result;
    }
    
}
