/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.RasterDownloadStatusDto;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.RasterDownloadStatusDto.RasterDownloadStatus;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.form.admin.disconnect.RasterDataDownloadForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.HttpUtil;
import jp.ecom_plat.saigaitask.util.MathUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;
import net.sf.json.JSONObject;


@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/disconnect/rasterDataDownload")
public class RasterDataDownloadAction extends AbstractDisconnectAction {

	public RasterDataDownloadForm rasterDataDownloadForm;

	@Resource
	private ResponseService responseService;

	@Resource
	protected RasterDownloadStatusDto rasterDownloadStatusDto;

	/** 対象マップ切り替えSELECTオプション */
	public Map<String, String> mapUrlSelectOptions;

	/** ダウンロードリスト */
	public List<RasterDownloadStatus> rasterDownloadStatusList;

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("mapUrlSelectOptions", mapUrlSelectOptions);
		model.put("rasterDownloadStatusList", rasterDownloadStatusList);
	}

	protected Logger getLogger() {
		return Logger.getLogger(RasterDataDownloadAction.class);
	}

	/**
	 * トップ画面
	 * @return
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @ModelAttribute RasterDataDownloadForm rasterDataDownloadForm) {
		this.rasterDataDownloadForm = rasterDataDownloadForm;

		// セットアッパーの初期化
		initDisconnect();

		// 表示内容の取得
		content(model, rasterDataDownloadForm);

	return "/admin/disconnect/rasterDataDownload/index";
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/content")
	public String content(Map<String,Object>model, @ModelAttribute RasterDataDownloadForm rasterDataDownloadForm) {
		this.rasterDataDownloadForm = rasterDataDownloadForm;

		LocalgovInfo localgovInfo;
		if(loginDataDto.getLocalgovinfoid()==0) {
			rasterDataDownloadForm.systemname = siteName;
			// 自治体セレクトボックスの作成
			createLocalgovSelectOptions();

			// 先頭の自治体を取得
			Long topLocalgovinfoid = 0L;
			for(Map.Entry<Long, String> e : localgovSelectOptions.entrySet()){
				topLocalgovinfoid = e.getKey();
				break;
			}
			localgovInfo = localgovInfoService.findById(topLocalgovinfoid);
		}
		else {
			// 自治体情報
			localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		}
		setlocalgovContent(localgovInfo);

		// 対象マップセレクトボックスの作成
		// プロパティファイルから取得
		mapUrlSelectOptions = new LinkedHashMap<String, String>();
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String baseStr = rb.getString("DISCONNECT_RASTERDATA_DOWNLOADMAPS");
		if(baseStr != null){
			String regex = "\\{[^\\{]+\\}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(baseStr);
			while(m.find()){
				String tempStr  =  m.group();
				tempStr = tempStr.replaceAll("\\{", "");
				tempStr = tempStr.replaceAll("\\}", "");
				String [] tempArray = tempStr.split(",");
				mapUrlSelectOptions.put(tempArray[0], tempArray[1]);
			}
		}

		rasterDownloadStatusList = rasterDownloadStatusDto.list(loginDataDto.getLocalgovinfoid());

		setupModel(model);

		return "/admin/disconnect/rasterDataDownload/content";
	}

	/**
	 * 画面上のセレクトボックスで選択した自治体の自治体情報でフォームを更新
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/changeLocalgov")
	@ResponseBody
	public String changeLocalgov(){
		String localgovInfoIdStr = (String)request.getParameter("selectLocalgov");

		try{
			long localgovInfoId = Long.parseLong(localgovInfoIdStr);
			LocalgovInfo localgovInfo = localgovInfoService.findById(localgovInfoId);
			setlocalgovContent(localgovInfo);

			JSONObject json = new JSONObject();
			json.put("systemname", localgovInfo.systemname);
			json.put("localgovType", localgovInfo.localgovtypeid);
			json.put("selectLocalgov", localgovInfoId);
			json.put("startZoomLevel", rasterDataDownloadForm.startZoomLevel);
			json.put("endZoomLevel", rasterDataDownloadForm.endZoomLevel);
			responseService.responseJson(json);

		}catch(NumberFormatException e){
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to get info of selected local gov.changeLocalgov()."), e);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to create JSON changeLocalgov()."), e);
		}

		return null;
	}


	/**
	 * 自治体の種別から取得するズームレベルの範囲を求める
	 * @param localgovInfo
	 */
	private void setlocalgovContent(LocalgovInfo localgovInfo){
		Beans.copy(localgovInfo, rasterDataDownloadForm).execute();
		rasterDataDownloadForm.startZoomLevel = "8";
		if(localgovInfo.localgovtypeid == Constants.LOCALGOVINFOTYPE_PREF){
			rasterDataDownloadForm.endZoomLevel = "16";
		}else if(localgovInfo.localgovtypeid == Constants.LOCALGOVINFOTYPE_CITY){
			rasterDataDownloadForm.endZoomLevel = "18";
		}
		rasterDataDownloadForm.localgovType = String.valueOf(localgovInfo.localgovtypeid);
	}

	/**
	 * ラスターデータを地図サーバからダウンロードする
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/download", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String download(){

		JSONObject json = new JSONObject();

		String localgovInfoIdStr = (String)request.getParameter("selectLocalgov");
		String selectMapUrl = (String)request.getParameter("selectMapUrl");
		String startLatStr = (String)request.getParameter("startLat");
		String startLonStr = (String)request.getParameter("startLon");
		String endLatStr = (String)request.getParameter("endLat");
		String endLonStr = (String)request.getParameter("endLon");
		String startZoomLevel = (String)request.getParameter("startZoomLevel");
		String endZoomLevel = (String)request.getParameter("endZoomLevel");


		try{
			List<String> errorList = borderCheck(startLonStr, startLatStr, endLonStr, endLatStr);
			if(errorList.size() > 0){
				json.put("errors", errorList);
				responseService.responseJson(json);
				return null;
			}

			Double startLat = Double.parseDouble(startLatStr);
			Double startLon = Double.parseDouble(startLonStr);
			Double endLat = Double.parseDouble(endLatStr);
			Double endLon = Double.parseDouble(endLonStr);

			// 対象ズームレベル
			int zoomLevelMin = Integer.parseInt(startZoomLevel);
			int zoomLevelMax = Integer.parseInt(endZoomLevel);


			Map<String, Double> innerCalcResult = innerCalc(zoomLevelMin, zoomLevelMax, startLat, startLon, endLat, endLon);
			/*
			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			String limitSizeUnit = rb.getString("DISCONNECT_RASTERDATA_GSITILE_DOWNLOADSIZE_LIMIT_UNIT");
			double limitSize;
			double multiply;
			Map<String,Double> limitSizeMap = getDownloadLimitSize(limitSizeUnit);
			limitSize = limitSizeMap.get("limitSize");
			multiply  = limitSizeMap.get("multiply");
			if( innerCalcResult.get("datasize") > limitSize * multiply){
				errorList.add(lang.__("Approximation data size is exceed {0}. Narrow the target district.", limitSize + limitSizeUnit));
				json.put("errors", errorList);
				responseService.responseJson(json);
				return null;
			}
			*/

			// テンポラリフォルダ作成
			File tmpDir = FileUtil.getTmpDir();
			// タイムスタンプから保存フォルダ名を生成
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String saveDirName = "saigaitask-download-rasterdata-";
			if(loginDataDto.getLocalgovinfoid()==0) {
				saveDirName += "localgovinfoid" + localgovInfoIdStr;
			}else{
				saveDirName += "localgovinfoid" + loginDataDto.getLocalgovinfoid();
			}
			saveDirName += "-"+timestamp;

			File saveDir = new File(tmpDir, saveDirName);
			saveDir.mkdir();

			/*
			// ダウンロード中のものがあったらキャンセル
			List<RasterDownloadStatus> statusList = rasterDownloadStatusDto.list(loginDataDto.getLocalgovinfoid());
			for(RasterDownloadStatus status : statusList) {
				if(!"100".equals(status.getProggress())) {
					status.cancel = true;
					// todo 作業ディレクトリの削除
				}
			}
			*/

			RasterDataDownloadRun r = new RasterDataDownloadRun(saveDir, selectMapUrl, zoomLevelMin, zoomLevelMax, startLat, startLon, endLat, endLon, innerCalcResult, loginDataDto);
			Thread t = new Thread(r);
			t.start();

			responseService.responseJson(json);

		}catch(Exception e){
			logger.error(loginDataDto.logInfo()+ lang.__("\n Failed to download raster data download()."),e);
		}


		return null;
	}

