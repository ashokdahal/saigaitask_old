/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.disastersituationhistoryData;
import static jp.ecom_plat.saigaitask.entity.Names.disastersummaryhistoryData;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.DisastersituationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class DisastersummaryhistoryDataService extends AbstractService<DisastersummaryhistoryData> {

    public DisastersummaryhistoryData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDでデータを検索
     * @param trackdataid 記録データID
     * @return 被災集計履歴データリスト
     */
    public List<DisastersummaryhistoryData> findByTrackDataId(Long tid) {
    	return select()
    			.where(
    			eq(disastersummaryhistoryData().trackdataid(), tid)
    			).orderBy(desc(disastersummaryhistoryData().period())).getResultList();
    }

    /**
     * 記録IDでデータを検索
     * @param trackdataid 記録データID
     * @return 被災集計履歴データリスト
     */
    public List<DisastersummaryhistoryData> findByTrackdataid(Long trackdataid) {
    	return select()
    			.innerJoin(disastersummaryhistoryData().disastersituationhistoryDataList())
    			.where(
    			eq(disastersummaryhistoryData().trackdataid(), trackdataid)
    			).orderBy(desc(disastersummaryhistoryData().period()), asc(disastersituationhistoryData().lineno())).getResultList();
    }

    /**
     * 記録IDで最新データを検索
     * @param trackdataid 記録データID
     * @return 被災集計履歴データ
     */
    public DisastersummaryhistoryData findCurrentByTrackdataid(Long trackdataid) {
    	return select().where(
    			eq(disastersummaryhistoryData().trackdataid(), trackdataid)
    			).orderBy(desc(disastersummaryhistoryData().period())).limit(1).getSingleResult();
    }

	@Override
	public DeleteCascadeResult deleteCascade(DisastersummaryhistoryData entity, DeleteCascadeResult result) {

		result.cascade(DisastersituationhistoryData.class, Names.disastersituationhistoryData().disastersummaryhistoryid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}