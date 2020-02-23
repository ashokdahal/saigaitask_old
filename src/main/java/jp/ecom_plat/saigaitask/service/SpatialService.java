/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 空間演算のサービスです.
 */
@org.springframework.stereotype.Service
public class SpatialService extends BaseService {
	
	Logger logger = Logger.getLogger(SpatialService.class);

	/** JDBCマネージャ */
	@Resource protected JdbcManager jdbcManager;

	/**
	 * WKTを指定レイヤで切り出します.
	 * @param wkt 切り出されるジオメトリのWKT(WGS84)
	 * @param intersectLayerId 切り出すレイヤのレイヤID
	 * @return WKT (WGS84)
	 */
	public String intersect(String wkt, String intersectLayerId) {
		Date[] timeParam = new Date[]{TimeUtil.newDate()};
		return intersect(wkt, intersectLayerId, timeParam);
	}

	/**
	 * WKTを指定レイヤで切り出します.
	 * @param wkt 切り出されるジオメトリのWKT(WGS84)
	 * @param intersectLayerId 切り出すレイヤのレイヤID
	 * @param timeParam 時間パラメータ
	 * @return WKT (WGS84)
	 */
	public String intersect(String wkt, String intersectLayerId, Date[] timeParam) {

		// 時系列レイヤかどうかチェック
		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layerInfo = mapDB.getLayerInfo(intersectLayerId);
		String timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);

		BeanMap result = null;
		StringBuffer sql = new StringBuffer()
		.append("WITH ")
		// 切り出されるジオメトリ
		.append(" input AS (SELECT ST_Buffer('SRID=4326;"+wkt+"'::geometry, 0.0) AS geom)")
		// 切り出されたジオメトリ
		.append(",intersections AS (SELECT ST_Intersection(input.geom, the_geom) AS geom FROM "+intersectLayerId+", input WHERE TRUE "+timeQuery+")")
		// 切り出されたジオメトリを統合して１つのジオメトリにする
		.append("SELECT ST_AsText(ST_Union(geom)) AS wkt FROM intersections");
		result = jdbcManager.selectBySql(BeanMap.class, sql.toString()).getSingleResult();
		String intersectWKT = (String) result.get("wkt");
		return intersectWKT;
	}
}
