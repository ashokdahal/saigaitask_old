package jp.ecom_plat.saigaitask.action;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.ows.WMSCapabilities;
import org.json.JSONObject;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.framework.util.Base64Util;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import facebook4j.internal.org.json.JSONArray;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.osw.WMSUtils;
import jp.ecom_plat.map.servlet.ServletUtil;
import jp.ecom_plat.saigaitask.action.page.AbstractPageAction;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.EarthQuakeLayerService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.Config;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class EarthquakeLayersAction extends AbstractPageAction{
	
	@Resource
	protected EarthQuakeLayerService earthQuakeLayerService;
	
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value = { "/earthquakelayers/getearthquakelayerdata"}, params= {"latitude","longitude", "depth", "magnitude"} )
	public String GetEarthquakeData(double latitude, double longitude, double depth, double magnitude) {
		JSONArray layersJson = new JSONArray();
		try {
			
			String url = Config.getEarthquakeGeoserverWMS();
			url = url+"?VERSION=1.0.0&Request=GetCapabilities&Service=WMS";
			String workSpace = Config.getEarthquakeGeoserverWorkspace();
			String earthquakeFileName = earthQuakeLayerService.GetClosestEarthquake(latitude, longitude, depth, magnitude);
	
			WMSCapabilities caps = null;
			HttpURLConnection conn = ServletUtil.getHttpConnection(url, false);
			caps = WMSUtils.getWMSCapabilities(conn);
			conn.disconnect();
			List<Layer> layers = new ArrayList<>(Arrays.asList(caps.getLayer().getChildren()));
			for(Layer layer : layers) {
				if(layer.getName().contains(earthquakeFileName)) {
					layersJson.put(workSpace+":"+layer.getName());
				}
			}
				
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return layersJson.toString();
	}

	//OSM
}
