/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.Map;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * VAL(...) のクラス
 */
public class TFNVal extends TFNFunction {

 	protected int argCount() {
		return 2;
	}

    protected boolean processArg(TagFunctionNode node, int n) throws TagFunctionException {
    	switch (n) {
    	case 0:
    		if (!(node instanceof TFNLiteral) || ((TFNLiteral)node).dataType != TagFunctionToken.STRING)
    			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
    		tagFunction.tagFunctionService.checkLayer(((TFNLiteral)node).value);
    		addChild(node);
    		break;
    	case 1:
    		if (!(node instanceof TFNIdentifier))
    			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
    		tagFunction.tagFunctionService.checkAttr(((TFNLiteral)firstChild).value, ((TFNIdentifier)node).name);
     		node.tagFunction.attr = ((TFNIdentifier)node).name;
    		break;
    	}
    	return true;
    }

    public void load() {

    	// レイヤＩＤ
    	String baseLayerId = ((TFNLiteral)firstChild).value;
    	String layerId = tagFunction.tagFunctionService.getLayerId(baseLayerId);

    	// 属性の値を取得する
    	tagFunction.share.valueList = tagFunction.tagFunctionService.selectAll(layerId);

    	// 属性の型情報を取得する
    	tagFunction.share.attrInfo = tagFunction.tagFunctionService.getAttrInfo(layerId);
    	super.load();
    }

    public Map<String, Object> execute(Map<String, Object> arg) {
    	arg.put("valueList", tagFunction.share.valueList);
    	return arg;
    }

}
