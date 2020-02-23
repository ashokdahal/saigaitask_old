package jp.ecom_plat.saigaitask.tags;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.tags.Param;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriUtils;

public class Functions extends S2Functions {

	private static final Log logger = LogFactory.getLog(Functions.class);

	/**
	 * Internal enum that classifies URLs by type.
	 */
	private enum UrlType {

		CONTEXT_RELATIVE, RELATIVE, ABSOLUTE
	}

	private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";

	private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";

	/**
	 * Replace template markers in the URL matching available parameters. The
	 * name of matched parameters are added to the used parameters set.
	 * <p>Parameter values are URL encoded.
	 * @param uri the URL with template parameters to replace
	 * @param params parameters used to replace template markers
	 * @param usedParams set of template parameter names that have been replaced
	 * @return the URL with template parameters replaced
	 */
	protected static String replaceUriTemplateParams(String uri, List<Param> params, Set<String> usedParams)
			throws JspException {

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
    			.getRequestAttributes()).getResponse();

		String encoding = response.getCharacterEncoding();
		if(params!=null)
		for (Param param : params) {
			String template = URL_TEMPLATE_DELIMITER_PREFIX + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
			if (uri.contains(template)) {
				usedParams.add(param.getName());
				try {
					uri = uri.replace(template, UriUtils.encodePath(param.getValue(), encoding));
				}
				catch (Exception ex) {
					throw new JspException(ex);
				}
			}
			else {
				template = URL_TEMPLATE_DELIMITER_PREFIX + '/' + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
				if (uri.contains(template)) {
					usedParams.add(param.getName());
					try {
						uri = uri.replace(template, UriUtils.encodePathSegment(param.getValue(), encoding));
					}
					catch (Exception ex) {
						throw new JspException(ex);
					}
				}
			}
		}
		return uri;
	}

	/**
	 * Build the query string from available parameters that have not already
	 * been applied as template params.
	 * <p>The names and values of parameters are URL encoded.
	 * @param params the parameters to build the query string from
	 * @param usedParams set of parameter names that have been applied as
	 * template params
	 * @param includeQueryStringDelimiter true if the query string should start
	 * with a '?' instead of '&'
	 * @return the query string
	 */
	protected static String createQueryString(List<Param> params, Set<String> usedParams, boolean includeQueryStringDelimiter)
			throws JspException {

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
    			.getRequestAttributes()).getResponse();

		String encoding = response.getCharacterEncoding();
		StringBuilder qs = new StringBuilder();
		if(params!=null)
		for (Param param : params) {
			if (!usedParams.contains(param.getName()) && StringUtils.hasLength(param.getName())) {
				if (includeQueryStringDelimiter && qs.length() == 0) {
					qs.append("?");
				}
				else {
					qs.append("&");
				}
				try {
					qs.append(UriUtils.encodeQueryParam(param.getName(), encoding));
					if (param.getValue() != null) {
						qs.append("=");
						qs.append(UriUtils.encodeQueryParam(param.getValue(), encoding));
					}
				}
				catch (Exception ex) {
					throw new JspException(ex);
				}
			}
		}
		return qs.toString();
	}

	/**
	 * Build the URL for the tag from the tag attributes and parameters.
	 * @return the URL value as a String
	 * @throws JspException
	 * @see {@link org.springframework.web.servlet.tags.UrlTag}
	 */
	public static String url(String value) throws JspException {
		UrlType type = null;
		if (value.contains(/*URL_TYPE_ABSOLUTE*/"://")) {
			type = UrlType.ABSOLUTE;
		}
		else if (value.startsWith("/")) {
			type = UrlType.CONTEXT_RELATIVE;
		}
		else {
			type = UrlType.RELATIVE;
		}


		ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if(servletRequestAttributes==null) return value;
    	HttpServletRequest request = servletRequestAttributes.getRequest();
    	HttpServletResponse response = servletRequestAttributes.getResponse();

		StringBuilder url = new StringBuilder();
		if (type == UrlType.CONTEXT_RELATIVE) {
			// add application context to url
			url.append(request.getContextPath());
		}
		if (type != UrlType.RELATIVE && type != UrlType.ABSOLUTE && !value.startsWith("/")) {
			url.append("/");
		}
		url.append(replaceUriTemplateParams(value, /*this.params*/null, /*this.templateParams*/null));
		url.append(createQueryString(/*this.params*/null, /*this.templateParams*/null, (url.indexOf("?") == -1)));

		String urlStr = url.toString();
		if (type != UrlType.ABSOLUTE) {
			// Add the session identifier if needed
			// (Do not embed the session identifier in a remote link!)
			urlStr = response.encodeURL(urlStr);
		}

		// HTML and/or JavaScript escape, if demanded.
		urlStr = HtmlUtils.htmlEscape(urlStr, response.getCharacterEncoding());
		//urlStr = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;

		return urlStr;
	}
    
	private static CompositeUriComponentsContributor getConfiguredUriComponentsContributor() {
		WebApplicationContext wac = getWebApplicationContext();
		if (wac == null)
			return null;
		try {
			return ((CompositeUriComponentsContributor) wac.getBean("mvcUriComponentsContributor",
					CompositeUriComponentsContributor.class));
		} catch (NoSuchBeanDefinitionException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No CompositeUriComponentsContributor bean with name 'mvcUriComponentsContributor'");
			}
		}
		return null;
	}

	private static RequestMappingInfoHandlerMapping getRequestMappingInfoHandlerMapping() {
		WebApplicationContext wac = getWebApplicationContext();
		Assert.notNull(wac, "Cannot lookup handler method mappings without WebApplicationContext");
		try {
			return ((RequestMappingInfoHandlerMapping) wac.getBean(RequestMappingInfoHandlerMapping.class));
		} catch (NoUniqueBeanDefinitionException ex) {
			throw new IllegalStateException("More than one RequestMappingInfoHandlerMapping beans found", ex);
		} catch (NoSuchBeanDefinitionException ex) {
			throw new IllegalStateException("No RequestMappingInfoHandlerMapping bean", ex);
		}
	}

	private static WebApplicationContext getWebApplicationContext() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			logger.debug("No request bound to the current thread: not in a DispatcherServlet request?");
			return null;
		}

		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

		WebApplicationContext wac = (WebApplicationContext) request
				.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		if (wac == null) {
			logger.debug("No WebApplicationContext found: not in a DispatcherServlet request?");
			return null;
		}
		return wac;
	}
}
