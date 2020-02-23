/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disastercasualtiesData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisastercasualtiesData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisastercasualtiesDataService extends AbstractService<DisastercasualtiesData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 被災状況人的被害データ
	 */
    public DisastercasualtiesData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況人的被害データリスト
     */
    public List<DisastercasualtiesData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disastercasualtiesData().trackdataid(), tid)
    			).orderBy(desc(disastercasualtiesData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況人的被害データ
     */
    public DisastercasualtiesData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disastercasualtiesData().trackdataid(), tid),
    			isNotNull(disastercasualtiesData().registtime())
    			).orderBy(desc(disastercasualtiesData().registtime())).
    				orderBy(desc(disastercasualtiesData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況人的被害データ
     */
    public DisastercasualtiesData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    			eq(disastercasualtiesData().trackdataid(), tid),
    			le(disastercasualtiesData().registtime(), time)
    		).orderBy(desc(disastercasualtiesData().registtime())).
    			orderBy(desc(disastercasualtiesData().id())).limit(1).getSingleResult();
    }

    /**
     * 合計値を返す。
     * @param list
     * @return 被災状況人的被害データ合計値
     */
    /*public DisastercasualtiesData getTotal(List<DisastercasualtiesData> list) {
    	DisastercasualtiesData data = new DisastercasualtiesData();
    	data.casualties1 = Integer.valueOf(0);
    	data.casualties2 = Integer.valueOf(0);
    	data.casualties3 = Integer.valueOf(0);
    	data.casualties4 = Integer.valueOf(0);
    	for (DisastercasualtiesData one : list) {
    		data.casualties1 += one.casualties1;
    		data.casualties2 += one.casualties2;
    		data.casualties3 += one.casualties3;
    		data.casualties4 += one.casualties4;
    	}
    	return data;
    }*/
    
    public List<DisastercasualtiesData> check() {
    	List<DisastercasualtiesData> reslist = select().leftOuterJoin(disastercasualtiesData().trackData()).getResultList();
    	List<DisastercasualtiesData> nolist = new ArrayList<DisastercasualtiesData>();
    	for (DisastercasualtiesData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
