/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

/**
 * <>, != のクラス
 */
public class TFNNotEqualTo extends TFNNumberStringComparison {

	@Override
	protected boolean compareNum(double attr, double literal) {
		return attr != literal;
	}

	@Override
	protected boolean compareStr(String attr, String literal) {
		return !literal.equals(attr);
	}

}
