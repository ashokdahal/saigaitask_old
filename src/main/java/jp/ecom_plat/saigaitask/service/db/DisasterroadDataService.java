/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterroadData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterroadData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterroadDataService extends AbstractService<DisasterroadData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況土木被害データ
	 */
    public DisasterroadData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況土木被害データリスト
     */
    public List<DisasterroadData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterroadData().trackdataid(), tid)
    			).orderBy(desc(disasterroadData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況土木被害データ
     */
    public DisasterroadData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterroadData().trackdataid(), tid),
    			isNotNull(disasterroadData().registtime())
    			).orderBy(desc(disasterroadData().registtime())).
    				orderBy(desc(disasterroadData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況土木被害データ
     */
    public DisasterroadData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterroadData().trackdataid(), tid),
    		le(disasterroadData().registtime(), time)
    		).orderBy(desc(disasterroadData().registtime())).
    			orderBy(desc(disasterroadData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterroadData> check() {
    	List<DisasterroadData> reslist = select().leftOuterJoin(disasterroadData().trackData()).getResultList();
    	List<DisasterroadData> nolist = new ArrayList<DisasterroadData>();
    	for (DisasterroadData road : reslist) {
    		if (road.trackData == null)
    			nolist.add(road);
    	}
    	return nolist;
    }
}
