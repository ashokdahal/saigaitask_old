package jp.ecom_plat.saigaitask.action;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.framework.util.StringUtil;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.service.LoginService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;

/**
 * ログイン画面のアクションクラス
 * 利用者画面用、モバイル画面用、管理画面用などいくつかログイン画面があるが、
 * サーバ側の処理はこのアクションクラスで共通化する。
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class LoginAction extends AbstractAction {

	LoginForm loginForm;
	
    /** 災害対応中の記録データリスト */
    public List<TrackData> trackDatas;

    /** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 災害情報マスターサービス */
    @Resource protected DisasterMasterService disasterMasterService;

    /** 班情報サービス */
    @Resource protected GroupInfoService groupInfoService;
    /** 班情報 */
    public GroupInfo groupInfo;
    /** 課情報サービス */
    @Resource protected UnitInfoService unitInfoService;
    /** 課情報 */
    protected UnitInfo unitInfo;
    /** 災害結合済み */
    public Boolean disasterCombined = false;
    
    /** 記録データサービス */
    @Resource protected TrackDataService trackDataService;

	/** メニューログイン情報サービス */
	@Resource MenuloginInfoService menuloginInfoService;


    /** 言語情報リスト */
	public List<MultilangInfo> multilangInfoItems;

	/** 職員参集に対するURLでの安否状況更新メッセージ表示用 */
	public String message;

	/** マスター確認 */
	public boolean bmaster = false;

	/** 表示タブ */
    public String currenttab = "0";

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("trackDatas", trackDatas);
		model.put("disasterItems", disasterItems);
		model.put("groupInfo", groupInfo);
		model.put("disasterCombined", disasterCombined);
		model.put("multilangInfoItems", multilangInfoItems);
		model.put("message", message);
		model.put("bmaster", bmaster);
		model.put("currenttab", currenttab);

		model.put("loginForm", loginForm);
	}
	
	/**
     * Login page display
     * @return login page
     */
    @org.springframework.web.bind.annotation.RequestMapping("/login")
	public String index(Map<String,Object>model,
			@ModelAttribute LoginForm loginForm, BindingResult bindingResult) {
    	this.loginForm = loginForm;

    	// ログイン画面の種別を初期化
    	if(StringUtil.isEmpty(loginForm.type)) {
    		loginForm.type = "page";

    		// ログイン画面を表示する前にアクセスしようとしたURLから、
    		// 表示するログイン画面を決める
    		RequestCache requestCache = new HttpSessionRequestCache();
    		SavedRequest savedRequest = requestCache.getRequest(request, response);
    		if(savedRequest!=null) {
        		String redirectUrl = savedRequest.getRedirectUrl();
        		try {
            		URL url = new URL(redirectUrl);
            		String path = url.getPath();
            		// 管理画面にアクセスしようとした場合
            		if(path.startsWith(request.getContextPath()+"/admin")) {
            			loginForm.type = "admin";
            		}
            		// モバイル画面にアクセスしようとした場合
            		else if(path.startsWith(request.getContextPath()+"/mob")) {
            			loginForm.type = "mob";
            		}
        		} catch(Exception e) {
        			logger.error("failed to parse SavedRequest.redirectUrl: "+redirectUrl, e);
        		}
    		}
    	}

    	version = Config.getVersionForView();
    	versionDetail = "";
    	if(StringUtil.isNotEmpty(version)) {
    		if(StringUtil.isNotEmpty(versionDetail)) versionDetail += " ";
        	versionDetail += "Ver "+version;
    	}
    	String revision = Config.getRevision();
    	if(StringUtil.isNotEmpty(revision)) {
    		if(StringUtil.isNotEmpty(versionDetail)) versionDetail += " ";
        	versionDetail += "rev."+revision;
    	}

    	// 自治体のデフォルト言語コードをセット
    	session.removeAttribute(Constants.SESSIONPARAM_LANG);
    	SaigaiTaskLangUtils.initLang(request);

    	String master = request.getParameter("master");
    	if (master != null && master.equals("true")) bmaster = true;

    	//String uri = (String)session.getAttribute("return_uri");
    	//StringBuffer buff = request.getRequestURL();
    	String url2 = request.getServerName();
    	System.out.println("url2: "+url2);
    	logger.info("url2: "+url2);
    	long localgovInfoId = 0;
    	//loginForm.localgovinfoid = "17";
    	LocalgovInfo gov = null;
    	if(StringUtil.isNotEmpty(loginForm.localgovinfoid)) {
    		try {
        		gov = localgovInfoService.findById(Long.parseLong(loginForm.localgovinfoid));
    		} catch(Exception e) {
    			logger.warn(lang.__("Failed to login specified local gov. ID {0} ", loginForm.localgovinfoid), e);
    		}
    	}
    	if(gov==null) {
        	gov = localgovInfoService.findByURLAndValid(url2);
    	}
    	if (gov != null) {
    		localgovInfoId = gov.id;
    		if (StringUtil.isNotEmpty(gov.systemname))
    			loginForm.systemname = gov.systemname;
    		disasterCombined = gov.disastercombined;
    	}
    	// システム名称が空ならばデフォルト値をセット
    	if(StringUtil.isEmpty(loginForm.systemname)) loginForm.systemname = lang.__("NIED disaster information sharing system");
    	// FORDEV
    	//localgovInfoId = 17;
    	loginForm.localgovinfoid = Long.toString(localgovInfoId);

    	if (localgovInfoId > 0) {
    		// 災害種別を取得
    		disasterItems = disasterMasterService.findGtZeroOrderBy();
    		HashSet<Integer> usingDisasterids = menuloginInfoService.getUsingDisasterIds(localgovInfoId);
    		if(0<usingDisasterids.size()) {
    			// 未使用の災害種別を逆順で削除していく
    			for(int idx=disasterItems.size()-1; 0<=idx; idx--) {
    				DisasterMaster disasterMaster = disasterItems.get(idx);
    				if(!usingDisasterids.contains(disasterMaster.id)) {
    					disasterItems.remove(idx);
    				}
    			}
    		}

    		// ログインユーザは選択式から入力式に変わったためコメントアウト
    		// 班名を取得
    		//groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(localgovInfoId);
    		// 課名を取得
    		//unitInfoItems = unitInfoService.findByLocalgovInfoIdAndValid(localgovInfoId);
    	}

    	// 対応中の記録データ
    	trackDatas = trackDataService.findByCurrentTrackDatas(localgovInfoId);
    	if(trackDatas.size() == 0){
    		// 訓練中の記録データが存在するか確認する
    		trackDatas = trackDataService.findByCurrentTrackDatas(localgovInfoId, true);
    	}
    	if (trackDatas.size() == 1) {
    		// 1つしか災害がなければ、デフォルトで選択状態にする。
   			loginForm.trackdataid = String.valueOf(trackDatas.get(0).id);
    	}

    	// 地図表示／非表示
    	// デフォルトは表示だが、セッションに情報が残っている場合はそちらを優先
    	String sessionMapVisible = (String)request.getSession().getAttribute(Constants.SESSIONPARAM_MAPVISIBLE);
    	if(sessionMapVisible == null){
    		loginForm.mapVisible = Constants.MAPVISIBLE_VISIBLE;
    	}else{
    		loginForm.mapVisible = sessionMapVisible;
    	}
    	if(StringUtil.isEmpty(loginForm.mapVisible)) {
    		loginForm.mapVisible = Constants.MAPVISIBLE_VISIBLE;
    	}

    	// 言語情報
    	// プルダウンリスト作成
    	multilangInfoItems = multilangInfoService.findAll();
    	/*
    	// 自治体のデフォルト言語コードをセット
    	String langCode = null;
    	if(gov != null){
    		Long multilangInfoId = gov.multilanginfoid;
    		if(multilangInfoId != null){
    			MultilangInfo multilangInfo = multilangInfoService.findById(multilangInfoId);
    			if(multilangInfo != null){
    				langCode = multilangInfo.code;
    			}
    		}
    	}
    	if(langCode == null || langCode.length() <= 0){
    		langCode = SaigaiTaskLangUtils.DEFAULT_LANGUAGE;
    	}
    	*/
    	loginForm.langCode = lang.getLangCode();

    	setupModel(model);

    	if(loginForm.type!=null) {
        	switch(loginForm.type) {
        	case "page": return "/login/login-page";
        	case "mob": return "/login/login-mob";
        	case "admin": return "/login/login-admin";
        	}
    	}

        return "/login/login-page";
	}

	/**
	 * If you fail to log in, you will be forwarded to this URL.
	 * Check input errors and add error messages
	 * @param model
	 * @param loginForm
	 * @param bindingResult
	 * @return
	 */
    @org.springframework.web.bind.annotation.RequestMapping("/login-error")
	public String error(Map<String,Object>model,
			@ModelAttribute LoginForm loginForm, BindingResult bindingResult) {
    	loginForm = new LoginForm(request);
    	this.loginForm = loginForm;
    	
    	List<String> errors = new ArrayList<String>();
    	if(LoginService.isUsualLoginRequest(request)) {
        	if (StringUtil.isEmpty(loginForm.unitid)) {
        		// エラー情報生成
        		errors.add(lang.__("{0} must be required.",lang.__("Unit name<!--2-->")));
        	}
        	if (StringUtil.isEmpty(loginForm.password2)) {
        		// エラー情報生成
        		errors.add(lang.__("{0} must be required.",lang.__("Password")));
        	}

        	if(errors.size()==0) {
            	//0:system
            	try{
                	unitInfo = unitInfoService.findByLoginIdAndPasswordAndValid(unitInfoService.findByName(loginForm.unitid, loginForm.password2, Long.parseLong(loginForm.localgovinfoid)).id, loginForm.password2);
            	}catch(NullPointerException e){
            		unitInfo = null;
            	}
            	if (unitInfo == null) {
            		// エラー情報生成
            		errors.add(lang.__("Invalid {0}",lang.__("Password")));
            	}
        	}
    	}
    	else {
        	if (StringUtil.isEmpty(loginForm.groupid)) {
        		// エラー情報生成
        		errors.add(lang.__("{0} must be required.",lang.__("Group name<!--2-->")));
        	}
        	if (StringUtil.isEmpty(loginForm.password)) {
        		// エラー情報生成
        		errors.add(lang.__("{0} must be required.",lang.__("Password")));
        	}
        	
        	if(errors.size()==0) {
            	//0:system
            	try{
            		groupInfo = groupInfoService.findByLoginIdAndPasswordAndValid(groupInfoService.findByName(loginForm.groupid, loginForm.password, Long.parseLong(loginForm.localgovinfoid)).id, loginForm.password);
            	}catch(NullPointerException e){
            		groupInfo = null;
            		logger.error(e.getMessage(), e);
            	}
            	if (groupInfo == null) {
            		// エラー情報生成
            		errors.add(lang.__("Invalid {0}",lang.__("Password")));
            	}
        	}
    	}

    	request.setAttribute("errors", errors);
    	return index(model, loginForm, bindingResult);
    }    
}
