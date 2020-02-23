/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.servlet.ServletContext;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.training.XmlFileEditHelper;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;

/**
 * File操作のユーティリティクラス
 */
public class FileUtil {

	static final Logger logger = Logger.getLogger(FileUtil.class);

	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	static HashMap<String, String> pathInfo;
	//int maxUploadSize = 10000;
	//String uploadEncoding;
	static HashSet<String> allowedExtent;
	
	static {
		//<div lang="ja">Resource 読み込み</div>
		//<div lang="en">Load Resource</div>
		ResourceBundle pathBundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
		//maxUploadSize = Integer.parseInt(pathBundle.getString("MAX_UPLOAD_SIZE"));
		//uploadEncoding = pathBundle.getString("UPLOAD_ENCODING");

		String[] uploadPath = pathBundle.getString("UPLOAD_TYPE").split(",");
		pathInfo = new HashMap<String, String>();
		for (String type : uploadPath) {
			String path = pathBundle.getString(type+"_PATH");
			if (!path.endsWith("/")) path += "/";
			pathInfo.put(type, path);
			logger.info("UploadPath : "+type+" = "+path);
		}

		String[] uploadExts = pathBundle.getString("UPLOAD_EXT").split(",");
		allowedExtent = new HashSet<String>();
		for (String ext : uploadExts) {
			allowedExtent.add(ext);
		}
	}

	public static String getFileBaseName(String fileName) {
		String fileBaseName = "";
		//<div lang="ja">.付き</div>
		//<div lang="en">. position</div>
		int dotIdx = fileName.lastIndexOf('.');
		if (dotIdx > 0) {
			fileBaseName = fileName.substring(0,dotIdx);
		}
		return fileBaseName;
	}
	
	public static String getFileExt(String fileName) {
		String ext = "";
		//<div lang="ja">.付き</div>
		//<div lang="en">. position</div>
		int dotIdx = fileName.lastIndexOf('.');
		if (dotIdx > 0) {
			ext = fileName.substring(dotIdx+1);
		}
		return ext;
	}
	
	public static void assertAllowedExtent(String fileName) {
		// <div lang="ja">PATH名を除くファイル名のみを取得</div>
		//<div lang="en">Get filename without PATH</div>
		logger.info("Uploaded File : "+fileName);

		//<div lang="ja">拡張子取得</div>
		//<div lang="en">Get file extension</div>
		String ext = getFileExt(fileName);

		//<div lang="ja">拡張子チェック</div>
		//<div lang="en">Check extension</div>>
		if (allowedExtent != null) {
			if (!allowedExtent.contains(ext.toLowerCase())) {
				logger.warn("Invalid Ext File : "+fileName);
				//return null;
				throw new ServiceException(lang.__("File\"{0}\" is a file type impossible to be uploaded.", fileName));
			}
		}
	}
	
	/**
	 * テンポラリディレクトリを取得する
	 * @return tmpDir
	 */
	public static File getTmpDir() {
		String tmpDirPath = System.getProperty("java.io.tmpdir");
		if(org.seasar.framework.util.StringUtil.isNotEmpty(tmpDirPath)) {
			File tmpDir = new File(tmpDirPath);
			if(tmpDir.isDirectory()) return tmpDir;
		}
		return null;
	}

	/**
	 * 指定したディレクトリ内のファイル数+1の数字を、指定した桁数の文字列で返す
	 * @param path		ファイル数をカウントするディレクトリのパス
	 * @param format	桁数　ex.3ケタの場合は「%03d」
	 * @return			指定した桁数で文字列化したファイル数
	 * @throws IOException
	 */
	public static String getFileNum(String path, String format) throws IOException{
		File dir = new File(path);
		File[] files = dir.listFiles();
		return String.format(format, files.length);
	}

	/**
	 *
	 * @param application ServletContext
	 * @param govid 自治体ID
	 * @param trackdataid 記録データID
	 * @return ファイルアップロードパスを取得
	 */
	public static String getUploadPath(ServletContext application, Long govid, Long trackdataid)
	{
		String dirPath = application.getRealPath("/upload");
		//自治体ID名のディレクトリの有無のチェック
		if(govid != null){
			if(dirPath.endsWith("/")==false) dirPath += "/";
			dirPath += (govid.toString()+"/");
			File localIdDir = new File(dirPath);
			if(! localIdDir.exists()){
				if(! localIdDir.mkdirs()) return null;
			}else{
				if(! localIdDir.isDirectory()){
					if(! localIdDir.mkdir()) return null;
				}
			}
			//さらにtrackdataidでもディレクトリを作成する
			if(trackdataid!=null){
				dirPath += (trackdataid.toString()+"/");
				File trackIdDir = new File(dirPath);
				if(! trackIdDir.exists()){
					if(! trackIdDir.mkdir()) return null;
				}else{
					if(! trackIdDir.isDirectory()){
						if(! trackIdDir.mkdir()) return null;
					}
				}
			}else{
				logger.error("localgovermentid : "+govid);
				logger.error("trackdataid is NULL");
				throw new ServiceException("trackdataid is NULL");
			}
		}else{
			logger.error("localgovinfoid is NULL");
			throw new ServiceException("localgovinfoid is NULL");
		}
		return dirPath;
	}

