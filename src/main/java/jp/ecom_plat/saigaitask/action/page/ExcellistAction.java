/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.ExcellistForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.TableFeatureService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.ReportDataService;
import jp.ecom_plat.saigaitask.service.excellist.ExcellistService;
import jp.ecom_plat.saigaitask.service.excellist.ExceltohtmlService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import net.sf.json.JSONObject;



/**
 * エクセル帳票を表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/page/excellist")
public class ExcellistAction extends PageBaseAction {

	protected ExcellistForm excellistForm;
	@Resource
	protected ExceltohtmlService exceltohtmlService;
	@Resource
	protected ExcellistService excellistService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected ReportDataService reportDataService;
	@Resource
	protected ResponseService responseService;
	@Resource
	protected MenuInfoService menuInfoService;

	public List<String> excelPages;
	public String excelPagesContent;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("excelPages", excelPages);
		model.put("excelPagesContent", excelPagesContent);
	}

	/**
	 * エクセル帳票ページを表示する.
	 * @return フォワード先
	 * @throws ParseException
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @ModelAttribute ExcellistForm excellistForm, BindingResult bindingResult) throws ParseException {
		this.excellistForm = excellistForm;
		initPage("excellist", excellistForm);

		long menuid;
		java.util.Date inputStatedDate;

		if(StringUtil.isEmpty(excellistForm.showtime_yy)){
			// 初回表示時
			menuid = pageDto.getMenuInfo().id;

			// 表示時刻の年の候補は前年、当年、翌年の３つにして
			// デフォルトで当年を選択状態にします。
			recalcShowYears(1);
			inputStatedDate = new Date();

			//表示時刻
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d-k-m");
			String currentTimeStr = sdf.format(new Timestamp(inputStatedDate.getTime()));
			String[] currentTime = currentTimeStr.split("-");
			excellistForm.showtime_mm = currentTime[1];
			excellistForm.showtime_dd = currentTime[2];
			excellistForm.showtime_hh = currentTime[3];
			excellistForm.showtime_mm2 = currentTime[4];
			//トークンを設定
			TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		}else{
			// 「表示」ボタン押下時
			menuid = excellistForm.menuid;

			inputStatedDate = TimeUtil.formatWareki(
					excellistForm.showtime_yy,
					excellistForm.showtime_mm,
					excellistForm.showtime_dd,
					excellistForm.showtime_hh,
					excellistForm.showtime_mm2,
				null);

			// 年選択リストボックス再作成
			String showtimeYy = excellistForm.showtime_yy;
			recalcShowYears(1);
			excellistForm.showtime_yy = showtimeYy;

		}
		// JST-UTC変換
		java.util.Date statedDate = convertUTC(inputStatedDate);


		// ExcelからHTMLを作成する
		// ファイル名生成
		ActionMessages errors = new ActionMessages();
		try{

			String dirPath = Constants.EXCELLIST_BASEDIR + loginDataDto.getLocalgovinfoid() + "/";
			String fileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + "menuinfoid_" + menuid + "_template.xlsx";
			
			System.out.println(new File(application.getRealPath("WEB-INF")).exists());
			System.out.println(new File(application.getRealPath(Constants.EXCELLIST_BASEDIR)).exists());
			System.out.println(new File(application.getRealPath(dirPath)).exists());

			
			String path = application.getRealPath(dirPath + fileName);
			//File excelTemplateFile = ResourceUtil.getResourceAsFile(dirPath + fileName);//new File(path);
			File excelTemplateFile = new File(path);
			if(! excelTemplateFile.exists()){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("No excel template file exits."), false));
			}else{
				exceltohtmlService.init(path, statedDate);
				Appendable content = exceltohtmlService.printPage();
				int pageCount = exceltohtmlService.getPageCount();
				exceltohtmlService.close();

				excelPages = new ArrayList<String>();
				for(int i = 1; i <= pageCount; i++ ){
					excelPages.add(Integer.toString(i));
				}
				excelPagesContent = content.toString();
			}
		}catch(IOException e){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to create HTML from the excel template file."), false));
		}

		if(! errors.isEmpty()){
			//ActionMessagesUtil.addErrors(bindingResult, errors);
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);

		return "/page/excellist/index";
	}


	/**
	 * エクセル帳票を作成する
	 * @return String
	 * @throws ParseException
	 */
	@RequestMapping(value="/createexcellist", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String createexcellist(){

		JSONObject json = new JSONObject();
		String menuinfoId = (String)request.getParameter("menuinfoid");
		String showtimeYy = (String)request.getParameter("showtime_yy");
		String showtimeMm = (String)request.getParameter("showtime_mm");
		String showtimeDd = (String)request.getParameter("showtime_dd");
		String showtimeHh = (String)request.getParameter("showtime_hh");
		String showtimeMm2 = (String)request.getParameter("showtime_mm2");
		java.util.Date inputStatedDate = TimeUtil.formatWareki(
				showtimeYy,
				showtimeMm,
				showtimeDd,
				showtimeHh,
				showtimeMm2,
			null);

		// JST-UTC変換
		java.util.Date statedDate = convertUTC(inputStatedDate);

		String userinputs = (String)request.getParameter(Constants.EXCELLIST_USERINPUTPREFIX);
		JSONObject userinputsJson = null;
		if(! StringUtil.isEmpty(userinputs)){
			userinputsJson = JSONObject.fromObject(userinputs);
		}


		// テンプレートファイル存在確認
		String dirPath = Constants.EXCELLIST_BASEDIR + loginDataDto.getLocalgovinfoid() + "/";
		String fileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + "menuinfoid_" + menuinfoId + "_template.xlsx";
		String path = application.getRealPath(dirPath + fileName);
		File templateFile = new File(path);

		try{
			if(templateFile.exists()){
				// テンプレートファイルから帳票ファイルを作成
				File excellistFile = excellistService.createExcellist(templateFile, Long.parseLong(menuinfoId), userinputsJson, statedDate);

				if(excellistFile != null && excellistFile.exists()){
					json.put("success", true);
					json.put("excelfile", excellistFile.getName());

				}else{
					json.put("success", false);
					json.put("message", lang.__("Failed to outpt excel file."));
				}
			}else{
				json.put("success", false);
				json.put("message", lang.__("No excel template file exits."));
			}

		}catch(Exception e){
			json.put("success", false);
			json.put("message", lang.__("Failed to outpt excel file."));
		}

		try{
			responseService.responseJson(json);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
		}

		return null;
	}

	/**
	 * エクセル帳票をダウンロードする
	 * @return String
	 * @throws ParseException
	 */
	@RequestMapping(value="/downloadexcellist")
	@ResponseBody
	public String downloadexcellist(){

		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		}
//		String menuinfoId = (String)request.getParameter("menuinfoid");
		String createdExcelfileName = (String)request.getParameter("excelfile");


		// ダウンロードファイルの名前生成
		String [] tmpArray = createdExcelfileName.split("_");
		String fileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + loginDataDto.getLocalgovinfoid() + "_" + loginDataDto.getGroupid() + "_" + tmpArray[4] + ".xlsx";

		// 作成済み帳票ファイルをダウンロードさせる
		String dirPath = Constants.EXCELLIST_BASEDIR + loginDataDto.getLocalgovinfoid() + "/";
		String createdExcelfilePath = application.getRealPath(dirPath + createdExcelfileName);
		OutputStream os = null;
		try {
			os = response.getOutputStream();

			FileInputStream hFile = new FileInputStream(createdExcelfilePath);
			BufferedInputStream bis = new BufferedInputStream(hFile);

			// レスポンス設定
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + fileName + "\"");

			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}

			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {

				} finally {
					os = null;
				}
			}

			// 作成済み帳票ファイルは削除しておく
			File createdExcelfile = new File(createdExcelfilePath);
			createdExcelfile.delete();
		}
		return null;
	}

	/**
	 * エクセル帳票を保存する
	 * @return String
	 * @throws ParseException
	 */
	@RequestMapping(value="/saveexcellist", method=org.springframework.web.bind.annotation.RequestMethod.POST, produces="application/json")
	public ResponseEntity<String> saveexcellist(){

		JSONObject json = new JSONObject();
		String menuinfoId = (String)request.getParameter("menuinfoid");
		String showtimeYy = (String)request.getParameter("showtime_yy");
		String showtimeMm = (String)request.getParameter("showtime_mm");
		String showtimeDd = (String)request.getParameter("showtime_dd");
		String showtimeHh = (String)request.getParameter("showtime_hh");
		String showtimeMm2 = (String)request.getParameter("showtime_mm2");
		java.util.Date inputStatedDate = TimeUtil.formatWareki(
				showtimeYy,
				showtimeMm,
				showtimeDd,
				showtimeHh,
				showtimeMm2,
			null);

		// JST-UTC変換
		java.util.Date statedDate = convertUTC(inputStatedDate);

		String userinputs = (String)request.getParameter(Constants.EXCELLIST_USERINPUTPREFIX);
		JSONObject userinputsJson = null;
		if(! StringUtil.isEmpty(userinputs)){
			userinputsJson = JSONObject.fromObject(userinputs);
		}


		// テンプレートファイル存在確認
		String dirPath = Constants.EXCELLIST_BASEDIR + loginDataDto.getLocalgovinfoid() + "/";
		String fileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + "menuinfoid_" + menuinfoId + "_template.xlsx";
		String path = application.getRealPath(dirPath + fileName);
		File templateFile = new File(path);

		File excellistFile = null;
		try{
			if(templateFile.exists()){
				// テンプレートファイルから帳票ファイルを作成
				excellistFile = excellistService.createExcellist(templateFile, Long.parseLong(menuinfoId), userinputsJson, statedDate);

				if(excellistFile != null && excellistFile.exists()){

					// メニュー情報にあるエクセル帳票出力先設定を取得する
					String tablename = "report_data";
					String layerId = null;
					String registtimeAttrId=null;
					String filepathAttrId=null;
					try {
						MenuInfo menuInfo = menuInfoService.findById(Long.valueOf(menuinfoId));

						// get tablename or layerid
						Long tmid = menuInfo.getExcellistoutputtablemasterinfoid();
						TablemasterInfo tmaster = tablemasterInfoService.findById(tmid);
						tablename = tmaster.tablename; layerId = tmaster.layerid;
						TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tmid, loginDataDto.getTrackdataid());
						if(ttbl!=null) {
							tablename = ttbl.tablename; layerId = ttbl.layerid;
						}
						
						// get registtime attrId
						registtimeAttrId = menuInfo.getExcellistoutputtableregisttimeattrid();
						
						filepathAttrId = menuInfo.getExcellistoutputtabledownloadlinkattrid();
					} catch(Exception e) {
						logger.error("error", e);
					}

					// 
					MapDB mapDB = MapDB.getMapDB();
					UserInfo userInfo = mapDB.getAuthIdUserInfo(loginDataDto.getEcomUser());
					if(userInfo==null) {
						throw new ServiceException(lang.__("e-community map user not found."));
					}

					// 出力ファイル名に付与する一意のIDを取得
					String excelid = null;
					ReportData entity = null;
					Long featureId = null;
					if("report_data".equals(tablename)) {
						Timestamp now = new Timestamp(System.currentTimeMillis());
						entity = new ReportData();
						entity.trackdataid = loginDataDto.getTrackdataid();
						entity.registtime = now;
						reportDataService.insert(entity);
						
						excelid = "report_data_"+entity.id;
					}
					else {
						HashMap<String, String> attributes = new HashMap<>();
						attributes.put(registtimeAttrId, new Timestamp(inputStatedDate.getTime()).toString());
						featureId = FeatureDB.insertFeature(userInfo, layerId, null, attributes);
						excelid = layerId+"_"+featureId;
					}

					// 4号様式の保存フォルダにコピーする
					// 登録日時はUTCではなくローカルタイムにする（一覧画面でUTC時刻表示になってしまうため）
					String dateStr = new SimpleDateFormat("yyyyMMddHHmm").format(inputStatedDate);
					String saveFileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + loginDataDto.getLocalgovinfoid() + "_" + loginDataDto.getGroupid() + "_" + dateStr + "_" + excelid +  ".xlsx";
					String savePath = FileUtil.getUploadPath(application, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid());
					File saveFile = new File(savePath+saveFileName);
					if(FileUtil.fileCopyByPath(excellistFile.getAbsolutePath(), saveFile.getAbsolutePath())){

						//ファイルが出力されたら、ファイルパスをDBに格納する
						String outPath = savePath+saveFileName;
						String retPath = "/"+outPath.substring(outPath.indexOf("upload/"));
						if(entity!=null) {
							entity.filepath = retPath;
							// 登録日時はUTCではなくローカルタイムにする（一覧画面でUTC時刻表示になってしまうため）
							//entity.registtime =  new Timestamp(statedDate.getTime());
							entity.registtime =  new Timestamp(inputStatedDate.getTime());

							reportDataService.update(entity);
						}
						else {
							HashMap<String, String> attribute = new HashMap<>();
							attribute.put(filepathAttrId, retPath);
							FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attribute, null);
						}

						json.put("success", true);
						json.put("excelfile", saveFile.getName());
					}else{
						json.put("success", false);
						json.put("message", lang.__("Failed to outpt excel file."));
					}
				}else{
					json.put("success", false);
					json.put("message", lang.__("Failed to outpt excel file."));
				}
			}else{
				json.put("success", false);
				json.put("message", lang.__("No excel template file exits."));
			}

		}catch(Exception e){
			json.put("success", false);
			json.put("message", lang.__("Failed to outpt excel file."));
		}finally{
			// 一時ディレクトリに作成したExcelファイルは削除しておく
			if(excellistFile != null && excellistFile.exists()){
				excellistFile.delete();
			}
		}

		//try{
		//	responseService.responseJson(json);
		//}catch(Exception e){
		//	logger.error(loginDataDto.logInfo(), e);
		//}
		//
		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
	}

	protected void recalcShowYears(int defaultIndex) {
		// 発表日時の候補は前年、当年、翌年の３つにします。
		int listsize = 3;
		excellistForm.showtime_yy_list = new String[listsize];

		Calendar cal0 = Calendar.getInstance();
		int year0 = cal0.get(Calendar.YEAR) - 1;	// 前年
		for(int i=0; i<listsize; i++){
			Calendar cal = (Calendar)cal0.clone();
			cal.set(Calendar.YEAR, year0 + i);
			String[] nen = TimeUtil.getWareki(cal.getTime()); 	// 和暦で選択
			excellistForm.showtime_yy_list[i] = nen[0]+nen[1];
		}

		if(0 <= defaultIndex && defaultIndex < listsize){
			// デフォルトで選択状態にします。
			excellistForm.showtime_yy = excellistForm.showtime_yy_list[defaultIndex];
		}
	}

	private java.util.Date convertUTC(java.util.Date inputDate){

//		long d = 1000 * 60 * 60 * 9;
//		java.util.Date convertDate = new Date(jstDate.getTime() - d);
//		return convertDate;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String convertDateStr =  formatter.format(inputDate);

        // Date型変換
		Date convertDate = null;
		try{
			formatter.setTimeZone(TimeZone.getDefault());
			convertDate = formatter.parse(convertDateStr);
		}catch(ParseException e){

		}

		return convertDate;

	}
}
