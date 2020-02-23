/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticegroupInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerData;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgenoticeInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeaddressInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupaddressInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupuserInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.MailUtil;

@org.springframework.stereotype.Repository
public class NoticegroupInfoService extends AbstractService<NoticegroupInfo> {

	@Resource
	protected LocalgovInfoService localgovInfoService;
	@Resource
	protected NoticegroupuserInfoService noticegroupuserInfoService;
	@Resource
	protected NoticegroupaddressInfoService noticegroupaddressInfoService;

	/**
	 * IDで検索
	 * @param id ID
	 * @return IDに対応唯一のレコード
	 */
	public NoticegroupInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 自治体IDで検索
	 * @param govid 自治体ID
	 * @return 検索結果のリスト
	 */
	public List<NoticegroupInfo> findByLocalgovInfoId(Long govid) {
		return select().where(
				eq(noticegroupInfo().localgovinfoid(), govid)
				).orderBy(asc(noticegroupInfo().disporder())).getResultList();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(noticegroupInfo().localgovInfo())
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
	public List<NoticegroupInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = noticegroupInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!noticegroupInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(noticegroupInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(noticegroupInfo().localgovInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}

	/**
	 * 表示順最大値を取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
	public int getLargestDisporder(Map<String, Object> conditions) {
		conditions.put(noticegroupInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<NoticegroupInfo> list = findByCondition(conditions, noticegroupInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}

	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(NoticegroupInfo entity, PropertyName<?>[] excludes) {
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

	/**
	 * メールグループへメール送信
	 * @param grpids
	 * @param smtp
	 * @param from
	 * @param title
	 * @param content
	 * @return ret[0]:成功,true,失敗,false
	 * 			ret[1]:メールアドレス
	 */
	public Object[] sendMailToNoticeGroups(Long govid, List<Long> grpids, String addto, String title, String content)
	{
		return sendAttachMailToNoticeGroups(govid, grpids, addto, title, content, null, null);
	}

	/**
	 * メールグループへ添付ファイル付きメール送信
	 * @param govid
	 * @param grpids
	 * @param title
	 * @param content
	 * @param formFiles
	 * @param dirPath
	 * @return
	 */
	public Object[] sendAttachMailToNoticeGroups(Long govid, List<Long> grpids, String addto, String title, String content, MultipartFile[] formFiles, String dirPath)
	{
		//メール送信用
		LocalgovInfo localgovInfo = localgovInfoService.findById(govid);
		String smtp = null;
		if (localgovInfo.smtp != null) smtp = localgovInfo.smtp;
		String from = null;
		if (localgovInfo.email != null) from = localgovInfo.email;

		//通知データへの保存用
		StringBuffer mailto = new StringBuffer();
		boolean bsend = true;

		//通知ユーザの送信先のリストを取得
		Map<Long, UserInfo> userMap = new HashMap<Long, UserInfo>();
		Map<Long, NoticeaddressInfo> addrMap = new HashMap<Long, NoticeaddressInfo>();
		for (Long grpid : grpids) {
			//NoticegroupInfo noticegroupInfo = noticegroupInfoService.findById(Long.valueOf(grpid));

			//送信ユーザ
			List<NoticegroupuserInfo> guserList = noticegroupuserInfoService.findUserInfoByNoticegroupInfoId(grpid);
			for (NoticegroupuserInfo guserInfo : guserList) {
				if (!userMap.containsKey(guserInfo.userInfo.id))
					userMap.put(guserInfo.userid, guserInfo.userInfo);
			}
			//通知先
			List<NoticegroupaddressInfo> gaddrList = noticegroupaddressInfoService.findNoticeaddressInfoByNoticegroupInfoId(grpid);
			for (NoticegroupaddressInfo gaddrInfo : gaddrList) {
				if (!addrMap.containsKey(gaddrInfo.noticeaddressinfoid))
					addrMap.put(gaddrInfo.noticeaddressinfoid, gaddrInfo.noticeaddressInfo);
			}
		}

		// 添付ファイルの有無チェック
		boolean attachFlg = false;
		if (formFiles != null) {
			for (MultipartFile file : formFiles) {
					if (file != null) {
						if(!"".equals(file.getOriginalFilename())) {
							attachFlg = true;
							break;
						}
					}
			}
		}

		//職員へメール送信
		Iterator<Long> uit = userMap.keySet().iterator();
		while (uit.hasNext()) {
			UserInfo userInfo = userMap.get(uit.next());
			if (StringUtil.isNotEmpty(userInfo.email) || StringUtil.isNotEmpty(userInfo.mobilemail)) {
				String to = "";
				if (StringUtil.isNotEmpty(userInfo.email))
					to += userInfo.email;
				if (StringUtil.isNotEmpty(to) && StringUtil.isNotEmpty(userInfo.mobilemail))
					to += ",";
				if (StringUtil.isNotEmpty(userInfo.mobilemail))
					to += userInfo.mobilemail;
				try {
					if (!attachFlg)
						MailUtil.sendMail(smtp, from, to, null, null, title, content);
					else
						MailUtil.sendMailAttach(smtp, from, to, null, null, title, content, formFiles, dirPath);

					if (mailto.length() != 0) mailto.append(",");
					mailto.append(to);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("MailUtil.sendMail", e);
					bsend = false;
				}
			}
		}

		//通知先へメール送信
		Iterator<Long> ait = addrMap.keySet().iterator();
		while (ait.hasNext()) {
			NoticeaddressInfo addrInfo = addrMap.get(ait.next());
			if (StringUtil.isNotEmpty(addrInfo.email) || StringUtil.isNotEmpty(addrInfo.mobilemail)) {
				String to = "";
				if (StringUtil.isNotEmpty(addrInfo.email))
					to += addrInfo.email;
				if (StringUtil.isNotEmpty(to) && StringUtil.isNotEmpty(addrInfo.mobilemail))
					to += ",";
				if (StringUtil.isNotEmpty(addrInfo.mobilemail))
					to += addrInfo.mobilemail;
				try {
					if (!attachFlg)
						MailUtil.sendMail(smtp, from, to, null, null, title, content);
					else
						MailUtil.sendMailAttach(smtp, from, to, null, null, title, content, formFiles, dirPath);

					if (mailto.length() != 0) mailto.append(",");
					mailto.append(to);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("MailUtil.sendMail", e);
					bsend = false;
				}
			}
		}

		//追加送信
		if (StringUtil.isNotBlank(addto)) {
			try {
				String tos[] = addto.split(",");
				for (String to : tos) {
					if (!attachFlg)
						MailUtil.sendMail(smtp, from, to, null, null, title, content);
					else
						MailUtil.sendMailAttach(smtp, from, to, null, null, title, content, formFiles, dirPath);
					if (mailto.length() != 0) mailto.append(",");
					mailto.append(to);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("MailUtil.sendMail", e);
				bsend = false;
			}
		}

		return new Object[]{Boolean.valueOf(bsend), mailto.toString()};
	}

	/**
	 * 自治体IDに紐付く通知グループ情報を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
	public List<NoticegroupInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<NoticegroupInfo> select = select();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(noticegroupInfo().localgovinfoid(), localgovinfoid)
					);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(noticegroupInfo().disporder()), asc(noticegroupInfo().id()));
		return select.getResultList();
	}

	public List<NoticegroupInfo> check() {
		List<NoticegroupInfo> reslist = select().innerJoin(noticegroupInfo().localgovInfo()).getResultList();
		List<NoticegroupInfo> nolist = new ArrayList<NoticegroupInfo>();
		for (NoticegroupInfo grp : reslist) {
			if (grp.localgovInfo == null)
				nolist.add(grp);
		}
		return nolist;
	}


	@Override
	public DeleteCascadeResult deleteCascade(NoticegroupInfo entity, DeleteCascadeResult result) {

		result.cascade(JalerttriggerData.class, Names.jalerttriggerData().noticegroupinfoid(), entity.id);
		result.cascade(MeteotriggerData.class, Names.meteotriggerData().noticegroupinfoid(), entity.id);
		result.cascade(NoticemailsendData.class, Names.noticemailsendData().noticegroupinfoid(), entity.id);
		result.cascade(AssembleInfo.class, Names.assembleInfo().noticegroupinfoid(), entity.id);
		result.cascade(JalerttriggerInfo.class, Names.jalerttriggerInfo().noticegroupinfoid(), entity.id);
		result.cascade(JudgenoticeInfo.class, Names.judgenoticeInfo().noticegroupinfoid(), entity.id);
		result.cascade(MeteotriggerInfo.class, Names.meteotriggerInfo().noticegroupinfoid(), entity.id);
		result.cascade(NoticedefaultgroupInfo.class, Names.noticedefaultgroupInfo().noticegroupinfoid(), entity.id);
		result.cascade(NoticegroupaddressInfo.class, Names.noticegroupaddressInfo().noticegroupinfoid(), entity.id);
		result.cascade(NoticegroupuserInfo.class, Names.noticegroupuserInfo().noticegroupinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
