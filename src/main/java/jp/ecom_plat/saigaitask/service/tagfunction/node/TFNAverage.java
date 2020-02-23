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
 * AVERAGE のクラス
 */
public class TFNAverage extends TFNAggregate {
	protected boolean format = false;

	public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	if (!isNumericType(tagFunction.attr))
    		throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);

    	List<BeanMap> valueList = (List<BeanMap>)arg.get("valueList");
    	double value = 0.0;
    	for (BeanMap record : valueList) {
			value += getDouble(record, tagFunction.attr);
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	if (valueList.size() != 0)
    		value /= (double)valueList.size();
    	
    	// フォーマットの指定がある場合
    	if (decimalFormat != null)
    		result.put("value", format(value));
    	else
    		result.put("value", "" + value);
    	return result;
    }

}
