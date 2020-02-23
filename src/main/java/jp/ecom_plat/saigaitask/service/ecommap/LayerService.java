/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.ecommap;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.AuthInfo;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.LayerGroupInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.file.SLDFile;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.JdbcManager;

/**
 * eコミマップのレイヤ操作を簡単に操作するためにラッパしたサービスクラス
 */
@org.springframework.stereotype.Service
public class LayerService {

	/** ログイン情報 */
	@Resource
	protected LoginDataDto loginDataDto;

	@Resource
	protected JdbcManager jdbcManager;

    //@Resource
    //protected EcommapInfoService ecommapInfoService;

    //@Resource
    //protected SessionService sessionService;

	Logger logger = Logger.getLogger(this.getClass());
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

    private static String LAYER_INSERT = "/geoserver/layerinsert?";
    //private static String LAYER_UPDATE = "/geoserver/layerupdate?";

	public String create(String euser, int cid, int layerGroupId, long mapId, String name, String description, String geomType, File defaultSldFile, String[] names, Integer[] types, String[] selects, Integer[] sizes)
	{
		try {
	        MapDB mapDB = MapDB.getMapDB();

			UserInfo userInfo = mapDB.getAuthIdUserInfo(euser);

			MapInfo mapInfo = mapDB.getMapInfo(mapId);

			//権限チェック
			if (!AuthInfo.authMapEdit(userInfo, cid, mapInfo)) {
				System.out.println(lang.__("No permissions  to edit map."));
				return null;
			};

			//パラメータチェック
			//レイヤ種別
			short type = LayerInfo.TYPE_LOCAL;

			//地図のグループへのレイヤ追加権限チェック
			if (type == LayerInfo.TYPE_LOCAL && !AuthInfo.authLayerCreate(userInfo, cid, layerGroupId)) {
				System.out.println(lang.__("No permissions  to create layer."));
				return null;
			};

			MapLayerInfo mapLayerInfo = null;
			String layerId;
			LayerInfo newLayerInfo;

			short layerGroupType = LayerInfo.getGroupType(type);

			//パラメータ読み込み
			boolean visible = true;//初期表示
			float opacity = 1.0f;//透明度
			boolean closed = true;//初期折りたたむ
			short searchType = MapLayerInfo.SEARCH_ALL;

			String attribution = "_blank";
			//String featuretypeId = null;
			//String wfsCapabilitiesURL = null;
			//String wfsURL = null;
			//String wmsCapabilitiesURL = null;
			//String wmsURL = null;
			//String wmsFormat = null;
			//String wmsLegendURL = null;
			//String wmsFeatureURL = null;
			//String wcsCapabilitiesURL = null;
			//String wcsURL = null;
			int scale = 0;
			int maxScale = 0;

			layerId = mapDB.createFeatureTableName(LayerInfo.getLayerPrefix(mapId, layerGroupType));
			//新規作成
			//newLayerInfo = new LayerInfo(layerId, name, description, LayerInfo.STATUS_DEFAULT, type, userInfo.userId, mapId, attribution,
			//		null/*featuretypeId*/, null/*wfsCapabilitiesURL*/, null/*wfsURL*/, null/*wmsCapabilitiesURL*/, null/*wmsURL*/,
			//		null/*wmsFormat*/, null/*wmsLegendURL*/, null/*wmsFeatureURL*/, null/*wcsCapabilitiesURL*/, null/*wcsURL*/,
			//		null, null, null, scale, maxScale, 0/*maxZoomLevel*/, null/*maxPaperSize*/, null/*params*/);
			newLayerInfo = new LayerInfo(layerId, name, description, LayerInfo.STATUS_DEFAULT, type, userInfo.userId, mapId, attribution, layerId,
					null/*wfsCapabilitiesURL*/, null/*wfsURL*/, null/*wmsCapabilitiesURL*/, null/*wmsURL*/, null/*wmsFormat*/, null/*wmsLegendURL*/,
					null/*wmsFeatureURL*/, null/*wcsCapabilitiesURL*/, null/*wcsURL*/, null, null, null, TimeSeriesType.NONE, scale, maxScale, 0, null, null);

			//属性項目追加
			for (int i=0; i<names.length; i++) {
				AttrInfo attrInfo = new AttrInfo("attr"+i,
						names[i],
						AttrInfo.STATUS_DEFAULT,
						sizes[i],
						0,
						types[i],
						(selects[i]!=null&&selects[i].length()>0)?selects[i]:"",
						true);
					newLayerInfo.addAttrInfo( attrInfo );
			}

			//_layerにLayerInfoの情報追加
			mapDB.insertLayerInfo(newLayerInfo);

			//地図情報に追加
			MapLayerInfo newMapLayerInfo = new MapLayerInfo(newLayerInfo.layerId, newLayerInfo.name, newLayerInfo.type, null/*parent*/, scale, maxScale, null/*attrNameId*/);
			newMapLayerInfo.visible = visible;
			newMapLayerInfo.opacity = opacity;
			newMapLayerInfo.closed = closed;

			mapDB.insertMapLayerInfo(mapInfo, newMapLayerInfo);
			mapDB.updateMapInfoModified(mapInfo, new Timestamp(System.currentTimeMillis()));
			mapDB.updateLayerInfo(newLayerInfo);

			if (type == LayerInfo.TYPE_LOCAL) {
				mapDB.createGeometryTable(newLayerInfo, geomType);
				//mapDB.createGeoServerFeatureFile(newLayerInfo.layerId, newLayerInfo.name);
				//if (mapId != 0) {
					/*boolean gret = */
				mapDB.createGeoServerFeature(newLayerInfo.layerId);
					//if (!gret) {
					//	System.out.println(lang.__("Unable to connect GeoServer"));
					//	return null;
					//}
					//session.setAttribute("reload_geoserver", "true");
				//}
				//共通SLDにデフォルトSLDを設定
				try {
					//sendSldFile(mapInfo.communityId, mapInfo.mapId, layerId, defaultSldFile);
					//defsldfile=/usr/local/apache-tomcat-6/webapps/SaigaiTask/files/styles/disasterstatus.sld
					//sldfile=/usr/local/apache-tomcat-6.0.35/webapps/SaigaiTask/WEB-INF/webapps/map/files/styles/default/default.sld
					//defsldfile=/usr/local/apache-tomcat-6/webapps/SaigaiTask/files/styles/task.sld
					//sldfile=/usr/local/apache-tomcat-6.0.35/webapps/SaigaiTask/WEB-INF/webapps/map/files/styles/default/default.sld

					//File sldFile = SLDFile.getCommunitySldFile(cid, layerId);
					//FileUtils.copyFile(defaultSldFile, sldFile);
					if (defaultSldFile == null) {
						SLDFile.copyMapToCommunitySldFile(cid, mapId, layerId, cid, layerId, true);
					}
				} catch (Exception e) {
					if (loginDataDto != null)
						logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
					logger.error("",e);
					throw new ServiceException(e);
				}
			}
			mapLayerInfo = mapInfo.getMapLayerInfo(layerId);
			mapLayerInfo.visible = visible;
			mapLayerInfo.opacity = opacity;
			mapLayerInfo.searchType = searchType;

			//グループ管理権限追加
			LayerGroupInfo.insert(cid, layerGroupId, layerId, LayerGroupInfo.SHARE_ALL);

			// 時系列化
			newLayerInfo.timeSeriesType = TimeSeriesType.HISTORY;
			mapDB.setTimeSeriesType(newLayerInfo);
			mapDB.updateLayerInfo(newLayerInfo);
			//mapDB.createGeoServerFeature(newLayerInfo.layerId);

			return layerId;
		} catch (Exception e) {
			if (loginDataDto != null)
				logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("",e);
			throw new ServiceException(e);
		}
		//return null;
	}

