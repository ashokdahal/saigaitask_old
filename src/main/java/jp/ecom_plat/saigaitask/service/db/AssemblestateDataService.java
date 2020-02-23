/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.assemblestateData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class AssemblestateDataService extends AbstractService<AssemblestateData> {

	/**
	 * IDで検索
	 * @param id ID
	 * @return IDに対応唯一のレコード
	 */
	public AssemblestateData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 記録データIDで検索
	 * @param trackdataid 記録データID
	 * @return 職員参集状況のリスト
	 */
	public List<AssemblestateData> findByTrackDataId(Long tid) {
		return select().where(
			and(
				eq(assemblestateData().trackdataid(), tid)
			)).orderBy(desc(assemblestateData().registtime())
		).getResultList();
	}

	/**
	 * 記録IDとユーザIDで検索
	 * @param trackdataid 記録データID
	 * @return 職員参集状況のレコード
	 */
	public AssemblestateData findByTrackdataidAndUserid(Long trackdataid, Long userid) {
		return select().where(
			and(
				eq(assemblestateData().trackdataid(), trackdataid),
				eq(assemblestateData().userid(), userid)
			)
		).getSingleResult();
	}

	public List<AssemblestateData> check() {
		List<AssemblestateData> reslist = select().leftOuterJoin(assemblestateData().trackData())
				.leftOuterJoin(assemblestateData().userInfo())
				.leftOuterJoin(assemblestateData().safetystateMaster()).getResultList();
		List<AssemblestateData> nolist = new ArrayList<AssemblestateData>();
		for (AssemblestateData data : reslist) {
			if (data.trackData == null || data.userInfo == null || data.safetystateMaster == null)
				nolist.add(data);
		}
		return nolist;
	}
}
