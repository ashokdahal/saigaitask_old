/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterwelfareData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterwelfareData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterwelfareDataService extends AbstractService<DisasterwelfareData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況民政被害データ
	 */
    public DisasterwelfareData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況民政被害データリスト
     */
    public List<DisasterwelfareData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterwelfareData().trackdataid(), tid)
    			).orderBy(desc(disasterwelfareData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況民政被害データ
     */
    public DisasterwelfareData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterwelfareData().trackdataid(), tid),
    			isNotNull(disasterwelfareData().registtime())
    			).orderBy(desc(disasterwelfareData().registtime())).orderBy(desc(disasterwelfareData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況民政被害データ
     */
    public DisasterwelfareData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterwelfareData().trackdataid(), tid),
    		le(disasterwelfareData().registtime(), time)
    		).orderBy(desc(disasterwelfareData().registtime())).orderBy(desc(disasterwelfareData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterwelfareData> check() {
    	List<DisasterwelfareData> reslist = select().leftOuterJoin(disasterwelfareData().trackData()).getResultList();
    	List<DisasterwelfareData> nolist = new ArrayList<DisasterwelfareData>();
    	for (DisasterwelfareData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
