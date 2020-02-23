/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.multilang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.extension.jdbc.SqlSelect;

import jp.ecom_plat.saigaitask.action.admin.multilang.ExportAction;
import jp.ecom_plat.saigaitask.action.admin.multilang.ImportAction;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.MultilangmesInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangmesInfoService;

@org.springframework.stereotype.Service
public class ImportService extends AbstractService<MultilangInfo>{
    @Resource protected UserTransaction userTransaction;

    @Resource protected MultilangInfoService multilangInfoService;
	@Resource protected MultilangmesInfoService multilangmesInfoService;

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ActionMessages importLang(File langDataFile, String importType, String langCode, String langName, String [] result){
		ActionMessages errors = new ActionMessages();

		try{
			// トランザクションの開始
	        userTransaction.begin();
	        try{
				// 言語情報テーブルに新規追加
		        if(importType.equals(ImportAction.IMPORTTYPE_NEW)){
					try{
						int currentDispOrder = multilangInfoService.getLargestDisporder(new HashMap<String, Object>());
						MultilangInfo multilangInfo = new MultilangInfo();
						multilangInfo.code = langCode;
						multilangInfo.name = langName;
						multilangInfo.disporder = currentDispOrder + 1;
						multilangInfo.updatetime = new Timestamp(System.currentTimeMillis());
						multilangInfoService.insert(multilangInfo);
					}catch(Exception e){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to create language info.{0}", e.getMessage()), false));
						e.printStackTrace();
						logger.error(e.getMessage(), e);
			        	userTransaction.setRollbackOnly();
					}
				}

		        if(errors.isEmpty()){
					MultilangInfo multilangInfo = multilangInfoService.findByCode(langCode);
					if(multilangInfo == null){
			        	userTransaction.setRollbackOnly();
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to get language info of language code {0}.", langCode), false));
					}else{
						try{
							if(importType.equals(ImportAction.IMPORTTYPE_NEW) || importType.equals(ImportAction.IMPORTTYPE_RENEW)){
								List<MultilangmesInfo> multilangmesInfoList = multilangmesInfoService.findByMultilanginfoid(multilangInfo.id);
								for(MultilangmesInfo multilangmesInfo : multilangmesInfoList){
									multilangmesInfoService.delete(multilangmesInfo);
								}
							}
						}catch(Exception e){
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to initialize language info of language code {0}. {1}", langCode, e.getMessage()), false));
				        	userTransaction.setRollbackOnly();
						}

				        if(errors.isEmpty()){
					        // 言語データファイルを読み込む
							int rowNum = 0;
							int totalcount = 0;
							int updatecount = 0;
							int insertcount = 0;
							BufferedReader br = null;
							try{
						        br = new BufferedReader(new InputStreamReader(new FileInputStream(langDataFile),"UTF-8"));
						        String line;
						        while ((line = br.readLine()) != null) {
					        		rowNum++;
					        		// 先頭行のチェック
					        		if(rowNum <= 1){
					            		// 空行の場合は無視する
					    	        	if(line == null || line.trim().length() <= 0){
					    	        		continue;
					    	        	}else{
					    	        		// ヘッダ行だった場合は無視
					    	        		String [] rowData = line.split("\t", -1);
						        			String rowDataId    = rowData[0];
						        			String rowDataKey   = rowData[1];
						        			String rowDataValue = rowData[2];
						        			if(ExportAction.CSVFILE_HEADER[0].equals(rowDataId.trim())
						        					&& ExportAction.CSVFILE_HEADER[1].equals(rowDataKey.trim())
					   	        					&& ExportAction.CSVFILE_HEADER[2].equals(rowDataValue.trim())
						        			){
						        				continue;
						        			}
					    	        	}
					        		}

					        		// 空行は無視する
						        	if(line == null || line.trim().length() <= 0){
						        		continue;
						        	}else{
						        		totalcount++;
						        		String [] rowData = line.split("\t", -1);

						    			//TODO:SQL使用禁止！
//					        			MultilangmesInfo multilangmesInfo;
						        		if(importType.equals(ImportAction.IMPORTTYPE_UPDATE)){
//						        			BeanMap condition = new BeanMap();
//						        			condition.put(multilangmesInfo().multilanginfoid().toString(), multilangInfo.id);
//						        			condition.put(multilangmesInfo().messageid().toString(), rowData[1]);

						        			String selectSql ="SELECT * FROM multilangmes_info WHERE multilanginfoid=";
						        			selectSql += multilangInfo.id;
						        			selectSql += " AND messageid = E'";
						        			selectSql += rowData[1].replaceAll("'", "''");
						        			selectSql += "'";
						        			SqlSelect<MultilangmesInfo> selectResult = jdbcManager.selectBySql(MultilangmesInfo.class, selectSql);
						        			List<MultilangmesInfo> multilangmesInfos = null;
						        			if(selectResult != null){
						        				multilangmesInfos = selectResult.getResultList();
						        			}

//						        			List<MultilangmesInfo> multilangmesInfos = multilangmesInfoService.findByCondition(condition);
						        			if(multilangmesInfos != null && multilangmesInfos.size() > 0){
//						        				multilangmesInfo = multilangmesInfos.get(0);
//							        			multilangmesInfo.message = rowData[2];
//							        			multilangmesInfoService.update(multilangmesInfo);

							        			String updateSql = "UPDATE multilangmes_info SET message = E'";
							        			updateSql += rowData[2].replaceAll("'", "''");
							        			updateSql += "'";
							        			updateSql += " WHERE multilanginfoid=";
							        			updateSql += multilangInfo.id;
							        			updateSql += " AND messageid =E'";
							        			updateSql += rowData[1].replaceAll("'", "''");
							        			updateSql += "'";
							        			jdbcManager.updateBySql(updateSql).execute();

							        			updatecount++;
						        			}else{
//							        			multilangmesInfo = new MultilangmesInfo();
//							        			multilangmesInfo.multilanginfoid = multilangInfo.id;
//							        			multilangmesInfo.messageid = rowData[1];
//							        			multilangmesInfo.message = rowData[2];
//							        			multilangmesInfoService.insert(multilangmesInfo);

							        			String insertSql = "INSERT INTO multilangmes_info (multilanginfoid, messageid, message) VALUES(";
							        			insertSql += multilangInfo.id;
							        			insertSql += ",";
							        			insertSql += "E'";
							        			insertSql += rowData[1].replaceAll("'", "''");
							        			insertSql += "'";
							        			insertSql += ",";
							        			insertSql += "E'";
							        			insertSql += rowData[2].replaceAll("'", "''");
							        			insertSql += "'";
							        			insertSql += ")";
							        			jdbcManager.updateBySql(insertSql).execute();

							        			insertcount++;
						        			}

						        		}else{
//						        			multilangmesInfo = new MultilangmesInfo();
//						        			multilangmesInfo.multilanginfoid = multilangInfo.id;
//						        			multilangmesInfo.messageid = rowData[1];
//						        			multilangmesInfo.message = rowData[2];
//						        			multilangmesInfoService.insert(multilangmesInfo);

						        			String insertSql = "INSERT INTO multilangmes_info (multilanginfoid, messageid, message) VALUES(";
						        			insertSql += multilangInfo.id;
						        			insertSql += ",";
						        			insertSql += "E'";
						        			insertSql += rowData[1].replaceAll("'", "''");
						        			insertSql += "'";
						        			insertSql += ",";
						        			insertSql += "E'";
						        			insertSql += rowData[2].replaceAll("'", "''");
						        			insertSql += "'";
						        			insertSql += ")";
						        			jdbcManager.updateBySql(insertSql).execute();

						        			insertcount++;
						        		}
						        	}
						        }
						        br.close();
						        result[0] = Integer.toString(totalcount);
						        result[1] = Integer.toString(updatecount);
						        result[2] = Integer.toString(insertcount);
							}catch(Exception e){
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to register data of {0} line. {1}", rowNum, e.getMessage()), false));
								e.printStackTrace();
								logger.error(e.getMessage(), e);
					        	userTransaction.setRollbackOnly();
							}finally{
								if(br != null){
									br.close();
								}
							}
				        }
					}
		        }


	        }catch(Exception e){
	        	userTransaction.setRollbackOnly();
	        } finally {
	        	if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
	        		// コミット
	        		userTransaction.commit();
	        	} else {
	        		// ロールバック
	        		userTransaction.rollback();
	        	}
	        }
		}catch(Exception e){

		}

		return errors;
	}
}
