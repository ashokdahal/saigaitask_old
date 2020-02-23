/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.osw.WMSUtils;
import jp.ecom_plat.map.servlet.ServletUtil;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.ExternalListDto;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * WFS のリクエストを行うクラスです
 */
@org.springframework.stereotype.Service
public class WFSService {

	Logger logger = Logger.getLogger(WFSService.class);
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	private static final Namespace NAMESPACE_WFS = Namespace.getNamespace("wfs","http://www.opengis.net/wfs");
	private static final Namespace NAMESPACE_OWS = Namespace.getNamespace("ows","http://www.opengis.net/ows");
	private static final Namespace NAMESPACE_GML = Namespace.getNamespace("gml","http://www.opengis.net/gml");
	private static final Namespace NAMESPACE_XLINK = Namespace.getNamespace("xlink","http://www.w3.org/1999/xlink");

    @Resource private ClearinghouseService clearinghouseService;
    @Resource private CkanService ckanService;
	/** ページDto */
	@Resource public PageDto pageDto;
	@Resource public LoginDataDto loginDataDto;

	/**
	 * クリアリングハウスメタデータＩＤより、WFS GetFeature の URL 等の情報を取得する
	 * @param metadataid
	 * @param isTraining 訓練フラグ
	 * @return
	 */
	public String[] initWfs(String metadataid, boolean isTraining, String authorizationHeader) {
		String getFeatureURL = null;
		String featureTypeName = null;
		String featureTypeTitle = null;
		String[] ret = null;
		String wfsCapsUrl = null;
		JSONObject jsonMetadata = null;
		String city = null;
		String administartiveArea = null;
		String localgovcode = null;
		String exTemp = null;

		// クリアリングハウスより情報を取得する
		try {
			jsonMetadata = (loginDataDto.isUseCkan() ? ckanService : clearinghouseService).getRecordById(metadataid, isTraining);
			if(loginDataDto.isUseCkan()) {
				try {
					localgovcode = jsonMetadata.getJSONObject("organizationData").getJSONObject("result").getString("title");
				} catch(Exception e) {
					logger.error("failed to get jsonMetadata organizationData.result.title(CKAN)", e);
				}
			}
			// 従来のクリアリングハウス
			else {
				city = jsonMetadata.has("cityCode") ? jsonMetadata.getString("cityCode") : null;
				if(city==null){
					throw new ServiceException(lang.__("Municipal code not acquired."));
				}
				administartiveArea = jsonMetadata.has("adminAreaCode") ? jsonMetadata.getString("adminAreaCode") : null;
				if(administartiveArea == null){
					throw new ServiceException(lang.__("Prefectural code not acquired."));
				}
				localgovcode = administartiveArea + city;
			}
			exTemp = jsonMetadata.has("exTemp") ? jsonMetadata.getString("exTemp") : null;

			// WFS の URL を取得する
			if (jsonMetadata.isNull("wfsurl") || (wfsCapsUrl = jsonMetadata.get("wfsurl").toString()).equals("")) {

				// 取得できない場合は WMS の URL より類推する
				String wmsCapsUrl = jsonMetadata.get("wmsurl").toString();
				wfsCapsUrl = wmsCapsUrl.replaceAll("WMS", "WFS").replaceAll("wms", "wfs");
				if (wfsCapsUrl.equals(wmsCapsUrl))
					wfsCapsUrl = null;
			}

			// GetCapabilities の URL を取得できた場合
			if (wfsCapsUrl != null) {

				// eコミの場合は GetCapabilities が実装されていなく、
				// GetFeatureのURLがクリアリングハウスに登録されるため、
				// GetFeature の場合は 本来 Capabilities のURLをGetFeature の URL として扱う
				if (wfsCapsUrl.toUpperCase().indexOf("GETFEATURE")!=-1) {
					getFeatureURL = wfsCapsUrl;
					ret = new String[] { getFeatureURL, null, null, localgovcode};
				}

				//GetCapabilities リクエストパラメータ追加
				if (!wfsCapsUrl.endsWith(".xml")) {
					wfsCapsUrl = wfsCapsUrl.replaceFirst("\\?", "&").replaceAll("\\&$", "");
					if (!wfsCapsUrl.matches("(?i).*\\&REQUEST=.*"))
						wfsCapsUrl += "&REQUEST=GetCapabilities";
					if (!wfsCapsUrl.matches("(?i).*\\&SERVICE=.*"))
						wfsCapsUrl += "&SERVICE=WFS";
					if (!wfsCapsUrl.matches("(?i).*\\&VERSION=.*"))
						wfsCapsUrl += "&VERSION=1.1.0";
					wfsCapsUrl = wfsCapsUrl.replaceFirst("&", "?");
				}

				//WFSCapabilities　取得
				InputStream is;
				HttpURLConnection conn = null;
				Element root = null;
				try {
					// GetCapabilities リクエスト発行する
					conn = ServletUtil.getHttpConnection(wfsCapsUrl, false);
					// 認証情報が入っていればHeaderに追加する
					if(StringUtil.isNotEmpty(authorizationHeader)){
						conn.setRequestProperty("Authorization", authorizationHeader);
					}
					int timeout = 30 * 1000;
					conn.setConnectTimeout(timeout);
					conn.setReadTimeout(timeout);
					is = conn.getInputStream();

					// 結果の XML を解析する
					SAXBuilder builder = new SAXBuilder();
					builder.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
					Document jdoc = builder.build(is);
					root = jdoc.getRootElement().detach();
					if (!root.getNamespace().equals(NAMESPACE_WFS) || !root.getName().equals("WFS_Capabilities"))
						throw new JDOMParseException("", null);

					// GetFeature の URL を取得する
					Element operationsMetadata = root.getChild("OperationsMetadata", NAMESPACE_OWS);
					List<Element> operationList = operationsMetadata.getChildren();
					Element getFeature = null;
					for (Element e : operationList) {
						if ("GetFeature".equals(e.getAttributeValue("name"))) {
							getFeature = e;
							break;
						}
					}
					Element dcp = getFeature.getChild("DCP", NAMESPACE_OWS);
					Element http = dcp.getChild("HTTP", NAMESPACE_OWS);
					Element get = http.getChild("Get", NAMESPACE_OWS);
					getFeatureURL = get.getAttributeValue("href", NAMESPACE_XLINK);
					// ↓デバッグコード
					//getFeatureURL = Config.getEcommapURL() + getFeatureURL.substring(getFeatureURL.indexOf("/map/")+1);
					// FeatureType の情報を取得する（複数ある場合は最初のものを取得）
					Element featureTypeList = root.getChild("FeatureTypeList", NAMESPACE_WFS);
					Element featureType = featureTypeList.getChild("FeatureType", NAMESPACE_WFS);
					Element name = featureType.getChild("Name", NAMESPACE_WFS);
					featureTypeName = name.getTextTrim();
					Element title = featureType.getChild("Title", NAMESPACE_WFS);
					featureTypeTitle = name.getTextTrim();
					// メタデータのWFSURLにあるタイプネームが優先
					List<NameValuePair> params = URLEncodedUtils.parse(new URI(wfsCapsUrl), "UTF-8");
					for (NameValuePair param : params){
						if(param.getName().equals("typeName"))
							featureTypeName = param.getValue();
					}

					ret = new String[] { getFeatureURL, featureTypeName, featureTypeTitle, localgovcode, exTemp };
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				finally {
					if (conn != null)
						conn.disconnect();
				}
			}

			// GetFeature の URL の取得に失敗した場合は WMS の  GetMap の URL より類推する
			if (getFeatureURL == null && jsonMetadata != null) {
				ret = (loginDataDto.isUseCkan() ? ckanService : clearinghouseService).getMapLayers(jsonMetadata, localgovcode, exTemp);
			}
			return ret;
		} catch (JSONException e1) {
			return null;
		} catch (ServiceException e2) {
			throw e2;
		}
	}

	/**
	 * URLのクエリ文字列からパラメータのMapを取得します.
	 * @param url URL
	 * @return パラメータがない場合は空のMap
	 */
	public Map<String, String> getUrlParams(String url) {
		Map<String, String> params = new HashMap<String, String>();
		// URL からパラメータを取得
		int qIdx = url.indexOf('?');
		if(qIdx!=-1 && qIdx!=url.length()-1) { // 末尾以外にあれば
			// クエリ解析
			String query = url.substring(qIdx+1);
			if(query!=null) {
				// keyvalue解析
				String[] keyvalues = query.split("&");
				for(String keyvalue : keyvalues) {
					String key = null;
					String value = null;
					int eqIdx = keyvalue.indexOf('=');
					String[] els = keyvalue.split("=");
					// ない場合
					if(eqIdx==-1) {
						key = keyvalue;
					}
					// 先頭の場合
					else if(eqIdx==0) {
						value = els[0];
					}
					// 末尾の場合
					else if(eqIdx==keyvalue.length()-1) {
						value = els[0];
					}
					else {
						key = els[0];
						value = els[1];
					}
					params.put(key, value);
				}
			}
		}
		return params;
	}

	/**
	 * WFS.DescribeFeatureType の JSON形式を取得します.
	 * @param wfsUrl WFSのURL
	 * @param typeName typeNameパラメータ
	 * @return WFS.DescribeFeatureType の JSON形式、 取得できなかった場合は null
	 */
	public JSONObject describeFeatureType(String wfsUrl, String typeName) {
		JSONObject describeFeatureType = null;

		// パラメータを上書き
		Map<String, String> params = getUrlParams(wfsUrl);
		params.put("request", "DescribeFeatureType");
		params.put("service", "WFS");
		params.put("version", "1.1.0");
		// eコミのURLの場合、推測されたWFSのURLなので、typeNameにはnullしか入らないため以下のコードは不要
		if(StringUtil.isNotEmpty(typeName)) params.put("typeName", typeName);
		params.put("outputFormat", "application/json");
		// パラメータを設定
		int qIdx = wfsUrl.indexOf('?');
		if(qIdx==-1) wfsUrl += '?';
		List<String> keyvalues = new ArrayList<String>();
		for(Map.Entry<String, String> entry : params.entrySet()) {
			keyvalues.add(entry.getKey() + "=" + entry.getValue());
		}
		wfsUrl = wfsUrl.split("\\?")[0] + "?" + jp.ecom_plat.saigaitask.util.StringUtil.join(keyvalues, "&");

		// http リクエスト
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			logger.debug("Connect WFS DescribeFeatureType: "+wfsUrl);
			conn = ServletUtil.getHttpConnection(wfsUrl, false);
			is = conn.getInputStream();

			String jsonStr = IOUtils.toString(is, "UTF-8");
			logger.debug("DescribeFeatureType(JSON): "+jsonStr);

			describeFeatureType = new JSONObject(jsonStr);
		} catch (KeyManagementException e) {
			logger.error(e.getMessage(), e);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if(conn!=null) {
				conn.disconnect();
			}
		}

		return describeFeatureType;
	}

