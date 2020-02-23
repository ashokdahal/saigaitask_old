/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.osw.WMSUtils;
import jp.ecom_plat.map.servlet.ServletUtil;
import jp.ecom_plat.saigaitask.entity.db.AuthorizationInfo;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.OperationType;
import org.geotools.data.ows.Service;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.ows.WMSCapabilities;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.framework.util.Base64Util;

/**
 *
 */
public class WMSUtil extends WMSUtils {

	static Logger logger = Logger.getLogger(WMSUtil.class);

	/**
	 * CapabilitiesのURLから範囲を取得 
	 * @param wmsCapsUrl 
	 * @param layerId 
	 * @return 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws MalformedURLException 
	 * @throws KeyManagementException
	 */
	public static CRSEnvelope getWMSBoundingBox(String wmsCapsUrl, String layerId)
			throws KeyManagementException, MalformedURLException, NoSuchAlgorithmException, IOException
	{
		WMSCapabilities caps = null;
		CRSEnvelope bbox = null;
		
		//GetCapabilitiesリクエストパラメータ追加
		//クリアリングハウス検索の結果の場合はcswidがあるので除外
		String chkUrl = wmsCapsUrl.replace('?','&').toUpperCase();//パラメータ確認のマッチング用に?を&に置換
		if (chkUrl.indexOf("&SERVICE=CSW") == -1 && !wmsCapsUrl.endsWith(".xml")) {
			if (wmsCapsUrl.indexOf("?") == -1) wmsCapsUrl += "?";
			if (chkUrl.indexOf("&REQUEST=") == -1) wmsCapsUrl += "&REQUEST=GetCapabilities";
			else wmsCapsUrl = wmsCapsUrl.replaceAll("REQUEST=[^\\&]+","&REQUEST=GetCapabilities");
			if (chkUrl.indexOf("&SERVICE=") == -1) wmsCapsUrl += "&SERVICE=WMS";
			if (chkUrl.indexOf("&VERSION=") == -1) wmsCapsUrl += "&VERSION=1.1.1";
		}

		logger.debug("wmsCapsUrl: "+wmsCapsUrl);
		logger.debug("layerId: "+layerId);

		HttpURLConnection conn = ServletUtil.getHttpConnection(wmsCapsUrl, false);
		//conn.setRequestProperty("Referer", siteUrl);//クリアリングハウス対応
		//WMSCapabilities取得
		caps = WMSUtils.getWMSCapabilities(conn);
		conn.disconnect();

		if(caps!=null) {
			bbox = caps.getLayer().getLatLonBoundingBox();

			// レイヤID指定があれば対象レイヤから範囲を取得する
			if(StringUtils.isNotEmpty(layerId)) {
				List<Layer> layers = caps.getLayerList();
				for(Layer layer : layers) {
					if(layerId.equals(layer.getName())) {
						bbox = layer.getLatLonBoundingBox();
					}
				}
			}
		}

		return bbox;
	}

