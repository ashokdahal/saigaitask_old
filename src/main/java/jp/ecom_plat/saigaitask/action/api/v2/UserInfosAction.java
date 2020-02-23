/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ユーザ情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/userInfos")
public class UserInfosAction extends AbstractApiAction
{
	/** ユーザ情報サービス */
	@Resource
	protected UserInfoService userInfoService;
	/**
	 * ユーザ情報一覧API
	 * @param unitid 
	 * 班ID : そのまま
	 * ユニットID : 先頭にuをつける
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/userInfos/{unitid}"})
	@ResponseBody
	public String index(@PathVariable Long unitid) {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}
		
		if(isGetMethod()) {
			// results
			JSONArray results = new JSONArray();
			List<UserInfo> userInfos = new ArrayList<UserInfo>();

			// Check login
			// 班でログイン
			if(apiDto.getGroupInfo()!=null && !apiDto.getGroupInfo().id.equals(0L)) {
				//GroupInfo groupInfo = groupInfoService.findByNotDeletedId(apiDto.getGroupInfo().id);
				// Get UserInfo
				//List<UserInfo> userInfos = userInfoService.findByUnitIdAllOrderByDisporder(unitid);
				userInfos = userInfoService.findByGroupIdAndValid(unitid);
			}
			// 課でログイン
			else if(apiDto.getUnitInfo()!=null && !apiDto.getUnitInfo().id.equals(0L)){
				userInfos = userInfoService.findByUnitIdAndValid(unitid);
			}
			// 未ログイン状態
			else{
				return Response.sendJSONError("No permission to regist UserInfo", HttpServletResponse.SC_FORBIDDEN).execute(response);
			}
			for(UserInfo userInfo: userInfos){
				long userId = userInfo.id;
				String userName = userInfo.name;
				String userMobileno = userInfo.mobileno;
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("id", userId);
				jsonobj.put("name", userName);
				jsonobj.put("mobileno", userMobileno);
				results.add(jsonobj);
			}

			JSONObject jsonresult = new JSONObject();
			jsonresult.put("total", results.size());
			jsonresult.put("results", results);
			responseJSONObject(jsonresult);
		}
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return null;
	}
}
