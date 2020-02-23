/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.alertcontentData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class AlertcontentDataService extends AbstractService<AlertcontentData> {

    public AlertcontentData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * 自治体IDで検索
     * @param govid
     * @param offset
     * @param limit
     * @return
     */
    public List<AlertcontentData> findByLocalgovinfoid(Long govid, int offset, int limit) {
    	return select().where(eq(alertcontentData().localgovinfoid(), govid))
    			.orderBy(desc(alertcontentData().receivetime())).offset(offset).limit(limit).getResultList();
    }
    
    /**
     * 自治体IDでカウント
     * @param govid
     * @return
     */
    public long getCount(Long govid) {
    	return select().where(eq(alertcontentData().localgovinfoid(), govid)).getCount();
    }
}
