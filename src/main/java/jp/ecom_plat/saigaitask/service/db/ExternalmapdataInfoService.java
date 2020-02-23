/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.externalmapdataInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class ExternalmapdataInfoService extends AbstractService<ExternalmapdataInfo> {

	public ExternalmapdataInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * leftJoinで認証情報を紐付けて返す
	 * @param id : ID
	 * @return
	 */
	public ExternalmapdataInfo findMapAuthById(Long id) {
		return select().leftOuterJoin(externalmapdataInfo().authorizationInfo()).id(id).getSingleResult();
	}

	/**
	 * メニューＩＤを指定して検索
	 * @param menuid メニューID
	 * @return 検索結果
	 */
	public List<ExternalmapdataInfo> findByMenuid(Long menuid) {
		return select()
				.where(Operations.eq(externalmapdataInfo().menuinfoid(), menuid))
				.orderBy(new OrderByItem(externalmapdataInfo().disporder()))
				.getResultList();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(externalmapdataInfo().menuInfo())
			.leftOuterJoin(externalmapdataInfo().externaltabledataInfo())
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
	public List<ExternalmapdataInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = externalmapdataInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!externalmapdataInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(externalmapdataInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(externalmapdataInfo().menuInfo())
			.leftOuterJoin(externalmapdataInfo().authorizationInfo())
			.leftOuterJoin(externalmapdataInfo().externaltabledataInfo())
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
		conditions.put(externalmapdataInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<ExternalmapdataInfo> list = findByCondition(conditions, externalmapdataInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(ExternalmapdataInfo entity, PropertyName<?>[] excludes) {
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

	public List<ExternalmapdataInfo> check() {
		List<ExternalmapdataInfo> reslist = select().innerJoin(externalmapdataInfo().menuInfo()).getResultList();
		List<ExternalmapdataInfo> nolist = new ArrayList<ExternalmapdataInfo>();
		for (ExternalmapdataInfo map : reslist) {
			if (map.menuInfo == null)
				nolist.add(map);
		}
		//TODO:filterid
		return nolist;
	}

	/**
	 * レイヤの各サブレイヤを削除
	 * @param	layerparent		親レイヤのID
	 * @return
	 */
	public int deleteChildren(Long layerparent) {
		String sql = "DELETE FROM externalmapdata_info WHERE layerparent = " + layerparent;
		int ret = 0;
		ret = jdbcManager.updateBySql(sql).execute();
		return ret;
	}

	/**
	 * 追加したレイヤの中に最大のIDを取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
	public long getLargestId(Long menuinfoid) {
		BeanMap conditions = new BeanMap();
		conditions.put("menuinfoid", menuinfoid);
		List<ExternalmapdataInfo> list = findByCondition(conditions, externalmapdataInfo().id().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).id;
		}
	}

	/**
	 * 透明度変更
	 * @param metadataid	メタデータID
	 * @param layeropacity 透明度
	 */
	public void setLayeropacity(String metadataid, double layeropacity){
		String sql = "UPDATE externalmapdata_info SET layeropacity = " + layeropacity + " WHERE metadataid = '" + metadataid + "'";
		jdbcManager.updateBySql(sql).execute();
	}
}
