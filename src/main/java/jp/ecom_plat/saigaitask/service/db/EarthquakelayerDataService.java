/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ge;
import static org.seasar.extension.jdbc.operation.Operations.isNull;
import static org.seasar.extension.jdbc.operation.Operations.lt;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerDataNames;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class EarthquakelayerDataService extends AbstractService<EarthquakelayerData> {

	public EarthquakelayerData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * eventidで検索
	 * @param eventid
	 * @return earthquakelayerData
	 */
	public List<EarthquakelayerData> findByEventId(String eventid) {
		return select().where( eq(EarthquakelayerDataNames.eventid(), eventid) ).getResultList();
	}

	/**
	 * layeridで検索
	 * @param layerid
	 * @return boolean true: earthquakelayer_dataで所有する, false : 所有していない
	 */
	public List<EarthquakelayerData> findByLayerId(String layerid) {
		return select().where( eq(EarthquakelayerDataNames.layerid(), layerid) ).getResultList();
	}

	/**
	 * eventidとEarthQuakeGroupLayerDataIdで検索
	 * @param eventid
	 * @param earthquakegrouplayerid
	 * @return earthquakelayerData
	 */
	@Deprecated
	public List<EarthquakelayerData> findByEventIdGroupId(String eventid, Long earthQuakeGroupLayerId) {
		return select().where( and( eq(EarthquakelayerDataNames.eventid(), eventid), eq(EarthquakelayerDataNames.earthquakegrouplayerid(), earthQuakeGroupLayerId) ) ).getResultList();
	}

	/**
	 * eventidとEarthquakelayerInfoIdで検索
	 * @param eventid
	 * @param earthquakelayerinfoid
	 * @return earthquakelayerData
	 */
	public List<EarthquakelayerData> findByEventIdAndInfoId(String eventid, Long earthQuakeLayerInfoId) {
		return select().where( and( eq(EarthquakelayerDataNames.eventid(), eventid), eq(EarthquakelayerDataNames.earthquakelayerinfoid(), earthQuakeLayerInfoId) ) ).getResultList();
	}

	/**
	 * reportdatetimeとEarthQuakeGroupLayerDataIdで検索
	 * 現在時刻から30分前のレコードのみを取得する
	 * @param earthquakegrouplayerid
	 * @return earthquakelayerData
	 */
	public List<EarthquakelayerData> findByDisasterShareLayer(Long earthQuakeGroupLayerId, int reporttimeDiff) {
		Calendar minutesBefore30 = Calendar.getInstance();
		minutesBefore30.add(Calendar.MINUTE, reporttimeDiff);
		java.util.Date utilDate = minutesBefore30.getTime();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return select().where( and( ge(EarthquakelayerDataNames.reportdatetime(), new Timestamp(sqlDate.getTime())), eq(EarthquakelayerDataNames.earthquakegrouplayerid(), earthQuakeGroupLayerId) ) ).getResultList();
	}

	/**
	 * EarthQuakeGroupLayerDataId と reportdatetimeの開始・終了時刻で検索
	 * @param earthquakegrouplayerid
	 * @return earthquakelayerData
	 */
	public List<EarthquakelayerData> findByEarthquakegrouplayeridAndTimerange(Long earthquakegrouplayerid, Timestamp startreportdatetime, Timestamp endreportdatetime) {

		// select
		AutoSelect<EarthquakelayerData> select = select();

		// join
		select.leftOuterJoin(EarthquakelayerDataNames.earthquakegrouplayerData());

		// where
		SimpleWhere where = new SimpleWhere();
		where.ge(EarthquakelayerDataNames.origintime(), startreportdatetime);
		where.le(EarthquakelayerDataNames.origintime(), endreportdatetime);
		where.eq(EarthquakelayerDataNames.earthquakegrouplayerid(), earthquakegrouplayerid);

		return select.where(where).orderBy(desc(EarthquakelayerDataNames.origintime())).getResultList();
	}

	/**
	 * EarthQuakeLayerInfoId と reportdatetimeの開始・終了時刻で検索
	 * @param earthquakelayerinfoid
	 * @return earthquakelayerData
	 */
	public List<EarthquakelayerData> findByEarthquakeLayerinfoidAndTimerange(Long earthquakelayerinfoid, Timestamp startreportdatetime, Timestamp endreportdatetime) {

		// select
		AutoSelect<EarthquakelayerData> select = select();

		// join
		select.leftOuterJoin(EarthquakelayerDataNames.earthquakelayerInfo());

		// where
		SimpleWhere where = new SimpleWhere();
		where.ge(EarthquakelayerDataNames.origintime(), startreportdatetime);
		where.le(EarthquakelayerDataNames.origintime(), endreportdatetime);
		where.eq(EarthquakelayerDataNames.earthquakelayerinfoid(), earthquakelayerinfoid);

		return select.where(where).orderBy(desc(EarthquakelayerDataNames.origintime())).getResultList();
	}

	/**
	 * 全てのマスターマップで所有する震源震度レイヤに対し、reporttimeDiffより古いレイヤを返す
	 * @param reporttimediff : 分単位での設定時間
	 * @return earthquakelayerData
	 */
	public List<EarthquakelayerData> earthQuakeLayerTimeDelete(int reporttimeDiff) {
		Calendar minutesBefore30 = Calendar.getInstance();
		minutesBefore30.add(Calendar.MINUTE, reporttimeDiff);
		java.util.Date utilDate = minutesBefore30.getTime();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return select()
				.leftOuterJoin(EarthquakelayerDataNames.earthquakegrouplayerData())
				.leftOuterJoin(EarthquakelayerDataNames.earthquakegrouplayerData().mapmasterInfo()).
				where(
					and(
						lt(EarthquakelayerDataNames.reportdatetime(), new Timestamp(sqlDate.getTime())),
						isNull(EarthquakelayerDataNames.earthquakegrouplayerData().trackmapinfoid()),
						ne(EarthquakelayerDataNames.earthquakegrouplayerData().mapmasterInfo().deleted(), true)
					)
				).getResultList();
	}

	/**
	 * EarthQuakeGroupLayerDataIdで検索
	 * @param earthquakegrouplayerid : 検索対象の震度グループ情報のID
	 * @return earthquakelayerData : 紐付く震度レイヤ一覧
	 */
	public List<EarthquakelayerData> findByEarthGroupId(Long earthQuakeGroupLayerId) {
		return select().where( eq(EarthquakelayerDataNames.earthquakegrouplayerid(), earthQuakeGroupLayerId) ).orderBy(asc(EarthquakelayerDataNames.origintime())).getResultList();
	}

	/**
	 * EarthQuakeGroupLayerDataIdで検索(order by句付き)
	 * @param earthQuakeGroupLayerId : 検索対象の震度グループ情報のID
	 * @param asc : ASC = true, DESC = false (対象はorigin_time)
	 * @return earthquakelayerData : 紐付く震度レイヤ一覧(ソート済)
	 */
	public List<EarthquakelayerData> findByEarthGroupId(Long earthQuakeGroupLayerId, boolean asc) {
		if(asc){
			return select().where( eq(EarthquakelayerDataNames.earthquakegrouplayerid(), earthQuakeGroupLayerId) ).orderBy(asc(EarthquakelayerDataNames.origintime())).getResultList();
		}else{
			return select().where( eq(EarthquakelayerDataNames.earthquakegrouplayerid(), earthQuakeGroupLayerId) ).orderBy(desc(EarthquakelayerDataNames.origintime())).getResultList();
		}
	}

	/**
	 * EarthquakelayerDataにEarthquakelayerInfoをJoinした状態で返す
	 * @return : List<EarthquakeplayerData>
	 */
	public List<EarthquakelayerData> findOldQuakeLayers(Timestamp checkTime) {
		return select()
				.innerJoin(EarthquakelayerDataNames.earthquakelayerInfo())
				.innerJoin(EarthquakelayerDataNames.earthquakelayerInfo().tablemasterInfo())
				.innerJoin(EarthquakelayerDataNames.earthquakelayerInfo().tablemasterInfo().mapmasterInfo())
				.where(lt(EarthquakelayerDataNames.origintime(), checkTime))
				.orderBy( desc(EarthquakelayerDataNames.origintime()))
				.getResultList();
	}

}
