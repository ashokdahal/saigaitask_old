/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class MeasureAction extends AbstractPageAction {
	/**
	 * 距離・面積計算用のjspを表示する
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/measure/measure")
	public String measure() {

		return "page/measure/measure";
	}

}
