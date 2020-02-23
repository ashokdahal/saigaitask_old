/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.isNull;
import static org.seasar.extension.jdbc.operation.Operations.lt;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.EarthquakegrouplayerData;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.names.EarthquakegrouplayerDataNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class EarthquakegrouplayerDataService extends AbstractService<EarthquakegrouplayerData> {

	public EarthquakegrouplayerData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 第二引数が true : 災害マップ用のグループのみ取得、false : マスターマップのグループのみ取得
	 * 全て取りたい場合は、findByMapMasterInfoIdを利用する事。
	 * @param mapmasterinfoid : マップID
	 * @param isTrack : 災害マップのグループ取得かどうか
	 * @return EarthquakegrouplayerData
	 */

	public List<EarthquakegrouplayerData> findByMapMasterInfoId(Long mapmasterinfoid, boolean isTrack){
		if(isTrack) {
			return select()
					.leftOuterJoin(EarthquakegrouplayerDataNames.trackmapInfo())
					.leftOuterJoin(EarthquakegrouplayerDataNames.mapmasterInfo())
					.where(
							eq(EarthquakegrouplayerDataNames.mapmasterinfoid(), mapmasterinfoid),
							ne(EarthquakegrouplayerDataNames.mapmasterInfo().deleted(), true),
							isNotNull(EarthquakegrouplayerDataNames.trackmapinfoid())
					)
					.getResultList();
		}
		else {
			return select()
					.leftOuterJoin(EarthquakegrouplayerDataNames.mapmasterInfo())
					.where(and(
							eq(EarthquakegrouplayerDataNames.mapmasterinfoid(), mapmasterinfoid),
							ne(EarthquakegrouplayerDataNames.mapmasterInfo().deleted(), true),
							isNull(EarthquakegrouplayerDataNames.trackmapinfoid())
					))
					.getResultList();
		}
	}

	/**
	 * 平時のグループ情報は、自治体IDから検索
	 * 災害時のグループ情報は、記録データIDから検索
	 * @param localgovinfoid 自治体ID
	 * @param trackdataid 記録データID
	 * @return
	 */
	public List<EarthquakegrouplayerData> findByLocalgovinfoidAndTrackdataid(Long localgovinfoid, long trackdataid) {

		// select
		AutoSelect<EarthquakegrouplayerData> select = select();

		// 災害用
		if(0<trackdataid) {
			// join
			select.leftOuterJoin(EarthquakegrouplayerDataNames.trackmapInfo());
			select.leftOuterJoin(EarthquakegrouplayerDataNames.trackmapInfo().trackDatas());

			// where
			SimpleWhere where = new SimpleWhere();
			//where.isNotNull(EarthquakegrouplayerDataNames.trackmapinfoid(), true);
			where.eq(EarthquakegrouplayerDataNames.trackmapInfo().trackDatas().id(), trackdataid);
			where.eq(EarthquakegrouplayerDataNames.trackmapInfo().trackDatas().localgovinfoid(), localgovinfoid);
			select.where(where);
		}
		// 平時用
		else {
			// join
			select.leftOuterJoin(EarthquakegrouplayerDataNames.mapmasterInfo());

			// where
			SimpleWhere where = new SimpleWhere();
			//where.isNotNull(EarthquakegrouplayerDataNames.mapmasterInfo(), true);
			where.eq(EarthquakegrouplayerDataNames.mapmasterInfo().localgovinfoid(), localgovinfoid);
			where.ne(EarthquakegrouplayerDataNames.mapmasterInfo().deleted(), true);
			select.where(where);
		}

		return select.getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(EarthquakegrouplayerData entity, DeleteCascadeResult result) {

		result.cascade(EarthquakelayerData.class, Names.earthquakelayerData().earthquakegrouplayerid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}

	/**
	 * EarthquakegrouplayerDataにEarthquakelayerDataをJoinした状態で返す
	 * @return : List<EarthquakegrouplayerData>
	 */
	public List<EarthquakegrouplayerData> findOldQuakeLayers(Timestamp checkTime) {
		return select()
				.innerJoin(EarthquakegrouplayerDataNames.mapmasterInfo())
				.leftOuterJoin(EarthquakegrouplayerDataNames.trackmapInfo())
				.innerJoin(EarthquakegrouplayerDataNames.earthquakelayerDatas())
				.where(lt(EarthquakegrouplayerDataNames.earthquakelayerDatas().origintime(), checkTime))
				.orderBy( desc(EarthquakegrouplayerDataNames.earthquakelayerDatas().origintime()))
				.getResultList();
	}
}
