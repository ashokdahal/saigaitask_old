/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.setupper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.seasar.extension.jdbc.JdbcManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TableService.Dependency;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;

/**
 * インポートの抽象サービスです。
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractImportService {

	// jdbc, db
	@Resource protected JdbcManager jdbcManager;

	// service
	@Resource protected LocalgovInfoService localgovInfoService;
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected TableService tableService;
	@Resource protected ExportService exportService;
	// ServletAPI (暗黙コンポーネント)
	@Resource protected ServletContext application;


	/**
	 * 指定したタイプのエンティティのクラスを
	 * 依存のないものから順のリストで取得する。
	 * @param entityType エンティティタイプ
	 * @return 依存のないものから順のリスト
	 */
	public List<Class<?>> getClassesOrderbyDependency(ExportService.EntityType entityType) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		// 指定種別のすべてのエンティティクラス
		List<Class<?>> entitys = new ArrayList<Class<?>>(ExportService.entitys.get(entityType));
		for(Class<?> entity : entitys) {
			// 追加されていれば次へ
			if(classes.contains(entity)) continue;

			// 依存しているテーブルを先に追加
			List<Dependency> dependencys = tableService.selectDependedOn(DatabaseUtil.getTableName(entity));
			// 得られた結果は、依存元(依存している方)から依存先(依存されている方)で求めているので、
			// 逆順にして依存先からインポートする。
			for(int idx=dependencys.size()-1; 0<=idx; idx--) {
				Dependency dependency = dependencys.get(idx);
				// 依存先を先にインポートする
				Class<?> toEntityClass = tableService.getEntity(dependency.referenceFromTableName);
				// 設定エンティティでないなら、次へ
				if(!entitys.contains(toEntityClass)) continue;
				// 未追加なら追加する
				if(!classes.contains(toEntityClass)) {
					classes.add(toEntityClass);
				}
			}

			// 未追加なら追加する
			if(!classes.contains(entity)) {
				classes.add(entity);
			}
		}

		return classes;
	}
}
