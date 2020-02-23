package jp.ecom_plat.saigaitask.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.exception.EcommapConflictException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.AutocompleteInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculateInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculatecolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.AutocompleteDataService;
import jp.ecom_plat.saigaitask.service.db.AutocompleteInfoService;
import jp.ecom_plat.saigaitask.service.db.HistorytableInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculateInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculatecolumnInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.SAStrutsUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * システムテーブルのレコードや、レイヤの地物を登録/更新/削除するサービスです。
 * ListAction および SlimerAction を統合し、ここで定義しなおしました。
 */
@org.springframework.stereotype.Service
public class TableFeatureService extends BaseService {

	/** Logger */
	protected Logger logger = Logger.getLogger(TableFeatureService.class);

	/** ログイン情報 */
	@Resource public LoginDataDto loginDataDto;

	@Resource protected MapService mapService;
	@Resource protected AutocompleteInfoService autocompleteInfoService;
	@Resource protected AutocompleteDataService autocompleteDataService;
	@Resource protected HistorytableInfoService historytableInfoService;
	@Resource protected HistoryTableService historyTableService;
	@Resource protected TrackdataidAttrService trackdataidAttrService;
	@Resource protected EdituserAttrService edituserAttrService;
	@Resource protected TablecalculateInfoService tablecalculateInfoService;
	@Resource protected TablecalculatecolumnInfoService tablecalculatecolumnInfoService;
	@Resource protected EvalService evalService;
	@Resource protected TableService tableService;
	@Resource protected TriggerAlertService triggerAlertService;

