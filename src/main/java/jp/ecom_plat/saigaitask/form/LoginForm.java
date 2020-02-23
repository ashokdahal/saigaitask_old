/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.service.LoginService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.validator.Required;

/**
 * ログインフォーム
 */
@lombok.Getter @lombok.Setterpublic class LoginForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 班情報サービス */
    @Resource
    protected GroupInfoService groupInfoService;
	/** ID */
	public String id = "";
	/** 自治体ID */
	public String localgovinfoid = "";
	/** システム名 */
	public String systemname = "";
	/** ログイン班ID */
	/*@Required*/
	public String groupid = null; //"admin";
	/** ログイン課ID */
	/*@Required*/
	public String unitid = null; // "0";
	/** 災害種別 */
	@Required
	public String disasterid = "0";
	/** 災害時パスワード */
	public String password = "";
	/** 平常時パスワード */
	public String password2 = "";

	/** ログイン後の転送先  */
	public String returnpath = "";

	/*public Long getUnitidLong() {
		if (unitid.length() > 0)
			return Long.parseLong(unitid);
		return 0L;
	}*/

	/** URLクリックで職員参集の安否状況を変更 用*/
	public String userid = null;
	public String safetystateid = null;
	public String trackdataid = null;
	public String key = null;

	/** 地図表示／非表示 */
	public String mapVisible = "1";

	@Required
	/** 言語コード */
	public String langCode = "";

	/** 現在のタブインデックス  */
	public String currenttab = "0";

	/**
	 * ログインフォーム種別
	 * page: 利用者用
	 * mob: モバイル用
	 * admin: 管理画面用
	 */
	public String type;

	/**
	 * ログインモード
	 * MODE_TASK: 災害対応モード（班ログイン）　※デフォルト
	 * MODE_USUAL: 平常時モード（課ログイン）
	 */
	public Integer mode = null;
	
	public LoginForm() {
		
	}
	
	public LoginForm(HttpServletRequest request) {
        // リクエストパラメータをLoginFormにセット
        LoginForm loginForm = this;
        loginForm.id = request.getParameter("id");
        loginForm.localgovinfoid = request.getParameter("localgovinfoid");
        loginForm.systemname = request.getParameter("systemname");
        loginForm.groupid = request.getParameter("groupid");
        loginForm.type = request.getParameter("type");
        {
        	// 利用者画面のログイン画面
        	if("page".equals(loginForm.type)) {
            	// 災害ログインで、班名が未入力の場合は、adminとする
            	if(LoginService.isUsualLoginRequest(request)==false && StringUtil.isEmpty(loginForm.groupid)) {
            		loginForm.groupid = "admin";
            		loginForm.mode = LoginService.MODE_SETUPPER;
            	}
        	}
        }
        loginForm.unitid = request.getParameter("unitid");
        loginForm.disasterid = request.getParameter("disasterid");
        loginForm.password = request.getParameter("password");
        loginForm.password2 = request.getParameter("password2");
        loginForm.returnpath = request.getParameter("returnpath");
        loginForm.userid = request.getParameter("userid");
        loginForm.safetystateid = request.getParameter("safetystateid");
        loginForm.trackdataid = request.getParameter("trackdataid");
        loginForm.key = request.getParameter("key");
        loginForm.mapVisible = request.getParameter("mapVisible");
        loginForm.langCode = request.getParameter("langCode");
        loginForm.currenttab = request.getParameter("currenttab");
        String mode = request.getParameter("mode");
        if(StringUtil.isNotEmpty(mode)) loginForm.mode = Integer.parseInt(mode);
        if(loginForm.mode==null) {
        	if(LoginService.isUsualLoginRequest(request)) loginForm.mode = LoginService.MODE_USUAL;
        	else loginForm.mode = LoginService.MODE_TASK;
        }
	}
}
