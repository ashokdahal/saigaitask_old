/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.disconnect;


import org.springframework.web.multipart.MultipartFile;
import org.seasar.struts.annotation.LongType;

@lombok.Getter @lombok.Setter
public class InitImportExportForm {
	@LongType
	public String id = "";

	//自治体関連
	public String selectLocalgov;

	@LongType
	public String localgovinfoid;

	/**
	 * インポートモード
	 * 0: インポートしない
	 * 1: 災害データのみインポート
	 * 2: 災害データと自治体設定のインポート
	 * 現状は1固定
	 */
	public Integer importmode = 1;

	/** 災害データファイル */
	public MultipartFile trackDataFile;

	public String [] updateFeatures;

	public String [] updateTrackTableDatas;
	public String updateTrackTableDatasHidden;

	/** アップロードされたファイルの保存ディレクトリ */
	public String uploadTrackDataDirName;

	/** アップロードされたファイルのファイル名 */
	public String uploadTrackDataFileName;


	/** インポートステータス
	 * 0:インポートファイルアップロード
	 * 1:インポート対象確認
	 * 2:インポート実行
	 * */
	public String importExecStatus;

	public String hasError;

	public String [] selectFileTrackMultibox;
	public String [] selectDbTrackMultibox;
	public String trackMapText;
	
	/** 添付ファイル
	 * 0:添付ファイルを含ない
	 * 1:添付ファイルを含める
	 */
	public String exportAttachedFile = "1";
	public String importAttachedFile = "1";
	
	/** 履歴データ
	 * 0:最新のみ
	 * 1:すべて
	 */
	public String importAll = "1";
}
