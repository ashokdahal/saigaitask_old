/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.disconnect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.admin.MapBackup;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.db.DisasterbuildData;
import jp.ecom_plat.saigaitask.entity.db.DisastercasualtiesData;
import jp.ecom_plat.saigaitask.entity.db.DisasterfarmData;
import jp.ecom_plat.saigaitask.entity.db.DisasterfireData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhospitalData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseholdData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseregidentData;
import jp.ecom_plat.saigaitask.entity.db.DisasterlifelineData;
import jp.ecom_plat.saigaitask.entity.db.DisasterroadData;
import jp.ecom_plat.saigaitask.entity.db.DisasterschoolData;
import jp.ecom_plat.saigaitask.entity.db.DisastersituationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisasterwelfareData;
import jp.ecom_plat.saigaitask.entity.db.HeadofficeData;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.Reportcontent2Data;
import jp.ecom_plat.saigaitask.entity.db.ReportcontentData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.EntityType;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.ExportFileSet;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;


@org.springframework.stereotype.Service
public class TrackDataExportService extends BaseService {

	/** システムマスタバックアップファイルのプレフィックス */
	public static final String PREFIX_MASTER = "saigaitask-master";
	/** 自治体設定バックアップファイルのプレフィックス */
	public static final String PREFIX_INFO   = "saigaitask-info";
	/** 災害データバックアップファイルのプレフィックス */
	public static final String PREFIX_DATA   = "saigaitask-data";
	/** マッピングファイルのプレフィックス */
	public static final String PREFIX_MAPPING   = "saigaitask-mapping";
	/** 履歴ファイルのプレフィックス */
	public static final String PREFIX_HISTORY   = "saigaitask-history";
	/** 危機管理クラウドバージョンファイルのプレフィックス */
	public static final String PREFIX_VERSION   = "saigaitask-version";
	/** 危機管理クラウドOAUTH認証ファイルのプレフィックス */
	public static final String PREFIX_OAUTH   = "saigaitask-oauth";
	/** 危機管理クラウドOAUTH認証ファイルのプレフィックス */
	public static final String PREFIX_UPLOADEDZIP   = "saigaitask-uploadedzip";
	/** マスタマップバックアップファイルのプレフィックス */
	public static final String PREFIX_MASTERMAP = "Master-";

	/** 開発時バージョン文字列（WARから起動しないとjp.ecom_plat.saigaitask.util.Config.getVersion()がNULLを返すため） */
	public static final String VERSION_DEV = "DEVELOP";


	// Logger
	Logger logger = Logger.getLogger(TrackDataExportService.class);

	// Service
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected TrackDataService trackDataService;
	@Resource protected TrackmapInfoService trackmapInfoService;
	@Resource protected ExportService exportService;
	@Resource protected TableService tableService;

	/** ログイン情報 */
	@Resource protected LoginDataDto loginDataDto;
	@Resource protected JdbcManager jdbcManager;

	/**
	 * 通信途絶ツールの処理対象かどうか
	 * @param clazz エンティティクラス
	 * @return boolean
	 */
	public static boolean isUseDisconnect(Class<?> clazz) {
//		if(clazz==TrackData.class) return true;
//		if(clazz==TrackgroupData.class) return true;
//		if(clazz==TrackmapInfo.class) return true;
//		if(clazz==TracktableInfo.class) return true;
		if(clazz==DisastercasualtiesData.class) return true;
		if(clazz==HeadofficeData.class) return true;
		if(clazz==DisasterhouseData.class) return true;
		if(clazz==AssemblestateData.class) return true;
		if(clazz==DisasterroadData.class) return true;
		if(clazz==DisasterlifelineData.class) return true;
		if(clazz==DisasterhospitalData.class) return true;
		if(clazz==DisasterfarmData.class) return true;
		if(clazz==DisasterwelfareData.class) return true;
		if(clazz==DisasterschoolData.class) return true;
		if(clazz==DisasterbuildData.class) return true;
		if(clazz==DisasterfireData.class) return true;
		if(clazz==ReportData.class) return true;
		if(clazz==DisasterhouseholdData.class) return true;
		if(clazz==DisasterhouseregidentData.class) return true;
		if(clazz==Reportcontent2Data.class) return true;
		if(clazz==ReportcontentData.class) return true;
		if(clazz==DisastersummaryhistoryData.class) return true;
		if(clazz==DisastersituationhistoryData.class) return true;
	return false;
	}

