/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import static jp.ecom_plat.saigaitask.entity.Names.importtrackInfo;

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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.DisconnectImportConfirmDto;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.admin.disconnect.InitImportExportForm;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.ImporttrackInfoService;
import jp.ecom_plat.saigaitask.service.db.LocalgovtypeMasterService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.disconnect.ImportTrackdataService;
import jp.ecom_plat.saigaitask.service.disconnect.ImportTrackdataService.UploadedTracdatakInfo;
import jp.ecom_plat.saigaitask.service.disconnect.TrackDataExportService.ExportTrackFileSet;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/disconnect/initImportExport")
public class InitImportExportAction extends AbstractDisconnectAction {

	public InitImportExportForm initImportExportForm;


	// Service
	@Resource protected LocalgovtypeMasterService localgovtypeMasterService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected FileUploadService fileUploadService;
	@Resource protected FileService fileService;
	@Resource protected ImportTrackdataService importTrackdataService;
	@Resource protected ImporttrackInfoService importtrackInfoService;
	@Resource protected TrackDataService trackDataService;
	@Resource protected TableService tableService;


	public DisconnectImportConfirmDto disconnectImportConfirmDto;

	public String ecommapURL;

	public Long localgovinfoid;

	public Integer importmode;

	public String uploadTrackDataDirName;
	public String uploadTrackDataFileName;
	public String trackMapText;
	public String hasError;
	public String updateTrackTableDatasHidden;
	public String endMessage;

	protected Logger getLogger() {
		return Logger.getLogger(InitImportExportAction.class);
	}

	// JSPへの定数返却
	public String constantDisconnectImportmodeTrack = Constants.DISCONNECT_IMPORTMODE_TRACK;
	public String constantDisconnectImportmodeInfoAndTrack = Constants.DISCONNECT_IMPORTMODE_INFOANDTRACK;
	public String constantDisconnectImportstatusStep1 = Constants.DISCONNECT_IMPORTSTATUS_STEP1;
	public String constantDisconnectImportstatusStep2 = Constants.DISCONNECT_IMPORTSTATUS_STEP2;
	public String constantDisconnectImportstatusStep3 = Constants.DISCONNECT_IMPORTSTATUS_STEP3;

	public void setupModel(Map<String,Object> model) {
		super.setupModel(model);
		model.put("disconnectImportConfirmDto", disconnectImportConfirmDto);
		model.put("uploadTrackDataDirName", uploadTrackDataDirName);
		model.put("uploadTrackDataFileName", uploadTrackDataFileName);
		model.put("hasError", hasError);
		model.put("constantDisconnectImportmodeTrack", constantDisconnectImportmodeTrack);
		model.put("constantDisconnectImportmodeInfoAndTrack", constantDisconnectImportmodeInfoAndTrack);
		model.put("constantDisconnectImportstatusStep1", constantDisconnectImportstatusStep1);
		model.put("constantDisconnectImportstatusStep2", constantDisconnectImportstatusStep2);
		model.put("constantDisconnectImportstatusStep3", constantDisconnectImportstatusStep3);
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @ModelAttribute InitImportExportForm initImportExportForm) {
		this.initImportExportForm = initImportExportForm;

		// セットアッパーの初期化
		initDisconnect();

		// 表示内容の取得
		content(model, initImportExportForm);
//		// OAuth の初期化
//		oAuthInit();

		return "/admin/disconnect/initImportExport/index";
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/content")
	public String content(Map<String,Object>model, @ModelAttribute InitImportExportForm initImportExportForm) {
		this.initImportExportForm = initImportExportForm;
		hasError = null;
		ecommapURL = Config.getEcommapURL();

		// 新規作成の場合
		if(loginDataDto.getLocalgovinfoid()==0) {
			// 自治体セレクトボックスの作成
			createLocalgovSelectOptions();
		}
		// 編集の場合
		else {
			// 自治体情報
			LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());

			Beans.copy(localgovInfo, initImportExportForm).execute();
			// マスタマップ
		}

		setupModel(model);

		return "/admin/disconnect/initImportExport/content";
	}


