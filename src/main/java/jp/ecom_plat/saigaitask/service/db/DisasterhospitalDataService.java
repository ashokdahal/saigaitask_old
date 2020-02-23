/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterhospitalData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterhospitalData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterhospitalDataService extends AbstractService<DisasterhospitalData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況病院被害データ
	 */
    public DisasterhospitalData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況病院被害データリスト
     */
    public List<DisasterhospitalData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterhospitalData().trackdataid(), tid)
    			).orderBy(desc(disasterhospitalData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況病院被害データ
     */
    public DisasterhospitalData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterhospitalData().trackdataid(), tid),
    			isNotNull(disasterhospitalData().registtime())
    			).orderBy(desc(disasterhospitalData().registtime())).
    				orderBy(desc(disasterhospitalData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況病院被害データ合計値
     */
    public DisasterhospitalData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterhospitalData().trackdataid(), tid),
    		le(disasterhospitalData().registtime(), time)
    		).orderBy(desc(disasterhospitalData().registtime())).
    			orderBy(desc(disasterhospitalData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterhospitalData> check() {
    	List<DisasterhospitalData> reslist = select().leftOuterJoin(disasterhospitalData().trackData()).getResultList();
    	List<DisasterhospitalData> nolist = new ArrayList<DisasterhospitalData>();
    	for (DisasterhospitalData data : reslist) {
    		if (data.trackData == null) 
    			nolist.add(data);
    	}
    	return reslist;
    }
}
