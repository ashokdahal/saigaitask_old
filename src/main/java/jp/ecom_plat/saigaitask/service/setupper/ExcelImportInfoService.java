/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.setupper;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.framework.beans.util.BeanUtil;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Observ;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.MobileqrcodeInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservlistInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.names.MobileqrcodeInfoNames;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TableSheetReader;

/**
 * エクセルからテーブルレコードをインポートするサービスです.
 * テーブルごとにエクセルシートがわかれており、
 * エクセルシート名がテーブル名(31文字まで)に設定されていること.
 * エクセルシートには、一行目に列名が載っていること.
 * 二行目以降はレコードとみなしてインポートする.
 */
@org.springframework.stereotype.Service
public class ExcelImportInfoService extends AbstractImportService {

	/** Logger */
	protected Logger logger = Logger.getLogger(ExcelImportInfoService.class);

	/**
	 * Id変換Mapを持つクラス.
	 * キーを内部で正しいキーに変換する。 (ex: issuetablemasterinfoid -> tablemasterinfoid)
	 */
	public static class IdConverver extends HashMap<String, Map<Object, Object>> {
		private static final long serialVersionUID = 1L;

		/** Logger */
		transient protected Logger logger = Logger.getLogger(IdConverver.class);

		@Override
		public Map<Object, Object> get(Object key) {
			// エンティティ名とセットにしないと正確でない
			return get(key, null);
		}

		/**
		 * フィールド名をキーに渡すことで、ID変換するマップ<インポート前ID, インポート後ID>を取得する.
		 * エンティティも渡すと、エンティティのクラスやフィールド値に応じて正しいID変換マップを返す.
		 * @param key ID変換したいフィールド名
		 * @param entity ID変換したいフィールドのエンティティオブジェクト
		 * @return ID変換マップ<インポート前ID, インポート後ID>
		 */
		public Map<Object, Object> get(Object key, Object entity) {
			// カラム名を別名にする
			if(entity!=null) {
				try {
					if(entity.getClass()==ObservlistInfo.class) {
						// 監視観測リスト情報の監視所IDは、監視観測IDに応じて参照するIDが変わるので変換する
						if(key.equals(Names.observlistInfo().observatoryinfoid().toString())){
							Field observidField = entity.getClass().getField(Names.observlistInfo().observid().toString());
							Integer observid = (Integer) observidField.get(entity);
							if(observid!=null) {
								if(observid.equals(Observ.RAIN)) {
									key = DatabaseUtil.getIdFieldName(ObservatoryrainInfo.class);
								}
								else if(observid.equals(Observ.RIVER)) {
									key = DatabaseUtil.getIdFieldName(ObservatoryriverInfo.class);
								}
								else if(observid.equals(Observ.DAM)) {
									key = DatabaseUtil.getIdFieldName(ObservatorydamInfo.class);
								}

							}
						}
					}
					// tablemasterinfoid の別名になっているものはここで定義する
					else if(
							(entity.getClass()==MeteotriggerInfo.class && key.equals(Names.meteotriggerInfo().issuetablemasterinfoid().toString()))
							|| (entity.getClass()==SummarylistInfo.class && key.equals(Names.summarylistInfo().tablemasterinfoid().toString()))
							|| (entity.getClass()==SummarylistInfo.class && key.equals(Names.summarylistInfo().targettablemasterinfoid().toString()))
							|| (entity.getClass()==MenuInfo.class && key.equals(Names.menuInfo().excellistoutputtablemasterinfoid().toString()))
					)
					{
						key = DatabaseUtil.getIdFieldName(TablemasterInfo.class);
					}
				} catch(Exception e) {
					logger.warn("IdConvert error: "+e.getMessage(), e);
				}
			}

			return super.get(key);
		}
	}

	/**
	 * インポート結果
	 */
	public static class ImportResult {
		/** インポートしたINFOクラス */
		public List<Class<?>> importClasses = new ArrayList<Class<?>>();
		/**
		 * ID変換用マップ 
		 * インポートデータと、DBにインポートしたデータのIDマッピング
		 */
		public IdConverver idConverter = new IdConverver();
		/**
		 * インポート結果
		 * Map<エンティティクラス, インポート数>
		 * インポート数は、インポート処理されていれば、0以上
		 * インポート処理されていなければ、null
		 */
		public Map<Class<?>, List<?>> importResult = new HashMap<Class<?>, List<?>>();
		/**
		 * Ver2.0 では UnitInfo は UnitInfo だけでなく GroupInfo にも紐づくため groupid が必須となる。
		 * しかし、旧バージョンのインポートの場合、groupid の指定なしの状態となるため、
		 * あとで groupid を設定できるように、インポート後 unitid から インポート後 groupid を取得できる Map を定義する.
		 */
		public Map<Long, Long> unitid2groupid = new HashMap<Long, Long>();
	}