//	public String download(){
//
//		// CSRF対策
//		if (!FormUtils.checkToken(request)) {
//			throw new InvalidAccessException(lang.__("Invalid session."));
//		};
//
//		JSONObject json = new JSONObject();
//
//		File tmpDir = FileUtil.getTmpDir();
//		File zipFile = new File(tmpDir, "test.zip");
//		json.put("zipfilename", zipFile.getName());
//
//		try{
//			responseService.responseJson(json);
//		}catch(Exception e){
//
//		}
//
//		return null;
//	}
	/**
	 * ラスターデータZIPファイルをクライアントへダウンロード
	 * @return
	 */
	@RequestMapping(value="/downloadfile")
	@ResponseBody
	public String downloadfile(){
		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		};


		String zipFileName = request.getParameter("zipfile");
		RasterDownloadStatus status = rasterDownloadStatusDto.findByZipfilename(loginDataDto.getLocalgovinfoid(), zipFileName);
		File zipFile = status.getZipFile();
		if(zipFile.exists()){
			InputStream is = null;
			try {
				is = new FileInputStream(zipFile);
				downloadFile(zipFile.getName(), is);
				status.deleteZipfile();
				rasterDownloadStatusDto.unregist(status);
			} catch (FileNotFoundException e) {
				logger.error(loginDataDto.logInfo()+ "\n downloadfile() \n" + lang.__("File {0} read error", zipFileName),e);
			} catch(IORuntimeException ie){
				logger.error(loginDataDto.logInfo()+ "\n downloadfile() \n" + lang.__("Download {0} was canceled.", zipFileName),ie);
				status.deleteZipfile();
			} finally {
				if(is!=null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(loginDataDto.logInfo()+ "\n downloadfile() \n" + lang.__("Failed to close file {0}.", zipFileName),e);
					}
				}
			}
		}else{
			logger.error(loginDataDto.logInfo()+ "\n downloadfile() \n" + lang.__("File {0} not exist.", zipFileName));
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Target download file {0} not exist. Retry operation to set target district.", zipFileName), false));
			ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
			return "/admin/disconnect/rasterDataDownload/";
		}

		return null;
	}


		/**
	 * ダウンロードファイルサイズとダウンロード時間の計算
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/calc", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String calc(){

		JSONObject json = new JSONObject();

		String startLatStr = (String)request.getParameter("startLat");
		String startLonStr = (String)request.getParameter("startLon");
		String endLatStr = (String)request.getParameter("endLat");
		String endLonStr = (String)request.getParameter("endLon");
		String startZoomLevel = (String)request.getParameter("startZoomLevel");
		String endZoomLevel = (String)request.getParameter("endZoomLevel");

		try{
			List<String> errorList = borderCheck(startLonStr, startLatStr, endLonStr, endLatStr);
			if(errorList.size() > 0){
				json.put("errors", errorList);
				responseService.responseJson(json);
				return null;
			}

			Double startLat = Double.parseDouble(startLatStr);
			Double startLon = Double.parseDouble(startLonStr);
			Double endLat = Double.parseDouble(endLatStr);
			Double endLon = Double.parseDouble(endLonStr);

			// 対象ズームレベル
			int zoomLevelMin = Integer.parseInt(startZoomLevel);
			int zoomLevelMax = Integer.parseInt(endZoomLevel);

			Map<String, Double> innerCalcResult = innerCalc(zoomLevelMin, zoomLevelMax, startLat, startLon, endLat, endLon);
			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			String limitSizeUnit = rb.getString("DISCONNECT_RASTERDATA_GSITILE_DOWNLOADSIZE_LIMIT_UNIT");
			double limitSize;
			double multiply;
			Map<String,Double> limitSizeMap = getDownloadLimitSize(limitSizeUnit);
			limitSize = limitSizeMap.get("limitSize");
			multiply  = limitSizeMap.get("multiply");
			if( innerCalcResult.get("datasize") > limitSize * multiply){
				errorList.add(lang.__("Approximation data size is exceed {0}. Narrow the target district.", limitSize + limitSizeUnit));
				json.put("errors", errorList);
			}

			Map<String, String> calcResult = addUnit(innerCalcResult);
			json.put("datasize", calcResult.get("datasize"));
			json.put("downloadtime", calcResult.get("downloadtime"));
			responseService.responseJson(json);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo()+ "\n calc() \n " + lang.__("Failed to calculate tile coordinates."),e);
		}

		return null;
	}

	/**
	 * 地図表示ページを開く
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/showMap")
	public String showMap() {

		return "/admin/disconnect/rasterDataDownload/map";
	}

	/**
	 * ラスター画像ダウンロード処理を別スレッドで実行する.
	 */
	private class RasterDataDownloadRun implements Runnable {

		File saveDir;
		String url;
		int zoomLevelMin;
		int zoomLevelMax;
		Double startLat;
		Double startLon;
		Double endLat;
		Double endLon;
		Map<String, Double> innerCalcResult;
		LoginDataDto loginDataDto;

		/**
		 * ラスターデータを地図サーバからダウンロードし、所定のディレクトリへ保存する
		 * @param saveDir
		 * @param url
		 * @param zoomLevelMin
		 * @param zoomLevelMax
		 * @param startLat
		 * @param startLon
		 * @param endLat
		 * @param endLon
		 * @param innerCalcResult ダウンロードデータ計算結果
		 */
		public RasterDataDownloadRun(File saveDir, String url, int zoomLevelMin, int zoomLevelMax, Double startLat, Double startLon, Double endLat, Double endLon, Map<String, Double> innerCalcResult, LoginDataDto loginDataDto) {
			this.saveDir=saveDir;
			this.url=url;
			this.zoomLevelMin=zoomLevelMin;
			this.zoomLevelMax=zoomLevelMax;
			this.startLat=startLat;
			this.startLon=startLon;
			this.endLat=endLat;
			this.endLon=endLon;
			this.innerCalcResult=innerCalcResult;
			this.loginDataDto=new LoginDataDto();
			BeanUtils.copyProperties(loginDataDto, this.loginDataDto);
		}
		public  void run(){
			try {
				RasterDownloadStatus status = getRasterData(saveDir, url, zoomLevelMin, zoomLevelMax, startLat, startLon, endLat, endLon, innerCalcResult);
				if(status.result){
					String zipFileName =  saveDir.getAbsolutePath()+".zip";
					if(FileUtil.zipDirectory(saveDir.getAbsolutePath(), zipFileName)){
						File zipFile = new File(zipFileName);
						status.zipfilename = zipFile.getName();

						FileUtil.dirDelete(saveDir);
					}
				}else{
					logger.error(loginDataDto.logInfo()+ lang.__("\n Failed to get raster data download()."));
					status.errorList.add(lang.__("Failed to get raster data."));
				}
			}catch(Exception e){
				logger.error(loginDataDto.logInfo()+ lang.__("\n Failed to download raster data download()."),e);
			}
		}

		/**
		 * ラスターデータを地図サーバからダウンロードし、所定のディレクトリへ保存する
		 * @param saveDir
		 * @param url
		 * @param zoomLevelMin
		 * @param zoomLevelMax
		 * @param startLat
		 * @param startLon
		 * @param endLat
		 * @param endLon
		 * @param innerCalcResult ダウンロードデータ計算結果
		 * @return
		 */
		private RasterDownloadStatus getRasterData(File saveDir, String url, int zoomLevelMin, int zoomLevelMax, Double startLat, Double startLon, Double endLat, Double endLon, Map<String, Double> innerCalcResult){

			// ダウンロード状況の登録
			Double pixCalcSum = innerCalcResult.get("pixCalcSum");
			RasterDownloadStatus status = rasterDownloadStatusDto.regist(loginDataDto, pixCalcSum);

			// ズームレベルで繰り返し
			double logProgress = 0;
			for(int zoomLevelIndex = zoomLevelMin ; zoomLevelIndex <= zoomLevelMax; zoomLevelIndex++){
				Map<String,Integer> startTileCoordinateMap = MathUtil.getGSITileCoordinate(zoomLevelIndex, startLat, startLon);
				Map<String,Integer> endTileCoordinateMap = MathUtil.getGSITileCoordinate(zoomLevelIndex, endLat, endLon);

				int startTileLat;
				int startTileLon;
				int endTileLat;
				int endTileLon;

				// 保存フォルダがない場合は作成しておく
				File saveDirZoomlevel = new File(saveDir,String.valueOf(zoomLevelIndex));
				if(!saveDirZoomlevel.exists()){
					saveDirZoomlevel.mkdir();
				}

				if(startTileCoordinateMap.get("lat") <= endTileCoordinateMap.get("lat")){
					startTileLat = startTileCoordinateMap.get("lat");
					endTileLat   = endTileCoordinateMap.get("lat");
				}else{
					startTileLat = endTileCoordinateMap.get("lat");
					endTileLat   = startTileCoordinateMap.get("lat");
				}

				if(startTileCoordinateMap.get("lon") <= endTileCoordinateMap.get("lon")){
					startTileLon = startTileCoordinateMap.get("lon");
					endTileLon   = endTileCoordinateMap.get("lon");
				}else{
					startTileLon = endTileCoordinateMap.get("lon");
					endTileLon   = startTileCoordinateMap.get("lon");
				}

				//URL作成
				for(int lonIndex = startTileLon; lonIndex <= endTileLon; lonIndex++){
					// 保存フォルダがない場合は作成しておく
					File saveDirLon = new File(saveDirZoomlevel,String.valueOf(lonIndex));
					if(!saveDirLon.exists()){
						saveDirLon.mkdir();
					}

					for(int latIndex = startTileLat; latIndex <= endTileLat; latIndex++){
						// ダウンロードキャンセルチェック
						if(status.cancel) {
							status.result=false;
							logger.info("Cancel raster download...");
							FileUtil.dirDelete(saveDir);
							return status;
						}

						StringBuffer urlBuf = new StringBuffer();
						urlBuf.append(url);
						urlBuf.append(String.valueOf(zoomLevelIndex));
						urlBuf.append("/");
						urlBuf.append(String.valueOf(lonIndex));
						urlBuf.append("/");
						urlBuf.append(String.valueOf(latIndex));
						urlBuf.append(".png");
//						System.out.println(urlBuf.toString());

						try{
							File saveFile = new File(saveDirLon,String.valueOf(latIndex) + ".png");
							URL targetUrl = new URL(urlBuf.toString());
							// ダウンロード実行
							HttpUtil.getFile(targetUrl, saveFile);
						}catch(MalformedURLException e){
							logger.error(loginDataDto.logInfo()+ "\n getRasterData() \n " + lang.__("{0} is invalid.", "URL:" + urlBuf.toString()),e);
							//status.result = false;
						}catch(Exception e){
							logger.error(loginDataDto.logInfo()+ "\n getRasterData() \n " + lang.__("Failed to get file for {0}.", "URL:" + urlBuf.toString()),e);
							//status.result = false;
						}finally {
							// ダウンロード数のカウント
							status.countDownload();

							// 進捗を5%単位でログ出力
							String progress = status.getProggress();
							if(logProgress<=Double.valueOf(progress)) {
								logger.debug("[RasterDownloadProggress]"+progress+"% "+String.format("%.0f", status.downloadedPixCalcSum)+"/"+String.format("%.0f", status.pixCalcSum)
										+" start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(status.starttime)
										+status.loginDataDto.logInfo());
								logProgress+=5D;
							}
						}
					}
				}
				status.result = true;
			}

			return status;
		}
	}

	/**
	 * 地図サーバからダウンロードするラスターデータのサイズと所要時間の概算を求める
	 * 基準となる値はSaigaiTask.propertiesから取得
	 * @param zoomLevelMin
	 * @param zoomLevelMax
	 * @param startLat
	 * @param startLon
	 * @param endLat
	 * @param endLon
	 * @return
	 */
	private Map<String,Double> innerCalc(int zoomLevelMin, int zoomLevelMax, Double startLat, Double startLon, Double endLat, Double endLon){
		Map<String,Double> returnMap= new HashMap<String,Double>();

		// 計算
		try{
			// タイル枚数
			int pixCalcSum = 0;
			// ファイルサイズ概算
			double datasize = 0.0;
			// ダウンロード時間概算
			double downloadtime = 0.0;

			// ズームレベルで繰り返し
			for(int zoomLevelIndex = zoomLevelMin ; zoomLevelIndex <= zoomLevelMax; zoomLevelIndex++){
				Map<String,Integer> startTileCoordinateMap = MathUtil.getGSITileCoordinate(zoomLevelIndex, startLat, startLon);
				Map<String,Integer> endTileCoordinateMap = MathUtil.getGSITileCoordinate(zoomLevelIndex, endLat, endLon);

				int startTileLat;
				int startTileLon;
				int endTileLat;
				int endTileLon;
				int pixCalc = 0;

				if(startTileCoordinateMap.get("lat") <= endTileCoordinateMap.get("lat")){
					startTileLat = startTileCoordinateMap.get("lat");
					endTileLat   = endTileCoordinateMap.get("lat");
				}else{
					startTileLat = endTileCoordinateMap.get("lat");
					endTileLat   = startTileCoordinateMap.get("lat");
				}

				if(startTileCoordinateMap.get("lon") <= endTileCoordinateMap.get("lon")){
					startTileLon = startTileCoordinateMap.get("lon");
					endTileLon   = endTileCoordinateMap.get("lon");
				}else{
					startTileLon = endTileCoordinateMap.get("lon");
					endTileLon   = startTileCoordinateMap.get("lon");
				}

				pixCalc = (endTileLon - startTileLon + 1) * (endTileLat - startTileLat + 1);
				pixCalcSum += pixCalc;
			}

			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			String baseDatasizeStr = rb.getString("DISCONNECT_RASTERDATA_GSITILE_FILESIZE_AVG_KB");
			String baseDownloadtimeStr = rb.getString("DISCONNECT_RASTERDATA_GSITILE_DOWNLOADTIME_AVG_SEC");

			datasize = pixCalcSum * Double.parseDouble(baseDatasizeStr);
			downloadtime = pixCalcSum * Double.parseDouble(baseDownloadtimeStr);
//	        System.out.println("pixCalcSum:"+pixCalcSum);
//	        System.out.println("datasize:"+datasize);
//	        System.out.println("downloadtime:"+downloadtime)

			returnMap.put("pixCalcSum", Double.valueOf(pixCalcSum));
			returnMap.put("datasize", datasize);
			returnMap.put("downloadtime", downloadtime);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo()+ "\n innerCalc() \n " + lang.__("Failed to calculate raster data value."),e);
		}

		return returnMap;
	}

	/**
	 * 地図サーバからダウンロードするラスターデータのサイズ上限を
	 * SaigaiTask.propertiesから取得する
	 * @param limitSizeUnit
	 * @return
	 */
	private Map<String, Double> getDownloadLimitSize(String limitSizeUnit){

		Map<String, Double> returnMap = new HashMap<String, Double>();

		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String limitSizeBaseStr = rb.getString("DISCONNECT_RASTERDATA_GSITILE_DOWNLOADSIZE_LIMIT");
		double limitSize = Double.parseDouble(limitSizeBaseStr);
		double multiply = 0.0;
		if(limitSizeUnit.toUpperCase().equals("KB")){
			multiply = 1.0;
		}else if(limitSizeUnit.toUpperCase().equals("MB")){
			multiply = 1024.0;
		}else if(limitSizeUnit.toUpperCase().equals("GB")){
			multiply = 1024.0 * 1024.0;
		}

		returnMap.put("limitSize", limitSize);
		returnMap.put("multiply", multiply);

		return returnMap;
	}

	/**
	 * 算出したダウンロードするラスターデータのサイズと所要時間に単位を付与する
	 * @param innerCalcResult
	 * @return
	 */
	private Map<String,String> addUnit(Map<String, Double> innerCalcResult){
		Map<String,String> returnMap= new HashMap<String,String>();

		double datasize = innerCalcResult.get("datasize");
		double downloadtime = innerCalcResult.get("downloadtime");

		String datasizeUnitStr = "KB";
		if(datasize > 1048576.0){
			datasize = datasize / 1048576.0;
			datasizeUnitStr = "GB";
		}else if(datasize > 1024.0){
			datasize = datasize / 1024.0;
			datasizeUnitStr = "MB";
		}

		String downloadtimeUnitStr = lang.__("Second");
		if(downloadtime > 86400.0){
			downloadtime = downloadtime / 86400.0;
			downloadtimeUnitStr = lang.__("Day");
		}else if(downloadtime > 3600.0){
			downloadtime = downloadtime / 3600.0;
			downloadtimeUnitStr = lang.__("Time");
		}else if(downloadtime > 60.0){
			downloadtime = downloadtime / 60.0;
			downloadtimeUnitStr = lang.__(" minutes");
		}

        // 小数第2位で切り捨て、単位をつける
		BigDecimal datasizeBD = new BigDecimal(datasize);
		BigDecimal downloadtimeBD = new BigDecimal(downloadtime);
        String datasizeStr = datasizeBD.setScale(1, BigDecimal.ROUND_DOWN).toString() + datasizeUnitStr;
        String downloadtimeStr = downloadtimeBD.setScale(1, BigDecimal.ROUND_DOWN).toString() + downloadtimeUnitStr;

        returnMap.put("datasize", datasizeStr);
        returnMap.put("downloadtime", downloadtimeStr);

        return returnMap;
	}

	/**
	 * 画面入力項目チェック
	 * @param startLonStr
	 * @param startLatStr
	 * @param endLonStr
	 * @param endLatStr
	 * @return
	 */
	private List<String> borderCheck(String startLonStr, String startLatStr, String endLonStr, String endLatStr){
		List<String> result = new ArrayList<String>();

		// 必須チェック
		if(startLatStr == null || startLatStr.length() <= 0){
			result.add(lang.__("Start coordinates is required."));
		}
		if(startLonStr == null || startLonStr.length() <= 0){
			result.add(lang.__("Start longitude is required."));
		}
		if(endLatStr == null || endLatStr.length() <= 0){
			result.add(lang.__("End coordinate is required."));
		}
		if(endLonStr == null || endLonStr.length() <= 0){
			result.add(lang.__("End longitude is required."));
		}

		if(result.size() > 0){
			return result;
		}

		// 数値変換チェック
		try{
			Double.parseDouble(startLatStr);
		}catch(NumberFormatException e){
			result.add(lang.__("The start latitude is not numeric."));
		}
		try{
			Double.parseDouble(startLonStr);
		}catch(NumberFormatException e){
			result.add(lang.__("Start longitude is not numeric."));
		}
		try{
			Double.parseDouble(endLatStr);
		}catch(NumberFormatException e){
			result.add(lang.__("The end latitude is not numeric."));
		}
		try{
			Double.parseDouble(endLonStr);
		}catch(NumberFormatException e){
			result.add(lang.__("End longitude is not numeric."));
		}

		return result;
	}
}
