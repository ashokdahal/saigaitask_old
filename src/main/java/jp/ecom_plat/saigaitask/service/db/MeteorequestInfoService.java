/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.meteorequestInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.DemoInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoData;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MeteorequestInfoService extends AbstractService<MeteorequestInfo> {

	public MeteorequestInfo findById(Long id) {
		return select().id(id)
				.leftOuterJoin(meteorequestInfo().meteotypeMaster())
				.getSingleResult();
	}

	/**
	 * 有効なものを検索
	 * @return 判定管理情報リスト
	 */
	public List<MeteorequestInfo> findByValid() {
		return select()
				.leftOuterJoin(meteorequestInfo().meteotypeMaster())
				.where(
				eq(meteorequestInfo().valid(), true)
				).orderBy(asc(meteorequestInfo().id())).getResultList();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(meteorequestInfo().meteotypeMaster())
			.innerJoin(meteorequestInfo().localgovInfo())
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
	public List<MeteorequestInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = meteorequestInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!meteorequestInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(meteorequestInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			.innerJoin(meteorequestInfo().meteotypeMaster())
			.innerJoin(meteorequestInfo().localgovInfo())
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
	public int update(MeteorequestInfo entity, PropertyName<?>[] excludes) {
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
	
	public List<MeteorequestInfo> check() {
		List<MeteorequestInfo> reslist = select().leftOuterJoin(meteorequestInfo().localgovInfo())
				.leftOuterJoin(meteorequestInfo().meteotypeMaster()).getResultList();
		List<MeteorequestInfo> nolist = new ArrayList<MeteorequestInfo>();
		for (MeteorequestInfo info : reslist) {
			if (info.localgovInfo == null || info.meteotypeMaster == null)
				nolist.add(info);
		}
		return nolist;
	}
	
	/**
	 * 特定のMeteotypeID、有効なものを検索
	 * @return 判定管理情報リスト
	 */
	public List<MeteorequestInfo> findByMeteoTypeIDValid(int meteoTypeID) {
		return select().where(
				eq(meteorequestInfo().valid(), true),
				eq(meteorequestInfo().meteotypeid(), meteoTypeID)
				).orderBy(asc(meteorequestInfo().id())).getResultList();
	}


	@Override
	public DeleteCascadeResult deleteCascade(MeteorequestInfo entity, DeleteCascadeResult result) {

		result.cascade(MeteoData.class, Names.meteoData().meteorequestinfoid(), entity.id);
		result.cascade(DemoInfo.class, Names.demoInfo().meteorequestinfoid(), entity.id);
		result.cascade(MeteotriggerInfo.class, Names.meteotriggerInfo().meteorequestinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}