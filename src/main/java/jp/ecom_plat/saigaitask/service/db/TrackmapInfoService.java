/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.trackmapInfo;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.EarthquakegrouplayerData;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.SpringContext;

@org.springframework.stereotype.Repository
public class TrackmapInfoService extends AbstractService<TrackmapInfo> {

	/**
	 * IDで検索
	 *
	 * @param id
	 * @return 地図情報
	 */
	public TrackmapInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * IDで検索
	 * 削除されていない記録データを結合.
	 * @param id
	 * @return 記録地図情報
	 */
	public TrackmapInfo findByIdLeftJoinNotDeletedTrackDatas(Long id) {
		return select().id(id)
				.leftOuterJoin(trackmapInfo().trackDatas())
				.where(and(
					eq(trackmapInfo().id(), id),
					ne(trackmapInfo().trackDatas().deleted(), true)
				))
				.getSingleResult();
	}

	/**
	 * IDで検索
	 * 削除された記録データも含めて結合.
	 * @param id
	 * @return 記録地図情報
	 */
	public TrackmapInfo findByIdLeftJoinTrackDatas(Long id) {
		return select().id(id)
				.leftOuterJoin(trackmapInfo().trackDatas())
				.where(and(
					eq(trackmapInfo().id(), id)
				))
				.getSingleResult();
	}

	/**
	 * 記録IDから検索
	 *
	 * @param tid 記録ID
	 * @return 地図情報
	 */
	public TrackmapInfo findByTrackDataId(Long tid) {
		return select()
				.leftOuterJoin(trackmapInfo().trackDatas())
				.where(eq(trackmapInfo().trackDatas().id(), tid)).limit(1)
				.getSingleResult();
	}

	/**
	 * 自治体ID から検索
	 * 記録データを結合する.
	 * @param localgovinfoid 自治体ID
	 * @return 地図情報
	 */
	public List<TrackmapInfo> findByLocalgovInfoIdAndTrackDataNotDelete(Long localgovinfoid) {
		return select()
				.innerJoin(trackmapInfo().trackDatas())
				.where(
						eq(trackmapInfo().trackDatas().localgovinfoid(), localgovinfoid),
						ne(trackmapInfo().trackDatas().deleted(), true)
				).getResultList();
	}

	/**
	 * 自治体ID から検索。
	 * 削除済み災害を含む記録データを結合する.
	 * @param localgovinfoid 自治体ID
	 * @return 地図情報
	 */
	public List<TrackmapInfo> findByLocalgovInfoIdByTrackData(Long localgovinfoid) {
		return select()
				.innerJoin(trackmapInfo().trackDatas())
				.where(
						eq(trackmapInfo().trackDatas().localgovinfoid(), localgovinfoid)
				).getResultList();
	}

	/**
	 * 紐付くtrack_dataを走査して、endtimeを全て持っているか確認する
	 * @param id trackmapinfoのid
	 * @return : true -> 全て完了済, false -> 1つ以上の災害が発生中
	 */
	public boolean isTrackdataEnd(Long id) {
		boolean check = true;
		TrackmapInfo trackmapinfo = select().innerJoin(trackmapInfo().trackDatas()).id(id).getSingleResult();
		for(TrackData trackdata : trackmapinfo.trackDatas){
			if(trackdata.endtime == null){
				check = false;
				break;
			}
		}
		return check;
	}

	/**
	 * 地図IDで検索
	 *
	 * @param mapid
	 * @return 地図情報のリスト
	 */
	public List<TrackmapInfo> findByMapId(Long mapid) {
		return select()
				.where(
						eq(trackmapInfo().mapid(), mapid)
				).getResultList();
	}


//	public List<TrackmapInfo> check() {
//		List<TrackmapInfo> reslist = select().innerJoin(trackmapInfo().trackData()).getResultList();
//		List<TrackmapInfo> nolist = new ArrayList<TrackmapInfo>();
//		for (TrackmapInfo map : reslist) {
//			if (map.trackData != null)
//				nolist.add(map);
//		}
//		//TODO:communityid
//		//TODO:mapgroupid
//		//TODO:mapid
//		return reslist;
//	}

	@Override
	public DeleteCascadeResult deleteCascade(TrackmapInfo entity, DeleteCascadeResult result) {

		result.cascade(EarthquakegrouplayerData.class, Names.earthquakegrouplayerData().trackmapinfoid(), entity.id);
		result.cascade(HistorytableInfo.class, Names.historytableInfo().trackmapinfoid(), entity.id);
		//result.cascade(TrackData.class, Names.trackData().trackmapinfoid(), entity.id);
		result.cascade(TracktableInfo.class, Names.tracktableInfo().trackmapinfoid(), entity.id);

		// eコミマップの削除
		result.cascadeEcommapMapInfo(entity.mapid, entity, String.valueOf(entity.id));

		// 複数同時災害の場合に TrackData.trackmapinfoid が複数設定されるため、一括削除する
		entity = findByIdLeftJoinTrackDatas(entity.id);
		if(0<entity.trackDatas.size()) {
			TrackDataService trackDataService = SpringContext.getApplicationContext().getBean(TrackDataService.class);
			for(TrackData trackData : entity.trackDatas) {
				trackData.trackmapinfoid = null;
				trackDataService.update(trackData);
			}
		}
		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
