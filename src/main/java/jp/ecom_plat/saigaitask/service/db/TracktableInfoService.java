/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.tracktableInfo;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.ImporttracktableInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class TracktableInfoService extends AbstractService<TracktableInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return 記録テーブル情報
	 */
	public TracktableInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * テーブルIDと記録IDから検索
	 * @param tmid	tablemaster_infoテーブルのID
	 * @param tid	trackdataid
	 * @return 記録テーブル情報
	 */
	public TracktableInfo findByTablemasterInfoIdAndTrackDataId(Long tmid, Long tid) {
		return jdbcManager.from(TracktableInfo.class)
				.innerJoin(tracktableInfo().trackmapInfo())
				.innerJoin(tracktableInfo().trackmapInfo().trackDatas())
				.where(
					and(
						eq(tracktableInfo().trackmapInfo().trackDatas().id(), tid),
						eq(tracktableInfo().tablemasterinfoid(), tmid)))
				.limit(1).getSingleResult();
	}

	/**
	 * テーブルIDと記録IDから検索（eコミマップ無しリスト）
	 * @param tmid	tablemaster_infoテーブルのID
	 * @return 記録テーブル情報
	 */
	public TracktableInfo findByTablemasterInfoIdAndTrackDataIdNoMap(Long tmid) {
		return jdbcManager.from(TracktableInfo.class)
				.where(
					eq(tracktableInfo().tablemasterinfoid(), tmid))
				.limit(1).getSingleResult();
	}

	/**
	 * 記録IDとテーブル名から検索
	 * @param tid
	 * @param table
	 * @return
	 */
	public TracktableInfo findByTrackDataIdAndTablename(Long tid, String table) {
		return jdbcManager.from(TracktableInfo.class)
				.innerJoin(tracktableInfo().trackmapInfo())
				.innerJoin(tracktableInfo().trackmapInfo().trackDatas())
				.where(
					and(
						eq(tracktableInfo().trackmapInfo().trackDatas().id(), tid),
						eq(tracktableInfo().tablename(), table)))
				.limit(1).getSingleResult();
	}

	/**
	 * 記録IDから検索
	 * @param tid
	 * @param table
	 * @return
	 */
	public List<TracktableInfo> findByTrackDataId(Long tid) {
		return jdbcManager.from(TracktableInfo.class)
				.innerJoin(tracktableInfo().trackmapInfo())
				.innerJoin(tracktableInfo().trackmapInfo().trackDatas())
				.where(
					and(
						eq(tracktableInfo().trackmapInfo().trackDatas().id(), tid)))
				.getResultList();
	}

	/**
	 * テーブルIDと地図IDで検索
	 * @param tid テーブルID
	 * @return 記録テーブル情報
	 */
	/*public TracktableInfo findByTrackMapInfoIdAndTableId(Long tid, Integer tableid) {
		return select().where(
				and(
					eq(tracktableInfo().tableid(), tableid),
					eq(tracktableInfo().trackmapinfoid(), tid)
			)).limit(1).getSingleResult();
	}*/

	/**
	 * 地図情報IDから検索
	 * @param trackmapinfoid
	 * @return
	 */
	public List<TracktableInfo> findByTrackmapInfoId(Long trackmapinfoid) {
		return select()
				.innerJoin(tracktableInfo().tablemasterInfo())
				.where(eq(tracktableInfo().trackmapinfoid(), trackmapinfoid))
				.getResultList();
	}

	/**
	 * 地図情報IDから検索
	 * @param trackmapinfoid
	 * @return
	 */
	public TracktableInfo findByTrackmapInfoIdAndTablemasterinfoid(Long trackmapinfoid, Long tablemasterinfoid) {
		return select()
				.innerJoin(tracktableInfo().tablemasterInfo())
				.where(
					eq(tracktableInfo().trackmapinfoid(), trackmapinfoid)
					,eq(tracktableInfo().tablemasterinfoid(), tablemasterinfoid))
				.limit(1)
				.getSingleResult();
	}

	/**
	 * 地図情報IDから検索
	 * tablemaster_infoのresetフラグがtrueのものだけ抽出
	 * @param trackmapinfoid
	 * @return
	 */
	public List<TracktableInfo> findByTrackmapInfoAndResetLayerId(Long trackmapinfoid) {
		return select()
				.innerJoin(tracktableInfo().tablemasterInfo())
				.where(
					eq(tracktableInfo().trackmapinfoid(), trackmapinfoid),
					eq(tracktableInfo().tablemasterInfo().reset(), true),
					eq(tracktableInfo().tablemasterInfo().deleted(), false)
				)
				.getResultList();
	}

	/**
	 * レイヤIDから未削除のものを検索
	 * @param layerId レイヤID
	 * @return 未削除のリスト
	 */
	public List<TracktableInfo> findByLayerId(String layerId) {
		return select()
				.innerJoin(tracktableInfo().tablemasterInfo())
				.where(
						eq(tracktableInfo().layerid(), layerId)
						,ne(tracktableInfo().tablemasterInfo().deleted(), true)
				).getResultList();
	}

	/**
	 * @param trackmapinfoid 記録地図情報ID
	 * @return コピーした記録テーブル情報リストを取得
	 */
	public List<TracktableInfo> findCopyTracktableInfos(Long trackmapinfoid) {
		String sql = "/*findCopyTracktableInfos*/select * from tracktable_info where trackmapinfoid=? and char_length(layerid)!=0 and layerid<>(select layerid from tablemaster_info where id=tablemasterinfoid);";
		return jdbcManager.selectBySql(this.entityClass, sql, trackmapinfoid).getResultList();
	}

	public List<TracktableInfo> check() {
		List<TracktableInfo> reslist = select().innerJoin(tracktableInfo().trackmapInfo())
				.innerJoin(tracktableInfo().tablemasterInfo()).getResultList();
		List<TracktableInfo> nolist = new ArrayList<TracktableInfo>();
		for (TracktableInfo tbl : reslist) {
			if (tbl.trackmapInfo == null || tbl.tablemasterInfo == null)
				nolist.add(tbl);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(TracktableInfo entity, DeleteCascadeResult result) {

		result.cascade(ImporttracktableInfo.class, Names.importtracktableInfo().tracktableinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
