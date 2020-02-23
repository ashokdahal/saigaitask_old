/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionToken;

/**
 * DISASTER_START() のクラス
 */
public class TFNDisasterStart extends TFNFunction {
	private String format;

 	protected int argCount() {
		return 1;
	}

   protected boolean processArg(TagFunctionNode node, int n) throws TagFunctionException {
    	switch (n) {
    	case 0:
    		if (!(node instanceof TFNLiteral) || ((TFNLiteral)node).dataType != TagFunctionToken.STRING)
    			throw new TagFunctionException(TagFunctionException.INCOMPATIBLE_DATATYPE);
    		format = ((TFNLiteral)node).value;
    		break;
    	}
    	return true;
    }

    public Map<String, Object> execute(Map<String, Object> arg) throws TagFunctionException {
    	TrackData trackData = tagFunction.tagFunctionService.getTrackData();
    	Map<String, Object> result = new HashMap<String, Object>();
    	if (trackData != null && trackData.starttime != null) {
    		SimpleDateFormat fmt = new SimpleDateFormat(format);
    		result.put("value", fmt.format(trackData.starttime));
    	}
    	else {
    		result.put("value", "");
    	}
    	return result;
    }

}


