/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterfireData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterfireData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterfireDataService extends AbstractService<DisasterfireData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況火災発生
	 */
    public DisasterfireData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況火災発生データリスト
     */
    public List<DisasterfireData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterfireData().trackdataid(), tid)
    			).orderBy(desc(disasterfireData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況火災発生データ
     */
    public DisasterfireData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterfireData().trackdataid(), tid),
    			isNotNull(disasterfireData().registtime())
    			).orderBy(desc(disasterfireData().registtime())).
    				orderBy(desc(disasterfireData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況火災発生データ
     */
    public DisasterfireData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterfireData().trackdataid(), tid),
    		le(disasterfireData().registtime(), time)
    		).orderBy(desc(disasterfireData().registtime())).
    			orderBy(desc(disasterfireData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterfireData> check() {
    	List<DisasterfireData> reslist = select().leftOuterJoin(disasterfireData().trackData()).getResultList();
    	List<DisasterfireData> nolist = new ArrayList<DisasterfireData>();
    	for (DisasterfireData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
