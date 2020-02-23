/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import static jp.ecom_plat.saigaitask.util.Constants.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AlarmdefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.FilterInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapbaselayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapkmllayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MapreferencelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenumapInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutypeMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteolayerInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.PagebuttonMaster;
import jp.ecom_plat.saigaitask.entity.db.PagemenubuttonInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablerowstyleInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.names.AlarmdefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerattrInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenumapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutasktypeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.form.admin.setupper.MenuWizardForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.AlarmdefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternalmapdataInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternaltabledataInfoService;
import jp.ecom_plat.saigaitask.service.db.FilterInfoService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MapbaselayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapkmllayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MaplayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MaplayerattrInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MapreferencelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenumapInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutasktypeInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutypeMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteolayerInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.PagebuttonMasterService;
import jp.ecom_plat.saigaitask.service.db.PagemenubuttonInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablerowstyleInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.EcommapDataGetService;
import jp.ecom_plat.saigaitask.service.excellist.ExcellistService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.FileUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.BeanUtil;
import org.seasar.framework.util.StringUtil;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class MenuWizardAction extends AbstractSetupperAction{

	protected MenuWizardForm menuWizardForm;

	@Resource
	protected ResponseService responseService;

	@Resource
	protected JdbcManager jdbcManager;

	@Resource
	protected MenuloginInfoService menuloginInfoService;
	@Resource
	protected MenuprocessInfoService menuprocessInfoService;
	@Resource
	protected MenutaskInfoService menutaskInfoService;
	@Resource
	protected MenutasktypeInfoService menutasktypeInfoService;
	@Resource
	protected MenutypeMasterService menutypeMasterService;
	@Resource
	protected MenutaskmenuInfoService menutaskmenuInfoService;
	@Resource
	protected MenuInfoService menuInfoService;

	@Resource
	protected PagemenubuttonInfoService     pagemenubuttonInfoService;
	@Resource
	protected MapmasterInfoService          mapmasterInfoService;
	@Resource
	protected TablemasterInfoService        tablemasterInfoService;
	@Resource
	protected MaplayerInfoService           maplayerInfoService;
	@Resource
	protected MaplayerattrInfoService       maplayerattrInfoService;
	@Resource
	protected MapbaselayerInfoService       mapbaselayerInfoService;
	@Resource
	protected MenutableInfoService          menutableInfoService;
	@Resource
	protected TablelistcolumnInfoService    tablelistcolumnInfoService;
	@Resource
	protected TablerowstyleInfoService      tablerowstyleInfoService;
	@Resource
	protected SummarylistcolumnInfoService  summarylistcolumnInfoService;
	@Resource
	protected MeteolayerInfoService         meteolayerInfoService;
	@Resource
	protected ExternalmapdataInfoService    externalmapdataInfoService;
	@Resource
	protected ExternaltabledataInfoService  externaltabledataInfoService;
	@Resource
	protected MenumapInfoService            menumapInfoService;
	@Resource
	protected NoticedefaultInfoService      noticedefaultInfoService;
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected AlarmdefaultgroupInfoService  alarmdefaultgroupInfoService;
	@Resource
	protected FilterInfoService             filterInfoService;
	@Resource
	protected MapreferencelayerInfoService  mapreferencelayerInfoService;
	@Resource
	protected MapkmllayerInfoService        mapkmllayerInfoService;

	@Resource
	protected PagebuttonMasterService pagebuttonMasterService;
	@Resource
	protected EcommapDataGetService ecommapDataGetService;

	@Resource
	private UserTransaction userTransaction;

    @Resource
    protected UnitInfoService unitInfoService;

    @Resource
    protected GroupInfoService groupInfoService;

	@Resource
	protected ExcellistService excellistService;

	public boolean isSystemAdmin;
	public boolean isAdmin;
	public boolean isUsual;
	public long guId;

	private static final int STEPM2 = -2;
	private static final int STEPM1 = -1;
	private static final int STEP0  = -0;
	private static final int STEP1  =  1;
	private static final int STEP2  =  2;
	private static final int STEP3  =  3;
	private static final int STEP4  =  4;
	private static final int STEP5  =  5;
	private static final int STEP6  =  6;
	private static final int STEP7  =  7;
	private static final int STEP8  =  8;
	private static final int STEP9  =  9;
	private static final int STEP10 = 10;
	private static final int STEP11 = 11;
	private static final int STEP12 = 12;
	private static final int ERRORSTEP = 99;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("isSystemAdmin", isSystemAdmin);
		model.put("isAdmin", isAdmin);
		model.put("isUsual", isUsual);
		model.put("guId", guId);

	}

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/menuWizard","/admin/setupper/menuWizard/index"})
	public String index(Map<String,Object>model,  @ModelAttribute MenuWizardForm menuWizardForm) {
		content(model, menuWizardForm);

		setupModel(model);
		return "/admin/setupper/menuWizard/index";
	}

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/menuWizard/content")
	public String content(Map<String,Object>model,  @ModelAttribute MenuWizardForm menuWizardForm) {

		// 管理権限を持つ班・課でログインしているか確認
		// システム管理者の場合
		if((!loginDataDto.isUsual()) && loginDataDto.getGroupid() <= 0) {
			isSystemAdmin = true;
			isAdmin = true;
		// 一般ユーザの場合
		}else{
			isSystemAdmin = false;
			if(loginDataDto.isUsual()){
				isUsual = true;
				if(loginDataDto.getUnitInfo().admin){
					isAdmin = true;
				}else{
					isAdmin = false;
				}
			}else{
				isUsual = false;
				if(loginDataDto.getGroupInfo().admin){
					isAdmin = true;
				}else{
					isAdmin = false;
				}
			}
		}

		setupModel(model);
		return "/admin/setupper/menuWizard/content";
	}

	/**
	 * 利用者画面からのウイザードページ遷移処理
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/menuWizard/userpage")
	public String userpage(Map<String,Object>model,  @ModelAttribute MenuWizardForm menuWizardForm){
		this.menuWizardForm = menuWizardForm;
		setupModel(model);
		return "/admin/setupper/menuWizard/userpage";

	}

	/**
	 * ウイザードページ遷移処理
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/menuWizard/wizard")
	@ResponseBody
	public String wizard(Map<String,Object>model){

		long localgovinfoid;
		long guId;
		boolean isUsual;
		// 平時／災害時判定
		if(loginDataDto.isUsual()){
			isUsual = true;
		}else{
			isUsual = false;
		}

		String fromJsonDataStr = request.getParameter("jsonData");
		JSONObject fromJsonData = JSONObject.fromObject(fromJsonDataStr);
		// 次に遷移するステップ
		int nextStep  = fromJsonData.getInt("nextStep");

		// 自治体IDと班または課IDのセット
		if(fromJsonData.containsKey("localgovinfoid")){
			localgovinfoid = fromJsonData.getLong("localgovinfoid");
		}else{
			localgovinfoid = loginDataDto.getLocalgovinfoid();
		}

		if(fromJsonData.containsKey("guid")){
			guId = fromJsonData.getLong("guid");
		}else{
			if(isUsual){
				guId = loginDataDto.getUnitid();
			}else{
				guId = loginDataDto.getGroupid();
			}
		}


		boolean adminAuth = true;
		JSONObject stepM2JsonData;
		if(fromJsonData.containsKey("stepM2Data")){
			stepM2JsonData = fromJsonData.getJSONObject("stepM2Data");

			// 管理権限のない一般ユーザでウイザードを開いた場合は管理権限ユーザ認証処理を行う
			if(nextStep == STEPM1){
				if(! stepM2JsonData.isEmpty()){
					String guName = stepM2JsonData.getString("guname");
					String guPass = stepM2JsonData.getString("gupass");

					if(isUsual){
						UnitInfo unitInfo;
						try{
				        	unitInfo = unitInfoService.findByLoginIdAndPasswordAndValid(
				        			unitInfoService.findByName(guName, guPass, loginDataDto.getLocalgovinfoid()).id, guPass);
						}catch(NullPointerException e){
							unitInfo = null;
						}

			        	if(unitInfo != null && unitInfo.admin){
			        		adminAuth = true;
			        		guId = loginDataDto.getUnitid();
			        	}else{
			        		adminAuth = false;
			        	}
					}else{
						GroupInfo groupInfo;
						try{
				        	groupInfo = groupInfoService.findByLoginIdAndPasswordAndValid(
				        			groupInfoService.findByName(guName, guPass, loginDataDto.getLocalgovinfoid()).id, guPass);
						}catch(NullPointerException e){
							groupInfo = null;
						}

			        	if(groupInfo != null && groupInfo.admin){
			        		adminAuth = true;
			    			guId = loginDataDto.getGroupid();
			        	}else{
			        		adminAuth = false;
			        	}
					}

					// 認証に失敗した場合は同じページを再表示
					if(!adminAuth){
						nextStep = STEPM2;
					}else{
						// 自治体選択画面はシステム管理者の場合のみ
						nextStep++;
					}
				}else{
					// 自治体選択画面はシステム管理者の場合のみ
					if(loginDataDto.getLocalgovinfoid() != Constants.ADMIN_LOCALGOVINFOID && guId > 0){
						nextStep++;
					}
				}
			}
		}else{
			if(loginDataDto.getLocalgovinfoid() != Constants.ADMIN_LOCALGOVINFOID){
				if(isUsual){
					guId = loginDataDto.getUnitid();
				}else{
					guId = loginDataDto.getGroupid();
				}
			}
		}


		// 自治体選択画面での班課確認
		if(nextStep == STEP0 ){
			if(fromJsonData.containsKey("stepM1Data")){
				JSONObject stepM1JsonData = fromJsonData.getJSONObject("stepM1Data");
				localgovinfoid = stepM1JsonData.getLong("localgovinfoid");
				guId = stepM1JsonData.getLong("guid");
			}else{
				localgovinfoid = loginDataDto.getLocalgovinfoid();
			}

			if(! fromJsonData.containsKey("localgovinfoid")){
				fromJsonData.put("localgovinfoid", localgovinfoid);
			}
			if(! fromJsonData.containsKey("guid")){
				fromJsonData.put("guid", guId);
			}
		}

		// 「戻る」ボタンで遷移した場合は遷移先ステップを戻す
		boolean isNext  = fromJsonData.getBoolean("isNext");
		if(!isNext){
			nextStep -= 2;
		}else{
			// 「次へ」または「完了」ボタンが押された場合は入力した項目をDBに反映
			if(! fromJsonData.getBoolean("isReload")){
				fromJsonData = dbUpdate(nextStep, fromJsonData, isUsual, localgovinfoid, guId);
			}
		}

		// ウイザード完了かどうか
		boolean isFinish  = fromJsonData.getBoolean("isFinish");

		JSONObject retValue;
		if(fromJsonData.containsKey("dbError")){
			retValue = createErrorContents(fromJsonData);
			JSONObject toJsonData = fromJsonData;
			toJsonData.put("nextStep", nextStep+1);
			retValue.put("jsonData", toJsonData);

		}else{
			retValue = createReturnContents(isFinish, nextStep, fromJsonData, isUsual, localgovinfoid, guId, adminAuth);
			JSONObject toJsonData = fromJsonData;
			toJsonData.put("nextStep", nextStep+1);
			retValue.put("jsonData", toJsonData);
		}

		try{
			responseService.responseJson(retValue);
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		setupModel(model);
		return null;
	}

	/**
	 * ウイザードステップページ作成
	 * @param nextStep
	 * @param baseData
	 * @param isUsual
	 * @param guId
	 * @return
	 */
	public JSONObject createReturnContents(boolean isFinish, int nextStep, JSONObject baseData, boolean isUsual, long localgovinfoid, long guId, boolean auth){
		JSONObject contentsJson = new JSONObject();

		if(!isFinish){
			Map<String, Boolean> buttonMap = new HashMap<String, Boolean>();
			contentsJson.put("title", createTitle(nextStep));
			contentsJson.put("description", createDescription(nextStep));
			contentsJson.put("contents", createContents(nextStep, baseData, isUsual, localgovinfoid, guId, buttonMap));
			for(Map.Entry<String, Boolean> e : buttonMap.entrySet()){
				contentsJson.put(e.getKey(), e.getValue());
			}

			contentsJson.put("helpList", createHelp(nextStep));
			contentsJson.put("filterDetails", createFilterDetail(nextStep, isUsual, localgovinfoid, guId));
			if(!auth){
				String message = "<p>" + lang.__("Failed to authenticate the Admin privilege.") + "</p>";
				contentsJson.put("errors", message);
			}else{
				contentsJson.put("errors", "");
			}
			contentsJson.put("finished", false);
		}else{
			contentsJson.put("finished", true);
		}


		return contentsJson;
	}

	/**
	 * ウイザードエラーページ作成
	 * @param baseData
	 * @return
	 */
	public JSONObject createErrorContents(JSONObject baseData){
		JSONObject contentsJson = new JSONObject();

		Map<String, Boolean> buttonMap = createWizardButton(ERRORSTEP);
		for(Map.Entry<String, Boolean> e : buttonMap.entrySet()){
			contentsJson.put(e.getKey(), e.getValue());
		}
		contentsJson.put("title", createTitle(ERRORSTEP));
		contentsJson.put("description", createDescription(ERRORSTEP));
		StringBuffer contentsBuf = new StringBuffer();
		contentsBuf.append("<p>");
		contentsBuf.append(baseData.getString("dbErrorClass"));
		contentsBuf.append("</p>");
		contentsBuf.append("<p>");
		contentsBuf.append(baseData.getString("dbErrorMessage"));
		contentsBuf.append("</p>");
		contentsJson.put("contents",contentsBuf.toString());
		contentsJson.put("help", "");

		contentsJson.put("errors", "");


		return contentsJson;
	}

	/**
	 * ウイザードステップページのボタン作成
	 * @param nextStep
	 * @return
	 */
	private Map<String,Boolean> createWizardButton(int nextStep){

		Map<String,Boolean> buttonMap = new HashMap<String, Boolean>();
		buttonMap.put("cancelButton", true);
		buttonMap.put("reloadButton", true);
		switch (nextStep) {
		case STEPM2:
		case STEPM1:
		case STEP0:
			buttonMap.put("backButton", false);
			buttonMap.put("nextButton", true);
			buttonMap.put("finishButton", false);
			buttonMap.put("reloadButton", false);
			break;
		case STEP1:
			buttonMap.put("backButton", false);
			buttonMap.put("nextButton", true);
			buttonMap.put("finishButton", false);
			break;
		case STEP2:
		case STEP3:
		case STEP4:
		case STEP5:
		case STEP6:
		case STEP7:
		case STEP8:
			buttonMap.put("backButton", true);
			buttonMap.put("nextButton", true);
			buttonMap.put("finishButton", false);
			break;
		case STEP9:
		case STEP10:
			buttonMap.put("backButton", true);
			buttonMap.put("nextButton", true);
			buttonMap.put("finishButton", true);
			break;
		case STEP11:
			buttonMap.put("backButton", true);
			buttonMap.put("nextButton", false);
			buttonMap.put("finishButton", true);
			break;
		case ERRORSTEP:
			buttonMap.put("cancelButton", true);
			buttonMap.put("reloadButton", false);
			buttonMap.put("backButton", false);
			buttonMap.put("nextButton", false);
			buttonMap.put("finishButton", false);
			break;
		default:
			buttonMap.put("backButton", false);
			buttonMap.put("nextButton", false);
			buttonMap.put("finishButton", false);
			break;
		}

		return buttonMap;
	}

	/**
	 * ウイザードステップページのタイトル作成
	 * @param nextStep
	 * @return
	 */
	private String createTitle(int nextStep){
		String title = "";
		switch (nextStep) {
		case STEPM2:
			title = lang.__("Check the Admin privilege.");
			break;
		case STEPM1:
			title = lang.__("Select local government.");
			break;
		case STEP0:
			title = lang.__("Select editing target.");
			break;
		case STEP1:
			title = lang.__("The 1st layer task registration");
			break;
		case STEP2:
			title = lang.__("The 2nd layer task registration");
			break;
		case STEP3:
			title = lang.__("Menu registration");
			break;
		case STEP4:
			title = lang.__("Page button display info registration");
			break;
		case STEP5:
			title = lang.__("Menu table info registration");
			break;
		case STEP6:
			title = lang.__("Table list item info registration");
			break;
		case STEP7:
			title = lang.__("Map layer info registration");
			break;
		case STEP8:
			title = lang.__("Map base layer info registration");
			break;
		case STEP9:
			title = lang.__("Filter setting info registration");
			break;
		case STEP10:
			title = lang.__("Map reference layer info registration");
			break;
		case STEP11:
			title = lang.__("Map KML layer info registration");
			break;
		case STEP12:
			title = lang.__("");
			break;
		case ERRORSTEP:
			title = lang.__("DB update error.");
			break;
		default:
			title = lang.__("");
			break;
		}

		if(nextStep > STEP0 && nextStep < ERRORSTEP){
			return nextStep + "/11 " + title;
		}else{
			return  title;
		}
	}

	/**
	 * ウイザードステップページの説明文作成
	 * @param nextStep
	 * @return
	 */
	private String createDescription(int nextStep){
		String title = "";
		switch (nextStep) {
		case STEPM2:
			title = lang.__("Check the Admin privilege on this page.");
			break;
		case STEPM1:
			title = lang.__("Select local government on this page.");
			break;
		case STEP0:
			title = lang.__("");
			break;
		case STEP1:
			title = lang.__("Register the 1st layer task.");
			break;
		case STEP2:
			title = lang.__("Register the 2nd layer task.");
			break;
		case STEP3:
			title = lang.__("Register menu items.");
			break;
		case STEP4:
			title = lang.__("Register page button display information.");
			break;
		case STEP5:
			title = lang.__("Register menu table information.");
			break;
		case STEP6:
			title = lang.__("Register table list items information.");
			break;
		case STEP7:
			title = lang.__("Register map layer information.");
			break;
		case STEP8:
			title = lang.__("Register map base layer information.");
			break;
		case STEP9:
			title = lang.__("Register filter setting information.");
			break;
		case STEP10:
			title = lang.__("Register map reference layer information.");
			break;
		case STEP11:
			title = lang.__("Register map KML layer information.");
			break;
		case STEP12:
			title = lang.__("");
			break;
		case ERRORSTEP:
			title = lang.__("Cancel wizard due to DB update error.");
			break;
		default:
			break;
		}

		return  title;
	}

	/**
	 * ウイザードステップページのヘルプツールチップ作成
	 * @param nextStep
	 * @return
	 */
	private JSONObject createHelp(int nextStep){

		JSONObject message = new JSONObject();
		switch (nextStep) {
		case STEPM2:
			message.put("message1", "none");
			break;
		case STEPM1:
			message.put("message1", "none");
			break;
		case STEP0:
			message.put("message1", "none");
			break;
		case STEP1:
			message.put("message1", lang.__("The selected tab is highlighted."));
			break;
		case STEP2:
			message.put("message1", lang.__("The selected tab is highlighted."));
			break;
		case STEP3:
			message.put("message1", lang.__("It shows a window type displayed by a menu clicked."));
			break;
		case STEP4:
			message.put("message1", lang.__("If selected, an action depending on page button ID, will be performed by a button clicked."));
			break;
		case STEP5:
			message.put("message1", lang.__("If selected, an item will be added."));
			message.put("message2", lang.__("If selected, an item will be deleted."));
			message.put("message3", lang.__("If selected, a total amount will be displayed."));
			break;
		case STEP6:
			message.put("message1", lang.__("The selected attribution is to be edited."));
			message.put("message2", lang.__("The selected attribution is highlighted."));
			message.put("message3", lang.__("If selected, the butch group operation will be valid."));
			message.put("message4", lang.__("If selected, sort operation will be valid."));
			message.put("message5", lang.__("Sort setting. \"-1\": sort invalid \"0\": ascending \"1\": descending"));
			message.put("message6", lang.__("Data, such as photos can be uploaded."));
			message.put("message7", lang.__("Log will be displayed in response history when an attribution changes."));
			break;
		case STEP7:
			message.put("message1", lang.__("The selected attribution is displayed as default."));
			message.put("message2", lang.__("If selected, the legend will be folded as default."));
			message.put("message3", lang.__("The selected item is to be edited."));
			message.put("message4", lang.__("The selected item is to be added."));
			message.put("message5", lang.__("If selected, search function will be valid."));
			message.put("message6", lang.__("Cursor performs with snap when features are registered."));
			message.put("message7", lang.__("Geospatial calculation will perform trimming with a feature on a cutting-out-layerID when points, lines or polygons are registered."));
			break;
		case STEP8:
			message.put("message1", lang.__("The selected item will be displayed as default."));
			break;
		case STEP9:
			message.put("message1", lang.__("It shows registered filter ID."));
			break;
		case STEP10:
			message.put("message1", lang.__("The selected item will be displayed as default."));
			message.put("message2", lang.__("If selected, the legend will be folded as default."));
			message.put("message3", lang.__("If selected, search function will be valid."));
			break;
		case STEP11:
			message.put("message1", lang.__("The selected item will be displayed as default."));
			message.put("message2", lang.__("If selected, search function will be valid."));
			break;
		case STEP12:
			message.put("message1", "none");
			break;
		default:
			break;
		}

		return message;
	}

	/**
	 * eコミマップから利用可能なフィルターのリストを取得する
	 * @param nextStep
	 * @param isUsual
	 * @return
	 */
	private JSONArray createFilterDetail(int nextStep, boolean isUsual, long localgovinfoid, long guId){
		JSONArray filterListJSONArray = new JSONArray();
		// STEP9 フィルター情報の入力ページでのみ使用
		if(nextStep == 9){
	    	// eコミマップから地図ベースレイヤを取得
	    	String getConditionListResult = "";
	        MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
	        if(mapmasterInfo != null){
	            String ecommUser;
	            if(isUsual){
	            	UnitInfo unitInfo = unitInfoService.findById(guId);
	            	ecommUser = unitInfo.ecomuser;
	            }else{
	            	GroupInfo groupInfo = groupInfoService.findById(guId);
	            	ecommUser = groupInfo.ecomuser;
	            }
		    	// 利用可能なフィルターリストを取得
	            getConditionListResult = ecommapDataGetService.getConditionList(ecommUser, new Integer(mapmasterInfo.mapid.toString()));
	        }

        	JSONObject getConditionListResultJson = JSONObject.fromObject(getConditionListResult);
    		filterListJSONArray = getConditionListResultJson.getJSONArray("items");
		}
		return filterListJSONArray;
	}

	/**
	 * ウイザードステップページコンテンツ作成
	 * @param nextStep
	 * @param jsonData
	 * @param isUsual
	 * @param guId
	 * @return
	 */
	private String createContents(int nextStep, JSONObject jsonData, boolean isUsual, long localgovinfoid,
			long guId, Map<String, Boolean> buttonMap){

		// ボタンの作成
		Map<String,Boolean> tempButton = createWizardButton(nextStep);
		for(Map.Entry<String, Boolean> e: tempButton.entrySet()){
			buttonMap.put(e.getKey(), e.getValue());
		}


		StringBuffer contentsMainBuf = new StringBuffer();
		boolean blankPage = false;
		String addInputMessage = lang.__("Please enter name.");

		switch (nextStep) {
		// Step-2 管理者権限の確認
		case STEPM2:
			contentsMainBuf.append("<table class=\"menuWizard_contents_table\">");
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append("<td>");
			if(isUsual){
				contentsMainBuf.append(lang.__("Unit name<!--2-->"));
			}else{
				contentsMainBuf.append(lang.__("Group name<!--2-->"));
			}
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td>");
			contentsMainBuf.append("<input type=\"text\" name=\"menuWizardStepM2_text1\" value=\"\">");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("</tr>");
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append("<td>");
			contentsMainBuf.append(lang.__("Password"));
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td>");
			contentsMainBuf.append("<input type=\"password\" name=\"menuWizardStepM2_text2\" value=\"\">");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("</tr>");
			contentsMainBuf.append("</table>");
			break;
		// Step-1 自治体の選択
		case STEPM1:

			// 自治体リスト作成
			List<LocalgovInfo> localgovInfoList = localgovInfoService.findValidOrderByID();

			contentsMainBuf.append("<table class=\"menuWizard_contents_table\">");
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append("<td>");
			contentsMainBuf.append(lang.__("Local gov."));
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td>");
			contentsMainBuf.append("<select id=\"menuWizardStepM1_select1\" name=\"menuWizardStepM1_select1\">");
			for(LocalgovInfo localgovInfo : localgovInfoList){
				contentsMainBuf.append("<option value=\"");
				contentsMainBuf.append(localgovInfo.id);
				contentsMainBuf.append("\">");
				contentsMainBuf.append(localgovInfo.id);
				contentsMainBuf.append(":");
				if(localgovInfo.localgovtypeid.equals(Constants.LOCALGOVINFOTYPE_CITY)){
					contentsMainBuf.append(localgovInfo.pref);
					contentsMainBuf.append(localgovInfo.city);
				}else{
					contentsMainBuf.append(localgovInfo.city);
				}
				contentsMainBuf.append("</option>");
			}
			contentsMainBuf.append("</select>");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append("<td>");
			if(isUsual){
				contentsMainBuf.append(lang.__("Unit name<!--2-->"));
			}else{
				contentsMainBuf.append(lang.__("Group name<!--2-->"));
			}
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td>");
			contentsMainBuf.append("<select id=\"menuWizardStepM1_select2\" name=\"menuWizardStepM1_select2\">");
			contentsMainBuf.append("<option>");
			contentsMainBuf.append("</option>");
			contentsMainBuf.append("</select>");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("</tr>");

			break;
		// Step00 適用範囲の選択
		case STEP0:
			// ログインモード（平時／災害）判定
			String step0Message1 = "";
			String step0Message2 = "";
			if(isUsual){
				step0Message1 = lang.__("Only my unit is editing target.");
				step0Message2 = lang.__("All units are editing target.");
			}else{
				step0Message1 = lang.__("Only my group is editing target.");
				step0Message2 = lang.__("All groups are editing target.");
			}

			String step0Radio = "";
			String step0Radio1;
			String step0Radio2;
			if(jsonData.containsKey("step0Data")){
				JSONObject step0JsonData = jsonData.getJSONObject("step0Data");
				step0Radio = step0JsonData.getString("radio");
			}
			if(step0Radio.equals("all")){
				step0Radio1 = "<input type=\"radio\" name=\"menuWizardStep0_radio\" value=\"mine\">";
				step0Radio2 = "<input type=\"radio\" name=\"menuWizardStep0_radio\" value=\"all\" checked=\"checked\">";
			}else{
				step0Radio1 = "<input type=\"radio\" name=\"menuWizardStep0_radio\" value=\"mine\" checked=\"checked\">";
				step0Radio2 = "<input type=\"radio\" name=\"menuWizardStep0_radio\" value=\"all\">";
			}
			contentsMainBuf.append("<p>");
			contentsMainBuf.append(lang.__("Edit task, menu info by the Wizard function. First, select the shown below."));
			contentsMainBuf.append("</p>");
			contentsMainBuf.append(step0Radio1);
			contentsMainBuf.append(step0Message1);
			contentsMainBuf.append("<BR>");
			contentsMainBuf.append(step0Radio2);
			contentsMainBuf.append(step0Message2);

			break;

		// Step01 第１階層タスク登録
		case STEP1:
			// ログインモード（平時／災害）判定
			MenuloginInfo menuloginInfo = null;
			if(isUsual){
				menuloginInfo = menuloginInfoService.findByUnitInfoIdAndNotDeleted(guId);
			}else{
				menuloginInfo = menuloginInfoService.findByGroupInfoIdAndNotDeleted(guId);
			}

			// 当該班または課にメニューログイン情報が作成されていない場合は
			// ウイザードを中止する。
			if(menuloginInfo == null){
	        	blankPage = true;
			}else{
	        	blankPage = false;
			}


			if(!blankPage){
				// 自班のメニュープロセス情報リストを作成
				List<MenuprocessInfo> menuprocessInfoList = new ArrayList<MenuprocessInfo>();
				if(menuloginInfo != null){
					menuprocessInfoList = menuprocessInfoService.findByMenuloginInfoId(menuloginInfo.id);
				}

				String step1Radio = "";
				String step1Radio1 = "<input type=\"radio\" name=\"menuWizardStep1_radio\" checked=\"checked\" value=\"";
				String step1Radio2 = "<input type=\"radio\" name=\"menuWizardStep1_radio\" value=\"";
				if(jsonData.containsKey("step1Data")){
					JSONObject step1JsonData = jsonData.getJSONObject("step1Data");
					if((! step1JsonData.isEmpty()) && (! step1JsonData.isNullObject())){
						step1Radio = step1JsonData.getString("radio");
					}
				}

				// THEAD作成
				contentsMainBuf.append(createFixHtml1(true));
				contentsMainBuf.append(createThTag(true, false, lang.__("Name(*)"), "name", null));
				contentsMainBuf.append(createThTag(true, true, lang.__("vital flag"), "important", "menuWizard_head_important"));
				contentsMainBuf.append(createFixHtml2(true, true, true));

				contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
				// 1行目は追加時のテンプレートに使用
				contentsMainBuf.append("<tr>");
				contentsMainBuf.append(createZeroHiddenTd(2));
				contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
				contentsMainBuf.append(step1Radio2);
				contentsMainBuf.append(0);
				contentsMainBuf.append("\">");
				contentsMainBuf.append("</td>");
				contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
				contentsMainBuf.append("<div contenteditable=\"true\">");
				contentsMainBuf.append(addInputMessage);
				contentsMainBuf.append("</div>");
				contentsMainBuf.append("</td>");
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep1_checkbox\" value=\"");
				contentsMainBuf.append("\" id=\"menuWizardStep1_checkbox_");
				contentsMainBuf.append("0");
				contentsMainBuf.append("\">");
				contentsMainBuf.append("</td>");
				contentsMainBuf.append(createFixHtml3(true, true, true));

				for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
					// 削除フラグが立っているものは除外
					if(menuprocessInfo.deleted){
						continue;
					}
					contentsMainBuf.append("<tr>");

					// 非表示項目1:メニュープロセス情報ID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append(menuprocessInfo.id);
					contentsMainBuf.append("</td>");
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");

					// 表示項目1:選択ラジオボタン
					contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
					if(step1Radio.equals(Long.toString(menuprocessInfo.id))){
						contentsMainBuf.append(step1Radio1);
					}else{
						contentsMainBuf.append(step1Radio2);
					}
					contentsMainBuf.append(menuprocessInfo.id);
					contentsMainBuf.append("\">");
					contentsMainBuf.append("</td>");

					// 表示項目2:タスク名称
					contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
					contentsMainBuf.append("<div contenteditable=\"true\">");
					contentsMainBuf.append(menuprocessInfo.name);
					contentsMainBuf.append("</div>");
					contentsMainBuf.append("</td>");

					// 表示項目3:重要フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					if(menuprocessInfo.important){
						contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep1_checkbox\" value=\"");
						contentsMainBuf.append(menuprocessInfo.id);
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep1_checkbox\" value=\"");
						contentsMainBuf.append(menuprocessInfo.id);
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					contentsMainBuf.append(createFixHtml3(true, true, true));
				}
				contentsMainBuf.append("</tbody>");
				contentsMainBuf.append("</table>");
			}else{
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"1\">");
				contentsMainBuf.append("<p>");
				contentsMainBuf.append(lang.__("A group or an unit corresponding to this wizard is not yet registered. First, create a record of Menu conf info of the target group or unit on admin window."));
				contentsMainBuf.append("</p>");
				buttonMap.clear();
				tempButton = createWizardButton(ERRORSTEP);
				for(Map.Entry<String, Boolean> e: tempButton.entrySet()){
					buttonMap.put(e.getKey(), e.getValue());
				}
			}

			break;
		case STEP2:
			// Step02 第２階層タスク登録
			JSONObject step1JsonData;
			String menuprocessinfoId;
			step1JsonData = jsonData.getJSONObject("step1Data");
			menuprocessinfoId = step1JsonData.getString("radio");

			// タスク種別プルダウン作成
			BeanMap step2MenutasktypeInfoCondition = new BeanMap();
			step2MenutasktypeInfoCondition.put(MenutasktypeInfoNames.localgovinfoid().toString(), localgovinfoid);
			step2MenutasktypeInfoCondition.put(MenutasktypeInfoNames.deleted().toString(), false);
			List<MenutasktypeInfo> menutasktypeInfoList = menutasktypeInfoService.findByCondition(
					step2MenutasktypeInfoCondition,
					MenutasktypeInfoNames.disporder().toString(),
					Constants.ASC,
					null,
					null,
					false);


			// 「戻る」から遷移
			String step2Radio = "";
			String step2Radio1 = "<input type=\"radio\" name=\"menuWizardStep2_radio\" checked=\"checked\" value=\"";
			String step2Radio2 = "<input type=\"radio\" name=\"menuWizardStep2_radio\" value=\"";
			if(jsonData.containsKey("step2Data")){
				JSONObject step2JsonData = jsonData.getJSONObject("step2Data");
				if((! step2JsonData.isEmpty()) && (! step2JsonData.isNullObject())){
					step2Radio = step2JsonData.getString("radio");
				}
			}

			// メニュータスク新規追加ボタン作成
			contentsMainBuf.append("<button type=\"button\" id=\"menuWizard_step2_menutasktype_addbutton\" name=\"menuWizard_step2_menutasktype_addbutton\">");
			contentsMainBuf.append(lang.__("Menu task type"));
			contentsMainBuf.append(lang.__("Add new"));
			contentsMainBuf.append("</button>");

			// 自班のメニュータスク情報リストを作成
			List<MenutaskInfo> menutaskInfoList = new ArrayList<MenutaskInfo>();
			BeanMap step2Condition = new BeanMap();
			step2Condition.put(MenutaskInfoNames.menuprocessinfoid().toString(), Long.parseLong(menuprocessinfoId));
			menutaskInfoList = menutaskInfoService.findByCondition(step2Condition,
					MenutaskInfoNames.disporder().toString(),
					Constants.ASC,
					null,
					null);

			// THEAD作成
			contentsMainBuf.append(createFixHtml1(true));
			contentsMainBuf.append(createThTag(true, false, lang.__("Menu task type"), "tasktype", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Name(*)"), "name", null));
			contentsMainBuf.append(createThTag(true, true, lang.__("vital flag"), "important","menuWizard_head_important"));
			contentsMainBuf.append(createFixHtml2(true, true, true));

			contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
			// 1行目は追加時のテンプレートに使用
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append(createZeroHiddenTd(2));
			contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
			contentsMainBuf.append(step2Radio2);
			contentsMainBuf.append(0);
			contentsMainBuf.append("\">");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td class=\"menuWizard_contents_select\">");
			contentsMainBuf.append("<select class=\"menuWizard_contents_select_step2\">");
			for(MenutasktypeInfo menutasktypeInfo : menutasktypeInfoList){
				contentsMainBuf.append("<option value=\"");
				contentsMainBuf.append(menutasktypeInfo.id);
				contentsMainBuf.append("\">");
				contentsMainBuf.append(menutasktypeInfo.id);
				contentsMainBuf.append(":");
				contentsMainBuf.append(menutasktypeInfo.name);
				contentsMainBuf.append("</option>");
			}
//			contentsMainBuf.append("<option value=\"");
//			contentsMainBuf.append(0);
//			contentsMainBuf.append("\">");
//			contentsMainBuf.append(lang.__("Add new"));
//			contentsMainBuf.append("</option>");
			contentsMainBuf.append("</select>");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
			contentsMainBuf.append("<div contenteditable=\"true\">");
			contentsMainBuf.append(addInputMessage);
			contentsMainBuf.append("</div>");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
			contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep2_checkbox\" value=\"");
			contentsMainBuf.append("\" id=\"menuWizardStep2_checkbox_");
			contentsMainBuf.append("0");
			contentsMainBuf.append("\">");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append(createFixHtml3(true, true, true));

			for(MenutaskInfo menutaskInfo : menutaskInfoList){
				// 削除フラグが立っているものは除外
				if(menutaskInfo.deleted){
					continue;
				}
				contentsMainBuf.append("<tr>");
				// 非表示項目1:メニュータスク情報ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append(menutaskInfo.id);
				contentsMainBuf.append("</td>");
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");

				// 表示項目1:選択ラジオボタン
				contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
				if(step2Radio.equals(Long.toString(menutaskInfo.id))){
					contentsMainBuf.append(step2Radio1);
				}else{
					contentsMainBuf.append(step2Radio2);
				}
				contentsMainBuf.append(menutaskInfo.id);
				contentsMainBuf.append("\">");
				contentsMainBuf.append("</td>");

				// 表示項目2:メニュータスク種別選択リストボックス
				contentsMainBuf.append("<td class=\"menuWizard_contents_select\">");
				contentsMainBuf.append("<select class=\"menuWizard_contents_select_step2\">");
				for(MenutasktypeInfo menutasktypeInfo : menutasktypeInfoList){
					contentsMainBuf.append("<option value=\"");
					contentsMainBuf.append(menutasktypeInfo.id);
					contentsMainBuf.append("\" ");
					if(menutaskInfo.menutasktypeinfoid.equals(menutasktypeInfo.id)){
						contentsMainBuf.append("selected>");
					}else{
						contentsMainBuf.append(">");
					}
					contentsMainBuf.append(menutasktypeInfo.id);
					contentsMainBuf.append(":");
					contentsMainBuf.append(menutasktypeInfo.name);
					contentsMainBuf.append("</option>");
				}
//				contentsMainBuf.append("<option value=\"");
//				contentsMainBuf.append(0);
//				contentsMainBuf.append("\">");
//				contentsMainBuf.append(lang.__("Add new"));
//				contentsMainBuf.append("</option>");
				contentsMainBuf.append("</select>");
				contentsMainBuf.append("</td>");

				// 表示項目3:名称
				contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
				contentsMainBuf.append("<div contenteditable=\"true\">");
				contentsMainBuf.append(menutaskInfo.name);
				contentsMainBuf.append("</div>");
				contentsMainBuf.append("</td>");

				// 表示項目4:重要フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				if(menutaskInfo.important){
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep2_checkbox\" value=\"");
					contentsMainBuf.append(menutaskInfo.id);
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep2_checkbox\" value=\"");
					contentsMainBuf.append(menutaskInfo.id);
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				contentsMainBuf.append(createFixHtml3(true, true, true));
			}
			contentsMainBuf.append("</tbody>");
			contentsMainBuf.append("</table>");
			break;
		case STEP3:
			// Step03 メニュー登録
			JSONObject step2JsonData;
			String menutaskinfoId;
			step2JsonData = jsonData.getJSONObject("step2Data");
			menutaskinfoId = step2JsonData.getString("radio");

			// メニュータイプマスタを取得
			List<MenutypeMaster> menutypeMasterList = menutypeMasterService.findAll(new OrderByItem("disporder", OrderingSpec.ASC));

			// 「戻る」から遷移
			String step3Radio = "";
			String step3Radio1 = "<input type=\"radio\" name=\"menuWizardStep3_radio\" checked=\"checked\" value=\"";
			String step3Radio2 = "<input type=\"radio\" name=\"menuWizardStep3_radio\" value=\"";
			if(jsonData.containsKey("step3Data")){
				JSONObject step3JsonData = jsonData.getJSONObject("step3Data");
				if((! step3JsonData.isEmpty()) && (! step3JsonData.isNullObject())){
					step3Radio = step3JsonData.getString("radio");
				}
			}

			// 自班のタスクメニュー情報リストを作成
			List<MenutaskmenuInfo> menutaskmenuInfoList = new ArrayList<MenutaskmenuInfo>();
			BeanMap step3Condition = new BeanMap();
			step3Condition.put(MenutaskmenuInfoNames.menutaskinfoid().toString(), Long.parseLong(menutaskinfoId));
			menutaskmenuInfoList = menutaskmenuInfoService.findByCondition(step3Condition,
					MenutaskmenuInfoNames.disporder().toString(),
					Constants.ASC,
					null,
					null);

			// THEAD作成
			contentsMainBuf.append(createFixHtml1(true));
			contentsMainBuf.append(createThTag(true, false, lang.__("Name(*)"), "name", null));
			contentsMainBuf.append(createThTag(true, true, lang.__("Menu type"), "menutype", null));
			contentsMainBuf.append(createThTag(false, false, lang.__("Excel template file"), "excellisttemplatetempfile", null));
			contentsMainBuf.append(createFixHtml2(true, true, true));

			contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
			// 1行目は追加時のテンプレートに使用
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append(createZeroHiddenTd(2));
			contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
			contentsMainBuf.append(step3Radio2);
			contentsMainBuf.append(0);
			contentsMainBuf.append("\">");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
			contentsMainBuf.append("<div contenteditable=\"true\">");
			contentsMainBuf.append(addInputMessage);
			contentsMainBuf.append("</div>");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append("<td class=\"menuWizard_contents_select\">");
			contentsMainBuf.append("<select class=\"menuWizard_contents_select_step3\">");
			for(MenutypeMaster menutypeMaster : menutypeMasterList){
				contentsMainBuf.append("<option value=\"");
				contentsMainBuf.append(menutypeMaster.id);
				contentsMainBuf.append("\">");
				contentsMainBuf.append(menutypeMaster.id);
				contentsMainBuf.append(":");
				contentsMainBuf.append(menutypeMaster.name);
				contentsMainBuf.append("</option>");
			}
			contentsMainBuf.append("</select>");
			contentsMainBuf.append("</td>");
			contentsMainBuf.append(createZeroHiddenTd(1));
			contentsMainBuf.append(createFixHtml3(true, true, true));

			for(MenutaskmenuInfo menutaskmenuInfo : menutaskmenuInfoList){
				contentsMainBuf.append("<tr>");

				// 非表示項目1:メニュータスク情報ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append(menutaskmenuInfo.id);
				contentsMainBuf.append("</td>");
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");

				// 表示項目1:選択ラジオボタン
				contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
				if(step3Radio.equals(Long.toString(menutaskmenuInfo.id))){
					contentsMainBuf.append(step3Radio1);
				}else{
					contentsMainBuf.append(step3Radio2);
				}
				contentsMainBuf.append(menutaskmenuInfo.id);
				contentsMainBuf.append("\">");
				contentsMainBuf.append("</td>");

				// 表示項目2:名称（編集可能）
				contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
				contentsMainBuf.append("<div contenteditable=\"true\">");
				contentsMainBuf.append(menutaskmenuInfo.menuInfo.name);
				contentsMainBuf.append("</div>");
				contentsMainBuf.append("</td>");

				// 表示項目4:メニュータイプ選択リストボックス
				contentsMainBuf.append("<td class=\"menuWizard_contents_select\">");
				contentsMainBuf.append("<select class=\"menuWizard_contents_select_step3\">");
				for(MenutypeMaster menutypeMaster : menutypeMasterList){
					contentsMainBuf.append("<option value=\"");
					contentsMainBuf.append(menutypeMaster.id);
					contentsMainBuf.append("\" ");
					if(menutaskmenuInfo.menuInfo.menutypeid.equals(menutypeMaster.id)){
						contentsMainBuf.append("selected>");
					}else{
						contentsMainBuf.append(">");
					}
					contentsMainBuf.append(menutypeMaster.id);
					contentsMainBuf.append(":");
					contentsMainBuf.append(menutypeMaster.name);
					contentsMainBuf.append("</option>");
				}
				contentsMainBuf.append("</select>");
				contentsMainBuf.append("</td>");
				contentsMainBuf.append(createZeroHiddenTd(1));
				contentsMainBuf.append(createFixHtml3(true, true, true));
			}
			contentsMainBuf.append("</tbody>");
			contentsMainBuf.append("</table>");
			break;
		case STEP4:
			// Step04 ページボタン表示情報登録
			JSONObject step3JsonData;
			String menutaskmenuinfoId;
			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			MenutaskmenuInfo menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報からページボタン情報を取得
			MenuInfo menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<PagemenubuttonInfo> pagemenubuttonInfoList = pagemenubuttonInfoService.findByMenuInfoId(menuInfo.id);

	    	// ページボタン情報が持つページボタンマスタIDのリストを作成しておく
	    	Map<Integer, PagemenubuttonInfo> usedPagebuttonMasterIdMap = new HashMap<Integer, PagemenubuttonInfo>();
	    	for(PagemenubuttonInfo pagemenubuttonInfo : pagemenubuttonInfoList){
	    		usedPagebuttonMasterIdMap.put(pagemenubuttonInfo.pagebuttonid, pagemenubuttonInfo);
	    	}

			// ページボタンマスタ取得
			List<PagebuttonMaster> pagebuttonMasterList = pagebuttonMasterService.findAllOrderBy();

			//  ページボタン表示情報が存在する場合は並べ替え
			Set<PagebuttonMaster> pagebuttonMasterHashSet = new LinkedHashSet<PagebuttonMaster>();
			if(pagemenubuttonInfoList != null && pagemenubuttonInfoList.size() > 0){
				// ページボタン表示情報にIDがあるボタンを先に表示させる
				for(PagemenubuttonInfo pagemenubuttonInfo : pagemenubuttonInfoList){
			    	for(PagebuttonMaster pagebuttonMaster : pagebuttonMasterList){
			    		if(pagemenubuttonInfo.pagebuttonid.equals(pagebuttonMaster.id)){
			    			pagebuttonMasterHashSet.add(pagebuttonMaster);
			    			break;
			    		}
			    	}
				}
				// ページボタン表示情報にIDがないボタンは後ろに表示させる
				for(PagebuttonMaster pagebuttonMaster : pagebuttonMasterList){
					if(! pagebuttonMasterHashSet.contains(pagebuttonMaster)){
		    			pagebuttonMasterHashSet.add(pagebuttonMaster);
					}
		    	}
			}else{
				for(PagebuttonMaster pagebuttonMaster : pagebuttonMasterList){
	    			pagebuttonMasterHashSet.add(pagebuttonMaster);
		    	}
			}

			// THEAD作成
			contentsMainBuf.append(createFixHtml1(false));
			contentsMainBuf.append(createThTag(false, false, lang.__("Menu Page button ID"), "menuButtonid", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Page button ID"), "buttonid", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select", "menuWizard_head_select"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Available flag"), "enable", "menuWizard_head_availableFlag"));
			contentsMainBuf.append(createFixHtml2(true, false, false));

			contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
			// 1行目は追加時のテンプレートに使用
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append(createZeroHiddenTd(10));
			contentsMainBuf.append("</tr>");
			for(PagebuttonMaster pagebuttonMaster : pagebuttonMasterHashSet){
				PagemenubuttonInfo pagemenubuttonInfo = null;
				if(usedPagebuttonMasterIdMap.containsKey(pagebuttonMaster.id)){
					pagemenubuttonInfo = usedPagebuttonMasterIdMap.get(pagebuttonMaster.id);
				}

				contentsMainBuf.append("<tr>");
				// 非表示項目1:ページボタンマスタID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append(pagebuttonMaster.id);
				contentsMainBuf.append("</td>");
				// 非表示項目3:削除フラグ（未使用）
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");
				// 非表示項目2:作成対象フラグ（未使用）
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");

				// 非表示項目4:ページボタン情報ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				if(pagemenubuttonInfo != null){
					contentsMainBuf.append(pagemenubuttonInfo.id);
				}else{
					contentsMainBuf.append("0");
				}
				contentsMainBuf.append("</td>");

				// 表示項目1:ページボタンマスタID
				contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
				contentsMainBuf.append(pagebuttonMaster.id);
				contentsMainBuf.append(":");
				contentsMainBuf.append(pagebuttonMaster.name);
				contentsMainBuf.append("</td>");

				// 表示項目2:選択チェックボックス
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep4_checkbox1\" value=\"");
				contentsMainBuf.append("1");
				if(pagemenubuttonInfo != null){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目3:利用可フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep4_checkbox2\" value=\"");
				contentsMainBuf.append("1");
				if(pagemenubuttonInfo != null && pagemenubuttonInfo.enable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				contentsMainBuf.append(createFixHtml3(true, false, false));
			}
			contentsMainBuf.append("</tbody>");
			contentsMainBuf.append("</table>");

			break;
		case STEP5:
			// Step05 メニューテーブル情報登録
			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報からメニューテーブル情報を取得
			menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<MenutableInfo> menutableInfoList = menutableInfoService.findByMenuInfoId(menuInfo.id);

	    	// メニューテーブル情報が持つテーブルマスタ情報IDのリストを作成しておく
	    	Map<Long, MenutableInfo> menutableInfoHasTablemasterInfoIdMap = new HashMap<Long, MenutableInfo>();
	    	for(MenutableInfo menutableInfo : menutableInfoList){
	    		menutableInfoHasTablemasterInfoIdMap.put(menutableInfo.tablemasterinfoid, menutableInfo);
	    	}

			// テーブルマスタ情報取得
	    	List<TablemasterInfo> tablemasterInfoList = tablemasterInfoService.findByLocalgovinfoid(localgovinfoid);

			//  メニューテーブル情報が存在する場合は並べ替え
			Set<TablemasterInfo> tablemasterInfoHashSet = new LinkedHashSet<TablemasterInfo>();
			if(menutableInfoList != null && menutableInfoList.size() > 0){
				// メニューテーブル情報にIDがあるテーブルマスタを先に表示させる
				for(MenutableInfo menutableInfo : menutableInfoList){
			    	for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
			    		if(menutableInfo.tablemasterinfoid.equals(tablemasterInfo.id)){
			    			tablemasterInfoHashSet.add(tablemasterInfo);
			    			break;
			    		}
			    	}
				}
				// メニューテーブル情報にIDがないテーブルマスタは後ろに表示させる
				for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
					if(! tablemasterInfoHashSet.contains(tablemasterInfo)){
						tablemasterInfoHashSet.add(tablemasterInfo);
					}
		    	}
			}else{
				for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
					tablemasterInfoHashSet.add(tablemasterInfo);
		    	}
			}

			// THEAD作成
			contentsMainBuf.append(createFixHtml1(false));
			contentsMainBuf.append(createThTag(false, false, lang.__("Menu table ID"), "menutableinfoid", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Name"), "name", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select", "menuWizard_head_select"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Add flag"), "addable", "menuWizard_head_addFlag"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Delete flag"), "deletable", "menuWizard_head_deleteFlag"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Total flag"), "totalable", "menuWizard_head_totalFlag"));
			contentsMainBuf.append(createFixHtml2(false, false, false));

			contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
			// 1行目は追加時のテンプレートに使用
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append(createZeroHiddenTd(12));
			contentsMainBuf.append("</tr>");

			for(TablemasterInfo tablemasterInfo : tablemasterInfoHashSet){
				MenutableInfo menutableInfo = null;
				if(menutableInfoHasTablemasterInfoIdMap.containsKey(tablemasterInfo.id)){
					menutableInfo = menutableInfoHasTablemasterInfoIdMap.get(tablemasterInfo.id);
				}

				contentsMainBuf.append("<tr>");
				// 非表示項目1:テーブルマスタ情報ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append(tablemasterInfo.id);
				contentsMainBuf.append("</td>");
				// 非表示項目2:削除フラグ（未使用）
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");
				// 非表示項目3:作成対象フラグ（未使用）
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");

				// 非表示項目4:メニューテーブル情報ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				if(menutableInfo != null){
					contentsMainBuf.append(menutableInfo.id);
				}else{
					contentsMainBuf.append("0");
				}
				contentsMainBuf.append("</td>");

				// 表示項目1:名称
				contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
				contentsMainBuf.append(tablemasterInfo.id);
				contentsMainBuf.append(":");
				contentsMainBuf.append(tablemasterInfo.name);
				contentsMainBuf.append("</td>");

				// 表示項目2:選択ラジオボタン
				contentsMainBuf.append("<td class=\"menuWizard_contents_radio\">");
				contentsMainBuf.append("<input type=\"radio\" name=\"menuWizardStep5_radio\" value=\"");
				if(menutableInfo != null){
					contentsMainBuf.append(menutableInfo.id);
				}else{
					contentsMainBuf.append("0");
				}
				if(menutableInfo != null && menutableInfo.tablemasterinfoid.equals(tablemasterInfo.id)){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目3:追加フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep5_checkbox1\" value=\"");
				contentsMainBuf.append("1");
				if(menutableInfo != null && menutableInfo.addable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目4:削除フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep5_checkbox2\" value=\"");
				contentsMainBuf.append("1");
				if(menutableInfo != null && menutableInfo.deletable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目5:合計フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep5_checkbox3\" value=\"");
				contentsMainBuf.append("1");
				if(menutableInfo != null && menutableInfo.totalable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				contentsMainBuf.append(createFixHtml3(false, false, false));
			}
			contentsMainBuf.append("</tbody>");
			contentsMainBuf.append("</table>");
			break;
		case STEP6:
			// Step06 テーブルリスト項目情報登録
			// メニューテーブル情報取得
			JSONObject step5JsonData = jsonData.getJSONObject("step5Data");
			String menutableInfoId = step5JsonData.getString("radio");
			MenutableInfo menutableInfo = menutableInfoService.findById(Long.parseLong(menutableInfoId));
			// メニューテーブル情報からテーブルリスト項目情報を取得
	    	List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(menutableInfo.id);

	    	// テーブルリスト項目情報が持つ属性IDのリストを作成しておく
	    	Map<String, TablelistcolumnInfo> usedAttrIdMap = new HashMap<String, TablelistcolumnInfo>();
	    	for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){
	    		usedAttrIdMap.put(tablelistcolumnInfo.attrid, tablelistcolumnInfo);
	    	}

	    	// レイヤ属性リスト作成
			Map<String, String> attrInfoMap = createAttrInfoList(menutableInfo);

			//  テーブルリスト項目情報が存在する場合は並べ替え
	    	Map<String, String> sortedAttrInfoMap = new LinkedHashMap<String, String>();
			if(tablelistcolumnInfoList != null && tablelistcolumnInfoList.size() > 0){
				// テーブルリスト項目情報に属性IDがあるレイヤ属性を先に表示させる
				for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){
					if(attrInfoMap.containsKey(tablelistcolumnInfo.attrid)){
						sortedAttrInfoMap.put(tablelistcolumnInfo.attrid, attrInfoMap.get(tablelistcolumnInfo.attrid));
					}
				}
				// テーブルリスト項目情報に属性IDがないレイヤ属性は後ろに表示させる
				for(Map.Entry<String, String> e : attrInfoMap.entrySet()){
					if(! sortedAttrInfoMap.containsKey(e.getKey())){
						sortedAttrInfoMap.put(e.getKey(), e.getValue());
					}
		    	}
			}else{
				for(Map.Entry<String, String> e : attrInfoMap.entrySet()){
					sortedAttrInfoMap.put(e.getKey(), e.getValue());
		    	}
			}

			// THEAD作成
			contentsMainBuf.append(createFixHtml1(false));
			contentsMainBuf.append(createThTag(false, false, lang.__("Menu table ID"), "tablelistcolumninfoid", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Name"), "name", null));
			contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select", "menuWizard_head_select"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Allowed to edit"), "editable", "menuWizard_head_editable"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Highlighting"), "highlight", "menuWizard_head_highlight"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Grouping"), "grouping", "menuWizard_head_grouping"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Allowed to sort"), "sortable", "menuWizard_head_sortable"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Default sort item(*)"), "defaultsort", "menuWizard_head_defaultsort"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Allowed to upload file"), "uploadable", "menuWizard_head_uploadable"));
			contentsMainBuf.append(createThTag(true, true, lang.__("Allowed to log output"), "loggable", "menuWizard_head_loggable"));
			contentsMainBuf.append(createFixHtml2(true, false, false));

			contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
			// 1行目は追加時のテンプレートに使用
			contentsMainBuf.append("<tr>");
			contentsMainBuf.append(createZeroHiddenTd(16));
			contentsMainBuf.append("</tr>");

			for(Map.Entry<String, String> attrInfoEntry: sortedAttrInfoMap.entrySet()){
				TablelistcolumnInfo tablelistcolumnInfo = null;
				if(usedAttrIdMap.containsKey(attrInfoEntry.getKey())){
					tablelistcolumnInfo = usedAttrIdMap.get(attrInfoEntry.getKey());
				}

				contentsMainBuf.append("<tr>");
				// 非表示項目1:属性ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append(attrInfoEntry.getKey());
				contentsMainBuf.append("</td>");
				// 非表示項目2:削除フラグ（未使用）
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");
				// 非表示項目3:作成対象フラグ（未使用）
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				contentsMainBuf.append("0");
				contentsMainBuf.append("</td>");

				// 非表示項目4:テーブルリストカラム情報ID
				contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
				if(tablelistcolumnInfo != null){
					contentsMainBuf.append(tablelistcolumnInfo.id);
				}else{
					contentsMainBuf.append("0");
				}
				contentsMainBuf.append("</td>");

				// 表示項目1:名称
				contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
				contentsMainBuf.append(attrInfoEntry.getValue());
				contentsMainBuf.append("</td>");

				// 表示項目2:選択チェックボックス
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox1\" value=\"");
				contentsMainBuf.append(attrInfoEntry.getKey());
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.attrid.equals(attrInfoEntry.getKey())){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目3:編集可能フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox2\" value=\"");
				contentsMainBuf.append("1");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.editable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目4:強調表示フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox3\" value=\"");
				contentsMainBuf.append("1");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.highlight){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目5:グループ化可能フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox4\" value=\"");
				contentsMainBuf.append("1");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.grouping){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目6:ソート可能フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox5\" value=\"");
				contentsMainBuf.append("1");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.sortable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目7:デフォルトソート項目（編集可能）
				contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
				contentsMainBuf.append("<div contenteditable=\"true\">");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.defaultsort != null){
					contentsMainBuf.append(tablelistcolumnInfo.defaultsort);
				}else {
					contentsMainBuf.append("&nbsp;");
				}
				contentsMainBuf.append("</div>");
				contentsMainBuf.append("</td>");

				// 表示項目8:ファイルアップロード可能フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox6\" value=\"");
				contentsMainBuf.append("1");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.uploadable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				// 表示項目9:ログ出力可能フラグ
				contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
				contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep6_checkbox7\" value=\"");
				contentsMainBuf.append("1");
				if(tablelistcolumnInfo != null && tablelistcolumnInfo.loggable){
					contentsMainBuf.append("\" checked=\"checked\">");
				}else{
					contentsMainBuf.append("\">");
				}
				contentsMainBuf.append("</td>");

				contentsMainBuf.append(createFixHtml3(true, false, false));
			}
			contentsMainBuf.append("</tbody>");
			contentsMainBuf.append("</table>");

			break;
		case STEP7:
			// Step07 地図レイヤ情報登録

			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報から地図レイヤ情報を取得
			menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<MaplayerInfo> maplayerInfoList = maplayerInfoService.findByMenuid(menuInfo.id, 0L);

			// メニュー情報からメニューテーブル情報を取得
	    	menutableInfoList = menutableInfoService.findByMenuInfoId(menuInfo.id);


	    	// 地図レイヤ情報が持つテーブルマスタ情報IDのリストを作成しておく
	    	Map<Long, MaplayerInfo> maplayerInfoHasTablemasterInfoIdMap = new HashMap<Long, MaplayerInfo>();
	    	for(MaplayerInfo maplayerInfo : maplayerInfoList){
	    		maplayerInfoHasTablemasterInfoIdMap.put(maplayerInfo.tablemasterinfoid, maplayerInfo);
	    	}

			// テーブルマスタ情報取得
	    	tablemasterInfoList = tablemasterInfoService.findByLocalgovinfoid(localgovinfoid);
			tablemasterInfoHashSet = new LinkedHashSet<TablemasterInfo>();


	    	// 表示の先頭はメニューテーブル情報に登録されたテーブルマスタ
	    	menutableInfoHasTablemasterInfoIdMap = new HashMap<Long, MenutableInfo>();
			for(MenutableInfo menutableInfoStep7 : menutableInfoList){
		    	for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
		    		if(menutableInfoStep7.tablemasterinfoid.equals(tablemasterInfo.id) && (! StringUtil.isEmpty(tablemasterInfo.layerid))){
		    			tablemasterInfoHashSet.add(tablemasterInfo);
			    		menutableInfoHasTablemasterInfoIdMap.put(menutableInfoStep7.tablemasterinfoid, menutableInfoStep7);
		    			break;
		    		}
		    	}
			}

			// メニューテーブル情報で選択したテーブルマスタがレイヤではなかった場合
			if(tablemasterInfoHashSet.size() <= 0){
				blankPage = true;
			}else{
				blankPage = false;
			}

			if(! blankPage){
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"0\">");

				//  地図レイヤ情報が存在する場合は並べ替え
				if(maplayerInfoList != null && maplayerInfoList.size() > 0){
					// 地図レイヤ情報にIDがあるテーブルマスタを先に表示させる
					for(MaplayerInfo maplayerInfo : maplayerInfoList){
				    	for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
				    		if(maplayerInfo.tablemasterinfoid.equals(tablemasterInfo.id)){
				    			tablemasterInfoHashSet.add(tablemasterInfo);
				    			break;
				    		}
				    	}
					}
					// 地図レイヤ情報にIDがないテーブルマスタは後ろに表示させる
					for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
						if((! tablemasterInfoHashSet.contains(tablemasterInfo) ) && (! StringUtil.isEmpty(tablemasterInfo.layerid)) ){
							tablemasterInfoHashSet.add(tablemasterInfo);
						}
			    	}
				}else{
					for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
						if(! StringUtil.isEmpty(tablemasterInfo.layerid) ){
							tablemasterInfoHashSet.add(tablemasterInfo);
						}
			    	}
				}

				// THEAD作成
				contentsMainBuf.append(createFixHtml1(false));
				contentsMainBuf.append(createThTag(false, false, lang.__("Menu table ID"), "menutableinfoid", null));
				contentsMainBuf.append(createThTag(false, false, lang.__("Map layer ID"), "maplayerinfoid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Table master ID"), "tablemasterid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select","menuWizard_head_select"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Initially displayed flag"), "visible","menuWizard_head_visibleFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Legend folding"), "closed", "menuWizard_head_closedFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Edit flag"), "editable", "menuWizard_head_editableFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Add flag"), "addable", "menuWizard_head_addableFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Search flag"), "searchable", "menuWizard_head_searchableFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Snap flag"), "snapable", "menuWizard_head_snapableFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Cropping layer ID"), "intersectionlayerid", "menuWizard_head_intersectionlayerid"));
				contentsMainBuf.append(createFixHtml2(true, false, false));

				contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
				// 1行目は追加時のテンプレートに使用
				contentsMainBuf.append("<tr>");
				contentsMainBuf.append(createZeroHiddenTd(17));
				contentsMainBuf.append("</tr>");

				for(TablemasterInfo tablemasterInfo : tablemasterInfoHashSet){
					MaplayerInfo maplayerInfo = null;
					if(maplayerInfoHasTablemasterInfoIdMap.containsKey(tablemasterInfo.id)){
						maplayerInfo = maplayerInfoHasTablemasterInfoIdMap.get(tablemasterInfo.id);
					}
					MenutableInfo menutableInfoStep7 = null;
					if(menutableInfoHasTablemasterInfoIdMap.containsKey(tablemasterInfo.id)){
						menutableInfoStep7 = menutableInfoHasTablemasterInfoIdMap.get(tablemasterInfo.id);
					}

					boolean hasMaplayerInfo = false;
					if(maplayerInfo != null && maplayerInfo.tablemasterinfoid.equals(tablemasterInfo.id)){
						hasMaplayerInfo = true;
					}
					boolean hasMenutableInfo = false;
					if(menutableInfoStep7 != null && menutableInfoStep7.tablemasterinfoid.equals(tablemasterInfo.id)){
						hasMenutableInfo = true;
					}

					if(hasMenutableInfo){
						contentsMainBuf.append("<tr class=\"menuWizard_contents_disable\">");
					}else{
						contentsMainBuf.append("<tr>");
					}

					String step7Checked = "\" checked=\"checked\"";
					String step7CheckboxEnd;
					if(hasMenutableInfo){
						step7CheckboxEnd = "onclick=\"return false;\">";
					}else{
						step7CheckboxEnd = ">";
					}
					String step7CheckboxEndDisable = "onclick=\"return false;\">";

					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					// 非表示項目1:テーブルマスター情報ID
					contentsMainBuf.append(tablemasterInfo.id);
					contentsMainBuf.append("</td>");
					// 非表示項目2:削除フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");
					// 非表示項目3:処理対象フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");

					// 非表示項目4:メニューテーブル情報ID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					if(menutableInfoStep7 != null){
						contentsMainBuf.append(menutableInfoStep7.id);
					}else{
						contentsMainBuf.append("0");
					}
					contentsMainBuf.append("</td>");

					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					if(maplayerInfo != null){
						contentsMainBuf.append(maplayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					contentsMainBuf.append("</td>");

					// 表示項目1:テーブルマスタID
					contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
					contentsMainBuf.append(tablemasterInfo.id);
					contentsMainBuf.append(":");
					contentsMainBuf.append(tablemasterInfo.name);
					contentsMainBuf.append("</td>");

					// 表示項目2:選択チェックボックス
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox1\" value=\"");
					if(maplayerInfo != null){
						contentsMainBuf.append(maplayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(hasMaplayerInfo || hasMenutableInfo){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					contentsMainBuf.append(step7CheckboxEnd);
					contentsMainBuf.append("</td>");


					// 表示項目3:初期表示フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox2\" value=\"");
					contentsMainBuf.append("1");
					if(maplayerInfo != null && maplayerInfo.visible){
						contentsMainBuf.append(step7Checked);
					}else if(hasMenutableInfo){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					contentsMainBuf.append(step7CheckboxEnd);
					contentsMainBuf.append("</td>");

					// 表示項目4:凡例折り畳みフラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox3\" value=\"");
					contentsMainBuf.append("1");
					if(maplayerInfo != null && maplayerInfo.closed){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					contentsMainBuf.append(step7CheckboxEnd);
					contentsMainBuf.append("</td>");


					// 表示項目5:編集可能フラグ
					if(hasMenutableInfo){
						contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					}else{
						contentsMainBuf.append("<td class=\"menuWizard_contents_disable menuWizard_contents_checkbox\">");
					}
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox4\" value=\"");
					contentsMainBuf.append("1");
					if(hasMenutableInfo){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					// 編集可能フラグは常に編集不可
					contentsMainBuf.append(step7CheckboxEndDisable);
					contentsMainBuf.append("</td>");

					// 表示項目6:追加可能フラグ
					if(hasMenutableInfo){
						contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					}else{
						contentsMainBuf.append("<td class=\"menuWizard_contents_disable menuWizard_contents_checkbox\">");
					}
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox5\" value=\"");
					contentsMainBuf.append("1");
					if(hasMenutableInfo){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					// 追加可能フラグは常に編集不可
					contentsMainBuf.append(step7CheckboxEndDisable);
					contentsMainBuf.append("</td>");

					// 表示項目9:検索フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox6\" value=\"");
					contentsMainBuf.append("1");
					if(maplayerInfo != null && maplayerInfo.searchable){
						contentsMainBuf.append(step7Checked);
					}else if(hasMenutableInfo){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					contentsMainBuf.append(step7CheckboxEnd);
					contentsMainBuf.append("</td>");

					// 表示項目9:スナップフラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep7_checkbox7\" value=\"");
					contentsMainBuf.append("1");
					if(maplayerInfo != null && maplayerInfo.snapable){
						contentsMainBuf.append(step7Checked);
					}else{
						contentsMainBuf.append("\"");
					}
					contentsMainBuf.append(step7CheckboxEnd);
					contentsMainBuf.append("</td>");

					// 表示項目10:切り出しレイヤ
					contentsMainBuf.append("<td class=\"menuWizard_contents_disable menuWizard_contents_text\">");
					contentsMainBuf.append("<div contenteditable=\"false\">");
					if(maplayerInfo != null && maplayerInfo.intersectionlayerid != null){
						contentsMainBuf.append(maplayerInfo.intersectionlayerid);
					}
					contentsMainBuf.append("</div>");
					contentsMainBuf.append("</td>");

					contentsMainBuf.append(createFixHtml3(true, false, false));
				}
				contentsMainBuf.append("</tbody>");
				contentsMainBuf.append("</table>");
			}else{
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"1\">");
				contentsMainBuf.append("<p>");
				contentsMainBuf.append(lang.__("No items to be edited on this page."));
				contentsMainBuf.append("</p>");
			}

			break;
		case STEP8:
			// Step08 地図ベースレイヤ情報登録
			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報から地図ベースレイヤ情報を取得
			menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<MapbaselayerInfo> mapbaselayerInfoList = mapbaselayerInfoService.findByMenuid(menuInfo.id);

	    	// eコミマップから地図ベースレイヤを取得
	        MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
            Map<String, String> ecommapMapbaselayerInfoMap = new HashMap<String, String>();
	        if(mapmasterInfo != null){
	        	// 地図レイヤ情報取得
	            ecommapMapbaselayerInfoMap = ecommapDataGetService.getMapUsedBaseLayersAll(mapmasterInfo.communityid, mapmasterInfo.mapgroupid, mapmasterInfo.mapid);
	        }

	        if(ecommapMapbaselayerInfoMap == null || ecommapMapbaselayerInfoMap.size() <= 0){
				blankPage = true;
			}else{
				blankPage = false;
			}


	        if(!blankPage){
		    	// 地図ベースレイヤ情報が持つ地図ベースレイヤIDのリストを作成しておく
		    	Map<String, MapbaselayerInfo> mapbaselayerIdMap = new HashMap<String, MapbaselayerInfo>();
		    	for(MapbaselayerInfo mapbaselayerInfo : mapbaselayerInfoList){
		    		mapbaselayerIdMap.put(mapbaselayerInfo.layerid, mapbaselayerInfo);
		    	}

				// 地図ベースレイヤ情報が存在する場合は並べ替え
				Map<String,String> sortedMapbaselayerIdMap = new LinkedHashMap<String,String>();
				if(mapbaselayerInfoList != null && mapbaselayerInfoList.size() > 0){
					// メニューテーブル情報にIDがあるテーブルマスタを先に表示させる
					for(MapbaselayerInfo mapbaselayerInfo : mapbaselayerInfoList){
				    	for(Map.Entry<String,String> entry : ecommapMapbaselayerInfoMap.entrySet()){
				    		if(mapbaselayerInfo.layerid.equals(entry.getKey())){
				    			sortedMapbaselayerIdMap.put(entry.getKey(), entry.getValue());
				    			break;
				    		}
				    	}
					}
					// 地図ベースレイヤ情報にレイヤIDがない地図ベースレイヤは後ろに表示させる
			    	for(Map.Entry<String,String> entry : ecommapMapbaselayerInfoMap.entrySet()){
						if(! sortedMapbaselayerIdMap.containsKey(entry.getKey())){
							sortedMapbaselayerIdMap.put(entry.getKey(), entry.getValue());
						}
			    	}
				}else{
			    	for(Map.Entry<String,String> entry : ecommapMapbaselayerInfoMap.entrySet()){
						sortedMapbaselayerIdMap.put(entry.getKey(), entry.getValue());
			    	}
				}

				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"0\">");
				// THEAD作成
				contentsMainBuf.append(createFixHtml1(false));
				contentsMainBuf.append(createThTag(false, false, lang.__("Menu table ID"), "mapbaselayerinfoid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Layer ID"), "layerid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select", "menuWizard_head_select"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Initially displayed flag"), "visible", "menuWizard_head_visibleFlag"));
				contentsMainBuf.append(createFixHtml2(true, false, false));

				contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
				// 1行目は追加時のテンプレートに使用
				contentsMainBuf.append("<tr>");
				contentsMainBuf.append(createZeroHiddenTd(10));
				contentsMainBuf.append("</tr>");

		    	for(Map.Entry<String,String> entry : sortedMapbaselayerIdMap.entrySet()){
					MapbaselayerInfo mapbaselayerInfo = null;
					if(mapbaselayerIdMap.containsKey(entry.getKey())){
						mapbaselayerInfo = mapbaselayerIdMap.get(entry.getKey());
					}

					contentsMainBuf.append("<tr>");
					// 非表示項目1:レイヤID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append(entry.getKey());
					contentsMainBuf.append("</td>");
					// 非表示項目2:削除フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");
					// 非表示項目3:作成対象フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");

					// 非表示項目4:地図ベースレイヤ情報ID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					if(mapbaselayerInfo != null){
						contentsMainBuf.append(mapbaselayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					contentsMainBuf.append("</td>");

					// 表示項目1:レイヤID
					contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
					contentsMainBuf.append(entry.getKey());
					contentsMainBuf.append(":");
					contentsMainBuf.append(entry.getValue());
					contentsMainBuf.append("</td>");

					// 表示項目2:選択チェックボックス
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep8_checkbox1\" value=\"");
					if(mapbaselayerInfo != null){
						contentsMainBuf.append(mapbaselayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapbaselayerInfo != null && mapbaselayerInfo.layerid.equals(entry.getKey())){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目3:初期表示フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep8_checkbox2\" value=\"");
					contentsMainBuf.append("1");
					if(mapbaselayerInfo != null && mapbaselayerInfo.visible){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					contentsMainBuf.append(createFixHtml3(true, false, false));
				}
				contentsMainBuf.append("</tbody>");
				contentsMainBuf.append("</table>");
	        }else{
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"1\">");
				contentsMainBuf.append("<p>");
				contentsMainBuf.append(lang.__("No items to be edited on this page."));
				contentsMainBuf.append("</p>");
	        }

			break;
		case STEP9:
			// Step09 フィルター設定情報登録
			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報からフィルター設定情報を取得
			menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<FilterInfo> menuFilterInfoList = filterInfoService.findByMenuid(menuInfo.id);

	    	// eコミマップから地図ベースレイヤを取得
	    	String getConditionListResult = "";
	        mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
	        if(mapmasterInfo != null){
	            String ecommUser;
	            if(isUsual){
	            	UnitInfo unitInfo = unitInfoService.findById(guId);
	            	ecommUser = unitInfo.ecomuser;
	            }else{
	            	GroupInfo groupInfo = groupInfoService.findById(guId);
	            	ecommUser = groupInfo.ecomuser;
	            }
		    	// 利用可能なフィルターリストを取得
	            getConditionListResult = ecommapDataGetService.getConditionList(ecommUser, new Integer(mapmasterInfo.mapid.toString()));
	        }

	        if(StringUtil.isEmpty(getConditionListResult)){
	        	blankPage = true;
	        }else{
	        	blankPage = false;
	        }

	        if(!blankPage){
		        // 利用可能なフィルターのIDリストを作成
		        List<Long> filterIdList = new ArrayList<Long>();
	        	JSONObject getConditionListResultJson = JSONObject.fromObject(getConditionListResult);
	        	if(getConditionListResultJson.containsKey("items")){
	        		JSONArray filterListJSONArray = getConditionListResultJson.getJSONArray("items");
	        		if(filterListJSONArray != null){
	        			for(int i = 0; i < filterListJSONArray.size(); i++){
	        				JSONObject filterJSON = filterListJSONArray.getJSONObject(i);
	        				filterIdList.add(Long.parseLong(filterJSON.getString("id")));
	        			}
	        		}
	        	}

		    	// フィルター情報が持つ地フィルターIDのリストを作成しておく
		    	Map<Long, FilterInfo> filterIdMap = new HashMap<Long, FilterInfo>();
		    	for(FilterInfo filterInfo : menuFilterInfoList){
		    		filterIdMap.put(filterInfo.filterid, filterInfo);
		    	}

				// フィルター情報が存在する場合は並べ替え
				Set<Long> sortedFilterIdList = new LinkedHashSet<Long>();
				if(menuFilterInfoList != null && menuFilterInfoList.size() > 0){
					// フィルター情報にIDがあるフィルターを先に表示させる
					for(FilterInfo filterInfo : menuFilterInfoList){
				    	for(Long filterId : filterIdList){
				    		if(filterInfo.filterid.equals(filterId)){
				    			sortedFilterIdList.add(filterId);
				    			break;
				    		}
				    	}
					}
					// フィルター情報にIDがないフィルターは後ろに表示させる
			    	for(Long filterId : filterIdList){
						if(! sortedFilterIdList.contains(filterId)){
							sortedFilterIdList.add(filterId);
						}
			    	}
				}else{
			    	for(Long filterId : filterIdList){
						sortedFilterIdList.add(filterId);
			    	}
				}

				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"0\">");
				// THEAD作成
				contentsMainBuf.append(createFixHtml1(false));
				contentsMainBuf.append(createThTag(false, false, lang.__("Filter info ID"), "filterinfoid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Name"), "name", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select", "menuWizard_head_select"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Filter ID"), "filterid", "menuWizard_head_filterid"));
				contentsMainBuf.append(createFixHtml2(true, false, false));

				contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
				// 1行目は追加時のテンプレートに使用
				contentsMainBuf.append("<tr>");
				contentsMainBuf.append(createZeroHiddenTd(10));
				contentsMainBuf.append("</tr>");

		    	for(Long filterId : sortedFilterIdList){
					FilterInfo filterInfo = null;
					if(filterIdMap.containsKey(filterId)){
						filterInfo = filterIdMap.get(filterId);
					}

					contentsMainBuf.append("<tr>");
					// 非表示項目1:フィルターID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append(filterId);
					contentsMainBuf.append("</td>");
					// 非表示項目2:削除フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");
					// 非表示項目3:作成対象フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");

					// 非表示項目4:フィルター情報ID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					if(filterInfo != null){
						contentsMainBuf.append(filterInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					contentsMainBuf.append("</td>");

					// 表示項目1:名称
					contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizard_contents_editableText\">");
					contentsMainBuf.append("<div contenteditable=\"true\">");
					if(filterInfo != null && filterInfo.name != null){
						contentsMainBuf.append(filterInfo.name);
					}
					contentsMainBuf.append("</div>");
					contentsMainBuf.append("</td>");

					// 表示項目2:選択チェックボックス
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep9_checkbox\" value=\"");
					if(filterInfo != null){
						contentsMainBuf.append(filterInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(filterInfo != null && filterInfo.filterid.equals(filterId)){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目3:フィルターID
					contentsMainBuf.append("<td class=\"menuWizard_contents_text menuWizardStep9_hover\">");
					contentsMainBuf.append(filterId);
					contentsMainBuf.append("</td>");

					contentsMainBuf.append(createFixHtml3(true, false, false));
				}
				contentsMainBuf.append("</tbody>");
				contentsMainBuf.append("</table>");

	        }else{
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"1\">");
				contentsMainBuf.append("<p>");
				contentsMainBuf.append(lang.__("No items to be edited on this page."));
				contentsMainBuf.append("</p>");
	        }

			break;
		case STEP10:
			// Step10 地図参照レイヤ情報登録
			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報から地図参照レイヤ情報を取得
			menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<MapreferencelayerInfo> mapreferencelayerInfoList = mapreferencelayerInfoService.findByMenuid(menuInfo.id);

	    	// eコミマップから地図参照レイヤを取得
	    	Map<String, String> ecommapReferencelayerMap = new HashMap<String, String>();
	        mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
	        if(mapmasterInfo != null){
	            Map<MapLayerInfo,List<MapLayerInfo>> mapLayerInfos;
	            mapLayerInfos = ecommapDataGetService.getMapUsedReferenceLayersAll(mapmasterInfo.communityid, mapmasterInfo.mapgroupid, mapmasterInfo.mapid);
	            for(MapLayerInfo mapLayerInfo : mapLayerInfos.keySet()){
	            	ecommapReferencelayerMap.put(mapLayerInfo.layerId, mapLayerInfo.layerName);
	            }
	        }

	        if(ecommapReferencelayerMap == null || ecommapReferencelayerMap.size() <= 0){
	        	blankPage = true;
	        }else{
	        	blankPage = false;
	        }

	        if(!blankPage){
		    	// 地図参照レイヤ情報が持つレイヤIDのリストを作成しておく
		    	Map<String, MapreferencelayerInfo> mapreferencelayerMap = new HashMap<String, MapreferencelayerInfo>();
		    	for(MapreferencelayerInfo mapreferencelayerInfo : mapreferencelayerInfoList){
		    		mapreferencelayerMap.put(mapreferencelayerInfo.layerid, mapreferencelayerInfo);
		    	}

				// 地図参照レイヤ情報が存在する場合は並べ替え
				Map<String, String> sortedEcommapRapreferencelayerMap = new LinkedHashMap<String, String>();
				if(mapreferencelayerInfoList != null && mapreferencelayerInfoList.size() > 0){
					// 地図参照レイヤ情報にIDがあるレイヤを先に表示させる
					for(MapreferencelayerInfo mapreferencelayerInfo : mapreferencelayerInfoList){
				    	for(Map.Entry<String, String> entry: ecommapReferencelayerMap.entrySet()){
				    		if(mapreferencelayerInfo.layerid.equals(entry.getKey())){
				    			sortedEcommapRapreferencelayerMap.put(entry.getKey(), entry.getValue());
				    			break;
				    		}
				    	}
					}
					// 地図参照レイヤ情報にIDがないレイヤは後ろに表示させる
			    	for(Map.Entry<String, String> entry: ecommapReferencelayerMap.entrySet()){
						if(! sortedEcommapRapreferencelayerMap.containsKey(entry.getKey())){
							sortedEcommapRapreferencelayerMap.put(entry.getKey(), entry.getValue());
						}
			    	}
				}else{
			    	for(Map.Entry<String, String> entry: ecommapReferencelayerMap.entrySet()){
						sortedEcommapRapreferencelayerMap.put(entry.getKey(), entry.getValue());
			    	}
				}

				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"0\">");
				// THEAD作成
				contentsMainBuf.append(createFixHtml1(false));
				contentsMainBuf.append(createThTag(false, false, lang.__("Map reference info ID"), "mapreferencelayerinfoid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Layer ID"), "layerid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select","menuWizard_head_select"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Initially displayed flag"), "visible", "menuWizard_head_visibleFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Legend folding"), "closed", "menuWizard_head_closedFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Search flag"), "searchable", "menuWizard_head_searchableFlag"));
				contentsMainBuf.append(createFixHtml2(true, false, false));

				contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
				// 1行目は追加時のテンプレートに使用
				contentsMainBuf.append("<tr>");
				contentsMainBuf.append(createZeroHiddenTd(12));
				contentsMainBuf.append("</tr>");

		    	for(Map.Entry<String, String> entry: sortedEcommapRapreferencelayerMap.entrySet()){
					MapreferencelayerInfo mapreferencelayerInfo = null;
					if(mapreferencelayerMap.containsKey(entry.getKey())){
						mapreferencelayerInfo = mapreferencelayerMap.get(entry.getKey());
					}

					contentsMainBuf.append("<tr>");
					// 非表示項目1:レイヤID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append(entry.getKey());
					contentsMainBuf.append("</td>");
					// 非表示項目2:削除フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");
					// 非表示項目3:作成対象フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");

					// 非表示項目4:地図参照レイヤ情報ID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					if(mapreferencelayerInfo != null){
						contentsMainBuf.append(mapreferencelayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					contentsMainBuf.append("</td>");

					// 表示項目1:レイヤID
					contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
					contentsMainBuf.append(entry.getKey());
					contentsMainBuf.append(":");
					contentsMainBuf.append(entry.getValue());
					contentsMainBuf.append("</td>");

					// 表示項目2:選択チェックボックス
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox1\" value=\"");
					if(mapreferencelayerInfo != null){
						contentsMainBuf.append(mapreferencelayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapreferencelayerInfo != null && mapreferencelayerInfo.layerid.equals(entry.getKey())){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目3:初期表示フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox2\" value=\"");
					if(mapreferencelayerInfo != null){
						contentsMainBuf.append(mapreferencelayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapreferencelayerInfo != null && mapreferencelayerInfo.visible){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目4:凡例折り畳みフラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox3\" value=\"");
					if(mapreferencelayerInfo != null){
						contentsMainBuf.append(mapreferencelayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapreferencelayerInfo != null && mapreferencelayerInfo.closed){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目5:検索フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox4\" value=\"");
					if(mapreferencelayerInfo != null){
						contentsMainBuf.append(mapreferencelayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapreferencelayerInfo != null && mapreferencelayerInfo.searchable){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					contentsMainBuf.append(createFixHtml3(true, false, false));
				}
				contentsMainBuf.append("</tbody>");
				contentsMainBuf.append("</table>");
	        }else{
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"1\">");
				contentsMainBuf.append("<p>");
				contentsMainBuf.append(lang.__("No items to be edited on this page."));
				contentsMainBuf.append("</p>");
	        }

			break;
		case STEP11:
			// Step11 地図KMLレイヤ情報登録
			step3JsonData = jsonData.getJSONObject("step3Data");
			menutaskmenuinfoId = step3JsonData.getString("radio");
			menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));

			// メニュー情報から地図KMLレイヤ情報を取得
			menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
	    	List<MapkmllayerInfo> mapkmllayerInfoList = mapkmllayerInfoService.findByMenuid(menuInfo.id);

	    	// eコミマップから地図KMLレイヤを取得
	    	Map<String, String> ecommapKmllayerMap = new HashMap<String, String>();
	        mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
	        if(mapmasterInfo != null){
	        	// 地図レイヤ情報取得
	            Map<MapLayerInfo,List<MapLayerInfo>> mapLayerInfos;
	            mapLayerInfos = ecommapDataGetService.getMapUsedKMLLayersAll(mapmasterInfo.communityid, mapmasterInfo.mapgroupid, mapmasterInfo.mapid);
	            for(MapLayerInfo mapLayerInfo : mapLayerInfos.keySet()){
	            	ecommapKmllayerMap.put(mapLayerInfo.layerId, mapLayerInfo.layerName);
	            }
	        }

	        if(ecommapKmllayerMap == null || ecommapKmllayerMap.size() <= 0){
	        	blankPage = true;
	        }else{
	        	blankPage = false;
	        }

	        if(!blankPage){
		    	// 地図KMLレイヤ情報が持つレイヤIDのリストを作成しておく
		    	Map<String, MapkmllayerInfo> mapkmllayerMap = new HashMap<String, MapkmllayerInfo>();
		    	for(MapkmllayerInfo mapkmllayerInfo : mapkmllayerInfoList){
		    		mapkmllayerMap.put(mapkmllayerInfo.layerid, mapkmllayerInfo);
		    	}

				// 地図KMLレイヤ情報が存在する場合は並べ替え
				Map<String, String> sortedEcommapKmllayerMap = new LinkedHashMap<String, String>();
				if(mapkmllayerInfoList != null && mapkmllayerInfoList.size() > 0){
					// 地図KMLレイヤ情報にIDがあるレイヤを先に表示させる
					for(MapkmllayerInfo mapkmllayerInfo : mapkmllayerInfoList){
				    	for(Map.Entry<String, String> entry: ecommapKmllayerMap.entrySet()){
				    		if(mapkmllayerInfo.layerid.equals(entry.getKey())){
				    			sortedEcommapKmllayerMap.put(entry.getKey(), entry.getValue());
				    			break;
				    		}
				    	}
					}
					// 地図KMLレイヤ情報にIDがないレイヤは後ろに表示させる
			    	for(Map.Entry<String, String> entry: ecommapKmllayerMap.entrySet()){
						if(! sortedEcommapKmllayerMap.containsKey(entry.getKey())){
							sortedEcommapKmllayerMap.put(entry.getKey(), entry.getValue());
						}
			    	}
				}else{
			    	for(Map.Entry<String, String> entry: ecommapKmllayerMap.entrySet()){
			    		sortedEcommapKmllayerMap.put(entry.getKey(), entry.getValue());
			    	}
				}

				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"0\">");
				// THEAD作成
				contentsMainBuf.append(createFixHtml1(false));
				contentsMainBuf.append(createThTag(false, false, lang.__("Map KML info ID"), "mapkmllayerinfoid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Layer ID"), "layerid", null));
				contentsMainBuf.append(createThTag(true, false, lang.__("Select"), "select", "menuWizard_head_select"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Initially displayed flag"), "visible", "menuWizard_head_visibleFlag"));
				contentsMainBuf.append(createThTag(true, true, lang.__("Search flag"), "searchable", "menuWizard_head_searchableFlag"));
				contentsMainBuf.append(createFixHtml2(true, false, false));

				contentsMainBuf.append("<tbody class=\"menuWizard_contents_tbody\">");
				// 1行目は追加時のテンプレートに使用
				contentsMainBuf.append("<tr>");
				contentsMainBuf.append(createZeroHiddenTd(11));
				contentsMainBuf.append("</tr>");

		    	for(Map.Entry<String, String> entry: sortedEcommapKmllayerMap.entrySet()){
		    		MapkmllayerInfo mapkmllayerInfo = null;
					if(mapkmllayerMap.containsKey(entry.getKey())){
						mapkmllayerInfo = mapkmllayerMap.get(entry.getKey());
					}

					contentsMainBuf.append("<tr>");
					// 非表示項目1:レイヤID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append(entry.getKey());
					contentsMainBuf.append("</td>");
					// 非表示項目2:削除フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");
					// 非表示項目3:作成対象フラグ（未使用）
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					contentsMainBuf.append("0");
					contentsMainBuf.append("</td>");

					// 非表示項目4:地図KMLレイヤ情報ID
					contentsMainBuf.append("<td class=\"menuWizard_contents_hidden\">");
					if(mapkmllayerInfo != null){
						contentsMainBuf.append(mapkmllayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					contentsMainBuf.append("</td>");

					// 表示項目1:レイヤID
					contentsMainBuf.append("<td class=\"menuWizard_contents_text\">");
					contentsMainBuf.append(entry.getKey());
					contentsMainBuf.append(":");
					contentsMainBuf.append(entry.getValue());
					contentsMainBuf.append("</td>");

					// 表示項目2:選択チェックボックス
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox1\" value=\"");
					if(mapkmllayerInfo != null){
						contentsMainBuf.append(mapkmllayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapkmllayerInfo != null && mapkmllayerInfo.layerid.equals(entry.getKey())){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目3:初期表示フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox2\" value=\"");
					if(mapkmllayerInfo != null){
						contentsMainBuf.append(mapkmllayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapkmllayerInfo != null && mapkmllayerInfo.visible){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					// 表示項目4:検索フラグ
					contentsMainBuf.append("<td class=\"menuWizard_contents_checkbox\">");
					contentsMainBuf.append("<input type=\"checkbox\" name=\"menuWizardStep10_checkbox3\" value=\"");
					if(mapkmllayerInfo != null){
						contentsMainBuf.append(mapkmllayerInfo.id);
					}else{
						contentsMainBuf.append("0");
					}
					if(mapkmllayerInfo != null && mapkmllayerInfo.searchable){
						contentsMainBuf.append("\" checked=\"checked\">");
					}else{
						contentsMainBuf.append("\">");
					}
					contentsMainBuf.append("</td>");

					contentsMainBuf.append(createFixHtml3(true, false, false));
				}
				contentsMainBuf.append("</tbody>");
				contentsMainBuf.append("</table>");
	        }else{
				contentsMainBuf.append("<input type=\"hidden\" id=\"menuWizard_blankpage\" name=\"menuWizard_blankpage\" value=\"1\">");
				contentsMainBuf.append("<p>");
				contentsMainBuf.append(lang.__("No items to be edited on this page."));
				contentsMainBuf.append("</p>");
	        }
			break;
		case STEP12:
			// Finish
			break;
		default:
			break;
		}

		return contentsMainBuf.toString();
	}

	/**
	 * ウイザード各ページでのDB更新処理
	 * @param nextStep
	 * @param baseData
	 * @param isUsual
	 * @param guId
	 * @return
	 */
	private JSONObject dbUpdate(int nextStep, JSONObject baseData, boolean isUsual, long localgovinfoid, long guId){
		JSONArray tablesJsonArray = baseData.getJSONArray("editTableData");

		boolean allGroupOrUnit = false;
		if(baseData != null && baseData.containsKey("step0Data")){
			JSONObject step0JsonData = baseData.getJSONObject("step0Data");
			String allGroupOrUnitStr = step0JsonData.getString("radio");
			if(allGroupOrUnitStr.equals("all")){
				allGroupOrUnit = true;
			}
		}

		boolean blankPage = false;
		if(baseData.containsKey("blankPage")){
			String blankPageFlag = baseData.getString("blankPage");
			if(blankPageFlag.equals("1")){
				blankPage = true;
			}
		}

		try{
			try{
				switch (nextStep) {
				case STEPM2:
				case STEPM1:
				case STEP0:
					// 初期化中なので不要
					return baseData;
				case STEP1:
					// ウイザード第一ページなので不要
					return baseData;
				case STEP2:
					// メニュープロセス情報の追加／更新／削除

					// メニューログイン情報のリストを作成する
					// 全ての班に適用する場合は自治体IDでの検索結果
					// 自班のみ適用の場合は自班のメニューログイン情報のみ
					List<MenuloginInfo> menuloginInfoList = new ArrayList<MenuloginInfo>();
					if(allGroupOrUnit){
						menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
					}else{
						// ログインモード（平時／災害）判定
						MenuloginInfo menuloginInfo = null;
						if(isUsual){
							menuloginInfo = menuloginInfoService.findByUnitInfoIdAndNotDeleted(guId);
						}else{
							menuloginInfo = menuloginInfoService.findByGroupInfoIdAndNotDeleted(guId);
						}
						menuloginInfoList.add(menuloginInfo);
					}

					if(tablesJsonArray != null){
						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){

							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String idStr = rowJson.getString("id");
							String deleted = rowJson.getString("column_deleted");
							String name = rowJson.getString("name");
							Boolean important = rowJson.getBoolean("important");
							Integer disporder = new Integer(i);

							//新規登録
							if(idStr.equals("0") && deleted.equals("0")){
								for(MenuloginInfo menuloginInfo : menuloginInfoList){
									MenuprocessInfo menuprocessInfo = new MenuprocessInfo();
									menuprocessInfo.disporder = disporder;
									menuprocessInfo.deleted = false;
									menuprocessInfo.important = important;
									menuprocessInfo.menulogininfoid = menuloginInfo.id;
									menuprocessInfo.name = name;
									menuprocessInfo.valid = true;
									menuprocessInfo.visible = true;
									menuprocessInfoService.insert(menuprocessInfo);

									JSONObject step1Data = baseData.getJSONObject("step1Data");
									if(isUsual){
										if(menuloginInfo.unitid.equals(guId)){
											step1Data.put("radio", menuprocessInfo.id);
											baseData.put("step1Data", step1Data);
										}
									}else{
										if(menuloginInfo.groupid.equals(guId)){
											step1Data.put("radio", menuprocessInfo.id);
											baseData.put("step1Data", step1Data);
										}
									}
								}
							}else{
								// 更新
								if(deleted.equals("0")){
									MenuprocessInfo myMenuprocessInfo = menuprocessInfoService.findById(Long.parseLong(idStr));
									// 変更がなければ更新しない
									if( ! myMenuprocessInfo.disporder.equals(disporder)
										|| ! myMenuprocessInfo.important.equals(important)
										|| ! myMenuprocessInfo.name.equals(name)
									){
										if(allGroupOrUnit){
											// 他班の同じ名称のメニュープロセス情報を更新する
											for(MenuloginInfo menuloginInfo : menuloginInfoList){
												List<MenuprocessInfo> menuprocessInfoList = menuprocessInfoService.findByMenuloginInfoId(menuloginInfo.id);
												for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
													if(menuprocessInfo.name.equals(myMenuprocessInfo.name)){
														menuprocessInfo.disporder = disporder;
														menuprocessInfo.important = important;
														menuprocessInfo.name = name;
														menuprocessInfoService.update(menuprocessInfo);
													}
												}
											}
										}else{
											myMenuprocessInfo.disporder = disporder;
											myMenuprocessInfo.important = important;
											myMenuprocessInfo.name = name;
											menuprocessInfoService.update(myMenuprocessInfo);
										}
									}
								// 削除
								}else{
									deleteCascade(allGroupOrUnit, nextStep, baseData, idStr, isUsual, localgovinfoid, guId);
								}
							}
						}
					}

					break;
				case STEP3:
					// メニュータスク情報の追加／更新／削除
					if(tablesJsonArray != null){
						JSONObject step1JsonData = baseData.getJSONObject("step1Data");
						String menuprocessinfoId = step1JsonData.getString("radio");
						MenuprocessInfo myMenuprocessInfo = menuprocessInfoService.findById(Long.parseLong(menuprocessinfoId));

						// メニュープロセス情報のリストを作成する
						// 全ての班に適用する場合は自治体に紐づくもののうち、STEP1で選択したメニュープロセス情報と名称が一致するもの
						// 自班のみ適用の場合はSTEP1で選択したメニュープロセス情報
						menuloginInfoList = new ArrayList<MenuloginInfo>();
						List<MenuprocessInfo> menuprocessInfoList = new ArrayList<MenuprocessInfo>();
						if(allGroupOrUnit){
							menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
							for(MenuloginInfo menuloginInfo : menuloginInfoList){
								List<MenuprocessInfo> menuprocessInfoTempList = menuprocessInfoService.findByMenuloginInfoId(menuloginInfo.id);
								for(MenuprocessInfo menuprocessInfo : menuprocessInfoTempList){
									if(menuprocessInfo.name.equals(myMenuprocessInfo.name)){
										menuprocessInfoList.add(menuprocessInfo);
									}
								}
							}
						}else{
							menuprocessInfoList.add(myMenuprocessInfo);
						}

						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){
							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String idStr = rowJson.getString("id");
							String deleted = rowJson.getString("column_deleted");
							String menutasktypeinfoidStr = rowJson.getString("tasktype");
							String name = rowJson.getString("name");
							Boolean important = rowJson.getBoolean("important");
							Integer disporder = new Integer(i);

							//新規登録
							if(idStr.equals("0") && deleted.equals("0")){
								for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
									MenutaskInfo menutaskInfo = new MenutaskInfo();
									menutaskInfo.disporder = disporder;
									menutaskInfo.deleted = false;
									menutaskInfo.important = important;
									menutaskInfo.menuprocessinfoid = menuprocessInfo.id;
									menutaskInfo.menutasktypeinfoid = Long.parseLong(menutasktypeinfoidStr);
									menutaskInfo.name = name;
									menutaskInfo.valid = true;
									menutaskInfo.visible = true;
									menutaskInfoService.insert(menutaskInfo);

									JSONObject step2Data = baseData.getJSONObject("step2Data");
									if(myMenuprocessInfo.name.equals(menuprocessInfo.name)){
										step2Data.put("radio", menutaskInfo.id);
										baseData.put("step2Data", step2Data);
									}
								}
							}else{
								// 更新
								if(deleted.equals("0")){
									MenutaskInfo myMenutaskInfo = menutaskInfoService.findById(Long.parseLong(idStr));
									// 変更がなければ更新しない
									if(! myMenutaskInfo.disporder.equals(disporder)
										|| ! myMenutaskInfo.important.equals(important)
										|| ! myMenutaskInfo.menutasktypeinfoid.equals(Long.parseLong(menutasktypeinfoidStr))
										|| ! myMenutaskInfo.name.equals(name)
									){
										if(allGroupOrUnit){
											// 他班の同じ名称のメニュータスク情報を更新する
											for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
												BeanMap menutaskInfoCondition = new BeanMap();
												menutaskInfoCondition.put(MenutaskInfoNames.menuprocessinfoid().toString(), menuprocessInfo.id);
												menutaskInfoCondition.put(MenutaskInfoNames.name().toString(), myMenutaskInfo.name);
												List<MenutaskInfo> menutaskInfoList = menutaskInfoService.findByCondition(menutaskInfoCondition);
												for(MenutaskInfo menutaskInfo : menutaskInfoList){
													menutaskInfo.disporder = disporder;
													menutaskInfo.important = important;
													menutaskInfo.menutasktypeinfoid = Long.parseLong(menutasktypeinfoidStr);
													menutaskInfo.name = name;
													menutaskInfoService.update(menutaskInfo);
												}
											}
										}else{
											myMenutaskInfo.disporder = disporder;
											myMenutaskInfo.important = important;
											myMenutaskInfo.menutasktypeinfoid = Long.parseLong(menutasktypeinfoidStr);
											myMenutaskInfo.name = name;
											menutaskInfoService.update(myMenutaskInfo);
										}
									}
								// 削除
								}else{
									deleteCascade(allGroupOrUnit, nextStep, baseData, idStr, isUsual, localgovinfoid, guId);
								}
							}
						}
					}
					break;
				case STEP4:
					// タスクメニュー情報とメニュー情報の追加／更新／削除
					JSONObject step2JsonData = baseData.getJSONObject("step2Data");
					String menutaskinfoId = step2JsonData.getString("radio");
					MenutaskInfo myMenutaskInfo = menutaskInfoService.findById(Long.parseLong(menutaskinfoId));
					MenuprocessInfo myMenuprocessInfo = menuprocessInfoService.findById(myMenutaskInfo.menuprocessinfoid);

					// メニュータスク情報のリストを作成する
					// 全ての班に適用する場合は自治体に紐づくもののうち、STEP2で選択したメニュータスク情報と名称が一致するもの
					// 自班のみ適用の場合はSTEP2で選択したメニュータスク情報
					menuloginInfoList = new ArrayList<MenuloginInfo>();
					List<MenutaskInfo> menutaskInfoList = new ArrayList<MenutaskInfo>();
					if(allGroupOrUnit){
						menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
						for(MenuloginInfo menuloginInfo : menuloginInfoList){
							List<MenuprocessInfo> menuprocessInfoTempList = menuprocessInfoService.findByMenuloginInfoId(menuloginInfo.id);
							for(MenuprocessInfo menuprocessInfo : menuprocessInfoTempList){
								if(menuprocessInfo.name.equals(myMenuprocessInfo.name)){
									BeanMap menutaskInfoCondition = new BeanMap();
									menutaskInfoCondition.put(MenutaskInfoNames.menuprocessinfoid().toString(), menuprocessInfo.id);
									menutaskInfoCondition.put(MenutaskInfoNames.menutasktypeinfoid().toString(), myMenutaskInfo.menutasktypeinfoid);
									menutaskInfoCondition.put(MenutaskInfoNames.name().toString(), myMenutaskInfo.name);
									List<MenutaskInfo> menutaskInfoTempList = menutaskInfoService.findByCondition(menutaskInfoCondition);
									if(menutaskInfoTempList != null){
										menutaskInfoList.addAll(menutaskInfoTempList);
									}
								}
							}
						}
					}else{
						menutaskInfoList.add(myMenutaskInfo);
					}

					// 処理時間文字列作成
					Timestamp nowdate = new Timestamp(System.currentTimeMillis());
					String execTimeStr = new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM"), lang.getLocale()).format(nowdate);
					String execUserStr;
					if(isUsual){
						UnitInfo unitInfo = unitInfoService.findById(guId);
						execUserStr = unitInfo.name + lang.__("Unit<!--2-->");

					}else{
						GroupInfo groupInfo = groupInfoService.findById(guId);
						execUserStr = groupInfo.name + lang.__("Group");
					}

					// 先頭行はテンプレートなのでループは1から。
					for(int i = 1; i < tablesJsonArray.size(); i++){
						// 新規作成メッセージ
						String addMessage = lang.__("{0} {1} newly created by wizard function.", execTimeStr, execUserStr);
						// 更新メッセージ
						String editMessage = lang.__("{0} {1} updated by wizard function.", execTimeStr, execUserStr);
						// 削除メッセージ
						String delMessage = lang.__("{0} {1} deleted by wizard function.", execTimeStr, execUserStr);

						JSONObject rowJson = tablesJsonArray.getJSONObject(i);
						String idStr = rowJson.getString("id");
						String deleted = rowJson.getString("column_deleted");
						Boolean target = rowJson.getBoolean("column_target");
						String menutypeidStr = rowJson.getString("menutype");
						String name = rowJson.getString("name");
						Integer disporder = new Integer(i);
						String excellisttemplatetempfile = rowJson.getString("excellisttemplatetempfile");

						//新規登録
						if(idStr.equals("0") && deleted.equals("0")){
							// メニュー情報作成
							MenuInfo menuInfo = new MenuInfo();
							menuInfo.visible = true;
							menuInfo.valid = true;
							menuInfo.name = name;
							menuInfo.deleted = false;
							menuInfo.menutypeid = Integer.parseInt(menutypeidStr);
							menuInfo.menutasktypeinfoid = myMenutaskInfo.menutasktypeinfoid;
							menuInfo.note = addMessage;
							menuInfoService.insert(menuInfo);

							for(MenutaskInfo menutaskInfo : menutaskInfoList){
								// タスクメニュー情報新規作成
								MenutaskmenuInfo menutaskmenuInfo = new MenutaskmenuInfo();
								menutaskmenuInfo.disporder = disporder;
								menutaskmenuInfo.important = false;
								menutaskmenuInfo.menuinfoid = menuInfo.id;
								menutaskmenuInfo.menutaskinfoid = menutaskInfo.id;
								menutaskmenuInfoService.insert(menutaskmenuInfo);

								JSONObject step3Data = baseData.getJSONObject("step3Data");
								if(myMenutaskInfo.name.equals(menutaskInfo.name)){
									step3Data.put("radio", menutaskmenuInfo.id);
									baseData.put("step3Data", step3Data);
								}
							}
							moveExcellisttemplatetempfile(localgovinfoid, excellisttemplatetempfile, menuInfo, false);
						}else{
							MenutaskmenuInfo myMnutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(idStr));
							MenuInfo myMenuInfo = menuInfoService.findById(myMnutaskmenuInfo.menuinfoid);
							// 更新
							if(deleted.equals("0")){
								if(allGroupOrUnit){

									// 変更がなければ更新しない
									if(! myMenuInfo.name.equals(name)
										|| ! myMenuInfo.menutypeid.equals(Integer.parseInt(menutypeidStr))
									){
										myMenuInfo.name = name;
										myMenuInfo.menutypeid = Integer.parseInt(menutypeidStr);
										myMenuInfo.note = editMessage;
										menuInfoService.update(myMenuInfo);
									}

									List<MenutaskmenuInfo> menutaskmenuInfoList = menutaskmenuInfoService.findByMenuInfoId(myMenuInfo.id);
									for(MenutaskmenuInfo menutaskmenuInfo : menutaskmenuInfoList){
										// 変更がなければ更新しない
										if(! menutaskmenuInfo.disporder.equals(disporder)){
											menutaskmenuInfo.disporder = disporder;
											menutaskmenuInfoService.update(myMnutaskmenuInfo);
										}
									}
								}else{
									// 他の班・課で使用していないかチェック
									if(isOtherUsedMenuInfo(myMenuInfo.id, isUsual, localgovinfoid, guId)){
										// 複製メッセージ
										String copyMessage = lang.__("{0} {1} copied from menu ID:{2} by wizard function.", execTimeStr, execUserStr, myMenuInfo.id);

										// 他の班・課で使用されているメニュー情報がラジオボタンが選択されていた場合は
										// メニュー情報を複製し、以後そちらを使用する
										if(target){
											MenuInfo copyMenuInfo = new MenuInfo();
											copyMenuInfo.visible = true;
											copyMenuInfo.valid = true;
											copyMenuInfo.name = name;
											copyMenuInfo.deleted = false;
											copyMenuInfo.menutypeid = Integer.parseInt(menutypeidStr);
											copyMenuInfo.menutasktypeinfoid = myMenutaskInfo.menutasktypeinfoid;
											copyMenuInfo.note = copyMessage;
											menuInfoService.insert(copyMenuInfo);
											// メニュー情報の子要素を複製
											cloneMenuInfoChiledlen(myMenuInfo, copyMenuInfo);

											myMnutaskmenuInfo.disporder = disporder;
											myMnutaskmenuInfo.menuinfoid = copyMenuInfo.id;
											menutaskmenuInfoService.update(myMnutaskmenuInfo);

										// ラジオボタンが選択されていない場合は変更があった場合のみメニュー情報を複製しておく
										}else{
											// 変更がなければ更新しない
											if(! myMnutaskmenuInfo.disporder.equals(disporder)){
												myMnutaskmenuInfo.disporder = disporder;
												menutaskmenuInfoService.update(myMnutaskmenuInfo);
											}
											if(! myMenuInfo.name.equals(name)
													|| ! myMenuInfo.menutypeid.equals(Integer.parseInt(menutypeidStr))
											){
												// 他の班・課で使用されていた場合は新規にメニュー情報を作成する
												MenuInfo copyMenuInfo = new MenuInfo();
												copyMenuInfo.visible = true;
												copyMenuInfo.valid = true;
												copyMenuInfo.name = name;
												copyMenuInfo.deleted = false;
												copyMenuInfo.menutypeid = Integer.parseInt(menutypeidStr);
												copyMenuInfo.menutasktypeinfoid = myMenutaskInfo.menutasktypeinfoid;
												copyMenuInfo.note = copyMessage;
												menuInfoService.insert(copyMenuInfo);
												// メニュー情報の子要素を複製
												cloneMenuInfoChiledlen(myMenuInfo, copyMenuInfo);

												myMnutaskmenuInfo.disporder = disporder;
												myMnutaskmenuInfo.menuinfoid = copyMenuInfo.id;
												menutaskmenuInfoService.update(myMnutaskmenuInfo);
											}
										}
									}else{
										// 変更がなければ更新しない
										if(! myMnutaskmenuInfo.disporder.equals(disporder)){
											myMnutaskmenuInfo.disporder = disporder;
											menutaskmenuInfoService.update(myMnutaskmenuInfo);
										}

										if(! myMenuInfo.name.equals(name)
											|| ! myMenuInfo.menutypeid.equals(Integer.parseInt(menutypeidStr))
										){
											myMenuInfo.name = name;
											myMenuInfo.menutypeid = Integer.parseInt(menutypeidStr);
											myMenuInfo.note = editMessage;
											menuInfoService.update(myMenuInfo);
										}
									}
								}
								moveExcellisttemplatetempfile(localgovinfoid, excellisttemplatetempfile, myMenuInfo, false);

							}else{
								// 削除
								deleteCascade(allGroupOrUnit,  nextStep, baseData, idStr, isUsual, localgovinfoid, guId);
								moveExcellisttemplatetempfile(localgovinfoid, excellisttemplatetempfile, myMenuInfo, true);
							}

						}
					}
					break;
				case STEP5:
					// ページボタン表示情報の追加／更新／削除
					JSONObject step3JsonData = baseData.getJSONObject("step3Data");
					String menutaskmenuinfoId = step3JsonData.getString("radio");
					MenutaskmenuInfo menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
					MenuInfo menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

					// 先頭行はテンプレートなのでループは1から。
					for(int i = 1; i < tablesJsonArray.size(); i++){
						JSONObject rowJson = tablesJsonArray.getJSONObject(i);
						String pagebuttonMasterStr = rowJson.getString("id");
						int pagebuttonMasterId = Integer.parseInt(pagebuttonMasterStr);
						String pagemenubuttonInfoStr = rowJson.getString("menuButtonid");
						Boolean select = rowJson.getBoolean("select");
						Boolean enable = rowJson.getBoolean("enable");
						Integer disporder = new Integer(i);

						if(pagemenubuttonInfoStr.equals("0") && select){
							//新規登録
							PagemenubuttonInfo pagemenubuttonInfo = new PagemenubuttonInfo();
							pagemenubuttonInfo.enable = enable;
							pagemenubuttonInfo.menuinfoid = menuInfo.id;
							pagemenubuttonInfo.pagebuttonid = pagebuttonMasterId;
							pagemenubuttonInfo.disporder = disporder;
							pagemenubuttonInfoService.insert(pagemenubuttonInfo);

						}else if( (!pagemenubuttonInfoStr.equals("0")) && select ){
							// 更新
							long pagemenubuttonInfoId = Long.parseLong(pagemenubuttonInfoStr);
							PagemenubuttonInfo pagemenubuttonInfo = pagemenubuttonInfoService.findById(pagemenubuttonInfoId);
							// 変更がなければ更新しない
							if(! pagemenubuttonInfo.enable.equals(enable)
								|| ! pagemenubuttonInfo.disporder.equals(disporder)
							){
								pagemenubuttonInfo.enable = enable;
								pagemenubuttonInfo.disporder = disporder;
								pagemenubuttonInfoService.update(pagemenubuttonInfo);
							}
						}else if( (!pagemenubuttonInfoStr.equals("0")) && (!select) ){
							// 削除
							deleteCascade(allGroupOrUnit,  nextStep, baseData, pagemenubuttonInfoStr, isUsual, localgovinfoid, guId);
						}
					}

					break;
				case STEP6:
					// メニューテーブル情報の追加／更新／削除
					step3JsonData = baseData.getJSONObject("step3Data");
					menutaskmenuinfoId = step3JsonData.getString("radio");
					menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
					menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

					// 先頭行はテンプレートなのでループは1から。
					for(int i = 1; i < tablesJsonArray.size(); i++){

						JSONObject rowJson = tablesJsonArray.getJSONObject(i);
						String tablemasterinfoIdStr = rowJson.getString("id");
						long tablemasterinfoId = Long.parseLong(tablemasterinfoIdStr);
						String menutableInfoIdStr = rowJson.getString("menutableinfoid");
						Boolean select = rowJson.getBoolean("select");
						Boolean addable = rowJson.getBoolean("addable");
						Boolean deletable = rowJson.getBoolean("deletable");
						Boolean totalable = rowJson.getBoolean("totalable");

						if(menutableInfoIdStr.equals("0") && select){
							//新規登録
							MenutableInfo menutableInfo = new MenutableInfo();
							menutableInfo.addable = addable;
							menutableInfo.deletable = deletable;
							menutableInfo.deleted = false;
							menutableInfo.menuinfoid = menuInfo.id;
							menutableInfo.tablemasterinfoid = tablemasterinfoId;
							menutableInfo.totalable = totalable;
							menutableInfoService.insert(menutableInfo);
							JSONObject step5Data = baseData.getJSONObject("step5Data");
							step5Data.put("radio", menutableInfo.id);
							baseData.put("step5Data", step5Data);

						}else if( (!menutableInfoIdStr.equals("0")) && select ){
							// 更新
							long menutableInfoId = Long.parseLong(menutableInfoIdStr);
							MenutableInfo menutableInfo = menutableInfoService.findById(menutableInfoId);
							// 変更がなければ更新しない
							if(! menutableInfo.addable.equals(addable)
								|| ! menutableInfo.deletable.equals(deletable)
								|| ! menutableInfo.menuinfoid.equals(menuInfo.id)
								|| ! menutableInfo.tablemasterinfoid.equals(tablemasterinfoId)
								|| ! menutableInfo.totalable.equals(totalable)
							){
								menutableInfo.addable = addable;
								menutableInfo.deletable = deletable;
								menutableInfo.menuinfoid = menuInfo.id;
								menutableInfo.tablemasterinfoid = tablemasterinfoId;
								menutableInfo.totalable = totalable;
								menutableInfoService.update(menutableInfo);
							}
						}else if( (!menutableInfoIdStr.equals("0")) && (!select) ){
							// 削除
							deleteCascade(allGroupOrUnit, nextStep, baseData, menutableInfoIdStr, isUsual, localgovinfoid, guId);
						}
					}

					break;
				case STEP7:
					// テーブルリスト項目情報の追加／更新／削除
					JSONObject step5JsonData = baseData.getJSONObject("step5Data");
					String menutableinfoId = step5JsonData.getString("radio");
					MenutableInfo menutableInfo = menutableInfoService.findById(Long.parseLong(menutableinfoId));

					// レイヤ属性リスト作成
					Map<String, String> attrInfoMap = createAttrInfoList(menutableInfo);

					// 先頭行はテンプレートなのでループは1から。
					for(int i = 1; i < tablesJsonArray.size(); i++){
						JSONObject rowJson = tablesJsonArray.getJSONObject(i);
						String attrId = rowJson.getString("id");
						String tablelistcolumninfoIdStr = rowJson.getString("tablelistcolumninfoid");
						Boolean select = rowJson.getBoolean("select");
						Boolean editable = rowJson.getBoolean("editable");
						Boolean highlight = rowJson.getBoolean("highlight");
						Boolean grouping = rowJson.getBoolean("grouping");
						Boolean sortable = rowJson.getBoolean("sortable");
						String defaultsortStr = rowJson.getString("defaultsort");
						Boolean uploadable = rowJson.getBoolean("uploadable");
						Boolean loggable = rowJson.getBoolean("loggable");
						Integer disporder = new Integer(i);

						defaultsortStr = defaultsortStr.trim();
						StringBuffer dSbuf = new StringBuffer();
						char [] d = defaultsortStr.toCharArray();
						for(int i2 = 0; i2 < d.length; i2++) {
							int ascii = (int) d[i2];
							if(ascii != 160) {
								dSbuf.append(d[i2]);
							}
						}
						
						if(tablelistcolumninfoIdStr.equals("0") && select){
							//新規登録
							TablelistcolumnInfo tablelistcolumnInfo = new TablelistcolumnInfo();
							tablelistcolumnInfo.addable = false;
							tablelistcolumnInfo.attrid = attrId;
							tablelistcolumnInfo.defaultcheck = false;
							tablelistcolumnInfo.defaultsort = Integer.parseInt(dSbuf.toString());
							tablelistcolumnInfo.disporder = disporder;
							tablelistcolumnInfo.editable = editable;
							tablelistcolumnInfo.groupdefaultcheck = false;
							tablelistcolumnInfo.grouping = grouping;
							tablelistcolumnInfo.highlight = highlight;
							tablelistcolumnInfo.loggable = loggable;
							tablelistcolumnInfo.menutableinfoid = menutableInfo.id;
							tablelistcolumnInfo.name = attrInfoMap.get(tablelistcolumnInfo.attrid);
							tablelistcolumnInfo.sortable = sortable;
							tablelistcolumnInfo.uploadable = uploadable;
							tablelistcolumnInfoService.insert(tablelistcolumnInfo);
						}else if( (!tablelistcolumninfoIdStr.equals("0")) && select ){
							// 更新
							long tablelistcolumninfoId = Long.parseLong(tablelistcolumninfoIdStr);
							TablelistcolumnInfo tablelistcolumnInfo = tablelistcolumnInfoService.findById(tablelistcolumninfoId);
							// 変更がなければ更新しない
							if(! tablelistcolumnInfo.attrid.equals(attrId)
								|| ! tablelistcolumnInfo.defaultsort.equals(defaultsortStr)
								|| ! tablelistcolumnInfo.disporder.equals(disporder)
								|| ! tablelistcolumnInfo.editable.equals(editable)
								|| ! tablelistcolumnInfo.grouping.equals(grouping)
								|| ! tablelistcolumnInfo.highlight.equals(highlight)
								|| ! tablelistcolumnInfo.loggable.equals(loggable)
								|| ! tablelistcolumnInfo.menutableinfoid.equals(menutableInfo.id)
								|| ! tablelistcolumnInfo.name.equals(attrInfoMap.get(tablelistcolumnInfo.attrid))
								|| ! tablelistcolumnInfo.sortable.equals(sortable)
								|| ! tablelistcolumnInfo.uploadable.equals(uploadable)
							){
								tablelistcolumnInfo.attrid = attrId;
								tablelistcolumnInfo.defaultsort = Integer.parseInt(defaultsortStr);
								tablelistcolumnInfo.disporder = disporder;
								tablelistcolumnInfo.editable = editable;
								tablelistcolumnInfo.grouping = grouping;
								tablelistcolumnInfo.highlight = highlight;
								tablelistcolumnInfo.loggable = loggable;
								tablelistcolumnInfo.menutableinfoid = menutableInfo.id;
								tablelistcolumnInfo.name = attrInfoMap.get(tablelistcolumnInfo.attrid);
								tablelistcolumnInfo.sortable = sortable;
								tablelistcolumnInfo.uploadable = uploadable;
								tablelistcolumnInfoService.update(tablelistcolumnInfo);
							}

						}else if( (!tablelistcolumninfoIdStr.equals("0")) && (!select) ){
							// 削除
							deleteCascade(allGroupOrUnit, nextStep, baseData, tablelistcolumninfoIdStr, isUsual, localgovinfoid, guId);
						}
					}

					break;
				case STEP8:
					// 地図レイヤ情報の追加／更新／削除
					if(!blankPage){
						step3JsonData = baseData.getJSONObject("step3Data");
						menutaskmenuinfoId = step3JsonData.getString("radio");
						menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
						menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){

							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String tablemasterinfoIdStr = rowJson.getString("id");
							long tablemasterinfoId = Long.parseLong(tablemasterinfoIdStr);
							String menutableinfoIdStr = rowJson.getString("menutableinfoid");
							String maplayerinfoidStr = rowJson.getString("maplayerinfoid");
							Boolean select = rowJson.getBoolean("select");
							Boolean visible = rowJson.getBoolean("visible");
							Boolean closed = rowJson.getBoolean("closed");
							Boolean editable = rowJson.getBoolean("editable");
							Boolean addable = rowJson.getBoolean("addable");
							Boolean searchable = rowJson.getBoolean("searchable");
							Boolean snapable = rowJson.getBoolean("snapable");
							String intersectionlayerid = rowJson.getString("intersectionlayerid");
							Integer disporder = new Integer(i);

							if(maplayerinfoidStr.equals("0") && select){
								//新規登録
								MaplayerInfo maplayerInfo = new MaplayerInfo();
								maplayerInfo.addable = addable;
								maplayerInfo.closed = closed;
								maplayerInfo.deleted = false;
								maplayerInfo.disporder = disporder;
								maplayerInfo.editable = editable;
								maplayerInfo.intersectionlayerid = intersectionlayerid;
								maplayerInfo.menuinfoid = menuInfo.id;
								maplayerInfo.searchable = searchable;
								maplayerInfo.snapable = snapable;
								maplayerInfo.tablemasterinfoid = tablemasterinfoId;
								maplayerInfo.valid = true;
								maplayerInfo.visible = visible;
								maplayerInfoService.insert(maplayerInfo);

								// 地図レイヤ属性情報作成
								// テーブルリスト項目情報と設定が同じデータを作成する
								List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(Long.parseLong(menutableinfoIdStr));
								for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){
									MaplayerattrInfo maplayerattrInfo = new MaplayerattrInfo();
									maplayerattrInfo.addable = tablelistcolumnInfo.addable;
									maplayerattrInfo.attrid = tablelistcolumnInfo.attrid;
									maplayerattrInfo.defaultcheck = tablelistcolumnInfo.defaultcheck;
									maplayerattrInfo.disporder = tablelistcolumnInfo.disporder;
									maplayerattrInfo.editable = tablelistcolumnInfo.editable;
									maplayerattrInfo.groupdefaultcheck = tablelistcolumnInfo.groupdefaultcheck;
									maplayerattrInfo.grouping = tablelistcolumnInfo.grouping;
									maplayerattrInfo.highlight = tablelistcolumnInfo.highlight;
									maplayerattrInfo.name = tablelistcolumnInfo.name;
									maplayerattrInfo.maplayerinfoid = maplayerInfo.id;
									maplayerattrInfoService.insert(maplayerattrInfo);
								}



							}else if( (!maplayerinfoidStr.equals("0")) && select ){
								// 更新
								long maplayerinfoId = Long.parseLong(maplayerinfoidStr);
								MaplayerInfo maplayerInfo = maplayerInfoService.findById(maplayerinfoId);
								// 変更がなければ更新しない
								if( ! maplayerInfo.addable.equals(addable)
									|| ! maplayerInfo.closed.equals(closed)
									|| ! maplayerInfo.disporder.equals(disporder)
									|| ! maplayerInfo.editable.equals(editable)
									|| ! maplayerInfo.intersectionlayerid.equals(intersectionlayerid)
									|| ! maplayerInfo.menuinfoid.equals(menuInfo.id)
									|| ! maplayerInfo.searchable.equals(searchable)
									|| ! maplayerInfo.snapable.equals(snapable)
									|| ! maplayerInfo.tablemasterinfoid.equals(tablemasterinfoId)
									|| ! maplayerInfo.visible.equals(visible)
								){
									maplayerInfo.addable = addable;
									maplayerInfo.closed = closed;
									maplayerInfo.disporder = disporder;
									maplayerInfo.editable = editable;
									maplayerInfo.intersectionlayerid = intersectionlayerid;
									maplayerInfo.menuinfoid = menuInfo.id;
									maplayerInfo.searchable = searchable;
									maplayerInfo.snapable = snapable;
									maplayerInfo.tablemasterinfoid = tablemasterinfoId;
									maplayerInfo.visible = visible;
									maplayerInfoService.update(maplayerInfo);
								}

							}else if( (!maplayerinfoidStr.equals("0")) && (!select) ){
								// 削除
								deleteCascade(allGroupOrUnit, nextStep, baseData, maplayerinfoidStr, isUsual, localgovinfoid, guId);
							}
						}
					}

					break;
				case STEP9:
					// 地図ベースレイヤ情報の追加／更新／削除
					if(!blankPage){
						step3JsonData = baseData.getJSONObject("step3Data");
						menutaskmenuinfoId = step3JsonData.getString("radio");
						menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
						menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){

							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String mapbaselayerId = rowJson.getString("id");
							String mapbaselayerInfoIdStr = rowJson.getString("mapbaselayerinfoid");
							Boolean select = rowJson.getBoolean("select");
							Boolean visible = rowJson.getBoolean("visible");
							Integer disporder = new Integer(i);

							if(mapbaselayerInfoIdStr.equals("0") && select){
								//新規登録
								MapbaselayerInfo mapbaselayerInfo = new MapbaselayerInfo();
								mapbaselayerInfo.disporder = disporder;
								mapbaselayerInfo.layerid = mapbaselayerId;
								mapbaselayerInfo.menuinfoid = menuInfo.id;
								mapbaselayerInfo.valid = true;
								mapbaselayerInfo.visible = visible;
								mapbaselayerInfoService.insert(mapbaselayerInfo);
							}else if( (!mapbaselayerInfoIdStr.equals("0")) && select ){
								// 更新
								long mapbaselayerInfoId = Long.parseLong(mapbaselayerInfoIdStr);
								MapbaselayerInfo mapbaselayerInfo = mapbaselayerInfoService.findById(mapbaselayerInfoId);
								// 変更がなければ更新しない
								if(! mapbaselayerInfo.layerid.equals(mapbaselayerId)
									|| ! mapbaselayerInfo.visible.equals(visible)
									|| ! mapbaselayerInfo.disporder.equals(disporder)
								){
									mapbaselayerInfo.layerid = mapbaselayerId;
									mapbaselayerInfo.visible = visible;
									mapbaselayerInfo.disporder = disporder;
									mapbaselayerInfoService.update(mapbaselayerInfo);
								}
							}else if( (!mapbaselayerInfoIdStr.equals("0")) && (!select) ){
								// 削除
								deleteCascade(allGroupOrUnit, nextStep, baseData, mapbaselayerInfoIdStr, isUsual, localgovinfoid, guId);
							}
						}
					}

					break;
				case STEP10:
					// フィルター設定情報の追加／更新／削除
					if(!blankPage){
						step3JsonData = baseData.getJSONObject("step3Data");
						menutaskmenuinfoId = step3JsonData.getString("radio");
						menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
						menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){

							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String filterIdStr = rowJson.getString("filterid");
							String filterInfoIdStr = rowJson.getString("filterinfoid");
							Boolean select = rowJson.getBoolean("select");
							String name = rowJson.getString("name");
							Integer disporder = new Integer(i);

							if(filterInfoIdStr.equals("0") && select){
								//新規登録
								FilterInfo filterInfo = new FilterInfo();
								filterInfo.disporder = disporder;
								filterInfo.filterid = Long.parseLong(filterIdStr);
								filterInfo.menuinfoid = menuInfo.id;
								filterInfo.name = name;
								filterInfo.note = "";
								filterInfo.valid = true;
								filterInfoService.insert(filterInfo);
							}else if( (!filterInfoIdStr.equals("0")) && select ){
								// 更新
								long filterInfoId = Long.parseLong(filterInfoIdStr);
								FilterInfo filterInfo = filterInfoService.findById(filterInfoId);
								// 変更がなければ更新しない
								if(! filterInfo.disporder.equals(disporder)
									|| ! filterInfo.filterid.equals(Long.parseLong(filterIdStr))
									|| ! filterInfo.name.equals(name)
								){
									filterInfo.disporder = disporder;
									filterInfo.filterid = Long.parseLong(filterIdStr);
									filterInfo.name = name;
									filterInfoService.update(filterInfo);
								}

							}else if( (!filterInfoIdStr.equals("0")) && (!select) ){
								// 削除
								deleteCascade(allGroupOrUnit, nextStep, baseData, filterInfoIdStr, isUsual, localgovinfoid, guId);
							}
						}
					}
					break;
				case STEP11:
					// 地図参照レイヤ情報の追加／更新／削除
					if(!blankPage){
						step3JsonData = baseData.getJSONObject("step3Data");
						menutaskmenuinfoId = step3JsonData.getString("radio");
						menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
						menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){

							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String layerId = rowJson.getString("id");
							String mapreferencelayerInfoIdStr = rowJson.getString("mapreferencelayerinfoid");
							Boolean select = rowJson.getBoolean("select");
							Boolean closed = rowJson.getBoolean("closed");
							Boolean visible = rowJson.getBoolean("visible");
							Boolean searchable = rowJson.getBoolean("searchable");
							Integer disporder = new Integer(i);

							if(mapreferencelayerInfoIdStr.equals("0") && select){
								//新規登録
								MapreferencelayerInfo mapreferencelayerInfo = new MapreferencelayerInfo();
								mapreferencelayerInfo.closed = closed;
								mapreferencelayerInfo.disporder = disporder;
								mapreferencelayerInfo.layerid = layerId;
								mapreferencelayerInfo.menuinfoid  = menuInfo.id;
								mapreferencelayerInfo.searchable = searchable;
								mapreferencelayerInfo.visible = visible;
								mapreferencelayerInfo.valid = true;
								mapreferencelayerInfoService.insert(mapreferencelayerInfo);
							}else if( (!mapreferencelayerInfoIdStr.equals("0")) && select ){
								// 更新
								long mapreferencelayerInfoId = Long.parseLong(mapreferencelayerInfoIdStr);
								MapreferencelayerInfo mapreferencelayerInfo = mapreferencelayerInfoService.findById(mapreferencelayerInfoId);
								// 変更がなければ更新しない
								if(! mapreferencelayerInfo.closed.equals(closed)
									|| ! mapreferencelayerInfo.disporder.equals(disporder)
									|| ! mapreferencelayerInfo.layerid.equals(layerId)
									|| ! mapreferencelayerInfo.searchable.equals(searchable)
									|| ! mapreferencelayerInfo.visible.equals(visible)
								){
									mapreferencelayerInfo.closed = closed;
									mapreferencelayerInfo.disporder = disporder;
									mapreferencelayerInfo.layerid = layerId;
									mapreferencelayerInfo.searchable = searchable;
									mapreferencelayerInfo.visible = visible;
									mapreferencelayerInfoService.update(mapreferencelayerInfo);
								}

							}else if( (!mapreferencelayerInfoIdStr.equals("0")) && (!select) ){
								// 削除
								deleteCascade(allGroupOrUnit, nextStep, baseData, mapreferencelayerInfoIdStr, isUsual, localgovinfoid, guId);
							}
						}
					}
					break;
				case STEP12:
					// 地図KMLレイヤ情報の追加／更新／削除
					if(!blankPage){
						step3JsonData = baseData.getJSONObject("step3Data");
						menutaskmenuinfoId = step3JsonData.getString("radio");
						menutaskmenuInfo = menutaskmenuInfoService.findById(Long.parseLong(menutaskmenuinfoId));
						menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);

						// 先頭行はテンプレートなのでループは1から。
						for(int i = 1; i < tablesJsonArray.size(); i++){

							JSONObject rowJson = tablesJsonArray.getJSONObject(i);
							String layerId = rowJson.getString("id");
							String mapkmllayerInfoIdStr = rowJson.getString("mapkmllayerinfoid");
							Boolean select = rowJson.getBoolean("select");
							Boolean visible = rowJson.getBoolean("visible");
							Boolean searchable = rowJson.getBoolean("searchable");
							Integer disporder = new Integer(i);

							if(mapkmllayerInfoIdStr.equals("0") && select){
								//新規登録
								MapkmllayerInfo mapkmllayerInfo = new MapkmllayerInfo();
								mapkmllayerInfo.closed = true;
								mapkmllayerInfo.disporder = disporder;
								mapkmllayerInfo.layerid = layerId;
								mapkmllayerInfo.menuinfoid  = menuInfo.id;
								mapkmllayerInfo.searchable = searchable;
								mapkmllayerInfo.visible = visible;
								mapkmllayerInfo.valid = true;
								mapkmllayerInfoService.insert(mapkmllayerInfo);
							}else if( (!mapkmllayerInfoIdStr.equals("0")) && select ){
								// 更新
								long mapkmllayerInfoId = Long.parseLong(mapkmllayerInfoIdStr);
								MapkmllayerInfo mapkmllayerInfo = mapkmllayerInfoService.findById(mapkmllayerInfoId);
								// 変更がなければ更新しない
								if(! mapkmllayerInfo.disporder.equals(disporder)
									|| ! mapkmllayerInfo.layerid.equals(layerId)
									|| ! mapkmllayerInfo.searchable.equals(searchable)
									|| ! mapkmllayerInfo.visible.equals(visible)
								){
									mapkmllayerInfo.disporder = disporder;
									mapkmllayerInfo.layerid = layerId;
									mapkmllayerInfo.searchable = searchable;
									mapkmllayerInfo.visible = visible;
									mapkmllayerInfoService.update(mapkmllayerInfo);
								}
							}else if( (!mapkmllayerInfoIdStr.equals("0")) && (!select) ){
								// 削除
								deleteCascade(allGroupOrUnit, nextStep, baseData, mapkmllayerInfoIdStr, isUsual, localgovinfoid, guId);
							}
						}
					}
					break;
				default:
					break;
				}
			}catch(Exception e){
				userTransaction.setRollbackOnly();

				String exceptionMesasge = e.getMessage();
				if(StringUtil.isEmpty(e.getMessage())){
					exceptionMesasge = "";
				}
				baseData.put("dbError", true);
				baseData.put("dbErrorClass", e.getClass().getName());
				baseData.put("dbErrorMessage", exceptionMesasge);
			}finally{
			}
		}catch(SystemException se){
		}

		return baseData;

	}

	/**
	 * ウイザード各ステップでのDBレコード削除処理
	 * @param nextStep
	 * @param jsonData
	 * @param idStr
	 * @param isUsual
	 * @param guId
	 */
	private void deleteCascade(boolean allGroupOrUnit, int nextStep, JSONObject jsonData, String idStr, boolean isUsual, long localgovinfoid, long guId){

		// 削除された新規登録レコードの場合は何もしない。
		if(StringUtil.isEmpty(idStr) || idStr.equals("0")){
			return;
		}

		switch (nextStep) {
		case STEPM2:
		case STEPM1:
		case STEP0:
			break;
		case STEP1:
			break;
		case STEP2:
			// 削除対象 (*)は論理削除
			// MenuprocessInfo(*)
			//   MenutaskInfo(*)
			//     MenutaskmenuInfo
			// 以下はやらない
			// MenuInfo(*)
			//   PagemenubuttonInfo
			//   MaplayerInfo(*)
			//     MaplayerattrInfo
			//   MapbaselayerInfo
			//   MenutableInfo(*)
			//     TablelistcolumnInfo
			//       TablerowstyleInfo
			//   SummarylistcolumnInfo
			//   MeteolayerInfo
			//   ExternalmapdataInfo
			//   ExternaltabledataInfo
			//   MenumapInfo
			//   NoticedefaultInfo
			//     NoticedefaultgroupInfo
			//     AlarmdefaultgroupInfo
			//   FilterInfo
			//   MapreferencelayerInfo
			//   MapkmllayerInfo

			long menuprocessInfoId = Long.parseLong(idStr);
			MenuprocessInfo myMenuprocessInfo = menuprocessInfoService.findById(menuprocessInfoId);
			List<MenuloginInfo> menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
			// 全班が対象の場合は自治体のメニューログイン情報を全て取得
			if(allGroupOrUnit){
				menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
			}else{
				menuloginInfoList.add(menuloginInfoService.findById(myMenuprocessInfo.menulogininfoid));
			}
			for(MenuloginInfo menuloginInfo : menuloginInfoList){
				BeanMap menuprocessInfoCondition = new BeanMap();
				// 現在選択しているメニュープロセス情報と名称が一致するものを削除する
				menuprocessInfoCondition.put(MenuprocessInfoNames.menulogininfoid().toString(), menuloginInfo.id);
				menuprocessInfoCondition.put(MenuprocessInfoNames.name().toString(), myMenuprocessInfo.name);
				List<MenuprocessInfo> menuprocessInfoList = menuprocessInfoService.findByCondition(menuprocessInfoCondition);
				for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
				    BeanMap step2Condition = new BeanMap();
				    step2Condition.put(MenutaskInfoNames.menuprocessinfoid().toString(), menuprocessInfo.id);
					List<MenutaskInfo> menutaskInfoList = menutaskInfoService.findByCondition(step2Condition);
					for(MenutaskInfo menutaskInfo: menutaskInfoList){
						step2Condition.clear();
						step2Condition.put(MenutaskmenuInfoNames.menutaskinfoid().toString(), menutaskInfo.id);
					    List<MenutaskmenuInfo> menutaskmenuInfoList = menutaskmenuInfoService.findByCondition(step2Condition);
					    for(MenutaskmenuInfo menutaskmenuInfo : menutaskmenuInfoList){
					    	// メニュー情報とその子テーブルの削除
//					    	MenuInfo menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
//					    	if(! isOtherUsedMenuInfo(menuInfo.id, isUsual, guId)){
//					    		deleteMenuInfo(menuInfo);
//					    	}
					    	menutaskmenuInfoService.delete(menutaskmenuInfo);
					    }
					    menutaskInfo.deleted = true;
					    menutaskInfoService.update(menutaskInfo);
					}
					menuprocessInfo.deleted = true;
					menuprocessInfoService.update(menuprocessInfo);
				}
			}

			break;
		case STEP3:
			// 削除対象 (*)は論理削除
			// MenutaskInfo(*)
			//     MenutaskmenuInfo
			// 以下はやらない
			// MenuInfo(*)
			//   PagemenubuttonInfo
			//   MaplayerInfo(*)
			//     MaplayerattrInfo
			//   MapbaselayerInfo
			//   MenutableInfo(*)
			//     TablelistcolumnInfo
			//       TablerowstyleInfo
			//   SummarylistcolumnInfo
			//   MeteolayerInfo
			//   ExternalmapdataInfo
			//   ExternaltabledataInfo
			//   MenumapInfo
			//   NoticedefaultInfo
			//     NoticedefaultgroupInfo
			//     AlarmdefaultgroupInfo
			//   FilterInfo
			//   MapreferencelayerInfo
			//   MapkmllayerInfo
			long menutaskInfoId = Long.parseLong(idStr);
			MenutaskInfo myMenutaskInfo = menutaskInfoService.findById(menutaskInfoId);
			myMenuprocessInfo = menuprocessInfoService.findById(myMenutaskInfo.menuprocessinfoid);
			menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
			// 全班が対象の場合は自治体のメニューログイン情報を全て取得
			if(allGroupOrUnit){
				menuloginInfoList = menuloginInfoService.findByLocalgovinfoid(localgovinfoid);
			}else{
				menuloginInfoList.add(menuloginInfoService.findById(myMenuprocessInfo.menulogininfoid));
			}
			for(MenuloginInfo menuloginInfo : menuloginInfoList){
				BeanMap menuprocessInfoCondition = new BeanMap();
				menuprocessInfoCondition.put(MenuprocessInfoNames.menulogininfoid().toString(), menuloginInfo.id);
				List<MenuprocessInfo> menuprocessInfoList = menuprocessInfoService.findByCondition(menuprocessInfoCondition);
				for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
					BeanMap menutaskInfoCondition = new BeanMap();
					// 現在選択しているメニュータスク情報と名称が一致するものを削除する
					menutaskInfoCondition.put(MenutaskInfoNames.menuprocessinfoid().toString(), menuprocessInfo.id);
					menutaskInfoCondition.put(MenutaskInfoNames.name().toString(), myMenutaskInfo.name);
					List<MenutaskInfo> menutaskInfoList = menutaskInfoService.findByCondition(menutaskInfoCondition);
					for(MenutaskInfo menutaskInfo: menutaskInfoList){
					    BeanMap step3Condition = new BeanMap();
					    step3Condition.put(MenutaskmenuInfoNames.menutaskinfoid().toString(), menutaskInfo.id);
					    List<MenutaskmenuInfo> menutaskmenuInfoList = menutaskmenuInfoService.findByCondition(step3Condition);
					    for(MenutaskmenuInfo menutaskmenuInfo : menutaskmenuInfoList){
					    	// メニュー情報とその子テーブルの削除
//					    	MenuInfo menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
//					    	if(! isOtherUsedMenuInfo(menuInfo.id, isUsual, guId)){
//					    		deleteMenuInfo(menuInfo);
//					    	}
					    	menutaskmenuInfoService.delete(menutaskmenuInfo);
					    }
					    menutaskInfo.deleted = true;
					    menutaskInfoService.update(menutaskInfo);
					}
				}
			}

			break;
		case STEP4:
			// 削除対象 (*)は論理削除
			// MenutaskmenuInfo
			// 以下はやらない
			// MenuInfo(*)
			//   PagemenubuttonInfo
			//   MaplayerInfo(*)
			//     MaplayerattrInfo
			//   MapbaselayerInfo
			//   MenutableInfo(*)
			//     TablelistcolumnInfo
			//       TablerowstyleInfo
			//   SummarylistcolumnInfo
			//   MeteolayerInfo
			//   ExternalmapdataInfo
			//   ExternaltabledataInfo
			//   MenumapInfo
			//   NoticedefaultInfo
			//     NoticedefaultgroupInfo
			//     AlarmdefaultgroupInfo
			//   FilterInfo
			//   MapreferencelayerInfo
			//   MapkmllayerInfo
			long menutaskmenuInfoId = Long.parseLong(idStr);
			MenutaskmenuInfo myMenutaskmenuInfo = menutaskmenuInfoService.findById(menutaskmenuInfoId);
			if(allGroupOrUnit){
				List<MenutaskmenuInfo> menutaskmenuInfoList = menutaskmenuInfoService.findByMenuInfoId(myMenutaskmenuInfo.menuinfoid);
				for(MenutaskmenuInfo menutaskmenuInfo : menutaskmenuInfoList){
				    menutaskmenuInfoService.delete(menutaskmenuInfo);
				}
			}else{
			    menutaskmenuInfoService.delete(myMenutaskmenuInfo);
			}
			// メニュー情報とその子テーブルの削除
