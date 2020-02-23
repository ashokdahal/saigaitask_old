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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.TwitterInfo;
import jp.ecom_plat.saigaitask.entity.db.TwitterMaster;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.form.page.PcommonsmediaForm;
import jp.ecom_plat.saigaitask.form.page.TwitterForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.TwitterInfoService;
import jp.ecom_plat.saigaitask.service.db.TwitterMasterService;
import jp.ecom_plat.saigaitask.util.SpringContext;
import twitter4j.TwitterException;


/**
 * twitterによる周知を表示するアクションクラスです.
 * spring checked take 5/3
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class TwitterAction extends PageBaseAction {

	protected TwitterForm twitterForm;

	protected PcommonsmediaForm pcommonsmediaForm;

	@Resource
	protected TwitterInfoService twitterInfoService;
	@Resource
	protected TwitterMasterService twitterMasterService;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected FilteredFeatureService filteredFeatureService;

	/** 通知グループ情報リスト */
	public List<NoticegroupInfo> noticegroupInfoItems;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("noticeType", NoticeType.TWITTER);
		model.put("twitterForm", twitterForm);
		model.put("pcommonsmediaForm", pcommonsmediaForm);
	}

	/**
	 * twitterページを表示する.
	 * @return フォワード先
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/twitter","/page/twitter/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute TwitterForm twitterForm) throws ParseException {
		this.twitterForm = twitterForm;
		initPage("twitter", twitterForm);
		ActionMessages errors = new ActionMessages();

		setAdditionalReceiver();

		//インポートデータ
		List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		MenutableInfo mtbl = null;
		if (menutableList.size() > 0) {//TODO:当面は一つだけ
			mtbl = menutableList.get(0);
		}

		if (StringUtil.isBlank(twitterForm.twitterContent)) {
			StringBuffer content = null;
			if (0<pageDto.getMenuInfo().filterInfoList.size())
				content = filteredFeatureService.getFilteredFeatureContext(loginDataDto.getTrackdataid(), pageDto.getMenuInfo(), mtbl);
			else
				content = filteredFeatureService.getAllFeatureContext(loginDataDto.getTrackdataid(), mtbl);
			twitterForm.twitterContent = content.toString();
		}

		// 管理画面で設定されていない場合、送信不可 2014/7/11
		TwitterMaster twitterMaster = twitterMasterService.find();
		TwitterInfo twitterInfo = twitterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if (twitterMaster != null && twitterInfo != null) {
			if (	twitterMaster.consumerkey == null || "".equals(twitterMaster.consumerkey) ||
					twitterMaster.consumersecret == null || "".equals(twitterMaster.consumersecret) ||
					twitterInfo.accesstoken == null || "".equals(twitterInfo.accesstoken) ||
				twitterInfo.accesstokensecret == null || "".equals(twitterInfo.accesstokensecret) )
			{
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Twitter is not properly set. Configuration is needed on admin window.<br />・[Twitter info] Access token<br />・[Twitter info] Access token secret<br />・[Twitter master] Consumer key<br />・[Twitter master] Consumer secret"), false));
				ActionMessagesUtil.addErrors(session, errors);
			}
		} else {	// Twitter発信先情報自体が取得できない場合
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Twitter is not properly set. Configuration is needed on admin window.<br />・[Twitter info] Access token<br />・[Twitter info] Access token secret<br />・[Twitter master] Consumer key<br />・[Twitter master] Consumer secret"), false));
			ActionMessagesUtil.addErrors(session, errors);
		}

		/*if (!StringUtil.isBlank((String)session.getAttribute("honbun"))) {
			twitterForm.twitterContent = (String)session.getAttribute("honbun");
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

			if (!StringUtil.isBlank(hinanshiji.toString())) twitterForm.twitterContent = twitterForm.twitterContent + "\n\n【避難指示】" + hinanshiji.toString().substring(1);
			if (!StringUtil.isBlank(hinankankoku.toString())) twitterForm.twitterContent = twitterForm.twitterContent + "\n\n【避難勧告】" + hinankankoku.toString().substring(1);
			session.removeAttribute("REFUGE");
		}*/

		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

		//デフォルト設定
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist)
			twitterForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());

		//トークンを設定
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);

		return "page/twitter/index";
	}

	/**
	 * 送信処理
	 * @return 次のページ
	 * @throws TwitterException
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/twitter/send")
	public String send(Map<String,Object>model,
			@Valid @ModelAttribute TwitterForm twitterForm, BindingResult bindingResult) {
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
				if(pageDto.getTrackData().trainingplandataid > 0 && !pageDto.getTrackData().trainingplanData.twitterflag){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Notification function out of service due to training"), false));
				}
			}
		}

		// 管理画面で設定されていない場合、送信不可 2014/7/11
		TwitterMaster twitterMaster = twitterMasterService.find();
		TwitterInfo twitterInfo = twitterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if (twitterMaster != null && twitterInfo != null) {
			if (	twitterMaster.consumerkey == null || "".equals(twitterMaster.consumerkey) ||
					twitterMaster.consumersecret == null || "".equals(twitterMaster.consumersecret) ||
					twitterInfo.accesstoken == null || "".equals(twitterInfo.accesstoken) ||
				twitterInfo.accesstokensecret == null || "".equals(twitterInfo.accesstokensecret) )
			{
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Twitter is not properly set. Configuration is needed on admin window.<br />・[Twitter info] Access token<br />・[Twitter info] Access token secret<br />・[Twitter master] Consumer key<br />・[Twitter master] Consumer secret"), false));
				ActionMessagesUtil.addErrors(session, errors);
				return "redirect:/page/?menutaskid="+twitterForm.menutaskid+"&menuid=" + twitterForm.menuid;
			}
		} else {	// Twitter発信先情報自体が取得できない場合
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for Twitter is not properly set. Configuration is needed on admin window.<br />・[Twitter info] Access token<br />・[Twitter info] Access token secret<br />・[Twitter master] Consumer key<br />・[Twitter master] Consumer secret"), false));
			ActionMessagesUtil.addErrors(session, errors);
			return "redirect:/page/?menutaskid="+twitterForm.menutaskid+"&menuid=" + twitterForm.menuid;
		}

//		タイトル削除
//		 Twitterタイトル(必須チェック)
//		if ("".equals(twitterForm.twitterTitle)) {
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "twitter向けテキストのタイトル"));
//		}

		// Twitterタイトル(文字数チェック)
		// なし

		// Twitter本文(必須チェック)
		if ("".equals(twitterForm.twitterContent)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text body of text for Twitter")), false));
		}
//		タイトル削除
//		// Twitterタイトルと本文(文字数チェック)
//		if ( (twitterForm.twitterTitle.length() + twitterForm.twitterContent.length()) > 139 ) {
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.maxlength", "twitter向けテキストのタイトルと本文", "140"));
//		}

		// Twitter本文(文字数チェック)
		if ( (twitterForm.twitterContent.length()) > 140 ) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Text body of text for Twitter"), "140"), false));
		}

		// Eメールタイトル(必須チェック)
		if ("".equals(twitterForm.emailTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title of confirmation e-mail")), false));
		}

		// Eメールタイトル(文字数チェック)
		if (twitterForm.emailTitle.length() > 50) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title of confirmation e-mail"), "50"), false));
		}

		// Eメール配信先(必須チェック)
		if (( "".equals(twitterForm.additionalReceiver)) && (twitterForm.noticegroupinfoid == null || twitterForm.noticegroupinfoid.isEmpty())) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("E-mail delivery destination")), false));
		}

		// 追加送付先(メールアドレス文法チェック)
		String mails[] = twitterForm.additionalReceiver.split(",");
		for (String mail : mails) {
			if (!checkEmail(mail.trim())) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} is invalid e-mail address.", lang.__("Add delivery target")), false));
				break;
			}
		}

		// エラーが見つかった場合、戻す
		if (errors.size() > 0) {
			ActionMessagesUtil.addErrors(session, errors);
			return "redirect:/page/?menutaskid="+twitterForm.menutaskid+"&menuid=" + twitterForm.menuid;
		}

		// twitter投稿
		boolean twitterFlg = true;
		int twitterErrorCode = 0;
		try {
			//タイトル削除
//			twitterInfoService.postMessage(loginDataDto.getLocalgovinfoid(), twitterForm.twitterTitle, twitterForm.twitterContent);
			twitterInfoService.postMessage(loginDataDto.getLocalgovinfoid(), twitterForm.twitterContent);
		} catch (TwitterException e) {
			logger.error("", e);
			twitterErrorCode = e.getErrorCode();
			twitterFlg = false;
		}

		boolean sendFlg = true;
		if (twitterFlg) {
			// 確認用Eメール送信と履歴登録
			sendFlg = sendEmail(twitterForm.emailTitle, twitterForm.twitterContent, twitterForm.noticegroupinfoid, twitterForm.additionalReceiver, NoticeType.TWITTER);
		}

		if (twitterFlg && sendFlg){
			// 送信完了メッセージ
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Auto delivered to SNS."), false));
		} else {
			if(twitterErrorCode == getTwitterDuplicateErrorCode()){
				// 重複投稿エラーメッセージ
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send. Confirm post is not duplicated with the same title and body text."), false));
			}else{
				// その他エラーメッセージ
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send."), false));
			}
		}
		ActionMessagesUtil.addErrors(session, errors);
		return "redirect:/page/?menutaskid="+twitterForm.menutaskid+"&menuid=" + twitterForm.menuid;
	}

	/**
	 * Twitter重複投稿エラーコードを取得
	 * @return 重複投稿エラーコード
	 */
	private int getTwitterDuplicateErrorCode() {
		final String DEFAULT_VALUE = "187";

		InputStream is = null;
		Properties prop = null;
		try {
			is = getClass().getResourceAsStream("/SaigaiTask.properties");
			prop = new Properties();
			prop.load(is);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}

		// デフォルト 187
		try {
			return Integer.parseInt(prop.getProperty("TWITTER_DUPLICATE_ERROR_CODE", DEFAULT_VALUE));

		} catch(NumberFormatException e) {
			logger.error(lang.__("SaigaiTask.properties : TWITTER_DUPLICATE_ERROR_CODE value is invalid."));
			return Integer.parseInt(DEFAULT_VALUE);
		}
	}
}
