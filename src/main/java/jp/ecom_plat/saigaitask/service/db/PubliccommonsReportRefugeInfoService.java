/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.publiccommonsReportRefugeInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;

import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportRefugeInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * {@link PubliccommonsReportRefugeInfo}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/09/25 12:06:18")
@org.springframework.stereotype.Repository
public class PubliccommonsReportRefugeInfoService extends AbstractService<PubliccommonsReportRefugeInfo> {

    /**
     * 識別子でエンティティを検索します。
     *
     * @param id
     *            識別子
     * @return エンティティ
     */
    public PubliccommonsReportRefugeInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 自治体IDでエンティティを検索します。
     *
     * @param localgovinfoid
     *
     * @return エンティティ
     */
    public PubliccommonsReportRefugeInfo findByLocalgovInfoId(long localgovinfoid) {
    	//return select().where(Operations.eq(Names.publiccommonsReportRefugeInfo().localgovinfoid(), localgovinfoid)).getSingleResult();
    	return select().where(Operations.eq(publiccommonsReportRefugeInfo().localgovinfoid(), localgovinfoid)).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(publiccommonsReportRefugeInfo().tablemasterInfo(), ne(publiccommonsReportRefugeInfo().tablemasterInfo().deleted(), true))
			.innerJoin(publiccommonsReportRefugeInfo().localgovInfo())
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
	public List<PubliccommonsReportRefugeInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = publiccommonsReportRefugeInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!publiccommonsReportRefugeInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(publiccommonsReportRefugeInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(publiccommonsReportRefugeInfo().tablemasterInfo(), ne(publiccommonsReportRefugeInfo().tablemasterInfo().deleted(), true))
			.innerJoin(publiccommonsReportRefugeInfo().localgovInfo())
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
	public int update(PubliccommonsReportRefugeInfo entity, PropertyName<?>[] excludes) {
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

	public List<PubliccommonsReportRefugeInfo> check() {
		List<PubliccommonsReportRefugeInfo> reslist = select().leftOuterJoin(publiccommonsReportRefugeInfo().localgovInfo())
				.leftOuterJoin(publiccommonsReportRefugeInfo().tablemasterInfo()).getResultList();
		List<PubliccommonsReportRefugeInfo> nolist = new ArrayList<PubliccommonsReportRefugeInfo>();
		for (PubliccommonsReportRefugeInfo info : reslist) {
			if (info.localgovInfo == null || info.tablemasterInfo == null)
				nolist.add(info);
		}
		return nolist;
	}
}
