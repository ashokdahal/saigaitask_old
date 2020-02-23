/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import facebook4j.Account;
import facebook4j.FacebookException;
import facebook4j.Group;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.entity.db.FacebookInfo;
import jp.ecom_plat.saigaitask.entity.db.FacebookMaster;
import jp.ecom_plat.saigaitask.entity.db.FacebookpostdefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.form.page.AssembleForm;
import jp.ecom_plat.saigaitask.form.page.FacebookForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.FacebookInfoService;
import jp.ecom_plat.saigaitask.service.db.FacebookMasterService;
import jp.ecom_plat.saigaitask.service.db.FacebookpostdefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.util.SpringContext;


/**
 * facebookによる周知を表示するアクションクラスです.
 * spring checked take 5/1
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class FacebookAction extends PageBaseAction {

	protected FacebookForm facebookForm;
	@Resource
	protected FacebookInfoService facebookInfoService;
	@Resource
	protected FacebookMasterService facebookMasterService;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected FacebookpostdefaultInfoService facebookpostdefaultInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected FilteredFeatureService filteredFeatureService;

	private final int FACEBOOK_PAGE_TYPE_HOME = 1;	// Facebookホーム
	private final int FACEBOOK_PAGE_TYPE_PAGE = 2;	// Facebookページ
	private final int FACEBOOK_PAGE_TYPE_GROUP = 3;	// Facebookグループ

	/** デフォルト通知グループリスト */
	public List<NoticegroupInfo> noticegroupInfoItems;
	/** Facebookグループ情報リスト */
	public List<Group> facebookgroupInfo;
	/** Facebookアカウント情報リスト */
	public List<Account> facebookaccountInfo;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("facebookgroupInfo", facebookgroupInfo);
		model.put("facebookaccountInfo", facebookaccountInfo);
		model.put("noticeType", NoticeType.FACEBOOK);
		model.put("facebookForm", facebookForm);
	}

	/**
	 * facebookページを表示する.
	 * @return フォワード先
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/facebook","/page/facebook/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute FacebookForm facebookForm) throws ParseException {
		this.facebookForm = facebookForm;
		initPage("facebook", facebookForm);
		ActionMessages errors = new ActionMessages();

		setAdditionalReceiver();

		//インポートデータ
		List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		MenutableInfo mtbl = null;
		if (menutableList.size() > 0) {//TODO:当面は一つだけ
			mtbl = menutableList.get(0);
		}

		if (StringUtil.isBlank(facebookForm.facebookContent)) {
			StringBuffer content = null;
			if (0<pageDto.getMenuInfo().filterInfoList.size())
				content = filteredFeatureService.getFilteredFeatureContext(loginDataDto.getTrackdataid(), pageDto.getMenuInfo(), mtbl);
			else
				content = filteredFeatureService.getAllFeatureContext(loginDataDto.getTrackdataid(), mtbl);
			facebookForm.facebookContent = content.toString();
		}

		/*if (!StringUtil.isBlank((String)session.getAttribute("honbun"))) {
			facebookForm.facebookContent = (String)session.getAttribute("honbun");
			session.removeAttribute("honbun");
		}*/

		// フィルタリング結果を取得
		//setInformationList();
		// フィルタリング結果を挿入
		/*StringBuffer hinanshiji = new StringBuffer();
		StringBuffer hinankankoku = new StringBuffer();

		if (session.getAttribute("REFUGE") != null) {
			for (RefugeInformationDto refugeInformation : refugeInformationList) {
				if (refugeInformation.hatureiKbn.equals("避難指示")) {
					hinanshiji.append("、");
					hinanshiji.append(refugeInformation.chikuName);
				} else if (refugeInformation.hatureiKbn.equals("避難勧告")) {
					hinankankoku.append("、");
					hinankankoku.append(refugeInformation.chikuName);
				}
			}

			if (!StringUtil.isBlank(hinanshiji.toString())) facebookForm.facebookContent = facebookForm.facebookContent + "\n\n【避難指示】" + hinanshiji.toString().substring(1);
			if (!StringUtil.isBlank(hinankankoku.toString())) facebookForm.facebookContent = facebookForm.facebookContent + "\n\n【避難勧告】" + hinankankoku.toString().substring(1);
			session.removeAttribute("REFUGE");
		}*/
		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

		//デフォルト設定
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist)
			facebookForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());

		//Facebook投稿先情報の取得 2014/7/11
		boolean facebookinfoFlg = true;
		try {
			//FacebookグループのページIDとグループ名を取得
			facebookgroupInfo = facebookInfoService.getGroup(loginDataDto.getLocalgovinfoid());
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			facebookinfoFlg = false;
		}

		try {
			// FacebookページのページIDとページ名を取得 2014/7/11
			facebookaccountInfo = facebookInfoService.getAccount(loginDataDto.getLocalgovinfoid());
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			facebookinfoFlg = false;
		}

		try {
			// FacebookタイムラインのページIDとページ名(アカウント名)を取得 2014/7/11
			facebookForm.facebookHomeinfo = facebookInfoService.getId(loginDataDto.getLocalgovinfoid());
			facebookForm.facebookUsername = facebookInfoService.getUsername(loginDataDto.getLocalgovinfoid());
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			facebookinfoFlg = false;
		}

		//Facebook投稿先情報を正しく取得できたか確認(標準アカウントのタイムライン、ページ、グループいずれも取得できない場合は投稿先がないためエラー) 2014/7/11
		//※Facebookサイトでグループやページを作成していない可能性はあるが、標準アカウントのタイムラインは必ずあるため、AppIDなどが間違っていない限り、この条件分岐の中に入ることはない
		if ( facebookgroupInfo == null && facebookaccountInfo == null && (facebookForm.facebookHomeinfo == null || "".equals(facebookForm.facebookHomeinfo) ))
			facebookinfoFlg = false;

		// エラーが見つかった場合、
		if (facebookinfoFlg == false) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Facebook is not properly set. Configuration is needed on admin window.<br />・[Facebook info] Access token<br />・[Facebook master] Application id<br />・[Facebook master] Application secret<br />・[Facebook posted target info] page ID"), false));
			ActionMessagesUtil.addErrors(session, errors);
		} else {
			//デフォルト設定(投稿先)
			List<FacebookpostdefaultInfo> facebookpostdefaultlist = facebookpostdefaultInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id, loginDataDto.getLocalgovinfoid());
			for (FacebookpostdefaultInfo finfo : facebookpostdefaultlist) {
				// リアルタイムにFacebookから取得したpageidとDBのpageidが一致しないとデフォルトチェックがつかないため注意
				switch (finfo.facebookpostInfo.pagetype) {
					case FACEBOOK_PAGE_TYPE_HOME:	// Facebookホーム
						facebookForm.facebookHomeid = finfo.facebookpostInfo.pageid;
						break;
					case FACEBOOK_PAGE_TYPE_PAGE:	// Facebookページ
						facebookForm.facebookaccountinfo.add(finfo.facebookpostInfo.pageid);
						break;
					case FACEBOOK_PAGE_TYPE_GROUP:	// Facebookグループ
						facebookForm.facebookgroupinfo.add(finfo.facebookpostInfo.pageid);
						break;
					default:
						break;
				}
			}
		}

		//トークンを設定
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);

		return "page/facebook/index";
	}

	/**
	 * 送信処理
	 * @return 次のページ
	 * @throws FacebookException
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/facebook/send")
	public String send(Map<String,Object>model,
			@Valid @ModelAttribute FacebookForm facebookForm, BindingResult bindingResult) {
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

		ActionMessages errors = new ActionMessages();

		// 災害時、送信可能かチェック
		if(0<loginDataDto.getTrackdataid()) {
			// 訓練モードで機能制限が入っている場合、JS側で制限されているはずだが、Action側でもチェックする
			pageDto.setTrackData(trackDataService.findById(loginDataDto.getTrackdataid()));
			if(pageDto.getTrackData()!=null && pageDto.getTrackData().trainingplandataid != null){
				if(pageDto.getTrackData().trainingplandataid > 0 && !pageDto.getTrackData().trainingplanData.facebookflag){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Notification function out of service due to training"), false));
				}
			}
		}

		// 管理画面で設定されていない場合、送信不可 2014/7/11
		FacebookMaster facebookMaster = facebookMasterService.find();
		FacebookInfo facebookInfo = facebookInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if (facebookMaster != null && facebookInfo != null) {
			if (	facebookMaster.appid == null || "".equals(facebookMaster.appid) ||
					facebookMaster.appsecret == null || "".equals(facebookMaster.appsecret) ||
					facebookInfo.accesstoken == null || "".equals(facebookInfo.accesstoken) )
			{
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Facebook is not properly set. Configuration is needed on admin window.<br />・[Facebook info] Access token<br />・[Facebook master] Application id<br />・[Facebook master] Application secret<br />・[Facebook posted target info] page ID"), false));
				ActionMessagesUtil.addErrors(session, errors);
				return "redirect:/page/?menutaskid="+facebookForm.menutaskid+"&menuid=" + facebookForm.menuid;
			}
		} else {	// Facebook発信先情報自体が取得できない場合
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Facebook is not properly set. Configuration is needed on admin window.<br />・[Facebook info] Access token<br />・[Facebook master] Application id<br />・[Facebook master] Application secret<br />・[Facebook posted target info] page ID"), false));
			ActionMessagesUtil.addErrors(session, errors);
			return "redirect:/page/?menutaskid="+facebookForm.menutaskid+"&menuid=" + facebookForm.menuid;
		}

		//タイトル削除
//		// Facebookタイトル(必須チェック)
//		if ("".equals(facebookForm.facebookTitle)) {
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "facebook向けテキストのタイトル"));
//		}

		// Facebookタイトル(文字数チェック)
		// なし


		// Facebook投稿先チェック
		if (facebookForm.facebookHomeid == null && facebookForm.facebookaccountinfo.size() <1 && facebookForm.facebookgroupinfo.size() <1) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Post destination of text for Facebook")), false));
		}

		// Facebook本文(必須チェック)
		if ("".equals(facebookForm.facebookContent)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text body of text for Facebook")), false));
		}
//		タイトル削除
//		// Facebookタイトルと本文(文字数チェック)
//		if (facebookForm.facebookTitle.length() + facebookForm.facebookContent.length() > 5000) {
//		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.maxlength", "facebook向けテキストのタイトルと本文", "5000"));
//	}
		// 本文(文字数チェック)
		if (facebookForm.facebookContent.length() > 5000) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Text body of text for Facebook"), "5000"), false));
		}

		// Eメールタイトル(必須チェック)
		if ("".equals(facebookForm.emailTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title of confirmation e-mail")), false));
		}

		// Eメールタイトル(文字数チェック)
		if (facebookForm.emailTitle.length() > 50) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title of confirmation e-mail"), "50"), false));
		}

		// Eメール配信先(必須チェック)
		if (( "".equals(facebookForm.additionalReceiver)) && (facebookForm.noticegroupinfoid == null || facebookForm.noticegroupinfoid.isEmpty())) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("E-mail delivery destination")), false));
		}

		// 追加送付先(メールアドレス文法チェック)
		String mails[] = facebookForm.additionalReceiver.split(",");
		for (String mail : mails) {
			if (!checkEmail(mail.trim())) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} is invalid e-mail address.", lang.__("Add delivery target")), false));
				break;
			}
		}

		// エラーが見つかった場合、戻す
		if (errors.size() > 0) {
			ActionMessagesUtil.addErrors(session, errors);
			return "redirect:/page/?menutaskid="+facebookForm.menutaskid+"&menuid=" + facebookForm.menuid;
		}

		// facebook投稿
		boolean facebookFlg = true;
		int facebookErrorCode = 0;
		try {
			//タイトル削除
			//facebookInfoService.postMessage(facebookForm.facebookHomeid, facebookForm.facebookaccountinfo, facebookForm.facebookgroupinfo, loginDataDto.getLocalgovinfoid(), facebookForm.facebookTitle, facebookForm.facebookContent);

			//ホームへ投稿
			if(facebookForm.facebookHomeid != null){
				facebookInfoService.postMessage( loginDataDto.getLocalgovinfoid(), facebookForm.facebookContent);
			}
			//Facebookページへ投稿
			if(facebookForm.facebookaccountinfo.size() > 0){
				facebookInfoService.postMessageAccount(facebookForm.facebookaccountinfo, loginDataDto.getLocalgovinfoid(), facebookForm.facebookContent);
			}
			//グループへ投稿
			if(facebookForm.facebookgroupinfo.size() > 0){
				facebookInfoService.postMessageGroup(facebookForm.facebookgroupinfo, loginDataDto.getLocalgovinfoid(), facebookForm.facebookContent);
			}
		} catch (FacebookException e) {
			logger.error(loginDataDto.logInfo(), e);
			facebookErrorCode = e.getErrorCode();
			facebookFlg = false;
		}

		boolean sendFlg = true;
		if (facebookFlg) {
			// 確認用Eメール送信と履歴登録
			sendFlg = sendEmail(facebookForm.emailTitle, facebookForm.facebookContent, facebookForm.noticegroupinfoid, facebookForm.additionalReceiver, NoticeType.FACEBOOK);
		}

		// 送信完了メッセージ
		if (facebookFlg && sendFlg){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Auto delivered to SNS."), false));
		} else {
			if(facebookErrorCode == getFacebookDuplicateErrorCode()){
				// 重複投稿エラーメッセージ
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send. Confirm post is not duplicated with the same title and body text."), false));	//sendDuplicateFacebookはまだない。
			}else{
				// その他エラーメッセージ
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send."), false));
			}
		}
		ActionMessagesUtil.addErrors(session, errors);
		return "redirect:/page/?menutaskid="+facebookForm.menutaskid+"&menuid=" + facebookForm.menuid;
	}

	/**
	 * Facebook重複投稿エラーコードを取得
	 * @return 重複投稿エラーコード
	 */
	private int getFacebookDuplicateErrorCode() {
		final String DEFAULT_VALUE = "506";

		InputStream is = null;
		Properties prop = null;
		try {
			is = getClass().getResourceAsStream("/SaigaiTask.properties");
			prop = new Properties();
			prop.load(is);

		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(loginDataDto.logInfo(), e);
				}
			}
		}

		// デフォルト 506
		try {
			return Integer.parseInt(prop.getProperty("FACEBOOK_DUPLICATE_ERROR_CODE", DEFAULT_VALUE));

		} catch(NumberFormatException e) {
			logger.error(lang.__("SaigaiTask.properties : FACEBOOK_DUPLICATE_ERROR_COD value is invalid."));
			return Integer.parseInt(DEFAULT_VALUE);
		}
	}
}
