/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page.map;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.HttpUtil;

/**
 * メモレイヤのアクションクラスです.
 * spring checked take 5/14
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class RakugakiAction extends AbstractAction  {

	public String kml;

	// Service
	@Resource protected FileService fileService;
	@Resource protected TrackDataService trackDataService;

	private static String rakugaki_dir = "rakugaki";
	private static String rakugaki_kml_file_name = "doc.kml";

	/**
	 * メモKMLを保存します.
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/rakugaki/save")
	@ResponseBody
	public String save() {
		kml = request.getParameter("kml");

		if(StringUtil.isEmpty(kml)) {
			throw new ServiceException(lang.__("KML has not been specified."));
		}

		// KMLファイルを取得
		long trackmapinfoid = 0;
		if(0<loginDataDto.getTrackdataid()) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			trackmapinfoid = trackData.trackmapinfoid;
		}
		File kmlFile = getKMLFile(loginDataDto.getLocalgovinfoid(), trackmapinfoid);

		// ファイルがなければ作成
		if(kmlFile.exists()==false) {
			fileService.createFile(kmlFile);
		}

		// ファイルを上書き
		FileUtil.write(kmlFile.getAbsolutePath(), kml.getBytes());

		// 結果の出力
		try {
			JSONObject ret = new JSONObject();
			ret.put("success", true);
			//response.setContentType("application/json");
			//response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( ret.toString() );
			return ret.toString();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * KMLファイルのダウンロード
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/rakugaki/download")
	@ResponseBody
	public String download(HttpServletResponse res) {
		Long localgovinfoid = null;
		if(localgovinfoid==null) localgovinfoid = loginDataDto.getLocalgovinfoid();
		long trackmapinfoid = 0;
		if(0<loginDataDto.getTrackdataid()) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			trackmapinfoid = trackData.trackmapinfoid;
		}

		File kmlFile = getKMLFile(localgovinfoid, trackmapinfoid);
		if(kmlFile.exists()) {
			//InputStream in = null;
			//try {
				//in = new BufferedInputStream(new FileInputStream(kmlFile), 1024*8);
				//ResponseUtil.download(kmlFile.getName(), in, (int) kmlFile.length());
				HttpUtil.download(res, kmlFile);
			//} catch (FileNotFoundException e) {
			//	logger.error(lang.__("Unable to download memo KML file \"{0}\".", fileService.getFileRealRelativePath(kmlFile)), e);
			//} finally {
			//	if(in!=null) {
			//		try {
			//			in.close();
			//		} catch (IOException e) {
			//			logger.fatal(e);
			//		}
			//	}
			//}
		}

		return null;
	}

	/**
	 * KMLファイルの編集ロック
	 * 複数ユーザが同時に編集しないように、排他ロックします.
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/rakugaki/lock")
	@ResponseBody
	public String lock() {

		JSONObject result = lock(true);

		// 出力
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		//ResponseUtil.write(result.toString());
		return result.toString();

		//return null;
	}

	/**
	 * KMLファイルの編集ロック解除
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/rakugaki/unlock")
	@ResponseBody
	public String unlock() {

		JSONObject result = lock(false);

		// 出力
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		//ResponseUtil.write(result.toString());
		return result.toString();

		//return null;
	}

	/**
	 * メモ編集ロック
	 * @param lockMode true: ロックする, false: ロック解除
	 * @return ロック結果
	 */
	protected JSONObject lock(boolean lockMode) {

		// ロックユーザ
		long trackdataid = loginDataDto.getTrackdataid();
		String sessionId = session.getId();
		Date now = new Date();

		// ロック有効秒数
		int EXPIRE_SEC = 30;
		Date expireDate = new Date(now.getTime()+(EXPIRE_SEC*1000));
		// ServletContext の Attribute に保存に使用するキー
		String key = "Rakugaki.lock.trackdataid"+trackdataid;
		// ロックしたタイムスタンプの保存形式
		final String DATE_PATTERN ="yyyy/MM/dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

		try {
			// ロックフラグ
			boolean lock = true;
			// ロック情報
			JSONObject lockInfo = null;
			synchronized(application) {
				// ロックされているかチェック
				lockInfo = (JSONObject) application.getAttribute(key);
				if(lockInfo!=null) {
					// 有効期限を取得
					try {
						Date timestamp = dateFormat.parse(lockInfo.getString("timestamp"));
						expireDate = new Date(timestamp.getTime()+(EXPIRE_SEC*1000));
					} catch (ParseException e) {
						throw new ServiceException(lang.__("Unable to read memo editing lock time."), e);
					}

					// 他の人がロックしているかチェック
					String lockedSessionId = (String) lockInfo.get("sessionid");
					if(sessionId.equals(lockedSessionId)==false) {
						// 有効期限がまだ過ぎていないかチェック
						if(now.before(expireDate)) {
							// ロック失敗
							lock=false;
						}
					}
				}

				// ロック可能ならば
				if(lock) {
					// ロックする
					if(lockMode) {
						// ロック情報を作成
						lockInfo = new JSONObject();
						lockInfo.put("trackdataid", trackdataid);
						lockInfo.put("sessionid", sessionId);
						lockInfo.put("timestamp", dateFormat.format(now));
						// ロックしている班情報
						if(loginDataDto.getGroupInfo()!=null) {
							GroupInfo info = loginDataDto.getGroupInfo();
							JSONObject infoJSON = new JSONObject();
							infoJSON.put("id", info.id);
							infoJSON.put("name", info.name);
							lockInfo.put("groupInfo", infoJSON);
						}
						// ロックしている課情報
						if(loginDataDto.getUnitInfo()!=null) {
							UnitInfo info = loginDataDto.getUnitInfo();
							JSONObject infoJSON = new JSONObject();
							infoJSON.put("id", info.id);
							infoJSON.put("name", info.name);
							lockInfo.put("unitInfo", infoJSON);
						}

						// ServletContext に保存
						application.setAttribute(key, lockInfo);
					}
					// ロック解除
					else {
						// ServletContext をクリア
						application.removeAttribute(key);
						lock = false;
						lockInfo = null;
						expireDate = null;
					}
				}
			}

			// 結果情報
			JSONObject result = new JSONObject();
			result.put("lock", lock);
			result.put("timestamp", dateFormat.format(now));
			if(expireDate!=null) {
				result.put("expireDate", dateFormat.format(expireDate));
			}
			// ロック情報
			if(lockInfo!=null) {
				// セッション情報はハイジャックされる可能性があるため取り除く
				JSONObject clone = new JSONObject(lockInfo.toString());
				clone.remove("sessionid");
				result.put("lockInfo", clone);
			}

			return result;

		} catch(JSONException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * メモKMLファイルを取得します.
	 * 複数同時災害の場合でも同じメモになるようにtrackmapinfoidで管理する。
	 * (登録情報レイヤなどと同じ仕様)
	 * @param localgovinfoid 自治体ID
	 * @param trackmapinfoid 災害地図ID(平常時の場合は 0)
	 */
	public File getKMLFile(long localgovinfoid, long trackmapinfoid) {
		return fileService.getFileOnLocalgovDataDir(loginDataDto.getLocalgovinfoid(), rakugaki_dir+"/"+trackmapinfoid+"/"+rakugaki_kml_file_name);
	}
}