	/**
	 * trackdataidを持つエンティティかどうか
	 * @param clazz エンティティクラス
	 * @return boolean
	 */
	public static boolean hasTrackdataid(Class<?> clazz) {
//		if(clazz==TrackData.class) return true;
//		if(clazz==TrackgroupData.class) return true;
//		if(clazz==TrackmapInfo.class) return true;
//		if(clazz==TracktableInfo.class) return true;
		if(clazz==DisastercasualtiesData.class) return true;
		if(clazz==HeadofficeData.class) return true;
		if(clazz==DisasterhouseData.class) return true;
		if(clazz==AssemblestateData.class) return true;
		if(clazz==DisasterroadData.class) return true;
		if(clazz==DisasterlifelineData.class) return true;
		if(clazz==DisasterhospitalData.class) return true;
		if(clazz==DisasterfarmData.class) return true;
		if(clazz==DisasterwelfareData.class) return true;
		if(clazz==DisasterschoolData.class) return true;
		if(clazz==DisasterbuildData.class) return true;
		if(clazz==DisasterfireData.class) return true;
		if(clazz==ReportData.class) return true;
		if(clazz==DisasterhouseholdData.class) return true;
		if(clazz==DisasterhouseregidentData.class) return true;
		if(clazz==Reportcontent2Data.class) return false;
		if(clazz==ReportcontentData.class) return false;
		if(clazz==DisastersummaryhistoryData.class) return true;
		if(clazz==DisastersituationhistoryData.class) return false;
	return false;
	}

	/**
	 * エンティティの時刻カラム名を返却
	 * @param clazz エンティティクラス
	 * @return boolean
	 */
	public static String getTimeColumnName(Class<?> clazz) {
		String registtime = "registtime";
		String meetingtime = "meetingtime";
		String period = "period";
		String reporttime = "reporttime";
//		if(clazz==TrackData.class) return true;
//		if(clazz==TrackgroupData.class) return true;
//		if(clazz==TrackmapInfo.class) return true;
//		if(clazz==TracktableInfo.class) return true;
		if(clazz==DisastercasualtiesData.class) return registtime;
		if(clazz==HeadofficeData.class) return meetingtime;
		if(clazz==DisasterhouseData.class) return registtime;
		if(clazz==AssemblestateData.class) return registtime;
		if(clazz==DisasterroadData.class) return registtime;
		if(clazz==DisasterlifelineData.class) return registtime;
		if(clazz==DisasterhospitalData.class) return registtime;
		if(clazz==DisasterfarmData.class) return registtime;
		if(clazz==DisasterwelfareData.class) return registtime;
		if(clazz==DisasterschoolData.class) return registtime;
		if(clazz==DisasterbuildData.class) return registtime;
		if(clazz==DisasterfireData.class) return registtime;
		if(clazz==ReportData.class) return registtime;
		if(clazz==DisasterhouseholdData.class) return registtime;
		if(clazz==DisasterhouseregidentData.class) return registtime;
		if(clazz==Reportcontent2Data.class) return reporttime;
		if(clazz==ReportcontentData.class) return reporttime;
		if(clazz==DisastersummaryhistoryData.class) return period;
		if(clazz==DisastersituationhistoryData.class) return null;
		return null;
	}

	public static Class<?> getParentEntity(Class<?> clazz){
		if(clazz==Reportcontent2Data.class) return ReportData.class;
		if(clazz==ReportcontentData.class) return ReportData.class;
		if(clazz==DisastersituationhistoryData.class) return DisastersummaryhistoryData.class;

		return null;
	}

	public static List<Class<?>> getChildEntities(Class<?> clazz){
		List<Class<?>> retValue = new ArrayList<Class<?>>();
		if(clazz==ReportData.class){
			retValue.add(ReportcontentData.class);
			retValue.add(Reportcontent2Data.class);
		}
		if(clazz==DisastersummaryhistoryData.class){
			retValue.add(DisastersituationhistoryData.class);
		}

		if(retValue.size() <= 0){
			retValue = null;
		}
		return retValue;
	}

	public static String getParentIdColumnName(Class<?> clazz){
		if(clazz==Reportcontent2Data.class) return "reportdataid";
		if(clazz==ReportcontentData.class) return "reportdataid";
		if(clazz==DisastersituationhistoryData.class) return "disastersummaryhistoryid";

		return null;
	}

