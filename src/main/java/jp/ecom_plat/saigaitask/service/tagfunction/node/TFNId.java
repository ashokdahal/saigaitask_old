/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunction;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * ID(n) のクラス
 */
public class TFNId extends TFNFunction {

    public TFNId() {
    	shareAlways = true;
	}

    public TFNId(TagFunction tagFunction) {
		this.tagFunction = tagFunction;
    	shareAlways = true;
	}

 	protected int argCount() {
		return 1;
	}

	protected boolean processArg(TagFunctionNode node, int n) throws TagFunctionException {
    	switch (n) {
    	case 0:
    		if (!(node instanceof TFNLiteral) || ((TFNLiteral)node).dataType != TagFunctionToken.NUMBER)
    			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
    		node.tagFunction.rowId = (int)((TFNLiteral)node).nval;
    		if (node.tagFunction.rowId <= 0)
    			throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    		break;
    	}
    	return true;
    }

    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {

    	// ページ番号より開始レコード番号を求める
    	List<BeanMap> valueList = (List<BeanMap>)arg.get("valueList");
    	if (valueList == null)
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	int page = ((Integer)arg.get("page")).intValue();
    	int row = tagFunction.rowId - 1 + tagFunction.share.maxRowId * page;

    	// 属性の値を追加する
    	String value = "";
    	if (row < valueList.size()) {
    		if (!valueList.get(row).containsKey(tagFunction.attr))
    			throw new TagFunctionException(TagFunctionException.INVALID_ATTR);
    		Object obj = valueList.get(row).get(tagFunction.attr);
			if (obj == null)
				value = "";
			else
				value = obj.toString();
    	}

    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("value", value);
    	return result;
    }

}
