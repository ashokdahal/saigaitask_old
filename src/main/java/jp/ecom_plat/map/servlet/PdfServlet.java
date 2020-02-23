/* Copyright (c) 2009 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.map.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.ecom_plat.map.db.FilteredFeatureId;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.file.KeyFile;
import jp.ecom_plat.map.osw.WMSUtils;
import jp.ecom_plat.map.pdf.PdfMapWriter;
import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.EcommapServletContext;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.framework.util.Base64Util;
import org.seasar.framework.util.StringUtil;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * レイヤのグレーアウトに対応したPDF出力サーブレット.
 * eコミの PdfServlet を移植して、グレーアウト処理を追加してある.
 * [マージ履歴]
 * 2016/10/18 trunk@3880
 *
 * <div lang="ja">
 * 地図印刷用のPFDを出力するServlet。<br/>
 * Postでのレスポンス出力の他、後からダウンロードできるように一旦ファイルで出力し、セッションにPDFファイルのパスを登録しておく。
 * </div>
 *
 * <div lang="en">
 * Servlet to output PDF for printint map. <br/>
 * To output other than POST response, first output the file, then save PDF file path to session to support later download.
 * </div>
 */
public class PdfServlet extends HttpServlet
{
	/**
	 * <div lang="ja">tmpファイル名prefix 。</div>
	 * <div lang="en">Prefix of tmp filename.</div>*/
	private static final String TMP_PREFIX = "ecommap";
	/**  tmpファイル削除期限 48時間 */
	private static final long TMP_EXP = 48*3600*1000;

	// Logger
	private static Logger logger = Logger.getLogger(PdfServlet.class.getName());
	private static final long serialVersionUID = 1L;

	//private String alphaImageUrl;
	private String localRootUrl;

	private String legendServletURL;
	private String localLegendUrl;

	private String localWmsUrl;

	private String thumbServletUrl;

	private String nameSpace;

	private MapDB mapDB;

	BaseFont baseFont = null;

	/**<div lang="ja">Progressカウント用。</div>
	 * <div lang="en">For count progress.</div>*/
	HashMap<String, int[]> progressMap = new HashMap<String, int[]>();

	/**<div lang="ja">TMPファイル名保存用 String[]{ファイルパス, タイトル}。</div>
	 * <div lang="en">For count progress.</div>*/
	HashMap<String, String[]> fileNameMap = new HashMap<String, String[]>();

	/**
	 * <div lang="ja">
	 * キャンセルとThread動作確認用。
	 * docが無ければThread停止。
	 * doc.isOpen==false ならキャンセル中 。
	 * </div><div lang="en">
	 * To cancel and confirm the Thread action.
	 * If there is no doc, stop Thread.
	 * doc.isOpen==false means cancelling.
	 * </div>
	 * */
	HashMap<String, Document> docMap = new HashMap<String, Document>();

	/**
	 * <div lang="ja">
	 * プレビューキャンセル確認用。
	 * docが無ければThread停止。
	 * doc.isOpen==false ならキャンセル中 。
	 * </div><div lang="en">
	 * To confirm preview cancelling.
	 * If there is no doc, stop Thread.
	 * doc.isOpen==false means cancelling.
	 * </div>
	 * */
	HashMap<String, Document> previewDocMap = new HashMap<String, Document>();

	/** email 通知用 */
	HashMap<String, String> emailMap = new HashMap<String, String>();

