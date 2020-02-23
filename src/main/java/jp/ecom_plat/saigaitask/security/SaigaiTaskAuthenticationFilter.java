package jp.ecom_plat.saigaitask.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.service.LoginService;

@Component
public class SaigaiTaskAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: "
                    + request.getMethod());
        }

        LoginForm loginForm = new LoginForm(request);

        // ログインユーザ名を取得
        String loginFormString = obtainUsername(loginForm);
        // パスワードを取得
        String password = obtainPassword(request, loginForm);

        // パラメータから認証トークンを生成
        AuthenticationToken authRequest =
        		new AuthenticationToken(loginFormString, password);
        authRequest.loginForm = loginForm;

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * ユーザ名文字列を生成する。
     * UserDetailsService.loadUserByUsername にはユーザ名しか渡らないが、
     * localgovinfoid と {groupid, unitid} のパラメータが必要なので、
     * LoginForm のシリアルを渡すようにする。
     */
    protected String obtainUsername(LoginForm loginForm) {
        try {
			String loginFormString = jp.ecom_plat.saigaitask.util.StringUtil.serialize(loginForm);
			return loginFormString;
		} catch (IOException e) {
			throw new ServiceException("ERROR: cannot serialize loginForm", e);
		}
    }
    
    /**
     * 災害時と平常時の場合で取得するパスワードを切り替える
     * @param request
     * @param loginForm
     * @return
     */
    protected String obtainPassword(HttpServletRequest request, LoginForm loginForm) {
    	if(LoginService.isUsualLoginRequest(request)) {
    		return loginForm.password2;
    	}
    	return loginForm.password;
    }
    
    /**
     * AuthenticationManagerをDIする
     * authenticationManager変数は親クラスが持っているので、
     * 関数の引数にDIして、それをセットする。
     */
    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}