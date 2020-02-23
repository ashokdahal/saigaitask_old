/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.menutableInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MenutableInfoService extends AbstractService<MenutableInfo> {

	/**
	 * IDから検索
	 * @param id
	 * @return
	 */
    public MenutableInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
	 * deleted = false の条件付きでid検索
	 * @return MenuInfo
	 */
	public MenutableInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().id(id).getSingleResult();
	}

    /**
     * メニューIDから検索
     * @param mid メニューID
     * @return メニューテーブルリスト
     */
    public List<MenutableInfo> findByMenuInfoId(Long mid) {
    	return select()
    			.innerJoin(menutableInfo().tablemasterInfo(), ne(menutableInfo().tablemasterInfo().deleted(), true))
    			.where(
    				and(
    					eq(menutableInfo().menuinfoid(), mid),
    					ne(menutableInfo().deleted(), true)
    				)
    		).getResultList();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(menutableInfo().tablemasterInfo())
			.innerJoin(menutableInfo().menuInfo())
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
	public List<MenutableInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menutableInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menutableInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menutableInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return selectNotDeleted()
			.innerJoin(menutableInfo().tablemasterInfo())
			.innerJoin(menutableInfo().menuInfo())
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
		conditions.put(menutableInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MenutableInfo> list = findByCondition(conditions, menutableInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(MenutableInfo entity, PropertyName<?>[] excludes) {
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

	public List<MenutableInfo> check() {
		List<MenutableInfo> reslist = select().innerJoin(menutableInfo().menuInfo())
				.innerJoin(menutableInfo().tablemasterInfo()).getResultList();
		List<MenutableInfo> nolist = new ArrayList<MenutableInfo>();
		for (MenutableInfo table : reslist) {
			if (table.menuInfo == null || table.tablemasterInfo == null)
				nolist.add(table);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(MenutableInfo entity, DeleteCascadeResult result) {

		result.cascade(TablelistcolumnInfo.class, Names.tablelistcolumnInfo().menutableinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
