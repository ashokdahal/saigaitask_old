/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.tools;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API エクスプローラ
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ExplorerAction {

	/**
	 * @return JSP
	 */
	@RequestMapping("/api/tools/explorer")
	public String index() {
		return "/api/tools/explorer/index";
	}
}
