/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.oauth2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.seasar.framework.util.StringUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.map.db.StoredConsumer;
import jp.ecom_plat.map.oauth2.OAuth2Provider;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.OauthtokenData;
import jp.ecom_plat.saigaitask.form.api.AuthorizeForm;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.OauthtokenDataService;
import net.sf.json.JSONArray;

/**
 * OAuthクライアント認証アクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class AuthorizeAction extends AbstractApiAction {

	/** Logger log4j */
	private static Logger logger = Logger.getLogger(TokenAction.class.getName());

	/** Consumer Description */
	public String consumerDescription;
	// Service DI
	/** 班情報サービス */
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected OauthtokenDataService oauthtokenDataService;

	@Override
	public void setupModel(Map<String, Object> model) {
		super.setupModel(model);
		model.put("consumerDescription", consumerDescription);
	}
	
	/**
	 * OAuthクライアント認証エンドポイント
	 * @return フォワード先、リダイレクト先 URL
	 */
	@RequestMapping(value={"/oauth2/authorize/","/oauth2/authorize/index"})
	public String index(Map<String,Object>model,
			@ModelAttribute AuthorizeForm authorizeForm, BindingResult bindingResult) {
		try {
			return authorize(model, authorizeForm, bindingResult).execute(response);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * OAuthクライアント認証処理
	 * @return Response
	 * @throws OAuthSystemException when invalid method(GET), parameter, oauth client
	 * @throws URISyntaxException 
	 */
	public Response authorize(Map<String,Object>model,
			@ModelAttribute AuthorizeForm authorizeForm, BindingResult bindingResult) throws OAuthSystemException, URISyntaxException {

		logger.debug("OAuth authorize request: remoteAddr="+request.getRemoteAddr()+", isSecure="+request.isSecure());

		String redirectUri = null;
		try {
			// dynamically recognize an OAuth profile based on request characteristic (params,
			// method, content type etc.), perform validation
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
			redirectUri = oauthRequest.getRedirectURI();

			//
			// validate client
			//
			authorizeForm.consumerKey = oauthRequest.getClientId();
			StoredConsumer storedConsumer = OAuth2Provider.getStoredConsumer(oauthRequest.getClientId());
			if(storedConsumer==null) {
				OAuthResponse clientErrorResponse = OAuth2Provider.getInvalidClientResponse().buildJSONMessage();
				return Response.status(clientErrorResponse.getResponseStatus()).entity(clientErrorResponse.getBody()).build();
			}
			// サイトが存在するかどうかのチェックは不要とする
			/*
			CommunityInfo community = OAuth2Provider.getCommunityInfo(oauthRequest.getClientId());
			if(community==null) {
				OAuthResponse clientErrorResponse = OAuth2Provider.getInvalidClientResponse()
						.setErrorDescription("community not exist.")
						.buildJSONMessage();
				return Response.status(clientErrorResponse.getResponseStatus()).entity(clientErrorResponse.getBody()).build();
			}
			*/

			//
			// check login user
			// 班でも課でもログインしていなければログイン画面表示
			if( loginDataDto.getGroupInfo()==null && loginDataDto.getUnitInfo()==null ) {
				// redirect login page with return_url
				String requestUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
				String loginPageUrl = "/admin/";
				// set return_url (return from /admin login page)
				session.setAttribute("return_admin_uri", requestUrl);
				return Response.temporaryRedirect(URI.create(loginPageUrl), request).build();
			}

			//
			// check user authorize
			// ユーザがOAuthクライアントに認可しているかどうか
			//
			boolean userAuthorized = false;
			JSONArray authorizedClients = getAuthorizedClients();
			for(int idx=0; idx<authorizedClients.size(); idx++) {
				String authorizedClient = authorizedClients.getString(idx);
				if(oauthRequest.getClientId().equals(authorizedClient)) {
					userAuthorized = true;
					clearAuthorizedClients();
					break;
				}
			}

			// not authorized
			if(!userAuthorized) {
				try {
					consumerDescription = (String) storedConsumer.getSetting("description");
					String requestUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
					authorizeForm.oauthCallback = requestUrl;
					return Response.status(0).location(new URI(confirm(model, authorizeForm, bindingResult)), request).build();
				} catch(Exception e) {
					throw new ServiceException(e);
				}
			}

			//
			// OAuth2 Issue Code or Token
			//
			//build response according to response_type
			OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse
					.authorizationResponse(request, HttpServletResponse.SC_FOUND);
			builder.location(redirectUri);

			// ======================================================================================
			// GrantType: Authorization Code Grant
			// ここで発行した認可コードは /oauth2/token?grant_type=authorization_code にてアクセストークンに交換する
			if(ResponseType.CODE.toString().equalsIgnoreCase(oauthRequest.getResponseType())) {
				// issue authorization_code(認可コード)
				String authorizationCode = null;
				if(loginDataDto.getGroupInfo()!=null) {
					authorizationCode = oauthtokenDataService.issueAuthorizationCodeByGroupId(oauthRequest.getClientId(), loginDataDto.getGroupid());
				}
				if(loginDataDto.getUnitInfo()!=null) {
					authorizationCode = oauthtokenDataService.issueAuthorizationCodeByUnitId(oauthRequest.getClientId(), loginDataDto.getUnitid());
				}
				if(authorizationCode==null) throw OAuthUtils.handleOAuthProblemException("Cannot issue authorization_code");

				// set redirect url paramerter "code"
				builder.setCode(authorizationCode);
			}
			// ======================================================================================
			// GrantType: Implicit Grant
			// Implicit Grant は、client_secretを隠すことが出来ないJavaScriptなどのために
			// 認可コードを /oauth2/token にてアクセストークンに交換する処理を省略して、
			// この場でアクセストークンを発行する.
			else if(ResponseType.TOKEN.toString().equalsIgnoreCase(oauthRequest.getResponseType())) {
				// issue access_token
				String accessToken = null;
				if(loginDataDto.getGroupInfo()!=null) {
					OauthtokenData oauthtokenData = oauthtokenDataService.issueAccessTokenByGroupId(oauthRequest.getClientId(), loginDataDto.getGroupid());
					accessToken = oauthtokenData.accessToken;
				}
				if(loginDataDto.getUnitInfo()!=null) {
					OauthtokenData oauthtokenData = oauthtokenDataService.issueAccessTokenByUnitId(oauthRequest.getClientId(), loginDataDto.getUnitid());
					accessToken = oauthtokenData.accessToken;
				}
				if(accessToken==null) throw OAuthUtils.handleOAuthProblemException("Cannot issue access_token");

				// set redirect url paramerter "access_token"
				builder.setAccessToken(accessToken);
				//builder.setExpiresIn(3600l); // optional
			}
			else {
				throw OAuthUtils.handleOAuthProblemException("Invalid response_type parameter value");
			}

			//build OAuth response
			// OAuthクライアントの指定したURLにリダイレクトする。
			OAuthResponse resp = builder.buildQueryMessage();
			logger.debug("/oauth2/authorize redirect_url:"+resp.getLocationUri());
			return Response.status(resp.getResponseStatus()).location(URI.create(resp.getLocationUri()), request).build();

			//if something goes wrong
		} catch(OAuthProblemException e) {
			logger.error("/oauth2/authorize error", e);

			// For missing parameter "redirect_uri" on OAuth authorize request
			if(redirectUri==null) redirectUri = "/oauth2/authorize/error";
			OAuthResponse resp = OAuthASResponse
					.errorResponse(HttpServletResponse.SC_FOUND)
					.error(e)
					.location(redirectUri)
					.buildQueryMessage();

			return Response.status(resp.getResponseStatus()).location(URI.create(resp.getLocationUri()), request).build();
		}
	}

	/**
	 * @return confirm page
	 * @throws OAuthSystemException
	 */
	@RequestMapping("/oauth2/authorize/confirm")
	public String confirm(Map<String,Object>model,
			@ModelAttribute AuthorizeForm authorizeForm, BindingResult bindingResult) {
		initAction();
		setupModel(model);
		return "/oauth2/authorize/confirm/index";
	}

	/**
	 * @return oauthCallback URL
	 * @throws OAuthSystemException
	 */
	@RequestMapping("/oauth2/authorize/allow")
	public String allow(Map<String,Object>model,
			@ModelAttribute AuthorizeForm authorizeForm, BindingResult bindingResult) {
		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}

    	// add authorized client
		 String consumerKey = authorizeForm.consumerKey;		
		if(StringUtil.isNotEmpty(consumerKey)) {
			addAuthorizedClients(consumerKey);
		}

		return "redirect:"+authorizeForm.oauthCallback;
	}

	/**
	 * @return ログインユーザが許可したOAuthクライアントリスト
	 */
	public JSONArray getAuthorizedClients() {
		JSONArray authorizedClients = new JSONArray();
		Object obj = session.getAttribute("authorizedClient");
		if(obj!=null) authorizedClients = (JSONArray) obj;
		return authorizedClients;
	}

	/**
	 * @param consumerKey ログインユーザが許可するOAuthクライアント
	 * @return ログインユーザが許可したOAuthクライアントリスト
	 */
	public JSONArray addAuthorizedClients(String consumerKey) {
		JSONArray authorizedClients = getAuthorizedClients();
		authorizedClients.add(consumerKey);
		session.setAttribute("authorizedClient", authorizedClients);
		return authorizedClients;
	}

	/**
	 * ログインユーザが許可したOAuthクライアントリストをクリアする
	 */
	public void clearAuthorizedClients() {
		session.setAttribute("authorizedClient", null);
	}
}
