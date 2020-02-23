/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.assembleInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.constant.SafetyState;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupuserInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.util.GCMUtil;
import jp.ecom_plat.saigaitask.util.MailUtil;

@org.springframework.stereotype.Repository
public class AssembleInfoService extends AbstractService<AssembleInfo> {

	@Resource
	protected HttpServletRequest request;

	@Resource
	protected AssemblestateDataService assemblestateDataService;
	@Resource
	protected NoticegroupInfoService noticegroupInfoService;
	@Resource
	protected NoticegroupuserInfoService noticegroupuserInfoService;
	@Resource
	protected UserInfoService userInfoService;
	@Resource
	protected LocalgovInfoService localgovInfoService;
	@Resource
	protected UnitInfoService unitInfoService;
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected TrackDataService trackDataService;

	/**
	 * IDで検索
	 * @param id ID
	 * @return IDに対応唯一のレコード
	 */
	public AssembleInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 自治体IDと体制IDで有効な職員参集情報を検索
	 * @param localgovinfoid 自治体ID
	 * @param stationclassinfoid 体制ID
	 * @return 職員参集情報
	 */
	public AssembleInfo findByLocalgovInfoIdAndStationclassInfoIdAndValid(Long localgovinfoid, Long stationclassinfoid) {
		return select().where(
				and(
					eq(assembleInfo().localgovinfoid(), localgovinfoid),
					eq(assembleInfo().stationclassinfoid(), stationclassinfoid),
					eq(assembleInfo().valid(), true)
				)).limit(1).getSingleResult();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(assembleInfo().noticegroupInfo())
			.innerJoin(assembleInfo().noticeTemplate())
			.innerJoin(assembleInfo().stationclassInfo())
			.innerJoin(assembleInfo().localgovInfo())
			.where(conditions)
			.getCount();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @return 検索結果
	 */
	public List<AssembleInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = assembleInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!assembleInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(assembleInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(assembleInfo().noticegroupInfo())
			.innerJoin(assembleInfo().noticeTemplate())
			.innerJoin(assembleInfo().stationclassInfo())
			.innerJoin(assembleInfo().localgovInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}


	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(AssembleInfo entity, PropertyName<?>[] excludes) {
		if(excludes != null){
			return jdbcManager
				.update(entity)
				.excludes(excludes)
				.execute();
		}else{
			return jdbcManager
				.update(entity)
				.execute();
		}
	}

	public Object[] sendAssembleMail(AssembleInfo assemble, String title, String content, TrackData track)
	{
		if (assemble.noticegroupinfoid != null && assemble.noticegroupinfoid == 0l) return new Object[]{Boolean.valueOf(false), ""};
		if (track == null) return new Object[]{Boolean.valueOf(false), ""};
		JSONArray pushtokens_android = new JSONArray();
		JSONArray pushtokens_ios = new JSONArray();

		//実行時刻
		Timestamp now = new Timestamp(System.currentTimeMillis());

		//メール送信用
		LocalgovInfo localgovInfo = localgovInfoService.findById(assemble.localgovinfoid);
		String smtp = null;
		if (localgovInfo.smtp != null) smtp = localgovInfo.smtp;
		String from = null;
		if (localgovInfo.email != null) from = localgovInfo.email;

		//通知データへの保存用
		StringBuffer mailto = new StringBuffer();
		boolean bsend = true;

		// クリックで安否状況を更新するURL文字列の準備
		//URLのベース
		String host = null, cont = null;
		if (request != null) {
			host = request.getServerName();
		//int port = request.getServerPort();
			cont = request.getContextPath();
		}
		else {
			host = localgovInfo.domain;
			cont = "/SaigaiTask";
		}
		String baseURL = "";
		//if(port!=80){//80番以外のポートで稼働している場合はポート番号を追加する
		//	baseURL = "http://" + host + ":"+port + cont + "/setsafetystate/";
		//}else{
			baseURL = "http://" + host + cont + "/setsafetystate/show/";
		//}
		//URLにユーザIDを含めるので、オリジナルのcontentに都度、URLを組み立てて追記する
		String contentOrg = content;

		NoticegroupInfo noticegroupInfo = noticegroupInfoService.findById(assemble.noticegroupinfoid);
		List<NoticegroupuserInfo> noticegroupuserInfo = noticegroupuserInfoService.findByNoticegroupInfoId(noticegroupInfo.id);
		if (noticegroupuserInfo != null) {
			for (NoticegroupuserInfo bean : noticegroupuserInfo) {
				//ユーザ情報を取得
				UserInfo userInfo = (userInfoService.findById(bean.userid));
				if (userInfo == null) continue;
				String to = "";
				if (StringUtil.isNotEmpty(userInfo.email))
					to += userInfo.email;
				if (StringUtil.isNotEmpty(to) && StringUtil.isNotEmpty(userInfo.mobilemail))
					to += ",";
				if (StringUtil.isNotEmpty(userInfo.mobilemail))
					to += userInfo.mobilemail;
				if (StringUtil.isNotEmpty(userInfo.pushtoken)) {
					if (userInfo.pushtoken.indexOf("android:") == 0)
						pushtokens_android.put(userInfo.pushtoken.substring(8));
					if (userInfo.pushtoken.indexOf("ios:") == 0)
						pushtokens_ios.put(userInfo.pushtoken.substring(4));
				}
				if (StringUtil.isEmpty(to)) continue;
				//班情報を取得
				UnitInfo unitInfo = unitInfoService.findById(userInfo.unitid);
				//ユニット（課、部署など）情報を取得
				//GroupInfo groupInfo = groupInfoService.findById(unitInfo.groupid);
				GroupInfo groupInfo = groupInfoService.findById(userInfo.groupid);

				//職員参集状況に、登録が無いユーザ、または「未送信」のユーザにメール送信
				AssemblestateData entity = assemblestateDataService.findByTrackdataidAndUserid(track.id, bean.userid);
				//登録が無い場合
				if (entity == null) {
					//職員参集情報を作成
					entity = new AssemblestateData();
					entity.trackdataid = track.id;
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

					/** 安否状況を変更するURLの追加 */
					//{context_path}/setsafetystate/show/{encryptparam}
					//メールに追加する安否状況を設定するURLの組み立て
					//引数のbaseURLはsetsafetystateまで定義済
					try{
						StringBuffer sbuf = new StringBuffer();
						sbuf.append(track.id);
						sbuf.append(",");
						sbuf.append(assemble.localgovinfoid);
						sbuf.append(",");
						sbuf.append(entity.userid);

						String safetyURL = "\n\n" + lang.__("In case to change gathering status, click on url shown below.") + "\n"
							+ baseURL +  URLEncoder.encode(jp.ecom_plat.saigaitask.util.StringUtil.encrypt(sbuf.toString()),"UTF-8");
//						//3:メール返信有
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.3") + baseURL+"3/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						//4:参集中
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.4") + baseURL+"4/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						//6:参集困難
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.6") + baseURL+"6/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						//5:参集完了
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.5") + baseURL+"5/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						;
						logger.info("safetyURL : "+safetyURL);
						content = contentOrg + safetyURL;
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
						bsend = false;
					}

					//メールの送信
					try {
						//デバッグ用にメール本文にユーザ名を埋め込んでいます。
						//String content = "To: " + userInfo.name + "\r\n" + requestForm.mailcontent;
						//MailUtil.sendMail(smtp, from, to, null, null, requestForm.mailtitle, content);
						MailUtil.sendMail(smtp, from, to, null, null, title, content);

						if (mailto.length() != 0) mailto.append(",");
						mailto.append(to);
						entity.safetystateid = SafetyState.SENT;	//{1:未送信,2:メール送付済}
					} catch (Exception e) {
						e.printStackTrace();
						bsend = false;
					}

					//職員参集情報を追加
					assemblestateDataService.insert(entity);
				}
				//未送信もしくは応答なしの場合
				else if (entity.safetystateid == null || entity.safetystateid.equals(SafetyState.NOTSEND) || entity.safetystateid.equals(SafetyState.SENT)) {
					//職員参集情報を作成
					entity.updatetime = now;

					/** 安否状況を変更するURLの追加 */
					//{context_path}/setsafetystate/show/{encryptparam}
					//メールに追加する安否状況を設定するURLの組み立て
					//引数のbaseURLはsetsafetystateまで定義済
					try{
						StringBuffer sbuf = new StringBuffer();
						sbuf.append(track.id);
						sbuf.append(",");
						sbuf.append(assemble.localgovinfoid);
						sbuf.append(",");
						sbuf.append(entity.userid);

						String safetyURL = "\n\n" + lang.__("In case to change gathering status, click on url shown below.") + "\n"
								+ baseURL +  URLEncoder.encode(jp.ecom_plat.saigaitask.util.StringUtil.encrypt(sbuf.toString()),"UTF-8");
//						//3:メール返信有
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.3") + baseURL+"3/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						//4:参集中
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.4") + baseURL+"4/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						//6:参集困難
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.6") + baseURL+"6/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//						//5:参集完了
//						+ MessageResourcesUtil.getMessage("info.safetystate.template.5") + baseURL+"5/"+track.id+"/"+entity.userid+"/"
//						+String.format("%010d", random.nextInt(Integer.MAX_VALUE))+"\n"
//					;
						logger.info("safetyURL : "+safetyURL);
						content = contentOrg + safetyURL;

					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
						bsend = false;
					}

					//メールの送信
					try {
						//デバッグ用にメール本文にユーザ名を埋め込んでいます。
						//String content = "To: " + userInfo.name + "\r\n" + requestForm.mailcontent;
						//MailUtil.sendMail(smtp, from, to, null, null, requestForm.mailtitle, content);
						MailUtil.sendMail(smtp, from, to, null, null, title, content);

						if (mailto.length() != 0) mailto.append(",");
						mailto.append(to);
						entity.safetystateid = SafetyState.SENT;	//{1:未送信,2:メール送付済}
					} catch (Exception e) {
						e.printStackTrace();
						bsend = false;
					}

					//職員参集情報を更新
					assemblestateDataService.update(entity);
				}
			}
		}

		//PUSH通知
		//for android
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		if (rb.containsKey("GOOGLE_API_KEY")) {
			String API_KEY = rb.getString("GOOGLE_API_KEY");
			if (StringUtil.isNotEmpty(API_KEY) && pushtokens_android.length() > 0) {
				GCMUtil.sendGCMforAndroid(pushtokens_android, contentOrg, API_KEY);
			}
		}
		//for iOS
		if (rb.containsKey("APNS_CERTIFICATE_FILE") && rb.containsKey("APNS_KEY_PASSWORD")) {
			String APNS_KEY = rb.getString("APNS_CERTIFICATE_FILE");
			String password = rb.getString("APNS_KEY_PASSWORD");
			if (StringUtil.isNotEmpty(APNS_KEY)) {
				URL url = this.getClass().getResource(APNS_KEY);
				if (url != null && pushtokens_ios.length() > 0) {
					GCMUtil.sendAPNSforiOS(pushtokens_ios, contentOrg, url, password);
				}
			}
		}
		
		return new Object[]{Boolean.valueOf(bsend), mailto.toString()};
	}

	public List<AssembleInfo> check() {
		List<AssembleInfo> reslist = select().leftOuterJoin(assembleInfo().localgovInfo())
				.leftOuterJoin(assembleInfo().stationclassInfo())
				.leftOuterJoin(assembleInfo().noticeTemplate())
				.leftOuterJoin(assembleInfo().noticegroupInfo()).getResultList();
		List<AssembleInfo> nolist = new ArrayList<AssembleInfo>();
		for (AssembleInfo info : reslist) {
			if (info.localgovInfo == null || info.stationclassInfo == null ||
					info.noticeTemplate == null || info.noticegroupInfo == null)
				nolist.add(info);
		}
		return nolist;
	}
}
