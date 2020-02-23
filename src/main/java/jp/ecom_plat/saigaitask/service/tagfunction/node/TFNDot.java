/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.Map;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunction;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * ドッと（.）のクラス
 */
public class TFNDot extends TagFunctionNode {
	
	public TFNDot() {
	}
	
	public TFNDot(TagFunction tagFunction) {
		this.tagFunction = tagFunction;
	}

	public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);
    	
    	// . の前後のノードの組み合わせをチェックする
    	boolean prevIsVal = token.prev.node instanceof TFNVal ||
    		token.prev.node instanceof TFNDot && token.prev.node.firstChild instanceof TFNVal;
    	boolean nextIsFilter = token.next.node instanceof TFNFilter;
    	boolean nextIsSetFn = token.next.node instanceof TFNAggregate || token.next.node instanceof TFNAggregateIf;
    	boolean nextIsId = token.next.node instanceof TFNId;
    	
    	if (!(prevIsVal && (nextIsFilter || nextIsId || nextIsSetFn)))
     		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	
    	addChild(token.prev.node);
    	token.prev.remove();
    	firstChild.addNext(token.next.node);
    	token.next.remove();
    }
    
    public Map<String, Object> load(Map<String, Object> arg) throws TagFunctionException {
    	Map<String, Object> data = firstChild.execute(arg);
    	return firstChild.nextSib.execute(data);
    }
    
    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	Map<String, Object> data = firstChild.execute(arg);
    	return firstChild.nextSib.execute(data);
    }

}
