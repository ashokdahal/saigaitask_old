package jp.ecom_plat.saigaitask.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import jp.ecom_plat.saigaitask.form.LoginForm;

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

	public LoginForm loginForm;

	public AuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	public AuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
}
