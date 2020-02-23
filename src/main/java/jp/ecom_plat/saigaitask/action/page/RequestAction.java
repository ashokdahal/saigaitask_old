/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.entity.db.AlarmdefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.form.page.RequestForm;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.AlarmdefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;

/**
 * 要請ページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class RequestAction extends PageBaseAction {

	/** アクションフォーム */
	protected RequestForm requestForm;

	/** サービス */
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;
	@Resource
	protected AlarmdefaultgroupInfoService alarmdefaultgroupInfoService;
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected FilteredFeatureService filteredFeatureService;

	/** 通知先リスト */
	public List<NoticegroupInfo> noticegroupInfoItems;
	/** 項目名リスト */
	public List<TablelistcolumnInfo> colinfoItems;
	/** 班リスト */
	public List<GroupInfo> groupInfoItems;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("colinfoItems", colinfoItems);
		model.put("groupInfoItems", groupInfoItems);
		model.put("requestForm", requestForm);
	}

	/**
	 * 要請ページを表示する（メール配信画面）.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/request","/page/request/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute RequestForm requestForm) {
		this.requestForm = requestForm;
		initPage("request", requestForm);

		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
		requestForm.trackdataid = Long.toString(loginDataDto.getTrackdataid());
		requestForm.menuid = pageDto.getMenuInfo().id;

		//インポートデータ
		List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		MenutableInfo mtbl = null;
		if (menutableList.size() > 0) {//TODO:当面は一つだけ
			mtbl = menutableList.get(0);
		}

		StringBuffer content = null;
		if (0<pageDto.getMenuInfo().filterInfoList.size())
			content = filteredFeatureService.getFilteredFeatureContext(loginDataDto.getTrackdataid(), pageDto.getMenuInfo(), mtbl);
		else
			content = filteredFeatureService.getAllFeatureContext(loginDataDto.getTrackdataid(), mtbl);
		content.append(loginDataDto.getPreMenuUrl());
		content.append("\n");
		requestForm.mailcontent = content.toString();

		//デフォルト設定
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(pageDto.getMenuInfo().id);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist)
			requestForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());
		List<AlarmdefaultgroupInfo> alarmgrplist = alarmdefaultgroupInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		for (AlarmdefaultgroupInfo ginfo : alarmgrplist)
			requestForm.checkedAlertList.add(ginfo.groupid.toString());

		//トークンを設定
		//TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);
		// 入力画面
		return "/page/request/index";
	}

	/**
	 * メール送信処理
	 * @return 次のページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/request/send", produces="application/json")
	public ResponseEntity<String> send(Map<String,Object>model,
			@Valid @ModelAttribute RequestForm requestForm, BindingResult bindingResult) {
		this.requestForm = requestForm;

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		//トークンを判定
		/*if (!TokenProcessor.getInstance().isTokenValid(SpringContext.getRequest(), true)) {
			//不正なトークンの場合、エラー画面を表示
			ResponseUtil.write("double");
			return null;
		}*/

		//ActionMessages errors = new ActionMessages();

		// 件名(必須チェック)
		if (StringUtil.isEmpty(requestForm.mailtitle)) {
			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "件名"));
			return new ResponseEntity<String>("notitle", httpHeaders, HttpStatus.OK);
		}
        // 本文(必須チェック)
		if (StringUtil.isEmpty(requestForm.mailcontent)) {
			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "本文"));
			return new ResponseEntity<String>("nocontent", httpHeaders, HttpStatus.OK);
		}
		if (!checkEmail(requestForm.additionalReceiver)) {
			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.invalid", "追加送付先"));
			//ActionMessagesUtil.addErrors(request, errors);
			return new ResponseEntity<String>("errormail", httpHeaders, HttpStatus.OK);
		}
		if (requestForm.noticegroupinfoid != null && requestForm.noticegroupinfoid.size() == 0) {
			return new ResponseEntity<String>("nosend", httpHeaders, HttpStatus.OK);
		}
		if (requestForm.enablePopup && requestForm.checkedAlertList.size() == 0) {
			return new ResponseEntity<String>("nopopup", httpHeaders, HttpStatus.OK);
		}


		//実行時刻
		Timestamp now = new Timestamp(System.currentTimeMillis());

		//送信
		List<Long> grpids = new ArrayList<Long>();
		String mailto = "";
		boolean bsend = false;
		if (requestForm.noticegroupinfoid != null) {
			for (String grpid : requestForm.noticegroupinfoid)
				grpids.add(Long.parseLong(grpid));
			Object[] ret = noticegroupInfoService.sendMailToNoticeGroups(loginDataDto.getLocalgovinfoid(), grpids, requestForm.additionalReceiver, requestForm.mailtitle, requestForm.mailcontent);
			bsend = (Boolean)ret[0];
			mailto = (String)ret[1];
		}
		else
			bsend = true;

		//アラート
		List<AlarmdefaultgroupInfo> alarmgrplist = alarmdefaultgroupInfoService.findByMenuInfoId(requestForm.menuid);
		if (requestForm.enablePopup && requestForm.checkedAlertList != null) {
			for (String gid : requestForm.checkedAlertList) {
				GroupInfo ginfo = groupInfoService.findById(Long.parseLong(gid));
				AlarmmessageData alarm = new AlarmmessageData();
				alarm.localgovinfoid = loginDataDto.getLocalgovinfoid();
				alarm.duration = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String time = sdf.format(now);
				alarm.message = lang.__("[{0}]", time)+ requestForm.mailcontent;
				alarm.messagetype = "information";
				for (AlarmdefaultgroupInfo alrtmgr : alarmgrplist) {
					if (alrtmgr.groupid.toString().equals(gid)) {
						String messagetype = AlarmmessageDataService.messageTypes.get(alrtmgr.messagetype);
						if (messagetype == null && "white".equals(alrtmgr.messagetype))	/* messageTypes の誤りを救済 */
							messagetype = "alert";
						if (messagetype != null && !messagetype.isEmpty()) {
							alarm.messagetype = messagetype;
							break;
						}
					}
				}
				alarm.showmessage = true;
				alarm.groupid = ginfo.id;
				alarm.registtime = new Timestamp(System.currentTimeMillis());
				alarm.trackdataid = loginDataDto.getTrackdataid();
				alarm.sendgroupid = loginDataDto.getGroupid();
				alarm.noticeto = "";
				for (Long grpid : grpids) {
					NoticegroupInfo noticegroupInfo = noticegroupInfoService.findById(grpid);
					alarm.noticeto += noticegroupInfo.name+" ";
				}
				alarm.noticeto = alarm.noticeto.trim();
				alarm.noticeurl = loginDataDto.getPreMenuUrl();
				alarm.deleted = false;

				alarmmessageDataService.insert(alarm);
			}
		}

		//通知データ（メール配信）を履歴に登録
		if (!StringUtil.isEmpty(mailto)) {
			NoticemailData entity = new NoticemailData();
			//entity.trackdataid = loginDataDto.getTrackdataid();
			entity.mailto = mailto;
			if (StringUtil.isNotEmpty(requestForm.additionalReceiver))
				entity.mailto += ","+requestForm.additionalReceiver;
			entity.title = requestForm.mailtitle;
			entity.content = requestForm.mailcontent;
			entity.sendtime = now;
			entity.send = bsend;

			// 要請メールの通知種別はメール
			entity.noticetypeid = NoticeType.MAIL;

			// 災害が立っている時のみ災害名をセット
			TrackData trackData = null;
			if(0<loginDataDto.getTrackdataid()) {
				trackData = trackDataService.findById(loginDataDto.getTrackdataid());  // 記録データを取得
			}
			if (trackData != null ) {
				entity.trackdataid = Long.valueOf(trackData.id.toString());
				entity.trackdataname = trackData.name;
			} else {
				entity.trackdataid = Long.valueOf(0);
				entity.trackdataname = "";
			}

			noticemailDataService.insert(entity);

			for (Long grpid : grpids) {
				//通知先データ（メールグループ）を履歴に登録
				NoticemailsendData entity2 = new NoticemailsendData();
				entity2.noticemaildataid = entity.id;
				entity2.noticegroupinfoid = grpid;
				noticemailsendDataService.insert(entity2);
			}
		}
/*
		try {
			HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
			httpServletResponse.setContentType("application/json");
			PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());

			if (bsend)
				sendPoint.println("{\"result\":\"OK\"}");
			else
				sendPoint.println("{\"result\":\"ERROR\"}");
			sendPoint.flush();
			sendPoint.close();*/
			if (bsend){
				return new ResponseEntity<String>("{\"result\":\"OK\"}", httpHeaders, HttpStatus.OK);
			}else{
				return new ResponseEntity<String>("{\"result\":\"ERROR\"}", httpHeaders, HttpStatus.OK);
			}

		/*} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}*/

		//ResponseUtil.write("success");

		//return null;
	}
}
