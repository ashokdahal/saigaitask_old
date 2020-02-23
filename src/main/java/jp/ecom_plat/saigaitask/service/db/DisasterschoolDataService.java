/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterschoolData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterschoolData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterschoolDataService extends AbstractService<DisasterschoolData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況文教被害データ
	 */
    public DisasterschoolData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況文教被害データリスト
     */
    public List<DisasterschoolData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterschoolData().trackdataid(), tid)
    			).orderBy(desc(disasterschoolData().registtime())).getResultList();
    }

    /**
     * 記録IDで最新の検索結果を返す。
     * @param tid
     * @return 被災状況文教被害データ
     */
    public DisasterschoolData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterschoolData().trackdataid(), tid),
    			isNotNull(disasterschoolData().registtime())
    			).orderBy(desc(disasterschoolData().registtime())).orderBy(desc(disasterschoolData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況文教被害データ
     */
    public DisasterschoolData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterschoolData().trackdataid(), tid),
    		le(disasterschoolData().registtime(), time)
    		).orderBy(desc(disasterschoolData().registtime())).orderBy(desc(disasterschoolData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterschoolData> check() {
    	List<DisasterschoolData> reslist = select().leftOuterJoin(disasterschoolData().trackData()).getResultList();
    	List<DisasterschoolData> nolist = new ArrayList<DisasterschoolData>();
    	for (DisasterschoolData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
