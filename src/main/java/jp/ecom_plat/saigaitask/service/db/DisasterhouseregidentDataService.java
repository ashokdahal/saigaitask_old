/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterhouseregidentData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterhouseregidentData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterhouseregidentDataService extends AbstractService<DisasterhouseregidentData> {

    public DisasterhouseregidentData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況住家被害データリスト
     */
    public List<DisasterhouseregidentData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterhouseregidentData().trackdataid(), tid)
    			).orderBy(desc(disasterhouseregidentData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況住家被害データ
     */
    public DisasterhouseregidentData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterhouseregidentData().trackdataid(), tid),
    			isNotNull(disasterhouseregidentData().registtime())
    			).orderBy(desc(disasterhouseregidentData().registtime())).
    				orderBy(desc(disasterhouseregidentData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況住家被害データ
     */
    public DisasterhouseregidentData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterhouseregidentData().trackdataid(), tid),
    		le(disasterhouseregidentData().registtime(), time)
    		).orderBy(desc(disasterhouseregidentData().registtime())).
    			orderBy(desc(disasterhouseregidentData().id())).limit(1).getSingleResult();
    }
}
