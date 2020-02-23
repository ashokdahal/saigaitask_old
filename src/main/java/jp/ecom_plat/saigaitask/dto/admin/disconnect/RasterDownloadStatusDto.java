/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto.admin.disconnect;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.util.FileUtil;

/**
 * ラスタ画像ダウンロード状況管理Dto
 * アプリケーションスコープ
 */

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component

public class RasterDownloadStatusDto {

	Logger logger = Logger.getLogger(RasterDownloadStatusDto.class);

	/** すべての状況をこの変数で管理する（シングルトン） */
	static private Map<Long, List<RasterDownloadStatus>> rasterDownloadStatusDataStore = new HashMap<Long, List<RasterDownloadStatus>>();

	/**
	 * ラスタ画像ダウンロード状況クラス
	 */
	@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component
	public static class RasterDownloadStatus {

		/** ダウンロード実行ユーザ */
		final public LoginDataDto loginDataDto;

		/** ダウンロード開始時刻 */
		final public Date starttime = new Date();

		/** ダウンロード更新時刻 */
		protected Date updatetime;

		/** ダウンロード終了時刻 */
		protected Date endtime;

		/** タイル枚数 */
		public Double pixCalcSum;

		/** ダウンロード済みタイル枚数 */
		public Double downloadedPixCalcSum = Double.valueOf(0);

		/** ダウンロード結果 */
		public boolean result = false;

		/** ダウンロード用ZIPファイル名 */
		public String zipfilename;

		/** エラーメッセージ */
		public List<String> errorList = new ArrayList<String>();

		/**
		 * キャンセルフラグ
		 * 処理を中止させる場合にtrueにする
		 */
		public boolean cancel;

		/**
		 * ダウンロード状況
		 * @param loginDataDto
		 * @param pixCalcSum
		 */
		public RasterDownloadStatus(LoginDataDto loginDataDto/*, Double pixCalcSum*/) {
			this.loginDataDto = loginDataDto;
//			this.pixCalcSum = pixCalcSum;
		}

		/**
		 * @return 進捗状況をパーセンテージで取得
		 */
		public String getProggress() {
			int digits = 0;
			double proggress = (double)(downloadedPixCalcSum/pixCalcSum)*100;
			if(digits==0) proggress = Math.floor(proggress); // 切捨て
			return String.format("%."+digits+"f", proggress);
		}

		/**
		 * ダウンロードカウントアップ
		 */
		public void countDownload() {
			downloadedPixCalcSum++;
			updatetime = new Date();
		}

		/**
		 * @return ダウンロード用Zipファイル
		 */
		public File getZipFile() {
			File tmpDir = FileUtil.getTmpDir();
			File zipFile = new File(tmpDir, zipfilename);
			return zipFile;
		}

		/**
		 * @return ダウンロード用Zipファイルを削除
		 */
		public boolean deleteZipfile() {
			File zipFile = getZipFile();
			if(zipFile.exists()){
				return zipFile.delete();
			}
			return false;
		}
	}

	/**
	 * データストアに登録
	 * @param loginDataDto
	 * @param pixCalcSum
	 * @return ラスタ画像ダウンロード状況クラス
	 */
	public RasterDownloadStatus regist(LoginDataDto loginDataDto, Double pixCalcSum) {
		// 初期化
		RasterDownloadStatus status = new RasterDownloadStatus(loginDataDto/*, pixCalcSum*/);
		status.pixCalcSum = pixCalcSum;

		// データストアから自治体別のListを取得
		Long localgovinfoid = Long.valueOf(status.loginDataDto.getLocalgovinfoid());
		List<RasterDownloadStatus> rasterDownloadStatusList = rasterDownloadStatusDataStore.get(localgovinfoid);
		// DataStore 初期化
		if(rasterDownloadStatusList==null) {
			rasterDownloadStatusList = new ArrayList<RasterDownloadStatus>();
			rasterDownloadStatusDataStore.put(localgovinfoid, rasterDownloadStatusList);
		}

		// データストアに登録
		rasterDownloadStatusList.add(status);

		return status;
	}

	/**
	 * ダウンロードリストを取得
	 * @param localgovinfoid
	 * @return ダウンロードリスト
	 */
	public List<RasterDownloadStatus> list(long localgovinfoid) {
		List<RasterDownloadStatus> list = rasterDownloadStatusDataStore.get(localgovinfoid);
		return list!=null ? list : new ArrayList<RasterDownloadStatus>();
	}

	/**
	 * データストアから削除
	 * @param status
	 * @return 削除結果
	 */
	public boolean unregist(RasterDownloadStatus status) {
		boolean unregist  = false;
		// データストアから自治体別のListを取得
		Long localgovinfoid = Long.valueOf(status.loginDataDto.getLocalgovinfoid());
		List<RasterDownloadStatus> rasterDownloadStatusList = rasterDownloadStatusDataStore.get(localgovinfoid);
		if(rasterDownloadStatusList!=null && status!=null) {
			unregist = rasterDownloadStatusList.remove(status);
		}
		return unregist;
	}

	/**
	 * zipファイル名からステータス取得
	 * @param localgovinfoid
	 * @param zipfilename
	 * @return status
	 */
	public RasterDownloadStatus findByZipfilename(long localgovinfoid, String zipfilename) {
		List<RasterDownloadStatus> rasterDownloadStatusList = rasterDownloadStatusDataStore.get(localgovinfoid);
		if(rasterDownloadStatusList!=null) {
			for(RasterDownloadStatus rasterDownloadStatus : rasterDownloadStatusList) {
				if(zipfilename.equals(rasterDownloadStatus.zipfilename)) {
					return rasterDownloadStatus;
				}
			}
		}
		return null;
	}

	/**
	 * アプリケーション終了時の処理
	 * すべてのダウンロード用ZIPファイルを削除
	 */
	public void destroy() {
		if(0<rasterDownloadStatusDataStore.size()) {
			logger.info("Destroy RasterDownloadStatusDto start");
			for(Map.Entry<Long, List<RasterDownloadStatus>> entry : rasterDownloadStatusDataStore.entrySet()) {
				Long localgovinfoid = entry.getKey();
				List<RasterDownloadStatus> rasterDownloadStatusList = entry.getValue();
				if(rasterDownloadStatusList!=null) {
					int count = 1;
					for(RasterDownloadStatus rasterDownloadStatus : rasterDownloadStatusList) {
						// 未完了ならキャンセル
						if(!rasterDownloadStatus.result) rasterDownloadStatus.cancel = false;
						else {
							File file = rasterDownloadStatus.getZipFile();
							logger.info("deleting raster download zip file: localgovinfoid="+localgovinfoid+"["+count+"/"+rasterDownloadStatusList.size()+"]"+file.getName()+"("+FileUtil.getSizeStr(file.length())+")");
							rasterDownloadStatus.deleteZipfile();
							count++;
						}
					}
				}
			}
			logger.info("Destroy RasterDownloadStatusDto end");
		}
	}
}
