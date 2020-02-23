/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.db.ImporttracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class ImporttracktableInfoService extends AbstractService<ImporttracktableInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public ImporttracktableInfo findById(Long id) {
        return select().id(id).getSingleResult();
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
	public List<ImporttracktableInfo> findByCondition(Map<String, Object> conditions) {
		return select()
			.where(conditions)
			.getResultList();
	}
}
