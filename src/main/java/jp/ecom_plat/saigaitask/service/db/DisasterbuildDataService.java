/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterbuildData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterbuildData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterbuildDataService extends AbstractService<DisasterbuildData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況非住家被害データ
	 */
    public DisasterbuildData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況非住家被害データリスト
     */
    public List<DisasterbuildData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterbuildData().trackdataid(), tid)
    			).orderBy(desc(disasterbuildData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況非住家被害データ
     */
    public DisasterbuildData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterbuildData().trackdataid(), tid),
    			isNotNull(disasterbuildData().registtime())
    			).orderBy(desc(disasterbuildData().registtime())).
    				orderBy(desc(disasterbuildData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況非住家被害データ
     */
    public DisasterbuildData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterbuildData().trackdataid(), tid),
    		le(disasterbuildData().registtime(), time)
    		).orderBy(desc(disasterbuildData().registtime())).
    			orderBy(desc(disasterbuildData().id())).limit(1).getSingleResult();
    }
    
    public List<DisasterbuildData> check() {
    	List<DisasterbuildData> reslist = select().leftOuterJoin(disasterbuildData().trackData()).getResultList();
    	List<DisasterbuildData> nolist = new ArrayList<DisasterbuildData>();
    	for (DisasterbuildData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return reslist;
    }
}
