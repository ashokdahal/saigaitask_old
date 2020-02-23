/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.headofficeData;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.entity.db.HeadofficeData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.FileUtil;

@org.springframework.stereotype.Repository
public class HeadofficeDataService extends AbstractService<HeadofficeData> {

    public HeadofficeData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return 本部会議データリスト
     */
    public List<HeadofficeData> findByTrackDataId(Long tid) {
    	return select().where(
    			eq(headofficeData().trackdataid(), tid)
    			).orderBy(desc(headofficeData().registtime())).getResultList();
    }

    public List<HeadofficeData> check() {
    	List<HeadofficeData> reslist = select().leftOuterJoin(headofficeData().trackData()).getResultList();
    	List<HeadofficeData> nolist = new ArrayList<HeadofficeData>();
    	for (HeadofficeData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	return reslist;
    }

	@Override
	public DeleteCascadeResult deleteCascade(HeadofficeData entity, DeleteCascadeResult result) {

		// File 削除
		if(StringUtil.isNotEmpty(entity.filepath)) {
			result.cascadeFile(FileUtil.getFile(entity.filepath), entity, String.valueOf(entity.id), entity.filepath);
		}

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
