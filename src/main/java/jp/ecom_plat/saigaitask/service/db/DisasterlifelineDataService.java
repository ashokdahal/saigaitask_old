/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterlifelineData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterlifelineData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterlifelineDataService extends AbstractService<DisasterlifelineData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況ライフライン被害データ
	 */
    public DisasterlifelineData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況土木被害データリスト
     */
    public List<DisasterlifelineData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterlifelineData().trackdataid(), tid)
    			).orderBy(desc(disasterlifelineData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況土木被害データ
     */
    public DisasterlifelineData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterlifelineData().trackdataid(), tid),
    			isNotNull(disasterlifelineData().registtime())
    			).orderBy(desc(disasterlifelineData().registtime())).
    				orderBy(desc(disasterlifelineData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況土木被害データ
     */
    public DisasterlifelineData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterlifelineData().trackdataid(), tid),
    		le(disasterlifelineData().registtime(), time)
    		).orderBy(desc(disasterlifelineData().registtime())).
    			orderBy(desc(disasterlifelineData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterlifelineData> check() {
    	List<DisasterlifelineData> reslist = select().leftOuterJoin(disasterlifelineData()).getResultList();
    	List<DisasterlifelineData> nolist = new ArrayList<DisasterlifelineData>();
    	for (DisasterlifelineData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
