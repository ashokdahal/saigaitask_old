package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.whiteboardData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.and;
import jp.ecom_plat.saigaitask.entity.db.WhiteboardData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class WhiteboardDataService extends AbstractService<WhiteboardData> {

    public WhiteboardData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
	 * メッセージを更新
	 * @param entity 更新対象データ
	 * @return
	*/
	public int insert(WhiteboardData entity) {
		return jdbcManager
			.insert(entity)
			.execute();
	}

	/**
	 * 最新のメッセージを取得
	 * @param groupid
	 * @param trackdataid
	 * @return 最新のメッセージ
	 */
	public WhiteboardData findBygroupIdAndTrackdataId(Long groupid, Long trackdataid){
		return select().where(
				and(
					eq(whiteboardData().groupid(), groupid),
					eq(whiteboardData().trackdataid(), trackdataid)
    			)).orderBy(desc(whiteboardData().id())).limit(1).getSingleResult();

	}
}
