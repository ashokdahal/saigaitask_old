/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.util.Map;

import org.apache.struts.action.ActionMessages;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.web.bind.annotation.RequestParam;

import jp.ecom_plat.saigaitask.action.ServiceException;

@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin/setupper")
public class IndexAction extends AbstractSetupperAction {

	/**
	 * セットアッパーのトップ画面
	 * @param localgovinfoid セットアップ対象の自治体ID(システム管理者用)
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper","/admin/setupper/index"})
	public String index(Map<String,Object>model, @RequestParam(value = "localgovinfoid", required = false) Long localgovinfoid) {

		try {
			// 自治体の切り替えパラメータ有り、かつシステム管理者の場合
			if(localgovinfoid!=null && loginDataDto.getGroupid()==0) {
				loginLocalgovInfo(localgovinfoid);
			}

	/*
			// 新規作成の場合は、initSetupへとばす
			if(loginDataDto.getLocalgovinfoid()==0) {
				return "initSetup";
			}

			// セットアッパーの初期化
			initSetupper();

			return "/admin/setupper/index/index";
	*/
		} catch (ServiceException e) {
			// エラーをキャッチしたら、ログ出力、ユーザへのエラーメッセージを追加
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Municipality info can't be deleted."), e);
			ActionMessages errors = new ActionMessages();
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);
		return "forward:/admin/setupper/initSetup";
	}
}
