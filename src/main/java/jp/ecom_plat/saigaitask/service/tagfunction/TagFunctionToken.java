/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction;

import java.util.HashMap;

import jp.ecom_plat.saigaitask.service.tagfunction.node.*;

/**
 * 軸解析の結果であるトークンのクラス
 *
 */
public class TagFunctionToken {

    public static final int IDENTIFIER = 1;
    public static final int NUMBER = 3;
    public static final int STRING = 4;
    public static final int KEYWORD = 5;
    public static final int FUNCTION = 6;
    public static final int BOOLEAN_EXPRESSION = 20;
    
    protected static HashMap<String, TagFuncionTokenInfo> keywordMap = new HashMap<String, TagFuncionTokenInfo>();
    protected static HashMap<String, TagFuncionTokenInfo> functionMap = new HashMap<String, TagFuncionTokenInfo>();
    
	/**
	 * トークンの情報
	 */
	protected static class TagFuncionTokenInfo {
	    int leftPrecedance;
	    int rightPrecedance;
	    Class<? extends TagFunctionNode> nodeClass;
	    
	    TagFuncionTokenInfo(int leftPrecedance, int rightPrecedance, Class<? extends TagFunctionNode> nodeClass) {
	        this.leftPrecedance = leftPrecedance;
	        this.rightPrecedance = rightPrecedance;
	        this.nodeClass = nodeClass;
	    }
	}
    
    static {
        keywordMap.put(":",					new TagFuncionTokenInfo(700, 701, TFNColon.class));
        keywordMap.put("OR",				new TagFuncionTokenInfo(600, 601, TFNOr.class));
        keywordMap.put("AND",				new TagFuncionTokenInfo(500, 501, TFNAnd.class));
        keywordMap.put("NOT",				new TagFuncionTokenInfo(400, 401, TFNNot.class));
        keywordMap.put("=",					new TagFuncionTokenInfo(300, 301, TFNEqualTo.class));
		keywordMap.put("<>",				new TagFuncionTokenInfo(300, 301, TFNNotEqualTo.class));
		keywordMap.put("!=",				new TagFuncionTokenInfo(300, 301, TFNNotEqualTo.class));
        keywordMap.put(">",					new TagFuncionTokenInfo(300, 301, TFNGreaterThan.class));
        keywordMap.put(">=",				new TagFuncionTokenInfo(300, 301, TFNGreaterThanEqualTo.class));
        keywordMap.put("<",					new TagFuncionTokenInfo(300, 301, TFNLessThan.class));
        keywordMap.put("<=",				new TagFuncionTokenInfo(300, 301, TFNLessThan.class));
        keywordMap.put("INCLUDES",			new TagFuncionTokenInfo(300, 301, TFNIncludes.class));
        keywordMap.put("NOT INCLUDE",		new TagFuncionTokenInfo(300, 301, TFNNotInclude.class));
        keywordMap.put(",",					new TagFuncionTokenInfo(700, 701, TFNCommaList.class));
        keywordMap.put(".",					new TagFuncionTokenInfo(200, 201, TFNDot.class));
        keywordMap.put("(",					new TagFuncionTokenInfo(900,   0, null));
        keywordMap.put(")",					new TagFuncionTokenInfo( -1, 901, null));
        keywordMap.put("_LITERAL",			new TagFuncionTokenInfo( 50,  51, null));
        keywordMap.put("_IDENTIFIER",		new TagFuncionTokenInfo( 50,  51, null));
        
        functionMap.put("VAL",				new TagFuncionTokenInfo(100, 101, TFNVal.class));
        functionMap.put("FILTER",			new TagFuncionTokenInfo(100, 101, TFNFilter.class));
        functionMap.put("ID",				new TagFuncionTokenInfo(100, 101, TFNId.class));
        functionMap.put("SUM",				new TagFuncionTokenInfo(100, 101, TFNSum.class));
        functionMap.put("SUMIF",			new TagFuncionTokenInfo(100, 101, TFNSumIf.class));
        functionMap.put("COUNT",			new TagFuncionTokenInfo(100, 101, TFNCount.class));
        functionMap.put("COUNTIF",			new TagFuncionTokenInfo(100, 101, TFNCountIf.class));
        functionMap.put("AVERAGE",			new TagFuncionTokenInfo(100, 101, TFNAverage.class));
        functionMap.put("MIN",				new TagFuncionTokenInfo(100, 101, TFNMin.class));
        functionMap.put("MAX",				new TagFuncionTokenInfo(100, 101, TFNMax.class));
        functionMap.put("PREF_NAME",		new TagFuncionTokenInfo(100, 101, TFNPrefName.class));
        functionMap.put("CITY_NAME",		new TagFuncionTokenInfo(100, 101, TFNCityName.class));
        functionMap.put("DISASTER_NAME",	new TagFuncionTokenInfo(100, 101, TFNDisasterName.class));
        functionMap.put("DISASTER_START",	new TagFuncionTokenInfo(100, 101, TFNDisasterStart.class));
    };
    
    public int type;
    public String text;
    public double nval;
    public TagFunctionToken prev;
    public TagFunctionToken next;
    public TagFunctionToken prevOp;
    public TagFunctionToken nextOp;
    public TagFunctionNode node;
    public TagFuncionTokenInfo info;
    
    /**
     * コンストラクタ
     */
    public TagFunctionToken()
    {
    }
        
    /**
     * コンストラクタ
     * 
     * @param text
     * @param nval
     * @param prev
     * @param type
     * @param tagFunction
     * @throws TagFunctionException
     */
    public TagFunctionToken(String text, double nval, TagFunctionToken prev, int type, TagFunction tagFunction) throws TagFunctionException
        {
        this.text = text;
        this.nval = nval;
        this.type = type;
        this.prev = prev;
        this.prevOp = prev;
        if (prev != null) {
            prev.next = this;
            prev.nextOp = this;
        }
        
        if (type == IDENTIFIER || type == KEYWORD) {
            info = keywordMap.get(text.toUpperCase());
            if (info != null) {
                this.type = KEYWORD;
                try {
                    if (info.nodeClass != null)
                        node = info.nodeClass.newInstance();
                } catch (Exception e) {
                }
            }
            else {
                if (type == KEYWORD)
                    throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
                info = keywordMap.get("_IDENTIFIER");
                node = new TFNIdentifier();
            }
        }
        else if (type == FUNCTION) {
        	info = functionMap.get(text.toUpperCase());
            if (info != null) {
                this.type = FUNCTION;
                 if (info.nodeClass != null)
					try {
						node = info.nodeClass.newInstance();
					} catch (Exception e) {
					}
           }
            else {
                throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
            }
        }
        else if (type == NUMBER || type == STRING) {
        	info = keywordMap.get("_LITERAL");
        	node = new TFNLiteral();
        }
        else
        	throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
        
        if (node != null)
        	node.tagFunction = tagFunction;
    }

    /**
     * オペランドのリンクを削除する
     */
    public void remove()
    {
        if (prev != null)
            prev.next = next;
        if (next != null)
            next.prev = prev;
    }
    
    /**
     * オペレータのリンクを削除する
     */
    public void removeOp()
    {
        if (prevOp != null)
            prevOp.nextOp = nextOp;
        if (nextOp != null)
            nextOp.prevOp = prevOp;
    }
    
}