	/**
	 * 登録確認／実行
	 *
	 * @return 遷移ページ
	 */
	@RequestMapping(value="/importTrackdata")
	public String importTrackdata(Map<String,Object> model, @ModelAttribute InitImportExportForm initImportExportForm, BindingResult bindingResult) {
		this.initImportExportForm = initImportExportForm;

		// CSRF対策
		if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
		};

//		Boolean doExec;
		String [] updateFeatures = initImportExportForm.updateFeatures;
		String [] updateTrackTableDatas = initImportExportForm.updateTrackTableDatas;
		updateTrackTableDatasHidden = initImportExportForm.updateTrackTableDatasHidden;
		boolean importAll = "1".equals(initImportExportForm.importAll);
		boolean importAttachedFile = "1".equals(initImportExportForm.importAttachedFile);

		importmode = initImportExportForm.importmode;
		if(importmode > 0){
			LocalgovInfo localgovInfo = null;
			GroupInfo groupInfo = null;
			long nowLocalgovinfoid;

			ActionMessages errors = new ActionMessages();
			File workDir = null;
			String workDirName = null;
			File tmpDir = null;
			File templateZipFile = null;
			ExportTrackFileSet exportTrackFileSet =  null;
			Map<Long, Long> trackMap = new HashMap<Long, Long>();
			try{
				switch (initImportExportForm.importExecStatus) {
				case Constants.DISCONNECT_IMPORTSTATUS_STEP1:
//					doExec = false;

					// 自治体情報の取得
					// システム管理者の場合は画面のセレクトボックスの指定値を使用
					if(loginDataDto.getLocalgovinfoid()==0) {
						nowLocalgovinfoid = Long.parseLong(initImportExportForm.selectLocalgov);
						localgovinfoid = nowLocalgovinfoid;
					}else{
						nowLocalgovinfoid = loginDataDto.getLocalgovinfoid();
						localgovinfoid = nowLocalgovinfoid;
					}

					// ファイルを展開して情報取得
					tmpDir = FileUtil.getTmpDir();
					workDirName = "saigaitask-import-trackdata-" + loginDataDto.getLocalgovinfoid() + "-" + loginDataDto.getGroupid() + "-" + UUID.randomUUID().toString();
					workDir = new File(tmpDir, workDirName);
					if(! workDir.exists()){
						workDir.mkdir();
					}
					uploadTrackDataDirName = workDir.getName();

					// リクエストパラメータのチェック
					MultipartFile formFile = initImportExportForm.trackDataFile;
					if(formFile==null || formFile.getBytes().length==0) {
						throw new ServiceException(lang.__("Disaster data file has not been specified."));
					}

					// ファイルの展開
					HashSet<String> allowedExtent = new HashSet<String>(Arrays.asList(new String[]{"zip"}));
					String savedTrackDataFileName = fileUploadService.uploadFile(initImportExportForm.trackDataFile, workDir.getPath(), allowedExtent);
					if(savedTrackDataFileName==null) {
						throw new ServiceException(lang.__("Failed to expand zip file."));
					}
					templateZipFile = new File(workDir, savedTrackDataFileName);
					if(!templateZipFile.exists()) throw new ServiceException(lang.__("Disaster data is not found."));
					exportTrackFileSet =  ExportTrackFileSet.unzip(templateZipFile);

					uploadTrackDataFileName = savedTrackDataFileName;

					// バージョンチェック
					try {
						if(! exportTrackFileSet.isValidVersion()){
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("It can not be imported because it is different in the version of the NIED disaster information sharing system. {0},{1}",exportTrackFileSet.version,version), false));
						}
					} catch (IOException e) {
						throw new ServiceException(lang.__("An error occurred while importing disaster data."),e);
					}

			        if(errors.isEmpty()){
						// 起動中の災害を取得
						List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
//						if(trackDatas == null || trackDatas.size() <= 0){
//							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("An disaster has not been started."), false));
//						}
						disconnectImportConfirmDto = new DisconnectImportConfirmDto();
						disconnectImportConfirmDto.targetTrackDataList = trackDatas;

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
									conditions.put(importtrackInfo().oldtrackdataid().toString(), null/*Long.parseLong(csvLine[UploadedTracdatakInfo.idxTrackdataid])*/);
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
							disconnectImportConfirmDto.fileTrackDataList = fileTrackDataList;
							if(importedOldTrackIdList.size() > 0){
								initImportExportForm.selectFileTrackMultibox = (String[])importedOldTrackIdList.toArray(new String[0]);
							}
							if(importedNewTrackIdList.size() > 0){
								initImportExportForm.selectDbTrackMultibox = (String[])importedNewTrackIdList.toArray(new String[0]);
							}
				        } catch (IOException e) {
							throw new ServiceException(lang.__("An error occurred while importing disaster data."),e);
				        }
			        }


