/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.ecommap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.GroupInfo;
import jp.ecom_plat.map.db.LayerGroupInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.ListSearchConditionInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.extension.jdbc.JdbcManager;

/**
 * eコミマップの情報を簡単に取得するためにラッパしたサービスクラス
 */
@org.springframework.stereotype.Service
public class EcommapDataGetService {
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	@Resource
	protected HttpSession session;

	@Resource
	protected JdbcManager jdbcManager;

	private MapDB mapDB;

	Logger logger = Logger.getLogger(EcommapDataGetService.class);

	/**
	 * コミュニティIDリストの取得
	 * @return コミュニティIDと名前のマップ
	 */
	public Map<Integer,String> getCommunityIds(){
		MapDB mapDB = getMapDB();
		Map<Integer,String> retVal = new LinkedHashMap<Integer, String>();

		List<CommunityInfo> communities = new ArrayList<CommunityInfo>();
		try{
			communities= mapDB.searchCommunityInfo("community_id", true);
		}catch(Exception e){
			e.printStackTrace();
		}

		for(CommunityInfo community : communities){
			String value = community.name;
			retVal.put(community.communityId, value);
		}

		return retVal;
	}


	/**
	 * コミュニティ名の取得
	 * @return コミュニティ名
	 */
	public String getCommunityName(int communityId){

		MapDB mapDB = getMapDB();
		String retVal = null;

		try{
			CommunityInfo communityInfo = mapDB.getCommunityInfo(communityId);
			retVal = communityInfo.name;
		}catch(Exception e){
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * グループ名の取得
	 * @return グループ名
	 */
	public String getGroupName(int communityId, int groupId){

		String retVal = null;
		try{
			GroupInfo communityGroupInfo = null;
			communityGroupInfo = GroupInfo.getCommunityGroupInfo(communityId);
			if(communityGroupInfo.groupId == groupId){
				retVal =  communityGroupInfo.name;

			}else{
				GroupInfo groupInfo = GroupInfo.getGroupInfo(groupId);
				retVal = groupInfo.name;
			}
		}catch(SQLException se){
			se.printStackTrace();
		}


		return retVal;
	}


	/**
	 * 地図名の取得
	 * @return 地図名
	 */
	public String getMapName(long mapId){

		MapDB mapDB = getMapDB();
		String retVal = null;

		try{
			MapInfo mapInfo = mapDB.getMapInfo(mapId);
			retVal = mapInfo.title;
		}catch(Exception e){
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * グループIDリストの取得
	 * @return グループIDと名前のマップ
	 */
	public Map<Integer,String> getGroupInfos(int communityId){

		GroupInfo communityGroupInfo = null;
		List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
		Map<Integer,String> retVal = new LinkedHashMap<Integer, String>();

		try{
			communityGroupInfo = GroupInfo.getCommunityGroupInfo(communityId);
			groupInfos = GroupInfo.getGroupInfoList(communityId);
		}catch(SQLException se){
			se.printStackTrace();
		}

		if(communityGroupInfo != null){
			String value =  communityGroupInfo.name;
			retVal.put(communityGroupInfo.groupId, value);
		}
		for(GroupInfo groupInfo : groupInfos){
			String value = groupInfo.name;
			retVal.put(groupInfo.groupId, value);
		}

		return retVal;
	}


	/**
	 * 地図IDリストの取得
	 * @return 地図IDと名前のマップ
	 */
	public Map<Long, String> getMapInfo(UserInfo userInfo,  int communityId, int groupId){
		MapDB mapDB = getMapDB();
		Map<Long,String> retVal = new LinkedHashMap<Long, String>();

		try{
			int level = mapDB.getGroupUserLevel(userInfo, communityId, groupId);
			if (level >= UserInfo.LEVEL_EDITOR) {
				List<MapInfo> mapInfos =  mapDB.getCommunityMapInfo(userInfo, MapInfo.STATUS_DEFAULT, communityId, groupId, null);
				for(MapInfo mapInfo : mapInfos){
					String value = mapInfo.title;
					retVal.put(mapInfo.mapId, value);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return retVal;
	}



	/**
	 * レイヤIDリストの取得
	 * @return レイヤIDと名前のマップ
	 */
	public Map<String,String> getLayers(int communityId, int groupId){
		MapDB mapDB = getMapDB();
		Map<String,String> retVal = new HashMap<String, String>();
		try {
			List<LayerInfo> layerInfos = mapDB.getGroupLayers(
				communityId,
				groupId,
				LayerInfo.TYPE_LOCAL,
				LayerGroupInfo.SHARE_NONE,
				LayerInfo.STATUS_DEFAULT
			);

			for(LayerInfo layerInfo : layerInfos){
				String value = layerInfo.attribution  + "|"
						+ layerInfo.created + "|"
						+ layerInfo.description + "|"
						+ layerInfo.featuretypeId + "|"
						+ LayerInfo.GROUPTYPE_BASE + "|"
						+ LayerInfo.GROUPTYPE_KML + "|"
						+ LayerInfo.GROUPTYPE_LOCAL + "|"
						+ LayerInfo.GROUPTYPE_OVERLAY + "|"
						+ LayerInfo.GROUPTYPE_REFERENCE + "|"
						+ layerInfo.layerId  + "|"
						+ layerInfo.maxScale  + "|"
						+ layerInfo.metadataURL  + "|"
						+ layerInfo.modified  + "|"
						+ layerInfo.name  + "|"
						+ layerInfo.ownerId  + "|"
						+ layerInfo.ownerMapId   + "|"
						+ layerInfo.scale   + "|"
						+ LayerInfo.SHARE_ALL   + "|"
						+ LayerInfo.SHARE_INSERT   + "|"
						+ LayerInfo.SHARE_NONE    + "|"
						+ LayerInfo.SHARE_READ   + "|"
						+ layerInfo.shareType    + "|"
						+ layerInfo.status    + "|"
						+ LayerInfo.STATUS_DEFAULT   + "|"
						+ LayerInfo.STATUS_DELETED   + "|"
						+ LayerInfo.STATUS_DRAFT    + "|"
						+ LayerInfo.STATUS_FIXED    + "|"
						+ LayerInfo.STATUS_FIXGEOM    + "|"
						+ layerInfo.timeFrom    + "|"
						+ layerInfo.timeTo    + "|"
						+ layerInfo.type     + "|"
						+ LayerInfo.TYPE_BASE_CDS     + "|"
						+ LayerInfo.TYPE_BASE_GOOGLE    + "|"
						+ LayerInfo.TYPE_BASE_GROUP     + "|"
						+ LayerInfo.TYPE_BASE_KAMAPCACHE    + "|"
						+ LayerInfo.TYPE_BASE_OSM    + "|"
						+ LayerInfo.TYPE_BASE_TILECACHE    + "|"
						+ LayerInfo.TYPE_BASE_TILED    + "|"
						+ LayerInfo.TYPE_BASE_WEBTIS     + "|"
						+ LayerInfo.TYPE_BASE_WMS    + "|"
						+ LayerInfo.TYPE_BASE_WMS_SINGLE     + "|"
						+ LayerInfo.TYPE_BASE_XYZ     + "|"
						+ LayerInfo.TYPE_GEORSS    + "|"
						+ LayerInfo.TYPE_KML     + "|"
						+ LayerInfo.TYPE_LOCAL     + "|"
						+ LayerInfo.TYPE_LOCAL_GROUP     + "|"
						+ LayerInfo.TYPE_OVERLAY_CDS     + "|"
						+ LayerInfo.TYPE_OVERLAY_GROUP    + "|"
						+ LayerInfo.TYPE_OVERLAY_OSM     + "|"
						+ LayerInfo.TYPE_OVERLAY_TILECACHE     + "|"
						+ LayerInfo.TYPE_OVERLAY_TILED     + "|"
						+ LayerInfo.TYPE_OVERLAY_WMS    + "|"
						+ LayerInfo.TYPE_OVERLAY_WMS_SINGLE    + "|"
						+ LayerInfo.TYPE_REFERENCE     + "|"
						+ LayerInfo.TYPE_REFERENCE_GROUP    + "|"
						+ LayerInfo.TYPE_REFERENCE_WMS    + "|"
						+ layerInfo.wcsCapabilitiesURL     + "|"
						+ layerInfo.wcsURL     + "|"
						+ layerInfo.wfsCapabilitiesURL     + "|"
						+ layerInfo.wfsURL    + "|"
						+ layerInfo.wmsCapabilitiesURL     + "|"
						+ layerInfo.wmsFeatureURL     + "|"
						+ layerInfo.wmsFormat     + "|"
						+ layerInfo.wmsLegendURL     + "|"
						+ layerInfo.wmsURL;
				retVal.put(layerInfo.layerId, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}


	/**
	 * ある地図で使用済みのレイヤ情報・属性情報の取得
	 * テーブルマスター情報へセットするデータ取得用。
	 * @return レイヤ情報と文字列配列(属性ID)のマップ
	 */
	public Map<LayerInfo, String []> getMapUsedLayersforTablemasterInfo(int communityId, int groupId, long mapId){
		MapDB mapDB = getMapDB();
		Map<LayerInfo, String []> retVal = new LinkedHashMap<LayerInfo, String[]>();
		try {
			// グループに所属しているレイヤを取得
			List<LayerInfo> groupLayerInfos = mapDB.getGroupLayers(
				communityId,
				groupId,
				LayerInfo.TYPE_LOCAL,
				LayerGroupInfo.SHARE_NONE,
				LayerInfo.STATUS_DEFAULT
			);

			// レイヤがマップで使用されているかチェック
			for(LayerInfo groupLayerInfo : groupLayerInfos){
				List<MapInfo> mapInfos = mapDB.getLayerUsedMapInfo(groupLayerInfo.layerId);
				boolean hasLayer = false;
				for(MapInfo mapInfo : mapInfos ){
					long mid = mapInfo.mapId;
					if (mid == mapId) {
						hasLayer = true;
						break;
					}

				}
				if (hasLayer == true) {
					// getGroupLayersで取得したレイヤ情報には属性情報が含まれないため、
					// MapDBから直接レイヤ情報を取得し直す。
					LayerInfo layerInfo = mapDB.getLayerInfo(groupLayerInfo.layerId);

					// 属性を検索し、以下の属性名称を持つ属性IDを取得
					// 住所
					// 最終更新日時
					// 座標
					Iterable<AttrInfo> attrInfos = layerInfo.getAttrIterable();
					String [] attrIds = new String[3];
					for(AttrInfo attrInfo : attrInfos){
						if(attrInfo.name.equals(lang.__("Address"))){
							attrIds[0] = attrInfo.attrId;
						}else if(attrInfo.name.equals(lang.__("Last modified"))){
							attrIds[1] = attrInfo.attrId;
						}else if(attrInfo.name.equals(lang.__("Coordinates<!--2-->"))){
							attrIds[2] = attrInfo.attrId;
						}else{
						}
					}
					retVal.put(layerInfo, attrIds);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}


	/**
	 * ある地図で使用済みの参照レイヤの取得
	 * 子レイヤを持つ場合はそれも返却する
	 * @return レイヤIDと名前のマップ
	 */
	public Map<MapLayerInfo,List<MapLayerInfo>> getMapUsedKMLLayersAll(int communityId, int groupId, long mapId){
		MapDB mapDB = getMapDB();
		Map<MapLayerInfo,List<MapLayerInfo>> retVal = new LinkedHashMap<MapLayerInfo, List<MapLayerInfo>>();


    	try {
    		Iterable<MapLayerInfo> mapLayerInfos = mapDB.getMapInfo(mapId).getMapLayerIterable();
    		for(MapLayerInfo mapLayerInfo : mapLayerInfos){
    			if(mapLayerInfo.getLayerType() == LayerInfo.TYPE_KML){
        			List<MapLayerInfo> mapLayerInfoChildlen = mapDB.getMapInfo(mapId).getMapLayerInfoChildren(mapLayerInfo);
        			retVal.put(mapLayerInfo, mapLayerInfoChildlen);
    			}
    		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}


		return retVal;
	}

	/**
	 * ある地図で使用済みの参照レイヤの取得
	 * 子レイヤを持つ場合はそれも返却する
	 * @return レイヤIDと名前のマップ
	 */
	public Map<MapLayerInfo,List<MapLayerInfo>> getMapUsedReferenceLayersAll(int communityId, int groupId, long mapId){
		MapDB mapDB = getMapDB();
		Map<MapLayerInfo,List<MapLayerInfo>> retVal = new LinkedHashMap<MapLayerInfo, List<MapLayerInfo>>();


    	try {
    		Iterable<MapLayerInfo> mapLayerInfos = mapDB.getMapInfo(mapId).getMapLayerIterable();
    		for(MapLayerInfo mapLayerInfo : mapLayerInfos){
    			if(mapLayerInfo.getLayerType() == LayerInfo.TYPE_REFERENCE_WMS ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_OVERLAY_WMS ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_OVERLAY_XYZ ||
					mapLayerInfo.getLayerType() == LayerInfo.TYPE_REFERENCE){
        			List<MapLayerInfo> mapLayerInfoChildlen = mapDB.getMapInfo(mapId).getMapLayerInfoChildren(mapLayerInfo);
        			retVal.put(mapLayerInfo, mapLayerInfoChildlen);
    			}
    		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}


		return retVal;
	}

	/**
	 * ある地図で使用済みのベースレイヤの取得
	 * 子レイヤを持つ場合はそれも返却する
	 * @return レイヤIDと名前のマップ
	 */
	public Map<String, String> getMapUsedBaseLayersAll(int communityId, int groupId, long mapId){
		MapDB mapDB = getMapDB();
		Map<String, String> retVal = new LinkedHashMap<String, String>();


    	try {
    		Iterable<MapLayerInfo> mapLayerInfos = mapDB.getMapInfo(mapId).getMapLayerIterable();
    		for(MapLayerInfo mapLayerInfo : mapLayerInfos){
    			if(mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_CDS             ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_GOOGLE     ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_GROUP      ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_KAMAPCACHE ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_OSM        ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_TILECACHE  ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_TILED      ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_WEBTIS     ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_WMS        ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_WMS_SINGLE ||
    					mapLayerInfo.getLayerType() == LayerInfo.TYPE_BASE_XYZ
    				){
        			retVal.put(mapLayerInfo.layerId, mapLayerInfo.layerName);
    			}
    		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}


		return retVal;
	}


	/**
	 * MapDBを取得
	 * @return MapDB
	 */
    public MapDB getMapDB()
    {
    	if (mapDB == null) {
    		mapDB = MapDB.getMapDB();
    	}
    	return mapDB;
    }

    /**
     * eコミマップのユーザIDからUserInfoオブジェクトを取得する
     * @param authId
     * @return UserInfo
     */
    public UserInfo getAuthIdUserInfo(String authId) {
    	MapDB mdb = getMapDB();
    	System.out.println("★authId="+mdb.getAuthIdUserInfo(authId));
		return mdb.getAuthIdUserInfo(authId);
	}

    /**
     * 避難発令のレイヤIDと、その種別の属性IDを返す。
     * @param communityId
     * @param groupId
     * @param mapid
     * @return String[]
     */
    /*public String[] getIssueLayerInfo(int communityId, int groupId, long mapid) {
    	ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
    	String layerName = "避難地域";
    	String attrName = "発令状況";
    	try {
			layerName = rb.getString("ISSUELAYERNAME");
			attrName = rb.getString("ISSUEATTRNAME");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("",e);
		}

    	MapDB mdb = getMapDB();
    	MapInfo mapInfo = mdb.getMapInfo(mapid);
    	int sz = mapInfo.countMapLayerInfo();

    	String[] issuelayer = new String[2];
    	boolean find = false;
    	try {
    		for (int i = 0; i < sz; i++) {
    			MapLayerInfo maplayerInfo = mapInfo.getMapLayerInfo(i);
    			if (maplayerInfo.layerName.equals(layerName)) {
    				LayerInfo layerInfo = mdb.getLayerInfo(maplayerInfo.layerId);
    				issuelayer[0] = layerInfo.layerId;
    				for (AttrInfo attrInfo : layerInfo.getAttrIterable()) {
    					if (attrInfo.name.equals(attrName)) {
    						issuelayer[1] = attrInfo.attrId;
    						find = true;
    						break;
    					}
    				}
    			}
    			if (find) break;
    		}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("",e);
		}
    	return issuelayer;
    }*/


	public String getLayerList(String euser, int mapId) {
    	MapDB mapDB = MapDB.getMapDB();
        MapInfo mapInfo = mapDB.getMapInfo(mapId);

        try {
	        if (mapInfo == null) {
		    	JSONObject json = new JSONObject();
				json.put("error", lang.__("Specified map ID ({0}) not exist in _map table.\n", mapId) +
						lang.__("Please re-select map info after searching for e-Com map info in map master info table."));
				return json.toString();
	        }

	    	JSONArray items = new JSONArray();

	    	String[] contentsLayerId = mapDB.getContentsLayerId(mapInfo);
	        for (int i=0; i<contentsLayerId.length; i++) {
	        	LayerInfo layerInfo2 = mapDB.getLayerInfo(contentsLayerId[i]);
	        	if (layerInfo2.type == LayerInfo.TYPE_LOCAL) {
	        		JSONObject item = new JSONObject();

	        		item.put("layerid", layerInfo2.layerId);
	        		item.put("name", layerInfo2.name);

		    		items.put(item);
	        	}
	        }

	    	JSONObject json = new JSONObject();
			json.put("items", items);
			return json.toString();
    	} catch (Exception e) {
			try {
		    	JSONObject json = new JSONObject();
				json.put("error", lang.__("An error occurred:") + e.getMessage());
				return json.toString();
			} catch (JSONException e1) {
			}
    	}

        return null;
    }


    public String getConditionList(String euser, int mapId) {
    	MapDB mapDB = MapDB.getMapDB();
        UserInfo userInfo = mapDB.getAuthIdUserInfo(euser);
        MapInfo mapInfo = mapDB.getMapInfo(mapId);
        String sort = "created";
        boolean desc = false;
        int limit = -1;
    	int offset = -1;

        try {
	        if (mapInfo == null) {
		    	JSONObject json = new JSONObject();
				json.put("error", lang.__("Specified map ID ({0}) not exist in _map table.", mapId) +
						lang.__("Please re-select map info after searching for e-Com map info in map master info table."));
				return json.toString();
	        }
	        return ListSearchConditionInfo.getConditionList(userInfo, ListSearchConditionInfo.STATUS_DEFAULT, mapInfo, sort, desc, limit, offset).toString();
        } catch (Exception e) {
			try {
		    	JSONObject json = new JSONObject();
				json.put("error", lang.__("An error occurred:") + e.getMessage());
				return json.toString();
			} catch (JSONException e1) {
			}
    	}

        return null;
    }

    public String getConditionValue(String euser, long filterId) {
    	MapDB mapDB = MapDB.getMapDB();
        UserInfo userInfo = mapDB.getAuthIdUserInfo(euser);

        try {
	        if (userInfo == null) {
		    	JSONObject json = new JSONObject();
				json.put("error", lang.__("Specified user ({0}) not exist in _user table.", euser));
				return json.toString();
	        }
        	return ListSearchConditionInfo.getConditionValue(userInfo, filterId).toString();
        }
        catch (JSONException ex) {
			try {
		    	JSONObject json = new JSONObject();
				json.put("error", lang.__("An error occurred:") + ex.getMessage());
				return json.toString();
			} catch (JSONException e1) {
			}
        }

        return null;
    }

    public String getAttributeList(String euser, String layerId) {
    	MapDB mapDB = MapDB.getMapDB();
    	LayerInfo layerInfo = mapDB.getLayerInfo(layerId);

    	//TODO: Check right of the user

    	try {
	    	JSONArray items = new JSONArray();
	    	for (AttrInfo attrInfo : layerInfo.getSearchVisibleAttrInfo()) {
	    		JSONObject item = new JSONObject();

	    		item.put("dataType", attrInfo.dataType);
	    		item.put("name", attrInfo.name);
	    		item.put("attrId", attrInfo.attrId);
	    		item.put("dataExp", attrInfo.dataExp);

	    		items.put(item);
	    	}


	    	JSONObject json = new JSONObject();
			json.put("items", items);
			return json.toString();
    	} catch (Exception e) {

    	}

    	return null;
    }


    public int deleteCondition(String euser, long filterId) {
    	MapDB mapDB = MapDB.getMapDB();
    	UserInfo userInfo = mapDB.getAuthIdUserInfo(euser);
    	return ListSearchConditionInfo.deleteCondition(userInfo, filterId);
    }
}