	/**
	 * 自治体設定をZIPでエクスポートする
	 * @param localgovinfoid
	 * @param exportAuthInfo
	 * @return
	 */
	public File zip(Long localgovinfoid, boolean exportAuthInfo, boolean exportAttachedFile) {

		// 自治体IDが指定されている場合、存在するかチェックする
		if(localgovinfoid!=null) {
			LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
			if(localgovInfo==null) throw new ServiceException(lang.__("Local gov. ID = {0} not exist.", localgovinfoid));
		}

		// タイムスタンプからZipファイル名を生成
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String zipFileName = "saigaitask-export-trackdata";
		if(localgovinfoid!=null) zipFileName += "-localgovinfoid"+localgovinfoid;
		zipFileName += "-"+timestamp;

		// 作業ディレクトリの作成
		File workingDir = new File(FileUtil.getTmpDir(), zipFileName);
		if(!workingDir.mkdir()) {
			throw new ServiceException(lang.__("Unable to create working directory of export."));
		}

		ExportTrackFileSet exportFileSet = new ExportTrackFileSet();

		// 危機管理クラウドバージョン情報ファイルをエクスポート
		exportFileSet.saigaitaskVersionFile = exportService.exportVersionFile(workingDir, localgovinfoid);

		// システムマスタのエクスポート
		exportFileSet.masterExportXsl = exportService.exportMaster(workingDir, exportAuthInfo);
		// 自治体情報のエクスポート
		exportFileSet.infoExportXsl = exportService.exportInfo(workingDir, localgovinfoid, exportAuthInfo);

		// マスタマップのエクスポート
		List<MappingFileColumns> mappingFileRows = new ArrayList<MappingFileColumns>();
		try {
			MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
			if(mapmasterInfo==null) throw new ServiceException(lang.__("Map master info has not been set."));
			if(mapmasterInfo.mapid==null) throw new ServiceException(lang.__("Map ID of map master info has not been set."));
			MapBackup mapBackup = new MapBackup();
			mapBackup.setFeatureFileBackup(exportAttachedFile);
			File masterMapFile = mapBackup.backupMap(mapmasterInfo.mapid);
			String masterMapFileName = masterMapFile.getName();
			masterMapFileName = PREFIX_MASTERMAP + masterMapFileName;
			File newMasterMapFile = new File(masterMapFile.getParentFile().getAbsolutePath() + File.separator + masterMapFileName);
			masterMapFile.renameTo(newMasterMapFile);
			exportFileSet.mapbackupZip = newMasterMapFile;

			// マッピングファイルの先頭にマスタマップの情報を追加する
			MappingFileColumns mappingFileColumns = new MappingFileColumns();
			mappingFileColumns.localgovinfoid = localgovinfoid;
			mappingFileColumns.trackdataid = 0L;
			mappingFileColumns.mapid = mapmasterInfo.mapid;
			mappingFileColumns.xlsFileName = "";
			mappingFileColumns.mapZipFileName = newMasterMapFile.getName();
			mappingFileColumns.trackdataname = "";
			mappingFileRows.add(mappingFileColumns);
		} catch (Exception e) {
			throw new ServiceException(lang.__("Unable to export master map."), e);
		}


		// 起動中の災害を取得
		List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
//		List<MappingFileColumns> mappingFileRows = new ArrayList<MappingFileColumns>();

		// 履歴データ格納ディレクトリ作成
//		File historyRootDir = new File(workingDir, PREFIX_HISTORY);
//		if(! historyRootDir.mkdir()){
//			logger.error(loginDataDto.logInfo());
//			throw new ServiceException(lang.__("An export of disaster data was failed because a history storing data directory could not be made."));
//		}

		List<Long> targetTrackDataIdList = new ArrayList<Long>();
		for(TrackData trackData : trackDatas){
			try {
				TrackmapInfo trackmapInfo = trackmapInfoService.findByTrackDataId(trackData.id);

				// 災害情報のエクスポート
				File dataExportXsl = exportData(workingDir, localgovinfoid, trackData.id, exportAuthInfo);
				exportFileSet.dataExportXsls.add(dataExportXsl);

				// 災害地図のエクスポート
//				MapBackup mapBackup = new MapBackup();
//				File mapbackupZip = mapBackup.backupMap(trackmapInfo.mapid);
//				exportFileSet.trackMapbackupZips.add(mapbackupZip);

				// 災害地図上レイヤの履歴テーブルをエクスポート
//				exportLayerHistory(historyRootDir, trackmapInfo.mapid, trackmapInfo.id, trackData.id, localgovinfoid);

				// マッピングファイルの行を作成
				MappingFileColumns mappingFileColumns = new MappingFileColumns();
				mappingFileColumns.localgovinfoid = localgovinfoid;
				mappingFileColumns.trackdataid = trackData.id;
				mappingFileColumns.mapid = trackmapInfo.mapid;
				mappingFileColumns.xlsFileName = dataExportXsl.getName();
				mappingFileColumns.mapZipFileName = "";
				mappingFileColumns.trackdataname = trackData.name;
				mappingFileRows.add(mappingFileColumns);

				targetTrackDataIdList.add(trackData.id);
			} catch (Exception e) {
				throw new ServiceException(lang.__("Can't export disaster map."), e);
			}
		}
		// マッピングファイルをエクスポート
		exportFileSet.xslMapMappingFile = exportMappingFile(workingDir, localgovinfoid, mappingFileRows);

		// 危機管理クラウドOAUTH認証情報ファイルをエクスポート
		exportFileSet.saigaitaskOauthFile = exportOauthFile(workingDir, localgovinfoid);

		// 危機管理クラウドにアップロードされたファイルをエクスポート
		exportFileSet.uploadedZipFile = exportUploadedFile(workingDir, localgovinfoid, targetTrackDataIdList);

		// 履歴データ格納ディレクトリを圧縮し、zipファイルに登録
//		try{
//			// 履歴データファイルが存在する場合のみzipファイルを作成する
//			String [] histryFiles = historyRootDir.list();
//			if(histryFiles != null && histryFiles.length > 0){
//				if(FileUtil.zipDirectory(historyRootDir.getAbsolutePath(), historyRootDir.getAbsolutePath() + ".zip")){
//					File historyDataZipFile = new File(historyRootDir.getAbsolutePath() + ".zip");
//					exportFileSet.historyDataZipFile = historyDataZipFile;
//				}else{
//					logger.error(loginDataDto.logInfo());
//					throw new ServiceException(lang.__("An export of disaster data was failed because a history storing data directory was not put on by an archive."));
//				}
//			}else{
//				exportFileSet.historyDataZipFile = null;
//			}
//		}catch(IOException e){
//			logger.error(loginDataDto.logInfo(),e);
//			throw new ServiceException(lang.__("An export of disaster data was failed because a history storing data directory was not put on by an archive."));
//		}

		File exportZipFile = exportFileSet.zip(zipFileName);

		// 作業ディレクトリの削除
		FileUtil.dirDelete(workingDir);

		// マスター地図バックアップファイルの削除
		if(exportFileSet.mapbackupZip.exists()){
			exportFileSet.mapbackupZip.delete();
		}

		// 災害地図バックアップファイルの削除
		for(File mapFile : exportFileSet.trackMapbackupZips){
			if(mapFile.exists()){
				mapFile.delete();
			}
		}
		return exportZipFile;
	}

