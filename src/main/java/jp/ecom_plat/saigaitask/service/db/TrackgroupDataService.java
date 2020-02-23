/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.extension.jdbc.operation.Operations;

import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.names.TrackgroupDataNames;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * 災害グループの取得・更新・削除を提供するサービスクラス
 */
@org.springframework.stereotype.Repository
public class TrackgroupDataService extends AbstractService<TrackgroupData> {

	public TrackgroupData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	public List<TrackgroupData> findByPreftrackdataid(Long preftrackdataid) {
		return select()
				.leftOuterJoin(TrackgroupDataNames.cityTrackData())
				.leftOuterJoin(TrackgroupDataNames.cityTrackData().localgovInfo())
				.where(Operations.eq(TrackgroupDataNames.preftrackdataid(), preftrackdataid))
				.getResultList();
	}

	public List<TrackgroupData> findByPreflocalgovinfoAndCitytrackdataid(Long preflocalgovinfoid, long cityTrackdataid) {
		return select()
				.leftOuterJoin(TrackgroupDataNames.prefTrackData())
				.where(
						Operations.eq(TrackgroupDataNames.citytrackdataid(), cityTrackdataid)
						,Operations.eq(TrackgroupDataNames.prefTrackData().localgovinfoid(), preflocalgovinfoid)
				)
				.getResultList();
	}

	/**
	 * 都道府県の記録データIDを指定して、グループを削除
	 */
	public void deleteByPreftrackdataid(Long preftrackdataid) {
		// 都道府県の記録データIDを指定して削除
		List<TrackgroupData> trackgroupDatas = findByPreftrackdataid(preftrackdataid);
		for(TrackgroupData trackgroupData : trackgroupDatas) {
			delete(trackgroupData);
		}
	}

	public void update(Long preftrackdataid, List<Long> citytrackdataids) {
		if(citytrackdataids==null) {
			citytrackdataids = new ArrayList<Long>();
		}

		// 既存のデータがあれば、すでに存在するかチェックする
		List<TrackgroupData> trackgroupDatas = findByPreftrackdataid(preftrackdataid);
		if(0<trackgroupDatas.size()) {
			// citytrackgroupinfoid のマップを作成
			Map<Long, TrackgroupData> map = new HashMap<Long, TrackgroupData>(trackgroupDatas.size());
			for(TrackgroupData trackgroupData: trackgroupDatas) {
				map.put(trackgroupData.citytrackdataid, trackgroupData);
			}

			// すでに登録ずみかチェックする
			List<Long> notExistCitytrackdataids = new ArrayList<Long>();
			while(0<citytrackdataids.size()) {
				Long citytrackdataid = citytrackdataids.get(0); // pop
				// なければ、登録予定
				if(map.get(citytrackdataid)==null) {
					notExistCitytrackdataids.add(citytrackdataid);
				}
				// あれば、除外
				else map.remove(citytrackdataid);
				// 念のため重複削除
				while(citytrackdataids.remove(citytrackdataid)) ;
			}
			citytrackdataids.addAll(notExistCitytrackdataids);

			// 残っているものは削除
			for(Map.Entry<Long, TrackgroupData> entry : map.entrySet()) {
				delete(entry.getValue());
			}
		}

		// 存在しないものだけ登録
		for(Long citytrackdataid : citytrackdataids) {
			TrackgroupData trackgroupData = new TrackgroupData();
			trackgroupData.preftrackdataid = preftrackdataid;
			trackgroupData.citytrackdataid = citytrackdataid;
			insert(trackgroupData);
		}
	}

}
