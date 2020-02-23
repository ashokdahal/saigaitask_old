package jp.ecom_plat.saigaitask.service.db;

import java.util.List;
import javax.annotation.Generated;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupmemberInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

import static jp.ecom_plat.saigaitask.entity.names.LocalgovgroupmemberInfoNames.*;
import static org.seasar.extension.jdbc.operation.Operations.*;

/**
 * {@link LocalgovgroupmemberInfo}のサービスクラスです。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2018/10/09 12:13:50")
@org.springframework.stereotype.Repository
public class LocalgovgroupmemberInfoService extends AbstractService<LocalgovgroupmemberInfo> {

    /**
     * 識別子でエンティティを検索します。
     * 
     * @param id
     *            識別子
     * @return エンティティ
     */
    public LocalgovgroupmemberInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     * 
     * @return エンティティのリスト
     */
    public List<LocalgovgroupmemberInfo> findAllOrderById() {
        return select().orderBy(asc(id())).getResultList();
    }

    /**
     * 自治体グループ情報IDで検索
     * @param localgovgroupinfoid 自治体グループ情報ID
     * @return 自治体グループメンバー情報
     */
	public List<LocalgovgroupmemberInfo> findByLocalgovgroupinfoid(Long localgovgroupinfoid) {
        return select().where(eq(localgovgroupinfoid(),localgovgroupinfoid)).orderBy(asc(disporder())).getResultList();
	}
}