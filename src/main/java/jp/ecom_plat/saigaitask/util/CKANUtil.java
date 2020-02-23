package jp.ecom_plat.saigaitask.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.referencing.CRS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.LayoutInfo;
import jp.ecom_plat.map.db.MapDB;

public class CKANUtil {
    static final Logger logger = Logger.getLogger(CSWUtil.class);
	private static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	static final String ORGANIZATION_DATA = "organizationData";

	/**
     * メタデータ検索
     *
	 * @param ckanurl
     * @param keyword
     * @param resourceUrl
     * @param startPosition
     * @param maxRecords
     * @param orderby
	 * @return
	 */
	public static String getRecords(String ckanurl, String keyword, String resourceUrl, int startPosition, int maxRecords, int orderby) {
        try{
        	JSONObject json = new JSONObject();
     		String separator = "";
        	StringBuilder query = new StringBuilder();
       	
            // キーワード（AND）
            if (keyword != null && !keyword.isEmpty()){
            	String[] keywords = keyword.split(" ", -1);
            	if (keywords.length > 0) {
            		for (String word : keywords) {
            			query.append(separator);
            			searchFields(query, word);
	            		separator = " AND ";
            		}
            	}
           	}
            
            // リソース URL
            if (resourceUrl != null && !resourceUrl.isEmpty()) {
            	query.append(separator);
            	query.append("res_url:");
            	query.append(resourceUrl.replaceAll(":", "\\\\:"));
        		separator = " AND ";
            }
            
            if (query.length() > 0) {
        		json.put("q", query.toString());
            }
            
            // ソート順
            switch(orderby) {
            // 自動
            case 5:
            	break;
            // 登録が新しいもの
            case 11:
            	json.put("sort", "metadata_created desc");
            	break;
           	// 登録が古いもの
            case 10:
            	json.put("sort", "metadata_created asc");
            	break;
            }

            if (startPosition > 1) {
            	json.put("start", startPosition - 1);
            }
            json.put("rows", maxRecords);

            // CKAN API 実行
            StringBuilder sb = new StringBuilder();
            int st = sendHttpRequest(ckanurl + "/package_search", null, json, sb);
            if (st == HttpURLConnection.HTTP_OK)
                return sb.toString();

        } catch (JSONException e) {
        	e.printStackTrace();
        } finally {
        }
        return null;
	}
	
    /**
     * メタデータ検索（CSW 互換）
     *
     * @param ckanurl
     * @param condition
     * @param startpos
     * @param maxrec
     * @param orderby 
     * @param orderby
     * @return
     */
    public static String getRecords(String ckanurl, Map<String,String> condition, int startpos, int maxrec, int orderby) {
        try{
        	JSONObject json = new JSONObject();
     		String and = "";
        	StringBuilder query = new StringBuilder();

            // 範囲（地理境界ボックス）
            if (condition.get("BBOX") != null) {
            	JSONObject extras = new JSONObject();
            	extras.put("ext_bbox", condition.get("BBOX"));
            	json.put("extras", extras);
            }

            // メタデータの更新日時（システムが管理）による検索
            if (condition.get("UPDATETERM") != null) {
            	query.append(and);
            	String[] updateterm = condition.get("UPDATETERM").split(",");
            	query.append("metadata_modified:[");
            	query.append(updateterm[0]);
            	query.append(" TO ");
            	query.append(updateterm[1]);
            	query.append("]");
        		and = " AND ";
            }

            // キーワード（AND）
            if (condition.get("KEYWORD") != null) {
            	String[] keywords = condition.get("KEYWORD").split(" ", -1);
        		for (String word : keywords) {
        			query.append(and);
        			searchFields(query, word);
            		and = " AND ";
            	}
           	}
           	
            // キーワード（OR）
            if (condition.get("KEYWORDOR") != null) {
            	String[] keywords = condition.get("KEYWORDOR").split(" ", -1);
            	if (keywords.length > 0) {
            		String or = "";
        			query.append(and);
                	if (keywords.length > 1)
                		query.append("(");
            		for (String word : keywords) {
            			query.append(or);
            			searchFields(query, word);
            			or = " OR ";
            		}
                	if (keywords.length > 1)
                		query.append(")");
            		and = " AND ";
            	}
           	}
            
            // キーワード（NOT）
            if (condition.get("KEYWORDNOT") != null) {
            	String[] keywords = condition.get("KEYWORDNOT").split(" ", -1);
            	if (keywords.length > 0) {
            		for (String word : keywords) {
            			query.append(and);
            			query.append("NOT ");
            			searchFields(query, word);
	            		and = " AND ";
            		}
            	}
           	}
           	
            if (query.length() > 0) {
        		json.put("q", query.toString());
            }

            // 更新が古い順
            json.put("sort", "metadata_modified asc");

            if (startpos > 1) {
            	json.put("start", startpos - 1);
            }
            json.put("rows", maxrec);

            // CKAN API 実行
            StringBuilder sb = new StringBuilder();
            int st = sendHttpRequest(ckanurl + "/package_search", null, json, sb);
            if (st == HttpURLConnection.HTTP_OK)
                return GetRecordsResponseToJSON(sb.toString(), startpos);

        } catch (JSONException e) {
        	e.printStackTrace();
        } finally {
        }
        return null;
	}
    
