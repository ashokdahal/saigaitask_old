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
 * COUNT のクラス
 */
public class TFNCount extends TFNAggregate {
	
	public TFNCount() {
		super();
    	allowFormat = false;
	}
    
    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	List<BeanMap> valueList = (List<BeanMap>)arg.get("valueList");
    	Map<String, Object> result = new HashMap<String, Object>();
		result.put("value", "" + valueList.size());
    	return result;
    }

}
