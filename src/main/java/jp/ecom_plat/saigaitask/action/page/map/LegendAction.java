package jp.ecom_plat.saigaitask.action.page.map;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.form.page.map.WmsAuthForm;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * eコミのLegendServletにプロキシするアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class LegendAction extends AbstractAction {

	/**
	 * WMSAuthアクションの実行メソッド
	 * @return 画像のバイナリ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/legend", method = RequestMethod.GET)
	public ResponseEntity<byte[]> index(Map<String,Object>model, @Valid @ModelAttribute WmsAuthForm wmsAuthForm, HttpServletRequest request, HttpServletResponse response) {
		String query = WmsAuthAction.getQueryString(request);
		String urlStr = null;
		String layerId = request.getParameter("layer");

		// 外部地図の場合
		if(layerId.startsWith("extmap_")) {
			// eコミマップ側のセッションには存在しないため、
			// LegendServletにはプロキシせず、
			// 直接、凡例画像を取得する
			LayerInfo layerInfo = null;
			layerInfo = LayerInfo.getSessionLayerInfo(session, layerId);
			if(layerInfo!=null) {
				if(layerInfo.wmsLegendURL!=null) {
					urlStr = layerInfo.wmsLegendURL;
				}
				//<div lang="ja">凡例指定がなければ親のwmsLegendURLを取得</div>
				//<div lang="en">If legend is not specified, get parent wmsLegendURL</div>
				else {
					Long mapId = Long.valueOf(request.getParameter("mid"));
					MapLayerInfo mapLayerInfo = MapLayerInfo.getSessionMapLayerInfo(request.getSession(), mapId, layerId);
					if(mapLayerInfo!=null&&mapLayerInfo.parent!=null) {
						LayerInfo parentLayerInfo = LayerInfo.getSessionLayerInfo(session, mapLayerInfo.parent);
						if(parentLayerInfo!=null) {
							if(parentLayerInfo.wmsLegendURL!=null) {
								urlStr = parentLayerInfo.wmsLegendURL;
							}
						}
					}
				}
			}
		}
		// eコミマップのLegendServletにプロキシ
		if(urlStr==null) {
			String legendServletURL = Config.getEcommapLegendServletURL();
			urlStr = legendServletURL+"&"+query;
		}

		byte[] imageBytes = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			// http 接続
			imageBytes = WmsAuthAction.proxy(url, response);
			logger.debug("Legend: "+url.toString());
		} catch (IOException e) {
			logger.error("Legend proxy fail.", e);
		}
		
    	final HttpHeaders headers= new HttpHeaders();
    	MediaType mediaType = MediaType.IMAGE_PNG;
    	if(response.getContentType()!=null) {
    		mediaType = MediaType.parseMediaType(response.getContentType());
    	}
		headers.setContentType(mediaType);
		return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
	}
}
