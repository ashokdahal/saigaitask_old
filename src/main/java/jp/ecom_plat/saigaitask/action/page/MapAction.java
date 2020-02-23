/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.FilterDto;
import jp.ecom_plat.saigaitask.dto.MapDto;
import jp.ecom_plat.saigaitask.dto.MapInitDto;
import jp.ecom_plat.saigaitask.dto.MapInitDto.JSONLayerInfo;
import jp.ecom_plat.saigaitask.dto.MapInitDto.MapInitConfig;
import jp.ecom_plat.saigaitask.dto.SlimerDto;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousesearchInfo;
import jp.ecom_plat.saigaitask.entity.db.DecisionsupportInfo;
import jp.ecom_plat.saigaitask.entity.db.MapbaselayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapkmllayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MapreferencelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MenumapInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteolayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.MapForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.GeocoderService;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.ClearinghousesearchInfoService;
import jp.ecom_plat.saigaitask.service.db.DecisionsupportInfoService;
import jp.ecom_plat.saigaitask.service.db.EarthquakegrouplayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerDataService;
import jp.ecom_plat.saigaitask.service.db.MapbaselayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapkmllayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MaplayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MapreferencelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MenumapInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteolayerInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import net.sf.json.JSONNull;

/**
 * An action class that displays a map page.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/page/map")
public class MapAction extends AbstractPageAction {

	protected MapForm mapForm;

	// Service
	@Resource protected MaplayerInfoService maplayerInfoService;
	@Resource protected MapbaselayerInfoService mapbaselayerInfoService;
	@Resource protected MapreferencelayerInfoService mapreferencelayerInfoService;
	@Resource protected MapkmllayerInfoService mapkmllayerInfoService;
	@Resource protected MapService mapService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected LayerService layerService;
	@Resource protected MenumapInfoService menumapInfoService;
	@Resource protected ClearinghouseService clearinghouseService;
	@Resource protected ClearinghousesearchInfoService clearinghousesearchInfoService;
	@Resource protected TrackdataidAttrService trackdataidAttrService;
	@Resource protected MeteolayerInfoService meteolayerInfoService;
	@Resource protected EarthquakegrouplayerDataService earthquakegrouplayerDataService;
	@Resource protected EarthquakelayerDataService earthquakelayerDataService;
	@Resource protected MeteoricEarthQuakeService meteoricEarthQuakeService;
	@Resource protected DecisionsupportInfoService decisionsupportInfoService;
	@Resource protected GeocoderService geocoderService;


	// Dto
	/** 地図画面用Dto */
	@Resource public MapDto mapDto;
	/** 一括変更用Dto */
	@Resource public SlimerDto slimerDto;
	/** フィルタDto */
	@Resource public FilterDto filterDto;

	/**「情報の登録」ボタン表示フラグ */
	public Boolean addable;

	/** クリアリングハウスの定期的に検索するときに除くメタデータIDリスト */
	public JSONObject excludeMetadataIds = null;

	/** 意志決定支援用更新時間 */
	public String decisionUpdateTime = null;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("mapDto", mapDto);
		model.put("slimerDto", slimerDto);
		model.put("filterDto", filterDto);
		model.put("addable", addable);
		model.put("excludeMetadataIds", excludeMetadataIds);
		model.put("decisionUpdateTime", decisionUpdateTime);
		model.put("mapForm", mapForm);
	}

	/**
	 * Show map page
	 * @return Forward destination
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute MapForm mapForm, BindingResult bindingResult) {
		this.mapForm = mapForm;
		getContentData(model, mapForm, bindingResult);
    	return "page/map/index";
	}

	/**
	 *Show map page
	 * @return Forward destination
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/content")
	public String content(Map<String,Object>model, @Valid @ModelAttribute MapForm mapForm, BindingResult bindingResult) {
		this.mapForm = mapForm;
		getContentData(model, mapForm, bindingResult);
		pageDto.setContentOnly(true);
		return "page/map/contentIndex";
	}

	/**
	 * Get map data
	 */
	public void getContentData(Map<String,Object>model, MapForm mapForm, BindingResult bindingResult) {
		initPage("map", mapForm);
		pageDto.setEnableFullscreen(true);
		try {
			TrackData trackData = pageDto.getTrackData();
			boolean isTraining = trackData!=null && trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
			JSONObject ecommapInfoJSON = getEcommapInfo(isTraining, bindingResult);
			if(ecommapInfoJSON!=null) {
				mapDto.setEcommapInfoJSON(ecommapInfoJSON.toString());
			}
			if(filterDto!=null) {
				if(StringUtil.isNotEmpty(filterDto.getLayerId())) {
					mapDto.setFilterlayer(filterDto.getLayerId());
				}
			}
			if(loginDataDto.getLocalgovInfo().coordinatedecimal!=null) {
				mapDto.setCoordinateDecimal(loginDataDto.getLocalgovInfo().coordinatedecimal);
			}
			// 地図画面で除外するメタデータIDのリストを取得
			excludeMetadataIds = clearinghouseService.getExcludeMetadataIds(pageDto.getMenuInfo().id, true, false, isTraining);

			// リクエストフォームをJSONオブジェクトに変換
			pageDto.setFormJSONFromForm(mapForm);
			// ユーザが前回の地図画面にて最後に表示していた位置を取得
			if(loginDataDto.getLocation()!=null) {
				net.sf.json.JSONObject location = loginDataDto.getLocation();
				String[] copyKeys = new String[]{"center", "zoom"};
				try{
					for(String key : copyKeys) {
						Object val = null;
						val= location.get(key);
						if(val instanceof JSONNull) {
							val = null;
						}

						pageDto.getFormJSON().put(key, val);
					}
				} catch(Exception e) {
					logger.debug(e);
				}
			}

			// addressパラメータがあれば逆ジオした結果を初期表示の中心位置にする
			if(mapForm.address != null) {
				String address = mapForm.address;
				String center = geocoderService.geocode(address);
				pageDto.getFormJSON().put("center", center);
			}
			// クリアリングハウス検索、ポップアップ通知の検索する間隔をDBから設定する
			//DBからパラメータ、検索範囲を取得する。
			ClearinghousesearchInfo search = clearinghousesearchInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			if (search != null) {
				if(search.interval!=null) {
					pageDto.setMetadataInterval(search.interval * 1000);
				}
			}

			//一括変更
			if(StringUtil.isNotEmpty(mapDto.getFilterlayer())) {
				if(slimerDto.indexOf(mapDto.getFilterlayer())!=-1) {
					if(0<filterDto.getFilteredFeatureIds().size()) {
						slimerDto.setTargetIds(mapDto.getFilterlayer(), filterDto.getFilteredFeatureIds());
					}
				}
			}
		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display map."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);
	}
	/**
	 * Returns JSON object of e-commap initialization information to HTTP response.
	 * @return null(Not forward)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/ecommapInfo")
	@ResponseBody
	public String ecommapInfo(Map<String,Object>model, @Valid @ModelAttribute MapForm mapForm, BindingResult bindingResult, HttpServletResponse response) {
		this.mapForm = mapForm;
		initPage("map", mapForm);
		TrackData trackData = pageDto.getTrackData();
		boolean isTraining = trackData!=null && trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
		JSONObject ecommapInfoJSON = getEcommapInfo(isTraining, bindingResult);
		try {
			return ecommapInfoJSON.toString();
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}

		//return null;
	}

	/**
	 * Get the map initialization data from the logged record ID.
	 * @param isTraining Training mode flag
	 * @return E-commap information of JSON object
	 */
	protected JSONObject getEcommapInfo(boolean isTraining, BindingResult bindingResult) {

		long logstarttime = System.currentTimeMillis();

		// ログイン中の記録データ
		final TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());

		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

		// 複合災害対応により、記録データ内にある地図情報IDを使う
		TrackmapInfo trackmapInfo = null;
		long mapId = 0;
		if(trackData!=null) {
			if(trackData.trackmapinfoid==null) throw new ServiceException(lang.__("Disaster map info is not found."));
			trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackData.trackmapinfoid);
			if (trackmapInfo != null) mapId = trackmapInfo.mapid;
		}
		else {//平時モード
			if (mapmasterInfo != null)
				mapId = mapmasterInfo.mapid;
			// 平時モードにもかかわらず、地図マスタ情報がない
			else return null;
		}
		if(mapForm.mapid!=null) {
			mapId = mapForm.mapid;
		}
		mapForm.mapid = mapId;

		// Map<レイヤID, ルール>
		Map<String, String> ruleMap = loginDataDto.getRuleMap();
		if(ruleMap==null) {
			loginDataDto.setRuleMap(new HashMap<>());
			ruleMap = loginDataDto.getRuleMap();
		}

		// SLDルールによる凡例の表示切替のURL再現
		// URLパラメータがあればセッションの状態を上書き
		if(StringUtil.isNotEmpty(mapForm.rule)){
			for(String layerRule : mapForm.rule.split(",")) {
				String[] ruleElems = layerRule.split(":");
				if(0<ruleElems.length) {
					// 最初の要素がレイヤID
					String ruleLayerId = ruleElems[0];
					ruleMap.put(ruleLayerId, layerRule);
				}
			}
		}

		// レイヤIDを TablemasterInfo.layerid から TracktableInfo.layerId に変換するマップを取得
		Map<String, String> layerIdConvertMap = new HashMap<String, String>();
		if(trackmapInfo!=null) {
			layerIdConvertMap = mapService.getLayerIdConvertMap(trackmapInfo.id);
		}

		// 地図に追加されているすべてのレイヤの初期化データを取得する
		MapInitDto mapInitDto = new MapInitDto();
		mapService.loadLayerInfos(mapId, null, mapInitDto, null, pageDto.getMenuInfo().id, isTraining);

		if(mapInitDto.layoutInfo!=null) {
			// グループの設定をDBから取得
			String extent = null;
			if(loginDataDto.getGroupInfo()!=null) extent = loginDataDto.getGroupInfo().extent;
			if(loginDataDto.getUnitInfo()!=null)  extent = loginDataDto.getUnitInfo().extent;
			if (StringUtil.isNotEmpty(extent)) {
				try {
					Geometry geom = new WKTReader().read(extent);
					Envelope env = geom.getEnvelopeInternal();
					mapInitDto.layoutInfo.mapExtent = new double[]{env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY()};
				} catch (ParseException e) {
					logger.error(loginDataDto.logInfo()+"\n" + lang.__("Unable to set map range. WKT might be incorrect."), e);
				}
			}
			// 解像度を設定
			Double resolution = null;
			if(loginDataDto.getGroupInfo()!=null) resolution = loginDataDto.getGroupInfo().resolution;
			if(loginDataDto.getUnitInfo()!=null)  resolution = loginDataDto.getUnitInfo().resolution;
			if(resolution!=null) {
				mapInitDto.layoutInfo.mapResolution = resolution;
			}

			// 地図の設定をDBから取得
			MenumapInfo menumapInfo = menumapInfoService.findByMenuinfoid(pageDto.getMenuInfo().id);
			if(menumapInfo!=null) {
				// 範囲を設定
				if(StringUtil.isNotEmpty(menumapInfo.extent)) {
					try {
						Geometry geom = new WKTReader().read(menumapInfo.extent);
						Envelope env = geom.getEnvelopeInternal();
						mapInitDto.layoutInfo.mapExtent = new double[]{env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY()};
					} catch (ParseException e) {
						logger.error(loginDataDto.logInfo()+"\n" + lang.__("Unable to set map range. WKT might be incorrect."), e);
					}
				}
				// 解像度を設定
				if(menumapInfo.resolution!=null) {
					mapInitDto.layoutInfo.mapResolution = menumapInfo.resolution;
				}
			}

			// 表示制限範囲を設定
			if(mapmasterInfo!=null) {
				if(StringUtil.isNotEmpty(mapmasterInfo.restrictedextent)) {
					try {
						String[] strs = mapmasterInfo.restrictedextent.split(",");
						if(strs.length==4) {
							double minx = Double.parseDouble(strs[0]);
							double maxy = Double.parseDouble(strs[1]);
							double maxx = Double.parseDouble(strs[2]);
							double miny = Double.parseDouble(strs[3]);
							mapInitDto.layoutInfo.restrictedExtent = new double[]{minx, miny, maxx, maxy};
						}
					} catch (Exception e) {
						logger.error(loginDataDto.logInfo()+"\n" + lang.__("Impossible to set display limitation range. ID={0} ,restrictedextent=\"{1}\"", mapmasterInfo.id, mapmasterInfo.restrictedextent), e);
					}
				}
			}
		}

		// ここから危機管理クラウドの設定を反映する
		// 登録情報レイヤ
		List<MaplayerInfo> maplayerInfos = maplayerInfoService.findByMenuid(pageDto.getMenuInfo().id, loginDataDto.getTrackdataid());
		// KMLレイヤ
		List<MapkmllayerInfo> mapkmllayerInfos = mapkmllayerInfoService.findByMenuid(pageDto.getMenuInfo().id);
		// 主題図
		List<MapreferencelayerInfo> mapreferencelayerInfos = mapreferencelayerInfoService.findByMenuid(pageDto.getMenuInfo().id);
		// 気象情報
		List<MeteolayerInfo> meteolayerInfos = meteolayerInfoService.findByMenuid(pageDto.getMenuInfo().id);
		// 背景地図
		List<MapbaselayerInfo> mapbaselayerInfos = mapbaselayerInfoService.findByMenuid(pageDto.getMenuInfo().id);

		// 地図初期化データの設定
		MapInitConfig config = new MapInitConfig();

		// フィルタを適用
		filterDto = mapService.filter(mapForm.conditionValue, mapForm.timeParams());
		if(filterDto!=null) mapForm.conditionValue = filterDto.getConditionValue();
		// filterDto が null だと、JSP EL式で PropertyNotFoundエラーとなるためインスタンスをセットしておく
		if(filterDto==null) { filterDto = new FilterDto(); }

		// JSONLayerInfo のマップ
		Map<String, JSONLayerInfo> jsonLayerInfoMap = mapInitDto.getJSONLayerInfoMap();

		// DBから設定
		{
			// 意思決定支援レイヤ
			if(pageDto.isDecisionSupport()){
				// 意思決定支援用のMaplayerInfos
				List<MaplayerInfo> decision_maplayerInfos = new ArrayList<MaplayerInfo>();
				// 避難所を先頭にする為、layeridを格納する
				String shelterLayerID = "";
				// 1～3次データ用のグループレイヤ
				JSONLayerInfo groupLayerID_1 = null;
				//JSONLayerInfo groupLayerID_2 = null;
				JSONLayerInfo groupLayerID_3 = null;
				// 自治体で持つ意思決定支援レイヤを取得する
				List<DecisionsupportInfo> decisionsupportInfos = decisionsupportInfoService.findByLocalgovinfoId(pageDto.getLocalgovInfo().id);

				for(DecisionsupportInfo decisionsupportInfo : decisionsupportInfos){
					// 各種パラメータが設定されているかチェック
					if(decisionsupportInfoService.isNotEmptyParam(decisionsupportInfo)){
						// テーブルマスターからレイヤ情報を取得
						//TablemasterInfo tablemasterInfo = tablemasterInfoService.findByNotDeletedId(decisionsupportInfo.tablemasterinfoid);
						//if(tablemasterInfo != null){
						if(decisionsupportInfo.tablemasterInfo != null){

							int decisionTypeId = decisionsupportInfo.decisionsupporttypeid;
							// 2次レイヤは非表示するよう指示有 2016/03/24
							if(decisionTypeId >= 6 && decisionTypeId <= 8){
								continue;
							}
							// 災害時なら災害マップから取得する
							if(loginDataDto.getTrackdataid() > 0){
								TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(decisionsupportInfo.tablemasterinfoid, loginDataDto.getTrackdataid());
								if(ttbl != null) decisionsupportInfo.tablemasterInfo.layerid = ttbl.layerid;
							}
							String decisionLayerID = decisionsupportInfoService.findByTrackLayerID(decisionsupportInfo);
							// 避難所レイヤのlayeridを格納
							if(decisionTypeId == 10){
								shelterLayerID = decisionLayerID;
								continue;
							}
							// maplayerinfoを作成して格納する
							MaplayerInfo mLayerInfo = decisionsupportInfoService.dummyMaplayerInfoCreate(pageDto.getMenuInfo().id, decisionsupportInfo.tablemasterInfo, decisionsupportInfo.tablemasterInfo.layerid);
							if(mLayerInfo != null){
								decision_maplayerInfos.add(mLayerInfo);
							}

							if(decisionTypeId == 1 || decisionTypeId == 2){
								// 1次データ用のレイヤのグループレイヤを取得する
								if(groupLayerID_1 == null){
									groupLayerID_1 = searchLayerGroupName(mapInitDto.groupContentsLayerInfos, decisionsupportInfo.tablemasterInfo.layerid);
									// 検索出来ればグループレイヤを追加する
									if(groupLayerID_1 != null){
										//config.layerIds.add(groupLayerID_1.layerId);
										//config.hiddenLayerIds.add(groupLayerID_1.layerId);
										config.expandLayerIds.add(groupLayerID_1.layerId);
									}
								}
							}
							// 停電、断水エリアは被災状況入力等に含まれるレイヤを想定し、1次レイヤ用グループの外にいるはずなので、それらをグループに入れる
							else if(decisionTypeId == 11){
								for(JSONLayerInfo contentLayer : mapInitDto.contentsLayerInfos){
									//if(contentLayer.layerId.equals(tablemasterInfo.layerid)){
									if(contentLayer.layerId.equals(decisionsupportInfo.tablemasterInfo.layerid)){
										// 1次データグループに入れる
										contentLayer.parent = groupLayerID_1;
										contentLayer.mapLayerInfo.parent = groupLayerID_1.layerId;
										// グループレイヤのchildrenに入れる
										for(JSONLayerInfo groupContentLayer : mapInitDto.groupContentsLayerInfos){
											if(groupContentLayer.layerId.equals(groupLayerID_1.layerId)){
												groupContentLayer.children.add(contentLayer);
											}
										}
									}
								}
							}
							/*
							// 2次レイヤは非表示するよう指示有 2016/03/24
							// 2次レイヤのグループレイヤを追加する
							else if(decisionTypeId >= 6 && decisionTypeId <= 8){
								if(groupLayerID_2 == null){
									groupLayerID_2 = searchLayerGroupName(mapInitDto.groupContentsLayerInfos, decisionLayerID);
									// 検索出来ればグループレイヤを追加する
									if(groupLayerID_2 != null){
										//config.layerIds.add(groupLayerID_2.layerId);
										//config.hiddenLayerIds.add(groupLayerID_2.layerId);
										config.expandLayerIds.add(groupLayerID_2.layerId);
									}
								}
							}
							*/
							// 3次レイヤのグループレイヤを追加する
							else if(decisionTypeId == 9){
								if(groupLayerID_3 == null){
									groupLayerID_3 = searchLayerGroupName(mapInitDto.groupContentsLayerInfos, decisionsupportInfo.tablemasterInfo.layerid);
									// 検索出来ればグループレイヤを追加する
									if(groupLayerID_3 != null){
										//config.layerIds.add(groupLayerID_3.layerId);
										//config.hiddenLayerIds.add(groupLayerID_3.layerId);
										config.expandLayerIds.add(groupLayerID_3.layerId);
									}
								}
							}

							// レイヤを追加(初期表示OFF, 折り畳み, 検索可能で設定)
							//config.layerIds.add(decisionLayerID);
							if(decisionTypeId != 1 && decisionTypeId != 11 && decisionTypeId != 10){
								// 初期表示OFF設定をmapFormに追加する
								if(mapForm.hidden != null){
									if(mapForm.hidden.length() == 0){
										mapForm.hidden = decisionsupportInfo.tablemasterInfo.layerid;
									}else{
										mapForm.hidden += "," + decisionsupportInfo.tablemasterInfo.layerid;
									}
								}else{
									mapForm.hidden = decisionsupportInfo.tablemasterInfo.layerid;
								}
								/*
								// timslider
								//pageDto.addTimesliderConf(decisionsupportInfo.tablemasterInfo.name, decisionsupportInfo.tablemasterInfo.layerid);
								// SLDルールによる凡例の表示切替のURL再現
								if(ruleMap!=null && ruleMap.containsKey(decisionsupportInfo.tablemasterInfo.layerid)) {
									String layerRule = ruleMap.get(decisionsupportInfo.tablemasterInfo.layerid);
									config.putExLayerProperty(decisionsupportInfo.tablemasterInfo.layerid, "legendrules_visiblerules", layerRule);
								}
								*/
							}
						}
					}
				}
				// 意思決定支援の総避難者数レイヤの更新時間を取得
				Timestamp decisionLastTime = decisionsupportInfoService.decisionsupportUpdateTime();
				String decisionUpdateTimeWareki = getUpdateTimeBy(decisionLastTime);
				decisionUpdateTime = decisionUpdateTimeWareki == null ? lang.__("not yet executed") : decisionUpdateTimeWareki;
				// 表示順を、避難所、１次データ。。。とする
				List<MaplayerInfo> dummy_maplayerInfos = new ArrayList<MaplayerInfo>();
				MaplayerInfo shelterMaplayerInfo = null;
				// 避難所のデータを取得する
				for(MaplayerInfo maplayerInfo : maplayerInfos) {
					String layerId = null;
					if (loginDataDto.getTrackdataid() == 0) layerId = maplayerInfo.tablemasterInfo.layerid;
					if (maplayerInfo.tablemasterInfo.tracktableInfos != null) layerId = maplayerInfo.tablemasterInfo.tracktableInfos.get(0).layerid;
					if (layerId == null) continue;
					if (shelterLayerID.equals(layerId)){
						shelterMaplayerInfo = maplayerInfo;
						shelterMaplayerInfo.closed = true;
						// 避難所のデータを除外する
						maplayerInfos.remove(maplayerInfo);
						break;
					}
				}
				// 順に格納していく
				if(shelterMaplayerInfo != null) dummy_maplayerInfos.add(shelterMaplayerInfo);
				for(MaplayerInfo maplayerInfo : decision_maplayerInfos) {
					dummy_maplayerInfos.add(maplayerInfo);
				}
				for(MaplayerInfo maplayerInfo : maplayerInfos) {
					dummy_maplayerInfos.add(maplayerInfo);
				}

				// 置き換える
				maplayerInfos = dummy_maplayerInfos;
			}
			// 登録情報レイヤ
			addable = false;
			for(MaplayerInfo maplayerInfo : maplayerInfos) {
				TablemasterInfo tablemasterInfo = maplayerInfo.tablemasterInfo;
				String layerId = null;
				if (loginDataDto.getTrackdataid() == 0) layerId = tablemasterInfo.layerid;
				if (tablemasterInfo.tracktableInfos != null) layerId = tablemasterInfo.tracktableInfos.get(0).layerid;
				if (layerId == null) continue;
				JSONLayerInfo jsonLayerInfo = jsonLayerInfoMap.get(layerId);
				if(jsonLayerInfo==null) continue;

				// 親がいて、追加されていなければ追加する
				if(jsonLayerInfo.parent!=null) {
					JSONLayerInfo parent = jsonLayerInfo.parent;
					if(config.layerIds.contains(parent.layerId)==false) {
						config.layerIds.add(parent.layerId);
					}
				}

				// データがあれば追加対象
				config.layerIds.add(layerId);
				// 初期表示
				boolean visible = maplayerInfo.visible;
				if(visible==false) {
					config.hiddenLayerIds.add(layerId);
				}
				// 初期折り畳み状態
				boolean closed = maplayerInfo.closed;
				if(closed==false) {
					config.expandLayerIds.add(layerId);
				}
				//「情報の登録」ボタン表示フラグ
				addable = (maplayerInfo.addable != null && maplayerInfo.addable && loginDataDto.isEdiable());
				if(pageDto.isViewMode()) addable = false;
				// 新規登録可能
				if(addable) {
					config.addableLayerIds.add(layerId);
				}
				// 編集可能
				boolean editable = maplayerInfo.editable;
				if(pageDto.isViewMode()) editable = false;
				if(editable) {
					config.editableLayerIds.add(layerId);
					// 最初の編集可能なレイヤは最終更新日時を表示させる
					if (pageDto.getUpdateTime()==null && !StringUtil.isEmpty(tablemasterInfo.updatecolumn)) {
						Timestamp lasttime = tableService.getEcomDataLastUpdateTime(layerId, tablemasterInfo.updatecolumn, mapForm.timeParams());
						setUpdateTime(lasttime);
						// 最終更新日時 の属性ID
						config.putExLayerProperty(layerId, "updatecolumn", tablemasterInfo.updatecolumn);
					}
				}
				// 検索可能
				config.putExLayerProperty(layerId, "searchable", maplayerInfo.searchable!=null ? maplayerInfo.searchable : MaplayerInfo.DEFAULT_SEARCHABLE);
				// スナップ可能
				config.putExLayerProperty(layerId, "snappable", maplayerInfo.snapable);
				// フィルタレイヤ
				if(filterDto!=null && layerId.equals(filterDto.getLayerId())) {
					config.putExLayerProperty(layerId, "filterkey", filterDto.getFilterkey());
					config.putExLayerProperty(layerId, "grayout", 0.5); // 1 そのまま表示、 0.5 半透明、 0 非表示
					config.putExLayerProperty(layerId, "spatialLayers", filterDto.getSpatialLayer());
				}

				// 切り出しレイヤ
				if(StringUtil.isNotEmpty(maplayerInfo.intersectionlayerid)) {
					String intersectionlayerid = maplayerInfo.intersectionlayerid;
					// マスタレイヤから災害用レイヤに変換できるなら変換する
					if(layerIdConvertMap.containsKey(intersectionlayerid)) intersectionlayerid = layerIdConvertMap.get(intersectionlayerid);
					LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(intersectionlayerid);
					if(layerInfo!=null) {
						config.putExLayerProperty(layerId, "intersectionlayerid", intersectionlayerid);
						config.putExLayerProperty(layerId, "intersectionlayername", layerInfo.name);
					}
				}

				// 属性の表示・編集フラグ
				if(0<maplayerInfo.maplayerattrInfos.size()) {
					config.attrDefaultStatus = AttrInfo.STATUS_SEARCHHIDE;
				}

				LinkedHashMap<String, Short> attrStatus = new LinkedHashMap<String, Short>();
				LayerInfo linfo = jsonLayerInfo.layerInfo;
				// 地図に記録データが複数あるなら、trackdataid属性を表示させる
				if(trackmapInfo!=null) {
					if(1<trackmapInfo.trackDatas.size()) {
						MaplayerattrInfo maplayerattrInfo = trackdataidAttrService.createMaplayerattrInfo(maplayerInfo);
						maplayerInfo.maplayerattrInfos.add(maplayerattrInfo);
					}
				}
				for(MaplayerattrInfo maplayerattrInfo : maplayerInfo.maplayerattrInfos) {
					String attrId = maplayerattrInfo.attrid;
					// 属性のステータスを表示・編集フラグから設定
					short status = AttrInfo.STATUS_DEFAULT;
					boolean editableAttr = maplayerattrInfo.editable;
					if(pageDto.isViewMode()) editableAttr = false;
					//座標の項目は編集不可
					if (StringUtil.isNotEmpty(tablemasterInfo.coordinatecolumn) && tablemasterInfo.coordinatecolumn.equals(attrId))
						editableAttr = false;
					if(editableAttr==false) status = AttrInfo.STATUS_READONLY;
					attrStatus.put(attrId, status);

					// 属性情報を取得
					AttrInfo attr = null;
					if(linfo!=null) {
						attr = linfo.getAttrInfo(maplayerattrInfo.attrid);
						// trackdataid なら、 attrInfo を上書き
						if(TrackdataidAttrService.TRACKDATA_ATTR_ID.equals(attrId)) {
							// trackdataid 属性を更新
							if(attr!=null) {
								trackdataidAttrService.overrideTrackdataidAttr(attr, trackData.id, trackmapInfo.trackDatas);
							}
						}
					}

					// 一括変更
					if(attr!=null && (editableAttr||maplayerattrInfo.grouping)) mapService.addEcomColumn(slimerDto, linfo, attr, editableAttr, maplayerattrInfo.grouping, maplayerattrInfo.defaultcheck, maplayerattrInfo.groupdefaultcheck, maplayerattrInfo.addable);

					// 拡張プロパティ
					// 属性のハイライトフラグを設定
					config.putExAttrProperty(layerId, attrId, "highlight", maplayerattrInfo.highlight);
					// 名前があれば上書き
					if(StringUtil.isNotEmpty(maplayerattrInfo.name)) {
						config.putExAttrProperty(layerId, attrId, "name", maplayerattrInfo.name);
					}
				}
				if(0<attrStatus.size()) {
					config.layerAttrStatusMap.put(layerId, attrStatus);
				}
				// 最終更新日カラムフラグを設定
				if(StringUtil.isNotEmpty(tablemasterInfo.updatecolumn)) {
					String attrId = tablemasterInfo.updatecolumn;
					config.putExAttrProperty(layerId, attrId, "updateInserted", true);
					config.putExAttrProperty(layerId, attrId, "updateModified", true);
				}
				// 住所カラムフラグを設定
				if(StringUtil.isNotEmpty(tablemasterInfo.addresscolumn)) {
					String attrId = tablemasterInfo.addresscolumn;
					config.putExAttrProperty(layerId, attrId, "addAddressButton", true);
				}
				// timslider
				pageDto.addTimesliderConf(tablemasterInfo.name, layerId);

				// SLDルールによる凡例の表示切替のURL再現
				if(ruleMap!=null && ruleMap.containsKey(layerId)) {
					String layerRule = ruleMap.get(layerId);
					config.putExLayerProperty(layerId, "legendrules_visiblerules", layerRule);
				}
			}
			// 震度Layer registration information layer is flagged
			{
				// Seismic intensity layer information
				String earthquakelayerId = meteoricEarthQuakeService.earthquakeLayerId(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid());
				if(StringUtil.isNotEmpty(earthquakelayerId)){
					config.putExLayerProperty(earthquakelayerId, "isEarthquakeLayer", true);
				}
				/*
				List<String> earthquakelayerIds = tableService.selectDistinct(DatabaseUtil.getTableName(EarthquakelayerData.class), Names.earthquakelayerData().layerid().toString());
				List<String> earthquakelayerIds = new ArrayList<>();
				List<EarthquakegrouplayerData> earthquakegrouplayerDatas = earthquakegrouplayerDataService.findByLocalgovinfoidAndTrackdataid(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid());
				for(EarthquakegrouplayerData earthquakegrouplayerData : earthquakegrouplayerDatas) {
					List<EarthquakelayerData> earthquakelayerDatas = earthquakelayerDataService.findByEarthGroupId(earthquakegrouplayerData.id);
					for(EarthquakelayerData earthquakelayerData : earthquakelayerDatas) {
						String earthquakelayerId = earthquakelayerData.layerid;
						if(earthquakelayerIds.contains(earthquakelayerId)==false) {
							earthquakelayerIds.add(earthquakelayerId);
						}
					}
				}
				for(String earthquakelayerId : earthquakelayerIds) {
					config.putExLayerProperty(earthquakelayerId, "isEarthquakeLayer", true);
				}
				*/
			}

			// KMLレイヤ
			JSONLayerInfo parent = null;
			for(int idx=0; idx<mapkmllayerInfos.size(); idx++) {
				MapkmllayerInfo mapkmllayerInfo = mapkmllayerInfos.get(idx);
				String layerId = mapkmllayerInfo.layerid;
				// check layerId is null
				if(StringUtil.isEmpty(layerId)) {
					addRequestErrorMessage(bindingResult, lang.__("LayerID of KML layer info (ID={0}) is not set.", mapkmllayerInfo.id));
					continue;
				}
				// グループレイヤの場合、親を更新
				if(layerId.startsWith("group:")) {
					// グループ用レイヤID 生成
					layerId = "kmlgroup"+mapkmllayerInfo.id;
					// グループ名
					String name = layerId;
					try {
						name = mapkmllayerInfo.layerid.split(":")[1];
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
					}
					// グループ用レイヤ情報を生成
					JSONLayerInfo jsonLayerInfo = new JSONLayerInfo(mapmasterInfo.communityid, mapId, layerId, name);
					// MapInfo に追加
					mapInitDto.mapInfo.addMapLayerInfo(jsonLayerInfo.mapLayerInfo);
					// KMLレイヤ情報に追加
					mapInitDto.kmlLayerInfos.add(jsonLayerInfo);

					// KMLレイヤで利用する親グループ変数を更新
					parent = jsonLayerInfo;

					// 初期折り畳み状態は false にする
					mapkmllayerInfo.closed = false;
				}
				// KMLレイヤの場合、親子関係を設定
				else {
					if(parent!=null) {
						JSONLayerInfo jsonLayerInfo = mapInitDto.getJSONLayerInfoMap().get(layerId);
						jsonLayerInfo.mapLayerInfo.parent = parent.layerId;
						jsonLayerInfo.parent = parent;
						parent.children.add(jsonLayerInfo);
					}
				}
				// データがあれば追加対象
				config.layerIds.add(layerId);
				boolean visible = mapkmllayerInfo.visible;
				if(visible==false) {
					config.hiddenLayerIds.add(layerId);
				}
				// 初期折り畳み状態
				boolean closed = mapkmllayerInfo.closed;
				if(closed==false) {
					config.expandLayerIds.add(layerId);
				}
				// 検索可能
				config.putExLayerProperty(layerId, "searchable", mapkmllayerInfo.searchable!=null ? mapkmllayerInfo.searchable : MapreferencelayerInfo.DEFAULT_SEARCHABLE);
			}

			// 主題図
			for(int idx=0; idx<mapreferencelayerInfos.size(); idx++) {
				MapreferencelayerInfo mapreferencelayerInfo = mapreferencelayerInfos.get(idx);
				MapreferencelayerInfo next = idx+1<mapreferencelayerInfos.size() ? mapreferencelayerInfos.get(idx+1) : null;
				String layerId = mapreferencelayerInfo.layerid;
				// check layerId is null
				if(StringUtil.isEmpty(layerId)) {
					addRequestErrorMessage(bindingResult, lang.__("LayerID of map reference layer info (ID={0}) is not set.", mapreferencelayerInfo.id));
					continue;
				}
				// 親いる場合は、レイヤの追加位置は親によるため、追加されていなければ追加する
				if(layerId.contains("_")) {
					String playerId = layerId.split("_")[0];
					if(config.layerIds.contains(playerId)==false) {
						config.layerIds.add(playerId);
					}
				}
				// データがあれば追加対象
				config.layerIds.add(layerId);
				// 初期表示
				boolean visible = mapreferencelayerInfo.visible;
				if(visible==false) {
					config.hiddenLayerIds.add(layerId);
				}
				// 初期折り畳み状態
				boolean closed = mapreferencelayerInfo.closed;
				if(closed==false) {
					config.expandLayerIds.add(layerId);
				}
				// 検索可能
				config.putExLayerProperty(layerId, "searchable", mapreferencelayerInfo.searchable!=null ? mapreferencelayerInfo.searchable : MapreferencelayerInfo.DEFAULT_SEARCHABLE);
				// 透明度
				if(mapreferencelayerInfo.layeropacity!=null) {
					JSONLayerInfo jsonLayerInfo = jsonLayerInfoMap.get(layerId);
					// 管理画面設定後にeコミマップの方で除外されることがあるため Nullチェック
					if(jsonLayerInfo!=null) {
						jsonLayerInfo.mapLayerInfo.opacity = mapreferencelayerInfo.layeropacity.floatValue();
					}
				}

				// 子がいる場合
				JSONLayerInfo jsonLayerInfo = jsonLayerInfoMap.get(layerId);
				if(jsonLayerInfo!=null && 0<jsonLayerInfo.children.size()) {
					// 子のレイヤIDリストを取得
					List<String> childLayerIds = new ArrayList<String>();
					for(JSONLayerInfo child : jsonLayerInfo.children) {
						childLayerIds.add(child.layerId);
					}
					// 次のレイヤの指定がない、または
					// 次が子レイヤの指定でないなら、子をすべて追加する
					if(next==null || childLayerIds.contains(next.layerid)==false) {
						config.layerIds.addAll(childLayerIds);
						if(visible==false) {
							config.hiddenLayerIds.addAll(childLayerIds);
						}
						// 初期折り畳み状態
						if(closed==false) {
							config.expandLayerIds.addAll(childLayerIds);
						}
						for(String childLayerId : childLayerIds) {
							// 検索可能フラグは自動追加のため親と同じ
							config.putExLayerProperty(childLayerId, "searchable", mapreferencelayerInfo.searchable!=null ? mapreferencelayerInfo.searchable : MapreferencelayerInfo.DEFAULT_SEARCHABLE);
						}
					}
				}
			}

			// Weather information layer
			for(MeteolayerInfo meteolayerInfo : meteolayerInfos) {
			}

			// 背景地図
			for(MapbaselayerInfo mapbaselayerInfo : mapbaselayerInfos) {
				String layerId = mapbaselayerInfo.layerid;
				// check layerId is null
				if(StringUtil.isEmpty(layerId)) {
					addRequestErrorMessage(bindingResult, lang.__("LayerID of base layer info (ID={0}) is not set.", mapbaselayerInfo.id));
					continue;
				}
				// データがあれば追加対象
				config.layerIds.add(layerId);
				// 初期表示
				boolean visible = mapbaselayerInfo.visible;
				if(visible==false) {
					config.hiddenLayerIds.add(layerId);
				}
			}

		}

		// リクエストパラメータから設定
		{
			if(StringUtil.isNotEmpty(mapForm.layers)) {
				config.layerIds = Arrays.asList(mapForm.layers.split(","));
			}
			if(StringUtil.isNotEmpty(mapForm.hidden)) {
				config.hiddenLayerIds = Arrays.asList(mapForm.hidden.split(","));
			}
			if(StringUtil.isNotEmpty(mapForm.expand)) {
				config.expandLayerIds = Arrays.asList(mapForm.expand.split(","));
			}
		}

		// 設定の反映
		config.config(mapInitDto);

		// JSONオブジェクトの作成
		JSONObject jsonObj = mapService.getInitData(mapInitDto, null);
		try {
			ResourceBundle pathBundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
			String geoserverWFS = pathBundle.getString("GEOSERVER_WFS");
			if(StringUtil.isNotEmpty(geoserverWFS)) {
				// http で始まらない場合は localRootURL で補完
				if(geoserverWFS.startsWith("http")==false) {
					String localRootURL = pathBundle.getString("LOCAL_ROOT_URL");
					geoserverWFS = localRootURL + geoserverWFS;
				}
				jsonObj.put("wfsURL", geoserverWFS);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}

		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] MapAction.getEcommapInfo elapsed: "+String.format("%.2f", (double)(endtime-logstarttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(logstarttime))+")");

		// JSONを出力する
		return jsonObj;
	}

	/**
	 * 意思決定支援用の該当LayerIDが何処のグループレイヤに属しているか確認するメソッド
	 * @param groupContentsLayerInfos : mapInfoDto.groupContentsLayerInfos
	 * @param layerid : 検索対象LayerID
	 * @return : グループレイヤ情報
	 */
	public JSONLayerInfo searchLayerGroupName(List<JSONLayerInfo> groupContentsLayerInfos, String layerid){
		// 支援用のレイヤIDを元にグループレイヤを探すのが筋だが、検索数的に、グループレイヤからchildrenに該当レイヤが存在するかを探した方が少ないので、そちらで実装
		for(JSONLayerInfo groupContentLayer : groupContentsLayerInfos){
			for(JSONLayerInfo childrenLayer : groupContentLayer.children){
				if(childrenLayer.layerId.equals(layerid)){
					return groupContentLayer;
				}
			}
		}
		return null;
	}

}
