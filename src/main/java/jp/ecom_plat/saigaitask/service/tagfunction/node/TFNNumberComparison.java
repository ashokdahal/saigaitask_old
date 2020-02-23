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
 * 数値の比較演算子のクラス
 */
public abstract class TFNNumberComparison extends TFNComparison {

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	if (!(firstChild instanceof TFNIdentifier && firstChild.nextSib instanceof TFNLiteral) &&
    		!(firstChild instanceof TFNLiteral && firstChild.nextSib instanceof TFNIdentifier))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    }

	@Override
	protected boolean evalCondition(BeanMap record) throws TagFunctionException {
		
		// Identifier と Literal のノードを取得
		TFNIdentifier identifier;
		TFNLiteral literal;
		if (firstChild instanceof TFNIdentifier) {
			identifier = (TFNIdentifier)firstChild;
			literal = (TFNLiteral)firstChild.nextSib;
		}
		else {
			identifier = (TFNIdentifier)firstChild.nextSib;
			literal = (TFNLiteral)firstChild;
		}

		// データ型チェック
		if (!identifier.isNumericType() || literal.dataType != TagFunctionToken.NUMBER)
			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
		
		// 値を比較
		return compare(getDouble(record, identifier.name), literal.nval);
	}
	
	protected abstract boolean compare(double val1, double val2) throws TagFunctionException;
	
}
