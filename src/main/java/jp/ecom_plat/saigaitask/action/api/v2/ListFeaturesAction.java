package jp.ecom_plat.saigaitask.action.api.v2;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.beans.util.BeanMap;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResultList;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
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

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class ListFeaturesAction extends AbstractApiAction {

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

	/** テーブルリスト項目情報サービス */
	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;

	/** テーブルリストカルテ情報サービス */
	@Resource
	protected TablelistkarteInfoService tablelistkarteInfoService;

	// カルテフラグ(未使用)
	//boolean isKarte = false;

	@RequestMapping(value={"/api/v2/listFeatures"})
	@Transactional(propagation=Propagation.NEVER)
	@ResponseBody
	public String index(@RequestParam(name = "taskmenuinfoid", required = true) long taskmenuinfoid,
						@RequestParam(name = "sort", required = false, defaultValue = "") String sort,
						@RequestParam(name = "limit", required = false, defaultValue = "0") int limit,
						@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
						@RequestParam(name = "position", required = false, defaultValue = "") String position,
						@RequestParam(name = "desc", required = false, defaultValue = "false") boolean desc) {
		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}
		//　取得したデータ数の件数
		int total = 0;
		if(isGetMethod()) {
			MapDB mapDB = MapDB.getMapDB();
			JSONObject result = new JSONObject();
			JSONArray results = new JSONArray();
			MenutaskmenuInfo menutaskmenuInfo = menutaskmenuInfoService.findById(taskmenuinfoid);
			String layerid = "";

			// menutaskmenuInfo がnullの場合はlimitに0を入れて終了
			if(menutaskmenuInfo == null) {
				total = 0;
			}
			else{
				long menuinfoid = menutaskmenuInfo.menuinfoid;
				MenuInfo menuInfo = menuInfoService.findByNotDeletedId(menuinfoid);
				List<MenutableInfo> menutableInfos = menutableInfoService.findByMenuInfoId(menuInfo.id);
				// 項目名リスト
				List<?> columnList;
				for(MenutableInfo menutableInfo : menutableInfos) {
					// カルテフラグがfalseなら項目名リストにテーブルリスト項目情報
					//if(!isKarte)
						columnList = tablelistcolumnInfoService.findByMenutableInfoId(menutableInfo.id);
					// カルテフラグがtrue(listfeaturesKarteが呼ばれていたら)なら項目名リストにテーブルリストカルテ情報
					/*else
						columnList = tablelistkarteInfoService.findByMenutableInfoId(menutableInfo.id);*/
					// テーブルリスト項目情報が設定されていない場合は終了
					if(columnList.size() == 0)
						break;
					TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(menutableInfo.tablemasterinfoid);
					layerid = tablemasterInfo.layerid;
					MapmasterInfo mapmasterInfo = mapmasterInfoService.findById(tablemasterInfo.mapmasterinfoid);
					long mapid = mapmasterInfo.mapid;
					LayerInfo layerInfo = mapDB.getLayerInfo(layerid);
					try {
						// システムテーブルの場合はpositionを無視した一覧を返す
						if(layerInfo == null) {
							// データ数をカウントしてlimitを更新する
							total = new Integer(tableService.getCount(tablemasterInfo.tablename).toString());
							if(total - (limit + offset) < 0)
								limit = total - offset;
							// limitが負の値ならlimitを0にする
							if(limit < 0)
								limit = 0;

							// 昇順降順
							String orderBy = desc ? "desc" : "asc";
							List<BeanMap> tableInfos = tableService.selectAll(tablemasterInfo.tablename, orderBy, sort, offset, limit, null);

							JSONObject featureData = new JSONObject();
							// 属性IDの一覧
							JSONArray attrids = new JSONArray();
							// 列名の一覧
							JSONArray th_names = new JSONArray();
							// 地物の配列
							JSONArray values = new JSONArray();

							for(Object column : columnList){
								Class<?> clazz = column.getClass();
								Field field = ReflectionUtils.findField(clazz, "attrid");
								ReflectionUtils.makeAccessible(field);
								String attrid = field.get(column).toString();
								attrids.add(attrid);

								field = ReflectionUtils.findField(clazz, "name");
								ReflectionUtils.makeAccessible(field);
								String name = field.get(column).toString();
								th_names.add(name);
							}

							for(BeanMap tableInfo : tableInfos) {
								JSONArray value = new JSONArray();
								for(Object column : columnList) {
									for(Entry<String, Object> entry : tableInfo.entrySet()) {
										Class<?> clazz = column.getClass();
										Field field = ReflectionUtils.findField(clazz, "attrid");
										ReflectionUtils.makeAccessible(field);
										String attrid = field.get(column).toString();
										if(attrid.equals(entry.getKey())) {
											if(entry.getValue() != null)
												value.add(entry.getValue().toString());
											else
												value.add("");
										}
									}
								}
								values.add(value);
							}
							featureData.put("attrid", attrids);
							featureData.put("th_name", th_names);
							featureData.put("values", values);
							results.add(featureData);
						}
						// システムテーブルではない場合
						else {
							Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
							vecLayerInfo.add(layerInfo);
							double[] center = new double[2];
							// パラメータposition から緯度経度を取得 (例:POINT(134.09827571425873 33.85527479996466))
							if(!position.isEmpty()) {
								String longitudeStr = position.substring(position.indexOf("(")+1, position.indexOf(" "));
								String latitudeStr = position.substring(position.indexOf(" ")+1, position.indexOf(")"));
								double longitude = Double.parseDouble(longitudeStr);
								double latitude = Double.parseDouble(latitudeStr);
								center[0] = longitude;
								center[1] = latitude;
							}
							else
								center = null;
							// sortが空文字(=パラメータがない)場合は、FeatureDB.searchFeatureBboxの引数sortAttrIdにnullを渡す必要がある
							if(sort == null || sort.isEmpty())
								sort = null;

							// フィーチャリストを取得する
							FeatureResultList resultList = FeatureDB.searchFeatureBbox(session, mapid, vecLayerInfo, null, null, "", limit, offset, false, FeatureDB.GEOM_TYPE_CENTER, sort, desc, center, false, null);
							total = (int) resultList.total;
							JSONObject featureData = new JSONObject();
							// 属性IDの一覧
							//JSONArray attrids = new JSONArray();
							// 列名の一覧
							//JSONArray th_names = new JSONArray();
							// 地物の配列
							JSONArray values = new JSONArray();
							// 項目情報の配列
							JSONArray attrInfos = new JSONArray();
							for(Object column : columnList) {
								// 項目情報のオブジェクト
								JSONObject obj = new JSONObject();
								Class<?> clazz = column.getClass();
								Field field = ReflectionUtils.findField(clazz, "attrid");
								ReflectionUtils.makeAccessible(field);
								String attrid = field.get(column).toString();
								//attrids.add(attrid);

								AttrInfo attr = layerInfo.getAttrInfo(attrid);
								obj.put("attrId", attrid);
								// データ型が選択やチェックボックスでなければ空文字を入れる(mapDB.getLayerInfoでdataExpの値が初期化されていない不具合がある模様)
								if(!(attr.dataType == AttrInfo.DATATYPE_SELECT || attr.dataType == AttrInfo.DATATYPE_CHECKBOX))
									obj.put("dataExp", "");
								else
									obj.put("dataExp", attr.dataExp);
								obj.put("dataType", attr.dataType);
								obj.put("name", attr.name);
								obj.put("nullable", attr.nullable);
								/*field = ReflectionUtils.findField(clazz, "editable");
								ReflectionUtils.makeAccessible(field);
								obj.put("editable", field.get(column));*/
								attrInfos.add(obj);

								/*field = ReflectionUtils.findField(clazz, "name");
								ReflectionUtils.makeAccessible(field);
								String name = field.get(column).toString();
								th_names.add(name)*/;
							}
							/*attrids.add("gid");
							th_names.add("gid");*/
							JSONObject obj = new JSONObject();
							obj.put("attrId", "gid");
							obj.put("dataExp", "");
							obj.put("dataType", "");
							obj.put("name", "gid");
							obj.put("nullable", "");
							attrInfos.add(obj);

							// valuesのJSONArrayを作成
							Iterator<FeatureResult> iterator = resultList.getResultIteratable().iterator();
							while(iterator.hasNext()) {
								FeatureResult featureResult = iterator.next();
								JSONArray value = new JSONArray();
								for(Object column : columnList){
									Class<?> clazz = column.getClass();
									Field field = ReflectionUtils.findField(clazz, "attrid");
									ReflectionUtils.makeAccessible(field);
									String attrid = field.get(column).toString();
									value.add(featureResult.getAttrResult(attrid).getAttrValue());
								}
								value.add(featureResult.featureId);
								values.add(value);
							}
							featureData.put("attrInfos", attrInfos);
							// attridとth_nameはattrInfosに統合した。2018/04/27
							/*featureData.put("attrid", attrids);
							featureData.put("th_name", th_names);*/
							featureData.put("values", values);
							results.add(featureData);
						}
					} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
						return Response.sendJSONError(lang.__("position is incorrect.")+" position="+position, HttpServletResponse.SC_BAD_REQUEST).execute(response);
					} catch (Exception e) {
						return Response.sendJSONError(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST).execute(response);
					}
					result.put("addable", menutableInfo.addable);
					result.put("deletable", menutableInfo.deletable);
					if(layerInfo != null)
						result.put("geomType", layerInfo.getGeometryType());
					else
						result.put("geomType", "");
				}
			}
			result.put("total", total);
			result.put("taskmenuinfoid", taskmenuinfoid);
			result.put("layerid", layerid);
			result.put("results", results);
			return responseJSONObject(result);
		}

		return null;
	}
}