	@Override
	public void init() throws ServletException
	{
		super.init();

		ResourceBundle bundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
		this.localRootUrl = bundle.getString("LOCAL_ROOT_URL");
		//this.legendServletURL = bundle.getString("LEGEND_URL");
		//if(!this.legendServletURL.startsWith("http")) this.legendServletURL = this.localRootUrl + this.legendServletURL;
		this.localLegendUrl = bundle.getString("GEOSERVER_LEGEND");
		if(!this.localLegendUrl.startsWith("http")) this.localLegendUrl = this.localRootUrl + this.localLegendUrl;
		this.localWmsUrl = bundle.getString("GEOSERVER_WMS");
		if(!this.localWmsUrl.startsWith("http")) this.localWmsUrl = this.localRootUrl + this.localWmsUrl;

		this.thumbServletUrl = bundle.getString("LOCAL_ROOT_URL")+bundle.getString("THUMBNAIL_URL");;
		if(!this.thumbServletUrl.startsWith("http")) this.thumbServletUrl = this.localRootUrl + this.thumbServletUrl;

		this.mapDB = MapDB.getMapDB();

		//Init Font
		ResourceBundle bundle2 = ResourceBundle.getBundle("ResourceInfo", Locale.getDefault());
		this.nameSpace = bundle2.getString("NAMESPACE");
		String fontFilePath = null;
		if (this.baseFont == null) {
			//fontFilePath = getServletContext().getRealPath("/")+bundle2.getString("FONT");//bundle2.getString("FONT_LINUX");
			fontFilePath = Config.getFontFilePath();
			try {
				this.baseFont = BaseFont.createFont(fontFilePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				//this.baseFont = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H",false);
			} catch (Exception e1) {
				try {
					this.baseFont = BaseFont.createFont(fontFilePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
					//this.baseFont = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H",false);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}

		/*try {
			this.scaleFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(fontFilePath));
		} catch (Exception e) {
			this.scaleFont = new java.awt.Font(java.awt.Font.SERIF, java.awt.Font.PLAIN, 12);
			e.printStackTrace();
		}*/
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		SaigaiTaskDBLang lang = SaigaiTaskLangUtils.initLang(request);

		//<div lang="ja">セッションIDに応じたダウンロード状況を返す</div>
		//<div lang="en">Return download status associated with session ID</div>
		String sessionId = request.getParameter("sid");
		if (sessionId == null) sessionId = request.getSession().getId();

		if (request.getParameter("cancel") != null) {
			synchronized (this.progressMap) {
				int[] counts = this.progressMap.get(sessionId);
				if (counts != null) {
					counts[0] = 0;
					counts[1] = 0;
				}
			}
			Document doc;
			synchronized (docMap) {
				doc = docMap.get(sessionId);
			}
			response.setContentType("application/json; charset=UTF-8");
			if (doc != null && doc.isOpen()) {
				synchronized (doc) { doc.close(); }
				response.getWriter().print("{error:\'"+lang.__("Canceled")+"\'}");
			} else {
				response.getWriter().print("{error:\'"+lang.__("Not create PDF")+"\'}");
			}
			return;
		}

		//パラメータ取得
		RequestParams params = new RequestParams(request);

		//<div lang="ja">ファイルを再ダウンロード</div>
		//<div lang="en">Redownload file</div>
		if (request.getParameter("download") != null) {
			synchronized (this.fileNameMap) {
				String[] fileInfo = this.fileNameMap.get(sessionId);
				if (fileInfo == null) return;
				File file = new File(fileInfo[0]);
				if (!file.exists()) return;
				this.outputFile(request, response, file, fileInfo[1]);
				return;
			}
		}

		//通知先email変更時
		if (request.getParameter("notice") != null) {
			String email = params.get("email");
			response.setContentType("application/json; charset=UTF-8");
			if (email != null) {
				synchronized (this.emailMap) {
					this.emailMap.put(sessionId, email);
				}
				response.getWriter().print("{success:\"set email\"}");
			} else {
				//nullなら登録解除
				synchronized (this.emailMap) {
					this.emailMap.remove(sessionId);
				}
				response.getWriter().print("{success:\"remove email\"}");
			}
			return;
		}

		response.setContentType("text/html");
		int[] counts = null;
		synchronized (this.progressMap) {
			counts = this.progressMap.get(sessionId);
		}
		try {
			if (counts == null) response.getWriter().println("[0,0]");
			else response.getWriter().println("["+counts[0]+","+counts[1]+"]");
		} catch (Exception e) { e.printStackTrace();}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		SaigaiTaskDBLang lang = SaigaiTaskLangUtils.initLang(request);

		// 凡例画像取得は危機管理クラウドのLegendAction経由にする（外部地図対応のため）
		legendServletURL = Config.getRequestContextURL(request)+"/page/map/legend?";

		//パラメータ取得
		RequestParams params = new RequestParams(request);

		//<div lang="ja">ユーザチェック</div>
		//<div lang="en">Check user</div>
		String authId;
		UserInfo userInfo = null;
		try {
			authId = UserAuthorization.getAuthorizedId(request);
			userInfo = mapDB.getAuthIdUserInfo(authId);
		} catch (NoSuchAlgorithmException e3) { e3.printStackTrace(); }

		if (userInfo == null) {
			if (request.getServerName().equals("localhost")) {
				userInfo = new UserInfo("offline", lang.__("Off line user"), UserInfo.LEVEL_ADMIN);
			} else {
				userInfo = UserInfo.createGuestUser();
			}
		}

		//<div lang="ja">Thread停止チェック用</div>
		//<div lang="en">To check whether Thread stops or not </div>
		Document sessionDoc = null;

		//<div lang="ja">プレビュー画像作成モード</div>
		//<div lang="en">Preview image generating mode</div>
		boolean preview = params.getBoolean("preview");
		if (preview) {
			synchronized (previewDocMap) {
				sessionDoc = previewDocMap.get(request.getSession().getId());
			}
			try {
				if (sessionDoc != null) sessionDoc.close();
			} catch (Exception e) { logger.warn(e); e.printStackTrace();}

		} else {
			synchronized (docMap) {
				sessionDoc = docMap.get(request.getSession().getId());
			}

			if (sessionDoc != null) {
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().print("{error:\'"+lang.__("Cancel processing")+"\'}");
				return;
			}
		}
		//<div lang="ja">地図印刷権限チェック</div>
		//<div lang="en">Check map printing permission</div>
		String markerPath = localRootUrl+request.getContextPath()+"/map/";

		boolean check = request.getParameter("check") != null;
		//<div lang="ja">Progress用sessionid</div>
		//<div lang="en">sessionid for Progress</div>
		String sessionId = request.getParameter("sid");
		if (sessionId == null) sessionId = request.getSession().getId();

		//cid
		int cid = params.getInt("cid");

		//mapId
		long mapId =  params.getLong("mid");
		if (mapId == 0) {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().print("{error:\'"+lang.__("No map ID")+"\'}");
			return;
		}
		MapInfo mapInfo = mapDB.getMapInfo(mapId);

		//登録情報取得用URLに時間パラメータを設定
		String contentsWmsUrl = localWmsUrl;
		//<div lang="ja">出力用クラス初期化 リクエストごとに生成</div>
		//<div lang="en">Initializatioin class for output   Generate for each request</div>
		PdfMapWriter pdfMapWriter = new PdfMapWriter(request.getSession(), cid, mapInfo, userInfo, localRootUrl, legendServletURL, localLegendUrl, contentsWmsUrl, thumbServletUrl, nameSpace, mapDB, baseFont, markerPath);

		//登録情報の時間パラメータ
		String contentsTimeParam = params.get("time");
		if (contentsTimeParam == null) {
			contentsTimeParam = WMSUtils.formatWMSTime(new Date());
		}
		//レイヤ個別の時間パラメータ 登録情報はレイヤ単位、主題図はサーバ単位、主題図画像はレイヤ単位
		//カンマ区切り "レイヤID1,時間1,レイヤID2,時間2"
		HashMap<String, String> layerTimeParams = new HashMap<String, String>();
		if (params.has("layertimes")) {
			String val = params.get("layertimes");
			if (val != null) {
				String[] values = val.split(",");
				for (int i=1; i<values.length; i=i+2) {
					layerTimeParams.put(values[i-1], values[i]);
				}
			}
		}
		//時間パラメータを設定
		pdfMapWriter.setTimeParams(contentsTimeParam, layerTimeParams);

		////////////////////////////////////////////////////////////////
		//	<div lang="ja">パラメータ</div>
		//<div lang="en">Parameter</div>
		////////////////////////////////////////////////////////////////
		try {

			//<div lang="ja">タイルサイズ取得</div>
			//<div lang="en">Get tile resize</div>
			String tileSizeOption = mapDB.getOption("TILESIZE", mapInfo.communityId);
			if (tileSizeOption != null) try { pdfMapWriter.setTileSize(Integer.parseInt(tileSizeOption)); } catch (Exception e) {e.printStackTrace();}

			//<div lang="ja">印刷種別</div>
			//<div lang="en">Printing type</div>
			boolean printMap = params.getBoolean("printmap", false);
			boolean printLegend = params.getBoolean("printlegend", false);
			boolean printList = params.getBoolean("printlist", false);
			pdfMapWriter.setMapOutput(
				printMap, printLegend, printList,
				params.getBoolean("printindex", false)
			);

			//地図のEPSG
			int epsg = params.getInt("epsg", 900913);

			//<div lang="ja">カンマ区切りBBOX文字列 4326座標系 (指定が無ければ地図初期範囲)</div>
			//<div lang="en">BBOX Param. If it is not specified, use map initialize boundary</div>
			pdfMapWriter.setBbox(params.getBbox("bbox"));

			//地図編集キー
			String mapkey = params.get("mapkey");
			if (mapkey != null && !KeyFile.hasKeyInfo(mapkey)) mapkey = null;
			if (mapkey != null) pdfMapWriter.setMapKey(mapkey);

			//<div lang="ja">用紙</div>
			//<div lang="en">Paper size</div>
			pdfMapWriter.setPaperSize(params.get("pagesize", "a4"), params.getBoolean("rotate"));

			// SaigaiTask 主題図の個別透明度対応
			String refLayersParam = request.getParameter("reflayers");
			{
				JSONArray refJSON = new JSONArray();
				if (refLayersParam.length() > 0) {
					refJSON = new JSONArray(refLayersParam);
					for (int i=0; i<refJSON.length(); i++) {
						JSONArray jsonArray = refJSON.getJSONArray(i);
						// eコミマップの主題図レイヤは透明度を個別で設定することができない
						// そのため、透明度を個別で設定するにはレイヤを分ける必要がある。
						// リクエストでは分ける必要があるレイヤIDは ref10#1 の様にレイヤIDの後ろに＃区切りでナンバリングして一意にしている。
						// ref10#1 というレイヤIDの場合は、ref10 のレイヤ情報をコピーしてセッションに保存する
						String layerId = jsonArray.getString(0);
						if(layerId.indexOf("#")!=-1) {
							String cloneLayerId = layerId;
							layerId  = layerId.substring(0, layerId.indexOf("#"));
							//String cloneNum = layerId.substring(layerId.indexOf("#"));
							LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerId);
							layerInfo.layerId = cloneLayerId; // レイヤIDだけ変える
							LayerInfo.setSessionLayerInfo(request.getSession(), layerInfo);
						}
					}
				}
			}
			// SaigaiTask 主題図画像の透明度対応
			// SaigaiTask 外部地図印刷機能
			String overlayLayersParam = request.getParameter("overlaylayers");
			{
				// 外部地図は主題図(reference)または主題図画像(overlay)に追加する
				JSONArray refJSON = new JSONArray();
				if (refLayersParam.length() > 0) {
					refJSON = new JSONArray(refLayersParam);
				}

				// MapInfo.MapLayerInfo
				Vector<MapLayerInfo> mapLayerInfos = (Vector<MapLayerInfo>) mapInfo.getMapLayerIterable();
				String key = null; // JSON のキー

				// 外部地図のLayerInfo をリクエストパラメータから作成し、セッションに保存する.
				String exLayersParam = request.getParameter("exlayers");
				if (exLayersParam!=null && exLayersParam.length() > 0) {
					JSONArray exJSON = new JSONArray(exLayersParam);
					for (int i=0; i<exJSON.length(); i++) {
						JSONArray jsonArray = exJSON.getJSONArray(i);
						String layerId = jsonArray.getString(0);

						// リクエストパラメータからレイヤ情報を生成
						JSONObject layerInfoJSON = new JSONObject(request.getParameter("layerInfo."+layerId));
						System.out.println(layerInfoJSON);

						// レイヤ種別の取得
						short layerType = 0;
						key = "type"; if(layerInfoJSON.has(key)) layerType = (short) layerInfoJSON.getInt(key);
						
						float opacity = (float) (layerInfoJSON.has("opacity") ? layerInfoJSON.getDouble("opacity") : 1);

						// 印刷リクエストに追加
						if(layerType==ClearinghouseService.LAYER_TYPE_EXTERNALMAP_WMS) {
							refJSON.put(jsonArray); // 主題図に追加
						}
						else if(layerType==ClearinghouseService.LAYER_TYPE_EXTERNALMAP_XYZ) {
							if(StringUtil.isNotEmpty(overlayLayersParam)) overlayLayersParam+=",";
							overlayLayersParam+=layerId+":"+opacity;
						}
						else {
							logger.error(lang.__("External map of {0} not support for print function.", "LayerInfo.type"+layerType));
							continue;
						}

						// レイヤ情報を生成する
						LayerInfo layerInfo = new LayerInfo();
						MapLayerInfo mapLayerInfo = new MapLayerInfo();
						// 認証情報用
						String authData = "";
						boolean parentAuth = false;
						{
							// レイヤ情報を作成し、登録
							if(layerType==ClearinghouseService.LAYER_TYPE_EXTERNALMAP_WMS) {
								layerInfo.type = LayerInfo.TYPE_REFERENCE_WMS;
							}
							else if(layerType==ClearinghouseService.LAYER_TYPE_EXTERNALMAP_XYZ) {
								layerInfo.type = LayerInfo.TYPE_OVERLAY_XYZ;
							}
							else logger.fatal("unexpected error at PdfServlet LayerInfo.type mapping.");
							layerInfo.layerId = layerId;
							layerInfo.name = layerInfoJSON.has("name") ? layerInfoJSON.getString("name") : null;
							layerInfo.wmsURL = layerInfoJSON.has("wmsURL") ? layerInfoJSON.getString("wmsURL") : null;
							layerInfo.wmsFormat = layerInfoJSON.has("wmsFormat") ? layerInfoJSON.getString("wmsFormat") : null;
							layerInfo.params = layerInfoJSON.has("params") ? layerInfoJSON.getString("params") : null;
							String metadataId = layerInfoJSON.has("metadataId") ? layerInfoJSON.getString("metadataId") : "";
							// 認証情報があれば取得
							//String wmsProxy = layerInfoJSON.has("wmsproxy") ? layerInfoJSON.getString("wmsproxy") : null;
							if(!metadataId.equals("")){
								// username:userpassの形式に戻す
								String decodeStr = authorizationDecode(request.getSession(), metadataId);
								// こちらで認証出来ているかのフラグ
								// 子レイヤ側にのみmetadataidが登録されている場合、wmsURLは子レイヤの処理で修正する為
								if(!decodeStr.equals("")) parentAuth = true;
								layerInfo.wmsURL = setAuthorityURL(layerInfo.wmsURL, decodeStr);
							}
							LayerInfo.setSessionLayerInfo(request.getSession(), layerInfo);

							// 地図レイヤ情報をリクエストパラメータから作成し登録
							mapLayerInfo.layerId = layerId;
							mapLayerInfo.opacity = opacity;
							mapLayerInfos.add(mapLayerInfo);
							MapLayerInfo.setSessionMapLayerInfo(request.getSession(), mapId, mapLayerInfo);
						}

						// WMS の LAYERS用のレイヤ情報を生成する
						String layers = jsonArray.getString(2);
						if (layers.length()>0) {
							String[] childrenLayer = layers.split(",");
							for (int j=0; j<childrenLayer.length; j++) {
								String childLayerId = childrenLayer[j];
								// レイヤ情報
								LayerInfo childLayerInfo = layerInfo.cloneLayerInfo();
								childLayerInfo.type = LayerInfo.TYPE_REFERENCE;
								childLayerInfo.layerId = childLayerId;
								// リクエストパラメータから得られたレイヤ情報を設定
								JSONObject childLayerInfoJSON = null;
								String childLayerInfoJSONString = request.getParameter("layerInfo."+childLayerId);
								if(StringUtil.isNotEmpty(childLayerInfoJSONString)) {
									childLayerInfoJSON = new JSONObject(childLayerInfoJSONString);
									key = "name"; if(childLayerInfoJSON.has(key)) childLayerInfo.name = childLayerInfoJSON.getString(key);
									key = "featuretypeId"; if(childLayerInfoJSON.has(key)) childLayerInfo.featuretypeId = childLayerInfoJSON.getString(key);
									key = "wmsLegendURL"; if(childLayerInfoJSON.has(key)) childLayerInfo.wmsLegendURL = childLayerInfoJSON.getString(key);
									// 認証情報を確認
									String metadataId = "";
									key = "metadataId"; if(childLayerInfoJSON.has(key)) metadataId = childLayerInfoJSON.getString(key);
									if(!metadataId.equals("")){
										// username:userpassの形式に戻す
										String decodeStr = authorizationDecode(request.getSession(), metadataId);
										// 子レイヤ側にのみmetadataidが登録されている場合、wmsURLは子レイヤの処理で修正する為
										if(!parentAuth){
											layerInfo.wmsURL = setAuthorityURL(layerInfo.wmsURL, decodeStr);
										}
										layerInfo.wmsLegendURL = setAuthorityURL(layerInfo.wmsLegendURL, decodeStr);
									}
								}
								// セッションに保存
								LayerInfo.setSessionLayerInfo(request.getSession(), childLayerInfo);

								// 地図レイヤ情報をリクエストパラメータから作成し登録
								MapLayerInfo childMapLayerInfo = new MapLayerInfo();
								childMapLayerInfo.layerId = childLayerInfo.layerId;
								childMapLayerInfo.parent = layerInfo.layerId;
								childMapLayerInfo.opacity = mapLayerInfo.opacity;
								mapLayerInfos.add(mapLayerInfo);
								MapLayerInfo.setSessionMapLayerInfo(request.getSession(), mapId, childMapLayerInfo);
							}
						}
					}
				}

				// reflayers に外部地図レイヤを含める
				refLayersParam = refJSON.toString();
			}

			//長さ0の文字列なら出力しない
			EcommapServletContext ecommapServletContext = new EcommapServletContext();
			pdfMapWriter.setLayes(ecommapServletContext, request.getParameter("baselayer"), overlayLayersParam,
					refLayersParam, request.getParameter("kmllayers"),
					request.getParameter("contentslayers"), request.getParameterValues("listlayers[]"),
					params.getBoolean("memoVisible", true),
					params.getInt("mgrsPrecision", -1),
					params.getInt("mgrsDisplayType", -1),
					request.getCookies()
			);
			//塗り分け表示切替パラメータ設定
			if (params.has("rule")) {
				pdfMapWriter.setRuleVisible(params.get("rule"));
			}

			////////////////////////////////////////////////////////////////
			//<div lang="ja">タイトル 空欄の場合は出力しない</div>
			//<div lang="en">Title</div>
			pdfMapWriter.mapTitle = params.get("maptitle", "");
			if (request.getParameter("maptitle") == null) pdfMapWriter.mapTitle = mapInfo.title;
			pdfMapWriter.titleAlign = params.getInt("titlealign", 0);
			pdfMapWriter.titleFontSize = 0;
			if (!params.getBoolean("titlefontauto")) pdfMapWriter.titleFontSize = params.getFloat("titlefontsize");

			////////////////////////////////////////////////////////////////
			//<div lang="ja">説明</div>
			//<div lang="en">Description</div>
			pdfMapWriter.description = params.get("description", "");
			pdfMapWriter.descAlign = params.getInt("descalign", PdfMapWriter.ALIGN_LT);
			pdfMapWriter.descFontSize = params.getFloat("descfontsize");
			if (params.getBoolean("descfontauto")) pdfMapWriter.descFontSize = 0;
			//<div lang="ja">最大表示幅のページに対するパーセント</div>
			//<div lang="en">Maximum percentage for page display</div>
			pdfMapWriter.descPageWidth = params.getFloat("descpagewidth", 50);

			////////////////////////////////////////////////////////////////
			//<div lang="ja">地物ポップアップ</div>
			//<div lang="en">Feature popup</div>
			pdfMapWriter.featureDescription = params.get("feature_desc");
			pdfMapWriter.featureDescAlign = params.getInt("feature_descalign", PdfMapWriter.ALIGN_LT);
			pdfMapWriter.featureDescFontSize = params.getFloat("feature_descfontsize");
			if (params.getBoolean("feature_descfontauto")) pdfMapWriter.featureDescFontSize = 0;
			//<div lang="ja">最大表示幅のページに対するパーセント</div>
			//<div lang="en">Maximum percentage for page display</div>
			pdfMapWriter.featureDescPageWidth = params.getFloat("feature_descpagewidth", 50);

			//<div lang="ja">マーカー</div>
			//<div lang="en">Marker</div>
			pdfMapWriter.popupMarker = params.get("popup_marker");
			pdfMapWriter.popupMarkerSize = params.getFloat("popup_marker_size");
			pdfMapWriter.featureLonLat = null;
			if (pdfMapWriter.popupMarker != null)
				pdfMapWriter.featureLonLat = new double[]{params.getDouble("feature_lon", 0), params.getDouble("feature_lat", 0)};
			pdfMapWriter.drawPopupLine = params.getBoolean("popup_line");

			////////////////////////////////
			//fit Page
			pdfMapWriter.fitToPage = params.getBoolean("fitpage", true);

			pdfMapWriter.innerTitle = params.getBoolean("innertitle", true);

			//layout type

			//<div lang="ja">アイコンの倍率 ((ページ解像度*pageScaleRate)/タイル画像描画解像度)*iconTextRate = 倍率;</div>
			//<div lang="en">Icon scale ((Page resolution * pageScaleRate) / Tile image drawing resolution) * iconTextRate = scale;</div>
			double defaultRate = pdfMapWriter.pageScaleRate * pdfMapWriter.iconTextDefaultRate;
			pdfMapWriter.iconRate = defaultRate * params.getDouble("iconrate", 1.0);
			pdfMapWriter.lineRate = defaultRate * params.getDouble("linerate", pdfMapWriter.iconRate);
			pdfMapWriter.textRate = defaultRate * params.getDouble("textrate", pdfMapWriter.iconRate);
			//タイルの関係上ラベルは実解像度の5倍までに制限
			if (pdfMapWriter.textRate > 5) pdfMapWriter.textRate = 5;

			//<div lang="ja">地図分割数</div>
			//<div lang="en">Map's number of partition</div>
			pdfMapWriter.pageCols = params.getInt("cols", 1);
			pdfMapWriter.pageRows = params.getInt("rows", 1);

			////////////////////////////////
			//<div lang="ja">スケール</div>
			//<div lang="en">Scale</div>
			pdfMapWriter.scaleAlign = params.getInt("scalealign", PdfMapWriter.ALIGN_RB);
			pdfMapWriter.scaleAllPage = params.getBoolean("scalepages");

			////////////////////////////////
			//<div lang="ja">凡例</div>
			//<div lang="en">Legend</div>
			pdfMapWriter.setLegendParams(params.getBoolean("legendcolauto")?0:params.getInt("legendcols", 0),
					params.getFloat("legendrate", 1.0f),
					params.getShort("legendpos", PdfMapWriter.ALIGN_LB),
					params.getBoolean("legendposchk"),
					params.getShort("legendcontents"),
					params.getShort("legendref"),
					params.getShort("legendbase")
			);

			////////////////////////////////
			//<div lang="ja">帳票表示</div>
			//<div lang="en">Display form</div>
			//int listType = -1;
			//try { listType = Integer.parseInt(request.getParameter("listtype")); } catch (Exception e) {}
			pdfMapWriter.listFontSize = 9;
			pdfMapWriter.listColumnWidth = 180;
			pdfMapWriter.listColumnNum = 0;
			if (!params.getBoolean("listcolauto")) {
				pdfMapWriter.listColumnNum = params.getInt("listcols");
			}
			pdfMapWriter.listFileNum = params.getInt("listfile");

			//<div lang="ja">地図ページマージン {marginLeft, marginRight, marginTop, marginBottom}</div>
			//<div lang="en">Map page margin {marginLeft, marginRight, marginTop, marginBottom}</div>
			boolean noFrame = params.getBoolean("noframe");
			float[] pageMargin;
			if (noFrame) {
				pageMargin = new float[]{0, 0, 0, 0};
			} else {
				pageMargin = new float[]{
					Utilities.millimetersToPoints(params.getFloat("mapml", 5)),
					Utilities.millimetersToPoints(params.getFloat("mapmr", 5)),
					Utilities.millimetersToPoints(params.getFloat("mapmt", 5)),
					Utilities.millimetersToPoints(params.getFloat("mapmb", 5))
				};
			}
			pdfMapWriter.pageMargin = pageMargin;

			//<div lang="ja">凡例を地図内に表示ならtrue</div>
			//<div lang="en">If the legend is displayed inside the map, then true</div>
			boolean legendNoFrame = params.getBoolean("legendnoframe");
			//<div lang="ja">凡例のマージン</div>
			//<div lang="en">Legend margin</div>
			if (legendNoFrame) {
				pdfMapWriter.legendMargin = new float[]{0, 0, 0, 0};
			} else {
				pdfMapWriter.legendMargin = new float[]{
					Utilities.millimetersToPoints(params.getFloat("legendml", 10)),
					Utilities.millimetersToPoints(params.getFloat("legendmr", 10)),
					Utilities.millimetersToPoints(params.getFloat("legendmt", 10)),
					Utilities.millimetersToPoints(params.getFloat("legendmb", 10))
				};
			}

			//<div lang="ja">地図内凡例マージン</div>
			//<div lang="en">Legend margin inside the map</div>
			pdfMapWriter.legendMapMargin = new float[]{
				Utilities.millimetersToPoints(params.getFloat("legendmapml", 5)),
				Utilities.millimetersToPoints(params.getFloat("legendmapmr", 5)),
				Utilities.millimetersToPoints(params.getFloat("legendmapmt", 5)),
				Utilities.millimetersToPoints(params.getFloat("legendmapmb", 5))
			};

			//<div lang="ja">地物がある場合のみ凡例表示</div>
			pdfMapWriter.legendDisplay = FormUtils.getShortParameter(request, "legenddisplay", PdfMapWriter.LEGEND_INCLUDING_ONLY);

			//<div lang="ja">一覧のマージン</div>
			//<div lang="en">List margin</div>
			boolean listNoFrame = params.getBoolean("listnoframe");
			if (listNoFrame) {
				pdfMapWriter.listMargin = new float[]{0, 0, 0, 0};
			} else {
				pdfMapWriter.listMargin = new float[]{
					Utilities.millimetersToPoints(params.getFloat("listml", 15)),
					Utilities.millimetersToPoints(params.getFloat("listmr", 15)),
					Utilities.millimetersToPoints(params.getFloat("listmt", 15)),
					Utilities.millimetersToPoints(params.getFloat("listmb", 15))
				};
			}
			//<div lang="ja">地図のマージン （初期値はpageMarginと同じだが、タイトルの地図外表示、ページに合わせない時の縦横比で余白ができる場合はあとで変更する）</div>
			//<div lang="en">Map margin (Initialized value is the same to pageMargin, but the title is displayed outside the map, if the page is not suitable, the vertical and horizontal margin may be adjusted later)</div>
			pdfMapWriter.mapOuterMargin = new float[]{ pageMargin[0], pageMargin[1], pageMargin[2], pageMargin[3] };

			//<div lang="ja">ヘッダ</div>
			//<div lang="en">Header</div>
			pdfMapWriter.headerLeftType = params.get("header_l", ""); if ("null".equals(pdfMapWriter.headerLeftType)) pdfMapWriter.headerLeftType = "";
			pdfMapWriter.headerCenterType = params.get("header_c", ""); if ("null".equals(pdfMapWriter.headerCenterType)) pdfMapWriter.headerCenterType = "";
			pdfMapWriter.headerRightType = params.get("header_r", ""); if ("null".equals(pdfMapWriter.headerRightType)) pdfMapWriter.headerRightType = "";
			pdfMapWriter.headerLeftText = params.get("header_l_text", "");
			pdfMapWriter.headerCenterText = params.get("header_c_text", "");
			pdfMapWriter.headerRightText = params.get("header_r_text", "");

			//<div lang="ja">
			//フッタ
			//ページ番号</div>
			//<div lang="en">
			//Footer
			//Page number
			//</div>
			pdfMapWriter.footerPage = params.getBoolean("fpage");
			pdfMapWriter.footerMapPage = params.getBoolean("fmappage");

			//<div lang="ja">プレビュー作成パラメータ</div>
			//<div lang="en">Parameter for preview</div>
			pdfMapWriter.prevRow = 0;
			pdfMapWriter.prevCol = 0;
			if (preview) {
				pdfMapWriter.prevRow = params.getInt("prev_row");
				pdfMapWriter.prevCol = params.getInt("prev_col");
			}

			//<div lang="ja">インデックス表示</div>
			//<div lang="en">Display index</div>
			pdfMapWriter.indexEnabled = params.getBoolean("index_enabled");
			pdfMapWriter.indexTypeH = params.getShort("index_h", PdfMapWriter.INDEX_NONE);
			pdfMapWriter.indexTypeV = params.getShort("index_v", PdfMapWriter.INDEX_NONE);
			pdfMapWriter.indexCols = params.getShort("index_cols", (short)1);
			pdfMapWriter.indexRows = params.getShort("index_rows", (short)1);
			pdfMapWriter.indexSize = Utilities.millimetersToPoints(params.getFloat("index_size", 5));
			pdfMapWriter.indexMargin = new float[4];
			try {
				pdfMapWriter.indexMargin[0] = Utilities.millimetersToPoints(params.getFloat("indexml", pdfMapWriter.pageMargin[0]));
				pdfMapWriter.indexMargin[1] = Utilities.millimetersToPoints(params.getFloat("indexmr", pdfMapWriter.pageMargin[1]));
				pdfMapWriter.indexMargin[2] = Utilities.millimetersToPoints(params.getFloat("indexmt", pdfMapWriter.pageMargin[2]));
				pdfMapWriter.indexMargin[3] = Utilities.millimetersToPoints(params.getFloat("indexmb", pdfMapWriter.pageMargin[3]));
			} catch (Exception e) {e.printStackTrace();}

			////////////////////////////////////////////////////////////////
			//シート結合 append sheet pdf
			//シートのファイルパスはセッションから取得する
			File preSheetFile = null;
			File sufSheetFile = null;
			if (params.getBoolean("pre_sheet")) {
				preSheetFile = (File)request.getSession().getAttribute("pdf_pre_sheet");
				if (preSheetFile != null && !preSheetFile.isFile()) preSheetFile =  null;
			}
			if (params.getBoolean("suf_sheet")) {
				sufSheetFile = (File)request.getSession().getAttribute("pdf_suf_sheet");
				if (sufSheetFile != null && !sufSheetFile.isFile()) sufSheetFile =  null;
			}

			////////////////////////////////////////////////////////////////
			//フィルタレイヤの設定
			String filterkey = FormUtils.getStringParameter(request, "filterkey");
			String filterLayerId = FormUtils.getStringParameter(request, "filterLayerId");
			if(filterkey!=null && 0<filterkey.length() && filterLayerId!=null && 0<filterLayerId.length()) {
				Vector<Long> featureIds = FilteredFeatureId.getFilteredFeatureId(request.getSession(), filterkey);
				pdfMapWriter.setFilter(filterLayerId, featureIds, FormUtils.getBooleanParameter(request, "grayout"));
			}

			////////////////////////////////////////////////////////////////
			// メール設定
			String email = params.get("email");
			if (email == null) this.emailMap.remove(sessionId);
			else this.emailMap.put(sessionId, email);

			////////////////////////////////////////////////////////////////
			// パラメータチェック Check parameter
			////////////////////////////////////////////////////////////////
			//<div lang="ja">分割表示時の全体サイズをチェック</div>
			//<div lang="en">When displaying partition, check the whole size</div>
			float maxHeight = PageSize.B0.getHeight(); 	//B0 1456
			if (userInfo.hasSuperUserCapability()) maxHeight *= 8; 	//B0 8x8 11648
			else if (userInfo.hasManagerCapability(cid)) maxHeight *= 3; 	//B0 3x3 4368
			if (printMap &&  Math.max(pdfMapWriter.pageRect.getHeight()*pdfMapWriter.pageRows, pdfMapWriter.pageRect.getWidth()*pdfMapWriter.pageCols) > maxHeight * maxHeight) {
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().print("{error:\'"+lang.__("Too much output data size")+"\'}");
				return;
			}

			////////////////////////////////
			//ベースレイヤの種類で印刷制限 Limit to base layer type
			/*LayerInfo baseLayerInfo = pdfMapWriter.getBaseLayerInfo();
			if (baseLayerInfo != null) {
				switch (baseLayerInfo.type) {
				case LayerInfo.TYPE_BASE_WEBTIS:
					//電子国土 (WEBTIS) A3制限
					if (printMap && PageSize.A3.getWidth()*PageSize.A3.getHeight() < pdfMapWriter.pageRect.getWidth()*pdfMapWriter.pageRect.getHeight()) {
						response.setContentType("application/json; charset=UTF-8");
						response.getWriter().print("{error:\'"+lang.__("Paper size is restricted to A3")+"\'}");
						return;
					}
					break;
				}
			}*/

			////////////////////////////////
			//地図なしのプレビュー Preview without map
			if (preview && !printMap) {
				response.setContentType("text/html");
				response.getWriter().print("{}");
				return;
			}

			//チェックなら終了 If check is true, finish
			if (check) {
				response.setContentType("text/html");
				response.getWriter().print("{success:true}");
				return;
			}

			////////////////////////////////////////////////////////////////
			//印刷開始
			////////////////////////////////////////////////////////////////

			logger.info(sessionId+":"+userInfo.userId+":Print Start\t"+FormUtils.getParametersString(request));

			//<div lang="ja">ProgressBarのカウント 地図表示時は地図出力タイル数計算後に設定 PDFの生成完了時に+1するので初期値は1</div>
			//<div lang="en">Count of ProgressBar   When displaying map, after calculating number of tile to display the map, default value is 1 because +1 when finish generating the setting PDF</div>
			int[] progressCounts = new int[]{0, 1};
			if (!preview) {
				if (printLegend) progressCounts[1] += 1;
				if (printList) progressCounts[1] += pdfMapWriter.getListLayerSize();
				synchronized (this.progressMap) {
					this.progressMap.put(sessionId, progressCounts);
				}
			}

			////////////////////////////////
			//<div lang="ja">一旦ファイルに出力</div>
			//<div lang="en">Output the file</div>
			File tmpFile = File.createTempFile(TMP_PREFIX, ".pdf");
			OutputStream out = new FileOutputStream(tmpFile);

			////////////////////////////////
			try {

				////////////////////////////////
				//<div lang="ja">文書オブジェクトを生成</div>
				//<div lang="en">Create Document object</div>
				Document doc = new Document(pdfMapWriter.pageRect);
				BufferedOutputStream bos = new BufferedOutputStream(out);
				PdfWriter pdfWriter = PdfWriter.getInstance(doc, bos);
				try {
					//<div lang="ja">Thread実行とキャンセル確認用 document → 処理が終わる前にdoc.close()されたらキャンセル</div>
					//<div lang="en">Document to confirm Thread executing and cancelling --> Before finishing, if doc.close() is executed, then cancel</div>
					if (preview) {
						synchronized (previewDocMap) {
							previewDocMap.put(sessionId, doc);
						}
					} else {
						synchronized (docMap) {
							docMap.put(sessionId, doc);
						}
					}

					//<div lang="ja">PDFを出力</div>
					//<div lang="en">Output PDF</div>
					try {
						if (preview) {
							try {
								JSONObject prevJSON = pdfMapWriter.getPreviewInfo(doc, pdfWriter, pdfMapWriter.pageRect, epsg);
								response.setContentType("application/json; charset=UTF-8");
								response.getWriter().print(prevJSON.toString());
								if (tmpFile.exists()) tmpFile.delete();
								return;
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							doc.open();
							pdfMapWriter.printMapPdf(doc, pdfWriter, pdfMapWriter.pageRect, progressCounts, epsg);
						}

					} catch (Exception e) {
						logger.warn(sessionId+":"+userInfo.userId+": "+e.getMessage());
						e.printStackTrace();
					}
				} finally {
					//<div lang="ja">PDF出力終了</div>
					//<div lang="en">Finish outputing PDF</div>
					synchronized (doc) {
						//<div lang="ja">キャンセルされている</div>
						//<div lang="en">Already cancelled</div>
						if (!doc.isOpen()) {
							logger.info(sessionId+":PDF Download Canceled");
							bos.close();
							return;
						}
						pdfWriter.flush();
					}
					try {
					doc.close();
					bos.close();
					} catch (Exception e) { e.printStackTrace(); }
				}

				//<div lang="ja">Tmpファイルから出力</div>
				//<div lang="en">Output from Tmp file</div>
				if (tmpFile.exists()) {
					//読めるようになるまで待つ
					if (!tmpFile.canRead()) {
						logger.warn("pdf Can not Read");
						Thread.sleep(500);
						if (!tmpFile.canRead()) {
							logger.warn("pdf Can not Read");
							Thread.sleep(500);
						}
					}

					//シートを結合
					try {
					if (preSheetFile != null || sufSheetFile != null) {
						File newTempFile = File.createTempFile(TMP_PREFIX, ".pdf");
						bos = new BufferedOutputStream(new FileOutputStream(newTempFile));
						try {
							PdfCopy copy = new PdfCopy(doc, bos);
							doc.open();
							if (preSheetFile != null) this.appendPdfFile(copy, preSheetFile);
							this.appendPdfFile(copy, tmpFile);
							if (sufSheetFile != null) this.appendPdfFile(copy, sufSheetFile);
							doc.close();
							//入れ替え
							tmpFile.delete();
							tmpFile = newTempFile;
						} finally {
							bos.close();
						}
					}
					} catch (Exception e) { e.printStackTrace(); logger.error(e.getMessage(), e); }

					progressCounts[0]++;

					this.fileNameMap.put(sessionId, new String[]{tmpFile.getPath(), mapInfo.title});

					//メール送信
					email = this.emailMap.get(sessionId);
					if (email != null) {
						try {
							this.sendEmail(lang, request, sessionId, cid, mapInfo, tmpFile, email);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							e.printStackTrace();
						}
					}

					//<div lang="ja">出力</div>
					//<div lang="en">Output</div>
					this.outputFile(request, response, tmpFile, mapInfo.title);

					////////////////////////////////
					//<div lang="ja">利用Log</div>
					//<div lang="en">Write log</div>
					logger.info(sessionId+":"+userInfo.userId+":Printed\t"+FormUtils.getParametersString(request));

				}
			} finally {
				if (preview) {
					synchronized (previewDocMap) {
						previewDocMap.remove(sessionId);
					}
				} else {
					synchronized (docMap) {
						docMap.remove(sessionId);
					}
				}
				//Delete Old tempFile
				this.removeTempFiles();
				System.gc();
			}
		} catch (Exception e) {
			//ServletUtil.printError(response, 500, "PDF Error");
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			return;
		}
	}

	//ダウンロード用のURLを生成してメール送信
	private void sendEmail(SaigaiTaskDBLang lang, HttpServletRequest request, String sessionId, int cid, MapInfo mapInfo, File tmpFile, String email) throws Exception
	{
		//ダウンロード用のキーを生成してTempファイル情報をキーに設定
		String emailKey = null;
		while (emailKey == null) {
			emailKey = UserAuthorization.createLayerAuthorizedKey(sessionId+System.currentTimeMillis());
			if (this.fileNameMap.containsKey(emailKey)) emailKey = null;
			else this.fileNameMap.put(emailKey, new String[]{tmpFile.getPath(), mapInfo.title});
		}

		String url = ServletUtil.getServerRootURL(request)+request.getContextPath()+"/map/pdfload?download=1&sid="+emailKey;

		//送信元
		String from = mapDB.getOption("MAIL_FROM", cid);
		if (from == null) from = mapDB.getOption("MAIL_FROM");
		if (from == null) from = "info@ecom-plat.jp";
		//件名
		String subject = lang.__("e-Com map PDF download")+" : "+mapInfo.title;
		//ダウンロード期限
		String expDateTime =  FormUtils.getDateTimeFormat().format(new Date(tmpFile.lastModified()+TMP_EXP));
		//本文
		String message = lang.__("We inform you that PDF was ready.")+"\n"+lang.__("You can download PDF file from following link.")+"\n\n"
			+lang.__("Map title<!--3-->")+" : \n  "+mapInfo.title+"\n"
			+lang.__("PDF download URL")+" : \n  "+URLDecoder.decode(url, "UTF-8")+"\n"
			+lang.__("Download expiry")+" : \n  "+expDateTime;

		//SMTPサーバーの指定
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "localhost");
		MimeMessage msg = new MimeMessage(Session.getDefaultInstance(props, null));

		//宛先
		msg.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email, false));

		//送信元
		msg.setFrom(new InternetAddress(from));
		msg.setSentDate(new Date());

		msg.setSubject(subject,"ISO-2022-JP");
		msg.setText(message,"ISO-2022-JP");
		Transport.send(msg);

		logger.info("PDF email : "+email);
		logger.info("PDF url : "+url);
	}

	/** <div lang="ja">
	 * 48時間以上前のtmpファイルを削除 。
	 * </div>
	 *
	 * <div lang="en">
	 * If the tmp file is older than 48 hour or more, delete it.
	 * </div>
	 * */
	private void removeTempFiles() throws IOException
	{
		File tmpFile = File.createTempFile(TMP_PREFIX, "");
		File tmpPath = tmpFile.getParentFile();
		tmpFile.delete();

		File[] files = tmpPath.listFiles();
		if (files != null) {
			long now = System.currentTimeMillis();
			for (File file : files) {
				if (file.getName().startsWith(TMP_PREFIX)) {
					if (now - file.lastModified() > TMP_EXP) {
						file.delete();
					}
				}
			}
		}
	}

	/** <div lang="ja">
	 * tmpファイルからPDFを出力 。
	 * </div>
	 *
	 * <div lang="en">
	 * Output PDF from tmp file.
	 * </div>
	 * */
	private void outputFile(HttpServletRequest request, HttpServletResponse response, File file, String title) throws IOException
	{
		//<div lang="ja">出力設定</div>
		//<div lang="en">Set output</div>
		response.setContentType("application/pdf");
		//response.setContentType("application/octet-stream");
		String fileName = title.replaceAll(" |#|\\?|\\/|\\\"|\\\\'", "");
		//WindowsのFirefox
		if (request.getHeader("User-Agent").indexOf("Firefox") > -1 && request.getHeader("User-Agent").indexOf("Windows") > -1) {
			fileName = new String((fileName+".pdf").getBytes("Shift-JIS"), "iso-8859-1");
		} else {
			//fileName =  MimeUtility.encodeWord(fileName+".pdf", "ISO-2022-JP", "B");
			fileName = URLEncoder.encode(fileName+".pdf", "UTF-8");
		}
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		response.setContentLength((int)file.length());
		int length;
		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

		byte[] b = new byte[1024];
		while ((length = bis.read(b)) != -1) {
			bos.write(b, 0, length);
		}
		bis.close();
		bos.flush();
		bos.close();
	}

	/** pdfファイルを結合
	 * コピー用ストリームでdocをopenしてからファイルの追加のみ行う。
	 * pdfWriterでの出力は別ファイルに出力後に結合
	 * @throws IOException
	 * @throws BadPdfFormatException */
	private void appendPdfFile(PdfCopy copy, File appendFile) throws IOException, BadPdfFormatException
	{
		RandomAccessFileOrArray raf = new RandomAccessFileOrArray(appendFile.getAbsolutePath());
		try {
			PdfReader reader = new PdfReader(raf, null);
			reader.consolidateNamedDestinations();
			int numberOfPages = reader.getNumberOfPages();
			for (int i=1; i<=numberOfPages; i++) {
				PdfImportedPage page = copy.getImportedPage(reader, i);
				copy.addPage(page);
			}
			reader.close();
		} finally {
			raf.close();
		}
	}

	private String authorizationDecode(HttpSession httpSession, String metadataId){
		String decodeStr = "";
		if(!metadataId.equals("")){
			if(httpSession.getAttribute("loginDataDto") != null){
				LoginDataDto loginDataDto = (LoginDataDto)httpSession.getAttribute("loginDataDto");
				String authData = (String)httpSession.getAttribute("Externalmapdatainfoid_" + loginDataDto.getLocalgovinfoid() + "_" + metadataId);
				// 認証情報を分解
				if(authData != null){
					authData = authData.replace("Basic ", "");
					// Decode
					byte[] decodeArr = Base64Util.decode(authData);
					decodeStr = new String(decodeArr);
				}
			}
		}
		return decodeStr;
	}

	private String setAuthorityURL(String baseurl, String auth){
		if(baseurl==null) return null;
		String authUrl = "";
		if(!baseurl.equals("")){
			try {
				URL url = new URL(baseurl);
				StringBuffer sb = new StringBuffer();
				sb.append(url.getProtocol());
				sb.append("://");
				sb.append(auth + "@");
				sb.append(url.getAuthority());
				sb.append(url.getFile());
				sb.append(url.getRef());
				authUrl = sb.toString();
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
				//e.printStackTrace();
			}
		}
		return authUrl;
	}
}