	/**
	 * 訓練用ディレクトリを返す
	 * @param application
	 * @param govid
	 * @param trackdataid
	 * @return
	 */
	public static String getUploadTrainingPath(ServletContext application, Long govid)
	{
		String dirPath = application.getRealPath("/upload/");
		//自治体ID名のディレクトリの有無のチェック
		if(govid != null){
			dirPath += "/"+(govid.toString()+"/");
			File localIdDir = new File(dirPath);
			if(! localIdDir.exists()){
				if(! localIdDir.mkdirs()) return null;
			}else{
				if(! localIdDir.isDirectory()){
					if(! localIdDir.mkdir()) return null;
				}
			}
			//さらにtrainingディレクトリを作成する
			dirPath += "training/";
			File trainingDir = new File(dirPath);
			if(! trainingDir.exists()){
				if(! trainingDir.mkdir()) return null;
			}else{
				if(! trainingDir.isDirectory()){
					if(! trainingDir.mkdir()) return null;
				}
			}
		}else{
			logger.error("localgovinfoid is NULL");
			throw new ServiceException("localgovinfoid is NULL");
		}
		return dirPath;
	}

	/**
	 * アップロードされたファイル名を、リネームして登録（日本語ファイル名対応）
	 * @param application	ServletContext
	 * @param govid			自治体ID
	 * @param trackdataid  記録データID
	 * @param file			登録するファイル
	 * @return				登録したファイルのコンテクスト以下のパス文字列
	 */
	public static String upload(ServletContext application, Long govid, Long trackdataid, MultipartFile file)
	{
		String retPath = null;
		do{
			String dirPath = getUploadPath(application, govid, trackdataid);
			if (dirPath == null) break;
			//System.out.println("dirPath : "+dirPath);

			/*
			//現在時刻のミリ秒文字列をファイル名に使う
			String now = String.valueOf(System.currentTimeMillis());
			//拡張子の取得
			//最後の「.」以降の文字のみを取得しているので「.tar.gz」などの場合、「.gz」のみが取得される
			String ext = ImageUtil.getSuffix(file.getName());
			//リネームされたパス名を取得
			dirPath += now + "." + ext;
			*/

			// ファイルの重複チェック
			String fileName = upload_filenameCheck(file, dirPath);
			// 連番をつけて重複対応したファイルを使用する
			dirPath += fileName;
			write(dirPath, file);

			//windows環境だと、uploadよりも上流のpathの区切り文字が「\」になるのでupload以下の文字列を切り出す
			retPath = "/"+dirPath.substring(dirPath.indexOf("upload/"));

		}while(false);

		//System.out.println("retPath : "+retPath);

		return retPath;

		/*
		String path = application.getRealPath("/upload/"
				+ file.getName());
		UploadUtil.write(path, file);

		return "/upload/"+file.getName();
		*/
	}

	/**
     * アップロードされたデータをファイルに書き出します。
     *
     * @param path
     *            ファイルのパス
     * @param formFile
     *            アップロードされたデータ
     */
    public static void write(String path, MultipartFile formFile) {
        if (formFile == null || formFile.getSize() == 0) {
            return;
        }
        BufferedOutputStream out = null;
        InputStream in = null;
        try {
            in = formFile.getInputStream();
            out = new BufferedOutputStream(new FileOutputStream(path));
            InputStreamUtil.copy(in, out);
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            try {
                InputStreamUtil.close(in);
            } finally {
                OutputStreamUtil.close(out);
            }
        }
    }

