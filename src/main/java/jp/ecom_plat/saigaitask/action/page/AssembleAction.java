/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.constant.SafetyState;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupuserInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.form.page.AssembleForm;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AssembleInfoService;
import jp.ecom_plat.saigaitask.service.db.AssemblestateDataService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupuserInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import jp.ecom_plat.saigaitask.util.MailUtil;
import net.sf.json.JSONObject;

/**
 * 職員参集のアクションクラス
 * spring checked take 4/30
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class AssembleAction extends PageBaseAction {

	protected AssembleForm assembleForm;

	/** サービス */
	@Resource
	protected AssembleInfoService assembleInfoService;
	@Resource
	protected AssemblestateDataService assemblestateDataService;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	@Resource
	protected NoticegroupuserInfoService noticegroupuserInfoService;
	@Resource
	protected UserInfoService userInfoService;
	@Resource
	protected UnitInfoService unitInfoService;
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("assembleForm", assembleForm);
	}

	/**
	 * 職員参集ページを表示する（メール配信画面）.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/assemble","/page/assemble/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute AssembleForm assembleForm) {
		this.assembleForm = assembleForm;
		initPage("assemble", assembleForm);

		initAssembleForm();

		//トークンを設定
		//TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

    	setupModel(model);

		// 入力画面
		return "page/assemble/index";
	}

	/**
	 * フォームの値を初期化
	 */
	public void initAssembleForm() {
		//記録データを取得
		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());

		//記録IDを取得
		if (trackData != null) {
			// 体制レイヤ情報を取得
			StationlayerInfo stationlayerInfo = stationService.getLoginStationlayerInfo();
			if(stationlayerInfo==null) {
				assembleForm.message = lang.__("Set system layer.");
				return;
			}

			//体制区分IDを取得
			StationclassInfo stationclassInfo = stationService.getLoginCurrentStationclassInfo();
			if(stationclassInfo==null) {
				assembleForm.message = lang.__("Set system type of current system ({0}).", stationService.getLoginCurrentSationName());
				return;
			}

			//職員参集情報を取得
			AssembleInfo assembleInfo = assembleInfoService.findByLocalgovInfoIdAndStationclassInfoIdAndValid(loginDataDto.getLocalgovinfoid(), stationclassInfo.id);
			if (assembleInfo == null) {
				assembleForm.message = lang.__("Set the staff assembly info of system type ({0}).", stationclassInfo.name);
				return;
			}

			assembleForm.assembleinfoid = Long.toString(assembleInfo.id);
			//通知テンプレートを取得
			NoticeTemplate noticeTemplate = noticeTemplateService.findById(assembleInfo.noticetemplateid);
			if (noticeTemplate != null) {
				//メールで要請する場合
				if (noticeTemplate.noticetypeid.equals(NoticeType.MAIL)) {
					//記録IDを取得
					assembleForm.trackdataid = trackData.id.toString();
					//通知ユーザグループIDと名称を取得
					NoticegroupInfo noticegroupInfo = noticegroupInfoService.findById(assembleInfo.noticegroupinfoid);
					assembleForm.noticegroupinfoid = noticegroupInfo.id.toString();
					assembleForm.noticegroupinfoname = noticegroupInfo.name;
					//メール件名とメール定型文を取得
					assembleForm.mailtitle = noticeTemplateService.replaceTag(noticeTemplate.title);
					assembleForm.mailcontent = noticeTemplateService.replaceTag(noticeTemplate.content);
				}
				//アラームで要請する場合?
				/*else if (noticeTemplate.noticetypeid.equals(NoticeType.ALERT)) {
				}
				//テロップで要請する場合?
				else if (noticeTemplate.noticetypeid.equals(NoticeType.TELOP)) {
				}*/
			}
		}
	}

	/**
	 * メール送信処理
	 * @return 次のページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/assemble/send", produces="application/json")
	@ResponseBody
	public String send(Map<String,Object>model, @Valid @ModelAttribute AssembleForm assembleForm) {

    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	//ActionMessages errors = new ActionMessages();

		// 件名(必須チェック)
		if (StringUtil.isEmpty(assembleForm.mailtitle)) {
			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "件名"));
			//ResponseUtil.write("notitle");
			return "notitle";
		}
        // 本文(必須チェック)
		if (StringUtil.isEmpty(assembleForm.mailcontent)) {
			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "本文"));
			//ResponseUtil.write("nocontent");
			return "nocontent";
		}
		if (!checkEmail(assembleForm.additionalReceiver)) {
			//errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.invalid", "追加送付先"));
			//ResponseUtil.write("errormail");
			return "errormail";
		}
		// エラーが見つかった場合、戻す
		//if (errors.size() > 0) {
		//	ActionMessagesUtil.addErrors(request, errors);
		//	return "index.jsp";
		//}

		//トークンを判定
		//if (!TokenProcessor.getInstance().isTokenValid(SpringContext.getRequest(), true)) {
		//	//不正なトークンの場合、エラー画面を表示
		//	ResponseUtil.write("double");
		//	return null;
		//}

		//実行時刻
		Timestamp now = new Timestamp(System.currentTimeMillis());

		//記録データを取得
		Long trackdataid = Long.valueOf(assembleForm.trackdataid);
		TrackData track = trackDataService.findById(trackdataid);

		//送信
		Long assid = Long.parseLong(assembleForm.assembleinfoid);
		AssembleInfo assemble = assembleInfoService.findById(assid);
		Object[] ret = assembleInfoService.sendAssembleMail(assemble, assembleForm.mailtitle, assembleForm.mailcontent, track);
		boolean bsend = (Boolean)ret[0];
		String mailto = (String)ret[1];

		//追加送信
		try {
			if (StringUtil.isNotBlank(assembleForm.additionalReceiver)) {
				LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
				String smtp = null;
				if (localgovInfo.smtp != null) smtp = localgovInfo.smtp;
				String from = null;
				if (localgovInfo.email != null) from = localgovInfo.email;
				MailUtil.sendMail(smtp, from, assembleForm.additionalReceiver, null, null, assembleForm.mailtitle, assembleForm.mailcontent);
			}
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			bsend = false;
		}

		//アラート
		if (assembleForm.enablePopup) {
			AlarmmessageData alarm = new AlarmmessageData();
			alarm.localgovinfoid = loginDataDto.getLocalgovinfoid();
			alarm.duration = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String time = sdf.format(now);
			alarm.message = lang.__("[{0}]", time)+ assembleForm.mailcontent.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			alarm.messagetype = "information";
			alarm.showmessage = true;
			alarm.groupid = 0L;
			alarm.registtime = new Timestamp(System.currentTimeMillis());
			alarm.trackdataid = loginDataDto.getTrackdataid();
			alarm.sendgroupid = loginDataDto.getGroupid();
			alarm.noticeto = assembleForm.noticegroupinfoname;
			alarm.deleted = false;

			alarmmessageDataService.insert(alarm);
		}

		//通知データ（メール配信）を履歴に登録
		NoticemailData entity = new NoticemailData();
		entity.trackdataid = trackdataid;
		entity.mailto = mailto;
		if (StringUtil.isNotEmpty(assembleForm.additionalReceiver))
			entity.mailto += ","+assembleForm.additionalReceiver;
		entity.title = assembleForm.mailtitle;
		entity.content = assembleForm.mailcontent;
		entity.sendtime = now;
		entity.send = bsend;
		noticemailDataService.insert(entity);

		//通知先データ（メールグループ）を履歴に登録
		NoticemailsendData entity2 = new NoticemailsendData();
		entity2.noticemaildataid = entity.id;
		entity2.noticegroupinfoid = assemble.noticegroupinfoid;
		noticemailsendDataService.insert(entity2);

		//try {
			//HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
			//httpServletResponse.setContentType("application/json");
			//httpServletResponse.setCharacterEncoding("UTF-8");
			//PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());

			JSONObject res = new JSONObject();
			if (bsend)
				//sendPoint.println("{\"result\":\"OK\"}");
				res.put("result", "OK");
			else
				//sendPoint.println("{\"result\":\"ERROR\"}");
				res.put("result", "ERROR");
			return res.toString();
			//sendPoint.flush();
			//sendPoint.close();

		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		//	throw new ServiceException(e);
		//}
		//return null;
	}

	/**
	 * 職員参集が必要な職員を追加する。
	 * @param noticegroupinfoid
	 */
	public void addAssembleStateData(Long noticegroupinfoid)
	{
		//実行時刻
		Timestamp now = new Timestamp(System.currentTimeMillis());

		NoticegroupInfo noticegroupInfo = noticegroupInfoService.findById(noticegroupinfoid);
		if (noticegroupInfo == null) return;

		List<NoticegroupuserInfo> noticegroupuserInfo = noticegroupuserInfoService.findByNoticegroupInfoId(noticegroupInfo.id);
		if (noticegroupuserInfo != null) {
			for (NoticegroupuserInfo bean : noticegroupuserInfo) {
				//ユーザ情報を取得
				UserInfo userInfo = (userInfoService.findById(bean.userid));
				if (userInfo == null) continue;

				//班情報を取得
				UnitInfo unitInfo = unitInfoService.findById(userInfo.unitid);
				//ユニット（課、部署など）情報を取得
				GroupInfo groupInfo = null;
				//if (unitInfo!=null) groupInfo = groupInfoService.findById(unitInfo.groupid);
				groupInfo = groupInfoService.findById(userInfo.groupid);

				//職員参集状況に、登録が無いユーザを追加
				AssemblestateData entity = assemblestateDataService.findByTrackdataidAndUserid(loginDataDto.getTrackdataid(), bean.userid);
				//登録が無い場合
				if (entity == null) {
					//職員参集情報を作成
					entity = new AssemblestateData();
					entity.trackdataid = loginDataDto.getTrackdataid();
					entity.userid = userInfo.id;
					entity.safetystateid = SafetyState.NOTSEND;			//{1:未送信,2:メール送付済}
					if (groupInfo != null)
						entity.groupname = groupInfo.name;
					if (unitInfo != null)
						entity.unitname = unitInfo.name;
					entity.username = userInfo.name;
					entity.staffno = userInfo.staffno;
					entity.note = "";
					entity.registtime = now;
					entity.updatetime = now;

					//職員参集情報を追加
					assemblestateDataService.insert(entity);
				}
			}
		}
	}
}
