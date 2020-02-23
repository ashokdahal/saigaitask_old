/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * 比較演算子のクラス
 */
public abstract class TFNComparison extends TFNConditionNode {

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	if (token.prev == null || token.next == null ||
    		!(token.prev.node instanceof TFNIdentifier && token.next.node instanceof TFNLiteral ||
    		  token.prev.node instanceof TFNLiteral && token.next.node instanceof TFNIdentifier)) {
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	}

		addChild(token.prev.node);
		firstChild.addNext(token.next.node);
		token.next.remove();
		token.prev.remove();
    }

}
