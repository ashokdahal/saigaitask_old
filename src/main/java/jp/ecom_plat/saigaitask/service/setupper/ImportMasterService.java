/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.setupper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.AdminmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangmesInfo;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.EntityType;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

/**
 * システムマスタをインポートするサービスクラスです.
 */
@org.springframework.stereotype.Service
public class ImportMasterService extends AbstractImportService {

	/** Logger */
	protected Logger logger = Logger.getLogger(ImportMasterService.class);

	// service
	@Resource protected ExcelImportInfoService excelImportInfoService;
	@Resource protected ImportInfoService importInfoService;

	/**
	 * システムマスタのチェック.
	 * エクセルにエクスポートされたマスタが、
	 * DBに存在するか、IDで検索してチェックする.
	 * @param masterExportXsl
	 * @return チェック結果
	 */
	public Map<Class<?>, Map<Object, Boolean>> checkMaster(File masterExportXsl) {
		return restoreMaster(masterExportXsl, false);
	}

	/**
	 * マスタをリストアする.
	 * @param masterExportXsl マスタのエクセル
	 * @return リストア結果
	 */
	public Map<Class<?>, Map<Object, Boolean>> restoreMaster(File masterExportXsl) {
		return restoreMaster(masterExportXsl, true);
	}

	/**
	 * マスタをリストアする.
	 * @param masterExportXsl マスタのエクセル
	 */
	protected Map<Class<?>, Map<Object, Boolean>> restoreMaster(File masterExportXsl, boolean execRestore) {
		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		try {
			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(masterExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			// リストアフラグ
			// 削除したらマップに<テーブル名,false>で登録、リストアしたらtrueにする
			Map<String, Boolean> restored = new HashMap<String, Boolean>();

			// マスタテーブルのリストア時に、localgovinfoid=0 が消えるため、存在チェック
			LocalgovInfo localgovInfo0 = null;
			{
				List<?> records = excelImportInfoService.readRecords(wb, LocalgovInfo.class);
				for(Object record: records) {
					LocalgovInfo localgovInfo = (LocalgovInfo) record;
					if(Long.valueOf(0l).equals(localgovInfo.id)) {
						localgovInfo0 = localgovInfo;
						break;
					}
				}
			}
			if(localgovInfo0==null) throw new ServiceException(lang.__("Since the data of the local gov. ID = 0 is not found, it was canceled to restore the system master."));

			// リストア後に、戻したいデータをバックアップしておく
			List<Object> backupEntities = new ArrayList<Object>();
			// DBから システム管理のアカウント情報(groupid=0)を取得しておく
			jp.ecom_plat.saigaitask.entity.db.GroupInfo groupInfo0 = groupInfoService.findById(0l);
			if(groupInfo0!=null) backupEntities.add(groupInfo0);

			// 全自治体を削除する
			{
				// 都道府県の自治体IDを解除
				List<LocalgovInfo> localgovInfos = localgovInfoService.findAll();
				for(LocalgovInfo localgovInfo : localgovInfos) {
					localgovInfo.preflocalgovinfoid = null;
					localgovInfoService.update(localgovInfo);
				}
				// 自治体の削除
				for(LocalgovInfo localgovInfo : localgovInfos) {
					DeleteCascadeResult result = localgovInfoService.deleteCascade(localgovInfo);
					ByteArrayOutputStream baos = null;
					PrintStream out = null;
					try {
						// DBテーブルの削除
						result = localgovInfoService.deleteCascade(localgovInfo);

						// 削除実行
						result.commit();

						// ログに記録
						baos = new ByteArrayOutputStream();
						out = new PrintStream(baos);
						result.printResult(out);
						logger.info("DeleteCascade: localgovinfoid="+localgovInfo.id+"\n"+baos.toString());
					} catch(Exception e) {
						//if(result!=null) {
						//	result.rollback(null);
						//}
						throw new ServiceException(lang.__("Error occurred in process of deleting all municipalities."), e);
					} finally {
						if(out!=null) out.close();
						if(baos!=null) baos.close();
					}
				}
			}

			// master class
			Map<Class<?>, Map<Object, Boolean>> notexistRecord = new LinkedHashMap<Class<?>, Map<Object,Boolean>>();
			List<Class<?>> masterClasses = ExportService.entitys.get(EntityType.master);
			for(Class<?> entityClass : masterClasses) {
				if(entityClass==AdminmenuInfo.class) {
					logger.debug(lang.__("AdminmenuInfo can not be imported in system master."));
					continue;
				}
				else if(entityClass==MultilangInfo.class || entityClass==MultilangmesInfo.class) {
					logger.debug(lang.__("This entity class can not be imported in system master: "+entityClass.getSimpleName()));
					continue;
				}
				else if(exportService.isAuthTable(entityClass)) {
					logger.debug(lang.__("{0} can not be imported for {0}  is system master of authentication info.", entityClass.getSimpleName()));
					continue;
				}
				// テーブル名を取得
				String tableName = null;

				try {
					// テーブル名を取得
					tableName = DatabaseUtil.getTableName(entityClass);
					// シートからレコードを取得
					List<?> records = excelImportInfoService.readRecords(wb, entityClass);
					if(records==null) {
						logger.debug("Exported "+tableName+" sheet is not found.");
					}
					else if(records.size()==0) {
						logger.debug("Exported "+tableName+" sheet is empty.");
					}
					// リストアを実行
					if(execRestore) {
						// まだ削除されていなければ削除する
						if(!restored.containsKey(tableName)) {
							// 外部キーで依存しているテーブルごと削除する
							Map<String, Integer> deleted = tableService.deleteAllWithDependency(tableName, restored);
							// 削除したテーブルを記録する（依存テーブル含む）
							for(Map.Entry<String, Integer> entry : deleted.entrySet()) {
								String deletedTableName = entry.getKey();
								Integer deletedNum = entry.getValue();
								restored.put(deletedTableName, false);
								logger.debug("Delete All "+deletedTableName+": "+deletedNum);
							}
						}
						// IDそのままでリストア
						tableService.insertKeepId(records);
						// シーケンスをリセット
						tableService.resetMaxSequence(tableName);
					}
					// マスタのチェックのみ
					else {
						// ID 存在チェック用Map
						Map<Object, Boolean> notexistId = new HashMap<Object, Boolean>();

						// idフィールド
						Field idField = entityClass.getField("id");
						// 各レコードの存在をチェック
						String sql = "SELECT 1 FROM "+tableName+" WHERE id=?";
						if(records!=null) {
							for(Object record : records) {
								Object id = idField.get(record);
								long count = jdbcManager.getCountBySql(sql, id); // 1件ずつの存在チェックは遅いかも？
								if(count==0) {
									notexistId.put(id, count==1);
								}
							}
						}

						// 存在しないIDがあれば記録
						if(0<notexistId.size()) {
							notexistRecord.put(entityClass, notexistId);
						}
					}
				} catch(Exception e) {
					tableService.rollbackResetMaxSequenceAll();
					throw new ServiceException((StringUtil.isNotEmpty(tableName)?tableName:lang.__("Unable to restore {0} master.", entityClass.getSimpleName())), e);
				}
			}

			// 念のため、master 以外の全エンティティを削除する
			{
				logger.info("begin of delete all data, info entities");
				List<EntityType> entityTypes = new ArrayList<EntityType>();
				entityTypes.add(EntityType.data);
				entityTypes.add(EntityType.info);
				for(Map.Entry<EntityType, List<Class<?>>> entry : ExportService.entitys.entrySet()) {
					EntityType entityType = entry.getKey();
					if(entityTypes.contains(entityType)==false) {
						entityTypes.add(entityType);
					}
				}
				entityTypes.remove(EntityType.master);

				Map<String, Boolean> excludes = new HashMap<String, Boolean>();

				for(EntityType entityType: entityTypes) {
					List<Class<?>> entitys = ExportService.entitys.get(entityType);
					for(Class<?> entity : entitys) {
						String tableName = DatabaseUtil.getTableName(entity);
						if(tableService.isExists(tableName) && excludes.containsKey(tableName)==false) {
							// 外部キーで依存しているテーブルごと削除する
							Map<String, Integer> deleted = tableService.deleteAllWithDependency(tableName, excludes);
							// 削除したテーブルを記録する（依存テーブル含む）
							for(Map.Entry<String, Integer> entry : deleted.entrySet()) {
								String deletedTableName = entry.getKey();
								Integer deletedNum = entry.getValue();
								excludes.put(deletedTableName, false);
								logger.debug("Delete All "+deletedTableName+": "+deletedNum);
							}
						}
					}
				}

				logger.info("end of delete all data, info entities");
			}

			// info(localgovinfoid=0) class
			tableService.insertKeepId(localgovInfo0);
			importInfoService.importInfo(masterExportXsl, 0L);

			// バックアップしたエンティティを戻しておく
			tableService.insertKeepId(backupEntities);

			return notexistRecord;

		} catch(Exception e) {
			throw new ServiceException(lang.__("Unable to restore master."), e);
		}
	}
}
