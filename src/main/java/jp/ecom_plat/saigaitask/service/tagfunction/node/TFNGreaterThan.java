/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

/**
 * > のクラス
 */
public class TFNGreaterThan extends TFNNumberComparison {

	@Override
	protected boolean compare(double val1, double val2) {
		return val1 > val2;
	}

}
