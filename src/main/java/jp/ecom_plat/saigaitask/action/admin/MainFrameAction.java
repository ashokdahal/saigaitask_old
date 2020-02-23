/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping(value="/admin/mainFrame")
public class MainFrameAction {
	MainFrameAction() {
	}

	protected SaigaiTaskDBLang lang;

	/** システムバージョン */
	public String version;

	public String loginName;
	public String ecomimapUrl;
	public String ecomuser;
	public String ecompass;
	public String localgovName;
	public String systemName;
	@Resource protected HttpServletRequest request;
	@Resource public LoginDataDto loginDataDto;
	@Resource protected LocalgovInfoService localgovInfoService;

	public void setupModel(Map<String,Object>model) {
		model.put("loginDataDto", loginDataDto);
		model.put("version", version);
		model.put("loginName", loginName);
		model.put("ecomimapUrl", ecomimapUrl);
		model.put("ecomuser", ecomuser);
		model.put("ecompass", ecompass);
		model.put("localgovName", localgovName);
		model.put("systemName", systemName);
	}

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ user_header
	 */
	@RequestMapping
	public String index(Map<String,Object>model) {	

		// 以下の条件に全て合致する場合は、
		// システム管理者が自治体セットアッパーまたは
		// 訓練パネルで自治体を選択した状態から管理画面に戻ってきたと判断し、自治体情報を取得し直す。
		// 1) loginDataDto.isAdmin() == true
		// 2) loginDataDto.isUsual() == false
		// 3) loginDataDto.getGroupid() == 0L
		// 4) logindataDto.localgovinfoid != 0L
        // String referer = request.getHeader("Referer");
        if(loginDataDto.isUsual() == false && loginDataDto.getGroupid() == Constants.ADMIN_LOCALGOVINFOID
        		&& loginDataDto.getLocalgovinfoid() != Constants.ADMIN_LOCALGOVINFOID){
        	loginDataDto.setLocalgovInfo(localgovInfoService.findById(Constants.ADMIN_LOCALGOVINFOID));
        	loginDataDto.setLocalgovinfoid(Constants.ADMIN_LOCALGOVINFOID);
        }

		lang = SaigaiTaskLangUtils.getSiteDBLang(request);
		// ecommap のURL 、　ecomuser , ecompass を取得する
		ecomimapUrl = Config.getEcommapURL();
		ecomuser = loginDataDto.getEcomUser();
		// ログインユーザ名
		loginName = loginDataDto.getLoginName();

		//パスワードはデコードできないので修正した。
		ecompass = "";//StringUtil.decrypt(loginDataDto.getGroupInfo().ecompass);
		// 自治体名
		Long localgovinfoid = loginDataDto.getLocalgovinfoid();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者でなければ"都道府県名＋市区町村名"
			localgovName = loginDataDto.getLocalgovInfo().pref+loginDataDto.getLocalgovInfo().city;
		}else{
			//システム管理者の場合は空
			localgovName = "";
		}
		systemName = lang.__("NIED disaster information sharing system");
		if (loginDataDto.getLocalgovInfo() != null && org.seasar.framework.util.StringUtil.isNotEmpty(loginDataDto.getLocalgovInfo().systemname))
			systemName = loginDataDto.getLocalgovInfo().systemname;

		// システムバージョン
		version = Config.getVersionForView();

		setupModel(model);
		return "/admin/mainFrame/index";
	}
}
