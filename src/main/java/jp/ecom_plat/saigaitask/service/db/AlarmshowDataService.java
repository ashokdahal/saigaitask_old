/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.alarmshowData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.AlarmshowData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class AlarmshowDataService extends AbstractService<AlarmshowData> {

	public AlarmshowData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * アラームメッセージデータIDとセッションIDでアラーム表示状態を検索
	 * @param alarmid アラームメッセージデータID
	 * @param sessionid セッションID
	 * @return アラーム表示済みデータ
	 */
	public List<AlarmshowData> findByAlarmmessageDataIdAndSesssionId(Long alarmid, String sessionid) {
		return select().where(
			and(
				eq(alarmshowData().alarmmessagedataid(), alarmid),
				eq(alarmshowData().sessionid(), sessionid)
			)).getResultList();
	}

	public List<AlarmshowData> check() {
		List<AlarmshowData> reslist = select().innerJoin(alarmshowData().alarmmessageData()).getResultList();
		List<AlarmshowData> nolist = new ArrayList<AlarmshowData>();
		for (AlarmshowData data : reslist) {
			if (data.alarmmessageData == null)
				nolist.add(data);
		}
		return nolist;
	}
}
