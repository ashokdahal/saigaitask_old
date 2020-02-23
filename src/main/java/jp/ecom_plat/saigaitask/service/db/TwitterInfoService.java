/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.twitterInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.TwitterInfo;
import jp.ecom_plat.saigaitask.entity.db.TwitterMaster;
import jp.ecom_plat.saigaitask.service.AbstractService;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@org.springframework.stereotype.Repository
public class TwitterInfoService extends AbstractService<TwitterInfo> {

	@Resource
	protected TwitterMasterService twitterMasterService;

    public TwitterInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    public List<TwitterInfo> findByLocalgovInfoIdAll(long localgovinfoid) {
    	return select().where(
    			Operations.eq(Names.twitterInfo().localgovinfoid(), localgovinfoid)
    		).getResultList();
    }

    public TwitterInfo findByLocalgovInfoId(long localgovinfoid) {
    	return select().where(Operations.eq(Names.twitterInfo().localgovinfoid(), localgovinfoid)).limit(1).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(twitterInfo().localgovInfo())
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
	public List<TwitterInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = twitterInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!twitterInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(twitterInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(twitterInfo().localgovInfo())
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
	public int update(TwitterInfo entity, PropertyName<?>[] excludes) {
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
	 * twitter投稿
	 * @param localgovinfoid
	 * @param twitterTitle
	 * @param twitterContent
	 * @throws TwitterException
	 */
	//タイトル削除
//	public void postMessage(Long localgovinfoid, String twitterTitle, String twitterContent) throws TwitterException {
		public void postMessage(Long localgovinfoid, String twitterContent) throws TwitterException {
		TwitterMaster twitterMaster = twitterMasterService.find();
		if (twitterMaster == null) return;
		TwitterInfo twitterInfo = findByLocalgovInfoId(localgovinfoid);
		if (twitterInfo == null) return;

		String consumerKey = twitterMaster.consumerkey;
		String consumerSecret = twitterMaster.consumersecret;
		String accessToken = twitterInfo.accesstoken;
		String accessTokenSecret = twitterInfo.accesstokensecret;

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(consumerKey);
		cb.setOAuthConsumerSecret(consumerSecret);
		cb.setOAuthAccessToken(accessToken);
		cb.setOAuthAccessTokenSecret(accessTokenSecret);
		// twitter4j-core を 3 から 4 にアップデート
		//cb.setUseSSL(true);

		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		//タイトル削除
//		 投稿メッセージ（件名＋本文）
//		String message = twitterTitle + "\r\n" + twitterContent;
//		twitter.updateStatus(message);
//	}

		// 投稿メッセージ（本文）
		String message =  twitterContent;
		twitter.updateStatus(message);
	}

	public List<TwitterInfo> check() {
		List<TwitterInfo> reslist = select().leftOuterJoin(twitterInfo().localgovInfo()).getResultList();
		List<TwitterInfo> nolist = new ArrayList<TwitterInfo>();
		for (TwitterInfo info : reslist) {
			if (info.localgovInfo == null)
				nolist.add(info);
		}
		return nolist;
	}
}
