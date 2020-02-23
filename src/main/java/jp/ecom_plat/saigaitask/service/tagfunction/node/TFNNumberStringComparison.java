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
 * 数値と文字列の比較演算子のクラス
 */
public abstract class TFNNumberStringComparison extends TFNComparison {

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

		// 数値型の場合
		if (identifier.isNumericType()) {
			
			// データ型チェック
			if (literal.dataType != TagFunctionToken.NUMBER)
				throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);

			// 比較
			return compareNum(getDouble(record, identifier.name), literal.nval);
		}
		
		// 文字型の場合
		else {
			
			// データ型チェック
			if (literal.dataType != TagFunctionToken.STRING)
				throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
			
			// 比較
			return compareStr(getString(record, identifier.name), literal.value);
		}
	}
	
	protected abstract boolean compareNum(double attr, double Literal) throws TagFunctionException;
	
	protected abstract boolean compareStr(String attr, String Literal) throws TagFunctionException;

}
