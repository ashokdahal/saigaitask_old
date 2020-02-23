/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.menuloginInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;
import static org.seasar.extension.jdbc.operation.Operations.or;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MenuloginInfoService extends AbstractService<MenuloginInfo> {

	/** サービスクラス */
	@Resource
	protected MenuprocessInfoService menuprocessInfoService;

	/**
	 * IDで検索
	 * @param id
	 * @return メニューログイン情報
	 */
    public MenuloginInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * IDで検索
	 * @param id
	 * @return 検索結果件数
	 */
    public int getCountById(Long id) {
        return (int)selectNotDeleted().id(id).getCount();
    }

    /**
	 * deleted = false の条件付きでid検索
	 * @param id
	 * @return メニューログイン情報
	 */
    public MenuloginInfo findByNotDeletedId(Long id) {
        return selectNotDeleted().id(id).getSingleResult();
    }

    /**
     * 自治体IDで削除されていないものを検索
     * @param localgovinfoid 自治体ID
     * @return 削除されていないメニューログイン情報
     */
    public List<MenuloginInfo> findByLocalgovinfoAndNotDeleted(Long localgovinfoid) {
        return select()
        		.innerJoin(menuloginInfo().groupInfo())
        		.where(
        				eq(menuloginInfo().groupInfo().localgovinfoid(), localgovinfoid),
        				ne(menuloginInfo().deleted(), true)
        		)
        		.getResultList();
    }

    /**
     * 災害種別と班IDから検索
     * @param did
     * @param gid
     * @return メニューログイン情報
     */
    /*public MenuloginInfo findByDisasterIdAndGroupInfoIdOrZero(Integer did, Long gid) {
    	MenuloginInfo login = select().where(
    			and(
    				eq(menuloginInfo().disasterid(), did),
    				eq(menuloginInfo().groupid(), gid),
    				ne(menuloginInfo().deleted(), true)
    			)).limit(1).getSingleResult();
    	if (login == null) {
    		login = select().where(
        			and(
            			eq(menuloginInfo().disasterid(), did),
            			eq(menuloginInfo().groupid(), 0L),
            			ne(menuloginInfo().deleted(), true)
            		)).limit(1).getSingleResult();
    	}
    	return login;
    }*/

    /**
     * 課IDから検索
     * @param uid
     * @return メニューログイン情報
     */
    public MenuloginInfo findByUnitInfoIdAndNotDeleted(Long uid) {
    	return select().where(
    			and(
    				eq(menuloginInfo().unitid(), uid),
    				eq(menuloginInfo().deleted(), false)
    			)).limit(1).getSingleResult();
    }

    /**
     * 災害種別と班IDから検索
     * @param did
     * @param gid
     * @return メニューログイン情報
     * @deprecated
     */
    public MenuloginInfo findByDisasterIdAndGroupInfoIdAndNotDeleted(Integer did, Long gid) {
    	return select().where(
    			and(
    				eq(menuloginInfo().disasterid(), did),
    				eq(menuloginInfo().groupid(), gid),
        			ne(menuloginInfo().deleted(), true)
    			)).limit(1).getSingleResult();
    }

    /**
     * 班IDから検索
     * @param gid
     * @return メニューログイン情報
     */
    public MenuloginInfo findByGroupInfoIdAndNotDeleted(Long gid) {
    	return select().where(
    			and(
    				eq(menuloginInfo().groupid(), gid),
        			ne(menuloginInfo().deleted(), true)
    			)).limit(1).getSingleResult();
    }

    /**
     * 災害種別と班IDから検索
     * @param did
     * @param gid
     * @return メニューログイン情報
     */
    public List<MenuloginInfo> findByDisasterIdAndGroupInfoId(Integer did, Long gid) {
    	return select().where(
    			and(
    				eq(menuloginInfo().disasterid(), did),
    				eq(menuloginInfo().groupid(), gid),
        			ne(menuloginInfo().deleted(), true)
    			)).getResultList();
    }

    /**
     * 課IDから検索
     * @param did
     * @param uid
     * @return メニューログイン情報
     */
    public List<MenuloginInfo> findByUnitInfoId(Long uid) {
    	return select().where(
    			and(
    				eq(menuloginInfo().unitid(), uid),
        			ne(menuloginInfo().deleted(), true)
    			)).getResultList();
    }


	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。班IDが空欄 or 課IDが空欄を考慮する。
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @param localgovinfoid ログイン自治体ID
	 * @param isConditions 従来通りconditionsを利用する場合 true, それ以外 false
	 * @return 検索結果
	 */
	public List<MenuloginInfo> findByConditionWithGroupUnit(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset,Long localgovinfoid, boolean isConditions) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menuloginInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menuloginInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menuloginInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		if(isConditions){
			return selectNotDeleted()
				.innerJoin(menuloginInfo().disasterMaster())
			    .leftOuterJoin(menuloginInfo().groupInfo())
			    .leftOuterJoin(menuloginInfo().unitInfo())
				.leftOuterJoin(menuloginInfo().unitInfo().localgovInfo())
				.leftOuterJoin(menuloginInfo().groupInfo().localgovInfo())
				.where(conditions)
				.orderBy(orderByItems)
				.limit(limit)
				.offset(offset)
				.getResultList();
		}else{
			return select()
				.innerJoin(menuloginInfo().disasterMaster())
				.leftOuterJoin(menuloginInfo().groupInfo())
				.leftOuterJoin(menuloginInfo().unitInfo())
				.leftOuterJoin(menuloginInfo().unitInfo().localgovInfo())
				.leftOuterJoin(menuloginInfo().groupInfo().localgovInfo())
				//.where(conditions)
				.where(
				  and(
					or(
						eq(menuloginInfo().groupInfo().localgovinfoid(), localgovinfoid),
						eq(menuloginInfo().unitInfo().localgovinfoid(), localgovinfoid)
					),
						eq(menuloginInfo().deleted(), false)
					)
				)
				.orderBy(orderByItems)
				.limit(limit)
				.offset(offset)
				.getResultList();
		}
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。班IDが空欄 or 課IDが空欄を考慮する。
	 * @param conditions 検索条件マップ
	 * @param localgovinfoid ログイン自治体ID
	 * @param isConditions 従来通りconditionsを利用する場合 true, それ以外 false
	 * @return 検索結果件数
	 */
	public int getCountWithGroupUnit(Map<String, Object> conditions,Long localgovinfoid, boolean isConditions) {
		if(isConditions){
			return (int)selectNotDeleted()
					.innerJoin(menuloginInfo().disasterMaster())
		        	.leftOuterJoin(menuloginInfo().groupInfo())
		        	.leftOuterJoin(menuloginInfo().unitInfo())
					.leftOuterJoin(menuloginInfo().unitInfo().localgovInfo())
					.leftOuterJoin(menuloginInfo().groupInfo().localgovInfo())
					.where(conditions)
					.getCount();
		}else{
			return (int)selectNotDeleted()
					.innerJoin(menuloginInfo().disasterMaster())
					.leftOuterJoin(menuloginInfo().groupInfo())
					.leftOuterJoin(menuloginInfo().unitInfo())
					.leftOuterJoin(menuloginInfo().unitInfo().localgovInfo())
					.leftOuterJoin(menuloginInfo().groupInfo().localgovInfo())
					//.where(conditions)
					.where(
							  and(
										or(
											eq(menuloginInfo().groupInfo().localgovinfoid(), localgovinfoid),
											eq(menuloginInfo().unitInfo().localgovinfoid(), localgovinfoid)
										),
											eq(menuloginInfo().deleted(), false)
							)
					)
					.getCount();
		}
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(menuloginInfo().disasterMaster())
			.innerJoin(menuloginInfo().groupInfo())
			.innerJoin(menuloginInfo().groupInfo().localgovInfo())
			.where(conditions)
			.getCount();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。（平時）
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCountForUnit(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(menuloginInfo().disasterMaster())
			.innerJoin(menuloginInfo().unitInfo())
			.innerJoin(menuloginInfo().unitInfo().localgovInfo())
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
	public List<MenuloginInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menuloginInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menuloginInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menuloginInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return selectNotDeleted()
			.innerJoin(menuloginInfo().disasterMaster())
			.innerJoin(menuloginInfo().groupInfo())
			.innerJoin(menuloginInfo().groupInfo().localgovInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。（平時）
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @return 検索結果
	 */
	public List<MenuloginInfo> findByConditionForUnit(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menuloginInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menuloginInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menuloginInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return selectNotDeleted()
			.innerJoin(menuloginInfo().disasterMaster())
			.innerJoin(menuloginInfo().unitInfo())
			.innerJoin(menuloginInfo().unitInfo().localgovInfo())
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
	public int update(MenuloginInfo entity, PropertyName<?>[] excludes) {
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

	public List<MenuloginInfo> check() {
		List<MenuloginInfo> reslist = select().innerJoin(menuloginInfo().groupInfo())
				.innerJoin(menuloginInfo().disasterMaster()).getResultList();
		List<MenuloginInfo> nolist = new ArrayList<MenuloginInfo>();
		for (MenuloginInfo  menu : reslist) {
			if (menu.groupInfo == null || menu.disasterMaster == null)
				nolist.add(menu);
		}
		return reslist;
	}

	public int deleteLogically(MenuloginInfo entity) throws Exception {
		menuprocessInfoService.deleteLogicallyBySimpleWhere(MenuprocessInfoNames.menulogininfoid(), entity.id);
        return super.deleteLogically(entity);
    }

	/**
	 * 班のメニュー階層の設定で使用されている災害種別IDを取得する
	 * @param localgovInfoId
	 * @return
	 */
	public HashSet<Integer> getUsingDisasterIds(long localgovInfoId) {
		HashSet<Integer> usingDisasterids = new HashSet<Integer>();
		List<MenuloginInfo> menuloginInfos = findByLocalgovinfoAndNotDeleted(localgovInfoId);
		if(0<menuloginInfos.size()) {
			// メニュー設定情報で使用中の disastermasterid を取得
			for(MenuloginInfo menuloginInfo : menuloginInfos) {
				usingDisasterids.add(menuloginInfo.disasterid);
			}
		}
		return usingDisasterids;
	}

	@Override
	public DeleteCascadeResult deleteCascade(MenuloginInfo entity, DeleteCascadeResult result) {

		result.cascade(MenuprocessInfo.class, Names.menuprocessInfo().menulogininfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}

	@Override
	public List<MenuloginInfo> findByLocalgovinfoid(Long localgovinfoid, boolean deleted) {
		// メニュー階層は localgovinfoid を持たない。
		// V1では 班情報 でメニュー階層を設定していたが、
		// V2では 報情報 または 課情報 でメニュー階層を設定するようになった。
		// 班・課の両方で自治体検索をかけるためにオーバーライド
		return select()
				.leftOuterJoin(menuloginInfo().groupInfo())
				.leftOuterJoin(menuloginInfo().unitInfo())
				.where(or(
						eq(menuloginInfo().groupInfo().localgovinfoid(), localgovinfoid),
						eq(menuloginInfo().unitInfo().localgovinfoid(), localgovinfoid)
						))
				.getResultList();
	}
}
