/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.jalertreceivefileData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import jp.ecom_plat.saigaitask.entity.db.JalertreceivefileData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class JalertreceivefileDataService extends AbstractService<JalertreceivefileData> {

    public JalertreceivefileData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * 自治体IDとテキストファイル名で検索
     * @param govid
     * @param fname
     * @return
     */
    public JalertreceivefileData findByLocalgovInfoIdAndOrgtextfilename(Long govid, String fname) {
    	return select().where(
    			and(
    				eq(jalertreceivefileData().localgovinfoid(), govid),
    				eq(jalertreceivefileData().orgtextfilename(), fname)
    			)).limit(1).getSingleResult();
    }
}
