/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.threadsendtoData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNull;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.ThreadsendtoData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class ThreadsendtoDataService extends AbstractService<ThreadsendtoData> {

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
    public ThreadsendtoData findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * threaddataidで検索
	 * @param threaddataid
	 * @return threaddataidを送信先として設定されたレコード群
	 */
    public List<ThreadsendtoData> findBythreaddataId(Long threaddataid) {
        return select().where( eq(threadsendtoData().threaddataid(), threaddataid) ).getResultList();
    }

	/**
	 * threaddataidとgroupidで検索
	 * @param threaddataid
	 * @param groupid
	 * @return スレッドの該当グループが持つレコード
	 * @deprecated
	 */
    public ThreadsendtoData findBythreaddataIdGroupId(Long threaddataid, Long groupid) {
    	return findBythreaddataIdGroupId(threaddataid, threadsendtoData().groupid(), groupid);
    }
    public ThreadsendtoData findBythreaddataIdGroupId(Long threaddataid, Boolean usual, Long groupid, Long unitid) {
    	if (!usual)
    		return findBythreaddataIdGroupId(threaddataid, threadsendtoData().groupid(), groupid);
    	return findBythreaddataIdGroupId(threaddataid, threadsendtoData().unitid(), unitid);
    }
    public ThreadsendtoData findBythreaddataIdGroupId(Long threaddataid, PropertyName<Long> idname, Long groupid) {
        return select().where( eq(threadsendtoData().threaddataid(), threaddataid), eq(idname, groupid) ).limit(1).getSingleResult();
    }

	/**
	 * 最終更新時間（既読含む）で検索
	 * @param threadID : スレッドID
	 * @param groupID : グループID( nullなら全グループの最終更新 )
	 * @return 最終更新時刻
	 * @deprecated
	 */
    public Timestamp findByLatestUpdateTime(Long threadID, Long groupID){
    	return findByLatestUpdateTime(threadID, threadsendtoData().groupid(), groupID);
    }
    public Timestamp findByLatestUpdateTime(Long threadID, Boolean usual, Long groupID, Long unitID){
    	if (!usual)
    		return findByLatestUpdateTime(threadID, threadsendtoData().groupid(), groupID);
    	return findByLatestUpdateTime(threadID, threadsendtoData().unitid(), unitID);
    }
    private Timestamp findByLatestUpdateTime(Long threadID, PropertyName<Long> idname, Long groupID){
    	ThreadsendtoData sendData = select().where(
    			and(
    				eq(threadsendtoData().threaddataid(), threadID),
    				eq(idname, groupID)
    			)).orderBy(desc(threadsendtoData().updatetime())).limit(1).getSingleResult();
    	if(sendData != null)
    		return sendData.updatetime;
    	else
    		return null;
    }

	/**
	 * 送信先に設定されたスレッド一覧のIDを取得する
	 * @return Long[]
	 * @deprecated
	 */
    public Long[] findByOwnThreadList(Long groupID){
    	return findByOwnThreadList(threadsendtoData().groupid(), groupID);
    }
    public Long[] findByOwnThreadList(Boolean usual, Long groupID, Long unitID){
    	if (!usual)
    		return findByOwnThreadList(threadsendtoData().groupid(), groupID);
    	return findByOwnThreadList(threadsendtoData().unitid(), unitID);
    }
    private Long[] findByOwnThreadList(PropertyName<Long> idname, Long groupID){
    	List<ThreadsendtoData> threadList = select()
    			.innerJoin(threadsendtoData().threadData())
    			.where(
    				and(
    					eq(idname, groupID),
    					ne(threadsendtoData().threadData().deleted(), true)
					) ).orderBy(desc(threadsendtoData().updatetime())).getResultList();
    	if(threadList == null) return new Long[]{};
    	Long[] returndata = new Long[threadList.size()];
    	for(int i = 0; i < threadList.size(); i++){
    		returndata[i] = threadList.get(i).threaddataid;
    	}
    	return returndata;
    }

	/**
	 * 送信先に設定されたスレッド一覧のIDを取得する
	 * @return Long[]
	 */
    public List<ThreadsendtoData> findByOwnThreadList2(Long groupID){
    	return select()
    		.innerJoin(threadsendtoData().threadData())
    		.where(
    			and(
   					eq(threadsendtoData().groupid(), groupID),
   					ne(threadsendtoData().threadData().deleted(), true)
    			) ).orderBy(desc(threadsendtoData().updatetime())).getResultList();
    }

    /**
	 * 送信先に設定された平時スレッド一覧のIDを取得する
	 * orderby句は遅くなる事、responseDataの更新時間でソートをかける事を前提に、orderby句は使用していない
	 * @return List<ThreadsendtoData>
	 * @deprecated
	 */
    public List<ThreadsendtoData> findByOwnNormalThreadList(Long groupID){
    	return findByOwnNormalThreadList(threadsendtoData().groupid(), groupID);
    }
    public List<ThreadsendtoData> findByOwnNormalThreadList(Boolean usual, Long groupID, Long unitID){
    	if (!usual)
    		return findByOwnNormalThreadList(threadsendtoData().groupid(), groupID);
    	return findByOwnNormalThreadList(threadsendtoData().unitid(), unitID);
    }
    private List<ThreadsendtoData> findByOwnNormalThreadList(PropertyName<Long> idname, Long groupID){
    	return select()
    		.innerJoin(threadsendtoData().threadData())
    		.where(
    			and(
    				eq(idname, groupID),
    				isNull(threadsendtoData().threadData().trackdataid()),
    				ne(threadsendtoData().threadData().deleted(), true)
    			)
    		).getResultList();
    }

    /**
	 * 送信先に設定された災害スレッド一覧のIDを取得する
	 * orderby句は遅くなる事、responseDataの更新時間でソートをかける事を前提に、orderby句は使用していない
	 * @return List<ThreadsendtoData>
	 */
    public List<ThreadsendtoData> findByOwnTrackThreadList(Long groupID, Long trackdataid){
    	return select()
    		.innerJoin(threadsendtoData().threadData())
    		.where(
    			and(
    				eq(threadsendtoData().groupid(), groupID),
    				eq(threadsendtoData().threadData().trackdataid(), trackdataid),
    				ne(threadsendtoData().threadData().deleted(), true)
    			)
    		).getResultList();
    }


	/**
	 * threaddataidとgroupidで自身が送信先に含まれるスレッドか検索
	 * @param threaddataid
	 * @param groupid
	 * @return 該当スレッドの送信先に含まれている場合はtrue
	 * @deprecated
	 */
    public boolean checkThreadsendtoMe(Long threaddataid, Long groupid) {
    	return checkThreadsendtoMe(threaddataid, threadsendtoData().groupid(), groupid);
    }
    public boolean checkThreadsendtoMe(Long threaddataid, Boolean usual, Long groupid, Long unitid) {
    	if (!usual)
    		return checkThreadsendtoMe(threaddataid, threadsendtoData().groupid(), groupid);
    	return checkThreadsendtoMe(threaddataid, threadsendtoData().unitid(), unitid);
    }
    private boolean checkThreadsendtoMe(Long threaddataid, PropertyName<Long> idname, Long groupid) {
    	long threadcnt = select()
    			.innerJoin(threadsendtoData().threadData())
    			.where(
    				and(
    					eq(threadsendtoData().threaddataid(), threaddataid),
    					eq(idname, groupid),
    					ne(threadsendtoData().threadData().deleted(), true)
    				) ).getCount();
    	if(threadcnt > 0) return true;
    	else return false;
    }

	/**
	 * 全スレッド一覧のIDを取得する
	 * @param List<GroupInfo> : 連携しているグループのID群
	 * @return Long[]
	 */
    public Long[] findByThreadList(List<GroupInfo> groupList){
    	if(groupList == null || groupList.size() == 0) return new Long[0];
    	// グループID格納用
    	String ids = "";
    	for(GroupInfo gInfo : groupList){
    		if(!ids.equals("")) ids += ",";
    		ids += gInfo.id;
    	}
    	return findByThreadList("groupid", ids);
    }
    private Long[] findByThreadList(String idname, String ids){
    	// エラーチェック用
    	if(ids.equals("")) return new Long[0];

    	List<ThreadsendtoData> threadList = jdbcManager.selectBySql(ThreadsendtoData.class,
    			"SELECT threaddataid,max(updatetime) as updatetime FROM threadsendto_data"
    			+ " inner join thread_data on (threadsendto_data.threaddataid = thread_data.id)"
    			+ " WHERE threadsendto_data."+idname+" IN ("+ ids +") and deleted <> true GROUP BY threaddataid ORDER BY updatetime DESC").getResultList();
    	if(threadList == null) return new Long[]{};
    	Long[] returndata = new Long[threadList.size()];
    	for(int i = 0; i < threadList.size(); i++){
    		returndata[i] = threadList.get(i).threaddataid;
    	}
    	return returndata;
    }
	/** 
	 * 全スレッド一覧のIDを取得する
	 * @param List<UnitInfo> : 連携しているユニットのID群
	 * @return Long[]
	 */
    public Long[] findByUnitThreadList(List<UnitInfo> unitList){
    	if(unitList == null || unitList.size() == 0) return new Long[0];
    	// グループID格納用
    	String ids = "";
    	for(UnitInfo uInfo : unitList){
    		if(!ids.equals("")) ids += ",";
    		ids += uInfo.id;
    	}
    	return findByThreadList("unitid", ids);
    }
}
