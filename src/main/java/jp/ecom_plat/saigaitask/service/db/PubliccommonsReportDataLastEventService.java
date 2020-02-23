/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static org.seasar.extension.jdbc.operation.Operations.asc;

import java.util.List;

import javax.annotation.Generated;

import org.seasar.extension.jdbc.operation.Operations;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastEvent;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * {@link PubliccommonsReportDataLastEvent}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/09/25 12:06:18")
@org.springframework.stereotype.Repository
public class PubliccommonsReportDataLastEventService extends AbstractService<PubliccommonsReportDataLastEvent> {

    /**
     * 識別子でエンティティを検索します。
     *
     * @param id
     *            識別子
     * @return エンティティ
     */
    public PubliccommonsReportDataLastEvent findById(Long id) {
        return select().id(id).getSingleResult();
    }


    /**
     * 自治体IDでエンティティを検索します。
     *
     * @param localgovinfoid
     *            自治体ID
     * @return エンティティ
     */
    public List<PubliccommonsReportDataLastEvent> findByLocalgovInfoId(long localgovinfoid) {
    	return select().where(Operations.eq(Names.publiccommonsReportDataLastEvent().localgovinfoid(), localgovinfoid)).orderBy(asc(Names.publiccommonsReportDataLastEvent().id())).getResultList();
    }
}