package jp.ecom_plat.saigaitask.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import jp.ecom_plat.saigaitask.security.sample.Account;
import jp.ecom_plat.saigaitask.security.sample.MyUserDetails;
import jp.ecom_plat.saigaitask.service.LoginService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * ログアウト成功後に表示するログイン画面で、
 * 利用者画面/管理画面/モバイル画面のどのタイプのログイン画面を表示するかを制御するハンドラ.
 * 
 * /logout を呼び出す際に typeパラメータを付与すること。
 */
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

	Logger logger = Logger.getLogger(LogoutSuccessHandler.class);
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		// ログアウト時間を記録
		try {
			MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
			Account account = myUserDetails.getAccount();
			
			LoginService loginService = SpringContext.getApplicationContext().getBean(LoginService.class);
			loginService.logout(account.getLogindataid());
		} catch(Exception e) {
			logger.error("failed to save logout data", e);
		}
		
		StringBuffer param = new StringBuffer();
		
		// ログアウト時のリクエストパラメータにtypeがあれば、
		// ログイン画面のリダイレクトURLに付与する
		String type = request.getParameter("type");
		if(StringUtil.isNotEmpty(type)) {
			param.append("type="+type);
		}

		response.setStatus(HttpStatus.OK.value());

		String URL = request.getContextPath() + "/login";
		if(0<param.length()) URL += "?"+param.toString();
		response.sendRedirect(URL);		
	}

}
