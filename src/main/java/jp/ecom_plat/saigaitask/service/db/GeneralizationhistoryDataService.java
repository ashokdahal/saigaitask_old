/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.GeneralizationhistoryData;
import jp.ecom_plat.saigaitask.entity.names.GeneralizationhistoryDataNames;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class GeneralizationhistoryDataService extends AbstractService<GeneralizationhistoryData> {

    public GeneralizationhistoryData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    public List<GeneralizationhistoryData> findByTrackdataidAndPagetypeAndListid(long trackdataid, String pagetype, String listId) {
        return select()
        		.where(
        			and(
        				eq(GeneralizationhistoryDataNames.trackdataid(), trackdataid),
        				eq(GeneralizationhistoryDataNames.pagetype(), pagetype),
        				eq(GeneralizationhistoryDataNames.listid(), listId)
        			)
        		)
        		.getResultList();
    }
}
