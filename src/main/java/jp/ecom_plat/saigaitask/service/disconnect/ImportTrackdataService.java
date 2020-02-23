/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.disconnect;

import static jp.ecom_plat.saigaitask.entity.Names.convertidData;
import static jp.ecom_plat.saigaitask.entity.Names.importtablemasterData;
import static jp.ecom_plat.saigaitask.entity.Names.importtrackInfo;
import static jp.ecom_plat.saigaitask.entity.Names.importtracktableInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.postgresql.PGStatement;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.jsp.admin.MapRestore;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.admin.disconnect.DisconnectImportConfirmDto;
import jp.ecom_plat.saigaitask.entity.db.ConvertidData;
import jp.ecom_plat.saigaitask.entity.db.ImporttablemasterData;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.db.ImporttracktableInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.OauthconsumerData;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;
import jp.ecom_plat.saigaitask.service.db.ConvertidDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakegrouplayerDataService;
import jp.ecom_plat.saigaitask.service.db.EarthquakelayerDataService;
import jp.ecom_plat.saigaitask.service.db.ImporttablemasterDataService;
import jp.ecom_plat.saigaitask.service.db.ImporttrackInfoService;
import jp.ecom_plat.saigaitask.service.db.ImporttracktableInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.OauthconsumerDataService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.disconnect.TrackDataExportService.ExportTrackFileSet;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.util.FileUtil;


/**
 * 災害データをインポートするサービスクラスです.
 */
@org.springframework.stereotype.Service
public class ImportTrackdataService extends AbstractImportService {

	/** Logger */
	protected Logger logger = Logger.getLogger(ImportTrackdataService.class);

	// service
	@Resource protected ExcelImportService excelImportService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected TrackDataService trackDataService;
	@Resource protected TrackmapInfoService trackmapInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected LayerService layerService;
	@Resource protected ImporttracktableInfoService importTrackTableInfoService;
	@Resource protected ImporttrackInfoService importtrackInfoService;
	@Resource protected ConvertidDataService convertidDataService;
	@Resource protected OauthconsumerDataService oauthconsumerDataService;

	@Resource
	protected EarthquakegrouplayerDataService earthquakegrouplayerDataService;
	@Resource
	protected MeteoricEarthQuakeService meteoricEarthQuakeService;
	@Resource
	protected EarthquakelayerDataService earthquakelayerDataService;
	@Resource
	protected ImporttablemasterDataService importtablemasterDataService;

	// tracktable_infoのtrackmapinfoid外部キー制約確認SQL
	private static final String getTracktableInfoFkeySql = "SELECT constraint_name as fkeyname FROM information_schema.table_constraints WHERE constraint_type = 'FOREIGN KEY' AND table_name='tracktable_info' AND constraint_name='tracktable_info_tablemasterinfoid_fkey'";
	// tracktable_infoのtrackmapinfoid外部キー制約付加SQL
	private static final String addTracktableInfoFkeySql = "ALTER TABLE ONLY tracktable_info ADD CONSTRAINT tracktable_info_tablemasterinfoid_fkey FOREIGN KEY (tablemasterinfoid) REFERENCES tablemaster_info(id)";
	// tracktable_infoのtrackmapinfoid外部キー制約削除SQL
	private static final String dropTracktableInfoFkeySql = "ALTER TABLE ONLY tracktable_info DROP CONSTRAINT IF EXISTS tracktable_info_tablemasterinfoid_fkey";