	/**
	 * エクスポートZIPの圧縮/解凍クラス
	 */
	public static class ExportTrackFileSet extends ExportFileSet{

		/** 災害データのエクスポートExcelファイル */
		public List<File> dataExportXsls;
		/** 災害マップのバックアップZIPファイルのリスト */
		public List<File> trackMapbackupZips;
		/** 災害データのエクスポートExcelファイルと災害マップのバックアップZIPファイルのマッピング定義ファイル */
		public File xslMapMappingFile;
		/** 履歴データ格納zipファイル */
		public File historyDataZipFile;
		/** OAUTH認証情報格納ファイル */
		public File saigaitaskOauthFile;
		/** アップロードされたファイルのzipファイル */
		public File uploadedZipFile;

		public ExportTrackFileSet(){
			dataExportXsls = new ArrayList<File>();
			trackMapbackupZips = new ArrayList<File>();
		}

		/**
		 * ZIPファイルに圧縮する
		 * @param zipFileName
		 * @return
		 */
		public File zip(String zipFileName) {

			List<File> files = new ArrayList<File>();
			files.add(masterExportXsl);
			files.add(infoExportXsl);
			files.add(mapbackupZip);
			files.addAll(dataExportXsls);
			files.addAll(trackMapbackupZips);
			files.add(xslMapMappingFile);
			files.add(saigaitaskVersionFile);
			files.add(saigaitaskOauthFile);
			if(uploadedZipFile != null){
				files.add(uploadedZipFile);
			}
			if(historyDataZipFile != null){
				files.add(historyDataZipFile);
			}

			File zipFile = FileUtil.zip(zipFileName, (File[])files.toArray(new File[0]) );
			return zipFile;
		}

