/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.meteotriggerData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class MeteotriggerDataService extends AbstractService<MeteotriggerData> {

	/**
	 * IDで検索
	 * @param id
	 * @return 気象情報トリガーデータ
	 */
	public MeteotriggerData findById(Long id) {
		return select().id(id).getSingleResult();
	}
	
	/**
	 * 記録IDで検索
	 * @param trackdataid 記録ID
	 * @return 気象情報トリガーデータのリスト
	 */
	public List<MeteotriggerData> findByTrackdataIdOrderByTriggertime(Long trackdataid) {
		return select().where(
				and(
					eq(meteotriggerData().trackdataid(), trackdataid))
				).orderBy(
					asc(meteotriggerData().triggertime())
				).getResultList();
	}
	
	public List<MeteotriggerData> check() {
		List<MeteotriggerData> reslist = select().leftOuterJoin(meteotriggerData().localgovInfo())
				.leftOuterJoin(meteotriggerData().trackData())
				.leftOuterJoin(meteotriggerData().meteotriggerInfo()).getResultList();
		List<MeteotriggerData> nolist = new ArrayList<MeteotriggerData>();
		for (MeteotriggerData data : reslist) {
			if (data.localgovInfo == null || data.trackData == null || data.meteotriggerInfo == null)
				nolist.add(data);
		}
		reslist = select().leftOuterJoin(meteotriggerData().noticegroupInfo()).where(and(
				ne(meteotriggerData().noticegroupinfoid(), 0l),
				isNotNull(meteotriggerData().noticegroupinfoid()))).getResultList();
		for (MeteotriggerData data : reslist) {
			if (meteotriggerData().noticegroupInfo() == null)
				nolist.add(data);
		}
		reslist = select().leftOuterJoin(meteotriggerData().stationclassInfo()).where(and(
				ne(meteotriggerData().stationclassinfoid(), 0l),
				isNotNull(meteotriggerData().stationclassinfoid()))).getResultList();
		for (MeteotriggerData data : reslist) {
			if (meteotriggerData().stationclassInfo() == null)
				nolist.add(data);
		}
		return nolist;
	}
}
