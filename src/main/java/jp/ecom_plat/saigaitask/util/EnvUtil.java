/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import org.seasar.framework.env.Env;

public class EnvUtil{

    /**
     * 現在の環境が本番環境かどうかを返却します。
     * @return true：本番環境  false：テスト環境
     */
    public static boolean isProductEnv(){
    	String cond = Env.getValue();
    	if(cond.equals(Env.PRODUCT)){
    	    // 本番環境
    		return true;
    	}else{
    	    // テスト環境
    		return false;
    	}
    }
}
