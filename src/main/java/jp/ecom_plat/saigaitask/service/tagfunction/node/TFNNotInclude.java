/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

import org.seasar.framework.beans.util.BeanMap;

/**
 * NOT INCLUDE のクラス
 */
public class TFNNotInclude extends TFNComparison {

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	if (!(firstChild instanceof TFNIdentifier && firstChild.nextSib instanceof TFNLiteral))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    }

	protected boolean evalCondition(BeanMap record) throws TagFunctionException {
		TFNIdentifier identifier = (TFNIdentifier)firstChild;
		TFNLiteral literal = (TFNLiteral)firstChild.nextSib;
		
		// データ型チェック
		if (identifier.isNumericType() || literal.dataType != TagFunctionToken.STRING)
			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
		
		String val = (String)record.get(identifier.name);
		return val.indexOf(literal.value) < 0;
	}

}
