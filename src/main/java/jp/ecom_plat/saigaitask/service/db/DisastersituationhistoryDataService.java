/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disastersituationhistoryData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisastersituationhistoryData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisastersituationhistoryDataService extends AbstractService<DisastersituationhistoryData> {

    public DisastersituationhistoryData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDでデータを検索
     * @param trackdataid 記録データID
     * @return 被災状況履歴データリスト
     */
	public List<DisastersituationhistoryData> findByTrackDataId(Long tid) {
		List<DisastersituationhistoryData> rtn = select().innerJoin(disastersituationhistoryData().disastersummaryhistoryData())
 	             .where(
 	            		 	and(
	 	            			eq(disastersituationhistoryData().disastersummaryhistoryData().trackdataid(), tid)
	            			)
 	            )
 	            .orderBy(desc("period")).getResultList();
		return rtn;
	}

	/**
     * 被災集計履歴IDで検索
     * @param disastersummaryhistoryid 被災集計履歴ID
     * @return 被災状況履歴データリスト
     */
    public List<DisastersituationhistoryData> findByDisastersummaryhistoryid(Long disastersummaryhistoryid) {
    	return select().where(
    			eq(disastersituationhistoryData().disastersummaryhistoryid(), disastersummaryhistoryid)
    			).orderBy(asc(disastersituationhistoryData().lineno())).getResultList();
    }
}