	/**
	 * アップロードされたファイル名を、リネームして登録（日本語ファイル名対応） 訓練フォルダ用
	 * @param application	ServletContext
	 * @param govid			自治体ID
	 * @param file			登録するファイル
	 * @return				登録したファイルのコンテクスト以下のパス文字列
	 */
	public static String upload_training(ServletContext application, Long govid, MultipartFile file)
	{
		String retPath = null;
		do{
			String dirPath = getUploadTrainingPath(application, govid);
			if (dirPath == null) break;
			// ファイルの重複チェック
			String fileName = upload_filenameCheck(file, dirPath);
			// 連番をつけて重複対応したファイルを使用する
			dirPath += fileName;
			write(dirPath, file);
			//windows環境だと、uploadよりも上流のpathの区切り文字が「\」になるのでupload以下の文字列を切り出す
			int idx = dirPath.indexOf("upload\\/"); // Springに変えてからは path￥upload￥/xxxx のようになった
			if(0<=idx) {
				retPath = "/"+dirPath.substring(dirPath.indexOf("upload\\/"));
				retPath = retPath.replace("upload\\/", "upload/");
			}
			else {
				retPath = "/"+dirPath.substring(dirPath.indexOf("upload/"));
			}
		}while(false);

		return retPath;
	}

	/**
	 * ファイル名重複チェック用関数
	 * @param file
	 * @param dirPath
	 * @return 登録するfilename
	 */
	public static String upload_filenameCheck(MultipartFile file, String dirPath){
		// ファイルの重複チェック
		String fileName = file.getOriginalFilename();
		File fileCheck = new File(dirPath + fileName);
		if(fileCheck.exists()){
			// ファイルの末尾につける連番
			int cnt = 1;
			// ファイル名
			String filePath = "";
			// ファイル拡張子
			String kaku = "";
			// 拡張子付き
			if(fileName.indexOf(".") != -1){
				filePath = fileName.substring(0, fileName.indexOf("."));
				kaku = fileName.substring(fileName.indexOf("."), fileName.length());
			}else{
				filePath = fileName;
			}
			fileName = filePath + cnt + kaku;
			fileCheck = new File(dirPath + fileName);
			while(fileCheck.exists()){
				cnt++;
				fileName = filePath + cnt + kaku;
				fileCheck = new File(dirPath + fileName);
				if(cnt > 1000){
					// 強制終了
					break;
				}
			}
		}
		return fileName;
	}

	/**
	 * サーバからのファイル登録でのファイル名重複チェック
	 * @param filePath ファイルパス
	 * @return リネームしたファイルパス
	 */
	public static String uploadFromServer_fileRename(String filePath){
		File file = new File(filePath);
		//ファイルの重複チェック
		if(file.exists()) {
			//拡張子を取得
			int index = filePath.lastIndexOf(".");
			String ex = filePath.substring(index);
			//拡張子以外のパスを取得
			String renameFilePath = (filePath.substring(0,index));
			//リネームする
			for(int i=1; i<=1000; i++){
				System.out.println(renameFilePath + i + ex);
				//リネームしたファイルの重複チェック
				File renameFile = new File(renameFilePath + i + ex);
				if(!renameFile.exists()) {
					return renameFilePath + i + ex;
				}
			}
		}
		return filePath;
	}

	/**
	 * WEBアプリのディレクトリを基底とする相対パスからFileオブジェクトを取得する.
	 * ディレクトリトラバーサル対応済み
	 * @param relativePathOfWebAppDirBase WEBアプリのディレクトリを基底とする相対パス
	 * @return File
	 */
	public static File getFile(String relativePathOfWebAppDirBase) {
		ServletContext application = SpringContext.getApplicationContext().getBean(ServletContext.class);
		// Protect directory traversal
		String fileRealPath = application.getRealPath(relativePathOfWebAppDirBase.replaceAll("\\.\\.", ""));
		File file = new File(fileRealPath);
		return file;
	}

