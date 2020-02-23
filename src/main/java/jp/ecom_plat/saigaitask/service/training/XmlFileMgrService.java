/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.training;


import static jp.ecom_plat.saigaitask.entity.Names.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.entity.db.TrainingmeteoData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;
import jp.ecom_plat.saigaitask.service.db.TrainingmeteoDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingplanDataService;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * XMLファイル編集管理
 */
@org.springframework.stereotype.Service
public class XmlFileMgrService {

	protected Logger logger = Logger.getLogger(XmlFileMgrService.class);


	public static final int JsonUserNewFile = 1;
	public static final int JsonUserEditFile = 2;
	public static final int JsonUserDelFile = 3;
	public static final int JsonUserSaveFile = 4;
	public static final int JsonUserAddButton = 5;
	public static final int JsonUserDelButton = 6;

	public static final int JsonUserGetFileType = 101;
	public static final int JsonUserDetailGetTypeFileList = 102;
	public static final int JsonGetCurFileNameOnlyNew = 103;
	public static final int JsonCheckFileExist = 104;
	/*
	 * エリアの入力は気象情報等取得情報テーブル(meteorequest_info) のエリアで実装しているプルダウンメニューを利用
	 * (main\webapp\WEB-INF\view\admin\grid\meteorequest_info.jsp  の実装を参考に）
	 * */
	public static final int JsonUserDetailGetAreaCombobox = 13;

    /** サービスクラス */
	protected ResponseService responseService = null;
	protected HttpServletRequest request = null;

	/** サービスクラス */
	@Resource
	protected MeteorequestInfoService meteorequestInfoService = null;

	/** サービスクラス XmlEditorActionからインスタンスをセットする */
	protected TrainingplanDataService trainingplanDataService;
	protected TrainingmeteoDataService trainingmeteoDataService;
    protected ServletContext application;

	/**
	 * 配置ファイル
	 */
@org.springframework.stereotype.Service
	public class PropData {
		public String strMETEOXMLEDIT_PATH = "";
		public String strMETEOXMLEDIT_XSLTEDITPATH = "";
//		public String strMETEOXMLURL_BASE_PATH = "";
//		public String strMETEOXMLURL_PATH_PREFIX = "";
	}
	/**
	 * ファイル情報
	 */
@org.springframework.stereotype.Service
	public class FileInfoData {
		public String strFileName;
		public int iFileType = 0;

	}
	/**
	 * Combobox情報
	 */
@org.springframework.stereotype.Service
	public class ComboboxItem {
		public String name;
		public String value;
	}
	/**
	 * 配置ファイル情報
	 */
	private PropData clsPropDataObj = new PropData();
	/**
	 * ファイル情報
	 */
	private FileInfoData clsFileInfoDataObj = new FileInfoData();

