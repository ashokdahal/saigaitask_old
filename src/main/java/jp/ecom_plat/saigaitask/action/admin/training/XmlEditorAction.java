/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.training;

import static jp.ecom_plat.saigaitask.entity.Names.*;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.action.admin.AbstractAdminAction;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.TrainingmeteoData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.form.admin.setupper.GroupMenuForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutasktypeInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;
import jp.ecom_plat.saigaitask.service.db.TrainingmeteoDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingplanDataService;
import jp.ecom_plat.saigaitask.service.training.XmlFileDbHelper;
import jp.ecom_plat.saigaitask.service.training.XmlFileEditHelper;
import jp.ecom_plat.saigaitask.service.training.XmlFileMgrService;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

import org.apache.commons.lang.math.NumberUtils;
import org.jdom2.Namespace;
import org.seasar.framework.beans.util.BeanMap;
import org.springframework.web.bind.annotation.ResponseBody;



@jp.ecom_plat.saigaitask.action.RequestScopeController
public class XmlEditorAction extends AbstractAdminAction<XmlEditorAction> {

	@Resource protected GroupInfoService groupInfoService;
	@Resource protected MenuprocessInfoService menuprocessInfoService;
	@Resource protected MenutaskInfoService menutaskInfoService;
	@Resource protected MenutasktypeInfoService menutasktypeInfoService;
	@Resource protected MenuInfoService menuInfoService;
	@Resource protected MenutaskmenuInfoService menutaskmenuInfoService;
	@Resource protected TrainingplanDataService trainingplanDataService;
	@Resource protected TrainingmeteoDataService trainingmeteoDataService;
	@Resource protected MeteorequestInfoService meteorequestInfoService;
	@Resource protected DisasterMasterService disasterMasterService;
	//@Resource protected LocalgovInfoService localgovInfoService;

    /** 災害情報リスト */
	public List<DisasterMaster> disasterItems;

	/** XmlEditor */
	@Resource
	private ResponseService responseService;


	public List<GroupInfo> groupInfoItems;
	public List<MenuloginInfo> menuloginInfoItems = new ArrayList<MenuloginInfo>();

	//public Map<Long, List<MenuprocessInfo>> menuprocessInfoMap = new HashMap<Long, List<MenuprocessInfo>>();
	public List<String> menuprocessNames = new ArrayList<String>();
	public Map<String, List<String>> menutaskNames = new HashMap<String, List<String>>();
	public Map<String, String> menutasktypeNames = new HashMap<String, String>();
	public Map<String, List<MenuInfo>> menuNames = new HashMap<String, List<MenuInfo>>();
	public Map<Long, Map<Long, MenutaskmenuInfo>> groupMenuMap = new HashMap<Long, Map<Long, MenutaskmenuInfo>>();

	/**
	 * xml editor
	 */
	public static final Namespace NAMESPACE_JMX = Namespace.getNamespace("jmx", "http://xml.kishou.go.jp/jmaxml1/");
	public static final Namespace NAMESPACE_JMX_IB = Namespace.getNamespace("jmx_ib", "http://xml.kishou.go.jp/jmaxml1/informationBasis1/");
	public static final Namespace NAMESPACE_JMX_SEIS = Namespace.getNamespace("jmx_seis", "http://xml.kishou.go.jp/jmaxml1/body/seismology1/");
	public static final Namespace NAMESPACE_XSLS = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");

	public List<Map<String, String>> tableData;

//	private static XPathFactory xFactory = XPathFactory.instance();
//	private Document docXml;