	/**
	 * CapabilitiesのURLからLayerInfoを生成
	 * @param wmsCapsUrl 
	 * @param metadataUrl 
	 * @param siteUrl 
	 * @param layerId 
	 * @param layerIds 特定のレイヤだけ取得する場合は配列でレイヤIDを指定する.
	 * @return 
	 * @throws SQLException 
	 * @throws KeyManagementException 
	 * @throws MalformedURLException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static Vector<LayerInfo> getWMSCapsLayerInfo(String wmsCapsUrl, String metadataUrl, String siteUrl, String layerId, String[] layerIds, AuthorizationInfo authorizationInfo)
			throws SQLException, KeyManagementException, MalformedURLException, NoSuchAlgorithmException, IOException
	{
		WMSCapabilities caps = null;
		OperationType getMap = null;
		//String wmsName = "";
		String wmsTitle = "";
		String wmsGetMapURL = "";
		String wmsLegendURL = "";
		String wmsFeatureURL = "";
		
		//CRSEnvelope bbox = null;
		
		String wmsAbstract = "";
		String attribution = "";
		
		//GetCapabilitiesリクエストパラメータ追加
		//クリアリングハウス検索の結果の場合はcswidがあるので除外
		String chkUrl = wmsCapsUrl.replace('?','&').toUpperCase();//パラメータ確認のマッチング用に?を&に置換
		if (chkUrl.indexOf("&SERVICE=CSW") == -1 && !wmsCapsUrl.endsWith(".xml")) {
			if (wmsCapsUrl.indexOf("?") == -1) wmsCapsUrl += "?";
			if (chkUrl.indexOf("&REQUEST=") == -1) wmsCapsUrl += "&REQUEST=GetCapabilities";
			else {
				wmsCapsUrl = wmsCapsUrl.replaceAll("REQUEST=[^\\&]+","&REQUEST=GetCapabilities");
				wmsCapsUrl = wmsCapsUrl.replaceAll("request=[^\\&]+","&REQUEST=GetCapabilities");
			}
			if (chkUrl.indexOf("&SERVICE=") == -1) wmsCapsUrl += "&SERVICE=WMS";
			if (chkUrl.indexOf("&VERSION=") == -1) wmsCapsUrl += "&VERSION=1.1.1";
		}
		// 連続した&を除去する
		while(0<wmsCapsUrl.indexOf("&&")) {
			wmsCapsUrl = wmsCapsUrl.replaceAll("&&", "&");
		}
		
		HttpURLConnection conn = ServletUtil.getHttpConnection(wmsCapsUrl, false);
		conn.setRequestProperty("Referer", siteUrl);//クリアリングハウス対応
		if(authorizationInfo!=null) {
			String authData = authorizationInfo.username + ":" + authorizationInfo.userpass;
			String authorizationHeader = "Basic " + Base64Util.encode(authData.getBytes());
			conn.setRequestProperty("Authorization", authorizationHeader);
		}
		//WMSCapabilities取得
		caps = WMSUtils.getWMSCapabilities(conn);
		conn.disconnect();

		if(caps==null) return new Vector<LayerInfo>();
		//bbox = caps.getLayer().getLatLonBoundingBox();
		
		//WMS URLs
		try {
			getMap = caps.getRequest().getGetMap();
			wmsGetMapURL = getMap.getGet().toString();
			if (wmsGetMapURL.endsWith(".xml?")) wmsGetMapURL = wmsGetMapURL.substring(0, wmsGetMapURL.length()-1);
			else if (wmsGetMapURL.indexOf('?') != wmsGetMapURL.length()-1) wmsGetMapURL.substring(0, wmsGetMapURL.length()-1);
		} catch (Exception e) {}
		try {
			OperationType getLegendGraphic = caps.getRequest().getGetLegendGraphic();
			if (getLegendGraphic != null ) {
				if(getLegendGraphic.getGet() != null)
					wmsLegendURL = getLegendGraphic.getGet().toString();
			}
		} catch (Exception e) {}
		try {
			OperationType getFeatureInfo = caps.getRequest().getGetFeatureInfo();
			if (getFeatureInfo != null && getFeatureInfo.getGet() != null) wmsFeatureURL = getFeatureInfo.getGet().toString();
		} catch (Exception e) {}
		
		Service service = caps.getService();
		if (service != null) {
			//wmsName = service.getName();
			wmsTitle = service.getTitle();
			if (wmsAbstract.length() == 0) wmsAbstract = service.get_abstract();
		}
		if (wmsAbstract == null)  wmsAbstract = "";
		
		Vector<LayerInfo> vecLayerInfo = new Vector<LayerInfo>();
		
		vecLayerInfo.add(
			LayerInfo.createRemoteLayerInfo(
				layerId, wmsTitle, wmsAbstract, LayerInfo.STATUS_DEFAULT, ClearinghouseService.LAYER_TYPE_EXTERNALMAP_WMS, 0, 0, attribution, null,
				null, null, null, wmsGetMapURL, "image/png", wmsLegendURL, wmsFeatureURL,
				null, null, metadataUrl)
		);
		
		//子のレイヤ
		//List<Layer> layers = caps.getLayerList(); // ※ getLayerList() は親レイヤ＋すべての子レイヤを取得する
		List<Layer> layers = new ArrayList<>(Arrays.asList(caps.getLayer().getChildren()));
		int idx = 0;
		for (int i=0; i<layers.size(); i++) {
			// 親レイヤの場合
			List<Layer> children = layers.get(i).getLayerChildren();
			if(children!=null && 0<children.size()) {
				layers.addAll(children);
				continue;
			}
			
			String featureTypeId = layers.get(i).getName();
			if (featureTypeId != null) {
				// LAYERS にレイヤIDの指定がある場合は、指定されたレイヤIDのみレイヤ情報を取得する
				if(layerIds!=null && 0<layerIds.length) {
					if(ArrayUtil.indexOf(layerIds, featureTypeId)==-1) {
						continue;
					}
				}
				String childId = layerId+"_"+(idx++);
				String name = layers.get(i).getTitle();
				LayerInfo childLayer = LayerInfo.createReferenceLayerInfo(childId, name, null, LayerInfo.STATUS_DEFAULT, ClearinghouseService.LAYER_TYPE_EXTERNALMAP_WMS_LAYERS, 0, 0, attribution, featureTypeId);
				try {
					Layer layer = layers.get(i);
					// get style
					List<StyleImpl> styles = layer.getStyles();
					StyleImpl style = styles.get(0);
					if(1<styles.size()) { // 複数ある場合
				        Layer parent = layer.getParent();
				        if (parent != null) {
				            List<StyleImpl> parentStyles = parent.getStyles();
				            for(StyleImpl styleImpl : styles) {
				            	// 親スタイルなら採用しない
				            	if(parentStyles.contains(styleImpl)) continue;
				            	// 自分のスタイルを優先して取得
				            	style = styleImpl;
				            	break;
				            }
				        }
					}
					// get LegendURL
					String childLegendURL = style.getLegendURLs().get(0).toString();
					if(StringUtils.isNotEmpty(childLegendURL)) {
						childLayer.wmsLegendURL = childLegendURL;
					}
				} catch(Exception e) {
					// do nothing
				}

				vecLayerInfo.add(childLayer);
			}
		}
		
		return vecLayerInfo;
	}

	/** URLから画像を取得 
	 * @param url 
	 * @return 画像
	 * @throws KeyManagementException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException */
	static public BufferedImage getImage(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException
	{
		BufferedImage image = null;
		try {
			BufferedInputStream bis;
			HttpURLConnection conn = ServletUtil.getHttpConnection(url, false);
			conn.setConnectTimeout(10000);
			bis = new BufferedInputStream(conn.getInputStream());
			image = ImageIO.read(bis);
			bis.close();
			return image;
		} catch (Exception e) {
		}
		//Retry
		BufferedInputStream bis;
		HttpURLConnection conn = ServletUtil.getHttpConnection(url, false);
		conn.setConnectTimeout(10000);
		bis = new BufferedInputStream(conn.getInputStream());
		image = ImageIO.read(bis);
		bis.close();
		return image;
	}
}
