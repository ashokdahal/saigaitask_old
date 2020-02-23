/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.threadData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ge;
import static org.seasar.extension.jdbc.operation.Operations.le;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.ThreadData;
import jp.ecom_plat.saigaitask.entity.db.ThreadresponseData;
import jp.ecom_plat.saigaitask.entity.db.ThreadsendtoData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class ThreadDataService extends AbstractService<ThreadData> {


	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
    public ThreadData findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 * @deprecated
	 */
    public Boolean checkOwnThread(Long id, Long groupid) {
    	return checkOwnThread(id, false, threadData().groupid(), groupid);
    }
    public Boolean checkOwnThread(Long id, Boolean usual, Long groupid, Long unitid) {
    	if (!usual)
    		return checkOwnThread(id, usual, threadData().groupid(), groupid);
    	return checkOwnThread(id, usual, threadData().unitid(), unitid);
    }
    private Boolean checkOwnThread(Long id, Boolean usual, PropertyName<Long> idname, Long groupid) {
    	ThreadData threaddata = select().
    			where( and( eq(threadData().id(), id), eq(idname, groupid) ) ).getSingleResult();
    	if(!usual && threaddata.groupid.equals(groupid)) return true;
    	if (usual && threaddata.unitid.equals(groupid)) return true;
    	else return false;
    }

	/**
	 * IDで検索
	 * 該当のスレッドが災害モードの場合、完了しているかどうかをチェックする
	 * @param ID
	 * @return true : 平時 or 災害モード中   false : 災害モードが終了したスレッド
	 */
    public Boolean checkTrackDataThread(Long id){
    	ThreadData threaddata = select().leftOuterJoin(threadData().trackData()).where( eq(threadData().id(), id) ).getSingleResult();
    	// 平時のスレッド
    	if(threaddata == null || threaddata.trackData == null) return true;
    	// 災害中スレッド
    	if(threaddata.trackData.endtime == null) return true;
    	return false;
    }

    /**
	 * 日付範囲で検索 (論理削除されていないスレッド)
	 * @param starttime
	 * @param endtime
	 * @return 該当するスレッドデータリスト
	 */
    public List<ThreadData> rangeSearchThreadData(Timestamp starttime, Timestamp endtime) {
    	// 作成時間の範囲検索
    	return select().where(
    			and(
    				ge(threadData().registtime(), starttime),
    				le(threadData().registtime(), endtime),
    				ne(threadData().deleted(), true)
    			) ).getResultList();
    }


	@Override
	public DeleteCascadeResult deleteCascade(ThreadData entity, DeleteCascadeResult result) {

		result.cascade(ThreadresponseData.class, Names.threadresponseData().threaddataid(), entity.id);
		result.cascade(ThreadsendtoData.class, Names.threadsendtoData().threaddataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