		/**
		 * ZIPファイルを解凍する
		 * @param zipFile ZIPファイル
		 * @return
		 * @throws IOException
		 */
		public static ExportTrackFileSet unzip(File zipFile) throws IOException {
			ExportTrackFileSet exportFileSet = new ExportTrackFileSet();
			List<File> files = FileUtil.unzip(zipFile, "Windows-31J", true);
			for(File file : files) {
				String fileName = file.getName();
				// システムマスタ
				if(fileName.startsWith(PREFIX_MASTER)) {
					exportFileSet.masterExportXsl = file;
				}
				// 自治体情報
				else if(fileName.startsWith(PREFIX_INFO)) {
					exportFileSet.infoExportXsl = file;
				}
				// 災害データ
				else if(fileName.startsWith(PREFIX_DATA)) {
					exportFileSet.dataExportXsls.add(file);
				}
				// マスタマップ
				else if(fileName.startsWith(PREFIX_MASTERMAP)) {
					exportFileSet.trackMapbackupZips.add(file);
				}
				// マッピングファイル
				else if(fileName.startsWith(PREFIX_MAPPING)) {
					exportFileSet.xslMapMappingFile = file;
				}
				// 履歴データ格納zipファイル
				else if(fileName.startsWith(PREFIX_HISTORY)) {
					exportFileSet.historyDataZipFile = file;
				}
				// 危機管理クラウドバージョン格納ファイル
				else if(fileName.startsWith(PREFIX_VERSION)) {
					exportFileSet.saigaitaskVersionFile = file;
				}
				// 危機管理クラウドOAUTH認証情報格納ファイル
				else if(fileName.startsWith(PREFIX_OAUTH)) {
					exportFileSet.saigaitaskOauthFile = file;
				}
				// 危機管理クラウドアップロードファイル
				else if(fileName.startsWith(PREFIX_UPLOADEDZIP)) {
					exportFileSet.uploadedZipFile = file;
				}
			}
			return exportFileSet;
		}
	}