	/**
	 * エンティティクラスを指定してエクセルからインポート
	 * @param wb エクセルのブック
	 * @param importResult インポート結果格納オブジェクト
	 *                    指定しない場合は、インポートせず、インポートデータを読み込む
	 * @param importClasses インポート対象エンティティクラス
	 * @return
	 */
	protected ImportResult importBy(HSSFWorkbook wb, ImportResult importResult) {
		for(Class<?> importClass : importResult.importClasses) {
			List<?> entities = importRecords(wb, importClass, importResult);
			importResult.importResult.put(importClass, entities);
		}
		return importResult;
	}

	/**
	 * @param wb Workbook
	 * @param entityClass Entity
	 * @return TableSheetReader
	 */
	public TableSheetReader getTableSheetReader(HSSFWorkbook wb, Class<?> entityClass) {
		// テーブル名からシートを取得
		//Table table = entityClass.getAnnotation(Table.class);
		//String tableName = table.name();
		String tableName = DatabaseUtil.getTableName(entityClass);
		String sheetName = TableSheetReader.formatSheetName(tableName);

		// Reader 初期化
		TableSheetReader reader = null;
		try {
			reader = new TableSheetReader(wb, sheetName);
		} catch(Exception e) {
			logger.warn(e);
			return null;
		}
		
		return reader;
	}

	/**
	 * レコードをエクセルシートから読み込みます.
	 * @param wb エクセルのブック
	 * @param entityClass 対象エンティティクラス
	 * @return 読み込んだエンティティのリスト
	 */
	protected List<?> readRecords(HSSFWorkbook wb, Class<?> entityClass) {
		return importRecords(wb, entityClass, null);
	}

