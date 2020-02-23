/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * ベースとなるアクションクラス.
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractAction {

	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/** サイト名称 */
	public String siteName;

	/** システムバージョン */
	public String version;

	/** システムバージョン詳細 */
	public String versionDetail;

    /** ログイン情報 */
    @Resource public LoginDataDto loginDataDto;

    /** HTTP Request */
    @Resource protected HttpServletRequest request;
    /** HTTP Response */
    @Resource protected HttpServletResponse response;
    /** HTTP Session */
    @Resource protected HttpSession session;
    /** Servlet Context */
    @Resource protected ServletContext application;
    /** Logger */
    protected Logger logger = Logger.getLogger(AbstractAction.class);

	@Resource protected LocalgovInfoService localgovInfoService;
	@Resource protected MultilangInfoService multilangInfoService;

	public void setupModel(Map<String,Object> model) {
		model.put("siteName", siteName);
		model.put("version", version);
		model.put("versionDetail", versionDetail);
		model.put("loginDataDto", loginDataDto);
	}

	/**
	 * アクションの初期化
	 */
	public void initAction() {
		// ログイン自治体情報を取得
		LocalgovInfo gov = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		loginDataDto.setLocalgovInfo(gov);

		siteName = lang.__("NIED disaster information sharing system");
		version = Config.getVersionForView();

		// システム名称の設定
		if (gov!=null && StringUtil.isNotEmpty(gov.systemname))
			siteName = gov.systemname;
	}

	/* *
	 * エラーメッセージを Request に追加する
	 * @param message エラーメッセージ
	 * @deprecated
	 * /
	public void addRequestErrorMessage(String message) {
		ActionMessages errors = new ActionMessages();
		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(message, false));
		ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
	}*/

	/**
	 * エラーメッセージを Request に追加する
	 * @param message エラーメッセージ
	 */
	public void addRequestErrorMessage(BindingResult bindingResult, String message) {
		ActionMessages errors = new ActionMessages();
		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(message, false));
		ActionMessagesUtil.addErrors(bindingResult, errors);
	}

	/* *
	 * エラーメッセージを Request に追加する
	 * @param e
	 * @deprecated
	 * /
	protected void addRequestErrorMessage(Exception e) {
		ActionMessages errors = new ActionMessages();
		Throwable t = e;
		while(t!=null) {
			if(t instanceof ServiceException) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			t=t.getCause();
		}
		ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
	}*/

	/**
	 * エラーメッセージを Request に追加する
	 * @param e
	 */
	protected void addRequestErrorMessage(BindingResult bindingResult, Exception e) {
		ActionMessages errors = new ActionMessages();
		Throwable t = e;
		while(t!=null) {
			if(t instanceof ServiceException) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			t=t.getCause();
		}
		ActionMessagesUtil.addErrors(bindingResult, errors);
	}

	/**
	 * メッセージを Request に追加する
	 * @param message エラーメッセージ
	 */
	public void addRequestMessage(String message) {
		ActionMessages errors = new ActionMessages();
		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(message, false));
		ActionMessagesUtil.addMessages(SpringContext.getRequest(), errors);
	}

	protected JSONObject onfailJSONResponse(Exception e) {
		try {
			JSONObject fail = new JSONObject();
			fail.put("success", false);
			fail.put("message", e.getMessage());
			logger.error(e.getMessage(), e);
			return fail;
		} catch (Exception e2) {
			logger.error(e2.getMessage(), e2);
			return null;
		}
	}
}