	public boolean localgovinfoidSelected;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		if(disasterItems != null) model.put("disasterItems", disasterItems);
		if(groupInfoItems != null) model.put("groupInfoItems", groupInfoItems);
		if(menuloginInfoItems != null) model.put("menuloginInfoItems", menuloginInfoItems);
		if(menuprocessNames != null) model.put("menuprocessNames", menuprocessNames);
		if(menutaskNames != null) model.put("menutaskNames", menutaskNames);
		if(menutasktypeNames != null) model.put("menutasktypeNames", menutasktypeNames);
		if(menuNames != null) model.put("menuNames", menuNames);
		if(groupMenuMap != null) model.put("groupMenuMap", groupMenuMap);
		if(tableData != null) model.put("tableData", tableData);
		if(loginDataDto.getLocalgovinfoid() == Constants.ADMIN_LOCALGOVINFOID){
			model.put("localgovinfoidSelected", false);
		}else{
			model.put("localgovinfoidSelected", true);
		}

	}

    /**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor"})
	public String index(Map<String,Object>model) {
		XmlFileEditHelper.outputLog("xmleditor index-------------------start");
		setupModel(model);
		return "/admin/training/xmlEditor/index";
	}


	/**
	 * ページを表示する.
	 * @return 表示ページ
	 * @throws Exception
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/ajaxxml"})
	public String ajaxxml(Map<String,Object>model) throws Exception {
		try {

			XmlFileEditHelper.outputLog("ajax-------------------start");
			XmlFileMgrService s = new XmlFileMgrService();
			s.setTrainingplanDataServiceInstance(trainingplanDataService);
			s.setTrainingmeteoDataServiceInstance(trainingmeteoDataService);
			s.setApplicationInstance(application);

			s.setRp(responseService);
			s.entryAjax(request);
			XmlFileEditHelper.outputLog("ajax-------------------end");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		setupModel(model);
		return null;
	}
	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/content"})
	public String content(Map<String,Object>model) {
		if(loginDataDto.getLocalgovinfoid() == Constants.ADMIN_LOCALGOVINFOID){
			localgovinfoidSelected = false;
		}else{
			localgovinfoidSelected = true;
		}
		setupModel(model);
		return "/admin/training/xmlEditor/content";
	}


	/**
	 * XML編集
	 * @return
	 * @throws Exception
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmltable"})
	public String xmltable(Map<String,Object>model) throws Exception {
		try {

			XmlFileEditHelper.outputLog("xmltable index-------------------start");
			//utXmltableDemoData();
			XmlFileMgrService s = new XmlFileMgrService();
			s.setTrainingplanDataServiceInstance(trainingplanDataService);
			s.setTrainingmeteoDataServiceInstance(trainingmeteoDataService);
			s.setApplicationInstance(application);
			s.setRp(responseService);
			s.entryJsp(request);
			XmlFileEditHelper.outputLog("xmltable end-------------------start");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		tableData = (List<Map<String, String>>) request.getAttribute("tableData");
		setupModel(model);
		return "/admin/training/xmlEditor/xmltable";
	}

	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/update"})
	public String update(Map<String,Object>model) {
		setupModel(model);
		return "index";
	}

	protected void addMenu(MenuloginInfo menulogin, List<MenuprocessInfo> processList, List<Long> menuidList, Map<Long, Integer> disporders) {
		for (Long menuid : menuidList) {
			MenuInfo menu = menuInfoService.findById(menuid);

			//存在チェック
			boolean exist = false;
			MenutaskInfo mytask = null;
			for (MenuprocessInfo process : processList) {
				for (MenutaskInfo task : process.menutaskInfos) {
					if (!task.menutasktypeinfoid.equals(menu.menutasktypeinfoid))
						continue;

					mytask = task;
					for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
						if (menu.id.equals(tmenu.menuinfoid)) {
							exist = true;
							break;
						}
					}
					if (exist) break;
				}
				if (exist) break;
			}

			//新規
			if (!exist) {
				//プロセス、タスクがない
				if (mytask == null) {
					//プロセス有無確認
					MenutaskInfo task2 = menutaskInfoService.findBymenutasktypeinfoid(menu.menutasktypeinfoid);
					MenuprocessInfo process2 = menuprocessInfoService.findById(task2.menuprocessinfoid);

					MenuprocessInfo pros = null;
					for (MenuprocessInfo process : processList) {
						if (process.name.equals(process2.name)) {
							pros = process;
							break;
						}
					}

					//プロセスがなければ追加
					if (pros == null) {
						pros = new MenuprocessInfo();
						pros.disporder = process2.disporder;
						pros.menulogininfoid = menulogin.id;
						pros.name = process2.name;
						pros.valid = true;
						pros.visible = true;
						pros.deleted = false;
						menuprocessInfoService.insert(pros);
						processList.add(pros);
					}

					//タスク追加
					mytask = new MenutaskInfo();
					mytask.disporder = task2.disporder;
					mytask.menuprocessinfoid = pros.id;
					mytask.menutasktypeinfoid = task2.menutasktypeinfoid;
					mytask.name = task2.name;
					mytask.valid = true;
					mytask.visible = true;
					mytask.deleted = false;
					menutaskInfoService.insert(mytask);
					if (pros.menutaskInfos == null) pros.menutaskInfos = new ArrayList<MenutaskInfo>();
					pros.menutaskInfos.add(mytask);
				}

				//タスクへメニューの追加
				MenutaskmenuInfo tmenu = new MenutaskmenuInfo();
				tmenu.menuinfoid = menuid;
				tmenu.menutaskinfoid = mytask.id;
				tmenu.disporder = 1;
				if(disporders.get(menuid)!=null) tmenu.disporder = disporders.get(menuid);
				menutaskmenuInfoService.insert(tmenu);
				if (mytask.menutaskmenuInfos == null) mytask.menutaskmenuInfos = new ArrayList<MenutaskmenuInfo>();
				mytask.menutaskmenuInfos.add(tmenu);
			}
		}
	}

	protected void updateMenu(MenuloginInfo menulogin, List<MenuprocessInfo> processList, List<Long> menuidList, Map<Long, Integer> disporders) {
		for (MenuprocessInfo process : processList) {
			for (MenutaskInfo task : process.menutaskInfos) {
				for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
					if (!menuidList.contains(tmenu.menuinfoid))
						menutaskmenuInfoService.delete(tmenu);
					else {
						if(disporders.get(tmenu.menuinfoid)!=null) {
							int disporder = disporders.get(tmenu.menuinfoid);
							//表示順が変わった
							if (tmenu.disporder != disporder) {
								tmenu.disporder = disporder;
								menutaskmenuInfoService.update(tmenu);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * タスクメニューをすべて削除する.
	 * @param menulogin
	 * @param processList
	 */
	protected void deleteAll(MenuloginInfo menulogin, List<MenuprocessInfo> processList) {
		for (MenuprocessInfo process : processList) {
			for (MenutaskInfo task : process.menutaskInfos) {
				for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
					menutaskmenuInfoService.delete(tmenu);
				}
			}
		}
	}


	/**
	 * 訓練プラン外部データを取得し、リストをAJAX形式で返却する.
	 * @return 表示ページ
	 * @throws Exception
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/ajaxGetTrainingmeteoData"})
	public String ajaxGetTrainingmeteoData(Map<String,Object>model) throws Exception {
		final String RETCODE = "retcode";
		final String INNERHTML = "innerHtml";
		final String RETCODE_SUCCESS = "0";
		final String RETCODE_NO_TRAININGPLAN = "1";
		final String RETCODE_NO_METEOREQUESTINFO = "2";

		try {
			StringBuffer htmlBuf = new StringBuffer();

			String localgovInfoidStr = (String)request.getParameter("localgovinfoid");
			if(localgovInfoidStr == null || localgovInfoidStr.length() <= 0){
				localgovInfoidStr = Long.toString(loginDataDto.getLocalgovinfoid());
			}
			String meteotypeidStr = (String)request.getParameter("meteotypeid");
			String meteotypeName = (String)request.getParameter("meteotypename");
			String xmlFileName = (String)request.getParameter("xmlFileName");
			String fileType = (String)request.getParameter("fileType");

			// 保存したXMLファイルのパスを取得
			XmlFileMgrService xmlFileMgrService = new XmlFileMgrService();
			xmlFileMgrService.initProp();
			String strXml = xmlFileMgrService.getPropData().strMETEOXMLEDIT_PATH;
			String strFolder;
			XmlFileDbHelper.MeteotypeMasterServiceExt ext = new XmlFileDbHelper().new MeteotypeMasterServiceExt();
			ArrayList<Map<String,String>> oType = ext.getComboboxDataCodeName();
			String strTypeFolder = ext.getTypeByID(""+fileType, oType);
			strFolder = localgovInfoidStr + "/" + strTypeFolder;

			String xmlFilePath = strXml + "/" + strFolder + "/" + xmlFileName;
			xmlFilePath = StringUtil.replaceAll("//", "/", xmlFilePath);


			JSONObject jsonObject = new JSONObject();

			BeanMap conditions = new BeanMap();
			conditions.put(meteorequestInfo().localgovinfoid().toString(), Long.parseLong(localgovInfoidStr));
			conditions.put(meteorequestInfo().meteotypeid().toString(), Integer.parseInt(meteotypeidStr));
			List<MeteorequestInfo> meteorequestInfoList = meteorequestInfoService.findByCondition(conditions);

			if(meteorequestInfoList == null || meteorequestInfoList.size() <= 0){
				// 気象情報種別なしエラー
				jsonObject.put(RETCODE, RETCODE_NO_METEOREQUESTINFO);
				htmlBuf.append(lang.__("<label>"));
				htmlBuf.append(lang.__("No meteor info type to be edited. Configure again after adding {0} meteor info type to meteor info table.", meteotypeName));
				htmlBuf.append("</label><br/>");
			}else{

				Map<TrainingplanData, List<TrainingmeteoData>> trainingDatas = new LinkedHashMap<TrainingplanData, List<TrainingmeteoData>>();
				BeanMap conditionsSub = new BeanMap();
				conditionsSub.put(trainingplanData().localgovinfoid().toString(),Long.parseLong(localgovInfoidStr));
				conditionsSub.put(trainingplanData().deleted().toString(), Boolean.FALSE);

				List<TrainingplanData> trainingplanDataSub = trainingplanDataService.findByCondition(conditionsSub);

				if(trainingplanDataSub != null && trainingplanDataSub.size() > 0){
					for(TrainingplanData trainingplanData : trainingplanDataSub){
						BeanMap conditionsSubSub = new BeanMap();
						conditionsSubSub.put(trainingmeteoData().trainingplandataid().toString(),trainingplanData.id);
						conditionsSubSub.put(trainingmeteoData().meteotypeid().toString(),meteotypeidStr);
						conditionsSubSub.put(trainingmeteoData().deleted().toString(), Boolean.FALSE);

						List<TrainingmeteoData> trainingmeteoDataList = trainingmeteoDataService.findByCondition(conditionsSubSub);

						if(trainingmeteoDataList != null && trainingmeteoDataList.size() > 0){
							trainingDatas.put(trainingplanData, trainingmeteoDataList);
						}
					}
				}

				if(trainingDatas.size() <= 0){
					// 訓練プラン外部データなしエラー
					jsonObject.put(RETCODE, RETCODE_NO_TRAININGPLAN);
					htmlBuf.append("<label>");
					htmlBuf.append(lang.__("Drill plan external data of {0} is not created.", meteotypeName));
					htmlBuf.append("</label><br/>");
				}else{
					jsonObject.put(RETCODE, RETCODE_SUCCESS);

					// 災害種別マスタリスト作成
					List<DisasterMaster> disasterMasterList = disasterMasterService.findAll();
					Map<Integer,String> disasterMasterMap = new HashMap<Integer, String>();
					for(DisasterMaster disasterMaster: disasterMasterList){
						disasterMasterMap.put(disasterMaster.id, disasterMaster.name);
					}

					// 返却データ作成
					htmlBuf.append("<label>");
					htmlBuf.append(lang.__("Select drill plan external data to change trigger URL."));
					htmlBuf.append("</label><br/>");
					for(Map.Entry<TrainingplanData, List<TrainingmeteoData>> e : trainingDatas.entrySet()){
						TrainingplanData trainingplanData = e.getKey();

						//String disasterName = disasterMasterMap.get(e.getKey().disasterid);

						htmlBuf.append("<H1>\n");
						//htmlBuf.append(lang.__("Disaster type") + "：");
						//htmlBuf.append(disasterName);
						htmlBuf.append(",");
						htmlBuf.append(" " + lang.__("Training name") + "：");
						htmlBuf.append(trainingplanData.name);
						htmlBuf.append("</H1>\n");
						htmlBuf.append("<INPUT type='hidden' id='select_trainingmeteoData_xmlFilePath_hidden' value='");
						htmlBuf.append(xmlFilePath);
						htmlBuf.append("'>");
						htmlBuf.append("<TABLE id='dialog_selectdemoinfo_table' border='1' class='form'>\n");
						htmlBuf.append("<TR>\n");
						htmlBuf.append("<TH>&nbsp;</TH>\n");
						htmlBuf.append("<TH>ID</TH>\n");
						htmlBuf.append("<TH>" + lang.__("Weather info type") + "</TH>\n");
						htmlBuf.append("<TH>" + lang.__("XML registration name") + "</TH>\n");
						htmlBuf.append("<TH>" + lang.__("XML file") + "</TH>\n");
						htmlBuf.append("<TH>" + lang.__("Overview") + "</TH>\n");
						htmlBuf.append("<TH>" + lang.__("Update time") + "</TH>\n");
						htmlBuf.append("</TR>\n");

						for(TrainingmeteoData trainingmeteoData: e.getValue()){

							htmlBuf.append("<TR>\n");
							htmlBuf.append("<TD>");
							htmlBuf.append("<INPUT type='radio' name ='select_trainingmeteoData_radio' id='");
							htmlBuf.append("select_trainingmeteoData_id_");
							htmlBuf.append(trainingmeteoData.id);
							htmlBuf.append("' value='");
							htmlBuf.append(trainingmeteoData.id);
							htmlBuf.append("'>");
							htmlBuf.append("</TD>\n");

							htmlBuf.append("<TD>");
							htmlBuf.append(trainingmeteoData.id);
							htmlBuf.append("</TD>\n");

							htmlBuf.append("<TD>");
							htmlBuf.append(trainingmeteoData.meteotypeid + ":" + meteotypeName);
							htmlBuf.append("</TD>\n");

							htmlBuf.append("<TD>");
							htmlBuf.append(trainingmeteoData.name);
							htmlBuf.append("</TD>\n");

							htmlBuf.append("<TD>");
							htmlBuf.append(trainingmeteoData.meteourl);
							htmlBuf.append("</TD>\n");

							htmlBuf.append("<TD>");
							htmlBuf.append(trainingmeteoData.note);
							htmlBuf.append("</TD>\n");

							htmlBuf.append("<TD>");
							htmlBuf.append(new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM<!--3-->"), lang.getLocale()).format(trainingmeteoData.updatetime));
							htmlBuf.append("</TD>\n");

							htmlBuf.append("</TR>\n");
						}
						htmlBuf.append("</TABLE>\n");
					}
				}
			}
			jsonObject.put(INNERHTML, htmlBuf.toString());

			responseService.responseJson(jsonObject);

			setupModel(model);
			return jsonObject.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

	}

	/**
	 * 訓練プラン外部データを更新する
	 * @return 表示ページ
	 * @throws Exception
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/ajaxUpdateTtrainingmeteoData"})
	public String ajaxUpdateTtrainingmeteoData(Map<String,Object>model) throws Exception {
		final String RETMSG = "retmessage";

		JSONObject jsonObject = new JSONObject();

		// リクエストパラメータからXMLファイル名を取得
//		String xmlFilePath = (String)request.getParameter("xmlFilePath");
		String xmlFilePath = URLDecoder.decode((String)request.getParameter("xmlFilePath"),"UTF-8");

		if(xmlFilePath == null || xmlFilePath.length() < 0){
			jsonObject.put(RETMSG, lang.__("Failed to restore saved XML file name."));
		}else{
			int idx = xmlFilePath.lastIndexOf("/");
			String xmlFileName = xmlFilePath.substring(idx + 1);


			String localgovInfoidStr = (String)request.getParameter("localgovinfoid");
			if(localgovInfoidStr == null || localgovInfoidStr.length() <= 0){
				localgovInfoidStr = Long.toString(loginDataDto.getLocalgovinfoid());
			}
			if(NumberUtils.isNumber(localgovInfoidStr)){
				LocalgovInfo localgovInfo = localgovInfoService.findById(Long.parseLong(localgovInfoidStr));
				if(localgovInfo == null){
					jsonObject.put(RETMSG, lang.__("Failed to get municipality info."));
				}else{
					String trainingplanDataIdStr = (String)request.getParameter("trainingplanDataId");
					if(NumberUtils.isNumber(trainingplanDataIdStr)){
						TrainingmeteoData trainingmeteoData = trainingmeteoDataService.findById(Long.parseLong(trainingplanDataIdStr));
						if(trainingmeteoData == null){
							jsonObject.put(RETMSG, lang.__("Failed to get drill plan external data."));
						}else{
							XmlFileMgrService xmlFileMgrService = new XmlFileMgrService();
							xmlFileMgrService.initProp();

							// 訓練プラン外部データのXML保存パスを取得
							String trainingmeteoDataDirPath = FileUtil.getUploadTrainingPath(application, localgovInfo.id);
							String trainingmeteoDataDirPathSaved = trainingmeteoDataDirPath;
							if(trainingmeteoDataDirPath.indexOf("/upload") != -1){
								trainingmeteoDataDirPath = trainingmeteoDataDirPath.substring(trainingmeteoDataDirPath.indexOf("/upload") + 7, trainingmeteoDataDirPath.length());
							}else{
								if(! File.separator.equals("/")){
									//windows環境だと、uploadよりも上流のpathの区切り文字が「\」になるのでupload以下の文字列を切り出す
									trainingmeteoDataDirPath = "/"+trainingmeteoDataDirPath.substring(trainingmeteoDataDirPath.indexOf("upload/") + 7);
								}
							}

							StringBuffer sbuf = new StringBuffer();
							sbuf.append(trainingmeteoDataDirPath);
							sbuf.append(xmlFileName);
							trainingmeteoData.meteourl = sbuf.toString();
							trainingmeteoData.updatetime = new Timestamp(System.currentTimeMillis());
							try{
								int updCnt = trainingmeteoDataService.update(trainingmeteoData);
								if(updCnt != 1){
									jsonObject.put(RETMSG, lang.__("Failed to update drill plan external data.<!--2-->"));
								}else{
									String contextPath = request.getSession().getServletContext().getRealPath("");
									String xmlFilePathAbs = contextPath + "/WEB-INF/" + xmlFilePath;
									xmlFilePathAbs = StringUtil.replaceAll("//", "/", xmlFilePathAbs);
									xmlFilePathAbs = StringUtil.replaceAll("/", File.separator, xmlFilePathAbs);

									String trainingmeteoDataDirPathAbs = trainingmeteoDataDirPathSaved + "/" + xmlFileName;
									trainingmeteoDataDirPathAbs = StringUtil.replaceAll("//", "/", trainingmeteoDataDirPathAbs);
									trainingmeteoDataDirPathAbs = StringUtil.replaceAll("/", File.separator, trainingmeteoDataDirPathAbs);

									File trainingmeteoDataDirFile = new File(trainingmeteoDataDirPathSaved);
									if(! trainingmeteoDataDirFile.exists()){
										trainingmeteoDataDirFile.mkdirs();
									}

									// ファイルのコピー
									FileUtil.fileCopyByPath(xmlFilePathAbs, trainingmeteoDataDirPathAbs);
								}
							}catch(Exception e){
								jsonObject.put(RETMSG, lang.__("Failed to update drill plan external data."));
								logger.error(e.getMessage(), e);
							}
						}
					}else{
						jsonObject.put(RETMSG, lang.__("Drill plan external data ID incorrect.") + "：" + localgovInfoidStr);
					}
				}
			}else{
				jsonObject.put(RETMSG, lang.__("Municipality ID is incorrect.") + "：" + localgovInfoidStr);
			}
		}
		if(jsonObject.size() <= 0){
			jsonObject.put(RETMSG, lang.__("Updated drill plan external data."));
		}
		responseService.responseJson(jsonObject);

		setupModel(model);
		return jsonObject.toString();
	}

	/**
	 * 削除対象の訓練プラン外部データを検索し、結果をメッセージで返す
	 * @return 表示ページ
	 * @throws Exception
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/ajaxGetDelTrainingmeteoData"})
	public String ajaxGetDelTrainingmeteoData(Map<String,Object>model) throws Exception {
		final String RETMSG = "retmessage";
		StringBuffer trainingmeteoDataSbuf = new StringBuffer();
		JSONObject jsonObject = new JSONObject();

		String localgovInfoidStr = (String)request.getParameter("localgovinfoid");
		String meteotypeidStr = (String)request.getParameter("meteotypeid");
		String xmlFileName = (String)request.getParameter("xmlFileName");
		if(localgovInfoidStr == null || localgovInfoidStr.length() <= 0){
			localgovInfoidStr = Long.toString(loginDataDto.getLocalgovinfoid());
		}

		if(NumberUtils.isNumber(localgovInfoidStr) && NumberUtils.isNumber(meteotypeidStr) && (xmlFileName != null && xmlFileName.length() > 0)){
			BeanMap conditions = new BeanMap();
			conditions.put(trainingplanData().localgovinfoid().toString(), Long.parseLong(localgovInfoidStr));
			conditions.put(trainingplanData().deleted().toString(), Boolean.FALSE);
			List<TrainingplanData> trainingplanDataList = trainingplanDataService.findByCondition(conditions);

			for(TrainingplanData trainingplanData : trainingplanDataList){
				BeanMap conditionsSub = new BeanMap();
				conditionsSub.put(trainingmeteoData().trainingplandataid().toString(),trainingplanData.id);
				conditionsSub.put(trainingmeteoData().meteotypeid().toString(),Integer.parseInt(meteotypeidStr));
				conditionsSub.put(trainingmeteoData().deleted().toString(),Boolean.FALSE);
				List<TrainingmeteoData> trainingmeteoDataList = trainingmeteoDataService.findByCondition(conditionsSub);
				for(TrainingmeteoData trainingmeteoData : trainingmeteoDataList){
					String url = trainingmeteoData.meteourl;
					if(url != null && url.length() > 0){
						if(url.indexOf(xmlFileName) >= 0){
							trainingmeteoDataSbuf.append(lang.__("Training name") + "=");
							trainingmeteoDataSbuf.append(trainingplanData.name);
							trainingmeteoDataSbuf.append(", ");
							trainingmeteoDataSbuf.append("ID=");
							trainingmeteoDataSbuf.append(trainingmeteoData.id);
							trainingmeteoDataSbuf.append(", ");
							trainingmeteoDataSbuf.append(lang.__("XML registration name") + "=");
							trainingmeteoDataSbuf.append(trainingmeteoData.name);
							trainingmeteoDataSbuf.append("\n");
						}
					}
				}
			}
		}

		if(trainingmeteoDataSbuf.length() > 0){
			jsonObject.put(RETMSG, "\n" + lang.__("Drill plan external data shown below will be deleted.") + "\n" + trainingmeteoDataSbuf.toString());
		}else{
			jsonObject.put(RETMSG,"");
		}
		responseService.responseJson(jsonObject);

		setupModel(model);
		return jsonObject.toString();
	}

	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/filelistdlg"})
	public String filelistdlg(Map<String,Object>model) throws Exception {
		setupModel(model);
		return "/admin/training/xmlEditor/filelistdlg";
	}

	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/xmlEditor/selecttrainingmeteodatadlg"})
	public String selecttrainingmeteodatadlg(Map<String,Object>model) throws Exception {
		setupModel(model);
		return "/admin/training/xmlEditor/selecttrainingmeteodatadlg";
	}

}
