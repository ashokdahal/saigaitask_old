/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.mapbaselayerInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

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

import jp.ecom_plat.saigaitask.entity.db.MapbaselayerInfo;
import jp.ecom_plat.saigaitask.entity.names.MapbaselayerInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * 地図ベースレイヤ情報サービス.
 */
@org.springframework.stereotype.Repository
public class MapbaselayerInfoService extends AbstractService<MapbaselayerInfo> {

    public MapbaselayerInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * すべて検索
	 * menuinfoid, disporder順でソートします.
	 * @return 地図ベースレイヤ情報リスト
	 */
	public List<MapbaselayerInfo> findAllOrderbyMenuinfoidDisporder() {
		return select().orderBy(Operations.asc(MapbaselayerInfoNames.menuinfoid()), Operations.asc(MapbaselayerInfoNames.disporder())).getResultList();
	}

	/**
	 * メニューID指定で検索
	 * valid=true のもののみ取得します.
	 * disporder順でソートします.
	 * @param menuid メニューID
	 * @return 地図ベースレイヤ情報リスト
	 */
	public List<MapbaselayerInfo> findByMenuid(long menuid) {

		// Select
		AutoSelect<MapbaselayerInfo> select = select();

		// Where
		SimpleWhere where = new SimpleWhere();
		where.eq(MapbaselayerInfoNames.menuinfoid(), menuid);
		where.eq(MapbaselayerInfoNames.valid(), true);
		select.where(where);

		// OrderBy
		select.orderBy(
				Operations.asc(MapbaselayerInfoNames.disporder())
		);

		return select.getResultList();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(mapbaselayerInfo().menuInfo())
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
	public List<MapbaselayerInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = mapbaselayerInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!mapbaselayerInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(mapbaselayerInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			.innerJoin(mapbaselayerInfo().menuInfo())
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
		conditions.put(mapbaselayerInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MapbaselayerInfo> list = findByCondition(conditions, mapbaselayerInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(MapbaselayerInfo entity, PropertyName<?>[] excludes) {
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
	
	public List<MapbaselayerInfo> check() {
		List<MapbaselayerInfo> reslist = select().innerJoin(mapbaselayerInfo().menuInfo()).getResultList();
		List<MapbaselayerInfo> nolist = new ArrayList<MapbaselayerInfo>();
		for (MapbaselayerInfo layer : reslist) {
			if (layer.menuInfo == null)
				nolist.add(layer);
		}
		return nolist;
	}
}