	/**
	 * レコードをエクセルシートから読み込み、ID変換をします.
	 * @param wb エクセルのブック
	 * @param entityClass 対象エンティティクラス
	 * @param importResult インポート結果格納オブジェクト
	 *                    指定しない場合は、インポートせず、インポートデータを読み込む
	 * @return 読み込んだエンティティ、または、インポートしたエンティティのリスト
	 */
	protected List<?> importRecords(HSSFWorkbook wb, Class<?> entityClass, ImportResult importResult) throws SQLRuntimeException {
		IdConverver idConverter = importResult!=null ? importResult.idConverter : null;
		//boolean isRead = idConverter==null;
		boolean isImport = idConverter!=null;

		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		try {
			TableSheetReader reader = getTableSheetReader(wb, entityClass);
			if(reader==null) return null;

			// インポート用変数の初期化
			List<Object> entities = new ArrayList<Object>();
			Map<String, Field> fields = new HashMap<String, Field>();


			// インポートならID変換処理用の変数初期化
			// ヘッダ行からフィールドを取得
			if(isImport) {
				for(String fieldName : reader.fieldNames) {
					// 存在しないフィールドはインポート無視する
					try {
						fields.put(fieldName, entityClass.getField(fieldName));
					} catch(NoSuchFieldException e) {
						logger.warn("NoSuchField: " + entityClass.getName()+"."+fieldName+" Ignore import.");
					}
				}
			}

			// データ行の読込
			Map<String, Object> record = null;
			while((record=reader.readLine())!=null) {
				// エンティティのオブジェクト
				Object entity = entityClass.newInstance();
				if(entity instanceof TablemasterInfo) {
					// Ver2.0 にて TablemasterInfo.copy は Boolean から Short となったためデータ変換する
					String copy = (String) record.get("copy");
					if("true".equalsIgnoreCase(copy)) record.put("copy", TablemasterInfo.COPY_LATEST);
					if("false".equalsIgnoreCase(copy)) record.put("copy", TablemasterInfo.COPY_SHARE);
				}
				if(entity instanceof MobileqrcodeInfo) {
					// パースする日付を調整
					// NG:"2018-05-30 00:00:00.0"
					// OK:"2018/05/30 00:00:00.0"
					String dateStr = (String) record.get(MobileqrcodeInfoNames.authenticationdate().toString());
					if(StringUtil.isNotEmpty(dateStr)) {
						record.put(MobileqrcodeInfoNames.authenticationdate().toString(), dateStr.replaceAll("-", "/"));
					}
				}
				BeanUtil.copyProperties(record, entity);
				entities.add(entity);

				// インポートの場合
				if(isImport) {
					// 登録前に、ID変換処理
					for(Map.Entry<String, Field> entry : fields.entrySet()) {
						String fieldName = entry.getKey();
						Field field = entry.getValue();
						if(field==null) continue; // フィールドがなくなっていればID変換処理スキップ

						// ID変換マップがあれば変換
						Map<Object, Object> idMap = idConverter.get(fieldName, entity);
						if(idMap==null) continue;

						Object oldVal = FieldUtil.get(field, entity);
						if(oldVal==null) {
							logger.debug("Convert[Skipped] "+entityClass.getSimpleName()+"."+fieldName+": null");
						}
						else {
							Object newVal = idMap.get(oldVal);
							if(newVal==null) {
								logger.warn(lang.__("Destination of {0} not found.", entityClass.getSimpleName()+"."+fieldName+"="+oldVal));
							}
							FieldUtil.set(field, entity, newVal);
							logger.debug("Convert "+entityClass.getSimpleName()+"."+fieldName+": \""+oldVal+"\" -> \""+newVal+"\"");
						}
					}
					// 課情報の場合
					if(entity instanceof UnitInfo) {
						UnitInfo unitInfo = (UnitInfo) entity;
						// localgovinfoid が未指定なら、自動付与
						if(unitInfo.localgovinfoid==null) unitInfo.localgovinfoid=(Long)idConverter.get("localgovinfoid").values().iterator().next();
					}

					// インポートデータとしての ID を保存
					Field idField = fields.get("id");
					if(idField==null) {
						logger.fatal(lang.__("ID field not found in {0}.", entityClass.getSimpleName()));
					}
					Object oldId = idField.get(entity);

					// DB登録
					int insertResult = 0;
					try{
						insertResult = jdbcManager.insert(entity).execute();
					// SQLRuntimeException(参照制約), SEntityExistsException(一意制約)
					} catch(Exception e) {
						// エラーログ用に、一度キャッチする
						String errorMessage = DatabaseUtil.getErrorMessage(e);
						if(StringUtil.isNotEmpty(errorMessage)) {
							StringBuffer message = new StringBuffer(entityClass.getSimpleName()+lang.__("Unable to import {0} for {1}.", " ID="+oldId, errorMessage));
							// エラーメッセージをログ出力
							// PSQLException メッセージを追記
							 String logMessage = message+"\n"+e.getMessage();
							// インサート対象のエンティティを追記
							logMessage += "\n"+ReflectionToStringBuilder.toString(entity, ToStringStyle.SHORT_PREFIX_STYLE);
							logger.error(logMessage);

							// 最初に発生した例外のエラーメッセージを画面に表示
							Throwable t = e;
							while((t=t.getCause())!=null) {
								if(t instanceof SQLException) {
									// 最初に発生した例外の場合
									if(t.getCause()==null) {
										SQLException sqlException = (SQLException) t;
										message.append("<br/>"+sqlException.getMessage());
									}
								}
							}
							throw new ServiceException(message.toString(), e);
						}
						// そのまま投げる
						throw e;
					}

					// 登録チェック
					if(insertResult!=1) {
						logger.error(lang.__("Unable to register {0} record.", entityClass.getSimpleName()+".id: "+oldId));
					}

					// Insert 後の ID を取得
					Object newId = idField.get(entity);

					// IDフィールド名を取得
					String idFieldName = DatabaseUtil.getIdFieldName(entityClass);

					// ID変換Map に記録
					Map<Object, Object> idMap = idConverter.get(idFieldName);
					if(idMap==null) {
						idMap = new HashMap<Object, Object>();
						idConverter.put(idFieldName, idMap);
					}
					idMap.put(oldId, newId);
					logger.debug("Regist convert "+idFieldName+": \""+oldId+"\" -> \""+newId+"\"");

					// UnitInfo の場合は unitid2groupid に登録しておく
					if(importResult!=null) {
						if(entity instanceof UnitInfo) {
							// V2.0 以前の設定ファイルの場合、UnitInfo.groupid が設定されているため記録しておく
							Object oldgroupid = record.get("groupid");
							if(oldgroupid!=null) {
								// インポート後の groupid を取得する
								String groupidFieldName = DatabaseUtil.getIdFieldName(GroupInfo.class);
								Map<Object, Object> groupidMap = idConverter.get(groupidFieldName);
								Object newgroupid = groupidMap.get(Long.valueOf((String)oldgroupid));
								// インポート後unitid から インポート後groupid へのマップを記録
								importResult.unitid2groupid.put((Long) newId, (Long) newgroupid);
							}
						}
						
					}
				}
			}

			// information log
			if(isImport) {
				logger.info("Imported "+entityClass.getSimpleName()+": "+entities.size());
			}
			else {
				logger.info("Read "+entityClass.getSimpleName()+": "+entities.size());
			}

			return entities;
		} catch (InstantiationException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return new ArrayList<Object>();
		} catch (IllegalAccessException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return new ArrayList<Object>();
		}
	}
}
