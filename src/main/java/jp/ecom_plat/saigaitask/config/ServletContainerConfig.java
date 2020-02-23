package jp.ecom_plat.saigaitask.config;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * web.xml に相当する サーブレットコンテナの設定
 * @author oku
 *
 */
@Configuration
public class ServletContainerConfig {

	// eコミマップ2.5ではJNDIが不要になったのでコメントアウト
	/*
	static String namespace_ecommap_mapdb;
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("ResourceInfo", Locale.getDefault());
		namespace_ecommap_mapdb = bundle.getString("MAPDB");
	}

	/* *
	 * 組み込みTomcatでJNDIリソースを使えるようにする
	 * @see http://vinayshivaswamy.blogspot.jp/2014/12/jndi-lookup-in-spring-boot.html
	 *
	 * eコミマップは下記のようなJNDIデータソースの呼び出しをしているのでその対応。
	 * <pre>
	 * InitialContext ic = new InitialContext();
	 * DataSource dataSource2 = (DataSource)ic.lookup("java:comp/env/jdbc/bosai");
	 * </pre>
	 *
	 * tomcat-dbcp.jar がないと下記のエラーが発生する
	 * javax.naming.NamingException: Could not create resource factory instance [Root exception is java.lang.ClassNotFoundException: org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory]
	 *
	 * @return
	 * /
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory(DataSourceProperties dataSourceProperties) {
	        return new TomcatEmbeddedServletContainerFactory() {

	            @Override
	            protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
	            	// JNDIネーミングリソースはデフォルト無効になっているので有効化
	            	tomcat.enableNaming();

					return super.getTomcatEmbeddedServletContainer(tomcat);
	            }

	            @Override
	            protected void postProcessContext(Context context) {

	            	// eコミマップ用の jdbc/bosai リソースを登録
	            	// 接続情報は application.properties のものを流用する
	        		ContextResource resource = new ContextResource();
	                resource.setName(namespace_ecommap_mapdb);
	                resource.setType(DataSource.class.getName());
	                resource.setProperty("driverClassName", org.postgresql.Driver.class.getName());
	                resource.setProperty("url", dataSourceProperties.getUrl());
	                resource.setProperty("password", dataSourceProperties.getPassword());
	                resource.setProperty("username", dataSourceProperties.getUsername());

	                context.getNamingResources().addResource(resource);
	            }
	        };
	}*/

	/**
	 * ServletContainerCustomizer
	 *
	 * @return
	 */
	@Bean
	public ConfigurableServletWebServerFactory containerCustomizer() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
			factory.addContextCustomizers(
				new TomcatContextCustomizer() {
					@Override
					public void customize(Context context) {
						//context.addWelcomeFile("index.jsp");

						/*
								// ハンドリングされない例外が発生した場合の処理
								final ErrorPage throwableErrorPage = new ErrorPage();
								throwableErrorPage.setExceptionType("java.lang.Throwable");
								throwableErrorPage.setLocation("/WEB-INF/view/error.jsp");
								context.addErrorPage(throwableErrorPage);

								final ErrorPage errorPage403 = new ErrorPage();
								errorPage403.setErrorCode(403);
								errorPage403.setLocation("/WEB-INF/view/error.jsp");
								context.addErrorPage(errorPage403);

								final ErrorPage errorPage404 = new ErrorPage();
								errorPage404.setErrorCode(404);
								errorPage404.setLocation("/WEB-INF/view/error.jsp");
								context.addErrorPage(errorPage404);

								final ErrorPage errorPage500 = new ErrorPage();
								errorPage500.setErrorCode(500);
								errorPage500.setLocation("/WEB-INF/view/error.jsp");
								context.addErrorPage(errorPage500);
						 */

						// jsp-config
						final Collection<JspPropertyGroupDescriptor> jspPropertyGroups = new ArrayList<>();
						final Collection<TaglibDescriptor> taglibs = new ArrayList<>();

						final JspPropertyGroup group = new JspPropertyGroup();
						group.addUrlPattern("*.jsp");
						group.setPageEncoding("UTF-8");
						group.addIncludePrelude("/WEB-INF/view/common/common.jsp");

						final JspPropertyGroupDescriptor descriptor = new JspPropertyGroupDescriptorImpl(group);
						jspPropertyGroups.add(descriptor);

						final JspConfigDescriptor jspConfigDescriptor = new JspConfigDescriptorImpl(jspPropertyGroups, taglibs);
						context.setJspConfigDescriptor(jspConfigDescriptor);

						//System.out.println("reloadable: "+context.getReloadable());
						//context.setReloadable(true);
						System.out.println("reloadable: "+context.getReloadable());
						System.out.println("docBase: "+context.getDocBase());

						Container jsp = context.findChild("jsp");
						if (jsp instanceof Wrapper) {
							// JSPを変更したらすぐ反映されるようにdevelopmentモードにする
							// development - Is Jasper used in development mode?
							// If true, the frequency at which JSPs are checked for modification may be specified
							// via the modificationTestInterval parameter.
							// true or false, default true
							((Wrapper)jsp).addInitParameter("development", "true");
						}
					}
				}
				);
		return factory;
	}

}