	public File exportData(File workingDir, Long localgovinfoid, Long trackdataid, boolean exportAuthInfo) {
		try {
			// initialize
			HSSFWorkbook wb = new HSSFWorkbook();
			export(wb, EntityType.data, localgovinfoid, trackdataid, exportAuthInfo);

			// 一時ファイルに保存
			String filename = PREFIX_DATA;
			if(localgovinfoid!=null) filename += "-localgovinfoid"+localgovinfoid;
			if(trackdataid!=null) filename += "-trackdataid"+trackdataid;
			File xlsFile = new File(workingDir, filename+".xls");
			logger.debug("temp: "+xlsFile.getAbsolutePath());
			FileOutputStream xlsOut = new FileOutputStream(xlsFile);
			try {
				wb.write(xlsOut);
			} finally {
				xlsOut.close();
			}
			return xlsFile;
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}

//	private void exportLayerHistory(File historyRootDir, Long mapId, Long trackmapInfoId, Long trackdataId,  Long localgovinfoid){
//
//		String ceparator = ",";
//		try{
//			// 班情報をDBから取得しておく
//			List<GroupInfo> groupInfos = groupInfoService.findByLocalgovinfoid(localgovinfoid);
//			Map<Long,GroupInfo> groupInfoMap = new HashMap<Long,GroupInfo>();
//			for(GroupInfo groupInfo : groupInfos){
//				groupInfoMap.put(groupInfo.id, groupInfo);
//			}
//
//
//			File historyMapDir = new File(historyRootDir, Long.toString(mapId));
//			if(! historyMapDir.mkdir()){
//				// TODO:例外
//			}
//
//			// 履歴テーブルを取得する
//			List<HistorytableInfo> historytableInfos = historyTableInfoService.findByTrackmapInfoId(trackmapInfoId);
//			for(HistorytableInfo historytableInfo : historytableInfos){
//				String layerId;
//				int hIndex = historytableInfo.historytablename.indexOf("_history");
//				layerId = historytableInfo.historytablename.substring(0, hIndex);
//				// 履歴データファイルを作成
//				File historyDataFile = new File(historyMapDir, layerId + "_history.csv");
//				if(historyTableService.checkGroupidColumnAndAdd(historytableInfo.historytablename, false)){
//					List<List<BeanMap>> historyRecordsGroup = historyTableService.getLogByTrackdataidGroupById(historytableInfo, trackdataId);
//					for(List<BeanMap> historyRecords : historyRecordsGroup){
//						if(historyRecords != null && historyRecords.size() > 0){
//							BeanMap historyRecord = historyRecords.get(0);
//							String gid = "";
//							String id = "";
//							if(historyRecord.containsKey("gid")){
//								gid = Long.toString((Long)historyRecord.get("gid"));
//							}else{
//								if(historyRecord.containsKey("id")){
//									id = Long.toString((Long)historyRecord.get("id"));
//								}
//							}
//							String groupId = (String)historyRecord.get("groupid");
//							if(groupId == null || groupId.length() <= 0){
//								groupId = "";
//							}
//
//							String groupName;
//							if(groupId != null && groupId.length() > 0){
//								groupName = groupInfoMap.get(Long.parseLong(groupId)).name;
//							}else{
//								groupName = "";
//							}
//
//							FileWriter fw = new FileWriter(historyDataFile, true);
//							PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
//							pw.print(gid);
//							pw.print(ceparator);
//							pw.print(groupId);
//							pw.print(ceparator);
//							pw.print(groupName);
//							pw.print(ceparator);
//							pw.print(id);
//							pw.println();
//							pw.close();
//						}
//					}
//				}
//			}
//
//		}catch(IOException e){
//
//		}
//	}

	public File exportMappingFile(File workingDir, Long localgovinfoid, List<MappingFileColumns> mappingFileRows ) {
		try {

			String ceparator = ",";
			// 一時ファイルに保存
			String filename = PREFIX_MAPPING;
			if(localgovinfoid!=null) filename += "-localgovinfoid"+localgovinfoid;
			File csvFile = new File(workingDir, filename+".csv");
			logger.debug("temp: "+csvFile.getAbsolutePath());

            FileWriter fw = new FileWriter(csvFile, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            for(MappingFileColumns mappingFileColumns : mappingFileRows){
            	pw.print(mappingFileColumns.localgovinfoid);
            	pw.print(ceparator);
            	pw.print(mappingFileColumns.trackdataid);
            	pw.print(ceparator);
            	pw.print(mappingFileColumns.mapid);
            	pw.print(ceparator);
            	pw.print(mappingFileColumns.xlsFileName);
            	pw.print(ceparator);
            	pw.print(mappingFileColumns.mapZipFileName);
            	pw.print(ceparator);
            	pw.print(mappingFileColumns.trackdataname);
                pw.println();
            }
            pw.close();
			return csvFile;
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}

	public File exportUploadedFile(File workingDir, Long localgovinfoid, List<Long> trackDataIdList) {
		try {

			String dirPath = application.getRealPath("/upload/");
			dirPath += "/"+(localgovinfoid.toString()+"/");
			File localIdDir = new File(dirPath);
			if(localIdDir.exists()){
				// 作業ディレクトリ作成
				String dirname = PREFIX_UPLOADEDZIP;
				if(localgovinfoid!=null) dirname += "-localgovinfoid"+localgovinfoid;
				String zipFileName = dirname + ".zip";
				String zipFilePath = workingDir.getAbsolutePath() + File.separator + zipFileName;

				File zipWorkDirLevel0 = new File(workingDir, dirname);
				if(!zipWorkDirLevel0.exists()){
					zipWorkDirLevel0.mkdirs();
				}

				File zipWorkDirLevel1 = new File(zipWorkDirLevel0,localgovinfoid.toString());
				if(!zipWorkDirLevel1.exists()){
					zipWorkDirLevel1.mkdirs();
				}



				for(Long trackDataId : trackDataIdList){
					String trackDataDirPath = dirPath + (trackDataId.toString()+"/");
					File trackDataDir = new File(trackDataDirPath);
					if(trackDataDir.exists()){
						// 作業ディレクトリにコピーする
						Boolean result = FileUtil.directoryCopy(trackDataDir, zipWorkDirLevel1);
						if(!result){
							throw new ServiceException(lang.__("Failed to export."));
						}
					}
				}

				Boolean zipFileCreated = FileUtil.zipDirectory(zipWorkDirLevel0.getAbsolutePath(), zipFilePath);
				if(zipFileCreated){
					return new File(zipFilePath);
				}else{
					return null;
				}
			}else{
				return null;
			}
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}


	public File exportOauthFile(File workingDir, Long localgovinfoid ) {
		try {
			// 一時ファイルに保存
			String filename = PREFIX_OAUTH;
			if(localgovinfoid!=null) filename += "-localgovinfoid"+localgovinfoid;
			File txtFile = new File(workingDir, filename+".txt");
			logger.debug("temp: "+txtFile.getAbsolutePath());

			String oAuthConsumerKey = "";
			String oAuthConsumerSecretKey = "";
			//  consumer key と consumer cecret key を取得する
			List<BeanMap> result = tableService.selectAll("_oauth_consumer");
			String appName = lang.__("NIED disaster information sharing system surrogate server");
			for (BeanMap row : result) {
				JSONObject json = new JSONObject((String)row.get("settings"));
				if (appName.equals(json.get("description"))) {
					oAuthConsumerKey = (String)row.get("consumerKey");
					oAuthConsumerSecretKey = (String)row.get("consumerKeySecret");
					break;
				}
//				if (appName.equals(json.get("description"))) {
//					JSONObject saigaitaskJson = json.getJSONObject("SaigaiTask");
//					if(saigaitaskJson == null){
//						oAuthConsumerKey = (String)row.get("consumerKey");
//						oAuthConsumerSecretKey = (String)row.get("consumerKeySecret");
//					}else{
//						Long dbLocalgovinfoid = saigaitaskJson.getLong("localgovinfoid");
//						Boolean isDisconnectapp = saigaitaskJson.getBoolean("disconnectapp");
//						if(dbLocalgovinfoid != null && isDisconnectapp != null){
//							if((!isDisconnectapp) && (dbLocalgovinfoid.equals(localgovinfoid) )){
//								oAuthConsumerKey = (String)row.get("consumerKey");
//								oAuthConsumerSecretKey = (String)row.get("consumerKeySecret");
//							}
//						}
//					}
//					break;
//				}
			}

            FileWriter fw = new FileWriter(txtFile, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
        	pw.print(oAuthConsumerKey +","+oAuthConsumerSecretKey);
            pw.close();
			return txtFile;
		} catch (JSONException je) {
			logger.error(loginDataDto.logInfo(), je);
			throw new ServiceException(lang.__("Failed to export."), je);
		} catch(Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(lang.__("Failed to export."), e);
		}
	}

	public void export(HSSFWorkbook wb, EntityType type, Long localgovinfoid, Long trackdataid, boolean exportAuthInfo) {
		// エンティティ
		List<Class<?>> classes = new ArrayList<Class<?>>(ExportService.entitys.get(type));
//		List<Class<?>> classes = getClassesOrderbyDependency(type);


		List<Class<?>> exported = new ArrayList<Class<?>>();
		List<Class<?>> notexported = new ArrayList<Class<?>>();
		List<Class<?>> skipped = new ArrayList<Class<?>>();
		for(Class<?> entity : classes) {
			try {
				if(!exportAuthInfo && ExportService.isAuthTable(entity)) {
					skipped.add(entity);
				}
				else {
					appendTableSheet(wb, entity, localgovinfoid, trackdataid, exportAuthInfo);
					exported.add(entity);
				}
			} catch(Exception e) {
				logger.warn("cannot export "+entity.getSimpleName());
				notexported.add(entity);
			}
		}
		exportService.appendExportResultSheet(wb, type, classes, exported, notexported, skipped);
		// log
		if(0<notexported.size()) {
			logger.warn("not export classes("+notexported.size()+"): "+notexported.toString());
		}
		logger.debug("export classes("+exported.size()+"): "+exported.toString());
	}

	/**
	 * テーブルのエクスポートシートを追加
	 * @param type
	 */
	protected void appendTableSheet(HSSFWorkbook wb, Class<?> clazz, Long localgovinfoid, Long trackdataid, boolean exportAuthInfo) {

		// Table アノテーションがあるかチェック
		Table table = clazz.getAnnotation(Table.class);
		if(table==null || StringUtil.isEmpty(table.name())) {
			return;
		}

		String tablename = table.name();

		// テーブル名長が30を超えているものは対応できない。。
		if(tablename.length() > 30){
			return;
		}

		//ファイルを開く
		try {
			// CSVシートにデータを書き込み
			final String SHEET_NAME=tablename;
			HSSFSheet sheet = wb.getSheet(SHEET_NAME);
			if(sheet==null) sheet = wb.createSheet(SHEET_NAME);
			int rowIdx = 0;

			// ヘッダの定義
			Map<String, Field> fields = new HashMap<String, Field>();
			List<String> columns = new ArrayList<String>();
			for(Field field : clazz.getFields()) {
				Column column = field.getAnnotation(Column.class);
				if(column!=null) {
					String name = field.getName();
					columns.add(name);
					fields.put(name, field);
				}
			}

			// ヘッダの出力
			int cellNum = 0;
			if(true) {
				HSSFRow row = sheet.createRow(rowIdx++);
				int cellIdx = 0;
				for(String column : columns) {
					row.createCell(cellIdx++).setCellValue(new HSSFRichTextString(column));
				}
				cellNum = cellIdx;
			}

			// データを取得
			List<?> result = null;
//			SimpleWhere where = new SimpleWhere();
			if(localgovinfoid!=null) {
					// Service クラス
					String serviceClassName = clazz.getSimpleName()+"Service";
					//String serviceComponentName = StringUtil.decapitalize(serviceClassName);

					Object service = null;
					try {
						Class<?> serviceClass = null;
						// TrackgroupDataの場合
						if(clazz.getSimpleName().equals(TrackgroupData.class.getSimpleName())){
							serviceClass = Class.forName("jp.ecom_plat.saigaitask.service."+serviceClassName);
						}else{
							serviceClass = Class.forName("jp.ecom_plat.saigaitask.service.db."+serviceClassName);
						}
						service = SpringContext.getApplicationContext().getBean(serviceClass);
					} catch(Exception e) {
						logger.warn(e);
					}

					try{
						if(service instanceof TrackDataService){
							result = new ArrayList<TrackData>(Arrays.asList(trackDataService.findById(trackdataid)));
						}else{
							try{
								Method findByTrackDataId = service.getClass().getMethod("findByTrackDataId",Long.class);
								Class<?> returnType = findByTrackDataId.getReturnType();
								Object obj = findByTrackDataId.invoke(service, new Object[]{ new Long(trackdataid) });
								if(returnType.getName().equals(List.class.getName())){
									result = (List<?>)obj;
								}else{
									List<Object> tempList = new ArrayList<Object>();
									tempList.add(obj);
									result = tempList;
								}
							}catch(Exception e){
								throw new ServiceException("cannot export "+tablename+" by localgovinfoid="+localgovinfoid,e);
							}
						}

					}catch(Exception e){
						logger.warn("cannot export "+tablename+" by localgovinfoid="+localgovinfoid + " cause Service class do not have findByTrackDataId(Long)");
					}
			}
			if(result==null) {
			}
//			if(result==null) {
//				AutoSelect<?> select = jdbcManager.from(clazz);
//				if(0<where.getParams().length) select.where(where);
//				result = select.orderBy("id").getResultList();
//				logger.debug("SELECT "+clazz.getSimpleName()+" order by id");
//			}

			// 認証情報カラム
			Map<String, Boolean> isAuthColumn = new HashMap<String, Boolean>();
			for(String column : columns) {
				isAuthColumn.put(column, ExportService.isAuthColumn(clazz, column));
			}

			// データの出力
			if(result != null){
				for(Object record : result) {
					HSSFRow row = sheet.createRow(rowIdx++);
					int cellIdx = 0;
					// ヘッダとずれないように、ヘッダ定義から値を取得する
					for(String column : columns) {
						// 値を取得
						String value = null;
						Field field = fields.get(column);
						Object val = field.get(record);
						value = val!=null ? val.toString() : null;

						// セルへ書き込み
						HSSFCell cell = row.createCell(cellIdx++);

						// 認証情報を出力しない場合は、値を出力しない
						if(!exportAuthInfo && isAuthColumn.get(column)) {
							continue;
						}
						// 値を出力する
						if(value==null) {
							cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
						}
						else if(StringUtil.isNumber(value)) {
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Double.parseDouble(value));
						}
						// TODO: boolean output
						//cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
						else {
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(new HSSFRichTextString(value));
						}
					}
				}

				// フィルター(not working)
				//sheet.setAutoFilter(new CellRangeAddress(sheet.getFirstRowNum(), sheet.getLastRowNum(), 0, cellNum));

				// 幅を自動調整
				for(int idx=0; idx<cellNum; idx++) {
					sheet.autoSizeColumn(idx);
				}
			}

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * マッピングファイルのCSV項目定義
	 */
	public static class MappingFileColumns {
		/** 自治体ID */
		public Long localgovinfoid;

		/** 記録データID */
		public Long trackdataid;

		/** 地図ID */
		public Long mapid;

		/** 災害データエクセルファイル名 */
		public String xlsFileName;

		/** 地図バックアップファイル名 */
		public String mapZipFileName;

		/** 災害名 */
		public String trackdataname;

		public static final int idxLocalgovinfoid = 0;
		public static final int idxTrackdataid = 1;
		public static final int idxMapid = 2;
		public static final int idxXlsFileName = 3;
		public static final int idxMapZipFileName = 4;
		public static final int idxTrackDataName = 5;
	}
}
