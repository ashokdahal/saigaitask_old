/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.disconnect;

import static jp.ecom_plat.saigaitask.entity.Names.convertidData;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.ConvertidData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.ConvertidDataService;
import jp.ecom_plat.saigaitask.util.BeanUtiles;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.FileUtil;


/**
 * エクセルからテーブルレコードをインポートするサービスです.
 * テーブルごとにエクセルシートがわかれており、
 * エクセルシート名がテーブル名(31文字まで)に設定されていること.
 * エクセルシートには、一行目に列名が載っていること.
 * 二行目以降はレコードとみなしてインポートする.
 */
@org.springframework.stereotype.Service
public class ExcelImportService extends AbstractImportService {

	@Resource
	private ConvertidDataService convertidDataService;

	/** Logger */
	protected Logger logger = Logger.getLogger(ExcelImportService.class);

	/**
	 * インポート結果
	 */
	public static class ImportResult {
		/** インポートしたINFOクラス */
		public List<Class<?>> importClasses = new ArrayList<Class<?>>();
		/** ID変換用マップ */
		public Map<String, Map<Object, Object>> idConverter = new HashMap<String, Map<Object, Object> >();
		/**
		 * インポート結果
		 * Map<エンティティクラス, インポート数>
		 * インポート数は、インポート処理されていれば、0以上
		 * インポート処理されていなければ、null
		 */
		public Map<Class<?>, List<?>> importResult = new HashMap<Class<?>, List<?>>();


		public Map<String,List<List<Map<String,Object>>>> sameListMap = new HashMap<String,List<List<Map<String,Object>>>>();
	}

	/**
	 * エンティティクラスを指定してエクセルからインポート
	 * @param wb エクセルのブック
	 * @param idConverter インポートデータと、DBにインポートしたデータのIDマッピング
	 *                    指定しない場合は、インポートせず、インポートデータを読み込む
	 * @param importClasses インポート対象エンティティクラス
	 * @return
	 */
	protected ImportResult importBy(HSSFWorkbook wb, ImportResult importResult, Long localgovinfoidDest) {
		for(Class<?> importClass : importResult.importClasses) {
			List<?> entities = importRecords(wb, importClass, importResult.idConverter, localgovinfoidDest);
			importResult.importResult.put(importClass, entities);
		}
		return importResult;
	}

	/**
	 * エンティティクラスを指定してエクセルからインポート
	 * @param wb エクセルのブック
	 * @param idConverter インポートデータと、DBにインポートしたデータのIDマッピング
	 *                    指定しない場合は、インポートせず、インポートデータを読み込む
	 * @param importClasses インポート対象エンティティクラス
	 * @return
	 */
	protected ImportResult importTrackTableBy(
			Boolean doImport,
			HSSFWorkbook wb,
			ImportResult importResult,
			Long localgovinfoidSrc,
			Long localgovinfoidDest,
			Long trackdataidSrc,
			Long trackdataidDest,
			Map<String, Map<String, String>> targetIdMap,
			Map<String, List<String>> sameTrackTableDatasMap,
			File uploadZipFileDir){
		for(Class<?> importClass : importResult.importClasses) {
			List<List<Map<String,Object>>> result = importTrackRecords(
					doImport,
					wb,
					importClass,
					localgovinfoidSrc,
					localgovinfoidDest,
					trackdataidSrc,
					trackdataidDest,
					targetIdMap,
					sameTrackTableDatasMap,
					uploadZipFileDir);
			if(result != null){
				importResult.sameListMap.put(DatabaseUtil.getTableName(importClass), result);
			}
		}
		return importResult;
	}

	/**
	 * レコードをエクセルシートから読み込みます.
	 * @param wb エクセルのブック
	 * @param entityClass 対象エンティティクラス
	 * @return 読み込んだエンティティのリスト
	 */
	protected List<?> readRecords(HSSFWorkbook wb, Class<?> entityClass) {
		return importRecords(wb, entityClass, null, null);
	}