//		    MenuInfo menuInfo = menuInfoService.findById(menutaskmenuInfo.menuinfoid);
//	    	if(! isOtherUsedMenuInfo(menuInfo.id, isUsual, guId)){
//	    		deleteMenuInfo(menuInfo);
//	    	}


			break;
		case STEP5:
			// 削除対象 (*)は論理削除
			// PagemenubuttonInfo
			long pagemenubuttonInfoId = Long.parseLong(idStr);
			PagemenubuttonInfo pagemenubuttonInfo = pagemenubuttonInfoService.findById(pagemenubuttonInfoId);
			pagemenubuttonInfoService.delete(pagemenubuttonInfo);

			break;
		case STEP6:
			// 削除対象 (*)は論理削除
			// MenutableInfo(*)
			//   TablelistcolumnInfo
			//     TablerowstyleInfo
			long menutableInfoId = Long.parseLong(idStr);
			MenutableInfo menutableInfo = menutableInfoService.findById(menutableInfoId);
			List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(menutableInfo.id);
			for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){
				List<TablerowstyleInfo> tablerowstyleInfoList = tablerowstyleInfoService.findByTablelistcolumnInfoId(tablelistcolumnInfo.id);
				for(TablerowstyleInfo tablerowstyleInfo : tablerowstyleInfoList){
					tablerowstyleInfoService.delete(tablerowstyleInfo);
				}
				tablelistcolumnInfoService.delete(tablelistcolumnInfo);
			}
			menutableInfo.deleted = true;
			menutableInfoService.update(menutableInfo);

			break;
		case STEP7:
			// 削除対象 (*)は論理削除
			// TablelistcolumnInfo
			//   TablerowstyleInfo
			long tablelistcolumnInfoId = Long.parseLong(idStr);
			TablelistcolumnInfo tablelistcolumnInfo = tablelistcolumnInfoService.findById(tablelistcolumnInfoId);
			List<TablerowstyleInfo> tablerowstyleInfoList = tablerowstyleInfoService.findByTablelistcolumnInfoId(tablelistcolumnInfo.id);
			for(TablerowstyleInfo tablerowstyleInfo : tablerowstyleInfoList){
				tablerowstyleInfoService.delete(tablerowstyleInfo);
			}
			tablelistcolumnInfoService.delete(tablelistcolumnInfo);

			break;
		case STEP8:
			// 削除対象 (*)は論理削除
			// MaplayerInfo(*)
			//   MaplayerattrInfo
			long maplayerInfoId = Long.parseLong(idStr);
			MaplayerInfo maplayerInfo = maplayerInfoService.findById(maplayerInfoId);
		    BeanMap step6Condition = new BeanMap();
		    step6Condition.put(MaplayerattrInfoNames.maplayerinfoid().toString(), maplayerInfo.id);
			List<MaplayerattrInfo> maplayerattrInfoList = maplayerattrInfoService.findByCondition(step6Condition);
			for(MaplayerattrInfo maplayerattrInfo : maplayerattrInfoList){
				maplayerattrInfoService.delete(maplayerattrInfo);
			}
			maplayerInfo.deleted = true;
			maplayerInfoService.update(maplayerInfo);

			break;
		case STEP9:
			// 削除対象 (*)は論理削除
			// MapbaselayerInfo
			long mapbaselayerInfoId = Long.parseLong(idStr);
			MapbaselayerInfo mapbaselayerInfo = mapbaselayerInfoService.findById(mapbaselayerInfoId);
			mapbaselayerInfoService.delete(mapbaselayerInfo);

			break;
		case STEP10:
			// 削除対象 (*)は論理削除
			// FilterInfo
			long filterInfoId = Long.parseLong(idStr);
			FilterInfo filterInfo = filterInfoService.findById(filterInfoId);
			filterInfoService.delete(filterInfo);

			break;
		case STEP11:
			// 削除対象 (*)は論理削除
			// MapreferencelayerInfo
			long mapreferencelayerInfoId = Long.parseLong(idStr);
			MapreferencelayerInfo mapreferencelayerInfo = mapreferencelayerInfoService.findById(mapreferencelayerInfoId);
			mapreferencelayerInfoService.delete(mapreferencelayerInfo);

			break;
		case STEP12:
			// 削除対象 (*)は論理削除
			// MapkmllayerInfo
			long mapkmllayerInfoId = Long.parseLong(idStr);
			MapkmllayerInfo mapkmllayerInfo = mapkmllayerInfoService.findById(mapkmllayerInfoId);
			mapkmllayerInfoService.delete(mapkmllayerInfo);

			break;
		default:
			break;
		}
	}

	/**
	 * 空かつ不可視のTD要素を作成
	 * @param count
	 * @return
	 */
	private String createZeroHiddenTd(int count){
		StringBuffer sbuf = new StringBuffer();
		for(int i = 0; i < count; i++){
			sbuf.append("<td class=\"menuWizard_contents_hidden\">");
			sbuf.append("0");
			sbuf.append("</td>");
		}

		return sbuf.toString();
	}

	/**
	 * TH要素を作成
	 * @param visible
	 * @param label
	 * @param hiddenLabel
	 * @return
	 */
	private String createThTag(boolean visible, boolean balloon, String label, String hiddenLabel, String className){
		StringBuffer sbuf = new StringBuffer();
		if(visible){
			if(StringUtil.isEmpty(className)){
				sbuf.append("<th>");
			}else{
				sbuf.append("<th class=\"");
				sbuf.append(className);
				sbuf.append("\">");
			}
			sbuf.append("<span class=\"menuWizard_contents_columnName\">");
		}else{
			sbuf.append("<th class=\"menuWizard_contents_hidden\">");
			sbuf.append("<span>");
		}
		sbuf.append(label);
		sbuf.append("</span>");

		// バルーンチップ表示アイコン
		if(balloon){
			sbuf.append("<span class='menuWizardHelp ui-icon ui-icon-help' style='display: inline-block;'></span>");
		}

		// JSONで送信するカラム名
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">");
		sbuf.append(hiddenLabel);
		sbuf.append("</span>");
		sbuf.append("</th>");

		return sbuf.toString();
	}

	/**
	 * 固定HTML作成1 <THEAD>部のID、削除フラグ、選択ラジオボタン
	 * @param targetRadio
	 * @return
	 */
	private String createFixHtml1(boolean targetRadio){
		StringBuffer sbuf = new StringBuffer();

		sbuf.append("<table class=\"menuWizard_contents_table\">");
		sbuf.append("<thead class=\"menuWizard_contents_thead\">");
		sbuf.append("<tr>");

		sbuf.append("<th class=\"menuWizard_contents_hidden\">");
		sbuf.append("<span class=\"menuWizard_contents_columnName\">ID</span>");
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">id</span>");
		sbuf.append("</th>");

		sbuf.append("<th class=\"menuWizard_contents_hidden\">");
		sbuf.append("<span class=\"menuWizard_contents_columnName\">deleted</span>");
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">column_deleted</span>");
		sbuf.append("</th>");

		if(targetRadio){
			sbuf.append("<th class=\"menuWizard_head_target\">");
		}else{
			sbuf.append("<th class=\"menuWizard_contents_hidden\">");
		}
		sbuf.append("<span class=\"menuWizard_contents_columnName\">");
		sbuf.append(lang.__("Editing target"));
		sbuf.append("</span>");
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">column_target</span>");
		sbuf.append("</th>");

		return sbuf.toString();
	}

	/**
	 * 固定HTML作成2 <THEAD>部の上下ボタンと追加ボタン、削除ボタン
	 * @param disporderButton
	 * @param addButton
	 * @param deleteButton
	 * @return
	 */
	private String createFixHtml2(boolean disporderButton, boolean addButton, boolean deleteButton){
		StringBuffer sbuf = new StringBuffer();
		if(disporderButton){
			sbuf.append("<th class=\"menuWizard_head_disporder\">");
		}else{
			sbuf.append("<th class=\"menuWizard_contents_hidden\">");
		}
		sbuf.append("<span class=\"menuWizard_contents_columnName\">");
		sbuf.append(lang.__("Display order"));
		sbuf.append("</span>");
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">disporder</span>");
		sbuf.append("</th>");

		if(addButton){
			sbuf.append("<th class=\"menuWizard_head_addButton\">");
		}else{
			sbuf.append("<th class=\"menuWizard_contents_hidden\">");
		}
		sbuf.append("<span class=\"menuWizard_contents_columnName\">");
		sbuf.append(lang.__("Add"));
		sbuf.append("</span>");
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">button_add</span>");
		sbuf.append("</th>");

		if(deleteButton){
			sbuf.append("<th class=\"menuWizard_head_delButton\">");
		}else{
			sbuf.append("<th class=\"menuWizard_contents_hidden\">");
		}
		sbuf.append("<span class=\"menuWizard_contents_columnName\">");
		sbuf.append(lang.__("Delete"));
		sbuf.append("</span>");
		sbuf.append("<span class=\"menuWizard_contents_hidden menuWizard_contents_columnName\">button_delete</span>");
		sbuf.append("</th>");

		sbuf.append("</tr>");
		sbuf.append("</thead>");

		return sbuf.toString();
	}

	/**
	 * 固定HTML作成3 <TBODY>部の上下ボタンと追加ボタン、削除ボタン
	 * @param upDown
	 * @param addButton
	 * @param delButton
	 * @return
	 */
	private String createFixHtml3(boolean upDown, boolean addButton, boolean delButton){
		StringBuffer sbuf = new StringBuffer();

		if(upDown){
			sbuf.append("<td>");
			sbuf.append("<span style='display: inline-block;' class='menuWizard_contents_lineup ui-icon ui-icon-circle-triangle-n'></span>");
			sbuf.append(lang.__("upward"));
			sbuf.append("<span style='display: inline-block;' class='menuWizard_contents_linedown ui-icon ui-icon-circle-triangle-s'></span>");
			sbuf.append(lang.__("downward"));
			sbuf.append("</td>");
		}else{
			sbuf.append("<td class=\"menuWizard_contents_hidden\">");
			sbuf.append("<span style='display: inline-block;' class='menuWizard_contents_lineup ui-icon ui-icon-circle-triangle-n'></span>");
			sbuf.append(lang.__("upward"));
			sbuf.append("<span style='display: inline-block;' class='menuWizard_contents_linedown ui-icon ui-icon-circle-triangle-s'></span>");
			sbuf.append(lang.__("downward"));
			sbuf.append("</td>");
		}

		if(addButton){
			sbuf.append("<td>");
			sbuf.append("<button type=\"button\" class=\"menuWizard_addbutton\" name=\"menuWizard_addbutton\" value=\"\">");
			sbuf.append(lang.__("Add"));
			sbuf.append("</button>");
			sbuf.append("</td>");
		}else{
			sbuf.append("<td class=\"menuWizard_contents_hidden\">");
			sbuf.append("</td>");
		}

		if(delButton){
			sbuf.append("<td>");
			sbuf.append("<button type=\"button\" class=\"menuWizard_delbutton\" name=\"menuWizard_delbutton\" value=\"\">");
			sbuf.append(lang.__("Delete"));
			sbuf.append("</button>");
			sbuf.append("</td>");
		}else{
			sbuf.append("<td class=\"menuWizard_contents_hidden\">");
			sbuf.append("</td>");
		}

		sbuf.append("</tr>");

		return sbuf.toString();

	}

	/**
	 * ダイアログからのメニュータスク種別追加処理
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/menuWizard/addMenutasktype")
	@ResponseBody
	public String addMenutasktype(Map<String,Object>model){
//		// CSRF対策
//		if (!FormUtils.checkToken(request)) {
//			throw new InvalidAccessException(lang.__("Invalid session."));
//		}

		JSONObject retJson = new JSONObject();

		try{
			long localgovinfoid = loginDataDto.getLocalgovinfoid();
			String name = (String)request.getParameter("name");
			String note = (String)request.getParameter("note");

			MenutasktypeInfo menutasktypeInfo = new MenutasktypeInfo();
			BeanMap condition = new BeanMap();
			condition.put(MenutasktypeInfoNames.deleted().toString(), false);
			int currentMaxDisporder = menutasktypeInfoService.getLargestDisporder(condition, false);
			menutasktypeInfo.name = name;
			menutasktypeInfo.note = note;
			menutasktypeInfo.disporder = (currentMaxDisporder + 1);
			menutasktypeInfo.deleted = false;
			menutasktypeInfo.localgovinfoid = localgovinfoid;

			menutasktypeInfoService.insert(menutasktypeInfo);
			retJson.put("success", true);
		}catch(Exception e){
			retJson.put("success", false);
			retJson.put("message", e.getMessage());
			logger.error(loginDataDto.logInfo(), e);
		}

		try{
			responseService.responseJson(retJson);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
		}

		setupModel(model);
		return null;
	}

	/**
	 * 引数で指定したメニュー情報が、引数で指定した班または課以外のメニュータスク情報で使用されているか調べる
	 * @param menuinfoid
	 * @param isUsual
	 * @param guId
	 * @return
	 */
	private boolean isOtherUsedMenuInfo(long menuinfoid, boolean isUsual, long localgovinfoid, long guId){

		BeanMap conditionGroup = new BeanMap();
		BeanMap conditionUnit = new BeanMap();

		conditionGroup.put(Names.menuloginInfo().groupInfo().localgovinfoid().toString(), localgovinfoid);
		conditionUnit.put(Names.menuloginInfo().unitInfo().localgovinfoid().toString(), localgovinfoid);

		List<MenuloginInfo>  menuLoginInfoList = menuloginInfoService.findByCondition(conditionGroup, null, null, null, null);
		List<MenuloginInfo> menuLoginInfoUnitList = menuloginInfoService.findByConditionForUnit(conditionUnit, null, null, null, null);
		Set<MenuloginInfo> menuLoginInfoSet = new HashSet<MenuloginInfo>();

		for(MenuloginInfo menuloginInfo : menuLoginInfoList){
			menuLoginInfoSet.add(menuloginInfo);
		}
		for(MenuloginInfo menuloginInfo : menuLoginInfoUnitList){
			menuLoginInfoSet.add(menuloginInfo);
		}

		boolean isOtherUsedMenuInfo = false;
		for(MenuloginInfo menuloginInfo : menuLoginInfoSet){
			if(isUsual){
				if(menuloginInfo.unitid != null && menuloginInfo.unitid.equals(guId)){
					continue;
				}
			}else{
				if(menuloginInfo.groupid != null && menuloginInfo.groupid.equals(guId)){
					continue;
				}
			}
			List<MenuprocessInfo> menuprocessInfoList = menuprocessInfoService.findByMenuloginInfoId(menuloginInfo.id);
			for(MenuprocessInfo menuprocessInfo : menuprocessInfoList){
				BeanMap conditionMenutaskInfo = new BeanMap();
				conditionMenutaskInfo.put(MenutaskInfoNames.menuprocessinfoid().toString(), menuprocessInfo.id);
				List<MenutaskInfo> menutaskInfoList = menutaskInfoService.findByCondition(conditionMenutaskInfo);
				for(MenutaskInfo menutaskInfo : menutaskInfoList){
					MenutaskmenuInfo menutaskmenuInfo = menutaskmenuInfoService.findByMenutaskInfoIdAndMenuInfoId(menutaskInfo.id, menuinfoid);
					if(menutaskmenuInfo != null){
						isOtherUsedMenuInfo = true;
						break;
					}
				}
			}
		}
		return isOtherUsedMenuInfo;
	}

	/**
	 * メニュー情報削除処理。子テーブルも削除する。
	 * @param menuInfo
	 */
	private void deleteMenuInfo(MenuInfo menuInfo){

		// ページボタン表示情報の削除
    	List<PagemenubuttonInfo> pagemenubuttonInfoList = pagemenubuttonInfoService.findByMenuInfoId(menuInfo.id);
    	for(PagemenubuttonInfo pagemenubuttonInfo : pagemenubuttonInfoList){
    		pagemenubuttonInfoService.delete(pagemenubuttonInfo);
    	}

		// 地図レイヤ情報の削除
		BeanMap condition = new BeanMap();
		condition.put(MaplayerInfoNames.menuinfoid().toString(), menuInfo.id);
    	List<MaplayerInfo> mapLayerInfoList = maplayerInfoService.findByCondition(condition);
    	for(MaplayerInfo maplayerInfo: mapLayerInfoList){

    		// 地図レイヤ属性情報の削除
			condition.clear();
			condition.put(MaplayerattrInfoNames.maplayerinfoid().toString(), maplayerInfo.id);
    		List<MaplayerattrInfo> maplayerattrInfoList = maplayerattrInfoService.findByCondition(condition);
    		for(MaplayerattrInfo maplayerattrInfo : maplayerattrInfoList){
    			maplayerattrInfoService.delete(maplayerattrInfo);
    		}
    		maplayerInfo.deleted = true;
    		maplayerInfoService.update(maplayerInfo);
    	}

		// 地図ベースレイヤ情報の削除
    	List<MapbaselayerInfo> mapbaselayerInfoList = mapbaselayerInfoService.findByMenuid(menuInfo.id);
    	for(MapbaselayerInfo mapbaselayerInfo : mapbaselayerInfoList){
    		mapbaselayerInfoService.delete(mapbaselayerInfo);
    	}

		// メニューテーブル情報の削除
    	List<MenutableInfo> menutableInfoList = menutableInfoService.findByMenuInfoId(menuInfo.id);
    	for(MenutableInfo menutableInfo : menutableInfoList){

    		// テーブルリストカラム情報の削除
    		List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(menutableInfo.id);
    		for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){

        		// 属性行スタイル情報の削除
    			List<TablerowstyleInfo> tablerowstyleInfoList = tablerowstyleInfoService.findByTablelistcolumnInfoId(tablelistcolumnInfo.id);
    			for(TablerowstyleInfo tablerowstyleInfo :tablerowstyleInfoList){
    				tablerowstyleInfoService.delete(tablerowstyleInfo);
    			}
    			tablelistcolumnInfoService.delete(tablelistcolumnInfo);
    		}
    		menutableInfo.deleted = true;
    		menutableInfoService.update(menutableInfo);
    	}

		// 集計リスト項目情報の削除
    	List<SummarylistcolumnInfo> summarylistcolumnInfoList = summarylistcolumnInfoService.findByMenuid(menuInfo.id);
    	for(SummarylistcolumnInfo summarylistcolumnInfo : summarylistcolumnInfoList){
    		summarylistcolumnInfoService.delete(summarylistcolumnInfo);
    	}

		// 気象情報レイヤ情報の削除
    	List<MeteolayerInfo> meteolayerInfoList = meteolayerInfoService.findByMenuid(menuInfo.id);
    	for(MeteolayerInfo meteolayerInfo : meteolayerInfoList){
    		meteolayerInfoService.delete(meteolayerInfo);
    	}

		// 外部地図データ情報の削除
    	List<ExternalmapdataInfo> externalmapdataInfoList = externalmapdataInfoService.findByMenuid(menuInfo.id);
    	for(ExternalmapdataInfo externalmapdataInfo : externalmapdataInfoList){
    		externalmapdataInfoService.delete(externalmapdataInfo);
    	}

		// 外部リストデータ情報の削除
    	List<ExternaltabledataInfo> externaltabledataInfoList = externaltabledataInfoService.findByMenuInfoId(menuInfo.id);
    	for(ExternaltabledataInfo externaltabledataInfo : externaltabledataInfoList){
    		externaltabledataInfoService.delete(externaltabledataInfo);
    	}

		// メニュー地図情報の削除
		condition.clear();
		condition.put(MenumapInfoNames.menuinfoid().toString(), menuInfo.id);
    	List<MenumapInfo> menumapInfoList = menumapInfoService.findByCondition(condition);
    	for(MenumapInfo menumapInfo : menumapInfoList){
    		menumapInfoService.delete(menumapInfo);
    	}

		// 通知デフォルト情報の削除
		condition.clear();
		condition.put(NoticedefaultInfoNames.menuinfoid().toString(), menuInfo.id);
    	List<NoticedefaultInfo> noticedefaultInfoList = noticedefaultInfoService.findByCondition(condition);
    	for(NoticedefaultInfo noticedefaultInfo : noticedefaultInfoList){

    		// 通知デフォルトグループ情報の削除
			condition.clear();
			condition.put(NoticedefaultgroupInfoNames.noticedefaultinfoid().toString(), noticedefaultInfo.id);
	    	List<NoticedefaultgroupInfo> noticedefaultgroupInfoList = noticedefaultgroupInfoService.findByCondition(condition);
	    	for(NoticedefaultgroupInfo noticedefaultgroupInfo : noticedefaultgroupInfoList){
		    	noticedefaultgroupInfoService.delete(noticedefaultgroupInfo);
	    	}

			// アラームデフォルトグループ情報の削除
			condition.clear();
			condition.put(AlarmdefaultgroupInfoNames.noticedefaultinfoid().toString(), noticedefaultInfo.id);
	    	List<AlarmdefaultgroupInfo> alarmdefaultgroupInfoList = alarmdefaultgroupInfoService.findByCondition(condition);
	    	for(AlarmdefaultgroupInfo alarmdefaultgroupInfo : alarmdefaultgroupInfoList){
	    		alarmdefaultgroupInfoService.delete(alarmdefaultgroupInfo);
	    	}

	    	noticedefaultInfoService.delete(noticedefaultInfo);
    	}

		// フィルター情報の削除
    	List<FilterInfo> filterInfoList = filterInfoService.findByMenuid(menuInfo.id);
    	for(FilterInfo filterInfo : filterInfoList){
    		filterInfoService.delete(filterInfo);
    	}

		// 地図参照レイヤ情報の削除
    	List<MapreferencelayerInfo> mapreferencelayerInfoList = mapreferencelayerInfoService.findByMenuid(menuInfo.id);
    	for(MapreferencelayerInfo mapreferencelayerInfo : mapreferencelayerInfoList){
    		mapreferencelayerInfoService.delete(mapreferencelayerInfo);
    	}

		// 地図KMLレイヤ情報の削除
    	List<MapkmllayerInfo> mapkmllayerInfoList = mapkmllayerInfoService.findByMenuid(menuInfo.id);
    	for(MapkmllayerInfo mapkmllayerInfo : mapkmllayerInfoList){
    		mapkmllayerInfoService.delete(mapkmllayerInfo);
    	}

    	menuInfo.deleted = true;
    	menuInfoService.update(menuInfo);
	}

	/**
	 * メニュー情報の子要素を複製する
	 * @param baseMenuInfo
	 * @param newMenuInfo
	 */
	private void cloneMenuInfoChiledlen(MenuInfo baseMenuInfo, MenuInfo menuInfo){

		// ページボタン表示情報の複製
    	List<PagemenubuttonInfo> pagemenubuttonInfoList = pagemenubuttonInfoService.findByMenuInfoId(baseMenuInfo.id);
    	for(PagemenubuttonInfo basePagemenubuttonInfo : pagemenubuttonInfoList){
    		PagemenubuttonInfo pagemenubuttonInfo = new PagemenubuttonInfo();
    		BeanUtil.copyProperties(basePagemenubuttonInfo, pagemenubuttonInfo);
    		pagemenubuttonInfo.id = null;
    		pagemenubuttonInfo.menuInfo = menuInfo;
    		pagemenubuttonInfo.menuinfoid = menuInfo.id;
    		pagemenubuttonInfoService.insert(pagemenubuttonInfo);
    	}

    	// メニューテーブル情報の複製
    	List<MenutableInfo> menutableInfoList = menutableInfoService.findByMenuInfoId(baseMenuInfo.id);
    	for(MenutableInfo baseMenutableInfo : menutableInfoList){
    		MenutableInfo menutableInfo = new MenutableInfo();
    		BeanUtil.copyProperties(baseMenutableInfo, menutableInfo);
    		menutableInfo.id = null;
    		menutableInfo.menuInfo = menuInfo;
    		menutableInfo.menuinfoid = menuInfo.id;
    		menutableInfoService.insert(menutableInfo);

    		// テーブルリストカラム情報の複製
    		List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(baseMenutableInfo.id);
    		for(TablelistcolumnInfo baseTablelistcolumnInfo : tablelistcolumnInfoList){
    			TablelistcolumnInfo tablelistcolumnInfo = new TablelistcolumnInfo();
        		BeanUtil.copyProperties(baseTablelistcolumnInfo, tablelistcolumnInfo);
        		tablelistcolumnInfo.id = null;
        		tablelistcolumnInfo.menutableInfo = menutableInfo;
        		tablelistcolumnInfo.menutableinfoid = menutableInfo.id;
        		tablelistcolumnInfoService.insert(tablelistcolumnInfo);

        		// 属性行スタイル情報の複製
        		List<TablerowstyleInfo> tablerowstyleInfoList = tablerowstyleInfoService.findByTablelistcolumnInfoId(baseTablelistcolumnInfo.id);
    			for(TablerowstyleInfo baseTablerowstyleInfo :tablerowstyleInfoList){
    				TablerowstyleInfo tablerowstyleInfo = new TablerowstyleInfo();
            		BeanUtil.copyProperties(baseTablerowstyleInfo, tablerowstyleInfo);
            		tablerowstyleInfo.id = null;
            		tablerowstyleInfo.tablelistcolumnInfo = tablelistcolumnInfo;
            		tablerowstyleInfo.tablelistcolumninfoid = tablelistcolumnInfo.id;
            		tablerowstyleInfoService.insert(tablerowstyleInfo);
    			}
    		}
    	}

		// 地図レイヤ情報の複製
		BeanMap condition = new BeanMap();
		condition.put(MaplayerInfoNames.menuinfoid().toString(), baseMenuInfo.id);
    	List<MaplayerInfo> mapLayerInfoList = maplayerInfoService.findByCondition(condition);
    	for(MaplayerInfo baseMaplayerInfo: mapLayerInfoList){
    		MaplayerInfo maplayerInfo = new MaplayerInfo();
    		BeanUtil.copyProperties(baseMaplayerInfo, maplayerInfo);
    		maplayerInfo.id = null;
    		maplayerInfo.menuInfo = menuInfo;
    		maplayerInfo.menuinfoid = menuInfo.id;
    		maplayerInfoService.insert(maplayerInfo);

    		// 地図レイヤ属性情報の複製
			condition.clear();
			condition.put(MaplayerattrInfoNames.maplayerinfoid().toString(), baseMaplayerInfo.id);
    		List<MaplayerattrInfo> maplayerattrInfoList = maplayerattrInfoService.findByCondition(condition);
    		for(MaplayerattrInfo baseMaplayerattrInfo : maplayerattrInfoList){
    			MaplayerattrInfo maplayerattrInfo = new MaplayerattrInfo();
        		BeanUtil.copyProperties(baseMaplayerattrInfo, maplayerattrInfo);
        		maplayerattrInfo.id = null;
        		maplayerattrInfo.maplayerInfo = maplayerInfo;
        		maplayerattrInfo.maplayerinfoid = maplayerInfo.id;
        		maplayerattrInfoService.insert(maplayerattrInfo);
    		}
    	}

		// 地図ベースレイヤ情報の複製
    	List<MapbaselayerInfo> mapbaselayerInfoList = mapbaselayerInfoService.findByMenuid(baseMenuInfo.id);
    	for(MapbaselayerInfo baseMapbaselayerInfo : mapbaselayerInfoList){
    		MapbaselayerInfo mapbaselayerInfo = new MapbaselayerInfo();
    		BeanUtil.copyProperties(baseMapbaselayerInfo, mapbaselayerInfo);
    		mapbaselayerInfo.id = null;
    		mapbaselayerInfo.menuInfo = menuInfo;
    		mapbaselayerInfo.menuinfoid = menuInfo.id;
    		mapbaselayerInfoService.insert(mapbaselayerInfo);
    	}

		// 集計リスト項目情報の複製  集計リスト項目情報のカラム変更によりこのブロック削除
//    	List<SummarylistcolumnInfo> summarylistcolumnInfoList = summarylistcolumnInfoService.findByMenuid(baseMenuInfo.id);
//    	for(SummarylistcolumnInfo baseSummarylistcolumnInfo : summarylistcolumnInfoList){
//    		SummarylistcolumnInfo summarylistcolumnInfo = new SummarylistcolumnInfo();
//    		BeanUtil.copyProperties(baseSummarylistcolumnInfo, summarylistcolumnInfo);
//    		summarylistcolumnInfo.id = null;
//    		summarylistcolumnInfo.summarylistInfo = summarylistInfo;
//    		summarylistcolumnInfo.summarylistinfoid = summarylistInfo.id;
//    		summarylistcolumnInfoService.insert(summarylistcolumnInfo);
//    	}

		// 気象情報レイヤ情報の複製
    	List<MeteolayerInfo> meteolayerInfoList = meteolayerInfoService.findByMenuid(baseMenuInfo.id);
    	for(MeteolayerInfo baseMeteolayerInfo : meteolayerInfoList){
    		MeteolayerInfo meteolayerInfo = new MeteolayerInfo();
    		BeanUtil.copyProperties(baseMeteolayerInfo, meteolayerInfo);
    		meteolayerInfo.id = null;
    		meteolayerInfo.menuInfo = menuInfo;
    		meteolayerInfo.menuinfoid = menuInfo.id;
    		meteolayerInfoService.insert(meteolayerInfo);
    	}

		// 外部地図データ情報の複製
    	List<ExternalmapdataInfo> externalmapdataInfoList = externalmapdataInfoService.findByMenuid(baseMenuInfo.id);
    	for(ExternalmapdataInfo baseEternalmapdataInfo : externalmapdataInfoList){
    		ExternalmapdataInfo externalmapdataInfo = new ExternalmapdataInfo();
    		BeanUtil.copyProperties(baseEternalmapdataInfo, externalmapdataInfo);
    		externalmapdataInfo.id = null;
    		externalmapdataInfo.menuInfo = menuInfo;
    		externalmapdataInfo.menuinfoid = menuInfo.id;
    		externalmapdataInfoService.insert(externalmapdataInfo);
    	}

		// 外部リストデータ情報の複製
    	List<ExternaltabledataInfo> externaltabledataInfoList = externaltabledataInfoService.findByMenuInfoId(baseMenuInfo.id);
    	for(ExternaltabledataInfo baseEternaltabledataInfo : externaltabledataInfoList){
    		ExternaltabledataInfo externaltabledataInfo = new ExternaltabledataInfo();
    		BeanUtil.copyProperties(baseEternaltabledataInfo, externaltabledataInfo);
    		externaltabledataInfo.id = null;
    		externaltabledataInfo.menuInfo = menuInfo;
    		externaltabledataInfo.menuinfoid = menuInfo.id;
    		externaltabledataInfoService.insert(externaltabledataInfo);
    	}

		// メニュー地図情報の複製
		condition.clear();
		condition.put(MenumapInfoNames.menuinfoid().toString(), baseMenuInfo.id);
    	List<MenumapInfo> menumapInfoList = menumapInfoService.findByCondition(condition);
    	for(MenumapInfo baseMenumapInfo : menumapInfoList){
    		MenumapInfo menumapInfo = new MenumapInfo();
    		BeanUtil.copyProperties(baseMenumapInfo, menumapInfo);
    		menumapInfo.id = null;
    		menumapInfo.menuInfo = menuInfo;
    		menumapInfo.menuinfoid = menuInfo.id;
    		menumapInfoService.insert(menumapInfo);
    	}

		// 通知デフォルト情報の複製
		condition.clear();
		condition.put(NoticedefaultInfoNames.menuinfoid().toString(), baseMenuInfo.id);
    	List<NoticedefaultInfo> noticedefaultInfoList = noticedefaultInfoService.findByCondition(condition);
    	for(NoticedefaultInfo basenNticedefaultInfo : noticedefaultInfoList){
    		NoticedefaultInfo noticedefaultInfo = new NoticedefaultInfo();
    		BeanUtil.copyProperties(basenNticedefaultInfo, noticedefaultInfo);
    		noticedefaultInfo.id = null;
    		noticedefaultInfo.menuInfo = menuInfo;
    		noticedefaultInfo.menuinfoid = menuInfo.id;
    		noticedefaultInfoService.insert(noticedefaultInfo);

    		// 通知デフォルトグループ情報の複製
    		condition.clear();
			condition.put(NoticedefaultgroupInfoNames.noticedefaultinfoid().toString(), basenNticedefaultInfo.id);
	    	List<NoticedefaultgroupInfo> noticedefaultgroupInfoList = noticedefaultgroupInfoService.findByCondition(condition);
	    	for(NoticedefaultgroupInfo baseNoticedefaultgroupInfo : noticedefaultgroupInfoList){
	    		NoticedefaultgroupInfo noticedefaultgroupInfo = new NoticedefaultgroupInfo();
	    		BeanUtil.copyProperties(baseNoticedefaultgroupInfo, noticedefaultgroupInfo);
	    		noticedefaultgroupInfo.id = null;
	    		noticedefaultgroupInfo.noticedefaultInfo = noticedefaultInfo;
	    		noticedefaultgroupInfo.noticedefaultinfoid = noticedefaultInfo.id;
	    		noticedefaultgroupInfoService.insert(noticedefaultgroupInfo);
	    	}

    		// アラームデフォルトグループ情報の複製
			condition.clear();
			condition.put(AlarmdefaultgroupInfoNames.noticedefaultinfoid().toString(), basenNticedefaultInfo.id);
	    	List<AlarmdefaultgroupInfo> alarmdefaultgroupInfoList = alarmdefaultgroupInfoService.findByCondition(condition);
	    	for(AlarmdefaultgroupInfo baseAlarmdefaultgroupInfo : alarmdefaultgroupInfoList){
	    		AlarmdefaultgroupInfo alarmdefaultgroupInfo = new AlarmdefaultgroupInfo();
	    		BeanUtil.copyProperties(baseAlarmdefaultgroupInfo, alarmdefaultgroupInfo);
	    		alarmdefaultgroupInfo.id = null;
	    		alarmdefaultgroupInfo.noticedefaultInfo = noticedefaultInfo;
	    		alarmdefaultgroupInfo.noticedefaultinfoid = noticedefaultInfo.id;
	    		alarmdefaultgroupInfoService.insert(alarmdefaultgroupInfo);
	    	}
    	}

		// フィルター情報の複製
    	List<FilterInfo> filterInfoList = filterInfoService.findByMenuid(baseMenuInfo.id);
    	for(FilterInfo baseFilterInfo : filterInfoList){
    		FilterInfo filterInfo = new FilterInfo();
    		BeanUtil.copyProperties(baseFilterInfo, filterInfo);
    		filterInfo.id = null;
    		filterInfo.menuInfo = menuInfo;
    		filterInfo.menuinfoid = menuInfo.id;
    		filterInfoService.insert(filterInfo);
    	}

		// 地図参照レイヤ情報の複製
    	List<MapreferencelayerInfo> mapreferencelayerInfoList = mapreferencelayerInfoService.findByMenuid(baseMenuInfo.id);
    	for(MapreferencelayerInfo baseMapreferencelayerInfo : mapreferencelayerInfoList){
    		MapreferencelayerInfo mapreferencelayerInfo = new MapreferencelayerInfo();
    		BeanUtil.copyProperties(baseMapreferencelayerInfo, mapreferencelayerInfo);
    		mapreferencelayerInfo.id = null;
    		mapreferencelayerInfo.menuInfo = menuInfo;
    		mapreferencelayerInfo.menuinfoid = menuInfo.id;
    		mapreferencelayerInfoService.insert(mapreferencelayerInfo);
    	}

    	// 地図KMLレイヤ情報の複製
    	List<MapkmllayerInfo> mapkmllayerInfoList = mapkmllayerInfoService.findByMenuid(baseMenuInfo.id);
    	for(MapkmllayerInfo baseMapkmllayerInfo : mapkmllayerInfoList){
    		MapkmllayerInfo mapkmllayerInfo = new MapkmllayerInfo();
    		BeanUtil.copyProperties(baseMapkmllayerInfo, mapkmllayerInfo);
    		mapkmllayerInfo.id = null;
    		mapkmllayerInfo.menuInfo = menuInfo;
    		mapkmllayerInfo.menuinfoid = menuInfo.id;
    		mapkmllayerInfoService.insert(mapkmllayerInfo);
    	}
	}

	/**
	 * eコミマップレイヤの属性リスト作成
	 * @param menutableInfo
	 * @return
	 */
	private Map<String, String> createAttrInfoList(MenutableInfo menutableInfo){
    	Map<String, String> attrInfoMap = new HashMap<String, String>();

    	// テーブルマスタ情報取得
    	TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(menutableInfo.tablemasterinfoid);
    	if(! StringUtil.isEmpty(tablemasterInfo.layerid)){
	    	// レイヤ情報取得
	    	MapDB mapDB = MapDB.getMapDB();
	    	LayerInfo layerInfo = mapDB.getLayerInfo(tablemasterInfo.layerid);
	    	// レイヤ属性リスト作成
	    	Iterator<AttrInfo> ita = layerInfo.getAttrIterable().iterator();
	    	while(ita.hasNext()){
	    		AttrInfo ati = ita.next();
	    		attrInfoMap.put(ati.attrId, ati.name);
	    	}

    	}else{
    		attrInfoMap = DatabaseUtil.getAlterFieldNameMap(jdbcManager, tablemasterInfo.tablename);
    	}

    	return attrInfoMap;
	}

	@org.springframework.web.bind.annotation.RequestMapping(value="/admin/setupper/menuWizard/loadGUList", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String loadGUList(Map<String,Object>model){

		JSONObject retJson = new JSONObject();

		try{
			String localgovinfoidStr = (String)request.getParameter("localgovinfoid");
			long localgovinfoid = Long.parseLong(localgovinfoidStr);

			Map<Long, String> guMap = new LinkedHashMap<Long, String>();
			if(loginDataDto.isUsual()){
				List<UnitInfo> unitInfoList = unitInfoService.findByLocalgovInfoIdAndValid(localgovinfoid);
				for(UnitInfo unitInfo : unitInfoList){
					guMap.put(unitInfo.id, unitInfo.name);
				}
			}else{
				List<GroupInfo> groupInfoList = groupInfoService.findByLocalgovInfoIdAndValid(localgovinfoid);
				for(GroupInfo groupInfo : groupInfoList){
					guMap.put(groupInfo.id, groupInfo.name);
				}
			}

			JSONArray mapObj = new JSONArray();
			for(Map.Entry<Long, String> e : guMap.entrySet()){
				JSONObject row = new JSONObject();
				row.put("key", e.getKey());
				row.put("value", e.getValue());
				mapObj.add(row);
			}
			retJson.put("success", true);
			retJson.put("list", mapObj);
		}catch(Exception e){
			retJson.put("success", false);
			retJson.put("message", e.getMessage());
			logger.error(loginDataDto.logInfo(), e);
		}

		try{
			responseService.responseJson(retJson);
		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
		}

		setupModel(model);
		return null;
	}

	/**
	 * エクセル帳票テンプレートファイルアップロードリクエスト処理
	 * @return
	 * @throws Exception
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/menuWizard/uploadExcelTemplate")
	public String uploadExcelTemplate(Map<String,Object>model) throws Exception {
		String timeFormat = "yyyyMMddHHmmss";
		String timeStr = new SimpleDateFormat(timeFormat).format(new Timestamp(System.currentTimeMillis()));
	    String uuidStr = request.getSession().getId() + timeStr;

		// ファイル名生成
		String fileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + "menuinfoid_" + uuidStr + "_template.xlsx";

		// ファイルアップロード
		File uploadedFile = excelListTemplatefileUpload(menuWizardForm.excellisttemplatetempfile, Long.toString(loginDataDto.getLocalgovinfoid()), fileName);

		// テンプレート内容チェック
		JSONObject json = excellistService.check(uploadedFile);
		if(json.containsKey(JQG_MESSAGE)){
			responseService.responseJsonText(json);
		}else{
			json.put("filename", uploadedFile.getName());
			responseService.responseJsonText(json);
		}
		setupModel(model);
		return null;
	}

	/**
	 * エクセル帳票テンプレートファイルアップロード処理
	 * @param uploadFile
	 * @param localgovinfoid
	 * @throws ServiceException
	 */
	private File excelListTemplatefileUpload(MultipartFile uploadFile, String localgovinfoid, String fileName) throws ServiceException {
		long fileSize = uploadFile.getSize();
		if (fileSize != 0) {
			String fileExt = "";
			int point = uploadFile.getOriginalFilename().lastIndexOf(".");
			if (point != -1) {
				fileExt = uploadFile.getOriginalFilename().substring(point + 1);
			}

			// 拡張子チェック。.xlsxのみ有効
			if(!((!StringUtil.isEmpty(fileExt)) && fileExt.equals("xlsx"))){
				try{
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("Excel template file: only XLSX format is valid."));
					responseService.responseJsonText(json);
					return null;
				}catch(Exception e){
					e.printStackTrace();
					logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
					logger.error("", e);
					if(EnvUtil.isProductEnv()){
						throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
					}else{
						//本番環境でなければエラー詳細内容も合わせて返却。
						throw new ServiceException(e);
					}
				}
			}

			String dirPath = Constants.EXCELLIST_BASEDIR + localgovinfoid + "/";
			String path = application.getRealPath(dirPath + fileName);

			try {
				File dir = new File(application.getRealPath(dirPath));
				if (!dir.exists()) dir.mkdirs();
				OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
				try {
					out.write(uploadFile.getBytes(), 0, (int) fileSize);
					return new File(path);
				} finally {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
				logger.error("", e);
				if(EnvUtil.isProductEnv()){
					throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
				}else{
					//本番環境でなければエラー詳細内容も合わせて返却。
					throw new ServiceException(e);
				}
			}
		}else{
			try {
				JSONObject json = new JSONObject();
				json.put(JQG_MESSAGE, lang.__("The file size is zero."));
				responseService.responseJsonText(json);
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
				logger.error("", e);
				if(EnvUtil.isProductEnv()){
					throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
				}else{
					//本番環境でなければエラー詳細内容も合わせて返却。
					throw new ServiceException(e);
				}
			}
		}
	}

	/**
	 * エクセル帳票テンプレートファイルを一時ファイル名からメニューID付きにリネームする
	 * @param localgovinfoid
	 * @param excellisttemplatetempfileName
	 * @param menuInfo
	 * @param delete
	 */
	private void moveExcellisttemplatetempfile(long localgovinfoid, String excellisttemplatetempfileName, MenuInfo menuInfo, boolean delete){
		String dirPath = Constants.EXCELLIST_BASEDIR + localgovinfoid + "/";

		if(! menuInfo.menutypeid.equals(Menutype.EXCELLIST)){
			return ;
		}

		if(! delete){
			if(StringUtil.isEmpty(excellisttemplatetempfileName)||excellisttemplatetempfileName.equals("0")){
				return;
			}
			String newFileNmae = "";
			String [] excellisttemplatetempfileSplit = excellisttemplatetempfileName.split("_");
			newFileNmae = excellisttemplatetempfileSplit[0] + "_" + excellisttemplatetempfileSplit[1] + "_" +
					menuInfo.id + "_" + excellisttemplatetempfileSplit[3];

			String oldPath = application.getRealPath(dirPath + excellisttemplatetempfileName);
			String newPath = application.getRealPath(dirPath + newFileNmae);
			File oldFile = new File(oldPath);
			File newFile = new File(newPath);
			if(newFile.exists()){
				newFile.delete();
			}
			FileUtil.fileCopyByPath(oldPath, newPath);
			oldFile.delete();
		}else{
			String deleteFileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX + "menuinfoid_" + menuInfo.id + "_template.xlsx";
			String delPath = application.getRealPath(dirPath + deleteFileName);
			File delFile = new File(delPath);
			delFile.delete();
		}
	}
}
