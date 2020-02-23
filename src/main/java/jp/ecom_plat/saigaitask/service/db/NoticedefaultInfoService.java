/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticedefaultInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AlarmdefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostdefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.FacebookpostdefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class NoticedefaultInfoService extends AbstractService<NoticedefaultInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return 通知デフォルト情報
	 */
    public NoticedefaultInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * プロセスIDで検索
     * @param pid
     * @return 通知デフォルト情報
     */
    public NoticedefaultInfo findByMenuInfoId(Long mid) {
    	return select().where(
    			eq(noticedefaultInfo().menuinfoid(), mid))
    			.limit(1).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.leftOuterJoin(noticedefaultInfo().noticetemplatetypeMaster())
			.innerJoin(noticedefaultInfo().menuInfo())
			//.innerJoin(noticedefaultInfo().localgovInfo())
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
	public List<NoticedefaultInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = noticedefaultInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!noticedefaultInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(noticedefaultInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			.leftOuterJoin(noticedefaultInfo().noticetemplatetypeMaster())
			.innerJoin(noticedefaultInfo().menuInfo())
			//.innerJoin(noticedefaultInfo().localgovInfo())
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
	public int update(NoticedefaultInfo entity, PropertyName<?>[] excludes) {
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
	
	public List<NoticedefaultInfo> check() {
		List<NoticedefaultInfo> reslist = select()//.innerJoin(noticedefaultInfo().localgovInfo())
				.innerJoin(noticedefaultInfo().menuInfo())
				.innerJoin(noticedefaultInfo().noticetemplatetypeMaster()).getResultList();
		List<NoticedefaultInfo> nolist = new ArrayList<NoticedefaultInfo>();
		for (NoticedefaultInfo def : reslist) {
			if (/*def.localgovInfo == null || */def.menuInfo == null || def.noticetemplatetypeMaster == null)
				nolist.add(def);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(NoticedefaultInfo entity, DeleteCascadeResult result) {

		result.cascade(AlarmdefaultgroupInfo.class, Names.alarmdefaultgroupInfo().noticedefaultinfoid(), entity.id);
		result.cascade(EcomgwpostdefaultInfo.class, Names.ecomgwpostdefaultInfo().noticedefaultinfoid(), entity.id);
		result.cascade(FacebookpostdefaultInfo.class, Names.facebookpostdefaultInfo().noticedefaultinfoid(), entity.id);
		result.cascade(NoticedefaultgroupInfo.class, Names.noticedefaultgroupInfo().noticedefaultinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
