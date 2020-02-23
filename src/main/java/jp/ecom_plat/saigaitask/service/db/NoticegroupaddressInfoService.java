/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticegroupaddressInfo;
import static jp.ecom_plat.saigaitask.entity.Names.noticegroupuserInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.db.NoticegroupaddressInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class NoticegroupaddressInfoService extends AbstractService<NoticegroupaddressInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public NoticegroupaddressInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
	/**
	 * 通知グループIDで通知先情報も検索
	 * @param noticegroupinfoid
	 * @return 通知先のリスト
	 */
	public List<NoticegroupaddressInfo> findNoticeaddressInfoByNoticegroupInfoId(Long noticegroupinfoid) {
		return select().innerJoin(noticegroupaddressInfo().noticeaddressInfo()).where(
				and(
					eq(noticegroupuserInfo().noticegroupinfoid(), noticegroupinfoid)
				)).getResultList();
	}    
    
	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(noticegroupaddressInfo().noticeaddressInfo())
			.innerJoin(noticegroupaddressInfo().noticegroupInfo())
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
	public List<NoticegroupaddressInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = noticegroupaddressInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!noticegroupaddressInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(noticegroupaddressInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			.innerJoin(noticegroupaddressInfo().noticeaddressInfo())
			.innerJoin(noticegroupaddressInfo().noticegroupInfo())
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
	public int update(NoticegroupaddressInfo entity, PropertyName<?>[] excludes) {
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
	
	public List<NoticegroupaddressInfo> check() {
		List<NoticegroupaddressInfo> reslist = select().innerJoin(noticegroupaddressInfo().noticegroupInfo())
				.innerJoin(noticegroupaddressInfo().noticeaddressInfo()).getResultList();
		List<NoticegroupaddressInfo> nolist = new ArrayList<NoticegroupaddressInfo>();
		for (NoticegroupaddressInfo addr : reslist) {
			if (addr.noticegroupInfo == null || addr.noticeaddressInfo == null)
				nolist.add(addr);
		}
		return nolist;
	}
}
