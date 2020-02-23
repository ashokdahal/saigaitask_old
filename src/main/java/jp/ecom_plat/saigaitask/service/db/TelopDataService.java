/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.telopData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.in;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.TelopData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class TelopDataService extends AbstractService<TelopData> {

	public TelopData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 表示するデータを取得する。
	 * @param govid 自治体ID
	 * @return
	 */
	public List<TelopData> findValidData(Long govid) {
		List<Long> ids = jdbcManager.selectBySql(Long.class, "select max(id) from telop_data where localgovinfoid = "+govid+" and viewlimit > now() group by teloptypeid").getResultList();

		List<TelopData> list = new ArrayList<TelopData>();
		if (ids.size() > 0) {
			list = select().where(
					in(telopData().id(), ids)
					).orderBy(desc(telopData().id())).getResultList();
		}
		return list;
	}

	public List<TelopData> check() {
		List<TelopData> reslist = select().leftOuterJoin(telopData().localgovInfo())
				.leftOuterJoin(telopData().teloptypeMaster()).getResultList();
		List<TelopData> nolist = new ArrayList<TelopData>();
		for (TelopData data : reslist) {
			if (data.localgovInfo == null || data.teloptypeMaster == null)
				nolist.add(data);
		}
		return nolist;
	}
}
