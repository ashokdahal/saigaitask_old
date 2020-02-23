/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import static jp.ecom_plat.saigaitask.entity.Names.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.DisconnectImportConfirmDto;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.api.TrackdatasyncForm;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.ImporttrackInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import jp.ecom_plat.saigaitask.service.disconnect.ImportTrackdataService;
import jp.ecom_plat.saigaitask.service.disconnect.ImportTrackdataService.UploadedTracdatakInfo;
import jp.ecom_plat.saigaitask.service.disconnect.TrackDataExportService.ExportTrackFileSet;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import net.sf.json.JSONObject;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

/**
 * 災害データ受信・マージ確認API
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/trackdatasync")
@RequestMapping("/api/v2/trackdatasync")
public class TrackdatasyncAction extends AbstractApiAction {

	/** ユーザID */
	public Long id;

	@Resource protected GroupInfoService groupInfoService;
	@Resource protected UnitInfoService unitInfoService;
	@Resource protected UserInfoService userInfoService;

	@Resource protected TrackDataService trackDataService;
	@Resource protected ImporttrackInfoService importtrackInfoService;
	@Resource protected FileUploadService fileUploadService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected ImportTrackdataService importTrackdataService;

	public TrackdatasyncForm trackdatasyncForm;

	/**
	 * GET: Not Support
	 * POST: 災害データファイルを受信し、ローカルが持つ災害とのマッピング情報を返す
	 * PUT: Not Support
	 * PATCH: Not Support
	 * @return API結果 JSONObject
	 */
	@RequestMapping("/confirmtrackmapping")
	@ResponseBody
	public String confirmtrackmapping(Map<String,Object> model, @ModelAttribute TrackdatasyncForm trackdatasyncForm) {
		this.trackdatasyncForm = trackdatasyncForm;

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		JSONObject returnJsonObject = new JSONObject();
		if(isGetMethod()) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		else if(isPostMethod()) {
			// ログインしていなければ、登録できない
			if(apiDto.getGroupInfo()==null) {
				return Response.sendJSONError("No permission to regist UserInfo", HttpServletResponse.SC_FORBIDDEN).execute(response);
			}

			String contentType = request.getContentType();
			if(contentType.startsWith("multipart/form-data")) {
				// リクエストデータを取得
				try{
					// リクエストパラメータのチェック
					File tmpDir = FileUtil.getTmpDir();
					File uploadFile;
					File workDir;
					MultipartFile formFile = trackdatasyncForm.trackDatafile;
					if(formFile==null || formFile.getBytes().length==0) {
						if((!StringUtil.isEmpty(trackdatasyncForm.uploadTrackDataDirName)) && (!StringUtil.isEmpty(trackdatasyncForm.uploadTrackDataDirName))){
							String uploadTrackDataDirName = trackdatasyncForm.uploadTrackDataDirName;
							String uploadTrackDataFileName = trackdatasyncForm.uploadTrackDataFileName;
							workDir = new File(tmpDir, uploadTrackDataDirName);
							uploadFile = new File(workDir, uploadTrackDataFileName);
							if(!uploadFile.exists()){
								throw new ServiceException(lang.__("Disaster data is not found."));
							}
						}else{
							throw new ServiceException(lang.__("Disaster data file has not been specified."));
						}
					}else{
						String workDirName = "saigaitask-datasync-trackdata-" + apiDto.getGroupInfo().localgovinfoid + "-" + apiDto.getGroupInfo().id + "-" + UUID.randomUUID().toString();
						workDir = new File(tmpDir, workDirName);
						if(! workDir.exists()){
							workDir.mkdir();
						}

						// ファイルの保存
						HashSet<String> allowedExtent = new HashSet<String>(Arrays.asList(new String[]{"zip"}));
						String savedTrackDataFileName = fileUploadService.uploadFile(trackdatasyncForm.trackDatafile, workDir.getPath(), allowedExtent);
						if(savedTrackDataFileName==null) {
							throw new ServiceException(lang.__("Failed to expand zip file."));
						}
						uploadFile = new File(workDir, savedTrackDataFileName);

						// MD5ハッシュ値確認
						String nowHashValue = FileUtil.getMd5hash(uploadFile);
						String sendHashValue = trackdatasyncForm.trackDatafileHash;
						if(StringUtil.isEmpty(nowHashValue)){
							throw new ServiceException(lang.__("The hash value of the uploaded file could not be acquired. Please redo datasync operation."));
						}else if(StringUtil.isEmpty(sendHashValue)){
							throw new ServiceException(lang.__("The uploaded hash value is unjust. Please redo datasync operation."));
						}else if( ! nowHashValue.equals(sendHashValue)){
							throw new ServiceException(lang.__("The hash value of the uploaded file is unjust. Please redo datasync operation."));
						}
					}

					// ファイルの展開
					long localgovinfoid = apiDto.getGroupInfo().localgovinfoid;

					if(!uploadFile.exists()) throw new ServiceException(lang.__("Disaster data is not found."));
					ExportTrackFileSet exportTrackFileSet =  ExportTrackFileSet.unzip(uploadFile);


					// バージョンチェック
					try {
						if(! exportTrackFileSet.isValidVersion()){
							// 返却JSONデータを作成
							returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
							returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,lang.__("It can not be imported because it is different in the version of the NIED disaster information sharing system. {0},{1}",exportTrackFileSet.version,version));
						}
					} catch (IOException e) {
						returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
						returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,lang.__("An error occurred while importing disaster map and data.") + " : " + e.getMessage());
					}

			        if(returnJsonObject.isEmpty()){
						// 起動中の災害を取得
						List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
//						if(trackDatas == null || trackDatas.size() <= 0){
//							returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
//							returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,lang.__("An disaster has not been started."));
//						}else{
							// インポートファイルから災害情報のみ取得
					        try {
					            //ファイルを読み込む
					            FileReader fr = new FileReader(exportTrackFileSet.xslMapMappingFile);
					            BufferedReader br = new BufferedReader(fr);

								List<TrackData> fileTrackDataList = new ArrayList<TrackData>();

								//読み込んだファイルを１行ずつ処理する
					            String line;
					            List<String> importedOldTrackIdList = new ArrayList<String>();
					            List<String> importedNewTrackIdList = new ArrayList<String>();

					            // 災害が起動している場合のみ実行する
					            if (trackDatas.size() > 0) {
						            while ((line = br.readLine()) != null) {
						                //区切り文字","で分割する
						                String [] csvLine = line.split(",");
						                TrackData fileTrackData = new TrackData();
						                fileTrackData.id = Long.parseLong(csvLine[UploadedTracdatakInfo.idxTrackdataid]);
						                // 先頭のマスタマップ情報をスキップする
						                if (fileTrackData.id.longValue() == 0)
						                	continue;
						                fileTrackData.name = csvLine[UploadedTracdatakInfo.idxTrackDataName];
						                fileTrackDataList.add(fileTrackData);


										// 初回インポート済みかチェックする
										BeanMap conditions = new BeanMap();
										conditions.put(importtrackInfo().localgovinfoid().toString(), localgovinfoid);
										conditions.put(importtrackInfo().oldlocalgovinfoid().toString(), Long.parseLong(csvLine[UploadedTracdatakInfo.idxLocalgovinfoid]));
										conditions.put(importtrackInfo().oldtrackdataid().toString(), Long.parseLong(csvLine[UploadedTracdatakInfo.idxTrackdataid]));
										List<ImporttrackInfo> importtrackInfoList = importtrackInfoService.findByCondition(conditions);
										// 初回インポート済みの場合は、チェックボックスをONにしておく
										if(importtrackInfoList != null && importtrackInfoList.size() > 0){
											ImporttrackInfo importtrackInfo = importtrackInfoList.get(0);
											importedOldTrackIdList.add(String.valueOf(importtrackInfo.oldtrackdataid));
											importedNewTrackIdList.add(String.valueOf(importtrackInfo.oldtrackdataid) + "," + String.valueOf(importtrackInfo.trackdataid));
										}
						            }
					            }
					            br.close();
								// 返却JSONデータを作成
								returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSSUCCESS);
								List<JSONObject> targetTrackDataListJson = new ArrayList<JSONObject>();
								List<JSONObject> fileTrackDataListJson = new ArrayList<JSONObject>();
								List<JSONObject> importedOldTrackIdListJson = new ArrayList<JSONObject>();
								List<JSONObject> importedNewTrackIdListJson = new ArrayList<JSONObject>();
								returnJsonObject.put("uploadTrackDataFileName", uploadFile.getName());
								returnJsonObject.put("uploadTrackDataDirName", workDir.getName());

								for(TrackData targetTrackData : trackDatas){
									JSONObject targetTrackDataJson = new JSONObject();
									targetTrackDataJson.put("id", targetTrackData.id);
									targetTrackDataJson.put("name", targetTrackData.name);
									targetTrackDataListJson.add(targetTrackDataJson);
								}
								returnJsonObject.put("targetTrackDataListJson", targetTrackDataListJson);

								for(TrackData  fileTrackData :fileTrackDataList){
									JSONObject fileTrackDataJson = new JSONObject();
									fileTrackDataJson.put("id", fileTrackData.id);
									fileTrackDataJson.put("name", fileTrackData.name);
									fileTrackDataListJson.add(fileTrackDataJson);

								}
								returnJsonObject.put("fileTrackDataListJson", fileTrackDataListJson);


								if(importedOldTrackIdList.size() > 0){
									for(String value : importedOldTrackIdList){
										JSONObject json = new JSONObject();
										json.put("value", value);
										importedOldTrackIdListJson.add(json);
									}
								}
								if(importedNewTrackIdList.size() > 0){
									for(String value : importedNewTrackIdList){
										JSONObject json = new JSONObject();
										json.put("value", value);
										importedNewTrackIdListJson.add(json);
									}
								}
								returnJsonObject.put("importedOldTrackIdListJson", importedOldTrackIdListJson);
								returnJsonObject.put("importedNewTrackIdListJson", importedNewTrackIdListJson);
					        } catch (IOException e) {
								e.printStackTrace();
					        	throw e;
					        }
//						}
			        }

				}catch(ServiceException se){
					returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
					returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,se.getMessage());
				}catch(IOException e){
					returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
					returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,lang.__("An error occurred while importing disaster map and data.") + " : " + e.getMessage());
				}

				return responseJSONObject(returnJsonObject);
			}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		}
		//else if(isDeleteMethod()) { }
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}

