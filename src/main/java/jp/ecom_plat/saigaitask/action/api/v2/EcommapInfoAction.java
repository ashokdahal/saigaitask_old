/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.oauth2.OAuth2Provider;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.util.Config;
import net.sf.json.JSONObject;

/**
 * eコミマップ情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/ecommapInfo")
public class EcommapInfoAction extends AbstractApiAction {

	/**
	 * エンドポイント
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/ecommapInfo"})
	@ResponseBody
	public String index() {

		// このAPIは認証なしでも使える
		apiDto.authorize();
//		// 認証
//		if(apiDto.authorize()==false) {
//			return apiDto.error401NotAuthorized(response);
//		}

		// Get Access Token
		if(isGetMethod()) {
			String accessToken = null;
			String authId = null;
			String errorMsg = null;
			if(apiDto.getGroupInfo()!=null) {
				GroupInfo groupInfo = apiDto.getGroupInfo();
				long groupId = groupInfo.id;
				errorMsg = "groupId:"+groupId;
				authId = groupInfo.ecomuser;
			}
			if(apiDto.getUnitInfo()!=null) {
				UnitInfo unitInfo = apiDto.getUnitInfo();
				long unitId = unitInfo.id;
				errorMsg = "unitId:"+unitId;
				authId = unitInfo.ecomuser;
			}
			
			if(authId==null) {
				throw new ServiceException(errorMsg+" ecomuser not set.");
			}

			//
			// OAuth2 Issue Token
			//
			try {
				OAuth2Provider oauth2Provider = new OAuth2Provider();

				// issue ecommap access_token
				MapDB mapDB = MapDB.getMapDB();
				UserInfo userInfo = mapDB.getAuthIdUserInfo(authId);
				if(userInfo==null) throw new ServiceException(errorMsg+" ecomuser not found.");

				// issue access_token if user authenticate
				accessToken = oauth2Provider.issueAccessToken(apiDto.getOauthtokenData().consumerKey, userInfo);
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}

			// Get Ecommap URL
			String ecommapURL = Config.getEcommapURL();

			// Response JSONObject
			JSONObject json = new JSONObject();
			json.put("url", ecommapURL+"map/");
			json.put("token", accessToken);
			responseJSONObject(json);
		}
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}
}
