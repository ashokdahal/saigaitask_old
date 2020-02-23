/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.names.HistorycolumnlistInfoNames.id;
import static org.seasar.extension.jdbc.operation.Operations.asc;

import java.util.List;

import javax.annotation.Generated;

import org.seasar.framework.beans.util.BeanMap;

import jp.ecom_plat.saigaitask.entity.db.HistorycolumnlistInfo;
import jp.ecom_plat.saigaitask.entity.names.HistorycolumnlistInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * {@link HistorycolumnlistInfo}のサービスクラスです。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/10/07 18:06:11")
@org.springframework.stereotype.Repository
public class HistorycolumnlistInfoService extends AbstractService<HistorycolumnlistInfo> {

    /**
     * 識別子でエンティティを検索します。
     * 
     * @param id
     *            識別子
     * @return エンティティ
     */
    public HistorycolumnlistInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     * 
     * @return エンティティのリスト
     */
    public List<HistorycolumnlistInfo> findAllOrderById() {
        return select().orderBy(asc(id())).getResultList();
    }
 
    public List<HistorycolumnlistInfo> findByHistorytableinfoid(Long id){
		BeanMap conditions = new BeanMap();
		conditions.put(HistorycolumnlistInfoNames.historytableinfoid().toString(), id);
		return findByCondition(conditions);
    }
}