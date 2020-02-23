/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;

import org.seasar.framework.beans.util.BeanMap;

/**
 * COUNTIF のクラス
 */
public class TFNCountIf extends TFNAggregateIf {
 	
	public TFNCountIf() {
		super();
    	allowFormat = false;
	}
   
    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	List<BeanMap> valueList = (List<BeanMap>)arg.get("valueList");
    	int count = 0;
    	for (BeanMap record : valueList) {
    		if (evalCondition(record)) {
    			count++;
    		}
    	}
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("value", "" + count);
    	return result;
    }

}
