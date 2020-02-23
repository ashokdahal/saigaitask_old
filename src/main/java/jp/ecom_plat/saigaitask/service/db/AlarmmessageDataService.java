/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.alarmmessageData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.gt;
import static org.seasar.extension.jdbc.operation.Operations.ne;
import static org.seasar.extension.jdbc.operation.Operations.or;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.AlarmshowData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class AlarmmessageDataService extends AbstractService<AlarmmessageData> {

	public static Map<String, String> messageTypes = new HashMap<String, String>(); static {
		messageTypes.put("red", "error");
		messageTypes.put("yellow", "warning");
		messageTypes.put("green", "success");
		messageTypes.put("blue", "information");
		messageTypes.put("whilte", "alert");
	}
	
	public AlarmmessageData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 表示可能なアラーム表示データを検索
	 *
	 * @param tid 記録データID
	 * @param gid 班ID
	 * @return アラーム表示データ
	 */
	public List<AlarmmessageData> findByRecentShowData(Long govid, Long tid, Long gid) {
		long hour = 7200000;//2h
		Timestamp beforeh = new Timestamp(System.currentTimeMillis()-hour);
		return select().where(
			or(
				and(
					eq(alarmmessageData().trackdataid(), tid),
					or(
						eq(alarmmessageData().groupid(), gid),
						eq(alarmmessageData().groupid(), 0L)
						),
					eq(alarmmessageData().showmessage(), true),
					gt(alarmmessageData().registtime(), beforeh)//>
				),
				and(
					eq(alarmmessageData().localgovinfoid(), govid),
					eq(alarmmessageData().trackdataid(), -1L),
					gt(alarmmessageData().registtime(), beforeh)//>
				)
			)).orderBy(asc(alarmmessageData().id())).getResultList();
	}

	/**
	 * 表示可能なアラーム表示データを検索
	 *
	 * @param tid 記録データID
	 * @param uid 課ID
	 * @return アラーム表示データ
	 */
	public List<AlarmmessageData> findByRecentShowData(Long govid, Long uid) {
		long hour = 7200000;//2h
		Timestamp beforeh = new Timestamp(System.currentTimeMillis()-hour);
		return select().where(
			or(
				and(
					or(
						eq(alarmmessageData().unitid(), uid),
						eq(alarmmessageData().groupid(), 0L)
						),
					eq(alarmmessageData().showmessage(), true),
					gt(alarmmessageData().registtime(), beforeh)//>
				),
				and(
					eq(alarmmessageData().localgovinfoid(), govid),
					eq(alarmmessageData().trackdataid(), -1L),
					gt(alarmmessageData().registtime(), beforeh)//>
				)
			)).orderBy(asc(alarmmessageData().id())).getResultList();
	}

	/**
	 * 記録データIDから検索
	 *
	 * @param tid 記録データID
	 * @param gid 班ID
	 * @return アラームデータのリスト
	 * @deprecated
	 */
	public List<AlarmmessageData> findByTrackdataIdAndGroupId(Long tid, Long gid) {
		return select().where(
			and(
				eq(alarmmessageData().trackdataid(), tid),
				or(
					eq(alarmmessageData().groupid(), gid),
					eq(alarmmessageData().groupid(), 0L)
				)
			)
		).orderBy(desc(alarmmessageData().registtime())).getResultList();
	}

	/**
	 * 記録データIDもしくは、平時の2日前のデータから検索
	 *
	 * @param tid 記録データID
	 * @param gid 班ID
	 * @return アラームデータのリスト
	 */
	public List<AlarmmessageData> findByLocalgovInfoIdOrTrackdataIdAndGroupId(Long govid, Long tid, Long gid) {
		long day2 = 172800000;//2day
		Timestamp before2day = new Timestamp(System.currentTimeMillis()-day2);
		return select().where(
			or(
				and(
					eq(alarmmessageData().trackdataid(), tid),
					or(
						eq(alarmmessageData().groupid(), gid),
						eq(alarmmessageData().groupid(), 0L)
					)
				),
				and(
					eq(alarmmessageData().localgovinfoid(), govid),
					eq(alarmmessageData().trackdataid(), -1L),
					gt(alarmmessageData().registtime(), before2day)//>
				)
			)
		).orderBy(desc(alarmmessageData().registtime())).getResultList();
	}

	/**
	 * 自課宛または全班宛もしくは、平時の2日前のデータから検索
	 *
	 * @param tid 記録データID
	 * @param gid 班ID
	 * @return アラームデータのリスト
	 */
	public List<AlarmmessageData> findByLocalgovInfoIdAndUnitId(Long govid, Long uid) {
		long day2 = 172800000;//2day
		Timestamp before2day = new Timestamp(System.currentTimeMillis()-day2);
		return select().where(
			or(
				/* 下記の条件だと、全自治体の全災害のgroupid=0のアラームが取得されてしまうのでNG
				or(
					eq(alarmmessageData().unitid(), uid),
					eq(alarmmessageData().groupid(), 0L)
				),
				*/
				// 自課宛または全課宛のメッセージを取得
				and(
					eq(alarmmessageData().localgovinfoid(), govid),
					or(
						eq(alarmmessageData().unitid(), uid),
						eq(alarmmessageData().unitid(), 0L)
					)
				),
				// 災害の全班宛の2日前のデータから検索
				and(
					eq(alarmmessageData().localgovinfoid(), govid),
					eq(alarmmessageData().groupid(), 0L),
					gt(alarmmessageData().registtime(), before2day)//>
				),
				// 平時の2日前のデータから検索
				and(
					eq(alarmmessageData().localgovinfoid(), govid),
					eq(alarmmessageData().trackdataid(), -1L),
					gt(alarmmessageData().registtime(), before2day)//>
				)
			)
		).orderBy(desc(alarmmessageData().registtime())).getResultList();
	}
	
	public void insert(Long trackid, AlarmmessageInfo info) {
		AlarmmessageData data = new AlarmmessageData();
		data.localgovinfoid = info.localgovinfoid;
		data.alarmmessageinfoid = info.id;
		data.deleted = false;
		data.duration = info.duration;
		if (data.duration==null)
			data.duration = 0;
		data.message = info.message;
		data.messagetype = messageTypes.get(info.messagetype);
		data.registtime = new Timestamp(System.currentTimeMillis());
		data.groupid = info.groupid;
		data.sendgroupid = 0l;
		data.showmessage = info.showmessage;
		data.trackdataid = trackid;
		this.insert(data);
	}

    /**
     * アラームデータを登録
     * @param trackid 記録ID
     * @param groupid 表示班ID 0以上であれば、こちらを採用する
     * @param alarminfo アラーム情報
     * @param name アラーム本文中の名称
     * @param data アラーム本文中のデータ
     * @return アラームデータ
     */
    public AlarmmessageData insert(Long trackid, Long groupid, AlarmmessageInfo alarminfo, String name, String data) {
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	AlarmmessageData alarm = new AlarmmessageData();
    	alarm.trackdataid = trackid;
    	alarm.alarmmessageinfoid = alarminfo.id;
    	if (groupid > 0)
    		alarm.groupid = groupid;
    	else
    		alarm.groupid = alarminfo.groupid;
    	String msg = alarminfo.message;
    	if (name != null)
    		msg = msg.replace("<name>", name);
    	if (data != null)
    		msg = msg.replace("<data>", data);
		alarm.message = msg;
    	alarm.showmessage = alarminfo.showmessage;
    	alarm.messagetype = alarminfo.messagetype;
    	alarm.duration = alarminfo.duration;
    	alarm.registtime = now;
    	alarm.deleted = false;
    	
    	insert(alarm);
    	
    	return alarm;
    }
    
	public List<AlarmmessageData> check() {
		List<AlarmmessageData> reslist = select().leftOuterJoin(alarmmessageData().trackData())
				.leftOuterJoin(alarmmessageData().localgovInfo())
				.leftOuterJoin(alarmmessageData().alarmmessageInfo()).getResultList();
		List<AlarmmessageData> nolist = new ArrayList<AlarmmessageData>();
		for (AlarmmessageData msg : reslist) {
			if (msg.trackData == null || msg.alarmmessageInfo == null)
				nolist.add(msg);
		}
		reslist = select().innerJoin(alarmmessageData().sendgroupInfo()).where(ne(alarmmessageData().sendgroupid(), 0l)).getResultList();
		for (AlarmmessageData msg : reslist) {
			if (msg.sendgroupInfo == null)
				nolist.add(msg);
		}
		reslist = select().innerJoin(alarmmessageData().groupInfo()).where(ne(alarmmessageData().groupid(), 0l)).getResultList();
		for (AlarmmessageData msg : reslist) {
			if (msg.sendgroupInfo == null)
				nolist.add(msg);
		}
		
		return nolist;
	}
	
	//
//	/**
//	 * 検索条件に従い検索し、検索結果件数を取得する。
//	 * @param conditions 検索条件マップ
//	 * @return 検索結果件数
//	 */
//	public int getCount(Map<String, Object> conditions) {
//		return (int)select()
//			.where(conditions)
//			.getCount();
//	}
//
//	/**
//	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
//	 * @param conditions 検索条件マップ
//	 * @param sortName ソート項目名
//	 * @param sortOrder ソート順（昇順 or 降順）
//	 * @param limit 取得件数
//	 * @param offset 取得開始位置
//	 * @return 全データ
//	 */
//	public List<AlarmmailInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
//		if(StringUtils.isEmpty(sortName)) {sortName = "id";}
//		if(StringUtils.isEmpty(sortOrder)) {sortName = "ASC";}
//		if(limit == null) {limit = 0;}
//		if(offset == null) {offset = 0;}
//
//		return select()
//			.where(conditions)
//			.orderBy("DESC".equals(sortOrder.toUpperCase())?desc(sortName):asc(sortName))
//			.limit(limit)
//			.offset(offset)
//			.getResultList();
//	}
//
//
//	/**
//	 * 更新対象外の項目を指定して更新する。
//	 * @param entity 更新対象データ
//	 * @param excludes 更新対象外プロパティ配列
//	 * @return
//	*/
//	public int update(AlarmmailInfo entity, PropertyName<?>[] excludes) {
//		if(excludes != null){
//			return jdbcManager
//				.update(entity)
//				.excludes(excludes)
//				.execute();
//		}else{
//			return jdbcManager
//				.update(entity)
//				.execute();
//		}
//	}

	@Override
	public DeleteCascadeResult deleteCascade(AlarmmessageData entity, DeleteCascadeResult result) {

		result.cascade(AlarmshowData.class, Names.alarmshowData().alarmmessagedataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
