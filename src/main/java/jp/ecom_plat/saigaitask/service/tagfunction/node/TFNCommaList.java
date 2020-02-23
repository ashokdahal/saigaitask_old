/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * カンマ区切りリストのクラス
 */
public class TFNCommaList extends TagFunctionNode {
	TagFunctionNode lastSib;

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);

    	// 左側の項目を追加
    	if (token.prev.node == null ||
			!(token.prev.node instanceof TFNLiteral) &&
			!(token.prev.node instanceof TFNIdentifier) && 
			!(token.prev.node instanceof TFNCommaList) &&
			!(token.prev.node instanceof TFNConditionNode))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	TagFunctionNode lastNode;
    	
    	// 左側がカンマリストの場合は末尾に追加
    	if (token.prev.node instanceof TFNCommaList) {
    		addChild(token.prev.node.firstChild);
    		lastNode = ((TFNCommaList)token.prev.node).lastSib;
    	}
    	
    	// それ以外の場合は先頭に追加
    	else {
    		addChild(token.prev.node);
    		lastNode = token.prev.node;
    	}
    	token.prev.remove();

    	// 右側の項目を追加
    	if (token.next == null ||
			!(token.next.node instanceof TFNLiteral) && 
			!(token.next.node instanceof TFNIdentifier))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
   		lastNode.addNext(token.next.node);
   		// 末尾ノードを記録
   		lastSib = token.next.node;
   		token.next.remove();
     }

}
