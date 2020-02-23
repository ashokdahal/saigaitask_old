/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.clearinghousemetadataInfo;
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

import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadataInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/** クリアリングハウス事前データ情報サービス */
@org.springframework.stereotype.Repository
public class ClearinghousemetadataInfoService extends AbstractService<ClearinghousemetadataInfo> {

    public ClearinghousemetadataInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(clearinghousemetadataInfo().tablemasterInfo(), ne(clearinghousemetadataInfo().tablemasterInfo().deleted(), true))
			.innerJoin(clearinghousemetadataInfo().localgovInfo())
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
	public List<ClearinghousemetadataInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = clearinghousemetadataInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!clearinghousemetadataInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(clearinghousemetadataInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(clearinghousemetadataInfo().tablemasterInfo(),ne(clearinghousemetadataInfo().tablemasterInfo().deleted(), true))
			.innerJoin(clearinghousemetadataInfo().localgovInfo())
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
	public int update(ClearinghousemetadataInfo entity, PropertyName<?>[] excludes) {
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
	public List<ClearinghousemetadataInfo> findByLocalgovinfoId(long localgovinfoid) {
		return select()
				.innerJoin(clearinghousemetadataInfo().tablemasterInfo(),ne(clearinghousemetadataInfo().tablemasterInfo().deleted(), true))
				.innerJoin(clearinghousemetadataInfo().localgovInfo())
				.where(Operations.eq(ClearinghousemetadataInfoNames.localgovinfoid(), localgovinfoid))
				.getResultList();
	}

	public List<ClearinghousemetadataInfo> check() {
		List<ClearinghousemetadataInfo> reslist = select().innerJoin(clearinghousemetadataInfo().localgovInfo())
				.innerJoin(clearinghousemetadataInfo().tablemasterInfo()).getResultList();
		List<ClearinghousemetadataInfo> nolist = new ArrayList<ClearinghousemetadataInfo>();
		for (ClearinghousemetadataInfo data : reslist) {
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
	public List<ClearinghousemetadataInfo> findByMetadataid(String metadataId) {
		return select()
				.innerJoin(clearinghousemetadataInfo().tablemasterInfo(),ne(clearinghousemetadataInfo().tablemasterInfo().deleted(), true))
				.innerJoin(clearinghousemetadataInfo().localgovInfo())
				.where(Operations.eq(ClearinghousemetadataInfoNames.metadataid(), metadataId))
				.orderBy(new OrderByItem(clearinghousemetadataInfo().id(), OrderingSpec.DESC)) // 登録の新しい順
				.getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(ClearinghousemetadataInfo entity, DeleteCascadeResult result) {

		// クリアリングハウスカスケード削除
		if(StringUtil.isNotEmpty(entity.metadataid)) {
			result.cascadeClearinghouse(entity.metadataid, entity, String.valueOf(entity.id));
		}

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}