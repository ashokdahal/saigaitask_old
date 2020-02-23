/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disasterhouseData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.le;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisasterhouseData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DisasterhouseDataService extends AbstractService<DisasterhouseData> {

	/**
	 * IDで検索
	 *
	 * @param id
	 * @return 被災状況住家被害データ
	 */
    public DisasterhouseData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 被災状況住家被害データリスト
     */
    public List<DisasterhouseData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterhouseData().trackdataid(), tid)
    			).orderBy(desc(disasterhouseData().registtime())).getResultList();
    }

    /**
     * 記録IDでの最新の検索結果を返す。
     * @param tid
     * @return 被災状況住家被害データ
     */
    public DisasterhouseData getLatestByTrackDataId(Long tid) {
    	return select().where(
    			eq(disasterhouseData().trackdataid(), tid),
    			isNotNull(disasterhouseData().registtime())
    			).orderBy(desc(disasterhouseData().registtime())).
    				orderBy(desc(disasterhouseData().id())).limit(1).getSingleResult();
    }

    /**
     * 記録IDと指定日時以降の最新の検索結果を返す。
     * @param tid	trackdataid
     * @param time	指定日時
     * @return 被災状況住家被害データ
     */
    public DisasterhouseData getLatestByTrackDataIdLETime(Long tid, Timestamp time) {
    	return select().where(
    		eq(disasterhouseData().trackdataid(), tid),
    		le(disasterhouseData().registtime(), time)
    		).orderBy(desc(disasterhouseData().registtime())).
    			orderBy(desc(disasterhouseData().id())).limit(1).getSingleResult();
    }

    /**
     * 合計値を返す。
     * @param list
     * @return 被災状況人的被害データ合計値
     */
    //public DisasterhouseData[] getTotals(DisasterhouseData one) {
    //	if (one == null) return null;
    //	DisasterhouseData[] datas = new DisasterhouseData[3];
    	/*for (int idx = 0; idx < 3; idx++) {
    		datas[idx] = new DisasterhouseData();
    		DisasterhouseData data = datas[idx];
    		data.houseall = Integer.valueOf(0);
    		data.househalf = Integer.valueOf(0);
    		data.housepart = Integer.valueOf(0);
    		data.houseupper = Integer.valueOf(0);
    		data.houselower = Integer.valueOf(0);
        	
    		if (one.houseunitid1 != null && idx+1 == one.houseunitid1)
    			data.houseall += one.houseall;
    		if (one.houseunitid2 != null && idx+1 == one.houseunitid2)
    			data.househalf += one.househalf;
    		if (one.houseunitid3 != null && idx+1 == one.houseunitid3)
    			data.housepart += one.housepart;
    		if (one.houseunitid4 != null && idx+1 == one.houseunitid4)
    			data.houseupper += one.houseupper;
    		if (one.houseunitid5 != null && idx+1 == one.houseunitid5)
    			data.houselower += one.houselower;
    	}*/
    //	return datas;
    //}
    
    public List<DisasterhouseData> check() {
    	List<DisasterhouseData> reslist = select().leftOuterJoin(disasterhouseData().trackData()).getResultList();
    	List<DisasterhouseData> nolist = new ArrayList<DisasterhouseData>();
    	for(DisasterhouseData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	/*reslist = select().leftOuterJoin(disasterhouseData().houseunitMaster1()).where(and(
    			ne(disasterhouseData().houseunitid1(), 0),
    			isNotNull(disasterhouseData().houseunitid1()))).getResultList();
    	for (DisasterhouseData data : reslist) {
    		if (data.houseunitMaster1 == null)
    			nolist.add(data);
    	}
    	reslist = select().leftOuterJoin(disasterhouseData().houseunitMaster2()).where(and(
    			ne(disasterhouseData().houseunitid2(), 0),
    			isNotNull(disasterhouseData().houseunitid2()))).getResultList();
    	for (DisasterhouseData data : reslist) {
    		if (data.houseunitMaster2 == null)
    			nolist.add(data);
    	}
    	reslist = select().leftOuterJoin(disasterhouseData().houseunitMaster3()).where(and(
    			ne(disasterhouseData().houseunitid3(), 0),
    			isNotNull(disasterhouseData().houseunitid3()))).getResultList();
    	for (DisasterhouseData data : reslist) {
    		if (data.houseunitMaster3 == null)
    			nolist.add(data);
    	}
    	reslist = select().leftOuterJoin(disasterhouseData().houseunitMaster4()).where(and(
    			ne(disasterhouseData().houseunitid4(), 0),
    			isNotNull(disasterhouseData().houseunitid4()))).getResultList();
    	for (DisasterhouseData data : reslist) {
    		if (data.houseunitMaster4 == null)
    			nolist.add(data);
    	}
    	reslist = select().leftOuterJoin(disasterhouseData().houseunitMaster5()).where(and(
    			ne(disasterhouseData().houseunitid5(), 0),
    			isNotNull(disasterhouseData().houseunitid5()))).getResultList();
    	for (DisasterhouseData data : reslist) {
    		if (data.houseunitMaster5 == null)
    			nolist.add(data);
    	}*/
    	return nolist;
    }
}
