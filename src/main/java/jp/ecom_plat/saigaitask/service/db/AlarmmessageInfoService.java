/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.alarmmessageInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgealarmInfo;
import jp.ecom_plat.saigaitask.entity.db.StationalarmInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class AlarmmessageInfoService extends AbstractService<AlarmmessageInfo> {

    public AlarmmessageInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(alarmmessageInfo().alarmtypeMaster())
			.innerJoin(alarmmessageInfo().groupInfo(), ne(alarmmessageInfo().groupInfo().deleted(), true))
			.innerJoin(alarmmessageInfo().localgovInfo())
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
	public List<AlarmmessageInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = alarmmessageInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!alarmmessageInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(alarmmessageInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(alarmmessageInfo().alarmtypeMaster())
			.innerJoin(alarmmessageInfo().groupInfo(), ne(alarmmessageInfo().groupInfo().deleted(), true))
			.innerJoin(alarmmessageInfo().localgovInfo())
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
		conditions.put(alarmmessageInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<AlarmmessageInfo> list = findByCondition(conditions, alarmmessageInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(AlarmmessageInfo entity, PropertyName<?>[] excludes) {
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
	 * 自治体IDに紐付くアラームメッセージ設定情報を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
    public List<AlarmmessageInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<AlarmmessageInfo> select = select();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(alarmmessageInfo().localgovinfoid(), localgovinfoid)
					);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(alarmmessageInfo().disporder()), asc(alarmmessageInfo().id()));
		return select.getResultList();
	}
    
    public List<AlarmmessageInfo> check() {
    	List<AlarmmessageInfo> reslist = select().innerJoin(alarmmessageInfo().localgovInfo())
    			.innerJoin(alarmmessageInfo().groupInfo())
    			.innerJoin(alarmmessageInfo().alarmtypeMaster()).getResultList();
    	List<AlarmmessageInfo> nolist = new ArrayList<AlarmmessageInfo>();
    	for (AlarmmessageInfo msg : reslist) {
    		if (msg.localgovInfo == null || msg.groupInfo == null || msg.alarmtypeMaster == null)
    			nolist.add(msg);
    	}
    	return reslist;
    }

	@Override
	public DeleteCascadeResult deleteCascade(AlarmmessageInfo entity, DeleteCascadeResult result) {

		result.cascade(AlarmmessageData.class, Names.alarmmessageData().alarmmessageinfoid(), entity.id);
		result.cascade(JudgealarmInfo.class, Names.judgealarmInfo().alarmmessageinfoid(), entity.id);
		result.cascade(StationalarmInfo.class, Names.stationalarmInfo().alarmmessageinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
