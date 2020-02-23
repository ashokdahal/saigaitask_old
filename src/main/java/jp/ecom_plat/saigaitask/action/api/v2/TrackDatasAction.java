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
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/trackDatas")
public class TrackDatasAction extends AbstractApiAction{
	/** 災害情報サービス */
	@Resource
	protected TrackDataService trackDataService;

	@RequestMapping(value={"/api/v2/trackDatas/{current}"})
	@ResponseBody
	public String index(@PathVariable boolean current){
		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}
		if(isGetMethod()){
			// results
			JSONArray results = new JSONArray();
			List<TrackData> trackDatas = null;
			// Check login
			if(apiDto.getGroupInfo().localgovinfoid != 0L){
				if(!current){
					trackDatas = trackDataService.findByLocalgovInfoIdAndNotDelete(apiDto.getGroupInfo().localgovinfoid);
				}else{
					trackDatas = trackDataService.findByCurrentTrackDatas(apiDto.getGroupInfo().localgovinfoid);
				}
					for(TrackData trackData: trackDatas){
						long id = trackData.id;
						String name = trackData.name;
						JSONObject jsonobj = new JSONObject();
						jsonobj.put("id", id);
						jsonobj.put("name", name);
						results.add(jsonobj);
					}

			}
			JSONObject jsonresult = new JSONObject();
			jsonresult.put("total", results.size());
			jsonresult.put("results", results);
			responseJSONObject(jsonresult);
		}else{
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;
	}
}