	/**
	 *
	 * @param master テーブルマスタ
	 * @param ttbl 記録テーブル情報
	 * @param features 更新データ（登録の場合は fid=0 とする）
	 * @param isAddColumn 追記属性フラグ
	 * @param delfids 削除fid
	 * @throws EcommapConflictException
	 * @throws Exception
	 */
	public long execute(TablemasterInfo master, TracktableInfo ttbl,
			Map<Long, HashMap<String, String>> features, HashMap<String, Boolean> isAddColumn, List<Long> delfids) throws EcommapConflictException, Exception {

		long fid = 0;
		Long orgid = null;

		Timestamp time = new Timestamp(TimeUtil.newDate().getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String now = sdf.format(new Date());


		// システムテーブルの準備
		String table = ttbl!=null ? ttbl.tablename : master.tablename;
		String layerid = ttbl!=null ? ttbl.layerid : master.layerid;

		// 履歴テーブル
		HistorytableInfo htbl = null;

		// レイヤの準備
		MapDB mapDB = MapDB.getMapDB();
		UserInfo userInfo = null;
		LayerInfo layerInfo = null;
		if(StringUtil.isNotEmpty(layerid)) {
			layerInfo = mapDB.getLayerInfo(layerid);
			table = layerid;
			userInfo = mapDB.getAuthIdUserInfo(loginDataDto.getEcomUser());
			if(userInfo==null) {
				throw new ServiceException(lang.__("e-community map user not found."));
			}
			// 属性チェック
			trackdataidAttrService.checkIfExists(layerInfo);
			edituserAttrService.checkIfExists(layerInfo);
			// 履歴レイヤの取得
			if(ttbl != null){
				htbl = historyTableService.findOrCreateByTracktableInfo(ttbl, layerInfo);
			}
		}

		// 地物のループ
		for(Map.Entry<Long, HashMap<String, String>> entry : features.entrySet()) {
			fid = entry.getKey().longValue();
			HashMap<String, String> attribute = entry.getValue();

			boolean isCreate = fid==0l;
			boolean isDelete = (delfids!=null && delfids.contains(fid));
			boolean isUpdate = !(isCreate|isDelete);

			//
			// システムテーブルとeコミレイヤの共通前処理
			//
			// 登録/更新の場合、オートコンプリートの保存とトリガーを実行する
			if(!isDelete) {
				for(Map.Entry<String, String> attrEntry : attribute.entrySet()) {
					String attrid = attrEntry.getKey();
					String value = attrEntry.getValue();

					//オートコンプリート
					if (StringUtil.isNotEmpty(value)) {
						AutocompleteInfo autoInfo = autocompleteInfoService.get(loginDataDto.getLocalgovinfoid(), loginDataDto.getGroupid(), master.id, attrid);
						autocompleteDataService.set(autoInfo.id, value);
					}

					// 更新する属性で、アラートをトリガーする
					triggerAlertService.trigger(master.id, attrid, value);
				}
			}

			//
			// システムテーブル、eコミレイヤの個別処理
			//
			// eコミマップのテーブル
			if(StringUtil.isNotEmpty(layerid)) {
				// 削除処理
				if (isDelete) {
					// 削除実行ユーザを編集者として記録
					mapService.deleteFeature(userInfo, layerInfo, fid);
					continue;
				}

				// trackdataid 属性の更新チェック
				// trackdataid 属性の指定がなければログイン中の trackdataid を自動付与
				if(attribute.containsKey(TrackdataidAttrService.TRACKDATA_ATTR_ID)==false) {
					attribute.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, String.valueOf(loginDataDto.getTrackdataid()));
				}

				//属性更新日時をセット
				if (!StringUtil.isEmpty(master.updatecolumn))
					attribute.put(master.updatecolumn, now);

				// 履歴の場合は時間を指定する
				if(LayerInfo.TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
					attribute.put("time_from", time.toString());
				}

				// 編集者も更新項目として追加
				if(StringUtil.isEmpty(attribute.get(EdituserAttrService.EDITUSER_ATTR_ID))) {
					if(EdituserAttrService.hasEdituserAttr(layerInfo)) {
						attribute.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
					}
				}


				// WKT の取得
				boolean updateGeometry = attribute.containsKey("theGeom");
				String wkt = attribute.get("theGeom");
				if(updateGeometry) attribute.remove("theGeom");
				if (StringUtil.isEmpty(wkt)) updateGeometry = false;
				//住所から座標を取得 は一時的に無効
				//if (!StringUtil.isEmpty(master.addresscolumn) && master.addresscolumn.equals(ids[1]))
				//	wkt = getWktFromAddress(data.value);

				// 更新処理
				if (isUpdate) {

					// 更新用 Attribute はＤＢから取得したものを元にして、リクエストパラメータで上書きする
					boolean attrGrouped = true;
					int resultGeomType = FeatureDB.GEOM_TYPE_GEOM; // ジオメトリを取得する必要がないが、取得しない方法がないので WKT を取得する
					FeatureResult featureResult = FeatureDB.getFeatureContent(layerInfo, fid, attrGrouped, resultGeomType, /*bbox*/null, /*timeParam*/null);
					HashMap<String, String> updateAttribute = ExMapDB.getAttributes(featureResult);
					// 入力値で上書き
					for (Map.Entry<String, String> attrEntry : attribute.entrySet()) {
						String attrId = attrEntry.getKey();
						String attrVal = attrEntry.getValue();
						String oldval = updateAttribute.get(attrId);
						// 追記処理
						if(isAddColumn!=null && SAStrutsUtil.defalutValue(isAddColumn.get(attrId), false)) {
							updateAttribute.put(attrId, (StringUtil.isNotEmpty(oldval) ? oldval : "")+attrVal);
						}
						else {
							updateAttribute.put(attrId, attrVal);
						}
					}

					//座標値 ありの更新の場合
					if(updateGeometry) {
						FeatureDB.updateFeature(userInfo, layerid, fid, wkt, updateAttribute, /*timestamp*/null);
					}
					// 属性のみの更新の場合
					else {
						// 属性の更新
						// 履歴レイヤ対応のため、tableService.update から eコミマップの関数を使うように変更
						//tableService.update(ids[0], ids[1], ids[2], fid, value);
						FeatureDB.updateFeatureAttribute(userInfo, layerid, fid, updateAttribute, /*timestamp*/null);
					}

					logger.debug("datas : UPDATE ecommap");

					// 履歴レイヤの場合は、更新すると gid が変化するので、更新時に指定した time_from から gid を取得する。
					if(LayerInfo.TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
						orgid = fid;
						fid = ExMapDB.getHistoryFeatureId(layerid, fid, new Date[]{time});
					}

					/*
					 * リストの更新の時は座標の更新は行わない
					 *
					//住所から座標を取得
					String wkt = null;
					if (!StringUtil.isEmpty(master.addresscolumn) && master.addresscolumn.equals(ids[1]))
						wkt = getWktFromAddress(data.value);
					//座標取得
					if(!StringUtil.isEmpty(master.coordinatecolumn) && master.coordinatecolumn.equals(ids[1]) && wkt!=null){
						//update時に住所から座標を取得すると、本来の正しい位置からずれてしまう可能性があるので、
						//既存の値があった場合はupdateしない
						String curVal = tableService.getEcomColValue(ids[0], ids[1], ids[3]);
						if(curVal != null && curVal.length()>0 ){
							//リストからの位置取得は、リストからの位置指定ができるまでは、住所からの座標取得の場合のみ
							try{
								Geometry geom = GeometryUtils.getGeometryFromWKT(wkt);
								Point pt = geom.getCentroid();
								String xy = "("+String.valueOf(pt.getX())+","+ String.valueOf(pt.getY())+")";
								tableService.update(ids[0], ids[1], ids[2], Long.parseLong(ids[3]), xy);
							}catch (Exception e){
								logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
								logger.error("", e);
								throw new ServiceException(e);
							}
						}
					}
					*/
				}
				else if(isCreate) {

					fid = mapService.insertFeature(userInfo.authId, layerid, wkt, attribute);

					//座標取得
					/*if(!StringUtil.isEmpty(master.coordinatecolumn) && wkt!=null){
						//リストからの位置取得は、リストからの位置指定ができるまでは、住所からの座標取得の場合のみ
						try{
							Geometry geom = GeometryUtils.getGeometryFromWKT(wkt);
							Point pt = geom.getCentroid();
							String xy = "("+String.valueOf(pt.getX())+","+ String.valueOf(pt.getY())+")";
							tableService.update(table, master.coordinatecolumn, ids[2], fid, xy);
						}catch (Exception e){
							logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
							logger.error("", e);
							throw new ServiceException(e);
						}
					}*/
				}

				//
				// eコミレイヤの後処理
				//

				//計算
				// 履歴レイヤの場合は _orgid を利用するのではなく、gid を指定して履歴編集という形で計算結果を保存する
				List<TablecalculateInfo> calcList = tablecalculateInfoService.findByTablemasterInfoId(master.id);
				for (TablecalculateInfo calc : calcList) {
					Object val = evalService.eval(calc, layerid, table, fid);
					TablecalculatecolumnInfo ccol = tablecalculatecolumnInfoService.findById(calc.tablecalculatecolumninfoid);
					tableService.update(table, ccol.columnname, "gid", fid, val!=null ? val.toString() : null);
				}

			}
			// その他のテーブル
			else {
				//テーブル名からEntityのクラスを取得
				Class<?> entityClass = tableService.getEntity(table);
				Object entityObj = ClassUtil.newInstance(entityClass);
				if(isUpdate|isDelete) entityObj = tableService.selectById(entityClass, table, fid);

				// 削除処理
				if (isDelete) {
					//if (ids[3].equals(value))
					tableService.deleteByEntity(entityObj);
					logger.debug("datas : DELETE NOT ecommap");
					continue;
				}

				// 属性値をセット
				for (Map.Entry<String, String> attrEntry : attribute.entrySet()) {
					String attrId = attrEntry.getKey();
					String attrVal = attrEntry.getValue();
					tableService.setFieldValue(entityClass, entityObj, attrId, attrVal);
					boolean add = isAddColumn!=null && isAddColumn.get(attrId);
					if(add) {
						Field field = entityClass.getField(attrId);
						String oldval = (String) FieldUtil.get(field, entityObj);
						tableService.setFieldValue(entityClass, entityObj, attrId, (StringUtil.isNotEmpty(oldval) ? oldval : "")+attrVal);
					}
					else {
						tableService.setFieldValue(entityClass, entityObj, attrId, attrVal);
					}
				}

				//属性更新日時をセット
				if (!StringUtil.isEmpty(master.updatecolumn))
					tableService.setFieldValue(entityClass, entityObj, master.updatecolumn, now);

				//trackdataidをセット
				tableService.setFieldValue(entityClass, entityObj, "trackdataid", String.valueOf(loginDataDto.getTrackdataid()));

				// 更新処理
				if (isUpdate) {
					tableService.updateByEntity(entityObj);
					logger.debug("datas : UPDATE NOT ecommap");
				}
				// 登録処理
				else if(isCreate) {
					tableService.insertByEntity(entityObj);

					//ID取得
					Field f = ClassUtil.getField(entityClass, "id");
					if (f != null) {
						Object oid = FieldUtil.get(f, entityObj);
						if (oid != null){
							fid = ((Long)oid).longValue();
						}
					}
				}
			}

			//
			// システムテーブル、eコミレイヤの地物の共通後処理
			//
			if(isCreate | isUpdate) {
				//履歴テーブルに記録
				if(htbl != null)
					historyTableService.log(ttbl, htbl, fid, layerInfo);
			}
		}
		if (orgid != null)
			return orgid;
		return fid;
	}
}
