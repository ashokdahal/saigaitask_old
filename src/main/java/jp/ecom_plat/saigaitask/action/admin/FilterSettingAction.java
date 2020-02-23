/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import java.util.List;
import java.util.Vector;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.FeatureSearchCondition;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.ListSearchConditionInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.SessionGeometry;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.EcommapDataGetService;


@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/filterSetting")
public class FilterSettingAction extends AbstractAdminAction<LocalgovInfo> {

	/** サービスクラス */
	@Resource
	protected MenutaskInfoService menutaskInfoService;

	@Resource
	protected MenuprocessInfoService menuprocessInfoService;

	@Resource
	protected MenuloginInfoService menuloginInfoService;

	@Resource
	protected GroupInfoService groupInfoService;

	@Resource
	protected MapmasterInfoService mapmasterInfoService;

	@Resource
	protected MenuInfoService menuInfoService;

	FilterSettingAction() {
		// タブの位置
	}

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/filterSetting/index";
	}


	@RequestMapping(value="getUserMapIds/{taskid}")
	@ResponseBody
	public String getUserMapIds(
			@PathVariable("taskid") String strTaskId) throws ServiceException {
		JSONObject jsonError = new JSONObject();
		JSONObject json = new JSONObject();

		try {
//			String strTaskId = (String) request.getParameter("taskid"); //selectタグとするグリッドカラム名
			Long taskId = Long.parseLong(strTaskId);

//			MenutaskInfo menuTask = menutaskInfoService.findById(taskId);
			MenutaskInfo menuTask = menutaskInfoService.findBymenutasktypeinfoid(taskId);
			if (menuTask == null) {
				jsonError.put("result", 0);
				jsonError.put("error",  lang.__("Specified menu task info (menutasktypeinfoid = {0}) not exist.\n", taskId) +
						lang.__("Set menu task info ID {0} of menu task info table related to specified group info ID of local gov. ID.", taskId));
				responseService.response(jsonError.toString());
				return null;
			}

			MenuprocessInfo menuProcess = menuprocessInfoService.findById(menuTask.menuprocessinfoid);
			if (menuProcess == null) {
				jsonError.put("result", 4);
				jsonError.put("error",  lang.__("Specified menu process info (id = {0}) not exist.", menuTask.menuprocessinfoid));
				responseService.response(jsonError.toString());
				return null;
			}

			MenuloginInfo menuLogin = menuloginInfoService.findById(menuProcess.menulogininfoid);
			//menuLogin = null;
			if (menuLogin == null) {
				jsonError.put("result", 2);
				jsonError.put("error",  lang.__("Specified menu settings info (id = {0}) not exist.", menuProcess.menulogininfoid));
				responseService.response(jsonError.toString());
				return null;
			}

			GroupInfo groupInfo = groupInfoService.findById(menuLogin.groupid);
			if (groupInfo == null) {
				jsonError.put("result", 3);
				jsonError.put("error",  lang.__("Specified group info (id = {0}) not exist.", menuLogin.groupid));
				responseService.response(jsonError.toString());
				return null;
			}

			MapmasterInfo mapMaster = mapmasterInfoService.findByLocalgovInfoId(groupInfo.localgovinfoid);

			json.put("result"	, 1);
			json.put("ecomuser"	, groupInfo.ecomuser);
			json.put("mapid"	, mapMaster.mapid);
			responseService.response(json.toString());
		}
		catch (Exception ex) {
			try {
				jsonError.put("result"	, 0);
				jsonError.put("error", lang.__("An error occurred:") + ex.getMessage());
				responseService.response(jsonError.toString());
			}
			catch (Exception ex1) {

			}
		}

		return null;
	}

	//URL: http://192.168.0.167:8080/SaigaiTask2/admin/filterSetting/getLayerList/admin/16
	@RequestMapping(value="getLayerList/{euser}/{mapid}")
	@ResponseBody
	public String getLayerList(
			@PathVariable String euser,
			@PathVariable("mapid") String strMapId) throws ServiceException {
		EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		try {
//			String euser = (String) request.getParameter("euser");
//			String strMapId = (String) request.getParameter("mapid");
			int mapId = Integer.parseInt(strMapId);
			String strResult = ecommapDataGetService.getLayerList(euser, mapId);

			//strResult = "{'items':[{'layerid':'c20','name':'ホテル'},{'layerid':'c21','name':'学校'}]}";
			//responseService.response("Hello world");
			responseService.response(strResult);
		}catch(Exception e){

		}

		return null;
	}

	//URL: http://192.168.0.167:8080/SaigaiTask2/admin/filterSetting/getConditionList/admin/14
	@RequestMapping(value="getConditionList/{euser}/{mapid}")
	@ResponseBody
	public String getConditionList(
			@PathVariable String euser,
			@PathVariable("mapid") String strMapId) throws ServiceException {
		EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		try {
//			String euser = (String) request.getParameter("euser");
//			String strMapId = (String) request.getParameter("mapid");
			int mapId = Integer.parseInt(strMapId);
			String strResult = ecommapDataGetService.getConditionList(euser, mapId);

			responseService.response(strResult);
		}catch(Exception e){

		}

		return null;
	}

	//URL: http://192.168.0.167:8080/SaigaiTask2/admin/filterSetting/getConditionValue/admin/23
	@RequestMapping(value="getConditionValue/{euser}/{filterid}")
	@ResponseBody
	public String getConditionValue(
			@PathVariable String euser,
			@PathVariable String filterid) throws ServiceException {
		EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		try {
//			String euser = (String) request.getParameter("euser");
//			long filterId = Long.parseLong(request.getParameter("filterid"));

			String strResult = ecommapDataGetService.getConditionValue(euser, Long.parseLong(filterid));

			responseService.response(strResult);
		}catch(Exception e){

		}

		return null;
	}

	//URL: http://192.168.0.167:8080/SaigaiTask2/admin/filterSetting/getAttributeList/c20
	@RequestMapping(value="getAttributeList/{euser}/{layerid}")
	@ResponseBody
	public String getAttributeList(
			@PathVariable String euser,
			@PathVariable("layerid") String layerId) throws ServiceException {
		EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		try {
//			String euser = (String) request.getParameter("euser");
//			String layerId = (String) request.getParameter("layerid"); //selectタグとするグリッドカラム名
			String strResult = ecommapDataGetService.getAttributeList(euser, layerId);

			responseService.response(strResult);
		}catch(Exception e){

		}

		return null;
	}

	@RequestMapping(value="deleteConditions/{euser}")
	@ResponseBody
	public String deleteConditions(
			@PathVariable String euser) throws ServiceException {
		EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		JSONObject json = new JSONObject();
		try {
//			String euser = (String) request.getParameter("euser");
			String strIds = (String) request.getParameter("ids"); //selectタグとするグリッドカラム名

			String[] ids = strIds.split(",");
			int i;

			for (i = 0; i < ids.length; i++) {
				int delCount = ecommapDataGetService.deleteCondition(euser, Long.parseLong(ids[i]));

				if (delCount == 0) {
					json.put("result", 0);
					json.put("error", lang.__("Search conditions could not be saved. Check the user and map ID of e-Com map."));
					responseService.response(json.toString());
				// deleteConditionが成功した場合は、フィルターIDを持つテーブルを更新する
				}else{
					// メニュー情報テーブル
					// 修正対象:
					//   jp.ecom_plat.saigaitask.service.db.MenuInfoService
					//      findByFilterid(long filterid)を追加
					List<MenuInfo> menuInfos = menuInfoService.findByFilterid(Long.parseLong(ids[i]));
					for (MenuInfo menuInfo : menuInfos) {
						// フィルターIDは削除済みなのでnullをセット
						menuInfo.filterid = null;
						int updCnt = menuInfoService.update(menuInfo);
						if(updCnt == 0){
							// break;
						}
					}
				}
			}

			json.put("result", 1);
			responseService.response(json.toString());
		} catch(Exception e){

		}

		return null;
	}



	@RequestMapping(value="renderSearchName")
	@ResponseBody
	public String renderSearchName() throws ServiceException {
		//EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		try {
			String keywords = (String)request.getParameter("keywords");
			double buffer = Double.parseDouble(request.getParameter("buffer"));
			int spatialType = Integer.parseInt(request.getParameter("spatial"));


			MapDB mapDB = MapDB.getMapDB();
			String layerId = (String)request.getParameter("layer");
			LayerInfo layerInfo = mapDB.getLayerInfo(layerId);


			//キーワードをパースして検索条件文字列生成
			StringBuilder keywordsString = new StringBuilder();

			Vector<FeatureSearchCondition> vecCondition = null;
			if (keywords != null) {
				//属性名称に変換
				vecCondition = FeatureSearchCondition.parseKeywordString(keywords);
				for (FeatureSearchCondition condition : vecCondition) {
					AttrInfo attrInfo = layerInfo.getAttrInfo(condition.attrId);
					if (condition.logical != null) keywordsString.append(condition.logical).append(" ");
					keywordsString.append(attrInfo.name).append(condition.comp).append(condition.value).append(" ");
				}
			}

			if (spatialType > 0) {
				keywordsString.append(lang.__("Search range<!--2-->")).append(SessionGeometry.getSpatialTypeName(spatialType));
				if (buffer > 0) keywordsString.append(lang.__("Distance")+buffer+"m");
			}


			responseService.response(keywordsString.toString());
		}catch(Exception e){

		}

		return null;
	}

	/**
	 * 検索条件をDBへ挿入する。
	 *
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value="insertCondition")
	@ResponseBody
	public String insertCondition() throws ServiceException, JSONException {
		//EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		JSONObject json = new JSONObject();
		try {
			String keywords = (String)request.getParameter("keywords");
			double buffer = Double.parseDouble(request.getParameter("buffer"));
			int spatialType = Integer.parseInt(request.getParameter("spatial"));
			String strSpatialLayer = (String)request.getParameter("spatialLayer");	//JSON 形式

			MapDB mapDB = MapDB.getMapDB();
			String euser = (String)request.getParameter("user");
			UserInfo userInfo = mapDB.getAuthIdUserInfo(euser);
			String name = (String)request.getParameter("name");
			long mapId = Long.parseLong(request.getParameter("mapId"));
			String layerId = (String)request.getParameter("layer");
			LayerInfo layerInfo = mapDB.getLayerInfo(layerId);

			int isNot = Integer.parseInt(request.getParameter("isnot"));

			//キーワードをパースして検索条件文字列生成
			StringBuilder keywordsString = new StringBuilder();

			//空間条件処理
			JSONArray jsonSpatialLayer = new JSONArray(strSpatialLayer);
			if (jsonSpatialLayer.length() == 0)
				jsonSpatialLayer = null;

			Vector<FeatureSearchCondition> vecCondition = null;
			if (keywords != null) {
				//属性名称に変換
				vecCondition = FeatureSearchCondition.parseKeywordString(keywords);
				for (FeatureSearchCondition condition : vecCondition) {
					AttrInfo attrInfo = layerInfo.getAttrInfo(condition.attrId);
					if (condition.logical != null) keywordsString.append(condition.logical).append(" ");
					keywordsString.append(attrInfo.name).append(condition.comp).append(condition.value).append(" ");
				}
			}

			if (spatialType > 0) {
				keywordsString.append(lang.__("Search range<!--2-->")).append(SessionGeometry.getSpatialTypeName(spatialType));
				if (buffer > 0) keywordsString.append(lang.__("Distance")+buffer+"m");
			}

			long newId = ListSearchConditionInfo.insertCondition(userInfo, mapId, layerId, name!=null?name:keywordsString.toString(), keywords, buffer, spatialType, jsonSpatialLayer, (isNot == 1 ? true : false));

			if (newId <= 0) {
				json.put("error", lang.__("Search conditions could not be saved. Check the user and map ID of e-Com map."));
			} else {
				json.put("id", newId);
			}

			responseService.response(json.toString());

		}catch(Exception e){
			//e.printStackTrace();
			json = new JSONObject();
			json.put("error", lang.__("An error occurred.") + ", msg:" + e.getMessage());
			responseService.response(json.toString());
		}

		return null;
	}

	/**
	 * 検索条件のDBレコードを更新する。
	 *
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value="updateCondition")
	@ResponseBody
	public String updateCondition() throws ServiceException, JSONException {
		//EcommapDataGetService ecommapDataGetService = new EcommapDataGetService();

		JSONObject json = new JSONObject();
		try {
			long filterId = Long.parseLong(request.getParameter("filterid"));

			String keywords = (String)request.getParameter("keywords");
			double buffer = Double.parseDouble(request.getParameter("buffer"));
			int spatialType = Integer.parseInt(request.getParameter("spatial"));
			String strSpatialLayer = (String)request.getParameter("spatialLayer");	//JSON 形式

			MapDB mapDB = MapDB.getMapDB();
			String euser = (String)request.getParameter("user");
			UserInfo userInfo = mapDB.getAuthIdUserInfo(euser);
			String name = (String)request.getParameter("name");
			long mapId = Long.parseLong(request.getParameter("mapId"));
			String layerId = (String)request.getParameter("layer");
			LayerInfo layerInfo = mapDB.getLayerInfo(layerId);

			int isNot = Integer.parseInt(request.getParameter("isnot"));

			//キーワードをパースして検索条件文字列生成
			StringBuilder keywordsString = new StringBuilder();

			//空間条件処理
			JSONArray jsonSpatialLayer = new JSONArray(strSpatialLayer);
			if (jsonSpatialLayer.length() == 0)
				jsonSpatialLayer = null;

			Vector<FeatureSearchCondition> vecCondition = null;
			JSONArray keywordsArray = new JSONArray();
			if (keywords != null) {
				//属性名称に変換
				vecCondition = FeatureSearchCondition.parseKeywordString(keywords);
				for (FeatureSearchCondition condition : vecCondition) {
					AttrInfo attrInfo = layerInfo.getAttrInfo(condition.attrId);
					if (condition.logical != null) keywordsString.append(condition.logical).append(" ");
					keywordsString.append(attrInfo.name).append(condition.comp).append(condition.value).append(" ");
					keywordsArray.put(condition.toJSON());
				}
			}

			if (spatialType > 0) {
				keywordsString.append(lang.__("Search range<!--2-->")).append(SessionGeometry.getSpatialTypeName(spatialType));
				if (buffer > 0) keywordsString.append(lang.__("Distance")+buffer+"m");
			}

			// 2014.04.24
			// ListSearchConditionInfo.updateConditionが発行しているSQLがおかしい
			// ( update文なのにvalue句がある）ので更新が常に失敗する
			int updCont = ListSearchConditionInfo.updateCondition(userInfo, filterId, mapId, layerId, name!=null?name:keywordsString.toString(), keywordsArray, buffer, spatialType, jsonSpatialLayer, (isNot == 1 ? true : false));
			if (updCont <= 0) {
				json.put("error", lang.__("Search conditions could not be saved. Check the user and map ID of e-Com map."));
			} else {
				json.put("id", filterId);
			}

			responseService.response(json.toString());

		}catch(Exception e){
			e.printStackTrace();
			json = new JSONObject();
			json.put("error", lang.__("An error occurred.") + ", msg:" + e.getMessage());
			responseService.response(json.toString());
		}

		return null;
	}
}
