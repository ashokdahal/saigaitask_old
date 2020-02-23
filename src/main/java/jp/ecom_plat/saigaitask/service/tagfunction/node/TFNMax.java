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
 * MAX() のクラス
 */
public class TFNMax extends TFNAggregate {
    
    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	if (!isNumericType(tagFunction.attr))
    		throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);

    	List<BeanMap> valueList = (List<BeanMap>)arg.get("valueList");
    	double max = 0.0;
		boolean first = true;
    	for (BeanMap record : valueList) {
			double value = getDouble(record, tagFunction.attr);
			if (first || max < value)
				max = value;
			first = false;
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	// フォーマットの指定がある場合
    	if (decimalFormat != null)
    		result.put("value", format(max));
    	// 属性が整数型の場合
    	if (isIntegerType(tagFunction.attr))
			result.put("value", "" + (int)max);
		// 整数型以外の場合
		else
			result.put("value", "" + max);
    	
    	return result;
    }

}