    /**
     * キーワードが含まれる可能性のある全部のフィールドを含む条件式を作成する
     * 
     * @param query
     * @param word
     */
    private static void searchFields(StringBuilder query, String word) {
       	word = word.replaceAll("([+\\-&|!(){}\\[\\]\\^\"~*?:\\\\])", "\\\\$1");
    	query
    	.append("(title:").append(word)
    	.append(" OR notes:").append(word)
    	.append(" OR tags:").append(word)
    	.append(" OR autor:").append(word)
    	.append(" OR maintainer:").append(word)
    	.append(" OR organization:").append(word)
    	.append(" OR disaster_name:*").append(word)
    	.append("*)");
    }

    /**
     * CKAN からのレスポンスを、従来の CSW に互換の形式に変換する
     * 
     * @param sb
     * @param startpos
     * @return
     */
    private static String GetRecordsResponseToJSON(String jsonStr, int startpos) {
    	try {
	    	JSONObject json = new JSONObject(jsonStr);
	    	JSONObject result = json.getJSONObject("result");
	        if (json.getBoolean("success") && result != null) {
	        	JSONObject items = new JSONObject();
	            //items.put("requestId",　recid);
	            JSONArray results = result.getJSONArray("results");
	            JSONArray record = new JSONArray();
	            items.put("numMatch", result.getString("count"));
	            items.put("numReturn", String.valueOf(results.length()));
	            int nextRecord = startpos + results.length();
	            String nextrec = nextRecord > result.getInt("count") ? "0" : String.valueOf(nextRecord);
	            items.put("nextRecord", nextrec);
	            for (int i = 0; i < results.length(); i++) {
	                JSONObject rec = new JSONObject();
	            	JSONObject e = results.getJSONObject(i);
	                rec.put("metadataId", e.getString("id"));
	                rec.put("updateTime", e.getString("metadata_modified"));
	                rec.put("fileIdentifier", e.getString("name"));
	                rec.put("contactname", e.getString("author"));
	                rec.put("title", e.getString("title"));
	                rec.put("abstract", e.getString("notes"));
	                // メタデータが新しく登録された場合は「新規」、更新されたメタデータの場合は「更新」とする
	                try {
                		rec.put("status", lang.__("New")); 
	                	String metadata_created = e.getString("metadata_created");
	                	String metadata_modified = e.getString("metadata_modified");
	                	if(!metadata_created.equals(metadata_modified)) {
	                		rec.put("status", lang.__("Update<!--2-->")); 
	                	}
	                } catch(Exception e2) {
	                	logger.error("can't get status", e2);
	                }
	                JSONArray resources = e.getJSONArray("resources");
	                for (int j = 0; j < resources.length(); j++) {
	                	JSONObject resource = resources.getJSONObject(j);
	                	if ("OGC WMS".equals(resource.getString("format"))) {
	                		String url = resource.getString("url");
	                		if (!rec.has("wmsurl"))
	                			rec.put("wmsurl", url);
                			JSONArray urls;
                			if (!rec.has("wmsurls"))
                				rec.put("wmsurls", (urls = new JSONArray()));
                			else {
                				urls = rec.getJSONArray("wmsurls");
                				items.put("wmsMoreThanOne", true);
                			}
                			JSONObject jsonUrl = new JSONObject();
                			jsonUrl.put("url", url);
                			jsonUrl.put("name", resource.getString("name"));
                			urls.put(jsonUrl);
	                	}
	                	else if ("thumbnail.png".equals(resource.getString("name"))) {
	                		rec.put("thumbnail", resource.getString("url"));
	                	}
	                }
	                
	                // WMS があるのもののみ追加する
	                if (rec.has("wmsurl"))
	                	record.put(rec);
	             }
	            items.put("record", record);
	            return items.toString();
	        }
        }
        catch (JSONException e) {
        }
    	return null;
    }
	
