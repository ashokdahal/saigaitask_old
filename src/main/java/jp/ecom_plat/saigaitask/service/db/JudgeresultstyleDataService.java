/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.judgeresultstyleData;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class JudgeresultstyleDataService extends AbstractService<JudgeresultstyleData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 判定結果スタイルデータ
	 */
    public JudgeresultstyleData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * テレメータデータIDで検索
     * @param dataid
     * @return 判定結果スタイルデータ
     */
    public JudgeresultstyleData findByTelemeterDataId(Long dataid) {
    	return select().where(eq(judgeresultstyleData().telemeterdataid(), dataid)).limit(1).getSingleResult();
    }
}
