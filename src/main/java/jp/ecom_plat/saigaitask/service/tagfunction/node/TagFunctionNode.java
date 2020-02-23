/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.Map;

import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunction;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * タグ関数のパーズ結果である構文木のノード
 */
public abstract class TagFunctionNode {

	public TagFunction tagFunction;
    public TagFunctionNode firstChild;
    public TagFunctionNode nextSib;
	protected boolean shareAlways = false;
	
    /**
     * ノードの初期化、タグ関数をパースして tree を構築する
     * 
     * @param token TagFunctionToken　インスタンス
     * @throws TagFunctionException
     */
    public void init(TagFunctionToken token) throws TagFunctionException {
    	if (token == null)
    		throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
    	token.removeOp();
    }

    /**
     * 子ノードを追加する
     * 
     * @param node TagFunctionNode インスタンス
     */
    public void addChild(TagFunctionNode node) {
        firstChild = node;
    }

    /**
     * 次のノードを追加する
     * 
     * @param node TagFunctionNode インスタンス
     */
    public void addNext(TagFunctionNode node) {
        nextSib = node;
    }
    
    /**
     * DB よりデータを取得する
     * @throws TagFunctionException 
     * 
     */
    public void load()  {
    	if (!shareAlways) {
			if (firstChild != null)
				firstChild.load();
			if (nextSib != null)
				nextSib.load();
    	}
    }

	/**
	 * タグ関数の実行結果を得る
	 * 
	 * @param arg
	 * @return
	 * @throws TagFunctionException
	 */
	public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
		return arg;
	}
	
	private TagFunctionNode childNotShare() {
		return firstChild == null || firstChild.shareAlways && !tagFunction.tagFunctionService.generatingIdnode ? null : firstChild;
	}
	
	private TagFunctionNode nextNotShare() {
		return nextSib == null || nextSib.shareAlways && !tagFunction.tagFunctionService.generatingIdnode ? null : nextSib;
	}
    
    /**
     * 属性が数値型かどうかを返す
     * 
     * @return
     * @throws TagFunctionException
     */
    public boolean isNumericType(String attrName) throws TagFunctionException {
    	return tagFunction.share.isNumericType(attrName);
    }
    
    /**
     * 属性が整数型かどうかを返す
     * 
     * @return
     * @throws TagFunctionException
     */
    public boolean isIntegerType(String attrName) throws TagFunctionException {
    	return tagFunction.share.isIntegerType(attrName);
    }
    
    /**
     * 数値型の属性値を取得する
     * 
     * @param strVal
     * @return
     * @throws TagFunctionException 
     */
    protected double getDouble(BeanMap record, String name) throws TagFunctionException {
    	if (!record.containsKey(name))
    		throw new TagFunctionException(TagFunctionException.INVALID_ATTR);
    	String strVal = (String)record.get(name);
    	double value = 0;
		if (strVal != null && !strVal.trim().isEmpty()) {
			try {
				value = Double.parseDouble(strVal);
			}
			catch(Exception e) {
				value = 0;
			}
		}
		return value;
	}
    
    /**
     * 文字型の属性値を取得する
     * 
     * @param strVal
     * @return
     * @throws TagFunctionException 
     */
    protected String getString(BeanMap record, String name) throws TagFunctionException {
    	if (!record.containsKey(name))
    		throw new TagFunctionException(TagFunctionException.INVALID_ATTR);
    	String strVal = (String)record.get(name);
 		if (strVal != null)
			return strVal;
 		else
 			return "";
    } 
   
	@Override
	public boolean equals(Object node) {
		TagFunctionNode tfnNode = (TagFunctionNode)node;
		if (!getClass().equals(tfnNode.getClass()))
			return false;
		if ((childNotShare() == null) != (tfnNode.childNotShare() == null))
			return false;
		if (childNotShare() != null && !childNotShare().equals(tfnNode.firstChild))
			return false;
		if ((nextNotShare() == null) != (tfnNode.nextNotShare() == null))
			return false;
		if (nextNotShare() != null && !nextNotShare().equals(tfnNode.nextNotShare()))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = getClass().hashCode();
		if (childNotShare() != null)
			hash = ((hash << 13) | (hash >>> 19) ^ childNotShare().hashCode());
		if (nextNotShare() != null)
			hash = ((hash << 13) | (hash >>> 19) ^ nextNotShare().hashCode());
		return hash;
	}

}
