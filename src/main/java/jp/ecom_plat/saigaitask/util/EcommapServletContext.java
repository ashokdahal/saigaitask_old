/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.commons.lang.StringUtils;

/**
 * eコミマップ用サーブレットコンテキスト
 * servlet-api 3.1 対応
 */
public class EcommapServletContext implements ServletContext {

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public ServletContext getContext(String arg0) {
		return null;
	}

	@Override
	public String getInitParameter(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {
		return null;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		File mapDir = Config.getMapDir();
		String realPath = mapDir.getAbsolutePath();
		if(StringUtils.isNotEmpty(arg0)) {
			if(arg0.startsWith("/")) return realPath + arg0;
			else return realPath + "/" + arg0;
		}
		return realPath;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		return null;
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		return null;
	}

	@Override
	public String getServerInfo() {
		return null;
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		return null;
	}

	@Override
	public String getServletContextName() {
		return null;
	}

	@Override
	public Enumeration<String> getServletNames() {
		return null;
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		return null;
	}

	@Override
	public void log(String arg0) {

	}

	@Override
	public void log(Exception arg0, String arg1) {

	}

	@Override
	public void log(String arg0, Throwable arg1) {

	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public Dynamic addFilter(String arg0, String arg1)
			throws IllegalArgumentException, IllegalStateException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1)
			throws IllegalArgumentException, IllegalStateException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1)
			throws IllegalArgumentException, IllegalStateException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void addListener(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public <T extends EventListener> void addListener(T arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) throws IllegalArgumentException, IllegalStateException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) throws IllegalArgumentException,
			IllegalStateException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) throws IllegalArgumentException,
			IllegalStateException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void declareRoles(String... arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public ClassLoader getClassLoader() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int getEffectiveMajorVersion() throws UnsupportedOperationException {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public int getEffectiveMinorVersion() throws UnsupportedOperationException {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public String getVirtualServerName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
