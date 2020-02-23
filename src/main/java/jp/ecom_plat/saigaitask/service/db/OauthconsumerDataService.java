/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.oauthconsumerData;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.OauthconsumerData;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * OAuthコンシューマデータのサービス.
 */
@org.springframework.stereotype.Repository
public class OauthconsumerDataService extends AbstractService<OauthconsumerData> {

	/**
	 * @param id ID
	 * @return 検索結果
	 */
	public OauthconsumerData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
	public List<OauthconsumerData> findByLocalgovinfoid(long localgovinfoid) {
		return select().where(
				eq(oauthconsumerData().localgovinfoid(), localgovinfoid)
				)
				.getResultList();

	}

	/**
	 * @param localgovinfoid 自治体ID
	 * @param applicationname アプリケーション名
	 * @return 検索結果
	 */
	public OauthconsumerData findByLocalgovinfoidAndApplicationname(long localgovinfoid, String applicationname) {
		return select().where(
				eq(oauthconsumerData().localgovinfoid(), localgovinfoid),
				eq(oauthconsumerData().applicationname(), applicationname)
				)
				.getSingleResult();

	}
}
