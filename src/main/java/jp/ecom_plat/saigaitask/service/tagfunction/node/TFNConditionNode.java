/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction.node;

import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;

import org.seasar.framework.beans.util.BeanMap;

/**
 * 条件式のクラス
 */
public abstract class TFNConditionNode extends TagFunctionNode {

	protected abstract boolean evalCondition(BeanMap record) throws TagFunctionException;

}
