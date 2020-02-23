/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.action.page.AbstractPageAction;
import jp.ecom_plat.saigaitask.constant.NoticeType;
import jp.ecom_plat.saigaitask.entity.db.AlarmdefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticetemplatetypeMaster;
import jp.ecom_plat.saigaitask.entity.db.NoticetypeMaster;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.form.SendInformationForm;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.AlarmdefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.FacebookInfoService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticeaddressInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticemailDataService;
import jp.ecom_plat.saigaitask.service.db.NoticetemplatetypeMasterService;
import jp.ecom_plat.saigaitask.service.db.NoticetypeMasterService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import jp.ecom_plat.saigaitask.util.GCMUtil;
import jp.ecom_plat.saigaitask.validator.CommonValidator;

/**
 * 情報発信ページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class SendInformationAction extends AbstractPageAction {

	/** アクションフォーム */
	protected SendInformationForm sendInformationForm;

	@Resource
	protected NoticegroupInfoService noticegroupInfoService;
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected NoticedefaultInfoService noticedefaultInfoService;

	@Resource
	protected NoticeaddressInfoService noticeaddressInfoService;

	@Resource
	protected NoticemailDataService noticemailDataService;

	@Resource
	protected FacebookInfoService facebookInfoService;

	@Resource
	protected GroupInfoService groupInfoService;

	@Resource
	protected AlarmmessageDataService alarmmessageDataService;
	@Resource
	protected AlarmdefaultgroupInfoService alarmdefaultgroupInfoService;

	/** 通知グループ情報リスト */
	public List<NoticegroupInfo> noticegroupInfoItems;
	/** 班情報リスト */
	public List groupInfoItems;

	@Resource
	protected NoticetemplatetypeMasterService noticetemplatetypeMasterService;
	/** 通知テンプレート種別マスタ */
	public List<NoticetemplatetypeMaster> noticetemplatetypeItems;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	/** 通知テンプレート */
	public Map<Integer, List<NoticeTemplate>> noticeTemplateMap;

	@Resource
	protected NoticetypeMasterService noticetypeMasterService;
	/** 通知種別マスタ */
	public List<NoticetypeMaster> noticetypeMasterItems;

	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected FilteredFeatureService filteredFeatureService;
	@Resource
	protected UnitInfoService unitInfoService;
	@Resource
	protected UserInfoService userInfoService;

	public boolean enablePush = false;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("noticegroupInfoItems", noticegroupInfoItems);
		model.put("groupInfoItems", groupInfoItems);
		model.put("noticetemplatetypeItems", noticetemplatetypeItems);
		model.put("noticeTemplateMap", noticeTemplateMap);
		model.put("noticetypeMasterItems", noticetypeMasterItems);
		model.put("sendInformationForm", sendInformationForm);
	}
	/**
	 * 情報発信ページを表示する.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/sendInformation/{menuinfoid}")
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute SendInformationForm sendinformationForm, BindingResult bindingResult) {
		this.sendInformationForm = sendinformationForm;
		// メール配信先
		noticegroupInfoItems = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		/*for (NoticegroupInfo cur1 : noticegroupInfo) {
			List<NoticeaddressInfo> noticeaddressInfo = noticeaddressInfoService.getML(cur1.id);
			for (NoticeaddressInfo cur2 : noticeaddressInfo) {
				if (StringUtil.isBlank(cur2.email)) {
					continue;
				}
				sendInformationForm.emailAddressMap.put(cur1.name, cur2.email);
			}
		}*/

		if (!loginDataDto.isUsual())
			groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
		else
			groupInfoItems = unitInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());

		if (!StringUtil.isBlank((String)session.getAttribute("honbun"))) {
			sendInformationForm.sendInformationContent = (String)session.getAttribute("honbun");
			session.removeAttribute("honbun");
		}

    	long menuid = 0;
    	int templatetypeid = 0;
    	String templateclass = null;
    	if (StringUtil.isNotEmpty(sendInformationForm.menuinfoid)) {
    		menuid = Integer.parseInt(sendInformationForm.menuinfoid);
    		NoticedefaultInfo definfo = noticedefaultInfoService.findByMenuInfoId(menuid);
    		if (definfo != null && definfo.noticetemplatetypeid != null) {
    			templatetypeid = definfo.noticetemplatetypeid;
    			templateclass = definfo.templateclass;
    		}
    	}

		//デフォルト設定
		List<NoticedefaultgroupInfo> defaultgrplist = noticedefaultgroupInfoService.findByMenuInfoIdAndDefaultOn(menuid);
		for (NoticedefaultgroupInfo ginfo : defaultgrplist) {
			sendInformationForm.noticegroupinfoid.add(ginfo.noticegroupinfoid.toString());
		}
		List<AlarmdefaultgroupInfo> alarmgrplist = alarmdefaultgroupInfoService.findByMenuInfoId(menuid);
		for (AlarmdefaultgroupInfo ginfo : alarmgrplist) {
			sendInformationForm.checkedAlertList.add(ginfo.groupid.toString());
		}

		//テンプレート用
    	noticetemplatetypeItems = noticetemplatetypeMasterService.findAllOrderByDisporder();

    	noticeTemplateMap = new HashMap<Integer, List<NoticeTemplate>>();
    	List<NoticeTemplate> tlist = noticeTemplateService.findByLoaclgovInfoIdNoticetypeId(loginDataDto.getLocalgovinfoid(), NoticeType.MAIL);
    	for (NoticeTemplate temp : tlist) {
    		if (temp.noticetemplatetypeid == null) continue;
    		if (templatetypeid != 0 && templatetypeid != temp.noticetemplatetypeid) continue;
    		if (StringUtil.isNotEmpty(templateclass) && !templateclass.equals(temp.templateclass)) continue;
    		List<NoticeTemplate> list = noticeTemplateMap.get(temp.noticetemplatetypeid);
    		if (list == null) {
    			list = new ArrayList<NoticeTemplate>();
    			noticeTemplateMap.put(temp.noticetemplatetypeid, list);
    		}
    		temp.content = temp.content.replaceAll("\n", "<br>");
    		list.add(temp);
    	}

		//インポートデータ
		List<MenutableInfo> menutableList = menutableInfoService.findByMenuInfoId(menuid);
		MenutableInfo mtbl = null;
		if (menutableList.size() > 0) {//TODO:当面は一つだけ
			mtbl = menutableList.get(0);
		}
    	MenuInfo minfo = menuInfoService.findByNotDeletedId(menuid);
		// フィルター情報を検索
		minfo.filterInfoList = filterInfoService.findByMenuid(minfo.id);
		StringBuffer content = null;
		if (minfo != null && 0<minfo.filterInfoList.size())
			content = filteredFeatureService.getFilteredFeatureContext(loginDataDto.getTrackdataid(), minfo, mtbl);
		else
			content = filteredFeatureService.getAllFeatureContext(loginDataDto.getTrackdataid(), mtbl);
		if (content != null)
			sendInformationForm.sendInformationContent = content.toString();
    	//トークンを設定
		//TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String key = "GOOGLE_API_KEY";
		if(rb.containsKey(key)) {
			String API_KEY = rb.getString(key);
			if (StringUtil.isNotEmpty(API_KEY))
				enablePush = true;

		}
		setupModel(model);
		return "/sendInformation/index";
	}

	/**
	 * 送信処理
	 * @return 次のページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/sendInformation/send")
	public String send(Map<String,Object>model, HttpServletResponse res,
			@Valid @ModelAttribute SendInformationForm sendInformationForm, BindingResult bindingResult){
		ActionMessages errors = new ActionMessages();
		this.sendInformationForm = sendInformationForm;
		//トークンを判定
		/*if (!TokenProcessor.getInstance().isTokenValid(SpringContext.getRequest(), true)) {
			//不正なトークンの場合、エラーを表示
			//ResponseUtil.write("double");
			return "error";
		}*/

		// ポップアップさせない、メール配信しない両方ともチェックOFF
		if (sendInformationForm.notmail && !sendInformationForm.popup && !sendInformationForm.push) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Alert or e-mail delivery")), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
			setupModel(model);
			return index(model,sendInformationForm,bindingResult);
		}

		if (sendInformationForm.popup) {
			// アラート配信先(必須チェック)
			if (sendInformationForm.checkedAlertList == null || sendInformationForm.checkedAlertList.size() == 0) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Alert to")), false));
			}

			// 件名(必須チェック)
			if ("".equals(sendInformationForm.emailTitle)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title<!--3-->")), false));
			}
	        // 本文(必須チェック)
			if ("".equals(sendInformationForm.sendInformationContent)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text")), false));
			}
			// 件名(文字数チェック)
			if (sendInformationForm.emailTitle.length() > 50) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title<!--3-->"), "50"), false));
			}
			// 本文(文字数チェック)
			if (sendInformationForm.sendInformationContent.length() > 5000) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Text"), "5000"), false));
			}
			// エラーが見つかった場合、戻す
			if (errors.size() > 0) {
				ActionMessagesUtil.addErrors(bindingResult, errors);
				setupModel(model);
				return "/sendInformation/"+sendInformationForm.menuinfoid;
			}
		}

		if (sendInformationForm.push) {
			// アラート配信先(必須チェック)
			if (sendInformationForm.checkedAlertList == null || sendInformationForm.checkedAlertList.size() == 0) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Alert to")), false));
			}
	        // 本文(必須チェック)
			if (StringUtil.isEmpty(sendInformationForm.pushContent)) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text")), false));
			}
			// エラーが見つかった場合、戻す
			if (errors.size() > 0) {
				ActionMessagesUtil.addErrors(bindingResult, errors);
				setupModel(model);
				return "/sendInformation/"+sendInformationForm.menuinfoid;
			}
		}

        // メール配信しないチェックOFF時のみメール送信
		if (!sendInformationForm.notmail) {
			String dirPath = "";

			try {

				// メール配信先(必須チェック)
				if (( "".equals(sendInformationForm.additionalReceiver)) && (sendInformationForm.noticegroupinfoid.isEmpty())) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("E-mail delivery destination")), false));
				}
		        // 追加送付先(メールアドレス文法チェック)
				String mails[] = sendInformationForm.additionalReceiver.split(",");
				for (String mail : mails) {
					if (!checkEmail(mail.trim())) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Invalid {0}", lang.__("Add delivery target")), false));
						break;
					}
				}
				// 件名(必須チェック)
				if ("".equals(sendInformationForm.emailTitle)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Title<!--3-->")), false));
				}
		        // 本文(必須チェック)
				if ("".equals(sendInformationForm.sendInformationContent)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} must be required.", lang.__("Text")), false));
				}
				// 件名(文字数チェック)
				if (sendInformationForm.emailTitle.length() > 50) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Title<!--3-->"), "50"), false));
				}
				// 本文(文字数チェック)
				if (sendInformationForm.sendInformationContent.length() > 5000) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The length of {0} exceeds max {1}.", lang.__("Text"), "5000"), false));
				}
				// エラーが見つかった場合、戻す
				if (errors.size() > 0) {
					ActionMessagesUtil.addErrors(bindingResult, errors);
					setupModel(model);
					return index(model,sendInformationForm,bindingResult);
				}

				// 添付ファイルの有無チェック
				boolean attachFlg = false;
				for (MultipartFile file : sendInformationForm.formFiles) {
						if (file != null) {
							if(!"".equals(file.getOriginalFilename())) {
								attachFlg = true;
								break;
							}
						}
				}

				// 添付ファイルが指定されている場合
				if (attachFlg) {
					// 添付ファイルの保存先パスを取得
					Date datetimenow = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					ResourceBundle rb = ResourceBundle.getBundle("ResourceInfo");
					dirPath = rb.getString("MAIL_ATTACH_FILE_PATH") + File.separator + loginDataDto.getLocalgovinfoid() + File.separator + sdf.format(datetimenow) + File.separator;

					// Windowsのセパレータ対策(主に開発環境用)
					String sr = File.separator;
					if ("\\".equals(File.separator)) sr = "\\\\";

					// 1階層ずつディレクトリを作成
					StringBuffer dirPathEx = new StringBuffer();
					String[] dirName = dirPath.split(sr);
					for(int i=0; i<dirName.length; i++) {
						dirPathEx.append(dirName[i]);
						dirPathEx.append(File.separator);
						File f = new File(dirPathEx.toString());
						if(! f.exists()){
							f.mkdir();
						}else{
							if(! f.isDirectory()) f.mkdir();
						}
					}

					// アップロード
					ActionMessages messages = new ActionMessages();
					ActionMessagesUtil.addMessages(request, messages);
					for (MultipartFile file : sendInformationForm.formFiles) {
						if (file != null){
							if (!"".equals(file.getOriginalFilename())) {
								upload(file, messages, dirPath);
							}
						}
					}
				}
				/*HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
				httpServletResponse.setContentType("application/json");
				res = ResponseUtil.getResponse();*/
				res.setContentType("text/html; charset=Windows-31J");
				PrintWriter out = res.getWriter();

				// Eメール送信と履歴登録
				if (sendEmail(dirPath)) {	// 送信成功時
					// Thickbox利用中につき画面遷移をせずに送信結果を表示
					out.println("<html><head>");
					out.println("<body>" + lang.__("Completed to send.") + "<br /><br /><div class=\"close\"><input type=\"submit\" value=\"" + lang.__("Close") + "\" onClick=\"self.parent.tb_remove(); return false;\" />");
					out.println("</body></html>");
				} else {	// 送信失敗時
					// Thickbox利用中につき画面遷移をせずに送信結果を表示
					out.println("<html>");
					out.println("<body>" + lang.__("Transmission error has occurred.")+ "<br /><br /><div class=\"close\"><input type=\"submit\" value=\"" + lang.__("Close") + "\" onClick=\"self.parent.tb_remove(); return false;\" />");
					out.println("</body></html>");
				}
				out.flush();
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(loginDataDto.logInfo(), e);
				throw new ServiceException(e);
