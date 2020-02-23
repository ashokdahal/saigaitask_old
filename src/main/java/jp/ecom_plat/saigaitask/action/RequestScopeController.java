package jp.ecom_plat.saigaitask.action;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;

/**
 * SAStrutsのActionのように、Controllerをリクエストごとに生成するためのアノテーション
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Controller
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public @interface RequestScopeController {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * @return the suggested component name, if any
	 * @since 4.0.1
	 */
	String value() default "";

}