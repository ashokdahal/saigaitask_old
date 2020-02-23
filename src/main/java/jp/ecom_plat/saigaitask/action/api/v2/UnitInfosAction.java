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
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ユニット情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/unitInfos")
public class UnitInfosAction extends AbstractApiAction{

	/** ユニット情報サービス */
	@Resource
	protected UnitInfoService unitInfoService;

	/**
	 * ユニット情報一覧API
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/unitInfos/{localgovinfoid}"})
	@ResponseBody
	public String index(@PathVariable Long localgovinfoid) {

		// 課情報一覧は認証不要とする
		apiDto.authorize();
//		// 認証
//		if(apiDto.authorize()==false) {
//			return apiDto.error401NotAuthorized(response);
//		}
		
		if(isGetMethod()) {

			// results
			JSONArray results = new JSONArray();
			// Check login
			if(/*apiDto.getGroupInfo()!=null && */localgovinfoid!=null) {
				// Get UnitInfo
				//List<UnitInfo> unitInfos = unitInfoService.findByGroupIDOrderByDisporder(groupid);
				List<UnitInfo> unitInfos = unitInfoService.findByLocalgovinfoid(localgovinfoid, true);
				for(UnitInfo unitInfo: unitInfos){
					long unitId = unitInfo.id;
					String unitName = unitInfo.name;
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("id", unitId);
					jsonobj.put("name", unitName);
					results.add(jsonobj);
				}
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
