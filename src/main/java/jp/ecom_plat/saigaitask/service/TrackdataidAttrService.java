/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * 同時複数災害に対応するための trackdataid 属性を
 * 登録情報レイヤに追加するサービスクラスです.
 */
@org.springframework.stereotype.Service
public class TrackdataidAttrService extends BaseService {

	Logger logger = Logger.getLogger(TrackdataidAttrService.class);
	
	/** trackdataid 属性ID */
	public static final String TRACKDATA_ATTR_ID = "trackdataid"; //Config.getString("TRACKDATAID_ATTR_ID", "trackdataid");
	/** trackdataid 属性名称 */
	public String TRACKDATA_ATTR_NAME() {
		return Config.getString("TRACKDATAID_ATTR_NAME", lang.__("Record ID"));
	};
	/** trackdataid 属性、本システムの表示名 */
	public String TRACKDATA_ATTR_DISPLAY() {
		return Config.getString("TRACKDATAID_ATTR_DISPLAY", lang.__("Disaster"));
	};

	/**
	 * 登録情報レイヤに trackdataid 属性を追加する。
	 * @param layerId
	 * @return 成功/失敗
	 */
	public boolean alterTableAddTrackdataidColumnIfNotExists(String layerId) {
		MapDB mapDB = MapDB.getMapDB();

		LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
		if(layerInfo==null) return false;
		if(layerInfo.type!=LayerInfo.TYPE_LOCAL) return false;
		if(layerInfo.getAttrInfo(TRACKDATA_ATTR_ID)!=null) return false;

		try {
			String attrId = TRACKDATA_ATTR_ID;
			String name = TRACKDATA_ATTR_NAME();
			AttrInfo attrInfo = new AttrInfo(attrId, name, AttrInfo.STATUS_DEFAULT, 20, 0, AttrInfo.DATATYPE_INTEGER, "", true);

			try {
				mapDB.insertAttrInfo(layerInfo, attrInfo);
			} catch ( Exception ex ) {
				throw new RuntimeException( ex );
			}
			layerInfo.addAttrInfo( attrInfo );

			try {
				mapDB.insertAttrColumn(layerInfo.layerId, attrId, "text");
				mapDB.updateGeoServerFeature(layerInfo);
				//session.setAttribute("reload_geoserver", "true");
				return true;
			} catch ( Exception ex ) {
				throw new RuntimeException( ex );
			}
		} catch (SQLException ex) {
			logger.error(ex);
			return false;
		}
	}

	/**
	 * trackdataid属性のセレクトボックス用 dataExp を取得.
	 * 引数で渡した記録データと、同じ地図を使う記録データのリストから選択オプションを作る.
	 * dataExp は、key と value の両方を指定できるように、JSONArray<JSONObject> の書式にする.
	 * @param firstTrackdataid セレクトで先頭にする記録データID
	 * @param trackDatas 記録データリスト
	 * @return trackdataid属性のセレクトボックス用 dataExp の JSONArray
	 */
	public JSONArray getTrackdataidAttrDataExp(final long firstTrackdataid, List<TrackData> trackDatas) {
		// ログイン中の記録データを先頭にソートする
		Collections.sort(trackDatas, new Comparator<TrackData>() {
			@Override
			public int compare(TrackData o1, TrackData o2) {
				if(firstTrackdataid==o1.id.longValue()) return -1;
				if(firstTrackdataid==o2.id.longValue()) return  1;
				return 0;
			}
		});

		// dataExp の組み立て(JSON形式)
		JSONArray array = new JSONArray();
		for(TrackData track : trackDatas) {
			try {
				array.put(new JSONObject().put(String.valueOf(track.id), track.name));
			} catch (JSONException e) {
				logger.warn(lang.__("JSONException occurred when dataExp of track data ID attribute (trackdataid) was creating."), e);
			}
		}
		return array;
	}

	/**
	 * trackdataid の eコミの属性情報を、上書きします.
	 * @param layerInfo eコミのレイヤ情報
	 * @param trackdataid 記録データID
	 * @param trackDatas 記録データリスト
	 */
	public void overrideTrackdataidAttr(LayerInfo layerInfo, Long trackdataid, List<TrackData> trackDatas) {
		if(layerInfo!=null) {
			AttrInfo attr = layerInfo.getAttrInfo(TRACKDATA_ATTR_ID);
			if(attr!=null) overrideTrackdataidAttr(attr, trackdataid, trackDatas);
		}
	}

