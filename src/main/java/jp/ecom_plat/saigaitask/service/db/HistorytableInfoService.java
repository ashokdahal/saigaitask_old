/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.historytableInfo;
import static jp.ecom_plat.saigaitask.entity.names.HistorytableInfoNames.id;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.HistorycolumnlistInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * {@link HistorytableInfo}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/10/07 18:06:12")
@org.springframework.stereotype.Repository
public class HistorytableInfoService extends AbstractService<HistorytableInfo> {

    /**
     * 識別子でエンティティを検索します。
     *
     * @param id
     *            識別子
     * @return エンティティ
     */
    public HistorytableInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     *
     * @return エンティティのリスト
     */
    public List<HistorytableInfo> findAllOrderById() {
        return select().orderBy(asc(id())).getResultList();
    }

	/**
	 * テーブルIDと記録IDから検索
	 * @param tmid	tablemaster_infoテーブルのID
	 * @param tid	trackdataid
	 * @return 履歴テーブル情報
	 */
	public HistorytableInfo findByTablemasterInfoIdAndTrackDataId(Long tmid, Long tid) {
		return jdbcManager.from(HistorytableInfo.class)
				.innerJoin(historytableInfo().trackmapInfo(), false)
				.innerJoin(historytableInfo().trackmapInfo().trackDatas(), false)
				.where(
					and(
						eq(historytableInfo().trackmapInfo().trackDatas().id(), tid),
						eq(historytableInfo().tablemasterinfoid(), tmid)))
				.limit(1).getSingleResult();
	}

	/**
	 * 記録テーブルで検索
	 * @param ttbl	記録テーブル
	 * @return 履歴テーブル情報
	 */
	public HistorytableInfo findByTracktableInfo(TracktableInfo ttbl) {
		return jdbcManager.from(HistorytableInfo.class)
				.where(
					and(
						eq(historytableInfo().trackmapinfoid(), ttbl.trackmapinfoid),
						eq(historytableInfo().tablemasterinfoid(), ttbl.tablemasterinfoid)))
				.limit(1).getSingleResult();
	}


	/**
	 * 記録地図IDから検索
	 * @param trackmapInfoId	trackmapInfoId
	 * @return 履歴テーブル情報
	 */
	public List<HistorytableInfo> findByTrackmapInfoId(Long trackmapInfoId) {
		return jdbcManager.from(HistorytableInfo.class)
				.where(
					and(
						eq(historytableInfo().trackmapinfoid(), trackmapInfoId)))
				.getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(HistorytableInfo entity, DeleteCascadeResult result) {

		result.cascade(HistorycolumnlistInfo.class, Names.historycolumnlistInfo().historytableinfoid(), entity.id);

		// 履歴テーブルの削除
		HistoryTableService historyTableService = SpringContext.getApplicationContext().getBean(HistoryTableService.class);
		if("gid".equals(entity.idColumn)) { // レイヤ履歴の場合のみ削除。システムテーブルだったら削除しない
			if(historyTableService.dropTable(entity)) {
				logger.info("DROP HistoryTable: "+entity.historytablename);
			}
		}

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}