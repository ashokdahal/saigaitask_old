/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.ecommap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import com.vividsolutions.jts.io.ParseException;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResult.AttrResult;
import jp.ecom_plat.map.db.FeatureResult.FileResult;
import jp.ecom_plat.map.db.FeatureSearchCondition;
import jp.ecom_plat.map.db.FilteredFeatureId;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayoutInfo;
import jp.ecom_plat.map.db.ListSearchConditionInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.db.SessionGeometry;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.exception.EcommapConflictException;
import jp.ecom_plat.map.servlet.AuthProxyServlet;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.FilterDto;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.MapInitDto;
import jp.ecom_plat.saigaitask.dto.MapInitDto.JSONLayerInfo;
import jp.ecom_plat.saigaitask.dto.MapInitDto.ReferenceJSONLayerInfo;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.dto.SlimerDto;
import jp.ecom_plat.saigaitask.entity.db.FilterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.names.ObservatoryriverInfoNames;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.CkanService;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.FilterInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatorydamInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryrainInfoService;
import jp.ecom_plat.saigaitask.service.db.ObservatoryriverInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.HttpUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * eコミマップのサービスクラスです.
 */
@org.springframework.stereotype.Service
public class MapService extends BaseService {

	protected Logger logger = Logger.getLogger(MapService.class);

	// Service
	@Resource protected TrackmapInfoService trackmapInfoService;
	@Resource protected MenutableInfoService menutableInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected ClearinghouseService clearinghouseService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected FileUploadService fileUploadService;
	@Resource protected ObservatoryriverInfoService observatoryriverInfoService;
	@Resource protected ObservatoryrainInfoService observatoryrainInfoService;
	@Resource protected ObservatorydamInfoService observatorydamInfoService;
	@Resource protected FilterInfoService filterInfoService;
	@Resource protected TableService tableService;
	@Resource protected EdituserAttrService edituserAttrService;
	@Resource protected CkanService ckanService;

    /** ログイン情報 */
    @Resource protected LoginDataDto loginDataDto;
	/** ページDto */
	@Resource public PageDto pageDto;

    /**
	 * レイヤ情報をMapInitDtoに読み込みます.
	 * ベースレイヤ、登録情報レイヤ、主題図項目のみ対応しています.
	 * KMLレイヤはまだ未対応です.
	 * @param mapId 地図ID
	 * @param authId ユーザID
	 * @param mapInitDto 振り分けたレイヤ情報を保存するためのDto
	 * @param excludes 除きたいレイヤIDのリスト
	 * @param isTraining 訓練フラグ
	 */
	public void loadLayerInfos(long mapId, String authId, MapInitDto mapInitDto, List<String> excludes, Long menuid, boolean isTraining){
		// 処理開始時刻
		long starttime = System.currentTimeMillis();

		if( mapInitDto==null ) return;

		//		//グループID取得
		//		int groupId = FormUtils.getIntParameter(request, "gid");

		// マップIDを取得
		if (mapId == Long.MIN_VALUE) {
			return;
		}

		// ユーザ情報のロード
    	MapDB mapDB = MapDB.getMapDB();
		UserInfo userInfo = mapDB.getAuthIdUserInfo(authId);

		// 地図情報
		MapInfo mapInfo = mapDB.getMapInfo(mapId);
		if(mapInfo==null) mapInfo = mapDB.getMapInfo(mapId, false);
		if(mapInfo==null) throw new ServiceException(lang.__("Map info is not found.(mid=")+mapId+")");
		mapInitDto.mapInfo = mapInfo;

		// 地図のサイト情報を取得
		CommunityInfo communityInfo = null;
		int cid = mapInfo.communityId;
		try{
			communityInfo = mapDB.getCommunityInfo(cid);
			// 地図レイアウト情報
			mapInitDto.layoutInfo = mapDB.getLayoutInfo(mapId);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		if (cid > 0 && communityInfo == null) {
			return;
		}

		try {
			// 登録情報項目のリロード設定を取得
			String redrawOption = mapDB.getOption("REDRAW", cid, mapId);
			if(StringUtil.isNotEmpty(redrawOption)) {
				mapInitDto.redrawInterval = Integer.parseInt(redrawOption);
			}
			// サイトのEPSG設定を取得
			mapInitDto.epsg = MapDB.getMapDB().getOption("EPSG", cid);
			// サイトの縮尺を取得
			mapInitDto.scales = null;
			String scalesStr = mapDB.getOption("SCALES", cid);
			if(StringUtil.isNotEmpty(scalesStr)) {
				List<Long> scales = new ArrayList<Long>();
				String[] elems = scalesStr.split(",");
				for(String elem : elems) {
					scales.add(Long.valueOf(elem));
				}
				mapInitDto.scales = scales.toArray(new Long[scales.size()]);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		ReferenceJSONLayerInfo refLayerInfo = null;

		// 地図に登録されたレイヤを順にロードする
		for (MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
			LayerInfo layerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId);
			//レイヤが削除されていたら出力しない
			if (layerInfo == null || layerInfo.status==LayerInfo.STATUS_DELETED) continue;
			// 除きたいレイヤIDのリストに入っているかチェックする
			if(excludes!=null && excludes.contains(layerInfo.layerId) ) continue;


			// レイヤ種別毎にレイヤ情報を作成
			switch(layerInfo.type){

			//レイヤグループ
			case LayerInfo.TYPE_LOCAL_GROUP:
				mapInitDto.groupContentsLayerInfos.add(new JSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo));
				break;

			// 登録情報レイヤ
			case LayerInfo.TYPE_LOCAL:
				// レイヤの閲覧権限がなければ出力しない
				try {
					// 認証キーを出力する
					String layerPhrase = layerInfo.layerId+authId;
					String layerAuthKey = createLayerAuthorizedKey(layerPhrase);
					mapInitDto.layerAuthKeyMap.put(layerInfo.layerId, layerAuthKey);

					// JSON
					mapInitDto.contentsLayerInfos.add(new JSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				break;

				// KMLレイヤ
				case LayerInfo.TYPE_KML:
					//KMLレイヤ情報出力

					//閲覧権限がなければ出力しない
					//layerVisible = AuthInfo.authLayerView(userInfo, cid, layerInfo);
					//layerAccessKey = UserAuthorization.setSessionLayerAccessKey(session, authId, layerInfo.layerId, layerVisible);

					mapInitDto.kmlLayerInfos.add(new JSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo));
					break;

			// 主題図項目レイヤ
			case LayerInfo.TYPE_REFERENCE_WMS:
				//参照地図の親レイヤ WMSの情報をもつ
				//ProxyURLで権限がなければ出力しない
				if (layerInfo.wmsURL != null) {
					if (!AuthProxyServlet.checkProxyAuthorized(layerInfo.wmsURL, userInfo)) {
						continue;
					}
				}

				// 17/07/07 主題図の親はグループ化しかしていないのでfeaturetype_idは無視
				// 主題図追加時に WMSCapabilies の <Name>の値が記録されている場合があるので削除しておく
				layerInfo.featuretypeId = null;

				refLayerInfo = new ReferenceJSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo);
				mapInitDto.referenceLayerInfoMap.put(layerInfo.layerId, refLayerInfo);
				break;

			// 主題図項目レイヤに属する項目レイヤ
			case LayerInfo.TYPE_REFERENCE:
				// 主題図項目レイヤの下に追加するのをやめて直接追加
				refLayerInfo = mapInitDto.referenceLayerInfoMap.get(mapLayerInfo.parent);
				mapInitDto.referenceLayerInfoMap.put(layerInfo.layerId, new ReferenceJSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo));
				break;

			// 主題図（画像）項目レイヤ
			case LayerInfo.TYPE_OVERLAY_WMS:
			case LayerInfo.TYPE_OVERLAY_WMS_SINGLE:
			case LayerInfo.TYPE_OVERLAY_TILED:
			case LayerInfo.TYPE_OVERLAY_TILECACHE:
			case LayerInfo.TYPE_OVERLAY_OSM:
			case LayerInfo.TYPE_OVERLAY_XYZ:
			case LayerInfo.TYPE_OVERLAY_GROUP:
				//ProxyURLで権限がなければ出力しない
				if (layerInfo.wmsURL != null) {
					if (!AuthProxyServlet.checkProxyAuthorized(layerInfo.wmsURL, userInfo))
						continue;
				}

				mapInitDto.overlayLayerInfos.add(new JSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo));
				break;

			// ベースレイヤ
			case LayerInfo.TYPE_BASE_WMS:
			case LayerInfo.TYPE_BASE_TILED:
			case LayerInfo.TYPE_BASE_TILECACHE:
			case LayerInfo.TYPE_BASE_KAMAPCACHE:
			case LayerInfo.TYPE_BASE_OSM:
			case LayerInfo.TYPE_BASE_CDS:
			case LayerInfo.TYPE_BASE_GOOGLE:
			case LayerInfo.TYPE_BASE_WEBTIS:
			case LayerInfo.TYPE_BASE_XYZ:
				mapInitDto.baseLayerInfos.add(new JSONLayerInfo(cid, mapId, mapLayerInfo, layerInfo));
				break;

			default:
				// do nothing
				break;
			}
		}

