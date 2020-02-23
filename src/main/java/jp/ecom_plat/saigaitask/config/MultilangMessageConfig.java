package jp.ecom_plat.saigaitask.config;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.ecom_plat.saigaitask.multilang.DatabaseMessageSource;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 多言語化に関する設定
 */
@Configuration
public class MultilangMessageConfig implements WebMvcConfigurer {

    /**
     * lang という名前で SaigaiTaskDBLang をBean登録
     * @return
     */
    @Bean 
    public SaigaiTaskDBLang lang() {
    	return new SaigaiTaskDBLang();
    }

    
    //
    // MessageSource を message.properties から データベースに変更する
    //
    
    @Resource ReloadableResourceBundleMessageSource propertiesMessageSource;
    @Resource SaigaiTaskDBLang lang;

    /**
     * 標準機能の言語メッセージファイルのメッセージソースを propertiesMessageSource で定義
     */
    @Bean
    public ReloadableResourceBundleMessageSource propertiesMessageSource() {
    	ReloadableResourceBundleMessageSource propertiesMessageSource = new ReloadableResourceBundleMessageSource();
    	propertiesMessageSource.addBasenames("messages");
    	propertiesMessageSource.setCacheSeconds(-1);
    	propertiesMessageSource.setDefaultEncoding("UTF-8");
    	return propertiesMessageSource;
    }

    /**
     * MessageSource を message.properties から データベースに変更する.
     * DBになければ propertiesMessageSource を使って言語メッセージファイルを参照させる.
     */
    @Bean
    public DatabaseMessageSource messageSource() {
    	DatabaseMessageSource messageSource = new DatabaseMessageSource();
    	messageSource.setParentMessageSource(propertiesMessageSource);
    	return messageSource;
    }
    


    //@Resource
    //private MessageSource messageSource;

    /**
     * LocalValidatorFactoryBeanのsetValidationMessageSourceで
     * バリデーションメッセージをValidationMessages.propertiesからSpringの
     * MessageSource(messages.properties)に上書きする
     * 
     * http://qiita.com/NagaokaKenichi/items/65d0e07151292968d67f
     * http://qiita.com/yhinoz/items/5dd8b58fa555dfb0ca82
     * 
     * @return localValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
    	MessageSource messageSource = SpringContext.getApplicationContext().getBean(DatabaseMessageSource.class);
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }

    @Override
    public org.springframework.validation.Validator getValidator() {
        return validator();
    }
    
    
}
