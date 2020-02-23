/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto.admin.disconnect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.TrackData;


@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class DisconnectImportConfirmDto implements Serializable{

	private static final long serialVersionUID = 1L;

	public DisconnectImportConfirmDto(){
		importTrackDataList = new ArrayList<ImportTrackData>();
	}

	public List<ImportTrackData> importTrackDataList;
	public List<TrackData> fileTrackDataList;
	public List<TrackData> targetTrackDataList;


	@lombok.Getter @lombok.Setter
	public static class ImportTrackData {

		public ImportTrackData(){
			importLayerList = new ArrayList<ImportLayerInfo>();
			isLayerInfoVisiblel = false;
			importTrackTableDataList = new ArrayList<DisconnectImportConfirmDto.ImportTrackTableData>();
			isTableInfoVisiblel = false;
		}

		/** 新規／更新 */
		public Boolean isUpdate;

		/** インポート元TrackID */
		public Long  fileTrackDataId;

		/** インポート元Track名 */
		public String  fileTrackDataName;

		/** インポート先TrackID */
		public Long  dbTrackDataId;

		/** インポート先Track名 */
		public String  dbTrackDataName;

		/** インポート元地図ID */
		public Long  fileMapId;

		/** インポート先地図ID */
		public Long  dbMapId;

		/** 災害名称 */
		public String  trackDataName;

		/** レイヤリスト */
		public List<ImportLayerInfo> importLayerList;

		/** レイヤリスト表示可否 */
		public Boolean isLayerInfoVisiblel;

		/** レイヤではないテーブルデータ */
		public List<ImportTrackTableData> importTrackTableDataList;

		/** テーブルデータリスト表示可否 */
		public Boolean isTableInfoVisiblel;
}

	public static class ImportLayerInfo {

		public ImportLayerInfo(){
			unmatchFeatureInfoList = new ArrayList<UnmatchFeatureInfo>();
			unmatchCount = 0;
		}

		/** レイヤID */
		public String  layerId;

		/** レイヤ名称 */
		public String  layerName;

		/** アンマッチ数 */
		public int unmatchCount;

		/** アンマッチ情報 */
		public List<UnmatchFeatureInfo>  unmatchFeatureInfoList;

		/** 属性名称リスト */
		public List<String>  attrNamesList;
	}

	public static class UnmatchFeatureInfo {

		public UnmatchFeatureInfo(){
		}

		/** gid */
		public Long gid;

		/** DB側 groupId */
		public String dbGroupId;

		/** DB側 groupName */
		public String dbGroupName;

		/** ファイル側 groupId */
		public String fileGroupId;

		/** ファイル側 groupName */
		public String fileGroupName;

		/** ファイル側属性情報 */
		public List<FileAttrInfo> fileAttrs;

		/** ファイル側添付ファイル情報 */
		public List<String>  fileAttachFiles;

		/** DB側属性情報 */
		public List<String>  dbAttrs;

		/** DB側添付ファイル情報 */
		public List<String>  dbAttachFiles;

		/** チェックボックスのValue */
		public String checkBoxValue;
	}

	public static class FileAttrInfo {
		public String value;

		public Boolean modified;
	}

	public static class ImportTrackTableData{
		public ImportTrackTableData(){
			sameTrackTableDataRecordList = new ArrayList<SameTrackTableDataRecord>();
		}

		public String tableName;

		public String tableComment;

		public List<String> columnCommentList;

		public List<SameTrackTableDataRecord> sameTrackTableDataRecordList;
	}
	public static class SameTrackTableDataRecord{

		public SameTrackTableDataRecord(){
		}

		/** DB側 groupId */
		public String dbGroupId;

		/** DB側 groupName */
		public String dbGroupName;

		/** ファイル側 groupId */
		public String fileGroupId;

		/** ファイル側 groupName */
		public String fileGroupName;

		/** ファイル側データ */
		public List<String> fileRecord;

		/** DB側データ */
		public List<String> dbRecord;

		/** チェックボックスのValue */
		public String checkBoxValue;
	}
}
