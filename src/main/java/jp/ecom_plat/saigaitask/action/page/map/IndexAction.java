/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page.map;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.geoserver.util.ISO8601Formatter;
import org.geotools.styling.StyledLayerDescriptor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;

import gov.nasa.worldwind.geom.coords.MGRSCoord;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResult.AttrResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.servlet.SLDServlet;
import jp.ecom_plat.map.servlet.SLDServlet2;
import jp.ecom_plat.map.util.GeometryUtils;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.page.AbstractPageAction;
import jp.ecom_plat.saigaitask.beans.StringToListEditor;
import jp.ecom_plat.saigaitask.entity.db.DatatransferInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.LandmarkData;
import jp.ecom_plat.saigaitask.entity.db.LandmarkInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MapreferencelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculateInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculatecolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.names.LandmarkDataNames;
import jp.ecom_plat.saigaitask.form.page.map.MapApiForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.DataTransferService;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.EvalService;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.SpatialService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.TriggerAlertService;
import jp.ecom_plat.saigaitask.service.db.DatatransferInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternalmapdataInfoService;
import jp.ecom_plat.saigaitask.service.db.LandmarkDataService;
import jp.ecom_plat.saigaitask.service.db.LandmarkInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MapreferencelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculateInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculatecolumnInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.HttpUtil;

