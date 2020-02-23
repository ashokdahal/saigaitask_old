/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import facebook4j.FacebookException;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.constant.PubliccommonsSendStatusValue;
import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendToInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.form.page.PcommonsmailForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsSendToInfoService;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 公共情報コモンズ（緊急速報）ページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class PcommonsmailAction extends PageBaseAction {

	protected PcommonsmailForm pcommonsmailForm;

	/** 通知グループ情報リスト */
	public List<NoticegroupInfo> noticegroupInfoItems;
	/** コモンズ送信情報 */
	@Resource
	protected PCommonsSendDto pCommonsSendDto;
	/** 通知デフォルトグループ情報サービスクラス */
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	/** 通知テンプレートサービスクラス */
	@Resource
	protected NoticeTemplateService noticeTemplateService;

	@Resource
	protected GroupInfoService groupInfoService;

	/** メニューテーブル情報サービスクラス */
	@Resource
	protected MenutableInfoService menutableInfoService;

	/** フィルタサービスクラス */
	@Resource
	protected FilteredFeatureService filteredFeatureService;

	@Resource
	protected PubliccommonsSendToInfoService publiccommonsSendToInfoService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("pcommonsmailForm", pcommonsmailForm);
	}
	/**
	 * 公共情報コモンズ（緊急速報）ページを表示する.
	 * @return index.jsp
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/pcommonsmail","/page/pcommonsmail/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute PcommonsmailForm pcommonsmailForm, BindingResult bindingResult) throws ParseException {
		this.pcommonsmailForm = pcommonsmailForm;
		initPage("pcommonsmail", pcommonsmailForm);
		setAdditionalReceiver();

		//インポートデータ
		List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		MenutableInfo mtbl = null;
		if (menutableList.size() > 0) {//TODO:当面は一つだけ
			mtbl = menutableList.get(0);
		}

		if (StringUtil.isBlank(pcommonsmailForm.pcommonsmailContent)) {
			StringBuffer content = null;
			if (0<pageDto.getMenuInfo().filterInfoList.size())
				content = filteredFeatureService.getFilteredFeatureContext(loginDataDto.getTrackdataid(), pageDto.getMenuInfo(), mtbl);
			else
				content = filteredFeatureService.getAllFeatureContext(loginDataDto.getTrackdataid(), mtbl);
			pcommonsmailForm.pcommonsmailContent = content.toString();
		}

		//画面表示初期値を取得
		ActionMessages errors = setInformationList();

		if (errors.size() > 0) {
			//ActionMessagesUtil.addErrors(bindingResult, errors);
			ActionMessagesUtil.addErrors(session, errors);
		}

		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

		//デフォルト設定
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist)
			pcommonsmailForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());

		//トークンを設定
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);
		return "page/pcommonsmail/index";
	}

	/**
	 * 送信処理
	 * @return 次のページ
	 * @throws FacebookException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/pcommonsmail/send"})
	public String send(Map<String,Object>model,
			@Valid @ModelAttribute PcommonsmailForm pcommonsmailForm, BindingResult bindingResult) throws ParseException{
		this.pcommonsmailForm = pcommonsmailForm;
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
				if(pageDto.getTrackData().trainingplandataid > 0 && !pageDto.getTrackData().trainingplanData.publiccommonsflag){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Notification function out of service due to training"),false));
				}
			}
		}

		// 災害なしの場合、送信不可 2014/7/11
		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
		if (trackData == null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send because of not yet disaster registration."), false));
			//ActionMessagesUtil.addErrors(bindingResult, errors);
			ActionMessagesUtil.addErrors(session, errors);
			setupModel(model);
			return "/page/?menutaskid="+pcommonsmailForm.menutaskid+"&menuid=" + pcommonsmailForm.menuid;
		}

		// 管理画面で設定されていない場合、送信不可 2014/7/11
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(loginDataDto.getLocalgovinfoid());
		if (publiccommonsSendToInfo != null) {
			if (	publiccommonsSendToInfo.endpointUrl == null || "".equals(publiccommonsSendToInfo.endpointUrl) ||
					publiccommonsSendToInfo.username == null || "".equals(publiccommonsSendToInfo.username) ||
					publiccommonsSendToInfo.password == null || "".equals(publiccommonsSendToInfo.password) ||
					(	!PubliccommonsSendStatusValue.ACTUAL.equals(publiccommonsSendToInfo.statusValues) &&
						!PubliccommonsSendStatusValue.EXERCISE.equals(publiccommonsSendToInfo.statusValues) &&
						!PubliccommonsSendStatusValue.TEST.equals(publiccommonsSendToInfo.statusValues)
					)
				)
			{
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for L-Alert is not properly set. Configuration is needed on admin window.<br />・[L-Alert receiver data] End point URL<br />・[L-Alert receiver data] User name<br />・[L-Alert receiver data] Password<br />・[L-Alert receiver data] Operation type"), false));
				//ActionMessagesUtil.addErrors(bindingResult, errors);
				ActionMessagesUtil.addErrors(session, errors);
				setupModel(model);
				return "/page/?menutaskid="+pcommonsmailForm.menutaskid+"&menuid=" + pcommonsmailForm.menuid;
			}else{
				// 訓練モード中の場合、本番から訓練に自動的に変更する
				if(0<loginDataDto.getTrackdataid()) {
					if(pageDto.getTrackData().trainingplandataid != null){
						if(pageDto.getTrackData().trainingplandataid > 0){
							// 本番になっている時のみ変更。テストの時は何もしない
							if(PubliccommonsSendStatusValue.ACTUAL.equals(publiccommonsSendToInfo.statusValues))
								publiccommonsSendToInfo.statusValues = PubliccommonsSendStatusValue.EXERCISE;
						}
					}
				}
			}
		} else {	// コモンズ発信先情報自体が取得できない場合
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for L-Alert is not properly set. Configuration is needed on admin window.<br />・[L-Alert receiver data] End point URL<br />・[L-Alert receiver data] User name<br />・[L-Alert receiver data] Password<br />・[L-Alert receiver data] Operation type"), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
			setupModel(model);
			return "/page/?menutaskid="+pcommonsmailForm.menutaskid+"&menuid=" + pcommonsmailForm.menuid;
		}

		// 緊急メールタイトル(必須チェック)
		if (StringUtil.isBlank(pcommonsmailForm.pcommonsmailTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title<!--2-->")), false));
		} else {
			List<String> errorMessageParam = checkAreaMail(pcommonsmailForm.pcommonsmailTitle);
			if (errorMessageParam.size() > 0) {
				for (String param : errorMessageParam)
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{1} can not be set to be {0}.", lang.__("Title<!--2-->"), param), false));
			}
		}

		// 緊急メールタイトル(文字数チェック)
		if (pcommonsmailForm.pcommonsmailTitle.length() > 15) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title<!--2-->"), "15"), false));  // 一律15
		}

		// 緊急メール本文(必須チェック)
		if (StringUtil.isBlank(pcommonsmailForm.pcommonsmailContent)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text body for emergency e-mail")), false));
		} else {
			List<String> errorMessageParam = checkAreaMail(pcommonsmailForm.pcommonsmailContent);
			if (errorMessageParam.size() > 0) {
				for (String param : errorMessageParam)
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{1} can not be set to be {0}.", lang.__("Text"), param), false));
			}
		}

		// 緊急メールタイトルと本文(文字数チェック)
		// Windows改行コード対策(正規表現だと文字数カウントが手間なので置換で対処)
		String cTitle = pcommonsmailForm.pcommonsmailTitle;
		cTitle = cTitle.replaceAll("\r\n", "\r");
		String cContent = pcommonsmailForm.pcommonsmailContent;
		cContent = cContent.replaceAll("\r\n", "\r");
		if (cTitle.length() + cContent.length() > 200) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title and text"), "200"), false));  // docomo:500, au:200, softbank:200
		}

		// 発表組織(必須チェック)
		if ("".equals(pcommonsmailForm.organizationName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Organization name<!--2-->")), false));
		}
		if ("".equals(pcommonsmailForm.organizationCode)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("City code<!--2-->")), false));
		}
		if ("".equals(pcommonsmailForm.organizationDomainName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Organization domain<!--2-->")), false));
		}
		if ("".equals(pcommonsmailForm.officeName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department name")), false));
		}
		if ("".equals(pcommonsmailForm.officeNameKana)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department name by kana")), false));
		}
		if ("".equals(pcommonsmailForm.officeLocationArea)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department address")), false));
		}
		if ("".equals(pcommonsmailForm.phone)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department phone number")), false));
		}
		if ("".equals(pcommonsmailForm.fax)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department FAX number")), false));
		}
		if ("".equals(pcommonsmailForm.email)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department  e-mail address")), false));
		}
		if ("".equals(pcommonsmailForm.officeDomainName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department domain")), false));
		}

		// 作成組織(必須チェック)
		if ("".equals(pcommonsmailForm.organizationNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization organization name")), false));
		}
		if ("".equals(pcommonsmailForm.organizationCodeEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization local gov. code")), false));
		}
		if ("".equals(pcommonsmailForm.organizationDomainNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization organization domain")), false));
		}
		if ("".equals(pcommonsmailForm.officeNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department name")), false));
		}
		if ("".equals(pcommonsmailForm.officeNameKanaEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department name by kana")), false));
		}
		if ("".equals(pcommonsmailForm.officeLocationAreaEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department address")), false));
		}
		if ("".equals(pcommonsmailForm.phoneEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department phone number")), false));
		}
		if ("".equals(pcommonsmailForm.faxEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department FAX number")), false));
		}
		if ("".equals(pcommonsmailForm.emailEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department e-mail address")), false));
		}
		if ("".equals(pcommonsmailForm.officeDomainNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department domain")), false));
		}

		// 公式発表日時チェック
		if("".equals(pcommonsmailForm.reporttime)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Official announcement date and time")), false));
		}

		// Eメールタイトル(必須チェック)
		if ("".equals(pcommonsmailForm.emailTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title of confirmation e-mail")), false));
		}

		// Eメールタイトル(文字数チェック)
		if (pcommonsmailForm.emailTitle.length() > 50) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title of confirmation e-mail"), "50"), false));
		}

		// Eメール配信先(必須チェック)
		if (( "".equals(pcommonsmailForm.additionalReceiver)) && (pcommonsmailForm.noticegroupinfoid == null || pcommonsmailForm.noticegroupinfoid.isEmpty())) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("E-mail delivery destination")), false));
		}

		// 追加送付先(メールアドレス文法チェック)
		String mails[] = pcommonsmailForm.additionalReceiver.split(",");
		for (String mail : mails) {
			if (!checkEmail(mail.trim())) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} is invalid e-mail address.", lang.__("Add delivery target")), false));
				break;
			}
		}

		//重複投稿エラーチェック
		List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMAIL, PubliccommonsSendType.URGENT_MAIL);
		if (noticemailDataItems.size() > 0) {
			String content = noticemailDataItems.get(0).content;
			String sendMessage = pcommonsmailForm.pcommonsmailContent;
			if (sendMessage.equals(content)){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send. Confirm post is not duplicated with the same title and body text."), false));
			}
		}

		// エラーが見つかった場合、戻す
		if (errors.size() > 0) {
			ActionMessagesUtil.addErrors(bindingResult, errors);
			setupModel(model);
			return "/page/?menutaskid="+pcommonsmailForm.menutaskid+"&menuid=" + pcommonsmailForm.menuid + "&<script language='javascript'>alert('xss');</script>";
		}

		// 緊急速報メールを送信
		boolean pcommonsmailFlg = true;
		try {
			pcommonsmailFlg = report();

		} catch (FileNotFoundException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} catch (JAXBException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
		boolean sendFlg = true;
		if (pcommonsmailFlg) {
			// 確認用Eメール送信と履歴登録
			sendFlg = sendEmail(pcommonsmailForm.emailTitle, pcommonsmailForm.pcommonsmailContent, pcommonsmailForm.noticegroupinfoid, pcommonsmailForm.additionalReceiver, NoticeType.COMMONSMAIL);
		}

		// 送信完了メッセージ
		if (pcommonsmailFlg && sendFlg){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Emergency e-mail, area mail auto delivered."), false));
		} else {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send."), false));
		}

		// 再送有無
		if (!pcommonsmailFlg) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Resend on browser window of mobile phone carrier."), false));
		}

		ActionMessagesUtil.addErrors(bindingResult, errors);
		setupModel(model);
		return "/page/?menutaskid="+pcommonsmailForm.menutaskid+"&menuid=" + pcommonsmailForm.menuid;
	}

	/**
	 * 緊急速報メールを送信
	 * @param validdatetime 希望公開終了日時(緊急速報メールは48時間固定)
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @return true: 送信OK, false: 送信失敗
	 */
	private boolean report() throws FileNotFoundException, JAXBException {
		pCommonsSendDto.localgovinfoid = loginDataDto.getLocalgovinfoid();																// 自治体ID
		pCommonsSendDto.trackdataid = loginDataDto.getTrackdataid();																	// 記録データID

		// 公式発表日時
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try{
			pCommonsSendDto.reporttime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(pcommonsmailForm.reporttime).getTime());
		}catch (Exception e){
			pCommonsSendDto.reporttime = now;
			logger.error(loginDataDto.logInfo(), e);
		}
		// 運用種別
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(loginDataDto.getLocalgovinfoid());
		// 訓練モード中の場合、本番から訓練に自動的に変更する
		if(0<loginDataDto.getTrackdataid()){
			pageDto.setTrackData(trackDataService.findById(loginDataDto.getTrackdataid()));
			if(pageDto.getTrackData()!=null && pageDto.getTrackData().trainingplandataid != null){
				if(pageDto.getTrackData().trainingplandataid > 0){
					// 本番になっている時のみ変更。テストの時は何もしない
					if(PubliccommonsSendStatusValue.ACTUAL.equals(publiccommonsSendToInfo.statusValues))
						publiccommonsSendToInfo.statusValues = PubliccommonsSendStatusValue.EXERCISE;
				}
			}
		}
		pCommonsSendDto.statusValue = publiccommonsSendToInfo.statusValues;

		pCommonsSendDto.createdatetime = now;																					// 作成日時
		pCommonsSendDto.targetdatetime = now;																					// 希望公開開始日時
		pCommonsSendDto.personResponsible = pcommonsmailForm.personResponsible;							// 発表組織 担当者名
		pCommonsSendDto.organizationName = pcommonsmailForm.organizationName;								// 発表組織 組織名
		pCommonsSendDto.organizationCode = pcommonsmailForm.organizationCode;								// 発表組織 地方公共団体コード
		pCommonsSendDto.organizationDomainName = pcommonsmailForm.organizationDomainName;		// 発表組織 組織ドメイン
		pCommonsSendDto.officeName = pcommonsmailForm.officeName;													// 発表組織 部署名
		pCommonsSendDto.officeNameKana = pcommonsmailForm.officeNameKana;									// 発表組織 部署名(カナ)
		pCommonsSendDto.officeLocationArea = pcommonsmailForm.officeLocationArea;							// 発表組織 部署住所
		pCommonsSendDto.phone = pcommonsmailForm.phone;																// 発表組織 部署電話番号
		pCommonsSendDto.fax = pcommonsmailForm.fax;																			// 発表組織 部署FAX番号
		pCommonsSendDto.email = pcommonsmailForm.email;																	// 発表組織 部署メールアドレス
		pCommonsSendDto.officeDomainName = pcommonsmailForm.officeDomainName;							// 発表組織 部署ドメイン

		pCommonsSendDto.organizationNameEditorial = pcommonsmailForm.organizationNameEditorial;								// 作成組織 組織名
		pCommonsSendDto.organizationCodeEditorial = pcommonsmailForm.organizationCodeEditorial;								// 作成組織 地方公共団体コード
		pCommonsSendDto.organizationDomainNameEditorial = pcommonsmailForm.organizationDomainNameEditorial;		// 作成組織 組織ドメイン
		pCommonsSendDto.officeNameEditorial = pcommonsmailForm.officeNameEditorial;													// 作成組織 部署名
		pCommonsSendDto.officeNameKanaEditorial = pcommonsmailForm.officeNameKanaEditorial;									// 作成組織 部署名(カナ)
		pCommonsSendDto.officeLocationAreaEditorial = pcommonsmailForm.officeLocationAreaEditorial;								// 作成組織 部署住所
		pCommonsSendDto.phoneEditorial = pcommonsmailForm.phoneEditorial;																	// 作成組織 部署電話番号
		pCommonsSendDto.faxEditorial = pcommonsmailForm.faxEditorial;																			// 作成組織 部署FAX番号
		pCommonsSendDto.emailEditorial = pcommonsmailForm.emailEditorial;																	// 作成組織 部署メールアドレス
		pCommonsSendDto.officeDomainNameEditorial = pcommonsmailForm.officeDomainNameEditorial;							// 作成組織 部署ドメイン

		pCommonsSendDto.validdatetime = getValidDateTime();
		pCommonsSendDto.title = pcommonsmailForm.pcommonsmailTitle;
		pCommonsSendDto.contentDescription = pcommonsmailForm.pcommonsmailTitle;
		return publicCommonsService.sendUrgentMail(pcommonsmailForm.pcommonsmailTitle, pcommonsmailForm.pcommonsmailContent, pCommonsSendDto);
	}

	/**
	 * 現在日時から48時間後を日時を返す
	 * @return 48時間後の日時
	 */
	public String getValidDateTime() {
		// 48時間語固定を希望公開終了日時にセット
		Date now = new Date();
		long nowtime = now.getTime();
		long targettime = nowtime + (long)(48*60*60*1000);	// 48時間語固定
		Date targetdate = new Date(targettime);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(targetdate);
	}

	/**
	 *
	 * @return エラーメッセージ
	 * @throws ParseException
	 */
	public ActionMessages setInformationList() throws ParseException {
		ActionMessages errors = new ActionMessages();

		// 都道府県フラグ
		if ("".equals(loginDataDto.getLocalgovInfo().city)) {
			pcommonsmailForm.isPrefecture = true;
		} else {
			pcommonsmailForm.isPrefecture = false;
		}

		// 本文に初期表示で自治体名を追加
		if (loginDataDto.getLocalgovInfo() != null) {
			if (StringUtil.isEmpty(pcommonsmailForm.pcommonsmailContent) && loginDataDto.getLocalgovInfo().pref != null) {
				pcommonsmailForm.pcommonsmailContent = lang.__(" {0}.", loginDataDto.getLocalgovInfo().pref);
			} else if (StringUtil.isEmpty(pcommonsmailForm.pcommonsmailContent) && loginDataDto.getLocalgovInfo().city != null) {
				pcommonsmailForm.pcommonsmailContent = lang.__(" {0}.", loginDataDto.getLocalgovInfo().city);
			}
		}

		// 運用種別
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(loginDataDto.getLocalgovinfoid());
		if (publiccommonsSendToInfo != null) {
			if (StringUtil.isEmpty(publiccommonsSendToInfo.endpointUrl) ||
				StringUtil.isEmpty(publiccommonsSendToInfo.username) ||
				StringUtil.isEmpty(publiccommonsSendToInfo.password) ||
				(!PubliccommonsSendStatusValue.ACTUAL.equals(publiccommonsSendToInfo.statusValues) &&
				 !PubliccommonsSendStatusValue.EXERCISE.equals(publiccommonsSendToInfo.statusValues) &&
				 !PubliccommonsSendStatusValue.TEST.equals(publiccommonsSendToInfo.statusValues))
				)
			{
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for L-Alert is not properly set. Configuration is needed on admin window.<br />・[L-Alert receiver data] End point URL<br />・[L-Alert receiver data] User name<br />・[L-Alert receiver data] Password<br />・[L-Alert receiver data] Operation type"), false));
			} else {
				// 訓練モード中の場合、本番から訓練に自動的に変更する
				// trackdataidが0の場合、trackDataが取得されていないので先にチェックする
				if(0<loginDataDto.getTrackdataid()) {
					if(pageDto.getTrackData().trainingplandataid != null){
						if(pageDto.getTrackData().trainingplandataid > 0){
							// 本番になっている時のみ変更。テストの時は何もしない
							if(PubliccommonsSendStatusValue.ACTUAL.equals(publiccommonsSendToInfo.statusValues))
								publiccommonsSendToInfo.statusValues = PubliccommonsSendStatusValue.EXERCISE;
						}
					}
				}
				pcommonsmailForm.statusValue = publiccommonsSendToInfo.statusValues;
			}
		} else {	// コモンズ発信先情報自体が取得できない場合
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for L-Alert is not properly set. Configuration is needed on admin window.<br />・[L-Alert receiver data] End point URL<br />・[L-Alert receiver data] User name<br />・[L-Alert receiver data] Password<br />・[L-Alert receiver data] Operation type"), false));
		}

		// チェックディジェット付き地方公共自治体コード
		String organizationCode = "";
		if(loginDataDto.getLocalgovInfo()!=null) organizationCode = PublicCommonsUtils.getLocalGovermentCode(loginDataDto.getLocalgovInfo());

		// 班情報から取得
		GroupInfo group = groupInfoService.findById(loginDataDto.getGroupid());
		if (group != null) {
			// 発表組織
			if (StringUtil.isEmpty(pcommonsmailForm.organizationName)) pcommonsmailForm.organizationName = PublicCommonsUtils.getOrganizationName(loginDataDto.getLocalgovInfo());
			if (StringUtil.isEmpty(pcommonsmailForm.organizationDomainName))pcommonsmailForm.organizationDomainName = loginDataDto.getLocalgovInfo().domain;
			if (StringUtil.isEmpty(pcommonsmailForm.organizationCode)) pcommonsmailForm.organizationCode = organizationCode;
			if (StringUtil.isEmpty(pcommonsmailForm.officeName))pcommonsmailForm.officeName = group.name;
			if (StringUtil.isEmpty(pcommonsmailForm.officeNameKana))pcommonsmailForm.officeNameKana = group.namekana;
			if (StringUtil.isEmpty(pcommonsmailForm.officeLocationArea))pcommonsmailForm.officeLocationArea = group.address;
			if (StringUtil.isEmpty(pcommonsmailForm.phone))pcommonsmailForm.phone = group.phone;
			if (StringUtil.isEmpty(pcommonsmailForm.fax))pcommonsmailForm.fax = group.fax;
			if (StringUtil.isEmpty(pcommonsmailForm.email))pcommonsmailForm.email = group.email;
			if (StringUtil.isEmpty(pcommonsmailForm.officeDomainName))pcommonsmailForm.officeDomainName = group.domain;

			// 作成組織
			if (StringUtil.isEmpty(pcommonsmailForm.organizationNameEditorial)) pcommonsmailForm.organizationNameEditorial = PublicCommonsUtils.getOrganizationName(loginDataDto.getLocalgovInfo());
			if (StringUtil.isEmpty(pcommonsmailForm.organizationDomainNameEditorial))pcommonsmailForm.organizationDomainNameEditorial = loginDataDto.getLocalgovInfo().domain;
			if (StringUtil.isEmpty(pcommonsmailForm.organizationCodeEditorial)) pcommonsmailForm.organizationCodeEditorial = organizationCode;
			if (StringUtil.isEmpty(pcommonsmailForm.officeNameEditorial))pcommonsmailForm.officeNameEditorial = group.name;
			if (StringUtil.isEmpty(pcommonsmailForm.officeNameKanaEditorial))pcommonsmailForm.officeNameKanaEditorial = group.namekana;
			if (StringUtil.isEmpty(pcommonsmailForm.officeLocationAreaEditorial))pcommonsmailForm.officeLocationAreaEditorial = group.address;
			if (StringUtil.isEmpty(pcommonsmailForm.phoneEditorial))pcommonsmailForm.phoneEditorial = group.phone;
			if (StringUtil.isEmpty(pcommonsmailForm.faxEditorial))pcommonsmailForm.faxEditorial = group.fax;
			if (StringUtil.isEmpty(pcommonsmailForm.emailEditorial))pcommonsmailForm.emailEditorial = group.email;
			if (StringUtil.isEmpty(pcommonsmailForm.officeDomainNameEditorial))pcommonsmailForm.officeDomainNameEditorial = group.domain;
		}

		// 公式発表日時
		if (StringUtil.isEmpty(pcommonsmailForm.reporttime)) pcommonsmailForm.reporttime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

		return errors;
	}

	/**
	 * 緊急速報メールの入力チェック
	 * @param titleContent タイトル、もしくは本文
	 * @return エラー項目
	 */
	public List<String> checkAreaMail(String titleContent) {
		List<String> errorMessageParam = new ArrayList<String>();
		try {
			final String uriPattern = "(?m)(?i)^.*(http:\\/\\/|https:\\/\\/).*$";
			final String addressPattern = "(?m)^.*[0-9０-９a-zａ-ｚA-ZＡ-Ｚ]+[@][0-9０-９a-zａ-ｚA-ZＡ-Ｚ]+.*$";
			final String numberPattern = "(?m)^.*(\\+|0)[\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#].*$|(?m)^.*(\\*|#)[\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#].*$|(?m)^.*(tel:)[\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#].*$|(?m)^.*(tel-av:)[\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#][\\(\\).-]*[\\d\\*#].*$";

			// URIチェック
			if (Pattern.compile(uriPattern).matcher(titleContent).find()) errorMessageParam.add("URI");
			// メールアドレスチェック
			if (Pattern.compile(addressPattern).matcher(titleContent).find()) errorMessageParam.add(lang.__("E-mail address"));
			// mailtoの内包チェック
			if (titleContent.toLowerCase().indexOf("mailto") != -1) errorMessageParam.add("mailto");
			// 電話番号チェック
			if (Pattern.compile(numberPattern).matcher(titleContent).find()) errorMessageParam.add(lang.__("Phone number"));

			// 機種依存文字チェック
			for (int i = 0; i < titleContent.length(); i++) {
				// 1文字分をバイト配列として取得
				byte[] checkByte = titleContent.substring(i, i + 1).getBytes("MS932");
				if (checkByte.length != 2) {
					continue;
				}
				// 1のビット列と論理積をとって、符号なしにする
				int checkInt1 = checkByte[0] & 0xFF;
				int checkInt2 = checkByte[1] & 0xFF;
				// 上位8ビットと下位8ビットとして論理和をとり、0x0000の整数値として取得
				int targetChar = (checkInt1 << 8) | checkInt2;
				// 16進表記で下記の範囲なら入力不可
				if (Integer.decode("0x8740").intValue() <= targetChar && targetChar <= Integer.decode("0x879C").intValue()) {
					errorMessageParam.add(lang.__("Platform dependent characters"));break;
				} else if (Integer.decode("0xED40").intValue() <= targetChar && targetChar <= Integer.decode("0xEDFC").intValue()) {
					errorMessageParam.add(lang.__("Platform dependent characters"));break;
				} else if (Integer.decode("0xFA40").intValue() <= targetChar && targetChar <= Integer.decode("0xFC4C").intValue()) {
					errorMessageParam.add(lang.__("Platform dependent characters"));break;
				}
			}
		} catch (Exception e) {
			// 住民伝達を優先するため、入力チェックの例外エラーは記録だけとって無視
			logger.error(loginDataDto.logInfo(), e);
		}
		return errorMessageParam;
	}
}