	/**
	 * GetFeature リクエストを実行し、リスト表示用のデータを取得する
	 *
	 * @param wfsUrl WFS URL
	 * @param typeName TypeName
	 * @param timeParams 時間パラメータ
	 * @param authorizationHeader 認証ヘッダ
	 * @param exTemp メタデータ時間に関する情報
	 * @return 外部リストDto
	 */
	public ExternalListDto getList(String wfsUrl, String typeName, Date[] timeParams, String authorizationHeader, String exTemp, int npage) {
		return getListImpl(wfsUrl, typeName, timeParams, authorizationHeader, exTemp, npage, null, null);
	}

	/**
	 * GetFeature リクエストを実行し、履歴表示用のデータを取得する
	 *
	 * @param wfsUrl WFS URL
	 * @param typeName TypeName
	 * @param timeParams 時間パラメータ　※[0]=履歴の開始時刻、[1]=履歴の終了時刻
	 * @param authorizationHeader 認証ヘッダ
	 * @param exTemp メタデータ時間に関する情報
	 * @param npage ページ番号　※関数内で-2ページングOFFに固定する
	 * @param gid 履歴取得する gid
	 * @param _orgid 履歴取得する _orgid
	 * @return 外部リストDto
	 */
	public ExternalListDto getHistoryList(String wfsUrl, String typeName, Date[] timeParams, String authorizationHeader, String exTemp, int npage, Long gid, Long _orgid) {
		npage = -2; // ページングOFF
		return getListImpl(wfsUrl, typeName, timeParams, authorizationHeader, exTemp, npage, gid, _orgid);
	}
	
