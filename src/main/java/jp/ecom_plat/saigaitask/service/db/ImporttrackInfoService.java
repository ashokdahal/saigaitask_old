/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.importtrackInfo;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.db.ImporttracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;


@org.springframework.stereotype.Repository
public class ImporttrackInfoService extends AbstractService<ImporttrackInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public ImporttrackInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 自治体IDで検索
     * @param gid
     * @return
     */
    public ImporttrackInfo findByLocalgovInfoId(Long gid) {
    	return select().where(
    			eq(importtrackInfo().localgovinfoid(), gid)
    		).getSingleResult();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.where(conditions)
			.getCount();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
	 * @param conditions 検索条件マップ
	 * @return 検索結果
	 */
	public List<ImporttrackInfo> findByCondition(Map<String, Object> conditions) {
		return select()
			.where(conditions)
			.getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(ImporttrackInfo entity, DeleteCascadeResult result) {

		result.cascade(ImporttracktableInfo.class, Names.importtracktableInfo().importtrackinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
