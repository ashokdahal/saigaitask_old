package jp.ecom_plat.saigaitask.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.service.LoginService;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Resource LoginDataDto loginDataDto;

	private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		// 強制的にログイン後に表示する画面があればリダイレクト
		{
			String targetUrl = null;
			
			LoginForm loginForm = new LoginForm(request);

			//初期インストール 自治体もグループもなし
			if (loginForm.mode==LoginService.MODE_SETUPPER) {
				targetUrl = "/admin/setupper/initSetup/";
			}

			if(targetUrl!=null) {
				requestCache.removeRequest(request, response);
				redirectStrategy.sendRedirect(request, response, targetUrl);
				return;
			}
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

	/**
	 * ログイン前にアクセスしようとしたリクエストがない場合、
	 * どのログイン画面を利用したかで、ログイン後に表示するURLを切り替える
	 */
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		
		
		String type = request.getParameter("type");
    	if("page".equals(type)) {
    		return "/page";
    	}
    	else if("admin".equals(type)) {
    		return "/admin/mainFrame/";
    	}
    	else if("mob".equals(type)) {
    		return "/mob/process";
    	}
		return super.determineTargetUrl(request, response);
	}
}
