/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.ecommap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.annotation.Resource;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.file.ContentsFile;
import jp.ecom_plat.map.util.FileUploadUtil;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.form.FileForm;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONArray;
import org.seasar.framework.util.StringUtil;

/**
 * ContentsUploadServletに相当するサービスクラスです.
 * とりあえず登録情報のファイルアップロード処理部分のみ実装。（eコミのように汎用的ではない）
 */
@org.springframework.stereotype.Service
public class FileUploadService {

	@Resource protected LoginDataDto loginDataDto;

	Logger logger = Logger.getLogger(FileUploadService.class);
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/**
	 * eコミマップのサーバのルートURLを返す
	 * @return "http://example.com"のようなパス（コンテキストパスは含めない）
	 */
	public String getEcommapServerRootUrl() {
		return Config.getEcommapURL().replaceFirst("/$", ""); // 最後のスラッシュがあればはずす
	}

	/**
	 * eコミマップのコンテキストパスを返す
	 * @return "http://example.com/map"のようなパス
	 */
	public String getEcommapContextPath() {
		return getEcommapServerRootUrl() + "/map";
	}

	/**
	 * eコミマップのホームディレクトリの絶対パスを付けて返す
	 * @param path
	 * @return "/home/map{path}"のようにして返す
	 */
	public String getEcommapRealPath(String path) {
		String mapDir = Config.getMapDir().getAbsolutePath();
		if(StringUtil.isEmpty(mapDir)) {
			logger.error("MAPDIR not found on SaigaiTask.properties");
			throw new ServiceException(lang.__("Unable to get e-Com home directory."));
		}
		return mapDir+path;
	}

	/**
	 * 危機管理クラウドにログインしているアカウントが持つeコミユーザ情報を取得します.
	 * @return
	 */
	public UserInfo getLoginUserInfo() {
		// eコミユーザIDをセッションから取得
		String authId = loginDataDto.getEcomUser();
		// ログインユーザ情報を取得
		UserInfo userInfo = MapDB.getMapDB().getAuthIdUserInfo(authId);
		if (userInfo == null) {
			userInfo = UserInfo.createGuestUser();
		}
		return userInfo;
	}

