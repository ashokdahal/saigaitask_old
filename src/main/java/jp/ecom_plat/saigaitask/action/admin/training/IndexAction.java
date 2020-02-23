/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.dto.AlertrequestInfoDto;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotypeMaster;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrainingmeteoData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames;
import jp.ecom_plat.saigaitask.form.admin.TrainingplanForm;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.MeteoParseXMLService;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteotriggerInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteotypeMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteoxsltInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingmeteoDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingplanDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingplanlinkDataService;
import jp.ecom_plat.saigaitask.service.training.XmlFileEditHelper;
import jp.ecom_plat.saigaitask.service.training.XmlFileMgrService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 訓練パネルのアクションクラス.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin/training")
public class IndexAction extends AbstractAction{

    /** 訓練プラン情報 */
    @Resource protected TrainingplanDataService trainingplanDataService;
    /** 訓練プラン連携自治体情報 */
    @Resource protected TrainingplanlinkDataService trainingplanlinkDataService;
    /** 訓練外部データ情報 */
    @Resource protected TrainingmeteoDataService trainingmeteoDataService;
    /** 災害種別 */
    @Resource protected DisasterMasterService disasterMasterService;
    /** 災害データ */
	@Resource protected TrackDataService trackDataService;
	/** trainingdataid 属性を登録情報レイヤに追加するサービスクラス */
	@Resource protected TrackdataidAttrService trackdataidAttrService;
	/** History */
	//@Resource protected HistoryTableService historyTableService;
	/** 班情報 */
	@Resource protected GroupInfoService groupInfoService;
	/** */
	@Resource protected TrackgroupDataService trackgroupDataService;
	/** 地図マスター情報 */
	@Resource protected MapmasterInfoService mapmasterInfoService;
	/** テーブルマスター情報 */
	@Resource protected MeteotypeMasterService meteotypeMasterService;
	/** 気象情報 */
	//@Resource protected MeteoricAlarmService meteoricAlarmService;
	@Resource protected MeteoParseXMLService meteoParseXMLService;
	/** 気象情報取得情報 */
	@Resource protected MeteorequestInfoService meteorequestInfoService;
	/** 気象情報トリガー */
	@Resource protected MeteotriggerInfoService meteotriggerInfoService;
	/** 気象情報XSLT情報 */
	@Resource protected MeteoxsltInfoService meteoxsltInfoService;
	@Resource protected FileService fileService;
	/** 気象庁XMLの「震源震度に関する情報」から震度レイヤを作成するサービス */
	@Resource
	protected MeteoricEarthQuakeService meteoricEarthQuakeService;
	/** トランザクション */
	@Resource protected UserTransaction userTransaction;

    protected TrainingplanForm trainingplanForm;

    /** 過去訓練情報リスト */
    public List<TrackData> trainingDataList;
    /** 訓練プランリスト */
    public List<TrainingplanData> trainingPlanList;
    /** 連携自治体リスト */
    public List<LocalgovInfo> cityLocalgovInfos;
    /** 災害種別リスト */
    public List<DisasterMaster> disasterMasterList;
    /** 外部データリスト */
    public List<TrainingmeteoData> trainingMeteoList;
    /** 連携自治体のON/OFFリスト */
    public List<TrainingplanLinkClass> trainingplanLinkClassList;
    /** 災害種別の名称マップ */
    public Map<Integer, String> disasterMap;
    /** 過去訓練情報リストのTH部 */
    public String[] trainingDataTh;
    /** 訓練プランリストのTH部 */
    public String[] trainingPlanTh;
    /** 訓練プラン外部データリストのTH部 */
    public String[] trainingExternalDataTh;
    /** 訓練モード移行リストのTH部 */
    public String[] planControlTh;
    /** 訓練モードデータ配信実施リストのTH部 */
    public String[] planControlExternalTh;
    /** 連携している自治体の訓練プランを表示するかどうかを決めるフラグ true=表示する */
    public boolean plan_link_flag = true;
    /** リクエストで使用するプランIDの引渡し用 */
    public String training_planid = "0";
    /** リクエストで使用する左手メニューIDの引渡し用 */
    public String training_Listtype = "";
    /** エラーメッセージ用 */
    public String errorMsg = "";


	/** 自治体切り替えSELECTオプション */
	public Map<Long, String> localgovSelectOptions;

