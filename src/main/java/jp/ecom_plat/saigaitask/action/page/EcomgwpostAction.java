/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostInfo;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostdefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.form.page.EcomgwpostForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.form.page.PcommonsmediaForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.EcomgwpostInfoService;
import jp.ecom_plat.saigaitask.service.db.EcomgwpostdefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.util.SpringContext;


/**
 * Eコミによる周知を表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class EcomgwpostAction extends PageBaseAction {

	protected EcomgwpostForm ecomgwpostForm;

	protected PcommonsmediaForm pcommonsmediaForm;

	@Resource
	protected EcomgwpostInfoService ecomgwpostInfoService;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected EcomgwpostdefaultInfoService ecomgwpostdefaultInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected FilteredFeatureService filteredFeatureService;
	/** 通知グループ情報リスト */
	public List<NoticegroupInfo> noticegroupInfoItems;
	/** eコミGW投稿先情報リスト */
	public List<EcomgwpostInfo> ecomgwpostInfoItems;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("ecomgwpostInfoItems", ecomgwpostInfoItems);
		model.put("ecomgwpostForm", ecomgwpostForm);
	}

	/**
	 * ecomグループウェア投稿ページを表示する.
	 * @return フォワード先
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/ecomgwpost","/page/ecomgwpost/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute EcomgwpostForm ecomgwpostForm, BindingResult bindingResult) throws ParseException {
		this.ecomgwpostForm = ecomgwpostForm;
		initPage("ecomgwpost", ecomgwpostForm);

		setAdditionalReceiver();

		//インポートデータ
		List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		MenutableInfo mtbl = null;
		if (menutableList.size() > 0) {//TODO:当面は一つだけ
			mtbl = menutableList.get(0);
		}

		if (StringUtil.isBlank(ecomgwpostForm.ecomgwpostContent)) {
			StringBuffer content = null;
			if (0<pageDto.getMenuInfo().filterInfoList.size())
				content = filteredFeatureService.getFilteredFeatureContext(loginDataDto.getTrackdataid(), pageDto.getMenuInfo(), mtbl);
			else
				content = filteredFeatureService.getAllFeatureContext(loginDataDto.getTrackdataid(), mtbl);

			// 改行コードは<br />に変換。
			String contentText = content.toString().replace("\r\n", "\n").replace("\n", "<br />\r\n");
			ecomgwpostForm.ecomgwpostContent = contentText;
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


		// メール送信先一覧取得
		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

		// ecomgw投稿先一覧取得
		ecomgwpostInfoItems = ecomgwpostInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

		//デフォルト設定
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist) {
			ecomgwpostForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());
		}

		//デフォルト設定(投稿先)
		List<EcomgwpostdefaultInfo> ecomgwpostdefaultlist = ecomgwpostdefaultInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id, loginDataDto.getLocalgovinfoid());
		for (EcomgwpostdefaultInfo einfo : ecomgwpostdefaultlist) {
			ecomgwpostForm.ecomgwpostinfoid.add(einfo.ecomgwpostinfoid.toString());
		}

		// 緊急度は5がデフォルト。
		ecomgwpostForm.ecomgwpostUrgent  = "5";

		//トークンを設定
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);
		return "/page/ecomgwpost/index";
	}

	/**
	 * 送信処理
	 * @return 次のページ
	 * @throws Exception
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/ecomgwpost/send")
	public String send(Map<String,Object>model, @Valid @ModelAttribute EcomgwpostForm ecomgwpostForm, BindingResult bindingResult) {
		this.ecomgwpostForm = ecomgwpostForm;
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
				if(pageDto.getTrackData().trainingplandataid > 0 && !pageDto.getTrackData().trainingplanData.ecommapgwflag){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Notification function out of service due to training"), false));
				}
			}
		}

		// 管理画面で設定されていない場合、送信不可 2014/7/11
		for (String id : ecomgwpostForm.ecomgwpostinfoid) {
			EcomgwpostInfo postInfo = ecomgwpostInfoService.findById(new Long(id));
			if (postInfo != null) {
				if (	((postInfo.blockid == null || "".equals(postInfo.blockid))&&
						(postInfo.groupid == null || "".equals(postInfo.groupid)) &&
						(postInfo.partsid == null || "".equals(postInfo.partsid))) ||
						(postInfo.posturl == null || "".equals(postInfo.posturl))
				) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for e-commap groupware is not properly set. Configuration is needed on admin window.<br />・[e-com GW posting info] Posting info group ID<br />・[e-com GW posting info] Parts common ID<br />・[e-com GW posting info] Parts individual ID<br />・[e-com GW posting info] Destination URL"), false));
					ActionMessagesUtil.addErrors(bindingResult, errors);
					setupModel(model);
					return "redirect:/page/?menutaskid="+ecomgwpostForm.menutaskid+"&menuid=" + ecomgwpostForm.menuid;
				}
			} else {	// eコミグループウェア発信先情報自体が取得できない場合
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for e-commap groupware is not properly set. Configuration is needed on admin window.<br />・[e-com GW posting info] Posting info group ID<br />・[e-com GW posting info] Parts common ID<br />・[e-com GW posting info] Parts individual ID<br />・[e-com GW posting info] Destination URL"), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
				setupModel(model);
				return "redirect:/page/?menutaskid="+ecomgwpostForm.menutaskid+"&menuid=" + ecomgwpostForm.menuid;
			}
		}

		// バリデーション
		errors = validateSend();

		// エラーが見つかった場合、戻す
		if (errors.size() > 0) {
			ActionMessagesUtil.addErrors(bindingResult, errors);
			setupModel(model);
			return "redirect:/page/?menutaskid="+ecomgwpostForm.menutaskid+"&menuid=" + ecomgwpostForm.menuid;
		}

		// eコミグループウェア投稿
		boolean result = ecomgwpostInfoService.send(
				ecomgwpostForm.ecomgwpostinfoid	// 送信先のID
				, ecomgwpostForm.ecomgwpostUrgent	// 緊急度
				, ecomgwpostForm.ecomgwpostTitle		// タイトル
				, ecomgwpostForm.ecomgwpostContent	// 本文(html)
				);

		if (result) {
			// --------------------------- 全件送信成功
			// メール送信内容作成
			String mailContent = ecomgwpostForm.ecomgwpostContent;

			// brタグを改行コードへ変換
			// 2014-03-20不要と判断
//			mailContent = mailContent
//							.replace("<br>", "\r\n")
//							.replace("<br />", "\r\n")
//							.replace("<BR>", "\r\n")
//							.replace("<BR />", "\r\n");

			// htmlタグを除去(全角タグ文字はＯＫのためascii文字で構成されたタグのみ除去)
			mailContent = mailContent.replaceAll("<[\\u0020-\\u007E]+?>", "");

			// &nbsp;などの特殊文字は置き換え
			mailContent = StringEscapeUtils.unescapeHtml(mailContent);

			// ＤＢ保存内容
			String dataContent = ecomgwpostForm.ecomgwpostContent;
			dataContent =  StringEscapeUtils.unescapeHtml(dataContent);

			// 確認用Eメール送信と履歴登録(メール本文と通知履歴は別々の値で指定する)
			boolean mailSended = sendEmail(
						ecomgwpostForm.emailTitle
						, mailContent
						, dataContent
						, ecomgwpostForm.noticegroupinfoid
						, ecomgwpostForm.additionalReceiver
						, NoticeType.ECOMGW
					);

			if (mailSended) {
				// 送信完了メッセージ
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Auto delivered to ecom groupware."), false));
			} else {
				// メール送信失敗メッセージ
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send e-com groupware.", lang.__("E-mail")), false));
			}

		} else {
			// --------------------------- 送信失敗（一件でも失敗したら、継続せずに即終了している）
			// Ecomグループウェア投稿失敗メッセージ
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send."), false));

		}


		ActionMessagesUtil.addErrors(bindingResult, errors);
		setupModel(model);
		return "redirect:/page/?menutaskid="+ecomgwpostForm.menutaskid+"&menuid=" + ecomgwpostForm.menuid;
	}

	/**
	 * バリデーション
	 * @return ActionErrors
	 * @throws Exception
	 */
	private ActionMessages validateSend() {

		ActionMessages errors = new ActionMessages();

		// 緊急度(必須チェック)
		if ("".equals(ecomgwpostForm.ecomgwpostUrgent)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Degree of emergency")), false));
		}

		// 緊急度(不正値チェック)
		if (!"".equals(ecomgwpostForm.ecomgwpostUrgent)
				&& !"1".equals(ecomgwpostForm.ecomgwpostUrgent)
				&& !"2".equals(ecomgwpostForm.ecomgwpostUrgent)
				&& !"3".equals(ecomgwpostForm.ecomgwpostUrgent)
				&& !"4".equals(ecomgwpostForm.ecomgwpostUrgent)
				&& !"5".equals(ecomgwpostForm.ecomgwpostUrgent)
				) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Invalid {0}", lang.__("Degree of emergency")), false));
		}

		// Eグループウェアタイトル(必須チェック)
		if ("".equals(ecomgwpostForm.ecomgwpostTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text title for e-Com GW")), false));
		}

//		// Eグループウェアタイトル(文字数チェック)
//		if (ecomgwpostForm.ecomgwpostTitle.length() > 140) {
//			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.maxlength", "Eコミグループウェア向けテキストのタイトル", "140"));
//		}

		// Eグループウェア本文(必須チェック)
		if ("".equals(ecomgwpostForm.ecomgwpostContent)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Body text for e-Com GW")), false));
		}

		// 送信先(必須チェック)
		if (ecomgwpostForm.ecomgwpostinfoid == null || ecomgwpostForm.ecomgwpostinfoid.isEmpty()) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Post destination")), false));
		}

		// 送信先(数値チェック)
		if (ecomgwpostForm.ecomgwpostinfoid != null && !ecomgwpostForm.ecomgwpostinfoid.isEmpty()) {
			for (String id : ecomgwpostForm.ecomgwpostinfoid) {
				if (StringUtils.isEmpty(id) || !NumberUtils.isDigits(id)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be integer type.", lang.__("Post destination")), false));
				}
			}
		}

		// Eメールタイトル(必須チェック)
		if ("".equals(ecomgwpostForm.emailTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title of confirmation e-mail")), false));
		}

		// Eメールタイトル(文字数チェック)
		if (ecomgwpostForm.emailTitle.length() > 50) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title of confirmation e-mail"), "50"), false));
		}

		// Eメール配信先(必須チェック)
		if (( "".equals(ecomgwpostForm.additionalReceiver)) && (ecomgwpostForm.noticegroupinfoid == null || ecomgwpostForm.noticegroupinfoid.isEmpty())) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("E-mail delivery destination")), false));
		}

		// 追加送付先(メールアドレス文法チェック)
		String mails[] = ecomgwpostForm.additionalReceiver.split(",");
		for (String mail : mails) {
			if (!checkEmail(mail.trim())) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} is invalid e-mail address.", lang.__("Add delivery target")), false));
				break;
			}
		}

		return errors;
	}



}
