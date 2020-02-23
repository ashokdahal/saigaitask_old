/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import net.sf.json.JSONObject;

/**
 * ユーザ情報のAPI
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/userInfo")
public class UserInfoAction extends AbstractApiAction {

	@Resource protected GroupInfoService groupInfoService;
	@Resource protected UnitInfoService unitInfoService;
	@Resource protected UserInfoService userInfoService;

	/**
	 * GET: Not Support
	 * POST: 1件分のレコードを登録
	 * PUT: Not Support
	 * PATCH: Not Support
	 * @return API結果 JSONObject
	 */
	@RequestMapping(value={"/api/v2/userInfo"})
	@ResponseBody
	public String index() {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		if(isGetMethod()) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		else if(isPostMethod()) {
			if(request.getContentType().startsWith("application/json")) {

				// リクエストデータを取得
				JSONObject jsonObject = toJSONObject(apiDto.getRequestData());
				UserInfo userInfo = new UserInfo();
				DatabaseUtil.copyColumnFieldOnly(jsonObject, userInfo);

				// INSERT
				userInfoService.insert(userInfo);

				jsonObject = toJSONObject(userInfo);
				return responseJSONObject(jsonObject);
			}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		}
		//else if(isDeleteMethod()) { }
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}

	/**
	 * ID指定のAPI.
	 *
	 * GET: 指定IDのレコード取得
	 * POST: Not Support
	 * PUT: 指定IDのレコードを置換
	 * PATCH: 指定IDのレコードを一部更新
	 * DELETE: 指定IDのレコードを削除
	 *
	 * @param id ユーザID
	 *
	 * @return API結果 JSONObject
	 */
	@RequestMapping(value={"/api/v2/userInfo/{id}"})
	@ResponseBody
	public String apiById(@PathVariable Long id) {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		// ログインしていなければ取得できない
		if(apiDto.getGroupInfo()==null) {
			return Response.sendJSONError("No permission to access UserInfo", HttpServletResponse.SC_FORBIDDEN).execute(response);
		}

		if(isGetMethod()) {
			UserInfo userInfo = userInfoService.findById(id);
			JSONObject jsonObject = toJSONObject(userInfo);
			responseJSONObject(jsonObject);
		}
		else if(isPostMethod()) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		else if(isPutMethod() || isPatchMethod()) {
			if(request.getContentType().startsWith("application/json")) {

				// リクエストデータを取得
				JSONObject jsonObject = toJSONObject(apiDto.getRequestData());

				UserInfo userInfo = null;

				// UPDATE(REPLACE)
				if(isPutMethod()) {
					userInfo = new UserInfo();
					DatabaseUtil.copyColumnFieldOnly(jsonObject, userInfo);
					userInfo.id = id;
					userInfoService.update(userInfo);
				}
				// UPDATE(PARTIAL)
				else if(isPatchMethod()) {
					userInfo = userInfoService.findById(id);
					DatabaseUtil.copyColumnFieldOnly(jsonObject, userInfo);
					userInfo.id = id;
					userInfoService.update(userInfo);
				}

				jsonObject = toJSONObject(userInfo);
				return responseJSONObject(jsonObject);
			}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		}
		else if(isDeleteMethod()) {
			UserInfo userInfo = userInfoService.findById(id);
			userInfoService.delete(userInfo);
		}
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;
	}
}
