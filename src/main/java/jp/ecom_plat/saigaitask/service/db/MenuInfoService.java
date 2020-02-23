/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.menuInfo;
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

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.FilterInfo;
import jp.ecom_plat.saigaitask.entity.db.MapbaselayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapkmllayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapreferencelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenumapInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteolayerInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.PagemenubuttonInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * メニュー情報サービス
 */
@org.springframework.stereotype.Repository
public class MenuInfoService extends AbstractService<MenuInfo> {

	/** サービスクラス */
	@Resource
	protected MaplayerInfoService maplayerInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;

	/**
	 * IDから検索
	 * @param id メニュー情報ID
	 * @return メニュー情報
	 */
	public MenuInfo findById(Long id) {
		/*AutoSelect<MenuInfo> select = select();
		select.innerJoin(MenuInfoNames.menutaskInfo());
		select.innerJoin(MenuInfoNames.menutaskInfo().menuprocessInfo());
		select.leftOuterJoin(MenuInfoNames.menutypeMaster());
		//select.leftOuterJoin(MenuInfoNames.tableMaster());
		select.id(id);
		return select.getSingleResult();*/
		return select().id(id).getSingleResult();
	}

	/**
	 * deleted = false の条件付きでid検索
	 * @param id メニュー情報ID
	 * @return MenuInfo
	 */
	public MenuInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().id(id).getSingleResult();
	}

	/**
	 * 全検索
	 * @return メニューリスト
	 */
	public List<MenuInfo> findAllOrderBy() {
		return select().orderBy(asc(menuInfo().menutasktypeinfoid()), asc(menuInfo().id())).getResultList();
	}

	/**
	 * タスクＩＤを指定し
	 * disporder順でメニューを検索
	 * @param menutaskid
	 * @return メニュー情報リスト
	 */
	public List<MenuInfo> findByMenutaskinfoidOrderbyDisporder(Long menutaskid) {
		AutoSelect<MenuInfo> select = select();
		select.innerJoin(MenuInfoNames.menutaskmenuInfoList());
		select.innerJoin(MenuInfoNames.menutaskmenuInfoList().menutaskInfo(), ne(MenuInfoNames.menutaskmenuInfoList().menutaskInfo().deleted(), true));
		select.innerJoin(MenuInfoNames.menutaskmenuInfoList().menutaskInfo().menuprocessInfo(), ne(MenuInfoNames.menutaskmenuInfoList().menutaskInfo().menuprocessInfo().deleted(), true));
		select.leftOuterJoin(MenuInfoNames.menutypeMaster());
		//select.leftOuterJoin(MenuInfoNames.tableMaster());
		SimpleWhere where = new SimpleWhere();
		where.eq(MenuInfoNames.menutaskmenuInfoList().menutaskinfoid(), menutaskid);
		where.ne(MenuInfoNames.deleted(), true);
		select.where(where);
		//select.where(new SimpleWhere().eq(MenuInfoNames.menutaskmenuInfoList().menutaskinfoid(), menutaskid));
		select.orderBy(Operations.asc(MenuInfoNames.menutaskmenuInfoList().disporder()));
		return select.getResultList();
	}

	/**
	 * 班IDでメニュータスク情報を検索
	 * @param groupid 班ID
	 * @return メニュータスク情報リスト
	 */
	/*public List<MenuInfo> findByGroupid(Long groupid) {
		return select().where(
				new SimpleWhere().eq(MenuInfoNames.groupid(), groupid))
				.getResultList();
	}*/

	/**
	 * 班の設定を検索して
	 * 設定があれば適用します.
	 * @param menuMasters
	 * @param groupid
	 * @param defaultVisible
	 */
	/*public void applyMenuInfo(List<MenuMaster> menuMasters, long groupid, boolean defaultVisible) {
		List<MenuInfo> menuInfos = findByGroupid(groupid);
		Map<Integer, Boolean> visible = new HashMap<Integer, Boolean>();
		if(0<menuInfos.size()) {
			// 表示設定を読み込み
			for(MenuInfo menuInfo : menuInfos) {
				visible.put(menuInfo.menuid, menuInfo.visible);
			}
		}

		for(MenuMaster menuMaster : menuMasters) {
			// 表示設定があるなら適用
			if(0<menuInfos.size()) {
				Boolean isVisible = visible.get(menuMaster.id);
				if(isVisible==null) isVisible = defaultVisible; // デフォルト
				// 非表示の場合は削除
				if(isVisible==false) {
					menuMasters.remove(menuMaster);
				}
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
			.innerJoin(menuInfo().menutypeMaster())
			.innerJoin(menuInfo().menutasktypeInfo())
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
	public List<MenuInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menuInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menuInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menuInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return selectNotDeleted()
			.innerJoin(menuInfo().menutypeMaster())
			.innerJoin(menuInfo().menutasktypeInfo())
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
	/*public int getLargestDisporder(Map<String, Object> conditions) {
		conditions.put(menuInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MenuInfo> list = findByCondition(conditions, menuInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}*/

	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return 更新数
	*/
	public int update(MenuInfo entity, PropertyName<?>[] excludes) {
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
	 * 自治体IDに紐付くメニュー情報を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
	public List<MenuInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<MenuInfo> select = select();
		select.innerJoin(menuInfo().menutaskmenuInfoList());
		select.innerJoin(menuInfo().menutaskmenuInfoList().menutaskInfo());
		select.innerJoin(menuInfo().menutaskmenuInfoList().menutaskInfo().menuprocessInfo());
		select.innerJoin(menuInfo().menutaskmenuInfoList().menutaskInfo().menuprocessInfo().menuloginInfo());
		select.innerJoin(menuInfo().menutaskmenuInfoList().menutaskInfo().menuprocessInfo().menuloginInfo().groupInfo());
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(menuInfo().menutaskmenuInfoList().menutaskInfo().menuprocessInfo().menuloginInfo().groupInfo().localgovinfoid(), localgovinfoid)
					);
		}else{
			//システム管理者の場合、全件
		}
		select.orderBy(asc(menuInfo().menutaskmenuInfoList().disporder()), asc(menuInfo().id()));
		return select.getResultList();
	}

	/**
	 * タスク種別に紐付くメニュー情報を取得する。<br>
	 * @param menutasktypeinfoid タスク種別
	 * @return 検索結果
	 */
	public List<MenuInfo> findByMenutasktypeinfoid(Long menutasktypeinfoid) {
		AutoSelect<MenuInfo> select = select();

		select.where(
					eq(menuInfo().menutasktypeinfoid(), menutasktypeinfoid), ne(menuInfo().deleted(),true)
					);
		select.orderBy(asc(menuInfo().id()));
		return select.getResultList();
	}

	/**
	 * フィルターIDを指定しメニュー情報を検索
	 * @param filterid
	 * @return 検索結果
	 */
	public List<MenuInfo> findByFilterid(Long filterid) {
		AutoSelect<MenuInfo> select = select();
		select.where(
					eq(menuInfo().filterid(), filterid)
					);
		select.orderBy(asc(menuInfo().id()));
		return select.getResultList();
	}

	/**
	 * ベースレイヤが登録されている menuinfo を検索する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果
	 */
	public List<MenuInfo> findByConditionWithBaseLayerInfo(Map<String, Object> conditions) {
		return select()
			.innerJoin(menuInfo().menutasktypeInfo())
			.innerJoin(menuInfo().mapbaselayerInfoList())
			.where(conditions)
			.getResultList();
	}

	public int deleteLogically(MenuInfo entity) throws Exception {
		maplayerInfoService.deleteLogicallyBySimpleWhere(MaplayerInfoNames.menuinfoid(), entity.id);
		menutableInfoService.deleteLogicallyBySimpleWhere(MenutableInfoNames.menuinfoid(), entity.id);
        return super.deleteLogically(entity);
    }

	@Override
	public DeleteCascadeResult deleteCascade(MenuInfo entity, DeleteCascadeResult result) {

		result.cascade(ExternalmapdataInfo.class, Names.externalmapdataInfo().menuinfoid(), entity.id);
		result.cascade(ExternaltabledataInfo.class, Names.externaltabledataInfo().menuinfoid(), entity.id);
		result.cascade(FilterInfo.class, Names.filterInfo().menuinfoid(), entity.id);
		result.cascade(MapbaselayerInfo.class, Names.mapbaselayerInfo().menuinfoid(), entity.id);
		result.cascade(MapkmllayerInfo.class, Names.mapkmllayerInfo().menuinfoid(), entity.id);
		result.cascade(MaplayerInfo.class, Names.maplayerInfo().menuinfoid(), entity.id);
		result.cascade(MapreferencelayerInfo.class, Names.mapreferencelayerInfo().menuinfoid(), entity.id);
		result.cascade(MenumapInfo.class, Names.menumapInfo().menuinfoid(), entity.id);
		result.cascade(MenutableInfo.class, Names.menutableInfo().menuinfoid(), entity.id);
		result.cascade(MenutaskmenuInfo.class, Names.menutaskmenuInfo().menuinfoid(), entity.id);
		result.cascade(MeteolayerInfo.class, Names.meteolayerInfo().menuinfoid(), entity.id);
		result.cascade(NoticedefaultInfo.class, Names.noticedefaultInfo().menuinfoid(), entity.id);
		result.cascade(ObservmenuInfo.class, Names.observmenuInfo().menuinfoid(), entity.id);
		result.cascade(PagemenubuttonInfo.class, Names.pagemenubuttonInfo().menuinfoid(), entity.id);
		result.cascade(SummarylistcolumnInfo.class, Names.summarylistcolumnInfo().menuinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
