/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.maplayerInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * 地図レイヤ情報サービス.
 * 登録情報レイヤ情報のサービスクラスです.
 */
@org.springframework.stereotype.Repository
public class MaplayerInfoService extends AbstractService<MaplayerInfo> {

	public List<MaplayerInfo> findAllOrderBy() {
		return select()
				.orderBy(
						Operations.asc(MaplayerInfoNames.menuinfoid()),
						Operations.asc(MaplayerInfoNames.disporder()))
				.getResultList();
	}

	public MaplayerInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * deleted = false の条件付きでid検索
	 * @return MenuInfo
	 */
	public MaplayerInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().id(id).getSingleResult();
	}
	
	/**
	 * メニューID指定で地図レイヤ情報リストを取得します.
	 * 地図レイヤ属性情報を結合します.
	 * valid=true のもののみ取得します.
	 * disporder順でソートします.
	 * @param menuid メニューID
	 * @param trackdataid 記録ID
	 * @return 地図レイヤ情報リスト
	 */
	public List<MaplayerInfo> findByMenuid(long menuid, long trackdataid) {

		// Select
		AutoSelect<MaplayerInfo> select = select();

		// Join
		select.leftOuterJoin(MaplayerInfoNames.maplayerattrInfos());
		select.leftOuterJoin(MaplayerInfoNames.tablemasterInfo());
		if (trackdataid > 0) {
			select.leftOuterJoin(MaplayerInfoNames.tablemasterInfo().tracktableInfos());
			select.leftOuterJoin(MaplayerInfoNames.tablemasterInfo().tracktableInfos().trackmapInfo());
			select.leftOuterJoin(MaplayerInfoNames.tablemasterInfo().tracktableInfos().trackmapInfo().trackDatas());
		}

		// Where
		SimpleWhere where = new SimpleWhere();
		where.eq(MaplayerInfoNames.valid(), true);
		where.eq(MaplayerInfoNames.menuinfoid(), menuid);
		where.ne(MaplayerInfoNames.deleted(), true);
		where.ne(MaplayerInfoNames.tablemasterInfo().deleted(), true);
		if (trackdataid > 0)
			where.eq(MaplayerInfoNames.tablemasterInfo().tracktableInfos().trackmapInfo().trackDatas().id(), trackdataid);
		select.where(where);

		// OrderBy
		select.orderBy(
				Operations.asc(MaplayerInfoNames.disporder()),
				Operations.asc(MaplayerInfoNames.maplayerattrInfos().disporder())
		);

		return select.getResultList();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(maplayerInfo().tablemasterInfo())
			.innerJoin(maplayerInfo().menuInfo())
			.leftOuterJoin(maplayerInfo().menutableInfo(), ne(maplayerInfo().menutableInfo().deleted(), true))
			.where(conditions)
			.getCount();
	}
	
    /**
     * 条件付で検索します。
     * @param conditions 条件
     * @return エンティティのリスト
     */
	public List<MaplayerInfo> findByCondition(BeanMap conditions) {
        return selectNotDeleted()
        		.where(conditions)
        		.getResultList();
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
	public List<MaplayerInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = maplayerInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!maplayerInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(maplayerInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return selectNotDeleted()
			.innerJoin(maplayerInfo().tablemasterInfo())
			.innerJoin(maplayerInfo().menuInfo())
			.leftOuterJoin(maplayerInfo().menutableInfo(), ne(maplayerInfo().menutableInfo().deleted(), true))
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
		conditions.put(maplayerInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MaplayerInfo> list = findByCondition(conditions, maplayerInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(MaplayerInfo entity, PropertyName<?>[] excludes) {
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

	public List<MaplayerInfo> check() {
		List<MaplayerInfo> reslist = select().innerJoin(maplayerInfo().menuInfo())
				.innerJoin(maplayerInfo().tablemasterInfo()).getResultList();
		List<MaplayerInfo> nolist = new ArrayList<MaplayerInfo>();
		for (MaplayerInfo layer : reslist) {
			if (layer.menuInfo == null || layer.tablemasterInfo == null)
				nolist.add(layer);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(MaplayerInfo entity, DeleteCascadeResult result) {

		result.cascade(MaplayerattrInfo.class, Names.maplayerattrInfo().maplayerinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
