/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;

public class MathUtil {


	/**
	 * 緯度経度から電子国土地理院タイルの座標を算出する
	 * @param zoomLevel
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static Map<String,Integer> getGSITileCoordinate(int zoomLevel, double lat, double lon){

		final double hc = 180.0;
		final double L  = 180 / Math.PI * Math.asin(Math.tanh(Math.PI));

		double tileLat;
		double tileLon;
		double pixSize = 256.0;

		double v1 = Math.sin(lat * (Math.PI/hc));
		double v2 = Math.sin(L * (Math.PI/hc));

		tileLat = Math.floor(
				(Math.pow(2.0, zoomLevel+7.0) / Math.PI * ( -(FastMath.atanh(v1)) + FastMath.atanh(v2) ))
				/ pixSize
			);

		tileLon = Math.floor(
				(Math.pow(2.0, zoomLevel+7.0) * (lon / hc + 1.0 ))
				/ pixSize
			);


		Map<String,Integer> gsiTileCoordinateMap = new HashMap<String,Integer>();
		gsiTileCoordinateMap.put("lat", (int)tileLat);
		gsiTileCoordinateMap.put("lon", (int)tileLon);

		return gsiTileCoordinateMap;
	}
}
