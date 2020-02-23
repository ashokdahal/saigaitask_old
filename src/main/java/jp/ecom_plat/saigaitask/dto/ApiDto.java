/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.BufferedReader;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.seasar.framework.util.StringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction.Response;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.OauthtokenData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.OauthtokenDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * APIアクションのためのDTO.
 * APIはトークンによる認証処理をするため、
 * セッションではなくリクエストスコープでデータを保持する.
 */
@lombok.Getter @lombok.Setter
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)public class ApiDto {
	
	Logger logger = Logger.getLogger(ApiDto.class);

	static boolean allowOAuth1 = true;
	static boolean allowOAuth2 = true;

	/** 初期化処理フラグ */
	private Boolean init = false;

	/** 認証フラグ */
	private Boolean authorized = false;

	/** OAuthトークンデータ */
	private OauthtokenData oauthtokenData;

	/** 班情報 */
	private GroupInfo groupInfo;

	/** 課情報 */
	private UnitInfo unitInfo;

	/** リクエストデータ(POST/PUT/PATCH) */
	private String requestData;

	/** リクエストメソッド */
	private String method;
	
	@Resource HttpServletRequest request;

	
	/**
	 * @return 班でログイン中のフラグ
	 */
	public boolean isGroupLogin() {
		return groupInfo!=null;
	}

	/**
	 * @return 課でログイン中のフラグ
	 */
	public boolean isUnitLogin() {
		return unitInfo!=null;
	}
	
	public boolean authorize() {

		ApiDto apiDto = this;
		
		ApplicationContext context = SpringContext.getApplicationContext();
		GroupInfoService groupInfoService = context.getBean(GroupInfoService.class);
		UnitInfoService unitInfoService = context.getBean(UnitInfoService.class);
		OauthtokenDataService oauthtokenDataService = context.getBean(OauthtokenDataService.class);
		//HttpServletRequest request = context.getBean(HttpServletRequest.class);
		//HttpServletResponse response = context.getBean(HttpServletResponse.class);
		
		// 初期化処理
		if(!apiDto.init) {
			apiDto.init = true;

			//boolean authorized = false;

			//
			// API KEY
			//
			if(!authorized) {
				String apikey = getAPIKey(request);
				if(StringUtil.isNotEmpty(apikey)) {

					// get groupInfo
					apiDto.groupInfo = groupInfoService.findByAPIKey(apikey);

					// get unitInfo
					apiDto.unitInfo = unitInfoService.findByAPIKey(apikey);

					// mark as authorized
					authorized = apiDto.groupInfo!=null || apiDto.unitInfo!=null;
				}
			}

			//
			// OAuth 2.0
			//
			if(!authorized) {
				try {
					// oauth_token get from request parameter
					String oauthToken = request.getParameter(OAuth.OAUTH_TOKEN);
					// oauth_token get from request header
					if(oauthToken==null) {
						try {
							OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
							oauthToken = oauthRequest.getAccessToken();
						} catch(org.apache.oltu.oauth2.common.exception.OAuthProblemException e) {
							// if Authorization header not found, do nothing(this request is not OAuth2 request)
							// else if Authorization header contain, throw OAuthProblemException
							String authorization = request.getHeader(OAuth.HeaderType.AUTHORIZATION);
							if(authorization!=null) {
								logger.error("authorization header: "+authorization, e);
								throw e;
							}
						}
					}
					// check valid oauth_token
					if ( null != oauthToken ) {
						OauthtokenData oauthtokenData = oauthtokenDataService.findByAccessToken(oauthToken);
						if(oauthtokenData!=null) {
							if(allowOAuth2) {
								logger.debug("valid oauth2. login as: "+oauthtokenData.groupid);

								// get oauthtokenData
								apiDto.oauthtokenData = oauthtokenData;

								// get groupInfo
								if(oauthtokenData.groupid!=null) {
									long groupid = oauthtokenData.groupid;
									GroupInfo groupInfo = groupInfoService.findById(groupid);
									apiDto.groupInfo = groupInfo;
								}

								// get unitInfo
								if(oauthtokenData.unitid!=null) {
									long unitid = oauthtokenData.unitid;
									UnitInfo unitInfo = unitInfoService.findById(unitid);
									apiDto.unitInfo = unitInfo;
								}
							}
							else {
								throw new ServiceException("OAuth2 not allowed.");
							}
						}
						else throw new ServiceException("invalid_token");
					}
				} catch(Exception e) {
				}
			}

			// mark as authorized
			authorized = apiDto.groupInfo!=null || apiDto.unitInfo!=null;

			// x-form-urlencoded 以外のリクエストデータを取得する
			if(apiDto.requestData==null) {
				if(request.getContentType() != null && request.getContentType().startsWith("application/x-zip-compressed")){
				}else if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")){
				}else{
					StringBuffer jb = new StringBuffer();
					String line = null;
					try {
						BufferedReader reader = request.getReader();
						while ((line = reader.readLine()) != null)
							jb.append(line);
					} catch (Exception e) {
						e.printStackTrace();
					}
					apiDto.requestData = jb.toString();
				}
			}

			// HTTPメソッドを取得する
			apiDto.method = request.getMethod();
			String methodOverride = request.getHeader(AbstractApiAction.REQUEST_HEADER_X_HTTP_METHOD_OVERRIDE);
			if(StringUtil.isNotEmpty(methodOverride)) {
				apiDto.method = methodOverride;
			}

			/*
			// CSRF対策(GETリクエスト以外はチェックする)
			// API のGETメソッドはデータ取得のみでデータの状態変化が起きないため、CSRF対策は不要とする.
			if("GET".equals(apiDto.method)==false) {
				// OAuthトークンがあればCSRF対策できているはず
				// OAuth認証できていなければ CRSFトークンチェックを行う。
				// ※OAuth認証なしで、POST,PUT,PATCH,DELETEリクエストを受け付けるAPIができた場合
				if(authorized==false) {
					// API のGETメソッドはデータ取得のみでデータの状態変化が起きないため、CSRF対策は不要とする.
					if(request.getServletPath().startsWith("/api/v1")) {
						if (!FormUtils.checkToken(request)) {
							return Response.sendJSONError("Invalid X-CSRF-Token", HttpServletResponse.SC_BAD_REQUEST).execute(response);
						}
					}
				}
			}
			*/
		}
		return authorized;
	}
	
	public String getAPIKey(HttpServletRequest request) {
		String apikey = request.getParameter("api_key");
		if(StringUtil.isEmpty(apikey)) {
			String authorization = request.getHeader("Authorization");
			if(authorization.indexOf(' ')==-1) {
				try{
				    UUID uuid = UUID.fromString(authorization);
					// uuid でパース成功の場合に APIKEYとして利用する
				    apikey = uuid.toString();
				} catch (IllegalArgumentException exception){
				    // do nothing
				}
			}
		}
		if(StringUtil.isEmpty(apikey)) return null;
		return apikey;
	}
	
	public String error401NotAuthorized(HttpServletResponse response) {
		try {
			// if invalid oauth_token, catch OAuthProblemException
			// Return the OAuth error message
			OAuthResponse oauthResponse = OAuthRSResponse
					.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
					.setRealm("SaigaiTask")
					.setError(OAuthError.ResourceResponse.INVALID_TOKEN)
					.buildHeaderMessage();

			// abort request
			Response res = Response.status(401).tag("User is not Authorized.")
					.header(OAuth.HeaderType.WWW_AUTHENTICATE,
							oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE))
					.build();
			res.execute(response);
		} catch(OAuthSystemException e) {
			
		}
		
		return null;
	}
}
