/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.meteoData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.db.MeteoData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class MeteoDataService extends AbstractService<MeteoData> {

	public MeteoData findById(Long id) {
		return select().id(id).getSingleResult();
	}


	/**
	 * 最新のレコードを返す
	 * @return
	 */
	public MeteoData findLatest(){
		return select().orderBy(desc(meteoData().id())).limit(1).getSingleResult();
	}

	/**
	 * 指定したmeteorequestinfoidを持った最新のレコードを返す
	 * @return
	 */
	public MeteoData findLatest(long meteorequestinfoid){
		return select().where(
				eq(meteoData().meteorequestinfoid(), meteorequestinfoid)
			).orderBy(desc(meteoData().id())).limit(1).getSingleResult();
	}
	
	public List<MeteoData> check() {
		List<MeteoData> reslist = select().leftOuterJoin(meteoData().meteorequestInfo()).getResultList();
		List<MeteoData> nolist = new ArrayList<MeteoData>();
		for (MeteoData data : reslist) {
			if (data.meteorequestInfo == null)
				nolist.add(data);
		}
		return nolist;
	}

	/**
	 * 自治体IDと気象警報IDで検索
	 * @param meteoid
	 * @param govid
	 * @return
	 */
	public List<MeteoData> findByMeteoIdAndLocalgovInfoId(long meteoid, long govid) {
		return select().innerJoin(meteoData().meteorequestInfo()).where(
				and(
					eq(meteoData().meteoid(), meteoid),
					eq(meteoData().meteorequestInfo().localgovinfoid(), govid)
				)).getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(MeteoData entity, DeleteCascadeResult result) {

		result.cascade(EarthquakelayerData.class, Names.earthquakelayerData().meteodataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
