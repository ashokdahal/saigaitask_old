/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 班情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/groupInfos")
public class GroupInfosAction extends AbstractApiAction {

	/** 班情報サービス */
    @Resource
    protected GroupInfoService groupInfoService;

	/**
	 * 班情報一覧API
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/groupInfos/{localgovinfoid}"})
	@ResponseBody
	public String index(@PathVariable Long localgovinfoid) {

		// 班情報一覧は認証不要とする
		apiDto.authorize();
//		// 認証
//		if(apiDto.authorize()==false) {
//			return apiDto.error401NotAuthorized(response);
//		}
		
		if(isGetMethod()) {
			// results
			JSONArray results = new JSONArray();
			if(localgovinfoid!=null) {
				// ログイン画面と同様で、ログインなしで班情報IDと名称を返す
				// Get GroupInfo
				List<GroupInfo> groupInfos = groupInfoService.findByLocalgovInfoIdAndValid(localgovinfoid);
				for(GroupInfo groupInfo: groupInfos){
					long groupId = groupInfo.id;
					String groupName = groupInfo.name;
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("id", groupId);
					jsonobj.put("name", groupName);
					results.add(jsonobj);
				}
			}

			// Response
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
