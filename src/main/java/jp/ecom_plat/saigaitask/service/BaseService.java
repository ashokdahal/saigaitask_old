/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

/**
 * サービスクラスの抽象クラスです.
 * よく使うプロパティを持っています.
 */
@org.springframework.stereotype.Service
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
abstract public class BaseService {

	// ServletAPI (暗黙コンポーネント)
	@Resource protected HttpServletRequest request;
	@Resource protected HttpServletResponse response;
	@Resource protected HttpSession session;
	@Resource protected ServletContext application;

	// 災害対応システム
	/** ログイン情報 */
	//@Resource protected LoginDataDto loginDataDto;

	@Resource protected LocalgovInfoService localgovInfoService;
	
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/** ログイン中の自治体情報 */
	//private LocalgovInfo loginLocalgovInfo;

	/**
	 * ログイン中の自治体情報を取得します.
	 * @return localgovInfo
	 */
	/*public LocalgovInfo getLoginLocalgovInfo() {
		if(loginLocalgovInfo==null) {
			long localgovinfoid = loginDataDto.getLocalgovinfoid();
			loginLocalgovInfo = localgovInfoService.findById(localgovinfoid);
		}
		return loginLocalgovInfo;
	}*/

	/**
	 * @param loginLocalgovInfo セットする localgovInfo
	 */
	/*public void setLoginLocalgovInfo(LocalgovInfo loginLocalgovInfo) {
		this.loginLocalgovInfo = loginLocalgovInfo;
	}*/
}
