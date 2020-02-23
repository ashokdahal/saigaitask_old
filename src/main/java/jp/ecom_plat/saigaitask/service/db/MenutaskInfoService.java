/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.menutaskInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * メニュータスク情報サービスクラス
 */
@org.springframework.stereotype.Repository
public class MenutaskInfoService extends AbstractService<MenutaskInfo> {

	/**
	 * IDから検索
	 * @param id メニュータスク情報ID
	 * @return メニュータスク情報
	 */
	public MenutaskInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 「メニュータスク種別ID」から未削除の「メニュータスク情報」を検索する.
	 * メニュータスク種別は自治体単位なので、
	 * 全班の「メニューログイン情報」の中から、最初に見つかった未削除の「メニュータスク情報」が取得できる.
	 * 削除フラグは、「メニュータスク情報」自体のチェックに加えて、「メニュープロセス情報」の削除フラグをチェックする.
	 * @param menutasktypeinfoid メニュータスク種別ID
	 * @return 全班の「メニューログイン情報」の中から、最初に見つかった未削除の「メニュータスク情報」
	 */
	public MenutaskInfo findBymenutasktypeinfoid(Long menutasktypeinfoid) {
//		return select().id(menutasktypeinfoid).getSingleResult();
    	return select()
    			.innerJoin(menutaskInfo().menuprocessInfo(), new SimpleWhere().ne(menutaskInfo().menuprocessInfo().deleted(), true))
    			.where(
    				eq(menutaskInfo().menutasktypeinfoid(), menutasktypeinfoid),
    				ne(menutaskInfo().deleted(), true)
    			)
    			.limit(1).getSingleResult();
	}

	/**
	 * deleted = false の条件付きでid検索
	 * @param id メニュータスク情報ID
	 * @return MenuInfo
	 */
	public MenutaskInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().id(id).getSingleResult();
	}

	/**
	 * プロセスIDで有効なタスク情報を検索
	 * @param pid
	 * @return
	 */
	public List<MenutaskInfo> findByMenuprocessInfoIdAndValidAndNotDeleted(Long pid) {
		return select().where(
				and(
					eq(menutaskInfo().menuprocessinfoid(), pid),
					eq(menutaskInfo().valid(), true),
    				ne(menutaskInfo().deleted(), true)
				)).orderBy(asc(menutaskInfo().disporder())).getResultList();
	}

	/**
	 * 班IDでメニュータスク情報を検索
	 * @param groupid 班ID
	 * @return メニュータスク情報リスト
	 */
	/*public List<MenutaskInfo> findByGroupid(Long groupid) {
		return select().where(
				new SimpleWhere().eq(MenutaskInfoNames.groupid(), groupid))
				.getResultList();
	}*/

	/**
	 * 班の設定を検索して
	 * 設定があれば適用します.
	 * @param menutaskMasters
	 * @param groupid
	 * @param defaultVisible
	 */
	/*public void applyMenutaskInfo(List<MenutaskMaster> menutaskMasters, long groupid, boolean defaultVisible) {
		MenuInfoService menuInfoService = SpringContext.getApplicationContext().getBean(MenuInfoService.class);
		List<MenutaskInfo> menutaskInfos = findByGroupid(groupid);
		Map<Integer, Boolean> visible = new HashMap<Integer, Boolean>();
		if(0<menutaskInfos.size()) {
			// 表示設定を読み込み
			for(MenutaskInfo menutaskInfo : menutaskInfos) {
				visible.put(menutaskInfo.menutaskid, menutaskInfo.visible);
			}
		}

		for(MenutaskMaster menutaskMaster : menutaskMasters) {
			// 表示設定があるなら適用
			if(0<menutaskInfos.size()) {
				Boolean isVisible = visible.get(menutaskMaster.id);
				if(isVisible==null) isVisible = defaultVisible; // デフォルト
				// 非表示の場合は削除
				if(isVisible==false) {
					menutaskMasters.remove(menutaskMaster);
					continue;
				}
			}
			// メニューを持つならメニューの設定を適用する
			List<MenuMaster> menuMasters = menutaskMaster.menuMasters;
			if(menuMasters!=null) {
				menuInfoService.applyMenuInfo(menuMasters, groupid, defaultVisible);
			}
		}
	}*/

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(menutaskInfo().menuprocessInfo())
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
	public List<MenutaskInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menutaskInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menutaskInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menutaskInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return selectNotDeleted()
			.innerJoin(menutaskInfo().menuprocessInfo())
			.innerJoin(menutaskInfo().mentasktypeInfo())
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
		conditions.put(menutaskInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MenutaskInfo> list = findByCondition(conditions, menutaskInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	 * @return 更新数
	*/
	public int update(MenutaskInfo entity, PropertyName<?>[] excludes) {
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

	public List<MenutaskInfo> check() {
		List<MenutaskInfo> reslist = select().innerJoin(menutaskInfo().menuprocessInfo()).getResultList();
		List<MenutaskInfo> nolist = new ArrayList<MenutaskInfo>();
		for (MenutaskInfo task : reslist) {
			if (task.menuprocessInfo == null)
				nolist.add(task);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(MenutaskInfo entity, DeleteCascadeResult result) {

		result.cascade(MenutaskmenuInfo.class, Names.menutaskmenuInfo().menutaskinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