	/**
	 * レコードをエクセルシートから読み込み、ID変換をします.
	 * @param doImport インポート実行フラグ
	 * @param wb エクセルのブック
	 * @param entityClass 対象エンティティクラス
	 * @param localgovinfoidSrc インポート元自治体ID
	 * @param localgovinfoidDest インポート先自治体ID
	 * @param trackdataidSrc インポート元災害ID
	 * @param trackdataidDest インポート先災害ID
	 * @param targetIdMap インポート対象とするインポート元エンティティとインポート先エンティティのIDマップ
	 * @param sameTrackTableDatasMap 更新判断が必要なインポート元エンティティのIDリスト
	 * @param uploadZipFileDir インポート元アップロードZIPファイル展開ディレクトリ
	 * @return doImport=falseの場合、時刻属性が一致したエンティティのマップのリスト。doImport=trueの場合は空リスト。
	 */
	protected List<List<Map<String,Object>>> importTrackRecords(
			Boolean doImport,
			HSSFWorkbook wb,
			Class<?> entityClass,
			Long localgovinfoidSrc,
			Long localgovinfoidDest,
			Long trackdataidSrc,
			Long trackdataidDest,
			Map<String, Map<String, String>> targetIdMap,
			Map<String, List<String>> sameTrackTableDatasMap,
			File uploadZipFileDir) throws SQLRuntimeException {
		boolean isImport = doImport;
		List<List<Map<String,Object>>> retValue = new ArrayList<List<Map<String,Object>>>();

		try {
			// テーブル名からシートを取得
			String tableName = DatabaseUtil.getTableName(entityClass);
			String sheetName = tableName;
			if(31<tableName.length()) sheetName = tableName.substring(0, 31); // シート名は31文字まで
			HSSFSheet sheet = wb.getSheet(sheetName);
			if(sheet==null) {
				logger.warn("Excel Sheet not found: "+tableName);
				return null;
			}
			// シートを読み込み
			List<Object> entities = new ArrayList<Object>();
			List<String> fieldNames = new ArrayList<String>();
			Map<String, Field> fields = new HashMap<String, Field>();
			boolean isHeader = true;
			for(int rowIdx=0; rowIdx<=sheet.getLastRowNum(); rowIdx++) {
				HSSFRow row = sheet.getRow(rowIdx);

				// 行から、1レコード分のデータを取得
				Map<String, Object> record = new HashMap<String, Object>();
				for(int colIdx=0; colIdx<row.getLastCellNum(); colIdx++) {
					HSSFCell cell = row.getCell(colIdx);
					if(cell==null) continue;
					// get value
					Object val = null;
					switch(cell.getCellType()) {
					case HSSFCell.CELL_TYPE_BLANK:
						val = null;
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						double tempVal = cell.getNumericCellValue();
						String tempValStr = Double.toString(tempVal);
						if(tempValStr.endsWith(".0")){
							tempValStr = tempValStr.substring(0, tempValStr.length() -2);
						}
						val = tempValStr;
//						val = cell.getNumericCellValue();
						break;
					default:
						val = cell.getStringCellValue();
						break;
					}

					// 一行目にフィールド名が入っている
					if(isHeader) {
						String fieldName = (String) val;
						fieldNames.add(fieldName);
						// インポートならID変換処理
						if(isImport) {
							fields.put(fieldName, entityClass.getField(fieldName));
						}
					}
					else {
						String fieldName = fieldNames.get(colIdx);
						// 値を保存
						record.put(fieldName, val);
					}
				}

				// ヘッダーの読み込み終了
				if(isHeader) {
					isHeader = false;
				}
				// データ行の場合
				else {
					// エンティティのオブジェクト
					Object entity = entityClass.newInstance();
					try{
						BeanUtiles.copyProperties(record, entity);
					}catch(ParseException e){
						throw e;
					}
					entities.add(entity);

					// インポートの場合
					if(isImport) {
						Map<String, Object> modifyRecord = new HashMap<String, Object>();
						for(Map.Entry<String, Object> e : record.entrySet()){
							modifyRecord.put(e.getKey(), e.getValue());
						}
						Object modifyEntity = entityClass.newInstance();
						if(TrackDataExportService.hasTrackdataid(entity.getClass())){
							String fileId = (String)record.get("id");
							String targetId = null;
							boolean updateRecoed = false;
							if(sameTrackTableDatasMap != null && sameTrackTableDatasMap.size() >0){
								List<String> sameTrackTableDatas = sameTrackTableDatasMap.get(tableName);
								if(sameTrackTableDatas != null && sameTrackTableDatas.size() > 0){
									if(sameTrackTableDatas.indexOf(fileId) >= 0){
										updateRecoed = true;
										if(targetIdMap != null && targetIdMap.size() > 0){
											Map<String, String> tablesTargetIdMap = targetIdMap.get(tableName);
											if(tablesTargetIdMap != null && tablesTargetIdMap.size() > 0){
												targetId = tablesTargetIdMap.get(fileId);
											}
										}
									}
								}
							}
							modifyRecord.put("trackdataid", trackdataidDest);
							int execResult = 0;
							try{
								// 添付ファイルのあるテーブルの場合、ファイルもコピーする
								if(uploadZipFileDir != null){
									String filePath = (String)modifyRecord.get("filepath");
									if(! StringUtil.isEmpty(filePath)){
								   	    int point = filePath.lastIndexOf("/");
										String fromFileName;
								   	    if (point != -1) {
								   	    	fromFileName =  filePath.substring(point + 1);
								   	    }else{
								   	    	fromFileName =  filePath;
								   	    }
										File fromDir = new File(uploadZipFileDir,localgovinfoidSrc + "/" + trackdataidSrc);
						            	if(fromDir.exists()){
						            		File  fromFile = new File(fromDir, fromFileName);
						            		if(fromFile.exists()){
						            			// コピー先ディレクトリを特定する
						            			String targetDirPath = application.getRealPath("/upload/");
						            			targetDirPath += "/"+(localgovinfoidDest.toString()+"/");
						            			File localIdDir = new File(targetDirPath);
						            			if(! localIdDir.exists()){
						            				localIdDir.mkdirs();
						            			}
						        				String targetTrackDataDirPath = targetDirPath + (trackdataidDest.toString()+"/");
						        				File targetTrackDataDir = new File(targetTrackDataDirPath);
						        				if(! targetTrackDataDir.exists()){
						        					targetTrackDataDir.mkdirs();
						        				}

				            					File toFile = new File(targetTrackDataDir,fromFileName);
				            					// 同名のファイルが存在していた場合は、連番をつけて別ファイルとする。
				            					if(toFile.exists()){
				            						String [] separatedToFileName = FileUtil.getSeparatedFilename(fromFileName);
				            						int idx = 1;
				            						while(true){
				            							if(StringUtil.isEmpty(separatedToFileName[1])){
				            								toFile = new File(targetTrackDataDir,separatedToFileName[0] + "_" + idx);
				            							}else{
				            								toFile = new File(targetTrackDataDir,separatedToFileName[0] + "_" + idx + "." + separatedToFileName[1]);
				            							}

				            							if(toFile.exists()){
				            								idx++;
				            							}else{
				            								break;
				            							}
				            						}
				            					}
				            					// コピー実行
				            					if(! FileUtil.fileCopyByPath(fromFile.getAbsolutePath(), toFile.getAbsolutePath())){
				            						// TODO エラーチェック
				            					}else{
				            						// filepathを更新
				            						modifyRecord.put("filepath","/upload/" + localgovinfoidDest + "/" + trackdataidDest + "/" + toFile.getName());
				            					}
						            		}
						            	}
									}
								}

								String newIdStr = null;
								String oldIdStr = (String)modifyRecord.get("id");
								if(targetId != null && updateRecoed){
									modifyRecord.put("id", targetId);
									BeanUtiles.copyProperties(modifyRecord, modifyEntity);
									execResult = jdbcManager.update(modifyEntity).execute();
									newIdStr = targetId;
								}else if(! updateRecoed){
									modifyRecord.put("id", null);
									BeanUtiles.copyProperties(modifyRecord, modifyEntity);
									execResult = jdbcManager.insert(modifyEntity).execute();
									Long resultValue = jdbcManager.selectBySql(Long.class, "select lastval()").getSingleResult();
									newIdStr = Long.toString(resultValue);
								}else{
									execResult = 1;
								}
								// 登録チェック
								if(execResult!=1) {
									logger.error(lang.__("Unable to register {0} record.", entityClass.getSimpleName()+".id: "+fileId));
								}

								// 子テーブルのデータ登録
								List<Class<?>> childEntities = TrackDataExportService.getChildEntities(entity.getClass());
								if(childEntities != null){
									for(Class<?> clazz : childEntities){
										importChildRecord(wb, clazz, oldIdStr, newIdStr);
									}
								}
							}catch(SQLRuntimeException e){
								// エラーログ用に、一度キャッチする
								String errorMessage = DatabaseUtil.getErrorMessage(e);
								if(StringUtil.isNotEmpty(errorMessage)) {
									String message = entityClass.getSimpleName()+lang.__("Unable to import {0} for {1}.", " ID="+fileId, errorMessage);
									// エラーメッセージをログ出力
									String logMessage = message.toString();
									// PSQLException メッセージを追記
									logMessage += "\n"+e.getMessage();
									// インサート対象のエンティティを追記
									logMessage += "\n"+ReflectionToStringBuilder.toString(modifyEntity, ToStringStyle.SHORT_PREFIX_STYLE);
									logger.error(logMessage);

									// エラーメッセージを画面に表示
									throw new ServiceException(message.toString(), e);
								}
								// そのまま投げる
								throw e;
							}
						}
					}else{
						try{
							if(TrackDataExportService.hasTrackdataid(entity.getClass())){
								String timeStr = TrackDataExportService.getTimeColumnName(entity.getClass());
								if(record.get(timeStr) != null){
									String registtime = (String)record.get(timeStr);
									// yyyy-MM-dd HH:mm:ss
									String registtimeStr = registtime.substring(0, 19);
									// DBを検索する
									List<BeanMap> dbResult = tableService.selectByTrackdataid(tableName, trackdataidDest);
									// 同じ時刻のデータがあるかチェック
									for(BeanMap dbRecord : dbResult){
										if(dbRecord.get(timeStr) != null){
											Timestamp dbRegisttime = (Timestamp)dbRecord.get(timeStr);
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											String dbRegisttimeStr = sdf.format(dbRegisttime);
											if(registtimeStr.equals(dbRegisttimeStr)){
												List<Map<String,Object>> sameList = new ArrayList<Map<String,Object>>();
												sameList.add(record);
												sameList.add(dbRecord);
												retValue.add(sameList);
											}
										}
									}
								}
							}

						}catch(Exception e){
							e.printStackTrace();
							logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
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

			return retValue;
		} catch(NoSuchFieldException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
//			return new ArrayList<Object>();
			return null;
		} catch (InstantiationException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
//			return new ArrayList<Object>();
			return null;
		} catch (IllegalAccessException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
//			return new ArrayList<Object>();
			return null;
		} catch(ParseException e){
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
//			return new ArrayList<Object>();
			return null;
		}
	}

	/**
	 * レコードをエクセルシートから読み込み、ID変換をします.
	 * @param wb エクセルのブック
	 * @param entityClass 対象エンティティクラス
	 * @param idConverter インポートデータと、DBにインポートしたデータのIDマッピング
	 *                    指定しない場合は、インポートせず、インポートデータを読み込む
	 * @return 読み込んだエンティティ、または、インポートしたエンティティのリスト
	 */
	protected List<?> importRecords(HSSFWorkbook wb, Class<?> entityClass, Map<String, Map<Object, Object>> idConverter, Long localgovinfoidDest) throws SQLRuntimeException {
		//boolean isRead = idConverter==null;
		boolean isImport = idConverter!=null;

		try {
			// テーブル名からシートを取得
			//Table table = entityClass.getAnnotation(Table.class);
			//String tableName = table.name();
			String tableName = DatabaseUtil.getTableName(entityClass);
			String sheetName = tableName;
			if(31<tableName.length()) sheetName = tableName.substring(0, 31); // シート名は31文字まで
			HSSFSheet sheet = wb.getSheet(sheetName);
			if(sheet==null) {
				logger.warn("Excel Sheet not found: "+tableName);
				return null;
			}
			// シートを読み込み
			List<Object> entities = new ArrayList<Object>();
			List<String> fieldNames = new ArrayList<String>();
			Map<String, Field> fields = new HashMap<String, Field>();
			List<Map<Object, Object>> idMaps = new ArrayList<Map<Object,Object>>();
			// records
			boolean isHeader = true;
			for(int rowIdx=0; rowIdx<=sheet.getLastRowNum(); rowIdx++) {
				HSSFRow row = sheet.getRow(rowIdx);

				// 行から、1レコード分のデータを取得
				Map<String, Object> record = new HashMap<String, Object>();
				for(int colIdx=0; colIdx<row.getLastCellNum(); colIdx++) {
					HSSFCell cell = row.getCell(colIdx);
					if(cell==null) continue;
					// get value
					Object val = null;
					switch(cell.getCellType()) {
					case HSSFCell.CELL_TYPE_BLANK:
						val = null;
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						val = cell.getNumericCellValue();
						break;
					default:
						val = cell.getStringCellValue();
						break;
					}

					// 一行目にフィールド名が入っている
					if(isHeader) {
						String fieldName = (String) val;
						fieldNames.add(fieldName);
						// インポートならID変換処理
						if(isImport) {
							idMaps.add(idConverter.get(val));
							fields.put(fieldName, entityClass.getField(fieldName));
						}
					}
					else {
						String fieldName = fieldNames.get(colIdx);
						// 値を保存
						record.put(fieldName, val);
					}
				}

				// ヘッダーの読み込み終了
				if(isHeader) {
					isHeader = false;
				}
				// データ行の場合
				else {
					// エンティティのオブジェクト
					Object entity = entityClass.newInstance();
					try{
						BeanUtiles.copyProperties(record, entity);
					}catch(ParseException e){
						throw e;
					}
					entities.add(entity);

					// インポートの場合
					if(isImport) {
						// 登録前に、ID変換処理
						for(int idMapsIdx=0; idMapsIdx<idMaps.size(); idMapsIdx++) {
							Map<Object, Object> idMap = idMaps.get(idMapsIdx);
							if(idMap!=null) {
								String fieldName = fieldNames.get(idMapsIdx);
								Field field = fields.get(fieldName);
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
						} catch(SQLRuntimeException e) {
							// エラーログ用に、一度キャッチする
							String errorMessage = DatabaseUtil.getErrorMessage(e);
							if(StringUtil.isNotEmpty(errorMessage)) {
								String message = entityClass.getSimpleName()+lang.__("Unable to import {0} for {1}.", " ID="+oldId, errorMessage);
								// エラーメッセージをログ出力
								String logMessage = message.toString();
								// PSQLException メッセージを追記
								logMessage += "\n"+e.getMessage();
								// インサート対象のエンティティを追記
								logMessage += "\n"+ReflectionToStringBuilder.toString(entity, ToStringStyle.SHORT_PREFIX_STYLE);
								logger.error(logMessage);

								// エラーメッセージを画面に表示
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
						String idFieldName = entityClass.getSimpleName().toLowerCase()+"id";
						String idFieldNameSaved = "id";
						// xxxinfoid という書式でない idフィールド
						if(entityClass==GroupInfo.class) idFieldName = "groupid";
						if(entityClass==UnitInfo.class) idFieldName = "unitid";
						if(entityClass==UserInfo.class) idFieldName = "userid";

						// ID変換Map に記録
						Map<Object, Object> idMap = idConverter.get(idFieldName);
						if(idMap==null) {
							idMap = new HashMap<Object, Object>();
							idConverter.put(idFieldName, idMap);
						}
						idMap.put(oldId, newId);
						logger.debug("Regist convert "+idFieldName+": \""+oldId+"\" -> \""+newId+"\"");


						// ID変更履歴の保存
						try{
							int execResult;
							ConvertidData convertidData = new ConvertidData();
							BeanMap conditions = new BeanMap();
							conditions.put(convertidData().localgovinfoid().toString(), localgovinfoidDest);
							conditions.put(convertidData().entityname().toString(), entityClass.getSimpleName());
							conditions.put(convertidData().idname().toString(), idFieldNameSaved);
							conditions.put(convertidData().oldval().toString(), oldId.toString());
							int count = convertidDataService.getCount(conditions);
							if(count == 0 ){
								convertidData.localgovinfoid = localgovinfoidDest;
								convertidData.entityname = entityClass.getSimpleName();
								convertidData.idname = idFieldNameSaved;
								convertidData.oldval = oldId.toString();
								convertidData.newval = newId.toString();
								execResult = jdbcManager.insert(convertidData).execute();
							}else if(count == 1){
								convertidData = convertidDataService.findByCondition(conditions).get(0);
								convertidData.oldval = oldId.toString();
								convertidData.newval = newId.toString();
								execResult = jdbcManager.update(convertidData).execute();
							}
						} catch(SQLRuntimeException e) {
							// エラーログ用に、一度キャッチする
							String errorMessage = DatabaseUtil.getErrorMessage(e);
							if(StringUtil.isNotEmpty(errorMessage)) {
								String message = lang.__("Failed to save {0} history because of {1}.", entityClass.getSimpleName()+" ID:"+oldId+" -> " + newId, errorMessage);
								// エラーメッセージをログ出力
								String logMessage = message.toString();
								// PSQLException メッセージを追記
								logMessage += "\n"+e.getMessage();
								// インサート対象のエンティティを追記
								logMessage += "\n"+ReflectionToStringBuilder.toString(entity, ToStringStyle.SHORT_PREFIX_STYLE);
								logger.error(logMessage);

								// エラーメッセージを画面に表示
								throw new ServiceException(message.toString(), e);
							}
							// そのまま投げる
							throw e;
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
		} catch(NoSuchFieldException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return new ArrayList<Object>();
		} catch (InstantiationException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return new ArrayList<Object>();
		} catch (IllegalAccessException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return new ArrayList<Object>();
		} catch(ParseException e){
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return new ArrayList<Object>();
		}
	}

	private Boolean importChildRecord(HSSFWorkbook wb,  Class<?> entityClass, String oldParentIdStr, String newParentIdStr){
		try {
			// テーブル名からシートを取得
			String tableName = DatabaseUtil.getTableName(entityClass);
			String sheetName = tableName;
			if(31<tableName.length()) sheetName = tableName.substring(0, 31); // シート名は31文字まで
			HSSFSheet sheet = wb.getSheet(sheetName);
			if(sheet==null) {
				logger.warn("Excel Sheet not found: "+tableName);
				return false;
			}

			// 対象となる親テーブル名IDを持つレコードを削除しておく
			String parentIdColumnName = TrackDataExportService.getParentIdColumnName(entityClass);
			List<BeanMap> dbResult = tableService.selectById(tableName, parentIdColumnName, Long.parseLong(newParentIdStr));
			for(BeanMap beanMap : dbResult){
				Object dBntity = entityClass.newInstance();
				BeanUtiles.copyProperties(beanMap, dBntity);
				tableService.deleteByEntity(dBntity);
			}

			// シートを読み込み
			List<Object> entities = new ArrayList<Object>();
			List<String> fieldNames = new ArrayList<String>();
			Map<String, Field> fields = new HashMap<String, Field>();
			boolean isHeader = true;
			for(int rowIdx=0; rowIdx<=sheet.getLastRowNum(); rowIdx++) {
				HSSFRow row = sheet.getRow(rowIdx);

				// 行から、1レコード分のデータを取得
				Map<String, Object> record = new HashMap<String, Object>();
				for(int colIdx=0; colIdx<row.getLastCellNum(); colIdx++) {
					HSSFCell cell = row.getCell(colIdx);
					if(cell==null) continue;
					// get value
					Object val = null;
					switch(cell.getCellType()) {
					case HSSFCell.CELL_TYPE_BLANK:
						val = null;
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						double tempVal = cell.getNumericCellValue();
						String tempValStr = Double.toString(tempVal);
						if(tempValStr.endsWith(".0")){
							tempValStr = tempValStr.substring(0, tempValStr.length() -2);
						}
						val = tempValStr;
//						val = cell.getNumericCellValue();
						break;
					default:
						val = cell.getStringCellValue();
						break;
					}

					// 一行目にフィールド名が入っている
					if(isHeader) {
						String fieldName = (String) val;
						fieldNames.add(fieldName);
						// ID変換処理
						fields.put(fieldName, entityClass.getField(fieldName));
					}
					else {
						String fieldName = fieldNames.get(colIdx);
						// 値を保存
						record.put(fieldName, val);
					}
				}

				// ヘッダーの読み込み終了
				if(isHeader) {
					isHeader = false;
				}
				// データ行の場合
				else {
					String parentId = (String)record.get(parentIdColumnName);
					if(parentId.equals(oldParentIdStr)){
						// エンティティのオブジェクト
						Object entity = entityClass.newInstance();
						try{
							BeanUtiles.copyProperties(record, entity);
						}catch(ParseException e){
							throw e;
						}
						entities.add(entity);

						Map<String, Object> modifyRecord = new HashMap<String, Object>();
						for(Map.Entry<String, Object> e : record.entrySet()){
							modifyRecord.put(e.getKey(), e.getValue());
						}
						Object modifyEntity = entityClass.newInstance();

						String fileId = (String)record.get("id");
						modifyRecord.put("id", null);
						modifyRecord.put(parentIdColumnName, Long.parseLong(newParentIdStr));
						int execResult = 0;
						try{
							BeanUtiles.copyProperties(modifyRecord, modifyEntity);
							execResult = jdbcManager.insert(modifyEntity).execute();
							// 登録チェック
							if(execResult!=1) {
								logger.error(lang.__("Unable to register {0} record.", entityClass.getSimpleName()+".id: "+fileId));
							}
						}catch(SQLRuntimeException e){
							// エラーログ用に、一度キャッチする
							String errorMessage = DatabaseUtil.getErrorMessage(e);
							if(StringUtil.isNotEmpty(errorMessage)) {
								String message = entityClass.getSimpleName()+lang.__("Unable to import {0} for {1}.", " ID="+fileId, errorMessage);
								// エラーメッセージをログ出力
								String logMessage = message.toString();
								// PSQLException メッセージを追記
								logMessage += "\n"+e.getMessage();
								// インサート対象のエンティティを追記
								logMessage += "\n"+ReflectionToStringBuilder.toString(modifyEntity, ToStringStyle.SHORT_PREFIX_STYLE);
								logger.error(logMessage);

								// エラーメッセージを画面に表示
								throw new ServiceException(message.toString(), e);
							}
							// そのまま投げる
							throw e;
						}
					}
				}
			}

			// information log
			logger.info("Imported "+entityClass.getSimpleName()+": "+entities.size());
			return true;
		} catch(NoSuchFieldException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return false;
		} catch (InstantiationException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return false;
		} catch (IllegalAccessException e) {
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return false;
		} catch(ParseException e){
			logger.warn(lang.__("Unable to import {0}.", entityClass.getSimpleName()), e);
			return false;
		}

	}
}
