/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.servlet.ServletUtil;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;

public class ArcGISUtil {

	static Logger logger = Logger.getLogger(ArcGISUtil.class);

	/** 汎用グループ レイヤ種別 SaigaiTask用 */
	public static final short LAYER_TYPE_GROUP = 0;
	/** 外部地図用のレイヤ種別 WMSレイヤのLAYERS用 */
	public static final short LAYER_TYPE_EXTERNALMAP_ARCGIS_LAYERS = 903;

	/**
	 * @param info 外部地図情報
	 * @return wmscapsurl に layertype=arcgis と入っていればArcGISレイヤと判定
	 */
	public static boolean isArcGISLayer(ExternalmapdataInfo info) {
		if(org.seasar.framework.util.StringUtil.isEmpty(info.wmscapsurl)) return false;
		return info.wmscapsurl.indexOf("?layertype=arcgis") != -1 || info.wmscapsurl.indexOf("&layertype=arcgis") != -1;
	}

	/**
	 * CapabilitiesのURLからLayerInfoを生成
	 * @param layerIds 特定のレイヤだけ取得する場合は配列でレイヤIDを指定する.
	 * @throws JSONException
	 * @throws ParseException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws SQLException
	 */
	public static Vector<LayerInfo> getArcGISLayerInfo(String wmsCapsUrl, String layerId, String layerName)
			throws IOException, JSONException, ParseException, KeyManagementException, NoSuchAlgorithmException, SQLException
	{
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();

		if(wmsCapsUrl == null || wmsCapsUrl.equals("")) return vecLayerInfo;

		// 先頭の?で分割する
		int question = wmsCapsUrl.indexOf("?");
		// ?より手前のURL
		String arcgisURL = question == -1 ? wmsCapsUrl : wmsCapsUrl.substring(0, wmsCapsUrl.indexOf("?"));
		// 末尾が/の場合は外す
		if(arcgisURL.substring(arcgisURL.length()-1, arcgisURL.length()).equals("/")){
			arcgisURL = arcgisURL.substring(0, arcgisURL.length()-1);
		}
		// URL以外のパラメータ部分を取得する
		StringBuffer wmsurl = new StringBuffer();
		StringBuffer optionUrl = new StringBuffer();
		wmsurl.append(arcgisURL);
		// 時系列パラメータ
		String times = "";
		String timefrom = "";
		String timeto = "";
		// Timefrom,toの未設定対応で、ArcGIS側のMap情報のtimeExtent値で設定しておく

		// ArcGISのマップ情報を取得
		String mapServerURL = arcgisURL + "?f=pjson";
		URL url = new URL(mapServerURL);
		BufferedReader br;
		StringBuilder sb = new StringBuilder();
		String mapName = "";
		String description = "";

		HttpURLConnection httpConnection = ServletUtil.getHttpConnection(url, true);
		httpConnection.getResponseCode();
		try {
			br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String line;
			while ((line =br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject result = new JSONObject(sb.toString());
		if(result.has("mapName")){
			mapName = result.getString("mapName");
		}
		if(result.has("description")){
			description = result.getString("description");
		}
		if(result.has("timeInfo")){
			JSONObject timeObj = result.getJSONObject("timeInfo");
			if(timeObj.has("timeExtent")){
				JSONArray timeExtents = timeObj.getJSONArray("timeExtent");
				if(timeExtents.length() >= 2){
					timefrom = String.valueOf(timeExtents.get(0));
					timeto = String.valueOf(timeExtents.get(1));
				}
			}
		}

		// &で分割する
		String reqParam = question == -1 ? "" : wmsCapsUrl.substring(question + 1, wmsCapsUrl.length());
		String[] reqParams = reqParam.split("&");
		for(String str : reqParams){
			// =がなければ無視する
			if(str.equals("") || str.indexOf("=") == -1) continue;
			String[] param_ch = str.split("=");
			switch(param_ch[0]){
			case "layertype" :
				break;
			case "timefrom" :
				timefrom = dateToTime(param_ch[1], timefrom, true);
				break;
			case "timeto" :
				timeto = dateToTime(param_ch[1], timeto, false);
				break;
			default :
				// そのまま入れる
				if(optionUrl.length() > 0) optionUrl.append("&");
				optionUrl.append(str);
				break;
			}
		}
		if(optionUrl.length() > 0) optionUrl.append("&");
		optionUrl.append("time=" + timefrom + "," + timeto);

		// 親ディレクトリ
		vecLayerInfo.add(
			LayerInfo.createRemoteLayerInfo(
				layerId, layerName, description, LayerInfo.STATUS_DEFAULT, LAYER_TYPE_GROUP, 0, 0, ""/*attribution*/, null,
				null, null, null, "", "", "", ""/*wmsFeatureURL*/,
				null, null, ""/* metadataUrl */)
				);
		// 子レイヤ
		if(result.has("layers")){
			JSONArray layers = result.getJSONArray("layers");
			for(int i = 0; i < layers.length(); i++){
				JSONObject obj = (JSONObject) layers.get(i);
				if(obj != null){
					LayerInfo layer = new LayerInfo(layerId+"_"+i, obj.getString("name"), ""/*description*/, LayerInfo.STATUS_DEFAULT,
							LAYER_TYPE_EXTERNALMAP_ARCGIS_LAYERS, ""/*attribution*/, obj.getString("name")/*featuretypeid*/);
					layer.wmsCapabilitiesURL = wmsurl.toString() + "/export?id=" + obj.getString("id") + "&" + optionUrl.toString();
					layer.wmsLegendURL = wmsurl.toString() + "/legend?id=" + obj.getString("id");
					vecLayerInfo.add(layer);
				}
			}
		}
		return vecLayerInfo;
	}

	/**
	 * 20160624等の日付をTime値に変換する
	 * @param param : 変換する日付文字列
	 * @param defaultParam : 変換に失敗した時に返すデフォルト値
	 * @return
	 */
	static public String dateToTime(String param, String defaultParam, boolean timefrom){
		// 値がなければ何もしない
		if(param.equals("")) return defaultParam;
		String time = defaultParam;
		// ハイフンとスラッシュを外す
		String dateStr = param.replaceAll("-", "").replaceAll("/", "").replaceAll(":", "");
		// yyyyMMddのみなら後ろに000000 or 235959を足す
		if(dateStr.length() == 8){
			if(timefrom) dateStr += "000000";
			else dateStr += "235959";
		}
		// yyyyMMddhhmmのみなら後ろに00を足す
		else if(dateStr.length() == 12){
			dateStr += "00";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try{
			java.util.Date formatDate = sdf.parse(dateStr);
			time = String.valueOf(formatDate.getTime());
		} catch (IllegalArgumentException e){
		} catch (ParseException e) {
		}
		return time;
	}

}
