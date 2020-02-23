/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page.map;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.seasar.framework.util.Base64Util;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.form.page.map.WmsAuthForm;
import jp.ecom_plat.saigaitask.service.db.AuthorizationInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternalmapdataInfoService;

/**
 * An action class equivalent to e-comi's WMSAuth.
 * This action is for external sites with authentication
 *
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ExternalWmsAuthAction extends AbstractAction {

	protected WmsAuthForm wmsAuthForm;

	//
	@Resource protected ExternalmapdataInfoService externalmapdataInfoService;
	// Authentication information service
	@Resource protected AuthorizationInfoService authorizationInfoService;

	protected Logger getLogger() {
		return Logger.getLogger(ExternalWmsAuthAction.class);
	}

	/**
	 * Execution method of WMSAuth action
	 * TODO: Authentication of layer access authority
	 * @return null(Not forward)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/map/externalWmsAuth","/page/map/externalWmsAuth/index"})
	public ResponseEntity<byte[]> index(Map<String,Object>model,
			@ModelAttribute WmsAuthForm wmsAuthForm, BindingResult bindingResult) {

		Logger logger = getLogger();

		// ?url=以降にリクエスト情報が入っている
		String req_url = request.getParameter("url");
		if(req_url == null){
			return null;
		}
		// Metadata ID
		String metadataId = request.getParameter("metadataid");
		// External map information ID
		String externalmapdatainfoid = request.getParameter("externalmapdatainfoid");
		// It is impossible that JS side checks parameters.
		if(externalmapdatainfoid == null || metadataId == null) return null;

		// Stores parameters other than specific parameters
		StringBuilder exProperty = new StringBuilder();
		Enumeration<String> reqNames = request.getParameterNames();
		while (reqNames.hasMoreElements()){
			String name = (String)reqNames.nextElement();
			// Encoded URL and metadataid, wmsproxy do nothing
			if(name.equals("url")) continue;
			if(name.equals("metadataid")) continue;
			if(name.equals("wmsproxy")) continue;
			if(name.equals("externalmapdatainfoid")) continue;
			// Does not support array
			String val = request.getParameter(name);
			exProperty.append("&");
			exProperty.append(name);
			exProperty.append("=");
			exProperty.append(val);
		}

		// Proxy
		URL url = null;
		try {
			//Get authentication information associated with metadata
			String authorizationHeader = "";
			if(session.getAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + metadataId) != null){
				authorizationHeader = (String)session.getAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + metadataId);
			}else{
				// Register authentication session
				ExternalmapdataInfo externalmapdataInfo = externalmapdataInfoService.findMapAuthById(Long.valueOf(externalmapdatainfoid));
				if(externalmapdataInfo.authorizationInfo != null){
					String authData = externalmapdataInfo.authorizationInfo.username + ":" + externalmapdataInfo.authorizationInfo.userpass;
					session.setAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + externalmapdataInfo.metadataid, "Basic " + Base64Util.encode(authData.getBytes()));
				}else{
					session.setAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + externalmapdataInfo.metadataid, "");
				}
			}
			// In the case of PDF, since the URL of Proxy is created before the parameter for GetMap is given, I will add the parameter for GetMap.
			// Forwarding destination
			url = new URL(req_url);
			// Dismantle the URL once
			String reqURL = url.toString() + exProperty;
			url = new URL(reqURL);
			// http connection
			return proxy(url, authorizationHeader);

		} catch (NumberFormatException e) {
			logger.warn("WMS proxy Warn.", e);
		} catch (MalformedURLException e) {
			// TODO Automatically generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Connect to the specified URL and return it to HTTP Response as it is.
	 * @param url
	 * @return success failure
	 */
	public ResponseEntity<byte[]> proxy(URL url, String authorizationHeader) {

		Logger logger = getLogger();
		logger.debug("ExternalWMSAuth: "+url.toString());
		//long starttime = System.currentTimeMillis();

		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		HttpURLConnection httpConnection = null;
		try {
			//<div lang="ja">コネクション取得</div>
			//<div lang="en">Get connection</div>
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setDoOutput(false);
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);
			int timeout = 5 * 1000;
			httpConnection.setConnectTimeout(timeout);
			httpConnection.setReadTimeout(timeout);
			if(!authorizationHeader.equals("")){
				httpConnection.setRequestProperty("Authorization", authorizationHeader);
			}

			// Content-Type, Set header such as Content-Disposition
			for(Map.Entry<String, List<String>> entry : httpConnection.getHeaderFields().entrySet()) {
				String headerName = entry.getKey();
				if("Transfer-Encoding".equals(headerName)) continue; // Excluded because double addition
				response.setHeader(headerName, httpConnection.getHeaderField(headerName));
			}

			//<div lang="ja">そのまま返却</div>
			//<div lang="en">Return without changing</div>
			//out = new BufferedOutputStream(response.getOutputStream(), 65536);
			out = new ByteArrayOutputStream();
			in = new BufferedInputStream(httpConnection.getInputStream(), 65536);
			byte[] b = new byte[4096];
			int length;
			while ((length = in.read(b)) != -1) {
				out.write(b, 0, length);
			}
			in.close();
			//out.flush();
			//out.close();
			
	    	final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(httpConnection.getContentType()));
			return new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);	
		} catch(FileNotFoundException e) {
			// 404 の場合に FileNotFoundException が発生している.
			String msg = "";
			if(httpConnection!=null) {
				try {
					msg += "http " + httpConnection.getResponseCode() + " " +httpConnection.getResponseMessage();
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			logger.error(msg, e);
			return null;
		} catch(Exception e) {
			logger.error("WMSAuth.proxy "+url.toString(), e);
			return null;

		} finally {
			if(out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
}
