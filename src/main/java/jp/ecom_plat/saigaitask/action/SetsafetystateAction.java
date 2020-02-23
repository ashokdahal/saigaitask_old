/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.SafetystateInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.form.SetsafetystateForm;
import jp.ecom_plat.saigaitask.service.db.AssemblestateDataService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.SafetystateInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import jp.ecom_plat.saigaitask.util.StringUtil;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class SetsafetystateAction extends AbstractAction{

	/** アクションフォーム */
	protected SetsafetystateForm setsafetystateForm;

	public String comment;
	public Map<Integer,String> safetyStateListMap;

	@Resource
	protected AssemblestateDataService assemblestateDataService;
	@Resource
	protected SafetystateInfoService safetystateInfoService;
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected UserInfoService userInfoService;
	@Resource
	protected UnitInfoService unitInfoService;
	@Resource
	protected GroupInfoService groupInfoService;

	public String hiddenEncryptparam;
	public String trackName;
	public String groupName;
	public String errorMessage;

	private static final String ERROR_BADPARAM = "1";
	private static final String ERROR_NOSANSYU = "2";

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("comment", comment);
		model.put("safetyStateListMap", safetyStateListMap);
		model.put("hiddenEncryptparam", hiddenEncryptparam);
		model.put("trackName", trackName);
		model.put("groupName", groupName);
		model.put("errorMessage", errorMessage);
	}

	/**
	 * 職員参集応答画面を表示する
	 * @return フォワード先
	 */
	@RequestMapping(value="/setsafetystate/show/{encryptparam}")
	public String index(Map<String,Object>model, @ModelAttribute SetsafetystateForm setsafetystateForm) {

    	hiddenEncryptparam = setsafetystateForm.encryptparam;
		Map <String, String> params = openParam(setsafetystateForm.encryptparam);
		if(params == null){
			return "error/" + ERROR_BADPARAM;
		}
		String trackdataid = params.get("trackdataid");
		String localgovinfoid = params.get("localgovinfoid");
		String userid = params.get("userid");

		// 参集報告対象かチェック
		AssemblestateData assemblestateData = assemblestateDataService.findByTrackdataidAndUserid(Long.parseLong(trackdataid), Long.parseLong(userid));
		if (assemblestateData == null) {
			return "error/" + ERROR_NOSANSYU;
		}else{

			// ログイン済みかチェックし、未ログインであればログイン画面へ遷移
			if( assemblestateData.loginstatetus == null || (! assemblestateData.loginstatetus)){
				StringBuffer sbuf = new StringBuffer();
				sbuf.append("forward:/setsafetystate/login/");
				sbuf.append(setsafetystateForm.encryptparam);
				return sbuf.toString();
			}

			// 参集状況セレクトボックスの作成
			safetyStateListMap = new HashMap<Integer, String>();
			List<SafetystateInfo> safetystateInfoList = safetystateInfoService.findByLoaclgovInfoId(Long.parseLong(localgovinfoid));
			for(SafetystateInfo safetystateInfo : safetystateInfoList){
				safetyStateListMap.put(safetystateInfo.id, safetystateInfo.name);
			}

			// コメントの設定
			if(org.seasar.framework.util.StringUtil.isEmpty(assemblestateData.comment)){
				comment = "";
			}else{
				comment = assemblestateData.comment;
			}

			setupModel(model);

			// 入力画面
			return "/setsafetystate/index";
		}
	}

	/**
	 * ログイン画面を表示する
	 * @return フォワード先
	 */
	@RequestMapping(value="/setsafetystate/login/{encryptparam}")
	public String login(Map<String,Object>model, @ModelAttribute SetsafetystateForm setsafetystateForm) {
		hiddenEncryptparam = setsafetystateForm.encryptparam;
		Map <String, String> params = openParam(setsafetystateForm.encryptparam);
		String trackdataid = params.get("trackdataid");
		String userid = params.get("userid");


		// 災害名の取得
		trackName = "";
		TrackData trackData = trackDataService.findById(Long.parseLong(trackdataid));
		if(trackData != null){
			trackName = trackData.name;
		}

		// 班名の取得
		groupName = "";
		GroupInfo groupInfo = getGroupInfo(userid);
		if(groupInfo != null){
			groupName = groupInfo.name;
		}

		setupModel(model);

		return "/setsafetystate/login";
	}

	/**
	 * エラー画面を表示する
	 * @return フォワード先
	 */
	@RequestMapping(value="/setsafetystate/error/{errorcode}")
	public String showError(Map<String,Object>model, @ModelAttribute SetsafetystateForm setsafetystateForm) {
		String errorCode = setsafetystateForm.errorcode;
		switch (errorCode) {
		case ERROR_BADPARAM:
			errorMessage = lang.__("Parameter is invalid.");
			break;
		case ERROR_NOSANSYU:
			errorMessage = lang.__("Staff request email is not send.");
			break;
		default:
			break;
		}

		setupModel(model);

		return "/setsafetystate/error";
	}


	/**
	 * 参集応答を保存する
	 * @return フォワード先
	 */
	@RequestMapping(value="/setsafetystate/save")
	public String seveSafetystate(Map<String,Object>model, @ModelAttribute SetsafetystateForm setsafetystateForm) {
		hiddenEncryptparam = setsafetystateForm.encryptparam;
		Map <String, String> params = openParam(setsafetystateForm.encryptparam);
		String trackdataid = params.get("trackdataid");
		String userid = params.get("userid");

		AssemblestateData assemblestateData = assemblestateDataService.findByTrackdataidAndUserid(Long.parseLong(trackdataid), Long.parseLong(userid));

		if (assemblestateData!= null) {
			if(! assemblestateData.loginstatetus){
				StringBuffer sbuf = new StringBuffer();
				sbuf.append("login/");
				sbuf.append(setsafetystateForm.encryptparam);
				setupModel(model);
				return sbuf.toString();
			}

			Timestamp now = new Timestamp(System.currentTimeMillis());
			assemblestateData.updatetime = now;
			assemblestateData.safetystateinfoid = Integer.parseInt(setsafetystateForm.selectedSafetystate);
			assemblestateData.comment = setsafetystateForm.comment;

			// ログアウト
			assemblestateData.loginstatetus = Boolean.FALSE;
			assemblestateDataService.update(assemblestateData);
		}else{
			setupModel(model);
			return "error/" + ERROR_NOSANSYU;
		}

		setupModel(model);
		return "/setsafetystate/done";
	}

	/**
	 * ログイン処理を行う
	 * @return フォワード先
	 */
	@RequestMapping(value="/setsafetystate/dologin")
	public String dologin(@ModelAttribute SetsafetystateForm setsafetystateForm) {
		hiddenEncryptparam = setsafetystateForm.encryptparam;
		Map <String, String> params = openParam(setsafetystateForm.encryptparam);
		String trackdataid = params.get("trackdataid");
		String userid = params.get("userid");

		AssemblestateData assemblestateData = assemblestateDataService.findByTrackdataidAndUserid(Long.parseLong(trackdataid), Long.parseLong(userid));
		assemblestateData.loginstatetus = Boolean.TRUE;
		assemblestateDataService.update(assemblestateData);

		StringBuffer sbuf = new StringBuffer();
		sbuf.append("forward:/setsafetystate/show/");
		sbuf.append(setsafetystateForm.encryptparam);
		return sbuf.toString();
	}

    /**
     * ログインのバリデータ
     * @return エラー
     */
    public ActionMessages validateDoLogin()
    {
		// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	ActionMessages errors = new ActionMessages();

		hiddenEncryptparam = setsafetystateForm.encryptparam;
		Map <String, String> params = openParam(setsafetystateForm.encryptparam);
		String userid = params.get("userid");
		GroupInfo groupInfo = getGroupInfo(userid);

    	//0:system
    	GroupInfo authGroupInfo = groupInfoService.findByLoginIdAndPasswordAndValid(groupInfo.id, setsafetystateForm.password);
    	if (authGroupInfo == null) {
    		// エラー情報生成
    		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Invalid {0}",lang.__("Password")), false));
    	}
    	return errors;
    }

    /**
     * 暗号化されたパラメータを復号する
     * @param encryptParam
     * @return 復号されたパラメータのMap
     */
    private Map<String, String> openParam(String encryptParam){
    	Map<String, String> retValue = null;

    	try{
        	String planeParan = StringUtil.decrypt(URLDecoder.decode(encryptParam, "UTF-8"));
        	String [] params = planeParan.split(",");
        	if(params != null && params.length == 3){
        		retValue = new HashMap<String, String>();
        		retValue.put("trackdataid", params[0]);
        		retValue.put("localgovinfoid", params[1]);
        		retValue.put("userid", params[2]);
        	}
    	}catch(UnsupportedEncodingException e){
    		return null;
    	}


    	return retValue;
    }

    private GroupInfo getGroupInfo(String userid){

    	GroupInfo groupInfo = null;
    	UserInfo  userInfo  = userInfoService.findById(Long.parseLong(userid));
		if(userInfo != null){
			//UnitInfo unitInfo = unitInfoService.findById(userInfo.unitid);
			//if(unitInfo != null){
				//groupInfo = groupInfoService.findById(unitInfo.groupid);
				groupInfo = groupInfoService.findById(userInfo.groupid);
			//}
		}

    	return groupInfo;
    }
}
