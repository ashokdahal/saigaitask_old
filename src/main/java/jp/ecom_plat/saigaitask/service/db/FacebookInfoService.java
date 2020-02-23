/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.facebookInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;

import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Group;
import facebook4j.ResponseList;
import facebook4j.User;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.FacebookInfo;
import jp.ecom_plat.saigaitask.entity.db.FacebookMaster;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * {@link FacebookInfo}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/08/29 18:39:19")
@org.springframework.stereotype.Repository
public class FacebookInfoService extends AbstractService<FacebookInfo> {

	@Resource
	protected FacebookMasterService facebookMasterService;

    /**
     * 識別子でエンティティを検索します。
     *
     * @param id
     *            識別子
     * @return エンティティ
     */
    public FacebookInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    public FacebookInfo findByLocalgovInfoId(long localgovinfoid) {
    	return select().where(Operations.eq(Names.facebookInfo().localgovinfoid(), localgovinfoid)).limit(1).getSingleResult();
    }

    /**
	 * Facebook投稿
	 * @param localgovinfoid
	 * @param facebookContent
	 * @throws FacebookException
	 */
	public void postMessage(Long localgovinfoid, String facebookContent) throws FacebookException {
		FacebookMaster facebookMaster = facebookMasterService.find();
		if (facebookMaster == null) return;
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		if (facebookInfo == null) return;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		// 投稿メッセージ（件名＋本文）
		String message = facebookContent;

		// Facebookホーム投稿
			facebook.postStatusMessage(message);
	}


    /**
	 * Facebookページ投稿
	 * @param account
	 * @param localgovinfoid
	 * @param facebookContent
	 * @throws FacebookException
	 */
	public void postMessageAccount( List<String>account, Long localgovinfoid, String facebookContent) throws FacebookException {
		FacebookMaster facebookMaster = facebookMasterService.find();
		if (facebookMaster == null) return;
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		if (facebookInfo == null) return;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		// 投稿メッセージ（件名＋本文）
		String message = facebookContent;

		// Facebookページ投稿
			for (String pageid : account){
				facebook.postStatusMessage(pageid, message);
			}
	}

    /**
	 * Facebookグループ投稿
	 * @param group
	 * @param localgovinfoid
	 * @param facebookContent
	 * @throws FacebookException
	 */
	public void postMessageGroup(List<String>group ,Long localgovinfoid, String facebookContent) throws FacebookException {
		FacebookMaster facebookMaster = facebookMasterService.find();
		if (facebookMaster == null) return;
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		if (facebookInfo == null) return;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		// 投稿メッセージ（件名＋本文）
		String message = facebookContent;


		// Facebookグループ投稿
			for (String pageid : group){
				facebook.postStatusMessage(pageid, message);
			}
	}


    /**
	 * Facebookユーザー情報取得
	 * @param localgovinfoid
	 * @throws Exception
	 * @return Facebookユーザー情報
	 */
	public User getUsername(Long localgovinfoid) throws Exception {
		FacebookMaster facebookMaster = facebookMasterService.find();
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		if (facebookMaster == null) return null;
		if (facebookInfo == null) return null;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		//ユーザーID取得
		String userid = facebook.getMe().getId();
		//ユーザーネーム取得
		User username = facebook.getUser(userid);
		System.out.println(username);
		return username;
	}


    /**
	 * FacebookユーザーID取得
	 * @param localgovinfoid
	 * @throws Exception
	 * @return FacebookユーザーID
	 */
	public String getId(Long localgovinfoid) throws Exception {
		FacebookMaster facebookMaster = facebookMasterService.find();
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		if (facebookMaster == null) return null;
		if (facebookInfo == null) return null;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		//ユーザーの取得
		String userid = facebook.getMe().getId();
		return userid;
	}


    /**
	 * ログイン中の自治体に紐付いたFacebookアカウントが所属するFacebookグループ一覧を返す
	 * @param localgovinfoid
	 * @throws Exception
	 * @return Facebookグループリスト
	 */
	public List<Group> getGroup(Long localgovinfoid) throws Exception {
		FacebookMaster facebookMaster = facebookMasterService.find();
		//if (facebookMaster == null) return;
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		//if (facebookInfo == null) return;
		if (facebookMaster == null) return null;
		if (facebookInfo == null) return null;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		//ユーザーの取得
		String userid = facebook.getMe().getId();

		//グループの取得
		ResponseList <Group> responselist = facebook.getGroups(userid);
		return responselist;
	}

    /**
	 * ログイン中の自治体に紐付いたFacebookアカウント情報を返す(Facebookページ情報も返します)
	 * @param localgovinfoid
	 * @throws Exception
	 * @return Facebookアカウント情報(Facebookページ情報も返します)
	 */
	public List<Account> getAccount(Long localgovinfoid) throws Exception {
		FacebookMaster facebookMaster = facebookMasterService.find();
		//if (facebookMaster == null) return;
		FacebookInfo facebookInfo = findByLocalgovInfoId(localgovinfoid);
		//if (facebookInfo == null) return;
		if (facebookMaster == null) return null;
		if (facebookInfo == null) return null;

		String appId = facebookMaster.appid;
		String appSecret = facebookMaster.appsecret;
		String permissions = "email,publish_stream";
		String accessToken = facebookInfo.accesstoken;

		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions(permissions);
		facebook.setOAuthAccessToken( new facebook4j.auth.AccessToken(accessToken));

		//ユーザーの取得
		String userid = facebook.getMe().getId();

		//アカウント情報の取得
		ResponseList<Account> responselist = facebook.getAccounts(userid);
		return responselist;
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(facebookInfo().localgovInfo())
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
	public List<FacebookInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = facebookInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!facebookInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(facebookInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return select()
			.innerJoin(facebookInfo().localgovInfo())
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
	public int update(FacebookInfo entity, PropertyName<?>[] excludes) {
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

	public List<FacebookInfo> check() {
		List<FacebookInfo> reslist = select().leftOuterJoin(facebookInfo().localgovInfo()).getResultList();
		List<FacebookInfo> nolist = new ArrayList<FacebookInfo>();
		for (FacebookInfo info : reslist) {
			if (info.localgovInfo == null)
				nolist.add(info);
		}
		return nolist;
	}
}