	/**
	 * responseを設定
	 * @param responseService
	 */
	public void setRp(ResponseService responseService) {
		this.responseService = responseService;
	}
	/**
	 * AJAXから入り口
	 * @param req
	 * @throws Exception
	 */
	public void entryAjax(HttpServletRequest req) throws Exception {
		//String realPath = ServletActionContext.getServletContext().getRealPath("");
		initProp();

		try {
			//req.setCharacterEncoding("UTF-8");
		} catch (Exception e3) {
			// TODO 自動生成された catch ブロック
			e3.printStackTrace();
		}

		// new
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------entryAjax");
		String apitype = req.getParameter("apitype");
		String filetype = req.getParameter("filetype");
		String jsondata = req.getParameter("jsondata");

		XmlFileEditHelper.outputLog("XmlEditorAction-------------------req.getParameterMap().size()=" + req.getParameterMap().size());
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------apitype=" + apitype);
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------filetype=" + filetype);
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------jsondata=" + jsondata);
		try {
			//name  = new String(name.getBytes("ISO-8859-1"),"UTF-8");
			if(jsondata != null) {
				// XmlFileEditHelper.outputLog("XmlEditorAction-------------------jsondata2=" + new String(jsondata.getBytes("ISO-8859-1"),"UTF-8"));
				XmlFileEditHelper.outputLog("XmlEditorAction-------------------jsondata2=" + jsondata);
			}
		} catch (Exception e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		}

		XmlFileEditHelper.outputLog("XmlEditorAction apitype=" + StringUtil.getInt(apitype));

		request = req;


		try {
			{
				XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
				Object oType = ext.getComboboxDataCodeName();
				this.request.setAttribute("MeteotypeMasterServiceExt", oType);
			}

			//その他の操作
			if(StringUtil.getInt(apitype) == XmlFileMgrService.JsonUserGetFileType) {
				XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();

				ArrayList<Map<String,String>> listF = ext.getComboboxDataCodeName();

				JSONObject json = new JSONObject();
				json.put("errcode", "0");
				json.put("errmsg", "");


				JSONObject o = new JSONObject();
				JSONArray array = new JSONArray();

				for(int i = 0; i < listF.size(); i++) {
					o = new JSONObject();
					Map<String,String> map = (Map<String,String>)listF.get(i);
					o.put("key", map.get("key"));
					o.put("value", map.get("value"));
					array.add(o);

				}

				json.put("combo", array);
;

				responseService.responseJson(json);
			}

			if(StringUtil.getInt(apitype) == XmlFileMgrService.JsonUserDetailGetTypeFileList) {
				// XMLファイル種別よって、このXMLの既存ファイルのリストを取得すること
				// ファイルのフォルダnewfile　
				ArrayList<String> listF = getFileListJsonByType(StringUtil.getInt(filetype));
				JSONObject json = new JSONObject();
				json.put("errcode", "0");
				json.put("errmsg", "");


				JSONObject o = new JSONObject();
				JSONArray array = new JSONArray();

				for(int i = 0; i < listF.size(); i++) {
					o = new JSONObject();
					String strFile = (String)listF.get(i);
					o.put("file", strFile);
					array.add(o);

				}

				json.put("combo", array);

				responseService.responseJson(json);
			}
			if(StringUtil.getInt(apitype) == XmlFileMgrService.JsonUserDetailGetAreaCombobox) {
				//this.g
				JSONObject json = new JSONObject();
				json.put("errcode", "0");
				json.put("errmsg", "");
				json.put("ComboboxDataObject", "");
				//JSONObject json = new JSONObject();
				responseService.responseJson(json);

			}
			if(StringUtil.getInt(apitype) == XmlFileMgrService.JsonGetCurFileNameOnlyNew) {
				//this.g
				String strCurFileName = (String)request.getSession().getAttribute("JsonGetCurFileNameOnlyNew");
				JSONObject json = new JSONObject();
				json.put("errcode", "0");
				json.put("errmsg", "");
				json.put("filename", strCurFileName);
				XmlFileEditHelper.outputLog("XmlEditorAction strCurFileName=" + strCurFileName);
				//JSONObject json = new JSONObject();
				//responseService.responseJsonText(json);
				responseService.responseJson(json);
			}
			if(StringUtil.getInt(apitype) == XmlFileMgrService.JsonCheckFileExist) {
				// ファイル存在の判断
				String contextPath = request.getSession().getServletContext().getRealPath("");
				String strXml = this.getPropData().strMETEOXMLEDIT_PATH;
				String strFolder = this.getFolder(StringUtil.getInt(filetype));
				String strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/";
				strTemp = StringUtil.replaceAll("//", "/", strTemp);

				JSONObject jsonIn = getJsonInData();

				XmlFileEditHelper.outputLog("XmlEditorAction xmlext_filename=" + jsonIn.getString("xmlext_filename"));

				String strPath = strTemp + jsonIn.getString("xmlext_filename");
				File fileT = new File(strPath);

				int iEditType =  StringUtil.getInt(jsonIn.getString("xmlext_edittype"));
				// 0 直接保存、1別名
				int iEditSaveType =  StringUtil.getInt(jsonIn.getString("xmlext_editsavetype"));

				XmlFileEditHelper.outputLog("XmlEditorAction strPath=" + strPath);
				XmlFileEditHelper.outputLog("XmlEditorAction iEditType=" + iEditType);
				XmlFileEditHelper.outputLog("XmlEditorAction iEditSaveType=" + iEditSaveType);

				for( ; ; ) {
					if(iEditType == 1) {
						// new
						if(fileT.exists()) {
							// ファイルが存在する
							JSONObject json = new JSONObject();
							json.put("errcode", "0");
							json.put("errmsg", "");
							json.put("filenameok", "-1");

							responseService.responseJson(json);
							break;

						}
					}
					else if(iEditType == 2) {
						// edit
						if(iEditSaveType == 1) {
							if(fileT.exists()) {
								// ファイルが存在する
								JSONObject json = new JSONObject();
								json.put("errcode", "0");
								json.put("errmsg", "");
								json.put("filenameok", "-1");
								responseService.responseJson(json);
								break;

							}
						}

					}

					JSONObject json = new JSONObject();
					json.put("errcode", "0");
					json.put("errmsg", "");
					json.put("filenameok", "1");
					responseService.responseJson(json);
					break;
				}
			}

		} catch (Exception e){

			XmlFileEditHelper.outputLog("Xml Exception-----------------1");

			logger.error(e.getMessage(), e);
			throw e;

		}






	}



