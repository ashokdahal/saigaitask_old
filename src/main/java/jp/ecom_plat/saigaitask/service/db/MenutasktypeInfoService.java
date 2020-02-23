/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;
import static jp.ecom_plat.saigaitask.entity.Names.menutasktypeInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.where.ComplexWhere;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.Constants;

@org.springframework.stereotype.Repository
public class MenutasktypeInfoService extends AbstractService<MenutasktypeInfo> {

	/** サービスクラス */
	@Resource
	protected MenuInfoService menuInfoService;
	@Resource
	protected MenutaskInfoService menutaskInfoService;

    public MenutasktypeInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    private List<Integer> getTemplateFlags(){
		List<Integer> templateFlags = new ArrayList<Integer>();
		templateFlags.add(Constants.MENUTASKTYPE_TEMPLATE_PARENT);
		templateFlags.add(Constants.MENUTASKTYPE_TEMPLATE_CHILD);
		return templateFlags;
    }
    
	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return getCount(conditions, false);
	}
	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @param isTemplate テンプレートメニュー検索フラグ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions, boolean isTemplate) {
		if(isTemplate) {
			return (int)select()
					.innerJoin(menutasktypeInfo().localgovInfo())
					.where(conditions)
					.where(
							new SimpleWhere().in(menutasktypeInfo().template().toString(), getTemplateFlags()),
							new SimpleWhere().eq(menutasktypeInfo().deleted().toString(), false))
					.getCount();
			
		}else {
			return (int)select()
					.innerJoin(menutasktypeInfo().localgovInfo())
					.where(conditions)
					.where(
							new ComplexWhere().eq(menutasktypeInfo().template().toString(), Constants.MENUTASKTYPE_TEMPLATE_NONE).or().isNull(menutasktypeInfo().template().toString(), true),
							new SimpleWhere().eq(menutasktypeInfo().deleted().toString(), false))
					.getCount();
			
		}
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
	public List<MenutasktypeInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		return findByCondition(conditions, sortName, sortOrder, limit, offset, false);
	}
	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @param isTemplate テンプレートメニュー検索フラグ
	 * @return 検索結果
	 */
	public List<MenutasktypeInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset, boolean isTemplate) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = menutasktypeInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!menutasktypeInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(menutasktypeInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		if(isTemplate) {
			return select()
					.innerJoin(menutasktypeInfo().localgovInfo())
					.where(conditions)
					.where(
							new SimpleWhere().in(menutasktypeInfo().template().toString(), getTemplateFlags()),
							new SimpleWhere().eq(menutasktypeInfo().deleted().toString(), false))
					.orderBy(orderByItems)
					.limit(limit)
					.offset(offset)
					.getResultList();
			
		}else {
			return select()
					.innerJoin(menutasktypeInfo().localgovInfo())
					.where(conditions)
					.where(
							new ComplexWhere().eq(menutasktypeInfo().template().toString(), Constants.MENUTASKTYPE_TEMPLATE_NONE).or().isNull(menutasktypeInfo().template().toString(), true),
							new SimpleWhere().eq(menutasktypeInfo().deleted().toString(), false))
					.orderBy(orderByItems)
					.limit(limit)
					.offset(offset)
					.getResultList();
			
		}
//		return selectNotDeleted()
//			.innerJoin(menutasktypeInfo().localgovInfo())
//			.where(conditions)
//			.orderBy(orderByItems)
//			.limit(limit)
//			.offset(offset)
//			.getResultList();
	}

	/**
	 * 表示順最大値を取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
	public int getLargestDisporder(Map<String, Object> conditions, boolean isTemplate) {
		conditions.put(menutasktypeInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MenutasktypeInfo> list = findByCondition(conditions, menutasktypeInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET, isTemplate);

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
	public int update(MenutasktypeInfo entity, PropertyName<?>[] excludes) {
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

	public int deleteLogically(MenutasktypeInfo entity) throws Exception {
		menuInfoService.deleteLogicallyBySimpleWhere(MenuInfoNames.menutasktypeinfoid(), entity.id);
		menutaskInfoService.deleteLogicallyBySimpleWhere(MenutaskInfoNames.menutasktypeinfoid(), entity.id);
		        return super.deleteLogically(entity);
	}

	@Override
	public DeleteCascadeResult deleteCascade(MenutasktypeInfo entity, DeleteCascadeResult result) {

		result.cascade(MenuInfo.class, Names.menuInfo().menutasktypeinfoid(), entity.id);
		result.cascade(MenutaskInfo.class, Names.menutaskInfo().menutasktypeinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