/**
 * 地図関連のJSON API のアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/page/map")
public class IndexAction extends AbstractPageAction {

	protected MapApiForm mapApiForm;

	// Service
	@Resource protected MapService mapService;
	@Resource protected FileUploadService fileUploadService;
	@Resource protected HistoryTableService historyTableService;
	@Resource protected ClearinghouseService clearinghouseService;
	@Resource protected TablecalculateInfoService tablecalculateInfoService;
	@Resource protected TablecalculatecolumnInfoService tablecalculatecolumnInfoService;
	@Resource protected EvalService evalService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected SpatialService spatialService;
	@Resource protected LandmarkInfoService landmarkInfoService;
	@Resource protected LandmarkDataService landmarkDataService;
	@Resource protected TrackdataidAttrService trackdataidAttrService;
	@Resource protected EdituserAttrService edituserAttrService;
	@Resource protected TriggerAlertService triggerAlertService;
	@Resource protected DatatransferInfoService datatransferInfoService;
	@Resource protected DataTransferService dataTransferService;
	@Resource protected ExternalmapdataInfoService externalmapdataInfoService;
	@Resource protected MapreferencelayerInfoService mapreferencelayerInfoService;

    @InitBinder("mapApiForm")
    public void initBinder(WebDataBinder binder, HttpServletRequest request) {
        binder.registerCustomEditor(List.class, "wkt", new StringToListEditor());
    }
	
	/**
	 * eコミマップの登録情報を１件JSONで取得します.
	 * eMapContents.jsp に対応しています.
	 * 参照系のためCSRF対策不要
	 * @return String
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/ecommap/contents/get", method=org.springframework.web.bind.annotation.RequestMethod.GET)
	@ResponseBody
	public String getEMapContents(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){

		try{
			String authId = loginDataDto.getEcomUser();
			JSONObject json = mapService.getContents(authId, mapApiForm.mid, mapApiForm.layer, mapApiForm.fid, mapApiForm.getTimeParams());

			//座標表示変換（毎回情報を検索するのは、無駄な気もするが。。。）
			try {
				if (!loginDataDto.getLocalgovInfo().coordinatedecimal) {
					// マスタレイヤ情報を取得
					TablemasterInfo master = null;
					if(loginDataDto.getTrackdataid()==0L) {
						master =  tablemasterInfoService.findByLayerId(mapApiForm.layer);
					}
					else {
						// 災害マップのレイヤIDから取得
						master = tracktableInfoService.findByLayerId(mapApiForm.layer).get(0).tablemasterInfo;
					}
					String ccolmn = master.coordinatecolumn;
					if (StringUtil.isNotEmpty(ccolmn)) {
						JSONArray aary = json.getJSONArray("attrs");
						for (int i = 0; i < aary.length(); i++) {
							JSONObject attr = aary.getJSONObject(i);
							if (attr.getString("attrId").equals(ccolmn)) {
								String val = to60Digree(attr.getString("attrValue"));
								attr.put("attrValue", val);
							}
						}
					}
				}
			} catch(Exception e) {
				logger.error(loginDataDto.logInfo()+" transform coordinatedecimal error: "+e.getMessage(), e);
			}

			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();

		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * eコミマップの登録情報を１件登録します.
	 * @return String
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/ecommap/contents/create", method = RequestMethod.POST)
	@ResponseBody
	public String createEMapContents(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){
		boolean success = false;
		String msg = null;

		try {
			String layerId = mapApiForm.layer;
			// TODO: 認証
			UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());
			// 属性
			HashMap<String, String> attributes = new HashMap<String, String>();
			if(mapApiForm.attrIds!=null) {
				for(String attrId : mapApiForm.attrIds) {
					String value = request.getParameter(attrId);
					if(value!=null) {
						attributes.put(attrId, value);
					}
				}
			}
			LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);

			// 属性チェック
			trackdataidAttrService.checkIfExists(layerInfo);
			edituserAttrService.checkIfExists(layerInfo);

			// trackdataid 属性の値が設定されていないか
			if(StringUtil.isEmpty(attributes.get(TrackdataidAttrService.TRACKDATA_ATTR_ID))) {
				// trackdataid 属性の値が入っていなければ、ログイン中の trackdataid を設定
				if(0<loginDataDto.getTrackdataid()) {
					attributes.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, String.valueOf(loginDataDto.getTrackdataid()));
				}
			}

			// 編集者 属性の値が設定されていないか
			if(StringUtil.isEmpty(attributes.get(EdituserAttrService.EDITUSER_ATTR_ID))) {
				// edituser 属性の値が入っていなければ、ログイン名を設定
				attributes.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
			}

			// WKT
			String wkt = null;
			if(mapApiForm.wkt!=null&&mapApiForm.wkt.size()==1) {
				wkt = mapApiForm.wkt.get(0);
			}

			//座標をカラムに登録
			MapmasterInfo mastermap = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			TracktableInfo ttbl = tracktableInfoService.findByTrackDataIdAndTablename(loginDataDto.getTrackdataid(), mapApiForm.layer);
			TablemasterInfo master = null;
			HistorytableInfo htbl = null;
			if (ttbl == null) {
				master = tablemasterInfoService.findByMapmasterInfoIdAndLayerId(mastermap.id, mapApiForm.layer);
				if (master == null) return null;
			}
			else {
				htbl = historyTableService.findOrCreateByTracktableInfo(ttbl, layerInfo);
				master = tablemasterInfoService.findById(ttbl.tablemasterinfoid);
			}
			try {
				if(!StringUtil.isEmpty(master.coordinatecolumn) && wkt!=null){
					Geometry geom = GeometryUtils.getGeometryFromWKT(wkt);
					Point pt = geom.getCentroid();
					DecimalFormat df = new DecimalFormat("0.00000");
					String xy = "("+df.format(pt.getX())+","+ df.format(pt.getY())+")";
					attributes.put(master.coordinatecolumn, xy);
				}
				if (StringUtil.isNotEmpty(master.mgrscolumn)) {
					String mgrs = tableService.getCoordinate(wkt, true, master.mgrsdigit);
					attributes.put(master.mgrscolumn, mgrs);
				}
			} catch (ParseException e) {
				logger.error(loginDataDto.logInfo(), e);
				throw new ServiceException(e);
			}

			try {
				long featureId = FeatureDB.insertFeature(userInfo, layerId, wkt, attributes);
				//履歴テーブルに記録
				if(htbl != null)
					historyTableService.log(ttbl, htbl, featureId, layerInfo);

				if(featureId!=-1) {
					// fileList
					fileUploadService.updateFeatureFileList(mapApiForm.mid, layerInfo, featureId, mapApiForm.fileList, mapApiForm.getTimeParams());
				}

				//計算
				List<TablecalculateInfo> calcList = tablecalculateInfoService.findByTablemasterInfoId(master.id);
				for (TablecalculateInfo calc : calcList) {
					Object val = evalService.eval(calc, layerId, layerId, featureId);
					TablecalculatecolumnInfo ccol = tablecalculatecolumnInfoService.findById(calc.tablecalculatecolumninfoid);
					if (val != null)
						tableService.update(layerId, ccol.columnname, "gid", featureId, val.toString());
					else
						tableService.update(layerId, ccol.columnname, "gid", featureId, null);
				}

				// 更新後のデータ
				FeatureResult afterFeatureResult = FeatureDB.getFeatureContent(layerInfo, featureId, true, FeatureDB.GEOM_TYPE_GEOM, null, /*Date[] timeParam*/null);
				// 更新後の属性情報で走査
				for(int idx=0; idx<afterFeatureResult.countAttrResult(); idx++) {
					AttrResult afterAttrResult = afterFeatureResult.getAttrResult(idx);
					String attrId = afterAttrResult.getAttrId();
					String afterValue = afterAttrResult.getAttrValue();
					// 更新後、データが登録されているなら
					if(StringUtil.isNotEmpty(afterValue)) {
						triggerAlertService.trigger(master.id, attrId, afterValue);
					}
				}

				//データ転送
				DatatransferInfo transinfo = datatransferInfoService.findByTablemasterInfoIdAndValid(master.id);
				if (transinfo != null)
					dataTransferService.transferData(transinfo, loginDataDto.getTrackdataid());

				success = true;
			} catch (Exception e) {
				success = false;
				msg = e.getMessage();
				logger.error(e.getMessage(), e);
			}

			// 結果JSONを生成
			JSONObject result = new JSONObject();
			result.put("success", success);
			result.put("msg", msg);

			// 出力の準備
			// @see https://www.sencha.com/forum/showthread.php?132949-Fileupload-Invalid-JSON-string
			// The server response is parsed by the browser to create the document for the IFRAME.
			// If the server is using JSON to send the return object,
			// then the Content-Type header must be set to "text/html" in order to tell the browser to insert the text unchanged into the document body.
			// content-type should be set to text/html
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(result.toString());
			return result.toString();

		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * eコミのフィーチャにファイルをアップロードします.
	 * レスポンス結果は JSONであるが、ExtJS FormPanel がIFRAMEを使う都合により、
	 * content-type は application/json ではなく、text/html とする。
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/contents/uploadFile")
	@ResponseBody
	public String uploadEMapContents(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {
		//TODO:validateUploadEMapContents
		try {
			boolean success = false;
			String msg = null;
			JSONArray fileList =  null;

			// ファイルアップロード
			try {
				fileList = fileUploadService.uploadContents(mapApiForm.layer, mapApiForm.files);
				success = true;
				msg = lang.__("File uploaded.");
			} catch(ServiceException e) {
				success = false;
				msg = e.getMessage();
				logger.error("/page/map/ecommap/contents/uploadFile error: "+e.getMessage(), e);
			}

			// 結果JSONを生成
			JSONObject result = new JSONObject();
			result.put("success", success);
			result.put("msg", msg);
			result.put("fileList", fileList);

			// 出力の準備
			// @see https://www.sencha.com/forum/showthread.php?132949-Fileupload-Invalid-JSON-string
			// The server response is parsed by the browser to create the document for the IFRAME.
			// If the server is using JSON to send the return object,
			// then the Content-Type header must be set to "text/html" in order to tell the browser to insert the text unchanged into the document body.
			// content-type should be set to text/html
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(result.toString());
			return result.toString();
		//} catch (IOException e) {
		//	logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
		//	logger.error("", e);
		} catch (JSONException e) {
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
		}
		return null;
	}

	/**
	 * eコミマップの登録情報を１件更新します.
	 * @return String
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/contents/update")
	@ResponseBody
	public String updateEMapContents(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){
		boolean success = false;
		String msg = null;

		try {
			try {
				String layerId = mapApiForm.layer;
				long featureId = mapApiForm.fid;
				// TODO: 認証
				UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(loginDataDto.getEcomUser());
				// 属性
				HashMap<String, String> attributes = new HashMap<String, String>();
				if(mapApiForm.attrIds!=null) {
					for(String attrId : mapApiForm.attrIds) {
						attributes.put(attrId, request.getParameter(attrId));
					}
				}
				// 編集者 属性の値が設定されていないか
				if(StringUtil.isEmpty(attributes.get(EdituserAttrService.EDITUSER_ATTR_ID))) {
					// edituser 属性の値が入っていなければ、ログイン名を設定
					attributes.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
				}
				// WKT
				String wkt = null;
				if(mapApiForm.wkt!=null&&mapApiForm.wkt.size()==1) {
					wkt = mapApiForm.wkt.get(0);
				}

				MapmasterInfo mastermap = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
				TracktableInfo ttbl = tracktableInfoService.findByTrackDataIdAndTablename(loginDataDto.getTrackdataid(), mapApiForm.layer);
				TablemasterInfo master = null;
				HistorytableInfo htbl = null;
				LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
				if (ttbl == null) {
					master = tablemasterInfoService.findByMapmasterInfoIdAndLayerId(mastermap.id, mapApiForm.layer);
					if (master == null) return null;
				}
				else {
					htbl = historyTableService.findOrCreateByTracktableInfo(ttbl, layerInfo);
					master = tablemasterInfoService.findById(ttbl.tablemasterinfoid);
				}


				// 更新前のデータ
				FeatureResult beforeFeatureResult = FeatureDB.getFeatureContent(layerInfo, featureId, /*attrGrouped*/true, /*attrAll*/ false, FeatureDB.GEOM_TYPE_GEOM, /*bbox*/null);//, /*nextOrder*/null, /*timeParam*/null);

				if(wkt!=null) {
					//座標をカラムに登録
					if(!StringUtil.isEmpty(master.coordinatecolumn)){
						Geometry geom = GeometryUtils.getGeometryFromWKT(wkt);
						Point pt = geom.getCentroid();
						DecimalFormat df = new DecimalFormat("0.00000");
						String xy = "("+df.format(pt.getX())+","+ df.format(pt.getY())+")";
						attributes.put(master.coordinatecolumn, xy);
					}
					if (StringUtil.isNotEmpty(master.mgrscolumn)) {
						String mgrs = tableService.getCoordinate(wkt, true, master.mgrsdigit);
						attributes.put(master.mgrscolumn, mgrs);
					}
					FeatureDB.updateFeature(userInfo, layerId, featureId, wkt, attributes, null);
				}
				else {
					FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attributes, null);
				}

				//計算
				List<TablecalculateInfo> calcList = tablecalculateInfoService.findByTablemasterInfoId(master.id);
				for (TablecalculateInfo calc : calcList) {
					Object val = evalService.eval(calc, layerId, layerId, featureId);
					TablecalculatecolumnInfo ccol = tablecalculatecolumnInfoService.findById(calc.tablecalculatecolumninfoid);
					tableService.update(layerId, ccol.columnname, "gid", featureId, val!=null?val.toString():null);
				}

				//履歴テーブルに記録
				if(htbl != null)
					historyTableService.log(ttbl, htbl, featureId, layerInfo);

				// 更新後のデータ
				FeatureResult afterFeatureResult = FeatureDB.getFeatureContent(layerInfo, featureId, /*attrGrouped*/true, /*attrAll*/ false, FeatureDB.GEOM_TYPE_GEOM, null);
				// 更新後の属性情報で走査
				for(int idx=0; idx<afterFeatureResult.countAttrResult(); idx++) {
					AttrResult afterAttrResult = afterFeatureResult.getAttrResult(idx);
					String attrId = afterAttrResult.getAttrId();
					String afterValue = afterAttrResult.getAttrValue();
					// 更新後、データが登録されているなら
					if(StringUtil.isNotEmpty(afterValue)) {
						AttrResult beforeAttrResult = beforeFeatureResult.getAttrResult(attrId);
						if(beforeAttrResult!=null) {
							String beforeValue = beforeAttrResult.getAttrValue();
							// 更新前との値を比較して変更されていれば、アラートトリガーを起動する
							if(!afterValue.equals(beforeValue)) {
								triggerAlertService.trigger(master.id, attrId, afterValue);
							}
						}
					}
				}

				//データ転送
				DatatransferInfo transinfo = datatransferInfoService.findByTablemasterInfoIdAndValid(master.id);
				if (transinfo != null)
					dataTransferService.transferData(transinfo, loginDataDto.getTrackdataid());

				// fileList
				fileUploadService.updateFeatureFileList(mapApiForm.mid, layerInfo, featureId, mapApiForm.fileList, mapApiForm.getTimeParams());

				success = true;
			} catch (Exception e) {
				success = false;
				msg = e.getMessage();
				logger.error(e.getMessage(), e);
			}

			// 結果JSONを生成
			JSONObject result = new JSONObject();
			result.put("success", success);
			result.put("msg", msg);

			// 出力の準備
			// @see https://www.sencha.com/forum/showthread.php?132949-Fileupload-Invalid-JSON-string
			// The server response is parsed by the browser to create the document for the IFRAME.
			// If the server is using JSON to send the return object,
			// then the Content-Type header must be set to "text/html" in order to tell the browser to insert the text unchanged into the document body.
			// content-type should be set to text/html
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(result.toString());
			return result.toString();

		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * eコミマップの登録情報を１件削除します.
	 * @return String
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/contents/delete")
	@ResponseBody
	public String deleteEMapContents(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){
		try {
			// TODO: 認証
			MapDB mapDB = MapDB.getMapDB();
			UserInfo userInfo = mapDB.getAuthIdUserInfo(loginDataDto.getEcomUser());
			LayerInfo layerInfo = mapDB.getLayerInfo(mapApiForm.layer);

			// 削除処理
			mapService.deleteFeature(userInfo, layerInfo, mapApiForm.fid);

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * eコミマップの登録情報を矩形で検索してJSONで取得します.
	 * eMapContents.jsp に対応しています.
	 * 参照系のためCSRF対策不要
	 * @return String
	 * @throws Exception
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/ecommap/contents/bbox", method=org.springframework.web.bind.annotation.RequestMethod.GET)
	@ResponseBody
	public String eMapContents(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) throws Exception {

		// TODO: 認証

		// ruleパラメータがあれば検索クエリMapを取得する
		Map<String, String> sldFilterQueryMap = null;
		if(StringUtil.isNotEmpty(mapApiForm.rule)) {
			try {
				HashMap<String, HashSet<String>> ruleVisible = mapApiForm.ruleVisible();
				StyledLayerDescriptor sld = SLDServlet.getSld(mapApiForm.cid, mapApiForm.mid, mapApiForm.getLayersArray(), ruleVisible, /*pageRate*/1.0f, /*iconRate*/1.0f, /*lineRate*/1.0f, /*textRate*/1.0f, /*iconMaxSize*/0);
				sldFilterQueryMap = SLDServlet2.getSLDFilterQueryMap(sld);
				logger.debug(sldFilterQueryMap);
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		List<JSONArray> results = new ArrayList<>();

		// レイヤ個別の時間指定
		// ※本来は eコミマップの関数の方で対応すべきだが、未対応なので検索を分割して結果をマージする。
		List<String> layers = new LinkedList<String>(Arrays.asList(mapApiForm.getLayersArray()));
		mapApiForm.layertime("");
		if(mapApiForm.layertimesMap!=null) {
			Set<String> layertimeLayerIds = mapApiForm.layertimesMap.keySet();
			for(String layertimeLayerId : layertimeLayerIds) {
				JSONArray json = mapService.getEMapContents(
						session,
						mapApiForm.mid,
						new String[]{layertimeLayerId},
						mapApiForm.getBboxDoubleArray(),
						mapApiForm.limit,
						mapApiForm.offset,
						mapApiForm.getTimeParams(layertimeLayerId),
						sldFilterQueryMap
				);
				results.add(json);
			}
			layers.removeAll(layertimeLayerIds);
		}

		// 通常の検索
		if(0<layers.size()) {
			JSONArray json = mapService.getEMapContents(
					session,
					mapApiForm.mid,
					layers.toArray(new String[layers.size()]),
					mapApiForm.getBboxDoubleArray(),
					mapApiForm.limit,
					mapApiForm.offset,
					mapApiForm.getTimeParams(),
					sldFilterQueryMap
			);
			results.add(json);
		}

		JSONArray json = null;
		if(results.size()==1) json = results.get(0);
		// 複数検索の場合は結果をマージする
		// ※offset はどの検索のものか不明になるので、利用できなくなる
		else {
			// [
			//    [2,10,0],
			//    [
			//         ["c9233",36,[141.891585326036,39.2783754744925,0],[["attr0","名称","82",12]],[],[0,0,null,"2014-04-24 20:43:50.504617",1,"システム管理者","2016-12-22 14:32:25.583356","2016-12-22 14:32:25.0",null]],
			//         ["c9256",102,[141.891190844171,39.2769860163112,26.011112213134766],[["attr0","名称","4",12]],[],[0,0,null,"2014-03-11 09:42:31.563211",1,"システム管理者","2016-12-26 15:35:40.184399","2016-12-26 06:35:40.265",null]]]
			//    ]

			// キーを distance にして、距離順 Map を生成
			long total = 0;
			long limit = 0;
			long offset = 0;
			TreeMap<Double, JSONArray> mix = new TreeMap<>();
			for(JSONArray result : results) {
				JSONArray countJson = result.getJSONArray(0);
				total += countJson.getLong(0); // 足し算
				limit = countJson.getLong(1);
				offset = countJson.getLong(2); // とりあえず代入

				JSONArray featureResults = result.getJSONArray(1);
				for(int idx=0; idx<featureResults.length(); idx++) {
					JSONArray featureResult = featureResults.getJSONArray(idx);
					//int frIdx = 0;
					//String layerId = featureResult.getString(frIdx++);
					//String featureId = featureResult.getString(frIdx++);
					JSONArray geomArray = featureResult.getJSONArray(2);
					double distance = geomArray.getDouble(geomArray.length()-1); // 最後が distance
					mix.put(distance, featureResult);
				}
			}

			// 距離順でJSONを生成
			json = new JSONArray();
			// count
			JSONArray countJson = new JSONArray();
			countJson.put(total);
			countJson.put(limit);
			countJson.put(offset);
			json.put(countJson);
			// feature results
			JSONArray featureResults = new JSONArray();
			for(Map.Entry<Double, JSONArray> entry : mix.entrySet()) {
				JSONArray featureResult = entry.getValue();
				featureResults.put(featureResult);
			}
			json.put(featureResults);
		}

		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		//PrintWriter out = response.getWriter();
		//out.print(json.toString());

		return json.toString();
	}

	/**
	 * 属性情報をJSONで取得します.
	 * @return String
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/attrInfo")
	@ResponseBody
	public String attrInfo(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {
		//TODO: validateAttrInfo
		// TODO: 認証
		try {
			JSONObject json = mapService.getAttrInfo(mapApiForm.layer);

			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			if(json!=null) {
				//out.print(json.toString());
				return json.toString();
			}
			else {
				json = new JSONObject();
				json.put("error", lang.__("Attribute info of layer ID ({0}) is not found.", mapApiForm.layer));
				//out.print(json);
				return json.toString();
			}
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		}

		return null;
	}

	/**
	 * 指定レイヤの最終更新日時をJSONで取得します.
	 * @return JSONレスポンス
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/updatetime")
	@ResponseBody
	public String layerLastUpdateTime(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {
		try {
			String layerId = mapApiForm.layer;
			String updatecolumn = mapApiForm.attrIds.get(0);
			Timestamp lasttime = tableService.getEcomDataLastUpdateTime(layerId, updatecolumn, mapApiForm.getTimeParams());
			String updatetime = getUpdateTimeBy(lasttime);

			JSONObject json = new JSONObject();
			// 指定レイヤ
			json.put("layerId", layerId);
			// 最終更新日時の属性ID
			json.put("updatecolumn", updatecolumn);
			// 指定時間パラメータ
			json.put("time", mapApiForm.time);
			// その時点での最終更新日時
			json.put("updateTime", updatetime);


			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(json.toString());
			return json.toString();
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;

	}

	/**
	 * eコミマップのWFSに代理で問い合わせる.
	 * rev1392 以降のeコミマップでのみ動作する.
	 * それ以前は WFS:GetFeature が GETのみの対応のため、
	 * WFSProxyServlet の POST に GetFeature の処理を追記する必要がある。
	 * (getWFSLayerId関数 が NullPointerException になるので
	 * nodeListが長さ0の場合はnullを返すように修正する)
	 * @return String
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/ecommap/wfsProxy"/*, method = RequestMethod.POST OpenLayersがGETでリクエストするため*/)
	public ResponseEntity<byte[]> wfsProxy(){
		try {
			String body = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
			String url = request.getParameter("url");
			String metadataId = request.getParameter("metadataid");
			String authorizationHeader = "";
			System.out.println("url: "+url);
			metadataId = metadataId == null ? "" : metadataId;
			if(session.getAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + metadataId) != null){
				authorizationHeader = (String)session.getAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + metadataId);
			}
			// TODO: ローカルのGeoServerへのリクエストならユーザ認証する
			// プロキシする
			String method = request.getMethod();
			String contentType = "application/xml; charset=UTF-8";
			boolean useCaches = false;
			try {
				String postData = body;
				HttpURLConnection http = HttpUtil.httpProxy(url, method,
						contentType, postData, useCaches, authorizationHeader);
				InputStream is = null;
				logger.debug(http.getResponseCode()+" "+url);
				if(http.getResponseCode()<400) {
					is = http.getInputStream();
				} else {
					is = http.getErrorStream();
				}

				// レスポンスのMIMEタイプを設定する
				//response.setContentType(http.getContentType());
				//response.setCharacterEncoding(http.getContentEncoding());
		    	final HttpHeaders headers= new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType(http.getContentType()));

				//PrintWriter writer = response.getWriter();
				//writer.write(IOUtils.toString(is, http.getContentEncoding()));

				// SaigaiTaskのサーバのGeoServerにリクエストして 404 Not Found の場合は警告を出す.
				if(http.getResponseCode()==404) {
					ResourceBundle pathBundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
					String localRootURL = pathBundle.getString("LOCAL_ROOT_URL");
					if(url.startsWith(localRootURL)) {
						String msg = url + " : http 404 Not Found.";
						msg += lang.__("In case not to open port, you should set GEOSERVER_WFS in PathInfo.properties to \"http://localhost:port number/geoserver/wfs?\".");
						logger.warn(msg);
					}
				}

				return new ResponseEntity<byte[]>(IOUtils.toByteArray(is), headers, HttpStatus.OK);

			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
		}

		return null;
	}

	/**
	 * 指定レイヤのSLDをHTTPレスポンスする
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/sld")
	@ResponseBody
	public String sld(){
		try {
			String mid = request.getParameter("mid");
			Long mapId = Long.parseLong(mid);
			String layerId = request.getParameter("layer");
			String sld = mapService.getSld(mapId, layerId);

			// レスポンスのMIMEタイプを設定する
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(sld.getBytes("UTF-8").length);
			IOUtils.write(sld, response.getOutputStream(), "UTF-8");
		} catch (FileNotFoundException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * 災害用レイヤIDを指定してメタデータを更新する.
	 * 地図ページのフィーチャ更新時に呼ばれる.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/updatemetadata")
	@ResponseBody
	public String updatemetadata(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){
		long trackmapinfoid = 0;
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			trackmapinfoid = trackData!=null&&trackData.trackmapinfoid!=null ? trackData.trackmapinfoid : 0L;
		}
		String layerId = mapApiForm.layer;
		boolean ret = clearinghouseService.updatemetadataByLayerId(layerId, trackmapinfoid);
		//ResponseUtil.write(""+ret,"text/plain","UTF-8");
		return ""+ret;
		//return null;
	}

	/**
	 * 凡例別の表示切替のチェック状態のセッション保存
	 * @return なし
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/sessionSLDRule", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String sessionSLDRule(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {

		// ruleMap
		Map<String, String> ruleMap = loginDataDto.getRuleMap();
		if(ruleMap==null) {
			loginDataDto.setRuleMap(new HashMap<>());
			ruleMap = loginDataDto.getRuleMap();
		}

		// set state
		if(StringUtil.isNotEmpty(mapApiForm.rule)) {
			for(String layerRule : mapApiForm.rule.split(",")) {
				String[] ruleElems = layerRule.split(":");
				if(0<ruleElems.length) {
					// 最初の要素がレイヤID
					String ruleLayerId = ruleElems[0];
					ruleMap.put(ruleLayerId, layerRule);
				}
			}
		}

		// clearrule
		if(StringUtil.isNotEmpty(mapApiForm.clearrule)) {
			for(String layerId : mapApiForm.clearrule.split(",")) {
				ruleMap.remove(layerId);
			}
		}

		return null;
	}

	/**
	 * 外部地図をメニュー別でセッションで管理する.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/sessionMetadata", method = RequestMethod.POST)
	@ResponseBody
	public String sessionMetadata(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {
		boolean success = false;
		String errorMsg = null;
		try{
			boolean isTraining = false;
			if(loginDataDto.getTrackdataid()!=0L) {
				TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
				isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
			}

			//net.sf.json.JSONArray records = mapApiForm.records;
			net.sf.json.JSONArray records = (net.sf.json.JSONArray)net.sf.json.JSONSerializer.toJSON(mapApiForm.records);

			if(records.size()==1) {
				// net.sf.json.JSONArray の場合、配列が2重になってしまうことがあるので、
				// 2重になってたら外す
				Object first = records.get(0);
				if(first instanceof net.sf.json.JSONArray) {
					records = (net.sf.json.JSONArray) first;
				}
			}
			net.sf.json.JSONObject sessionMetadatas = isTraining ? loginDataDto.getTrainingsessionMetadatas() : loginDataDto.getSessionMetadatas();
			ISO8601Formatter formatter = new ISO8601Formatter();
			String userUpdateTime = formatter.format(new Date());
			for(int i=0; i<records.size(); i++) {
				try {
					net.sf.json.JSONObject record =  records.getJSONObject(i);
					// eコミマップのレイヤ
					if(record.containsKey("layerId")) {
						Long menuinfoid = record.getLong("menuinfoid");
						String layerId = record.getString("layerId");
						String state = record.getString("state");
						if(state.equals("update")) {
							
							// 透明度
							Double layeropacity = null;
							if(record.has("layeropacity")) {
								layeropacity = record.getDouble("layeropacity");
							}

							// 対象レイヤを検索してアップデート
							List<MapreferencelayerInfo> mapreferencelayerInfos = mapreferencelayerInfoService.findByMenuidAndLayerid(menuinfoid, layerId);
							for(MapreferencelayerInfo mapreferencelayerInfo : mapreferencelayerInfos) {
								if(!layeropacity.equals(mapreferencelayerInfo.layeropacity)) {
									mapreferencelayerInfo.layeropacity = layeropacity;
									mapreferencelayerInfoService.update(mapreferencelayerInfo);
								}
							}
						}
					}
					// 外部地図
					else {
						// セッションに保存された外部地図レイヤか
						boolean sessionExternalMapLayer = true;
						if(record.containsKey("sessionExternalMapLayer")) sessionExternalMapLayer = record.getBoolean("sessionExternalMapLayer");
						if(sessionExternalMapLayer){
							Long menuinfoid = record.getLong("menuinfoid");
							String metadataid = record.getString("metadataid");
							String ckanresourceid = record.has("ckanresourceid") ? record.getString("ckanresourceid") : null;
							String state = record.getString("state");
							if(state.equals("update")) {
								// 更新の場合は userUpdateTime は変えない（凡例の表示順が変わってしまうため）
								// 更新先のキーを取得
								List<String> keys = new ArrayList<String>();
								keys.add(String.valueOf(menuinfoid)); // この地図に追加したレコードを取得
								keys.add("all"); // すべての地図に追加したレコードを取得
								for(String key : keys) {
									if(sessionMetadatas.has(key)) {
										net.sf.json.JSONObject sessionRecords = sessionMetadatas.getJSONObject(key);
										if(sessionRecords.has(metadataid)) {
											net.sf.json.JSONObject sessionRecord = sessionRecords.getJSONObject(metadataid);
											// 透過度を更新
											String recordKey = "layeropacity";
											if(record.has(recordKey)) {
												sessionRecord.put(recordKey, record.get(recordKey));
											}
											// 更新
											sessionRecords.put(metadataid, sessionRecord);
											// 更新できればそこで終了
											break;
										}
									}
								}
							}
							else {
								record.put("userUpdateTime", userUpdateTime); // ユーザ操作日時を記録
								// 追加先のキーを取得
								String key = String.valueOf(menuinfoid);
								if("add-all".equals(state)) key = "all"; // 全ての地図に追加の場合は、メニューIDではなく、allに追加する
								// なければ作成
								if(sessionMetadatas.has(key)==false) {
									sessionMetadatas.put(key, new net.sf.json.JSONObject());
								}
								net.sf.json.JSONObject sessionRecords = sessionMetadatas.getJSONObject(key);
								// 更新
								String sessionRecordsKey = metadataid + (StringUtil.isNotEmpty(ckanresourceid) ? "["+ckanresourceid+"]" : "");
								sessionRecords.put(sessionRecordsKey, record);
							}
						} else {
							externalmapdataInfoService.setLayeropacity(record.getString("metadataid"), record.getDouble("layeropacity"));
						}
					}
					// TODO: すべてに追加時に削除レコードがあるメニューには追加されないので、削除レコードを消す必要がある？
				} catch (net.sf.json.JSONException e) {
					logger.error(loginDataDto.logInfo(), e);
				}
			}
			logger.debug(sessionMetadatas);

			success = true;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			success = false;
			errorMsg = e.getMessage();
		}

		try {
			JSONObject ret = new JSONObject();
			ret.put("success", success);
			ret.put("info", errorMsg);

			// 出力の準備
			// application/json で返しても、なぜか<pre>が付いてしまい、JSONで出力できない。
			// uncaught exception: You're trying to decode an invalid JSON String: <pre>{"success":true,"errorCode":-1,"error":""}</pre>
			// そのため、text/htmlで送信する
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( ret.toString() );
			return ret.toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		//} catch (IOException e1) {
		//	e1.printStackTrace();
		}

		return null;

	}

	/* *
	 * エラーメッセージ出力
	 * @return null(フォワードしない)
	 * /
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/ecommap/error")
	@ResponseBody
	public String error() {
		String errorMsg = null;
		SizeLimitExceededException e = (SizeLimitExceededException) request.getAttribute(S2MultipartRequestHandler.SIZE_EXCEPTION_KEY);
		if (e != null) {
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
			String limit  = format.format((double)e.getPermittedSize()/1024/1024)+"MB";
			String actual = format.format((double)e.getActualSize()/1024/1024)+"MB";
			errorMsg = lang.__("File upload size ({0}) exceed limit ({1}).", actual, limit);
		}

		try {
			JSONObject ret = new JSONObject();
			ret.put("success", false);
			ret.put("info", errorMsg);

			// 出力の準備
			// application/json で返しても、なぜか<pre>が付いてしまい、JSONで出力できない。
			// uncaught exception: You're trying to decode an invalid JSON String: <pre>{"success":true,"errorCode":-1,"error":""}</pre>
			// そのため、text/htmlで送信する
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( ret.toString() );
			return ret.toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		//} catch (IOException e1) {
		//	e1.printStackTrace();
		}

		// JSON形式に不要な preタグ等を出さないようにする
		request.setAttribute("org.apache.struts.action.ERROR", null);
		request.setAttribute("org.apache.struts.action.MESSAGE", null);

		return null;
	}*/

	/**
	 * 指定したジオメトリをレイヤで切り出します.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/intersection", produces="application/json")
	@ResponseBody
	public ResponseEntity<String> intersection(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {
		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		// TODO: 認証
		try{
			// 切り出されるジオメトリのWKT
			String wkt = null;
			if(mapApiForm.wkt!=null && 0<mapApiForm.wkt.size()) {
				wkt = mapApiForm.wkt.get(0);
			}
			// 切り出しレイヤID
			String intersectLayerId = mapApiForm.layer;

			// 切り出す
			String intersectionWKT = spatialService.intersect(wkt, intersectLayerId);

			// 出力の準備
			JSONObject json = new JSONObject();
			json.put("wkt", intersectionWKT);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 地図画面の「この場所について」で位置情報やUTMコードを取得する.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/pointinfo", method = RequestMethod.POST)
	@ResponseBody
	public String pointinfo(){

		JSONArray ary = new JSONArray();
		int w = 250;

		String lat = request.getParameter("lat");
		String lon = request.getParameter("lon");
		if (lat == null || lon == null) return null;
		double dlon = Double.parseDouble(lon);
		double dlat = Double.parseDouble(lat);

		//住所
		String geocoderType = Config.getGeocoder();
		boolean isGoogleGeocoder = geocoderType!=null && geocoderType.toUpperCase().equals("GOOGLE");
		if(isGoogleGeocoder) {
			try {
				//Geocoder geocoder = new Geocoder();
				Geocoder geocoder = new Geocoder(jp.ecom_plat.map.db.MapDB.getMapDB().getOption("GOOGLE_API_KEY"),geocoderType);
				LatLng latlon = new LatLng();
				latlon.setLat(new BigDecimal(dlat));
				latlon.setLng(new BigDecimal(dlon));
				GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setLocation(latlon).setLanguage("ja").getGeocoderRequest();
				GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
				List<GeocoderResult> reslist = geocoderResponse.getResults();
				if (reslist.size() > 0) {
					GeocoderResult res = reslist.get(0);
					String addr = res.getFormattedAddress();
					if (addr != null) {
						if (addr.indexOf(',') > 0)
							addr = addr.substring(addr.indexOf(',')+1).trim();
						JSONObject jobj = pointinfoJSON(lang.__("Address"), w, addr);
						ary.put(jobj);
					}
				}
			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		}

		//座標
		try {
			DecimalFormat df = new DecimalFormat("0.00000");
			String xy = df.format(dlon)+","+ df.format(dlat);
			if (!loginDataDto.getLocalgovInfo().coordinatedecimal) {//60進法で表示
				df = new DecimalFormat("0.0");
				int hx = (int)dlon;
				int mx = (int)((dlon-hx)*60);
				String sx = df.format(((dlon-hx)*60-mx)*60);
				int hy = (int)dlat;
				int my = (int)((dlat-hy)*60);
				String sy = df.format(((dlat-hy)*60-my)*60);
				xy = ""+hx+"°"+mx+"'"+sx+" "+hy+"°"+my+"'"+sy;
			}
			JSONObject jobj = pointinfoJSON(lang.__("Coordinates<!--2-->"), w, xy);
			ary.put(jobj);
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		String wkt = "POINT("+lon+" "+lat+")";
		//グリッド
		try {
			// 17/07/25 自衛隊は３ケタまでしか使わないので4桁を3桁に変更
			String mgrs = tableService.getCoordinate(wkt, true, 3);
			JSONObject jobj = pointinfoJSON(lang.__("Grid"), w, mgrs);
			ary.put(jobj);
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}


		//try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(ary.toString());

			return ary.toString();
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		//	throw new ServiceException(e);
		//}

		//return null;
	}

	/**
	 * 位置情報JSONを生成する.
	 * @param name 名称
	 * @param w
	 * @param value
	 * @return 位置情報JSON
	 * @throws JSONException
	 */
	public JSONObject pointinfoJSON(String name, int w, String value) throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("fieldLabel", name);
		jobj.put("width", w);
		jobj.put("value", value);

		return jobj;
	}

	/**
	 * MGRSコードから経緯度を取得する
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/mgrs2lonlat")
	@ResponseBody
	public String mgrs2lonlat(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm) {
		JSONObject json = new JSONObject();
		if(StringUtil.isNotEmpty(mapApiForm.mgrs)) {
			String mgrs = mapApiForm.mgrs;
			try {
				MGRSCoord mgrs2degCoord = MGRSCoord.fromString(mgrs, null);
				// LonLat
				json.put("lon", mgrs2degCoord.getLongitude().degrees);
				json.put("lat", mgrs2degCoord.getLatitude().degrees);
				// MGRS code
				json.put("mgrs", mgrs2degCoord.toString());
				if(StringUtil.isNotEmpty(mgrs2degCoord.toString())) {
					try {
						String code = mgrs2degCoord.toString();

						// Grid Zone Designation
						//               (utm )(latBand )
						String regex = "^(\\d+)([A-Za-z])";
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(code);
						if(matcher.find()) {
							//String utmZone = matcher.group(1);    // UTMグリッドのゾーン番号
							//String utmLatBand = matcher.group(2); // UTMグリッドの横線
							code = matcher.replaceFirst(""); // 抽出部分を削除

							if(StringUtil.isNotEmpty(code)) {
								// 100km Grid Square ID
								regex = "^([A-Za-z])([A-Za-z])";
								pattern = Pattern.compile(regex);
								matcher = pattern.matcher(code);
								if(matcher.find()) {
									//String utm100kmLonBand = matcher.group(1); // Lon Band
									//String utm100kmLatBand = matcher.group(2); // Lat Band
									code = matcher.replaceFirst(""); // 抽出部分を削除
									int digits = 0;

									if(StringUtil.isNotEmpty(code)) {
										// Numerical location
										String numericalLocation = code; // easting + northing
										if(StringUtil.isNotEmpty(numericalLocation)) {
											// 長さが偶数かどうか
											if(numericalLocation.length()%2!=0) {
												logger.warn(lang.__("The length of MGRS code numerical location is odd. ") + " \""+numericalLocation+"\"");
											}
											else {
												digits = numericalLocation.length()/2;
											}
										}
									}

									json.put("mgrs_digits", digits);
								}
							}
						}
					} catch(Exception e) {
						logger.warn(lang.__("Failed to judge digits of MGRS code. MGRS: \"{0}\"", mgrs2degCoord.toString()));
					}
				}
			} catch(Exception e) {
				logger.warn(lang.__("Unable to convert MGRS code into coordinates. MGRS: \"{0}\"", mgrs));
			}
		}

		//try {
			// 出力の準備
			//response.setContentType("application/json");
			//response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print(json.toString());
			return json.toString();
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		//}

		//return null;
	}

	/**
	 * ランドマークデータ検索機能が有効か無効かをチェックする
	 * @return ランドマークデータ検索機能の有効フラグ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/landmark/valid", method = RequestMethod.POST)
	@ResponseBody
	public String landmarkValid(){
		try{
			//住所検索機能が有効か
			LandmarkInfo lInfo = landmarkInfoService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());

			// 出力の準備
			JSONObject json = new JSONObject();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			//有効
			if(lInfo != null && lInfo.valid){
				json.put("valid", 1);
			}else{
				json.put("valid", 0);
			}

			//結果をjsonで出力
			//PrintWriter out = response.getWriter();
			//out.print(json.toString());

			return json.toString();

		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}


	/**
	 * 1件のランドマークデータを登録する
	 * @return 登録結果
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/landmark/regist")
	@ResponseBody
	public String landmarkRegist(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){
		try{
			//住所検索機能が有効か
			LandmarkInfo lInfo = landmarkInfoService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());

			// 出力の準備
			JSONObject json = new JSONObject();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			//有効
			if(lInfo != null && lInfo.valid){

				net.sf.json.JSONArray landmarkData = (net.sf.json.JSONArray)net.sf.json.JSONSerializer.toJSON(mapApiForm.landmarkData);

				if(landmarkData.size()==1) {
					// net.sf.json.JSONArray の場合、配列が2重になってしまうことがあるので、
					// 2重になってたら外す
					Object first = landmarkData.get(0);
					if(first instanceof net.sf.json.JSONArray) {
						landmarkData = (net.sf.json.JSONArray) first;
					}
				}

				String landmark = "";//返り値用

				for(int i=0; i<landmarkData.size(); i++) {
					net.sf.json.JSONObject record = landmarkData.getJSONObject(i);
					landmark = record.getString("landmark");
					double lon = record.getDouble("lon");
					double lat = record.getDouble("lat");

					//重複チェック
					BeanMap conditions = new BeanMap();
					conditions.put(LandmarkDataNames.landmarkinfoid().toString(), lInfo.id);
					conditions.put(LandmarkDataNames.landmark().toString(), landmark);
					if(landmarkDataService.getCount(conditions) > 0) {
						json.put("error", lang.__("Land mark name [{0}] is already registered!", landmark));
						PrintWriter out = response.getWriter();
						out.print(json.toString());
						return null;
					}

					//データセット
					LandmarkData lData = new LandmarkData();
					lData.landmarkinfoid = lInfo.id;
					lData.groupid = loginDataDto.getGroupid();
					lData.landmark = landmark;
					lData.latitude = lat;
					lData.longitude = lon;

					//登録
					landmarkDataService.insert(lData);

				}

				//結果をjsonで出力
				json.put("landmark", landmark);
				//PrintWriter out = response.getWriter();
				//out.print(json.toString());
				return json.toString();

			//無効
			}else{
				//jsonとして何も出力しないとfail（reject）とみなされるが、
				//何かを出力するとdone（resolved）とみなされるので、返り値の扱いに注意
				json.put("error", lang.__("Landmark search function is not enabled."));
				//PrintWriter out = response.getWriter();
				//out.print(json.toString());
				return json.toString();
			}

		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * ランドマークデータを検索する
	 * @return 検索結果
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/map/landmark/search")
	@ResponseBody
	public String landmarkSearch(Map<String,Object>model, @Valid @ModelAttribute MapApiForm mapApiForm){
		try{
			//住所検索機能が有効か
			LandmarkInfo lInfo = landmarkInfoService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());

			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			//有効
			if(lInfo != null && lInfo.valid){

				//net.sf.json.JSONArray landmarkData = mapApiForm.landmarkData;
				net.sf.json.JSONArray landmarkData = (net.sf.json.JSONArray)net.sf.json.JSONSerializer.toJSON(mapApiForm.landmarkData);

				if(landmarkData.size()==1) {
					// net.sf.json.JSONArray の場合、配列が2重になってしまうことがあるので、
					// 2重になってたら外す
					Object first = landmarkData.get(0);
					if(first instanceof net.sf.json.JSONArray) {
						landmarkData = (net.sf.json.JSONArray) first;
					}
				}

				//結果をjsonArrayで出力
				JSONArray jsonArr = new JSONArray();

				for(int i=0; i<landmarkData.size(); i++) {
					net.sf.json.JSONObject record = landmarkData.getJSONObject(i);

					//データセット
					LandmarkData lData = new LandmarkData();
					lData.landmarkinfoid = lInfo.id;;
					lData.landmark = record.getString("landmark");

					//検索
					List<LandmarkData> dataList = landmarkDataService.findByLandmark(lData);
					//検索された全件を返す
					if(dataList.size()>0){
						for(LandmarkData data : dataList){
							JSONObject json = new JSONObject();
							json.put("landmark", data.landmark);
							json.put("lon", data.longitude.toString());
							json.put("lat", data.latitude.toString());
							jsonArr.put(json);
						}
					//無ければ空のまま
					}else{
					}
				}

				//結果をjsonで出力
				//PrintWriter out = response.getWriter();
				//out.print(jsonArr.toString());
				return jsonArr.toString();

			//無効
			}else{
				//検索時に都度有効でないメッセージが表示されるとおかしいので、無効時には何も表示しない。
				//JSONObject json = new JSONObject();
				//json.put("error", "ランドマーク検索機能が有効になっていません。");
				//PrintWriter out = response.getWriter();
				//out.print(json.toString());

				return null;
			}

		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * wktからグリッドを取得する
	 * @param wkt
	 * @return グリッド
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/getGrid", method = RequestMethod.GET)
	@ResponseBody
	public String getGrid(){

		JSONObject obj = new JSONObject();

		String wkt = request.getParameter("wkt");
		//グリッド
		try {
			String mgrs = tableService.getCoordinate(wkt, true, 3);
			obj.put("wkt", mgrs);
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		return obj.toString();
	}

}
