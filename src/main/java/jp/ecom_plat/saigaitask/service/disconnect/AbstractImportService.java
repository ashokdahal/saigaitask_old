/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.disconnect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;

import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TableService.Dependency;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;

/**
 * インポートの抽象サービスです。
 */
public abstract class AbstractImportService extends BaseService{
	// jdbc, db
	@Resource protected JdbcManager jdbcManager;

	// service
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected TableService tableService;
	@Resource protected TrackDataExportService trackDataExportService;
	@Resource protected ExportService exportService;


	/**
	 * 指定したタイプのエンティティのクラスを
	 * 依存のないものから順のリストで取得する。
	 * @return
	 */
	public List<Class<?>> getClassesOrderbyDependency(ExportService.EntityType entityType) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		// 指定種別のすべてのエンティティクラス
		List<Class<?>> baseEntitys = new ArrayList<Class<?>>(ExportService.entitys.get(entityType));

		// 通信途絶ツールに対応しているエンティティクラスのみに絞り込み
		List<Class<?>> entitys = new ArrayList<Class<?>>();
		for(Class<?> entity : baseEntitys){
			if(TrackDataExportService.isUseDisconnect(entity)){
				entitys.add(entity);
			}
		}

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
					if(toEntityClass == TrackmapInfo.class){
						classes.add(0, toEntityClass);
					}else{
						classes.add(toEntityClass);
					}
				}
			}

			// 未追加なら追加する
			if(!classes.contains(entity)) {
				if(entity == TrackmapInfo.class){
					classes.add(0, entity);
				}else{
					classes.add(entity);
				}
			}
		}

		return classes;
	}
}
