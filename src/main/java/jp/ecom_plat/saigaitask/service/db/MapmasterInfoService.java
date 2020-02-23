/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.mapmasterInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.EarthquakegrouplayerData;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MapmasterInfoService extends AbstractService<MapmasterInfo> {

	/** サービスクラス */
	@Resource
	protected TablemasterInfoService tablemasterInfoService;

	/**
	 * IDで検索
	 * @param id
	 * @return 地図情報
	 */
    public MapmasterInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
	 * deleted = false の条件付きでid検索
	 * @param id
	 * @return メニューログイン情報
	 */
    public MapmasterInfo findByNotDeletedId(Long id) {
        return selectNotDeleted().id(id).getSingleResult();
    }

    /**
     * 自治体IDで検索
     * @param govid 自治体ID
     * @return 地図情報
     */
    public MapmasterInfo findByLocalgovInfoId(Long govid) {
    	return select().where(
    			eq(mapmasterInfo().localgovinfoid(), govid)
    			,ne(mapmasterInfo().deleted(), true)
    		).limit(1).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(mapmasterInfo().localgovInfo())
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
	public List<MapmasterInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = mapmasterInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!mapmasterInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(mapmasterInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return selectNotDeleted()
			.innerJoin(mapmasterInfo().localgovInfo())
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
	public int update(MapmasterInfo entity, PropertyName<?>[] excludes) {
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

	public List<MapmasterInfo> check() {
		List<MapmasterInfo> reslist = select().innerJoin(mapmasterInfo().localgovInfo()).getResultList();
		List<MapmasterInfo> nolist = new ArrayList<MapmasterInfo>();
		for (MapmasterInfo map : reslist) {
			if (map.localgovInfo == null)
				nolist.add(map);
		}
		//TODO:communityid
		//TODO:mapgroupid
		//TODO:mapid
		return nolist;
	}

	public int deleteLogically(MapmasterInfo entity) throws Exception {
		tablemasterInfoService.deleteLogicallyBySimpleWhere(TablemasterInfoNames.mapmasterinfoid(), entity.id);
        return super.deleteLogically(entity);
    }

	@Override
	public DeleteCascadeResult deleteCascade(MapmasterInfo entity, DeleteCascadeResult result) {

		result.cascade(EarthquakegrouplayerData.class, Names.earthquakegrouplayerData().mapmasterinfoid(), entity.id);
		result.cascade(TablemasterInfo.class, Names.tablemasterInfo().mapmasterinfoid(), entity.id);

		// eコミマップの削除
		result.cascadeEcommapMapInfo(entity.mapid, entity, String.valueOf(entity.id));

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