	/**
	 * trackdataid の eコミの属性情報を、上書きします.
	 * @param attr eコミの属性情報
	 * @param trackdataid 記録データID
	 * @param trackDatas 記録データリスト
	 */
	public void overrideTrackdataidAttr(AttrInfo attr, Long trackdataid, List<TrackData> trackDatas) {
		attr.dataType = AttrInfo.DATATYPE_SELECT;
		attr.nullable = false;
		// JSON形式で、trackdataid のSELECT値を取得
		// (ループの外でやったほうが早い)
		String trackdataidAttrDataExp = getTrackdataidAttrDataExp(trackdataid, trackDatas).toString();
		attr.dataExp = trackdataidAttrDataExp;
	}

	/**
	 * MapAction のための、trackdataid属性の maplayerattrInfo を作成します.
	 * @param maplayerInfo 地図レイヤ情報
	 * @return  地図レイヤ属性情報
	 */
	public MaplayerattrInfo createMaplayerattrInfo(MaplayerInfo maplayerInfo) {
		MaplayerattrInfo maplayerattrInfo = new MaplayerattrInfo();
		maplayerattrInfo.attrid = TrackdataidAttrService.TRACKDATA_ATTR_ID;
		maplayerattrInfo.name = TRACKDATA_ATTR_DISPLAY();
		maplayerattrInfo.editable = maplayerInfo.editable;
		maplayerattrInfo.grouping = false;
		maplayerattrInfo.disporder = 1;
		if(0<maplayerInfo.maplayerattrInfos.size()) {
			maplayerattrInfo.disporder = maplayerInfo.maplayerattrInfos.get(maplayerInfo.maplayerattrInfos.size()-1).disporder + 1;
		}
		return maplayerattrInfo;
	}

	/**
	 * ListAction のための、trackdataid属性の TablelistcolumnInfo を作成します.
	 * @param tablelistcolumnInfos
	 * @return テーブルリスト項目情報
	 */
	public TablelistcolumnInfo createTablelistcolumnInfo(List<TablelistcolumnInfo> tablelistcolumnInfos) {
		// 1つでも編集可能かどうか
		boolean editable = false;
		int maxDisporder = 0;
		for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfos) {
			if(tablelistcolumnInfo.editable) {
				editable = true;
			}
			if(tablelistcolumnInfo.disporder!=null) {
				maxDisporder = Math.max(maxDisporder, tablelistcolumnInfo.disporder);
			}
		}
		TablelistcolumnInfo tablelistcolumnInfo = new TablelistcolumnInfo();
		tablelistcolumnInfo.attrid = TrackdataidAttrService.TRACKDATA_ATTR_ID;
		tablelistcolumnInfo.name = TRACKDATA_ATTR_DISPLAY();
		tablelistcolumnInfo.editable = editable;
		tablelistcolumnInfo.loggable = false;
		tablelistcolumnInfo.grouping = false;
		tablelistcolumnInfo.disporder = maxDisporder+1;
		tablelistcolumnInfo.defaultsort = -1;
		return tablelistcolumnInfo;
	}

	/**
	 * @param layerInfo レイヤ情報
	 * @return 記録ID属性情報
	 */
	public AttrInfo getAttrInfo(LayerInfo layerInfo) {
		if(layerInfo!=null) {
			return layerInfo.getAttrInfo(TrackdataidAttrService.TRACKDATA_ATTR_ID);
		}
		return null;
	}

	/**
	 * @param layerInfo レイヤ情報
	 * @return 記録ID属性情報
	 */
	public boolean hasTrackdataidAttr(LayerInfo layerInfo) {
		return getAttrInfo(layerInfo)!=null;
	}

	/**
	 * 属性チェック
	 * @param layerInfo
	 */
	public void checkIfExists(LayerInfo layerInfo) {
		boolean hasTrackdataidAttr = hasTrackdataidAttr(layerInfo);
		if(!hasTrackdataidAttr) {
			throw new ServiceException(lang.__("Record ID not found. Do optimization on Admin window."));
		}
	}
}
