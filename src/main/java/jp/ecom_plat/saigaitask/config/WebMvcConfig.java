package jp.ecom_plat.saigaitask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.ecom_plat.saigaitask.interceptor.RememberLocationInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RememberLocationInterceptor()).addPathPatterns("/**");
	}
}
