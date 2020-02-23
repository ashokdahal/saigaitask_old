/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunction;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * リテラル（数値、文字列）のクラス
 */
public class TFNLiteral extends TagFunctionNode {
    protected int dataType;
    protected String value;
    protected double nval;

    public TFNLiteral()
    {
        super();
    }
    
    public TFNLiteral(TagFunction tagFunction, int dataType, String value, double nval)
    {
        super();
        this.tagFunction = tagFunction;
        this.dataType = dataType;
        this.value = value;
        this.nval = nval;
    }
    
    public void init(TagFunctionToken token) throws TagFunctionException
    {
    	super.init(token);
    	dataType = token.type;
    	if (token.type != TagFunctionToken.STRING && token.type != TagFunctionToken.NUMBER)
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
        value = token.text;
        nval = token.nval;
    }
    
    /**
     * リテラルの値を得る
     * 
     * @return リテラルの値
     */
    public String getValue ()
    {
        return value;
    }
	
	@Override
	public boolean equals(Object node) {
		return node instanceof TFNLiteral && value.equals(((TFNLiteral)node).value);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ value.hashCode();
	}

}
