/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * 関数のクラス
 */
public abstract class TFNFunction extends TagFunctionNode {

	public void init(TagFunctionToken token) throws TagFunctionException
    {
    	super.init(token);
    	if (token.next.node instanceof TFNCommaList) {
	    	int n = 0;
	        for (TagFunctionNode node = token.next.node.firstChild; node != null; node = node.nextSib)
	        	n++;
    		if (argCount() != n)
    			throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    		n = 0;
	        for (TagFunctionNode node = token.next.node.firstChild; node != null; node = node.nextSib) {
	        	if (!processArg(node, n))
	        		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
	        	n++;
	        }
    	}
    	else if (token.next.node instanceof TFNLiteral || token.next.node instanceof TFNIdentifier) {
    		if (argCount() != 1)
    			throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
        	if (!processArg(token.next.node, 0))
        		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	}
    	else
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	token.next.remove();
    }
	
	/**
	 * 引数の数を返す
	 * 
	 * @return
	 */
	protected int argCount() {
		return 0;
	}
    
    /**
     * 関数の引数を処理する
     * 
     * @param node
     * @param n
     * @return
     * @throws TagFunctionException
     */
    protected boolean processArg(TagFunctionNode node, int n) throws TagFunctionException {
    	return true;
    }

}