//			※仕様変更の際に使う可能性有
//			} finally {
//				// 削除しないことになったためコメントアウト
//				try {
//					// 添付ファイルが指定されている場合
//					if (sendInformationForm.formFiles.length > 1) {
//						// 添付ファイルを削除
//						for (MultipartFile file : sendInformationForm.formFiles) {
//							File filepath = new File(dirPath + file);
//							if (filepath.exists()){
//								filepath.delete();
//							}
//						}
//						// 一時保存先ディレクトリを削除
//						File filepath = new File(dirPath);
//						if (filepath.exists()){
//							filepath.delete();
//						}
//					}
//				} catch (Exception e) {
//					logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
//					logger.error("", e);
//					throw new ServiceException(e);
//				}
			}

		} else {
			try {
				/*HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
				httpServletResponse.setContentType("application/json");
				HttpServletResponse */res = response;
				res.setContentType("text/html; charset=Windows-31J");
				PrintWriter out = res.getWriter();

				out.println("<html><head>");
				out.println("<body>" + lang.__("Completed to send.") + "<br /><br /><div class=\"close\"><input type=\"submit\" value=\"" + lang.__("Close") + "\" onClick=\"self.parent.tb_remove(); return false;\" />");
				out.println("</body></html>");

				out.flush();
				out.close();

			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
				throw new ServiceException(e);
			}
		}

        // ポップアップさせないチェックOFF時のみアラート表示
		if (sendInformationForm.popup && sendInformationForm.checkedAlertList != null) {
			for (String gid : sendInformationForm.checkedAlertList) {
				Long groupid = 0l;
				String gname = "";
				if (!loginDataDto.isUsual()) {
					GroupInfo ginfo = groupInfoService.findByNotDeletedId(Long.parseLong(gid));
					if(ginfo == null) continue;
					groupid = ginfo.id;
					gname = ginfo.name;
				}
				else {
					UnitInfo uinfo = unitInfoService.findByNotDeletedId(Long.parseLong(gid));
					if (uinfo == null)	continue;
					groupid = uinfo.id;
					gname = uinfo.name;
				}
				// 削除済等でグループが見つからない場合は処理しない
				AlarmmessageData alarm = new AlarmmessageData();
				alarm.localgovinfoid = loginDataDto.getLocalgovinfoid();
				alarm.duration = 0;
				Timestamp time = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String now = sdf.format(time);
				alarm.message = lang.__("[{0}]", now)+ sendInformationForm.sendInformationContent;
				alarm.messagetype = "information";
				alarm.showmessage = true;
				if (!loginDataDto.isUsual()) {
					alarm.groupid = groupid;
					alarm.sendgroupid = loginDataDto.getGroupid();
				}
				else {
					alarm.unitid = groupid;
					alarm.sendunitid = loginDataDto.getUnitid();
				}
				alarm.registtime = new Timestamp(System.currentTimeMillis());
				alarm.trackdataid = loginDataDto.getTrackdataid();
				alarm.noticeto = gname;
				alarm.noticeto = alarm.noticeto.trim();
				alarm.noticeurl = loginDataDto.getPreMenuUrl();
				alarm.deleted = false;

				alarmmessageDataService.insert(alarm);
			}
		}

		// アプリにPUSH通知
		if (sendInformationForm.push && sendInformationForm.checkedAlertList != null) {
			JSONArray pushtokens_android = new JSONArray();
			JSONArray pushtokens_ios = new JSONArray();
			for (String gid : sendInformationForm.checkedAlertList) {
				List<UserInfo> userlist = null;
				if (!loginDataDto.isUsual()) {
					GroupInfo ginfo = groupInfoService.findByNotDeletedId(Long.parseLong(gid));
					if(ginfo == null) continue;
					userlist = userInfoService.findByGroupIdAndValid(ginfo.id);
				}
				else {
					UnitInfo uinfo = unitInfoService.findByNotDeletedId(Long.parseLong(gid));
					if (uinfo == null) continue;
					userlist = userInfoService.findByGroupIdAndValid(uinfo.id);
				}
				//List<UnitInfo> unitlist = unitInfoService.findByGroupIdAndValid(ginfo.id);
				//for (UnitInfo unit : unitlist) {
					//List<UserInfo> userlist = userInfoService.findByUnitIdAndValid(unit.id);
				if (userlist != null) {
					for (UserInfo user : userlist) {
						if (StringUtil.isNotEmpty(user.pushtoken)) {
							if (user.pushtoken.indexOf("android:") == 0)
								pushtokens_android.put(user.pushtoken.substring(8));
							if (user.pushtoken.indexOf("ios:") == 0)
								pushtokens_ios.put(user.pushtoken.substring(4));
						}
					}
				}

			}

			//PUSH通知
			//for android
			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			if(rb.containsKey("GOOGLE_API_KEY")) {
				String API_KEY = rb.getString("GOOGLE_API_KEY");
				if (StringUtil.isNotEmpty(API_KEY) && pushtokens_android.length() > 0) {
					GCMUtil.sendGCMforAndroid(pushtokens_android, sendInformationForm.pushContent, API_KEY);
				}
			}
			//for iOS
			if (rb.containsKey("APNS_CERTIFICATE_FILE") && rb.containsKey("APNS_KEY_PASSWORD")) {
				String APNS_KEY = rb.getString("APNS_CERTIFICATE_FILE");
				String password = rb.getString("APNS_KEY_PASSWORD");
				if (StringUtil.isNotEmpty(APNS_KEY)) {
					URL url = this.getClass().getResource(APNS_KEY);
					if (url != null && pushtokens_ios.length() > 0) {
						GCMUtil.sendAPNSforiOS(pushtokens_ios, sendInformationForm.pushContent, url, password);
					}
				}
			}
		}
		setupModel(model);
		return null;
	}

 protected void upload(MultipartFile file, ActionMessages messages, String dirPath) throws Exception {
	if (file.getSize() == 0) return;

	String filepath = dirPath + file.getOriginalFilename();
	OutputStream out = new BufferedOutputStream(new FileOutputStream(filepath));
	try {
		out.write(file.getBytes(), 0, (int) file.getSize());
	} catch (Exception e) {
		throw e;
	} finally {
		out.close();
	}
	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("messages.upload.complete", new Object[] { filepath }));
 }

	/**
	 * カンマ区切りで指定された追加送付先の各メールアドレスをチェック
	 *
	 * OK: true NG: false
	 *
	 * @param target
	 * @return
	 */
	private boolean checkEmail(String target) throws Exception {
		if (StringUtils.isBlank(target)) {
			return true;
		}

		String[] emails = target.split(",", -1);
		for (String cur : emails) {
			if (!CommonValidator.isEmail(cur)) return false;
		}
		return true;
	}

	/**
	 * Eメール送信と履歴登録
	 * @param dirPath
	 * @return true: 送信OK, false: 送信失敗
	 */
	private boolean sendEmail(String dirPath){
		Boolean sendFlg;

		try {

			//送信
			List<Long> grpids = new ArrayList<Long>();
			for (String grpid : sendInformationForm.noticegroupinfoid)
				grpids.add(Long.parseLong(grpid));
			Object[] ret = noticegroupInfoService.sendAttachMailToNoticeGroups(loginDataDto.getLocalgovinfoid(), grpids, sendInformationForm.additionalReceiver, sendInformationForm.emailTitle, sendInformationForm.sendInformationContent, sendInformationForm.formFiles, dirPath);
			sendFlg = (Boolean)ret[0];
			String mailto = (String)ret[1];

			// 実行時刻
			Timestamp now = new Timestamp(System.currentTimeMillis());

			// 履歴登録（noticemail_data）
			NoticemailData entity = new NoticemailData();
			entity.mailto = mailto;
			entity.title = sendInformationForm.emailTitle;
			entity.content = sendInformationForm.sendInformationContent;
			entity.sendtime = now;
			entity.send = sendFlg;
			entity.noticetypeid = NoticeType.MAIL;

			// 災害が立っている時のみ災害名をセット
			// 災害終了時に「避難勧告・避難指示の全解除」や「避難所の全閉鎖」を自動で行う際、コモンズに災害名を通知する必要がある
			// また、災害がない時でも発信できる通知がある
			TrackData trackData = null;
			if(0<loginDataDto.getTrackdataid()) {
				trackData = trackDataService.findById(loginDataDto.getTrackdataid());  // 記録データを取得
			}
			if (trackData != null ) {
				entity.trackdataid = Long.valueOf(trackData.id.toString());
				entity.trackdataname = trackData.name;
			} else {
				entity.trackdataid = Long.valueOf(0);
			}

			// 添付ファイル(画面で添付指定→指定解除してもフォームに配列が残るため1つでも存在するかチェックする)
			for (MultipartFile file : sendInformationForm.formFiles) {
				if (file != null){
					if (!"".equals(file.getOriginalFilename())) {
						entity.attachfilename = dirPath;
						break;
					}
				}
			}
			noticemailDataService.insert(entity);
		} catch(Exception e) {
			sendFlg = false;
		}
		return sendFlg;
	}
}