	public ExternalListDto getListImpl(String wfsUrl, String typeName, Date[] timeParams, String authorizationHeader, String exTemp, int npage, Long gid, Long _orgid) {
		ExternalListDto externallistDto = new ExternalListDto();
		externallistDto.columnNames = new ArrayList<String>();
		externallistDto.columnValues = new ArrayList<List<String>>();

		// GetFeature リクエストパラメータ追加
		wfsUrl = wfsUrl.replaceFirst("\\?", "&").replaceAll("\\&$", "");
		if (!wfsUrl.matches("(?i).*\\&REQUEST=.*"))
			wfsUrl += "&REQUEST=GetFeature";
		if (!wfsUrl.matches("(?i).*\\&SERVICE=.*"))
			wfsUrl += "&SERVICE=WFS";
		if (!wfsUrl.matches("(?i).*\\&VERSION=.*"))
			wfsUrl += "&VERSION=1.1.0";
		if (!wfsUrl.matches("(?i).*\\&TYPENAME=.*"))
			wfsUrl += "&TYPENAME=" + typeName;
		if (!wfsUrl.matches("(?i).*\\&cql_filter=.*")) {
			//時間パラメータはexTempに関係なく常に追加する
			//if(StringUtil.isNotEmpty(exTemp)) {
			String timeParam = null;
			//if(timeParams!=null&&timeParams.length==1) timeParam = WMSUtils.formatWMSTime(timeParams[0]);
			if(timeParams!=null&&timeParams.length==1) timeParam = TimeUtil.formatISO8601WithOffset(timeParams[0].getTime());
			if (timeParam == null) {
				timeParam = WMSUtils.formatWMSTime(new Date());
				// eコミマップは TimeZone を持っていないため、
				// eコミマップの TimeZone に合わせて ISO8601 で整形する
				timeParam = TimeUtil.formatISO8601WithOffset(System.currentTimeMillis());
			}
			// WFS は TIMEパラメータが使えないため、CQL_FILTER で対応する
			String cql_filter = "time_from<"+timeParam+" AND "+timeParam+"<time_to";
			// [例]
			// time_from<2016-10-07T18:54:33.509Z AND 2016-10-07T18:54:33.509Z<time_to
			// time_from%3C2016-10-07T18%3A54%3A33.509Z%20AND%202016-10-07T18%3A54%3A33.509Z%3Ctime_to

			// 履歴取得の場合は、timeParams[0]に開始時刻、timeParams[1]に終了時刻、
			// _orgid に取得したい地物の _orgid
			// time_to が infinity だった場合を考慮して、TIMEパラメータにORでgid条件を付与する
			if(timeParams!=null&&timeParams.length==2) {
				cql_filter = "";
				if(_orgid!=null) {
					cql_filter = "_orgid="+_orgid;
				}
				if(StringUtil.isNotEmpty(cql_filter)) cql_filter += " AND ";
				if(gid!=null) cql_filter += "(";
				cql_filter += "("+TimeUtil.formatISO8601WithOffset(timeParams[0].getTime())+"<time_to AND time_to<"+TimeUtil.formatISO8601WithOffset(timeParams[1].getTime())+")";
				if(gid!=null) cql_filter += " OR id IN ("+gid+"))";
			}
	        try {
				wfsUrl += "&cql_filter=" + URLEncoder.encode(cql_filter, "UTF-8");
	        } catch (UnsupportedEncodingException e) {
	            throw new IORuntimeException(e);
	        }
		//}
		}

		// ①全件数取得のためのURLを作る
		String wfsUrl2 = wfsUrl;
		if (!wfsUrl2.matches("(?i).*\\&resultType=.*"))
			wfsUrl2 += "&resultType=hits";
		wfsUrl2 = wfsUrl2.replaceFirst("&", "?");

		// GetFeature　取得
		InputStream is2;
		HttpURLConnection conn2 = null;
		Element root2 = null;
		int countx = 0;
		try {
			// GetFeature リクエストを発行する
			logger.debug("WFS URL: "+wfsUrl2);
			conn2 = ServletUtil.getHttpConnection(wfsUrl2, false);
			// 認証情報が入っていればHeaderに追加する
			if(StringUtil.isNotEmpty(authorizationHeader)){
				conn2.setRequestProperty("Authorization", authorizationHeader);
			}
			is2 = conn2.getInputStream();

			// 結果の XML を解析する
	    	SAXBuilder builder = new SAXBuilder();
			builder.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
			Document jdoc = builder.build(is2);
			root2 = jdoc.getRootElement().detach();
			if (!root2.getNamespace().equals(NAMESPACE_WFS) || !root2.getName().equals("FeatureCollection"))
				throw new JDOMParseException("", null);

		// 全件数を取得する。
		Attribute featureCount = root2.getAttribute("numberOfFeatures");
		countx = Integer.parseInt(featureCount.getValue());
		externallistDto.setCount(countx);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			if (conn2 != null)
				conn2.disconnect();
		}


		// ②データ取得のURLを作る
		int pagerow = pageDto.getPagerow();
		// 表示件数が全件数より多い場合は通常のページングOFFの画面を表示する。
		if (pagerow > countx) {
			npage = -2;
		}
		//最初に表示するレコードのインデックス番号
		int starti = 0;
		// npageが-2のときはページングフラグがOFFのとき。
		if (npage > -2) {
			// MAXFEATURESパラメータに指定した数の件数を取得する
			if (!wfsUrl.matches("(?i).*\\&MAXFEATURES=.*"))
				//実際には次のページの一件目も表示しないといけないので＋１
				pagerow = pagerow + 1;
				wfsUrl += "&MAXFEATURES="+ pagerow;
				// startIndexパラメータに指定した値の次のインデックスから取得する
				//（例）startIndex=1なら、2番目のデータから取得する。
				if (!wfsUrl.matches("(?i).*\\&startIndex=.*"))
					if (npage>0) {
						starti = pagerow * npage - (npage-1)-1;
					}
					else if (npage == -1) {
						pagerow = pagerow -1;
						npage = (int)Math.ceil( (double)countx / pagerow)-1;
						pagerow = pagerow +1;
						starti = pagerow * npage - (npage-1)-1;
					}
				wfsUrl += "&startIndex=" + starti;
		}

		if (!wfsUrl.matches("(?i).*\\&SortBy=.*"))
			wfsUrl += "&SortBy=_orgid+A,time_from+D";

		wfsUrl = wfsUrl.replaceFirst("&", "?");

		// GetFeature　取得
		InputStream is;
		HttpURLConnection conn = null;
		Element root = null;
		try {
			// GetFeature リクエストを発行する
			logger.debug("WFS URL: "+wfsUrl);
			conn = ServletUtil.getHttpConnection(wfsUrl, false);
			// 認証情報が入っていればHeaderに追加する
			if(StringUtil.isNotEmpty(authorizationHeader)){
				conn.setRequestProperty("Authorization", authorizationHeader);
			}
			is = conn.getInputStream();

			// 結果の XML を解析する
	    	SAXBuilder builder = new SAXBuilder();
			builder.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
			Document jdoc = builder.build(is);
			root = jdoc.getRootElement().detach();
			if (!root.getNamespace().equals(NAMESPACE_WFS) || !root.getName().equals("FeatureCollection"))
				throw new JDOMParseException("", null);

			// そのページに表示される件数を取得する。
			Attribute featureCount = root.getAttribute("numberOfFeatures");
			countx = Integer.parseInt(featureCount.getValue());
			externallistDto.setIndex(countx);

			// 結果を JSON に変換する
			Element featureMembers = root.getChild("featureMembers", NAMESPACE_GML);
			List<Element> features = featureMembers.getChildren();
			/*
			 * 属性名、属性値を読み込む 属性はレコード毎に出現するものが違う場合がある. 並びも保証されない.
			 * つまり、最初の行だけで属性名の一覧は得られない.
			 */
			for (Element feature : features) {
				List<String> columnValues = new ArrayList<String>();
				List<Element> properties = feature.getChildren();

				// 属性値を読み込む
				Map<String, String> attrs = new HashMap<String, String>();
				for (Element property : properties) {
					String columnName = property.getName();
					String columnValue = property.getTextTrim();
					attrs.put(columnName, columnValue);
				}

				// 属性値を保存しておく
				Map<String, String> attrMap = new HashMap<>(attrs);
				for(Attribute attribute : feature.getAttributes()) {
					attrMap.put(attribute.getName(), attribute.getValue());
				}
				externallistDto.columnValueMaps.add(attrMap);
				
				// すでにリストに追加済みのカラム名を順番に登録する
				for (String columnName : externallistDto.columnNames) {
					columnValues.add(attrs.get(columnName));
					attrs.remove(columnName);
				}

				// リストにないカラムがあればリストを拡張する
				int addedColumnNameNum = attrs.size();
				if (0 < addedColumnNameNum) {
					for (Map.Entry<String, String> entry : attrs.entrySet()) {
						// リストにカラムを追加
						String columnName = entry.getKey();
						externallistDto.columnNames.add(columnName);
						// このレコードの属性値を保存
						String value = entry.getValue();
						columnValues.add(value);
					}
					// リストにすでに追加したレコードに、追加されたカラムの列値をnullで埋める
					for (List<String> columnValue : externallistDto.columnValues) {
						for (int count = 0; count < addedColumnNameNum; count++) {
							columnValue.add(null);
						}
					}
				}

				// このフィーチャの属性値を保存
				externallistDto.columnValues.add(columnValues);
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			if (conn != null)
				conn.disconnect();
		}
		return externallistDto;
	}

	/**
	 * ラベルのMap<ラベル, attrId>を初期化
	 * label要素があれば Mapを作成する
	 * @return
	 */
	public Map<String, String> getLabelMap(String wfsUrl, String typeName) {
		// DescribeFeatureType リクエストで、属性情報を取得する
		JSONObject describeFeatureType = describeFeatureType(wfsUrl, typeName);

		Map<String, String> labelMap = null;
		if(describeFeatureType!=null) {
			JSONArray featureTypes = null;
			try {
				featureTypes = describeFeatureType.getJSONArray("featureTypes");
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
			}
			if(featureTypes!=null) {
				for(int idx=0; idx<featureTypes.length(); idx++) {
					try {
						JSONObject featureType = featureTypes.getJSONObject(idx);
						// DescribeFeatureType で typeName指定するので結果は１つのはずなので、typeName のチェックは不要
						/*if(externallistDto.typeName.equals(featureType.getString("typeName")))*/ {
							labelMap = new HashMap<String, String>();
							boolean hasLabel = false;
							JSONArray properties = featureType.getJSONArray("properties");
							for(int propertiesIdx=0; propertiesIdx<properties.length(); propertiesIdx++) {
								JSONObject property = properties.getJSONObject(propertiesIdx);
								String attrId = property.has("name") ? property.getString("name") : null;
								String label = property.has("label") ? property.getString("label") : null;
								labelMap.put(label, attrId);
								if(property.has("label")) hasLabel = true;
							}
							if(hasLabel==false) labelMap=null; // label要素を持つ属性がなければ、Mapは使わない
							else break;
						}
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		return labelMap;
	}
}
