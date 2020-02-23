/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import facebook4j.FacebookException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.constant.PubliccommonsSendStatusValue;
import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.dto.AntidisasterInformationDto;
import jp.ecom_plat.saigaitask.dto.DamageInformationDto;
import jp.ecom_plat.saigaitask.dto.EventInformationDto;
import jp.ecom_plat.saigaitask.dto.GeneralInformationDto;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.RefugeInformationDto;
import jp.ecom_plat.saigaitask.dto.ShelterInformationDto;
import jp.ecom_plat.saigaitask.dto.SlimerDto;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendToInfo;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.PcommonsmediaForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportRefugeInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportShelterInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsSendToInfoService;
import jp.ecom_plat.saigaitask.service.db.ReportDataService;
import jp.ecom_plat.saigaitask.service.db.Reportcontent2DataService;
import jp.ecom_plat.saigaitask.service.db.StationMasterService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.service.publiccommons.PcAntidisasterService;
import jp.ecom_plat.saigaitask.service.publiccommons.PcEvacuationService;
import jp.ecom_plat.saigaitask.service.publiccommons.PublicCommonsService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 公共情報コモンズ（メディア）ページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class PcommonsmediaAction extends PageBaseAction {

	protected PcommonsmediaForm pcommonsmediaForm;

	/** 公共情報コモンズの本部設置状況サービス */
	@Resource
	public PcAntidisasterService pcAntidisasterService;

	/** 公共情報コモンズの避難勧告・指示サービス */
	@Resource
	public PcEvacuationService pcEvacuationService;

	@Resource
	protected ReportDataService reportDataService;

	@Resource
	protected Reportcontent2DataService reportcontent2DataService;

	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;

	@Resource
	protected MapService mapService;

	@Resource
	protected PubliccommonsReportRefugeInfoService publiccommonsReportRefugeInfoService;

	@Resource
	protected NoticeTemplateService noticeTemplateService;

	@Resource
	protected PubliccommonsReportShelterInfoService publiccommonsReportShelterInfoService;

	@Resource
	protected GroupInfoService groupInfoService;

	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;

	@Resource
	protected PubliccommonsSendToInfoService publiccommonsSendToInfoService;

	@Resource
	protected StationMasterService stationMasterService;

	/** 一括変更Dto */
	@Resource
	public SlimerDto slimerDto;

    /** コモンズ送信情報 */
    @Resource
    protected PCommonsSendDto pCommonsSendDto;

	/** サービスクラス */
	@Resource protected MenutableInfoService menutableInfoService;

	/** 避難情報リスト */
	public List<RefugeInformationDto> refugeInformationList;

	/** 避難所情報リスト */
	public List<ShelterInformationDto> shelterInformationList;

	/** イベント情報 */
	public EventInformationDto eventInformationDto;

	/** 被害情報 */
	public DamageInformationDto damageInformationDto;

	/** 被害情報リスト */
	public List<DamageInformationDto> damageInformationList;

	/** お知らせ情報 */
	public GeneralInformationDto generalInformationDto;

	/** お知らせ情報リスト */
	public List<GeneralInformationDto> generalInformationList;

	/** イベント情報リスト */
	public List<EventInformationDto> eventInformationList;

	/** 災害対策本部設置状況情報 */
	public AntidisasterInformationDto antidisasterInformationDto;

	/** 公共情報コモンズ送信先情報リスト */
	List<PubliccommonsSendToInfo> pcommonsSendToInfoList;

	/** 避難勧告／避難指示セッション */
	private final String SESSION_ATTR_REFUGE = "REFUGE";

	/** 避難所セッション */
	private final String SESSION_ATTR_SHELTER = "SHELTER";

	/** イベント情報セッション */
	private final String SESSION_ATTR_EVENT = "EVENT";

	/** 被害情報セッション */
	private final String SESSION_ATTR_DAMAGE_INFORMATION = "DAMAGE_INFORMATION";

	/** お知らせセッション */
	private final String SESSION_ATTR_GENERAL_INFORMATION = "GENERAL_INFORMATION";

	/** 本部設置状況セッション */
	private final String SESSION_ATTR_ANTIDISASTER_HEADQUARTER = "ANTIDISASTER_HEADQUARTER";

	/** 避難所全閉鎖フラグ */
	public boolean isAllClose = true;				//避難所(true:全閉鎖)、避難勧告・避難指示(true:全解除)

	/** 通知グループ情報リスト */
	public List<NoticegroupInfo> noticegroupInfoItems;


	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("slimerDto", slimerDto);
		model.put("refugeInformationList", refugeInformationList);
		model.put("shelterInformationList", shelterInformationList);
		model.put("eventInformationDto", eventInformationDto);
		model.put("damageInformationDto", damageInformationDto);
		model.put("damageInformationList", damageInformationList);
		model.put("generalInformationDto", generalInformationDto);
		model.put("generalInformationList", generalInformationList);
		model.put("eventInformationList", eventInformationList);
		model.put("antidisasterInformationDto", antidisasterInformationDto);
		model.put("isAllClose", isAllClose);
		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("pcommonsmediaForm", pcommonsmediaForm);

	}

	/**
	 * 公共情報コモンズ（メディア）ページを表示する.
	 * @return index.jsp
	 * @throws ParseException
	 */
	//@SuppressWarnings("unchecked")
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/pcommonsmedia","/page/pcommonsmedia/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute PcommonsmediaForm pcommonsmediaForm) throws ParseException {
		this.pcommonsmediaForm = pcommonsmediaForm;
		initPage("pcommonsmedia", pcommonsmediaForm);

		session.removeAttribute(SESSION_ATTR_REFUGE);
		session.removeAttribute(SESSION_ATTR_SHELTER);
		session.removeAttribute(SESSION_ATTR_EVENT);
		session.removeAttribute(SESSION_ATTR_DAMAGE_INFORMATION);
		session.removeAttribute(SESSION_ATTR_GENERAL_INFORMATION);
		session.removeAttribute(SESSION_ATTR_ANTIDISASTER_HEADQUARTER);

		setAdditionalReceiver();

		//メディア向けXMLデータと画面表示初期値を取得
		ActionMessages errors = setInformationList();

		// 災害の有無チェック
		Boolean flgTrackValid = true;	// 災害有無
		if(PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)){
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			if (trackData == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send because of not yet disaster registration."), false));
				flgTrackValid = false;
			}
		}

		// 避難所
		if (flgTrackValid && PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType)) {
			//1箇所でも閉鎖以外のステータスが存在した場合、希望公開終了日時の入力欄は表示しない
			shelterInformationList = new ArrayList<ShelterInformationDto>((List<ShelterInformationDto>)session.getAttribute("SHELTER"));
			isAllClose = PublicCommonsService.checkAllCloseShelter(shelterInformationList);
		// 避難勧告・避難指示
		} else if (flgTrackValid && PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)){
			//1箇所でも解除以外のステータスが存在した場合、希望公開終了日時の入力欄は表示しない
			refugeInformationList = new ArrayList<RefugeInformationDto>();
			List<RefugeInformationDto> list = (List<RefugeInformationDto>)session.getAttribute("REFUGE");
			if(list!=null) refugeInformationList.addAll(list);
			isAllClose = PublicCommonsService.checkAllCloseRefuge(refugeInformationList);
		// イベント
		}else if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)){
			if("".equals(pcommonsmediaForm.documentId)){		//ドロップダウンメニュークリック時
				pcommonsmediaForm.documentId = "0";
			}
		// 被害情報
		}else if (flgTrackValid && PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType)){
			if ("".equals(pcommonsmediaForm.complementaryInfo) && damageInformationDto.remarks != null && !"".equals(damageInformationDto.remarks)) {		// 補足情報が未入力の場合、4号様式の備考を挿入
				pcommonsmediaForm.complementaryInfo = damageInformationDto.remarks.replaceAll("\r\n", " ");		// 改行はスペースに変換
			}
		// お知らせ
		}else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)){
			if("".equals(pcommonsmediaForm.documentId)){		//ドロップダウンメニュークリック時
				pcommonsmediaForm.documentId = "0";
				pcommonsmediaForm.disasterInformationType = "ORDINARY";
			}
		// 災害対策本部設置状況
		}else if (flgTrackValid && PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)){
			// 解散以外の場合、希望公開終了日時の入力欄は表示しない
			antidisasterInformationDto = (AntidisasterInformationDto)session.getAttribute("ANTIDISASTER_HEADQUARTER");
			isAllClose = PublicCommonsService.checkAllCloseAntidisaster(antidisasterInformationDto);
		}

		if (errors.size() > 0) {
			//ActionMessagesUtil.addErrors(bindingResult, errors);
			ActionMessagesUtil.addErrors(session, errors);
		}

		// デフォルト設定
		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist)
			pcommonsmediaForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());

		//トークンを設定
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);
		return "page/pcommonsmedia/index";
	}

	/**
	 * 送信処理
	 * @return 次のページ
	 * @throws ParseException
	 * @throws FacebookException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/pcommonsmedia/send"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String send(Map<String,Object>model,
			@Valid @ModelAttribute PcommonsmediaForm pcommonsmediaForm, BindingResult bindingResult) throws ParseException{
		this.pcommonsmediaForm = pcommonsmediaForm;

		ActionMessages errors = new ActionMessages();
		pcommonsmediaForm.validationCheckflg = false;
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

		// 災害の有無チェック
		if(PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)){
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			if (trackData == null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send because of not yet disaster registration."), false));
			} else {
				if (StringUtil.isEmpty(pcommonsmediaForm.trackdataname)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Disaster name<!--2-->")), false));
				}
			}
		}

		// 都道府県から避難勧告・避難指示は送信不可 2015/3/26
		if(PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)){
			if (PublicCommonsUtils.checkPrefecture(loginDataDto.getLocalgovInfo().city)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Sending from prefectures is not allowed. Send data from municipalities."), false));
			}
		}

		// 管理画面で発信先が設定されていない場合、送信不可 2014/7/11
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
		}

		// 送信するXMLデータ有無のチェック
		{
			if (PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType))  {
//				shelterInformationList = new ArrayList<ShelterInformationDto>((List<ShelterInformationDto>)session.getAttribute("SHELTER")); // 2017.07.28 kawada 避難所：ＮＵＬＬ対応にて変更
				shelterInformationList = new ArrayList<ShelterInformationDto>();
				List<ShelterInformationDto> list = (List<ShelterInformationDto>)session.getAttribute("SHELTER");
				if(list!=null) shelterInformationList.addAll(list);
				if (shelterInformationList.isEmpty()) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Register shelter open status."), false));
				}
				// 未開設のみの発信はNG
				if(PublicCommonsService.checkAllEstablishShelter(shelterInformationList)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Can't send data. Check all shelter are closed."), false));
				}
				// 必須チェック
				int n = 0;
				for (ShelterInformationDto r : shelterInformationList) {
					n++;
					if (r.type.isEmpty()) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Type L-Alert ({0} line)", Integer.toString(n))), false));
					if (r.shelterName.isEmpty()) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Shelter name ({0} line)", Integer.toString(n))), false));
					if (r.shelterAddress.isEmpty()) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Address ({0} line)", Integer.toString(n))), false));
					if (r.circle.isEmpty()) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Coordinates ({0} line)", Integer.toString(n))), false));
					if (r.shelterStatus.isEmpty()) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Shelter status ({0} line)", Integer.toString(n))), false));
					if (Constants.SHELTER_OPEN().equals(r.shelterStatus) && r.setupTime==null) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Opening date and time ({0} line)", Integer.toString(n))), false));
					if (Constants.SHELTER_CLOSE().equals(r.shelterStatus) && r.closeTime==null) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Closing date and time ({0} line)", Integer.toString(n))), false));
				}
			} else if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)) {
//				refugeInformationList = new ArrayList<RefugeInformationDto>((List<RefugeInformationDto>)session.getAttribute("REFUGE")); // 2017.07.28 kawada 避難勧告：ＮＵＬＬ対応にて変更
				refugeInformationList = new ArrayList<RefugeInformationDto>();
				List<RefugeInformationDto> list = (List<RefugeInformationDto>)session.getAttribute("REFUGE");
				if(list!=null) refugeInformationList.addAll(list);
				if (refugeInformationList.isEmpty()) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Register announcement info."), false));
				}
				// 必須チェック
				int n = 0;
				for (RefugeInformationDto r : refugeInformationList) {
					n++;
					if (r.chikuName.isEmpty()) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("District name ({0} line)", Integer.toString(n))), false));
					// 全域は発令区分の未入力を無視
					if (r.hatureiKbn.isEmpty() && !lang.__("Whole district").equals(r.chikuName)) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement type ({0} line)", Integer.toString(n))), false));
					if (r.hatureiDateTime == null) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement time ({0} line)", Integer.toString(n))), false));
				}
			}else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType)) {
				damageInformationDto = (DamageInformationDto)session.getAttribute("DAMAGE_INFORMATION");
				if(damageInformationDto == null){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Create No.4-type-format"), false));
				} else {
					// 「施設被害額小計」は数値があって、「公立文教施設」～「公共土木施設」がいずれもNULLの場合、エラー
					if(StringUtil.isNotEmpty(damageInformationDto.subtotalDamageFacilities) &&
							StringUtil.isEmpty(damageInformationDto.publicScoolFacillities) &&
							StringUtil.isEmpty(damageInformationDto.agricultureFacilities) &&
							StringUtil.isEmpty(damageInformationDto.publicEngineeringFacilities))
					{
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Data exist in subtotal with no input data between public educational facilities and public works facilities."), false));
					}
					// 「被害額小計」は数値があって、「農業被害」～「その他」がいずれもNULLの場合、エラー
					if(StringUtil.isNotEmpty(damageInformationDto.subtotalOtherDamage) &&
							StringUtil.isEmpty(damageInformationDto.farmingDamage) &&
							StringUtil.isEmpty(damageInformationDto.forestryDamage) &&
							StringUtil.isEmpty(damageInformationDto.animalDamage) &&
							StringUtil.isEmpty(damageInformationDto.fisheriesDamage) &&
							StringUtil.isEmpty(damageInformationDto.commerceAndIndustryDamage) &&
							StringUtil.isEmpty(damageInformationDto.otherDamageOther))
					{
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Data exist in subtotal with no input data between damaged agriculture and others."), false));
					}
				}
			}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)) {
				antidisasterInformationDto = (AntidisasterInformationDto)session.getAttribute("ANTIDISASTER_HEADQUARTER");
				if (StringUtil.isEmpty(antidisasterInformationDto.antidisasterKbn)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Register disaster response HQ info."), false));
				} else {
					// 必須チェック
					if (antidisasterInformationDto.antidisasterKbn.isEmpty()) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("HQ type")), false));
					}
					if (antidisasterInformationDto.hatureiDateTime == null) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Installation and dissolution date and time")), false));
					}
				}
			}

			// エラーが見つかった場合、戻す
			if (errors.size() > 1) {
				//ActionMessagesUtil.addErrors(bindingResult, errors);
				ActionMessagesUtil.addErrors(session, errors);
				setupModel(model);
//				return "/page/?menutaskid="+pcommonsmediaForm.menutaskid+"&menuid=" + pcommonsmediaForm.menuid; // 2017.07.28 kawada 変更
				return index(model, pcommonsmediaForm);
			}
		}

		// イベント情報
		if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)) {
			if ("".equals(pcommonsmediaForm.title)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title of event content")), false));
			}
			if ("".equals(pcommonsmediaForm.text)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text of event content")), false));
			}
			if ("".equals(pcommonsmediaForm.area)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Venue")), false));
			}
			if ("".equals(pcommonsmediaForm.eventFrom)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("The start date")), false));
			}
			if ("".equals(pcommonsmediaForm.eventTo)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("The end date")), false));
			}
			if ("".equals(pcommonsmediaForm.eventFee)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Entry fee")), false));
			}
			if ("".equals(pcommonsmediaForm.personResponsible)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Person in charge")), false));
			}

			if (!"".equals(pcommonsmediaForm.eventFrom) && !"".equals(pcommonsmediaForm.eventTo)) {
				Date  eventFromDate = null;
				eventFromDate = DateFormat.getDateTimeInstance().parse(pcommonsmediaForm.eventFrom);
				Date  eventToDate = null;
				eventToDate = DateFormat.getDateTimeInstance().parse(pcommonsmediaForm.eventTo);
				if (eventToDate.before(eventFromDate)){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The closing date and time must be after the opening date and time."), false));
					Date  maxValidDateTimeDate = null;
					maxValidDateTimeDate = DateFormat.getDateTimeInstance().parse(PublicCommonsService.getMaxValidDateTime(pcommonsmediaForm.eventTo));
					Date  validDateTimeDate = null;
					validDateTimeDate = DateFormat.getDateTimeInstance().parse(pcommonsmediaForm.validdatetime);
					if (maxValidDateTimeDate.before(validDateTimeDate)){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The desired publishing end date and time should be before 90 days later of target event end date and time."), false));
					}
				}
			}
		}

		// お知らせ情報
		if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)) {
			if ("".equals(pcommonsmediaForm.division)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Classification")), false));
			}
			if ("".equals(pcommonsmediaForm.area)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Place<!--2-->")), false));
			}
			if ("".equals(pcommonsmediaForm.title)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title<!--2-->")), false));
			}
			if ("".equals(pcommonsmediaForm.text)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text")), false));
			}
		}

		// 災害対策本部設置状況
		if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)) {
			if ("".equals(pcommonsmediaForm.name)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("HQ name")), false));
			}
		}

		// 前回発令区分(必須チェック)
		 if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)) {
			if (pcommonsmediaForm.lasthatureiKbnMap !=null ) {
				for (int i = 0 ; i < refugeInformationList.size() ; i++) {
					// 解除の場合
					if (Constants.ISSUE_CANCEL_NAME().equals(refugeInformationList.get(i).hatureiKbn)) {
						// 画面で選択された前回発令区分をセット
				    	refugeInformationList.get(i).lasthatureiKbn = pcommonsmediaForm.lasthatureiKbnMap.get(refugeInformationList.get(i).chikuName);
					}
			    	// 前回発令区分が未指定且つ、発令区分が解除の場合はエラー
			    	if (StringUtil.isEmpty(refugeInformationList.get(i).lasthatureiKbn) && Constants.ISSUE_CANCEL_NAME().equals(refugeInformationList.get(i).hatureiKbn)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Last announcement type ({0} line)", toString(i+1)) ), false));
			    	}
			   }
			}
		} else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)) {
			if (pcommonsmediaForm.lasthatureiKbnMap !=null ) {
		    	// 前回発信本部種別が未指定且つ、本部種別が解散の場合はエラー
		    	if (StringUtil.isEmpty(antidisasterInformationDto.lastAntidisasterKbn) && Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(antidisasterInformationDto.antidisasterKbn)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Previous issued HQ type in case of dissolving system") ), false));
		    	}
			}
		}

		// Eメールタイトル(必須チェック)
		if ("".equals(pcommonsmediaForm.emailTitle)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title of confirmation e-mail")), false));
		}

		// Eメールタイトル(文字数チェック)
		if (pcommonsmediaForm.emailTitle.length() > 50) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title of confirmation e-mail"), "50"), false));
		}

		// Eメール配信先(必須チェック)
		if (( "".equals(pcommonsmediaForm.additionalReceiver)) && (pcommonsmediaForm.noticegroupinfoid == null || pcommonsmediaForm.noticegroupinfoid.isEmpty())) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("E-mail delivery destination")), false));
		}

		// 追加送付先(メールアドレス文法チェック)
		String mails[] = pcommonsmediaForm.additionalReceiver.split(",");
		for (String mail : mails) {
			if (!checkEmail(mail.trim())) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} is invalid e-mail address.", lang.__("Add delivery target")), false));
				break;
			}
		}

		// 発表組織(必須チェック)
		if ("".equals(pcommonsmediaForm.organizationName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Organization name<!--2-->")), false));
		}
		if ("".equals(pcommonsmediaForm.organizationCode)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("City code<!--2-->")), false));
		}
		if ("".equals(pcommonsmediaForm.organizationDomainName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Organization domain<!--2-->")), false));
		}
		if ("".equals(pcommonsmediaForm.officeName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department name")), false));
		}
		if ("".equals(pcommonsmediaForm.officeNameKana)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department name by kana")), false));
		}
		if ("".equals(pcommonsmediaForm.officeLocationArea)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department address")), false));
		}
		if ("".equals(pcommonsmediaForm.phone)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department phone number")), false));
		}
		if ("".equals(pcommonsmediaForm.fax)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department FAX number")), false));
		}
		if ("".equals(pcommonsmediaForm.email)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department  e-mail address")), false));
		}
		if ("".equals(pcommonsmediaForm.officeDomainName)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Announcement organization department domain")), false));
		}

		// 作成組織(必須チェック)
		if ("".equals(pcommonsmediaForm.organizationNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization organization name")), false));
		}
		if ("".equals(pcommonsmediaForm.organizationCodeEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization local gov. code")), false));
		}
		if ("".equals(pcommonsmediaForm.organizationDomainNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization organization domain")), false));
		}
		if ("".equals(pcommonsmediaForm.officeNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department name")), false));
		}
		if ("".equals(pcommonsmediaForm.officeNameKanaEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department name by kana")), false));
		}
		if ("".equals(pcommonsmediaForm.officeLocationAreaEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department address")), false));
		}
		if ("".equals(pcommonsmediaForm.phoneEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department phone number")), false));
		}
		if ("".equals(pcommonsmediaForm.faxEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department FAX number")), false));
		}
		if ("".equals(pcommonsmediaForm.emailEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department e-mail address")), false));
		}
		if ("".equals(pcommonsmediaForm.officeDomainNameEditorial)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Creating organization department domain")), false));
		}

		// 発信種別チェック
		if("".equals(pcommonsmediaForm.distributiontype)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Select delivery type")), false));
		}

		// 訂正・取消理由チェック
		if(("".equals(pcommonsmediaForm.description)) && (!"normalSend".equals(pcommonsmediaForm.distributiontype) && (!"".equals(pcommonsmediaForm.distributiontype)))){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Enter reasons  in case of cancel/ correct.")), false));
		}

		// 公式発表日時チェック
		if("".equals(pcommonsmediaForm.reporttime)){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Official announcement date and time")), false));
		}

		// 希望公開終了日時有効期間チェック
		if (!"".equals(pcommonsmediaForm.validdatetime) && !"".equals(pcommonsmediaForm.validdatetimetermfrom) && !"".equals(pcommonsmediaForm.validdatetimetermto)) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			    Date target = dateFormat.parse(pcommonsmediaForm.validdatetime);
			    Date from = dateFormat.parse(pcommonsmediaForm.validdatetimetermfrom);
			    Date to =dateFormat.parse(pcommonsmediaForm.validdatetimetermto);
			    // 範囲外の場合
				if (from.after(target) || to.before(target)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Desired date and time to publish must be set between 24hr and 72hr."), false));
				}
			} catch (ParseException e) {
				logger.error(loginDataDto.logInfo(), e);
				removeSessionAttr();
				throw new ServiceException(e);
			}
		}

		List<PubliccommonsReportData> publiccommonsReportDataItems = publiccommonsReportDataService.findByNoticetypeCategory(pcommonsmediaForm.sendType);

		// 重複投稿エラーチェック(同じ内容で通常や訂正をしていないか)
		if("normalSend".equals(pcommonsmediaForm.distributiontype) || "correctionSend".equals(pcommonsmediaForm.distributiontype)){
			String content = "";
			if(PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)){
				//過去のイベント取得
				eventInformationList = publicCommonsService.getEventInformationList(loginDataDto.getLocalgovinfoid());
				HashMap<String,EventInformationDto> map = new HashMap<String,EventInformationDto>();
				// 過去のイベント指定時
				if(!"0".equals(pcommonsmediaForm.documentId)){
					//同じドキュメントIDの最新の投稿だけ取得
					for (EventInformationDto eventInformation : eventInformationList) {
						map.put(eventInformation.documentid, eventInformation);
					}
					content = map.get(pcommonsmediaForm.documentId).content;
				}else{
					if (eventInformationList.size() > 0) content = eventInformationList.get(eventInformationList.size() - 1).content;
				}
			}else if(PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)){
				generalInformationList = publicCommonsService.getGeneralInformationList(loginDataDto.getLocalgovinfoid());
				HashMap<String,GeneralInformationDto> map = new HashMap<String,GeneralInformationDto>();
				if(!"0".equals(pcommonsmediaForm.documentId)){
					//同じドキュメントIDの最新の投稿だけ取得
					for (GeneralInformationDto generalInformation : generalInformationList) {
						map.put(generalInformation.documentid, generalInformation);
					}
					content = map.get(pcommonsmediaForm.documentId).content;
				}else{
					if (generalInformationList.size() > 0) content = generalInformationList.get(generalInformationList.size() - 1).content;
				}
			}else{
				// 先頭行を取得
				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, pcommonsmediaForm.sendType);
				if (noticemailDataItems.size() > 0) content = noticemailDataItems.get(0).content;
			}

			String sendMessage = createContent();	// 本文取得
			if (publiccommonsReportDataItems.size() > 0) {
				if(!"cancelSend".equals(publiccommonsReportDataItems.get(0).distributionType)){
					if("normalSend".equals(pcommonsmediaForm.distributiontype)){
						int description = content.indexOf(lang.__("[Correction or Revocation reason]"));
						if( description >= 0 ) content = content.substring(0,description);
					}
					if (sendMessage.equals(content)){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send. Confirm post is not duplicated with the same title and body text."), false));
					}
				}
			}
		}

		// 訂正と取消選択チェック
		if(!"normalSend".equals(pcommonsmediaForm.distributiontype)){
			// お知らせ、イベント
			if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType) || PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)) {
				// ドロップダウンで新規(documentID=0)を選んでいるのに、訂正や取消なのでエラー
				if ("0".equals(pcommonsmediaForm.documentId)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Correction or cancel is not selected with new info."), false));
				}
			// 避難勧告・避難指示、避難所、被害情報、災害対策本部設置状況
			} else {
				if (publiccommonsReportDataItems.size() > 0) {
					// 取消発信の直後に訂正や取消発信なのでエラー(避難勧告・避難指示、避難所、被害情報、災害対策本部設置状況)
					if("cancelSend".equals(publiccommonsReportDataItems.get(0).distributionType)){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Correction or cancel is not selected with new info."), false));
					}
				} else {
					// 過去に発信していないのに訂正や取消なのでエラー
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Correction or cancel is not selected with new info."), false));
				}
			}
		}

		// エラーが見つかった場合、戻す
		if (errors.size() > 0) {
			//ActionMessagesUtil.addErrors(request, errors);
			ActionMessagesUtil.addErrors(session, errors);
			pcommonsmediaForm.validationCheckflg = true;
			setupModel(model);
			//return "/page/?menutaskid="+pcommonsmediaForm.menutaskid+"&menuid=" + pcommonsmediaForm.menuid;
			return index(model, pcommonsmediaForm);

		}

		// 前回発信種別を保存
		if (publiccommonsReportDataItems.size() > 0) pcommonsmediaForm.distributiontypelast = publiccommonsReportDataItems.get(0).distributionType;

		// メディア送信
		boolean pcommonsmediaFlg = true;
		try {
			pcommonsmediaFlg= report();
			removeSessionAttr();

		} catch (FileNotFoundException e) {
			logger.error(loginDataDto.logInfo(), e);
			removeSessionAttr();
			throw new ServiceException(e);

		} catch (JAXBException e) {
			logger.error(loginDataDto.logInfo(), e);
			removeSessionAttr();
			throw new ServiceException(e);
		}

		boolean sendFlg = true;
		if (pcommonsmediaFlg) {
			// 確認用Eメール送信と履歴登録
			sendFlg = sendEmail(pcommonsmediaForm.emailTitle, createContent(), pcommonsmediaForm.noticegroupinfoid, pcommonsmediaForm.additionalReceiver, NoticeType.COMMONSMADIA);
		}

		// 送信完了メッセージ
		if (pcommonsmediaFlg && sendFlg){
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Auto delivered to L-Alert."), false));
		} else {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to send."), false));
		}
		//ActionMessagesUtil.addErrors(bindingResult, errors);
		ActionMessagesUtil.addErrors(session, errors);
		setupModel(model);
//		return "/page/?menutaskid="+pcommonsmediaForm.menutaskid+"&menuid=" + pcommonsmediaForm.menuid; // 2017.07.28 kawada 変更
		return index(model, pcommonsmediaForm);
	}


	/**
	 * メディア送信
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @return true: 送信OK, false: 送信失敗
	 * @throws ParseException
	 */
	//@SuppressWarnings("unchecked")
	private boolean report() throws FileNotFoundException, JAXBException, ParseException{
		List<PubliccommonsReportData> pReportDataList = new ArrayList<PubliccommonsReportData>();

		// 共通部
		pCommonsSendDto.localgovinfoid = loginDataDto.getLocalgovinfoid();																// 自治体ID
		pCommonsSendDto.distributiontype = pcommonsmediaForm.distributiontype;											// 発信種別(normalSend:新規or更新 correctionSend:訂正 cancelSend:取消)
		pCommonsSendDto.description = pcommonsmediaForm.description;														// 訂正・取消理由
		pCommonsSendDto.distributiontypelast = pcommonsmediaForm.distributiontypelast;								// 前回発信種別
		pCommonsSendDto.documentId = pcommonsmediaForm.documentId;													// documentID
		pCommonsSendDto.documentRevision = pcommonsmediaForm.documentRevision;									// 版数
		pCommonsSendDto.categoryType = pcommonsmediaForm.sendType;														// カテゴリ
		pCommonsSendDto.trackdataname = pcommonsmediaForm.trackdataname;											// 災害名称
		pCommonsSendDto.trackdataid = loginDataDto.getTrackdataid();																	// 記録データID

		// 運用種別
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(loginDataDto.getLocalgovinfoid());
		// 訓練モード中の場合、本番から訓練に自動的に変更する
		if(0<loginDataDto.getTrackdataid()) {
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

		// 通常発信と訂正発信は、画面表示値で発信
		if (!"cancelSend".equals(pcommonsmediaForm.distributiontype)) {
			Timestamp now = new Timestamp(System.currentTimeMillis());

			// 公式発表日時
			try{
				pCommonsSendDto.reporttime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(pcommonsmediaForm.reporttime).getTime());
			}catch (Exception e){
				pCommonsSendDto.reporttime = now;
				logger.error(loginDataDto.logInfo(), e);
			}

			pCommonsSendDto.createdatetime = now;																						// 作成日時
			pCommonsSendDto.targetdatetime = now;																						// 希望公開開始日時
			pCommonsSendDto.validdatetime = pcommonsmediaForm.validdatetime;											// 希望公開終了日時
			pCommonsSendDto.complementaryInfo = pcommonsmediaForm.complementaryInfo;							// 補足情報
			pCommonsSendDto.personResponsible = pcommonsmediaForm.personResponsible;								// 発表組織 担当者名
			pCommonsSendDto.organizationName = pcommonsmediaForm.organizationName;								// 発表組織 組織名
			pCommonsSendDto.organizationCode = pcommonsmediaForm.organizationCode;									// 発表組織 地方公共団体コード
			pCommonsSendDto.organizationDomainName = pcommonsmediaForm.organizationDomainName;		// 発表組織 組織ドメイン
			pCommonsSendDto.officeName = pcommonsmediaForm.officeName;													// 発表組織 部署名
			pCommonsSendDto.officeNameKana = pcommonsmediaForm.officeNameKana;									// 発表組織 部署名(カナ)
			pCommonsSendDto.officeLocationArea = pcommonsmediaForm.officeLocationArea;								// 発表組織 部署住所
			pCommonsSendDto.phone = pcommonsmediaForm.phone;																	// 発表組織 部署電話番号
			pCommonsSendDto.fax = pcommonsmediaForm.fax;																			// 発表組織 部署FAX番号
			pCommonsSendDto.email = pcommonsmediaForm.email;																	// 発表組織 部署メールアドレス
			pCommonsSendDto.officeDomainName = pcommonsmediaForm.officeDomainName;							// 発表組織 部署ドメイン
			pCommonsSendDto.organizationNameEditorial = pcommonsmediaForm.organizationNameEditorial;		// 作成組織 組織名
			pCommonsSendDto.organizationCodeEditorial = pcommonsmediaForm.organizationCodeEditorial;			// 作成組織 地方公共団体コード
			pCommonsSendDto.organizationDomainNameEditorial = pcommonsmediaForm.organizationDomainNameEditorial;		// 作成組織 組織ドメイン
			pCommonsSendDto.officeNameEditorial = pcommonsmediaForm.officeNameEditorial;							// 作成組織 部署名
			pCommonsSendDto.officeNameKanaEditorial = pcommonsmediaForm.officeNameKanaEditorial;			// 作成組織 部署名(カナ)
			pCommonsSendDto.officeLocationAreaEditorial = pcommonsmediaForm.officeLocationAreaEditorial;		// 作成組織 部署住所
			pCommonsSendDto.phoneEditorial = pcommonsmediaForm.phoneEditorial;											// 作成組織 部署電話番号
			pCommonsSendDto.faxEditorial = pcommonsmediaForm.faxEditorial;													// 作成組織 部署FAX番号
			pCommonsSendDto.emailEditorial = pcommonsmediaForm.emailEditorial;											// 作成組織 部署メールアドレス
			pCommonsSendDto.officeDomainNameEditorial = pcommonsmediaForm.officeDomainNameEditorial;	// 作成組織 部署ドメイン

			// テストノード利用時(開発時(tdv/jdv指定時))のみ事業者を追記
			String head= PublicCommonsUtils.getDocumentIdHead();
			if ("tdv".equals(head) || "jdv".equals(head)) {
				String Corpname = PublicCommonsUtils.getCorpname();
				if (StringUtil.isNotEmpty(Corpname)) pCommonsSendDto.organizationName = pCommonsSendDto.organizationName + " (" + Corpname + ")";
			}

		// 取消発信は、前回発信内容で発信
		} else {
			// 検索キーが異なるため注意
			if (PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType)) {		//避難所
				pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.SHELTER, loginDataDto.getTrackdataid(), null, "");
			}else if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)) {	//避難勧告・避難指示
				pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.EVACUATION_ORDER, loginDataDto.getTrackdataid(), null, "");
			}else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType)) {		//被害情報
				pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.DAMAGE_INFORMATION, null, loginDataDto.getLocalgovinfoid(), "");
			}else if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)) {	//イベント情報
				pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.EVENT, null, loginDataDto.getLocalgovinfoid(), "");
			}else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)) {	//お知らせ情報
				pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.GENERAL_INFORMATION, null, loginDataDto.getLocalgovinfoid(), "");
			}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)) {	//災害対策本部設置状況
				pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.ANTIDISASTER_HEADQUARTER, loginDataDto.getTrackdataid(), null, "");
			}

			// 過去の発信が取得できない場合はエラー
			if (pReportDataList == null) return false;

			pCommonsSendDto.createdatetime =  pReportDataList.get(0).createtime;											// 作成日時
			pCommonsSendDto.firstcreatedatetime = pReportDataList.get(0).startsendtime;									// 初版作成日時
			pCommonsSendDto.reporttime = pReportDataList.get(0).reporttime;													// 公式発表日時
			pCommonsSendDto.targetdatetime =  pReportDataList.get(0).targetdatetime;									// 希望公開開始日時
			//pCommonsSendDto.validdatetime = pReportDataList.get(0).validDateTime;											// 希望公開終了日時
			pCommonsSendDto.complementaryInfo = pReportDataList.get(0).complementaryinfo;							// 補足情報
			pCommonsSendDto.personResponsible = pReportDataList.get(0).personresponsible;								// 発表組織 担当者名
			pCommonsSendDto.organizationName = pReportDataList.get(0).organizationname;								// 発表組織 組織名
			pCommonsSendDto.organizationCode = pReportDataList.get(0).organizationcode;								// 発表組織 地方公共団体コード
			pCommonsSendDto.organizationDomainName = pReportDataList.get(0).organizationdomainname;		// 発表組織 組織ドメイン
			pCommonsSendDto.officeName = pReportDataList.get(0).officename;													// 発表組織 部署名
			pCommonsSendDto.officeNameKana = pReportDataList.get(0).officenamekana;									// 発表組織 部署名(カナ)
			pCommonsSendDto.officeLocationArea = pReportDataList.get(0).officelocationarea;								// 発表組織 部署住所
			pCommonsSendDto.phone = pReportDataList.get(0).phone;																// 発表組織 部署電話番号
			pCommonsSendDto.fax = pReportDataList.get(0).fax;																			// 発表組織 部署FAX番号
			pCommonsSendDto.email = pReportDataList.get(0).email;																	// 発表組織 部署メールアドレス
			pCommonsSendDto.officeDomainName = pReportDataList.get(0).officedomainname;							// 発表組織 部署ドメイン
			pCommonsSendDto.organizationNameEditorial = pReportDataList.get(0).organizationnameeditorial;		// 作成組織 組織名
			pCommonsSendDto.organizationCodeEditorial = pReportDataList.get(0).organizationcodeeditorial;			// 作成組織 地方公共団体コード
			pCommonsSendDto.organizationDomainNameEditorial = pReportDataList.get(0).organizationdomainnameeditorial;		// 作成組織 組織ドメイン
			pCommonsSendDto.officeNameEditorial = pReportDataList.get(0).officenameeditorial;							// 作成組織 部署名
			pCommonsSendDto.officeNameKanaEditorial = pReportDataList.get(0).officenamekanaeditorial;			// 作成組織 部署名(カナ)
			pCommonsSendDto.officeLocationAreaEditorial = pReportDataList.get(0).officelocationareaeditorial;		// 作成組織 部署住所
			pCommonsSendDto.phoneEditorial = pReportDataList.get(0).phoneeditorial;											// 作成組織 部署電話番号
			pCommonsSendDto.faxEditorial = pReportDataList.get(0).faxeditorial;													// 作成組織 部署FAX番号
			pCommonsSendDto.emailEditorial = pReportDataList.get(0).emaileditorial;											// 作成組織 部署メールアドレス
			pCommonsSendDto.officeDomainNameEditorial = pReportDataList.get(0).officedomainnameeditorial;	// 作成組織 部署ドメイン
		}

		// 通常発信と訂正発信は、画面表示値で発信
		if (!"cancelSend".equals(pcommonsmediaForm.distributiontype)) {
			// 通常発信・訂正発信 - 避難所
			if (PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType)) {
				shelterInformationList = new ArrayList<ShelterInformationDto>((List<ShelterInformationDto>)session.getAttribute("SHELTER"));
				return publicCommonsService.sendMediaShelter(shelterInformationList, pCommonsSendDto);
			// 通常発信・訂正発信 - 被害情報
			}else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType)) {
				damageInformationDto = (DamageInformationDto)session.getAttribute("DAMAGE_INFORMATION");
				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.DAMAGE_INFORMATION);
				return publicCommonsService.sendMediaDamageInformation(noticemailDataItems, damageInformationDto, pCommonsSendDto);
			// 通常発信・訂正発信 - 避難勧告・避難指示
			}else if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)) {
				refugeInformationList = new ArrayList<RefugeInformationDto>((List<RefugeInformationDto>)session.getAttribute("REFUGE"));
				pCommonsSendDto.refugeInformationList = refugeInformationList;
				PublicCommonsUtils.convertAllArea(pCommonsSendDto.refugeInformationList);
				return publicCommonsService.sendMediaEvacuationOrder(pCommonsSendDto);
			// 通常発信・訂正発信 - イベント
			}else if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)) {
				pCommonsSendDto.documentId = pcommonsmediaForm.documentId;
				pCommonsSendDto.documentRevision = pcommonsmediaForm.documentRevision;
				pCommonsSendDto.title = pcommonsmediaForm.title;
				pCommonsSendDto.contentDescription = pcommonsmediaForm.title;
				pCommonsSendDto.text = pcommonsmediaForm.text;
				pCommonsSendDto.area = pcommonsmediaForm.area;
				if (eventInformationDto == null) { // 2017.07.28 kawada ＮＵＬＬチェック
					eventInformationDto = new EventInformationDto(); // 2017.07.28 kawada
				}
				eventInformationDto.title = pcommonsmediaForm.title;
				eventInformationDto.text = pcommonsmediaForm.text;
				eventInformationDto.eventArea = pcommonsmediaForm.area;
				eventInformationDto.eventFrom = pcommonsmediaForm.eventFrom;
				eventInformationDto.eventTo = pcommonsmediaForm.eventTo;
				eventInformationDto.eventFee = pcommonsmediaForm.eventFee;
				eventInformationDto.eventNotificationUri = pcommonsmediaForm.notificationUri;
				eventInformationDto.eventFileUri = pcommonsmediaForm.fileUri;
				eventInformationDto.eventFileCaption = pcommonsmediaForm.fileCaption;
				eventInformationDto.disasterInformationType = pcommonsmediaForm.disasterInformationType;		// 情報識別区分
				eventInformationDto.distributiontype = pcommonsmediaForm.distributiontype;								//発信種別
				eventInformationDto.content = createContent();
				eventInformationDto.emailtitle = pcommonsmediaForm.emailTitle;
				// MimeTypeとMediaType
				if (StringUtil.isNotEmpty(pcommonsmediaForm.fileUri)) {
					eventInformationDto.mimeType = PublicCommonsUtils.getMimeType(pcommonsmediaForm.fileUri);
					eventInformationDto.mediaType =  PublicCommonsUtils.getMediaType(pcommonsmediaForm.fileUri);
				}
				// 希望公開終了日時(未入力は、48時間後をセット)
//				if(StringUtil.isNotEmpty(pcommonsmediaForm.validdatetime)){ // 2017.07.28 kawada 変更
				if(StringUtil.isEmpty(pcommonsmediaForm.validdatetime)){
					eventInformationDto.validDateTime = PublicCommonsService.getValidDateTime();
				}else{
					Date  validDateTimeDate = DateFormat.getDateTimeInstance().parse(pcommonsmediaForm.validdatetime);
					Date  eventToDate = DateFormat.getDateTimeInstance().parse(pcommonsmediaForm.eventTo);
					// 開催終了日時＞希望公開終了日時の場合
					if(eventToDate.after(validDateTimeDate)){
						eventInformationDto.validDateTime = eventInformationDto.eventTo;	// 開催終了日時と同日時に設定
					}else{
						eventInformationDto.validDateTime = pcommonsmediaForm.validdatetime;
					}
				}
				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.EVENT);
				return publicCommonsService.sendMediaEventInformation(noticemailDataItems, eventInformationDto, pCommonsSendDto);
			// 通常発信・訂正発信 - お知らせ
			} else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)) {
				pCommonsSendDto.documentId = pcommonsmediaForm.documentId;
				pCommonsSendDto.documentRevision = pcommonsmediaForm.documentRevision;
				pCommonsSendDto.title = pcommonsmediaForm.title;
				pCommonsSendDto.contentDescription = pcommonsmediaForm.title;
				pCommonsSendDto.text = pcommonsmediaForm.text;
				pCommonsSendDto.area = pcommonsmediaForm.area;
				if (generalInformationDto == null) { // 2017.07.28 kawada ＮＵＬＬチェック
					generalInformationDto = new GeneralInformationDto();
				}
				generalInformationDto.division = pcommonsmediaForm.division;
				generalInformationDto.area = pcommonsmediaForm.area;
				generalInformationDto.title = pcommonsmediaForm.title;
				generalInformationDto.text = pcommonsmediaForm.text;
				generalInformationDto.notificationUri = pcommonsmediaForm.notificationUri;
				generalInformationDto.fileUri = pcommonsmediaForm.fileUri;
				generalInformationDto.fileCaption = pcommonsmediaForm.fileCaption;
				generalInformationDto.disasterInformationType = pcommonsmediaForm.disasterInformationType;
				generalInformationDto.emailtitle = pcommonsmediaForm.emailTitle;
				generalInformationDto.distributiontype = pcommonsmediaForm.distributiontype;
				generalInformationDto.content = createContent();
				// MimeTypeとMediaType
				if (StringUtil.isNotEmpty(pcommonsmediaForm.fileUri)) {
					generalInformationDto.mimeType = PublicCommonsUtils.getMimeType(pcommonsmediaForm.fileUri);
					generalInformationDto.mediaType = PublicCommonsUtils.getMediaType(pcommonsmediaForm.fileUri);
				}
				//希望公開終了日時(未入力は、48時間後をセット)
				generalInformationDto.validDateTime = StringUtil.isEmpty(pcommonsmediaForm.validdatetime) ? PublicCommonsService.getValidDateTime() : pcommonsmediaForm.validdatetime;

				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.GENERAL_INFORMATION);
				return publicCommonsService.sendMediaGeneralInformation(noticemailDataItems, generalInformationDto, pCommonsSendDto);
			// 通常発信・訂正発信 - 災害対策本部設置状況
			}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)) {
				antidisasterInformationDto = (AntidisasterInformationDto)session.getAttribute("ANTIDISASTER_HEADQUARTER");
				antidisasterInformationDto.name = pcommonsmediaForm.name;
				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.ANTIDISASTER_HEADQUARTER);
				return publicCommonsService.sendMediaAntidisasterInformation(noticemailDataItems, antidisasterInformationDto, pCommonsSendDto);
			}
		// 取消発信は、前回発信内容で発信
		} else {
			// 取消発信 - 避難所
			if (PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType)) {
				shelterInformationList = new ArrayList<ShelterInformationDto>();
				for (int i = 0; i < pReportDataList.get(0).publiccommonsReportDataLastShelterList.size(); i++){
					ShelterInformationDto dto = new ShelterInformationDto();
					dto.chikuName = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).chikuname;
					dto.closeTime = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).closetime;
					dto.setupTime = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).setuptime;
					dto.shelterAddress = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).shelteraddress;
					dto.shelterCapacity = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).sheltercapacity;
					dto.shelterFax = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).shelterfax;
					dto.shelterName = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).sheltername;
					dto.shelterPhone = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).shelterphone;
					dto.shelterStaff = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).shelterstaff;
					dto.shelterStatus = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).shelterstatus;
					dto.circle = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).circle;
					dto.type = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).type;
					dto.typeDetail = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).typedetail;
					dto.headCount = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).headcount;
					dto.headCountVoluntary = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).headcountvoluntary;
					dto.houseHolds = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).households;
					dto.houseHoldsVoluntary = pReportDataList.get(0).publiccommonsReportDataLastShelterList.get(i).householdsvoluntary;
					shelterInformationList.add(dto);
				}
				return publicCommonsService.sendMediaShelter(shelterInformationList, pCommonsSendDto);
			// 取消発信 - 被害情報
			} else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType)) {
				damageInformationDto.remarks = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).remarks;
				damageInformationDto.deadPeople = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).deadpeople;
				damageInformationDto.missingPeople = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).missingpeople;
				damageInformationDto.seriouslyInjuredPeople = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).seriouslyinjuredpeople;
				damageInformationDto.slightlyInjuredPeople = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).slightlyinjuredpeople;
				damageInformationDto.totalCollapseBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).totalcollapsebuilding;
				damageInformationDto.totalCollapseHousehold = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).totalcollapsehousehold;
				damageInformationDto.totalCollapseHuman = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).totalcollapsehuman;
				damageInformationDto.halfCollapseBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).halfcollapsebuilding;
				damageInformationDto.halfCollapseHousehold = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).halfcollapsehousehold;
				damageInformationDto.halfCollapseHuman = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).halfcollapsehuman;
				damageInformationDto.someCollapseBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).somecollapsebuilding;
				damageInformationDto.someCollapseHousehold = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).somecollapsehousehold;
				damageInformationDto.someCollapseHuman = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).somecollapsehuman;
				damageInformationDto.overInundationBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).overinundationbuilding;
				damageInformationDto.overInundationHousehold = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).overinundationhousehold;
				damageInformationDto.overInundationHuman = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).overinundationhuman;
				damageInformationDto.underInundationBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).underinundationbuilding;
				damageInformationDto.underInundationHousehold = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).underinundationhousehold;
				damageInformationDto.underInundationHuman = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).underinundationhuman;
				damageInformationDto.publicBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).publicbuilding;
				damageInformationDto.otherBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).otherbuilding;
				damageInformationDto.ricefieldOutflowBuried = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).ricefieldoutflowburied;
				damageInformationDto.ricefieldFlood = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).ricefieldflood;
				damageInformationDto.fieldOutflowBuried = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).fieldoutflowburied;
				damageInformationDto.fieldFlood = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).fieldflood;
				damageInformationDto.educationalFacilities = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).educationalfacilities;
				damageInformationDto.hospital = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).hospital;
				damageInformationDto.road = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).road;
				damageInformationDto.bridge = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).bridge;
				damageInformationDto.river = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).river;
				damageInformationDto.port = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).port;
				damageInformationDto.sedimentControl = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).sedimentcontrol;
				damageInformationDto.cleaningFacility = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).cleaningfacility;
				damageInformationDto.cliffCollapse = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).cliffcollapse;
				damageInformationDto.railwayInterruption = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).railwayinterruption;
				damageInformationDto.ship = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).ship;
				damageInformationDto.water = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).water;
				damageInformationDto.phone = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).phone;
				damageInformationDto.electric = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).electric;
				damageInformationDto.gas = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).gas;
				damageInformationDto.blockWalls_Etc = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).blockwalls_etc;
				damageInformationDto.suffererHousehold = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).suffererhousehold;
				damageInformationDto.suffererHuman = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).suffererhuman;
				damageInformationDto.fireBuilding = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).firebuilding;
				damageInformationDto.fireDangerousGoods = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).firedangerousgoods;
				damageInformationDto.otherFire = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).otherfire;
				damageInformationDto.publicScoolFacillities = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).publicscoolfacillities;
				damageInformationDto.agricultureFacilities = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).agriculturefacilities;
				damageInformationDto.publicEngineeringFacilities = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).publicengineeringfacilities;
				damageInformationDto.subtotalDamageFacilities = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).subtotaldamagefacilities;
				damageInformationDto.farmingDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).farmingdamage;
				damageInformationDto.forestryDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).forestrydamage;
				damageInformationDto.animalDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).animaldamage;
				damageInformationDto.fisheriesDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).fisheriesdamage;
				damageInformationDto.commerceAndIndustryDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).commerceandindustrydamage;
				damageInformationDto.otherDamageOther = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).otherdamageother;
				damageInformationDto.totalDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).totaldamage;
				damageInformationDto.schoolmount = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).schoolmount;
				damageInformationDto.farmmount = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).farmmount;
				damageInformationDto.subtotalOtherDamage = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).subtotalotherdamage;
				damageInformationDto.fireman1 = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).fireman1;
				damageInformationDto.fireman2 = pReportDataList.get(0).publiccommonsReportDataLastDamageList.get(0).fireman2;

				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.DAMAGE_INFORMATION);
				return publicCommonsService.sendMediaDamageInformation(noticemailDataItems, damageInformationDto, pCommonsSendDto);
			// 取消発信 - 避難勧告・避難指示
			}else if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)) {
				refugeInformationList = new ArrayList<RefugeInformationDto>();
				for (int i = 0; i < pReportDataList.get(0).publiccommonsReportDataLastRefugeList.size(); i++){
					RefugeInformationDto dto = new RefugeInformationDto();
					dto.chikuName = pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).chikuname;
					dto.hatureiDateTime = pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).hatureidatetime;

					// 解除の場合
					if (Constants.ISSUE_CANCEL_NAME().equals(pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).issueorlift)) {
						dto.hatureiKbn = Constants.ISSUE_CANCEL_NAME();
						dto.lasthatureiKbn = pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).hatureikbn;
					} else {
						dto.hatureiKbn = pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).hatureikbn;
					}

					dto.people = pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).people;
					dto.targetHouseholds = pReportDataList.get(0).publiccommonsReportDataLastRefugeList.get(i).targethouseholds;
					refugeInformationList.add(dto);
				}
				pCommonsSendDto.refugeInformationList = refugeInformationList;
				PublicCommonsUtils.convertAllArea(pCommonsSendDto.refugeInformationList);
				return publicCommonsService.sendMediaEvacuationOrder(pCommonsSendDto);
			// 取消発信 - イベント
			}else if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)) {
				// 今回発信を優先する項目
				eventInformationDto.distributiontype = pcommonsmediaForm.distributiontype;

				// 前回発信で発信する項目
				pCommonsSendDto.area = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).area;
				eventInformationDto.content = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).content;
				eventInformationDto.disasterInformationType = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).disasterinformationtype;
				eventInformationDto.documentid = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).documentid;
				eventInformationDto.documentRevision = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).documentrevision;
				eventInformationDto.emailtitle = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).emailtitle;
				eventInformationDto.eventArea = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).area;
				eventInformationDto.eventFee = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).eventfee;
				eventInformationDto.eventFileCaption = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).filecaption;
				eventInformationDto.eventFileUri = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).fileuri;
				eventInformationDto.eventFrom = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).eventfrom;
				eventInformationDto.eventNotificationUri = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).notificationuri;
				eventInformationDto.eventTo = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).eventto;
				eventInformationDto.mediaType = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).mediatype;
				eventInformationDto.mimeType = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).mimetype;
				eventInformationDto.text = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).text;
				eventInformationDto.title = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).title;
				eventInformationDto.validDateTime = pReportDataList.get(0).publiccommonsReportDataLastEventList.get(0).validdatetime;
				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.EVENT);
				return publicCommonsService.sendMediaEventInformation(noticemailDataItems, eventInformationDto, pCommonsSendDto);
			// 取消発信 - お知らせ
			} else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)) {
				// 今回発信を優先する項目
				generalInformationDto.distributiontype = pcommonsmediaForm.distributiontype;

				// 前回発信で発信する項目
				pCommonsSendDto.area = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).area;
				generalInformationDto.division = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).division;
				generalInformationDto.area = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).area;
				generalInformationDto.text = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).text;
				generalInformationDto.title = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).title;
				generalInformationDto.fileUri = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).fileuri;
				generalInformationDto.notificationUri = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).notificationuri;
				generalInformationDto.mimeType = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).mimetype;
				generalInformationDto.mediaType = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).mediatype;
				generalInformationDto.documentid = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).documentid;
				generalInformationDto.documentRevision = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).documentrevision;
				generalInformationDto.fileCaption = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).filecaption;
				generalInformationDto.disasterInformationType = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).disasterinformationtype;
				generalInformationDto.validDateTime = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).validdatetime;
				generalInformationDto.emailtitle = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).emailtitle;
				generalInformationDto.content = pReportDataList.get(0).publiccommonsReportDataLastGeneralList.get(0).content;
				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.GENERAL_INFORMATION);
				return publicCommonsService.sendMediaGeneralInformation(noticemailDataItems, generalInformationDto, pCommonsSendDto);
			// 災害対策本部設置状況
			}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)) {
				antidisasterInformationDto = new AntidisasterInformationDto();
				antidisasterInformationDto.hatureiDateTime = pReportDataList.get(0).publiccommonsReportDataLastAntidisaster.hatureidatetime;
				antidisasterInformationDto.name = pReportDataList.get(0).publiccommonsReportDataLastAntidisaster.name;
				// 解散の場合
				if (Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(pReportDataList.get(0).publiccommonsReportDataLastAntidisaster.issueorlift)) {
					antidisasterInformationDto.antidisasterKbn = Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME();
					antidisasterInformationDto.lastAntidisasterKbn = pReportDataList.get(0).publiccommonsReportDataLastAntidisaster.antidisasterkbn;
				} else {
					antidisasterInformationDto.antidisasterKbn = pReportDataList.get(0).publiccommonsReportDataLastAntidisaster.antidisasterkbn;
				}

				List<NoticemailData> noticemailDataItems = noticemailDataService.findByNoticetypeId(NoticeType.COMMONSMADIA, PubliccommonsSendType.ANTIDISASTER_HEADQUARTER);
				return publicCommonsService.sendMediaAntidisasterInformation(noticemailDataItems, antidisasterInformationDto, pCommonsSendDto);
			}
		}
		return false;
	}

	/**
	 * @throws ParseException
	 */
	private ActionMessages setInformationList() throws ParseException {
		ActionMessages errors = new ActionMessages();

		// 都道府県フラグ
		pcommonsmediaForm.isPrefecture = PublicCommonsUtils.checkPrefecture(loginDataDto.getLocalgovInfo().city);

		// 運用種別
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(loginDataDto.getLocalgovinfoid());
		// 訓練モード中の場合、本番から訓練に自動的に変更する
		if(0<loginDataDto.getTrackdataid()) {
			pageDto.setTrackData(trackDataService.findById(loginDataDto.getTrackdataid()));
			if(pageDto.getTrackData()!=null && pageDto.getTrackData().trainingplandataid != null){
				if(pageDto.getTrackData().trainingplandataid > 0){
					// 本番になっている時のみ変更。テストの時は何もしない
					if(PubliccommonsSendStatusValue.ACTUAL.equals(publiccommonsSendToInfo.statusValues))
						publiccommonsSendToInfo.statusValues = PubliccommonsSendStatusValue.EXERCISE;
				}
			}
		}
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
				pcommonsmediaForm.statusValue = publiccommonsSendToInfo.statusValues;
			}
		} else {	// コモンズ発信先情報自体が取得できない場合
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Connection info for L-Alert is not properly set. Configuration is needed on admin window.<br />・[L-Alert receiver data] End point URL<br />・[L-Alert receiver data] User name<br />・[L-Alert receiver data] Password<br />・[L-Alert receiver data] Operation type"), false));
		}

		// 災害名称
		//if(StringUtil.isEmpty(pcommonsmediaForm.trackdataname)) pcommonsmediaForm.trackdataname = publicCommonsService.getTrackDataName(loginDataDto.getLocalgovinfoid());
		if(StringUtil.isEmpty(pcommonsmediaForm.trackdataname)) pcommonsmediaForm.trackdataname = publicCommonsService.getTrackDataNameSingle(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid());

		switch(pageDto.getMenuInfo().menutypeid){
		case 8:
			pcommonsmediaForm.sendType = "EVACUATION_ORDER";				//避難勧告・指示情報
			break;
		case 12:
			pcommonsmediaForm.sendType = "SHELTER";								//避難所情報
			break;
		case 20:
			pcommonsmediaForm.sendType = "GENERAL_INFORMATION";		//お知らせ
			break;
		case 21:
			pcommonsmediaForm.sendType = "EVENT";									//イベント情報
			break;
		case 22:
			pcommonsmediaForm.sendType = "DAMAGE_INFORMATION";			//被害情報
			break;
		case 23:
			pcommonsmediaForm.sendType = "ANTIDISASTER_HEADQUARTER";			//災害対策本部設置状況
			break;
		}
		pcommonsmediaForm.isShelter = (pageDto.getMenuInfo().menutypeid == Menutype.PUBLICCOMMONS_MEDIA_SHELTER.intValue());
		pcommonsmediaForm.isRefuge = (pageDto.getMenuInfo().menutypeid == Menutype.PUBLICCOMMONS_MEDIA_REFUGE.intValue());
		pcommonsmediaForm.isEvent = (pageDto.getMenuInfo().menutypeid == Menutype.PUBLICCOMMONS_MEDIA_EVENT.intValue());
		pcommonsmediaForm.isGeneral = (pageDto.getMenuInfo().menutypeid == Menutype.PUBLICCOMMONS_MEDIA_GENERAL_INFORMATION.intValue());
		pcommonsmediaForm.isDamage = (pageDto.getMenuInfo().menutypeid == Menutype.PUBLICCOMMONS_MEDIA_DAMAGE_INFORMATION.intValue());
		pcommonsmediaForm.isAntidisaster = (pageDto.getMenuInfo().menutypeid == Menutype.PUBLICCOMMONS_MEDIA_ANTIDISASTER_HEADQUARTER.intValue());

		// チェックディジェット付き地方公共自治体コード
		String organizationCode = "";
		if(loginDataDto.getLocalgovInfo()!=null) organizationCode = PublicCommonsUtils.getLocalGovermentCode(loginDataDto.getLocalgovInfo());

		// 班情報から取得
		GroupInfo group = groupInfoService.findById(loginDataDto.getGroupid());
		if (group != null) {
			// 発表組織
			if (StringUtil.isEmpty(pcommonsmediaForm.organizationName)) pcommonsmediaForm.organizationName = PublicCommonsUtils.getOrganizationName(loginDataDto.getLocalgovInfo());
			if (StringUtil.isEmpty(pcommonsmediaForm.organizationDomainName))pcommonsmediaForm.organizationDomainName = loginDataDto.getLocalgovInfo().domain;
			if (StringUtil.isEmpty(pcommonsmediaForm.organizationCode)) pcommonsmediaForm.organizationCode = organizationCode;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeName))pcommonsmediaForm.officeName = group.name;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeNameKana))pcommonsmediaForm.officeNameKana = group.namekana;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeLocationArea))pcommonsmediaForm.officeLocationArea = group.address;
			if (StringUtil.isEmpty(pcommonsmediaForm.phone))pcommonsmediaForm.phone = group.phone;
			if (StringUtil.isEmpty(pcommonsmediaForm.fax))pcommonsmediaForm.fax = group.fax;
			if (StringUtil.isEmpty(pcommonsmediaForm.email))pcommonsmediaForm.email = group.email;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeDomainName))pcommonsmediaForm.officeDomainName = group.domain;

			// 作成組織
			if (StringUtil.isEmpty(pcommonsmediaForm.organizationNameEditorial)) pcommonsmediaForm.organizationNameEditorial = PublicCommonsUtils.getOrganizationName(loginDataDto.getLocalgovInfo());
			if (StringUtil.isEmpty(pcommonsmediaForm.organizationDomainNameEditorial))pcommonsmediaForm.organizationDomainNameEditorial = loginDataDto.getLocalgovInfo().domain;
			if (StringUtil.isEmpty(pcommonsmediaForm.organizationCodeEditorial)) pcommonsmediaForm.organizationCodeEditorial = organizationCode;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeNameEditorial))pcommonsmediaForm.officeNameEditorial = group.name;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeNameKanaEditorial))pcommonsmediaForm.officeNameKanaEditorial = group.namekana;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeLocationAreaEditorial))pcommonsmediaForm.officeLocationAreaEditorial = group.address;
			if (StringUtil.isEmpty(pcommonsmediaForm.phoneEditorial))pcommonsmediaForm.phoneEditorial = group.phone;
			if (StringUtil.isEmpty(pcommonsmediaForm.faxEditorial))pcommonsmediaForm.faxEditorial = group.fax;
			if (StringUtil.isEmpty(pcommonsmediaForm.emailEditorial))pcommonsmediaForm.emailEditorial = group.email;
			if (StringUtil.isEmpty(pcommonsmediaForm.officeDomainNameEditorial))pcommonsmediaForm.officeDomainNameEditorial = group.domain;
		}

		// 公式発表日時
		if (StringUtil.isEmpty(pcommonsmediaForm.reporttime)) pcommonsmediaForm.reporttime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

		// 避難所
		if (PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType)) {
			// 避難所、福祉避難所、みなし避難所、緊急避難場所のリストを「公共情報コモンズ避難所情報(publiccommons_report_shelter_info)」から取得
			shelterInformationList = new ArrayList<ShelterInformationDto>();
			try{
				shelterInformationList = publicCommonsService.getShelterInformationList(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid());
			} catch (Exception e) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("L-Alert info {0} is not properly set. Configuration is needed on admin window.", lang.__("Shelter")), false));
				logger.error(loginDataDto.logInfo(), e);
			}
			session.setAttribute("SHELTER", shelterInformationList);
		// 避難勧告・避難指示
		} else if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType)){
			refugeInformationList = new ArrayList<RefugeInformationDto>();
			long mid = pageDto.getMenuInfo().id;
			List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(mid);
			MenutableInfo mtbl = null;
			if (menutableList.size() > 0) mtbl = menutableList.get(0);
			if (mtbl != null) {
				// 状態遷移型の対象テーブル名を取得
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(mtbl.tablemasterinfoid, loginDataDto.getTrackdataid());  // 29, 2

				// eコミマップのレイヤ：trackdataidは無い、状態遷移型の発信はテーブル名が取得できたらXMLデータを取得
				if (ttbl != null) {
					// 避難勧告・避難指示のリストを「メニューテーブル情報(menutable_info)」から取得
					try{
						refugeInformationList = publicCommonsService.getRefugeInformationList(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), ttbl.tablemasterinfoid);
					} catch (Exception e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("L-Alert info {0} is not properly set. Configuration is needed on admin window.", lang.__("Evacuation advisory")), false));
						logger.error(loginDataDto.logInfo(), e);
					}
					// 前回発令区分
					List<PubliccommonsReportData> publiccommonsReportDataItems = publiccommonsReportDataService.findByNoticetypeCategory(PubliccommonsSendType.EVACUATION_ORDER);
					if (publiccommonsReportDataItems.size() > 0) {
						if(!"cancelSend".equals(publiccommonsReportDataItems.get(0).distributionType)){
							int i = 0;
							for (RefugeInformationDto dto : refugeInformationList ) {
								if (StringUtil.isNotEmpty(dto.chikuName)) {

									String lastHatureiKbn = pcEvacuationService.getLastEvacuationOrderByArea(loginDataDto.getTrackdataid(), loginDataDto.getLocalgovinfoid(), dto.chikuName);
									if (StringUtil.isNotEmpty(lastHatureiKbn)) refugeInformationList.get(i).lasthatureiKbn = lastHatureiKbn;

									String issueOrLift = pcEvacuationService.getIssueOrLiftEvacuationOrderByArea(loginDataDto.getTrackdataid(), loginDataDto.getLocalgovinfoid(), dto.chikuName);
									if (StringUtil.isNotEmpty(issueOrLift)) refugeInformationList.get(i).lasthatureiKbn = issueOrLift;

									pcommonsmediaForm.lasthatureiKbnMap.put(dto.chikuName, lastHatureiKbn);
								}
								i++;
							}
						}
					}

					// 避難準備情報を避難準備に変換
					for (int i=0; i<refugeInformationList.size(); i++ ) {
						if (Constants.ISSUE_PREPARE_NAME().equals(refugeInformationList.get(i).hatureiKbn)) refugeInformationList.get(i).hatureiKbn = Constants.ISSUE_PREPARE_PCOMMONS_NAME();
					}

					session.setAttribute("REFUGE", refugeInformationList);
				}
			}
		// 被害情報
		} else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType)){
			damageInformationDto = new DamageInformationDto();
//			List<Reportcontent2Data> reportcontent2datalist = reportcontent2DataService.findByTrackDataId(loginDataDto.getTrackdataid());
//			Reportcontent2Data reportcontent2Data = reportcontent2datalist.get(0);
			List<ReportData> list = reportDataService.findByTrackdataid(loginDataDto.getTrackdataid());

			// 1件以上ある場合
			if (list.size() > 0) {
				ReportData reportData = list.get(0);
				// 報告数
				damageInformationDto.reportno = toString(reportData.reportcontent2Datas.get(0).reportno);
				// 被災状況人的被害データ
				damageInformationDto.deadPeople = toString(reportData.reportcontent2Datas.get(0).casualties21);
				damageInformationDto.missingPeople = toString(reportData.reportcontent2Datas.get(0).casualties22);
				damageInformationDto.seriouslyInjuredPeople = toString(reportData.reportcontent2Datas.get(0).casualties23);
				damageInformationDto.slightlyInjuredPeople = toString(reportData.reportcontent2Datas.get(0).casualties24);
				// 被災状況住家被害 (棟) データ
				damageInformationDto.totalCollapseBuilding = toString(reportData.reportcontent2Datas.get(0).houseall1);
				damageInformationDto.halfCollapseBuilding = toString(reportData.reportcontent2Datas.get(0).househalf1);
				damageInformationDto.someCollapseBuilding = toString(reportData.reportcontent2Datas.get(0).housepart1);
				damageInformationDto.overInundationBuilding = toString(reportData.reportcontent2Datas.get(0).houseupper1);
				damageInformationDto.underInundationBuilding = toString(reportData.reportcontent2Datas.get(0).houselower1);
				// 被災状況住家被害（世帯）データ
				damageInformationDto.totalCollapseHousehold = toString(reportData.reportcontent2Datas.get(0).houseall2);
				damageInformationDto.halfCollapseHousehold = toString(reportData.reportcontent2Datas.get(0).househalf2);
				damageInformationDto.someCollapseHousehold = toString(reportData.reportcontent2Datas.get(0).housepart2);
				damageInformationDto.overInundationHousehold = toString(reportData.reportcontent2Datas.get(0).houseupper2);
				damageInformationDto.underInundationHousehold = toString(reportData.reportcontent2Datas.get(0).houselower2);
				// 被災状況住家被害（人）データ
				damageInformationDto.totalCollapseHuman = toString(reportData.reportcontent2Datas.get(0).houseall3);
				damageInformationDto.halfCollapseHuman = toString(reportData.reportcontent2Datas.get(0).househalf3);
				damageInformationDto.someCollapseHuman = toString(reportData.reportcontent2Datas.get(0).housepart3);
				damageInformationDto.overInundationHuman = toString(reportData.reportcontent2Datas.get(0).houseupper3);
				damageInformationDto.underInundationHuman = toString(reportData.reportcontent2Datas.get(0).houselower3);
				//被災状況非住家被害
				damageInformationDto.publicBuilding = toString(reportData.reportcontent2Datas.get(0).build1);
				damageInformationDto.otherBuilding = toString(reportData.reportcontent2Datas.get(0).build2);
				// 被災状況農林被害
				damageInformationDto.ricefieldOutflowBuried = reportData.reportcontent2Datas.get(0).field1 == null ? "" : toString(reportData.reportcontent2Datas.get(0).field1.intValue());
				damageInformationDto.ricefieldFlood = reportData.reportcontent2Datas.get(0).field2 == null ? "" : toString(reportData.reportcontent2Datas.get(0).field2.intValue());
				damageInformationDto.fieldOutflowBuried = reportData.reportcontent2Datas.get(0).farm1 == null ? "" : toString(reportData.reportcontent2Datas.get(0).farm1.intValue());
				damageInformationDto.fieldFlood = reportData.reportcontent2Datas.get(0).farm2 == null ? "" : toString(reportData.reportcontent2Datas.get(0).farm2.intValue());
				damageInformationDto.farmmount = toString(reportData.reportcontent2Datas.get(0).amount5);
				// 被災状況文教被害
				damageInformationDto.educationalFacilities = toString(reportData.reportcontent2Datas.get(0).school);
				damageInformationDto.schoolmount = toString(reportData.reportcontent2Datas.get(0).amount1);
				// 被災状況病院被害
				damageInformationDto.hospital = toString(reportData.reportcontent2Datas.get(0).hospital);
				// 被災状況土木被害
				damageInformationDto.road = toString(reportData.reportcontent2Datas.get(0).road);
				damageInformationDto.bridge = toString(reportData.reportcontent2Datas.get(0).bridge);
				damageInformationDto.river = toString(reportData.reportcontent2Datas.get(0).river);
				damageInformationDto.port = toString(reportData.reportcontent2Datas.get(0).harbor);
				damageInformationDto.sedimentControl = toString(reportData.reportcontent2Datas.get(0).landslide);
				damageInformationDto.cliffCollapse = toString(reportData.reportcontent2Datas.get(0).cliff);
				damageInformationDto.railwayInterruption = toString(reportData.reportcontent2Datas.get(0).railway);
				damageInformationDto.ship = toString(reportData.reportcontent2Datas.get(0).ship);
				damageInformationDto.water = toString(reportData.reportcontent2Datas.get(0).water);
				// 被災状況ライフライン被害
				damageInformationDto.phone = toString(reportData.reportcontent2Datas.get(0).telephone);
				damageInformationDto.electric = toString(reportData.reportcontent2Datas.get(0).electricity);
				damageInformationDto.gas = toString(reportData.reportcontent2Datas.get(0).gas);
				// 被災状況民政被害
				damageInformationDto.blockWalls_Etc = toString(reportData.reportcontent2Datas.get(0).block);
				damageInformationDto.cleaningFacility = toString(reportData.reportcontent2Datas.get(0).gabage);
				// り災
				damageInformationDto.suffererHousehold  = toString(reportData.reportcontent2Datas.get(0).suffer1);
				damageInformationDto.suffererHuman  = toString(reportData.reportcontent2Datas.get(0).suffer2);
				// 火災
				damageInformationDto.fireBuilding  = toString(reportData.reportcontent2Datas.get(0).fire1);
				damageInformationDto.fireDangerousGoods  = toString(reportData.reportcontent2Datas.get(0).fire2);
				damageInformationDto.otherFire  = toString(reportData.reportcontent2Datas.get(0).fire3);
				// 文教施設
				damageInformationDto.publicScoolFacillities  = toString(reportData.reportcontent2Datas.get(0).amount1);
				damageInformationDto.agricultureFacilities  = toString(reportData.reportcontent2Datas.get(0).amount2);
				damageInformationDto.publicEngineeringFacilities  = toString(reportData.reportcontent2Datas.get(0).amount3);
				damageInformationDto.subtotalDamageFacilities  = toString(reportData.reportcontent2Datas.get(0).subtotal);
				damageInformationDto.farmingDamage  = toString(reportData.reportcontent2Datas.get(0).amount5);
				damageInformationDto.forestryDamage  = toString(reportData.reportcontent2Datas.get(0).amount6);
				damageInformationDto.animalDamage  = toString(reportData.reportcontent2Datas.get(0).amount7);
				damageInformationDto.fisheriesDamage  = toString(reportData.reportcontent2Datas.get(0).amount8);
				damageInformationDto.commerceAndIndustryDamage  = toString(reportData.reportcontent2Datas.get(0).amount9);
				damageInformationDto.otherDamageOther  = toString(reportData.reportcontent2Datas.get(0).amount10);
				damageInformationDto.subtotalOtherDamage  = toString(reportData.reportcontent2Datas.get(0).amount4);
				damageInformationDto.totalDamage  = toString(reportData.reportcontent2Datas.get(0).atotal);
				// 消防
				damageInformationDto.fireman1 = toString(reportData.reportcontent2Datas.get(0).fireman1);
				damageInformationDto.fireman2 = toString(reportData.reportcontent2Datas.get(0).fireman2);
				// 備考(補足情報に使用)
				damageInformationDto.remarks = reportData.reportcontent2Datas.get(0).note2;

				session.setAttribute("DAMAGE_INFORMATION", damageInformationDto);
			}
		// イベント
		} else if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType)){
			// 過去のイベント取得
			eventInformationList = publicCommonsService.getEventInformationList(loginDataDto.getLocalgovinfoid());
			HashMap<String,EventInformationDto> map = new HashMap<String,EventInformationDto>();
			// 同じドキュメントIDの最新の投稿だけ取得
			for (EventInformationDto eventInformation : eventInformationList) {
				map.put(eventInformation.documentid, eventInformation);
			}
			eventInformationList = new ArrayList<EventInformationDto>();
			for (Entry<String, EventInformationDto> key : map.entrySet()) {
				if (key.getValue().validDateTime != null && publicCommonsService.checkValidDateTime(key.getValue().validDateTime) && !"cancelSend".equals(key.getValue().distributiontype)){
					eventInformationList.add(key.getValue());
				}
			}

			// ドロップダウンメニュークリック時
			if(!"".equals(pcommonsmediaForm.documentId) && !"0".equals(pcommonsmediaForm.documentId) && !pcommonsmediaForm.validationCheckflg){
				pcommonsmediaForm.area = map.get(pcommonsmediaForm.documentId).eventArea;
				pcommonsmediaForm.title = map.get(pcommonsmediaForm.documentId).title;
				pcommonsmediaForm.text = map.get(pcommonsmediaForm.documentId).text;
				pcommonsmediaForm.notificationUri = map.get(pcommonsmediaForm.documentId).eventNotificationUri;
				pcommonsmediaForm.fileUri = map.get(pcommonsmediaForm.documentId).eventFileUri;
				pcommonsmediaForm.fileCaption = map.get(pcommonsmediaForm.documentId).eventFileCaption;
				pcommonsmediaForm.eventFrom = map.get(pcommonsmediaForm.documentId).eventFrom;
				pcommonsmediaForm.eventTo = map.get(pcommonsmediaForm.documentId).eventTo;
				pcommonsmediaForm.eventFee = map.get(pcommonsmediaForm.documentId).eventFee;
				pcommonsmediaForm.fileCaption = map.get(pcommonsmediaForm.documentId).eventFileCaption;
				pcommonsmediaForm.emailTitle = map.get(pcommonsmediaForm.documentId).emailtitle;
				pcommonsmediaForm.documentRevision = map.get(pcommonsmediaForm.documentId).documentRevision.toString();
			}
		// お知らせ
		}else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType)){
			// 過去のお知らせ取得
			generalInformationList = publicCommonsService.getGeneralInformationList(loginDataDto.getLocalgovinfoid());
			HashMap<String,GeneralInformationDto> map = new HashMap<String,GeneralInformationDto>();
			// 同じドキュメントIDの最新の投稿だけ取得
			for (GeneralInformationDto generalInformation : generalInformationList) {
				map.put(generalInformation.documentid, generalInformation);
			}
			generalInformationList = new ArrayList<GeneralInformationDto>();
			for (Entry<String, GeneralInformationDto> key : map.entrySet()) {
				if (key.getValue().validDateTime != null && publicCommonsService.checkValidDateTime(key.getValue().validDateTime) && !"cancelSend".equals(key.getValue().distributiontype)){
					generalInformationList.add(key.getValue());
				}
			}

			// ドロップダウンメニュークリック時
			if(!"".equals(pcommonsmediaForm.documentId) && !"0".equals(pcommonsmediaForm.documentId) && !pcommonsmediaForm.validationCheckflg){
				pcommonsmediaForm.disasterInformationType = map.get(pcommonsmediaForm.documentId).disasterInformationType;
				pcommonsmediaForm.division = map.get(pcommonsmediaForm.documentId).division;
				pcommonsmediaForm.area = map.get(pcommonsmediaForm.documentId).area;
				pcommonsmediaForm.title = map.get(pcommonsmediaForm.documentId).title;
				pcommonsmediaForm.text = map.get(pcommonsmediaForm.documentId).text;
				pcommonsmediaForm.notificationUri = map.get(pcommonsmediaForm.documentId).notificationUri;
				pcommonsmediaForm.fileUri = map.get(pcommonsmediaForm.documentId).fileUri;
				pcommonsmediaForm.fileCaption = map.get(pcommonsmediaForm.documentId).fileCaption;
				pcommonsmediaForm.emailTitle = map.get(pcommonsmediaForm.documentId).emailtitle;
				pcommonsmediaForm.documentRevision = map.get(pcommonsmediaForm.documentId).documentRevision.toString();
			}
		// 災害対策本部設置状況
		}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType)){
			antidisasterInformationDto = new AntidisasterInformationDto();

			// 災害対策本部設置状況を取得
			antidisasterInformationDto = pcAntidisasterService.getAntidisasterHeadquarter();

			if (antidisasterInformationDto != null) {
				// 本部名称
				if (StringUtil.isEmpty(pcommonsmediaForm.name)) pcommonsmediaForm.name = antidisasterInformationDto.name;

				// 前回発信本部種別
				List<PubliccommonsReportData> publiccommonsReportDataItems = publiccommonsReportDataService.findByNoticetypeCategory(PubliccommonsSendType.ANTIDISASTER_HEADQUARTER);
				if (publiccommonsReportDataItems.size() > 0) {
					if(!"cancelSend".equals(publiccommonsReportDataItems.get(0).distributionType)){
						String lastStation = pcAntidisasterService.getLastAntidisasterHeadquarter(loginDataDto.getTrackdataid(), loginDataDto.getLocalgovinfoid());
						if (StringUtil.isNotEmpty(lastStation)) antidisasterInformationDto.lastAntidisasterKbn = lastStation;
					}
				}
			} else {
				// NullPointerException対策
				antidisasterInformationDto = new AntidisasterInformationDto();
			}

			session.setAttribute("ANTIDISASTER_HEADQUARTER", antidisasterInformationDto);
		}
		return errors;
	}

	/**
	 * 確認用Eメール本文作成
	 * @return 本文
	 */
	private String createContent() {
		StringBuffer content = new StringBuffer();

		final String CR = "\n";
		final String COMMA = ", ";
		final String NO_TIME = "---";
		final String CONTENT = lang.__("Following info is sent to the L-Alert.");

		content.append(CONTENT + CR);

		// 避難所
		if (PubliccommonsSendType.SHELTER.equals(pcommonsmediaForm.sendType)) {
			for (ShelterInformationDto cur : shelterInformationList) {
				// 開設・閉鎖日時
				String setupTime;
				String closeTime;
				try {
					setupTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(cur.setupTime);
				} catch (Exception e) {
					setupTime = NO_TIME;
				}
				try {
					closeTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(cur.closeTime);
				} catch (Exception e) {
					closeTime = NO_TIME;
				}

				// format 【名称】○○公民館, 【状況】開設, 【開設日時】2013/9/17 12:30, 【閉鎖日時】---
				content.append(lang.__("[Name]") + cur.shelterName + COMMA);
				content.append(lang.__("[Shelter status]") + cur.shelterStatus + COMMA);
				content.append(lang.__("[Opening date and time]") + (StringUtil.isBlank(setupTime) ? NO_TIME : setupTime) + COMMA);
				content.append(lang.__("[Closing date and time]") + (StringUtil.isBlank(closeTime) ? NO_TIME : closeTime) + CR);
			}
			content.append(lang.__("[Supplementary info]") + pcommonsmediaForm.complementaryInfo + CR);

		// 避難勧告・避難指示
		} else if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonsmediaForm.sendType))  {

			//発令が解除された地区に、画面で選択された前回発令区分をセット
		    for (int i = 0 ; i < refugeInformationList.size() ; i++){
		    	if(refugeInformationList.get(i).lasthatureiKbn==null || Constants.ISSUE_CANCEL_NAME().equals(refugeInformationList.get(i).hatureiKbn)){
		    		refugeInformationList.get(i).lasthatureiKbn = pcommonsmediaForm.lasthatureiKbnMap.get(refugeInformationList.get(i).chikuName);
		    	}
		    }

			for (RefugeInformationDto cur : refugeInformationList) {
				// format 【名称】○○地区, 【発令区分】避難指示, 【対象世帯数】3, 【人数】12, 【発令時刻】2013/9/17 12:30
				content.append(lang.__("[Name]") + cur.chikuName + COMMA);

				//発令区分が解除の場合のみ、前回発令区分も含める
				if (Constants.ISSUE_CANCEL_NAME().equals(cur.hatureiKbn)){
					content.append(lang.__("[Announcement type]") + cur.lasthatureiKbn + cur.hatureiKbn + COMMA);
				} else {
					content.append(lang.__("[Announcement type]") + cur.hatureiKbn + COMMA);
				}

				content.append(lang.__("[Number of target households]") + cur.targetHouseholds + COMMA);
				content.append(lang.__("[Number of people]") + cur.people + COMMA);
				// 発令日時
				String hatureiDateTime;
				try {
					hatureiDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(cur.hatureiDateTime);
				} catch (Exception e) {
					hatureiDateTime = NO_TIME;
				}
				content.append(lang.__("[Announcement date and time]") + (StringUtil.isBlank(hatureiDateTime) ? NO_TIME : hatureiDateTime) + CR);
			}
			content.append(lang.__("[Supplementary info]") + pcommonsmediaForm.complementaryInfo + CR);

		// イベント情報
		} else if (PubliccommonsSendType.EVENT.equals(pcommonsmediaForm.sendType))  {
			content.append(lang.__("[Title of event content]") + pcommonsmediaForm.title + COMMA);
			content.append(lang.__("[Text of event content]") + pcommonsmediaForm.text + COMMA);
			content.append(lang.__("[Venue]") + pcommonsmediaForm.area + COMMA);
			content.append(lang.__("[Start date]") + pcommonsmediaForm.eventFrom + COMMA);
			content.append(lang.__("[End date]") + pcommonsmediaForm.eventTo + COMMA);
			content.append(lang.__("[Participation cost]") + pcommonsmediaForm.eventFee + COMMA);
			content.append(lang.__("[Announcement URI]") + pcommonsmediaForm.notificationUri + COMMA);
			content.append(lang.__("[File URI]") + pcommonsmediaForm.fileUri + COMMA);
			content.append(lang.__("[File title]") + pcommonsmediaForm.fileCaption + COMMA);

		// 被害情報
//		} else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType))  { // 2017.07.28 kawada 条件追加
		} else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonsmediaForm.sendType) && damageInformationDto!= null)  {
			// format 【名称】○○地区, 【発令区分】避難指示, 【対象世帯数】3, 【人数】12, 【発令時刻】2013/9/17 12:30
			content.append(lang.__("[Deaths]") + damageInformationDto.deadPeople + COMMA);
			content.append(lang.__("[Missing person]") + damageInformationDto.missingPeople + COMMA);
			content.append(lang.__("[Seriously injured]") + damageInformationDto.seriouslyInjuredPeople + COMMA);
			content.append(lang.__("[Slightly injured]") + damageInformationDto.slightlyInjuredPeople + COMMA);
			content.append(lang.__("[Fully destroyed (buildings) ]") + damageInformationDto.totalCollapseBuilding + COMMA);
			content.append(lang.__("[Partially destroyed (buildings) ]") + damageInformationDto.halfCollapseBuilding + COMMA);
			content.append(lang.__("[Partial damage (buildings) ]") + damageInformationDto.someCollapseBuilding + COMMA);
			content.append(lang.__("[Inundation above floor (buildings)]") + damageInformationDto.overInundationBuilding + COMMA);
			content.append(lang.__("[Inundation under floor (buildings)]") + damageInformationDto.underInundationBuilding + COMMA);
			content.append(lang.__("[Fully destroyed (households) ]") + damageInformationDto.totalCollapseHousehold + COMMA);
			content.append(lang.__("[Partially destroyed (households) ]") + damageInformationDto.halfCollapseHousehold + COMMA);
			content.append(lang.__("[Partial damage (households) ]") + damageInformationDto.someCollapseHousehold + COMMA);
			content.append(lang.__("[Inundation above floor (households)]") + damageInformationDto.overInundationHousehold + COMMA);
			content.append(lang.__("[Inundation under floor (households)]") + damageInformationDto.underInundationHousehold + COMMA);
			content.append(lang.__("[Fully destroyed (people) ]") + damageInformationDto.totalCollapseHuman + COMMA);
			content.append(lang.__("[Partially destroyed (people) ]") + damageInformationDto.halfCollapseHuman + COMMA);
			content.append(lang.__("[Partial damage (people) ]") + damageInformationDto.someCollapseHuman + COMMA);
			content.append(lang.__("[Inundation above floor (people)]") + damageInformationDto.overInundationHuman + COMMA);
			content.append(lang.__("[Inundation under floor (people)]") + damageInformationDto.underInundationHuman + COMMA);
			content.append(lang.__("[Public facility]") + damageInformationDto.publicBuilding + COMMA);
			content.append(lang.__("[Others]") + damageInformationDto.otherBuilding + COMMA);
			content.append(lang.__("[Outflow, burying rice field]") + damageInformationDto.ricefieldOutflowBuried + COMMA);
			content.append(lang.__("[Flooded rice field]") + damageInformationDto.ricefieldFlood + COMMA);
			content.append(lang.__("[Outflow, burying farm]") + damageInformationDto.fieldOutflowBuried + COMMA);
			content.append(lang.__("[Flooded farm]") + damageInformationDto.fieldFlood + COMMA);
			content.append(lang.__("[Educational facilities]") + damageInformationDto.educationalFacilities + COMMA);
			content.append(lang.__("[Hospital]") + damageInformationDto.hospital + COMMA);
			content.append(lang.__("[Road]") + damageInformationDto.road + COMMA);
			content.append(lang.__("[Bridge]") + damageInformationDto.bridge + COMMA);
			content.append(lang.__("[River]") + damageInformationDto.river + COMMA);
			content.append(lang.__("[Port]") + damageInformationDto.port + COMMA);
			content.append(lang.__("[Erosion control]") + damageInformationDto.sedimentControl + COMMA);
			content.append(lang.__("[Cleaning facilities]") + damageInformationDto.cleaningFacility + COMMA);
			content.append(lang.__("[Landslide]") + damageInformationDto.cliffCollapse + COMMA);
			content.append(lang.__("[Train service interruptions]") + damageInformationDto.railwayInterruption + COMMA);
			content.append(lang.__("[Damaged ship]") + damageInformationDto.ship + COMMA);
			content.append(lang.__("[Water supply]") + damageInformationDto.water + COMMA);
			content.append(lang.__("[Telephone]") + damageInformationDto.phone + COMMA);
			content.append(lang.__("[Electricity]") + damageInformationDto.electric + COMMA);
			content.append(lang.__("[Gas]") + damageInformationDto.gas + COMMA);
			content.append(lang.__("[Block wall, etc.]") + damageInformationDto.blockWalls_Etc + COMMA);
			content.append(lang.__("[Number of afflicted households]") + damageInformationDto.suffererHousehold + COMMA);
			content.append(lang.__("[Number of victims]") + damageInformationDto.suffererHuman + COMMA);
			content.append(lang.__("[Building]") + damageInformationDto.fireBuilding + COMMA);
			content.append(lang.__("[Hazardous material]") + damageInformationDto.fireDangerousGoods + COMMA);
			content.append(lang.__("[Others]") + damageInformationDto.otherFire + COMMA);
			content.append(lang.__("[Public educational facilities]") + damageInformationDto.publicScoolFacillities + COMMA);
			content.append(lang.__("[Agriculture, forestry and fisheries facilities]") + damageInformationDto.agricultureFacilities + COMMA);
			content.append(lang.__("[Public works facilities ] ") + damageInformationDto.publicEngineeringFacilities + COMMA);
			content.append(lang.__("[Subtotal of other cost of facility damage]") + damageInformationDto.subtotalDamageFacilities + COMMA);
			content.append(lang.__("[Agricultural damage]") + damageInformationDto.farmingDamage + COMMA);
			content.append(lang.__("[Forestry damage]") + damageInformationDto.forestryDamage + COMMA);
			content.append(lang.__("[Livestock damage]") + damageInformationDto.animalDamage + COMMA);
			content.append(lang.__("[Fisheries damage]") + damageInformationDto.fisheriesDamage + COMMA);
			content.append(lang.__("[Commerce and industry damage]") + damageInformationDto.commerceAndIndustryDamage + COMMA);
			content.append(lang.__("[Others]") + damageInformationDto.otherDamageOther + COMMA);
			content.append(lang.__("[Subtotal of other cost of damage]") + damageInformationDto.subtotalOtherDamage + COMMA);
			content.append(lang.__("[Total amount of damage]") + damageInformationDto.totalDamage + COMMA);
			content.append(lang.__("[Total number of dispatched firefighters]") + damageInformationDto.fireman1 + COMMA);
			content.append(lang.__("[Total number of dispatched volunteer firefighters]") + damageInformationDto.fireman2 + COMMA);
			content.append(lang.__("[Supplementary info]") + pcommonsmediaForm.complementaryInfo + CR);

		// お知らせ
		} else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonsmediaForm.sendType))  {
			content.append(lang.__("[Info identification type]") + pcommonsmediaForm.disasterInformationType + COMMA);
			content.append(lang.__("[Classification]") + pcommonsmediaForm.division + COMMA);
			content.append(lang.__("[Title]") + pcommonsmediaForm.title + COMMA);
			content.append(lang.__("[Text]") + pcommonsmediaForm.text + COMMA);
			content.append(lang.__("[Place]") + pcommonsmediaForm.area + COMMA);
			content.append(lang.__("[Announcement URI]") + pcommonsmediaForm.notificationUri + COMMA);
			content.append(lang.__("[File URI]") + pcommonsmediaForm.fileUri + COMMA);
			content.append(lang.__("[File title]") + pcommonsmediaForm.fileCaption + COMMA);
		} else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonsmediaForm.sendType))  {
			// 本部種別
			if (Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(antidisasterInformationDto.antidisasterKbn)) {
				content.append(lang.__("[HQ type]") + antidisasterInformationDto.lastAntidisasterKbn + antidisasterInformationDto.antidisasterKbn + COMMA);
			} else {
				content.append(lang.__("[HQ type]") + antidisasterInformationDto.antidisasterKbn + COMMA);
			}

			// 設置・解散日時
			String hatureiDateTime;
			try {
				hatureiDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(antidisasterInformationDto.hatureiDateTime);
			} catch (Exception e) {
				hatureiDateTime = NO_TIME;
			}
			content.append(lang.__("[Installation and dissolution date and time]") + hatureiDateTime + COMMA);
			content.append(lang.__("[HQ name]") + pcommonsmediaForm.name);
		}

		// 訂正発信、取消発信の時のみ、訂正・取消理由を含める
		if(!"normalSend".equals(pcommonsmediaForm.distributiontype)){
			content.append(lang.__("[Correction or Revocation reason]") + pcommonsmediaForm.description);
		}
		return content.toString();
	}

	/**
	 * セッション情報削除
	 */
	private void removeSessionAttr() {
		session.removeAttribute(SESSION_ATTR_REFUGE);
		session.removeAttribute(SESSION_ATTR_SHELTER);
		session.removeAttribute(SESSION_ATTR_EVENT);
		session.removeAttribute(SESSION_ATTR_DAMAGE_INFORMATION);
		session.removeAttribute(SESSION_ATTR_GENERAL_INFORMATION);
	}

	/**
	 * 数値がnullの場合は""、その他の場合は数値を文字列に変換して返す。
	 * @param i 数値
	 * @return 数値文字列
	 */
	public static String toString(Integer i) {
		if (i == null) return "";
		return i.toString();
	}
}
