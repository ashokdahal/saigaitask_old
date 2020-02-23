/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.validation.Valid;

import org.apache.commons.validator.UrlValidator;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.DisconnectImportConfirmDto;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.DisconnectImportConfirmDto.ImportTrackData;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.OauthconsumerData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.admin.disconnect.DatasyncForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.MapbaselayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.OauthconsumerDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.disconnect.TrackDataExportService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/disconnect/datasync")
public class DatasyncAction extends AbstractDisconnectAction{
	public DatasyncForm datasyncForm;

	@Resource
	private ResponseService responseService;
	@Resource
	private MapmasterInfoService mapmasterInfoService;
	@Resource
	private TrackmapInfoService trackmapInfoService;
	@Resource
	private MenuInfoService menuInfoService;
	@Resource
	private MapbaselayerInfoService mapbaselayerInfoService;
	@Resource
	private TableService tableService;
	@Resource
	private TrackDataExportService trackDataExportService;
	@Resource
	private OauthconsumerDataService oauthconsumerDataService;

	public String cloudurl;
	public String cloudurlChecked;

	public String oAuthConsumerKey;
	private String oAuthConsumerSecretKey;
	public String oAuthCommunityId;

	public String step2Url;
	public String targetLocalgovinfoid;
	public DisconnectImportConfirmDto disconnectImportConfirmDto;
	public String hasError;
	public String uploadTrackDataDirName;
	public String uploadTrackDataFileName;
	public String oAuthCode;
	public String trackMapText;
	public String updateTrackTableDatasHidden;

