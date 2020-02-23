/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import jp.ecom_plat.saigaitask.action.ServiceException;

/**
 * ファイルを読み書きするサービスクラスです.
 */
@org.springframework.stereotype.Service
public class FileService extends BaseService {

	Logger logger = Logger.getLogger(FileService.class);

	/** データディレクトリパス */
	public static final String DATA_DIR = "/WEB-INF/data";

	/** 公共情報コモンズの発信XMLディレクトリ名 */
	public static final String PUBLICCOMMONS_DIR = "publiccommons";
	/** 報告用出力ファイル格納用ディレクトリ名 */
	public static final String REPORT_DIR = "report";
	/** 履歴のCSV,PDFファイル格納用ディレクトリ名 */
	public static final String HISTORY_DIR = "history";

	/**
	 * @return xmlファイル格納用のルートディレクトリのパス
	 */
	public String getXmlRoot(){
		return application.getRealPath("/WEB-INF"+"/jmaxml");
	}

	/**
	 * @return テンプレートファイル格納用のルートディレクトリのパス
	 */
	public String getTemplateRoot(){
		return application.getRealPath("/WEB-INF"+"/template");
	}

	/**
	 * Webアプリ(RealPath)からの相対パスを取得する.
	 * @param file Webアプリ内のファイル
	 * @return / から始まらない相対パスを取得する。
	 *         相対パスが取得できなければ、null を返す
	 */
	public String getFileRealRelativePath(File file) {

		// Webアプリまでのパスを取得
		String realPath = application.getRealPath("/");

		// 絶対パスが RealPath と一致していれば
		String absolutePath = file.getAbsolutePath();
		if(absolutePath.startsWith(realPath)) {
			// RealPath を除いた パスを取得
			String realRelativePath = absolutePath.substring(realPath.length());

			// 先頭の / は削除する
			realRelativePath = realRelativePath.replaceAll("^/*", "");

			return realRelativePath;
		}
		else {
			logger.warn(lang.__("Because it does not start from RealPath, unable to get the relative path.\"")+absolutePath+"\"");
			return null;
		}
	}

	/**
	 * Webアプリ(RealPath)からの相対パスを取得する.
	 * @param realRelativePath Webアプリ内のファイルパス
	 * @return File
	 */
	public File getFileBy(String realRelativePath) {
		// Webアプリまでのパスを取得
		String realPath = application.getRealPath("/");
		return new File(realPath+realRelativePath);
	}

	/**
	 * 新規ファイルを生成します.
	 * 親ディレクトリがない場合は生成します.
	 * @param file
	 * @return created
	 */
	public boolean createFile(File file) {
		boolean created = false;
		try {
			if(file.getParentFile().exists()==false) {
				boolean createdParentDirectory = file.getParentFile().mkdirs();
				if(createdParentDirectory==false) return false;
			}
			created = file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return created;
	}

	/**
	 * 
	 * @param localgovinfoid
	 * @param filename ファイル名: <id>-<pagetype>-<listid>-<timestamp>.<suffix>
	 * @param suffix
	 * @return File
	 */
	public File createHistoryFile(long localgovinfoid, String filename, String suffix) {
		FileService fileService = this;
		File dir = fileService.getFileOnLocalgovDataDir(localgovinfoid, FileService.HISTORY_DIR);
		File file = new File(dir, filename+"."+suffix);
		if(!fileService.createFile(file)) {
			throw new ServiceException(lang.__("Unable to create {0}file \"{1}\" for history.", suffix, file.getName()));
		}
		return file;
	}

	/**
	 * 自治体IDとファイル名を指定して公共情報コモンズ発信XMLファイルを取得します.
	 * @param localgovinfoid
	 * @param fileName
	 * @return File
	 */
	public File getPubliccommonsXMLFile(Long localgovinfoid, String fileName) {
		return getFileOnLocalgovDataDir(localgovinfoid, PUBLICCOMMONS_DIR+"/"+fileName);
	}

	/**
	 * 自治体データディレクトリ内のファイルを取得します.
	 * @param localgovinfoid 自治体ID
	 * @param child 子パス名文字列
	 * @return File
	 */
	public File getFileOnLocalgovDataDir(Long localgovinfoid, String child) {
		File localgovDataDir = getLocalgovDataDir(localgovinfoid);
		return new File(localgovDataDir, child);
	}

	/**
	 * 自治体のデータディレクトリを取得します.
	 * @param localgovinfoid
	 * @return File
	 */
	public File getLocalgovDataDir(Long localgovinfoid) {
		File dataDir = getDataDir();
		return new File(dataDir, localgovinfoid.toString());
	}

	/**
	 * データディレクトリを取得します.
	 * @return File
	 */
	public File getDataDir() {
		String path = application.getRealPath(DATA_DIR);
		return new File(path);
	}
}