	public boolean createGeoServerFeature(String featureType) throws MalformedURLException, IOException
	{
		//EcommapInfo ecomInfo = ecommapInfoService.find();
		//String localRootURL = ecomInfo.getUrl();
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String localRootURL = rb.getString("ECOMIMAPURL");

		//レイヤ追加URL取得 (GeoServer2.1.1用はPathInfo.propertiesで設定)
		String layerRegister = LAYER_INSERT;

		String url = localRootURL+layerRegister+"featuretype="+MapDB.GEOSERVER_DATA_STORE+":"+featureType;
		System.out.println("createGeoServerFeature:"+url);

		HttpURLConnection loadConn = (HttpURLConnection)(new URL(url).openConnection());
		int code = loadConn.getResponseCode();
		System.out.println("createGeoServerFeature: code="+code);
		loadConn.disconnect();

		if (code >= 400) {
			//再接続
			loadConn = (HttpURLConnection)(new URL(url).openConnection());
			code = loadConn.getResponseCode();
			System.out.println("createGeoServerFeature: code="+code);
			loadConn.disconnect();
			if (code >= 400) {
				return false;
			}
		}
		return true;
	}

	/**
	 * レイヤIDで検索
	 * @param layerId レイヤID
	 * @return レイヤ情報
	 */
	public LayerInfo getLayerInfo(String layerId)
	{
		MapDB mapDB = MapDB.getMapDB();

		return mapDB.getLayerInfo(layerId);
	}

