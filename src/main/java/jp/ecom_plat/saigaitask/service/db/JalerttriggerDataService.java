/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import jp.ecom_plat.saigaitask.entity.db.JalerttriggerData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class JalerttriggerDataService extends AbstractService<JalerttriggerData> {

    public JalerttriggerData findById(Long id) {
        return select().id(id).getSingleResult();
    }
}
