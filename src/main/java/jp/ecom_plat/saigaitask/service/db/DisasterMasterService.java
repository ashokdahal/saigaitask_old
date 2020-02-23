/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterMaster;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.gt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * 災害種別のエンティティに対する操作を格納するクラス
 */
@org.springframework.stereotype.Repository
public class DisasterMasterService extends AbstractService<DisasterMaster> {
	//protected static String[] alterFieldName = { "ID", "名称", "表示順" };
	//private static Map<Integer, String> disasterMasteMap;
	private /*static */String getIdZeroName() {
		return lang.__("Common");
	}
	private static int idZero = 0;

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
    public DisasterMaster findById(Integer id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * IDから対応名称を取得
	 * @param ID
	 * @return 名称
	 */
	public String getNameByID(Integer id){
		if (id == null || id == idZero ){
			return getIdZeroName();
		}else{
			return findById(id).name;
		}
	}

	/**
	 * <ID,Name>のようなMapを取得
	 * @return <ID,Name>のようなMap
	 */
	public Map<Integer, String> getNameMap() {
		//if (disasterMasteMap != null)
		//	return disasterMasteMap;

		Map<Integer, String> disasterMasteMap = new HashMap<Integer, String>();
		List<DisasterMaster> tslist = findAllOrderBy();
		Iterator<DisasterMaster> it = tslist.iterator();
		disasterMasteMap.put(idZero, getIdZeroName());
		while (it.hasNext()) {
			DisasterMaster ts = it.next();
			disasterMasteMap.put(ts.id, ts.name);
		}
		return disasterMasteMap;
	}


	/**
	 * <ID,Name>のようなMapを取得。デフォルト値(0)なし
	 * @return <ID,Name>のようなMap
	 */
	public Map<Integer, String> getNameMapNoZero() {
		//if (disasterMasteMap != null)
		//	return disasterMasteMap;

		Map<Integer, String> disasterMasteMap = new HashMap<Integer, String>();
		List<DisasterMaster> tslist = findAllOrderBy();
		Iterator<DisasterMaster> it = tslist.iterator();
		while (it.hasNext()) {
			DisasterMaster ts = it.next();
			disasterMasteMap.put(ts.id, ts.name);
		}
		return disasterMasteMap;
	}

	/**
	 * 表示順で全検索
	 * @return 全データ
	 */
	public List<DisasterMaster> findAllOrderByDisporder() {
		return select().orderBy(asc(disasterMaster().disporder())).getResultList();
	}

    /**
     * ソートして全検索
     * @return 災害種別リスト
     */
    public List<DisasterMaster> findAllOrderBy() {
    	return select().orderBy(asc(disasterMaster().disporder())).getResultList();
    }

    /**
     * ソートしてID０以上の検索
     * @return 災害種別リスト
     */
    public List<DisasterMaster> findGtZeroOrderBy() {
    	return select().where(gt(disasterMaster().id(), 0)).orderBy(asc(disasterMaster().disporder())).getResultList();
    }

    /**
     * 表示順最大値を取得
     * @return 表示順最大値
     */
	public int getLargestDisporder() {
		List<DisasterMaster> list = findAllOrderByDisporder();

		if (list == null || list.size() == 0)
			return 0;
		else
			return list.get(list.size() - 1).disporder;
	}

	/**
	 * 表示順上に変更
	 * @param DisasterMasterオブジェクト
	 */
	public void up(DisasterMaster o) {
		List<DisasterMaster> list = findAllOrderByDisporder();

		// ずらす処理
		DisasterMaster pre = null;
		for (DisasterMaster cur : list) {
			if (cur.id.equals(o.id)) {
				if (pre == null)
					return;
				cur.disporder = pre.disporder;
				pre.disporder = o.disporder;
				update(pre);
				update(cur);
				return;
			}
			pre = cur;
		}
	}

	/**
	 * 表示順下に変更
	 * @param DisasterMasterオブジェクト
	 */
	public void down(DisasterMaster o) {
		List<DisasterMaster> list = findAllOrderByDisporder();

		// ずらす処理
		DisasterMaster pre = null;
		for (DisasterMaster cur : list) {
			if (cur.id.equals(o.id)) {
				pre = cur;
				continue;
			}
			if (pre != null) {
				pre.disporder = cur.disporder;
				cur.disporder = o.disporder;
				update(pre);
				update(cur);
				return;
			}
		}
	}

	/**
	 * 表示順を更新、画面レコード削除時用
	 */
	public void sortDisporder() {
		List<DisasterMaster> list = findAllOrderByDisporder();
		int idx = 1;

		// 順番のチェック
		for (DisasterMaster o : list) {
			if (o.disporder == idx) {
				idx++;
				continue;
			}

			// ずれている？
			o.disporder = idx;
			update(o);
			idx++;
		}
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
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
	public List<DisasterMaster> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = disasterMaster().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!disasterMaster().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(disasterMaster().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
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
		conditions.put(disasterMaster().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<DisasterMaster> list = findByCondition(conditions, disasterMaster().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(DisasterMaster entity, PropertyName<?>[] excludes) {
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
}
