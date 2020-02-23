/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


/**
 * BASIC認証クラス
 */
public class BasicAuthenticator extends Authenticator {
	/**
	 * ユーザID
	 */
	private String username;
	/**
	 * パスワード
	 */
	private String password;
	/**
	 * コンストラクタ
	 * 
	 * @param username ユーザID
	 * @param password パスワード
	 */
	public BasicAuthenticator(String username, String password){
		this.username = username;
		this.password = password;
	}
	/**
	 * getPasswordAuthentication
	 * @return PasswordAuthentication
	 */
	protected PasswordAuthentication getPasswordAuthentication(){
		return new
		PasswordAuthentication(username, password.toCharArray());
	}
	/**
	 * myGetRequestingPrompt
	 * @return String
	 */
	public String myGetRequestingPrompt(){
		return super.getRequestingPrompt();
	}
}
