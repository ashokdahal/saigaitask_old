/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * 編集者ユーザ記録用の st_edituser 属性を
 * 登録情報レイヤに追加するサービスクラスです.
 */
@org.springframework.stereotype.Service
public class EdituserAttrService extends BaseService {

	Logger logger = Logger.getLogger(EdituserAttrService.class);
	
	/** edituser 属性ID */
	public static final String EDITUSER_ATTR_ID = "st_edituser";

	/**
	 * 登録情報レイヤに st_edituser 属性を追加する。
	 * @param layerId
	 * @return 成功/失敗
	 */
	public boolean alterTableAddEdituserColumnIfNotExists(String layerId) {
		MapDB mapDB = MapDB.getMapDB();

		LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
		if(layerInfo==null) return false;
		if(layerInfo.type!=LayerInfo.TYPE_LOCAL) return false;
		if(layerInfo.getAttrInfo(EDITUSER_ATTR_ID)!=null) return false;

		try {
			String attrId = EDITUSER_ATTR_ID;
			String name = Config.getString("EDITUSERATTR_NAME", lang.__("[NIED DISS]Editor"));
			AttrInfo attrInfo = new AttrInfo(attrId, name, AttrInfo.STATUS_DEFAULT, 20, 0, AttrInfo.DATATYPE_TEXT, "", true);

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
	 * @param layerInfo レイヤ情報
	 * @return 編集者属性情報
	 */
	public static AttrInfo getAttrInfo(LayerInfo layerInfo) {
		if(layerInfo!=null) {
			return layerInfo.getAttrInfo(EdituserAttrService.EDITUSER_ATTR_ID);
		}
		return null;
	}

	/**
	 * @param layerInfo レイヤ情報
	 * @return 編集者属性情報
	 */
	public static boolean hasEdituserAttr(LayerInfo layerInfo) {
		return getAttrInfo(layerInfo)!=null;
	}

	/**
	 * 属性チェック
	 * @param layerInfo
	 */
	public void checkIfExists(LayerInfo layerInfo) {
		boolean hasEdituserAttr = hasEdituserAttr(layerInfo);
		if(!hasEdituserAttr) {
			throw new ServiceException(lang.__("Editor not found. Do optimization on Admin window."));
		}
	}
}
