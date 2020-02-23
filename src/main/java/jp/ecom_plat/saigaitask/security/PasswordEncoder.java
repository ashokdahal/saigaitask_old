package jp.ecom_plat.saigaitask.security;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.ServiceException;

/**
 * Spring Security のパスワードエンコーダ実装クラス
 * @author oku
 *
 */
@Component
public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			return UserAuthorization.getEncryptedPass(rawPassword.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encode(rawPassword).equals(encodedPassword);
	}

}