	/**
	 * ファイルを圧縮します.
	 * @param zipFileName 圧縮ファイル解凍時に生成されるフォルダ名
	 * @param files    圧縮ファイルに追加するファイル
	 * @return ZIPファイル
	 */
	public static File zip(String zipFileName, File... files) {
		ZipOutputStream zos = null;
		InputStream is = null;
		try {
			File zFile = new File(FileUtil.getTmpDir(), zipFileName+".zip");
			zos = new ZipOutputStream(zFile);
			zos.setEncoding("MS932");
			byte[] buf = new byte[1024*1024]; // 1MB
			for(File file : files) {
				ZipEntry entry = new ZipEntry(zipFileName+"/"+file.getName());
				zos.putNextEntry(entry);
				is = new BufferedInputStream(new FileInputStream(file));
				while(true) {
					int len = is.read(buf);
					if (len < 0) break;
					zos.write(buf, 0, len);
				}
			}
			return zFile;
		} catch (IOException e) {
    		Logger.getLogger(FileUtil.class).error(e);
		} finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
		    		Logger.getLogger(FileUtil.class).error(e);
				}
			}
			if(zos!=null) {
				try {
					zos.close();
				} catch (IOException e) {
		    		Logger.getLogger(FileUtil.class).error(e);
				}
			}
		}

		return null;
	}

	/**
	 * ZIPファイルを展開して、Fileリストで返します.
	 * @param zipFile
	 * @param encording
	 * @return ZIPファイルを展開して得られたFileのリスト
	 * @throws IOException
	 */
	public static List<File> unzip(File zipFile, String encording) throws IOException {
		File workingDir = new File(getTmpDir(), UUID.randomUUID().toString());
		if (workingDir.exists() && !workingDir.isDirectory()) {
			throw new ServiceException("working dir already exists.");
		}
		if(!workingDir.mkdirs()) throw new ServiceException("can not create working dir.");

		final InputStream is = new FileInputStream(zipFile);

		ZipArchiveInputStream archive;
		if (encording == null) {
			archive = new ZipArchiveInputStream(is);
		} else {
			archive = new ZipArchiveInputStream(is, encording, true);
		}

		// 展開したファイル
		List<File> files = new ArrayList<File>();

		ZipArchiveEntry entry;
		while ((entry = archive.getNextZipEntry()) != null) {
			File file = new File(workingDir.getAbsolutePath(), entry.getName());
			if (entry.isDirectory()) {
				if(!file.mkdirs()) throw new ServiceException("cannot extract directory: "+entry.getName());
			} else {
				if (!file.getParentFile().exists()) {
					if(!file.getParentFile().mkdirs()) throw new ServiceException("cannot extract file, failed mkdirs: "+entry.getName());
				}
				OutputStream out = null;
				try {
					out = new FileOutputStream(file);
					IOUtils.copy(archive, out);
				} finally {
					if(out!=null) out.close();
				}
			}
			files.add(file);
		}

		archive.close();

		return files;
	}

	/**
	 * ZIPファイルを展開して、Fileリストで返します.
	 * @param zipFile
	 * @param encording
	 * @return
	 * @throws IOException
	 */
	public static List<File> unzip(File zipFile, String encording, boolean unzipSameDir) throws IOException {

		File workingDir;
		if(unzipSameDir){
			workingDir = new File(zipFile.getParent(), UUID.randomUUID().toString());
		}else{
			workingDir = new File(getTmpDir(), UUID.randomUUID().toString());
		}

		if (workingDir.exists() && !workingDir.isDirectory()) {
			throw new ServiceException("working dir already exists.");
		}
		if(!workingDir.mkdirs()) throw new ServiceException("can not create working dir.");

		final InputStream is = new FileInputStream(zipFile);

		ZipArchiveInputStream archive;
		if (encording == null) {
			archive = new ZipArchiveInputStream(is);
		} else {
			archive = new ZipArchiveInputStream(is, encording, true);
		}

		// 展開したファイル
		List<File> files = new ArrayList<File>();

		ZipArchiveEntry entry;
		while ((entry = archive.getNextZipEntry()) != null) {
			File file = new File(workingDir.getAbsolutePath(), entry.getName());
			if (entry.isDirectory()) {
				if(!file.mkdirs()) throw new ServiceException("cannot extract directory: "+entry.getName());
			} else {
				if (!file.getParentFile().exists()) {
					if(!file.getParentFile().mkdirs()) throw new ServiceException("cannot extract file, failed mkdirs: "+entry.getName());
				}
				OutputStream out = null;
				try {
					out = new FileOutputStream(file);
					IOUtils.copy(archive, out);
				} finally {
					if(out!=null) out.close();
				}
			}
			files.add(file);
		}

		archive.close();

		return files;
	}

	/**
	 * ZIPファイルを展開して、Fileリストで返します.
	 * @param zipFile
	 * @param encording
	 * @return
	 * @throws IOException
	 */
	public static List<File> unzip(File zipFile, String encording, String unzipDirName, boolean unzipSameDir) throws IOException {

		File workingDir;
		if(unzipSameDir){
			workingDir = new File(zipFile.getParent(), unzipDirName);
		}else{
			workingDir = new File(getTmpDir(), unzipDirName);
		}

		if (workingDir.exists() && !workingDir.isDirectory()) {
			throw new ServiceException("working dir already exists.");
		}
		if(!workingDir.mkdirs()) throw new ServiceException("can not create working dir.");

		final InputStream is = new FileInputStream(zipFile);

		ZipArchiveInputStream archive;
		if (encording == null) {
			archive = new ZipArchiveInputStream(is);
		} else {
			archive = new ZipArchiveInputStream(is, encording, true);
		}

		// 展開したファイル
		List<File> files = new ArrayList<File>();

		ZipArchiveEntry entry;
		while ((entry = archive.getNextZipEntry()) != null) {
			File file = new File(workingDir.getAbsolutePath(), entry.getName());
			if (entry.isDirectory()) {
				if(!file.mkdirs()) throw new ServiceException("cannot extract directory: "+entry.getName());
			} else {
				if (!file.getParentFile().exists()) {
					if(!file.getParentFile().mkdirs()) throw new ServiceException("cannot extract file, failed mkdirs: "+entry.getName());
				}
				OutputStream out = null;
				try {
					out = new FileOutputStream(file);
					IOUtils.copy(archive, out);
				} finally {
					if(out!=null) out.close();
				}
			}
			files.add(file);
		}

		archive.close();

		return files;
	}


	/**
	 * ディレクトリの削除処理
	 * @param target 削除するディレクトリ
	 * @return boolean
	 */
	public static boolean dirDelete(File target) {
		boolean result = true;

		if (target.exists()) {
			if (target.isFile()) {
				result = target.delete();
			}
			else
			if (target.isDirectory()) {
				File[] files = target.listFiles();
				for(int i = 0; i < files.length; i++){
					dirDelete(files[i]);
				}
				result = target.delete();
			}
		}
		return result;
	}

	/**
	 * ディレクトリの作成処理
	 * @param target 作成するディレクトリ
	 * @return boolean
	 */
	public static boolean dirCreate(String target) {
		File newfile = new File(target);
		return newfile.mkdirs();
	}

	/**
	 * ディレクトリ内ファイルの圧縮処理
	 * @param inDirName 圧縮するディレクトリ
	 * @param outZipFileName 出力ファイル
	 * @return boolean
	 */
	public static boolean zipDirectory(String inDirName, String outZipFileName) throws IOException {
		File zipFile = new File(outZipFileName);
		File inDir = new File(inDirName);
		ZipOutputStream outZipStream = null;
		try {
			outZipStream = new ZipOutputStream(new FileOutputStream(zipFile));
			dirZIP(zipFile, inDir, outZipStream);
		} catch (IOException e) {
			throw new IOException(lang.__("Failed to compress the directory."), e) ;
		} finally {
			if (outZipStream != null) {
				try {
//					outZipStream.closeEntry();
					outZipStream.flush();
					outZipStream.close();
				} catch (IOException e) {
					throw new IOException(lang.__("Failed to compress the directory."), e) ;
				}
			}
		}
		return true;
	}

	/**
	 * ArrayList内ファイルの圧縮処理
	 * @param inFileNameList 圧縮するファイルリスト
	 * @param outZipFileName 出力ファイル
	 * @return boolean
	 */
	public static boolean zipFileList(ArrayList<String> inFileNameList, String outZipFileName) throws IOException {
		ZipOutputStream outZipStream = null;
		File zipFile = new File(outZipFileName);
		try {
			outZipStream = new ZipOutputStream(new FileOutputStream(zipFile));
			for (int i = 0; i < inFileNameList.size(); i++) {
				File file = new File((String)inFileNameList.get(i));
				fileZIP(zipFile, file, file.getName(), "Shift_JIS", outZipStream);
			}
		} catch (IOException e) {
			throw new IOException(lang.__("Failed to compress file."), e) ;
		} finally {
			if (outZipStream != null) {
				try {
					outZipStream.closeEntry();
					outZipStream.flush();
					outZipStream.close();
				} catch (IOException e) {
					throw new IOException(lang.__("Failed to compress file."), e) ;
				}
			}
		}
		return true;
	}

	/**
	 * ディレクトリ圧縮処理
	 * @param zipFile 保存先ファイル
	 * @param file 圧縮ファイル
	 * @param outZipStream
	 */
	private static void dirZIP(File zipFile, File file, ZipOutputStream outZipStream) throws IOException {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					dirZIP(zipFile, f, outZipStream);
				} else {
					if (!f.getAbsoluteFile().equals(zipFile)) {
						fileZIP(zipFile, f, f.getAbsolutePath().replace(zipFile.getParent(), "").substring(1), "Shift_JIS", outZipStream);
					}
				}
			}
		}
	}

	/**
	 * ファイルの圧縮処理
	 * @param zipFile 保存先ファイル
	 * @param file 圧縮ファイル
	 * @parma fileName 保存ファイル
	 * @param encoding 文字コード
	 * @param outStream ZipOutputStream
	 * @return boolean
	 */
	private static boolean fileZIP(File zipFile, File file, String fileName, String encoding, ZipOutputStream outZipStream) throws IOException {
		BufferedInputStream inStream = null;
		//outZipStream.setLevel(5);
		outZipStream.setEncoding(encoding);
		try {
			outZipStream.putNextEntry(new ZipEntry(fileName));
			inStream = new BufferedInputStream(new FileInputStream(file));
			int readSize = 0;
			byte buffer[] = new byte[1024];
			while ((readSize = inStream.read(buffer, 0, buffer.length)) != -1) {
				outZipStream.write(buffer, 0, readSize);
			}
			inStream.close();
			outZipStream.closeEntry();
		} catch (IOException e) {
			throw new IOException(lang.__("Failed to compress file."), e) ;
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					throw new IOException(lang.__("Failed to compress file."), e) ;
				}
			}
		}
		return true;
	}

	/**
	 * ファイルの解凍処理
	 * @param zipFileName 解凍するZIPファイル名
	 * @param unzipDir 解凍先ディレクトリ
	 * @return boolean
	 */
	public static boolean unZIP(String zipFileName, String unzipDir) throws IOException {
		File inFile = new File(zipFileName);
		File inDir = new File(inFile.getParent(), inFile.getName().substring(0, inFile.getName().lastIndexOf(".")));
		if (!inDir.exists()) {
			return false;
		}
		ZipFile zipFile = null;
		BufferedInputStream inStream = null;
		BufferedOutputStream outStream = null;
		try {
			// ZIPファイル内のファイル展開
			zipFile = new ZipFile(zipFileName);
			Enumeration<? extends ZipEntry> enumZip = zipFile.getEntries();
			while (enumZip.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)enumZip.nextElement();
				File unzipFile = new File(unzipDir);
				File outFile = new File(unzipFile.getAbsolutePath() + "/" + inDir.getName(), zipEntry.getName());
				if (zipEntry.isDirectory()) {
					// ディレクトリ作成
					try {
						outFile.mkdir();
					} catch (Exception e) {
						throw new IOException(lang.__("Failed to decompress file."), e) ;
					}
				}
				else {
					inStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
					if (!outFile.getParentFile().exists()) {
						// ディレクトリ作成
						try {
							outFile.getParentFile().mkdirs();
						} catch (Exception e) {
							throw new IOException(lang.__("Failed to decompress file."), e) ;
						}
					}
					// 解凍ファイル出力
					outStream = new BufferedOutputStream(new FileOutputStream(outFile));
					byte[] buffer = new byte[1024];
					int readSize = 0;
					while ((readSize = inStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, readSize);
					}
					outStream.close();
					inStream.close();
				}
			}
		} catch(Exception e) {
			throw new IOException(lang.__("Failed to decompress file."), e) ;
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					throw new IOException(lang.__("Failed to decompress file."), e) ;
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				}  catch (IOException e) {
					throw new IOException(lang.__("Failed to decompress file."), e) ;
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					throw new IOException(lang.__("Failed to decompress file."), e) ;
				}
			}
		}
		return true;
	}


	/**
     * フォルダを作る
     *
     * @param folderPath
     */
    public void creatFolder(String folderPath) {
        try {
            File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            System.out.println("create folder failed!");
            e.printStackTrace();
        }
    }
    /**
     * ファイルを作る
     *
     * @param filePath
     * @param fileContext
     */
    public void creatFile(String filePath, String fileContext) {
        try {
            if (filePath != null && !"".equals(filePath)) {
                String folder = filePath
                        .substring(0, filePath.lastIndexOf("/"));
                creatFolder(folder);
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (fileContext != null && !"".equals(fileContext)) {
                FileWriter fileWriter = new FileWriter(file);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(fileContext);
                fileWriter.close();
                printWriter.close();
            }
        } catch (Exception e) {
            System.out.println("create file failed!");
            e.printStackTrace();
        }
    }
    /**
     * ファイルを削除
     *
     * @param filePath
     */
    public Boolean deleteFile(String filePath) {
        File file = new File(filePath);
        Boolean flag = false;
        if (file.isFile()) {
            flag = file.delete();
        }
        return flag;
    }
    /**
     * フォルダを削除
     *
     * @param folderPath
     * @return
     */
    public Boolean deleteNullFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.isDirectory() || !file.exists()) {
            return false;
        }
        File[] fileList = file.listFiles();
        if (fileList.length > 0) {
            return false;
        }
        file.delete();
        return true;
    }
    /**
     * フォルダを削除
     *
     * @param folderPath
     * @return
     */
    public Boolean deleteAllFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.isDirectory() || !file.exists()) {
            return false;
        }
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                File fileSon = fileList[i];
                if (!deleteNullFolder(fileSon.toString())) {
                    deleteAllFolder(fileSon.toString());
                    deleteAllfiles(fileSon.toString());
                    fileSon.delete();
                } else {
                    fileSon.delete();
                }
            }
        }
        return true;
    }
    /**
     * 指定ディレクトリ下の全アイル削除
     *
     * @param filePath
     * @return
     */
    public Boolean deleteAllfiles(String filePath) {
        File file = new File(filePath);
        if (!file.isDirectory() || !file.exists()) {
            return false;
        }
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (!fileList[i].isDirectory()) {
                File fileSon = fileList[i];
                if (fileSon.isFile()) {
                    deleteFile(fileSon.toString());
                }
            }
        }
        return true;
    }
    /**
     * フォルダを削除
     *
     * @param filePath
     */
    public void deleteAll(String filePath) {
        deleteAllfiles(filePath);
        deleteAllFolder(filePath);
    }
    /**
     * ファイルを複写
     *
     * @param oldPass
     * @param newPath
     * @return
     */
    public Boolean copyFile(String oldPath, String newPath) {
        try {
            File file = new File(oldPath);
            if (file.isDirectory()) {
                return false;
            }
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                FileOutputStream outputStream = new FileOutputStream(newPath);
                BufferedInputStream reader = new BufferedInputStream(
                        inputStream);
                int len = inputStream.available();
                byte[] buffer = new byte[len];
                reader.read(buffer, 0, len);
                outputStream.write(buffer);
                inputStream.close();
                outputStream.close();
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("copy file failed!");
            e.printStackTrace();
        }
        return true;
    }
    /**
     * ファイルを複写
     *
     * @param oldPath
     * @param newPath
     */
    @SuppressWarnings("static-access")
    public Boolean copyAllFile(String oldPath, String newPath) {
        File file = new File(oldPath);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            copyFile(oldPath, newPath);
            return false;
        }
        File[] fileList = file.listFiles();
        File mkdir = new File(newPath);
        if (!mkdir.exists()) {
            mkdir.mkdirs();
        }
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                copyAllFile(oldPath + fileList[i].separator
                        + fileList[i].getName(), newPath
                        + fileList[i].separator + fileList[i].getName());
            } else {
                copyFile(oldPath + fileList[i].separator
                        + fileList[i].getName(), newPath
                        + fileList[i].separator + fileList[i].getName());
            }
        }
        return true;
    }


    /**
     * ディレクトリ単位でのファイルコピー
     * @param dirFrom
     * @param dirTo
     * @return
     */
    public static Boolean directoryCopy(File dirFrom, File dirTo){
       File[] fromFile = dirFrom.listFiles();
       dirTo = new File(dirTo.getPath() +  File.separator + dirFrom.getName());
       dirTo.mkdir();

       if(fromFile != null) {
          for(File f : fromFile) {
             if (f.isFile()){
                if(!fileCopy(f, dirTo)){
                   return false;
                }
             }else{
                if(!directoryCopy(f, dirTo)){
                   return false;
                }
             }
          }
        }
       return true;
    }
    private static Boolean fileCopy(File file, File dir){
       File copyFile = new File(dir.getPath() + File.separator + file.getName());
       FileChannel channelFrom = null;
       FileChannel channelTo = null;

       try{
          copyFile.createNewFile();
          channelFrom = new FileInputStream(file).getChannel();
          channelTo = new FileOutputStream(copyFile).getChannel();

          channelFrom.transferTo(0, channelFrom.size(), channelTo);

          return true;
       }catch(IOException e){
          return false;
       }finally{
          try{
             if (channelFrom != null) { channelFrom.close(); }
             if (channelTo != null) { channelTo.close(); }

             //更新日付もコピー
             copyFile.setLastModified(file.lastModified());
          }catch (IOException e){
             return false;
          }
       }
    }

    /**
     * パスを指定してのファイルのコピー
     *
     * @param oldPass
     * @param newPath
     * @return
     */
    public static Boolean fileCopyByPath(String oldPath, String newPath) {
        try {
            File file = new File(oldPath);
            if (file.isDirectory()) {
                return false;
            }
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                FileOutputStream outputStream = new FileOutputStream(newPath);
                BufferedInputStream reader = new BufferedInputStream(
                        inputStream);
                int len = inputStream.available();
                byte[] buffer = new byte[len];
                reader.read(buffer, 0, len);
                outputStream.write(buffer);
                inputStream.close();
                outputStream.close();
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("copy file failed!");
            e.printStackTrace();
        }
        return true;
    }


    /**
     * TempのPathを削除
     * @param strPath パース
     * @param iDayBeforeSec 周期
     */
    public static void delTempPathFile(String strPath, int iDayBeforeSec){
        File file = new File(strPath);
        if(file.exists() == false) {
        	return;
        }
        File[] fileList = file.listFiles();
        for(int i = 0; i < fileList.length; i++) {
        	long l = fileList[i].lastModified();
        	java.util.Date dateC = new java.util.Date(l);

        	Calendar cal = new GregorianCalendar();
        	java.util.Date dateT = cal.getTime();

            long diff = dateT.getTime() - dateC.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
        	if(days > iDayBeforeSec) {
        		fileList[i].delete();
        	}
        }
    }

    /**
     * ファイルリストを取得する
     * @param file
     * @param resultFileList
     * @return
     */
    private static List<String> getFileList(File file,List<String> resultFileList){
        File[] files = file.listFiles();
        if(files==null)return resultFileList;
        for (File f : files) {
            if(f.isDirectory()){
                resultFileList.add(f.getPath());
                getFileList(f,resultFileList);
            }else
                resultFileList.add(f.getPath());
        }
        return resultFileList;
    }
    /**
     * TempのPathを削除
     * @param strPath パース
     * @param iDayBeforeSec 周期
     */
    public static void delTempPathFileM(String strPath, int iDayBeforeSec){
        File file = new File(strPath);
        if(file.exists() == false) {
        	return;
        }
        List<String> resultFileList = new ArrayList<String>();
        resultFileList = getFileList(file, resultFileList);

        XmlFileEditHelper.outputLog("XmlEditorAction resultFileList size=" + resultFileList.size());

        for(int i = 0; i < resultFileList.size(); i++) {
        	File fileCur = new File(resultFileList.get(i));
        	long l = fileCur.lastModified();
        	java.util.Date dateC = new java.util.Date(l);

        	Calendar cal = new GregorianCalendar();
        	java.util.Date dateT = cal.getTime();

            long diff = dateT.getTime() - dateC.getTime();
            //long days = diff / (1000 * 60 * 60 * 24);
            long sec = diff / (1000);

        	if(sec > iDayBeforeSec
        		&& (fileCur.getName().endsWith(".tmp") ||
        				fileCur.getName().endsWith(".new") ||
        				fileCur.getName().endsWith(".in")
        				)
        				) {
        		fileCur.delete();
        	}
        }
    }

    /**
     * 引数で与えられたファイルのMD5ハッシュ値を返す
     * @param targetFile
     * @return
     */
    public static String getMd5hash(File targetFile){
    	String retValue = "";
    	try{
    		InputStream in = new FileInputStream(targetFile);
    		MessageDigest digest = MessageDigest.getInstance("MD5");
    		try{
    			byte[] buff = new byte[1024];
    			int len = 0;
    			while((len=in.read(buff,0,buff.length)) >= 0){
    				digest.update(buff,0,len);
    			}
    		}catch(IOException e){
    			throw e;
    		}finally{
    			if (in != null){
    				try{
    					in.close();
    				}catch(IOException e){
    					throw e;
    				}
    			}
    		}
    		StringBuilder sb = new StringBuilder();
    		for(byte b : digest.digest()){
    			sb.append(String.format("%02x",b));
    		}
    		retValue = sb.toString();
    	}catch(Exception e){
    		retValue = null;
    	}

  	   return retValue;
    }

	/**
	 * ファイル名をファイル名と拡張子に分割して返す
	 * @param fileName ファイル名
	 * @return ファイル名と拡張子のString配列
	 */
    public static String [] getSeparatedFilename(String fileName){
    	String [] retValue = new String[2];
    	retValue[0] = "";
    	retValue[1] = "";

    	if(fileName == null){
	        return null;
    	}

   	    int point = fileName.lastIndexOf(".");
   	    if (point != -1) {
   	    	retValue[0] =  fileName.substring(0, point);
   	    	retValue[1] =  fileName.substring(point + 1);
   	    }else{
   	    	retValue[0] =  fileName;
   	    }

   	    return retValue;
    }

    /**
     * @param size
     * @return Byte/KByte/MB
     */
    public static String getSizeStr(long size) {
        if (1024 > size) {
          return size + " Byte";
        } else if (1024 * 1024 > size) {
          double dsize = size;
          dsize = dsize / 1024;
          BigDecimal bi = new BigDecimal(String.valueOf(dsize));
          double value = bi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
          return value + " KByte";
        } else {
          double dsize = size;
          dsize = dsize / 1024 / 1024;
          BigDecimal bi = new BigDecimal(String.valueOf(dsize));
          double value = bi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
          return value + " MB";
        }
      }
}
