package jp.ecom_plat.saigaitask.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jp.ecom_plat.saigaitask.security.LoginSuccessHandler;
import jp.ecom_plat.saigaitask.security.LogoutSuccessHandler;
import jp.ecom_plat.saigaitask.security.SaigaiTaskAuthenticationFilter;

/**
 * Spring Security によるログイン認証の設定
 * @author oku
 *
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	//@Resource AuthenticationProviderImpl authenticationProvider;

	@Resource SaigaiTaskAuthenticationFilter saigaiTaskAuthenticationFilter;
	@Resource LoginSuccessHandler loginSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// ログイン成功時のハンドラ
		// デフォルトは SavedRequestAwareAuthenticationSuccessHandler
		saigaiTaskAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);

		// ログイン失敗時のハンドラを設定
		SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler("/login-error");
		authenticationFailureHandler.setUseForward(true);
		saigaiTaskAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

		http

		// HTTPリクエストのアクセス権限の設定
		.authorizeRequests()
			// 認証不要でアクセス許可する URLパターン
			// ? → 1文字にマッチ
			// * (ワイルドカード) → 0かそれ以上の文字数にマッチ
			// ** (ダブルワイルドカード) → 0かそれ以上のディレクトリにマッチ
			.antMatchers(
					// 静的リソース
					"/css/**", "/js/**", "/images/**", "/admin-js/**"
					// ログイン画面で使用する AJAX URL
					,"/triggerinfo/*", "/blank/*"
					// ログイン画面
					// /login?type=admin などを許可する
					,"/login*"
					// Legend処理はPDF出力があるため公開(認証不要)にする
					,"/page/map/legend*"
					// MGRSのWMS処理はPDF出力があるため公開(認証不要)にする
					,"/page/map/mgrs/*"
					// OAuth, API
					// /oauth/authorize はログイン必須とする
					,"/oauth2/token/", "/api/**"
					// テスト用ページ
					//,"/message/**"
					//,"/page/**"
					//,"/template/**"
					//,"/login/**"
					).permitAll()
			// 上記以外は認証を必須とする
			.anyRequest().fullyAuthenticated()

		.and()

		// CSRFの除外パターン
		.csrf().ignoringAntMatchers(
				// POST /oauth2/token
				"/oauth2/token/**",
				// POST /api/**
				"/api/**"
				)

		.and()

		// ログイン処理の設定
		.formLogin()
			.loginPage("/login")
			// ログイン成功/失敗時のハンドラは SaigaiTaskAuthenticationFilterで設定する
			.permitAll()

		// 認証フィルタを追加
		.and()
		.addFilterBefore(saigaiTaskAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

		// ログアウト処理の設定
		.logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessHandler(new LogoutSuccessHandler())
		.permitAll()

		.and()
		
		// セキュリティヘッダの付与
		// Cache-Control:no-cache, no-store, max-age=0, must-revalidate
		// →あるユーザーがログインして閲覧できるコンテンツがキャッシュされ、ログアウト後に別ユーザーも閲覧できてしまう場合があるので、
		// →コンテンツをキャッシュしないように指示をして、ブラウザがサーバの情報を常に取得するようにする。
		// X-Content-Type-Options:nosniff
		// →ブラウザはMIMEタイプが間違っていても中身で判断する機能があるが無効にする
		// →MIMEタイプが一致しない場合にスクリプトが実行されないようにする
		// Strict-Transport-Security
		// →一度、httpsでアクセスしたら、その後はhttpsで接続するように強制する
		// →万が一、httpでアクセスすることがあった場合に、http由来の攻撃を受ける可能性があるため。
		// X-Frame-Options:DENY
		// →DENY フレーム内にページを表示することを禁止
		// →クリックジャッキング対策
		// X-XSS-Protection:1; mode=block
		// →ブラウザの XSSフィルターの機能を有効
		.headers()
		//X-Frame-Options:DENY->SameOrigin thickboxなど
		.frameOptions().sameOrigin()
		.and()

		// セッション管理設定
		.sessionManagement()
		// セッション固定攻撃対策：ログイン成功時にセッションIDを変更する
		.sessionFixation().changeSessionId()
		// セッション固定攻撃対策：対策なし
		//.sessionFixation().none()

		.and()

		// ベーシック認証を無効化
		.httpBasic().disable();
	}

	/*
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");

		//auth
	    //.authenticationProvider(authenticationProvider);
        //.authenticationEventPublisher(eventPublisher);
	}
	*/

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	    StrictHttpFirewall firewall = new StrictHttpFirewall();
	    //firewall.setAllowUrlEncodedSlash(true);
	    firewall.setAllowSemicolon(true);
	    return firewall;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}
}
