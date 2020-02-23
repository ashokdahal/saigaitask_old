/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.threadresponseData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.gt;
import static org.seasar.extension.jdbc.operation.Operations.lt;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.db.ThreadresponseData;
import jp.ecom_plat.saigaitask.entity.db.ThreadsendtoData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class ThreadresponseDataService extends AbstractService<ThreadresponseData> {

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
    public ThreadresponseData findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * 対象グループの最終更新時間以降に投稿されたレスの数を検索
	 * @param groupID : 対象のグループID
	 * @param updateTime : 対象グループの最終更新時間
	 * @return 件数
	 */
    public int getUnReadCount(Long groupID, Timestamp updateTime){
    	return (int)select().where(
    			and(
    				lt(threadresponseData().registtime(), updateTime),
    				eq(threadresponseData().groupid(), groupID)
    			)).getCount();
    }
    
	/**
	 * 該当スレッドの対象グループの最終更新時間以降に投稿されたレスの数を検索
	 * @param threaddataid : スレッドのID
	 * @param updateTime : 対象グループの最終更新時間
	 * @param groupid
	 * @return 件数
	 * @deprecated
	 */
    public int getThreadUnReadCount(Long threaddataid, Timestamp updateTime, Long groupid){
    	return getThreadUnReadCount(threaddataid, updateTime, threadresponseData().groupid(), groupid);
    }
    public int getThreadUnReadCount(Long threaddataid, Timestamp updateTime, Boolean usual, Long groupid, Long unitid){
    	if (!usual)
    		return getThreadUnReadCount(threaddataid, updateTime, threadresponseData().groupid(), groupid);
    	return getThreadUnReadCount(threaddataid, updateTime, threadresponseData().unitid(), unitid);
    }
    public int getThreadUnReadCount(Long threaddataid, Timestamp updateTime, PropertyName<Long> idname, Long groupid){
    	return (int)select().where(
    			and(
    				gt(threadresponseData().registtime(), updateTime),
    				ne(idname, groupid),
    				eq(threadresponseData().threaddataid(), threaddataid)
    			)).getCount();
    }

	/**
	 * 対象スレッドの最終更新時間を検索
	 * @param threaddataid : 対象のスレッドID
	 * @return registtime : 最終投稿送信時間
	 */
    public Timestamp getLatestResponse(Long threaddataID){
    	ThreadresponseData responseData = select().where(eq(threadresponseData().threaddataid(), threaddataID)).orderBy(desc(threadresponseData().registtime())).limit(1).getSingleResult();
    	if(responseData != null)
    		return responseData.registtime;
    	else
    		return null;
    }

	/**
	 * 該当スレッドの検索時間以降に投稿されたレスを検索
	 * @param threaddataid : 対象のスレッドID
	 * @param updateTime : 対象グループの最終更新時間
	 * @return 件数
	 */
    public List<ThreadresponseData> getUnReadResponse(Long threaddataid, Timestamp updateTime){
    	return select().where(
    			and(
    				gt(threadresponseData().registtime(), updateTime),
    				eq(threadresponseData().threaddataid(), threaddataid)
    			)).orderBy(asc(threadresponseData().registtime())).getResultList();
    }

	/**
	 * 全スレッド一覧のIDを取得する
	 * @return Long[]
	 */
    public Long[] findByThreadList(){
    	List<ThreadsendtoData> threadList = jdbcManager.selectBySql(ThreadsendtoData.class,
    			"SELECT threaddataid, max(registtime) FROM threadresponse_data GROUP BY threaddataid").getResultList();
    	if(threadList == null) return new Long[]{};
    	Long[] returndata = new Long[threadList.size()];
    	for(int i = 0; i < threadList.size(); i++){
    		returndata[i] = threadList.get(i).threaddataid;
    	}
    	return returndata;
    }
	/**
	 * 最終書込み時間で検索
	 * @param threadID : スレッドID
	 * @return 最終更新時刻
	 */
    public Timestamp findByLatestWriteTime(Long threadID){
    	ThreadresponseData writeData = select()
    			.where( eq(threadresponseData().threaddataid(), threadID))
    			.orderBy(desc(threadresponseData().registtime())).limit(1).getSingleResult();
    	if(writeData != null)
    		return writeData.registtime;
    	else
    		return null;
    }
}