					if(! errors.isEmpty()){
						hasError = "error";
						ActionMessagesUtil.addErrors(bindingResult, errors);
					}

					setupModel(model);

					// STEP1の場合はここで終了
					return "/admin/disconnect/initImportExport/trackselect";
				case Constants.DISCONNECT_IMPORTSTATUS_STEP2:
//					doExec = false;
					uploadTrackDataDirName = initImportExportForm.uploadTrackDataDirName;
					uploadTrackDataFileName = initImportExportForm.uploadTrackDataFileName;

					// 自治体情報の取得
					// システム管理者の場合は画面のセレクトボックスの指定値を使用
					if(loginDataDto.getLocalgovinfoid()==0) {
						nowLocalgovinfoid = Long.parseLong(initImportExportForm.localgovinfoid);
						localgovinfoid = nowLocalgovinfoid;
					}else{
						nowLocalgovinfoid = loginDataDto.getLocalgovinfoid();
						localgovinfoid = nowLocalgovinfoid;
					}

					// 災害マッピング取得
					String [] selectTrackFileMultibox = initImportExportForm.selectFileTrackMultibox;
					String [] selectTrackDbMultibox = initImportExportForm.selectDbTrackMultibox;
					// バリデーションチェック
					// ファイルの災害と既存の災害のマッピングは1対1
					// ファイルの災害をチェックした場合は既存の災害を選択しておく必要がある。
					boolean nullCheck = true;
					if(selectTrackFileMultibox == null && selectTrackDbMultibox == null){
//						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose an disaster of an import target and an disaster of a merging target."), false));
						nullCheck = false;
					}else if(selectTrackFileMultibox == null && selectTrackDbMultibox != null){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose an disaster of a import target."), false));
						nullCheck = false;
					}else if(selectTrackFileMultibox != null && selectTrackDbMultibox == null){
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose 1 disaster of a merging target."), false));
						nullCheck = false;
					}
					if(nullCheck){
						for(int i = 0; i < selectTrackFileMultibox.length; i++){
							trackMap.put(Long.parseLong(selectTrackFileMultibox[i]), null);
						}
						for(int i = 0; i < selectTrackDbMultibox.length; i++){
							String[] rowData = selectTrackDbMultibox[i].split(":");
							String fileTrackId = rowData[0];
							String dbTrackId = rowData[1];

							if(! trackMap.containsKey(Long.parseLong(fileTrackId))){
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose an disaster of a import target."), false));
							}else{
								Long nowDbTrackId = trackMap.get(Long.parseLong(fileTrackId));
								if(nowDbTrackId != null){
									errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose only 1 disaster of a merging target."), false));
								}else{
									trackMap.put(Long.parseLong(fileTrackId), Long.parseLong(dbTrackId));
								}
							}
						}
						for(int i = 0; i < selectTrackFileMultibox.length; i++){
							Long nowDbTrackId = trackMap.get(Long.parseLong(selectTrackFileMultibox[i]));
							if(nowDbTrackId == null){
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please choose 1 disaster of a merging target."), false));
							}
						}
					}