	/**
	 * 画面に表示する「地図情報」を設定する
	 * 
	 * @param layerId
	 * @param json
	 */
	public static void getSpatial(Long mapId, String layerId, JSONObject json) {
		MapDB mapDB = MapDB.getMapDB();

		double[] bbox;
		try {
			// レイヤ内の全データの外接矩形を取得
			bbox = mapDB.getFeatureDataExtent(layerId);
			if (bbox != null) {
				bbox = scaleBBox(bbox, (bbox[2] - bbox[0]) * 1.1, (bbox[3] - bbox[1]) * 1.1);
			}
			// データがない場合は _layout テーブルよりマップの表示範囲を取得
			else {
				LayoutInfo layout = mapDB.getLayoutInfo(mapId);
				if (layout != null) {
					bbox = layout.mapExtent;
					if (bbox == null)
						return;
				}
			}
			json.put("minx", bbox[0]);
			json.put("miny", bbox[1]);
			json.put("maxx", bbox[2]);
			json.put("maxy", bbox[3]);
			json.put("spatial","{\"type\":\"Polygon\",\"coordinates\":[[["
					+ bbox[0] + "," + bbox[1] + "], ["
					+ bbox[2] + "," + bbox[1] + "], ["
					+ bbox[2] + "," + bbox[3] + "], ["
					+ bbox[0] + "," + bbox[3] + "], ["
					+ bbox[0] + "," + bbox[1] + "]]]}");
		} catch (Exception e) {
		}
	}