//	/**
//	 * GET: Not Support
//	 * POST: 災害データファイルを受信し、ローカルが持つ災害との地物の比較結果を返す
//	 * PUT: Not Support
//	 * PATCH: Not Support
//	 * @return API結果 JSONObject
//	 */
//	public String confirmdatasync() {
//
//		JSONObject returnJsonObject = new JSONObject();
//		if(isGetMethod()) {
//			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//		}
//		else if(isPostMethod()) {
//			// ログインしていなければ、登録できない
//			if(apiDto.getGroupInfo()==null) {
//				return Response.sendJSONError("No permission to regist UserInfo", HttpServletResponse.SC_FORBIDDEN).execute(response);
//			}
//
//			String contentType = request.getContentType();
//			if(contentType.startsWith("multipart/form-data")) {
//				// リクエストデータを取得
//				try{
//					// リクエストパラメータのチェック
//					String uploadTrackDataDirName = trackdatasyncForm.uploadTrackDataDirName;
//					String uploadTrackDataFileName = trackdatasyncForm.uploadTrackDataFileName;
//					// 災害マッピング情報の復元
//		        	Map<Long,Long> trackMapimngMap = new HashMap<Long, Long>();
//					String trackMapText = trackdatasyncForm.trackMapText;
//					String [] trackMapTextArray = trackMapText.split(",");
//					for(int i = 0 ; i < trackMapTextArray.length; i++){
//						String [] trackMapTextArray2 = trackMapTextArray[i].split("-");
//						trackMapimngMap.put(Long.parseLong(trackMapTextArray2[0]), Long.parseLong(trackMapTextArray2[1]));
//
//					}
//
//					// 受信ファイルの存在確認
//					File tmpDir = FileUtil.getTmpDir();
//					File workDir = new File(tmpDir, uploadTrackDataDirName);
//					File uploadFile = new File(workDir, uploadTrackDataFileName);
//					if(!uploadFile.exists()){
//						throw new ServiceException(lang.__("Disaster data is not found."));
//					}
//
//					// ファイルの展開
//					long localgovinfoid = apiDto.getGroupInfo().localgovinfoid;
//
//					if(!uploadFile.exists()) throw new ServiceException(lang.__("Disaster data is not found."));
//					ExportTrackFileSet exportTrackFileSet =  ExportTrackFileSet.unzip(uploadFile);
//
//					// 起動中の災害を取得
//					List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
//					if(trackDatas == null || trackDatas.size() <= 0){
//					}else{
//				        // 災害データの更新チェックを実施
//						LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
//						// 自治体の管理者グループを取得
//						GroupInfo groupInfo = groupInfoService.findByLocalgovInfoIdAndAdmin(localgovinfoid);
//
//						// 地図マスター情報からデータ取得
//						MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovInfo.id);
//
//						// 災害マップと災害データのインポート
//						try {
//							MapDB mapDB = MapDB.getMapDB();
//							//出力先サイトID
//							CommunityInfo communityInfo = mapDB.getCommunityInfo(mapmasterInfo.communityid);
//							if(communityInfo==null) throw new ServiceException(lang.__("Site ID = {0} not exist.", mapmasterInfo.communityid));
//							//復元先グループ
//							jp.ecom_plat.map.db.GroupInfo ecommGroupInfo = jp.ecom_plat.map.db.GroupInfo.getGroupInfo(mapmasterInfo.mapgroupid);
//							DisconnectImportConfirmDto disconnectImportConfirmDto = importTrackdataService.execute(
//									false,
//									trackMapimngMap,
//									null,
//									null,
//									null,
//									exportTrackFileSet,
//									localgovInfo.id,
//									groupInfo.ecomuser,
//									communityInfo,
//									ecommGroupInfo,
//									mapmasterInfo);
//
//							// 返却JSONデータを作成
//							returnJsonObject.put("status","success");
//							List<JSONObject> trackJsonObjectList = new ArrayList<JSONObject>();
//							for(DisconnectImportConfirmDto.ImportTrackData importTrackData : disconnectImportConfirmDto.importTrackDataList){
//
//								JSONObject trackJsonObject = new JSONObject();
//
//								List<JSONObject> baseJsonObjectList = new ArrayList<JSONObject>();
//								JSONObject baseJsonObject = new JSONObject();
//								baseJsonObject.put("fileTrackDataId",importTrackData.fileTrackDataId);
//								baseJsonObject.put("fileTrackDataName",importTrackData.fileTrackDataName);
//								baseJsonObject.put("dbTrackDataId",importTrackData.dbTrackDataId);
//								baseJsonObject.put("dbTrackDataName",importTrackData.dbTrackDataName);
//								baseJsonObject.put("fileMapId",importTrackData.fileMapId);
//								baseJsonObject.put("dbMapId",importTrackData.dbMapId);
//								baseJsonObject.put("trackDataName",importTrackData.trackDataName);
//								baseJsonObject.put("isLayerInfoVisiblel",importTrackData.isLayerInfoVisiblel);
//								baseJsonObject.put("isTableInfoVisiblel",importTrackData.isTableInfoVisiblel);
//								baseJsonObject.put("isUpdate",importTrackData.isUpdate);
//								baseJsonObjectList.add(baseJsonObject);
//								trackJsonObject.put("baseJsonObjectList", baseJsonObjectList);
//
//								// マージ確認が必要な地物のリストを作成
//								List<JSONObject> layerJsonObjectList = new ArrayList<JSONObject>();
//								for(DisconnectImportConfirmDto.ImportLayerInfo importLayerInfo : importTrackData.importLayerList){
//									JSONObject layerJsonObject = new JSONObject();
//
//									List<JSONObject> attrNamesListJsonObjectList = new ArrayList<JSONObject>();
//									for(String attrName : importLayerInfo.attrNamesList){
//										JSONObject attrNamesListJsonObject = new JSONObject();
//										attrNamesListJsonObject.put("value", attrName);
//										attrNamesListJsonObjectList.add(attrNamesListJsonObject);
//									}
//									layerJsonObject.put("attrNamesListJsonObjectList", attrNamesListJsonObjectList);
//
//									layerJsonObject.put("attrNamesList", importLayerInfo.attrNamesList);
//									layerJsonObject.put("layerId", importLayerInfo.layerId);
//									layerJsonObject.put("layerName", importLayerInfo.layerName);
//									layerJsonObject.put("unmatchCount", importLayerInfo.unmatchCount);
//
//									List<JSONObject> featureJsonObjectList = new ArrayList<JSONObject>();
//									for(DisconnectImportConfirmDto.UnmatchFeatureInfo unmatchFeatureInfo : importLayerInfo.unmatchFeatureInfoList){
//										JSONObject featureJsonObject = new JSONObject();
//										featureJsonObject.put("gid", unmatchFeatureInfo.gid);
//										if(StringUtil.isEmpty(unmatchFeatureInfo.dbGroupId)){
//											featureJsonObject.put("dbGroupId", "");
//										}else{
//											featureJsonObject.put("dbGroupId", unmatchFeatureInfo.dbGroupId);
//										}
//										if(StringUtil.isEmpty(unmatchFeatureInfo.dbGroupName)){
//											featureJsonObject.put("dbGroupName", "");
//										}else{
//											featureJsonObject.put("dbGroupName", unmatchFeatureInfo.dbGroupName);
//										}
//										if(StringUtil.isEmpty(unmatchFeatureInfo.fileGroupId)){
//											featureJsonObject.put("fileGroupId", "");
//										}else{
//											featureJsonObject.put("fileGroupId", unmatchFeatureInfo.fileGroupId);
//										}
//										if(StringUtil.isEmpty(unmatchFeatureInfo.fileGroupName)){
//											featureJsonObject.put("fileGroupName", "");
//										}else{
//											featureJsonObject.put("fileGroupName", unmatchFeatureInfo.fileGroupName);
//										}
//
//										featureJsonObject.put("checkBoxValue", unmatchFeatureInfo.checkBoxValue);
//
//										List<JSONObject> dbAttrsJsonObjectList = new ArrayList<JSONObject>();
//										for(String dbAttr : unmatchFeatureInfo.dbAttrs){
//											JSONObject dbAttrJsonObject = new JSONObject();
//											dbAttrJsonObject.put("value", dbAttr);
//											dbAttrsJsonObjectList.add(dbAttrJsonObject);
//										}
//										featureJsonObject.put("dbAttrsJsonObjectList", dbAttrsJsonObjectList);
//
//										List<JSONObject> fileAttrsJsonObjectList = new ArrayList<JSONObject>();
//										for(DisconnectImportConfirmDto.FileAttrInfo fileAttr : unmatchFeatureInfo.fileAttrs){
//											JSONObject fileAttrJsonObject = new JSONObject();
//											fileAttrJsonObject.put("value", fileAttr.value);
//											fileAttrJsonObject.put("modified", fileAttr.modified);
//											fileAttrsJsonObjectList.add(fileAttrJsonObject);
//										}
//										featureJsonObject.put("fileAttrsJsonObjectList", fileAttrsJsonObjectList);
//
//										featureJsonObjectList.add(featureJsonObject);
//									}
//									layerJsonObject.put("featureJsonObjectList", featureJsonObjectList);
//									layerJsonObjectList.add(layerJsonObject);
//								}
//								trackJsonObject.put("layerJsonObjectList", layerJsonObjectList);
//
//
//								// マージ確認が必要なデータのリストを作成
//								List<JSONObject> tracktabledataJsonObjectList = new ArrayList<JSONObject>();
//								for(DisconnectImportConfirmDto.ImportTrackTableData importTrackTableData : importTrackData.importTrackTableDataList){
//									JSONObject tracktabledataJsonObject = new JSONObject();
//
//									List<JSONObject> columnComentListJsonObjectList = new ArrayList<JSONObject>();
//									for(String  columnComment: importTrackTableData.columnCommentList){
//										JSONObject columnCommentListJsonObject = new JSONObject();
//										columnCommentListJsonObject.put("value", columnComment);
//										columnComentListJsonObjectList.add(columnCommentListJsonObject);
//									}
//									tracktabledataJsonObject.put("columnComentListJsonObjectList", columnComentListJsonObjectList);
//									tracktabledataJsonObject.put("columnCommentList", importTrackTableData.columnCommentList);
//									tracktabledataJsonObject.put("tableName", importTrackTableData.tableName);
//									tracktabledataJsonObject.put("tableComment", importTrackTableData.tableComment);
//
//									List<JSONObject> samerecordJsonObjectList = new ArrayList<JSONObject>();
//									for(DisconnectImportConfirmDto.SameTrackTableDataRecord sameTrackTableDataRecord : importTrackTableData.sameTrackTableDataRecordList){
//										JSONObject samerecordJsonObject = new JSONObject();
//										if(StringUtil.isEmpty(sameTrackTableDataRecord.dbGroupId)){
//											samerecordJsonObject.put("dbGroupId", "");
//										}else{
//											samerecordJsonObject.put("dbGroupId", sameTrackTableDataRecord.dbGroupId);
//										}
//										if(StringUtil.isEmpty(sameTrackTableDataRecord.dbGroupName)){
//											samerecordJsonObject.put("dbGroupName", "");
//										}else{
//											samerecordJsonObject.put("dbGroupName", sameTrackTableDataRecord.dbGroupName);
//										}
//										if(StringUtil.isEmpty(sameTrackTableDataRecord.fileGroupId)){
//											samerecordJsonObject.put("fileGroupId", "");
//										}else{
//											samerecordJsonObject.put("fileGroupId", sameTrackTableDataRecord.fileGroupId);
//										}
//										if(StringUtil.isEmpty(sameTrackTableDataRecord.fileGroupName)){
//											samerecordJsonObject.put("fileGroupName", "");
//										}else{
//											samerecordJsonObject.put("fileGroupName", sameTrackTableDataRecord.fileGroupName);
//										}
//
//										samerecordJsonObject.put("checkBoxValue", sameTrackTableDataRecord.checkBoxValue);
//
//										List<JSONObject> dbRecordJsonObjectList = new ArrayList<JSONObject>();
//										for(String dbRecord : sameTrackTableDataRecord.dbRecord){
//											JSONObject dbRecordJsonObject = new JSONObject();
//											dbRecordJsonObject.put("value", dbRecord);
//											dbRecordJsonObjectList.add(dbRecordJsonObject);
//										}
//										samerecordJsonObject.put("dbRecordJsonObjectList", dbRecordJsonObjectList);
//
//										List<JSONObject> fileRecordJsonObjectList = new ArrayList<JSONObject>();
//										for(String fileRecord : sameTrackTableDataRecord.fileRecord){
//											JSONObject fileRecordJsonObject = new JSONObject();
//											fileRecordJsonObject.put("value", fileRecord);
//											fileRecordJsonObjectList.add(fileRecordJsonObject);
//										}
//										samerecordJsonObject.put("fileRecordJsonObjectList", fileRecordJsonObjectList);
//
//										samerecordJsonObjectList.add(samerecordJsonObject);
//									}
//									tracktabledataJsonObject.put("samerecordJsonObjectList", samerecordJsonObjectList);
//									tracktabledataJsonObjectList.add(tracktabledataJsonObject);
//								}
//								trackJsonObject.put("tracktabledataJsonObjectList", tracktabledataJsonObjectList);
//
//								trackJsonObjectList.add(trackJsonObject);
//							}
//							returnJsonObject.put("trackJsonObjectList", trackJsonObjectList);
//							returnJsonObject.put("uploadTrackDataFileName", uploadFile.getName());
//							returnJsonObject.put("uploadTrackDataDirName", workDir.getName());
//						} catch (Exception e) {
//							e.printStackTrace();
//							throw new ServiceException(lang.__("An error occurred while importing disaster map and data.") + " : " + e.getMessage());
//						}
//					}
//				}catch(ServiceException se){
//					returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
//					returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,se.getMessage());
//				}catch(IOException e){
//					returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
//					returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE, lang.__("An error occurred while importing disaster map and data.") + " : " + e.getMessage());
//				}
//				return responseJSONObject(returnJsonObject);
//			}
//			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
//		}
//		//else if(isDeleteMethod()) { }
//		else {
//			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//		}
//
//		return null;
//	}

	/**
	 * GET: Not Support
	 * POST: 災害データファイルを受信し、ローカルの災害とのマージを実行する
	 * PUT: Not Support
	 * PATCH: Not Support
	 * @return API結果 JSONObject
	 */
	@RequestMapping("/execdatasync")
	@ResponseBody
	public String execdatasync(Map<String,Object> model, @ModelAttribute TrackdatasyncForm trackdatasyncForm) {
		this.trackdatasyncForm = trackdatasyncForm;

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}

		setupModel(model);

		JSONObject returnJsonObject = new JSONObject();
		if(isGetMethod()) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		else if(isPostMethod()) {
			// ログインしていなければ、登録できない
			if(apiDto.getGroupInfo()==null) {
				return Response.sendJSONError("No permission to regist UserInfo", HttpServletResponse.SC_FORBIDDEN).execute(response);
			}

			String contentType = request.getContentType();
			if(contentType.startsWith("multipart/form-data")) {
				// リクエストデータを取得
				try{
					// リクエストパラメータのチェック
					String updateFeaturesStr = trackdatasyncForm.updateFeatures;
					String [] updateFeatures;
					if(updateFeaturesStr != null && updateFeaturesStr.length() > 0){
						updateFeatures = updateFeaturesStr.split(",");
					}else{
						updateFeatures = null;
					}

					String updateTrackTableDatasStr = trackdatasyncForm.updateTrackTableDatas;
					String [] updateTrackTableDatas;
					if(updateTrackTableDatasStr != null && updateTrackTableDatasStr.length() > 0){
						updateTrackTableDatas = updateTrackTableDatasStr.split(",");
					}else{
						updateTrackTableDatas = null;
					}

					String uploadTrackDataDirName = trackdatasyncForm.uploadTrackDataDirName;
					String uploadTrackDataFileName = trackdatasyncForm.uploadTrackDataFileName;
					boolean syncAll = "1".equals(trackdatasyncForm.syncAll);
					boolean syncAttachedFile = "1".equals(trackdatasyncForm.syncAttachedFile);

					// 災害マッピング情報の復元
		        	Map<Long,Long> trackMapimngMap = new HashMap<Long, Long>();
					String trackMapText = trackdatasyncForm.trackMapText;
					if (trackMapText.length() > 0) {
						String [] trackMapTextArray = trackMapText.split(",");
						for(int i = 0 ; i < trackMapTextArray.length; i++){
							String [] trackMapTextArray2 = trackMapTextArray[i].split("-");
							trackMapimngMap.put(Long.parseLong(trackMapTextArray2[0]), Long.parseLong(trackMapTextArray2[1]));

						}
					}

					// 受信ファイルの存在確認
					File tmpDir = FileUtil.getTmpDir();
					File workDir = new File(tmpDir, uploadTrackDataDirName);
					File uploadFile = new File(workDir, uploadTrackDataFileName);
					if(!uploadFile.exists()){
						throw new ServiceException(lang.__("Disaster data is not found."));
					}

					// ファイルの展開
					long localgovinfoid = apiDto.getGroupInfo().localgovinfoid;
					ExportTrackFileSet exportTrackFileSet =  ExportTrackFileSet.unzip(uploadFile);

					// 起動中の災害を取得
					List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
//					if(trackDatas == null || trackDatas.size() <= 0){
//					}else{
				        // 災害データの更新チェックを実施
						LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
						// 自治体の管理者グループを取得
						GroupInfo groupInfo = groupInfoService.findByLocalgovInfoIdAndAdmin(localgovinfoid);

						// 地図マスター情報からデータ取得
						MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovInfo.id);

						// 災害マップと災害データのインポート
						try {
							MapDB mapDB = MapDB.getMapDB();
							//出力先サイトID
							CommunityInfo communityInfo = mapDB.getCommunityInfo(mapmasterInfo.communityid);
							if(communityInfo==null) throw new ServiceException(lang.__("Site ID = {0} not exist.", mapmasterInfo.communityid));
							//復元先グループ
							jp.ecom_plat.map.db.GroupInfo ecommGroupInfo = jp.ecom_plat.map.db.GroupInfo.getGroupInfo(mapmasterInfo.mapgroupid);
							DisconnectImportConfirmDto disconnectImportConfirmDto = importTrackdataService.execute(
									true,
									trackMapimngMap,
									updateFeatures,
									updateTrackTableDatas,
									trackdatasyncForm.updateTrackTableDatasHidden,
									exportTrackFileSet,
									localgovInfo.id,
									groupInfo.ecomuser,
									communityInfo,
									ecommGroupInfo,
									mapmasterInfo,
									syncAll,
									syncAttachedFile);

							// 返却JSONデータを作成
							List<JSONObject> trackJsonObjectList = new ArrayList<JSONObject>();
							returnJsonObject.put("status","success");
							for(DisconnectImportConfirmDto.ImportTrackData importTrackData : disconnectImportConfirmDto.importTrackDataList){

								JSONObject trackJsonObject = new JSONObject();
								List<JSONObject> baseJsonObjectList = new ArrayList<JSONObject>();
								JSONObject baseJsonObject = new JSONObject();
								baseJsonObject.put("fileTrackDataId",importTrackData.fileTrackDataId);
								baseJsonObject.put("fileTrackDataName",importTrackData.fileTrackDataName);
								baseJsonObject.put("dbTrackDataId",importTrackData.dbTrackDataId);
								baseJsonObject.put("dbTrackDataName",importTrackData.dbTrackDataName);
								baseJsonObject.put("fileMapId",importTrackData.fileMapId);
								baseJsonObject.put("dbMapId",importTrackData.dbMapId);
								baseJsonObject.put("trackDataName",importTrackData.trackDataName);
								baseJsonObject.put("isLayerInfoVisiblel",importTrackData.isLayerInfoVisiblel);
								baseJsonObject.put("isTableInfoVisiblel",importTrackData.isTableInfoVisiblel);
								baseJsonObject.put("isUpdate",importTrackData.isUpdate);
								baseJsonObjectList.add(baseJsonObject);
								trackJsonObject.put("baseJsonObjectList", baseJsonObjectList);

								// マージ確認が必要な地物のリストを作成
								List<JSONObject> layerJsonObjectList = new ArrayList<JSONObject>();
								for(DisconnectImportConfirmDto.ImportLayerInfo importLayerInfo : importTrackData.importLayerList){
									JSONObject layerJsonObject = new JSONObject();

									List<JSONObject> attrNamesListJsonObjectList = new ArrayList<JSONObject>();
									for(String attrName : importLayerInfo.attrNamesList){
										JSONObject attrNamesListJsonObject = new JSONObject();
										attrNamesListJsonObject.put("value", attrName);
										attrNamesListJsonObjectList.add(attrNamesListJsonObject);
									}
									layerJsonObject.put("attrNamesListJsonObjectList", attrNamesListJsonObjectList);

									layerJsonObject.put("attrNamesList", importLayerInfo.attrNamesList);
									layerJsonObject.put("layerId", importLayerInfo.layerId);
									layerJsonObject.put("layerName", importLayerInfo.layerName);
									layerJsonObject.put("unmatchCount", importLayerInfo.unmatchCount);

									List<JSONObject> featureJsonObjectList = new ArrayList<JSONObject>();
									for(DisconnectImportConfirmDto.UnmatchFeatureInfo unmatchFeatureInfo : importLayerInfo.unmatchFeatureInfoList){
										JSONObject featureJsonObject = new JSONObject();
										featureJsonObject.put("gid", unmatchFeatureInfo.gid);
										featureJsonObject.put("dbGroupId", unmatchFeatureInfo.dbGroupId);
										featureJsonObject.put("dbGroupName", unmatchFeatureInfo.dbGroupName);
										featureJsonObject.put("fileGroupId", unmatchFeatureInfo.fileGroupId);
										featureJsonObject.put("fileGroupName", unmatchFeatureInfo.fileGroupName);
										featureJsonObject.put("checkBoxValue", unmatchFeatureInfo.checkBoxValue);

										List<JSONObject> dbAttrsJsonObjectList = new ArrayList<JSONObject>();
										for(String dbAttr : unmatchFeatureInfo.dbAttrs){
											JSONObject dbAttrJsonObject = new JSONObject();
											dbAttrJsonObject.put("value", dbAttr);
											dbAttrsJsonObjectList.add(dbAttrJsonObject);
										}
										featureJsonObject.put("dbAttrsJsonObjectList", dbAttrsJsonObjectList);

										List<JSONObject> fileAttrsJsonObjectList = new ArrayList<JSONObject>();
										for(DisconnectImportConfirmDto.FileAttrInfo fileAttr : unmatchFeatureInfo.fileAttrs){
											JSONObject fileAttrJsonObject = new JSONObject();
											fileAttrJsonObject.put("value", fileAttr.value);
											fileAttrJsonObject.put("modified", fileAttr.modified);
											fileAttrsJsonObjectList.add(fileAttrJsonObject);
										}
										featureJsonObject.put("fileAttrsJsonObjectList", fileAttrsJsonObjectList);

										featureJsonObjectList.add(featureJsonObject);
									}
									layerJsonObject.put("featureJsonObjectList", featureJsonObjectList);
									layerJsonObjectList.add(layerJsonObject);
								}
								trackJsonObject.put("layerJsonObjectList", layerJsonObjectList);
								trackJsonObjectList.add(trackJsonObject);
							}
							returnJsonObject.put("trackJsonObjectList", trackJsonObjectList);
							returnJsonObject.put("uploadTrackDataFileName", uploadFile.getName());
							returnJsonObject.put("uploadTrackDataDirName", workDir.getName());

							if(workDir != null && workDir.exists()){
								FileUtil.dirDelete(workDir);
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new ServiceException(lang.__("An error occurred while importing disaster map and data.")+ " : " + e.getMessage());
						}
//					}
				}catch(ServiceException se){
					returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
					returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,se.getMessage());
				}catch(IOException e){
					returnJsonObject.put(Constants.JSON_KEY_STATUS,Constants.JSON_KEY_STATUSERROR);
					returnJsonObject.put(Constants.JSON_KEY_ERRORMESSAGE,lang.__("An error occurred while importing disaster map and data.") + " : " + e.getMessage());
				}

				return responseJSONObject(returnJsonObject);
			}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		}
		//else if(isDeleteMethod()) { }
		else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		return null;
	}
}