	public String endMessage;

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("cloudurl", cloudurl);
		model.put("cloudurlChecked", cloudurlChecked);
		model.put("oAuthConsumerKey", oAuthConsumerKey);
		model.put("oAuthCommunityId", oAuthCommunityId);
		model.put("step2Url", step2Url);
		model.put("targetLocalgovinfoid", targetLocalgovinfoid);
		model.put("disconnectImportConfirmDto", disconnectImportConfirmDto);
		model.put("hasError", hasError);
		model.put("uploadTrackDataDirName", uploadTrackDataDirName);
		model.put("uploadTrackDataFileName", uploadTrackDataFileName);
		model.put("oAuthCode", oAuthCode);
		model.put("trackMapText", trackMapText);
		model.put("updateTrackTableDatasHidden", updateTrackTableDatasHidden);
	}

	/**
	 * 災害データ同期画面を表示する
	 */
	@RequestMapping(value="/content")
	public String content(Map<String,Object> model, @Valid @ModelAttribute DatasyncForm datasyncForm) {
		this.datasyncForm = datasyncForm;
		initDisconnect();
		hasError = null;
		oAuthConsumerKey = null;
		oAuthConsumerSecretKey = null;
		oAuthCommunityId = null;
		oAuthCode = null;

		// 自治体セレクトボックスの作成
		if (loginDataDto.getLocalgovinfoid() == 0) {
			createLocalgovSelectOptions();
		}

		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		datasyncForm.cloudurl = rb.getString("DISCONNTCT_DATASYNC_URL");

		setupModel(model);

		return "/admin/disconnect/datasync/content";
	}

	/**
	 * @throws Exception
	 *
	 */
	@RequestMapping(value="/checkUrl")
	public String checkUrl(Map<String,Object> model, @Valid @ModelAttribute DatasyncForm datasyncForm, BindingResult bindingResult) {
		this.datasyncForm = datasyncForm;

		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}

		String code = request.getParameter("code");
		ActionMessages errors = new ActionMessages();
		if(code == null || code.isEmpty()){
			// 自治体IDの保存
			if(! StringUtil.isEmpty(datasyncForm.selectLocalgov)){
				datasyncForm.targetLocalgovinfoid = datasyncForm.selectLocalgov;
			}else{
				datasyncForm.targetLocalgovinfoid =  String.valueOf(loginDataDto.getLocalgovinfoid());
			}

			try{
				cloudurl = datasyncForm.cloudurl;
				cloudurlChecked = datasyncForm.cloudurl;

				UrlValidator uv = new UrlValidator();
				if((! StringUtil.isEmpty(cloudurl)) &&  (! uv.isValid(cloudurl))){
					if(! (cloudurl.startsWith("http://localhost") || cloudurl.startsWith("https://localhost"))){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Invalid URL"), false));
					}
				}

			} catch(Exception e) {
				e.printStackTrace();
			}

			// URLが正しい場合、Oauth認証を実行しておく
			if(errors.isEmpty()){
				// OAuth の初期化
				try{
					oAuthInit(cloudurl, Long.parseLong(datasyncForm.targetLocalgovinfoid));
				}catch(ServiceException e){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
				}
				if(errors.isEmpty()){
					session.setAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY, cloudurl);
					StringBuffer url = request.getRequestURL();
					String searchStr = "/admin/disconnect/";
					int tempIndex = url.indexOf(searchStr);
					step2Url = url.substring(0, tempIndex + searchStr.length()) + "datasync/checkUrl";
					session.setAttribute(Constants.OAUTH_LOCALGOVINFOID_SESSION_KEY, datasyncForm.targetLocalgovinfoid);
					setupModel(model);
					return "/admin/disconnect/datasync/step2";
				}else{
					ActionMessagesUtil.addErrors(bindingResult, errors);
					setupModel(model);
					return "/admin/disconnect/datasync/step1";
				}
			}else{
				ActionMessagesUtil.addErrors(bindingResult, errors);
				setupModel(model);
				return "/admin/disconnect/datasync/step1";
			}
		}else{
			session.setAttribute(Constants.OAUTH_CODE_SESSION_KEY, code);
			oAuthCode = code;
			targetLocalgovinfoid = (String)session.getAttribute(Constants.OAUTH_LOCALGOVINFOID_SESSION_KEY);
			cloudurl = (String)session.getAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY);
			cloudurlChecked = (String)session.getAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY);

			// OAuth の初期化
			try{
				oAuthInit(cloudurl,Long.parseLong(targetLocalgovinfoid));
			}catch(ServiceException e){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			}
			if(errors.isEmpty()){
				StringBuffer url = request.getRequestURL();
				String searchStr = "/admin/disconnect/";
				int tempIndex = url.indexOf(searchStr);
				step2Url = url.substring(0, tempIndex + searchStr.length()) + "datasync/checkUrl";
				setupModel(model);
				return "/admin/disconnect/datasync/step2";
			}else{
				ActionMessagesUtil.addErrors(bindingResult, errors);
				setupModel(model);
				return "/admin/disconnect/datasync/step1";

			}
		}
	}

	/**
	 * @throws Exception
	 *
	 */
	@RequestMapping(value="/trackmapping")
	public String trackmapping(Map<String,Object> model, @ModelAttribute DatasyncForm datasyncForm, BindingResult bindingResult) {
		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}

		String cloudUrlBase = (String)session.getAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY);
		CookieStore cookieStore = new BasicCookieStore();
	    HttpClientContext context = HttpClientContext.create();
	    context.setCookieStore(cookieStore);

		ActionMessages errors = new ActionMessages();
		// APIへリクエストを投げる
	    try{
		    RequestConfig requestConfig = createRequestConfig();
		    SSLContext sslContext = createSSLContext();
			String sessionKey = (String)session.getAttribute(Constants.OAUTH_TOKEN_SESSION_KEY);
			List<Header> headers = new ArrayList<Header>();
//				headers.add(new BasicHeader("X-CSRF-Token",session.getId()));
			headers.add(new BasicHeader("Authorization","Bearer " + sessionKey));

			try (CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext)
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
					.setDefaultRequestConfig(requestConfig)
					.setDefaultHeaders(headers)
					.setDefaultCookieStore(cookieStore).build()) {
				// JSESSIONID取得のため、まずGETを行う。
				HttpGet getMethod = new HttpGet(cloudUrlBase + Constants.API_GETURL);
				try (CloseableHttpResponse getResponse = httpClient.execute(getMethod)) {
					if (getResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						List<Cookie> cookies = context.getCookieStore().getCookies();
						String jsessionid = "";
						for(Cookie cookie : cookies){
							if(cookie.getName().equals("JSESSIONID")){
								jsessionid = cookie.getValue();
							}
						}

						// 災害データファイルを作成する
						String localgovinfoidStr = (String)session.getAttribute(Constants.OAUTH_LOCALGOVINFOID_SESSION_KEY);
						long localgovinfoid = Long.parseLong(localgovinfoidStr);
						boolean syncAttachFile = "1".equals(datasyncForm.syncAttachedFile);
						File zipFile = trackDataExportService.zip(localgovinfoid, false, syncAttachFile);
						if(zipFile.exists()){
							String md5HashValue = FileUtil.getMd5hash(zipFile);
							if(StringUtil.isEmpty(md5HashValue)){
								hasError = "error";
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("", false));
								ActionMessagesUtil.addErrors(bindingResult, errors);
							}else{
								// POSTする
								HttpPost postMethod = new HttpPost(cloudUrlBase + "api/v2/trackdatasync/confirmtrackmapping");
								MultipartEntityBuilder builder = MultipartEntityBuilder.create();
								builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
								builder.addTextBody(FormUtils.TOKEN_PARAM_NAME, jsessionid);
							    builder.addTextBody("Authorization", "Bearer " + sessionKey);
							    builder.addTextBody("trackDatafileHash", md5HashValue);
								builder.addBinaryBody("trackDatafile", zipFile, ContentType.create("application/zip"), zipFile.getName());
								HttpEntity sendEntity = builder.build();
								postMethod.setEntity(sendEntity);
								try (CloseableHttpResponse postResponse = httpClient.execute(postMethod)) {
									if (postResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
										HttpEntity reciveEntity = postResponse.getEntity();

										// 受信した文字列からJSONオブジェクトを生成し、DisconnectImportConfirmDtoオブジェクトに復元する
										String jsonStr = EntityUtils.toString(reciveEntity,StandardCharsets.UTF_8);
										try{
											disconnectImportConfirmDto = new DisconnectImportConfirmDto();
											JSONObject resultData = new JSONObject(jsonStr);

											// エラーチェック
											String status = resultData.getString(Constants.JSON_KEY_STATUS);
											if(! status.equals(Constants.JSON_KEY_STATUSSUCCESS)){
												hasError = "error";
												errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(resultData.getString(Constants.JSON_KEY_ERRORMESSAGE), false));
												ActionMessagesUtil.addErrors(bindingResult, errors);
											}else{
												// クラウドに保存した災害データファイルのファイル名とディレクトリ名を取得
												uploadTrackDataDirName = resultData.getString("uploadTrackDataDirName");
												uploadTrackDataFileName = resultData.getString("uploadTrackDataFileName");

												disconnectImportConfirmDto.targetTrackDataList = new ArrayList<TrackData>();
												JSONArray targetTrackDataListJson = resultData.getJSONArray("targetTrackDataListJson");
												for(int i = 0; i < targetTrackDataListJson.length() ; i++){
													JSONObject targetTrackDataJson = targetTrackDataListJson.getJSONObject(i);
													TrackData targetTrackData = new TrackData();
													targetTrackData.id = targetTrackDataJson.getLong("id");
													targetTrackData.name = targetTrackDataJson.getString("name");
													disconnectImportConfirmDto.targetTrackDataList.add(targetTrackData);
												}

												disconnectImportConfirmDto.fileTrackDataList = new ArrayList<TrackData>();
												JSONArray fileTrackDataListJson = resultData.getJSONArray("fileTrackDataListJson");
												for(int i = 0; i < fileTrackDataListJson.length() ; i++){
													JSONObject fileTrackDataJson = fileTrackDataListJson.getJSONObject(i);
													TrackData fileTrackData = new TrackData();
													fileTrackData.id = fileTrackDataJson.getLong("id");
													fileTrackData.name = fileTrackDataJson.getString("name");
													disconnectImportConfirmDto.fileTrackDataList.add(fileTrackData);
												}

												JSONArray importedOldTrackIdListJson = resultData.getJSONArray("importedOldTrackIdListJson");
												List<String> importedOldTrackIdList = new ArrayList<String>();
												if(importedOldTrackIdListJson != null){
													for(int i = 0; i < importedOldTrackIdListJson.length(); i++){
														JSONObject jsonObj = importedOldTrackIdListJson.getJSONObject(i);
														importedOldTrackIdList.add(jsonObj.getString("value"));
													}
													datasyncForm.selectFileTrackMultibox = (String[])importedOldTrackIdList.toArray(new String[0]);
												}

												JSONArray importedNewTrackIdListJson = resultData.getJSONArray("importedNewTrackIdListJson");
												List<String> importedNewTrackIdList = new ArrayList<String>();
												if(importedNewTrackIdListJson != null){
													for(int i = 0; i < importedNewTrackIdListJson.length(); i++){
														JSONObject jsonObj = importedNewTrackIdListJson.getJSONObject(i);
														importedNewTrackIdList.add(jsonObj.getString("value"));
													}
													datasyncForm.selectDbTrackMultibox = (String[])importedNewTrackIdList.toArray(new String[0]);
												}
											}
											// ローカルの災害データファイルは削除する。
											zipFile.delete();
										}catch(JSONException e){
											e.printStackTrace();
											hasError = "error";
											errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
											ActionMessagesUtil.addErrors(bindingResult, errors);
										}
									}
								}
							}
						}
					}
				}
			}

	    }catch(KeyStoreException e){
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
	    }catch(KeyManagementException e){
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
	    }catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		} catch (IOException e) {
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);

		return "/admin/disconnect/datasync/step3";

	}

	/**
	 * @throws Exception
	 *
	 */
	@RequestMapping(value="/trackdatacheck")
	public String trackdatacheck(Map<String,Object> model, @ModelAttribute DatasyncForm datasyncForm, BindingResult bindingResult) {
		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}

		ActionMessages errors = new ActionMessages();
		// 災害マッピング取得
		Map<Long, Long> trackMap = new HashMap<Long, Long>();
		String [] selectTrackFileMultibox = datasyncForm.selectFileTrackMultibox;
		String [] selectTrackDbMultibox = datasyncForm.selectDbTrackMultibox;
		updateTrackTableDatasHidden = datasyncForm.updateTrackTableDatasHidden;

		// バリデーションチェック
		// ファイルの災害と既存の災害のマッピングは1対1
		// ファイルの災害をチェックした場合は既存の災害を選択しておく必要がある。
		boolean nullCheck = true;
		if(selectTrackFileMultibox == null && selectTrackDbMultibox == null){
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose an disaster of an import target and an disaster of a merging target."), false));
			nullCheck = false;
		}else if(selectTrackFileMultibox == null && selectTrackDbMultibox != null){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose an disaster of a import target."), false));
			nullCheck = false;
		}else if(selectTrackFileMultibox != null && selectTrackDbMultibox == null){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose 1 disaster of a merging target."), false));
			nullCheck = false;
		}
		if(nullCheck){
			for(int i = 0; i < selectTrackFileMultibox.length; i++){
				trackMap.put(Long.parseLong(selectTrackFileMultibox[i]), null);
			}
			for(int i = 0; i < selectTrackDbMultibox.length; i++){
				String[] rowData = selectTrackDbMultibox[i].split(":");
				String fileTrackId = rowData[0];
				String dbTrackId = rowData[1];

				if(! trackMap.containsKey(Long.parseLong(fileTrackId))){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose an disaster of a import target."), false));
				}else{
					Long nowDbTrackId = trackMap.get(Long.parseLong(fileTrackId));
					if(nowDbTrackId != null){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose only 1 disaster of a merging target."), false));
					}else{
						trackMap.put(Long.parseLong(fileTrackId), Long.parseLong(dbTrackId));
					}
				}
			}
			for(int i = 0; i < selectTrackFileMultibox.length; i++){
				Long nowDbTrackId = trackMap.get(Long.parseLong(selectTrackFileMultibox[i]));
				if(nowDbTrackId == null){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose 1 disaster of a merging target."), false));
				}
			}
		}

		if(errors.size() > 0){
			ActionMessagesUtil.addErrors(bindingResult, errors);

			// 再描画用データ取得
			uploadTrackDataDirName = datasyncForm.uploadTrackDataDirName;
			uploadTrackDataFileName = datasyncForm.uploadTrackDataFileName;
			String cloudUrlBase = (String)session.getAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY);

			CookieStore cookieStore = new BasicCookieStore();
		    HttpClientContext context = HttpClientContext.create();
		    context.setCookieStore(cookieStore);

			// APIへリクエストを投げる
			try {
			    RequestConfig requestConfig = createRequestConfig();
			    SSLContext sslContext = createSSLContext();
				String sessionKey = (String)session.getAttribute(Constants.OAUTH_TOKEN_SESSION_KEY);
				List<Header> headers = new ArrayList<Header>();
//				headers.add(new BasicHeader("X-CSRF-Token",session.getId()));
				headers.add(new BasicHeader("Authorization","Bearer " + sessionKey));
				try (CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext)
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
						.setDefaultRequestConfig(requestConfig)
						.setDefaultHeaders(headers)
						.setDefaultCookieStore(cookieStore).build()) {
					// JSESSIONID取得のため、まずGETを行う。
					HttpGet getMethod = new HttpGet(cloudUrlBase + Constants.API_GETURL);
					try (CloseableHttpResponse getResponse = httpClient.execute(getMethod)) {
						if (getResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							List<Cookie> cookies = context.getCookieStore().getCookies();
							String jsessionid = "";
							for(Cookie cookie : cookies){
								if(cookie.getName().equals("JSESSIONID")){
									jsessionid = cookie.getValue();
								}
							}

							// POSTする
							String localgovinfoidStr = (String)session.getAttribute(Constants.OAUTH_LOCALGOVINFOID_SESSION_KEY);
							HttpPost postMethod = new HttpPost(cloudUrlBase + "api/v2/trackdatasync/confirmtrackmapping");
							MultipartEntityBuilder builder = MultipartEntityBuilder.create();
							builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
							builder.addTextBody(FormUtils.TOKEN_PARAM_NAME, jsessionid);
						    builder.addTextBody("Authorization", "Bearer " + sessionKey);
						    builder.addTextBody("uploadTrackDataDirName", uploadTrackDataDirName);
						    builder.addTextBody("uploadTrackDataFileName", uploadTrackDataFileName);
							HttpEntity sendEntity = builder.build();
							postMethod.setEntity(sendEntity);
							try (CloseableHttpResponse postResponse = httpClient.execute(postMethod)) {
								if (postResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
									HttpEntity reciveEntity = postResponse.getEntity();

									// 受信した文字列からJSONオブジェクトを生成し、DisconnectImportConfirmDtoオブジェクトに復元する
									String jsonStr = EntityUtils.toString(reciveEntity,StandardCharsets.UTF_8);
									try{
										disconnectImportConfirmDto = new DisconnectImportConfirmDto();
										JSONObject resultData = new JSONObject(jsonStr);

										// エラーチェック
										String status = resultData.getString(Constants.JSON_KEY_STATUS);
										if(! status.equals(Constants.JSON_KEY_STATUSSUCCESS)){
											hasError = "error";
											errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(resultData.getString(Constants.JSON_KEY_ERRORMESSAGE), false));
											ActionMessagesUtil.addErrors(bindingResult, errors);
										}else{
											disconnectImportConfirmDto.targetTrackDataList = new ArrayList<TrackData>();
											JSONArray targetTrackDataListJson = resultData.getJSONArray("targetTrackDataListJson");
											for(int i = 0; i < targetTrackDataListJson.length() ; i++){
												JSONObject targetTrackDataJson = targetTrackDataListJson.getJSONObject(i);
												TrackData targetTrackData = new TrackData();
												targetTrackData.id = targetTrackDataJson.getLong("id");
												targetTrackData.name = targetTrackDataJson.getString("name");
												disconnectImportConfirmDto.targetTrackDataList.add(targetTrackData);
											}

											disconnectImportConfirmDto.fileTrackDataList = new ArrayList<TrackData>();
											JSONArray fileTrackDataListJson = resultData.getJSONArray("fileTrackDataListJson");
											for(int i = 0; i < fileTrackDataListJson.length() ; i++){
												JSONObject fileTrackDataJson = fileTrackDataListJson.getJSONObject(i);
												TrackData fileTrackData = new TrackData();
												fileTrackData.id = fileTrackDataJson.getLong("id");
												fileTrackData.name = fileTrackDataJson.getString("name");
												disconnectImportConfirmDto.fileTrackDataList.add(fileTrackData);
											}

											JSONArray importedOldTrackIdListJson = resultData.getJSONArray("importedOldTrackIdListJson");
											List<String> importedOldTrackIdList = new ArrayList<String>();
											if(importedOldTrackIdListJson != null){
												for(int i = 0; i < importedOldTrackIdListJson.length(); i++){
													JSONObject jsonObj = importedOldTrackIdListJson.getJSONObject(i);
													importedOldTrackIdList.add(jsonObj.getString("value"));
												}
												datasyncForm.selectFileTrackMultibox = (String[])importedOldTrackIdList.toArray(new String[0]);
											}

											JSONArray importedNewTrackIdListJson = resultData.getJSONArray("importedNewTrackIdListJson");
											List<String> importedNewTrackIdList = new ArrayList<String>();
											if(importedNewTrackIdListJson != null){
												for(int i = 0; i < importedNewTrackIdListJson.length(); i++){
													JSONObject jsonObj = importedNewTrackIdListJson.getJSONObject(i);
													importedNewTrackIdList.add(jsonObj.getString("value"));
												}
												datasyncForm.selectDbTrackMultibox = (String[])importedNewTrackIdList.toArray(new String[0]);
											}
										}
									}catch(JSONException e){
										hasError = "error";
										errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
										ActionMessagesUtil.addErrors(bindingResult, errors);
									}
								}
							}
						}
					}
				}
		    }catch(KeyStoreException e){
				e.printStackTrace();
				hasError = "error";
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
		    }catch(KeyManagementException e){
				e.printStackTrace();
				hasError = "error";
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
		    }catch(NoSuchAlgorithmException e){
				e.printStackTrace();
				hasError = "error";
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
			} catch (IOException e) {
				hasError = "error";
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}

			setupModel(model);

			return "/admin/disconnect/datasync/step3";
		}else{
			StringBuffer sbuf = new StringBuffer();
			for(Map.Entry<Long, Long> e : trackMap.entrySet()){
				sbuf.append(e.getKey());
				sbuf.append("-");
				sbuf.append(e.getValue());
				sbuf.append(",");
			}
			int index = sbuf.lastIndexOf(",");
			if (sbuf.length() > 0)
				sbuf = sbuf.deleteCharAt(index);
			trackMapText = sbuf.toString();
		}

