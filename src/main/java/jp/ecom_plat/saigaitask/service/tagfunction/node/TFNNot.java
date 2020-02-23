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
 * NOT のクラス
 */
public class TFNNot extends TFNConditionNode {

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	if (!(token.next.node instanceof TFNConditionNode))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    		
		addChild(token.next.node);
		token.next.remove();
    }

	@Override
	protected boolean evalCondition(BeanMap record) throws TagFunctionException {
		return	!((TFNConditionNode)firstChild).evalCondition(record);
	}

}
