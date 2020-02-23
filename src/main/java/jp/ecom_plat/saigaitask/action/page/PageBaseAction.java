/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.NoticeaddressInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.db.NoticetypeMaster;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.db.NoticeaddressInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticemailDataService;
import jp.ecom_plat.saigaitask.service.db.NoticemailsendDataService;
import jp.ecom_plat.saigaitask.service.db.NoticetypeMasterService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataService;
import jp.ecom_plat.saigaitask.service.publiccommons.PublicCommonsService;
import jp.ecom_plat.saigaitask.validator.CommonValidator;

/**
 * ページアクションの基底クラスです.
 * 基本機能はここで実装します.
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class PageBaseAction extends AbstractPageAction {

	@Resource
	protected NoticegroupInfoService noticegroupInfoService;

	@Resource
	protected NoticeaddressInfoService noticeaddressInfoService;

	@Resource
	protected NoticemailDataService noticemailDataService;

	@Resource
	protected NoticemailsendDataService noticemailsendDataService;

	@Resource
	protected PubliccommonsReportDataService publiccommonsReportDataService;

	@Resource
	protected PublicCommonsService publicCommonsService;

	@Resource
	protected NoticetypeMasterService noticetypeMasterService;
	/** 通知種別マスタリスト */
	public List<NoticetypeMaster> noticetypeMasterItems;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticetypeMasterItems", noticetypeMasterItems);
	}
	
	/** email 宛先リスト */
	public Map<String, String> emailAddressMap;

	protected void setAdditionalReceiver() {
		emailAddressMap = new LinkedHashMap<String, String>();
		List<NoticegroupInfo> noticegroupInfo = noticegroupInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		for (NoticegroupInfo cur1 : noticegroupInfo) {
			List<NoticeaddressInfo> noticeaddressInfo = noticeaddressInfoService.getML(cur1.id);
			for (NoticeaddressInfo cur2 : noticeaddressInfo) {
				if (StringUtil.isBlank(cur2.email)) {
					continue;
				}
				emailAddressMap.put(cur1.name, cur2.email);
			}
		}
	}

	/**
	 * 確認用Eメール送信と履歴登録(メール本文と履歴登録データを分ける)
	 * @param title メールタイトル
	 * @param mailContent メール本文
	 * @param noticemailDataContent 通知履歴に登録する本文
	 * @param noticegroupinfoid メール宛先リスト
	 * @param additionalReceiver 追加送付先
	 * @param noticetypeid 通知先種別マスタ(noticetype_master)のid
	 * @return true: 送信OK, false: 送信失敗
	 */
	protected boolean sendEmail(String title, String mailContent, String noticemailDataContent, List<String> noticegroupinfoid, String additionalReceiver, int noticetypeid) {
		boolean sendFlg = true;

		List<Long> grpids = new ArrayList<Long>();
		for (String grpid : noticegroupinfoid) {
			grpids.add(Long.parseLong(grpid));
		}
		Object[] ret = noticegroupInfoService.sendMailToNoticeGroups(loginDataDto.getLocalgovinfoid(), grpids, additionalReceiver, title, mailContent);
		sendFlg = (Boolean)ret[0];
		String mailto = (String)ret[1];

		//通知データ（メール配信）を履歴に登録
		NoticemailData entity = new NoticemailData();
		entity.mailto = mailto;
		if (StringUtil.isNotEmpty(additionalReceiver)) {
			entity.mailto += "," + additionalReceiver;
		}
		entity.title = title;
		entity.content = noticemailDataContent;
		entity.sendtime = new Timestamp(System.currentTimeMillis());
		entity.send = sendFlg;
		entity.noticetypeid = noticetypeid;

		// 災害が立っている時のみ災害名をセット
		// 災害終了時に「避難勧告・避難指示の全解除」や「避難所の全閉鎖」を自動で行う際、コモンズに災害名を通知する必要がある
		// また、災害がない時でも発信できる通知がある
		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid()); // 記録データを取得
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

		return sendFlg;
	}

	/**
	 * 確認用Eメール送信と履歴登録
	 * @param title メールタイトル
	 * @param content メール本文
	 * @param noticegroupinfoid メール宛先リスト
	 * @param additionalReceiver 追加送付先
	 * @param noticetypeid 通知先種別マスタ(noticetype_master)のid
	 * @return true: 送信OK, false: 送信失敗
	 */
	protected boolean sendEmail(String title, String content, List<String> noticegroupinfoid, String additionalReceiver, int noticetypeid) {
		// オーバーロードメソッドへ処理を渡す
		return sendEmail(title, content, content, noticegroupinfoid, additionalReceiver, noticetypeid);
	}

	/**
	 * カンマ区切りで指定された追加送付先の各メールアドレスをチェック
	 *
	 * OK: true NG: false
	 *
	 * @param target
	 * @return
	 */
	protected boolean checkEmail(String target) {
		if (StringUtils.isBlank(target)) {
			return true;
		}

		String[] emails = target.split(",", -1);
		for (String cur : emails) {
			if (!CommonValidator.isEmail(cur)) {
				return false;
			}
		}
		return true;
	}

//	/**
//	 * 画面へのコールバック
//	 * @param sendFlg 確認用Eメール送信結果フラグ
//	 */
//	protected void callBack(boolean sendFlg) {
//		try {
//			HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
//			httpServletResponse.setContentType("application/json");
//			HttpServletResponse res = ResponseUtil.getResponse();
//			res.setContentType("text/html; charset=Windows-31J");
//			PrintWriter out = res.getWriter();
//
//			// Eメール送信
//			if (sendFlg) {	// 送信成功時
//				out.println("<html><head>");
//				out.println("<body>送信が完了しました。<br /><br /><div class=\"close\"><input type=\"submit\" value=\"閉じる\" onClick=\"self.parent.tb_remove(); return false;\" />");
//				out.println("</body></html>");
//			} else {	// 送信失敗時
//				out.println("<html>");
//				out.println("<body>送信エラーが発生しました。</body></html>");
//			}
//			out.flush();
//			out.close();
////			HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
////			httpServletResponse.setContentType("application/json");
////			PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());
////
////			if (sendFlg) {
////				sendPoint.println("{\"result\":\"OK\"}");
////			} else {
////				sendPoint.println("{\"result\":\"ERROR\"}");
////			}
////			sendPoint.flush();
////			sendPoint.close();
//		} catch (IOException e) {
//			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
//			logger.error("", e);
//			throw new ServiceException(e);
//		}
//	}

	/**
	 * メール宛先の作成
	 * @param checkedEmailAddressList メーリングリスト
	 * @param additionalReceiver 追加送付先
	 * @return メール宛先
	 */
	protected String createToAddress(String[] checkedEmailAddressList, String additionalReceiver) {
		StringBuffer toAddress = new StringBuffer();

		if (checkedEmailAddressList != null) {
			for (String cur : checkedEmailAddressList) {
				toAddress.append(cur);
				toAddress.append(",");
			}
		}

		if (!StringUtil.isBlank(additionalReceiver)) {
			toAddress.append(additionalReceiver);
		}

		return toAddress.toString();
	}
}