//		String cloudUrlBase = (String)session.getAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY);
//		CookieStore cookieStore = new BasicCookieStore();
//	    HttpClientContext context = HttpClientContext.create();
//	    context.setCookieStore(cookieStore);
//
//		// APIへリクエストを投げる
//		try {
//		    RequestConfig requestConfig = createRequestConfig();
//		    SSLContext sslContext = createSSLContext();
//			String sessionKey = (String)session.getAttribute(Constants.OAUTH_TOKEN_SESSION_KEY);
//			List<Header> headers = new ArrayList<Header>();
////			headers.add(new BasicHeader("X-CSRF-Token",session.getId()));
//			headers.add(new BasicHeader("Authorization","Bearer " + sessionKey));
//
//
//			try (CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext)
//					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//					.setDefaultRequestConfig(requestConfig)
//					.setDefaultHeaders(headers)
//					.setDefaultCookieStore(cookieStore).build()) {
//				// JSESSIONID取得のため、まずGETを行う。
//				HttpGet getMethod = new HttpGet(cloudUrlBase + Constants.API_GETURL);
//				try (CloseableHttpResponse getResponse = httpClient.execute(getMethod)) {
//					if (getResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//						List<Cookie> cookies = context.getCookieStore().getCookies();
//						String jsessionid = "";
//						for(Cookie cookie : cookies){
//							if(cookie.getName().equals("JSESSIONID")){
//								jsessionid = cookie.getValue();
//							}
//						}
//
//						String localgovinfoidStr = (String)session.getAttribute(Constants.OAUTH_LOCALGOVINFOID_SESSION_KEY);
//						// POSTする
//						HttpPost postMethod = new HttpPost(cloudUrlBase + "api/v1/trackdatasync/confirmdatasync");
//						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//						builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//						builder.addTextBody(FormUtils.TOKEN_PARAM_NAME, jsessionid);
//					    builder.addTextBody("Authorization", "Bearer " + sessionKey);
//					    builder.addTextBody("uploadTrackDataDirName", datasyncForm.uploadTrackDataDirName);
//					    builder.addTextBody("uploadTrackDataFileName", datasyncForm.uploadTrackDataFileName);
//					    builder.addTextBody("trackMapText", trackMapText);
//
//
//					    HttpEntity sendEntity = builder.build();
//						postMethod.setEntity(sendEntity);
//						try (CloseableHttpResponse postResponse = httpClient.execute(postMethod)) {
//							if (postResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//								HttpEntity reciveEntity = postResponse.getEntity();
//
//								// 受信した文字列からJSONオブジェクトを生成し、DisconnectImportConfirmDtoオブジェクトに復元する
//								String jsonStr = EntityUtils.toString(reciveEntity,StandardCharsets.UTF_8);
//								try{
//
//									disconnectImportConfirmDto = new DisconnectImportConfirmDto();
//									JSONObject resultData = new JSONObject(jsonStr);
//									// エラーチェック
//									String status = resultData.getString(Constants.JSON_KEY_STATUS);
//									if(! status.equals(Constants.JSON_KEY_STATUSSUCCESS)){
//										hasError = "error";
//										errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(resultData.getString(Constants.JSON_KEY_ERRORMESSAGE), false));
//										ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
//									}else{
//										JSONArray trackJsonObjectList = resultData.getJSONArray("trackJsonObjectList");
//
//										// クラウドに保存した災害データファイルのファイル名とディレクトリ名を取得
//										uploadTrackDataDirName = resultData.getString("uploadTrackDataDirName");
//										uploadTrackDataFileName = resultData.getString("uploadTrackDataFileName");
//
//										disconnectImportConfirmDto.importTrackDataList = new ArrayList<ImportTrackData>();
//										for(int i = 0; i < trackJsonObjectList.length(); i++){
//											DisconnectImportConfirmDto.ImportTrackData importTrackData = new DisconnectImportConfirmDto.ImportTrackData();
//											JSONObject trackJsonObject = trackJsonObjectList.getJSONObject(i);
//
//											JSONArray baseJsonObjectList =  trackJsonObject.getJSONArray("baseJsonObjectList");
//											JSONObject baseJsonObject = baseJsonObjectList.getJSONObject(0);
//											importTrackData.isUpdate = baseJsonObject.getBoolean("isUpdate");
//											importTrackData.dbMapId = baseJsonObject.getLong("dbMapId");
//											importTrackData.dbTrackDataId = baseJsonObject.getLong("dbTrackDataId");
//											importTrackData.dbTrackDataName = baseJsonObject.getString("dbTrackDataName");
//											importTrackData.fileMapId = baseJsonObject.getLong("fileMapId");
//											importTrackData.fileTrackDataId = baseJsonObject.getLong("fileTrackDataId");
//											importTrackData.fileTrackDataName = baseJsonObject.getString("fileTrackDataName");
//											importTrackData.trackDataName = baseJsonObject.getString("trackDataName");
//											importTrackData.isLayerInfoVisiblel = baseJsonObject.getBoolean("isLayerInfoVisiblel");
//											importTrackData.isTableInfoVisiblel = baseJsonObject.getBoolean("isTableInfoVisiblel");
//											importTrackData.importLayerList = new ArrayList<DisconnectImportConfirmDto.ImportLayerInfo>();
//
//											JSONArray layerJsonObjectList =  trackJsonObject.getJSONArray("layerJsonObjectList");
//											for(int j = 0; j < layerJsonObjectList.length(); j++){
//												DisconnectImportConfirmDto.ImportLayerInfo importLayerInfo = new DisconnectImportConfirmDto.ImportLayerInfo();
//												JSONObject layerJsonObject = layerJsonObjectList.getJSONObject(j);
//
//												importLayerInfo.layerId = layerJsonObject.getString("layerId");
//												importLayerInfo.layerName = layerJsonObject.getString("layerName");
//												importLayerInfo.unmatchCount = layerJsonObject.getInt("unmatchCount");
//
//												importLayerInfo.attrNamesList = new ArrayList<String>();
//												JSONArray attrNamesListJsonObjectList = layerJsonObject.getJSONArray("attrNamesListJsonObjectList");
//												for(int k = 0; k < attrNamesListJsonObjectList.length(); k++){
//													JSONObject attrNamesListJsonObject = attrNamesListJsonObjectList.getJSONObject(k);
//													importLayerInfo.attrNamesList.add(attrNamesListJsonObject.getString("value"));
//												}
//
//												importLayerInfo.unmatchFeatureInfoList = new ArrayList<DisconnectImportConfirmDto.UnmatchFeatureInfo>();
//												JSONArray featureJsonObjectList = layerJsonObject.getJSONArray("featureJsonObjectList");
//												for(int l = 0; l < featureJsonObjectList.length(); l++){
//													JSONObject featureJsonObject = featureJsonObjectList.getJSONObject(l);
//													DisconnectImportConfirmDto.UnmatchFeatureInfo unmatchFeatureInfo = new DisconnectImportConfirmDto.UnmatchFeatureInfo();
//													unmatchFeatureInfo.gid = featureJsonObject.getLong("gid");
//													unmatchFeatureInfo.dbGroupId = featureJsonObject.getString("dbGroupId");
//													unmatchFeatureInfo.dbGroupName = featureJsonObject.getString("dbGroupName");
//													unmatchFeatureInfo.fileGroupId = featureJsonObject.getString("fileGroupId");
//													unmatchFeatureInfo.fileGroupName = featureJsonObject.getString("fileGroupName");
//													unmatchFeatureInfo.checkBoxValue = featureJsonObject.getString("checkBoxValue");
//
//													unmatchFeatureInfo.dbAttrs = new ArrayList<String>();
//													JSONArray dbAttrsJSONArray = featureJsonObject.getJSONArray("dbAttrsJsonObjectList");
//													for(int m = 0 ; m < dbAttrsJSONArray.length(); m++){
//														JSONObject dbAttrsJSONObject = dbAttrsJSONArray.getJSONObject(m);
//														unmatchFeatureInfo.dbAttrs.add(dbAttrsJSONObject.getString("value"));
//													}
//
//													unmatchFeatureInfo.fileAttrs = new ArrayList<DisconnectImportConfirmDto.FileAttrInfo>();
//													JSONArray fileAttrsJSONArray = featureJsonObject.getJSONArray("fileAttrsJsonObjectList");
//													for(int n = 0 ; n < fileAttrsJSONArray.length(); n++){
//														JSONObject fileAttrsJSONObject = fileAttrsJSONArray.getJSONObject(n);
//														DisconnectImportConfirmDto.FileAttrInfo fileAttrInfo = new DisconnectImportConfirmDto.FileAttrInfo();
//														fileAttrInfo.value = fileAttrsJSONObject.getString("value");
//														fileAttrInfo.modified = fileAttrsJSONObject.getBoolean("modified");
//														unmatchFeatureInfo.fileAttrs.add(fileAttrInfo);
//													}
//													importLayerInfo.unmatchFeatureInfoList.add(unmatchFeatureInfo);
//												}
//												importTrackData.importLayerList.add(importLayerInfo);
//											}
//
//											JSONArray tracktabledataJsonObjectList =  trackJsonObject.getJSONArray("tracktabledataJsonObjectList");
//											for(int j = 0; j < tracktabledataJsonObjectList.length(); j++){
//												DisconnectImportConfirmDto.ImportTrackTableData importTrackTableData = new DisconnectImportConfirmDto.ImportTrackTableData();
//												JSONObject tracktabledataJsonObject = tracktabledataJsonObjectList.getJSONObject(j);
//
//												importTrackTableData.tableComment = tracktabledataJsonObject.getString("tableComment");
//												importTrackTableData.tableName = tracktabledataJsonObject.getString("tableName");
//
//												importTrackTableData.columnCommentList = new ArrayList<String>();
//												JSONArray columnComentListJsonObjectList = tracktabledataJsonObject.getJSONArray("columnComentListJsonObjectList");
//												for(int k = 0; k < columnComentListJsonObjectList.length(); k++){
//													JSONObject columnComentListJsonObject = columnComentListJsonObjectList.getJSONObject(k);
//													importTrackTableData.columnCommentList.add(columnComentListJsonObject.getString("value"));
//												}
//
//												importTrackTableData.sameTrackTableDataRecordList = new ArrayList<DisconnectImportConfirmDto.SameTrackTableDataRecord>();
//												JSONArray samerecordJsonObjectList = tracktabledataJsonObject.getJSONArray("samerecordJsonObjectList");
//												for(int l = 0; l < samerecordJsonObjectList.length(); l++){
//													JSONObject samerecordJsonObject = samerecordJsonObjectList.getJSONObject(l);
//													DisconnectImportConfirmDto.SameTrackTableDataRecord sameTrackTableDataRecord = new DisconnectImportConfirmDto.SameTrackTableDataRecord();
//													sameTrackTableDataRecord.dbGroupId = samerecordJsonObject.getString("dbGroupId");
//													sameTrackTableDataRecord.dbGroupName = samerecordJsonObject.getString("dbGroupName");
//													sameTrackTableDataRecord.fileGroupId = samerecordJsonObject.getString("fileGroupId");
//													sameTrackTableDataRecord.fileGroupName = samerecordJsonObject.getString("fileGroupName");
//													sameTrackTableDataRecord.checkBoxValue = samerecordJsonObject.getString("checkBoxValue");
//
//													sameTrackTableDataRecord.dbRecord = new ArrayList<String>();
//													JSONArray dbRecordJsonObjectList = samerecordJsonObject.getJSONArray("dbRecordJsonObjectList");
//													for(int m = 0 ; m < dbRecordJsonObjectList.length(); m++){
//														JSONObject dbRecordJsonObject = dbRecordJsonObjectList.getJSONObject(m);
//														sameTrackTableDataRecord.dbRecord.add(dbRecordJsonObject.getString("value"));
//													}
//
//													sameTrackTableDataRecord.fileRecord = new ArrayList<String>();
//													JSONArray fileRecordJsonObjectList = samerecordJsonObject.getJSONArray("fileRecordJsonObjectList");
//													for(int n = 0 ; n < fileRecordJsonObjectList.length(); n++){
//														JSONObject fileRecordJsonObject = fileRecordJsonObjectList.getJSONObject(n);
//														sameTrackTableDataRecord.fileRecord.add(fileRecordJsonObject.getString("value"));
//													}
//													importTrackTableData.sameTrackTableDataRecordList.add(sameTrackTableDataRecord);
//												}
//												importTrackData.importTrackTableDataList.add(importTrackTableData);
//											}
//
//											disconnectImportConfirmDto.importTrackDataList.add(importTrackData);
//										}
//
//										// チェックボックスをONにしておく
//										List<String> updateFeaturesList = new ArrayList<String>();
//										List<String> updateTrackTableDataList = new ArrayList<String>();
//										StringBuffer sbuf = new StringBuffer();
//										for(DisconnectImportConfirmDto.ImportTrackData importTrackData : disconnectImportConfirmDto.importTrackDataList){
//											// レイヤ
//											for(DisconnectImportConfirmDto.ImportLayerInfo importLayerInfo : importTrackData.importLayerList){
//												for(DisconnectImportConfirmDto.UnmatchFeatureInfo unmatchFeatureInfo : importLayerInfo.unmatchFeatureInfoList){
//													updateFeaturesList.add(unmatchFeatureInfo.checkBoxValue);
//												}
//											}
//
//											// レイヤ以外
//											for(DisconnectImportConfirmDto.ImportTrackTableData importTrackTableData : importTrackData.importTrackTableDataList){
//												int idx = 0;
//												for(DisconnectImportConfirmDto.SameTrackTableDataRecord sameTrackTableDataRecord : importTrackTableData.sameTrackTableDataRecordList){
//													updateTrackTableDataList.add(sameTrackTableDataRecord.checkBoxValue);
//													String [] tmpArray = sameTrackTableDataRecord.checkBoxValue.split("-");
//													if(idx == 0){
//														if(sbuf.length() <= 0){
//															sbuf.append(tmpArray[0]);
//															sbuf.append("-");
//															sbuf.append(tmpArray[1]);
//															sbuf.append(",");
//														}else{
//															sbuf.append(":");
//															sbuf.append(tmpArray[0]);
//															sbuf.append("-");
//															sbuf.append(tmpArray[1]);
//															sbuf.append(",");
//														}
//													}else{
//														sbuf.append(tmpArray[1]);
//														sbuf.append(",");
//													}
//													idx++;
//												}
//												if(sbuf.length() > 0){
//											        int index = sbuf.lastIndexOf(",");
//											        sbuf = sbuf.deleteCharAt(index);
//												}
//											}
//										}
//										datasyncForm.updateFeatures = (String[])updateFeaturesList.toArray(new String[0]);
//										datasyncForm.updateTrackTableDatas = (String[])updateTrackTableDataList.toArray(new String[0]);
//										if(sbuf.length() > 0){
//									        updateTrackTableDatasHidden = sbuf.toString();
//										}
//
//									}
//
//								}catch(JSONException e){
//									hasError = "error";
//									errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
//									ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
//								}
//							}
//						}
//					}
//				}
//			}
//	    }catch(KeyStoreException e){
//			e.printStackTrace();
//			hasError = "error";
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
//			ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
//	    }catch(KeyManagementException e){
//			e.printStackTrace();
//			hasError = "error";
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
//			ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
//	    }catch(NoSuchAlgorithmException e){
//			e.printStackTrace();
//			hasError = "error";
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
//			ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
//		} catch (IOException e) {
//			hasError = "error";
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
//			ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
//		}
//
//		return "step4.jsp";
//
//	}
//
//	/**
//	 * @throws Exception
//	 *
//	 */
//	public String execdatasync() {
//
//
//		// CSRF対策
//		if (!FormUtils.checkToken(request)) {
//			throw new InvalidAccessException(lang.__("Invalid session."));
//		}
//
//		ActionMessages errors = new ActionMessages();

		// OAuth の初期化
		String cloudUrlBase = (String)session.getAttribute(Constants.OAUTH_CLOUDURL_SESSION_KEY);

		CookieStore cookieStore = new BasicCookieStore();
	    HttpClientContext context = HttpClientContext.create();
	    context.setCookieStore(cookieStore);

		// APIへリクエストを投げる
		try {
		    RequestConfig requestConfig = createRequestConfig();
		    SSLContext sslContext = createSSLContext();
			String sessionKey = (String)session.getAttribute(Constants.OAUTH_TOKEN_SESSION_KEY);
			List<Header> headers = new ArrayList<Header>();
//			headers.add(new BasicHeader("X-CSRF-Token",session.getId()));
			headers.add(new BasicHeader("Authorization","Bearer " + sessionKey));

			try (CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext)
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
					.setDefaultRequestConfig(requestConfig)
					.setDefaultHeaders(headers)
					.setDefaultCookieStore(cookieStore).build()) {
				// JSESSIONID取得のため、まずGETを行う。
				HttpGet getMethod = new HttpGet(cloudUrlBase + Constants.API_GETURL);
				try (CloseableHttpResponse getResponse = httpClient.execute(getMethod)) {
					if (getResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						List<Cookie> cookies = context.getCookieStore().getCookies();
						String jsessionid = "";
						for(Cookie cookie : cookies){
							if(cookie.getName().equals("JSESSIONID")){
								jsessionid = cookie.getValue();
							}
						}

						String localgovinfoidStr = (String)session.getAttribute(Constants.OAUTH_LOCALGOVINFOID_SESSION_KEY);
						// POSTする
						HttpPost postMethod = new HttpPost(cloudUrlBase + "api/v2/trackdatasync/execdatasync");
						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
						builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
						builder.addTextBody(FormUtils.TOKEN_PARAM_NAME, jsessionid);
					    builder.addTextBody("Authorization", "Bearer " + sessionKey);
					    builder.addTextBody("uploadTrackDataDirName", datasyncForm.uploadTrackDataDirName);
					    builder.addTextBody("uploadTrackDataFileName", datasyncForm.uploadTrackDataFileName);
					    builder.addTextBody("trackMapText", trackMapText);
					    builder.addTextBody("syncAll", datasyncForm.syncAll);
					    builder.addTextBody("syncAttachedFile", datasyncForm.syncAttachedFile);

					    String [] updateFeatures = datasyncForm.updateFeatures;
					    StringBuffer sbuf = new StringBuffer();
					    if(updateFeatures != null && updateFeatures.length > 0){
					    	for(int i = 0; i < updateFeatures.length; i++){
					    		sbuf.append(updateFeatures[i]);
					    		sbuf.append(",");
					    	}
					        int sbufIndex = sbuf.lastIndexOf(",");
					        sbuf.deleteCharAt(sbufIndex);
						    builder.addTextBody("updateFeatures", sbuf.toString());
					    }else{
						    builder.addTextBody("updateFeatures", "");
					    }


					    String [] updateTrackTableDatas = datasyncForm.updateTrackTableDatas;
					    StringBuffer sbuf2 = new StringBuffer();
					    if(updateTrackTableDatas != null && updateTrackTableDatas.length > 0){
					    	for(int i = 0; i < updateTrackTableDatas.length; i++){
					    		sbuf2.append(updateTrackTableDatas[i]);
					    		sbuf2.append(",");
					    	}
					        int sbufIndex = sbuf2.lastIndexOf(",");
					        sbuf2.deleteCharAt(sbufIndex);
						    builder.addTextBody("updateTrackTableDatas", sbuf2.toString());
					    }else{
						    builder.addTextBody("updateTrackTableDatas", "");
					    }

					    if(StringUtil.isEmpty(datasyncForm.updateTrackTableDatasHidden) ){
						    builder.addTextBody("updateTrackTableDatasHidden","");
					    }else{
						    builder.addTextBody("updateTrackTableDatasHidden",datasyncForm.updateTrackTableDatasHidden);
					    }

					    HttpEntity sendEntity = builder.build();
						postMethod.setEntity(sendEntity);
						try (CloseableHttpResponse postResponse = httpClient.execute(postMethod)) {
							if (postResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								HttpEntity reciveEntity = postResponse.getEntity();

								// 受信した文字列からJSONオブジェクトを生成し、DisconnectImportConfirmDtoオブジェクトに復元する
								String jsonStr = EntityUtils.toString(reciveEntity,StandardCharsets.UTF_8);
								try{
									disconnectImportConfirmDto = new DisconnectImportConfirmDto();
									JSONObject resultData = new JSONObject(jsonStr);
									// エラーチェック
									String status = resultData.getString(Constants.JSON_KEY_STATUS);
									if(! status.equals(Constants.JSON_KEY_STATUSSUCCESS)){
										hasError = "error";
										errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(resultData.getString(Constants.JSON_KEY_ERRORMESSAGE), false));
										ActionMessagesUtil.addErrors(bindingResult, errors);
									}else{
										JSONArray trackJsonObjectList = resultData.getJSONArray("trackJsonObjectList");

										// クラウドに保存した災害データファイルのファイル名とディレクトリ名を取得
										uploadTrackDataDirName = resultData.getString("uploadTrackDataDirName");
										uploadTrackDataFileName = resultData.getString("uploadTrackDataFileName");

										disconnectImportConfirmDto.importTrackDataList = new ArrayList<ImportTrackData>();
										for(int i = 0; i < trackJsonObjectList.length(); i++){
											DisconnectImportConfirmDto.ImportTrackData importTrackData = new DisconnectImportConfirmDto.ImportTrackData();
											JSONObject trackJsonObject = trackJsonObjectList.getJSONObject(i);

											JSONArray baseJsonObjectList =  trackJsonObject.getJSONArray("baseJsonObjectList");
											JSONObject baseJsonObject = baseJsonObjectList.getJSONObject(0);
											importTrackData.isUpdate = baseJsonObject.getBoolean("isUpdate");
											importTrackData.dbMapId = baseJsonObject.getLong("dbMapId");
											importTrackData.dbTrackDataId = baseJsonObject.getLong("dbTrackDataId");
											importTrackData.dbTrackDataName = baseJsonObject.getString("dbTrackDataName");
											importTrackData.fileMapId = baseJsonObject.getLong("fileMapId");
											importTrackData.fileTrackDataId = baseJsonObject.getLong("fileTrackDataId");
											importTrackData.fileTrackDataName = baseJsonObject.getString("fileTrackDataName");
											importTrackData.trackDataName = baseJsonObject.getString("trackDataName");
											importTrackData.isLayerInfoVisiblel = baseJsonObject.getBoolean("isLayerInfoVisiblel");
											disconnectImportConfirmDto.importTrackDataList.add(importTrackData);
										}
									}
								}catch(JSONException e){
									hasError = "error";
									errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
									ActionMessagesUtil.addErrors(bindingResult, errors);
								}
							}
						}
					}
				}
			}
	    }catch(KeyStoreException e){
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
	    }catch(KeyManagementException e){
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
	    }catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		} catch (IOException e) {
			hasError = "error";
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		if((StringUtil.isEmpty(hasError)) || (! hasError.equals("error"))){
			endMessage = lang.__("An import of disaster data has been completed.");
		}else{
			endMessage = "";
		}

		setupModel(model);

		return "/admin/disconnect/datasync/step5";
	}

	/**
	 * OAuth の初期化
	 */
	private void oAuthInit(String baseUrl, long localgovinfoid) {
		String oauth_token_url = baseUrl + "oauth2/token/";

		//  consumer key と consumer cecret key を取得する
		String appName = lang.__("NIED disaster information sharing system surrogate server");
		OauthconsumerData oauthconsumerData = oauthconsumerDataService.findByLocalgovinfoidAndApplicationname(localgovinfoid, appName);
		if(oauthconsumerData == null){
			throw new ServiceException(lang.__("Authentication information is not preserved by surrogate server. Please do an import of disaster data."));
		}else{
			oAuthConsumerKey = oauthconsumerData.consumerkey;
			oAuthConsumerSecretKey = oauthconsumerData.consumerkeysecret;
		}

		// cid を取得する
		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		oAuthCommunityId = mapmasterInfo.communityid + "";

		// OAuth アクセストークンの取得
		String code = request.getParameter("code");
		String accessToken = (String)session.getAttribute(Constants.OAUTH_TOKEN_SESSION_KEY);
		if (code != null && accessToken == null) {
	        HttpURLConnection conn = null;
	        OutputStreamWriter osw = null;
	        BufferedReader br = null;
	        try {
		        String data = "grant_type=authorization_code"
		                + "&code=" + code
		                + "&client_id=" + oAuthConsumerKey
		                + "&client_secret=" + oAuthConsumerSecretKey
		                + "&redirect_uri=dummy";

		        URL requestUrl = new URL(oauth_token_url);

		        if ("https".equals(requestUrl.getProtocol())) {

		            //証明書情報　全て空を返す
		            TrustManager[] tm = { new X509TrustManager() {
		                public X509Certificate[] getAcceptedIssuers() {
		                    return null;
		                }
		                @Override
		                public void checkClientTrusted(X509Certificate[] chain,
		                        String authType) throws CertificateException {
		                }
		                @Override
		                public void checkServerTrusted(X509Certificate[] chain,
		                        String authType) throws CertificateException {
		                }
		            } };
		            SSLContext sslcontext = SSLContext.getInstance("SSL");
		            sslcontext.init(null, tm, null);
		            //ホスト名の検証ルール　何が来てもtrueを返す
		            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
		                @Override
		                public boolean verify(String hostname,
		                        SSLSession session) {
		                    return true;
		                }
		            });

		            conn = (HttpsURLConnection) requestUrl.openConnection();

		            ((HttpsURLConnection)conn).setSSLSocketFactory(sslcontext.getSocketFactory());
		        // http接続の場合
		        } else {
		        	conn = (HttpURLConnection) requestUrl.openConnection();
		        }
		        conn.setDoOutput(true);
		        conn.setRequestMethod("POST");

		        osw = new OutputStreamWriter(conn.getOutputStream());
		        osw.write(data);
		        osw.flush();

		        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        StringBuilder sb = new StringBuilder();
				String str;
				while ((str = br.readLine()) != null)
				    sb.append(str);
				JSONObject json = new JSONObject(sb.toString());

		        accessToken = (String)json.get("access_token");
		        session.setAttribute(Constants.OAUTH_TOKEN_SESSION_KEY, accessToken);

		    } catch (Exception e) {
				throw new ServiceException(lang.__("Surrogate server failed in authentication processing with the NIED disaster information sharing system."));
		    }finally{
		    	try{
			    	if(osw != null){
				    	osw.close();
			    	}

			    	if(br != null ){
			    		br.close();
			    	}
		    	}catch(IOException e){
					throw new ServiceException(lang.__("Surrogate server failed in authentication processing with the NIED disaster information sharing system."));
		    	}
		    }
		}
	}

	private RequestConfig createRequestConfig(){
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		int connectTimeout = Integer.parseInt(rb.getString("DISCONNTCT_DATASYNC_CONNECT_TIMEOUT"));
		int sockettTmeout =  Integer.parseInt(rb.getString("DISCONNTCT_DATASYNC_SOCKET_TIMEOUT"));

		// タイムアウト設定
	    RequestConfig requestConfig = RequestConfig.custom()
	                    .setConnectTimeout(connectTimeout)          // コネクションタイムアウト
	                    .setSocketTimeout(sockettTmeout)        // 通信タイムアウト
	                    .build();

	    return requestConfig;
	}

	private SSLContext createSSLContext() throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException{
	    SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy(){
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
       }).build();

	    return sslContext;
	}


}
