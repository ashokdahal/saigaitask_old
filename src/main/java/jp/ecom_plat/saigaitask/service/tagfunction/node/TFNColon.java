/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

public class TFNColon extends TagFunctionNode {

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	if (token.prev == null || token.next == null ||
    		!(token.prev.node instanceof TFNIdentifier && token.next.node instanceof TFNLiteral &&
    		  ((TFNLiteral)token.next.node).dataType == TagFunctionToken.STRING)) {
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	}

    	TFNIdentifier identifier = (TFNIdentifier)token.prev.node;
    	TFNLiteral literal = (TFNLiteral)token.next.node;

    	if ("IS_EDITABLE".equalsIgnoreCase(identifier.name))
    		tagFunction.editable = "true".equalsIgnoreCase(literal.value);

		addChild(token.prev.node);
		firstChild.addNext(token.next.node);
		token.next.remove();
		token.prev.remove();
    }

    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("value", "");
    	return result;
    }

}