	/**
	 * ファイルを "ユーザID/レイヤID" のディレクトリの下にアップロードします.
	 * ContentsUploadServlet.doPost の移植.
	 * @return fileListのJSONArray [[fileUrl, fileTitle], ...]
	 */
	public JSONArray uploadContents(String layerId, List<FileForm> fileForms) {
		JSONArray fileList = new JSONArray();

		HashMap<String, String> pathInfo;
		//int maxUploadSize = 10000;
		//String uploadEncoding;
		HashSet<String> allowedExtent;
		{
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

		//DBLang lang = new DBLang("ja"); // とりあえず決めうち

		// ログインユーザ情報を取得
		UserInfo userInfo = getLoginUserInfo();

		try {
			// アップロードファイルを格納するPATHを取得
			// コンテンツの場合はユーザID+レイヤIDの下
			String uploadPath = ContentsFile.getContentsDirUrl(userInfo.userId, layerId);
			Vector<String> uploadedFileNames = uploadFiles(fileForms, getEcommapRealPath(uploadPath), allowedExtent);

			if (uploadedFileNames == null) {
				throw new ServiceException("Invalid File Extent.");
			}

			//<div lang="ja">icons.txt書き換え</div>
			//<div lang="en">Rewrite icons.txt</div>
			String iconsPath = getEcommapRealPath(uploadPath);
			for(String fileName : uploadedFileNames){
				writeIconsTxt(iconsPath, fileName, "", "");
			}

			for(int idx=0; idx<fileForms.size(); idx++) {
				String uploadedFileName = uploadedFileNames.get(idx);
				FileForm fileForm = fileForms.get(idx);

				// URLを設定
				fileForm.url = getEcommapServerRootUrl() + ContentsFile.getFileUrl("/map", uploadPath, uploadedFileName);

				String fileUrl = fileForm.url;
				String fileTitle = fileForm.title;
				// タイトルが空だったらファイル名をいれておく
				if(StringUtil.isEmpty(fileTitle)) fileTitle = uploadedFileName;
				fileList.put(new JSONArray().put(fileUrl).put(fileTitle));
			}

			return fileList;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * FileUploadUtil.uploadFiles の移植
	 */
	public String uploadFile(MultipartFile file, String path, HashSet<String> allowedExtent) {
		if(0<file.getSize()) {
			//<div lang="ja">pathが無ければ作成</div>
			//<div lang="en">If there is no path, create it</div>
			File pathFile = new File(path);
			if (!pathFile.exists()) {
				if(!pathFile.mkdirs()) throw new ServiceException(lang.__("Unable to create destination directory."));
			}

			// <div lang="ja">PATH名を除くファイル名のみを取得</div>
			//<div lang="en">Get filename without PATH</div>
			String fileName = file.getOriginalFilename();

			// 拡張子をチェック
			FileUtil.assertAllowedExtent(fileName);

			// 出力先を取得する（重複していればユニークにする）
			String fileBaseName = FileUtil.getFileBaseName(fileName);
			String ext = FileUtil.getFileExt(fileName);
			String uniqFilePath = path;
			if(uniqFilePath.endsWith("/")==false) uniqFilePath+="/";
			File outFile = FileUploadUtil.getUniqFile(uniqFilePath, fileBaseName, ext);

			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
				try {
					out.write(file.getBytes(), 0, (int)file.getSize());
					logger.info("Uploaded File To: "+outFile.getAbsolutePath());
					return outFile.getName();
				} finally {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Vector<String> uploadFiles(List<FileForm> fileForms, String path, HashSet<String> allowedExtent) {

		Vector<String> vecFileNames = new Vector<String>();

		for(FileForm fileForm : fileForms) {
			vecFileNames.add(uploadFile(fileForm.formFile, path, allowedExtent));
		}

		return vecFileNames;
	}

	/**
	 * <div lang="ja">
	 * icons.txt書き換え 。
	 * </div>
	 *
	 * <div lang="en">
	 * Rewrite icons.txt.
	 * </div>
	 * */
	private void writeIconsTxt(String filePath, String iconName, String category, String _abs)
	{
		try{
			File iconListFile = new File(filePath,"icons.txt");
			//<div lang="ja">tmpファイルに書き換え</div>
			//<div lang="en">Rewrite tmp file</div>
			File tmpFile = File.createTempFile("icons_upload", ".tmp");
			boolean f = false;
			BufferedReader br = null;
			PrintWriter pw = null;
			try{
				if (iconListFile.exists()) {
					//<div lang="ja">icons.txtで指定されたファイルのみ表示</div>
					//<div lang="en">Display only the file specified in icons.txt</div>
					br = new BufferedReader(new FileReader(iconListFile));
					pw = new PrintWriter(new FileWriter(tmpFile.getPath()));
					String categoryName = "";
					String line;
					int count = 0;
					while ((line = br.readLine()) != null) {
						String iconFilename = line.trim();
						if (iconFilename.length() > 0) {
							//<div lang="ja">カテゴリ指定ない場合、最初に追加</div>
							//<div lang="en">If the category is not specified, insert to the beginning</div>
							if(!f&&category.equals("")){
								if(_abs.equals("")){
									pw.println(iconName);
								} else {
									pw.print(iconName);
									pw.print("\t");
									pw.println(_abs);
								}
								f = true;
							}
							//<div lang="ja">カテゴリ名取得</div>
							//<div lang="en">Get category name</div>
							if (!f&&iconFilename.startsWith("#")) {
								categoryName = iconFilename.substring(1);
								if(categoryName.equals(category)){
									//<div lang="ja">カテゴリ記述</div>
									//<div lang="en">Print the category</div>
									pw.println(line);
									//<div lang="ja">アイコン記述</div>
									//<div lang="en">Print the icon</div>
									if(_abs.equals("")){
										pw.println(iconName);
									} else {
										pw.print(iconName);
										pw.print("\t");
										pw.println(_abs);
									}
									f = true;
									count++;
									continue;
								}
							}
							//<div lang="ja">同名のアイコンは記述しない</div>
							//<div lang="en">Do not print the same icon name</div>
							else if(iconFilename.startsWith(iconName)){
								if(iconFilename.indexOf("\t")!=-1){
									String[] split = iconFilename.split("\t");
									String name = split[0];
									if(name.equals(iconName)){count++; continue; }
								} else {
									if(iconFilename.equals(iconName)){count++; continue; }
								}
							}
						}
						pw.println(line);
						count++;
					}//while
					if(count==0){
						if(_abs.equals("")){
							pw.println(iconName);
						} else {
							pw.print(iconName);
							pw.print("\t");
							pw.println(_abs);
						}
					}
				}
			} finally {
				if(br!=null) {
					try {
						br.close();
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				if(pw!=null) {
					try {
						pw.close();
					} catch(Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			FileUtils.copyFile(tmpFile, iconListFile);
			if(tmpFile.exists()) {
				if(!tmpFile.delete()) throw new ServiceException(lang.__("Failed to delete tmp file."));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 登録情報のファイル情報を１件追加します.
	 * @param fileUrl
	 * @param fileTitle
	 * @throws Exception 
	 */
	public int insertFeatureFileList(long mid, LayerInfo layerInfo, long fid, JSONArray fileList, Date[] timeParam) throws Exception {
		String layerId = layerInfo.layerId;
		// ログインユーザ情報を取得
		UserInfo userInfo = getLoginUserInfo();
		int userId = userInfo.userId;
		MapDB mapDB = MapDB.getMapDB();
		int count = 0;
		if(fileList!=null && 0<fileList.length()) {
			for(int index=0; index<fileList.length(); index++) {
				JSONArray values = fileList.getJSONArray(index);
				String fileUrl = values.getString(0);
				String fileTitle = values.getString(1);
				String serverRootUrl = getEcommapServerRootUrl();
				// 履歴レイヤの場合は追加モードになる mapDB.updateFeatureFileListを利用して追加
				if(layerInfo.timeSeriesType==TimeSeriesType.HISTORY) {
					long _orgid = fid;
					mapDB.updateFeatureFileList(layerInfo, _orgid, mid, userId, fileList, serverRootUrl, timeParam);
				}
				else mapDB.insertFeatureFile(layerId, fid, mid, userId, fileUrl, fileTitle, serverRootUrl);
			}
		}
		return count;
	}

	/**
	 * 登録情報のファイル情報をすべて消して指定したものだけに更新します.
	 * @param mid
	 * @param layerInfo
	 * @param featureId
	 * @param fileList
	 * @param timeParam 
	 * @return 更新数
	 */
	public int updateFeatureFileList(long mid, LayerInfo layerInfo, long featureId, JSONArray fileList, Date[] timeParam) {
		String serverRootUrl = getEcommapServerRootUrl();
		if(fileList!=null) {
			try {
				MapDB mapDB = MapDB.getMapDB();
				// ログインユーザ情報を取得
				UserInfo userInfo = getLoginUserInfo();
				int userId = userInfo.userId;
				// 履歴レイヤの場合は追加モードになるので、全削除を呼び出す
				if(layerInfo.timeSeriesType==TimeSeriesType.HISTORY) {
					String layerId = layerInfo.layerId;
					long _orgid = featureId;
					long fid = ExMapDB.getHistoryFeatureId(layerId, _orgid, timeParam);
					mapDB.deleteFeatureFileList(layerId, fid, mid, userId);
				}
				return mapDB.updateFeatureFileList(layerInfo, featureId, mid, userId, fileList, serverRootUrl, timeParam);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return 0;
	}
}
