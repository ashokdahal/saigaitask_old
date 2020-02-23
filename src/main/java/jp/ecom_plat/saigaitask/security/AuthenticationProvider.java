package jp.ecom_plat.saigaitask.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.security.sample.MyUserDetails;
import jp.ecom_plat.saigaitask.service.LoginService;


/**
 * Spring Security によるログイン認証の実装クラス
 * @author oku
 *
 */
@Component
public class AuthenticationProvider extends DaoAuthenticationProvider { //implements org.springframework.security.authentication.AuthenticationProvider {

	@Resource LoginService loginService;
	@Resource LoginDataDto loginDataDto;
	
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);

    	AuthenticationToken authRequest = (AuthenticationToken) authentication;
    	LoginForm loginForm = authRequest.loginForm;

    	MyUserDetails myUserDetails = (MyUserDetails) userDetails;
    	Object info = myUserDetails.getAccount().getGroupInfo();
    	if(loginForm.mode==LoginService.MODE_USUAL) {
        	info = myUserDetails.getAccount().getUnitInfo();
    	}
    	loginService.login(loginForm.mode, loginForm, info);
    	
    	// ログアウト成功時に login_data のログアウト時間を記録するために、
    	// myUserDetailsにも logindataid を保持しておく
    	myUserDetails.getAccount().setLogindataid(loginDataDto.getLogindataid());
    	
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
    }
}

