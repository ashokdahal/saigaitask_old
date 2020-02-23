/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.stationclassInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
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
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerData;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.StationalarmInfo;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class StationclassInfoService extends AbstractService<StationclassInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public StationclassInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 自治体IDで検索
     * @param govid
     * @return リスト
     */
    public List<StationclassInfo> findByLocalgovInfoId(Long govid) {
    	return select().where(
    			eq(stationclassInfo().localgovinfoid(), govid)
    			).orderBy(asc(stationclassInfo().disporder())).getResultList();
    }

    /**
     * 自治体IDと体制区分名で検索
     * @param govid
     * @return リスト
     */
    public StationclassInfo findByLocalgovInfoIdAndName(Long govid, String name) {
    	return select().where(
    			eq(stationclassInfo().localgovinfoid(), govid)
    			,eq(stationclassInfo().name(), name)
    			).orderBy(asc(stationclassInfo().disporder())).limit(1).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(stationclassInfo().stationMaster())
			//.innerJoin(stationclassInfo().disasterMaster())
			.innerJoin(stationclassInfo().localgovInfo())
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
	public List<StationclassInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = stationclassInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!stationclassInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(stationclassInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(stationclassInfo().stationMaster())
			//.innerJoin(stationclassInfo().disasterMaster())
			.innerJoin(stationclassInfo().localgovInfo())
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
		conditions.put(stationclassInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<StationclassInfo> list = findByCondition(conditions, stationclassInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(StationclassInfo entity, PropertyName<?>[] excludes) {
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
	 * 自治体IDに紐付く体制区分を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
    public List<StationclassInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<StationclassInfo> select = select();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(stationclassInfo().localgovinfoid(), localgovinfoid)
					);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(stationclassInfo().disporder()), asc(stationclassInfo().id()));
		return select.getResultList();
	}
    
    public List<StationclassInfo> check() {
    	List<StationclassInfo> reslist = select().leftOuterJoin(stationclassInfo().localgovInfo())
    			//.leftOuterJoin(stationclassInfo().disasterMaster())
    			.leftOuterJoin(stationclassInfo().stationMaster()).getResultList();
    	List<StationclassInfo> nolist = new ArrayList<StationclassInfo>();
    	for (StationclassInfo info : reslist) {
    		if (info.localgovInfo == null || /*info.disasterMaster == null ||*/ info.stationMaster == null)
    			nolist.add(info);
    	}
    	return nolist;
    }

	@Override
	public DeleteCascadeResult deleteCascade(StationclassInfo entity, DeleteCascadeResult result) {

		result.cascade(JalerttriggerData.class, Names.jalerttriggerData().stationclassinfoid(), entity.id);
		result.cascade(MeteotriggerData.class, Names.meteotriggerData().stationclassinfoid(), entity.id);
		result.cascade(AssembleInfo.class, Names.assembleInfo().stationclassinfoid(), entity.id);
		result.cascade(JalerttriggerInfo.class, Names.jalerttriggerInfo().stationclassinfoid(), entity.id);
		result.cascade(MeteotriggerInfo.class, Names.meteotriggerInfo().stationclassinfoid(), entity.id);
		result.cascade(StationalarmInfo.class, Names.stationalarmInfo().stationclassinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