    /**
     * HTTP GET/POST リクエストを送信する
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param query 送信するクエリ（GET/POST）またはデータ（POST）
     * @param method GETまたはPOSTを指定
     * @param response レスポンスを格納する（出力）
     * @return HTTP Status
     */
    static int sendHttpRequest(String ckanwurl, String apikey, JSONObject json, StringBuilder response){
		long starttime = System.currentTimeMillis();
        int httpstatus;
        HttpURLConnection urlcon = null;
        String url = null;
        String method = "POST";
        try{
            url = ckanwurl;
            URL urlobj = new URL(url);
            urlcon = (HttpURLConnection)urlobj.openConnection();
            urlcon.setUseCaches(false);
            urlcon.setDefaultUseCaches(false);
            urlcon.setInstanceFollowRedirects(true);

            if (StringUtil.isNotEmpty(apikey)) {
                urlcon.setRequestProperty("Authorization", apikey);
            }
            urlcon.setRequestMethod(method);
            if ("POST".equals(method)){
                urlcon.setDoOutput(true);
                BufferedOutputStream reqbos = new BufferedOutputStream(urlcon.getOutputStream());
                reqbos.write(json.toString().getBytes("UTF-8"));
                reqbos.close();
            }
            httpstatus = urlcon.getResponseCode();
            logger.info("--- sendHttpRequest statuscode: "+httpstatus);
            byte[] buff = new byte[4096];
            int len;
            if(httpstatus == HttpURLConnection.HTTP_OK){
                BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream());
                while ((len = bis.read(buff, 0, buff.length)) > 0)
                	response.append(new String(buff, 0, len, "UTF-8"));
            }
            else {
            	InputStream is = urlcon.getErrorStream();
            	if (is != null) {
                    BufferedInputStream bis = new BufferedInputStream(is);
                    while ((len = bis.read(buff, 0, buff.length)) > 0)
                    	response.append(new String(buff, 0, len, "UTF-8"));
            	}
            }
            return httpstatus;
        }catch(Exception e){
        	logger.error("url: "+url);
            logger.error(e,e);
            return -1;
        }finally{
        	// https の場合、SSLオーバヘッドでリクエスト時間が延びるため、disconnectしてしまうとリクエストのたびにオーバヘッドがかかってしまう。
        	// リクエストが複数連続する場合は KeepAlive を利用してSSLのオーバヘッドを少なくするために disconnect() で接続を切らないようにする。
        	//if(urlcon != null)
        	//urlcon.disconnect();
    		long endtime = System.currentTimeMillis();
    		logger.info("[MethodDuration] CSWUtil.sendHttpRequest elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");
        }
    }

	/**
	 * メタデータ登録・更新
	 * @param items
	 * @return
	 */
	public static boolean uploadMetadata(String ckanurl, String apikey, Map<String, Object> items, boolean update, boolean isTraining) {
		try {
			JSONObject json = new JSONObject() {
				public JSONObject put(String key, Object value) throws JSONException {
					// 値が null でなく空文字列でない場合のみ put する
					if (value != null && !(value instanceof String && ((String)value).isEmpty())) {
						super.put(key, value);
					}
					return this;
				}
			};

			// 標準フィールド
			json.put("name", items.get("name"));
			json.put("title", items.get("title"));
			json.put("notes", items.get("abstr"));
			json.put("private", !"1".equals(items.get("isOpen")));
			json.put("license_id", items.get("licenseInfo"));
			json.put("version", items.get("version"));
			json.put("owner_org", items.get("organization"));
			json.put("author",  items.get("author"));
			json.put("author_email", items.get("authorEmail"));
			json.put("maintainer", items.get("maintainer"));
			json.put("maintainer_email", items.get("maintainerEmail"));
			json.put("state", "active");

			// タグ
			JSONArray tags = null;
			String strTags = (String)items.get("tags");
			if (strTags != null && !strTags.isEmpty()) {
				tags = new JSONArray();
				for (String strTag : strTags.split(",")) {
					JSONObject tag = new JSONObject();
					tag.put("name", strTag);
					tags.put(tag);
				}
				json.put("tags", tags);
				json.put("num_tags", tags.length());
			}
			
			// カスタムフィールド
			JSONArray extras = new JSONArray();
			putCustom(extras, "information_type", items.get("infoType"));
			putCustom(extras, "depend_information_type", items.get("depInfoType"));
//			putCustom(extras, "disaster_name", items.get("disasterName"));
//			putCustom(extras, "disaster_id", items.get("disasterId"));
			putCustom(extras, "system_name", items.get("systemName"));
			putCustom(extras, "system_url", items.get("systemUrl"));
			putCustom(extras, "language", items.get("language"));
			putCustom(extras, "minx", items.get("minx"));
			putCustom(extras, "miny", items.get("miny"));
			putCustom(extras, "maxx", items.get("maxx"));
			putCustom(extras, "maxy", items.get("maxy"));
			putCustom(extras, "spatial", items.get("spatial"));
			json.put("extras", extras);

			// メタデータを登録する
			StringBuilder sb = new StringBuilder();
			int st;
        	sb = new StringBuilder();
        	// 更新
			if (update) {
            	json.put("id", items.get("name"));
            	st = sendHttpRequest(ckanurl + "/package_update", apikey, json, sb);
			}
			// 新規
			else {
				// 念のため、あらかじめ削除する
				deleteMetadata((String)items.get("name"), apikey, isTraining);
				st = sendHttpRequest(ckanurl + "/package_create", apikey, json, sb);
			}
            
            // 結果を JSON に変換
            JSONObject data = null;
            String strData = null;
            if (sb.length() > 0) {
        		strData = sb.toString();
            	try {
            		data = new JSONObject(strData);
            	} catch (JSONException e) {
            	}
            }
            
            // エラー？
            if (st != HttpURLConnection.HTTP_OK || !data.getBoolean("success")) {
            	if (data != null)
            		items.put("error", data.getJSONObject("error").toString());
            	else if (strData != null)
            		items.put("error", strData);
            	return false;
            }

            // サムネイルを追加する
            String thumnailUrl = (String)items.get("thumnailUrl");
            if (thumnailUrl != null && !thumnailUrl.isEmpty()) {
	            json = new JSONObject();
	            json.put("package_id", data.getJSONObject("result").getString("id"));
	            json.put("name", "thumbnail.png");
	            json.put("format", "PNG");
	            json.put("url", thumnailUrl);
	            json.put("description", lang.__("Thumbnail URL"));
	            json.put("mimetype", "image/png");
	            sb = new StringBuilder();
	            st = sendHttpRequest(ckanurl + "/resource_create", apikey, json, sb);
            }
            
            // WMS URL
            String wmsUrl = (String)items.get("wms");
            if (wmsUrl != null && !wmsUrl.isEmpty()) {
	            json = new JSONObject();
	            json.put("package_id", data.getJSONObject("result").getString("id"));
	            json.put("name", "OGC WMS");
	            json.put("format", "OGC WMS");
	            json.put("url", wmsUrl);
	            json.put("description", "WMS URL");
	            sb = new StringBuilder();
	            st = sendHttpRequest(ckanurl + "/resource_create", apikey, json, sb);
            }
            
            // WFS URL
            String wfsUrl = (String)items.get("wfs");
            if (wmsUrl != null && !wmsUrl.isEmpty()) {
	            json = new JSONObject();
	            json.put("package_id", data.getJSONObject("result").getString("id"));
	            json.put("name", "OGC WFS");
	            json.put("format", "OGC WFS");
	            json.put("url", wfsUrl);
	            json.put("description", "WFS URL");
	            sb = new StringBuilder();
	            st = sendHttpRequest(ckanurl + "/resource_create", apikey, json, sb);
            }
            return true;
            
		} catch (JSONException e) {
			items.put("error", e.toString());
			return false;
		}
	}
	
	/**	
	 * サムネイル用の WMS リクエストを作成する
	 * @param wms
	 * @param cid
	 * @param mapId
	 * @param layerId
	 * @param epsgCode
	 * @return
	 */
	public static String thumbnailUrl(String wms, int cid, long mapId, String layerId, double[] bbox, int epsgCode) {
		if (bbox == null)
			return null;

		int thumbnailWidth = 400;
		int thumbnailHeight = 300;
		MapDB mapDB = MapDB.getMapDB();
		try {
			
			// サイズがゼロの場合
			if (bbox[0] == bbox[2] && bbox[1] == bbox[3]) {
				bbox = new double[] { bbox[0] - 0.01, bbox[1] - 0.01, bbox[2] + 0.01, bbox[3] + 0.01 };
			}
			
			// 座標変換
			if (epsgCode != 4326) {
				CoordinateReferenceSystem crsFrom = CRS.decode("EPSG:4326");
				CoordinateReferenceSystem crsTo = CRS.decode("EPSG:" + epsgCode);
				MathTransform transform = CRS.findMathTransform(crsFrom, crsTo, true);
				double[] newBbox = new double[4];
				transform.transform(bbox, 0, newBbox, 0, 2);
				bbox = newBbox;
			}
	
			// bbox の縦横比をサムネイルに合うように調整する
			double bboxWidth = bbox[2] - bbox[0];
			double bboxHeight = bbox[3] - bbox[1];
			if (thumbnailWidth * bboxHeight >= thumbnailHeight * bboxWidth)
				bboxWidth = bboxHeight * ((double)thumbnailWidth / (double)thumbnailHeight);
			else
				bboxHeight = bboxWidth * ((double)thumbnailHeight / (double)thumbnailWidth);
			bbox = scaleBBox(bbox, bboxWidth, bboxHeight);
	
			// WMW リクエストを作成する
			StringBuilder sb = new StringBuilder()
			.append(wms.replaceAll("\\?.*$", ""))
			.append("?SERVICE=WMS&TRANSPARENT=FALSE&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng")
			.append("&SRS=EPSG%3A" + epsgCode)
			.append("&cid=").append(cid)
			.append("&mid=").append(mapId)
			.append("&LAYERS=").append(layerId)
			.append("&BBOX=").append(BigDecimal.valueOf(bbox[0]).toPlainString())
			.append(",").append(BigDecimal.valueOf(bbox[1]).toPlainString())
			.append(",").append(BigDecimal.valueOf(bbox[2]).toPlainString())
			.append(",").append(BigDecimal.valueOf(bbox[3]).toPlainString())
			.append("&WIDTH=").append(thumbnailWidth)
			.append("&HEIGHT=").append(thumbnailHeight);
			return sb.toString();

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * bbox を、中心位置を固定して拡大／縮小する
	 * 
	 * @param bbox
	 * @param width
	 * @param height
	 * @return
	 */
	private static double[] scaleBBox(double[] bbox, double width, double height) {
		return new double[] {
				(bbox[0] + bbox[2] - width) * 0.5,
				(bbox[1] + bbox[3] - height) * 0.5,
				(bbox[0] + bbox[2] + width) * 0.5,
				(bbox[1] + bbox[3] + height) * 0.5
		};
	}
	
	private static void putCustom(JSONArray array, String key, Object value) throws JSONException {
		if (value != null && !((String)value).isEmpty()) {
			JSONObject json = new JSONObject();
			json.put("key", key);
			json.put("value", value);
			array.put(json);
		}
	}

    /**
     * メタデータ削除
     * クリアリングハウスに DeleteMetadata リクエストを送信してメタデータを削除する。
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param id 削除するメタデータのファイル識別子
     * @return 削除結果、true/false
     */
	public static boolean deleteMetadata(String mdFileID, String apikey, boolean isTraining) {
		try {
		    String ckanurl = Config.getString(isTraining ? "CKAN_URL_TRAINING" : "CKAN_URL");
	
	        JSONObject json = new JSONObject();
	        json.put("id", mdFileID);
	        StringBuilder sb = new StringBuilder();
	        int st = sendHttpRequest(ckanurl + "/package_delete", apikey, json, sb);
	        if (st != HttpURLConnection.HTTP_OK)
	            return false;
	        return true;
		}
		catch (JSONException e) {
			//e.printStackTrace();
			return false;
		}
	}

    /**
     * メタデータ取得
     * 指定したクリアリングハウスに GetRecordById リクエストを送信してメタデータを取得する。
     *
     * @param id メタデータのファイル識別子
     * @return メタデータ（MD_Metadata XML）
     */
	public static JSONObject getRecordById(String ckanurl, String apikey, String metadataid) {
		try {
			JSONObject json = new JSONObject();
			json.put("id", metadataid);
			StringBuilder sb = new StringBuilder();
	        int st = sendHttpRequest(ckanurl + "/package_show", apikey, json, sb);
	        if (st != HttpURLConnection.HTTP_OK)
	        	return null;
	        JSONObject data = new JSONObject(sb.toString());
	        if (!data.getBoolean("success"))
	        	return null;
	
	        json = new JSONObject();
	        JSONObject result = data.getJSONObject("result");

			// 標準フィールド
			json.put("infoType", getString(result, "name"));
			json.put("title", getString(result, "title"));
			json.put("abstr", getString(result, "notes"));
			json.put("isOpen", result.getBoolean("private") ? "2" : "1");
			json.put("version", getString(result, "version"));
			json.put("author",  getString(result, "author"));
			json.put("authorEmail", getString(result, "author_email"));
			json.put("maintainer", getString(result, "maintainer"));
			json.put("maintainerEmail", getString(result, "maintainer_email"));
			json.put("metadataCreated", getString(result, "metadata_created"));
			json.put("metadataModified", getString(result, "metadata_modified"));
			json.put("organization", getString(result, "owner_org"));
	        json.put(ORGANIZATION_DATA, getOrganizetion(ckanurl, apikey, json.getString("organization")));
			json.put("licenseInfo", getString(result, "license_id"));

			// タグ
			JSONArray tags = result.getJSONArray("tags");
			if (tags != null && tags.length() > 0) {
				sb = new StringBuilder();
				for (int i = 0; i < tags.length(); i++) {
					JSONObject tag = tags.getJSONObject(i);
					if ("active".equals(tag.getString("state"))) {
						sb.append(",");
						sb.append(tag.getString("name"));
					}
				}
				json.put("tags", sb.substring(1));
			}
	        
			// カスタムフィールド
			Map<String, String> custom = new HashMap<String, String>();
			custom.put("information_type", "infoType");
			custom.put("depend_information_type", "depInfoType");
//			custom.put("disaster_name", "disasterName");
//			custom.put("disaster_id", "disasterId");
			custom.put("system_name", "systemName");
			custom.put("system_url", "systemUrl");
			custom.put("language", "language");
			custom.put("minx", "minx");
			custom.put("miny", "miny");
			custom.put("maxx", "maxx");
			custom.put("maxy", "maxy");
			custom.put("spatial", "spatial");
			JSONArray extras = result.getJSONArray("extras");
			for (int i = 0; i < extras.length(); i++) {
				JSONObject extra = extras.getJSONObject(i);
				String key = extra.getString("key");
				String name = custom.get(key);
				if (name != null) {
					json.put(name, getString(extra, "value"));
				}
			}

			// リソース
	        JSONArray resources = result.getJSONArray("resources");
	        if (resources != null) {
		        for (int i = 0; i < resources.length(); i++) {
		        	JSONObject resource = resources.getJSONObject(i);
					if ("OGC WMS".equals(resource.getString("format"))) {
				        json.put("wmsurl", getString(resource, "url"));
				        json.put("wms", getString(resource, "url"));
		        	}
					else if ("OGC WFS".equals(resource.getString("format"))) {
				        json.put("wfsurl", getString(resource, "url"));
				        json.put("wfs", getString(resource, "url"));
		        	}
		        }
	        }
	        
	        return json;
	 	}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * メタデータ取得結果のJSONから組織名を取得
	 * @param record メタデータ取得結果のJSON
	 * @return 組織名（取得失敗時は null）
	 */
	public static String getOrganizationNameByRecord(JSONObject record) {
		if(record.isNull(ORGANIZATION_DATA))
			return null;
		try {
			JSONObject organizationData = record.getJSONObject(ORGANIZATION_DATA);
			JSONObject result = organizationData.getJSONObject("result");
			//String organizationName = result.getString("name");
			String organizationName = result.getString("display_name");
			return organizationName;
		} catch(JSONException e) {
			logger.error("json error", e);
			return null;
		}
	}
	
	/**
	 * JSONObject の getString が　null 値に対して "null" という文字列を返すことへの対応
	 * @param json
	 * @param name
	 * @return
	 */
	private static String getString(JSONObject json, String name) {
		try {
			if (json.isNull(name))
				return null;
			else
				return json.getString(name);
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 組織の一覧を取得する
	 * @return
	 */
	public static List<JSONObject> getOrganizetionList(String apikey, boolean isTraining) {
        try {
		    String ckanurl = Config.getString(isTraining ? "CKAN_URL_TRAINING" : "CKAN_URL");
		    JSONObject json = new JSONObject();
			json.put("permission", "create_dataset");
	        StringBuilder sb = new StringBuilder();
	        int st = sendHttpRequest(ckanurl + "/organization_list_for_user", apikey, json, sb);
	        if (st != HttpURLConnection.HTTP_OK)
	        	return null;
	    	JSONObject data = new JSONObject(sb.toString());
	        if (!data.getBoolean("success"))
	        	return null;
	        JSONArray result = data.getJSONArray("result");
	        List<JSONObject> list = new ArrayList<JSONObject>();
	        for (int i = 0; i < result.length(); i++) {
	        	list.add(result.getJSONObject(i));
	        }
	        return list;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 組織情報を取得する
	 * @return
	 */
	public static JSONObject getOrganizetion(String ckanurl, String apikey, String id) {
        try {
		    JSONObject json = new JSONObject();
			json.put("id", id);
	        StringBuilder sb = new StringBuilder();
	        int st = sendHttpRequest(ckanurl + "/organization_show", apikey, json, sb);
	        if (st != HttpURLConnection.HTTP_OK)
	        	return null;
	    	JSONObject data = new JSONObject(sb.toString());
	        if (!data.getBoolean("success"))
	        	return null;
	        return data;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * ライセンスの一覧を取得する
	 * @return
	 */
	public static List<JSONObject> getLicenseList(boolean isTraining) {
        try {
		    String ckanurl = Config.getString(isTraining ? "CKAN_URL_TRAINING" : "CKAN_URL");
		    JSONObject json = new JSONObject();
	        StringBuilder sb = new StringBuilder();
	        int st = sendHttpRequest(ckanurl + "/license_list", null, json, sb);
	        if (st != HttpURLConnection.HTTP_OK)
	        	return null;
	    	JSONObject data = new JSONObject(sb.toString());
	        if (!data.getBoolean("success"))
	        	return null;
	        JSONArray result = data.getJSONArray("result");
	        List<JSONObject> list = new ArrayList<JSONObject>();
	        for (int i = 0; i < result.length(); i++) {
	        	list.add(result.getJSONObject(i));
	        }
	        return list;
		} catch (JSONException e) {
			return null;
		}
	}

}
