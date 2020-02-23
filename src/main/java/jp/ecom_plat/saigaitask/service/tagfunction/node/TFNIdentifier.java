/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * 識別子（属性名）のクラス
 */
public class TFNIdentifier extends TagFunctionNode {
	protected String name;
	
    public TFNIdentifier() {
    	super();
    	shareAlways = true;
	}
	
	public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	if (token.type!= TagFunctionToken.IDENTIFIER)
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
        name = token.text;
    }
    
    /**
     * 属性が数値型かどうかを返す
     * 
     * @return
     * @throws TagFunctionException
     */
    public boolean isNumericType() throws TagFunctionException {
    	return tagFunction.share.isNumericType(name);
    }
	
	@Override
	public boolean equals(Object node) {
		return node instanceof TFNIdentifier && name.equals(((TFNIdentifier)node).name);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ name.hashCode();
	}

}