	/**
	 * 指定種別のレイヤを一括共有設定する
	 * @param mastermapid
	 * @param trackmapid
	 * @param groupType
	 * @return 一括共有設定をしたレイヤのMapLayerInfoリスト
	 * @throws Exception
	 */
	public List<MapLayerInfo> shareMapLayer(long mastermapid, long trackmapid, short groupType) throws Exception {
		List<MapLayerInfo> mapLayerInfos = new ArrayList<MapLayerInfo>();
		MapDB mapDB = MapDB.getMapDB();

		MapInfo tmapInfo = mapDB.getMapInfo(trackmapid);
		MapInfo pmapInfo = mapDB.getMapInfo(mastermapid);
		int sz = pmapInfo.countMapLayerInfo();
		for (int i=0; i<sz; i++) {

			MapLayerInfo mapLayerInfo = pmapInfo.getMapLayerInfo(i);
			LayerInfo layerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId, true);
			if (layerInfo.getGroupType() != groupType)
				continue;

			//共有
			mapDB.shareLayer(tmapInfo, pmapInfo.communityId, pmapInfo.mapId, layerInfo.layerId);

			//更新日時変更
			mapDB.updateMapInfoModified(tmapInfo, new Timestamp(System.currentTimeMillis()));

			mapLayerInfos.add(mapLayerInfo);
		}

		return mapLayerInfos;
	}

	/**
	 * 指定レイヤを共有設定する
	 * @param mastermapid
	 * @param trackmapid
	 * @param layerid
	 * @throws Exception
	 */
	public void shareMapLayer(long mastermapid, long trackmapid, String layerid) throws Exception {
		MapDB mapDB = MapDB.getMapDB();

		MapInfo tmapInfo = mapDB.getMapInfo(trackmapid);
		MapInfo pmapInfo = mapDB.getMapInfo(mastermapid);
		MapLayerInfo mapLayerInfo = pmapInfo.getMapLayerInfo(layerid);
		if (mapLayerInfo == null) return;
		LayerInfo layerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId, true);
		if (layerInfo == null) return;

		//共有
		mapDB.shareLayer(tmapInfo, pmapInfo.communityId, pmapInfo.mapId, layerInfo.layerId);
		logger.debug("shareMapLayer (mid=" + pmapInfo.mapId + ", layerId="+layerInfo.layerId + ") to mid="+tmapInfo.mapId);

		//更新日時変更
		mapDB.updateMapInfoModified(tmapInfo, new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * 指定レイヤをコピーする
	 * @param mastermapid
	 * @param trackmapid
	 * @param layerid
	 * @param authId
	 * @param copy コピーフラグ.
	 * 0: コピーしない（レイヤの引用）
	 * 1: レイヤ複製、最新のみコピーする（デフォルト）
	 * 2: レイヤ複製、すべてコピーする
	 * 3: レイヤ複製、地物なし
	 * @return レイヤID
	 * @throws Exception
	 */
	public String cloneMapLayer(long mastermapid, long trackmapid, String layerid, String authId, Short copy) throws Exception {

		if(copy==null) copy = TablemasterInfo.COPY_LATEST;

		if(TablemasterInfo.COPY_SHARE.equals(copy)) {
			shareMapLayer(mastermapid, trackmapid, layerid);
			return layerid;
		}

		MapDB mapDB = MapDB.getMapDB();

		MapInfo tmapInfo = mapDB.getMapInfo(trackmapid);
		MapInfo pmapInfo = mapDB.getMapInfo(mastermapid);
		//ユーザの取得
    	UserInfo user = mapDB.getAuthIdUserInfo(authId);

    	String layerId = null;
    	boolean cloneFeature = !copy.equals(TablemasterInfo.COPY_NOFEATURE);
    	MapLayerInfo mlay = pmapInfo.getMapLayerInfo(layerid);
    	if (mlay != null)
    		layerId = mapDB.cloneLayer(tmapInfo.communityId, tmapInfo.groupId, tmapInfo, pmapInfo.communityId, pmapInfo.mapId, layerid, user.userId, cloneFeature);

    	int userId = 0; //"admin";
    	Timestamp created = new Timestamp(System.currentTimeMillis());
    	boolean clearModified = true;
    	if(cloneFeature) FeatureDB.updateFeatureCreated(layerId, userId, created, clearModified); // 作成者などをリセット

    	// 最新のみコピーの場合は
    	if(TablemasterInfo.COPY_LATEST.equals(copy)) {
    		LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
    		if(layerInfo.timeSeriesType==LayerInfo.TimeSeriesType.HISTORY) {
    			// timeFrom は null にして、最終更新日時をそのままコピー
        		//FeatureDB.deleteDeletedFeatureHistory(layerInfo, /*timeFrom*/ null);

    			// timeFrom を現在時刻で更新
        		FeatureDB.deleteDeletedFeatureHistory(layerInfo, new Timestamp(TimeUtil.newDate().getTime()).toString());
    		}
    	}

		//更新日時変更
		mapDB.updateMapInfoModified(tmapInfo, new Timestamp(System.currentTimeMillis()));

		return layerId;
	}
}
