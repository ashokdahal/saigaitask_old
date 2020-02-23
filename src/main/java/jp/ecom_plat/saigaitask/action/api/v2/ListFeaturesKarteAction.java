package jp.ecom_plat.saigaitask.action.api.v2;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResult.FileResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.RequestScopeController;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistkarteInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistkarteInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RequestScopeController
public class ListFeaturesKarteAction extends AbstractApiAction {

	/** タスクメニュー情報サービス */
	@Resource
	protected MenutaskmenuInfoService menutaskmenuInfoService;

	/** メニュー情報サービス */
	@Resource
	protected MenuInfoService menuInfoService;

	/** メニューテーブル情報サービス */
	@Resource
	protected MenutableInfoService menutableInfoService;

	/** 地図マスター情報サービス */
	@Resource
	protected MapmasterInfoService mapmasterInfoService;

	/** テーブルマスター情報サービス */
	@Resource
	protected TablemasterInfoService tablemasterInfoService;

	@Resource
	protected TableService tableService;

	/** テーブルリストカルテ情報サービス */
	@Resource
	protected TablelistkarteInfoService tablelistkarteInfoService;

	/** テーブルリスト項目情報サービス */
	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;

	@RequestMapping("/api/v2/listFeaturesKarte")
	@Transactional(propagation=Propagation.NEVER)
	@ResponseBody
	public String index(@RequestParam(name = "taskmenuinfoid", required = true) long taskmenuinfoid,
			@RequestParam(name = "gid", required = true) long gid) {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		if(isGetMethod()) {
			MapDB mapDB = MapDB.getMapDB();
			JSONArray results = new JSONArray();
			JSONObject result = new JSONObject();

			MenutaskmenuInfo menutaskmenuInfo = menutaskmenuInfoService.findById(taskmenuinfoid);
			// タスクメニュー情報がみつからなければ終了
			if(menutaskmenuInfo == null) {
				result.put("addable", "");
				result.put("deletable", "");
				result.put("attrinfos", new JSONArray());
				result.put("values", new JSONArray());
				result.put("files", new JSONArray());
				results.add(result);
				return responseJSONObject(results);
			}
			long menuinfoid = menutaskmenuInfo.menuinfoid;
			MenuInfo menuInfo = menuInfoService.findByNotDeletedId(menuinfoid);

			List<MenutableInfo> menutableInfos = menutableInfoService.findByMenuInfoId(menuInfo.id);

			for(MenutableInfo menutableInfo : menutableInfos) {
				List<TablelistkarteInfo> columnList = tablelistkarteInfoService.findByMenutableInfoId(menutableInfo.id);
				TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(menutableInfo.tablemasterinfoid);
				String layerid = tablemasterInfo.layerid;
				LayerInfo layerInfo = mapDB.getLayerInfo(layerid);
				if(columnList.size() != 0) {
					List<TablelistkarteInfo> tablelistkarteInfos = tablelistkarteInfoService.findByMenutableInfoId(menutableInfo.id);
					if(tablelistkarteInfos.size() > 0) {
						// フィーチャリストを取得する
						try {
							//JSONArray attrids = new JSONArray();
							//JSONArray th_names = new JSONArray();
							JSONArray values = new JSONArray();
							JSONArray attrInfos = new JSONArray();
							JSONArray files = new JSONArray();

							FeatureResult resultList = FeatureDB.getFeatureContent(layerInfo, gid, false, true, FeatureDB.GEOM_TYPE_CENTER, null);
							// 添付ファイル
							for (int i = 0;i < resultList.countFileResult();i++) {
								JSONObject obj = new JSONObject();
								FileResult fileResult = resultList.getFileResult(i);
								obj.put("title", fileResult.title);
								obj.put("url", fileResult.url);
								files.add(obj);
							}

							for(TablelistkarteInfo tablelistkarteInfo : tablelistkarteInfos) {
								JSONObject obj = new JSONObject();
								String attrid = tablelistkarteInfo.attrid;
								//attrids.add(attrid);
								//th_names.add(tablelistkarteInfo.name);
								if(resultList != null)
									values.add(resultList.getAttrResult(attrid).getAttrValue());
								AttrInfo attr = layerInfo.getAttrInfo(attrid);
								/*result.put("attrid", attrids);
								result.put("th_name", th_names);
								result.put("values", values);*/
								obj.put("attrId", attrid);
								if(!(attr.dataType == AttrInfo.DATATYPE_SELECT || attr.dataType == AttrInfo.DATATYPE_CHECKBOX))
									obj.put("dataExp", "");
								else
									obj.put("dataExp", attr.dataExp);
								obj.put("dataType", attr.dataType);
								obj.put("name", attr.name);
								obj.put("nullable", attr.nullable);
								obj.put("editable", tablelistkarteInfo.editable);
								attrInfos.add(obj);
							}
							JSONObject obj = new JSONObject();
							obj.put("attrId", "gid");
							obj.put("dataExp", "");
							obj.put("dataType", "");
							obj.put("name", "gid");
							obj.put("nullable", "");
							obj.put("editable", "");
							attrInfos.add(obj);
							if(resultList != null)
								values.add(gid);
							result.put("addable", menutableInfo.addable);
							result.put("deletable", menutableInfo.deletable);
							result.put("attrinfos", attrInfos);
							result.put("values", values);
							result.put("files", files);
						} catch (Exception e) {
							return Response.sendJSONError(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST).execute(response);
						}
						results.add(result);
						return responseJSONObject(results);
					}
				}
				// カルテ情報がなければ、テーブルリストカラム情報の設定を流用
				else {
					List<TablelistcolumnInfo> tablelistcolumnInfos = tablelistcolumnInfoService.findByMenutableInfoId(menutableInfo.id);
					//JSONArray attrids = new JSONArray();
					//JSONArray th_names = new JSONArray();
					JSONArray values = new JSONArray();
					JSONArray attrInfos = new JSONArray();
					JSONArray files = new JSONArray();
					FeatureResult resultList;
					try {
						resultList = FeatureDB.getFeatureContent(layerInfo, gid, false, true, FeatureDB.GEOM_TYPE_CENTER, null);
						// 添付ファイル
						if(resultList != null) {
							for (int i = 0;i < resultList.countFileResult();i++) {
								JSONObject obj = new JSONObject();
								FileResult fileResult = resultList.getFileResult(i);
								obj.put("title", fileResult.title);
								obj.put("url", fileResult.url);
								files.add(obj);
							}
						}
						for(TablelistcolumnInfo colinfo : tablelistcolumnInfos) {
							JSONObject obj = new JSONObject();
							String attrid = colinfo.attrid;
							//attrids.add(attrid);
							//th_names.add(colinfo.name);
							if(resultList != null)
								values.add(resultList.getAttrResult(attrid).getAttrValue());
							AttrInfo attr = layerInfo.getAttrInfo(attrid);
							obj.put("attrId", attrid);
							if(!(attr.dataType == AttrInfo.DATATYPE_SELECT || attr.dataType == AttrInfo.DATATYPE_CHECKBOX))
								obj.put("dataExp", "");
							else
								obj.put("dataExp", attr.dataExp);
							obj.put("dataType", attr.dataType);
							obj.put("name", attr.name);
							obj.put("nullable", attr.nullable);
							obj.put("editable", colinfo.editable);
							attrInfos.add(obj);
						}
						//result.put("attrid", attrids);
						//result.put("th_name", th_names);
						JSONObject obj = new JSONObject();
						obj.put("attrId", "gid");
						obj.put("dataExp", "");
						obj.put("dataType", "");
						obj.put("name", "gid");
						obj.put("nullable", "");
						obj.put("editable", "");
						attrInfos.add(obj);
						if(resultList != null)
							values.add(gid);
						result.put("addable", menutableInfo.addable);
						result.put("deletable", menutableInfo.deletable);
						result.put("attrinfos", attrInfos);
						result.put("values", values);
						result.put("files", files);
					}
					catch (Exception e) {
						e.printStackTrace();
						return Response.sendJSONError(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST).execute(response);
					}
					results.add(result);
					return responseJSONObject(results);
				}
			}
		}
		return null;
	}
}
