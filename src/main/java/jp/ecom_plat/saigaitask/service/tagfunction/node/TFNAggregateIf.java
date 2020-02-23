/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.text.DecimalFormat;

import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * 条件付き集計関数のクラス
 */
public class TFNAggregateIf extends TagFunctionNode {
	protected boolean allowFormat = true;
	protected DecimalFormat decimalFormat;

    public void init(TagFunctionToken token) throws TagFunctionException {
     	super.init(token);
    	if (token.next.node == null ||
    		!(token.next.node instanceof TFNConditionNode ||
    		  allowFormat && token.next.node instanceof TFNCommaList &&
    		  token.next.node.firstChild != null &&
    		  token.next.node.firstChild instanceof TFNConditionNode &&
    		  token.next.node.firstChild.nextSib != null &&
    		  token.next.node.firstChild.nextSib instanceof TFNLiteral &&
    		  token.next.node.firstChild.nextSib.nextSib == null))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	
    	// フォーマット指定がない場合
    	if (token.next.node instanceof TFNConditionNode) {
    		addChild(token.next.node);
    	}
    	
    	// フォーマット指定がある場合
    	else {
    		addChild(token.next.node.firstChild);
    		TFNLiteral literal = (TFNLiteral)token.next.node.firstChild.nextSib;
    		if (literal.dataType != TagFunctionToken.STRING)
    			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
    		try {
				decimalFormat = new DecimalFormat(literal.value);
			} catch (Exception e) {
				throw new TagFunctionException(TagFunctionException.INVALID_FORMAT_STRING);
			}
    	}
    
    	token.next.remove();
    }
     
    /**
     * フォーマットを使用して数値を文字列に変換する
     * 
     * @param value
     * @return
     * @throws TagFunctionException 
     */
    protected String format(double value) {
		return decimalFormat.format(value);
    }
  
    /**
     * 条件式を評価する
     * 
     * @param record
     * @return
     * @throws TagFunctionException
     */
    protected boolean evalCondition(BeanMap record) throws TagFunctionException {
    	return ((TFNConditionNode)firstChild).evalCondition(record);
    }

}
