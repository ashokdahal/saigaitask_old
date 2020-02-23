/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.menuprocessInfo;
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

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames._MenutaskInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskmenuInfoNames._MenutaskmenuInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MenuprocessInfoService extends AbstractService<MenuprocessInfo> {

	/** サービスクラス */
	@Resource
	protected MenutaskInfoService menutaskInfoService;

	public MenuprocessInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * deleted = false の条件付きでid検索
	 * @return MenuInfo
	 */
	public MenuprocessInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().id(id).getSingleResult();
	}

	/**
	 * メニューログイン情報IDでメニュー情報までJOINして検索
	 * @param id メニューログイン情報ID
	 * @return
	 */
	public List<MenuprocessInfo> findAllJoinMenuInfoOrderbyDisporder(Long id) {
		AutoSelect<MenuprocessInfo> select = select();

		// 削除されていないメニュータスク情報をインナージョイン
		_MenutaskInfoNames menutaskInfoNames = MenuprocessInfoNames.menutaskInfos();
		select.innerJoin(menutaskInfoNames, new SimpleWhere().ne(menutaskInfoNames.deleted(), true));
		// メニュータスクメニュー情報をインナージョイン(これは論理削除がない)
		_MenutaskmenuInfoNames menutaskmenuInfoNames = menutaskInfoNames.menutaskmenuInfos();
		select.innerJoin(menutaskmenuInfoNames);
		// 削除されていないメニュー情報をインナージョイン
		_MenuInfoNames menuInfoNames = menutaskmenuInfoNames.menuInfo();
		select.innerJoin(menuInfoNames, new SimpleWhere().ne(menuInfoNames.deleted(), true));
		select.leftOuterJoin(menuInfoNames.menutypeMaster());
		//select.leftOuterJoin(menuInfoNames.tableMaster());
		// 削除されていないメニュー設定情報をレフトジョイン
		select.leftOuterJoin(menuprocessInfo().menuloginInfo(), new SimpleWhere().ne(menuprocessInfo().menuloginInfo().deleted(), true));

		// Where
		SimpleWhere where = new SimpleWhere();
		if(id!=null) {
			where.eq(MenuprocessInfoNames.menulogininfoid(), id);
			where.ne(MenuprocessInfoNames.deleted(), true);
		}
		select.where(where);

		// Orderby
		select.orderBy(
				Operations.asc(MenuprocessInfoNames.disporder()),
				Operations.asc(menutaskInfoNames.disporder()),
				Operations.asc(menutaskmenuInfoNames.disporder())
		);

		return select.getResultList();
	}

	public List<MenuprocessInfo> findValidJoinMenuInfoOrderbyDisporder(Long id) {
		AutoSelect<MenuprocessInfo> select = select();

		// Join menutaskMaster
		_MenutaskInfoNames menutaskInfoNames = MenuprocessInfoNames.menutaskInfos();
		select.innerJoin(menutaskInfoNames, ne(menutaskInfoNames.deleted(), true));
		// Join menuMaster
		_MenutaskmenuInfoNames menutaskmenuInfoNames = menutaskInfoNames.menutaskmenuInfos();
		select.innerJoin(menutaskmenuInfoNames);
		_MenuInfoNames menuInfoNames = menutaskmenuInfoNames.menuInfo();
		select.innerJoin(menuInfoNames, ne(menuInfoNames.deleted(), true));
		select.leftOuterJoin(menuInfoNames.menutypeMaster());
		//select.leftOuterJoin(menuInfoNames.tableMaster());
		// Join menuloginInfo
		select.leftOuterJoin(menuprocessInfo().menuloginInfo());

		// Where
		SimpleWhere where = new SimpleWhere();
		if(id!=null) {
			where.eq(MenuprocessInfoNames.menulogininfoid(), id);
			where.eq(MenuprocessInfoNames.menutaskInfos().menutaskmenuInfos().menuInfo().valid(), true);
			where.eq(MenuprocessInfoNames.menutaskInfos().valid(), true);
			where.eq(MenuprocessInfoNames.valid(), true);
			where.ne(MenuprocessInfoNames.deleted(), true);
			where.ne(MenuprocessInfoNames.menuloginInfo().deleted(), true);
		}
		select.where(where);

		// Orderby
		select.orderBy(
				Operations.asc(MenuprocessInfoNames.disporder()),
				Operations.asc(menutaskInfoNames.disporder()),
				Operations.asc(menutaskmenuInfoNames.disporder())
		);

		return select.getResultList();
	}

	/**
	 * ログインIDで有効なメニュープロセス情報を検索
	 * @param lid メニューログイン情報ID
	 * @return メニュープロセス情報リスト
	 */
	public List<MenuprocessInfo> findByMenuloginInfoIdAndValidAndNotDeleted(Long lid) {
		return select().where(
				and(
					eq(menuprocessInfo().menulogininfoid(), lid),
					eq(menuprocessInfo().valid(), true),
					eq(menuprocessInfo().deleted(), false)
				)).orderBy(asc(menuprocessInfo().disporder())).getResultList();
	}

	/**
	 * ログインIDで有効なメニュープロセス情報を検索
	 * @param lid メニューログイン情報ID
	 * @return メニュープロセス情報リスト
	 */
	public List<MenuprocessInfo> findByMenuloginInfoIdAndValidAndNotDeletedAndVisible(Long lid) {
		return select().where(
				and(
					eq(menuprocessInfo().menulogininfoid(), lid),
					eq(menuprocessInfo().valid(), true),
					eq(menuprocessInfo().deleted(), false),
					eq(menuprocessInfo().visible(), true)
				)).orderBy(asc(menuprocessInfo().disporder())).getResultList();
	}

	/**
	 * ログインIDでメニュープロセス情報を検索
	 * @param lid メニューログイン情報ID
	 * @return メニュープロセス情報リスト
	 */
	public List<MenuprocessInfo> findByMenuloginInfoId(Long lid) {
		return select().where(
				eq(menuprocessInfo().menulogininfoid(), lid)
				).orderBy(asc(menuprocessInfo().disporder())).getResultList();
	}

	/**
	 * 班IDでメニュープロセス情報を検索
	 * @param groupid 班ID
	 * @return メニュープロセス情報リスト
	 */
	/*public List<MenuprocessInfo> findByGroupid(Long groupid) {
		return select().where(
				new SimpleWhere().eq(MenuprocessInfoNames.groupid(), groupid))
				.getResultList();
	}*/

	/**
	 * 班の設定を検索して
	 * 設定があれば適用します.
	 * @param menuprocessMasters
	 * @param groupid
	 */
	/*public void applyMenuprocessInfo(List<MenuprocessMaster> menuprocessMasters, long groupid, boolean defaultVisible) {
		MenutaskInfoService menutaskInfoService = SpringContext.getApplicationContext().getBean(MenutaskInfoService.class);
		List<MenuprocessInfo> menuprocessInfos = findByGroupid(groupid);
		Map<Integer, Boolean> visible = new HashMap<Integer, Boolean>();
		if(0<menuprocessInfos.size()) {
			// 表示設定を読み込み
			for(MenuprocessInfo menuprocessInfo : menuprocessInfos) {
				visible.put(menuprocessInfo.menuprocessid, menuprocessInfo.visible);
			}
		}

		for(MenuprocessMaster menuprocessMaster : menuprocessMasters) {
			// 表示設定があるなら適用
			if(0<menuprocessInfos.size()) {
				Boolean isVisible = visible.get(menuprocessMaster.id);
				if(isVisible==null) isVisible = defaultVisible; // デフォルト
				// 非表示の場合は削除
				if(isVisible==false) {
					menuprocessMasters.remove(menuprocessMaster);
					continue;
				}
			}
			// タスクを持つならタスクの設定を適用する
			List<MenutaskMaster> menutaskMasters = menuprocessMaster.menutaskMasters;
			if(menutaskMasters!=null) {
				menutaskInfoService.applyMenutaskInfo(menutaskMasters, groupid, defaultVisible);
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
			.innerJoin(menuprocessInfo().menuloginInfo())
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
	public List<MenuprocessInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menuprocessInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menuprocessInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menuprocessInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return selectNotDeleted()
			.innerJoin(menuprocessInfo().menuloginInfo())
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
		conditions.put(menuprocessInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MenuprocessInfo> list = findByCondition(conditions, menuprocessInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(MenuprocessInfo entity, PropertyName<?>[] excludes) {
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

	public List<MenuprocessInfo> check() {
		List<MenuprocessInfo> reslist = select().innerJoin(menuprocessInfo().menuloginInfo()).getResultList();
		List<MenuprocessInfo> nolist = new ArrayList<MenuprocessInfo>();
		for (MenuprocessInfo process : reslist) {
			if (process.menuloginInfo == null)
				nolist.add(process);
		}
		return nolist;
	}

	public int deleteLogically(MenuprocessInfo entity) throws Exception {
		menutaskInfoService.deleteLogicallyBySimpleWhere(MenutaskInfoNames.menuprocessinfoid(), entity.id);
        return super.deleteLogically(entity);
    }

	@Override
	public DeleteCascadeResult deleteCascade(MenuprocessInfo entity, DeleteCascadeResult result) {

		result.cascade(MenutaskInfo.class, Names.menutaskInfo().menuprocessinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
