package jp.ecom_plat.saigaitask.multilang;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;

/**
 * DBから言語メッセージを取得する
 * <a href="https://blog.mornati.net/spring-mvc-database-messagesource-fall-back-to-properties-file/">Spring MVC: Database MessageSource fall back to properties file</a>
 */
public class DatabaseMessageSource extends AbstractMessageSource implements ResourceLoaderAware {

	Logger logger = Logger.getLogger(DatabaseMessageSource.class);

	private ResourceLoader resourceLoader;
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String msg = getText(code, locale);
		if(msg==null) return null;
		MessageFormat result = createMessageFormat(msg, locale);
		return result;
	}

    private String getText(String code, Locale locale) {

    	//Map<String, String> localized = properties.get(code);
		// HTTPリクエストの場合はセッションの言語を利用する
		SaigaiTaskDBLang lang = SaigaiTaskLangUtils.getSessionLang();
    	
		String textForCurrentLanguage = null;

		// 未ログイン時にログインが必要なページにアクセスした際に発生するメッセージ
		// 無いと下記のエラーメッセージがログに出力されるため、メッセージを固定で返すようにする
		// Cannot find message with code: AbstractAccessDecisionManager.accessDenied
		if(code.equals("AbstractAccessDecisionManager.accessDenied")) {
			return "Access is denied";
		}
		else if(code.equals("AbstractUserDetailsAuthenticationProvider.onlySupports")) {
			return "Only UsernamePasswordAuthenticationToken is supported";
		}
		
		// DBの言語メッセージを取得
        if (lang != null) {
        	// ActionForm の @Required 対応
        	// 例えば、initSetupForm.domain の @Required であれば、下記のパターンで呼び出される
        	//   initSetupForm.domain
        	//   Required.initSetupForm.domain
        	//   Required.domain
        	//   Required.java.lang.String
        	//   Required
        	//
        	// "Required." から始まれば "{0} is required." で検索をかける
        	// 例）code="Required.initSetupForm.domain"
        	if(code.startsWith("Required.")) {
        		// 例） fieldName=lang.__("initSetupForm.domain")
        		String fieldNameKey = code.substring("Required.".length());
        		if(lang.mapping.containsKey(fieldNameKey)) {
        			String fieldName = lang.__(fieldNameKey);
            		textForCurrentLanguage = lang.__("{0} is required.", fieldName);
        		}
        	}
        	
        	// ActionForm のバリデータはいろんなパターンの code で呼ばれるので、
        	// ある場合のみメッセージを利用するようにする ※ない場合は　code がそのまま返却されるのでチェック
        	if(lang.mapping.containsKey(code)) {
                textForCurrentLanguage = lang.__(code);
        	}
        }

        // DBから取得できない場合はファイルから検索
        // 言語リソースファイルは利用しない予定だが、念のため Fallback 機能を残しておく
        if (textForCurrentLanguage == null) {
            //logger.debug("Fallback to properties message");
            try {
                textForCurrentLanguage = getParentMessageSource().getMessage(code, null, locale);
            } catch (Exception e) {
                logger.error("Cannot find message with code: " + code);
            }
        }

        return textForCurrentLanguage;
    }
}
