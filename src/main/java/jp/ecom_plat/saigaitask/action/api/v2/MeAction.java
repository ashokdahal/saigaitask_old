/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import net.sf.json.JSONObject;

/**
 * ログイン情報取得API
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class MeAction extends AbstractApiAction {

	/**
	 * ログイン情報取得API
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/me"})
	@ResponseBody
	public String index() {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}
		
		if(isGetMethod()) {


			// Response JSONObject
			JSONObject json = new JSONObject();
			if(apiDto.isGroupLogin()) {
				json.put("localgovinfoid", apiDto.getGroupInfo().localgovinfoid);
				json.put("groupid", apiDto.getGroupInfo().id);
			}
			if(apiDto.isUnitLogin()) {
				json.put("localgovinfoid", apiDto.getUnitInfo().localgovinfoid);
				json.put("unitid", apiDto.getUnitInfo().id);
			}
			// OAuth認証の場合のみ
			if(apiDto.getOauthtokenData()!=null) {
				json.put("token_created", TimeUtil.iso8601Formatter.format(apiDto.getOauthtokenData().created));
			}
			responseJSONObject(json);
		}
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}
}
