/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.tablelistcolumnInfo;
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
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablerowstyleInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class TablelistcolumnInfoService extends AbstractService<TablelistcolumnInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public TablelistcolumnInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 前検索
     * @return リスト
     */
    public List<TablelistcolumnInfo> findAllOrderBy() {
    	return select().orderBy(asc(tablelistcolumnInfo().menutableinfoid()), asc(tablelistcolumnInfo().disporder())).getResultList();
    }
    
    /**
     * メニューIDとテーブルIDで検索
     * @param mid メニューID
     * @param tid テーブルID
     * @return リスト
     */
    public List<TablelistcolumnInfo> findByMenutableInfoId(Long mtid) {
    	return select().where(
    			eq(tablelistcolumnInfo().menutableinfoid(), mtid)
    			).orderBy(asc(tablelistcolumnInfo().disporder())).getResultList();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(tablelistcolumnInfo().menutableInfo())
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
	public List<TablelistcolumnInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = tablelistcolumnInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!tablelistcolumnInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(tablelistcolumnInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			.innerJoin(tablelistcolumnInfo().menutableInfo())
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
		conditions.put(tablelistcolumnInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<TablelistcolumnInfo> list = findByCondition(conditions, tablelistcolumnInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(TablelistcolumnInfo entity, PropertyName<?>[] excludes) {
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
	
	public List<TablelistcolumnInfo> check() {
		List<TablelistcolumnInfo> reslist = select().innerJoin(tablelistcolumnInfo().menutableInfo()).getResultList();
		List<TablelistcolumnInfo> nolist = new ArrayList<TablelistcolumnInfo>();
		for (TablelistcolumnInfo col : reslist) {
			if (col.menutableInfo == null)
				nolist.add(col);
		}
		return nolist;
	}

	/**
	 * 同じタスク内で、指定レイヤID・属性IDのテーブルリストカラム情報を１件検索。
	 * @param menutaskid メニュータスクID
	 * @param tablemasterinfoid テーブルマスタ情報ID
	 * @param attrId 属性ID
	 * @return 検索結果リスト
	 */
	public List<TablelistcolumnInfo> findByMenutaskidAndTablemasterinfoidAndAttrid(Long menutaskid, Long tablemasterinfoid, String attrId) {
		return select()
				.leftOuterJoin(tablelistcolumnInfo().menutableInfo())
				.leftOuterJoin(tablelistcolumnInfo().menutableInfo().menuInfo())
				.leftOuterJoin(tablelistcolumnInfo().menutableInfo().menuInfo().menutaskmenuInfoList())
				.where(
					eq(tablelistcolumnInfo().attrid(), attrId),
					eq(tablelistcolumnInfo().menutableInfo().tablemasterinfoid(), tablemasterinfoid),
					eq(tablelistcolumnInfo().menutableInfo().menuInfo().menutaskmenuInfoList().menutaskinfoid(), menutaskid)
				).limit(1).getResultList();

	}

	@Override
	public DeleteCascadeResult deleteCascade(TablelistcolumnInfo entity, DeleteCascadeResult result) {

		result.cascade(TablerowstyleInfo.class, Names.tablerowstyleInfo().tablelistcolumninfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}

}
