/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.judgeresultData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import jp.ecom_plat.saigaitask.entity.db.JudgeresultData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class JudgeresultDataService extends AbstractService<JudgeresultData> {

	/**
	 * IDで検索
	 * @param id ID
	 * @return 判定結果データ
	 */
    public JudgeresultData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * 判定管理IDで検索
     * @param jmid 判定管理ID
     * @return 判定結果データ
     */
    public JudgeresultData findByJudgemanInfoId(Long jmid) {
    	return select().where(eq(judgeresultData().judgemaninfoid(), jmid))
    			.orderBy(desc(judgeresultData().judgetime())).limit(1).getSingleResult();
    }
}
