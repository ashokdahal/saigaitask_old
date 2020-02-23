/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.FilterDto;
import jp.ecom_plat.saigaitask.entity.db.FilterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.form.page.FilterForm;
import jp.ecom_plat.saigaitask.service.db.FilterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;

/**
 * フィルターのアクションクラス
 * 複数フィルター対応
 * spring checked take 5/14
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class FilterAction extends AbstractAction {

	protected FilterForm filterForm;

	// Service
	@Resource protected MenuInfoService menuInfoService;
	@Resource protected FilterInfoService filterInfoService;
	@Resource protected MapService mapService;

	/**
	 * メニュー情報IDに紐づくフィルタ情報を取得する.
	 * @return フィルタ情報 JSONArray
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/filter/list")
	@ResponseBody
	public String list(Map<String,Object>model, @Valid @ModelAttribute FilterForm filterForm) {
		try {
			MapDB mapDB = MapDB.getMapDB();

			// eコミユーザ情報を取得(検索条件の閲覧権限を要チェックのため)
			String authId = loginDataDto.getEcomUser();
			if(StringUtil.isEmpty(authId)) throw new ServiceException(lang.__("e-Com user info has not been set."));
			UserInfo userInfo = mapDB.getAuthIdUserInfo(authId);
			if(userInfo==null) throw new ServiceException(lang.__("Unable to get e-Com user info.")+authId);

			// フィルタ情報をJSONにしてレスポンス
			List<FilterInfo> filterInfos =  filterInfoService.findByMenuid(filterForm.menuid);
			Map<String, LayerInfo> cache = new HashMap<String, LayerInfo>();
			JSONArray array = new JSONArray();
			// フィルター解除用で無しを追加
			if(0<filterInfos.size()) {
				FilterInfo filterInfo = mapService.createNothingFilterInfo(filterForm.menuid);
				JSONObject json = filterInfoService.toJSONObject(filterInfo);
				JSONObject conditionValue = mapService.getConditionValue(filterInfo);
				json.put("conditionValue", conditionValue);
				array.put(json);
			}
			for(FilterInfo filterInfo : filterInfos) {
				if(filterInfo.valid) {
					JSONObject json = filterInfoService.toJSONObject(filterInfo);
					JSONObject conditionValue = mapService.getConditionValue(filterInfo);
					json.put("conditionValue", conditionValue);
					// レイヤ情報
					{
						// 検索対象のレイヤ情報を取得
						List<String> layerIds = new ArrayList<String>();
						if(conditionValue.has("layerId")) {
							String layerId = conditionValue.getString("layerId");
							layerIds.add(layerId);
						}
						// 空間検索範囲のレイヤ情報を取得
						if(conditionValue.has("spatiallayer")) {
							JSONArray spatiallayer = conditionValue.getJSONArray("spatiallayer");
							for(int idx=0; idx<spatiallayer.length(); idx++) {
								JSONObject cond = spatiallayer.getJSONObject(idx);
								if(cond.has("layer")) {
									String layer = cond.getString("layer");
									layerIds.add(layer);
								}
							}
						}
						// JSONObject に layerInfos を設定
						JSONObject layerInfos = new JSONObject();
						for(String layerId : layerIds) {
							LayerInfo layerInfo = cache.get(layerId);
							if(layerInfo==null) {
								layerInfo = mapDB.getLayerInfo(layerId);
								cache.put(layerId, layerInfo);
							}
							layerInfos.put(layerInfo.layerId, layerInfo.toJSON());
						}
						json.put("layerInfos", layerInfos);
					}
					array.put(json);
				}
			}

			// 脆弱性対応(Ratproxy)
			// JSONレスポンスにJSONArrayを返すと安全ではないJSONと認識されてしまう。
			// なので、JSONレスポンスは必ず JSONObject で返すようにしなければならない
			//ResponseUtil.write(array.toString(),"application/json","UTF-8");
			//return array.toString();
			
			// 結果JSONを生成
			JSONObject result = new JSONObject();
			//result.put("success", success);
			//result.put("msg", msg);
			result.put("filterInfos", array);
			return result.toString();
		} catch(Exception e) {
			return onfailJSONResponse(e).toString();
		}
	}

	/**
	 * 指定された条件で再度フィルターする
	 * @return 検索結果JSONObject
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/filter/search")
	@ResponseBody
	public String search(Map<String,Object>model, @Valid @ModelAttribute FilterForm filterForm) {

		MenuInfo menuInfo = menuInfoService.findById(filterForm.menuid);
		
		//FilterInfo filterInfo = filterInfoService.findById(filterForm.filterid);
		//if(filterInfo==null) throw new ServiceException("フィルタ情報が見つかりません。");

		FilterDto filterDto = mapService.filter(filterForm.conditionValue, menuInfo, filterForm.timeParams());

		try {
			JSONObject json = new JSONObject();
			json.put("total", filterDto.getTotal());
			json.put("filteredFeatureIds", filterDto.getFilteredFeatureIds());
			json.put("conditionValueActual", filterDto.getConditionValueActual());
			//ResponseUtil.write(json.toString(),"application/json","UTF-8");
			return json.toString();
		} catch(Exception e) {
			return onfailJSONResponse(e).toString();
		}
	}
}
