/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.meteotriggerInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MeteotriggerInfoService extends AbstractService<MeteotriggerInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public MeteotriggerInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 気象情報取得IDで検索
     * @param rid
     * @return 気象情報トリガー情報
     */
    public MeteotriggerInfo findByMeteorequestInfoId(Long rid) {
    	return select().where(
    			eq(meteotriggerInfo().meteorequestinfoid(), rid)
    		).limit(1).getSingleResult();
    }
    
	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(meteotriggerInfo().stationclassInfo())
			.innerJoin(meteotriggerInfo().noticegroupInfo())
			.innerJoin(meteotriggerInfo().meteorequestInfo())
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
	public List<MeteotriggerInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = meteotriggerInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!meteotriggerInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(meteotriggerInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			.innerJoin(meteotriggerInfo().stationclassInfo())
			.innerJoin(meteotriggerInfo().noticegroupInfo())
			.innerJoin(meteotriggerInfo().meteorequestInfo())
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
	public int update(MeteotriggerInfo entity, PropertyName<?>[] excludes) {
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
	
	public List<MeteotriggerInfo> check() {
		List<MeteotriggerInfo> reslist = select().leftOuterJoin(meteotriggerInfo().meteorequestInfo()).getResultList();
		List<MeteotriggerInfo> nolist = new ArrayList<MeteotriggerInfo>();
		for (MeteotriggerInfo info : reslist) {
			if (info.meteorequestInfo == null)
				nolist.add(info);
		}
		reslist = select().leftOuterJoin(meteotriggerInfo().noticegroupInfo()).where(and(
				ne(meteotriggerInfo().noticegroupinfoid(), 0L),
				isNotNull(meteotriggerInfo().noticegroupinfoid()))).getResultList();
		for (MeteotriggerInfo info : reslist) {
			if (info.noticegroupInfo == null)
				nolist.add(info);
		}
		reslist = select().leftOuterJoin(meteotriggerInfo().stationclassInfo()).where(and(
				ne(meteotriggerInfo().stationclassinfoid(), 0L),
				isNotNull(meteotriggerInfo().stationclassinfoid()))).getResultList();
		for (MeteotriggerInfo info : reslist) {
			if (info.stationclassInfo == null)
				nolist.add(info);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(MeteotriggerInfo entity, DeleteCascadeResult result) {

		result.cascade(MeteotriggerData.class, Names.meteotriggerData().meteotriggerinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}