	@Resource protected MenuloginInfoService menuloginInfoService;
	/** セットアップ対象の自治体ID(システム管理者用) */
	public Long localgovinfoid;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("trainingplanForm", trainingplanForm);
		model.put("trainingDataList", trainingDataList);
		model.put("trainingPlanList", trainingPlanList);
		model.put("cityLocalgovInfos", cityLocalgovInfos);
		model.put("disasterMasterList", disasterMasterList);
		model.put("trainingMeteoList", trainingMeteoList);
		model.put("trainingplanLinkClassList", trainingplanLinkClassList);
		model.put("disasterMap", disasterMap);
		model.put("trainingDataTh", trainingDataTh);
		model.put("trainingPlanTh", trainingPlanTh);
		model.put("trainingExternalDataTh", trainingExternalDataTh);
		model.put("planControlTh", planControlTh);
		model.put("planControlExternalTh", planControlExternalTh);
		model.put("plan_link_flag", plan_link_flag);
		model.put("training_planid", training_planid);
		model.put("training_Listtype", training_Listtype);
		model.put("errorMsg", errorMsg);
		model.put("localgovinfoid", localgovinfoid);
		model.put("localgovSelectOptions", localgovSelectOptions);
	}

	public void initXmlEditorSetupper() {
		XmlFileEditHelper.outputLog("initXmlEditorSetupper 1");

		XmlFileEditHelper.outputLog("initXmlEditorSetupper 2");
		// 自治体切り替えSELECTオプション
		localgovSelectOptions = new LinkedHashMap<Long, String>();

		XmlFileEditHelper.outputLog("initXmlEditorSetupper 3");
		// システム管理者でログイン中
		if(loginDataDto.getGroupid()==0) {
			List<LocalgovInfo> localgovInfos = localgovInfoService.findAll(
					Operations.asc(LocalgovInfoNames.id()),
					Operations.asc(LocalgovInfoNames.prefcode()),
					Operations.asc(LocalgovInfoNames.citycode())

			);

			XmlFileEditHelper.outputLog("initXmlEditorSetupper 4");

			localgovSelectOptions.put(0L, lang.__("whole"));
			for(LocalgovInfo localgovInfo : localgovInfos) {
				if(localgovInfo.id.equals(0L)) continue;
				localgovSelectOptions.put(localgovInfo.id, localgovInfoService.getLocalgovNameFull(localgovInfo));
			}
			XmlFileEditHelper.outputLog("initXmlEditorSetupper 5");

			XmlFileEditHelper.outputLog("setAttribute initXmlEditorSetupper-------------------Admin 0");

			request.getSession().setAttribute("JsonGetCurLocalGovID", "0");
		}
		else {
			XmlFileEditHelper.outputLog("initXmlEditorSetupper 6");
			LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
			localgovSelectOptions.put(localgovInfo.id, localgovInfoService.getLocalgovNameFull(localgovInfo));
			XmlFileEditHelper.outputLog("setAttribute initXmlEditorSetupper 7 localgovInfo.id="+ localgovInfo.id);
			request.getSession().setAttribute("JsonGetCurLocalGovID", "" + localgovInfo.id);
		}
	}

	public boolean loginLocalgovInfo(long localgovinfoid) {
		LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
		if(localgovInfo==null) throw new ServiceException(lang.__("Local gov. ID = {0} not found.", localgovinfoid));
		// 自治体情報のログイン
		loginDataDto.setLocalgovinfoid(localgovInfo.id);
		loginDataDto.setLocalgovInfo(localgovInfo);
		// 使っている災害種別を取得
		HashSet<Integer> usingDisasterids = menuloginInfoService.getUsingDisasterIds(loginDataDto.getLocalgovinfoid());
		// 使っている災害種別があれば、先頭のものでとりあえずログイン
		if(0<usingDisasterids.size()) {
			//loginDataDto.getDisasterid() = usingDisasterids.iterator().next();
		}

		return true;
	}

    /**
     * 訓練プラン一覧ページを表示する
     * @return : index.jsp
     * @throws ParseException
     */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training", "/admin/training/index"})
	public String index(Map<String,Object>model,
		@Valid @ModelAttribute TrainingplanForm trainingplanForm, BindingResult bindingResult) {
		this.trainingplanForm = trainingplanForm;
		
    	// 災害類型の削除に伴い、災害種別を削除
	    //trainingDataTh = new String[] {lang.__("Disaster type"),lang.__("Training name"),lang.__("Training completion date")};
    	trainingDataTh = new String[] {lang.__("Training name"),lang.__("Training completion date")};
	    trainingPlanTh = new String[] {lang.__("Creating local gov."),lang.__("Training name"),lang.__("Update date"),lang.__("Edit"),lang.__("Trainingplan copy"),lang.__("Delete")};

	    trainingExternalDataTh = new String[] {lang.__("Registration name"),lang.__("Weather info type"),lang.__("Overview"),lang.__("Setting date")};
	    planControlTh = new String[] {lang.__("Local gov. name"),lang.__("Current mode"),lang.__("Shift training mode<!--2-->")};
	    planControlExternalTh =  new String[] {lang.__("Local gov. name"),lang.__("Delivery execution time"),lang.__("Delivery completion")};


	    // システム管理者でログインし、自治体の選択がされた場合
	    {
			// xml editor
			XmlFileEditHelper.outputLog("loginDataDto.getLocalgovinfoid() = " + loginDataDto.getLocalgovinfoid());
			XmlFileEditHelper.outputLog("localgovinfoid = " + localgovinfoid);
			XmlFileEditHelper.outputLog("localgovinfoid = request=" + request);
			XmlFileEditHelper.outputLog("localgovinfoid = " + request.getParameter("localgovinfoid"));
			String strLocalgovinfoid = request.getParameter("localgovinfoid");
			try {
				localgovinfoid = Long.decode(""+StringUtil.getInt(strLocalgovinfoid));
			} catch(Exception e) {
				localgovinfoid = null;
			}

			//localgovinfoid
			// 自治体の切り替えパラメータ有り、かつシステム管理者の場合
			if(localgovinfoid!=null && loginDataDto.getGroupid()==0) {
				loginLocalgovInfo(localgovinfoid);
			}
			initXmlEditorSetupper();

			XmlFileEditHelper.outputLog("IndexAction-------------------strLocalgovinfoid " + strLocalgovinfoid);

			if(strLocalgovinfoid == null) {

				// noting to do


			} else {
				request.getSession().setAttribute("JsonGetCurLocalGovID", "" + localgovinfoid.intValue());

				XmlFileEditHelper.outputLog("IndexAction-------------------JsonGetCurLocalGovID " + localgovinfoid.intValue());
			}
			//this.request.setAttribute("loginDataDto", loginDataDto);
		}


		if(loginDataDto.getLocalgovinfoid() == 0){
			// システム管理者は全て表示する
			trainingPlanList = trainingplanDataService.findByLocalgovinfoId();
			// 全自治体で実施した過去訓練データを全て表示する
			trainingDataList = trackDataService.findByOldTrackData(null, true);
			// 連携自治体情報はvalidが有効になっているものを全て表示
			cityLocalgovInfos = localgovInfoService.findValidOrderByID();
		}else{
			// ログインしている自治体で作成した訓練プランを表示する
			//trainingPlanList = trainingplanDataService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());
			// ログインしている自治体で実施した過去訓練データを表示する
			trainingDataList = trackDataService.findByOldTrackData(loginDataDto.getLocalgovinfoid(), true);
			for(TrackData td : trainingDataList){
				//td.disasterMaster = disasterMasterService.findById(td.disasterid);
			}
			// 自治体情報を取得
			cityLocalgovInfos = new ArrayList<LocalgovInfo>();
			// 自自治体
			LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
			// 連携する自治体を取得
			long preflocalgovinfoid = havePrefLocalgovinfoid(localgovInfo);
			// 自治体連携有
			if(preflocalgovinfoid > 0){
				// 県を挿入する
				LocalgovInfo prefLocalgovInfo = localgovInfoService.findById(preflocalgovinfoid);
				cityLocalgovInfos.add(prefLocalgovInfo);
				// 連携自治体(市町村) を検索
				List<LocalgovInfo> linkCityLocalgovInfos = localgovInfoService.findCityLocalgovInfo(preflocalgovinfoid);
				cityLocalgovInfos.addAll(linkCityLocalgovInfos);
				// 訓練プランも連携自治体分(県、市町村)を表示する
				trainingPlanList = trainingplanDataService.findByLocalgovinfoId(preflocalgovinfoid);
				for(LocalgovInfo gov : linkCityLocalgovInfos){
					List<TrainingplanData> cityTrainingPlan = trainingplanDataService.findByLocalgovinfoId(gov.id);
					trainingPlanList.addAll(cityTrainingPlan);
				}
			}else{
				// 自自治体は必ず入れる
				cityLocalgovInfos.add(localgovInfo);
				// 自自治体で作成した訓練プランのみを表示する
				trainingPlanList = trainingplanDataService.findByLocalgovinfoId(loginDataDto.getLocalgovinfoid());
			}
		}
		// 気象情報取得種別マスタを取得する
		trainingplanForm.meteoTypeMasterList = meteotypeMasterService.findAll(Operations.asc(MeteotypeMasterNames.id()));

		// 災害種別の選択肢用
		disasterMasterList = disasterMasterService.findGtZeroOrderBy();
		disasterMap = disasterMasterService.getNameMapNoZero();

		training_planid = request.getParameter("training_planid");
		training_planid = training_planid == null ? "0" : training_planid;
		// planidが付与されていれば該当するプランデータを取得する
		if(org.seasar.framework.util.StringUtil.isNotEmpty(training_planid) &&  !training_planid.equals("0")){
			planData();
		}
		training_Listtype = request.getParameter("training_Listtype");
		training_Listtype = training_Listtype == null ? "" : training_Listtype;

		setupModel(model);
		return "/admin/training/index";
	}


	/**
	 * planidが渡された際に必要なデータを取得する関数
	 */
	public void planData(){
		try{
			// プランIDをintにキャスト
			long planid = Long.parseLong(training_planid);
			// プラン情報取得
			trainingplanForm.trainingplanData = trainingplanDataService.findNotDeletedById(planid);
			// 削除フラグが立っていないものが選択された場合は、外部ファイル設定、過去アーカイブデータを取得する
			if(trainingplanForm.trainingplanData != null){
				// 外部データを取得する
				trainingMeteoList = trainingmeteoDataService.findByPlandataId(trainingplanForm.trainingplanData.id);
				// 過去アーカイブデータ
				trainingDataList = trackDataService.findByOldTrackData(trainingplanForm.trainingplanData.id,true);
				// 連携自治体
				trainingplanForm.trainingplanlinkDataList = trainingplanlinkDataService.findByTrainingplandataId(trainingplanForm.trainingplanData.id);
				// 自治体情報を元にボタン配置用Classに内容をセットする
				trainingplanLinkClassList = new ArrayList<TrainingplanLinkClass>();
				if(cityLocalgovInfos != null){
					for(LocalgovInfo citys : cityLocalgovInfos){
						// Classを作成する
						TrainingplanLinkClass linkClass = new TrainingplanLinkClass(citys.id, citys.city.equals("") ? citys.pref : citys.city);
						// 連携しているか確認
						for(TrainingplanlinkData links : trainingplanForm.trainingplanlinkDataList){
							// 現在のモードをセットする為に、災害をもっているか、訓練を持っているか調査する
							List<TrackData> tracks = trackDataService.findByCurrentTrackDatas(citys.id, false);
							List<TrackData> trainings = trackDataService.findByCurrentTrackDatas(citys.id, true);
							linkClass.mode = trainings.size() > 0 ? lang.__("Undergoing training") : lang.__("Peacetime");
							if(linkClass.mode.equals(lang.__("Peacetime"))){
								linkClass.mode = tracks.size() > 0 ? lang.__("During disaster") : lang.__("Peacetime");
							}
							if(!linkClass.mode.equals(lang.__("Peacetime"))){
								linkClass.setSaigaiFlag(true);
							}
							// 一致したらtrueと更新時間をセットする
							if(links.localgovinfoid.equals(citys.id)){
								linkClass.setFlagAndTime(true, links.updatetime);
								break;
							}
						}
						trainingplanLinkClassList.add(linkClass);
					}
				}
			}
		}catch(NumberFormatException e){
			logger.error(lang.__("Planid = {0} is not numeric.", training_planid));
		}
	}



	/**
	 * 訓練プランの編集
	 * @return JSON Object
	 * @throws ParseException
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/updatePlan"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String updatePlan(Map<String,Object>model) {

		// 返却用JSON
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try{
			/* パラメータを取得 */
			// プランID
			String planidS = request.getParameter("planids");
			// NumberFormatExceptionをチェックする
			long planid = Long.parseLong(planidS);
			// プランidを元に、現在の情報を取得(作成自治体を変更させない為)
			TrainingplanData trainingplanData = trainingplanDataService.findNotDeletedById(planid);
			if(trainingplanData == null && planid > 0l){
				json.put("newPlan", false);
				json.put("error", lang.__("Failed to register. training that has the plan ID is not found."));
				PrintWriter out = response.getWriter();
				out.print( json.toString() );
				return null;
			}
			if(planid == 0l){
				trainingplanData = new TrainingplanData();
				// 現在ログインしている自治体で登録
				trainingplanData.localgovinfoid = loginDataDto.getLocalgovinfoid();
			}
			// プラン名称
			String planName = request.getParameter("planName");
			// プラン概要
			String planNote = request.getParameter("planNote");
			// プラン災害種別
			//String planDisasterS = request.getParameter("planDisasters");
			//int planDisaster = Integer.parseInt(planDisasterS);
			// 通知機能制限
			String commonsFlagS = request.getParameter("commonsFlag");
			String facebookFlagS = request.getParameter("facebookFlag");
			String twitterFlagS = request.getParameter("twitterFlag");
			String ecomGWFlagS = request.getParameter("ecomGWFlag");
			// プラン登録用
			//trainingplanData.localgovinfoid = loginDataDto.getLocalgovinfoid();
			//trainingplanData.disasterid = planDisaster;
			trainingplanData.name = planName;
			trainingplanData.note = planNote;
			trainingplanData.publiccommonsflag = commonsFlagS.equals("true") ? true : false;
			trainingplanData.facebookflag = facebookFlagS.equals("true") ? true : false;
			trainingplanData.twitterflag = twitterFlagS.equals("true") ? true : false;
			trainingplanData.ecommapgwflag = ecomGWFlagS.equals("true") ? true : false;
			trainingplanData.updatetime = new Timestamp(System.currentTimeMillis());
			trainingplanData.deleted = false;
			if(planid > 0){
				trainingplanData.id = planid;
				trainingplanDataService.update(trainingplanData);
			}else{
				trainingplanDataService.insert(trainingplanData);
			}
			// プランの連携自治体登録用
			// 最初に、連携自治体情報をクリアする
			if(planid > 0){
				List<TrainingplanlinkData> trainingplanlinkDelete = trainingplanlinkDataService.findByTrainingplandataId(planid);
				for(TrainingplanlinkData planlinkInfo : trainingplanlinkDelete){
					trainingplanlinkDataService.delete(planlinkInfo);
				}
			}
			// 連携自治体は配列で取得
			String localgovinfoidParam = request.getParameter("localgovinfoid");
			if(localgovinfoidParam != null){
				String[] localgovinfoidArr = request.getParameterValues("localgovinfoid");
				for(String localgovinfoid : localgovinfoidArr){
					// planinfoidと共にInsertしていく
					TrainingplanlinkData insertPlanlink = new TrainingplanlinkData();
					insertPlanlink.localgovinfoid = Long.parseLong(localgovinfoid);
					insertPlanlink.trainingplandataid = trainingplanData.id;
					trainingplanlinkDataService.insert(insertPlanlink);
				}
			}
			json.put("planid", trainingplanData.id);
			json.put("planname", trainingplanData.name);
			// 新規登録時はページリロードを行えるよう、フラグを渡す
			if(planid > 0){
				json.put("newPlan", false);
			}else{
				json.put("newPlan", true);
			}
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		}catch(NumberFormatException e){
			logger.error(lang.__("Failed to register. Plan ID value is invalid."));
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 自自治体で、選択した気象情報種別情報の設定が行われているか確認する関数
	 * @return
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/checkMeteoRequestInfo"})
	public String checkMeteoRequestInfo(){
		try{
			// JSON返却用
			JSONObject json = new JSONObject();
			json.put("message", lang.__("In Self-local gov., corresponding weather info type has not been registered. Set the info on admin window."));
			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// 自自治体で該当する気象情報種別情報が登録されているか確認
			String meteotypeid_s = request.getParameter("checkMeteoTypeid");
			int meteotypeid = Integer.parseInt(meteotypeid_s);
			//MeteotypeMaster meteoTypeMaster = meteotypeMasterService.findById(meteotypeid);
			BeanMap beanmap = new BeanMap();
			beanmap.put("localgovinfoid", loginDataDto.getLocalgovinfoid());
			beanmap.put("meteotypeid", meteotypeid);
			List<MeteorequestInfo> meteorequestInfoList = meteorequestInfoService.findByCondition(beanmap);
			if(meteorequestInfoList.size() > 0){
				json.put("message", "");
			}
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		}catch(JSONException e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 外部データ設定の登録機能
	 * @return 訓練プランの外部データ設定ページへフォワード
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/updateExternalFile"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String updateExternalFile(Map<String,Object>model,
			@Valid @ModelAttribute TrainingplanForm trainingplanForm, BindingResult bindingResult) {
		this.trainingplanForm = trainingplanForm;

		// 外部データ情報
		TrainingmeteoData trainingmeteoData = new TrainingmeteoData();
		try{
			long meteoId = Long.parseLong(trainingplanForm.meteoid);
			long planId = Long.parseLong(trainingplanForm.trainingplandataid);
			String meteotypeid_s = request.getParameter("meteotypeid");
			int meteotypeid = Integer.parseInt(meteotypeid_s);
			// 編集の場合は一旦データを取得する(登録ファイルの変更が無い事を考慮)
			if(meteoId > 0){
				trainingmeteoData = trainingmeteoDataService.findById(meteoId);
			}
			// 登録ファイル以外のデータを設定
			trainingmeteoData.trainingplandataid = planId;

			trainingmeteoData.name = trainingplanForm.meteoName;
			trainingmeteoData.note = trainingplanForm.meteoNote;
			trainingmeteoData.meteotypeid = meteotypeid;
			trainingmeteoData.updatetime = new Timestamp(System.currentTimeMillis());
			trainingmeteoData.deleted = false;
			// 添付ファイル
			if(trainingplanForm.formFile_external_xml != null && !trainingplanForm.formFile_external_xml.toString().equals("") ) {
				// アップロードされたファイル名を meteourl として利用する
				trainingmeteoData.meteourl = trainingplanForm.formFile_external_xml.getOriginalFilename();
				// 古い登録ファイルのパスを保持
				String filepath_old = application.getRealPath("/upload/") + trainingmeteoData.meteourl;
				File filepath_old_File = new File(filepath_old);
				// 古い登録ファイルを一旦リネーム
				String filepath_rename = filenameCheck(application.getRealPath("/upload/"), trainingmeteoData.meteourl, "_del");
				File files_rename = new File(filepath_rename);
				boolean filerenameflag = filepath_old_File.renameTo(files_rename);
				if(!filerenameflag){
					logger.error(lang.__("Failed to rename past file of external data. :") + filepath_old + " rename to " + filepath_rename);
				}
				// 今回登録予定のファイルを登録する
				String path = FileUtil.upload_training(application, loginDataDto.getLocalgovinfoid(), trainingplanForm.formFile_external_xml);
				if(path.indexOf(/*File.separator*/"/" + "upload") != -1){
					path = path.substring(path.indexOf(/*File.separator*/"/" + "upload") + 7, path.length());
				}
				trainingmeteoData.meteourl = path;
				// 退避させた古い登録ファイルを削除する
				boolean deleteflag = files_rename.delete();
				if(!deleteflag){
					logger.error(lang.__("Failed to delete past file of external data. :") + filepath_rename);
				}
				logger.info("External Meteo file upload : " + path);
			}
			// サーバ上の防災情報XMLファイルを指定した場合
			if( (!org.seasar.framework.util.StringUtil.isEmpty(trainingplanForm.bousaixmlfile_selected_type))
					&& (!org.seasar.framework.util.StringUtil.isEmpty(trainingplanForm.bousaixmlfile_selected_filename))  ) {
				trainingmeteoData.meteourl = trainingplanForm.bousaixmlfile_selected_filename;
				// 古い登録ファイルのパスを保持
				String filepath_old = application.getRealPath("/upload/") + trainingmeteoData.meteourl;
				File filepath_old_File = new File(filepath_old);
				// 古い登録ファイルを一旦リネーム
				String filepath_rename = filenameCheck(application.getRealPath("/upload/"), trainingmeteoData.meteourl, "_del");
				File files_rename = new File(filepath_rename);
				boolean filerenameflag = filepath_old_File.renameTo(files_rename);
				if(!filerenameflag){
					logger.error(lang.__("Failed to rename past file of external data. :") + filepath_old + " rename to " + filepath_rename);
				}

				// 今回登録予定のファイルを防災情報XMLファイル保存ディレクトリからコピーする
				// 防災情報XMLファイルのパスを取得
				XmlFileMgrService xmlFileMgrService = new XmlFileMgrService();
				xmlFileMgrService.initProp();
				String strXml = "/WEB-INF/" + xmlFileMgrService.getPropData().strMETEOXMLEDIT_PATH;
				MeteotypeMaster meteotypeMaster = meteotypeMasterService.findById(meteotypeid);
				TrainingplanData trainingplanData = trainingplanDataService.findById(planId);
				String xmlFromFiePath = strXml + "/" + trainingplanData.localgovinfoid + "/" + meteotypeMaster.type + "/" + trainingplanForm.bousaixmlfile_selected_filename;;
				xmlFromFiePath = StringUtil.replaceAll("//", "/", xmlFromFiePath);
				// コピー実行
				String xmlToFilePath = FileUtil.getUploadTrainingPath(application, loginDataDto.getLocalgovinfoid()) + "/" + trainingplanForm.bousaixmlfile_selected_filename;
				xmlToFilePath = StringUtil.replaceAll("//", "/", xmlToFilePath);
				xmlToFilePath =FileUtil.uploadFromServer_fileRename(xmlToFilePath);
				boolean copyResult = FileUtil.fileCopyByPath(application.getRealPath(xmlFromFiePath), xmlToFilePath);
				if(! copyResult){
					logger.error(lang.__("Failed to copy file of Disaster info xml . :") + xmlFromFiePath + " to " + xmlToFilePath);
				}

				if(xmlToFilePath.indexOf("/upload") != -1){
					xmlToFilePath = xmlToFilePath.substring(xmlToFilePath.indexOf("/upload") + 7, xmlToFilePath.length());
				}else if(! File.separator.equals("/")){
					//windows環境だと、uploadよりも上流のpathの区切り文字が「\」になるのでupload以下の文字列を切り出す
					//xmlToFilePath = "/"+xmlToFilePath.substring(xmlToFilePath.indexOf("upload/") + 7);
					int idx = xmlToFilePath.indexOf("upload\\/"); // Springに変えてからは path￥upload￥/xxxx のようになった
					if(0<=idx) {
						xmlToFilePath = "/"+xmlToFilePath.substring(xmlToFilePath.indexOf("upload\\/"));
						xmlToFilePath = xmlToFilePath.replace("upload\\/", "upload/");
					}
					else {
						xmlToFilePath = "/"+xmlToFilePath.substring(xmlToFilePath.indexOf("upload/"));
					}
					if(xmlToFilePath.indexOf(/*File.separator*/"/" + "upload") != -1){
						xmlToFilePath = xmlToFilePath.substring(xmlToFilePath.indexOf(/*File.separator*/"/" + "upload") + 7, xmlToFilePath.length());
					}
				}

				trainingmeteoData.meteourl = xmlToFilePath;
				// 退避させた古い登録ファイルを削除する
				boolean deleteflag = files_rename.delete();
				if(!deleteflag){
					logger.error(lang.__("Failed to delete past file of external data. :") + filepath_rename);
				}
				logger.info("External Meteo file upload : " + xmlToFilePath);
			}
			if(meteoId > 0){
				trainingmeteoDataService.update(trainingmeteoData);
			}else{
				trainingmeteoDataService.insert(trainingmeteoData);
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
		}

		setupModel(model);
		return "/admin/training/edit";
	}

	/**
	 * 訓練情報の論理削除
	 * @return JSPに結果が挿入されたJSONを返却する
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/deleteTrainingPlanData"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String deleteTrainingPlanData(){
		try{
			// JSON返却用
			JSONObject json = new JSONObject();
			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// 削除対象のplanidを取得する
			String planIdS = request.getParameter("trainingplandataid");
			long planId = Long.parseLong(planIdS);
			// 一旦planidを元にデータを取得する
			TrainingplanData trainingplanData = null;
			if(planId > 0){
				trainingplanData = trainingplanDataService.findById(planId);
				if(trainingplanData != null){
					// データが存在していれば論理削除する
					trainingplanData.deleted = true;
					trainingplanDataService.update(trainingplanData);
					json.put("message", lang.__("Deletion processing has completed"));
					json.put("trainingplandataid", trainingplanData.id);
				}else{
					json.put("message", lang.__("Selected training plan has been deleted.") + " id = " + planId);
					logger.error(lang.__("Target training plan not found.") + "  id = " + planId);
				}
			}else{
				json.put("message", lang.__("Training plan has not been selected."));
				logger.error(lang.__("Unable to delete because training plan id equals to 0"));
			}
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString());

			return json.toString();
		}catch(NumberFormatException e){
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 外部データの削除
	 * @return null(JSONレスポンス)
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/deleteExternalData"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String deleteExternalData(){
		try{
			// JSON返却用
			JSONObject json = new JSONObject();
			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// 削除対象のmeteoidを取得する
			String meteoIdS = request.getParameter("meteoid");
			long meteoId = Long.parseLong(meteoIdS);
			// 一旦meteoidを元にデータを取得する
			TrainingmeteoData trainingmeteoData = null;
			if(meteoId > 0){
				trainingmeteoData = trainingmeteoDataService.findById(meteoId);
				if(trainingmeteoData != null){
					// データが存在していれば論理削除する
					trainingmeteoData.deleted = true;
					trainingmeteoDataService.update(trainingmeteoData);
					// 削除の後、登録ファイルも同様に削除する
					String filepath_old = application.getRealPath("/upload/") + trainingmeteoData.meteourl;
					File filepath_old_File = new File(filepath_old);
					boolean deleteflag = filepath_old_File.delete();
					if(!deleteflag){
						logger.error(lang.__("Failed to delete past file of external data. :") + filepath_old);
					}
					json.put("message", lang.__("Deletion processing has completed"));
					json.put("trainingplandataid", trainingmeteoData.trainingplandataid);
				}else{
					json.put("message", lang.__("Selected external data has been deleted.") + " id = " + meteoId);
					logger.error(lang.__("Target external data id not found.") + "  id = " + meteoId);
				}

			}else{
				json.put("message", lang.__("External data has not been selected."));
				logger.error(lang.__("Unable to delete because external data id equals to 0"));
			}
			// 出力
//			PrintWriter out = response.getWriter();
//			out.print( json.toString() );
			return json.toString();
		}catch(NumberFormatException e){
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 外部データの実行
	 * @return JSON Object
	 * @throws ParseException
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/sendExternalData"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String sendExternalData() {

		// 返却用JSON
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try{
			/* パラメータを取得 */
			// 送信する自治体id
			String externalLocalgovSend = request.getParameter("externalLocalgovSend");
			// 外部データid
			String trainingmeteoid = request.getParameter("trainingmeteoid");
			long trainingmeteoID = Long.parseLong(trainingmeteoid);
			// 外部データidに紐付いているか確認して、存在していれば実行
			TrainingmeteoData trainingmeteoData = trainingmeteoDataService.findById(trainingmeteoID);
			if(trainingmeteoData != null && externalLocalgovSend != null){
				// 送信自治体は複数の場合があるので、配列で取得する
				String[] externalLocalgovSendArr = request.getParameterValues("externalLocalgovSend");
				// 返却用JSONArray
				JSONArray jsonarray = new JSONArray();
				for(String localgovinfoid : externalLocalgovSendArr){
					long localgovinfoID = Long.parseLong(localgovinfoid);
					// レコードを取得する
					TrainingplanlinkData trainingplanlinkData = trainingplanlinkDataService.findByLocalgovinfoIdPlaninfoId(trainingmeteoData.trainingplandataid, localgovinfoID);
					if(trainingplanlinkData != null){
						// ここで送信処理を実施する
						Timestamp nowdate = new Timestamp(System.currentTimeMillis());
						trainingplanlinkData.updatetime = nowdate;
						// 送信時間を入れて更新する
						trainingplanlinkDataService.update(trainingplanlinkData);
						// 気象情報取得情報を取得する
						BeanMap beanmap = new BeanMap();
						beanmap.put("localgovinfoid", localgovinfoID);
						beanmap.put("meteotypeid", trainingmeteoData.meteotypeid);
						List<MeteorequestInfo> meteorequestList = meteorequestInfoService.findByCondition(beanmap);
						if(meteorequestList.size() > 0){
							// 運用上は1つしかないはずだが、システム上2つ以上登録出来てしまうので、1つ目を固定的に取得する
							MeteorequestInfo meteoRequest = meteorequestList.get(0);
							String urls = application.getRealPath("/upload/") + trainingmeteoData.meteourl;
							// XMLのパース
							File jmaxml = new File(urls);
							String[] datas = meteoParseXMLService.parseMeteoXML(jmaxml, localgovinfoID, trainingmeteoData.meteotypeid, meteoRequest.meteoareaid);
							if(datas != null && datas.length > 0){
								// 配信時間を整形して取得
								String reporttimeStr = meteoParseXMLService.getXMLReporttime(datas[1]);
								// テロップ用の文字列を生成
								String telop_mes = meteoParseXMLService.createTelopString(datas, reporttimeStr, trainingmeteoData.meteotypeid);
								// 解析結果が存在すれば実施
								if(!telop_mes.equals("")){
									// 気象庁発表を訓練情報に置き換える
									telop_mes = telop_mes.replaceAll(lang.__("【気象庁発表】"), lang.__("Training info"));
									// アラーム
									if(meteoRequest.alarm){
										List<TrackData> trainingDataList = trackDataService.findByCurrentTrackDatas(localgovinfoID, true);
										meteoParseXMLService.addAlarm(trainingDataList, localgovinfoID, telop_mes, "information", 0);
									}
									// テロップ表示
									if(meteoRequest.view){
										meteoParseXMLService.addTelop(localgovinfoID, telop_mes, trainingmeteoData.meteotypeid, 0);
									}
								}
								// 地震用レイヤの作成
								if (meteoRequest.meteotypeid.equals(Constants.METEO_SHINGENSHINDOJOUHOU)) {//震源・震度に関する情報
									// レイヤ追加フラグが有効になっている場合のみ追加する
									boolean addlayer = false;
									AlertrequestInfoDto requestDto = null;

									// 送信先の気象情報取得情報を検索
									List<MeteorequestInfo> requestInfo = meteorequestInfoService.findByMeteoTypeIDValid(Constants.METEO_SHINGENSHINDOJOUHOU);
									for(MeteorequestInfo info : requestInfo) {
										// 自治体IDが同じかどうか
										if(info.localgovinfoid.equals(localgovinfoID)) {
											MeteotriggerInfo trigger = meteotriggerInfoService.findByMeteorequestInfoId(info.id);
											if(trigger.valid && trigger.addlayer) {
												// TODO: 本来は受信したXMLのエリアコードをチェックすべき
												addlayer = true;
												requestDto = Beans.createAndCopy(AlertrequestInfoDto.class, info).execute();
												break;
											}
										}
									}
									if(addlayer) {
										meteoricEarthQuakeService.inputEarthQuake(jmaxml, null, requestDto);
									}
								}
							}

							// 結果返却用
							JSONObject obj = new JSONObject();
							obj.put("trainingmeteoid", trainingmeteoData.id);
							obj.put("localgovinfoid", localgovinfoID);
							obj.put("updatetime", new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM<!--3-->"), lang.getLocale()).format(nowdate));
							jsonarray.put(obj);
						}
					}
				}
				json.put("sendData", jsonarray);
				json.put("error", "");
			}else{
				if(externalLocalgovSend == null){
					json.put("error", lang.__("Transmission target local gov. has not been selected."));
				}else if(trainingmeteoData == null){
					json.put("error", lang.__("Target external data info does not exist or no permission of selected local gov."));
				}
			}
			// 出力
/*			PrintWriter out = response.getWriter();
			out.print( json.toString() );*/
			return json.toString();
		}catch(NumberFormatException e){
			try {
				json.put("error", lang.__("Failed to register. Plan ID value is invalid."));
				PrintWriter out = response.getWriter();
				out.print( json.toString() );
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 外部データの実行(訓練中の全ての自治体に一括変更)
	 * @return JSON Object
	 * @throws ParseException
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/sendExternalDataAll"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String sendExternalDataAll() {
		// 返却用JSON
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try{
			/* パラメータを取得 */
			// 訓練プランでリンクしている自治体id
			// 訓練プランID
			String trainingplanid = request.getParameter("trainingplandataid");
			// 外部データid
			String trainingmeteoid = request.getParameter("trainingmeteoid");
			long trainingplanID = Long.parseLong(trainingplanid);
			long trainingmeteoID = Long.parseLong(trainingmeteoid);
			// 外部データidに紐付いているか確認して、存在していれば実行
			TrainingmeteoData trainingmeteoData = trainingmeteoDataService.findByIdAndPlanId(trainingmeteoID, trainingplanID);
			if(trainingmeteoData != null){
				// 訓練プラン連携自治体を取得する
				List<TrainingplanlinkData> trainingplanlinkDataList = trainingplanlinkDataService.findByTrainingplandataId(trainingmeteoData.trainingplandataid);
				// 返却用JSONArray
				JSONArray jsonarray = new JSONArray();
				// リンク情報を検索
				for(TrainingplanlinkData linkData : trainingplanlinkDataList){
					// 訓練モード中であれば実施
					List<TrackData> trackdataList = trackDataService.findByCurrentTrackDatas(linkData.localgovinfoid, true);
					// 訓練プランIDを探す
					boolean sendFlag = false;
					for(TrackData td : trackdataList){
						if(td.trainingplandataid.equals(trainingplanID)){
							sendFlag = true;
							break;
						}
					}
					// 訓練モードでない or 訓練モードの訓練プランがこのプランIDと違う場合は何もしない
					if(!sendFlag) continue;
					// 更新時刻を更新する
					Timestamp nowdate = new Timestamp(System.currentTimeMillis());
					linkData.updatetime = nowdate;
					// 送信時間を入れて更新する
					trainingplanlinkDataService.update(linkData);

					// 気象情報取得情報を取得する
					BeanMap beanmap = new BeanMap();
					beanmap.put("localgovinfoid", linkData.localgovinfoid);
					beanmap.put("meteotypeid", trainingmeteoData.meteotypeid);
					List<MeteorequestInfo> meteorequestList = meteorequestInfoService.findByCondition(beanmap);
					if(meteorequestList.size() > 0){
						// 運用上は1つしかないはずだが、システム上2つ以上登録出来てしまうので、1つ目を固定的に取得する
						MeteorequestInfo meteoRequest = meteorequestList.get(0);
						String urls = application.getRealPath("/upload/") + trainingmeteoData.meteourl;
						// XMLのパース
						File jmaxml = new File(urls);
						String[] datas = meteoParseXMLService.parseMeteoXML(jmaxml, linkData.localgovinfoid, trainingmeteoData.meteotypeid, meteoRequest.meteoareaid);
						if(datas != null && datas.length > 0){
							// 配信時間を整形して取得
							String reporttimeStr = meteoParseXMLService.getXMLReporttime(datas[1]);
							// テロップ用の文字列を生成
							String telop_mes = meteoParseXMLService.createTelopString(datas, reporttimeStr, trainingmeteoData.meteotypeid);
							// 解析結果が存在すれば実施
							if(!telop_mes.equals("")){
								// 気象庁発表を訓練情報に置き換える
								telop_mes = telop_mes.replaceAll(lang.__("【気象庁発表】"), lang.__("Training info"));
								// アラーム
								if(meteoRequest.alarm){
									List<TrackData> trainingDataList = trackDataService.findByCurrentTrackDatas(linkData.localgovinfoid, true);
									meteoParseXMLService.addAlarm(trainingDataList, linkData.localgovinfoid, telop_mes, "information", 0);
								}
								// テロップ表示
								if(meteoRequest.view){
									meteoParseXMLService.addTelop(linkData.localgovinfoid, telop_mes, trainingmeteoData.meteotypeid, 0);
								}
							}
						}

						// 結果返却用
						JSONObject obj = new JSONObject();
						obj.put("trainingmeteoid", trainingmeteoData.id);
						obj.put("localgovinfoid", linkData.localgovinfoid);
						obj.put("updatetime", new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM<!--3-->"), lang.getLocale()).format(nowdate));
						jsonarray.put(obj);
					}
				}
				json.put("sendData", jsonarray);
				json.put("error", "");
			}else{
				json.put("error", lang.__("Target external data info does not exist or no permission of selected local gov."));
			}
			// 出力
/*			PrintWriter out = response.getWriter();
			out.print( json.toString() );*/
			return json.toString();
		}catch(NumberFormatException e){
			try {
				json.put("error", lang.__("Failed to register. Plan ID value is invalid."));
				PrintWriter out = response.getWriter();
				out.print( json.toString() );
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 訓練モードに移行する
	 * @param localgovinfoid : 移行する自治体id
	 * @param trainingplandataid : 使用する訓練プランid
	 * @param json : エラー時のメッセージ返却用
	 * @return null(JSONレスポンス)
	 */
	@ResponseBody
	@TransactionAttribute(TransactionAttributeType.NEVER)
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/doTraining"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String doTraining() {

		// 返却用JSON
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// パラメータを取得
		String localgovinfoid_str = request.getParameter("localgovinfoid");
		String trainingplandataid_str = request.getParameter("trainingplandataid");
		long localgovinfoid = 0L;
		long trainingplandataid = 0L;
		try{
			// error出力用
			json.put("error", "");

			if(localgovinfoid_str == null || trainingplandataid_str == null){
				json.put("error", lang.__("Not selected local gov. info or training plan info."));
			}else{
				localgovinfoid = Long.parseLong(localgovinfoid_str);
				trainingplandataid = Long.parseLong(trainingplandataid_str);
				json.put("trainingModeName", lang.__("Undergoing training"));
				json.put("localgovinfoid", localgovinfoid);
			}
		}catch(NumberFormatException e){
			logger.error(lang.__("Failed to cast Long type in insertTrainingData"));
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 訓練情報の登録
		TrackData entity = null;
		TrainingplanData trainingplanData = null;
		try{
			// すでに記録データがあるかどうか
			List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
			// 災害モード中により中止
			if(trackDatas.size() > 0){
				logger.error(lang.__("For the selected local gov. of disaster mode, it can not be shifted to the training mode."));
				//json.put("error", "選択した自治体が災害モードの為、訓練モードに移行出来ません");
				//json.put("trainingModeName", "災害中");
				return null;
			}

			// 実施する自治体が、すでに訓練データを持っている場合は中止する
			List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid, true);
			// 訓練モード中により中止
			if(trainingDatas.size() > 0){
				logger.error(lang.__("For the selected local gov. in training mode, new training mode can not be added. localgovinfoid = ") + localgovinfoid);
				//json.put("error", "選択した自治体が訓練モード中の為、新たに訓練モードを追加する事が出来ません");
				return null;
			}

			// 訓練プランを取得する
			trainingplanData = trainingplanDataService.findNotDeletedById(trainingplandataid);
			if(trainingplanData == null){
				logger.error(lang.__("For the selected local gov. in training mode, new training mode can not be added.")+" trainingplandataid = " + trainingplandataid);
				//json.put("error", "選択した自治体が訓練モード中の為、新たに訓練モードを追加する事が出来ません");
				return null;
			}

			// 自治体情報を取得する
			LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
			if(localgovInfo == null){
				logger.error(lang.__("Selected local gov. is not found."));
				//json.put("error", "選択した自治体情報が見つかりません");
				return null;
			}

			// 外部データXMLファイルの時刻情報を、訓練開始時の時刻に更新する
			if(! updateJmaxmlTime(localgovinfoid, trainingplandataid)){
				logger.error(lang.__("Failed to update the time of external XML file."));
				return null;
			}
		}catch(Exception e){
			logger.error(lang.__("Selected local gov. is not found."), e);
			return null;
		}

		// 複数同時訓練は想定していないので、trainingmapinfoid(trackmapinfoid)はnullのまま
		//Long trainingmapinfoid = null;
		// 同じく、複数同時訓練は想定していないので、地図作成フラグもfalseで固定
		boolean existTrackData = false;

		entity = new TrackData();

		// 訓練の登録のみ別トランザクション
		try {
			userTransaction.begin();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			//entity = Beans.createAndCopy(TrackData.class, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
			entity.localgovinfoid = localgovinfoid;
			entity.demoinfoid = 0L;
			//entity.disasterid = trainingplanData.disasterid;
			entity.name = lang.__("[Training]") + trainingplanData.name;
			entity.note = trainingplanData.note;
			entity.starttime = now;
			entity.endtime = null;
			entity.trackmapinfoid = null; // 将来的に複数同時訓練の場合に指定する
			entity.trainingplandataid = trainingplandataid;
			entity.deleted = false;
			trackDataService.insert(entity);

			// 災害グループがあれば登録
			//trackgroupDataService.update(entity.id, ListUtil.toLongList(trackForm.citytrackdataids));
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {e1.printStackTrace();}
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {e.printStackTrace();}
		}

		//地図作成
		if(existTrackData==false) {
			boolean create = true;
			// 地図を作成する際は、システム管理者を利用する
			GroupInfo groupInfo = groupInfoService.findById(0L);
			groupInfo.ecomuser = "admin";
			try {
				userTransaction.begin();
				//地図、レイヤの作成
				boolean copyMap = true; // 訓練時はマップ複製
				if (!trackDataService.createDisasterMap(localgovinfoid, groupInfo, entity, copyMap)) {
					create = false;
					userTransaction.setRollbackOnly();
				}

			} catch (Exception e) {
				e.printStackTrace();
				//logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
				logger.error("", e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {e1.printStackTrace();}
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
						userTransaction.commit();
					} else {
						userTransaction.rollback();
					}
				} catch (Exception e) {e.printStackTrace();}
			}

			if (!create) {//作成失敗で災害を削除
				try {
					userTransaction.begin();
					trackDataService.delete(entity);
				} catch (Exception e) {
					logger.error("", e);
					e.printStackTrace();
					try {
						userTransaction.setRollbackOnly();
					} catch (Exception e1) {e1.printStackTrace();}
				} finally {
					try {
						if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
							userTransaction.commit();
						} else {
							userTransaction.rollback();
						}
					} catch (Exception e) {e.printStackTrace();}
				}
				throw new ServiceException(lang.__("Unable to register disaster because failed to create map or layer."));
			}
		}
		// 地図作成済み(訓練モードとしてはここに入る事は現状無し)
		else {
			// 地図情報の更新
			trackDataService.updateMapInfo(entity.trackmapinfoid);
		}

		/*
		// 災害モード
    	loginDataDto.getTrackdataid() = entity.id;
    	loginDataDto.getDisasterid() = entity.disasterid;
    	loginDataDto.isEdiable() = true;
		 */

		/*
		// 出力
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print( json.toString() );
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		*/

		return  json.toString();
	}

	/**
	 * 訓練情報の複製
	 * @return JSPに結果が挿入されたJSONを返却する
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/planCopy/{copyTrainingplanId}"})
	public String planCopy(Map<String,Object>model,
			@Valid @ModelAttribute TrainingplanForm trainingplanForm, BindingResult bindingResult) {
		this.trainingplanForm = trainingplanForm;
		try{
			// コピー元の訓練プランを取得する
			long orgTrainingplanId = Long.valueOf(trainingplanForm.copyTrainingplanId);
			TrainingplanData org_trainingplanData = trainingplanDataService.findNotDeletedById(orgTrainingplanId);
			if(org_trainingplanData == null){
				logger.error("Trainingplan Copy Id is NULL!");
			}
			// コピー元の訓練プランのidとnameとupdatetimeを更新する
			org_trainingplanData.id = null;
			org_trainingplanData.name = "Copy_" + org_trainingplanData.name;
			org_trainingplanData.updatetime = new Timestamp(System.currentTimeMillis());
			trainingplanDataService.insert(org_trainingplanData);

			// プランと紐付くメテオ情報とリンク情報も同様にコピーする
			List<TrainingplanlinkData> org_trainingplanlinkDataList = trainingplanlinkDataService.findByTrainingplandataId(orgTrainingplanId);
			List<TrainingmeteoData> org_trainingmeteoDataList = trainingmeteoDataService.findByPlandataId(orgTrainingplanId);
			if(org_trainingplanlinkDataList != null){
				for(TrainingplanlinkData linkData : org_trainingplanlinkDataList){
					linkData.id = null;
					linkData.updatetime = new Timestamp(System.currentTimeMillis());
					linkData.trainingplandataid = org_trainingplanData.id;
					trainingplanlinkDataService.insert(linkData);
				}
			}
			if(org_trainingmeteoDataList != null){
				for(TrainingmeteoData meteo : org_trainingmeteoDataList){
					meteo.id = null;
					meteo.updatetime = new Timestamp(System.currentTimeMillis());
					meteo.trainingplandataid = org_trainingplanData.id;
					// ファイルも複製する
					String urls = application.getRealPath(File.separator + "upload" + File.separator) + meteo.meteourl;
					File xmlFile = new File(urls);
					// ファイルが存在していれば、コピーして登録する
					if(xmlFile.exists()){
						// 複製名の取得
						String copyName = filenameCheck(application.getRealPath(File.separator + "upload" + File.separator), meteo.meteourl, "_copy");
						// ファイルの複製
						Files.copy(xmlFile.toPath(), new File(copyName).toPath(), StandardCopyOption.REPLACE_EXISTING);
						if(copyName.indexOf(File.separator + "upload") != -1){
							copyName = copyName.substring(copyName.indexOf(File.separator + "upload") + 7, copyName.length());
						}
						meteo.meteourl = copyName;
						trainingmeteoDataService.insert(meteo);
					}
				}
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/admin/training/";
	}

	/**
	 * 連携先県コードを取得する
	 * @param localgovInfo 連携先県コードを調べたい自治体情報
	 * @return 連携先県コード
	 */
	public long havePrefLocalgovinfoid(LocalgovInfo localgovInfo){
		// 県の場合は、連携する自治体を検索する
		if(Localgovtype.PREF.equals(localgovInfo.localgovtypeid)) {
			return localgovInfo.id;
		}
		// 市町村の場合は連携している県を持っていれば、その県に連携する市町村を検索する
		else{
			return localgovInfo.preflocalgovinfoid == null ? 0 : localgovInfo.preflocalgovinfoid;
		}
	}


	/**
	 * ファイル名の重複チェックし、重複の場合は番号を付加して新ファイル名を返す
	 * @param path ファイルパス
	 * @param filename ファイル名
	 * @param addName ファイル名に付与する名称 (_del, _copy等)
	 * @return 重複しない新ファイル名
	 */
	public String filenameCheck(String path, String filename, String addName){
		File checkfile = new File(path + filename);
		String filename_cut = filename;
		String kaku = "";
		if(filename.indexOf(".") != -1){
			filename_cut = filename.substring(0, filename.indexOf("."));
			kaku = filename.substring(filename.indexOf("."), filename.length());
		}
		// ファイルが存在していたらリネームして対応
		if(checkfile.exists()){
			int i = 0;
			// 重複対応
			while(checkfile.exists()){
				i++;
				String filepath_rename = path + filename_cut + i + addName + kaku;
				checkfile = new File(filepath_rename);
			}
		}
		return checkfile.getPath();
	}

	/**
	 * 連携自治体のON/OFf管理クラス
	 */
	@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
	public class TrainingplanLinkClass{
		/** 連携自治体ID */
		public long localgovinfoid;
		/** 連携自治体名称 */
		public String localgovname;
		/** ON/OFFフラグ */
		public boolean flags;
		/** 訓練実行の更新時間 */
		public Timestamp updatetime;
		/** 平時、災害、訓練 */
		public String mode;
		/** 災害起動中フラグ */
		public boolean saigaiFlag;
		/**
		 * 初期化
		 * @param localgovinfoid 連携自治体ID
		 * @param localgovname 連携自治体名
		 */
		public TrainingplanLinkClass(long localgovinfoid, String localgovname){
			this.localgovinfoid = localgovinfoid;
			this.localgovname = localgovname;
			this.flags = false;
			this.updatetime = null;
			this.saigaiFlag = false;
		}
		/**
		 * @param flags ON/OFFフラグ
		 * @param updatetime 訓練実行の更新時間
		 */
		public void setFlagAndTime(boolean flags, Timestamp updatetime){
			this.flags = flags;
			this.updatetime = updatetime;
		}
		/**
		 * @param mode 平時、災害、訓練
		 */
		public void setModeName(String mode){
			this.mode = mode;
		}
		/**
		 * @param flags 災害起動中フラグ
		 */
		public void setSaigaiFlag(boolean flags){
			this.saigaiFlag = flags;
		}
		/**
		 * @return 連携自治体ID
		 */
		public long getLocalgovid(){ return this.localgovinfoid; }
		/**
		 * @return 連携自治体名称
		 */
		public String getLocalgovname(){ return this.localgovname; }
	}

	private boolean updateJmaxmlTime(Long localgovinfoid, Long trainingplandataid){

		// 現在時刻を取得し書式整形
		lang.getLangCode();
		String currentDateTimestr = TimeUtil.getCurDateYYYYMMDDHHMMSS2();
		currentDateTimestr = TimeUtil.getCurDateXmlEditorYMDHMS(currentDateTimestr);

		char a[] = {(char)(0x0a)};
		String LF = new String(a);

		// 訓練外部XMLデータ取得
		List<TrainingmeteoData> trainingmeteoDataList = trainingmeteoDataService.findByPlandataId(trainingplandataid);
		for(TrainingmeteoData trainingmeteoData : trainingmeteoDataList){
			FileReader fr = null;
			BufferedReader br = null;

			FileWriter fw = null;
			BufferedWriter bw = null;
			try{
				String urls = application.getRealPath("/upload/") + trainingmeteoData.meteourl;
				File jmaxml = new File(urls);
				File newJmaxml = new File(jmaxml.getAbsolutePath() + ".new");

		        fr = new FileReader(jmaxml);
		        br = new BufferedReader(fr);
		        fw = new FileWriter(newJmaxml, true);
				bw = new BufferedWriter(fw);

		        String line;
		        while ((line = br.readLine()) != null) {
		        	int sIndex = line.indexOf("<DateTime>");
		        	int eIndex  = line.indexOf("</DateTime>");

		        	String newLine;
		        	if(sIndex >= 0 && eIndex >= 0){
		        		StringBuffer sbuf = new StringBuffer();
		        		for(int i = 0; i < sIndex; i++){
		        			sbuf.append(" ");
		        		}
	        			sbuf.append("<DateTime>");
	        			sbuf.append(currentDateTimestr);
	        			sbuf.append("</DateTime>");
	        			sbuf.append(LF);
	        			newLine = sbuf.toString();
		        	}else{
		        		newLine = line + LF;
		        	}

		        	bw.write(newLine);
		        	bw.flush();
		        }
		        br.close();
		        fr.close();
		        bw.close();
		        fw.close();
		        br = null;
		        fr = null;
		        bw = null;
		        fw = null;

		        //
		        String orgPath = jmaxml.getAbsolutePath();
		        String newPath = newJmaxml.getAbsolutePath();
		        FileUtil.fileCopyByPath(newPath, orgPath);
		        newJmaxml.delete();

//				// XMLファイルを読み込みDOMオブジェクトを作成
//				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
//				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
//				InputStream in = new FileInputStream(jmaxml);
//				Document document = docBuilder.parse(in);
//				in.close();
//
//				// DateTime要素を取得し更新
//				Node dateTimeNode = document.getElementsByTagName("DateTime").item(0);
//				dateTimeNode.getFirstChild().setNodeValue(currentDateTimestr);
//
//				// DOMオブジェクトをファイルに出力
//				TransformerFactory transFactory = TransformerFactory.newInstance();
//				Transformer transformer = transFactory.newTransformer();
//				DOMSource source = new DOMSource(document);
//				FileOutputStream os = new FileOutputStream(jmaxml);
//				StreamResult result = new StreamResult(os);
//				transformer.transform(source, result);
//				os.close();
			}catch(IOException ie){
				return false;
//			}catch(SAXException se){
//				return false;
//			}catch(ParserConfigurationException pe){
//				return false;
//			}catch(TransformerException te){
//				return false;
			}finally{
				try{
					if(br != null){
						br.close();
					}
					if(fr != null){
						fr.close();
					}
					if(bw != null){
						bw.close();
					}
					if(fw != null){
						fw.close();
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * サーバ上の防災情報XMLファイルのパスリストをJSON形式で返却する
	 * @return
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/training/loadBousaixmlFileList"})
	public String loadBousaixmlFileList() {
		try{
			// JSON返却用
			JSONObject json = new JSONObject();
			json.put("message", lang.__("In Self-local gov., corresponding weather info type has not been registered. Set the info on admin window."));
			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			// 訓練プランの自治体IDを取得
			String trainingplandataid = request.getParameter("trainingplandataid");
			String meteoTypeid = request.getParameter("checkMeteoTypeid");
			TrainingplanData  trainingplanData = trainingplanDataService.findById(Long.parseLong(trainingplandataid));
			String localgovinfoidStr = Long.toString(trainingplanData.localgovinfoid);

			// 防災情報XMLファイルのパスを取得
			XmlFileMgrService xmlFileMgrService = new XmlFileMgrService();
			xmlFileMgrService.initProp();
			String strXml = "/WEB-INF/" + xmlFileMgrService.getPropData().strMETEOXMLEDIT_PATH;
			MeteotypeMaster meteotypeMaster = meteotypeMasterService.findById(Integer.parseInt(meteoTypeid));
			String xmlDirPath = strXml + "/" + localgovinfoidStr + "/" + meteotypeMaster.type + "/";
			xmlDirPath = StringUtil.replaceAll("//", "/", xmlDirPath);
			File xmlDir = new File(application.getRealPath(xmlDirPath));
			List<String> fileList = new ArrayList<String>();
			if(xmlDir.exists()){
				File [] files = xmlDir.listFiles();
				if(files != null && files.length > 0){
					for(int i = 0; i < files.length; i++){
						File file = files[i];
						String fileName = file.getName();
						String [] sp = FileUtil.getSeparatedFilename(fileName);
						if(sp != null){
							if(sp[1].equals("xml")){
								fileList.add(meteotypeMaster.id + "," + meteotypeMaster.name + "," + fileName);
							}
						}
					}
				}
			}

			// ダイアログ内のHTML作成
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("<h2>");
			sbuf.append(lang.__("Disaster info type"));
			sbuf.append(":");
			sbuf.append(meteotypeMaster.name);
			sbuf.append("</h2>");
			if(fileList.size() > 0){
				sbuf.append("<table>");
				sbuf.append("<tr>");
				sbuf.append("<th>");
				sbuf.append(lang.__("Select a file."));
				sbuf.append("</th>");
				sbuf.append("</tr>");
				int index  = 1;
				for(String fileStr : fileList){
					String [] fileStrArray = fileStr.split(",");
					sbuf.append("<tr>");
					sbuf.append("<td>");
					sbuf.append("<a href=\"#\" onClick=\"setServerSendBousaiXmlFile('");
					sbuf.append(index);
					sbuf.append("');\">");
					sbuf.append(fileStrArray[2]);
					sbuf.append("</a>");
					sbuf.append("<input type=\"hidden\" id=\"bousaixmlfile_lists_type_");
					sbuf.append(index);
					sbuf.append("\" value=\"");
					sbuf.append(fileStrArray[0]);
					sbuf.append("\">");
					sbuf.append("<input type=\"hidden\" id=\"bousaixmlfile_lists_filename_");
					sbuf.append(index);
					sbuf.append("\" value=\"");
					sbuf.append(fileStrArray[2]);
					sbuf.append("\">");
					sbuf.append("</td>");
					sbuf.append("</tr>");
					index++;
				}
				sbuf.append("</table>");
			}else{
				sbuf.append("<p>");
				sbuf.append(lang.__("On the server,"));
				sbuf.append(meteotypeMaster.name);
				sbuf.append(" ");
				sbuf.append(lang.__("meteorological xml file not found."));
				sbuf.append("</p>");
			}
			json.put("filesContent", sbuf.toString());
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );

			return json.toString();
		}catch(JSONException e){
			e.printStackTrace();
		}

		return null;
	}
}
