/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.map.jsp.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;

/**
 * eコミマップの地図を指定して削除する.
 * 
 */
public class MapDelete {

	/** 論理削除フラグ */
	public boolean deleteLogically = true;

	MapDB mapDB = MapDB.getMapDB();

	MapInfo mapInfo;

	Map<Long, Short> rollbackMapInfoStatus = new HashMap<Long, Short>();
	Map<String, Short> rollbackLayerInfoStatus = new HashMap<String, Short>();

	/**
	 * @param mapid 削除したい地図ID
	 */
	public MapDelete(Long mapid) {
		mapInfo = mapDB.getMapInfo(mapid);
	}

	/**
	 * @return コミット結果
	 */
	public boolean commit() {
		List<Long> deleteMapIds = new ArrayList<Long>();
		deleteMapIds.add(mapInfo.mapId);
		return commit(deleteMapIds);
	}

	/**
	 * @param deleteMapIds 削除対象地図ID
	 * @return コミット結果
	 */
	public boolean commit(List<Long> deleteMapIds) {

		Logger logger = Logger.getLogger(MapDelete.class);

		// すでに削除済みの場合
		if(mapInfo==null) return true;

		// レイヤ情報の削除
		for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
			String deleteLayerId = mapLayerInfo.layerId;
			LayerInfo layerInfo = mapDB.getLayerInfo(deleteLayerId);
			if(layerInfo==null || deleteMapIds.contains(Long.valueOf(layerInfo.ownerMapId))==false) continue;

			// 論理削除
			if(deleteLogically) {
				try {
					rollbackLayerInfoStatus.put(layerInfo.layerId, layerInfo.status);
					layerInfo.status = LayerInfo.STATUS_DELETED;
					mapDB.updateLayerInfo(layerInfo);
				} catch(Exception e) {
					throw new RuntimeException("cannot delete "+LayerInfo.class.getSimpleName()+"."+" layerid="+deleteLayerId);
				}
			}
			// レイヤ物理削除
			else {
				try {
					ExMapDB.deleteLayerInfo(layerInfo);
				} catch(Exception e) {
					logger.error("MapDelete failed to delete layer "+layerInfo.layerId, e);
				}
			}
		}

		// 地図情報の論理削除
		if(deleteLogically) {
			try {
				rollbackMapInfoStatus.put(mapInfo.mapId, mapInfo.status);
				mapInfo.status = MapInfo.STATUS_DELETED;
				mapDB.updateMapInfo(mapInfo);
			} catch(Exception e) {
				throw new RuntimeException("cannot delete "+MapInfo.class.getSimpleName()+"."+" mapid="+mapInfo.mapId);
			}
		}
		// 地図情報の物理削除
		else {
			try {
				ExMapDB.deleteMapInfo(mapInfo);
			} catch(Exception e) {
				logger.error("MapDelete failed to delete map "+mapInfo.mapId, e);
			}
		}

		return false;
	}

	/**
	 * @return ロールバック結果
	 */
	public boolean rollback() {
		// ロールバック論理削除
		if(deleteLogically) {
			// 地図情報の論理削除をロールバック
			Short rollbackStatus = rollbackMapInfoStatus.get(mapInfo.mapId);
			if(rollbackStatus!=null) {
				try {
					mapInfo.status = rollbackStatus; //MapInfo.STATUS_DEFAULT;
					mapDB.updateMapInfo(mapInfo);
				} catch(Exception e) {
					throw new RuntimeException("cannot rollback "+MapInfo.class.getSimpleName()+"."+" mapid="+mapInfo.mapId);
				}
			}

			// レイヤ情報の削除
			for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
				String deleteLayerId = mapLayerInfo.layerId;
				rollbackStatus = rollbackLayerInfoStatus.get(deleteLayerId);
				if(rollbackStatus!=null) {
					LayerInfo layerInfo = mapDB.getLayerInfo(deleteLayerId);
					//if(mapInfo.mapId!=layerInfo.ownerMapId) continue;

					// 論理削除をロールバック
					try {
						layerInfo.status = rollbackStatus; //LayerInfo.STATUS_DEFAULT;
						mapDB.updateLayerInfo(layerInfo);
					} catch(Exception e) {
						throw new RuntimeException("cannot rollback "+LayerInfo.class.getSimpleName()+"."+" layerid="+deleteLayerId);
					}
				}
			}
		}
		return false;
	}
}
