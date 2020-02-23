/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.beans.util.BeanMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * メニュータスク情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/taskmenuInfos")
public class TaskmenuInfosAction extends AbstractApiAction
{
	/** メニュープロセス情報サービス */
	@Resource
	protected MenuprocessInfoService menuprocessInfoService;

	/*メニュータスク情報サービス */
	@Resource
	protected MenutaskInfoService menutaskInfoService;

	/** タスクメニュー情報サービス */
	@Resource
	protected MenutaskmenuInfoService menutaskmenuInfoService;

	/** メニュー情報サービス */
	@Resource
	protected MenuInfoService menuInfoService;

	/**
	 * メニュータスク情報一覧API
	 * @param groupid
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/taskmenuInfos/{menutaskinfoid}"})
	@ResponseBody
	public String index(@PathVariable long menutaskinfoid) {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		if(isGetMethod()) {
			// results
			JSONArray results = new JSONArray();
			JSONObject jsonobj = new JSONObject();
			// response
			JSONObject jsonresult = new JSONObject();
			// メニュータスク情報の一覧を表示順の昇順で取得する。
			BeanMap conditions = new BeanMap();
			conditions.put("menutaskinfoid", menutaskinfoid);
			List<MenutaskmenuInfo> menutaskmenuInfos = menutaskmenuInfoService.findByCondition(conditions, "disporder", "", 0, 0);
			MenutaskInfo menutaskInfo = menutaskInfoService.findById(menutaskinfoid);
			MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(menutaskInfo.menuprocessinfoid);

			// メニュータスク情報、メニュプロセス情報が有効の時のみメニュータスク情報の一覧を返す
			if(menutaskInfo.valid && menuprocessInfo.valid) {
				for(MenutaskmenuInfo menutaskmenuInfo : menutaskmenuInfos) {
					jsonobj.put("id", menutaskmenuInfo.id);
					MenuInfo menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
					jsonobj.put("name", menuInfo.name);
					jsonobj.put("menutypeid", menuInfo.menutypeid);
					jsonobj.put("menuid", menuInfo.id);
					results.add(jsonobj);
				}
			}
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