	/**
	 * 災害データのインポート
	 * @param exportFileSet
	 * @param localgovinfoid
	 * @param password
	 * @param ecomuser
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public DisconnectImportConfirmDto execute(
										Boolean doImport,
										Map<Long, Long> trackMap,
										String [] updateFeatures,
										String [] updateTrackTableDatas,
										String sameTrackTableDatas,
										ExportTrackFileSet exportTrackFileSet,
										Long localgovinfoidDest,
										String ecomuser,
										CommunityInfo communityInfo,
										jp.ecom_plat.map.db.GroupInfo groupInfo,
										MapmasterInfo mapmasterInfo
									) throws NoSuchAlgorithmException {
		return execute(
				doImport,
				trackMap,
				updateFeatures,
				updateTrackTableDatas,
				sameTrackTableDatas,
				exportTrackFileSet,
				localgovinfoidDest,
				ecomuser,
				communityInfo,
				groupInfo,
				mapmasterInfo,
				true,
				true);
	}
	
	public DisconnectImportConfirmDto execute(
										Boolean doImport,
										Map<Long, Long> trackMap,
										String [] updateFeatures,
										String [] updateTrackTableDatas,
										String sameTrackTableDatas,
										ExportTrackFileSet exportTrackFileSet,
										Long localgovinfoidDest,
										String ecomuser,
										CommunityInfo communityInfo,
										jp.ecom_plat.map.db.GroupInfo groupInfo,
										MapmasterInfo mapmasterInfo,
										boolean importAll,
										boolean importAttachedFile
									) throws NoSuchAlgorithmException {


		// エクセルファイルの存在チェック(自治体情報)
		File infoExportXsl =  exportTrackFileSet.infoExportXsl;
		if(!infoExportXsl.exists()) {
			throw new ServiceException(lang.__("Localgovinfo data {0} not found.", infoExportXsl.getName()));
		}


		// 地図バックアップファイルの存在チェック
		Map<String, File> mapBackupZipFiles = new HashMap<String, File>();
		for(File mapBackupFile : exportTrackFileSet.trackMapbackupZips){
			if(!mapBackupFile.exists()) {
				throw new ServiceException(lang.__("Map backup {0} is not found.", mapBackupFile.getName()));
			}else{
				mapBackupZipFiles.put(mapBackupFile.getName(), mapBackupFile);
			}
		}

		// エクセルファイルの存在チェック(災害データ)
		Map<String, File> dataExportXslFiles = new HashMap<String, File>();
		for(File dataExportXsl : exportTrackFileSet.dataExportXsls){
			if(!dataExportXsl.exists()) {
				throw new ServiceException(lang.__("Disaster data {0} not found.", dataExportXsl.getName()));
			}else{
				dataExportXslFiles.put(dataExportXsl.getName(), dataExportXsl);
			}
		}

		// マッピングファイルの存在チェック
		if(!exportTrackFileSet.xslMapMappingFile.exists()) {
			throw new ServiceException(lang.__("Mapping file of disaster data and map backup file are not found."));
		}

		// OAuthコンシューマデータファイルの存在チェック
		if(!exportTrackFileSet.saigaitaskOauthFile.exists()) {
			throw new ServiceException(lang.__("OAuth authentication information file not found."));
		}

		// アップロードファイルzipファイルの存在チェック
		File uploadZipFileDir = null;
		if(exportTrackFileSet.uploadedZipFile != null){
			if(!exportTrackFileSet.uploadedZipFile.exists()) {
				throw new ServiceException(lang.__("An archive file of upload file is not found."));
			}else{
				try{
					FileUtil.unzip(exportTrackFileSet.uploadedZipFile, "Windows-31J", TrackDataExportService.PREFIX_UPLOADEDZIP, true);
					File uploadZipFileRootDir = new File(exportTrackFileSet.uploadedZipFile.getParentFile(), TrackDataExportService.PREFIX_UPLOADEDZIP);
					String uploadZipFileDirName = exportTrackFileSet.uploadedZipFile.getName().substring(0, exportTrackFileSet.uploadedZipFile.getName().length() - 4);
					uploadZipFileDir = new File(uploadZipFileRootDir, uploadZipFileDirName);
					if(!(uploadZipFileDir.exists() && uploadZipFileDir.isDirectory())){
						uploadZipFileDir = null;
					}
				}catch(IOException e){
	    			logger.error("uploadedZipFile",e);
					throw new ServiceException(lang.__("Failed in opening of a upload file archive file."),e);
				}
			}
		}


		// ユーザID変換テーブル生成
		MapDB mapDB = MapDB.getMapDB();
		UserInfo userInfo = mapDB.getAuthIdUserInfo(ecomuser);
		if(userInfo==null) throw new ServiceException(lang.__("e-Com map user \"{0}\" not exist.", ecomuser));
		HashMap<Integer, UserInfo> userInfoTable = new HashMap<Integer, UserInfo>();
		userInfoTable.put(0, userInfo);
		userInfoTable.put(userInfo.userId, userInfo);


		// マッピングファイルからマッピング情報を取得
		List<UploadedTracdatakInfo> uploadedTracdatakInfos = new ArrayList<UploadedTracdatakInfo>();
        ImporttrackInfo importtrackInfo = null;
        try {
            //ファイルを読み込む
            FileReader fr = new FileReader(exportTrackFileSet.xslMapMappingFile);
            BufferedReader br = new BufferedReader(fr);

            //読み込んだファイルを１行ずつ処理する
            String line;
            int lineCnt = 0;
            while ((line = br.readLine()) != null) {
                //区切り文字","で分割する
                String [] csvLine = line.split(",");

                UploadedTracdatakInfo uploadedTracdatakInfo = new UploadedTracdatakInfo();
                uploadedTracdatakInfo.localgovinfoid = Long.parseLong(csvLine[UploadedTracdatakInfo.idxLocalgovinfoid]);
                uploadedTracdatakInfo.mapid = Long.parseLong(csvLine[UploadedTracdatakInfo.idxMapid]);
                
                // 先頭のマスタマップ情報
                if (lineCnt == 0) {
	                uploadedTracdatakInfo.mapZipFileName = csvLine[UploadedTracdatakInfo.idxMapZipFileName];
                }
                    
                // 画面で選択した災害のみインポート
                else if (trackMap.containsKey(Long.parseLong(csvLine[UploadedTracdatakInfo.idxTrackdataid]))) {
	                uploadedTracdatakInfo.xlsFileName = csvLine[UploadedTracdatakInfo.idxXlsFileName];
	                uploadedTracdatakInfo.trackdataid = Long.parseLong(csvLine[UploadedTracdatakInfo.idxTrackdataid]);
                    uploadedTracdatakInfo.trackdataname = csvLine[UploadedTracdatakInfo.idxTrackDataName];

                    // インポート済みかチェック
    				BeanMap conditions = new BeanMap();
    				conditions.put(importtrackInfo().oldlocalgovinfoid().toString(), uploadedTracdatakInfo.localgovinfoid);
    				conditions.put(importtrackInfo().oldmapid().toString(), uploadedTracdatakInfo.mapid);
    				conditions.put(importtrackInfo().oldtrackdataid().toString(), uploadedTracdatakInfo.trackdataid);
                    // SV20160614--
                	Long newTrackdataid = trackMap.get(Long.parseLong(csvLine[UploadedTracdatakInfo.idxTrackdataid]));
    				conditions.put(importtrackInfo().trackdataid().toString(), newTrackdataid);
                    // --SV20160614
    				List<ImporttrackInfo> importtrackInfos = importtrackInfoService.findByCondition(conditions);
    				if(importtrackInfos == null || importtrackInfos.size() <= 0){
    					uploadedTracdatakInfo.isImported = false;
    				}else{
    					uploadedTracdatakInfo.isImported  = true;
    					importtrackInfo = importtrackInfos.get(0);
    					uploadedTracdatakInfo.importtrackinfoid = importtrackInfo.id;
    					uploadedTracdatakInfo.newtrackdataid = importtrackInfo.trackdataid;
    					uploadedTracdatakInfo.newmapid = importtrackInfo.mapid;
    				}
                }
				uploadedTracdatakInfos.add(uploadedTracdatakInfo);
                lineCnt++;
            }
            br.close();
        } catch (IOException e) {
			throw new ServiceException(lang.__("An error occurred while importing disaster data."),e);
        }


        DisconnectImportConfirmDto disconnectUpdateLayerInfoDto;
        	disconnectUpdateLayerInfoDto =  executeImport(
												mapDB,
												ecomuser,
												communityInfo,
												groupInfo,
												localgovinfoidDest,
												userInfoTable,
												mapBackupZipFiles,
												infoExportXsl,
												dataExportXslFiles,
												uploadedTracdatakInfos,
												updateFeatures,
												updateTrackTableDatas,
												sameTrackTableDatas,
												trackMap,
												exportTrackFileSet.saigaitaskOauthFile,
												uploadZipFileDir,
												importAll,
												importAttachedFile

        									);
        	
        return disconnectUpdateLayerInfoDto;
	}
	

	/**
	 * 災害データのインポート
	 * @param exportFileSet
	 * @param localgovinfoid
	 * @param password
	 * @param ecomuser
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private DisconnectImportConfirmDto executeImport(
				MapDB mapDB,
				String ecomuser,
				CommunityInfo communityInfo,
				jp.ecom_plat.map.db.GroupInfo groupInfo,
				Long localgovinfoidDest,
				HashMap<Integer, UserInfo> userInfoTable,
//				ImporttrackInfo importtrackInfo,
				Map<String, File> mapBackupZipFiles,
				File infoExportXsl,
				Map<String, File> dataExportXslFiles,
				List<UploadedTracdatakInfo> uploadedTracdatakInfos,
				String [] updateFeatures,
				String [] updateTrackTableDatas,
				String sameTrackTableDatas,
				Map<Long, Long> trackMap,
				File saigaitaskOauthFile,
				File uploadZipFileDir,
				boolean importAll,
				boolean importAttachedFile
			) throws NoSuchAlgorithmException {
        DisconnectImportConfirmDto disconnectUpdateLayerInfoDto = new DisconnectImportConfirmDto();
        Map<String, String> layerInfoIdReverseMap = new HashMap<String, String>();    
        
        boolean firstRecord = true;
        for(UploadedTracdatakInfo uploadedTracdatakInfo : uploadedTracdatakInfos){
        	DisconnectImportConfirmDto.ImportTrackData importTrackData = new DisconnectImportConfirmDto.ImportTrackData();

    		// マスタマップ情報（先頭レコード）
    		if (firstRecord) {
    			firstRecord = false;
		       	File targetMapBackupZipFile = mapBackupZipFiles.get(uploadedTracdatakInfo.mapZipFileName);
		       	importTrackData.dbMapId = mapmasterInfoService.findByLocalgovInfoId(localgovinfoidDest).id;

        		MapRestore mapImporter = null;
        		try{
			
					// DB からテーブルマスタ情報を取得する
					List<TablemasterInfo> tableMasterInfoList = tablemasterInfoService.findByLocalgovinfoid(localgovinfoidDest);
					
					// 名称をキーとする　Map を作成する
					Map<String, TablemasterInfo> nameMasterMap = new HashMap<String, TablemasterInfo>();
					for (TablemasterInfo tablemasterInfo : tableMasterInfoList)
						if (tablemasterInfo.layerid != null && !tablemasterInfo.layerid.isEmpty())
							nameMasterMap.put(tablemasterInfo.name, tablemasterInfo);
			
					// インポートファイルからテーブルマスタ情報を取得する
					POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(infoExportXsl));
					HSSFWorkbook wb = new HSSFWorkbook(fs);
					tableMasterInfoList = (List<TablemasterInfo>)excelImportService.importRecords(wb, TablemasterInfo.class, null, localgovinfoidDest);
				
					// レイヤID の変換表を作成する（名称で対応させる）
					Map<String, TablemasterInfo> layerIdMap = new HashMap<String, TablemasterInfo>();
					for (TablemasterInfo tablemasterInfo : tableMasterInfoList) {
						if (tablemasterInfo.layerid != null && !tablemasterInfo.layerid.isEmpty() &&
							nameMasterMap.containsKey(tablemasterInfo.name)) {
							layerIdMap.put(tablemasterInfo.layerid, nameMasterMap.get(tablemasterInfo.name));
							layerInfoIdReverseMap.put(nameMasterMap.get(tablemasterInfo.name).layerid, tablemasterInfo.layerid);
						}
					}
					
        			mapImporter = new MapRestore(targetMapBackupZipFile, true);
        			mapImporter.dest(communityInfo, groupInfo, userInfoTable);
					mapImporter.initRestoreContentsLayerData();

        			Map<String, TablemasterInfo> layerInfoIdMap = new HashMap<String, TablemasterInfo>();
					while(mapImporter.hasNextRestoreLayer()){
        				LayerInfo layerInfo = mapImporter.readLayerInfo();
        				if(layerInfo.type != LayerInfo.TYPE_LOCAL){
        					continue;
        				}

        				// インポート後のレイヤIDを取得
        				if (layerIdMap.containsKey(layerInfo.layerId)){
            				layerInfoIdMap.put(layerInfo.layerId, layerIdMap.get(layerInfo.layerId));
        				}
        			}

        			for(Map.Entry<String, TablemasterInfo> layerInfoIdMapEntry : layerInfoIdMap.entrySet()) {
        				String fileLayerId = layerInfoIdMapEntry.getKey();
        				List<String> fileFeatureColumnNames = mapImporter.getFileFeatureColumnNames(fileLayerId);
        				List<BeanMap> fileFeatureRecords = mapImporter.getFileFeatureRecords(fileLayerId);
        				List<String> fileFeatureAttrColumnNames = mapImporter.getFileFeatureAttrColumnNames(fileFeatureColumnNames);

        				// インポート先のFeatureの属性情報を取得
        				LayerInfo dbLayer = mapDB.getLayerInfo(layerInfoIdMapEntry.getValue().layerid);
        				Iterable<AttrInfo> attrInfos = dbLayer.getAttrIterable();
        				List<String> attrNameList = new ArrayList<String>();
        				int attrCount = 0;
        				for(AttrInfo attrInfo: attrInfos){
        					attrCount++;
        					attrNameList.add(attrInfo.name);
        				}

        				// 属性のカラム数が合わない場合はインポートしない
        				if(fileFeatureAttrColumnNames.size() != attrCount){
        					// TODO:警告フォームに入れる
        					continue;
        				}

        				String tableName = layerInfoIdMapEntry.getValue().tablename;
//        				{
//        					List<TracktableInfo> tracktableInfos = tracktableInfoService.findByLayerId(dbLayer.layerId);
//            				// コピーフラグ:falseの場合、複数結果が得られる。
//            				// どれも同じなので先頭１つ目を用いる
//        					if(0<tracktableInfos.size()) tableName = tracktableInfos.get(0).tablename;
//        				}
        				
        				// 時系列データをマージする
        				mergeFeatures(tableName, fileFeatureRecords, mapImporter, dbLayer, importTrackData.dbMapId,
        						fileFeatureColumnNames, trackMap, importAll);

        				Iterator<BeanMap> rows = fileFeatureRecords.iterator();

        				// アップロードファイルを追加更新する
        				while(rows.hasNext()){

        					BeanMap row = (BeanMap)rows.next();
        					if (!row.containsKey("old_gid"))
        						continue;
        					Long gId = Long.parseLong(row.get("gid").toString());			/* DB 検索用 */
        					Long oldGId = Long.parseLong(row.get("old_gid").toString());	/* ファイル検索用 */