	/**
	 * XmlEditorの入り口
	 * @param req
	 * @throws Exception
	 */
	public void entryJsp(HttpServletRequest req) throws Exception {
		initProp();

		// new
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------entry");
		String apitype = req.getParameter("apitype");
		String filetype = req.getParameter("filetype");
		String jsondata = req.getParameter("jsondata");

		XmlFileEditHelper.outputLog("XmlEditorAction-------------------req.getParameterMap().size()=" + req.getParameterMap().size());
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------apitype=" + apitype);
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------filetype=" + filetype);
		XmlFileEditHelper.outputLog("XmlEditorAction-------------------jsondata=" + jsondata);


		XmlFileEditHelper.outputLog("XmlEditorAction apitype=" + StringUtil.getInt(apitype));

		request = req;



		if(StringUtil.getInt(apitype) <= 100 && StringUtil.getInt(apitype) >= 1) {
			// ファイルの新規、編集、保存、削除
			newEditDelFile(StringUtil.getInt(apitype), StringUtil.getInt(filetype), "");
		}
		if(StringUtil.getInt(apitype) == XmlFileMgrService.JsonUserNewFile ||
				StringUtil.getInt(apitype) == XmlFileMgrService.JsonUserSaveFile
				) {

			{
				String contextPath = request.getSession().getServletContext().getRealPath("");
				String strXml = this.getPropData().strMETEOXMLEDIT_PATH;
				String strTempF = contextPath + "/WEB-INF/" + strXml;
				strTempF = StringUtil.replaceAll("//", "/", strTempF);

				XmlFileEditHelper.outputLog("XmlEditorAction strTempF=" + strTempF);

				FileUtil.delTempPathFileM(strTempF, 86400);
			}
		}
	}

	/**
	 * 配置ファイル対象を取得
	 * @return
	 */
	public PropData getPropData() {
		return clsPropDataObj;
	}
	/**
	 * JSONのデータを取得する
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJsonInData() throws Exception {

		JSONObject jsonIn = new JSONObject();

		String jsondata = request.getParameter("jsondata");
		XmlFileEditHelper.outputLog("jsondataA1 jsondata=" + jsondata);

		//((XmlFileT01Jishinsokuhou) file).utTest01();

		// jsondata = new String(jsondata.getBytes("ISO-8859-1"),"UTF-8");
		//jsondata = StringUtil.escapeUJava(jsondata);
		XmlFileEditHelper.outputLog("jsondataA2 jsondata=" + jsondata);
		String strIn = jsondata;
		jsonIn = JSONObject.fromObject(strIn);

		return jsonIn;
	}
	/**
	 * GovermentIDを取得する
	 * @return
	 */
	public String getLocalGovFolder() {
		// 20150827test.xml
		String str = (String) request.getSession().getAttribute("JsonGetCurLocalGovID");
		if(str == null)str = "";
		return str;
	}


	/**
	 * Xmlのフォルダを取得する
	 * @param iType
	 * @return
	 */
	public String getFolder(int iType) {

		XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
		@SuppressWarnings("unchecked")
		List<Map<String, String>> oType = (List<Map<String, String>>)this.request.getAttribute("MeteotypeMasterServiceExt");
		String strTypeFolder = ext.getTypeByID(""+iType, oType);
		if(getLocalGovFolder().isEmpty()) {

			return strTypeFolder;
		} else {
			String str = getLocalGovFolder();
			return str + "/" + strTypeFolder;
		}
	}

