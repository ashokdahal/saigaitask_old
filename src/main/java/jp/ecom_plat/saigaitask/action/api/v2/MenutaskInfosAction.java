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
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * メニュータスク情報のAPIアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/menutaskInfos")
public class MenutaskInfosAction extends AbstractApiAction
{
	/** メニュー設定情報サービス */
	@Resource
	protected MenuloginInfoService menuloginInfoService;

	/** メニュープロセス情報サービス */
	@Resource
	protected MenuprocessInfoService menuprocessInfoService;

	/** メニュータスク情報サービス */
	@Resource
	protected MenutaskInfoService menutaskInfoService;

	/** タスクメニュー情報サービス */
	@Resource
	protected MenutaskmenuInfoService menutaskmenuInfoService;

	/**
	 * メニュータスク情報一覧API
	 * @param groupid
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/menutaskInfos/{groupid}"})
	@ResponseBody
	public String index(@PathVariable String groupid) {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		if(isGetMethod()) {
			// results
			JSONArray results = new JSONArray();
			MenuloginInfo menuloginInfo;
			List<MenuprocessInfo> menuprocessInfos = new ArrayList<>();
			List<MenutaskInfo> menutaskInfos = new ArrayList<>();

			// 班でログイン中を示すフラグ
			boolean isGroup = !groupid.substring(0, 1).equals("u");
			Long longGroupid;
			try{
				// groupidをStringからLongに変換する
				if(isGroup)
					longGroupid = Long.parseLong(groupid);
				else
					longGroupid = Long.parseLong(groupid.substring(1));

				// 班のメニュー設定情報
				if(isGroup)
					menuloginInfo = menuloginInfoService.findByGroupInfoIdAndNotDeleted(longGroupid);
				// 課のメニュー設定情報
				else
					menuloginInfo = menuloginInfoService.findByUnitInfoIdAndNotDeleted(longGroupid);

				// メニュー設定情報IDからメニュープロセス情報を取得する
				if(menuloginInfo != null)
					menuprocessInfos = menuprocessInfoService.findByMenuloginInfoIdAndValidAndNotDeletedAndVisible(menuloginInfo.id);

				// メニュープロセス情報が有効、メニュータスク情報が有効、タスクメニュー情報が空ではない場合に一覧を返す
				for(MenuprocessInfo menuprocessInfo : menuprocessInfos) {
					menutaskInfos = menutaskInfoService.findByMenuprocessInfoIdAndValidAndNotDeleted(menuprocessInfo.id);
					if(!menutaskInfos.isEmpty()) {
						JSONObject jsonmenuprocessInfo = new JSONObject();
						JSONArray jsonmenutaskInfos = new JSONArray();
						JSONObject jsonmenutaskInfo = new JSONObject();
						for(MenutaskInfo menutaskInfo : menutaskInfos) {
							List<MenutaskmenuInfo> menutaskmenuInfos = menutaskmenuInfoService.findByMenutaskInfoId(menutaskInfo.id);
							if(menutaskmenuInfos.size() > 0) {
								jsonmenuprocessInfo.put("menuprocessinfoid", menuprocessInfo.id);
								jsonmenuprocessInfo.put("menuprocess_name", menuprocessInfo.name);
								jsonmenutaskInfo.put("menutaskinfoid", menutaskInfo.id);
								jsonmenutaskInfo.put("menutask_name", menutaskInfo.name);
								jsonmenutaskInfos.add(jsonmenutaskInfo);
							}
						}
						jsonmenuprocessInfo.put("menutaskinfos", jsonmenutaskInfos);
						if(!jsonmenuprocessInfo.isEmpty() && jsonmenutaskInfos.size() > 0 )
							results.add(jsonmenuprocessInfo);
					}
				}

				JSONObject jsonresult = new JSONObject();
				jsonresult.put("total", results.size());
				jsonresult.put("results", results);
				responseJSONObject(jsonresult);
			} catch (NumberFormatException e) {
				return Response.sendJSONError(lang.__("groupid is incorrect.")+" groupid="+groupid, HttpServletResponse.SC_BAD_REQUEST).execute(response);
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}
}
