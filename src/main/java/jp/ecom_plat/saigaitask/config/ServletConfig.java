package jp.ecom_plat.saigaitask.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.ecom_plat.map.servlet.PdfServlet;

/**
 * Servlet登録コンフィグ
 */
@Configuration
public class ServletConfig {

	/**
	 * @return 地図PDF出力サーブレット
	 */
	@Bean
	public ServletRegistrationBean pdfServletRegistrationBean(){
	    return new ServletRegistrationBean(new PdfServlet(),"/PdfServlet/*");
	}
}
