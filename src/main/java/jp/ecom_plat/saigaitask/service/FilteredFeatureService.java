/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.dto.FilterDto;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;

/**
 * フィルタリングサービスクラス
 */
@org.springframework.stereotype.Service
public class FilteredFeatureService {

	/** サービスクラス */
	@Resource
	protected MapService mapService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;
	@Resource
	protected TableService tableService;

	/**
	 * フィルタリングされた属性値CSVを返す。
	 * @param trackdataid 記録ID
	 * @param menuInfo メニュー情報
	 * @param mtbl テーブル情報
	 * @return 属性値CSV
	 */
	public StringBuffer getFilteredFeatureContext(Long trackdataid, MenuInfo menuInfo, MenutableInfo mtbl)
	{
		StringBuffer content = new StringBuffer();
		List<BeanMap> result = null;
		if (mtbl != null) {
			List<TablelistcolumnInfo> colinfoItems = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
			
			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, trackdataid);
			//eコミマップのレイヤ：trackdataidは無い
			if (ttbl != null) {
			if (ttbl.layerid != null && !ttbl.layerid.isEmpty()) {
				result = tableService.selectAll(ttbl.tablename);
			//リストのみのレイヤ
			}else{
				result = tableService.selectByTrackdataid(ttbl.tablename, trackdataid);
			}
			//キーの取得
			String key = null;
			if (!StringUtil.isEmpty(ttbl.layerid)) {
				LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(ttbl.layerid);
				key = LayerInfo.TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType) ? "_orgid" : "gid";
			}
			else key = "id";
			
			//フィルタリング対応
			List<String> ids = new ArrayList<String>();
			// idsを取得して、StringのListに加工する
			//eコミマップのレイヤ
			if (ttbl.layerid != null && !ttbl.layerid.isEmpty()) {
				// FIXME: リクエストパラメータから検索条件を指定できるようにする？
				FilterDto filterDto = mapService.filter(null, menuInfo, null);
				if(filterDto!=null) {
					List<Long> filterfeatureIds = filterDto.getFilteredFeatureIds();
					if(filterfeatureIds!=null) {
						for(Long filterfeatureId : filterfeatureIds) {
							ids.add(String.valueOf(filterfeatureId));
						}
					}
				}
			}
			
			// フィルタリングidsから各レコードを削除する
			if(ids.size()>=0){
				List<BeanMap> res = new ArrayList<BeanMap>();
				for(BeanMap map : result){
					String recordId = String.valueOf(map.get(key));
					if(ids.indexOf(recordId)!=-1) {
						//key（id）が合致したら追加
						res.add(map);
					}
				}
				result = res;//結果の入れ替え
			}
			
			
			//属性項目を絞り込み
			for(BeanMap map : result){
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo colinfo = colinfoItems.get(i);
					if (i != 0) content.append(",");
					if (map.get(colinfo.attrid) != null)
						content.append(map.get(colinfo.attrid).toString());
				}
				content.append("\n");
			}
			}
		}
		return content;
	}

	/**
	 * 前フィーチャの属性値CSVを返す。
	 * @param trackdataid 記録ID
	 * @param mtbl テーブル情報
	 * @return 属性値CSV
	 */
	public StringBuffer getAllFeatureContext(Long trackdataid, MenutableInfo mtbl)
	{
		StringBuffer content = new StringBuffer();
		List<BeanMap> result = null;
		if (mtbl != null) {
			List<TablelistcolumnInfo> colinfoItems = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
			
			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, trackdataid);
			//eコミマップのレイヤ：trackdataidは無い
			if (ttbl != null) {
			if (ttbl.layerid != null && !ttbl.layerid.isEmpty()) {
				result = tableService.selectAll(ttbl.tablename);
			//リストのみのレイヤ
			}else{
				result = tableService.selectByTrackdataid(ttbl.tablename, trackdataid);
			}
			
			// 選択形式、編集できない画面での表示用値Map
			Map<String, Map<String,String>> selectValView = new TreeMap<String, Map<String,String>>();
			// 選択形式Map
			Map<String, String[]> selectStr = new TreeMap<String, String[]>();
			// 選択形式値Map
			Map<String, String[]> selectVal = new TreeMap<String, String[]>();
			//eコミマップ外のレイヤ
			if (StringUtil.isEmpty(ttbl.layerid)) {
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo cinfo = colinfoItems.get(i);
					String colName = cinfo.attrid;//カラム名取得
					if(TableService.isInfoColumn(colName)){
						Map<String, String> selMap = tableService.setRelTable("info", colName, selectStr, selectVal);
						//表示用のMap格納
						selectValView.put(colName, selMap);
					}
					else if(TableService.isMasterColumn(colName)){
						//指定したmasterテーブルがあれば、そのnameカラムをSelectの選択肢として追加
						Map<String, String> selMap = tableService.setRelTable("master", colName, selectStr, selectVal);
						//表示用のMap格納
						selectValView.put(colName, selMap);
					}
				}
			}
			
			//属性項目を絞り込み
			for(BeanMap map : result){
				for (int i = 0; i < colinfoItems.size(); i++) {
					TablelistcolumnInfo colinfo = colinfoItems.get(i);
					if (i != 0) content.append(",");
					if (map.get(colinfo.attrid) != null) {
						String val = map.get(colinfo.attrid).toString();
						if (selectValView.containsKey(colinfo.attrid)) {
							Map<String,String> vmap = selectValView.get(colinfo.attrid);
							val = vmap.get(val);
						}
						if (val == null) val = "";
						content.append(val);
					}
				}
				content.append("\n");
			}
			}
		}
		return content;
	}
}
