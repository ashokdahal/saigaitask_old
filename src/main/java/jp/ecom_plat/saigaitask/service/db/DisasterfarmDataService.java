/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterfarmData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterfarmData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterfarmDataService extends AbstractService<DisasterfarmData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況農林被害データ
	 */
    public DisasterfarmData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況農林被害データリスト
     */
    public List<DisasterfarmData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterfarmData().trackdataid(), tid)
    			).orderBy(desc(disasterfarmData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況農林被害データ
     */
    public DisasterfarmData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterfarmData().trackdataid(), tid),
    			isNotNull(disasterfarmData().registtime())
    			).orderBy(desc(disasterfarmData().registtime())).
    				orderBy(desc(disasterfarmData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況農林被害データ
     */
    public DisasterfarmData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    			eq(disasterfarmData().trackdataid(), tid),
    			le(disasterfarmData().registtime(), time)
    			).orderBy(desc(disasterfarmData().registtime())).
    				orderBy(desc(disasterfarmData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterfarmData> check() {
    	List<DisasterfarmData> reslist = select().leftOuterJoin(disasterfarmData().trackData()).getResultList();
    	List<DisasterfarmData> nolist = new ArrayList<DisasterfarmData>();
    	for (DisasterfarmData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
