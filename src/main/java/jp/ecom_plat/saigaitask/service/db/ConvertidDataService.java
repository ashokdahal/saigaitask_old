/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.convertidData;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.db.ConvertidData;
import jp.ecom_plat.saigaitask.service.AbstractService;


@org.springframework.stereotype.Repository
public class ConvertidDataService extends AbstractService<ConvertidData> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public ConvertidData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 自治体IDで検索
     * @param govid
     * @return
     */
    public List<ConvertidData> findByLocalgovInfoId(Long govid) {
    	return select().where(
    			eq(convertidData().localgovinfoid(), govid)).getResultList();
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
	public List<ConvertidData> findByCondition(Map<String, Object> conditions) {
		return select()
			.where(conditions)
			.getResultList();
	}

}
