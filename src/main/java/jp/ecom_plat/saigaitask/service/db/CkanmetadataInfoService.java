/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.ckanmetadataInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.entity.db.CkanmetadataInfo;
import jp.ecom_plat.saigaitask.entity.names.CkanmetadataInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/** クリアリングハウス事前データ情報サービス */
@org.springframework.stereotype.Repository
public class CkanmetadataInfoService extends AbstractService<CkanmetadataInfo> {

    public CkanmetadataInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(ckanmetadataInfo().tablemasterInfo(), ne(ckanmetadataInfo().tablemasterInfo().deleted(), true))
			.innerJoin(ckanmetadataInfo().localgovInfo())
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
	public List<CkanmetadataInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = ckanmetadataInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!ckanmetadataInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(ckanmetadataInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(ckanmetadataInfo().tablemasterInfo(),ne(ckanmetadataInfo().tablemasterInfo().deleted(), true))
			.innerJoin(ckanmetadataInfo().localgovInfo())
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
	public int update(CkanmetadataInfo entity, PropertyName<?>[] excludes) {
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
	 * 自治体IDを指定して検索する.
	 * @param localgovinfoid
	 * @return
	 */
	public List<CkanmetadataInfo> findByLocalgovinfoId(long localgovinfoid) {
		return select()
				.innerJoin(ckanmetadataInfo().tablemasterInfo(),ne(ckanmetadataInfo().tablemasterInfo().deleted(), true))
				.innerJoin(ckanmetadataInfo().localgovInfo())
				.where(Operations.eq(CkanmetadataInfoNames.localgovinfoid(), localgovinfoid))
				.getResultList();
	}

	public List<CkanmetadataInfo> check() {
		List<CkanmetadataInfo> reslist = select().innerJoin(ckanmetadataInfo().localgovInfo())
				.innerJoin(ckanmetadataInfo().tablemasterInfo()).getResultList();
		List<CkanmetadataInfo> nolist = new ArrayList<CkanmetadataInfo>();
		for (CkanmetadataInfo data : reslist) {
			if (data.localgovInfo == null || data.tablemasterInfo == null)
				nolist.add(data);
		}
		return nolist;
	}

	/**
	 * メタデータIDから検索.
	 * @param metadataId メタデータID
	 * @return 検索結果リスト
	 */
	public List<CkanmetadataInfo> findByMetadataid(String metadataId) {
		return select()
				.innerJoin(ckanmetadataInfo().tablemasterInfo(),ne(ckanmetadataInfo().tablemasterInfo().deleted(), true))
				.innerJoin(ckanmetadataInfo().localgovInfo())
				.where(Operations.eq(CkanmetadataInfoNames.infotype(), metadataId))
				.orderBy(new OrderByItem(ckanmetadataInfo().id(), OrderingSpec.DESC)) // 登録の新しい順
				.getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(CkanmetadataInfo entity, DeleteCascadeResult result) {

		// クリアリングハウスカスケード削除
		if(StringUtil.isNotEmpty(entity.name)) {
			result.cascadeClearinghouse(entity.name, entity, String.valueOf(entity.id));
		}

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
