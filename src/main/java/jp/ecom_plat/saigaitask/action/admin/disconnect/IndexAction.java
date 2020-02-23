/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import org.springframework.web.bind.annotation.RequestMapping;

@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin/disconnect")
@RequestMapping("/admin/disconnect")
public class IndexAction extends AbstractDisconnectAction {
	
	/** インポート対象の自治体ID(システム管理者用) */
	public Long localgovinfoid;

	/**
	 * トップ画面
	 * @return
	 */
	@RequestMapping(value={"","/index"})
	public String index() {

		// 自治体の切り替えパラメータ有り、かつシステム管理者の場合
		if(localgovinfoid!=null && loginDataDto.getGroupid()==0) {
			loginLocalgovInfo(localgovinfoid);
		}

	return "forward:/admin/disconnect/initImportExport/index";
	}
}
