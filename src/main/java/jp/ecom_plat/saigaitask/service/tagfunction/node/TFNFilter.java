/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.util.Map;


import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * フィルタのクラス
 */
public class TFNFilter extends TFNFunction {
	private long filterid;

 	protected int argCount() {
		return 1;
	}

    protected boolean processArg(TagFunctionNode node, int n) throws TagFunctionException {
    	switch (n) {
    	case 0:
    		if (!(node instanceof TFNLiteral) || ((TFNLiteral)node).dataType != TagFunctionToken.NUMBER)
    			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
    		filterid = (long)((TFNLiteral)node).nval;
    		// フィルタID存在確認
    		tagFunction.tagFunctionService.checkFilter(filterid);
    		break;
    	}
    	return true;
    }

    public void load() {

    	// フィルタＩＤより、対象 gidまたは_orgid の一覧を得る
    	// 対象がgidか_orgidかはフィルタの実行結果から逆引きする
    	String key = "gid";
    	Map<Long, String> gidSet = tagFunction.tagFunctionService.filter(filterid);
    	if(gidSet != null && (! gidSet.isEmpty())){
    		int i = 0;
    		for(String keys : gidSet.values()){
    			if(i > 0){
    				break;
    			}
    			key = keys;
    			i++;
    		}
    	}

    	// 対象 gidまたは_orgid に含まれていないものを削除する
    	for (int i = 0; i < tagFunction.share.valueList.size(); i++) {
    		BeanMap record = tagFunction.share.valueList.get(i);
    		Long gid = (Long)record.get(key);
    		if (! gidSet.containsKey(gid)) {
    			tagFunction.share.valueList.remove(i);
    			i--;
    		}
    	}

    	super.load();
    }

}