		// 外部地図レイヤをロードする
		List<JSONLayerInfo> externalMapLayers = (loginDataDto.isUseCkan() ? ckanService : clearinghouseService).getMapLayers(menuid, isTraining);
		for (JSONLayerInfo mapLayerInfo : externalMapLayers) {
			mapInitDto.externalMapLayerInfos.add(mapLayerInfo);
		}

		//サイトを地図所属サイトに変更
		mapInitDto.communityId = mapInfo.communityId;

		// レイヤ情報の親子関係をビルド
		mapInitDto.buildTree();

		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] MapService.loadLayerInfos elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");

		return;
	}

	/**
	 * Get map initialization data in JSON.
	 * @param mapInitDto Map initialization Dto
	 * @param excludes List of layer IDs to be removed (such as past disaster layers)
	 * @return JSONObject Initialized JSON data
	 */
	public JSONObject getInitData(MapInitDto mapInitDto, List<String> excludes){

		try{
			JSONObject json = new JSONObject();
			json.put("ecommapURL", Config.getEcommapURL()+"map");

			// WMSAuth のURL
			//json.put("wmsAuthURL", SpringContext.getRequest().getContextPath()+("/page/map/wmsAuth/?"));
			String contextPath = SpringContext.getRequest().getContextPath();
			json.put("wmsAuthURL", contextPath+"/page/map/wmsAuth/?");

			// Clearinghouse のURL
			boolean isTraining = pageDto.getTrackData()!=null && pageDto.getTrackData().isTraining();
			json.put("cswURL", isTraining ? Config.getString("CSWURL_TRAINING", null) : Config.getString("CSWURL", null));
			String ckanurl = isTraining ? Config.getString("CKAN_URL_TRAINING") : Config.getString("CKAN_URL");
			if(StringUtil.isNotEmpty(ckanurl)) json.put("cswURL", ckanurl);

			// EPSG
			json.put("epsg", mapInitDto.epsg);

			// EPSG
			json.put("scales", mapInitDto.scales);

			// 地図情報
			JSONObject mapInfoJSON = MapInfo.toJSON(mapInitDto.mapInfo);
			mapInfoJSON.put("reload", mapInitDto.redrawInterval);
			json.put("mapInfo", mapInfoJSON);

			// レイアウト情報
			LayoutInfo layoutInfo = mapInitDto.layoutInfo;
			json.put("layoutInfo", layoutInfo!=null?LayoutInfo.toJSON(layoutInfo):null);

			// ベースレイヤ情報
			JSONArray baseLayers = new JSONArray();
			json.put("baseLayers", baseLayers);
			for(JSONLayerInfo baseLayer : mapInitDto.baseLayerInfos ) {
				baseLayers.put(baseLayer.toJSON());
			}

			// グループ登録情報レイヤの項目情報
			JSONArray groupContentsLayers = new JSONArray();
			json.put("groupContentsLayers", groupContentsLayers);
			for(JSONLayerInfo groupContentsLayer : mapInitDto.groupContentsLayerInfos) {
				groupContentsLayers.put(groupContentsLayer.toJSON());
			}

			// 登録情報レイヤの項目情報
			JSONArray contentsLayers = new JSONArray();
			json.put("contentsLayers", contentsLayers);
			for(JSONLayerInfo contentsLayer : mapInitDto.contentsLayerInfos ) {
				JSONObject contentsLayerJson = contentsLayer.toJSON();
				contentsLayerJson.put("authkey", mapInitDto.layerAuthKeyMap.get(contentsLayer.layerInfo.layerId));
				contentsLayers.put(contentsLayerJson);
			}

			// 登録情報レイヤの認証キー
			json.put("layerAuthKeyMap", mapInitDto.layerAuthKeyMap);

			// KMLレイヤ情報
			JSONArray kmlLayers = new JSONArray();
			json.put("kmlLayers", kmlLayers);
			for(JSONLayerInfo kmlLayer : mapInitDto.kmlLayerInfos) {
				JSONObject kmlLayerJson = kmlLayer.toJSON();
				kmlLayers.put(kmlLayerJson);
			}

			// 主題図項目レイヤ情報
			JSONArray referenceLayers = new JSONArray();
			json.put("referenceLayers", referenceLayers);
			Map<String, ReferenceJSONLayerInfo> referenceLayerInfoMap = mapInitDto.referenceLayerInfoMap;
			for(Map.Entry<String, ReferenceJSONLayerInfo> entry : referenceLayerInfoMap.entrySet()){
				ReferenceJSONLayerInfo layerInfo = entry.getValue();
				referenceLayers.put(layerInfo.toJSON());
			}

			// 主題図（画像）項目レイヤ情報
			JSONArray overlayLayers = new JSONArray();
			json.put("overlayLayers", overlayLayers);
			for(JSONLayerInfo overlayLayerInfo : mapInitDto.overlayLayerInfos) {
				overlayLayers.put(overlayLayerInfo.toJSON());
			}

			// 外部地図レイヤ情報
			JSONArray externalMapLayers = new JSONArray();
			json.put("externalMapLayers", externalMapLayers);
			for(JSONLayerInfo externalMapLayerInfo : mapInitDto.externalMapLayerInfos ) {
				externalMapLayers.put(externalMapLayerInfo.toJSON());
			}

			return json;
		} catch( JSONException e ) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 矩形で検索した登録情報をJSONで取得します.
	 *
	 * @param session セッション
	 * @param mid 地図ID
	 * @param layers レイヤ配列
	 * @param bbox 矩形 [pointLowLeft.x, pointLowLeft.y, pointUpRight.x, pointUpRight.y]
	 * @param limit imit
	 * @param offset offset
	 * @param time new Date[2]
	 * http://ec2-18-222-68-104.us-east-2.compute.amazonaws.com
	 * @return 結果 JSON
	 */
	public JSONArray getEMapContents(HttpSession session, long mid, String[] layers, double[] bbox, int limit, int offset, Date[] time, Map<String, String> sldFilterQueryMap) {

		//一覧検索
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
		for (String layerId : layers) {
			// TODO 表示権限チェック
			LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
			//if (AuthInfo.authLayerView(userInfo, cid, layerInfo))
			vecLayerInfo.add(layerInfo);
		}

		if (vecLayerInfo.size() == 0) {
			return new JSONArray();
		}

		JSONArray json;
		try {

			json = FeatureDB
					.searchFeatureBbox(session, mid, vecLayerInfo, sldFilterQueryMap, bbox, null,
							limit, offset,true, FeatureDB.GEOM_TYPE_CENTER, null, false, null, false, time)
							.toJSON(FeatureDB.GEOM_TYPE_CENTER);
			return json;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 登録情報を取得します.
	 * @param authId eコミユーザ名
	 * @param mid 地図ID
	 * @param layerId レイヤID
	 * @param featureId フィーチャID
	 * @param timeParam 時間パラメータ nullなら現在時刻
	 * @return JSONArray 登録情報のJSON配列、なければ null
	 */
	public JSONObject getContents(String authId, long mid, String layerId, long featureId, Date[] timeParam) {
		// TODO 表示権限チェック
		try {
			MapDB mapDB = MapDB.getMapDB();
			LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
			FeatureResult featureResult = FeatureDB.getFeatureContent(layerInfo, featureId, true, FeatureDB.GEOM_TYPE_GEOM, /*bbox*/null, timeParam);
			JSONObject result = toJSON(featureResult, FeatureResult.GEOM_RESULT_WKT);

			// そのほか災害対応システムの情報を取得する
			// ダム観測所情報のURLのため？
			JSONObject external = getExternalData(layerId, featureId);
			result.put("external", external);

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * フィーチャ検索結果をJSONObjectに変換します.
	 * 属性とファイル情報のみ対応。
	 * @param featureResult フィーチャ検索結果
	 * @param geomType Featureのジオメトリ型
	 * @return JSONObject 変換失敗時は null
	 */
	public JSONObject toJSON(FeatureResult featureResult, int geomType) {
		try {
			JSONObject json = new JSONObject();
			//JSONArray json = new JSONArray();

			json.put("layerId", featureResult.layerId);
			json.put("featureId", featureResult.featureId);

			try {
				JSONArray originalJSON = featureResult.toJSON(geomType);
				JSONArray geomArray = originalJSON.getJSONArray(2);
				json.put("geom", geomArray);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}

			JSONArray attrArray = new JSONArray();
			int attrResultNum = featureResult.countAttrResult();
			for(int i=0; i<attrResultNum; i++) {
				AttrResult attr = featureResult.getAttrResult(i);
				attrArray.put(new JSONObject()
				.put("attrId", attr.getAttrId())
				.put("attrName", attr.getAttrName())
				.put("attrValue", attr.getAttrValue()));
			}
			json.put("attrs", attrArray);

			JSONArray fileArray = new JSONArray();
			int fileResultNum = featureResult.countFileResult();
			for(int idx=0; idx<fileResultNum; idx++) {
				FileResult file = featureResult.getFileResult(idx);
				if(StringUtil.isNotEmpty(file.url) && file.url.startsWith("http")==false) {
					file.url = Config.getEcommapURL().replaceAll("/$", "") + file.url;
				}
				fileArray.put(file.url);
				fileArray.put(file.title);
			}
			json.put("files", fileArray);

			JSONArray metaArray = new JSONArray();
			if (featureResult.metaInfo != null) {
				metaArray.put(featureResult.metaInfo.status);
				metaArray.put(featureResult.metaInfo.userid);
				String userName = null;
				if (featureResult.metaInfo.userid > 0) {
					UserInfo userInfo = MapDB.getMapDB().getUserInfo(featureResult.metaInfo.userid);
					if (userInfo != null) userName = userInfo.name;
				}
				metaArray.put(userName);
				metaArray.put(featureResult.metaInfo.created);
				metaArray.put(featureResult.metaInfo.moduserid);
				userName = null;
				if (featureResult.metaInfo.moduserid > 0) {
					UserInfo userInfo = MapDB.getMapDB().getUserInfo(featureResult.metaInfo.moduserid);
					if (userInfo != null) userName = userInfo.name;
				}
				metaArray.put(userName);
				metaArray.put(featureResult.metaInfo.modified);
				metaArray.put(featureResult.metaInfo.time_from);
				metaArray.put(featureResult.metaInfo.time_to);
			}
			json.put("meta", metaArray);

//			if (caps != null && caps.length >= 4) {
//				JSONArray capsArray = new JSONArray();
//				capsArray.put(caps[0]); //updata
//				capsArray.put(caps[1]); //delete
//				capsArray.put(caps[2]); //fileupload
//				capsArray.put(caps[3]); //authContentsInsert
//				json.put(capsArray);
//			}

			/*json.put("geom", geomArray);

			json.put("dist", featureResult.distance);

			JSONArray fileArray = new JSONArray();
			json.put("files", fileArray);

			JSONArray metaArray = new JSONArray();
			json.put("meta", metaArray);


			if (caps != null && caps.length >=3) {
				JSONObject caps = new JSONObject();
				if (caps[0]) caps.put("update", true);
				if (caps[1]) caps.put("delete", true);
				if (caps[2]) caps.put("fileupload", true);
				jsonb.put(caps);
			}
			*/
			return json;
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * レイヤの属性情報をJSONオブジェクトで取得します.
	 * eコミマップの /map/map/eMapAttrInfo.jsp に対応します.
	 * @param layerId レイヤID
	 * @return JSONObject なければ null
	 */
	public JSONObject getAttrInfo(String layerId) {

		// レイヤ情報を取得
		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layerInfo = mapDB.getLayerInfo(layerId);

		if(layerInfo!=null){
			// trackdataid 属性があるなら、dataExp などを上書き
			AttrInfo trackdataidAttr = layerInfo.getAttrInfo(TrackdataidAttrService.TRACKDATA_ATTR_ID);
			if(trackdataidAttr!=null) {
				if(loginDataDto.getTrackdataid()!=0) {
					TrackDataService trackDataService = SpringContext.getApplicationContext().getBean(TrackDataService.class);
					// ログイン中の記録データ
					final TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());

					// 複合災害対応により、記録データ内にある地図情報IDを使う
					if(trackData!=null) {
						TrackmapInfo trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackData.trackmapinfoid);
						TrackdataidAttrService trackdataidAttrService = SpringContext.getApplicationContext().getBean(TrackdataidAttrService.class);
						trackdataidAttrService.overrideTrackdataidAttr(trackdataidAttr, trackData.id, trackmapInfo.trackDatas);
					}
				}
			}

			try{
				JSONObject json = new JSONObject();

				// レイヤ情報をJSONに変換
				JSONObject layerJson = new JSONObject();
				layerJson.put("layerName", layerInfo.name);
				layerJson.put("layerId", layerInfo.layerId);
				json.put("layerInfo", layerJson);

				// 属性情報をJSONArrayで取得
				JSONArray attrJsonArray = MapService.getAttrInfos(layerInfo);
				json.put("attrInfos", attrJsonArray);

				return json;
			} catch(JSONException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * レイヤの属性情報をJSON配列で取得します.
	 * レイヤ情報を取得して{@link #getAttrInfos(LayerInfo)}を呼び出します.
	 * @param layerId レイヤID
	 * @return JSONArray なければ null
	 */
	public static JSONArray getAttrInfos(String layerId) {
		LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
		return getAttrInfos(layerInfo);
	}

	/**
	 * レイヤの属性情報をJSON配列で取得します.
	 * @param layerInfo レイヤ情報
	 * @return JSONArray なければ null
	 */
	public static JSONArray getAttrInfos(LayerInfo layerInfo) {
		if(layerInfo!=null){
			// 属性情報をJSONに変換
			JSONArray attrJsonArray = new JSONArray();
			int idx = 0;
			Map<Integer, String> datatypeNameMap = new HashMap<Integer, String>();
			for (AttrInfo attrInfo : layerInfo.getAttrIterable()) {
				if (attrInfo.status>=AttrInfo.STATUS_SEARCHHIDE) {
					try {
						JSONObject attrJson = new JSONObject();
						// 属性ID
						attrJson.put("attrId", attrInfo.attrId);
						// 属性名称
						attrJson.put("name", attrInfo.name);
						// TODO: 属性の状態（編集可・編集不可・非表示（編集可）の３種類）でフォームの入力方法を変更する.
						// 編集可: 編集可能.
						// 編集不可： フォームに表示されているが値を指定できない.
						// 非表示（編集可）: フォームに表示されていて編集もできる.ラベルがグレーっぽくなっている.
						attrJson.put("status", attrInfo.status);
						// 属性の表示文字数. どう使えばいい？
						attrJson.put("length", attrInfo.length);
						// 属性の最大文字数.指定がなければ0になっている
						attrJson.put("maxLength", attrInfo.maxLength);
						// TODO: 属性のデータ型でフォームの入力方法を変更する.
						attrJson.put("dataType", attrInfo.dataType);
						attrJson.put("dataTypeId", AttrInfo.getDataTypeId(attrInfo.dataType));
						String dataTypeName = datatypeNameMap.get(attrInfo.dataType);
						// データ型名称を毎回ＤＢ問い合わせすると遅いのでMapを使ってキャッシュする
						if(dataTypeName==null) {
							dataTypeName = attrInfo.getDataTypeName();
							datatypeNameMap.put(attrInfo.dataType, dataTypeName);
						}
						attrJson.put("dataTypeName", dataTypeName);
						// 属性データ種別に応じた設定内容文字列. どう使えばいい？
						attrJson.put("dataExp", attrInfo.dataExp);
						// nullの許可
						attrJson.put("nullable", attrInfo.nullable);
						attrJson.put("disporder", idx);
						attrJsonArray.put(attrJson);
					} catch (JSONException e) {
						Logger.getLogger(MapService.class).error(e);
					}
				}
				idx++;
			}

			return attrJsonArray;
		}

		return null;
	}

	/**
	 * ログインユーザ別のレイヤ認証キーを取得する
	 * キーは生成しなおしても前回と同じになる
	 * UserAuthorization.java のメソッドをカスタマイズ
	 * @param layerPhrase layerId+authId
	 * @return レイヤ認証キー
	 * @throws Exception
	 */
	public String createLayerAuthorizedKey(String layerPhrase) throws Exception {
		MapDB mapDB = MapDB.getMapDB();
		logger.trace("create layerAuthKey: "+layerPhrase);
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update((layerPhrase+mapDB.getAuthKey()).getBytes());
		//長さを短くして返却
		return new String(Base64.encodeBase64(md.digest())).substring(0,8).replaceAll("\\/|\\=|\\+", "");
	}

	/**
	 * データの追加
	 * @param authId
	 * @param layerid
	 * @param wkt
	 * @param attributes
	 * @return フィーチャID
	 */
	public long insertFeature(String authId, String layerid, String wkt, HashMap<String, String> attributes) {
    	MapDB mapDB = MapDB.getMapDB();
		UserInfo userInfo = mapDB.getAuthIdUserInfo(authId);

		try {
			long fid = FeatureDB.insertFeature(userInfo, layerid, wkt, attributes);
			return fid;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0l;
	}

	/**
	 *
	 * @param userInfo
	 * @param layerInfo
	 * @param featureId
	 * @return 成功フラグ
	 * @throws EcommapConflictException
	 * @throws Exception
	 */
	public boolean deleteFeature(UserInfo userInfo, LayerInfo layerInfo, long featureId) throws EcommapConflictException, Exception {

		boolean ret = false;

		String layerId = layerInfo.layerId;

		// 履歴レイヤの場合は、更新すると gid が変化するので、更新時に指定した time_from から gid を取得する。
		if(LayerInfo.TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
			final long orgid = featureId;

			// 削除レコードに設定する time_to を決定
			//Timestamp timeTo;
			//if (deleted == null) timeTo = new Timestamp(WMSUtils.getCurrentUTCDate().getTime());
			//else timeTo = new Timestamp(deleted.getTime());
			Timestamp timeTo = new Timestamp(TimeUtil.newDate().getTime());

			// 削除実行
			ret = FeatureDB.deleteFeature(userInfo, layerId, orgid, /*timestamp*/ null, timeTo);

			// 履歴レイヤの削除では削除も履歴としてレコードが存在するため、
			// 編集者など、削除情報として必要なデータがあれば更新する。
			{
				// 削除レコードの fid を取得
				long deletedFeatureId = ExMapDB.getHistoryFeatureId(layerId, orgid, new Date[]{timeTo});

				HashMap<String, String> attribute = new HashMap<>();

				// trackdataid: 記録ID
				attribute.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, String.valueOf(loginDataDto.getTrackdataid()));

				// st_edituser: 編集者
				// 編集者も更新項目として追加
				if(edituserAttrService.hasEdituserAttr(layerInfo)) {
					attribute.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
				}

				// 削除レコードに対して、属性更新
				for(Map.Entry<String, String> entry : attribute.entrySet()) {
					tableService.update(layerInfo.layerId, entry.getKey(), "gid", deletedFeatureId, entry.getValue());
				}
			}
		}
		// 通常の登録情報レイヤの場合
		else {
			return FeatureDB.deleteFeature(userInfo, layerId, featureId, /*timestamp*/ null, /*deleted*/ null);
		}

		return ret;
	}

	/**
	 * 地図を作成する。
	 * @param mastermapid マスタの地図ID
	 * @param grpid グループID
	 * @param mapname 地図名称
	 * @param cid コミュニティーID
	 * @param authId eコミマップユーザID
	 * @param description 備考
	 * @return 地図ID
	 * @throws Exception
	 */
	public long createMap(long mastermapid, int grpid, String mapname, int cid, String authId, String description) throws Exception {
    	MapDB mapDB = MapDB.getMapDB();

    	//マスターのレイアウトを取得
    	LayoutInfo masterlayoutInfo = mapDB.getLayoutInfo(mastermapid);
		//ユーザの取得
    	UserInfo user = mapDB.getAuthIdUserInfo(authId);

    	//マップの追加
    	MapInfo mapInfo = mapDB.createNewMapInfo(cid, user.userId, mapname, description);
    	//レイアウトの追加
    	mapDB.insertLayoutInfo(new LayoutInfo(mapInfo.mapId, masterlayoutInfo.mapExtent, masterlayoutInfo.mapResolution, masterlayoutInfo.subMaps, masterlayoutInfo.visibleSubMap, null));
    	//マップのグループ
    	if (grpid > 0)
    		mapDB.updateMapGroupId(mapInfo, grpid);

    	return mapInfo.mapId;
	}

	/**
	 * フィルタを実行します.
	 * @param conditionValue 条件
	 * @param timeParam 時間パラメータ nullなら現在時刻
	 * @return 検索時は処理結果を返す。検索しなかった場合はnullを返す。
	 */
	public FilterDto filter(JSONObject conditionValue, Date[] timeParam) {
		return filter(conditionValue, pageDto.getMenuInfo(), timeParam);
	}

	/**
	 * メニュー情報に設定されている最初のフィルタ条件でフィルタします.
	 * @param conditionValue 条件
	 * @param menuInfo メニュー情報
	 * @param timeParam 時間パラメータ nullなら現在時刻
	 * @return 検索時は処理結果を返す。検索しなかった場合はnullを返す。
	 */
	public FilterDto filter(JSONObject conditionValue, MenuInfo menuInfo, Date[] timeParam) {
		// 検索条件がないなら、メニュー情報から取得する
		if(conditionValue==null) {
			// 検索条件を取得
			FilterInfo filterInfo = null;
			if(menuInfo.filterInfoList==null) {
				menuInfo.filterInfoList = filterInfoService.findByMenuid(menuInfo.id);
			}
			//if(0<menuInfo.filterInfoList.size()) filterInfo = menuInfo.filterInfoList.get(0);
			// デフォルトはフィルター解除
			if(0<menuInfo.filterInfoList.size()) filterInfo = createNothingFilterInfo(menuInfo.id);
			else return null;
			
			// filterInfoId をセット
			FilterDto filterDto = filter(filterInfo, timeParam);
			filterDto.setFilterInfoId(filterInfo.id);
			return filterDto;
		}

		// メニューIDを取得
		Long menuid = menuInfo.id;
		return doFilter(menuid, conditionValue, timeParam);
	}
	
	public FilterInfo createNothingFilterInfo(Long menuinfoid) {
		FilterInfo filterInfo = new FilterInfo();
		filterInfo.id = 0L;
		filterInfo.menuinfoid = menuinfoid;
		filterInfo.filterid = 0L;
		filterInfo.name = lang.__("解除");
		filterInfo.disporder = 0;
		return filterInfo;
	}
	
	public boolean isNothingFilterInfo(FilterInfo filterInfo) {
		FilterInfo nothingFilterInfo = createNothingFilterInfo(0L);
		return nothingFilterInfo.id.equals(filterInfo.id);
	}
	
	public boolean isNoFilter(JSONObject conditionValue) {
		// フィルター解除
		String key = "nofilter";
		if(conditionValue.has(key)) {
			try {
				return conditionValue.getBoolean(key);
			} catch (JSONException e) {
				// do nothing
			}
		}
		return false;
	}

	/**
	 * フィルタ情報の条件でフィルタします.
	 * @param filterInfo フィルター情報
	 * @param timeParam 時間パラメータ nullなら現在時刻
	 * @return 検索時は処理結果を返す。検索しなかった場合はnullを返す。
	 */
	public FilterDto filter(FilterInfo filterInfo, Date[] timeParam) {

		// 検索条件がないなら、メニュー情報から取得する
		JSONObject conditionValue = null;
		try {
			conditionValue = getConditionValue(filterInfo);
		} catch (Exception e) {
			logger.error(lang.__("An error occurred while getting the search condition."));
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			throw new ServiceException(lang.__("Search condition is invalid. Check a search condition."), e);
		}

		// メニューIDを取得
		Long menuid = filterInfo.menuinfoid;
		return doFilter(menuid, conditionValue, timeParam);
	}

	/**
	 * フィルター情報から条件を取得
	 * @param filterInfo フィルター情報
	 * @return 条件
	 * @throws JSONException
	 */
	public JSONObject getConditionValue(FilterInfo filterInfo) throws JSONException {
		Long filterid = filterInfo.filterid;
		// フィルター解除の場合、該当メニューから適当なフィルタ情報をベースにする
		if(isNothingFilterInfo(filterInfo)) {
			filterid = filterInfoService.findByMenuid(filterInfo.menuinfoid).get(0).filterid;
		}
		MapDB mapDB = MapDB.getMapDB();

		// eコミユーザ情報を取得(検索条件の閲覧権限を要チェックのため)
		String authId = loginDataDto.getEcomUser();
		if(StringUtil.isEmpty(authId)) throw new ServiceException(lang.__("e-Com user info has not been set."));
		UserInfo userInfo = mapDB.getAuthIdUserInfo(authId);
		if(userInfo==null) throw new ServiceException(lang.__("Unable to get e-Com user info.")+authId);

		// フィルタキー
		if(filterid==null) throw new ServiceException(lang.__("Unable to get filter conditions. (filter ID = {0})", "NULL"));
		JSONObject conditionValue = ListSearchConditionInfo.getConditionValue(userInfo , filterid);

		// フィルタ情報の書き込み
		conditionValue.put("filterinfoid", filterInfo.id);
		conditionValue.put("name", filterInfo.name);

		// フィルター解除の場合
		if(isNothingFilterInfo(filterInfo)) {
			// 属性検索
			conditionValue.put("condition", new JSONArray());
			// 空間検索
			conditionValue.put("spatiallayer", new JSONArray());
			// フィルター解除
			conditionValue.put("nofilter", true);
			// フィルター解除用のメニューID
			conditionValue.put("menuid", filterInfo.menuinfoid);
		}

		return conditionValue;
	}

	/**
	 *
	 * @param filterkey セッションに保存するためのフィルタキー（動的フィルター対応のためメニューIDを使用する）
	 * @param conditionValue 属性検索条件
	 * @param timeParam 時間パラメータ nullなら現在時刻
	 * @return フィルタ結果
	 */
	public FilterDto doFilter(Long filterkey, JSONObject conditionValue, Date[] timeParam) {
		//平時モードではフィルタリングしない
		//if (loginDataDto.getTrackdataid() == 0) return null;

		JSONObject conditionValueActual = null;
		try {
			conditionValueActual = new JSONObject(conditionValue.toString());
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		MapDB mapDB = MapDB.getMapDB();
		logger.debug("condition = "+conditionValueActual);

		// 検索対象の地図ID
		long mapId = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid()).mapid;
		Map<String, String> layerIdConvertMap = null;
		// 災害モードでログイン中
		if(0<loginDataDto.getTrackdataid()) {
			// 記録IDから地図IDを取得する
			TrackmapInfo trackmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());
			if(trackmapInfo==null) return null;
			mapId = trackmapInfo.mapid;

			// レイヤIDを TablemasterInfo.layerid から TracktableInfo.layerId に変換するマップを取得
			layerIdConvertMap = getLayerIdConvertMap(trackmapInfo.id);
		}

		// 検索条件をオブジェクトに変換
		String layerId = null;
		boolean isNot = false;
		int spatialType = 0;
		double buffer = 0;
		Vector<FeatureSearchCondition> keywords = new Vector<FeatureSearchCondition>();
		JSONArray spatialLayer = null;
		boolean nofilter = false; // フィルター解除
		Long menuid = null;
		try {
			String key = null;
			
			// フィルター解除
			key = "nofilter";
			if(conditionValueActual.has(key)) {
				nofilter = conditionValueActual.getBoolean(key);
				key = "menuid";
				if(conditionValueActual.has(key)) {
					menuid = conditionValueActual.getLong(key);
				}
			}

			// 検索対象レイヤID
			key = "layerId";
			if(conditionValueActual.has(key)) {
				layerId = conditionValueActual.getString(key);
				// 検索対象のレイヤIDがマスタマップのレイヤIDになっていれば変換
				if(layerIdConvertMap!=null && layerIdConvertMap.containsKey(layerId)) layerId = layerIdConvertMap.get(layerId);
				conditionValueActual.put(key, layerId);
				logger.debug("layerId: "+layerId);
			}

			// 条件を反転するならtrue
			key = "isnot";
			if(conditionValueActual.has(key)) {
				isNot = conditionValueActual.getBoolean(key);
			}

			// TODO: グレーアウトの不透明度 0なら非表示
			key = "grayout";
			if(conditionValueActual.has(key)) {
				int grayout = conditionValueActual.getInt(key);
				logger.debug(lang.__("Grayout(no use)")+grayout);
			}

			// 入力範囲の空間検索範囲のバッファ（単位はm）
			key = "buffer";
			if(conditionValueActual.has(key)) {
				buffer = conditionValueActual.getDouble(key);
			}

			// 入力範囲の空間検索範囲の重なり条件
			// これは使わないはず
			key = "spatial";
			if(conditionValueActual.has(key)) {
				spatialType = conditionValueActual.getInt(key);
			}

			// 空間検索範囲レイヤ条件
			key = "spatiallayer";
			if(conditionValueActual.has(key)) {
				spatialLayer = conditionValueActual.getJSONArray(key);
				int length = spatialLayer.length();
				for(int idx=0; idx<length; idx++) {
					JSONObject spatialLayerJson = spatialLayer.getJSONObject(idx);
					String layer = spatialLayerJson.getString("layer");
					// レイヤIDがマスタマップのレイヤIDになっていれば変換
					if(layerIdConvertMap!=null && layerIdConvertMap.containsKey(layer)) {
						spatialLayerJson.put("layer", layerIdConvertMap.get(layer));
					}
					spatialType = spatialLayerJson.getInt("type");
				}
			}

			// 属性検索条件
			key = "condition";
			if(conditionValueActual.has(key)) {
				JSONArray condition = conditionValueActual.getJSONArray("condition");
				int length = condition.length();
				for(int i=0; i<length; i++) {
					JSONObject json = condition.getJSONObject(i);
					FeatureSearchCondition keyword = new FeatureSearchCondition(null, null, null, null);
					keyword.fromJSON(json);
					keywords.add(keyword);
				}
			}

			// 入力範囲一覧情報JSONの配列
			key = "geometry";
			if(conditionValueActual.has(key)) {
				JSONArray geometry = conditionValueActual.getJSONArray(key);
				logger.debug(lang.__("Geometry(no use)")+geometry);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(lang.__("An error occurred while converting search condition into object."));
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			throw new ServiceException(e);
		}

		// 検索対象のレイヤIDを取得
		if(layerId==null) {
			List<MenutableInfo> mtbllist = menutableInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
			List<TracktableInfo> ttbllist = new ArrayList<TracktableInfo>();
			for (MenutableInfo mtbl : mtbllist) {
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());
				if(ttbl != null)ttbllist.add(ttbl);
			}
			if(0<ttbllist.size()) {
				layerId = ttbllist.get(0).layerid;
			}
			if(StringUtil.isEmpty(layerId)) throw new ServiceException(lang.__("Retrieval object layer has not been set."));
		}

		// 検索対象のレイヤIDがマスタマップのレイヤIDになっていれば変換
		if(layerIdConvertMap!=null && layerIdConvertMap.containsKey(layerId)) layerId = layerIdConvertMap.get(layerId);
		// 検索対象のレイヤ情報
			LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
		vecLayerInfo.add(layerInfo);

		// フィルタは WMS.GetMap のリクエストパラメータに featureid パラメータにCSV形式で指定していた。
		// 時系列化により、FilteredFeatureId には _orgid がセットされるようになるが、
		// GeoServer へのリクエストでは featureid パラメータは gid である必要がある。
		// WMSAuthAction, PdfServlet では TimeSeriesType.History の場合、
		// featureidパラメータをcql_filterに変換するが、TimeSeriesTypeはキャッシュされるので、
		// 地図表示時にDBと同じタイプになっているかチェックする。
		// 検索対象レイヤの TimeSeriesType チェック
		if(layerInfo!=null) {
			LayerInfo.TimeSeriesType tst = LayerInfo.getTimeSeriesType(layerInfo.layerId);
			if(layerInfo.timeSeriesType.equals(tst)==false) {
				// キャッシュ更新
				LayerInfo.resetTimeSeriesType(layerId);
				LayerInfo.getTimeSeriesType(layerId);
			}
		}

		// 検索実行する
		int attrStatus = AttrInfo.STATUS_DEFAULT;
		int limit = 0;
		int offset = 0;
		boolean nameOnly = true;
		int resultGeomType = FeatureDB.GEOM_TYPE_CENTER; // 検索結果の地物形状の種類
		boolean desc = false;
		int rangeType = SessionGeometry.TYPE_CONDITION;
		Map<String, String> layerQueryWhere = new HashMap<String, String>();
		Vector<Long> filterdFeatureIds = new Vector<Long>();
		try {
			// sessionを渡すと、入力範囲のレイヤが追加されてしまい、SQL構文がおかしくなるため null を渡す.
			if(timeParam==null) timeParam = new Date[]{TimeUtil.newDate()};
			FeatureDB.searchFeatureSessionGeometry(null, mapId, vecLayerInfo, layerQueryWhere, attrStatus,
					keywords, limit, offset, nameOnly, resultGeomType, /*sortAttrId*/ null, desc, isNot,
					rangeType, spatialType, buffer, spatialLayer, filterdFeatureIds, /*attach*/null, timeParam);
		} catch (Exception e) {
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			throw new ServiceException(e);
		}

		// 結果をセッションに保存する
		FilteredFeatureId.setFilteredFeatureId(session, String.valueOf(filterkey), filterdFeatureIds);

		/*
		// _orgid が返ってくるので、gid に変換しなおす
		{
			// WMS.GetMap でフィルター指定する featureid は _orgid ではなく gid で指定する必要があるため
			Vector<Long> gids = new Vector<Long>();
			Timestamp time = new Timestamp(System.currentTimeMillis());
			for(Long orgid : filterdFeatureIds) {
				gids.add(ExMapDB.getHistoryFeatureId(layerId, orgid, new Date[]{time}));
			}

			// 結果をセッションに保存する
			FilteredFeatureId.setFilteredFeatureId(session, String.valueOf(filterkey), gids);

			// _orgid が返ってくるので、gid に変換しなおす
			//filterdFeatureIds = gids;
		}
		*/

		// フィルター解除の場合
		if(nofilter) {
			// 属性検索をクリア
    		loginDataDto.getMenuConditionMap().remove(menuid);
        	loginDataDto.getMenuSearchDatas().put(menuid, /*saveDatas*/null);
        	
        	// 結果をクリア
			FilteredFeatureId.removeFilteredFeatureId(session, String.valueOf(menuid));
		}

		// 処理結果を返す
		FilterDto filterDto = new FilterDto();
		filterDto.setLayerId(layerId);
		filterDto.setFilterkey(String.valueOf(filterkey));
		filterDto.setFilteredFeatureIds(filterdFeatureIds);
		filterDto.setSpatialLayer(spatialLayer);
		filterDto.setConditionValue(conditionValue);
		filterDto.setConditionValueActual(conditionValueActual);
		filterDto.setTotal(tableService.getCount(layerId, timeParam));

		return filterDto;
	}

	/**
	 * マスタマップにあるレイヤIDから災害用マップのレイヤIDの対応マップを取得する.
	 * @param trackmapinfoid 記録地図情報ID
	 * @return マスタマップにあるレイヤIDから災害用マップのレイヤIDの対応マップ
	 */
	public Map<String, String> getLayerIdConvertMap(long trackmapinfoid) {
		// 災害用テーブル情報リストを取得
		List<TracktableInfo> tracktableInfos = tracktableInfoService.findByTrackmapInfoId(trackmapinfoid);
		// 災害用テーブル情報リストからレイヤID変換対応マップを作成
		Map<String, String> layerIdConvertMap = new HashMap<String, String>();
		for(TracktableInfo tracktableInfo : tracktableInfos) {
			String src = tracktableInfo.tablemasterInfo.layerid;
			if(StringUtil.isNotEmpty(src)) {
				String dst = tracktableInfo.layerid;
				layerIdConvertMap.put(src, dst);
				logger.trace(" "+src+" -> "+dst);
			}
		}
		logger.debug("LayerIdConvertMap(trackmapinfoid="+trackmapinfoid+"): "+layerIdConvertMap.toString());
		return layerIdConvertMap;
	}

	/**
	 * @param mapId 地図ID
	 * @param layerId レイヤID
	 * @return SLD
	 */
	public String getSld(long mapId, String layerId) {
		String sld = null;
		ResourceBundle pathBundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
		String localRootURL = pathBundle.getString("LOCAL_ROOT_URL");
		String ecommapURL = localRootURL+"/map";

		try {
			String ecommapSldUrl = ecommapURL+"/sld?mid="+mapId+"&layer="+layerId;
			String method = "GET";
			String contentType = "text/plain; charset=utf-8";
			boolean useCaches = true;
			HttpURLConnection http = HttpUtil.httpProxy(ecommapSldUrl, method, contentType, null, useCaches);
			sld = IOUtils.toString(http.getInputStream(), "UTF-8");
			logger.debug(sld);

			// サーバのローカルになっているものをサーバ名に直す
			String regex = "http:\\/\\/localhost(:\\d*)?";
			String server = ecommapURL.substring(0,ecommapURL.indexOf("/map"));
			String replacement = server;
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sld);
			while(m.find()){System.out.println(m.start()+"/"+m.end()+": "+sld.substring(m.start(),m.end()));}
			sld = m.replaceAll(replacement);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return sld;
	}

	/**
	 * 一括変更対象項目とグルーピングの初期化データを追加する.
	 * @param slimerDto 一括変更Dto
	 * @param layerInfo
	 * @param attr
	 * @param editable 一括変更可フラグ
	 * @param grouping 一括変更グループフラグ
	 */
	public void addEcomColumn(SlimerDto slimerDto, LayerInfo layerInfo, AttrInfo attr, boolean editable, boolean grouping, boolean defaultcheck, boolean groupdefaultcheck, boolean addable) {
		String layerId = layerInfo.layerId;
		if(attr != null) {
			String dataType = "String";
			Map<String, String> selectOptions = null;
			String checkDisplay = null;
			switch(attr.dataType) {
			case AttrInfo.DATATYPE_SELECT: dataType = "Select";
			selectOptions = new LinkedHashMap<String, String>();
				// JSON配列文字列なら、キーと値をJSONから取得
				try {
					JSONArray dataExps = new JSONArray(attr.dataExp);
					for(int idx=0; idx<dataExps.length(); idx++) {
						JSONObject json = dataExps.getJSONObject(idx);
						@SuppressWarnings("unchecked")
						Iterator<String> it = json.keys();
						while(it.hasNext()) {
							String key = it.next();
							selectOptions.put(json.getString(key), key);
						}
					}
					break;
				} catch(JSONException e) { }
				//eコミマップではSelectの選択肢と値は同じ
				String[] selectItems = attr.dataExp.split(",");
				for(String selectItem : selectItems) selectOptions.put(selectItem, selectItem);
				break;
			case AttrInfo.DATATYPE_CHECKBOX: dataType = "Checkbox";
				checkDisplay = attr.dataExp;
				break;
			case AttrInfo.DATATYPE_INTEGER:  dataType="Number"; break;
			case AttrInfo.DATATYPE_FLOAT:    dataType="Float"; break;
			case AttrInfo.DATATYPE_DATE:     dataType="Date"; break;
			case AttrInfo.DATATYPE_DATETIME: dataType="DateTime"; break;
			case AttrInfo.DATATYPE_TEXT:
				dataType = "String";
				if (attr.length > 20)
					dataType = "TextArea";
				break;
			case AttrInfo.DATATYPE_URL:
				dataType = "Link";
				break;
			default: dataType = "String"; break;
			}

			// 一括変更対象項目
			slimerDto.addColumn(layerId, attr.name, attr.attrId, dataType, selectOptions, checkDisplay, editable, attr.nullable, defaultcheck , groupdefaultcheck , addable);
			String key = "gid";
			LayerInfo linfo = MapDB.getMapDB().getLayerInfo(layerId);
			if(linfo!=null) key = LayerInfo.TimeSeriesType.HISTORY.equals(linfo.timeSeriesType) ? "_orgid" : "gid";
			slimerDto.setKey(layerId, key);

			// 一括変更グループ
			if(grouping) {
				List<String> groupList = tableService.selectDistinct(layerId, attr.attrId);
				Map<String, String> gSelectOptions = new LinkedHashMap<String, String>();
				for(String groupValue : groupList) {
					if(StringUtil.isNotEmpty(groupValue))
						gSelectOptions.put(groupValue.replace('\n', ' '), groupValue.replace('\n', ' '));
				}
				slimerDto.addGrouping(layerId, attr.name, attr.attrId, dataType, gSelectOptions, groupdefaultcheck);
			}
		}
	}

	/**
	 * eコミマップ以外の災害対応システムのデータを取得します.
	 * @param layerId レイヤID
	 * @param fid フィーチャID
	 * @return JSONObject なければ null
	 */
	protected JSONObject getExternalData(String layerId, long fid){
		// assert
		if(StringUtil.isEmpty(layerId)) return null;

		TablemasterInfo tablemasterInfo = tablemasterInfoService.findByLayerId(layerId);
		if(tablemasterInfo==null) return null;

		ExternalJSONFactory factory = new ExternalJSONFactory();

		try{
			BeanMap condition = new BeanMap();
			condition.put(ObservatoryriverInfoNames.tablemasterinfoid().toString(), tablemasterInfo.id);
			condition.put(ObservatoryriverInfoNames.featureid().toString(), fid);

			//水位観測所
			{
				List<ObservatoryriverInfo> result = observatoryriverInfoService.findByCondition(condition);
				for(ObservatoryriverInfo info : result) {
					if(info==null) continue;
					if(StringUtil.isNotEmpty(info.url)) {
						if(info.iframe==true) return factory.linkButton(lang.__("Detail"), info.url, true).toJSON();
						else return factory.linkButtonBlank(lang.__("Detail"), info.url, true).toJSON();
					}
					if(info.id!=null) {
						String url = SpringContext.getRequest().getContextPath()+"/observ/river/"+info.id+"?time=";
						if(info.iframe==true) return factory.linkButton(lang.__("Detail"), url, true).toJSON();
						else return factory.linkButtonBlank(lang.__("Detail"), url, true).toJSON();
					}
				}
			}
			//雨量観測所
			{
				List<ObservatoryrainInfo> result = observatoryrainInfoService.findByCondition(condition);
				for(ObservatoryrainInfo info : result) {
					if(info==null) continue;
					if(StringUtil.isNotEmpty(info.url)) {
						if(info.iframe==true) return factory.linkButton(lang.__("Detail"), info.url, true).toJSON();
						else return factory.linkButtonBlank(lang.__("Detail"), info.url, true).toJSON();
					}
					if(info.id!=null) {
						String url = SpringContext.getRequest().getContextPath()+"/observ/rain/"+info.id+"?time=";
						if(info.iframe==true) return factory.linkButton(lang.__("Detail"), url, true).toJSON();
						else return factory.linkButtonBlank(lang.__("Detail"), url, true).toJSON();
					}
				}
			}
			//ダム
			{
				List<ObservatorydamInfo> result = observatorydamInfoService.findByCondition(condition);
				for(ObservatorydamInfo info : result) {
					if(info==null) continue;
					if(StringUtil.isNotEmpty(info.url)) {
						if(info.iframe==true) return factory.linkButton(lang.__("Detail"), info.url, true).toJSON();
						else return factory.linkButtonBlank(lang.__("Detail"), info.url, true).toJSON();
					}
					if(info.id!=null) {
						String url = SpringContext.getRequest().getContextPath()+("/observ/dam/"+info.id+"?time=");
						if(info.iframe==true) return factory.linkButton(lang.__("Detail"), url, true).toJSON();
						else return factory.linkButtonBlank(lang.__("Detail"), url, true).toJSON();
					}
				}
			}

		} catch(JSONException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * テーブルマスターIDより属性情報を取得する
	 * @param tablemasterId
	 * @return 属性情報リスト
	 * @throws Exception
	 */
	public List<AttrInfo> getAttrInfoFromTablamasterId(String tablemasterId) throws Exception {
		TablemasterInfo tablemasterInfo = tablemasterInfoService.findByNotDeletedId(Long.valueOf(tablemasterId));
		ArrayList<AttrInfo> result = new ArrayList<AttrInfo>();
		// eコミマップレイヤ
		if(StringUtil.isNotEmpty(tablemasterInfo.layerid)) {
			JSONObject attrInfo = getAttrInfo(tablemasterInfo.layerid);
			if (attrInfo != null) {
				JSONArray array = attrInfo.getJSONArray("attrInfos");
				for (int i = 0; i < array.length(); i++) {
					AttrInfo attr = AttrInfo.fromJSON(array.getJSONObject(i));
					result.add(attr);
				}
			}
		}
		// システムテーブル
		else {
			Class<?> entityClass = tableService.getEntity(tablemasterInfo.tablename);
			if(entityClass!=null) {
				for(Field field : entityClass.getFields()) {
					// とりあえず管理画面の属性ID選択で必要なのは上記まで。
					String attrId = field.getName();
					String name = field.getName();
					// 下記のプロパティはとりあえず決め打ち
					short status = AttrInfo.STATUS_DEFAULT;
					int length = 20;
					int maxLength = 20;
					int dataType = AttrInfo.DATATYPE_TEXT;
					String dataExp = null;
					boolean nullable = false;
					AttrInfo attr = new AttrInfo(attrId, name, status, length, maxLength, dataType, dataExp, nullable);
					result.add(attr);
				}
			}
		}
		return result;
	}

	/**
	 * 属性のステータスを取得します.
	 * @param layerId レイヤID
	 * @return 属性のステータス
	 */
	public LinkedHashMap<String, Short> getAttrStatus(String layerId) {
		LinkedHashMap<String, Short> attrStatus = new LinkedHashMap<String, Short>();
		if(StringUtil.isNotEmpty(layerId)) {
			LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
			if(layerInfo!=null) {
				for(AttrInfo attrInfo : layerInfo.getAttrIterable()) {
					attrStatus.put(attrInfo.attrId, attrInfo.status);
				}
			}
		}
		return attrStatus;
	}


	/**
	 * 災害対応システムの情報をJSONで作成するクラスです.
	 */
	public static class ExternalJSONFactory{

		/** デフォルトコンストラクタ */
		public ExternalJSONFactory(){}

		/** ExtJSのボタン配列 */
		JSONArray buttons = new JSONArray();

		/**
		 * IFrameでリンク先を表示するボタンを追加します.
		 * @param text ボタンラベル
		 * @param url リンクURL
		 * @param open TODO
		 * @return ExternalJSONFactory カリー化
		 * @throws JSONException
		 */
		public ExternalJSONFactory linkButton(final String text, final String url, final boolean open) throws JSONException{
			buttons.put(new JSONObject(){{
				put("type", "link");
				put("text", text);
				put("url", url);
				put("open", open);
				put("TB_iframe", true);
			}});
			return this;
		}

		/**
		 * ポップアップ(target=_blank)でリンク先を表示するボタンを追加します.
		 * @param text
		 * @param url
		 * @return ExternalJSONFactory カリー化
		 * @throws JSONException
		 */
		public ExternalJSONFactory linkButtonBlank(final String text, final String url) throws JSONException{
			return linkButtonBlank(text, url, false);
		}

		/**
		 * ポップアップ(target=_blank)でリンク先を表示するボタンを追加します.
		 * @param text
		 * @param url
		 * @param open デフォルトでリンク先を開くかどうか
		 * @return ExternalJSONFactory カリー化
		 * @throws JSONException
		 */
		public ExternalJSONFactory linkButtonBlank(final String text, final String url, final boolean open) throws JSONException{
			buttons.put(new JSONObject(){{
				put("type", "link");
				put("text", text);
				put("url", url);
				put("open", open);
			}});
			return this;
		}

		/**
		 * このインスタンスをJSONに変換します.
		 * @return JSONObject
		 * @throws JSONException
		 */
		public JSONObject toJSON() throws JSONException{
			JSONObject json = new JSONObject(){{
				// Button
				put("toolbar", new JSONObject(){{
					put("buttons", buttons);
				}});
			}};
			return json;
		}
	}
}
