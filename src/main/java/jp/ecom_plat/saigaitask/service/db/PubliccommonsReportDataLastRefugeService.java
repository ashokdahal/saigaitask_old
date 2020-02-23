/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastRefuge;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * {@link PubliccommonsReportDataLastRefuge}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/09/25 12:06:18")
@org.springframework.stereotype.Repository
public class PubliccommonsReportDataLastRefugeService extends AbstractService<PubliccommonsReportDataLastRefuge> {

    /**
     * 識別子でエンティティを検索します。
     *
     * @param id
     *            識別子
     * @return エンティティ
     */
    public PubliccommonsReportDataLastRefuge findById(Long id) {
        return select().id(id).getSingleResult();
    }
}