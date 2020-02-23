/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.oauth2;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.StoredConsumer;
import jp.ecom_plat.map.oauth2.OAuth2Provider;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction.Response;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.OauthtokenData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.OauthtokenDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;

/**
 * OAuthトークン発行アクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class TokenAction extends AbstractAction {

	/** Logger log4j */
	private static Logger logger = Logger.getLogger(TokenAction.class.getName());

	// Service DI
	/** 班情報サービス */
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected OauthtokenDataService oauthtokenDataService;
	/** 課情報サービス */
	@Resource protected UnitInfoService unitInfoService;

	/**
	 * OAuthトークン発行エンドポイント
	 * @return フォワード先、リダイレクト先 URL
	 */
	@RequestMapping(value={"/oauth2/token/","/oauth2/authorize/token"})
	@ResponseBody
	public String index() {
		try {
			Response res = token();
			return res.execute(response);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * OAuthトークン発行処理
	 * @return Response
	 * @throws OAuthSystemException when invalid method(GET), parameter, oauth client
	 */
	public Response token() throws OAuthSystemException {

		OAuthTokenRequest oauthRequest = null;

		logger.debug("OAuth token request: remoteAddr="+request.getRemoteAddr()+", isSecure="+request.isSecure());

		try {
			oauthRequest = new OAuthTokenRequest(request);

			//
			// validate client
			//
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
			// client authenticate
			//
			if(! storedConsumer.getConsumerKeySecret().equals(oauthRequest.getClientSecret())) {
				OAuthResponse clientErrorResponse = OAuth2Provider.getUnauthorizedClientResponse();
				return Response.status(clientErrorResponse.getResponseStatus()).entity(clientErrorResponse.getBody()).build();
			}


			//
			// OAuth2 Issue Token
			//
			String accessToken = null;

			// do checking for different grant types
			String grantType = oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE);

			// GrantType: Authorization Code Grant
			// 認可コードと引き換えにアクセストークンを発行する
			// 認可コードは /oauth2/authorize で取得したものを利用する
			if (GrantType.AUTHORIZATION_CODE.toString().equalsIgnoreCase(grantType)) {
				// issue access_token if valid authorization_code
				String authorizationCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
				OauthtokenData oauthtokenData = oauthtokenDataService.issueAccessToken(authorizationCode);
				accessToken = oauthtokenData.accessToken;
				if (accessToken==null) {
					String msg = "invalid authorization code.";
					logger.error("OAuth2 [Authorization Code Grant]"+msg
							+" clientId="+storedConsumer.getConsumerKey()
							+", authorization_coe="+authorizationCode);
					return getInvalidGrantErrorResponse(msg);
				}
			}
			// GrantType: Resource Owner Password Credentials Grant
			else if (GrantType.PASSWORD.toString().equalsIgnoreCase(grantType)) {
				// check username, password
				Long groupId = null;
				Long unitId = null;
				OauthtokenData oauthtokenData = null;
				try {
					// check GroupInfo login
					String username = oauthRequest.getUsername();
					if(StringUtils.isEmpty(username)){
						throw new ServiceException("invalid groupid");
					}
					//groupId = Long.valueOf(oauthRequest.getUsername());
					// 課IDを利用
					if(username.substring(0,1).equals("u")){
						unitId = Long.valueOf(username.substring(1, username.length()));
						UnitInfo unitInfo = unitInfoService.findByLoginIdAndPasswordAndValid(unitId, oauthRequest.getPassword());
						if(unitInfo==null) throw new ServiceException("invalid unitid or password");
						// issue access_token
						oauthtokenData = oauthtokenDataService.issueAccessTokenByUnitId(oauthRequest.getClientId(), unitId);
					}
					// 班IDを利用
					else{
						groupId = Long.valueOf(username.substring(0, username.length()));
						GroupInfo groupInfo = groupInfoService.findByLoginIdAndPasswordAndValid(groupId, oauthRequest.getPassword());
						if(groupInfo==null) throw new ServiceException("invalid groupid or password");
						// issue access_token
						oauthtokenData = oauthtokenDataService.issueAccessTokenByGroupId(oauthRequest.getClientId(), groupId);
					}
					accessToken = oauthtokenData.accessToken;
				} catch(Exception e) {
					String msg = "invalid username or password.";
					logger.error("OAuth2 [Resource Owner Password Credentials Grant] "+msg
							+" "+e.getMessage()+"\n"
							+" clientId="+storedConsumer.getConsumerKey()
							+", groupId="+groupId
							+", unitid="+unitId, e);
					return getInvalidGrantErrorResponse(msg);
				}
			}
			// GrantType: Refresh Token
			else if (GrantType.REFRESH_TOKEN.toString().equalsIgnoreCase(grantType)) {
				// refresh token is not supported in this implementation
				String msg = "refresh token is not supported yet.";
				logger.error("OAuth2 [Refresh Token] "+msg
						+" clientId="+storedConsumer.getConsumerKey());
				return getInvalidGrantErrorResponse(msg);
			}
			// GrantType: Client Credentials Grant
			else if (GrantType.CLIENT_CREDENTIALS.toString().equalsIgnoreCase(grantType)) {
				// client_credentials is not supported in this implementation
				String msg = "Client Credentials Grant is not supported.";
				logger.error("OAuth2 [Client Credentials Grant] "+msg
						+" clientId="+storedConsumer.getConsumerKey());
				return getInvalidGrantErrorResponse(msg);
			}
			else {
				return getInvalidGrantErrorResponse("Invalid grant_type parameter value");
			}

			// check accessToken
			if(accessToken==null) {
				throw OAuthProblemException.error("internal_error", "cannot issue access_token.");
			}

			OAuthResponse response = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken)
					//.setExpiresIn("3600")
					.buildJSONMessage();
			return Response.status(response.getResponseStatus()).entity(response.getBody()).build();

		} catch (OAuthProblemException e) {
			logger.error(e.getMessage(), e);
			OAuthResponse res = OAuthASResponse
					.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
					.error(e)
					.buildJSONMessage();
			return Response.status(res.getResponseStatus()).entity(res.getBody()).build();
		}
	}

	private Response getInvalidGrantErrorResponse(String description) throws OAuthSystemException {
		OAuthResponse response = OAuthASResponse
				.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
				.setError(OAuthError.TokenResponse.INVALID_GRANT)
				.setErrorDescription(description)
				.buildJSONMessage();
		return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
	}
}
