/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.telemeterfileData;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import jp.ecom_plat.saigaitask.entity.db.TelemeterfileData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class TelemeterfileDataService extends AbstractService<TelemeterfileData> {

    public TelemeterfileData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * ファイル名で検索 
     * @param fname
     * @return ファイル情報
     */
    public TelemeterfileData findByFilename(String fname) {
    	return select().where(eq(telemeterfileData().filename(), fname)).limit(1).getSingleResult();
    }
}
