package jp.ecom_plat.saigaitask.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.util.UriComponentsBuilder;

import jp.ecom_plat.saigaitask.security.TokenProcessor;

/**
 * CSRFに加えて、Struts の TokenProcessorのようなトランザクショントークンを付与する RequestDataValueProcessor
 *
 * @see <a href="http://qiita.com/kazuki43zoo/items/aaf5e1623cac4e961a0b">Spring Boot上でCsrfRequestDataValueProcessorと独自RequestDataValueProcessorを共存させる方法</a>
 *
 * META-INF/spring.factories で下記のように Auto Configure に登録している
 * org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
 * jp.ecom_plat.saigaitask.config.SaigaiTaskRequestDataValueProcessor
 */
@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class SaigaiTaskRequestDataValueProcessor {

    @Bean
    RequestDataValueProcessor requestDataValueProcessor() {
        CsrfRequestDataValueProcessor csrfRequestDataValueProcessor = new CsrfRequestDataValueProcessor();
        return new RequestDataValueProcessor() {
            @Override
            public String processAction(HttpServletRequest request, String action, String httpMethod) {
                return addCachePreventQueryParameter(csrfRequestDataValueProcessor.processAction(request, action, httpMethod));
            }

            @Override
            public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
                return csrfRequestDataValueProcessor.processFormFieldValue(request, name, value, type);
            }

            @Override
            public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
                return addTransactionTokenHiddenField(request, csrfRequestDataValueProcessor.getExtraHiddenFields(request));
            }

            @Override
            public String processUrl(HttpServletRequest request, String url) {
                return addCachePreventQueryParameter(csrfRequestDataValueProcessor.processUrl(request, url));
            }

            private String addCachePreventQueryParameter(String url) {
                return UriComponentsBuilder.fromUriString(url).queryParam("_s", System.currentTimeMillis()).build().encode()
                        .toUriString();
            }

            /**
             * Struts1 の TokenProcessor のようなトランザクショントークンを生成する
             * @param extraHiddenFields
             * @return
             */
            private Map<String, String> addTransactionTokenHiddenField(HttpServletRequest request, Map<String, String> extraHiddenFields) {
            	String name = TokenProcessor.getTokenKey();
            	String value = TokenProcessor.getInstance().getToken(request);
            	if(value!=null) {
                	extraHiddenFields.put(name, value);
            	}
            	return extraHiddenFields;
            }
		};
    }
}
