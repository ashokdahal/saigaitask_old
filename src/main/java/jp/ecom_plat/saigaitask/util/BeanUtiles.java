/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

public final class BeanUtiles  {

    /**
     * マップの値をJavaBeansにコピーします。
     *
     * @param src
     *            ソース
     * @param dest
     *            あて先
     */
    public static void copyProperties(Map<String,Object> src, Object dest) throws ParseException{
        if (src == null || dest == null) {
            return;
        }
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(dest.getClass());
        for (Iterator<String> i = src.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            if (!beanDesc.hasPropertyDesc(key)) {
                continue;
            }
            PropertyDesc pd = beanDesc.getPropertyDesc(key);
            if (pd.isWritable()) {
            	if(pd.getPropertyType().getSimpleName().equals(java.sql.Timestamp.class.getSimpleName())){
            		try{
            			Timestamp val = null;
            			if(src.get(key) != null){
                    		val = new Timestamp(DateUtils.parseDate(src.get(key).toString(), new String[] {"yyyy-MM-dd HH:mm:ss.SSS"}).getTime());
            			}
                        pd.setValue(dest, val);
            		}catch(ParseException e){
            			throw e;
            		}
            	}else{
                    pd.setValue(dest, src.get(key));
            	}
            }
        }
    }
}
