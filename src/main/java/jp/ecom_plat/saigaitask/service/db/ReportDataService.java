/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.reportData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.or;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.Reportcontent2Data;
import jp.ecom_plat.saigaitask.entity.db.ReportcontentData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class ReportDataService extends AbstractService<ReportData> {

    public ReportData findById(Long id) {
        return select()
				.leftOuterJoin(reportData().reportcontentDatas())
				.leftOuterJoin(reportData().reportcontent2Datas())
				.leftOuterJoin(reportData().trackData())
				.leftOuterJoin(reportData().trackData().localgovInfo())
				.id(id)
				.getSingleResult();
    }

    public List<ReportData> check() {
    	List<ReportData> reslist = select().leftOuterJoin(reportData().trackData()).getResultList();
    	List<ReportData> nolist = new ArrayList<ReportData>();
    	for (ReportData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }

    /**
     * 都道府県の記録データの災害グループに属している連携自治体の４号様式データを取得
     * @param preftrackdataid 都道府県の記録データID
     * @return
     */
    public List<ReportData> findTrackgroupReportDatas(long preftrackdataid) {

    	return select()
    			.leftOuterJoin(reportData().trackData())
    			.leftOuterJoin(reportData().trackData().cityTrackgroupDatas())
    			.where(
    					or(
    						eq(reportData().trackdataid(), preftrackdataid), // 都道府県の記録データ
    						eq(reportData().trackData().cityTrackgroupDatas().preftrackdataid(), preftrackdataid) // 都道府県の記録データの災害グループに入っている各市の記録データ
    					)
    			)
    			.orderBy(desc(reportData().registtime()))
    			.getResultList();
    }

    /**
     * 記録データIDから検索
     * @param id
     */
	public List<ReportData> findByTrackDataId(Long tid) {
		return select()
				.where(eq(reportData().trackdataid(), tid))
				.orderBy(desc(reportData().registtime()))
				.getResultList();
	}

	/**
     * 記録データIDから検索
     * @param id
     */
	public List<ReportData> findByTrackdataid(Long trackdataid) {
		return select()
				.leftOuterJoin(reportData().reportcontentDatas())
				.leftOuterJoin(reportData().reportcontent2Datas())
				.where(eq(reportData().trackdataid(), trackdataid))
				.orderBy(desc(reportData().registtime()))
				.getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(ReportData entity, DeleteCascadeResult result) {

		result.cascade(Reportcontent2Data.class, Names.reportcontent2Data().reportdataid(), entity.id);
		result.cascade(ReportcontentData.class, Names.reportcontentData().reportdataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
