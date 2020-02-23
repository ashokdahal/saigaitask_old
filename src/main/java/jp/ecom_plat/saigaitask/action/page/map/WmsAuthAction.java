/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page.map;

import static jp.ecom_plat.saigaitask.util.StringUtil.join;
import static jp.ecom_plat.saigaitask.util.StringUtil.splitQuery;
import static jp.ecom_plat.saigaitask.util.StringUtil.toQuery;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.factory.GeoTools;
import org.geotools.referencing.CRS;
import org.json.JSONException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.ecom_plat.map.db.FeatureSearchSpatialLayer;
import jp.ecom_plat.map.db.FilteredFeatureId;
import jp.ecom_plat.map.image.WMSImage;
import jp.ecom_plat.map.servlet.SLDServlet;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.form.page.map.WmsAuthForm;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import jp.ecom_plat.saigaitask.util.WMSUtil;

/**
 * An action class equivalent to e-comi's WMSAuth.
 * spring checked take 5/12
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class WmsAuthAction extends AbstractAction {

	protected WmsAuthForm wmsAuthForm;

	@SuppressWarnings("unused")
	private static MathTransform mtFromMercator;

	static {
		// デフォルト(lon, lat)順とする
		System.setProperty(GeoTools.FORCE_LONGITUDE_FIRST_AXIS_ORDER, "true");
		try {
			mtFromMercator = CRS.findMathTransform(CRS.decode("EPSG:900913", false), CRS.decode("EPSG:4326", false));
		} catch (NoSuchAuthorityCodeException e) {
			Logger.getLogger(WmsAuthAction.class).error(e);
		} catch (FactoryException e) {
			Logger.getLogger(WmsAuthAction.class).error(e);
		}
	}

	/** GeoServerのURL */
	private static final String geoserverWMS = Config.getLocalGeoserverWMS();

	protected static Logger getLogger() {
		return Logger.getLogger(WmsAuthAction.class);
	}

	/**
	 * Execution method of WMSAuth action
	 * TODO: Authentication of layer access authority
	 * @return null(Not forward)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/wmsAuth", method = RequestMethod.GET)
	public ResponseEntity<byte[]> index(Map<String,Object>model, @Valid @ModelAttribute WmsAuthForm wmsAuthForm, HttpServletRequest request, HttpServletResponse response) {
		this.wmsAuthForm = wmsAuthForm;
		Logger logger = getLogger();

		//TODO:Why can't I get a request?
		//String query = (String) requestScope.get("javax.servlet.forward.query_string");
		String query = (String) request.getAttribute("javax.servlet.forward.query_string");
		query = getQueryString(request);
		
		boolean isFilterRequest = StringUtil.isNotEmpty(wmsAuthForm.filterkey) && StringUtil.isNotEmpty(wmsAuthForm.filterlayer);
		boolean isGrayoutRequest = wmsAuthForm.grayout!=null;
		boolean isSpatialLayerRequest = StringUtil.isNotEmpty(wmsAuthForm.filterkey) && wmsAuthForm.spatiallayer!=null && wmsAuthForm.spatiallayer;
		List<Long> filteredFeatureIds = FilteredFeatureId.getFilteredFeatureId(session, wmsAuthForm.filterkey);
		if(filteredFeatureIds==null) { // filterreset
			isFilterRequest = false;
			//isGrayoutRequest = wmsAuthForm!=null && wmsAuthForm.grayout!=null && 0<wmsAuthForm.grayout;
			isGrayoutRequest = false; // グレーアウト無視
			isSpatialLayerRequest = false;
		}

		BufferedImage tileImage = null;
		byte[] imageBytes = null;

		//Added time parameter
		String timeParam = wmsAuthForm.time;
		if (timeParam == null) {
			//timeParam = WMSUtils.formatWMSTime(new Date());
			// Because e-commap does not have TimeZone,
			// Format in ISO8601 according to TimeZone of e-commap
			timeParam = TimeUtil.formatISO8601WithOffset(System.currentTimeMillis());
		}
		query += "&TIME="+timeParam;

		// Normal request
		logger.trace("layers: "+wmsAuthForm.LAYERS);
		if(isFilterRequest==false && isGrayoutRequest==false && isSpatialLayerRequest==false) {
			URL url = null;
			try {
				if(StringUtil.isEmpty(wmsAuthForm.layertimes)) {
					// パラメータの設定
					// SLDをURLで指定する
					query += "&SLD="+SLDServlet.getEncodedURL(wmsAuthForm.cid, wmsAuthForm.mid, wmsAuthForm.LAYERS, wmsAuthForm.rule, wmsAuthForm.rate, 1.0F, wmsAuthForm.linerate, wmsAuthForm.textrate, 0);

					url = new URL(geoserverWMS+"&"+query);
//				url = new URL("http://ec2-18-222-68-104.us-east-2.compute.amazonaws.com:8080/geoserver/EarthQuake/wms?TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.0&REQUEST=GetMap&STYLES=&FORMAT=image/png&cid=1&mid=14&LAYERS=EarthQuake:H1_1020_5_5_GOVT&keys=Efww4kIX&_OLSALT=0.8403525680300463&SRS=EPSG:4326&BBOX=77.62224865744471,28.879020551384123,80.85273015385597,31.117324383305206&WIDTH=256&HEIGHT=256&TIME=2019-08-04T06:48:33.190Z");
					// http 接続
					imageBytes = proxy(url, response);
					logger.debug("WMSAuth: "+url.toString());
				}
				// 時間パラメータのレイヤ個別指定
				else {
					// 分割取得した地図画像リスト
					List<BufferedImage> images = new ArrayList<BufferedImage>();

					Map<String, List<String>> splitQuery = splitQuery(query);

					String layers = "";
					for(String layer : wmsAuthForm.LAYERS.split(",")) {
						final String layertime = wmsAuthForm.layertime(layer);
						if(layertime==null) {
							if(layers.length()!=0) layers+=",";
							layers += layer;
						}
						else {
							// レイヤ個別指定よりも前の layers パラメータ
							if(0<layers.length()) {
								// get tile image
								String newQuery = replaceLayersParameter(splitQuery, layers, timeParam);
								images.add(WMSUtil.getImage(geoserverWMS+"&"+newQuery));

								// clear
								layers = "";
							}

							// 時間パラメータを個別指定するレイヤ
							{
								// get tile image
								String newQuery = replaceLayersParameter(splitQuery, layer, layertime);
								images.add(WMSUtil.getImage(geoserverWMS+"&"+newQuery));
							}
						}
					}
					// レイヤ個別指定よりも後の layers パラメータ
					//if(0<layers.length()) {
						// レイヤ個別指定よりも前の layers パラメータ
						if(0<layers.length()) {
							// get tile image
							String newQuery = replaceLayersParameter(splitQuery, layers, timeParam);
							images.add(WMSUtil.getImage(geoserverWMS+"&"+newQuery));

							// clear
							layers = "";
						}
					//}

					tileImage = mergeImage(images);
				}

			} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
				logger.error("WMS proxy fail.", e);
			}
		}
		// 検索範囲のリクエスト
		else if(isSpatialLayerRequest) {
			doSpatialLayerRequest();
		}
		// フィルタ、グレイアウトのリクエスト
		else {
			String[] layersArray = wmsAuthForm.LAYERS.split(","); // layersパラメータで指定されたレイヤID配列
			// フィルタキーとフィルタレイヤが指定されていればフィルタ処理する
			String filterlayer = wmsAuthForm.filterlayer;
			int filterlayerIdx = ArrayUtil.indexOf(layersArray, filterlayer);
			if(isFilterRequest && filterlayerIdx!=-1) {
				// タイル画像を取得
				List<BufferedImage> images = new ArrayList<BufferedImage>();
				// レイヤの重ね合わせが正しくなるように順番に取得する
				String layers = null;
				// filterlayerを含む下のグレーアウトレイヤ
				{
					layers = StringUtils.join(Arrays.copyOfRange(layersArray, 0, filterlayerIdx+1), ",");
					logger.debug("base grayout layers:" + layers);
					BufferedImage image = getGrayoutImage(layers);
					if(image!=null) images.add(image);
				}
				// filterlayerのみのフィルタ済みレイヤ
				{
					logger.debug("filter layer:" + filterlayer);
					if(filteredFeatureIds.size()==0) {
						logger.debug(lang.__("Search result is 0."));
					}
					else {
						try {
							BufferedImage filteredImage = WMSImage.getFilteredImage(wmsAuthForm.cid, wmsAuthForm.mid, geoserverWMS, query, wmsAuthForm.filterlayer, wmsAuthForm.rule, join(filteredFeatureIds, ","));
							images.add(filteredImage);
						} catch (KeyManagementException e) {
							logger.error(e.getMessage(), e);
						} catch (NoSuchAlgorithmException e) {
							logger.error(e.getMessage(), e);
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				// filterlayerよりも上のレイヤのグレーアウトレイヤ(ほとんど使わないはずだが念のため実装)
				if(filterlayerIdx+1 < layersArray.length) {
					layers = StringUtils.join(Arrays.copyOfRange(layersArray, filterlayerIdx+1, layersArray.length), ",");
					logger.debug("overlay grayout layers:" + layers);
					BufferedImage image = getGrayoutImage(layers);
					if(image!=null) images.add(image);
				}

				// タイル画像を一枚にする
				tileImage = mergeImage(images);
			}
			// フィルタのレイヤIDがLAYERSに指定されていない場合
			else {
				tileImage = getGrayoutImage(wmsAuthForm.LAYERS);
			}
		}

		// タイル画像を返却
		if(tileImage!=null) {
			try {
				ByteArrayOutputStream bs = new ByteArrayOutputStream();
				ImageIO.write(tileImage, wmsAuthForm.FORMAT.split("/")[1], bs);
				imageBytes = bs.toByteArray();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

    	final HttpHeaders headers= new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(wmsAuthForm.FORMAT));
		return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
	}

	/**
	 * 指定URLに接続し、HTTP Responseにそのまま返却します.
	 * @param url
	 * @return 成功/失敗
	 */
	public static byte[] proxy(URL url, HttpServletResponse response) {

		Logger logger = getLogger();

		//BufferedOutputStream out = null;
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		HttpURLConnection httpConnection = null;
		try {
			//<div lang="ja">コネクション取得</div>
			//<div lang="en">Get connection</div>
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setDoOutput(false);
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);

			// Content-Type, Content-Disposition などのヘッダーを設定
			for(Map.Entry<String, List<String>> entry : httpConnection.getHeaderFields().entrySet()) {
				String headerName = entry.getKey();
				if("Transfer-Encoding".equals(headerName)) continue; // 2重付加されるので除外
				response.setHeader(headerName, httpConnection.getHeaderField(headerName));
			}

			//<div lang="ja">そのまま返却</div>
			//<div lang="en">Return without changing</div>
			//out = new BufferedOutputStream(response.getOutputStream(), 65536);
			out = new ByteArrayOutputStream();
			in = new BufferedInputStream(httpConnection.getInputStream(), 65536);
			byte[] b = new byte[4096];
			int length;
			while ((length = in.read(b)) != -1) {
				out.write(b, 0, length);
			}
			in.close();
			//out.flush();
			//out.close();
			return out.toByteArray();
		} catch(FileNotFoundException e) {
			// 404 の場合に FileNotFoundException が発生している.
			String msg = "";
			if(httpConnection!=null) {
				try {
					msg += "http " + httpConnection.getResponseCode() + " " +httpConnection.getResponseMessage();
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			logger.error(msg, e);
			return new byte[0];
		} catch(Exception e) {
			logger.error("WMSAuth.proxy "+url.toString(), e);
			return new byte[0];

		} finally {
			if(out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	protected BufferedImage mergeImage(List<BufferedImage> images) {
		BufferedImage tileImage = null;
		// タイル画像を一枚にする
		if(images.size()==1) {
			tileImage = images.get(0);
		}
		else {
			// 下のレイヤから重ねて描画していく
			Graphics2D graphics = null;
			for(BufferedImage image : images) {
				if(tileImage==null) {
					tileImage = image;
					graphics = tileImage.createGraphics();
				}
				else {
					graphics.drawImage(image, 0, 0, null);
				}
			}
			if(graphics!=null) graphics.dispose();
		}
		return tileImage;
	}

	protected String replaceLayersParameter(Map<String, List<String>> queryMap, String layers, final String time) throws UnsupportedEncodingException {
		final String newLayers = layers;
		Map<String, List<String>> newQueryMap = new HashMap<>(queryMap);
		// replace TIME パラメータ
		newQueryMap.remove("time");
		newQueryMap.put("TIME", new ArrayList<String>(){{
			add(time);
		}});
		// replace LAYERS パラメータ
		newQueryMap.put("LAYERS", new ArrayList<String>(){{
			add(newLayers);
		}});
		// replace SLD パラメータ
		newQueryMap.remove("SLD");
		String newQuery = toQuery(newQueryMap);
		newQuery += "&SLD="+SLDServlet.getEncodedURL(wmsAuthForm.cid, wmsAuthForm.mid, newLayers, wmsAuthForm.rule, wmsAuthForm.rate, 1.0F, wmsAuthForm.linerate, wmsAuthForm.textrate, 0);
		return newQuery;
	}

	protected BufferedImage getGrayoutImage(String layers) {
		//String query = (String) requestScope.get("javax.servlet.forward.query_string");
		String query = getQueryString(request);

		//時間パラメータ追加
		String timeParam = wmsAuthForm.time;
		if (timeParam == null) {
			//timeParam = WMSUtils.formatWMSTime(new Date());
			// eコミマップは TimeZone を持っていないため、
			// eコミマップの TimeZone に合わせて ISO8601 で整形する
			timeParam = TimeUtil.formatISO8601WithOffset(System.currentTimeMillis());
		}
		query += "&TIME="+timeParam;

		float grayout = 1.0f;
		if(wmsAuthForm.grayout!=null) grayout = wmsAuthForm.grayout.floatValue();
		//response.setContentType(wmsAuthForm.FORMAT);
		//response.setCharacterEncoding("UTF-8");
		try {
			BufferedImage image = WMSImage.getGlayoutImage(wmsAuthForm.cid, wmsAuthForm.mid, geoserverWMS, query, layers, wmsAuthForm.rule, grayout);
			return image;
		} catch (KeyManagementException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 空間検索範囲レイヤのＷＭＳ画像を取得します.
	 */
	protected void doSpatialLayerRequest() {

		Logger logger = getLogger();

		logger.trace("spatiallayer: "+wmsAuthForm.spatiallayers);

		double[] bbox = null;
		if(StringUtil.isNotEmpty(wmsAuthForm.BBOX)) {
			// パラメータから取得
			bbox = new double[4];
			String[] strs = wmsAuthForm.BBOX.split(",");
			for(int i=0; i<strs.length && i<4; i++) {
				bbox[i] = Double.parseDouble(strs[i]);
			}
		}
		int imgWidth = Integer.parseInt(wmsAuthForm.WIDTH);
		int imgHeight = Integer.parseInt(wmsAuthForm.HEIGHT);
		String srs = wmsAuthForm.SRS;
		double buffer = 0;
		boolean union = true;
		boolean drawPoint = false;
		float strokeWidth = 1;
		int transparent = 128;
		Color strokeColor = new Color(0, 0, 0, transparent);   // Color.BLACK;
		Color fillColor = new Color(255, 200, 0, transparent); // Color.ORANGE;
		//Vector<Geometry> vecGeom = null; // FIXME: 空間検索範囲の手入力を実装する
		Vector<FeatureSearchSpatialLayer> vecSpatialLayer = new Vector<FeatureSearchSpatialLayer>();
		if(wmsAuthForm.spatiallayers!=null && 0<wmsAuthForm.spatiallayers.length()) {
			try {
				vecSpatialLayer.addAll(FeatureSearchSpatialLayer.getFeatureSearchSpatialLayers(wmsAuthForm.spatiallayers));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		BufferedImage tileImage = null;
		try {
			tileImage = WMSImage.createSearchRangeImage(bbox, imgWidth, imgHeight, srs, buffer, union, drawPoint, strokeWidth, strokeColor, fillColor, /*vecGeom*/ null, vecSpatialLayer, wmsAuthForm.timeParams(false));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		// タイル画像を返却
		if(tileImage!=null) {
			try {
				response.setContentType(wmsAuthForm.FORMAT);
				response.setCharacterEncoding("UTF-8");
				ImageIO.write(tileImage, wmsAuthForm.FORMAT.split("/")[1], response.getOutputStream());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

		}
	}
	
	public static String getQueryString(HttpServletRequest request)
	{
		StringBuffer qbuff = new StringBuffer();
		Map<String, String[]> requestMap = request.getParameterMap();
		Iterator<Entry<String, String[]>>  it = requestMap.entrySet().iterator();
		while (it.hasNext()	) {
			Entry<String, String[]> entry = it.next();
			qbuff.append("&"+entry.getKey()+"="+jp.ecom_plat.saigaitask.util.StringUtil.join(entry.getValue(), ","));
		}
		return qbuff.substring(1);
	}
}
