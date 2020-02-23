/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static org.seasar.extension.jdbc.operation.Operations.eq;

import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerInfo;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class EarthquakelayerInfoService extends AbstractService<EarthquakelayerInfo> {

	public EarthquakelayerInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 自治体でもつ震度レイヤ設定を取得
	 * TablemasterInfoをJoinして返す
	 * @param localgovinfoid
	 * @return
	 */
	public EarthquakelayerInfo findByLocalgovinfoId(Long localgovinfoid){
		return select()
				.leftOuterJoin(EarthquakelayerInfoNames.tablemasterInfo())
				.where( eq(EarthquakelayerInfoNames.localgovinfoid(), localgovinfoid) ).getSingleResult();
	}
}