					if(! errors.isEmpty()){
						ActionMessagesUtil.addErrors(bindingResult, errors);
						setupModel(model);
						return "/admin/disconnect/initImportExport/trackselect";
					}else{
//						StringBuffer sbuf = new StringBuffer();
//						for(Map.Entry<Long, Long> e : trackMap.entrySet()){
//							sbuf.append(e.getKey());
//							sbuf.append("-");
//							sbuf.append(e.getValue());
//							sbuf.append(",");
//						}
//						int index = sbuf.lastIndexOf(",");
//						sbuf = sbuf.deleteCharAt(index);
//						trackMapText = sbuf.toString();
					}

					break;
//				case Constants.DISCONNECT_IMPORTSTATUS_STEP3:
//					doExec = true;
//					uploadTrackDataDirName = initImportExportForm.uploadTrackDataDirName;
//					uploadTrackDataFileName = initImportExportForm.uploadTrackDataFileName;
//
//					// 自治体情報の取得
//					// システム管理者の場合は画面のセレクトボックスの指定値を使用
//					if(loginDataDto.getLocalgovinfoid()==0) {
//						nowLocalgovinfoid = Long.parseLong(initImportExportForm.localgovinfoid);;
//						localgovinfoid = nowLocalgovinfoid;
//					}
//					// 各自治体の場合
//					else {
//						nowLocalgovinfoid = loginDataDto.getLocalgovinfoid();
//						localgovinfoid = nowLocalgovinfoid;
//					}

//					// 災害マッピング情報の復元
//					trackMapText = initImportExportForm.trackMapText;
//					String [] trackMapTextArray = trackMapText.split(",");
//					for(int i = 0 ; i < trackMapTextArray.length; i++){
//						String [] trackMapTextArray2 = trackMapTextArray[i].split("-");
//						trackMap.put(Long.parseLong(trackMapTextArray2[0]), Long.parseLong(trackMapTextArray2[1]));
//
//					}

//					break;
				default:
					logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to import disaster data in communication disruption tool."));
					// ActionMessages errors = new ActionMessages();
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to import disaster data in communication disruption tool."), false));
					ActionMessagesUtil.addErrors(bindingResult, errors);
					return "/admin/disconnect/initImportExport/result";
				}
			}catch(Exception e) {
				throw new ServiceException(lang.__("Unable to read disaster file."), e);
			}


			try {

				if(initImportExportForm.importExecStatus.equals(Constants.DISCONNECT_IMPORTSTATUS_STEP2) ||
						initImportExportForm.importExecStatus.equals(Constants.DISCONNECT_IMPORTSTATUS_STEP3)){
					try{
						tmpDir = FileUtil.getTmpDir();
						workDirName = initImportExportForm.uploadTrackDataDirName;
						workDir = new File(tmpDir, workDirName);
						if(! workDir.exists()){
							throw new ServiceException(lang.__("Work directory is not available. Confirm import process again."));
						}

						String savedTemplateFileName = initImportExportForm.uploadTrackDataFileName;
						if(savedTemplateFileName==null) {
							throw new ServiceException(lang.__("Failed to expand zip file."));
						}
						templateZipFile = new File(workDir, savedTemplateFileName);
						if(!templateZipFile.exists()) throw new ServiceException(lang.__("Disaster data is not found."));
						exportTrackFileSet =  ExportTrackFileSet.unzip(templateZipFile);
					}catch(IOException e){
						throw new ServiceException(lang.__("Failed to expand zip file."));

					}

				}




				localgovInfo = localgovInfoService.findById(nowLocalgovinfoid);
				// 自治体の管理者グループを取得
				groupInfo = groupInfoService.findByLocalgovInfoIdAndAdmin(nowLocalgovinfoid);

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

					disconnectImportConfirmDto = importTrackdataService.execute(
							true,
							trackMap,
							updateFeatures,
							updateTrackTableDatas,
							updateTrackTableDatasHidden,
							exportTrackFileSet,
							localgovInfo.id,
							groupInfo.ecomuser,
							communityInfo,
							ecommGroupInfo,
							mapmasterInfo,
							importAll,
							importAttachedFile);

//					// チェックボックスをデフォルトでONにしておく
//					if(!doExec){
//						List<String> updateFeaturesList = new ArrayList<String>();
//						List<String> updateTrackTableDataList = new ArrayList<String>();
//						StringBuffer sbuf = new StringBuffer();
//						for(DisconnectImportConfirmDto.ImportTrackData importTrackData : disconnectImportConfirmDto.importTrackDataList){
//							// レイヤ
//							for(DisconnectImportConfirmDto.ImportLayerInfo importLayerInfo : importTrackData.importLayerList){
//								for(DisconnectImportConfirmDto.UnmatchFeatureInfo unmatchFeatureInfo : importLayerInfo.unmatchFeatureInfoList){
//									updateFeaturesList.add(unmatchFeatureInfo.checkBoxValue);
//								}
//							}
//
//							// レイヤ以外
//							for(DisconnectImportConfirmDto.ImportTrackTableData importTrackTableData : importTrackData.importTrackTableDataList){
//								int idx = 0;
//								for(DisconnectImportConfirmDto.SameTrackTableDataRecord sameTrackTableDataRecord : importTrackTableData.sameTrackTableDataRecordList){
//									updateTrackTableDataList.add(sameTrackTableDataRecord.checkBoxValue);
//									String [] tmpArray = sameTrackTableDataRecord.checkBoxValue.split("-");
//									if(idx == 0){
//										if(sbuf.length() <= 0){
//											sbuf.append(tmpArray[0]);
//											sbuf.append("-");
//											sbuf.append(tmpArray[1]);
//											sbuf.append(",");
//										}else{
//											sbuf.append(":");
//											sbuf.append(tmpArray[0]);
//											sbuf.append("-");
//											sbuf.append(tmpArray[1]);
//											sbuf.append(",");
//										}
//									}else{
//										sbuf.append(tmpArray[1]);
//										sbuf.append(",");
//									}
//									idx++;
//								}
//								if(sbuf.length() > 0){
//							        int index = sbuf.lastIndexOf(",");
//							        sbuf = sbuf.deleteCharAt(index);
//								}
//							}
//						}
//						initImportExportForm.updateFeatures = (String[])updateFeaturesList.toArray(new String[0]);
//						initImportExportForm.updateTrackTableDatas = (String[])updateTrackTableDataList.toArray(new String[0]);
//
//						if(sbuf.length() > 0){
//					        updateTrackTableDatasHidden = sbuf.toString();
//						}
//					}

				} catch (Exception e) {
					throw new ServiceException(lang.__("An error occurred while importing disaster map and data."), e);
				}

				// 作業ディレクトリの削除
//				if(doExec){
					if(workDir != null && workDir.exists()){
						FileUtil.dirDelete(workDir);
					}
//				}
			} catch (ServiceException e) {
				logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to import disaster data in communication disruption tool."), e);
				//ActionMessages errors = new ActionMessages();
				Throwable t = e;
				while(t!=null) {
					if(t instanceof ServiceException) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(t.getMessage(), false));
					}
					t=t.getCause();
				}
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}

			endMessage = "";
//			if(doExec){
				if((StringUtil.isEmpty(hasError)) || (! hasError.equals("error"))){
					endMessage = lang.__("An import of disaster data has been completed.");
				}
				setupModel(model);
				return "/admin/disconnect/initImportExport/result";
//			}else{
//				return "confirm.jsp";
//			}
		}else{
			setupModel(model);
			return "/admin/disconnect/initImportExport/result";
		}
	}

}
