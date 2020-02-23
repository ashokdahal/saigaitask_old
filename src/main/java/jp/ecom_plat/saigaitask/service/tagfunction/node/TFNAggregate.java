/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.text.DecimalFormat;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * 集計関数のクラス
 */
public class TFNAggregate extends TagFunctionNode {
	protected boolean allowFormat = true;
	protected DecimalFormat decimalFormat;
	
	public TFNAggregate() {
		super();
    	shareAlways = true;
	}

    public void init(TagFunctionToken token) throws TagFunctionException {
    	super.init(token);    	
     	if (token.next.node == null ||
    		!(token.next.node instanceof TFNCommaList && ((TFNCommaList)token.next.node).firstChild == null ||
    		  allowFormat && token.next.node instanceof TFNLiteral))
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	
    	// フォーマット指定がある場合
    	if (token.next.node instanceof TFNLiteral) {
    		TFNLiteral literal = (TFNLiteral)token.next.node;
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
    protected String format(double value) throws TagFunctionException {
    	return decimalFormat.format(value);
    }

}