	/**
	 * Xmlファイルの編集対象を取得する
	 * @param iType
	 * @return
	 */
	public XmlFileBase getFileObject(int iType) {

		XmlFileTAllBase file = new XmlFileTAllBase();
		file.iFileType = iType;
		return file;
	}
	/**
	 * 配置ファイルの内容を初期化する
	 */
	public void initProp() {
		///usr/local/apache-tomcat-7/webapps/SaigaiTask2/WEB-INF/classes/SaigaiTask.properties
		/*
		#
		# kisho xml settings
		#
		#METEOURL=http://localhost:8080/Meteo/
		METEOURL=http://task.bosai-cloud.jp/Meteo/

		#\u53d6\u5f97\u3057\u305fxml\u683c\u7d0d\u7528WEB-INF/jmaxml/\u306b\u7d9a\u304fpath
		METEOXMLPATH=/getxml/
		#xslt\u683c\u7d0d\u7528WEB-INF/jmaxml/\u306b\u7d9a\u304fpath
		METEOXSLTPATH=/xslt/
		METEOXSLTVALUEPATH=/xslt_val/
		*/
		//clsPropDataObj.strMETEOXMLPATH
		ResourceBundle bundle = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
		clsPropDataObj.strMETEOXMLEDIT_PATH = bundle.getString("METEOXMLEDIT_PATH");
		clsPropDataObj.strMETEOXMLEDIT_XSLTEDITPATH = bundle.getString("METEOXMLEDIT_XSLTEDITPATH");
		XmlFileEditHelper.outputLog("clsPropDataObj.strMETEOXMLEDIT_PATH" + clsPropDataObj.strMETEOXMLEDIT_PATH);
		XmlFileEditHelper.outputLog("clsPropDataObj.strMETEOXMLEDIT_XSLTEDITPATH" + clsPropDataObj.strMETEOXMLEDIT_XSLTEDITPATH);

//		clsPropDataObj.strMETEOXMLURL_PATH_PREFIX = bundle.getString("METEOXMLURL_PATH_PREFIX");
//		clsPropDataObj.strMETEOXMLURL_BASE_PATH = bundle.getString("METEOXMLURL_BASE_PATH");
	}


	/**
	 * ファイルリストを取得する
	 * @param iType
	 * @return
	 */
	public ArrayList<String> getFileListJsonByType(int iType) {
		XmlFileEditHelper.outputLog("getFileListJsonByType start");
		ArrayList<String> listF = new ArrayList<String>();
		String contextPath = request.getSession().getServletContext().getRealPath("");
		String strXml = this.getPropData().strMETEOXMLEDIT_PATH;
		String strFolder = this.getFolder(iType);
		String strTemp = contextPath + "/WEB-INF/" + strXml + "/" + strFolder + "/";
		strTemp = StringUtil.replaceAll("//", "/", strTemp);

		XmlFileEditHelper.outputLog("getFileListJsonByType 1");
		XmlFileEditHelper.outputLog("getFileListJsonByType 1 + strTemp="+strTemp);
		File file = new File(strTemp);
		if(file.exists()) {
			File[] fileAll = file.listFiles();
			for(int i = 0; i < fileAll.length; i++)
			{
				if(fileAll[i].isFile()) {
					if(fileAll[i].getName().endsWith(".tmp")) {
						continue;
					}

					if(fileAll[i].getName().endsWith(".new")) {
						continue;
					}
					if(fileAll[i].getName().endsWith(".in")) {
						continue;
					}
					if(fileAll[i].getName().endsWith(".xml")) {
						listF.add(fileAll[i].getName());
					}

				}
			}
		}
		XmlFileEditHelper.outputLog("getFileListJsonByType 2");
		return listF;
	}

