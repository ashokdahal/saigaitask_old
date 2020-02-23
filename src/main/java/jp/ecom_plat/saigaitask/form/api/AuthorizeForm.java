package jp.ecom_plat.saigaitask.form.api;

/**
 * OAuth2 authorizeリクエストフォーム
 */
@lombok.Getter @lombok.Setter
public class AuthorizeForm {

	/** Cosumer Key */
	public String consumerKey;
	/** Callback URL of User Authorization Page */
	public String oauthCallback;

}
