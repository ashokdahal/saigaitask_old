package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.names.LocalgovgroupInfoNames.id;
import static org.seasar.extension.jdbc.operation.Operations.asc;

import java.util.List;

import javax.annotation.Generated;

import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupmemberInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * {@link LocalgovgroupInfo}のサービスクラスです。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2018/10/09 12:13:49")
@org.springframework.stereotype.Repository
public class LocalgovgroupInfoService extends AbstractService<LocalgovgroupInfo> {

    /**
     * 識別子でエンティティを検索します。
     * 
     * @param id
     *            識別子
     * @return エンティティ
     */
    public LocalgovgroupInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     * 
     * @return エンティティのリスト
     */
    public List<LocalgovgroupInfo> findAllOrderById() {
        return select().orderBy(asc(id())).getResultList();
    }


	public List<LocalgovgroupInfo> findByLocalgovinfoidOrderByDisporder(long filterlocalgovinfoid) {
	    return select()
	    		.leftOuterJoin(Names.localgovgroupInfo().localgovgroupmemberInfoList())
	    		// 未削除のものかつ
	    		.where(Operations.ne(Names.localgovgroupInfo().deleted(), true),
	    				// 自治体グループのオーナー自治体、または所属メンバー
	    				Operations.or(
	    						Operations.eq(Names.localgovgroupInfo().localgovinfoid(), filterlocalgovinfoid),
	    						Operations.eq(Names.localgovgroupInfo().localgovgroupmemberInfoList().localgovinfoid(), filterlocalgovinfoid)
	    				))
	    		.orderBy(asc(Names.localgovgroupInfo().disporder()))
	    		.getResultList();
	}

	public int getLargestDisporder() {
		BeanMap conditions = new BeanMap();
		conditions.put(Names.localgovgroupInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);

		LocalgovgroupInfo entity = select()
		.where(conditions)
		.orderBy(new OrderByItem(Names.localgovgroupInfo().disporder().toString(), OrderingSpec.DESC))
		.limit(1)
		.getSingleResult();

		if(entity!=null) return entity.disporder;
		return 0;
	}
	
	@Override
	public DeleteCascadeResult deleteCascade(LocalgovgroupInfo entity, DeleteCascadeResult result) {

		result.cascade(LocalgovgroupmemberInfo.class, Names.localgovgroupmemberInfo().localgovgroupinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}