	/**
	 * 画面は新規する場合
	 * iType=1　strFileNameファイル名は規則より生成する
	 * 画面は編集する場合
	 * iType=2　strFileNameユーザー選択よって
	 * 画面は削除する場合
	 * iType=3　strFileNameユーザー選択よって
	 *
	 * @param iApiType
	 * @param strFileName
	 * @throws Exception
	 */
	public void newEditDelFile(int iApiType, int iFileType, String strFileName) throws Exception {

		try {
			{
				XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
				Object oType = ext.getComboboxDataCodeName();
				this.request.setAttribute("MeteotypeMasterServiceExt", oType);
			}


			XmlFileEditHelper.outputLog("newEditDelFile iApiType=" + iApiType);
			XmlFileEditHelper.outputLog("newEditDelFile iFileType=" + iFileType);
			clsFileInfoDataObj.iFileType = iFileType;

			XmlFileBase file = this.getFileObject(iFileType);
			file.setFileType(iFileType);
			file.setRP(responseService);
			file.request = request;

			if(iApiType == XmlFileMgrService.JsonUserNewFile) {

				XmlFileEditHelper.outputLog("JsonUserNewFile 1");
				//XmlFileT01Jishinsokuhou
				//直接内容なしのJSONを戻す
				file.clsXmlFileMgrServiceObj = this;
				file.newFile();

			}
			if(iApiType == XmlFileMgrService.JsonUserEditFile) {
				XmlFileEditHelper.outputLog("JsonUserEditFile 1");
				//直接内容ありのJSONを戻す
				file.clsXmlFileMgrServiceObj = this;
				JSONObject jsonIn = getJsonInData();
				file.fileToJsonData(jsonIn);
				XmlFileEditHelper.outputLog("JsonUserEditFile 2");

			}
			if(iApiType == XmlFileMgrService.JsonUserDelFile) {
				//ファイル直接削除
				JSONObject jsonIn = getJsonInData();
				file.clsXmlFileMgrServiceObj = this;
				String strPath = file.getFileNamePre(iFileType);
				strPath = strPath + jsonIn.getString("xmlext_filename");

				File fileT = new File(strPath);
				fileT.delete();
				fileT = new File(strPath + ".new");
				fileT.delete();
				fileT = new File(strPath + ".tmp");
				fileT.delete();


				//// 訓練プラン外部データディレクトリ上のファイルを削除
				//　対象ファイル名を取得
				String strPath2 = strPath.replace(File.separator, "/");
				int idx = strPath2.lastIndexOf("/");
				String xmlFileName = strPath2.substring(idx + 1);

				// パスを取得
				String trainingmeteoDataDirPath = FileUtil.getUploadTrainingPath(application, Long.parseLong(file.getLocalGovID()));

				// 絶対パスを取得
				String trainingmeteoDataDirPathAbs = trainingmeteoDataDirPath + "/" + xmlFileName;
				trainingmeteoDataDirPathAbs = StringUtil.replaceAll("//", "/", trainingmeteoDataDirPathAbs);
				trainingmeteoDataDirPathAbs = StringUtil.replaceAll("/", File.separator, trainingmeteoDataDirPathAbs);

				if(trainingmeteoDataDirPath.indexOf("/upload") != -1){
					trainingmeteoDataDirPath = trainingmeteoDataDirPath.substring(trainingmeteoDataDirPath.indexOf("/upload") + 7, trainingmeteoDataDirPath.length());
				}else{
					if(! File.separator.equals("/")){
						//windows環境だと、uploadよりも上流のpathの区切り文字が「\」になるのでupload以下の文字列を切り出す
						trainingmeteoDataDirPath = "/"+trainingmeteoDataDirPath.substring(trainingmeteoDataDirPath.indexOf("upload/"));
					}
				}
				trainingmeteoDataDirPath = trainingmeteoDataDirPath + "/" + xmlFileName;
				trainingmeteoDataDirPath = StringUtil.replaceAll("//", "/", trainingmeteoDataDirPath);

				// 訓練プラン外部データのレコード削除
				List<TrainingplanData> trainingplanDataList = trainingplanDataService.findByLocalgovinfoId(Long.parseLong(file.getLocalGovID()));
				if(trainingplanDataList != null && trainingplanDataList.size() > 0){
					for(TrainingplanData trainingplanData : trainingplanDataList){
						BeanMap conditionsSubSub = new BeanMap();
						conditionsSubSub.put(trainingmeteoData().trainingplandataid().toString(),trainingplanData.id);
						conditionsSubSub.put(trainingmeteoData().meteotypeid().toString(), Integer.toString(iFileType));
						List<TrainingmeteoData> trainingmeteoDataList = trainingmeteoDataService.findByCondition(conditionsSubSub);
						if(trainingmeteoDataList != null && trainingmeteoDataList.size() > 0){
							for(TrainingmeteoData trainingmeteoData :trainingmeteoDataList){
								if(trainingmeteoData.meteourl != null && trainingmeteoData.meteourl.length() > 0){
									if(trainingmeteoData.meteourl.equals(trainingmeteoDataDirPath)){
										trainingmeteoData.deleted = Boolean.TRUE;
										trainingmeteoDataService.update(trainingmeteoData);
									}
								}
							}
						}
					}
				}
				// 訓練プラン外部データのXMLファイル削除
				File trainingmeteoDataDirPathAbsFile = new File(trainingmeteoDataDirPathAbs);
				if(trainingmeteoDataDirPathAbsFile.exists()){
					trainingmeteoDataDirPathAbsFile.delete();
				}
			}
			if(iApiType == XmlFileMgrService.JsonUserSaveFile) {
				//HttpServletRequest request = null;
				JSONObject jsonIn = getJsonInData();
				String strFileNameT = request.getParameter("filename");
				String strOrgFileNameT = request.getParameter("orgfilename");

//				strFileNameT = new String(strFileNameT.getBytes("ISO-8859-1"),"UTF-8");
//				strOrgFileNameT = new String(strOrgFileNameT.getBytes("ISO-8859-1"),"UTF-8");

				XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strFileNameT " + strFileNameT);
				XmlFileEditHelper.outputLog("jsonDataToFile 2=copy strOrgFileNameT " + strOrgFileNameT);
				//ファイルSave
				file.clsXmlFileMgrServiceObj = this;
				file.jsonDataToFile(jsonIn, strFileNameT, strOrgFileNameT);

				//JSONObject json = new JSONObject();
				//json.put("errcode", "0");
				//json.put("errmsg", "");
				//responseService.responseJson(json);

			}
			if(iApiType == XmlFileMgrService.JsonUserAddButton)
			{

				String strLineUID = request.getParameter("lineuid");
				String strFileNameT = request.getParameter("filename");

//				strFileNameT = new String(strFileNameT.getBytes("ISO-8859-1"),"UTF-8");

				XmlFileEditHelper.outputLog("newEditDelFile strLineUID=" + strLineUID);
				XmlFileEditHelper.outputLog("newEditDelFile strFileNameT=" + strFileNameT);

				file.clsXmlFileMgrServiceObj = this;

				file.addButton(strLineUID, strFileNameT);
			}
			if(iApiType == XmlFileMgrService.JsonUserDelButton)
			{

				String strLineUID = request.getParameter("lineuid");
				String strFileNameT = request.getParameter("filename");

//				strFileNameT = new String(strFileNameT.getBytes("ISO-8859-1"),"UTF-8");

				XmlFileEditHelper.outputLog("newEditDelFile strLineUID=" + strLineUID);
				XmlFileEditHelper.outputLog("newEditDelFile strFileNameT=" + strFileNameT);

				file.clsXmlFileMgrServiceObj = this;
				file.delButton(strLineUID, strFileNameT);
			}

		} catch (Exception e){

			XmlFileEditHelper.outputLog("Xml Exception-----------------1");

			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * DemoInfoServiceのインスタンスをセットする
	 * @param demoInfoService
	 */
	public void setTrainingplanDataServiceInstance(TrainingplanDataService trainingplanDataService){
		this.trainingplanDataService = trainingplanDataService;
	}
	/**
	 * DemoInfoServiceのインスタンスをセットする
	 * @param demoInfoService
	 */
	public void setTrainingmeteoDataServiceInstance(TrainingmeteoDataService trainingmeteoDataService){
		this.trainingmeteoDataService = trainingmeteoDataService;
	}
	/**
	 * DemoInfoServiceのインスタンスをセットする
	 * @param demoInfoService
	 */
	public void setApplicationInstance(ServletContext application){
		this.application = application;
	}
}