        					List<BeanMap> result = tableService.selectById(tableName, "gid", gId);

           					// 新規追加
            				if(result == null || result.size() <= 0){

            					// ファイルがあればそれも追加する
            					mapImporter.restoreFeatureFiles(fileLayerId, importTrackData.dbMapId, dbLayer, gId, oldGId);

            				// 更新
            				}else{

       							// ファイルの追加・更新
    							List<MapRestore.FeatureFileInfo> fileFeatureFileInfos = mapImporter.getFileFeatureFiles(fileLayerId, gId, oldGId);
        						if(importAttachedFile && fileFeatureFileInfos.size() > 0){
            						Vector<MapRestore.FeatureFileInfo> dbFeatureFiles = mapImporter.getDbFeatureFiles(dbLayer.layerId, gId);
            						if(dbFeatureFiles.size() > 0){
            							for(MapRestore.FeatureFileInfo fileFeatureFileInfo : fileFeatureFileInfos){
                							// ファイル名はパスを除去して比較
            								String [] fileTempArray = fileFeatureFileInfo.url.split("/");
            								String fileFileUrl = fileTempArray[fileTempArray.length - 1];

            								Boolean isFound = false;
            								for(MapRestore.FeatureFileInfo dbFeatureFile : dbFeatureFiles){
                    							// ファイル名はパスを除去して比較
                								String [] dbTempArray = dbFeatureFile.url.split("/");
                								String dbFileUrl = dbTempArray[dbTempArray.length - 1];

                   								// タイトルとファイル名が一致したファイルは更新対象候補
                								if(fileFeatureFileInfo.title.equals(dbFeatureFile.title) && fileFileUrl.equals(dbFileUrl)){
                									isFound = true;
                       								// アップロード日時が一致したファイルは変更なしとして処理しない
            										if(fileFeatureFileInfo.time_upload.equals(dbFeatureFile.time_upload)){
            											continue;
            										}else{
            											// アップロード日時が新しい場合は更新
            											if(Timestamp.valueOf(fileFeatureFileInfo.time_upload).compareTo(Timestamp.valueOf(dbFeatureFile.time_upload)) > 0){
            												fileFeatureFileInfo.gid = gId.longValue();
                        									mapImporter.updateFeatureFile(fileLayerId, importTrackData.dbMapId, dbLayer, fileFeatureFileInfo, true);
            											}
            											break;
            										}
            									}
            								}
            								if( ! isFound ){
            									// 追加
            									fileFeatureFileInfo.gid = gId.longValue();
            									mapImporter.updateFeatureFile(fileLayerId, importTrackData.dbMapId, dbLayer, fileFeatureFileInfo, false);
            								}
            							}
            						}else{
            							// 全ファイルを追加する
            							mapImporter.restoreFeatureFiles(fileLayerId, importTrackData.dbMapId, dbLayer, gId, oldGId);
            						}
        						}
            				}
        				}
        			}
         		}catch(Exception e){
        			logger.debug("import layer faild",e);
    				throw new ServiceException(lang.__("An error occurred in process of importing map backup file."), e);
        		}finally{
        			if(mapImporter != null){
        				try{
        					mapImporter.destroyRestoreContentsLayerData();
        				}catch(Exception e){
                			logger.error("ecommap connection close faild", e);
        				}
        			}
        		}
        	}
    		
    		// 災害の情報（２番め以降のレコード）
    		else {

	        	// 画面で選択した災害のみ処理対象
	        	if(! trackMap.containsKey(uploadedTracdatakInfo.trackdataid))
	        		continue;
	        	
    			importTrackData.fileTrackDataId = uploadedTracdatakInfo.trackdataid;
    			importTrackData.fileMapId = uploadedTracdatakInfo.mapid;
    			importTrackData.trackDataName = uploadedTracdatakInfo.trackdataname;
    			importTrackData.fileTrackDataName = uploadedTracdatakInfo.trackdataname;

	    		TrackData dbTrackData = trackDataService.findById(trackMap.get(uploadedTracdatakInfo.trackdataid));
	    		importTrackData.dbTrackDataName = dbTrackData.name;

	    		//過去にインポート済みの場合
	   			if(uploadedTracdatakInfo.isImported){
	        		importTrackData.isUpdate = true;
	        		importTrackData.dbTrackDataId = uploadedTracdatakInfo.newtrackdataid;
	        		importTrackData.dbMapId = uploadedTracdatakInfo.newmapid;
        			disconnectUpdateLayerInfoDto.importTrackDataList.add(importTrackData);
	   			}
	   			
	         	// 初回マージ
	   			else {
		        	File targetDataExportXslFile = dataExportXslFiles.get(uploadedTracdatakInfo.xlsFileName);
		    		importTrackData.fileTrackDataId = uploadedTracdatakInfo.trackdataid;
	
		    		// 地図の復元はしない
	        		// 災害データの取り込みも不要（起動済みのため）
	        		//  四号様式などはいるかも
	        		// 地物の更新と、インポート履歴の作成
	    			try{
			        	
	//    				// インポートファイルからTrackTableInfoを取得
	//    				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(targetDataExportXslFile));
	//    				HSSFWorkbook wb = new HSSFWorkbook(fs);
	// 					@SuppressWarnings("unchecked")
	// 					List<TracktableInfo> fileTracktableInfoList = (List<TracktableInfo>)excelImportService.readRecords(wb, TracktableInfo.class);
	// 					// ソートしておく
	// 					fileTracktableInfoList = sortTracktableInfoList(fileTracktableInfoList);
	// 					wb.close();
	
	 					// インポート先災害のTrackTableInfoを取得
	 					long dbTrackDataId = trackMap.get(uploadedTracdatakInfo.trackdataid);
	 					TrackmapInfo dbTtrackmapInfo = trackmapInfoService.findByTrackDataId(dbTrackData.id);
	 					List<TracktableInfo> dbTracktableInfoList = tracktableInfoService.findByTrackDataId(dbTrackDataId);
	 					// ソートしておく
	 					dbTracktableInfoList = sortTracktableInfoList(dbTracktableInfoList);
	 					long dbMapId = dbTtrackmapInfo.mapid;
		        			
	    	    		// インポート履歴情報を更新する
	    	        	updateImportResult(uploadedTracdatakInfo, localgovinfoidDest, dbTrackDataId, dbMapId, dbTracktableInfoList, layerInfoIdReverseMap);
	    	        	
	        			importTrackData.isUpdate = false;
	            		importTrackData.dbTrackDataId = dbTrackDataId;
	            		importTrackData.dbMapId = dbMapId;
	        			disconnectUpdateLayerInfoDto.importTrackDataList.add(importTrackData);
	    			}catch(Exception e){
	        			logger.debug("import layer faild",e);
	    				throw new ServiceException(lang.__("An error occurred in process of importing map backup file."));
	    			}finally{
	    				try{
	            			// 記録テーブル情報の外部キー制約を再設定
	            			// addTracktableInfoFkeyConst();
	    				}catch(Exception e2){
	    					throw e2;
	    				}
	    			}
	    			
		         	// レイヤ以外のデータインポート
		        	ImportTrackdataResult  importTrackdataResult = importTrackdata(
		        			true,
		        			targetDataExportXslFile,
		        			uploadedTracdatakInfo.localgovinfoid,
		        			localgovinfoidDest,
		        			importTrackData.fileTrackDataId,
		        			importTrackData.dbTrackDataId,
		        			updateTrackTableDatas,
		        			sameTrackTableDatas,
		        			uploadZipFileDir);
	    		}
    		}
        }


		// OAUTH認証情報のインポート
        //ファイルを読み込む
        try {
            FileReader fr = new FileReader(saigaitaskOauthFile);
            BufferedReader br = new BufferedReader(fr);

			//読み込んだファイルを１行ずつ処理する
            String line;
            String consumerkey = "";
            String consumerkeysecret = "";
            while ((line = br.readLine()) != null) {
            	String []  row = line.split(",");
            	consumerkey = row[0];
            	consumerkeysecret = row[1];
            }
            br.close();

            if(StringUtil.isEmpty(consumerkey) || StringUtil.isEmpty(consumerkeysecret)){
				throw new ServiceException(lang.__("OAuth authentication information file read error."));
	        }else{
	            String appName = lang.__("NIED disaster information sharing system surrogate server");
    			OauthconsumerData oauthconsumerData = oauthconsumerDataService.findByLocalgovinfoidAndApplicationname(
    					localgovinfoidDest, appName);
    			if(oauthconsumerData != null){
    				oauthconsumerData.consumerkey = consumerkey;
    				oauthconsumerData.consumerkeysecret = consumerkeysecret;
    				oauthconsumerDataService.update(oauthconsumerData);
    			}else{
    				oauthconsumerData = new OauthconsumerData();
    				oauthconsumerData.localgovinfoid = localgovinfoidDest;
    				oauthconsumerData.applicationname = appName;
    				oauthconsumerData.consumerkey = consumerkey;
    				oauthconsumerData.consumerkeysecret = consumerkeysecret;
    				oauthconsumerDataService.insert(oauthconsumerData);
    			}
	        }
        } catch(ArrayIndexOutOfBoundsException ae){
			throw new ServiceException(lang.__("Authentication information is not included in an OAuth authentication information file."),ae);
        } catch (IOException e) {
			throw new ServiceException(lang.__("An error occurred while importing OAuth authentication information data."),e);
        }

        return disconnectUpdateLayerInfoDto;
	}
	
	/**
	 * 地物の時系列データをマージする
	 * 
	 * @param tableName
	 * @param fileFeatureList
	 * @param mapImporter
	 * @param dbLayer
	 * @param importTrackData
	 * @param fileFeatureColumnNames
	 * @throws SQLException 
	 */
	private void mergeFeatures(String tableName, List<BeanMap> fileFeatureList, MapRestore mapImporter,
			LayerInfo dbLayer, Long dbMapId, List<String> fileFeatureColumnNames,
			Map<Long, Long> trackMap, boolean importAll) throws SQLException {
		Timestamp minTime = new Timestamp(PGStatement.DATE_NEGATIVE_INFINITY); 
		
		Map<Long, SortedMap<Timestamp, BeanMap>> dbOrgidFeatureMap = new HashMap<Long, SortedMap<Timestamp, BeanMap>>();
		Map<Long, SortedMap<Timestamp, BeanMap>> flOrgidFeatureMap = new HashMap<Long, SortedMap<Timestamp, BeanMap>>();
		
		// ファイル（マージ元）より取得した地物一覧を _orgid で分類、time_from でソートする
		for (BeanMap record : fileFeatureList) {

			// レイヤが時系列有効でない
			if (!record.containsKey("_orgid") || !record.containsKey("trackdataid")) {
				continue;
			}
			String _orgidStr = (String)record.get("_orgid");
			String timeFromStr = (String)record.get("time_from");
			
			if (_orgidStr == null || timeFromStr == null) {
				continue;
			}
			Long _orgid = new Long(_orgidStr);
			
			// trackdataid を置換える
			String trackdataidStr = (String)record.get("trackdataid");
			if (trackdataidStr != null && !trackdataidStr.isEmpty()) {
				Long trackdataid = trackMap.get(new Long(trackdataidStr));
				if (trackdataid != null)
					record.put("trackdataid", trackdataid.toString());
				// 画面で選択された災害でない場合は trackdataid を消去する
				else
					record.put("trackdataid", "");
			}
			
			// time_from 文字列を Timestamp オブジェクトに変換する
			Timestamp timeFrom;
			if ("-infinity".equals(timeFromStr))
				timeFrom = new Timestamp(PGStatement.DATE_NEGATIVE_INFINITY);
			else
				timeFrom = Timestamp.valueOf(timeFromStr);
			
			// _orgid が同一の地物がある場合はそれに追加、ない場合は新規作成
			SortedMap<Timestamp, BeanMap> feature = flOrgidFeatureMap.get(_orgid);
			if (feature == null) {
				feature = new TreeMap<Timestamp, BeanMap>();
				flOrgidFeatureMap.put(_orgid, feature);
			}
			feature.put(timeFrom, record);
			
			// 最も古い時刻を求める
//			if (timeFrom.compareTo(minTime) < 0)
//				minTime = timeFrom;
		}
		
		// DB よりデータを取得する
		String sql = "SELECT * FROM " + tableName + " WHERE time_from >= ?";
		List<BeanMap> dbFeatureList = jdbcManager.selectBySql(BeanMap.class, sql, minTime).getResultList();
		
		// DB（マージ先）より取得した地物一覧を _orgid で分類、time_from でソートする
		for (BeanMap record : dbFeatureList) {
			Long _orgid = (Long)record.get("_orgid");
			Timestamp timeFrom = (Timestamp)record.get("timeFrom");
			
			// レイヤが時系列有効でない
			if (_orgid == null || timeFrom == null)
				continue;
						
			// _orgid が同一の地物がある場合はそれに追加、ない場合は新規作成
			SortedMap<Timestamp, BeanMap> feature = dbOrgidFeatureMap.get(_orgid);
			if (feature == null) {
				feature = new TreeMap<Timestamp, BeanMap>();
				dbOrgidFeatureMap.put(_orgid, feature);
			}
			feature.put(timeFrom, record);
		}
		
		// ファイルの _orgid ごとに実行する
		for (Long _orgid : flOrgidFeatureMap.keySet()){
			SortedMap<Timestamp, BeanMap> fileFeature = flOrgidFeatureMap.get(_orgid);
			SortedMap<Timestamp, BeanMap> dbFeature = dbOrgidFeatureMap.get(_orgid);
			
			// 最古と最新のレコードを取得する
			Timestamp timeOldest = fileFeature.firstKey();
			Timestamp timeNewest = fileFeature.lastKey();
			BeanMap recordOldest = fileFeature.get(timeOldest);
			BeanMap recordNewest = fileFeature.get(timeNewest);
			
			// 「すべての履歴」が選択された場合は、trackdataid が 空のもの（画面で選択された災害でないもの）を削除する
			if (importAll) {
				for (Timestamp timeFrom : fileFeature.keySet().toArray(new Timestamp[0])) {
					BeanMap record = fileFeature.get(timeFrom);
					if (record.get("trackdataid") == null || ((String)record.get("trackdataid")).isEmpty())
						fileFeature.remove(timeFrom);
				}
			}
			//　「最新のデータのみ」が選択された場合は、一旦全部削除する
			else {
				fileFeature.clear();
			}
			
			// 最古と最新のレコードを復元する（画面で選択されてないものもインポートの対象とする）
			// 最古レコードは、同一の地物かどうかの判定で必要なため、無条件でインポートする
			fileFeature.put(timeOldest, recordOldest);
			fileFeature.put(timeNewest, recordNewest);
			
			// ファイルの最新レコードの time_to が infinity でない場合（地物が削除された場合）は、それ以降の DB のレコードを削除する
			String timeToStr = (String)fileFeature.get(timeNewest).get("time_to");
			if (!"infinity".equals(timeToStr) && dbFeature != null) {
				Timestamp timeTo = Timestamp.valueOf(timeToStr);				
				mapImporter.deleteByTimeFrom(tableName, _orgid, timeTo);
//				dbFeature = dbFeature.subMap(new Timestamp(PGStatement.DATE_NEGATIVE_INFINITY), timeTo);
			}
			
			// ファイルの _orgid　が DB に存在しない場合はレコードを追加する
			if (dbFeature == null) {
				
				// シーケンスの値を調整する
				mapImporter.adjustSequence(tableName, _orgid);
				
				for (Timestamp timeFrom : fileFeature.keySet()) {
					BeanMap record = fileFeature.get(timeFrom);
					String oldGid = (String)record.get("gid");
					
					// DB に追加
           			mapImporter.restoreLayerFeature(dbLayer, dbMapId, fileFeatureColumnNames, record, false);
           			
           			// 旧 gid を保存する
           			record.put("old_gid", oldGid);
				}
			}
			
			// 最古レコードの time_from が同一の場合は同一の地物と見なしてマージする
			else if (timeOldest.equals(dbFeature.firstKey())){
				for (Entry<Timestamp, BeanMap> flEntry : fileFeature.entrySet()) {
					BeanMap flRecord = flEntry.getValue();
					BeanMap dbRecord = dbFeature.get(flEntry.getKey());
					String oldGid = (String)flRecord.get("gid");
					
					// time_from が同一のレコードが DB に存在しない場合は追加する
					if (dbRecord == null) {
 						mapImporter.restoreLayerFeature(dbLayer, dbMapId, fileFeatureColumnNames, flRecord, false);
					}
					
					// time_from が同一のレコードが DB に存在する場合
					else  {
						
						// gid を変更する
						flRecord.put("gid", ((Long)dbRecord.get("gid")).toString());
						
						// ファイルの trackdataid が null の場合は DB のレコードの trackdataid を優先する
						if ((flRecord.get("trackdataid") == null || ((String)flRecord.get("trackdataid")).isEmpty()) && 
							!(dbRecord.get("trackdataid") == null || ((String)dbRecord.get("trackdataid")).isEmpty())) {
							flRecord.put("trackdataid", (String)dbRecord.get("trackdataid"));
						}
						
						// DB のレコードをファイルのレコードで上書きする
	 					mapImporter.restoreLayerFeature(dbLayer, dbMapId, fileFeatureColumnNames, flRecord, true);
					}
           			
           			// 旧 gid を保存する
           			flRecord.put("old_gid", oldGid);
				}
				
				// 全レコードに time_to をセットする
				mapImporter.setTimeTo(tableName, _orgid, minTime);
			}
			
			// 最古レコードの time_from が異なる場合は別の地物と見なして追加する
			else {
				
				// シーケンスの次の値を取得する
				long newOrgid = mapImporter.getNextSeqVal(tableName);
				
				// DB のレコードの _orgid を変更する
				mapImporter.updateOrgid(tableName, _orgid, newOrgid);
				
				// ファイルのレコードを DB に追加する
				for (Timestamp timeFrom : fileFeature.keySet()) {
					BeanMap record = fileFeature.get(timeFrom);
					String oldGid = (String)record.get("gid");
					
					// DB に追加
           			mapImporter.restoreLayerFeature(dbLayer, dbMapId, fileFeatureColumnNames, record, false);
           			
           			// 旧 gid を保存する
           			record.put("old_gid", oldGid);
				}
			}
		}
	}

	/**
	 * インポート履歴情報を更新する
	 * @param mapRestoreResult 地図復元結果
	 */
	public void updateImportResult(UploadedTracdatakInfo uploadedTracdatakInfo, Long localgovinfoidDest, Long dbTrackDataId, Long dbMapId, List<TracktableInfo> dbTracktableInfoList, Map<String,String> layerInfoIdReverseMap) {


		// インポート記録情報の更新
		BeanMap conditions = new BeanMap();
		conditions.put(importtrackInfo().localgovinfoid().toString(), localgovinfoidDest);
		conditions.put(importtrackInfo().oldtrackdataid().toString(), uploadedTracdatakInfo.localgovinfoid);
		conditions.put(importtrackInfo().trackdataid().toString(), dbTrackDataId);
		conditions.put(importtrackInfo().oldtrackdataid().toString(), uploadedTracdatakInfo.trackdataid);
		List<ImporttrackInfo> importtrackInfoList = importtrackInfoService.findByCondition(conditions);
		ImporttrackInfo importtrackInfo;
		if(importtrackInfoList != null && importtrackInfoList.size() > 0){
			importtrackInfo = importtrackInfoList.get(0);
		}else{
			importtrackInfo = new ImporttrackInfo();
			importtrackInfo.localgovinfoid = localgovinfoidDest;
			importtrackInfo.mapid = dbMapId;
			importtrackInfo.trackdataid = dbTrackDataId;
			importtrackInfo.oldlocalgovinfoid = uploadedTracdatakInfo.localgovinfoid;
			importtrackInfo.oldmapid = uploadedTracdatakInfo.mapid;
			importtrackInfo.oldtrackdataid = uploadedTracdatakInfo.trackdataid;
			importtrackInfoService.insert(importtrackInfo);

			importtrackInfoList = importtrackInfoService.findByCondition(conditions);
			importtrackInfo = importtrackInfoList.get(0);
		}

		// インポート記録テーブル情報の更新
		for(TracktableInfo tracktableInfo : dbTracktableInfoList){
			if(StringUtil.isEmpty(tracktableInfo.layerid) ){
				continue;
			}
			conditions = new BeanMap();
			conditions.put(importtracktableInfo().tracktableinfoid().toString(), tracktableInfo.id);
			conditions.put(importtracktableInfo().importtrackinfoid().toString(), importtrackInfo.id);
			conditions.put(importtracktableInfo().layerid().toString(), tracktableInfo.layerid);

			List<ImporttracktableInfo> importtracktableInfoList = importTrackTableInfoService.findByCondition(conditions);
			ImporttracktableInfo importtracktableInfo;
			if(importtracktableInfoList != null && importtracktableInfoList.size() > 0){
				importtracktableInfo = importtracktableInfoList.get(0);
				importtracktableInfo.oldlayerid = layerInfoIdReverseMap.get(tracktableInfo.layerid);
				importTrackTableInfoService.update(importtracktableInfo);
			}else{
				importtracktableInfo = new ImporttracktableInfo();
				importtracktableInfo.importtrackinfoid = importtrackInfo.id;
				importtracktableInfo.layerid = tracktableInfo.layerid;
				importtracktableInfo.oldlayerid = layerInfoIdReverseMap.get(tracktableInfo.layerid);
				importtracktableInfo.tracktableinfoid = tracktableInfo.id;

				importTrackTableInfoService.insert(importtracktableInfo);
			}
		}
	}



		/**
	 * 地図のリストアでIDが変わっているため、災害データの方もIDを更新する.
	 * @param mapRestoreResult 地図復元結果
	 */
	public void updateRestoreMapIdLayerId(MapRestore.Result mapRestoreResult, ImportTrackdataResult importTrackdataResult, Long oldTrackMapId, Long trackMapId) {

		BeanMap conditions = new BeanMap();
		int rowCount;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 地図情報の変換
		conditions = new BeanMap();
		conditions.put(convertidData().localgovinfoid().toString(), importTrackdataResult.localgovinfoidDest);
		conditions.put(convertidData().entityname().toString(), TrackmapInfo.class.getSimpleName());
		conditions.put(convertidData().idname().toString(), "id");
		List<ConvertidData> trackmapInfoConvertidDatas = convertidDataService.findByCondition(conditions);

		// テーブルマスタ情報を取得
		List<TablemasterInfo> tablemasterInfoList = tablemasterInfoService.findByLocalgovinfoid(importTrackdataResult.localgovinfoidDest);
		// テーブルマスタ情報から、ベースマップのレイヤを取得し保存しておく
		Map<String, Long> tablemasterInfoMap = new HashMap<String,Long>();
		Map<String, String> baseLayerMap = new HashMap<String,String>();
		for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
			tablemasterInfoMap.put(tablemasterInfo.tablename, tablemasterInfo.id);
			LayerInfo layerInfo = layerService.getLayerInfo(tablemasterInfo.layerid);
			if(layerInfo != null){
				baseLayerMap.put(layerInfo.name, layerInfo.layerId);
			}
		}

		for(ConvertidData trackmapInfoConvertidData : trackmapInfoConvertidDatas){
			TrackmapInfo trackmapInfo = trackmapInfoService.findById(Long.parseLong(trackmapInfoConvertidData.newval));

			if(trackmapInfo.mapid.equals(oldTrackMapId)){
				trackmapInfo.mapid = trackMapId;
				// DB 更新
				trackmapInfoService.update(trackmapInfo);

				conditions = new BeanMap();
				conditions.put(convertidData().localgovinfoid().toString(), importTrackdataResult.localgovinfoidDest);
				conditions.put(convertidData().entityname().toString(), TrackData.class.getSimpleName());
				conditions.put(convertidData().idname().toString(), "id");
				List<ConvertidData> trackDataConvertidDatas = convertidDataService.findByCondition(conditions);
				ImporttrackInfo importtrackInfo = new ImporttrackInfo();
				for(ConvertidData trackDataConvertidData : trackDataConvertidDatas){
					TrackData trackData = trackDataService.findById(Long.parseLong(trackDataConvertidData.newval));

					trackData.trackmapinfoid = trackmapInfo.id;
					trackDataService.update(trackData);

					// 変更履歴の保存
					conditions = new BeanMap();
					conditions.put(importtrackInfo().localgovinfoid().toString(), importTrackdataResult.localgovinfoidDest);
					conditions.put(importtrackInfo().oldlocalgovinfoid().toString(), importTrackdataResult.localgovinfoidSrc);
					conditions.put(importtrackInfo().oldtrackdataid().toString(), trackDataConvertidData.oldval);
					conditions.put(importtrackInfo().oldmapid().toString(), oldTrackMapId);
					rowCount = importtrackInfoService.getCount(conditions);
					if(rowCount == 0){
						importtrackInfo.localgovinfoid = importTrackdataResult.localgovinfoidDest;
						importtrackInfo.oldlocalgovinfoid = importTrackdataResult.localgovinfoidSrc;
						importtrackInfo.trackdataid = trackData.id;
						importtrackInfo.oldtrackdataid = Long.parseLong(trackDataConvertidData.oldval);
						importtrackInfo.mapid = trackMapId;
						importtrackInfo.oldmapid = oldTrackMapId;
						importtrackInfoService.insert(importtrackInfo);
					}else if(rowCount == 1){
						importtrackInfoService.update(importtrackInfo);
					}
					importtrackInfo = importtrackInfoService.findByCondition(conditions).get(0);

					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					// 記録テーブル情報の変換
					conditions = new BeanMap();
					conditions.put(convertidData().localgovinfoid().toString(), importTrackdataResult.localgovinfoidDest);
					conditions.put(convertidData().entityname().toString(), TracktableInfo.class.getSimpleName());
					conditions.put(convertidData().idname().toString(), "id");
					List<ConvertidData> tracktableInfoConvertidDatas = convertidDataService.findByCondition(conditions);
					for(ConvertidData tracktableInfoConvertidData : tracktableInfoConvertidDatas){
						TracktableInfo tracktableInfo = tracktableInfoService.findById(Long.parseLong(tracktableInfoConvertidData.newval));
						tracktableInfo.tablemasterinfoid = null;
						// レイヤIDが空の場合はテーブルマスタ情報の、同じテーブル名のIDをセット
						if(tracktableInfo.layerid == null || tracktableInfo.layerid.length() <= 0){
							Long tablemasterInfoId = tablemasterInfoMap.get(tracktableInfo.tablename);
							tracktableInfo.tablemasterinfoid = tablemasterInfoId;
						}else{
							// 変換後のレイヤIDを取得
							String dstLayerId = mapRestoreResult.layerIdTable.get(tracktableInfo.layerid);

							// 変更前のレイヤIDを保存
							String srcLayerId = tracktableInfo.layerid;

							if(dstLayerId==null) {
								logger.warn(lang.__("Unable to get {0} layer ID after restoration.", "layerid="+tracktableInfo.layerid));
							}
							logger.debug("tracktable_info id="+tracktableInfo.id+" layerid="+tracktableInfo.layerid+"->"+dstLayerId+" tablename="+tracktableInfo.tablename+"->"+dstLayerId);
							tracktableInfo.layerid = dstLayerId;
							tracktableInfo.tablename = dstLayerId;

							// 記録テーブル情報のtablemasterinfoidを移行先に合わせる
							// インポート後のレイヤ情報を取得
							LayerInfo layerInfo = layerService.getLayerInfo(dstLayerId);
							if(layerInfo != null){
								// レイヤの名称と一致するベースマップのレイヤIDを取得
								String baseId = baseLayerMap.get(layerInfo.name);
								if(baseId != null && baseId.length() > 0){
									tracktableInfo.tablemasterinfoid = tablemasterInfoMap.get(baseId);
								}
							}

							// 変更履歴の保存
							ImporttracktableInfo importTrackTableInfo = new ImporttracktableInfo();
							conditions = new BeanMap();
							conditions.put(importtracktableInfo().tracktableinfoid().toString(), tracktableInfo.id);
							conditions.put(importtracktableInfo().oldlayerid().toString(), srcLayerId);
							rowCount = importTrackTableInfoService.getCount(conditions);
							try{
								if(rowCount == 0){
									importTrackTableInfo.importtrackinfoid = importtrackInfo.id;
									importTrackTableInfo.tracktableinfoid = tracktableInfo.id;
									importTrackTableInfo.layerid = dstLayerId;
									importTrackTableInfo.oldlayerid = srcLayerId;
									importTrackTableInfoService.insert(importTrackTableInfo);
								}else if(rowCount == 1){
									importTrackTableInfoService.update(importTrackTableInfo);
								}
							}catch(Exception e){
								logger.debug("importTrackTableInfoService error! oldlayerid = " + importTrackTableInfo.oldlayerid);
							}
						}

						// DB 更新
						tracktableInfoService.update(tracktableInfo);
					}
				}
			}
		}
	}

	/**
	 * 災害データのインポート結果
	 */
	public static class ImportTrackdataResult extends ExcelImportService.ImportResult{

		/** インポート元の自治体ID */
		public Long localgovinfoidSrc;

		/** インポート先の自治体ID */
		public Long localgovinfoidDest;


		public Map<Class<?>,List<?>> sameRecordsMap;
	}

	/**
	 * マッピングファイルのCSV項目定義
	 */
	public static class UploadedTracdatakInfo extends TrackDataExportService.MappingFileColumns{
		/** 過去にインポート済みかどうかのフラグ */
		public Boolean isImported;

		/** インポート記録情報ID */
		public Long importtrackinfoid;

		/** インポート後の記録データID */
		public Long newtrackdataid;

		/** インポート後の地図ID */
		public Long newmapid;
	}


	/**
	 * 災害データをインポートします.
	 * @param doImport インポート実行フラグ
	 * @param trackdataExportXsl 災害データをエクスポートしたエクセルファイル
	 * @param localgovinfoidSrc インポート元自治体ID
	 * @param localgovinfoidDest インポート先自治体ID
	 * @param trackdataidSrc インポート元災害ID
	 * @param trackdataidDest インポート先災害ID
	 * @param updateTrackTableDatas インポート対象のIDチェックボックスリスト
	 * @param useUpdateTrackTableDatas updateTrackTableDatas使用フラグ
	 * @param uploadZipFileDir インポート元アップロードZIPファイル展開ディレクトリ
	 * @return インポートされたクラスのリスト
	 */
	public ImportTrackdataResult importTrackdata(
			Boolean doImport,
			File trackdataExportXsl,
			Long localgovinfoidSrc,
			Long localgovinfoidDest,
			Long trackdataidSrc,
			Long trackdataidDest,
			String [] updateTrackTableDatas,
			String sameTrackTableDatas,
			File uploadZipFileDir){

		// インポート結果
		ImportTrackdataResult importTrackdataResult = new ImportTrackdataResult();

		// 設定をインポートするクラスを外部キー依存関係順で並び替え
		importTrackdataResult.importClasses = getClassesOrderbyDependency(ExportService.EntityType.data);

		// インポート先自治体IDを指定した場合は存在チェック
		LocalgovInfo localgovInfoDest = null;
		if(localgovinfoidDest!=null) {
			localgovInfoDest = localgovInfoService.findById(localgovinfoidDest);
			if(localgovInfoDest==null) {
				throw new ServiceException(lang.__("Local gov. ID:{0} of import destination not exist.", localgovinfoidDest));
			}
		}

		try {

			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(trackdataExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			// インポート元の自治体情報の読み込み
			importTrackdataResult.localgovinfoidSrc = localgovinfoidSrc;
			// インポート先の自治体情報の読み込み(なければ作成)
			importTrackdataResult.localgovinfoidDest = localgovInfoDest.id;
			// 自治体IDのマッピング
			{
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put(localgovinfoidSrc, localgovInfoDest.id);
				logger.debug(lang.__("Disaster data of local gov. ID : {0} import to {1}.", localgovinfoidSrc, localgovInfoDest.id));
				importTrackdataResult.idConverter.put("localgovinfoid", map);
			}

			Map<String,Map<String,String>> targetIdMap = new HashMap<String, Map<String,String>>();
			if(updateTrackTableDatas != null && updateTrackTableDatas.length > 0){
				for(int i = 0; i < updateTrackTableDatas.length; i++){
					String [] tempArray = updateTrackTableDatas[i].split("-");
					Map<String, String> innerMap;
					if(targetIdMap.containsKey(tempArray[0])){
						innerMap = targetIdMap.get(tempArray[0]);
					}else{
						innerMap = new HashMap<String, String>();
					}
					innerMap.put(tempArray[1], tempArray[2]);
					targetIdMap.put(tempArray[0], innerMap);
				}
			}


			Map<String, List<String>> sameTrackTableDatasMap = new HashMap<String, List<String>>();
			if(sameTrackTableDatas != null && sameTrackTableDatas.length() > 0){
				String [] tempArray = sameTrackTableDatas.split(":");
				if(tempArray != null){
					for(int i = 0; i < tempArray.length; i++){
						String [] tempArray2 = tempArray[i].split("-");
						if(tempArray2 != null){
							String tableName = tempArray2[0];
							String [] tempArray3 = tempArray2[1].split(",");
							if(tempArray3 != null){
								sameTrackTableDatasMap.put(tableName,  Arrays.asList(tempArray3));
							}
						}
					}
				}
			}

			// インポート実行
			try {
				excelImportService.importTrackTableBy(
						doImport,
						wb,
						importTrackdataResult,
						importTrackdataResult.localgovinfoidSrc,
						importTrackdataResult.localgovinfoidDest,
						trackdataidSrc,
						trackdataidDest,
						targetIdMap,
						sameTrackTableDatasMap,
						uploadZipFileDir);
			} catch(Exception e) {
				throw new ServiceException(lang.__("An error occurred while importing disaster data."), e);
			}

			return importTrackdataResult;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Import was interrupted."), e);
		}
	}

	/**
	 * 災害データをインポートします.
	 * @param trackdataExportXsl 災害データをエクスポートしたエクセルファイル
	 * @param localgovinfoidDest インポート先自治体ID
	 * @return インポートされたクラスのリスト
	 */
	public ImportTrackdataResult importTrackdata(Boolean doImport, File trackdataExportXsl, Long localgovinfoidSrc, Long localgovinfoidDest) {

		// インポート結果
		ImportTrackdataResult importTrackdataResult = new ImportTrackdataResult();

		if(! doImport){
			return importTrackdataResult;
		}

		// 設定をインポートするクラスを外部キー依存関係順で並び替え
		importTrackdataResult.importClasses = getClassesOrderbyDependency(ExportService.EntityType.data);

		// インポート先自治体IDを指定した場合は存在チェック
		LocalgovInfo localgovInfoDest = null;
		if(localgovinfoidDest!=null) {
			localgovInfoDest = localgovInfoService.findById(localgovinfoidDest);
			if(localgovInfoDest==null) {
				throw new ServiceException(lang.__("Local gov. ID:{0} of import destination not exist.", localgovinfoidDest));
			}
		}

		try {

			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(trackdataExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			// インポート元の自治体情報の読み込み
			importTrackdataResult.localgovinfoidSrc = localgovinfoidSrc;
			// インポート先の自治体情報の読み込み(なければ作成)
			importTrackdataResult.localgovinfoidDest = localgovInfoDest.id;
			// 自治体IDのマッピング
			{
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put(localgovinfoidSrc, localgovInfoDest.id);
				logger.debug(lang.__("Disaster data of local gov. ID : {0} import to {1}.", localgovinfoidSrc, localgovInfoDest.id));
				importTrackdataResult.idConverter.put("localgovinfoid", map);
			}

			// インポート実行
			try {
				excelImportService.importBy(wb, importTrackdataResult, importTrackdataResult.localgovinfoidDest);
			} catch(Exception e) {
				throw new ServiceException(lang.__("An error occurred while importing disaster data."), e);
			}

			return importTrackdataResult;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Import was interrupted."), e);
		}
	}

	public void addTracktableInfoFkeyConst(){
		// tracktable_infoにtablemasterinfoid外部キー制約を付与する

		// 既に存在するかチェック
		List<BeanMap> fkeys = jdbcManager.selectBySql(BeanMap.class, getTracktableInfoFkeySql).getResultList();
		if(fkeys == null || fkeys.size() <= 0){
			jdbcManager.updateBySql(addTracktableInfoFkeySql).execute();
		}
	}

	public void dropTracktableInfoFkeyConst(){
		// tracktable_infoのtablemasterinfoid外部キー制約を削除する
		jdbcManager.updateBySql(dropTracktableInfoFkeySql).execute();
	}


	private void updateImporttablemasterData(Long localgovinfoid, File infoExportXsl){
		try {
			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(infoExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);


			// インポート実行
			try {
				@SuppressWarnings("unchecked")
				List<TablemasterInfo> tablemasterInfoList = (List<TablemasterInfo>)excelImportService.readRecords(wb, TablemasterInfo.class);
				for(TablemasterInfo tablemasterInfo : tablemasterInfoList){
					BeanMap conditions = new BeanMap();
					conditions.put(importtablemasterData().localgovinfoid().toString(), localgovinfoid);
					conditions.put(importtablemasterData().tablemasterinfoid().toString(), tablemasterInfo.id);
					List<ImporttablemasterData> importtablemasterDataList = importtablemasterDataService.findByCondition(conditions);
					ImporttablemasterData entity;
					if(importtablemasterDataList != null && importtablemasterDataList.size() > 0){
						entity = importtablemasterDataList.get(0);
						entity.tablemasterinfoid = tablemasterInfo.id;
						entity.mapmasterinfoid = tablemasterInfo.mapmasterinfoid;
						entity.layerid = tablemasterInfo.layerid;
						entity.tablename = tablemasterInfo.tablename;
						entity.name = tablemasterInfo.name;
						entity.geometrytype = tablemasterInfo.geometrytype;
						//entity.copy = tablemasterInfo.copy;
						entity.addresscolumn = tablemasterInfo.addresscolumn;
						entity.updatecolumn = tablemasterInfo.updatecolumn;
						entity.coordinatecolumn = tablemasterInfo.coordinatecolumn;
						entity.mgrscolumn = tablemasterInfo.mgrscolumn;
						entity.mgrsdigit = tablemasterInfo.mgrsdigit;
						entity.note = tablemasterInfo.note;
						entity.deleted = tablemasterInfo.deleted;
						importtablemasterDataService.update(entity);
					}else{
						entity = new ImporttablemasterData();
						entity.localgovinfoid = localgovinfoid;
						entity.tablemasterinfoid = tablemasterInfo.id;
						entity.mapmasterinfoid = tablemasterInfo.mapmasterinfoid;
						entity.layerid = tablemasterInfo.layerid;
						entity.tablename = tablemasterInfo.tablename;
						entity.name = tablemasterInfo.name;
						entity.geometrytype = tablemasterInfo.geometrytype;
						//entity.copy = tablemasterInfo.copy;
						entity.addresscolumn = tablemasterInfo.addresscolumn;
						entity.updatecolumn = tablemasterInfo.updatecolumn;
						entity.coordinatecolumn = tablemasterInfo.coordinatecolumn;
						entity.mgrscolumn = tablemasterInfo.mgrscolumn;
						entity.mgrsdigit = tablemasterInfo.mgrsdigit;
						entity.note = tablemasterInfo.note;
						entity.deleted = tablemasterInfo.deleted;
						importtablemasterDataService.insert(entity);

					}

				}



			} catch(Exception e) {
				throw new ServiceException(lang.__("An error occurred while importing disaster data."), e);
			}
			wb.close();
		} catch(Exception e) {
			throw new ServiceException(lang.__("Import was interrupted."), e);
		}
	}

	/**
	 * 引数で渡されたList<TracktableInfo>をソートして返す。
	 * @param orgList
	 * @return List<TracktableInfo>
	 */
	private List<TracktableInfo> sortTracktableInfoList(List<TracktableInfo> orgList){
		List<TracktableInfo> returnList = new ArrayList<TracktableInfo>();

		List<TracktableInfo> notLayerList = new ArrayList<TracktableInfo>();
		List<TracktableInfo> layerList = new ArrayList<TracktableInfo>();

		// 地図レイヤのデータとそれ以外に分ける
		for(TracktableInfo tracktableInfo : orgList){
			if(StringUtil.isEmpty(tracktableInfo.layerid)){
				notLayerList.add(tracktableInfo);
			}else{
				layerList.add(tracktableInfo);
			}
		}

		// 地図レイヤではないリストはtablenameでソートしておく
		Collections.sort(notLayerList, new Comparator<TracktableInfo>() {
			public int compare(TracktableInfo o1, TracktableInfo o2) {
				return o1.tablename.compareTo(o2.tablename);
			}
		});

		// 地図レイヤのデータはレイヤIDの数値部分でソートする
		Map<Long, TracktableInfo> layerMap = new TreeMap<Long, TracktableInfo>();
		for(TracktableInfo tracktableInfo : layerList){
			String layerId = tracktableInfo.layerid;
			layerId = layerId.replaceAll("[^0-9]","");
			Long layerIdNum = Long.parseLong(layerId);
			layerMap.put(layerIdNum, tracktableInfo);
		}

		// マージして返却
		for(Map.Entry<Long, TracktableInfo> e: layerMap.entrySet()){
			returnList.add(e.getValue());
		}
		for(TracktableInfo tracktableInfo : notLayerList){
			returnList.add(tracktableInfo);
		}

		return returnList;
	}
}


